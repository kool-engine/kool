package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.input.PlatformInputJs
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGl
import de.fabmax.kool.util.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Window
import org.w3c.dom.events.Event
import org.w3c.dom.events.UIEvent
import org.w3c.files.get
import kotlin.math.min
import kotlin.math.roundToInt

@JsName("createJsContext")
suspend fun JsContext(): JsContext {
    val config = KoolSystem.configJs
    val canvas: HTMLCanvasElement = document.getElementById(config.canvasName) as HTMLCanvasElement? ?:
            throw IllegalStateException("canvas element not found! Add a canvas with id \"${config.canvasName}\" to your html.")

    val ctx = JsContext(canvas, config)
    ctx.createBackend()
    return ctx
}

private val browserWindow: Window get() = window

class JsContext internal constructor(canvas: HTMLCanvasElement, val config: KoolConfigJs) : KoolContext() {
    override lateinit var backend: RenderBackend
        private set

    override val window: JsWindow = JsWindow(canvas, config)

    private val sysInfo = mutableListOf<String>()

    internal suspend fun createBackend() {
        val backendProvider = config.renderBackend
        val backendResult = backendProvider.createBackend(this@JsContext)

        backend = when {
            backendResult.isSuccess -> backendResult.getOrThrow()
            config.useWebGlFallback && backendProvider != RenderBackendGl -> {
                logE { "Failed creating render backend ${backendProvider.displayName}: ${backendResult.exceptionOrNull()}\nFalling back to WebGL2" }
                RenderBackendGl.createBackend(this@JsContext).getOrThrow()
            }
            else -> error("Failed creating render backend ${backendProvider.displayName}: ${backendResult.exceptionOrNull()}")
        }

        PlatformInputJs.onContextCreated(this)
        KoolSystem.onContextCreated(this)
    }

    private suspend fun renderFrame(time: Double) {
        // update viewport size according to window scale
        window.updateCanvasSize()

        // render frame
        val frameData = render()
        frameData.syncData()
        incrementFrameTime(time / 1000.0)
        KoolDispatchers.Backend.executeDispatchedTasks()
        backend.renderFrame(frameData, this)
        requestAnimationFrame()
    }

    override fun openUrl(url: String, sameWindow: Boolean) {
        if (sameWindow) {
            browserWindow.open(url, "_self")
        } else {
            browserWindow.open(url)
        }
    }

    override fun run() {
        requestAnimationFrame()
    }

    private fun requestAnimationFrame() {
        browserWindow.requestAnimationFrame { t ->
            ApplicationScope.launch {
                renderFrame(t)
            }
        }
    }

    override fun getSysInfos(): List<String> {
        return sysInfo
    }
}

class JsWindow(val canvas: HTMLCanvasElement, val config: KoolConfigJs) : KoolWindow {

    override val parentScreenScale: Float
        get() = min(config.deviceScaleLimit, browserWindow.devicePixelRatio).toFloat()

    override var positionInScreen: Vec2i = Vec2i.ZERO
        set(value) {
            logE { "JsWindow position cannot be set" }
        }

    override var sizeOnScreen: Vec2i
        get() = Vec2i((framebufferSize.x / parentScreenScale).roundToInt(), (framebufferSize.y / parentScreenScale).roundToInt())
        set(value) {
            logE { "JsWindow size cannot be set" }
        }

    override var renderResolutionFactor: Float = 1f
        set(value) {
            if (value != field) {
                field = value
                updateUiScales()
            }
        }

    override var framebufferSize: Vec2i = Vec2i(canvas.width, canvas.height); private set

    override var size: Vec2i = Vec2i(canvas.width, canvas.height); private set

    override val renderScale: Float
        get() = parentScreenScale * renderResolutionFactor

    override var title: String
        get() = document.title
        set(value) {
            document.title = value
        }

    private var _flags = WindowFlags()
        set(value) {
            if (value != field) {
                val oldFlags = field
                field = value
                flagListeners.updated().forEach { it.onFlagsChanged(oldFlags, value) }
            }
        }
    override var flags: WindowFlags
        get() = _flags
        set(value) {
            if (value != _flags) {
                applyFlags(_flags, value)
                _flags = value
            }
        }

    override val capabilities: WindowCapabilities = WindowCapabilities(
        canSetSize = false,
        canSetPosition = false,
        canSetFullscreen = true,
        canMaximize = false,
        canMinimize = false,
        canSetVisibility = false,
        canSetTitle = true,
        canHideTitleBar = false
    )

    override val resizeListeners: BufferedList<WindowResizeListener> = BufferedList()
    override val scaleChangeListeners: BufferedList<ScaleChangeListener> = BufferedList()
    override val flagListeners: BufferedList<WindowFlagsListener> = BufferedList()
    override val closeListeners: BufferedList<WindowCloseListener> = BufferedList()
    override val dragAndDropListeners: BufferedList<DragAndDropListener> = BufferedList()

