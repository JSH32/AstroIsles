package com.github.jsh32.astroisles.core.modules

import PlayerServiceGrpcKt
import com.github.jsh32.astroisles.common.module.Module
import com.google.inject.Inject
import com.google.inject.Singleton
import createPlayerRequest
import io.grpc.ManagedChannel
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent


@Singleton
class PlayerModule  @Inject constructor(
    orbitClient: ManagedChannel
): Module("PlayerModule") {
    private val playerService = PlayerServiceGrpcKt.PlayerServiceCoroutineStub(orbitClient)
}