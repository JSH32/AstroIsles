plugins {
    id("kotlin-project")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    api("com.google.inject:guice:7.0.0")
    api("org.spongepowered:configurate-hocon:4.1.2")
    api("org.spongepowered:configurate-yaml:4.1.2")
    api("net.kyori:adventure-api:4.13.1")
    api("net.kyori:adventure-platform-bukkit:4.3.0")

    api("cloud.commandframework:cloud-paper:1.8.3")
    api("cloud.commandframework:cloud-minecraft-extras:1.8.3")
    api("cloud.commandframework:cloud-annotations:1.8.3")
    api("cloud.commandframework:cloud-kotlin-coroutines:1.8.3")
    api("cloud.commandframework:cloud-kotlin-coroutines-annotations:1.8.3")
    api("me.lucko:commodore:2.2")
    annotationProcessor("cloud.commandframework:cloud-annotations:1.7.1")

    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}