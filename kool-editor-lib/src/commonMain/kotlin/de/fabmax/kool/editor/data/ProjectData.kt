package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ProjectData(var mainClass: String) {
    var nextId = 1L

    val sceneNodeIds: MutableList<Long> = mutableListOf()
    val sceneNodes: MutableSet<SceneNodeData> = mutableSetOf()
    val materials: MutableList<MaterialData> = mutableListOf()
}