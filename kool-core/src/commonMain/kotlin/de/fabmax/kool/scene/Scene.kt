package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.Ray
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.ScreenRenderPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.logD

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Node(name) {

    val onRenderScene: BufferedList<(KoolContext) -> Unit> = BufferedList()

    val mainRenderPass = ScreenRenderPass(this)

    var camera: Camera by mainRenderPass::camera
    val lighting: Lighting
        get() = mainRenderPass.lighting!!

    val offscreenPasses: BufferedList<OffscreenRenderPass> = BufferedList()
    internal val sortedOffscreenPasses = mutableListOf<OffscreenRenderPass>()

    var framebufferCaptureMode = FramebufferCaptureMode.Disabled
    val capturedFramebuffer by lazy {
        Texture2d(name = "$name.capturedFramebuffer")
    }

    val isEmpty: Boolean
        get() = children.isEmpty() && offscreenPasses.isEmpty()

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
        for (i in offscreenPasses.indices) {
            offscreenPasses[i].release()
        }
        offscreenPasses.clear()
        capturedFramebuffer.dispose()

        logD { "Released scene \"$name\"" }
    }

    fun computePickRay(pointer: Pointer, ctx: KoolContext, result: Ray): Boolean {
        return camera.computePickRay(result, pointer, mainRenderPass.viewport, ctx)
    }

    enum class FramebufferCaptureMode {
        Disabled,
        BeforeRender,
        AfterRender
    }
}
