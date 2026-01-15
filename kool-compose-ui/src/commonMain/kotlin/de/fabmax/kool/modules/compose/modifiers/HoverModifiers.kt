package de.fabmax.kool.modules.compose.modifiers

import de.fabmax.kool.modules.ui2.Hoverable
import de.fabmax.kool.modules.ui2.UiModifier
import de.fabmax.kool.modules.ui2.hoverListener
import me.dvyy.compose.mini.modifier.Modifier

fun Modifier.hoverListener(hoverable: Hoverable) = edit<UiModifier> { it.hoverListener(hoverable) }
