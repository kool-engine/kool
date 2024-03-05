package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual suspend fun InMemoryFileSystem.toZip(): Uint8Buffer {
    val bos = ByteArrayOutputStream()
    ZipOutputStream(bos).use { zipOut ->
        listAll().filterIsInstance<FileSystemFile>().forEach { file ->
            val entry = ZipEntry(file.path.removePrefix("/"))
            entry.size = file.size
            zipOut.putNextEntry(entry)
            zipOut.write(file.read().toArray())
        }
    }
    return Uint8BufferImpl(bos.toByteArray())
}