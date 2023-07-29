package de.fabmax.kool.editor.data

import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.PerspectiveCamera
import kotlinx.serialization.Serializable

@Serializable
class CameraComponentData(var camera: CameraTypeData) : ComponentData

@Serializable
sealed class CameraTypeData {
    abstract val name: String

    abstract val clipNear: Float
    abstract val clipFar: Float

    abstract fun createCamera(): Camera

    abstract fun updateOrCreateCamera(existingCamera: Camera): Camera

    @Serializable
    data class Perspective(val fovY: Float = 60f, override val clipNear: Float = 0.1f, override val clipFar: Float = 1000f) : CameraTypeData() {
        override val name: String = "Perspective"

        private fun applyCamProperties(target: PerspectiveCamera) {
            target.setClipRange(clipNear, clipFar)
            target.fovY = this@Perspective.fovY
            target.name = name
        }

        override fun createCamera(): PerspectiveCamera = PerspectiveCamera(name).also { applyCamProperties(it) }

        override fun updateOrCreateCamera(existingCamera: Camera): PerspectiveCamera {
            val perspectiveCam = if (existingCamera is PerspectiveCamera) existingCamera else createCamera()
            applyCamProperties(perspectiveCam)
            return perspectiveCam
        }
    }

    @Serializable
    data class Orthographic(val height: Float, override val clipNear: Float = 0.1f, override val clipFar: Float = 1000f) : CameraTypeData() {
        override val name: String = "Orthographic"

        private fun applyCamProperties(target: OrthographicCamera) {
            target.setClipRange(clipNear, clipFar)
            target.top = height * 0.5f
            target.bottom = height * -0.5f
            target.isKeepAspectRatio = true
            target.name = name
        }

        override fun createCamera(): OrthographicCamera = OrthographicCamera(name).also { applyCamProperties(it) }

        override fun updateOrCreateCamera(existingCamera: Camera): OrthographicCamera {
            val orthographicCam = if (existingCamera is OrthographicCamera) existingCamera else createCamera()
            applyCamProperties(orthographicCam)
            return orthographicCam
        }
    }
}
