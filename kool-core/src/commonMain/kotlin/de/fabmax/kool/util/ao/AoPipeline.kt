package de.fabmax.kool.util.ao

import de.fabmax.kool.pipeline.NormalLinearDepthMapPass
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.deferred.DeferredMrtPass

abstract class AoPipeline {

    abstract val aoPass: AmbientOcclusionPass
    abstract val denoisePass: AoDenoisePass

    // ao map size relative to screen resolution
    var size = 0.5f

    val aoMap: Texture
        get() = denoisePass.colorTexture

    var radius: Float
        get() = aoPass.radius
        set(value) {
            aoPass.radius = value
            denoisePass.radius = value
        }

    var intensity: Float
        get() = aoPass.intensity
        set(value) { aoPass.intensity = value }

    var bias: Float
        get() = aoPass.bias
        set(value) { aoPass.bias = value }

    var kernelSz: Int
        get() = aoPass.kernelSz
        set(value) { aoPass.kernelSz = value }

    open fun setEnabled(enabled: Boolean) {
        aoPass.isEnabled = enabled
        denoisePass.isEnabled = enabled
    }

    class ForwardAoPipeline(scene: Scene) : AoPipeline() {
        val depthPass: NormalLinearDepthMapPass
        override val aoPass: AmbientOcclusionPass
        override val denoisePass: AoDenoisePass

        private var mapWidth = (1600 * size).toInt()
        private var mapHeight = (900 * size).toInt()

        init {
            val proxyCamera = PerspectiveCamera.Proxy(scene.camera as PerspectiveCamera)
            depthPass = NormalLinearDepthMapPass(scene, mapWidth, mapHeight)
            depthPass.camera = proxyCamera
            depthPass.isUpdateDrawNode = false
            depthPass.onBeforeCollectDrawCommands += { ctx ->
                proxyCamera.sync(scene.mainRenderPass.viewport, ctx)
            }

            aoPass = AmbientOcclusionPass(proxyCamera, AoSetup.forward(depthPass), mapWidth, mapHeight)
            aoPass.dependsOn(depthPass)
            denoisePass = AoDenoisePass(aoPass, depthPass.colorTexture, "a")
            denoisePass.dependsOn(aoPass)

            scene.addOffscreenPass(depthPass)
            scene.addOffscreenPass(aoPass)
            scene.addOffscreenPass(denoisePass)

            scene.onRenderScene += { ctx ->
                val mapW = (mainRenderPass.viewport.width * this@ForwardAoPipeline.size).toInt()
                val mapH = (mainRenderPass.viewport.height * this@ForwardAoPipeline.size).toInt()

                if (mapW > 0 && mapH > 0 && (mapW != mapWidth || mapH != mapHeight)) {
                    mapWidth = mapW
                    mapHeight = mapH
                    depthPass.resize(mapW, mapH, ctx)
                    aoPass.resize(mapW, mapH, ctx)
                    denoisePass.resize(mapW, mapH, ctx)
                }
            }
        }

        override fun setEnabled(enabled: Boolean) {
            super.setEnabled(enabled)
            depthPass.isEnabled = enabled
        }
    }

    class DeferredAoPipeline(scene: Scene, mrtPass: DeferredMrtPass) : AoPipeline() {
        override val aoPass: AmbientOcclusionPass
        override val denoisePass: AoDenoisePass

        private var mapWidth = (mrtPass.texWidth * size).toInt()
        private var mapHeight = (mrtPass.texHeight * size).toInt()

        init {
            aoPass = AmbientOcclusionPass(mrtPass.camera, AoSetup.deferred(mrtPass), mapWidth, mapHeight)
            aoPass.dependsOn(mrtPass)
            denoisePass = AoDenoisePass(aoPass, mrtPass.positionAo, "z")
            denoisePass.dependsOn(aoPass)

            scene.addOffscreenPass(aoPass)
            scene.addOffscreenPass(denoisePass)

            scene.onRenderScene += { ctx ->
                val mapW = (mrtPass.texWidth * this@DeferredAoPipeline.size).toInt()
                val mapH = (mrtPass.texHeight * this@DeferredAoPipeline.size).toInt()

                if (mapW > 0 && mapH > 0 && (mapW != mapWidth || mapH != mapHeight)) {
                    mapWidth = mapW
                    mapHeight = mapH
                    aoPass.resize(mapW, mapH, ctx)
                    denoisePass.resize(mapW, mapH, ctx)
                }
            }
        }
    }

    companion object {
        fun createForward(scene: Scene) = ForwardAoPipeline(scene)
        fun createDeferred(scene: Scene, mrtPass: DeferredMrtPass) = DeferredAoPipeline(scene, mrtPass)
    }
}