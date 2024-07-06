package de.fabmax.kool.editor

import de.fabmax.kool.*
import de.fabmax.kool.editor.ui.OkCancelBrowsePathDialog
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.modules.filesystem.PhysicalFileSystem
import de.fabmax.kool.modules.filesystem.getDirectoryOrNull
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.pathString

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlatformFunctions {

    actual val windowButtonStyle: WindowButtonStyle get() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        return if (ctx.backend.glfwWindow.isHiddenTitleBar) WindowButtonStyle.WINDOWS else WindowButtonStyle.NONE
    }

    actual val isWindowMaximized: Boolean get() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        return ctx.backend.glfwWindow.isMaximized
    }

    actual fun onEditorStarted(ctx: KoolContext) {
        ctx as Lwjgl3Context
        val wnd = ctx.backend.glfwWindow

        val posX = KeyValueStore.getInt("editor.window.posX", -1)
        val posY = KeyValueStore.getInt("editor.window.posY", -1)
        if (posX != -1 && posY != -1) {
            wnd.setWindowPos(posX, posY)
        }

        val width = KeyValueStore.getInt("editor.window.width", KoolSystem.configJvm.windowSize.x)
        val height = KeyValueStore.getInt("editor.window.height", KoolSystem.configJvm.windowSize.y)
        wnd.setWindowSize(width, height)

        val isMaximized = KeyValueStore.getBoolean("editor.window.isMaximized", false)
        if (isMaximized) {
            wnd.isMaximized = true
        }
        wnd.isVisible = true
    }

    actual fun onExit(ctx: KoolContext) {
        ctx as Lwjgl3Context
        val wnd = ctx.backend.glfwWindow

        if (wnd.isMaximized) {
            KeyValueStore.setBoolean("editor.window.isMaximized", true)
        } else {
            KeyValueStore.setBoolean("editor.window.isMaximized", false)
            KeyValueStore.setInt("editor.window.posX", wnd.windowPosX)
            KeyValueStore.setInt("editor.window.posY", wnd.windowPosY)
            KeyValueStore.setInt("editor.window.width", wnd.windowWidth)
            KeyValueStore.setInt("editor.window.height", wnd.windowHeight)
        }
    }

    actual fun editBehavior(behaviorClassName: String) {
        val srcDir = KoolEditor.instance.projectFiles.fileSystem.getDirectoryOrNull("src/commonMain/kotlin")
        val srcPath = (srcDir as? PhysicalFileSystem.Directory?)?.physPath?.pathString ?: ""
        val classPath = behaviorClassName.replace('.', '/')
        val behaviorSourcePath = "${srcPath}/${classPath}.kt"

        val behaviorPath = File(behaviorSourcePath).canonicalPath
        logD { "Edit behavior source: $behaviorPath" }

        val ideaPath = KeyValueStore.loadString("editor.idea.path") ?: "idea64"

        try {
            ProcessBuilder()
                .command(ideaPath, behaviorSourcePath)
                .start()
        } catch (e: IOException) {
            logW { "IntelliJ executable not found" }

            OkCancelBrowsePathDialog("Select IntelliJ Path (idea64)", "idea64", "path/to/idea64") { path ->
                KeyValueStore.storeString("editor.idea.path", path)
                editBehavior(behaviorSourcePath)
            }

        } catch (e: Exception) {
            logE { "Failed launching IntelliJ: ${e.message}" }
        }
    }

    actual suspend fun chooseFilePath(): String? {
        val result = Assets.loadFileByUser()
        if (result.isNotEmpty()) {
            return (result[0] as LoadableFileImpl).file.path
        }
        return null
    }

    actual fun saveProjectBlocking() {
        runBlocking {
            KoolEditor.instance.saveProject()
        }
    }

    private val startPointerScreen = MutableVec2i()
    private val startWindow = MutableVec2i()

    actual fun toggleMaximizeWindow() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        ctx.backend.glfwWindow.isMaximized = !ctx.backend.glfwWindow.isMaximized
    }

    actual fun minimizeWindow() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        ctx.backend.glfwWindow.isMinimized = true
    }

    actual fun closeWindow() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        ctx.backend.glfwWindow.closeWindow()
    }
}

fun KoolEditor(projectRoot: String, ctx: KoolContext): KoolEditor {
    return runBlocking {
        val rootPath = Path(projectRoot)
        if (rootPath.notExists()) {
            logW { "Project root path does not exist, creating: $projectRoot" }
            rootPath.createDirectories()
        }
        val fs = PhysicalFileSystem(
            rootPath,
            excludePaths = setOf(
                rootPath.resolve(".editor"),
                rootPath.resolve(".gradle"),
                rootPath.resolve(".httpCache"),
                rootPath.resolve(".idea"),
                rootPath.resolve(".kotlin"),
                rootPath.resolve("build"),
                rootPath.resolve("dist"),
                rootPath.resolve("kotlin-js-store")
            ),
            isLaunchWatchService = true
        )
        val projFiles = ProjectFiles(fs)
        KoolEditor(projFiles, ctx)
    }
}
