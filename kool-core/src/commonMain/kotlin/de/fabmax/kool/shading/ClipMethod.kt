package de.fabmax.kool.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.scene.Node

abstract class ClipMethod : GlslGenerator.GlslInjector {
    open fun getUniforms(): List<Uniform<*>> = emptyList()

    open fun onBind(node: Node, ctx: KoolContext) { }
}

class NoClipping : ClipMethod()

abstract class LocalClip : ClipMethod() {
    override fun vsAfterInput(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("${ctx.glCapabilities.glslDialect.vsOut} vec3 vClipPosLocal;\n")
    }

    override fun vsBeforeProj(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("vClipPosLocal = ${GlslGenerator.L_VS_POSITION}.xyz;\n")
    }

    override fun fsAfterInput(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("${ctx.glCapabilities.glslDialect.fsIn} vec3 vClipPosLocal;\n")
    }
}

class LocalPlaneClip(val numPlanes: Int) : LocalClip() {
    private val uPlanes = Uniform4fv("uClipPlanes", numPlanes)

    val planes: List<MutableVec4f>
        get() = uPlanes.value

    override fun getUniforms(): List<Uniform<*>> {
        return listOf(uPlanes)
    }

    override fun fsBeforeSampling(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("float minPlaneClipDist = 1.0;\n")
        for (i in 0 until numPlanes) {
            text.append("minPlaneClipDist = min(minPlaneClipDist, dot(uClipPlanes[$i].xyz, vClipPosLocal) - uClipPlanes[$i].w);\n")
        }
        text.append("if (minPlaneClipDist < 0.0) { discard; }\n")
    }

    override fun onBind(node: Node, ctx: KoolContext) {
        uPlanes.bind(ctx)
    }
}

class LocalSphereClip : LocalClip() {
    private val uClipSphere = Uniform4f("uClipSphere")

    val center = MutableVec3f()
    var radius = 1f

    override fun getUniforms(): List<Uniform<*>> {
        return listOf(uClipSphere)
    }

    override fun fsBeforeSampling(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("vec3 clipCenterDist = uClipSphere.xyz - vClipPosLocal;\n")
        text.append("if (dot(clipCenterDist, clipCenterDist) > uClipSphere.w) { discard; }\n")
    }

    override fun onBind(node: Node, ctx: KoolContext) {
        uClipSphere.value.set(center.x, center.y, center.z, radius * radius)
        uClipSphere.bind(ctx)
    }
}
