package de.fabmax.kool.util

import de.fabmax.kool.RenderContext
import de.fabmax.kool.gl.GL_ALWAYS
import de.fabmax.kool.gl.GL_LINES
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader

/**
 * @author fabmax
 */

fun lineMesh(name: String? = null, block: LineMesh.() -> Unit): LineMesh {
    return LineMesh(name = name).apply(block)
}

open class LineMesh(data: MeshData = MeshData(Attribute.POSITIONS, Attribute.COLORS), name: String? = null) :
        Mesh(data, name) {
    init {
        primitiveType = GL_LINES
        shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    var isXray = false
    var lineWidth = 1f

    fun addLine(point0: Vec3f, color0: Color, point1: Vec3f, color1: Color) {
        var idx =  meshData.addVertex(point0, null, color0, null)
        meshData.addIndex(idx)
        idx =  meshData.addVertex(point1, null, color1, null)
        meshData.addIndex(idx)
    }

    override fun render(ctx: RenderContext) {
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
