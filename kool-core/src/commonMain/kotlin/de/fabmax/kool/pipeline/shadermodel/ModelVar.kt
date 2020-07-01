package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.GlslType

open class ModelVar(val name: String, val type: GlslType) {
    open fun glslType(): String = type.glslType
    open fun declare(): String = "${glslType()} $name"

    fun refAsType(targetType: GlslType) = when(targetType) {
        GlslType.FLOAT -> ref1f()
        GlslType.VEC_2F -> ref2f()
        GlslType.VEC_3F -> ref3f()
        GlslType.VEC_4F -> ref4f()
        else -> {
            if (targetType == type) { name } else { throw KoolException("$type cannot be converted to $targetType") }
        }
    }

    open fun ref1f() = when (type) {
        GlslType.FLOAT -> name
        GlslType.VEC_2F -> "$name.x"
        GlslType.VEC_3F -> "$name.x"
        GlslType.VEC_4F -> "$name.x"
        GlslType.INT -> "float($name)"
        GlslType.VEC_2I -> "float($name.x)"
        GlslType.VEC_3I -> "float($name.x)"
        GlslType.VEC_4I -> "float($name.x)"
        else -> throw KoolException("$type cannot be converted to float")
    }

    open fun ref2f() = when (type) {
        GlslType.FLOAT -> "vec2($name, 0.0)"
        GlslType.VEC_2F -> name
        GlslType.VEC_3F -> "$name.xy"
        GlslType.VEC_4F -> "$name.xy"
        GlslType.INT -> "vec2(float($name), 0.0)"
        GlslType.VEC_2I -> "vec2(float($name.x), float($name.y))"
        GlslType.VEC_3I -> "vec2(float($name.x), float($name.y))"
        GlslType.VEC_4I -> "vec2(float($name.x), float($name.y))"
        else -> throw KoolException("$type cannot be converted to vec2")
    }

    open fun ref3f() = when (type) {
        GlslType.FLOAT -> "vec3($name, 0.0, 0.0)"
        GlslType.VEC_2F -> "vec3($name, 0.0)"
        GlslType.VEC_3F -> name
        GlslType.VEC_4F -> "$name.xyz"
        GlslType.INT -> "vec3(float($name), 0.0, 0.0)"
        GlslType.VEC_2I -> "vec3(float($name.x), float($name.y), 0.0)"
        GlslType.VEC_3I -> "vec3(float($name.x), float($name.y), float($name.z))"
        GlslType.VEC_4I -> "vec3(float($name.x), float($name.y), float($name.z))"
        else -> throw KoolException("$type cannot be converted to vec3")
    }

    open fun ref4f() = when (type) {
        GlslType.FLOAT -> "vec4($name, 0.0, 0.0, 0.0)"
        GlslType.VEC_2F -> "vec4($name, 0.0, 0.0)"
        GlslType.VEC_3F -> "vec4($name, 0.0)"
        GlslType.VEC_4F -> name
        GlslType.INT -> "vec4(float($name), 0.0, 0.0, 0.0)"
        GlslType.VEC_2I -> "vec4(float($name.x), float($name.y), 0.0, 0.0)"
        GlslType.VEC_3I -> "vec4(float($name.x), float($name.y), float($name.z), 0.0)"
        GlslType.VEC_4I -> "vec4(float($name.x), float($name.y), float($name.z), float($name.w))"
        else -> throw KoolException("$type cannot be converted to vec4")
    }

    open fun ref1i() = when (type) {
        GlslType.FLOAT -> "int(name)"
        GlslType.VEC_2F -> "int($name.x)"
        GlslType.VEC_3F -> "int($name.x)"
        GlslType.VEC_4F -> "int($name.x)"
        GlslType.INT -> name
        GlslType.VEC_2I -> "$name.x"
        GlslType.VEC_3I -> "$name.x"
        GlslType.VEC_4I -> "$name.x"
        else -> throw KoolException("$type cannot be converted to int")
    }

    open fun ref2i() = when (type) {
        GlslType.FLOAT -> "ivec2(int($name), 0)"
        GlslType.VEC_2F -> "ivec2(int($name.x), int($name.y))"
        GlslType.VEC_3F -> "ivec2(int($name.x), int($name.y))"
        GlslType.VEC_4F -> "ivec2(int($name.x), int($name.y))"
        GlslType.INT -> "ivec2($name, 0)"
        GlslType.VEC_2I -> name
        GlslType.VEC_3I -> "ivec2($name.xy)"
        GlslType.VEC_4I -> "ivec2($name.xy)"
        else -> throw KoolException("$type cannot be converted to ivec2")
    }

    open fun ref3i() = when (type) {
        GlslType.FLOAT -> "ivec3(int($name), 0, 0)"
        GlslType.VEC_2F -> "ivec3(int($name.x), int($name.y), 0)"
        GlslType.VEC_3F -> "ivec3(int($name.x), int($name.y), int($name.z))"
        GlslType.VEC_4F -> "ivec3(int($name.x), int($name.y), int($name.z))"
        GlslType.INT -> "ivec3($name, 0, 0)"
        GlslType.VEC_2I -> "ivec3($name, 0)"
        GlslType.VEC_3I -> name
        GlslType.VEC_4I -> "ivec3($name.xyz)"
        else -> throw KoolException("$type cannot be converted to ivec3")
    }

