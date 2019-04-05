package de.fabmax.kool.math

import kotlin.math.sqrt

//
// point to line functions (no end, infinite length)
//

fun Vec3f.distanceToLine(lineA: Vec3f, lineB: Vec3f) = sqrt(sqrDistanceToLine(lineA, lineB))

fun Vec3f.sqrDistanceToLine(lineA: Vec3f, lineB: Vec3f) =
        sqrDistancePointToLine(x, y, z, lineA, lineB)

fun sqrDistancePointToLine(x: Float, y: Float, z: Float, lineA: Vec3f, lineB: Vec3f): Float {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3f
    val rx = lineB.x - lineA.x
    val ry = lineB.y - lineA.y
    val rz = lineB.z - lineA.z

    val dotPt = x*rx + y*ry + z*rz
    val dotLineA = lineA.x*rx + lineA.y*ry + lineA.z*rz
    val dotR = rx*rx + ry*ry + rz*rz

    val l = (dotPt - dotLineA) / dotR
    val nx = rx * l + lineA.x
    val ny = ry * l + lineA.y
    val nz = rz * l + lineA.z

    val dx = nx - x
    val dy = ny - y
    val dz = nz - z
    return dx*dx + dy*dy + dz*dz
}

fun Vec3f.nearestPointOnLine(lineA: Vec3f, lineB: Vec3f, result: MutableVec3f): MutableVec3f {
    lineB.subtract(lineA, result)
    val l = (dot(result) - lineA * result) / (result * result)
    return result.scale(l).add(lineA)
}

//
// point to ray functions (one end, infinite length)
//

fun Vec3f.distanceToRay(ray: Ray) = distanceToRay(ray.origin, ray.direction)

fun Vec3f.distanceToRay(origin: Vec3f, direction: Vec3f) = sqrt(sqrDistanceToRay(origin, direction))

fun Vec3f.sqrDistanceToRay(ray: Ray) = sqrDistanceToRay(ray.origin, ray.direction)

fun Vec3f.sqrDistanceToRay(origin: Vec3f, direction: Vec3f) =
        sqrDistancePointToRay(x, y, z, origin, direction)

fun sqrDistancePointToRay(x: Float, y: Float, z: Float, origin: Vec3f, direction: Vec3f): Float {
    val nx: Float
    val ny: Float
    val nz: Float
    val dot = x * direction.x + y * direction.y + z * direction.z
    val l = (dot - origin * direction) / (direction * direction)
    if (l <= 0) {
        nx = origin.x - x
        ny = origin.y - y
        nz = origin.z - z
    } else {
        nx = direction.x * l + origin.x - x
        ny = direction.y * l + origin.y - y
        nz = direction.z * l + origin.z - z
    }
    return nx*nx + ny*ny + nz*nz
}

fun Vec3f.nearestPointOnRay(origin: Vec3f, direction: Vec3f, result: MutableVec3f): MutableVec3f {
    val l = (dot(direction) - origin * direction) / (direction * direction)
    return if (l <= 0) {
        result.set(origin)
    } else {
        result.set(direction).scale(l).add(origin)
    }
}

//
// point to edge functions (two ends, finite length)
//

fun Vec3f.distanceToEdge(edgeA: Vec3f, edgeB: Vec3f) = sqrt(sqrDistanceToEdge(edgeA, edgeB))

fun Vec3f.sqrDistanceToEdge(edgeA: Vec3f, edgeB: Vec3f) =
        sqrDistancePointToEdge(x, y, z, edgeA, edgeB)

fun sqrDistancePointToEdge(x: Float, y: Float, z: Float, edgeA: Vec3f, edgeB: Vec3f): Float {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3f
    val rx = edgeB.x - edgeA.x
    val ry = edgeB.y - edgeA.y
    val rz = edgeB.z - edgeA.z

    val dotPt = x*rx + y*ry + z*rz
    val dotEdgeA = edgeA.x*rx + edgeA.y*ry + edgeA.z*rz
    val dotR = rx*rx + ry*ry + rz*rz

    val l = (dotPt - dotEdgeA) / dotR
    val nx: Float
    val ny: Float
    val nz: Float
    when {
        l <= 0 -> { nx = edgeA.x; ny = edgeA.y; nz = edgeA.z }
        l >= 1 -> { nx = edgeB.x; ny = edgeB.y; nz = edgeB.z }
        else -> { nx = rx * l + edgeA.x; ny = ry * l + edgeA.y; nz = rz * l + edgeA.z }
    }

    val dx = nx - x
    val dy = ny - y
    val dz = nz - z
    return dx*dx + dy*dy + dz*dz
}

fun Vec3f.nearestPointOnEdge(edgeA: Vec3f, edgeB: Vec3f, result: MutableVec3f): MutableVec3f {
    edgeB.subtract(edgeA, result)
    val l = (dot(result) - edgeA * result) / (result * result)
    return when {
        l <= 0 -> result.set(edgeA)
        l >= 1 -> result.set(edgeB)
        else -> result.scale(l).add(edgeA)
    }
}