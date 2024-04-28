package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Node(name) {

    val onRenderScene: BufferedList<(KoolContext) -> Unit> = BufferedList()

    val lighting = Lighting()
    val mainRenderPass: SceneRenderPass = SceneRenderPass()

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

    val offscreenPasses: BufferedList<OffscreenRenderPass> = BufferedList()
    internal val sortedOffscreenPasses = mutableListOf<OffscreenRenderPass>()

    val isEmpty: Boolean
        get() = children.isEmpty() && (offscreenPasses.isEmpty() && !offscreenPasses.hasStagedMutations)

    fun tryEnableInfiniteDepth(): Boolean {
        val ctx = KoolSystem.getContextOrNull() ?: return false
        if (ctx.backend.depthRange == DepthRange.ZERO_TO_ONE) {
            mainRenderPass.isReverseDepth = true
            logI { "Enabled infinite depth mode" }
            return true
        } else {
            logW { "Failed to enable infinite depth mode: Incompatible clip depth range" }
            return false
        }
    }

    fun addOffscreenPass(pass: OffscreenRenderPass) {
        offscreenPasses += pass
    }

    fun removeOffscreenPass(pass: OffscreenRenderPass) {
        offscreenPasses -= pass
    }

    fun renderScene(ctx: KoolContext) {
        onRenderScene.update()
        for (i in onRenderScene.indices) {
            onRenderScene[i](ctx)
        }

        mainRenderPass.update(ctx)

        if (offscreenPasses.update()) {
            // offscreen passes have changed, re-sort them to maintain correct dependency order
            sortedOffscreenPasses.clear()
            sortedOffscreenPasses.addAll(offscreenPasses)
            OffscreenRenderPass.sortByDependencies(sortedOffscreenPasses)
        }

        for (i in offscreenPasses.indices) {
            val pass = offscreenPasses[i]
            pass.parentScene = this
            if (pass.isEnabled) {
                pass.update(ctx)
                pass.collectDrawCommands(ctx)
            }
        }
        mainRenderPass.collectDrawCommands(ctx)
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
        offscreenPasses.update()
        for (i in offscreenPasses.indices) {
            offscreenPasses[i].release()
        }
        offscreenPasses.clear()

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

    inner class SceneRenderPass : RenderPass("${name}:OnScreenRenderPass") {
        val screenView = View("screen", this@Scene, PerspectiveCamera())
        var camera: Camera by screenView::camera
        val viewport: Viewport by screenView::viewport
        var useWindowViewport = true

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
