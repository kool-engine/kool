package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.toBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LoadableFileImpl(val file: File, val selectionPath: String = file.name) : LoadableFile {

    override val name: String
        get() = file.name

    override val size: Long
        get() = file.length()

    override val mimeType: MimeType
        get() = MimeType.forFileName(name)

    override suspend fun read(): Uint8Buffer {
        return withContext(Dispatchers.IO) {
            file.readBytes().toBuffer()
        }
    }

    override fun toString(): String {
        return "$selectionPath [${(size / (1024.0 * 1024.0)).toString(1)} mb / ${mimeType.value}]"
    }
}