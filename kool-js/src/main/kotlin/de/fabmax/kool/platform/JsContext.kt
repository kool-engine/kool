package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.gl.WebGL2RenderingContext
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import kotlin.browser.document
import kotlin.browser.window

/**
 * @author fabmax
 */
@Suppress("UnsafeCastFromDynamic")
class JsContext internal constructor(val props: InitProps) : RenderContext() {

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set

    override var anisotropicTexFilterInfo = AnisotropicTexFilterInfo(0f, 0)
        private set

    internal val canvas: HTMLCanvasElement
    internal val gl: WebGLRenderingContext
    internal val supportsUint32Indices: Boolean

    private var animationMillis = 0.0

    init {
        canvas = document.getElementById(props.canvasName) as HTMLCanvasElement
        // try to get a WebGL2 context first and use WebGL version 1 as fallback
        var webGlCtx = canvas.getContext("webgl2")
        if (webGlCtx == null) {
            webGlCtx = canvas.getContext("experimental-webgl2")
        }
        JsImpl.isWebGl2Context = webGlCtx != null

        if (webGlCtx != null) {
            println("Using WebGL 2 context")
            gl = webGlCtx as WebGL2RenderingContext
            glCapabilities = GlCapabilities.GL_ES_300

        } else {
            println("Falling back to WebGL 1 context")
            webGlCtx = canvas.getContext("webgl")
            if (webGlCtx == null) {
                webGlCtx = canvas.getContext("experimental-webgl")
            }
            if (webGlCtx == null) {
                js("alert(\"Unable to initialize WebGL. Your browser may not support it.\")")
            }
            gl = webGlCtx as WebGLRenderingContext
            glCapabilities = GlCapabilities.GL_ES_200

            if (gl.getExtension("OES_element_index_uint") != null) {
                glCapabilities.uint32Indices = true
            }
            if (gl.getExtension("WEBGL_depth_texture") != null) {
                glCapabilities.depthTextures = true
            }
        }

//        for (ext in gl.getSupportedExtensions() ?: arrayOf("none")) {
//            println(ext)
//        }

        screenDpi = JsImpl.dpi

        supportsUint32Indices = gl.getExtension("OES_element_index_uint") != null

        val extAnisotropic = gl.getExtension("EXT_texture_filter_anisotropic") ?:
                gl.getExtension("MOZ_EXT_texture_filter_anisotropic") ?:
                gl.getExtension("WEBKIT_EXT_texture_filter_anisotropic")
        if (extAnisotropic != null) {
            val max = gl.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) as Float
            anisotropicTexFilterInfo = AnisotropicTexFilterInfo(max, extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT)
        }

        windowWidth = canvas.clientWidth
        windowHeight = canvas.clientHeight

        canvas.onmousemove = { ev ->
            ev as MouseEvent
            val bounds = canvas.getBoundingClientRect()
            val x = (ev.clientX - bounds.left).toFloat()
            val y = (ev.clientY - bounds.top).toFloat()
            inputMgr.updatePointerPos(InputManager.PRIMARY_POINTER, x, y)
        }
        canvas.onmousedown = { ev ->
            ev as MouseEvent
            inputMgr.updatePointerButtonStates(InputManager.PRIMARY_POINTER, ev.buttons.toInt())
        }
        canvas.onmouseup = { ev ->
            ev as MouseEvent
            inputMgr.updatePointerButtonStates(InputManager.PRIMARY_POINTER, ev.buttons.toInt())
        }

        // suppress context menu
        canvas.oncontextmenu = Event::preventDefault

        canvas.onmouseenter = { inputMgr.updatePointerValid(InputManager.PRIMARY_POINTER, true) }
        canvas.onmouseleave = { inputMgr.updatePointerValid(InputManager.PRIMARY_POINTER, false) }
        canvas.onwheel = { ev ->
            ev as WheelEvent
            // scroll amount is browser dependent, try to norm it to roughly 1.0 ticks per mouse scroll
            // wheel tick
            var ticks = -ev.deltaY.toFloat() / 3f
            if (ev.deltaMode == 0) {
                // scroll delta is specified in pixels...
                ticks /= 30f
            }
            inputMgr.updatePointerScrollPos(InputManager.PRIMARY_POINTER, ticks)
            ev.preventDefault()
        }

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

    override fun run() {
        window.requestAnimationFrame { t -> renderFrame(t) }
    }

    override fun destroy() {
        // nothing to do here...
    }

    class InitProps : RenderContext.InitProps() {
        var canvasName = "glCanvas"
        val excludedKeyCodes: MutableSet<String> = mutableSetOf("F5")
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