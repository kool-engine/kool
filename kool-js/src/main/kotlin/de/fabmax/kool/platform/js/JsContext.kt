package de.fabmax.kool.platform.js

import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.RenderContext
import org.khronos.webgl.WebGLRenderingContext
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
        val dynGl = document.getElementById("glCanvas").asDynamic().getContext("webgl")
        if (dynGl == null) {
            js("alert(\"Unable to initialize WebGL. Your browser may not support it.\")")
        }
        gl = dynGl

        supportsUint32Indices = gl.getExtension("OES_element_index_uint") != null

        // fixme: don't use hardcoded viewport size
        viewportWidth = 800
        viewportHeight = 600
    }

    override fun run() {
        //webGlRender()
        js("setInterval(_.de.fabmax.kool.platform.js.JsContext.Companion.webGlRender, 15);")
    }

    override fun destroy() {
        // nothing to do here...
    }

    class InitProps : RenderContext.InitProps()

}