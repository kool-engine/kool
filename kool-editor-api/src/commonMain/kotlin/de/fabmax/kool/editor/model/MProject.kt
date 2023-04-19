package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.scene.Scene
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class MProject(
    val mainClass: String,
    val scenes: List<MScene>
) : Creatable<List<Scene>> {

    @Transient
    override var created: List<Scene>? = null

    override fun create(classFactory: ClassFactory): List<Scene> {
        val createdScenes = scenes.map { it.create(classFactory) }
        created = createdScenes
        return createdScenes
    }

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
