package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.platform.vk.util.Shaderc
import de.fabmax.kool.util.logD
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT
import java.io.InputStream

class ShaderStage(val name: String, val code: ByteArray, val stage: Int, val entryPoint: String = "main") {

    private val codeHash = code.contentHashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShaderStage) return false

        if (codeHash != other.codeHash) return false
        if (stage != other.stage) return false
        if (name != other.name) return false
        if (entryPoint != other.entryPoint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + codeHash
        result = 31 * result + stage
        result = 31 * result + entryPoint.hashCode()
        return result
    }

    companion object {
        fun fromSource(name: String, srcCode: InputStream, stage: Int, entryPoint: String = "main"): ShaderStage {
            return fromSource(name, String(srcCode.readBytes()), stage, entryPoint)
        }

        fun fromSource(name: String, srcCode: String, stage: Int, entryPoint: String = "main"): ShaderStage {
            val compileResult = when (stage) {
                VK_SHADER_STAGE_VERTEX_BIT -> Shaderc.compileVertexShader(srcCode, name, entryPoint)
                VK_SHADER_STAGE_FRAGMENT_BIT -> Shaderc.compileFragmentShader(srcCode, name, entryPoint)
                else -> throw IllegalArgumentException("Invalid shader stage: $stage")
            }
            val data = compileResult.spirvData!!
            val codeArray = ByteArray(data.remaining())
            data.get(codeArray)
            compileResult.free()
            logD { "Loaded shader $name: ${codeArray.size} bytes" }
            return ShaderStage(name, codeArray, stage, entryPoint)
        }
    }
}

class SpirvShaderCode(val vertexStage: ShaderStage, val fragmentStage: ShaderStage) : ShaderCode