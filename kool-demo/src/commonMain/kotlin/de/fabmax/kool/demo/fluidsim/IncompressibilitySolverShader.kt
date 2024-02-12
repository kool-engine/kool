package de.fabmax.kool.demo.fluidsim

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.StorageBuffer2d
import kotlin.random.Random

class IncompressibilitySolverShader(
    uState: StorageBuffer2d,
    vState: StorageBuffer2d,
    borderState: StorageBuffer2d,
    randomIndices: StorageBuffer2d
) : KslComputeShader("Incompressibility solver") {

    var overRelaxation: Float by uniform1f("overRelaxation", 1f)
    var indexOffset: Vec2i by uniform2i("idxOffset")

    init {
        storage2d("uVals", uState)
        storage2d("vVals", vState)
        storage2d("borderState", borderState)

        storage2d("indices", randomIndices)

        program.solverProg()
    }

    private fun KslProgram.solverProg() {
        // larger work group sizes are faster but result in less stable simulation
        computeStage(2, 2) {
            val uVals = storage2d<KslInt1>("uVals")
            val vVals = storage2d<KslInt1>("vVals")
            val indices = storage2d<KslInt1>("indices")
            val borderState = storage2d<KslInt1>("borderState")
            val idxOffset = uniformInt2("idxOffset")
            val overRelaxation = uniformFloat1("overRelaxation")

            main {
                val size = int2Var((inNumWorkGroups.xy * inWorkGroupSize.xy).toInt2())
                val bounds = int2Var(size - Vec2i.ONES.const)
                val offsetCoord = int2Var((inGlobalInvocationId.xy.toInt2() + idxOffset) % size)

                // Randomize access pattern to improve simulation stability.
                val randomIndex = uint1Var(storageRead(indices, offsetCoord).toUint1())
                val randomX = int1Var((randomIndex shr 16u.const).toInt1())
                val randomY = int1Var((randomIndex and 0xffffu.const).toInt1())
                val ij = int2Var(int2Value(randomX, randomY))

                val s = float1Var(storageRead(borderState, ij).toFloat1())
                `if` ((s eq 1f.const) and all(ij gt Vec2i.ZERO.const) and all(ij lt bounds)) {
                    val x = ij.x
                    val y = ij.y
                    val c = 1.const

                    val sx0 = float1Var(storageRead(borderState, int2Value(x-c, y)).toFloat1())
                    val sx1 = float1Var(storageRead(borderState, int2Value(x+c, y)).toFloat1())
                    val sy0 = float1Var(storageRead(borderState, int2Value(x, y-c)).toFloat1())
                    val sy1 = float1Var(storageRead(borderState, int2Value(x, y+c)).toFloat1())

                    s set sx0 + sx1 + sy0 + sy1
                    `if`(s gt 0f.const) {
                        val di = int1Var(0.const)
                        di += storageRead(uVals, int2Value(x+c, y))
                        di -= storageRead(uVals, int2Value(x, y))
                        di += storageRead(vVals, int2Value(x, y+c))
                        di -= storageRead(vVals, int2Value(x, y))

                        // div is clamped as a safety measure: keeps simulation instabilities locally
                        // otherwise it explodes as soon as instabilities occur
                        val div = float1Var(clamp(di.toFloating(), (-1f).const, 1f.const))
                        val p = float1Var(-div / s * overRelaxation)

                        // Write back modified field velocities.
                        // Notice that velocities are written to the same storage they were read from. This makes
                        // the algorithm inherently unstable in multi-threaded environments (and a compute shader
                        // is very heavily multi-threaded). Unfortunately this is required to make the algorithm work
                        // at all. Instabilities are mitigated by the random access pattern and value sanitizing
                        // techniques used above.
                        //
                        // Also notice that storageAtomicAdd() is assigned to a variable to make sure it is
                        // executed (because it is an expression and not a statement)
                        int1Var(storageAtomicAdd(uVals, int2Value(x, y), -(sx0 * p).toFixed()))
                        int1Var(storageAtomicAdd(uVals, int2Value(x+c, y), (sx1 * p).toFixed()))
                        int1Var(storageAtomicAdd(vVals, int2Value(x, y), -(sy0 * p).toFixed()))
                        int1Var(storageAtomicAdd(vVals, int2Value(x, y+c), (sy1 * p).toFixed()))
                    }

                }.`else` {
                    // copy fluid states in border cells from neighboring cells
                    `if`(ij.y eq 0.const) {
                        storageWrite(vVals, ij, storageRead(vVals, ij + Vec2i(0, 1).const))
                    }
                    `if`(ij.y gt bounds.y) {
                        storageWrite(vVals, ij, storageRead(vVals, ij + Vec2i(0, -1).const))
                    }
                    `if`(ij.x eq 0.const) {
                        storageWrite(uVals, ij, storageRead(uVals, ij + Vec2i(1, 0).const))
                    }
                    `if`(ij.x gt bounds.x) {
                        storageWrite(vVals, ij, storageRead(vVals, ij + Vec2i(-1, 0).const))
                    }
                }
            }
        }
    }

    companion object {
        fun makeRandomAccessIndices(simWidth: Int, simHeight: Int): StorageBuffer2d {
            val randomIndices = StorageBuffer2d(simWidth, simHeight, GpuType.INT1)

            randomIndices.writeInts { buf ->
                val indices = mutableListOf<Vec2i>()
                for (y in 0 ..< simHeight) {
                    for (x in 0 ..< simWidth) {
                        indices += Vec2i(x, y)
                    }
                }
                indices.shuffle(Random(31))

                for (y in 0 ..< simHeight) {
                    for (x in 0 ..< simWidth) {
                        val idx = indices[y * simWidth + x]
                        buf.put((idx.x shl 16) or idx.y)
                    }
                }
            }
            return randomIndices
        }
    }
}