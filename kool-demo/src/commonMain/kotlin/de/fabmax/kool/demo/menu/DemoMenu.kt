package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont

class DemoMenu(val demoLoader: DemoLoader) {

    private val isExpandedState = mutableStateOf(false)
    private val menuPositionAnimator = AnimatedFloatBidir(0.2f)

    private val drawerButton = DrawerButton(this)
    private val navDemoButton = NavDemoButton(this)
    private val navSettingsButton = NavSettingsButton(this)

    private val demoList = DemoListContent(this)
    private val settings = SettingsContent(this)

    val content = mutableStateOf(MenuContent.Demos)

    var isExpanded: Boolean
        get() = isExpandedState.value
        set(value) {
            if (value != isExpandedState.value) {
                isExpandedState.set(value)
                menuPositionAnimator.start(if (value) 1f else 0f)
            }
        }

    val ui = UiScene("Demo Menu") {
        addPanelSurface(colors = Colors.neon) {
            surface.sizes = Settings.uiSize.use().sizes

            modifier
                .layout(CellLayout)
                .width(FitContent)
                .padding(start = UiSizes.menuWidth * -1f)
                .height(Grow.Std)
                .background(background = null)

            if (isExpandedState.use() || menuPositionAnimator.isActive) {
                MenuContent()

                surface.onEachFrame {
                    val ptr = PointerInput.primaryPointer
                    if (ptr.isAnyButtonEvent) {
                        val ptrPos = Vec2f(ptr.x.toFloat(), ptr.y.toFloat())
                        if (!uiNode.isInBounds(ptrPos)) {
                            isExpanded = false
                        }
                    }
                }
            }

            drawerButton()
        }.apply {
            inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        }
    }

    private fun UiScope.MenuContent() = Row {
        val p = 1f - Easing.quadRev(menuPositionAnimator.progressAndUse())
        val position = UiSizes.menuWidth * -p
        modifier
            .margin(start = position)
            .width(UiSizes.menuWidth)
            .height(Grow.Std)
            .backgroundColor(colors.background)

        NavigationBar()
        Box {
            modifier
                .height(Grow.Std)
                .width(1.dp)
                .backgroundColor((MdColor.GREY tone 800).withAlpha(0.5f))
        }
        when (content.use()) {
            MenuContent.Demos -> demoList()
            MenuContent.Settings -> settings()
        }
    }

    private fun UiScope.NavigationBar() = Column {
        modifier
            .padding(top = UiSizes.baseSize)
            .width(UiSizes.baseSize)
            .height(Grow.Std)
            .backgroundColor(colors.backgroundVariant)

        navDemoButton()
        navSettingsButton()

        Text("kool Demos") {
            modifier
                .height(Grow.Std)
                .textRotation(270f)
                .textColor(colors.primaryVariant)
                .font(MsdfFont(sizePts = sizes.largeText.sizePts * 1.25f, weight = MsdfFont.WEIGHT_LIGHT))
                .margin(bottom = sizes.gap * 1.5f)
                .alignX(AlignmentX.Center)
                .textAlignY(AlignmentY.Bottom)
        }
    }

    enum class MenuContent {
        Demos,
        Settings
    }

    companion object {
        const val navBarButtonSelectedAlpha = 0.20f
        const val navBarButtonHoveredAlpha = 0.35f

        val titleBgMesh = TitleBgRenderer.BgMesh()

        val titleTextGlowColor = Color.BLACK.withAlpha(0.5f)
    }
}