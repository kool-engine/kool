package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f

class Vec2fView(private val buf: Float32Buffer, var offset: Int) : MutableVec2f() {
    override operator fun get(i: Int): Float = buf[offset + i]
    override operator fun set(i: Int, v: Float) {
        buf[offset + i] = v
    }
}

class Vec3fView(private val buf: Float32Buffer, var offset: Int) : MutableVec3f() {
    override operator fun get(i: Int): Float = buf[offset + i]
    override operator fun set(i: Int, v: Float) {
        buf[offset + i] = v
    }
}

class Vec4fView(private val buf: Float32Buffer, var offset: Int) : MutableVec4f() {
    override operator fun get(i: Int): Float = buf[offset + i]
    override operator fun set(i: Int, v: Float) {
        buf[offset + i] = v
    }
}