    open fun ref4i() = when (type) {
        GlslType.FLOAT -> "ivec4(int($name), 0, 0, 0)"
        GlslType.VEC_2F -> "ivec4(int($name.x), int($name.y), 0, 0)"
        GlslType.VEC_3F -> "ivec4(int($name.x), int($name.x), int($name.z), 0)"
        GlslType.VEC_4F -> "ivec4(int($name.x), int($name.x), int($name.z), int($name.w))"
        GlslType.INT -> "ivec4($name, 0, 0, 0)"
        GlslType.VEC_2I -> "ivec4($name, 0, 0)"
        GlslType.VEC_3I -> "ivec4($name, 0)"
        GlslType.VEC_4I -> name
        else -> throw KoolException("$type cannot be converted to ivec4")
    }
}

open class ModelVar1f(name: String) : ModelVar(name, GlslType.FLOAT)
open class ModelVar2f(name: String) : ModelVar(name, GlslType.VEC_2F)
open class ModelVar3f(name: String) : ModelVar(name, GlslType.VEC_3F)
open class ModelVar4f(name: String) : ModelVar(name, GlslType.VEC_4F)

open class ModelVar1i(name: String) : ModelVar(name, GlslType.INT)
open class ModelVar2i(name: String) : ModelVar(name, GlslType.VEC_2I)
open class ModelVar3i(name: String) : ModelVar(name, GlslType.VEC_3I)
open class ModelVar4i(name: String) : ModelVar(name, GlslType.VEC_4I)

open class ModelVarMat2f(name: String) : ModelVar(name, GlslType.MAT_2F)
open class ModelVarMat3f(name: String) : ModelVar(name, GlslType.MAT_3F)
open class ModelVarMat4f(name: String) : ModelVar(name, GlslType.MAT_4F)

class ModelVar1fConst(val value: Float) : ModelVar1f("_") {
    override fun ref1f() = "float($value)"
    override fun ref2f() = "vec2(float($value), 0.0)"
    override fun ref3f() = "vec3(float($value), 0.0, 0.0)"
    override fun ref4f() = "vec4(float($value), 0.0, 0.0, 0.0)"

    override fun ref1i() = "int($value)"
    override fun ref2i() = "ivec2(int($value), 0)"
    override fun ref3i() = "ivec3(int($value), 0, 0)"
    override fun ref4i() = "ivec4(int($value), 0, 0, 0)"
}
class ModelVar2fConst(val value: Vec2f) : ModelVar2f("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), 0.0)"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), 0.0, 0.0)"

    override fun ref1i() = "int(${value.x})"
    override fun ref2i() = "ivec2(int(${value.x}), int(${value.y}))"
    override fun ref3i() = "ivec3(int(${value.x}), int(${value.y}), 0)"
    override fun ref4i() = "ivec4(int(${value.x}), int(${value.y}), 0, 0)"
}
class ModelVar3fConst(val value: Vec3f) : ModelVar3f("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), 0.0)"

    override fun ref1i() = "int(${value.x})"
    override fun ref2i() = "ivec2(int(${value.x}), int(${value.y}))"
    override fun ref3i() = "ivec3(int(${value.x}), int(${value.y}), int(${value.z}))"
    override fun ref4i() = "ivec4(int(${value.x}), int(${value.y}), int(${value.z}), 0)"
}
class ModelVar4fConst(val value: Vec4f) : ModelVar4f("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), float(${value.w}))"

    override fun ref1i() = "int(${value.x})"
    override fun ref2i() = "ivec2(int(${value.x}), int(${value.y}))"
    override fun ref3i() = "ivec3(int(${value.x}), int(${value.y}), int(${value.z}))"
    override fun ref4i() = "ivec4(int(${value.x}), int(${value.y}), int(${value.z}), int(${value.w}))"
}

class ModelVar1iConst(val value: Int) : ModelVar1i("_") {
    override fun ref1f() = "float($value)"
    override fun ref2f() = "vec2(float($value), 0.0)"
    override fun ref3f() = "vec3(float($value), 0.0, 0.0)"
    override fun ref4f() = "vec4(float($value), 0.0, 0.0, 0.0)"

    override fun ref1i() = "$value"
    override fun ref2i() = "ivec2($value, 0)"
    override fun ref3i() = "ivec3($value, 0, 0)"
    override fun ref4i() = "ivec4($value, 0, 0, 0)"
}
class ModelVar2iConst(val value: Vec2i) : ModelVar2i("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), 0.0)"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), 0.0, 0.0)"

    override fun ref1i() = "int(${value.x})"
    override fun ref2i() = "ivec2(${value.x}, ${value.y})"
    override fun ref3i() = "ivec3(${value.x}, ${value.y}, 0)"
    override fun ref4i() = "ivec4(${value.x}, ${value.y}, 0, 0)"
}
class ModelVar3iConst(val value: Vec3i) : ModelVar3i("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), 0.0)"

    override fun ref1i() = "int(${value.x})"
    override fun ref2i() = "ivec2(${value.x}, ${value.y})"
    override fun ref3i() = "ivec3(${value.x}, ${value.y}, ${value.z})"
    override fun ref4i() = "ivec4(${value.x}, ${value.y}, ${value.z}, 0)"
}
class ModelVar4iConst(val value: Vec4i) : ModelVar4i("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), float(${value.w}))"

    override fun ref1i() = "int(${value.x})"
    override fun ref2i() = "ivec2(${value.x}, ${value.y})"
    override fun ref3i() = "ivec3(${value.x}, ${value.y}, ${value.z})"
    override fun ref4i() = "ivec4(${value.x}, ${value.y}, ${value.z}, ${value.w})"
}
