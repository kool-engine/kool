package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.shading.BlurredBackgroundHelper
import de.fabmax.kool.util.BoundingBox

/**
 * @author fabmax
 */
class UiRoot(val uiDpi: Float = 96f, name: String = "UiRoot") : UiContainer(name) {

    override var root: UiRoot? = this

    var globalWidth = 10f
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }
    var globalHeight = 10f
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }
    var globalDepth = 10f
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    var isFillViewport = false
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    var theme = UiTheme.DEFAULT
        set(value) {
            if (value != field) {
                field = value
                isApplyThemeNeeded = true
            }
        }

    private var blurHelper: BlurredBackgroundHelper? = null

    private var isApplyThemeNeeded = false
    private var contentScale = 1f

    fun getBlurHelper(): BlurredBackgroundHelper {
        val helper = blurHelper ?: BlurredBackgroundHelper()
        if (blurHelper == null) {
            blurHelper = helper
        }
        return helper
    }

    fun setGlobalSize(width: Float, height: Float, depth: Float) {
        isFillViewport = false
        this.globalWidth = width
        this.globalHeight = height
        this.globalDepth = depth
    }

    fun scaleContentTo(scaledContentHeight: SizeSpec) {
        contentScale = 1f / (scaledContentHeight.toUnits(globalHeight, uiDpi) / globalHeight)
        scale(contentScale, contentScale, contentScale)
    }

    override fun render(ctx: RenderContext) {
        if (isFillViewport &&
                (globalWidth != ctx.viewportWidth.toFloat() || globalHeight != ctx.viewportHeight.toFloat())) {
            globalWidth = ctx.viewportWidth.toFloat()
            globalHeight = ctx.viewportHeight.toFloat()
        }

        if (isLayoutNeeded) {
            contentBounds.set(0f, 0f, 0f,
                    globalWidth / contentScale, globalHeight / contentScale, globalDepth / contentScale)
        }

        if (isApplyThemeNeeded) {
            isApplyThemeNeeded = false
            applyTheme(theme, ctx)
        }
        blurHelper?.updateDistortionTexture(this, ctx, contentBounds)

        ctx.pushAttributes()
        ctx.isDepthMask = false
        ctx.isCullFace = false
        ctx.applyAttributes()
        super.render(ctx)
        ctx.popAttributes()
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        this.theme = theme
        super.applyTheme(theme, ctx)
    }

    override fun applyBounds(bounds: BoundingBox, ctx: RenderContext) {
        // Normal layouts set their transform matrix according to given bounds, for the root node this would mess up
        // the UI position in global space so don't do anything
    }
}
