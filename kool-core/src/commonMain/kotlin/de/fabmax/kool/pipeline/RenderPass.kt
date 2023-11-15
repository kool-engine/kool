package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.drawqueue.DrawQueue
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Viewport

abstract class RenderPass(var name: String) : BaseReleasable() {

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

        private var updateEvent: UpdateEvent? = null

        internal fun makeUpdateEvent(ctx: KoolContext): UpdateEvent {
            return updateEvent ?: UpdateEvent(this, ctx).also { updateEvent = it }
        }

        fun appendMeshToDrawQueue(mesh: Mesh, ctx: KoolContext): DrawCommand {
            return drawQueue.addMesh(mesh, ctx)
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

class ScreenRenderPass(val scene: Scene) : RenderPass("${scene.name}:ScreenRenderPass") {

    val screenView = View("screen", scene, PerspectiveCamera(), arrayOf(Color(0.15f, 0.15f, 0.15f, 1f)))
    var camera: Camera by screenView::camera
    val viewport: Viewport by screenView::viewport
    var clearColor: Color? by screenView::clearColor
    var clearDepth: Boolean by screenView::clearDepth

    private val _views = mutableListOf(screenView)
    override val views: List<View>
        get() = _views

    var useWindowViewport = true

    init {
        parentScene = scene
        lighting = Lighting()
    }

    fun createView(name: String): View {
        val view = View(name, scene, PerspectiveCamera(), arrayOf(null))
        _views += view
        return view
    }

    fun removeView(view: View) {
        _views -= view
    }

    override fun update(ctx: KoolContext) {
        if (useWindowViewport) {
            ctx.getWindowViewport(viewport)
        }
        super.update(ctx)
    }
}
