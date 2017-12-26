package de.fabmax.kool.demo.earth

import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*

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

            +transformGroup {
                rotate(13.341f, Vec3f.Y_AXIS)
                rotate(-52.556f, Vec3f.X_AXIS)
                translate(0f, 0f, Earth.EARTH_R.toFloat())

                +colorMesh {
                    generator = {
                        scale(2e1f, 2e1f, 2e1f)

                        cube {
                            colorCube()
                            centerOrigin()
                        }
                    }
                }
            }
        }
        +earth!!
    }

    val ui = EarthUi(earth!!)
    scenes += ui.scene

    return scenes
}

class EarthUi(val earth: Earth) {

    private lateinit var attributionText: Label
    private var attribWidth = 0f
    private var posWidth = 0f

    val scene = uiScene {
        theme = theme(UiTheme.DARK) {
            componentUi(::SimpleComponentUi)
            containerUi({ BlankComponentUi() })
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }

        attributionText = button("attributionText") {
            padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
            textColor.setCustom(Color.LIME)
            textColorHovered.setCustom(color("#42A5F5"))

            onRender += {
                text = earth.attribution
                val w = font.apply().textWidth(text)
                if (w != attribWidth) {
                    attribWidth = w
                    layoutSpec.setSize(dps(w + 8, true), dps(18f), zero())
                    posWidth = 0f
                }
            }

            onClick += { _,_,_ ->
                if (!earth.attributionUrl.isEmpty()) {
                    Platform.openUrl(earth.attributionUrl)
                }
            }
        }
        +attributionText

        +label("posLabel") {
            padding = Margin(zero(), zero(), dps(4f, true), dps(0f, true))

            onRender += {
                val lat = formatDouble(earth.centerLat, 5)
                val lon = formatDouble(earth.centerLon, 5)
                val hgt = if (earth.cameraHeight > 10000) {
                    "${formatDouble(earth.cameraHeight / 1000.0, 1)} km"
                } else {
                    "${formatDouble(earth.cameraHeight, 1)} m"
                }
                text = "$lat°, $lon°  $hgt"
                val w = font.apply().textWidth(text)
                if (w != posWidth) {
                    posWidth = w

                    val xOri = dps(-w - 8, true)
                    layoutSpec.setSize(dps(w + 8, true), dps(18f), zero())
                    layoutSpec.setOrigin(xOri, dps(0f), zero())
                    attributionText.layoutSpec.setOrigin(xOri - attributionText.layoutSpec.width, zero(), zero())

                    content.requestLayout()
                }
            }
        }
    }

    // fixme: any better way to do string formatting which also works in js?
    private fun formatDouble(value: Double, precision: Int): String {
        var s = "$value"
        if (!s.contains('.')) {
            s += "."
        }
        s += "000000"
        return s.substring(0, s.indexOf('.') + 1 + Math.min(6, precision))
    }

}
