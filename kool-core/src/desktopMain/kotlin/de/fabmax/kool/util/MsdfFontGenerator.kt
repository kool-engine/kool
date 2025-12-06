package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.MipMapping
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.msdfgen.MSDFGen.*
import org.lwjgl.util.msdfgen.MSDFGenBitmap
import org.lwjgl.util.msdfgen.MSDFGenBounds
import org.lwjgl.util.msdfgen.MSDFGenExt.*
import org.lwjgl.util.msdfgen.MSDFGenTransform
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.io.path.writeText
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Generates [MsdfFont]s from TrueType fonts. Loaded fonts can either be directly used (see
 * [MsdfFontGenerator.loadMsdfFont]) or written to disk (see [MsdfFontGenerator.writeMsdfFont]).
 *
 * The generator currently does not resolve self-intersecting fonts, which results in artifacts for some fonts (e.g.
 * `Roboto`). Other fonts like `Jetbrains Mono` work fine. In case you have a problematic ttf-font, you can still
 * generate an MSDF font using the msdf-atlas-gen available at https://github.com/Chlumsky/msdf-atlas-gen.
 */
object MsdfFontGenerator {

    /**
     * Loads a [MsdfFont] from the TrueType font at the given path.
     *
     * @param inputTtfPath path to the TrueType font.
     * @param fontName human-readable name of the font.
     * @param glyphsToGenerate collection of Unicode characters that should be included in the font.
     * @param msdfGenSize size of a MSDF glyph in the generated atlas. This is more or less independent of the later
     *        used font size. The default value should be fine for most cases.
     * @param pxRange distance field range in pixels.
     * @param atlasDim width of the generated atlas in pixels.
     * @param type type of the generated MSDF font. [MsdfType.MTSDF] is the most flexible one and the only one
     *        currently supported by kool.
     */
    suspend fun loadMsdfFont(
        inputTtfPath: String,
        fontName: String,
        glyphsToGenerate: GeneratorGlyphSet = GeneratorGlyphSet.ALL_BASIC,
        msdfGenSize: Int = 36,
        pxRange: Double = 8.0,
        atlasDim: Int = 512,
        type: MsdfType = MsdfType.MTSDF,
    ): MsdfFont = withContext(Dispatchers.IO) {
        val ttfData = Path(inputTtfPath).readBytes().toBuffer()
        val (meta, pixels) = generateMsdf(ttfData, fontName, glyphsToGenerate, msdfGenSize, pxRange, atlasDim, type)
        val map = Texture2d(pixels, MipMapping.Off, name = fontName)
        val fontData = MsdfFontData(map, meta)
        MsdfFont(fontData)
    }

    /**
     * Loads a TrueType font and saves the generated MSDF data to disk. The output path should denote a file without
     * extension, e.g.: `path/to/my_font`. The implementation will then write to files `path/to/my_font.png` and
     * `path/to/my_font.json` containing the MSDF font data (texture and metadata).
     *
     * @param inputTtfPath path to the TrueType font file.
     * @param outputMsdfPath path to the directory where the generated MSDF data should be saved.
     * @param fontName human-readable name of the font.
     * @param glyphsToGenerate collection of Unicode characters that should be included in the font.
     * @param msdfGenSize size of a MSDF glyph in the generated atlas. This is more or less independent of the later
     *        used font size. The default value should be fine for most cases.
     * @param pxRange distance field range in pixels.
     * @param atlasDim width of the generated atlas in pixels.
     * @param type type of the generated MSDF font. [MsdfType.MTSDF] is the most flexible one and the only one
     *        currently supported by kool.
     */
    suspend fun writeMsdfFont(
        inputTtfPath: String,
        outputMsdfPath: String,
        fontName: String,
        glyphsToGenerate: GeneratorGlyphSet = GeneratorGlyphSet.ALL_BASIC,
        msdfGenSize: Int = 36,
        pxRange: Double = 8.0,
        atlasDim: Int = 512,
        type: MsdfType = MsdfType.MTSDF,
    ) {
        withContext(Dispatchers.IO) {
            val ttfData = Path(inputTtfPath).readBytes().toBuffer()
            val (meta, pixels) = generateMsdf(ttfData, fontName, glyphsToGenerate, msdfGenSize, pxRange, atlasDim, type)
            val compactMeta = meta.copy(
                glyphs = emptyList(),
                compactGlyphs = meta.glyphs.map { MsdfCompactGlyph.fromMsdfGlyph(it) },
            )
            ImageIO.write(pixels.toBufferedImage(), "png", File("$outputMsdfPath.png"))
            Path("$outputMsdfPath.json").writeText(Json.encodeToString(compactMeta))
        }
    }

