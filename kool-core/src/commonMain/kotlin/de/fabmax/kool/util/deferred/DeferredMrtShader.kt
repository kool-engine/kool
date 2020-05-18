package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureSampler
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.util.Color

class DeferredMrtShader(cfg: MrtPbrConfig, model: ShaderModel = defaultMrtPbrModel(cfg)) : ModeledShader(model) {

    // Simple material props
    private var uRoughness: PushConstantNode1f? = null
    private var uMetallic: PushConstantNode1f? = null
    private var uAlbedo: PushConstantNodeColor? = null

    var metallic = cfg.metallic
        set(value) {
            field = value
            uMetallic?.uniform?.value = value
        }

    var roughness = cfg.roughness
        set(value) {
            field = value
            uRoughness?.uniform?.value = value
        }

    var albedo: Color = cfg.albedo
        set(value) {
            field = value
            uAlbedo?.uniform?.value?.set(value)
        }

    // Material maps
    private var albedoSampler: TextureSampler? = null
    private var normalSampler: TextureSampler? = null
    private var metallicSampler: TextureSampler? = null
    private var roughnessSampler: TextureSampler? = null
    private var ambientOcclusionSampler: TextureSampler? = null
    private var displacementSampler: TextureSampler? = null
    private var uDispStrength: PushConstantNode1f? = null

    var albedoMap: Texture? = cfg.albedoMap
        set(value) {
            field = value
            albedoSampler?.texture = value
        }
    var normalMap: Texture? = cfg.normalMap
        set(value) {
            field = value
            normalSampler?.texture = value
        }
    var metallicMap: Texture? = cfg.metallicMap
        set(value) {
            field = value
            metallicSampler?.texture = value
        }
    var roughnessMap: Texture? = cfg.roughnessMap
        set(value) {
            field = value
            roughnessSampler?.texture = value
        }
    var ambientOcclusionMap: Texture? = cfg.ambientOcclusionMap
        set(value) {
            field = value
            ambientOcclusionSampler?.texture = value
        }
    var displacementMap: Texture? = cfg.displacementMap
        set(value) {
            field = value
            displacementSampler?.texture = value
        }
    var displacementStrength = 0.1f
        set(value) {
            field = value
            uDispStrength?.uniform?.value = value
        }

    override fun onPipelineCreated(pipeline: Pipeline) {
        uMetallic = model.findNode("uMetallic")
        uMetallic?.let { it.uniform.value = metallic }
        uRoughness = model.findNode("uRoughness")
        uRoughness?.let { it.uniform.value = roughness }
        uAlbedo = model.findNode("uAlbedo")
        uAlbedo?.uniform?.value?.set(albedo)

        albedoSampler = model.findNode<TextureNode>("tAlbedo")?.sampler
        albedoSampler?.let { it.texture = albedoMap }
        normalSampler = model.findNode<TextureNode>("tNormal")?.sampler
        normalSampler?.let { it.texture = normalMap }
        metallicSampler = model.findNode<TextureNode>("tMetallic")?.sampler
        metallicSampler?.let { it.texture = metallicMap }
        roughnessSampler = model.findNode<TextureNode>("tRoughness")?.sampler
        roughnessSampler?.let { it.texture = roughnessMap }
        ambientOcclusionSampler = model.findNode<TextureNode>("tAmbOccl")?.sampler
        ambientOcclusionSampler?.let { it.texture = ambientOcclusionMap }
        displacementSampler = model.findNode<TextureNode>("tDisplacement")?.sampler
        displacementSampler?.let { it.texture = displacementMap }
        uDispStrength = model.findNode("uDispStrength")
        uDispStrength?.let { it.uniform.value = displacementStrength }

        super.onPipelineCreated(pipeline)
    }

