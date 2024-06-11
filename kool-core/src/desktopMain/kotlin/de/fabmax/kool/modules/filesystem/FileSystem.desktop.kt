package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.toBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

actual suspend fun zipFileSystem(path: String): FileSystem = ZipFileSytem(path)

actual suspend fun zipFileSystem(zipData: Uint8Buffer): FileSystem {
    val memFs = InMemoryFileSystem()
    ZipInputStream(ByteArrayInputStream(zipData.toArray())).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                val path = FileSystem.sanitizePath(entry.name)
                memFs.getOrCreateDirectories(FileSystem.parentPath(path))
                memFs.createFile(path, Uint8BufferImpl(zip.readAllBytes()))
            }
            zip.closeEntry()
            entry = zip.nextEntry
        }
    }
    return memFs
}

class ZipFileSytem(zipPath: String) : FileSystem {
    val zipFile = ZipFile(zipPath)

    override val root: FileSystemDirectory = Directory("/")
    private val fsItems = mutableMapOf<String, FileSystemItem>("/" to root)

    init {
        zipFile.entries().asSequence().filter { !it.isDirectory }.forEach { entry ->
            val path = FileSystem.sanitizePath(entry.name)
            makeParentDirs(path)
            val file = File(path, entry)
            fsItems[file.path] = file
        }
        fsItems.values.filter { it != root }.forEach { (it.parent as Directory).items[it.name] = it }
    }

    private fun makeParentDirs(path: String) {
        path.substringBeforeLast("/").split("/")
            .filter { it.isNotEmpty() }
            .fold(root as Directory) { parent, name ->
                parent.items.getOrPut(name) {
                    val newDir = Directory(FileSystem.sanitizePath("${parent.path}/${name}"))
                    fsItems[newDir.path] = newDir
                    newDir
                } as Directory
            }
    }

    override fun listAll(): List<FileSystemItem> = fsItems.values.toList().sortedBy { it.path }

    override fun get(path: String): FileSystemItem {
        val sanitized = FileSystem.sanitizePath(path)
        return checkNotNull(fsItems[sanitized]) { "File not found: $sanitized" }
    }

    override fun contains(path: String): Boolean = FileSystem.sanitizePath(path) in fsItems

    override fun addFileSystemWatcher(listener: FileSystemWatcher) {
        // zip file system will never change, no need to keep any listeners around
    }

    override fun removeFileSystemWatcher(listener: FileSystemWatcher) { }

    private inner class Directory(override val path: String): FileSystemDirectory {
        override val parent: FileSystemDirectory?
            get() = this@ZipFileSytem.getDirectoryOrNull(FileSystem.parentPath(path))

        val items = mutableMapOf<String, FileSystemItem>()

        override fun list(): List<FileSystemItem> = items.values.toList().sortedBy { it.path }

        override fun getChildOrNull(name: String): FileSystemItem? = items[name]
        override fun contains(name: String): Boolean = name in items
    }

    private inner class File(override val path: String, val zipEntry: ZipEntry): FileSystemFile {
        override val parent: FileSystemDirectory?
            get() = this@ZipFileSytem.getDirectoryOrNull(FileSystem.parentPath(path))

        override val size: Long
            get() = zipEntry.size

        override suspend fun read(): Uint8Buffer {
            return withContext(Dispatchers.IO) {
                zipFile.getInputStream(zipEntry).use { it.readAllBytes() }.toBuffer()
            }
        }
    }
}
