package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.*
import kotlin.math.sqrt

/**
 * A scene node. Scene nodes have a [transform], which controls the position, orientation and size of the node and all
 * its child-nodes.
 * Moreover, this is the base class for all other scene objects (e.g. [Mesh]). On its own, a Node acts as a group for
 * an arbitrary number of child-nodes.
 *
 * @author fabmax
 */
open class Node(name: String? = null) : BaseReleasable() {

    val id = NodeId()

    var name: String = name ?: makeNodeName(this::class.simpleName ?: "Node")

    val onUpdate: BufferedList<(RenderPass.UpdateEvent) -> Unit> = BufferedList()

    val tags = Tags()

    protected val mutChildren = mutableListOf<Node>()
    val children: List<Node> get() = mutChildren

    /**
     * Axis-aligned bounding box of this node in local coordinate frame.
     */
    val bounds = BoundingBoxF()
    private val tmpTransformVec = MutableVec3f()
    private val parentBoundsCache = BoundingBoxF()
    private var parentBoundsModCount = -1
    private val cachedBoundsMin = MutableVec3f()
    private val cachedBoundsMax = MutableVec3f()

    /**
     * Center point of this node's bounds in global coordinates.
     */
    val globalCenter: Vec3f get() = globalCenterMut

    /**
     * Radius of this node's bounding sphere in global coordinates.
     */
    var globalRadius = 0f
        protected set

    /**
     * Can be used to group nodes in the scene, changing their draw order. These groups will be drawn in ascending
     * order of their drawGroupId. Nodes within a group (with the same drawGroupId will keep their order).
     * A drawGroupId of 0 (the default value) will inherit the drawGroupId of the parent node.
     *
     * Draw groups are particularly useful in combination with [RenderPass.View.frameCopies] to capture intermediate
     * render outputs, which can then be used by following draw operations / shaders.
     */
    var drawGroupId = 0

    private val globalCenterMut = MutableVec3f()

    /**
     * This node's transform. Can be used to manipulate this node's position, size, etc. Notice that, by default, the
     * transform is set to [TrsTransformF], which treats position, rotation and scale as separate independent properties.
     * As an alternative, you can also use [MatrixTransformF], which applies all transform operations directly to a 4x4
     * transform matrix.
     */
    var transform: Transform = TrsTransformF()

    val modelMatrixData = SyncedMatrixFd()
    private val tmpVec = MutableVec3f()

    /**
     * This node's single-precision model matrix. Updated on each frame based on this node's transform and the model
     * matrix of the parent node.
     */
    val modelMatF: Mat4f by modelMatrixData::matF

    /**
     * This node's double-precision model matrix. Actual double-precision is only achieved, if this node also uses a
     * double precision [transform]. Updated on each frame based on this node's transform and the model
     * matrix of the parent node.
     */
    val modelMatD: Mat4d by modelMatrixData::matD

    /**
     * Inverse of this node's model matrix (single-precision).
     */
    val invModelMatF: Mat4f get() = modelMatrixData.invF

    /**
     * Inverse of this node's model matrix (double-precision).
     */
    val invModelMatD: Mat4d get() = modelMatrixData.invD

    /**
     * Parent node is set when this node is added to another [Node] as a child.
     */
    var parent: Node? = null
        private set

    /**
     * Determines the visibility of this node. If visible is false this node will be skipped on
     * rendering.
     */
    var isVisible = true

    /**
     * Determines whether this node is considered for ray-picking tests.
     */
    var isPickable = true

    /**
     * Determines whether this node is checked for visibility during rendering. If true the node is only rendered
     * if it is within the camera frustum.
     */
    var isFrustumChecked = false

    /**
     * Flag indicating if this node should be rendered. The flag is updated in the [collectDrawCommands] method based on
     * the [isVisible] flag and [isFrustumChecked]. I.e. it is false if this node is either explicitly hidden or outside
     * the camera frustum and frustum checking is enabled.
     */
    var isRendered = true
        protected set

