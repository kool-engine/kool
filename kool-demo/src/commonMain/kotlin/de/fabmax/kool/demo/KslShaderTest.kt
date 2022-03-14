package de.fabmax.kool.demo

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.BlinnPhongMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.SimpleShadowMapConfig
import de.fabmax.kool.modules.ksl.lang.plus
import de.fabmax.kool.modules.ksl.lang.r
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.modules.ksl.lang.toFloat1
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.PI

class KslShaderTest : DemoScene("KslShader") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        lighting.apply {
            lights.clear()
            lights += Light().apply {
//                setPoint(Vec3f(3f, 3f, 3f))
//                setColor(Color.WHITE, 5f)
                setSpot(Vec3f(5f, 5f, 3f), Vec3f(-1f, -1f, -1f), 45f)
                setColor(Color.WHITE, 15f)
//                setDirectional(Vec3f(-1f, -1f, -1f))
//                setColor(Color.WHITE, 1f)
            }
            lights += Light().apply {
                setPoint(Vec3f(3f, -3f, 3f))
                setColor(MdColor.RED.toLinear(), 5f)
            }
            lights += Light().apply {
                setPoint(Vec3f(-3f, -3f, 3f))
                setColor(MdColor.GREEN.toLinear(), 5f)
            }
            lights += Light().apply {
                setPoint(Vec3f(-3f, 3f, 3f))
                setColor(MdColor.BLUE.toLinear(), 5f)
            }
        }

        val simpleShadowMap = SimpleShadowMap(this, 0, 2048)

        +group {
            +colorMesh {
                generate {
                    cube {
                        colored()
//                        origin.set(3f, 3f, 1.3f)
                        size.set(0.5f, 0.5f, 0.5f)
                        centered()
                    }
                    cube {
                        colored()
                        origin.set(0.0f, 0.25f, 0.0f)
                        size.set(0.2f, 0.2f, 0.2f)
                    }

                    color = MdColor.LIME.toLinear()
                    icoSphere {
                        steps = 3
                        radius = 0.2f
                        center.set(0f, -0.3f, 0f)
                    }
                }

                shader = blinnPhongShader {
                    color {
                        addVertexColor()
                    }
                    shadowCfg = SimpleShadowMapConfig(simpleShadowMap, 0)
                }
            }

            var rotationX = -23f //-46f
            var rotationY = 52f //40f

            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "rotx") {
                rotationX += 1f
                println("x: $rotationX, y: $rotationY")
            }
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "rotx") {
                rotationX -= 1f
                println("x: $rotationX, y: $rotationY")
            }
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "roty") {
                rotationY += 1f
                println("x: $rotationX, y: $rotationY")
            }
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "roty") {
                rotationY -= 1f
                println("x: $rotationX, y: $rotationY")
            }

            onUpdate += {
                setIdentity()
                translate(3.25, 3.25, 1.55)
                rotate(rotationX, Vec3f.X_AXIS)
                rotate(rotationY, Vec3f.Y_AXIS)
            }
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

            val phongShader = blinnPhongShader {
                isInstanced = true
                color {
                    addInstanceColor()
                }
                pipeline {
                    cullMethod = CullMethod.NO_CULLING
                }
                shadowCfg = SimpleShadowMapConfig(simpleShadowMap, 0)

                modelCustomizer = {
                    val shininessTex = texture2d("tShininess")
                    val sparkleOffset = uniformFloat1("uSparkle")
                    val texCoords = interStageFloat2()
                    val instanceOffset = interStageFloat1()

                    vertexStage {
                        main {
                            texCoords.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                            instanceOffset.input set inInstanceIndex.toFloat1() * 1.73f.const
                        }
                    }

                    fragmentStage {
                        main {
                            findBlock<BlinnPhongMaterialBlock>()!!.apply {
                                val sparkle = floatVar((sampleTexture(shininessTex, texCoords.output).r + sparkleOffset + instanceOffset.output) * (2f * PI.toFloat()).const)
                                inShininess = 10f.const + (cos(sparkle) * 0.5f.const + 0.5f.const) * 30f.const
                            }
                        }
                    }
                }
            }

            var uSparkle: Uniform1f? = null
            phongShader.onPipelineCreated += { _, _, _ ->
                phongShader.texSamplers2d["tShininess"]!!.texture = makeNoiseTex()
                uSparkle = phongShader.uniforms["uSparkle"] as Uniform1f
            }

            shader = phongShader
            onUpdate += { ev ->
                uSparkle?.value = (ev.time % 1.0).toFloat()
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