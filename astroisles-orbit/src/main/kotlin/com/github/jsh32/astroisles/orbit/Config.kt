package com.github.jsh32.astroisles.orbit

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class Config {
    @Comment("Port to run gRPC server")
    val port: Int = 3000

    @Comment("Database config")
    val database: OrbitDatabaseConfig = OrbitDatabaseConfig()

    @Comment("Redis connection details")
    val redis: RedisConfig = RedisConfig()
}

@ConfigSerializable
class RedisConfig {
    val host: String = "localhost"
    val port: Int = 6379
}

@ConfigSerializable
class OrbitDatabaseConfig {
    val uri: String = "postgresql://localhost:5432/orbit"
    val username: String = "username"
    val password: String = "password"
}