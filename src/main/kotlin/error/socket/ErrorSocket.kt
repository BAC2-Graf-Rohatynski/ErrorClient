package error.socket

import apibuilder.json.Json
import error.ErrorClientRunner
import error.socket.interfaces.IErrorSocket
import error.queue.MessageQueue
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket
import kotlin.concurrent.thread

class ErrorSocket: IErrorSocket {
    private lateinit var clientSocket: Socket
    private lateinit var bufferedReader: BufferedReader
    private lateinit var printWriter: PrintWriter
    private val logger: Logger = LoggerFactory.getLogger(ErrorSocket::class.java)

    init {
        try {
            openSockets()
            receive()
        } catch (ex: Exception) {
            logger.error("Error socket failure while running socket!\n${ex.message}")
            closeSockets()
        }
    }

    private fun openSockets() {
        logger.info("Opening sockets ...")
        clientSocket = Socket("127.0.0.1", PortProperties.getErrorPort())
        bufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        printWriter = PrintWriter(clientSocket.getOutputStream(), true)
        logger.info("Sockets opened")
    }

    private fun receive() {
        logger.info("Hearing for messages ...")

        thread {
            bufferedReader.use {
                while (ErrorClientRunner.isRunnable()) {
                    try {
                        val inputLine = bufferedReader.readLine()

                        if (inputLine != null) {
                            val message = JSONArray(inputLine)
                            logger.info("Message '$message' received")
                            MessageQueue.putIntoQueue(message = message)
                        }
                    } catch (ex: Exception) {
                        logger.error("Error occurred while parsing message!\n${ex.message}")
                    }
                }
            }
        }
    }

    @Synchronized
    override fun send(message: JSONArray) {
        try {
            if (::printWriter.isInitialized) {
                printWriter.println(message)
                logger.info("Message '$message' sent")
            } else {
                throw Exception("Print writer isn't initialized yet!")
            }
        } catch (ex: Exception) {
            logger.error("Error occurred while sending message!\n${ex.message}")
        }
    }

    @Synchronized
    override fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (::printWriter.isInitialized) {
                printWriter.close()
            }

            if (::bufferedReader.isInitialized) {
                bufferedReader.close()
            }

            if (::clientSocket.isInitialized) {
                clientSocket.close()
            }

            logger.info("Sockets closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing sockets!\n${ex.message}")
        }
    }
}