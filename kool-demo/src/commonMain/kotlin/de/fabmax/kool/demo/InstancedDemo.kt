package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.serialization.ModelData
import kotlin.math.atan2
import kotlin.math.sqrt


/**
 * @author fabmax
 */

fun instancedDemo(ctx: KoolContext): Scene = scene {
    // setup camera
    +sphericalInputTransform {
        panMethod = yPlanePan()
        translationBounds = BoundingBox(Vec3f(-50f, 0f, -50f), Vec3f(50f, 0f, 50f))
        // Set some initial rotation so that we look down on the scene
        setMouseRotation(20f, -30f)
        // Add camera to the transform group
        +camera

        (camera as PerspectiveCamera).apply {
            clipNear = 0.3f
            clipFar = 300f
        }
    }

    // create a custom shadow map with reduced maximum distance (increases shadow resolution)
    lighting.shadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

    +makeGroundGrid(100)

    +transformGroup {
        // model uses z-axis as up-axis, rotate it accordingly
        rotate(-90f, Vec3f.X_AXIS)

        // add animated character model
        ctx.assetMgr.loadAsset("player.kmf") { data ->
            if (data == null) {
                throw KoolException("Fatal: Failed loading model")
            }
            // load model
            val model = ModelData.load(data).meshes[0].toMesh() as Armature

            for (i in 1..5) {
                +spawnMesh(model, randomF(0.2f, 0.6f))
            }
        }
    }
}

private fun computeAnimationWeights(speed: Float): Map<String, Float> {
    val idleWeight = (1f - speed * 2f).clamp(0f, 1f)
    val runWeight = ((speed - 0.5f) * 2f).clamp(0f, 1f)
    val walkWeight = when {
        runWeight > 0f -> 1f - runWeight
        else -> 1f - idleWeight
    }
    return mapOf("Armature|idle" to idleWeight, "Armature|walk" to walkWeight, "Armature|run" to runWeight)
}

private fun spawnMesh(proto: Armature, movementSpeed: Float): Armature {
    // create custom model instances
    val instances = InstancedMesh.Instances<ModelInstance>(100)

    // loaded Armature is an InstancedMesh with default attributes, for this demo we also want a custom
    // attribute, hence we need to create a copy with additional attributes
    val mesh = Armature(proto.meshData, proto.name, instances, InstancedMesh.makeAttributeList(ModelShader.INSTANCE_COLOR))
    // also copy bone info
    mesh.copyBonesAndAnimations(proto)

    // spawn instances
    for (i in 1..instances.maxInstances) {
        val agent = ModelInstance(movementSpeed)
        instances.instances += agent
        mesh.onPreRender += { agent.animate(it) }
    }

    // create shader
    mesh.shader = ModelShader {
        lightModel = LightModel.PHONG_LIGHTING
        colorModel = ColorModel.CUSTOM_COLOR
        isReceivingShadows = true
        isInstanced = true

        if (!mesh.isCpuAnimated) {
            // do mesh animation on vertex shader if available.
            // Works with GLSL version 300 and above (OpenGL (ES) 3.0 and WebGL2)
            numBones = mesh.bones.size
        }
    }

    computeAnimationWeights(sqrt(movementSpeed)).forEach { (name, weight) -> mesh.getAnimation(name)?.weight = weight }
    mesh.animationPos = randomF()

    return mesh
}

private class ModelInstance(val movementSpeed: Float) : InstancedMesh.Instance(Mat4f()) {
    private val spawnPosition = Vec3f(randomF(-20f, 20f), randomF(-20f, 20f), 0f)
    private val position = MutableVec3f(spawnPosition)

    private val headingVec = MutableVec3f()
    private val heading = InterpolatedFloat(randomF(0f, 360f), randomF(0f, 360f))
    private val headingAnimator = CosAnimator(heading).apply {
        duration = 5f
        progress = randomF()
    }

    private val color = MutableColor()

    init {
        applyTransform()
    }

    override fun putInstanceAttributes(target: Float32Buffer) {
        super.putInstanceAttributes(target)
        target.put(color.r).put(color.g).put(color.b).put(color.a)
    }

    fun animate(ctx: KoolContext) {
        headingAnimator.tick(ctx)
        if (headingAnimator.progress >= 1f) {
            headingAnimator.progress = 0f
            headingAnimator.speed = 1f
            heading.from = heading.to
            heading.to = if (position.distance(spawnPosition) < 15f) {
                randomF(0f, 360f)
            } else {
                val dx = spawnPosition.x - position.x
                val dy = spawnPosition.y - position.y
                atan2(dy, dx).toDeg() + 90
            }
        }

        headingVec.set(Vec3f.NEG_Y_AXIS).rotate(heading.value, Vec3f.Z_AXIS)
        position += headingVec.scale(5f * movementSpeed * ctx.deltaT)

        color.set(ColorGradient.PLASMA.getColor(position.distance(spawnPosition), 0f, 25f))

        applyTransform()
    }

    private fun applyTransform() {
        modelMat.setIdentity()
                .translate(position)
                .rotate(heading.value, Vec3f.Z_AXIS)
    }
}

private class ModelShader(props: ShaderProps.() -> Unit) :
        BasicShader(ShaderProps().apply(props), GlslGenerator()) {

    init {
        generator.customAttributes += INSTANCE_COLOR

        generator.injectors += object : GlslGenerator.GlslInjector {
            override fun vsAfterInput(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                text.append("flat ${generator.vsOut} vec4 inst_color;\n")
            }

            override fun vsEnd(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                text.append("inst_color = ${INSTANCE_COLOR.glslSrcName};\n")
            }

            override fun fsAfterInput(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                text.append("flat ${generator.fsIn} vec4 inst_color;\n")
            }

            override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                text.append("${generator.fsOutBody} = inst_color;\n")
            }
        }
    }

    companion object {
        val INSTANCE_COLOR = Attribute("attrib_inst_color", AttributeType.COLOR_4F).apply { divisor = 1 }
    }
}
