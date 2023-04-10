package de.fabmax.kool

/**
 * @author fabmax
 */

/**
 * Replacement function for synchronized() which is deprecated on multi-platform. On JVM this falls back to
 * synchronized(), on JS this simply executes block.
 */
expect inline fun <R> lock(lock: Any, block: () -> R): R

expect fun Double.toString(precision: Int): String

fun Float.toString(precision: Int): String = this.toDouble().toString(precision)

expect fun defaultKoolConfig(): KoolConfig

/**
 * Creates a new [KoolContext] based on the [KoolConfig] provided by [KoolSystem]. [KoolSystem.initialize] has to be
 * called before invoking this function.
 */
expect fun createContext(): KoolContext

expect fun KoolApplication(config: KoolConfig = defaultKoolConfig(), appBlock: (KoolContext) -> Unit)

