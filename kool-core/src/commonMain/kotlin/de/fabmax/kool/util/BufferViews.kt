package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f

class Vec2fView(private val buf: Float32Buffer, var offset: Int) : MutableVec2f() {
    override var x
        get() = buf[offset]
        set(value) { buf[offset] = value }
    override var y
        get() = buf[offset + 1]
        set(value) { buf[offset + 1] = value }
}

class Vec3fView(private val buf: Float32Buffer, var offset: Int) : MutableVec3f() {
    override var x
        get() = buf[offset]
        set(value) { buf[offset] = value }
    override var y
        get() = buf[offset + 1]
        set(value) { buf[offset + 1] = value }
    override var z
        get() = buf[offset + 2]
        set(value) { buf[offset + 2] = value }
}

class Vec4fView(private val buf: Float32Buffer, var offset: Int) : MutableVec4f() {
    override var x
        get() = buf[offset]
        set(value) { buf[offset] = value }
    override var y
        get() = buf[offset + 1]
        set(value) { buf[offset + 1] = value }
    override var z
        get() = buf[offset + 2]
        set(value) { buf[offset + 2] = value }
    override var w
        get() = buf[offset + 3]
        set(value) { buf[offset + 3] = value }
}