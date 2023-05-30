package com.github.jsh32.astroisles.orbit

import com.github.jsh32.astroisles.common.loadConfig
import com.github.jsh32.astroisles.orbit.models.Friends
import com.github.jsh32.astroisles.orbit.models.Player
import com.github.jsh32.astroisles.orbit.services.ChatService
import com.github.jsh32.astroisles.orbit.services.FriendService
import com.github.jsh32.astroisles.orbit.services.PlayerService
import io.ebean.Database
import io.ebean.DatabaseFactory
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig
import io.ebean.migration.MigrationConfig
import io.ebean.migration.MigrationRunner
import io.grpc.ServerBuilder
import redis.clients.jedis.JedisPool
import java.nio.file.Paths
import java.util.*


fun main() {
    val config = loadConfig<Config>(Paths.get("orbit.conf").toAbsolutePath().toFile()).config

    // This can be discarded since it's the main DB.
    initDatabase(config.database, Friends::class.java, Player::class.java)

    val jedisPool = JedisPool(config.redis.host, config.redis.port)

    val server = ServerBuilder
        .forPort(config.port)
        .intercept(LoggingInterceptor())
        .addService(FriendService())
        .addService(PlayerService(jedisPool))
        .addService(ChatService(jedisPool))
        .build()

    server.start()
    println("Server started, listening on ${config.port}")
    Runtime.getRuntime().addShutdownHook(
        Thread {
            println("*** shutting down gRPC server since JVM is shutting down")
            server.shutdown()
            println("*** server shut down")
        }
    )

    server.awaitTermination()
}

fun initDatabase(config: OrbitDatabaseConfig, vararg classes: Class<*>): Database {
    Class.forName("org.postgresql.Driver")

    val dataSourceConfig = DataSourceConfig()
    dataSourceConfig.setUrl("jdbc:${config.uri}")
    dataSourceConfig.setUsername(config.username)
    dataSourceConfig.setPassword(config.password)

    val dbConfig = DatabaseConfig()
    dbConfig.dataSourceConfig = dataSourceConfig
    dbConfig.isDefaultServer = true
    dbConfig.setClasses(classes.asList())

    val database = DatabaseFactory.create(dbConfig)

    // Run available migrations
    val migrationConfig = MigrationConfig()
    migrationConfig.migrationPath =
        "classpath:/dbmigration/postgres"
    migrationConfig.load(Properties())
    MigrationRunner(migrationConfig).run(database.dataSource())

    return database
}