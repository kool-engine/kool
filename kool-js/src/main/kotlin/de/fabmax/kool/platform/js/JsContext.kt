package de.fabmax.kool.platform.js

import de.fabmax.kool.InputHandler
import de.fabmax.kool.KoolException
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.RenderContext
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import kotlin.browser.document

/**
 * @author fabmax
 */
@Suppress("UnsafeCastFromDynamic")
class JsContext internal constructor(props: InitProps) : RenderContext() {

    companion object {
        /**
         * Javascript callback function: Renders a single frame
         */
        fun webGlRender() {
            PlatformImpl.jsContext!!.render()
            PlatformImpl.gl.finish()
        }
    }

    internal val gl: WebGLRenderingContext
    internal val supportsUint32Indices: Boolean

    init {
        val canvas: HTMLCanvasElement = document.getElementById(props.canvasName) as HTMLCanvasElement
        var webGlCtx = canvas.getContext("webgl")
        if (webGlCtx == null) {
            webGlCtx = canvas.getContext("experimental-webgl")
            if (webGlCtx == null) {
                js("alert(\"Unable to initialize WebGL. Your browser may not support it.\")")
            }
        }

        gl = webGlCtx as WebGLRenderingContext
        supportsUint32Indices = gl.getExtension("OES_element_index_uint") != null

        // fixme: don't use hardcoded viewport size
        viewportWidth = 800
        viewportHeight = 600

        canvas.onmousemove = { ev ->
            ev as MouseEvent
            val bounds = canvas.getBoundingClientRect()
            inputHandler.updatePointerPos(InputHandler.PRIMARY_POINTER,
                    ev.clientX - bounds.left, ev.clientY - bounds.top)
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
            var ticks = ev.deltaY / 3.0
            if (ev.deltaMode == 0) {
                // scroll delta is specified in pixels...
                ticks /= 30
            }
            inputHandler.updatePointerScrollPos(InputHandler.PRIMARY_POINTER, ticks)
            // mark wheel event as handled to prevent scrolling the page
            // unfortunately Kotlin event handler signature returns Unit, but this does the trick...
            js("return false;")
        }

    }

    override fun run() {
        //webGlRender()
        js("setInterval(_.de.fabmax.kool.platform.js.JsContext.Companion.webGlRender, 15);")
    }

    override fun destroy() {
        // nothing to do here...
    }

    class InitProps : RenderContext.InitProps() {
        var canvasName = "glCanvas"
    }

}