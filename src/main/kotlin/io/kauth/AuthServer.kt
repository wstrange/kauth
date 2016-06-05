package io.kauth

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver;



/**
 * Created by warren on 2016-06-04.
 */

class AuthServer {
    val logger = logger()

    val port = 50051;
    lateinit var server: Server

    fun start() {
        server = ServerBuilder.forPort(port)
                .addService(AuthImpl())
                .build()
                .start()
        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(Thread() {

            fun run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                this.stop();
                System.err.println("*** server shut down");
            }
        })
    }

    fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

        inner class AuthImpl:AuthGrpc.AbstractAuth() {
        override  fun authorize(req:AuthRequest, responseObserver:StreamObserver<AuthReply>) {
            val reply = AuthReply.newBuilder().setMessage("Hello " + req.url).build()
            responseObserver.onNext(reply)
            responseObserver.onCompleted()
        }
    }


    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val server = AuthServer()
            server.start()
            server.blockUntilShutdown()
        }
    }
}