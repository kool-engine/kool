package de.fabmax.kool

import de.fabmax.kool.input.Input
import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.GpuPass
import de.fabmax.kool.pipeline.OffscreenPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.ibl.BrdfLutPass
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.KoolDispatchers
import de.fabmax.kool.util.Time

/**
 * @author fabmax
 */

abstract class KoolContext {
    abstract val backend: RenderBackend
    abstract val window: KoolWindow

    private val frameDatas = List(2) { FrameData() }
    private var frameDataPtr = 0

    val onRender = BufferedList<(KoolContext) -> Unit>()
    val onShutdown = BufferedList<(KoolContext) -> Unit>()

    val scenes: BufferedList<Scene> = BufferedList()

    val backgroundScene = Scene("backgroundScene").apply { mainRenderPass.isEnabled = false }
    val backgroundPasses: BufferedList<GpuPass>
        get() = backgroundScene.extraPasses

    val defaultPbrBrdfLut: Texture2d by lazy {
        BrdfLutPass(backgroundScene)
            .also { pass -> addBackgroundRenderPass(pass) }.copyColor()
            .also { brdf -> onShutdown += { brdf.release() } }
    }

    abstract fun openUrl(url: String, sameWindow: Boolean = true)

    abstract fun run()

    abstract fun getSysInfos(): List<String>

    fun addBackgroundRenderPass(pass: OffscreenPass) = backgroundScene.addOffscreenPass(pass)
    fun removeBackgroundRenderPass(pass: OffscreenPass) = backgroundScene.removeOffscreenPass(pass)

    fun addBackgroundComputePass(pass: ComputePass) = backgroundScene.addComputePass(pass)
    fun removeBackgroundComputePass(pass: ComputePass) = backgroundScene.removeComputePass(pass)

    fun addScene(scene: Scene) {
        scenes += scene
    }

    fun removeScene(scene: Scene) {
        scenes -= scene
    }

    protected suspend fun render(dt: Double): FrameData {
        val frameData = frameDatas[frameDataPtr].also { it.reset() }
        frameDataPtr = ++frameDataPtr and 1

        Time.update(dt)
        Time.frameCount++

        Input.poll(this)

        KoolDispatchers.Frontend.executeDispatchedTasks()
        onRender.update()
        for (i in onRender.indices) {
            onRender[i](this)
        }

        if (!backgroundScene.isEmpty) {
            backgroundScene.collectScene(frameData, this)
        }

        // draw scene contents (back to front)
        scenes.update()
        for (i in scenes.indices) {
            if (scenes[i].isVisible) {
                scenes[i].collectScene(frameData, this)
            }
        }

        frameData.updatePipelineData()
        return frameData
    }

    protected fun FrameData.updatePipelineData() {
        for (pi in passData.indices) {
            val pass = passData[pi]
            for (vi in pass.viewData.indices) {
                val viewData = pass.viewData[vi]
                viewData.drawQueue.forEach { it.updatePipelineData() }
            }
            (pass.gpuPass as? ComputePass)?.let { computePass ->
                computePass.tasks.forEach { task -> task.pipeline.updatePipelineData(computePass) }
            }
        }
    }

    protected fun FrameData.syncData() {
        KoolDispatchers.Synced.executeDispatchedTasks()
        for (pi in passData.indices) {
            val pass = passData[pi]
            for (vi in pass.viewData.indices) {
                val viewData = pass.viewData[vi]
                viewData.drawQueue.view.viewPipelineData.captureBuffer()
                viewData.drawQueue.forEach { cmd ->
                    cmd.captureData()
                }
            }
            (pass.gpuPass as? ComputePass)?.let { computePass ->
                computePass.tasks.forEach { task -> task.pipeline.captureBuffer() }
            }
        }
    }

    companion object {
        const val KOOL_VERSION = "0.18.0-SNAPSHOT"
    }
}
