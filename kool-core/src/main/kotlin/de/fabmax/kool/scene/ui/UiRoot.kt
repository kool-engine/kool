package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.RayTest

/**
 * @author fabmax
 */
class UiRoot(name: String = "UiRoot") : UiLayout(name) {

    var globalWidth = 10f
        set(value) {
            if (value != field) {
                field = value
                isResizeNeeded = true
            }
        }
    var globalHeight = 10f
        set(value) {
            if (value != field) {
                field = value
                isResizeNeeded = true
            }
        }
    var globalDepth = 10f
        set(value) {
            if (value != field) {
                field = value
                isResizeNeeded = true
            }
        }

    private var isResizeNeeded = true
    private var contentScale = 1f
    var isFillViewport = false

    fun setGlobalSize(width: Float, height: Float, depth: Float) {
        isFillViewport = false
        this.globalWidth = width
        this.globalHeight = height
        this.globalDepth = depth
    }

    fun scaleContentTo(scaledContentHeight: SizeSpec, dpi: Float) {
        contentScale = 1f / (scaledContentHeight.toUnits(globalHeight, dpi) / globalHeight)
        scale(contentScale, contentScale, contentScale)
    }

    override fun render(ctx: RenderContext) {
        if (isFillViewport && (globalWidth != ctx.viewportWidth.toFloat() || globalHeight != ctx.viewportHeight.toFloat())) {
            globalWidth = ctx.viewportWidth.toFloat()
            globalHeight = ctx.viewportHeight.toFloat()
        }

        if (isResizeNeeded) {
            isResizeNeeded = false
            contentBounds.set(0f, 0f, 0f,
                    globalWidth / contentScale, globalHeight / contentScale, globalDepth / contentScale)

            onLayout(contentBounds, ctx)
        }

        ctx.pushAttributes()
        ctx.isDepthMask = false
        ctx.applyAttributes()

        super.render(ctx)
        ctx.popAttributes()
    }

    override fun applyBounds(bounds: BoundingBox, ctx: RenderContext) {
        // Normal layouts set their transform matrix according to given bounds, for the root node this would mess up
        // the UI position in global space so don't do anything
    }
}
