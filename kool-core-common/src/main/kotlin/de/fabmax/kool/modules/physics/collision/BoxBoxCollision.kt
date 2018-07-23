package de.fabmax.kool.modules.physics.collision

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.physics.Box
import de.fabmax.kool.modules.physics.RigidBody
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * Tests two oriented boxes for collision. Ported from Bullet's btBoxBoxDetector. Code was reorganized quite a bit
 * during porting. Also, temporary objects were moved to private members to reduce GC load (this also means this class
 * is not thread-safe).
 *
 * Bullet's original copyright notice is below:
 *
 * Box-Box collision detection re-distributed under the ZLib license with permission from Russell L. Smith
 * Original version is from Open Dynamics Engine, Copyright (C) 2001,2002 Russell L. Smith.
 * All rights reserved.  Email: russ@q12.org   Web: www.q12.org
 * Bullet Continuous Collision Detection and Physics Library
 * Bullet is Copyright (c) 2003-2006 Erwin Coumans  http://continuousphysics.com/Bullet/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 *    If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is
 *    not required.
 * 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original
 *    software.
 * 3. This notice may not be removed or altered from any source distribution.
 */
class BoxBoxCollision {
    private val normal = MutableVec3f()
    private var depth = 0f

    private val satTest = SatTest()

    // temp vars for edge edge intersection point computation
    private val pa = MutableVec3f()
    private val pb = MutableVec3f()
    private val tmpP = MutableVec3f()

    // temp vars for face something intersection point computation
    private val normal2 = MutableVec3f()
    private val nr = MutableVec3f()
    private val anr = MutableVec3f()
    private val faceCenter = MutableVec3f()
    private val rectIntersector = QuadRectIntersector()
    private val quadPt = MutableVec3f()
    private val tmpVec1 = MutableVec3f()

    /**
     * Tests the two given boxes for intersection. Returns the number of generated contact points (0 in case the
     * boxes do not penetrate each other).
     */
    fun testForCollision(bodyA: RigidBody, bodyB: RigidBody, result: Contacts): Int {
        val satAxis = satTest.performSat(bodyA.shape, bodyB.shape)

        return when {
            // an edge from box 1 touches an edge from box 2
            satAxis > 6 -> computeEdgeEdgeIntersection(bodyA, bodyB, satAxis, result)

            // we have a face-something intersection (because the separating
            // axis is perpendicular to a face). define face 'a' to be the reference
            // face (i.e. the normal vector is perpendicular to this) and face 'b' to be
            // the incident face (the closest face of the other box).
            satAxis > 0 -> {
                val body1 = if (satAxis < 4) bodyA else bodyB
                val body2 = if (satAxis < 4) bodyB else bodyA
                computeFaceSthIntersection(body1, body2, satAxis, result)
            }

            // boxes do not intersect
            else -> 0
        }
    }

    /**
     * Computes the one and only intersection point in case of an edge-edge collision.
     */
    private fun computeEdgeEdgeIntersection(bodyA: RigidBody, bodyB: RigidBody, satAxis: Int, result: Contacts): Int {
        val box1 = bodyA.shape
        val box2 = bodyB.shape

        // find a point pa on the intersecting edge of box 1
        pa.set(box1.center)
        for (i in 0 .. 2) {
            val b = when(i) { 0 -> box1.bX; 1 -> box1.bY; else -> box1.bZ }
            val sign = sign(normal * b )
            pa.x += sign * box1.eX * b.x
            pa.y += sign * box1.eY * b.y
            pa.z += sign * box1.eZ * b.z
        }

        // find a point pb on the intersecting edge of box 2
        pb.set(box2.center)
        for (i in 0 .. 2) {
            val b = box2.base(i)
            val sign = -sign(normal * b )
            pb.x += sign * box2.eX * b.x
            pb.y += sign * box2.eY * b.y
            pb.z += sign * box2.eZ * b.z
        }

        val ua = box1.base((satAxis - 7) / 3)
        val ub = box2.base((satAxis - 7) % 3)
        lineClosestApproach(pa, ua, pb, ub)

//        result.addContactPoint(normal.scale(-1f), pb, -depth)
        result.addContact(bodyA, bodyB) {
            worldNormalOnB.set(normal).scale(-1f)
            worldPosB += result.newWorldPosVec(pb, -depth)
        }
        return 1
    }

