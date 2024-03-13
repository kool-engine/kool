package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer

interface FileSystem {
    val root: FileSystemDirectory

    fun listAll(): List<FileSystemItem>
    operator fun get(path: String): FileSystemItem
    operator fun contains(path: String): Boolean

    fun addFileSystemWatcher(listener: FileSystemWatcher)
    fun removeFileSystemWatcher(listener: FileSystemWatcher)

    val FileSystemItem.parent: FileSystemDirectory
        get() = get(parentPath(path)) as FileSystemDirectory

    companion object {
        fun sanitizePath(path: String): String {
            val p = path.replace('\\', '/')
            return if (p.startsWith("/")) p else "/$p"
        }

        fun sanitizeDirPath(path: String): String {
            val p = sanitizePath(path)
            return if (p.endsWith("/")) p else "$p/"
        }

        fun parentPath(path: String): String {
            val p = sanitizePath(path)
            return if (p == "/") p else p.removeSuffix("/").substringBeforeLast('/') + '/'
        }
    }
}

fun FileSystem.getItemOrNull(path: String): FileSystemItem? {
    return if (path in this) {
        this[path]
    } else {
        null
    }
}

fun FileSystem.getFileOrNull(path: String): FileSystemFile? = getItemOrNull(path) as? FileSystemFile?
fun FileSystem.getDirectoryOrNull(path: String): FileSystemDirectory? = getItemOrNull(path) as? FileSystemDirectory?

fun FileSystem.getItem(path: String): FileSystemItem = checkNotNull(getItemOrNull(path)) { "Item not found: $path" }
fun FileSystem.getFile(path: String): FileSystemFile = checkNotNull(getFileOrNull(path)) { "File not found: $path" }
fun FileSystem.getDirectory(path: String): FileSystemDirectory = checkNotNull(getDirectoryOrNull(path)) { "Directory not found: $path" }

interface WritableFileSystem : FileSystem {
    fun createDirectory(path: String): WritableFileSystemDirectory
    suspend fun createFile(path: String, data: Uint8Buffer): WritableFileSystemFile

    fun getOrCreateDirectory(path: String): WritableFileSystemDirectory {
        val dirPath = FileSystem.sanitizeDirPath(path)
        return if (dirPath in this) {
            get(dirPath) as WritableFileSystemDirectory
        } else {
            createDirectory(dirPath)
        }
    }

    suspend fun getOrCreateFile(path: String): WritableFileSystemFile {
        return if (path in this) {
            get(path) as WritableFileSystemFile
        } else {
            createFile(path, Uint8Buffer(0))
        }
    }
}

interface FileSystemWatcher {
    fun onFileCreated(file: FileSystemFile) { }
    fun onFileDeleted(file: FileSystemFile) { }
    fun onFileChanged(file: FileSystemFile) { }

    fun onDirectoryCreated(directory: FileSystemDirectory) { }
    fun onDirectoryDeleted(directory: FileSystemDirectory) { }
    fun onDirectoryChanged(directory: FileSystemDirectory) { }
}

fun FileSystem.print() {
    fun size(s: Long): String {
        var str = "$s"
        while (str.length < 10) {
            str = " $str"
        }
        return str
    }

    val w = if (this is WritableFileSystem) "w" else "r"

    listAll().forEach {
        when (it) {
            is FileSystemDirectory -> println("${size(it.list().size.toLong())}  D$w  ${it.path}")
            is FileSystemFile -> println("${size(it.size)}  F$w  ${it.path}")
        }
    }
}

expect suspend fun zipFileSystem(path: String): FileSystem
