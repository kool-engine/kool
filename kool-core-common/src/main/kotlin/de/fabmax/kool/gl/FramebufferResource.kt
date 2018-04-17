package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.util.Disposable
import de.fabmax.kool.util.UniqueId
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

class Framebuffer(val width: Int, val height: Int) : Disposable {

    private val fbId = UniqueId.nextId()

    var fbResource: FramebufferResource? = null
        private set

    var colorAttachment: Texture? = null
        private set
    var depthAttachment: Texture? = null
        private set

    fun withColor(): Framebuffer {
        if (colorAttachment == null) {
            colorAttachment = FbColorTexData.colorTex(width, height, fbId)
        }
        return this
    }

    fun withDepth(): Framebuffer {
        if (depthAttachment == null) {
            depthAttachment = FbDepthTexData.depthTex(width, height, fbId)
        }
        return this
    }

    override fun dispose(ctx: KoolContext) {
        fbResource?.delete(ctx)
        colorAttachment?.dispose(ctx)
        depthAttachment?.dispose(ctx)

        fbResource = null
        colorAttachment = null
        depthAttachment = null
    }

    fun bind(ctx: KoolContext) {
        val fb = fbResource ?: FramebufferResource.create(width, height, ctx).apply {
            fbResource = this
            colorAttachment = this@Framebuffer.colorAttachment
            depthAttachment = this@Framebuffer.depthAttachment
        }
        fb.bind(ctx)
    }

    fun unbind(ctx: KoolContext) {
        fbResource?.unbind(ctx)
    }
}

class FramebufferResource private constructor(glRef: Any, val width: Int, val height: Int, ctx: KoolContext) :
        GlResource(glRef, Type.FRAMEBUFFER, ctx) {
    companion object {
        fun create(width: Int, height: Int, ctx: KoolContext): FramebufferResource {
            return FramebufferResource(glCreateFramebuffer(), width, height, ctx)
        }
    }
    private val fbId = UniqueId.nextId()

    var colorAttachment: Texture? = null
    var depthAttachment: Texture? = null
    private var isFbComplete = false

    override fun delete(ctx: KoolContext) {
        glDeleteFramebuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: KoolContext) {
        glBindFramebuffer(GL_FRAMEBUFFER, this)
        if (!isFbComplete) {
            isFbComplete = true

            val color = colorAttachment
            if (color != null) {
                ctx.textureMgr.bindTexture(color, ctx)
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, color.res!!, 0)
            } else {
                glDrawBuffer(GL_NONE)
                glReadBuffer(GL_NONE)
            }
            val depth = depthAttachment
            if (depth != null) {
                ctx.textureMgr.bindTexture(depth, ctx)
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth.res!!, 0)
            }

            var fbStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)
            if (fbStatus != GL_FRAMEBUFFER_COMPLETE) {
                // fixme: Improve depth rendering performance on OpenGL ES 2.0
                // Currently depth renderer expects that there is no color attachment at the depth framebuffer
                // this way regular shaders can be used during depth rendering without big performance impact:
                // without color attachment all expensive fragment shader operations are skipped.
                // However not every implementation supports having only a depth attachment, so we
                // need to specify a color attachment which makes depth rendering very expensive...
                if (colorAttachment == null && depthAttachment != null) {
                    colorAttachment = FbColorTexData.colorTex(width, height, fbId)
                    ctx.textureMgr.bindTexture(colorAttachment!!, ctx)
                    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment!!.res!!, 0)
                    glDrawBuffer(GL_FRONT)
                    glReadBuffer(GL_FRONT)
                    fbStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)
                    logW { "Depth-framebuffer is incomplete without an color attachment, adding one (makes shadow rendering slow)" }
                }

                if (fbStatus != GL_FRAMEBUFFER_COMPLETE) {
                    logE { "Framebuffer is incomplete, status: $fbStatus, has-color: ${colorAttachment != null}, has-depth: ${depthAttachment != null}" }
                }
            }
        }

        ctx.pushAttributes()
        ctx.viewport = KoolContext.Viewport(0, 0, width, height)
        ctx.applyAttributes()
    }

    fun unbind(ctx: KoolContext) {
        glBindFramebuffer(GL_FRAMEBUFFER, null)
        ctx.popAttributes()
    }
}

private class FbColorTexData(width: Int, height: Int) : TextureData() {
    init {
        this.isAvailable = true
        this.width = width
        this.height = height
    }

    override fun onLoad(texture: Texture, ctx: KoolContext) {
        // sets up the color attachment texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
    }

    companion object {
        fun colorTex(sizeX: Int, sizeY: Int, fbId: Long): Texture {
            val colorProps = TextureProps("framebuffer-$fbId-color",
                    GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0)
            return Texture(colorProps) {
                FbColorTexData(sizeX, sizeY)
            }
        }
    }
}

private class FbDepthTexData(width: Int, height: Int) : TextureData() {
    init {
        this.isAvailable = true
        this.width = width
        this.height = height
    }

    override fun onLoad(texture: Texture, ctx: KoolContext) {
        // make sure correct filter method is set (GLES only supports GL_NEAREST for depth textures)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, ctx.glCapabilities.depthFilterMethod)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, ctx.glCapabilities.depthFilterMethod)

        // sets up the depth attachment texture
        glTexImage2D(GL_TEXTURE_2D, 0, ctx.glCapabilities.depthComponentIntFormat, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null)
    }

    companion object {
        fun depthTex(sizeX: Int, sizeY: Int, fbId: Long): Texture {
            val depthProps = TextureProps("framebuffer-$fbId-depth",
                    GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0)
            return Texture(depthProps) {
                FbDepthTexData(sizeX, sizeY)
            }
        }
    }
}