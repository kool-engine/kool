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
    val dpi: Float
        get() = root?.uiDpi ?: 96f

    override var alpha = 1f

    protected var isUpdateNeeded = true
    protected var isThemeApplied = false

    val background: ThemeOrCustomProp<Background?> = ThemeOrCustomProp(null)

    fun setupBuilder(builder: MeshBuilder) {
        builder.clear()
        builder.identity()
        builder.translate(contentBounds.min)
    }

    override fun render(ctx: RenderContext) {
        if (!isThemeApplied) {
            isThemeApplied = true
            val root = root
            if (root != null) {
                applyTheme(root.theme, ctx)
            }
        }

        if (isUpdateNeeded) {
            isUpdateNeeded = false
            update(ctx)
        }

        super.render(ctx)
    }

    protected open fun update(ctx: RenderContext) {
        if (!background.isThemeSet) {
            background.setTheme(createThemeBackground(ctx))
        }
        if (background.isUpdate) {
            val prev = background.prop
            if (prev != null) {
                prev.dispose(ctx)
                this -= prev
            }
            val prop = background.apply()
            if (prop != null) {
                this.addNode(prop, 0)
            }
        }
        background.prop?.update(ctx)
    }

    override fun doLayout(bounds: BoundingBox, ctx: RenderContext) {
        if (!contentBounds.isEqual(bounds)) {
            contentBounds.set(bounds)
            isUpdateNeeded = true
        }
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        background.setTheme(createThemeBackground(ctx))
        isUpdateNeeded = true
    }

    protected open fun createThemeBackground(ctx: RenderContext): Background? {
        return root?.theme?.componentBackground?.invoke(this)
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
