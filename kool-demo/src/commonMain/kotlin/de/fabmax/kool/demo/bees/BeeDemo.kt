package de.fabmax.kool.demo.bees

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ui2.Grow
import de.fabmax.kool.modules.ui2.Text
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addLineMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.min
import kotlin.math.roundToInt

class BeeDemo : DemoScene("Fighting Bees") {

    private val cpuBeesA = CpuBees(0)
    private val cpuBeesB = CpuBees(1)

    val gpuBees = GpuBees(mainScene)

    private val isGpuSimulation = mutableStateOf(KoolSystem.requireContext().backend.hasComputeShaders).onChange {
        applyMode(it)
    }

    private val beeTex: Texture2d by texture2d(
        path = "${DemoLoader.materialPath}/bee.png",
        props = TextureProps(
            generateMipMaps = false,
            defaultSamplerSettings = SamplerSettings().clamped().nearest()
        )
    )

    init {
        cpuBeesA.enemyBees = cpuBeesB
        cpuBeesB.enemyBees = cpuBeesA
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera().apply {
            minZoom = 10.0
            maxZoom = 400.0
            zoom = 200.0
        }

        clearColor = bgColor
        mainRenderPass.isProfileTimes = true

        val beeMeshA = cpuBeesA.beeMesh
        val beeMeshB = cpuBeesB.beeMesh
        beeMeshA.shader = CpuBeeShader(MdColor.BLUE, MdColor.PURPLE).apply { colorMap = beeTex }
        beeMeshB.shader = CpuBeeShader(MdColor.AMBER, MdColor.DEEP_ORANGE).apply { colorMap = beeTex }
        addNode(beeMeshA)
        addNode(beeMeshB)
        onUpdate {
            if (!isGpuSimulation.value) {
                cpuBeesA.updateBees()
                cpuBeesB.updateBees()
            }
        }

        gpuBees.setupShaders(beeTex)
        addNode(gpuBees.beeMeshA)
        addNode(gpuBees.beeMeshB)

        applyMode(isGpuSimulation.value)

        addLineMesh {
            addBoundingBox(
                BoundingBoxF(
                    BeeConfig.worldExtent.mul(-1f, MutableVec3f()),
                    BeeConfig.worldExtent.mul(1f, MutableVec3f())
                ), Color.WHITE)
        }
    }

    private fun applyMode(isGpu: Boolean) {
        gpuBees.setEnabled(isGpu)
        cpuBeesA.beeMesh.isVisible = !isGpu
        cpuBeesB.beeMesh.isVisible = !isGpu

        val maxBees = if (isGpu) BeeConfig.maxBeesPerTeamGpu else BeeConfig.maxBeesPerTeamCpu
        BeeConfig.beesPerTeam.set(min(BeeConfig.beesPerTeam.value, maxBees))
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        // There are two teams -> total number of bees is beesPerTeam * 2
        val maxBees = if (isGpuSimulation.use()) BeeConfig.maxBeesPerTeamGpu else BeeConfig.maxBeesPerTeamCpu
        MenuSlider2(
            "Number of bees",
            BeeConfig.beesPerTeam.use().toFloat(),
            10f,
            maxBees.toFloat(),
            { "${it.roundToInt() * 2}" }
        ) {
            BeeConfig.beesPerTeam.set(it.roundToInt())
        }

        if (ctx.backend.hasComputeShaders) {
            LabeledSwitch("GPU simulation", isGpuSimulation)
        }

        MenuRow {
            Text("Bee simulation time:") { labelStyle(Grow.Std) }
            val t = if (isGpuSimulation.use()) {
                gpuBees.beeUpdateTime.use()
            } else {
                cpuBeesA.beeUpdateTime.use() + cpuBeesB.beeUpdateTime.use()
            }
            Text("${t.toString(2)} ms") { labelStyle() }
        }

        Text("Bee Movement") { sectionTitleStyle() }

        MenuSlider2("Team attraction", BeeConfig.teamAttraction.use(), 0f, 50f, { it.toInt().toString() }) {
            BeeConfig.teamAttraction.set(it)
        }
        MenuSlider2("Team repulsion", BeeConfig.teamRepulsion.use(), 0f, 50f, { it.toInt().toString() }) {
            BeeConfig.teamRepulsion.set(it)
        }
        MenuSlider2("Jitter", BeeConfig.speedJitter.use(), 0f, 500f, { it.toInt().toString() }) {
            BeeConfig.speedJitter.set(it)
        }
        MenuSlider2("Chase force", BeeConfig.chaseForce.use(), 0f, 50f, { it.toInt().toString() }) {
            BeeConfig.chaseForce.set(it)
        }
    }

    companion object {
        val ATTR_POSITION = Attribute("aPosition", GpuType.FLOAT4)
        val ATTR_ROTATION = Attribute("aRotation", GpuType.FLOAT4)

        val bgColor = MdColor.LIGHT_BLUE tone 400
    }
}
