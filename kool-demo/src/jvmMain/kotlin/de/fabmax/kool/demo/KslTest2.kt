package de.fabmax.kool.demo

import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.modules.ksl.lang.*

fun main() {

    // todo
    //  remaining operators (bitwise ops, +=, *=, ++, --, etc.)
    //  functions
    //  builtin functions, texture functions impl
    //  custom building-blocks aka ShaderNodes
    //  some way to get and modify shader building blocks
    //  structs?
    //  find and remove unused statements?
    //  UBOs

    // limitations / potential problems
    // - generic expressions make it difficult to write overloaded functions for different expression types
    // - fragment shader in position: gl_FragCoord is in pixels, in WebGPU position is in normalized coordinates

    val program = KslProgram("Test Shader").apply {
        val mvp = uniformMat4("uMvp")
        val vertColor = interStageFloat4()

        vertexStage {
            val color = vertexAttribFloat4("aColor")

            main {
//                val inA = floatVar("inA")
//                val inB = floatVar("inB")
//                val out = floatVar("out")
//
//                inA set 1f.const
//                inB set 2f.const
//
//                `if` (inA gt inB) {
//                    out set inA
//                }.elseIf (out lt inB) {
//                    out set inB
//                }.`else` {
//                    out set 0f.const
//                }

                val floatVar = floatVar()
                floatVar set 17f.const
                val intVar = intVar()
                intVar set 8.const

                val floatArray = floatArray(8)
                floatArray[4] set floatVar

                val position = float4Var("aPosition")
                position set color * 3f.const

                val int4 = int4Var()
                int4 set position.toInt4()

                floatVar set position.x
                intVar set int4.y

                vertColor.input set int4.toFloat4() / 255f.const

                val boolVecExpr = constFloat2(1f, 2f) lt constFloat2(2f, 1f)


                val tstBlock = TestBlock(this).apply {
                    inA = 1f.const
                    inB = 2f.const
                }
                ops += tstBlock

                `if` (tstBlock.out lt 6f.const) {
                    outPosition set mvp * position

                }.elseIf (!(any(boolVecExpr) or all(boolVecExpr))) {
                    val float2 = float2Var()
                    float2 set constFloat2(1f, 2f) + floatArray[4]

                }.`else` {
                    val float3 = float3Var()
                    float3 set constFloat3(1f, 2f, 3f)
                    float3 set float3 - floatVar
                }
            }
        }

        fragmentStage {
            main {
                outColor() set vertColor.output
                outDepth set 17f.const
            }
        }
    }

    val src = GlslGenerator().generateProgram(program)
    src.dump()

//    program.vertexStage.hierarchy.globalScope.ops.shuffle()
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

class TestBlock(parentScope: KslScopeBuilder) : KslBlock("testBlock", parentScope) {
    var inA: KslScalarExpression<KslTypeFloat1> by inFloat1(name = "inA")
    var inB: KslScalarExpression<KslTypeFloat1> by inFloat1(name = "inB")

    val out = outFloat1(name = "maxOfAandB")

    init {
        body.apply {
            `if` (inA gt inB) {
                out set inA
            }.`else` {
                out set inB
            }
        }
    }
}