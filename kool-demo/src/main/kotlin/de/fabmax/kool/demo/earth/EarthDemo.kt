package de.fabmax.kool.demo.earth

import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.SphericalInputTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.Vec3f

/**
 * Earth demo: Show an OSM map on a sphere.
 */

fun earthScene(): List<Scene> {
    val scenes = mutableListOf<Scene>()
    var earth: Earth? = null

    scenes += scene {
        +sphericalInputTransform {
            // some of the mouse interactions are handled by Earth itself
            // therefore we configure the camera transform to center-zoom and no panning
            leftDragMethod = SphericalInputTransform.DragMethod.NONE
            rightDragMethod = SphericalInputTransform.DragMethod.ROTATE
            zoomMethod = SphericalInputTransform.ZoomMethod.ZOOM_CENTER

            // zoom range is quite large: 20 meters to 20000 km above surface
            minZoom = 2e1f
            maxZoom = 2e7f
            zoom = 1e7f
            zoomAnimator.set(zoom)

            verticalAxis = Vec3f.Z_AXIS
            minHorizontalRot = 0f
            maxHorizontalRot = 85f

            updateTransform()

            +camera
        }

        earth = Earth().apply {
            translate(0f, 0f, -Earth.EARTH_R.toFloat())
        }
        +earth!!
    }

    val ui = EarthUi(earth!!)
    scenes += ui.scene

    return scenes
}

class EarthUi(val earth: Earth) {

    private lateinit var attributionText: Label
    private var textWidth = 0f

    val scene = uiScene {
        theme = theme(UiTheme.DARK) {
            componentUi(::SimpleComponentUi)
            containerUi({ BlankComponentUi() })
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }

        attributionText = label("attributionText") {
            layoutSpec.setOrigin(dps(-320f), dps(0f), zero())
            layoutSpec.setSize(dps(320f), dps(18f), zero())
            padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
            onRender += {
                // fixme any better way to do string formatting which also works in js?
                val lat = if (earth.centerLat == 0.0) { "0.000000" } else { "${earth.centerLat}000000" }
                val lon = if (earth.centerLon == 0.0) { "0.000000" } else { "${earth.centerLon}000000" }
                val hgt = if (earth.cameraHeight > 10000) {
                    val s = "${earth.cameraHeight / 1000.0}"
                    "${s.subSequence(0, s.indexOf('.') + 2)} km"
                } else {
                    val s = "${earth.cameraHeight}"
                    "${s.subSequence(0, s.indexOf('.') + 2)} m"
                }
                text = "${earth.attribution}   ${lat.subSequence(0, lat.indexOf('.') + 6)}°, ${lon.subSequence(0, lat.indexOf('.') + 6)}°   $hgt"
                val w = font.apply().textWidth(text)
                if (w != textWidth) {
                    textWidth = w
                    layoutSpec.setSize(dps(w + 8, true), dps(18f), zero())
                    layoutSpec.setOrigin(dps(-w - 8, true), dps(0f), zero())
                    content.requestLayout()
                }
            }
        }
        +attributionText
    }

}
