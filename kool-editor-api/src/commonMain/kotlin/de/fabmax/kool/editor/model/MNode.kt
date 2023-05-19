package de.fabmax.kool.editor.model

import de.fabmax.kool.scene.Node

interface MNode {

    val nodeId: Long
    val name: String
    val node: Node

    fun addChild(child: MSceneNode)
    fun removeChild(child: MSceneNode)

}