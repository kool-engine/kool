package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.ProvideZLayer
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.composables.layout.Box
import de.fabmax.kool.modules.compose.modifiers.*
import de.fabmax.kool.modules.compose.state.collectAsState
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun rememberScrollState() = remember { ScrollState() }

@Composable
fun ScrollArea(
    modifier: Modifier = Modifier,
    scrollPaneModifier: Modifier = Modifier,
    isScrollableHorizontal: Boolean = true,
    isScrollableVertical: Boolean = true,
    showVerticalScrollbar: Boolean = true,
    showHorizontalScrollbar: Boolean = true,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .onWheelX {
                if (isScrollableHorizontal) {
                    scrollState.scrollDpX(it.pointer.scroll.x * -20f)
                }
            }.onWheelY {
                if (isScrollableVertical) {
                    scrollState.scrollDpY(it.pointer.scroll.y * -50f)
                }
            }
    ) {
        val scrollState by rememberUpdatedState(scrollState)
        ScrollPane(scrollState, modifier = scrollPaneModifier) {
            content()
        }

        val bottomPadding = if (showHorizontalScrollbar) 8.dp else 0.dp
        if (showVerticalScrollbar)
            VerticalScrollbar(
                Modifier.padding(bottom = bottomPadding).fillMaxHeight().alignX(AlignmentX.End),
                state = scrollState
            )
        if (showHorizontalScrollbar)
            HorizontalScrollbar(Modifier.fillMaxWidth().alignY(AlignmentY.Bottom), state = scrollState)
    }
}

@Composable
fun ScrollPane(
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val surface = LocalUiSurface.current
    val scrollState by rememberUpdatedState(scrollState)
    val node = remember(surface) { ScrollPaneNode(null, surface) }
    node.state = scrollState

    Layout({ _, _ -> node }, modifier) {
        content()
    }
}

@Composable
fun VerticalScrollbar(
    modifier: Modifier = Modifier,
    state: ScrollState,
    scrollbarColor: Color? = null,
) {
    val scroll by state.yScrollDp.collectAsState()
    val viewHeight by state.viewHeightDp.collectAsState()
    val contentHeight by state.contentHeightDp.collectAsState()
    scroll; viewHeight; contentHeight
    val surface = LocalUiSurface.current
    val scrollBar = remember { ScrollbarNode(null, surface) }

    ProvideZLayer(10) {
        Layout({ _, _ -> scrollBar }, modifier.edit<ScrollbarModifier> { modifier ->
            modifier
                .relativeBarPos(state.relativeBarPosY)
                .relativeBarLen(state.relativeBarLenY)
                .orientation(ScrollbarOrientation.Vertical)
                .width(8.dp)
                .hoverListener(scrollBar)
                .dragListener(scrollBar)
                .onChange {
                    state.scrollRelativeY(it)
                }
            scrollbarColor?.let { modifier.colors(it) }
        }
        )
    }
}

@Composable
fun HorizontalScrollbar(
    modifier: Modifier = Modifier,
    state: ScrollState,
    scrollbarColor: Color? = null,
) {
    state.xScrollDp.collectAsState().value
    state.viewWidthDp.collectAsState().value
    state.contentWidthDp.collectAsState().value
    val surface = LocalUiSurface.current
    val scrollBar = remember { ScrollbarNode(null, surface) }

    ProvideZLayer(10) {
        Layout({ _, _ -> scrollBar }, modifier.edit<ScrollbarModifier> { modifier ->
            modifier
                .relativeBarPos(state.relativeBarPosX)
                .relativeBarLen(state.relativeBarLenX)
                .orientation(ScrollbarOrientation.Horizontal)
                .height(8.dp)
                .hoverListener(scrollBar)
                .dragListener(scrollBar)
                .onChange {
                    state.scrollRelativeX(it)
                }
            scrollbarColor?.let { modifier.colors(it) }
        }
        )
    }
}
