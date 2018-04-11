import de.fabmax.kool.createContext
import de.fabmax.kool.demo.Demo
import kotlin.browser.window
import kotlin.collections.set

/**
 * @author fabmax
 */
fun main() {
    // create KoolContext
    val ctx = createContext()

    // this assumes that the server directory is in docs/kool-js, assets are located next to it
    ctx.assetMgr.assetsBaseDir = "../assets"

    // launch demo
    Demo(ctx, getParams()["demo"])
}

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
