package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class MProject(
    val mainClass: String,
    val scenes: List<MScene>
) {
    companion object {
        suspend fun loadFromAssets(): MProject? {
            return try {
                val json = Assets.loadBlobAsset("kool-project.json").toArray().decodeToString()
                Json.decodeFromString(json)
            } catch (e: Exception) {
                null
            }
        }
    }
}
