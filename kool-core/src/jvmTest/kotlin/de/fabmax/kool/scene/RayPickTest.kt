package de.fabmax.kool.scene

import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.mock.Mock
import org.junit.Test

class RayPickTest {
    @Test
    fun pickNodeNoTransform() {
        val rayTest = RayTest()
        rayTest.ray.origin.set(0f, 0f, 10f)
        rayTest.ray.direction.set(0f, 0f, -1f)

        val unitCube = ColorMesh().apply {
            generate {
                cube { }
            }
        }
        Mock.mockDraw(unitCube)

        unitCube.rayTest(rayTest)
        assert(rayTest.isHit)
        assert(rayTest.hitNode == unitCube)
        assert(rayTest.hitPositionGlobal.isFuzzyEqual(Vec3f(0f, 0f, 0.5f))) {
            "Expected hit position: (0.5, 0.0, 0.0) but is ${rayTest.hitPositionGlobal}"
        }
    }

    @Test
    fun pickNodeTranslated() {
        val rayTest = RayTest()
        rayTest.ray.origin.set(1f, 0f, 10f)
        rayTest.ray.direction.set(0f, 0f, -1f)

        val unitCube = ColorMesh().apply {
            generate {
                cube { }
            }
            transform.translate(1f, 0f, 0f)
        }
        Mock.mockDraw(unitCube)

        unitCube.rayTest(rayTest)
        assert(rayTest.isHit)
        assert(rayTest.hitNode == unitCube)
        assert(rayTest.hitPositionGlobal.isFuzzyEqual(Vec3f(1f, 0f, 0.5f))) {
            "Expected hit position: (0.5, 0.0, 0.0) but is ${rayTest.hitPositionGlobal}"
        }
    }

    @Test
    fun pickNodeNested() {
        val rayTest = RayTest()
        rayTest.ray.origin.set(2f, 0f, 10f)
        rayTest.ray.direction.set(0f, 0f, -1f)

        val unitCube1 = ColorMesh().apply {
            generate {
                cube { }
            }
            transform.translate(1f, 0f, 0f)
        }
        val unitCube2 = ColorMesh().apply {
            generate {
                cube { }
            }
            transform.translate(1f, 0f, 0f)
        }

        unitCube1.addNode(unitCube2)
        Mock.mockDraw(unitCube1)

        println("1 center: ${unitCube1.globalCenter}, r = ${unitCube1.globalRadius}")
        println("2 center: ${unitCube2.globalCenter}, r = ${unitCube2.globalRadius}")

        unitCube1.rayTest(rayTest)
        assert(rayTest.isHit)
        assert(rayTest.hitNode == unitCube2)
        assert(rayTest.hitPositionGlobal.isFuzzyEqual(Vec3f(2f, 0f, 0.5f))) {
            "Expected hit position: (0.5, 0.0, 0.0) but is ${rayTest.hitPositionGlobal}"
        }
    }
}
