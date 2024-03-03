package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.fetch
import de.fabmax.kool.platform.JsZip
import de.fabmax.kool.platform.ZipObject
import de.fabmax.kool.platform.asyncU8
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await

actual suspend fun zipFileSystem(path: String): FileSystem {
    val zipFetch = fetch(path).await()
    check(zipFetch.ok)
    val zipBlob = zipFetch.blob().await()
    val zip = JsZip().loadAsync(zipBlob).await()
    return ZipFileSytem(zip)
}

class ZipFileSytem(zip: JsZip) : FileSystem {

    override val root: FileSystemDirectory = Directory("/")
    private val fsItems = mutableMapOf<String, FileSystemItem>("/" to root)

    init {
        zip.forEach { relativePath, zipObj ->
            if (!zipObj.dir) {
                var path = relativePath
                if (!path.startsWith("/")) {
                    path = "/$path"
                }
                val parent = getParentDir(path)
                val file = File(path, zipObj)
                fsItems[file.path] = file
                parent.items[file.name] = file
            }
        }
    }

    private fun getParentDir(path: String): Directory {
        return path.substringBeforeLast("/").split("/")
            .filter { it.isNotEmpty() }
            .fold(root as Directory) { parent, name ->
                parent.items.getOrPut("$name/") {
                    val newDir = Directory("${parent.path}${name}/")
                    fsItems[newDir.path] = newDir
                    newDir
                } as Directory
            }
    }

    override fun listAll(): List<FileSystemItem> = fsItems.values.toList().sortedBy { it.path }

    override fun get(path: String): FileSystemItem = checkNotNull(fsItems[path]) { "File not found: $path" }

    private class Directory(override val path: String): FileSystemDirectory {
        val items = mutableMapOf<String, FileSystemItem>()

        override fun list(): List<FileSystemItem> = items.values.toList().sortedBy { it.path }

        override fun get(name: String): FileSystemItem = checkNotNull(items[name]) { "File not found: $name" }
    }

    private inner class File(override val path: String, val zipEntry: ZipObject): FileSystemFile {
        override val size: Long
            get() = 0L

        override suspend fun read(): Uint8Buffer {
            val data = zipEntry.asyncU8().await()
            return Uint8BufferImpl(data)
        }
    }
}
