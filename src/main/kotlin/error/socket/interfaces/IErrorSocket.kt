package error.socket.interfaces

import org.json.JSONArray

interface IErrorSocket {
    fun send(message: JSONArray)
    fun closeSockets()
}