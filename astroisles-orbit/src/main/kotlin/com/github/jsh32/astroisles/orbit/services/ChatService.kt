package com.github.jsh32.astroisles.orbit.services

import ChatServiceGrpcKt
import com.github.jsh32.astroisles.common.redis.RedisMessages
import com.github.jsh32.astroisles.common.redis.publishChannel
import com.google.protobuf.Empty
import com.google.protobuf.empty
import redis.clients.jedis.JedisPool

class ChatService(private val redis: JedisPool) : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {
    override suspend fun sendChat(request: ChatServiceOuterClass.ChatRequest): Empty {
        redis.resource.use { conn ->
            conn.publishChannel(RedisMessages.PlayerChat(request.playerId, request.message))
        }

        return empty {}
    }
}