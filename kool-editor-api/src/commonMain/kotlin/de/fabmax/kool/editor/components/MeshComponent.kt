package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.UpdateMaxNumLightsComponent
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.*
import kotlinx.atomicfu.atomic

class MeshComponent(override val componentData: MeshComponentData) :
    SceneNodeComponent(),
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

    override val contentNode: Node?
        get() = mesh

    private val isRecreatingShader = atomic(false)
    private var isIblShaded = false
    private var isSsaoEnabled = false

    constructor(): this(MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0))))

    init {
        dependsOn(MaterialComponent::class, isOptional = true)
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)

        mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS).apply {
            name = nodeModel.name
            isVisible = nodeModel.isVisibleState.value

            if (AppState.isInEditor) {
                rayTest = MeshRayTest.geometryTest(this)
            }
            this@MeshComponent.nodeModel.setContentNode(this)
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
    }

    private suspend fun createMeshShader() {
        val mesh = this.mesh ?: return

        logD { "${nodeModel.name}: (re-)creating shader" }
        val ibl = sceneModel.shaderData.environmentMaps
        val ssao = sceneModel.shaderData.ssaoMap

        val materialData = nodeModel.getComponent<MaterialComponent>()?.materialData
        if (materialData != null) {
            mesh.shader = materialData.createShader(ibl)

        } else {
            mesh.shader = KslPbrShader {
                color { uniformColor(MdColor.GREY.toLinear()) }
                shadow { addShadowMaps(sceneModel.shaderData.shadowMaps) }
                maxNumberOfLights = sceneModel.maxNumLightsState.value
                ibl?.let {
                    enableImageBasedLighting(ibl)
                }
                ssao?.let {
                    ao { enableSsao(it) }
                }
            }.apply {
                if (ibl == null) {
                    ambientFactor = sceneModel.shaderData.ambientColorLinear
                }
            }
        }
    }

    override fun updateMaterial(material: MaterialData?) {
        val mesh = this.mesh ?: return
        val holder = nodeModel.getComponent<MaterialComponent>()
        if (holder?.isHoldingMaterial(material) != false) {
            launchOnMainThread {
                val ibl = sceneModel.shaderData.environmentMaps
                if (material == null || !material.updateShader(mesh.shader, ibl)) {
                    createMeshShader()
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorLinear: Color) {
        if (isIblShaded) {
            // recreate shader without ibl lighting
            recreateShader()
        } else {
            (mesh?.shader as? KslLitShader)?.ambientFactor = bgColorLinear
        }
        isIblShaded = false
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (!isIblShaded) {
            // recreate shader without ibl lighting
            recreateShader()
        } else {
            (mesh?.shader as? KslLitShader)?.ambientMap = ibl.irradianceMap
            (mesh?.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
        }
        isIblShaded = true
    }

    override fun updateShadowMaps(shadowMaps: List<ShadowMap>) {
        (mesh?.shader as? KslLitShader)?.let {
            if (shadowMaps != it.shadowMaps) {
                recreateShader()
            }
        }
    }

    override fun updateSsao(ssaoMap: Texture2d?) {
        val needsSsaoEnabled = ssaoMap != null
        if (needsSsaoEnabled != isSsaoEnabled) {
            isSsaoEnabled = needsSsaoEnabled
            recreateShader()
        }
        (mesh?.shader as? KslLitShader)?.ssaoMap = ssaoMap
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