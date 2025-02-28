package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f

sealed class KslValueExpression<T: KslType>(override val expressionType: T) : KslExpression<T>

class KslValueFloat1(val value: Float) : KslValueExpression<KslFloat1>(KslFloat1), KslScalarExpression<KslFloat1> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive()
    override fun toPseudoCode() = "$value"
}
class KslValueFloat2(val x: KslExpression<KslFloat1>, val y: KslExpression<KslFloat1>) :
    KslValueExpression<KslFloat2>(KslFloat2), KslVectorExpression<KslFloat2, KslFloat1>
{
    constructor(x: Float, y: Float) : this(KslValueFloat1(x), KslValueFloat1(y))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y)
    override fun toPseudoCode() = "vec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueFloat3(val x: KslExpression<KslFloat1>, val y: KslExpression<KslFloat1>, val z: KslExpression<KslFloat1>) :
    KslValueExpression<KslFloat3>(KslFloat3), KslVectorExpression<KslFloat3, KslFloat1>
{
    constructor(x: Float, y: Float, z: Float) : this(KslValueFloat1(x), KslValueFloat1(y), KslValueFloat1(z))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z)
    override fun toPseudoCode() = "vec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueFloat4(val x: KslExpression<KslFloat1>, val y: KslExpression<KslFloat1>, val z: KslExpression<KslFloat1>, val w: KslExpression<KslFloat1>) :
    KslValueExpression<KslFloat4>(KslFloat4), KslVectorExpression<KslFloat4, KslFloat1>
{
    constructor(x: Float, y: Float, z: Float, w: Float) : this(KslValueFloat1(x), KslValueFloat1(y), KslValueFloat1(z), KslValueFloat1(w))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z, w)
    override fun toPseudoCode() = "vec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueInt1(val value: Int) : KslValueExpression<KslInt1>(KslInt1), KslScalarExpression<KslInt1> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive()
    override fun toPseudoCode() = "$value"
}
class KslValueInt2(val x: KslExpression<KslInt1>, val y: KslExpression<KslInt1>) :
    KslValueExpression<KslInt2>(KslInt2), KslVectorExpression<KslInt2, KslInt1>
{
    constructor(x: Int, y: Int) : this(KslValueInt1(x), KslValueInt1(y))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y)
    override fun toPseudoCode() = "ivec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueInt3(val x: KslExpression<KslInt1>, val y: KslExpression<KslInt1>, val z: KslExpression<KslInt1>) :
    KslValueExpression<KslInt3>(KslInt3), KslVectorExpression<KslInt3, KslInt1>
{
    constructor(x: Int, y: Int, z: Int) : this(KslValueInt1(x), KslValueInt1(y), KslValueInt1(z))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z)
    override fun toPseudoCode() = "ivec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueInt4(val x: KslExpression<KslInt1>, val y: KslExpression<KslInt1>, val z: KslExpression<KslInt1>, val w: KslExpression<KslInt1>) :
    KslValueExpression<KslInt4>(KslInt4), KslVectorExpression<KslInt4, KslInt1>
{
    constructor(x: Int, y: Int, z: Int, w: Int) : this(KslValueInt1(x), KslValueInt1(y), KslValueInt1(z), KslValueInt1(w))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z, w)
    override fun toPseudoCode() = "ivec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueUint1(val value: UInt) : KslValueExpression<KslUint1>(KslUint1), KslScalarExpression<KslUint1> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive()
    override fun toPseudoCode() = "$value"
}
class KslValueUint2(val x: KslExpression<KslUint1>, val y: KslExpression<KslUint1>) :
    KslValueExpression<KslUint2>(KslUint2), KslVectorExpression<KslUint2, KslUint1>
{
    constructor(x: UInt, y: UInt) : this(KslValueUint1(x), KslValueUint1(y))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y)
    override fun toPseudoCode() = "uvec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueUint3(val x: KslExpression<KslUint1>, val y: KslExpression<KslUint1>, val z: KslExpression<KslUint1>) :
    KslValueExpression<KslUint3>(KslUint3), KslVectorExpression<KslUint3, KslUint1>
{
    constructor(x: UInt, y: UInt, z: UInt) : this(KslValueUint1(x), KslValueUint1(y), KslValueUint1(z))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z)
    override fun toPseudoCode() = "uvec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueUint4(val x: KslExpression<KslUint1>, val y: KslExpression<KslUint1>, val z: KslExpression<KslUint1>, val w: KslExpression<KslUint1>) :
    KslValueExpression<KslUint4>(KslUint4), KslVectorExpression<KslUint4, KslUint1>
{
    constructor(x: UInt, y: UInt, z: UInt, w: UInt) : this(KslValueUint1(x), KslValueUint1(y), KslValueUint1(z), KslValueUint1(w))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z, w)
    override fun toPseudoCode() = "uvec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueBool1(val value: Boolean) : KslValueExpression<KslBool1>(KslBool1), KslScalarExpression<KslBool1> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive()
    override fun toPseudoCode() = "$value"
}
class KslValueBool2(val x: KslExpression<KslBool1>, val y: KslExpression<KslBool1>) :
    KslValueExpression<KslBool2>(KslBool2), KslVectorExpression<KslBool2, KslBool1>
{
    constructor(x: Boolean, y: Boolean) : this(KslValueBool1(x), KslValueBool1(y))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y)
    override fun toPseudoCode() = "bvec2(${x.toPseudoCode()}, ${y.toPseudoCode()})"
}
class KslValueBool3(val x: KslExpression<KslBool1>, val y: KslExpression<KslBool1>, val z: KslExpression<KslBool1>) :
    KslValueExpression<KslBool3>(KslBool3), KslVectorExpression<KslBool3, KslBool1>
{
    constructor(x: Boolean, y: Boolean, z: Boolean) : this(KslValueBool1(x), KslValueBool1(y), KslValueBool1(z))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z)
    override fun toPseudoCode() = "bvec3(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()})"
}
class KslValueBool4(val x: KslExpression<KslBool1>, val y: KslExpression<KslBool1>, val z: KslExpression<KslBool1>, val w: KslExpression<KslBool1>) :
    KslValueExpression<KslBool4>(KslBool4), KslVectorExpression<KslBool4, KslBool1>
{
    constructor(x: Boolean, y: Boolean, z: Boolean, w: Boolean) : this(KslValueBool1(x), KslValueBool1(y), KslValueBool1(z), KslValueBool1(w))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(x, y, z, w)
    override fun toPseudoCode() = "bvec4(${x.toPseudoCode()}, ${y.toPseudoCode()}, ${z.toPseudoCode()}, ${w.toPseudoCode()})"
}

