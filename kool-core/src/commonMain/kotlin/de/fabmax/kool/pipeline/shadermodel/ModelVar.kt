package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.GlslType

open class ModelVar(val name: String, val type: GlslType, val isArray: Boolean = false) {
    open fun glslType(): String = type.glslType

    open fun declare(): String {
        val arr = if (isArray) "[]" else ""
        return "${glslType()}$arr $name"
    }

    fun refAsType(targetType: GlslType, arrayIndex: String = "") = when(targetType) {
        GlslType.FLOAT -> ref1f(arrayIndex)
        GlslType.VEC_2F -> ref2f(arrayIndex)
        GlslType.VEC_3F -> ref3f(arrayIndex)
        GlslType.VEC_4F -> ref4f(arrayIndex)
        GlslType.INT -> ref1i(arrayIndex)
        GlslType.VEC_2I -> ref2i(arrayIndex)
        GlslType.VEC_3I -> ref3i(arrayIndex)
        GlslType.VEC_4I -> ref4i(arrayIndex)
        else -> {
            if (targetType == type) { name } else { throw KoolException("$type cannot be converted to $targetType") }
        }
    }

    open fun ref1f(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "$name$arr"
            GlslType.VEC_2F -> "$name$arr.x"
            GlslType.VEC_3F -> "$name$arr.x"
            GlslType.VEC_4F -> "$name$arr.x"
            GlslType.INT -> "float($name$arr)"
            GlslType.VEC_2I -> "float($name$arr.x)"
            GlslType.VEC_3I -> "float($name$arr.x)"
            GlslType.VEC_4I -> "float($name$arr.x)"
            else -> throw KoolException("$type cannot be converted to float")
        }
    }

    open fun ref2f(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "vec2($name$arr, 0.0)"
            GlslType.VEC_2F -> "$name$arr"
            GlslType.VEC_3F -> "$name$arr.xy"
            GlslType.VEC_4F -> "$name$arr.xy"
            GlslType.INT -> "vec2(float($name$arr), 0.0)"
            GlslType.VEC_2I -> "vec2(float($name$arr.x), float($name$arr.y))"
            GlslType.VEC_3I -> "vec2(float($name$arr.x), float($name$arr.y))"
            GlslType.VEC_4I -> "vec2(float($name$arr.x), float($name$arr.y))"
            else -> throw KoolException("$type cannot be converted to vec2")
        }
    }

    open fun ref3f(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "vec3($name$arr, 0.0, 0.0)"
            GlslType.VEC_2F -> "vec3($name$arr, 0.0)"
            GlslType.VEC_3F -> "$name$arr"
            GlslType.VEC_4F -> "$name$arr.xyz"
            GlslType.INT -> "vec3(float($name$arr), 0.0, 0.0)"
            GlslType.VEC_2I -> "vec3(float($name$arr.x), float($name$arr.y), 0.0)"
            GlslType.VEC_3I -> "vec3(float($name$arr.x), float($name$arr.y), float($name$arr.z))"
            GlslType.VEC_4I -> "vec3(float($name$arr.x), float($name$arr.y), float($name$arr.z))"
            else -> throw KoolException("$type cannot be converted to vec3")
        }
    }

    open fun ref4f(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "vec4($name$arr, 0.0, 0.0, 0.0)"
            GlslType.VEC_2F -> "vec4($name$arr, 0.0, 0.0)"
            GlslType.VEC_3F -> "vec4($name$arr, 0.0)"
            GlslType.VEC_4F -> "$name$arr"
            GlslType.INT -> "vec4(float($name$arr), 0.0, 0.0, 0.0)"
            GlslType.VEC_2I -> "vec4(float($name$arr.x), float($name$arr.y), 0.0, 0.0)"
            GlslType.VEC_3I -> "vec4(float($name$arr.x), float($name$arr.y), float($name$arr.z), 0.0)"
            GlslType.VEC_4I -> "vec4(float($name$arr.x), float($name$arr.y), float($name$arr.z), float($name$arr.w))"
            else -> throw KoolException("$type cannot be converted to vec4")
        }
    }

    open fun ref1i(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "int($name$arr)"
            GlslType.VEC_2F -> "int($name$arr.x)"
            GlslType.VEC_3F -> "int($name$arr.x)"
            GlslType.VEC_4F -> "int($name$arr.x)"
            GlslType.INT -> "$name$arr"
            GlslType.VEC_2I -> "$name$arr.x"
            GlslType.VEC_3I -> "$name$arr.x"
            GlslType.VEC_4I -> "$name$arr.x"
            else -> throw KoolException("$type cannot be converted to int")
        }
    }

    open fun ref2i(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "ivec2(int($name$arr), 0)"
            GlslType.VEC_2F -> "ivec2(int($name$arr.x), int($name$arr.y))"
            GlslType.VEC_3F -> "ivec2(int($name$arr.x), int($name$arr.y))"
            GlslType.VEC_4F -> "ivec2(int($name$arr.x), int($name$arr.y))"
            GlslType.INT -> "ivec2($name$arr, 0)"
            GlslType.VEC_2I -> "$name$arr"
            GlslType.VEC_3I -> "ivec2($name$arr.xy)"
            GlslType.VEC_4I -> "ivec2($name$arr.xy)"
            else -> throw KoolException("$type cannot be converted to ivec2")
        }
    }

    open fun ref3i(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "ivec3(int($name$arr), 0, 0)"
            GlslType.VEC_2F -> "ivec3(int($name$arr.x), int($name$arr.y), 0)"
            GlslType.VEC_3F -> "ivec3(int($name$arr.x), int($name$arr.y), int($name$arr.z))"
            GlslType.VEC_4F -> "ivec3(int($name$arr.x), int($name$arr.y), int($name$arr.z))"
            GlslType.INT -> "ivec3($name$arr, 0, 0)"
            GlslType.VEC_2I -> "ivec3($name$arr, 0)"
            GlslType.VEC_3I -> "$name$arr"
            GlslType.VEC_4I -> "ivec3($name$arr.xyz)"
            else -> throw KoolException("$type cannot be converted to ivec3")
        }
    }

    open fun ref4i(arrayIndex: String = "0"): String {
        val arr = if (isArray) "[$arrayIndex]" else ""
        return when (type) {
            GlslType.FLOAT -> "ivec4(int($name$arr), 0, 0, 0)"
            GlslType.VEC_2F -> "ivec4(int($name$arr.x), int($name$arr.y), 0, 0)"
            GlslType.VEC_3F -> "ivec4(int($name$arr.x), int($name$arr.x), int($name$arr.z), 0)"
            GlslType.VEC_4F -> "ivec4(int($name$arr.x), int($name$arr.x), int($name$arr.z), int($name$arr.w))"
            GlslType.INT -> "ivec4($name$arr, 0, 0, 0)"
            GlslType.VEC_2I -> "ivec4($name$arr, 0, 0)"
            GlslType.VEC_3I -> "ivec4($name$arr, 0)"
            GlslType.VEC_4I -> "$name$arr"
            else -> throw KoolException("$type cannot be converted to ivec4")
        }
    }
}

