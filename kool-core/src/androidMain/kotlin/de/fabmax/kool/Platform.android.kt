package de.fabmax.kool

import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.AndroidContext
import java.util.*

actual fun Double.toString(precision: Int): String = "%.${precision.clamp(0, 12)}f".format(Locale.ENGLISH, this)

val KoolSystem.configAndroid: KoolConfigAndroid get() = config as KoolConfigAndroid

actual fun defaultKoolConfig(): KoolConfig = KoolConfigAndroid()

actual fun createContext(config: KoolConfig): KoolContext {
    KoolSystem.initialize(config)
    return AndroidContext()
}
