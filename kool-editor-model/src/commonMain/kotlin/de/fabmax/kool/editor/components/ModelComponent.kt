package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
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
    SceneBackgroundComponent.ListenerComponent,
    EditorScene.SceneShaderDataListener
{
    val modelPathState = mutableStateOf(data.modelPath).onChange {
        if (AppState.isEditMode) {
            data.modelPath = it
        }
        gltfState.set(null)
        recreateModelAsync()
    }

    val sceneIndexState = mutableStateOf(data.sceneIndex).onChange {
        if (AppState.isEditMode) {
            data.sceneIndex = it
        }
        recreateModelAsync()
    }

    val animationIndexState = mutableStateOf(data.animationIndex).onChange {
        if (AppState.isEditMode) {
            data.animationIndex = it
        }
        drawNode?.apply {
            enableAnimation(it)
        }
    }

    val gltfState = mutableStateOf<GltfFile?>(null)

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

    override suspend fun applyComponent() {
        super.applyComponent()
        recreateModel()
    }

    override fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
        launchOnMainThread {
            recreateModel()
        }
    }

    override fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
        val model = drawNode ?: return
        val holder = gameEntity.getComponent<MaterialReferenceComponent>() ?: return

        if (holder.isHoldingMaterial(component)) {
            launchOnMainThread {
                // update model shaders and recreate model in case update fails
                val updateFail = model.meshes.values.any {
                    !materialData.updateShader(it.shader, scene.shaderData)
                }
                if (updateFail) {
                    recreateModel()
                }
                model.meshes.values.forEach {
                    it.isCastingShadow = materialData.shaderData.genericSettings.isCastingShadow
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorLinear: Color) {
        if (isIblShaded) {
            // recreate models without ibl lighting
            recreateModelAsync()
        } else {
            drawNode?.meshes?.values?.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientFactor = bgColorLinear
            }
        }
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (!isIblShaded) {
            // recreate models with ibl lighting
            recreateModelAsync()
        } else {
            drawNode?.meshes?.values?.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
    }

    override fun onSceneShaderDataChanged(sceneShaderData: EditorScene.SceneShaderData) {
        if (sceneShaderData.maxNumberOfLights != maxNumLights) {
            recreateModelAsync()
        }
        if (shaderShadowMaps != sceneShaderData.shadowMaps) {
            recreateModelAsync()
        }

        val hasSsao = sceneShaderData.ssaoMap != null
        if (isSsaoEnabled != hasSsao) {
            recreateModelAsync()
        }
        drawNode?.meshes?.values?.forEach { mesh ->
            (mesh.shader as? KslLitShader)?.ssaoMap = sceneShaderData.ssaoMap
        }
    }

    private suspend fun createModel(): Model? {
        logD { "${gameEntity.name}: (re-)loading model" }

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

        val gltfFile = gltfState.value ?: AppAssets.loadModel(data.modelPath).also { gltfState.set(it) } ?: return null
        val loadScene = if (sceneIndexState.value in gltfFile.scenes.indices) sceneIndexState.value else 0

        val model = gltfFile.makeModel(modelCfg, loadScene)
        if (materialRef != null) {
            model.meshes.forEach { (name, mesh) ->
                val shader = materialRef.data.createShader(shaderData)
                val shaderOk = when (shader) {
                    is KslPbrShader -> {
                        val requiredAttribs = shader.findRequiredVertexAttributes()
                        if (mesh.geometry.hasAttributes(requiredAttribs)) true else {
                            logE {
                                "Model ${data.modelPath}: sub-mesh $name misses required vertex attributes " +
                                "to apply material: ${(requiredAttribs - mesh.geometry.vertexAttributes.toSet())}"
                            }
                            false
                        }
                    }
                    else -> true
                }
                if (shaderOk) {
                    mesh.shader = shader
                    mesh.isCastingShadow = materialRef.shaderData.genericSettings.isCastingShadow
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
        drawNode = createModel()

        // set newly created model as new content node (or an empty Node in case model loading failed)
        // this also disposes any previous model
        gameEntity.replaceDrawNode(drawNode ?: Node(gameEntity.name))
    }
}