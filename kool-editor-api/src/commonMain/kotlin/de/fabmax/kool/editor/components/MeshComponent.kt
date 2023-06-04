package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logD

class MeshComponent(override val componentData: MeshComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<MeshComponentData>,
    UpdateMaterialComponent,
    UpdateSceneBackgroundComponent
{
    val shapesState = MutableStateList(componentData.shapes)

    private var _mesh: Mesh? = null
    val mesh: Mesh
        get() = _mesh ?: throw IllegalStateException("MeshComponent was not yet created")

    private var isIblShaded = false

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
        _mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
        mesh.rayTest = MeshRayTest.geometryTest(mesh)
        updateGeometry()
    }

    override suspend fun initComponent(nodeModel: EditorNodeModel) {
        createMeshShader()
    }

    fun updateGeometry() {
        mesh.generate {
            shapesState.forEach {
                withTransform {
                    it.pose.toMat4f(transform)
                    color = it.vertexColor.toColor()
                    vertexModFun = {
                        texCoord.x *= it.uvScale.x.toFloat()
                        texCoord.y *= it.uvScale.y.toFloat()
                    }
                    it.generate(this)
                }
            }
            geometry.generateTangents()
        }
    }

    private suspend fun createMeshShader(updateBg: Boolean = true) {
        logD { "${sceneNode.name}: (re-)creating shader" }
        val ibl = scene.sceneBackground.loadedEnvironmentMaps
        val materialData = sceneNode.getComponent<MaterialComponent>()?.materialData
        if (materialData != null) {
            mesh.shader = materialData.createShader(ibl)
        } else {
            mesh.shader = KslPbrShader {
                color { uniformColor(MdColor.GREY.toLinear()) }
                ibl?.let {
                    enableImageBasedLighting(ibl)
                }
            }
        }
        if (updateBg) updateBackground(scene.sceneBackground)
    }

    override fun updateMaterial(material: MaterialData?) {
        val holder = sceneNode.getComponent<MaterialComponent>()
        if (holder?.isHoldingMaterial(material) != false) {
            launchOnMainThread {
                val ibl = scene.sceneBackground.loadedEnvironmentMaps
                if (material == null || !material.updateShader(mesh.shader, ibl)) {
                    createMeshShader()
                }
            }
        }
    }

    override fun updateSingleColorBg(bgColorSrgb: Color) {
        if (mesh.shader is KslLitShader) {
            if (isIblShaded) {
                launchOnMainThread {
                    createMeshShader(updateBg = false)
                    (mesh.shader as KslLitShader).ambientFactor = bgColorSrgb.toLinear()
                }
            } else {
                (mesh.shader as KslLitShader).ambientFactor = bgColorSrgb.toLinear()
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
                (mesh.shader as KslLitShader).ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
        isIblShaded = true
    }

}