    fun generateMsdf(
        ttfData: Uint8Buffer,
        fontName: String,
        glyphsToGenerate: GeneratorGlyphSet = GeneratorGlyphSet.ALL_BASIC,
        msdfGenSize: Int = 36,
        pxRange: Double = 8.0,
        atlasDim: Int = 512,
        type: MsdfType = MsdfType.MTSDF,
    ): Pair<MsdfMeta, BufferedImageData2d> {
        scopedMem {
            val ftHandle = callocPointer(1)
            msdf_ft_init(ftHandle).checked()
            val fontContext = ftHandle.get(0)
            val stbFontInfo = STBTTFontinfo.calloc(this)
            val fontHandle = callocPointer(1)
            ttfData.useRaw {
                msdf_ft_load_font_data(fontContext, it, fontHandle).checked()
                stbtt_InitFont(stbFontInfo, it).checked()
            }
            val font = fontHandle.get(0)
            val shapes = glyphsToGenerate
                .getCodepoints { stbtt_FindGlyphIndex(stbFontInfo, it) > 0 }
                .mapNotNull { codePoint -> scopedMem { loadCharShape(font, codePoint) } }
            logI { "Generating ${shapes.size} glyphs for font $fontName..." }

            val bitMapSize = msdfGenSize * 2
            val padding = ceil(pxRange).toInt() / 2
            shapes.arrangeAtlas(msdfGenSize, bitMapSize, padding, atlasDim)
            val atlasW = shapes.maxOf { it.dstRect.r }
            val atlasH = shapes.maxOf { it.dstRect.b}
            val pixelBuffer = Uint8Buffer(atlasW * atlasH * 4)
            val imageData = BufferedImageData2d(pixelBuffer, atlasW, atlasH, TexFormat.RGBA)

            val ascentOut = callocInt(1)
            val descentOut = callocInt(1)
            val lineGapOut = callocInt(1)
            stbtt_GetFontVMetrics(stbFontInfo, ascentOut, descentOut, lineGapOut)
            val emScale = stbtt_ScaleForMappingEmToPixels(stbFontInfo, 1f)
            val ascent = ascentOut.get(0) * emScale
            val descent = descentOut.get(0) * emScale

            val glyphMeta = generateGlyphs(shapes, type, pxRange, msdfGenSize, stbFontInfo, emScale, imageData)

            msdf_ft_font_destroy(font)
            msdf_ft_deinit(fontContext)

            logI { "Done generating font $fontName: ${shapes.size} glyphs, $atlasW x $atlasH pixels" }

            val msdfAtlasInfo = MsdfAtlasInfo(
                type = type.stringType,
                distanceRange = pxRange.toFloat(),
                size = msdfGenSize.toFloat(),
                width = atlasW,
                height = atlasH,
                yOrigin = "bottom"
            )
            val metrics = MsdfMetrics(
                emSize = 1f,
                lineHeight = ascent - descent,
                ascender = ascent,
                descender = descent,
                underlineY = 0f,
                underlineThickness = 0f,
            )
            val msdfMeta = MsdfMeta(
                atlas = msdfAtlasInfo,
                name = fontName,
                metrics = metrics,
                glyphs = glyphMeta.sortedBy { it.unicode },
                kerning = emptyList(),
            )
            return msdfMeta to imageData
        }
    }

