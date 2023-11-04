package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.drawqueue.DrawQueue
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Profiling
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Viewport

abstract class RenderPass(var drawNode: Node) {

    var name: String? = null

    val dependencies = mutableListOf<RenderPass>()

    val viewport = Viewport(0, 0, 0, 0)
    abstract val camera: Camera

    var lighting: Lighting? = null

    var isUpdateDrawNode = true
    var drawMeshFilter: (Mesh) -> Boolean = { true }
    private var updateEvent: UpdateEvent? = null

    var clearColors = Array<Color?>(1) { Color(0.15f, 0.15f, 0.15f, 1f) }
        protected set
    var clearColor: Color?
        get() = clearColors[0]
        set(value) { clearColors[0] = value }
    var clearDepth = true

    var drawQueue = DrawQueue(this)
        protected set
    var isDoublePrecision = false

    val onBeforeCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()
    val onAfterCollectDrawCommands = mutableListOf<((KoolContext) -> Unit)>()
    val onBeforeRenderQueue: MutableList<(KoolContext) -> Unit> = mutableListOf()
    val onAfterRenderQueue: MutableList<(KoolContext) -> Unit> = mutableListOf()
    val onAfterDraw = mutableListOf<((KoolContext) -> Unit)>()

    var isProfileDetailed = false

    private fun setupUpdateEvent(ctx: KoolContext): UpdateEvent {
        val event = updateEvent ?: UpdateEvent(this, ctx).also { updateEvent = it }
        event.meshFilter = drawMeshFilter
        return event
    }

    fun profileTag(subTag: String): String {
        return if (isProfileDetailed) {
            "RP:${name}-${subTag}"
        } else {
            "RP:${name}"
        }
    }

    fun dependsOn(renderPass: RenderPass) {
        dependencies += renderPass
    }

    open fun update(ctx: KoolContext) {
        if (ctx.isProfileRenderPasses) {
            Profiling.enter(profileTag("update"))
        }
        val updateEvent = setupUpdateEvent(ctx)
        if (isUpdateDrawNode) {
            drawNode.update(updateEvent)
        }
        if (camera.parent == null) {
            // camera is not attached to any node, make sure it gets updated anyway
            camera.update(updateEvent)
        }
        if (ctx.isProfileRenderPasses) {
            Profiling.exit(profileTag("update"))
        }
    }

    open fun collectDrawCommands(ctx: KoolContext) {
        if (ctx.isProfileRenderPasses) {
            Profiling.enter(profileTag("collect"))
        }
        beforeCollectDrawCommands(ctx)
        drawNode.collectDrawCommands(setupUpdateEvent(ctx))
        afterCollectDrawCommands(ctx)
        if (ctx.isProfileRenderPasses) {
            Profiling.exit(profileTag("collect"))
        }
    }

    /**
     * Called on every frame after initial setup (viewport and clear) before any geometry is drawn. Does not work with
     * Vulkan backend.
     */
    open fun onBeforeRenderQueue(ctx: KoolContext) {
        if (onBeforeRenderQueue.isNotEmpty()) {
            for (i in onBeforeRenderQueue.indices) {
                onBeforeRenderQueue[i](ctx)
            }
        }
    }

    /**
     * Called on every frame after all content of this render pass is drawn. Does not work with Vulkan backend.
     */
    open fun onAfterRenderQueue(ctx: KoolContext) {
        if (onAfterRenderQueue.isNotEmpty()) {
            for (i in onAfterRenderQueue.indices) {
                onAfterRenderQueue[i](ctx)
            }
        }
    }

    open fun appendMeshToDrawQueue(mesh: Mesh, ctx: KoolContext): DrawCommand? {
        return drawQueue.addMesh(mesh, ctx)
    }

    protected open fun beforeCollectDrawCommands(ctx: KoolContext) {
        drawQueue.reset(isDoublePrecision)
        for (i in onBeforeCollectDrawCommands.indices) {
            onBeforeCollectDrawCommands[i](ctx)
        }
        camera.updateCamera(this, ctx)
        drawQueue.setupCamera(camera)
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
        @Deprecated("Replaced by Time.gameTime", ReplaceWith("de.fabmax.kool.util.Time.gameTime"))
        val time: Double
            get() = Time.gameTime

        @Deprecated("Replaced by Time.deltaT", ReplaceWith("de.fabmax.kool.util.Time.deltaT"))
        val deltaT: Float
            get() = Time.deltaT
        val frameIndex: Int
            get() = Time.frameCount

        val camera: Camera
            get() = renderPass.camera
        val viewport: Viewport
            get() = renderPass.viewport

        var meshFilter: (Mesh) -> Boolean = { true }

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

    override fun update(ctx: KoolContext) {
        if (useWindowViewport) {
            ctx.getWindowViewport(viewport)
        }
        super.update(ctx)
    }
}
