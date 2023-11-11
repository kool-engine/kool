package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.logE

class CompiledShader(val program: GlProgram, val pipeline: Pipeline, val backend: RenderBackendGl) {

    private val pipelineId = pipeline.pipelineHash.toLong()

    private val attributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val instanceAttributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val uniformLocations = mutableMapOf<String, IntArray>()
    private val uboLayouts = mutableMapOf<String, ExternalBufferLayout>()
    private val instances = mutableMapOf<Long, ShaderInstance>()

    private val gl: GlApi = backend.gl

    init {
        pipeline.layout.vertices.bindings.forEach { bnd ->
            bnd.vertexAttributes.forEach { attr ->
                when (bnd.inputRate) {
                    InputRate.VERTEX -> attributes[attr.attribute.name] = attr
                    InputRate.INSTANCE -> instanceAttributes[attr.attribute.name] = attr
                }
            }
        }
        pipeline.layout.descriptorSets.forEach { set ->
            set.descriptors.forEach { desc ->
                when (desc) {
                    is UniformBuffer -> {
                        val blockIndex = gl.getUniformBlockIndex(program, desc.name)
                        if (blockIndex == gl.INVALID_INDEX) {
                            // descriptor does not describe an actual UBO but plain old uniforms...
                            desc.uniforms.forEach { uniformLocations[it.name] = intArrayOf(gl.getUniformLocation(program, it.name)) }
                        } else {
                            setupUboLayout(desc, blockIndex)
                        }
                    }
                    is TextureSampler1d -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is TextureSampler2d -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is TextureSampler3d -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is TextureSamplerCube -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                }
            }
        }
        pipeline.layout.pushConstantRanges.forEach { pcr ->
            pcr.pushConstants.forEach { pc ->
                // in OpenGL push constants are mapped to regular uniforms
                uniformLocations[pc.name] = intArrayOf(gl.getUniformLocation(program, pc.name))
            }
        }
    }

    private fun setupUboLayout(desc: UniformBuffer, blockIndex: Int) {
        val bufferSize = gl.getActiveUniformBlockParameter(program, blockIndex, gl.UNIFORM_BLOCK_DATA_SIZE)
        val uniformNames = desc.uniforms.map {
            if (it.size > 1) "${it.name}[0]" else it.name
        }.toTypedArray()

        val indices = gl.getUniformIndices(program, uniformNames)
        val offsets = gl.getActiveUniforms(program, indices, gl.UNIFORM_OFFSET)

        val sortedOffsets = offsets.sorted()
        val bufferPositions = Array(desc.uniforms.size) { i ->
            val off = offsets[i]
            val nextOffI = sortedOffsets.indexOf(off) + 1
            val nextOff = if (nextOffI < sortedOffsets.size) sortedOffsets[nextOffI] else bufferSize
            BufferPosition(off, nextOff - off)
        }

        gl.uniformBlockBinding(program, blockIndex, desc.binding)
        uboLayouts[desc.name] = ExternalBufferLayout(desc.uniforms, bufferPositions, bufferSize)
    }

    private fun getUniformLocations(name: String, arraySize: Int): IntArray {
        val locations = mutableListOf<Int>()
        if (arraySize > 1) {
            for (i in 0 until arraySize) {
                locations += gl.getUniformLocation(program, "$name[$i]")
            }
        } else {
            locations += gl.getUniformLocation(program, name)
        }
        return locations.toIntArray()
    }

