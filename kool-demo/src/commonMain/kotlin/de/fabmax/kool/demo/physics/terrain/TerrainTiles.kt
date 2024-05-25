package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.physics.geometry.HeightFieldGeometry
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.ShadowMap

class TerrainTiles(val terrain: Terrain, val sky: Sky) : Node() {

    private val meshes = mutableMapOf<Vec2i, Mesh>()

    init {
        isFrustumChecked = false
        for (y in 0 until TILE_CNT_XY) {
            for (x in 0 until TILE_CNT_XY) {
                addMesh(
                    Attribute.POSITIONS,
                    Attribute.NORMALS,
                    Attribute.TEXTURE_COORDS,
                    Attribute.TANGENTS,
                    name = "terrain-tile[$x,$y]"
                ) {
                    meshes[Vec2i(x, y)] = this
                    generate {
                        vertexModFun = {
                            texCoord *= Terrain.TEXTURE_SCALE
                        }
                        withTransform {
                            transform.set(terrain.terrainTransform)
                            (terrain.terrainBody.shapes[0].geometry as HeightFieldGeometry)
                                .generateTiledMesh(this, x, y, TILE_CNT_XY, TILE_CNT_XY)
                        }
                        geometry.generateTangents()
                    }
                }
            }
        }

        // fit normals of adjacent meshes
        for (i in 0 until TILE_CNT_XY) {
            for (j in 0 until (TILE_CNT_XY - 1)) {
                fitNormalsX(meshes[Vec2i(j, i)]!!, meshes[Vec2i(j + 1, i)]!!, TILE_CNT_XY)
                fitNormalsY(meshes[Vec2i(i, j)]!!, meshes[Vec2i(i, j + 1)]!!, TILE_CNT_XY)
            }
        }

        onUpdate += {
            meshes.values.first().let { m ->
                with(TerrainDemo) { (m.shader as KslLitShader).updateSky(sky.weightedEnvs) }
            }
        }
    }

    fun makeTerrainShaders(
        colorMap: Texture2d,
        normalMap: Texture2d,
        splatMap: Texture2d,
        shadowMap: ShadowMap,
        ssaoMap: Texture2d,
        isPbr: Boolean
    ) {
        val shader = Terrain.makeTerrainShader(colorMap, normalMap, splatMap, shadowMap, ssaoMap, isPbr)
        meshes.values.forEach {
            it.shader = shader
        }
    }

    private fun fitNormalsX(left: Mesh, right: Mesh, gridSz: Int) {
        val meshSz = terrain.heightMap.columns / gridSz + 1
        for (y in 0 until meshSz) {
            val ap = left.geometry[(y + 1) * meshSz - 1]
            val bp = right.geometry[y * meshSz]

            val n = MutableVec3f(ap.normal).add(bp.normal).norm()
            ap.normal.set(n)
            bp.normal.set(n)
        }
    }

    private fun fitNormalsY(left: Mesh, right: Mesh, gridSz: Int) {
        val meshSz = terrain.heightMap.columns / gridSz + 1
        for (y in 0 until meshSz) {
            val ap = left.geometry[right.geometry.numVertices - meshSz + y]
            val bp = right.geometry[y]

            val n = MutableVec3f(ap.normal).add(bp.normal).norm()
            ap.normal.set(n)
            bp.normal.set(n)
        }
    }

    fun getTile(tileX: Int, tileY: Int): Mesh {
        return meshes[Vec2i(tileX, TILE_CNT_XY - 1 - tileY)]!!
    }

    fun getMinElevation(tileX: Int, tileY: Int): Float {
        return meshes[Vec2i(tileX, TILE_CNT_XY - 1 - tileY)]?.bounds?.min?.y ?: -100f
    }

    companion object {
        const val TILE_CNT_XY = 8
    }
}