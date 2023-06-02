package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.model.EditorNodeModel

interface EditorModelComponent {
    suspend fun createComponent(nodeModel: EditorNodeModel)
    suspend fun initComponent(nodeModel: EditorNodeModel)
}

interface EditorDataComponent<T: ComponentData> : EditorModelComponent {
    val componentData: T
}