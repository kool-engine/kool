package de.fabmax.kool.demo

import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.modules.ksl.lang.*

fun main() {
    val program = KslProgram().apply {
        val mvp = uniformMat4("uMvp")

        val vertColor = interStageFloat4()

        vertexStage {
            val color = vertexAttribFloat4("aColor")

            main {
                val floatVar = floatVar()
                floatVar `=` 17f.const
                val intVar = intVar()
                intVar `=` 8.const

                val floatArray = floatArray(8)
                floatArray[4] `=` floatVar

                val position = float4Var("aPosition")
                position `=` color * 3f.const

                val int4 = int4Var()
                int4 `=` position.toInt4()

                floatVar `=` position.x
                intVar `=` int4.y

                vertColor.input `=` int4.toFloat4() / 255f.const

                val boolVecExpr = constFloat2(1f, 2f) lt constFloat2(2f, 1f)

                `if` (4f.const lt 6f.const) {
                    val outPos = float4Var()
                    outPos `=` mvp * position

                }.`else if`(any(boolVecExpr) or all(boolVecExpr)) {
                    val float2 = float2Var()
                    float2 `=` constFloat2(1f, 2f) + floatArray[4]

                }.`else` {
                    val float3 = float3Var()
                    float3 `=` constFloat3(1f, 2f, 3f)
                    float3 `=` float3 - floatVar
                }

            }
        }

        fragmentStage {
            main {
                outColor() `=` vertColor.output
                outDepth `=` 17f.const
            }
        }
    }

    program.vertexStage.hierarchy.globalScope.ops.shuffle()

    val src = GlslGenerator().generateProgram(program)
    println("// vertex stage:")
    println(src.vertexSrc)
    println("\n// fragment stage:")
    println(src.fragmentSrc)

//    val hierarchy = program.vertexStage.hierarchy
//    hierarchy.globalScope.ops.shuffle()
//
//    println("unordered:")
//    hierarchy.printHierarchy()
//
//    KslProcessor().process(hierarchy)
//
//    println("\nreordered:")
//    program.vertexStage.hierarchy.printHierarchy()
}