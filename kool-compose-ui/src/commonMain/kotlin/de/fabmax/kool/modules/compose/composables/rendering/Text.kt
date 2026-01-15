package de.fabmax.kool.modules.compose.composables.rendering

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import de.fabmax.kool.modules.compose.LocalContentColor
import de.fabmax.kool.modules.compose.LocalSizes
import de.fabmax.kool.modules.compose.LocalTextStyle
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.edit
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    font: Font = LocalSizes.current.normalText,
    fontSize: Float? = null,
    color: Color? = null,
    softWrap: Boolean = false,
    style: TextStyle = LocalTextStyle.current,
) {
    val font = font.let {
        if (fontSize != null) it.derive(fontSize)
        else it
    }
    val textColor = color ?: style.color ?: LocalContentColor.current
    Layout(
        ::TextNode, modifier
            .text(text)
            .textColor(textColor)
            .isWrapText(softWrap)
            .font(font)
    )
}

@Stable
private fun Modifier.font(font: Font) = edit<TextModifier> { it.font(font) }

@Stable
private fun Modifier.text(text: String) = edit<TextModifier> { it.text(text) }

@Stable
private fun Modifier.textColor(color: Color) = edit<TextModifier> { it.textColor(color) }

@Stable
private fun Modifier.isWrapText(enabled: Boolean) = edit<TextModifier> { it.isWrapText(enabled) }

data class TextStyle(
    val color: Color? = null,
) {
    companion object {
        val Default = TextStyle()
    }
}
