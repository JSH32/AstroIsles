package com.github.jsh32.astroisles.core

import com.github.jsh32.astroisles.common.CommandManager
import com.github.jsh32.astroisles.common.loadConfig
import com.github.jsh32.astroisles.common.module.ModuleLoader
import com.github.jsh32.astroisles.common.module.spigotLoader
import com.github.jsh32.astroisles.common.redis.JedisListener
import com.github.jsh32.astroisles.core.modules.FriendModule
import com.github.jsh32.astroisles.core.modules.player.PlayerModule
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.google.inject.AbstractModule
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import redis.clients.jedis.JedisPool
import java.nio.file.Paths


/**
 * GRPC orbit connection stuff.
 */
private class CoreModule(
    private val orbit: ManagedChannel,
    private val redis: JedisPool,
    private val listener: JedisListener,
    private val audience: BukkitAudiences
) : AbstractModule() {
    override fun configure() {
        this.bind(ManagedChannel::class.java).toInstance(orbit)
        this.bind(JedisPool::class.java).toInstance(redis)
        this.bind(JedisListener::class.java).toInstance(listener)
        this.bind(BukkitAudiences::class.java).toInstance(audience)
    }
}

class AstroIslesCore : SuspendingJavaPlugin() {
    lateinit var audience: BukkitAudiences

    private lateinit var config: AstroIslesConfig

    private lateinit var modules: ModuleLoader
    private lateinit var commandManager: CommandManager

    private lateinit var jedisPool: JedisPool
    private lateinit var orbitChannel: ManagedChannel
    private lateinit var jedisListener: JedisListener

    override suspend fun onEnableAsync() {
        this.audience = BukkitAudiences.create(this)
        this.commandManager = CommandManager(this, audience, "AstroIsles", "core")

        this.config = loadConfig<AstroIslesConfig>(
            Paths.get(dataFolder.path, "core.conf").toFile()
        ).config

        this.orbitChannel = ManagedChannelBuilder.forAddress(config.orbit.host, config.orbit.port).usePlaintext().build()
        this.jedisPool = JedisPool(config.redis.host, config.redis.port)
        this.jedisListener = JedisListener(jedisPool)
        this.jedisListener.start()

        modules = spigotLoader(this, CoreModule(orbitChannel, jedisPool, jedisListener, audience))

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

    override suspend fun onDisableAsync() {
        modules.deInitialize()
        orbitChannel.shutdown()
        if (jedisListener.isStarted()) jedisListener.stop()
        jedisPool.close()
    }
}