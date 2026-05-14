package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.Time

class TemporalFilterPass(
    val lightingOutput: Texture2d,
    val gbuffers: AlternatingPair<GbufferPass>,
    val camera: Camera,
    private var size: Vec2i,
    filterStorageFmt: TexFormat = TexFormat.RGBA_F16,
) : ComputePass("deferred2-lighting-pass") {
    val filterOutput = AlternatingPair {
        StorageTexture2d(size.x, size.y, filterStorageFmt, samplerSettings = SamplerSettings().clamped().nearest())
    }
    private val filterState = StorageTexture2d(size.x, size.y, TexFormat.R, samplerSettings = SamplerSettings().clamped().nearest())

    private val temporalShader = TemporalFilterShader(filterStorageFmt, lightingOutput, filterState)

    init {
        setupPasses()
    }

    fun resize(size: Vec2i) {
        this.size = size
        filterOutput.a.resize(size.x, size.y)
        filterOutput.b.resize(size.x, size.y)
        filterState.resize(size.x, size.y)
        setupPasses()
    }

    private fun setupPasses() {
        clearAndReleaseTasks()
        val groupsX = (size.x + 7) / 8
        val groupsY = (size.y + 7) / 8
        addTask(temporalShader, Vec3i(groupsX, groupsY, 1))
    }

    fun swapBuffers() {
        temporalShader.swapPipelineDataCapturing(filterOutput.newVal) {
            oldAlbedo = gbuffers.oldVal.albedoEmission
            newAlbedo = gbuffers.newVal.albedoEmission
            oldMeta = gbuffers.oldVal.objectIds
            newMeta = gbuffers.newVal.objectIds
            oldFilter = filterOutput.oldVal
            newFilter = filterOutput.newVal
            frameI = Time.frameCount
        }
    }
}

