package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.Key
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.modules.ui2.*

class KeyInfo(val ui: EditorUi) : Composable {

    override fun UiScope.compose() = ReverseColumn {
        modifier
            .size(450.dp, 400.dp)
            .margin(sizes.largeGap)
            .align(AlignmentX.End, AlignmentY.Bottom)

        Column {
            modifier
                .width(Grow.Std)
                .height(Grow(1f, min = FitContent, max = 400.dp))
                .padding(start = sizes.largeGap, end = sizes.gap, bottom = sizes.gap)
                .background(RoundRectBackground(colors.backgroundVariantAlpha(0.7f), sizes.gap))
                .onPointer { it.pointer.consume() }

            Row(width = Grow.Std) {
                Text("${ui.inputModeState.use()} - Keymap") {
                    modifier
                        .margin(vertical = sizes.gap * 1.5f)
                        .width(Grow.Std)
                        .font(sizes.boldText)
                }
                closeButton { ui.sceneView.isShowKeyInfo.set(false) }
            }

            val currentInput = InputStack.handlerStack.lastOrNull { it is EditorKeyListener } as EditorKeyListener?
            currentInput?.let { input ->
                LazyColumn(
                    containerModifier = {
                        it
                            .margin(end = sizes.gap * 1.4f)
                            .backgroundColor(null)
                    }
                ) {
                    items(input.registeredKeys.filter { it != Key.Help }) { key ->
                        Row(width = Grow.Std) {
                            modifier.margin(bottom = sizes.smallGap)

                            Box {
                                modifier.width(150.dp)
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