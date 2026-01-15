package de.fabmax.kool.modules.compose.composables.layout

import androidx.compose.runtime.*
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.ProvideZLayer
import de.fabmax.kool.modules.compose.modifiers.align
import de.fabmax.kool.modules.compose.modifiers.margin
import de.fabmax.kool.modules.compose.modifiers.onMeasured
import de.fabmax.kool.modules.compose.modifiers.onPositioned
import de.fabmax.kool.modules.ui2.AlignmentX
import de.fabmax.kool.modules.ui2.AlignmentY
import de.fabmax.kool.modules.ui2.UiNode
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.modules.ui2.dp
import de.fabmax.kool.pipeline.RenderPass
import me.dvyy.compose.mini.modifier.Modifier
import me.dvyy.compose.mini.runtime.layers.Content
import me.dvyy.compose.mini.runtime.layers.rememberComposeSceneLayer

@Composable
private fun Popup(
    layerOffset: Int = UiSurface.LAYER_POPUP,
    content: @Composable () -> Unit,
) {
    val layer = rememberComposeSceneLayer()
    layer.Content {
        ProvideZLayer(layerOffset) {
            content()
        }
    }
}

/**
 * A separate layer in this composition, positioned either relative to the parent node, or the root node.
 */
@Composable
fun Popup(
    offset: Vec2f = Vec2f.ZERO,
    layerOffset: Int = UiSurface.LAYER_POPUP,
    relativeToParent: Boolean = true,
    alignmentX: AlignmentX = AlignmentX.Start,
    alignmentY: AlignmentY = AlignmentY.Top,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var parentPosition by remember { mutableStateOf(Vec2f.ZERO) }
    // Hide for first frame since parent position lags behind one frame
    // TODO update position on same frame, possible to do with a custom layout?
    var positioned by remember { mutableStateOf(false) }

    // Empty box for getting the position of the parent node
    if (relativeToParent && !positioned) Box(Modifier.onPositioned {
        parentPosition = Vec2f(it.leftPx, it.bottomPx)
        positioned = true
    }) {}

    if (!relativeToParent || positioned) Popup(layerOffset) {
        var uiNode: UiNode? by remember { mutableStateOf(null) }
        val surface = LocalUiSurface.current
        Box(
            modifier.margin(
                start = offset.x.dp + parentPosition.x.dp,
                top = offset.y.dp + parentPosition.y.dp,
                end = 0.dp,
                bottom = 0.dp
            ).align(alignmentX, alignmentY)
                .onMeasured { uiNode = it }
        ) {
            content()
        }

        DisposableEffect(uiNode, onDismissRequest) {
            var dismissedLastFrame = false
            var startedClickInBounds = false
            val listener = { _: RenderPass.UpdateEvent ->
                val ptr = PointerInput.primaryPointer
                if (dismissedLastFrame) {
                    onDismissRequest()
                }
                if (ptr.isAnyButtonPressed) {
                    startedClickInBounds = uiNode?.isInBounds(PointerInput.primaryPointer.pos) == true
                }
                if (ptr.isAnyButtonReleased) {
                    if (!startedClickInBounds && uiNode?.isInBounds(PointerInput.primaryPointer.pos) == false) {
                        dismissedLastFrame = true
                    }
                    startedClickInBounds = false
                }
            }
            surface.onUpdate.stageAdd(listener)
            onDispose {
                surface.onUpdate.stageRemove(listener)
            }
        }
    }
}
