package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.smoothStep
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.BlinnPhongMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.TexCoordAttributeBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

object TerrainMesh {

    val TERRAIN_GRID_COORDS = Attribute("aGridCoords", GlslType.VEC_2F)

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

    fun generateSplatMap(heightMap: HeightMap, sampleStep: Int): Texture2d {
        val width = heightMap.width / sampleStep
        val height = heightMap.height / sampleStep
        val data = createUint8Buffer(width * height * 4)

        // water floor weight - height based
        val wWater = SplatWeightFunc(-1f, 0f, 1.5f, 2.5f)
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
                val sy = (heightMap.height - 1 - (y + 0.5f) * sampleStep).roundToInt()
                val h = heightMap.getHeight(sx, sy)

                val sr = 0.75f
                val ha = heightMap.getHeightLinear(sx.toFloat() - sr, sy.toFloat() - sr) / sr
                val hb = heightMap.getHeightLinear(sx.toFloat() - sr, sy.toFloat() + sr) / sr
                val hc = heightMap.getHeightLinear(sx.toFloat() + sr, sy.toFloat() - sr) / sr
                val hd = heightMap.getHeightLinear(sx.toFloat() + sr, sy.toFloat() + sr) / sr
                val slope = maxOf(ha, hb, hc, hd) - minOf(ha, hb, hc, hd)

                val ws = wRockSlope.weight(slope)
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
        data.flip()

        return Texture2d(loader = BufferedTextureLoader(TextureData2d(data, width, height, TexFormat.RGBA)))
    }

    fun generateTerrainMesh(terrainShape: Shape, terrainTransform: Mat4f) =
        mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, TERRAIN_GRID_COORDS, Attribute.TANGENTS)) {
            generate {
                vertexModFun = {
                    getVec2fAttribute(TERRAIN_GRID_COORDS)?.set(texCoord.x * 64f, texCoord.y * 64f)
                }
                withTransform {
                    transform.set(terrainTransform).mul(terrainShape.localPose)
                    terrainShape.geometry.generateMesh(this)
                }
                geometry.generateTangents()
            }
        }

    fun makeTerrainShader(colorMap: Texture2d, normalMap: Texture2d, splatMap: Texture2d, shadowMap: ShadowMap, ibl: EnvironmentMaps) =
        blinnPhongShader {
            color { addTextureColorLinearize(colorMap, coordAttribute = TERRAIN_GRID_COORDS) }
            normalMapping { setNormalMap(normalMap, coordAttribute = TERRAIN_GRID_COORDS) }
            shadow { addShadowMap(shadowMap) }
            imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
            specularStrength = 0.25f
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR

            // customize blinn-phong shader to consider the splat map:
            // splat map is sampled and rgba channels are multiplied with some hard coded colors (we could as well use
            // more textures here)
            modelCustomizer = {
                //dumpCode = true

                fragmentStage {
                    main {
                        val texCoordBlock = vertexStage.findBlock<TexCoordAttributeBlock>()!!
                        val splatCoords = texCoordBlock.getAttributeCoords(Attribute.TEXTURE_COORDS)

                        val material = findBlock<BlinnPhongMaterialBlock>()!!
                        val baseColor = material.inFragmentColor.input!!

                        val splatMapSampler = texture2d("tSplatMap")
                        val splatWeights = float4Var(sampleTexture(splatMapSampler, splatCoords))

                        val waterColor = MdColor.CYAN.toLinear().const
                        val beachColor = (MdColor.AMBER toneLin 300).const
                        val grassColor = (MdColor.LIGHT_GREEN toneLin 600).const
                        val rockColor = (MdColor.GREY toneLin 500).const * (1f.const - material.inFragmentPos.y / 150f.const) * material.inNormal.y

                        val terrainColor = float4Var(splatWeights.r * beachColor +
                                splatWeights.g * grassColor +
                                splatWeights.b * waterColor +
                                splatWeights.a * rockColor)

                        val specularStrength = floatVar(splatWeights.r * 0.3f.const +
                                splatWeights.g * 0.4f.const +
                                splatWeights.b * 1.0f.const +
                                splatWeights.a * 0.2f.const)

                        material.inFragmentColor(baseColor * terrainColor.rgb)
                        material.inSpecularStrength(specularStrength)
                    }
                }
            }

        }.apply {
            // do not forget to assign the splat map to the corresponding sampler after the shader is created
            onPipelineCreated += { _, _, _ -> texSamplers2d["tSplatMap"]?.texture = splatMap }
        }
}