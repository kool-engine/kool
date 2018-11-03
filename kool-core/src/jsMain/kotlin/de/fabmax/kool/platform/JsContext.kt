package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.gl.*
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.*
import kotlin.browser.document
import kotlin.browser.window

/**
 * @author fabmax
 */
@Suppress("UnsafeCastFromDynamic")
class JsContext internal constructor(val props: InitProps) : KoolContext() {
    override val glCapabilities: GlCapabilities

    override val assetMgr = JsAssetManager(props.assetsBaseDir)

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set

    internal val canvas: HTMLCanvasElement
    internal val gl: WebGLRenderingContext

    private var animationMillis = 0.0

    init {
        canvas = document.getElementById(props.canvasName) as HTMLCanvasElement
        // try to get a WebGL2 context first and use WebGL version 1 as fallback
        var webGlCtx = canvas.getContext("webgl2")
        if (webGlCtx == null) {
            webGlCtx = canvas.getContext("experimental-webgl2")
        }
        JsImpl.isWebGl2Context = webGlCtx != null

        // default attributes for minimal WebGL 1 context, are changed depending on available stuff
        val uint32Indices: Boolean
        val depthTextures: Boolean
        val maxTexUnits: Int
        var shaderIntAttribs = false
        var depthComponentIntFormat = GL_DEPTH_COMPONENT
        val depthFilterMethod = GL_NEAREST
        var anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED
        var glslDialect = GlslDialect.GLSL_DIALECT_100
        var glVersion = GlVersion("WebGL", 1, 0)

        if (webGlCtx != null) {
            gl = webGlCtx as WebGL2RenderingContext

            uint32Indices = true
            depthTextures = true
            shaderIntAttribs = true
            depthComponentIntFormat = GL_DEPTH_COMPONENT24
            glslDialect = GlslDialect.GLSL_DIALECT_300_ES
            glVersion = GlVersion("WebGL", 2, 0)
            maxTexUnits = gl.getParameter(GL_MAX_TEXTURE_IMAGE_UNITS).asDynamic()

        } else {
            webGlCtx = canvas.getContext("webgl")
            if (webGlCtx == null) {
                webGlCtx = canvas.getContext("experimental-webgl")
            }
            if (webGlCtx == null) {
                js("alert(\"Unable to initialize WebGL. Your browser may not support it.\")")
            }
            gl = webGlCtx as WebGLRenderingContext

            uint32Indices = gl.getExtension("OES_element_index_uint") != null
            depthTextures = gl.getExtension("WEBGL_depth_texture") != null
            maxTexUnits = gl.getParameter(GL_MAX_TEXTURE_IMAGE_UNITS).asDynamic()
        }

        val extAnisotropic = gl.getExtension("EXT_texture_filter_anisotropic") ?:
        gl.getExtension("MOZ_EXT_texture_filter_anisotropic") ?:
        gl.getExtension("WEBKIT_EXT_texture_filter_anisotropic")
        if (extAnisotropic != null) {
            val max = gl.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) as Float
            anisotropicTexFilterInfo = AnisotropicTexFilterInfo(max, extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT)
        }

        glCapabilities = GlCapabilities(
                uint32Indices,
                shaderIntAttribs,
                maxTexUnits,
                depthTextures,
                depthComponentIntFormat,
                depthFilterMethod,
                anisotropicTexFilterInfo,
                glslDialect,
                glVersion)

        screenDpi = JsImpl.dpi
        windowWidth = canvas.clientWidth
        windowHeight = canvas.clientHeight

        // suppress context menu
        canvas.oncontextmenu = Event::preventDefault

