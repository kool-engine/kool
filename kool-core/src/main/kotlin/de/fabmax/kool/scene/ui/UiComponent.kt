package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.RayTest

/**
 * Base class for all UI components.
 *
 * @author fabmax
 */
open class UiComponent(name: String) : Group(name), UiNode {

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

    protected var isUpdateNeeded = true

    var background: Background? = null
        set(value) {
            val prev = field
            if (prev != null) {
                this -= prev
                // fixme: possible resource leak: prev.dispose(ctx), but we don't have ctx here...
            }
            if (value != null) {
                // add background at index 0 (draw it before any other content)
                addNode(value, 0)
            }
            field = value
        }

    fun setupBuilder(builder: MeshBuilder) {
        builder.clear()
        builder.identity()
        builder.translate(contentBounds.min)
    }

    override fun onLayout(bounds: BoundingBox, ctx: RenderContext) {
        if (!contentBounds.isEqual(bounds)) {
            contentBounds.set(bounds)
            isUpdateNeeded = true
        }
    }

    override fun render(ctx: RenderContext) {
        if (isUpdateNeeded) {
            update(ctx)
        }
        super.render(ctx)
    }

    protected open fun update(ctx: RenderContext) {
        isUpdateNeeded = false
        background?.drawBackground(ctx)
    }

    override fun rayTest(test: RayTest) {
        val hitNode = test.hitNode
        super.rayTest(test)
        if (hitNode != test.hitNode) {
            // an element of this component was hit!
            test.hitNode = this
            test.hitPositionLocal.subtract(bounds.min)
        }
    }
}
