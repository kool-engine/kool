# PhysX JS

PhysX for JavaScript.

This repo complements the work being done over at [prestomation/PhysX](https://github.com/prestomation/PhysX) to create emscripten bindings for [NVIDIAGameWorks/PhysX](https://github.com/NVIDIAGameWorks/PhysX). 
At some point the prestomation/PhysX fork may be merged into NVIDIAGameWorks/PhysX and this repo will be updated to track that repo instead.

This repo serves multiple purposes:

- Provide a Docker workflow for compiling and building PhysX to WebAssembly via [Emscripten](https://emscripten.org)
- Provide a Docker workflow for adding new bindings (the whole PhysX API is not currently covered yet)
- To publish WebAssembly files to npm ([physx-js](https://www.npmjs.com/package/physx-js)) so that they can be used in projects via npm or CDN

## Example

There is an example with [threejs](https://threejs.org/) and [physx-js](https://www.npmjs.com/package/physx-js) using Webpack in the `/example` folder.
The example is also available to [preview here](https://physx-js-example.deminetix.now.sh), it's hosted on [now](https://zeit.co/)

## Usage via npm

```
npm install physx-js
```

The `physx.release.js` file can be imported via Webpack or included as a script on the page

The `physx.release.wasm` file needs to be served in a static/public folder so that it can be loaded in a browser environment

See `/examples` for how this can be used in Node, Browser and Browser w/ Webpack.

## Usage via CDN (jsDelivr)

You may want to load the WebAssembly files via CDN, in which case you can use jsDelivr's npm mirror, loaded as a script:

```
<script src="https://cdn.jsdelivr.net/npm/physx-js/dist/physx.release.js">
```

You can also target a specific version:

```
<script src="https://cdn.jsdelivr.net/npm/physx-js@0.0.6/dist/physx.release.js">
```

Then configure this to also load the wasm file from the same place:

```
PHYSX({
  locateFile(path) {
    if (path.endsWith('.wasm')) {
      return 'https://cdn.jsdelivr.net/npm/physx-js@0.0.6/dist/physx.release.wasm'
    }
    return path
  }
})

```

## Development

You may want to make your own builds from source, or modify the bindings yourself. The full [PhysX API](https://gameworksdocs.nvidia.com/PhysX/4.1/documentation/physxapi/files/index.html) isn't covered yet but more will be added in the future.

The only dependencies you need to do this are Docker, Node and Npm. All other dependencies are managed inside the docker image.

The scripts run from this repo expect the repo `prestomation/PhysX#emscripten_wip` to be mounted here at `./PhysX`.

```
// Install dependencies
npm install

// Generate the project (i believe this only needs to be run once, can take a while)
npm run generate

// Build WebAssembly files (physx.release.js and physx.release.wasm)
// This is all that needs to be run after making changes to bindings.
npm run make
```

These scripts will start a docker container, mount the PhysX source code and then build and compile it using emscripten.

The output files (js and wasm) are copied into `./dist`

If you want to add new bindings, edit `./PhysX/physx/source/physxwebbindings/src/PxWebBindings.cpp` and then run `npm run make` to rebuild.