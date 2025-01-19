package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class RenderPass(var name: String) : BaseReleasable() {

    /**
     * Dimension of the output (window / screen or framebuffer / texture). Notice, that the part
     * of that output space actually used as output can be smaller and is set via [View.viewport].
     */
    abstract val size: Vec3i
    val width: Int get() = size.x
    val height: Int get() = size.y
    val depth: Int get() = size.z

    var mipMode: MipMode = MipMode.None
        protected set
    val numTextureMipLevels: Int get() = mipMode.getTextureMipLevels(size)
    val numRenderMipLevels: Int get() = mipMode.getRenderMipLevels(size)

    var parentScene: Scene? = null

    abstract val clearColors: Array<Color?>
    var clearColor: Color?
        get() = clearColors.getOrNull(0)
        set(value) { clearColors[0] = value }
    var clearDepth = true

    abstract val views: List<View>

    /**
     * Frame copies to perform after the entire render pass is done.
     */
    val frameCopies = mutableListOf<FrameCopy>()

    var lighting: Lighting? = null

    var isDoublePrecision = false
    open var isReverseDepth = false

    val onBeforeCollectDrawCommands = BufferedList<((UpdateEvent) -> Unit)>()
    val onAfterCollectDrawCommands = BufferedList<((UpdateEvent) -> Unit)>()
    val onAfterDraw = BufferedList<(() -> Unit)>()
    val onSetupMipLevel = BufferedList<((Int) -> Unit)>()

    var isProfileTimes = false
    var tUpdate: Duration = 0.0.seconds
    var tCollect: Duration = 0.0.seconds
    var tGpu: Duration = 0.0.seconds

    var isMirrorY = false
        protected set

    protected fun mirrorIfInvertedClipY() {
        isMirrorY = KoolSystem.requireContext().backend.isInvertedNdcY
    }

    open fun update(ctx: KoolContext) {
        checkIsNotReleased()
        val t = if (isProfileTimes) Time.precisionTime else 0.0
        for (i in views.indices) {
            views[i].update(ctx)
        }
        tUpdate = if (isProfileTimes) (Time.precisionTime - t).seconds else 0.0.seconds
    }

    open fun collectDrawCommands(ctx: KoolContext) {
        val t = if (isProfileTimes) Time.precisionTime else 0.0
        for (i in views.indices) {
            views[i].collectDrawCommands(ctx)
        }
        tCollect = if (isProfileTimes) (Time.precisionTime - t).seconds else 0.0.seconds
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

    open fun afterDraw() {
        onAfterDraw.update()
        for (i in onAfterDraw.indices) {
            onAfterDraw[i]()
        }
    }

    fun onAfterDraw(block: () -> Unit) {
        onAfterDraw += block
    }

    /**
     * Executes the given block each time before this render pass is rendered at a specific mip-level. This can
     * be used to change mip-level specific shader configuration if this render pass is rendered at multiple mip-levels.
     * However, be aware that, at the time this function is called, previous mip-level passes are enqueued but not yet
     * executed. This means, you should avoid changing single uniform values of a shader because that would affect
     * the previous mip-levels as well. Instead, you can and should change the entire pipeline bind-group of a shader
     * in these cases.
     */
    fun onSetupMipLevel(block: (Int) -> Unit) {
        onSetupMipLevel += block
    }

    open fun setupMipLevel(mipLevel: Int) {
        onSetupMipLevel.update()
        for (i in onSetupMipLevel.indices) {
            onSetupMipLevel[i](mipLevel)
        }
    }

    override fun toString(): String {
        return "${this::class.simpleName}:$name"
    }

    sealed class MipMode(val hasMipLevels: Boolean) {
        data object None : MipMode(false) {
            override fun getTextureMipLevels(size: Vec3i) = 1
            override fun getRenderMipLevels(size: Vec3i) = 1
        }

        data object Generate : MipMode(true) {
            override fun getTextureMipLevels(size: Vec3i) = numMipLevels(size.x, size.y)
            override fun getRenderMipLevels(size: Vec3i) = 1
        }

        data class Render(
            val numMipLevels: Int,
            val renderOrder: MipMapRenderOrder = MipMapRenderOrder.HigherResolutionFirst
        ) : MipMode(true) {
            override fun getTextureMipLevels(size: Vec3i) = if (numMipLevels == 0) numMipLevels(size.x, size.y) else numMipLevels
            override fun getRenderMipLevels(size: Vec3i) = getTextureMipLevels(size)
        }

        abstract fun getTextureMipLevels(size: Vec3i): Int
        abstract fun getRenderMipLevels(size: Vec3i): Int
    }

    enum class MipMapRenderOrder {
        HigherResolutionFirst,
        LowerResolutionFirst
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

    inner class View(var name: String, var drawNode: Node, var camera: Camera) {
        val renderPass: RenderPass get() = this@RenderPass

        val viewport = Viewport(0, 0, 0, 0)
        val drawQueue = DrawQueue(this@RenderPass, this)
        var drawFilter: (Node) -> Boolean = { true }

        val onSetupView = BufferedList<(() -> Unit)>()

        /**
         * Frame copies to perform during this view is rendered. This is particularly useful to
         * capture intermediate render outputs, which can then be used by following draw
         * operations / shaders.
         */
        val frameCopies = mutableListOf<FrameCopy>()

        var isUpdateDrawNode = true
        var isReleaseDrawNode = true

        val viewPipelineData = PipelineData(BindGroupScope.VIEW)

        private var updateEvent: UpdateEvent? = null

        init {
            viewPipelineData.releaseWith(this@RenderPass)
        }

        /**
         * Convenience function to add an item to this view's [frameCopies].
         */
        fun copyOutput(isCopyColor: Boolean, isCopyDepth: Boolean, drawGroupId: Int = 0, isSingleShot: Boolean = false): FrameCopy {
            val copy = FrameCopy(this@RenderPass, isCopyColor, isCopyDepth, drawGroupId, isSingleShot)
            frameCopies += copy
            frameCopies.sortBy { it.drawGroupId }
            return copy
        }

        internal fun setupView() {
            onSetupView.update()
            for (i in onSetupView.indices) {
                onSetupView[i]()
            }
        }

        /**
         * Executes the given block each time before a specific view of this render pass is rendered. This can
         * be used to change view specific shader configurations.
         * However, be aware that, at the time this function is called, previous view passes are enqueued but not yet
         * executed. This means, you should avoid changing single uniform values of a shader because that would affect
         * the previous passes as well. Instead, you can and should change the entire pipeline bind-group of a shader
         * in these cases.
         */
        fun onSetupView(block: () -> Unit) {
            onSetupView += block
        }

        internal fun makeUpdateEvent(ctx: KoolContext): UpdateEvent {
            return updateEvent ?: UpdateEvent(this, ctx).also { updateEvent = it }
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

    enum class ViewRenderMode {
        SINGLE_RENDER_PASS,
        MULTI_RENDER_PASS,
    }
}
