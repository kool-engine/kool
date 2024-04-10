package de.fabmax.kool

import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.launchOnMainThread
import org.w3c.dom.HTMLCanvasElement

actual fun Double.toString(precision: Int): String {
    return when {
        this.isNaN() -> "NaN"
        this.isInfinite() -> "Infinity"
        else -> asDynamic().toFixed(precision).toString()
    }
}

val KoolSystem.configJs: KoolConfigJs get() = config as KoolConfigJs

/**
 * Creates a new [KoolContext] with the given [KoolConfigJs]. Notice that there can only be one [KoolContext], calling
 * this method multiple times is an error.
 */
fun createContext(config: KoolConfigJs = KoolConfigJs()): JsContext {
    KoolSystem.initialize(config)
    return JsImpl.createContext()
}

fun KoolApplication(config: KoolConfigJs, appBlock: suspend KoolApplication.() -> Unit) =
    KoolApplication(createContext(config), appBlock)

fun KoolApplication(ctx: JsContext = createContext(), appBlock: suspend KoolApplication.() -> Unit) {
    val koolApp = KoolApplication(ctx)
    launchOnMainThread {
        koolApp.appBlock()
    }
    ctx.run()
}

internal object JsImpl {
    private var ctx: JsContext? = null
    val canvas: HTMLCanvasElement
        get() = checkNotNull(ctx?.canvas) { "Platform.createContext() not called" }

    fun createContext(): JsContext {
        check(ctx == null) { "Context was already created (multi-context is currently not supported in js" }
        ctx = JsContext()
        return ctx!!
    }
}