plugins {
    `java-library`
    id("com.google.protobuf")
}

dependencies {
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-protobuf:1.55.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.22.2")
    implementation("io.grpc:grpc-netty-shaded:1.55.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.55.1"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("$projectDir/../protobufs")
        }
    }
}