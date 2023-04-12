package de.fabmax.kool.app

import de.fabmax.kool.KoolApplication

fun main() = KoolApplication { ctx ->
    ctx.scenes += App().startApp(ctx, false)
}