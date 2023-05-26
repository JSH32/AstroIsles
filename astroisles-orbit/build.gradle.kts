plugins {
    id("kotlin-project")
    id("grpc-definitions")
    id("io.ebean") version "13.15.0"
    application
}

application {
    mainClass.set("com.github.jsh32.astroisles.orbit.MainKt")
}

dependencies {
    implementation(project(":astroisles-common"))

    implementation("ch.qos.logback:logback-classic:1.4.7")

    // Ebean
    val ebeanVersion = "13.15.0"
    implementation("io.ebean:ebean-postgres:$ebeanVersion")
    implementation("io.ebean:ebean-querybean:$ebeanVersion")
    implementation("io.ebean:ebean-migration:13.6.0")
    implementation("io.ebean:ebean-ddl-generator:$ebeanVersion")
    kapt("io.ebean:kotlin-querybean-generator:$ebeanVersion")
    implementation("org.postgresql:postgresql:42.6.0")

    // Needed for db migration
    testImplementation("javax.xml.bind:jaxb-api:2.3.1")
    testImplementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
    testImplementation("com.sun.xml.bind:jaxb-impl:2.3.0.1")
    testImplementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")
}

tasks.register("createMigration", JavaExec::class) {
    group = "Execution"
    description = "Create a migration using current model data"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("GenerateMigration")
}