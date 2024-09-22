package de.fabmax.kool

import de.fabmax.kool.GenerateVariantsFromFloatPrototype.PatternTransformer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.math.roundToInt

open class GenerateVariantsFromFloatPrototype : DefaultTask() {
    @Input
    var filesToUpdate = listOf<String>()

    @Input
    var generateIntTypes = true

    @TaskAction
    fun updateVariants() {
        filesToUpdate.forEach { path ->
            val file = File(path)
            val text = file.readText()

            val header = text
                .substringBefore("<template>")
                .substringBeforeLast('\n')
                .trim()

            val templateStartLine = text.lines().first { "<template>" in it }
            val templateEndLine = text.lines().first { "</template>" in it }
            val template = text
                .substringAfter("<template>")
                .substringAfter('\n')
                .substringBefore("</template>")
                .substringBeforeLast('\n')
                .trim()

            val output = StringBuilder("$header\n\n")
                .append("$templateStartLine\n\n")
                .append("$template\n\n")
                .append("$templateEndLine\n\n\n")
                .append("${transform(template, floatToDoubleTransforms)}\n")

            if (generateIntTypes) {
                output.append("\n\n${transform(template, floatToIntTransforms)}\n")
            }

            val outText = output.toString()
            if (outText != text) {
                file.writeText(outText)
            }
        }
    }

    private fun transform(template: String, transforms: List<Pair<Regex, PatternTransformer>>): String {
        var transformed = template

        transforms.forEach { (regex, transformer) ->
            var startIndex = 0
            do {
                val match = regex.find(transformed, startIndex)?.also {
                    val (range, replacement) = transformer.transform(it)
                    transformed = transformed.replaceRange(range, replacement)
                    startIndex = range.first
                }
            } while (match != null)
        }
        return transformed
    }

    private fun interface PatternTransformer {
        fun transform(match: MatchResult): Pair<IntRange, String>
    }

    companion object {
        private val floatToDoubleTransforms = listOf(
            Regex("""\W(Vec(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "Vec${it.groupValues[2]}d" },
            Regex("""\W(Mat(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "Mat${it.groupValues[2]}d" },
            Regex("""\W(Mat(\d+)fStack)\W""") to PatternTransformer { it.groups[1]!!.range to "Mat${it.groupValues[2]}dStack" },
            Regex("""\W(LazyMat(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "LazyMat${it.groupValues[2]}d" },
            Regex("""\W(PlaneF)\W""") to PatternTransformer { it.groups[1]!!.range to "PlaneD" },
            Regex("""\W(PoseF)\W""") to PatternTransformer { it.groups[1]!!.range to "PoseD" },
            Regex("""\W(QuatF)\W""") to PatternTransformer { it.groups[1]!!.range to "QuatD" },
            Regex("""\W(RayF)\W""") to PatternTransformer { it.groups[1]!!.range to "RayD" },
            Regex("""\W(BoundingBoxF)\W""") to PatternTransformer { it.groups[1]!!.range to "BoundingBoxD" },
            Regex("""\W(MutableVec(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "MutableVec${it.groupValues[2]}d" },
            Regex("""\W(MutableMat(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "MutableMat${it.groupValues[2]}d" },
            Regex("""\W(MutablePoseF)\W""") to PatternTransformer { it.groups[1]!!.range to "MutablePoseD" },
            Regex("""\W(MutableQuatF)\W""") to PatternTransformer { it.groups[1]!!.range to "MutableQuatD" },
            Regex("""\W(AngleF)\W""") to PatternTransformer { it.groups[1]!!.range to "AngleD" },
            Regex("""\W(TransformF)\W""") to PatternTransformer { it.groups[1]!!.range to "TransformD" },
            Regex("""\W(TrsTransformF)\W""") to PatternTransformer { it.groups[1]!!.range to "TrsTransformD" },
            Regex("""\W(MatrixTransformF)\W""") to PatternTransformer { it.groups[1]!!.range to "MatrixTransformD" },
            Regex("""\W(Float)\W""") to PatternTransformer { it.groups[1]!!.range to "Double" },
            Regex("""\W(FloatArray)\W""") to PatternTransformer { it.groups[1]!!.range to "DoubleArray" },
            Regex("""\W(floatArrayOf)\W""") to PatternTransformer { it.groups[1]!!.range to "doubleArrayOf" },
            Regex("""\W(FUZZY_EQ_F)\W""") to PatternTransformer { it.groups[1]!!.range to "FUZZY_EQ_D" },
            Regex("""\W(PI_F)\W""") to PatternTransformer { it.groups[1]!!.range to "PI" },

            Regex("""\W(matrixF)\W""") to PatternTransformer { it.groups[1]!!.range to "matrixD" },

            // remove noInt section markers
            Regex("""\h*// </?noInt>.*\v*""") to PatternTransformer { it.range to "" },

            // float literals in the form of 1f, 1.2f, .1f and 1.f (no exponent forms)
            Regex("""\W(\.\d+f|\d+\.\d*f|\d+f)\W""") to PatternTransformer {
                it.groups[1]!!.range to it.groupValues[1].toFloat().toDouble().toString()
            }
        )

        private val floatToIntTransforms = listOf(
            Regex("""\W(Vec(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "Vec${it.groupValues[2]}i" },
            Regex("""\W(MutableVec(\d+)f)\W""") to PatternTransformer { it.groups[1]!!.range to "MutableVec${it.groupValues[2]}i" },
            Regex("""\W(Float)\W""") to PatternTransformer { it.groups[1]!!.range to "Int" },
            Regex("""\W(FloatArray)\W""") to PatternTransformer { it.groups[1]!!.range to "IntArray" },
            Regex("""\W(floatArrayOf)\W""") to PatternTransformer { it.groups[1]!!.range to "intArrayOf" },
            Regex("""\W(Float32Buffer)\W""") to PatternTransformer { it.groups[1]!!.range to "Int32Buffer" },
            Regex("""\W(putFloat32)\W""") to PatternTransformer { it.groups[1]!!.range to "putInt32" },

            // remove entire <noInt>..</noInt> blocks
            Regex("""\s*// <noInt>[\s\S]*?(?<=</noInt>)""") to PatternTransformer { it.range to "" },

            // float literals in the form of 1f, 1.2f, .1f and 1.f (no exponent forms)
            Regex("""\W(\.\d+f|\d+\.\d*f|\d+f)\W""") to PatternTransformer {
                it.groups[1]!!.range to it.groupValues[1].toFloat().roundToInt().toString()
            }
        )
    }
}