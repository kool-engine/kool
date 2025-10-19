package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.noise.MultiPerlin3d
import de.fabmax.kool.pipeline.BufferedImageData3d
import de.fabmax.kool.pipeline.MipMapping
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture3d
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Wind {

    val offsetStrength = MutableVec4f(0f, 0f, 0f, 1f)
    val speed = MutableVec3f(10f, 0.5f, 6.7f)
    var scale = 100f

    val density = generateWindDensityTex()

    fun updateWind(deltaT: Float) {
        offsetStrength.x += speed.x * deltaT
        offsetStrength.y += speed.y * deltaT
        offsetStrength.z += speed.z * deltaT
    }

    private fun generateWindDensityTex(): Texture3d {
        val pt = PerfTimer()
        var min = 10f
        var max = -10f

        val perlin = MultiPerlin3d(3, 3)
        val nMinP = -0.7f
        val nMaxP = 0.7f

        val sz = 96
        val buf = Uint8Buffer(sz*sz*sz*4)
        for (z in 0 until sz) {
            for (y in 0 until sz) {
                for (x in 0 until sz) {
                    val nx = x / sz.toFloat()
                    val ny = y / sz.toFloat()
                    val nz = z / sz.toFloat()

                    val fp = perlin.eval(nx, ny, nz)
                    val n = (fp - nMinP) / (nMaxP - nMinP) * 0.9f + 0.06f
                    val c = (n * 255).roundToInt().clamp(0, 255)
                    min = min(min, n)
                    max = max(max, n)

                    buf.put(c.toByte())
                    buf.put(0.toByte())
                    buf.put(0.toByte())
                    buf.put(255.toByte())
                }
            }
        }

        for (z in 0 until sz) {
            for (y in 0 until sz) {
                for (x in 0 until sz) {
                    val i = z * sz * sz + y * sz + x
                    buf[i * 4 + 1] = buf[((sz-1-z) * sz * sz + y * sz + x) * 4]
                    buf[i * 4 + 2] = buf[(z * sz * sz + (sz-1-y) * sz + x) * 4]
                }
            }
        }
        logD { "Generated wind density in ${pt.takeSecs().toString(3)} s, tex saturation: min = $min, max = $max" }
        return Texture3d(mipMapping = MipMapping.Off, name = "wind-density") { BufferedImageData3d(buf, sz, sz, sz, TexFormat.RGBA) }
    }

    object VertexLayoutWind : Struct("VertexLayoutWind", MemoryLayout.TightlyPacked) {
        val position = include(VertexLayouts.Position.position)
        val normal = include(VertexLayouts.Normal.normal)
        val texCoord = include(VertexLayouts.TexCoord.texCoord)
        val windSensitivity = float1("attr_wind_sensitivity")
    }

    object VertexLayoutWindColored : Struct("VertexLayoutWind", MemoryLayout.TightlyPacked) {
        val position = include(VertexLayouts.Position.position)
        val normal = include(VertexLayouts.Normal.normal)
        val color = include(VertexLayouts.Color.color)
        val windSensitivity = float1("attr_wind_sensitivity")
    }
}