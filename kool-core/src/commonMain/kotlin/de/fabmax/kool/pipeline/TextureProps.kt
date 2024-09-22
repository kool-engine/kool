package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec2i

data class TextureProps(
    val format: TexFormat = TexFormat.RGBA,

    /**
     * If true, mip-levels are generated for the given texture on load.
     */
    val generateMipMaps: Boolean = true,

    /**
     * If non-null, the loader implementation will try to scale the loaded texture image to the given size in pixels.
     * This is particular useful to scale vector (SVG) images to a desired resolution on load.
     */
    val resolveSize: Vec2i? = null,

    /**
     * Preferred / default sampler settings to be used with this texture.
     */
    val defaultSamplerSettings: SamplerSettings = SamplerSettings()
)

data class SamplerSettings(
    /**
     * Clamp or (mirror-) repeat the texture in U (i.e. X) direction.
     */
    val addressModeU: AddressMode = AddressMode.REPEAT,

    /**
     * Clamp or (mirror-) repeat the texture in V (i.e. Y) direction.
     */
    val addressModeV: AddressMode = AddressMode.REPEAT,

    /**
     * Clamp or (mirror-) repeat the texture in W (i.e. Z) direction (relevant for 3d textures only).
     */
    val addressModeW: AddressMode = AddressMode.REPEAT,

    /**
     * Minification filter method to use (when texture is viewed from far distance).
     * Either [FilterMethod.LINEAR] or [FilterMethod.NEAREST] Default is LINEAR. If LINEAR is chosen and the texture
     * has mip levels, tri-linear filtering is used, bi-linear otherwise.
     */
    val minFilter: FilterMethod = FilterMethod.LINEAR,

    /**
     * Magnification filter method to use (when texture is viewed from close distance).
     * Either [FilterMethod.LINEAR] or [FilterMethod.NEAREST]. Default is LINEAR.
     */
    val magFilter: FilterMethod = FilterMethod.LINEAR,

    /**
     * Maximum level of anisotropic filtering to apply:
     *  - 1: no anisotropic filtering
     *  - 4: default value
     * Anisotropic filtering requires a texture with mip-levels. For textures without mip-levels, this value is
     * ignored. The value might be clamped by the implementation if given level is higher than what the hardware
     * supports.
     */
    val maxAnisotropy: Int = 4,

    /**
     * Compare method to use in case this sampler is used to sample a depth map. Otherwise, compare op is ignored.
     */
    val compareOp: DepthCompareOp = DepthCompareOp.ALWAYS,

    /**
     * Set the base mip level of the bound texture to the given level. Currently only used in WebGPU implementation.
     *
     * **Notice**: This is not really a sampler setting but a texture view setting.
     */
    val baseMipLevel: Int = 0,

    /**
     * Limit the accessible number of mip levels of the bound texture to the given level. Currently only used in
     * WebGPU implementation.
     *
     * **Notice**: This is not really a sampler setting but a texture view setting.
     */
    val numMipLevels: Int = 0,
) {
    /**
     * Returns a copy of this [SamplerSettings] with [minFilter] and [magFilter] set to [FilterMethod.NEAREST].
     */
    fun nearest(): SamplerSettings = copy(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST)

    /**
     * Returns a copy of this [SamplerSettings] with [minFilter] and [magFilter] set to [FilterMethod.LINEAR].
     */
    fun linear(): SamplerSettings = copy(minFilter = FilterMethod.LINEAR, magFilter = FilterMethod.LINEAR)

    /**
     * Returns a copy of this [SamplerSettings] with u, v and w address modes set to [AddressMode.CLAMP_TO_EDGE].
     */
    fun clamped(): SamplerSettings = copy(
        addressModeU = AddressMode.CLAMP_TO_EDGE,
        addressModeV = AddressMode.CLAMP_TO_EDGE,
        addressModeW = AddressMode.CLAMP_TO_EDGE
    )

    /**
     * Returns a copy of this [SamplerSettings] with u, v and w address modes set to [AddressMode.REPEAT].
     */
    fun repeating(): SamplerSettings = copy(
        addressModeU = AddressMode.REPEAT,
        addressModeV = AddressMode.REPEAT,
        addressModeW = AddressMode.REPEAT
    )

    /**
     * Returns a copy of this [SamplerSettings] with u, v and w address modes set to [AddressMode.MIRRORED_REPEAT].
     */
    fun mirroredRepeating(): SamplerSettings = copy(
        addressModeU = AddressMode.MIRRORED_REPEAT,
        addressModeV = AddressMode.MIRRORED_REPEAT,
        addressModeW = AddressMode.MIRRORED_REPEAT
    )

    fun noAnisotropy(): SamplerSettings = copy(maxAnisotropy = 1)
    fun withAnisotropy(maxAnisotropy: Int): SamplerSettings = copy(maxAnisotropy = maxAnisotropy)

    fun limitMipLevels(baseLevel: Int, numLevels: Int) = copy(baseMipLevel = baseLevel, numMipLevels = numLevels)
}

enum class FilterMethod {
    NEAREST,
    LINEAR
}

enum class AddressMode {
    CLAMP_TO_EDGE,
    MIRRORED_REPEAT,
    REPEAT
}
