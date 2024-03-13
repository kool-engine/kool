package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.toBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
class PhysicalFileSystem(rootPath: String, private val isLaunchWatchService: Boolean = false) : WritableFileSystem, AutoCloseable {
    val rootPath = Path(rootPath).absolutePathString()

    override val root: FileSystemDirectory
    private val fsItems = mutableMapOf<String, FsItem>()

    private val listeners = BufferedList<FileSystemWatcher>()
    private val watchService: FileSystemWatchService?
    private val watchJob: Job?

    init {
        val root = Directory(Path(rootPath))
        fsItems["/"] = root
        this.root = root

        root.physPath
            .walk(PathWalkOption.INCLUDE_DIRECTORIES, PathWalkOption.FOLLOW_LINKS)
            .filter { it != root.physPath }
            .forEach { path ->
                val item: FsItem = if (path.isDirectory()) Directory(path) else File(path)
                fsItems[item.path] = item
            }
        fsItems.values.filter { it != root }.forEach { (it.parent as Directory).items[it.name] = it }

        if (isLaunchWatchService) {
            watchService = FileSystemWatchService(this)
            watchJob = launchWatchJob(watchService)
        } else {
            watchService = null
            watchJob = null
        }
    }

    private fun launchWatchJob(service: FileSystemWatchService) = launchOnMainThread {
        while (!service.isClosed) {
            val events = service.changes.receive()
            events.forEach { event ->
                // do not rely on Path.isDirectory to decide for item type -> if a directory was deleted,
                // Path.isDirectory will also be false
                val path = event.path.fsPath(false)
                val item = fsItems[path] ?: fsItems[FileSystem.sanitizeDirPath(path)]

                if (item is Directory) {
                    when (event.type) {
                        FileSystemWatchService.ChangeType.CREATED -> { } // created but item already exists -> already handled
                        FileSystemWatchService.ChangeType.MODIFIED -> listeners.updated().forEach { it.onDirectoryChanged(item) }
                        FileSystemWatchService.ChangeType.DELETED -> listeners.updated().forEach { it.onDirectoryDeleted(item) }
                    }
                } else if (item is File) {
                    when (event.type) {
                        FileSystemWatchService.ChangeType.CREATED -> { } // created but item already exists -> already handled
                        FileSystemWatchService.ChangeType.MODIFIED -> listeners.updated().forEach { it.onFileChanged(item) }
                        FileSystemWatchService.ChangeType.DELETED -> listeners.updated().forEach { it.onFileDeleted(item) }
                    }
                } else if (event.type == FileSystemWatchService.ChangeType.CREATED) {
                    val fsItem: FsItem = if (event.path.isDirectory()) {
                        val dir = Directory(event.path)
                        listeners.updated().forEach { it.onDirectoryCreated(dir) }
                        dir
                    } else {
                        val file = File(event.path)
                        listeners.updated().forEach { it.onFileCreated(file) }
                        file
                    }
                    (fsItem.parent as Directory).items[fsItem.name] = fsItem
                    fsItems[fsItem.path] = fsItem

                }
                // else: received event for an untracked item -> ignored
                // this can happen if a tracked file was deleted. In that case the event was already handled
            }
        }
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
            if (!isLaunchWatchService) {
                // if a watch service is running, the service will issue the change event
                it.onDirectoryChanged(parentDir)
            }
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
            if (!isLaunchWatchService) {
                // if a watch service is running, the service will issue the change event
                it.onDirectoryChanged(parentDir)
            }
        }
        return file
    }

    private fun Path.fsPath(isDir: Boolean): String {
        val fsPath = absolutePathString().removePrefix(rootPath)
        return if (isDir) FileSystem.sanitizeDirPath(fsPath) else FileSystem.sanitizePath(fsPath)
    }

    private sealed class FsItem: FileSystemItem {
        abstract fun delete(isParentDelete: Boolean)
    }

    private inner class Directory(val physPath: Path) : FsItem(), WritableFileSystemDirectory {
        override val path: String = physPath.fsPath(true)

        val items = mutableMapOf<String, FsItem>()

        override fun list(): List<FsItem> = items.values.toList().sortedBy { it.path }

        override fun getChildOrNull(name: String): FsItem? = items[name]
        override fun contains(name: String): Boolean = name in items

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
        override val path: String = physPath.fsPath(false)

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
            if (!isLaunchWatchService) {
                // if a watch service is running, the service will issue the change event
                listeners.updated().forEach { it.onFileChanged(this) }
            }
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

    override fun close() {
        watchService?.isClosed = true
        watchJob?.cancel()
    }
}