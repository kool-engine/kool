package de.fabmax.kool

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual data class KoolConfig(
    actual val assetPath: String = "./assets",

    val canvasName: String = "glCanvas",
    val isGlobalKeyEventGrabbing: Boolean = true,
    val isJsCanvasToWindowFitting: Boolean = true,

    val customTtfFonts: Map<String, String> = emptyMap(),
)