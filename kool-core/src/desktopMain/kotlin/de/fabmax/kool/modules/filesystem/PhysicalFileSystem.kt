package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.withContext
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
class PhysicalFileSystem(
    rootDir: Path,
    val excludePaths: Set<Path> = emptySet(),
    private val isLaunchWatchService: Boolean = false
) : WritableFileSystem, AutoCloseable {

    val rootPath = rootDir.absolute()

    override val root: Directory
    private val fsItems = mutableMapOf<String, FsItem>()

    private val watchers = BufferedList<FileSystemWatcher>()
    private val watchService: FileSystemWatchService?
    private val watchJob: Job?

    private val expectedEvents = mutableSetOf<FsEvent>()

    private val fsWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) {
            fsItems[file.path] = file as File
            file.parent?.let { it.children[file.name] = file }
        }

        override fun onFileDeleted(file: FileSystemFile) {
            fsItems.remove(file.path)
            val parent = file.parent as Directory?
            parent?.let { it.children -= file.name }
        }

        override fun onDirectoryCreated(directory: FileSystemDirectory) {
            fsItems[directory.path] = directory as Directory
            directory.parent?.let { it.children[directory.name] = directory }
        }

        override fun onDirectoryDeleted(directory: FileSystemDirectory) {
            fun removeDirItem(dir: Directory) {
                dir.children.values.forEach {
                    if (it is Directory) {
                        removeDirItem(it)
                    }
                    fsItems.remove(dir.path)
                }
            }

            removeDirItem(directory as Directory)
            fsItems.remove(directory.path)
            directory.parent?.let { it.children -= directory.name }
        }
    }

    init {
        watchers += fsWatcher

        root = Directory(rootPath)
        fsItems["/"] = root

        rootPath.visitFileTree {
            onPreVisitDirectory { dir, _ ->
                if (dir in excludePaths) {
                    FileVisitResult.SKIP_SUBTREE
                } else {
                    if (dir != rootPath) {
                        val dirItem = Directory(dir)
                        fsItems[dirItem.path] = dirItem
                    }
                    FileVisitResult.CONTINUE
                }
            }
            onVisitFile { file, _ ->
                if (file !in excludePaths) {
                    val fileItem = File(file)
                    fsItems[fileItem.path] = fileItem
                }
                FileVisitResult.CONTINUE
            }
        }
        fsItems.values
            .filter { it != root }
            .forEach {
                val parentDir = checkNotNull(it.parent as? Directory?) { "parent is null: ${it.physPath}" }
                parentDir.children[it.name] = it
            }

        if (isLaunchWatchService) {
            watchService = FileSystemWatchService(this)
            watchJob = launchWatchJob(watchService)
        } else {
            watchService = null
            watchJob = null
        }
    }

    private fun expectFsEvents(vararg events: FsEvent) {
        if (isLaunchWatchService) {
            synchronized(expectedEvents) {
                if (expectedEvents.size > 1000) {
                    expectedEvents.removeIf { System.currentTimeMillis() - it.time > 1_000 }
                }
                expectedEvents += events
            }
        }
    }

    private fun launchWatchJob(service: FileSystemWatchService) = launchOnMainThread {
        while (!service.isClosed) {
            val events = service.changes.receiveCatching()
            events.getOrElse { emptyList() }.forEach { event ->
                // do not rely on Path.isDirectory to decide for item type -> if a directory was deleted,
                // Path.isDirectory will also be false
                val path = event.path.fsPath()
                val fsEvent = FsEvent(path, event.type)
                val wasExpected = synchronized(expectedEvents) {
                    expectedEvents.remove(fsEvent)
                }

                if (!wasExpected) {
                    val item = fsItems[path] ?: fsItems[FileSystem.sanitizePath(path)]
                    if (item is Directory) {
                        when (event.type) {
                            FileSystemWatchService.ChangeType.CREATED -> { logW { "Unexpected: Creation event for existing directory: ${item.path}" } }
                            FileSystemWatchService.ChangeType.MODIFIED -> { }
                            FileSystemWatchService.ChangeType.DELETED -> watchers.updated().forEach { it.onDirectoryDeleted(item) }
                        }
                    } else if (item is File) {
                        when (event.type) {
                            FileSystemWatchService.ChangeType.CREATED -> { logW { "Unexpected: Creation event for existing file: ${item.path}" } }
                            FileSystemWatchService.ChangeType.MODIFIED -> watchers.updated().forEach { it.onFileChanged(item) }
                            FileSystemWatchService.ChangeType.DELETED -> watchers.updated().forEach { it.onFileDeleted(item) }
                        }
                    } else if (event.type == FileSystemWatchService.ChangeType.CREATED) {
                        if (event.path.isDirectory()) {
                            val dir = Directory(event.path)
                            watchers.updated().forEach { it.onDirectoryCreated(dir) }
                        } else {
                            val file = File(event.path)
                            watchers.updated().forEach { it.onFileCreated(file) }
                        }
                    }
                }
            }
        }
    }

    override fun listAll(): List<WritableFileSystemItem> = fsItems.values.toList().sortedBy { it.path }

    override fun get(path: String): WritableFileSystemItem {
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
        val dirPath = FileSystem.sanitizePath(path)
        check(dirPath !in this) { "directory already exists: $dirPath" }

        val parentPath = FileSystem.parentPath(dirPath)
        val name = dirPath.removeSuffix("/").substringAfterLast('/')
        check(name.isNotBlank()) { "invalid directory path: $dirPath" }

        val parentDir = get(parentPath) as Directory
        val dirPhysPath = Path(parentDir.physPath.absolutePathString(), name)
        val dir = Directory(dirPhysPath)
        expectFsEvents(FsEvent(dir.path, FileSystemWatchService.ChangeType.CREATED))

        dirPhysPath.createDirectory()
        watchers.updated().forEach {
            it.onDirectoryCreated(dir)
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

        expectFsEvents(
            FsEvent(file.path, FileSystemWatchService.ChangeType.CREATED),
            FsEvent(file.path, FileSystemWatchService.ChangeType.MODIFIED)
        )

        withContext(Dispatchers.IO) {
            physPath.writeBytes(data.toArray())
        }
        watchers.updated().forEach {
            it.onFileCreated(file)
        }
        return file
    }

    override suspend fun move(sourcePath: String, destinationPath: String) {
        val dst = FileSystem.sanitizePath(destinationPath)
        val src = getItem(sourcePath) as FsItem
        check(src != root) { "root directory cannot be moved" }
        check(getItemOrNull(dst) == null) { "destination path already exists" }

        val dstDir = getDirectory(FileSystem.parentPath(dst)) as Directory
        val dstName = dst.substringAfterLast('/')
        val dstPath = Path(dstDir.physPath.pathString, dstName)

        when (src) {
            is File -> moveFile(src, dst, dstPath)
            is Directory -> moveDir(src, dst, dstPath)
        }
    }

    private fun moveFile(src: File, dst: String, dstPath: Path) {
        expectFsEvents(
            FsEvent(src.path, FileSystemWatchService.ChangeType.DELETED),
            FsEvent(dst, FileSystemWatchService.ChangeType.CREATED),
        )
        src.physPath.moveTo(dstPath, StandardCopyOption.ATOMIC_MOVE)

        val moved = File(dstPath)
        watchers.updated().forEach {
            it.onFileDeleted(src)
            it.onFileCreated(moved)
        }
    }

    private fun moveDir(src: Directory, dst: String, dstPath: Path) {
        val watchers = watchers.updated()

        expectFsEvents(
            FsEvent(src.path, FileSystemWatchService.ChangeType.DELETED),
            FsEvent(dst, FileSystemWatchService.ChangeType.CREATED),
        )

        watchService?.stopWatching(src.physPath)
        src.physPath.moveTo(dstPath, StandardCopyOption.ATOMIC_MOVE)
        watchService?.startWatching(dstPath)

        fun notifyMoved(dir: Directory) {
            val moveDir = Path(dstPath.pathString, dir.path.removePrefix(src.path))
            watchers.forEach { it.onDirectoryCreated(Directory(moveDir)) }

            dir.children.values.toList().forEach { child ->
                if (child is File) {
                    val moveFile = File(Path(moveDir.pathString, child.name))
                    watchers.forEach {
                        it.onFileCreated(moveFile)
                        it.onFileDeleted(child)
                    }
                } else if (child is Directory) {
                    notifyMoved(child)
                }
            }
            watchers.forEach { it.onDirectoryDeleted(dir) }
        }
        notifyMoved(src)
    }

    private fun Path.fsPath(): String {
        return FileSystem.sanitizePath(absolute().relativeTo(rootPath).pathString)
    }

    override fun close() {
        watchService?.isClosed = true
        watchJob?.cancel()
    }

    private data class FsEvent(val itemPath: String, val event: FileSystemWatchService.ChangeType) {
        val time: Long = System.currentTimeMillis()
    }

    sealed class FsItem(val physPath: Path): WritableFileSystemItem

    inner class Directory(physPath: Path) : FsItem(physPath), WritableFileSystemDirectory {
        override val parent: Directory?
            get() = this@PhysicalFileSystem.getDirectoryOrNull(FileSystem.parentPath(path)) as Directory?

        override var path: String = physPath.fsPath()
            private set

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
            check(this != root) { "root directory cannot be deleted" }

            list().forEach { it.delete() }
            try {
                expectFsEvents(FsEvent(path, FileSystemWatchService.ChangeType.DELETED))
                physPath.deleteIfExists()
            } catch (e: Exception) {
                logE { "Error on deleting directory: $e" }
            }
            watchers.updated().forEach {
                it.onDirectoryDeleted(this)
            }
        }
    }

    inner class File(physPath: Path) : FsItem(physPath), WritableFileSystemFile {
        override val parent: Directory?
            get() = this@PhysicalFileSystem.getDirectoryOrNull(FileSystem.parentPath(path)) as Directory?

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
            if (!isLaunchWatchService) {
                // if a watch service is running, the service will issue the change event
                watchers.updated().forEach { it.onFileChanged(this) }
            }
        }

        override fun delete() {
            expectFsEvents(FsEvent(path, FileSystemWatchService.ChangeType.DELETED))

            physPath.deleteIfExists()
            watchers.updated().forEach {
                it.onFileDeleted(this)
            }
        }
    }
}