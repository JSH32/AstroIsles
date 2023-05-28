package com.github.jsh32.astroisles.core.modules.player

import PlayerServiceGrpcKt
import com.github.jsh32.astroisles.common.module.Module
import com.github.jsh32.astroisles.common.redis.JedisListener
import com.google.inject.Inject
import com.google.inject.Singleton
import io.grpc.ManagedChannel


@Singleton
class PlayerModule @Inject constructor(
    orbitClient: ManagedChannel,
    private val pubsub: JedisListener
): Module("PlayerModule", listOf(PlayerConfig::class)) {
    private val playerService = PlayerServiceGrpcKt.PlayerServiceCoroutineStub(orbitClient)

    override suspend fun enable() {
        val listener = JoinListener(plugin, loadConfig<PlayerConfig>()!!, playerService)
        pubsub.addListener(listener)
    }

    override suspend fun disable() {
        super.disable()
    }
}