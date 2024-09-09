package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.BehaviorLoader
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.api.EditorProject
import de.fabmax.kool.editor.api.KoolBehavior
import de.fabmax.kool.modules.filesystem.FileSystemFile
import de.fabmax.kool.modules.filesystem.FileSystemWatcher
import de.fabmax.kool.modules.filesystem.PhysicalFileSystem
import de.fabmax.kool.modules.filesystem.getFileOrNull
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.*
import kotlin.reflect.KClass

actual fun AppLoadService(projectFiles: ProjectFiles): AppLoadService = AppLoadServiceImpl(projectFiles)

class AppLoadServiceImpl(private val projectFiles: ProjectFiles) : AppLoadService, CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()

    private val physFs: PhysicalFileSystem? = projectFiles.fileSystem as? PhysicalFileSystem
    private val buildInProgress = AtomicBoolean(false)
    private val appSourcesChanged = AtomicBoolean(false)

    private val ignoredPaths = mutableSetOf<Path>()

    private val changeListeners = mutableListOf<AppSourcesChangeListener>()

    private val fsWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) = checkIfSourceFileChanged(file)
        override fun onFileChanged(file: FileSystemFile) = checkIfSourceFileChanged(file)
        override fun onFileDeleted(file: FileSystemFile) = checkIfSourceFileChanged(file)

        private fun checkIfSourceFileChanged(file: FileSystemFile) {
            // only trigger app reload if an actual source file has changed
            // valid source file need to be kotlin files be somewhere under /src/ - this excludes assets under /src/*/resources/
            if (file is PhysicalFileSystem.File) {
                if (file.path.startsWith("/src/") &&
                    file.path.endsWith(".kt") &&
                    file.path.removePrefix("/") != JS_BEHAVIOR_GEN_OUTPUT &&
                    !appSourcesChanged.getAndSet(true)
                ) {
                    logD { "App sources changed" }
                    changeListeners.forEach { it.onAppSourcesChanged() }
                }
            }
        }
    }

    init {
        if (physFs == null) {
            logW { "Project file system is not physical, dynamic app building and loading will not work." }
        }
        projectFiles.fileSystem.addFileSystemWatcher(fsWatcher)
    }

    override fun addChangeListener(listener: AppSourcesChangeListener) {
        changeListeners += listener
    }

    override suspend fun buildApp() {
        val buildGradle = physFs?.getFileOrNull(BUILD_GRADLE) as PhysicalFileSystem.File?
        if (buildGradle == null) {
            logI { "build.gradle.kts not found, unable to build app" }
            return
        }

        if (buildInProgress.getAndSet(true)) {
            logW("AppLoader.loader") { "Build is already in progress" }
            return
        }

        logI("AppLoader.loader") { "Executing gradle build" }
        suspendCoroutine { continuation ->
            thread {
                try {
                    val isWindows = "windows" in System.getProperty("os.name").lowercase()
                    val gradleDir = buildGradle.physPath.parent.toFile().canonicalFile
                    val gradlewCmd = if (isWindows) "$gradleDir\\gradlew.bat" else "$gradleDir/gradlew"
                    logI { "Building app: $gradlewCmd $GRADLE_BUILD_TASK, working dir: $gradleDir" }

                    val buildProcess = ProcessBuilder()
                        .command(gradlewCmd, GRADLE_BUILD_TASK)
                        .directory(gradleDir)
                        .start()
                    thread {
                        BufferedReader(InputStreamReader(buildProcess.inputStream)).lines().forEach {
                            if (it.isNotEmpty()) {
                                logD("gradle") { "  $it" }
                            }
                        }
                    }
                    thread {
                        BufferedReader(InputStreamReader(buildProcess.errorStream)).lines().forEach {
                            logE("gradle") { "  $it" }
                        }
                    }
                    val exitCode = buildProcess.waitFor()
                    logI { "Gradle build finished (exit code: $exitCode)" }

                    if (exitCode == 0) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWith(Result.failure(IllegalStateException("Build failed")))
                    }
                } finally {
                    appSourcesChanged.set(false)
                    buildInProgress.set(false)
                }
            }
        }
    }

    override suspend fun loadApp(): LoadedApp {
        if (physFs == null) {
            return LoadedApp(EmptyApp(), emptyMap())
        }

        val buildClasses = physFs.rootPath.resolve(BUILD_OUTPUT_CLASSES)
        if (!buildClasses.exists()) {
            buildApp()
            if (!buildClasses.exists()) {
                logE { "Built app, but classes not found at $BUILD_OUTPUT_CLASSES" }
                return LoadedApp(EmptyApp(), emptyMap())
            }
        }

        logD { "Loading app from directory: $buildClasses" }
        val loader = ReloadingClassLoader(arrayOf(buildClasses.toUri().toURL()), this::class.java.classLoader)
        BehaviorLoader.appBehaviorLoader = BehaviorLoader.ReflectionAppBehaviorLoader(loader)
        val behaviorClasses = examineClasses(loader, buildClasses)

        val jsGenOutput = physFs.rootPath.resolve(JS_BEHAVIOR_GEN_OUTPUT).pathString
        logD { "Generating Javascript behavior bindings: $jsGenOutput" }
        JsAppBehaviorBindingsGenerator.generateBehaviorBindings(behaviorClasses.values.toList(), jsGenOutput)

        val appClass = loader.loadClass(projectFiles.appMainClass)
        val app = appClass.getDeclaredConstructor().newInstance()
        return LoadedApp(app as EditorAwareApp, behaviorClasses)
    }

    @OptIn(ExperimentalPathApi::class)
    private fun examineClasses(loader: ReloadingClassLoader, classpath: Path): Map<KClass<*>, AppBehavior> {
        val behaviorClasses = mutableMapOf<KClass<*>, AppBehavior>()
        classpath.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach {
            if (!it.isDirectory() && it.name.endsWith(".class")) {
                val className = it.pathString
                    .removePrefix(classpath.pathString)
                    .replace('\\', '/')
                    .removePrefix("/")
                    .removeSuffix(".class")
                    .replace('/', '.')

                try {
                    val behaviorClass = loader.reloadClass(className)
                    val kclass = behaviorClass.kotlin
                    val superClass = behaviorClass.superclass
                    if (superClass != null && KoolBehavior::class.java.isAssignableFrom(superClass)) {
                        val simple = kclass.simpleName ?: "<unknown>"
                        val qualified = kclass.qualifiedName ?: "<unknown>"
                        behaviorClasses[kclass] = AppBehavior(simple, qualified, BehaviorReflection.getEditableProperties(kclass))
                    }
                } catch (e: Exception) {
                    logE { "Failed examining class $className: $e" }
                    e.printStackTrace()
                }
            }
        }
        return behaviorClasses
    }

    private class ReloadingClassLoader(urls: Array<URL>, parent: ClassLoader) : URLClassLoader(urls, parent) {
        fun reloadClass(name: String): Class<*> {
            return findLoadedClass(name) ?: findClass(name)
        }
    }

    companion object {
        private const val BUILD_GRADLE = "build.gradle.kts"
        private const val GRADLE_BUILD_TASK = "jvmMainClasses"
        private const val BUILD_OUTPUT_CLASSES = "build/classes/kotlin/jvm/main"
        private const val JS_BEHAVIOR_GEN_OUTPUT = "src/jsMain/kotlin/BehaviorBindings.kt"
    }

    private class EmptyApp : EditorAwareApp {
        override suspend fun loadApp(projectModel: EditorProject, ctx: KoolContext) {
            projectModel.createScenes()
        }
    }
}