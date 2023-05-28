package com.github.jsh32.astroisles.proxy

import com.github.jsh32.astroisles.common.StandardConfigs
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class ProxyConfig {
    @Comment("Orbit config settings")
    val orbit: StandardConfigs.OrbitConfig = StandardConfigs.OrbitConfig()
}
