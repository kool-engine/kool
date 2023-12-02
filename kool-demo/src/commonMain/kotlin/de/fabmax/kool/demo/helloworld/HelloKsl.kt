package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.lang.plus
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.modules.ksl.lang.xyz
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.vertexAttribFloat3
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Time
import kotlin.math.sin

class HelloKsl : DemoScene("Hello KSL Shaders") {

    override fun Scene.setupMainScene(ctx: KoolContext) {
        // create the shader
        val helloKslShader = KslShader("Example KSL shader") {
            // uniforms are used to pass data from the host (CPU) into the shader (GPU)
            val uScale = uniformFloat1("uScale")

            // inter stage variables are used to transfer variables from vertex to fragment shader (with interpolation)
            val interNormal = interStageFloat3()

            vertexStage {
                // vertex shader main function: Is executed once per mesh vertex and transforms the vertex attributes
                main {
                    val modelMat = modelMatrix()
                    val camData = cameraData()

                    val position = float3Var(vertexAttribFloat3(Attribute.POSITIONS))
                    val normal = float3Var(vertexAttribFloat3(Attribute.NORMALS))

                    position set position * uScale
                    normal set normalize(normal)

                    interNormal.input set (modelMat.matrix * float4Value(normal, 0f.const)).xyz

                    outPosition set camData.viewProjMat * modelMat.matrix * float4Value(position, 1f.const)
                }
            }

            fragmentStage {
                // fragment shader main function: Is executed once per pixel to compute the output color
                main {
                    val normalColor = float3Var(interNormal.output * 0.5f.const + 0.5f.const)
                    colorOutput(normalColor * uScale, 1f.const)
                }
            }
        }

        addMesh(Attribute.POSITIONS, Attribute.NORMALS) {
            shader = helloKslShader

            // connect to the shader uniform (the names need to match)
            var shaderScale by helloKslShader.uniform1f("uScale")
            onUpdate {
                // update shader uniform on each frame
                shaderScale = sin(Time.gameTime * 2).toFloat() * 0.25f + 0.75f
                transform.rotate(45f.deg * Time.deltaT, Vec3f(0.5f, 1f, 0f))
            }

            generate {
                cube { size.set(3f, 3f, 3f) }
            }
        }

        defaultOrbitCamera()
    }
}