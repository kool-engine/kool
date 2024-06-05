package de.fabmax.kool.editor.components

import de.fabmax.kool.scene.Node

interface DrawNodeComponent<T: Node> {
    val typedDrawNode: T?
}
