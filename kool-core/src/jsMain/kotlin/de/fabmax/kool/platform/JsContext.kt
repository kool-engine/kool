package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.platform.webgl.QueueRendererWebGl
import de.fabmax.kool.platform.webgl.ShaderGeneratorImplWebGl
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logE
import kotlinx.browser.document
import kotlinx.browser.window
import org.khronos.webgl.*
import org.khronos.webgl.WebGLRenderingContext.Companion.MAX_TEXTURE_IMAGE_UNITS
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.ImageData
import org.w3c.dom.events.Event
import org.w3c.dom.events.UIEvent

/**
 * @author fabmax
 */
@Suppress("UnsafeCastFromDynamic")
class JsContext internal constructor(val props: InitProps) : KoolContext() {
    override val assetMgr = JsAssetManager(props, this)
    override val inputMgr: JsInputManager

    override val shaderGenerator: ShaderGenerator = ShaderGeneratorImplWebGl()

    internal val queueRenderer = QueueRendererWebGl(this)
    internal val afterRenderActions = mutableListOf<() -> Unit>()

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

    private val canvas: HTMLCanvasElement
    internal val gl: WebGL2RenderingContext
    private val sysInfo = mutableListOf<String>()

    private var animationMillis = 0.0

    val glCapabilities = GlCapabilities()

    private val openRenderPasses = mutableListOf<OffscreenRenderPass>()
    private val doneRenderPasses = mutableSetOf<OffscreenRenderPass>()

    init {
        canvas = document.getElementById(props.canvasName) as? HTMLCanvasElement ?:
                throw IllegalStateException("canvas element not found! Add a canvas with id \"${props.canvasName}\" to your html.")

        canvas.style.width = "100%"
        canvas.style.height = "100%"
        canvas.width = (window.innerWidth * window.devicePixelRatio).toInt()
        canvas.height = (window.innerHeight * window.devicePixelRatio).toInt()

        // try to get a WebGL2 context first and use WebGL version 1 as fallback
        var webGlCtx = canvas.getContext("webgl2")
        if (webGlCtx == null) {
            webGlCtx = canvas.getContext("experimental-webgl2")
        }

        if (webGlCtx != null) {
            gl = webGlCtx as WebGL2RenderingContext
            sysInfo += "WebGL 2.0"

            glCapabilities.maxTexUnits = gl.getParameter(MAX_TEXTURE_IMAGE_UNITS).asDynamic()
            glCapabilities.hasFloatTextures = gl.getExtension("EXT_color_buffer_float") != null

        } else {
            js("alert(\"Unable to initialize WebGL2 context. Your browser may not support it.\")")
            throw KoolException("WebGL2 context required")
        }

        val extAnisotropic = gl.getExtension("EXT_texture_filter_anisotropic") ?:
                gl.getExtension("MOZ_EXT_texture_filter_anisotropic") ?:
                gl.getExtension("WEBKIT_EXT_texture_filter_anisotropic")
        if (extAnisotropic != null) {
            glCapabilities.maxAnisotropy = gl.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) as Int
            glCapabilities.glTextureMaxAnisotropyExt = extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT
        }

        document.onfullscreenchange = {
            isFullscreenEnabled = document.fullscreenElement != null
            null
        }

        screenDpi = 96f * window.devicePixelRatio.toFloat()
        windowWidth = canvas.width
        windowHeight = canvas.height

        // suppress context menu
        canvas.oncontextmenu = Event::preventDefault

