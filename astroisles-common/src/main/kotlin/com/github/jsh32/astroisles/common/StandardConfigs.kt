package com.github.jsh32.astroisles.common

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

/**
 * Standard config classes to be used across projects
 */
object StandardConfigs {
    @ConfigSerializable
    class RedisConfig(
        @Comment("Redis host")
        val host: String = "localhost",
        @Comment("Redis port")
        val port: Int = 6379
    )

    @ConfigSerializable
    class OrbitConfig(
        @Comment("Host of orbit server")
        val host: String = "0.0.0.0",
        @Comment("Port of orbit server")
        val port: Int = 3000
    )
}