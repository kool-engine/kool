package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.MenuSlider
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.demo.labelStyle
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.Text
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class ColorGridContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("Color grid") {
    private val shaders = mutableListOf<KslPbrShader>()
    private var contentMesh: Mesh? = null

    private val roughness = mutableStateOf(0.3f).onChange { shaders.forEach { s -> s.roughness = it } }
    private val metallic = mutableStateOf(0f).onChange { shaders.forEach { s -> s.metallic = it } }

    override fun UiScope.createContentMenu() {
        val lblSize = UiSizes.baseSize * 2f
        val txtSize = UiSizes.baseSize * 0.75f
        MenuRow {
            Text("Roughness") { labelStyle(lblSize) }
            MenuSlider(roughness.use(), 0f, 1f, txtWidth = txtSize) { roughness.set(it) }
        }
        MenuRow {
            Text("Metallic") { labelStyle(lblSize) }
            MenuSlider(metallic.use(), 0f, 1f, txtWidth = txtSize) { metallic.set(it) }
        }
    }

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Node {
        content = Node().apply {
            isVisible = false
            isFrustumChecked = false
            contentMesh = makeSpheres(true, envMaps)
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        contentMesh?.let {
            val pbrShader = it.shader as KslPbrShader
            pbrShader.ambientMap = envMaps.irradianceMap
            pbrShader.reflectionMap = envMaps.reflectionMap
        }
    }

    private fun Node.makeSpheres(withIbl: Boolean, environmentMaps: EnvironmentMaps): Mesh {
        val nRows = 4
        val nCols = 5
        val spacing = 4.5f

        val colors = mutableListOf<Color>()
        colors += MdColor.PALETTE
        colors.remove(MdColor.LIGHT_BLUE)
        colors.remove(MdColor.GREY)
        colors.remove(MdColor.BLUE_GREY)
        colors += Color.WHITE
        colors += MdColor.GREY
        colors += MdColor.BLUE_GREY
        colors += Color(0.1f, 0.1f, 0.1f)

        val instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), nRows * nCols) .apply {
            val mat = MutableMat4f()
            for (y in 0 until nRows) {
                for (x in 0 until nCols) {
                    mat.setIdentity()
                    mat.translate((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)
                    mat.scale(1.5f)

                    addInstance {
                        mat.putTo(this)
                        colors[(y * nCols + x) % colors.size].toLinear().putTo(this)
                    }
                }
            }
        }

        return addMesh(Attribute.POSITIONS, Attribute.NORMALS, instances = instances) {
            isFrustumChecked = false
            geometry.addGeometry(sphereProto.simpleSphere)
            shader = instancedPbrShader(withIbl, environmentMaps).also { shaders += it }
        }
    }

    private fun instancedPbrShader(withIbl: Boolean, envMaps: EnvironmentMaps) = KslPbrShader {
        vertices { isInstanced = true }
        color { instanceColor(Attribute.COLORS) }
        roughness { uniformProperty(0.1f) }
        metallic { uniformProperty(0f) }
        if (withIbl) {
            imageBasedAmbientColor(envMaps.irradianceMap)
            reflectionMap = envMaps.reflectionMap
        }
    }
}