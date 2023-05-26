plugins {
    id("paper-plugin")
    id("grpc-definitions")
}

dependencies {
    implementation(project(":astroisles-common"))
}

bukkit {
    name = "AstroIslesCore"
    main = "com.github.jsh32.astroisles.core.AstroIslesCore"
    apiVersion = "1.19"
    authors = listOf("JSH32")
}