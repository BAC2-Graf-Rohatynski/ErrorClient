package error.client.interfaces

import apibuilder.error.response.ResponseItem
import enumstorage.language.Language

interface IErrorClient {
    fun setLanguage(language: Language)
    fun sendMessage(code: Int, ssid: Int = 0, enabled: Boolean)
    fun deleteMessage(code: Int)
    fun deleteAllMessages()
    fun getAllMessages(): ResponseItem
}