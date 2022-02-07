package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene

fun Scene.embeddedUi(width: Float, height: Float, contentHeight: SizeSpec?, dpi: Float = 96f, block: UiRoot.() -> Unit): UiRoot {
    val ui = UiRoot(this)
    ui.uiDpi = dpi
    ui.contentHeight = contentHeight
    ui.globalWidth = width
    ui.globalHeight = height
    ui.block()
    return ui
}

fun uiScene(name: String? = null, overlay: Boolean = true, block: UiRoot.(Scene) -> Unit) = scene(name ?: "uiScene") {
    camera = OrthographicCamera().apply {
        isClipToViewport = true
        near = -1000f
        far = 1000f
    }

    if (overlay) {
        mainRenderPass.clearColor = null
    }

    +embeddedUi(1f, 1f, null, 0f) {
        isFillViewport = true
        this.block(this@scene)
    }
}

/**
 * @author fabmax
 */
class UiRoot(val scene: Scene, name: String = "UiRoot") : Node(name) {

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

    var uiDpi = 0f
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    val content = UiContainer("$name-content", this)
    var contentHeight: SizeSpec? = null
        set(value) {
            field = value
            isLayoutNeeded = true
        }
    override val bounds: BoundingBox
        get() = content.bounds

    private var isLayoutNeeded = true

    init {
        content.parent = this
        content.layoutSpec.setSize(pcs(100f), pcs(100f), pcs(100f))
    }

    fun setGlobalSize(width: Float, height: Float, depth: Float) {
        isFillViewport = false
        this.globalWidth = width
        this.globalHeight = height
        this.globalDepth = depth
    }

    fun requestLayout() {
        isLayoutNeeded = true
    }

    private val dpiChangeListener: (KoolContext) -> Unit = { uiDpi = it.screenDpi }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        val viewport = scene.mainRenderPass.viewport

        if (isFillViewport &&
                (globalWidth != viewport.width.toFloat() || globalHeight != viewport.height.toFloat())) {
            globalWidth = viewport.width.toFloat()
            globalHeight = viewport.height.toFloat()
        }

        if (uiDpi == 0f) {
            uiDpi = updateEvent.ctx.screenDpi
            if (dpiChangeListener !in updateEvent.ctx.onScreenDpiChange) {
                updateEvent.ctx.onScreenDpiChange += dpiChangeListener
            }
        }

        if (isLayoutNeeded) {
            isLayoutNeeded = false

            var contentScale = 1f
            val ch = contentHeight
            if (ch != null) {
                contentScale = 1f / (ch.toUnits(globalHeight, uiDpi) / globalHeight)
            }

            val contentBounds = BoundingBox().set(0f, 0f, 0f,
                    globalWidth / contentScale,
                    globalHeight / contentScale,
                    globalDepth / contentScale)
            content.contentScale = contentScale
            content.doLayout(contentBounds, updateEvent.ctx)
        }

        content.update(updateEvent)
        super.update(updateEvent)

        content.updateComponent(updateEvent.ctx)
    }

    override fun collectDrawCommands(updateEvent: RenderPass.UpdateEvent) {
        super.collectDrawCommands(updateEvent)
        content.collectDrawCommands(updateEvent)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        content.dispose(ctx)
        ctx.onScreenDpiChange -= dpiChangeListener
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
