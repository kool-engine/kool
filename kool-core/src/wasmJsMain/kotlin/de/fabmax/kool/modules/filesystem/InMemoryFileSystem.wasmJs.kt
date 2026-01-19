package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.platform.JsZip
import de.fabmax.kool.platform.generate
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl

actual suspend fun InMemoryFileSystem.toZip(): Uint8Buffer {
    val zip = JsZip()
    listAll().filterIsInstance<FileSystemFile>().forEach { file ->
        val data = (file.read() as Uint8BufferImpl).buffer
        zip.file(file.path.removePrefix("/"), data)
    }
    return zip.generate()
}