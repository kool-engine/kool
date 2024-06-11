package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer

interface WritableFileSystem : FileSystem {
    override val root: WritableFileSystemDirectory

    override fun listAll(): List<WritableFileSystemItem>
    override operator fun get(path: String): WritableFileSystemItem

    fun createDirectory(path: String): WritableFileSystemDirectory
    suspend fun createFile(path: String, data: Uint8Buffer): WritableFileSystemFile

    suspend fun move(sourcePath: String, destinationPath: String)
}

fun WritableFileSystem.getFileOrNull(path: String): WritableFileSystemFile? {
    return (this as FileSystem).getFileOrNull(path) as WritableFileSystemFile?
}

fun WritableFileSystem.getDirectoryOrNull(path: String): WritableFileSystemDirectory? {
    return (this as FileSystem).getDirectoryOrNull(path) as WritableFileSystemDirectory?
}

fun WritableFileSystem.getItem(path: String): WritableFileSystemItem {
    return (this as FileSystem).getItem(path) as WritableFileSystemItem
}

fun WritableFileSystem.getFile(path: String): WritableFileSystemFile {
    return (this as FileSystem).getFile(path) as WritableFileSystemFile
}

fun WritableFileSystem.getDirectory(path: String): WritableFileSystemDirectory {
    return (this as FileSystem).getDirectory(path) as WritableFileSystemDirectory
}

fun WritableFileSystem.getOrCreateDirectory(path: String): WritableFileSystemDirectory {
    return root.getOrCreateDirectory(path)
}

suspend fun WritableFileSystem.getOrCreateFile(path: String): WritableFileSystemFile {
    return root.getOrCreateFile(path)
}

fun WritableFileSystem.getOrCreateDirectories(path: String): WritableFileSystemDirectory {
    return root.getOrCreateDirectories(path)
}
