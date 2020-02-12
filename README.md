# Kotlin + OpenGL = kool
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/fabmax/kool/blob/master/LICENSE)

A multi-platform Vulkan / OpenGL based graphics engine that works on Desktop Java and browsers with
WebGL2. Android support is currently suspended but it should be quite easy to get that going again.

This is just a personal pet-project. However, if you are curious
you can checkout the [javascript demo](https://fabmax.github.io/kool/kool-js/?demo=pbrDemo).
The hamburger-button in the upper-left corner triggers the demo chooser menu. Code for
all demos is available in kool-demo sub-project.

In order to add support for Vulkan, I had to drastically change some parts of the engine and this is an
ongoing process. Hence, stuff is a still a bit messy but things are getting better.

Together with Vulkan
support I implemented a new, much more flexible shader generator. Shaders are composed of nodes quite
similar to Unity's Shader Graph (however it's completely code-based, no fancy editor). Shader code is
generated and compiled from the node-based model on-the-fly for each backend.

## Features / Noticeable stuff:
- All new Vulkan rendering backend (for JVM, based on lwjgl3)
- Support for physical based rendering (with metallic workflow) and image-based lighting
- Normal, roughness, metallic, ambient occlusion and Displacement mapping
- HDR lighting with [Uncharted2 tone-mapping](http://filmicworlds.com/blog/filmic-tonemapping-operators/)
- Lighting with multiple point, spot and directional lights
