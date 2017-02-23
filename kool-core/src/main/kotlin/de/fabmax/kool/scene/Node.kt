package de.fabmax.kool.scene

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

    var onRender: (Node.(RenderContext) -> Unit)? = null
    var onHoverEnter: (Node.(RenderContext) -> Unit)? = null
    var onHoverExit: (Node.(RenderContext) -> Unit)? = null

    /**
     * Axis-aligned bounds of this node, implementations should set and refresh their bounds on every frame
     * if applicable.
     */
    open val bounds = BoundingBox()

    /**
     * Parent node is set when this node is added to a [TransformGroup]
     */
    var parent: Node? = null

    /**
     * Determines the visibility of this node. If visible is false this node will be skipped on
     * rendering. Implementations must consider this flag in order to get the expected behaviour.
     */
    var isVisible = true

    /**
     * Determines whether this node is considered for ray-picking tests.
     */
    var isPickable = true

    /**
     * Renders this node using the specified graphics engine context. Implementations should consider the [isVisible]
     * flag and return without drawing anything if it is false.
     *
     * @param ctx    the graphics engine context
     */
    open fun render(ctx: RenderContext) {
        onRender?.invoke(this, ctx)
    }

    /**
     * Frees all resources occupied by this Node.
     *
     * @param ctx    the graphics engine context
     */
    open fun delete(ctx: RenderContext) { }

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
}
