package de.fabmax.kool.scene

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LineString
import kotlin.math.max
import kotlin.math.min

/**
 * @author fabmax
 */

fun Node.addLineMesh(name: String? = null, block: LineMesh.() -> Unit): LineMesh {
    val lineMesh = LineMesh(name = name).apply(block)
    addNode(lineMesh)
    return lineMesh
}

fun Node.addWireframeMesh(triMesh: IndexedVertexList, lineColor: Color? = null) = addLineMesh {
    addWireframe(triMesh, lineColor)
}

fun Node.addNormalMesh(geometry: IndexedVertexList, lineColor: Color? = null, len: Float = 1f) = addLineMesh {
    addNormals(geometry, lineColor, len)
}

@Deprecated("to be replaced by addLineMesh()", ReplaceWith("addLineMesh(name) { block() }"))
fun Node.lineMesh(name: String? = null, block: LineMesh.() -> Unit) = addLineMesh(name, block)

@Deprecated("to be replaced by addWireframeMesh()", ReplaceWith("addWireframeMesh(triMesh, lineColor)"))
fun Node.wireframeMesh(triMesh: IndexedVertexList, lineColor: Color? = null) = addWireframeMesh(triMesh, lineColor)

@Deprecated("to be replaced by addNormalMesh()", ReplaceWith("addNormalMesh(geometry, lineColor, len)"))
fun Node.normalMesh(geometry: IndexedVertexList, lineColor: Color? = null, len: Float = 1f) = addNormalMesh(geometry, lineColor, len)

open class LineMesh(geometry: IndexedVertexList = IndexedVertexList(Attribute.POSITIONS, Attribute.COLORS), name: String? = null) :
        Mesh(geometry, name) {
    init {
        geometry.primitiveType = PrimitiveType.LINES
        rayTest = MeshRayTest.nopTest()
        shader = KslUnlitShader {
            color { vertexColor() }
        }
    }

    fun addLine(point0: Vec3f, point1: Vec3f, color: Color) = addLine(point0, color, point1, color)

    fun addLine(point0: Vec3f, color0: Color, point1: Vec3f, color1: Color): Int {
        var idx0 = 0
        geometry.batchUpdate {
            idx0 = addVertex(point0, null, color0, null)
            addVertex(point1, null, color1, null)
            addIndex(idx0)
            addIndex(idx0 + 1)
        }
        return idx0
    }

    fun addLineString(lineString: LineString<*>, color: Color) {
        for (i in 0 until lineString.lastIndex) {
            addLine(lineString[i], color, lineString[i+1], color)
        }
    }

    fun addWireframe(triMesh: IndexedVertexList, lineColor: Color? = null) {
        if (triMesh.primitiveType != PrimitiveType.TRIANGLES) {
            throw KoolException("Supplied mesh is not a triangle mesh: ${triMesh.primitiveType}")
        }

        val addedEdges = mutableSetOf<Long>()
        geometry.batchUpdate {
            val v = triMesh[0]
            val startI = numVertices
            for (i in 0 until triMesh.numVertices) {
                v.index = i
                geometry.addVertex {
                    position.set(v.position)
                    color.set(lineColor ?: v.color)
                }
            }
            for (i in 0 until triMesh.numIndices step 3) {
                val i1 = startI + triMesh.indices[i]
                val i2 = startI + triMesh.indices[i + 1]
                val i3 = startI + triMesh.indices[i + 2]

                val e1 = min(i1, i2).toLong() shl 32 or max(i1, i2).toLong()
                val e2 = min(i2, i3).toLong() shl 32 or max(i2, i3).toLong()
                val e3 = min(i3, i1).toLong() shl 32 or max(i3, i1).toLong()

                if (e1 !in addedEdges) {
                    geometry.addIndex(i1)
                    geometry.addIndex(i2)
                    addedEdges += e1
                }
                if (e2 !in addedEdges) {
                    geometry.addIndex(i2)
                    geometry.addIndex(i3)
                    addedEdges += e2
                }
                if (e3 !in addedEdges) {
                    geometry.addIndex(i3)
                    geometry.addIndex(i1)
                    addedEdges += e3
                }
            }
        }
    }

    fun addNormals(geometry: IndexedVertexList, lineColor: Color? = null, len: Float = 1f) {
        geometry.batchUpdate {
            val tmpN = MutableVec3f()
            geometry.forEach {
                tmpN.set(it.normal).mul(len).add(it.position)
                val color = lineColor ?: it.color
                addLine(it.position, color, tmpN, color)
            }
        }
    }

    fun clear() {
        geometry.clear()
        bounds.clear()
    }

    fun addBoundingBox(aabb: BoundingBox, color: Color) {
        geometry.batchUpdate {
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
}
