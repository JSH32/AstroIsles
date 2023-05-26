import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("kotlin-project")
    id("grpc-definitions")
    application
}

application {
    mainClass.set("com.github.jsh32.astroisles.orbit.MainKt")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.7")

    // Ebean
    val ebeanVersion = "13.15.0"
    implementation("io.ebean:ebean:$ebeanVersion")
    implementation("io.ebean:ebean-querybean:$ebeanVersion")
    kapt("io.ebean:kotlin-querybean-generator:$ebeanVersion")
    implementation("org.postgresql:postgresql:42.6.0")
}