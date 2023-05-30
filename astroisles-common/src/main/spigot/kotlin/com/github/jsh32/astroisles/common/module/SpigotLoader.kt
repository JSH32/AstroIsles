package com.github.jsh32.astroisles.common.module

import com.github.jsh32.astroisles.common.CommandManager
import com.google.inject.AbstractModule
import org.bukkit.plugin.java.JavaPlugin

/**
 * Spigot injector.
 */
class SpigotLoader<T : JavaPlugin>(
    private val plugin: T,
    private val commandManager: CommandManager
) : AbstractModule() {
    override fun configure() {
        bind(JavaPlugin::class.java).toInstance(plugin)
        bind(plugin.javaClass).toInstance(plugin)
        bind(CommandManager::class.java).toInstance(commandManager)
    }
}

/**
 * Make a module loader with a spigot plugin.
 */
fun <T : JavaPlugin> spigotLoader(
    plugin: T,
    commandManager: CommandManager,
    vararg modules: com.google.inject.Module
) = ModuleLoader(SpigotLoader(plugin, commandManager), *modules)