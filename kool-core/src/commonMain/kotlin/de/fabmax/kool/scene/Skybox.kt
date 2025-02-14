package de.fabmax.kool.scene

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.UniqueId

fun Scene.skybox(ibl: EnvironmentMap, lod: Float = 1f) {
    this += Skybox.cube(ibl.reflectionMap, lod)
}

object Skybox {
    fun cube(
        environmentMap: TextureCube,
        texLod: Float = 0f,
        colorSpaceConversion: ColorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr(),
        depthMode: DepthMode = DepthMode.Reversed
    ): Cube {
        return Cube(environmentMap, texLod, colorSpaceConversion, depthMode)
    }

    class Cube(
        skyTex: TextureCube? = null,
        texLod: Float = 0f,
        colorSpaceConversion: ColorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr(),
        depthMode: DepthMode = DepthMode.Reversed
    ) : Mesh(IndexedVertexList(Attribute.POSITIONS), name = UniqueId.nextId("Skybox.Cube")) {

        val skyboxShader: KslSkyCubeShader

        init {
            generate {
                cube { }
            }
            isFrustumChecked = false
            isCastingShadow = false
            rayTest = MeshRayTest.nopTest()
            skyboxShader = KslSkyCubeShader(colorSpaceConversion, depthMode).apply {
                setSingleSky(skyTex)
                lod = texLod
            }
            shader = skyboxShader
        }
    }

    class KslSkyCubeShader(val colorSpaceConversion: ColorSpaceConversion, val depthMode: DepthMode) :
        KslShader(
            Model(colorSpaceConversion, depthMode),
            PipelineConfig(cullMethod = CullMethod.CULL_FRONT_FACES, isWriteDepth = false)
        )
    {
        val skies = List(2) { textureCube("tSky_$it") }
        var skyWeights: Vec2f by uniform2f("uSkyWeights", Vec2f.X_AXIS)

        var skyOrientation: Mat3f by uniformMat3f("uSkyOrientation")
        var lod: Float by uniform1f("uLod")

        fun setSingleSky(skyTex: TextureCube?) = setBlendSkies(skyTex, 1f, skyTex, 0f)

        fun setBlendSkies(skyA: TextureCube?, weightA: Float, skyB: TextureCube?, weightB: Float) {
            skies[0].set(skyA)
            skies[1].set(skyB)
            skyWeights = Vec2f(weightA, weightB)
        }

        class Model(colorSpaceConversion: ColorSpaceConversion, depthMode: DepthMode) : KslProgram("skycube-shader") {
            init {
                val orientedPos = interStageFloat3()
                vertexStage {
                    main {
                        val cam = cameraData()
                        val skyOrientation = uniformMat3("uSkyOrientation")
                        val localPos = vertexAttribFloat3(Attribute.POSITIONS.name)
                        orientedPos.input set skyOrientation * localPos

                        val rotX = float3Var(normalize(cam.viewMat[0].xyz))
                        val rotY = float3Var(normalize(cam.viewMat[1].xyz))
                        val rotZ = float3Var(normalize(cam.viewMat[2].xyz))
                        val viewRot = mat4Var(
                            mat4Value(
                                float4Value(rotX, 0f.const),
                                float4Value(rotY, 0f.const),
                                float4Value(rotZ, 0f.const),
                                float4Value(0f.const, 0f.const, 0f.const, 1f.const),
                            )
                        )
                        outPosition set (cam.projMat * viewRot * float4Value(localPos * cam.clipNear * 5f.const, 1f))
                    }
                }
                fragmentStage {
                    main {
                        val skies = List(2) { textureCube("tSky_$it") }
                        val skyWeights = uniformFloat2("uSkyWeights")
                        val texLod = uniformFloat1("uLod")
                        val color = float3Var(sampleTexture(skies[0], orientedPos.output, texLod).rgb * skyWeights.x)
                        `if` (skyWeights.y gt 0f.const) {
                            color += sampleTexture(skies[1], orientedPos.output, texLod).rgb * skyWeights.y
                        }
                        colorOutput(convertColorSpace(color, colorSpaceConversion), 1f.const)
                        outDepth set depthMode.far.const
                    }
                }
            }
        }
    }
}