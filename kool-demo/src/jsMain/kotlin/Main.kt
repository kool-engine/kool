import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJs
import de.fabmax.kool.demo.demo
import de.fabmax.kool.physics.Physics
import kotlinx.browser.window

val params = getParams()
val backend: KoolConfigJs.Backend
    get() = when {
        params["backend"] == "webgpu" -> KoolConfigJs.Backend.WEB_GPU
        params["backend"] == "webgl" -> KoolConfigJs.Backend.WEB_GL2
        else -> KoolConfigJs.Backend.PREFER_WEB_GPU
    }


fun main() = KoolApplication(
    KoolConfigJs(
        renderBackend = backend,
        isGlobalKeyEventGrabbing = true,
        deviceScaleLimit = 1.5,
        loaderTasks = listOf { Physics.loadAndAwaitPhysics() }
    )
) {
    // uncomment to load assets locally instead of from remote
    //DemoLoader.setProperty("assets.base", ".")

    // launch demo
    demo(params["demo"], ctx)
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
