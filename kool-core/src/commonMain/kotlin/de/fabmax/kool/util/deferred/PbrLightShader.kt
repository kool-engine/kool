package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
class PbrLightShader(cfg: Config, model: ShaderModel = defaultDeferredPbrModel(cfg)) : ModeledShader(model) {

    private var deferredCameraNode: DeferredCameraNode? = null

    var sceneCamera: Camera? = cfg.sceneCamera
        set(value) {
            field = value
            deferredCameraNode?.sceneCam = value
        }

    private var positionAoSampler: TextureSampler? = null
    private var normalRoughnessSampler: TextureSampler? = null
    private var albedoMetalSampler: TextureSampler? = null

    var positionAo: Texture? = cfg.positionAo
        set(value) {
            field = value
            positionAoSampler?.texture = value
        }
    var normalRoughness: Texture? = cfg.normalRoughness
        set(value) {
            field = value
            normalRoughnessSampler?.texture = value
        }
    var albedoMetal: Texture? = cfg.albedoMetal
        set(value) {
            field = value
            albedoMetalSampler?.texture = value
        }

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.blendMode = BlendMode.BLEND_ADDITIVE
        builder.cullMethod = CullMethod.CULL_FRONT_FACES
        builder.depthTest = DepthCompareOp.DISABLED
        return super.createPipeline(mesh, builder, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline) {
        deferredCameraNode = model.findNode("deferredCam")
        deferredCameraNode?.let { it.sceneCam = sceneCamera }

        positionAoSampler = model.findNode<TextureNode>("positionAo")?.sampler
        positionAoSampler?.let { it.texture = positionAo }
        normalRoughnessSampler = model.findNode<TextureNode>("normalRoughness")?.sampler
        normalRoughnessSampler?.let { it.texture = normalRoughness }
        albedoMetalSampler = model.findNode<TextureNode>("albedoMetal")?.sampler
        albedoMetalSampler?.let { it.texture = albedoMetal }
    }

    companion object {
        fun defaultDeferredPbrModel(cfg: Config) = ShaderModel("defaultDeferredPbrModel()").apply {
            val ifFragCoords: StageInterfaceNode

            vertexStage {
                positionOutput = simpleVertexPositionNode().outVec4
                ifFragCoords = stageInterfaceNode("ifTexCoords", positionOutput)
            }
            fragmentStage {
                val xyPos = divideNode(channelNode(ifFragCoords.output, "xy").output, channelNode(ifFragCoords.output, "w").output).output
                val texPos = addNode(multiplyNode(xyPos, 0.5f).output, ShaderNodeIoVar(ModelVar1fConst(0.5f))).output

                val coord = texPos
                val mrtDeMultiplex = addNode(DeferredMrtShader.MrtDeMultiplexNode(stage)).apply {
                    inPositionAo = textureSamplerNode(textureNode("positionAo"), coord).outColor
                    inNormalRough = textureSamplerNode(textureNode("normalRoughness"), coord).outColor
                    inAlbedoMetallic = textureSamplerNode(textureNode("albedoMetal"), coord).outColor
                }

                addNode(DiscardClearNode(stage)).apply { inViewPos = mrtDeMultiplex.outViewPos }

                val defCam = addNode(DeferredCameraNode(stage))
                val worldPos = vec3TransformNode(mrtDeMultiplex.outViewPos, defCam.outInvViewMat, 1f).outVec3
                val worldNrm = vec3TransformNode(mrtDeMultiplex.outViewNormal, defCam.outInvViewMat, 0f).outVec3
                val lightNode = singleLightNode(cfg.light).apply { isReducedSoi = true }

                colorOutput(ShaderNodeIoVar(ModelVar4fConst(Color.DARK_GRAY.toLinear())))

                val mat = pbrLightNode(lightNode).apply {
                    flipBacksideNormals = cfg.flipBacksideNormals
                    inFragPos = worldPos
                    inCamPos = defCam.outCamPos

                    inAlbedo = gammaNode(mrtDeMultiplex.outAlbedo).outColor
                    inNormal = worldNrm
                    inMetallic = mrtDeMultiplex.outMetallic
                    inRoughness = mrtDeMultiplex.outRoughness
                }

                colorOutput(mat.outColor)
            }
        }
    }

    class DiscardClearNode(stage: ShaderGraph) : ShaderNode("discardClear", stage) {
        var inViewPos = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("if (${inViewPos.name}.z > 1.0) { discard; }")
        }
    }

    class DeferredCameraNode(stage: ShaderGraph) : ShaderNode("deferredCam", stage) {
        private val uInvViewMat = UniformMat4f("uInvViewMat")
        private val uCamPos = Uniform4f("uCamPos")
        private val uViewport = Uniform4f("uViewport")

        val outInvViewMat = ShaderNodeIoVar(ModelVarMat4f(uInvViewMat.name), this)
        val outCamPos = ShaderNodeIoVar(ModelVar4f(uCamPos.name), this)
        val outViewport = ShaderNodeIoVar(ModelVar4f(uViewport.name), this)

        var sceneCam: Camera? = null

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uInvViewMat }
                    +{ uCamPos }
                    +{ uViewport }
                    onUpdate = { _, cmd ->
                        val cam = sceneCam
                        if (cam != null) {
                            uInvViewMat.value.set(cam.invView)
                            uCamPos.value.set(cam.globalPos, 1f)
                        }
                        cmd.renderPass.viewport.let {
                            uViewport.value.set(it.x.toFloat(), it.y.toFloat(), it.width.toFloat(), it.height.toFloat())
                        }
                    }
                }
            }
        }

    }

    class Config {
        var sceneCamera: Camera? = null
        var light: Light? = null

        var flipBacksideNormals = false

        var positionAo: Texture? = null
        var normalRoughness: Texture? = null
        var albedoMetal: Texture? = null
    }
}