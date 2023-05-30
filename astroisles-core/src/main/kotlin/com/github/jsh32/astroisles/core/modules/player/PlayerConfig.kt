package com.github.jsh32.astroisles.core.modules.player

import com.github.jsh32.astroisles.common.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@Config("player.conf")
@ConfigSerializable
class PlayerConfig {
    @Comment("Format for player name/details")
    val playerName = PlayerName()

    @Comment("Player status messages (join, leave, death)")
    val status = PlayerStatus()

    @Comment("Welcome messages on player join")
    val welcome = WelcomeMessages()
}

@ConfigSerializable
class PlayerName {
    @Comment("Player name format. Variables: [display_name, name, uuid, online, server_id]")
    val name = """
        <hover:show_text:'<gray>Name: <dark_gray><name></gray>
        <gray>UUID: <dark_gray><uuid></gray>
        <gray>Online: <dark_gray><online></gray>
        <gray>Server: <dark_gray><server_id></gray>'><display_name></hover>
    """.trimIndent()

    @Comment("Used when showing if player is offline")
    val offline = "<red>Offline</red>"

    @Comment("Used when showing if player is online")
    val online = "<green>Online</green>"
}

@ConfigSerializable
class PlayerStatus {
    @Comment("Message sent to players when a player leaves. Variables available: [player]")
    val onLeave = "<gray>[<red>-</red>]</gray> <player>"

    @Comment("Message sent to players when a player joins. Variables available: [player]")
    val onJoin = "<gray>[<green>+</green>]</gray> <player>"
}

@ConfigSerializable
class WelcomeMessages {
    @Comment("Welcome message on first join. Variables available: [player]")
    val firstJoin = """
        <gray>Welcome to <gradient:#9b59b6:#3498db>AstroIsles</gradient> <player>!

        We're thrilled to have you onboard for an interstellar
        adventure! As a new explorer, you've been granted your very
        own realm within the vast <color:#3498db>AstroVerse</color>. Craft your story,
        and journey through an enormous hub of
        interconnected planets!

        Venture boldly <player>! The stars await...
    """.trimIndent()

    @Comment("Welcome message. Variables available: [player, time_since_last_quit]")
    val welcome = """
        <gray>Welcome back to <gradient:#9b59b6:#3498db>AstroIsles</gradient>, <player>!

        You've been away from the <color:#3498db>AstroVerse</color> for <color:#9b59b6><time_since_last_quit></color>.
        We hope you're ready to continue exploring.

        Happy adventuring!</gray>
    """.trimIndent()
}