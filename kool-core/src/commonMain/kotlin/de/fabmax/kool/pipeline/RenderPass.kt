package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Viewport

abstract class RenderPass(val drawNode: Node) {

    var name: String? = null
    var type = Type.COLOR

    val dependencies = mutableListOf<RenderPass>()

    val viewport = Viewport(0, 0, 0, 0)
    abstract val camera: Camera

    var lighting: Lighting? = null

    var isUpdateDrawNode = true

    open val clearColors = Array<Color?>(1) { Color(0.15f, 0.15f, 0.15f, 1f) }
    var clearColor: Color?
        get() = clearColors[0]
        set(value) { clearColors[0] = value }
    var clearDepth = true

    var drawQueue = DrawQueue(this)
        protected set

    val onBeforeCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()
    val onAfterCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()

    fun dependsOn(renderPass: RenderPass) {
        dependencies += renderPass
    }

    open fun update(ctx: KoolContext) {
        if (isUpdateDrawNode) {
            drawNode.update(this, ctx)
        }
    }

    open fun collectDrawCommands(ctx: KoolContext) {
        beforeCollectDrawCommands(ctx)
        drawNode.collectDrawCommands(this, ctx)
        afterCollectDrawCommands(ctx)
    }

    protected open fun beforeCollectDrawCommands(ctx: KoolContext) {
        drawQueue.clear()
        for (i in onBeforeCollectDrawCommands.indices) {
            onBeforeCollectDrawCommands[i](ctx)
        }
        camera.updateCamera(ctx, viewport)
    }

    protected open fun afterCollectDrawCommands(ctx: KoolContext) {
        for (i in onAfterCollectDrawCommands.indices) {
            onAfterCollectDrawCommands[i](ctx)
        }
    }

    open fun dispose(ctx: KoolContext) { }

    enum class Type {
        COLOR,
        DEPTH
    }
}

class ScreenRenderPass(val scene: Scene) : RenderPass(scene) {
    override val camera
        get() = scene.camera

    var useWindowViewport = true

    init {
        lighting = scene.lighting
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        if (useWindowViewport) {
            ctx.getWindowViewport(viewport)
        }
        super.collectDrawCommands(ctx)
    }
}
