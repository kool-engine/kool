package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader

object Skybox {

    fun cube(environmentMap: CubeMapTexture, texLod: Float = 0f, hdriInput: Boolean = true, hdrOutput: Boolean = false): Mesh {
        return mesh(listOf(Attribute.POSITIONS), "skybox-cube") {
            generate {
                cube {
                    centered()
                }
            }
            isFrustumChecked = false
            isCastingShadow = false
            shader = SkyboxCubeShader(environmentMap, texLod, hdriInput, hdrOutput)
        }
    }

    fun sphere(environmentMap: Texture, texLod: Float = 0f, hdriInput: Boolean = true, hdrOutput: Boolean = false): Mesh {
        return mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS), "skybox-cube") {
            generate {
                vertexModFun = {
                    texCoord.x = 1f - texCoord.x
                }
                uvSphere {
                    steps = 10
                }
            }
            isFrustumChecked = false
            isCastingShadow = false
            shader = SkyboxSphereShader(environmentMap, texLod, hdriInput, hdrOutput)
        }
    }

    class SkyboxCubeShader(environmentMap: CubeMapTexture, texLod: Float = 0f, hdriInput: Boolean, hdrOutput: Boolean) :
            ModeledShader(skyboxCubeShaderModel(texLod, hdriInput, hdrOutput)) {

        private var cubeMapSampler: CubeMapSampler? = null
        var environmentMap: CubeMapTexture? = environmentMap
            set(value) {
                field = value
                cubeMapSampler?.texture = value
            }

        init {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.CULL_FRONT_FACES
                builder.depthTest = DepthCompareOp.LESS_EQUAL
            }
            onPipelineCreated += { _, _, _ ->
                cubeMapSampler = model.findNode<CubeMapNode>("skyboxCube")?.sampler
                cubeMapSampler?.let { it.texture = environmentMap }
            }
        }

        companion object {
            fun skyboxCubeShaderModel(texLod: Float, hdriInput: Boolean, hdrOutput: Boolean) = ShaderModel("skybox-cube").apply {
                val ifLocalPos: StageInterfaceNode

                vertexStage {
                    val mvp = mvpNode()
                    val worldPos = vec3TransformNode(attrPositions().output, mvp.outModelMat, 1f)
                    ifLocalPos = stageInterfaceNode("ifLocalPos", worldPos.outVec3)
                    positionOutput = addNode(SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
                }
                fragmentStage {
                    val sampler = cubeMapSamplerNode(cubeMapNode("skyboxCube"), ifLocalPos.output)
                    if (texLod != 0f) {
                        sampler.texLod = ShaderNodeIoVar(ModelVar1fConst(texLod))
                    }

                    when {
                        hdriInput == hdrOutput -> colorOutput(sampler.outColor)
                        hdriInput -> colorOutput(hdrToLdrNode(sampler.outColor).outColor)
                        else -> colorOutput(gammaNode(sampler.outColor).outColor)
                    }
                }
            }
        }
    }

    class SkyboxSphereShader(environmentMap: Texture, texLod: Float = 0f, hdriInput: Boolean, hdrOutput: Boolean) :
            ModeledShader(skyboxSphereShaderModel(texLod, hdriInput, hdrOutput)) {

        private var textureSampler: TextureSampler? = null
        var environmentMap: Texture? = environmentMap
            set(value) {
                field = value
                textureSampler?.texture = value
            }

        init {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.CULL_FRONT_FACES
                builder.depthTest = DepthCompareOp.LESS_EQUAL
            }
            onPipelineCreated += { _, _, _ ->
                textureSampler = model.findNode<TextureNode>("skyboxSphere")?.sampler
                textureSampler?.let { it.texture = environmentMap }
            }
        }

        companion object {
            fun skyboxSphereShaderModel(texLod: Float, hdriInput: Boolean, hdrOutput: Boolean) = ShaderModel("skybox-sphere").apply {
                val ifUv: StageInterfaceNode

                vertexStage {
                    val mvp = mvpNode()
                    ifUv = stageInterfaceNode("ifUv", attrTexCoords().output)
                    positionOutput = addNode(SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
                }
                fragmentStage {
                    val sampler = textureSamplerNode(textureNode("skyboxSphere"), ifUv.output)
                    if (texLod != 0f) {
                        sampler.texLod = ShaderNodeIoVar(ModelVar1fConst(texLod))
                    }

                    when {
                        hdriInput == hdrOutput -> colorOutput(sampler.outColor)
                        hdriInput -> colorOutput(hdrToLdrNode(sampler.outColor).outColor)
                        else -> colorOutput(gammaNode(sampler.outColor).outColor)
                    }
                }
            }
        }
    }

    class SkyboxPosNode(val mvp: UniformBufferMvp, val inPos: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("skyboxPos", graph, ShaderStage.VERTEX_SHADER.mask) {
        val outPosition = ShaderNodeIoVar(ModelVar4f("skyboxPos_outPosition"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPos)
            dependsOn(mvp)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outPosition.declare()} = (${mvp.outMvpMat} * vec4(${inPos.ref3f()}, 0.0)).xyww;")
        }
    }
}