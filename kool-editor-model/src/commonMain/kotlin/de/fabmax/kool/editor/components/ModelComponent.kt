package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.*
import kotlinx.atomicfu.atomic

class ModelComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<ModelComponentData>
) :
    GameEntityDataComponent<ModelComponentData>(gameEntity, componentInfo),
    DrawNodeComponent,
    MaterialComponent.ListenerComponent,
    MaterialReferenceComponent.ListenerComponent,
    EditorScene.SceneShaderDataListener
{
    private var gltfPath: String? = null
    var gltfFile: GltfFile? = null
        private set

    override var drawNode: Model? = null
        private set

    private val isRecreatingModel = atomic(false)
    private var isIblShaded = false
    private var isSsaoEnabled = false
    private var maxNumLights = 4
    private var shaderShadowMaps: List<ShadowMap> = emptyList()

    init {
        dependsOn(MaterialReferenceComponent::class, isOptional = true)

        if (data.modelPath.isNotBlank()) {
            requiredAssets += AssetReference.Model(data.modelPath)
        }
    }

    override fun onDataChanged(oldData: ModelComponentData, newData: ModelComponentData) {
        recreateModelAsync(newData)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        recreateModel(data)
    }

    override fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
        val model = drawNode ?: return
        launchOnMainThread {
            if (material != null) {
                val updateFail = model.meshes.values.any { !material.applyMaterialTo(gameEntity, it) }
                if (updateFail) {
                    recreateModel(data)
                }
            } else {
                recreateModel(data)
            }
        }
    }

    override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
        val model = drawNode ?: return
        val holder = gameEntity.getComponent<MaterialReferenceComponent>() ?: return

        if (holder.isHoldingMaterial(component)) {
            val updateFail = model.meshes.values.any { !component.applyMaterialTo(gameEntity, it) }
            if (updateFail) {
                recreateModel(data)
            }
        }
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        if (gameEntity.hasComponent<MaterialReferenceComponent>()) {
            return
        }

        val ibl = sceneShaderData.environmentMaps
        val isIbl = ibl != null
        val isSsao = sceneShaderData.ssaoMap != null

        if (isIblShaded != isIbl) {
            recreateModelAsync(data)
        }
        if (maxNumLights != sceneShaderData.maxNumberOfLights) {
            recreateModelAsync(data)
        }
        if (shaderShadowMaps != sceneShaderData.shadowMaps) {
            recreateModelAsync(data)
        }
        if (isSsaoEnabled != isSsao) {
            recreateModelAsync(data)
        }

        drawNode?.meshes?.values?.forEach { mesh ->
            (mesh.shader as? KslLitShader)?.apply {
                ssaoMap = sceneShaderData.ssaoMap
                if (ibl != null) {
                    ambientFactor = Color.WHITE
                    ambientMap = ibl.irradianceMap
                    (this as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
                } else {
                    ambientFactor = sceneShaderData.ambientColorLinear
                }
            }
        }
    }

    private suspend fun createModel(data: ModelComponentData): Model? {
        logD { "${gameEntity.name}: (re-)loading model ${data.modelPath}" }

        val shaderData = scene.shaderData
        shaderShadowMaps = shaderData.shadowMaps.copy()
        val ibl = shaderData.environmentMaps
        val ssao = shaderData.ssaoMap
        val materialRef = gameEntity.getComponent<MaterialReferenceComponent>()?.material
        val modelCfg = GltfLoadConfig(
            materialConfig = GltfMaterialConfig(
                environmentMaps = ibl,
                shadowMaps = shaderShadowMaps,
                scrSpcAmbientOcclusionMap = ssao,
                maxNumberOfLights = shaderData.maxNumberOfLights
            ),
            applyMaterials = materialRef == null,
            assetLoader = AppAssets.assetLoader
        )
        isIblShaded = ibl != null
        isSsaoEnabled = ssao != null
        maxNumLights = shaderData.maxNumberOfLights

        if (gltfPath != data.modelPath) {
            gltfPath = data.modelPath
            gltfFile = AppAssets.loadModel(data.modelPath)
        }

        val gltf = gltfFile ?: return null
        val loadScene = if (data.sceneIndex in gltf.scenes.indices) data.sceneIndex else 0

        val model = gltf.makeModel(modelCfg, loadScene)
        if (materialRef != null) {
            model.meshes.values.forEach { materialRef.applyMaterialTo(gameEntity, it) }
        }

        if (!isIblShaded) {
            val bgColor = shaderData.ambientColorLinear
            model.meshes.values.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientFactor = bgColor
            }
        }

        if (AppState.isInEditor) {
            model.meshes.values.forEach { it.rayTest = MeshRayTest.geometryTest(it) }
        }

        if (data.animationIndex >= 0) {
            model.animations.getOrNull(data.animationIndex)?.weight = 1f
        }
        model.onUpdate {
            if (data.animationIndex >= 0) {
                model.applyAnimation(Time.deltaT)
            }
        }

        model.name = gameEntity.name
        model.isVisible = gameEntity.isVisibleState.value
        return model
    }

    private fun recreateModelAsync(data: ModelComponentData) {
        if (!isRecreatingModel.getAndSet(true)) {
            launchOnMainThread {
                recreateModel(data)
                isRecreatingModel.lazySet(false)
            }
        }
    }

    private suspend fun recreateModel(data: ModelComponentData) {
        drawNode = createModel(data)

        // set newly created model as new content node (or an empty Node in case model loading failed)
        // this also disposes any previous model
        gameEntity.replaceDrawNode(drawNode ?: Node(gameEntity.name))
    }
}