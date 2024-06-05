package de.fabmax.kool.editor.api

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import kotlinx.serialization.json.Json

class EditorProject(val projectData: ProjectData) : BaseReleasable() {

    private var nextId = projectData.entities.maxOf { it.id.value } + 1

    private val _sceneNodeData = projectData.entities.associateBy { it.id }.toMutableMap()
    val sceneNodeData: Map<EntityId, GameEntityData>
        get() = _sceneNodeData

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
        val referencedEntityIds = mutableSetOf<EntityId>()

        fun collectChildNodeIds(node: GameEntityData) {
            node.childEntityIds.forEach { childId ->
                val child = entityMap[childId]
                if (child == null) {
                    logE { "Node \"${node.name}\" references non-existing child node $childId" }
                } else {
                    referencedEntityIds += childId
                    collectChildNodeIds(child)
                }
            }
        }

        projectData.entities
            .filter { it.components.any { c -> c is SceneComponentData } }
            .forEach { scene ->
                referencedEntityIds += scene.id
                collectChildNodeIds(scene)
            }

        val unreferencedIds = entityMap.keys - referencedEntityIds
        if (unreferencedIds.isNotEmpty()) {
            logE { "Project contains unreferenced entities: ${unreferencedIds.map { "$it: ${entityMap[it]?.name}" }}" }
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
        val existingNames = sceneNodeData.values.map { it.name }.toSet()
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

    fun addSceneNodeData(data: GameEntityData) {
        projectData.entities += data
        _sceneNodeData[data.id] = data
    }

    fun removeSceneNodeData(data: GameEntityData) {
        projectData.entities -= data
        _sceneNodeData -= data.id
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
                entities += GameEntityData("New Scene", sceneId).apply {
                    childEntityIds += listOf(camId, boxId, lightId)
                    components += SceneComponentData(cameraEntityId = camId)
                    components += SceneBackgroundComponentData(
                        SceneBackgroundData.SingleColor(ColorData(MdColor.GREY toneLin 900))
                    )
                }
                entities += GameEntityData("Camera", camId).apply {
                    components += CameraComponentData(CameraTypeData.Perspective())
                    components += TransformComponentData(
                        TransformData.fromMatrix(
                            MutableMat4d()
                                .translate(0.0, 2.5, 5.0)
                                .rotate((-30.0).deg, Vec3d.X_AXIS)
                        ))
                }
                entities += GameEntityData("Default Cube", boxId).apply {
                    components += MeshComponentData(ShapeData.Box(Vec3Data(1.0, 1.0, 1.0)))
                }
                entities += GameEntityData("Directional Light", lightId).apply {
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
