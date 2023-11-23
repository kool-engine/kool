package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.model.UpdateMaxNumLightsComponent
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

class ModelComponent(nodeModel: SceneNodeModel, override val componentData: ModelComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<ModelComponentData>,
    ContentComponent,
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
        recreateModel()
    }

    val sceneIndexState = mutableStateOf(componentData.sceneIndex).onChange {
        if (AppState.isEditMode) {
            componentData.sceneIndex = it
        }
        recreateModel()
    }

    val animationIndexState = mutableStateOf(componentData.animationIndex).onChange {
        if (AppState.isEditMode) {
            componentData.animationIndex = it
        }
        model?.apply {
            enableAnimation(it)
        }
    }

    val gltfState = mutableStateOf<GltfFile?>(null)

    var model: Model? = null

    override val contentNode: Model?
        get() = model

    private val isRecreatingModel = atomic(false)
    private var isIblShaded = false
    private var isSsaoEnabled = false
    private var shaderShadowMaps: List<ShadowMap> = emptyList()

    init {
        dependsOn(MaterialComponent::class, isOptional = true)
    }

    override suspend fun createComponent() {
        super.createComponent()
        recreateModel()
    }

    override fun updateMaterial(material: MaterialData?) {
        val holder = nodeModel.getComponent<MaterialComponent>() ?: return
        val model = this.model
        if (holder.isHoldingMaterial(material)) {
            launchOnMainThread {
                if (material == null || model == null) {
                    // recreate model with default materials
                    recreateModel()
                } else {
                    // update model shaders and recreate model in case update fails
                    val updateFail = model.meshes.values.any {
                        !material.updateShader(it.shader, sceneModel.shaderData)
                    }
                    if (updateFail) {
                        recreateModel()
                    }
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorLinear: Color) {
        if (isIblShaded) {
            // recreate models without ibl lighting
            recreateModel()
        } else {
            model?.meshes?.values?.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientFactor = bgColorLinear
            }
        }
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (!isIblShaded) {
            // recreate models with ibl lighting
            recreateModel()
        } else {
            model?.meshes?.values?.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
    }

    override fun updateShadowMaps(shadowMaps: List<ShadowMap>) {
        if (shadowMaps != shaderShadowMaps) {
            recreateModel()
        }
    }

    override fun updateSsao(ssaoMap: Texture2d?) {
        val needsSsaoEnabled = ssaoMap != null
        if (needsSsaoEnabled != isSsaoEnabled) {
            // recreate models with changed ssao setting
            recreateModel()
        }
        model?.meshes?.values?.forEach { mesh ->
            (mesh.shader as? KslLitShader)?.ssaoMap = ssaoMap
        }
    }

    override fun updateMaxNumLightsComponent(newMaxNumLights: Int) {
        recreateModel()
    }

    private suspend fun createModel(): Model? {
        logD { "${nodeModel.name}: (re-)loading model" }

        shaderShadowMaps = sceneModel.shaderData.shadowMaps.copy()
        val ibl = sceneModel.shaderData.environmentMaps
        val ssao = sceneModel.shaderData.ssaoMap
        val material = nodeModel.getComponent<MaterialComponent>()?.materialData
        val modelCfg = GltfLoadConfig(
            materialConfig = GltfMaterialConfig(
                environmentMaps = ibl,
                shadowMaps = shaderShadowMaps,
                scrSpcAmbientOcclusionMap = ssao,
                maxNumberOfLights = sceneModel.maxNumLightsState.value
            ),
            applyMaterials = material == null
        )
        isIblShaded = ibl != null
        isSsaoEnabled = ssao != null

        val gltfFile = gltfState.value ?: AppAssets.loadModel(componentData.modelPath).also { gltfState.set(it) } ?: return null
        val loadScene = if (sceneIndexState.value in gltfFile.scenes.indices) sceneIndexState.value else 0

        val model = gltfFile.makeModel(modelCfg, loadScene)
        if (material != null) {
            model.meshes.forEach { (name, mesh) ->
                val shader = material.createShader(sceneModel.shaderData)
                val requiredAttribs = shader.findRequiredVertexAttributes()
                if (mesh.geometry.hasAttributes(requiredAttribs)) {
                    mesh.shader = shader
                } else {
                    logE {
                        "Model ${componentData.modelPath}: sub-mesh $name misses required vertex attributes to apply " +
                                "material: ${(requiredAttribs - mesh.geometry.vertexAttributes.toSet())}"
                    }
                }
            }
        }

        if (!isIblShaded) {
            val bgColor = sceneModel.shaderData.ambientColorLinear
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

        model.name = nodeModel.name
        model.isVisible = nodeModel.isVisibleState.value
        return model
    }

    private fun recreateModel() {
        if (!isRecreatingModel.getAndSet(true)) {
            launchOnMainThread {
                isRecreatingModel.lazySet(false)
                model = createModel()

                // set newly created model as new content node (or an empty Node in case model loading failed)
                // this also disposes any previous model
                nodeModel.setDrawNode(model ?: Node(nodeModel.name))
            }
        }
    }
}