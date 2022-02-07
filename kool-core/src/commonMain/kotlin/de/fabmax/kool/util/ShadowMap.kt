package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.DepthMapPass
import de.fabmax.kool.pipeline.TextureSampler2d
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

interface ShadowMap {
    var isShadowMapEnabled: Boolean
    fun setupSampler(sampler: TextureSampler2d?)
}

class SimpleShadowMap(val scene: Scene, val lightIndex: Int, mapSize: Int = 2048, drawNode: Node = scene) :
        DepthMapPass(drawNode, renderPassConfig {
            name = "SimpleShadowMap"
            setSize(mapSize, mapSize)
            setDepthTexture(true)
            clearColorTexture()
        }), ShadowMap {

    val lightViewProjMat = Mat4d()

    var shadowMapLevel = 0
    var sceneCam = scene.camera
    var clipNear = 1f
    var clipFar = 100f
    var directionalCamNearOffset = -20f
    var shaderDepthOffset = -0.005f
    var shadowBounds: BoundingBox? = null

    private val nearSceneCamPlane = FrustumPlane()
    private val farSceneCamPlane = FrustumPlane()
    private val shadowCamBounds = BoundingBox()
    private val tmpVec = MutableVec3f()

    var shadowBoundsMod: ((BoundingBox) -> Unit)? = null

    override var isShadowMapEnabled: Boolean
        get() = isEnabled
        set(value) { isEnabled = value }

    init {
        isUpdateDrawNode = false
        scene.addOffscreenPass(this)

        drawMeshFilter = {
            it.isCastingShadow(shadowMapLevel)
        }

        onBeforeCollectDrawCommands += { ctx ->
            if (lightIndex < scene.lighting.lights.size) {
                val light = scene.lighting.lights[lightIndex]
                setupCamera(light)
                camera.updateCamera(this, ctx)
                ctx.depthBiasMatrix.mul(camera.viewProj, lightViewProjMat)
            }
        }
    }

    override fun setupDrawCommand(cmd: DrawCommand, ctx: KoolContext) {
        super.setupDrawCommand(cmd, ctx)
        if (cmd.geometry === cmd.mesh.geometry && cmd.mesh.shadowGeometry.isNotEmpty()) {
            cmd.geometry = cmd.mesh.shadowGeometry[min(cmd.mesh.shadowGeometry.lastIndex, shadowMapLevel)]
        }
    }

    fun setDefaultDepthOffset(isDirectional: Boolean) {
        val szMultiplier = 2048f / width
        shaderDepthOffset = szMultiplier * if (isDirectional) -0.001f else -0.005f
    }

    override fun dispose(ctx: KoolContext) {
        scene.removeOffscreenPass(this)
        super.dispose(ctx)
    }

    override fun setupSampler(sampler: TextureSampler2d?) {
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
            cam = OrthographicCamera().apply { isKeepAspectRatio = false }
            cam.projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
            camera = cam
        }
        cam.position.set(Vec3f.ZERO)
        cam.lookAt.set(light.direction)

        if (abs(light.direction * Vec3f.Y_AXIS) > 0.99f) {
            cam.up.set(Vec3f.NEG_Z_AXIS)
        } else {
            cam.up.set(Vec3f.Y_AXIS)
        }

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

        shadowBoundsMod?.invoke(shadowCamBounds)

        cam.left = shadowCamBounds.min.x
        cam.right = shadowCamBounds.max.x
        cam.bottom = shadowCamBounds.min.y
        cam.top = shadowCamBounds.max.y
        cam.near = -shadowCamBounds.max.z + directionalCamNearOffset
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

class CascadedShadowMap(scene: Scene, val lightIndex: Int, var maxRange: Float = 100f, val numCascades: Int = 3, mapSizes: List<Int>? = null, drawNode: Node = scene) : ShadowMap {
    val mapRanges = Array(numCascades) { i ->
        val near = i.toFloat().pow(2) / numCascades.toFloat().pow(2)
        val far = (i + 1).toFloat().pow(2) / numCascades.toFloat().pow(2)
        MapRange(near, far)
    }

    val cascades = Array(numCascades) { level ->
        SimpleShadowMap(scene, lightIndex, mapSizes?.get(level) ?: 2048, drawNode).apply {
            name = "CascadedShadopwMap-level-$level"
            shadowMapLevel = level
            setDefaultDepthOffset(true)
        }
    }

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

    override fun setupSampler(sampler: TextureSampler2d?) {
        if (sampler != null) {
            cascades.forEachIndexed { i, cascade ->
                sampler.textures[i] = cascade.depthTexture
            }
        }
    }

    class MapRange(var near: Float, var far: Float) {
        fun set(near: Float, far: Float) {
            this.near = near
            this.far = far
        }

        override fun toString(): String {
            return "[$near..$far]"
        }
    }
}