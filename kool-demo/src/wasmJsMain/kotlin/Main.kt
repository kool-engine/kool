import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigWasm
import de.fabmax.kool.demo.demo
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics2d.Physics2d
import de.fabmax.kool.pipeline.backend.webgpu.RenderBackendWebGpu

fun main() = KoolApplication(
    KoolConfigWasm(
        renderBackend = RenderBackendWebGpu,
        isGlobalKeyEventGrabbing = true,
        deviceScaleLimit = 1.5,
    )
) {
    Physics.loadPhysics()
    Physics2d.loadPhysics2d()
    demo(null, ctx)
}