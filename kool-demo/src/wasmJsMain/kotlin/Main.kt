import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigWasm
import de.fabmax.kool.demo.demo

fun main() = KoolApplication(
    KoolConfigWasm(
        isGlobalKeyEventGrabbing = true,
        deviceScaleLimit = 1.5,
    )
) {
    demo(null, ctx)
}