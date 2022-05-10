package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslDepthShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.ShadowMap

class GrassShader(grassColor: Texture2d, ibl: EnvironmentMaps, shadowMap: ShadowMap, windTex: Texture3d, isInstanced: Boolean) :
    KslBlinnPhongShader(grassShaderConfig(grassColor, ibl, shadowMap, isInstanced)) {

    var windOffset by uniform3f("uWindOffset")
    var windStrength by uniform1f("uWindStrength", 1f)
    var windScale by uniform1f("uWindScale", 0.01f)
    var windDensity by texture3d("tWindTex", windTex)

    class Shadow(grassColor: Texture2d, windTex: Texture3d, isInstanced: Boolean) : KslDepthShader(grassShadowConfig(isInstanced)) {
        var windOffset by uniform3f("uWindOffset")
        var windStrength by uniform1f("uWindStrength", 1f)
        var windScale by uniform1f("uWindScale", 0.01f)
        var windDensity by texture3d("tWindTex", windTex)
        var grassAlpha by texture2d("grassAlpha", grassColor)

        companion object {
            private fun grassShadowConfig(isInstanced: Boolean) = Config().apply {
                pipeline { cullMethod = CullMethod.NO_CULLING }
                this.isInstanced = isInstanced
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
                            val uv = texCoords.getAttributeCoords(Attribute.TEXTURE_COORDS)
                            val texAlpha = floatVar(sampleTexture(tex, uv).a)
                            `if` (texAlpha lt 0.35f.const) {
                                discard()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        val DISTANCE_SCALE = Attribute("aDistScale", GlslType.FLOAT)

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

        private fun grassShaderConfig(grassColor: Texture2d, ibl: EnvironmentMaps, shadowMap: ShadowMap, isInstanced: Boolean) = Config().apply {
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { addTextureColor(grassColor) }
            shadow { addShadowMap(shadowMap) }
            imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
            vertices {
                this.isInstanced = isInstanced
                isFlipBacksideNormals = false
            }

            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            alphaMode = AlphaMode.Opaque()
            specularStrength = 0.15f

            modelCustomizer = {
                grassWindMod(isInstanced)

                val tint = interStageFloat2("windTint")
                vertexStage {
                    val lightData = dataBlocks.first { it is SceneLightData } as SceneLightData

                    main {
                        val worldPos = getFloat3Port("worldPos")
                        val windTex = texture3d("tWindTex")

                        // wind based tint (moving darker patches)
                        val windOffset = uniformFloat3("uWindOffset") * 0.5f.const
                        val windSamplePos = (windOffset + worldPos) * uniformFloat1("uWindScale")
                        val slowWind = float3Var(sampleTexture(windTex, windSamplePos).xyz)
                        val windTint = clamp(slowWind.x + 0.3f.const, 0.2f.const, 1f.const)

                        // position based tint (fixed position yellowish patches)
                        val positionTint = sampleTexture(windTex, worldPos * 0.05f.const).y * 0.5f.const

                        // forward tint values to fragment stage
                        tint.input set float2Value(windTint, positionTint)

                        // adjust vertex normals to always point in light direction
                        val normalPort = getFloat3Port("worldNormal")
                        val normal = float3Var(normalPort.input.input!!)
                        val dotNormal = floatVar(dot(normal, lightData.encodedPositions[0].xyz))
                        `if` (abs(dotNormal) lt 0.1f.const) {
                            normal -= normalize(lightData.encodedPositions[0].xyz)
                        }.elseIf(dotNormal gt 0f.const) {
                            normal set normal * (-1f).const
                        }

                        // modify normal y-component by the same input as wind tint to magnify wind based
                        // darkening / brightening effect
                        normal.y -= 0.5f.const - slowWind.x * 0.5f.const
                        normalPort.input(normal)
                    }
                }
                fragmentStage {
                    @Suppress("UNCHECKED_CAST")
                    val worldPos = interStageVars.first { it.output.stateName == "positionWorldSpace" } as KslInterStageVector<KslTypeFloat3, KslTypeFloat1>
                    val camData = dataBlocks.first { it is CameraData } as CameraData

                    main {
                        val fragColorPort = getFloat4Port("fragmentColor")
                        val fragColor = fragColorPort.input.input!!

                        val distToCam = distance(worldPos.output, camData.position)
                        val cutOff = (1f.const - clamp(distToCam / 30f.const, 0f.const, 1f.const)) * 0.4f.const + 0.1f.const
                        `if` (fragColor.a lt cutOff) {
                            discard()
                        }

                        val fragColorMod = float4Var(fragColor)
                        // boost brightness of distant grass sprites (otherwise they appear too dark because of mipmapping / alpha issues)
                        fragColorMod.rgb set fragColorMod.rgb / pow(fragColorPort.input.a, 1.5f.const)
                        // make some yellowish patches
                        fragColorMod.rgb set mix(fragColorMod.rgb, (MdColor.YELLOW toneLin 600).const.rgb, tint.output.y)
                        // apply some fake cloud shadow
                        fragColorMod.rgb set fragColorMod.rgb * tint.output.x
                        fragColorPort.input(fragColorMod)
                    }
                }
            }
        }
    }
}