package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.scene.Node

interface EditorDataComponent<T: ComponentData> {
    val componentData: T
}

interface ContentComponent {
    val contentNode: Node?
}
