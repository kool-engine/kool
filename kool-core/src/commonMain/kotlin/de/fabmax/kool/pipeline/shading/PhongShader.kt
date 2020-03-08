package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureSampler
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.util.Color

class PhongShader(cfg: PhongConfig = PhongConfig(), model: ShaderModel = defaultPhongModel(cfg)) : ModeledShader(model) {

    private var uShininess: PushConstantNode1f? = null
    private var uSpecularIntensity: PushConstantNode1f? = null

    private var albedoSampler: TextureSampler? = null
    private var normalSampler: TextureSampler? = null
    private var uAlbedo: PushConstantNodeColor? = null

    var shininess = cfg.shininess
        set(value) {
            field = value
            uShininess?.uniform?.value = value
        }
    var specularIntensity = cfg.specularIntensity
        set(value) {
            field = value
            uSpecularIntensity?.uniform?.value = value
        }

    var albedo: Color = cfg.albedo
        set(value) {
            field = value
            uAlbedo?.uniform?.value?.set(value)
        }
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

    override fun onPipelineCreated(pipeline: Pipeline) {
        super.onPipelineCreated(pipeline)
        uShininess = model.findNode("uShininess")
        uShininess?.uniform?.value = shininess
        uSpecularIntensity = model.findNode("uSpecularIntensity")
        uSpecularIntensity?.uniform?.value = specularIntensity

        uAlbedo = model.findNode("uAlbedo")
        uAlbedo?.uniform?.value?.set(albedo)

        albedoSampler = model.findNode<TextureNode>("tAlbedo")?.sampler
        albedoSampler?.let { it.texture = albedoMap }
        normalSampler = model.findNode<TextureNode>("tNormal")?.sampler
        normalSampler?.let { it.texture = normalMap }
    }

    companion object {
        fun defaultPhongModel(cfg: PhongConfig) = ShaderModel("defaultPhongModel()").apply {
            val ifNormals: StageInterfaceNode
            val ifColors: StageInterfaceNode?
            val ifTexCoords: StageInterfaceNode?
            val ifTangents: StageInterfaceNode?
            val ifFragPos: StageInterfaceNode
            val mvp: UniformBufferMvp

            vertexStage {
                mvp = mvpNode()
                val nrm = transformNode(attrNormals().output, mvp.outModelMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.output)

                ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifTexCoords = if (cfg.requiresTexCoords()) {
                    stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                } else {
                    null
                }
                ifTangents = if (cfg.isNormalMapped) {
                    val tan = transformNode(attrTangents().output, mvp.outModelMat, 0f)
                    stageInterfaceNode("ifTangents", tan.output)
                } else {
                    null
                }

                val worldPos = transformNode(attrPositions().output, mvp.outModelMat, 1f).output
                ifFragPos = stageInterfaceNode("ifFragPos", worldPos)
                positionOutput = vertexPositionNode(attrPositions().output, mvp.outMvpMat).outPosition
            }
            fragmentStage {
                val mvpFrag = mvp.addToStage(fragmentStageGraph)
                val lightNode = defaultLightNode()

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
                    bumpNormal.outNormal
                } else {
                    ifNormals.output
                }

                val phongMat = phongMaterialNode(albedo, normal, ifFragPos.output, mvpFrag.outCamPos, lightNode).apply {
                    inShininess = pushConstantNode1f("uShininess").output
                    inSpecularIntensity = pushConstantNode1f("uSpecularIntensity").output
                }
                colorOutput = phongMat.outColor
            }
        }
    }

    class PhongConfig {
        var albedoSource = Albedo.VERTEX_ALBEDO
        var isNormalMapped = false

        // initial shader values
        var albedo = Color.GRAY
        var shininess = 20f
        var specularIntensity = 1f

        var albedoMap: Texture? = null
        var normalMap: Texture? = null

        fun requiresTexCoords(): Boolean {
            return albedoSource == Albedo.TEXTURE_ALBEDO || isNormalMapped
        }
    }
}

fun phongShader(cfgBlock: PhongShader.PhongConfig.() -> Unit): PhongShader {
    val cfg = PhongShader.PhongConfig()
    cfg.cfgBlock()
    return PhongShader(cfg)
}