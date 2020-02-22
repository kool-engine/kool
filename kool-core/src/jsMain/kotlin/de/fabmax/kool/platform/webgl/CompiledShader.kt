package de.fabmax.kool.platform.webgl

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.PrimitiveType
import de.fabmax.kool.util.Usage
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext.Companion.ARRAY_BUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.DYNAMIC_DRAW
import org.khronos.webgl.WebGLRenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.INT
import org.khronos.webgl.WebGLRenderingContext.Companion.LINES
import org.khronos.webgl.WebGLRenderingContext.Companion.POINTS
import org.khronos.webgl.WebGLRenderingContext.Companion.STATIC_DRAW
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE0
import org.khronos.webgl.WebGLRenderingContext.Companion.TRIANGLES
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_INT
import org.khronos.webgl.WebGLUniformLocation

class CompiledShader(val prog: WebGLProgram?, pipeline: Pipeline, val ctx: JsContext) {

    val pipelineName = pipeline.name
    private val pipelineId = pipeline.pipelineHash.toLong()

    private val attributeLocations = mutableMapOf<String, Int>()
    private val uniformLocations = mutableMapOf<String, List<WebGLUniformLocation?>>()
    private val instances = mutableMapOf<Long, ShaderInstance>()

    init {
        pipeline.vertexLayout.bindings.forEach { bnd ->
            bnd.attributes.forEach { attr ->
                attributeLocations[attr.name] = attr.location
            }
        }
        pipeline.descriptorSetLayouts.forEach { set ->
            set.descriptors.forEach { desc ->
                when (desc) {
                    is UniformBuffer -> {
                        desc.uniforms.forEach { uniformLocations[it.name] = listOf(ctx.gl.getUniformLocation(prog, it.name)) }
                    }
                    is TextureSampler -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is CubeMapSampler -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                }
            }
        }
        pipeline.pushConstantRanges.forEach { pcr ->
            pcr.pushConstants.forEach { pc ->
                // in WebGL push constants are mapped to regular uniforms
                uniformLocations[pc.name] = listOf(ctx.gl.getUniformLocation(prog, pc.name))
            }
        }
    }

    private fun getUniformLocations(name: String, arraySize: Int): List<WebGLUniformLocation?> {
        val locations = mutableListOf<WebGLUniformLocation?>()
        if (arraySize > 1) {
            for (i in 0 until arraySize) {
                locations += ctx.gl.getUniformLocation(prog, "$name[$i]")
            }
        } else {
            locations += ctx.gl.getUniformLocation(prog, name)
        }
        return locations
    }

    fun use() {
        ctx.gl.useProgram(prog)
        attributeLocations.values.forEach { loc -> ctx.gl.enableVertexAttribArray(loc) }
    }

    fun unUse() {
        attributeLocations.values.forEach { loc -> ctx.gl.disableVertexAttribArray(loc) }
    }

    fun bindInstance(cmd: DrawCommand): ShaderInstance? {
        val pipelineInst = cmd.pipeline!!
        val inst = instances.getOrPut(pipelineInst.pipelineInstanceId) {
            ShaderInstance(cmd.mesh, pipelineInst)
        }
        return if (inst.bindInstance(cmd)) { inst } else { null }
    }

    fun destroyInstance(pipeline: Pipeline) {
        instances.remove(pipeline.pipelineInstanceId)?.let {
            it.destroyInstance()
            ctx.engineStats.pipelineInstanceDestroyed(pipelineId)
        }
    }

    fun isEmpty(): Boolean = instances.isEmpty()

    fun destroy() {
        ctx.engineStats.pipelineDestroyed(pipelineId)
        ctx.gl.deleteProgram(prog)
    }

    inner class ShaderInstance(val mesh: Mesh, pipeline: Pipeline) {
        private val pushConstants = mutableListOf<PushConstantRange>()
        private val ubos = mutableListOf<UniformBuffer>()
        private val textures = mutableListOf<TextureSampler>()
        private val cubeMaps = mutableListOf<CubeMapSampler>()
        private val mappings = mutableListOf<MappedUniform>()
        private val attributeBinders = mutableMapOf<String, AttributeOnLocation>()

        private var dataBufferF: BufferResource? = null
        private var dataBufferI: BufferResource? = null
        private var indexBuffer: BufferResource? = null

        private var nextTexUnit = TEXTURE0

        var numIndices = 0
        var indexType = 0
        var primitiveType = 0

        private var isDataSet = false

        init {
            pipeline.descriptorSetLayouts.forEach { set ->
                set.descriptors.forEach { desc ->
                    when (desc) {
                        is UniformBuffer -> mapUbo(desc)
                        is TextureSampler -> mapTexture(desc)
                        is CubeMapSampler -> mapCubeMap(desc)
                        else -> TODO("$desc")
                    }
                }
            }
            pipeline.pushConstantRanges.forEach { pc ->
                mapPushConstants(pc)
            }
            ctx.engineStats.pipelineInstanceCreated(pipelineId)
        }

        private fun mapPushConstants(pc: PushConstantRange) {
            pushConstants.add(pc)
            pc.pushConstants.forEach { mappings += MappedUniform.mappedUniform(it, uniformLocations[it.name]?.get(0)) }
        }

        private fun mapUbo(ubo: UniformBuffer) {
            ubos.add(ubo)
            ubo.uniforms.forEach { mappings += MappedUniform.mappedUniform(it, uniformLocations[it.name]?.get(0)) }
        }

        private fun mapTexture(tex: TextureSampler) {
            textures.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex2d(tex, nextTexUnit, locs)
                nextTexUnit += locs.size
            }
        }

