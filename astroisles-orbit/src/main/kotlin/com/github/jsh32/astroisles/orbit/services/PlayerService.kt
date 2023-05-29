package com.github.jsh32.astroisles.orbit.services

import PlayerServiceGrpcKt
import PlayerServiceOuterClass
import com.github.jsh32.astroisles.common.redis.*
import com.github.jsh32.astroisles.orbit.fromSql
import com.github.jsh32.astroisles.orbit.models.Player
import com.github.jsh32.astroisles.orbit.models.query.QPlayer
import com.google.gson.Gson
import com.google.protobuf.Empty
import com.google.protobuf.TimestampKt
import com.google.protobuf.empty
import io.grpc.Status
import io.grpc.StatusRuntimeException
import player
import redis.clients.jedis.JedisPool
import java.sql.Timestamp
import java.time.Instant
import java.util.*


class PlayerService(private val redis: JedisPool) : PlayerServiceGrpcKt.PlayerServiceCoroutineImplBase() {
    override suspend fun createPlayer(request: PlayerServiceOuterClass.CreatePlayerRequest): PlayerServiceOuterClass.Player {
        val playerUuid = UUID.fromString(request.uuid)
        val found = QPlayer().where().playerId.eq(playerUuid).findOne()
        if (found != null) {
            throw StatusRuntimeException(Status.ALREADY_EXISTS
                .withDescription("User with ID ${found.playerId} already exists."))
        }

        val player = Player(playerUuid, request.name, request.name, Timestamp.from(Instant.now()))
        player.save()

        return player {
            uuid = playerUuid.toString()
            name = player.playerName
            displayName = player.displayName
            online = true
            firstJoin = TimestampKt.fromSql(player.firstJoin)
        }
    }

    override suspend fun getPlayerData(request: PlayerServiceOuterClass.GetPlayerRequest): PlayerServiceOuterClass.Player {
        val foundPlayer = QPlayer()
            .or()
            .playerId.eq(UUID.fromString(request.uuid))
            .playerName.eq(request.name)
            .endOr()
            .findOne()
            ?: throw StatusRuntimeException(Status.NOT_FOUND
                .withDescription("User with that query was not found."))

        return player {
            uuid = foundPlayer.playerId.toString()
            name = foundPlayer.playerName
            displayName = foundPlayer.displayName

            val details = redis.resource.use { conn ->
                conn.select(RedisDatabases.PLAYER_SERVER)
                conn.getObject<RedisObjects.PlayerServerData>(uuid)
            }

            online = details?.serverId != null
            if (online) {
                serverId = details?.serverId!!
            }

            firstJoin = TimestampKt.fromSql(foundPlayer.firstJoin)
            if (foundPlayer.lastLogout != null) {
                lastLogout = TimestampKt.fromSql(foundPlayer.lastLogout)
            }
        }
    }

    override suspend fun playerJoin(request: PlayerServiceOuterClass.PlayerJoinRequest): Empty {
        val joiningPlayer = QPlayer()
            .playerId.eq(UUID.fromString(request.uuid))
            .findOne()

        redis.resource.use { conn ->
            conn.select(RedisDatabases.PLAYER_SERVER)
            conn.set(request.uuid, Gson().toJson(RedisObjects.PlayerServerData(request.serverId)))
            conn.publishChannel(
                RedisMessages.PlayerJoin(
                request.uuid,
                request.serverId,
                joiningPlayer?.lastLogout == null,
                conn.exists(request.uuid)
            ))
        }

        // This marks the player as having experienced the first join.
        if (joiningPlayer?.lastLogout == null) {
            QPlayer()
                .playerId.eq(UUID.fromString(request.uuid))
                .asUpdate()
                .set("last_logout", Timestamp.from(Instant.now()))
                .update()
        }

        return empty {}
    }

    override suspend fun playerQuit(request: PlayerServiceOuterClass.PlayerQuitRequest): Empty {
        redis.resource.use { conn ->
            conn.select(RedisDatabases.PLAYER_SERVER)
            conn.del(request.uuid)
            conn.publishChannel(RedisMessages.PlayerQuit(request.uuid))
        }

        QPlayer()
            .playerId.eq(UUID.fromString(request.uuid))
            .asUpdate()
            .set("last_logout", Timestamp.from(Instant.now()))
            .update()

        return empty {}
    }
}