package de.fabmax.kool.modules.filesystem

interface FileSystemWatcher {
    fun onFileCreated(file: FileSystemFile) { }
    fun onFileDeleted(file: FileSystemFile) { }
    fun onFileChanged(file: FileSystemFile) { }

    fun onDirectoryCreated(directory: FileSystemDirectory) { }
    fun onDirectoryDeleted(directory: FileSystemDirectory) { }
}