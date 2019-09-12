package error

import error.socket.ErrorSocketHandler
import enumstorage.update.ApplicationName
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ErrorClientRunner {
    private val logger: Logger = LoggerFactory.getLogger(ErrorClientRunner::class.java)

    @Volatile
    private var runApplication = true

    fun start() {
        logger.info("Starting application")
        ErrorSocketHandler
    }

    @Synchronized
    fun isRunnable(): Boolean = runApplication

    fun stop() {
        logger.info("Stopping application")
        runApplication = false

        ErrorSocketHandler.closeSockets()
    }

    fun getUpdateInformation(): JSONObject = UpdateInformation.getAsJson(applicationName = ApplicationName.ErrorClient.name)
}