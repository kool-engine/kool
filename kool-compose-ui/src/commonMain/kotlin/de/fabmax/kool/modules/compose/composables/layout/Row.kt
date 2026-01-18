package de.fabmax.kool.modules.compose.composables.layout

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.compose.composables.EmptyUiNode
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.layout
import de.fabmax.kool.modules.ui2.RowLayout
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Row(modifier: Modifier = Modifier.Companion, content: @Composable () -> Unit) {
    Layout(::EmptyUiNode, modifier.layout(RowLayout)) {
        content()
    }
}
