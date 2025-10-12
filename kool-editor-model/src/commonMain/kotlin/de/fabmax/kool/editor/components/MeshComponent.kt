package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.SceneNodes
import de.fabmax.kool.editor.api.cachedEntityComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.scene.InstanceLayouts
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.FrontendScope
import de.fabmax.kool.util.StructBuffer
import kotlinx.coroutines.launch

class MeshComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MeshComponentData> = ComponentInfo(MeshComponentData(ShapeData.Box()))
) :
    GameEntityDataComponent<MeshComponentData>(gameEntity, componentInfo),
    SceneNodeComponent,
    MaterialReferenceComponent.ListenerComponent
{
    private var meshKey: SceneNodes.MeshKey? = null
    var sceneNode: Node? = null
        private set

    private val listeners by cachedEntityComponents<ListenerComponent>()

    init {
        dependsOn(MaterialReferenceComponent::class, isOptional = true)
        updateRequiredAssets()
    }

    override fun onDataChanged(oldData: MeshComponentData, newData: MeshComponentData) {
        FrontendScope.launch {
            if (oldData.shapes != newData.shapes) {
                updateDrawNode(newData)
            }
        }
    }

    fun addInstanceData(target: StructBuffer<InstanceLayouts.ModelMat>) {
        target.put { set(it.modelMat, gameEntity.localToViewF) }
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateDrawNode(data)
    }

    override fun destroyComponent() {
        meshKey?.let { scene.sceneNodes.removeUser(it, this) }

        super.destroyComponent()
    }

    private fun updateRequiredAssets() {
        requiredAssets.clear()
        requiredAssets += data.shapes
            .filterIsInstance<ShapeData.AssetBased>()
            .mapNotNull { it.toAssetRef() }
    }

    private suspend fun updateDrawNode(data: MeshComponentData) {
        val material = gameEntity.getComponent<MaterialReferenceComponent>()?.materialId ?: EntityId.NULL
        val newMeshKey = SceneNodes.MeshKey(data.shapes, material, gameEntity.drawGroupId)
        if (newMeshKey != meshKey) {
            updateRequiredAssets()
            meshKey?.let { scene.sceneNodes.removeUser(it, this) }
            sceneNode = scene.sceneNodes.useNode(newMeshKey, this)
            meshKey = newMeshKey
            listeners.forEach { it.onMeshGeometryChanged(this, data) }
        }
    }

    override suspend fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
        updateDrawNode(data)
    }

    companion object {
        const val DEFAULT_HEIGHTMAP_ROWS = 129
        const val DEFAULT_HEIGHTMAP_COLS = 129
    }

    interface ListenerComponent {
        suspend fun onMeshGeometryChanged(component: MeshComponent, newData: MeshComponentData)
    }
}