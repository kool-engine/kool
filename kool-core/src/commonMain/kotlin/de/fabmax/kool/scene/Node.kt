package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.*

/**
 * A scene node. Scene nodes have a [transform], which controls the position, orientation and size of the node and all
 * its child-nodes.
 * Moreover, this is the base class for all other scene objects (e.g. [Mesh]). On its own, a Node acts as a group for
 * an arbitrary number of child-nodes.
 *
 * @author fabmax
 */
open class Node(name: String? = null) : Disposable {

    var name: String = name ?: getDefaultName()

    val onUpdate: MutableList<(RenderPass.UpdateEvent) -> Unit> = mutableListOf()
    val onDispose: MutableList<(KoolContext) -> Unit> = mutableListOf()

    val tags = Tags()

    protected val childrenBounds = BoundingBox()
    protected val mutChildren = mutableListOf<Node>()
    val children: List<Node> get() = mutChildren

    /**
     * Axis-aligned bounding box of this node in parent coordinate frame.
     */
    val bounds = BoundingBox()
    private val tmpTransformVec = MutableVec3f()

    /**
     * Center point of this node's bounds in global coordinates.
     */
    val globalCenter: Vec3f get() = globalCenterMut

    /**
     * Radius of this node's bounding sphere in global coordinates.
     */
    var globalRadius = 0f
        protected set

    private val globalCenterMut = MutableVec3f()
    private val globalExtentMut = MutableVec3f()

    /**
     * This node's transform. Can be used to manipulate this node's position, size, etc. Notice that, by default, the
     * transform is set to [TrsTransformF], which treats position, rotation and scale as separate independent properties.
     * As an alternative, you can also use [MatrixTransformF], which applies all transform operations directly to a 4x4
     * transform matrix.
     */
    var transform: Transform = TrsTransformF()

    private val modelMats = ModelMats()

    /**
     * This node's single-precision model matrix. Updated on each frame based on this node's transform and the model
     * matrix of the parent node.
     */
    val modelMatF: Mat4f by modelMats::modelMatF

    /**
     * This node's double-precision model matrix. Actual double-precision is only achieved, if this node also uses a
     * double precision [transform]. Updated on each frame based on this node's transform and the model
     * matrix of the parent node.
     */
    val modelMatD: Mat4d by modelMats::modelMatD

    /**
     * Inverse of this node's model matrix (single-precision).
     */
    val invModelMatF: Mat4d get() = modelMats.lazyInvModelMatD.get()

    /**
     * Inverse of this node's model matrix (double-precision).
     */
    val invModelMatD: Mat4d get() = modelMats.lazyInvModelMatD.get()

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
     * Determines whether this node updates its scene bounds on update. Default is true, but disabling it can save
     * some performance in scenes with many nodes. Notice that [isUpdateBounds] has to be true for ray picking to work.
     */
    var isUpdateBounds = true

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
        for (i in onUpdate.indices) {
            onUpdate[i](updateEvent)
        }

        updateModelMat()

        childrenBounds.clear()
        for (i in mutChildren.indices) {
            mutChildren[i].update(updateEvent)
            if (isUpdateBounds) {
                childrenBounds.add(mutChildren[i].bounds)
            }
        }
        if (isUpdateBounds) {
            computeLocalBounds(bounds)
        }

        // update global center and radius
        toGlobalCoords(globalCenterMut.set(bounds.center))
        toGlobalCoords(globalExtentMut.set(bounds.max))
        globalRadius = globalCenter.distance(globalExtentMut)

