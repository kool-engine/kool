package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.fabmax.kool.modules.compose.LocalSizes
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.dragListener
import de.fabmax.kool.modules.compose.modifiers.edit
import de.fabmax.kool.modules.compose.modifiers.hoverListener
import de.fabmax.kool.modules.compose.modifiers.onClick
import de.fabmax.kool.modules.compose.state.CompatUiScope
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Font
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    font: Font = LocalSizes.current.normalText,
    onSubmit: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val surface = LocalUiSurface.current
    val textFieldNode = remember { TextFieldNode(null, surface) }

    CompatUiScope {
        if (textFieldNode.isFocused.use()) {
            surface.onEachFrame { ctx ->
                textFieldNode.updateCaretBlinkState(ctx)
            }
        }
    }

    Layout(
        { _, _ -> textFieldNode }, modifier
            .onClick { textFieldNode.onClick(it) }
            .hoverListener(textFieldNode)
            .dragListener(textFieldNode)
            .edit<TextFieldModifier> { it.onChange { onValueChange(it) } }
            .edit<TextFieldModifier> { it.text(value) }
            .edit<TextFieldModifier> { it.onEnterPressed { onSubmit(it) } }
            .edit<TextFieldModifier> { it.font(font) }
    )
}
