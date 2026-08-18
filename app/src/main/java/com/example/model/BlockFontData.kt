package com.example.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Pattern

data class ColorPreset(
    val name: String,
    val hexColors: List<String>,
    val description: String
)

enum class FontStyleType(val label: String, val iconDesc: String) {
    BLOCK_3D("3D Block Classic", "█▀█"),
    BLOCK_OUTLINE("3D Shadow Outline", "▛▀▜"),
    FANCY_GOTHIC("Gothic Fraktur", "𝕬𝖗𝖞𝖆𝖓"),
    FANCY_SCRIPT("Cursive Script", "𝓐𝓻𝔂𝓪𝓷"),
    FANCY_BOLD("Cyber Sans", "𝗔𝗥𝗬𝗔𝗡"),
    FANCY_DOUBLE("Double-Struck", "𝔸𝕣𝕪𝕒𝕟"),
    FANCY_WIDE("Aesthetic Spaced", "ＡＲＹＡＮ"),
    FANCY_SMALL_CAPS("Small Caps", "ᴀʀʏᴀɴ"),
    FANCY_BUBBLE("Neon Bubble", "🅰🆁🆈🅰🅽"),
    CLAN_WINGS("Royal Clan Wings", "꧁༺ ★ ༻꧂")
}

data class BioTemplate(
    val name: String,
    val description: String,
    val templatePattern: (String) -> String
)

object BlockFontData {

    val BLOCK_FONT: Map<Char, List<String>> = mapOf(
        'A' to listOf("█▀█", "█▀█", "▀░▀"),
        'B' to listOf("█▀▄", "█▀▄", "▀▀░"),
        'C' to listOf("█▀▀", "█░░", "▀▀▀"),
        'D' to listOf("█▀▄", "█░█", "▀▀░"),
        'E' to listOf("█▀▀", "█▀▀", "▀▀▀"),
        'F' to listOf("█▀▀", "█▀▀", "▀░░"),
        'G' to listOf("█▀▀", "█░█", "▀▀▀"),
        'H' to listOf("█░█", "█▀█", "▀░▀"),
        'I' to listOf("▀█▀", "░█░", "▀▀▀"),
        'J' to listOf("░░█", "░░█", "▀▀░"),
        'K' to listOf("█░█", "█▀▄", "▀░▀"),
        'L' to listOf("█░░", "█░░", "▀▀▀"),
        'M' to listOf("█▀▄▀█", "█░▀░█", "▀░░░▀"),
        'N' to listOf("█▀█", "█░█", "▀░▀"),
        'O' to listOf("█▀█", "█░█", "▀▀▀"),
        'P' to listOf("█▀█", "█▀▀", "▀░░"),
        'Q' to listOf("█▀█", "█▄█", "▀▀▀"),
        'R' to listOf("█▀▄", "█▀▄", "▀░▀"),
        'S' to listOf("█▀▀", "▀▀█", "▀▀▀"),
        'T' to listOf("▀█▀", "░█░", "░█░"),
        'U' to listOf("█░█", "█░█", "▀▀▀"),
        'V' to listOf("█░█", "█░█", "░▀░"),
        'W' to listOf("█░░░█", "█░█░█", "▀▄▀▄▀"),
        'X' to listOf("█░█", "░█░", "▀░▀"),
        'Y' to listOf("█░█", "░█░", "░▀░"),
        'Z' to listOf("▀▀█", "░█░", "▀▀▀"),
        '0' to listOf("█▀█", "█░█", "▀▀▀"),
        '1' to listOf("░█░", "░█░", "▀▀▀"),
        '2' to listOf("▀▀█", "█▀▀", "▀▀▀"),
        '3' to listOf("▀▀█", "░▀█", "▀▀▀"),
        '4' to listOf("█░█", "▀▀█", "░░█"),
        '5' to listOf("█▀▀", "▀▀█", "▀▀▀"),
        '6' to listOf("█▀▀", "█▀█", "▀▀▀"),
        '7' to listOf("▀▀█", "░░█", "░░█"),
        '8' to listOf("█▀█", "█▀█", "▀▀▀"),
        '9' to listOf("█▀█", "▀▀█", "▀▀▀"),
        ' ' to listOf("░░░", "░░░", "░░░"),
        '-' to listOf("░░░", "▀▀▀", "░░░"),
        '.' to listOf("░░░", "░░░", "▀▀░"),
        '!' to listOf("░█░", "░█░", "░▀░"),
        '?' to listOf("▀▀█", "░█░", "░▀░")
    )

