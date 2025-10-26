package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.l
import kotlin.reflect.KClass

class LauncherWindow(uiDemo: UiDemo) : DemoWindow("Window Launcher", uiDemo, false) {

    init {
        windowDockable.setFloatingBounds(width = Dp(250f))
    }

    override fun UiScope.windowContent() = Column(Grow.Std) {
        var allowMultiInstances by remember(false)

        Button("UI Basics".l) {
            launcherButtonStyle("Example window with a few basic UI components".l)
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances, BasicUiWindow::class) { BasicUiWindow(uiDemo) }
            }
        }
        Button("Text Style".l) {
            launcherButtonStyle("Signed-distance-field font rendering showcase".l)
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances, TextStyleWindow::class) { TextStyleWindow(uiDemo) }
            }
        }
        Button("Text Area".l) {
            launcherButtonStyle("Editable text area with many different text styles".l)
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances, TextAreaWindow::class) { TextAreaWindow(uiDemo) }
            }
        }
        Button("Conway's Game of Life".l) {
            launcherButtonStyle("Game of Life simulation / toggle-button benchmark".l)
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances, GameOfLifeWindow::class) { GameOfLifeWindow(uiDemo) }
            }
        }
        Button("Theme Editor".l) {
            launcherButtonStyle("UI color theme editor".l)
            modifier.onClick {
                launchOrBringToTop(false, ThemeEditorWindow::class) { ThemeEditorWindow(uiDemo) }
            }
        }
        Button("Drag and Drop".l) {
            launcherButtonStyle("Two windows with drag & droppable items".l)
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances, DragAndDropWindow.A::class) { DragAndDropWindow.A(uiDemo) }
                launchOrBringToTop(allowMultiInstances, DragAndDropWindow.B::class) { DragAndDropWindow.B(uiDemo) }
            }
        }
        Row(Grow.Std) {
            modifier
                .margin(sizes.largeGap)
            Text("Multiple instances".l) {
                modifier
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .onClick { allowMultiInstances = !allowMultiInstances }
            }
            Switch(allowMultiInstances) {
                modifier.onToggle { allowMultiInstances = it }
            }
        }
    }

    private fun <T: DemoWindow> launchOrBringToTop(multiAllowed: Boolean, windowClass: KClass<T>, factory: () -> T) {
        if (!multiAllowed) {
            val existing = uiDemo.demoWindows.find { it::class == windowClass }
            if (existing != null) {
                existing.windowSurface.isFocused.set(true)
                return
            }
        }
        uiDemo.spawnWindow(factory())
    }

    private fun ButtonScope.launcherButtonStyle(tooltip: String) {
        modifier
            .width(Grow.Std)
            .margin(sizes.largeGap)
            .padding(vertical = sizes.gap)

        Tooltip(tooltip)
    }
}