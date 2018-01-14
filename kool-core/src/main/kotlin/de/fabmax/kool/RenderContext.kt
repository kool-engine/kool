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

    var viewport by attribs.get<Viewport>("viewport")
    var clearColor by attribs.get<Color>("clearColor")
    var depthFunc by attribs.get<Int>("depthFunc")
    var isDepthTest by attribs.get<Boolean>("isDepthTest")
    var isDepthMask by attribs.get<Boolean>("isDepthMask")
    var isCullFace by attribs.get<Boolean>("isCullFace")
    var isBlend by attribs.get<Boolean>("isBlend")
    var lineWidth by attribs.get<Float>("lineWidth")

    internal val boundBuffers: MutableMap<Int, BufferResource?> = mutableMapOf()

    abstract class InitProps

    abstract fun run()

    abstract fun destroy()

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
        viewport = Viewport(0, 0, windowWidth, windowHeight)
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
        private val attribs = mutableListOf(
                Property("viewport", Viewport(0, 0, 0, 0)) {
                    val dimen = clear
                    glViewport(dimen.x, dimen.y, dimen.width, dimen.height)
                },
                Property("clearColor", Color(0.05f, 0.15f, 0.25f, 1f)) {
                    val color = clear
                    glClearColor(color.r, color.g, color.b, color.a)
                },
                Property("depthFunc", GL_LEQUAL) {
                    glDepthFunc(clear)
                },
                Property("isDepthTest", true) {
                    if (clear) {
                        glEnable(GL_DEPTH_TEST)
                    } else {
                        glDisable(GL_DEPTH_TEST)
                    }
                },
                Property("isDepthMask", true) {
                    glDepthMask(clear)
                },
                Property("isCullFace", true) {
                    if (clear) {
                        glEnable(GL_CULL_FACE)
                    } else {
                        glDisable(GL_CULL_FACE)
                    }
                },
                Property("isBlend", true) {
                    if (clear) {
                        glEnable(GL_BLEND)
                        // use blending with pre-multiplied alpha
                        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
                    } else {
                        glDisable(GL_BLEND)
                    }
                },
                Property("lineWidth", 1f) {
                    glLineWidth(clear)
                }
        )

        fun <T> get(name: String): Property<T> {
            for (i in attribs.indices) {
                if (attribs[i].name == name) {
                    return attribs[i] as (Property<T>)
                }
            }
            throw RuntimeException("Attribute not found: $name")
        }

        fun apply() {
            for (i in attribs.indices) {
                attribs[i].applyIfChanged()
            }
        }

        fun set(other: Attribs) {
            for (i in attribs.indices) {
                attribs[i].copy(other.attribs[i], false)
            }
        }
    }

    data class AnisotropicTexFilterInfo(val maxAnisotropy: Float, val TEXTURE_MAX_ANISOTROPY_EXT: Int) {
        val isSupported get() = TEXTURE_MAX_ANISOTROPY_EXT != 0
    }

    data class Viewport(val x: Int, val y: Int, val width: Int, val height: Int) {
        fun isInViewport(x: Float, y: Float) = x >= this.x && x < this.x + width &&
                y >= this.y && y < this.y + height
    }
}
