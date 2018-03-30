package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox

/**
 * @author fabmax
 */

open class UiContainer(name: String, root: UiRoot) : UiComponent(name, root) {

    val posInParent = MutableVec3f()
    private var isLayoutNeeded = true
    private val tmpChildBounds = BoundingBox()

    fun requestLayout() {
        isLayoutNeeded = true
    }

    override fun updateTheme(ctx: KoolContext) {
        super.updateTheme(ctx)
        for (i in children.indices) {
            val child = children[i]
            if (child is UiComponent) {
                child.requestThemeUpdate()
            }
        }
    }

    override fun updateComponentAlpha() {
        super.updateComponentAlpha()
        for (i in children.indices) {
            val child = children[i]
            if (child is UiComponent) {
                child.alpha = alpha
            }
        }
    }

    override fun preRender(ctx: KoolContext) {
        if (isLayoutNeeded) {
            isLayoutNeeded = false
            doLayout(contentBounds, ctx)
        }
        super.preRender(ctx)
    }

    override fun doLayout(bounds: BoundingBox, ctx: KoolContext) {
        applyBounds(bounds, ctx)
        for (i in children.indices) {
            val child = children[i]
            if (child is UiComponent) {
                computeChildLayoutBounds(tmpChildBounds, child, ctx)
                child.doLayout(tmpChildBounds, ctx)
            }
        }
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.containerUi(this)
    }

    protected open fun applyBounds(bounds: BoundingBox, ctx: KoolContext) {
        if (!bounds.size.isFuzzyEqual(contentBounds.size) || !bounds.min.isFuzzyEqual(contentBounds.min)) {
            posInParent.set(bounds.min)
            setIdentity().translate(bounds.min)
            contentBounds.set(Vec3f.ZERO, bounds.size)
            requestUiUpdate()
        }
    }

    protected open fun computeChildLayoutBounds(result: BoundingBox, child: UiComponent, ctx: KoolContext) {
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
