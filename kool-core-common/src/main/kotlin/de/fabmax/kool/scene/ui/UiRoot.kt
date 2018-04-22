package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.GL_DEPTH_BUFFER_BIT
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.shading.BlurredBackgroundHelper
import de.fabmax.kool.shading.LightModel

fun embeddedUi(contentHeight: SizeSpec?, dpi: Float = 300f, block: UiRoot.() -> Unit): UiRoot {
    val ui = UiRoot(dpi)
    ui.contentHeight = contentHeight
    ui.block()
    return ui
}

fun uiScene(dpi: Float = 96f, overlay: Boolean = true, block: UiRoot.() -> Unit): Scene {
    return scene {
        camera = OrthographicCamera().apply {
            isClipToViewport = true
            near = -1000f
            far = 1000f
        }

        if (overlay) {
            clearMask = GL_DEPTH_BUFFER_BIT
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

    override fun preRender(ctx: KoolContext) {
        if (isFillViewport &&
                (globalWidth != ctx.viewport.width.toFloat() || globalHeight != ctx.viewport.height.toFloat())) {
            globalWidth = ctx.viewport.width.toFloat()
            globalHeight = ctx.viewport.height.toFloat()
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

        content.preRender(ctx)
        bounds.set(content.bounds)
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        blurHelper?.updateDistortionTexture(this, ctx, content.bounds)

        ctx.pushAttributes()
        //ctx.isDepthMask = false
        ctx.isCullFace = false
        ctx.applyAttributes()

        super.render(ctx)
        content.render(ctx)

        ctx.popAttributes()
    }

    override fun postRender(ctx: KoolContext) {
        content.postRender(ctx)
        super.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        content.dispose(ctx)
        blurHelper?.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        super.rayTest(test)
        content.rayTest(test)
    }

    operator fun Node.unaryPlus() = content.addNode(this)

    operator fun DrawerMenu.unaryPlus() {
        content.addNode(this)
        content.addNode(menuButton)
    }

    fun component(name: String, block: UiComponent.() -> Unit) = UiComponent(name, this).apply(block)

    fun container(name: String, block: UiContainer.() -> Unit) = UiContainer(name, this).apply(block)

    fun drawerMenu(name: String, title: String? = null, width: SizeSpec = dps(250f, true), block: DrawerMenu.() -> Unit) = DrawerMenu(width, title, name, this).apply(block)

    fun button(name: String, block: Button.() -> Unit) = Button(name, this).apply(block)

    fun label(name: String, block: Label.() -> Unit) = Label(name, this).apply(block)

    fun slider(name: String, block: Slider.() -> Unit) = slider(name, 0f, 100f, 50f, block)
    fun slider(name: String, min: Float, max: Float, value: Float, block: Slider.() -> Unit) =
            Slider(name, min, max, value, this).apply(block)

    fun textField(name: String, block: TextField.() -> Unit) = TextField(name, this).apply(block)

    fun toggleButton(name: String, block: ToggleButton.() -> Unit) = ToggleButton(name, this).apply(block)
}
