package de.fabmax.kool.util.ao

import de.fabmax.kool.pipeline.NormalLinearDepthMapPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.deferred.DeferredMrtPass

abstract class AoPipeline {

    abstract val aoPass: AmbientOcclusionPass
    abstract val denoisePass: AoDenoisePass

    // ao map size relative to screen resolution
    var mapSize = 0.5f

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
        } else {
            denoisePass.clearAndDisable = true
        }
    }

    class ForwardAoPipeline(scene: Scene) : AoPipeline() {
        val depthPass: NormalLinearDepthMapPass
        override val aoPass: AmbientOcclusionPass
        override val denoisePass: AoDenoisePass

        private var mapWidth = 0
        private var mapHeight = 0

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
            denoisePass = AoDenoisePass(aoPass, depthPass.colorTexture!!, "a")
            //denoisePass.dependsOn(aoPass)

            scene.addOffscreenPass(depthPass)
            scene.addOffscreenPass(aoPass)
            scene.addOffscreenPass(denoisePass)

            scene.onRenderScene += { ctx ->
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
        }

        override fun updateEnabled() {
            super.updateEnabled()
            depthPass.isEnabled = isEnabled
        }
    }

    class DeferredAoPipeline(scene: Scene, mrtPass: DeferredMrtPass) : AoPipeline() {
        override val aoPass: AmbientOcclusionPass
        override val denoisePass: AoDenoisePass

        private var mapWidth = 0
        private var mapHeight = 0

        init {
            aoPass = AmbientOcclusionPass(mrtPass.camera, AoSetup.deferred(mrtPass), mapWidth, mapHeight)
            aoPass.dependsOn(mrtPass)
            denoisePass = AoDenoisePass(aoPass, mrtPass.positionAo, "z")
            //denoisePass.dependsOn(aoPass)

            scene.addOffscreenPass(aoPass)
            scene.addOffscreenPass(denoisePass)

            scene.onRenderScene += { ctx ->
                val mapW = (mrtPass.width * mapSize).toInt()
                val mapH = (mrtPass.height * mapSize).toInt()

                if (isEnabled && mapW > 0 && mapH > 0 && (mapW != aoPass.width || mapH != aoPass.height)) {
                    aoPass.resize(mapW, mapH, ctx)
                }
                if (isEnabled && mapW > 0 && mapH > 0 && (mapW != denoisePass.width || mapH != denoisePass.height)) {
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