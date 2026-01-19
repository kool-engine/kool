package de.fabmax.kool.modules.compose

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.ui2.Colors
import de.fabmax.kool.modules.ui2.Sizes
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

@ExperimentalKoolComposeAPI
fun ComposableSurface(
    scene: Scene,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    content: @Composable () -> Unit,
): UiSurface {
    val surface = UiSurface(scene, colors, sizes, clearViewportOnUpdateUi = false).apply {
        inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
    }
    val composition = UiSurfaceComposition(surface)
    composition.start {
        content()
    }
    surface.onRelease { composition.exit() }
    return surface
}

@ExperimentalKoolComposeAPI
fun Node.addComposableSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    content: @Composable () -> Unit,
): UiSurface {
    val scene = checkNotNull(findParentOfType<Scene>()) {
        "Parent scene not found. Make sure the node is added to a scene before calling addPanelSurface()"
    }
    val surface = ComposableSurface(scene, colors, sizes, content)
    addNode(surface)
    return surface
}