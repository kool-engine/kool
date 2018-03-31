package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.assetTexture
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_REPEAT
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BoxMesh
import de.fabmax.kool.physics.CollisionWorld
import de.fabmax.kool.physics.staticBox
import de.fabmax.kool.physics.uniformMassBox
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.lineMesh


fun collisionDemo(ctx: KoolContext): Scene = scene {
    light.direction.set(1f, 0.8f, 0.4f)
    defaultShadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

    +sphericalInputTransform {
        +camera
        setMouseRotation(20f, -20f)
    }

    +twoBoxes().apply {
        shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.PHONG_LIGHTING
            shadowMap = defaultShadowMap
            isNormalMapped = true
            specularIntensity = 0.25f

            val props = TextureProps("perlin_nrm.png", GL_LINEAR, GL_REPEAT)
            normalMap = assetTexture(props, ctx)
        }
    }

    +lineMesh {
        isCastingShadow = false
        for (i in -5..5) {
            val color = Color.MD_GREY_600.withAlpha(0.5f)
            val y = -1.995f
            addLine(Vec3f(i.toFloat(), y, -5f), color,
                    Vec3f(i.toFloat(), y, 5f), color)
            addLine(Vec3f(-5f, y, i.toFloat()), color,
                    Vec3f(5f, y, i.toFloat()), color)
        }
        shader = basicShader {
            lightModel = LightModel.NO_LIGHTING
            colorModel = ColorModel.VERTEX_COLOR
            shadowMap = defaultShadowMap
        }
    }

}


fun twoBoxes(): BoxMesh {
    val world = CollisionWorld()

    world.gravity.set(0f, -1f, 0f)

    val box1 = uniformMassBox(1f, 1f, 1f, 1f)
    box1.name = "smallBox"
    box1.shape.apply {
        center.set(0.75f, 3.0f, 0f)
        transform.rotate(-10f, Vec3f.Z_AXIS)
        transform.rotate(-5f, Vec3f.Y_AXIS)
    }

    val box2 = uniformMassBox(2.5f, 1f, 3f, 7.5f)
    box2.name = "bigBox"
    box2.shape.apply {
        center.set(0f, 1.5f, 0f)
        transform.rotate(5f, Vec3f.X_AXIS)
        transform.rotate(10f, Vec3f.Z_AXIS)
    }

    world.bodies += box1
    world.bodies += box2

    // add a box as ground
    world.bodies += staticBox(10f, 0.2f, 10f).apply {
        centerOfMass.set(0f, -2.1f, 0f)
        name = "Ground"
    }

    return BoxMesh(world).apply {
        onPreRender += { ctx ->
            world.stepSimulation(ctx.deltaT)
            updateBoxes()
        }
    }
}