package com.github.jsh32.astroisles.common.module

import com.github.jsh32.astroisles.common.Config
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass


abstract class Module(val name: String, private val configClasses: List<KClass<*>>? = null) {
    private val configs = mutableMapOf<Class<*>, Any>()

    /**
     * Called when module is disabled.
     * This is not usually intended to be overridden, override [disable] instead.
     */
    open suspend fun onDisable() {
        disable()
    }

    /**
     * Get the folder where configs are stored
     */
    abstract fun getConfigsFolder(): Path

    /**
     * Get serializers used for this module.
     */
    open fun getSerializers(): ((TypeSerializerCollection.Builder) -> Unit)? = null

    /**
     * Create/load all configs and return configs that were created.
     */
    fun initConfigs(): List<File> {
        // List of created configs.
        val created = mutableListOf<File>()

        configClasses?.forEach { config ->
            val annotation = config.java.getAnnotation(Config::class.java)!!
            val file = Paths.get(getConfigsFolder().toString(), annotation.file).toFile()

            val loaded = com.github.jsh32.astroisles.common.loadConfig(config.java, file, false, getSerializers())
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

    open suspend fun enable() {}

    open suspend fun disable() {}
}