    val BLOCK_OUTLINE_FONT: Map<Char, List<String>> = mapOf(
        'A' to listOf("▛▀▜", "▌▄▐", "▌ ▐"),
        'B' to listOf("▛▀▖", "▌▀▖", "▙▄▟"),
        'C' to listOf("▛▀▀", "▌  ", "▙▄▄"),
        'D' to listOf("▛▀▖", "▌ ▐", "▙▄▟"),
        'E' to listOf("▛▀▀", "▌▀▀", "▙▄▄"),
        'F' to listOf("▛▀▀", "▌▀▀", "▙  "),
        'G' to listOf("▛▀▀", "▌ ▟", "▙▄▟"),
        'H' to listOf("▌ ▐", "▛▀▜", "▌ ▐"),
        'I' to listOf("▀▛▀", " ▌ ", "▄▙▄"),
        'J' to listOf("  ▌", "  ▌", "▙▄▟"),
        'K' to listOf("▌ ▐", "▛▀▖", "▌ ▐"),
        'L' to listOf("▌  ", "▌  ", "▙▄▄"),
        'M' to listOf("▌▀▄▀▐", "▌ ▀ ▐", "▌   ▐"),
        'N' to listOf("▛▀▜", "▌ ▐", "▌ ▐"),
        'O' to listOf("▛▀▜", "▌ ▐", "▙▄▟"),
        'P' to listOf("▛▀▜", "▛▀▀", "▙  "),
        'Q' to listOf("▛▀▜", "▌▄▐", "▙▄▟"),
        'R' to listOf("▛▀▖", "▛▀▖", "▌ ▐"),
        'S' to listOf("▛▀▀", " ▀▜", "▙▄▟"),
        'T' to listOf("▀▛▀", " ▌ ", " ▌ "),
        'U' to listOf("▌ ▐", "▌ ▐", "▙▄▟"),
        'V' to listOf("▌ ▐", "▌ ▐", " ▀ "),
        'W' to listOf("▌   ▐", "▌ ▄ ▐", "▀▄▀▄▀"),
        'X' to listOf("▌ ▐", " ▀ ", "▌ ▐"),
        'Y' to listOf("▌ ▐", " ▀ ", " ▌ "),
        'Z' to listOf("▀▀▜", " ▀ ", "▙▄▄"),
        '0' to listOf("▛▀▜", "▌ ▐", "▙▄▟"),
        '1' to listOf(" ▌ ", " ▌ ", "▙▄▟"),
        '2' to listOf("▀▀▜", "▛▀▀", "▙▄▄"),
        '3' to listOf("▀▀▜", " ▀▜", "▙▄▟"),
        '4' to listOf("▌ ▐", "▀▀▜", "  ▌"),
        '5' to listOf("▛▀▀", " ▀▜", "▙▄▟"),
        '6' to listOf("▛▀▀", "▛▀▜", "▙▄▟"),
        '7' to listOf("▀▀▜", "  ▌", "  ▌"),
        '8' to listOf("▛▀▜", "▛▀▜", "▙▄▟"),
        '9' to listOf("▛▀▜", " ▀▜", "▙▄▟"),
        ' ' to listOf("   ", "   ", "   ")
    )

    val PRESET_PALETTES = listOf(
        ColorPreset(
            name = "Cyberpunk Neo",
            hexColors = listOf("FF1493", "00FFFF", "00FF00", "FFFF00", "FF7700", "BF00FF"),
            description = "Electric Pink, Cyan & Matrix Green"
        ),
        ColorPreset(
            name = "Kawaii Sunset",
            hexColors = listOf("FF0844", "FF7700", "FFD700", "FF1493", "7F00FF"),
            description = "Neon Pink, Gold & Violet"
        ),
        ColorPreset(
            name = "Electric Glacier",
            hexColors = listOf("00F2FE", "4FACFE", "00FFFF", "7FFFD4", "00BFFF"),
            description = "Cyan, Ice Blue & Aqua"
        ),
        ColorPreset(
            name = "Matrix Code",
            hexColors = listOf("00FF66", "39FF14", "00FF00", "76FF03", "00E676"),
            description = "Deep Lime & Cyber Green"
        ),
        ColorPreset(
            name = "Gold Flame",
            hexColors = listOf("FFD700", "FFA500", "FF4500", "FFFF00", "FF8C00"),
            description = "Royal Gold, Amber & Flame"
        ),
        ColorPreset(
            name = "Crimson Blade",
            hexColors = listOf("FF0055", "FF0844", "D80032", "FF1744", "C2185B"),
            description = "Neon Red, Crimson & Rose"
        ),
        ColorPreset(
            name = "Royal Purple",
            hexColors = listOf("7F00FF", "E100FF", "8A2BE2", "9400D3", "BA55D3"),
            description = "Neon Violet & Magenta"
        ),
        ColorPreset(
            name = "Rainbow Sparkle",
            hexColors = listOf("FF0000", "FFA500", "FFFF00", "00FF00", "00FFFF", "0000FF", "FF00FF"),
            description = "Full Spectrum Rainbow"
        )
    )

