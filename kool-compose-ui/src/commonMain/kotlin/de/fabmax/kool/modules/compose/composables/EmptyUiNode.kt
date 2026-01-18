package de.fabmax.kool.modules.compose.composables

import de.fabmax.kool.modules.ui2.UiModifier
import de.fabmax.kool.modules.ui2.UiNode
import de.fabmax.kool.modules.ui2.UiSurface

/**
 * An instance of [UiNode] with no additional state defined.
 *
 * Used by some layout composables to define behaviour via Modifiers instead of inheritance.
 */
internal class EmptyUiNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface) {
    override val modifier: UiModifier = UiModifier(surface)
}
