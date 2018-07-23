package de.fabmax.kool.modules.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.ShadowMap


class BoxMesh(val box: RigidBody, boxColor: Color = Color.MD_GREY, sceneShadows: ShadowMap? = null) :
        Mesh(MeshData(Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS), box.name) {

    private val boundsMin = MutableVec3f()
    private val boundsMax = MutableVec3f()
    private val radius: Float = box.shape.halfExtents.length()

    init {
        MeshBuilder(meshData).apply {
            color = boxColor
            cube {
                size.set(box.shape.halfExtents).scale(2f)
                centerOrigin()
            }
        }

        shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.PHONG_LIGHTING
            shadowMap = sceneShadows
            specularIntensity = 0.4f
        }
    }

    override fun preRender(ctx: KoolContext) {
        box.worldTransform.getOrigin(boundsMin)
        boundsMax.set(boundsMin)

        boundsMin.x -= radius
        boundsMin.y -= radius
        boundsMin.z -= radius
        boundsMax.x += radius
        boundsMax.y += radius
        boundsMax.z += radius

        bounds.set(boundsMin, boundsMax)
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        ctx.mvpState.modelMatrix.push()
        ctx.mvpState.modelMatrix.mul(box.worldTransform)
        ctx.mvpState.update(ctx)

        super.render(ctx)

        ctx.mvpState.modelMatrix.pop()
        ctx.mvpState.update(ctx)
    }
}

class MultiBoxMesh(val world: CollisionWorld) : Mesh(MeshData(Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS,
        Attribute.TEXTURE_COORDS, Attribute.TANGENTS)) {

    private val boxMeshIdcs = mutableMapOf<RigidBody, Int>()
    private val vert = meshData[0]

    init {
        meshData.isRebuildBoundsOnSync = true
    }

    fun updateBoxes() {
        if (boxMeshIdcs.size != world.bodies.size) {
            for (i in world.bodies.indices) {
                if (!boxMeshIdcs.containsKey(world.bodies[i])) {
                    boxMeshIdcs[world.bodies[i]] = addBoxVerts(world.bodies[i])
                }
            }
        }
        var boxI = 0
        boxMeshIdcs.forEach { (body, idx) ->
            updateBoxVerts(body, boxI++, idx)
        }
        meshData.generateTangents()
    }

    private fun updateBoxVerts(body: RigidBody, boxIdx: Int, vertIdx: Int) {
        val color = when(boxIdx) {
            0 -> Color.MD_PURPLE
            1 -> Color.MD_PINK
            else -> Color.MD_ORANGE
        }

        // solid box
        for (i in 0..23) {
            vert.index = vertIdx + i
            vert.color.set(color)
            body.worldTransform.transform(vert.position.set(body.shape.halfExtents).mul(SOLID_SIGNS[i]))
            body.worldTransform.transform(vert.normal.set(SOLID_NORMALS[i/4]), 0f)
        }


        meshData.isSyncRequired = true
    }

    private fun addBoxVerts(rigidBody: RigidBody): Int {
        var startIdx = 0
        meshData.batchUpdate {
            // solid box
            for (i in 0..23) {
                val w = when (i / 8) {
                    0 -> rigidBody.shape.halfExtents.z * 2
                    1 -> rigidBody.shape.halfExtents.x * 2
                    else -> rigidBody.shape.halfExtents.x * 2
                }
                val h = when (i / 8) {
                    0 -> rigidBody.shape.halfExtents.y * 2
                    1 -> rigidBody.shape.halfExtents.z * 2
                    else -> rigidBody.shape.halfExtents.y * 2
                }

                val idx = addVertex {
                    when (i % 4) {
                        0 -> texCoord.set(0f, 0f)
                        1 -> texCoord.set(0f, h)
                        2 -> texCoord.set(w, h)
                        3 -> texCoord.set(w, 0f)
                    }
                }

                if (i == 0) {
                    startIdx = idx
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
                Vec3f(1f, -1f, 1f),
                Vec3f(1f, 1f, 1f),
                Vec3f(-1f, 1f, 1f),
                Vec3f(-1f, -1f, 1f),

                // back
                Vec3f(-1f, 1f, -1f),
                Vec3f(1f, 1f, -1f),
                Vec3f(1f, -1f, -1f),
                Vec3f(-1f, -1f, -1f)
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
