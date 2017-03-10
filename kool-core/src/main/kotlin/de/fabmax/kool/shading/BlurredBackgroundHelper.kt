package de.fabmax.kool.shading

import de.fabmax.kool.*
import de.fabmax.kool.gl.FramebufferResource
import de.fabmax.kool.gl.colorAttachmentTex
import de.fabmax.kool.platform.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
class BlurredBackgroundHelper(
        val texSize: Int = 256,
        val distMethod: BlurMethod = BlurredBackgroundHelper.BlurMethod.BLUR_13_TAP) {

    enum class BlurMethod {
        BLUR_9_TAP,
        BLUR_13_TAP
    }

    private val tmpRes = MutableVec3f()
    private val tmpVec = MutableVec3f()
    private val texBounds = BoundingBox()

    private val copyTex: Texture
    private val copyTexData = BlurredBgTextureData()
    private val texMesh: Mesh
    private val texMeshFlipped: Mesh
    private val blurX: QuadShader
    private val blurY: QuadShader

    private var blurFb1: FramebufferResource? = null
    private var blurFb2: FramebufferResource? = null

    private val fb1Tex = colorAttachmentTex(texSize, texSize, GL.LINEAR, GL.LINEAR)
    val blurredBgTex = colorAttachmentTex(texSize, texSize, GL.LINEAR, GL.LINEAR)

    val capturedScrX: Int get() = copyTexData.x
    val capturedScrY: Int get() = copyTexData.y
    val capturedScrW: Int get() = copyTexData.width
    val capturedScrH: Int get() = copyTexData.height

    var numPasses = 3

    init {
        val id = Math.random()
        val texProps = TextureProps("DistortedBackground-$id",
                GL.LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)
        copyTex = Texture(texProps, { copyTexData })

        texMesh = textureMesh {
            generator = {
                rect {
                    width = 2f
                    height = 2f
                    origin.set(-1f, -1f, 0f)
                    fullTexCoords()
                }
            }
        }
        texMeshFlipped = textureMesh {
            generator = {
                rect {
                    width = 2f
                    height = 2f
                    origin.set(-1f, -1f, 0f)
                    texCoordUpperLeft.set(0f, 1f)
                    texCoordUpperRight.set(1f, 1f)
                    texCoordLowerLeft.set(0f, 0f)
                    texCoordLowerRight.set(1f, 0f)
                }
            }
        }

        blurX = when(distMethod) {
            BlurMethod.BLUR_9_TAP -> BlurShader9Tap()
            BlurMethod.BLUR_13_TAP -> BlurShader13Tap()
        }.apply {
            uTexture.value = copyTex
            uDirection.value.set(1f / texSize, 0f)
        }

        blurY = when(distMethod) {
            BlurMethod.BLUR_9_TAP -> BlurShader9Tap()
            BlurMethod.BLUR_13_TAP -> BlurShader13Tap()
        }.apply {
            uTexture.value = fb1Tex
            uDirection.value.set(0f, 1f / texSize)
        }
    }

    fun updateDistortionTexture(node: Node, ctx: RenderContext) {
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

        var minScrX = Math.max(texBounds.min.x.toInt(), 0)
        val maxScrX = Math.min(texBounds.max.x.toInt(), ctx.viewportWidth - 1)
        var minScrY = Math.max(ctx.viewportHeight - texBounds.max.y.toInt(), 0)
        val maxScrY = Math.min(ctx.viewportHeight - texBounds.min.y.toInt(), ctx.viewportHeight - 1)

        var sizeX = maxScrX - minScrX
        var sizeY = maxScrY - minScrY
        if (maxScrX > 0 && minScrX < ctx.viewportWidth && sizeX > 0 &&
                maxScrY > 0 && minScrY < ctx.viewportHeight && sizeY > 0 &&
                texBounds.min.z < 1 && texBounds.max.z > 0) {

            // captured texture needs to be square for equal blur effect in x and y direction
            if (sizeX > sizeY) {
                sizeY = Math.min(sizeX, ctx.viewportHeight - 1)
                if (minScrY + sizeY >= ctx.viewportHeight) {
                    minScrY = ctx.viewportHeight - sizeY - 1
                }
            } else if (sizeY > sizeX) {
                sizeX = Math.min(sizeY, ctx.viewportWidth - 1)
                if (minScrX + sizeX >= ctx.viewportWidth) {
                    minScrX = ctx.viewportWidth - sizeX - 1
                }
            }

            copyTex.res?.isLoaded = false
            copyTexData.x = minScrX
            copyTexData.y = minScrY
            copyTexData.setCopyWidth(sizeX)
            copyTexData.setCopyHeight(sizeY)

            doBlurring(ctx)
        }
    }

    private fun doBlurring(ctx: RenderContext) {
        ctx.textureMgr.bindTexture(copyTex, ctx)
        ctx.textureMgr.bindTexture(fb1Tex, ctx)
        ctx.textureMgr.bindTexture(blurredBgTex, ctx)

        val fb1 = blurFb1 ?: FramebufferResource.create(fb1Tex, ctx)
        val fb2 = blurFb2 ?: FramebufferResource.create(blurredBgTex, ctx)

        if (blurFb1 == null) {
            blurFb1 = fb1
        }
        if (blurFb2 == null) {
            blurFb2 = fb2
        }

        ctx.pushAttributes()
        ctx.clearColor = Color.BLACK
        ctx.applyAttributes()

        ctx.shaderMgr.bindShader(null, ctx)
        ctx.mvpState.pushMatrices()
        ctx.mvpState.projMatrix.setIdentity()
        ctx.mvpState.viewMatrix.setIdentity()
        ctx.mvpState.modelMatrix.setIdentity()
        ctx.mvpState.update(ctx)

        blurX.uTexture.value = copyTex
        renderFb(fb1, texMeshFlipped, blurX, ctx)
        renderFb(fb2, texMesh, blurY, ctx)

        for (i in (1..numPasses-1)) {
            blurX.uTexture.value = blurredBgTex
            renderFb(fb1, texMesh, blurX, ctx)
            renderFb(fb2, texMesh, blurY, ctx)
        }

        ctx.mvpState.popMatrices()
        ctx.mvpState.update(ctx)
        ctx.popAttributes()
    }

    private fun renderFb(fb: FramebufferResource, mesh: Mesh, shader: Shader, ctx: RenderContext) {
        fb.bind(ctx)
        GL.clear(GL.COLOR_BUFFER_BIT)
        mesh.shader = shader
        mesh.render(ctx)
        fb.unbind(ctx)
    }

    fun computeTexCoords(result: MutableVec2f, point: Vec3f, node: Node, ctx: RenderContext) {
        val cam = ctx.scene.camera

        tmpVec.set(point)
        node.toGlobalCoords(tmpVec)
        cam.projectScreen(tmpRes, tmpVec, ctx)
        result.x = (tmpRes.x - copyTexData.x) / copyTexData.width
        result.y = (tmpRes.y + copyTexData.y - ctx.viewportHeight) / copyTexData.height + 1f
    }

    private fun addToScreenBounds(cam: Camera, node: Node, x: Float, y: Float, z: Float, ctx: RenderContext) {
        tmpVec.set(x, y, z)
        node.toGlobalCoords(tmpVec)
        cam.projectScreen(tmpRes, tmpVec, ctx)
        texBounds.add(tmpRes)
    }

    private class BlurredBgTextureData : TextureData() {
        var x = 0
        var y = 0

        init {
            isAvailable = true
        }

        fun setCopyWidth(value: Int) {
            width = value
        }

        fun setCopyHeight(value: Int) {
            height = value
        }

        override fun onLoad(texture: Texture, ctx: RenderContext) {
            val res = texture.res ?: throw KoolException("Texture wasn't created")
            GL.copyTexImage2D(res.target, 0, GL.RGBA, x, y, width, height, 0)
        }
    }

    private abstract class QuadShader(src: Shader.Source) : Shader(src) {
        val uTexture = UniformTexture2D("uTexture")
        val uDirection = Uniform2f("uDirection")

        override fun onLoad(ctx: RenderContext) {
            super.onLoad(ctx)
            enableAttribute(Attribute.POSITIONS, "aVertexPosition", ctx)
            enableAttribute(Attribute.TEXTURE_COORDS, "aVertexTexCoord", ctx)
            setUniformLocation(uTexture, ctx)
            setUniformLocation(uDirection, ctx)
        }

        override fun onBind(ctx: RenderContext) {
            uTexture.bind(ctx)
            uDirection.bind(ctx)
        }

        override fun onMatrixUpdate(ctx: RenderContext) {
            // not needed
        }
    }

    private class BlurShader9Tap : QuadShader(Source(
            GL.glslVertHeader() +
                    "attribute vec3 aVertexPosition;\n" +
                    "attribute vec2 aVertexTexCoord;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = vec4(aVertexPosition, 1.0);\n" +
                    "  vTexCoord = aVertexTexCoord;\n" +
                    "}",
            GL.glslFragHeader() +
                    "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "uniform vec2 uDirection;\n" +
                    "uniform float uTexSize;\n" +
                    "varying vec2 vTexCoord;" +
                    "void main() {\n" +
                    "  vec2 off1 = vec2(1.3846153) * uDirection;\n" +
                    "  vec2 off2 = vec2(3.2307692) * uDirection;\n" +
                    "  gl_FragColor = texture2D(uTexture, vTexCoord) * 0.2270270;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord + off1) * 0.3162162;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - off1) * 0.3162162;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord + off2) * 0.0702702;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - off2) * 0.0702702;\n" +
                    "  gl_FragColor.rgb *= gl_FragColor.a;\n" +
                    "}"
    ))

    private class BlurShader13Tap : QuadShader(Source(
            GL.glslVertHeader() +
                    "attribute vec3 aVertexPosition;\n" +
                    "attribute vec2 aVertexTexCoord;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = vec4(aVertexPosition, 1.0);\n" +
                    "  vTexCoord = aVertexTexCoord;\n" +
                    "}",
            GL.glslFragHeader() +
                    "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "uniform vec2 uDirection;\n" +
                    "uniform float uTexSize;\n" +
                    "varying vec2 vTexCoord;" +
                    "void main() {\n" +
                    "  vec2 off1 = vec2(1.4117647) * uDirection;\n" +
                    "  vec2 off2 = vec2(3.2941176) * uDirection;\n" +
                    "  vec2 off3 = vec2(5.1764705) * uDirection;\n" +
                    "  gl_FragColor = texture2D(uTexture, vTexCoord) * 0.1968255;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord + off1) * 0.2969069;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - off1) * 0.2969069;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord + off2) * 0.0944703;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - off2) * 0.0944703;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - off3) * 0.0103813;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - off3) * 0.0103813;\n" +
                    "  gl_FragColor.rgb *= gl_FragColor.a;\n" +
                    "}"
    ))
}

