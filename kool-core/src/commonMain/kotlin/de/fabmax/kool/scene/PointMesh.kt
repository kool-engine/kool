package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.asAttribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

/**
 * @author fabmax
 */

fun Node.addPointMesh(name: String = Node.makeNodeName("LineMesh"), block: PointMesh.() -> Unit): PointMesh {
    val pointMesh = PointMesh(name).apply(block)
    addNode(pointMesh)
    return pointMesh
}

open class PointMesh(
    name: String = makeNodeName("LineMesh"),
    geometry: IndexedVertexList<*> = IndexedVertexList(PointVertexLayout, primitiveType = PrimitiveType.POINTS)
) : Mesh(geometry, name = name) {
    init {
        rayTest = MeshRayTest.nopTest()

        shader = KslUnlitShader {
            color { vertexColor() }
            modelCustomizer = {
                vertexStage {
                    main {
                        outPointSize set vertexAttribFloat1(PointVertexLayout.pointSize)
                    }
                }
            }
        }
    }

    fun addPoint(block: VertexView<*>.() -> Unit): Int {
        val idx =  geometry.addVertex(block)
        geometry.addIndex(idx)
        return idx
    }

    fun addPoint(position: Vec3f, pointSize: Float, color: Color): Int {
        val idx = geometry.addVertex {
            this.position.set(position)
            this.color.set(color)
            getFloatAttribute(attrPointSize)?.f = pointSize
        }
        geometry.addIndex(idx)
        return idx
    }

    fun clear() {
        geometry.clear()
        bounds.clear()
    }

    object PointVertexLayout : Struct("PointVertexLayout", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val pointSize = float1("aPointSize")
        val color = float4(Attribute.COLORS.name)
    }

    companion object {
        val attrPointSize = PointVertexLayout.pointSize.asAttribute()
    }
}
