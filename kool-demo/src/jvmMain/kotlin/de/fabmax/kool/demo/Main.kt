package de.fabmax.kool.demo

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.Lwjgl3VkContext
import de.fabmax.kool.platform.vk.pipeline.ShaderStage
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.Attribute
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT

/**
 * @author fabmax
 */
fun main() {
//    Demo.setProperty("assetsBaseDir", "./docs/assets")
//    // launch demo
//    demo("modelDemo")

//    VkSystem(VkSetup().apply { isValidating = true }, VkTestScene()).run()
    testScene()
}

fun testScene() {
    val ctx = Lwjgl3VkContext(Lwjgl3Context.InitProps())
    ctx.assetMgr.assetsBaseDir = "./docs/assets"

    ctx.scenes += scene {
        defaultCamTransform()

        val pipelineConfig = Pipeline.Builder().apply {
            vertexLayout.forMesh(colorMesh { })
            descriptorLayout.apply {
                +UniformBuffer.Builder().apply {
                    stages += Stage.VERTEX_SHADER
                    +{ UniformMat4f("model") }
                    +{ UniformMat4f("view") }
                    +{ UniformMat4f("proj") }

                    onUpdate = { ubo, cmd ->
                        ubo.updateMvp(0, 1, 2, cmd)
                    }
                }
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
            }
        }

        +mesh(setOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)) {
            generator = {
                vertexModFun = {
                    normal.set(1f, 1f, 1f)
                }
                cube {
                    origin.set(0f, 2f, 0f)
                }
            }


            pipeline = Pipeline.Builder().apply {
                vertexLayout.forMesh(this@mesh)
                descriptorLayout.apply {
                    +UniformBuffer.Builder().apply {
                        stages += Stage.VERTEX_SHADER
                        +{ UniformMat4f("model") }
                        +{ UniformMat4f("view") }
                        +{ UniformMat4f("proj") }

                        onUpdate = { ubo, cmd ->
                            ubo.updateMvp(0, 1, 2, cmd)
                        }
                    }
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

//    ctx.scenes += debugOverlay(ctx)

    ctx.run()
}