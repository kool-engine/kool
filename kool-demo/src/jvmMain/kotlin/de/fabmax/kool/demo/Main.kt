package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shading.BasicMeshShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
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
//    ctx.scenes += simpleTestScene(ctx)
    ctx.scenes += debugOverlay(ctx)

    ctx.run()
}

fun uiTestScene(ctx: KoolContext): Scene {
    val embd = scene {
        defaultCamTransform()
        +embeddedUi(10f, 10f, dps(400f)) {
            setupUi()
        }
    }
    return embd
}

fun UiRoot.setupUi() {
    theme = theme(UiTheme.DARK_SIMPLE) {
        //containerUi { SimpleComponentUi(it) }
    }
    +label("label") {
        layoutSpec.setOrigin(zero(), zero(), zero())
        layoutSpec.setSize(dps(200f), dps(40f), full())

        text = "Hello World"
    }
}

fun simpleTestScene(ctx: KoolContext): Scene = scene {
    defaultCamTransform()

    +colorMesh {
        generator = {
//            for (i in -5 .. 5) {
//                cube {
//                    origin.x = i * 20f
//                    origin.z = 1000f
//                    size.set(10f, 10f, 1f)
//                    colorCube()
//                }
//
//                cube {
//                    origin.y = i * 20f
//                    origin.z = -999f
//                    size.set(10f, 10f, 1.000f)
//                    colorCube()
//                }
//            }

            cube {
                centerOrigin()
                colorCube()
            }
        }
        pipelineConfig { shaderLoader = BasicMeshShader.VertexColor.loader }
    }

    +transformGroup {
        translate(3f, 0f, 0f)

        +transformGroup {
            +colorMesh {
                generator = {
                    cube {
                        centerOrigin()
                        colorCube()
                    }
                }
                pipelineConfig { shaderLoader = BasicMeshShader.VertexColor.loader }
            }
            onPreRender += { ctx ->
                rotate(90f * ctx.deltaT, 0f, 1f, 0f)
            }

            +mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)) {
                generator = {
                    cube {
                        origin.set(0f, 2f, 0f)
                    }
                }

                pipelineConfig {
                    shaderLoader = BasicMeshShader.TextureColor.loader
                    onPipelineCreated += {
                        (it.shader as BasicMeshShader.TextureColor).textureSampler.texture = Texture { assets -> assets.loadImageData("world.jpg") }
                    }
                }
            }
        }
    }

    val font = Font(FontProps(Font.SYSTEM_FONT, 72f, Font.PLAIN), ctx)
    +mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) {
        generator = {
            color = Color.LIME
            text(font, 1f) {
                // Set the text to render, for now only characters defined in [Font.STD_CHARS] can be rendered
                text = "kool@Vulkan!"
                // Make the text centered
                origin.set(-font.textWidth(text) / 2f, 0f, 200f)
            }
        }

        pipelineConfig {
            shaderLoader = BasicMeshShader.MaskedColor.loader
            onPipelineCreated += {
                (it.shader as BasicMeshShader.MaskedColor).textureSampler.texture = font
            }
        }
    }
}