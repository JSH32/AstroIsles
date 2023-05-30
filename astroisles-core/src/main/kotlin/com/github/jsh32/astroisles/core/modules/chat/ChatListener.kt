package com.github.jsh32.astroisles.core.modules.chat

import ChatServiceGrpcKt
import chatRequest
import com.github.jsh32.astroisles.common.redis.ChannelListener
import com.github.jsh32.astroisles.common.redis.RedisMessages
import com.github.jsh32.astroisles.core.AstroIslesCore
import com.github.jsh32.astroisles.core.modules.player.PlayerModule
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener(
    private val plugin: AstroIslesCore,
    private val chatService: ChatServiceGrpcKt.ChatServiceCoroutineStub,
    private val config: ChatConfig,
    private val playerModule: PlayerModule
) : Listener, ChannelListener(RedisMessages.PlayerChat::class) {
    init {
        handle<RedisMessages.PlayerChat> {
            runBlocking {
                plugin.audience.players().sendMessage(MiniMessage.miniMessage().deserialize(config.chatFormat,
                    Placeholder.component("player", playerModule.getPlayerFormatted(it.playerId)),
                    Placeholder.unparsed("message", it.message)))
            }
        }
    }

    @EventHandler
    suspend fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        chatService.sendChat(chatRequest {
            playerId = event.player.uniqueId.toString()
            message = MiniMessage.miniMessage().serialize(event.message())
        })
    }
}