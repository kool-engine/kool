package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

abstract class RenderPass(val drawNode: Node) {

    var name: String? = null
    var type = Type.COLOR
    var viewport = KoolContext.Viewport(0, 0, 0, 0)
    abstract val camera: Camera

    var lighting: Lighting? = null

    var isUpdateDrawNode = true

    var clearDepth = true
    var clearColor: Color? = Color(0.15f, 0.15f, 0.15f, 1f)
    var colorBlend = true

    var drawQueue = DrawQueue(this)
        protected set

    val onBeforeCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()
    val onAfterCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()

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

    init {
        lighting = scene.lighting
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        viewport = ctx.viewport
        super.collectDrawCommands(ctx)
    }
}
