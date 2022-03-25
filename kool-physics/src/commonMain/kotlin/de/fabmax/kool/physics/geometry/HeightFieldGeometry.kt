package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.scene.geometry.MeshBuilder

expect class HeightFieldGeometry(heightField: HeightField) : CommonHeightFieldGeometry

abstract class CommonHeightFieldGeometry(val heightField: HeightField) : CollisionGeometry {
    override fun generateMesh(target: MeshBuilder) {
        target.apply {
            withTransform {
                val szX = (heightField.heightMap.width - 1) * heightField.rowScale
                val szY = (heightField.heightMap.height - 1) * heightField.columnScale
                translate(szX * 0.5f, 0f, szY * 0.5f)
                grid {
                    sizeX = szX
                    sizeY = szY
                    useHeightMap(heightField.heightMap)
                }
            }
        }
    }

    override fun getBounds(result: BoundingBox): BoundingBox {
        val rowScale = heightField.rowScale
        val columnScale = heightField.columnScale
        val map = heightField.heightMap
        return result.set(0f, map.minHeight, 0f,
            map.width * rowScale, map.maxHeight, map.height * columnScale)
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // height field does not have a meaningful inertia (it should always be statics)
        return result.set(1f, 1f, 1f)
    }
}
