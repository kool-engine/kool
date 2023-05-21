package de.fabmax.kool.app

import de.fabmax.kool.editor.api.KoolScript
import de.fabmax.kool.editor.model.TransformComponent
import de.fabmax.kool.util.Time

class SampleScript : KoolScript() {

    lateinit var transform: TransformComponent

    override fun onInit() {
        transform = nodeModel.getComponent<TransformComponent>()!!
    }

    override fun onUpdate() {
        val mat = transform.getMatrix()
        mat.rotate(Time.deltaT * 17.0, Time.deltaT * 31.0, Time.deltaT * 19.0)
        transform.setMatrix(mat)
    }
}