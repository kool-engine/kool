package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.lang.getFloat4Port
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.modules.ksl.lang.x
import de.fabmax.kool.modules.ksl.lang.y
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.joints.FixedJoint
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.deferred.DeferredKslPbrShader
import de.fabmax.kool.pipeline.deferred.DeferredPointLights
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.PolyUtil
import de.fabmax.kool.util.Time
import kotlin.math.*

class GuardRail {

    val guardRailMesh: Mesh
    val signs = mutableListOf<SignInstance>()

    var isReverse = false

    private val signInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, INSTANCE_EMISSION))

    init {
        guardRailMesh = makeMesh()
    }

    fun cleanUp() {
        signs.forEach { it.joint.release() }
    }

    private fun makeMesh() = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS,
        instances = signInstances
    ).apply {
        isFrustumChecked = false

        generate {
            val caseColor = VehicleDemo.color(250)
            val panelColor = MdColor.GREY toneLin 800
            val bgColor = MdColor.GREY toneLin 900
            val poleColor = VehicleDemo.color(400)

            val emmision = MutableVec2f(0f, 0f)
            vertexModFun = {
                texCoord.set(emmision)
            }

            val arrowLtPos = mutableListOf(
                Vec2i(2, 2), Vec2i(1, 2),
                Vec2i(0, 1), Vec2i(-1, 1),
                Vec2i(-2, 0),
                Vec2i(0, -1), Vec2i(-1, -1),
                Vec2i(2, -2), Vec2i(1, -2)
            )
            val arrowRtPos = mutableListOf(
                Vec2i(-2, 2), Vec2i(-1, 2),
                Vec2i(0, 1), Vec2i(1, 1),
                Vec2i(2, 0),
                Vec2i(0, -1), Vec2i(1, -1),
                Vec2i(-2, -2), Vec2i(-1, -2)
            )

            val sz = 0.4f
            val poly = lampProto(sz, sz * 0.4f)
            for (y in -2..2) {
                for (x in -2..2) {
                    color = panelColor
                    lamp(Vec3f(x * sz, y * sz, 0.15f), poly)

                    val pos = Vec2i(x, y)
                    if (pos in arrowLtPos) {
                        emmision.x = 1f
                    }
                    if (pos in arrowRtPos) {
                        emmision.y = 1f
                    }
                    color = bgColor
                    rect {
                        size.set(sz, sz)
                        origin.set(x * sz, y * sz, 0.14f)
                    }
                    emmision.set(0f, 0f)
                }
            }

            color = caseColor
            withTransform {
                profile {
                    simpleShape(false) {
                        xy(1f, -1f)
                        xy(-1f, -1f)

                        xy(-1f, -1f)
                        xy(-1f, 1f)

                        xy(-1f, 1f)
                        xy(1f, 1f)

                        xy(1f, 1f)
                        xy(1f, -1f)
                    }
                    translate(0f, 0f, 0.15f)
                    sample()
                    translate(0f, 0f, -0.3f)
                    sample()
                    sample(connect = false)
                    fillTop()
                }
            }
            color = poleColor
            cube {
                size.set(0.1f, 2f, 0.1f)
                origin.set(0f, -1f, 0f)
            }

            geometry.generateNormals()
        }

        shader = GuardRailShader.createShader()

        onUpdate += {
            signInstances.clear()
            signInstances.addInstances(signs.size) { buf ->
                for (i in signs.indices) {
                    val sign = signs[i]
                    sign.emission.set(Vec2f.ZERO)
                    if (!sign.joint.isBroken) {
                        if (isReverse) {
                            val em = (sin((Time.gameTime + sign.iSign * 0.1) * 6).toFloat() + 0.5f).clamp(0f, 1f)
                            if (sign.isLeft) { sign.emission.x = em } else { sign.emission.y = em }
                        } else {
                            val em = (sin((-Time.gameTime + sign.iSign * 0.1) * 6).toFloat() + 0.5f).clamp(0f, 1f)
                            if (sign.isLeft) { sign.emission.y = em } else { sign.emission.x = em }
                        }
                    }
                    sign.updateInstance(buf)
                }
            }
        }
    }

    private fun MeshBuilder.lamp(center: Vec3f, poly: PolyUtil.TriangulatedPolygon) {
        val inds = mutableListOf<Int>()
        poly.vertices.forEach { v ->
            inds += vertex {
                position.set(v).add(center)
                normal.set(Vec3f.Z_AXIS)
            }
        }
        for (i in poly.indices.indices step 3) {
            val i0 = inds[poly.indices[i]]
            val i1 = inds[poly.indices[i+1]]
            val i2 = inds[poly.indices[i+2]]
            geometry.addTriIndices(i0, i1, i2)
        }
    }

    private fun lampProto(sz: Float, r: Float): PolyUtil.TriangulatedPolygon {
        val h = sz / 2f
        val pts = mutableListOf(
            MutableVec3f(-h, -h, 0f),
            MutableVec3f(h, -h, 0f),
            MutableVec3f(h, h, 0f),
            MutableVec3f(-h, h, 0f)
        )
        val hole = mutableListOf<MutableVec3f>()
        for (i in 0 until 10) {
            val a = i / 10f * 2f * PI.toFloat()
            hole += MutableVec3f(sin(a), cos(a), 0f).mul(r)
        }
        return PolyUtil.fillPolygon(pts, listOf(hole))
    }

    class SignInstance(val iSign: Int, val isLeft: Boolean, initPose: Mat4f, track: Track, world: VehicleWorld) {
        val emission = MutableVec2f()
        val actor: RigidDynamic
        val joint: FixedJoint
        val pointLight: DeferredPointLights.PointLight

        init {
            val signBox = BoxGeometry(Vec3f(2f, 2f, 0.3f))
            val poleBox = BoxGeometry(Vec3f(0.1f, 2f, 0.1f))

            actor = RigidDynamic(250f, initPose)
            actor.attachShape(Shape(signBox, world.defaultMaterial, simFilterData = world.obstacleSimFilterData, queryFilterData = world.obstacleQryFilterData))
            actor.attachShape(Shape(poleBox, world.defaultMaterial, MutableMat4f().translate(0f, -1f, 0f), world.obstacleSimFilterData, world.obstacleQryFilterData))
            actor.updateInertiaFromShapesAndMass()
            world.physics.addActor(actor)

            joint = FixedJoint(track.trackActor, actor, initPose, MutableMat4f())
            joint.setBreakForce(2e5f, 2e5f)

            pointLight = world.deferredPipeline.dynamicPointLights.addPointLight {
                color.set(MdColor.ORANGE toneLin 300)
            }
        }

        fun updateInstance(buf: Float32Buffer) {
            pointLight.intensity = max(emission.x, emission.y) * 100f
            pointLight.radius = sqrt(pointLight.intensity)
            actor.transform.transform(pointLight.position.set(0f, 0.5f, 0.1f))

            actor.transform.matrixF.putTo(buf)
            emission.putTo(buf)
        }
    }

    private class GuardRailShader(cfg: Config) : DeferredKslPbrShader(cfg) {
        companion object {
            fun createShader(): GuardRailShader {
                val cfg = Config.Builder().apply {
                    vertices { isInstanced = true }
                    color { vertexColor() }
                    emission {
                        constColor(VehicleDemo.color(500, false).mulRgb(10f))
                    }

                    modelCustomizer = {
                        val emissionFactor = interStageFloat1()

                        vertexStage {
                            main {
                                val emissionDir = vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                                val emissionInst = instanceAttribFloat2(INSTANCE_EMISSION.name)
                                val emissionLt = emissionDir.x * emissionInst.x
                                val emissionRt = emissionDir.y * emissionInst.y
                                emissionFactor.input set max(emissionLt, emissionRt)
                            }
                        }
                        fragmentStage {
                            main {
                                val emissionPort = getFloat4Port("emissionColor")
                                val color = float4Var(emissionPort.input.input)
                                emissionPort.input(color * emissionFactor.output)
                            }
                        }
                    }
                }
                return GuardRailShader(cfg.build())
            }
        }
    }

    companion object {
        private val INSTANCE_EMISSION = Attribute("instEmission", GpuType.FLOAT2)
    }
}