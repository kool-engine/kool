package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder
import kotlin.math.max
import kotlin.math.min

/**
 * Base class for all UI components.
 *
 * @author fabmax
 */
open class UiComponent(name: String, val root: UiRoot) : TransformGroup(name) {

    val contentBounds = BoundingBox()
    val drawBounds = BoundingBox()

    val posX: Float get() = contentBounds.min.x
    val posY: Float get() = contentBounds.min.y
    val posZ: Float get() = contentBounds.min.z
    val width: Float get() = contentBounds.size.x
    val height: Float get() = contentBounds.size.y
    val depth: Float get() = contentBounds.size.z

    var layoutSpec = LayoutSpec()
    var padding = Margin(dps(16f), dps(16f), dps(16f), dps(16f))
        set(value) {
            if (field != value) {
                field = value
                isUiUpdate = true
            }
        }

    val ui: ThemeOrCustomProp<ComponentUi> = ThemeOrCustomProp(BlankComponentUi())

    var alpha = 1f
        set(value) {
            if (field != value) {
                field = value
                updateComponentAlpha()
            }
        }

    val dpi: Float
        get() = root.uiDpi

    private var isThemeUpdate = true
    private var isUiUpdate = true

    open fun setupBuilder(builder: MeshBuilder) {
        builder.clear()
        builder.identity()
        builder.translate(contentBounds.min)
    }

    open fun requestThemeUpdate() {
        isThemeUpdate = true
    }

    open fun requestUiUpdate() {
        isUiUpdate = true
    }

    protected open fun updateComponentAlpha() {
        ui.prop.updateComponentAlpha()
    }

    protected open fun updateUi(ctx: KoolContext) {
        ui.prop.updateUi(ctx)
    }

    protected open fun updateTheme(ctx: KoolContext) {
        ui.prop.dispose(ctx)
        ui.setTheme(createThemeUi(ctx)).apply()
        setThemeProps(ctx)
        ui.prop.createUi(ctx)
        ui.prop.updateComponentAlpha()
        requestUiUpdate()
    }

    open fun update(ctx: KoolContext) {
        if (isThemeUpdate) {
            isThemeUpdate = false
            updateTheme(ctx)
        }
        if (isUiUpdate) {
            isUiUpdate = false
            updateUi(ctx)
        }

        val parentContainer = parent
        if (parentContainer is UiContainer) {
            val bndMinX = max(contentBounds.min.x, parentContainer.viewport.min.x)
            val bndMinY = max(contentBounds.min.y, parentContainer.viewport.min.y)
            val bndMinZ = max(contentBounds.min.z, parentContainer.viewport.min.z)

            val bndMaxX = min(contentBounds.max.x, parentContainer.viewport.max.x)
            val bndMaxY = min(contentBounds.max.y, parentContainer.viewport.max.y)
            val bndMaxZ = min(contentBounds.max.z, parentContainer.viewport.max.z)

            if (bndMinX >= bndMaxX || bndMinY >= bndMaxY || bndMinZ >= bndMaxZ) {
                drawBounds.clear()
            } else {
                drawBounds.set(bndMinX, bndMinY, bndMinZ, bndMaxX, bndMaxY, bndMaxZ)
            }
        } else {
            drawBounds.set(contentBounds)
        }
    }

    protected open fun setThemeProps(ctx: KoolContext) {
        // no props to set
    }

    protected open fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.componentUi(this)
    }

    override fun render(ctx: KoolContext) {
        if (alpha > 0f && !drawBounds.isEmpty) {
            ui.prop.onRender(ctx)
            super.render(ctx)
        }
    }

    open fun doLayout(bounds: BoundingBox, ctx: KoolContext) {
        if (!contentBounds.isFuzzyEqual(bounds)) {
            contentBounds.set(bounds)
            requestUiUpdate()
        }
    }

    override fun rayTest(test: RayTest) {
        if (alpha != 0f) {
            val hitNode = test.hitNode
            super.rayTest(test)
            if (hitNode !== test.hitNode && test.hitNode !is UiComponent) {
                // an element of this component (and not a sub-component in case this is a container) was hit!
                test.setHit(this, test.hitPosition)
            }
        }
    }
}
