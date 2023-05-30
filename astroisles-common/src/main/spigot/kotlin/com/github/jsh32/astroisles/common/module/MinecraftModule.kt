package com.github.jsh32.astroisles.common.module

import com.github.jsh32.astroisles.common.CommandManager
import com.github.jsh32.astroisles.common.minecraftSerializers
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.google.inject.Inject
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Path
import kotlin.reflect.KClass


open class MinecraftModule(name: String, configClasses: List<KClass<*>>? = null) : Module(name, configClasses) {
    @Inject
    lateinit var plugin: JavaPlugin

    @Inject
    private lateinit var commandManager: CommandManager

    private val listeners = mutableListOf<Listener>()

    override fun getConfigsFolder(): Path = plugin.dataFolder.toPath()

    override fun getSerializers() = minecraftSerializers

    /**
     * Add an event listener to the module.
     */
    fun addListener(listener: Listener) {
        plugin.server.pluginManager.registerSuspendingEvents(listener, plugin)
    }

    fun registerCommands(instance: Any) {
        commandManager.registerCommands(instance)
    }

    final override suspend fun onDisable() {
        for (listener in listeners) {
            HandlerList.unregisterAll(listener)
        }

        super.onDisable()
    }
}