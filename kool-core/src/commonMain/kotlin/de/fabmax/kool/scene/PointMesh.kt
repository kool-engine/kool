package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.PrimitiveType
import de.fabmax.kool.util.VertexView

/**
 * @author fabmax
 */

fun pointMesh(name: String? = null, block: PointMesh.() -> Unit): PointMesh {
    return PointMesh(name = name).apply(block)
}

open class PointMesh(geometry: IndexedVertexList = IndexedVertexList(Attribute.POSITIONS, Attribute.COLORS), name: String? = null) :
        Mesh(geometry, name) {
    init {
        geometry.primitiveType = PrimitiveType.POINTS
//        rayTest = MeshRayTest.nopTest()
//        shader = basicPointShader {
//            colorModel = ColorModel.VERTEX_COLOR
//            lightModel = LightModel.NO_LIGHTING
//        }
    }

    var isXray = false
    var pointSize: Float = 1f
//        get() = (shader as BasicPointShader).pointSize
//        set(value) { (shader as BasicPointShader).pointSize = value }

    fun addPoint(block: VertexView.() -> Unit): Int {
        val idx =  geometry.addVertex(block)
        geometry.addIndex(idx)
        return idx
    }

    fun addPoint(position: Vec3f, color: Color): Int {
        val idx =  geometry.addVertex(position, null, color, null)
        geometry.addIndex(idx)
        return idx
    }

    fun clear() {
        geometry.clear()
        bounds.clear()
    }

    override fun render(ctx: KoolContext) {
//        ctx.pushAttributes()
//        if (isXray) {
//            ctx.depthFunc = GL_ALWAYS
//        }
//        ctx.applyAttributes()
//
//        super.render(ctx)
//
//        ctx.popAttributes()
    }
}
