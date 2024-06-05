package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.scene.Node

abstract class GameEntityDataComponent<T: ComponentData>(
    gameEntity: GameEntity,
    val componentData: T
) : GameEntityComponent(gameEntity)

interface DrawNodeComponent<T: Node> {
    val typedDrawNode: T?
}
