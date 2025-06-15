package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
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

    abstract val size: Vec3i
    val width: Int get() = size.x
    val height: Int get() = size.y
    val layers: Int get() = size.z

    abstract val views: List<View>

    val numRenderMipLevels: Int get() = mipMode.getRenderMipLevels(size)
    val numTextureMipLevels: Int get() = mipMode.getTextureMipLevels(size)

    /**
     * Frame copies to perform after the entire render pass is done. The draw group ID of frame copies is ignored.
     * In order to perform draw group ID aware copies, use [View.frameCopies].
     */
    val frameCopies = mutableListOf<FrameCopy>()

    var lighting: Lighting? = null

    var isDoublePrecision = false
    var depthMode = DepthMode.Reversed

    val onBeforeCollectDrawCommands = BufferedList<((UpdateEvent) -> Unit)>()
    val onAfterCollectDrawCommands = BufferedList<((UpdateEvent) -> Unit)>()
    val onSetupMipLevel = BufferedList<((Int) -> Unit)>()

    var isMirrorY = false

    fun mirrorIfInvertedClipY() {
        isMirrorY = KoolSystem.requireContext().backend.isInvertedNdcY
    }

    override fun update(ctx: KoolContext) {
        val t = if (isProfileTimes) Time.precisionTime else 0.0
        super.update(ctx)
        for (i in views.indices) {
            views[i].update(ctx)
        }
        tUpdate = if (isProfileTimes) (Time.precisionTime - t).seconds else 0.0.seconds
    }

    protected open fun beforeCollectDrawCommands(updateEvent: UpdateEvent) {
        onBeforeCollectDrawCommands.update()
        for (i in onBeforeCollectDrawCommands.indices) {
            onBeforeCollectDrawCommands[i](updateEvent)
        }
    }

    protected open fun afterCollectDrawCommands(updateEvent: UpdateEvent) {
        onAfterCollectDrawCommands.update()
        for (i in onAfterCollectDrawCommands.indices) {
            onAfterCollectDrawCommands[i](updateEvent)
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

    sealed class MipMode(val mipMapping: MipMapping) {
        data object Single : MipMode(MipMapping.Off) {
            override fun getTextureMipLevels(size: Vec3i) = 1
            override fun getRenderMipLevels(size: Vec3i) = 1
        }

        data object Generate : MipMode(MipMapping.Full) {
            override fun getTextureMipLevels(size: Vec3i) = numMipLevels(size.x, size.y)
            override fun getRenderMipLevels(size: Vec3i) = 1
        }

        data class Render(
            val numMipLevels: Int,
            val renderOrder: MipMapRenderOrder = MipMapRenderOrder.HigherResolutionFirst
        ) : MipMode(MipMapping.Limited(numMipLevels)) {
            init { require(numMipLevels >= 0) }
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

        var viewport = Viewport(0, 0, 0, 0)
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
        var isFillFramebuffer = true

        val viewPipelineData = PipelineData(BindGroupScope.VIEW)

        private var updateEvent: UpdateEvent? = null

        init {
            viewPipelineData.releaseWith(this@RenderPass)
        }

        /**
         * Convenience function to add an item to this view's [frameCopies]. Depending on the [drawGroupId], the
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
            frameCopies += copy
            frameCopies.sortBy { it.drawGroupId }
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

        internal fun update(ctx: KoolContext) {
            val updateEvent = makeUpdateEvent(ctx)
            if (isUpdateDrawNode) {
                drawNode.update(updateEvent)
            }
            if (camera.parent == null) {
                // camera is not attached to any node, make sure it gets updated anyway
                camera.update(updateEvent)
            }
            collectDrawCommands(ctx)
        }

        internal fun collectDrawCommands(ctx: KoolContext) {
            val updateEvent = makeUpdateEvent(ctx)

            drawQueue.reset(isDoublePrecision)
            beforeCollectDrawCommands(updateEvent)
            camera.updateCamera(updateEvent)
            drawQueue.setupCamera(camera)

            drawNode.collectDrawCommands(updateEvent)
            afterCollectDrawCommands(updateEvent)
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