class TemporalFilterShader(
    val filterStorageFmt: TexFormat,
    lightingOutput: Texture2d,
    filterState: StorageTexture2d,
) : KslComputeShader("deferred2-temporal-filter") {
    var lightingOutput by bindTexture2d("lightingOutput", lightingOutput)
    var oldAlbedo by bindTexture2d("oldAlbedo")
    var newAlbedo by bindTexture2d("newAlbedo")
    var oldMeta by bindTexture2d("oldMeta")
    var newMeta by bindTexture2d("newMeta")

//    var oldDepth by bindTexture2d("oldDepth")
//    var newDepth by bindTexture2d("newDepth")
//    var camData = bindUniformStruct("camData", NewOldCamDataStruct)

    var oldFilter by bindStorageTexture2d("oldFilter")
    var newFilter by bindStorageTexture2d("newFilter")
    var filterState by bindStorageTexture2d("filterState", filterState)

    var frameI by bindUniformInt1("frameI")

    init {
        program.program()
    }

    private fun KslProgram.program() {
        computeStage(workGroupSizeX = 8, workGroupSizeY = 8) {
            val lightingOutput = texture2d("lightingOutput")
            val oldAlbedo = texture2d("oldAlbedo")
            val newAlbedo = texture2d("newAlbedo")
            val oldMeta = texture2dInt("oldMeta")
            val newMeta = texture2dInt("newMeta")

            val oldFilter = if (filterStorageFmt.channels == 3) {
                storageTexture2d<KslFloat3>("oldFilter", filterStorageFmt)
            } else {
                storageTexture2d<KslFloat4>("oldFilter", filterStorageFmt)
            }
            val newFilter = if (filterStorageFmt.channels == 3) {
                storageTexture2d<KslFloat3>("newFilter", filterStorageFmt)
            } else {
                storageTexture2d<KslFloat4>("newFilter", filterStorageFmt)
            }

            val filterState = storageTexture2d<KslFloat1>("filterState", TexFormat.R)

            val frameI = uniformInt1("frameI")

//            val oldDepth = texture2d("oldDepth", isUnfilterable = true)
//            val newDepth = texture2d("newDepth", isUnfilterable = true)
//            val camData = uniformStruct("camData", NewOldCamDataStruct)

            val metaEqual = functionBool1("fnMetaEqual") {
                val meta1 = paramInt1()
                val meta2 = paramInt1()

                body {
                    val x1 by (meta1 shr 24.const) and 0xf.const
                    val y1 by (meta1 shr 28.const) and 0xf.const
                    val x2 by (meta2 shr 24.const) and 0xf.const
                    val y2 by (meta2 shr 28.const) and 0xf.const
                    (abs(x1 - x2) le 3.const) and (abs(y1 - y2) le 3.const)
                }
            }

            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
//                val uv by baseCoordToUv(baseCoord, lightingOutput.size())
                val state by (filterState.load(baseCoord).r * 255f.const).toInt1()
//                val filterNoise by noise31(uint3Value(inGlobalInvocationId.xy, frameI.toUint1()))

//                val camNear = camData[NewOldCamDataStruct.newCam][DeferredCamDataStruct.camNear]
//                val invProj = camData[NewOldCamDataStruct.newCam][DeferredCamDataStruct.invProj]
//                val invView = camData[NewOldCamDataStruct.newCam][DeferredCamDataStruct.invView]
//                val newWorldPos by unprojectUv(newDepth, baseCoord, camNear, invProj, invView).xyz

                val curAlbedo by newAlbedo.load(baseCoord).rgb
                val curMeta by newMeta.load(baseCoord).r
                val colorDiff by length(curAlbedo - oldAlbedo.load(baseCoord).rgb)
                val sameColorThresh = 0.08f.const //+ filterNoise * 0.01f.const
                val sameColor by colorDiff lt sameColorThresh
                val sameMeta by metaEqual(curMeta, oldMeta.load(baseCoord).r)
                val filterHit by sameColor and sameMeta

                val wasEdge by state and 1.const gt 0.const
                val isEdge by false.const
                `if`(!filterHit or wasEdge) {
                    val da by length(curAlbedo - oldAlbedo.load(baseCoord + int2Value(-1, -1)).rgb)
                    val db by length(curAlbedo - oldAlbedo.load(baseCoord + int2Value(1, 1)).rgb)
                    val dc by length(curAlbedo - oldAlbedo.load(baseCoord + int2Value(1, -1)).rgb)
                    val dd by length(curAlbedo - oldAlbedo.load(baseCoord + int2Value(-1, 1)).rgb)

                    val minD = min(colorDiff, min(min(da, db), min(dc, dd)))
                    val maxD = max(colorDiff, max(max(da, db), max(dc, dd)))
                    isEdge set ((minD lt sameColorThresh) and (maxD gt sameColorThresh))

                    val ma by metaEqual(curMeta, oldMeta.load(baseCoord + int2Value(-1, -1)).r)
                    val mb by metaEqual(curMeta, oldMeta.load(baseCoord + int2Value(1, 1)).r)
                    val mc by metaEqual(curMeta, oldMeta.load(baseCoord + int2Value(1, -1)).r)
                    val md by metaEqual(curMeta, oldMeta.load(baseCoord + int2Value(-1, 1)).r)

                    val anyMtrue by ma or mb or mc or md
                    val anyMfalse by !(ma and mb and mc and md)
                    isEdge set (isEdge or (anyMtrue and anyMfalse))
                }
                state set isEdge.toInt1()
                filterState.store(baseCoord, float4Value(state.toFloat1() / 255f.const, 0f.const, 0f.const, 0f.const))

//                val w by 4f.const
                val w by 8f.const //- filterNoise * filterNoise * filterNoise * 4f.const
//                val w by 16f.const - filterNoise * filterNoise * filterNoise * 14f.const
//                val w by 32f.const - filterNoise * filterNoise * filterNoise * 16f.const
//                val w by 100f.const - filterNoise * filterNoise * filterNoise * 75f.const

                `if`((!filterHit and !isEdge) or (wasEdge and !isEdge)) {
                    w set 0f.const
                }

                val curColor by lightingOutput.load(baseCoord).rgb
                val curSrgb by convertColorSpace(curColor, ColorSpaceConversion.LinearToSrgb())
                val oldSrgb by convertColorSpace(oldFilter.load(baseCoord).rgb, ColorSpaceConversion.LinearToSrgb())
                val weighted by (oldSrgb * w + curSrgb) / (w + 1f.const)
                val filtered by convertColorSpace(weighted, ColorSpaceConversion.SrgbToLinear())

                `if`(any(isNan(filtered))) {
                    filtered set curSrgb
                }

//                val filteredColor by (old * w + curColor) / (w + 1f.const)
                newFilter[baseCoord] = float4Value(filtered, 1f)
//                newFilter[baseCoord] = float4Value(current, 1f)

//                val x1 by (curMeta shr 16.const) and 0xff.const
//                val y1 by (curMeta shr 24.const) and 0xff.const
//                newFilter[baseCoord] = float4Value(x1.toFloat1() / 127f.const, y1.toFloat1() / 127f.const, 0f.const, 1f.const)

//                newFilter[baseCoord] = float4Value(mix(curColor, curAlbedo, 0.5f.const), 1f)

//                newFilter[baseCoord] = float4Value(colorDiff, colorDiff, colorDiff, 1f.const)
//                `if`(wasEdge and !isEdge) {
//                    newFilter[baseCoord] = Color.RED.const
//                }
//                `if`(isEdge) {
//                    newFilter[baseCoord] = Color.YELLOW.const
//                }
//                `if`(w eq 0f.const) {
//                    newFilter[baseCoord] = Color.CYAN.const
//                }
            }
        }
    }
}

object NewOldCamDataStruct : Struct("NewOldCamData", MemoryLayout.Std140) {
    val oldCam = struct(DeferredCamDataStruct)
    val newCam = struct(DeferredCamDataStruct)
}
