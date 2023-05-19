package de.fabmax.kool.editor.data

import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable

@Serializable
class SceneData(val sceneId: Long) {

    var name = "Scene"
    var background: SceneBackgroundData = SceneBackgroundData.SingleColor(MdColor.GREY tone 900)

    val sceneNodes: MutableSet<SceneNodeData> = mutableSetOf()
    val rootNodeIds: MutableSet<Long> = mutableSetOf()

}