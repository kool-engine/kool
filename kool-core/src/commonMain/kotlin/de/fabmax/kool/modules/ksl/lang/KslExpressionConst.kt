package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslExpressionConst<T: KslType>(override val expressionType: T) : KslExpression<T> {
    override fun collectStateDependencies() = emptySet<KslMutatedState>()
}

class KslConstFloat1(val value: Float)
    : KslExpressionConst<KslTypeFloat1>(KslTypeFloat1), KslScalarExpression<KslTypeFloat1> {
    override fun generateExpression(generator: KslGenerator) = generator.constFloatExpression(value)
    override fun toPseudoCode() = "$value"
}
class KslConstFloat2(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>)
    : KslExpressionConst<KslTypeFloat2>(KslTypeFloat2),
      KslVectorExpression<KslTypeFloat2, KslTypeFloat1> {
    constructor(x: Float, y: Float) : this(KslConstFloat1(x), KslConstFloat1(y))
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y)
    override fun toPseudoCode() = "vec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslConstFloat3(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>, val z: KslExpression<KslTypeFloat1>)
    : KslExpressionConst<KslTypeFloat3>(KslTypeFloat3),
      KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    constructor(x: Float, y: Float, z: Float) : this(KslConstFloat1(x), KslConstFloat1(y), KslConstFloat1(z))
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y, z)
    override fun toPseudoCode() = "vec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslConstFloat4(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>, val z: KslExpression<KslTypeFloat1>, val w: KslExpression<KslTypeFloat1>)
    : KslExpressionConst<KslTypeFloat4>(KslTypeFloat4),
      KslVectorExpression<KslTypeFloat4, KslTypeFloat1> {
    constructor(x: Float, y: Float, z: Float, w: Float) : this(KslConstFloat1(x), KslConstFloat1(y), KslConstFloat1(z), KslConstFloat1(w))
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y, z, w)
    override fun toPseudoCode() = "vec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslConstInt1(val value: Int)
    : KslExpressionConst<KslTypeInt1>(KslTypeInt1), KslScalarExpression<KslTypeInt1> {
    override fun generateExpression(generator: KslGenerator) = generator.constIntExpression(value)
    override fun toPseudoCode() = "$value"
}
class KslConstInt2(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>)
    : KslExpressionConst<KslTypeInt2>(KslTypeInt2), KslVectorExpression<KslTypeInt2, KslTypeInt1> {
    constructor(x: Int, y: Int) : this(KslConstInt1(x), KslConstInt1(y))
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y)
    override fun toPseudoCode() = "ivec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslConstInt3(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>, val z: KslExpression<KslTypeInt1>)
    : KslExpressionConst<KslTypeInt3>(KslTypeInt3), KslVectorExpression<KslTypeInt3, KslTypeInt1> {
    constructor(x: Int, y: Int, z: Int) : this(KslConstInt1(x), KslConstInt1(y), KslConstInt1(z))
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y, z)
    override fun toPseudoCode() = "ivec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslConstInt4(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>, val z: KslExpression<KslTypeInt1>, val w: KslExpression<KslTypeInt1>)
    : KslExpressionConst<KslTypeInt4>(KslTypeInt4), KslVectorExpression<KslTypeInt4, KslTypeInt1> {
    constructor(x: Int, y: Int, z: Int, w: Int) : this(KslConstInt1(x), KslConstInt1(y), KslConstInt1(z), KslConstInt1(w))
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y, z, w)
    override fun toPseudoCode() = "ivec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslConstBool1(val value: Boolean)
    : KslExpressionConst<KslTypeBool1>(KslTypeBool1), KslScalarExpression<KslTypeBool1> {
    override fun generateExpression(generator: KslGenerator) = generator.constBoolExpression(value)
    override fun toPseudoCode() = "$value"
}
class KslConstBool2(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>)
    : KslExpressionConst<KslTypeBool2>(KslTypeBool2), KslVectorExpression<KslTypeBool2, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean) : this(KslConstBool1(x), KslConstBool1(y))
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y)
    override fun toPseudoCode() = "bvec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslConstBool3(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>, val z: KslExpression<KslTypeBool1>)
    : KslExpressionConst<KslTypeBool3>(KslTypeBool3), KslVectorExpression<KslTypeBool3, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean, z: Boolean) : this(KslConstBool1(x), KslConstBool1(y), KslConstBool1(z))
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y, z)
    override fun toPseudoCode() = "bvec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslConstBool4(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>, val z: KslExpression<KslTypeBool1>, val w: KslExpression<KslTypeBool1>)
    : KslExpressionConst<KslTypeBool4>(KslTypeBool4), KslVectorExpression<KslTypeBool4, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean, z: Boolean, w: Boolean) : this(KslConstBool1(x), KslConstBool1(y), KslConstBool1(z), KslConstBool1(w))
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y, z, w)
    override fun toPseudoCode() = "bvec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslConstMat2(val col0: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>,
                   val col1: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>)
    : KslExpressionConst<KslTypeMat2>(KslTypeMat2), KslMatrixExpression<KslTypeMat2, KslTypeFloat2> {
    constructor(col0: Vec2f, col1: Vec2f) : this(KslConstFloat2(col0.x, col0.y), KslConstFloat2(col1.x, col1.y))
    override fun generateExpression(generator: KslGenerator) = generator.constMatExpression(col0, col1)
    override fun toPseudoCode() = "mat2(${col0.toPseudoCode()}, ${col1.toPseudoCode()})"
}
class KslConstMat3(val col0: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
                   val col1: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
                   val col2: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>)
    : KslExpressionConst<KslTypeMat3>(KslTypeMat3), KslMatrixExpression<KslTypeMat3, KslTypeFloat3> {
    constructor(col0: Vec3f, col1: Vec3f, col2: Vec3f) : this(KslConstFloat3(col0.x, col0.y, col0.z),
        KslConstFloat3(col1.x, col1.y, col1.z), KslConstFloat3(col2.x, col2.y, col2.z))
    override fun generateExpression(generator: KslGenerator) = generator.constMatExpression(col0, col1, col2)
    override fun toPseudoCode() = "mat3(${col0.toPseudoCode()}, ${col1.toPseudoCode()}, ${col2.toPseudoCode()})"
}
class KslConstMat4(val col0: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                   val col1: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                   val col2: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                   val col3: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>)
    : KslExpressionConst<KslTypeMat4>(KslTypeMat4), KslMatrixExpression<KslTypeMat4, KslTypeFloat4> {
    constructor(col0: Vec4f, col1: Vec4f, col2: Vec4f, col3: Vec4f) : this(KslConstFloat4(col0.x, col0.y, col0.z, col0.w),
        KslConstFloat4(col1.x, col1.y, col1.z, col1.w), KslConstFloat4(col2.x, col2.y, col2.z, col2.w), KslConstFloat4(col3.x, col3.y, col3.z, col3.w))
    override fun generateExpression(generator: KslGenerator) = generator.constMatExpression(col0, col1, col2, col3)
    override fun toPseudoCode() = "mat4(${col0.toPseudoCode()}, ${col1.toPseudoCode()}, ${col2.toPseudoCode()}, ${col3.toPseudoCode()})"
}
