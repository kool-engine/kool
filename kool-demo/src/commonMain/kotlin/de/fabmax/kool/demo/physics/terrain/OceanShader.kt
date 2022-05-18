package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.LitMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.calcBumpedNormal
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture3d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.ShadowMap

object OceanShader {

    class Pbr(ibl: EnvironmentMaps, shadowMap: ShadowMap, windTex: Texture3d, oceanBump: Texture2d)
        : KslPbrShader(pbrConfig(ibl, shadowMap)), WindAffectedShader {
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override val shader = this

        var oceanBump by texture2d("tOceanBump", oceanBump)
    }

    class BlinnPhong(ibl: EnvironmentMaps, shadowMap: ShadowMap, windTex: Texture3d, oceanBump: Texture2d)
        : KslBlinnPhongShader(blinnPhongConfig(ibl, shadowMap)), WindAffectedShader {
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override val shader = this

        var oceanBump by texture2d("tOceanBump", oceanBump)
    }

    fun makeOceanShader(ibl: EnvironmentMaps, shadowMap: ShadowMap, windTex: Texture3d, oceanBump: Texture2d, isPbr: Boolean): WindAffectedShader {
        return if (isPbr) {
            Pbr(ibl, shadowMap, windTex, oceanBump)
        } else {
            BlinnPhong(ibl, shadowMap, windTex, oceanBump)
        }
    }

    private fun KslLitShader.LitShaderConfig.baseConfig(shadowMap: ShadowMap) {
        vertices { isInstanced = true }
        color { constColor(MdColor.CYAN.toLinear()) }
        shadow { addShadowMap(shadowMap) }
        colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        modelCustomizer = { oceanMod() }
    }

    private fun pbrConfig(ibl: EnvironmentMaps, shadowMap: ShadowMap) = KslPbrShader.Config().apply {
        baseConfig(shadowMap)
        roughness(0.1f)
        with (TerrainDemo) {
            iblConfig(ibl)
        }
    }

    private fun blinnPhongConfig(ibl: EnvironmentMaps, shadowMap: ShadowMap) = KslBlinnPhongShader.Config().apply {
        baseConfig(shadowMap)
        imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
        specularStrength(1f)
        shininess(500f)
    }

    private fun KslProgram.oceanMod() {
        val windOffsetStrength = uniformFloat4("uWindOffsetStrength")
        val windScale = uniformFloat1("uWindScale")
        val windTex = texture3d("tWindTex")
        val oceanBumpTex = texture2d("tOceanBump")

        val waveHeight = interStageFloat1()

        vertexStage {
            main {
                val worldPosPort = getFloat3Port("worldPos")

                val pos = float3Var(worldPosPort.input.input!!)
                val posLt = float3Var(pos + float3Value((-1f).const, 0f.const, 0f.const))
                val posRt = float3Var(pos + float3Value(1f.const, 0f.const, 0f.const))
                val posUp = float3Var(pos + float3Value(0f.const, 0f.const, (-1f).const))
                val posDn = float3Var(pos + float3Value(0f.const, 0f.const, 1f.const))

                val windScale2 = floatVar(windScale * 0.71.const)

                pos.y set (sampleTexture(windTex, (windOffsetStrength.xyz + pos) * windScale).y - 0.5f.const) * windOffsetStrength.w +
                        (sampleTexture(windTex, (windOffsetStrength.xyz + pos) * windScale2).y - 0.5f.const) * windOffsetStrength.w
                posLt.y set (sampleTexture(windTex, (windOffsetStrength.xyz + posLt) * windScale).y - 0.5f.const) * windOffsetStrength.w +
                        (sampleTexture(windTex, (windOffsetStrength.xyz + posLt) * windScale2).y - 0.5f.const) * windOffsetStrength.w
                posRt.y set (sampleTexture(windTex, (windOffsetStrength.xyz + posRt) * windScale).y - 0.5f.const) * windOffsetStrength.w +
                        (sampleTexture(windTex, (windOffsetStrength.xyz + posRt) * windScale2).y - 0.5f.const) * windOffsetStrength.w
                posUp.y set (sampleTexture(windTex, (windOffsetStrength.xyz + posUp) * windScale).y - 0.5f.const) * windOffsetStrength.w +
                        (sampleTexture(windTex, (windOffsetStrength.xyz + posUp) * windScale2).y - 0.5f.const) * windOffsetStrength.w
                posDn.y set (sampleTexture(windTex, (windOffsetStrength.xyz + posDn) * windScale).y - 0.5f.const) * windOffsetStrength.w +
                        (sampleTexture(windTex, (windOffsetStrength.xyz + posDn) * windScale2).y - 0.5f.const) * windOffsetStrength.w

                worldPosPort.input(pos)

                val normalA = float3Var(normalize(cross(posUp - pos, posLt - pos)))
                val normalB = float3Var(normalize(cross(posDn - pos, posRt - pos)))
                getFloat3Port("worldNormal").input(normalize(normalA + normalB))

                waveHeight.input set pos.y
            }
        }
        fragmentStage {
            main {
                val baseColorPort = getFloat4Port("baseColor")
                val baseColor = float3Var()

                val lightColor = MdColor.LIGHT_BLUE toneLin 300
                val midColor = MdColor.LIGHT_BLUE toneLin 500
                val darkColor = MdColor.BLUE toneLin 700

                `if`(waveHeight.output gt 0f.const) {
                    baseColor set mix(midColor.const.rgb, lightColor.const.rgb, clamp(waveHeight.output / 6f.const, 0f.const, 1f.const))
                }.`else` {
                    baseColor set mix(midColor.const.rgb, darkColor.const.rgb, clamp(-waveHeight.output / 6f.const, 0f.const, 1f.const))
                }
                baseColorPort.input(float4Value(baseColor, 1f.const))

                val material = findBlock<LitMaterialBlock>()!!
                val worldPos = material.inFragmentPos.input!!
                val worldNormal = material.inNormal.input!!
                val tangent = float4Value(cross(worldNormal, Vec3f.X_AXIS.const), 1f.const)

                val offsetA = float2Var(windOffsetStrength.float2("xz") * 0.73f.const * windScale)
                val offsetB = float2Var(windOffsetStrength.float2("xz") * 0.37f.const * windScale)

                val mapNormalA = sampleTexture(oceanBumpTex, worldPos.float2("xz") * 0.051f.const + offsetA).xyz * 2f.const - 1f.const
                val mapNormalB = sampleTexture(oceanBumpTex, worldPos.float2("xz") * 0.017f.const + offsetB).xyz * 2f.const - 1f.const
                val mapNormal = float3Var(normalize(mapNormalA + mapNormalB))
                val bumpNormal = float3Var(calcBumpedNormal(worldNormal, tangent, mapNormal, 0.4f.const))
                material.inNormal(bumpNormal)
            }
        }
    }
}