package de.fabmax.kool

import de.fabmax.kool.platform.WasmContext
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
        else -> {
            val s = toString()
            val p = s.indexOf('.')
            if (p < 0) {
                "$s.${"0".repeat(precision)}"
            } else {
                val r = s.length - (p + 1)
                if (r < precision) {
                    s + "0".repeat(precision - r)
                } else {
                    s.substring(0, (p + precision + 1).coerceAtMost(s.length))
                }
            }
        }
    }
}

val KoolSystem.configWasm: KoolConfigWasm get() = config as KoolConfigWasm

/**
 * Creates a new [KoolContext] with the given [KoolConfigWasm]. Notice that there can only be one [KoolContext], calling
 * this method multiple times is an error.
 */
suspend fun createContext(config: KoolConfigWasm = KoolConfigWasm()): WasmContext {
    KoolSystem.initialize(config)
    return WasmImpl.createContext()
}

fun KoolApplication(config: KoolConfigWasm = KoolConfigWasm(), appBlock: suspend KoolApplication.() -> Unit) {
    ApplicationScope.launch {
        val ctx = createContext(config)
        KoolApplication(ctx, appBlock)
    }
}

fun KoolApplication(ctx: WasmContext, appBlock: suspend KoolApplication.() -> Unit) {
    val koolApp = KoolApplication(ctx)
    FrontendScope.launch {
        koolApp.appBlock()
    }
    ctx.run()
}

internal object WasmImpl {
    private var ctx: WasmContext? = null
    val canvas: HTMLCanvasElement
        get() = checkNotNull(ctx?.window?.canvas) { "Platform.createContext() not called" }

    init {
        if (Log.printer == Log.DEFAULT_PRINTER) {
            Log.printer = JsLogPrinter
        }
    }

    suspend fun createContext(): WasmContext {
        check(ctx == null) { "Context was already created (multi-context is not yet supported)" }
        ctx = WasmContext()
        return ctx!!
    }
}

private object JsLogPrinter : LogPrinter {
    override fun print(lvl: Log.Level, tag: String?, message: String) {

        when (lvl) {
            Log.Level.ERROR -> println("${lvl.indicator}/$tag: $message")
            Log.Level.WARN -> println("${lvl.indicator}/$tag: $message")
            Log.Level.INFO -> println("${lvl.indicator}/$tag: $message")
            else -> println("${lvl.indicator}/$tag: $message")
        }
    }
}