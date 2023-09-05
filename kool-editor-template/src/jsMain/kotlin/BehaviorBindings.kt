import de.fabmax.kool.app.SampleRotationAnimator
import de.fabmax.kool.editor.AppBehavior
import de.fabmax.kool.editor.BehaviorProperty
import de.fabmax.kool.editor.api.BehaviorLoader
import de.fabmax.kool.editor.api.KoolBehavior
import kotlin.reflect.KClass

// GENERATED FILE! Do not edit manually ////////////////////////////

object BehaviorBindings : BehaviorLoader.AppBehaviorLoader {
    override fun newInstance(behaviorClassName: String): KoolBehavior {
        return when (behaviorClassName) {
            "de.fabmax.kool.app.SampleRotationAnimator" -> SampleRotationAnimator()
            else -> throw IllegalArgumentException("$behaviorClassName not mapped.")
        }
    }

    override fun getProperty(behavior: KoolBehavior, propertyName: String): Any {
        return when (behavior) {
            is SampleRotationAnimator -> getSampleRotationAnimatorProperty(behavior, propertyName)
            else -> throw IllegalArgumentException("Unknown behavior class: ${behavior::class}")
        }
    }

    override fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?) {
        when (behavior) {
            is SampleRotationAnimator -> setSampleRotationAnimatorProperty(behavior, propertyName, value)
            else -> throw IllegalArgumentException("Unknown behavior class: ${behavior::class}")
        }
    }

    private fun getSampleRotationAnimatorProperty(behavior: SampleRotationAnimator, propertyName: String): Any {
        return when (propertyName) {
            "rotationSpeed" -> behavior.rotationSpeed
            "speedMulti" -> behavior.speedMulti
            else -> throw IllegalArgumentException("Unknown parameter $propertyName for behavior class ${behavior::class}")
        }
    }

    private fun setSampleRotationAnimatorProperty(behavior: SampleRotationAnimator, propertyName: String, value: Any?) {
        when (propertyName) {
            "rotationSpeed" -> behavior.rotationSpeed = value as de.fabmax.kool.math.Vec3f
            "speedMulti" -> behavior.speedMulti = value as Float
            else -> throw IllegalArgumentException("Unknown parameter $propertyName for behavior class ${behavior::class}")
        }
    }

    val behaviorClasses = mapOf<KClass<*>, AppBehavior>(
        SampleRotationAnimator::class to AppBehavior(
            simpleName = "SampleRotationAnimator",
            qualifiedName = "de.fabmax.kool.app.SampleRotationAnimator",
            properties = listOf(
                BehaviorProperty("rotationSpeed", de.fabmax.kool.math.Vec3f::class, "Rotation speed:", -360.0, 360.0),
                BehaviorProperty("speedMulti", Float::class, "Speed multiplier:", -10.0, 10.0),
            )
        ),
    )
}
