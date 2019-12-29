package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.AttributeType

open class ModelVar(val name: String, val type: AttributeType) {
    open fun glslType(): String = type.glslType
    open fun declare(): String = "${glslType()} $name"

    fun refAsType(targetType: AttributeType) = when(targetType) {
        AttributeType.FLOAT -> ref1f()
        AttributeType.VEC_2F -> ref2f()
        AttributeType.VEC_3F -> ref3f()
        AttributeType.VEC_4F -> ref4f()
        else -> {
            if (targetType == type) { name } else { throw KoolException("$type cannot be converted to $targetType") }
        }
    }

    open fun ref1f() = when (type) {
        AttributeType.FLOAT -> name
        AttributeType.VEC_2F -> "$name.x"
        AttributeType.VEC_3F -> "$name.x"
        AttributeType.VEC_4F -> "$name.x"
        else -> throw KoolException("$type cannot be converted to float")
    }

    open fun ref2f() = when (type) {
        AttributeType.FLOAT -> "vec2($name, 0.0)"
        AttributeType.VEC_2F -> name
        AttributeType.VEC_3F -> "$name.xy"
        AttributeType.VEC_4F -> "$name.xy"
        else -> throw KoolException("$type cannot be converted to vec2")
    }

    open fun ref3f() = when (type) {
        AttributeType.FLOAT -> "vec3($name, 0.0, 0.0)"
        AttributeType.VEC_2F -> "vec3($name, 0.0)"
        AttributeType.VEC_3F -> name
        AttributeType.VEC_4F -> "$name.xyz"
        else -> throw KoolException("$type cannot be converted to vec3")
    }

    open fun ref4f() = when (type) {
        AttributeType.FLOAT -> "vec4($name, 0.0, 0.0, 0.0)"
        AttributeType.VEC_2F -> "vec4($name, 0.0, 0.0)"
        AttributeType.VEC_3F -> "vec4($name, 0.0)"
        AttributeType.VEC_4F -> name
        else -> throw KoolException("$type cannot be converted to vec4")
    }
}

open class ModelVar1f(name: String) : ModelVar(name, AttributeType.FLOAT)
open class ModelVar2f(name: String) : ModelVar(name, AttributeType.VEC_2F)
open class ModelVar3f(name: String) : ModelVar(name, AttributeType.VEC_3F)
open class ModelVar4f(name: String) : ModelVar(name, AttributeType.VEC_4F)

open class ModelVarMat2f(name: String) : ModelVar(name, AttributeType.MAT_2F)
open class ModelVarMat3f(name: String) : ModelVar(name, AttributeType.MAT_3F)
open class ModelVarMat4f(name: String) : ModelVar(name, AttributeType.MAT_4F)

class ModelVar1fConst(val value: Float) : ModelVar1f("_") {
    override fun ref1f() = "$value"
    override fun ref2f() = "vec2($value, 0.0)"
    override fun ref3f() = "vec3($value, 0.0, 0.0)"
    override fun ref4f() = "vec4($value, 0.0, 0.0, 0.0)"
}
class ModelVar2fConst(val value: Vec2f) : ModelVar2f("_") {
    override fun ref1f() = "${value.x}"
    override fun ref2f() = "vec2(${value.x}, ${value.y})"
    override fun ref3f() = "vec3(${value.x}, ${value.y}, 0.0)"
    override fun ref4f() = "vec4(${value.x}, ${value.y}, 0.0, 0.0)"
}
class ModelVar3fConst(val value: Vec3f) : ModelVar3f("_") {
    override fun ref1f() = "${value.x}"
    override fun ref2f() = "vec2(${value.x}, ${value.y})"
    override fun ref3f() = "vec3(${value.x}, ${value.y}, ${value.z})"
    override fun ref4f() = "vec4(${value.x}, ${value.y}, ${value.z}, 0.0)"
}
class ModelVar4fConst(val value: Vec4f) : ModelVar4f("_") {
    override fun ref1f() = "${value.x}"
    override fun ref2f() = "vec2(${value.x}, ${value.y})"
    override fun ref3f() = "vec3(${value.x}, ${value.y}, ${value.z})"
    override fun ref4f() = "vec4(${value.x}, ${value.y}, ${value.z}, ${value.w})"
}
