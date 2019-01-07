package de.fabmax.kool.shading

import de.fabmax.kool.CubeMapTexture
import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.gl.*
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.util.*


abstract class Uniform<T>(val name: String, value: T) {
    abstract val type : String

    open var value = value
        protected set

    var location: Any? = null
    val isValid: Boolean
        get() = isValidUniformLocation(location)

    fun bind(ctx: KoolContext) {
        if (isValid) {
            doBind(ctx)
        }
    }

    protected abstract fun doBind(ctx: KoolContext)
}

class UniformTexture2D(name: String) : Uniform<Texture?>(name, null) {
    override val type = "sampler2D"
    override var value: Texture? = null
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: KoolContext) {
        val tex = value
        if (tex != null) {
            val unit = ctx.textureMgr.bindTexture(tex, ctx)
            if (tex.isValid && tex.res!!.isLoaded) {
                glUniform1i(location, unit)
            }
        } else {
            glUniform1i(location, GL_NONE)
        }
    }
}

class UniformTexture2Dv(name: String, size: Int) : Uniform<Array<Texture?>>(name, Array(size) { null }) {
    override val type = "sampler2D"
    private val texNames = IntArray(size)

    override fun doBind(ctx: KoolContext) {
        for (i in value.indices) {
            val tex = value[i]
            texNames[i] = if (tex != null) {
                ctx.textureMgr.bindTexture(tex, ctx)
            } else {
                GL_NONE
            }
        }
        glUniform1iv(location, texNames)
    }
}

class UniformTextureCubeMap(name: String) : Uniform<CubeMapTexture?>(name, null) {
    override val type = "samplerCube"
    override var value: CubeMapTexture? = null
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: KoolContext) {
        val tex = value
        if (tex != null) {
            val unit = ctx.textureMgr.bindTexture(tex, ctx)
            if (tex.isValid && tex.res!!.isLoaded) {
                glUniform1i(location, unit)
            }
        } else {
            glUniform1i(location, GL_NONE)
        }
    }
}

class Uniform1i(name: String) : Uniform<IntArray>(name, IntArray(1)) {
    override val type = "int"

    override fun doBind(ctx: KoolContext) {
        glUniform1i(location, value[0])
    }
}

class Uniform1iv(name: String) : Uniform<IntArray>(name, IntArray(0)) {
    override val type = "int"

    fun setSize(size: Int) {
        if (size != value.size) {
            value = IntArray(size)
        }
    }

    override fun doBind(ctx: KoolContext) {
        glUniform1iv(location, value)
    }
}

class Uniform1f(name: String) : Uniform<FloatArray>(name, FloatArray(1)) {
    override val type = "float"

    override fun doBind(ctx: KoolContext) {
        glUniform1f(location, value[0])
    }
}

class Uniform1fv(name: String) : Uniform<FloatArray>(name, FloatArray(0)) {
    override val type = "float"

    fun setSize(size: Int) {
        if (size != value.size) {
            value = FloatArray(size)
        }
    }

    override fun doBind(ctx: KoolContext) {
        glUniform1fv(location, value)
    }
}

class Uniform2f(name: String) : Uniform<MutableVec2f>(name, MutableVec2f()) {
    override val type = "vec2"

    override fun doBind(ctx: KoolContext) {
        glUniform2f(location, value.x, value.y)
    }
}

class Uniform2fv(name: String) : Uniform<List<MutableVec2f>>(name, emptyList()) {
    override val type: String
        get() = "vec2[${value.size}]"

    private var buffer: Float32Buffer? = null

    constructor(name: String, size: Int) : this(name) {
        setSize(size)
    }

    fun setSize(size: Int) {
        if (size != value.size) {
            buffer = createFloat32Buffer(size * 2)
            value = List(size) { Vec2fView(buffer!!, it * 2) }
        }
    }

    override fun doBind(ctx: KoolContext) {
        val buf = buffer
        if (buf != null) {
            glUniform4fv(location, buf)
        }
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(name, MutableVec3f()) {
    override val type = "vec3"

    override fun doBind(ctx: KoolContext) {
        glUniform3f(location, value.x, value.y, value.z)
    }
}

class Uniform3fv(name: String) : Uniform<List<MutableVec3f>>(name, emptyList()) {
    override val type: String
        get() = "vec3[${value.size}]"

    private var buffer: Float32Buffer? = null

    constructor(name: String, size: Int) : this(name) {
        setSize(size)
    }

    fun setSize(size: Int) {
        if (size != value.size) {
            buffer = createFloat32Buffer(size * 3)
            value = List(size) { Vec3fView(buffer!!, it * 3) }
        }
    }

    override fun doBind(ctx: KoolContext) {
        val buf = buffer
        if (buf != null) {
            glUniform4fv(location, buf)
        }
    }
}

class Uniform4f(name: String) : Uniform<MutableVec4f>(name, MutableVec4f()) {
    override val type = "vec4"

    override fun doBind(ctx: KoolContext) {
        glUniform4f(location, value.x, value.y, value.z, value.w)
    }
}

class Uniform4fv(name: String) : Uniform<List<MutableVec4f>>(name, emptyList()) {
    override val type: String
        get() = "vec4[${value.size}]"

    private var buffer: Float32Buffer? = null

    constructor(name: String, size: Int) : this(name) {
        setSize(size)
    }

    fun setSize(size: Int) {
        if (size != value.size) {
            buffer = createFloat32Buffer(size * 4)
            value = List(size) { Vec4fView(buffer!!, it * 4) }
        }
    }

    override fun doBind(ctx: KoolContext) {
        val buf = buffer
        if (buf != null) {
            glUniform4fv(location, buf)
        }
    }
}

class UniformMatrix4(name: String) : Uniform<Float32Buffer?>(name, null) {
    override val type = "mat4"
    override var value: Float32Buffer? = null
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: KoolContext) {
        val buf = value
        if (buf != null) {
            glUniformMatrix4fv(location, false, buf)
        }
    }
}