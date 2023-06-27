package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.scene.Node

interface EditorModelComponent {
    suspend fun createComponent(nodeModel: EditorNodeModel)
    suspend fun initComponent(nodeModel: EditorNodeModel)

    fun onNodeRemoved(nodeModel: EditorNodeModel) { }
    fun onNodeAdded(nodeModel: EditorNodeModel) { }
}

interface EditorDataComponent<T: ComponentData> : EditorModelComponent {
    val componentData: T
}

interface ContentComponent : EditorModelComponent {
    val contentNode: Node
}