package de.fabmax.kool.demo

import de.fabmax.kool.platform.PlatformImpl
import kotlin.browser.window

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    Demo(PlatformImpl.initContext(), getParams()["demo"])
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
                params.put(key, value)
                println(key + " = " + value)
            }
        }
    }
    return params
}
