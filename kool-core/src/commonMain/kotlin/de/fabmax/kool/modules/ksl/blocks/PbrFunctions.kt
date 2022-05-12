package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import kotlin.math.PI


class DistributionGgx(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat1>(FUNC_NAME, KslTypeFloat1, parentScope.parentStage) {

    init {
        val n = paramFloat3("n")
        val h = paramFloat3("h")
        val roughness = paramFloat1("roughness")

        body.apply {
            val a = floatVar(roughness * roughness)
            val a2 = floatVar(a * a)
            val nDotH = floatVar(max(dot(n, h), 0f.const))
            val nDotH2 = floatVar(nDotH * nDotH)

            val denom = floatVar(nDotH2 * (a2 - 1f.const) + 1f.const)
            denom set PI.const * denom * denom

            `return`(a2 / denom)
        }
    }

    companion object {
        const val FUNC_NAME = "distributionGgx"
    }
}

class FresnelSchlick(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat3>(FUNC_NAME, KslTypeFloat3, parentScope.parentStage) {

    init {
        val cosTheta = paramFloat1("cosTheta")
        val f0 = paramFloat3("f0")

        body.apply {
            `return`(f0 + (1f.const - f0) * pow(1f.const - cosTheta, 5f.const))

            //return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
        }
    }

    companion object {
        const val FUNC_NAME = "fresnelSchlick"
    }
}

class FresnelSchlickRoughness(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat3>(FUNC_NAME, KslTypeFloat3, parentScope.parentStage) {

    init {
        val cosTheta = paramFloat1("cosTheta")
        val f0 = paramFloat3("f0")
        val roughness = paramFloat1("roughness")

        body.apply {
            val x = floatVar(1f.const - roughness)
            `return`(f0 + (max(float3Value(x, x, x), f0) - f0) * pow(1f.const - cosTheta, 5f.const))
        }
    }

    companion object {
        const val FUNC_NAME = "fresnelSchlickRoughness"
    }
}

class GeometrySmith(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat1>(FUNC_NAME, KslTypeFloat1, parentScope.parentStage) {

    init {
        val n = paramFloat3("n")
        val v = paramFloat3("v")
        val l = paramFloat3("l")
        val roughness = paramFloat1("roughness")

        body.apply {
            val nDotV = floatVar(max(dot(n, v), 0f.const))
            val nDotL = floatVar(max(dot(n, l), 0f.const))
            val ggx1 = floatVar(geometrySchlickGgx(nDotL, roughness))
            val ggx2 = floatVar(geometrySchlickGgx(nDotV, roughness))
            `return`(ggx1 * ggx2)
        }
    }

    companion object {
        const val FUNC_NAME = "geometrySmith"
    }
}

class GeometrySchlickGgx(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat1>(FUNC_NAME, KslTypeFloat1, parentScope.parentStage) {

    init {
        val nDotX = paramFloat1("nDotX")
        val roughness = paramFloat1("roughness")

        body.apply {
            val r = floatVar(roughness + 1f.const)
            val k = floatVar((r * r) / 8f.const)
            val denom = nDotX * (1f.const - k) + k
            `return`(nDotX / denom)
        }
    }

    companion object {
        const val FUNC_NAME = "geometrySchlickGgx"
    }
}

fun KslScopeBuilder.distributionGgx(
    n: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    h: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    roughness: KslScalarExpression<KslTypeFloat1>
): KslScalarExpression<KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(DistributionGgx.FUNC_NAME) { DistributionGgx(this) }
    return KslInvokeFunctionScalar(func, this, KslTypeFloat1, n, h, roughness)
}

fun KslScopeBuilder.fresnelSchlick(
    cosTheta: KslScalarExpression<KslTypeFloat1>,
    f0: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>
): KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(FresnelSchlick.FUNC_NAME) { FresnelSchlick(this) }
    return KslInvokeFunctionVector(func, this, KslTypeFloat3, cosTheta, f0)
}

fun KslScopeBuilder.fresnelSchlickRoughness(
    cosTheta: KslScalarExpression<KslTypeFloat1>,
    f0: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    roughness: KslScalarExpression<KslTypeFloat1>
): KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(FresnelSchlickRoughness.FUNC_NAME) { FresnelSchlickRoughness(this) }
    return KslInvokeFunctionVector(func, this, KslTypeFloat3, cosTheta, f0, roughness)
}

fun KslScopeBuilder.geometrySchlickGgx(
    nDotX: KslScalarExpression<KslTypeFloat1>,
    roughness: KslScalarExpression<KslTypeFloat1>
): KslScalarExpression<KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(GeometrySchlickGgx.FUNC_NAME) { GeometrySchlickGgx(this) }
    return KslInvokeFunctionScalar(func, this, KslTypeFloat1, nDotX, roughness)
}

fun KslScopeBuilder.geometrySmith(
    n: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    v: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    l: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    roughness: KslScalarExpression<KslTypeFloat1>
): KslScalarExpression<KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(GeometrySmith.FUNC_NAME) { GeometrySmith(this) }
    return KslInvokeFunctionScalar(func, this, KslTypeFloat1, n, v, l, roughness)
}
