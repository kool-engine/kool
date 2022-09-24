package de.fabmax.kool.demo.menu

import de.fabmax.kool.modules.ui2.*

class SettingsContent(val menu: DemoMenu) : ComposableComponent {

    override fun UiScope.compose() {
        Column {
            modifier
                .height(Grow.Std)
                .width(Grow.Std)

            Text("Settings") {
                itemStyle()
                modifier
                    .backgroundColor(colors.accentVariant)
                    .textColor(colors.onAccent)
            }

            Row(width = Grow.Std) {
                Text("Debug overlay") {
                    itemStyle()
                    modifier.onClick {
                        menu.showDebugOverlay.set(!menu.showDebugOverlay.value)
                    }
                }
                Switch(menu.showDebugOverlay.use()) {
                    switchStyle()
                    modifier.onToggle { menu.showDebugOverlay.set(it) }
                }
            }
            Row(width = Grow.Std) {
                Text("Fullscreen") {
                    itemStyle()
                    modifier.onClick {
                        menu.isFullscreen.set(!menu.isFullscreen.value)
                    }
                }
                Switch(menu.isFullscreen.use()) {
                    switchStyle()
                    modifier.onToggle { menu.isFullscreen.set(it) }
                }
            }
            Row(width = Grow.Std) {
                Text("Hidden demos") {
                    itemStyle()
                    modifier.onClick {
                        menu.showHiddenDemos.set(!menu.showHiddenDemos.value)
                    }
                }
                Switch(menu.showHiddenDemos.use()) {
                    switchStyle()
                    modifier.onToggle { menu.showHiddenDemos.set(it) }
                }
            }
        }
    }

    private fun TextScope.itemStyle() {
        modifier
            .width(Grow.Std)
            .height(DemoMenu.itemSize.dp)
            .padding(horizontal = sizes.gap)
            .textAlignY(AlignmentY.Center)
    }

    private fun ToggleScope.switchStyle() {
        modifier
            .width(64.dp)
            .margin(end = sizes.gap)
            .height(DemoMenu.itemSize.dp)
    }
}