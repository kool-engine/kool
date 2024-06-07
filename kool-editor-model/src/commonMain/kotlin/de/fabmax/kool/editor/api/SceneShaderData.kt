package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DrawShader
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

class SceneShaderData(val scene: EditorScene) {
    private val listeners by CachedSceneComponents(scene, EditorScene.SceneShaderDataListener::class)

    val shaderCache = SceneShaderCache()

    var maxNumberOfLights: Int = 4
        set(value) {
            if (value != field) {
                field = value
                notifyChange()
            }
        }

    var environmentMaps: EnvironmentMaps? = null
        set(value) {
            if (value != field) {
                field = value
                notifyChange()
            }
        }

    var ambientColorLinear: Color = Color.BLACK
        set(value) {
            if (value != field) {
                field = value
                notifyChange()
            }
        }

    var shadowMaps = emptyList<ShadowMap>()
        set(value) {
            if (value != field) {
                field = value
                notifyChange()
            }
        }

    var ssaoMap: Texture2d? = null
        set(value) {
            if (value != field) {
                field = value
                notifyChange()
            }
        }

    fun addShadowMap(shadowMap: ShadowMap) {
        shadowMaps = shadowMaps + shadowMap
    }

    fun removeShadowMap(shadowMap: ShadowMap) {
        shadowMaps = shadowMaps - shadowMap
    }

    private fun notifyChange() {
        shaderCache.onSceneShaderDataChanged(scene, this)
        listeners.forEach { it.onSceneShaderDataChanged(scene, this) }
    }
}

class SceneShaderCache :
    EditorScene.SceneShaderDataListener
{
    private val materialShaders = mutableMapOf<EntityId, MutableMap<MeshShaderKey, DrawShader>>()

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        materialShaders.keys.forEach { matId ->
            scene.project.materialsById[matId]?.onSceneShaderDataChanged(scene, sceneShaderData)
        }
    }

    fun getShaderCache(material: MaterialComponent): MutableMap<MeshShaderKey, DrawShader>? {
        return materialShaders.get(material.id)
    }

    fun getOrPutShaderCache(material: MaterialComponent): MutableMap<MeshShaderKey, DrawShader> {
        return materialShaders.getOrPut(material.id) { mutableMapOf() }
    }
}

fun MeshShaderKey(gameEntity: GameEntity, mesh: Mesh): MeshShaderKey {
    return MeshShaderKey(
        sceneId = gameEntity.scene.sceneEntity.id,
        vertexLayout = mesh.geometry.vertexAttributes,
        instanceLayout = mesh.instances?.instanceAttributes ?: emptyList(),
        primitiveType = mesh.geometry.primitiveType
    )
}

data class MeshShaderKey(
    val sceneId: EntityId,
    val vertexLayout: List<Attribute>,
    val instanceLayout: List<Attribute>,
    val primitiveType: PrimitiveType
)
