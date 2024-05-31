package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.Uint8Buffer

class InMemoryFileSystem : WritableFileSystem {

    override val root: WritableFileSystemDirectory = Directory("/")
    private val fsItems = mutableMapOf<String, InMemoryItem>("/" to root as Directory)

    private val listeners = BufferedList<FileSystemWatcher>()

    override fun listAll(): List<WritableFileSystemItem> = fsItems.values.toList().sortedBy { it.path }

    override fun get(path: String): WritableFileSystemItem {
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
        val dirPath = FileSystem.sanitizePath(path)
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
        }
        return file
    }

    override suspend fun move(sourcePath: String, destinationPath: String) {
        val dst = FileSystem.sanitizePath(destinationPath)
        val src = getItem(sourcePath)
        check(src != root) { "root directory cannot be moved" }
        check(getItemOrNull(dst) == null) { "destination path already exists" }

        val dstDir = getDirectory(FileSystem.parentPath(destinationPath)) as Directory
        val dstName = FileSystem.sanitizePath(destinationPath).substringAfterLast('/')

        when (src) {
            is File -> {
                dstDir.createFile(dstName, src.data)
                src.delete()
            }
            is Directory -> {
                val subDst = dstDir.createDirectory(dstName)
                src.list().forEach {
                    move(it.path, "${subDst.path}/${it.name}")
                }
                src.delete()
            }
        }
    }

    private sealed class InMemoryItem(override val path: String): WritableFileSystemItem

    private inner class Directory(path: String) : InMemoryItem(path), WritableFileSystemDirectory {
        override val parent: FileSystemDirectory?
            get() = this@InMemoryFileSystem.getDirectoryOrNull(FileSystem.parentPath(path))

        val items = mutableMapOf<String, InMemoryItem>()

        override fun list(): List<InMemoryItem> = items.values.toList().sortedBy { it.path }

        override fun getChildOrNull(name: String): InMemoryItem? = items[name]
        override fun contains(name: String): Boolean = name in items

        override fun delete() {
            check(this != root) { "root directory cannot be deleted" }
            list().forEach { it.delete() }

            (parent as Directory?)?.let { it.items -= name }
            fsItems.remove(path)
            listeners.updated().forEach {
                it.onDirectoryDeleted(this)
            }
        }

        override fun createDirectory(name: String): WritableFileSystemDirectory {
            return this@InMemoryFileSystem.createDirectory("${path}/$name")
        }

        override suspend fun createFile(name: String, data: Uint8Buffer): WritableFileSystemFile {
            return this@InMemoryFileSystem.createFile("${path}/$name", data)
        }
    }

    private inner class File(path: String, var data: Uint8Buffer) : InMemoryItem(path), WritableFileSystemFile {
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
            (parent as Directory?)?.let { it.items -= name }
            fsItems.remove(path)
            listeners.updated().forEach {
                it.onFileDeleted(this)
            }
        }
    }
}

suspend fun InMemoryFileSystem(copyFrom: FileSystem): InMemoryFileSystem {
    val fs = InMemoryFileSystem()
    fs.merge(copyFrom)
    return fs
}

suspend fun InMemoryFileSystem.merge(from: FileSystem) {
    from.listAll()
        .filter { it.path != "/" && it.path !in this }
        .forEach {
            when (it) {
                is FileSystemDirectory -> createDirectory(it.path)
                is FileSystemFile -> createFile(it.path, it.read())
            }
        }
}

expect suspend fun InMemoryFileSystem.toZip(): Uint8Buffer
