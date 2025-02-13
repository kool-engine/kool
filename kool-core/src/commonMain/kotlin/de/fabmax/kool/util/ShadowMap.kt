package de.fabmax.kool.util

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.scene.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

sealed interface ShadowMap {
    var light: Light?
    var isShadowMapEnabled: Boolean
    val subMaps: List<SimpleShadowMap>
}

class SimpleShadowMap(
    var sceneCam: Camera,
    drawNode: Node,
    override var light: Light?,
    mapSize: Int = 2048,
    name: String = UniqueId.nextId("simple-shadow-map")
) :
    DepthMapPass(
        drawNode = drawNode,
        attachmentConfig = AttachmentConfig {
            depth {
                textureFormat = TexFormat.R_F32
                samplerSettings = SamplerSettings(compareOp = DepthCompareOp.LESS).clamped().linear()
            }
        },
        initialSize = Vec2i(mapSize),
        name = name
    ),
    ShadowMap
{
    constructor(scene: Scene, light: Light?, drawNode: Node = scene, mapSize: Int = 2048): this(scene.camera, drawNode, light, mapSize) {
        scene.addOffscreenPass(this)
    }

    val lightViewProjMat = MutableMat4f()

    var shadowMapLevel = 0
    var clipNear = 1f
    var clipFar = 100f
    var directionalCamNearOffset = -20f
    var shaderDepthOffset = -0.005f
    var shadowBounds: BoundingBoxF? = null

    private val biasMatrix = MutableMat4f()

    private val nearSceneCamPlane = FrustumPlane()
    private val farSceneCamPlane = FrustumPlane()
    private val shadowCamBounds = BoundingBoxF()
    private val tmpVec = MutableVec3f()

    var shadowBoundsMod: ((BoundingBoxF) -> Unit)? = null

    override var isShadowMapEnabled: Boolean
        get() = isEnabled
        set(value) { isEnabled = value }

    override val subMaps = listOf(this)

    init {
        isUpdateDrawNode = false
        isReleaseDrawNode = false

        defaultView.drawFilter = {
            it !is Mesh || it.isCastingShadow(shadowMapLevel)
        }

        val backend = KoolSystem.requireContext().backend
        if (backend.depthRange == DepthRange.ZERO_TO_ONE) {
            biasMatrix.set(depthBiasMatrixZeroToOne)
        } else {
            biasMatrix.set(depthBiasMatrixNegOneToOne)
        }
        if (backend.isInvertedNdcY) {
            biasMatrix[1, 1] = -biasMatrix[1, 1]
        }

        onBeforeCollectDrawCommands += { ev ->
            light?.let { setupCamera(it) }
            camera.updateCamera(ev)
            biasMatrix.mul(camera.viewProj, lightViewProjMat)
        }
    }

    override fun setupDrawCommand(cmd: DrawCommand, updateEvent: UpdateEvent) {
        super.setupDrawCommand(cmd, updateEvent)
        if (cmd.geometry === cmd.mesh.geometry && cmd.mesh.shadowGeometry.isNotEmpty()) {
            cmd.geometry = cmd.mesh.shadowGeometry[min(cmd.mesh.shadowGeometry.lastIndex, shadowMapLevel)]
        }
    }

    fun setDefaultDepthOffset(isDirectional: Boolean) {
        val szMultiplier = 2048f / width
        shaderDepthOffset = szMultiplier * if (isDirectional) -0.001f else -0.005f
    }

    private fun setupCamera(light: Light) {
        when (light) {
            is Light.Directional -> setupDirectionalLightCamera(light)
            is Light.Spot -> setupSpotLightCamera(light)
            is Light.Point -> TODO()  // point lights require a cube map render passe instead of 2d...
        }
    }

    private fun setupSpotLightCamera(light: Light.Spot) {
        var cam = camera
        if (cam !is PerspectiveCamera) {
            cam = PerspectiveCamera()
            camera = cam
        }
        val up = if (abs(light.direction.dot(Vec3f.Y_AXIS)) > 0.99f) Vec3f.NEG_Z_AXIS else Vec3f.Y_AXIS
        cam.setupCamera(position = light.position, up = up)
        cam.lookAt.set(light.position).add(light.direction)

        cam.fovY = light.spotAngle
        cam.clipNear = clipNear
        cam.clipFar = clipFar
    }

    private fun setupDirectionalLightCamera(light: Light.Directional) {
        var cam = camera
        if (cam !is OrthographicCamera) {
            cam = OrthographicCamera().apply { isKeepAspectRatio = false }
            camera = cam
        }
        val up = if (abs(light.direction.dot(Vec3f.Y_AXIS)) > 0.99f) Vec3f.NEG_Z_AXIS else Vec3f.Y_AXIS
        cam.setupCamera(position = Vec3f.ZERO, up = up, lookAt = light.direction)

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
        cam.clipNear = -shadowCamBounds.max.z + directionalCamNearOffset
        cam.clipFar = -shadowCamBounds.min.z
    }

    private fun Mat4f.transform(plane: FrustumPlane) {
        transform(plane.upperLeft)
        transform(plane.upperRight)
        transform(plane.lowerLeft)
        transform(plane.lowerRight)
    }

    private fun BoundingBoxF.setPlanes(near: FrustumPlane, far: FrustumPlane) = batchUpdate {
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

    companion object {
        val depthBiasMatrixNegOneToOne: Mat4f = MutableMat4f().translate(0.5f, 0.5f, 0.5f).scale(0.5f)

        val depthBiasMatrixZeroToOne = Mat4f(
            0.5f, 0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.0f, 0.5f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    }
}

class CascadedShadowMap(
    sceneCam: Camera,
    drawNode: Node,
    light: Light? = null,
    var maxRange: Float = 100f,
    val numCascades: Int = 3,
    nearOffset: Float = -20f,
    mapSizes: List<Int>? = null
) : ShadowMap {

    constructor(
        scene: Scene,
        light: Light? = null,
        maxRange: Float = 100f,
        numCascades: Int = 3,
        nearOffset: Float = -20f,
        mapSizes: List<Int>? = null,
        drawNode: Node = scene
    ): this(scene.camera, drawNode, light, maxRange, numCascades, nearOffset, mapSizes) {
        subMaps.forEach { scene.addOffscreenPass(it) }
    }

    override var light: Light? = light
        set(value) {
            field = value
            subMaps.forEach { it.light = value }
        }

    val mapRanges = Array(numCascades) { i ->
        val near = i.toFloat().pow(2) / numCascades.toFloat().pow(2) + 0.001f
        val far = (i + 1).toFloat().pow(2) / numCascades.toFloat().pow(2)
        MapRange(near, far)
    }

    override val subMaps = List(numCascades) { level ->
        SimpleShadowMap(sceneCam, drawNode, light, mapSizes?.get(level) ?: 2048, "cascaded-shadopw-map[$level]").apply {
            shadowMapLevel = level
            directionalCamNearOffset = nearOffset
            setDefaultDepthOffset(true)
        }
    }

    var drawNode: Node
        get() = subMaps[0].drawNode
        set(value) {
            subMaps.forEach { it.drawNode = value }
        }

    override var isShadowMapEnabled: Boolean
        get() = subMaps[0].isEnabled
        set(value) { subMaps.forEach { it.isEnabled = value } }

    init {
        check(numCascades <= 8) { "Too many shadow cascades: $numCascades (maximum is 8)" }

        subMaps[0].onBeforeCollectDrawCommands += {
            for (i in 0 until numCascades) {
                val near = mapRanges[i].near * maxRange
                val far = mapRanges[i].far * maxRange
                val farOverlap = 2f * sqrt(far)
                subMaps[i].clipNear = near
                subMaps[i].clipFar = far + farOverlap
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