    private fun MemoryStack.loadCharShape(font: Long, codePoint: Int): ShapeData? {
        val shapeBuffer = callocPointer(1)
        val glyphResult = msdf_ft_font_load_glyph(font, codePoint, MSDF_FONT_SCALING_EM_NORMALIZED, shapeBuffer)
        if (glyphResult != MSDF_SUCCESS) {
            logW { "Codepoint $codePoint / '${codePoint.toChar()}' not included in font" }
            return null
        }
        val shape = shapeBuffer.get(0)

        msdf_shape_normalize(shape).checked()
        msdf_shape_edge_colors_simple(shape, 3.0).checked()

        val bounds = MSDFGenBounds.calloc(this)
        msdf_shape_get_bounds(shape, bounds).checked()
        return if (bounds.l() > bounds.r() || bounds.b() > bounds.t()) {
            ShapeData(
                codePoint = codePoint,
                shape = shape,
                shapeL = 0.0,
                shapeR = 0.0,
                shapeB = 0.0,
                shapeT = 0.0
            )
        } else {
            ShapeData(
                codePoint = codePoint,
                shape = shape,
                shapeL = bounds.l(),
                shapeR = bounds.r(),
                shapeB = bounds.b(),
                shapeT = bounds.t()
            )
        }
    }

    private fun List<ShapeData>.arrangeAtlas(fontSize: Int, bitMapSize: Int, padding: Int, atlasDim: Int) {
        val descendingHeight = onEach { it.dstRect = it.getSrcRect(fontSize, bitMapSize, padding) }.sortedBy { -it.dstRect.h }
        val rowEnds = mutableListOf<Rect>()
        var prevRow = listOf<ShapeData>()
        var rowI = 0
        var idx = 0
        while (idx < size) {
            var row = buildList {
                var rowWidth = 0
                while (true) {
                    val next = descendingHeight.getOrNull(idx) ?: break

                    val fit = rowEnds.find { atlasDim - it.r >= next.dstRect.w }
                    if (fit != null) {
                        idx++
                        val replIndex = rowEnds.indexOf(fit)
                        next.dstRect = next.dstRect.moveTo(fit.r, fit.t)
                        rowEnds[replIndex] = next.dstRect

                    } else {
                        if (rowWidth + next.dstRect.w < atlasDim) {
                            add(next)
                            rowWidth += next.dstRect.w
                            idx++
                        } else {
                            break
                        }
                    }
                }
            }

            var x = 0
            if (++rowI % 2 == 0) {
                row = row.reversed()
            }
            row.forEach { shape ->
                val xr = x + shape.dstRect.w
                val y = prevRow.filter { it.dstRect.l < xr && it.dstRect.r > x }.maxOfOrNull { it.dstRect.b } ?: 0
                shape.dstRect = shape.dstRect.moveTo(x, y)
                x = shape.dstRect.r
            }
            rowEnds += row.last().dstRect
            prevRow = row
        }
    }

