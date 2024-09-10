package de.fabmax.kool.scene

import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.mock.Mock
import org.junit.Test

class RayPickTest {
    @Test
    fun pickNodeNoTransform() {
        val rayTest = RayTest()
        rayTest.ray.origin.set(0.0, 0.0, 10.0)
        rayTest.ray.direction.set(0.0, 0.0, -1.0)

        val unitCube = ColorMesh().apply {
            generate {
                cube { }
            }
        }
        Mock.mockDraw(unitCube)

        unitCube.rayTest(rayTest)
        assert(rayTest.isHit)
        assert(rayTest.hitNode == unitCube)
        assert(rayTest.hitPositionGlobal.isFuzzyEqual(Vec3d(0.0, 0.0, 0.5))) {
            "Expected hit position: (0.5, 0.0, 0.0) but is ${rayTest.hitPositionGlobal}"
        }
    }

    @Test
    fun pickNodeTranslated() {
        val rayTest = RayTest()
        rayTest.ray.origin.set(1.0, 0.0, 10.0)
        rayTest.ray.direction.set(0.0, 0.0, -1.0)

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
        assert(rayTest.hitPositionGlobal.isFuzzyEqual(Vec3d(1.0, 0.0, 0.5))) {
            "Expected hit position: (0.5, 0.0, 0.0) but is ${rayTest.hitPositionGlobal}"
        }
    }

    @Test
    fun pickNodeNested() {
        val rayTest = RayTest()
        rayTest.ray.origin.set(2.0, 0.0, 10.0)
        rayTest.ray.direction.set(0.0, 0.0, -1.0)

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
        assert(rayTest.hitPositionGlobal.isFuzzyEqual(Vec3d(2.0, 0.0, 0.5))) {
            "Expected hit position: (0.5, 0.0, 0.0) but is ${rayTest.hitPositionGlobal}"
        }
    }
}
