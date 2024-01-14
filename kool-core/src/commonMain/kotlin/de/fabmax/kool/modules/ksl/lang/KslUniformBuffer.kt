package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.pipeline.BindGroupScope

class KslUniformBuffer(val name: String, val program: KslProgram, val scope: BindGroupScope) {

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

    fun uniformFloat1(name: String) = getOrCreateUniform(name) { KslUniformScalar(KslVarScalar(name, KslFloat1, false)) }
    fun uniformFloat2(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslFloat2, false)) }
    fun uniformFloat3(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslFloat3, false)) }
    fun uniformFloat4(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslFloat4, false)) }

    fun uniformFloat1Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformScalarArray(KslArrayScalar(name, KslFloat1, arraySize, false)) }
    fun uniformFloat2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslFloat2, arraySize, false)) }
    fun uniformFloat3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslFloat3, arraySize, false)) }
    fun uniformFloat4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslFloat4, arraySize, false)) }

    fun uniformInt1(name: String) = getOrCreateUniform(name) { KslUniformScalar(KslVarScalar(name, KslInt1, false)) }
    fun uniformInt2(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslInt2, false)) }
    fun uniformInt3(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslInt3, false)) }
    fun uniformInt4(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslInt4, false)) }

    fun uniformInt1Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformScalarArray(KslArrayScalar(name, KslInt1, arraySize, false)) }
    fun uniformInt2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslInt2, arraySize, false)) }
    fun uniformInt3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslInt3, arraySize, false)) }
    fun uniformInt4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslInt4, arraySize, false)) }

    fun uniformMat2(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslMat2, false)) }
    fun uniformMat3(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslMat3, false)) }
    fun uniformMat4(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslMat4, false)) }

    fun uniformMat2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslMat2, arraySize, false)) }
    fun uniformMat3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslMat3, arraySize, false)) }
    fun uniformMat4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslMat4, arraySize, false)) }
}