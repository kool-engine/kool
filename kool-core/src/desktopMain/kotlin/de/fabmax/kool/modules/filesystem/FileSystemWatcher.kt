package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.nio.file.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.visitFileTree

internal class FileSystemWatchService(val parentFs: PhysicalFileSystem, val eventTimeoutMs: Long = 1000L) {

    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchKeysToPaths = mutableMapOf<WatchKey, Path>()
    private val pathsToWatchKeys = mutableMapOf<Path, WatchKey>()

    var isClosed = false
    val changes = Channel<List<ChangeEvent>>(Channel.UNLIMITED)

    private val watcherThread = thread(isDaemon = true) {
        logD { "Starting FileSystemWatchService for ${parentFs.rootPath}" }
        parentFs.rootPath.watchRecursively()
        try {
            while (!isClosed) {
                watchService.poll(eventTimeoutMs, TimeUnit.MILLISECONDS)?.let { watchKey ->
                    watchKeysToPaths[watchKey]?.let { watchDir ->
                        val changeEvents = watchKey.pollEvents()
                            .filter { it.context() is Path }
                            .filter { it.context() as Path !in parentFs.excludePaths }
                            .mapNotNull { event ->
                                val file = event.context() as Path
                                val changedFile = watchDir.resolve(file)
                                processChangeEvent(changedFile, event)
                            }
                        if (changeEvents.isNotEmpty()) {
                            runBlocking {
                                changes.send(changeEvents)
                            }
                        }
                    }
                    watchKey.reset()
                }
            }
        } catch (e: Exception) {
            logE("FileSystemWatchService") { "Watcher terminated by $e" }
        }
        changes.close()
        logD { "FileSystemWatchService for ${parentFs.rootPath} terminated" }
    }

    private fun processChangeEvent(chgPath: Path, chgEvent: WatchEvent<*>): ChangeEvent? {
        // update watched directories if necessary
        if (chgPath.isDirectory() && chgEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            chgPath.watchRecursively()
        } else if (chgPath in pathsToWatchKeys.keys && chgEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
            val removeKey = pathsToWatchKeys.remove(chgPath)
            watchKeysToPaths.remove(removeKey)
            removeKey?.cancel()
        }

        val eventType = when (chgEvent.kind()) {
            StandardWatchEventKinds.ENTRY_CREATE -> ChangeType.CREATED
            StandardWatchEventKinds.ENTRY_MODIFY -> ChangeType.MODIFIED
            StandardWatchEventKinds.ENTRY_DELETE -> ChangeType.DELETED
            else -> null
        }
        return eventType?.let { ChangeEvent(chgPath, it) }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun Path.watchRecursively() {
        visitFileTree {
            onPreVisitDirectory { dir, _ ->
                if (dir !in parentFs.excludePaths) {
                    dir.watch()
                    FileVisitResult.CONTINUE
                } else {
                    FileVisitResult.SKIP_SUBTREE
                }
            }
        }
    }

    private fun Path.watch() {
        try {
            val key = register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            watchKeysToPaths[key] = this
            pathsToWatchKeys[this] = key
        } catch (e: Exception) {
            logE { "Directory watching failed for path $this" }
            e.printStackTrace()
        }
    }

    fun stopWatching(path: Path) {
        val abs = path.absolutePathString()
        val watchedPaths = pathsToWatchKeys.filter { (path, _) -> path.absolutePathString().startsWith(abs) }
        watchedPaths.forEach { (path, watchKey) ->
            pathsToWatchKeys.remove(path)
            watchKeysToPaths.remove(watchKey)
            watchKey.cancel()
        }
    }

    fun startWatching(path: Path) {
        path.watchRecursively()
    }

    enum class ChangeType {
        CREATED,
        MODIFIED,
        DELETED
    }

    data class ChangeEvent(val path: Path, val type: ChangeType)
}
