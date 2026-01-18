package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.edit
import de.fabmax.kool.modules.ui2.*
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        ::SwitchNode, modifier
            .edit<SwitchModifier> { it.onClick { onCheckedChange(!checked) } }
            .edit<SwitchModifier> { it.toggleState(checked) }
    )
}

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        ::CheckboxNode, modifier
            .edit<CheckboxModifier> { it.onClick { onCheckedChange(!checked) } }
            .edit<CheckboxModifier> { it.toggleState(checked) }
    )
}

@Composable
fun RadioButton(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Layout(
        ::RadioButtonNode, modifier
            .edit<RadioButtonModifier> { it.onClick { onCheckedChange?.invoke(!checked) } }
            .edit<RadioButtonModifier> { it.toggleState(checked) }
    )
}
