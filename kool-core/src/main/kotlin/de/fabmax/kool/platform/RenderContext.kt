package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Property

/**
 * @author fabmax
 */
abstract class RenderContext {

    val memoryMgr = MemoryManager()
    val shaderMgr = ShaderManager()
    val textureMgr = TextureManager()
    val mvpState = MvpState()

    protected var startTimeMillis = 0L
    var time: Double = 0.0
        protected set
    var deltaT: Float = 0.0f
        protected set

    var scene: Scene = Scene()

    private val viewportWidthProp = Property(0)
    var viewportWidth: Int
        get() = viewportWidthProp.value
        protected set(value) { viewportWidthProp.value = value }

    private val viewportHeightProp = Property(0)
    var viewportHeight: Int
        get() = viewportHeightProp.value
        protected set(value) { viewportHeightProp.value = value }

    private val clearColorProp = Property(Color.DARK_CYAN)
    var clearColor: Color
        get() = clearColorProp.value
        set(value) { clearColorProp.value = value }

    var clearMask = GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT

    internal val boundBuffers: MutableMap<Int, BufferResource?> = mutableMapOf()

    abstract class InitProps

    abstract fun run()

    abstract fun destroy()

    protected fun onNewFrame() {
        val now = Platform.currentTimeMillis()
        if (startTimeMillis == 0L) {
            startTimeMillis = now
        }
        val t = (now - startTimeMillis).toDouble() / 1000.0
        deltaT = (t - time).toFloat()
        time = t

        //boundBuffers.clear()
        //bindShader(null)
        //binTexture(null)

        if (viewportWidthProp.valueChanged || viewportHeightProp.valueChanged) {
            GL.viewport(0, 0, viewportWidthProp.clear, viewportHeightProp.clear)
        }
        if (clearColorProp.valueChanged) {
            val color = clearColorProp.clear
            GL.clearColor(color.r, color.g, color.b, color.a)
        }

        if (clearMask != 0) {
            GL.clear(clearMask)
        }
        GL.enable(GL.DEPTH_TEST)
        GL.enable(GL.CULL_FACE)
        GL.enable(GL.BLEND)
        // straight alpha
        //GL.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)
        // pre-multiplied alpha
        GL.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA)
    }

    protected fun render() {
        onNewFrame()

        scene.onRender(this)
    }

}
