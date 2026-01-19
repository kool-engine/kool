package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.dragListener
import de.fabmax.kool.modules.compose.modifiers.edit
import de.fabmax.kool.modules.ui2.*
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
) {
    val surface = LocalUiSurface.current
    val slider = remember { SliderNode(null, surface) }
    Layout(
        { _, _ -> slider },
        modifier = modifier
            .edit<SliderModifier> { it.onChange { onValueChange(it) } }
            .edit<SliderModifier> { it.value(value) }
            .edit<SliderModifier> { it.minValue(range.start).maxValue(range.endInclusive) }
            .dragListener(slider)
    )
}
