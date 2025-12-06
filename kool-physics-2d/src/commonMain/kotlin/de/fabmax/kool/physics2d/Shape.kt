package de.fabmax.kool.physics2d

data class ShapeDef(
    val material: SurfaceMaterial = SurfaceMaterial.DEFAULT,
    val density: Float = ShapeDefDefaults.density,
    val filter: Filter = Filter.DEFAULT,
    val isSensor: Boolean = ShapeDefDefaults.isSensor,
    val enableSensorEvents: Boolean = ShapeDefDefaults.enableSensorEvents,
    val enableContactEvents: Boolean = ShapeDefDefaults.enableContactEvents,
    val enableHitEvents: Boolean = ShapeDefDefaults.enableHitEvents,
    val enablePreSolveEvents: Boolean = ShapeDefDefaults.enablePreSolveEvents,
    val invokeContactCreation: Boolean = ShapeDefDefaults.invokeContactCreation,
    val updateBodyMass: Boolean = ShapeDefDefaults.updateBodyMass,
) {
    companion object {
        val DEFAULT = ShapeDef()
    }
}

data class Filter(
    val categoryBits: Long = ShapeDefDefaults.categoryBits,
    val groupIndex: Int = ShapeDefDefaults.groupIndex,
    val maskBits: Long = ShapeDefDefaults.maskBits,
) {
    companion object {
        val DEFAULT = Filter()
    }
}

data class SurfaceMaterial(
    val friction: Float = ShapeDefDefaults.friction,
    val restitution: Float = ShapeDefDefaults.restitution,
    val rollingResistance: Float = ShapeDefDefaults.rollingResistance,
    val tangentSpeed: Float = ShapeDefDefaults.tangentSpeed,
    val userMaterialId: Int = ShapeDefDefaults.userMaterialId,
    val customColor: Int = ShapeDefDefaults.customColor,
) {
    companion object {
        val DEFAULT = SurfaceMaterial()
    }
}

sealed interface Geometry {
    data class Circle(val radius: Float) : Geometry
    data class Square(val halfSize: Float) : Geometry
    data class Box(val halfWidth: Float, val halfHeight: Float) : Geometry
}

internal expect object ShapeDefDefaults {
    val density: Float
    val isSensor: Boolean
    val enableSensorEvents: Boolean
    val enableContactEvents: Boolean
    val enableHitEvents: Boolean
    val enablePreSolveEvents: Boolean
    val invokeContactCreation: Boolean
    val updateBodyMass: Boolean

    val categoryBits: Long
    val groupIndex: Int
    val maskBits: Long

    val friction: Float
    val restitution: Float
    val rollingResistance: Float
    val tangentSpeed: Float
    val userMaterialId: Int
    val customColor: Int
}
