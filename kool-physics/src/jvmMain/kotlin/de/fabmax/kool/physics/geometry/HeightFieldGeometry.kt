package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.createPxMeshGeometryFlags
import org.lwjgl.system.MemoryStack
import physx.geomutils.PxHeightFieldGeometry

actual class HeightFieldGeometry actual constructor(heightField: HeightField) : CommonHeightFieldGeometry(heightField) {

    override val pxGeometry: PxHeightFieldGeometry

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val flags = mem.createPxMeshGeometryFlags(0)
            pxGeometry = PxHeightFieldGeometry(heightField.pxHeightField, flags, heightField.heightScale, heightField.rowScale, heightField.columnScale)
        }

        if (heightField.releaseWithGeometry) {
            if (heightField.refCnt > 0) {
                // PxHeightField starts with a ref count of 1, only increment it if this is not the first
                // geometry which uses it
                heightField.pxHeightField.acquireReference()
            }
            heightField.refCnt++
        }
    }

}