    /**
     * Called once on every new frame before draw commands are collected. Implementations should use this method to
     * update their transform matrices, bounding boxes, animation states, etc.
     */
    open fun update(updateEvent: RenderPass.UpdateEvent) {
        checkIsNotReleased()

        onUpdate.update()
        for (i in onUpdate.indices) {
            onUpdate[i](updateEvent)
        }

        updateModelMat()

        bounds.batchUpdate {
            clear()
            for (i in mutChildren.indices) {
                val child = mutChildren[i]
                child.update(updateEvent)
                child.addBoundsToParentBounds(bounds)
            }
            addContentToBoundingBox(bounds)
        }

        // update global center and radius, don't do modCount-based caching here since bounds can change too
        if (updateEvent.view.renderPass.isDoublePrecision) {
            val toGlobal = modelMatD
            toGlobal.transform(globalCenterMut.set(bounds.center))
            globalRadius = toGlobal.transform(tmpVec.set(bounds.size), 0f).length() * 0.5f
        } else {
            val toGlobal = modelMatF
            toGlobal.transform(globalCenterMut.set(bounds.center))
            globalRadius = toGlobal.transform(tmpVec.set(bounds.size), 0f).length() * 0.5f
        }
    }

    private fun addBoundsToParentBounds(parentBounds: BoundingBoxF) {
        if (!bounds.isEmpty) {
            if (transform.modCount != parentBoundsModCount || cachedBoundsMin != bounds.min || cachedBoundsMax != bounds.max) {
                cachedBoundsMin.set(bounds.min)
                cachedBoundsMax.set(bounds.max)
                parentBoundsModCount = transform.modCount

                parentBoundsCache.batchUpdate {
                    val minX = bounds.min.x
                    val minY = bounds.min.y
                    val minZ = bounds.min.z
                    val maxX = bounds.max.x
                    val maxY = bounds.max.y
                    val maxZ = bounds.max.z
                    clear()
                    add(transform.transform(tmpTransformVec.set(minX, minY, minZ)))
                    add(transform.transform(tmpTransformVec.set(minX, minY, maxZ)))
                    add(transform.transform(tmpTransformVec.set(minX, maxY, minZ)))
                    add(transform.transform(tmpTransformVec.set(minX, maxY, maxZ)))
                    add(transform.transform(tmpTransformVec.set(maxX, minY, minZ)))
                    add(transform.transform(tmpTransformVec.set(maxX, minY, maxZ)))
                    add(transform.transform(tmpTransformVec.set(maxX, maxY, minZ)))
                    add(transform.transform(tmpTransformVec.set(maxX, maxY, maxZ)))
                }
            }
            parentBounds.add(parentBoundsCache)
        }
    }

    protected open fun addContentToBoundingBox(localBounds: BoundingBoxF) { }

    fun updateModelMat(): Boolean = transform.applyToModelMat(parent?.modelMatrixData, modelMatrixData)

    fun updateModelMatRecursive() {
        updateModelMat()
        for (i in children.indices) {
            children[i].updateModelMatRecursive()
        }
    }

    /**
     * Called on a per-frame basis, when the draw queue is built. The actual number of times this method
     * is called per frame depends on various factors (number of render passes, object visibility, etc.).
     * When this message is called, implementations can, but don't have to, append a DrawCommand to the provided
     * DrawQueue.
     */
    open fun collectDrawCommands(updateEvent: RenderPass.UpdateEvent) {
        if (!updateEvent.drawFilter(this)) return

        isRendered = checkIsVisible(updateEvent.camera, updateEvent.ctx)
        if (!isRendered) return

        // determine this Node's drawOrderId
        val orderId = if (drawGroupId == 0) updateEvent.view.drawQueue.drawGroupId else drawGroupId

        for (i in mutChildren.indices) {
            // (re-)set this Node's drawOrderId for each child, in case it was changed by previous child
            updateEvent.view.drawQueue.drawGroupId = orderId
            mutChildren[i].collectDrawCommands(updateEvent)
        }
        // restore this Node's drawOrderId for subclass implementation (mesh)
        updateEvent.view.drawQueue.drawGroupId = orderId
    }

    /**
     * Frees all resources occupied by this Node.
     */
    override fun release() {
        // fixme: Ideally, nodes should only be released once. However, currently, multi-release still happens
        //  a lot, so check for it here
        if (!isReleased) {
            children.forEach { it.release() }
            super.release()
        }
    }

    /**
     * Transforms [vec] in-place from local to global coordinates.
     */
    fun toGlobalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f = modelMatF.transform(vec, w)

    fun toGlobalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d = modelMatD.transform(vec, w)

    /**
     * Transforms [vec] in-place from global to local coordinates.
     */
    fun toLocalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f = invModelMatF.transform(vec, w)

    fun toLocalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d = invModelMatD.transform(vec, w)

