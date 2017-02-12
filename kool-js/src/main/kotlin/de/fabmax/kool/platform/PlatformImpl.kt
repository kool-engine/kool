package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import de.fabmax.kool.Texture2d
import de.fabmax.kool.TextureResource
import de.fabmax.kool.platform.js.*
import de.fabmax.kool.shading.ShaderProps
import de.fabmax.kool.util.GlslGenerator
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import kotlin.browser.document

/**
 * Javascript / WebGL platform implementation
 *
 * @author fabmax
 */
class PlatformImpl private constructor() : Platform() {

    companion object {
        val MAXIMUM_TEX_WIDTH = 2048
        val MAXIMUM_TEX_HEIGHT = 2048

        internal var jsContext: JsContext? = null
        internal val gl: WebGLRenderingContext
            get() = jsContext?.gl ?: throw KoolException("Platform.createContext() not called")

        private val offscreenCanvas = document.createElement("canvas") as HTMLCanvasElement
        internal val offscreenCanvasCtx: CanvasRenderingContext2D

        init {
            offscreenCanvas.width = MAXIMUM_TEX_WIDTH
            offscreenCanvas.height = MAXIMUM_TEX_HEIGHT
            offscreenCanvasCtx = offscreenCanvas.getContext("2d") as CanvasRenderingContext2D
        }

        fun init() {
            Platform.initPlatform(PlatformImpl())
        }
    }

    override val supportsMultiContext = false

    override val supportsUint32Indices: Boolean
        get() = jsContext?.supportsUint32Indices ?: throw KoolException("Platform.createContext() not called")

    override fun createContext(props: RenderContext.InitProps): RenderContext{
        var ctx = jsContext
        if (ctx == null) {
            if (props is JsContext.InitProps) {
                ctx = JsContext(props)
                jsContext = ctx
            } else {
                throw IllegalArgumentException("Props must be of JsContext.InitProps")
            }
        }
        return ctx
    }

    override fun createDefaultShaderGenerator(): ShaderGenerator {
        return GlslGenerator(object: GlslGenerator.Customization {
            override fun fragmentShaderStart(shaderProps: ShaderProps, text: StringBuilder) {
                text.append("precision highp float;")
            }
        })
    }

    override fun getGlImpl(): GL.Impl {
        return WebGlImpl.instance
    }

    override fun createUint8Buffer(capacity: Int): Uint8Buffer {
        return Uint8BufferImpl(capacity)
    }

    override fun createUint16Buffer(capacity: Int): Uint16Buffer {
        return Uint16BufferImpl(capacity)
    }

    override fun createUint32Buffer(capacity: Int): Uint32Buffer {
        return Uint32BufferImpl(capacity)
    }

    override fun createFloat32Buffer(capacity: Int): Float32Buffer {
        return Float32BufferImpl(capacity)
    }

    override fun currentTimeMillis(): Long {
        return Date().getTime().toLong()
    }

    override fun loadTexture(path: String, props: TextureResource.Props): ImageTexture2d {
        val img = js("new Image();")
        val data = ImageTexture2d(img, props)
        img.src = path
        return data
    }
}

class ImageTexture2d(val image: HTMLImageElement, props: TextureResource.Props) : Texture2d(props) {
    override var isAvailable: Boolean
        get() = image.complete
        set(value) {}

    override fun loadData(target: Int, level: Int, ctx: RenderContext) {
        PlatformImpl.gl.texImage2D(target, level, GL.RGBA, GL.RGBA, GL.UNSIGNED_BYTE, image)

        val size = image.width * image.height * 4
        ctx.memoryMgr.memoryAllocated(res!!, size)
    }
}
