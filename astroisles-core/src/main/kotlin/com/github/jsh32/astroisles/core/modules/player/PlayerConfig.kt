package com.github.jsh32.astroisles.core.modules.player

import com.github.jsh32.astroisles.common.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@Config("player_join.conf")
@ConfigSerializable
class PlayerConfig {
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

    @Comment("Message sent to players when a player leaves. Variables available: [player]")
    val onLeave = "<gray>[<red>-</red>]</gray> <player>"

    @Comment("Message sent to players when a player joins. Variables available: [player]")
    val onJoin = "<gray>[<green>+</green>]</gray> <player>"
}