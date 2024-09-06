package de.fabmax.kool.editor.api

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.loadBlob
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.*
import kotlinx.serialization.json.Json

class EditorProject(val projectData: ProjectData) : BaseReleasable() {

    internal val entityData = projectData.allEntities().associateBy { it.id }.toMutableMap()

    private var nextId = entityData.values.maxOf { it.id.value } + 1

    private val _createdScenes: MutableMap<EntityId, EditorScene> = mutableMapOf()
    val createdScenes: Map<EntityId, EditorScene> get() = _createdScenes

    var sceneModCnt = 0
        internal set

    private val _materialsById: MutableMap<EntityId, MaterialComponent> = mutableMapOf()
    val materialScene: EditorScene
    val materialsById: Map<EntityId, MaterialComponent> get() = _materialsById
    val materials = mutableStateListOf<MaterialComponent>()
    var defaultMaterial: MaterialComponent? = null

    val listenerComponents = mutableListOf<Any>()

    init {
        projectData.checkConsistency()

        val materialSceneId = projectData.materials.find { it.components.any { c -> c.data is SceneComponentData } }?.id ?: nextId()
        materialScene = EditorScene(SceneData(SceneMeta(materialSceneId, "materials"), projectData.materials), this)
        materialScene.getAllComponents<MaterialComponent>().forEach { _materialsById[it.gameEntity.id] = it }
        materials.apply {
            addAll(materialsById.values)
            sortBy { it.name }
        }
    }

    suspend fun createScenes() {
        // brdf lut is lazy loaded -> force loading it now
        KoolSystem.requireContext().defaultPbrBrdfLut

        if (defaultMaterial == null) {
            defaultMaterial = materialScene.getAllComponents<MaterialComponent>().find {
                it.name == MaterialComponent.DEFAULT_MATERIAL_NAME
            } ?: createNewMaterial().apply {
                gameEntity.setPersistent(gameEntity.settings.copy(isVisible = false))
                setPersistent(MaterialComponentData(MaterialComponent.DEFAULT_MATERIAL_NAME, PbrShaderData()))
            }
        }

        logI { "Load project scenes" }
        projectData.checkConsistency()
        projectData.scenes.forEach { sceneData ->
            val scene = EditorScene(sceneData, this)
            scene.prepareScene()
            scene.applyComponents()
            _createdScenes[scene.sceneEntity.id] = scene
            sceneModCnt++
        }
    }

    fun onStart() {
        createdScenes.values.forEach { it.startScene() }
    }

    fun releaseScenes() {
        createdScenes.values.forEach { it.release() }
        _createdScenes.clear()
        sceneModCnt++
    }

    override fun release() {
        super.release()
        releaseScenes()
        materialScene.release()
    }

    fun nextId(): EntityId {
        return EntityId(nextId++)
    }

