package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.PipelineFactory
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder


inline fun mesh(attributes: Set<Attribute>, name: String? = null, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(IndexedVertexList(attributes), name)
    mesh.block()
    return mesh

}

fun colorMesh(name: String? = null, generate: Mesh.() -> Unit): Mesh {
    return mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS), name, generate)
}

fun textMesh(font: Font, name: String? = null, generate: Mesh.() -> Unit): Mesh {
    val text = mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS), name, generate)
    //text.shader = fontShader(font) { lightModel = LightModel.NO_LIGHTING }
    return text
}

fun textureMesh(name: String? = null, isNormalMapped: Boolean = false, generate: Mesh.() -> Unit): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    if (isNormalMapped) {
        attributes += Attribute.TANGENTS
    }
    val mesh = mesh(attributes, name, generate)
    if (isNormalMapped) {
        mesh.geometry.generateTangents()
    }
    return mesh
}

/**
 * Class for renderable geometry (triangles, lines, points, etc.).
 *
 * @author fabmax
 */
open class Mesh(var geometry: IndexedVertexList, name: String? = null) : Node(name) {

    //var pipelineLoader: (Mesh.(KoolContext) -> Pipeline)? = null
    var pipelineLoader: PipelineFactory? = null
    private var pipeline: Pipeline? = null

    var rayTest = MeshRayTest.boundsTest()

    override val bounds: BoundingBox
        get() = geometry.bounds

    open fun generate(generator: MeshBuilder.() -> Unit) {
        geometry.batchUpdate {
            clear()
            MeshBuilder(this).generator()
        }
    }

    open fun getPipeline(ctx: KoolContext): Pipeline? {
        return pipeline ?: pipelineLoader?.let { loader ->
            loader.createPipeline(this, Pipeline.Builder(), ctx).also { pipeline = it }
        }
    }

    override fun rayTest(test: RayTest) = rayTest.rayTest(test)

    /**
     * Deletes all buffers associated with this mesh.
     */
    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        // todo: pipeline.dispose()
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        if (!isRendered) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        // create or update data buffers for this mesh
//        if (meshData.checkBuffers(ctx)) {
//            // meshData was updated
//            rayTest.onMeshDataChanged(this)
//        }

        // fixme: use some caching, to avoid per-frame allocation of new DrawCommand object
        // multiple draw command objects are needed if object is rendered by multiple render passes (shadow maps, etc.)
        scene?.drawQueue?.addMesh(this, ctx)
    }
}