package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

class ReprojectComputePass(
    val maxObjects: Int = 65536,
    private val pipeline: Deferred2Pipeline
) : ComputePass("deferred2-reproject-compute-pass") {

    val uploadData = AlternatingPair {
        UploadData(maxObjects)
    }

    val modelMats = AlternatingPair {
        StorageBuffer(GpuType.Mat4, maxObjects)
    }
    val reprojectMats = StorageBuffer(GpuType.Mat4, maxObjects)

    private val shader = ReprojectComputeShader(reprojectMats)

    init {
        val groupsX = (maxObjects + 63) / 64
        addTask(shader, Vec3i(groupsX, 1, 1))
        onRelease {
            modelMats.a.release()
            modelMats.b.release()
            reprojectMats.release()
        }
    }

    fun swapBuffers() {
        val newUpload = uploadData.newVal
        modelMats.newVal.uploadData(newUpload.modelMats)
        shader.swapPipelineData(newUpload) {
            inputOldViewProj = uploadData.oldVal.viewProjMat
            oldModelMats = modelMats.oldVal
            newModelMats = modelMats.newVal
            numMatrices = pipeline.idAllocator.size
        }
    }
}

private fun UploadData(size: Int) = UploadData(Float32Buffer(size * 16), MutableMat4f())

class UploadData(val modelMats: Float32Buffer, val viewProjMat: MutableMat4f)

