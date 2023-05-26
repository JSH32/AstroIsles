plugins {
    id("kotlin-project")
    id("grpc-definitions")

}

repositories {
    maven(uri("https://repo.papermc.io/repository/maven-public/"))
}

dependencies {
    implementation(project(":astroisles-common"))

    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-api:2.12.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-core:2.12.0")

    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")
}