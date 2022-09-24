package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.atmosphere.AtmosphereDemo
import de.fabmax.kool.demo.pbr.PbrDemo
import de.fabmax.kool.demo.physics.collision.CollisionDemo
import de.fabmax.kool.demo.physics.joints.JointsDemo
import de.fabmax.kool.demo.physics.manyvehicles.ManyVehiclesDemo
import de.fabmax.kool.demo.physics.ragdoll.RagdollDemo
import de.fabmax.kool.demo.physics.terrain.TerrainDemo
import de.fabmax.kool.demo.physics.vehicle.VehicleDemo
import de.fabmax.kool.demo.procedural.ProceduralDemo
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor

object Demos {

    val physicsDemos = Category("Physics", ColorGradient(MdColor.AMBER, MdColor.DEEP_ORANGE), false).apply {
        entry("phys-terrain", "Island") { TerrainDemo() }
        entry("phys-vehicle", "Vehicle") { VehicleDemo() }
        entry("phys-ragdoll", "Ragdoll") { RagdollDemo() }
        entry("phys-joints", "Joints") { JointsDemo() }
        entry("physics", "Collision") { CollisionDemo() }
    }

    val graphicsDemos = Category("Graphics", ColorGradient(MdColor.PINK, MdColor.PURPLE), false).apply {
        entry("ao", "Ambient Occlusion") { AoDemo() }
        entry("gltf", "glTF Models") { GltfDemo() }
        entry("ssr", "Reflections") { MultiLightDemo() }
        entry("deferred", "Deferred Shading") { DeferredDemo() }
        entry("procedural", "Procedural Geometry") { ProceduralDemo() }
        entry("pbr", "PBR Materials") { PbrDemo() }
        entry("atmosphere", "Atmospheric Scattering") { AtmosphereDemo() }
    }

    val techDemos = Category("Tech", ColorGradient(MdColor.INDIGO, MdColor.BLUE, MdColor.CYAN), false).apply {
        entry("instance", "Instanced Drawing") { InstanceDemo() }
        entry("simplification", "Simplification") { SimplificationDemo() }
        entry("tree", "Procedural Tree") { TreeDemo() }
    }

    val hiddenDemos = Category("Hidden", ColorGradient(MdColor.GREEN, MdColor.LIGHT_GREEN, MdColor.LIME), true).apply {
        entry("helloworld", "Hello World") { HelloWorldDemo() }
        entry("hellogltf", "Hello glTF") { HelloGltfDemo() }
        entry("manyvehicles", "Many Vehicles") { ManyVehiclesDemo() }
        entry("ksl-test", "Ksl Shading Test") { KslShaderTest() }
        entry("ui2", "UI2 Test") { Ui2Demo() }
    }

    val categories = mutableListOf(physicsDemos, graphicsDemos, techDemos, hiddenDemos)
    val demos = categories.flatMap { it.entries }.map { it.id to it }.toMap()

    val defaultDemo = "phys-vehicle"

    class Category(val title: String, val colorSet: ColorGradient, val isHidden: Boolean) {
        val entries = mutableListOf<Entry>()

        fun entry(id: String, title: String, factory: (KoolContext) -> DemoScene) {
            entries += Entry(this, id, title, factory)
        }
    }

    class Entry(val category: Category, val id: String, val title: String, val newInstance: (KoolContext) -> DemoScene)
}