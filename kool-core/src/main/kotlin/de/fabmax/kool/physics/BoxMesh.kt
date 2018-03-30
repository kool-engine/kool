package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LineMesh


class BoxMesh(val world: CollisionWorld) : LineMesh() {

    private val boxMeshIdcs = mutableMapOf<RigidBody, Int>()
    private val vert = meshData[0]

    init {
        meshData.rebuildBoundsOnSync = true
    }

    fun updateBoxes() {
        if (boxMeshIdcs.size != world.bodies.size) {
            for (i in world.bodies.indices) {
                if (!boxMeshIdcs.containsKey(world.bodies[i])) {
                    boxMeshIdcs[world.bodies[i]] = addBoxVerts()
                }
            }
        }
        boxMeshIdcs.forEach { (body, idx) ->
            updateBoxVerts(body, idx)
        }
    }

    private fun updateBoxVerts(body: RigidBody, idx: Int) {
        val color = if (body.isInCollision) {
            Color.MD_RED
        } else {
            Color.MD_GREEN
        }
        for (i in 0..7) {
            vert.index = idx + i
            vert.color.set(color)
            body.worldTransform.transform(vert.position.set(body.shape.halfExtents).mul(SIGNS[i]))
        }
        meshData.isSyncRequired = true
    }

    private fun addBoxVerts(): Int {
        var startIdx = 0
        meshData.batchUpdate {
            startIdx = addVertex { }
            for (i in 1..7) {
                addVertex { }
            }
            for (i in 0..3) {
                addIndices(startIdx + i, startIdx + (i + 1) % 4)
                addIndices(startIdx + i + 4, startIdx + (i + 1) % 4 + 4)
                addIndices(startIdx + i, startIdx + i + 4)
            }
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
