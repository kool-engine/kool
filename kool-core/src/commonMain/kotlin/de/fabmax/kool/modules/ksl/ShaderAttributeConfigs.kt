package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d

data class BasicVertexConfig(
    val isInstanced: Boolean,
    val isFlipBacksideNormals: Boolean,
    val maxNumberOfBones: Int,
    val morphAttributes: List<Attribute>,
    val displacementCfg: PropertyBlockConfig,
    val modelMatrixComposition: List<ModelMatrixComposition>
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
        var modelMatrixComposition = listOf<ModelMatrixComposition>()

        fun enableArmature(maxNumberOfBones: Int = 32): Builder {
            this.maxNumberOfBones = maxNumberOfBones
            return this
        }

        fun displacement(block: PropertyBlockConfig.Builder.() -> Unit): Builder {
            displacementCfg.block()
            return this
        }

        fun build() = BasicVertexConfig(
            isInstanced = isInstanced,
            isFlipBacksideNormals = isFlipBacksideNormals,
            maxNumberOfBones = maxNumberOfBones,
            morphAttributes = morphAttributes.toList(),
            displacementCfg = displacementCfg.build(),
            modelMatrixComposition = modelMatrixComposition.ifEmpty {
                buildList {
                    add(ModelMatrixComposition.UNIFORM_MODEL_MAT)
                    if (isInstanced) {
                        add(ModelMatrixComposition.INSTANCE_MODEL_MAT)
                    }
                }
            }
        )
    }
}

enum class ModelMatrixComposition {
    UNIFORM_MODEL_MAT,
    INSTANCE_MODEL_MAT
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