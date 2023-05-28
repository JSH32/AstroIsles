package com.github.jsh32.astroisles.common.module

import com.google.inject.Guice
import java.io.File
import kotlin.reflect.KClass

class ModuleLoader(vararg guiceModules: com.google.inject.Module) {
    private var postInitHook: ((Module) -> Unit)? = null

    /**
     * Set lambda to be called after module has been initialized.
     */
    fun setPostInit(hook: ((Module) -> Unit)?) {
        postInitHook = hook
    }

    private val injector = Guice.createInjector(*guiceModules)
    private val registry = mutableMapOf<Class<*>, Module>()

    /**
     * Get names of all modules within module registry.
     */
    fun getRegisteredModuleNames() = registry.values.map { it.name }

    suspend fun initialize(vararg modules: KClass<out Module>) {
        val createdConfigs = mutableListOf<File>()
        val created = mutableListOf<Module>()

        for (module in modules) {
            val createdModule = injector?.getInstance(module.java)!!
            createdConfigs.addAll(createdModule.initConfigs())
            created.add(createdModule)
        }

        if (createdConfigs.isNotEmpty()) {
            val list = createdConfigs.joinToString(", ") { it.path }
            throw ModuleConfigException("New configs were created and need to be edited: $list\n Reload after editing.", createdConfigs)
        }

        for (module in created) {
            module.enable()
            postInitHook?.let { it(module) }
            registry[module.javaClass] = module
        }
    }

    suspend fun deInitialize() {
        for (module in registry.values) {
            module.disable()
        }

        registry.clear()
    }
}

/**
 * Thrown when new configs have been created and need to be modified.
 */
class ModuleConfigException(message: String, val configs: List<File>) : Exception(message)