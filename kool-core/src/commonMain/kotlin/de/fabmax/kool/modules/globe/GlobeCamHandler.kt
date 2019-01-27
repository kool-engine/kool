package de.fabmax.kool.modules.globe

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.SphericalInputTransform
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

class GlobeCamHandler(val globe: Globe, scene: Scene, ctx: KoolContext) : SphericalInputTransform() {

    private val globePan = GlobePan()

    init {
        globe.onPreRender += { onPreRender(it) }

        // panning is handled by the globe itself (by rotating it's parent transform group around the globe center)
        // therefore we configure the camera transform to center-zoom and no panning
        // this way camera center is always at (0, 0, 0) in world coordinates
        leftDragMethod = SphericalInputTransform.DragMethod.NONE
        rightDragMethod = SphericalInputTransform.DragMethod.ROTATE
        zoomMethod = SphericalInputTransform.ZoomMethod.ZOOM_CENTER

        // zoom range is quite large: 20 meters to 20000 km above surface
        minZoom = 2e1f
        maxZoom = 2e7f

        verticalAxis = Vec3f.Z_AXIS
        minHorizontalRot = 0f
        maxHorizontalRot = 85f

        resetZoom(1e7f)

        +scene.camera
        updateTransform()
        scene.camera.updateCamera(ctx)

        scene.registerDragHandler(this)
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, scene: Scene, ctx: KoolContext) {
        super.handleDrag(dragPtrs, scene, ctx)

        if (dragPtrs.size == 1 && dragPtrs[0].isInViewport(scene.viewport, ctx)) {
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
    }

    private fun onPreRender(ctx: KoolContext) {
        if (globePan.isValid) {
            globePan.apply(ctx)
            globe.setCenter(globePan.globeCenter.y, globePan.globeCenter.x)
        }
    }

    private inner class GlobePan {
        val pickRay = Ray()
        val tmpVec = MutableVec3d()
        val tmpRayO = MutableVec3d()
        val tmpRayL = MutableVec3d()

        val hitPosWorld = MutableVec3d()

        val screenPosStart = MutableVec2f()
        val screenPos = MutableVec2f()
        val globeCoordsStart = MutableVec2d()
        val globeCoords = MutableVec2d()
        val globeCenter = MutableVec2d()
        var startDist = 0.0
        var startZoom = 0f
        var isValid = false

        val cam
            get() = globe.scene?.camera!!
        val camTransform
            get() = cam.parent as SphericalInputTransform

        fun start(screenX: Float, screenY: Float, ctx: KoolContext) {
            globeCenter.set(globe.centerLon, globe.centerLat)

            screenPosStart.set(screenX, screenY)
            screenPos.set(screenPosStart)

            if (screen2LatLon(screenX, screenY, globeCoordsStart, ctx)) {
                startDist = cam.globalPos.toMutableVec3d(tmpVec).distance(hitPosWorld)
                startZoom = camTransform.zoom
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
                //applyZoom()
            }
        }

        private fun applyZoom() {
            cam.globalPos.toMutableVec3d(tmpRayO)
            cam.globalLookDir.toMutableVec3d(tmpRayL).norm()

            val d = hitDistSphere(tmpRayO, tmpRayL, hitPosWorld, startDist)
            if (d < Double.MAX_VALUE) {
                //val dd = d.toFloat().clamp(camTransform.zoom / -2, camTransform.zoom / 2)
                camTransform.resetZoom(camTransform.zoom - d.toFloat() / 4)
                println("startDist = $startDist, d = $d")
            }
        }

        /**
         * Compute latitude / longitude for the given screen coordinate. Result coordinate is stored in
         * result vec: x = longitude in degrees, y = latitude in degrees
         */
        private fun screen2LatLon(screenX: Float, screenY: Float, result: MutableVec2d, ctx: KoolContext): Boolean {
            val viewport = scene?.viewport ?: return false
            if (cam.computePickRay(pickRay, screenX, screenY, viewport, ctx)) {
                // compute origin and direction of pick ray in globe coordinates
                pickRay.origin.toMutableVec3d(tmpRayO)
                pickRay.direction.toMutableVec3d(tmpRayL)
                globe.toLocalCoordsDp(tmpRayO, 1.0)
                globe.toLocalCoordsDp(tmpRayL, 0.0)

                // compute hit point of pick ray on globe surface: ray-sphere intersection
                // globe center is at (0, 0, 0) -> ray origin is the vector from sphere origin to ray origin
                val radius = globe.radius + globe.getHeightAt(globe.centerLat, globe.centerLon)
                val ldo = tmpRayL * tmpRayO
                val sqr = ldo * ldo - tmpRayO.sqrLength() + radius * radius
                if (sqr > 0) {
                    val hitDist = -ldo - sqrt(sqr)
                    // compute ray hit point in globe coordinates and store it in tmpVec
                    tmpRayL.scale(hitDist, hitPosWorld).add(tmpRayO)

                    result.x = atan2(hitPosWorld.x, hitPosWorld.z).toDeg()
                    result.y = (PI * 0.5 - acos(hitPosWorld.y / radius)).toDeg()
                    globe.toGlobalCoordsDp(hitPosWorld)

                    return true
                }
            }
            return false
        }

        private fun hitDistSphere(orig: Vec3d, dir: Vec3d, center: Vec3d, radius: Double): Double {
            center.subtract(orig, tmpVec)

            val tc = tmpVec * dir
            if (tc < 0) {
                return Double.MAX_VALUE
            }

            val rSqr = radius * radius
            val dSqr = tmpVec.sqrLength() - (tc * tc)
            if (dSqr > rSqr) {
                return Double.MAX_VALUE
            }

            val t1c = sqrt(rSqr - dSqr)
            return tc - t1c
        }
    }
}