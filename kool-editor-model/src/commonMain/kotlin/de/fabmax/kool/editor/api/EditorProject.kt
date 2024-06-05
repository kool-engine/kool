package de.fabmax.kool.editor.api

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.*
import kotlinx.serialization.json.Json

class EditorProject(val projectData: ProjectData) : BaseReleasable() {

    private var nextId = projectData.entities.maxOf { it.id.value } + 1

    private val _entityData = projectData.entities.associateBy { it.id }.toMutableMap()
    val entityData: Map<EntityId, GameEntityData>
        get() = _entityData

    private val _materialsById = projectData.materials.associateBy { it.id }.toMutableMap()
    val materialsById: Map<EntityId, MaterialData>
        get() = _materialsById
    val materials = mutableStateListOf<MaterialData>().apply {
        addAll(projectData.materials)
        sortBy { it.name }
    }

    private val _createdScenes: MutableMap<EntityId, EditorScene> = mutableMapOf()
    val createdScenes: Map<EntityId, EditorScene> get() = _createdScenes

    private fun checkProjectModelConsistency() {
        val entityMap = projectData.entities.associateBy { it.id }
        val parentsToChildren = mutableMapOf<EntityId?, MutableList<EntityId>>()
        val referencedEntityIds = mutableSetOf<EntityId>()

        projectData.entities.forEach {
            if (it.parentId != null && it.parentId !in entityMap) {
                logE { "Entity ${it.name} references non-existing parent" }
            } else {
                parentsToChildren.getOrPut(it.parentId) { mutableListOf() } += it.id
            }
        }

        fun collectChildNodeIds(entity: GameEntityData) {
            parentsToChildren[entity.id]?.forEach { childId ->
                val child = checkNotNull(entityMap[childId])
                referencedEntityIds += childId
                collectChildNodeIds(child)
            }
        }

        val roots = parentsToChildren[null]?.mapNotNull { entityMap[it] } ?: emptyList()
        roots.forEach { scene ->
            if (scene.components.none { c -> c is SceneComponentData }) {
                logW { "Root entity ${scene.name} has no scene component" }
            }
            referencedEntityIds += scene.id
            collectChildNodeIds(scene)
        }

        val unreferencedIds = entityMap.keys - referencedEntityIds
        if (unreferencedIds.isNotEmpty()) {
            logE { "Project contains unreferenced entities: ${unreferencedIds.joinToString { "\n  $it: ${entityMap[it]?.name}" }}" }
        }
    }

    suspend fun createScenes() {
        logI { "Load project scenes" }
        checkProjectModelConsistency()
        projectData.entities
            .filter { it.components.any { c -> c is SceneComponentData } }
            .forEach { sceneData ->
                val sceneModel = _createdScenes.getOrPut(sceneData.id) { EditorScene(sceneData, this) }
                sceneModel.prepareScene()
                sceneModel.applyComponents()
            }
    }

    fun onStart() {
        createdScenes.values.forEach { it.onStart() }
    }

    fun releaseScenes() {
        createdScenes.values.forEach { it.release() }
        _createdScenes.clear()
    }

    override fun release() {
        super.release()
        releaseScenes()
    }

    fun nextId(): EntityId {
        return EntityId(nextId++)
    }

    /**
     * Checks if the given name exists and if so prepends an increasing number to it to make it unique. If the given
     * name already ends with a number, the number is replaced.
     */
    fun uniquifyName(name: String): String {
        val existingNames = entityData.values.map { it.name }.toSet()
        if (name !in existingNames) {
            return name
        }

        var nameBase = name
        while (nameBase.isNotEmpty() && nameBase.last().isDigit()) {
            nameBase = nameBase.substring(0 until nameBase.lastIndex)
        }
        nameBase = nameBase.trim()

        var counter = 1
        var uniqueName = "$nameBase ${counter++}"
        while (uniqueName in existingNames) {
            uniqueName = "$nameBase ${counter++}"
        }
        return uniqueName
    }

    fun addEntityData(data: GameEntityData) {
        projectData.entities += data
        _entityData[data.id] = data
    }

    fun removeEntityData(data: GameEntityData) {
        projectData.entities -= data
        _entityData -= data.id
    }

    fun createNewMaterial(): MaterialData {
        val id = nextId()
        val newMat = MaterialData(id, "Material-$id", PbrShaderData())
        addMaterial(newMat)
        return newMat
    }

    fun removeMaterial(material: MaterialData) {
        _materialsById -= material.id
        projectData.materials -= material
        materials -= material
    }

    fun addMaterial(material: MaterialData) {
        _materialsById[material.id] = material
        projectData.materials += material
        materials += material
        materials.sortBy { it.name }
    }

    companion object {
        suspend fun loadFromAssets(path: String = "kool-project.json"): EditorProject? {
            return try {
                val json = Assets.loadBlobAsset(path).toArray().decodeToString()
                val projectData: ProjectData = Json.decodeFromString(json)
                EditorProject(projectData)
            } catch (e: Exception) {
                null
            }
        }

        fun emptyProject(): EditorProject = EditorProject(
            ProjectData().apply {
                val sceneId = EntityId(1L)
                val camId = EntityId(2L)
                val boxId = EntityId(3L)
                val lightId = EntityId(4L)
                entities += GameEntityData("New Scene", sceneId, null).apply {
                    components += SceneComponentData(cameraEntityId = camId)
                    components += SceneBackgroundComponentData(
                        SceneBackgroundData.SingleColor(ColorData(MdColor.GREY toneLin 900))
                    )
                }
                entities += GameEntityData("Camera", camId, sceneId).apply {
                    components += CameraComponentData(CameraTypeData.Perspective())
                    components += TransformComponentData(
                        TransformData.fromMatrix(
                            MutableMat4d()
                                .translate(0.0, 2.5, 5.0)
                                .rotate((-30.0).deg, Vec3d.X_AXIS)
                        ))
                }
                entities += GameEntityData("Default Cube", boxId, sceneId).apply {
                    components += MeshComponentData(ShapeData.Box(Vec3Data(1.0, 1.0, 1.0)))
                }
                entities += GameEntityData("Directional Light", lightId, sceneId).apply {
                    components += DiscreteLightComponentData(LightTypeData.Directional())
                    components += TransformComponentData(
                        TransformData.fromMatrix(
                            MutableMat4d()
                                .translate(5.0, 5.0, 5.0)
                                .rotate(0.0.deg, 30.0.deg, (-120.0).deg)
                        ))
                }
            }
        )
    }
}
