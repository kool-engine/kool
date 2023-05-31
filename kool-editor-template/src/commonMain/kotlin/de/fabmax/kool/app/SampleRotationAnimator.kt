package de.fabmax.kool.app

import de.fabmax.kool.editor.api.EditorInfo
import de.fabmax.kool.editor.api.KoolScript
import de.fabmax.kool.editor.model.TransformComponent
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Time

class SampleRotationAnimator : KoolScript() {

    @EditorInfo("Rotation speed:", -360.0, 360.0)
    var rotationSpeed = Vec3f(17f, 31f, 19f)

    @EditorInfo("Speed multiplier:", -10.0, 10.0)
    var speedMulti = 1f

    private lateinit var transform: TransformComponent

    override fun onInit() {
        transform = nodeModel.getComponent<TransformComponent>()!!
    }

    override fun onUpdate() {
        val mat = transform.getMatrix()
        mat.rotate(
            Time.deltaT * rotationSpeed.x.toDouble() * speedMulti,
            Time.deltaT * rotationSpeed.y.toDouble() * speedMulti,
            Time.deltaT * rotationSpeed.z.toDouble() * speedMulti
        )
        transform.setMatrix(mat)
    }
}