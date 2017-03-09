package de.fabmax.kool.shading

import de.fabmax.kool.*
import de.fabmax.kool.gl.FramebufferResource
import de.fabmax.kool.gl.colorAttachmentTex
import de.fabmax.kool.platform.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
class BlurredBackgroundHelper(val texSize: Int = 256) {

    private val tmpRes = MutableVec3f()
    private val tmpVec = MutableVec3f()
    private val texBounds = BoundingBox()

    private val copyTex: Texture
    private val copyTexData = BlurredBgTextureData()
    private val texMesh: Mesh
    private val texMeshFlipped: Mesh
    private val blurX: BlurShaderTap9
    private val blurY: BlurShaderTap9

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

        blurX = BlurShaderTap9().apply {
            uTexture.value = copyTex
            uTexSize.value = texSize.toFloat()
            uDirection.value.set(1f, 0f)
        }
        blurY = BlurShaderTap9().apply {
            uTexture.value = fb1Tex
            uTexSize.value = texSize.toFloat()
            uDirection.value.set(0f, 1f)
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

        val minScrX = Math.max(texBounds.min.x.toInt(), 0)
        val maxScrX = Math.min(texBounds.max.x.toInt(), ctx.viewportWidth)
        val minScrY = Math.max(ctx.viewportHeight - texBounds.max.y.toInt(), 0)
        val maxScrY = Math.min(ctx.viewportHeight - texBounds.min.y.toInt(), ctx.viewportHeight)

        val sizeX = maxScrX - minScrX
        val sizeY = maxScrY - minScrY
        if (maxScrX > 0 && minScrX < ctx.viewportWidth && sizeX > 0 &&
                maxScrY > 0 && minScrY < ctx.viewportHeight && sizeY > 0 &&
                texBounds.min.z < 1 && texBounds.max.z > 0) {

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

    private class BlurShaderTap9() : Shader(Source(
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
                    "  gl_FragColor = texture2D(uTexture, vTexCoord) * 0.2270270270;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord + vec2(1.3846153846 / uTexSize) * uDirection) * 0.3162162162;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - vec2(1.3846153846 / uTexSize) * uDirection) * 0.3162162162;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord + vec2(3.2307692308 / uTexSize) * uDirection) * 0.0702702703;\n" +
                    "  gl_FragColor += texture2D(uTexture, vTexCoord - vec2(3.2307692308 / uTexSize) * uDirection) * 0.0702702703;\n" +
                    "  gl_FragColor.rgb *= gl_FragColor.a;\n" +
                    "}"
    )) {

        val uTexture = UniformTexture2D("uTexture")
        val uTexSize = Uniform1f("uTexSize")
        val uDirection = Uniform2f("uDirection")

        override fun onLoad(ctx: RenderContext) {
            super.onLoad(ctx)
            enableAttribute(Attribute.POSITIONS, "aVertexPosition", ctx)
            enableAttribute(Attribute.TEXTURE_COORDS, "aVertexTexCoord", ctx)
            setUniformLocation(uTexture, ctx)
            setUniformLocation(uTexSize, ctx)
            setUniformLocation(uDirection, ctx)
        }

        override fun onBind(ctx: RenderContext) {
            uTexture.bind(ctx)
            uTexSize.bind(ctx)
            uDirection.bind(ctx)
        }

        override fun onMatrixUpdate(ctx: RenderContext) {
            // not needed
        }
    }
}

fun blurShader(helper: BlurredBackgroundHelper, propsInit: ShaderProps.() -> Unit = { }): BasicShader {
    val props = ShaderProps()
    props.propsInit()
    // vertex color and texture color are required to render fonts
    props.isVertexColor = true
    props.isTextureColor = true
    val generator = GlslGenerator()

    generator.customUnitforms.put("uTexX", Uniform1f("uTexX"))
    generator.customUnitforms.put("uTexY", Uniform1f("uTexY"))
    generator.customUnitforms.put("uTexW", Uniform1f("uTexW"))
    generator.customUnitforms.put("uTexH", Uniform1f("uTexH"))

    generator.injectors += object: GlslGenerator.GlslInjector {
        override fun fsAfterInput(shaderProps: ShaderProps, text: StringBuilder) {
            text.append("uniform float uTexX;\n")
            text.append("uniform float uTexY;\n")
            text.append("uniform float uTexW;\n")
            text.append("uniform float uTexH;\n")
        }

        override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) {
            // sample texture based on fragCoord, not texCoord varying
            // todo: Hopefully the unnecessary texture sampling is optimized away by the shader compiler
            // todo: however it would be nice to not do that in the first place

            text.append("vec2 blurSamplePos = vec2((gl_FragCoord.x - uTexX) / uTexW, ")
                    .append("1.0 - (gl_FragCoord.y - uTexY) / uTexH);\n")
                    .append(GlslGenerator.LOCAL_NAME_FRAG_COLOR).append(" = texture2D(")
                    .append(GlslGenerator.UNIFORM_TEXTURE_0).append(", blurSamplePos) * 0.7;\n");
            //result.x = (tmpRes.x - copyTexData.x) / copyTexData.width
            //result.y = (tmpRes.y + copyTexData.y - ctx.viewportHeight) / copyTexData.height + 1f

//            text.append(GlslGenerator.LOCAL_NAME_FRAG_COLOR).append(" = texture2D(")
//                      .append(GlslGenerator.UNIFORM_TEXTURE_0).append(", ").append(GlslGenerator.VARYING_NAME_TEX_COORD).append(");\n");

            // mix texture color and vertex color
            text.append(GlslGenerator.LOCAL_NAME_FRAG_COLOR).append(" += ")
                    .append(GlslGenerator.LOCAL_NAME_VERTEX_COLOR).append(" * 0.3;\n")
        }
    }

    return BlurShader(helper, props, generator)
}

class BlurShader internal constructor(
                private val helper: BlurredBackgroundHelper, props: ShaderProps, generator: GlslGenerator
        ) : BasicShader(props, generator) {

    private val uTexX: Uniform1f = generator.customUnitforms["uTexX"] as Uniform1f
    private val uTexY: Uniform1f = generator.customUnitforms["uTexY"] as Uniform1f
    private val uTexW: Uniform1f = generator.customUnitforms["uTexW"] as Uniform1f
    private val uTexH: Uniform1f = generator.customUnitforms["uTexH"] as Uniform1f

    init {
        texture = helper.blurredBgTex
    }

    override fun onBind(ctx: RenderContext) {
        super.onBind(ctx)
        uTexX.value = helper.capturedScrX.toFloat()
        uTexY.value = helper.capturedScrY.toFloat()
        uTexW.value = helper.capturedScrW.toFloat()
        uTexH.value = helper.capturedScrH.toFloat()

        uTexX.bind(ctx)
        uTexY.bind(ctx)
        uTexW.bind(ctx)
        uTexH.bind(ctx)
    }
}
