package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.addScene
import de.fabmax.kool.loadTexture2d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.swapPipelineDataCapturing
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.debugOverlay

suspend fun KoolApplication.deferred2Test() {
    val texAlbedo = Assets.loadTexture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg").getOrThrow()
    val texNormal = Assets.loadTexture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg").getOrThrow()

    addScene {
        val content = Node().apply {
            val gShader = GbufferShader(GbufferShaderConfig.Builder().apply { color { vertexColor() } }.build())
            addColorMesh {
                generate {
                    color = Color.WHITE
                    cube { origin.set(-2.5f, 0f, 0f)}
                }
                onUpdate {
//                    transform.rotate(360f.deg * Time.deltaT, Vec3f.Y_AXIS)
                }
                shader = gShader
            }
            addTextureMesh(isNormalMapped = true) {
                generate {
                    cube { origin.set(0f, 0f, 0f) }
                }
                onUpdate {
                    transform
                        .setIdentity()
                        .rotate(90f.deg * Time.gameTime.toFloat(), Vec3f.Y_AXIS)
                        .translate(2.5f, 0f, 0f)
                }
                shader = GbufferShader(GbufferShaderConfig.Builder().apply {
                    color {
                        textureColor(texAlbedo)
                    }
                    normalMapping {
                        useNormalMap(texNormal)
                    }
                }.build()).apply {
                    bindTexture2d("tbaseColor", texAlbedo)
                    bindTexture2d("tNormalMap", texNormal)
                }
            }
            addColorMesh {
                generate {
                    cube { colored() }
                }
                onUpdate {
                    transform.rotate(90f.deg * Time.deltaT, Vec3f.Y_AXIS)
                }
                shader = gShader
            }
            addColorMesh {
                generate {
                    translate(0f, -2f, 0f)
                    color = MdColor.PURPLE //Color.WHITE
                    grid {
                        sizeX = 50f
                        sizeY = 50f
                    }
                }
                shader = gShader
            }
        }

        val lighting = Lighting().apply {
            singlePointLight {
                //setup(Vec3f(3f, 4f, 2f))
                //setColor(MdColor.AMBER.toLinear(), intensity = 30f)
                setup(Vec3f(1.2f, 1.2f, 2f))
                setColor(Color.WHITE, intensity = 5f)
            }
        }
        val deferred2Pipeline = Deferred2Pipeline(content, lighting, this)

        content.apply {
            val orbitCam = orbitCamera(deferred2Pipeline.camera) { }
            addNode(orbitCam)
        }

//        val matPass = GbufferPass(content)
//        addOffscreenPass(matPass)
//        content.apply {
//            orbitCamera(matPass.defaultView) { }
//        }
//
//        val lighting = Lighting().apply {
//            singlePointLight {
//                //setup(Vec3f(3f, 4f, 2f))
//                //setColor(MdColor.AMBER.toLinear(), intensity = 30f)
//                setup(Vec3f(1.2f, 1.2f, 2f))
//                setColor(Color.WHITE, intensity = 5f)
//            }
//        }
//
//        val lightingPass = DeferredLightingPass(
//            depth = matPass.depth,
//            encodedNormalsMeta = matPass.encodedNormalsMeta,
//            albedoEmission = matPass.albedoEmission,
//            metalRoughnessAo = matPass.metalRoughnessAo,
//            sceneCam = matPass.camera,
//            lighting = lighting,
//        )
//        addComputePass(lightingPass)

        addTextureMesh {
            generate {
                generateFullscreenQuad()
            }
            val outShader = KslShader("deferred2-output") {
                val uv = interStageFloat2()
                fullscreenQuadVertexStage(uv)
                fragmentStage {
                    main {
                        val output = texture2d("deferredOutput")
                        val uvi = (uv.output * output.size().toFloat2()).toInt2()
                        val color by output.load(uvi).rgb


                        val ditherTex = texture2d("ditherPattern")
                        //val ditherC by baseCoord % ditherTex.size()
                        val ditherNoise by ditherTex.load(uvi).r
                        val srgb by convertColorSpace(color, ColorSpaceConversion.LinearToSrgbHdr()) + (ditherNoise - 0.5f.const) / 255f.const


                        //color set convertColorSpace(color, ColorSpaceConversion.LinearToSrgbHdr())
                        colorOutput(srgb)
                    }
                }
            }

            outShader.bindTexture2d("ditherPattern", makeDitherPattern())
            var inputTex by outShader.bindTexture2d("deferredOutput", deferred2Pipeline.lightingPass.lightingOutput)
            onUpdate {
                outShader.swapPipelineDataCapturing(deferred2Pipeline.filterPass.filterOutput.newVal) {
                    inputTex = deferred2Pipeline.filterPass.filterOutput.newVal
                }
            }

            shader = outShader
        }
    }

    ctx.scenes += debugOverlay()
}

class AlternatingPair<out T>(factory: () -> T) {
    val a: T = factory()
    val b: T = factory()

    val newVal: T get() = if (Time.frameCount % 2 == 0) a else b
    val oldVal: T get() = if (Time.frameCount % 2 == 0) b else a
}