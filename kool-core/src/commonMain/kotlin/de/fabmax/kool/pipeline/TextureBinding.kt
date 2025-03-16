package de.fabmax.kool.pipeline

import kotlin.reflect.KProperty

sealed class TextureBinding<T: Texture<*>?>(
    textureName: String,
    defaultTexture: T,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : PipelineBinding(textureName, shader) {

    private var cache: T = defaultTexture
    private var cachedSamplerSettings: SamplerSettings? = defaultSampler

    fun get(): T {
        if (isValid) {
            bindGroupData?.let {
                cache = it.getFromData()
            }
        }
        return cache
    }

    fun set(value: T, samplerSettings: SamplerSettings? = cachedSamplerSettings) {
        cache = value
        cachedSamplerSettings = samplerSettings
        if (isValid) {
            bindGroupData?.setInData(value, samplerSettings)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        pipeline.findBindGroupItem<TextureLayout> { it.name == bindingName }?.let { (group, tex) ->
            bindGroup = group.group
            bindingIndex = tex.bindingIndex
            pipeline.pipelineData.setInData(cache, cachedSamplerSettings)
        }
    }

    protected abstract fun BindGroupData.getFromData(): T
    protected abstract fun BindGroupData.setInData(texture: T, sampler: SamplerSettings?)
}

class Texture1dBinding(
    textureName: String,
    defaultTexture: Texture1d?,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : TextureBinding<Texture1d?>(textureName, defaultTexture, defaultSampler, shader) {
    override fun BindGroupData.getFromData(): Texture1d? {
        return texture1dBindingData(bindingIndex).texture
    }

    override fun BindGroupData.setInData(texture: Texture1d?, sampler: SamplerSettings?) {
        val binding = texture1dBindingData(bindingIndex)
        binding.texture = texture
        binding.sampler = sampler
    }
}

class Texture2dBinding(
    textureName: String,
    defaultTexture: Texture2d?,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : TextureBinding<Texture2d?>(textureName, defaultTexture, defaultSampler, shader) {
    override fun BindGroupData.getFromData(): Texture2d? {
        return texture2dBindingData(bindingIndex).texture
    }

    override fun BindGroupData.setInData(texture: Texture2d?, sampler: SamplerSettings?) {
        val binding = texture2dBindingData(bindingIndex)
        binding.texture = texture
        binding.sampler = sampler
    }
}

class Texture3dBinding(
    textureName: String,
    defaultTexture: Texture3d?,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : TextureBinding<Texture3d?>(textureName, defaultTexture, defaultSampler, shader) {
    override fun BindGroupData.getFromData(): Texture3d? {
        return texture3dBindingData(bindingIndex).texture
    }

    override fun BindGroupData.setInData(texture: Texture3d?, sampler: SamplerSettings?) {
        val binding = texture3dBindingData(bindingIndex)
        binding.texture = texture
        binding.sampler = sampler
    }
}

class TextureCubeBinding(
    textureName: String,
    defaultTexture: TextureCube?,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : TextureBinding<TextureCube?>(textureName, defaultTexture, defaultSampler, shader) {
    override fun BindGroupData.getFromData(): TextureCube? {
        return textureCubeBindingData(bindingIndex).texture
    }

    override fun BindGroupData.setInData(texture: TextureCube?, sampler: SamplerSettings?) {
        val binding = textureCubeBindingData(bindingIndex)
        binding.texture = texture
        binding.sampler = sampler
    }
}

class Texture2dArrayBinding(
    textureName: String,
    defaultTexture: Texture2dArray?,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : TextureBinding<Texture2dArray?>(textureName, defaultTexture, defaultSampler, shader) {
    override fun BindGroupData.getFromData(): Texture2dArray? {
        return texture2dArrayBindingData(bindingIndex).texture
    }

    override fun BindGroupData.setInData(texture: Texture2dArray?, sampler: SamplerSettings?) {
        val binding = texture2dArrayBindingData(bindingIndex)
        binding.texture = texture
        binding.sampler = sampler
    }
}

class TextureCubeArrayBinding(
    textureName: String,
    defaultTexture: TextureCubeArray?,
    defaultSampler: SamplerSettings?,
    shader: ShaderBase<*>
) : TextureBinding<TextureCubeArray?>(textureName, defaultTexture, defaultSampler, shader) {
    override fun BindGroupData.getFromData(): TextureCubeArray? {
        return textureCubeArrayBindingData(bindingIndex).texture
    }

    override fun BindGroupData.setInData(texture: TextureCubeArray?, sampler: SamplerSettings?) {
        val binding = textureCubeArrayBindingData(bindingIndex)
        binding.texture = texture
        binding.sampler = sampler
    }
}
