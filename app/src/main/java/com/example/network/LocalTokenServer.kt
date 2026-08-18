package com.example.network

import android.util.Log
import com.example.security.SecureDataSanitizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class LocalTokenServer(
    private val port: Int = 6677,
    private val onTokenCaptured: (token: String) -> Unit,
    private val onStatusUpdate: (status: String) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    @Volatile
    var isRunning: Boolean = false
        private set

    fun start() {
        if (isRunning) return

        serverJob = scope.launch {
            try {
                serverSocket = ServerSocket().apply {
                    reuseAddress = true
                    bind(InetSocketAddress("0.0.0.0", port))
                }
                isRunning = true
                onStatusUpdate("Listening on http://127.0.0.1:$port/")

                while (isActive && isRunning) {
                    try {
                        val clientSocket = serverSocket?.accept() ?: break
                        launch(Dispatchers.IO) {
                            handleClient(clientSocket)
                        }
                    } catch (e: Exception) {
                        if (!isRunning) break
                    }
                }
            } catch (e: Exception) {
                Log.e("LocalTokenServer", SecureDataSanitizer.scrubLogMessage("Error starting server: ${e.message}"))
                onStatusUpdate("Server Error: ${SecureDataSanitizer.scrubLogMessage(e.localizedMessage)}")
                isRunning = false
            }
        }
    }

    fun stop() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null
        serverJob?.cancel()
        serverJob = null
        onStatusUpdate("Server stopped")
    }

    private fun handleClient(socket: Socket) {
        try {
            socket.use { s ->
                val input = BufferedInputStream(s.getInputStream())
                val output = s.getOutputStream()

                val headerBytes = readHeaders(input)
                val headerStr = String(headerBytes, Charsets.UTF_8)

                var contentLength = 0
                val lines = headerStr.split("\r\n")
                for (line in lines) {
                    if (line.startsWith("Content-Length:", ignoreCase = true)) {
                        contentLength = line.substringAfter(":").trim().toIntOrNull() ?: 0
                    }
                }

                val bodyBytes = if (contentLength > 0) {
                    readExactBytes(input, contentLength)
                } else {
                    readAvailableBytes(input)
                }

                val requestLine = lines.firstOrNull() ?: ""
                val path = requestLine.split(" ").getOrNull(1) ?: "/"

                if (bodyBytes.isNotEmpty()) {
                    try {
                        val decrypted = CryptoEngine.decryptAes(bodyBytes)
                        val captured = CryptoEngine.extractAccessTokenProtobuf(decrypted)
                        if (!captured.isNullOrEmpty()) {
                            SecureDataSanitizer.ensureNoDiskLeakage(captured)
                            onTokenCaptured(captured)
                        }
                    } catch (_: Exception) {
                        // In case body was plain or non-AES, also try extracting directly
                        val captured = CryptoEngine.extractAccessTokenProtobuf(bodyBytes)
                        if (!captured.isNullOrEmpty()) {
                            SecureDataSanitizer.ensureNoDiskLeakage(captured)
                            onTokenCaptured(captured)
                        }
                    }
                }

                // Match Python Flask endpoints behavior
                val (responseHeader, responseData) = when {
                    path.contains("ver.php", ignoreCase = true) -> {
                        val json = "{\"server_url\":\"http://127.0.0.1:$port/\"}"
                        val bytes = json.toByteArray(Charsets.UTF_8)
                        val h = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
                        Pair(h, bytes)
                    }
                    path.contains("Ping", ignoreCase = true) -> {
                        val json = "{\"status\":\"ok\",\"msg\":\"pong\"}"
                        val bytes = json.toByteArray(Charsets.UTF_8)
                        val h = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
                        Pair(h, bytes)
                    }
                    path.contains("MajorLogin", ignoreCase = true) || path.contains("GetLoginData", ignoreCase = true) -> {
                        val inGamePopup = "[b][c][00FFFF]\n" +
                                "╔══════════════════════════╗\n" +
                                "║  [FF1493]★ [FFFF00]A R I Y A N [FF1493]★[00FFFF]  ║\n" +
                                "╚══════════════════════════╝\n" +
                                "[FF7700]🔥 FREE FIRE BIO CHANGER 🔥\n\n" +
                                "[00FF00]━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "[FFFFFF]● [00FFFF]ACCESS TOKEN SECURED [FFFFFF]●\n" +
                                "[00FF00]━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                                "[00FFFF]──────────────────────────\n" +
                                "[FFFF00]⚡ STATUS : [00FF00]CONNECTED TO BIO STUDIO\n" +
                                "[FF007F]♥ READY TO UPDATE BIO ♥\n"
                        val bytes = inGamePopup.toByteArray(Charsets.UTF_8)
                        val h = "HTTP/1.1 500 Internal Server Error\r\nContent-Type: application/octet-stream\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
                        Pair(h, bytes)
                    }
                    else -> {
                        val json = "{\"status\":\"ok\",\"server_url\":\"http://127.0.0.1:$port/\"}"
                        val bytes = json.toByteArray(Charsets.UTF_8)
                        val h = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
                        Pair(h, bytes)
                    }
                }

                output.write(responseHeader.toByteArray(Charsets.UTF_8))
                output.write(responseData)
                output.flush()
            }
        } catch (e: Exception) {
            Log.e("LocalTokenServer", SecureDataSanitizer.scrubLogMessage("Error handling request: ${e.message}"))
        }
    }

    private fun readHeaders(input: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        var matchCount = 0
        while (true) {
            val b = input.read()
            if (b == -1) break
            buffer.write(b)
            if (b == '\r'.code) {
                if (matchCount == 0 || matchCount == 2) matchCount++ else matchCount = 1
            } else if (b == '\n'.code) {
                if (matchCount == 1 || matchCount == 3) matchCount++ else matchCount = 0
            } else {
                matchCount = 0
            }
            if (matchCount == 4) break
        }
        return buffer.toByteArray()
    }

    private fun readExactBytes(input: InputStream, length: Int): ByteArray {
        val buffer = ByteArray(length)
        var totalRead = 0
        while (totalRead < length) {
            val read = input.read(buffer, totalRead, length - totalRead)
            if (read == -1) break
            totalRead += read
        }
        return if (totalRead == length) buffer else buffer.copyOf(totalRead)
    }

    private fun readAvailableBytes(input: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        val temp = ByteArray(1024)
        while (input.available() > 0) {
            val read = input.read(temp)
            if (read == -1) break
            buffer.write(temp, 0, read)
        }
        return buffer.toByteArray()
    }
}
