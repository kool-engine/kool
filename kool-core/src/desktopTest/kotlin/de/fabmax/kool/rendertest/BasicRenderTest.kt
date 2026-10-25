package de.fabmax.kool.rendertest

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Uint8Buffer
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.DurationUnit

class BasicRenderTest {

    @Test
    fun clearColor() = renderTest {
        addScene {
            clearColor = ClearColorFill(MdColor.INDIGO)
        }
        val image = captureFrame()
        assertMatchesReference("basic/clear-color", image, ImageTolerance(maxChannelDiff = 1, maxDiffPixelRatio = 0.0))

        // additionally check pixel values directly: this also catches errors in the reference image handling
        // clear colors are written to the (UNORM) output as is, without any color space conversion
        val expected = MdColor.INDIGO
        val center = image.toBufferedImage().getRGB(image.width / 2, image.height / 2)
        assertChannel(expected.r, (center shr 16) and 0xff, "red")
        assertChannel(expected.g, (center shr 8) and 0xff, "green")
        assertChannel(expected.b, center and 0xff, "blue")
    }

    @Test
    fun unlitVertexColorCube() = renderTest {
        addScene {
            camera.setupCamera(position = Vec3f(2f, 2f, 3f), lookAt = Vec3f.ZERO)
            addColorMesh {
                generate {
                    cube { colored() }
                }
                shader = KslUnlitShader { color { vertexColor() } }
            }
        }
        assertFrameMatchesReference("basic/unlit-vertex-color-cube")
    }

    @Test
    fun texturedQuad() = renderTest {
        val texture = Texture2d(
            data = checkerboard(64, 8, Color.WHITE, MdColor.DEEP_ORANGE),
            mipMapping = MipMapping.Off,
            samplerSettings = SamplerSettings().nearest(),
            name = "render-test-checkerboard"
        )
        addScene {
            camera.setupCamera(position = Vec3f(0f, -1.5f, 2f), lookAt = Vec3f.ZERO)
            addTextureMesh {
                generate {
                    rect {
                        size.set(2f, 2f)
                    }
                }
                shader = KslUnlitShader {
                    color { textureColor(texture) }
                    // texture colors are converted to linear color space, convert them back for the sRGB output
                    colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
                }
            }
            onRelease { texture.release() }
        }
        assertFrameMatchesReference("basic/textured-quad")
    }

    @Test
    fun pbrSpheres() = renderTest(400, 200) {
        addScene {
            camera.setupCamera(position = Vec3f(0f, 0f, 7f), lookAt = Vec3f.ZERO)
            lighting.singleDirectionalLight {
                setup(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 5f)
            }
            val roughness = listOf(0.1f, 0.4f, 0.7f, 1f)
            roughness.forEachIndexed { i, r ->
                addColorMesh {
                    generate {
                        color = MdColor.LIGHT_BLUE
                        translate((i - 1.5f) * 2.2f, 0f, 0f)
                        icoSphere {
                            steps = 3
                        }
                    }
                    shader = KslPbrShader {
                        color { vertexColor() }
                        metallic(0f)
                        roughness(r)
                    }
                }
            }
        }
        assertFrameMatchesReference("basic/pbr-spheres")
    }

    @Test
    fun fixedTimeStepAnimation() = renderTest {
        var deltaT = 0f
        addScene {
            camera.setupCamera(position = Vec3f(0f, 1.5f, 3f), lookAt = Vec3f.ZERO)
            addColorMesh {
                generate {
                    cube { colored() }
                }
                shader = KslUnlitShader { color { vertexColor() } }

                // rotates the cube by 90° per second -> 1.5° per frame with the fixed time step of 1/60 s
                var angle = 0f
                onUpdate {
                    deltaT = Time.deltaT
                    angle += 90f * Time.deltaT
                    transform.setIdentity().rotate(angle.deg, Vec3f.Y_AXIS)
                }
            }
        }
        // 19 warmup frames + 1 captured frame -> 20 updates -> 30° rotation
        val image = captureFrame(warmupFrames = 19)
        assertEquals(RenderTestContext.timeStep.toDouble(DurationUnit.SECONDS).toFloat(), deltaT, 1e-6f)
        assertMatchesReference("basic/animated-cube-30deg", image)
    }

    @Test
    fun resize() = renderTest(320, 240) {
        addScene {
            camera.setupCamera(position = Vec3f(2f, 2f, 3f), lookAt = Vec3f.ZERO)
            addColorMesh {
                generate {
                    cube { colored() }
                }
                shader = KslUnlitShader { color { vertexColor() } }
            }
        }
        val before = captureFrame()
        assertEquals(320, before.width)
        assertEquals(240, before.height)

        window.setSize(Vec2i(200, 300))
        val after = captureFrame()
        assertEquals(200, after.width)
        assertEquals(300, after.height)
        assertEquals(Vec2i(200, 300), window.size)
        assertMatchesReference("basic/resized-cube", after)
    }

    private fun assertChannel(expected: Float, actual: Int, channel: String) {
        assertTrue(abs(expected * 255f - actual) <= 1f, "Unexpected $channel channel value: $actual, expected: ${expected * 255f}")
    }

    private fun checkerboard(size: Int, cells: Int, colorA: Color, colorB: Color): BufferedImageData2d {
        val data = Uint8Buffer(size * size * 4)
        val cellSize = size / cells
        for (y in 0 until size) {
            for (x in 0 until size) {
                val c = if ((x / cellSize + y / cellSize) % 2 == 0) colorA else colorB
                val o = (y * size + x) * 4
                data[o] = (c.r * 255f).roundToInt().toUByte()
                data[o + 1] = (c.g * 255f).roundToInt().toUByte()
                data[o + 2] = (c.b * 255f).roundToInt().toUByte()
                data[o + 3] = 255u
            }
        }
        return BufferedImageData2d(data, size, size, TexFormat.RGBA)
    }
}
