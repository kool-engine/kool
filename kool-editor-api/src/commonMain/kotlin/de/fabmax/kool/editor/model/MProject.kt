package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.scene.Scene
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.max

@Serializable
class MProject : Creatable<List<Scene>> {

    var mainClass = ""
    val scenes: MutableList<MScene> = mutableListOf()

    @Transient
    private var created: List<Scene>? = null

    @Transient
    private var nextId = -1L

    override fun getOrNull(): List<Scene>? = created

    override suspend fun getOrCreate() = created ?: create()

    private suspend fun create(): List<Scene> {
        val createdScenes = scenes.map { it.getOrCreate() }
        created = createdScenes
        scenes.forEach {
            it.nodesToNodeModels.values.forEach { sceneNode ->
                nextId = max(nextId, sceneNode.nodeId + 1)
            }
        }
        return createdScenes
    }

    fun nextId(): Long {
        if (nextId < 0) {
            throw IllegalStateException("create() must be called first")
        }
        return nextId++
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
