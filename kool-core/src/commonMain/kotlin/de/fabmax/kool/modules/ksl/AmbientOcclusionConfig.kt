package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.Texture2d

class AmbientOcclusionConfig {

    var isSsao = false
    var defaultSsaoMap: Texture2d? = null
    val materialAo = PropertyBlockConfig("ao").apply { constProperty(1f) }

    fun enableSsao(ssaoMap: Texture2d? = null) {
        isSsao = ssaoMap != null
        defaultSsaoMap = ssaoMap
    }

}