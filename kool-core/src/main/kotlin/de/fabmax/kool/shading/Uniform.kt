package de.fabmax.kool.shading

import de.fabmax.kool.Texture
import de.fabmax.kool.platform.Float32Buffer
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.MutableVec2f
import de.fabmax.kool.util.MutableVec3f
import de.fabmax.kool.util.MutableVec4f


abstract class Uniform<T>(val name: String, value: T) {
    abstract val type : String

    open var value = value
        protected set

    var location: Any? = null
    val isValid: Boolean
        get() = location != null

    internal fun bind(ctx: RenderContext) {
        if (isValid) {
            doBind(ctx)
        }
    }

    protected abstract fun doBind(ctx: RenderContext)
}

class UniformTexture2D(name: String) : Uniform<Texture?>(name, null) {
    override val type = "sampler2D"
    override var value: Texture? = null
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: RenderContext) {
        val tex = value
        if (tex != null) {
            GL.uniform1i(location, ctx.textureMgr.bindTexture(tex, ctx))
        }
    }
}

class Uniform1i(name: String) : Uniform<Int>(name, 0) {
    override val type = "int"
    override var value = 0
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: RenderContext) {
        GL.uniform1i(location, value)
    }
}

class Uniform1f(name: String) : Uniform<Float>(name, 0f) {
    override val type = "float"
    override var value = 0f
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: RenderContext) {
        GL.uniform1f(location, value)
    }
}

class Uniform2f(name: String) : Uniform<MutableVec2f>(name, MutableVec2f()) {
    override val type = "vec2"

    override fun doBind(ctx: RenderContext) {
        GL.uniform2f(location, value.x, value.y)
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(name, MutableVec3f()) {
    override val type = "vec3"

    override fun doBind(ctx: RenderContext) {
        GL.uniform3f(location, value.x, value.y, value.z)
    }
}

class Uniform4f(name: String) : Uniform<MutableVec4f>(name, MutableVec4f()) {
    override val type = "vec4"

    override fun doBind(ctx: RenderContext) {
        GL.uniform4f(location, value.x, value.y, value.z, value.w)
    }
}

class UniformMatrix4(name: String) : Uniform<Float32Buffer?>(name, null) {
    override val type = "mat4"
    override var value: Float32Buffer? = null
        public set      // explicit public is needed to overwrite protected set from super

    override fun doBind(ctx: RenderContext) {
        val buf = value
        if (buf != null) {
            GL.uniformMatrix4fv(location, false, buf)
        }
    }
}
