package de.fabmax.kool.shading

import de.fabmax.kool.*
import de.fabmax.kool.gl.*
import de.fabmax.kool.math.random
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableVec3f
import kotlin.math.max
import kotlin.math.min

/**
 * @author fabmax
 */

fun blurShader(propsInit: ShaderProps.() -> Unit = { }): BlurShader {
    return BlurShader(ShaderProps().apply(propsInit), GlslGenerator())
}

class BlurShader internal constructor(props: ShaderProps, generator: GlslGenerator) :
        BasicShader(props, generator) {

    private val uBlurTex = addUniform(UniformTexture2D("uBlurTexture"))
    private val uColorMix = addUniform(Uniform1f("uColorMix"))
    private val uTexPos = addUniform(Uniform2f("uTexPos"))
    private val uTexSz = addUniform(Uniform2f("uTexSz"))

    var blurHelper: BlurredBackgroundHelper? = null
    var colorMix: Float
        get() = uColorMix.value
        set(value) { uColorMix.value = value }

    init {
        colorMix = 0f

        generator.customUniforms += uBlurTex
        generator.customUniforms += uColorMix
        generator.customUniforms += uTexPos
        generator.customUniforms += uTexSz

        generator.injectors += object : GlslGenerator.GlslInjector {
            override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) {
                text.append("vec2 blurSamplePos = vec2((gl_FragCoord.x - uTexPos.x) / uTexSz.x, ")
                        .append("1.0 - (gl_FragCoord.y - uTexPos.y) / uTexSz.y);\n")
                        .append("${glCapabilities.glslDialect.fragColorBody} = ${glCapabilities.glslDialect.texSampler}(")
                        .append("uBlurTexture, blurSamplePos) * (1.0 - uColorMix) + ")
                        .append("${glCapabilities.glslDialect.fragColorBody} * uColorMix;\n")
            }
        }
    }

    override fun onBind(ctx: RenderContext) {
        super.onBind(ctx)
        val helper = blurHelper
        if (helper != null) {
            helper.isInUse = true
            uBlurTex.value = helper.getOutputTexture()
            uTexPos.value.set(helper.capturedScrX.toFloat(), helper.capturedScrY.toFloat())
            uTexSz.value.set(helper.capturedScrW.toFloat(), helper.capturedScrH.toFloat())
        }

        uBlurTex.bind(ctx)
        uColorMix.bind(ctx)
        uTexPos.bind(ctx)
        uTexSz.bind(ctx)
    }
}

