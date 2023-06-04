package de.fabmax.kool.editor.components

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE

class ModelComponent(override val componentData: ModelComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<ModelComponentData>,
    UpdateMaterialComponent,
    UpdateSceneBackgroundComponent
{
    val modelPathState = mutableStateOf(componentData.modelPath).onChange { componentData.modelPath = it }

    private var _model: Model? = null
    val model: Model
        get() = _model ?: throw IllegalStateException("ModelComponent was not yet created")

    private var isIblShaded = false

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
        _model = createModel(scene.sceneBackground.loadedEnvironmentMaps)
    }

    override suspend fun initComponent(nodeModel: EditorNodeModel) {
        super.initComponent(nodeModel)
        sceneNode.getComponent<MaterialComponent>()?.let { updateMaterial(it.materialData) }
    }

    override fun updateMaterial(material: MaterialData?) {
        val holder = sceneNode.getComponent<MaterialComponent>() ?: return
        if (holder.isHoldingMaterial(material)) {
            launchOnMainThread {
                val ibl = scene.sceneBackground.loadedEnvironmentMaps
                val bgColor = (scene.sceneBackground.backgroundState.value as? SceneBackgroundData.SingleColor)?.color?.toColor()?.toLinear()

                if (material == null) {
                    // recreate model with default materials
                    recreateModel(ibl, bgColor)
                } else {
                    // update model shaders and recreate model in case update fails
                    val updateFail = model.meshes.values.any {
                        !material.updateShader(it.shader, ibl)
                    }
                    if (updateFail) {
                        recreateModel(ibl, bgColor)
                    }
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorSrgb: Color) {
        val linColor = bgColorSrgb.toLinear()
        if (isIblShaded) {
            // recreate models without ibl lighting
            recreateModel(null, linColor)
        } else {
            model.meshes.values.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientFactor = linColor
            }
        }
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (!isIblShaded) {
            // recreate models with ibl lighting
            recreateModel(ibl, null)
        } else {
            model.meshes.values.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
    }

    private suspend fun createModel(ibl: EnvironmentMaps?): Model {
        val material = sceneNode.getComponent<MaterialComponent>()?.materialData
        val modelCfg = GltfFile.ModelGenerateConfig(
            materialConfig = GltfFile.ModelMaterialConfig(environmentMaps = ibl),
            applyMaterials = material == null
        )
        isIblShaded = ibl != null

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
        return model
    }

    private fun recreateModel(ibl: EnvironmentMaps?, bgColor: Color?) {
        launchOnMainThread {
            val oldModel = model
            _model = createModel(ibl)

            if (sceneNode.node == oldModel) {
                sceneNode.replaceCreatedNode(model)
            } else {
                val idx = sceneNode.node.children.indexOf(oldModel)
                sceneNode.node.removeNode(oldModel)
                sceneNode.node.addNode(model, idx)
                oldModel.dispose(KoolSystem.requireContext())
            }

            bgColor?.let {
                model.meshes.values.forEach { mesh ->
                    (mesh.shader as? KslLitShader)?.ambientFactor = bgColor
                }
            }
        }
    }
}