fun blurShader(helper: BlurredBackgroundHelper, propsInit: ShaderProps.() -> Unit = { }): BlurShader {
    val props = ShaderProps()
    props.propsInit()
    val generator = GlslGenerator()

    generator.addCustomUniform(UniformTexture2D("uBlurTexture"))
    generator.addCustomUniform(Uniform1f("uColorMix"))
    generator.addCustomUniform(Uniform1f("uTexX"))
    generator.addCustomUniform(Uniform1f("uTexY"))
    generator.addCustomUniform(Uniform1f("uTexW"))
    generator.addCustomUniform(Uniform1f("uTexH"))

    generator.injectors += object: GlslGenerator.GlslInjector {
        override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) {
            text.append("vec2 blurSamplePos = vec2((gl_FragCoord.x - uTexX) / uTexW, ")
                    .append("1.0 - (gl_FragCoord.y - uTexY) / uTexH);\n")
                    .append(GlslGenerator.LOCAL_NAME_FRAG_COLOR).append(" = texture2D(")
                    .append("uBlurTexture, blurSamplePos) * uColorMix + ")
                    .append(GlslGenerator.LOCAL_NAME_FRAG_COLOR).append(" * (1.0 - uColorMix);\n")
        }
    }

    return BlurShader(helper, props, generator)
}

