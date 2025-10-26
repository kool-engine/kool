package de.fabmax.kool.demo

import de.fabmax.kool.util.L10n

object DemoL10n {

    fun registerStrings() {
        // no need to register any en strings as long as meaningful string keys are used
        L10n.registerLanguage("en", "English") { }

        L10n.registerLanguage("de", "Deutsch") { registerDe() }
    }

    private fun MutableMap<String, String>.registerDe() = putAll(listOf(
        // Demo menu
        "kool Demos" to "kool Demos",

        "Physics" to "Physik",
        "Island" to "Insel",
        "Vehicle" to "Fahrzeug",
        "Ragdolls" to "Ragdolls",
        "Joints" to "Gelenke",
        "Rigid Bodies" to "Starrkörper",

        "Graphics" to "Grafik",
        "Ambient Occlusion" to "Ambient Occlusion",
        "Shell Shading / Fur" to "Shell Shading / Fell",
        "glTF Models" to "glTF Modelle",
        "Reflections" to "Reflektionen",
        "Deferred Shading" to "Deferred Shading",
        "Procedural Roses" to "Prozedurale Rosen",
        "PBR Materials" to "PBR Materiallien",

        "Tech" to "Tech",
        "Creative Coding" to "Creative Coding",
        "Instanced Drawing" to "Instanced Drawing",
        "Fighting Bees" to "Kämpfende Bienen",
        "Simplification" to "Simplifizierung",
        "User Interface" to "User Interface",
        "Bloom" to "Bloom",
        "Path-tracing" to "Path-tracing",
        "Tetris" to "Tetris",

        "Hidden" to "Versteckt",
        "Hello World" to "Hallo Welt",
        "Hello KSL Shaders" to "Hallo KSL Shaders",
        "Hello glTF" to "Hallo glTF",
        "Hello RenderToTexture" to "Hallo RenderToTexture",
        "Hello Compute Texture" to "Hallo Compute Texture",
        "Hello Compute Particles" to "Hallo Compute Partikel",
        "Hello UI" to "Hallo UI",
        "Many Bodies" to "Viele Körper",
        "Many Vehicles" to "Viele Fahrzeuge",
        "Ksl Shading Test" to "Ksl Shading Test",
        "Gizmo Test" to "Gizmo Test",
        "Clip Space Test" to "Clip Space Test",
        "Instancing Test" to "Instancing Test",
        "Array Textures Test" to "Array Textures Test",
        "Hello Structs" to "Hallo Structs",

        // Settings menu
        "Settings" to "Einstellungen",
        "Language" to "Sprache",
        "UI size" to "UI Größe",
        "Small" to "Klein",
        "Medium" to "Mittel",
        "Large" to "Groß",
        "Render scale" to "Skalierung",
        "Menu initially expanded" to "Start mit offenem Menü",
        "Debug overlay" to "Debug overlay",
        "Fullscreen" to "Vollbild",
        "Hidden demos" to "Versteckte Demos",
        "Show demo menus" to "Demo Menüs anzeigen",
    ))
}