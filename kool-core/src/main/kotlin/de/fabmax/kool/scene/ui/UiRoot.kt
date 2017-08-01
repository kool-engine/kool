package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.shading.BlurredBackgroundHelper
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.util.RayTest

fun embeddedUi(contentHeight: SizeSpec?, dpi: Float = 300f, block: UiRoot.() -> Unit): UiRoot {
    val ui = UiRoot(dpi)
    ui.contentHeight = contentHeight
    ui.block()
    return ui
}

fun uiScene(dpi: Float = 96f, overlay: Boolean = true, block: UiRoot.() -> Unit): Scene {
    return scene {
        camera = OrthographicCamera().apply { clipToViewport = true }

        if (overlay) {
            clearMask = 0
        }

        +embeddedUi(null, dpi) {
            isFillViewport = true
            this.block()
        }
    }
}

/**
 * @author fabmax
 */
class UiRoot(val uiDpi: Float, name: String = "UiRoot") : Node(name) {

    var globalWidth = 1f
        set(value) {
            if (value != field) {
                field = value
                isLayoutNeeded = true
            }
        }
    var globalHeight = 1f
        set(value) {
            if (value != field) {
                field = value
                isLayoutNeeded = true
            }
        }
    var globalDepth = 1f
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

    var theme = UiTheme.DARK
        set(value) {
            if (value != field) {
                field = value
                content.requestThemeUpdate()
            }
        }
    var shaderLightModel = LightModel.NO_LIGHTING

    val content = UiContainer("$name-content", this)
    var contentHeight: SizeSpec? = null
        set(value) {
            field = value
            isLayoutNeeded = true
        }

    private var blurHelper: BlurredBackgroundHelper? = null

    private var isLayoutNeeded = true

    init {
        content.parent = this
        content.layoutSpec.setSize(pcs(100f), pcs(100f), pcs(100f))
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        content.scene = newScene
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

    override fun render(ctx: RenderContext) {
        if (isFillViewport &&
                (globalWidth != ctx.viewportWidth.toFloat() || globalHeight != ctx.viewportHeight.toFloat())) {
            globalWidth = ctx.viewportWidth.toFloat()
            globalHeight = ctx.viewportHeight.toFloat()
        }

        if (isLayoutNeeded) {
            isLayoutNeeded = false

            var contentScale = 1f
            val ch = contentHeight
            if (ch != null) {
                contentScale = 1f / (ch.toUnits(globalHeight, uiDpi) / globalHeight)
                content.scale(contentScale, contentScale, contentScale)
            }

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
        blurHelper?.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        super.rayTest(test)
        content.rayTest(test)
    }

    operator fun Node.unaryPlus() = content.addNode(this)

    fun component(name: String, block: UiComponent.() -> Unit) = UiComponent(name, this).apply(block)

    fun container(name: String, block: UiContainer.() -> Unit) = UiContainer(name, this).apply(block)

    fun button(name: String, block: Button.() -> Unit) = Button(name, this).apply(block)

    fun label(name: String, block: Label.() -> Unit) = Label(name, this).apply(block)

    fun slider(name: String, block: Slider.() -> Unit) = slider(name, 0f, 100f, 50f, block)
    fun slider(name: String, min: Float, max: Float, value: Float, block: Slider.() -> Unit) =
            Slider(name, min, max, value, this).apply(block)

    fun textField(name: String, block: TextField.() -> Unit) = TextField(name, this).apply(block)

    fun toggleButton(name: String, block: ToggleButton.() -> Unit) = ToggleButton(name, this).apply(block)
}
