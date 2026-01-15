package de.fabmax.kool.modules.compose.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import de.fabmax.kool.modules.compose.InternalKoolComposeAPI
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.LocalZLayer
import de.fabmax.kool.modules.compose.UiNodeApplier
import de.fabmax.kool.modules.compose.modifiers.UiModifierWrapper
import de.fabmax.kool.modules.ui2.UiNode
import de.fabmax.kool.modules.ui2.UiSurface
import me.dvyy.compose.mini.modifier.Modifier
import me.dvyy.compose.mini.modifier.materialize

/**
 * The main component for layout, it measures and positions zero or more children.
 */
@Composable
@InternalKoolComposeAPI
inline fun <T : UiNode> Layout(
    noinline constructor: (parent: UiNode?, surface: UiSurface) -> T,
    modifier: Modifier,
    content: @Composable () -> Unit = {},
) {
    val surface = LocalUiSurface.current
    val zLayer = LocalZLayer.current

    val materializedModifier = currentComposer.materialize(modifier)
    ComposeNode<UiNode, UiNodeApplier>(
        factory = { constructor(null, surface).also { it.applyDefaults() } },
        update = {
            set(materializedModifier) {
                this.modifier.resetDefaults()
                this.modifier.zLayer = zLayer
                materializedModifier.foldOut(this.modifier) { modifier, uiModifier ->
                    if (modifier is UiModifierWrapper) modifier.applyTo(this.modifier)
                    uiModifier
                }
                //TODO update modifier system to be able to set this value directly
            }
            set(zLayer) {
                this.modifier.zLayer = zLayer
            }
        },
        content = content,
    )
}
