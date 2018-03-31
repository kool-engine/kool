package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
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
import de.fabmax.kool.util.ShadowMap


fun collisionDemo(ctx: KoolContext): Scene = scene {
    light.direction.set(1f, 0.8f, 0.4f)
    defaultShadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

    +sphericalInputTransform {
        +camera
    }

    +makeGroundGrid(10, defaultShadowMap, -1.99f, false, false)

    +twoBoxes(defaultShadowMap)

}


fun twoBoxes(sceneShadowMap: ShadowMap?): BoxMesh {
    val world = CollisionWorld()

    world.gravity.set(0f, -1f, 0f)

    val box1 = uniformMassBox(1f, 1f, 1f, 1f)
    box1.name = "smallBox"
    box1.shape.apply {
        center.set(1f, 1.0f, 0f)
        transform.rotate(-5f, Vec3f.Z_AXIS)
        transform.rotate(-5f, Vec3f.Y_AXIS)
    }

    val box2 = uniformMassBox(2.5f, 1f, 3f, 7.5f)
    box2.name = "bigBox"
    box2.shape.apply {
        center.set(0f, -0.5f, 0f)
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
        shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.PHONG_LIGHTING
            shadowMap = sceneShadowMap
        }

        onPreRender += { ctx ->
            world.stepSimulation(ctx.deltaT)
            updateBoxes()
        }
    }
}