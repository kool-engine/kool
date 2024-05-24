package de.fabmax.kool.demo.tests

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.BlinnPhongMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.TexCoordAttributeBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.PI

class KslShaderTest : DemoScene("KslShader") {

    private lateinit var colorMap: Texture2d
    private lateinit var normalMap: Texture2d

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        colorMap = loadTexture2d("${DemoLoader.materialPath}/castle_brick/castle_brick_02_red_diff_2k.jpg").also { it.releaseWith(mainScene) }
        normalMap = loadTexture2d("${DemoLoader.materialPath}/castle_brick/castle_brick_02_red_nor_2k.jpg").also { it.releaseWith(mainScene) }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        val lightPoses = listOf(
            Vec3f(5f, 5f, 3f) to Vec3f(-1f, -1f, -1f),
            Vec3f(5f, -5f, 3f) to Vec3f(-1f, 1f, -1f),
            Vec3f(-5f, 5f, 3f) to Vec3f(1f, -1f, -1f),
            Vec3f(-5f, -5f, 3f) to Vec3f(1f, 1f, -1f),
        )

        lighting.apply {
            clear()
            addSpotLight {
                setup(lightPoses[2].first, lightPoses[2].second, 60f.deg)
                setColor(MdColor.LIGHT_GREEN.toLinear(), 30f)
            }
            addSpotLight {
                setup(lightPoses[3].first, lightPoses[3].second, 60f.deg)
                setColor(MdColor.ORANGE.toLinear(), 30f)
            }
        }

        val shadowMaps = lighting.lights.map { light ->
            if (light is Light.Directional) {
                CascadedShadowMap(this, light).apply { setMapRanges(0.05f, 0.25f, 1f) }
            } else {
                SimpleShadowMap(this, light, mapSize = 2048)
            }
        }

        val lightRotTransform = MutableMat4f()
        onUpdate += {
            lightRotTransform.rotate(5f.deg * Time.deltaT, Vec3f.Z_AXIS)
            lighting.lights.forEachIndexed { i, light ->
                val pos = lightRotTransform.transform(MutableVec3f(lightPoses[i].first), 1f)
                val dir = lightRotTransform.transform(MutableVec3f(lightPoses[i].second), 0f)
                (light as Light.Spot).setup(pos, dir, 60f.deg)
            }
        }

        addGroup {
            addColorMesh {
                generate {
                    cube {
                        colored()
                        size.set(0.5f, 0.5f, 0.5f)
                    }
                    cube {
                        colored()
                        size.set(0.2f, 0.2f, 0.2f)
                        origin.set(0.1f, 0.35f, 0.1f)
                    }

                    color = MdColor.LIME.toLinear()
                    icoSphere {
                        steps = 3
                        radius = 0.2f
                        center.set(0f, -0.3f, 0f)
                    }
                }

                shader = KslBlinnPhongShader {
                    color {
                        vertexColor()
                    }
                    shadow {
                        addShadowMaps(shadowMaps)
                    }

//                    modelCustomizer = {
//                        fragmentStage {
//                            main {
//                                colorOutput(Vec4f(1f, 0f, 0f, 1f).const)
//                            }
//                        }
//                    }
                }
            }

            var rotationX = -23f
            var rotationY = 52f
//            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "rotx") {
//                rotationX += 1f
//                println("x: $rotationX, y: $rotationY")
//            }
//            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "rotx") {
//                rotationX -= 1f
//                println("x: $rotationX, y: $rotationY")
//            }
//            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "roty") {
//                rotationY += 1f
//                println("x: $rotationX, y: $rotationY")
//            }
//            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "roty") {
//                rotationY -= 1f
//                println("x: $rotationX, y: $rotationY")
//            }

            onUpdate += {
                transform.setIdentity()
                transform.scale(1.5f)
                transform.translate(0f, 0f, 0.5f)
                transform.rotate(rotationX.deg, Vec3f.X_AXIS)
                transform.rotate(rotationY.deg, Vec3f.Y_AXIS)

                rotationX += Time.deltaT * 10f
                rotationY += Time.deltaT * 13.7f
            }
        }

