package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.gl.GL_ALWAYS
import de.fabmax.kool.gl.GL_LINES
import de.fabmax.kool.gl.GL_TRIANGLES
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader

/**
 * @author fabmax
 */

fun lineMesh(name: String? = null, block: LineMesh.() -> Unit): LineMesh {
    return LineMesh(name = name).apply(block)
}

fun wireframeMesh(triMesh: MeshData, lineColor: Color? = null): LineMesh {
    val lines = LineMesh()
    lines.addWireframe(triMesh, lineColor)
    return lines
}

fun normalMesh(meshData: MeshData, lineColor: Color? = null, len: Float = 1f): LineMesh {
    val lines = LineMesh()
    lines.addNormals(meshData, lineColor, len)
    return lines
}

open class LineMesh(data: MeshData = MeshData(Attribute.POSITIONS, Attribute.COLORS), name: String? = null) :
        Mesh(data, name) {
    init {
        data.primitiveType = GL_LINES
        shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    var isXray = false
    var lineWidth = 1f

    fun addLine(point0: Vec3f, color0: Color, point1: Vec3f, color1: Color): Int {
        var idx0 = 0
        meshData.batchUpdate {
            idx0 = addVertex(point0, null, color0, null)
            addVertex(point1, null, color1, null)
            addIndex(idx0)
            addIndex(idx0 + 1)
        }
        return idx0
    }

    fun addWireframe(triMesh: MeshData, lineColor: Color? = null) {
        if (triMesh.primitiveType != GL_TRIANGLES) {
            throw KoolException("Supplied mesh is not a triangle mesh: ${triMesh.primitiveType}")
        }

        meshData.batchUpdate {
            val v = triMesh[0]
            for (i in 0 until triMesh.numVertices) {
                v.index = i
                meshData.addVertex {
                    position.set(v.position)
                    color.set(lineColor ?: v.color)
                }
            }
            for (i in 0 until triMesh.numIndices step 3) {
                val i1 = triMesh.vertexList.indices[i]
                val i2 = triMesh.vertexList.indices[i + 1]
                val i3 = triMesh.vertexList.indices[i + 2]
                meshData.addIndices(i1, i2, i2, i3, i3, i1)
            }
        }
    }

    fun addNormals(meshData: MeshData, lineColor: Color? = null, len: Float = 1f) {
        meshData.batchUpdate {
            val tmpN = MutableVec3f()
            meshData.vertexList.foreach {
                tmpN.set(it.normal).scale(len).add(it.position)
                val color = lineColor ?: it.color
                addLine(it.position, color, tmpN, color)
            }
        }
    }

    fun clear() {
        meshData.clear()
    }

    fun addBoundingBox(aabb: BoundingBox, color: Color) {
        meshData.batchUpdate {
            val i0 = addVertex {
                this.position.set(aabb.min.x, aabb.min.y, aabb.min.z)
                this.color.set(color)
            }
            val i1 = addVertex {
                this.position.set(aabb.min.x, aabb.min.y, aabb.max.z)
                this.color.set(color)
            }
            val i2 = addVertex {
                this.position.set(aabb.min.x, aabb.max.y, aabb.max.z)
                this.color.set(color)
            }
            val i3 = addVertex {
                this.position.set(aabb.min.x, aabb.max.y, aabb.min.z)
                this.color.set(color)
            }
            val i4 = addVertex {
                this.position.set(aabb.max.x, aabb.min.y, aabb.min.z)
                this.color.set(color)
            }
            val i5 = addVertex {
                this.position.set(aabb.max.x, aabb.min.y, aabb.max.z)
                this.color.set(color)
            }
            val i6 = addVertex {
                this.position.set(aabb.max.x, aabb.max.y, aabb.max.z)
                this.color.set(color)
            }
            val i7 = addVertex {
                this.position.set(aabb.max.x, aabb.max.y, aabb.min.z)
                this.color.set(color)
            }
            addIndices(i0, i1, i1, i2, i2, i3, i3, i0,
                    i4, i5, i5, i6, i6, i7, i7, i4,
                    i0, i4, i1, i5, i2, i6, i3, i7)
        }
    }

    override fun render(ctx: KoolContext) {
        ctx.pushAttributes()
        ctx.lineWidth = lineWidth
        if (isXray) {
            ctx.depthFunc = GL_ALWAYS
        }
        ctx.applyAttributes()

        super.render(ctx)

        ctx.popAttributes()
    }
}
