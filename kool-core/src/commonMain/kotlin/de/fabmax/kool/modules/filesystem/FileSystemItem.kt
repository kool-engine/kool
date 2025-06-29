package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.MimeType
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.decodeToString
import de.fabmax.kool.util.toBuffer

interface FileSystemItem {
    val path: String
    val name: String
        get() = path.substringAfterLast("/").ifEmpty { "/" }
    val isDirectory: Boolean

    val parent: FileSystemDirectory?
}

interface FileSystemFile : FileSystemItem {
    override val isDirectory: Boolean
        get() = false

    val mimeType: MimeType
        get() = MimeType.forFileName(name)

    val size: Long
    suspend fun read(): Uint8Buffer
}

suspend fun FileSystemFile.readText(): String = read().decodeToString()

interface FileSystemDirectory : FileSystemItem {
    override val isDirectory: Boolean
        get() = true

    fun list(): List<FileSystemItem>
    fun getChildOrNull(name: String): FileSystemItem?
    operator fun get(name: String) = checkNotNull(getChildOrNull(name)) { "Not found: $name" }
    operator fun contains(name: String): Boolean
}

fun FileSystemDirectory.listFiles() = list().filterIsInstance<FileSystemFile>()
fun FileSystemDirectory.listDirectories() = list().filterIsInstance<FileSystemDirectory>()
fun FileSystemDirectory.listRecursively(): List<FileSystemItem> {
    val result = mutableListOf<FileSystemItem>()
    val files = list()
    result += files
    files.filterIsInstance<FileSystemDirectory>().forEach {
        result += it.listRecursively()
    }
    return result
}

suspend fun FileSystemDirectory.copyRecursively(target: WritableFileSystemDirectory) {
    list().forEach { file ->
        if (file is FileSystemFile) {
            target.getOrCreateFile(file.name).write(file.read())
        } else if (file is FileSystemDirectory) {
            file.copyRecursively(target.getOrCreateDirectory(file.name))
        }
    }
}

interface WritableFileSystemItem : FileSystemItem {
    fun delete()
}

interface WritableFileSystemFile : FileSystemFile, WritableFileSystemItem {
    suspend fun write(data: Uint8Buffer)
}

suspend fun WritableFileSystemFile.writeText(text: String) = write(text.encodeToByteArray().toBuffer())

interface WritableFileSystemDirectory : FileSystemDirectory, WritableFileSystemItem {
    fun createDirectory(name: String): WritableFileSystemDirectory
    suspend fun createFile(name: String, data: Uint8Buffer): WritableFileSystemFile
}

fun FileSystemDirectory.collect() = buildList {
    add(this@collect)
    fun FileSystemDirectory.appendAll(): Unit = list().forEach {
        add(it)
        if (it is FileSystemDirectory) {
            it.appendAll()
        }
    }
    appendAll()
}

fun FileSystemDirectory.getItemOrNull(path: String): FileSystemItem? {
    var it = this
    val names = FileSystem.sanitizePath(path).split('/').filter { it.isNotBlank() }
    for (i in names.indices) {
        val name = names[i]
        val child = it.getChildOrNull(name) ?: return null
        if (i == names.lastIndex) {
            return child
        }
        if (child.isDirectory) {
            it = child as FileSystemDirectory
        } else {
            return null
        }
    }
    return null
}

fun FileSystemDirectory.getFileOrNull(path: String): FileSystemFile? = getItemOrNull(path) as? FileSystemFile?
fun FileSystemDirectory.getDirectoryOrNull(path: String): FileSystemDirectory? = getItemOrNull(path) as? FileSystemDirectory?

fun FileSystemDirectory.getItem(path: String): FileSystemItem = checkNotNull(getItemOrNull(path)) { "Item not found: $path" }
fun FileSystemDirectory.getFile(path: String): FileSystemFile = checkNotNull(getFileOrNull(path)) { "File not found: $path" }
fun FileSystemDirectory.getDirectory(path: String): FileSystemDirectory = checkNotNull(getDirectoryOrNull(path)) { "Directory not found: $path" }

fun WritableFileSystemDirectory.getOrCreateDirectory(path: String): WritableFileSystemDirectory {
    return (getDirectoryOrNull(path) as WritableFileSystemDirectory?) ?: createDirectory(path)
}

suspend fun WritableFileSystemDirectory.getOrCreateFile(path: String): WritableFileSystemFile {
    return (getFileOrNull(path) as WritableFileSystemFile?) ?: createFile(path, Uint8Buffer(0))
}

fun WritableFileSystemDirectory.getOrCreateDirectories(path: String): WritableFileSystemDirectory {
    val dirNames = FileSystem.sanitizePath(path).split("/").filter { it.isNotBlank() }
    var it = this
    for (name in dirNames) {
        it = it.getOrCreateDirectory(name)
    }
    return it
}