    private fun MemoryStack.generateGlyphs(
        shapes: List<ShapeData>,
        type: MsdfType,
        pxRange: Double,
        msdfGenSize: Int,
        stbFontInfo: STBTTFontinfo,
        emScale: Float,
        imageData: BufferedImageData2d,
    ): List<MsdfGlyph> {
        val bitMapSize = msdfGenSize * 2
        val pixelsPtr = callocPointer(1)
        val byteSizePtr = callocPointer(1)
        val channelCountBuf = callocInt(1)
        val bitmap = MSDFGenBitmap.calloc(this)

        msdf_bitmap_alloc(type.nativeType, bitMapSize, bitMapSize, bitmap).checked()
        msdf_bitmap_get_pixels(bitmap, pixelsPtr).checked()
        msdf_bitmap_get_byte_size(bitmap, byteSizePtr).checked()
        msdf_bitmap_get_channel_count(bitmap, channelCountBuf).checked()

        val pixelsAddr = pixelsPtr.get(0)
        val bufSize = byteSizePtr.get(0).toInt()
        val genChannels = channelCountBuf.get(0)
        val data = MemoryUtil.memFloatBuffer(pixelsAddr, bufSize / 4)

        val pixelBuffer = imageData.data as Uint8BufferImpl
        val padding = ceil(pxRange).toInt() / 2
        val glyphMeta = mutableListOf<MsdfGlyph>()
        shapes.sortedBy { it.codePoint }.forEach { shapeData ->
            scopedMem {
                val t = MSDFGenTransform.calloc(this)
                t.distance_mapping { it.set(-0.5 * (pxRange / msdfGenSize), 0.5 * (pxRange / msdfGenSize)) }
                t.translation { it.set(0.5, 0.5) }
                t.scale { it.set(msdfGenSize.toDouble(), msdfGenSize.toDouble()) }

                when (type) {
                    MsdfType.MTSDF -> msdf_generate_mtsdf(bitmap, shapeData.shape, t).checked()
                    MsdfType.MSDF -> msdf_generate_msdf(bitmap, shapeData.shape, t).checked()
                    MsdfType.PSDF -> msdf_generate_psdf(bitmap, shapeData.shape, t).checked()
                    MsdfType.SDF -> msdf_generate_sdf(bitmap, shapeData.shape, t).checked()
                }

                val srcRect = shapeData.getSrcRect(msdfGenSize, bitMapSize, padding)
                for (y in 0 until srcRect.h) {
                    for (x in 0 until srcRect.w) {
                        val srcX = x + srcRect.l
                        val srcY = (srcRect.h - 1 - y) + srcRect.t
                        val srcOff = (srcY * bitMapSize + srcX) * genChannels
                        val r = pixelFloatToByte(data.get(srcOff + 0))
                        val g = if (genChannels > 1) pixelFloatToByte(data.get(srcOff + 1)) else r
                        val b = if (genChannels > 2) pixelFloatToByte(data.get(srcOff + 2)) else r
                        val a = if (genChannels == 4) pixelFloatToByte(data.get(srcOff + 3)) else 255u.toUByte()

                        val dstX = x + shapeData.dstRect.l
                        val dstY = y + shapeData.dstRect.t
                        val dstOff = (dstY * imageData.width + dstX) * 4
                        pixelBuffer[dstOff + 0] = r
                        pixelBuffer[dstOff + 1] = g
                        pixelBuffer[dstOff + 2] = b
                        pixelBuffer[dstOff + 3] = a
                    }
                }
                msdf_shape_free(shapeData.shape)

                glyphMeta.add(makeGlyphData(shapeData, stbFontInfo, msdfGenSize, emScale, pxRange, imageData.height))
            }
        }
        msdf_bitmap_free(bitmap)
        return glyphMeta.sortedBy { it.unicode }
    }

    private fun MemoryStack.makeGlyphData(
        shapeData: ShapeData,
        stbFontInfo: STBTTFontinfo,
        msdfGenSize: Int,
        emScale: Float,
        pxRange: Double,
        atlasH: Int
    ): MsdfGlyph {
        val advance = callocInt(1)
        val leftBearing = callocInt(1)
        stbtt_GetCodepointHMetrics(stbFontInfo, shapeData.codePoint, advance, leftBearing)

        val x0 = callocInt(1)
        val y0 = callocInt(1)
        val x1 = callocInt(1)
        val y1 = callocInt(1)

        return if (stbtt_GetCodepointBox(stbFontInfo, shapeData.codePoint, x0, y0, x1, y1)) {
            val planePad = pxRange.toFloat() * 0.5f / msdfGenSize
            val planeBounds = MsdfRect(
                left = x0.get(0) * emScale - planePad,
                bottom = y0.get(0) * emScale - planePad,
                right = x1.get(0) * emScale + planePad,
                top = y1.get(0) * emScale + planePad,
            )
            val atlasBounds = MsdfRect(
                left = shapeData.dstRect.l + 0.5f,
                bottom = atlasH - (shapeData.dstRect.b - 0.5f),
                right = shapeData.dstRect.r - 0.5f,
                top = atlasH - (shapeData.dstRect.t + 0.5f),
            )
            MsdfGlyph(shapeData.codePoint, advance.get(0) * emScale, planeBounds, atlasBounds)
        } else {
            MsdfGlyph(shapeData.codePoint, advance.get(0) * emScale)
        }
    }

