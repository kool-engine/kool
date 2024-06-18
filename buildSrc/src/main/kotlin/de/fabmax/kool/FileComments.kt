package de.fabmax.kool

import java.io.File

fun File.comment(block: FileCommentScope.() -> Unit) {
    val commentScope = FileCommentScope(readText())
    commentScope.block()

    val commentedText = commentScope.commentedText
    if (commentedText != commentScope.text) {
        writeText(commentedText)
    }
}


class FileCommentScope(val text: String) {
    private val lines: MutableList<String> = text.lines().toMutableList()
    private val lineSep = if ("\r\n" in text) "\r\n" else "\n"

    val commentedText: String get() = lines.joinToString(lineSep)

    fun commentLines(line: String) {
        for (i in lines.indices) {
            if (lines[i].trim() == line.trim()) {
                lines[i] = "//${lines[i]}"
            }
        }
    }

    fun commentBlocks(blockStart: String) {
        var inBlock = false
        var cnt = 0
        for (i in lines.indices) {
            if (!inBlock && lines[i].trim().startsWith(blockStart.trim())) {
                inBlock = true
                cnt = 0
            }
            if (inBlock) {
                cnt += lines[i].count { it == '{' }
                cnt -= lines[i].count { it == '}' }
                lines[i] = "//${lines[i]}"
                if (cnt <= 0) {
                    inBlock = false
                }
            }
        }
    }

    fun uncommentLines(line: String) {
        for (i in lines.indices) {
            if (lines[i].startsWith("//") && lines[i].removePrefix("//").trim() == line.trim()) {
                lines[i] = lines[i].removePrefix("//")
            }
        }
    }

    fun uncommentBlocks(blockStart: String) {
        var inBlock = false
        var cnt = 0
        for (i in lines.indices) {
            val line = lines[i].removePrefix("//")
            if (!inBlock && line.trim().startsWith(blockStart.trim())) {
                inBlock = true
                cnt = 0
            }
            if (inBlock) {
                cnt += line.count { it == '{' }
                cnt -= line.count { it == '}' }
                lines[i] = line
                if (cnt <= 0) {
                    inBlock = false
                }
            }
        }
    }
}