        inputMgr = JsInputManager(canvas, props)
    }

    private fun renderFrame(time: Double) {
        // determine delta time
        val dt = (time - animationMillis) / 1000.0
        animationMillis = time

        // update viewport size
        screenDpi = 96f * window.devicePixelRatio.toFloat()
        windowWidth = (window.innerWidth * window.devicePixelRatio).toInt()
        windowHeight = (window.innerHeight * window.devicePixelRatio).toInt()
        if (windowWidth != canvas.width || windowHeight!= canvas.height) {
            // resize canvas to viewport
            canvas.width = windowWidth
            canvas.height = windowHeight
        }

        // render frame
        render(dt)
        draw()
        gl.finish()

        // request next frame
        window.requestAnimationFrame { t -> renderFrame(t) }
    }

    private fun draw() {
        if (disposablePipelines.isNotEmpty()) {
            queueRenderer.disposePipelines(disposablePipelines)
            disposablePipelines.clear()
        }

        engineStats.resetPerFrameCounts()

        for (j in backgroundPasses.indices) {
            if (backgroundPasses[j].isEnabled) {
                drawOffscreen(backgroundPasses[j])
                backgroundPasses[j].afterDraw(this)
            }
        }
        for (i in scenes.indices) {
            val scene = scenes[i]
            if (scene.isVisible) {
                doOffscreenPasses(scene, this)
                queueRenderer.renderQueue(scene.mainRenderPass.drawQueue)
                scene.mainRenderPass.afterDraw(this)
            }
        }

        if (afterRenderActions.isNotEmpty()) {
            afterRenderActions.forEach { it() }
            afterRenderActions.clear()
        }
    }

    private fun doOffscreenPasses(scene: Scene, ctx: KoolContext) {
        doneRenderPasses.clear()
        for (i in scene.offscreenPasses.indices) {
            val rp = scene.offscreenPasses[i]
            if (rp.isEnabled) {
                openRenderPasses += rp
            } else {
                doneRenderPasses += rp
            }
        }
        while (openRenderPasses.isNotEmpty()) {
            var anyDrawn = false
            for (i in openRenderPasses.indices) {
                val pass = openRenderPasses[i]
                var skip = false
                for (j in pass.dependencies.indices) {
                    val dep = pass.dependencies[j]
                    if (dep !in doneRenderPasses) {
                        skip = true
                        break
                    }
                }
                if (!skip) {
                    anyDrawn = true
                    openRenderPasses -= pass
                    doneRenderPasses += pass
                    drawOffscreen(pass)
                    pass.afterDraw(ctx)
                    break
                }
            }
            if (!anyDrawn) {
                logE { "Failed to render all offscreen passes, remaining:" }
                openRenderPasses.forEach { logE { "  ${it.name}" } }
                openRenderPasses.clear()
                break
            }
        }
    }

    private fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw(this)
            is OffscreenRenderPassCube -> offscreenPass.impl.draw(this)
            is OffscreenRenderPass2dPingPong -> drawOffscreenPingPong(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    private fun drawOffscreenPingPong(offscreenPass: OffscreenRenderPass2dPingPong) {
        for (i in 0 until offscreenPass.pingPongPasses) {
            offscreenPass.onDrawPing?.invoke(i)
            offscreenPass.ping.impl.draw(this)

            offscreenPass.onDrawPong?.invoke(i)
            offscreenPass.pong.impl.draw(this)
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
        window.requestAnimationFrame { t -> renderFrame(t) }
    }

    override fun destroy() {
        // nothing to do here...
    }

    override fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode {
        val src = GlslGenerator().generateProgram(shader.program)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ShaderCode(src.vertexSrc, src.fragmentSrc)
    }

    override fun getSysInfos(): List<String> {
        return sysInfo
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, windowWidth, windowHeight)
    }

    class InitProps {
        var canvasName = "glCanvas"
        val excludedKeyCodes: MutableSet<String> = mutableSetOf("F5", "F11")

        var assetsBaseDir = "./assets"
        val customFonts = mutableMapOf<String, String>()
    }
}

class GlCapabilities {
    var maxTexUnits = 16
        internal set
    var hasFloatTextures = false
        internal set
    var maxAnisotropy = 1
        internal set
    var glTextureMaxAnisotropyExt = 0
        internal set
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

abstract external class WebGL2RenderingContext : WebGLRenderingContext {
    fun bindBufferBase(target: Int, index: Int, buffer: WebGLBuffer?)
    fun bufferData(target: Int, srcData: ArrayBufferView, usage: Int, srcOffset: Int, length: Int)
    fun clearBufferfv(buffer: Int, drawBuffer: Int, values: Float32Array)
    fun drawBuffers(buffers: IntArray)
    fun drawElementsInstanced(mode: Int, count: Int, type: Int, offset: Int, instanceCount: Int)
    fun getActiveUniformBlockParameter(program: WebGLProgram?, uniformBlockIndex: Int, pname: Int): Int
    fun getActiveUniforms(program: WebGLProgram?, uniformIndices: IntArray, pname: Int): IntArray
    fun getUniformBlockIndex(program: WebGLProgram?, uniformBlockName: String): Int
    fun getUniformIndices(program: WebGLProgram?, uniformNames: Array<String>): IntArray
    fun readBuffer(src: Int)
    fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int)
    fun texImage3D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, depth: Int, border: Int, format: Int, type: Int, srcData: ArrayBufferView?)
    fun texImage3D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, depth: Int, border: Int, format: Int, type: Int, source: HTMLImageElement?)
    fun texSubImage3D(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, format: Int, type: Int, pixels: ImageData?)
    fun texStorage2D(target: Int, levels: Int, internalformat: Int, width: Int, height: Int)
    fun texStorage3D(target: Int, levels: Int, internalformat: Int, width: Int, height: Int, depth: Int)
    fun uniformBlockBinding(program: WebGLProgram?, uniformBlockIndex: Int, uniformBlockBinding: Int)
    fun vertexAttribDivisor(index: Int, divisor: Int)
    fun vertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)

    companion object {
        val COLOR: Int
        val DEPTH: Int
        val STENCIL: Int
        val DEPTH_STENCIL: Int

        val DEPTH_COMPONENT24: Int
        val DEPTH_COMPONENT32F: Int
        val TEXTURE_3D: Int
        val TEXTURE_WRAP_R: Int
        val TEXTURE_COMPARE_MODE: Int
        val COMPARE_REF_TO_TEXTURE: Int
        val TEXTURE_COMPARE_FUNC: Int

        val UNIFORM_BLOCK_DATA_SIZE: Int
        val UNIFORM_BUFFER: Int
        val UNIFORM_OFFSET: Int

        val INVALID_INDEX: Int

        val RED: Int
        val RG: Int

        val R8: Int
        val RG8: Int
        val RGB8: Int
        val RGBA8: Int

        val R16F: Int
        val RG16F: Int
        val RGB16F: Int
        val RGBA16F: Int
    }
}

val Touch.elementX: Double
    get() = clientX - ((target as? HTMLCanvasElement)?.clientLeft?.toDouble() ?: 0.0)

val Touch.elementY: Double
    get() = clientY - ((target as? HTMLCanvasElement)?.clientTop?.toDouble() ?: 0.0)
