package error.socket.interfaces

import apibuilder.error.interfaces.IErrorItem
import apibuilder.error.response.ResponseItem

interface IErrorSocketHandler {
    fun send(message: IErrorItem, withResponse: Boolean = false): ResponseItem
    fun closeSockets()
}