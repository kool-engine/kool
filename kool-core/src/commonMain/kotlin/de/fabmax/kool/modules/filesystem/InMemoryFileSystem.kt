package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer

class InMemoryFileSystem : WritableFileSystem {

    override val root: FileSystemDirectory = Directory("/")
    private val fsItems = mutableMapOf<String, InMemoryItem>("/" to root as Directory)

    override fun listAll(): List<FileSystemItem> = fsItems.values.toList().sortedBy { it.path }

    override fun get(path: String): FileSystemItem = checkNotNull(fsItems[path]) { "File not found: $path" }

    override fun createDirectory(path: String): FileSystemDirectory {
        var dirPath = path.removeSuffix("/")
        val parentPath = dirPath.substringBeforeLast('/') + "/"
        val name = dirPath.substringAfterLast('/')
        check(name.isNotBlank()) { "invalid directory path: $path" }
        dirPath = "${dirPath}/"

        val parentDir = get(parentPath) as Directory
        val dir = Directory(dirPath)
        fsItems[dirPath] = dir
        parentDir.items[dir.name] = dir
        return dir
    }

    override fun createFile(path: String, data: Uint8Buffer): FileSystemFile {
        val parentPath = path.substringBeforeLast('/') + "/"
        val name = path.substringAfterLast('/')
        check(name.isNotBlank()) { "invalid file path: $path" }

        val dir = get(parentPath) as Directory
        val file = File(path, data)
        fsItems[path] = file
        dir.items[file.name] = file
        return file
    }

    override fun writeFile(file: FileSystemFile, data: Uint8Buffer) {
        (get(file.path) as File).data = data
    }

    override fun delete(item: FileSystemItem) {
        fsItems[item.path]?.delete()
    }

    sealed class InMemoryItem: FileSystemItem {
        abstract fun delete()
    }

    private inner class Directory(override val path: String) : InMemoryItem(), FileSystemDirectory {
        val items = mutableMapOf<String, InMemoryItem>()

        override fun list(): List<InMemoryItem> = items.values.toList().sortedBy { it.path }

        override fun get(name: String): InMemoryItem = checkNotNull(items[name]) { "File not found: $name" }

        override fun delete() {
            check(this != root) { "root directory cannot be deleted" }
            list().forEach { it.delete() }
            fsItems.remove(path)
        }
    }

    private inner class File(override val path: String, var data: Uint8Buffer) : InMemoryItem(), FileSystemFile {
        override val size: Long
            get() = data.capacity.toLong()

        override suspend fun read(): Uint8Buffer = data

        override fun delete() {
            fsItems.remove(path)
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
