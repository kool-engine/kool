package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.demo.labelStyle
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import kotlin.math.max

class RoughnesMetalGridContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("Material grid") {
    private val shaders = mutableListOf<KslPbrShader>()
    private var contentMesh: Mesh? = null

    private val selectedColorIdx = mutableStateOf(0)

    override fun UiScope.createContentMenu() {
        MenuRow {
            Text("Color") { labelStyle(Grow.Std) }
            ComboBox {
                modifier
                    .width(UiSizes.baseSize * 3.5f)
                    .items(matColors)
                    .selectedIndex(selectedColorIdx.use())
                    .onItemSelected {
                        selectedColorIdx.set(it)
                        shaders.forEach { s -> s.color = matColors[it].linColor }
                    }
            }
        }
    }

    override fun createContent(scene: Scene, envMap: EnvironmentMap, ctx: KoolContext): Node {
        content = Node().apply {
            isVisible = false
            isFrustumChecked = false
            contentMesh = makeSpheres(true, envMap)
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMap: EnvironmentMap) {
        contentMesh?.let {
            val pbrShader = it.shader as KslPbrShader
            pbrShader.ambientMap = envMap.irradianceMap
            pbrShader.reflectionMap = envMap.reflectionMap
        }
    }

    private fun Node.makeSpheres(withIbl: Boolean, envMap: EnvironmentMap): Mesh {
        val nRows = 7
        val nCols = 7
        val spacing = 2.5f

        val instances = MeshInstanceList(InstanceLayout, nRows * nCols) .apply {
            val mat = MutableMat4f()
            for (y in 0 until nRows) {
                for (x in 0 until nCols) {
                    mat.setIdentity()
                    mat.translate((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)

                    addInstance {
                        set(it.modelMat, mat)
                        set(it.roughness, max(x / (nCols - 1).toFloat(), 0.05f))
                        set(it.metallic, y / (nRows - 1).toFloat())
                    }
                }
            }
        }

        return addMesh(Attribute.POSITIONS, Attribute.NORMALS, instances = instances) {
            isFrustumChecked = false
            geometry.addGeometry(sphereProto.simpleSphere)
            shader = instancedPbrShader(withIbl, envMap).also { shaders += it }
        }
    }

    private fun instancedPbrShader(withIbl: Boolean, envMap: EnvironmentMap) = KslPbrShader {
        vertices { instancedModelMatrix() }
        color { uniformColor(matColors[selectedColorIdx.value].linColor) }
        metallic { instanceProperty(InstanceLayout.metallic) }
        roughness { instanceProperty(InstanceLayout.roughness) }
        if (withIbl) {
            lightingCfg.imageBasedAmbientLight(envMap.irradianceMap)
            reflectionMap = envMap.reflectionMap
        }
    }

    private data class MatColor(val name: String, val linColor: Color) {
        override fun toString() = name
    }

    private object InstanceLayout : Struct("InstanceLayout", MemoryLayout.TightlyPacked) {
        val modelMat = mat4(InstanceLayouts.ModelMat.modelMat.name)
        val roughness = float1()
        val metallic = float1()
    }

    companion object {
        private val matColors = listOf(
            MatColor("Red", MdColor.RED.toLinear()),
            MatColor("Pink", MdColor.PINK.toLinear()),
            MatColor("Purple", MdColor.PURPLE.toLinear()),
            MatColor("Deep Purple", MdColor.DEEP_PURPLE.toLinear()),
            MatColor("Indigo", MdColor.INDIGO.toLinear()),
            MatColor("Blue", MdColor.BLUE.toLinear()),
            MatColor("Cyan", MdColor.CYAN.toLinear()),
            MatColor("Teal", MdColor.TEAL.toLinear()),
            MatColor("Green", MdColor.GREEN.toLinear()),
            MatColor("Light Green", MdColor.LIGHT_GREEN.toLinear()),
            MatColor("Lime", MdColor.LIME.toLinear()),
            MatColor("Yellow", MdColor.YELLOW.toLinear()),
            MatColor("Amber", MdColor.AMBER.toLinear()),
            MatColor("Orange", MdColor.ORANGE.toLinear()),
            MatColor("Deep Orange", MdColor.DEEP_ORANGE.toLinear()),
            MatColor("Brown", MdColor.BROWN.toLinear()),
            MatColor("White", Color.WHITE.toLinear()),
            MatColor("Grey", MdColor.GREY.toLinear()),
            MatColor("Blue Grey", MdColor.BLUE_GREY.toLinear()),
            MatColor("Almost Black", Color(0.1f, 0.1f, 0.1f).toLinear())
        )
    }
}