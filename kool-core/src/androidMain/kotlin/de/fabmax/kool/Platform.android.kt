package de.fabmax.kool

import de.fabmax.kool.math.clamp
import java.util.*

actual fun Double.toString(precision: Int): String = "%.${precision.clamp(0, 12)}f".format(Locale.ENGLISH, this)

actual fun defaultKoolConfig(): KoolConfig {
    TODO()
}

actual fun createContext(): KoolContext {
    TODO()
}

actual fun KoolApplication(config: KoolConfig, appBlock: (KoolContext) -> Unit) {
    TODO()
}
