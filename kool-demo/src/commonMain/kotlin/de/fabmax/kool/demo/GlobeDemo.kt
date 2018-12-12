package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.globe.Globe
import de.fabmax.kool.modules.globe.GlobeCamHandler
import de.fabmax.kool.modules.globe.elevation.ElevationMapHierarchy
import de.fabmax.kool.modules.globe.elevation.ElevationMapMetaHierarchy
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.doubleprec.DoublePrecisionRoot
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logW
import kotlinx.serialization.load
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
        // dim light to avoid over-exposure (OSM tiles are quite bright)
        light.color.set(Color.LIGHT_GRAY)

        // create globe with earth radius
        val globe = Globe(6_371_000.8)

        // globe is added into a double precision transform group
        // standard transform group with single precision is not accurate enough for high zoom levels
        val globeGroup = DoublePrecisionRoot(globe)

        ui.globe = globe

        // GlobeCamHandler handles mouse interaction
        +GlobeCamHandler(globe, this, ctx).apply {
            // set a nice start location in the alps
            globe.setCenter(47.05, 9.48)

            // zoom in and tilt the camera to get a nice mountain panorama
            resetZoom(15e3f)
            horizontalRotation = 60f
            verticalRotation = -30f
        }

        // load elevation map meta data
        // default elevation URL requires elevation map data in path docs/assets/elevation (not the case if pulled from github)
        val elevationUrl = Demo.getProperty("globe.elevationUrl", "elevation")
        ctx.assetMgr.loadAsset("$elevationUrl/meta.pb") { data ->
            if (data != null) {
                // deserialize elevation map meta data
                val metaHierarchy: ElevationMapMetaHierarchy = ProtoBuf.load(data)

                // use loaded elevation map info to generate tile meshes, tiles will have 2^6 x 2^6 vertices
                globe.elevationMapProvider = ElevationMapHierarchy(elevationUrl, metaHierarchy, ctx.assetMgr)
                globe.meshDetailLevel = 6
            } else {
                logW { "Height map data not available" }
            }

            // do not render globe before height map info is loaded...
            +globeGroup
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
                    textColorHovered.setCustom(Color.fromHex("#42A5F5"))
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
                    val lat = globe.centerLat.toString(5)
                    val lon = globe.centerLon.toString(5)
                    val hgt = if (globe.cameraHeight > 10000) {
                        "${(globe.cameraHeight / 1000.0).toString(1)} km"
                    } else {
                        "${globe.cameraHeight.toString(1)} m"
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
