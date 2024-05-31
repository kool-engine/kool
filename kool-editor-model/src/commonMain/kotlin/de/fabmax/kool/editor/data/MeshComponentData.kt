package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class MeshComponentData() : ComponentData {
    val shapes = mutableListOf<ShapeData>()

    constructor(singleShape: ShapeData) : this() {
        shapes += singleShape
    }
}
