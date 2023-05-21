package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ScriptComponentData(
    var scriptClassName: String,
    var runInEditMode: Boolean = false
) : ComponentData