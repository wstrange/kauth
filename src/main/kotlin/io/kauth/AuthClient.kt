package io.kauth

/**
 * Created by warren on 2016-06-04.
 */



import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
class AuthClient
/** Construct client connecting to HelloWorld server at {@code host:port}. */
(host:String, port:Int) {
    private val channel:ManagedChannel
    private val blockingStub:AuthGrpc.AuthBlockingStub

    init{
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build()
        blockingStub = AuthGrpc.newBlockingStub(channel)
    }

    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    /** Say hello to server. */
    fun authorize(url:String) {
        logger.info("Will try to authorize " + url + " ...")
        val request = AuthRequest.newBuilder().setUrl(url).build()
        val response:AuthReply
        try
        {
            response = blockingStub.authorize(request)
        }
        catch (e:StatusRuntimeException) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus())
            return
        }
        logger.info("Authing: " + response.getMessage())
    }
    companion object {
        private val logger = Logger.getLogger(AuthClient::class.java.name)
        /**
         * Auth server. If provided, the first element of {@code args} is the name to use in the
         * greeting.
         */
        @Throws(Exception::class)
        @JvmStatic fun main(args:Array<String>) {
            val client = AuthClient("localhost", 50051)
            try
            {
                /* Access a service running on the local machine on port 50051 */
                var url = "world"
                if (args.size > 0)
                {
                    url = args[0] /* Use the arg as the name to greet if provided */
                }
                client.authorize(url)
            }
            finally
            {
                client.shutdown()
            }
        }
    }
}
