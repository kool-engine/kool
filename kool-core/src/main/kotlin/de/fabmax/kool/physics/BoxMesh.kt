package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.util.Color


class BoxMesh(val world: CollisionWorld) : Mesh(MeshData(Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS)) {

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
        var boxI = 0
        boxMeshIdcs.forEach { (body, idx) ->
            updateBoxVerts(body, boxI++, idx)
        }
    }

    private fun updateBoxVerts(body: RigidBody, boxIdx: Int, vertIdx: Int) {
        val color = when(boxIdx) {
            0 -> Color.MD_PURPLE
            1 -> Color.MD_PINK
            else -> Color.MD_ORANGE
        }

        // box wireframe
//        for (i in 0..7) {
//            vert.index = idx + i
//            vert.color.set(color)
//            body.worldTransform.transform(vert.position.set(body.shape.halfExtents).mul(WIREFRAME_SIGNS[i]))
//        }

        // solid box
        for (i in 0..23) {
            vert.index = vertIdx + i
            vert.color.set(color)
            body.worldTransform.transform(vert.position.set(body.shape.halfExtents).mul(SOLID_SIGNS[i]))
            body.worldTransform.transform(vert.normal.set(SOLID_NORMALS[i/4]), 0f)
        }


        meshData.isSyncRequired = true
    }

    private fun addBoxVerts(): Int {
        var startIdx = 0
        meshData.batchUpdate {
            startIdx = addVertex {
                normal.set(SOLID_NORMALS[0])
            }

            // box wireframe
//            for (i in 1..7) {
//                addVertex { }
//            }
//            for (i in 0..3) {
//                addIndices(startIdx + i, startIdx + (i + 1) % 4)
//                addIndices(startIdx + i + 4, startIdx + (i + 1) % 4 + 4)
//                addIndices(startIdx + i, startIdx + i + 4)
//            }

            // solid box
            for (i in 1..23) {
                addVertex {
                    normal.set(SOLID_NORMALS[i/4])
                }
            }
            for (i in 0..5) {
                addTriIndices(startIdx + i*4, startIdx + i*4 + 1, startIdx + i*4 + 2)
                addTriIndices(startIdx + i*4, startIdx + i*4 + 2, startIdx + i*4 + 3)
            }
        }
        return startIdx
    }

    companion object {
        private val WIREFRAME_SIGNS = listOf(
                Vec3f(1f, 1f, 1f),
                Vec3f(1f, 1f, -1f),
                Vec3f(1f, -1f, -1f),
                Vec3f(1f, -1f, 1f),
                Vec3f(-1f, 1f, 1f),
                Vec3f(-1f, 1f, -1f),
                Vec3f(-1f, -1f, -1f),
                Vec3f(-1f, -1f, 1f)
        )

        private val SOLID_SIGNS = listOf(
                // right
                Vec3f(1f, 1f, 1f),
                Vec3f(1f, -1f, 1f),
                Vec3f(1f, -1f, -1f),
                Vec3f(1f, 1f, -1f),

                // left
                Vec3f(-1f, 1f, 1f),
                Vec3f(-1f, 1f, -1f),
                Vec3f(-1f, -1f, -1f),
                Vec3f(-1f, -1f, 1f),

                // top
                Vec3f(1f, 1f, 1f),
                Vec3f(1f, 1f, -1f),
                Vec3f(-1f, 1f, -1f),
                Vec3f(-1f, 1f, 1f),

                // bottom
                Vec3f(1f, -1f, 1f),
                Vec3f(-1f, -1f, 1f),
                Vec3f(-1f, -1f, -1f),
                Vec3f(1f, -1f, -1f),

                // front
                Vec3f(1f, 1f, 1f),
                Vec3f(-1f, 1f, 1f),
                Vec3f(-1f, -1f, 1f),
                Vec3f(1f, -1f, 1f),

                // back
                Vec3f(1f, 1f, -1f),
                Vec3f(1f, -1f, -1f),
                Vec3f(-1f, -1f, -1f),
                Vec3f(-1f, 1f, -1f)
        )

        private val SOLID_NORMALS = listOf(
                Vec3f.X_AXIS,
                Vec3f.NEG_X_AXIS,
                Vec3f.Y_AXIS,
                Vec3f.NEG_Y_AXIS,
                Vec3f.Z_AXIS,
                Vec3f.NEG_Z_AXIS
        )
    }
}
