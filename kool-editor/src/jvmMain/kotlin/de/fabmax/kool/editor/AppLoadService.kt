package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.api.KoolScript
import de.fabmax.kool.editor.api.ScriptLoader
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.*
import kotlin.reflect.KClass

actual class AppLoadService actual constructor(val paths: ProjectPaths) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()

    private val appClassPath = Path.of(paths.classPath)

    private val buildInProgress = AtomicBoolean(false)
    private val watcher = DirectoryWatcher(paths.srcPaths)

    actual var hasAppChanged = true
        private set

    val ignoredPaths = mutableSetOf<Path>()

    init {
        paths.jsAppScriptsPath?.let {
            ignoredPaths.add(Path.of(it))
        }
    }

    actual fun addIgnorePath(path: String) {
        ignoredPaths.add(Path.of(path))
    }

    private val loader = launch {
        while (true) {
            val changes = watcher.changes.receive().filter { it.path !in ignoredPaths }
            if (changes.isNotEmpty()) {
                logD { "File change detected: ${changes[0].path}, ${changes[0].type}}" }
                hasAppChanged = true
            }
        }
    }

    actual suspend fun buildApp() {
        if (buildInProgress.getAndSet(true)) {
            logW("AppLoader.loader") { "Build is already in progress" }
            return
        }

        logI("AppLoader.loader") { "Executing gradle build" }

        suspendCoroutine { continuation ->
            thread {
                try {
                    val isWindows = "windows" in System.getProperty("os.name").lowercase()
                    val gradleDir = File(paths.gradleRootDir).canonicalFile
                    val gradlewCmd = if (isWindows) "$gradleDir\\gradlew.bat" else "$gradleDir/gradlew"
                    logI { "Building app: $gradlewCmd ${paths.gradleBuildTask}" }

                    val buildProcess = ProcessBuilder()
                        .command(gradlewCmd, paths.gradleBuildTask)
                        .directory(gradleDir)
                        .start()
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
                } finally {
                    buildInProgress.set(false)
                }
            }
        }
    }

    actual suspend fun loadApp(): LoadedApp {
        if (!appClassPath.exists()) {
            buildApp()
        }

        logI { "Loading app from directory: $appClassPath" }
        val loader = URLClassLoader(arrayOf(appClassPath.toUri().toURL()), this.javaClass.classLoader)
        ScriptLoader.appScriptLoader = ScriptLoader.ReflectionAppScriptLoader(loader)
        val scriptClasses = examineClasses(loader, appClassPath)

        paths.jsAppScriptsPath?.let { genPath ->
            logI { "Generating Javascript script bindings: $genPath" }
            JsAppScriptsGenerator.generateScriptBindings(scriptClasses.values.toList(), genPath)
        }

        val appClass = loader.loadClass(paths.appMainClass)
        val app = appClass.getDeclaredConstructor().newInstance()
        return LoadedApp(app as EditorAwareApp, scriptClasses)
    }

    private fun examineClasses(loader: URLClassLoader, classpath: Path): Map<KClass<*>, AppScript> {
        val scriptClasses = mutableMapOf<KClass<*>, AppScript>()
        classpath.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach {
            if (!it.isDirectory() && it.name.endsWith(".class")) {
                val className = it.pathString
                    .removePrefix(classpath.pathString)
                    .replace('\\', '/')
                    .removePrefix("/")
                    .removeSuffix(".class")
                    .replace('/', '.')

                try {
                    val scriptClass = loader.loadClass(className)
                    val kclass = scriptClass.kotlin
                    if (KoolScript::class.java.isAssignableFrom(scriptClass.superclass)) {
                        scriptClasses[kclass] = AppScript(kclass, ScriptReflection.getEditableProperties(kclass))
                    }
                } catch (e: Exception) {
                    logE { "Failed examining class $className: $e" }
                }
            }
        }
        return scriptClasses
    }
}