package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.VertexTransformBlock
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.lang.gt
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.vertexAttribFloat3
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.Color
import kotlin.math.abs
import kotlin.math.cos

class AxisRotationHandle(
    val color: Color,
    val axis: GizmoHandle.Axis,
    override val gizmoOperation: GizmoOperation = AxisRotation(axis),
    val coveredColor: Color = color.withAlpha(0.7f),
    val colorIdle: Color = color.mulRgb(0.8f),
    val coveredColorIdle: Color = colorIdle.withAlpha(0.7f),
    radius: Float = 0.8f,
    name: String = "axis-rotation-handle"
) : Node(name), GizmoHandle {

    override val drawNode: Node
        get() = this
    override var isHidden: Boolean = false
        set(value) {
            field = value
            drawNode.isVisible = !value
        }

    private val hitMesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-hitMesh")
    private val mesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-mesh")
    private val coveredMesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-coveredMesh")

    private var isHovered = false
    private var alphaFactor = 1f

    init {
        transform = TrsTransformD().apply {
            rotation.set(axis.orientation)
            markDirty()
        }

        mesh.isPickable = false
        coveredMesh.isPickable = false
        hitMesh.rayTest = HandleHitTest(hitMesh)

        mesh.setupGeometry(radius, 0.015f)
        mesh.setupShader(DepthCompareOp.LESS)
        coveredMesh.setupGeometry(radius, 0.015f)
        coveredMesh.setupShader(DepthCompareOp.ALWAYS)
        hitMesh.setupGeometry(radius, 0.05f)

        // hasChanged flag is usually cleared after mesh is drawn the first time, but hitMesh is never drawn
        // -> clear flag manually to avoid hitTest kd-tree being regenerated every frame
        hitMesh.geometry.hasChanged = false

        addNode(coveredMesh)
        addNode(mesh)
        addNode(hitMesh)

        val camDelta = MutableVec3d()
        val alphaThreshHigh = cos(75f.deg.rad)
        val alphaThreshLow = cos(85f.deg.rad)

        onUpdate {
            parent?.invModelMatD?.transform(camDelta.set(it.camera.dataD.globalPos), 1.0)
            val cosAngle = abs(camDelta.norm() dot axis.axis).toFloat()
            alphaFactor = if (cosAngle < alphaThreshHigh) {
                smoothStep(alphaThreshLow, alphaThreshHigh, cosAngle)
            } else {
                1f
            }

            isVisible = !isHidden && alphaFactor > 0.01f
            updateColors()
        }
    }

    override fun moveVirtualPointer(pos: MutableVec2f, ptr: Pointer, speedMod: Float) {
        pos.set(ptr.pos)
    }

    private fun updateColors() {
        var mainColor: Color = if (isHovered) color else colorIdle
        var coveredColor: Color = if (isHovered) coveredColor else coveredColorIdle

        if (alphaFactor > 0f && isHovered) {
            alphaFactor = 1f
        }
        if (alphaFactor != 1f) {
            mainColor = mainColor.withAlpha(mainColor.a * alphaFactor)
            coveredColor = coveredColor.withAlpha(coveredColor.a * alphaFactor)
        }

        (mesh.shader as KslUnlitShader).color = mainColor
        (coveredMesh.shader as KslUnlitShader).color = coveredColor
    }

    override fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        isHovered = true
    }

    override fun onHoverExit(gizmo: GizmoNode) {
        isHovered = false
    }

    private fun Mesh.setupGeometry(
        orbitRadius: Float,
        geomRadius: Float,
    ) {
        isCastingShadow = false
        generate {
            rotate(90f.deg, Vec3f.Z_AXIS)
            profile {
                circleShape(geomRadius, 6)
                val normal = MutableVec3f()
                vertexModFun = { this.normal.set(normal) }

                val n = 60
                for (i in 0..n) {
                    withTransform {
                        rotate((360f * i / n).deg, Vec3f.NEG_Y_AXIS)
                        translate(orbitRadius, 0f, 0f)
                        transform.transform(normal.set(Vec3f.ZERO))
                        sample()
                    }
                }
            }
        }
    }

    private fun Mesh.setupShader(depthCompareOp: DepthCompareOp) {
        shader = KslUnlitShader {
            pipeline {
                depthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.ALWAYS) {
                    isWriteDepth = false
                }
            }
            color { uniformColor() }
            modelCustomizer = {
                val normal = interStageFloat3()
                vertexStage {
                    main {
                        findBlock<VertexTransformBlock>()?.apply {
                            inLocalNormal(vertexAttribFloat3(Attribute.NORMALS))
                            normal.input set outWorldNormal
                        }
                    }
                }
                fragmentStage {
                    main {
                        `if`(dot(cameraData().direction, normal.output) gt 0.1f.const) {
                            discard()
                        }
                    }
                }
            }
        }
    }

    private inner class HandleHitTest(hitMesh: Mesh) : MeshRayTest {
        private val triTest = MeshRayTest.geometryTest(hitMesh)
        private val proxyTest = RayTest()

        override fun onMeshDataChanged(mesh: Mesh) = triTest.onMeshDataChanged(mesh)

        override fun rayTest(test: RayTest, localRay: RayF): Boolean {
            proxyTest.clear()
            proxyTest.ray.set(test.ray)
            if (triTest.rayTest(proxyTest, localRay)) {
                val isFrontHit = test.camera?.let { sceneCam ->
                    val normal = (proxyTest.hitPositionGlobal.toVec3f() - globalCenter).normed()
                    normal dot sceneCam.globalLookDir < 0.1f
                } ?: true

                if (isFrontHit) {
                    proxyTest.hitNode?.let { test.setHit(it, proxyTest.hitPositionGlobal) }
                }
                return isFrontHit

            } else {
                return false
            }
        }

    }
}