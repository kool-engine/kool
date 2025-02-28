package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.TexFormat

/**
 * Default GLSL shader code generator.
 */
open class GlslGenerator protected constructor(generatorExpressions: Map<KslExpression<*>, KslExpression<*>>, val hints: Hints) :
    KslGenerator(generatorExpressions) {

    private var blockIndent = "    "

    override fun generateProgram(program: KslProgram, pipeline: DrawPipeline): GlslGeneratorOutput {
        val vertexStage = checkNotNull(program.vertexStage) {
            "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        val fragmentStage = checkNotNull(program.fragmentStage) {
            "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        return GlslGeneratorOutput.shaderOutput(
            generateVertexSrc(vertexStage, pipeline),
            generateFragmentSrc(fragmentStage, pipeline)
        ).also {
            if (program.dumpCode) {
                it.dump()
            }
        }
    }

    override fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): GlslGeneratorOutput {
        val computeStage = checkNotNull(program.computeStage) {
            "KslProgram computeStage is missing"
        }
        return GlslGeneratorOutput.computeOutput(generateComputeSrc(computeStage, pipeline)).also {
            if (program.dumpCode) {
                it.dump()
            }
        }
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage, pipeline: DrawPipeline): String {
        val src = StringBuilder()
        src.appendLine("""
            ${hints.glslVersionStr}
            precision highp sampler3D;
            
            /*
             * ${vertexStage.program.name} - generated vertex shader
             */
        """.trimIndent())
        src.appendLine()

        src.generateStorageBuffers(vertexStage, pipeline)
        src.generateUbos(vertexStage, pipeline)
        src.generateUniformSamplers(vertexStage, pipeline)
        src.generateStorageTextures(vertexStage, pipeline)
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }, pipeline, "instance attributes")
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Vertex }, pipeline, "vertex attributes")
        src.generateInterStageOutputs(vertexStage)

        src.appendLine(generateScope(vertexStage.globalScope, ""))
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            ${hints.glslVersionStr}
            precision highp float;
            precision highp sampler2DArray;
            precision highp sampler2DShadow;
            precision highp sampler3D;
            
            /*
             * ${fragmentStage.program.name} - generated fragment shader
             */
        """.trimIndent())
        src.appendLine()

        src.generateStorageBuffers(fragmentStage, pipeline)
        src.generateUbos(fragmentStage, pipeline)
        src.generateUniformSamplers(fragmentStage, pipeline)
        src.generateStorageTextures(fragmentStage, pipeline)
        src.generateInterStageInputs(fragmentStage)
        src.generateOutputs(fragmentStage.outColors)

        src.appendLine(generateScope(fragmentStage.globalScope, ""))
        return src.toString()
    }

    private fun generateComputeSrc(computeStage: KslComputeStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            ${hints.glslVersionStr}
            
            /*
             * ${computeStage.program.name} - generated compute shader
             */
             
            layout(local_size_x = ${computeStage.workGroupSize.x}, local_size_y = ${computeStage.workGroupSize.y}, local_size_z = ${computeStage.workGroupSize.z}) in;
        """.trimIndent())
        src.appendLine()

        src.generateStorageBuffers(computeStage, pipeline)
        src.generateUbos(computeStage, pipeline)
        src.generateUniformSamplers(computeStage, pipeline)
        src.generateStorageTextures(computeStage, pipeline)

        src.appendLine(generateScope(computeStage.globalScope, ""))
        return src.toString()
    }

    override fun constFloatVecExpression(vararg values: KslExpression<KslFloat1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "vec${values.size}(${values.joinToString { it.generateExpression() }})"
    }

    override fun constIntVecExpression(vararg values: KslExpression<KslInt1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "ivec${values.size}(${values.joinToString { it.generateExpression() }})"
    }

    override fun constUintVecExpression(vararg values: KslExpression<KslUint1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "uvec${values.size}(${values.joinToString { it.generateExpression() }})"
    }

    override fun constBoolVecExpression(vararg values: KslExpression<KslBool1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "bvec${values.size}(${values.joinToString { it.generateExpression() }})"
    }

    override fun constMatExpression(vararg columns: KslVectorExpression<*, KslFloat1>): String {
        if (columns.size !in 2..4) {
            throw IllegalArgumentException("invalid number of columns: ${columns.size} (must be between 2 and 4)")
        }
        return "mat${columns.size}(${columns.joinToString { it.generateExpression() }})"
    }

    override fun generateCastExpression(castExpr: KslExpressionCast<*>): String {
        return "${glslTypeName(castExpr.expressionType)}(${castExpr.value.generateExpression()})"
    }

    override fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String {
        val lt = expression.left.generateExpression()
        val rt = expression.right.generateExpression()
        return if (expression.left.expressionType is KslVector<*>) {
            when (expression.operator) {
                KslCompareOperator.Equal -> "equal($lt, $rt)"
                KslCompareOperator.NotEqual -> "notEqual($lt, $rt)"
                KslCompareOperator.Less -> "lessThan($lt, $rt)"
                KslCompareOperator.LessEqual -> "lessThanEqual($lt, $rt)"
                KslCompareOperator.Greater -> "greaterThan($lt, $rt)"
                KslCompareOperator.GreaterEqual -> "greaterThanEqual($lt, $rt)"
            }
        } else {
            "($lt ${expression.operator.opString} $rt)"
        }
    }

    override fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String {
        val sampler = sampleTexture.sampler.generateExpression()
        val isCompatSampler = hints.compat1dSampler &&
                sampleTexture.sampler.expressionType is KslSampler1dType &&
                sampleTexture.coord.expressionType is KslFloat1
        val coord = if (isCompatSampler) {
            // for better OpenGL ES compatibility 1d textures actually are 2d textures...
            "vec2(${sampleTexture.coord.generateExpression()}, 0.5)"
        } else {
            sampleTexture.coord.generateExpression()
        }

        return if (sampleTexture.lod != null) {
            "textureLod(${sampler}, ${coord}, ${sampleTexture.lod.generateExpression()})"
        } else {
            "texture(${sampler}, ${coord})"
        }
    }

    override fun sampleColorTextureGrad(sampleTextureGrad: KslSampleColorTextureGrad<*>): String {
        val sampler = sampleTextureGrad.sampler.generateExpression()
        val isCompatSampler = hints.compat1dSampler &&
                sampleTextureGrad.sampler.expressionType is KslSampler1dType &&
                sampleTextureGrad.coord.expressionType is KslFloat1
        val coord = if (isCompatSampler) {
            // for better OpenGL ES compatibility 1d textures actually are 2d textures...
            "vec2(${sampleTextureGrad.coord.generateExpression()}, 0.5)"
        } else {
            sampleTextureGrad.coord.generateExpression()
        }
        val ddx = sampleTextureGrad.ddx.generateExpression()
        val ddy = sampleTextureGrad.ddy.generateExpression()

        return "textureGrad($sampler, $coord, $ddx, $ddy)"
    }

    override fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String {
        val sampler = sampleTexture.sampler.generateExpression()
        val coord = sampleTexture.coord.generateExpression()
        val depthRef = sampleTexture.depthRef.generateExpression()
        return when (sampleTexture.sampler.expressionType) {
            KslDepthSampler2d -> "textureLod($sampler, vec3($coord, $depthRef), 0.0)"
            KslDepthSamplerCube -> "texture($sampler, vec4($coord, $depthRef))"
            else -> error("Unsupported depth sampler type ${sampleTexture.sampler.expressionType}")
        }
    }

    override fun sampleColorTextureArray(sampleTexture: KslSampleColorTextureArray<*>): String {
        val sampler = sampleTexture.sampler.generateExpression()
        val coord = sampleTexture.coord.generateExpression()
        val idx = "float(${sampleTexture.arrayIndex.generateExpression()})"
        val combined = when (sampleTexture.sampler.expressionType) {
            KslColorSampler2dArray -> "vec3($coord, $idx)"
            KslColorSamplerCubeArray -> "vec4($coord, $idx)"
            else -> error("Unsupported sampler array type ${sampleTexture.sampler.expressionType}")
        }
        return if (sampleTexture.lod != null) {
            "textureLod(${sampler}, ${combined}, ${sampleTexture.lod.generateExpression()})"
        } else {
            "texture(${sampler}, ${combined})"
        }
    }

    override fun sampleColorTextureArrayGrad(sampleTextureGrad: KslSampleColorTextureArrayGrad<*>): String {
        val sampler = sampleTextureGrad.sampler.generateExpression()
        val coord = sampleTextureGrad.coord.generateExpression()
        val idx = "float(${sampleTextureGrad.arrayIndex.generateExpression()})"
        val combined = when (sampleTextureGrad.sampler.expressionType) {
            KslColorSampler2dArray -> "vec3($coord, $idx)"
            KslColorSamplerCubeArray -> "vec4($coord, $idx)"
            else -> error("Unsupported sampler array type ${sampleTextureGrad.sampler.expressionType}")
        }
        val ddx = sampleTextureGrad.ddx.generateExpression()
        val ddy = sampleTextureGrad.ddy.generateExpression()

        return "textureGrad($sampler, $combined, $ddx, $ddy)"
    }

    override fun sampleDepthTextureArray(sampleTexture: KslSampleDepthTextureArray<*>): String {
        val sampler = sampleTexture.sampler.generateExpression()
        val coord = sampleTexture.coord.generateExpression()
        val idx = "float(${sampleTexture.arrayIndex.generateExpression()})"
        val depthRef = sampleTexture.depthRef.generateExpression()
        return when (sampleTexture.sampler.expressionType) {
            KslDepthSampler2dArray -> "texture($sampler, vec4($coord, $idx, $depthRef))"
            KslDepthSamplerCubeArray -> "texture($sampler, vec4($coord, $idx), $depthRef)"
            else -> error("Unsupported depth sampler array type ${sampleTexture.sampler.expressionType}")
        }
    }

    override fun generateTextureSize(textureSize: KslTextureSize<*, *>): String {
        return "textureSize(${textureSize.sampler.generateExpression()}, ${textureSize.lod.generateExpression()})"
    }

    override fun generateTextureSize(textureSize: KslStorageTextureSize<*, *, *>): String {
        return "imageSize(${textureSize.storageTex.generateExpression()})"
    }

    override fun imageTextureRead(expression: KslImageTextureLoad<*>): String {
        val sampler = expression.sampler.generateExpression()
        val coords = expression.coord.generateExpression()
        val lod = expression.lod?.generateExpression()
        return "texelFetch($sampler, $coords, ${lod ?: 0})"
    }

    private fun KslStorage<*,*>.getIndexString(coordExpr: String) = when (this) {
        // choosing array dimension based on storage dimension would also work, but seems to have issues
        // with some glsl compilers
        //is KslStorage1dType<*> -> "[${coord}]"
        //is KslStorage2dType<*> -> "[${coord}.y][${coord}.x]"
        //is KslStorage3dType<*> -> "[${coord}.z][${coord}.y][${coord}.x]"

        // always use a 1d array and compute array index dynamically based on buffer size
        is KslStorage1d<*> -> "[${coordExpr}]"
        is KslStorage2d<*> -> "[${coordExpr}.y * $sizeX + ${coordExpr}.x]"
        is KslStorage3d<*> -> "[${coordExpr}.z * ${sizeY * sizeX} + ${coordExpr}.y * $sizeX + ${coordExpr}.x]"
    }

    override fun generateStorageRead(storageRead: KslStorageRead<*, *, *>): String {
        val storage = storageRead.storage.generateExpression()
        val coord = storageRead.coord.generateExpression()
        val arrayIndex = storageRead.storage.getIndexString(coord)
        return "${storage}${arrayIndex}"
    }

    override fun opStorageWrite(op: KslStorageWrite<*, *, *>): String {
        val storage = op.storage.generateExpression()
        val expr = op.data.generateExpression()
        val coord = op.coord.generateExpression()
        val arrayIndex = op.storage.getIndexString(coord)
        return "${storage}${arrayIndex} = $expr;"
    }

    override fun opStorageTextureWrite(op: KslStorageTextureStore<*, *, *>): String {
        val storage = op.storage.generateExpression()
        val expr = op.data.generateExpression()
        val coord = op.coord.generateExpression()
        return "imageStore(${storage}, $coord, $expr);"
    }

    override fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *, *>): String {
        val storage = atomicOp.storage.generateExpression()
        val expr = atomicOp.data.generateExpression()
        val coord = atomicOp.coord.generateExpression()
        val arrayIndex = atomicOp.storage.getIndexString(coord)
        val func = when(atomicOp.op) {
            KslStorageAtomicOp.Op.Swap -> "atomicExchange"
            KslStorageAtomicOp.Op.Add -> "atomicAdd"
            KslStorageAtomicOp.Op.And -> "atomicAnd"
            KslStorageAtomicOp.Op.Or -> "atomicOr"
            KslStorageAtomicOp.Op.Xor -> "atomicXor"
            KslStorageAtomicOp.Op.Min -> "atomicMin"
            KslStorageAtomicOp.Op.Max -> "atomicMax"
        }
        return "$func(${storage}${arrayIndex}, ${expr})"
    }

    override fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *, *>): String {
        val storage = atomicCompSwap.storage.generateExpression()
        val comp = atomicCompSwap.compare.generateExpression()
        val expr = atomicCompSwap.data.generateExpression()
        val coord = atomicCompSwap.coord.generateExpression()
        val arrayIndex = atomicCompSwap.storage.getIndexString(coord)
        return "atomicCompSwap($storage${arrayIndex}, ${comp}, ${expr})"
    }

    override fun storageTextureRead(storageTextureRead: KslStorageTextureLoad<*, *, *>): String {
        val storage = storageTextureRead.storage.generateExpression()
        val coord = storageTextureRead.coord.generateExpression()
        return "imageLoad($storage, $coord)"
    }

    protected open fun StringBuilder.generateUniformSamplers(stage: KslShaderStage, pipeline: PipelineBase) {
        val samplers = stage.getUsedSamplers()
        if (samplers.isNotEmpty()) {
            appendLine("// texture samplers")
            for (u in samplers) {
                appendLine("uniform ${glslTypeName(u.expressionType)} ${getStateName(u.value)};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateStorageBuffers(stage: KslShaderStage, pipeline: PipelineBase) {
        val storage = stage.getUsedStorage()
        if (storage.isNotEmpty()) {
            appendLine("// storage buffers")
            val readonly = if (stage.type == KslShaderStageType.ComputeShader) "" else "readonly"
            storage.forEachIndexed { i, it ->
                // always use a 1d array and compute array index dynamically based on buffer size
                val arrayDim = "[]"
                // choosing array dimension based on storage dimension would also work, but seems to have issues
                // with some glsl compilers
                // val arrayDim = when (it) {
                //     is KslStorage1d<*> -> if (it.sizeX == null) "[]" else "[${it.sizeX}]"
                //     is KslStorage2d<*> -> if (it.sizeY == null) "[][${it.sizeX}]" else "[${it.sizeY}][${it.sizeX}]"
                //     is KslStorage3d<*> -> if (it.sizeZ == null) "[][${it.sizeY}][${it.sizeX}]" else "[${it.sizeZ}][${it.sizeY}][${it.sizeX}]"
                // }
                appendLine("""
                    layout(std430, binding=$i) $readonly buffer ssboLayout_$i {
                        ${glslTypeName(it.storageType.elemType)} ${it.name}$arrayDim;
                    };
                """.trimIndent())
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateStorageTextures(stage: KslShaderStage, pipeline: PipelineBase) {
        val storage = stage.getUsedStorageTextures()
        if (storage.isNotEmpty()) {
            appendLine("// storage textures")
            storage.forEachIndexed { i, it ->
                val formatQualifier = storageTextureFormatQualifier(it.texFormat)
                appendLine("layout($formatQualifier, binding=$i) uniform ${glslTypeName(it.expressionType)} ${it.name};")
            }
            appendLine()
        }
    }

    protected fun storageTextureFormatQualifier(texFormat: TexFormat): String {
        return when (texFormat) {
            TexFormat.R -> "r8"
            TexFormat.RG -> "rg8"
            TexFormat.RGBA -> "rgba8"
            TexFormat.R_F16 -> "r16f"
            TexFormat.RG_F16 -> "rg16f"
            TexFormat.RGBA_F16 -> "rgba16f"
            TexFormat.R_F32 -> "r32f"
            TexFormat.RG_F32 -> "rg32f"
            TexFormat.RGBA_F32 -> "rgba32f"
            TexFormat.R_I32 -> "r32i"
            TexFormat.RG_I32 -> "rg32i"
            TexFormat.RGBA_I32 -> "rgba32i"
            TexFormat.R_U32 -> "r32ui"
            TexFormat.RG_U32 -> "rg32ui"
            TexFormat.RGBA_U32 -> "rgba32ui"
            TexFormat.RG11B10_F -> "r11f_g11f_b10f"
        }
    }

    protected open fun StringBuilder.generateUbos(stage: KslShaderStage, pipeline: PipelineBase) {
        val ubos = stage.getUsedUbos().sortedBy { it.scope.ordinal }
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                if (hints.replaceUbosByPlainUniforms) {
                    // compatibility fallback required in some scenarios
                    ubo.uniforms.values
                        .filter { it.expressionType !is KslArrayType<*> || it.arraySize > 0 }
                        .forEach {
                            appendLine("    uniform highp ${glslTypeName(it.expressionType)} ${getStateName(it.value)};")
                        }

                } else {
                    appendLine("layout(std140) uniform ${ubo.name} {")
                    ubo.uniforms.values
                        .filter { it.expressionType !is KslArrayType<*> || it.arraySize > 0 }
                        .forEach {
                            appendLine("    highp ${glslTypeName(it.expressionType)} ${getStateName(it.value)};")
                        }
                    appendLine("};")
                }
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateAttributes(attribs: List<KslVertexAttribute<*>>, pipeline: DrawPipeline, info: String) {
        if (attribs.isNotEmpty()) {
            val mappedLocs = pipeline.vertexLayout.getAttribLocations()
            appendLine("// $info")
            attribs.forEach { a ->
                val attr = pipeline.vertexLayout.bindings.flatMap { it.vertexAttributes }.first { it.name == a.name }
                appendLine("layout(location=${mappedLocs[attr]!!}) in ${glslTypeName(a.expressionType)} ${getStateName(a.value)};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateInterStageOutputs(vertexStage: KslVertexStage) {
        if (vertexStage.interStageVars.isNotEmpty()) {
            appendLine("// custom vertex stage outputs")
            vertexStage.interStageVars.forEach { interStage ->
                val value = interStage.input
                appendLine("${interStage.interpolation.glsl()} out ${glslTypeName(value.expressionType)} ${getStateName(value)};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateInterStageInputs(fragmentStage: KslFragmentStage) {
        if (fragmentStage.interStageVars.isNotEmpty()) {
            appendLine("// custom fragment stage inputs")
            fragmentStage.interStageVars.forEach { interStage ->
                val value = interStage.output
                appendLine("${interStage.interpolation.glsl()} in ${glslTypeName(value.expressionType)} ${getStateName(value)};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateOutputs(outputs: List<KslStageOutput<*>>) {
        if (outputs.isNotEmpty()) {
            appendLine("// stage outputs")
            outputs.forEach { output ->
                val loc = if (output.location >= 0) "layout(location=${output.location}) " else ""
                appendLine("${loc}out ${glslTypeName(output.expressionType)} ${getStateName(output.value)};")
            }
            appendLine()
        }
    }

    override fun opFunctionBody(op: KslFunction<*>.FunctionRoot): String {
        val func = op.function
        return buildString {
            appendLine("${glslTypeName(func.returnType)} ${func.name}(${func.parameters.joinToString { p -> "${glslTypeName(p.expressionType)} ${p.stateName}" }}) {")
            appendLine(generateScope(op.childScopes.first(), blockIndent))
            appendLine("}")
            appendLine()
        }
    }

    override fun opDeclareVar(op: KslDeclareVar): String {
        val initExpr = op.initExpression?.let { " = ${it.generateExpression()}" } ?: ""
        val state = op.declareVar
        return "${glslTypeName(state.expressionType)} ${getStateName(state)}${initExpr};"
    }

    override fun opDeclareArray(op: KslDeclareArray): String {
        val array = op.declareVar
        val typeName = glslTypeName(array.expressionType)

        return if (op.elements.size == 1 && op.elements[0].expressionType == array.expressionType) {
            "$typeName ${getStateName(array)} = ${op.elements[0].generateExpression()};"
        } else {
            val initExpr = op.elements.joinToString { it.generateExpression() }
            "$typeName ${getStateName(array)} = ${typeName}(${initExpr});"
        }
    }

    override fun opAssign(op: KslAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression()};"
    }

    override fun opAugmentedAssign(op: KslAugmentedAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} ${op.augmentationMode.opChar}= ${op.assignExpression.generateExpression()};"
    }

    override fun opIf(op: KslIf): String {
        val txt = StringBuilder("if (${op.condition.generateExpression()}) {\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        op.elseIfs.forEach { elseIf ->
            txt.appendLine(" else if (${elseIf.first.generateExpression()}) {")
            txt.appendLine(generateScope(elseIf.second, blockIndent))
            txt.append("}")
        }
        if (op.elseBody.isNotEmpty()) {
            txt.appendLine(" else {")
            txt.appendLine(generateScope(op.elseBody, blockIndent))
            txt.append("}")
        }
        return txt.toString()
    }

    override fun opFor(op: KslLoopFor<*>): String {
        return StringBuilder("for (; ")
            .append(op.whileExpression.generateExpression()).append("; ")
            .append(op.loopVar.generateAssignable(this)).append(" += ").append(op.incExpr.generateExpression())
            .appendLine(") {")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opWhile(op: KslLoopWhile): String {
        return StringBuilder("while (${op.whileExpression.generateExpression()}) {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opDoWhile(op: KslLoopDoWhile): String {
        return StringBuilder("do {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .append("} while (${op.whileExpression.generateExpression()});")
            .toString()
    }

    override fun opBreak(op: KslLoopBreak) = "break;"

    override fun opContinue(op: KslLoopContinue) = "continue;"

    override fun opDiscard(op: KslDiscard): String = "discard;"

    override fun opReturn(op: KslReturn): String = "return ${op.returnValue.generateExpression()};"

    override fun opBlock(op: KslBlock): String {
        val txt = StringBuilder("{ // block: ${op.opName}\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        return txt.toString()
    }

    override fun opInlineCode(op: KslInlineCode): String {
        return op.code
    }

    private fun generateArgs(args: List<KslExpression<*>>, expectedArgs: Int): String {
        check(args.size == expectedArgs)
        return args.joinToString { it.generateExpression() }
    }

    override fun generateInvokeFunction(func: KslInvokeFunction<*>) = "${func.function.name}(${generateArgs(func.args, func.args.size)})"

    override fun builtinAbs(func: KslBuiltinAbsScalar<*>) = "abs(${generateArgs(func.args, 1)})"
    override fun builtinAbs(func: KslBuiltinAbsVector<*, *>) = "abs(${generateArgs(func.args, 1)})"
    override fun builtinAtan2(func: KslBuiltinAtan2Scalar) = "atan(${generateArgs(func.args, 2)})"
    override fun builtinAtan2(func: KslBuiltinAtan2Vector<*>) = "atan(${generateArgs(func.args, 2)})"
    override fun builtinCeil(func: KslBuiltinCeilScalar) = "ceil(${generateArgs(func.args, 1)})"
    override fun builtinCeil(func: KslBuiltinCeilVector<*>) = "ceil(${generateArgs(func.args, 1)})"
    override fun builtinClamp(func: KslBuiltinClampScalar<*>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinClamp(func: KslBuiltinClampVector<*, *>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinCross(func: KslBuiltinCross) = "cross(${generateArgs(func.args, 2)})"
    override fun builtinDegrees(func: KslBuiltinDegreesScalar) = "degrees(${generateArgs(func.args, 1)})"
    override fun builtinDegrees(func: KslBuiltinDegreesVector<*>) = "degrees(${generateArgs(func.args, 1)})"
    override fun builtinDistance(func: KslBuiltinDistanceScalar<*>) = "distance(${generateArgs(func.args, 2)})"
    override fun builtinDot(func: KslBuiltinDot<*>) = "dot(${generateArgs(func.args, 2)})"
    override fun builtinDpdx(func: KslBuiltinDpdxScalar) = "dFdx(${generateArgs(func.args, 1)})"
    override fun builtinDpdx(func: KslBuiltinDpdxVector<*>) = "dFdx(${generateArgs(func.args, 1)})"
    override fun builtinDpdy(func: KslBuiltinDpdyScalar) = "dFdy(${generateArgs(func.args, 1)})"
    override fun builtinDpdy(func: KslBuiltinDpdyVector<*>) = "dFdy(${generateArgs(func.args, 1)})"
    override fun builtinExp(func: KslBuiltinExpScalar) = "exp(${generateArgs(func.args, 1)})"
    override fun builtinExp(func: KslBuiltinExpVector<*>) = "exp(${generateArgs(func.args, 1)})"
    override fun builtinExp2(func: KslBuiltinExp2Scalar) = "exp2(${generateArgs(func.args, 1)})"
    override fun builtinExp2(func: KslBuiltinExp2Vector<*>) = "exp2(${generateArgs(func.args, 1)})"
    override fun builtinFaceForward(func: KslBuiltinFaceForward<*>) = "faceforward(${generateArgs(func.args, 3)})"
    override fun builtinFloor(func: KslBuiltinFloorScalar) = "floor(${generateArgs(func.args, 1)})"
    override fun builtinFloor(func: KslBuiltinFloorVector<*>) = "floor(${generateArgs(func.args, 1)})"
    override fun builtinFma(func: KslBuiltinFmaScalar) = "fma(${generateArgs(func.args, 3)})"
    override fun builtinFma(func: KslBuiltinFmaVector<*>) = "fma(${generateArgs(func.args, 3)})"
    override fun builtinFract(func: KslBuiltinFractScalar) = "fract(${generateArgs(func.args, 1)})"
    override fun builtinFract(func: KslBuiltinFractVector<*>) = "fract(${generateArgs(func.args, 1)})"
    override fun builtinInverseSqrt(func: KslBuiltinInverseSqrtScalar) = "inversesqrt(${generateArgs(func.args, 1)})"
    override fun builtinInverseSqrt(func: KslBuiltinInverseSqrtVector<*>) = "inversesqrt(${generateArgs(func.args, 1)})"
    override fun builtinIsInf(func: KslBuiltinIsInfScalar) = "isinf(${generateArgs(func.args, 1)})"
    override fun builtinIsInf(func: KslBuiltinIsInfVector<*, *>) = "isinf(${generateArgs(func.args, 1)})"
    override fun builtinIsNan(func: KslBuiltinIsNanScalar) = "isnan(${generateArgs(func.args, 1)})"
    override fun builtinIsNan(func: KslBuiltinIsNanVector<*, *>) = "isnan(${generateArgs(func.args, 1)})"
    override fun builtinLength(func: KslBuiltinLength<*>) = "length(${generateArgs(func.args, 1)})"
    override fun builtinLog(func: KslBuiltinLogScalar) = "log(${generateArgs(func.args, 1)})"
    override fun builtinLog(func: KslBuiltinLogVector<*>) = "log(${generateArgs(func.args, 1)})"
    override fun builtinLog2(func: KslBuiltinLog2Scalar) = "log2(${generateArgs(func.args, 1)})"
    override fun builtinLog2(func: KslBuiltinLog2Vector<*>) = "log2(${generateArgs(func.args, 1)})"
    override fun builtinMax(func: KslBuiltinMaxScalar<*>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMax(func: KslBuiltinMaxVector<*, *>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinScalar<*>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinVector<*, *>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMix(func: KslBuiltinMixScalar) = "mix(${generateArgs(func.args, 3)})"
    override fun builtinMix(func: KslBuiltinMixVector<*>) = "mix(${generateArgs(func.args, 3)})"
    override fun builtinNormalize(func: KslBuiltinNormalize<*>) = "normalize(${generateArgs(func.args, 1)})"
    override fun builtinReflect(func: KslBuiltinReflect<*>) = "reflect(${generateArgs(func.args, 2)})"
    override fun builtinRefract(func: KslBuiltinRefract<*>) = "refract(${generateArgs(func.args, 3)})"
    override fun builtinRound(func: KslBuiltinRoundScalar) = "round(${generateArgs(func.args, 1)})"
    override fun builtinRound(func: KslBuiltinRoundVector<*>) = "round(${generateArgs(func.args, 1)})"
    override fun builtinSign(func: KslBuiltinSignScalar<*>) = "sign(${generateArgs(func.args, 1)})"
    override fun builtinSign(func: KslBuiltinSignVector<*, *>) = "sign(${generateArgs(func.args, 1)})"
    override fun builtinPow(func: KslBuiltinPowScalar) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinPow(func: KslBuiltinPowVector<*>) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinRadians(func: KslBuiltinRadiansScalar) = "radians(${generateArgs(func.args, 1)})"
    override fun builtinRadians(func: KslBuiltinRadiansVector<*>) = "radians(${generateArgs(func.args, 1)})"
    override fun builtinSmoothStep(func: KslBuiltinSmoothStepScalar) = "smoothstep(${generateArgs(func.args, 3)})"
    override fun builtinSmoothStep(func: KslBuiltinSmoothStepVector<*>) = "smoothstep(${generateArgs(func.args, 3)})"
    override fun builtinSqrt(func: KslBuiltinSqrtScalar) = "sqrt(${generateArgs(func.args, 1)})"
    override fun builtinSqrt(func: KslBuiltinSqrtVector<*>) = "sqrt(${generateArgs(func.args, 1)})"
    override fun builtinStep(func: KslBuiltinStepScalar) = "step(${generateArgs(func.args, 2)})"
    override fun builtinStep(func: KslBuiltinStepVector<*>) = "step(${generateArgs(func.args, 2)})"
    override fun builtinTrigonometry(func: KslBuiltinTrigonometryScalar) = "${func.name}(${generateArgs(func.args, 1)})"
    override fun builtinTrigonometry(func: KslBuiltinTrigonometryVector<*>) = "${func.name}(${generateArgs(func.args, 1)})"
    override fun builtinTrunc(func: KslBuiltinTruncScalar) = "trunc(${generateArgs(func.args, 1)})"
    override fun builtinTrunc(func: KslBuiltinTruncVector<*>) = "trunc(${generateArgs(func.args, 1)})"

    override fun builtinDeterminant(func: KslBuiltinDeterminant<*, *>) = "determinant(${generateArgs(func.args, 1)})"
    override fun builtinTranspose(func: KslBuiltinTranspose<*, *>) = "transpose(${generateArgs(func.args, 1)})"

    protected fun KslInterStageInterpolation.glsl(): String {
        return when (this) {
            KslInterStageInterpolation.Smooth -> "smooth"
            KslInterStageInterpolation.Flat -> "flat"
            KslInterStageInterpolation.NoPerspective -> "noperspective"
        }
    }

    override fun getStateName(state: KslState): String {
        return when (state.stateName) {
            KslVertexStage.NAME_IN_VERTEX_INDEX -> "gl_VertexID"
            KslVertexStage.NAME_IN_INSTANCE_INDEX -> "gl_InstanceID"
            KslVertexStage.NAME_OUT_POSITION -> "gl_Position"
            KslVertexStage.NAME_OUT_POINT_SIZE -> "gl_PointSize"

            KslFragmentStage.NAME_IN_FRAG_POSITION -> "gl_FragCoord"
            KslFragmentStage.NAME_IN_IS_FRONT_FACING -> "gl_FrontFacing"
            KslFragmentStage.NAME_OUT_DEPTH -> "gl_FragDepth"

            KslComputeStage.NAME_IN_GLOBAL_INVOCATION_ID -> "gl_GlobalInvocationID"
            KslComputeStage.NAME_IN_LOCAL_INVOCATION_ID -> "gl_LocalInvocationID"
            KslComputeStage.NAME_IN_WORK_GROUP_ID -> "gl_WorkGroupID"
            KslComputeStage.NAME_IN_NUM_WORK_GROUPS -> "gl_NumWorkGroups"
            KslComputeStage.NAME_IN_WORK_GROUP_SIZE -> "gl_WorkGroupSize"

            else -> state.stateName
        }
    }

    protected fun glslTypeName(type: KslType): String {
        return when (type) {
            KslTypeVoid -> "void"
            KslBool1 -> "bool"
            KslBool2 -> "bvec2"
            KslBool3 -> "bvec3"
            KslBool4 -> "bvec4"
            KslFloat1 -> "float"
            KslFloat2 -> "vec2"
            KslFloat3 -> "vec3"
            KslFloat4 -> "vec4"
            KslInt1 -> "int"
            KslInt2 -> "ivec2"
            KslInt3 -> "ivec3"
            KslInt4 -> "ivec4"
            KslUint1 -> "uint"
            KslUint2 -> "uvec2"
            KslUint3 -> "uvec3"
            KslUint4 -> "uvec4"
            KslMat2 -> "mat2"
            KslMat3 -> "mat3"
            KslMat4 -> "mat4"

            KslColorSampler1d -> if (hints.compat1dSampler) "sampler2D" else "sampler1D"
            KslColorSampler2d -> "sampler2D"
            KslColorSampler3d -> "sampler3D"
            KslColorSamplerCube -> "samplerCube"
            KslColorSampler2dArray -> "sampler2DArray"
            KslColorSamplerCubeArray -> "samplerCubeArray"

            KslDepthSampler2d -> "sampler2DShadow"
            KslDepthSamplerCube -> "samplerCubeShadow"
            KslDepthSampler2dArray -> "sampler2DArrayShadow"
            KslDepthSamplerCubeArray -> "samplerCubeArrayShadow"

            is KslArrayType<*> -> "${glslTypeName(type.elemType)}[${type.arraySize}]"

            is KslStorageTexture1dType<*> -> "${type.typePrefix}image1D"
            is KslStorageTexture2dType<*> -> "${type.typePrefix}image2D"
            is KslStorageTexture3dType<*> -> "${type.typePrefix}image3D"

            is KslStorage1dType<*> -> error("KslStorage1dType has no glsl type name")
            is KslStorage2dType<*> -> error("KslStorage2dType has no glsl type name")
            is KslStorage3dType<*> -> error("KslStorage3dType has no glsl type name")
        }
    }

    private val KslStorageTextureType<*, *>.typePrefix: String
        get() = when (elemType) {
            is KslFloatType -> ""
            is KslInt1 -> "i"
            is KslInt2 -> "i"
            is KslInt3 -> "i"
            is KslInt4 -> "i"
            is KslUint1 -> "u"
            is KslUint2 -> "u"
            is KslUint3 -> "u"
            is KslUint4 -> "u"
        }

    data class Hints(
        val glslVersionStr: String,
        val compat1dSampler: Boolean = true,
        val replaceUbosByPlainUniforms: Boolean = false
    )

    class GlslGeneratorOutput : GeneratedSourceOutput() {
        companion object {
            fun shaderOutput(vertexSrc: String, fragmentSrc: String) = GlslGeneratorOutput().apply {
                stages[KslShaderStageType.VertexShader] = vertexSrc
                stages[KslShaderStageType.FragmentShader] = fragmentSrc
            }

            fun computeOutput(computeSrc: String) = GlslGeneratorOutput().apply {
                stages[KslShaderStageType.ComputeShader] = computeSrc
            }
        }
    }

    companion object {
        fun generateProgram(program: KslProgram, pipeline: DrawPipeline, hints: Hints): GlslGeneratorOutput {
            val vertexStage = checkNotNull(program.vertexStage) {
                "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
            }
            val fragmentStage = checkNotNull(program.fragmentStage) {
                "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
            }
            val generatorExpressions = vertexStage.generatorExpressions + fragmentStage.generatorExpressions
            val generator = GlslGenerator(generatorExpressions, hints)
            return generator.generateProgram(program, pipeline)
        }

        fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline, hints: Hints): GlslGeneratorOutput {
            val computeStage = checkNotNull(program.computeStage) {
                "KslProgram computeStage is missing"
            }
            val generator = GlslGenerator(computeStage.generatorExpressions, hints)
            return generator.generateComputeProgram(program, pipeline)
        }
    }
}