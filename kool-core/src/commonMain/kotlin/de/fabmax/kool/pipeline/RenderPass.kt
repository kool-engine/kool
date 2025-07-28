package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.PassData
import de.fabmax.kool.ViewData
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.*
import kotlin.time.Duration.Companion.seconds

abstract class RenderPass(
    val numSamples: Int,
    val mipMode: MipMode,
    name: String
) : GpuPass(name) {

    abstract val colorAttachments: List<RenderPassColorAttachment>
    abstract val depthAttachment: RenderPassDepthAttachment?

    val hasColor: Boolean get() = colorAttachments.isNotEmpty()
    val hasDepth: Boolean get() = depthAttachment != null

    abstract val dimensions: Vec3i
    val width: Int get() = dimensions.x
    val height: Int get() = dimensions.y
    val layers: Int get() = dimensions.z

    abstract val views: List<View>

    val numRenderMipLevels: Int get() = mipMode.getRenderMipLevels(dimensions)
    val numTextureMipLevels: Int get() = mipMode.getTextureMipLevels(dimensions)

    /**
     * Frame copies to perform after the entire render pass is done. The draw group ID of frame copies is ignored.
     * In order to perform draw group ID aware copies, use [View.frameCopies].
     */
    val frameCopies: List<FrameCopy> get() = _frameCopies
    private val _frameCopies = mutableListOf<FrameCopy>()
    private var hasSingleShotCopies = false

    var lighting: Lighting? = null

    var isDoublePrecision = false
    var depthMode = DepthMode.Reversed

    val onBeforeCollectDrawCommands = BufferedList<((ViewData) -> Unit)>()
    val onAfterCollectDrawCommands = BufferedList<((ViewData) -> Unit)>()
    val onSetupMipLevel = BufferedList<((Int) -> Unit)>()

    var isMirrorY = false

    fun mirrorIfInvertedClipY() {
        isMirrorY = KoolSystem.requireContext().backend.isInvertedNdcY
    }

    override fun update(passData: PassData, ctx: KoolContext) {
        val t = Time.precisionTime
        super.update(passData, ctx)
        check(passData.viewData.isEmpty())
        for (i in views.indices) {
            views[i].update(passData, ctx)
        }
        if (hasSingleShotCopies) {
            _frameCopies.removeAll { it.isSingleShot }
            hasSingleShotCopies = false
        }
        tUpdate = (Time.precisionTime - t).seconds
    }

    protected open fun beforeCollectDrawCommands(viewData: ViewData) {
        onBeforeCollectDrawCommands.update()
        for (i in onBeforeCollectDrawCommands.indices) {
            onBeforeCollectDrawCommands[i](viewData)
        }
    }

    protected open fun afterCollectDrawCommands(viewData: ViewData) {
        onAfterCollectDrawCommands.update()
        for (i in onAfterCollectDrawCommands.indices) {
            onAfterCollectDrawCommands[i](viewData)
        }
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

    /**
     * Adds an item to this renderpass's [frameCopies].
     */
    fun copyOutput(
        isCopyColor: Boolean,
        isCopyDepth: Boolean,
        isSingleShot: Boolean = false
    ): FrameCopy {
        val copy = FrameCopy(this, isCopyColor, isCopyDepth, isSingleShot = isSingleShot)
        _frameCopies += copy
        hasSingleShotCopies = hasSingleShotCopies || isSingleShot
        return copy
    }

    sealed class MipMode(val mipMapping: MipMapping) {
        data object Single : MipMode(MipMapping.Off) {
            override fun getTextureMipLevels(dimensions: Vec3i) = 1
            override fun getRenderMipLevels(dimensions: Vec3i) = 1
        }

        data object Generate : MipMode(MipMapping.Full) {
            override fun getTextureMipLevels(dimensions: Vec3i) = numMipLevels(dimensions.x, dimensions.y)
            override fun getRenderMipLevels(dimensions: Vec3i) = 1
        }

        data class Render(
            val numMipLevels: Int,
            val renderOrder: MipMapRenderOrder = MipMapRenderOrder.HigherResolutionFirst
        ) : MipMode(MipMapping.Limited(numMipLevels)) {
            init { require(numMipLevels >= 0) }
            override fun getTextureMipLevels(dimensions: Vec3i) = if (numMipLevels == 0) numMipLevels(dimensions.x, dimensions.y) else numMipLevels
            override fun getRenderMipLevels(dimensions: Vec3i) = getTextureMipLevels(dimensions)
        }

        abstract fun getTextureMipLevels(dimensions: Vec3i): Int
        abstract fun getRenderMipLevels(dimensions: Vec3i): Int
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

        var viewport = Viewport(0, 0, 0, 0)
        var drawFilter: (Node) -> Boolean = { true }

        val onSetupView = BufferedList<(() -> Unit)>()

        /**
         * Frame copies to perform during this view is rendered. This is particularly useful to
         * capture intermediate render outputs, which can then be used by following draw
         * operations / shaders.
         */
        val frameCopies: List<FrameCopy> get() = _frameCopies
        private val _frameCopies = mutableListOf<FrameCopy>()
        private var hasSingleShotCopies = false

        var isUpdateDrawNode = true
        var isReleaseDrawNode = true
        var isFillFramebuffer = true

        val viewPipelineData = MultiPipelineBindGroupData(BindGroupScope.VIEW)

        private var updateEvent: UpdateEvent? = null

        init {
            viewPipelineData.releaseWith(this@RenderPass)
        }

        /**
         * Adds an item to this view's [frameCopies]. Depending on the [drawGroupId], the
         * copy can be performed during frame render, interrupting the render pass. This way, the framebuffer can be
         * captured after a certain set of objects is drawn (everything with drawGroupId <= the specified
         * [drawGroupId]) but before everything else is drawn.
         * Objects / shaders executed later on in the renderpass can then make use of the copied textures to achieve
         * various background distortion effects like e.g. frosted glass.
         * However, to do the copy, the renderpass needs to be interrupted, which is a very expensive operation.
         */
        fun copyOutput(
            isCopyColor: Boolean,
            isCopyDepth: Boolean,
            drawGroupId: Int = 0,
            isSingleShot: Boolean = false
        ): FrameCopy {
            val copy = FrameCopy(this@RenderPass, isCopyColor, isCopyDepth, drawGroupId, isSingleShot)
            _frameCopies += copy
            _frameCopies.sortBy { it.drawGroupId }
            hasSingleShotCopies = hasSingleShotCopies || isSingleShot
            return copy
        }

        fun setupView() {
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

        internal fun update(passData: PassData, ctx: KoolContext) {
            val updateEvent = makeUpdateEvent(ctx)
            if (isUpdateDrawNode) {
                drawNode.update(updateEvent)
            }
            if (camera.parent == null) {
                // camera is not attached to any node, make sure it gets updated anyway
                camera.update(updateEvent)
            }

            val viewData = passData.acquireViewData(this)
            collectDrawCommands(viewData, updateEvent)
            if (hasSingleShotCopies) {
                _frameCopies.removeAll { it.isSingleShot }
                hasSingleShotCopies = false
            }
        }

        private fun collectDrawCommands(viewData: ViewData, updateEvent: UpdateEvent) {
            beforeCollectDrawCommands(viewData)
            camera.updateCamera(viewData)
            viewData.drawQueue.setupCamera(camera)
            drawNode.collectDrawCommands(viewData, updateEvent)
            afterCollectDrawCommands(viewData)
        }
    }
}

interface RenderPassColorAttachment {
    val clearColor: ClearColor
}

interface RenderPassColorTextureAttachment<T: Texture<*>> : RenderPassColorAttachment {
    val texture: T
}

interface RenderPassDepthAttachment {
    val clearDepth: ClearDepth
}

interface RenderPassDepthTextureAttachment<T: Texture<*>> : RenderPassDepthAttachment {
    val texture: T
}

sealed interface ClearColor
data object ClearColorLoad : ClearColor
data object ClearColorDontCare : ClearColor
data class ClearColorFill(val clearColor: Color) : ClearColor

sealed interface ClearDepth
data object ClearDepthLoad : ClearDepth
data object ClearDepthDontCare : ClearDepth
data object ClearDepthFill : ClearDepth

enum class DepthMode(val far: Float) {
    Reversed(0f),
    Legacy(1f),
}
