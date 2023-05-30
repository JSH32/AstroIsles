plugins {
    id("kotlin-project")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

sourceSets {
    create("spigot") {
        kotlin {
            compileClasspath += sourceSets["main"].output
            runtimeClasspath += sourceSets["main"].output
            this.srcDir("src/main/spigot/kotlin")
        }
    }
}

java {
    registerFeature("spigot") {
        usingSourceSet(sourceSets["spigot"])
        capability("com.github.jsh32.astroisles.common", "spigot", "1.0")
    }
}

dependencies {
    // Configurate
    api("org.spongepowered:configurate-hocon:4.1.2")

    // Redis
    api("redis.clients:jedis:4.3.0")

    api("com.google.code.gson:gson:2.10.1")
    api("com.google.inject:guice:7.0.0")

    val spigotApi by configurations
    val spigotAnnotationProcessor by configurations
    val spigotCompileOnly by configurations

    spigotApi(project(":astroisles-common"))

    // Adventure
    spigotApi("net.kyori:adventure-api:4.13.1")
    spigotApi("net.kyori:adventure-platform-bukkit:4.3.0")

    spigotApi("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.12.0")
    spigotApi("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.12.0")

    // Cloud command framework
    spigotApi("cloud.commandframework:cloud-paper:1.8.3")
    spigotApi("cloud.commandframework:cloud-minecraft-extras:1.8.3")
    spigotApi("cloud.commandframework:cloud-annotations:1.8.3")
    spigotApi("cloud.commandframework:cloud-kotlin-coroutines:1.8.3")
    spigotApi("cloud.commandframework:cloud-kotlin-coroutines-annotations:1.8.3")
    spigotApi("me.lucko:commodore:2.2")
    spigotAnnotationProcessor("cloud.commandframework:cloud-annotations:1.7.1")

    // Paper
    spigotCompileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    // Velocity
//    compileOnly("com.velocitypowered:velocity-api:3.1.1")
//    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
}