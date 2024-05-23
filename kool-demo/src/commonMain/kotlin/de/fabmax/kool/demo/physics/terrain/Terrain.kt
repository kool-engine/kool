package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.BlinnPhongMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.PbrMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.TexCoordAttributeBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.HeightField
import de.fabmax.kool.physics.geometry.HeightFieldGeometry
import de.fabmax.kool.pipeline.BufferedTextureLoader
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

class Terrain(val demo: TerrainDemo, val heightMap: Heightmap) {

    val splatMapData: TextureData2d = generateSplatMap(2)
    val splatMap = Texture2d(name = "terrain-splat", loader = BufferedTextureLoader(splatMapData)).also { it.releaseWith(demo.mainScene) }
    val terrainBody: RigidStatic

    val terrainTransform = MutableMat4f()
    private val terrainTransformInv = MutableMat4f()

    init {
        val heightField = HeightField(heightMap, 1f, 1f)
        val hfGeom = HeightFieldGeometry(heightField)
        val hfBounds = hfGeom.getBounds(BoundingBoxF())
        terrainBody = RigidStatic()
        terrainBody.attachShape(Shape(hfGeom, Physics.defaultMaterial))
        terrainBody.position = Vec3f(hfBounds.size.x * -0.5f, 0f, hfBounds.size.z * -0.5f)

        terrainTransform.set(terrainBody.transform.matrixF).mul(terrainBody.shapes[0].localPose)
        terrainTransformInv.set(terrainTransform).invert()
    }

    fun getSplatWeightsAt(x: Float, z: Float): Vec4f {
        val pos = terrainTransformInv.transform(MutableVec3f(x, 0f, z))
        val ix = ((pos.x - 1f) * 0.5f).roundToInt().clamp(0, splatMapData.width - 1)
        val iy = ((pos.z - 1f) * 0.5f).roundToInt().clamp(0, splatMapData.height - 1)

        val u8Buffer = splatMapData.data as Uint8Buffer
        val r = (u8Buffer[(iy * splatMapData.width + ix) * 4 + 0].toInt() and 0xff) / 255f
        val g = (u8Buffer[(iy * splatMapData.width + ix) * 4 + 1].toInt() and 0xff) / 255f
        val b = (u8Buffer[(iy * splatMapData.width + ix) * 4 + 2].toInt() and 0xff) / 255f
        val a = (u8Buffer[(iy * splatMapData.width + ix) * 4 + 3].toInt() and 0xff) / 255f
        return Vec4f(r, g, b, a)
    }

    fun getTerrainHeightAt(x: Float, z: Float): Float {
        val pos = terrainTransformInv.transform(MutableVec3f(x, 0f, -z - 1))
        return heightMap.getHeightLinear(pos.x, pos.z)
    }

    private fun generateSplatMap(sampleStep: Int): TextureData2d {
        val width = heightMap.columns / sampleStep
        val height = heightMap.rows / sampleStep
        val data = Uint8Buffer(width * height * 4)

        // water floor weight - height based
        val wWater = SplatWeightFunc(-50f, -49f, 1.5f, 2.5f)
        // beach weight - height based
        val wBeach = SplatWeightFunc(1.5f, 2.5f, 5f, 8f)
        // grass weight - height based
        val wGrass = SplatWeightFunc(5f, 8f, 50f, 60f)
        // rock weight - height based
        val wRockHeight = SplatWeightFunc(50f, 60f, 500f, 501f)
        // rock weight - slope based
        val wRockSlope = SplatWeightFunc(0.75f, 2f, 500f, 501f)

        // generate splat map based on terrain height and slope
        for (y in 0 until height) {
            for (x in 0 until width) {
                val sx = ((x + 0.5f) * sampleStep).roundToInt()
                val sy = (heightMap.rows - 1 - (y + 0.5f) * sampleStep).roundToInt()
                val h = heightMap.getHeight(sx, sy)

                val sr = 0.75f
                val ha = heightMap.getHeightLinear(sx.toFloat() - sr, sy.toFloat() - sr) / sr
                val hb = heightMap.getHeightLinear(sx.toFloat() - sr, sy.toFloat() + sr) / sr
                val hc = heightMap.getHeightLinear(sx.toFloat() + sr, sy.toFloat() - sr) / sr
                val hd = heightMap.getHeightLinear(sx.toFloat() + sr, sy.toFloat() + sr) / sr
                val slope = maxOf(ha, hb, hc, hd) - minOf(ha, hb, hc, hd)
                val ws = wRockSlope.weight(slope)
                //val ws = if (h > 0f) wRockSlope.weight(slope) else 0f

                val r = wBeach.weight(h) * (1f - ws)
                val g = wGrass.weight(h) * (1f - ws)
                val b = wWater.weight(h) * (1f - ws)
                val a = wRockHeight.weight(h) + ws

                val wSum = 1f / (r + g + b + a)
                data.put((r * wSum * 255f).toInt().toByte())
                data.put((g * wSum * 255f).toInt().toByte())
                data.put((b * wSum * 255f).toInt().toByte())
                data.put((a * wSum * 255f).toInt().toByte())
            }
        }
        return TextureData2d(data, width, height, TexFormat.RGBA)
    }

