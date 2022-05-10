package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.BlinnPhongMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.TexCoordAttributeBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.HeightField
import de.fabmax.kool.physics.geometry.HeightFieldGeometry
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

class Terrain(val heightMap: HeightMap) {

    val splatMapData: TextureData2d = generateSplatMap(2)
    val splatMap: Texture2d = Texture2d(loader = BufferedTextureLoader(splatMapData))
    val terrainBody: RigidStatic

    private val terrainTransform = Mat4f()
    private val terrainTransformInv = Mat4f()

    init {
        val heightField = HeightField(heightMap, 1f, 1f)
        val hfGeom = HeightFieldGeometry(heightField)
        val hfBounds = hfGeom.getBounds(BoundingBox())
        terrainBody = RigidStatic()
        terrainBody.attachShape(Shape(hfGeom, Physics.defaultMaterial))
        terrainBody.position = Vec3f(hfBounds.size.x * -0.5f, 0f, hfBounds.size.z * -0.5f)

        terrainTransform.set(terrainBody.transform).mul(terrainBody.shapes[0].localPose)
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
        return TextureData2d(data, width, height, TexFormat.RGBA)
    }

    fun generateTerrainMeshes() = Group().apply {
        isFrustumChecked = false
        val gridSz = 8
        val meshes = mutableMapOf<Vec2i, Mesh>()
        for (y in 0 until gridSz) {
            for (x in 0 until gridSz) {
                +mesh(
                    listOf(
                        Attribute.POSITIONS,
                        Attribute.NORMALS,
                        Attribute.TEXTURE_COORDS,
                        TERRAIN_GRID_COORDS,
                        Attribute.TANGENTS
                    )
                ) {
                    meshes[Vec2i(x, y)] = this
                    generate {
                        vertexModFun = {
                            getVec2fAttribute(TERRAIN_GRID_COORDS)?.set(texCoord.x * 64f, texCoord.y * 64f)
                        }
                        withTransform {
                            transform.set(terrainTransform)
                            (terrainBody.shapes[0].geometry as HeightFieldGeometry).generateTiledMesh(this, x, y, gridSz, gridSz)
                        }
                        geometry.generateTangents()
                    }
                }
            }
        }

        // fit normals of adjacent meshes
        for (i in 0 until gridSz) {
            for (j in 0 until (gridSz - 1)) {
                fitNormalsX(meshes[Vec2i(j, i)]!!, meshes[Vec2i(j + 1, i)]!!, gridSz)
                fitNormalsY(meshes[Vec2i(i, j)]!!, meshes[Vec2i(i, j + 1)]!!, gridSz)
            }
        }
    }

    private fun fitNormalsX(left: Mesh, right: Mesh, gridSz: Int) {
        val meshSz = heightMap.width / gridSz + 1
        for (y in 0 until meshSz) {
            val ap = left.geometry[(y + 1) * meshSz - 1]
            val bp = right.geometry[y * meshSz]

            val n = MutableVec3f(ap.normal).add(bp.normal).norm()
            ap.normal.set(n)
            bp.normal.set(n)
        }
    }

    private fun fitNormalsY(left: Mesh, right: Mesh, gridSz: Int) {
        val meshSz = heightMap.width / gridSz + 1
        for (y in 0 until meshSz) {
            val ap = left.geometry[right.geometry.numVertices - meshSz + y]
            val bp = right.geometry[y]

            val n = MutableVec3f(ap.normal).add(bp.normal).norm()
            ap.normal.set(n)
            bp.normal.set(n)
        }
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
        val TERRAIN_GRID_COORDS = Attribute("aGridCoords", GlslType.VEC_2F)

        fun makeTerrainShader(colorMap: Texture2d, normalMap: Texture2d, splatMap: Texture2d, shadowMap: ShadowMap, ibl: EnvironmentMaps) =
            blinnPhongShader {
                color { addTextureColor(colorMap, coordAttribute = TERRAIN_GRID_COORDS) }
                normalMapping { setNormalMap(normalMap, coordAttribute = TERRAIN_GRID_COORDS) }
                shadow { addShadowMap(shadowMap) }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                specularStrength = 0.25f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR

                // customize blinn-phong shader to consider the splat map:
                // splat map is sampled and rgba channels are multiplied with some hard coded colors (we could as
                // well use more textures here)
                modelCustomizer = {
                    //dumpCode = true

                    fragmentStage {
                        main {
                            val texCoordBlock = vertexStage.findBlock<TexCoordAttributeBlock>()!!
                            val splatCoords = texCoordBlock.getAttributeCoords(Attribute.TEXTURE_COORDS)

                            val material = findBlock<BlinnPhongMaterialBlock>()!!
                            val baseColor = material.inBaseColor.input!!

                            val splatMapSampler = texture2d("tSplatMap")
                            val splatWeights = float4Var(sampleTexture(splatMapSampler, splatCoords))

                            val waterColor = MdColor.CYAN.toLinear().const
                            val beachColor = (MdColor.AMBER toneLin 300).const
                            //val grassColor = (MdColor.LIGHT_GREEN toneLin 600).const
                            val grassColor = (MdColor.BROWN toneLin 600).const
                            val rockColor = (MdColor.GREY toneLin 500).const * (1f.const - material.inFragmentPos.y / 150f.const) * material.inNormal.y

                            val terrainColor = float4Var(
                                splatWeights.r * beachColor +
                                        splatWeights.g * grassColor +
                                        splatWeights.b * waterColor +
                                        splatWeights.a * rockColor
                            )

                            val specularStrength = floatVar(
                                splatWeights.r * 0.3f.const +
                                        splatWeights.g * 0.0f.const +
                                        splatWeights.b * 1.0f.const +
                                        splatWeights.a * 0.2f.const
                            )

                            material.inBaseColor(baseColor * terrainColor.rgb)
                            material.inSpecularStrength(specularStrength)
                        }
                    }
                }

            }.apply {
                // do not forget to assign the splat map to the corresponding sampler after the shader is created
                onPipelineCreated += { _, _, _ -> texSamplers2d["tSplatMap"]?.texture = splatMap }
            }
    }
}