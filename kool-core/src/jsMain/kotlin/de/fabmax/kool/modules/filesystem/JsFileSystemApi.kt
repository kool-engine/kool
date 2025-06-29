package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.FileFilterItem
import org.khronos.webgl.Uint8Array
import org.w3c.files.File
import kotlin.js.Promise

external fun showSaveFilePicker(): Promise<FileSystemFileHandle>
external fun showSaveFilePicker(options: FilePickerOptions): Promise<FileSystemFileHandle>

interface FilePickerOptions

fun FilePickerOptions(defaultFileName: String?, filterList: List<FileFilterItem>): FilePickerOptions {
    val opts = js("({})")
    defaultFileName?.let { opts["suggestedName"] = it }

    val types = js("[]")
    opts["types"] = types
    filterList.forEach {
        val type = js("({})")
        type["description"] = it.name
        val accept = js("({})")
        accept[it.mimeType.value] = it.fileExtensions.toTypedArray()
        type["accept"] = accept
        types.push(type)
        Unit
    }
    return opts.unsafeCast<FilePickerOptions>()
}

external interface FileSystemFileHandle {
    fun getFile(): Promise<File>
    fun createWritable(): Promise<FileSystemWritableFileStream>
}

external interface WritableStream {
    val locked: Boolean

    fun abort(reason: String): Promise<String>
    fun close(): Promise<dynamic>
}

external interface FileSystemWritableFileStream : WritableStream {
    fun write(data: Uint8Array): Promise<dynamic>
    fun seek(position: Long): Promise<dynamic>
    fun truncate(position: Long): Promise<dynamic>
}