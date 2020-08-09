package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.drawqueue.DrawQueue
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Viewport

abstract class RenderPass(val drawNode: Node) {

    var name: String? = null

    val dependencies = mutableListOf<RenderPass>()

    val viewport = Viewport(0, 0, 0, 0)
    abstract val camera: Camera

    var lighting: Lighting? = null

    var isUpdateDrawNode = true
    private var updateEvent: UpdateEvent? = null

    var clearColors = Array<Color?>(1) { Color(0.15f, 0.15f, 0.15f, 1f) }
        protected set
    var clearColor: Color?
        get() = clearColors[0]
        set(value) { clearColors[0] = value }
    var clearDepth = true

    var drawQueue = DrawQueue(this)
        protected set

    val onBeforeCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()
    val onAfterCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()
    val onAfterDraw = mutableListOf<((KoolContext) -> Unit)>()

    private fun updateEvent(ctx: KoolContext) = updateEvent ?: UpdateEvent(this, ctx).also { updateEvent = it }

    fun dependsOn(renderPass: RenderPass) {
        dependencies += renderPass
    }

    open fun update(ctx: KoolContext) {
        if (isUpdateDrawNode) {
            drawNode.update(updateEvent(ctx))
        }
    }

    open fun collectDrawCommands(ctx: KoolContext) {
        beforeCollectDrawCommands(ctx)
        drawNode.collectDrawCommands(updateEvent(ctx))
        afterCollectDrawCommands(ctx)
    }

    open fun addMesh(mesh: Mesh, ctx: KoolContext): DrawCommand? {
        return drawQueue.addMesh(mesh, ctx)
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

    open fun afterDraw(ctx: KoolContext) {
        for (i in onAfterDraw.indices) {
            onAfterDraw[i](ctx)
        }
    }

    open fun dispose(ctx: KoolContext) { }

    class UpdateEvent(val renderPass: RenderPass, val ctx: KoolContext) {
        val time: Double
            get() = ctx.time
        val deltaT: Float
            get() = ctx.deltaT
        val frameIndex: Int
            get() = ctx.frameIdx

        val camera: Camera
            get() = renderPass.camera
        val viewport: Viewport
            get() = renderPass.viewport

        operator fun component1() = renderPass
        operator fun component2() = ctx
    }
}

class ScreenRenderPass(val scene: Scene) : RenderPass(scene) {
    override val camera
        get() = scene.camera

    var useWindowViewport = true

    init {
        name = "onscreen/${scene.name}"
        lighting = scene.lighting
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        if (useWindowViewport) {
            ctx.getWindowViewport(viewport)
        }
        super.collectDrawCommands(ctx)
    }
}
