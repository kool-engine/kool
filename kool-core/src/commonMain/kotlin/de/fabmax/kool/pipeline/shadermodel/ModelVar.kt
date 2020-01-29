package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
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
        else -> throw KoolException("$type cannot be converted to float")
    }

    open fun ref2f() = when (type) {
        GlslType.FLOAT -> "vec2($name, 0.0)"
        GlslType.VEC_2F -> name
        GlslType.VEC_3F -> "$name.xy"
        GlslType.VEC_4F -> "$name.xy"
        else -> throw KoolException("$type cannot be converted to vec2")
    }

    open fun ref3f() = when (type) {
        GlslType.FLOAT -> "vec3($name, 0.0, 0.0)"
        GlslType.VEC_2F -> "vec3($name, 0.0)"
        GlslType.VEC_3F -> name
        GlslType.VEC_4F -> "$name.xyz"
        else -> throw KoolException("$type cannot be converted to vec3")
    }

    open fun ref4f() = when (type) {
        GlslType.FLOAT -> "vec4($name, 0.0, 0.0, 0.0)"
        GlslType.VEC_2F -> "vec4($name, 0.0, 0.0)"
        GlslType.VEC_3F -> "vec4($name, 0.0)"
        GlslType.VEC_4F -> name
        else -> throw KoolException("$type cannot be converted to vec4")
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
}
class ModelVar2fConst(val value: Vec2f) : ModelVar2f("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), 0.0)"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), 0.0, 0.0)"
}
class ModelVar3fConst(val value: Vec3f) : ModelVar3f("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), 0.0)"
}
class ModelVar4fConst(val value: Vec4f) : ModelVar4f("_") {
    override fun ref1f() = "float(${value.x})"
    override fun ref2f() = "vec2(float(${value.x}), float(${value.y}))"
    override fun ref3f() = "vec3(float(${value.x}), float(${value.y}), float(${value.z}))"
    override fun ref4f() = "vec4(float(${value.x}), float(${value.y}), float(${value.z}), float(${value.w}))"
}
