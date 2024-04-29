package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.model.UpdateMaxNumLightsComponent
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.util.*
import kotlinx.atomicfu.atomic

class MeshComponent(nodeModel: SceneNodeModel, override val componentData: MeshComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<MeshComponentData>,
    ContentComponent,
    UpdateMaterialComponent,
    UpdateSceneBackgroundComponent,
    UpdateShadowMapsComponent,
    UpdateSsaoComponent,
    UpdateMaxNumLightsComponent
{
    val shapesState = MutableStateList(componentData.shapes)

    var mesh: Mesh? = null

    override val contentNode: Mesh?
        get() = mesh

    private val isRecreatingShader = atomic(false)

    constructor(nodeModel: SceneNodeModel): this(nodeModel, MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0))))

    init {
        dependsOn(MaterialComponent::class, isOptional = true)
    }

    override suspend fun createComponent() {
        super.createComponent()

        mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS).apply {
            name = nodeModel.name
            isVisible = nodeModel.isVisibleState.value

            if (AppState.isInEditor) {
                rayTest = MeshRayTest.geometryTest(this)
            }
            nodeModel.setDrawNode(this)
        }

        updateGeometry()
        recreateShader()
    }

    fun updateGeometry() {
        val mesh = this.mesh ?: return
        mesh.generate {
            shapesState.forEach { shape ->
                withTransform {
                    shape.common.pose.toMat4f(transform)
                    color = shape.common.vertexColor.toColorLinear()
                    vertexModFun = {
                        texCoord.x *= shape.common.uvScale.x.toFloat()
                        texCoord.y *= shape.common.uvScale.y.toFloat()
                    }
                    shape.generate(this)
                }
            }
            geometry.generateTangents()
        }

        // force ray test mesh update
        mesh.rayTest.onMeshDataChanged(mesh)

        if (isCreated) {
            launchOnMainThread {
                nodeModel.getComponents<UpdateMeshComponent>().forEach { it.updateMesh(componentData) }
            }
        }
    }

    private suspend fun createMeshShader() {
        val mesh = this.mesh ?: return

        val sceneShaderData = sceneModel.shaderData

        val materialData = nodeModel.getComponent<MaterialComponent>()?.materialData
        if (materialData != null) {
            logD { "${nodeModel.name}: (re-)creating shader for material: ${materialData.name}" }

            mesh.shader = materialData.createShader(sceneShaderData)

        } else {
            logD { "${nodeModel.name}: (re-)creating shader for default material" }
            mesh.shader = KslPbrShader {
                color { uniformColor(MdColor.GREY.toLinear()) }
                shadow { addShadowMaps(sceneShaderData.shadowMaps) }
                maxNumberOfLights = sceneShaderData.maxNumberOfLights
                sceneShaderData.environmentMaps?.let {
                    enableImageBasedLighting(it)
                }
                sceneShaderData.ssaoMap?.let {
                    ao { enableSsao(it) }
                }
            }.apply {
                if (sceneShaderData.environmentMaps == null) {
                    ambientFactor = sceneShaderData.ambientColorLinear
                }
            }
        }
    }

    override fun updateMaterial(material: MaterialData?) {
        val mesh = this.mesh ?: return
        val holder = nodeModel.getComponent<MaterialComponent>()
        if (holder?.isHoldingMaterial(material) == true) {
            launchOnMainThread {
                if (material == null || !material.updateShader(mesh.shader, sceneModel.shaderData)) {
                    createMeshShader()
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorLinear: Color) {
        val shader = mesh?.shader as? KslLitShader ?: return
        if (shader.ambientCfg !is KslLitShader.AmbientColor.Uniform) {
            recreateShader()
        } else {
            (mesh?.shader as? KslLitShader)?.ambientFactor = bgColorLinear
        }
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        val shader = mesh?.shader as? KslLitShader ?: return
        val pbrShader = shader as? KslPbrShader
        if (shader.ambientCfg !is KslLitShader.AmbientColor.ImageBased) {
            recreateShader()
        } else {
            shader.ambientMap = ibl.irradianceMap
            pbrShader?.reflectionMap = ibl.reflectionMap
        }
    }

    override fun updateShadowMaps(shadowMaps: List<ShadowMap>) {
        (mesh?.shader as? KslLitShader)?.let {
            if (shadowMaps != it.shadowMaps) {
                recreateShader()
            }
        }
    }

    override fun updateSsao(ssaoMap: Texture2d?) {
        val shader = mesh?.shader as? KslLitShader ?: return
        val needsSsaoEnabled = ssaoMap != null
        if (shader.isSsao != needsSsaoEnabled) {
            recreateShader()
        }
        shader.ssaoMap = ssaoMap
    }

    override fun updateMaxNumLightsComponent(newMaxNumLights: Int) {
        recreateShader()
    }

    private fun recreateShader() {
        if (!isRecreatingShader.getAndSet(true)) {
            launchOnMainThread {
                isRecreatingShader.lazySet(false)
                createMeshShader()
            }
        }
    }
}

interface UpdateMeshComponent {
    fun updateMesh(mesh: MeshComponentData)
}