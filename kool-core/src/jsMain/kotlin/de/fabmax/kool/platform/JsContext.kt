package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.input.PlatformInputJs
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.pipeline.backend.wgpu.JsRenderBackendWebGpu
import de.fabmax.kool.pipeline.backend.wgpu.createWGPURenderBackend
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import de.fabmax.kool.util.logW
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.UIEvent
import org.w3c.files.get
import kotlin.coroutines.CoroutineContext
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author fabmax
 */
@OptIn(DelicateCoroutinesApi::class)
class JsContext internal constructor() : KoolContext() {

    override lateinit var backend: RenderBackendJs
        private set

    override var renderScale: Float = KoolSystem.configJs.renderScale
        set(value) {
            field = value
            windowScale = pixelRatio.toFloat() * value
        }

    val pixelRatio: Double
        get() = min(KoolSystem.configJs.deviceScaleLimit, window.devicePixelRatio)

    private val canvasSize = MutableVec2i(0, 0)
    override val windowWidth: Int get() = (canvasSize.x * renderScale).toInt()
    override val windowHeight: Int get() = (canvasSize.y * renderScale).toInt()

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
            canvas.width = (window.innerWidth * pixelRatio).toInt()
            canvas.height = (window.innerHeight * pixelRatio).toInt()
        } else {
            canvasFixedWidth = canvas.width
            canvasFixedHeight = canvas.height
            canvas.style.width = "${canvasFixedWidth}px"
            canvas.style.height = "${canvasFixedHeight}px"
            canvas.width = (canvasFixedWidth * pixelRatio).roundToInt()
            canvas.height = (canvasFixedHeight * pixelRatio).roundToInt()
        }

        GlobalScope.launch {
            backend = when (KoolSystem.configJs.renderBackend) {
                KoolConfigJs.Backend.WEB_GL2 -> RenderBackendGlImpl(this@JsContext, canvas)
                KoolConfigJs.Backend.WEB_GPU -> createWGPURenderBackend(this@JsContext, canvas)
                KoolConfigJs.Backend.PREFER_WEB_GPU -> {
                    if (JsRenderBackendWebGpu.isSupported()) {
                        createWGPURenderBackend(this@JsContext, canvas)
                    } else {
                        RenderBackendGlImpl(this@JsContext, canvas)
                    }
                }
            }
        }

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

        windowScale = pixelRatio.toFloat() * renderScale
        canvasSize.set(canvas.width, canvas.height)

        // suppress context menu
        canvas.oncontextmenu = Event::preventDefault

        PlatformInputJs.onContextCreated(this)
        KoolSystem.onContextCreated(this)
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal suspend fun renderFrame(time: Double) {
        RenderLoopCoroutineDispatcher.executeDispatchedTasks()

        // determine delta time
        val dt = (time - animationMillis) / 1000.0
        animationMillis = time

        // update viewport size according to window scale
        windowScale = pixelRatio.toFloat() * renderScale
        if (KoolSystem.configJs.isJsCanvasToWindowFitting) {
            canvasSize.set(
                (window.innerWidth * pixelRatio).toInt(),
                (window.innerHeight * pixelRatio).toInt(),
            )
        } else {
            canvasSize.set(
                (canvasFixedWidth * pixelRatio).toInt(),
                (canvasFixedHeight * pixelRatio).toInt(),
            )
        }
        if (canvasSize.x != canvas.width || canvasSize.y != canvas.height) {
            // resize canvas to viewport, this only affects the render resolution, actual canvas size is determined
            // by canvas.style.width / canvas.style.height set on init
            canvas.width = canvasSize.x
            canvas.height = canvasSize.y
            onWindowSizeChanged.updated().forEach { it(this) }
        }

        // render frame
        render(dt)
        backend.renderFrame(this)

        // request next frame
        window.requestAnimationFrame { t ->
            GlobalScope.launch {
                renderFrame(t)
            }
        }
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

            try {
                backend.startRenderLoop()
            } catch (e: Exception) {
                if (backend is JsRenderBackendWebGpu && KoolSystem.configJs.renderBackend == KoolConfigJs.Backend.PREFER_WEB_GPU) {
                    // WebGPU-context creation failed (although the browser theoretically supports it)

                    // fixme: KoolContext.run() is called relatively late and user code might have already done a lot
                    //  of stuff by now, so recreating the backend at this point seems risky but there isn't much we
                    //  can do about that.

                    logW { "Failed initializing WebGPU context, falling back to WebGL: $e" }
                    backend = RenderBackendGlImpl(this@JsContext, canvas)
                    backend.startRenderLoop()
                } else {
                    throw RuntimeException(e)
                }
            }
        }
    }

    override fun getSysInfos(): List<String> {
        return sysInfo
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
