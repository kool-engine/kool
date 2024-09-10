package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MaterialShaderData
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.logW

class MaterialComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MaterialComponentData>
) :
    GameEntityDataComponent<MaterialComponentData>(gameEntity, componentInfo),
    EditorScene.SceneShaderDataListener
{

    val id: EntityId get() = gameEntity.id
    val name: String get() = gameEntity.name

    val shaderData: MaterialShaderData get() = data.shaderData

    private val listeners by cachedProjectComponents<ListenerComponent>()

    suspend fun applyMaterialTo(mesh: Mesh, sceneShaderData: SceneShaderData, modelMats: List<ModelMatrixComposition>): Boolean {
        mesh.isCastingShadow = shaderData.genericSettings.isCastingShadow

        val meshKey = MeshLayoutInfo(mesh)
        val shader = sceneShaderData.shaderCache.getOrPutShaderCache(this).getOrPut(meshKey) {
            logT { "Creating new material shader $name (for mesh: ${mesh.name})" }
            data.createShader(meshKey, modelMats, sceneShaderData)
        }
        if (mesh.shader == shader) {
            return true
        }

        if (shader is KslShader && shader.findRequiredVertexAttributes().any { it !in mesh.geometry.vertexAttributes }) {
            val missing = shader.findRequiredVertexAttributes() - mesh.geometry.vertexAttributes.toSet()
            logW { "Material $name: Unable to apply material to mesh ${mesh.name}, missing attributes: $missing" }
            return false
        }
        mesh.shader = shader
        return true
    }

    override fun setPersistent(componentData: MaterialComponentData) {
        super.setPersistent(componentData)
        gameEntity.setPersistent(gameEntity.entityData.settings.copy(name = componentData.name))
    }

    override fun onDataChanged(oldData: MaterialComponentData, newData: MaterialComponentData) {
        gameEntity.name = newData.name
        launchOnMainThread {
            project.createdScenes.values.forEach { scene ->
                scene.shaderData.shaderCache.getShaderCache(this)?.let { shaders ->
                    val removeShaders = shaders.values.filter { !newData.updateShader(it, scene.shaderData) }
                    shaders.values -= removeShaders.toSet()
                }
            }
            listeners.forEach { it.onMaterialChanged(this, data) }
        }
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        val sceneShaders = scene.shaderData.shaderCache.getShaderCache(this) ?: return
        launchOnMainThread {
            val removeShaders = sceneShaders.values.filter { !data.updateShader(it, sceneShaderData) }
            sceneShaders.values -= removeShaders.toSet()
            listeners.forEach { it.onMaterialChanged(this, data) }
        }
    }

    companion object {
        const val DEFAULT_MATERIAL_NAME = "<\\Default/>"
    }

    fun interface ListenerComponent {
        suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData)
    }
}

fun MaterialComponent.isDefaultMaterial(): Boolean {
    return name == MaterialComponent.DEFAULT_MATERIAL_NAME
}