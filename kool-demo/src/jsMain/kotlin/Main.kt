import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJs
import de.fabmax.kool.demo.demo
import de.fabmax.kool.physics.Physics
import kotlinx.browser.window
import kotlin.collections.set

val params = getParams()
val isWebGpu: Boolean
    get() = params["backend"] == "webgpu"

fun main() = KoolApplication(
    KoolConfigJs(
        renderBackend = if (isWebGpu) KoolConfigJs.Backend.WEB_GPU else KoolConfigJs.Backend.WEB_GL2,
        isGlobalKeyEventGrabbing = true,
        loaderTasks = listOf { Physics.loadAndAwaitPhysics() }
    )
) {
    // uncomment to load assets locally instead of from remote
    //DemoLoader.setProperty("assets.base", ".")

    // launch demo
    demo(params["demo"], it)
}

@Suppress("UNUSED_VARIABLE")
fun getParams(): Map<String, String> {
    val params: MutableMap<String, String> = mutableMapOf()
    if (window.location.search.length > 1) {
        val vars = window.location.search.substring(1).split("&").filter { it.isNotBlank() }
        for (pair in vars) {
            val keyVal = pair.split("=")
            val keyEnc = keyVal[0]
            val key = js("decodeURIComponent(keyEnc)").toString()
            val value = if (keyVal.size == 2) {
                val valEnc = keyVal[1]
                js("decodeURIComponent(valEnc)").toString()
            } else {
                ""
            }
            params[key] = value
        }
    }
    return params
}
