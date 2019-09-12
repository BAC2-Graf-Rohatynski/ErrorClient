package error.socket

import apibuilder.error.interfaces.IErrorItem
import apibuilder.error.response.ResponseItem
import error.queue.MessageQueue
import error.socket.interfaces.IErrorSocketHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ErrorSocketHandler: IErrorSocketHandler {
    private lateinit var errorSocket: ErrorSocket
    private val logger: Logger = LoggerFactory.getLogger(ErrorSocketHandler::class.java)

    init {
        connect()
    }

    @Synchronized
    override fun send(message: IErrorItem, withResponse: Boolean): ResponseItem {
        try {
            return if (::errorSocket.isInitialized) {
                errorSocket.send(message = message.toJson())
                if (withResponse) MessageQueue.takeFromQueue(requestId = message.getRequestId()) else ResponseItem()
            } else {
                throw Exception("Socket isn't initialized! Message '$message' cannot be send!")
            }
        } catch (ex: Exception) {
            throw Exception("Error while waiting for response!\n${ex.message}")
        }
    }

    @Synchronized
    override fun closeSockets() {
        if (::errorSocket.isInitialized) {
            errorSocket.closeSockets()
        }
    }

    private fun connect() {
        try {
            logger.info("Connecting ...")
            errorSocket = ErrorSocket()
            logger.info("Connected")
        } catch (ex: Exception) {
            logger.error("Error occurred while connecting!\n${ex.message}")
        }
    }
}