package de.fabmax.kool.editor.model.ecs

import de.fabmax.kool.editor.data.ComponentData

interface EditorModelComponent

interface EditorDataComponent<T: ComponentData> : EditorModelComponent {
    val componentData: T
}