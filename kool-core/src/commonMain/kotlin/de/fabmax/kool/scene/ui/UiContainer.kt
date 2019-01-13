package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.BoundingBox

/**
 * @author fabmax
 */

open class UiContainer(name: String, root: UiRoot) : UiComponent(name, root) {

    protected val posInParent = MutableVec3f()
    var contentScale = 1f
    var customTransform: TransformGroup.() -> Unit = { }

    val scrollOffset = MutableVec3f()

    private val tmpChildBounds = BoundingBox()
    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()

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

    override fun update(ctx: KoolContext) {
        updateTransform()

        super.update(ctx)

        for (i in children.indices) {
            val child = children[i]
            if (child is UiComponent) {
                child.update(ctx)
            }
        }
    }

    override fun doLayout(layoutBounds: BoundingBox, ctx: KoolContext) {
        applyBounds(layoutBounds, ctx)

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

    override fun setDrawBoundsFromWrappedComponentBounds(parentContainer: UiContainer?, ctx: KoolContext) {
        super.setDrawBoundsFromWrappedComponentBounds(parentContainer, ctx)
        if (!drawBounds.isEmpty) {
            drawBounds.setWithOffset(drawBounds, scrollOffset)
            syncNodeBounds(ctx)
        } else {
            bounds.clear()
        }
    }

    protected open fun applyBounds(bounds: BoundingBox, ctx: KoolContext) {
        if (!bounds.size.isFuzzyEqual(componentBounds.size) || !bounds.min.isFuzzyEqual(posInParent)) {
            posInParent.set(bounds.min)
            updateTransform()
            componentBounds.set(Vec3f.ZERO, bounds.size)
            drawBounds.set(componentBounds)
            syncNodeBounds(ctx)
            requestUiUpdate()
        }
    }

    protected open fun syncNodeBounds(ctx: KoolContext) {
        tmpVec1.set(drawBounds.min)
        tmpVec2.set(drawBounds.max)
        transform.transform(tmpVec1)
        transform.transform(tmpVec2)

        bounds.set(tmpVec1, tmpVec2)
    }

    protected open fun updateTransform() {
        setIdentity()
                .scale(contentScale, contentScale, contentScale)
                .translate(posInParent)
                .translate(scrollOffset.x, scrollOffset.y, scrollOffset.z)
                .customTransform()
    }

    protected open fun computeChildLayoutBounds(result: BoundingBox, child: UiComponent, ctx: KoolContext) {
        var x = child.layoutSpec.x.toUnits(componentBounds.size.x, dpi)
        var y = child.layoutSpec.y.toUnits(componentBounds.size.y, dpi)
        var z = child.layoutSpec.z.toUnits(componentBounds.size.z, dpi)
        val w = child.layoutSpec.width.toUnits(componentBounds.size.x, dpi)
        val h = child.layoutSpec.height.toUnits(componentBounds.size.y, dpi)
        val d = child.layoutSpec.depth.toUnits(componentBounds.size.z, dpi)

        if (x < 0) { x += width }
        if (y < 0) { y += height }
        if (z < 0) { z += depth }

        result.set(x, y, z, x + w, y + h, z + d)
    }

    private fun BoundingBox.setWithOffset(set: BoundingBox, offset: Vec3f) {
        set(set.min.x - offset.x, set.min.y - offset.y, set.min.z - offset.z,
                set.max.x - offset.x, set.max.y - offset.y, set.max.z - offset.z)
    }
}
