import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfig
import de.fabmax.kool.demo.demo
import kotlinx.browser.window
import kotlin.collections.set

/**
 * @author fabmax
 */
fun main() = KoolApplication(
    KoolConfig(isGlobalKeyEventGrabbing = true)
) {
    // uncomment to load assets locally instead of from remote
    //Demo.setProperty("assets.base", ".")

    // sub-directories for individual asset classes within asset base dir
    //Demo.setProperty("assets.hdri", "hdri")
    //Demo.setProperty("assets.materials", "materials")
    //Demo.setProperty("assets.models", "models")

    // launch demo
    demo(getParams()["demo"], it)
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