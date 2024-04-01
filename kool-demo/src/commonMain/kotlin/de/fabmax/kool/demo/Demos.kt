package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.Platform
import de.fabmax.kool.demo.Demos.PlatformFilter
import de.fabmax.kool.demo.bees.BeeDemo
import de.fabmax.kool.demo.creativecoding.CreativeCodingDemo
import de.fabmax.kool.demo.helloworld.*
import de.fabmax.kool.demo.pbr.PbrDemo
import de.fabmax.kool.demo.physics.collision.CollisionDemo
import de.fabmax.kool.demo.physics.joints.JointsDemo
import de.fabmax.kool.demo.physics.manybodies.ManyBodiesDemo
import de.fabmax.kool.demo.physics.manyvehicles.ManyVehiclesDemo
import de.fabmax.kool.demo.physics.ragdoll.RagdollDemo
import de.fabmax.kool.demo.physics.terrain.TerrainDemo
import de.fabmax.kool.demo.physics.vehicle.VehicleDemo
import de.fabmax.kool.demo.procedural.ProceduralDemo
import de.fabmax.kool.demo.tests.GizmoTest
import de.fabmax.kool.demo.tests.InifiniteDepthTest
import de.fabmax.kool.demo.tests.KslShaderTest
import de.fabmax.kool.demo.uidemo.UiDemo
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.max

object Demos {

    private val AllPlatforms = PlatformFilter { true }
    private val NeedsComputeShaders = PlatformFilter { KoolSystem.requireContext().backend.hasComputeShaders }
    private val NonJavascript = PlatformFilter { KoolSystem.platform != Platform.JAVASCRIPT }
    private val NonVulkan = PlatformFilter { "Vulkan" !in KoolSystem.requireContext().backend.name }
    private val DesktopOpenGl = PlatformFilter { NonJavascript.applies() && NonVulkan.applies() }

    val demoColors = ColorGradient(
        0f to MdColor.AMBER,
        0.2f to MdColor.DEEP_ORANGE,

        0.25f to MdColor.PINK,
        0.45f to MdColor.PURPLE,

        0.5f to MdColor.INDIGO,
        0.6f to MdColor.BLUE,
        0.7f to MdColor.CYAN,

        0.8f to MdColor.GREEN,
        0.9f to MdColor.LIGHT_GREEN,
        0.95f to MdColor.LIME,
        1f to MdColor.LIME,
        n = 512
    )

    val physicsDemos = Category("Physics", false, 0f, 0.2f).apply {
        entry("phys-terrain", "Island") { TerrainDemo() }
        entry("phys-vehicle", "Vehicle") { VehicleDemo() }
        entry("phys-ragdoll", "Ragdolls") { RagdollDemo() }
        entry("phys-joints", "Joints") { JointsDemo() }
        entry("physics", "Collision") { CollisionDemo() }
    }

    val graphicsDemos = Category("Graphics", false, 0.25f, 0.45f).apply {
        entry("ao", "Ambient Occlusion") { AoDemo() }
        entry("shell", "Shell Shading / Fur") { ShellShadingDemo() }
        entry("gltf", "glTF Models") { GltfDemo() }
        entry("ssr", "Reflections") { ReflectionDemo() }
        entry("deferred", "Deferred Shading") { DeferredDemo() }
        entry("procedural", "Procedural Roses") { ProceduralDemo() }
        entry("pbr", "PBR Materials") { PbrDemo() }
    }

    val techDemos = Category("Tech", false, 0.5f, 0.7f).apply {
        entry("creative-coding", "Creative Coding") { CreativeCodingDemo() }
        entry("instance", "Instanced Drawing") { InstanceDemo() }
        entry("bees", "Fighting Bees") { BeeDemo() }
        entry("simplification", "Simplification") { SimplificationDemo() }
        entry("ui", "User Interface") { UiDemo() }
    }

    val hiddenDemos = Category("Hidden", true, 0.75f, 0.95f).apply {
        entry("helloworld", "Hello World") { HelloWorld() }
        entry("helloksl", "Hello KSL Shaders") { HelloKsl() }
        entry("hellogltf", "Hello glTF") { HelloGltf() }
        entry("hellobuffers", "Hello RenderToTexture") { HelloRenderToTexture() }
        entry("hellocompute", "Hello Compute", NeedsComputeShaders) { HelloCompute() }
        entry("hello-ui", "Hello UI") { HelloUi() }
        entry("manybodies", "Many Bodies") { ManyBodiesDemo() }
        entry("manyvehicles", "Many Vehicles") { ManyVehiclesDemo() }
        entry("ksl-test", "Ksl Shading Test") { KslShaderTest() }
        entry("gizmo-test", "Gizmo Test") { GizmoTest() }
        entry("inf-depth-test", "Clip Space Test") { InifiniteDepthTest() }
    }

    val categories = mutableListOf(physicsDemos, graphicsDemos, techDemos, hiddenDemos)
    val demos = categories.flatMap { it.entries }.associateBy { it.id }.toMutableMap()

    val defaultDemo = "ao"

    class Category(val title: String, val isHidden: Boolean, val fromColor: Float, val toColor: Float) {
        val entries = mutableListOf<Entry>()

        fun getCategoryColor(f: Float): Color {
            return demoColors.getColor(fromColor + f * (toColor - fromColor))
        }

        fun entry(id: String, title: String, platformFilter: PlatformFilter = AllPlatforms, factory: (KoolContext) -> DemoScene) {
            entries += Entry(this, id, title, platformFilter, factory)
        }
    }

    class Entry(
        val category: Category,
        val id: String,
        val title: String,
        val platformFilter: PlatformFilter,
        val newInstance: (KoolContext) -> DemoScene
    ) {
        val color: Color
            get() {
                val catIdx = max(0, category.entries.indexOf(this)).toFloat()
                val gradientF = catIdx / category.entries.lastIndex
                return category.getCategoryColor(gradientF)
            }
    }

    fun interface PlatformFilter {
        fun applies(): Boolean
    }
}