    private fun lineClosestApproach(pa: MutableVec3f, ua: Vec3f, pb: MutableVec3f, ub: Vec3f) {
        pb.subtract(pa, tmpP)
        val uaub = ua * ub
        val q1 = ua * tmpP
        val q2 = -(ub * tmpP)
        var d = 1f - uaub * uaub
        if (d > FLT_EPSILON) {
            d = 1f / d
            val alpha = (q1 + uaub * q2) * d
            val beta = (q1 * uaub + q2) * d

            pa.x += ua.x * alpha
            pa.y += ua.y * alpha
            pa.z += ua.z * alpha

            pb.x += ub.x * beta
            pb.y += ub.y * beta
            pb.z += ub.z * beta
        }
    }

    /**
     * Computes the intersection points in case of a face-something collision. There can be up to eight intersection
     * points.
     */
    private fun computeFaceSthIntersection(bodyA: RigidBody, bodyB: RigidBody, satAxis: Int, result: Contacts): Int {
        val box1 = bodyA.shape
        val box2 = bodyB.shape

        // nr = normal vector of reference face dotted with axes of incident box.
        // anr = absolute values of nr.
        normal2.set(normal)
        if (satAxis > 3) {
            normal2.scale(-1f)
        }
        box2.transform.transformTransposed(nr.set(normal2))
        anr.x = abs(nr.x)
        anr.y = abs(nr.y)
        anr.z = abs(nr.z)

        // find the largest component of anr: this corresponds to the normal for the incident face. The other axis
        // numbers of the incident face are stored in a1, a2.
        val lanr = anr.largestComponentIdx()
        val a1 = if (lanr == 0) 1 else 0
        val a2 = if (lanr <= 1) 2 else 1

        // compute center point of incident face, in reference-face coordinates
        val sign = if (nr[lanr] < 0) 1f else -1f
        faceCenter.x = box2.center.x - box1.center.x + sign * box2.halfExtents[lanr] * box2.transform[0, lanr]
        faceCenter.y = box2.center.y - box1.center.y + sign * box2.halfExtents[lanr] * box2.transform[1, lanr]
        faceCenter.z = box2.center.z - box1.center.z + sign * box2.halfExtents[lanr] * box2.transform[2, lanr]

        // find the normal and non-normal axis numbers of the reference box
        val codeN = if (satAxis <= 3) satAxis - 1 else satAxis -4
        val code1 = if (codeN == 0) 1 else 0
        val code2 = if (codeN == 2) 1 else 2

        // find the four corners of the incident face, in reference-face coordinates
        val box1Code1 = box1.base(code1)
        val box1Code2 = box1.base(code2)
        val box2A1 = box2.base(a1)
        val box2A2 = box2.base(a2)
        val c1 = faceCenter * box1Code1
        val c2 = faceCenter * box1Code2

        var m11 = box1Code1 * box2A1
        var m12 = box1Code1 * box2A2
        var m21 = box1Code2 * box2A1
        var m22 = box1Code2 * box2A2

        var k1 = m11 * box2.halfExtents[a1]
        var k2 = m21 * box2.halfExtents[a1]
        val k3 = m12 * box2.halfExtents[a2]
        val k4 = m22 * box2.halfExtents[a2]
        rectIntersector.quad[0].set(c1 - k1 - k3, c2 - k2 - k4)
        rectIntersector.quad[1].set(c1 - k1 + k3, c2 - k2 + k4)
        rectIntersector.quad[2].set(c1 + k1 + k3, c2 + k2 + k4)
        rectIntersector.quad[3].set(c1 + k1 - k3, c2 + k2 - k4)

        rectIntersector.rect.set(box1.halfExtents[code1], box1.halfExtents[code2])
        val n = rectIntersector.intersectRectQuad2()
        val quadInterPts = rectIntersector.resultPoints

        // convert the intersection points into reference-face coordinates,
        // and compute the contact position and depth for each point. only keep
        // those points that have a positive (penetrating) depth. delete points in
        // the 'ret' array as necessary so that 'point' and 'ret' correspond.
        val det1 = 1f / (m11*m22 - m12*m21)
        m11 *= det1
        m12 *= det1
        m21 *= det1
        m22 *= det1

        var cNum = 0
        var cont: Contact? = null
        var swapBodies = false
        for (j in 0 until n) {
            k1 = m22 * (quadInterPts[j].x - c1) - m12 * (quadInterPts[j].y - c2)
            k2 = -m21 * (quadInterPts[j].x - c1) + m11 * (quadInterPts[j].y - c2)
            quadPt.x = faceCenter.x + k1 * box2.base(a1).x + k2 * box2.base(a2).x
            quadPt.y = faceCenter.y + k1 * box2.base(a1).y + k2 * box2.base(a2).y
            quadPt.z = faceCenter.z + k1 * box2.base(a1).z + k2 * box2.base(a2).z
            val depth = box1.halfExtents[codeN] - normal2 * quadPt
            if (depth >= 0) {
                tmpVec1.set(quadPt).add(box1.center)
                if (satAxis >= 4) {
                    // contact bodies are swapped
                    swapBodies = true
                    tmpVec1.subtract(quadPt.set(normal).scale(depth))
                }

                val c = cont ?: result.addContact(if (swapBodies) bodyB else bodyA, if (swapBodies) bodyA else bodyB) {
                    cont = this
                    worldNormalOnB.set(normal).scale(-1f)
                }
                c.worldPosB += result.newWorldPosVec(tmpVec1, -depth)

                cNum++
            }
        }
        return cNum
    }

