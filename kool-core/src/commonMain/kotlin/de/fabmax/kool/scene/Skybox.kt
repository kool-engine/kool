package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader

class Skybox(val environmentMap: de.fabmax.kool.pipeline.CubeMapTexture) : Mesh(MeshData(Attribute.POSITIONS)) {
    constructor(ft: String, bk: String, lt: String, rt: String, up: String, dn: String) : this(CubeMapTexture {
        it.loadCubeMapImageData(ft, bk, lt, rt, up, dn)
    })

    init {
        generator = {
            cube {
                centerOrigin()
            }
        }
        generateGeometry()
        isFrustumChecked = false

        pipelineConfig {
            cullMethod = CullMethod.CULL_FRONT_FACES
            depthTest = DepthTest.LESS_EQUAL

            shaderLoader = { mesh, buildCtx, ctx ->
                val texName = "envMap"
                val model = ShaderModel("Skybox Shader").apply {
                    val ifLocalPos: StageInterfaceNode

                    vertexStage {
                        val mvp = mvpNode()
                        val worldPos = transformNode(attrPositions().output, mvp.outModelMat, 1f)
                        ifLocalPos = stageInterfaceNode("ifLocalPos", worldPos.output)
                        addNode(SkyboxPosNode(mvp, attrPositions().output, stage))
                    }
                    fragmentStage {
                        val sampler = cubeMapSamplerNode(cubeMapNode(texName), ifLocalPos.output)
                        unlitMaterialNode(sampler.outColor)
                    }
                }
                model.setup(mesh, buildCtx, ctx)
                ModeledShader.CubeMapColor(model, texName)
            }
            onPipelineCreated += {
                (it.shader as ModeledShader.CubeMapColor).cubeMapSampler.texture = environmentMap
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
