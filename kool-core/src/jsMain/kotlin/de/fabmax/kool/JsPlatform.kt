package de.fabmax.kool

import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext

/**
 * Javascript / WebGL platform implementation
 *
 * @author fabmax
 */

actual fun createDefaultContext(): KoolContext = createContext(JsContext.InitProps())

fun createContext(props: JsContext.InitProps): KoolContext = JsImpl.createContext(props)

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
    var ctx: JsContext? = null
    val gl: WebGL2RenderingContext
        get() = ctx?.gl ?: throw KoolException("Platform.createContext() not called")

    fun createContext(props: JsContext.InitProps): KoolContext {
        if (ctx != null) {
            throw KoolException("Context was already created (multi-context is currently not supported in js")
        }
        ctx = JsContext(props)
        return ctx!!
    }
}
