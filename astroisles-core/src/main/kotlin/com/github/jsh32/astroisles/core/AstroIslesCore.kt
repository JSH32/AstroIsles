package com.github.jsh32.astroisles.core

import com.github.jsh32.astroisles.common.CommandManager
import com.github.jsh32.astroisles.common.loadConfig
import com.github.jsh32.astroisles.common.module.spigotLoader
import com.github.jsh32.astroisles.core.modules.FriendModule
import com.github.jsh32.astroisles.core.modules.PlayerModule
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.google.inject.AbstractModule
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Paths

/**
 * GRPC orbit connection stuff.
 */
private class OrbitModule(private val host: String, private val port: Int) : AbstractModule() {
    override fun configure() {
        val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
        this.bind(ManagedChannel::class.java).toInstance(channel)
    }
}

class AstroIslesCore : SuspendingJavaPlugin() {
    private val config = loadConfig<AstroIslesConfig>(
        Paths.get(dataFolder.path, "core.conf").toFile()
    ).config

    private val modules = spigotLoader(this, OrbitModule(config.orbit.host, config.orbit.port))
    private lateinit var commandManager: CommandManager
    private lateinit var audiences: BukkitAudiences

    override fun onEnable() {
        this.audiences = BukkitAudiences.create(this)
        this.commandManager = CommandManager(this, audiences, "AstroIsles", "core")

        modules.setPostInit {
            server.pluginManager.registerSuspendingEvents(it, this)
            commandManager.registerCommands(it)
        }

        modules.initialize(
            FriendModule::class,
            PlayerModule::class
        )

        logger.info("Started with modules: [${modules.getRegisteredModuleNames().joinToString(", ")}]")
    }

    override fun onDisable() {
        modules.deInitialize()
    }
}