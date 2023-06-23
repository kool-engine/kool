package de.fabmax.kool.demo.bees

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ui2.Grow
import de.fabmax.kool.modules.ui2.Text
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addLineMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.roundToInt

class BeeDemo : DemoScene("Fighting Bees") {

    private val beeSystemA = BeeSystem(0)
    private val beeSystemB = BeeSystem(1)

    private lateinit var beeTex: Texture2d

    init {
        beeSystemA.enemyBees = beeSystemB
        beeSystemB.enemyBees = beeSystemA
    }

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        val texProps = TextureProps(
            addressModeU = AddressMode.CLAMP_TO_EDGE,
            addressModeV = AddressMode.CLAMP_TO_EDGE,
            minFilter = FilterMethod.NEAREST,
            magFilter = FilterMethod.NEAREST,
            mipMapping = false,
            maxAnisotropy = 1
        )
        beeTex = loadTexture2d("${DemoLoader.materialPath}/bee.png", texProps)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera().apply {
            minZoom = 10.0
            maxZoom = 250.0
            zoom = 100.0
        }
        camera.setClipRange(1f, 1000f)

        mainRenderPass.clearColor = bgColor

        beeSystemA.beeShader.colorMap = beeTex
        beeSystemB.beeShader.colorMap = beeTex
        addNode(beeSystemA.beeMesh)
        addNode(beeSystemB.beeMesh)

        addLineMesh {
            addBoundingBox(BoundingBox(
                BeeConfig.worldExtent.scale(-1f, MutableVec3f()),
                BeeConfig.worldExtent.scale(1f, MutableVec3f())
            ), Color.WHITE)
        }

        onDispose {
            beeTex.dispose()
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        // There are two teams -> total number of bees is beesPerTeam * 2
        MenuSlider2(
            "Number of Bees",
            BeeConfig.beesPerTeam.use().toFloat(),
            10f,
            BeeConfig.maxBeesPerTeam.toFloat(),
            { "${it.roundToInt() * 2}" }
        ) {
            BeeConfig.beesPerTeam.set(it.roundToInt())
        }

        MenuRow {
            val t = beeSystemA.beeUpdateTime.use() + beeSystemB.beeUpdateTime.use()
            Text("Bee update:") { labelStyle(Grow.Std) }
            Text("${t.toString(2)} ms") { labelStyle() }
        }
        MenuRow {
            val t = beeSystemA.instanceUpdateTime.use() + beeSystemB.instanceUpdateTime.use() +
                    beeSystemA.beeMesh.drawTime + beeSystemB.beeMesh.drawTime
            Text("Bee drawing:") { labelStyle(Grow.Std) }
            Text("${t.toString(2)} ms") { labelStyle() }
        }
    }

    companion object {
        val ATTR_POSITION = Attribute("aPosition", GlslType.VEC_4F)
        val ATTR_ROTATION = Attribute("aRotation", GlslType.VEC_4F)

        val bgColor = MdColor.LIGHT_BLUE tone 400
    }
}
