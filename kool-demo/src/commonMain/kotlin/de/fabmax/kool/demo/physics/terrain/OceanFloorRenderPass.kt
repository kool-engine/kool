package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.PerspectiveProxyCam
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor

class OceanFloorRenderPass(mainScene: Scene, val terrainTiles: TerrainTiles) :
    OffscreenRenderPass2d(Node(), renderPassConfig {
        name = "OceanFloorPass"
        setDynamicSize()
        addColorTexture(TexFormat.RGBA)
        setDepthTexture(false)
    }) {

    val renderGroup: Node
        get() = drawNode

    init {
        isUpdateDrawNode = false
        clearColor = MdColor.GREY
        drawNode.apply {
            for (i in 0 until TerrainTiles.TILE_CNT_XY) {
                for (j in 0 until TerrainTiles.TILE_CNT_XY) {
                    if (terrainTiles.getMinElevation(i, j) < Ocean.OCEAN_FLOOR_HEIGHT_THRESH) {
                        addNode(terrainTiles.getTile(i, j))
                    }
                }
            }
        }

        mainScene.addOffscreenPass(this)
        mainScene.onRenderScene += { ctx ->
            val mapW = (mainScene.mainRenderPass.viewport.width * RENDER_SIZE_FACTOR).toInt()
            val mapH = (mainScene.mainRenderPass.viewport.height * RENDER_SIZE_FACTOR).toInt()

            if (isEnabled && mapW > 0 && mapH > 0 && (mapW != width || mapH != height)) {
                resize(mapW, mapH, ctx)
            }
        }

        val proxyCamera = PerspectiveProxyCam(mainScene.camera as PerspectiveCamera)
        onBeforeCollectDrawCommands += {
            proxyCamera.sync(mainScene.mainRenderPass, it)
        }
        camera = proxyCamera
        lighting = mainScene.lighting
    }

    companion object {
        const val RENDER_SIZE_FACTOR = 0.5f
    }
}