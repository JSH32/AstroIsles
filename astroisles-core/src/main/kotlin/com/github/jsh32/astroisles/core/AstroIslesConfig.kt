package com.github.jsh32.astroisles.core

import com.github.jsh32.astroisles.common.StandardConfigs
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class AstroIslesConfig {
    @Comment("Orbit config settings")
    val orbit: StandardConfigs.OrbitConfig = StandardConfigs.OrbitConfig()

    @Comment("Redis settings")
    val redis: StandardConfigs.RedisConfig = StandardConfigs.RedisConfig()

    @Comment("Server ID")
    val serverId: String = ""
}
