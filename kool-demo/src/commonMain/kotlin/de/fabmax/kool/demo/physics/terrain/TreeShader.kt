package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.noise.MultiPerlin3d
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslDepthShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.getFloat3Port
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class TreeShader(ibl: EnvironmentMaps, shadowMap: ShadowMap, windTex: Texture3d) : KslBlinnPhongShader(treeShaderConfig(ibl, shadowMap)) {

    var windOffset by uniform3f("uWindOffset")
    var windStrength by uniform1f("uWindStrength", 1f)
    var windScale by uniform1f("uWindScale", 0.01f)
    var windDensity by texture3d("tWindTex", windTex)

    class Shadow(windTex: Texture3d) : KslDepthShader(treeShadowConfig()) {
        var windOffset by uniform3f("uWindOffset")
        var windStrength by uniform1f("uWindStrength", 1f)
        var windScale by uniform1f("uWindScale", 0.01f)
        var windDensity by texture3d("tWindTex", windTex)

        companion object {
            private fun treeShadowConfig() = Config().apply {
                isInstanced = true
                modelCustomizer = {
                    //dumpCode = true
                    windMod()
                }
            }
        }
    }

    companion object {
        private fun KslProgram.windMod() {
            vertexStage {
                main {
                    val worldPosPort = getFloat3Port("worldPos")

                    val windTex = texture3d("tWindTex")
                    val windOffset = uniformFloat3("uWindOffset")
                    val windStrength = uniformFloat1("uWindStrength")
                    val worldPos = worldPosPort.input.input!!
                    val windSamplePos = (windOffset + worldPos) * uniformFloat1("uWindScale")
                    val windValue = float3Var(sampleTexture(windTex, windSamplePos).xyz - constFloat3(0.5f, 0.5f, 0.5f), "windValue")
                    windValue.y *= 0.5f.const
                    val displacement = windValue * vertexAttribFloat1(WIND_SENSITIVITY.name) * windStrength
                    worldPosPort.input(worldPos + displacement)
                }
            }
        }

        private fun treeShaderConfig(ibl: EnvironmentMaps, shadowMap: ShadowMap) = Config().apply {
            color { addVertexColor() }
            shadow { addShadowMap(shadowMap) }
            imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
            specularStrength = 0.05f
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            isInstanced = true
            modelCustomizer = { windMod() }
        }

        val WIND_SENSITIVITY = Attribute("aWindSense", GlslType.FLOAT)

        fun generateWindDensityTex(): Texture3d {
            val pt = PerfTimer()
            var min = 10f
            var max = -10f

            val perlin = MultiPerlin3d(3, 3)
            val nMinP = -0.7f
            val nMaxP = 0.7f

            val sz = 96
            val buf = createUint8Buffer(sz*sz*sz*4)
            for (z in 0 until sz) {
                for (y in 0 until sz) {
                    for (x in 0 until sz) {
                        val nx = x / sz.toFloat()
                        val ny = y / sz.toFloat()
                        val nz = z / sz.toFloat()

                        val fp = perlin.eval(nx, ny, nz)
                        val n = (fp - nMinP) / (nMaxP - nMinP) * 0.9f + 0.06f
                        val c = (n * 255).roundToInt().clamp(0, 255)
                        min = min(min, n)
                        max = max(max, n)

                        buf.put(c.toByte())
                        buf.put(0.toByte())
                        buf.put(0.toByte())
                        buf.put(255.toByte())
                    }
                }
            }
            buf.flip()

            for (z in 0 until sz) {
                for (y in 0 until sz) {
                    for (x in 0 until sz) {
                        val i = z * sz * sz + y * sz + x
                        buf[i * 4 + 1] = buf[((sz-1-z) * sz * sz + y * sz + x) * 4]
                        buf[i * 4 + 2] = buf[(z * sz * sz + (sz-1-y) * sz + x) * 4]
                    }
                }
            }

            logD { "Generated wind density in ${pt.takeSecs().toString(3)} s, tex saturation: min = $min, max = $max" }

            val props = TextureProps(
                addressModeU = AddressMode.REPEAT,
                addressModeV = AddressMode.REPEAT,
                addressModeW = AddressMode.REPEAT,
                maxAnisotropy = 1
            )
            return Texture3d(props) { TextureData3d(buf, sz, sz, sz, TexFormat.RGBA) }
        }
    }
}
