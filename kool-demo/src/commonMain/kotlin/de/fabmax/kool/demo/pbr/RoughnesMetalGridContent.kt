package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.demo.labelStyle
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.max

class RoughnesMetalGridContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("Material grid") {
    private val shaders = mutableListOf<KslPbrShader>()
    private var iblContent: Mesh? = null
    private var nonIblContent: Mesh? = null

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

    override fun setUseImageBasedLighting(enabled: Boolean) {
        iblContent?.isVisible = enabled
        nonIblContent?.isVisible = !enabled
    }

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Node {
        content = Node().apply {
            isVisible = false
            isFrustumChecked = false

            val ibl = makeSpheres(true, envMaps)
            val nonIbl = makeSpheres(false, envMaps).apply { isVisible = false }

            iblContent = ibl
            nonIblContent = nonIbl
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        iblContent?.let {
            val pbrShader = it.shader as KslPbrShader
            pbrShader.ambientMap = envMaps.irradianceMap
            pbrShader.reflectionMap = envMaps.reflectionMap
        }
    }

    private fun Node.makeSpheres(withIbl: Boolean, envMaps: EnvironmentMaps): Mesh {
        val nRows = 7
        val nCols = 7
        val spacing = 2.5f

        return addMesh(Attribute.POSITIONS, Attribute.NORMALS) {
            isFrustumChecked = false
            geometry.addGeometry(sphereProto.simpleSphere)
            shader = instancedPbrShader(withIbl, envMaps).also { shaders += it }

            instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, ATTRIB_ROUGHNESS, ATTRIB_METAL), nRows * nCols) .apply {
                val mat = Mat4f()
                for (y in 0 until nRows) {
                    for (x in 0 until nCols) {
                        mat.setIdentity()
                        mat.translate((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)

                        addInstance {
                            put(mat.array)
                            val roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                            val metallic = y / (nRows - 1).toFloat()
                            put(roughness)
                            put(metallic)
                        }
                    }
                }
            }
        }
    }

    private fun instancedPbrShader(withIbl: Boolean, envMaps: EnvironmentMaps) = KslPbrShader {
        vertices { isInstanced = true }
        color { uniformColor(matColors[selectedColorIdx.value].linColor) }
        metallic { instanceProperty(ATTRIB_METAL) }
        roughness { instanceProperty(ATTRIB_ROUGHNESS) }
        if (withIbl) {
            imageBasedAmbientColor(envMaps.irradianceMap)
            reflectionMap = envMaps.reflectionMap
        }
    }

    private data class MatColor(val name: String, val linColor: Color) {
        override fun toString() = name
    }

    companion object {
        private val ATTRIB_ROUGHNESS = Attribute("aRoughness", GlslType.FLOAT)
        private val ATTRIB_METAL = Attribute("aMetal", GlslType.FLOAT)

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