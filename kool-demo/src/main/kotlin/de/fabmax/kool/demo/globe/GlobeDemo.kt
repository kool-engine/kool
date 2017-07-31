package de.fabmax.kool.demo.globe

import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Vec3f

/**
 * Globe demo: Show an OSM map on a sphere.
 */

fun globeScene(): Scene = scene {
    +sphericalInputTransform {
        leftDragMethod = SphericalInputTransform.DragMethod.NONE
        //leftDragMethod = SphericalInputTransform.DragMethod.PAN
        rightDragMethod = SphericalInputTransform.DragMethod.ROTATE

        panMethod = CameraOrthogonalPan()
        zoomMethod = SphericalInputTransform.ZoomMethod.ZOOM_CENTER

        minZoom = 2e1f
        maxZoom = 1.5e7f
        zoom = 1e7f
        zoomAnimator.set(zoom)

        verticalAxis = Vec3f.Z_AXIS
        minHorizontalRot = 0f
        maxHorizontalRot = 85f

        updateTransform()

        val cam = camera as PerspectiveCamera
        cam.clipNear = 2e4f
        cam.clipFar = 2e7f
        +cam
    }

    +Globe().apply {
        translate(0f, 0f, -Globe.EARTH_R.toFloat())

//        val z = 3
//        val n = (1 shl z) - 1
//        for (x in 0..n) {
//            for (y in 0..n) {
//                +TileMesh(x, y, z)
//            }
//        }
    }
}
