package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.DepthMapPass
import de.fabmax.kool.pipeline.TextureSampler
import de.fabmax.kool.scene.*
import kotlin.math.pow

interface ShadowMap {
    fun setupSampler(sampler: TextureSampler?)
}

class SimpleShadowMap(val scene: Scene, val lightIndex: Int, mapSize: Int = 1024) : DepthMapPass(scene, mapSize), ShadowMap {

    val lightViewProjMat = Mat4d()

    var clipNear = 1f
    var clipFar = 100f

    private val viewMat = Mat4d()
    private val nearSceneCamPlane = FrustumPlane()
    private val farSceneCamPlane = FrustumPlane()
    private val sceneFrustumBounds = BoundingBox()

    init {
        isUpdateDrawNode = false
        scene.addOffscreenPass(this)

        onBeforeCollectDrawCommands += { ctx ->
            if (lightIndex < scene.lighting.lights.size) {
                val light = scene.lighting.lights[lightIndex]
                setupCamera(light)
                camera.updateCamera(ctx, viewport)
                ctx.depthBiasMatrix.mul(camera.viewProj, lightViewProjMat)
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        scene.removeOffscreenPass(this)
        super.dispose(ctx)
    }

    override fun setupSampler(sampler: TextureSampler?) {
        sampler?.texture = depthTexture
    }

    private fun setupCamera(light: Light) {
        when (light.type) {
            Light.Type.DIRECTIONAL -> setupDirectionalLightCamera(light)
            Light.Type.SPOT -> setupSpotLightCamera(light)
            Light.Type.POINT -> TODO()  // point lights require a cube map render passe instead of 2d...
        }
    }

    private fun setupSpotLightCamera(light: Light) {
        var cam = camera
        if (cam !is PerspectiveCamera) {
            cam = PerspectiveCamera()
            cam.projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
            camera = cam
        }
        cam.position.set(light.position)
        cam.lookAt.set(light.position).add(light.direction)

        cam.fovY = light.spotAngle
        cam.clipNear = clipNear
        cam.clipFar = clipFar
    }

    private fun setupDirectionalLightCamera(light: Light) {
        var cam = camera
        if (cam !is OrthographicCamera) {
            cam = OrthographicCamera()
            cam.projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
            camera = cam
        }
        cam.position.set(Vec3f.ZERO)
        cam.lookAt.set(light.direction)

        scene.camera.computeFrustumPlane(clipNear, nearSceneCamPlane)
        scene.camera.computeFrustumPlane(clipFar, farSceneCamPlane)

        viewMat.setLookAt(cam.position, cam.lookAt, cam.up)
        viewMat.transform(nearSceneCamPlane)
        viewMat.transform(farSceneCamPlane)
        sceneFrustumBounds.setPlanes(nearSceneCamPlane, farSceneCamPlane)

        cam.left = sceneFrustumBounds.min.x
        cam.right = sceneFrustumBounds.max.x
        cam.bottom = sceneFrustumBounds.min.y
        cam.top = sceneFrustumBounds.max.y
        cam.near = -sceneFrustumBounds.max.z - 20
        cam.far = -sceneFrustumBounds.min.z
    }

    private fun Mat4d.transform(plane: FrustumPlane) {
        transform(plane.upperLeft)
        transform(plane.upperRight)
        transform(plane.lowerLeft)
        transform(plane.lowerRight)
    }

    private fun BoundingBox.setPlanes(near: FrustumPlane, far: FrustumPlane) = batchUpdate {
        clear()
        add(near.upperLeft)
        add(near.upperRight)
        add(near.lowerLeft)
        add(near.lowerRight)
        add(far.upperLeft)
        add(far.upperRight)
        add(far.lowerLeft)
        add(far.lowerRight)
    }
}

class CascadedShadowMap(scene: Scene, val lightIndex: Int, val numCascades: Int = 3, mapSize: Int = 1024) : ShadowMap {
    val mapRanges = Array(numCascades) { i ->
        val near = i.toFloat().pow(2) / numCascades.toFloat().pow(2)
        val far = (i + 1).toFloat().pow(2) / numCascades.toFloat().pow(2)
        MapRange(near, far)
    }
    var maxRange = 100f

    val cascades = Array(numCascades) { SimpleShadowMap(scene, lightIndex, mapSize) }
    val clipSpaceRanges = FloatArray(numCascades)

    init {
        val farPlane = FrustumPlane()
        val clipSpacePos = MutableVec3f()
        cascades[0].onBeforeCollectDrawCommands += {
            for (i in 0 until numCascades) {
                cascades[i].clipNear = mapRanges[i].near * maxRange
                cascades[i].clipFar = mapRanges[i].far * maxRange

                scene.camera.computeFrustumPlane(cascades[i].clipFar, farPlane)
                scene.camera.project(farPlane.upperLeft, clipSpacePos)
                clipSpaceRanges[i] = clipSpacePos.z
            }
        }
    }

    override fun setupSampler(sampler: TextureSampler?) {
        if (sampler != null) {
            cascades.forEachIndexed { i, cascade ->
                sampler.textures[i] = cascade.depthTexture
            }
        }
    }

    class MapRange(var near: Float, var far: Float)
}