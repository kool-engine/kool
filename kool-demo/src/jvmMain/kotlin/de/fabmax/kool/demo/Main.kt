package de.fabmax.kool.demo

import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.Lwjgl3VkContext
import de.fabmax.kool.platform.vk.pipeline.ShaderStage
import de.fabmax.kool.platform.vk.pipeline.SpirvShaderCode
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.transformGroup
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT

/**
 * @author fabmax
 */
fun main() {
    Demo.setProperty("assetsBaseDir", "./docs/assets")
    // launch demo
    //demo("modelDemo")

//    VkSystem(VkSetup().apply { isValidating = true }, VkTestScene()).run()
    testScene()
}

fun testScene() {
    //val ctx = createDefaultContext()
    val ctx = Lwjgl3VkContext(Lwjgl3Context.InitProps())

    ctx.scenes += scene {
        defaultCamTransform()

        +colorMesh {
            generator = {
                cube {
                    centerOrigin()
                    colorCube()
                }
            }

            pipelineConfig = pipelineConfig {
                shaderCode = SpirvShaderCode(
                        ShaderStage.fromSource("colorShader.vert", this::class.java.getResourceAsStream("/colorShader.vert"), VK_SHADER_STAGE_VERTEX_BIT),
                        ShaderStage.fromSource("colorShader.frag", this::class.java.getResourceAsStream("/colorShader.frag"), VK_SHADER_STAGE_FRAGMENT_BIT))
            }
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

                    pipelineConfig = pipelineConfig {
                        shaderCode = SpirvShaderCode(
                                ShaderStage.fromSource("colorShader.vert", this::class.java.getResourceAsStream("/colorShader.vert"), VK_SHADER_STAGE_VERTEX_BIT),
                                ShaderStage.fromSource("colorShader.frag", this::class.java.getResourceAsStream("/colorShader.frag"), VK_SHADER_STAGE_FRAGMENT_BIT))
                    }
                }
                onPreRender += { ctx ->
                    rotate(90f * ctx.deltaT, 0f, 1f, 0f)
                }
            }
        }
    }

//    ctx.scenes += debugOverlay(ctx)

    ctx.run()
}