    fun use() {
        gl.useProgram(program)
        attributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                val location = attr.location + i
                gl.enableVertexAttribArray(location)
                gl.vertexAttribDivisor(location, 0)
            }
        }
        instanceAttributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                val location = attr.location + i
                gl.enableVertexAttribArray(location)
                gl.vertexAttribDivisor(location, 1)
            }
        }
    }

    fun unUse() {
        attributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                gl.disableVertexAttribArray(attr.location + i)
            }
        }
        instanceAttributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                gl.disableVertexAttribArray(attr.location + i)
            }
        }
    }

    fun bindInstance(cmd: DrawCommand): ShaderInstance? {
        val pipelineInst = cmd.pipeline!!
        val inst = instances.getOrPut(pipelineInst.pipelineInstanceId) {
            ShaderInstance(cmd.geometry, cmd.mesh.instances, pipelineInst)
        }
        return if (inst.bindInstance(cmd)) { inst } else { null }
    }

    fun destroyInstance(pipeline: Pipeline) {
        instances.remove(pipeline.pipelineInstanceId)?.let {
            it.destroyInstance()
//            ctx.engineStats.pipelineInstanceDestroyed(pipelineId)
        }
    }

    fun isEmpty(): Boolean = instances.isEmpty()

    fun destroy() {
//        ctx.engineStats.pipelineDestroyed(pipelineId)
        gl.deleteProgram(program)
    }

    inner class ShaderInstance(var geometry: IndexedVertexList, val instances: MeshInstanceList?, val pipeline: Pipeline) {
        private val pushConstants = mutableListOf<PushConstantRange>()
        private val ubos = mutableListOf<UniformBuffer>()
        private val textures1d = mutableListOf<TextureSampler1d>()
        private val textures2d = mutableListOf<TextureSampler2d>()
        private val textures3d = mutableListOf<TextureSampler3d>()
        private val texturesCube = mutableListOf<TextureSamplerCube>()
        private val mappings = mutableListOf<MappedUniform>()
        private val attributeBinders = mutableListOf<AttributeOnLocation>()
        private val instanceAttribBinders = mutableListOf<AttributeOnLocation>()

        private var dataBufferF: BufferResource? = null
        private var dataBufferI: BufferResource? = null
        private var indexBuffer: BufferResource? = null
        private var instanceBuffer: BufferResource? = null
        private var uboBuffers = mutableListOf<BufferResource>()
        private var buffersSet = false

        private var nextTexUnit = gl.TEXTURE0

        var numIndices = 0
        var indexType = 0
        var primitiveType = 0

        init {
            pipeline.layout.descriptorSets.forEach { set ->
                set.descriptors.forEach { desc ->
                    when (desc) {
                        is UniformBuffer -> mapUbo(desc)
                        is TextureSampler1d -> mapTexture1d(desc)
                        is TextureSampler2d -> mapTexture2d(desc)
                        is TextureSampler3d -> mapTexture3d(desc)
                        is TextureSamplerCube -> mapTextureCube(desc)
                    }
                }
            }
            pipeline.layout.pushConstantRanges.forEach { pc ->
                mapPushConstants(pc)
            }
//            ctx.engineStats.pipelineInstanceCreated(pipelineId)
        }

        private fun mapPushConstants(pc: PushConstantRange) {
            pushConstants.add(pc)
            pc.pushConstants.forEach { mappings += MappedUniform.mappedUniform(it, uniformLocations[it.name]!![0], gl) }
        }

        private fun mapUbo(ubo: UniformBuffer) {
            ubos.add(ubo)
            val uboLayout = uboLayouts[ubo.name]
            if (uboLayout != null) {
                mappings += MappedUbo(ubo, uboLayout, gl)

            } else {
                ubo.uniforms.forEach {
                    val location = uniformLocations[it.name]
                    if (location != null) {
                        mappings += MappedUniform.mappedUniform(it, location[0], gl)
                    } else {
                        logE { "Uniform location not present for uniform ${ubo.name}.${it.name}" }
                    }
                }
            }
        }

        private fun mapTexture1d(tex: TextureSampler1d) {
            textures1d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex1d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture2d(tex: TextureSampler2d) {
            textures2d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex2d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture3d(tex: TextureSampler3d) {
            textures3d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex3d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTextureCube(cubeMap: TextureSamplerCube) {
            texturesCube.add(cubeMap)
            uniformLocations[cubeMap.name]?.let { locs ->
                mappings += MappedUniformTexCube(cubeMap, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        fun bindInstance(drawCmd: DrawCommand): Boolean {
            if (geometry !== drawCmd.geometry) {
                geometry = drawCmd.geometry
                destroyBuffers()
            }

            // call onUpdate callbacks
            for (i in pipeline.onUpdate.indices) {
                pipeline.onUpdate[i].invoke(drawCmd)
            }
            for (i in pushConstants.indices) {
                pushConstants[i].onUpdate?.invoke(pushConstants[i], drawCmd)
            }
            for (i in ubos.indices) {
                ubos[i].onUpdate?.invoke(ubos[i], drawCmd)
            }
            for (i in textures1d.indices) {
                textures1d[i].onUpdate?.invoke(textures1d[i], drawCmd)
            }
            for (i in textures2d.indices) {
                textures2d[i].onUpdate?.invoke(textures2d[i], drawCmd)
            }
            for (i in textures3d.indices) {
                textures3d[i].onUpdate?.invoke(textures3d[i], drawCmd)
            }
            for (i in texturesCube.indices) {
                texturesCube[i].onUpdate?.invoke(texturesCube[i], drawCmd)
            }

            // update buffers (vertex data, instance data, UBOs)
            checkBuffers()

            // bind uniform values
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform()
            }

            if (uniformsValid) {
                // bind vertex data
                indexBuffer?.bind()
                attributeBinders.forEach { it.vbo.bindAttribute(it.loc) }
                instanceAttribBinders.forEach { it.vbo.bindAttribute(it.loc) }
            }
            return uniformsValid
        }

        private fun destroyBuffers() {
            attributeBinders.clear()
            instanceAttribBinders.clear()
            dataBufferF?.delete()
            dataBufferI?.delete()
            indexBuffer?.delete()
            instanceBuffer?.delete()
            uboBuffers.forEach { it.delete() }
            dataBufferF = null
            dataBufferI = null
            indexBuffer = null
            instanceBuffer = null
            buffersSet = false
            uboBuffers.clear()
        }

        fun destroyInstance() {
            destroyBuffers()

            pushConstants.clear()
            ubos.clear()
            textures1d.clear()
            textures2d.clear()
            textures3d.clear()
            texturesCube.clear()
            mappings.clear()
        }

        private fun checkBuffers() {
            val md = geometry
            if (indexBuffer == null) {
                indexBuffer = BufferResource(gl.ELEMENT_ARRAY_BUFFER, backend)

                mappings.filterIsInstance<MappedUbo>().forEach { mappedUbo ->
                    val uboBuffer = BufferResource(gl.UNIFORM_BUFFER, backend)
                    uboBuffers += uboBuffer
                    mappedUbo.uboBuffer = uboBuffer
                }
            }
            var hasIntData = false
            if (dataBufferF == null) {
                dataBufferF = BufferResource(gl.ARRAY_BUFFER, backend)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        hasIntData = true
                    } else {
                        val stride = md.byteStrideF
                        val offset = md.attributeByteOffsets[vertexAttrib]!! / 4
                        attributeBinders += attributes.makeAttribBinders(vertexAttrib, dataBufferF!!, stride, offset)
                    }
                }
            }
            if (hasIntData && dataBufferI == null) {
                dataBufferI = BufferResource(gl.ARRAY_BUFFER, backend)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        attributes[vertexAttrib.name]?.let { attr ->
                            val vbo = VboBinder(dataBufferI!!, vertexAttrib.type.byteSize / 4,
                                md.byteStrideI, md.attributeByteOffsets[vertexAttrib]!! / 4, gl.INT)
                            attributeBinders += AttributeOnLocation(vbo, attr.location)
                        }
                    }
                }
            }

            val instanceList = instances
            if (instanceList != null) {
                var instBuf = instanceBuffer
                var isNewlyCreated = false
                if (instBuf == null) {
                    instBuf = BufferResource(gl.ARRAY_BUFFER, backend)
                    instanceBuffer = instBuf
                    isNewlyCreated = true
                    for (instanceAttrib in instanceList.instanceAttributes) {
                        val stride = instanceList.strideBytesF
                        val offset = instanceList.attributeOffsets[instanceAttrib]!! / 4
                        instanceAttribBinders += instanceAttributes.makeAttribBinders(instanceAttrib, instanceBuffer!!, stride, offset)
                    }
                }
                if (instanceList.hasChanged || isNewlyCreated) {
                    instBuf.setData(instanceList.dataF, instanceList.usage.glUsage)
                    backend.afterRenderActions += { instanceList.hasChanged = false }
                }
            }

            if (!md.isBatchUpdate && (md.hasChanged || !buffersSet)) {
                val usage = md.usage.glUsage

                indexType = gl.UNSIGNED_INT
                indexBuffer?.setData(md.indices, usage)

                primitiveType = pipeline.layout.vertices.primitiveType.glElemType
                numIndices = md.numIndices
                dataBufferF?.setData(md.dataF, usage)
                dataBufferI?.setData(md.dataI, usage)

                // fixme: data buffers should be bound to mesh, not to shader instance
                // if mesh is rendered multiple times (e.g. by additional shadow passes), clearing
                // hasChanged flag early results in buffers not being updated
                backend.afterRenderActions += { md.hasChanged = false }
                buffersSet = true
            }
        }
    }

    private fun Map<String, VertexLayout.VertexAttribute>.makeAttribBinders(attr: Attribute, buffer: BufferResource, stride: Int, offset: Int): List<AttributeOnLocation> {
        val binders = mutableListOf<AttributeOnLocation>()
        get(attr.name)?.let { vertAttr ->
            val (slots, size) = attr.glAttribLayout
            for (i in 0 until slots) {
                val off = offset + size * i
                val vbo = VboBinder(buffer, size, stride, off, gl.FLOAT)
                binders += AttributeOnLocation(vbo, vertAttr.location + i)
            }
        }
        return binders
    }

    private val PrimitiveType.glElemType: Int get() = when (this) {
        PrimitiveType.LINES -> gl.LINES
        PrimitiveType.POINTS -> gl.POINTS
        PrimitiveType.TRIANGLES -> gl.TRIANGLES
    }

    private val Usage.glUsage: Int get() = when (this) {
        Usage.DYNAMIC -> gl.DYNAMIC_DRAW
        Usage.STATIC -> gl.STATIC_DRAW
    }

    private val Attribute.glAttribLayout: AttribLayout get() = when (type) {
        GlslType.FLOAT -> AttribLayout(1, 1)
        GlslType.VEC_2F -> AttribLayout(1, 2)
        GlslType.VEC_3F -> AttribLayout(1, 3)
        GlslType.VEC_4F -> AttribLayout(1, 4)
        GlslType.INT -> AttribLayout(1, 1)
        GlslType.VEC_2I -> AttribLayout(1, 2)
        GlslType.VEC_3I -> AttribLayout(1, 3)
        GlslType.VEC_4I -> AttribLayout(1, 4)
        GlslType.MAT_2F -> AttribLayout(2, 2)
        GlslType.MAT_3F -> AttribLayout(3, 3)
        GlslType.MAT_4F -> AttribLayout(4, 4)
    }

    private data class AttribLayout(val slots: Int, val size: Int)

    private data class AttributeOnLocation(val vbo: VboBinder, val loc: Int)
}