class KslValueMat2(
    val col0: KslVectorExpression<KslFloat2, KslFloat1>,
    val col1: KslVectorExpression<KslFloat2, KslFloat1>
) : KslValueExpression<KslMat2>(KslMat2), KslMatrixExpression<KslMat2, KslFloat2> {
    constructor(col0: Vec2f, col1: Vec2f) : this(KslValueFloat2(col0.x, col0.y), KslValueFloat2(col1.x, col1.y))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(col0, col1)
    override fun toPseudoCode() = "mat2(${col0.toPseudoCode()}, ${col1.toPseudoCode()})"
}
class KslValueMat3(
    val col0: KslVectorExpression<KslFloat3, KslFloat1>,
    val col1: KslVectorExpression<KslFloat3, KslFloat1>,
    val col2: KslVectorExpression<KslFloat3, KslFloat1>
) : KslValueExpression<KslMat3>(KslMat3), KslMatrixExpression<KslMat3, KslFloat3> {
    constructor(col0: Vec3f, col1: Vec3f, col2: Vec3f) : this(KslValueFloat3(col0.x, col0.y, col0.z),
        KslValueFloat3(col1.x, col1.y, col1.z), KslValueFloat3(col2.x, col2.y, col2.z))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(col0, col1, col2)
    override fun toPseudoCode() = "mat3(${col0.toPseudoCode()}, ${col1.toPseudoCode()}, ${col2.toPseudoCode()})"
}
class KslValueMat4(
    val col0: KslVectorExpression<KslFloat4, KslFloat1>,
    val col1: KslVectorExpression<KslFloat4, KslFloat1>,
    val col2: KslVectorExpression<KslFloat4, KslFloat1>,
    val col3: KslVectorExpression<KslFloat4, KslFloat1>
) : KslValueExpression<KslMat4>(KslMat4), KslMatrixExpression<KslMat4, KslFloat4> {
    constructor(col0: Vec4f, col1: Vec4f, col2: Vec4f, col3: Vec4f) : this(KslValueFloat4(col0.x, col0.y, col0.z, col0.w),
        KslValueFloat4(col1.x, col1.y, col1.z, col1.w), KslValueFloat4(col2.x, col2.y, col2.z, col2.w), KslValueFloat4(col3.x, col3.y, col3.z, col3.w))
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(col0, col1, col2, col3)
    override fun toPseudoCode() = "mat4(${col0.toPseudoCode()}, ${col1.toPseudoCode()}, ${col2.toPseudoCode()}, ${col3.toPseudoCode()})"
}

object KslValueVoid : KslValueExpression<KslTypeVoid>(KslTypeVoid) {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive()
    override fun toPseudoCode(): String = ""
}