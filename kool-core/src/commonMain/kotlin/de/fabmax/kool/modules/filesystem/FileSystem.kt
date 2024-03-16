package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer

interface FileSystem {
    val root: FileSystemDirectory

    fun listAll(): List<FileSystemItem>
    operator fun get(path: String): FileSystemItem
    operator fun contains(path: String): Boolean

    fun addFileSystemWatcher(listener: FileSystemWatcher)
    fun removeFileSystemWatcher(listener: FileSystemWatcher)

    companion object {
        private val multiSeperatorRegex = Regex("/{2,}")

        fun sanitizePath(path: String): String {
            return "/${path}"
                .replace('\\', '/')
                .replace(multiSeperatorRegex, "/")
                .removeSuffix("/")
                .ifEmpty { "/" }
        }

        fun parentPath(path: String): String {
            val p = sanitizePath(path).substringBeforeLast('/')
            return p.ifEmpty { "/" }
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
    override val root: WritableFileSystemDirectory

    fun createDirectory(path: String): WritableFileSystemDirectory
    suspend fun createFile(path: String, data: Uint8Buffer): WritableFileSystemFile
}

fun WritableFileSystem.getOrCreateDirectory(path: String): WritableFileSystemDirectory {
    return root.getOrCreateDirectory(path)
}

suspend fun WritableFileSystem.getOrCreateFile(path: String): WritableFileSystemFile {
    return root.getOrCreateFile(path)
}

fun WritableFileSystem.createDirectories(path: String): WritableFileSystemDirectory {
    return root.createDirectories(path)
}

interface FileSystemWatcher {
    fun onFileCreated(file: FileSystemFile) { }
    fun onFileDeleted(file: FileSystemFile) { }
    fun onFileChanged(file: FileSystemFile) { }

    fun onDirectoryCreated(directory: FileSystemDirectory) { }
    fun onDirectoryDeleted(directory: FileSystemDirectory) { }
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
