package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.launchOnMainThread

class MeshComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MeshComponentData> = ComponentInfo(MeshComponentData(ShapeData.Box()))
) :
    GameEntityDataComponent<MeshComponentData>(gameEntity, componentInfo),
    MaterialComponent.ListenerComponent,
    MaterialReferenceComponent.ListenerComponent,
    EditorScene.SceneShaderDataListener
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
        launchOnMainThread {
            if (oldData.shapes != newData.shapes) {
                updateDrawNode(newData)
            }
        }
    }

    fun addInstanceData(target: Float32Buffer) {
        gameEntity.localToGlobalF.putTo(target)
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
            .map { it.toAssetRef() }
            .filter { it.path != null }
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

    override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
//        val holder = gameEntity.getComponent<MaterialReferenceComponent>() ?: return
//        if (holder.isHoldingMaterial(component)) {
//            meshHolder.onMaterialChanged(component, materialData)
//            modelHolder.onMaterialChanged(component, materialData)
//        }
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
//        modelHolder.onSceneShaderDataChanged(scene, sceneShaderData)
    }

    companion object {
        const val DEFAULT_HEIGHTMAP_ROWS = 129
        const val DEFAULT_HEIGHTMAP_COLS = 129
    }

    interface ListenerComponent {
        suspend fun onMeshGeometryChanged(component: MeshComponent, newData: MeshComponentData)
    }
}