package de.fabmax.kool

import de.fabmax.kool.platform.JsContext
import org.w3c.dom.HTMLCanvasElement

/**
 * Javascript / WebGL platform implementation
 *
 * @author fabmax
 */

actual fun defaultKoolConfig(): KoolConfig = KoolConfigJs()

val KoolSystem.configJs: KoolConfigJs get() = config as KoolConfigJs

/**
 * Creates a new [KoolContext] based on the [KoolConfig] provided by [KoolSystem]. [KoolSystem.initialize] has to be
 * called before invoking this function.
 */
actual fun createContext(config: KoolConfig): KoolContext {
    KoolSystem.initialize(config)
    return JsImpl.createContext()
}

actual fun Double.toString(precision: Int): String {
    if (this.isNaN()) {
        return "NaN"
    } else if (this.isInfinite()) {
        return "Infinity"
    }

    @Suppress("UNUSED_VARIABLE")
    val d = this
    return js("d.toFixed(precision)").toString()
}

internal object JsImpl {
    private var ctx: JsContext? = null
    val canvas: HTMLCanvasElement
        get() = checkNotNull(ctx?.canvas) { "Platform.createContext() not called" }

    fun createContext(): KoolContext {
        check(ctx == null) { "Context was already created (multi-context is currently not supported in js" }
        ctx = JsContext()
        return ctx!!
    }
}