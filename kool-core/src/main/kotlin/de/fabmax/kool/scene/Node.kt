package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext

/**
 * A scene node. This is the base class for all scene objects.
 *
 * @author fabmax
 */
abstract class Node {
    /**
     * Determines the visibility of this node. If visible is false. this node will be skipped on
     * rendering. Sub-classes which have sub-nodes (such as [Group]) must evaluate the
     * visibility of their sub-nodes.
     */
    var isVisible = true

    /**
     * Renders this node using the specified graphics engine context.
     *
     * @param ctx    the graphics engine context
     */
    abstract fun render(ctx: RenderContext)

    /**
     * Frees all resources occupied by this Node.
     *
     * @param ctx    the graphics engine context
     */
    abstract fun delete(ctx: RenderContext)
}
