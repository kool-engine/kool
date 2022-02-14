package de.fabmax.kool.demo.demos

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.modules.kslx.KslProgram
import de.fabmax.kool.modules.kslx.times
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.util.MdColor

class KslShaderTest : DemoScene("KslShader") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        +colorMesh {
            generate {
                color = MdColor.LIGHT_GREEN
                rect {
                    zeroTexCoords()
                }
            }
            shader = KslProgram("KslTestShader").apply {
                val ifColor = interStageFloat4()

                vertexShader {
                    val mvp = uniformMat4("uMvp")
                    val inPosition = vertexAttributeFloat3(Attribute.POSITIONS)

                    main {
                        ifColor `=` vertexAttributeFloat4(Attribute.COLORS)
                        outPosition `=` mvp * constFloat4(inPosition, 1f)
                    }
                }

                fragmentShader {
                    main {
                        outColor() `=` ifColor
                    }
                }

            }.buildShader().apply {
                onUpdate += { cmd ->
                    (uniforms["uMvp"] as? UniformMat4f)?.value?.set(cmd.mvpMat)
                }
            }
        }
    }
}