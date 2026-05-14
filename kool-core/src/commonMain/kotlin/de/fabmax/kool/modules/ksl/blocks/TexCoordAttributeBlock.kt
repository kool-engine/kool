package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.VertexLayouts

context(builder: KslScopeBuilder)
fun texCoordAttributeBlock(): TexCoordAttributeBlock {
    val texCoordBlock = TexCoordAttributeBlock(builder.parentStage.program.nextName("texCoordBlock"), builder)
    builder.ops += texCoordBlock
    return texCoordBlock
}

class TexCoordAttributeBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val texCoords = mutableMapOf<String, KslExprFloat2>()

    init {
        check(parentScope.parentStage is KslVertexStage) { "TexCoordAttributeBlock must be added to KslVertexStage" }
    }

    fun getTextureCoords(attributeName: String = VertexLayouts.TexCoord.name): KslExprFloat2 {
        return texCoords.getOrPut(attributeName) {
            body {
                val vertexStage = parentStage as KslVertexStage
                val uv = interStageFloat2()
                uv.input set vertexStage.vertexAttribFloat2(attributeName)
                uv.output
            }
        }
    }
}