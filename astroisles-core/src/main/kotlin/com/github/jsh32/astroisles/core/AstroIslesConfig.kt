package com.github.jsh32.astroisles.core

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class AstroIslesConfig(
    @Comment("Orbit config settings")
    val orbit: OrbitConfig = OrbitConfig()
)

@ConfigSerializable
class OrbitConfig {
    val host = "0.0.0.0"
    val port = 3000
}