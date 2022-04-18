package de.fabmax.kool.modules.ksl.lang

class KslUniformBuffer(val name: String, val program: KslProgram, val isShared: Boolean = false) {

    val uniforms = mutableMapOf<String, KslUniform<*>>()

    private fun registerUniform(uniform: KslUniform<*>) {
        uniforms[uniform.name] = uniform
        program.stages.forEach {
            it.globalScope.definedStates += uniform.value
        }
    }

    private inline fun <reified T: KslUniform<*>> getOrCreateUniform(name: String, create: () -> T): T {
        val uniform = uniforms[name] ?: create().also { registerUniform(it) }
        if (uniform !is T) {
            throw IllegalStateException("Existing uniform with name \"$name\" has not the expected type")
        }
        return uniform
    }

    fun uniformFloat1(name: String) = getOrCreateUniform(name) { KslUniformScalar(KslVarScalar(name, KslTypeFloat1, false)) }
    fun uniformFloat2(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeFloat2, false)) }
    fun uniformFloat3(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeFloat3, false)) }
    fun uniformFloat4(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeFloat4, false)) }

    fun uniformFloat1Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformScalarArray(KslArrayScalar(name, KslTypeFloat1, arraySize, false)) }
    fun uniformFloat2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeFloat2, arraySize, false)) }
    fun uniformFloat3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeFloat3, arraySize, false)) }
    fun uniformFloat4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeFloat4, arraySize, false)) }

    fun uniformInt1(name: String) = getOrCreateUniform(name) { KslUniformScalar(KslVarScalar(name, KslTypeInt1, false)) }
    fun uniformInt2(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeInt2, false)) }
    fun uniformInt3(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeInt3, false)) }
    fun uniformInt4(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeInt4, false)) }

    fun uniformInt1Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformScalarArray(KslArrayScalar(name, KslTypeInt1, arraySize, false)) }
    fun uniformInt2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeInt2, arraySize, false)) }
    fun uniformInt3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeInt3, arraySize, false)) }
    fun uniformInt4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeInt4, arraySize, false)) }

    fun uniformMat2(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslTypeMat2, false)) }
    fun uniformMat3(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslTypeMat3, false)) }
    fun uniformMat4(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslTypeMat4, false)) }

    fun uniformMat2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslTypeMat2, arraySize, false)) }
    fun uniformMat3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslTypeMat3, arraySize, false)) }
    fun uniformMat4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslTypeMat4, arraySize, false)) }
}