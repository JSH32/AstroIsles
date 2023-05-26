package com.github.jsh32.astroisles.common.module

import com.google.inject.Inject
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.loader.ConfigurationLoader
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
            val path = Paths.get(plugin.dataFolder.path, annotation.file)
            val file = path.toFile()

            val loader: ConfigurationLoader<CommentedConfigurationNode> =
                HoconConfigurationLoader.builder()
                    .defaultOptions { opts -> opts.shouldCopyDefaults(true) }
                    .prettyPrinting(true)
                    .path(path)
                    .build()

            val node = loader.load()

            if (!file.exists()) {
                created.add(file)

                // Set node to default value of config class and save it.
                node.set(config.java, node.get(config.java)!!)
                loader.save(node)
            } else {
                // Set config to class.
                configs[config.java] = node.get(config.java)!!
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

    open fun enable() {
    }

    open fun disable() {}
}