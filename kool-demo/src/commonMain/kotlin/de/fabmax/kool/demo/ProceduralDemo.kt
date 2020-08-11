package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shadermodel.RefractionSamplerNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.deferred.deferredPbrShader
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

fun proceduralDemo(ctx: KoolContext): List<Scene> {
    val demo = ProceduralDemo(ctx)
    return listOf(demo.mainScene)
}

class ProceduralDemo(ctx: KoolContext) {
    val mainScene = makeScene(ctx)

    fun makeScene(ctx: KoolContext) = scene {
        +orbitInputTransform {
            setMouseRotation(-20f, -10f)
            setMouseTranslation(0f, 14f, 0f)
            zoom = 40.0
            +camera

            onUpdate += {
                verticalRotation += 5f * it.deltaT
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f, -0.3f, -1f))
            setColor(Color.MD_AMBER.mix(Color.WHITE, 0.5f).toLinear(), 3f)
        }

        ctx.assetMgr.launch {
            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", this)
            +Skybox(ibl.reflectionMap, 1f)

            val shadowMap = SimpleShadowMap(this@scene, 0).apply {
                optimizeForDirectionalLight = true
                shadowBounds = BoundingBox(Vec3f(-30f, 0f, -30f), Vec3f(30f, 60f, 30f))
            }
            val deferredCfg = DeferredPipelineConfig().apply {
                isWithScreenSpaceReflections = true
                isWithAmbientOcclusion = true
                isWithEmissive = true
                maxGlobalLights = 1
                useImageBasedLighting(ibl)
                useShadowMaps(listOf(shadowMap))
            }
            val deferredPipeline = DeferredPipeline(this@scene, deferredCfg).apply {
                aoPipeline?.radius = 0.6f

                contentGroup.apply {
                    makeGlas(pbrPass.colorTexture!!, ibl, this@scene)
                    makeRose(this@scene)
                    makeVase()
                    makeTable()
                }
            }
            shadowMap.drawNode = deferredPipeline.contentGroup
            +deferredPipeline.renderOutput
        }
    }

    fun Group.makeGlas(pbrColorOut: Texture, ibl: EnvironmentMaps, scene: Scene) {
        +mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, attribThickness)) {
            generate {
                makeGlasShaftGeometry()
            }

            isOpaque = false
            shader = glasShader(pbrColorOut, ibl)
        }

        +mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, attribThickness)) {
            generate {
                makeWineGeometry()
            }

            shader = deferredPbrShader {
                roughness = 0.0f
                emissive = Color(0.3f, 0f, 0.1f).toLinear().withAlpha(0.8f)
                useStaticAlbedo(Color(0.3f, 0f, 0.1f).mix(Color.BLACK, 0.2f).toLinear())
                //useStaticAlbedo(Color.BLACK)
            }
        }/*.also {
            scene += wireframeMesh(it.geometry, Color.MD_AMBER)
            scene += normalMesh(it.geometry, Color.MD_PINK)
        }*/

        +colorMesh {
            isOpaque = false
            generate {
                makeGlasBodyGeometry()
            }
            shader = pbrShader {
                useImageBasedLighting(ibl)
                roughness = 0f
                alphaMode = AlphaModeBlend()
            }
        }
    }

    private fun glasShader(pbrColorOut: Texture, ibl: EnvironmentMaps): PbrShader {
        val glasCfg = PbrMaterialConfig().apply {
            useRefraction(pbrColorOut)
            useImageBasedLighting(ibl)
            roughness = 0f
            alphaMode = AlphaModeBlend()
        }
        val glasModel = PbrShader.defaultPbrModel(glasCfg).apply {
            val ifThickness: StageInterfaceNode
            vertexStage {
                ifThickness = stageInterfaceNode("ifThickness", attributeNode(attribThickness).output)
            }
            fragmentStage {
                val refrSampler = findNodeByType<RefractionSamplerNode>()
                refrSampler?.inMaterialThickness = ifThickness.output
            }
        }
        return PbrShader(glasCfg, glasModel)
    }

    fun Group.makeRose(scene: Scene) {
        +colorMesh {
            generate {
                makeRoseGeometry(6415168)
                makeRoseGeometry(2541685)
                makeRoseGeometry(25)
                makeRoseGeometry(523947)
                makeRoseGeometry(1234)

                geometry.removeDegeneratedTriangles()
                geometry.generateNormals()
            }
            shader = deferredPbrShader {
                roughness = 0.7f
            }
        }
//        .also {
//            scene += wireframeMesh(it.geometry, Color.MD_AMBER)
//            //scene += normalMesh(it.geometry, Color.MD_PINK)
//        }
    }

    fun Group.makeVase() {
        +colorMesh {
            generate {
                makeVaseGeometry()
            }
            shader = deferredPbrShader {
                roughness = 0.3f
            }
        }
    }

    fun Group.makeTable() {
        +textureMesh(isNormalMapped = true) {
            isCastingShadow = false
            generate {
                makeTableGeometry()
            }

            shader = deferredPbrShader {
                useAlbedoMap("${Demo.pbrBasePath}/granitesmooth1/granitesmooth1-albedo4.jpg")
                useNormalMap("${Demo.pbrBasePath}/granitesmooth1/granitesmooth1-normal2.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/granitesmooth1/granitesmooth1-roughness3.jpg")
            }
        }
    }

    private val attribThickness = Attribute("aMatThickness", GlslType.FLOAT)
    private class ExtrudeProps(val r: Float, val h: Float, val t: Float)

    fun MeshBuilder.makeGlasShaftGeometry() {
        rotate(90f, Vec3f.NEG_X_AXIS)
        translate(7.5f, 2.5f, 0f)
        color = Color.DARK_GRAY.withAlpha(0.1f).toLinear()

        profile {
            circleShape()

            var thickness = 0.5f
            vertexModFun = {
                getFloatAttribute(attribThickness)?.f = thickness
            }

            withTransform {
                scale(4f, 4f, 1f)
                sampleAndFillBottom()
            }

            val shaftExtrude = listOf(
                    ExtrudeProps(4.1f, 0.1f, 0.5f),
                    ExtrudeProps(4.0f, 0.2f, 0.5f),
                    ExtrudeProps(2.0f, 0.5f, 0.75f),
                    ExtrudeProps(1.5f, 0.65f, 1f),
                    ExtrudeProps(1.0f, 0.95f, 1.5f),
                    ExtrudeProps(0.75f, 1.25f, 2f),
                    ExtrudeProps(0.5f, 2.0f, 5f),
                    ExtrudeProps(0.4f, 3.0f, 20f),
                    ExtrudeProps(0.4f, 7.0f, 20f),
                    ExtrudeProps(0.5f, 8.0f, 20f),
                    ExtrudeProps(0.6f, 8.25f, 2.5f),
                    ExtrudeProps(0.9f, 8.65f, 1f),
                    ExtrudeProps(1.3f, 9.1f, 1f)
            )
            shaftExtrude.forEach { ep ->
                withTransform {
                    scale(ep.r, ep.r, 1f)
                    translate(0f, 0f, ep.h)
                    thickness = ep.t
                    sample()
                }
            }
        }

        geometry.removeDegeneratedTriangles()
        geometry.generateNormals()
    }

    fun MeshBuilder.makeWineGeometry() {
        rotate(90f, Vec3f.NEG_X_AXIS)
        translate(7.5f, 2.5f, 0f)

        profile {
            circleShape()

            var thickness = 1f
            vertexModFun = {
                getFloatAttribute(attribThickness)?.f = thickness
            }

            withTransform {
                translate(0f, 0f, 10f)
                sampleAndFillBottom()
            }
            val wineExtrude = listOf(
                    ExtrudeProps(1.28f, 9.1f, 1.5f),
                    ExtrudeProps(1.48f, 9.3f, 3f),
                    ExtrudeProps(3.95f, 11.8f, 7f),
                    ExtrudeProps(4.18f, 12.1f, 7f),
                    ExtrudeProps(4.43f, 12.6f, 7f),
                    ExtrudeProps(4.58f, 13.1f, 7f),
                    ExtrudeProps(4.63f, 13.5f, 7f),
                    ExtrudeProps(4.63f, 14.5f, 7f),
                    ExtrudeProps(4.56f, 15.5f, 7f),
                    ExtrudeProps(4.56f, 15.5f, 7f),
                    ExtrudeProps(4.5f, 15.4f, 7f),
                    ExtrudeProps(3.0f, 15.4f, 7f),
                    ExtrudeProps(1.5f, 15.4f, 7f),
                    ExtrudeProps(0.5f, 15.4f, 7f)
            )
            wineExtrude.forEach { ep ->
                //color = Color.MD_PINK_300.withAlpha(0.6f * min(ep.t, 7f) / 7f).toLinear()
                color = Color(0.3f, 0f, 0.12f).withAlpha(0.9f + 0.07f * min(ep.t, 7f) / 7f).toLinear()
                withTransform {
                    scale(ep.r, ep.r, 1f)
                    translate(0f, 0f, ep.h)
                    thickness = ep.t
                    sample()
                }
            }
            fillTop()
        }

        geometry.removeDegeneratedTriangles()
        geometry.generateNormals()
    }

    fun MeshBuilder.makeGlasBodyGeometry() {
        rotate(90f, Vec3f.NEG_X_AXIS)
        translate(7.5f, 2.5f, 0f)
        color = Color.BLACK.withAlpha(0.1f).toLinear()

        profile {
            circleShape()

            val bodyExtrude = listOf(
                    ExtrudeProps(1.3f, 9.1f, 1.5f),
                    ExtrudeProps(1.5f, 9.3f, 1f),
                    ExtrudeProps(3.97f, 11.8f, 1f),
                    ExtrudeProps(4.2f, 12.1f, 1f),
                    ExtrudeProps(4.45f, 12.6f, 1f),
                    ExtrudeProps(4.6f, 13.1f, 1f),
                    ExtrudeProps(4.65f, 13.5f, 1f),
                    ExtrudeProps(4.65f, 14.5f, 1f),
                    ExtrudeProps(4.58f, 15.5f, 1f),
                    ExtrudeProps(4.42f, 17f, 1f),
                    ExtrudeProps(4.0f, 20.0f, 1f),

                    ExtrudeProps(3.5f, 23.0f, 0.5f),
                    ExtrudeProps(3.475f, 23.05f, 0.5f),
                    ExtrudeProps(3.45f, 23.0f, 0.5f),

                    ExtrudeProps(3.95f, 20.0f, 0.5f),
                    ExtrudeProps(4.37f, 17f, 0.5f),
                    ExtrudeProps(4.6f, 14.5f, 0.5f),
                    ExtrudeProps(4.6f, 13.5f, 0.5f)
            ).reversed()
            bodyExtrude.forEach { ep ->
                withTransform {
                    scale(ep.r, ep.r, 1f)
                    translate(0f, 0f, ep.h)
                    sample(inverseOrientation = true)
                }
            }
        }

        geometry.removeDegeneratedTriangles()
        geometry.generateNormals()
    }

    fun MeshBuilder.makeRoseGeometry(seed: Int, origin: Vec3f = Vec3f(-7.5f, -2.5f, 7f)) {
        withTransform {
            rotate(90f, Vec3f.NEG_X_AXIS)
            translate(origin)

            val rand = Random(seed)

            val shaftGrad = ColorGradient(0f to Color.MD_BROWN_900.toLinear(), 0.4f to Color.MD_BROWN.toLinear(), 1f to Color.MD_LIGHT_GREEN_600.toLinear())
            val blossomLeafGrad = ColorGradient(Color.MD_LIGHT_GREEN_600.toLinear(), Color.MD_LIGHT_GREEN.mix(Color.MD_YELLOW_200, 0.5f).toLinear())

            // shaft
            profile {
                circleShape(0.2f, steps = 8)

                val steps = 6
                for (i in 0 until steps) {
                    val ax = MutableVec3f(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), 0f).norm()
                    rotate(rand.randomF(0f, 15f), ax)
                    val h = rand.randomF(2f, 4f)

                    val p = i.toFloat() / steps
                    val sub = 1f / steps

                    scale(0.8f, 0.8f, 1f)
                    translate(0f, 0f, h * 0.1f)
                    color = shaftGrad.getColor(p + sub * 0.15f).mix(Color.BLACK, 0.25f)
                    sample()
                    scale(0.8f, 0.8f, 1f)
                    translate(0f, 0f, h * 0.2f)
                    color = shaftGrad.getColor(p + sub * 0.3f)
                    sample()
                    translate(0f, 0f, h * 0.4f)
                    color = shaftGrad.getColor(p + sub * 0.55f)
                    sample()
                    scale(1 / 0.8f, 1 / 0.8f, 1f)
                    translate(0f, 0f, h * 0.2f)
                    color = shaftGrad.getColor(p + sub * 0.7f).mix(Color.BLACK, 0.25f)
                    sample()
                    scale(1 / 0.8f, 1 / 0.8f, 1f)
                    translate(0f, 0f, h * 0.1f)
                    color = shaftGrad.getColor(p + sub * 0.85f).mix(Color.BLACK, 0.55f)
                    sample()
                }

                translate(0f, 0f, 0.2f)
                withTransform {
                    scale(1.5f, 1.5f, 1f)
                    sample()
                }
                translate(0f, 0f, 0.15f)
                withTransform {
                    scale(3f, 3f, 1f)
                    sample()
                }
                fillTop()
            }

            // blossom - green
            for (l in 0..5) {
                withTransform {
                    scale(0.8f, 0.8f, 0.8f)
                    rotate(60f * l, Vec3f.Z_AXIS)
                    profile {
                        val ref = mutableListOf<Vec2f>()
                        val jit = 0.03f
                        for (i in 0..10) {
                            val a = (i / 10f * 72 - 36).toRad()
                            val seam = if (i == 5) -0.05f else 0f
                            if (i == 5) {
                                val aa = a - 2f.toRad()
                                ref += Vec2f(cos(aa) - 0.7f + rand.randomF(-jit, jit), sin(aa) + rand.randomF(-jit, jit))
                            }
                            ref += Vec2f(cos(a) - 0.7f + rand.randomF(-jit, jit) + seam, sin(a) + rand.randomF(-jit, jit))
                            if (i == 5) {
                                val aa = a + 2f.toRad()
                                ref += Vec2f(cos(aa) - 0.7f + rand.randomF(-jit, jit), sin(aa) + rand.randomF(-jit, jit))
                            }
                        }

                        simpleShape(true) {
                            ref.forEach { xy(it.x * 1.05f, it.y * 1.05f) }
                            ref.reversed().forEach { xy(it.x, it.y) }
                        }

                        rotate(90f, Vec3f.Y_AXIS)
                        val scales = listOf(0.5f, 0.7f, 0.8f, 0.93f, 1f, 0.95f, 0.8f, 0.7f, 0.6f, 0.35f, 0.2f, 0.1f, 0f)
                        scales.forEachIndexed { i, s ->
                            color = blossomLeafGrad.getColor(i.toFloat() / scales.lastIndex)
                            translate(0f, 0f, 0.3f)
                            rotate(100f / scales.size + rand.randomF(-8f, 8f), Vec3f.NEG_Y_AXIS)
                            rotate(rand.randomF(-8f, 8f), Vec3f.X_AXIS)
                            withTransform {
                                scale(s, s, 1f)
                                sample()
                            }
                        }
                    }
                }
            }

            // blossom - red
            val nLeafs = 17
            for (l in 0..nLeafs) {
                withTransform {
                    val ls = l.toFloat() / nLeafs * 0.8f + 0.2f
                    scale(ls, ls, 1.2f)

                    rotate(97f * l, Vec3f.Z_AXIS)
                    profile {
                        val ref = mutableListOf<Vec2f>()
                        val jit = 0.03f
                        for (i in 0..10) {
                            val a = (i / 10f * 120 - 60).toRad()
                            ref += Vec2f(cos(a) - 0.9f + rand.randomF(-jit, jit), sin(a) + rand.randomF(-jit, jit))
                        }

                        simpleShape(true) {
                            ref.forEach { xy(it.x * 1.05f, it.y * 1.05f) }
                            ref.reversed().forEach { xy(it.x, it.y) }
                        }

                        rotate(60f, Vec3f.Y_AXIS)
                        val scales = listOf(0.5f, 0.9f, 1f, 0.93f, 0.8f, 0.7f, 0.72f, 0.8f, 0.95f, 1.05f, 1f, 0.95f, 0.85f, 0.5f)
                        scales.forEachIndexed { i, s ->
                            val js = s * rand.randomF(0.95f, 1.05f)
                            color = Color.RED.toLinear()
                            translate(0f, 0f, 0.2f)
                            val r = (0.5f - (i.toFloat() / scales.size)) * 20f
                            rotate(r + rand.randomF(-5f, 5f), Vec3f.NEG_Y_AXIS)
                            rotate(rand.randomF(-5f, 5f), Vec3f.X_AXIS)
                            withTransform {
                                scale(js, js, 1f)
                                translate((1f - js) * 0.7f, (1f - js) * 0.7f, 0f)
                                sample()
                            }
                        }
                    }
                }
            }
        }
    }

    fun MeshBuilder.makeVaseGeometry() {
        rotate(90f, Vec3f.NEG_X_AXIS)
        translate(-7.5f, -2.5f, 0f)
        scale(1.7f, 1.7f, 1.7f)
        translate(0f, 0f, 0.15f)

        val gridGrad = ColorGradient(Color.MD_BROWN, Color.MD_BLUE_300)

        val tubeColors = Array(10) { i -> gridGrad.getColor(i / 9f).mix(Color.BLACK, 0.3f) }
        val tubeGrad = ColorGradient(*tubeColors)

        profile {
            circleShape(2.2f, 60)

            withTransform {
                for (i in 0..1) {
                    val invert = i == 1

                    withTransform {
                        color = tubeGrad.getColor(0f).toLinear()

                        scale(0.97f, 0.97f, 1f)
                        sample(connect = false, inverseOrientation = invert)
                        scale(1 / 0.97f, 1 / 0.97f, 1f)
                        scale(0.96f, 0.96f, 1f)
                        sample(inverseOrientation = invert)
                        scale(1 / 0.96f, 1 / 0.96f, 1f)
                        scale(0.95f, 0.95f, 1f)
                        //sample(inverseOrientation = invert)


                        for (j in 0..20) {
                            withTransform {
                                val p = j / 20f
                                val s = 1 - sin(p * PI.toFloat()) * 0.6f
                                val t = 5f - cos(p * PI.toFloat()) * 5

                                color = tubeGrad.getColor(p).toLinear()

                                translate(0f, 0f, t)
                                scale(s, s, 1f)
                                sample(inverseOrientation = invert)
                            }
                            if (j == 0) {
                                // actually fills bottom, but with inverted face orientation
                                fillTop()
                            }
                        }

                        translate(0f, 0f, 10f)
                        scale(1 / 0.95f, 1 / 0.95f, 1f)
                        scale(0.96f, 0.96f, 1f)
                        sample(inverseOrientation = invert)
                        scale(1 / 0.96f, 1 / 0.96f, 1f)
                        scale(0.97f, 0.97f, 1f)
                        sample(inverseOrientation = invert)
                    }
                    translate(0f, 0f, -0.15f)
                    scale(1f, 1f, 10.3f / 10f)
                }
            }

            for (i in 0..1) {
                color = tubeGrad.getColor(i.toFloat()).toLinear()
                withTransform {
                    translate(0f, 0f, -0.15f + 10.15f * i)
                    scale(0.97f, 0.97f, 1f)
                    sample(connect = false)
                    scale(1f / 0.97f, 1f / 0.97f, 1f)
                    sample()
                    translate(0f, 0f, 0.03f)
                    scale(1.02f, 1.02f, 1f)
                    sample()
                    translate(0f, 0f, 0.09f)
                    sample()
                    translate(0f, 0f, 0.03f)
                    scale(1f / 1.02f, 1f / 1.02f, 1f)
                    sample()
                    scale(0.97f, 0.97f, 1f)
                    sample()
                }
            }
        }

        profile {
            simpleShape(true) {
                xy(0.8f, 1f); xy(-0.8f, 1f)
                xy(-1f, 0.8f); xy(-1f, -0.8f)
                xy(-0.8f, -1f); xy(0.8f, -1f)
                xy(1f, -0.8f); xy(1f, 0.8f)
            }

            val n = 50
            val cols = 24

            for (c in 0 until cols) {
                val rad = 2f * PI.toFloat() * c / cols
                for (i in 0..n) {
                    withTransform {
                        val p = i.toFloat() / n
                        val rot = p * 180f * if (c % 2 == 0) 1 else -1

                        color = gridGrad.getColor(p).toLinear()

                        rotate(rot, Vec3f.Z_AXIS)
                        val r = 1f + (p - 0.5f) * (p - 0.5f) * 4
                        translate(cos(rad) * r, sin(rad) * r, 0f)

                        translate(0f, 0f, p * 10f)
                        rotate(rad.toDeg(), Vec3f.Z_AXIS)
                        scale(0.05f, 0.05f, 1f)

                        sample(i != 0)
                    }
                }
            }
        }

        geometry.removeDegeneratedTriangles()
        geometry.generateNormals()
    }

    fun MeshBuilder.makeTableGeometry() {
        val tableR = 30f
        val r = 1f

        translate(0f, -r, 0f)
        rotate(90f, Vec3f.X_AXIS)

        profile {
            val shape = simpleShape(true) {
                for (a in 0..100) {
                    val rad = 2f * PI.toFloat() * a / 100
                    xy(cos(rad) * tableR, sin(rad) * tableR)
                    uv(0f, 0f)
                }
            }

            for (i in 0..15) {
                val p = i / 15f
                withTransform {
                    val h = cos((1 - p) * PI.toFloat()) * r
                    val e = sin(p * PI.toFloat()) * r
                    val s = (tableR + e) / tableR
                    val uvS = (tableR + r * p * PI.toFloat()) * 0.04f

                    shape.texCoords.forEachIndexed { i, uv -> uv.set(shape.positions[i].x, shape.positions[i].y).norm().scale(uvS) }
                    translate(0f, 0f, h)
                    scale(s, s, 1f)
                    if (i == 0) {
                        sampleAndFillBottom()
                    } else {
                        sample()
                    }
                }
            }
            fillTop()
        }

        geometry.removeDegeneratedTriangles()
        geometry.generateNormals()
    }
}