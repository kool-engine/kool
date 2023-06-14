package de.fabmax.kool

actual data class KoolConfig(
    actual val assetPath: String = "./assets",

    val canvasName: String = "glCanvas",
    val isGlobalKeyEventGrabbing: Boolean = true,
    val isJsCanvasToWindowFitting: Boolean = true,

    val customTtfFonts: Map<String, String> = emptyMap(),
)