package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
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

fun Node.addLineMesh(
    name: String = makeChildName("LineMesh"),
    instances: MeshInstanceList? = null,
    block: LineMesh.() -> Unit
): LineMesh {
    val lineMesh = LineMesh(name, instances = instances).apply(block)
    addNode(lineMesh)
    return lineMesh
}

fun Node.addWireframeMesh(
    triMesh: IndexedVertexList,
    lineColor: Color? = null,
    instances: MeshInstanceList? = null
) = addLineMesh("${triMesh.name}-wireframe", instances = instances) {
    addWireframe(triMesh, lineColor)
}

fun Node.addNormalMesh(
    geometry: IndexedVertexList,
    lineColor: Color? = null,
    len: Float = 1f,
    instances: MeshInstanceList? = null
) = addLineMesh("${geometry.name}-normals", instances = instances) {
    addNormals(geometry, lineColor, len)
}

open class LineMesh(
    name: String = makeNodeName("LineMesh"),
    geometry: IndexedVertexList = IndexedVertexList(Attribute.POSITIONS, Attribute.COLORS, primitiveType = PrimitiveType.LINES),
    instances: MeshInstanceList? = null,
) : Mesh(geometry, instances = instances, name = name) {

    private val lineBuffer = mutableListOf<LineVertex>()
    var color = Color.RED

    init {
        rayTest = MeshRayTest.nopTest()
        shader = KslUnlitShader {
            color { vertexColor() }
        }
    }

    fun clear() {
        geometry.clear()
        bounds.clear()
    }

    fun addLine(from: Vec3f, to: Vec3f, color: Color = this.color) = addLine(from, color, to, color)

    fun addLine(from: Vec3f, fromColor: Color, to: Vec3f, toColor: Color): Int {
        var idx0 = 0
        geometry.batchUpdate {
            idx0 = addVertex(from, null, fromColor, null)
            addVertex(to, null, toColor, null)
            addIndex(idx0)
            addIndex(idx0 + 1)
        }
        return idx0
    }

    fun addLine(vararg points: Vec3f) {
        addLine(color, *points)
    }

    fun addLine(color: Color, vararg points: Vec3f) {
        for (i in 0 until points.lastIndex) {
            addLine(points[i], color, points[i+1], color)
        }
    }

    fun addLineString(lineString: LineString<*>, color: Color = this.color) {
        for (i in 0 until lineString.lastIndex) {
            addLine(lineString[i], color, lineString[i+1], color)
        }
    }

    fun moveTo(x: Float, y: Float, z: Float) = moveTo(Vec3f(x, y, z))

    fun moveTo(
        position: Vec3f,
        color: Color = this.color
    ): LineMesh {
        if (lineBuffer.isNotEmpty()) {
            stroke()
        }
        lineBuffer.add(LineVertex(position, color))
        return this
    }

    fun lineTo(x: Float, y: Float, z: Float) = lineTo(Vec3f(x, y, z))

    fun lineTo(
        position: Vec3f,
        color: Color = this.color,
    ): LineMesh {
        lineBuffer.add(LineVertex(position, color))
        return this
    }

    fun stroke(): LineMesh {
        for (i in 1 until lineBuffer.size) {
            addLine(lineBuffer[i-1], lineBuffer[i-1].color, lineBuffer[i], lineBuffer[i].color)
        }
        lineBuffer.clear()
        return this
    }

    fun addWireframe(triMesh: IndexedVertexList, lineColor: Color? = null) {
        if (triMesh.primitiveType != PrimitiveType.TRIANGLES) {
            throw IllegalArgumentException("Supplied mesh is not a triangle mesh: ${triMesh.primitiveType}")
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
        val tmpN = MutableVec3f()
        geometry.forEach {
            tmpN.set(it.normal).norm().mul(len).add(it.position)
            val color = lineColor ?: it.color
            addLine(it.position, color, tmpN, color)
        }
    }

    fun addBoundingBox(aabb: BoundingBoxF, color: Color = this.color) {
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

    class LineVertex(position: Vec3f, val color: Color) : Vec3f(position)
}
