plugins {
    id("paper-plugin")
    id("grpc-definitions")
}

ext.set("spigot", "true")

dependencies {
    implementation(project(":astroisles-common")) {
        capabilities {
            requireCapability("com.github.jsh32.astroisles.common:spigot")
        }
    }
}

bukkit {
    name = "AstroIslesCore"
    main = "com.github.jsh32.astroisles.core.AstroIslesCore"
    apiVersion = "1.19"
    authors = listOf("JSH32")
}