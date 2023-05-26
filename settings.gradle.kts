rootProject.name = "AstroIsles"

dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include (
    "astroisles-common",
    "astroisles-core",
    "astroisles-orbit",
    "astroisles-proxy"
)