        // install mouse handlers
        canvas.onmousemove = { ev ->
            ev as MouseEvent
            val bounds = canvas.getBoundingClientRect()
            val x = (ev.clientX - bounds.left).toFloat()
            val y = (ev.clientY - bounds.top).toFloat()
            inputMgr.handleMouseMove(x, y)
        }
        canvas.onmousedown = { ev ->
            ev as MouseEvent
            inputMgr.handleMouseButtonStates(ev.buttons.toInt())
        }
        canvas.onmouseup = { ev ->
            ev as MouseEvent
            inputMgr.handleMouseButtonStates(ev.buttons.toInt())
        }
        canvas.onmouseleave = { inputMgr.handleMouseExit() }
        canvas.onwheel = { ev ->
            ev as WheelEvent
            // scroll amount is browser dependent, try to norm it to roughly 1.0 ticks per mouse
            // scroll wheel tick
            var ticks = -ev.deltaY.toFloat() / 3f
            if (ev.deltaMode == 0) {
                // scroll delta is specified in pixels...
                ticks /= 30f
            }
            inputMgr.handleMouseScroll(ticks)
            ev.preventDefault()
        }

        // install touch handlers
        canvas.addEventListener("touchstart", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                inputMgr.handleTouchStart(touch.identifier, touch.elementX, touch.elementY)
            }
        }, false)
        canvas.addEventListener("touchend", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                inputMgr.handleTouchEnd(touch.identifier)
            }
        }, false)
        canvas.addEventListener("touchcancel", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                inputMgr.handleTouchCancel(touch.identifier)
            }
        }, false)
        canvas.addEventListener("touchmove", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                inputMgr.handleTouchMove(touch.identifier, touch.elementX, touch.elementY)
            }
        }, false)

        document.onkeydown = { ev -> handleKeyDown(ev as KeyboardEvent) }
        document.onkeyup = { ev -> handleKeyUp(ev as KeyboardEvent) }

