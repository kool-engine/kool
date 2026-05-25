package de.fabmax.kool.pipeline.deferred2

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.blocks.getLinearDepthReversed
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*

class TemporalFilterPass(
    val pipeline: Deferred2Pipeline,
    private var size: Vec2i,
    filterStorageFmt: TexFormat = TexFormat.RGBA_F16,
) : ComputePass("deferred2-lighting-pass") {
    val filterOutput = AlternatingPair {
        StorageTexture2d(size.x, size.y, filterStorageFmt, samplerSettings = SamplerSettings().clamped())
    }
    val filterState = AlternatingPair {
        StorageTexture2d(size.x, size.y, TexFormat.R, samplerSettings = SamplerSettings().clamped().nearest())
    }

    private val temporalShader = TemporalFilterShader(filterStorageFmt, pipeline.lightingPass.lightingOutput)

    var filterWeight = 8f

    init {
        setupPasses()
        onRelease {
            filterOutput.a.release()
            filterOutput.b.release()
            filterState.a.release()
            filterState.b.release()
        }
    }

    fun resize(size: Vec2i) {
        this.size = size
        filterOutput.a.resize(size.x, size.y)
        filterOutput.b.resize(size.x, size.y)
        filterState.a.resize(size.x, size.y)
        filterState.b.resize(size.x, size.y)
        setupPasses()
    }

    private fun setupPasses() {
        clearAndReleaseTasks()
        val groupsX = (size.x + 7) / 8
        val groupsY = (size.y + 7) / 8
        addTask(temporalShader, Vec3i(groupsX, groupsY, 1))
    }

    fun swapBuffers() {
        temporalShader.swapPipelineData(filterOutput.newVal) {
            val newGbuffer = pipeline.gbuffers.newVal
            val oldGbuffer = pipeline.gbuffers.oldVal

            newDepth = newGbuffer.depth
            oldDepth = oldGbuffer.depth
            oldMeta = oldGbuffer.objectIds
            newMeta = newGbuffer.objectIds
            oldFilter = filterOutput.oldVal
            newFilter = filterOutput.newVal
            filterW = filterWeight
            filterStateRd = filterState.oldVal
            filterStateWr = filterState.newVal

            reprojectMats = pipeline.reprojectMatrixComputePass.reprojectMats
            camData = pipeline.camData
        }
    }
}