class BlurShader internal constructor(
                private val helper: BlurredBackgroundHelper, props: ShaderProps, generator: GlslGenerator
        ) : BasicShader(props, generator) {

    private val uBlurTex = generator.customUnitforms["uBlurTexture"] as UniformTexture2D
    private val uColorMix = generator.customUnitforms["uColorMix"] as Uniform1f
    private val uTexX = generator.customUnitforms["uTexX"] as Uniform1f
    private val uTexY = generator.customUnitforms["uTexY"] as Uniform1f
    private val uTexW = generator.customUnitforms["uTexW"] as Uniform1f
    private val uTexH = generator.customUnitforms["uTexH"] as Uniform1f

    var colorMix: Float
        get() = uColorMix.value
        set(value) { uColorMix.value = value }

    init {
        uBlurTex.value = helper.blurredBgTex
        colorMix = 1f
    }

    override fun onBind(ctx: RenderContext) {
        super.onBind(ctx)
        uTexX.value = helper.capturedScrX.toFloat()
        uTexY.value = helper.capturedScrY.toFloat()
        uTexW.value = helper.capturedScrW.toFloat()
        uTexH.value = helper.capturedScrH.toFloat()

        uBlurTex.bind(ctx)
        uColorMix.bind(ctx)
        uTexX.bind(ctx)
        uTexY.bind(ctx)
        uTexW.bind(ctx)
        uTexH.bind(ctx)
    }
}
