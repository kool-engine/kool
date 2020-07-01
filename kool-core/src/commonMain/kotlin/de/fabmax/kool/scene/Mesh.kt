package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.PipelineFactory
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.animation.Skin
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.MeshInstanceList


inline fun mesh(attributes: List<Attribute>, name: String? = null, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(IndexedVertexList(attributes), name)
    mesh.block()
    return mesh

}

fun colorMesh(name: String? = null, generate: Mesh.() -> Unit): Mesh {
    return mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS), name, generate)
}

fun textureMesh(name: String? = null, isNormalMapped: Boolean = false, generate: Mesh.() -> Unit): Mesh {
    val attributes = mutableListOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
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
 * Class for renderable geometry (triangles, lines, points).
 */
open class Mesh(var geometry: IndexedVertexList, name: String? = null) : Node(name) {

    val id = instanceId++

    var instances: MeshInstanceList? = null
    var skin: Skin? = null

    var pipelineLoader: PipelineFactory? = null
        set(value) {
            field = value
            pipeline?.let { discardedPipelines += it }
            pipeline = null
        }

    private var pipeline: Pipeline? = null
    private val discardedPipelines = mutableListOf<Pipeline>()

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
        if (discardedPipelines.isNotEmpty()) {
            discardedPipelines.forEach { ctx.disposePipeline(it) }
            discardedPipelines.clear()
        }
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
        pipeline?.let { ctx.disposePipeline(it) }
        pipeline = null
    }

    override fun collectDrawCommands(renderPass: RenderPass, ctx: KoolContext) {
        super.collectDrawCommands(renderPass, ctx)

        if (!isRendered) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        // update bounds and ray test if geometry has changed
        if (geometry.hasChanged && !geometry.isBatchUpdate) {
            // don't clear the hasChanged flag yet, is done by rendering backend after vertex buffers are updated
            if (geometry.isRebuildBoundsOnSync) {
                geometry.rebuildBounds()
            }
            rayTest.onMeshDataChanged(this)
        }
        renderPass.drawQueue.addMesh(this, ctx)
    }

    companion object {
        private var instanceId = 1L
    }
}