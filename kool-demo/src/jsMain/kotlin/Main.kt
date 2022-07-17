import de.fabmax.kool.demo.demo
import kotlinx.browser.window
import kotlin.collections.set

/**
 * @author fabmax
 */
fun main() {
    // optional local directory to load assets from (by default they are loaded from web)
    //Demo.setProperty("assets.base", "./assets")

    // sub directories for individual asset classes within asset base dir
    //Demo.setProperty("assets.hdri", "hdri")
    //Demo.setProperty("assets.materials", "materials")
    //Demo.setProperty("assets.models", "models")

    // launch demo
    val params = getParams()
    if ("webgpu" in params.keys) {
        webGpuTest()
    } else {
        demo(getParams()["demo"])
    }
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