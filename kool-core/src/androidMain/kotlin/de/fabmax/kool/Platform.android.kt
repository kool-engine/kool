package de.fabmax.kool

import android.app.Activity
import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.KoolContextAndroid
import java.util.*

actual fun Double.toString(precision: Int): String = "%.${precision.clamp(0, 12)}f".format(Locale.ENGLISH, this)

val KoolSystem.configAndroid: KoolConfigAndroid get() = config as KoolConfigAndroid

fun createKoolContext(config: KoolConfigAndroid): KoolContextAndroid {
    KoolSystem.initialize(config)
    return KoolContextAndroid(config)
}

fun Activity.createDefaultKoolContext(): KoolContextAndroid {
    val ctx = createKoolContext(KoolConfigAndroid(applicationContext))
    return ctx
}
