package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer

interface FileSystem {
    val root: FileSystemDirectory

    fun listAll(): List<FileSystemItem>
    operator fun get(path: String): FileSystemItem
}

interface WritableFileSystem : FileSystem {
    fun createDirectory(path: String): FileSystemDirectory
    fun createFile(path: String, data: Uint8Buffer): FileSystemFile
    fun writeFile(file: FileSystemFile, data: Uint8Buffer)

    fun delete(item: FileSystemItem)
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
