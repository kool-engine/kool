package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.RayF
import de.fabmax.kool.pipeline.*
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
    var mainRenderPass: SceneRenderPass = OnscreenSceneRenderPass()
        private set
    val isInfiniteDepth: Boolean
        get() = mainRenderPass.renderPass.isReverseDepth

    var camera: Camera
        get() = mainRenderPass.camera
        set(value) { mainRenderPass.camera = value }

    val offscreenPasses: BufferedList<OffscreenRenderPass> = BufferedList()
    internal val sortedOffscreenPasses = mutableListOf<OffscreenRenderPass>()

    var framebufferCaptureMode = FramebufferCaptureMode.Disabled
    val capturedFramebuffer by lazy {
        Texture2d(name = "$name.capturedFramebuffer")
    }

    val isEmpty: Boolean
        get() = children.isEmpty() && offscreenPasses.isEmpty()

    fun tryEnableInfiniteDepth(): Boolean {
        if (isInfiniteDepth) {
            return true
        }

        val ctx = KoolSystem.getContextOrNull() ?: return false
        if (ctx.backend.isOnscreenInfiniteDepthCapable) {
            mainRenderPass.renderPass.useReversedDepthIfAvailable = true
            logI { "Enabled infinite depth mode (onscreen)" }
            return true

        } else if (ctx.backend.canBlitRenderPasses && ctx.backend.depthRange == DepthRange.ZERO_TO_ONE) {
            val cam = mainRenderPass.camera
            mainRenderPass.renderPass.release()
            mainRenderPass = OffscreenSceneRenderPass(true)
            mainRenderPass.camera = cam
            logI { "Enabled infinite depth mode (via offscreen render pass)" }
            return true
        }
        logW { "Failed to enable infinite depth mode" }
        return false
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

        mainRenderPass.renderPass.update(ctx)

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
        mainRenderPass.renderPass.collectDrawCommands(ctx)
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

        mainRenderPass.renderPass.release()
        for (i in offscreenPasses.indices) {
            offscreenPasses[i].release()
        }
        offscreenPasses.clear()
        capturedFramebuffer.dispose()

        logD { "Released scene \"$name\"" }
    }

    fun computePickRay(pointer: Pointer, result: RayF): Boolean {
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

    sealed interface SceneRenderPass {
        val screenView: RenderPass.View
        var camera: Camera
        val viewport: Viewport
        var clearColor: Color?
        var clearDepth: Boolean
        var useWindowViewport: Boolean

        val renderPass: RenderPass

        fun createView(name: String): RenderPass.View
        fun removeView(view: RenderPass.View)
    }

    inner class OnscreenSceneRenderPass : RenderPass("${name}:OnScreenRenderPass"), SceneRenderPass {
        override val screenView = View("screen", this@Scene, PerspectiveCamera(), arrayOf(DEFAULT_CLEAR_COLOR))
        override var camera: Camera by screenView::camera
        override val viewport: Viewport by screenView::viewport
        override var clearColor: Color? by screenView::clearColor
        override var clearDepth: Boolean by screenView::clearDepth
        override var useWindowViewport = true

        override val renderPass = this

        var blitRenderPass: OffscreenRenderPass2d? = null
            set(value) {
                field = value
                if (value != null) {
                    clearColor = null
                }
            }

        private val _views = mutableListOf(screenView)
        override val views: List<View>
            get() = _views

        override val isReverseDepth get() =
            useReversedDepthIfAvailable && KoolSystem.requireContext().backend.isOnscreenInfiniteDepthCapable

        override val width: Int
            get() = KoolSystem.requireContext().windowWidth
        override val height: Int
            get() = KoolSystem.requireContext().windowHeight
        override val depth: Int = 1

        init {
            parentScene = this@Scene
            lighting = this@Scene.lighting
        }

        override fun createView(name: String): View {
            val view = View(name, this@Scene, PerspectiveCamera(), arrayOf(null))
            _views += view
            return view
        }

        override fun removeView(view: View) {
            _views -= view
        }

        override fun update(ctx: KoolContext) {
            if (useWindowViewport) {
                ctx.getWindowViewport(viewport)
            }
            super.update(ctx)
        }
    }

    inner class OffscreenSceneRenderPass(isMultiSampled: Boolean) : OffscreenRenderPass2d(
        this@Scene,
        renderPassConfig {
            this.name = "${this@Scene.name}:OffScreenRenderPass"
            if (isMultiSampled) {
                colorTargetRenderBuffer(TexFormat.RGBA, true)
            } else {
                colorTargetTexture(TexFormat.RGBA)
            }
        }
    ), SceneRenderPass {
        override val screenView: View get() = views[0]
        override var useWindowViewport = true

        override val renderPass = this

        init {
            parentScene = this@Scene
            lighting = this@Scene.lighting
            useReversedDepthIfAvailable = true

            screenView.apply {
                name = "screen"
                drawNode = this@Scene
                clearColor = DEFAULT_CLEAR_COLOR
            }
        }

        override fun createView(name: String): View {
            val view = View(name, this@Scene, PerspectiveCamera(), arrayOf(null))
            views += view
            return view
        }

        override fun removeView(view: View) {
            views -= view
        }

        override fun update(ctx: KoolContext) {
            if (useWindowViewport) {
                ctx.getWindowViewport(viewport)
                setSize(viewport.width, viewport.height)
            }
            super.update(ctx)
        }
    }
}
