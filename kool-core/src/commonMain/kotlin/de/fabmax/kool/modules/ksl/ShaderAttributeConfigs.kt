package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d

data class BasicVertexConfig(
    var isInstanced: Boolean = false,
    var isFlipBacksideNormals: Boolean = true,
    var maxNumberOfBones: Int = 0,
    val morphAttributes: MutableList<Attribute> = mutableListOf(),
    val displacementCfg: PropertyBlockConfig = PropertyBlockConfig("displacement").apply { constProperty(0f) }
) {

    val isArmature: Boolean
        get() = maxNumberOfBones > 0
    val isMorphing: Boolean
        get() = morphAttributes.isNotEmpty()

    fun enableArmature(maxNumberOfBones: Int = 32) {
        this.maxNumberOfBones = maxNumberOfBones
    }

    fun displacement(block: PropertyBlockConfig.() -> Unit) {
        displacementCfg.block()
    }
}

data class AmbientOcclusionConfig(
    var isSsao: Boolean = false,
    var defaultSsaoMap: Texture2d? = null,
    val materialAo: PropertyBlockConfig = PropertyBlockConfig("ao").apply { constProperty(1f) }
) {
    fun enableSsao(ssaoMap: Texture2d? = null) {
        isSsao = ssaoMap != null
        defaultSsaoMap = ssaoMap
    }

    fun materialAo(block: PropertyBlockConfig.() -> Unit) {
        materialAo.block()
    }
}