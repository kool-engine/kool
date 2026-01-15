package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.compose.Colors
import de.fabmax.kool.modules.compose.Sizes
import de.fabmax.kool.modules.compose.composables.layout.Box
import de.fabmax.kool.modules.compose.modifiers.alignX
import de.fabmax.kool.modules.compose.modifiers.background
import de.fabmax.kool.modules.compose.modifiers.clickable
import de.fabmax.kool.modules.compose.modifiers.padding
import de.fabmax.kool.modules.ui2.AlignmentX
import de.fabmax.kool.modules.ui2.RoundRectBackground
import de.fabmax.kool.modules.ui2.dp
import de.fabmax.kool.util.Color
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Button(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val color = Colors.secondaryVariant

    Box(
        modifier.background(RoundRectBackground(color, 4.dp))
            .padding(horizontal = Sizes.gap, vertical = Sizes.smallGap)
            .clickable(RoundRectBackground(Color.WHITE.withAlpha(0.2f), 4.dp)) { onClick() }
    ) {
        Box(Modifier.alignX(AlignmentX.Center)) {
            content()
        }
    }
}
