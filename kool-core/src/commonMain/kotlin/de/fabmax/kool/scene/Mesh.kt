package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.scene.animation.Skin
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder


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
        set(value) {
            field = value
            if (value != null) {
                // frustum checking does not play well with instancing -> disable it if instancing is used
                isFrustumChecked = false
            }
        }
    var morphWeights: FloatArray? = null
    var skin: Skin? = null
    var isOpaque = true

    var shader: Shader? = null
        set(value) {
            field = value
            pipeline?.let { discardedPipelines += it }
            pipeline = null
        }

    /**
     * Optional shader used by DepthMapPass (mainly used for rendering shadow maps). If null DepthMapPass uses a
     * geometry based default shader.
     */
    var depthShader: Shader? = null

    /**
     * Optional list with lod geometry used by shadow passes. Shadow passes will use the geometry at index
     * [de.fabmax.kool.util.SimpleShadowMap.shadowMapLevel] or the last list entry in case the list has fewer entries.
     * If list is empty the regular geometry is used.
     */
    val shadowGeometry = mutableListOf<IndexedVertexList>()

    /**
     * Determines whether this node is considered during shadow pass.
     */
    var isCastingShadowLevelMask = -1
    var isCastingShadow: Boolean
        get() = isCastingShadowLevelMask != 0
        set(value) {
            isCastingShadowLevelMask = if (value) -1 else 0
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
        return pipeline ?: shader?.let { s ->
            s.createPipeline(this, ctx).also { pipeline = it }
        }
    }

    fun setIsCastingShadow(shadowMapLevel: Int, enabled: Boolean) {
        isCastingShadowLevelMask = if (enabled) {
            isCastingShadowLevelMask or (1 shl shadowMapLevel)
        } else {
            isCastingShadowLevelMask and (1 shl shadowMapLevel).inv()
        }
    }

    fun disableShadowCastingAboveLevel(shadowMapLevel: Int) {
        isCastingShadowLevelMask = isCastingShadowLevelMask and ((2 shl shadowMapLevel) - 1)
    }

    fun isCastingShadow(shadowMapLevel: Int): Boolean {
        return (isCastingShadowLevelMask and (1 shl shadowMapLevel)) != 0
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

    override fun collectDrawCommands(updateEvent: RenderPass.UpdateEvent) {
        if (!updateEvent.meshFilter(this)) {
            return
        }

        super.collectDrawCommands(updateEvent)

        if (!isRendered) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        val insts = instances
        if (insts != null && insts.numInstances == 0) {
            // instanced mesh has no instances
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
        updateEvent.renderPass.addMesh(this, updateEvent.ctx)
    }

    companion object {
        private var instanceId = 1L
    }
}