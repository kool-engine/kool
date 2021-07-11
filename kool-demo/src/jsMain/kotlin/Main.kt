import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.demo
import kotlinx.browser.window
import kotlin.collections.set

/**
 * @author fabmax
 */
fun main() {
    // optional local directory to load assets from (by default they are loaded from web)
    //Demo.setProperty("assetsBaseDir", "./assets")

    // sub directories for individual asset classes within asset base dir
    //Demo.setProperty("pbrDemo.envMaps", "hdri")
    //Demo.setProperty("pbrDemo.materials", "materials")
    //Demo.setProperty("pbrDemo.models", "models")

    // launch demo
    demo(getParams()["demo"])
}

@Suppress("UNUSED_VARIABLE")
fun getParams(): Map<String, String> {
    val params: MutableMap<String, String> = mutableMapOf()
    if (window.location.search.length > 1) {
        val vars = window.location.search.substring(1).split("&")
        for (pair in vars) {
            val keyVal = pair.split("=")
            if (keyVal.size == 2) {
                val keyEnc = keyVal[0]
                val valEnc = keyVal[1]
                val key = js("decodeURIComponent(keyEnc)").toString()
                val value = js("decodeURIComponent(valEnc)").toString()
                params[key] = value
            }
        }
    }
    return params
}