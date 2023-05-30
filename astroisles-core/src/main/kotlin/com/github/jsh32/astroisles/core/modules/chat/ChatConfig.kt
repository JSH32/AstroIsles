package com.github.jsh32.astroisles.core.modules.chat

import com.github.jsh32.astroisles.common.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@Config("chat.conf")
@ConfigSerializable
class ChatConfig(
    @Comment("Chat message format. Variables available: [player, message]")
    val chatFormat: String = "<gray><player>:</gray> <message>"
)