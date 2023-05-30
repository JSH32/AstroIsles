package com.github.jsh32.astroisles.core.modules.chat

import ChatServiceGrpcKt
import com.github.jsh32.astroisles.common.module.MinecraftModule
import com.github.jsh32.astroisles.common.redis.JedisListener
import com.github.jsh32.astroisles.core.AstroIslesCore
import com.github.jsh32.astroisles.core.modules.player.PlayerModule
import com.google.inject.Inject
import io.grpc.ManagedChannel
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class ChatModule @Inject constructor(
    orbitClient: ManagedChannel,
    private val playerModule: PlayerModule,
    private val pubsub: JedisListener,
) : MinecraftModule("ChatModule", listOf(ChatConfig::class)) {
    private val chatService = ChatServiceGrpcKt.ChatServiceCoroutineStub(orbitClient)
    private lateinit var listener: ChatListener

    override suspend fun enable() {
        val config = loadConfig<ChatConfig>()!!

        listener = ChatListener(plugin as AstroIslesCore, chatService, config, playerModule)
        pubsub.addListener(listener)
        addListener(listener)
    }

    override suspend fun disable() {
        pubsub.removeListener(listener)
    }
}