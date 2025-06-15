package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlin.time.Duration.Companion.seconds

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Node(name) {

    val onRenderScene: BufferedList<(KoolContext) -> Unit> = BufferedList()

    val lighting = Lighting()
    val mainRenderPass: ScreenPass = ScreenPass()

    var depthMode by mainRenderPass::depthMode
    var clearColor by mainRenderPass::clearColor
    var clearDepth by mainRenderPass::clearDepth

    var camera: Camera by mainRenderPass::camera

    val extraPasses: BufferedList<GpuPass> = BufferedList()
    private val _sortedPasses = mutableListOf<GpuPass>(mainRenderPass)
    val sortedPasses: List<GpuPass> get() = _sortedPasses

    val isEmpty: Boolean
        get() = children.isEmpty() && (extraPasses.isEmpty() && !extraPasses.hasStagedMutations)

    var sceneRecordTime = 0.0.seconds

    fun addComputePass(pass: ComputePass) {
        extraPasses += pass
    }

    fun removeComputePass(pass: ComputePass) {
        extraPasses -= pass
    }

    fun addOffscreenPass(pass: OffscreenPass) {
        extraPasses += pass
    }

    fun removeOffscreenPass(pass: OffscreenPass) {
        extraPasses -= pass
    }

    open fun renderScene(ctx: KoolContext) {
        onRenderScene.update()
        for (i in onRenderScene.indices) {
            onRenderScene[i](ctx)
        }

        // make sure mainRenderPass is updated first, so that scene info (e.g. camera) is updated
        // before offscreen passes are updated
        mainRenderPass.update(ctx)

        if (extraPasses.update()) {
            // offscreen / compute passes have changed, re-sort them to maintain correct dependency order
            _sortedPasses.clear()
            _sortedPasses.addAll(extraPasses)
            if (_sortedPasses.distinct().size != _sortedPasses.size) {
                logW { "Multiple occurrences of offscreen passes: $_sortedPasses" }
            }
            GpuPass.sortByDependencies(_sortedPasses)
            // main render pass is always executed last
            _sortedPasses.add(mainRenderPass)
        }

        for (i in extraPasses.indices) {
            val pass = extraPasses[i]
            pass.parentScene = this
            if (pass.isEnabled) {
                pass.update(ctx)
            }
        }
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        // update un-attached lights
        lighting.onUpdate(updateEvent)
        super.update(updateEvent)
    }

    override fun checkIsVisible(cam: Camera, ctx: KoolContext): Boolean {
        // don't do frustum checking for Scene
        return isVisible
    }

    override fun release() {
        // scenes shall not be released twice
        checkIsNotReleased()
        super.release()

        mainRenderPass.release()
        extraPasses.updated().forEach { it.release() }
        extraPasses.clear()

        logD { "Released scene \"$name\"" }
    }

    fun computePickRay(pointer: Pointer, result: RayF): Boolean {
        return camera.computePickRay(result, pointer, mainRenderPass.viewport)
    }

    fun computePickRay(pointer: Pointer, result: RayD): Boolean {
        return camera.computePickRay(result, pointer, mainRenderPass.viewport)
    }

    companion object {
        val DEFAULT_CLEAR_COLOR = Color(0.15f, 0.15f, 0.15f, 1f)
    }

    inner class ScreenPass : RenderPass(
        numSamples = KoolSystem.config.numSamples,
        mipMode = MipMode.Single,
        name = "${name}:ScreenPass"
    ), RenderPassColorAttachment, RenderPassDepthAttachment {

        override val colorAttachments: List<RenderPassColorAttachment> = listOf(this)
        override val depthAttachment: RenderPassDepthAttachment = this

        override var clearColor: ClearColor = ClearColorFill(DEFAULT_CLEAR_COLOR)
        override var clearDepth: ClearDepth = ClearDepthFill

        val defaultView = View("${name}:default-view", this@Scene, PerspectiveCamera())
        private val _views = mutableListOf(defaultView)
        override val views: List<View> get() = _views

        var camera: Camera by defaultView::camera
        var viewport: Viewport by defaultView::viewport
        var isFillFrame: Boolean by defaultView::isFillFramebuffer

        private val _size = MutableVec3i()
        override val size: Vec3i get() = _size

        init {
            parentScene = this@Scene
            lighting = this@Scene.lighting

            val ctx = KoolSystem.getContextOrNull()
            if (ctx != null) {
                _size.set(ctx.windowWidth, ctx.windowHeight, 1)
            } else {
                _size.set(Vec3i.ONES)
            }
        }

        fun createView(name: String, camera: Camera = PerspectiveCamera()): View {
            val view = View(name, this@Scene, camera)
            view.isUpdateDrawNode = false
            view.isReleaseDrawNode = false
            _views += view
            return view
        }

        fun removeView(view: View) {
            require(view != defaultView)
            _views -= view
        }

        override fun update(ctx: KoolContext) {
            _size.set(ctx.windowWidth, ctx.windowHeight, 1)
            if (isFillFrame && !viewport.equals(0, 0, size.x, size.y)) {
                viewport = Viewport(0, 0, size.x, size.y)
            }
            super.update(ctx)
        }
    }
}
