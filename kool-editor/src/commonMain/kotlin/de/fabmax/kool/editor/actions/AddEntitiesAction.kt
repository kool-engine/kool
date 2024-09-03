package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.EditorDefaults
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.toHierarchy
import de.fabmax.kool.editor.components.globalToLocalD
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.globalLookAt
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.QuatD
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.util.launchOnMainThread

class AddEntitiesAction(
    addEntityDatas: List<GameEntityData>
) : EditorAction {

    private val hierarchy = addEntityDatas.toHierarchy()

    override fun doAction() {
        launchOnMainThread {
            hierarchy.forEach {
                val scene = it.entityData.parentId.gameEntity?.scene
                scene?.addGameEntities(it)
            }
            KoolEditor.instance.selectionOverlay.setSelection(hierarchy.mapNotNull { it.entityData.id.gameEntity })
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        KoolEditor.instance.selectionOverlay.reduceSelection(hierarchy.mapNotNull { it.entityData.id.gameEntity })
        hierarchy.forEach { root ->
            val entity = root.entityData.id.gameEntity
            val scene = entity?.scene
            entity?.let { scene?.removeGameEntity(it) }
        }
        refreshComponentViews()
    }
}

private fun makeTransformComponent(parent: GameEntity?, position: Vec3d?, rotation: QuatD = QuatD.IDENTITY): ComponentInfo<TransformComponentData> {
    val editor = KoolEditor.instance
    val globalPos = position ?: editor.overlayScene.lastPickPosition ?: editor.globalLookAt
    val localPos = parent?.globalToLocalD?.transform(MutableVec3d(globalPos)) ?: globalPos
    val transform = TransformData(Vec3Data(localPos), Vec4Data(rotation), Vec3Data(Vec3d.ONES))
    return ComponentInfo(TransformComponentData(transform), displayOrder = 0)
}

fun EditorScene.addMesh(parent: GameEntity?, meshShape: ShapeData, pos: Vec3d? = null) {
    val id = project.nextId()
    val parentId = parent?.id ?: sceneEntity.id
    val shapeName = (meshShape as? ShapeData.Model)?.modelPath
        ?.substringAfterLast('/')?.removeSuffix(".gz")?.substringBeforeLast('.')
        ?: meshShape.name
    val entityData = GameEntityData(id, parentId, GameEntitySettings(project.uniquifyName(shapeName)))

    entityData.components += makeTransformComponent(parent, pos)
    entityData.components += ComponentInfo(MeshComponentData(meshShape), displayOrder = 1)
    entityData.components += ComponentInfo(MaterialReferenceComponentData(EntityId.NULL), displayOrder = 2)
    AddEntitiesAction(listOf(entityData)).apply()
}

fun EditorScene.addJoint(parent: GameEntity?, joint: JointData, pos: Vec3d? = null) {
    val id = project.nextId()
    val parentId = parent?.id ?: sceneEntity.id
    val entityData = GameEntityData(id, parentId, GameEntitySettings(project.uniquifyName(joint::class.simpleName ?: "joint")))

    entityData.components += makeTransformComponent(parent, pos)
    entityData.components += ComponentInfo(JointComponentData(jointData = joint), displayOrder = 1)
    AddEntitiesAction(listOf(entityData)).apply()
}

fun EditorScene.addLight(parent: GameEntity?, lightType: LightTypeData, pos: Vec3d? = null) {
    val id = project.nextId()
    val parentId = parent?.id ?: sceneEntity.id
    val name = project.uniquifyName(lightType.name)
    val entityData = GameEntityData(id, parentId, GameEntitySettings(name))

    val rot = if (lightType is LightTypeData.Point) QuatD.IDENTITY else EditorDefaults.DEFAULT_LIGHT_ROTATION
    entityData.components += makeTransformComponent(parent, pos, rot)
    entityData.components += ComponentInfo(DiscreteLightComponentData(lightType), displayOrder = 1)
    AddEntitiesAction(listOf(entityData)).apply()
}

fun EditorScene.addCamera(parent: GameEntity?, pos: Vec3d? = null) {
    val id = project.nextId()
    val parentId = parent?.id ?: sceneEntity.id
    val name = project.uniquifyName("Camera")
    val entityData = GameEntityData(id, parentId, GameEntitySettings(name))

    entityData.components += makeTransformComponent(parent, pos)
    entityData.components += ComponentInfo(CameraComponentData(CameraTypeData.Perspective()), displayOrder = 1)
    AddEntitiesAction(listOf(entityData)).apply()
}

fun EditorScene.addEmptyNode(parent: GameEntity?, pos: Vec3d? = null) {
    val id = project.nextId()
    val parentId = parent?.id ?: sceneEntity.id
    val name = project.uniquifyName("Empty")
    val entityData = GameEntityData(id, parentId, GameEntitySettings(name))
    entityData.components += makeTransformComponent(parent, pos)
    AddEntitiesAction(listOf(entityData)).apply()
}
