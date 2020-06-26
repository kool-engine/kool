package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

/**
 * 1st pass shader for deferred pbr shading: Renders view space position, normals, albedo, roughness, metallic and
 * texture-based AO into three separate texture outputs.
 */
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

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.blendMode = BlendMode.DISABLED
        return super.createPipeline(mesh, builder, ctx)
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
                    modelViewMat = multiplyNode(mvpNode.outViewMat, modelMat).output
                    mvpMat = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output
                } else {
                    modelViewMat = multiplyNode(mvpNode.outViewMat, mvpNode.outModelMat).output
                    mvpMat = mvpNode.outMvpMat
                }

                val worldNrm = vec3TransformNode(attrNormals().output, modelViewMat, 0f).outVec3
                ifNormals = stageInterfaceNode("ifNormals", worldNrm)

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
                val pos = vec3TransformNode(worldPos, modelViewMat, 1f).outVec3
                ifViewPos = stageInterfaceNode("ifViewPos", pos)

                ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifTangents = if (cfg.isNormalMapped) {
                    val tan = vec3TransformNode(attrTangents().output, modelViewMat, 0f)
                    stageInterfaceNode("ifTangents", tan.outVec3)
                } else {
                    null
                }

                positionOutput = vec4TransformNode(worldPos, mvpMat).outVec4
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

                val viewNormal = if (cfg.isNormalMapped && ifTangents != null) {
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
                    inViewNormal = viewNormal
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
        var inViewNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0.5f))
        var inMetallic = ShaderNodeIoVar(ModelVar1fConst(0f))
        var inAo = ShaderNodeIoVar(ModelVar1fConst(1f))

        val outPositionAo = ShaderNodeIoVar(ModelVar4f("outPositionAo"), this)
        val outNormalRough = ShaderNodeIoVar(ModelVar4f("outNormalRough"), this)
        val outAlbedoMetallic = ShaderNodeIoVar(ModelVar4f("outAlbedoMetallic"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos, inAlbedo, inViewNormal, inRoughness, inMetallic, inAo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outPositionAo.declare()} = vec4(${inViewPos.ref3f()}, ${inAo.ref1f()});
                ${outNormalRough.declare()} = vec4(normalize(${inViewNormal.ref3f()}), ${inRoughness.ref1f()});
                ${outAlbedoMetallic.declare()} = vec4(${inAlbedo.ref3f()}, ${inMetallic.ref1f()});
            """)
        }
    }

    class MrtDeMultiplexNode(graph: ShaderGraph) : ShaderNode("mrtDeMultiplex", graph, ShaderStage.FRAGMENT_SHADER.mask) {
        var inPositionAo = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inNormalRough = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inAlbedoMetallic = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

        val outViewPos = ShaderNodeIoVar(ModelVar4f("outViewPos"), this)
        val outAlbedo = ShaderNodeIoVar(ModelVar4f("outAlbedo"), this)
        val outViewNormal = ShaderNodeIoVar(ModelVar3f("outViewNormal"), this)
        val outRoughness = ShaderNodeIoVar(ModelVar1f("outRoughness"), this)
        val outMetallic = ShaderNodeIoVar(ModelVar1f("outMetallic"), this)
        val outAo = ShaderNodeIoVar(ModelVar1f("outAo"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPositionAo, inNormalRough, inAlbedoMetallic)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outViewPos.declare()} = vec4(${inPositionAo.ref3f()}, 1.0);
                ${outAlbedo.declare()} = vec4(${inAlbedoMetallic.ref3f()}, 1.0);
                ${outViewNormal.declare()} = vec3(${inNormalRough.ref3f()});
                ${outRoughness.declare()} = ${inNormalRough.ref4f()}.a;
                ${outMetallic.declare()} = ${inAlbedoMetallic.ref4f()}.a;
                ${outAo.declare()} = ${inPositionAo.ref4f()}.a;
            """)
        }
    }
}