package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.exists

actual class AppLoadService actual constructor() {

    // todo: paths are hardcoded for now
    private val appJarPath = Path.of(KoolEditor.PROJECT_JAR_PATH)
    private val watchPath = KoolEditor.PROJECT_SRC_DIR

    private val buildInProgress = AtomicBoolean(false)
    private val watcher = DirectoryWatcher(setOf(watchPath))

    actual var hasAppChanged = true
        private set

    private val loader = thread(isDaemon = true) {
        var changeFlag = false
        while (true) {
            val changeEvent = watcher.changeEvents.poll(CHANGE_FLAG_TIMEOUT_MS, TimeUnit.MILLISECONDS)

            if (changeEvent != null) {
                logD { "File changed: ${changeEvent.path}; ${changeEvent.type}" }
                changeFlag = true

            } else if (changeFlag) {
                // file system changes where detected in the past and the poll timeout passed without a new change
                //   -> all file changes where written, and it should be safe to trigger the rebuild
                changeFlag = false
                hasAppChanged = true
            }
        }
    }

    actual suspend fun buildApp() {
        logI("AppLoader.loader") { "Executing gradle build" }

        suspendCoroutine { continuation ->
            thread {
                val buildProcess = Runtime.getRuntime().exec(arrayOf("gradlew.bat", ":kool-editor-template:jvmJar"))
                thread {
                    BufferedReader(InputStreamReader(buildProcess.inputStream)).lines().forEach {
                        logD("AppLoader.gradleBuild") { it }
                    }
                }
                thread {
                    BufferedReader(InputStreamReader(buildProcess.errorStream)).lines().forEach {
                        logW("AppLoader.gradleBuild") { it }
                    }
                }
                val exitCode = buildProcess.waitFor()
                logI { "Gradle build finished (exit code: $exitCode)" }
                hasAppChanged = false

                if (exitCode == 0) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("Build failed")))
                }
            }
        }
    }

    actual suspend fun loadApp(): EditorAwareApp {
        if (!appJarPath.exists()) {
            buildApp()
        }

        logI { "Loading app from jar: $appJarPath" }
        val loader = URLClassLoader(arrayOf(appJarPath.toUri().toURL()), this.javaClass.classLoader)
        val appClass = loader.loadClass(KoolEditor.PROJECT_MAIN_CLASS)
        val app = appClass.getDeclaredConstructor().newInstance()
        return app as EditorAwareApp
    }

    companion object {
        private const val CHANGE_FLAG_TIMEOUT_MS = 300L
    }
}