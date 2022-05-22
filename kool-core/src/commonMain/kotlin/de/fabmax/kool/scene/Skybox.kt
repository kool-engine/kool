package de.fabmax.kool.scene

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.scene.geometry.IndexedVertexList

object Skybox {

    fun cube(environmentMap: TextureCube, texLod: Float = 0f, hdriInput: Boolean = true, hdrOutput: Boolean = false): Cube {
        val colorSpaceConversion = when {
            hdriInput == hdrOutput -> ColorSpaceConversion.AS_IS
            hdriInput -> ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            else -> ColorSpaceConversion.sRGB_TO_LINEAR
        }
        return Cube(environmentMap, texLod, colorSpaceConversion)
    }

    fun sphere(environmentMap: Texture2d, texLod: Float = 0f, hdriInput: Boolean = true, hdrOutput: Boolean = false): Mesh {
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

    class Cube(skyTex: TextureCube? = null, texLod: Float = 0f, colorSpaceConversion: ColorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR)
        : Mesh(IndexedVertexList(Attribute.POSITIONS)) {

        val skyboxShader: KslSkyCubeShader

        init {
            generate {
                cube {
                    centered()
                }
            }
            isFrustumChecked = false
            isCastingShadow = false
            skyboxShader = KslSkyCubeShader(colorSpaceConversion).apply {
                setSingleSky(skyTex)
                lod = texLod
            }
            shader = skyboxShader
        }
    }

    class KslSkyCubeShader(colorSpaceConversion: ColorSpaceConversion)
        : KslShader(Model(colorSpaceConversion), PipelineConfig().apply { cullMethod = CullMethod.CULL_FRONT_FACES }) {

        val skies: Array<TextureCube?> by textureCubeArray("tSkies", 2)
        var skyWeights: Vec2f by uniform2f("uSkyWeights", Vec2f.X_AXIS)

        var skyOrientation: Mat3f by uniformMat3f("uSkyOrientation", Mat3f().setIdentity())
        var lod: Float by uniform1f("uLod")

        fun setSingleSky(skyTex: TextureCube?) = setBlendSkies(skyTex, 1f, skyTex, 0f)

        fun setBlendSkies(skyA: TextureCube?, weightA: Float, skyB: TextureCube?, weightB: Float) {
            skies[0] = skyA
            skies[1] = skyB
            skyWeights = Vec2f(weightA, weightB)
        }

        class Model(colorSpaceConversion: ColorSpaceConversion) : KslProgram("skycube-shader") {
            init {
                val orientedPos = interStageFloat3()
                vertexStage {
                    main {
                        val mvpMat = mvpMatrix().matrix
                        val skyOrientation = uniformMat3("uSkyOrientation")
                        val localPos = vertexAttribFloat3(Attribute.POSITIONS.name)
                        orientedPos.input set skyOrientation * localPos
                        outPosition set (mvpMat * float4Value(localPos, 0f)).float4("xyww")
                    }
                }
                fragmentStage {
                    main {
                        val skies = textureArrayCube("tSkies", 2)
                        val skyWeights = uniformFloat2("uSkyWeights")
                        val texLod = uniformFloat1("uLod")
                        val color = float3Var(sampleTexture(skies[0], orientedPos.output, texLod).rgb * skyWeights.x)
                        `if` (skyWeights.y gt 0f.const) {
                            color += sampleTexture(skies[1], orientedPos.output, texLod).rgb * skyWeights.y
                        }
                        colorOutput(convertColorSpace(color, colorSpaceConversion), 1f.const)
                    }
                }
            }
        }
    }

    class SkyboxSphereShader(environmentMap: Texture2d, texLod: Float = 0f, hdriInput: Boolean, hdrOutput: Boolean) :
            ModeledShader(skyboxSphereShaderModel(texLod, hdriInput, hdrOutput)) {

        val environmentMapTex = Texture2dInput("skyboxCube", environmentMap)

        init {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.CULL_FRONT_FACES
                builder.depthTest = DepthCompareOp.LESS_EQUAL
            }
            onPipelineCreated += { _, _, _ ->
                environmentMapTex.connect(model)
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
                    val sampler = texture2dSamplerNode(texture2dNode("skyboxSphere"), ifUv.output)
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