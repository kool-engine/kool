package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MutableVec3f
import de.fabmax.kool.util.RayTest

/**
 * A scene node. This is the base class for all scene objects.
 *
 * @author fabmax
 */
abstract class Node(val name: String? = null) {

    val onRender: MutableList<Node.(RenderContext) -> Unit> = mutableListOf()

    val onHoverEnter: MutableList<Node.(InputManager.Pointer, RayTest, RenderContext) -> Unit> = mutableListOf()
    val onHover: MutableList<Node.(InputManager.Pointer, RayTest, RenderContext) -> Unit> = mutableListOf()
    val onHoverExit: MutableList<Node.(InputManager.Pointer, RayTest, RenderContext) -> Unit> = mutableListOf()

    /**
     * Axis-aligned bounds of this node, implementations should set and refresh their bounds on every frame
     * if applicable.
     */
    open val bounds = BoundingBox()

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
     * rendering. Implementations must consider this flag in order to get the expected behaviour.
     */
    open var isVisible = true

    /**
     * Determines whether this node is considered for ray-picking tests.
     */
    open var isPickable = true

    /**
     * Renders this node using the specified graphics engine context. Implementations should consider the [isVisible]
     * flag and return without drawing anything if it is false.
     *
     * @param ctx    the graphics engine context
     */
    open fun render(ctx: RenderContext) {
        if (!onRender.isEmpty()) {
            for (i in onRender.indices) {
                onRender[i](ctx)
            }
        }
    }

    /**
     * Frees all resources occupied by this Node.
     *
     * @param ctx    the graphics engine context
     */
    open fun dispose(ctx: RenderContext) { }

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
