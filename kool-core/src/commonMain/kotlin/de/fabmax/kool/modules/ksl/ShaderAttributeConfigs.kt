package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslLitShader.AmbientLight
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

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

        fun enableArmatureFixedNumberOfBones(fixedNumberOfBones: Int): Builder {
            this.maxNumberOfBones = fixedNumberOfBones
            return this
        }

        fun enableArmature(numberOfBones: Int): Builder {
            this.maxNumberOfBones = (numberOfBones + 63) and 63.inv()
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

data class LightingConfig(
    val ambientLight: AmbientLight,
    val lightStrength: Float,
    val maxNumberOfLights: Int,
    val shadowMaps: List<ShadowMapConfig>,
    val flipBacksideNormals: Boolean,
    val isSsao: Boolean,
    val defaultSsaoMap: Texture2d?,
) {
    class Builder {
        var ambientLight: AmbientLight = AmbientLight.Uniform(Color(0.2f, 0.2f, 0.2f).toLinear())
        var lightStrength: Float = 1f
        var maxNumberOfLights: Int = 4
        val shadowMaps = mutableListOf<ShadowMapConfig>()
        var flipBacksideNormals = false
        var isSsao = false
        var defaultSsaoMap: Texture2d? = null

        fun enableSsao(ssaoMap: Texture2d? = null): Builder {
            isSsao = ssaoMap != null
            defaultSsaoMap = ssaoMap
            return this
        }

        fun addShadowMap(shadowMap: ShadowMap, samplePattern: List<Vec2f> = ShadowMapConfig.SHADOW_SAMPLE_PATTERN_4x4): Builder {
            shadowMaps += ShadowMapConfig(shadowMap, samplePattern)
            return this
        }

        fun addShadowMaps(shadowMaps: Collection<ShadowMap>, samplePattern: List<Vec2f> = ShadowMapConfig.SHADOW_SAMPLE_PATTERN_4x4): Builder {
            this.shadowMaps += shadowMaps.map { ShadowMapConfig(it, samplePattern) }
            return this
        }

        fun uniformAmbientLight(ambientFactor: Color = Color(0.2f, 0.2f, 0.2f).toLinear()): Builder {
            ambientLight = AmbientLight.Uniform(ambientFactor)
            return this
        }

        fun imageBasedAmbientLight(ambientTexture: TextureCube? = null, ambientFactor: Color = Color.WHITE): Builder {
            ambientLight = AmbientLight.ImageBased(ambientTexture, ambientFactor)
            return this
        }

        fun dualImageBasedAmbientLight(ambientFactor: Color = Color.WHITE): Builder {
            ambientLight = AmbientLight.DualImageBased(ambientFactor)
            return this
        }

        fun build(): LightingConfig {
            return LightingConfig(
                ambientLight = ambientLight,
                lightStrength = lightStrength,
                maxNumberOfLights = maxNumberOfLights,
                shadowMaps = shadowMaps.toList(),
                flipBacksideNormals = flipBacksideNormals,
                isSsao = isSsao,
                defaultSsaoMap = defaultSsaoMap,
            )
        }
    }
}

data class ShadowMapConfig(val shadowMap: ShadowMap, val samplePattern: List<Vec2f> = SHADOW_SAMPLE_PATTERN_4x4) {
    companion object {
        val SHADOW_SAMPLE_PATTERN_1x1: List<Vec2f> = listOf(Vec2f.ZERO)
        val SHADOW_SAMPLE_PATTERN_3x3: List<Vec2f> = buildList {
            for (y in -1..1) {
                for (x in -1..1) {
                    add(Vec2f(x.toFloat(), y.toFloat()))
                }
            }
        }
        val SHADOW_SAMPLE_PATTERN_4x4: List<Vec2f> = buildList {
            for (y in 0..3) {
                for (x in 0..3) {
                    add(Vec2f(x - 1.5f, y - 1.5f))
                }
            }
        }
    }
}
