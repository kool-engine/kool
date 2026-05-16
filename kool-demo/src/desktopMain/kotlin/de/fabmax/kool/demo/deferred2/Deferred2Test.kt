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
import de.fabmax.kool.pipeline.BloomPass
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.swapPipelineDataCapturing
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.debugOverlay

fun gbufferShader(objectId: Int, block: GbufferShaderConfig.Builder.() -> Unit): GbufferShader {
    val cfg = GbufferShaderConfig.Builder().apply{
        this.objectId = objectId
        block()
    }.build()
    return GbufferShader(cfg)
}

suspend fun KoolApplication.deferred2Test() {
    val texAlbedo = Assets.loadTexture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg").getOrThrow()
    val texNormal = Assets.loadTexture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg").getOrThrow()
    val uvChecker = Assets.loadTexture2d("materials/uv_checker_map.jpg").getOrThrow()

    addScene {
        val content = Node().apply {

            addGroup {
                onUpdate {
//                    transform.rotate(-30f.deg * Time.deltaT, Vec3f.Y_AXIS)
                }

                addColorMesh {
                    generate {
                        color = MdColor.PINK.toLinear()
                        cube { origin.set(-2.5f, 0f, 0f)}
                        color = MdColor.AMBER.toLinear()
                        icoSphere {
                            center.set(-2.5f, 0f, 2.5f)
                            steps = 4
                            radius = 0.5f
                        }
                    }
                    onUpdate {
//                    transform.rotate(360f.deg * Time.deltaT, Vec3f.Y_AXIS)
                    }
                    shader = gbufferShader(objectId = 1) {
                        color { vertexColor() }
                    }
                }
                addTextureMesh(isNormalMapped = true) {
                    generate {
                        cube { }
                    }
                    onUpdate {
                        transform
                            .setIdentity()
//                            .rotate(90f.deg * Time.gameTime.toFloat(), Vec3f.Y_AXIS)
                            //.rotate(20f.deg, Vec3f.Y_AXIS)
                            .translate(2.5f, 0f, 0f)
                    }
                    shader = gbufferShader(objectId = 2) {
                        color {
                            textureColor(texAlbedo)
                        }
                        normalMapping {
                            useNormalMap(texNormal)
                        }
                    }.apply {
//                    bindTexture2d("tbaseColor", uvChecker)
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
                    shader = gbufferShader(objectId = 3) {
                        color { vertexColor() }
                    }
                }
                addColorMesh {
                    generate {
                        translate(0f, -1.5f, 0f)
                        color = Color.WHITE
                        grid {
                            sizeX = 50f
                            sizeY = 50f
                        }
                    }
                    shader = gbufferShader(objectId = 4) {
                        color { vertexColor() }
                    }
                }
            }
        }

        val lighting = Lighting().apply {
            singlePointLight {
                setup(Vec3f(1.2f, 1.2f, 2f))
                setColor(Color.WHITE, intensity = 50f)
            }
        }
        val deferred2Pipeline = Deferred2Pipeline(content, lighting, this)

        content.apply {
            val orbitCam = orbitCamera(deferred2Pipeline.camera) { }
            addNode(orbitCam)
        }

        val bloomPass = BloomPass(deferred2Pipeline.filterPass.filterOutput.newVal)
        addComputePass(bloomPass)
        deferred2Pipeline.onSwap {
            val filterOutput = deferred2Pipeline.filterPass.filterOutput.newVal
            bloomPass.inputShader.swapPipelineDataCapturing(filterOutput) {
                bloomPass.inputTexture = filterOutput
            }
        }

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
                        val bloom = texture2d("bloomOutput")
                        val uvi = (uv.output * output.size().toFloat2() + 0.5f.const2).toInt2()
                        val color by output.load(uvi).rgb + bloom.sample(uv.output).rgb

                        val ditherTex = texture2d("ditherPattern")
                        val ditherC by uvi % ditherTex.size()
                        val ditherNoise by ditherTex.load(ditherC).r
                        val srgb by convertColorSpace(color, ColorSpaceConversion.LinearToSrgbHdr()) + (ditherNoise - 0.5f.const) / 255f.const
                        colorOutput(srgb)
//                        colorOutput(color)
                    }
                }
            }

            outShader.bindTexture2d("ditherPattern", makeDitherPattern())
            outShader.bindTexture2d("bloomOutput", bloomPass.bloomMap)
            var inputTex by outShader.bindTexture2d("deferredOutput", deferred2Pipeline.gbuffers.a.encodedNormals)
            onUpdate {
                val filterOutput = deferred2Pipeline.filterPass.filterOutput.newVal
                outShader.swapPipelineDataCapturing(filterOutput) {
                    inputTex = filterOutput
                }
            }

            shader = outShader
        }
    }

    ctx.scenes += debugOverlay()
}

class AlternatingPair<out T>(factory: (Boolean) -> T) {
    val a: T = factory(true)
    val b: T = factory(false)

    val newVal: T get() = if (Time.frameCount % 2 == 0) a else b
    val oldVal: T get() = if (Time.frameCount % 2 == 0) b else a
}