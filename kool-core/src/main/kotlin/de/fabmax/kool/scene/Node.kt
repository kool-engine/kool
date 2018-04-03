package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Disposable

/**
 * A scene node. This is the base class for all scene objects.
 *
 * @author fabmax
 */
abstract class Node(val name: String? = null) : Disposable {

    val onPreRender: MutableList<Node.(KoolContext) -> Unit> = mutableListOf()
    val onRender: MutableList<Node.(KoolContext) -> Unit> = mutableListOf()
    val onPostRender: MutableList<Node.(KoolContext) -> Unit> = mutableListOf()
    val onDispose: MutableList<Node.(KoolContext) -> Unit> = mutableListOf()

    val onHoverEnter: MutableList<Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()
    val onHover: MutableList<Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()
    val onHoverExit: MutableList<Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()

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

    private val globalCenterMut = MutableVec3f()
    private val globalExtentMut = MutableVec3f()

    /**
     * Parent node is set when this node is added to a [Group]
     */
    open var parent: Node? = null
        set(value) {
            if (value !== field) {
                onParentChanged(field, value)
                field = value
                scene = findParentOfType()
            }
        }

    open var scene: Scene? = null
        set(value) {
            if (value !== field) {
                onSceneChanged(field, value)
                field = value
            }
        }

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
     * Flag indicating if this node should be rendered. The flag is updated in the [render] method based on
     * the [isVisible] flag and [isFrustumChecked]. I.e. it is false if this node is either explicitly hidden or outside
     * of the camera frustum and frustum checking is enabled.
     */
    protected var isRendered = true

    /**
     * Called before rendering a new frame. This method is only called once per frame, in contrast to render which
     * can be called multiple times (e.g, once in shadow pass and another time in screen pass). Implementations should
     * use this method to update their bounds, animation states, etc.
     */
    open fun preRender(ctx: KoolContext) {
        for (i in onPreRender.indices) {
            onPreRender[i](ctx)
        }

        // update global center and radius
        globalCenterMut.set(bounds.center)
        globalExtentMut.set(bounds.max)
        ctx.mvpState.modelMatrix.transform(globalCenterMut)
        ctx.mvpState.modelMatrix.transform(globalExtentMut)
        globalRadius = globalCenter.distance(globalExtentMut)
    }

    /**
     * Renders this node using the specified graphics engine context. Implementations should consider the [isVisible]
     * flag and return without drawing anything if it is false. This method might be called multiple times per frame,
     * e.g. during shadow depth texture rendering.
     *
     * @param ctx    the graphics engine context
     */
    open fun render(ctx: KoolContext) {
        isRendered = checkIsVisible(ctx)

        if (isRendered) {
            if (!onRender.isEmpty()) {
                for (i in onRender.indices) {
                    onRender[i](ctx)
                }
            }
        }
    }

    /**
     * Called after a frame was rendered.
     */
    open fun postRender(ctx: KoolContext) {
        for (i in onPostRender.indices) {
            onPostRender[i](ctx)
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
        parent?.toGlobalCoords(vec, w)
        return vec
    }

    /**
     * Transforms [vec] in-place from global to local coordinates.
     */
    open fun toLocalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        parent?.toLocalCoords(vec)
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
     * Called during [render]: Checks if this node is currently visible. If not rendering is skipped. Default
     * implementation considers [isVisible] flag and performs a camera frustum check if [isFrustumChecked] is true.
     */
    protected open fun checkIsVisible(ctx: KoolContext): Boolean {
        if (!isVisible) {
            return false
        } else if (isFrustumChecked && !bounds.isEmpty) {
            return scene?.camera?.isInFrustum(this) ?: true
        }
        return true
    }

    inline fun <reified T> findParentOfType(): T? {
        var p = parent
        while (p != null && p !is T) {
            p = p.parent
        }
        return p as T
    }

    /**
     * Called when the scene of this node is changed. Sub-classes can override this method in order to be notified
     * when this node's scene has changed. Overriding this method is better than overriding the property directly
     * because setting a overriden super-class property is super-slow in javascript.
     */
    protected open fun onSceneChanged(oldScene: Scene?, newScene: Scene?) { }

    /**
     * Called when the parent of this node is changed. Sub-classes can override this method in order to be notified
     * when this node's parent has changed. Overriding this method is better than overriding the property directly
     * because setting a overriden super-class property is super-slow in javascript.
     */
    protected open fun onParentChanged(oldParent: Node?, newParent: Node?) { }
}
