package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Disposable

/**
 * A scene node. This is the base class for all scene objects.
 *
 * @author fabmax
 */
abstract class Node(val name: String? = null) : Disposable {

    val onUpdate: MutableList<Node.(RenderPass.UpdateEvent) -> Unit> = mutableListOf()
    val onCollectDrawCommands: MutableList<Node.(RenderPass.UpdateEvent) -> Unit> = mutableListOf()
    val onDispose: MutableList<Node.(KoolContext) -> Unit> = mutableListOf()

    val onHoverEnter: MutableList<Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()
    val onHover: MutableList<Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()
    val onHoverExit: MutableList<Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()

    val tags = Tags()

    /**
     * Axis-aligned bounds of this node in local coordinates.
     * Implementations should set and refresh their bounds on every frame if applicable.
     */
    open val bounds = BoundingBox()

    /**
     * Center point of this node's bounds in global coordinates.
     */
    open val globalCenter: Vec3f get() = globalCenterMut

    /**
     * Radius of this node's bounding sphere in global coordinates.
     */
    open var globalRadius = 0f
        protected set

    protected val globalCenterMut = MutableVec3f()
    protected val globalExtentMut = MutableVec3f()

    val modelMat = Mat4d()
    val modelMatInv: Mat4d
        get() = checkModelMatInv()
    protected var modelMatDirty = false
    private val modelMatInvLazy = Mat4d()

    /**
     * Parent node is set when this node is added to a [Group]
     */
    open var parent: Node? = null

    /**
     * Determines the visibility of this node. If visible is false this node will be skipped on
     * rendering.
     */
    open var isVisible = true

    /**
     * Determines whether this node is considered during shadow pass.
     */
    var isCastingShadow = true

    /**
     * Determines whether this node is considered for ray-picking tests.
     */
    open var isPickable = true

    /**
     * Determines whether this node is checked for visibility during rendering. If true the node is only rendered
     * if it is within the camera frustum.
     */
    open var isFrustumChecked = true

    /**
     * Flag indicating if this node should be rendered. The flag is updated in the [collectDrawCommands] method based on
     * the [isVisible] flag and [isFrustumChecked]. I.e. it is false if this node is either explicitly hidden or outside
     * of the camera frustum and frustum checking is enabled.
     */
    protected var isRendered = true

    /**
     * Called once on every new frame before draw commands are collected. Implementations should use this method to
     * update their transform matrices, bounding boxes, animation states, etc.
     */
    open fun update(updateEvent: RenderPass.UpdateEvent) {
        for (i in onUpdate.indices) {
            onUpdate[i](updateEvent)
        }

        updateModelMat()

        // update global center and radius
        globalCenterMut.set(bounds.center)
        globalExtentMut.set(bounds.max)
        modelMat.transform(globalCenterMut)
        modelMat.transform(globalExtentMut)
        globalRadius = globalCenter.distance(globalExtentMut)
    }

    open fun updateModelMat() {
        modelMat.set(parent?.modelMat ?: MODEL_MAT_IDENTITY)
        modelMatDirty = true
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
            for (i in onCollectDrawCommands.indices) {
                onCollectDrawCommands[i](updateEvent)
            }
        }
    }

    /**
     * Frees all resources occupied by this Node.
     *
     * @param ctx    the graphics engine context
     */
    override fun dispose(ctx: KoolContext) {
        for (i in onDispose.indices) {
            onDispose[i](ctx)
        }
    }

    /**
     * Transforms [vec] in-place from local to global coordinates.
     */
    open fun toGlobalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        modelMat.transform(vec, w)
        return vec
    }

    open fun toGlobalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        modelMat.transform(vec, w)
        return vec
    }

    /**
     * Transforms [vec] in-place from global to local coordinates.
     */
    open fun toLocalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        modelMatInv.transform(vec, w)
        return vec
    }

    open fun toLocalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        modelMatInv.transform(vec, w)
        return vec
    }

    /**
     * Performs a hit test with the given [RayTest]. Implementations should override this method and test
     * if their contents are hit by the ray.
     */
    open fun rayTest(test: RayTest) { }

    /**
     * Searches for a node with the specified name. Returns null if no such node is found.
     */
    open operator fun get(name: String): Node? {
        if (name == this.name) {
            return this
        }
        return null
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

    open fun findNode(name: String): Node? = if (name == this.name) { this } else { null }

    inline fun <reified T> findParentOfType(): T? {
        var p = parent
        while (p != null && p !is T) {
            p = p.parent
        }
        return p as T
    }

    private fun checkModelMatInv(): Mat4d {
        if (modelMatDirty) {
            modelMat.invert(modelMatInvLazy)
            modelMatDirty = false
        }
        return modelMatInvLazy
    }

    companion object {
        private val MODEL_MAT_IDENTITY = Mat4d()
    }
}
