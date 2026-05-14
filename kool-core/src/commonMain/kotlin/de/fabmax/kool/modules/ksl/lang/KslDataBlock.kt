package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.KslDomain
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.TextureSampleType
import de.fabmax.kool.util.Struct

abstract class KslDataBlock(
    val name: String,
    @PublishedApi
    internal val program: KslProgram
) : KslDomain {

    protected fun uniformFloat1(name: String) = program.commonUniformBuffer.uniformFloat1(name)
    protected fun uniformFloat2(name: String) = program.commonUniformBuffer.uniformFloat2(name)
    protected fun uniformFloat3(name: String) = program.commonUniformBuffer.uniformFloat3(name)
    protected fun uniformFloat4(name: String) = program.commonUniformBuffer.uniformFloat4(name)

    protected fun uniformFloat1Array(name: String, size: Int) = program.commonUniformBuffer.uniformFloat1Array(name, size)
    protected fun uniformFloat2Array(name: String, size: Int) = program.commonUniformBuffer.uniformFloat2Array(name, size)
    protected fun uniformFloat3Array(name: String, size: Int) = program.commonUniformBuffer.uniformFloat3Array(name, size)
    protected fun uniformFloat4Array(name: String, size: Int) = program.commonUniformBuffer.uniformFloat4Array(name, size)

    protected fun uniformInt1(name: String) = program.commonUniformBuffer.uniformInt1(name)
    protected fun uniformInt2(name: String) = program.commonUniformBuffer.uniformInt2(name)
    protected fun uniformInt3(name: String) = program.commonUniformBuffer.uniformInt3(name)
    protected fun uniformInt4(name: String) = program.commonUniformBuffer.uniformInt4(name)

    protected fun uniformInt1Array(name: String, size: Int) = program.commonUniformBuffer.uniformInt1Array(name, size)
    protected fun uniformInt2Array(name: String, size: Int) = program.commonUniformBuffer.uniformInt2Array(name, size)
    protected fun uniformInt3Array(name: String, size: Int) = program.commonUniformBuffer.uniformInt3Array(name, size)
    protected fun uniformInt4Array(name: String, size: Int) = program.commonUniformBuffer.uniformInt4Array(name, size)

    protected fun uniformMat2(name: String) = program.commonUniformBuffer.uniformMat2(name)
    protected fun uniformMat3(name: String) = program.commonUniformBuffer.uniformMat3(name)
    protected fun uniformMat4(name: String) = program.commonUniformBuffer.uniformMat4(name)

    protected fun uniformMat2Array(name: String, size: Int) = program.commonUniformBuffer.uniformMat2Array(name, size)
    protected fun uniformMat3Array(name: String, size: Int) = program.commonUniformBuffer.uniformMat3Array(name, size)
    protected fun uniformMat4Array(name: String, size: Int) = program.commonUniformBuffer.uniformMat4Array(name, size)

    protected inline fun <reified S: Struct> uniformStruct(name: String, struct: S, scope: BindGroupScope = BindGroupScope.PIPELINE): KslUniformStruct<S> =
        program.getOrCreateStructUniform(name, struct, scope)

    protected fun texture1d(name: String, isUnfilterable: Boolean = false) =
        program.getOrCreateSampler(name, if (isUnfilterable) TextureSampleType.UNFILTERABLE_FLOAT else TextureSampleType.FLOAT) {
            KslUniform(KslVar(name, KslColorSampler1d, false))
        }
    protected fun texture2d(name: String, isUnfilterable: Boolean = false) =
        program.getOrCreateSampler(name, if (isUnfilterable) TextureSampleType.UNFILTERABLE_FLOAT else TextureSampleType.FLOAT) {
            KslUniform(KslVar(name, KslColorSampler2d, false))
        }
    protected fun texture3d(name: String, isUnfilterable: Boolean = false) =
        program.getOrCreateSampler(name, if (isUnfilterable) TextureSampleType.UNFILTERABLE_FLOAT else TextureSampleType.FLOAT) {
            KslUniform(KslVar(name, KslColorSampler3d, false))
        }
    protected fun textureCube(name: String, isUnfilterable: Boolean = false) =
        program.getOrCreateSampler(name, if (isUnfilterable) TextureSampleType.UNFILTERABLE_FLOAT else TextureSampleType.FLOAT) {
            KslUniform(KslVar(name, KslColorSamplerCube, false))
        }
    protected fun texture2dArray(name: String, isUnfilterable: Boolean = false) =
        program.getOrCreateSampler(name, if (isUnfilterable) TextureSampleType.UNFILTERABLE_FLOAT else TextureSampleType.FLOAT) {
            KslUniform(KslVar(name, KslColorSampler2dArray, false))
        }
    protected fun textureCubeArray(name: String, isUnfilterable: Boolean = false) =
        program.getOrCreateSampler(name, if (isUnfilterable) TextureSampleType.UNFILTERABLE_FLOAT else TextureSampleType.FLOAT) {
            KslUniform(KslVar(name, KslColorSamplerCubeArray, false))
        }

    protected fun texture2dInt(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.INT) { KslUniform(KslVar(name, KslIntSampler2d, false)) }
    protected fun texture3dInt(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.INT) { KslUniform(KslVar(name, KslIntSampler3d, false)) }
    protected fun texture2dArrayInt(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.INT) { KslUniform(KslVar(name, KslIntSampler2dArray, false)) }
    protected fun texture2dUint(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.UINT) { KslUniform(KslVar(name, KslUintSampler2d, false)) }
    protected fun texture3dUint(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.UINT) { KslUniform(KslVar(name, KslUintSampler3d, false)) }
    protected fun texture2dArrayUint(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.UINT) { KslUniform(KslVar(name, KslUintSampler2dArray, false)) }

    protected fun depthTexture2d(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSampler2d, false)) }
    protected fun depthTextureCube(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSamplerCube, false)) }
    protected fun depthTexture2dArray(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSampler2dArray, false)) }
    protected fun depthTextureCubeArray(name: String) =
        program.getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSamplerCubeArray, false)) }
}