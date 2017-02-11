package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.MutableVec3f

/**
 * A scene node. This is the base class for all scene objects.
 *
 * @author fabmax
 */
abstract class Node(val name: String? = null) {
    /**
     * Parent node is set when this node is added to a [TransformGroup]
     */
    var parent: Node? = null

    /**
     * Determines the visibility of this node. If visible is false this node will be skipped on
     * rendering.
     */
    var isVisible = true

    /**
     * Renders this node using the specified graphics engine context. Implementations should consider the [isVisible]
     * flag and return without drawing anything if it is false.
     *
     * @param ctx    the graphics engine context
     */
    open fun render(ctx: RenderContext) { }

    /**
     * Frees all resources occupied by this Node.
     *
     * @param ctx    the graphics engine context
     */
    open fun delete(ctx: RenderContext) { }

    /**
     * Transforms vec in-place from local to global coordinates.
     */
    open fun toGlobalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        parent?.toGlobalCoords(vec, w)
        return vec
    }

    /**
     * Transforms vec in-place from global to local coordinates.
     */
    open fun toLocalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        parent?.toLocalCoords(vec)
        return vec
    }

    /**
     * Searches for a node with the specified name. Returns null if no such node is found.
     */
    open fun findByName(name: String): Node? {
        if (name == this.name) {
            return this
        }
        return null
    }
}
