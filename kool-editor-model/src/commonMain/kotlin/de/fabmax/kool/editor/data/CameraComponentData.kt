package de.fabmax.kool.editor.data

import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.PerspectiveCamera
import kotlinx.serialization.Serializable

@Serializable
data class CameraComponentData(val camera: CameraTypeData) : ComponentData

@Serializable
sealed class CameraTypeData {
    abstract val name: String

    abstract val clipNear: Float
    abstract val clipFar: Float

    abstract fun createCamera(): Camera

    abstract fun updateCamera(existingCamera: Camera): Boolean

    @Serializable
    data class Perspective(val fovY: Float = 60f, override val clipNear: Float = 0.1f, override val clipFar: Float = 1000f) : CameraTypeData() {
        override val name: String = "Perspective"

        override fun createCamera(): PerspectiveCamera = PerspectiveCamera(name).also { updateCamera(it) }

        override fun updateCamera(existingCamera: Camera): Boolean {
            val perspectiveCam = existingCamera as? PerspectiveCamera ?: return false
            perspectiveCam.setClipRange(clipNear, clipFar)
            perspectiveCam.fovY = this@Perspective.fovY.deg
            perspectiveCam.name = name
            return true
        }
    }

    @Serializable
    data class Orthographic(val height: Float, override val clipNear: Float = 0.1f, override val clipFar: Float = 1000f) : CameraTypeData() {
        override val name: String = "Orthographic"

        override fun createCamera(): OrthographicCamera = OrthographicCamera(name).also { updateCamera(it) }

        override fun updateCamera(existingCamera: Camera): Boolean {
            val orthographicCam = existingCamera as? OrthographicCamera ?: return false
            orthographicCam.setClipRange(clipNear, clipFar)
            orthographicCam.top = height * 0.5f
            orthographicCam.bottom = height * -0.5f
            orthographicCam.isKeepAspectRatio = true
            orthographicCam.name = name
            return true
        }
    }
}
