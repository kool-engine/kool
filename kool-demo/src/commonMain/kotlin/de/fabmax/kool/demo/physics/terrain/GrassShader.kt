package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.ShadowMap

object GrassShader {

    class Pbr(grassColor: Texture2d, shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d, isInstanced: Boolean)
        : KslPbrShader(pbrConfig(grassColor, shadowMap, ssaoMap, isInstanced)), WindAffectedShader {
        override val shader = this
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps) {
            with(TerrainDemo) { updateSky(envMaps) }
        }
    }

    class BlinnPhong(grassColor: Texture2d, shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d, isInstanced: Boolean)
        : KslBlinnPhongShader(blinnPhongConfig(grassColor, shadowMap, ssaoMap, isInstanced)), WindAffectedShader {
        override val shader = this
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps) {
            with(TerrainDemo) { updateSky(envMaps) }
        }
    }

    class Shadow(grassColor: Texture2d, windTex: Texture3d, isInstanced: Boolean, isAoDepth: Boolean)
        : DepthShader(shadowConfig(isInstanced, isAoDepth)), WindAffectedShader {
        var grassAlpha by texture2d("grassAlpha", grassColor)

        override val shader = this
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps) { }
    }

    val DISTANCE_SCALE = Attribute("aDistScale", GpuType.FLOAT1)

    fun makeGrassShader(
        grassColor: Texture2d,
        shadowMap: ShadowMap,
        ssaoMap: Texture2d,
        windTex: Texture3d,
        isInstanced: Boolean,
        isPbr: Boolean
    ): WindAffectedShader {
        return if (isPbr) {
            Pbr(grassColor, shadowMap, ssaoMap, windTex, isInstanced)
        } else {
            BlinnPhong(grassColor, shadowMap, ssaoMap, windTex, isInstanced)
        }
    }

    private fun KslProgram.grassWindMod(isInstanced: Boolean) {
        vertexStage {
            main {
                val worldPos = getFloat3Port("worldPos")
                with (TreeShader) {
                    windMod()
                }
                if (isInstanced) {
                    val pos = float3Var(worldPos.input.input)
                    pos.y -= instanceAttribFloat1(DISTANCE_SCALE.name) * 1.3f.const
                    worldPos.input(pos)
                }
            }
        }
    }

    private fun pbrConfig(grassColor: Texture2d, shadowMap: ShadowMap, ssaoMap: Texture2d, isInstanced: Boolean) = KslPbrShader.Config.Builder().apply {
        dualImageBasedAmbientColor()
        with(TerrainDemo) {
            iblConfig()
        }
        roughness(0.8f)
        grassShaderConfig(grassColor, shadowMap, ssaoMap, isInstanced)
    }.build()

    private fun blinnPhongConfig(grassColor: Texture2d, shadowMap: ShadowMap, ssaoMap: Texture2d, isInstanced: Boolean) = KslBlinnPhongShader.Config.Builder().apply {
        dualImageBasedAmbientColor()
        specularStrength(0.15f)
        grassShaderConfig(grassColor, shadowMap, ssaoMap, isInstanced)
    }.build()

    private fun shadowConfig(isInstanced: Boolean, isAoDepth: Boolean) = DepthShader.Config.Builder().apply {
        pipeline { cullMethod = CullMethod.NO_CULLING }
        vertexCfg.isInstanced = isInstanced
        if (isAoDepth) {
            outputNormals = true
            outputLinearDepth = true
        }
        modelCustomizer = {
            grassWindMod(isInstanced)

            val texCoords: TexCoordAttributeBlock
            vertexStage {
                main {
                    texCoords = texCoordAttributeBlock()
                }
            }
            fragmentStage {
                main {
                    val tex = texture2d("grassAlpha")
                    val uv = texCoords.getTextureCoords()
                    val texAlpha = float1Var(sampleTexture(tex, uv).a)
                    `if`(texAlpha lt 0.35f.const) {
                        discard()
                    }
                }
            }
        }
    }.build()

    private fun KslLitShader.LitShaderConfig.Builder.grassShaderConfig(grassColor: Texture2d, shadowMap: ShadowMap, ssaoMap: Texture2d, isInstanced: Boolean) {
        pipeline { cullMethod = CullMethod.NO_CULLING }
        color { textureColor(grassColor) }
        shadow { addShadowMap(shadowMap) }
        enableSsao(ssaoMap)
        vertices {
            this.isInstanced = isInstanced
            isFlipBacksideNormals = false
        }

        colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        alphaMode = AlphaMode.Opaque

        modelCustomizer = {
            //dumpCode = true
            grassWindMod(isInstanced)

            val tint = interStageFloat2("windTint")
            vertexStage {
                val lightData = dataBlocks.first { it is SceneLightData } as SceneLightData

                main {
                    val worldPos = getFloat3Port("worldPos")
                    val windTex = texture3d("tWindTex")

                    // wind based tint (moving darker patches)
                    val windOffset = uniformFloat4("uWindOffsetStrength").xyz * 0.5f.const
                    val windSamplePos = (windOffset + worldPos) * uniformFloat1("uWindScale")
                    val slowWind = float3Var(sampleTexture(windTex, windSamplePos, 0f.const).xyz)
                    val windTint = clamp(slowWind.x + 0.3f.const, 0.2f.const, 1f.const)

                    // position based tint (fixed position yellowish patches)
                    val positionTint = sampleTexture(windTex, worldPos * 0.05f.const, 0f.const).y * 0.5f.const

                    // forward tint values to fragment stage
                    tint.input set float2Value(windTint, positionTint)

                    // adjust vertex normals to always point in light direction
                    val normalPort = getFloat3Port("worldNormal")
                    val normal = float3Var(normalPort.input.input!!)

                    // modify normal so that it always points in light direction
                    val dotNormal = float1Var(dot(normal, lightData.encodedPositions[0].xyz))
                    val modFac = smoothStep((-0.15f).const, (-0.05f).const, dotNormal) * smoothStep(0.05f.const, 0.1f.const, -lightData.encodedPositions[0].y)
                    normal -= normalize(lightData.encodedPositions[0].xyz) * modFac

                    // modify normal y-component by the same input as wind tint to magnify wind based
                    // darkening / brightening effect
                    normal.y -= 0.25f.const - slowWind.x * 0.25f.const
                    normalPort.input(normal)
                }
            }
            fragmentStage {
                @Suppress("UNCHECKED_CAST")
                val worldPos = interStageVars.first { it.output.stateName == "positionWorldSpace" } as KslInterStageVector<KslFloat3, KslFloat1>
                val camData = dataBlocks.first { it is CameraData } as CameraData

                main {
                    val baseColorPort = getFloat4Port("baseColor")
                    val baseColor = baseColorPort.input.input!!

                    val distToCam = distance(worldPos.output, camData.position)
                    val cutOff = (1f.const - clamp(distToCam / 30f.const, 0f.const, 1f.const)) * 0.4f.const + 0.1f.const
                    `if` (baseColor.a lt cutOff) {
                        discard()
                    }

                    val baseColorMod = float4Var(baseColor)
                    // boost brightness of distant grass sprites (otherwise they appear too dark because of mipmapping / alpha issues)
                    baseColorMod.rgb set baseColorMod.rgb / pow(baseColorPort.input.a, 1.5f.const)
                    // make some yellowish patches
                    baseColorMod.rgb set mix(baseColorMod.rgb, (MdColor.YELLOW toneLin 500).const.rgb, tint.output.y)
                    // apply some fake cloud shadow
                    baseColorMod.rgb set baseColorMod.rgb * tint.output.x
                    baseColorPort.input(baseColorMod)
                }
            }
        }
    }
}