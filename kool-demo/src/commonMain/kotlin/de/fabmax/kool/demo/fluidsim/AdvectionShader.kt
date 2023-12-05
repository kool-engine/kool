package de.fabmax.kool.demo.fluidsim

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.StorageTexture2d

class AdvectionShader(
    uStateIn: StorageTexture2d,
    vStateIn: StorageTexture2d,
    smokeIn: StorageTexture2d,
    uStateOut: StorageTexture2d,
    vStateOut: StorageTexture2d,
    smokeOut: StorageTexture2d,
    borderState: StorageTexture2d
) : KslComputeShader("Advection Projection") {
    var advectionStep: Float by uniform1f("advectionStep", 1f)

    init {
        storage2d("uStateIn", uStateIn)
        storage2d("vStateIn", vStateIn)
        storage2d("smokeIn", smokeIn)

        storage2d("uStateOut", uStateOut)
        storage2d("vStateOut", vStateOut)
        storage2d("smokeOut", smokeOut)

        storage2d("borderState", borderState)

        program.advectionProg()
    }

    private fun KslProgram.advectionProg() {
        computeStage(8, 8) {
            val uStateIn = storage2d<KslInt1>("uStateIn")
            val vStateIn = storage2d<KslInt1>("vStateIn")
            val uStateOut = storage2d<KslInt1>("uStateOut")
            val vStateOut = storage2d<KslInt1>("vStateOut")

            val smokeIn = storage2d<KslInt1>("smokeIn")
            val smokeOut = storage2d<KslInt1>("smokeOut")

            val borderState = storage2d<KslInt1>("borderState")

            val advectionStep = uniformFloat1("advectionStep")

            val funcAvgU = functionFloat1("avgU") {
                val ij = paramInt2()
                body {
                    val a = int1Var(storageRead(uStateIn, ij + Vec2i(0, 0).const))
                    val b = int1Var(storageRead(uStateIn, ij + Vec2i(0, -1).const))
                    val c = int1Var(storageRead(uStateIn, ij + Vec2i(1, -1).const))
                    val d = int1Var(storageRead(uStateIn, ij + Vec2i(1, 0).const))
                    (a + b + c + d).toFloating() * 0.25f.const
                }
            }

            val funcAvgV = functionFloat1("avgV") {
                val ij = paramInt2()
                body {
                    val a = int1Var(storageRead(vStateIn, ij + Vec2i(0, 0).const))
                    val b = int1Var(storageRead(vStateIn, ij + Vec2i(-1, 0).const))
                    val c = int1Var(storageRead(vStateIn, ij + Vec2i(-1, 1).const))
                    val d = int1Var(storageRead(vStateIn, ij + Vec2i(0, 1).const))
                    (a + b + c + d).toFloating() * 0.25f.const
                }
            }

            val funcIsBorder = functionBool1("isBorder") {
                val ij = paramInt2()
                body {
                    val a = int1Var(storageRead(borderState, ij + Vec2i(0, 0).const))
                    val b = int1Var(storageRead(borderState, ij + Vec2i(0, 1).const))
                    val c = int1Var(storageRead(borderState, ij + Vec2i(1, 0).const))
                    val d = int1Var(storageRead(borderState, ij + Vec2i(1, 1).const))

                    (a + b + c + d) eq 0.const
                }
            }

            main {
                val bounds = int2Var((inNumWorkGroups.xy * inWorkGroupSize.xy).toInt2() - Vec2i(0, 1).const)
                val ij = int2Var(inGlobalInvocationId.xy.toInt2())

                val u = float1Var()
                val v = float1Var()
                val xy = float2Var(ij.toFloat2())
                val dx = float1Var(0f.const)
                val dy = float1Var(0f.const)

                // u component
                u set storageRead(uStateIn, ij).toFloating()
                v set funcAvgV(ij)
                dx set -advectionStep * u
                dy set -advectionStep * v
                `if`(funcIsBorder(ij + float2Value(dx, dy).toInt2())) {
                    dx set 0f.const
                    dy set 0f.const
                }
                val uOut = float1Var(sampleField(xy + float2Value(dx, dy), uStateIn, bounds))
                storageWrite(uStateOut, ij, uOut.toFixed())

                // v component
                v set storageRead(vStateIn, ij).toFloating()
                u set funcAvgU(ij)
                dx set -advectionStep * u
                dy set -advectionStep * v
                `if`(funcIsBorder(ij + float2Value(dx, dy).toInt2())) {
                    dx set 0f.const
                    dy set 0f.const
                }
                val vOut = float1Var(sampleField(xy + float2Value(dx, dy), vStateIn, bounds))
                storageWrite(vStateOut, ij, vOut.toFixed())

                // smokeDensity
                dx set -advectionStep * uOut
                dy set -advectionStep * vOut
                `if`(funcIsBorder(ij + float2Value(dx, dy).toInt2())) {
                    dx set 0f.const
                    dy set 0f.const
                }
                val smokeDensity = float1Var(sampleField(xy + float2Value(dx, dy), smokeIn, bounds))
                storageWrite(smokeOut, ij, smokeDensity.toFixed())
            }
        }
    }

    private fun KslScopeBuilder.sampleField(
        xy: KslExprFloat2,
        storage: KslStorage2d<KslStorage2dType<KslInt1>>,
        bounds: KslExprInt2,
    ): KslScalarExpression<KslFloat1> {
        val ij = int2Var(xy.toInt2())
        val s00 = float1Var(storageRead(storage, min(max(ij + Vec2i(0, 0).const, Vec2i.ONES.const), bounds)).toFloating())
        val s01 = float1Var(storageRead(storage, min(max(ij + Vec2i(0, 1).const, Vec2i.ONES.const), bounds)).toFloating())
        val s10 = float1Var(storageRead(storage, min(max(ij + Vec2i(1, 0).const, Vec2i.ONES.const), bounds)).toFloating())
        val s11 = float1Var(storageRead(storage, min(max(ij + Vec2i(1, 1).const, Vec2i.ONES.const), bounds)).toFloating())

        val wx1 = float1Var(fract(xy.x))
        val wy1 = float1Var(fract(xy.y))
        val wx0 = float1Var(1f.const - wx1)
        val wy0 = float1Var(1f.const - wy1)

        return float1Var(
            s00 * wx0 * wy0 +
                    s01 * wx0 * wy1 +
                    s10 * wx1 * wy0 +
                    s11 * wx1 * wy1
        )
    }
}