        // transform node bounds from local to parent coordinates
        if (isUpdateBounds) {
            transformBoundsToParentFrame()
        }
    }

    private fun transformBoundsToParentFrame() {
        if (!bounds.isEmpty) {
            val minX = bounds.min.x
            val minY = bounds.min.y
            val minZ = bounds.min.z
            val maxX = bounds.max.x
            val maxY = bounds.max.y
            val maxZ = bounds.max.z

            bounds.clear()
            bounds.add(transform.transform(tmpTransformVec.set(minX, minY, minZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(minX, minY, maxZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(minX, maxY, minZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(minX, maxY, maxZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(maxX, minY, minZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(maxX, minY, maxZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(maxX, maxY, minZ), 1f))
            bounds.add(transform.transform(tmpTransformVec.set(maxX, maxY, maxZ), 1f))
        }
    }

    protected open fun computeLocalBounds(result: BoundingBox) {
        result.set(childrenBounds)
    }

    fun updateModelMat() {
        transform.applyToModelMat(parent?.modelMats, modelMats)
    }

    /**
     * Called on a per-frame basis, when the draw queue is built. The actual number of times this method
     * is called per frame depends on various factors (number of render passes, object visibility, etc.).
     * When this message is called, implementations can, but don't have to, append a DrawCommand to the provided
     * DrawQueue.
     */
    open fun collectDrawCommands(updateEvent: RenderPass.UpdateEvent) {
        isRendered = checkIsVisible(updateEvent.camera, updateEvent.ctx)
        if (isRendered) {
            for (i in mutChildren.indices) {
                mutChildren[i].collectDrawCommands(updateEvent)
            }
        }
    }

    /**
     * Frees all resources occupied by this Node.
     *
     * @param ctx    the graphics engine context
     */
    override fun dispose(ctx: KoolContext) {
        onDispose.forEach { it(ctx) }
        children.forEach { it.dispose(ctx) }
    }

    /**
     * Transforms [vec] in-place from local to global coordinates.
     */
    fun toGlobalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        modelMatF.transform(vec, w)
        return vec
    }

    fun toGlobalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        modelMatD.transform(vec, w)
        return vec
    }

    /**
     * Transforms [vec] in-place from global to local coordinates.
     */
    fun toLocalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        invModelMatF.transform(vec, w)
        return vec
    }

    fun toLocalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        invModelMatD.transform(vec, w)
        return vec
    }

    /**
     * Performs a hit test with the given [RayTest]. Subclasses should override this method and test
     * if their contents are hit by the ray.
     */
    open fun rayTest(test: RayTest) {
        if (children.isNotEmpty()) {
            // transform ray to local coordinates
            if (transform.isDoublePrecision) {
                test.transformBy(transform.invMatrixD)
            } else {
                test.transformBy(transform.invMatrixF)
            }

            for (i in mutChildren.indices) {
                val child = mutChildren[i]
                if (child.isVisible && child.isPickable) {
                    val d = child.bounds.hitDistanceSqr(test.ray)
                    if (d < Float.MAX_VALUE && d <= test.hitDistanceSqr) {
                        child.rayTest(test)
                    }
                }
            }

            // transform ray back to previous coordinates
            if (transform.isDoublePrecision) {
                test.transformBy(transform.matrixD)
            } else {
                test.transformBy(transform.matrixF)
            }
        }
    }

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

    fun onDispose(block: (KoolContext) -> Unit) {
        onDispose += block
    }

    private fun getDefaultName(): String {
        return UniqueId.nextId(this::class.simpleName ?: "unknown")
    }

    class ModelMats {
        val modelMatF: Mat4f get() {
            if (updateIdF != updateId) {
                mutModelMatF.set(mutModelMatD)
                updateIdF = updateId
            }
            return mutModelMatF
        }

        val modelMatD: Mat4d get() {
            if (updateIdD != updateId) {
                mutModelMatD.set(mutModelMatF)
                updateIdD = updateId
            }
            return mutModelMatD
        }

        val lazyInvModelMatF = LazyMat4f { modelMatF.invert(it) }
        val lazyInvModelMatD = LazyMat4d { modelMatD.invert(it) }

        val mutModelMatF = MutableMat4f()
        val mutModelMatD = MutableMat4d()

        private var updateId = 0
        private var updateIdF = 0
        private var updateIdD = 0

        fun markUpdatedF() {
            updateId++
            updateIdF = updateId
        }

        fun markUpdatedD() {
            updateId++
            updateIdD = updateId
        }
    }
}

fun Node.addGroup(name: String? = null, block: Node.() -> Unit): Node {
    val tg = Node(name)
    tg.block()
    addNode(tg)
    return tg
}
