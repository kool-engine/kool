package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.Color
import kotlin.math.PI

expect class Vehicle(vehicleProps: VehicleProperties, world: PhysicsWorld, pose: Mat4f = Mat4f()) : CommonVehicle {
    val updater: VehicleUpdater

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

abstract class CommonVehicle(val vehicleProps: VehicleProperties, pose: Mat4f) : RigidDynamic(vehicleProps.chassisMass, pose) {

    protected val mutWheelInfos = mutableListOf<WheelInfo>()
    val wheelInfos: List<WheelInfo>
        get() = mutWheelInfos

    abstract var steerInput: Float
    abstract var throttleInput: Float
    abstract var brakeInput: Float

    override fun toMesh(meshColor: Color, materialCfg: PbrMaterialConfig.() -> Unit) = group {
        val wheelGroups = List(4) { i ->
            group {
                +colorMesh {
                    generate {
                        color = Color.DARK_GRAY.toLinear()
                        shapes[i].geometry.generateMesh(this)
                    }
                    shader = pbrShader {
                        materialCfg()
                    }
                }
            }
        }
        wheelGroups.forEach { +it }

        +colorMesh {
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
            shader = pbrShader {
                materialCfg()
            }
        }
        onUpdate += {
            this@group.transform.set(this@CommonVehicle.transform)
            this@group.setDirty()
            for (i in 0..3) {
                wheelGroups[i].transform.set(wheelInfos[i].transform)
                wheelGroups[i].setDirty()
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
