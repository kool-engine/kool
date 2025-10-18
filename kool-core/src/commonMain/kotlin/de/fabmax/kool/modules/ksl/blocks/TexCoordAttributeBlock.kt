package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.KslBlock
import de.fabmax.kool.modules.ksl.lang.KslExprFloat2
import de.fabmax.kool.modules.ksl.lang.KslScopeBuilder
import de.fabmax.kool.modules.ksl.lang.KslVertexStage
import de.fabmax.kool.scene.VertexLayouts

fun KslScopeBuilder.texCoordAttributeBlock(): TexCoordAttributeBlock {
    val texCoordBlock = TexCoordAttributeBlock(parentStage.program.nextName("texCoordBlock"), this)
    ops += texCoordBlock
    return texCoordBlock
}

class TexCoordAttributeBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val texCoords = mutableMapOf<String, KslExprFloat2>()

    init {
        check(parentScope.parentStage is KslVertexStage) { "TexCoordAttributeBlock must be added to KslVertexStage" }
    }

    fun getTextureCoords(attributeName: String = VertexLayouts.TexCoord.name): KslExprFloat2 {
        return texCoords.getOrPut(attributeName) {
            body.run {
                val vertexStage = parentStage as KslVertexStage

                val uv = vertexStage.program.interStageFloat2()
                uv.input set vertexStage.vertexAttribFloat2(attributeName)
                uv.output
            }
        }
    }
}