package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.createUint8Buffer
import kotlin.math.sin

class KslShaderTest : DemoScene("KslShader") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        lighting.singleLight {
            setPoint(Vec3f(0f, 0f, 3f))
            setColor(Color.WHITE, 10f)
        }

        +mesh(listOf(Attribute.POSITIONS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.NORMALS)) {
            generate {
                color = MdColor.LIGHT_GREEN
                rect {
                    origin.set(-0.9f, -0.9f, 0f)
                    size.set(1.8f, 1.8f)
                }
            }

            instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR)).apply {
                val mat = Mat4f()
                val mutColor = MutableColor()
                var i = 0
                for (y in -2 .. 2) {
                    for (x in -2 .. 2) {
                        mat.setIdentity().translate(x * 2f, y * 2f, 0f)
                        addInstance {
                            put(mat.matrix)
                            put(mutColor.set(MdColor.PALETTE[i++ % MdColor.PALETTE.size]).toLinear().array)
                        }
                    }
                }
            }

//            val unlitShader = unlitShader {
//                isInstanced = true
//                color {
//                    addTextureColor(makeNoiseTex(), mixMode = ColorBlockConfig.MixMode.Set)
//                    addInstanceColor(mixMode = ColorBlockConfig.MixMode.Multiply)
//                    addUniformColor(Color.WHITE, mixMode = ColorBlockConfig.MixMode.Multiply)
//                }
//            }
//            shader = unlitShader
//            onUpdate += {
//                val brightness = sin(it.time * 3).toFloat() * 0.5f + 0.5f
//                unlitShader.uniformColor = Vec4f(brightness, brightness, brightness, 1f)
//            }

            val phongConfig = KslBlinnPhongShader.Config().apply {
                isInstanced = true
                color {
                    addTextureColor(makeNoiseTex(), mixMode = ColorBlockConfig.MixMode.Set, gamma = Color.GAMMA_sRGB_TO_LINEAR)
                    addInstanceColor(mixMode = ColorBlockConfig.MixMode.Multiply)
                    addUniformColor(Color.WHITE, mixMode = ColorBlockConfig.MixMode.Multiply)
                }
            }
            val phongShader = KslBlinnPhongShader(phongConfig)
            shader = phongShader
            onUpdate += {
                val brightness = sin(it.time * 3).toFloat() * 0.5f + 0.5f
                phongShader.uniformDiffuseColor = Vec4f(brightness, brightness, brightness, 1f)
            }

        }
    }

    private fun makeNoiseTex(): Texture2d {
        val w = 16
        val h = 16
        val noiseTexData = createUint8Buffer(w * h * 4)
        for (x in 0 until w) {
            for (y in 0 until w) {
                val n = (randomF() * 255).toInt().toByte()
                noiseTexData[(x + y * w) * 4 + 0] = n
                noiseTexData[(x + y * w) * 4 + 1] = n
                noiseTexData[(x + y * w) * 4 + 2] = n
                noiseTexData[(x + y * w) * 4 + 3] = 255.toByte()
            }
        }
        return Texture2d(
            TextureProps(
                minFilter = FilterMethod.LINEAR,
                magFilter = FilterMethod.NEAREST,
                addressModeU = AddressMode.CLAMP_TO_EDGE,
                addressModeV = AddressMode.CLAMP_TO_EDGE,
                mipMapping = false,
                maxAnisotropy = 1),
            loader = BufferedTextureLoader(TextureData2d(noiseTexData, w, h, TexFormat.RGBA))
        )
    }
}