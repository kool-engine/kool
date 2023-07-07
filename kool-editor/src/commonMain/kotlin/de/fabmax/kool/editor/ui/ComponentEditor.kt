package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.modules.ui2.Composable

interface ComponentEditor<T: EditorModelComponent> : Composable {
    var component: T
}