package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Vec3f

/**
 * @author fabmax
 */

open class UiLayout(name: String? = null) : TransformGroup(name), UiNode {

    override var layoutSpec = LayoutSpec()
    override val contentBounds = BoundingBox()

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
        var x = child.layoutSpec.x.toUnits(contentBounds.size.x, ctx.screenDpi)
        var y = child.layoutSpec.y.toUnits(contentBounds.size.y, ctx.screenDpi)
        var z = child.layoutSpec.z.toUnits(contentBounds.size.z, ctx.screenDpi)
        val w = child.layoutSpec.width.toUnits(contentBounds.size.x, ctx.screenDpi)
        val h = child.layoutSpec.height.toUnits(contentBounds.size.y, ctx.screenDpi)
        val d = child.layoutSpec.depth.toUnits(contentBounds.size.z, ctx.screenDpi)

        if (x < 0) { x += width }
        if (y < 0) { y += height }
        if (z < 0) { z += depth }

        result.set(x, y, z, x + w, y + h, z + d)
    }
}
