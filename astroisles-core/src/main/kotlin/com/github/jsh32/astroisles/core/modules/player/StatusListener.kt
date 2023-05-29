package com.github.jsh32.astroisles.core.modules.player

import PlayerServiceGrpcKt
import com.github.jsh32.astroisles.common.redis.ChannelListener
import com.github.jsh32.astroisles.common.redis.RedisMessages
import com.github.jsh32.astroisles.core.AstroIslesCore
import com.google.protobuf.Timestamp
import getPlayerRequest
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Duration
import java.time.Instant
import java.util.*


/**
 * Subscriber to join event
 */
class StatusListener(
    private val plugin: AstroIslesCore,
    private val config: PlayerConfig,
    private val playerService: PlayerServiceGrpcKt.PlayerServiceCoroutineStub,
) : Listener, ChannelListener(
    RedisMessages.PlayerJoin::class,
    RedisMessages.PlayerQuit::class
) {
    init {
        handle<RedisMessages.PlayerJoin> { playerJoin(it) }
        handle<RedisMessages.PlayerQuit> { playerQuit(it) }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage(null)
    }

    private fun playerQuit(message: RedisMessages.PlayerQuit) {
        runBlocking {
            val playerData = playerService.getPlayerData(getPlayerRequest { this.uuid = message.playerId })

            plugin.audience.players().sendMessage(MiniMessage.miniMessage().deserialize(config.onLeave,
                Placeholder.component("player", MiniMessage.miniMessage().deserialize(playerData.displayName))))
        }
    }

    private fun playerJoin(message: RedisMessages.PlayerJoin) {
        runBlocking {
            val playerData = playerService.getPlayerData(getPlayerRequest { this.uuid = message.playerId })

            // Send a welcome message to everyone
            val joinMessage = MiniMessage.miniMessage().deserialize(config.onJoin,
                Placeholder.component("player", MiniMessage.miniMessage().deserialize(playerData.displayName)))

            val player = plugin.server.getPlayer(UUID.fromString(message.playerId))
            if (player != null && message.initialJoin) {
                if (message.firstJoin) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(config.firstJoin,
                        Placeholder.component("player", player.displayName())))
                } else {
                    player.sendMessage(
                        MiniMessage.miniMessage().deserialize(config.welcome,
                            Placeholder.component("player", player.displayName()),
                            Placeholder.component("time_since_last_quit", Component.text(getHumanReadableDurationSince(playerData.lastLogout)))))
                }

                plugin.server.onlinePlayers
                    .filter { it.uniqueId != player.uniqueId }
                    .map { it.sendMessage(joinMessage) }
            } else {
                plugin.audience.players().sendMessage(joinMessage)
            }
        }
    }
}

private fun getHumanReadableDurationSince(timestamp: Timestamp): String {
    val now = Instant.now()
    val pastTimestamp = Instant.ofEpochSecond(timestamp.seconds)
    val duration = Duration.between(pastTimestamp, now)
    val months: Long = duration.toDays() / 30
    val days: Long = duration.toDays() % 30
    val hours: Long = duration.toHours() % 24
    val minutes: Long = duration.toMinutes() % 60
    return if (months > 0) {
        "$months months"
    } else if (days > 0) {
        "$days days"
    } else if (hours > 0) {
        "$hours hours"
    } else {
        "$minutes minutes"
    }
}