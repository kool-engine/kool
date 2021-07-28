package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.util.Color

abstract class ShaderInput<T: ShaderNode>(val uniformName: String) {
    var node: T? = null
        protected set

    open fun connect(model: ShaderModel) {
        @Suppress("UNCHECKED_CAST")
        node = model.findNodeByName(uniformName) as? T
        onConnect()
    }

    protected abstract fun onConnect()

    override fun toString(): String {
        return uniformName
    }
}

class Texture1dInput(uniformName: String, initial: Texture1d? = null) : ShaderInput<Texture1dNode>(uniformName) {
    var texture: Texture1d? = initial
        set(value) {
            field = value
            node?.sampler?.texture = value
        }

    fun dispose() {
        texture?.dispose()
    }

    operator fun invoke(texture: Texture1d?) {
        this.texture = texture
    }

    override fun onConnect() {
        node?.sampler?.texture = texture
    }
}

class Texture2dInput(uniformName: String, initial: Texture2d? = null) : ShaderInput<Texture2dNode>(uniformName) {
    var texture: Texture2d? = initial
        set(value) {
            field = value
            node?.sampler?.texture = value
        }

    fun dispose() {
        texture?.dispose()
    }

    operator fun invoke(texture: Texture2d?) {
        this.texture = texture
    }

    override fun onConnect() {
        node?.sampler?.texture = texture
    }
}

class Texture3dInput(uniformName: String, initial: Texture3d? = null) : ShaderInput<Texture3dNode>(uniformName) {
    var texture: Texture3d? = initial
        set(value) {
            field = value
            node?.sampler?.texture = value
        }

    fun dispose() {
        texture?.dispose()
    }

    operator fun invoke(texture: Texture3d?) {
        this.texture = texture
    }

    override fun onConnect() {
        node?.sampler?.texture = texture
    }
}

class TextureCubeInput(uniformName: String, initial: TextureCube? = null) : ShaderInput<TextureCubeNode>(uniformName) {
    var texture: TextureCube? = initial
        set(value) {
            field = value
            node?.sampler?.texture = value
        }

    fun dispose() {
        texture?.dispose()
    }

    operator fun invoke(texture: TextureCube?) {
        this.texture = texture
    }

    override fun onConnect() {
        node?.sampler?.texture = texture
    }
}

class FloatInput(uniformName: String, initial: Float = 0f) : ShaderInput<PushConstantNode<Uniform1f>>(uniformName) {
    var value = initial
        set(value) {
            field = value
            node?.uniform?.value = value
        }

    operator fun invoke(value: Float) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value = value
    }
}

class Vec2fInput(uniformName: String, initial: Vec2f = Vec2f.ZERO) : ShaderInput<PushConstantNode<Uniform2f>>(uniformName) {
    var value: Vec2f = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Vec2f) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class Vec3fInput(uniformName: String, initial: Vec3f = Vec3f.ZERO) : ShaderInput<PushConstantNode<Uniform3f>>(uniformName) {
    var value: Vec3f = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Vec3f) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class Vec4fInput(uniformName: String, initial: Vec4f = Vec4f.ZERO) : ShaderInput<PushConstantNode<Uniform4f>>(uniformName) {
    var value: Vec4f = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Vec4f) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class ColorInput(uniformName: String, initial: Color = Color.BLACK) : ShaderInput<PushConstantNode<UniformColor>>(uniformName) {
    var value: Color = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Color) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class IntInput(uniformName: String, initial: Int = 0) : ShaderInput<PushConstantNode<Uniform1i>>(uniformName) {
    var value = initial
        set(value) {
            field = value
            node?.uniform?.value = value
        }

    operator fun invoke(value: Int) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value = value
    }
}

class Vec2iInput(uniformName: String, initial: Vec2i = Vec2i.ZERO) : ShaderInput<PushConstantNode<Uniform2i>>(uniformName) {
    var value: Vec2i = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Vec2i) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class Vec3iInput(uniformName: String, initial: Vec3i = Vec3i.ZERO) : ShaderInput<PushConstantNode<Uniform3i>>(uniformName) {
    var value: Vec3i = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Vec3i) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class Vec4iInput(uniformName: String, initial: Vec4i = Vec4i.ZERO) : ShaderInput<PushConstantNode<Uniform4i>>(uniformName) {
    var value: Vec4i = initial
        set(value) {
            field = value
            node?.uniform?.value?.set(value)
        }

    operator fun invoke(value: Vec4i) {
        this.value = value
    }

    override fun onConnect() {
        node?.uniform?.value?.set(value)
    }
}

class Mat3fInput(uniformName: String, initial: Mat3f? = null) : ShaderInput<UniformMat3fNode>(uniformName) {
    private val backupField = Mat3f().apply { initial?.let { set(it) } }

    val value: Mat3f
        get() = node?.uniform?.value ?: backupField

    operator fun invoke(value: Mat3f) {
        this.value.set(value)
    }

    override fun onConnect() {
        node?.uniform?.value?.set(backupField)
    }
}

class Mat4fInput(uniformName: String, initial: Mat4f? = null) : ShaderInput<UniformMat4fNode>(uniformName) {
    private val backupField = Mat4f().apply { initial?.let { set(it) } }

    val value: Mat4f
        get() = node?.uniform?.value ?: backupField

    operator fun invoke(value: Mat4f) {
        this.value.set(value)
    }

    override fun onConnect() {
        node?.uniform?.value?.set(backupField)
    }
}
