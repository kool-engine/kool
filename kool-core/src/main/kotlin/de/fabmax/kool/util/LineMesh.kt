package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.GL_ALWAYS
import de.fabmax.kool.gl.GL_LINES
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