open class ModelVar1f(name: String) : ModelVar(name, GlslType.FLOAT)
open class ModelVar2f(name: String) : ModelVar(name, GlslType.VEC_2F)
open class ModelVar3f(name: String) : ModelVar(name, GlslType.VEC_3F)
open class ModelVar4f(name: String) : ModelVar(name, GlslType.VEC_4F)

open class ModelVar1fv(name: String) : ModelVar(name, GlslType.FLOAT, true)
open class ModelVar2fv(name: String) : ModelVar(name, GlslType.VEC_2F, true)
open class ModelVar3fv(name: String) : ModelVar(name, GlslType.VEC_3F, true)
open class ModelVar4fv(name: String) : ModelVar(name, GlslType.VEC_4F, true)

open class ModelVar1i(name: String) : ModelVar(name, GlslType.INT)
open class ModelVar2i(name: String) : ModelVar(name, GlslType.VEC_2I)
open class ModelVar3i(name: String) : ModelVar(name, GlslType.VEC_3I)
open class ModelVar4i(name: String) : ModelVar(name, GlslType.VEC_4I)

open class ModelVarMat2f(name: String) : ModelVar(name, GlslType.MAT_2F)
open class ModelVarMat3f(name: String) : ModelVar(name, GlslType.MAT_3F)
open class ModelVarMat4f(name: String) : ModelVar(name, GlslType.MAT_4F)

class ModelVar1fConst(val value: Float) : ModelVar1f("_") {
    override fun ref1f(arrayIndex: String) = "float($value)"
    override fun ref2f(arrayIndex: String) = "vec2(float($value), 0.0)"
    override fun ref3f(arrayIndex: String) = "vec3(float($value), 0.0, 0.0)"
    override fun ref4f(arrayIndex: String) = "vec4(float($value), 0.0, 0.0, 0.0)"

    override fun ref1i(arrayIndex: String) = "int($value)"
    override fun ref2i(arrayIndex: String) = "ivec2(int($value), 0)"
    override fun ref3i(arrayIndex: String) = "ivec3(int($value), 0, 0)"
    override fun ref4i(arrayIndex: String) = "ivec4(int($value), 0, 0, 0)"
}
class ModelVar2fConst(val value: Vec2f) : ModelVar2f("_") {
    override fun ref1f(arrayIndex: String) = "float(${value.x})"
    override fun ref2f(arrayIndex: String) = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f(arrayIndex: String) = "vec3(float(${value.x}), float(${value.y}), 0.0)"
    override fun ref4f(arrayIndex: String) = "vec4(float(${value.x}), float(${value.y}), 0.0, 0.0)"

