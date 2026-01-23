package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.ViewData
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.animation.Skin
import de.fabmax.kool.scene.geometry.*
import de.fabmax.kool.util.*

@Suppress("DEPRECATION")
@Deprecated("Use a layout struct instead of specifying vertex attributes individually")
fun Node.addMesh(
    attributes: List<Attribute>,
    instances: MeshInstanceList<*>? = null,
    name: String = makeChildName("mesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    block: Mesh<DynamicStruct>.() -> Unit
): Mesh<DynamicStruct> {
    val mesh = Mesh(IndexedVertexList(attributes, primitiveType), instances, name = name)
    mesh.block()
    addNode(mesh)
    return mesh
}

@Suppress("DEPRECATION")
@Deprecated("Use a layout struct instead of specifying vertex attributes individually")
fun Node.addMesh(
    vararg attributes: Attribute,
    instances: MeshInstanceList<*>? = null,
    name: String = makeChildName("mesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    block: Mesh<DynamicStruct>.() -> Unit
): Mesh<DynamicStruct> {
    val mesh = Mesh(IndexedVertexList(attributes.toList(), primitiveType), instances, name = name)
    mesh.block()
    addNode(mesh)
    return mesh
}

fun <Layout: Struct> Node.addMesh(
    layout: Layout,
    instances: MeshInstanceList<*>? = null,
    name: String = makeChildName("mesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    block: Mesh<Layout>.() -> Unit
): Mesh<Layout> {
    val mesh = Mesh(IndexedVertexList(layout, primitiveType = primitiveType), instances, name = name)
    mesh.block()
    addNode(mesh)
    return mesh
}

fun Node.addColorMesh(
    name: String = makeChildName("colorMesh"),
    primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
    instances: MeshInstanceList<*>? = null,
    block: ColorMesh.() -> Unit
): ColorMesh {
    return addMesh(
        layout = VertexLayouts.PositionNormalColor,
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
    block: TextureMesh.() -> Unit
): TextureMesh {
    val mesh = addMesh(
        layout = TextureMeshLayout,
        instances = instances,
        name = name,
        primitiveType = primitiveType,
        block = block
    )
    if (isNormalMapped) {
        mesh.geometry.generateTangents()
    }
    return mesh
}

@Suppress("DEPRECATION")
@Deprecated("Use a layout struct instead of specifying vertex attributes individually")
fun Mesh(
    attributes: List<Attribute>,
    instances: MeshInstanceList<*>? = null,
    name: String = Node.makeNodeName("Mesh"),
    usage: Usage = Usage.STATIC
): Mesh<DynamicStruct> = Mesh(
    geometry = IndexedVertexList(attributes, usage = usage),
    instances = instances,
    name = name
)

@Suppress("DEPRECATION")
@Deprecated("Use a layout struct instead of specifying vertex attributes individually")
fun Mesh(
    vararg attributes: Attribute,
    instances: MeshInstanceList<*>? = null,
    name: String = Node.makeNodeName("Mesh"),
    usage: Usage = Usage.STATIC
): Mesh<DynamicStruct> = Mesh(
    geometry = IndexedVertexList(*attributes, usage = usage),
    instances = instances,
    name = name
)

fun <Layout: Struct> Mesh(
    layout: Layout,
    instances: MeshInstanceList<*>? = null,
    name: String = Node.makeNodeName("Mesh"),
    usage: Usage = Usage.STATIC
): Mesh<Layout> = Mesh(
    geometry = IndexedVertexList(layout, usage = usage),
    instances = instances,
    name = name
)

/**
 * Class for renderable geometry (triangles, lines, points).
 */
open class Mesh<Layout: Struct>(
    val geometry: IndexedVertexList<Layout>,
    val instances: MeshInstanceList<*>? = null,
    val morphWeights: FloatArray? = null,
    val skin: Skin? = null,
    name: String = geometry.name
) : Node(name), DoubleBuffered {

    var isOpaque = true

    val meshPipelineData = MultiPipelineBindGroupData(BindGroupScope.MESH)

    private var pipeline: DrawPipeline? = null

    private val drawGeometry = IndexedVertexList(geometry.layout, primitiveType = geometry.primitiveType)
    private val drawInstances = instances?.let { MeshInstanceList(it.layout) }
    private var geometryUpdateModCount = -1
    val geometryBounds = BoundingBoxF()

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
        localBounds.add(geometryBounds)
    }

    inline fun generate(updateBounds: Boolean = true, clearGeometry: Boolean = true, generator: MeshBuilder<Layout>.() -> Unit) {
        if (clearGeometry) {
            geometry.clear()
        }
        MeshBuilder(geometry).generator()
        if (updateBounds) {
            updateGeometryBounds()
        }
    }

    fun updateGeometryBounds() {
        if (!geometry.hasAttribute(VertexLayouts.Position.position)) {
            logW { "Mesh $name has no default positions attribute, cannot update geometry bounds" }
            return
        }
        geometryBounds.clear()
        geometry.computeBounds(geometryBounds)
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
        // geometry.release()
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
        geometry.checkIsNotReleased()
        if (geometry.modCount.isDirty(geometryUpdateModCount)) {
            geometryUpdateModCount = geometry.modCount.count
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
        geom.checkIsNotReleased()
        cmd.setup(this, geom, insts, pipeline, drawGroupId)
    }

    override fun captureBuffer() {
        meshPipelineData.captureBuffer()
        if (isAsyncRendering) {
            if (drawGeometry.modCount.isDirty(geometry.modCount)) {
                geometry.checkIsNotReleased()
                drawGeometry.set(geometry)
            }
            if (instances != null && instances.modCount.isDirty(drawInstances!!.modCount)) {
                drawInstances.set(instances)
            }
        }
    }
}

typealias ColorMeshLayout = VertexLayouts.PositionNormalColor
typealias ColorMesh = Mesh<ColorMeshLayout>

/**
 * Creates a [Mesh] with [ColorMeshLayout] for vertex color based rendering.
 */
fun ColorMesh(
    instances: MeshInstanceList<*>? = null,
    name: String = Node.makeNodeName("ColorMesh"),
    usage: Usage = Usage.STATIC,
): ColorMesh = Mesh(
    layout = VertexLayouts.PositionNormalColor,
    instances = instances,
    name = name,
    usage = usage
)

typealias TextureMeshLayout = VertexLayouts.PositionNormalTexCoordTangent
typealias TextureMesh = Mesh<TextureMeshLayout>

/**
 * Creates a [Mesh] with [TextureMeshLayout] for texture color based rendering. The default [TextureMeshLayout] also
 * includes a tangent attribute in case normal mapping is used.
 */
fun TextureMesh(
    instances: MeshInstanceList<*>? = null,
    name: String = Node.makeNodeName("TextureMesh"),
    usage: Usage = Usage.STATIC,
): TextureMesh = Mesh(
    layout = TextureMeshLayout,
    instances = instances,
    name = name,
    usage = usage
)