package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.OffscreenRenderPass2D
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*

class ShadowMapPass(val scene: Scene, val light: Light, mapSize: Int = 1024) : OffscreenRenderPass2D(scene, mapSize, mapSize) {

    private val shadowPipelines = mutableMapOf<Long, Pipeline>()

    var clipNear = 1f
    var clipFar = 100f

    private val viewMat = Mat4d()
    private val nearSceneCamPlane = FrustumPlane()
    private val farSceneCamPlane = FrustumPlane()
    private val sceneFrustumBounds = BoundingBox()

    init {
        type = Type.SHADOW
        isUpdateDrawNode = false
        clearColor = Color.BLACK

        onBeforeCollectDrawCommands += { ctx ->
            setupCamera(ctx)
            camera.updateCamera(ctx, viewport)
            ctx.depthBiasMatrix.mul(camera.viewProj, light.lightViewProjMat)
        }

        onAfterCollectDrawCommands += { ctx ->
            // replace regular object shaders by cheaper shadow versions
            drawQueue.commands.forEach {
                it.pipeline = getShadowPipeline(it.mesh, ctx)
            }
        }
    }

    private fun setupCamera(ctx: KoolContext) {
        when (light.type) {
            Light.Type.DIRECTIONAL -> setupDirectionalLightCamera(ctx)
            Light.Type.SPOT -> setupSpotLightCamera(ctx)
            Light.Type.POINT -> TODO()  // point lights require a cube map render passe instead of 2d...
        }
    }

    private fun setupSpotLightCamera(ctx: KoolContext) {
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

    private fun setupDirectionalLightCamera(ctx: KoolContext) {
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
        cam.near = -sceneFrustumBounds.max.z
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

    override fun collectDrawCommands(ctx: KoolContext) {
        if (light.type == Light.Type.SPOT || light.type == Light.Type.DIRECTIONAL) {
            super.collectDrawCommands(ctx)

        } else {
            // todo: unsupported light type...
            drawQueue.clear()
            light.lightViewProjMat.setIdentity()
        }
    }

    private fun getShadowPipeline(actualMesh: Mesh, ctx: KoolContext): Pipeline? {
        return shadowPipelines.getOrPut(actualMesh.geometry.attributeHash) {
            // create a minimal dummy shader for each attribute set
            val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
                vertexStage { positionOutput = simpleVertexPositionNode().outPosition }
                fragmentStage { colorOutput = ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f, 1f, 1f, 1f))) }
            })
            shadowShader.createPipeline(actualMesh, Pipeline.Builder(), ctx)
        }
    }
}