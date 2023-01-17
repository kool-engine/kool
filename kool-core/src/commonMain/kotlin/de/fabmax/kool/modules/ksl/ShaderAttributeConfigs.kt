package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.Texture2d

class BasicVertexConfig {
    var isInstanced = false
    var isFlipBacksideNormals = true
    var maxNumberOfBones = 0
    val isArmature: Boolean
        get() = maxNumberOfBones > 0

    fun enableArmature(maxNumberOfBones: Int = 32) {
        this.maxNumberOfBones = maxNumberOfBones
    }
}

class AmbientOcclusionConfig {
    var isSsao = false
    var defaultSsaoMap: Texture2d? = null
    val materialAo = PropertyBlockConfig("ao").apply { constProperty(1f) }

    fun enableSsao(ssaoMap: Texture2d? = null) {
        isSsao = ssaoMap != null
        defaultSsaoMap = ssaoMap
    }

    fun materialAo(block: PropertyBlockConfig.() -> Unit) {
        materialAo.block()
    }
}