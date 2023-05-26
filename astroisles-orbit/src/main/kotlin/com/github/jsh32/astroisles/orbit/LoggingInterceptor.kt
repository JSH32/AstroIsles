package com.github.jsh32.astroisles.orbit

import io.grpc.*
import org.slf4j.LoggerFactory

class LoggingInterceptor : ServerInterceptor {
    private val logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        logger.info("Service: ${call.methodDescriptor.serviceName}, Method: ${call.methodDescriptor.fullMethodName}")
        val wrappedServerCall = object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            override fun close(status: Status, trailers: Metadata?) {
                logger.info("Closing call with status: $status, trailers: $trailers")
                super.close(status, trailers)
            }
        }
        return next.startCall(wrappedServerCall, headers)
    }
}