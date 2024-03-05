package de.fabmax.kool.platform

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import org.w3c.files.File
import org.w3c.files.FilePropertyBag

@JsModule("file-saver")
@JsNonModule
external object FileSaver {
    fun saveAs(file: File)
}

fun FileSaver.saveAs(data: Uint8Buffer, name: String, mimeType: String = "") {
    val file = File(arrayOf((data as Uint8BufferImpl).buffer), name, FilePropertyBag(type = mimeType))
    saveAs(file)
}
