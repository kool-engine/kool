import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJs
import de.fabmax.kool.demo.demo
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.gl.WebGlBackendProvider
import de.fabmax.kool.pipeline.backend.webgpu.WgpuBackendProvider
import de.fabmax.kool.pipeline.backend.wgpu.Wgpu4kBackendProvider
import kotlinx.browser.window

val params = getParams()
val backendProvider: BackendProvider
    get() = when {
        params["backend"] == "wgpu4k" -> Wgpu4kBackendProvider
        params["backend"] == "webgpu" -> WgpuBackendProvider
        params["backend"] == "webgl" -> WebGlBackendProvider
        else -> WgpuBackendProvider
    }


fun main() = KoolApplication(
    KoolConfigJs(
        renderBackend = backendProvider,
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
