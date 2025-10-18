package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

fun Node.addPointMesh(
    name: String = Node.makeNodeName("LineMesh"),
    block: PointMesh.() -> Unit
): PointMesh {
    val pointMesh = PointMesh(name).apply(block)
    addNode(pointMesh)
    return pointMesh
}

fun PointMesh(name: String = Node.makeNodeName("LineMesh")): PointMesh =
    PointMesh(IndexedVertexList(CustomPointMesh.PointVertexLayout, primitiveType = PrimitiveType.POINTS), name)

typealias PointMesh = CustomPointMesh<CustomPointMesh.PointVertexLayout>

open class CustomPointMesh<Layout: Struct>(
    geometry: IndexedVertexList<Layout>,
    name: String = makeNodeName("LineMesh"),
) : Mesh<Layout>(geometry, name = name) {
    private val sizeAttr = geometry.layout.getFloat1(PointVertexLayout.pointSize.name)

    init {
        rayTest = MeshRayTest.nopTest()

        shader = KslUnlitShader {
            color { vertexColor() }
            modelCustomizer = {
                vertexStage {
                    main {
                        outPointSize set vertexAttrib(PointVertexLayout.pointSize)
                    }
                }
            }
        }
    }

    fun addPoint(block: MutableStructBufferView<Layout>.(Layout) -> Unit): Int {
        val idx = geometry.addVertex(block)
        geometry.addIndex(idx)
        return idx
    }

    fun addPoint(position: Vec3f, pointSize: Float, color: Color): Int {
        val idx = geometry.addVertex {
            geometry.positionAttr?.set(position)
            geometry.colorAttr?.set(color)
            sizeAttr?.set(pointSize)
        }
        geometry.addIndex(idx)
        return idx
    }

    fun clear() {
        geometry.clear()
        bounds.clear()
    }

    object PointVertexLayout : Struct("PointVertexLayout", MemoryLayout.TightlyPacked) {
        val position = include(VertexLayouts.Position.position)
        val pointSize = float1("attr_point_size")
        val color = include(VertexLayouts.Color.color)
    }
}
