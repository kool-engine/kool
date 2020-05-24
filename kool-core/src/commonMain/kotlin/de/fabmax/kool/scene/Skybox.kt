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
                centered()
            }
        }
        isFrustumChecked = false
        isCastingShadow = false

        val texName = "envMap"
        val model = ShaderModel("Skybox Shader").apply {
            val ifLocalPos: StageInterfaceNode

            vertexStage {
                val mvp = mvpNode()
                val worldPos = vec3TransformNode(attrPositions().output, mvp.outModelMat, 1f)
                ifLocalPos = stageInterfaceNode("ifLocalPos", worldPos.outVec3)
                positionOutput = addNode(SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
            }
            fragmentStage {
                val sampler = cubeMapSamplerNode(cubeMapNode(texName), ifLocalPos.output, false)
                if (texLod != 0f) {
                    sampler.texLod = ShaderNodeIoVar(ModelVar1fConst(texLod))
                }
                val ldr = hdrToLdrNode(sampler.outColor)
                colorOutput(ldr.outColor)
            }
        }
        pipelineLoader = ModeledShader.CubeMapColor(texName, model).apply {
            onSetup += {
                it.cullMethod = CullMethod.CULL_FRONT_FACES
                it.depthTest = DepthCompareOp.LESS_EQUAL
            }
            onCreated += {
                cubeMapSampler.texture = environmentMap
            }
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
