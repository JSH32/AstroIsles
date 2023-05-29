package com.github.jsh32.astroisles.core.modules.player

import PlayerServiceGrpcKt
import com.github.jsh32.astroisles.common.module.Module
import com.github.jsh32.astroisles.common.redis.JedisListener
import com.github.jsh32.astroisles.core.AstroIslesCore
import com.google.inject.Inject
import com.google.inject.Singleton
import io.grpc.ManagedChannel
import org.bukkit.Bukkit.getServer
import org.bukkit.event.HandlerList


@Singleton
class PlayerModule @Inject constructor(
    orbitClient: ManagedChannel,
    private val pubsub: JedisListener,
): Module("PlayerModule", listOf(PlayerConfig::class)) {
    private val playerService = PlayerServiceGrpcKt.PlayerServiceCoroutineStub(orbitClient)
    private lateinit var statusListener: StatusListener

    override suspend fun enable() {
        statusListener = StatusListener(plugin as AstroIslesCore, loadConfig<PlayerConfig>()!!, playerService)
        pubsub.addListener(statusListener)
        plugin.server.pluginManager.registerEvents(statusListener, plugin)
    }

    override suspend fun disable() {
        pubsub.removeListener(statusListener)
        HandlerList.unregisterAll(statusListener)
    }
}