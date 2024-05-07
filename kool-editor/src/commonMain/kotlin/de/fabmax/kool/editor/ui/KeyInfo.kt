package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.Key
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.modules.ui2.*

class KeyInfo(val ui: EditorUi) : Composable {

    override fun UiScope.compose() = ReverseColumn {
        modifier
            .size(350.dp, 400.dp)
            .margin(sizes.largeGap)
            .align(AlignmentX.End, AlignmentY.Bottom)

        Column {
            modifier
                .width(Grow.Std)
                .height(Grow(1f, min = FitContent, max = 400.dp))
                .padding(start = sizes.largeGap, end = sizes.smallGap, bottom = sizes.gap)
                .background(RoundRectBackground(colors.backgroundVariantAlpha(0.7f), sizes.gap))
                .onPointer { it.pointer.consume() }

            Text(ui.inputModeState.use()) {
                modifier
                    .font(sizes.boldText)
                    .margin(sizes.gap * 1.5f)
            }

            val currentInput = InputStack.handlerStack.lastOrNull { it is EditorKeyListener } as EditorKeyListener?
            currentInput?.let { input ->
                LazyList(
                    containerModifier = { it.backgroundColor(null) }
                ) {
                    items(input.registeredKeys.filter { it != Key.Help }) { key ->
                        Row(width = Grow.Std) {
                            modifier.margin(bottom = sizes.smallGap)

                            Box {
                                modifier.width(100.dp)
                                keyLabel(key)
                            }
                            Text(key.description) { }
                        }
                    }
                }
            }
        }

        Box(height = Grow(10f)) { }

    }
}