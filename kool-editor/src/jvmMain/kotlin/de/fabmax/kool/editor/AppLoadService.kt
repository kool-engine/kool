package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLClassLoader
import java.nio.file.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.io.path.PathWalkOption
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.walk

actual class AppLoadService actual constructor(private val editor: KoolEditor) {

    // todo: jar path is hardcoded for now, discover it dynamically
    private val appJarPath = Path.of("kool-editor-template/build/libs/kool-editor-template-jvm-0.11.0-SNAPSHOT.jar")

    // todo: app project path is hardcoded for now, make it configurable
    private val watchPath = Path.of("kool-editor-template/src")

    private val watchPaths = mutableMapOf<WatchKey, Path>()
    private val changeEventQueue = ArrayBlockingQueue<FileChangeEvent>(100)
    private val buildInProgress = AtomicBoolean(false)

    private val watcher = thread(isDaemon = true) {
        logD { "Watching for file system changes in $watchPath" }

        val watchService = FileSystems.getDefault().newWatchService()
        watchPath.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach {
            try {
                if (it.isDirectory()) {
                    val key = it.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    )
                    watchPaths[key] = it
                }
            } catch (e: Exception) {
                logE { "Directory watching failed for path $it" }
                e.printStackTrace()
            }
        }

        try {
            var watchKey = watchService.take()
            while (watchKey != null) {
                for (event in watchKey.pollEvents()) {
                    val file = event.context() as? Path
                    val dir = watchPaths[watchKey]
                    if (file != null && dir != null) {
                        val fileChangeEvent = FileChangeEvent(dir.resolve(file), event)
                        if (fileChangeEvent.isRelevant()) {
                            changeEventQueue.put(fileChangeEvent)
                        }
                    }
                }
                watchKey.reset()
                watchKey = watchService.take()
            }
        } catch (e: Exception) {
            logI("AppLoader.watcher") { "File system watcher terminated by $e" }
        }
    }

    private val loader = thread(isDaemon = true) {
        var shouldReloadApp = false
        while (true) {
            val changeEvent = changeEventQueue.poll(500, TimeUnit.MILLISECONDS)
            if (changeEvent != null) {
                logD { "File changed: ${changeEvent.path}; ${changeEvent.watchEvent.kind()}" }
                shouldReloadApp = true

            } else if (shouldReloadApp) {
                shouldReloadApp = false
                buildApp()
            }
        }
    }

    init {
        if (appJarPath.exists()) {
            reloadAppJar(appJarPath)
        }
    }

    private fun buildApp() {
        if (!buildInProgress.getAndSet(true)) {
            thread {
                logI("AppLoader.loader") { "Executing gradle build" }
                val p = Runtime.getRuntime().exec(arrayOf("gradlew.bat", ":kool-editor-template:jvmJar"))

                thread {
                    BufferedReader(InputStreamReader(p.inputStream)).lines().forEach {
                        logI("AppLoader.gradleBuild") { it }
                    }
                }
                thread {
                    BufferedReader(InputStreamReader(p.errorStream)).lines().forEach {
                        logW("AppLoader.gradleBuild") { it }
                    }
                }
                val exitCode = p.waitFor()
                logI { "Gradle build finished (exit code: $exitCode)" }

                if (exitCode == 0) {
                    reloadAppJar(appJarPath)
                }
                buildInProgress.set(false)
            }
        }
    }

    private fun reloadAppJar(jarPath: Path) {
        logI { "Loading app from jar: $jarPath" }
        try {
            val loader = URLClassLoader(arrayOf(jarPath.toUri().toURL()), this.javaClass.classLoader)
            val appClass = loader.loadClass("de.fabmax.kool.app.App")
            val app = appClass.getDeclaredConstructor().newInstance()
            val editorAware = app as EditorAwareApp
            editor.loadApp(editorAware)

        } catch (e: Exception) {
            logE { "Failed to load app: $e" }
            e.printStackTrace()
        }
    }

    private data class FileChangeEvent(val path: Path, val watchEvent: WatchEvent<*>) {
        fun isRelevant(): Boolean {
            return !path.isDirectory()
        }
    }
}