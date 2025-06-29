package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.files.File
import kotlin.js.Promise

class LoadableFileImpl(val file: File) : LoadableFile {
    override val name: String
        get() = file.name
    override val size: Long
        get() = file.size.toLong()
    override val mimeType: MimeType
        get() = MimeType(file.type)

    override suspend fun read(): Uint8Buffer {
        val data = file.asDynamic().arrayBuffer() as Promise<ArrayBuffer>
        return Uint8BufferImpl(Uint8Array(data.await()))
    }

    override fun toString(): String {
        return "$name [${(size / (1024.0 * 1024.0)).toString(1)} mb / ${mimeType.value}]"
    }
}