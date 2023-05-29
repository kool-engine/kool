package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.util.Color

class MeshComponent(override val componentData: MeshComponentData) :
    EditorDataComponent<MeshComponentData>,
    UpdateSceneBackgroundComponent
{
    val shapesState = MutableStateList(componentData.shapes)

    private var _mesh: Mesh? = null
    val mesh: Mesh
        get() = _mesh ?: throw IllegalStateException("MeshComponent was not yet created")

    val isCreated: Boolean
        get() = _mesh != null

    private var isIblShaded = false

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        val sceneNode = requireNotNull(nodeModel as? SceneNodeModel) {
            "MeshComponent is only allowed in scene nodes (parent node is of type ${nodeModel::class})"
        }
        _mesh = ColorMesh()
        mesh.shader = defaultPbrShader(sceneNode.scene.sceneBackground.loadedEnvironmentMaps)
        mesh.rayTest = MeshRayTest.geometryTest(mesh)
        updateGeometry()
    }

    fun updateGeometry() {
        mesh.generate {
            shapesState.forEach {
                withTransform {
                    it.pose.toMat4f(transform)
                    color = it.vertexColor.toColor()
                    it.generate(this)
                }
            }
        }
    }

    private fun defaultPbrShader(ibl: EnvironmentMaps?): KslPbrShader {
        return KslPbrShader {
            color { vertexColor() }
            ibl?.let {
                enableImageBasedLighting(ibl)
            }
        }
    }

    override fun updateSingleColorBg(bgColorSrgb: Color) {
        val isLit = mesh.shader is KslLitShader
        if (isLit) {
            if (isIblShaded) {
                mesh.shader = defaultPbrShader(null)
            }
            (mesh.shader as KslLitShader).ambientFactor = bgColorSrgb.toLinear()
        }
        isIblShaded = false
    }

    override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        if (mesh.shader is KslLitShader) {
            if (!isIblShaded) {
                mesh.shader = defaultPbrShader(ibl)
            } else {
                (mesh.shader as KslLitShader).ambientMap = ibl.irradianceMap
                (mesh.shader as? KslPbrShader)?.reflectionMap = ibl.reflectionMap
            }
        }
        isIblShaded = true
    }

}