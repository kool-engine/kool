package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.toBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
class PhysicalFileSystem(rootPath: String) : WritableFileSystem {
    val rootPath = Path(rootPath).absolutePathString()

    override val root: FileSystemDirectory
    private val fsItems = mutableMapOf<String, FsItem>()

    private val listeners = BufferedList<FileSystemWatcher>()

    init {
        val root = Directory(Path(rootPath))
        fsItems["/"] = root
        this.root = root

        root.physPath
            .walk(PathWalkOption.INCLUDE_DIRECTORIES, PathWalkOption.FOLLOW_LINKS)
            .filter { it != root.physPath }
            .forEach { path ->
                println("walking $path")
                val item: FsItem = if (path.isDirectory()) Directory(path) else File(path)
                fsItems[item.path] = item
            }

        // todo: register OS watch service
    }

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
        val dirPhysPath = Path(parentDir.physPath.absolutePathString(), name)
        dirPhysPath.createDirectory()
        val dir = Directory(dirPhysPath)
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
        val physPath = Path(parentDir.physPath.absolutePathString(), name)
        val file = File(physPath)
        file.write(data)
        fsItems[filePath] = file
        parentDir.items[file.name] = file

        listeners.updated().forEach {
            it.onFileCreated(file)
            it.onDirectoryChanged(parentDir)
        }
        return file
    }

    private fun Path.fsPath(): String {
        val fsPath = absolutePathString().removePrefix(rootPath)
        return FileSystem.sanitizePath(fsPath)
    }

    private sealed class FsItem: FileSystemItem {
        abstract fun delete(isParentDelete: Boolean)
    }

    private inner class Directory(val physPath: Path) : FsItem(), WritableFileSystemDirectory {
        override val path: String = FileSystem.sanitizeDirPath(physPath.fsPath())

        val items = mutableMapOf<String, FsItem>()

        override fun list(): List<FsItem> = items.values.toList().sortedBy { it.path }

        override fun get(name: String): FsItem = checkNotNull(items[name]) { "File not found: $name" }

        override fun delete() {
            delete(false)
        }

        override fun delete(isParentDelete: Boolean) {
            check(this != root) { "root directory cannot be deleted" }
            list().forEach { it.delete(true) }
            fsItems.remove(path)
            physPath.deleteRecursively()

            val parent = this@PhysicalFileSystem[FileSystem.parentPath(path)] as Directory
            listeners.updated().forEach {
                it.onDirectoryDeleted(this)
                if (!isParentDelete) {
                    it.onDirectoryChanged(parent)
                }
            }
        }
    }

    private inner class File(val physPath: Path) : FsItem(), WritableFileSystemFile {
        override val path: String = physPath.fsPath()

        override val size: Long
            get() = physPath.fileSize()

        override suspend fun read(): Uint8Buffer {
            return withContext(Dispatchers.IO) {
                physPath.readBytes().toBuffer()
            }
        }

        override suspend fun write(data: Uint8Buffer) {
            withContext(Dispatchers.IO) {
                physPath.writeBytes(data.toArray())
            }
            listeners.updated().forEach { it.onFileChanged(this) }
        }

        override fun delete() {
            delete(false)
        }

        override fun delete(isParentDelete: Boolean) {
            fsItems.remove(path)
            physPath.deleteExisting()

            val parent = this@PhysicalFileSystem[FileSystem.parentPath(path)] as Directory
            listeners.updated().forEach {
                it.onFileDeleted(this)
                if (!isParentDelete) {
                    it.onDirectoryChanged(parent)
                }
            }
        }
    }
}