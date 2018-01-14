package de.fabmax.kool.util

import de.fabmax.kool.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.*

/**
 * @author fabmax
 */
class BillboardMesh(data: MeshData = MeshData(false, true, true), name: String = "") : Mesh(data, name) {

    init {
        shader = billboardShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    private var builder = MeshBuilder(data)

    var billboardSize: Float
        get() = (shader as BillboardShader).billboardSize
        set(value) { (shader as BillboardShader).billboardSize = value }

    fun addQuad(centerPosition: Vec3f, color: Color) {
        builder.color = color
        builder.rect {
            fullTexCoords()
            origin.set(centerPosition)
            // all vertices of the quad have the same position, actual coords are computed in vertex shader
            size.set(Vec2f.ZERO)
        }
    }

}

fun billboardShader(propsInit: ShaderProps.() -> Unit = { }): BillboardShader {
    val props = ShaderProps()
    props.propsInit()
    props.isTextureColor = true
    val generator = GlslGenerator()
    generator.addCustomUniform(Uniform2f("uViewportSz"))

    generator.injectors += object: GlslGenerator.GlslInjector {
        override fun vsAfterProj(shaderProps: ShaderProps, text: StringBuilder) {
            text.append("gl_Position.x += (").append(GlslGenerator.ATTRIBUTE_NAME_TEX_COORD)
                    .append(".x - 0.5) * gl_Position.w / uViewportSz.x;\n")
                    .append("gl_Position.y -= (").append(GlslGenerator.ATTRIBUTE_NAME_TEX_COORD)
                    .append(".y - 0.5) * gl_Position.w / uViewportSz.y;\n")
        }
    }
    return BillboardShader(props, generator)
}

class BillboardShader internal constructor(props: ShaderProps, generator: GlslGenerator) : BasicShader(props, generator) {

    private val uViewportSz = generator.customUnitforms["uViewportSz"] as Uniform2f

    var billboardSize = 1f

    override fun onBind(ctx: RenderContext) {
        super.onBind(ctx)
        uViewportSz.value.set(0.5f * ctx.viewport.width.toFloat() / billboardSize,
                0.5f * ctx.viewport.height.toFloat() / billboardSize)
        uViewportSz.bind(ctx)
    }
}
