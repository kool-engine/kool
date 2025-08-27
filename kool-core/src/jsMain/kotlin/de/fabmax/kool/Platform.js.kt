package de.fabmax.kool

import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.ApplicationScope
import de.fabmax.kool.util.FrontendScope
import de.fabmax.kool.util.Log
import de.fabmax.kool.util.LogPrinter
import kotlinx.coroutines.launch
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
suspend fun createContext(config: KoolConfigJs = KoolConfigJs()): JsContext {
    KoolSystem.initialize(config)
    return JsImpl.createContext()
}

fun KoolApplication(config: KoolConfigJs = KoolConfigJs(), appBlock: suspend KoolApplication.() -> Unit) {
    ApplicationScope.launch {
        val ctx = createContext(config)
        KoolApplication(ctx, appBlock)
    }
}

fun KoolApplication(ctx: JsContext, appBlock: suspend KoolApplication.() -> Unit) {
    val koolApp = KoolApplication(ctx)
    FrontendScope.launch {
        koolApp.appBlock()
    }
    ctx.run()
}

internal object JsImpl {
    private var ctx: JsContext? = null
    val canvas: HTMLCanvasElement
        get() = checkNotNull(ctx?.window?.canvas) { "Platform.createContext() not called" }

    init {
        if (Log.printer == Log.DEFAULT_PRINTER) {
            Log.printer = JsLogPrinter
        }
    }

    suspend fun createContext(): JsContext {
        check(ctx == null) { "Context was already created (multi-context is not yet supported)" }
        ctx = JsContext()
        return ctx!!
    }
}

private object JsLogPrinter : LogPrinter {
    override fun print(lvl: Log.Level, tag: String?, message: String) {
        when (lvl) {
            Log.Level.ERROR -> console.error("${lvl.indicator}/$tag: $message")
            Log.Level.WARN -> console.warn("${lvl.indicator}/$tag: $message")
            Log.Level.INFO -> console.info("${lvl.indicator}/$tag: $message")
            else -> console.log("${lvl.indicator}/$tag: $message")
        }
    }
}