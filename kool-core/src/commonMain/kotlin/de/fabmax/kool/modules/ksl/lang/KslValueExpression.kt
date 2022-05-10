package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslValueExpression<T: KslType>(override val expressionType: T) : KslExpression<T> {
    //override fun collectStateDependencies() = emptySet<KslMutatedState>()
}

class KslValueFloat1(val value: Float)
    : KslValueExpression<KslTypeFloat1>(KslTypeFloat1), KslScalarExpression<KslTypeFloat1> {
    override fun collectStateDependencies() = emptySet<KslMutatedState>()
    override fun generateExpression(generator: KslGenerator) = generator.constFloatExpression(value)
    override fun toPseudoCode() = "$value"
}
class KslValueFloat2(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>)
    : KslValueExpression<KslTypeFloat2>(KslTypeFloat2),
      KslVectorExpression<KslTypeFloat2, KslTypeFloat1> {
    constructor(x: Float, y: Float) : this(KslValueFloat1(x), KslValueFloat1(y))
    override fun collectStateDependencies() = listOf(x, y).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y)
    override fun toPseudoCode() = "vec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueFloat3(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>, val z: KslExpression<KslTypeFloat1>)
    : KslValueExpression<KslTypeFloat3>(KslTypeFloat3),
      KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    constructor(x: Float, y: Float, z: Float) : this(KslValueFloat1(x), KslValueFloat1(y), KslValueFloat1(z))
    override fun collectStateDependencies() = listOf(x, y, z).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y, z)
    override fun toPseudoCode() = "vec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueFloat4(val x: KslExpression<KslTypeFloat1>, val y: KslExpression<KslTypeFloat1>, val z: KslExpression<KslTypeFloat1>, val w: KslExpression<KslTypeFloat1>)
    : KslValueExpression<KslTypeFloat4>(KslTypeFloat4),
      KslVectorExpression<KslTypeFloat4, KslTypeFloat1> {
    constructor(x: Float, y: Float, z: Float, w: Float) : this(KslValueFloat1(x), KslValueFloat1(y), KslValueFloat1(z), KslValueFloat1(w))
    override fun collectStateDependencies() = listOf(x, y, z, w).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constFloatVecExpression(x, y, z, w)
    override fun toPseudoCode() = "vec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueInt1(val value: Int)
    : KslValueExpression<KslTypeInt1>(KslTypeInt1), KslScalarExpression<KslTypeInt1> {
    override fun collectStateDependencies() = emptySet<KslMutatedState>()
    override fun generateExpression(generator: KslGenerator) = generator.constIntExpression(value)
    override fun toPseudoCode() = "$value"
}
class KslValueInt2(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>)
    : KslValueExpression<KslTypeInt2>(KslTypeInt2), KslVectorExpression<KslTypeInt2, KslTypeInt1> {
    constructor(x: Int, y: Int) : this(KslValueInt1(x), KslValueInt1(y))
    override fun collectStateDependencies() = listOf(x, y).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y)
    override fun toPseudoCode() = "ivec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueInt3(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>, val z: KslExpression<KslTypeInt1>)
    : KslValueExpression<KslTypeInt3>(KslTypeInt3), KslVectorExpression<KslTypeInt3, KslTypeInt1> {
    constructor(x: Int, y: Int, z: Int) : this(KslValueInt1(x), KslValueInt1(y), KslValueInt1(z))
    override fun collectStateDependencies() = listOf(x, y, z).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y, z)
    override fun toPseudoCode() = "ivec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueInt4(val x: KslExpression<KslTypeInt1>, val y: KslExpression<KslTypeInt1>, val z: KslExpression<KslTypeInt1>, val w: KslExpression<KslTypeInt1>)
    : KslValueExpression<KslTypeInt4>(KslTypeInt4), KslVectorExpression<KslTypeInt4, KslTypeInt1> {
    constructor(x: Int, y: Int, z: Int, w: Int) : this(KslValueInt1(x), KslValueInt1(y), KslValueInt1(z), KslValueInt1(w))
    override fun collectStateDependencies() = listOf(x, y, z, w).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constIntVecExpression(x, y, z, w)
    override fun toPseudoCode() = "ivec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueBool1(val value: Boolean)
    : KslValueExpression<KslTypeBool1>(KslTypeBool1), KslScalarExpression<KslTypeBool1> {
    override fun collectStateDependencies() = emptySet<KslMutatedState>()
    override fun generateExpression(generator: KslGenerator) = generator.constBoolExpression(value)
    override fun toPseudoCode() = "$value"
}
class KslValueBool2(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>)
    : KslValueExpression<KslTypeBool2>(KslTypeBool2), KslVectorExpression<KslTypeBool2, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean) : this(KslValueBool1(x), KslValueBool1(y))
    override fun collectStateDependencies() = listOf(x, y).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y)
    override fun toPseudoCode() = "bvec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueBool3(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>, val z: KslExpression<KslTypeBool1>)
    : KslValueExpression<KslTypeBool3>(KslTypeBool3), KslVectorExpression<KslTypeBool3, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean, z: Boolean) : this(KslValueBool1(x), KslValueBool1(y), KslValueBool1(z))
    override fun collectStateDependencies() = listOf(x, y, z).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y, z)
    override fun toPseudoCode() = "bvec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueBool4(val x: KslExpression<KslTypeBool1>, val y: KslExpression<KslTypeBool1>, val z: KslExpression<KslTypeBool1>, val w: KslExpression<KslTypeBool1>)
    : KslValueExpression<KslTypeBool4>(KslTypeBool4), KslVectorExpression<KslTypeBool4, KslTypeBool1> {
    constructor(x: Boolean, y: Boolean, z: Boolean, w: Boolean) : this(KslValueBool1(x), KslValueBool1(y), KslValueBool1(z), KslValueBool1(w))
    override fun collectStateDependencies() = listOf(x, y, z, w).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constBoolVecExpression(x, y, z, w)
    override fun toPseudoCode() = "bvec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueMat2(val col0: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>,
                   val col1: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>)
    : KslValueExpression<KslTypeMat2>(KslTypeMat2), KslMatrixExpression<KslTypeMat2, KslTypeFloat2> {
    constructor(col0: Vec2f, col1: Vec2f) : this(KslValueFloat2(col0.x, col0.y), KslValueFloat2(col1.x, col1.y))
    override fun collectStateDependencies() = listOf(col0, col1).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constMatExpression(col0, col1)
    override fun toPseudoCode() = "mat2(${col0.toPseudoCode()}, ${col1.toPseudoCode()})"
}
class KslValueMat3(val col0: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
                   val col1: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
                   val col2: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>)
    : KslValueExpression<KslTypeMat3>(KslTypeMat3), KslMatrixExpression<KslTypeMat3, KslTypeFloat3> {
    constructor(col0: Vec3f, col1: Vec3f, col2: Vec3f) : this(KslValueFloat3(col0.x, col0.y, col0.z),
        KslValueFloat3(col1.x, col1.y, col1.z), KslValueFloat3(col2.x, col2.y, col2.z))
    override fun collectStateDependencies() = listOf(col0, col1, col2).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constMatExpression(col0, col1, col2)
    override fun toPseudoCode() = "mat3(${col0.toPseudoCode()}, ${col1.toPseudoCode()}, ${col2.toPseudoCode()})"
}
class KslValueMat4(val col0: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                   val col1: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                   val col2: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                   val col3: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>)
    : KslValueExpression<KslTypeMat4>(KslTypeMat4), KslMatrixExpression<KslTypeMat4, KslTypeFloat4> {
    constructor(col0: Vec4f, col1: Vec4f, col2: Vec4f, col3: Vec4f) : this(KslValueFloat4(col0.x, col0.y, col0.z, col0.w),
        KslValueFloat4(col1.x, col1.y, col1.z, col1.w), KslValueFloat4(col2.x, col2.y, col2.z, col2.w), KslValueFloat4(col3.x, col3.y, col3.z, col3.w))
    override fun collectStateDependencies() = listOf(col0, col1, col2, col3).flatMap { it.collectStateDependencies() }.toSet()
    override fun generateExpression(generator: KslGenerator) = generator.constMatExpression(col0, col1, col2, col3)
    override fun toPseudoCode() = "mat4(${col0.toPseudoCode()}, ${col1.toPseudoCode()}, ${col2.toPseudoCode()}, ${col3.toPseudoCode()})"
}
