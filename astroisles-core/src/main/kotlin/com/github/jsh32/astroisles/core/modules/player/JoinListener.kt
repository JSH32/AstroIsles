package com.github.jsh32.astroisles.core.modules.player

import PlayerServiceGrpcKt
import com.github.jsh32.astroisles.common.redis.ChannelListener
import com.github.jsh32.astroisles.common.redis.RedisMessages
import com.google.protobuf.Timestamp
import getPlayerRequest
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.time.Instant
import java.util.*


/**
 * Subscriber to join event
 */
class JoinListener(
    private val plugin: JavaPlugin,
    private val config: PlayerConfig,
    private val playerService: PlayerServiceGrpcKt.PlayerServiceCoroutineStub
) : ChannelListener(RedisMessages.PlayerJoin::class) {
    init {
        handle<RedisMessages.PlayerJoin> { playerHandle(it) }
    }

    private fun playerHandle(message: RedisMessages.PlayerJoin) {
        val player = plugin.server.getPlayer(UUID.fromString(message.playerId))
        if (player != null && message.initialJoin) {
            if (message.firstJoin) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(config.firstJoin,
                    Placeholder.component("player", player.displayName())))
            } else {
                runBlocking {
                    val playerData = playerService.getPlayerData(getPlayerRequest { this.uuid = player.uniqueId.toString() })

                    player.sendMessage(
                        MiniMessage.miniMessage().deserialize(config.welcome,
                            Placeholder.component("player", player.displayName()),
                            Placeholder.component("time_since_last_quit", Component.text(getHumanReadableDurationSince(playerData.lastLogout)))))
                }
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