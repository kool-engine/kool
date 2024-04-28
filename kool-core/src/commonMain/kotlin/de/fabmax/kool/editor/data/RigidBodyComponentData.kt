package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class RigidBodyComponentData(var mass: Float) : ComponentData {
}