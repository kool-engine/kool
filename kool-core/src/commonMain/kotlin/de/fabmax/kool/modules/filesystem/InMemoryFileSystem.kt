package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.Uint8Buffer

class InMemoryFileSystem : WritableFileSystem {

    override val root: FileSystemDirectory = Directory("/")
    private val fsItems = mutableMapOf<String, InMemoryItem>("/" to root as Directory)

    private val listeners = BufferedList<FileSystemWatcher>()

    override fun listAll(): List<FileSystemItem> = fsItems.values.toList().sortedBy { it.path }

    override fun get(path: String): FileSystemItem {
        val sanitized = FileSystem.sanitizePath(path)
        return checkNotNull(fsItems[sanitized]) { "File not found: $sanitized" }
    }

    override fun contains(path: String): Boolean = FileSystem.sanitizePath(path) in fsItems

    override fun addFileSystemWatcher(listener: FileSystemWatcher) {
        listeners += listener
    }

    override fun removeFileSystemWatcher(listener: FileSystemWatcher) {
        listeners -= listener
    }

    override fun createDirectory(path: String): WritableFileSystemDirectory {
        val dirPath = FileSystem.sanitizeDirPath(path)
        check(dirPath !in this) { "directory already exists: $dirPath" }

        val parentPath = FileSystem.parentPath(dirPath)
        val name = dirPath.removeSuffix("/").substringAfterLast('/')
        check(name.isNotBlank()) { "invalid directory path: $dirPath" }

        val parentDir = get(parentPath) as Directory
        val dir = Directory(dirPath)
        fsItems[dirPath] = dir
        parentDir.items[dir.name] = dir

        listeners.updated().forEach {
            it.onDirectoryCreated(dir)
            it.onDirectoryChanged(parentDir)
        }
        return dir
    }

    override suspend fun createFile(path: String, data: Uint8Buffer): WritableFileSystemFile {
        val filePath = FileSystem.sanitizePath(path)
        check(filePath !in this) { "file already exists: $filePath" }

        val parentPath = FileSystem.parentPath(filePath)
        val name = filePath.substringAfterLast('/')
        check(name.isNotBlank()) { "invalid file path: $filePath" }

        val parentDir = get(parentPath) as Directory
        val file = File(filePath, data)
        fsItems[filePath] = file
        parentDir.items[file.name] = file

        listeners.updated().forEach {
            it.onFileCreated(file)
            it.onDirectoryChanged(parentDir)
        }
        return file
    }

    private sealed class InMemoryItem: FileSystemItem {
        abstract fun delete(isParentDelete: Boolean)
    }

    private inner class Directory(override val path: String) : InMemoryItem(), WritableFileSystemDirectory {
        override val parent: FileSystemDirectory?
            get() = this@InMemoryFileSystem.getDirectoryOrNull(FileSystem.parentPath(path))

        val items = mutableMapOf<String, InMemoryItem>()

        override fun list(): List<InMemoryItem> = items.values.toList().sortedBy { it.path }

        override fun getChildOrNull(name: String): InMemoryItem? = items[name]
        override fun contains(name: String): Boolean = name in items

        override fun delete() {
            delete(false)
        }

        override fun delete(isParentDelete: Boolean) {
            check(this != root) { "root directory cannot be deleted" }
            list().forEach { it.delete(true) }
            fsItems.remove(path)

            val parent = this@InMemoryFileSystem[FileSystem.parentPath(path)] as Directory
            listeners.updated().forEach {
                it.onDirectoryDeleted(this)
                if (!isParentDelete) {
                    it.onDirectoryChanged(parent)
                }
            }
        }
    }

    private inner class File(override val path: String, var data: Uint8Buffer) : InMemoryItem(), WritableFileSystemFile {
        override val parent: FileSystemDirectory?
            get() = this@InMemoryFileSystem.getDirectoryOrNull(FileSystem.parentPath(path))

        override val size: Long
            get() = data.capacity.toLong()

        override suspend fun read(): Uint8Buffer = data

        override suspend fun write(data: Uint8Buffer) {
            this.data = data
            listeners.updated().forEach { it.onFileChanged(this) }
        }

        override fun delete() {
            delete(false)
        }

        override fun delete(isParentDelete: Boolean) {
            fsItems.remove(path)

            val parent = this@InMemoryFileSystem[FileSystem.parentPath(path)] as Directory
            listeners.updated().forEach {
                it.onFileDeleted(this)
                if (!isParentDelete) {
                    it.onDirectoryChanged(parent)
                }
            }
        }
    }
}

suspend fun InMemoryFileSystem(copyFrom: FileSystem): InMemoryFileSystem {
    val fs = InMemoryFileSystem()
    copyFrom.listAll().filter { it.path != "/" }.forEach {
        when (it) {
            is FileSystemDirectory -> fs.createDirectory(it.path)
            is FileSystemFile -> fs.createFile(it.path, it.read())
        }
    }
    return fs
}

expect suspend fun InMemoryFileSystem.toZip(): Uint8Buffer
