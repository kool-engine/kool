package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.model.ecs.EditorModelComponent
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.scene.Node

interface EditorNodeModel {

    val nodeId: Long
    val name: String
    val node: Node

    val components: MutableStateList<EditorModelComponent>

    fun addChild(child: SceneNodeModel)
    fun removeChild(child: SceneNodeModel)

}