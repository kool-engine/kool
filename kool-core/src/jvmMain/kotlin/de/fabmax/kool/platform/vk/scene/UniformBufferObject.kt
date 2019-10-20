package de.fabmax.kool.platform.vk.scene

import de.fabmax.kool.math.Mat4f

class UniformBufferObject {
    val model = Mat4f()
    val view = Mat4f()
    val proj = Mat4f()

    fun reset() {
        model.setIdentity()
        view.setIdentity()
        proj.setIdentity()
    }

    companion object {
        const val SIZE = 3 * 16 * 4
    }
}