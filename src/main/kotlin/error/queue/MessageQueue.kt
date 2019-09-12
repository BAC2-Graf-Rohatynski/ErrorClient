package error.queue

import apibuilder.error.response.ResponseItem
import error.queue.interfaces.IMessageQueue
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object MessageQueue: IMessageQueue {
    private val receivingQueue = mutableListOf<JSONArray>()
    private val logger: Logger = LoggerFactory.getLogger(MessageQueue::class.java)
    private var isBlocked = false

    override fun putIntoQueue(message: JSONArray) {
        try {
            logger.info("Pushing message into queue ...")
            while (isBlocked) {
                Thread.sleep(5)
            }
            isBlocked = true
            receivingQueue.add(message)
            isBlocked = false
            logger.info("Put into queue")
        } catch (ex: Exception) {
            logger.error("Queue error occurred while pushing! Clearing queue ...\n${ex.message}")
            receivingQueue.clear()
            isBlocked = false
        }
    }

    private fun getTime(): Long = System.currentTimeMillis()

    override fun takeFromQueue(requestId: Int): ResponseItem {
        val start = getTime()

        try {
            while ((getTime() - start) < 1000) {
                if (!isBlocked) {
                    if (receivingQueue.size > 0) {
                        receivingQueue.forEach { result ->
                            val responseItem = ResponseItem().create(message = result)

                            if (responseItem.getRequestId() == requestId) {
                                logger.info("Taking from queue: $result")
                                isBlocked = true
                                receivingQueue.remove(result)
                                isBlocked = false
                                return responseItem
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Queue error occurred while pulling! Clearing queue ...\n${ex.message}")
            receivingQueue.clear()
            isBlocked = false
        }

        throw Exception("Message with request ID '$requestId' not received!")
    }
}