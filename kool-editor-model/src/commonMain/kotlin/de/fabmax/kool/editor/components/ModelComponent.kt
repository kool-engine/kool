package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.*
import kotlinx.atomicfu.atomic

class ModelComponent(gameEntity: GameEntity, componentData: ModelComponentData) :
    GameEntityDataComponent<ModelComponentData>(gameEntity, componentData),
    DrawNodeComponent<Model>,
    UpdateMaterialComponent,
    UpdateSceneBackgroundComponent,
    UpdateShadowMapsComponent,
    UpdateSsaoComponent,
    UpdateMaxNumLightsComponent
{
    val modelPathState = mutableStateOf(componentData.modelPath).onChange {
        if (AppState.isEditMode) {
            componentData.modelPath = it
        }
        gltfState.set(null)
        recreateModelAsync()
    }

    val sceneIndexState = mutableStateOf(componentData.sceneIndex).onChange {
        if (AppState.isEditMode) {
            componentData.sceneIndex = it
        }
        recreateModelAsync()
    }

    val animationIndexState = mutableStateOf(componentData.animationIndex).onChange {
        if (AppState.isEditMode) {
            componentData.animationIndex = it
        }
        typedDrawNode?.apply {
            enableAnimation(it)
        }
    }

    val gltfState = mutableStateOf<GltfFile?>(null)

    override var typedDrawNode: Model? = null
        private set

    private val isRecreatingModel = atomic(false)
    private var isIblShaded = false
    private var isSsaoEnabled = false
    private var shaderShadowMaps: List<ShadowMap> = emptyList()

    init {
        dependsOn(MaterialComponent::class, isOptional = true)

        if (componentData.modelPath.isNotBlank()) {
            requiredAssets += AssetReference.Model(componentData.modelPath)
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        recreateModel()
    }

    override fun updateMaterial(material: MaterialData?) {
        val holder = gameEntity.getComponent<MaterialComponent>() ?: return
        val model = typedDrawNode
        if (holder.isHoldingMaterial(material)) {
            launchOnMainThread {
                if (material == null || model == null) {
                    // recreate model with default materials
                    recreateModel()
                } else {
                    // update model shaders and recreate model in case update fails
                    val updateFail = model.meshes.values.any {
                        !material.updateShader(it.shader, sceneComponent.shaderData)
                    }
                    if (updateFail) {
                        recreateModel()
                    }
                    model.meshes.values.forEach {
                        it.isCastingShadow = material.shaderData.genericSettings.isCastingShadow
                    }
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorLinear: Color) {
        if (isIblShaded) {
            // recreate models without ibl lighting
            recreateModelAsync()
        } else {
            typedDrawNode?.meshes?.values?.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientFactor = bgColorLinear
            }
        }
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (!isIblShaded) {
            // recreate models with ibl lighting
            recreateModelAsync()
        } else {
            typedDrawNode?.meshes?.values?.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
    }

    override fun updateShadowMaps(shadowMaps: List<ShadowMap>) {
        if (shadowMaps != shaderShadowMaps) {
            recreateModelAsync()
        }
    }

    override fun updateSsao(ssaoMap: Texture2d?) {
        val needsSsaoEnabled = ssaoMap != null
        if (needsSsaoEnabled != isSsaoEnabled) {
            // recreate models with changed ssao setting
            recreateModelAsync()
        }
        typedDrawNode?.meshes?.values?.forEach { mesh ->
            (mesh.shader as? KslLitShader)?.ssaoMap = ssaoMap
        }
    }

    override fun updateMaxNumLightsComponent(newMaxNumLights: Int) {
        recreateModelAsync()
    }

    private suspend fun createModel(): Model? {
        logD { "${gameEntity.name}: (re-)loading model" }

        val shaderData = sceneComponent.shaderData
        shaderShadowMaps = shaderData.shadowMaps.copy()
        val ibl = shaderData.environmentMaps
        val ssao = shaderData.ssaoMap
        val material = gameEntity.getComponent<MaterialComponent>()?.materialData
        val modelCfg = GltfLoadConfig(
            materialConfig = GltfMaterialConfig(
                environmentMaps = ibl,
                shadowMaps = shaderShadowMaps,
                scrSpcAmbientOcclusionMap = ssao,
                maxNumberOfLights = sceneComponent.maxNumLightsState.value
            ),
            applyMaterials = material == null,
            assetLoader = AppAssets.assetLoader
        )
        isIblShaded = ibl != null
        isSsaoEnabled = ssao != null

        val gltfFile = gltfState.value ?: AppAssets.loadModel(componentData.modelPath).also { gltfState.set(it) } ?: return null
        val loadScene = if (sceneIndexState.value in gltfFile.scenes.indices) sceneIndexState.value else 0

        val model = gltfFile.makeModel(modelCfg, loadScene)
        if (material != null) {
            model.meshes.forEach { (name, mesh) ->
                val shader = material.createShader(shaderData)
                val shaderOk = when (shader) {
                    is KslPbrShader -> {
                        val requiredAttribs = shader.findRequiredVertexAttributes()
                        if (mesh.geometry.hasAttributes(requiredAttribs)) true else {
                            logE {
                                "Model ${componentData.modelPath}: sub-mesh $name misses required vertex attributes " +
                                "to apply material: ${(requiredAttribs - mesh.geometry.vertexAttributes.toSet())}"
                            }
                            false
                        }
                    }
                    else -> true
                }
                if (shaderOk) {
                    mesh.shader = shader
                    mesh.isCastingShadow = material.shaderData.genericSettings.isCastingShadow
                }
            }
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

        if (animationIndexState.value >= 0) {
            model.animations.getOrNull(animationIndexState.value)?.weight = 1f
        }
        model.onUpdate {
            if (animationIndexState.value >= 0) {
                model.applyAnimation(Time.deltaT)
            }
        }

        model.name = gameEntity.name
        model.isVisible = gameEntity.isVisibleState.value
        return model
    }

    private fun recreateModelAsync() {
        if (!isRecreatingModel.getAndSet(true)) {
            launchOnMainThread {
                recreateModel()
                isRecreatingModel.lazySet(false)
            }
        }
    }

    private suspend fun recreateModel() {
        typedDrawNode = createModel()

        // set newly created model as new content node (or an empty Node in case model loading failed)
        // this also disposes any previous model
        gameEntity.replaceDrawNode(typedDrawNode ?: Node(gameEntity.name))
    }
}