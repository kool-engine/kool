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

    val inputHandler = InputHandler()
    val memoryMgr = MemoryManager()
    val shaderMgr = ShaderManager()
    val textureMgr = TextureManager()
    val mvpState = MvpState()

    private var nextId = 1L
    private val idLock = Any()

    private var startTimeMillis = 0L
    var time: Double = 0.0
        protected set
    var deltaT: Float = 0.0f
        protected set

    var scene: Scene = Scene()

    private val attribs = Attribs()
    private val attribsStack = Array<Attribs>(16, { Attribs() })
    private var attribsStackIdx = 0

    var viewportWidth by attribs.viewportWidth
    var viewportHeight by attribs.viewportHeight
    var clearColor by attribs.clearColor
    var isDepthTest by attribs.isDepthTest
    var isDepthMask by attribs.isDepthMask
    var isCullFace by attribs.isCullFace
    var isBlend by attribs.isBlend

    var clearMask = GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT

    internal val boundBuffers: MutableMap<Int, BufferResource?> = mutableMapOf()

    abstract class InitProps

    abstract fun run()

    abstract fun destroy()

    protected fun render() {
        val now = Platform.currentTimeMillis()
        if (startTimeMillis == 0L) {
            startTimeMillis = now
        }
        val t = (now - startTimeMillis).toDouble() / 1000.0
        val dt = (t - time).toFloat()

        render(dt)
    }

    protected open fun render(dt: Float) {
        time += dt
        deltaT = dt

        inputHandler.onNewFrame()

        // force re-binding shader, otherwise delayed loaded resources (e.g. textures) might not be loaded at all
        shaderMgr.bindShader(null, this)

        applyAttributes()
        if (clearMask != 0) {
            GL.clear(clearMask)
        }

        scene.onRender(this)
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
        val viewportWidth = Property(0)
        val viewportHeight = Property(0)
        val clearColor = Property(Color(0.05f, 0.15f, 0.25f, 1f))
        val depthFunc = Property(GL.LEQUAL)
        val isDepthTest = Property(true)
        val isDepthMask = Property(true)
        val isCullFace = Property(true)
        val isBlend = Property(true)

        fun apply() {
            if (viewportWidth.valueChanged || viewportHeight.valueChanged) {
                GL.viewport(0, 0, viewportWidth.clear, viewportHeight.clear)
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
