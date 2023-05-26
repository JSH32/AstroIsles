package com.github.jsh32.astroisles.proxy

import PlayerServiceGrpcKt
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.ProxyServer
import createPlayerRequest
import getPlayerRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusException
import net.kyori.adventure.text.Component
import playerJoinRequest
import playerQuitRequest


class PlayerListener(orbitClient: ManagedChannel) {
    private val playerService = PlayerServiceGrpcKt.PlayerServiceCoroutineStub(orbitClient)

    @Subscribe
    suspend fun ServerPreConnectEvent.onPlayerLogin() {
        try {
            playerService.getPlayerData(getPlayerRequest { uuid = player.uniqueId.toString() })
            player.sendMessage(Component.text("Welcome back to server"))
        } catch (e: StatusException) {
            if (e.status.code == Status.NOT_FOUND.code) {
                playerService.createPlayer(createPlayerRequest {
                    uuid = player.uniqueId.toString()
                    name = player.username
                })

                player.sendMessage(Component.text("Welcome to server"))
            } else { throw e }
        }
    }

    @Subscribe
    suspend fun ServerConnectedEvent.onPlayerConnectedServer() {
        playerService.playerJoin(playerJoinRequest {
            uuid = player.uniqueId.toString()
            serverId = server.serverInfo.name
        })
    }

    @Subscribe
    suspend fun DisconnectEvent.onPlayerLogout() {
        playerService.playerQuit(playerQuitRequest { uuid = player.uniqueId.toString() })
    }
}