//        if (canvas.tabIndex <= 0) {
//            println("No canvas tabIndex set! Falling back to document key events, this doesn't work with multi context")
//        } else {
//            canvas.onkeydown = { ev -> handleKeyDown(ev as KeyboardEvent) }
//            canvas.onkeyup = { ev -> handleKeyUp(ev as KeyboardEvent) }
//        }
    }

    private fun handleKeyDown(ev: KeyboardEvent) {
        val code = translateKeyCode(ev.code)
        if (code != 0) {
            var mods = 0
            if (ev.altKey) { mods = mods or InputManager.KEY_MOD_ALT }
            if (ev.ctrlKey) { mods = mods or InputManager.KEY_MOD_CTRL }
            if (ev.shiftKey) { mods = mods or InputManager.KEY_MOD_SHIFT }
            if (ev.metaKey) { mods = mods or InputManager.KEY_MOD_SUPER }

            var event = InputManager.KEY_EV_DOWN
            if (ev.repeat) {
                event = event or InputManager.KEY_EV_REPEATED
            }
            inputMgr.keyEvent(code, mods, event)
        }
        if (ev.key.length == 1) {
            inputMgr.charTyped(ev.key[0])
        }

        if (!props.excludedKeyCodes.contains(ev.code)) {
            ev.preventDefault()
        }
    }

    private fun handleKeyUp(ev: KeyboardEvent) {
        val code = translateKeyCode(ev.code)
        if (code != 0) {
            var mods = 0
            if (ev.altKey) { mods = mods or InputManager.KEY_MOD_ALT }
            if (ev.ctrlKey) { mods = mods or InputManager.KEY_MOD_CTRL }
            if (ev.shiftKey) { mods = mods or InputManager.KEY_MOD_SHIFT }
            if (ev.metaKey) { mods = mods or InputManager.KEY_MOD_SUPER }

            inputMgr.keyEvent(code, mods, InputManager.KEY_EV_UP)
        }

        if (!props.excludedKeyCodes.contains(ev.code)) {
            ev.preventDefault()
        }
    }

    private fun translateKeyCode(code: String): Int {
        if (code.length == 4 && code.startsWith("Key")) {
            return code[3].toInt()
        } else {
            return KEY_CODE_MAP[code] ?: 0
        }
    }

    private fun renderFrame(time: Double) {
        // determine delta time
        val dt = (time - animationMillis) / 1000.0
        animationMillis = time

        // update viewport size
        windowWidth = canvas.clientWidth
        windowHeight = canvas.clientHeight
        if (windowWidth != canvas.width || windowHeight!= canvas.height) {
            // resize canvas to viewport
            canvas.width = windowWidth
            canvas.height = windowHeight
        }

        // render frame
        render(dt)
        gl.finish()

        // request next frame
        window.requestAnimationFrame { t -> renderFrame(t) }
    }

    override fun openUrl(url: String) {
        window.open(url)
    }

    override fun run() {
        window.requestAnimationFrame { t -> renderFrame(t) }
    }

    override fun destroy() {
        // nothing to do here...
    }

    class InitProps {
        var canvasName = "glCanvas"
        val excludedKeyCodes: MutableSet<String> = mutableSetOf("F5")

        var assetsBaseDir = "./assets"
    }

    companion object {
        val KEY_CODE_MAP: Map<String, Int> = mutableMapOf(
                "ControlLeft" to InputManager.KEY_CTRL_LEFT,
                "ControlRight" to InputManager.KEY_CTRL_RIGHT,
                "ShiftLeft" to InputManager.KEY_SHIFT_LEFT,
                "ShiftRight" to InputManager.KEY_SHIFT_RIGHT,
                "AltLeft" to InputManager.KEY_ALT_LEFT,
                "AltRight" to InputManager.KEY_ALT_RIGHT,
                "MetaLeft" to InputManager.KEY_SUPER_LEFT,
                "MetaRight" to InputManager.KEY_SUPER_RIGHT,
                "Escape" to InputManager.KEY_ESC,
                "ContextMenu" to InputManager.KEY_MENU,
                "Enter" to InputManager.KEY_ENTER,
                "NumpadEnter" to InputManager.KEY_NP_ENTER,
                "NumpadDivide" to InputManager.KEY_NP_DIV,
                "NumpadMultiply" to InputManager.KEY_NP_MUL,
                "NumpadAdd" to InputManager.KEY_NP_PLUS,
                "NumpadSubtract" to InputManager.KEY_NP_MINUS,
                "Backspace" to InputManager.KEY_BACKSPACE,
                "Tab" to InputManager.KEY_TAB,
                "Delete" to InputManager.KEY_DEL,
                "Insert" to InputManager.KEY_INSERT,
                "Home" to InputManager.KEY_HOME,
                "End" to InputManager.KEY_END,
                "PageUp" to InputManager.KEY_PAGE_UP,
                "PageDown" to InputManager.KEY_PAGE_DOWN,
                "ArrowLeft" to InputManager.KEY_CURSOR_LEFT,
                "ArrowRight" to InputManager.KEY_CURSOR_RIGHT,
                "ArrowUp" to InputManager.KEY_CURSOR_UP,
                "ArrowDown" to InputManager.KEY_CURSOR_DOWN,
                "F1" to InputManager.KEY_F1,
                "F2" to InputManager.KEY_F2,
                "F3" to InputManager.KEY_F3,
                "F4" to InputManager.KEY_F4,
                "F5" to InputManager.KEY_F5,
                "F6" to InputManager.KEY_F6,
                "F7" to InputManager.KEY_F7,
                "F8" to InputManager.KEY_F8,
                "F9" to InputManager.KEY_F9,
                "F10" to InputManager.KEY_F10,
                "F11" to InputManager.KEY_F11,
                "F12" to InputManager.KEY_F12,
                "Space" to ' '.toInt()
        )
    }
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
    val screenX: Float
    val screenY: Float
    val clientX: Float
    val clientY: Float
    val pageX: Float
    val pageY: Float
    val target: Element
    val radiusX: Float
    val radiusY: Float
    val rotationAngle: Float
    val force: Float
}

val Touch.elementX: Float
    get() = clientX - ((target as? HTMLCanvasElement)?.clientLeft?.toFloat() ?: 0f)

val Touch.elementY: Float
    get() = clientY - ((target as? HTMLCanvasElement)?.clientTop?.toFloat() ?: 0f)
