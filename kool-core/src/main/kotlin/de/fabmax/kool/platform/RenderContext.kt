package de.fabmax.kool.platform

import de.fabmax.kool.*
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

    private var startTimeMillis = 0L
    var time: Double = 0.0
        protected set
    var deltaT: Float = 0.0f
        protected set

    var scene: Scene = Scene()

    private var viewportWidthProp = Property(0)
    var viewportWidth by viewportWidthProp

    private val viewportHeightProp = Property(0)
    var viewportHeight by viewportHeightProp

    private val clearColorProp = Property(Color(0.05f, 0.15f, 0.25f, 1f))
    var clearColor by clearColorProp

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
        // use blending with pre-multiplied alpha
        GL.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA)

        scene.onRender(this)
    }

}