    private fun Vec3f.largestComponentIdx(): Int = when {
        x > y && x > z -> 0
        y > x && y > z -> 1
        else -> 2
    }

    private fun Box.base(i: Int): Vec3f = when(i) {
        0 -> bX
        1 -> bY
        else -> bZ
    }

    companion object {
        private const val FUDGE_FACTOR = 1.05f
        private const val FUDGE_2 = 1e-5f
    }

    /**
     * Performs an separating axis test on two boxes. Returns the axis code of the deepest penetrating axis (1 .. 15)
     * or 0 if the boxes do not penetrate each other. Also sets [normal] and [depth].
     */
    private inner class SatTest {
        private val satP = MutableVec3f()
        private val satPp = MutableVec3f()
        private val satNormalC = MutableVec3f()

        private var s = -Float.MAX_VALUE
        private var normalR: Vec3f? = null
        private var invertNormal = false
        private var code = 0

        fun performSat(box1: Box, box2: Box): Int {
            box2.center.subtract(box1.center, satP)
            box1.transform.transformTransposed(satPp.set(satP))

            val r11 = box1.bX * box2.bX;    val r12 = box1.bX * box2.bY;    val r13 = box1.bX * box2.bZ
            val r21 = box1.bY * box2.bX;    val r22 = box1.bY * box2.bY;    val r23 = box1.bY * box2.bZ
            val r31 = box1.bZ * box2.bX;    val r32 = box1.bZ * box2.bY;    val r33 = box1.bZ * box2.bZ

            var q11 = abs(r11);     var q12 = abs(r12);     var q13 = abs(r13)
            var q21 = abs(r21);     var q22 = abs(r22);     var q23 = abs(r23)
            var q31 = abs(r31);     var q32 = abs(r32);     var q33 = abs(r33)

            s = -Float.MAX_VALUE
            normalR = null
            invertNormal = false
            code = 0

            // separating axis = box1.bX, box1.bY, box1.bZ
            if (!tstBase(satPp.x, box1.eX + box2.eX*q11 + box2.eY*q12 + box2.eZ*q13, box1.bX, 1)) { return 0 }
            if (!tstBase(satPp.y, box1.eY + box2.eX*q21 + box2.eY*q22 + box2.eZ*q23, box1.bY, 2)) { return 0 }
            if (!tstBase(satPp.z, box1.eZ + box2.eX*q31 + box2.eY*q32 + box2.eZ*q33, box1.bZ, 3)) { return 0 }

            // separating axis = box2.bX, box2.bY, box2.bZ
            if (!tstBase(box2.bX*satP, box1.eX*q11 + box1.eY*q21 + box1.eZ*q31 + box2.eX, box2.bX, 4)) { return 0 }
            if (!tstBase(box2.bY*satP, box1.eX*q12 + box1.eY*q22 + box1.eZ*q32 + box2.eY, box2.bY, 5)) { return 0 }
            if (!tstBase(box2.bZ*satP, box1.eX*q13 + box1.eY*q23 + box1.eZ*q33 + box2.eZ, box2.bZ, 6)) { return 0 }

            q11 += FUDGE_2; q12 += FUDGE_2; q13 += FUDGE_2
            q21 += FUDGE_2; q22 += FUDGE_2; q23 += FUDGE_2
            q31 += FUDGE_2; q32 += FUDGE_2; q33 += FUDGE_2

            // separating axis = box1.bX x (box2.bX, box2.bY, box2.bZ)
            if (!tstBaseXBase(satPp.z*r21 - satPp.y*r31, box1.eY*q31 + box1.eZ*q21 + box2.eY*q13 + box2.eZ*q12, 0f, -r31, r21, 7)) { return 0 }
            if (!tstBaseXBase(satPp.z*r22 - satPp.y*r32, box1.eY*q32 + box1.eZ*q22 + box2.eX*q13 + box2.eZ*q11, 0f, -r32, r22, 8)) { return 0 }
            if (!tstBaseXBase(satPp.z*r23 - satPp.y*r33, box1.eY*q33 + box1.eZ*q23 + box2.eX*q12 + box2.eY*q11, 0f, -r33, r23, 9)) { return 0 }

            // separating axis = box1.bY x (box2.bX, box2.bY, box2.bZ)
            if (!tstBaseXBase(satPp.x*r31 - satPp.z*r11, box1.eX*q31 + box1.eZ*q11 + box2.eY*q23 + box2.eZ*q22, r31, 0f, -r11, 10)) { return 0 }
            if (!tstBaseXBase(satPp.x*r32 - satPp.z*r12, box1.eX*q32 + box1.eZ*q12 + box2.eX*q23 + box2.eZ*q21, r32, 0f, -r12, 11)) { return 0 }
            if (!tstBaseXBase(satPp.x*r33 - satPp.z*r13, box1.eX*q33 + box1.eZ*q13 + box2.eX*q22 + box2.eY*q21, r33, 0f, -r13, 12)) { return 0 }

            // separating axis = box1.bZ x (box2.bX, box2.bY, box2.bZ)
            if (!tstBaseXBase(satPp.y*r11 - satPp.x*r21, box1.eX*q21 + box1.eY*q11 + box2.eY*q33 + box2.eZ*q32, -r21, r11, 0f, 13)) { return 0 }
            if (!tstBaseXBase(satPp.y*r12 - satPp.x*r22, box1.eX*q22 + box1.eY*q12 + box2.eX*q33 + box2.eZ*q31, -r22, r12, 0f, 14)) { return 0 }
            if (!tstBaseXBase(satPp.y*r13 - satPp.x*r23, box1.eX*q23 + box1.eY*q13 + box2.eX*q32 + box2.eY*q31, -r23, r13, 0f, 15)) { return 0 }

            // if we get to this point, the boxes interpenetrate. compute the normal in global coordinates.
            normal.set(normalR ?: satNormalC)
            if (normalR == null) {
                box1.transform.transform(normal, 0f)
            }
            if (invertNormal) {
                normal.scale(-1f)
            }
            depth = -s

            return code
        }

        fun tstBase(expr1: Float, expr2: Float, norm: Vec3f, cc: Int): Boolean {
            val s2 = abs(expr1) - expr2
            if (s2 > 0) {
                return false
            } else if (s2 > s) {
                s = s2
                normalR = norm
                invertNormal = expr1 < 0
                code = cc
            }
            return true
        }

        fun tstBaseXBase(expr1: Float, expr2: Float, n1: Float, n2: Float, n3: Float, cc: Int): Boolean {
            var s2 = abs(expr1) - expr2
            if (s2 > FLT_EPSILON) {
                return false
            }
            val l = sqrt(n1*n1 + n2*n2 + n3*n3)
            if (l > FLT_EPSILON) {
                s2 /= l
                if (s2 * FUDGE_FACTOR > s) {
                    s = s2
                    normalR = null
                    satNormalC.set(n1, n2, n3).scale(1f / l)
                    invertNormal = expr1 < 0
                    code = cc
                }
            }
            return true
        }
    }

