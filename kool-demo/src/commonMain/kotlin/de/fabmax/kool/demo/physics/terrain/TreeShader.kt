package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.*
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture3d
import de.fabmax.kool.util.ShadowMap

interface EnvMapShader {
    val shader: KslShader
    fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps)
}

interface WindAffectedShader : EnvMapShader {
    var windOffsetStrength: Vec4f
    var windScale: Float
    var windDensity: Texture3d?
}

object TreeShader {
    class Pbr(shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d)
        : KslPbrShader(pbrConfig(shadowMap, ssaoMap)), WindAffectedShader {
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)
        override val shader = this

        override fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps) {
            with(TerrainDemo) { updateSky(envMaps) }
        }
    }

    class BlinnPhong(shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d)
        : KslBlinnPhongShader(blinnPhongConfig(shadowMap, ssaoMap)), WindAffectedShader {
        override val shader = this
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps) {
            with(TerrainDemo) { updateSky(envMaps) }
        }
    }

    class Shadow(windTex: Texture3d, isAoDepth: Boolean) : KslDepthShader(shadowConfig(isAoDepth)), WindAffectedShader {
        override val shader = this
        override var windOffsetStrength by uniform4f("uWindOffsetStrength")
        override var windScale by uniform1f("uWindScale", 0.01f)
        override var windDensity by texture3d("tWindTex", windTex)

        override fun updateEnvMaps(envMaps: Sky.WeightedEnvMaps) { }
    }

    fun makeTreeShader(shadowMap: ShadowMap, ssaoMap: Texture2d, windTex: Texture3d, isPbr: Boolean): WindAffectedShader {
        return if (isPbr) {
            Pbr(shadowMap, ssaoMap, windTex)
        } else {
            BlinnPhong(shadowMap, ssaoMap, windTex)
        }
    }

    private fun KslLitShader.LitShaderConfig.baseConfig(shadowMap: ShadowMap, ssaoMap: Texture2d) {
        vertices { isInstanced = true }
        color { vertexColor() }
        shadow { addShadowMap(shadowMap) }
        enableSsao(ssaoMap)
        dualImageBasedAmbientColor()
        colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        modelCustomizer = { windMod() }
    }

    private fun pbrConfig(shadowMap: ShadowMap, ssaoMap: Texture2d) = KslPbrShader.Config().apply {
        baseConfig(shadowMap, ssaoMap)
        roughness(1f)
        with (TerrainDemo) {
            iblConfig()
        }
    }

    private fun blinnPhongConfig(shadowMap: ShadowMap, ssaoMap: Texture2d) = KslBlinnPhongShader.Config().apply {
        baseConfig(shadowMap, ssaoMap)
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
