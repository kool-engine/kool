package de.fabmax.kool.modules.compose.modifiers

import androidx.compose.runtime.Stable
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import me.dvyy.compose.mini.modifier.Modifier

@Stable
fun Modifier.background(background: UiRenderer<UiNode>) = edit<UiModifier> { it.background(background) }

@Stable
fun Modifier.border(border: UiRenderer<UiNode>) = edit<UiModifier> { it.border(border) }

@Stable
fun Modifier.backgroundColor(color: Color) = edit<UiModifier> { it.backgroundColor(color) }

fun Modifier.onEnter(run: (PointerEvent) -> Unit) = edit<UiModifier> { it.onEnter { run(it) } }

fun Modifier.onExit(run: (PointerEvent) -> Unit) = edit<UiModifier> { it.onExit { run(it) } }

fun Modifier.onClick(run: (PointerEvent) -> Unit) = edit<UiModifier> { it.onClick { run(it) } }

fun Modifier.onPositioned(run: (UiNode) -> Unit) = edit<UiModifier> { it.onPositioned { run(it) } }
fun Modifier.onMeasured(run: (UiNode) -> Unit) = edit<UiModifier> { it.onMeasured { run(it) } }
fun Modifier.draw(render: UiNode.() -> Unit) =
    edit<UiModifier> { it.onRender.add(render) }

@Stable
fun Modifier.text(text: String) = edit<TextModifier> { it.text(text) }

@Stable
fun Modifier.onWheelX(run: (PointerEvent) -> Unit) = edit<UiModifier> { it.onWheelX(run) }

@Stable
fun Modifier.onWheelY(run: (PointerEvent) -> Unit) = edit<UiModifier> { it.onWheelY(run) }
