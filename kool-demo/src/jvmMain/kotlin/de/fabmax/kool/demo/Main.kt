package de.fabmax.kool.demo

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.Lwjgl3VkContext
import de.fabmax.kool.platform.vk.pipeline.ShaderStage
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.UiTheme
import de.fabmax.kool.scene.ui.dp
import de.fabmax.kool.scene.ui.dps
import de.fabmax.kool.scene.ui.embeddedUi
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT

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
    val ctx = Lwjgl3VkContext(Lwjgl3Context.InitProps())
    ctx.assetMgr.assetsBaseDir = "./docs/assets"

    //ctx.scenes += uiTestScene(ctx)
    ctx.scenes += simpleTestScene(ctx)

//    ctx.scenes += debugOverlay(ctx)

    ctx.run()
}

fun uiTestScene(ctx: Lwjgl3VkContext): Scene = scene {
    defaultCamTransform()

    +embeddedUi(10f, 10f, dps(400f)) {
        theme = UiTheme.DARK_SIMPLE
        content.customTransform = { translate(-content.dp(200f), -content.dp(200f), 0f) }

//        +toggleButton("toggle-button") {
//            layoutSpec.setOrigin(pcs(15f), pcs(-25f), zero())
//            layoutSpec.setSize(pcs(70f), pcs(15f), full())
//
//            text = "Toggle Button"
//        }
//
//        +label("label") {
//            layoutSpec.setOrigin(pcs(15f), pcs(-45f), zero())
//            layoutSpec.setSize(pcs(21f), pcs(15f), full())
//
//            text = "Slider"
//        }
//
//        +slider("slider", 0.4f, 1f, 1f) {
//            layoutSpec.setOrigin(pcs(35f), pcs(-45f), zero())
//            layoutSpec.setSize(pcs(50f), pcs(15f), full())
//            padding.left = uns(0f)
//
//            onValueChanged += { value ->
//                root.content.alpha = value
//            }
//        }
//
//        +textField("text-field") {
//            layoutSpec.setOrigin(pcs(15f), pcs(-65f), zero())
//            layoutSpec.setSize(pcs(70f), pcs(15f), full())
//        }
//
//        +button("toggle-theme") {
//            layoutSpec.setOrigin(pcs(15f), pcs(-85f), zero())
//            layoutSpec.setSize(pcs(70f), pcs(15f), full())
//            text = "Toggle Theme"
//
//            onClick += { _,_,_ ->
//                if (theme == UiTheme.DARK) {
//                    theme = UiTheme.LIGHT
//                } else {
//                    theme = UiTheme.DARK
//                }
//            }
//        }
    }
}

fun simpleTestScene(ctx: Lwjgl3VkContext): Scene = scene {
    defaultCamTransform()

    val pipelineConfig = Pipeline.Builder().apply {
        vertexLayout.forMesh(colorMesh { })
        descriptorLayout.apply {
            +UniformBuffer.uboMvp()
        }
        shaderCode = ShaderCode(
                ShaderStage.fromSource("colorShader.vert", this::class.java.getResourceAsStream("/colorShader.vert"), VK_SHADER_STAGE_VERTEX_BIT),
                ShaderStage.fromSource("colorShader.frag", this::class.java.getResourceAsStream("/colorShader.frag"), VK_SHADER_STAGE_FRAGMENT_BIT))
    }

    +colorMesh {
        generator = {
            cube {
                centerOrigin()
                colorCube()
            }
        }
        pipeline = pipelineConfig.build()
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
                pipeline = pipelineConfig.build()
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

                pipeline = Pipeline.Builder().apply {
                    vertexLayout.forMesh(this@mesh)
                    descriptorLayout.apply {
                        +UniformBuffer.uboMvp()
                        +TextureSampler.Builder().apply {
                            name = "tex"
                            stages += Stage.FRAGMENT_SHADER
                        }
                    }
                    shaderCode = ShaderCode(
                            ShaderStage.fromSource("shader.vert", this::class.java.getResourceAsStream("/shader.vert"), VK_SHADER_STAGE_VERTEX_BIT),
                            ShaderStage.fromSource("shader.frag", this::class.java.getResourceAsStream("/shader.frag"), VK_SHADER_STAGE_FRAGMENT_BIT))
                }.build().apply {
                    descriptorLayout.getTextureSampler("tex").texture = Texture { assets -> assets.loadImageData("world.jpg") }
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

        pipeline = Pipeline.Builder().apply {
            vertexLayout.forMesh(this@mesh)
            descriptorLayout.apply {
                +UniformBuffer.uboMvp()
                +TextureSampler.Builder().apply {
                    name = "tex"
                    stages += Stage.FRAGMENT_SHADER
                }
            }
            shaderCode = ShaderCode(
                    ShaderStage.fromSource("shader.vert", this::class.java.getResourceAsStream("/font.vert"), VK_SHADER_STAGE_VERTEX_BIT),
                    ShaderStage.fromSource("shader-alpha.frag", this::class.java.getResourceAsStream("/font.frag"), VK_SHADER_STAGE_FRAGMENT_BIT))
        }.build().apply {
            descriptorLayout.getTextureSampler("tex").texture = font
        }
    }
}