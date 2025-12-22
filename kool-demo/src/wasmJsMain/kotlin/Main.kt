import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigWasm
import de.fabmax.kool.demo.demo
import de.fabmax.kool.physics2d.Physics2d

fun main() = KoolApplication(
    KoolConfigWasm(
        isGlobalKeyEventGrabbing = true,
        deviceScaleLimit = 1.5,
    )
) {
    Physics2d.loadPhysics2d()
    demo(null, ctx)
}