package de.fabmax.kool.editor

import de.fabmax.kool.util.copy
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.nio.file.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.io.path.PathWalkOption
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.io.path.walk

class DirectoryWatcher(val watchPaths: Set<String>, val eventTimeoutMs: Long = 100L) {

    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchKeysToPaths = mutableMapOf<WatchKey, Path>()
    private val pathsToWatchKeys = mutableMapOf<Path, WatchKey>()

    private val watcherThread = thread(isDaemon = true) {
        for (path in watchPaths) {
            addWatchPathWithSubDirs(path)
        }
        watchLoop()
    }

    val changes = Channel<List<ChangeEvent>>(1)

    private fun watchLoop() {
        try {
            val changeEvents = mutableListOf<ChangeEvent>()
            while (true) {
                val watchKey = watchService.poll(eventTimeoutMs, TimeUnit.MILLISECONDS)
                if (watchKey != null) {
                    for (event in watchKey.pollEvents()) {
                        val file = event.context() as? Path
                        val dir = watchKeysToPaths[watchKey]

                        if (file != null && dir != null) {
                            val changedFile = dir.resolve(file)
                            changeEvents += processChangeEvent(changedFile, event)
                        }
                    }
                    watchKey.reset()

                } else if (changeEvents.isNotEmpty()) {
                    runBlocking {
                        changes.send(changeEvents.copy())
                    }
                    changeEvents.clear()
                }
            }
        } catch (e: Exception) {
            logI("AppLoader.watcher") { "File system watcher terminated by $e" }
        }
    }

    private fun processChangeEvent(chgPath: Path, chgEvent: WatchEvent<*>): ChangeEvent {
        // update watched directories if necessary
        if (chgPath.isDirectory() && chgEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            addWatchPathWithSubDirs(chgPath.pathString)
        } else if (chgPath in pathsToWatchKeys.keys && chgEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
            val removeKey = pathsToWatchKeys.remove(chgPath)
            watchKeysToPaths.remove(removeKey)
            removeKey?.cancel()
        }

        // publish change event
        val eventType = when (chgEvent.kind()) {
            StandardWatchEventKinds.ENTRY_CREATE -> ChangeType.CREATED
            StandardWatchEventKinds.ENTRY_MODIFY -> ChangeType.MODIFIED
            StandardWatchEventKinds.ENTRY_DELETE -> ChangeType.DELETED
            else -> ChangeType.UNKNOWN
        }
        return ChangeEvent(chgPath, eventType)
    }

    private fun addWatchPathWithSubDirs(path: String) {
        Path.of(path).walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach {
            try {
                if (it.isDirectory()) {
                    //logD { "watching directory: $it" }
                    val key = it.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    )
                    watchKeysToPaths[key] = it
                    pathsToWatchKeys[it] = key
                }
            } catch (e: Exception) {
                logE { "Directory watching failed for path $it" }
                e.printStackTrace()
            }
        }
    }

    enum class ChangeType {
        CREATED,
        MODIFIED,
        DELETED,
        UNKNOWN
    }

    data class ChangeEvent(val path: Path, val type: ChangeType)
}
