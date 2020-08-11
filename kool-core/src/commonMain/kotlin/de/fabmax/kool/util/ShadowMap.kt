package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.DepthMapPass
import de.fabmax.kool.pipeline.TextureSampler
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.*
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

interface ShadowMap {
    var isShadowMapEnabled: Boolean
    fun setupSampler(sampler: TextureSampler?)
}

class SimpleShadowMap(val scene: Scene, val lightIndex: Int, mapSize: Int = 2048, drawNode: Node = scene) :
        DepthMapPass(drawNode, renderPassConfig {
            name = "SimpleShadowMap"
            setSize(mapSize, mapSize)
            setDepthTexture(true)
            clearColorTexture()
        }), ShadowMap {

    val lightViewProjMat = Mat4d()

    var optimizeForDirectionalLight = false
    var sceneCam = scene.camera
    var clipNear = 1f
    var clipFar = 100f
    var shadowBounds: BoundingBox? = null

    private val nearSceneCamPlane = FrustumPlane()
    private val farSceneCamPlane = FrustumPlane()
    private val shadowCamBounds = BoundingBox()
    private val tmpVec = MutableVec3f()

    override var isShadowMapEnabled: Boolean
        get() = isEnabled
        set(value) { isEnabled = value }

    init {
        isUpdateDrawNode = false
        scene.addOffscreenPass(this)

        drawQueue.meshFilter = {
            it.isCastingShadow
        }

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

        val bounds = shadowBounds
        if (bounds != null) {
            shadowCamBounds.clear()
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.min.x, bounds.min.y, bounds.min.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.min.x, bounds.min.y, bounds.max.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.min.x, bounds.max.y, bounds.min.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.min.x, bounds.max.y, bounds.max.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.max.x, bounds.min.y, bounds.min.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.max.x, bounds.min.y, bounds.max.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.max.x, bounds.max.y, bounds.min.z), 1f))
            shadowCamBounds.add(cam.view.transform(tmpVec.set(bounds.max.x, bounds.max.y, bounds.max.z), 1f))

        } else {
            sceneCam.computeFrustumPlane(clipNear, nearSceneCamPlane)
            sceneCam.computeFrustumPlane(clipFar, farSceneCamPlane)

            cam.view.transform(nearSceneCamPlane)
            cam.view.transform(farSceneCamPlane)
            shadowCamBounds.setPlanes(nearSceneCamPlane, farSceneCamPlane)
        }

        cam.left = shadowCamBounds.min.x
        cam.right = shadowCamBounds.max.x
        cam.bottom = shadowCamBounds.min.y
        cam.top = shadowCamBounds.max.y
        cam.near = -shadowCamBounds.max.z - 20
        cam.far = -shadowCamBounds.min.z
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

class CascadedShadowMap(scene: Scene, val lightIndex: Int, var maxRange: Float = 100f, val numCascades: Int = 3, mapSize: Int = 2048, drawNode: Node = scene) : ShadowMap {
    val mapRanges = Array(numCascades) { i ->
        val near = i.toFloat().pow(2) / numCascades.toFloat().pow(2)
        val far = (i + 1).toFloat().pow(2) / numCascades.toFloat().pow(2)
        MapRange(near, far)
    }

    val cascades = Array(numCascades) { SimpleShadowMap(scene, lightIndex, mapSize, drawNode).apply { optimizeForDirectionalLight = true } }
    val viewSpaceRanges = FloatArray(numCascades)

    var drawNode: Node
        get() = cascades[0].drawNode
        set(value) {
            cascades.forEach { it.drawNode = value }
        }

    override var isShadowMapEnabled: Boolean
        get() = cascades[0].isEnabled
        set(value) { cascades.forEach { it.isEnabled = value } }

    init {
        if (numCascades > 8) {
            throw KoolException("Too many shadow cascades: $numCascades (maximum is 8)")
        }

        cascades[0].onBeforeCollectDrawCommands += {
            for (i in 0 until numCascades) {
                val near = mapRanges[i].near * maxRange
                val far = mapRanges[i].far * maxRange
                val farOverlap = 2f * sqrt(far)
                cascades[i].clipNear = near
                cascades[i].clipFar = far + farOverlap
                // view space z-axis points in negative direction -> depth values are negative
                viewSpaceRanges[i] = -far
            }
        }
    }

    fun setMapRanges(vararg farRanges: Float) {
        var near = 0f
        for (i in 0 until min(farRanges.size, mapRanges.size)) {
            mapRanges[i].near = near
            mapRanges[i].far = farRanges[i]
            near = farRanges[i]
        }
    }

    override fun setupSampler(sampler: TextureSampler?) {
        if (sampler != null) {
            cascades.forEachIndexed { i, cascade ->
                sampler.textures[i] = cascade.depthTexture
            }
        }
    }

    class MapRange(var near: Float, var far: Float) {
        override fun toString(): String {
            return "[$near..$far]"
        }
    }
}