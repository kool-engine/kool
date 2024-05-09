package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.MemoryStack
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.PxTopLevelFunctions
import de.fabmax.kool.util.Heightmap
import physx.PxArray_PxHeightFieldSample
import physx.PxHeightField
import physx.PxHeightFieldFormatEnum
import physx.PxHeightFieldGeometry
import kotlin.math.max
import kotlin.math.roundToInt

actual fun HeightField(heightMap: Heightmap, rowScale: Float, columnScale: Float): HeightField {
    return HeightFieldImpl(heightMap, rowScale, columnScale)
}

val HeightField.pxHeightField: PxHeightField get() = (this as HeightFieldImpl).pxHeightField

class HeightFieldImpl(
    override val heightMap: Heightmap,
    override val rowScale: Float,
    override val columnScale: Float
) : HeightField() {

    val pxHeightField: PxHeightField
    override val heightScale: Float

    override var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        val maxAbsHeight = max(-heightMap.minHeight, heightMap.maxHeight)
        heightScale = maxAbsHeight / 32767f
        val revHeightToI16 = if (heightScale > 0) 1f / heightScale else 0f

        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val rows = heightMap.columns
            val cols = heightMap.rows
            val sample = mem.createPxHeightFieldSample()
            val samples = PxArray_PxHeightFieldSample()
            for (row in 0..rows) {
                for (col in (cols-1) downTo 0) {
                    sample.height = (heightMap.getHeight(row, col) * revHeightToI16).roundToInt().toShort()
                    if (row % 2 != col % 2) {
                        sample.clearTessFlag()
                    } else {
                        sample.setTessFlag()
                    }
                    samples.pushBack(sample)
                }
            }

            val desc = mem.createPxHeightFieldDesc()
            desc.format = PxHeightFieldFormatEnum.eS16_TM
            desc.nbRows = rows
            desc.nbColumns = cols
            desc.samples.data = samples.begin()
            desc.samples.stride = 4 //PxHeightFieldSample.SIZEOF

            pxHeightField = PxTopLevelFunctions.CreateHeightField(desc)
        }
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        super.release()
        pxHeightField.release()
    }
}

class HeightFieldGeometryImpl(override val heightField: HeightField) : CollisionGeometryImpl(), HeightFieldGeometry {
    override val pxGeometry: PxHeightFieldGeometry

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val flags = mem.createPxMeshGeometryFlags(0)
            pxGeometry = PxHeightFieldGeometry(heightField.pxHeightField, flags, heightField.heightScale, heightField.rowScale, heightField.columnScale)
        }

        if (heightField.releaseWithGeometry) {
            heightField as HeightFieldImpl
            if (heightField.refCnt > 0) {
                // PxHeightField starts with a ref count of 1, only increment it if this is not the first
                // geometry which uses it
                heightField.pxHeightField.acquireReference()
            }
            heightField.refCnt++
        }
    }
}
