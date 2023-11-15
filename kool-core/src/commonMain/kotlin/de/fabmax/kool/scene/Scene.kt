package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.Ray
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.ScreenRenderPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.logD

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Node(name) {

    val onRenderScene: MutableList<(KoolContext) -> Unit> = mutableListOf()

    val mainRenderPass = ScreenRenderPass(this)

    var camera: Camera by mainRenderPass::camera
    val lighting: Lighting
        get() = mainRenderPass.lighting!!

    private val mutOffscreenPasses = mutableListOf<OffscreenRenderPass>()
    private val addOffscreenPasses = mutableListOf<OffscreenRenderPass>()
    private val remOffscreenPasses = mutableListOf<OffscreenRenderPass>()
    val offscreenPasses: List<OffscreenRenderPass>
        get() = mutOffscreenPasses

    var framebufferCaptureMode = FramebufferCaptureMode.Disabled
    val capturedFramebuffer by lazy {
        Texture2d(name = "$name.capturedFramebuffer")
    }

    val isEmpty: Boolean
        get() = children.isEmpty() && mutOffscreenPasses.isEmpty() && addOffscreenPasses.isEmpty() && remOffscreenPasses.isEmpty()

    fun addOffscreenPass(pass: OffscreenRenderPass) {
        addOffscreenPasses += pass
    }

    fun removeOffscreenPass(pass: OffscreenRenderPass) {
        addOffscreenPasses -= pass
        remOffscreenPasses += pass
    }

    private fun addOffscreenPasses() {
        if (addOffscreenPasses.isNotEmpty()) {
            addOffscreenPasses.forEach {
                if (it !in mutOffscreenPasses) {
                    mutOffscreenPasses += it
                }
            }
            addOffscreenPasses.clear()
        }
    }

    private fun removeOffscreenPasses() {
        if (remOffscreenPasses.isNotEmpty()) {
            mutOffscreenPasses.removeAll(remOffscreenPasses)
            remOffscreenPasses.clear()
        }
    }

    fun renderScene(ctx: KoolContext) {
        for (i in onRenderScene.indices) {
            onRenderScene[i](ctx)
        }

        // remove all offscreen passes that were scheduled for removal in last frame
        removeOffscreenPasses()
        addOffscreenPasses()

        mainRenderPass.update(ctx)

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
        mainRenderPass.release()
        mutOffscreenPasses.removeAll(remOffscreenPasses)
        remOffscreenPasses.clear()
        for (i in offscreenPasses.indices) {
            offscreenPasses[i].release()
        }
        remOffscreenPasses.clear()
        mutOffscreenPasses.clear()
        capturedFramebuffer.dispose()

        super.release()

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
