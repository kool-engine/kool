package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.blocks.getLinearDepthReversed
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
    val filterState = StorageTexture2d(size.x, size.y, TexFormat.R, samplerSettings = SamplerSettings().clamped().nearest())

    private val temporalShader = TemporalFilterShader(filterStorageFmt, lightingOutput, filterState)

    var filterWeight by temporalShader.bindUniformFloat1("uFilterWeight", 8f)

    init {
        setupPasses()
        onRelease {
            filterOutput.a.release()
            filterOutput.b.release()
            filterState.release()
        }
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
            oldDepth = oldGbuffer.depth
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
    var oldMeta by bindTexture2d("oldMeta")
    var newMeta by bindTexture2d("newMeta")
    var newDepth by bindTexture2d("newDepth")
    var oldDepth by bindTexture2d("oldDepth")

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
            val oldMeta = texture2dInt("oldMeta")
            val newMeta = texture2dInt("newMeta")
            val newDepth = texture2d("newDepth", isUnfilterable = true)
            val oldDepth = texture2d("oldDepth", isUnfilterable = true)

            val invModelMatStruct = struct(ObjModelMatLayout)
            val objModelMats = storage("objModelMats", invModelMatStruct)

            val oldFilter = texture2d("oldFilter")
            val newFilter = if (filterStorageFmt.channels == 3) {
                storageTexture2d<KslFloat3>("newFilter", filterStorageFmt)
            } else {
                storageTexture2d<KslFloat4>("newFilter", filterStorageFmt)
            }

            val filterState = storageTexture2d<KslFloat1>("filterState", TexFormat.R)
//            val frameI = uniformInt1("frameI")
            val camData = uniformStruct("camData", FilterCamDataStruct)

            val filterWeight = uniformFloat1("uFilterWeight")

            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val curMeta by newMeta.load(baseCoord).r
                val id by curMeta and 0xffffff.const
                val oldSize by oldFilter.size().toFloat2()

                val camNear = camData[FilterCamDataStruct.camNear]
                val invViewProj = camData[FilterCamDataStruct.invViewProj]
                val worldPos by unprojectBaseCoord(newDepth, baseCoord, camNear, invViewProj)

                val oldUv by (baseCoord.toFloat2() + 0.5f.const2) / oldSize
                val oldBaseCoord by baseCoord
                `if`(id ne 0.const) {
                    val objModelMat = structVar(objModelMats[id])
                    val oldProj by objModelMat[ObjModelMatLayout.reprojectMat] * worldPos
                    oldUv set oldProj.xy / oldProj.w * float2Value(0.5f, -0.5f) + 0.5f.const
                    oldBaseCoord set (oldUv * oldSize).toInt2()
                }

                val oldStateBaseUv by oldUv * oldSize
                val oldState by
                    (filterState.load((oldStateBaseUv + float2Value(0.5f, 0.5f)).toInt2()).r * 255f.const).toInt1() or
                    (filterState.load((oldStateBaseUv + float2Value(0.5f, -0.5f)).toInt2()).r * 255f.const).toInt1() or
                    (filterState.load((oldStateBaseUv + float2Value(-0.5f, -0.5f)).toInt2()).r * 255f.const).toInt1() or
                    (filterState.load((oldStateBaseUv + float2Value(-0.5f, 0.5f)).toInt2()).r * 255f.const).toInt1()
                val wasEdge by oldState and 1.const gt 0.const

                val near by camData[FilterCamDataStruct.camNear]
                val refDepth by getLinearDepthReversed(newDepth.load(baseCoord).x, near)
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

                oldState set isEdge.toInt1()
                filterState.store(baseCoord, float4Value(oldState.toFloat1() / 255f.const, 0f.const, 0f.const, 0f.const))

//                val filterNoise by noise31(uint3Value(inGlobalInvocationId.xy, frameI.toUint1()))
//                val w by 4f.const
//                val w by 8f.const //- filterNoise * filterNoise * filterNoise * 4f.const
//                val w by 16f.const //- filterNoise * filterNoise * filterNoise * 14f.const
//                val w by 32f.const - filterNoise * filterNoise * filterNoise * 16f.const
//                val w by 100f.const - filterNoise * filterNoise * filterNoise * 75f.const

                val w by filterWeight
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

//                `if`(wasEdge and !isEdge) {
//                    filtered set Color.RED.const.rgb
//                }
//                `if`(isEdge) {
//                    filtered set Color.YELLOW.const.rgb
//                }
//                `if`(!filterHit) {
//                    filtered set Color.CYAN.const.rgb
//                }
//                `if`(w eq 0f.const) {
//                    newFilter[baseCoord] = Color.CYAN.const
//                }
                newFilter[baseCoord] = float4Value(filtered, 1f)
            }
        }
    }
}

object FilterCamDataStruct : Struct("FilterCamData", MemoryLayout.Std140) {
    val invViewProj = mat4("invViewProj")
    val camNear = float1("camClipNear")
}