        val instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR)).apply {
            val mat = MutableMat4f()
            val mutColor = MutableColor()
            var i = 0
            for (y in -2 .. 2) {
                for (x in -2 .. 2) {
                    mat.setIdentity().translate(x * 2.5f, y * 2.5f, 0f)
                    addInstance {
                        mat.putTo(this)
                        mutColor.set(MdColor.PALETTE[i++ % MdColor.PALETTE.size]).toLinear().putTo(this)
                    }
                }
            }
        }

        addMesh(
            Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, Attribute.COLORS, Attribute.TANGENTS,
            instances = instances
        ) {
            generate {
                color = MdColor.LIGHT_GREEN
                rect {
                    isCenteredOrigin = false
                    origin.set(-1.25f, -1.25f, 0f)
                    size.set(2.4f, 2.4f)
                }
                geometry.generateTangents()
            }

            val phongShader = KslBlinnPhongShader {
                shininess(16f)
                specularStrength(0.25f)

                vertices { isInstanced = true }
                color {
                    //addInstanceColor()
                    //addStaticColor(Color.WHITE)
                    textureColor(colorMap)
                }
                normalMapping { setNormalMap(normalMap) }
                shadow { addShadowMaps(shadowMaps) }
                pipeline { cullMethod = CullMethod.NO_CULLING }

                modelCustomizer = {
                    dumpCode = true
                    //sparkleMod()
                }
            }

            var uSparkle by phongShader.uniform1f("uSparkle")
            phongShader.texture2d("tShininess", makeNoiseTex())

            shader = phongShader
            onUpdate += {
                uSparkle = (Time.gameTime % 1.0).toFloat()
            }
        }
    }

    private fun KslProgram.sparkleMod() {
        val shininessTex = texture2d("tShininess")
        val sparkleOffset = uniformFloat1("uSparkle")
        val instanceOffset = interStageFloat1()

        vertexStage {
            main {
                instanceOffset.input set inInstanceIndex.toFloat1() * 1.73f.const
            }
        }

        fragmentStage {
            main {
                findBlock<BlinnPhongMaterialBlock>()!!.apply {
                    val texCoordBlock: TexCoordAttributeBlock = vertexStage!!.findBlock()!!
                    val texCoords = texCoordBlock.getTextureCoords()
                    val sparkle = float1Var((sampleTexture(shininessTex, texCoords).r + sparkleOffset + instanceOffset.output) * (2f * PI.toFloat()).const)
                    inShininess(10f.const + (cos(sparkle) * 0.5f.const + 0.5f.const) * 30f.const)
                }
            }
        }
    }

    private fun makeNoiseTex(): Texture2d {
        val w = 16
        val h = 16
        val noiseTexData = Uint8Buffer(w * h * 4)
        for (x in 0 until w) {
            for (y in 0 until w) {
                val n = (randomF() * 255).toInt().toUByte()
                noiseTexData[(x + y * w) * 4 + 0] = n
                noiseTexData[(x + y * w) * 4 + 1] = n
                noiseTexData[(x + y * w) * 4 + 2] = n
                noiseTexData[(x + y * w) * 4 + 3] = 255u
            }
        }
        return Texture2d(
            TextureProps(
                generateMipMaps = false,
                defaultSamplerSettings = SamplerSettings(
                    minFilter = FilterMethod.LINEAR,
                    magFilter = FilterMethod.NEAREST,
                    addressModeU = AddressMode.CLAMP_TO_EDGE,
                    addressModeV = AddressMode.CLAMP_TO_EDGE
                )
            ),
            loader = BufferedTextureLoader(TextureData2d(noiseTexData, w, h, TexFormat.RGBA))
        )
    }
}