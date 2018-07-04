package de.fabmax.kool.demo.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.formatDouble
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.SphericalInputTransform
import de.fabmax.kool.scene.doubleprec.DoublePrecisionRoot
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.color

/**
 * Globe demo: Show an OSM map on a sphere.
 */

fun globeScene(ctx: KoolContext): List<Scene> {
    val scenes = mutableListOf<Scene>()
    var globe: Globe? = null

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

        val earthRadius = 6_371_000.8
        globe = Globe(earthRadius)
        val dpGroup = DoublePrecisionRoot(globe!!)
        +dpGroup

        registerDragHandler(GlobeDragHandler(globe!!))
    }

    val ui = GlobeUi(globe!!, ctx)
    scenes += ui.scene

    return scenes
}

class GlobeUi(val globe: Globe, val ctx: KoolContext) {

    private lateinit var attributionText: Label
    private var attribWidth = 0f
    private var posWidth = 0f

    var attribution = "© OpenStreetMap"
    var attributionUrl = "http://www.openstreetmap.org/copyright"

    val scene = uiScene {
        theme = theme(UiTheme.DARK) {
            componentUi(::SimpleComponentUi)
            containerUi { BlankComponentUi() }
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }

        attributionText = button("attributionText") {
            padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
            textColor.setCustom(Color.LIME)
            textColorHovered.setCustom(color("#42A5F5"))

            onRender += {
                text = attribution
                val w = font.apply()?.textWidth(text) ?: 0f
                if (w != attribWidth) {
                    attribWidth = w
                    layoutSpec.setSize(dps(w + 8, true), dps(18f), zero())
                    posWidth = 0f
                }
            }

            onClick += { _,_,_ ->
                if (!attributionUrl.isEmpty()) {
                    ctx.openUrl(attributionUrl)
                }
            }
        }
        +attributionText

        +label("posLabel") {
            padding = Margin(zero(), zero(), dps(4f, true), dps(0f, true))

            onRender += {
                val lat = formatDouble(globe.centerLat, 5)
                val lon = formatDouble(globe.centerLon, 5)
                val hgt = if (globe.cameraHeight > 10000) {
                    "${formatDouble(globe.cameraHeight / 1000.0, 1)} km"
                } else {
                    "${formatDouble(globe.cameraHeight, 1)} m"
                }
                text = "$lat°, $lon°  $hgt"
                val w = font.apply()?.textWidth(text) ?: 0f
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
}
