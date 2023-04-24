package de.fabmax.kool.editor.model

import de.fabmax.kool.scene.Node

interface MSceneNode<T: Node> : Creatable<T> {

    val nodeProperties: MCommonNodeProperties

    val childNodes: MutableMap<Long, MSceneNode<*>>

}