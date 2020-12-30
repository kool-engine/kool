// ammo.js has a dependency to fs (native filesystem access within node.js), which is not available in browsers
// ignore it!
config.node = { fs: 'empty' }