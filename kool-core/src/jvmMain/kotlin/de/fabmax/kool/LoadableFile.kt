package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.toBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class LoadableFile(val file: File, val selectionPath: String = file.name) {

    actual val name: String
        get() = file.name

    actual val size: Long
        get() = file.length()

    actual val mimeType: String
        get() = "application/octet-stream"

    actual suspend fun read(): Uint8Buffer {
        return withContext(Dispatchers.IO) {
            file.readBytes().toBuffer()
        }
    }

    override fun toString(): String {
        return "$selectionPath [${(size / (1024.0 * 1024.0)).toString(1)} mb / $mimeType]"
    }
}