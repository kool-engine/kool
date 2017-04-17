package de.fabmax.kool.util

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.shading.*

/**
 * @author fabmax
 */

fun lineMesh(name: String? = null, block: LineMesh.() -> Unit): LineMesh {
    return LineMesh(name = name).apply(block)
}

class LineMesh(data: MeshData = MeshData(false, true, false), name: String? = null) : Mesh(data, name) {
    init {
        primitiveType = GL.LINES
        shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    var lineWidth = 1f

    fun addLine(point0: Vec3f, color0: Color, point1: Vec3f, color1: Color) {
        var idx =  meshData.addVertex(point0, null, color0, null)
        meshData.addIndex(idx)
        idx =  meshData.addVertex(point1, null, color1, null)
        meshData.addIndex(idx)
    }

    override fun render(ctx: RenderContext) {
        GL.lineWidth(lineWidth)
        super.render(ctx)
    }
}
