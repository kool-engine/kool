package de.fabmax.kool.physics

// assign type aliases to JBullet classes to distinguish them from our own classes

typealias btCollisionShape = com.bulletphysics.collision.shapes.CollisionShape

typealias btBoxShape = com.bulletphysics.collision.shapes.BoxShape
typealias btCapsuleShape = com.bulletphysics.collision.shapes.CapsuleShape
typealias btCylinderShape = com.bulletphysics.collision.shapes.CylinderShape
typealias btSphereShape = com.bulletphysics.collision.shapes.SphereShape
typealias btStaticPlaneShape = com.bulletphysics.collision.shapes.StaticPlaneShape

typealias btRigidBody = com.bulletphysics.dynamics.RigidBody
