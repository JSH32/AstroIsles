package com.github.jsh32.astroisles.proxy

import com.github.jsh32.astroisles.common.loadConfig
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import io.grpc.ManagedChannelBuilder
import org.slf4j.Logger
import java.nio.file.Path
import java.nio.file.Paths


@Plugin(
    id = "astroislesproxy",
    name = "AstroIsles Proxy",
    description = "AstroIsles proxy plugin",
    version = "0.1.0-SNAPSHOT",
    authors = ["JSH32"]
)
class AstroIslesProxy @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory dataDirectory: Path,
    private val suspendingPluginContainer: SuspendingPluginContainer
) {
    private val config = loadConfig<ProxyConfig>(Paths.get(dataDirectory.toString(), "orbit.conf").toFile()).config
    private val orbitConnection = ManagedChannelBuilder.forAddress(config.orbit.host, config.orbit.port).usePlaintext().build()

//    init {
//        suspendingPluginContainer.initialize(this)
//        suspendingPluginContainer.initialize(this)
//    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        suspendingPluginContainer.initialize(this)
        server.eventManager.registerSuspend(this, PlayerListener(orbitConnection))
        logger.info("AstroIsles proxy core initialized!")
    }
}