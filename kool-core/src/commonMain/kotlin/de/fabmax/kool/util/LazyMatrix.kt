package de.fabmax.kool.util

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4f

class LazyMat4d(val update: (Mat4d) -> Unit) {
    var isDirty = true

    private val mat = Mat4d()

    fun get(): Mat4d {
        if (isDirty) {
            update(mat)
            isDirty = false
        }
        return mat
    }
}

class LazyMat4f(val update: (Mat4f) -> Unit) {
    var isDirty = true

    private val mat = Mat4f()

    fun get(): Mat4f {
        if (isDirty) {
            update(mat)
            isDirty = false
        }
        return mat
    }
}