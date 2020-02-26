package de.fabmax.kool.demo

/**
 * @author fabmax
 */
fun main() {
    Demo.setProperty("assetsBaseDir", "./docs/assets")

    Demo.setProperty("pbrDemo.envMaps", "skybox/hdri")
    Demo.setProperty("pbrDemo.materials", "reserve/pbr/materials")

    // launch demo
    demo("treeDemo")

//    depthScene(createDefaultContext())
}