        private fun mapCubeMap(cubeMap: CubeMapSampler) {
            cubeMaps.add(cubeMap)
            uniformLocations[cubeMap.name]?.let { locs ->
                mappings += MappedUniformCubeMap(cubeMap, nextTexUnit, locs)
                nextTexUnit += locs.size
            }
        }

        fun bindInstance(drawCmd: DrawCommand): Boolean {
            // call onUpdate callbacks
            for (i in pushConstants.indices) {
                pushConstants[i].onUpdate?.invoke(pushConstants[i], drawCmd)
            }
            for (i in ubos.indices) {
                ubos[i].onUpdate?.invoke(ubos[i], drawCmd)
            }
            for (i in textures.indices) {
                textures[i].onUpdate?.invoke(textures[i], drawCmd)
            }
            for (i in cubeMaps.indices) {
                cubeMaps[i].onUpdate?.invoke(cubeMaps[i], drawCmd)
            }

            // update uniform values
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform(ctx)
            }

            // update vertex data
            checkBuffers()

            if (uniformsValid) {
                // bind vertex data
                indexBuffer?.bind(ctx)
                attributeBinders.values.forEach { it.vbo.bindAttribute(it.loc, ctx) }
            }
            return uniformsValid
        }

        fun destroyInstance() {
            dataBufferF?.delete(ctx)
            dataBufferI?.delete(ctx)
            indexBuffer?.delete(ctx)
            dataBufferF = null
            dataBufferI = null
            indexBuffer = null

            pushConstants.clear()
            ubos.clear()
            textures.clear()
            cubeMaps.clear()
            mappings.clear()
            attributeBinders.clear()
        }

        private fun checkBuffers() {
            val md = mesh.geometry
            if (indexBuffer == null) {
                indexBuffer = BufferResource(ELEMENT_ARRAY_BUFFER, ctx)
            }
            var hasIntData = false
            if (dataBufferF == null) {
                dataBufferF = BufferResource(ARRAY_BUFFER, ctx)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        hasIntData = true
                    } else {
                        attributeLocations[vertexAttrib.glslSrcName]?.let { location ->
                            val vbo = VboBinder(dataBufferF!!, vertexAttrib.type.size / 4,
                                    md.strideBytesF, md.attributeOffsets[vertexAttrib]!! / 4, FLOAT)
                            attributeBinders[vertexAttrib.glslSrcName] = AttributeOnLocation(vbo, location)
                        }
                    }
                }
            }
            if (hasIntData && dataBufferI == null) {
                dataBufferI = BufferResource(ARRAY_BUFFER, ctx)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        attributeLocations[vertexAttrib.glslSrcName]?.let { location ->
                            val vbo = VboBinder(dataBufferI!!, vertexAttrib.type.size / 4,
                                    md.strideBytesI, md.attributeOffsets[vertexAttrib]!! / 4, INT)
                            attributeBinders[vertexAttrib.glslSrcName] = AttributeOnLocation(vbo, location)
                        }
                    }
                }
            }

            if (!md.isBatchUpdate && (!isDataSet || md.hasChanged)) {
                val usage = md.usage.glUsage()

                indexType = UNSIGNED_INT
                indexBuffer?.setData(md.indices, usage, ctx)

                primitiveType = md.primitiveType.glElemType()
                numIndices = md.numIndices
                dataBufferF?.setData(md.dataF, usage, ctx)
                dataBufferI?.setData(md.dataI, usage, ctx)
                md.hasChanged = false

                // fixme: isDataSet flag is only a hack, data buffers should be bound to mesh, not to shader instance
                // if mesh is rendered multiple times per frame with different shaders (e.g. shadow pass + standard
                // pass), buffers are duplicated and only updated for first pass
                isDataSet = true
            }
        }
    }

    private fun PrimitiveType.glElemType(): Int {
        return when (this) {
            PrimitiveType.LINES -> LINES
            PrimitiveType.POINTS -> POINTS
            PrimitiveType.TRIANGLES -> TRIANGLES
        }
    }

    private fun Usage.glUsage(): Int {
        return when (this) {
            Usage.DYNAMIC -> DYNAMIC_DRAW
            Usage.STATIC -> STATIC_DRAW
        }
    }

    private data class AttributeOnLocation(val vbo: VboBinder, val loc: Int)
}