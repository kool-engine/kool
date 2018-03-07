package de.fabmax.kool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.fabmax.kool.gl.GL_RGB
import de.fabmax.kool.gl.GL_RGBA
import de.fabmax.kool.gl.GL_UNSIGNED_BYTE
import de.fabmax.kool.gl.glTexImage2D
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import kotlinx.coroutines.experimental.launch
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import kotlin.math.round

class ImageTextureData(assetPath: String, context: Context) : TextureData() {
    private var buffer: Uint8Buffer? = null
    private var format = 0

    init {
        launch {
            try {
                val inputStream = if (assetPath.startsWith("http")) {
                    URL(assetPath).openStream()
                } else {
                    context.assets.open(assetPath)
                }

                inputStream.use {
                    val t = System.nanoTime()

                    val opts = BitmapFactory.Options()
                    opts.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val bitmap = BitmapFactory.decodeStream(it, null, opts)
                    buffer = convertBitmapToBuffer(bitmap)
                    bitmap.recycle()

                    format = if (bitmap.hasAlpha()) GL_RGBA else GL_RGB
                    width = bitmap.width
                    height = bitmap.height
                    isAvailable = true

                    Log.d("KoolActivity", "Loaded texture asset \"$assetPath\" in " +
                            "${(System.nanoTime() - t) / 1e6} ms ($width x $height px)")
                }

            } catch (e: IOException) {
                Log.e("ImageTextureData", "Failed loading image asset", e)
            }
        }
    }

    override fun onLoad(texture: Texture, ctx: RenderContext) {
        val res = texture.res ?: throw KoolException("Texture wasn't created")
        val limit = buffer!!.limit
        val pos = buffer!!.position
        buffer!!.flip()

        glTexImage2D(res.target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer)
        buffer!!.limit = limit
        buffer!!.position = pos
        ctx.memoryMgr.memoryAllocated(res, buffer!!.position)
    }

    companion object {
        fun convertBitmapToBuffer(bitmap: Bitmap): Uint8Buffer {
            val w = bitmap.width
            val h = bitmap.height
            val alpha = bitmap.hasAlpha()
            val stride = if (alpha) 4 else 3
            val resultBuffer = createUint8Buffer(w * h * stride)
            val buf = (resultBuffer as Uint8BufferImpl).buffer

            if (bitmap.config === Bitmap.Config.ARGB_8888) {
                if (alpha) {
                    // copy bitmap pixels to created buf
                    bitmap.copyPixelsToBuffer(buf)
                    // pre-multiply alpha
                    for (i in 0 until w * h * 4 step 4) {
                        val a = buf[i+3].toInt() and 0xff
                        if (a < 255) {
                            val f = a / 255f
                            buf.put(i, round(buf[i] * f).toByte())
                            buf.put(i+1, round(buf[i+1] * f).toByte())
                            buf.put(i+2, round(buf[i+2] * f).toByte())
                        }
                    }

                } else {
                    // although image has no alpha we need a temp buffer with 4 bytes per pixel
                    val out = ByteBuffer.allocate(w * h * 4)
                    bitmap.copyPixelsToBuffer(out)
                    // copy pixel to RGB buf (and skip alpha)
                    for (i in 0 until w * h * 4 step 4) {
                        buf.put(out[i+0])
                        buf.put(out[i+1])
                        buf.put(out[i+2])
                    }
                    buf.flip()
                }
            } else {
                // bitmap has a weird internal format, fallback to slow copy
                for (i in 0 until w * h) {
                    val px = bitmap.getPixel(i % w, i / w)
                    val r = px shr 16 and 0xff
                    val g = px shr 8 and 0xff
                    val b = px and 0xff
                    if (alpha) {
                        val a = px shr 24 and 0xff
                        val f = a / 255f
                        buf.put(round(r * f).toByte())
                        buf.put(round(g * f).toByte())
                        buf.put(round(b * f).toByte())
                        buf.put(a.toByte())
                    } else {
                        buf.put(r.toByte())
                        buf.put(g.toByte())
                        buf.put(b.toByte())
                    }
                }
                buf.flip()
            }
            return resultBuffer
        }
    }
}
