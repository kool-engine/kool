package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.TrsTransform
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.util.Color
import kotlin.math.PI

expect class Vehicle(vehicleProps: VehicleProperties, world: PhysicsWorld, pose: Mat4f = Mat4f()) : CommonVehicle {
    val forwardSpeed: Float
    val sidewaysSpeed: Float
    val longitudinalAcceleration: Float
    val lateralAcceleration: Float
    val engineSpeedRpm: Float
    val engineTorqueNm: Float
    val enginePowerW: Float
    val currentGear: Int

    var isReverse: Boolean

    fun setToRestState()
}

abstract class CommonVehicle(val vehicleProps: VehicleProperties) : RigidBody() {

    protected val mutWheelInfos = mutableListOf<WheelInfo>()
    val wheelInfos: List<WheelInfo>
        get() = mutWheelInfos

    abstract var steerInput: Float
    abstract var throttleInput: Float
    abstract var brakeInput: Float

    override fun toMesh(meshColor: Color, materialCfg: KslPbrShader.Config.() -> Unit) = ColorMesh().apply {
        generate {
            // skip first 4 (wheel-)shapes and add the chassis shapes
            color = meshColor
            for (i in 4..shapes.lastIndex) {
                val shape = shapes[i]
                withTransform {
                    transform.mul(shape.localPose)
                    shape.geometry.generateMesh(this)
                }
            }
        }
        shader = KslPbrShader {
            color { vertexColor() }
            materialCfg()
        }

        val wheelGroups = List(4) { i ->
            addColorMesh {
                generate {
                    color = Color.DARK_GRAY.toLinear()
                    shapes[i].geometry.generateMesh(this)
                }
                shader = KslPbrShader {
                    color { vertexColor() }
                    materialCfg()
                }
            }
        }

        onUpdate += {
            (transform as TrsTransform).set(this@CommonVehicle.transform)
            for (i in 0..3) {
                (wheelGroups[i].transform as TrsTransform).set(wheelInfos[i].transform)
            }
        }
    }

    companion object {
        const val FRONT_LEFT = 0
        const val FRONT_RIGHT = 1
        const val REAR_LEFT = 2
        const val REAR_RIGHT = 3

        const val OMEGA_TO_RPM = 60f / (2f * PI.toFloat())
    }
}