private class ReprojectComputeShader(
    reprojectMats: GpuBuffer,
) : KslComputeShader("reproject-compute") {

    var numMatrices by bindUniformInt1("numMatrices")
    var inputOldViewProj by bindUniformMat4("oldViewProj")
    var oldModelMats by bindStorage("oldModelMats")
    var newModelMats by bindStorage("newModelMats")

    init {
        bindStorage("reprojectMats", reprojectMats)
        program.program()
    }

    private fun KslProgram.program() {
        computeStage(workGroupSizeX = 64) {
            val numMatrices = uniformInt1("numMatrices")
            val oldViewProj = uniformMat4("oldViewProj")
            val matStruct = struct(StorageMatLayout)
            val oldModelMats = storage("oldModelMats", matStruct)
            val newModelMats = storage("newModelMats", matStruct)
            val reprojectMats = storage("reprojectMats", matStruct)

            main {
                val idx by inGlobalInvocationId.x.toInt1()
                `if`(idx ge numMatrices) {
                    `return`()
                }

                val model = mat4Var(newModelMats[idx][StorageMatLayout.mat])
                val det by model.run {
                    m03*m12*m21*m30 - m02*m13*m21*m30 - m03*m11*m22*m30 + m01*m13*m22*m30 +
                    m02*m11*m23*m30 - m01*m12*m23*m30 - m03*m12*m20*m31 + m02*m13*m20*m31 +
                    m03*m10*m22*m31 - m00*m13*m22*m31 - m02*m10*m23*m31 + m00*m12*m23*m31 +
                    m03*m11*m20*m32 - m01*m13*m20*m32 - m03*m10*m21*m32 + m00*m13*m21*m32 +
                    m01*m10*m23*m32 - m00*m11*m23*m32 - m02*m11*m20*m33 + m01*m12*m20*m33 +
                    m02*m10*m21*m33 - m00*m12*m21*m33 - m01*m10*m22*m33 + m00*m11*m22*m33
                }
                `if`(det eq 0f.const) {
                    `return`()
                }

                val inverseModelMat by model.run {
                    val r00 by m12*m23*m31 - m13*m22*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33
                    val r01 by m03*m22*m31 - m02*m23*m31 - m03*m21*m32 + m01*m23*m32 + m02*m21*m33 - m01*m22*m33
                    val r02 by m02*m13*m31 - m03*m12*m31 + m03*m11*m32 - m01*m13*m32 - m02*m11*m33 + m01*m12*m33
                    val r03 by m03*m12*m21 - m02*m13*m21 - m03*m11*m22 + m01*m13*m22 + m02*m11*m23 - m01*m12*m23
                    val r10 by m13*m22*m30 - m12*m23*m30 - m13*m20*m32 + m10*m23*m32 + m12*m20*m33 - m10*m22*m33
                    val r11 by m02*m23*m30 - m03*m22*m30 + m03*m20*m32 - m00*m23*m32 - m02*m20*m33 + m00*m22*m33
                    val r12 by m03*m12*m30 - m02*m13*m30 - m03*m10*m32 + m00*m13*m32 + m02*m10*m33 - m00*m12*m33
                    val r13 by m02*m13*m20 - m03*m12*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 + m00*m12*m23
                    val r20 by m11*m23*m30 - m13*m21*m30 + m13*m20*m31 - m10*m23*m31 - m11*m20*m33 + m10*m21*m33
                    val r21 by m03*m21*m30 - m01*m23*m30 - m03*m20*m31 + m00*m23*m31 + m01*m20*m33 - m00*m21*m33
                    val r22 by m01*m13*m30 - m03*m11*m30 + m03*m10*m31 - m00*m13*m31 - m01*m10*m33 + m00*m11*m33
                    val r23 by m03*m11*m20 - m01*m13*m20 - m03*m10*m21 + m00*m13*m21 + m01*m10*m23 - m00*m11*m23
                    val r30 by m12*m21*m30 - m11*m22*m30 - m12*m20*m31 + m10*m22*m31 + m11*m20*m32 - m10*m21*m32
                    val r31 by m01*m22*m30 - m02*m21*m30 + m02*m20*m31 - m00*m22*m31 - m01*m20*m32 + m00*m21*m32
                    val r32 by m02*m11*m30 - m01*m12*m30 - m02*m10*m31 + m00*m12*m31 + m01*m10*m32 - m00*m11*m32
                    val r33 by m01*m12*m20 - m02*m11*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 + m00*m11*m22

                    val s by 1f.const / det
                    mat4Value(
                        col0 = float4Value(r00 * s, r10 * s, r20 * s, r30 * s),
                        col1 = float4Value(r01 * s, r11 * s, r21 * s, r31 * s),
                        col2 = float4Value(r02 * s, r12 * s, r22 * s, r32 * s),
                        col3 = float4Value(r03 * s, r13 * s, r23 * s, r33 * s),
                    )
                }

                val oldMvp by oldViewProj * oldModelMats[idx][StorageMatLayout.mat]
                val r = structVar(matStruct)
                r[StorageMatLayout.mat] set oldMvp * inverseModelMat
                reprojectMats[idx] = r
            }
        }
    }
}

private val KslExprMat4.m00: KslExprFloat1 get() = this[0].x
private val KslExprMat4.m10: KslExprFloat1 get() = this[0].y
private val KslExprMat4.m20: KslExprFloat1 get() = this[0].z
private val KslExprMat4.m30: KslExprFloat1 get() = this[0].w

private val KslExprMat4.m01: KslExprFloat1 get() = this[1].x
private val KslExprMat4.m11: KslExprFloat1 get() = this[1].y
private val KslExprMat4.m21: KslExprFloat1 get() = this[1].z
private val KslExprMat4.m31: KslExprFloat1 get() = this[1].w

private val KslExprMat4.m02: KslExprFloat1 get() = this[2].x
private val KslExprMat4.m12: KslExprFloat1 get() = this[2].y
private val KslExprMat4.m22: KslExprFloat1 get() = this[2].z
private val KslExprMat4.m32: KslExprFloat1 get() = this[2].w

private val KslExprMat4.m03: KslExprFloat1 get() = this[3].x
private val KslExprMat4.m13: KslExprFloat1 get() = this[3].y
private val KslExprMat4.m23: KslExprFloat1 get() = this[3].z
private val KslExprMat4.m33: KslExprFloat1 get() = this[3].w

object StorageMatLayout : Struct("mat_storage", MemoryLayout.Std140) {
    val mat = mat4("mat")
}
