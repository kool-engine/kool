package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d

data class BasicVertexConfig(
    var isInstanced: Boolean,
    var isFlipBacksideNormals: Boolean,
    var maxNumberOfBones: Int,
    val morphAttributes: List<Attribute>,
    val displacementCfg: PropertyBlockConfig
) {
    val isArmature: Boolean
        get() = maxNumberOfBones > 0
    val isMorphing: Boolean
        get() = morphAttributes.isNotEmpty()

    class Builder {
        var isInstanced: Boolean = false
        var isFlipBacksideNormals: Boolean = true
        var maxNumberOfBones: Int = 0
        val morphAttributes: MutableList<Attribute> = mutableListOf()
        val displacementCfg: PropertyBlockConfig.Builder = PropertyBlockConfig.Builder("displacement").apply { constProperty(0f) }

        fun enableArmature(maxNumberOfBones: Int = 32): Builder {
            this.maxNumberOfBones = maxNumberOfBones
            return this
        }

        fun displacement(block: PropertyBlockConfig.Builder.() -> Unit): Builder {
            displacementCfg.block()
            return this
        }

        fun build() = BasicVertexConfig(isInstanced, isFlipBacksideNormals, maxNumberOfBones, morphAttributes.toList(), displacementCfg.build())
    }
}

data class AmbientOcclusionConfig(
    val isSsao: Boolean,
    val defaultSsaoMap: Texture2d?,
    val materialAo: PropertyBlockConfig
) {
    class Builder {
        var isSsao: Boolean = false
        var defaultSsaoMap: Texture2d? = null
        val materialAo: PropertyBlockConfig.Builder = PropertyBlockConfig.Builder("ao").apply { constProperty(1f) }

        fun enableSsao(ssaoMap: Texture2d? = null): Builder {
            isSsao = ssaoMap != null
            defaultSsaoMap = ssaoMap
            return this
        }

        fun materialAo(block: PropertyBlockConfig.Builder.() -> Unit): Builder {
            materialAo.block()
            return this
        }

        fun build() = AmbientOcclusionConfig(isSsao, defaultSsaoMap, materialAo.build())
    }
}