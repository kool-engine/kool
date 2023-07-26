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

    open fun updateOrCreateCamera(existingCamera: Camera): Camera {
        existingCamera.name = name
        return existingCamera
    }

    @Serializable
    class Perspective(val fovY: Float = 60f, override val clipNear: Float = 0.1f, override val clipFar: Float = 1000f) : CameraTypeData() {
        override val name: String = "Perspective"

        override fun createCamera(): PerspectiveCamera = PerspectiveCamera(name).apply {
            setClipRange(clipNear, clipFar)
            this.fovY = this@Perspective.fovY
        }

        override fun updateOrCreateCamera(existingCamera: Camera): PerspectiveCamera {
            val perspectiveCam = if (existingCamera is PerspectiveCamera) existingCamera else createCamera()
            super.updateOrCreateCamera(existingCamera)
            return perspectiveCam
        }
    }

    @Serializable
    class Orthographic(val height: Float, override val clipNear: Float, override val clipFar: Float) : CameraTypeData() {
        override val name: String = "Orthographic"

        override fun createCamera(): OrthographicCamera = OrthographicCamera(name).apply {
            setClipRange(clipNear, clipFar)
            top = height * 0.5f
            bottom = height * -0.5f
            isKeepAspectRatio = true
        }

        override fun updateOrCreateCamera(existingCamera: Camera): OrthographicCamera {
            val perspectiveCam = if (existingCamera is OrthographicCamera) existingCamera else createCamera()
            super.updateOrCreateCamera(existingCamera)
            return perspectiveCam
        }
    }
}
