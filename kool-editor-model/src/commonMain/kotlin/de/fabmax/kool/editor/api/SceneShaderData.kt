package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.modules.ksl.blocks.ToneMapping
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DrawShader
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

class SceneShaderData(val scene: EditorScene, private val isNotifying: Boolean = true) {
    private val listeners by CachedSceneComponents(scene, EditorScene.SceneShaderDataListener::class)

    val shaderCache = SceneShaderCache()

    var maxNumberOfLights: Int = 4
        set(value) {
            if (value != field) {
                field = value
                notifyChange()
            }
        }

    var toneMapping: ToneMapping = ToneMapping.Aces
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

    fun set(other: SceneShaderData) {
        maxNumberOfLights = other.maxNumberOfLights
        toneMapping = other.toneMapping
        environmentMaps = other.environmentMaps
        ambientColorLinear = other.ambientColorLinear
        shadowMaps = other.shadowMaps
        ssaoMap = other.ssaoMap
    }

    private fun notifyChange() {
        if (isNotifying) {
            shaderCache.onSceneShaderDataChanged(scene, this)
            listeners.forEach { it.onSceneShaderDataChanged(scene, this) }
        }
    }
}

class SceneShaderCache : EditorScene.SceneShaderDataListener {
    private val materialShaders = mutableMapOf<EntityId, MutableMap<MeshLayoutKey, DrawShader>>()

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        materialShaders.keys.forEach { matId ->
            scene.project.materialsById[matId]?.onSceneShaderDataChanged(scene, sceneShaderData)
        }
    }

    fun getShaderCache(material: MaterialComponent): MutableMap<MeshLayoutKey, DrawShader>? {
        return materialShaders[material.id]
    }

    fun getOrPutShaderCache(material: MaterialComponent): MutableMap<MeshLayoutKey, DrawShader> {
        return materialShaders.getOrPut(material.id) { mutableMapOf() }
    }
}

fun MeshLayoutKey(mesh: Mesh): MeshLayoutKey = MeshLayoutKey(
    vertexLayout = mesh.geometry.vertexAttributes,
    instanceLayout = mesh.instances?.instanceAttributes ?: emptyList(),
    primitiveType = mesh.geometry.primitiveType
)

data class MeshLayoutKey(
    val vertexLayout: List<Attribute>,
    val instanceLayout: List<Attribute>,
    val primitiveType: PrimitiveType
)
