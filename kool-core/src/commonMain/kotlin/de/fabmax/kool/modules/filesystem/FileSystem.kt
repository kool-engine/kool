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
            check(".." !in path) { "'..' not allowed in paths (full path: $path)" }
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
expect suspend fun zipFileSystem(zipData: Uint8Buffer): FileSystem