class BlurredBackgroundHelper(private val texSize: Int = 256,
                              private val blurMethod: BlurMethod = BlurredBackgroundHelper.BlurMethod.BLUR_13_TAP) {

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

    private var blurFb1 = Framebuffer(texSize, texSize).withColor()
    private var blurFb2 = Framebuffer(texSize, texSize).withColor()
    private var blurX: BlurQuadShader? = null
    private var blurY: BlurQuadShader? = null

    var isForceUpdateTex = false
    internal var isInUse = true

    val capturedScrX: Int get() = copyTexData.x
    val capturedScrY: Int get() = copyTexData.y
    val capturedScrW: Int get() = copyTexData.width
    val capturedScrH: Int get() = copyTexData.height

    var numPasses = 2

    init {
        val id = random()
        val texProps = TextureProps("DistortedBackground-$id",
                GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0)
        copyTex = Texture(texProps, { copyTexData })

        texMesh = textureMesh {
            generator = {
                rect {
                    size.set(2f, 2f)
                    origin.set(-1f, -1f, 0f)
                    fullTexCoords()
                }
            }
        }
        texMeshFlipped = textureMesh {
            generator = {
                rect {
                    size.set(2f, 2f)
                    origin.set(-1f, -1f, 0f)
                    texCoordUpperLeft.set(0f, 1f)
                    texCoordUpperRight.set(1f, 1f)
                    texCoordLowerLeft.set(0f, 0f)
                    texCoordLowerRight.set(1f, 0f)
                }
            }
        }
    }

    fun getOutputTexture(): Texture? = blurFb2?.colorAttachment

    fun updateDistortionTexture(node: Node, ctx: RenderContext, bounds: BoundingBox = node.bounds) {
        // Only update the distortion texture if it is really used. This saves considerable performance if it is used
        // as background of a hidden UI.
        // The isInUse flag is set by BlurShaders which use this texture
        if (!isInUse || isForceUpdateTex || ctx.renderPass != RenderPass.SCREEN) {
            return
        }
        isInUse = false

        val cam = node.scene?.camera ?: return

        texBounds.clear()
        addToTexBounds(cam, node, bounds.min.x, bounds.min.y, bounds.min.z, ctx)
        addToTexBounds(cam, node, bounds.min.x, bounds.min.y, bounds.max.z, ctx)
        addToTexBounds(cam, node, bounds.min.x, bounds.max.y, bounds.min.z, ctx)
        addToTexBounds(cam, node, bounds.min.x, bounds.max.y, bounds.max.z, ctx)
        addToTexBounds(cam, node, bounds.max.x, bounds.min.y, bounds.min.z, ctx)
        addToTexBounds(cam, node, bounds.max.x, bounds.min.y, bounds.max.z, ctx)
        addToTexBounds(cam, node, bounds.max.x, bounds.max.y, bounds.min.z, ctx)
        addToTexBounds(cam, node, bounds.max.x, bounds.max.y, bounds.max.z, ctx)

        var minScrX = max(texBounds.min.x.toInt(), 0)
        val maxScrX = min(texBounds.max.x.toInt(), ctx.windowWidth - 1)
        var minScrY = max(ctx.windowHeight - texBounds.max.y.toInt(), 0)
        val maxScrY = min(ctx.windowHeight - texBounds.min.y.toInt(), ctx.windowHeight - 1)

        var sizeX = max(maxScrX - minScrX, 16)
        var sizeY = max(maxScrY - minScrY, 16)
        if (maxScrX > 0 && minScrX < ctx.windowWidth &&
                maxScrY > 0 && minScrY < ctx.windowHeight &&
                texBounds.min.z < 1 && texBounds.max.z > 0) {

            // captured texture needs to be square for equal blur effect in x and y direction
            if (sizeX > sizeY) {
                sizeY = min(sizeX, ctx.windowHeight - 1)
                if (minScrY + sizeY >= ctx.windowHeight) {
                    minScrY = ctx.windowHeight - sizeY - 1
                }
            } else if (sizeY > sizeX) {
                sizeX = min(sizeY, ctx.windowWidth - 1)
                if (minScrX + sizeX >= ctx.windowWidth) {
                    minScrX = ctx.windowWidth - sizeX - 1
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

    fun dispose(ctx: RenderContext) {
        blurFb1.delete(ctx)
        blurFb2.delete(ctx)
        blurX?.dispose(ctx)
        blurY?.dispose(ctx)
        copyTex.dispose(ctx)
        texMesh.dispose(ctx)
        texMeshFlipped.dispose(ctx)
    }

    private fun doBlurring(ctx: RenderContext) {
        ctx.textureMgr.bindTexture(copyTex, ctx)

        val blrX = blurX ?: BlurQuadShader(blurMethod).apply {
            uTexture.value = copyTex
            uDirection.value.set(1f / texSize, 0f)
            blurX = this
        }
        val blrY = blurY ?: BlurQuadShader(blurMethod).apply {
            uTexture.value = blurFb1.colorAttachment
            uDirection.value.set(0f, 1f / texSize)
            blurY = this
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

        blrX.uTexture.value = copyTex
        renderFb(blurFb1, texMeshFlipped, blrX, ctx)
        renderFb(blurFb2, texMesh, blrY, ctx)
        blrX.uTexture.value = blurFb2.colorAttachment

        for (i in 1 until numPasses) {
            renderFb(blurFb1, texMesh, blrX, ctx)
            renderFb(blurFb2, texMesh, blrY, ctx)
        }

        ctx.mvpState.popMatrices()
        ctx.mvpState.update(ctx)
        ctx.popAttributes()
    }

    private fun renderFb(fb: Framebuffer, mesh: Mesh, shader: Shader, ctx: RenderContext) {
        fb.bind(ctx)
        glClear(GL_COLOR_BUFFER_BIT)
        mesh.shader = shader
        mesh.render(ctx)
        fb.unbind(ctx)
    }

    private fun addToTexBounds(cam: Camera, node: Node, x: Float, y: Float, z: Float, ctx: RenderContext) {
        tmpVec.set(x, y, z)
        node.toGlobalCoords(tmpVec)
        cam.projectScreen(tmpVec, ctx, tmpRes)
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
            glCopyTexImage2D(res.target, 0, GL_RGBA, x, y, width, height, 0)
        }
    }

    private class BlurQuadShader(private val blurMethod: BlurMethod) : Shader() {
        val uTexture = addUniform(UniformTexture2D("uTexture"))
        val uDirection = addUniform(Uniform2f("uDirection"))

        override fun generate(ctx: RenderContext) {
            attributes.add(Attribute.POSITIONS)
            attributes.add(Attribute.TEXTURE_COORDS)

            val vs = "${glCapabilities.glslDialect.version}\n" +
                    "${glCapabilities.glslDialect.vsIn} vec3 ${Attribute.POSITIONS.name};\n" +
                    "${glCapabilities.glslDialect.vsIn} vec2 ${Attribute.TEXTURE_COORDS.name};\n" +
                    "${glCapabilities.glslDialect.vsOut} vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = vec4(${Attribute.POSITIONS.name}, 1.0);\n" +
                    "  vTexCoord = ${Attribute.TEXTURE_COORDS.name};\n" +
                    "}"

            val fs: String
            val fragColor = glCapabilities.glslDialect.fragColorBody
            val sampler = glCapabilities.glslDialect.texSampler
            if (blurMethod == BlurMethod.BLUR_9_TAP) {
                fs = "${glCapabilities.glslDialect.version}\n" +
                        "precision mediump float;\n" +
                        "uniform sampler2D uTexture;\n" +
                        "uniform vec2 uDirection;\n" +
                        "uniform float uTexSize;\n" +
                        "${glCapabilities.glslDialect.fsIn} vec2 vTexCoord;" +
                        "${glCapabilities.glslDialect.fragColorHead}\n" +
                        "void main() {\n" +
                        "  vec2 off1 = vec2(1.3846153) * uDirection;\n" +
                        "  vec2 off2 = vec2(3.2307692) * uDirection;\n" +
                        "  $fragColor = $sampler(uTexture, vTexCoord) * 0.2270270;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord + off1) * 0.3162162;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord - off1) * 0.3162162;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord + off2) * 0.0702702;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord - off2) * 0.0702702;\n" +
                        "  $fragColor.rgb *= ${glCapabilities.glslDialect.fragColorBody}.a;\n" +
                        "}"
            } else {
                fs = "${glCapabilities.glslDialect.version}\n" +
                        "precision mediump float;\n" +
                        "uniform sampler2D uTexture;\n" +
                        "uniform vec2 uDirection;\n" +
                        "uniform float uTexSize;\n" +
                        "${glCapabilities.glslDialect.fsIn} vec2 vTexCoord;" +
                        "${glCapabilities.glslDialect.fragColorHead}\n" +
                        "void main() {\n" +
                        "  vec2 off1 = vec2(1.4117647) * uDirection;\n" +
                        "  vec2 off2 = vec2(3.2941176) * uDirection;\n" +
                        "  vec2 off3 = vec2(5.1764705) * uDirection;\n" +
                        "  $fragColor = $sampler(uTexture, vTexCoord) * 0.1968255;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord + off1) * 0.2969069;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord - off1) * 0.2969069;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord + off2) * 0.0944703;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord - off2) * 0.0944703;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord - off3) * 0.0103813;\n" +
                        "  $fragColor += $sampler(uTexture, vTexCoord - off3) * 0.0103813;\n" +
                        "  $fragColor.rgb *= ${glCapabilities.glslDialect.fragColorBody}.a;\n" +
                        "}"
            }
            source = Source(vs, fs)
        }

        override fun onBind(ctx: RenderContext) {
            uTexture.bind(ctx)
            uDirection.bind(ctx)
        }

        override fun onMatrixUpdate(ctx: RenderContext) {
            // not needed
        }
    }
}
