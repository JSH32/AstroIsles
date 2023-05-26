package com.github.jsh32.astroisles.common.module

import com.github.jsh32.astroisles.common.Config
import com.google.inject.Inject
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass


abstract class Module(val name: String, private val configClasses: List<KClass<*>>? = null) : Listener {
    @Inject
    lateinit var plugin: JavaPlugin

    private val configs = mutableMapOf<Class<*>, Any>()

    fun onDisable() {
        HandlerList.unregisterAll(this)
        disable()
    }

    /**
     * Create/load all configs and return configs that were created.
     */
    fun initConfigs(): List<File> {
        // List of created configs.
        val created = mutableListOf<File>()

        configClasses?.forEach { config ->
            val annotation = config.java.getAnnotation(Config::class.java)!!
            val file = Paths.get(plugin.dataFolder.path, annotation.file).toFile()

            val loaded = com.github.jsh32.astroisles.common.loadConfig(config.java, file)
            if (loaded.created) {
                created.add(file)
            } else {
                configs[config.java] = loaded.config
            }
        }

        return created
    }

    /**
     * Load a config from file if exists.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> loadConfig(config: Class<T>): T? = configs[config] as T?

    /**
     * Load a config from file if exists.
     */
    inline fun <reified T> loadConfig(): T? = this.loadConfig(T::class.java)

    open fun enable() {}

    open fun disable() {}
}