    override var windowTitleHoverHandler: WindowTitleHoverHandler = WindowTitleHoverHandler()

    private var canvasFixedWidth = -1
    private var canvasFixedHeight = -1

    init {
        // set canvas style to desired size so that render resolution can be set according to window scale
        if (config.isJsCanvasToWindowFitting) {
            canvas.style.width = "100%"
            canvas.style.height = "100%"
            canvas.width = (browserWindow.innerWidth * parentScreenScale).toInt()
            canvas.height = (browserWindow.innerHeight * parentScreenScale).toInt()
        } else {
            canvasFixedWidth = canvas.width
            canvasFixedHeight = canvas.height
            canvas.style.width = "${canvasFixedWidth}px"
            canvas.style.height = "${canvasFixedHeight}px"
            canvas.width = (canvasFixedWidth * parentScreenScale).roundToInt()
            canvas.height = (canvasFixedHeight * parentScreenScale).roundToInt()
        }
        updateFramebufferSize()
        updateUiScales()

        canvas.oncontextmenu = Event::preventDefault
        document.onfullscreenchange = {
            _flags = _flags.copy(isFullscreen = document.fullscreenElement != null)
            null
        }
        browserWindow.onfocus = {
            _flags = _flags.copy(isFocused = true)
            null
        }
        browserWindow.onblur = {
            _flags = _flags.copy(isFocused = true)
            null
        }
        browserWindow.onbeforeunload = { e ->
            if (closeListeners.updated().any { !it.onCloseRequest() }) {
                logD { "Window close request was suppressed by application callback" }
                e.preventDefault()
                e.returnValue = "Are you sure you want to exit?"
            } else {
                // proceed with closing page, delete return value to prevent unwanted popups
                js("delete e['returnValue'];")
            }
            null
        }

        browserWindow.ondragenter = {
            it.preventDefault()
        }
        browserWindow.ondragover = {
            it.preventDefault()
        }
        browserWindow.ondrop = { e ->
            e.dataTransfer?.files?.let { fileList ->
                val dropFiles = mutableListOf<LoadableFile>()
                for (i in 0 until fileList.length) {
                    fileList[i]?.let { dropFiles += LoadableFileImpl(it) }
                }
                if (dropFiles.isNotEmpty()) {
                    dragAndDropListeners.forEach { it.onFileDrop(dropFiles) }
                }
            }
            e.preventDefault()
        }
    }

    private fun applyFlags(oldFlags: WindowFlags, newFlags: WindowFlags) {
        if (oldFlags.isFullscreen != newFlags.isFullscreen) {
            if (newFlags.isFullscreen) canvas.requestFullscreen() else document.exitFullscreen()
        }
    }

    private fun updateFramebufferSize() {
        val x: Int
        val y: Int
        if (config.isJsCanvasToWindowFitting) {
            x = (browserWindow.innerWidth * parentScreenScale * renderResolutionFactor).toInt()
            y = (browserWindow.innerHeight * parentScreenScale * renderResolutionFactor).toInt()
        } else {
            x = (canvasFixedWidth * parentScreenScale * renderResolutionFactor).toInt()
            y = (canvasFixedHeight * parentScreenScale * renderResolutionFactor).toInt()
        }
        if (framebufferSize.x != x || framebufferSize.y != y) {
            framebufferSize = Vec2i(x, y)
            size = framebufferSize
            resizeListeners.updated().forEach { it.onResize(size) }
        }
    }

    private fun updateUiScales() {
        UiScale.updateUiScaleFromWindowScale(renderScale)
        scaleChangeListeners.updated().forEach { it.onScaleChanged(renderScale) }
    }

    internal fun updateCanvasSize() {
        updateFramebufferSize()
        if (framebufferSize.x != canvas.width || framebufferSize.y != canvas.height) {
            // resize canvas to viewport, this only affects the render resolution, actual canvas size is determined
            // by canvas.style.width / canvas.style.height set on init
            canvas.width = framebufferSize.x
            canvas.height = framebufferSize.y
            updateUiScales()
        }
    }

    override fun close() { }
}

external class TouchEvent: UIEvent {
    val altKey: Boolean
    val changedTouches: TouchList
    val ctrlKey: Boolean
    val metaKey: Boolean
    val shiftKey: Boolean
    val targetTouches: TouchList
    val touches: TouchList
}

external class TouchList {
    val length: Int
    fun item(index: Int): Touch
}

external class Touch {
    val identifier: Int
    val screenX: Double
    val screenY: Double
    val clientX: Double
    val clientY: Double
    val pageX: Double
    val pageY: Double
    val target: Element
    val radiusX: Double
    val radiusY: Double
    val rotationAngle: Double
    val force: Double
}
