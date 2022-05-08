package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.CameraData
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.SceneLightData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture3d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.ShadowMap

class GrassShader(grassColor: Texture2d, ibl: EnvironmentMaps, shadowMap: ShadowMap, windTex: Texture3d) :
    KslBlinnPhongShader(grassShaderConfig(grassColor, ibl, shadowMap)) {

    var windOffset by uniform3f("uWindOffset")
    var windStrength by uniform1f("uWindStrength", 1f)
    var windScale by uniform1f("uWindScale", 0.01f)
    var windDensity by texture3d("tWindTex", windTex)

    companion object {
        private fun grassShaderConfig(grassColor: Texture2d, ibl: EnvironmentMaps, shadowMap: ShadowMap) = Config().apply {
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { addTextureColorLinearize(grassColor) }
            shadow { addShadowMap(shadowMap) }
            imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            alphaMode = AlphaMode.Opaque()
            isFlipBacksideNormals = false
            specularStrength = 0.15f

            modelCustomizer = {
                //with(TreeShader) { windMod() }
                val windTint = interStageFloat2("windTint")

                vertexStage {
                    val lightData = dataBlocks.first { it is SceneLightData } as SceneLightData

                    main {
                        with (TreeShader) {
                            windMod()
                        }

                        val worldPos = getFloat3Port("worldPos")
                        val windTex = texture3d("tWindTex")
                        val windOffset = uniformFloat3("uWindOffset") * 0.5f.const
                        val windSamplePos = (windOffset + worldPos) * uniformFloat1("uWindScale")
                        val slowWind = float3Var(sampleTexture(windTex, windSamplePos).xyz)
                        val windTintX = clamp(slowWind.x + 0.3f.const, 0.2f.const, 1f.const)

                        val windTintY = sampleTexture(windTex, worldPos * 0.05f.const).y * 0.5f.const

                        val normalPort = getFloat3Port("worldNormal")
                        val normal = float3Var(normalPort.input.input!!)
                        val dotNormal = floatVar(dot(normal, lightData.encodedPositions[0].xyz))
                        `if` (abs(dotNormal) lt 0.1f.const) {
                            normal -= normalize(lightData.encodedPositions[0].xyz)
                        }.elseIf(dotNormal gt 0f.const) {
                            normal set normal * (-1f).const
                        }
                        normal.y -= 0.4f.const - slowWind.x * 0.5f.const
                        normalPort.input(normal)

                        windTint.input set constFloat2(windTintX, windTintY)
                    }
                }
                fragmentStage {
                    @Suppress("UNCHECKED_CAST")
                    val worldPos = interStageVars.first { it.output.stateName == "positionWorldSpace" } as KslInterStageVector<KslTypeFloat3, KslTypeFloat1>
                    val camData = dataBlocks.first { it is CameraData } as CameraData

                    main {
                        dumpCode = true
                        val fragColorPort = getFloat4Port("fragmentColor")
                        val fragColor = fragColorPort.input.input!!

                        val distToCam = distance(worldPos.output, camData.position)
                        val cutOff = (1f.const - clamp(distToCam / 30f.const, 0f.const, 1f.const)) * 0.4f.const + 0.1f.const
                        `if` (fragColor.a lt cutOff) {
                            discard()
                        }

                        val fragColorMod = float4Var(fragColor)
                        // boost brightness of distant grass sprites
                        fragColorMod.rgb set fragColorMod.rgb / pow(fragColorPort.input.a, 1.5f.const)
                        // make some yellowish patches
                        fragColorMod.rgb set mix(fragColorMod.rgb, (MdColor.YELLOW toneLin 600).const.rgb, windTint.output.y)
                        // apply some fake cloud shadow
                        fragColorMod.rgb set fragColorMod.rgb * windTint.output.x
                        fragColorPort.input(fragColorMod)
                    }
                }
            }
        }
    }
}