package com.example.network

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

data class MajorLoginResponse(
    val accountUid: Long,
    val region: String,
    val token: String,
    val url: String,
    val timestamp: Long
)

object CryptoEngine {

    val STATIC_KEY = byteArrayOf(
        89, 103, 38, 116, 99, 37, 68, 69, 117, 104, 54, 37, 90, 99, 94, 56
    )

    val STATIC_IV = byteArrayOf(
        54, 111, 121, 90, 68, 114, 50, 50, 69, 51, 121, 99, 104, 106, 77, 37
    )

    const val CLIENT_VERSION = "1.126.4"

    fun encryptAes(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(STATIC_KEY, "AES")
        val ivSpec = IvParameterSpec(STATIC_IV)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    fun decryptAes(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(STATIC_KEY, "AES")
        val ivSpec = IvParameterSpec(STATIC_IV)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    /**
     * Extracts Access Token from Protobuf payload by prioritizing field 29,
     * with regex candidate fallback matching the Python extraction logic.
     */
    fun extractAccessTokenProtobuf(data: ByteArray): String? {
        var offset = 0
        val length = data.size

        while (offset < length) {
            try {
                val (tag, newOffset1) = readVarintFrom(data, offset)
                offset = newOffset1
                val wireType = (tag and 7).toInt()
                val fieldNum = (tag ushr 3).toInt()

                when (wireType) {
                    0 -> {
                        val (_, newOffset) = readVarintFrom(data, offset)
                        offset = newOffset
                    }
                    2 -> {
                        val (l, newOffset) = readVarintFrom(data, offset)
                        offset = newOffset
                        val lenInt = l.toInt()
                        if (offset + lenInt <= length) {
                            val valBytes = data.copyOfRange(offset, offset + lenInt)
                            offset += lenInt

                            // Field 29 = access_token
                            if (fieldNum == 29) {
                                val token = String(valBytes, Charsets.UTF_8).trim()
                                if (token.isNotEmpty()) {
                                    return token
                                }
                            }
                        } else {
                            break
                        }
                    }
                    1 -> {
                        offset += 8
                    }
                    5 -> {
                        offset += 4
                    }
                    else -> break
                }
            } catch (_: Exception) {
                break
            }
        }

        // Fallback regex check
        val candidates = extractTokensFromBytes(data)
        return candidates.firstOrNull()
    }

    private fun readVarintFrom(data: ByteArray, startOffset: Int): Pair<Long, Int> {
        var res = 0L
        var shift = 0
        var offset = startOffset
        while (offset < data.size) {
            val b = data[offset].toInt() and 0xFF
            offset++
            res = res or ((b and 0x7F).toLong() shl shift)
            if ((b and 0x80) == 0) break
            shift += 7
        }
        return Pair(res, offset)
    }

    /**
     * Extracts potential access tokens from decrypted byte stream using regex.
     */
    fun extractTokensFromBytes(bytes: ByteArray): List<String> {
        val result = mutableListOf<String>()
        try {
            val text = String(bytes, Charsets.ISO_8859_1)
            val pattern = Pattern.compile("[a-zA-Z0-9_\\-\\.]{50,512}")
            val matcher = pattern.matcher(text)
            val seen = mutableSetOf<String>()

            while (matcher.find()) {
                val token = matcher.group()
                if (!seen.contains(token)) {
                    if (!token.startsWith("http") &&
                        !token.contains("Handheld") &&
                        !token.contains("Android") &&
                        !token.contains("Adreno")
                    ) {
                        result.add(token)
                        seen.add(token)
                    }
                }
            }
        } catch (_: Exception) {}
        return result
    }

    // Helper for Protobuf Varint encoding
    private fun writeVarint(out: ByteArrayOutputStream, value: Long) {
        var v = value
        while (v and 0x7FL.inv() != 0L) {
            out.write(((v and 0x7F) or 0x80).toInt())
            v = v ushr 7
        }
        out.write((v and 0x7F).toInt())
    }

    private fun writeTag(out: ByteArrayOutputStream, fieldNumber: Int, wireType: Int) {
        writeVarint(out, ((fieldNumber shl 3) or wireType).toLong())
    }

    private fun writeString(out: ByteArrayOutputStream, fieldNumber: Int, value: String) {
        if (value.isEmpty()) return
        val bytes = value.toByteArray(Charsets.UTF_8)
        writeTag(out, fieldNumber, 2)
        writeVarint(out, bytes.size.toLong())
        out.write(bytes)
    }

    private fun writeBytes(out: ByteArrayOutputStream, fieldNumber: Int, bytes: ByteArray) {
        if (bytes.isEmpty()) return
        writeTag(out, fieldNumber, 2)
        writeVarint(out, bytes.size.toLong())
        out.write(bytes)
    }

    private fun writeInt32(out: ByteArrayOutputStream, fieldNumber: Int, value: Int) {
        if (value == 0) return
        writeTag(out, fieldNumber, 0)
        writeVarint(out, value.toLong())
    }

    private fun writeUInt32(out: ByteArrayOutputStream, fieldNumber: Int, value: Long) {
        if (value == 0L) return
        writeTag(out, fieldNumber, 0)
        writeVarint(out, value)
    }

    private fun writeUInt64(out: ByteArrayOutputStream, fieldNumber: Int, value: Long) {
        if (value == 0L) return
        writeTag(out, fieldNumber, 0)
        writeVarint(out, value)
    }

    private fun writeEmbedded(out: ByteArrayOutputStream, fieldNumber: Int, block: (ByteArrayOutputStream) -> Unit) {
        val childOut = ByteArrayOutputStream()
        block(childOut)
        val childBytes = childOut.toByteArray()
        writeTag(out, fieldNumber, 2)
        writeVarint(out, childBytes.size.toLong())
        out.write(childBytes)
    }

    /**
     * Builds and encrypts the MajorLogin protobuf message.
     */
    fun buildMajorLoginPayload(token: String, openId: String): ByteArray {
        val out = ByteArrayOutputStream()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val currentTime = sdf.format(Date())
        val deviceId = "Google|" + UUID.randomUUID().toString().replace("-", "").take(16)

        // Fields according to MajorLogin protobuf definition:
        writeString(out, 3, currentTime)
        writeString(out, 4, "free fire")
        writeInt32(out, 5, 2)
        writeString(out, 7, CLIENT_VERSION)
        writeString(out, 8, "Android OS 11 / API-30 (RQ3A.210805.001)")
        writeString(out, 9, "Handheld")
        writeString(out, 10, "Verizon")
        writeString(out, 11, "WIFI")
        writeUInt32(out, 12, 1080L)
        writeUInt32(out, 13, 2400L)
        writeString(out, 14, "440")
        writeString(out, 15, "ARMv8")
        writeUInt32(out, 16, 6144L)
        writeString(out, 17, "Adreno (TM) 650")
        writeString(out, 18, "OpenGL ES 3.2 V@1.50")
        writeString(out, 19, deviceId)
        writeString(out, 20, "")
        writeString(out, 21, "en")
        writeString(out, 22, openId)
        writeString(out, 23, "4")
        writeString(out, 24, "Handheld")

        // field 25: memory_available (GameSecurity: version=55, hidden_value=81)
        writeEmbedded(out, 25) { child ->
            writeInt32(child, 6, 55)
            writeUInt64(child, 8, 81L)
        }

        writeString(out, 29, token)
        writeInt32(out, 30, 2)
        writeString(out, 41, "Verizon")
        writeString(out, 42, "WIFI")
        writeString(out, 57, "7428b253defc164018c604a1ebbfebdf")
        writeInt32(out, 60, 128512)
        writeInt32(out, 61, 42000)
        writeInt32(out, 62, 110731)
        writeInt32(out, 63, 25000)
        writeInt32(out, 64, 22000)
        writeInt32(out, 65, 26628)
        writeInt32(out, 66, 50000)
        writeInt32(out, 67, 119234)
        writeInt32(out, 73, 3)
        writeString(out, 74, "/data/app/~~random/base.apk")
        writeInt32(out, 76, 1)
        writeString(out, 77, "hash|base.apk")
        writeInt32(out, 78, 3)
        writeInt32(out, 79, 2)
        writeString(out, 81, "64")
        writeString(out, 83, "2024010012")
        writeString(out, 86, "OpenGLES3")
        writeUInt32(out, 87, 16383L)
        writeInt32(out, 88, 4)

        try {
            val analyticsBytes = Base64.decode("FwQVTgUPX1UaUllDDwcWCRBpWAUOUgsvA1snWlBaO1kFYg==", Base64.DEFAULT)
            writeBytes(out, 89, analyticsBytes)
        } catch (_: Exception) {}

        writeUInt32(out, 92, 13564L)
        writeString(out, 93, "android")
        writeString(out, 94, "")
        writeUInt32(out, 95, 110009L)
        writeInt32(out, 97, 1)
        writeInt32(out, 98, 0)
        writeString(out, 99, "4")
        writeString(out, 100, "4")

        val rawProtobuf = out.toByteArray()
        return encryptAes(rawProtobuf)
    }

    /**
     * Parses the MajorLoginRes protobuf response bytes.
     */
    fun parseMajorLoginResponse(bytes: ByteArray): MajorLoginResponse? {
        try {
            var offset = 0
            var accountUid = 0L
            var region = ""
            var token = ""
            var url = ""
            var timestamp = 0L

            while (offset < bytes.size) {
                // Read Varint Tag
                var tag = 0L
                var shift = 0
                while (offset < bytes.size) {
                    val b = bytes[offset++].toInt() and 0xFF
                    tag = tag or ((b and 0x7F).toLong() shl shift)
                    if ((b and 0x80) == 0) break
                    shift += 7
                }

                val wireType = (tag and 0x07).toInt()
                val fieldNumber = (tag ushr 3).toInt()

                when (wireType) {
                    0 -> { // Varint
                        var value = 0L
                        var valShift = 0
                        while (offset < bytes.size) {
                            val b = bytes[offset++].toInt() and 0xFF
                            value = value or ((b and 0x7F).toLong() shl valShift)
                            if ((b and 0x80) == 0) break
                            valShift += 7
                        }
                        if (fieldNumber == 1) accountUid = value
                        if (fieldNumber == 21) timestamp = value
                    }
                    1 -> { // 64-bit
                        offset += 8
                    }
                    2 -> { // Length-delimited
                        var len = 0L
                        var lenShift = 0
                        while (offset < bytes.size) {
                            val b = bytes[offset++].toInt() and 0xFF
                            len = len or ((b and 0x7F).toLong() shl lenShift)
                            if ((b and 0x80) == 0) break
                            lenShift += 7
                        }
                        val length = len.toInt()
                        val strBytes = bytes.copyOfRange(offset, offset + length)
                        offset += length

                        when (fieldNumber) {
                            2 -> region = String(strBytes, Charsets.UTF_8)
                            8 -> token = String(strBytes, Charsets.UTF_8)
                            10 -> url = String(strBytes, Charsets.UTF_8)
                        }
                    }
                    5 -> { // 32-bit
                        offset += 4
                    }
                    else -> break
                }
            }

            if (token.isNotEmpty() || url.isNotEmpty()) {
                return MajorLoginResponse(accountUid, region, token, url, timestamp)
            }
        } catch (_: Exception) {}
        return null
    }

    /**
     * Serializes BioData and encrypts it for /UpdateSocialBasicInfo.
     */
    fun buildBioDataPayload(bioText: String): ByteArray {
        val out = ByteArrayOutputStream()

        // field 2: int32 = 17
        writeInt32(out, 2, 17)

        // field 5: EmptyMessage (length 0)
        writeEmbedded(out, 5) { /* empty */ }

        // field 6: EmptyMessage (length 0)
        writeEmbedded(out, 6) { /* empty */ }

        // field 8: string bio_text
        writeString(out, 8, bioText)

        // field 9: int32 = 1
        writeInt32(out, 9, 1)

        // field 11: EmptyMessage
        writeEmbedded(out, 11) { /* empty */ }

        // field 12: EmptyMessage
        writeEmbedded(out, 12) { /* empty */ }

        val raw = out.toByteArray()
        return encryptAes(raw)
    }
}