    val DEFAULT_COLORS = listOf(
        "FF1493", "00FF00", "00FFFF", "FFFF00", "FF7700",
        "BF00FF", "FF0000", "00BFFF", "FFD700", "7FFFD4"
    )

    fun convertToFancyUnicode(text: String, fontType: FontStyleType): String {
        return when (fontType) {
            FontStyleType.FANCY_GOTHIC -> toGothic(text)
            FontStyleType.FANCY_SCRIPT -> toScript(text)
            FontStyleType.FANCY_BOLD -> toBoldSans(text)
            FontStyleType.FANCY_DOUBLE -> toDoubleStruck(text)
            FontStyleType.FANCY_WIDE -> toFullwidth(text)
            FontStyleType.FANCY_SMALL_CAPS -> toSmallCaps(text)
            FontStyleType.FANCY_BUBBLE -> toBubble(text)
            FontStyleType.CLAN_WINGS -> "꧁༺ ${toBoldSans(text)} ༻꧂"
            else -> text
        }
    }

    private fun toGothic(text: String): String {
        val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val fancy = "𝕬𝕭𝕮𝕯𝕰𝕱𝕲𝕳𝕴𝕵𝕶𝕷𝕸𝕹𝕺𝕻𝕼𝕽𝕾𝕿𝖀𝖁𝖂𝖃𝖄𝖅𝖆𝖇𝖈𝖉𝖊𝖋𝖌𝖍𝖎𝖏𝖐𝖑𝖒𝖓𝖔𝖕𝖖𝖗𝖘𝖙𝖚𝖛𝖜砀𝖞𝖟0123456789"
        return transformText(text, normal, fancy)
    }

    private fun toScript(text: String): String {
        val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val fancy = "𝓐𝓑𝓒𝓓𝓔𝓕𝓖𝓗𝓘𝓙𝓚𝓛𝓜𝓝𝓞𝓟𝓠𝓡𝓢𝓣𝓤𝓥𝓦𝓧𝓨𝓩𝓪𝓫𝓬𝓭𝓮𝓯𝓰𝓱𝓲𝓳𝓴𝓵𝓶𝓷𝓸𝓹𝓺𝓻𝓼𝓽𝓾𝓿𝔀𝔁𝔂𝔩"
        return transformText(text, normal, fancy)
    }

    private fun toBoldSans(text: String): String {
        val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val fancy = "𝗔𝗕𝗖𝗗𝗘𝗙𝗚𝗛𝗜𝗝𝗞𝗟𝗠𝗡𝗢𝗣𝗤𝗥𝗦𝗧𝗨𝗩𝗪𝗫𝗬𝗭𝗮𝗯𝗰𝗱𝗲𝗳𝗴𝗵𝗶𝗷𝗸𝗹𝗺𝗻𝗼𝗽𝗾𝗿𝘀𝘁𝘂𝘃𝘄𝘅𝘆𝘇𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵"
        return transformText(text, normal, fancy)
    }

    private fun toDoubleStruck(text: String): String {
        val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val fancy = "𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡"
        return transformText(text, normal, fancy)
    }

