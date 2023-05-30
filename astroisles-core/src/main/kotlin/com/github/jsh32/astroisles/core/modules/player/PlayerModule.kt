package com.github.jsh32.astroisles.core.modules.player

import PlayerServiceGrpcKt
import com.github.jsh32.astroisles.common.module.MinecraftModule
import com.github.jsh32.astroisles.common.redis.JedisListener
import com.github.jsh32.astroisles.core.AstroIslesCore
import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.name.Named
import getPlayerRequest
import io.grpc.ManagedChannel
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.HandlerList
import java.util.UUID


@Singleton
class PlayerModule @Inject constructor(
    orbitClient: ManagedChannel,
    private val pubsub: JedisListener,
    @Named("server_id")
    private val serverId: String
): MinecraftModule("PlayerModule", listOf(PlayerConfig::class)) {
    private val playerService = PlayerServiceGrpcKt.PlayerServiceCoroutineStub(orbitClient)
    private lateinit var statusListener: StatusListener
    private lateinit var playerConfig: PlayerConfig

    /**
     * Get a players name with proper formatting.
     */
    suspend fun getPlayerFormatted(uuid: String): Component {
        val playerData = playerService.getPlayerData(getPlayerRequest { this.uuid = uuid })
        val mm = MiniMessage.miniMessage()
        return mm.deserialize(playerConfig.playerName.name,
            Placeholder.component("name", Component.text(playerData.name)),
            Placeholder.component("display_name", mm.deserialize(playerData.displayName)),
            Placeholder.component("uuid", Component.text(playerData.uuid)),
            Placeholder.component("online", mm.deserialize(playerConfig.playerName.online)),
            Placeholder.component("offline", mm.deserialize(playerConfig.playerName.offline)),
            Placeholder.component("server_id", Component.text(serverId))
        )
    }

    override suspend fun enable() {
        playerConfig = loadConfig<PlayerConfig>()!!
        statusListener = StatusListener(plugin as AstroIslesCore, playerConfig, playerService)
        pubsub.addListener(statusListener)
        addListener(statusListener)
    }

    override suspend fun disable() {
        pubsub.removeListener(statusListener)
    }
}