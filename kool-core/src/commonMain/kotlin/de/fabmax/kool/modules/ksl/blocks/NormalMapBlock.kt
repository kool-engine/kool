package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture2dArray

fun KslScopeBuilder.normalMapBlock(
    cfg: NormalMapConfig,
    ddx: KslExprFloat2? = null,
    ddy: KslExprFloat2? = null,
    block: NormalMapBlock.() -> Unit
): NormalMapBlock {
    val normalMapBlock = NormalMapBlock(cfg, parentStage.program.nextName("normalMapBlock"), ddx, ddy, this)
    ops += normalMapBlock.apply(block)
    return normalMapBlock
}

class NormalMapBlock(
    cfg: NormalMapConfig,
    name: String,
    inDdx: KslExprFloat2?,
    inDdy: KslExprFloat2?,
    parentScope: KslScopeBuilder
) : KslBlock(name, parentScope) {

    val inNormalWorldSpace = inFloat3("inNormalWorldSpace")
    val inTangentWorldSpace = inFloat4("inTangentWorldSpace")
    val inTexCoords = inFloat2("inTexCoords")
    val inStrength = inFloat1("inStrength", KslValueFloat1(1f))

    val outBumpNormal = outFloat3()

    init {
        body.apply {
            if (cfg.isNormalMapped) {
                val sample = if (cfg.isArrayNormalMap) {
                    val normalMap = parentStage.program.texture2dArray(cfg.textureName)
                    if (inDdx != null && inDdy != null) {
                        sampleTextureArrayGrad(normalMap, cfg.normalMapArrayIndex.const, inTexCoords, inDdx, inDdy)
                    } else {
                        sampleTextureArray(normalMap, cfg.normalMapArrayIndex.const, inTexCoords)
                    }
                } else {
                    val normalMap = parentStage.program.texture2d(cfg.textureName)
                    if (inDdx != null && inDdy != null) {
                        sampleTextureGrad(normalMap, inTexCoords, inDdx, inDdy)
                    } else {
                        sampleTexture(normalMap, inTexCoords)
                    }
                }
                val mapNormal = float3Var(sample.rgb)
                mapNormal set mapNormal * 2f.const - 1f.const
                outBumpNormal set calcBumpedNormal(inNormalWorldSpace, inTangentWorldSpace, mapNormal, inStrength)

            } else {
                outBumpNormal set inNormalWorldSpace
            }
        }
    }
}

data class NormalMapConfig(
    val isNormalMapped: Boolean,
    val textureName: String,
    val defaultNormalMap: Texture2d?,
    val defaultArrayNormalMap: Texture2dArray?,
    val normalMapArrayIndex: Int,
    val strengthCfg: PropertyBlockConfig
) {
    val isArrayNormalMap: Boolean get() = isNormalMapped && normalMapArrayIndex >= 0

    class Builder(var normalMapName: String = "tNormalMap") {
        var isNormalMapped: Boolean = false
        var defaultNormalMap: Texture2d? = null
        var defaultArrayNormalMap: Texture2dArray? = null
        var arrayIndex = -1
        val strengthCfg: PropertyBlockConfig.Builder = PropertyBlockConfig.Builder("${normalMapName}_strength").constProperty(1f)

        fun clearNormalMap(): Builder {
            isNormalMapped = false
            defaultNormalMap = null
            defaultArrayNormalMap = null
            arrayIndex = -1
            return this
        }

        fun useNormalMap(texture: Texture2d? = null, texName: String = "tNormalMap"): Builder {
            isNormalMapped = true
            defaultNormalMap = texture
            defaultArrayNormalMap = null
            arrayIndex = -1
            normalMapName = texName
            return this
        }

        fun useNormalMapFromArray(arrayIndex: Int, texName: String, texture: Texture2dArray? = null): Builder {
            this.arrayIndex = arrayIndex
            defaultArrayNormalMap = texture
            isNormalMapped = true
            defaultNormalMap = null
            normalMapName = texName
            return this
        }

        fun build() = NormalMapConfig(
            isNormalMapped = isNormalMapped,
            textureName = normalMapName,
            defaultNormalMap = defaultNormalMap,
            defaultArrayNormalMap = defaultArrayNormalMap,
            normalMapArrayIndex = arrayIndex,
            strengthCfg = strengthCfg.build()
        )
    }
}
