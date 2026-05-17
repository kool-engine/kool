package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
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

class Deferred2Demo : DemoScene("Deferred2 Demo") {

    private val albedoMap by texture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg")
    private val normalMap by texture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg")
    //private val uvChecker by texture2d("materials/uv_checker_map.jpg")

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val content = deferredContent()
        val lighting = Lighting().apply {
            singlePointLight {
                setup(Vec3f(1.2f, 3.2f, 2f))
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
                        val output = de.fabmax.kool.modules.ksl.lang.texture2d("deferredOutput")
                        val bloom = de.fabmax.kool.modules.ksl.lang.texture2d("bloomOutput")
                        val uvi = (uv.output * output.size().toFloat2() + 0.5f.const2).toInt2()
                        val color by output.load(uvi).rgb + bloom.sample(uv.output).rgb

                        val ditherTex = de.fabmax.kool.modules.ksl.lang.texture2d("ditherPattern")
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
            var inputTex by outShader.bindTexture2d("deferredOutput")
            onUpdate {
                val filterOutput = deferred2Pipeline.filterPass.filterOutput.newVal
                outShader.swapPipelineDataCapturing(filterOutput) {
                    inputTex = filterOutput
                }
            }

            shader = outShader
        }
    }

    private fun deferredContent() = Node("deferred content").apply {
        addGroup {
            onUpdate {
//                transform.rotate(-30f.deg * Time.deltaT, Vec3f.Y_AXIS)
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
//                        .rotate(90f.deg * Time.gameTime.toFloat(), Vec3f.Y_AXIS)
                        //.rotate(20f.deg, Vec3f.Y_AXIS)
                        .translate(2.5f, 0f, 0f)
                }
                shader = gbufferShader(objectId = 2) {
                    color {
                        textureColor(albedoMap)
                    }
                    normalMapping {
                        useNormalMap(normalMap)
                    }
                }.apply {
//                    bindTexture2d("tbaseColor", uvChecker)
                    bindTexture2d("tbaseColor", albedoMap)
                    bindTexture2d("tNormalMap", normalMap)
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
                    translate(0f, -0.5f, 0f)
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
}
