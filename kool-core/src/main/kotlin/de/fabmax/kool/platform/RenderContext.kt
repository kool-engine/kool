package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.gl.BufferResource
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Property

/**
 * @author fabmax
 */
abstract class RenderContext {

    var screenDpi = 96f

    val inputMgr = InputManager()
    val memoryMgr = MemoryManager()
    val shaderMgr = ShaderManager()
    val textureMgr = TextureManager()
    val mvpState = MvpState()

    val onRender: MutableList<(RenderContext) -> Unit> = mutableListOf()

    private var nextId = 1L
    private val idLock = Any()

    private var startTimeMillis = 0L
    var time = 0.0
        protected set
    var deltaT = 0f
        protected set
    var frameIdx = 0
        private set
    var fps = 60f
        private set

    val scenes: MutableList<Scene> = mutableListOf()

    private val attribs = Attribs()
    private val attribsStack = Array(16, { Attribs() })
    private var attribsStackIdx = 0

    private val frameTimes = FloatArray(25, { 0.017f })

    abstract val windowWidth: Int
    abstract val windowHeight: Int

    var viewportX by attribs.viewportX
    var viewportY by attribs.viewportY
    var viewportWidth by attribs.viewportWidth
    var viewportHeight by attribs.viewportHeight
    var clearColor by attribs.clearColor
    var isDepthTest by attribs.isDepthTest
    var isDepthMask by attribs.isDepthMask
    var isCullFace by attribs.isCullFace
    var isBlend by attribs.isBlend

    internal val boundBuffers: MutableMap<Int, BufferResource?> = mutableMapOf()

    abstract class InitProps

    abstract fun run()

    abstract fun destroy()

    protected fun render() {
        val now = Platform.currentTimeMillis()
        if (startTimeMillis == 0L) {
            // avoid deltaT being zero...
            startTimeMillis = now - 1
        }
        val t = (now - startTimeMillis).toDouble() / 1000.0
        val dt = (t - time).toFloat()

        render(dt)
    }

    protected open fun render(dt: Float) {
        frameIdx++
        time += dt
        deltaT = dt

        frameTimes[frameIdx % frameTimes.size] = dt
        var sum = 0f
        for (i in frameTimes.indices) { sum += frameTimes[i] }
        fps = (frameTimes.size / sum) * 0.1f + fps * 0.9f

        inputMgr.onNewFrame()

        // force re-binding shader, otherwise delayed loaded resources (e.g. textures) might not be loaded at all
        shaderMgr.bindShader(null, this)

        // by default the viewport covers the full window
        viewportWidth = windowWidth
        viewportHeight = windowHeight
        applyAttributes()

        for (i in onRender.indices) {
            onRender[i](this)
        }

        for (i in scenes.indices) {
            scenes[i].render(this)
        }
    }

    fun pushAttributes() {
        attribsStack[attribsStackIdx++].set(attribs)
    }

    fun popAttributes() {
        attribs.set(attribsStack[--attribsStackIdx])
        applyAttributes()
    }

    fun applyAttributes() {
        attribs.apply()
    }

    fun generateUniqueId(): Long {
        var id: Long = 0L
        synchronized(idLock) {
            id = ++nextId
        }
        return id
    }

    private class Attribs {
        val viewportX = Property(0)
        val viewportY = Property(0)
        val viewportWidth = Property(0)
        val viewportHeight = Property(0)
        val clearColor = Property(Color(0.05f, 0.15f, 0.25f, 1f))
        val depthFunc = Property(GL.LEQUAL)
        val isDepthTest = Property(true)
        val isDepthMask = Property(true)
        val isCullFace = Property(true)
        val isBlend = Property(true)

        fun apply() {
            if (viewportX.valueChanged || viewportY.valueChanged ||
                    viewportWidth.valueChanged || viewportHeight.valueChanged) {
                GL.viewport(viewportX.clear, viewportY.clear, viewportWidth.clear, viewportHeight.clear)
            }
            if (clearColor.valueChanged) {
                val color = clearColor.clear
                GL.clearColor(color.r, color.g, color.b, color.a)
            }
            if (depthFunc.valueChanged) {
                GL.depthFunc(depthFunc.clear)
            }
            if (isDepthTest.valueChanged) {
                if (isDepthTest.clear) {
                    GL.enable(GL.DEPTH_TEST)
                } else {
                    GL.disable(GL.DEPTH_TEST)
                }
            }
            if (isDepthTest.valueChanged) {
                if (isDepthTest.clear) {
                    GL.enable(GL.DEPTH_TEST)
                } else {
                    GL.disable(GL.DEPTH_TEST)
                }
            }
            if (isDepthMask.valueChanged) {
                GL.depthMask(isDepthMask.clear)
            }
            if (isCullFace.valueChanged) {
                if (isCullFace.clear) {
                    GL.enable(GL.CULL_FACE)
                } else {
                    GL.disable(GL.CULL_FACE)
                }
            }
            if (isBlend.valueChanged) {
                if (isBlend.clear) {
                    GL.enable(GL.BLEND)
                    // use blending with pre-multiplied alpha
                    GL.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA)
                } else {
                    GL.disable(GL.BLEND)
                }
            }
        }

        fun set(other: Attribs) {
            viewportX.copy(other.viewportX, false)
            viewportY.copy(other.viewportY, false)
            viewportWidth.copy(other.viewportWidth, false)
            viewportHeight.copy(other.viewportHeight, false)
            clearColor.copy(other.clearColor, false)
            depthFunc.copy(other.depthFunc, false)
            isDepthTest.copy(other.isDepthTest, false)
            isDepthMask.copy(other.isDepthMask, false)
            isCullFace.copy(other.isCullFace, false)
            isBlend.copy(other.isBlend, false)
        }
    }
}
