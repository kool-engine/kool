import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.demo
import kotlin.browser.window
import kotlin.collections.set

/**
 * @author fabmax
 */
fun main() {
    Demo.setProperty("assetsBaseDir", "../assets")

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