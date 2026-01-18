package de.fabmax.kool.modules.compose.composables.rendering

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.dragListener
import de.fabmax.kool.modules.compose.modifiers.edit
import de.fabmax.kool.modules.compose.modifiers.hoverListener
import de.fabmax.kool.modules.ui2.AttributedTextModifier
import de.fabmax.kool.modules.ui2.AttributedTextNode
import de.fabmax.kool.modules.ui2.TextLine
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun AttributedText(
    text: TextLine,
    modifier: Modifier = Modifier.Companion,
) {
    val surface = LocalUiSurface.current
    val textNode = remember { AttributedTextNode(null, surface) }
    Layout(
        { _, _ -> textNode }, modifier
            .edit<AttributedTextModifier> { it.text = text }
            .hoverListener(textNode)
            .dragListener(textNode)
    )
}