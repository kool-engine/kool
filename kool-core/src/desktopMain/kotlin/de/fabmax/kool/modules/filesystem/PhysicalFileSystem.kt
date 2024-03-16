package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
class PhysicalFileSystem(rootPath: String, private val isLaunchWatchService: Boolean = false) : WritableFileSystem, AutoCloseable {
    val rootPath = Path(rootPath).absolutePathString()

    override val root: Directory
    private val fsItems = mutableMapOf<String, FsItem>()

    private val watchers = BufferedList<FileSystemWatcher>()
    private val watchService: FileSystemWatchService?
    private val watchJob: Job?

    private val fsWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) {
            fsItems[file.path] = file as File
            val parent = file.parent
            parent?.let { it.children[file.name] = file }
        }

        override fun onFileDeleted(file: FileSystemFile) {
            fsItems.remove(file.path)
            val parent = file.parent as Directory?
            parent?.let { it.children -= file.name }
        }

        override fun onDirectoryCreated(directory: FileSystemDirectory) {
            fsItems[directory.path] = directory as Directory
            val parent = directory.parent
            parent?.let { it.children[directory.name] = directory }
        }

        override fun onDirectoryDeleted(directory: FileSystemDirectory) {
            fsItems.remove(directory.path)
            val parent = directory.parent as Directory?
            parent?.let { it.children -= directory.name }
        }
    }

    init {
        watchers += fsWatcher

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
        fsItems.values.filter { it != root }.forEach { (it.parent as Directory).children[it.name] = it }

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
            val events = service.changes.receiveCatching()
            events.getOrElse { emptyList() }.forEach { event ->
                // do not rely on Path.isDirectory to decide for item type -> if a directory was deleted,
                // Path.isDirectory will also be false
                val path = event.path.fsPath(false)
                val item = fsItems[path] ?: fsItems[FileSystem.sanitizeDirPath(path)]

                if (item is Directory) {
                    when (event.type) {
                        FileSystemWatchService.ChangeType.CREATED -> { } // created but item already exists -> already handled
                        FileSystemWatchService.ChangeType.MODIFIED -> watchers.updated().forEach { it.onDirectoryChanged(item) }
                        FileSystemWatchService.ChangeType.DELETED -> watchers.updated().forEach { it.onDirectoryDeleted(item) }
                    }
                } else if (item is File) {
                    when (event.type) {
                        FileSystemWatchService.ChangeType.CREATED -> { } // created but item already exists -> already handled
                        FileSystemWatchService.ChangeType.MODIFIED -> watchers.updated().forEach { it.onFileChanged(item) }
                        FileSystemWatchService.ChangeType.DELETED -> watchers.updated().forEach { it.onFileDeleted(item) }
                    }
                } else if (event.type == FileSystemWatchService.ChangeType.CREATED) {
                    // created but item is null -> externally created
                    if (event.path.isDirectory()) {
                        val dir = Directory(event.path)
                        watchers.updated().forEach { it.onDirectoryCreated(dir) }
                    } else {
                        val file = File(event.path)
                        watchers.updated().forEach { it.onFileCreated(file) }
                    }
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
        watchers += listener
    }

    override fun removeFileSystemWatcher(listener: FileSystemWatcher) {
        watchers -= listener
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

        if (!isLaunchWatchService) {
            watchers.updated().forEach {
                it.onDirectoryCreated(dir)
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

        if (!isLaunchWatchService) {
            watchers.updated().forEach {
                it.onFileCreated(file)
                it.onDirectoryChanged(parentDir)
            }
        }
        return file
    }

    private fun Path.fsPath(isDir: Boolean): String {
        val fsPath = absolutePathString().removePrefix(rootPath)
        return if (isDir) FileSystem.sanitizeDirPath(fsPath) else FileSystem.sanitizePath(fsPath)
    }

    sealed class FsItem: FileSystemItem {
        internal abstract fun delete(isParentDelete: Boolean)
    }

    inner class Directory(val physPath: Path) : FsItem(), WritableFileSystemDirectory {
        override val parent: Directory?
            get() = this@PhysicalFileSystem.getDirectoryOrNull(FileSystem.parentPath(path)) as Directory?

        override val path: String = physPath.fsPath(true)

        internal val children = mutableMapOf<String, FsItem>()

        override fun list(): List<FsItem> = children.values.toList().sortedBy { it.path }

        override fun getChildOrNull(name: String): FsItem? = children[name]
        override fun contains(name: String): Boolean = name in children

        override fun createDirectory(name: String): WritableFileSystemDirectory {
            return this@PhysicalFileSystem.createDirectory("${path}/$name")
        }

        override suspend fun createFile(name: String, data: Uint8Buffer): WritableFileSystemFile {
            return this@PhysicalFileSystem.createFile("${path}/$name", data)
        }

        override fun delete() {
            delete(false)
        }

        override fun delete(isParentDelete: Boolean) {
            check(this != root) { "root directory cannot be deleted" }
            list().forEach { it.delete(true) }
            try {
                physPath.deleteRecursively()
            } catch (e: Exception) {
                logE { "Error on deleting directory: $e" }
            }

            if (!isLaunchWatchService) {
                val parent = this.parent
                watchers.updated().forEach {
                    it.onDirectoryDeleted(this)
                    if (parent != null && !isParentDelete) {
                        it.onDirectoryChanged(parent)
                    }
                }
            }
        }
    }

    inner class File(val physPath: Path) : FsItem(), WritableFileSystemFile {
        override val parent: Directory?
            get() = this@PhysicalFileSystem.getDirectoryOrNull(FileSystem.parentPath(path)) as Directory?

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
                watchers.updated().forEach { it.onFileChanged(this) }
            }
        }

        override fun delete() {
            delete(false)
        }

        override fun delete(isParentDelete: Boolean) {
            physPath.deleteIfExists()

            if (!isLaunchWatchService) {
                val parent = this.parent
                watchers.updated().forEach {
                    it.onFileDeleted(this)
                    if (parent != null && !isParentDelete) {
                        it.onDirectoryChanged(parent)
                    }
                }
            }
        }
    }

    override fun close() {
        watchService?.isClosed = true
        watchJob?.cancel()
    }
}