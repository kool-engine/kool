package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslExpressionConst<T: KslType>(override val expressionType: T, private val pseudoCode: String) : KslExpression<T> {
    override fun collectStateDependencies() = emptySet<KslMutatedState>()
    override fun toPseudoCode() = pseudoCode
}

class KslConstFloat1(val value: Float)
    : KslExpressionConst<KslTypeFloat1>(KslTypeFloat1, "$value"), KslScalarExpression<KslTypeFloat1> {
    override fun generateExpression(generator: KslGenerator) = generator.constFloatExpression(value)
}
class KslConstFloat2(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>)
    : KslExpressionConst<KslTypeFloat2>(KslTypeFloat2, "vec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"),
      KslVectorExpression<KslTypeFloat2, KslTypeFloat1> {
    constructor(x: Float, y: Float) : this(KslConstFloat1(x), KslConstFloat1(y))
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y)
}
class KslConstFloat3(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>, val z: KslExpression<KslTypeFloat1>)
    : KslExpressionConst<KslTypeFloat3>(KslTypeFloat3, "vec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"),
      KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    constructor(x: Float, y: Float, z: Float) : this(KslConstFloat1(x), KslConstFloat1(y), KslConstFloat1(z))
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y, z)
}
class KslConstFloat4(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>, val z: KslExpression<KslTypeFloat1>, val w: KslExpression<KslTypeFloat1>)
    : KslExpressionConst<KslTypeFloat4>(KslTypeFloat4, "vec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"),
      KslVectorExpression<KslTypeFloat4, KslTypeFloat1> {
    constructor(x: Float, y: Float, z: Float, w: Float) : this(KslConstFloat1(x), KslConstFloat1(y), KslConstFloat1(z), KslConstFloat1(w))
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y, z, w)
}

class KslConstInt1(val value: Int)
    : KslExpressionConst<KslTypeInt1>(KslTypeInt1, "$value"), KslScalarExpression<KslTypeInt1> {
    override fun generateExpression(generator: KslGenerator) = generator.constIntExpression(value)
}
class KslConstInt2(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>)
    : KslExpressionConst<KslTypeInt2>(KslTypeInt2, "ivec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"), KslVectorExpression<KslTypeInt2, KslTypeInt1> {
    constructor(x: Int, y: Int) : this(KslConstInt1(x), KslConstInt1(y))
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y)
}
class KslConstInt3(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>, val z: KslExpression<KslTypeInt1>)
    : KslExpressionConst<KslTypeInt3>(KslTypeInt3, "ivec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"), KslVectorExpression<KslTypeInt3, KslTypeInt1> {
    constructor(x: Int, y: Int, z: Int) : this(KslConstInt1(x), KslConstInt1(y), KslConstInt1(z))
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y, z)
}
class KslConstInt4(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>, val z: KslExpression<KslTypeInt1>, val w: KslExpression<KslTypeInt1>)
    : KslExpressionConst<KslTypeInt4>(KslTypeInt4, "ivec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"), KslVectorExpression<KslTypeInt4, KslTypeInt1> {
    constructor(x: Int, y: Int, z: Int, w: Int) : this(KslConstInt1(x), KslConstInt1(y), KslConstInt1(z), KslConstInt1(w))
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y, z, w)
}

class KslConstBool1(val value: Boolean)
    : KslExpressionConst<KslTypeBool1>(KslTypeBool1, "$value"), KslScalarExpression<KslTypeBool1> {
    override fun generateExpression(generator: KslGenerator) = generator.constBoolExpression(value)
}
class KslConstBool2(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>)
    : KslExpressionConst<KslTypeBool2>(KslTypeBool2, "bvec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"), KslVectorExpression<KslTypeBool2, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean) : this(KslConstBool1(x), KslConstBool1(y))
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y)
}
class KslConstBool3(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>, val z: KslExpression<KslTypeBool1>)
    : KslExpressionConst<KslTypeBool3>(KslTypeBool3, "bvec3(${x.toPseudoCode()}, ${y.toPseudoCode()})"), KslVectorExpression<KslTypeBool3, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean, z: Boolean) : this(KslConstBool1(x), KslConstBool1(y), KslConstBool1(z))
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y, z)
}
class KslConstBool4(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>, val z: KslExpression<KslTypeBool1>, val w: KslExpression<KslTypeBool1>)
    : KslExpressionConst<KslTypeBool4>(KslTypeBool4, "bvec4(${x.toPseudoCode()}, ${y.toPseudoCode()})"), KslVectorExpression<KslTypeBool4, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean, z: Boolean, w: Boolean) : this(KslConstBool1(x), KslConstBool1(y), KslConstBool1(z), KslConstBool1(w))
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y, z, w)
}
