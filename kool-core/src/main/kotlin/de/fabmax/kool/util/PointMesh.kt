package de.fabmax.kool.util

import de.fabmax.kool.platform.GL
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.shading.BasicPointShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicPointShader

/**
 * @author fabmax
 */

fun pointMesh(name: String? = null, block: PointMesh.() -> Unit): PointMesh {
    return PointMesh().apply(block)
}

class PointMesh(data: MeshData = MeshData(false, true, false), name: String? = null) : Mesh(data, name) {
    init {
        primitiveType = GL.POINTS
        shader = basicPointShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    var pointSize: Float
        get() = (shader as BasicPointShader).pointSize
        set(value) { (shader as BasicPointShader).pointSize = value }

    fun addPoint(block: IndexedVertexList.Item.() -> Unit) {
        meshData.addIndex(meshData.addVertex(block))
    }

    fun addPoint(position: Vec3f, color: Color) {
        meshData.addIndex(meshData.addVertex(position, null, color, null))
    }
}