class TemporalFilterShader(
    val filterStorageFmt: TexFormat,
    lightingOutput: Texture2d,
) : KslComputeShader("deferred2-temporal-filter") {
    var lightingOutput by bindTexture2d("lightingOutput", lightingOutput)
    var oldMeta by bindTexture2d("oldMeta")
    var newMeta by bindTexture2d("newMeta")
    var newDepth by bindTexture2d("newDepth")
    var oldDepth by bindTexture2d("oldDepth")
    var oldFilter by bindTexture2d("oldFilter", defaultSampler = SamplerSettings().clamped().linear())
    var newFilter by bindStorageTexture2d("newFilter")
    var filterStateRd by bindTexture2d("filterStateRd")
    var filterStateWr by bindStorageTexture2d("filterStateWr")

    var reprojectMats by bindStorage("reprojectMats")
    var camData by bindStorage("camData")
    var filterW by bindUniformFloat1("uFilterWeight")

    init {
        program.program()
    }

    private fun KslProgram.program() {
        computeStage(workGroupSizeX = 8, workGroupSizeY = 8) {
            val lightingOutput = texture2d("lightingOutput")
            val oldMeta = texture2dInt("oldMeta")
            val newMeta = texture2dInt("newMeta")
            val newDepth = texture2d("newDepth", isUnfilterable = true)
            val oldDepth = texture2d("oldDepth", isUnfilterable = true)
            val oldFilter = texture2d("oldFilter")
            val newFilter = if (filterStorageFmt.channels == 3) {
                storageTexture2d<KslFloat3>("newFilter", filterStorageFmt)
            } else {
                storageTexture2d<KslFloat4>("newFilter", filterStorageFmt)
            }
            val filterStateRd = texture2d("filterStateRd")
            val filterStateWr = storageTexture2d<KslFloat1>("filterStateWr", TexFormat.R)

            val matStruct = struct(StorageMatLayout)
            val reprojectMats = storage("reprojectMats", matStruct)
            val camDataLayout = struct(DeferredCamDataLayout)
            val camData = storage("camData", camDataLayout)

            val filterWeight = uniformFloat1("uFilterWeight")

            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val newColor by lightingOutput.load(baseCoord).rgb
                val ddx by Vec2f.X_AXIS.const
                val ddy by Vec2f.Y_AXIS.const
                `if` (filterWeight eq 0f.const) {
                    newFilter[baseCoord] = float4Value(newColor, 1f)
                    `return`()
                }

                val curMeta by newMeta.load(baseCoord).r
                val id by curMeta and 0xffffff.const
                val size by newDepth.size()
                val sizeF by size.toFloat2()

                val near by camData.camNear
                val invViewProj by camData.invViewProj
                val depth by newDepth.load(baseCoord).x
                val worldPos by unprojectBaseCoord(depth, baseCoord, size, near, invViewProj)

                val oldProj by 0f.const4
                `if`(id ne 0.const) {
                    val reprojectMat = mat4Var(reprojectMats[id][StorageMatLayout.mat])
                    oldProj set reprojectMat * worldPos
                }.`else` {
                    oldProj set camData.oldViewProj * worldPos
                }
                val oldUv by oldProj.xy / oldProj.w * float2Value(0.5f, -0.5f) + 0.5f.const
                val oldBaseCoord by (oldUv * sizeF).toInt2()

                val oldStateBaseUv by oldUv * sizeF
                val oldState by
                    (filterStateRd.load((oldStateBaseUv + float2Value(0.49f, 0.49f)).toInt2()).r * 255f.const).toInt1() or
                    (filterStateRd.load((oldStateBaseUv + float2Value(0.49f, -0.49f)).toInt2()).r * 255f.const).toInt1() or
                    (filterStateRd.load((oldStateBaseUv + float2Value(-0.49f, -0.49f)).toInt2()).r * 255f.const).toInt1() or
                    (filterStateRd.load((oldStateBaseUv + float2Value(-0.49f, 0.49f)).toInt2()).r * 255f.const).toInt1()
                val wasEdge by oldState ne 0.const

                val refDepth by getLinearDepthReversed(depth, near)
                val depthA by getLinearDepthReversed(newDepth.load(baseCoord + int2Value(1, 1)).x, near)
                val depthB by getLinearDepthReversed(newDepth.load(baseCoord + int2Value(-1, -1)).x, near)
                val depthC by getLinearDepthReversed(newDepth.load(baseCoord + int2Value(-1, 0)).x, near)
                val depthD by getLinearDepthReversed(newDepth.load(baseCoord + int2Value(1, 0)).x, near)
                val depthDab by min(abs(refDepth - depthA), abs(refDepth - depthB)) + (refDepth * 0.01f.const)
                val depthDcd by min(abs(refDepth - depthC), abs(refDepth - depthD)) + (refDepth * 0.01f.const)

                val oldDepth by getLinearDepthReversed(oldDepth.load(oldBaseCoord).x, near)
                val depthHit by abs(refDepth - oldDepth) lt (max(depthDab, depthDcd) * 2f.const)
                val idHit by id eq (oldMeta.load(oldBaseCoord).r and 0xffffff.const)
                val filterHit by idHit and depthHit
                val isEdge by false.const
                `if`(!filterHit or wasEdge) {
                    val hitA by abs(refDepth - depthA) lt depthDab * 2f.const
                    val hitB by abs(refDepth - depthB) lt depthDab * 2f.const
                    val hitC by abs(refDepth - depthC) lt depthDcd * 2f.const
                    val hitD by abs(refDepth - depthD) lt depthDcd * 2f.const
                    val anyYes by hitA or hitB or hitC or hitD
                    val anyNo by !hitA or !hitB or !hitC or !hitD
                    val depthEdge by anyYes and anyNo

                    val borderCoords = listOf(Vec2i(-1, -1), Vec2i(1, 1), Vec2i(1, -1), Vec2i(-1, 1))
                    val anyEq by false.const
                    val anyNe by false.const
                    borderCoords.forEach { bc ->
                        val sampleId = int1Var(newMeta.load(baseCoord + bc.const).r and 0xffffff.const)
                        anyEq set (anyEq or (sampleId eq id))
                        anyNe set (anyNe or (sampleId ne id))
                    }
                    val idEdge by anyEq and anyNe
                    isEdge set (depthEdge or idEdge)
                }

                filterStateWr.store(baseCoord, float4Value(isEdge.toFloat1(), 0f.const, 0f.const, 0f.const))

                val w by filterWeight
                val isReprojectOutOfScreen by (oldUv.x lt 0f.const) or (oldUv.y lt 0f.const) or (oldUv.x gt 1f.const) or (oldUv.y gt 1f.const)
                `if`((!filterHit and !isEdge) or (wasEdge and !isEdge) or isReprojectOutOfScreen) {
                    w set 0f.const
                }

                // reduce filter weight in screen regions with high motion
                val dUv = saturate(length(oldUv - baseCoord.toFloat2() / sizeF) / 0.01f.const)
                w *= (1f.const - dUv)

                val oldColor by oldFilter.sample(oldUv, ddx, ddy).rgb
                val curSrgb by convertColorSpace(newColor, ColorSpaceConversion.LinearToSrgb())
                val oldSrgb by convertColorSpace(oldColor, ColorSpaceConversion.LinearToSrgb())
                val weighted by (oldSrgb * w + curSrgb) / (w + 1f.const)
                val filtered by convertColorSpace(weighted, ColorSpaceConversion.SrgbToLinear())

                `if`(any(isNan(filtered))) {
                    filtered set curSrgb
                }
                newFilter[baseCoord] = float4Value(filtered, 1f)
            }
        }
    }
}
