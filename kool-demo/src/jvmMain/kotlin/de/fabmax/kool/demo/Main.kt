package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.debugOverlay

/**
 * @author fabmax
 */
fun main() {
//    Demo.setProperty("assetsBaseDir", "./docs/assets")
//    // launch demo
//    demo("modelDemo")

    testScene()
}

fun testScene() {
    val ctx = createDefaultContext()
    ctx.assetMgr.assetsBaseDir = "./docs/assets"

//    ctx.scenes += uiTestScene(ctx)
    ctx.scenes += simpleTestScene(ctx)
    ctx.scenes += debugOverlay(ctx)

    ctx.run()
}

fun simpleTestScene(ctx: KoolContext): Scene = scene {
    defaultCamTransform()

    ctx.clearColor = Color.fromHsv(0f, 0f, 0.15f, 1f)

    val cyanLight = Light().setSpot(Vec3f(10f, 7f, -10f), Vec3f.X_AXIS, 45f).setColor(Color.MD_CYAN, 300f)
    val redLight = Light().setSpot(Vec3f(-10f, 7f, 10f), Vec3f.X_AXIS, 45f).setColor(Color.MD_RED, 300f)
//    val cyanLight = Light().setSpot(Vec3f(10f, 7f, -10f), Vec3f.X_AXIS, 45f).setColor(Color.MD_CYAN, 150f)
//    val redLight = Light().setSpot(Vec3f(-10f, 7f, 10f), Vec3f.X_AXIS, 45f).setColor(Color.MD_RED, 150f)
//    val yellowLight = Light().setSpot(Vec3f(-10f, 7f, -10f), Vec3f.X_AXIS, 45f).setColor(Color.MD_AMBER, 150f)
//    val greenLight = Light().setSpot(Vec3f(10f, 7f, 10f), Vec3f.X_AXIS, 45f).setColor(Color.MD_GREEN, 150f)

    lighting.lights.clear()
    lighting.lights.add(cyanLight)
    lighting.lights.add(redLight)
//    lighting.lights.add(yellowLight)
//    lighting.lights.add(greenLight)

    +LightMesh(cyanLight)
    +LightMesh(redLight)
//    +LightMesh(yellowLight)
//    +LightMesh(greenLight)

    +transformGroup {
//        +colorMesh {
//            generator = {
//                color = Color.GRAY
//                cube {
//                    centerOrigin()
//                    //colorCube()
//                }
//            }
//            pipelineConfig {
//                //shaderLoader = ModeledShader.staticColor()
//                //shaderLoader = ModeledShader.vertexColorPhong()
//                shaderLoader = ModeledShader.vertexColorPhongMultiLight()
//            }
//
////            onPreRender += { ctx ->
////                getPipeline(ctx)?.let {
////                    (it.shader as ModeledShader.StaticColor).uStaticColor.value.set(Color.fromHsv((ctx.time.toFloat() * 60f) % 360f, 0.8f, 1f, 1f))
////                }
////            }
//        }

        translate(0f, 1.5f, 0f)

        +colorMesh {
            generator = {
                color = Color.GRAY
                withTransform {
                    translate(0f, -3.75f, 0f)
                    rotate(90f, Vec3f.NEG_X_AXIS)
                    rect {
                        size.set(50f, 50f)
                        origin.set(-size.x / 2, -size.y/2, 0f)
                    }
                }
            }
            pipelineConfig { shaderLoader = ModeledShader.vertexColorPhongMultiLight() }
        }

        loadModel(ctx.assetMgr) { model ->
            println("adding mesh: ${model.meshData.numVertices} verts, ${model.meshData.numIndices / 3} tris")

            val colorMesh = mesh(setOf(Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS)) {
                pipelineConfig { shaderLoader = ModeledShader.vertexColorPhongMultiLight() }

                val target = meshData
                target.vertexList.addFrom(model.meshData.vertexList)
                target.vertexList.forEach { it.color.set(Color.GRAY) }
            }
            +colorMesh
            println("done")
        }

        onPreRender += { ctx ->
            rotate(10f * ctx.deltaT, 0f, 1f, 0f)
        }
    }

//    +colorMesh {
//        generator = {
//            color = Color.MD_RED
//            sphere {
//                radius = 0.2f
//                center.set(-10f, 5f, 5f)
//            }
//            color = Color.MD_CYAN
//            sphere {
//                radius = 0.2f
//                center.set(10f, 5f, 5f)
//            }
//        }
//        pipelineConfig { shaderLoader = ModeledShader.vertexColor() }
//    }

//    +transformGroup {
//        translate(3f, 0f, 0f)
//
//        +transformGroup {
//            +mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)) {
//                generator = {
//                    cube {
//                        origin.set(0f, 2f, 0f)
//                    }
//                }
//
//                pipelineConfig {
//                    //shaderLoader = BasicMeshShader.TextureColor.loader
//                    shaderLoader = ModeledShader.textureColor()
//                    onPipelineCreated += {
//                        (it.shader as ModeledShader.TextureColor).textureSampler.texture = Texture { assets -> assets.loadImageData("world.jpg") }
//                    }
//                }
//            }
//
//            onPreRender += { ctx ->
//                rotate(90f * ctx.deltaT, 0f, 1f, 0f)
//            }
//        }
//    }

//    val font = Font(FontProps(Font.SYSTEM_FONT, 72f, Font.PLAIN), ctx)
//    +mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) {
//        generator = {
//            color = Color.LIME
//            text(font, 1f) {
//                // Set the text to render, for now only characters defined in [Font.STD_CHARS] can be rendered
//                text = "kool@Vulkan!"
//                // Make the text centered
//                origin.set(-font.textWidth(text) / 2f, 0f, 200f)
//            }
//        }
//
//        pipelineConfig {
//            shaderLoader = BasicMeshShader.MaskedColor.loader
//            onPipelineCreated += {
//                (it.shader as BasicMeshShader.MaskedColor).textureSampler.texture = font
//            }
//        }
//    }
}

class LightMesh(val light: Light) : TransformGroup() {
    private val lightPos = Vec3f(light.position)
    private val rotOff = randomF(0f, 3f)
    init {
        val lightMesh = colorMesh {
            generator = {
                color = light.color.withAlpha(1f)
                sphere {
                    radius = 0.2f
                }
            }
            pipelineConfig { shaderLoader = ModeledShader.vertexColor() }
        }
        +lightMesh

        onPreRender += { ctx ->
            setIdentity()
            rotate(ctx.time.toFloat() * -20f, Vec3f.Y_AXIS)
            translate(lightPos)
            light.position.set(lightMesh.globalCenter)
            light.direction.set(lightMesh.globalCenter).scale(-1f).norm()

//            val r = cos(ctx.time / 15 + rotOff).toFloat()
//            light.direction.rotate(r * 10f, Vec3f.X_AXIS)
//            light.direction.rotate(r * 10f, Vec3f.Z_AXIS)
        }
    }
}

fun loadModel(assetMgr: AssetManager, recv: (Mesh) -> Unit) {
    assetMgr.loadModel("bunny.kmfz") { model ->
        val scale = 0.05f
        if (model != null) {
            val mesh = model.meshes[0].toMesh()
            mesh.meshData.vertexList.forEach {
                it.position.scale(scale)
            }
            recv(mesh)
        }
    }
}