    private fun pixelFloatToByte(f: Float): UByte {
        return (255.5f - 255f * f.clamp()).toInt().inv().toUByte()
    }

    private fun BufferedImageData2d.toBufferedImage(): BufferedImage {
        val byteData = data as Uint8BufferImpl
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        var i = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = byteData[i++].toInt().clamp(0, 255)
                val g = byteData[i++].toInt().clamp(0, 255)
                val b = byteData[i++].toInt().clamp(0, 255)
                val a = byteData[i++].toInt().clamp(0, 255)
                val rgb = (a shl 24) or (r shl 16) or (g shl 8) or b
                img.setRGB(x, y, rgb)
            }
        }
        return img
    }

    private data class ShapeData(
        val codePoint: Int,
        val shape: Long,
        val shapeL: Double,
        val shapeR: Double,
        val shapeB: Double,
        val shapeT: Double,
        var dstRect: Rect = Rect(),
    ) {
        fun getSrcRect(size: Int, bitMapSize: Int, padding: Int): Rect {
            val srcL = (floor((0.5 + shapeL) * size).toInt() - padding).coerceAtLeast(0)
            val srcR = (ceil((0.5 + shapeR) * size).toInt() + padding).coerceAtMost(bitMapSize - 1)
            // swap top and bottom because shape coordinates use inverted y-axis
            val srcT = (floor((0.5 + shapeB) * size).toInt() - padding).coerceAtLeast(0)
            val srcB = (ceil((0.5 + shapeT) * size).toInt() + padding).coerceAtMost(bitMapSize - 1)
            return Rect(srcL, srcR, srcB, srcT)
        }
    }

    private data class Rect(val l: Int = 0, val r: Int = 0, val b: Int = 0, val t: Int = 0) {
        val w = r - l
        val h = b - t
    }

    private fun Rect.moveTo(x: Int, y: Int) = Rect(x, x + w, y + h, y)

    private fun Int.checked() = check(this == MSDF_SUCCESS) { "Unexpected result code: $this" }
    private fun Boolean.checked() = check(this)
}

enum class MsdfType(val stringType: String, val nativeType: Int) {
    MTSDF("mtsdf", MSDF_BITMAP_TYPE_MTSDF),
    MSDF("msdf", MSDF_BITMAP_TYPE_MSDF),
    PSDF("psdf", MSDF_BITMAP_TYPE_PSDF),
    SDF("sdf", MSDF_BITMAP_TYPE_SDF)
}

sealed interface GeneratorGlyphSet {
    fun getCodepoints(predicate: (Int) -> Boolean): Set<Int>

    data class FromString(val str: String) : GeneratorGlyphSet {
        override fun getCodepoints(predicate: (Int) -> Boolean): Set<Int> =
            str.map { it.code }.filter(predicate).toSet()
    }

    data class FromRanges(val ranges: List<IntRange>) : GeneratorGlyphSet {
        override fun getCodepoints(predicate: (Int) -> Boolean): Set<Int> = buildSet {
            ranges.forEach { range ->
                range.asSequence().filter(predicate).forEach { add(it) }
            }
        }
    }

    companion object {
        val LATIN_SMALL = FromRanges(listOf(32..0xFF))
        val ALL_BASIC = FromRanges(listOf(32..0xFFFF))
        val ALL = FromRanges(listOf(32..0x10FFFF))
    }
}
