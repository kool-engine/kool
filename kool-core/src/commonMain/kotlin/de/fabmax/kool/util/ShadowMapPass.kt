package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.OffscreenPass2d
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*

class ShadowMapPass(val scene: Scene, val light: Light, mapSize: Int = 1024) {

    val offscreenPass = OffscreenPass2d(mapSize, mapSize)
    val shadowCam = PerspectiveCamera().apply {
        clipNear = 1f
        clipFar = 100f
        isApplyProjCorrection = false
    }

    private var tempCam: Camera = shadowCam
    private val shadowPipelines = mutableMapOf<Long, Pipeline>()

    init {
        offscreenPass.drawQueues[0].meshFilter = { it.isCastingShadow }
        offscreenPass.scene = scene
        offscreenPass.isMainPass = false

        offscreenPass.beforeRender += { ctx ->
            tempCam = scene.camera

            shadowCam.position.set(light.position)
            shadowCam.lookAt.set(light.position).add(light.direction)
            shadowCam.fovY = light.spotAngle
            scene.camera = shadowCam

            shadowCam.updateCamera(ctx)
            ctx.depthBiasMatrix.mul(ctx.mvpState.mvpMatrix, light.lightMvpMat)
        }
        offscreenPass.afterRender += { ctx ->
            scene.camera = tempCam
            if (light.type != Light.Type.SPOT) {
                // todo: support non spot lights...
                offscreenPass.drawQueues[0].clear()
                light.lightMvpMat.setIdentity()

            } else {
                offscreenPass.drawQueues[0].commands.forEach {
                    it.pipeline = getShadowPipeline(it.mesh, ctx)
                }
            }
        }
    }

    private fun getShadowPipeline(actualMesh: Mesh, ctx: KoolContext): Pipeline? {
        return shadowPipelines.getOrPut(actualMesh.geometry.attributeHash) {
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