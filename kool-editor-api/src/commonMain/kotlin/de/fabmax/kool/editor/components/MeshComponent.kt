package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
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

class MeshComponent(override val componentData: MeshComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<MeshComponentData>,
    ContentComponent,
    UpdateMaterialComponent,
    UpdateSceneBackgroundComponent,
    UpdateShadowMapsComponent,
    UpdateSsaoComponent
{
    val shapesState = MutableStateList(componentData.shapes)

    private var _mesh: Mesh? = null
    val mesh: Mesh
        get() = requireNotNull(_mesh) { "MeshComponent was not yet created" }

    override val contentNode: Node
        get() = mesh

    private var isIblShaded = false
    private var isSsaoEnabled = false

    private var shaderUpdateScheduled = false

    constructor(): this(MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0))))

    init {
        dependsOn(MaterialComponent::class, isOptional = true)
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)

        _mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
        mesh.name = nodeModel.name
        mesh.rayTest = MeshRayTest.geometryTest(mesh)

        updateGeometry()
        createMeshShader()

        this.nodeModel.setContentNode(mesh)
    }

    fun updateGeometry() {
        mesh.generate {
            shapesState.forEach { shape ->
                withTransform {
                    shape.pose.toMat4f(transform)
                    color = shape.vertexColor.toColorLinear()
                    vertexModFun = {
                        texCoord.x *= shape.uvScale.x.toFloat()
                        texCoord.y *= shape.uvScale.y.toFloat()
                    }
                    shape.generate(this)
                }
            }
            geometry.generateTangents()
        }
    }

    private suspend fun createMeshShader(updateBg: Boolean = true) {
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
                ibl?.let {
                    enableImageBasedLighting(ibl)
                }
                ssao?.let {
                    ao { enableSsao(it) }
                }
            }
        }
        if (updateBg) updateBackground(sceneModel.sceneBackground)
        shaderUpdateScheduled = false
    }

    override fun updateMaterial(material: MaterialData?) {
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
        if (mesh.shader is KslLitShader) {
            if (isIblShaded) {
                launchOnMainThread {
                    createMeshShader(updateBg = false)
                    (mesh.shader as KslLitShader).ambientFactor = bgColorLinear
                }
            } else {
                (mesh.shader as KslLitShader).ambientFactor = bgColorLinear
            }
        }
        isIblShaded = false
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (mesh.shader is KslLitShader) {
            if (!isIblShaded) {
                launchOnMainThread {
                    createMeshShader(updateBg = false)
                }
            } else {
                (mesh.shader as? KslLitShader)?.ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
        isIblShaded = true
    }

    override fun updateShadowMaps(shadowMaps: List<ShadowMap>) {
        (mesh.shader as? KslLitShader)?.let {
            if (shadowMaps != it.shadowMaps) {
                launchOnMainThread {
                    createMeshShader(updateBg = false)
                }
            }
        }
    }

    override fun updateSsao(ssaoMap: Texture2d?) {
        val needsSsaoEnabled = ssaoMap != null
        if (needsSsaoEnabled != isSsaoEnabled) {
            isSsaoEnabled = needsSsaoEnabled
            launchOnMainThread {
                createMeshShader(updateBg = false)
            }
        }
        (mesh.shader as? KslLitShader)?.ssaoMap = ssaoMap
    }
}