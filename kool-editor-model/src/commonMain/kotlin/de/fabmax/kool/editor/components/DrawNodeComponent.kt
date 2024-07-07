package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.scene.Node

interface DrawNodeComponent : TransformComponent.ListenerComponent {
    val drawNode: Node

    override fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData) {
        drawNode.transform = component.transform
    }
}
