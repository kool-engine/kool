package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.OffscreenRenderPass2D
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene

class ShadowMapPass(scene: Scene, val light: Light, mapSize: Int = 1024) : OffscreenRenderPass2D(scene, mapSize, mapSize) {

    private val shadowPipelines = mutableMapOf<Long, Pipeline>()

    init {
        type = Type.SHADOW
        isUpdateDrawNode = false

        val shadowCam = PerspectiveCamera().apply {
            clipNear = 1f
            clipFar = 100f
            isApplyProjCorrection = false
        }
        camera = shadowCam

        onBeforeCollectDrawCommands += { ctx ->
            // setup shadow camera
            shadowCam.apply {
                position.set(light.position)
                lookAt.set(light.position).add(light.direction)
                fovY = light.spotAngle
            }
            shadowCam.updateCamera(ctx, viewport)
            ctx.depthBiasMatrix.mul(camera.mvp, light.lightMvpMat)
        }

        onAfterCollectDrawCommands += { ctx ->
            // replace regular object shaders by cheaper shadow versions
            drawQueue.commands.forEach {
                it.pipeline = getShadowPipeline(it.mesh, ctx)
            }
        }
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        if (light.type == Light.Type.SPOT) {
            super.collectDrawCommands(ctx)

        } else {
            // todo: support non spot lights...
            drawQueue.clear()
            light.lightMvpMat.setIdentity()
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
}