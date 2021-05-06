//
// kotlin/js build webpack custom configuration
// contents of this file are merged into the webpack config at build/js/packages/kool-kool-demo/webpack.config.js
//
//   config.* translates to module.exports.*
//
// emscripten compiled native code has a few dependencies to node.js modules not available in browsers
// ignore those as they aren't actually needed
//

config.resolve = {
    fallback: {
        path: false,
        fs: false
    }
}