package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

class GetLinearDepth(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat1>(FUNC_NAME, KslTypeFloat1, parentScope.parentStage) {

    init {
        val depth = paramFloat1("depth")
        val camNear = paramFloat1("camNear")
        val camFar = paramFloat1("camFar")

        body {
            val depthN = float1Var(2f.const * depth - 1f.const)
            return@body 2f.const * camNear * camFar / (camFar + camNear - depthN * (camFar - camNear))
        }
    }

    companion object {
        const val FUNC_NAME = "GetLinearDepth"
    }
}

fun KslScopeBuilder.getLinearDepth(
    depth: KslExprFloat1,
    camNear: KslExprFloat1,
    camFar: KslExprFloat1
): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(GetLinearDepth.FUNC_NAME) { GetLinearDepth(this) }
    return func(depth, camNear, camFar)
}
