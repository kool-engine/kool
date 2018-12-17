package de.fabmax.kool

import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap

class RenderingHints {
    var shadowRange = 100f
    var shadowPreset: ShadowPreset = ShadowPreset.SHADOW_HIGH
}

enum class ShadowPreset {
    SHADOW_OFF {
        override fun createShadowMap(hints: RenderingHints): ShadowMap? = null
    },
    SHADOW_LOW {
        override fun createShadowMap(hints: RenderingHints): ShadowMap? = SimpleShadowMap(0f, hints.shadowRange)
    },
    SHADOW_MEDIUM {
        override fun createShadowMap(hints: RenderingHints): ShadowMap? = SimpleShadowMap(0f, hints.shadowRange, 4096)
    },
    SHADOW_HIGH {
        override fun createShadowMap(hints: RenderingHints): ShadowMap? = CascadedShadowMap.defaultCascadedShadowMap3(hints.shadowRange)
    },
    SHADOW_ULTRA {
        override fun createShadowMap(hints: RenderingHints): ShadowMap? = CascadedShadowMap.defaultCascadedShadowMap3(hints.shadowRange, 4096)
    };

    abstract fun createShadowMap(hints: RenderingHints): ShadowMap?
}