    private fun toFullwidth(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            if (c in ' '..'~') {
                sb.append((c.code + 0xFEE0).toChar())
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun toSmallCaps(text: String): String {
        val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val fancy = "ABCDEFGHIJKLMNOPQRSTUVWXYZᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ"
        return transformText(text, normal, fancy)
    }

    private fun toBubble(text: String): String {
        val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val fancy = "🅰🅱🅲🅳🅴🅵🅶🅷🅸🅹🅺🅻🅼🅽🅾🅿🆀🆁🆂🆃🆄🆅🆆🆇🆈🆉🅰🅱🅲🅳🅴🅵🅶🅷🅸🅹🅺🅻🅼🅽🅾🅿🆀🆁🆂🆃🆄🆅🆆🆇🆈🆉⓪①②③④⑤⑥⑦⑧⑨"
        return transformText(text, normal, fancy)
    }

    private fun transformText(text: String, from: String, to: String): String {
        val sb = StringBuilder()
        for (c in text) {
            val idx = from.indexOf(c)
            if (idx != -1 && idx * 2 < to.length) {
                // Unicode surrogate pairs safe
                val toCharIndex = idx * 2
                if (toCharIndex + 1 < to.length && Character.isSurrogate(to[toCharIndex])) {
                    sb.append(to.substring(toCharIndex, toCharIndex + 2))
                } else if (idx < to.length) {
                    sb.append(to[idx])
                } else {
                    sb.append(c)
                }
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * Generates a 3-line 3D block or stylized bio with Garena color tags ([B][C][HEX]).
     */
    fun generateFormattedBio(
        rawText: String,
        fontType: FontStyleType = FontStyleType.BLOCK_3D,
        chunkSize: Int = 2,
        colors: List<String> = DEFAULT_COLORS,
        includeCommunityFooter: Boolean = false
    ): String {
        val cleanText = rawText.trim()
        if (cleanText.isEmpty()) return ""

        val effectiveColors = if (colors.isNotEmpty()) colors else DEFAULT_COLORS

        val baseBio = when (fontType) {
            FontStyleType.BLOCK_3D -> generate3DBlock(cleanText.uppercase(), BLOCK_FONT, chunkSize, effectiveColors)
            FontStyleType.BLOCK_OUTLINE -> generate3DBlock(cleanText.uppercase(), BLOCK_OUTLINE_FONT, chunkSize, effectiveColors)
            else -> {
                // Fancy single/multi-line colored text
                val converted = convertToFancyUnicode(cleanText, fontType)
                generateColoredInlineText(converted, chunkSize, effectiveColors)
            }
        }

        return if (includeCommunityFooter) {
            "$baseBio\n[B][C][00F2FE]⚡ ARYAN VIP BIO | t.me/Premiume_FF_Tcp_bot_Community"
        } else {
            baseBio
        }
    }

    private fun generate3DBlock(
        cleanText: String,
        fontMap: Map<Char, List<String>>,
        chunkSize: Int,
        colors: List<String>
    ): String {
        val words = cleanText.split(" ")
        val line1Parts = mutableListOf<String>()
        val line2Parts = mutableListOf<String>()
        val line3Parts = mutableListOf<String>()

        var colorIndex = 0

        words.forEachIndexed { wIdx, word ->
            val chunks = word.chunked(chunkSize.coerceAtLeast(1))

            for (chk in chunks) {
                val color = colors[colorIndex % colors.size]
                colorIndex++

                val cL1 = mutableListOf<String>()
                val cL2 = mutableListOf<String>()
                val cL3 = mutableListOf<String>()

                for (char in chk) {
                    val fontChar = fontMap[char] ?: fontMap[' '] ?: listOf("░░░", "░░░", "░░░")
                    cL1.add(fontChar[0])
                    cL2.add(fontChar[1])
                    cL3.add(fontChar[2])
                }

                line1Parts.add("[B][C][$color]" + cL1.joinToString("░"))
                line2Parts.add("[B][C][$color]" + cL2.joinToString("░"))
                line3Parts.add("[B][C][$color]" + cL3.joinToString("░"))
            }

            if (wIdx < words.size - 1) {
                line1Parts.add("[FFFFFF]░░░")
                line2Parts.add("[FFFFFF]░░░")
                line3Parts.add("[FFFFFF]░░░")
            }
        }

        return listOf(
            line1Parts.joinToString(""),
            line2Parts.joinToString(""),
            line3Parts.joinToString("")
        ).joinToString("\n")
    }

    private fun generateColoredInlineText(
        text: String,
        chunkSize: Int,
        colors: List<String>
    ): String {
        val chunks = text.chunked(chunkSize.coerceAtLeast(1))
        val sb = StringBuilder()
        var colorIdx = 0

        sb.append("[B][C]")
        for (chunk in chunks) {
            val col = colors[colorIdx % colors.size]
            colorIdx++
            sb.append("[$col]$chunk")
        }
        return sb.toString()
    }

    /**
     * Creates custom gradient color steps between two hex colors.
     */
    fun createGradient(startHex: String, endHex: String, steps: Int = 6): List<String> {
        val s = startHex.removePrefix("#").trim()
        val e = endHex.removePrefix("#").trim()
        if (s.length != 6 || e.length != 6) return DEFAULT_COLORS

        val r1 = s.substring(0, 2).toIntOrNull(16) ?: 255
        val g1 = s.substring(2, 4).toIntOrNull(16) ?: 0
        val b1 = s.substring(4, 6).toIntOrNull(16) ?: 128

        val r2 = e.substring(0, 2).toIntOrNull(16) ?: 0
        val g2 = e.substring(2, 4).toIntOrNull(16) ?: 242
        val b2 = e.substring(4, 6).toIntOrNull(16) ?: 254

        val result = mutableListOf<String>()
        val count = steps.coerceIn(2, 12)
        for (i in 0 until count) {
            val factor = i.toFloat() / (count - 1)
            val r = (r1 + (r2 - r1) * factor).toInt().coerceIn(0, 255)
            val g = (g1 + (g2 - g1) * factor).toInt().coerceIn(0, 255)
            val b = (b1 + (b2 - b1) * factor).toInt().coerceIn(0, 255)
            result.add(String.format("%02X%02X%02X", r, g, b))
        }
        return result
    }

    /**
     * Parses Garena color tags like [FF0000], [B], [I], [U], [S], [C] and returns an AnnotatedString for rich UI rendering.
     */
    fun parseBioToAnnotatedString(bioText: String): AnnotatedString {
        return buildAnnotatedString {
            val lines = bioText.split("\n")
            lines.forEachIndexed { index, line ->
                // Matches [RRGGBB], [RGB], [b], [i], [u], [s], [c]
                val pattern = Pattern.compile("\\[([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})\\]|\\[([bBiIuUsScC])\\]")
                val matcher = pattern.matcher(line)

                var lastEnd = 0
                var currentColor: Color = Color.White
                var isBold = true
                var isItalic = false
                var isUnderline = false
                var isStrike = false

                while (matcher.find()) {
                    val textSegment = line.substring(lastEnd, matcher.start())
                    if (textSegment.isNotEmpty()) {
                        val startPos = length
                        append(textSegment)
                        
                        val textDeco = when {
                            isUnderline && isStrike -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                            isUnderline -> TextDecoration.Underline
                            isStrike -> TextDecoration.LineThrough
                            else -> TextDecoration.None
                        }
                        
                        addStyle(
                            SpanStyle(
                                color = currentColor,
                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                textDecoration = textDeco
                            ),
                            startPos,
                            length
                        )
                    }

                    val hexMatch = matcher.group(1)
                    val flagMatch = matcher.group(2)

                    if (hexMatch != null) {
                        try {
                            if (hexMatch.length == 6) {
                                val r = hexMatch.substring(0, 2).toInt(16)
                                val g = hexMatch.substring(2, 4).toInt(16)
                                val b = hexMatch.substring(4, 6).toInt(16)
                                currentColor = Color(r, g, b)
                            } else if (hexMatch.length == 3) {
                                val r = hexMatch.substring(0, 1).repeat(2).toInt(16)
                                val g = hexMatch.substring(1, 2).repeat(2).toInt(16)
                                val b = hexMatch.substring(2, 3).repeat(2).toInt(16)
                                currentColor = Color(r, g, b)
                            }
                        } catch (_: Exception) {
                            currentColor = Color.White
                        }
                    } else if (flagMatch != null) {
                        when (flagMatch.lowercase()) {
                            "b" -> isBold = true
                            "i" -> isItalic = true
                            "u" -> isUnderline = true
                            "s" -> isStrike = true
                        }
                    }

                    lastEnd = matcher.end()
                }

                if (lastEnd < line.length) {
                    val remaining = line.substring(lastEnd)
                    val startPos = length
                    append(remaining)
                    
                    val textDeco = when {
                        isUnderline && isStrike -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                        isUnderline -> TextDecoration.Underline
                        isStrike -> TextDecoration.LineThrough
                        else -> TextDecoration.None
                    }
                    
                    addStyle(
                        SpanStyle(
                            color = currentColor,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = textDeco
                        ),
                        startPos,
                        length
                    )
                }

                if (index < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }
}
