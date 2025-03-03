package de.fabmax.kool.scene

import de.fabmax.kool.math.PI_F
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.instanceAttribFloat4
import de.fabmax.kool.pipeline.vertexAttribFloat2
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.Color
import kotlin.math.cos
import kotlin.math.sin

class TriangulatedPointMesh(
    geometry: IndexedVertexList = IndexedVertexList(listOf(ATTR_POINT_VERTEX), PrimitiveType.TRIANGLE_STRIP),
    instances: MeshInstanceList = MeshInstanceList(listOf(ATTR_POINT_POS_SZ, ATTR_POINT_COLOR)),
    numVertices: Int = 8,
    name: String = makeNodeName("TriangulatedPointMesh")
) : Mesh(geometry, instances, name = name) {

    private val pointInstances: MeshInstanceList get() = instances!!

    init {
        check(numVertices >= 3)

        isCastingShadow = false

        generate {  }

        geometry.apply {
            repeat(numVertices) { i ->
                val a = 2f * PI_F / numVertices * i
                addVertex {
                    getVec2fAttribute(ATTR_POINT_VERTEX)!!.set(Vec2f(cos(a), sin(a)))
                }
            }

            addIndices(1, 2, 0)
            var frontPtr = 3
            var backPtr = numVertices - 1
            var frontOrBack = true
            repeat(numVertices - 3) {
                if (frontOrBack) {
                    addIndex(frontPtr++)
                } else {
                    addIndex(backPtr--)
                }
                frontOrBack = !frontOrBack
            }
        }
        shader = Shader()
    }

    fun addPoint(position: Vec3f, size: Float, color: Color) {
        pointInstances.addInstance {
            position.putTo(this)
            put(size)
            color.putTo(this)
        }
    }

    fun clear() {
        pointInstances.clear()
    }

    class Shader : KslShader("triangulated-point-shader") {
        init {
            program.apply {
                val color = interStageFloat4()
                vertexStage {
                    main {
                        val camData = cameraData()
                        val modelMat = modelMatrix()

                        val pointCfg = instanceAttribFloat4(ATTR_POINT_POS_SZ)
                        val pointPos = float3Var(pointCfg.xyz)
                        val pointSize = float1Var(pointCfg.w)
                        val pxSize = float2Var(float2Value(1f.const / camData.viewport.z, 1f.const / camData.viewport.w))

                        color.input set instanceAttribFloat4(ATTR_POINT_COLOR)

                        outPosition set camData.viewProjMat * modelMat.matrix * float4Value(pointPos, 1f.const)
                        outPosition.xy += vertexAttribFloat2(ATTR_POINT_VERTEX) * outPosition.w * pointSize * pxSize
                    }
                }
                fragmentStage {
                    main { colorOutput(color.output) }
                }
            }
        }
    }

    companion object {
        val ATTR_POINT_VERTEX = Attribute("aPointVertex", GpuType.Float2)
        val ATTR_POINT_POS_SZ = Attribute("aPointPos", GpuType.Float4)
        val ATTR_POINT_COLOR = Attribute("aPointColor", GpuType.Float4)
    }
}