package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.vertexAttribFloat3
import de.fabmax.kool.pipeline.vertexAttribFloat4
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.defaultOrbitCamera

class HelloWorld : DemoScene("Hello World") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        addColorMesh {
            generate {
                cube {
                    colored()
                }
            }
            shader = KslShader("Hello world shader") {
                val interStageColor = interStageFloat4()

                vertexStage {
                    main {
                        val mvp = mvpMatrix()
                        val localPosition = float3Var(vertexAttribFloat3(Attribute.POSITIONS))
                        outPosition set mvp.matrix * float4Value(localPosition, 1f.const)
                        interStageColor.input set vertexAttribFloat4(Attribute.COLORS)
                    }
                }
                fragmentStage {
                    main {
                        colorOutput(interStageColor.output)
                    }
                }
            }
        }
    }
}