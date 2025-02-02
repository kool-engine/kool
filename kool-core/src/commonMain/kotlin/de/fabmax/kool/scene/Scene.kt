package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.GpuPass
import de.fabmax.kool.pipeline.OffscreenPass
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.DepthRange
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

    var clearColor: Color?
        get() = mainRenderPass.clearColor
        set(value) { mainRenderPass.clearColor = value }
    var clearDepth: Boolean
        get() = mainRenderPass.clearDepth
        set(value) { mainRenderPass.clearDepth = value }

    val isInfiniteDepth: Boolean
        get() = mainRenderPass.isReverseDepth

    var camera: Camera
        get() = mainRenderPass.camera
        set(value) { mainRenderPass.camera = value }

    val extraPasses: BufferedList<GpuPass> = BufferedList()
    internal val sortedPasses = mutableListOf<GpuPass>(mainRenderPass)

    val isEmpty: Boolean
        get() = children.isEmpty() && (extraPasses.isEmpty() && !extraPasses.hasStagedMutations)

    var sceneRecordTime = 0.0.seconds

    fun tryEnableInfiniteDepth(): Boolean {
        val ctx = KoolSystem.getContextOrNull() ?: return false
        if (ctx.backend.depthRange == DepthRange.ZERO_TO_ONE) {
            mainRenderPass.isReverseDepth = true
            logD { "Enabled infinite depth mode" }
            return true
        } else {
            logW { "Failed to enable infinite depth mode: Incompatible clip depth range" }
            return false
        }
    }

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
            sortedPasses.clear()
            sortedPasses.addAll(extraPasses)
            if (sortedPasses.distinct().size != sortedPasses.size) {
                logW { "Multiple occurrences of offscreen passes: $sortedPasses" }
            }
            GpuPass.sortByDependencies(sortedPasses)
            // main render pass is always executed last
            sortedPasses.add(mainRenderPass)
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
        // update lights not attached
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

    enum class FramebufferCaptureMode {
        Disabled,
        BeforeRender,
        AfterRender
    }

    inner class ScreenPass : RenderPass("${name}:ScreenPass", MipMode.None) {
        val screenView = View("screen", this@Scene, PerspectiveCamera())
        var camera: Camera by screenView::camera
        val viewport: Viewport by screenView::viewport
        var useWindowViewport = true

        override val numSamples: Int get() = KoolSystem.config.numSamples
        override val clearColors: Array<Color?> = arrayOf(DEFAULT_CLEAR_COLOR)

        private val _views = mutableListOf(screenView)
        override val views: List<View>
            get() = _views

        private val _size: MutableVec3i by lazy {
            val ctx = KoolSystem.requireContext()
            MutableVec3i(ctx.windowWidth, ctx.windowHeight, 1)
        }
        override val size: Vec3i
            get() = _size

        init {
            parentScene = this@Scene
            lighting = this@Scene.lighting
        }

        fun createView(name: String): View {
            val view = View(name, this@Scene, PerspectiveCamera())
            view.isUpdateDrawNode = false
            view.isReleaseDrawNode = false
            _views += view
            return view
        }

        fun removeView(view: View) {
            _views -= view
        }

        override fun update(ctx: KoolContext) {
            _size.set(ctx.windowWidth, ctx.windowHeight, 1)
            if (useWindowViewport) {
                ctx.getWindowViewport(viewport)
            }
            super.update(ctx)
        }
    }
}
