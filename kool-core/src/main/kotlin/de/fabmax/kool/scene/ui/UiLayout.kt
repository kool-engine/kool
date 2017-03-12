package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Vec3f

/**
 * @author fabmax
 */

open class UiLayout(name: String) : TransformGroup(name), UiNode {

    override var layoutSpec = LayoutSpec()
    override val contentBounds = BoundingBox()

    override var root: UiRoot? = null
    override var parent: Node?
        get() = super.parent
        set(value) {
            super.parent = value
            if (value == null) {
                root = null
            } else if (value is UiNode) {
                root = value.root
                children.filter { it is UiNode }.forEach { (it as UiNode).root = root }
            }
        }

    protected var isLayoutNeeded = true

    private val childBounds = BoundingBox()

    fun requestLayout() {
        isLayoutNeeded = true
    }

    override fun render(ctx: RenderContext) {
        if (isLayoutNeeded) {
            onLayout(contentBounds, ctx)
        }
        super.render(ctx)
    }

    override fun onLayout(bounds: BoundingBox, ctx: RenderContext) {
        isLayoutNeeded = false
        applyBounds(bounds, ctx)

        for (i in children.indices) {
            val child = children[i]
            if (child is UiNode) {
                computeChildLayoutBounds(childBounds, child, ctx)
                child.onLayout(childBounds, ctx)
            }
        }
    }

    protected open fun applyBounds(bounds: BoundingBox, ctx: RenderContext) {
        setIdentity().translate(bounds.min)
        contentBounds.set(Vec3f.ZERO, bounds.size)
    }

    protected open fun computeChildLayoutBounds(result: BoundingBox, child: UiNode, ctx: RenderContext) {
        val dpi = root?.uiDpi ?: 96f

        var x = child.layoutSpec.x.toUnits(contentBounds.size.x, dpi)
        var y = child.layoutSpec.y.toUnits(contentBounds.size.y, dpi)
        var z = child.layoutSpec.z.toUnits(contentBounds.size.z, dpi)
        val w = child.layoutSpec.width.toUnits(contentBounds.size.x, dpi)
        val h = child.layoutSpec.height.toUnits(contentBounds.size.y, dpi)
        val d = child.layoutSpec.depth.toUnits(contentBounds.size.z, dpi)

        if (x < 0) { x += width }
        if (y < 0) { y += height }
        if (z < 0) { z += depth }

        result.set(x, y, z, x + w, y + h, z + d)
    }
}
