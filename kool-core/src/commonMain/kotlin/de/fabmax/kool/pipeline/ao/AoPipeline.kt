package de.fabmax.kool.pipeline.ao

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.NormalLinearDepthMapPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.deferred.DeferredPassSwapListener
import de.fabmax.kool.pipeline.deferred.DeferredPasses
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.PerspectiveProxyCam
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.max

abstract class AoPipeline : Releasable {

    abstract val aoPass: AmbientOcclusionPass
    abstract val denoisePass: AoDenoisePass

    // ao map size relative to screen resolution
    var mapSize = 0.7f

    val aoMap: Texture2d
        get() = denoisePass.colorTexture!!

    var radius: Float
        get() = aoPass.radius
        set(value) {
            aoPass.radius = value
            denoisePass.radius = value
        }

    var strength: Float
        get() = aoPass.strength
        set(value) { aoPass.strength = value }

    var power: Float
        get() = aoPass.power
        set(value) { aoPass.power = value }

    var bias: Float
        get() = aoPass.bias
        set(value) { aoPass.bias = value }

    var kernelSz: Int
        get() = aoPass.kernelSz
        set(value) { aoPass.kernelSz = value }

    var isEnabled = true
        set(value) {
            field = value
            updateEnabled()
        }

    protected open fun updateEnabled() {
        aoPass.isEnabled = isEnabled

        if (isEnabled) {
            denoisePass.isEnabled = true
            denoisePass.clearAndDisable = false
            if (aoPass !in denoisePass.dependencies) {
                denoisePass.dependencies += aoPass
            }
        } else {
            denoisePass.clearAndDisable = true
            denoisePass.dependencies -= aoPass
        }
    }

    class ForwardAoPipeline(val scene: Scene) : AoPipeline() {
        val depthPass: NormalLinearDepthMapPass
        override val aoPass: AmbientOcclusionPass
        override val denoisePass: AoDenoisePass

        private var mapWidth = max(32, (scene.mainRenderPass.viewport.width * mapSize).toInt())
        private var mapHeight = max(32, (scene.mainRenderPass.viewport.height * mapSize).toInt())

        private val onRenderSceneCallback: (KoolContext) -> Unit = { onRenderScene(it) }

        val proxyCamera = PerspectiveProxyCam(scene.camera as PerspectiveCamera)

        init {
            depthPass = NormalLinearDepthMapPass(scene, mapWidth, mapHeight)
            depthPass.camera = proxyCamera
            depthPass.isUpdateDrawNode = false
            depthPass.onBeforeCollectDrawCommands += { ev ->
                proxyCamera.sync(ev)
            }

            aoPass = AmbientOcclusionPass(AoSetup.forward(depthPass), mapWidth, mapHeight)
            aoPass.sceneCam = proxyCamera
            aoPass.dependsOn(depthPass)
            denoisePass = AoDenoisePass(aoPass, "a")
            denoisePass.depth = depthPass.colorTexture
            denoisePass.dependsOn(aoPass)

            scene.addOffscreenPass(depthPass)
            scene.addOffscreenPass(aoPass)
            scene.addOffscreenPass(denoisePass)

            scene.onRenderScene += onRenderSceneCallback
        }

        private fun onRenderScene(ctx: KoolContext) {
            val mapW = (scene.mainRenderPass.viewport.width * mapSize).toInt()
            val mapH = (scene.mainRenderPass.viewport.height * mapSize).toInt()

            if (isEnabled && mapW > 0 && mapH > 0 && (mapW != aoPass.width || mapH != aoPass.height)) {
                depthPass.resize(mapW, mapH, ctx)
                aoPass.resize(mapW, mapH, ctx)
            }
            if (isEnabled && mapW > 0 && mapH > 0 && (mapW != denoisePass.width || mapH != denoisePass.height)) {
                denoisePass.resize(mapW, mapH, ctx)
            }
        }

        override fun updateEnabled() {
            super.updateEnabled()
            depthPass.isEnabled = isEnabled
        }

        override fun release() {
            launchOnMainThread {
                scene.removeOffscreenPass(depthPass)
                scene.removeOffscreenPass(aoPass)
                scene.removeOffscreenPass(denoisePass)
                depthPass.release()
                aoPass.release()
                denoisePass.release()
            }
        }
    }

    class DeferredAoPipeline(val deferredPipeline: DeferredPipeline) : AoPipeline(), DeferredPassSwapListener {
        override val aoPass: AmbientOcclusionPass
        override val denoisePass: AoDenoisePass

        private var mapWidth = max(32, (deferredPipeline.scene.mainRenderPass.viewport.width * mapSize).toInt())
        private var mapHeight = max(32, (deferredPipeline.scene.mainRenderPass.viewport.height * mapSize).toInt())

        init {
            aoPass = AmbientOcclusionPass(AoSetup.deferred(), mapWidth, mapHeight)
            denoisePass = AoDenoisePass(aoPass, "z")
            denoisePass.dependsOn(aoPass)

            deferredPipeline.passes.forEach { aoPass.dependsOn(it.materialPass) }

            deferredPipeline.scene.addOffscreenPass(aoPass)
            deferredPipeline.scene.addOffscreenPass(denoisePass)
        }

        override fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses) {
            aoPass.sceneCam = currentPasses.materialPass.mainView.camera
            aoPass.deferredPosition = currentPasses.materialPass.positionFlags
            aoPass.deferredNormal = currentPasses.materialPass.normalRoughness
            denoisePass.depth = currentPasses.materialPass.positionFlags
        }

        fun checkSize(viewportW: Int, viewportH: Int, ctx: KoolContext) {
            val width = (viewportW * mapSize).toInt().clamp(1, 4096)
            val height = (viewportH * mapSize).toInt().clamp(1, 4096)

            if (aoPass.isEnabled && (width != aoPass.width || height != aoPass.height)) {
                aoPass.resize(width, height, ctx)
            }
            if (denoisePass.isEnabled && (width != denoisePass.width || height != denoisePass.height)) {
                denoisePass.resize(width, height, ctx)
            }
        }

        override fun release() {
            launchOnMainThread {
                deferredPipeline.scene.removeOffscreenPass(aoPass)
                deferredPipeline.scene.removeOffscreenPass(denoisePass)
                aoPass.release()
                denoisePass.release()
            }
        }
    }

    companion object {
        fun createForward(scene: Scene) = ForwardAoPipeline(scene)
        fun createDeferred(deferredPipeline: DeferredPipeline) = DeferredAoPipeline(deferredPipeline)
    }
}