package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute

fun KslScopeBuilder.texCoordAttributeBlock(): TexCoordAttributeBlock {
    val texCoordBlock = TexCoordAttributeBlock(parentStage.program.nextName("texCoordBlock"), this)
    ops += texCoordBlock
    return texCoordBlock
}

class TexCoordAttributeBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val texCoords = mutableMapOf<String, KslInterStageVector<KslTypeFloat2, KslTypeFloat1>>()

    init {
        check(parentScope.parentStage is KslVertexStage) { "TexCoordAttributeBlock must be added to KslVertexStage" }
    }

    fun getAttributeCoords(attribute: Attribute): KslExprFloat2 {
        return texCoords.getOrPut(attribute.name) {
            body.run {
                val vertexStage = parentStage as KslVertexStage

                val uv = vertexStage.program.interStageFloat2()
                uv.input set vertexStage.vertexAttribFloat2(attribute.name)
                uv
            }
        }.output
    }
}