    /**
     * Smooth weight interpolation function. Output weight will be:
     *   0     if input < c0
     *   0..1  if c0 < input < c1
     *   1     if c1 < input < c2
     *   1..0  if c2 < input < c3
     *   0     if input > c3
     */
    private class SplatWeightFunc(val c0: Float, val c1: Float, val c2: Float, val c3: Float) {
        fun weight(x: Float): Float = smoothStep(c0, c1, x) * (1f - smoothStep(c2, c3, x))
    }

    companion object {
        const val TERRAIN_SHADER_DISCARD_HEIGHT = "uDiscardHeight"
        const val TEXTURE_SCALE = 64f
        const val SPLAT_MAP_SCALE = 1f / TEXTURE_SCALE

        fun makeTerrainShader(
            colorMap: Texture2d,
            normalMap: Texture2d,
            splatMap: Texture2d,
            shadowMap: ShadowMap,
            ssaoMap: Texture2d,
            isPbr: Boolean
        ): KslShader {

            fun KslLitShader.LitShaderConfig.Builder.terrainConfig() {
                color { textureColor(colorMap) }
                normalMapping { setNormalMap(normalMap) }
                shadow { addShadowMap(shadowMap) }
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
                enableSsao(ssaoMap)
                dualImageBasedAmbientColor()

                if (this is KslPbrShader.Config.Builder) {
                    with (TerrainDemo) {
                        iblConfig()
                    }
                } else if (this is KslBlinnPhongShader.Config.Builder) {
                    //imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                    specularStrength(0.25f)
                }

                // customize to consider the splat map:
                // splat map is sampled and rgba channels are multiplied with some hard coded colors (we could as
                // well use more textures here)
                modelCustomizer = {
                    //dumpCode = true

                    fragmentStage {
                        main {
                            val texCoordBlock = vertexStage?.findBlock<TexCoordAttributeBlock>()!!
                            val splatCoords = float2Var(texCoordBlock.getTextureCoords() * SPLAT_MAP_SCALE.const)

                            val material = findBlock<PbrMaterialBlock>() ?: findBlock<BlinnPhongMaterialBlock>()!!
                            val baseColor = material.inBaseColor.input!!

                            `if`(material.inFragmentPos.y gt uniformFloat1(TERRAIN_SHADER_DISCARD_HEIGHT)) {
                                discard()
                            }

                            val splatMapSampler = texture2d("tSplatMap")
                            val splatWeights = float4Var(sampleTexture(splatMapSampler, splatCoords))

                            val wDeepWater = 1f.const - smoothStep((-30f).const, (-2f).const, material.inFragmentPos.y)
                            val wBeach = float1Var(splatWeights.r * (1f.const - wDeepWater))
                            val wGrass = float1Var(splatWeights.g * (1f.const - wDeepWater))
                            val wWater = float1Var(splatWeights.b * (1f.const - wDeepWater))
                            val wRock = float1Var(splatWeights.a * (1f.const - wDeepWater))

                            //val waterColor = MdColor.CYAN.toLinear().const
                            //val deepWaterColor = MdColor.INDIGO.toLinear().const

                            val beachColor = (MdColor.AMBER toneLin 300).const
                            val grassColor = (MdColor.BROWN toneLin 800).const
                            val rockColor = (MdColor.GREY toneLin 500).const * (1f.const - material.inFragmentPos.y / 150f.const) * material.inNormal.y

                            val waterColor = (MdColor.AMBER toneLin 500).const
                            val deepWaterColor = waterColor

                            val terrainColor = float4Var(
                                wBeach * beachColor +
                                        wGrass * grassColor +
                                        wWater * waterColor +
                                        wRock * rockColor +
                                        wDeepWater * deepWaterColor
                            )
                            material.inBaseColor(float4Value(baseColor.rgb * terrainColor.rgb, baseColor.a))

                            if (material is BlinnPhongMaterialBlock) {
                                val specularStrength = float1Var(
                                            splatWeights.r * 0.3f.const +
                                            splatWeights.g * 0.0f.const +
                                            splatWeights.b * 1.0f.const +
                                            splatWeights.a * 0.2f.const
                                )
                                material.inSpecularStrength(specularStrength)
                            } else if (material is PbrMaterialBlock) {
                                val roughness = float1Var(
                                            splatWeights.r * 0.6f.const +
                                            splatWeights.g * 0.8f.const +
                                            splatWeights.b * 0.2f.const +
                                            splatWeights.a * 0.7f.const
                                )
                                material.inRoughness(roughness)
                            }
                        }
                    }
                }
            }

            val shader = if (isPbr) {
                KslPbrShader { terrainConfig() }
            } else {
                KslBlinnPhongShader { terrainConfig() }
            }
            // do not forget to assign the splat map to the corresponding sampler
            shader.texture2d("tSplatMap", splatMap)
            shader.uniform1f(TERRAIN_SHADER_DISCARD_HEIGHT).set(1000f)
            return shader
        }
    }
}