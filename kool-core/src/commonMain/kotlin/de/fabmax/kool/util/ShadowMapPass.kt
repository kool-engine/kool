package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.OffscreenPass2d
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*

class ShadowMapPass(val scene: Scene, val light: Light, mapSize: Int = 2048) {

    val offscreenPass = OffscreenPass2d(mapSize, mapSize)
    val shadowCam = PerspectiveCamera().apply {
        clipNear = 1f
        clipFar = 25f
        position.set(light.position)
        lookAt.set(light.position).add(light.direction)
        fovY = light.spotAngle
        isApplyProjCorrection = false
    }

    private var tempCam: Camera = shadowCam
    private val dummyMeshes = mutableMapOf<Set<Attribute>, Pipeline>()

    init {
        offscreenPass.scene = scene
        offscreenPass.isMainPass = false

        offscreenPass.beforeRender += { ctx ->
            tempCam = scene.camera
            scene.camera = shadowCam

            shadowCam.updateCamera(ctx)
            ctx.depthBiasMatrix.mul(ctx.mvpState.mvpMatrix, light.lightMvpMat)
        }
        offscreenPass.afterRender += { ctx ->
            scene.camera = tempCam
            offscreenPass.drawQueues[0].commands.forEach {
                it.pipeline = getShadowPipeline(it.mesh, ctx)
            }
        }
    }

    private fun getShadowPipeline(actualMesh: Mesh, ctx: KoolContext): Pipeline? {
        return dummyMeshes.getOrPut(actualMesh.geometry.vertexAttributes) {
            // create a minimal dummy shader for each attribute set
            val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
                vertexStage { positionOutput = simpleVertexPositionNode().outPosition }
                fragmentStage { colorOutput = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO)) }
            })
            shadowShader.createPipeline(actualMesh, Pipeline.Builder(), ctx)
        }
    }

    fun dispose(ctx: KoolContext) {
        offscreenPass.dispose(ctx)
    }
}