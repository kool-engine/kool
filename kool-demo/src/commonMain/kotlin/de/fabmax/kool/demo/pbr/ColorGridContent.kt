package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.MenuSlider
import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.demo.labelStyle
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.modules.ui2.Text
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shadermodel.PbrMaterialNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class ColorGridContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("Color grid") {
    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Mesh? = null
    private var nonIblContent: Mesh? = null

    private val roughness = mutableStateOf(0.1f).onChange { shaders.forEach { s -> s.roughness(it) } }
    private val metallic = mutableStateOf(0f).onChange { shaders.forEach { s -> s.metallic(it) } }

    override fun UiScope.createContentMenu() {
        val lblSize = UiSizes.baseElemSize * 2f
        val txtSize = UiSizes.baseElemSize * 0.7f
        MenuRow {
            Text("Roughness") { labelStyle(lblSize) }
            MenuSlider(roughness.use(), 0f, 1f, txtWidth = txtSize) { roughness.set(it) }
        }
        MenuRow {
            Text("Metallic") { labelStyle(lblSize) }
            MenuSlider(metallic.use(), 0f, 1f, txtWidth = txtSize) { metallic.set(it) }
        }
    }

    override fun setUseImageBasedLighting(enabled: Boolean) {
        iblContent?.isVisible = enabled
        nonIblContent?.isVisible = !enabled
    }

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Group {
        content = group {
            isVisible = false
            isFrustumChecked = false

            val ibl = makeSpheres(true, envMaps)
            val nonIbl = makeSpheres(false, envMaps).apply { isVisible = false }

            +ibl
            +nonIbl

            iblContent = ibl
            nonIblContent = nonIbl
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        iblContent?.let {
            val pbrShader = it.shader as PbrShader
            pbrShader.irradianceMap(envMaps.irradianceMap)
            pbrShader.reflectionMap(envMaps.reflectionMap)
        }
    }

    private fun makeSpheres(withIbl: Boolean, environmentMaps: EnvironmentMaps): Mesh {
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

        return mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
            isFrustumChecked = false
            geometry.addGeometry(sphereProto.simpleSphere)
            shader = instancedPbrShader(withIbl, environmentMaps).also { shaders += it }

            instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), nRows * nCols) .apply {
                val mat = Mat4f()
                for (y in 0 until nRows) {
                    for (x in 0 until nCols) {
                        mat.setIdentity()
                        mat.translate((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)
                        mat.scale(1.5f)

                        addInstance {
                            put(mat.matrix)
                            put(colors[(y * nCols + x) % colors.size].toLinear().array)
                        }
                    }
                }
            }
        }
    }

    private fun instancedPbrShader(withIbl: Boolean, envMaps: EnvironmentMaps): PbrShader {
        val pbrCfg = PbrMaterialConfig().apply {
            albedoSource = Albedo.STATIC_ALBEDO
            roughness = 0.1f
            metallic = 0f
            isInstanced = true
            if (withIbl) {
                useImageBasedLighting(envMaps)
            }
        }

        // use default PBR shader model and replace color input by instance attribute
        val model = PbrShader.defaultPbrModel(pbrCfg).apply {
            val ifInstColor: StageInterfaceNode
            vertexStage {
                ifInstColor = stageInterfaceNode("ifInstColors", instanceAttributeNode(Attribute.COLORS).output)
            }
            fragmentStage {
                val material = findNode<PbrMaterialNode>("pbrMaterial")!!
                material.inAlbedo = ifInstColor.output
            }
        }
        return PbrShader(pbrCfg, model)
    }
}