package de.fabmax.kool.modules.ksl.lang

class KslProgram {

    private var nextNameIdx = 1
    internal fun nextName(prefix: String): String = "${prefix}_${nextNameIdx++}"

    val vertexStage = KslVertexStage(this)
    val fragmentStage = KslFragmentStage(this)

    val stages = listOf(vertexStage, fragmentStage)

    fun vertexStage(block: KslVertexStage.() -> Unit) {
        vertexStage.apply(block)
    }

    fun fragmentStage(block: KslFragmentStage.() -> Unit) {
        fragmentStage.apply(block)
    }

    private fun registerUniform(uniform: KslUniform<*>) {
        stages.forEach {
            it.uniforms += uniform
            it.globalScope.definedStates += uniform.value
        }
    }

    fun uniformFloat1(name: String) = KslUniformScalar(KslVarScalar(name, KslTypeFloat1, false)).also { registerUniform(it) }
    fun uniformFloat2(name: String) = KslUniformVector(KslVarVector(name, KslTypeFloat2, false)).also { registerUniform(it) }
    fun uniformFloat3(name: String) = KslUniformVector(KslVarVector(name, KslTypeFloat3, false)).also { registerUniform(it) }
    fun uniformFloat4(name: String) = KslUniformVector(KslVarVector(name, KslTypeFloat4, false)).also { registerUniform(it) }

    fun uniformMat4(name: String) = KslUniformMatrix(KslVarMatrix(name, KslTypeMat4, false)).also { registerUniform(it) }

    private fun registerInterStageVar(interStageVar: KslInterStageVar<*>) {
        stages.forEach { it.interStageVars += interStageVar }
        vertexStage.globalScope.definedStates += interStageVar.input
        fragmentStage.globalScope.definedStates += interStageVar.output
    }

    fun interStageFloat1(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageScalar<KslTypeFloat1> {
        val varName = name ?: nextName("interStageF1")
        val input = KslVarScalar(varName, KslTypeFloat1, true)
        val output = KslVarScalar(varName, KslTypeFloat1, true)
        return KslInterStageScalar(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }
    fun interStageFloat2(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageVector<KslTypeFloat2, KslTypeFloat1> {
        val varName = name ?: nextName("interStageF2")
        val input = KslVarVector(varName, KslTypeFloat2, true)
        val output = KslVarVector(varName, KslTypeFloat2, true)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }
    fun interStageFloat3(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageVector<KslTypeFloat3, KslTypeFloat1> {
        val varName = name ?: nextName("interStageF3")
        val input = KslVarVector(varName, KslTypeFloat3, true)
        val output = KslVarVector(varName, KslTypeFloat3, true)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }
    fun interStageFloat4(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageVector<KslTypeFloat4, KslTypeFloat1> {
        val varName = name ?: nextName("interStageF4")
        val input = KslVarVector(varName, KslTypeFloat4, true)
        val output = KslVarVector(varName, KslTypeFloat4, true)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }

    fun prepareGenerate() {
        stages.forEach { it.prepareGenerate() }
    }

}