package com.github.jsh32.astroisles.common.module

import com.google.inject.AbstractModule
import org.bukkit.plugin.java.JavaPlugin

/**
 * Spigot injector.
 */
class SpigotModule<T : JavaPlugin>(private val plugin: T) : AbstractModule() {
    override fun configure() {
        bind(JavaPlugin::class.java).toInstance(plugin)
        bind(plugin.javaClass).toInstance(plugin)
    }
}

/**
 * Make a module loader with a spigot plugin.
 */
fun <T : JavaPlugin> spigotLoader(
    plugin: T,
    vararg modules: com.google.inject.Module
) = ModuleLoader(SpigotModule(plugin), *modules)