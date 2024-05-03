package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.util.nodeModel
import kotlin.reflect.KClass

abstract class ComponentAction<T: EditorModelComponent>(
    val nodeId: NodeId,
    val componentType: KClass<T>
) : EditorAction {
    val nodeModel: NodeModel? get() = nodeId.nodeModel
    val component: T? get() {
        @Suppress("UNCHECKED_CAST")
        return nodeModel?.components?.first { it::class == componentType } as T?
    }
}