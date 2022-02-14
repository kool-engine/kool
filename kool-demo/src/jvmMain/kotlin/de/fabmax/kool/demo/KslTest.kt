package de.fabmax.kool.demo

import de.fabmax.kool.modules.kslx.*

class KslTest {

    fun main() {

        // todo
        //  remaining operators (modulo, bitwise ops, +=, *=, ++, --, etc.)
        //  builtin functions, texture functions impl
        //  custom building-blocks aka ShaderNodes
        //  some way to get and modify shader building blocks (via Statement tags?)
        //  structs?
        //  find and remove unused statements? might be tricky to find them in expressions...
        //  UBOs

        // potential problems
        // - KslAssignable: assignTarget is currently a simple String everywhere (not language specific)
        // - Array / Matrix / Swizzle accessor expressions generate in non language specific way
        // - maybe make non-prefixed type generators (e.g. float(4f)) should generate a const expression instead of var
        //   because it's used more often? rename var generator -> varFloat() or maybe not because vars can be used as
        //   expressions but not vice versa
        // - fragment shader in position: gl_FragCoord is in pixels, in WebGPU position is in normalized coordinates

        val prog = KslProgram("Test Shader")

        prog.vertexShader {
            val uModelMat = uniformMat4("uModelMat")
            val uSomeOtherUniform = uniformInt4Array("uOther", 17)
            val uTexture = uniformSamplerArray2d("uColorTexture", 2)

            main {
                val someVec = float3(1f, 2f, 3f)
                val someValue = float(someVec.y, "aFloatValue")

                someVec `=` (uModelMat * constFloat4(someVec, 1f)).xyz

                someValue `=` someVec.x
                someVec.z `=` someValue
                someVec `=` constFloat4(4f, 5f, 6f, 7f).rgb

                val ivec4 = int4(float4(4f, 5f, 6f, 7f).swiz4("wzyx").toInt4())
                val anInt = int(someValue.toInt())

                ivec4 `=` uSomeOtherUniform[5]

                `if`(someValue lt 3f) {
                    someValue `=` constFloat(9f) - constFloat(3f) * constFloat(7f)
                    someValue `=` constFloat(9f) * constFloat(3f) + constFloat(7f)

                }.`else if`((7f lt someValue) `&&` (anInt ge 12)) {
                    someValue `=` constFloat(17f)

                }.`else` {
                    ivec4.xyz `=` constFloat3(10f, 20f, 30f).toInt3()
                }

                val color = float4(sampleTexture(uTexture[0], uSomeOtherUniform[1].xy.toFloat2() / textureSize(uTexture[1]).toFloat2()))

                val myArray = intArray(5)
                fori(0 until 5) { i ->
                    myArray[i] `=` i * 2 + color.x.toInt()
                    anInt `=` myArray[2] + 4
                    `if`(anInt gt 17) {
                        breakLoop()
                    }.`else` {
                        continueLoop()
                    }
                }

                `do` {
                    myArray[0] `=` myArray[0] + 1
                }.`while`(myArray[0] lt 10)

                `while`(myArray[0] lt 20) {
                    myArray[0] `=` myArray[0] + 1
                }

                someValue `=` 3f * smoothStep(3f, 17f, someValue) + 5f
            }
        }

        prog.fragmentShader {
            val uModelMat = uniformMat4("uModelMat")
            val uMatArray = uniformMat4Array("uMatArray", 4)
            val uVector = uniformFloat3("uVector")

            main {
                discard()
            }
        }

        println("\n// vertex shader:")
        var generator = GlslGenerator()
        prog.vertexShader?.let { generator.generateElement(it) }
        println(generator.output)

        println("\n// fragment shader:")
        generator = GlslGenerator()
        prog.fragmentShader?.let { generator.generateElement(it) }
        println(generator.output)

    }
}