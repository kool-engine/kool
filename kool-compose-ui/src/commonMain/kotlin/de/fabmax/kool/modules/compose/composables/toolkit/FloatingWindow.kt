package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.compose.LocalColors
import de.fabmax.kool.modules.compose.LocalSizes
import de.fabmax.kool.modules.compose.composables.layout.Column
import de.fabmax.kool.modules.compose.composables.layout.Popup
import de.fabmax.kool.modules.compose.composables.layout.Row
import de.fabmax.kool.modules.compose.composables.rendering.Text
import de.fabmax.kool.modules.compose.modifiers.*
import de.fabmax.kool.modules.ui2.Draggable
import de.fabmax.kool.modules.ui2.PointerEvent
import de.fabmax.kool.modules.ui2.RoundRectBackground
import de.fabmax.kool.modules.ui2.RoundRectBorder
import de.fabmax.kool.modules.ui2.TitleBarBackground
import de.fabmax.kool.modules.ui2.dp
import me.dvyy.compose.mini.modifier.Modifier
import kotlin.Int
import kotlin.String
import kotlin.Unit

@Composable
fun FloatingWindow(
    title: String,
    startOffset: Vec2f = Vec2f.ZERO,
    modifier: Modifier = Modifier.Companion,
    layer: Int,
    content: @Composable () -> Unit,
) {
    var x by remember { mutableStateOf(startOffset.x.dp) }
    var y by remember { mutableStateOf(startOffset.y.dp) }
    Popup(offset = Vec2f(x.px, y.px), layerOffset = layer, relativeToParent = false) {
        val colors = LocalColors.current
        val sizes = LocalSizes.current
        Column(
            Modifier
                .background(RoundRectBackground(colors.background, sizes.smallGap))
                .border(RoundRectBorder(colors.backgroundVariant, sizes.smallGap, sizes.borderWidth))
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .background(TitleBarBackground(colors.backgroundVariant, sizes.smallGap.value, false))
                    .padding(sizes.smallGap)
                    .dragListener(object : Draggable {
                        override fun onDrag(ev: PointerEvent) {
                            x += de.fabmax.kool.modules.ui2.Dp.fromPx(ev.pointer.delta.x)
                            y += de.fabmax.kool.modules.ui2.Dp.fromPx(ev.pointer.delta.y)
                        }
                    })
            ) {
                Text(title)
            }
            Column(modifier.padding(sizes.smallGap)) {
                content()
            }
        }
    }
}