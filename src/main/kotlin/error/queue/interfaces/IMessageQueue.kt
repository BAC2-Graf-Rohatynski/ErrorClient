package error.queue.interfaces

import apibuilder.error.response.ResponseItem
import org.json.JSONArray

interface IMessageQueue {
    fun putIntoQueue(message: JSONArray)
    fun takeFromQueue(requestId: Int): ResponseItem
}