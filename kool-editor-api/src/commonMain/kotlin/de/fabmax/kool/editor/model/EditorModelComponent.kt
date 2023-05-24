package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.ComponentData

interface EditorModelComponent {
    suspend fun createComponent(nodeModel: EditorNodeModel) { }
    suspend fun initComponent(nodeModel: EditorNodeModel) { }
}

interface EditorDataComponent<T: ComponentData> : EditorModelComponent {
    val componentData: T
}