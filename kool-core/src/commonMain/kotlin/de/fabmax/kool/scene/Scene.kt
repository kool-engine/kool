package de.fabmax.kool.scene

import de.fabmax.kool.*
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Node(name) {
    private val job = Job(ApplicationScope.job)

    /**
     * This scene's [CoroutineScope]. Is automatically canceled when the scene is released.
     */
    val coroutineScope: CoroutineScope = CoroutineScope(job)

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
    private val passData = ResettableDataList { PassAndPassData() }

    val isEmpty: Boolean
        get() = children.isEmpty() && (extraPasses.isEmpty() && !extraPasses.hasStagedMutations)

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

    fun collectScene(frameData: FrameData, ctx: KoolContext) {
        onRenderScene.update()
        for (i in onRenderScene.indices) {
            onRenderScene[i](ctx)
        }

        if (sortedPasses.isEmpty() || extraPasses.update()) {
            sortGpuPassesByDependencies()
        }

        passData.reset()
        for (i in sortedPasses.indices) {
            val pass = sortedPasses[i]
            if (pass.isEnabled) {
                val passAndData = passData.acquire(pass)
                passAndData.latePassData = frameData.acquirePassData(pass)
            }
        }
        if (passData.isNotEmpty()) {
            // make sure mainRenderPass is updated first, so that scene info (camera, etc.) is updated
            // before offscreen passes are updated
            val (mainPass, mainPassData) = passData.last()
            mainPass.collect(mainPassData, ctx)
            for (i in 0 ..< passData.lastIndex) {
                val (pass, data) = passData[i]
                data.reset(pass)
                pass.collect(data, ctx)
            }
        }
    }

    private fun sortGpuPassesByDependencies() {
        _sortedPasses.clear()
        _sortedPasses.addAll(extraPasses)
        if (_sortedPasses.distinct().size != _sortedPasses.size) {
            logW { "Multiple occurrences of offscreen passes: $_sortedPasses" }
        }
        GpuPass.sortByDependencies(_sortedPasses)
        // main render pass is always executed last
        _sortedPasses.add(mainRenderPass)
        for (i in _sortedPasses.indices) { _sortedPasses[i].parentScene = this }
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

    override fun doRelease() {
        super.doRelease()
        job.cancel()
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

        private val _dimensions = MutableVec3i()
        override val dimensions: Vec3i get() = _dimensions

        init {
            parentScene = this@Scene
            lighting = this@Scene.lighting

            val ctx = KoolSystem.getContextOrNull()
            if (ctx != null) {
                _dimensions.set(ctx.windowWidth, ctx.windowHeight, 1)
            } else {
                _dimensions.set(Vec3i.ONES)
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

        override fun update(passData: PassData, ctx: KoolContext) {
            _dimensions.set(ctx.windowWidth, ctx.windowHeight, 1)
            if (isFillFrame && !viewport.equals(0, 0, dimensions.x, dimensions.y)) {
                viewport = Viewport(0, 0, dimensions.x, dimensions.y)
            }
            super.update(passData, ctx)
        }

        override fun doRelease() { }
    }

    private class PassAndPassData() : ResettableData<GpuPass> {
        private var latePass: GpuPass? = null
        var latePassData: PassData? = null

        val pass: GpuPass get() = latePass!!
        val passData: PassData get() = latePassData!!

        operator fun component1(): GpuPass = latePass!!
        operator fun component2(): PassData = latePassData!!

        override fun reset(init: GpuPass) {
            latePass = init
            latePassData = null
        }
    }
}
