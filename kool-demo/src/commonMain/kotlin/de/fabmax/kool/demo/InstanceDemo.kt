package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.MeshInstanceList

fun instanceDemo(ctx: KoolContext) = scene {
    +orbitInputTransform {
        +camera.apply {
            this as PerspectiveCamera
            clipNear = 1f
            clipFar = 500f
        }
        minZoom = 1f
        maxZoom = 250f
        zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
        zoom = 150f

        setMouseRotation(30f, -40f)
    }

    +colorMesh {
        generate {
            cube {
                colored(false)
                centered()
            }
        }

        val n = 20
        val n3 = n * n * n

        val instanceList = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT), n3)
        instances = instanceList

        val rotAxis = mutableListOf<MutableVec3f>()
        val offsets = mutableListOf<MutableVec3f>()
        for (x in 0 until n) {
            for (y in 0 until n) {
                for (z in 0 until n) {
                    offsets += MutableVec3f((x - n * 0.5f) * 5f + randomF(-2f, 2f), (y - n * 0.5f) * 5f + randomF(-2f, 2f), (z - n * 0.5f) * 5f + randomF(-2f, 2f))
                    rotAxis += MutableVec3f(randomF(-1f, 1f), randomF(-1f, 1f), randomF(-1f, 1f))
                }
            }
        }

        onPreRender += {
            instanceList.apply {
                clear()
                for (i in 0 until n3) {
                    addInstance {
                        put(Mat4f()
                                .translate(offsets[i])
                                .rotate(ctx.time.toFloat() * 120 * rotAxis[i].length(), rotAxis[i])
                                .matrix)
                    }
                }
            }
        }

        pipelineLoader = ModeledShader(ShaderModel().apply {
            val ifColors: StageInterfaceNode
            vertexStage {
                ifColors = stageInterfaceNode("ifColors", attrColors().output)

                val instModelMat = instanceAttrModelMat().output
                val mvpMat = premultipliedMvpNode().outMvpMat
                val mvp = multiplyNode(mvpMat, instModelMat).output
                positionOutput = vertexPositionNode(attrPositions().output, mvp).outPosition
            }
            fragmentStage {
                colorOutput = unlitMaterialNode(ifColors.output).outColor
            }
        })
    }
}
