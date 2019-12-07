package de.fabmax.kool.demo

import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shading.BasicMeshShader
import de.fabmax.kool.platform.Lwjgl3ContextGL
import de.fabmax.kool.platform.Lwjgl3ContextVk
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps

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
    val ctx = Lwjgl3ContextVk(Lwjgl3ContextGL.InitProps())
    ctx.assetMgr.assetsBaseDir = "./docs/assets"

//    ctx.scenes += uiTestScene(ctx)
    ctx.scenes += simpleTestScene(ctx)

//    ctx.scenes += debugOverlay(ctx)

    ctx.run()
}

fun uiTestScene(ctx: Lwjgl3ContextVk): Scene = scene {
    defaultCamTransform()

//    val mesh = Mesh(MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS))
//    mesh.generator = {
//        color = Color.CYAN
//        rect {
//            size.set(10f, 10f)
//            fullTexCoords()
//        }
//    }
//    mesh.pipelineConfig { shaderLoader = BasicMeshShader.VertexColor.loader }
//    mesh.generateGeometry()
//    +mesh

    +embeddedUi(10f, 10f, dps(400f)) {
        theme = theme(UiTheme.DARK_SIMPLE) {
            //containerUi { SimpleComponentUi(it) }
        }
        content.customTransform = { translate(-content.dp(200f), -content.dp(200f), 0f) }
        +label("label") {
            layoutSpec.setOrigin(pcs(15f), pcs(-45f), zero())
            layoutSpec.setSize(pcs(21f), pcs(15f), full())

            text = "Slider"
        }
    }
}

fun simpleTestScene(ctx: Lwjgl3ContextVk): Scene = scene {
    defaultCamTransform()

    +colorMesh {
        generator = {
            cube {
                centerOrigin()
                colorCube()
            }

            cube {
                centerOrigin()
                origin.x = 1.2f
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