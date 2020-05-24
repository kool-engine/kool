package de.fabmax.kool.util.aoMapGen

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.NormalLinearDepthMapPass
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene

class AmbientOcclusionHelper(scene: Scene) {

    val depthPass: NormalLinearDepthMapPass
    val aoPass: AmbientOcclusionPass
    val denoisePass: AoDenoisePass

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

    // ao map size relative to screen resolution
    var size = 0.5f
    private var mapWidth = (1600 * size).toInt()
    private var mapHeight = (900 * size).toInt()

    init {
        val proxyCamera = ProxyCamera(scene.camera as PerspectiveCamera)
        depthPass = NormalLinearDepthMapPass(scene, mapWidth, mapHeight)
        depthPass.camera = proxyCamera
        depthPass.isUpdateDrawNode = false
        depthPass.onBeforeCollectDrawCommands += { ctx ->
            proxyCamera.sync(scene.mainRenderPass.viewport, ctx)
        }

        aoPass = AmbientOcclusionPass(proxyCamera, depthPass)
        aoPass.dependsOn(depthPass)
        denoisePass = AoDenoisePass(aoPass, depthPass)
        denoisePass.dependsOn(aoPass)

        scene.addOffscreenPass(depthPass)
        scene.addOffscreenPass(aoPass)
        scene.addOffscreenPass(denoisePass)

        scene.onRenderScene += { ctx ->
            val mapW = (mainRenderPass.viewport.width * this@AmbientOcclusionHelper.size).toInt()
            val mapH = (mainRenderPass.viewport.height * this@AmbientOcclusionHelper.size).toInt()

            if (mapW > 0 && mapH > 0 && (mapW != mapWidth || mapH != mapHeight)) {
                mapWidth = mapW
                mapHeight = mapH
                depthPass.resize(mapW, mapH, ctx)
                aoPass.resize(mapW, mapH, ctx)
                denoisePass.resize(mapW, mapH, ctx)
            }
        }
    }

    fun setEnabled(enabled: Boolean) {
        depthPass.isEnabled = enabled
        aoPass.isEnabled = enabled
        denoisePass.isEnabled = enabled
    }

    private class ProxyCamera(val master: PerspectiveCamera) : PerspectiveCamera() {
        init {
            useViewportAspectRatio = false
            projCorrectionMode = ProjCorrectionMode.OFFSCREEN
        }

        fun sync(viewport: KoolContext.Viewport, ctx: KoolContext) {
            master.updateCamera(ctx, viewport)

            position.set(master.globalPos)
            lookAt.set(master.globalLookAt)
            up.set(master.globalUp)

            aspectRatio = master.aspectRatio
            fovY = master.fovY
            clipNear = master.clipNear
            clipFar = master.clipFar
        }
    }
}