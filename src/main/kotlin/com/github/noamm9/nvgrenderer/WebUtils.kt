package com.github.noamm9.nvgrenderer


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI

object WebUtils {
    private const val PRIVATE_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    private val SUCCESS_RANGE = 200 .. 299

    fun prepareConnection(url: String): HttpURLConnection {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent", PRIVATE_USER_AGENT)
        return connection
    }

    suspend fun downloadBytes(url: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = prepareConnection(url)
            connection.requestMethod = "GET"

            val code = connection.responseCode
            val stream = if (code in SUCCESS_RANGE) connection.inputStream else connection.errorStream

            if (code !in SUCCESS_RANGE) throw IllegalStateException("HTTP $code")

            stream.use { it.readBytes() }
        }
    }
}