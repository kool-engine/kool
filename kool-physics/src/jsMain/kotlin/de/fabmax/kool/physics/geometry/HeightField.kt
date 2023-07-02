package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.MemoryStack
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PxTopLevelFunctions
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.util.HeightMap
import physx.PxHeightField
import physx.PxHeightFieldFormatEnum
import physx.Vector_PxHeightFieldSample
import kotlin.math.max
import kotlin.math.roundToInt

actual class HeightField actual constructor(actual val heightMap: HeightMap, actual val rowScale: Float, actual val columnScale: Float) : Releasable {

    val pxHeightField: PxHeightField
    val heightScale: Float

    actual var releaseWithGeometry = true
    internal var refCnt = 0

    init {
        val maxAbsHeight = max(-heightMap.minHeight, heightMap.maxHeight)
        heightScale = maxAbsHeight / 32767f
        val revHeightToI16 = if (heightScale > 0) 1f / heightScale else 0f

        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val rows = heightMap.width
            val cols = heightMap.height
            val sample = mem.createPxHeightFieldSample()
            val samples = Vector_PxHeightFieldSample()
            for (row in 0..rows) {
                for (col in (cols-1) downTo 0) {
                    sample.height = (heightMap.getHeight(row, col) * revHeightToI16).roundToInt().toShort()
                    if (row % 2 != col % 2) {
                        sample.clearTessFlag()
                    } else {
                        sample.setTessFlag()
                    }
                    samples.push_back(sample)
                }
            }

            val desc = mem.createPxHeightFieldDesc()
            desc.format = PxHeightFieldFormatEnum.eS16_TM
            desc.nbRows = rows
            desc.nbColumns = cols
            desc.samples.data = samples.data()
            desc.samples.stride = 4 //PxHeightFieldSample.SIZEOF

            pxHeightField = PxTopLevelFunctions.CreateHeightField(desc)
        }
    }

    /**
     * Only use this if [releaseWithGeometry] is false. Releases the underlying PhysX mesh.
     */
    override fun release() {
        pxHeightField.release()
    }
}