package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.UnlitMaterialConfig
import de.fabmax.kool.pipeline.shading.UnlitShader
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color

/**
 * @author fabmax
 */

fun pointMesh(name: String? = null, block: PointMesh.() -> Unit): PointMesh {
    return PointMesh(name = name).apply(block)
}

open class PointMesh(geometry: IndexedVertexList = IndexedVertexList(Attribute.POSITIONS, ATTRIB_POINT_SIZE, Attribute.COLORS), name: String? = null) :
        Mesh(geometry, name) {
    init {
        geometry.primitiveType = PrimitiveType.POINTS
        rayTest = MeshRayTest.nopTest()

        val unlitCfg = UnlitMaterialConfig()
        val unlitModel = UnlitShader.defaultUnlitModel(unlitCfg).apply {
            vertexStage {
                pointSize(attributeNode(ATTRIB_POINT_SIZE).output)
            }
        }
        shader = UnlitShader(unlitCfg, unlitModel)
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
        val ATTRIB_POINT_SIZE = Attribute("aPointSize", GlslType.FLOAT)
    }
}
