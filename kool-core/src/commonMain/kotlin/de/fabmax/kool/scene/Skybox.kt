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
        generate {
            cube {
                centerOrigin()
            }
        }
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
                if (texLod != 0f) {
                    sampler.texLod = ShaderNodeIoVar(ModelVar1fConst(texLod))
                }
                val ldr = hdrToLdrNode(sampler.outColor)
                colorOutput = ldr.outColor

                //colorOutput = addNode(HdrTestNode(sampler.outColor, fragmentStage)).outColor
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

    private class HdrTestNode(val inColor: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("hdrTest", graph) {
        val outColor = ShaderNodeIoVar(ModelVar4f("hdrTest_out"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inColor)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                vec3 testRgb = vec3(log(length(${inColor.ref3f()})) / log(10.0) / 6.0);
                ${outColor.declare()} = vec4(testRgb, 1.0);
                """.trimIndent())
        }
    }

    private class SkyboxPosNode(val mvp: UniformBufferMvp, val inPos: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("skyboxPos", graph, ShaderStage.VERTEX_SHADER.mask) {
        val outPosition = ShaderNodeIoVar(ModelVar4f("skyboxPos_outPosition"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPos)
            dependsOn(mvp)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outPosition.declare()} = (${mvp.outProjMat} * ${mvp.outViewMat} * vec4(${inPos.ref3f()}, 0.0)).xyww;")
        }
    }
}
