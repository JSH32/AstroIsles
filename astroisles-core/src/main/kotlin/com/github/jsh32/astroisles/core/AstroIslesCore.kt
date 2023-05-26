package com.github.jsh32.astroisles.core

import com.github.jsh32.astroisles.common.module.CommandManager
import com.github.jsh32.astroisles.common.module.ModuleLoader
import com.github.jsh32.astroisles.core.modules.FriendModule
import com.google.inject.AbstractModule
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin

/**
 * GRPC orbit connection stuff.
 */
private class OrbitModule(private val host: String, private val port: Int) : AbstractModule() {
    override fun configure() {
        val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
        this.bind(ManagedChannel::class.java).toInstance(channel)
    }
}

class AstroIslesCore : JavaPlugin() {
    private val modules = ModuleLoader(this, OrbitModule("0.0.0.0", 3000))
    private lateinit var commandManager: CommandManager
    private lateinit var audiences: BukkitAudiences

    override fun onEnable() {
        this.audiences = BukkitAudiences.create(this)
        this.commandManager = CommandManager(this, audiences, "AstroIsles", "core")

        modules.setPostInit { commandManager.registerCommands(it) }

        modules.initialize(
            FriendModule::class
        )

        logger.info("Started with modules: [${modules.getRegisteredModuleNames().joinToString(", ")}]")
    }

    override fun onDisable() {
        modules.deInitialize()
    }
}