package de.fabmax.kool.scene

import de.fabmax.kool.math.PI_F
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.sin

fun Node.addTriangulatedPointMesh(
    numVertices: Int = 8,
    name: String = makeChildName("TriangulatedPointMesh"),
    block: TriangulatedPointMesh.() -> Unit
): TriangulatedPointMesh {
    val mesh = TriangulatedPointMesh(numVertices, name).apply(block)
    addNode(mesh)
    return mesh
}

fun TriangulatedPointMesh(
    numVertices: Int = 8,
    name: String = Node.makeNodeName("TriangulatedPointMesh")
): TriangulatedPointMesh {
    return TriangulatedPointMesh(
        geometry = IndexedVertexList(CustomTriangulatedPointMesh.PointVertexLayout, primitiveType = PrimitiveType.TRIANGLE_STRIP),
        numVertices = numVertices,
        name = name
    )
}

typealias TriangulatedPointMesh = CustomTriangulatedPointMesh<CustomTriangulatedPointMesh.PointVertexLayout>

class CustomTriangulatedPointMesh<Layout: Struct>(
    geometry: IndexedVertexList<Layout>,
    numVertices: Int = 8,
    name: String = makeNodeName("TriangulatedPointMesh")
) : Mesh<Layout>(geometry, MeshInstanceList(PointInstanceLayout, 1024), name = name) {
    @Suppress("UNCHECKED_CAST")
    private val pointInstances: MeshInstanceList<PointInstanceLayout> get() = instances as MeshInstanceList<PointInstanceLayout>

    init {
        check(numVertices >= 3)
        isCastingShadow = false
        geometry.apply {
            repeat(numVertices) { i ->
                val a = 2f * PI_F / numVertices * i
                addVertex {
                    it.getFloat2(PointVertexLayout.posOffset.name)!!.set(cos(a), sin(a))
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
            set(it.pointPosSize,
                position.x, position.y, position.z,
                size
            )
            set(it.pointColor, color)
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

                        val pointCfg = instanceAttrib(PointInstanceLayout.pointPosSize)
                        val pointPos = float3Var(pointCfg.xyz)
                        val pointSize = float1Var(pointCfg.w)
                        val pxSize = float2Var(float2Value(1f.const / camData.viewport.z, 1f.const / camData.viewport.w))

                        color.input set instanceAttrib(PointInstanceLayout.pointColor)

                        outPosition set camData.viewProjMat * modelMat.matrix * float4Value(pointPos, 1f.const)
                        outPosition.xy += vertexAttrib(PointVertexLayout.posOffset) * outPosition.w * pointSize * pxSize
                    }
                }
                fragmentStage {
                    main { colorOutput(color.output) }
                }
            }
        }
    }

    object PointInstanceLayout : Struct("PointInstanceLayout", MemoryLayout.TightlyPacked) {
        val pointPosSize = float4("pointPosSize")
        val pointColor = float4("aPointColor")
    }

    object PointVertexLayout : Struct("PointVertexLayout", MemoryLayout.TightlyPacked) {
        val posOffset = float2("aPointVertex")
    }
}