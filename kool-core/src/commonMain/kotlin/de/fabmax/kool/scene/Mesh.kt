package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.RenderPass
import de.fabmax.kool.gl.GL_BACK
import de.fabmax.kool.gl.GL_FRONT
import de.fabmax.kool.gl.glDrawElements
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.fontShader


inline fun mesh(withNormals: Boolean, withColors: Boolean, withTexCoords: Boolean, name: String? = null,
         block: Mesh.() -> Unit): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS)
    if (withNormals) {
        attributes += Attribute.NORMALS
    }
    if (withColors) {
        attributes += Attribute.COLORS
    }
    if (withTexCoords) {
        attributes += Attribute.TEXTURE_COORDS
    }
    return mesh(attributes, name, block)
}

inline fun mesh(attributes: Set<Attribute>, name: String? = null, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(MeshData(attributes), name)

    mesh.shader = basicShader {
        lightModel = if (attributes.contains(Attribute.NORMALS)) {
            LightModel.PHONG_LIGHTING
        } else {
            LightModel.NO_LIGHTING
        }

        colorModel = when {
            attributes.contains(Attribute.TEXTURE_COORDS) -> ColorModel.TEXTURE_COLOR
            attributes.contains(Attribute.COLORS) -> ColorModel.VERTEX_COLOR
            else -> ColorModel.STATIC_COLOR
        }
    }

    mesh.block()

    // todo: Optionally generate geometry lazily
    mesh.generateGeometry()

    return mesh

}

fun colorMesh(name: String? = null, generate: Mesh.() -> Unit): Mesh {
    return mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS), name, generate)
}

fun textMesh(font: Font, name: String? = null, generate: Mesh.() -> Unit): Mesh {
    val text = mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS), name, generate)
    text.shader = fontShader(font) { lightModel = LightModel.NO_LIGHTING }
    return text
}

fun textureMesh(name: String? = null, isNormalMapped: Boolean = false, generate: Mesh.() -> Unit): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    if (isNormalMapped) {
        attributes += Attribute.TANGENTS
    }
    val mesh = mesh(attributes, name, generate)
    if (isNormalMapped) {
        mesh.meshData.generateTangents()
    }
    return mesh
}

enum class CullMethod {
    DEFAULT,
    CULL_BACK_FACES,
    CULL_FRONT_FACES,
    NO_CULLING
}

/**
 * Abstract base class for renderable geometry (triangles, lines, points, etc.).
 *
 * @author fabmax
 */
open class Mesh(var meshData: MeshData, name: String? = null) : Node(name) {

    open var generator: (MeshBuilder.() -> Unit)?
        get() = meshData.generator
        set(value) { meshData.generator = value }

    var shader: Shader? = null
    var cullMethod = CullMethod.DEFAULT
    var rayTest = MeshRayTest.boundsTest()

    override val bounds: BoundingBox
        get() = meshData.bounds

    init {
        meshData.incrementReferenceCount()
    }

    open fun generateGeometry() {
        meshData.generateGeometry()
    }

    open fun getAttributeBinder(attrib: Attribute): VboBinder? = meshData.attributeBinders[attrib]

    override fun rayTest(test: RayTest) = rayTest.rayTest(test)

    /**
     * Deletes all buffers associated with this mesh.
     */
    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        meshData.dispose(ctx)
        shader?.dispose(ctx)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        val shader = this.shader
        if (!isRendered || shader == null || (!ctx.isDepthTest && ctx.renderPass == RenderPass.SHADOW)) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        // create or update data buffers for this mesh
        if (meshData.checkBuffers(ctx)) {
            // meshData was updated
            rayTest.onMeshDataChanged(this)
        }

        // bind shader for this mesh
        ctx.shaderMgr.bindShader(shader, this, ctx)

        // setup shader for mesh rendering, the active shader is not necessarily mMeshShader
        ctx.shaderMgr.boundShader?.let { boundShader ->
            // bind this mesh as input to the used shader
            boundShader.bindMesh(this, ctx)

            if (cullMethod != CullMethod.DEFAULT) {
                ctx.pushAttributes()
                when (cullMethod) {
                    CullMethod.CULL_BACK_FACES -> {
                        ctx.isCullFace = true
                        ctx.cullFace = GL_BACK
                    }
                    CullMethod.CULL_FRONT_FACES -> {
                        ctx.isCullFace = true
                        ctx.cullFace = GL_FRONT
                    }
                    else -> ctx.isCullFace = false
                }
                ctx.applyAttributes()
            }

            // draw mesh
            meshData.indexBuffer?.bind(ctx)
            drawElements(ctx)
            boundShader.unbindMesh(ctx)

            if (cullMethod != CullMethod.DEFAULT) {
                ctx.popAttributes()
            }
        }
    }

    protected open fun drawElements(ctx: KoolContext) {
        glDrawElements(meshData.primitiveType, meshData.numIndices, meshData.indexType, 0)
    }
}

