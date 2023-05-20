package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ProjectData(var mainClass: String) {

    val sceneNodes: MutableSet<SceneNodeData> = mutableSetOf()
    val sceneNodeIds: MutableList<Long> = mutableListOf()

    var nextId = 1L

}