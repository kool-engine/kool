package de.fabmax.kool.demo.fluidsim

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.StorageTexture2d

class CopyStateShader(
    uStateIn: StorageTexture2d,
    vStateIn: StorageTexture2d,
    smokeIn: StorageTexture2d,
    uStateOut: StorageTexture2d,
    vStateOut: StorageTexture2d,
    smokeOut: StorageTexture2d,
    borderStateOut: StorageTexture2d,
    drawOutput: StorageTexture2d,
) : KslComputeShader("Copy State Shader") {

    var clearFlag by uniform1i("clearFlag", 0)
    var flowSpeed by uniform1f("flowSpeed", 2f)

    var obstaclePos by uniform2f("obsPos", Vec2f(64f, 128f))
    var obstacleRadius by uniform1f("obsRadius", 12f)

    init {
        // set storage textures as shader inputs
        storage2d("uStateIn", uStateIn)
        storage2d("vStateIn", vStateIn)
        storage2d("smokeIn", smokeIn)
        storage2d("uStateOut", uStateOut)
        storage2d("vStateOut", vStateOut)
        storage2d("smokeOut", smokeOut)
        storage2d("borderStateOut", borderStateOut)
        storage2d("drawOutput", drawOutput)

        program.copyProgram()
    }

    fun KslProgram.copyProgram() {
        computeStage(8, 8) {
            val uStateIn = storage2d<KslInt1>("uStateIn")
            val vStateIn = storage2d<KslInt1>("vStateIn")
            val smokeIn = storage2d<KslInt1>("smokeIn")
            val uStateOut = storage2d<KslInt1>("uStateOut")
            val vStateOut = storage2d<KslInt1>("vStateOut")
            val smokeOut = storage2d<KslInt1>("smokeOut")
            val borderStateOut = storage2d<KslInt1>("borderStateOut")
            val drawOutput = storage2d<KslFloat4>("drawOutput")

            main {
                val size = int2Var(inNumWorkGroups.xy.toInt2() * inWorkGroupSize.xy.toInt2())
                val clearFlag = int1Var(uniformInt1("clearFlag"))
                val ij = int2Var(inGlobalInvocationId.xy.toInt2())

                val u = int1Var(storageRead(uStateIn, ij))
                val v = int1Var(storageRead(vStateIn, ij))
                val d = int1Var(storageRead(smokeIn, ij))
                u *= (1.const - clearFlag)
                v *= (1.const - clearFlag)
                d *= (1.const - clearFlag)

                `if`(ij.x eq 1.const) {
                    u set uniformFloat1("flowSpeed").toFixed()

                    val center = size.y / 2.const
                    val smokeExt = 5.const
                    `if`((ij.y le center + smokeExt) and (ij.y ge center - smokeExt)) {
                        d set 1f.const.toFixed()
                    }.`else` {
                        d set 0.const
                    }
                }

                val s = int1Var(1.const)
                `if`((ij.x eq 0.const) or (ij.y eq 0.const) or (ij.y eq size.y - 1.const)) {
                    s set 0.const
                }.elseIf(length(ij.toFloat2() - uniformFloat2("obsPos")) lt uniformFloat1("obsRadius")) {
                    s set 0.const
                    u set 0.const
                    v set 0.const
                    d set 0.const
                }

                storageWrite(uStateOut, ij, u)
                storageWrite(vStateOut, ij, v)
                storageWrite(smokeOut, ij, d)
                storageWrite(borderStateOut, ij, s)
                storageWrite(drawOutput, ij, float4Value(u.toFloating(), v.toFloating(), d.toFloating(), s.toFloat1()))
            }
        }
    }
}