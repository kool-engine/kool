package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color

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
    geometry: IndexedVertexList = IndexedVertexList(Attribute.POSITIONS, ATTRIB_POINT_SIZE, Attribute.COLORS, primitiveType = PrimitiveType.POINTS)
) : Mesh(geometry, name = name) {
    init {
        rayTest = MeshRayTest.nopTest()

        shader = KslUnlitShader {
            color { vertexColor() }
            modelCustomizer = {
                vertexStage {
                    main {
                        outPointSize set vertexAttribFloat1(ATTRIB_POINT_SIZE.name)
                    }
                }
            }
        }
    }

    fun addPoint(block: VertexView.() -> Unit): Int {
        val idx =  geometry.addVertex(block)
        geometry.addIndex(idx)
        return idx
    }

    fun addPoint(position: Vec3f, pointSize: Float, color: Color): Int {
        val idx = geometry.addVertex {
            this.position.set(position)
            this.color.set(color)
            getFloatAttribute(ATTRIB_POINT_SIZE)?.f = pointSize
        }
        geometry.addIndex(idx)
        return idx
    }

    fun clear() {
        geometry.clear()
        bounds.clear()
    }

    companion object {
        val ATTRIB_POINT_SIZE = Attribute("aPointSize", GpuType.Float1)
    }
}
