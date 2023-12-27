package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.input.PlatformInputJs
import de.fabmax.kool.pipeline.backend.JsRenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.UIEvent
import org.w3c.files.get
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

/**
 * @author fabmax
 */
class JsContext internal constructor() : KoolContext() {

    override val backend: RenderBackend

    override val isJavascript = true
    override val isJvm = false

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set
    override var isFullscreen
        get() = isFullscreenEnabled
        set(value) {
            if (value != isFullscreenEnabled) {
                if (value) {
                    canvas.requestFullscreen()
                } else {
                    document.exitFullscreen()
                }
            }
        }
    private var isFullscreenEnabled = false

    val canvas: HTMLCanvasElement = document.getElementById(KoolSystem.configJs.canvasName) as HTMLCanvasElement? ?:
            throw IllegalStateException("canvas element not found! Add a canvas with id \"${KoolSystem.configJs.canvasName}\" to your html.")
    private val sysInfo = mutableListOf<String>()

    private var animationMillis = 0.0

    private var canvasFixedWidth = -1
    private var canvasFixedHeight = -1

    init {
        // set canvas style to desired size so that render resolution can be set according to window scale
        if (KoolSystem.configJs.isJsCanvasToWindowFitting) {
            canvas.style.width = "100%"
            canvas.style.height = "100%"
            canvas.width = (window.innerWidth * window.devicePixelRatio).toInt()
            canvas.height = (window.innerHeight * window.devicePixelRatio).toInt()
        } else {
            canvasFixedWidth = canvas.width
            canvasFixedHeight = canvas.height
            canvas.style.width = "${canvasFixedWidth}px"
            canvas.style.height = "${canvasFixedHeight}px"
            canvas.width = (canvasFixedWidth * window.devicePixelRatio).roundToInt()
            canvas.height = (canvasFixedHeight * window.devicePixelRatio).roundToInt()
        }

        backend = RenderBackendGlImpl(this, canvas)
        //backend = RenderBackendWebGpu(this, canvas)

        document.onfullscreenchange = {
            isFullscreenEnabled = document.fullscreenElement != null
            null
        }
        window.onbeforeunload = { e ->
            if (applicationCallbacks.onWindowCloseRequest(this)) {
                // proceed with closing page, delete return value to prevent unwanted popups
                js("delete e['returnValue'];")
            } else {
                e.preventDefault()
                e.returnValue = "Are you sure you want to exit?"
            }
            null
        }
        window.onfocus = {
            isWindowFocused = true
            null
        }
        window.onblur = {
            isWindowFocused = false
            null
        }

        window.ondragenter = {
            it.preventDefault()
        }
        window.ondragover = {
            it.preventDefault()
        }
        window.ondrop = { e ->
            e.dataTransfer?.files?.let { fileList ->
                val dropFiles = mutableListOf<LoadableFile>()
                for (i in 0 until fileList.length) {
                    fileList[i]?.let { dropFiles += LoadableFileImpl(it) }
                }
                if (dropFiles.isNotEmpty()) {
                    applicationCallbacks.onFileDrop(dropFiles)
                }
            }
            e.preventDefault()
        }

        windowScale = window.devicePixelRatio.toFloat()
        windowWidth = canvas.width
        windowHeight = canvas.height

        // suppress context menu
        canvas.oncontextmenu = Event::preventDefault

        PlatformInputJs.onContextCreated(this)
        KoolSystem.onContextCreated(this)
    }

    internal fun renderFrame(time: Double) {
        RenderLoopCoroutineDispatcher.executeDispatchedTasks()

        // determine delta time
        val dt = (time - animationMillis) / 1000.0
        animationMillis = time

        // update viewport size according to window scale
        windowScale = window.devicePixelRatio.toFloat()
        if (KoolSystem.configJs.isJsCanvasToWindowFitting) {
            windowWidth = (window.innerWidth * window.devicePixelRatio).toInt()
            windowHeight = (window.innerHeight * window.devicePixelRatio).toInt()
        } else {
            windowWidth = (canvasFixedWidth * window.devicePixelRatio).toInt()
            windowHeight = (canvasFixedHeight * window.devicePixelRatio).toInt()
        }
        if (windowWidth != canvas.width || windowHeight != canvas.height) {
            // resize canvas to viewport, this only affects the render resolution, actual canvas size is determined
            // by canvas.style.width / canvas.style.height set on init
            canvas.width = windowWidth
            canvas.height = windowHeight
        }

        // render frame
        render(dt)
        backend.renderFrame(this)

        // request next frame
        window.requestAnimationFrame { t -> renderFrame(t) }
    }

    override fun openUrl(url: String, sameWindow: Boolean) {
        if (sameWindow) {
            window.open(url, "_self")
        } else {
            window.open(url)
        }
    }

    override fun run() {
        Loader.launch {
            KoolSystem.configJs.loaderTasks.forEach { it() }
            (backend as JsRenderBackend).startRenderLoop()
        }
    }

    override fun getSysInfos(): List<String> {
        return sysInfo
    }

    class InitProps {
        var canvasName = "glCanvas"
        val excludedKeyCodes: MutableSet<String> = mutableSetOf("F5", "F11")

        var localAssetPath = "./assets"
        val customFonts = mutableMapOf<String, String>()
    }
}

private object Loader : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()
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

val Touch.elementX: Double
    get() = clientX - ((target as? HTMLCanvasElement)?.clientLeft?.toDouble() ?: 0.0)

val Touch.elementY: Double
    get() = clientY - ((target as? HTMLCanvasElement)?.clientTop?.toDouble() ?: 0.0)
