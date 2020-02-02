package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.util.IndexedVertexList

class Skybox(val environmentMap: CubeMapTexture, texLod: Float = 0f) : Mesh(IndexedVertexList(Attribute.POSITIONS)) {
    constructor(ft: String, bk: String, lt: String, rt: String, up: String, dn: String) : this(CubeMapTexture {
        it.loadCubeMapTextureData(ft, bk, lt, rt, up, dn)
    })

    init {
        generator = {
            cube {
                centerOrigin()
            }
        }
        generateGeometry()
        isFrustumChecked = false

        val texName = "envMap"
        val model = ShaderModel("Skybox Shader").apply {
            val ifLocalPos: StageInterfaceNode

            vertexStage {
                val mvp = mvpNode()
                val worldPos = transformNode(attrPositions().output, mvp.outModelMat, 1f)
                ifLocalPos = stageInterfaceNode("ifLocalPos", worldPos.output)
                positionOutput = addNode(SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
            }
            fragmentStage {
                val sampler = cubeMapSamplerNode(cubeMapNode(texName), ifLocalPos.output, false)
                val ldr = hdrToLdrNode(sampler.outColor)
                colorOutput = ldr.outColor
                if (texLod != 0f) {
                    sampler.texLod = "$texLod"
                }
            }
        }
        val shader = ModeledShader.CubeMapColor(model, texName)

        pipelineConfig {
            cullMethod = CullMethod.CULL_FRONT_FACES
            depthTest = DepthCompareOp.LESS_EQUAL

            shaderLoader = shader::setup
            onPipelineCreated += {
                shader.cubeMapSampler.texture = environmentMap
            }
        }
    }

    private class SkyboxPosNode(val mvp: UniformBufferMvp, val inPos: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("skyboxPos", graph, ShaderStage.VERTEX_SHADER.mask) {
        val outPosition = ShaderNodeIoVar(ModelVar4f("skyboxPos_outPosition"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPos)
            dependsOn(mvp)

            shaderGraph as VertexShaderGraph
            shaderGraph.positionOutput = outPosition
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outPosition.declare()} = (${mvp.outProjMat} * ${mvp.outViewMat} * vec4(${inPos.ref3f()}, 0.0)).xyww;")
        }
    }
}
