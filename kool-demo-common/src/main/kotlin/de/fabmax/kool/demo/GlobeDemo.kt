package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.formatDouble
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.globe.Globe
import de.fabmax.kool.modules.globe.GlobeDragHandler
import de.fabmax.kool.modules.globe.elevation.ElevationMapHierarchy
import de.fabmax.kool.modules.globe.elevation.ElevationMapMetaHierarchy
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
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.math.max

/**
 * Globe demo: Show an OSM map on a sphere.
 */

fun globeScene(ctx: KoolContext): List<Scene> {
    val scenes = mutableListOf<Scene>()

    // Create UI overlay with attribution info and stats (globe is set once it is created)
    val ui = GlobeUi(null, ctx)

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

            +camera
            updateTransform()
            camera.updateCamera(ctx)
        }

        // dim light to avoid over-exposure (OSM tiles are quite bright)
        light.color.set(Color.LIGHT_GRAY)

        // load elevation map meta data
        // default elevation URL requires elevation map data in path docs/assets/elevation (not the case if pulled from github)
        val elevationUrl = Demo.getProperty("globe.elevationUrl", "elevation")
        ctx.assetMgr.loadAsset("$elevationUrl/meta.pb") { data ->
            // create globe with earth radius
            val earth = Globe(6_371_000.8)
            ui!!.globe = earth

            if (data != null) {
                // deserialize elevation map meta data
                val metaHierarchy: ElevationMapMetaHierarchy = ProtoBuf.load(data)

                // use loaded elevation map info to generate tile meshes, tiles will have 2^6 x 2^6 vertices
                earth.elevationMapProvider = ElevationMapHierarchy(elevationUrl, metaHierarchy, ctx.assetMgr)
                earth.meshDetailLevel = 6
            }

            val dpGroup = DoublePrecisionRoot(earth)
            +dpGroup

            // register specialized mouse input handler for globe manipulation
            registerDragHandler(GlobeDragHandler(earth))
        }

    }

    // add ui overlay after globe scene (order matters)
    scenes += ui.scene
    return scenes
}

class GlobeUi(var globe: Globe?, val ctx: KoolContext) {

    private var containerWidth = 0f

    val scene = uiScene {
        theme = theme(UiTheme.DARK) {
            //componentUi(::SimpleComponentUi)
            //containerUi { BlankComponentUi() }
            containerUi(::SimpleComponentUi)
            componentUi { BlankComponentUi() }
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }
        content.ui.setCustom(BlankComponentUi())

        val attributions = mutableListOf<Pair<Button, String>>()
        val maxAttributions = 2

        +container("globeUI") {

            val posLbl = label("posLabel") {
                layoutSpec.setSize(dps(200f, true), dps(18f), zero())
                layoutSpec.setOrigin(dps(-200f, true), zero(), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +posLbl

            for (i in 0 until maxAttributions) {
                val button = button("attributionText_$i") {
                    layoutSpec.setSize(dps(200f, true), dps(18f), zero())
                    layoutSpec.setOrigin(dps(-200f, true), dps(18f * (i+1), true), zero())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    textColor.setCustom(Color.LIME)
                    textColorHovered.setCustom(color("#42A5F5"))
                    text = ""

                    onClick += { _, _, _ ->
                        if (!attributions[i].second.isEmpty()) {
                            ctx.openUrl(attributions[i].second)
                        }
                    }
                }
                attributions += Pair(button, "")
                +button
            }

            onPreRender += {
                var width = 0f
                var lines = 1

                val globe = this@GlobeUi.globe
                if (globe != null) {
                    val lat = formatDouble(globe.centerLat, 5)
                    val lon = formatDouble(globe.centerLon, 5)
                    val hgt = if (globe.cameraHeight > 10000) {
                        "${formatDouble(globe.cameraHeight / 1000.0, 1)} km"
                    } else {
                        "${formatDouble(globe.cameraHeight, 1)} m"
                    }
                    posLbl.text = "Center: $lat°, $lon°  $hgt"
                    width = posLbl.font.apply()?.textWidth(posLbl.text) ?: 0f
                }

                globe?.tileManager?.getCenterTile()?.let {
                    for (i in attributions.indices) {
                        attributions[i].first.text = ""
                    }
                    it.attributionInfo.forEachIndexed { i, attributionInfo ->
                        if (i < attributions.size) {
                            attributions[i].first.text = attributionInfo.text
                            if (attributionInfo.url != attributions[i].second) {
                                attributions[i].also { attributions[i] = Pair(it.first, it.second) }
                            }
                            val w = attributions[i].first.font.apply()?.textWidth(attributions[i].first.text) ?: 0f
                            width = max(width, w)
                            lines++
                        }
                    }
                }

                if (width != containerWidth) {
                    containerWidth = width
                    layoutSpec.setSize(dps(containerWidth + 8, true), dps(18f * lines), zero())
                    layoutSpec.setOrigin(dps(-containerWidth - 8, true), zero(), zero())
                    this@uiScene.content.requestLayout()
                }
            }
        }
    }
}