    /**
     * Performs a hit test with the given [RayTest]. Subclasses should override [rayTestLocal] and test
     * if their contents are hit by the ray.
     */
    fun rayTest(test: RayTest) {
        if (test.isIntersectingBoundingSphere(this)) {
            test.collectHitBoundingSphere(this)
            // bounding sphere hit -> transform ray to local coordinates and do further testing
            val localRay = if (transform.isDoublePrecision) {
                test.getRayTransformed(invModelMatD)
            } else {
                test.getRayTransformed(invModelMatF)
            }

            val dLocal = bounds.hitDistanceSqr(localRay)
            if (dLocal < Float.POSITIVE_INFINITY) {
                test.collectHitBoundingBox(this)
                val dGlobal = toGlobalCoords(tmpVec.set(localRay.direction).mul(sqrt(dLocal)), 0f).length()
                if (dGlobal <= test.hitDistance) {
                    // local bounding box hit, test node content
                    rayTestLocal(test, localRay)

                    // test child nodes
                    for (i in mutChildren.indices) {
                        val child = mutChildren[i]
                        if (child.isVisible && child.isPickable) {
                            child.rayTest(test)
                        }
                    }
                }
            }
        } else {
            test.collectNoHit(this)
        }
    }

    open fun rayTestLocal(test: RayTest, localRay: RayF) { }

    /**
     * Called during [collectDrawCommands]: Checks if this node is currently visible. If not rendering is skipped. Default
     * implementation considers [isVisible] flag and performs a camera frustum check if [isFrustumChecked] is true.
     */
    protected open fun checkIsVisible(cam: Camera, ctx: KoolContext): Boolean {
        if (!isVisible) {
            return false
        } else if (isFrustumChecked && !bounds.isEmpty) {
            return cam.isInFrustum(this)
        }
        return true
    }

    open fun addNode(node: Node, index: Int = -1) {
        if (node in children) {
            logW { "Node ${node.name}($node) added multiple times to parent ${name}($this)" }
        }

        if (index >= 0) {
            mutChildren.add(index, node)
        } else {
            mutChildren.add(node)
        }
        node.parent = this
        bounds.add(node.bounds)
    }

    open fun removeNode(node: Node): Boolean {
        if (mutChildren.remove(node)) {
            node.parent = null
            return true
        }
        return false
    }

    open operator fun contains(node: Node): Boolean = mutChildren.contains(node)

    operator fun plusAssign(node: Node) {
        addNode(node)
    }

    operator fun minusAssign(node: Node) {
        removeNode(node)
    }

    open fun <R: Comparable<R>> sortChildrenBy(selector: (Node) -> R) {
        mutChildren.sortBy(selector)
    }

    open fun clearChildren() {
        mutChildren.forEach { it.parent = null }
        mutChildren.clear()
    }

    /**
     * Searches for a node with the specified name. Returns null if no such node is found.
     */
    open fun findNode(name: String): Node? {
        if (name == this.name) {
            return this
        }
        for (i in children.indices) {
            val found = children[i].findNode(name)
            if (found != null) {
                return found
            }
        }
        return null
    }

    fun traverse(block: (Node) -> Unit) {
        block(this)
        children.forEach {
            it.traverse(block)
        }
    }

    open fun collectTag(result: MutableList<Node>, tag: String, value: String? = null) {
        if (tags.hasTag(tag, value)) {
            result += this
        }
        for (i in children.indices) {
            children[i].collectTag(result, tag, value)
        }
    }

    inline fun <reified T> findParentOfType(): T? {
        var p = parent
        while (p != null && p !is T) {
            p = p.parent
        }
        return p as? T
    }

    fun onUpdate(block: (RenderPass.UpdateEvent) -> Unit) {
        onUpdate += block
    }

    fun Node.makeChildName(suffix: String): String {
        return "$name/${UniqueId.nextId(suffix)}"
    }

    override fun toString(): String {
        return "${this::class.simpleName}(name=$name)"
    }

    companion object {
        fun makeNodeName(type: String = "Node") = UniqueId.nextId(type)
    }
}

// intentionally not a value class to avoid continuous boxing when used as a map key
data class NodeId(val value: Long = UniqueId.nextId())

fun Node.addGroup(name: String? = null, block: Node.() -> Unit): Node {
    val tg = Node(name)
    tg.block()
    addNode(tg)
    return tg
}
