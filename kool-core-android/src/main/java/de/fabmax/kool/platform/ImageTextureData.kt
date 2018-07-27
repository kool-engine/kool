package de.fabmax.kool.platform

import android.graphics.Bitmap
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.gl.GL_RGB
import de.fabmax.kool.gl.GL_RGBA
import de.fabmax.kool.gl.GL_UNSIGNED_BYTE
import de.fabmax.kool.gl.glTexImage2D
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import java.nio.ByteBuffer
import kotlin.math.round

class ImageTextureData : TextureData() {
    private var buffer: Uint8Buffer? = null
    private var format = 0

    internal fun setTexImage(bitmap: Bitmap) {
        buffer = convertBitmapToBuffer(bitmap)
        format = if (bitmap.hasAlpha()) GL_RGBA else GL_RGB
        width = bitmap.width
        height = bitmap.height
        isAvailable = true
    }

    override fun onLoad(texture: Texture, ctx: KoolContext) {
        val res = texture.res ?: throw KoolException("Texture wasn't created")
        glTexImage2D(res.target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer)
        ctx.memoryMgr.memoryAllocated(res, buffer!!.remaining)
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
                    // copy bitmap pixels to created buf, we need pre-multiplied alpha but that's
                    // the standard format in android, so there's nothing else to do
                    bitmap.copyPixelsToBuffer(buf)

                } else {
                    // although image has no alpha we need a temp buffer with 4 bytes per pixel
                    val tmp = ByteBuffer.allocate(w * h * 4)
                    bitmap.copyPixelsToBuffer(tmp)
                    tmp.flip()
                    // discard alpha channel bytes (every 4th byte)
                    // use backing byte array instead of buffer (much faster)
                    val tmpArr = if (tmp.hasArray()) tmp.array() else {
                        val array = ByteArray(w * h * 4)
                        tmp.get(array)
                        array
                    }
                    var j = 0
                    for (i in 3 until w * h * 3 step 3) {
                        tmpArr[i] = tmpArr[j]
                        tmpArr[i+1] = tmpArr[j+1]
                        tmpArr[i+2] = tmpArr[j+2]
                        j += 4
                    }
                    buf.put(tmpArr, 0, w * h * 3)
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
            }
            buf.flip()
            return resultBuffer
        }
    }
}
