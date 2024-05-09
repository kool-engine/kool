package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder

expect fun HeightFieldGeometry(heightField: HeightField): HeightFieldGeometry

interface HeightFieldGeometry : CollisionGeometry {
    val heightField: HeightField

    fun generateTiledMesh(target: MeshBuilder, gridX: Int, gridY: Int, gridSizeX: Int, gridSizeY: Int) {
        target.apply {
            withTransform {
                val stepsX = heightField.heightMap.columns / gridSizeX
                val stepsY = heightField.heightMap.rows / gridSizeY
                val szX = stepsX * heightField.rowScale
                val szY = stepsY * heightField.columnScale

                val tx = gridX * stepsX * heightField.rowScale + szX * 0.5f
                val tz = (heightField.heightMap.rows - 1) * heightField.columnScale - ((gridY + 0.5f) * stepsY * heightField.columnScale)
                translate(tx, 0f, tz)

                grid {
                    sizeX = szX
                    sizeY = szY

                    this.stepsX = stepsX
                    this.stepsY = stepsY
                    val tst = 0f/512f
                    texCoordScale.set(1f / gridSizeX + tst, 1f / gridSizeY + tst)
                    texCoordOffset.set(gridX.toFloat() / gridSizeX, (gridSizeY - 1f - gridY) / gridSizeY)
                    heightFun = { hx, hy ->
                        heightField.heightMap.getHeight(hx + gridX * stepsX, hy + gridY * stepsY)
                    }
                }
            }
        }
    }

    override fun generateMesh(target: MeshBuilder) {
        target.apply {
            withTransform {
                val szX = (heightField.heightMap.columns - 1) * heightField.rowScale
                val szY = (heightField.heightMap.rows - 1) * heightField.columnScale
                translate(szX * 0.5f, 0f, szY * 0.5f)
                grid {
                    sizeX = szX
                    sizeY = szY
                    useHeightMap(heightField.heightMap)
                }
            }
        }
    }

    override fun getBounds(result: BoundingBoxF): BoundingBoxF {
        val rowScale = heightField.rowScale
        val columnScale = heightField.columnScale
        val map = heightField.heightMap
        return result.set(0f, map.minHeight, 0f,
            map.columns * rowScale, map.maxHeight, map.rows * columnScale)
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // height field does not have a meaningful inertia (it should always be statics)
        return result.set(1f, 1f, 1f)
    }
}
