package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d

fun KslScopeBuilder.normalMapBlock(cfg: NormalMapConfig, block: NormalMapBlock.() -> Unit): NormalMapBlock {
    val normalMapBlock = NormalMapBlock(cfg, parentStage.program.nextName("normalMapBlock"), this)
    ops += normalMapBlock.apply(block)
    return normalMapBlock
}

class NormalMapBlock(cfg: NormalMapConfig, name: String, parentScope: KslScopeBuilder)
    : KslBlock(name, parentScope) {

    val inNormalWorldSpace = inFloat3("inNormalWorldSpace")
    val inTangentWorldSpace = inFloat4("inTangentWorldSpace")
    val inTexCoords = inFloat2("inTexCoords")
    val inStrength = inFloat1("inStrength", KslValueFloat1(1f))

    val outBumpNormal = outFloat3()

    init {
        body.apply {
            if (cfg.isNormalMapped) {
                val normalMap = parentStage.program.texture2d(cfg.normalMapName)
                val mapNormal = normalize(sampleTexture(normalMap, inTexCoords).xyz * 2f.const - 1f.const)
                outBumpNormal set calcBumpedNormal(inNormalWorldSpace, inTangentWorldSpace, mapNormal, inStrength)

            } else {
                outBumpNormal set inNormalWorldSpace
            }
        }
    }
}

data class NormalMapConfig(
    var isNormalMapped: Boolean = false,
    var normalMapName: String = "tNormalMap",
    var coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
    var defaultNormalMap: Texture2d? = null,
    val strengthCfg: PropertyBlockConfig = PropertyBlockConfig("normalMapStrength").apply { constProperty(1f) }
) {
    fun clearNormalMap() {
        isNormalMapped = false
        defaultNormalMap = null
    }

    fun setNormalMap(texture: Texture2d? = null, normalMapName: String = "tNormalMap", coordAttribute: Attribute = Attribute.TEXTURE_COORDS) {
        this.isNormalMapped = true
        this.normalMapName = normalMapName
        this.coordAttribute = coordAttribute
        this.defaultNormalMap = texture
    }
}
