package de.fabmax.kool.physics2d

import box2dandroid.B2_Shape
import box2dandroid.b2ShapeDef

internal actual object ShapeDefDefaults {
    private val defaults = b2ShapeDef().also { B2_Shape.defaultShapeDef(it) }

    actual val density: Float = defaults.density
    actual val isSensor: Boolean = defaults.isSensor
    actual val enableSensorEvents: Boolean = defaults.enableSensorEvents
    actual val enableContactEvents: Boolean = defaults.enableContactEvents
    actual val enableHitEvents: Boolean = defaults.enableHitEvents
    actual val enablePreSolveEvents: Boolean = defaults.enablePreSolveEvents
    actual val invokeContactCreation: Boolean = defaults.invokeContactCreation
    actual val updateBodyMass: Boolean = defaults.updateBodyMass

    actual val categoryBits: Long = defaults.filter.categoryBits
    actual val groupIndex: Int = defaults.filter.groupIndex
    actual val maskBits: Long = defaults.filter.maskBits

    actual val friction: Float = defaults.material.friction
    actual val restitution: Float = defaults.material.restitution
    actual val rollingResistance: Float = defaults.material.rollingResistance
    actual val tangentSpeed: Float = defaults.material.tangentSpeed
    actual val userMaterialId: Int = defaults.material.userMaterialId
    actual val customColor: Int = defaults.material.customColor
}