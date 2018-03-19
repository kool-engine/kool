package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LineMesh


class BoxMesh : LineMesh() {

    val boxes = mutableListOf<Box>()
    private val boxMeshIdcs = mutableMapOf<Box, Int>()
    private val vert = meshData[0]

    init {
        meshData.rebuildBoundsOnSync = true
    }

    fun addBox(box: Box, boxColor: Color = Color.MD_PINK) {
        if (!boxMeshIdcs.containsKey(box)) {
            boxes += box
            val idx = addBoxVerts(boxColor)
            boxMeshIdcs[box] = idx
            updateBoxVerts(box, idx)
        }
    }

    fun updateBoxVerts() {
        boxMeshIdcs.forEach { (box, idx) ->
            updateBoxVerts(box, idx)
        }
    }

    private fun updateBoxVerts(box: Box, idx: Int) {
        val color = if (box.isInCollision) {
            Color.MD_RED
        } else {
            Color.MD_GREEN
        }
        for (i in 0..7) {
            vert.index = idx + i
            vert.color.set(color)
            box.transform.transform(vert.position.set(box.halfExtents).mul(SIGNS[i]))
        }
        meshData.isSyncRequired = true
    }

    private fun addBoxVerts(boxColor: Color): Int {
        val startIdx = meshData.addVertex { color.set(boxColor) }
        for (i in 1..7) {
            meshData.addVertex { color.set(boxColor) }
        }
        for (i in 0..3) {
            meshData.addIndices(startIdx + i, startIdx + (i + 1) % 4)
            meshData.addIndices(startIdx + i + 4, startIdx + (i + 1) % 4 + 4)
            meshData.addIndices(startIdx + i, startIdx + i + 4)
        }
        return startIdx
    }

    companion object {
        private val SIGNS = listOf(
                Vec3f(1f, 1f, 1f),
                Vec3f(1f, 1f, -1f),
                Vec3f(1f, -1f, -1f),
                Vec3f(1f, -1f, 1f),
                Vec3f(-1f, 1f, 1f),
                Vec3f(-1f, 1f, -1f),
                Vec3f(-1f, -1f, -1f),
                Vec3f(-1f, -1f, 1f)
        )
    }
}
