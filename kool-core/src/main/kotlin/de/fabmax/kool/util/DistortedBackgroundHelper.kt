package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.platform.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.ShaderProps

/**
 * @author fabmax
 */
class DistortedBackgroundHelper(private val margin: Int) {

    private val tmpRes = MutableVec3f()
    private val tmpVec = MutableVec3f()
    private val texBounds = BoundingBox()

    val backgroundTex: Texture
    private val copyTexData = CopyTextureData()

    init {
        val texProps = TextureProps("DistortedBackground-${Math.random()}",
                GL.LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)
        backgroundTex = Texture(texProps, { copyTexData })
    }

    fun prepareBackgroundTex(node: Node, ctx: RenderContext) {
        val bounds = node.bounds
        val cam = ctx.scene.camera

        texBounds.clear()
        addToScreenBounds(cam, node, bounds.min.x, bounds.min.y, bounds.min.z, ctx)
        addToScreenBounds(cam, node, bounds.min.x, bounds.min.y, bounds.max.z, ctx)
        addToScreenBounds(cam, node, bounds.min.x, bounds.max.y, bounds.min.z, ctx)
        addToScreenBounds(cam, node, bounds.min.x, bounds.max.y, bounds.max.z, ctx)
        addToScreenBounds(cam, node, bounds.max.x, bounds.min.y, bounds.min.z, ctx)
        addToScreenBounds(cam, node, bounds.max.x, bounds.min.y, bounds.max.z, ctx)
        addToScreenBounds(cam, node, bounds.max.x, bounds.max.y, bounds.min.z, ctx)
        addToScreenBounds(cam, node, bounds.max.x, bounds.max.y, bounds.max.z, ctx)

        val minScrX = Math.max(texBounds.min.x.toInt() - margin, 0)
        val maxScrX = Math.min(texBounds.max.x.toInt() + margin, ctx.viewportWidth)
        val minScrY = Math.max(ctx.viewportHeight - texBounds.max.y.toInt() - margin, 0)
        val maxScrY = Math.min(ctx.viewportHeight - texBounds.min.y.toInt() + margin, ctx.viewportHeight)
        val sizeX = maxScrX - minScrX
        val sizeY = maxScrY - minScrY
        if (maxScrX > 0 && minScrX < ctx.viewportWidth && sizeX > 0 &&
                maxScrY > 0 && minScrY < ctx.viewportHeight && sizeY > 0 &&
                texBounds.min.z < 1 && texBounds.max.z > 0) {

            backgroundTex.res?.isLoaded = false
            copyTexData.x = minScrX
            copyTexData.y = minScrY
            copyTexData.w = sizeX
            copyTexData.h = sizeY
        }
    }

    fun computeTexCoords(result: MutableVec2f, point: Vec3f, node: Node, ctx: RenderContext) {
        val cam = ctx.scene.camera

        tmpVec.set(point)
        node.toGlobalCoords(tmpVec)
        cam.projectScreen(tmpRes, tmpVec, ctx)
        result.x = (tmpRes.x - copyTexData.x) / copyTexData.w
        result.y = (ctx.viewportHeight - tmpRes.y - copyTexData.y) / copyTexData.h
    }

    private fun addToScreenBounds(cam: Camera, node: Node, x: Float, y: Float, z: Float, ctx: RenderContext) {
        tmpVec.set(x, y, z)
        node.toGlobalCoords(tmpVec)
        cam.projectScreen(tmpRes, tmpVec, ctx)
        texBounds.add(tmpRes)
    }

    private class CopyTextureData : TextureData() {
        init {
            isAvailable = true
        }

        var x = 0
        var y = 0
        var w = 0
        var h = 0

        override fun onLoad(texture: Texture, ctx: RenderContext) {
            val res = texture.res ?: throw KoolException("Texture wasn't created")
            GL.copyTexImage2D(res.target, 0, GL.RGBA, x, y, w, h, 0)
        }
    }

    fun blurShader(propsInit: ShaderProps.() -> Unit = { }): BasicShader {
        val props = ShaderProps()
        props.propsInit()
        props.colorModel = ColorModel.TEXTURE_COLOR
        val generator = Platform.createDefaultShaderGenerator()

        // inject shader code for blurring background
        generator.injectors += object: ShaderGenerator.GlslInjector {
            override fun fsAfterInput(shaderProps: ShaderProps, text: StringBuilder) {
                text.append("vec4 blur(vec2 uv) {\n").append("vec4 color;\n")
                for (i in 0..31 step 2) {
                    text.append("color += texture2D(").append(ShaderGenerator.UNIFORM_TEXTURE_0)
                            .append(", vec2(uv.x + ").append(BLUR_OFFSETS[i] * 0.01)
                            .append(", uv.y + ").append(BLUR_OFFSETS[i+1] * 0.01).append(")) * 0.0625;\n")
                }
                /*        .append("vec4 color = texture2D(").append(ShaderGenerator.UNIFORM_TEXTURE_0)
                        .append(", vec2(uv.x - 0.01, uv.y - 0.01)) * 0.25;\n")
                        .append("color += texture2D(").append(ShaderGenerator.UNIFORM_TEXTURE_0)
                        .append(", vec2(uv.x - 0.01, uv.y + 0.01)) * 0.25;\n")
                        .append("color += texture2D(").append(ShaderGenerator.UNIFORM_TEXTURE_0)
                        .append(", vec2(uv.x + 0.01, uv.y - 0.01)) * 0.25;\n")
                        .append("color += texture2D(").append(ShaderGenerator.UNIFORM_TEXTURE_0)
                        .append(", vec2(uv.x + 0.01, uv.y + 0.01)) * 0.25;\n")*/
                text.append("return color * 0.7 + vec4(0.3, 0.3, 0.3, 0.3);\n")
                        .append("}\n")
            }

            override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) {
                text.append(ShaderGenerator.LOCAL_NAME_FRAG_COLOR).append(" = blur(")
                        .append(ShaderGenerator.VARYING_NAME_TEX_COORD).append(");\n")
            }
        }

        val shader = BasicShader(props, generator)
        shader.texture = backgroundTex
        return shader
    }

    companion object {
        val BLUR_OFFSETS = arrayOf(
                -0.9420f, -0.3990f,
                +0.9456f, -0.7689f,
                -0.0942f, -0.9294f,
                +0.3450f, +0.2939f,
                -0.9159f, +0.4577f,
                -0.8154f, -0.8791f,
                -0.3828f, +0.2768f,
                +0.9748f, +0.7565f,
                +0.4432f, -0.9751f,
                +0.5374f, -0.4737f,
                -0.2650f, -0.4189f,
                +0.7920f, +0.1909f,
                -0.2419f, +0.9971f,
                -0.8141f, +0.9144f,
                +0.1998f, +0.7864f,
                +0.1438f, -0.1410f
        )
    }
}
