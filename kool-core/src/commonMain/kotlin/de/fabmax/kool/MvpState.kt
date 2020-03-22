package de.fabmax.kool

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4dStack


/**
 * @author fabmax
 */

class MvpState internal constructor() {

    val projMatrix = Mat4dStack()

    val viewMatrix = Mat4dStack()

    val modelMatrix = Mat4dStack()

    // combined model view projection matrix
    val mvpMatrix = Mat4d()

    // temp matrix buffer for calculations
    private val tempMatrix = Mat4d()

    init {
        reset()
    }

    fun reset() {
        projMatrix.reset()
        viewMatrix.reset()
        modelMatrix.reset()
        mvpMatrix.setIdentity()
    }

    fun pushMatrices() {
        projMatrix.push()
        viewMatrix.push()
        modelMatrix.push()
    }

    fun popMatrices() {
        projMatrix.pop()
        viewMatrix.pop()
        modelMatrix.pop()
    }

    /**
     * Computes the MVP matrix from the individual model-, view- and projection matrices. This
     * method must be called after an update of any of these matrices.
     */
    fun update(ctx: KoolContext) {
        // Combine projection, model and view matrices
        projMatrix.mul(viewMatrix.mul(modelMatrix, tempMatrix), mvpMatrix)
    }

}
