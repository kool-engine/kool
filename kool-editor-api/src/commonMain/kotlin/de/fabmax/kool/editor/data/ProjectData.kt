package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ProjectData(var mainClass: String) {

    val scenes: MutableList<SceneData> = mutableListOf()

    var nextId = 1L

}