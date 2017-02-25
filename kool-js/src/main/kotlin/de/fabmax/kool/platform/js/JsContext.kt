package de.fabmax.kool.platform.js

import de.fabmax.kool.InputHandler
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.RenderContext
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import kotlin.browser.document
import kotlin.browser.window

/**
 * @author fabmax
 */
@Suppress("UnsafeCastFromDynamic")
class JsContext internal constructor(props: InitProps) : RenderContext() {

    internal val canvas: HTMLCanvasElement
    internal val gl: WebGLRenderingContext
    internal val supportsUint32Indices: Boolean

    private var animationMillis = 0.0

    init {
        canvas = document.getElementById(props.canvasName) as HTMLCanvasElement
        var webGlCtx = canvas.getContext("webgl")
        if (webGlCtx == null) {
            webGlCtx = canvas.getContext("experimental-webgl")
            if (webGlCtx == null) {
                js("alert(\"Unable to initialize WebGL. Your browser may not support it.\")")
            }
        }
        screenDpi = PlatformImpl.dpi

        gl = webGlCtx as WebGLRenderingContext
        supportsUint32Indices = gl.getExtension("OES_element_index_uint") != null

        viewportWidth = canvas.clientWidth
        viewportHeight = canvas.clientHeight

        canvas.onmousemove = { ev ->
            ev as MouseEvent
            val bounds = canvas.getBoundingClientRect()
            val x = (ev.clientX - bounds.left).toFloat()
            val y = (ev.clientY - bounds.top).toFloat()
            inputHandler.updatePointerPos(InputHandler.PRIMARY_POINTER, x, y)
        }
        canvas.onmousedown = { ev ->
            ev as MouseEvent
            inputHandler.updatePointerButtonStates(InputHandler.PRIMARY_POINTER, ev.buttons.toInt())
        }
        canvas.onmouseup = { ev ->
            ev as MouseEvent
            inputHandler.updatePointerButtonStates(InputHandler.PRIMARY_POINTER, ev.buttons.toInt())
        }
        canvas.onmouseenter = { ev ->
            inputHandler.updatePointerValid(InputHandler.PRIMARY_POINTER, true)
        }
        canvas.onmouseleave = { ev ->
            inputHandler.updatePointerValid(InputHandler.PRIMARY_POINTER, false)
        }
        canvas.onwheel = { ev ->
            ev as WheelEvent
            // scroll amount is browser dependent, try to norm it to roughly 1.0 ticks per mouse scroll
            // wheel tick
            var ticks = -ev.deltaY.toFloat() / 3f
            if (ev.deltaMode == 0) {
                // scroll delta is specified in pixels...
                ticks /= 30f
            }
            inputHandler.updatePointerScrollPos(InputHandler.PRIMARY_POINTER, ticks)
            // mark wheel event as handled to prevent scrolling the page
            // unfortunately Kotlin event handler signature returns Unit, but this does the trick...
            js("return false;")
        }

    }

    private fun renderFrame(time: Double) {
        // determine delta time
        val dt = (time - animationMillis) / 1000.0
        animationMillis = time

        // update viewport size
        viewportWidth = canvas.clientWidth
        viewportHeight = canvas.clientHeight
        if (viewportWidth != canvas.width || viewportHeight!= canvas.height) {
            // resize canvas to viewport
            canvas.width = viewportWidth
            canvas.height = viewportHeight
        }

        // render frame
        render(dt.toFloat())
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
    }

}