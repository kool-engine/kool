package de.fabmax.kool.modules.globe

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

class GlobeDragHandler(val globe: Globe) : InputManager.DragHandler {
    private val pickRay = Ray()
    private val tmpVec = MutableVec3d()
    private val tmpRayO = MutableVec3d()
    private val tmpRayL = MutableVec3d()

    private val globePan = GlobePan()

    init {
        globe.onPreRender += { onPreRender(it) }
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, ctx: KoolContext): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isInViewport(ctx)) {
            val ptr = dragPtrs[0]
            val startPan = ptr.isLeftButtonEvent && ptr.isLeftButtonDown
            val startRotate = ptr.isRightButtonEvent && ptr.isRightButtonDown
            val startZoom = ptr.deltaScroll != 0f && (ptr.x != globePan.screenPosStart.x || ptr.y != globePan.screenPosStart.y)

            if (startPan || startRotate || startZoom) {
                // start pan: Map is panned such that the lat/long coordinate at the specified screen pixel will remain constant
                globePan.start(ptr.x, ptr.y, ctx)
            }

            if (ptr.isLeftButtonDown) {
                // left click drag: update screen position to current cursor pos to do the panning
                globePan.screenPos.set(ptr.x, ptr.y)
            }
        }
        return 0
    }

    private fun onPreRender(ctx: KoolContext) {
        if (globePan.isValid) {
            globePan.apply(ctx)
            globe.setCenter(globePan.globeCenter.y, globePan.globeCenter.x, 0.0)
        }
    }

    private inner class GlobePan {
        val screenPosStart = MutableVec2f()
        val screenPos = MutableVec2f()
        val globeCoordsStart = MutableVec2d()
        val globeCoords = MutableVec2d()
        val globeCenter = MutableVec2d()
        var isValid = false

        fun start(screenX: Float, screenY: Float, ctx: KoolContext) {
            globeCenter.set(globe.centerLon, globe.centerLat)

            screenPosStart.set(screenX, screenY)
            screenPos.set(screenPosStart)

            if (screen2LatLon(screenX, screenY, globeCoordsStart, ctx)) {
                globeCoords.set(globeCoordsStart)
            }
            isValid = true
        }

        fun apply(ctx: KoolContext) {
            if (isValid) {
                if (screen2LatLon(screenPos.x, screenPos.y, globeCoords, ctx)) {
                    globeCenter.x += (globeCoordsStart.x - globeCoords.x)
                    globeCenter.y = (globeCenter.y + (globeCoordsStart.y - globeCoords.y)).clamp(-85.0, 85.0)
                }
            }
        }

        /**
         * Compute latitude / longitude for the given screen coordinate. Result coordinate is stored in
         * result vec: x = longitude in degrees, y = latitude in degrees
         */
        fun screen2LatLon(screenX: Float, screenY: Float, result: MutableVec2d, ctx: KoolContext): Boolean {
            if (globe.scene?.camera?.computePickRay(pickRay, screenX, screenY, ctx) == true) {
                // compute origin and direction of pick ray in globe coordinates
                pickRay.origin.toMutableVec3d(tmpRayO)
                pickRay.direction.toMutableVec3d(tmpRayL)
                globe.toLocalCoordsDp(tmpRayO, 1.0)
                globe.toLocalCoordsDp(tmpRayL, 0.0)

                // compute hit point of pick ray on globe surface: ray-sphere intersection
                // globe center is at (0, 0, 0) -> ray origin is the vector from sphere origin to ray origin
                // fixme: this ignores the heightmap / mountains
                val ldo = tmpRayL * tmpRayO
                val sqr = ldo * ldo - tmpRayO.sqrLength() + globe.radius * globe.radius
                if (sqr > 0) {
                    val hitDist = -ldo - sqrt(sqr)
                    // compute ray hit point in globe coordinates and store it in tmpVec
                    tmpRayL.scale(hitDist, tmpVec).add(tmpRayO)

                    val radius = tmpVec.length()
                    result.x = atan2(tmpVec.x, tmpVec.z).toDeg()
                    result.y = (PI * 0.5 - acos(tmpVec.y / radius)).toDeg()

                    return true
                }
            }
            return false
        }
    }
}