package de.fabmax.kool

/**
 * @author fabmax
 */

expect fun Double.toString(precision: Int): String

fun Float.toString(precision: Int): String = this.toDouble().toString(precision)

expect fun defaultKoolConfig(): KoolConfig

/**
 * Creates a new [KoolContext] based on the [KoolConfig] provided by [KoolSystem]. [KoolSystem.initialize] has to be
 * called before invoking this function.
 */
expect fun createContext(config: KoolConfig = defaultKoolConfig()): KoolContext

fun KoolApplication(config: KoolConfig = defaultKoolConfig(), appBlock: (KoolContext) -> Unit) {
    val ctx = createContext(config)
    appBlock(ctx)
    ctx.run()
}
