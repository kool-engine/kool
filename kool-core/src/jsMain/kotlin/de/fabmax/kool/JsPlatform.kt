package de.fabmax.kool

import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext
import org.w3c.dom.HTMLCanvasElement

/**
 * Javascript / WebGL platform implementation
 *
 * @author fabmax
 */

actual fun defaultKoolConfig() = KoolConfig()

/**
 * Creates a new [KoolContext] based on the [KoolConfig] provided by [KoolSystem]. [KoolSystem.initialize] has to be
 * called before invoking this function.
 */
actual fun createContext() = JsImpl.createContext()

actual fun KoolApplication(config: KoolConfig, appBlock: (KoolContext) -> Unit) {
    KoolSystem.initialize(config)
    val ctx = createContext()
    appBlock(ctx)
    ctx.run()
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

actual inline fun <R> lock(lock: Any, block: () -> R): R = block()

internal object JsImpl {
    private var ctx: JsContext? = null
    val gl: WebGL2RenderingContext
        get() = ctx?.gl ?: throw KoolException("Platform.createContext() not called")
    val canvas: HTMLCanvasElement
        get() = ctx?.canvas ?: throw KoolException("Platform.createContext() not called")

    fun createContext(): KoolContext {
        if (ctx != null) {
            throw KoolException("Context was already created (multi-context is currently not supported in js")
        }
        ctx = JsContext()
        return ctx!!
    }
}