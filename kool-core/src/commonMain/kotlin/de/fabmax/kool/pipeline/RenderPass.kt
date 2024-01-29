package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.drawqueue.DrawQueue
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*

abstract class RenderPass(var name: String) : BaseReleasable() {

    /**
     * Available width of the output (window / screen or framebuffer / texture). Notice, that the part
     * of that output space actually used as output can be smaller and is set via [View.viewport].
     */
    abstract val width: Int

    /**
     * Available height of the output (window / screen or framebuffer / texture). Notice, that the part
     * of that output space actually used as output can be smaller and is set via [View.viewport].
     */
    abstract val height: Int

    /**
     * Available depth of the output (window / screen or framebuffer / texture). This should be one for all
     * non 3d render passes.
     */
    abstract val depth: Int

    var parentScene: Scene? = null

    abstract val views: List<View>

    var lighting: Lighting? = null

    private var updateEvent: UpdateEvent? = null

    var isDoublePrecision = false
    var useReversedDepthIfAvailable = false
    abstract val isReverseDepth: Boolean

    val onBeforeCollectDrawCommands = BufferedList<((UpdateEvent) -> Unit)>()
    val onAfterCollectDrawCommands = BufferedList<((UpdateEvent) -> Unit)>()
    val onAfterDraw = BufferedList<((KoolContext) -> Unit)>()

    var isProfileTimes = false
    var tUpdate = 0.0
    var tCollect = 0.0
    var tDraw = 0.0

    protected var complainedAboutReversedDepth = false

    open fun update(ctx: KoolContext) {
        checkIsNotReleased()
        val t = if (isProfileTimes) Time.precisionTime else 0.0
        for (i in views.indices) {
            views[i].update(ctx)
        }
        tUpdate = if (isProfileTimes) Time.precisionTime - t else 0.0
    }

    open fun collectDrawCommands(ctx: KoolContext) {
        val t = if (isProfileTimes) Time.precisionTime else 0.0
        for (i in views.indices) {
            views[i].collectDrawCommands(ctx)
        }
        tCollect = if (isProfileTimes) Time.precisionTime - t else 0.0

        if (useReversedDepthIfAvailable && !isReverseDepth && !complainedAboutReversedDepth) {
            complainedAboutReversedDepth = true
            logW { "Reversed depth testing requested but not available" }
        }
    }

    protected open fun beforeCollectDrawCommands(updateEvent: UpdateEvent) {
        updateEvent.view.drawQueue.reset(isDoublePrecision)
        onBeforeCollectDrawCommands.update()
        for (i in onBeforeCollectDrawCommands.indices) {
            onBeforeCollectDrawCommands[i](updateEvent)
        }
        updateEvent.view.camera.updateCamera(updateEvent)
        updateEvent.view.drawQueue.setupCamera(updateEvent.view.camera)
    }

    protected open fun afterCollectDrawCommands(updateEvent: UpdateEvent) {
        onAfterCollectDrawCommands.update()
        for (i in onAfterCollectDrawCommands.indices) {
            onAfterCollectDrawCommands[i](updateEvent)
        }
    }

    open fun afterDraw(ctx: KoolContext) {
        onAfterDraw.update()
        for (i in onAfterDraw.indices) {
            onAfterDraw[i](ctx)
        }
    }

    override fun toString(): String {
        return "${this::class.simpleName}:$name"
    }

    class UpdateEvent(val view: View, val ctx: KoolContext) {
        val renderPass: RenderPass get() = view.renderPass

        val camera: Camera
            get() = view.camera
        val viewport: Viewport
            get() = view.viewport

        val drawFilter: (Node) -> Boolean get() = view.drawFilter

        operator fun component1() = view
        operator fun component2() = ctx
    }

    inner class View(var name: String, var drawNode: Node, var camera: Camera, val clearColors: Array<Color?>) {
        val renderPass: RenderPass get() = this@RenderPass

        val viewport = Viewport(0, 0, 0, 0)
        val drawQueue = DrawQueue(this@RenderPass, this)
        var drawFilter: (Node) -> Boolean = { true }

        var clearDepth = true
        var clearColor: Color?
            get() = clearColors[0]
            set(value) { clearColors[0] = value }

        var isUpdateDrawNode = true
        var isReleaseDrawNode = true

        val viewPipelineData = PipelineData(BindGroupScope.VIEW)

        private var updateEvent: UpdateEvent? = null

        init {
            viewPipelineData.releaseWith(this@RenderPass)
        }

        internal fun makeUpdateEvent(ctx: KoolContext): UpdateEvent {
            return updateEvent ?: UpdateEvent(this, ctx).also { updateEvent = it }
        }

        fun appendMeshToDrawQueue(mesh: Mesh, updateEvent: UpdateEvent): DrawCommand {
            return drawQueue.addMesh(mesh, updateEvent)
        }

        internal fun update(ctx: KoolContext) {
            val updateEvent = makeUpdateEvent(ctx)
            if (isUpdateDrawNode) {
                drawNode.update(updateEvent)
            }
            if (camera.parent == null) {
                // camera is not attached to any node, make sure it gets updated anyway
                camera.update(updateEvent)
            }
        }

        internal fun collectDrawCommands(ctx: KoolContext) {
            val updateEvent = makeUpdateEvent(ctx)
            beforeCollectDrawCommands(updateEvent)
            drawNode.collectDrawCommands(updateEvent)
            afterCollectDrawCommands(updateEvent)
        }
    }
}
