package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.animation.Skin
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.UniqueId


fun Node.addMesh(attributes: List<Attribute>, name: String? = null, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(IndexedVertexList(attributes), name)
    mesh.block()
    addNode(mesh)
    return mesh
}

fun Node.addMesh(vararg attributes: Attribute, name: String? = null, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(IndexedVertexList(*attributes), name)
    mesh.block()
    addNode(mesh)
    return mesh
}

fun Node.addColorMesh(name: String? = null, block: Mesh.() -> Unit): Mesh {
    return addMesh(
        Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS,
        name = name ?: UniqueId.nextId("colorMesh"),
        block = block
    )
}

fun Node.addTextureMesh(name: String? = null, isNormalMapped: Boolean = false, block: Mesh.() -> Unit): Mesh {
    val attributes = mutableListOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    if (isNormalMapped) {
        attributes += Attribute.TANGENTS
    }
    val mesh = addMesh(attributes, name ?: UniqueId.nextId("textureMesh"), block)
    if (isNormalMapped) {
        mesh.geometry.generateTangents()
    }
    return mesh
}

@Deprecated("to be replaced by addMesh()", ReplaceWith("addMesh(attributes, name) { block() }"))
fun Node.mesh(attributes: List<Attribute>, name: String? = null, block: Mesh.() -> Unit) = addMesh(attributes, name, block)

@Deprecated("to be replaced by addMesh()", ReplaceWith("addMesh(attributes, name) { block() }"))
fun Node.mesh(vararg attributes: Attribute, name: String? = null, block: Mesh.() -> Unit) = addMesh(*attributes, name = name, block = block)

@Deprecated("to be replaced by addColorMesh()", ReplaceWith("addColorMesh(name) { block() }"))
fun Node.colorMesh(name: String? = null, block: Mesh.() -> Unit) = addColorMesh(name, block)

@Deprecated("to be replaced by addTextureMesh()", ReplaceWith("addTextureMesh(name, isNormalMapped) { block() }"))
fun Node.textureMesh(name: String? = null, isNormalMapped: Boolean = false, block: Mesh.() -> Unit) = addTextureMesh(name, isNormalMapped, block)

/**
 * Class for renderable geometry (triangles, lines, points).
 */
open class Mesh(var geometry: IndexedVertexList, name: String? = null) : Node(name) {

    constructor(attributes: List<Attribute>, name: String? = null) : this(IndexedVertexList(attributes), name)
    constructor(vararg attributes: Attribute, name: String? = null) : this(IndexedVertexList(*attributes), name)

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
            if (field !== value) {
                field = value
                // fixme: this is not optimal in cases where the old shader is still used in other places
                pipeline?.let { discardedPipelines += it }
                pipeline = null
            }
        }

    /**
     * Optional shader used by [de.fabmax.kool.pipeline.DepthMapPass] (mainly used for rendering shadow maps). If null
     * DepthMapPass uses [depthShaderConfig] or - if this is null as well - a geometry based default config to create
     * a default depth shader.
     */
    var depthShader: Shader? = null

    /**
     * Optional shader used by [de.fabmax.kool.pipeline.NormalLinearDepthMapPass] (mainly used for rendering
     * screen-space ao maps). If null NormalLinearDepthMapPass uses [depthShaderConfig] or - if this is null as well -
     * a geometry based default config to create a default depth shader.
     */
    var normalLinearDepthShader: Shader? = null

    /**
     * Custom config for depth shader creation. If non-null, this is used to create depth shaders for shadow and ssao
     * passes. By supplying a custom depth shader config, depth shaders can consider alpha masks. If [depthShader]
     * and / or [normalLinearDepthShader] are set, these are preferred.
     */
    var depthShaderConfig: DepthShader.Config? = null

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

    /**
     * Time the latest draw call took (in ms).
     */
    var drawTime = 0.0
        internal set

    init {
        isFrustumChecked = true
    }

    override fun computeLocalBounds(result: BoundingBox) {
        super.computeLocalBounds(result)
        result.add(geometry.bounds)
    }

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

    override fun rayTest(test: RayTest) {
        super.rayTest(test)

        if (!transform.isIdentity) {
            // transform ray to local coordinates
            test.transformBy(transform.matrixInverse)
        }
        rayTest.rayTest(test)
        if (!transform.isIdentity) {
            // transform ray back to previous coordinates
            test.transformBy(transform.matrix)
        }
    }

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
        updateEvent.renderPass.appendMeshToDrawQueue(this, updateEvent.ctx)
    }

    companion object {
        private var instanceId = 1L
    }
}

/**
 * Mesh with default attributes for vertex color based rendering:
 * [Attribute.POSITIONS], [Attribute.NORMALS], [Attribute.COLORS]
 */
open class ColorMesh(name: String? = null) : Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, name = name)

/**
 * Mesh with default attributes for texture color based rendering:
 * [Attribute.POSITIONS], [Attribute.NORMALS], [Attribute.TEXTURE_COORDS] and [Attribute.TANGENTS] if
 * isNormalMapped is true.
 */
open class TextureMesh(isNormalMapped: Boolean = false, name: String? = null) : Mesh(
    if (isNormalMapped) {
        listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
    } else {
        listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    },
    name = name
)
