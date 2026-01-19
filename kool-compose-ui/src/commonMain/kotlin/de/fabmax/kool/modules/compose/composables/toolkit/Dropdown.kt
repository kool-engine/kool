package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.compose.LocalColors
import de.fabmax.kool.modules.compose.LocalSizes
import de.fabmax.kool.modules.compose.LocalTextStyle
import de.fabmax.kool.modules.compose.composables.layout.Box
import de.fabmax.kool.modules.compose.composables.layout.Column
import de.fabmax.kool.modules.compose.composables.layout.Popup
import de.fabmax.kool.modules.compose.composables.layout.Row
import de.fabmax.kool.modules.compose.modifiers.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun DropdownButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sizes = LocalSizes.current
    val colors = LocalColors.current

    Row(
        modifier
            .background(RoundRectBackground(colors.secondaryVariant, sizes.smallGap))
            .clickable(RoundRectBackground(Color.WHITE.withAlpha(0.5f), sizes.smallGap)) { onClick() }) {
        Box(Modifier.padding(start = sizes.smallGap, top = sizes.smallGap, bottom = sizes.smallGap)) {
            content()
        }
        Box(
            Modifier
                .padding(sizes.smallGap)
                .alignX(AlignmentX.End)
                .fillMaxHeight()
        ) {
            Arrow(color = Color.WHITE, Modifier.alignY(AlignmentY.Center))
        }
    }
}

@Composable
fun Arrow(
    color: Color? = LocalTextStyle.current.color,
    modifier: Modifier = Modifier,
) {
    Box(modifier.background(UiRenderer {
        with(it) {
            val cx = it.widthPx * 0.5f
            val cy = it.heightPx * 0.5f
            val d = it.sizes.smallGap.px * 2.5f
            it.getPlainBuilder().configured(color = color) {
                arrow(cx, cy, d, 90f)
            }
        }
    }).size(16.dp, 16.dp)) {}
}


@Composable
fun DropdownMenu(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val colors = LocalColors.current
    val sizes = LocalSizes.current
    val borderColor = colors.primaryVariantAlpha(0.5f)
    if (expanded) Popup(
        relativeToParent = true,
        coerceToViewportBounds = true,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .background(RoundRectBackground(colors.backgroundVariant, sizes.smallGap))
            .border(RoundRectBorder(borderColor, sizes.smallGap, sizes.borderWidth))
            .padding(sizes.smallGap)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun DropdownMenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    text: @Composable () -> Unit,
) {
    Box(
        modifier
            .padding(LocalSizes.current.smallGap)
            .fillMaxWidth()
            .clickable(RoundRectBackground(Color.WHITE.withAlpha(0.2f), LocalSizes.current.smallGap)) { onClick() }) {

        text()
    }
}
