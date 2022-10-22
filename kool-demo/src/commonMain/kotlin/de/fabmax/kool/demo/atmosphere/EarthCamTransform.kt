package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.clamp
import de.fabmax.kool.scene.Group
import de.fabmax.kool.util.InputStack
import kotlin.math.min
import kotlin.math.pow

class EarthCamTransform(val earthRadius: Float) : Group(), InputStack.PointerListener {

    var planetRotX = 0.0
    var planetRotY = 0.0

    var lookRotX = 0.0
    var lookRotY = 0.0

    var camRotX = 0.0

    var zoom = 2f

    var minZoom = 0.001f
    var maxZoom = 15f

    private val planetRot = Mat4d()
    private val lookRot = Mat4d()
    private val camRot = Mat4d()

    init {
        // initial center position (lat / long)
        planetRot.rotate(13.0, Vec3d.Y_AXIS)
        planetRot.rotate(40.0, Vec3d.NEG_X_AXIS)

        onUpdate += {
            val localX = MutableVec3d(Vec3d.X_AXIS)
            val localY = MutableVec3d(Vec3d.Y_AXIS)

            lookRot.setIdentity()
            lookRot.rotate(lookRotY, Vec3d.Z_AXIS)
            lookRot.rotate(lookRotX, Vec3d.X_AXIS)

            camRot.setIdentity()
            camRot.rotate(camRotX, Vec3d.X_AXIS)

            lookRot.transform(localX, 0.0)
            lookRot.transform(localY, 0.0)
            planetRot.rotate(planetRotX, localX)
            planetRot.rotate(planetRotY, localY)

            setIdentity()
            mul(planetRot)
            translate(0f, 0f, earthRadius)
            mul(lookRot)
            translate(0f, 0f, earthRadius * zoom)
            mul(camRot)
        }

        InputStack.defaultInputHandler.pointerListeners += this
        onDispose += {
            InputStack.defaultInputHandler.pointerListeners -= this
        }
    }

    override fun handlePointer(pointerState: InputManager.PointerState, ctx: KoolContext) {
        val dragPtr = pointerState.primaryPointer
        if (!dragPtr.isConsumed() && dragPtr.isValid) {
            val moveScale = min(zoom, 10f)
            when {
                dragPtr.isLeftButtonDown -> {
                    planetRotX = -dragPtr.deltaY / 15.0 * moveScale.pow(0.6f)
                    planetRotY = -dragPtr.deltaX / 15.0 * moveScale.pow(0.6f)
                }
                dragPtr.isRightButtonDown -> {
                    lookRotX -= dragPtr.deltaY / 10.0
                    lookRotY -= dragPtr.deltaX / 10.0
                }
                dragPtr.isMiddleButtonDown -> {
                    camRotX = (camRotX - dragPtr.deltaY / 10.0).clamp(0.0, 180.0)
                    lookRotY -= dragPtr.deltaX / 10.0
                }
            }
            zoom = (zoom * (1f - dragPtr.deltaScroll.toFloat() / 10f)).clamp(minZoom, maxZoom)

            dragPtr.consume()
        }
    }
}