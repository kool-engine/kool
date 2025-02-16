package de.fabmax.kool.demo.tests

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.loadTexture2dArray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.div
import de.fabmax.kool.modules.ksl.lang.rem
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.modules.ksl.lang.toFloat1
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.releaseWith

class ArrayTexturesTest : DemoScene("Array Textures Test") {

    private lateinit var pbrArrayTex: Texture2dArray

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        pbrArrayTex = loadTexture2dArray(listOf(
            "${DemoLoader.materialPath}/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.jpg",
            "${DemoLoader.materialPath}/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.jpg",
            "${DemoLoader.materialPath}/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.jpg",
            "${DemoLoader.materialPath}/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.jpg",
        )).getOrThrow().releaseWith(mainScene)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(0f, 0f)

        lighting.singleSpotLight {
            setup(Vec3f(1f, 1f, 8f), Vec3f(-1f, -1f, -8f).normed())
            setColor(Color.WHITE, 75f)
        }

        pbrTex()
        texArray2d()
        if (KoolSystem.features.cubeMapArrays) {
            texArrayCube()
        }
    }

    private fun Scene.pbrTex() {
        addTextureMesh(isNormalMapped = true) {
            generate {
                rect { size.set(5f, 5f) }
            }

            val texName = "pbr_textures"
            shader = KslPbrShader {
                color { textureColor(0, texName, pbrArrayTex) }
                normalMapping { useNormalMapFromArray(1, texName, pbrArrayTex) }
                ao { textureProperty(2, texName, pbrArrayTex) }
                roughness { textureProperty(3, texName, pbrArrayTex) }
            }
        }
    }

    private fun Scene.texArray2d() {
        val bufs = List(colors.size) { Uint8Buffer(W * H * 4) }
        colors.forEachIndexed { i, color ->
            for (y in 0 until H) {
                for (x in 0 until W) {
                    var c = if (x % 2 == y % 2) color else color.mulRgb(0.75f)
                    c = c.mulRgb((y / 4 + 1).toFloat() / (H / 4))
                    c.putTo(bufs[i])
                }
            }
        }
        val arrayData = ImageData2dArray(bufs.map { BufferedImageData2d(it, W, H, TexFormat.RGBA) })
        val arrayTex = Texture2dArray(samplerSettings = SamplerSettings().clamped()).apply { uploadLazy(arrayData) }
        arrayTex.releaseWith(this)

        addTextureMesh {
            generate {
                rect { size.set(5f, 5f) }
            }
            transform.translate(-7f, 0f, 0f)

            shader = KslShader("array-2d-test") {
                val uv = interStageFloat2()
                vertexStage {
                    main {
                        uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS)
                        outPosition set mvpMatrix().matrix * float4Value(vertexAttribFloat3(Attribute.POSITIONS), 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        val camData = cameraData()
                        val arraySampler = texture2dArray("texArray2d")
                        val arrayIdx = int1Var((camData.frameIndex / 100.const) % colors.size.const)
                        val mipLevel = int1Var((camData.frameIndex / 600.const) % 4.const)
                        val color = sampleTextureArray(arraySampler, arrayIdx, uv.output, mipLevel.toFloat1())
                        colorOutput(color)
                    }
                }
            }.also {
                it.texture2dArray("texArray2d", arrayTex)
            }
        }
    }

    private fun Scene.texArrayCube() {
        val bufs = List(6) { Uint8Buffer(W * H * 4) }
        for (y in 0 until H) {
            for (x in 0 until W) {
                colors.forEachIndexed { i, color ->
                    var c = if (x % 2 == y % 2) color else color.mulRgb(0.75f)
                    c = c.mulRgb((y / 4 + 1).toFloat() / (H / 4))
                    c.putTo(bufs[i])
                }
            }
        }

        val cubeA = ImageDataCube(
            BufferedImageData2d(bufs[2], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[3], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[5], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[4], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[0], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[1], 16, 16, TexFormat.RGBA),
        )
        val cubeB = ImageDataCube(
            BufferedImageData2d(bufs[5], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[0], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[2], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[1], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[3], 16, 16, TexFormat.RGBA),
            BufferedImageData2d(bufs[4], 16, 16, TexFormat.RGBA),
        )
        val cubeArray = ImageDataCubeArray(listOf(cubeA, cubeB))
        val cubeArrayTex = TextureCubeArray().apply { uploadLazy(cubeArray) }
        cubeArrayTex.releaseWith(this)

        addTextureMesh {
            generate {
                icoSphere {
                    steps = 3
                    radius = 2.5f
                }
            }
            transform.translate(7f, 0f, 0f)

            shader = KslShader("array-cube-test") {
                val pos = interStageFloat3()
                vertexStage {
                    main {
                        pos.input set vertexAttribFloat3(Attribute.POSITIONS)
                        outPosition set mvpMatrix().matrix * float4Value(vertexAttribFloat3(Attribute.POSITIONS), 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        val camData = cameraData()
                        val cubeArraySampler = textureCubeArray("texArrayCube")
                        val arrayIdx = int1Var((camData.frameIndex / 100.const) % 2.const)
                        val mipLevel = int1Var((camData.frameIndex / 200.const) % 4.const)
                        val color = sampleTextureArray(cubeArraySampler, arrayIdx, normalize(pos.output), mipLevel.toFloat1())
                        colorOutput(color)
                    }
                }
            }.also {
                it.textureCubeArray("texArrayCube", cubeArrayTex)
            }
        }
    }

    companion object {
        private const val W = 16
        private const val H = 16
        private val colors = listOf(MdColor.RED, MdColor.GREEN, MdColor.BLUE, MdColor.AMBER, MdColor.CYAN, MdColor.PURPLE)
    }
}