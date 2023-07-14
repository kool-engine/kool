package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.modules.gltf.GltfFile
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

class ModelComponent(override val componentData: ModelComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<ModelComponentData>,
    ContentComponent,
    UpdateMaterialComponent,
    UpdateSceneBackgroundComponent,
    UpdateShadowMapsComponent,
    UpdateSsaoComponent
{
    val modelPathState = mutableStateOf(componentData.modelPath).onChange { componentData.modelPath = it }

    var model: Model? = null

    override val contentNode: Node?
        get() = model

    private val isRecreatingModel = atomic(false)
    private var isIblShaded = false
    private var isSsaoEnabled = false
    private var shaderShaowMaps: List<ShadowMap> = emptyList()

    init {
        dependsOn(MaterialComponent::class, isOptional = true)
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
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
                        !material.updateShader(it.shader, sceneModel.shaderData.environmentMaps)
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
        if (shadowMaps != shaderShaowMaps) {
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

    private suspend fun createModel(): Model {
        logD { "${nodeModel.name}: (re-)loading model" }

        shaderShaowMaps = sceneModel.shaderData.shadowMaps.copy()
        val ibl = sceneModel.shaderData.environmentMaps
        val ssao = sceneModel.shaderData.ssaoMap
        val material = nodeModel.getComponent<MaterialComponent>()?.materialData
        val modelCfg = GltfFile.ModelGenerateConfig(
            materialConfig = GltfFile.ModelMaterialConfig(environmentMaps = ibl, shadowMaps = shaderShaowMaps, scrSpcAmbientOcclusionMap = ssao),
            applyMaterials = material == null
        )
        isIblShaded = ibl != null
        isSsaoEnabled = ssao != null

        val model = AppAssets.loadModel(componentData).makeModel(modelCfg)
        if (material != null) {
            model.meshes.forEach { (name, mesh) ->
                val shader = material.createShader(ibl)
                if (mesh.geometry.hasAttributes(shader.requiredVertexAttributes)) {
                    mesh.shader = shader
                } else {
                    logE {
                        "Model ${componentData.modelPath}: sub-mesh $name misses required vertex attributes to apply " +
                                "material: ${(shader.requiredVertexAttributes - mesh.geometry.vertexAttributes.toSet())}"
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

        model.name = nodeModel.name
        model.isVisible = nodeModel.isVisibleState.value
        return model
    }

    private fun recreateModel() {
        if (!isRecreatingModel.getAndSet(true)) {
            launchOnMainThread {
                isRecreatingModel.lazySet(false)
                model = createModel().also {
                    // set newly created model as new content node, this also disposes any previous model
                    nodeModel.setContentNode(it)
                }
            }
        }
    }
}