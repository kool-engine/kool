package de.fabmax.kool.modules.compose.composables.layout

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.compose.composables.EmptyUiNode
import de.fabmax.kool.modules.compose.composables.Layout
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Box(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(::EmptyUiNode, modifier) {
        content()
    }
}
