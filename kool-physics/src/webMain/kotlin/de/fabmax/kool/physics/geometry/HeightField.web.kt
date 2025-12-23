package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.*
import de.fabmax.kool.util.Heightmap
import de.fabmax.kool.util.scopedMem
import physx.*
import physx.prototypes.PxTopLevelFunctions
import kotlin.math.max
import kotlin.math.roundToInt

// GENERATED CODE BELOW:
// Transformed from desktop source

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
        scopedMem {
            val rows = heightMap.columns
            val cols = heightMap.rows
            val sample = createPxHeightFieldSample()
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

            val desc = createPxHeightFieldDesc()
            desc.formatEnum = PxHeightFieldFormatEnum.eS16_TM
            desc.nbRows = rows
            desc.nbColumns = cols
            desc.samples.data = samples.begin()
            desc.samples.stride = SIZEOF.PxHeightFieldSample

            pxHeightField = PxTopLevelFunctions.CreateHeightField(desc)
        }
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun doRelease() {
        pxHeightField.release()
    }
}

class HeightFieldGeometryImpl(override val heightField: HeightField) : CollisionGeometryImpl(), HeightFieldGeometry {
    override val pxGeometry: PxHeightFieldGeometry

    init {
        PhysicsImpl.checkIsLoaded()
        scopedMem {
            val flags = createPxMeshGeometryFlags(0)
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