package de.fabmax.kool

import de.fabmax.kool.gl.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Property
import kotlin.math.max

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

    /**
     * Run time of this render context in seconds. This is the wall clock time between now and the first time render()
     * was called.
     */
    var time = 0.0
        protected set

    /**
     * Time between current and last call of render() in seconds. This will never be zero.
     */
    var deltaT = 0.0001
        protected set

    /**
     * Number of rendered frames.
     */
    var frameIdx = 0
        private set

    /**
     * Frames per second (averaged over last 25 frames)
     */
    var fps = 60.0
        private set

    val scenes: MutableList<Scene> = mutableListOf()

    private val attribs = Attribs()
    private val attribsStack = Array(16, { Attribs() })
    private var attribsStackIdx = 0

    private val frameTimes = DoubleArray(25, { 0.017 })

    abstract val windowWidth: Int
    abstract val windowHeight: Int

    abstract val anisotropicTexFilterInfo: AnisotropicTexFilterInfo

    var viewportX by attribs.viewportX
    var viewportY by attribs.viewportY
    var viewportWidth by attribs.viewportWidth
    var viewportHeight by attribs.viewportHeight
    var clearColor by attribs.clearColor
    var depthFunc by attribs.depthFunc
    var isDepthTest by attribs.isDepthTest
    var isDepthMask by attribs.isDepthMask
    var isCullFace by attribs.isCullFace
    var isBlend by attribs.isBlend

    internal val boundBuffers: MutableMap<Int, BufferResource?> = mutableMapOf()

    abstract class InitProps

    abstract fun run()

    abstract fun destroy()

    /*protected fun render() {
        val now = currentTimeMillis()
        if (startTimeMillis == 0L) {
            // this is the first time render() is called, set start time
            startTimeMillis = now
        }
        val prevTime = time
        time = (now - startTimeMillis) / 1000.0
        deltaT = max((time - prevTime).toFloat(), 0.0001f)

        render(deltaT)
    }*/

    protected fun render(dt: Double) {
        deltaT = max(dt, 0.0001)
        time += dt
        frameIdx++

        frameTimes[frameIdx % frameTimes.size] = dt
        var sum = 0.0
        for (i in frameTimes.indices) { sum += frameTimes[i] }
        fps = (frameTimes.size / sum) * 0.1 + fps * 0.9

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
        var id = 0L
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
        val depthFunc = Property(GL_LEQUAL)
        val isDepthTest = Property(true)
        val isDepthMask = Property(true)
        val isCullFace = Property(true)
        val isBlend = Property(true)

        fun apply() {
            if (viewportX.valueChanged || viewportY.valueChanged ||
                    viewportWidth.valueChanged || viewportHeight.valueChanged) {
                glViewport(viewportX.clear, viewportY.clear, viewportWidth.clear, viewportHeight.clear)
            }
            if (clearColor.valueChanged) {
                val color = clearColor.clear
                glClearColor(color.r, color.g, color.b, color.a)
            }
            if (depthFunc.valueChanged) {
                glDepthFunc(depthFunc.clear)
            }
            if (isDepthTest.valueChanged) {
                if (isDepthTest.clear) {
                    glEnable(GL_DEPTH_TEST)
                } else {
                    glDisable(GL_DEPTH_TEST)
                }
            }
            if (isDepthTest.valueChanged) {
                if (isDepthTest.clear) {
                    glEnable(GL_DEPTH_TEST)
                } else {
                    glDisable(GL_DEPTH_TEST)
                }
            }
            if (isDepthMask.valueChanged) {
                glDepthMask(isDepthMask.clear)
            }
            if (isCullFace.valueChanged) {
                if (isCullFace.clear) {
                    glEnable(GL_CULL_FACE)
                } else {
                    glDisable(GL_CULL_FACE)
                }
            }
            if (isBlend.valueChanged) {
                if (isBlend.clear) {
                    glEnable(GL_BLEND)
                    // use blending with pre-multiplied alpha
                    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
                } else {
                    glDisable(GL_BLEND)
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

    data class AnisotropicTexFilterInfo(val maxAnisotropy: Float, val TEXTURE_MAX_ANISOTROPY_EXT: Int) {
        val isSupported get() = TEXTURE_MAX_ANISOTROPY_EXT != 0
    }
}
