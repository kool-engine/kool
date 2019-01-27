package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Ray
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

    // bounds of this component in local coordinates
    val componentBounds = BoundingBox()

    // drawBounds contain the clipped component bounds (also in local coordinates), everything out of draw bounds
    // should be discarded during rendering regular Node.bounds also contain the drawBounds but in parent coordinates
    val drawBounds = BoundingBox()

    val posX: Float get() = componentBounds.min.x
    val posY: Float get() = componentBounds.min.y
    val posZ: Float get() = componentBounds.min.z
    val width: Float get() = componentBounds.size.x
    val height: Float get() = componentBounds.size.y
    val depth: Float get() = componentBounds.size.z

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
        builder.translate(componentBounds.min)
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

        setDrawBoundsFromWrappedComponentBounds(parent as? UiContainer, ctx)
    }

    protected open fun setDrawBoundsFromWrappedComponentBounds(parentContainer: UiContainer?, ctx: KoolContext) {
        if (parentContainer != null) {
            val bndMinX = max(componentBounds.min.x, parentContainer.drawBounds.min.x)
            val bndMinY = max(componentBounds.min.y, parentContainer.drawBounds.min.y)
            val bndMinZ = max(componentBounds.min.z, parentContainer.drawBounds.min.z)

            val bndMaxX = min(componentBounds.max.x, parentContainer.drawBounds.max.x)
            val bndMaxY = min(componentBounds.max.y, parentContainer.drawBounds.max.y)
            val bndMaxZ = min(componentBounds.max.z, parentContainer.drawBounds.max.z)

            if (bndMinX >= bndMaxX || bndMinY >= bndMaxY || bndMinZ >= bndMaxZ) {
                drawBounds.clear()
            } else {
                drawBounds.set(bndMinX, bndMinY, bndMinZ, bndMaxX, bndMaxY, bndMaxZ)
            }
        } else {
            drawBounds.set(componentBounds)
        }
        bounds.set(drawBounds)
    }

    override fun setLocalBounds() {
        bounds.set(drawBounds)
    }

    protected open fun setThemeProps(ctx: KoolContext) {
        // no props to set
    }

    protected open fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.componentUi(this)
    }

    override fun render(ctx: KoolContext) {
        if (isVisible && alpha > 0f && !bounds.isEmpty) {
            ui.prop.onRender(ctx)
            super.render(ctx)
        }
    }

    open fun doLayout(layoutBounds: BoundingBox, ctx: KoolContext) {
        if (!componentBounds.isFuzzyEqual(layoutBounds)) {
            componentBounds.set(layoutBounds)
            requestUiUpdate()
        }
        setDrawBoundsFromWrappedComponentBounds(parent as? UiContainer, ctx)
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

    fun computeLocalPickRay(pointer: InputManager.Pointer, ctx: KoolContext, result: Ray): Boolean {
        val success = scene?.computeRay(pointer, ctx, result) ?: false
        if (success) {
            toLocalCoords(result.origin)
            toLocalCoords(result.direction, 0f).norm()
        }
        return success
    }
}
