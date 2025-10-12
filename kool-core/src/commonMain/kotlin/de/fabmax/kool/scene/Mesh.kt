package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.ViewData
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.animation.Skin
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.MutableStructBufferView
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.StructBuffer

fun Node.addMesh(
    attributes: List<Attribute>,
    instances: MeshInstanceList<*>? = null,
    name: String = makeChildName("mesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    block: Mesh.() -> Unit
): Mesh {
    val mesh = Mesh(IndexedVertexList(attributes, primitiveType), instances, name = name)
    mesh.block()
    addNode(mesh)
    return mesh
}

fun Node.addMesh(
    vararg attributes: Attribute,
    instances: MeshInstanceList<*>? = null,
    name: String = makeChildName("mesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    block: Mesh.() -> Unit
): Mesh {
    val mesh = Mesh(IndexedVertexList(attributes.toList(), primitiveType), instances, name = name)
    mesh.block()
    addNode(mesh)
    return mesh
}

fun Node.addColorMesh(
    name: String = makeChildName("colorMesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    instances: MeshInstanceList<*>? = null,
    block: Mesh.() -> Unit
): Mesh {
    return addMesh(
        Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS,
        instances = instances,
        name = name,
        primitiveType = primitiveType,
        block = block
    )
}

fun Node.addTextureMesh(
    name: String = makeChildName("textureMesh"),
    isNormalMapped: Boolean = false,
    instances: MeshInstanceList<*>? = null,
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    block: Mesh.() -> Unit
): Mesh {
    val attributes = mutableListOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    if (isNormalMapped) {
        attributes += Attribute.TANGENTS
    }
    val mesh = addMesh(attributes, instances, name, primitiveType, block)
    if (isNormalMapped) {
        mesh.geometry.generateTangents()
    }
    return mesh
}

/**
 * Class for renderable geometry (triangles, lines, points).
 */
open class Mesh(
    val geometry: IndexedVertexList<*>,
    val instances: MeshInstanceList<*>? = null,
    val morphWeights: FloatArray? = null,
    val skin: Skin? = null,
    name: String = geometry.name
) : Node(name), DoubleBuffered {

    constructor(attributes: List<Attribute>, instances: MeshInstanceList<*>? = null, name: String = makeNodeName("Mesh")) :
            this(IndexedVertexList(attributes), instances = instances, name = name)
    constructor(vararg attributes: Attribute, instances: MeshInstanceList<*>? = null, name: String = makeNodeName("Mesh")) :
            this(IndexedVertexList(*attributes), instances = instances, name = name)

    var isOpaque = true

    val meshPipelineData = MultiPipelineBindGroupData(BindGroupScope.MESH)

    private var pipeline: DrawPipeline? = null

    private val drawGeometry = IndexedVertexList(geometry.vertexAttributes, geometry.primitiveType)
    private val drawInstances = instances?.let { MeshInstanceList(it.layout) }
    private var geometryUpdateModCount = -1

    private var isAsyncRendering = true

    var shader: DrawShader? = null
        set(value) {
            if (field !== value) {
                field = value
                pipeline?.removeUser(this)
                pipeline = null
            }
        }

    /**
     * Optional shader used by [de.fabmax.kool.pipeline.DepthMapPass] (mainly used for rendering shadow maps). If null
     * DepthMapPass uses [depthShaderConfig] or - if this is null as well - a geometry based default config to create
     * a default depth shader.
     */
    var depthShader: DrawShader? = null

    /**
     * Optional shader used by [de.fabmax.kool.pipeline.NormalLinearDepthMapPass] (mainly used for rendering
     * screen-space ao maps). If null NormalLinearDepthMapPass uses [depthShaderConfig] or - if this is null as well -
     * a geometry based default config to create a default depth shader.
     */
    var normalLinearDepthShader: DrawShader? = null

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
    val shadowGeometry = mutableListOf<IndexedVertexList<*>>()

    /**
     * Determines whether this node is considered during shadow pass.
     */
    var isCastingShadowLevelMask = -1
    var isCastingShadow: Boolean
        get() = isCastingShadowLevelMask != 0
        set(value) {
            isCastingShadowLevelMask = if (value) -1 else 0
        }

    var rayTest = MeshRayTest.boundsTest()

    init {
        // frustum check is disabled by default for instanced meshes
        isFrustumChecked = instances == null
    }

    override fun addContentToBoundingBox(localBounds: BoundingBoxF) {
        localBounds.add(geometry.bounds)
    }

    inline fun generate(updateBounds: Boolean = true, generator: MeshBuilder.() -> Unit) {
        geometry.batchUpdate(updateBounds) {
            clear()
            MeshBuilder(this).generator()
        }
    }

    inline fun <reified T: Struct> addInstances(numInstances: Int, clear: Boolean = false, block: (StructBuffer<T>) -> Unit) {
        val insts = checkNotNull(instances) { "Mesh $name was not created with a MeshInstanceList" }
        require(insts.layout is T) { "Mesh $name uses ${insts.layout::class.simpleName} instance layout instead of ${T::class.simpleName}" }
        if (clear) {
            insts.clear()
        }
        @Suppress("UNCHECKED_CAST")
        (insts as MeshInstanceList<T>).addInstances(numInstances, block)
    }

    inline fun <reified T: Struct> addInstance(block: MutableStructBufferView<T>.(T) -> Unit) {
        addInstances(1) { buf -> buf.put(block) }
    }

    fun getOrCreatePipeline(
        ctx: KoolContext,
        instances: MeshInstanceList<*>? = this.instances
    ): DrawPipeline? {
        return pipeline ?: shader?.let { s ->
            s.getOrCreatePipeline(this, ctx, instances).also { pipeline = it }
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

    override fun rayTestLocal(test: RayTest, localRay: RayF) {
        if (rayTest.rayTest(test, localRay)) {
            test.collectHitGeometry(this)
        }
    }

    /**
     * Deletes all buffers associated with this mesh.
     */
    override fun doRelease() {
        super.doRelease()
        geometry.release()
        drawGeometry.release()
        instances?.release()
        drawInstances?.release()
        meshPipelineData.release()
        shadowGeometry.forEach { it.release() }
        pipeline?.removeUser(this)
        pipeline = null
    }

    override fun collectDrawCommands(viewData: ViewData, updateEvent: RenderPass.UpdateEvent) {
        super.collectDrawCommands(viewData, updateEvent)
        isAsyncRendering = updateEvent.ctx.backend.isAsyncRendering

        if (!updateEvent.drawFilter(this) || !isRendered) {
            // mesh is not visible (either hidden or outside frustum)
            return
        }

        val insts = instances
        if (insts != null && insts.numInstances == 0) {
            // instanced mesh has no instances
            return
        }

        // update bounds and ray test if geometry has changed
        if (geometry.modCount.isDirty(geometryUpdateModCount)) {
            geometryUpdateModCount = geometry.modCount.count
            if (geometry.isRebuildBoundsOnSync) {
                geometry.rebuildBounds()
            }
            rayTest.onMeshDataChanged(this)
        }

        getOrCreatePipeline(updateEvent.ctx)?.let { pipeline ->
            viewData.drawQueue.addMesh(this, pipeline)
        }
    }

    internal fun setupDrawCommand(cmd: DrawCommand, pipeline: DrawPipeline, drawGroupId: Int) {
        val geom: IndexedVertexList<*>
        val insts: MeshInstanceList<*>?
        if (isAsyncRendering) {
            geom = drawGeometry
            insts = drawInstances
        } else {
            geom = geometry
            insts = instances
        }
        cmd.setup(this, geom, insts, pipeline, drawGroupId)
    }

    override fun captureBuffer() {
        meshPipelineData.captureBuffer()
        if (isAsyncRendering) {
            if (drawGeometry.modCount.isDirty(geometry.modCount)) {
                drawGeometry.set(geometry)
            }
            if (instances != null && instances.modCount.isDirty(drawInstances!!.modCount)) {
                drawInstances.set(instances)
            }
        }
    }
}

/**
 * Mesh with default attributes for vertex color based rendering:
 * [Attribute.POSITIONS], [Attribute.NORMALS], [Attribute.COLORS]
 */
open class ColorMesh(
    instances: MeshInstanceList<*>? = null,
    name: String = makeNodeName("ColorMesh"),
) : Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, instances = instances, name = name)

/**
 * Mesh with default attributes for texture color based rendering:
 * [Attribute.POSITIONS], [Attribute.NORMALS], [Attribute.TEXTURE_COORDS] and [Attribute.TANGENTS] if
 * isNormalMapped is true.
 */
open class TextureMesh(
    isNormalMapped: Boolean = false,
    instances: MeshInstanceList<*>? = null,
    name: String = makeNodeName("TextureMesh")
) : Mesh(
    if (isNormalMapped) {
        listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
    } else {
        listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
    },
    instances = instances,
    name = name
)
