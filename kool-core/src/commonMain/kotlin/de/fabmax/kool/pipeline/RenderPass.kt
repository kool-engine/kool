package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.drawqueue.DrawQueue
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Viewport

abstract class RenderPass(
    var drawNode: Node,
    var name: String
) {

    var parentScene: Scene? = null

    val dependencies = mutableListOf<RenderPass>()

    abstract val views: List<View>

    var lighting: Lighting? = null

    private var updateEvent: UpdateEvent? = null

    var isDoublePrecision = false

    val onBeforeCollectDrawCommands = mutableListOf<((UpdateEvent) -> Unit)>()
    val onAfterCollectDrawCommands = mutableListOf<((UpdateEvent) -> Unit)>()
    val onAfterDraw = mutableListOf<((KoolContext) -> Unit)>()

    var isProfileTimes = true
    var tUpdate = 0.0
    var tCollect = 0.0
    var tDraw = 0.0

    fun dependsOn(renderPass: RenderPass) {
        dependencies += renderPass
    }

    open fun update(ctx: KoolContext) {
        val t = if (isProfileTimes) Time.precisionTime else 0.0

        for (i in views.indices) {
            val view = views[i]
            if (view.isUpdateDrawNode) {
                val updateEvent = view.makeUpdateEvent(ctx)

                drawNode.update(updateEvent)
                if (view.camera.parent == null) {
                    // camera is not attached to any node, make sure it gets updated anyway
                    view.camera.update(updateEvent)
                }
            }
        }

        tUpdate = if (isProfileTimes) Time.precisionTime - t else 0.0
    }

    open fun collectDrawCommands(ctx: KoolContext) {
        val t = if (isProfileTimes) Time.precisionTime else 0.0

        for (i in views.indices) {
            val view = views[i]
            val updateEvent = view.makeUpdateEvent(ctx)

            beforeCollectDrawCommands(updateEvent)
            drawNode.collectDrawCommands(updateEvent)
            afterCollectDrawCommands(updateEvent)
        }

        tCollect = if (isProfileTimes) Time.precisionTime - t else 0.0
    }

    protected open fun beforeCollectDrawCommands(updateEvent: UpdateEvent) {
        updateEvent.view.drawQueue.reset(isDoublePrecision)
        for (i in onBeforeCollectDrawCommands.indices) {
            onBeforeCollectDrawCommands[i](updateEvent)
        }
        updateEvent.view.camera.updateCamera(updateEvent)
        updateEvent.view.drawQueue.setupCamera(updateEvent.view.camera)
    }

    protected open fun afterCollectDrawCommands(updateEvent: UpdateEvent) {
        for (i in onAfterCollectDrawCommands.indices) {
            onAfterCollectDrawCommands[i](updateEvent)
        }
    }

    open fun afterDraw(ctx: KoolContext) {
        for (i in onAfterDraw.indices) {
            onAfterDraw[i](ctx)
        }
    }

    open fun dispose(ctx: KoolContext) { }

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

    inner class View(var name: String, var camera: Camera, val clearColors: Array<Color?>) {
        val renderPass: RenderPass get() = this@RenderPass

        val viewport = Viewport(0, 0, 0, 0)
        val drawQueue = DrawQueue(this@RenderPass, this)
        var drawFilter: (Node) -> Boolean = { true }

//        var clearColors = Array<Color?>(1) { Color(0.15f, 0.15f, 0.15f, 1f) }
//            protected set

        var clearDepth = true
        var clearColor: Color?
            get() = clearColors[0]
            set(value) { clearColors[0] = value }

        var isUpdateDrawNode = true

        private var updateEvent: UpdateEvent? = null

        internal fun makeUpdateEvent(ctx: KoolContext): UpdateEvent {
            return updateEvent ?: UpdateEvent(this, ctx).also { updateEvent = it }
        }

        fun appendMeshToDrawQueue(mesh: Mesh, ctx: KoolContext): DrawCommand {
            return drawQueue.addMesh(mesh, ctx)
        }
    }
}

class ScreenRenderPass(val scene: Scene) : RenderPass(scene, "${scene.name}:ScreenRenderPass") {

    val screenView = View("screen", PerspectiveCamera(), arrayOf(Color(0.15f, 0.15f, 0.15f, 1f)))
    var camera: Camera by screenView::camera
    val viewport: Viewport by screenView::viewport
    var clearColor: Color? by screenView::clearColor
    var clearDepth: Boolean by screenView::clearDepth

    override val views: List<View> = listOf(screenView)

    var useWindowViewport = true

    init {
        parentScene = scene
        lighting = Lighting()
    }

    override fun update(ctx: KoolContext) {
        if (useWindowViewport) {
            ctx.getWindowViewport(viewport)
        }
        super.update(ctx)
    }
}
