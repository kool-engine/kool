package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.shading.BlurredBackgroundHelper
import de.fabmax.kool.util.RayTest

/**
 * @author fabmax
 */
class UiRoot(val uiDpi: Float = 96f, name: String = "UiRoot") : Node(name) {

    var globalWidth = 10f
        set(value) {
            if (value != field) {
                field = value
                isLayoutNeeded = true
            }
        }
    var globalHeight = 10f
        set(value) {
            if (value != field) {
                field = value
                isLayoutNeeded = true
            }
        }
    var globalDepth = 10f
        set(value) {
            if (value != field) {
                field = value
                isLayoutNeeded = true
            }
        }

    var isFillViewport = false
        set(value) {
            if (value != field) {
                field = value
                isLayoutNeeded = true
            }
        }

    var theme = UiTheme.DEFAULT
        set(value) {
            if (value != field) {
                field = value
                content.requestThemeUpdate()
            }
        }

    val content = UiContainer("$name-content", this)

    private var blurHelper: BlurredBackgroundHelper? = null

    private var isLayoutNeeded = true
    private var contentScale = 1f

    override var scene: Scene?
        get() = super.scene
        set(value) {
            super.scene = value
            content.scene = value
        }

    init {
        content.parent = this
    }

    fun createBlurHelper(): BlurredBackgroundHelper {
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
        content.scale(contentScale, contentScale, contentScale)
        isLayoutNeeded = true
    }

    override fun render(ctx: RenderContext) {
        if (isFillViewport &&
                (globalWidth != ctx.viewportWidth.toFloat() || globalHeight != ctx.viewportHeight.toFloat())) {
            globalWidth = ctx.viewportWidth.toFloat()
            globalHeight = ctx.viewportHeight.toFloat()
        }

        if (isLayoutNeeded) {
            isLayoutNeeded = false
            content.contentBounds.set(0f, 0f, 0f,
                    globalWidth / contentScale, globalHeight / contentScale, globalDepth / contentScale)
            content.requestLayout()
        }

        blurHelper?.updateDistortionTexture(this, ctx, content.bounds)

        ctx.pushAttributes()
        ctx.isDepthMask = false
        ctx.isCullFace = false
        ctx.applyAttributes()

        super.render(ctx)
        content.render(ctx)
        bounds.set(content.bounds)

        ctx.popAttributes()
    }

    override fun dispose(ctx: RenderContext) {
        super.dispose(ctx)
        content.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        super.rayTest(test)
        content.rayTest(test)
    }
}
