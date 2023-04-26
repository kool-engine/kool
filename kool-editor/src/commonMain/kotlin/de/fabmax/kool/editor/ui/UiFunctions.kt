package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.toString

fun UiScope.doubleTextField(
    value: Double,
    precision: Int,
    width: Dimension = FitContent,
    onSet: (Double) -> Unit
) = TextField {
    var text by remember(value.toString(precision))
    if (!isFocused.use()) {
        text = value.toString(precision)
    }
    modifier
        .text(text)
        .width(width)
        .margin(start = sizes.smallGap)
        .alignY(AlignmentY.Center)
        .textAlignX(AlignmentX.End)
        .onChange { text = it }
        .onEnterPressed { txt ->
            val d = txt.toDoubleOrNull()
            if (d != null && d.isFinite()) {
                onSet(d)
                // unfocus text field
                surface.requestFocus(null)
            }
        }
}

fun UiScope.intTextField(
    value: Int,
    width: Dimension = FitContent,
    onSet: (Int) -> Unit
) = TextField {
    var text by remember(value.toString())
    if (!isFocused.use()) {
        text = value.toString()
    }
    modifier
        .text(text)
        .width(width)
        .margin(start = sizes.smallGap)
        .alignY(AlignmentY.Center)
        .textAlignX(AlignmentX.End)
        .onChange { text = it }
        .onEnterPressed { txt ->
            txt.toIntOrNull()?.let {
                onSet(it)
                // unfocus text field
                surface.requestFocus(null)
            }
        }
}
