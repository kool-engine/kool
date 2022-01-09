package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.BoundingBox

/**
 * @author fabmax
 */

open class UiContainer(name: String, root: UiRoot) : UiComponent(name, root) {

    val contentBounds: BoundingBox get() = childrenBounds

    protected val posInParent = MutableVec3f()
    var contentScale = 1f
    var customTransform: Group.() -> Unit = { }

    val scrollOffset: Vec3f get() = scrollOffsetMut
    protected val scrollOffsetMut = MutableVec3f()
    protected var isScrollDirty = true

    private val childComponents = mutableListOf<UiComponent>()
    private var scrollHandler: ScrollHandler? = null

    private val tmpChildBounds = BoundingBox()
    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()

    override fun updateTheme(ctx: KoolContext) {
        super.updateTheme(ctx)
        for (i in childComponents.indices) {
            childComponents[i].requestThemeUpdate()
        }
    }

    override fun updateComponentAlpha() {
        super.updateComponentAlpha()
        for (i in childComponents.indices) {
            childComponents[i].alpha = alpha
        }
    }

    override fun updateComponent(ctx: KoolContext) {
        if (isScrollDirty) {
            isScrollDirty = false
            scrollHandler?.requestUiUpdate()
            updateTransform()
        }

        super.updateComponent(ctx)

        for (i in childComponents.indices) {
            childComponents[i].updateComponent(ctx)
        }
    }

    override fun doLayout(layoutBounds: BoundingBox, ctx: KoolContext) {
        applyBounds(layoutBounds, ctx)

        contentBounds.clear()
        for (i in childComponents.indices) {
            val child = childComponents[i]
            computeChildLayoutBounds(tmpChildBounds, child, ctx)
            contentBounds.add(tmpChildBounds)
            child.doLayout(tmpChildBounds, ctx)
        }
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.containerUi(this)
    }

    override fun setDrawBoundsFromWrappedComponentBounds(parentContainer: UiContainer?, ctx: KoolContext) {
        super.setDrawBoundsFromWrappedComponentBounds(parentContainer, ctx)
        if (!drawBounds.isEmpty) {
            drawBounds.move(scrollOffset)
            syncNodeBounds(ctx)
        } else {
            bounds.clear()
        }
    }

    override fun setLocalBounds() {
        childrenBounds.clear()
        for (i in childComponents.indices) {
            childrenBounds.add(childComponents[i].componentBounds)
        }
        bounds.set(childrenBounds)
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

        val minX: Float
        val maxX: Float
        val minY: Float
        val maxY: Float
        val minZ: Float
        val maxZ: Float
        if (tmpVec1.x < tmpVec2.x) {
            minX = tmpVec1.x
            maxX = tmpVec2.x
        } else {
            minX = tmpVec2.x
            maxX = tmpVec2.x
        }
        if (tmpVec1.y < tmpVec2.y) {
            minY = tmpVec1.y
            maxY = tmpVec2.y
        } else {
            minY = tmpVec2.y
            maxY = tmpVec1.y
        }
        if (tmpVec1.z < tmpVec2.z) {
            minZ = tmpVec1.z
            maxZ = tmpVec2.z
        } else {
            minZ = tmpVec2.z
            maxZ = tmpVec1.z
        }
        bounds.set(minX, minY, minZ, maxX, maxY, maxZ)
    }

    protected open fun updateTransform() {
        setIdentity()
                .scale(contentScale, contentScale, contentScale)
                .translate(posInParent)
                .translate(-scrollOffsetMut.x, -scrollOffsetMut.y, -scrollOffsetMut.z)
                .customTransform()
    }

    protected open fun computeChildLayoutBounds(result: BoundingBox, child: UiComponent, ctx: KoolContext) {
        child.onLayout.forEach { it(ctx) }
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

    fun setScrollOffset(offset: Vec3f) = setScrollOffset(offset.x, offset.y, offset.z)

    fun setScrollOffset(offX: Float, offY: Float, offZ: Float) {
        if (offX != scrollOffsetMut.x || offY != scrollOffsetMut.y || offZ != scrollOffsetMut.z) {
            scrollOffsetMut.set(offX, offY, offZ)
            isScrollDirty = true
        }
    }

    fun requestUpdateTransform() {
        isScrollDirty = true
    }

    override fun addNode(node: Node, index: Int) {
        // if the last component is a ScrollHandler we want it to stay the last one to maintain correct
        // draw order: scrollbars should stay on top of all sub-components
        val idx = if (children.isNotEmpty() && index < 0 && children.last() is ScrollHandler) {
            children.lastIndex
        } else {
            index
        }

        super.addNode(node, idx)
        if (node is UiComponent) {
            childComponents.add(node)

            if (node is ScrollHandler) {
                scrollHandler = node
            }
        }
    }

    override fun removeNode(node: Node): Boolean {
        if (node is UiComponent) {
            childComponents.remove(node)

            if (node == scrollHandler) {
                scrollHandler = null
            }
        }
        return super.removeNode(node)
    }

    override fun removeAllChildren() {
        super.removeAllChildren()
        childComponents.clear()
        scrollHandler = null
    }
}
