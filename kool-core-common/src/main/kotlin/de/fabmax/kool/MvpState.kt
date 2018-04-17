package de.fabmax.kool

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Mat4fStack
import de.fabmax.kool.util.createFloat32Buffer


/**
 * @author fabmax
 */

class MvpState internal constructor() {

    val projMatrix = Mat4fStack()
    val projMatrixBuffer = createFloat32Buffer(16)
        get() {
            projMatrix.toBuffer(field)
            return field
        }

    val viewMatrix = Mat4fStack()
    val viewMatrixBuffer = createFloat32Buffer(16)
        get() {
            viewMatrix.toBuffer(field)
            return field
        }

    val modelMatrix = Mat4fStack()
    val modelMatrixBuffer = createFloat32Buffer(16)
        get() {
            modelMatrix.toBuffer(field)
            return field
        }

    // combined model view projection matrix
    val mvpMatrix = Mat4f()
    val mvpMatrixBuffer = createFloat32Buffer(16)
        get() {
            mvpMatrix.toBuffer(field)
            return field
        }

    // temp matrix buffer for calculations
    private val tempMatrix = Mat4f()

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

        // notify current shader about matrix update
        ctx.shaderMgr.boundShader?.onMatrixUpdate(ctx)
    }

}
