package error.client

import apibuilder.error.*
import apibuilder.error.response.ResponseItem
import error.client.interfaces.IErrorClient
import error.socket.ErrorSocketHandler
import enumstorage.language.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ErrorClient: IErrorClient {
    private val logger: Logger = LoggerFactory.getLogger(ErrorClient::class.java)

    @Synchronized
    override fun setLanguage(language: Language) {
        try {
            val message = SetLanguageItem().create(language = language)
            ErrorSocketHandler.send(message = message)
        } catch (ex: Exception) {
            logger.error("Error while setting language!\n${ex.message}")
        }
    }

    @Synchronized
    override fun deleteMessage(code: Int) {
        try {
            val message = DeleteMessageItem().create(code = code)
            ErrorSocketHandler.send(message = message)
        } catch (ex: Exception) {
            logger.error("Error while deleting item!\n${ex.message}")
        }
    }

    @Synchronized
    override fun deleteAllMessages() {
        try {
            val message = DeleteAllMessagesItem().create()
            ErrorSocketHandler.send(message = message)
        } catch (ex: Exception) {
            logger.error("Error while deleting all items!\n${ex.message}")
        }
    }

    @Synchronized
    override fun getAllMessages(): ResponseItem {
        return try {
            val message = GetAllMessagesItem().create()
            ErrorSocketHandler.send(message = message, withResponse = true)
        } catch (ex: Exception) {
            throw Exception("Error while getting all items!\n${ex.message}")
        }
    }

    @Synchronized
    override fun sendMessage(code: Int, ssid: Int, enabled: Boolean) {
        try {
            val message = SendMessageItem().create(code = code, ssid = ssid, enabled = enabled, timestamp = System.currentTimeMillis())
            ErrorSocketHandler.send(message = message)
        } catch (ex: Exception) {
            logger.error("Error while sending item!\n${ex.message}")
        }
    }
}