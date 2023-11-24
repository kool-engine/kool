package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import kotlin.math.PI


class DistributionGgx(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {

    init {
        val n = paramFloat3("n")
        val h = paramFloat3("h")
        val roughness = paramFloat1("roughness")

        body {
            val a = float1Var(roughness * roughness)
            val a2 = float1Var(a * a)
            val nDotH = float1Var(max(dot(n, h), 0f.const))
            val nDotH2 = float1Var(nDotH * nDotH)

            val denom = float1Var(nDotH2 * (a2 - 1f.const) + 1f.const)
            denom set PI.const * denom * denom

            return@body a2 / denom
        }
    }

    companion object {
        const val FUNC_NAME = "distributionGgx"
    }
}

class FresnelSchlick(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {

    init {
        val cosTheta = paramFloat1("cosTheta")
        val f0 = paramFloat3("f0")

        body {
            return@body f0 + (1f.const - f0) * pow(1f.const - cosTheta, 5f.const)
        }
    }

    companion object {
        const val FUNC_NAME = "fresnelSchlick"
    }
}

class FresnelSchlickRoughness(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {

    init {
        val cosTheta = paramFloat1("cosTheta")
        val f0 = paramFloat3("f0")
        val roughness = paramFloat1("roughness")

        body {
            val x = float1Var(1f.const - roughness)
            return@body f0 + (max(float3Value(x, x, x), f0) - f0) * pow(1f.const - cosTheta, 5f.const)
        }
    }

    companion object {
        const val FUNC_NAME = "fresnelSchlickRoughness"
    }
}

class GeometrySmith(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {

    init {
        val n = paramFloat3("n")
        val v = paramFloat3("v")
        val l = paramFloat3("l")
        val roughness = paramFloat1("roughness")

        body {
            val nDotV = float1Var(max(dot(n, v), 0f.const))
            val nDotL = float1Var(max(dot(n, l), 0f.const))
            val ggx1 = float1Var(geometrySchlickGgx(nDotL, roughness))
            val ggx2 = float1Var(geometrySchlickGgx(nDotV, roughness))
            return@body ggx1 * ggx2
        }
    }

    companion object {
        const val FUNC_NAME = "geometrySmith"
    }
}

class GeometrySchlickGgx(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {

    init {
        val nDotX = paramFloat1("nDotX")
        val roughness = paramFloat1("roughness")

        body {
            val r = float1Var(roughness + 1f.const)
            val k = float1Var((r * r) / 8f.const)
            val denom = nDotX * (1f.const - k) + k
            return@body nDotX / denom
        }
    }

    companion object {
        const val FUNC_NAME = "geometrySchlickGgx"
    }
}

fun KslScopeBuilder.distributionGgx(
    n: KslExprFloat3,
    h: KslExprFloat3,
    roughness: KslExprFloat1
): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(DistributionGgx.FUNC_NAME) { DistributionGgx(this) }
    return func(n, h, roughness)
}

fun KslScopeBuilder.fresnelSchlick(
    cosTheta: KslExprFloat1,
    f0: KslExprFloat3
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(FresnelSchlick.FUNC_NAME) { FresnelSchlick(this) }
    return func(cosTheta, f0)
}

fun KslScopeBuilder.fresnelSchlickRoughness(
    cosTheta: KslExprFloat1,
    f0: KslExprFloat3,
    roughness: KslExprFloat1
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(FresnelSchlickRoughness.FUNC_NAME) { FresnelSchlickRoughness(this) }
    return func(cosTheta, f0, roughness)
}

fun KslScopeBuilder.geometrySchlickGgx(
    nDotX: KslExprFloat1,
    roughness: KslExprFloat1
): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(GeometrySchlickGgx.FUNC_NAME) { GeometrySchlickGgx(this) }
    return func(nDotX, roughness)
}

fun KslScopeBuilder.geometrySmith(
    n: KslExprFloat3,
    v: KslExprFloat3,
    l: KslExprFloat3,
    roughness: KslExprFloat1
): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(GeometrySmith.FUNC_NAME) { GeometrySmith(this) }
    return func(n, v, l, roughness)
}