    companion object {
        fun defaultMrtPbrModel(cfg: MrtPbrConfig) = ShaderModel("defaultMrtPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifNormals: StageInterfaceNode
            val ifTangents: StageInterfaceNode?
            val ifViewPos: StageInterfaceNode
            val ifTexCoords: StageInterfaceNode?
            val mvpNode: UniformBufferMvp

            vertexStage {
                val modelViewMat: ShaderNodeIoVar
                val mvpMat: ShaderNodeIoVar

                mvpNode = mvpNode()

                if (cfg.isInstanced) {
                    val modelMat = multiplyNode(mvpNode.outModelMat, instanceAttrModelMat().output).output
                    modelViewMat = multiplyNode(modelMat, mvpNode.outViewMat).output
                    mvpMat = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output
                } else {
                    modelViewMat = multiplyNode(mvpNode.outModelMat, mvpNode.outViewMat).output
                    mvpMat = mvpNode.outMvpMat
                }

                val nrm = transformNode(attrNormals().output, modelViewMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.output)

                ifTexCoords = if (cfg.requiresTexCoords()) {
                    stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                } else {
                    null
                }

                val worldPos = if (cfg.isDisplacementMapped) {
                    val dispTex = textureNode("tDisplacement")
                    val dispNd = displacementMapNode(dispTex, ifTexCoords!!.input, attrPositions().output, attrNormals().output).apply {
                        inStrength = pushConstantNode1f("uDispStrength").output
                    }
                    dispNd.outPosition
                } else {
                    attrPositions().output
                }
                val pos = transformNode(worldPos, modelViewMat, 1f).output
                ifViewPos = stageInterfaceNode("ifViewPos", pos)

                ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifTangents = if (cfg.isNormalMapped) {
                    val tan = transformNode(attrTangents().output, modelViewMat, 0f)
                    stageInterfaceNode("ifTangents", tan.output)
                } else {
                    null
                }

                positionOutput = vertexPositionNode(worldPos, mvpMat).outPosition
            }
            fragmentStage {
                val viewPos = ifViewPos.output

                val albedo = when (cfg.albedoSource) {
                    Albedo.VERTEX_ALBEDO -> ifColors!!.output
                    Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uAlbedo").output
                    Albedo.TEXTURE_ALBEDO -> {
                        val albedoSampler = textureSamplerNode(textureNode("tAlbedo"), ifTexCoords!!.output, false)
                        val albedoLin = gammaNode(albedoSampler.outColor)
                        albedoLin.outColor
                    }
                }

                val normal = if (cfg.isNormalMapped && ifTangents != null) {
                    val bumpNormal = normalMapNode(textureNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                    bumpNormal.inStrength = ShaderNodeIoVar(ModelVar1fConst(cfg.normalStrength))
                    bumpNormal.outNormal
                } else {
                    ifNormals.output
                }

                val metallic = if (cfg.isMetallicMapped) {
                    textureSamplerNode(textureNode("tMetallic"), ifTexCoords!!.output, false).outColor
                } else {
                    pushConstantNode1f("uMetallic").output
                }

                val roughness = if (cfg.isRoughnessMapped) {
                    textureSamplerNode(textureNode("tRoughness"), ifTexCoords!!.output, false).outColor
                } else {
                    pushConstantNode1f("uRoughness").output
                }

                val aoFactor = if (cfg.isAmbientOcclusionMapped) {
                    textureSamplerNode(textureNode("tAmbOccl"), ifTexCoords!!.output, false).outColor
                } else {
                    ShaderNodeIoVar(ModelVar1fConst(1f))
                }

                val mrtMultiplexNode = addNode(MrtMultiplexNode(stage)).apply {
                    inViewPos = viewPos
                    inAlbedo = albedo
                    inNormal = normal
                    inRoughness = roughness
                    inMetallic = metallic
                    inAo = aoFactor
                }

                colorOutput(channels = 3).apply {
                    inColors[0] = mrtMultiplexNode.outPositionAo
                    inColors[1] = mrtMultiplexNode.outNormalRough
                    inColors[2] = mrtMultiplexNode.outAlbedoMetallic
                }
            }
        }
    }

    class MrtPbrConfig {
        var albedoSource = Albedo.VERTEX_ALBEDO
        var isNormalMapped = false
        var isRoughnessMapped = false
        var isMetallicMapped = false
        var isAmbientOcclusionMapped = false
        var isDisplacementMapped = false

        var normalStrength = 1f

        var isInstanced = false

        // initial shader values
        var albedo = Color.GRAY
        var roughness = 0.5f
        var metallic = 0.0f

        var albedoMap: Texture? = null
        var normalMap: Texture? = null
        var roughnessMap: Texture? = null
        var metallicMap: Texture? = null
        var ambientOcclusionMap: Texture? = null
        var displacementMap: Texture? = null

        fun requiresTexCoords(): Boolean {
            return albedoSource == Albedo.TEXTURE_ALBEDO ||
                    isNormalMapped ||
                    isRoughnessMapped ||
                    isMetallicMapped ||
                    isAmbientOcclusionMapped ||
                    isDisplacementMapped
        }
    }

    class MrtMultiplexNode(graph: ShaderGraph) : ShaderNode("mrtMultiplex", graph, ShaderStage.FRAGMENT_SHADER.mask) {
        var inViewPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inAlbedo = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0.5f))
        var inMetallic = ShaderNodeIoVar(ModelVar1fConst(0f))
        var inAo = ShaderNodeIoVar(ModelVar1fConst(1f))

        val outPositionAo = ShaderNodeIoVar(ModelVar4f("outPositionAo"), this)
        val outNormalRough = ShaderNodeIoVar(ModelVar4f("outNormalRough"), this)
        val outAlbedoMetallic = ShaderNodeIoVar(ModelVar4f("outAlbedoMetallic"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos, inAlbedo, inNormal, inRoughness, inMetallic, inAo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outPositionAo.declare()} = vec4(${inViewPos.ref3f()}, ${inAo.ref1f()});
                ${outNormalRough.declare()} = vec4(normalize(${inNormal.ref3f()}) * vec3(0.5) + vec3(0.5), ${inRoughness.ref1f()});
                ${outAlbedoMetallic.declare()} = vec4(${inAlbedo.ref3f()}, ${inMetallic.ref1f()});
            """)
        }
    }
}