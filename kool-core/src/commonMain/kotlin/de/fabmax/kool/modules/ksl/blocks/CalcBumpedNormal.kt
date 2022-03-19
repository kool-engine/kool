package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

class CalcBumpedNormal(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat3>(FUNC_NAME, KslTypeFloat3, parentScope.parentStage) {

    init {
        val normal = paramFloat3("normal")
        val tangent = paramFloat4("tangent")
        val bumpNormal = paramFloat3("bumpNormal")
        val strength = paramFloat1("strength")

        body.apply {
            val tang = float3Var(normalize(tangent.xyz - dot(tangent.xyz, normal) * normal))
            val bitangent = cross(normal, tang)
            val tbn = mat3Var(constMat3(tang, bitangent, normal))
            `return`(normalize(mix(normal, tbn * bumpNormal, strength)))
        }
    }

    companion object {
        const val FUNC_NAME = "calcBumpedNormal"
    }
}

fun KslScopeBuilder.calcBumpedNormal(
    normal: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    tangent: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
    bumpNormal: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    strength: KslScalarExpression<KslTypeFloat1>
): KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(CalcBumpedNormal.FUNC_NAME) { CalcBumpedNormal(this) }
    return KslInvokeFunctionVector(func, this, KslTypeFloat3, normal, tangent, bumpNormal, strength)
}