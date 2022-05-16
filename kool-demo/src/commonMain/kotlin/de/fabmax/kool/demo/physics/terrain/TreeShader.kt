package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.*
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture3d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

interface WindAffectedShader {
    var windOffsetStrength: Vec4f
    var windScale: Float
    var windDensity: Texture3d?

    val shader: KslShader
}

object TreeShader {
    class Pbr(ibl: EnvironmentMaps, shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d)
        : KslPbrShader(pbrConfig(ibl, shadowMap, ssaoMap)), WindAffectedShader {
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override val shader = this
    }

    class BlinnPhong(ibl: EnvironmentMaps, shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d)
        : KslBlinnPhongShader(blinnPhongConfig(ibl, shadowMap, ssaoMap)), WindAffectedShader {
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override val shader = this
    }

    class Shadow(windTex: Texture3d, isAoDepth: Boolean) : KslDepthShader(shadowConfig(isAoDepth)), WindAffectedShader {
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override val shader = this
    }

    fun makeTreeShader(ibl: EnvironmentMaps, shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d, isPbr: Boolean): WindAffectedShader {
        return if (isPbr) {
            Pbr(ibl, shadowMap, ssaoMap, windTex)
        } else {
            BlinnPhong(ibl, shadowMap, ssaoMap, windTex)
        }
    }

    private fun KslLitShader.LitShaderConfig.baseConfig(shadowMap: ShadowMap, ssaoMap: Texture2d) {
        vertices { isInstanced = true }
        color { vertexColor() }
        shadow { addShadowMap(shadowMap) }
        enableSsao(ssaoMap)
        colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        modelCustomizer = { windMod() }
    }

    private fun pbrConfig(ibl: EnvironmentMaps, shadowMap: ShadowMap, ssaoMap: Texture2d) = KslPbrShader.Config().apply {
        baseConfig(shadowMap, ssaoMap)
        roughness(1f)
        with (TerrainDemo) {
            iblConfig(ibl)
        }
    }

    private fun blinnPhongConfig(ibl: EnvironmentMaps, shadowMap: ShadowMap, ssaoMap: Texture2d) = KslBlinnPhongShader.Config().apply {
        baseConfig(shadowMap, ssaoMap)
        imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
        specularStrength(0.05f)
    }

    private fun shadowConfig(isAoDepth: Boolean) = KslDepthShader.Config().apply {
        vertices { isInstanced = true }
        if (isAoDepth) {
            outputMode = KslDepthShader.OutputMode.NORMAL_LINEAR
        }
        modelCustomizer = { windMod() }
    }

    fun KslProgram.windMod() {
        vertexStage {
            main {
                val worldPosPort = getFloat3Port("worldPos")

                val windTex = texture3d("tWindTex")
                val windOffset = uniformFloat4("uWindOffsetStrength")
                val worldPos = worldPosPort.input.input!!
                val windSamplePos = (windOffset.xyz + worldPos) * uniformFloat1("uWindScale")
                val windValue = float3Var(sampleTexture(windTex, windSamplePos).xyz - float3Value(0.5f, 0.5f, 0.5f), "windValue")
                windValue.y *= 0.5f.const
                val displacement = float3Port("windDisplacement", windValue * vertexAttribFloat1(Wind.WIND_SENSITIVITY.name) * windOffset.w)
                worldPosPort.input(worldPos + displacement)
            }
        }
    }
}