    /**
     * Checks if the given name exists and if so prepends an increasing number to it to make it unique. If the given
     * name already ends with a number, the number is replaced.
     */
    fun uniquifyName(name: String): String {
        val existingNames = entityData.values.map { it.settings.name }.toSet()
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

    suspend fun createNewMaterial(): MaterialComponent {
        val id = nextId()
        val materialData = GameEntityData(
            id = id,
            settings = GameEntitySettings("Material-${id.value}"),
            parentId = materialScene.sceneEntity.id
        )
        materialData.components += ComponentInfo(MaterialComponentData(materialData.settings.name, PbrShaderData()))
        return addMaterial(materialData)
    }

    suspend fun addMaterial(materialData: GameEntityData): MaterialComponent {
        if (materialData.components.none { it.data is MaterialComponentData }) {
            logW { "Adding material without material data, creating default" }
            materialData.components += ComponentInfo(MaterialComponentData(materialData.settings.name, PbrShaderData()))
        }
        val material = materialScene.addGameEntity(materialData)
        val materialComponent: MaterialComponent = material.requireComponent()
        _materialsById[material.id] = materialComponent
        materials += materialComponent
        materials.sortBy { it.name }
        return materialComponent
    }

    fun removeMaterial(material: MaterialComponent) {
        _materialsById -= material.id
        materials -= material
        materialScene.removeGameEntity(material.gameEntity)
    }

    companion object {
        suspend fun loadFromAssets(path: String = "kool-project.json"): EditorProject? {
            return try {
                Assets.loadBlob(path).getOrNull()?.toArray()?.decodeToString()?.let { json ->
                    EditorProject(Json.decodeFromString(json))
                }
            } catch (e: Exception) {
                null
            }
        }

        fun emptyProjectData(): ProjectData = ProjectData(ProjectMeta(ProjectData.MODEL_VERSION)).apply {
            val sceneId = EntityId(1L)
            val camId = EntityId(2L)
            val boxId = EntityId(3L)
            val lightId = EntityId(4L)

            scenes += SceneData(SceneMeta(sceneId, "New Scene")).apply {
                entities += GameEntityData(sceneId, EntityId.NULL, GameEntitySettings(meta.name)).apply {
                    components += ComponentInfo(SceneComponentData(cameraEntityId = camId, upAxis = SceneUpAxis.Z_AXIS))
                    components += ComponentInfo(SceneBackgroundComponentData(
                        SceneBackgroundData.SingleColor(ColorData(MdColor.GREY toneLin 900))
                    ))
                }
                entities += GameEntityData(camId, sceneId, GameEntitySettings("Camera")).apply {
                    components += ComponentInfo(CameraComponentData(CameraTypeData.Perspective()))
                    components += ComponentInfo(TransformComponentData(
                        TransformData(
                            MutableMat4d()
                                .translate(0.0, 2.5, 5.0)
                                .rotate((-30.0).deg, Vec3d.X_AXIS)
                        )))
                }
                entities += GameEntityData(boxId, sceneId, GameEntitySettings("Default cube")).apply {
                    components += ComponentInfo(MeshComponentData(ShapeData.Box(Vec3Data(1.0, 1.0, 1.0))))
                    components += ComponentInfo(MaterialReferenceComponentData(EntityId.NULL))
                }
                entities += GameEntityData(lightId, sceneId, GameEntitySettings("Directional light")).apply {
                    components += ComponentInfo(DiscreteLightComponentData(LightTypeData.Directional()))
                    components += ComponentInfo(TransformComponentData(
                        TransformData(
                            MutableMat4d()
                                .translate(5.0, 5.0, 5.0)
                                .rotate(0.0.deg, 30.0.deg, (-120.0).deg)
                        )))
                }
            }
        }
    }
}

fun ProjectData.allEntities(): List<GameEntityData> {
    return buildList {
        scenes.forEach { addAll(it.entities) }
        addAll(materials)
    }
}

fun ProjectData.checkConsistency() {
    if (meta.modelVersion != ProjectData.MODEL_VERSION) {
        logW { "Project data model version mismatch: ${meta.modelVersion} (current version is ${ProjectData.MODEL_VERSION})" }
    }

    scenes.forEach { scene ->
        val entityMap = scene.entities.associateBy { it.id }
        val parentsToChildren = mutableMapOf<EntityId?, MutableList<EntityId>>()
        val referencedEntityIds = mutableSetOf<EntityId>()

        scene.entities.forEach {
            if (it.parentId != EntityId.NULL && it.parentId !in entityMap) {
                logE { "Entity ${it.settings.name} references non-existing parent" }
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
        val roots = parentsToChildren[EntityId.NULL]?.mapNotNull { entityMap[it] } ?: emptyList()
        if (roots.size != 1) {
            logE { "Scene ${scene.meta.name} has ${roots.size} root entities" }
        }

        roots.forEach { root ->
            if (root.components.none { c -> c.data is SceneComponentData }) {
                logW { "Root entity ${root.settings.name} has no scene component" }
            }
            referencedEntityIds += root.id
            collectChildNodeIds(root)
        }

        val unreferencedIds = entityMap.keys - referencedEntityIds
        if (unreferencedIds.isNotEmpty()) {
            logE { "Project contains unreferenced entities: ${unreferencedIds.joinToString { "\n  $it: ${entityMap[it]?.settings?.name}" }}" }
        }
    }
}