    override fun ref1i(arrayIndex: String) = "int(${value.x})"
    override fun ref2i(arrayIndex: String) = "ivec2(int(${value.x}), int(${value.y}))"
    override fun ref3i(arrayIndex: String) = "ivec3(int(${value.x}), int(${value.y}), 0)"
    override fun ref4i(arrayIndex: String) = "ivec4(int(${value.x}), int(${value.y}), 0, 0)"
}
class ModelVar3fConst(val value: Vec3f) : ModelVar3f("_") {
    override fun ref1f(arrayIndex: String) = "float(${value.x})"
    override fun ref2f(arrayIndex: String) = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f(arrayIndex: String) = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f(arrayIndex: String) = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), 0.0)"

    override fun ref1i(arrayIndex: String) = "int(${value.x})"
    override fun ref2i(arrayIndex: String) = "ivec2(int(${value.x}), int(${value.y}))"
    override fun ref3i(arrayIndex: String) = "ivec3(int(${value.x}), int(${value.y}), int(${value.z}))"
    override fun ref4i(arrayIndex: String) = "ivec4(int(${value.x}), int(${value.y}), int(${value.z}), 0)"
}
class ModelVar4fConst(val value: Vec4f) : ModelVar4f("_") {
    override fun ref1f(arrayIndex: String) = "float(${value.x})"
    override fun ref2f(arrayIndex: String) = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f(arrayIndex: String) = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f(arrayIndex: String) = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), float(${value.w}))"

    override fun ref1i(arrayIndex: String) = "int(${value.x})"
    override fun ref2i(arrayIndex: String) = "ivec2(int(${value.x}), int(${value.y}))"
    override fun ref3i(arrayIndex: String) = "ivec3(int(${value.x}), int(${value.y}), int(${value.z}))"
    override fun ref4i(arrayIndex: String) = "ivec4(int(${value.x}), int(${value.y}), int(${value.z}), int(${value.w}))"
}

class ModelVar1iConst(val value: Int) : ModelVar1i("_") {
    override fun ref1f(arrayIndex: String) = "float($value)"
    override fun ref2f(arrayIndex: String) = "vec2(float($value), 0.0)"
    override fun ref3f(arrayIndex: String) = "vec3(float($value), 0.0, 0.0)"
    override fun ref4f(arrayIndex: String) = "vec4(float($value), 0.0, 0.0, 0.0)"

    override fun ref1i(arrayIndex: String) = "$value"
    override fun ref2i(arrayIndex: String) = "ivec2($value, 0)"
    override fun ref3i(arrayIndex: String) = "ivec3($value, 0, 0)"
    override fun ref4i(arrayIndex: String) = "ivec4($value, 0, 0, 0)"
}
class ModelVar2iConst(val value: Vec2i) : ModelVar2i("_") {
    override fun ref1f(arrayIndex: String) = "float(${value.x})"
    override fun ref2f(arrayIndex: String) = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f(arrayIndex: String) = "vec3(float(${value.x}), float(${value.y}), 0.0)"
    override fun ref4f(arrayIndex: String) = "vec4(float(${value.x}), float(${value.y}), 0.0, 0.0)"

    override fun ref1i(arrayIndex: String) = "int(${value.x})"
    override fun ref2i(arrayIndex: String) = "ivec2(${value.x}, ${value.y})"
    override fun ref3i(arrayIndex: String) = "ivec3(${value.x}, ${value.y}, 0)"
    override fun ref4i(arrayIndex: String) = "ivec4(${value.x}, ${value.y}, 0, 0)"
}
class ModelVar3iConst(val value: Vec3i) : ModelVar3i("_") {
    override fun ref1f(arrayIndex: String) = "float(${value.x})"
    override fun ref2f(arrayIndex: String) = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f(arrayIndex: String) = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f(arrayIndex: String) = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), 0.0)"

    override fun ref1i(arrayIndex: String) = "int(${value.x})"
    override fun ref2i(arrayIndex: String) = "ivec2(${value.x}, ${value.y})"
    override fun ref3i(arrayIndex: String) = "ivec3(${value.x}, ${value.y}, ${value.z})"
    override fun ref4i(arrayIndex: String) = "ivec4(${value.x}, ${value.y}, ${value.z}, 0)"
}
class ModelVar4iConst(val value: Vec4i) : ModelVar4i("_") {
    override fun ref1f(arrayIndex: String) = "float(${value.x})"
    override fun ref2f(arrayIndex: String) = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f(arrayIndex: String) = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f(arrayIndex: String) = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), float(${value.w}))"

    override fun ref1i(arrayIndex: String) = "int(${value.x})"
    override fun ref2i(arrayIndex: String) = "ivec2(${value.x}, ${value.y})"
    override fun ref3i(arrayIndex: String) = "ivec3(${value.x}, ${value.y}, ${value.z})"
    override fun ref4i(arrayIndex: String) = "ivec4(${value.x}, ${value.y}, ${value.z}, ${value.w})"
}
