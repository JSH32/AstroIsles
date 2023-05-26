package com.github.jsh32.astroisles.orbit

import com.github.jsh32.astroisles.orbit.services.FriendService
import io.grpc.ServerBuilder

fun main() {
    val port = 3000

    val server = ServerBuilder
        .forPort(port)
        .intercept(LoggingInterceptor())
        .addService(FriendService())
        .build()

    server.start()
    println("Server started, listening on $port")
    Runtime.getRuntime().addShutdownHook(
        Thread {
            println("*** shutting down gRPC server since JVM is shutting down")
            server.shutdown()
            println("*** server shut down")
        }
    )

    server.awaitTermination()
}
