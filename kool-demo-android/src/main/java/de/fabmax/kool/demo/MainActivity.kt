package de.fabmax.kool.demo

import de.fabmax.kool.platform.AndroidRenderContext
import de.fabmax.kool.platform.KoolActivity

class MainActivity : KoolActivity() {
    override fun onKoolContextCreated(ctx: AndroidRenderContext) {
        super.onKoolContextCreated(ctx)
        Demo(ctx, "modelDemo")

        /*for (r in 1..3) {
            val n = 1000_000

            var buf = FloatBuffer.allocate(n)
            timedMs("FloatBuffer took") {
                for (i in 0 until n) {
                    buf.put(i, i.toFloat())
                }
            }

            buf = ByteBuffer.allocateDirect(n * 4).asFloatBuffer()
            timedMs("Direct FloatBuffer took") {
                for (i in 0 until n) {
                    buf.put(i, i.toFloat())
                }
            }

            val arr = FloatArray(n)
            timedMs("FloatArray took") {
                for (i in 0 until n) {
                    arr[i] = i.toFloat()
                }
            }
        }*/
    }
}
