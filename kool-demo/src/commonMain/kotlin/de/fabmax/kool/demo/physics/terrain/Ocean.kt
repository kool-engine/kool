package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.InViewFrustumTraverser
import de.fabmax.kool.math.spatial.ItemAdapter
import de.fabmax.kool.math.spatial.KdTree
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.profiled

class Ocean(terrainTiles: TerrainTiles, val camera: Camera, val wind: Wind, val sky: Sky) {

    private val oceanInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
    private val tileCenters: KdTree<OceanTilePose>
    private val visibleTileTraverser = InViewFrustumTraverser<OceanTilePose>()
    private var updateFrameIdx = 0

    var oceanShader: WindAffectedShader? = null
        set(value) {
            field = value
            oceanMesh.shader = value?.shader
        }

    val oceanMesh = Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS), instances = oceanInstances, name = "ocean-mesh").apply {
        isFrustumChecked = false
        isCastingShadow = false
        generate {
            grid {
                sizeX = TILE_SIZE
                sizeY = TILE_SIZE
                stepsX = 32
                stepsY = 32
            }
        }

        onUpdate += {
            oceanShader?.let {
                val offStr = MutableVec4f(wind.offsetStrength).mul(0.4f)
                offStr.w = wind.offsetStrength.w * 4f

                it.windScale = 1f / (wind.scale * 1.5f)
                it.windOffsetStrength = offStr
                it.updateEnvMaps(sky.weightedEnvs)
            }
        }
    }

    init {
        tileCenters = KdTree(generateTilePoses(terrainTiles), TileAdapter)
        camera.onCameraUpdated += {
            profiled("update ocean tiles") {
                if (Time.frameCount != updateFrameIdx) {
                    updateFrameIdx = Time.frameCount
                    visibleTileTraverser.setup(camera).traverse(tileCenters)
                    oceanInstances.clear()
                    oceanInstances.addInstances(visibleTileTraverser.result.size) { buf ->
                        for (i in visibleTileTraverser.result.indices) {
                            visibleTileTraverser.result[i].transform.putTo(buf)
                        }
                    }
                }
            }
        }
    }

    private fun generateTilePoses(terrainTiles: TerrainTiles): List<OceanTilePose> {
        // generate tiles of increasing size at farther distance
        val tilePoses = mutableListOf<OceanTilePose>()
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                // center 8x8 tiles at scale 1
                if (terrainTiles.getMinElevation(x, y) < OCEAN_FLOOR_HEIGHT_THRESH) {
                    tilePoses += OceanTilePose(Vec3f((x - 3.5f) * TILE_SIZE, 0f, (y - 3.5f) * TILE_SIZE), 1f)
                }

                // outer tiles at scales 2, 4, 8, 16, 32
                if (x !in 2..5 || y !in 2..5) {
                    for (i in 1..5) {
                        val scale = (1 shl i).toFloat()
                        tilePoses += OceanTilePose(Vec3f((x - 3.5f) * TILE_SIZE * scale, 0f, (y - 3.5f) * TILE_SIZE * scale), scale)
                    }
                }
            }
        }
        return tilePoses
    }

    private class OceanTilePose(center: Vec3f, val scale: Float): Vec3f(center) {
        val transform = MutableMat4f().translate(center).scale(scale)
    }

    private object TileAdapter : ItemAdapter<OceanTilePose> {
        override fun getMinX(item: OceanTilePose): Double = item.x - item.scale * TILE_SIZE * 0.5
        override fun getMinY(item: OceanTilePose): Double = item.y - item.scale * TILE_SIZE * 0.5
        override fun getMinZ(item: OceanTilePose): Double = item.z - item.scale * TILE_SIZE * 0.5

        override fun getMaxX(item: OceanTilePose): Double = item.x + item.scale * TILE_SIZE * 0.5
        override fun getMaxY(item: OceanTilePose): Double = item.y + item.scale * TILE_SIZE * 0.5
        override fun getMaxZ(item: OceanTilePose): Double = item.z + item.scale * TILE_SIZE * 0.5

        override fun getCenterX(item: OceanTilePose): Double = item.x.toDouble()
        override fun getCenterY(item: OceanTilePose): Double = item.y.toDouble()
        override fun getCenterZ(item: OceanTilePose): Double = item.z.toDouble()

        override fun getSzX(item: OceanTilePose): Double = item.scale * TILE_SIZE.toDouble()
        override fun getSzY(item: OceanTilePose): Double = item.scale * TILE_SIZE.toDouble()
        override fun getSzZ(item: OceanTilePose): Double = item.scale * TILE_SIZE.toDouble()
    }

    companion object {
        const val TILE_SIZE = 64f
        const val OCEAN_FLOOR_HEIGHT_THRESH = 3f
    }
}