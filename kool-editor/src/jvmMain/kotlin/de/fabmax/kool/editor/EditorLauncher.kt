package de.fabmax.kool.editor

import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfig

fun main() = KoolApplication(
    KoolConfig(
        windowTitle = "Kool Editor"
    )
) { ctx ->
    KoolEditor(ctx)
}