    /**
     * Computes intersection points between two face-something penetrating boxes. This is a direct port of Bullet's
     * C code (intersectRectQuad2() in btBoxBoxDetector.cpp) and I don't really know how it works (looks kinda
     * terrible...). It might be a good idea to rewrite this code to something one can actually understand...
     */
    private class QuadRectIntersector {
        // input points
        val quad = Array(4) { MutableVec2f() }
        val rect = MutableVec2f()

        // temporary points needed during computation
        private val bufferPoints = Array(8) { MutableVec2f() }

        // resulting intersection points
        val resultPoints = Array(8) { MutableVec2f() }
        // number of valid result points (also returned by function intersectRectQuad2())
        var resultPointCnt = 0
            private set

        /**
         * find all the intersection points between the 2D rectangle with vertices
         * at (+/-rect.x,+/-rect.y) and the 2D quadrilateral with vertices quad[0..3].
         *
         * the intersection points are returned as x,y pairs in the 'ret' array.
         * the number of intersection points is returned by the function (this will
         * be in the range 0 to 8).
         */
        fun intersectRectQuad2(): Int {
            // q (and r) contain nq (and nr) coordinate points for the current (and chopped) polygons
            var nq = 4
            resultPointCnt = 0
            var q = quad
            var r = resultPoints
            dirLoop@ for (dir in 0..1) {
                // direction notation: xy[0] = x axis, xy[1] = y axis
                var sign = -1
                while (sign < 1) {
                    // chop q along the line xy[dir] = sign*h[dir]
                    var pq = 0
                    var pr = 0
                    resultPointCnt = 0
                    for (i in nq downTo 1) {
                        // go through all points in q and all lines between adjacent points
                        if (sign * q[pq][dir] < rect[dir]) {
                            // this point is inside the chopping line
                            r[pr].set(q[pq])
                            pr++
                            resultPointCnt++
                            if (resultPointCnt >= 8) {
                                q = r
                                break@dirLoop
                            }
                        }
                        val nextQ = if (i > 1) pq + 1 else 0
                        if ((sign * q[pq][dir] < rect[dir]) xor (sign * q[nextQ][dir] < rect[dir])) {
                            // this line crosses the chopping line
                            r[pr][1-dir] = q[pq][1-dir] + (q[nextQ][1-dir] - q[pq][1-dir]) /
                                    (q[nextQ][dir] - q[pq][dir]) * (sign * rect[dir] - q[pq][dir])
                            r[pr][dir] = sign * rect[dir]
                            pr++
                            resultPointCnt++
                            if (resultPointCnt >= 8) {
                                q = r
                                break@dirLoop
                            }
                        }
                        pq++
                    }
                    q = r
                    r = if (q === resultPoints) bufferPoints else resultPoints
                    nq = resultPointCnt
                    sign += 2
                }
            }
            if (q !== resultPoints) {
                for (i in 0 until resultPointCnt) {
                    resultPoints[i].set(q[i])
                }
            }
            return resultPointCnt
        }
    }
}

private fun Mat4f.transformTransposed(vec: MutableVec3f): MutableVec3f {
    val x = vec.x * this[0, 0] + vec.y * this[1, 0] + vec.z * this[2, 0]
    val y = vec.x * this[0, 1] + vec.y * this[1, 1] + vec.z * this[2, 1]
    val z = vec.x * this[0, 2] + vec.y * this[1, 2] + vec.z * this[2, 2]
    return vec.set(x, y, z)
}