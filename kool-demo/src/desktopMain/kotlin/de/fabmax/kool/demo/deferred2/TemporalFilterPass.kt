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
        StorageTexture2d(size.x, size.y, filterStorageFmt, samplerSettings = SamplerSettings().clamped())
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
            val newGbuffer = gbuffers.newVal
            val oldGbuffer = gbuffers.oldVal

            newDepth = newGbuffer.depth
            oldAlbedo = oldGbuffer.albedoEmission
            newAlbedo = newGbuffer.albedoEmission
            oldMeta = oldGbuffer.objectIds
            newMeta = newGbuffer.objectIds
            oldFilter = filterOutput.oldVal
            newFilter = filterOutput.newVal
            frameI = Time.frameCount

            objModelMats = newGbuffer.objModelMatsGpu
            camData.set {
                set(it.invViewProj, camera.invViewProj)
                set(it.camNear, camera.clipNear)
            }
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
    var newDepth by bindTexture2d("newDepth")

    var objModelMats by bindStorage("objModelMats")
    var camData = bindUniformStruct("camData", FilterCamDataStruct)

    var oldFilter by bindTexture2d("oldFilter", defaultSampler = SamplerSettings().clamped().linear())
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
            val newDepth = texture2d("newDepth", isUnfilterable = true)

            val invModelMatStruct = struct(ObjModelMatLayout)
            val objModelMats = storage("objModelMats", invModelMatStruct)
//            val oldObjModelMats = storage("oldObjModelMats", invModelMatStruct)

            val oldFilter = texture2d("oldFilter")
            val newFilter = if (filterStorageFmt.channels == 3) {
                storageTexture2d<KslFloat3>("newFilter", filterStorageFmt)
            } else {
                storageTexture2d<KslFloat4>("newFilter", filterStorageFmt)
            }

            val filterState = storageTexture2d<KslFloat1>("filterState", TexFormat.R)

            val frameI = uniformInt1("frameI")

            val camData = uniformStruct("camData", FilterCamDataStruct)

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
                val curMeta by newMeta.load(baseCoord).r
                val id by curMeta and 0xffffff.const

                val oldUv by (baseCoord.toFloat2() + 0.5f.const2) / oldFilter.size().toFloat2()
                val oldBaseCoord by baseCoord
                `if`(id ne 0.const) {
                    val camNear = camData[FilterCamDataStruct.camNear]
                    val invViewProj = camData[FilterCamDataStruct.invViewProj]
                    val worldPos by unprojectBaseCoord(newDepth, baseCoord, camNear, invViewProj)
                    val objModelMat = structVar(objModelMats[id])
                    val oldProj by objModelMat[ObjModelMatLayout.reprojectMat] * worldPos
                    oldUv set oldProj.xy / oldProj.w * float2Value(0.5f, -0.5f) + 0.5f.const
                    oldBaseCoord set (oldUv * oldFilter.size().toFloat2()).toInt2()
                }

                val oldStateCa by (oldUv * oldFilter.size().toFloat2() + float2Value(0.5f, 0.5f)).toInt2()
                val oldStateCb by (oldUv * oldFilter.size().toFloat2() + float2Value(0.5f, -0.5f)).toInt2()
                val oldStateCc by (oldUv * oldFilter.size().toFloat2() + float2Value(-0.5f, -0.5f)).toInt2()
                val oldStateCd by (oldUv * oldFilter.size().toFloat2() + float2Value(-0.5f, 0.5f)).toInt2()
                val oldState by (filterState.load(oldStateCa).r * 255f.const).toInt1() or
                    (filterState.load(oldStateCb).r * 255f.const).toInt1() or
                    (filterState.load(oldStateCc).r * 255f.const).toInt1() or
                    (filterState.load(oldStateCd).r * 255f.const).toInt1()
                val wasEdge by oldState and 1.const gt 0.const

                val sameColorThresh = 0.25f.const
                val curAlbedo by float4Var(newAlbedo.load(baseCoord))
                val colorDiff by length(curAlbedo - oldAlbedo.sample(oldUv))
                val sameColor by colorDiff lt sameColorThresh
                val filterHit by sameColor
                val isEdge by false.const
                `if`(!filterHit or wasEdge) {
//                    val depth0 by 1f.const / newDepth.load(baseCoord, lod = 0.const).x
//                    val da by depthEdge(depth0, newDepth.load(baseCoord + int2Value(-1, -1)).x)
//                    val db by depthEdge(depth0, newDepth.load(baseCoord + int2Value(1, 1)).x)
//                    val dc by depthEdge(depth0, newDepth.load(baseCoord + int2Value(1, -1)).x)
//                    val dd by depthEdge(depth0, newDepth.load(baseCoord + int2Value(-1, 1)).x)
//                    isEdge set (da or db or dc or dd)

                    val oldPxSz by 1f.const2 / oldFilter.size().toFloat2()
                    val da by length(curAlbedo - oldAlbedo.sample(oldUv + float2Value(-1f, -1f) * oldPxSz))
                    val db by length(curAlbedo - oldAlbedo.sample(oldUv + float2Value(1f, 1f) * oldPxSz))
                    val dc by length(curAlbedo - oldAlbedo.sample(oldUv + float2Value(1f, -1f) * oldPxSz))
                    val dd by length(curAlbedo - oldAlbedo.sample(oldUv + float2Value(-1f, 1f) * oldPxSz))

                    val minD = min(colorDiff, min(min(da, db), min(dc, dd)))
                    val maxD = max(colorDiff, max(max(da, db), max(dc, dd)))
                    isEdge set ((minD lt sameColorThresh) and (maxD gt sameColorThresh))
                }

                oldState set isEdge.toInt1()
                filterState.store(baseCoord, float4Value(oldState.toFloat1() / 255f.const, 0f.const, 0f.const, 0f.const))

//                val filterNoise by noise31(uint3Value(inGlobalInvocationId.xy, frameI.toUint1()))
//                val w by 4f.const
//                val w by 8f.const //- filterNoise * filterNoise * filterNoise * 4f.const
                val w by 16f.const //- filterNoise * filterNoise * filterNoise * 14f.const
//                val w by 32f.const - filterNoise * filterNoise * filterNoise * 16f.const
//                val w by 100f.const - filterNoise * filterNoise * filterNoise * 75f.const

                `if`((!filterHit and !isEdge) or (wasEdge and !isEdge)) {
                    w set 0f.const
                }

                val curColor by lightingOutput.load(baseCoord).rgb
                val oldColor by oldFilter.sample(oldUv).rgb
                val curSrgb by convertColorSpace(curColor, ColorSpaceConversion.LinearToSrgb())
                val oldSrgb by convertColorSpace(oldColor, ColorSpaceConversion.LinearToSrgb())
                val weighted by (oldSrgb * w + curSrgb) / (w + 1f.const)
                val filtered by convertColorSpace(weighted, ColorSpaceConversion.SrgbToLinear())

                `if`(any(isNan(filtered))) {
                    filtered set curSrgb
                }
                newFilter[baseCoord] = float4Value(filtered, 1f)

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

context(scope: KslScopeBuilder)
private fun depthEdge(baseDepthLin: KslExprFloat1, cmpDepth: KslExprFloat1): KslExprBool1 {
    val cmpDepthLin = float1Var(1f.const / cmpDepth)
    return min(baseDepthLin, cmpDepthLin) / max(baseDepthLin, cmpDepthLin) lt 0.99f.const
}

object FilterCamDataStruct : Struct("FilterCamData", MemoryLayout.Std140) {
    val invViewProj = mat4("invViewProj")
    val camNear = float1("camClipNear")
}
