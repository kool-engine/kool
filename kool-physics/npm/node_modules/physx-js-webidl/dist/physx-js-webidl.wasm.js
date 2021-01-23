

var PhysX = (function() {
  var _scriptDir = typeof document !== 'undefined' && document.currentScript ? document.currentScript.src : undefined;
  if (typeof __filename !== 'undefined') _scriptDir = _scriptDir || __filename;
  return (
function(PhysX) {
  PhysX = PhysX || {};



// The Module object: Our interface to the outside world. We import
// and export values on it. There are various ways Module can be used:
// 1. Not defined. We create it here
// 2. A function parameter, function(Module) { ..generated code.. }
// 3. pre-run appended it, var Module = {}; ..generated code..
// 4. External script tag defines var Module.
// We need to check if Module already exists (e.g. case 3 above).
// Substitution will be replaced with actual code on later stage of the build,
// this way Closure Compiler will not mangle it (e.g. case 4. above).
// Note that if you want to run closure, and also to use Module
// after the generated code, you will need to define   var Module = {};
// before the code. Then that object will be used in the code, and you
// can continue to use Module afterwards as well.
var Module = typeof PhysX !== 'undefined' ? PhysX : {};


// Set up the promise that indicates the Module is initialized
var readyPromiseResolve, readyPromiseReject;
Module['ready'] = new Promise(function(resolve, reject) {
  readyPromiseResolve = resolve;
  readyPromiseReject = reject;
});

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_main')) {
        Object.defineProperty(Module['ready'], '_main', { configurable: true, get: function() { abort('You are getting _main on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_main', { configurable: true, set: function() { abort('You are setting _main on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_stackSave')) {
        Object.defineProperty(Module['ready'], '_stackSave', { configurable: true, get: function() { abort('You are getting _stackSave on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_stackSave', { configurable: true, set: function() { abort('You are setting _stackSave on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_stackRestore')) {
        Object.defineProperty(Module['ready'], '_stackRestore', { configurable: true, get: function() { abort('You are getting _stackRestore on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_stackRestore', { configurable: true, set: function() { abort('You are setting _stackRestore on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_stackAlloc')) {
        Object.defineProperty(Module['ready'], '_stackAlloc', { configurable: true, get: function() { abort('You are getting _stackAlloc on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_stackAlloc', { configurable: true, set: function() { abort('You are setting _stackAlloc on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '___data_end')) {
        Object.defineProperty(Module['ready'], '___data_end', { configurable: true, get: function() { abort('You are getting ___data_end on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '___data_end', { configurable: true, set: function() { abort('You are setting ___data_end on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '___wasm_call_ctors')) {
        Object.defineProperty(Module['ready'], '___wasm_call_ctors', { configurable: true, get: function() { abort('You are getting ___wasm_call_ctors on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '___wasm_call_ctors', { configurable: true, set: function() { abort('You are setting ___wasm_call_ctors on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '___errno_location')) {
        Object.defineProperty(Module['ready'], '___errno_location', { configurable: true, get: function() { abort('You are getting ___errno_location on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '___errno_location', { configurable: true, set: function() { abort('You are setting ___errno_location on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_malloc')) {
        Object.defineProperty(Module['ready'], '_malloc', { configurable: true, get: function() { abort('You are getting _malloc on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_malloc', { configurable: true, set: function() { abort('You are setting _malloc on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_free')) {
        Object.defineProperty(Module['ready'], '_free', { configurable: true, get: function() { abort('You are getting _free on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_free', { configurable: true, set: function() { abort('You are setting _free on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_setThrew')) {
        Object.defineProperty(Module['ready'], '_setThrew', { configurable: true, get: function() { abort('You are getting _setThrew on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_setThrew', { configurable: true, set: function() { abort('You are setting _setThrew on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_htons')) {
        Object.defineProperty(Module['ready'], '_htons', { configurable: true, get: function() { abort('You are getting _htons on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_htons', { configurable: true, set: function() { abort('You are setting _htons on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_ntohs')) {
        Object.defineProperty(Module['ready'], '_ntohs', { configurable: true, get: function() { abort('You are getting _ntohs on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_ntohs', { configurable: true, set: function() { abort('You are setting _ntohs on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_memcpy')) {
        Object.defineProperty(Module['ready'], '_memcpy', { configurable: true, get: function() { abort('You are getting _memcpy on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_memcpy', { configurable: true, set: function() { abort('You are setting _memcpy on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_htonl')) {
        Object.defineProperty(Module['ready'], '_htonl', { configurable: true, get: function() { abort('You are getting _htonl on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_htonl', { configurable: true, set: function() { abort('You are setting _htonl on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], '_emscripten_main_thread_process_queued_calls')) {
        Object.defineProperty(Module['ready'], '_emscripten_main_thread_process_queued_calls', { configurable: true, get: function() { abort('You are getting _emscripten_main_thread_process_queued_calls on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], '_emscripten_main_thread_process_queued_calls', { configurable: true, set: function() { abort('You are setting _emscripten_main_thread_process_queued_calls on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

      if (!Object.getOwnPropertyDescriptor(Module['ready'], 'onRuntimeInitialized')) {
        Object.defineProperty(Module['ready'], 'onRuntimeInitialized', { configurable: true, get: function() { abort('You are getting onRuntimeInitialized on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
        Object.defineProperty(Module['ready'], 'onRuntimeInitialized', { configurable: true, set: function() { abort('You are setting onRuntimeInitialized on the Promise object, instead of the instance. Use .then() to get called back with the instance, see the MODULARIZE docs in src/settings.js') } });
      }
    

// --pre-jses are emitted after the Module integration code, so that they can
// refer to Module (if they choose; they can also define Module)


// Sometimes an existing Module object exists with properties
// meant to overwrite the default module functionality. Here
// we collect those properties and reapply _after_ we configure
// the current environment's defaults to avoid having to be so
// defensive during initialization.
var moduleOverrides = {};
var key;
for (key in Module) {
  if (Module.hasOwnProperty(key)) {
    moduleOverrides[key] = Module[key];
  }
}

var arguments_ = [];
var thisProgram = './this.program';
var quit_ = function(status, toThrow) {
  throw toThrow;
};

// Determine the runtime environment we are in. You can customize this by
// setting the ENVIRONMENT setting at compile time (see settings.js).

var ENVIRONMENT_IS_WEB = false;
var ENVIRONMENT_IS_WORKER = false;
var ENVIRONMENT_IS_NODE = false;
var ENVIRONMENT_IS_SHELL = false;
ENVIRONMENT_IS_WEB = typeof window === 'object';
ENVIRONMENT_IS_WORKER = typeof importScripts === 'function';
// N.b. Electron.js environment is simultaneously a NODE-environment, but
// also a web environment.
ENVIRONMENT_IS_NODE = typeof process === 'object' && typeof process.versions === 'object' && typeof process.versions.node === 'string';
ENVIRONMENT_IS_SHELL = !ENVIRONMENT_IS_WEB && !ENVIRONMENT_IS_NODE && !ENVIRONMENT_IS_WORKER;

if (Module['ENVIRONMENT']) {
  throw new Error('Module.ENVIRONMENT has been deprecated. To force the environment, use the ENVIRONMENT compile-time option (for example, -s ENVIRONMENT=web or -s ENVIRONMENT=node)');
}



// `/` should be present at the end if `scriptDirectory` is not empty
var scriptDirectory = '';
function locateFile(path) {
  if (Module['locateFile']) {
    return Module['locateFile'](path, scriptDirectory);
  }
  return scriptDirectory + path;
}

// Hooks that are implemented differently in different runtime environments.
var read_,
    readAsync,
    readBinary,
    setWindowTitle;

var nodeFS;
var nodePath;

if (ENVIRONMENT_IS_NODE) {
  if (ENVIRONMENT_IS_WORKER) {
    scriptDirectory = require('path').dirname(scriptDirectory) + '/';
  } else {
    scriptDirectory = __dirname + '/';
  }




read_ = function shell_read(filename, binary) {
  if (!nodeFS) nodeFS = require('fs');
  if (!nodePath) nodePath = require('path');
  filename = nodePath['normalize'](filename);
  return nodeFS['readFileSync'](filename, binary ? null : 'utf8');
};

readBinary = function readBinary(filename) {
  var ret = read_(filename, true);
  if (!ret.buffer) {
    ret = new Uint8Array(ret);
  }
  assert(ret.buffer);
  return ret;
};



  if (process['argv'].length > 1) {
    thisProgram = process['argv'][1].replace(/\\/g, '/');
  }

  arguments_ = process['argv'].slice(2);

  // MODULARIZE will export the module in the proper place outside, we don't need to export here

  process['on']('uncaughtException', function(ex) {
    // suppress ExitStatus exceptions from showing an error
    if (!(ex instanceof ExitStatus)) {
      throw ex;
    }
  });

  process['on']('unhandledRejection', abort);

  quit_ = function(status) {
    process['exit'](status);
  };

  Module['inspect'] = function () { return '[Emscripten Module object]'; };



} else
if (ENVIRONMENT_IS_SHELL) {


  if (typeof read != 'undefined') {
    read_ = function shell_read(f) {
      return read(f);
    };
  }

  readBinary = function readBinary(f) {
    var data;
    if (typeof readbuffer === 'function') {
      return new Uint8Array(readbuffer(f));
    }
    data = read(f, 'binary');
    assert(typeof data === 'object');
    return data;
  };

  if (typeof scriptArgs != 'undefined') {
    arguments_ = scriptArgs;
  } else if (typeof arguments != 'undefined') {
    arguments_ = arguments;
  }

  if (typeof quit === 'function') {
    quit_ = function(status) {
      quit(status);
    };
  }

  if (typeof print !== 'undefined') {
    // Prefer to use print/printErr where they exist, as they usually work better.
    if (typeof console === 'undefined') console = /** @type{!Console} */({});
    console.log = /** @type{!function(this:Console, ...*): undefined} */ (print);
    console.warn = console.error = /** @type{!function(this:Console, ...*): undefined} */ (typeof printErr !== 'undefined' ? printErr : print);
  }


} else

// Note that this includes Node.js workers when relevant (pthreads is enabled).
// Node.js workers are detected as a combination of ENVIRONMENT_IS_WORKER and
// ENVIRONMENT_IS_NODE.
if (ENVIRONMENT_IS_WEB || ENVIRONMENT_IS_WORKER) {
  if (ENVIRONMENT_IS_WORKER) { // Check worker, not web, since window could be polyfilled
    scriptDirectory = self.location.href;
  } else if (document.currentScript) { // web
    scriptDirectory = document.currentScript.src;
  }
  // When MODULARIZE, this JS may be executed later, after document.currentScript
  // is gone, so we saved it, and we use it here instead of any other info.
  if (_scriptDir) {
    scriptDirectory = _scriptDir;
  }
  // blob urls look like blob:http://site.com/etc/etc and we cannot infer anything from them.
  // otherwise, slice off the final part of the url to find the script directory.
  // if scriptDirectory does not contain a slash, lastIndexOf will return -1,
  // and scriptDirectory will correctly be replaced with an empty string.
  if (scriptDirectory.indexOf('blob:') !== 0) {
    scriptDirectory = scriptDirectory.substr(0, scriptDirectory.lastIndexOf('/')+1);
  } else {
    scriptDirectory = '';
  }


  // Differentiate the Web Worker from the Node Worker case, as reading must
  // be done differently.
  {




  read_ = function shell_read(url) {
      var xhr = new XMLHttpRequest();
      xhr.open('GET', url, false);
      xhr.send(null);
      return xhr.responseText;
  };

  if (ENVIRONMENT_IS_WORKER) {
    readBinary = function readBinary(url) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, false);
        xhr.responseType = 'arraybuffer';
        xhr.send(null);
        return new Uint8Array(/** @type{!ArrayBuffer} */(xhr.response));
    };
  }

  readAsync = function readAsync(url, onload, onerror) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'arraybuffer';
    xhr.onload = function xhr_onload() {
      if (xhr.status == 200 || (xhr.status == 0 && xhr.response)) { // file URLs can return 0
        onload(xhr.response);
        return;
      }
      onerror();
    };
    xhr.onerror = onerror;
    xhr.send(null);
  };




  }

  setWindowTitle = function(title) { document.title = title };
} else
{
  throw new Error('environment detection error');
}


// Set up the out() and err() hooks, which are how we can print to stdout or
// stderr, respectively.
var out = Module['print'] || console.log.bind(console);
var err = Module['printErr'] || console.warn.bind(console);

// Merge back in the overrides
for (key in moduleOverrides) {
  if (moduleOverrides.hasOwnProperty(key)) {
    Module[key] = moduleOverrides[key];
  }
}
// Free the object hierarchy contained in the overrides, this lets the GC
// reclaim data used e.g. in memoryInitializerRequest, which is a large typed array.
moduleOverrides = null;

// Emit code to handle expected values on the Module object. This applies Module.x
// to the proper local x. This has two benefits: first, we only emit it if it is
// expected to arrive, and second, by using a local everywhere else that can be
// minified.
if (Module['arguments']) arguments_ = Module['arguments'];if (!Object.getOwnPropertyDescriptor(Module, 'arguments')) Object.defineProperty(Module, 'arguments', { configurable: true, get: function() { abort('Module.arguments has been replaced with plain arguments_ (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
if (Module['thisProgram']) thisProgram = Module['thisProgram'];if (!Object.getOwnPropertyDescriptor(Module, 'thisProgram')) Object.defineProperty(Module, 'thisProgram', { configurable: true, get: function() { abort('Module.thisProgram has been replaced with plain thisProgram (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
if (Module['quit']) quit_ = Module['quit'];if (!Object.getOwnPropertyDescriptor(Module, 'quit')) Object.defineProperty(Module, 'quit', { configurable: true, get: function() { abort('Module.quit has been replaced with plain quit_ (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });

// perform assertions in shell.js after we set up out() and err(), as otherwise if an assertion fails it cannot print the message
// Assertions on removed incoming Module JS APIs.
assert(typeof Module['memoryInitializerPrefixURL'] === 'undefined', 'Module.memoryInitializerPrefixURL option was removed, use Module.locateFile instead');
assert(typeof Module['pthreadMainPrefixURL'] === 'undefined', 'Module.pthreadMainPrefixURL option was removed, use Module.locateFile instead');
assert(typeof Module['cdInitializerPrefixURL'] === 'undefined', 'Module.cdInitializerPrefixURL option was removed, use Module.locateFile instead');
assert(typeof Module['filePackagePrefixURL'] === 'undefined', 'Module.filePackagePrefixURL option was removed, use Module.locateFile instead');
assert(typeof Module['read'] === 'undefined', 'Module.read option was removed (modify read_ in JS)');
assert(typeof Module['readAsync'] === 'undefined', 'Module.readAsync option was removed (modify readAsync in JS)');
assert(typeof Module['readBinary'] === 'undefined', 'Module.readBinary option was removed (modify readBinary in JS)');
assert(typeof Module['setWindowTitle'] === 'undefined', 'Module.setWindowTitle option was removed (modify setWindowTitle in JS)');
assert(typeof Module['TOTAL_MEMORY'] === 'undefined', 'Module.TOTAL_MEMORY has been renamed Module.INITIAL_MEMORY');
if (!Object.getOwnPropertyDescriptor(Module, 'read')) Object.defineProperty(Module, 'read', { configurable: true, get: function() { abort('Module.read has been replaced with plain read_ (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
if (!Object.getOwnPropertyDescriptor(Module, 'readAsync')) Object.defineProperty(Module, 'readAsync', { configurable: true, get: function() { abort('Module.readAsync has been replaced with plain readAsync (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
if (!Object.getOwnPropertyDescriptor(Module, 'readBinary')) Object.defineProperty(Module, 'readBinary', { configurable: true, get: function() { abort('Module.readBinary has been replaced with plain readBinary (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
if (!Object.getOwnPropertyDescriptor(Module, 'setWindowTitle')) Object.defineProperty(Module, 'setWindowTitle', { configurable: true, get: function() { abort('Module.setWindowTitle has been replaced with plain setWindowTitle (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
var IDBFS = 'IDBFS is no longer included by default; build with -lidbfs.js';
var PROXYFS = 'PROXYFS is no longer included by default; build with -lproxyfs.js';
var WORKERFS = 'WORKERFS is no longer included by default; build with -lworkerfs.js';
var NODEFS = 'NODEFS is no longer included by default; build with -lnodefs.js';






var STACK_ALIGN = 16;

function alignMemory(size, factor) {
  if (!factor) factor = STACK_ALIGN; // stack alignment (16-byte) by default
  return Math.ceil(size / factor) * factor;
}

function getNativeTypeSize(type) {
  switch (type) {
    case 'i1': case 'i8': return 1;
    case 'i16': return 2;
    case 'i32': return 4;
    case 'i64': return 8;
    case 'float': return 4;
    case 'double': return 8;
    default: {
      if (type[type.length-1] === '*') {
        return 4; // A pointer
      } else if (type[0] === 'i') {
        var bits = Number(type.substr(1));
        assert(bits % 8 === 0, 'getNativeTypeSize invalid bits ' + bits + ', type ' + type);
        return bits / 8;
      } else {
        return 0;
      }
    }
  }
}

function warnOnce(text) {
  if (!warnOnce.shown) warnOnce.shown = {};
  if (!warnOnce.shown[text]) {
    warnOnce.shown[text] = 1;
    err(text);
  }
}




// Wraps a JS function as a wasm function with a given signature.
function convertJsFunctionToWasm(func, sig) {

  // If the type reflection proposal is available, use the new
  // "WebAssembly.Function" constructor.
  // Otherwise, construct a minimal wasm module importing the JS function and
  // re-exporting it.
  if (typeof WebAssembly.Function === "function") {
    var typeNames = {
      'i': 'i32',
      'j': 'i64',
      'f': 'f32',
      'd': 'f64'
    };
    var type = {
      parameters: [],
      results: sig[0] == 'v' ? [] : [typeNames[sig[0]]]
    };
    for (var i = 1; i < sig.length; ++i) {
      type.parameters.push(typeNames[sig[i]]);
    }
    return new WebAssembly.Function(type, func);
  }

  // The module is static, with the exception of the type section, which is
  // generated based on the signature passed in.
  var typeSection = [
    0x01, // id: section,
    0x00, // length: 0 (placeholder)
    0x01, // count: 1
    0x60, // form: func
  ];
  var sigRet = sig.slice(0, 1);
  var sigParam = sig.slice(1);
  var typeCodes = {
    'i': 0x7f, // i32
    'j': 0x7e, // i64
    'f': 0x7d, // f32
    'd': 0x7c, // f64
  };

  // Parameters, length + signatures
  typeSection.push(sigParam.length);
  for (var i = 0; i < sigParam.length; ++i) {
    typeSection.push(typeCodes[sigParam[i]]);
  }

  // Return values, length + signatures
  // With no multi-return in MVP, either 0 (void) or 1 (anything else)
  if (sigRet == 'v') {
    typeSection.push(0x00);
  } else {
    typeSection = typeSection.concat([0x01, typeCodes[sigRet]]);
  }

  // Write the overall length of the type section back into the section header
  // (excepting the 2 bytes for the section id and length)
  typeSection[1] = typeSection.length - 2;

  // Rest of the module is static
  var bytes = new Uint8Array([
    0x00, 0x61, 0x73, 0x6d, // magic ("\0asm")
    0x01, 0x00, 0x00, 0x00, // version: 1
  ].concat(typeSection, [
    0x02, 0x07, // import section
      // (import "e" "f" (func 0 (type 0)))
      0x01, 0x01, 0x65, 0x01, 0x66, 0x00, 0x00,
    0x07, 0x05, // export section
      // (export "f" (func 0 (type 0)))
      0x01, 0x01, 0x66, 0x00, 0x00,
  ]));

   // We can compile this wasm module synchronously because it is very small.
  // This accepts an import (at "e.f"), that it reroutes to an export (at "f")
  var module = new WebAssembly.Module(bytes);
  var instance = new WebAssembly.Instance(module, {
    'e': {
      'f': func
    }
  });
  var wrappedFunc = instance.exports['f'];
  return wrappedFunc;
}

var freeTableIndexes = [];

// Weak map of functions in the table to their indexes, created on first use.
var functionsInTableMap;

// Add a wasm function to the table.
function addFunctionWasm(func, sig) {
  var table = wasmTable;

  // Check if the function is already in the table, to ensure each function
  // gets a unique index. First, create the map if this is the first use.
  if (!functionsInTableMap) {
    functionsInTableMap = new WeakMap();
    for (var i = 0; i < table.length; i++) {
      var item = table.get(i);
      // Ignore null values.
      if (item) {
        functionsInTableMap.set(item, i);
      }
    }
  }
  if (functionsInTableMap.has(func)) {
    return functionsInTableMap.get(func);
  }

  // It's not in the table, add it now.


  var ret;
  // Reuse a free index if there is one, otherwise grow.
  if (freeTableIndexes.length) {
    ret = freeTableIndexes.pop();
  } else {
    ret = table.length;
    // Grow the table
    try {
      table.grow(1);
    } catch (err) {
      if (!(err instanceof RangeError)) {
        throw err;
      }
      throw 'Unable to grow wasm table. Set ALLOW_TABLE_GROWTH.';
    }
  }

  // Set the new value.
  try {
    // Attempting to call this with JS function will cause of table.set() to fail
    table.set(ret, func);
  } catch (err) {
    if (!(err instanceof TypeError)) {
      throw err;
    }
    assert(typeof sig !== 'undefined', 'Missing signature argument to addFunction');
    var wrapped = convertJsFunctionToWasm(func, sig);
    table.set(ret, wrapped);
  }

  functionsInTableMap.set(func, ret);

  return ret;
}

function removeFunctionWasm(index) {
  functionsInTableMap.delete(wasmTable.get(index));
  freeTableIndexes.push(index);
}

// 'sig' parameter is required for the llvm backend but only when func is not
// already a WebAssembly function.
function addFunction(func, sig) {
  assert(typeof func !== 'undefined');

  return addFunctionWasm(func, sig);
}

function removeFunction(index) {
  removeFunctionWasm(index);
}









function makeBigInt(low, high, unsigned) {
  return unsigned ? ((+((low>>>0)))+((+((high>>>0)))*4294967296.0)) : ((+((low>>>0)))+((+((high|0)))*4294967296.0));
}

var tempRet0 = 0;

var setTempRet0 = function(value) {
  tempRet0 = value;
};

var getTempRet0 = function() {
  return tempRet0;
};

function getCompilerSetting(name) {
  throw 'You must build with -s RETAIN_COMPILER_SETTINGS=1 for getCompilerSetting or emscripten_get_compiler_setting to work';
}




// === Preamble library stuff ===

// Documentation for the public APIs defined in this file must be updated in:
//    site/source/docs/api_reference/preamble.js.rst
// A prebuilt local version of the documentation is available at:
//    site/build/text/docs/api_reference/preamble.js.txt
// You can also build docs locally as HTML or other formats in site/
// An online HTML version (which may be of a different version of Emscripten)
//    is up at http://kripken.github.io/emscripten-site/docs/api_reference/preamble.js.html


var wasmBinary;if (Module['wasmBinary']) wasmBinary = Module['wasmBinary'];if (!Object.getOwnPropertyDescriptor(Module, 'wasmBinary')) Object.defineProperty(Module, 'wasmBinary', { configurable: true, get: function() { abort('Module.wasmBinary has been replaced with plain wasmBinary (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });
var noExitRuntime;if (Module['noExitRuntime']) noExitRuntime = Module['noExitRuntime'];if (!Object.getOwnPropertyDescriptor(Module, 'noExitRuntime')) Object.defineProperty(Module, 'noExitRuntime', { configurable: true, get: function() { abort('Module.noExitRuntime has been replaced with plain noExitRuntime (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });


if (typeof WebAssembly !== 'object') {
  abort('no native wasm support detected');
}




// In MINIMAL_RUNTIME, setValue() and getValue() are only available when building with safe heap enabled, for heap safety checking.
// In traditional runtime, setValue() and getValue() are always available (although their use is highly discouraged due to perf penalties)

/** @param {number} ptr
    @param {number} value
    @param {string} type
    @param {number|boolean=} noSafe */
function setValue(ptr, value, type, noSafe) {
  type = type || 'i8';
  if (type.charAt(type.length-1) === '*') type = 'i32'; // pointers are 32-bit
    switch(type) {
      case 'i1': HEAP8[((ptr)>>0)]=value; break;
      case 'i8': HEAP8[((ptr)>>0)]=value; break;
      case 'i16': HEAP16[((ptr)>>1)]=value; break;
      case 'i32': HEAP32[((ptr)>>2)]=value; break;
      case 'i64': (tempI64 = [value>>>0,(tempDouble=value,(+(Math.abs(tempDouble))) >= 1.0 ? (tempDouble > 0.0 ? ((Math.min((+(Math.floor((tempDouble)/4294967296.0))), 4294967295.0))|0)>>>0 : (~~((+(Math.ceil((tempDouble - +(((~~(tempDouble)))>>>0))/4294967296.0)))))>>>0) : 0)],HEAP32[((ptr)>>2)]=tempI64[0],HEAP32[(((ptr)+(4))>>2)]=tempI64[1]); break;
      case 'float': HEAPF32[((ptr)>>2)]=value; break;
      case 'double': HEAPF64[((ptr)>>3)]=value; break;
      default: abort('invalid type for setValue: ' + type);
    }
}

/** @param {number} ptr
    @param {string} type
    @param {number|boolean=} noSafe */
function getValue(ptr, type, noSafe) {
  type = type || 'i8';
  if (type.charAt(type.length-1) === '*') type = 'i32'; // pointers are 32-bit
    switch(type) {
      case 'i1': return HEAP8[((ptr)>>0)];
      case 'i8': return HEAP8[((ptr)>>0)];
      case 'i16': return HEAP16[((ptr)>>1)];
      case 'i32': return HEAP32[((ptr)>>2)];
      case 'i64': return HEAP32[((ptr)>>2)];
      case 'float': return HEAPF32[((ptr)>>2)];
      case 'double': return HEAPF64[((ptr)>>3)];
      default: abort('invalid type for getValue: ' + type);
    }
  return null;
}






// Wasm globals

var wasmMemory;
var wasmTable;


//========================================
// Runtime essentials
//========================================

// whether we are quitting the application. no code should run after this.
// set in exit() and abort()
var ABORT = false;

// set by exit() and abort().  Passed to 'onExit' handler.
// NOTE: This is also used as the process return code code in shell environments
// but only when noExitRuntime is false.
var EXITSTATUS = 0;

/** @type {function(*, string=)} */
function assert(condition, text) {
  if (!condition) {
    abort('Assertion failed: ' + text);
  }
}

// Returns the C function with a specified identifier (for C++, you need to do manual name mangling)
function getCFunc(ident) {
  var func = Module['_' + ident]; // closure exported function
  assert(func, 'Cannot call unknown function ' + ident + ', make sure it is exported');
  return func;
}

// C calling interface.
/** @param {string|null=} returnType
    @param {Array=} argTypes
    @param {Arguments|Array=} args
    @param {Object=} opts */
function ccall(ident, returnType, argTypes, args, opts) {
  // For fast lookup of conversion functions
  var toC = {
    'string': function(str) {
      var ret = 0;
      if (str !== null && str !== undefined && str !== 0) { // null string
        // at most 4 bytes per UTF-8 code point, +1 for the trailing '\0'
        var len = (str.length << 2) + 1;
        ret = stackAlloc(len);
        stringToUTF8(str, ret, len);
      }
      return ret;
    },
    'array': function(arr) {
      var ret = stackAlloc(arr.length);
      writeArrayToMemory(arr, ret);
      return ret;
    }
  };

  function convertReturnValue(ret) {
    if (returnType === 'string') return UTF8ToString(ret);
    if (returnType === 'boolean') return Boolean(ret);
    return ret;
  }

  var func = getCFunc(ident);
  var cArgs = [];
  var stack = 0;
  assert(returnType !== 'array', 'Return type should not be "array".');
  if (args) {
    for (var i = 0; i < args.length; i++) {
      var converter = toC[argTypes[i]];
      if (converter) {
        if (stack === 0) stack = stackSave();
        cArgs[i] = converter(args[i]);
      } else {
        cArgs[i] = args[i];
      }
    }
  }
  var ret = func.apply(null, cArgs);

  ret = convertReturnValue(ret);
  if (stack !== 0) stackRestore(stack);
  return ret;
}

/** @param {string=} returnType
    @param {Array=} argTypes
    @param {Object=} opts */
function cwrap(ident, returnType, argTypes, opts) {
  return function() {
    return ccall(ident, returnType, argTypes, arguments, opts);
  }
}

// We used to include malloc/free by default in the past. Show a helpful error in
// builds with assertions.

var ALLOC_NORMAL = 0; // Tries to use _malloc()
var ALLOC_STACK = 1; // Lives for the duration of the current function call

// allocate(): This is for internal use. You can use it yourself as well, but the interface
//             is a little tricky (see docs right below). The reason is that it is optimized
//             for multiple syntaxes to save space in generated code. So you should
//             normally not use allocate(), and instead allocate memory using _malloc(),
//             initialize it with setValue(), and so forth.
// @slab: An array of data.
// @allocator: How to allocate memory, see ALLOC_*
/** @type {function((Uint8Array|Array<number>), number)} */
function allocate(slab, allocator) {
  var ret;
  assert(typeof allocator === 'number', 'allocate no longer takes a type argument')
  assert(typeof slab !== 'number', 'allocate no longer takes a number as arg0')

  if (allocator == ALLOC_STACK) {
    ret = stackAlloc(slab.length);
  } else {
    ret = _malloc(slab.length);
  }

  if (slab.subarray || slab.slice) {
    HEAPU8.set(/** @type {!Uint8Array} */(slab), ret);
  } else {
    HEAPU8.set(new Uint8Array(slab), ret);
  }
  return ret;
}




// runtime_strings.js: Strings related runtime functions that are part of both MINIMAL_RUNTIME and regular runtime.

// Given a pointer 'ptr' to a null-terminated UTF8-encoded string in the given array that contains uint8 values, returns
// a copy of that string as a Javascript String object.

var UTF8Decoder = typeof TextDecoder !== 'undefined' ? new TextDecoder('utf8') : undefined;

/**
 * @param {number} idx
 * @param {number=} maxBytesToRead
 * @return {string}
 */
function UTF8ArrayToString(heap, idx, maxBytesToRead) {
  var endIdx = idx + maxBytesToRead;
  var endPtr = idx;
  // TextDecoder needs to know the byte length in advance, it doesn't stop on null terminator by itself.
  // Also, use the length info to avoid running tiny strings through TextDecoder, since .subarray() allocates garbage.
  // (As a tiny code save trick, compare endPtr against endIdx using a negation, so that undefined means Infinity)
  while (heap[endPtr] && !(endPtr >= endIdx)) ++endPtr;

  if (endPtr - idx > 16 && heap.subarray && UTF8Decoder) {
    return UTF8Decoder.decode(heap.subarray(idx, endPtr));
  } else {
    var str = '';
    // If building with TextDecoder, we have already computed the string length above, so test loop end condition against that
    while (idx < endPtr) {
      // For UTF8 byte structure, see:
      // http://en.wikipedia.org/wiki/UTF-8#Description
      // https://www.ietf.org/rfc/rfc2279.txt
      // https://tools.ietf.org/html/rfc3629
      var u0 = heap[idx++];
      if (!(u0 & 0x80)) { str += String.fromCharCode(u0); continue; }
      var u1 = heap[idx++] & 63;
      if ((u0 & 0xE0) == 0xC0) { str += String.fromCharCode(((u0 & 31) << 6) | u1); continue; }
      var u2 = heap[idx++] & 63;
      if ((u0 & 0xF0) == 0xE0) {
        u0 = ((u0 & 15) << 12) | (u1 << 6) | u2;
      } else {
        if ((u0 & 0xF8) != 0xF0) warnOnce('Invalid UTF-8 leading byte 0x' + u0.toString(16) + ' encountered when deserializing a UTF-8 string on the asm.js/wasm heap to a JS string!');
        u0 = ((u0 & 7) << 18) | (u1 << 12) | (u2 << 6) | (heap[idx++] & 63);
      }

      if (u0 < 0x10000) {
        str += String.fromCharCode(u0);
      } else {
        var ch = u0 - 0x10000;
        str += String.fromCharCode(0xD800 | (ch >> 10), 0xDC00 | (ch & 0x3FF));
      }
    }
  }
  return str;
}

// Given a pointer 'ptr' to a null-terminated UTF8-encoded string in the emscripten HEAP, returns a
// copy of that string as a Javascript String object.
// maxBytesToRead: an optional length that specifies the maximum number of bytes to read. You can omit
//                 this parameter to scan the string until the first \0 byte. If maxBytesToRead is
//                 passed, and the string at [ptr, ptr+maxBytesToReadr[ contains a null byte in the
//                 middle, then the string will cut short at that byte index (i.e. maxBytesToRead will
//                 not produce a string of exact length [ptr, ptr+maxBytesToRead[)
//                 N.B. mixing frequent uses of UTF8ToString() with and without maxBytesToRead may
//                 throw JS JIT optimizations off, so it is worth to consider consistently using one
//                 style or the other.
/**
 * @param {number} ptr
 * @param {number=} maxBytesToRead
 * @return {string}
 */
function UTF8ToString(ptr, maxBytesToRead) {
  return ptr ? UTF8ArrayToString(HEAPU8, ptr, maxBytesToRead) : '';
}

// Copies the given Javascript String object 'str' to the given byte array at address 'outIdx',
// encoded in UTF8 form and null-terminated. The copy will require at most str.length*4+1 bytes of space in the HEAP.
// Use the function lengthBytesUTF8 to compute the exact number of bytes (excluding null terminator) that this function will write.
// Parameters:
//   str: the Javascript string to copy.
//   heap: the array to copy to. Each index in this array is assumed to be one 8-byte element.
//   outIdx: The starting offset in the array to begin the copying.
//   maxBytesToWrite: The maximum number of bytes this function can write to the array.
//                    This count should include the null terminator,
//                    i.e. if maxBytesToWrite=1, only the null terminator will be written and nothing else.
//                    maxBytesToWrite=0 does not write any bytes to the output, not even the null terminator.
// Returns the number of bytes written, EXCLUDING the null terminator.

function stringToUTF8Array(str, heap, outIdx, maxBytesToWrite) {
  if (!(maxBytesToWrite > 0)) // Parameter maxBytesToWrite is not optional. Negative values, 0, null, undefined and false each don't write out any bytes.
    return 0;

  var startIdx = outIdx;
  var endIdx = outIdx + maxBytesToWrite - 1; // -1 for string null terminator.
  for (var i = 0; i < str.length; ++i) {
    // Gotcha: charCodeAt returns a 16-bit word that is a UTF-16 encoded code unit, not a Unicode code point of the character! So decode UTF16->UTF32->UTF8.
    // See http://unicode.org/faq/utf_bom.html#utf16-3
    // For UTF8 byte structure, see http://en.wikipedia.org/wiki/UTF-8#Description and https://www.ietf.org/rfc/rfc2279.txt and https://tools.ietf.org/html/rfc3629
    var u = str.charCodeAt(i); // possibly a lead surrogate
    if (u >= 0xD800 && u <= 0xDFFF) {
      var u1 = str.charCodeAt(++i);
      u = 0x10000 + ((u & 0x3FF) << 10) | (u1 & 0x3FF);
    }
    if (u <= 0x7F) {
      if (outIdx >= endIdx) break;
      heap[outIdx++] = u;
    } else if (u <= 0x7FF) {
      if (outIdx + 1 >= endIdx) break;
      heap[outIdx++] = 0xC0 | (u >> 6);
      heap[outIdx++] = 0x80 | (u & 63);
    } else if (u <= 0xFFFF) {
      if (outIdx + 2 >= endIdx) break;
      heap[outIdx++] = 0xE0 | (u >> 12);
      heap[outIdx++] = 0x80 | ((u >> 6) & 63);
      heap[outIdx++] = 0x80 | (u & 63);
    } else {
      if (outIdx + 3 >= endIdx) break;
      if (u >= 0x200000) warnOnce('Invalid Unicode code point 0x' + u.toString(16) + ' encountered when serializing a JS string to an UTF-8 string on the asm.js/wasm heap! (Valid unicode code points should be in range 0-0x1FFFFF).');
      heap[outIdx++] = 0xF0 | (u >> 18);
      heap[outIdx++] = 0x80 | ((u >> 12) & 63);
      heap[outIdx++] = 0x80 | ((u >> 6) & 63);
      heap[outIdx++] = 0x80 | (u & 63);
    }
  }
  // Null-terminate the pointer to the buffer.
  heap[outIdx] = 0;
  return outIdx - startIdx;
}

// Copies the given Javascript String object 'str' to the emscripten HEAP at address 'outPtr',
// null-terminated and encoded in UTF8 form. The copy will require at most str.length*4+1 bytes of space in the HEAP.
// Use the function lengthBytesUTF8 to compute the exact number of bytes (excluding null terminator) that this function will write.
// Returns the number of bytes written, EXCLUDING the null terminator.

function stringToUTF8(str, outPtr, maxBytesToWrite) {
  assert(typeof maxBytesToWrite == 'number', 'stringToUTF8(str, outPtr, maxBytesToWrite) is missing the third parameter that specifies the length of the output buffer!');
  return stringToUTF8Array(str, HEAPU8,outPtr, maxBytesToWrite);
}

// Returns the number of bytes the given Javascript string takes if encoded as a UTF8 byte array, EXCLUDING the null terminator byte.
function lengthBytesUTF8(str) {
  var len = 0;
  for (var i = 0; i < str.length; ++i) {
    // Gotcha: charCodeAt returns a 16-bit word that is a UTF-16 encoded code unit, not a Unicode code point of the character! So decode UTF16->UTF32->UTF8.
    // See http://unicode.org/faq/utf_bom.html#utf16-3
    var u = str.charCodeAt(i); // possibly a lead surrogate
    if (u >= 0xD800 && u <= 0xDFFF) u = 0x10000 + ((u & 0x3FF) << 10) | (str.charCodeAt(++i) & 0x3FF);
    if (u <= 0x7F) ++len;
    else if (u <= 0x7FF) len += 2;
    else if (u <= 0xFFFF) len += 3;
    else len += 4;
  }
  return len;
}





// runtime_strings_extra.js: Strings related runtime functions that are available only in regular runtime.

// Given a pointer 'ptr' to a null-terminated ASCII-encoded string in the emscripten HEAP, returns
// a copy of that string as a Javascript String object.

function AsciiToString(ptr) {
  var str = '';
  while (1) {
    var ch = HEAPU8[((ptr++)>>0)];
    if (!ch) return str;
    str += String.fromCharCode(ch);
  }
}

// Copies the given Javascript String object 'str' to the emscripten HEAP at address 'outPtr',
// null-terminated and encoded in ASCII form. The copy will require at most str.length+1 bytes of space in the HEAP.

function stringToAscii(str, outPtr) {
  return writeAsciiToMemory(str, outPtr, false);
}

// Given a pointer 'ptr' to a null-terminated UTF16LE-encoded string in the emscripten HEAP, returns
// a copy of that string as a Javascript String object.

var UTF16Decoder = typeof TextDecoder !== 'undefined' ? new TextDecoder('utf-16le') : undefined;

function UTF16ToString(ptr, maxBytesToRead) {
  assert(ptr % 2 == 0, 'Pointer passed to UTF16ToString must be aligned to two bytes!');
  var endPtr = ptr;
  // TextDecoder needs to know the byte length in advance, it doesn't stop on null terminator by itself.
  // Also, use the length info to avoid running tiny strings through TextDecoder, since .subarray() allocates garbage.
  var idx = endPtr >> 1;
  var maxIdx = idx + maxBytesToRead / 2;
  // If maxBytesToRead is not passed explicitly, it will be undefined, and this
  // will always evaluate to true. This saves on code size.
  while (!(idx >= maxIdx) && HEAPU16[idx]) ++idx;
  endPtr = idx << 1;

  if (endPtr - ptr > 32 && UTF16Decoder) {
    return UTF16Decoder.decode(HEAPU8.subarray(ptr, endPtr));
  } else {
    var i = 0;

    var str = '';
    while (1) {
      var codeUnit = HEAP16[(((ptr)+(i*2))>>1)];
      if (codeUnit == 0 || i == maxBytesToRead / 2) return str;
      ++i;
      // fromCharCode constructs a character from a UTF-16 code unit, so we can pass the UTF16 string right through.
      str += String.fromCharCode(codeUnit);
    }
  }
}

// Copies the given Javascript String object 'str' to the emscripten HEAP at address 'outPtr',
// null-terminated and encoded in UTF16 form. The copy will require at most str.length*4+2 bytes of space in the HEAP.
// Use the function lengthBytesUTF16() to compute the exact number of bytes (excluding null terminator) that this function will write.
// Parameters:
//   str: the Javascript string to copy.
//   outPtr: Byte address in Emscripten HEAP where to write the string to.
//   maxBytesToWrite: The maximum number of bytes this function can write to the array. This count should include the null
//                    terminator, i.e. if maxBytesToWrite=2, only the null terminator will be written and nothing else.
//                    maxBytesToWrite<2 does not write any bytes to the output, not even the null terminator.
// Returns the number of bytes written, EXCLUDING the null terminator.

function stringToUTF16(str, outPtr, maxBytesToWrite) {
  assert(outPtr % 2 == 0, 'Pointer passed to stringToUTF16 must be aligned to two bytes!');
  assert(typeof maxBytesToWrite == 'number', 'stringToUTF16(str, outPtr, maxBytesToWrite) is missing the third parameter that specifies the length of the output buffer!');
  // Backwards compatibility: if max bytes is not specified, assume unsafe unbounded write is allowed.
  if (maxBytesToWrite === undefined) {
    maxBytesToWrite = 0x7FFFFFFF;
  }
  if (maxBytesToWrite < 2) return 0;
  maxBytesToWrite -= 2; // Null terminator.
  var startPtr = outPtr;
  var numCharsToWrite = (maxBytesToWrite < str.length*2) ? (maxBytesToWrite / 2) : str.length;
  for (var i = 0; i < numCharsToWrite; ++i) {
    // charCodeAt returns a UTF-16 encoded code unit, so it can be directly written to the HEAP.
    var codeUnit = str.charCodeAt(i); // possibly a lead surrogate
    HEAP16[((outPtr)>>1)]=codeUnit;
    outPtr += 2;
  }
  // Null-terminate the pointer to the HEAP.
  HEAP16[((outPtr)>>1)]=0;
  return outPtr - startPtr;
}

// Returns the number of bytes the given Javascript string takes if encoded as a UTF16 byte array, EXCLUDING the null terminator byte.

function lengthBytesUTF16(str) {
  return str.length*2;
}

function UTF32ToString(ptr, maxBytesToRead) {
  assert(ptr % 4 == 0, 'Pointer passed to UTF32ToString must be aligned to four bytes!');
  var i = 0;

  var str = '';
  // If maxBytesToRead is not passed explicitly, it will be undefined, and this
  // will always evaluate to true. This saves on code size.
  while (!(i >= maxBytesToRead / 4)) {
    var utf32 = HEAP32[(((ptr)+(i*4))>>2)];
    if (utf32 == 0) break;
    ++i;
    // Gotcha: fromCharCode constructs a character from a UTF-16 encoded code (pair), not from a Unicode code point! So encode the code point to UTF-16 for constructing.
    // See http://unicode.org/faq/utf_bom.html#utf16-3
    if (utf32 >= 0x10000) {
      var ch = utf32 - 0x10000;
      str += String.fromCharCode(0xD800 | (ch >> 10), 0xDC00 | (ch & 0x3FF));
    } else {
      str += String.fromCharCode(utf32);
    }
  }
  return str;
}

// Copies the given Javascript String object 'str' to the emscripten HEAP at address 'outPtr',
// null-terminated and encoded in UTF32 form. The copy will require at most str.length*4+4 bytes of space in the HEAP.
// Use the function lengthBytesUTF32() to compute the exact number of bytes (excluding null terminator) that this function will write.
// Parameters:
//   str: the Javascript string to copy.
//   outPtr: Byte address in Emscripten HEAP where to write the string to.
//   maxBytesToWrite: The maximum number of bytes this function can write to the array. This count should include the null
//                    terminator, i.e. if maxBytesToWrite=4, only the null terminator will be written and nothing else.
//                    maxBytesToWrite<4 does not write any bytes to the output, not even the null terminator.
// Returns the number of bytes written, EXCLUDING the null terminator.

function stringToUTF32(str, outPtr, maxBytesToWrite) {
  assert(outPtr % 4 == 0, 'Pointer passed to stringToUTF32 must be aligned to four bytes!');
  assert(typeof maxBytesToWrite == 'number', 'stringToUTF32(str, outPtr, maxBytesToWrite) is missing the third parameter that specifies the length of the output buffer!');
  // Backwards compatibility: if max bytes is not specified, assume unsafe unbounded write is allowed.
  if (maxBytesToWrite === undefined) {
    maxBytesToWrite = 0x7FFFFFFF;
  }
  if (maxBytesToWrite < 4) return 0;
  var startPtr = outPtr;
  var endPtr = startPtr + maxBytesToWrite - 4;
  for (var i = 0; i < str.length; ++i) {
    // Gotcha: charCodeAt returns a 16-bit word that is a UTF-16 encoded code unit, not a Unicode code point of the character! We must decode the string to UTF-32 to the heap.
    // See http://unicode.org/faq/utf_bom.html#utf16-3
    var codeUnit = str.charCodeAt(i); // possibly a lead surrogate
    if (codeUnit >= 0xD800 && codeUnit <= 0xDFFF) {
      var trailSurrogate = str.charCodeAt(++i);
      codeUnit = 0x10000 + ((codeUnit & 0x3FF) << 10) | (trailSurrogate & 0x3FF);
    }
    HEAP32[((outPtr)>>2)]=codeUnit;
    outPtr += 4;
    if (outPtr + 4 > endPtr) break;
  }
  // Null-terminate the pointer to the HEAP.
  HEAP32[((outPtr)>>2)]=0;
  return outPtr - startPtr;
}

// Returns the number of bytes the given Javascript string takes if encoded as a UTF16 byte array, EXCLUDING the null terminator byte.

function lengthBytesUTF32(str) {
  var len = 0;
  for (var i = 0; i < str.length; ++i) {
    // Gotcha: charCodeAt returns a 16-bit word that is a UTF-16 encoded code unit, not a Unicode code point of the character! We must decode the string to UTF-32 to the heap.
    // See http://unicode.org/faq/utf_bom.html#utf16-3
    var codeUnit = str.charCodeAt(i);
    if (codeUnit >= 0xD800 && codeUnit <= 0xDFFF) ++i; // possibly a lead surrogate, so skip over the tail surrogate.
    len += 4;
  }

  return len;
}

// Allocate heap space for a JS string, and write it there.
// It is the responsibility of the caller to free() that memory.
function allocateUTF8(str) {
  var size = lengthBytesUTF8(str) + 1;
  var ret = _malloc(size);
  if (ret) stringToUTF8Array(str, HEAP8, ret, size);
  return ret;
}

// Allocate stack space for a JS string, and write it there.
function allocateUTF8OnStack(str) {
  var size = lengthBytesUTF8(str) + 1;
  var ret = stackAlloc(size);
  stringToUTF8Array(str, HEAP8, ret, size);
  return ret;
}

// Deprecated: This function should not be called because it is unsafe and does not provide
// a maximum length limit of how many bytes it is allowed to write. Prefer calling the
// function stringToUTF8Array() instead, which takes in a maximum length that can be used
// to be secure from out of bounds writes.
/** @deprecated
    @param {boolean=} dontAddNull */
function writeStringToMemory(string, buffer, dontAddNull) {
  warnOnce('writeStringToMemory is deprecated and should not be called! Use stringToUTF8() instead!');

  var /** @type {number} */ lastChar, /** @type {number} */ end;
  if (dontAddNull) {
    // stringToUTF8Array always appends null. If we don't want to do that, remember the
    // character that existed at the location where the null will be placed, and restore
    // that after the write (below).
    end = buffer + lengthBytesUTF8(string);
    lastChar = HEAP8[end];
  }
  stringToUTF8(string, buffer, Infinity);
  if (dontAddNull) HEAP8[end] = lastChar; // Restore the value under the null character.
}

function writeArrayToMemory(array, buffer) {
  assert(array.length >= 0, 'writeArrayToMemory array must have a length (should be an array or typed array)')
  HEAP8.set(array, buffer);
}

/** @param {boolean=} dontAddNull */
function writeAsciiToMemory(str, buffer, dontAddNull) {
  for (var i = 0; i < str.length; ++i) {
    assert(str.charCodeAt(i) === str.charCodeAt(i)&0xff);
    HEAP8[((buffer++)>>0)]=str.charCodeAt(i);
  }
  // Null-terminate the pointer to the HEAP.
  if (!dontAddNull) HEAP8[((buffer)>>0)]=0;
}



// Memory management

var PAGE_SIZE = 16384;
var WASM_PAGE_SIZE = 65536;

function alignUp(x, multiple) {
  if (x % multiple > 0) {
    x += multiple - (x % multiple);
  }
  return x;
}

var HEAP,
/** @type {ArrayBuffer} */
  buffer,
/** @type {Int8Array} */
  HEAP8,
/** @type {Uint8Array} */
  HEAPU8,
/** @type {Int16Array} */
  HEAP16,
/** @type {Uint16Array} */
  HEAPU16,
/** @type {Int32Array} */
  HEAP32,
/** @type {Uint32Array} */
  HEAPU32,
/** @type {Float32Array} */
  HEAPF32,
/** @type {Float64Array} */
  HEAPF64;

function updateGlobalBufferAndViews(buf) {
  buffer = buf;
  Module['HEAP8'] = HEAP8 = new Int8Array(buf);
  Module['HEAP16'] = HEAP16 = new Int16Array(buf);
  Module['HEAP32'] = HEAP32 = new Int32Array(buf);
  Module['HEAPU8'] = HEAPU8 = new Uint8Array(buf);
  Module['HEAPU16'] = HEAPU16 = new Uint16Array(buf);
  Module['HEAPU32'] = HEAPU32 = new Uint32Array(buf);
  Module['HEAPF32'] = HEAPF32 = new Float32Array(buf);
  Module['HEAPF64'] = HEAPF64 = new Float64Array(buf);
}

var STACK_BASE = 5414336,
    STACKTOP = STACK_BASE,
    STACK_MAX = 171456;

assert(STACK_BASE % 16 === 0, 'stack must start aligned');



var TOTAL_STACK = 5242880;
if (Module['TOTAL_STACK']) assert(TOTAL_STACK === Module['TOTAL_STACK'], 'the stack size can no longer be determined at runtime')

var INITIAL_INITIAL_MEMORY = Module['INITIAL_MEMORY'] || 268435456;if (!Object.getOwnPropertyDescriptor(Module, 'INITIAL_MEMORY')) Object.defineProperty(Module, 'INITIAL_MEMORY', { configurable: true, get: function() { abort('Module.INITIAL_MEMORY has been replaced with plain INITIAL_INITIAL_MEMORY (the initial value can be provided on Module, but after startup the value is only looked for on a local variable of that name)') } });

assert(INITIAL_INITIAL_MEMORY >= TOTAL_STACK, 'INITIAL_MEMORY should be larger than TOTAL_STACK, was ' + INITIAL_INITIAL_MEMORY + '! (TOTAL_STACK=' + TOTAL_STACK + ')');

// check for full engine support (use string 'subarray' to avoid closure compiler confusion)
assert(typeof Int32Array !== 'undefined' && typeof Float64Array !== 'undefined' && Int32Array.prototype.subarray !== undefined && Int32Array.prototype.set !== undefined,
       'JS engine does not provide full typed array support');


// In non-standalone/normal mode, we create the memory here.



// Create the main memory. (Note: this isn't used in STANDALONE_WASM mode since the wasm
// memory is created in the wasm, not in JS.)

  if (Module['wasmMemory']) {
    wasmMemory = Module['wasmMemory'];
  } else
  {
    wasmMemory = new WebAssembly.Memory({
      'initial': INITIAL_INITIAL_MEMORY / WASM_PAGE_SIZE
      ,
      'maximum': INITIAL_INITIAL_MEMORY / WASM_PAGE_SIZE
    });
  }


if (wasmMemory) {
  buffer = wasmMemory.buffer;
}

// If the user provides an incorrect length, just use that length instead rather than providing the user to
// specifically provide the memory length with Module['INITIAL_MEMORY'].
INITIAL_INITIAL_MEMORY = buffer.byteLength;
assert(INITIAL_INITIAL_MEMORY % WASM_PAGE_SIZE === 0);
updateGlobalBufferAndViews(buffer);










// Initializes the stack cookie. Called at the startup of main and at the startup of each thread in pthreads mode.
function writeStackCookie() {
  assert((STACK_MAX & 3) == 0);
  // The stack grows downwards
  HEAPU32[(STACK_MAX >> 2)+1] = 0x2135467;
  HEAPU32[(STACK_MAX >> 2)+2] = 0x89BACDFE;
  // Also test the global address 0 for integrity.
  HEAP32[0] = 0x63736d65; /* 'emsc' */
}

function checkStackCookie() {
  if (ABORT) return;
  var cookie1 = HEAPU32[(STACK_MAX >> 2)+1];
  var cookie2 = HEAPU32[(STACK_MAX >> 2)+2];
  if (cookie1 != 0x2135467 || cookie2 != 0x89BACDFE) {
    abort('Stack overflow! Stack cookie has been overwritten, expected hex dwords 0x89BACDFE and 0x2135467, but received 0x' + cookie2.toString(16) + ' ' + cookie1.toString(16));
  }
  // Also test the global address 0 for integrity.
  if (HEAP32[0] !== 0x63736d65 /* 'emsc' */) abort('Runtime error: The application has corrupted its heap memory area (address zero)!');
}





// Endianness check (note: assumes compiler arch was little-endian)
(function() {
  var h16 = new Int16Array(1);
  var h8 = new Int8Array(h16.buffer);
  h16[0] = 0x6373;
  if (h8[0] !== 0x73 || h8[1] !== 0x63) throw 'Runtime error: expected the system to be little-endian!';
})();

function abortFnPtrError(ptr, sig) {
	abort("Invalid function pointer " + ptr + " called with signature '" + sig + "'. Perhaps this is an invalid value (e.g. caused by calling a virtual method on a NULL pointer)? Or calling a function with an incorrect type, which will fail? (it is worth building your source files with -Werror (warnings are errors), as warnings can indicate undefined behavior which can cause this). Build with ASSERTIONS=2 for more info.");
}



var __ATPRERUN__  = []; // functions called before the runtime is initialized
var __ATINIT__    = []; // functions called during startup
var __ATMAIN__    = []; // functions called when main() is to be run
var __ATEXIT__    = []; // functions called during shutdown
var __ATPOSTRUN__ = []; // functions called after the main() is called

var runtimeInitialized = false;
var runtimeExited = false;


function preRun() {

  if (Module['preRun']) {
    if (typeof Module['preRun'] == 'function') Module['preRun'] = [Module['preRun']];
    while (Module['preRun'].length) {
      addOnPreRun(Module['preRun'].shift());
    }
  }

  callRuntimeCallbacks(__ATPRERUN__);
}

function initRuntime() {
  checkStackCookie();
  assert(!runtimeInitialized);
  runtimeInitialized = true;
  
  callRuntimeCallbacks(__ATINIT__);
}

function preMain() {
  checkStackCookie();
  
  callRuntimeCallbacks(__ATMAIN__);
}

function exitRuntime() {
  checkStackCookie();
  runtimeExited = true;
}

function postRun() {
  checkStackCookie();

  if (Module['postRun']) {
    if (typeof Module['postRun'] == 'function') Module['postRun'] = [Module['postRun']];
    while (Module['postRun'].length) {
      addOnPostRun(Module['postRun'].shift());
    }
  }

  callRuntimeCallbacks(__ATPOSTRUN__);
}

function addOnPreRun(cb) {
  __ATPRERUN__.unshift(cb);
}

function addOnInit(cb) {
  __ATINIT__.unshift(cb);
}

function addOnPreMain(cb) {
  __ATMAIN__.unshift(cb);
}

function addOnExit(cb) {
}

function addOnPostRun(cb) {
  __ATPOSTRUN__.unshift(cb);
}




// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/imul

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/fround

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/clz32

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/trunc

assert(Math.imul, 'This browser does not support Math.imul(), build with LEGACY_VM_SUPPORT or POLYFILL_OLD_MATH_FUNCTIONS to add in a polyfill');
assert(Math.fround, 'This browser does not support Math.fround(), build with LEGACY_VM_SUPPORT or POLYFILL_OLD_MATH_FUNCTIONS to add in a polyfill');
assert(Math.clz32, 'This browser does not support Math.clz32(), build with LEGACY_VM_SUPPORT or POLYFILL_OLD_MATH_FUNCTIONS to add in a polyfill');
assert(Math.trunc, 'This browser does not support Math.trunc(), build with LEGACY_VM_SUPPORT or POLYFILL_OLD_MATH_FUNCTIONS to add in a polyfill');



// A counter of dependencies for calling run(). If we need to
// do asynchronous work before running, increment this and
// decrement it. Incrementing must happen in a place like
// Module.preRun (used by emcc to add file preloading).
// Note that you can add dependencies in preRun, even though
// it happens right before run - run will be postponed until
// the dependencies are met.
var runDependencies = 0;
var runDependencyWatcher = null;
var dependenciesFulfilled = null; // overridden to take different actions when all run dependencies are fulfilled
var runDependencyTracking = {};

function getUniqueRunDependency(id) {
  var orig = id;
  while (1) {
    if (!runDependencyTracking[id]) return id;
    id = orig + Math.random();
  }
}

function addRunDependency(id) {
  runDependencies++;

  if (Module['monitorRunDependencies']) {
    Module['monitorRunDependencies'](runDependencies);
  }

  if (id) {
    assert(!runDependencyTracking[id]);
    runDependencyTracking[id] = 1;
    if (runDependencyWatcher === null && typeof setInterval !== 'undefined') {
      // Check for missing dependencies every few seconds
      runDependencyWatcher = setInterval(function() {
        if (ABORT) {
          clearInterval(runDependencyWatcher);
          runDependencyWatcher = null;
          return;
        }
        var shown = false;
        for (var dep in runDependencyTracking) {
          if (!shown) {
            shown = true;
            err('still waiting on run dependencies:');
          }
          err('dependency: ' + dep);
        }
        if (shown) {
          err('(end of list)');
        }
      }, 10000);
    }
  } else {
    err('warning: run dependency added without ID');
  }
}

function removeRunDependency(id) {
  runDependencies--;

  if (Module['monitorRunDependencies']) {
    Module['monitorRunDependencies'](runDependencies);
  }

  if (id) {
    assert(runDependencyTracking[id]);
    delete runDependencyTracking[id];
  } else {
    err('warning: run dependency removed without ID');
  }
  if (runDependencies == 0) {
    if (runDependencyWatcher !== null) {
      clearInterval(runDependencyWatcher);
      runDependencyWatcher = null;
    }
    if (dependenciesFulfilled) {
      var callback = dependenciesFulfilled;
      dependenciesFulfilled = null;
      callback(); // can add another dependenciesFulfilled
    }
  }
}

Module["preloadedImages"] = {}; // maps url to image data
Module["preloadedAudios"] = {}; // maps url to audio data

/** @param {string|number=} what */
function abort(what) {
  if (Module['onAbort']) {
    Module['onAbort'](what);
  }

  what += '';
  err(what);

  ABORT = true;
  EXITSTATUS = 1;

  var output = 'abort(' + what + ') at ' + stackTrace();
  what = output;

  // Use a wasm runtime error, because a JS error might be seen as a foreign
  // exception, which means we'd run destructors on it. We need the error to
  // simply make the program stop.
  var e = new WebAssembly.RuntimeError(what);

  readyPromiseReject(e);
  // Throw the error whether or not MODULARIZE is set because abort is used
  // in code paths apart from instantiation where an exception is expected
  // to be thrown when abort is called.
  throw e;
}

// {{MEM_INITIALIZER}}







// show errors on likely calls to FS when it was not included
var FS = {
  error: function() {
    abort('Filesystem support (FS) was not included. The problem is that you are using files from JS, but files were not used from C/C++, so filesystem support was not auto-included. You can force-include filesystem support with  -s FORCE_FILESYSTEM=1');
  },
  init: function() { FS.error() },
  createDataFile: function() { FS.error() },
  createPreloadedFile: function() { FS.error() },
  createLazyFile: function() { FS.error() },
  open: function() { FS.error() },
  mkdev: function() { FS.error() },
  registerDevice: function() { FS.error() },
  analyzePath: function() { FS.error() },
  loadFilesFromDB: function() { FS.error() },

  ErrnoError: function ErrnoError() { FS.error() },
};
Module['FS_createDataFile'] = FS.createDataFile;
Module['FS_createPreloadedFile'] = FS.createPreloadedFile;




function hasPrefix(str, prefix) {
  return String.prototype.startsWith ?
      str.startsWith(prefix) :
      str.indexOf(prefix) === 0;
}

// Prefix of data URIs emitted by SINGLE_FILE and related options.
var dataURIPrefix = 'data:application/octet-stream;base64,';

// Indicates whether filename is a base64 data URI.
function isDataURI(filename) {
  return hasPrefix(filename, dataURIPrefix);
}

var fileURIPrefix = "file://";

// Indicates whether filename is delivered via file protocol (as opposed to http/https)
function isFileURI(filename) {
  return hasPrefix(filename, fileURIPrefix);
}



function createExportWrapper(name, fixedasm) {
  return function() {
    var displayName = name;
    var asm = fixedasm;
    if (!fixedasm) {
      asm = Module['asm'];
    }
    assert(runtimeInitialized, 'native function `' + displayName + '` called before runtime initialization');
    assert(!runtimeExited, 'native function `' + displayName + '` called after runtime exit (use NO_EXIT_RUNTIME to keep it alive after main() exits)');
    if (!asm[name]) {
      assert(asm[name], 'exported native function `' + displayName + '` not found');
    }
    return asm[name].apply(null, arguments);
  };
}


var wasmBinaryFile = 'physx-js-webidl.wasm.wasm';
if (!isDataURI(wasmBinaryFile)) {
  wasmBinaryFile = locateFile(wasmBinaryFile);
}

function getBinary() {
  try {
    if (wasmBinary) {
      return new Uint8Array(wasmBinary);
    }

    if (readBinary) {
      return readBinary(wasmBinaryFile);
    } else {
      throw "both async and sync fetching of the wasm failed";
    }
  }
  catch (err) {
    abort(err);
  }
}

function getBinaryPromise() {
  // If we don't have the binary yet, and have the Fetch api, use that;
  // in some environments, like Electron's render process, Fetch api may be present, but have a different context than expected, let's only use it on the Web
  if (!wasmBinary && (ENVIRONMENT_IS_WEB || ENVIRONMENT_IS_WORKER) && typeof fetch === 'function'
      // Let's not use fetch to get objects over file:// as it's most likely Cordova which doesn't support fetch for file://
      && !isFileURI(wasmBinaryFile)
      ) {
    return fetch(wasmBinaryFile, { credentials: 'same-origin' }).then(function(response) {
      if (!response['ok']) {
        throw "failed to load wasm binary file at '" + wasmBinaryFile + "'";
      }
      return response['arrayBuffer']();
    }).catch(function () {
      return getBinary();
    });
  }
  // Otherwise, getBinary should be able to get it synchronously
  return Promise.resolve().then(getBinary);
}



// Create the wasm instance.
// Receives the wasm imports, returns the exports.
function createWasm() {
  // prepare imports
  var info = {
    'env': asmLibraryArg,
    'wasi_snapshot_preview1': asmLibraryArg
  };
  // Load the wasm module and create an instance of using native support in the JS engine.
  // handle a generated wasm instance, receiving its exports and
  // performing other necessary setup
  /** @param {WebAssembly.Module=} module*/
  function receiveInstance(instance, module) {
    var exports = instance.exports;




    Module['asm'] = exports;

    wasmTable = Module['asm']['__indirect_function_table'];
    assert(wasmTable, "table not found in wasm exports");


    removeRunDependency('wasm-instantiate');
  }
  // we can't run yet (except in a pthread, where we have a custom sync instantiator)
  addRunDependency('wasm-instantiate');


  // Async compilation can be confusing when an error on the page overwrites Module
  // (for example, if the order of elements is wrong, and the one defining Module is
  // later), so we save Module and check it later.
  var trueModule = Module;
  function receiveInstantiatedSource(output) {
    // 'output' is a WebAssemblyInstantiatedSource object which has both the module and instance.
    // receiveInstance() will swap in the exports (to Module.asm) so they can be called
    assert(Module === trueModule, 'the Module object should not be replaced during async compilation - perhaps the order of HTML elements is wrong?');
    trueModule = null;
    // TODO: Due to Closure regression https://github.com/google/closure-compiler/issues/3193, the above line no longer optimizes out down to the following line.
    // When the regression is fixed, can restore the above USE_PTHREADS-enabled path.
    receiveInstance(output['instance']);
  }


  function instantiateArrayBuffer(receiver) {
    return getBinaryPromise().then(function(binary) {
      return WebAssembly.instantiate(binary, info);
    }).then(receiver, function(reason) {
      err('failed to asynchronously prepare wasm: ' + reason);


      abort(reason);
    });
  }

  // Prefer streaming instantiation if available.
  function instantiateAsync() {
    if (!wasmBinary &&
        typeof WebAssembly.instantiateStreaming === 'function' &&
        !isDataURI(wasmBinaryFile) &&
        // Don't use streaming for file:// delivered objects in a webview, fetch them synchronously.
        !isFileURI(wasmBinaryFile) &&
        typeof fetch === 'function') {
      fetch(wasmBinaryFile, { credentials: 'same-origin' }).then(function (response) {
        var result = WebAssembly.instantiateStreaming(response, info);
        return result.then(receiveInstantiatedSource, function(reason) {
            // We expect the most common failure cause to be a bad MIME type for the binary,
            // in which case falling back to ArrayBuffer instantiation should work.
            err('wasm streaming compile failed: ' + reason);
            err('falling back to ArrayBuffer instantiation');
            return instantiateArrayBuffer(receiveInstantiatedSource);
          });
      });
    } else {
      return instantiateArrayBuffer(receiveInstantiatedSource);
    }
  }
  // User shell pages can write their own Module.instantiateWasm = function(imports, successCallback) callback
  // to manually instantiate the Wasm module themselves. This allows pages to run the instantiation parallel
  // to any other async startup actions they are performing.
  if (Module['instantiateWasm']) {
    try {
      var exports = Module['instantiateWasm'](info, receiveInstance);
      return exports;
    } catch(e) {
      err('Module.instantiateWasm callback failed with error: ' + e);
      return false;
    }
  }

  instantiateAsync();
  return {}; // no exports yet; we'll fill them in later
}

// Globals used by JS i64 conversions
var tempDouble;
var tempI64;

// === Body ===

var ASM_CONSTS = {
  1396: function($0, $1) {var self = Module['getCache'](Module['JsPxSimulationEventCallback'])[$0]; if (!self.hasOwnProperty('cbFun')) throw 'a JSImplementation must implement all functions, you forgot JsPxSimulationEventCallback::cbFun.'; self['cbFun']($1);}
};
function array_bounds_check_error(idx,size){ throw 'Array index ' + idx + ' out of bounds: [0,' + size + ')'; }





  function abortStackOverflow(allocSize) {
      abort('Stack overflow! Attempted to allocate ' + allocSize + ' bytes on the stack, but stack has only ' + (STACK_MAX - stackSave() + allocSize) + ' bytes available!');
    }

  function callRuntimeCallbacks(callbacks) {
      while(callbacks.length > 0) {
        var callback = callbacks.shift();
        if (typeof callback == 'function') {
          callback(Module); // Pass the module as the first argument.
          continue;
        }
        var func = callback.func;
        if (typeof func === 'number') {
          if (callback.arg === undefined) {
            wasmTable.get(func)();
          } else {
            wasmTable.get(func)(callback.arg);
          }
        } else {
          func(callback.arg === undefined ? null : callback.arg);
        }
      }
    }

  function demangle(func) {
      warnOnce('warning: build with  -s DEMANGLE_SUPPORT=1  to link in libcxxabi demangling');
      return func;
    }

  function demangleAll(text) {
      var regex =
        /\b_Z[\w\d_]+/g;
      return text.replace(regex,
        function(x) {
          var y = demangle(x);
          return x === y ? x : (y + ' [' + x + ']');
        });
    }

  function dynCallLegacy(sig, ptr, args) {
      assert(('dynCall_' + sig) in Module, 'bad function pointer type - no table for sig \'' + sig + '\'');
      if (args && args.length) {
        // j (64-bit integer) must be passed in as two numbers [low 32, high 32].
        assert(args.length === sig.substring(1).replace(/j/g, '--').length);
      } else {
        assert(sig.length == 1);
      }
      if (args && args.length) {
        return Module['dynCall_' + sig].apply(null, [ptr].concat(args));
      }
      return Module['dynCall_' + sig].call(null, ptr);
    }
  function dynCall(sig, ptr, args) {
      // Without WASM_BIGINT support we cannot directly call function with i64 as
      // part of thier signature, so we rely the dynCall functions generated by
      // wasm-emscripten-finalize
      if (sig.indexOf('j') != -1) {
        return dynCallLegacy(sig, ptr, args);
      }
  
      return wasmTable.get(ptr).apply(null, args)
    }

  function jsStackTrace() {
      var error = new Error();
      if (!error.stack) {
        // IE10+ special cases: It does have callstack info, but it is only populated if an Error object is thrown,
        // so try that as a special-case.
        try {
          throw new Error();
        } catch(e) {
          error = e;
        }
        if (!error.stack) {
          return '(no stack trace available)';
        }
      }
      return error.stack.toString();
    }

  function stackTrace() {
      var js = jsStackTrace();
      if (Module['extraStackTrace']) js += '\n' + Module['extraStackTrace']();
      return demangleAll(js);
    }

  var ExceptionInfoAttrs={DESTRUCTOR_OFFSET:0,REFCOUNT_OFFSET:4,TYPE_OFFSET:8,CAUGHT_OFFSET:12,RETHROWN_OFFSET:13,SIZE:16};
  function ___cxa_allocate_exception(size) {
      // Thrown object is prepended by exception metadata block
      return _malloc(size + ExceptionInfoAttrs.SIZE) + ExceptionInfoAttrs.SIZE;
    }

  function ExceptionInfo(excPtr) {
      this.excPtr = excPtr;
      this.ptr = excPtr - ExceptionInfoAttrs.SIZE;
  
      this.set_type = function(type) {
        HEAP32[(((this.ptr)+(ExceptionInfoAttrs.TYPE_OFFSET))>>2)]=type;
      };
  
      this.get_type = function() {
        return HEAP32[(((this.ptr)+(ExceptionInfoAttrs.TYPE_OFFSET))>>2)];
      };
  
      this.set_destructor = function(destructor) {
        HEAP32[(((this.ptr)+(ExceptionInfoAttrs.DESTRUCTOR_OFFSET))>>2)]=destructor;
      };
  
      this.get_destructor = function() {
        return HEAP32[(((this.ptr)+(ExceptionInfoAttrs.DESTRUCTOR_OFFSET))>>2)];
      };
  
      this.set_refcount = function(refcount) {
        HEAP32[(((this.ptr)+(ExceptionInfoAttrs.REFCOUNT_OFFSET))>>2)]=refcount;
      };
  
      this.set_caught = function (caught) {
        caught = caught ? 1 : 0;
        HEAP8[(((this.ptr)+(ExceptionInfoAttrs.CAUGHT_OFFSET))>>0)]=caught;
      };
  
      this.get_caught = function () {
        return HEAP8[(((this.ptr)+(ExceptionInfoAttrs.CAUGHT_OFFSET))>>0)] != 0;
      };
  
      this.set_rethrown = function (rethrown) {
        rethrown = rethrown ? 1 : 0;
        HEAP8[(((this.ptr)+(ExceptionInfoAttrs.RETHROWN_OFFSET))>>0)]=rethrown;
      };
  
      this.get_rethrown = function () {
        return HEAP8[(((this.ptr)+(ExceptionInfoAttrs.RETHROWN_OFFSET))>>0)] != 0;
      };
  
      // Initialize native structure fields. Should be called once after allocated.
      this.init = function(type, destructor) {
        this.set_type(type);
        this.set_destructor(destructor);
        this.set_refcount(0);
        this.set_caught(false);
        this.set_rethrown(false);
      }
  
      this.add_ref = function() {
        var value = HEAP32[(((this.ptr)+(ExceptionInfoAttrs.REFCOUNT_OFFSET))>>2)];
        HEAP32[(((this.ptr)+(ExceptionInfoAttrs.REFCOUNT_OFFSET))>>2)]=value + 1;
      };
  
      // Returns true if last reference released.
      this.release_ref = function() {
        var prev = HEAP32[(((this.ptr)+(ExceptionInfoAttrs.REFCOUNT_OFFSET))>>2)];
        HEAP32[(((this.ptr)+(ExceptionInfoAttrs.REFCOUNT_OFFSET))>>2)]=prev - 1;
        assert(prev > 0);
        return prev === 1;
      };
    }
  
  var exceptionLast=0;
  
  function __ZSt18uncaught_exceptionv() { // std::uncaught_exception()
      return __ZSt18uncaught_exceptionv.uncaught_exceptions > 0;
    }
  function ___cxa_throw(ptr, type, destructor) {
      var info = new ExceptionInfo(ptr);
      // Initialize ExceptionInfo content after it was allocated in __cxa_allocate_exception.
      info.init(type, destructor);
      exceptionLast = ptr;
      if (!("uncaught_exception" in __ZSt18uncaught_exceptionv)) {
        __ZSt18uncaught_exceptionv.uncaught_exceptions = 1;
      } else {
        __ZSt18uncaught_exceptionv.uncaught_exceptions++;
      }
      throw ptr + " - Exception catching is disabled, this exception cannot be caught. Compile with -s DISABLE_EXCEPTION_CATCHING=0 or DISABLE_EXCEPTION_CATCHING=2 to catch.";
    }

  function _abort() {
      abort();
    }

  function _emscripten_asm_const_int(code, sigPtr, argbuf) {
      var args = readAsmConstArgs(sigPtr, argbuf);
      return ASM_CONSTS[code].apply(null, args);
    }

  function _emscripten_memcpy_big(dest, src, num) {
      HEAPU8.copyWithin(dest, src, src + num);
    }

  function _emscripten_get_heap_size() {
      return HEAPU8.length;
    }
  
  function abortOnCannotGrowMemory(requestedSize) {
      abort('Cannot enlarge memory arrays to size ' + requestedSize + ' bytes (OOM). Either (1) compile with  -s INITIAL_MEMORY=X  with X higher than the current value ' + HEAP8.length + ', (2) compile with  -s ALLOW_MEMORY_GROWTH=1  which allows increasing the size at runtime, or (3) if you want malloc to return NULL (0) instead of this abort, compile with  -s ABORTING_MALLOC=0 ');
    }
  function _emscripten_resize_heap(requestedSize) {
      requestedSize = requestedSize >>> 0;
      abortOnCannotGrowMemory(requestedSize);
    }

  function flush_NO_FILESYSTEM() {
      // flush anything remaining in the buffers during shutdown
      if (typeof _fflush !== 'undefined') _fflush(0);
      var buffers = SYSCALLS.buffers;
      if (buffers[1].length) SYSCALLS.printChar(1, 10);
      if (buffers[2].length) SYSCALLS.printChar(2, 10);
    }
  
  var SYSCALLS={mappings:{},buffers:[null,[],[]],printChar:function(stream, curr) {
        var buffer = SYSCALLS.buffers[stream];
        assert(buffer);
        if (curr === 0 || curr === 10) {
          (stream === 1 ? out : err)(UTF8ArrayToString(buffer, 0));
          buffer.length = 0;
        } else {
          buffer.push(curr);
        }
      },varargs:undefined,get:function() {
        assert(SYSCALLS.varargs != undefined);
        SYSCALLS.varargs += 4;
        var ret = HEAP32[(((SYSCALLS.varargs)-(4))>>2)];
        return ret;
      },getStr:function(ptr) {
        var ret = UTF8ToString(ptr);
        return ret;
      },get64:function(low, high) {
        if (low >= 0) assert(high === 0);
        else assert(high === -1);
        return low;
      }};
  function _fd_write(fd, iov, iovcnt, pnum) {
      // hack to support printf in SYSCALLS_REQUIRE_FILESYSTEM=0
      var num = 0;
      for (var i = 0; i < iovcnt; i++) {
        var ptr = HEAP32[(((iov)+(i*8))>>2)];
        var len = HEAP32[(((iov)+(i*8 + 4))>>2)];
        for (var j = 0; j < len; j++) {
          SYSCALLS.printChar(fd, HEAPU8[ptr+j]);
        }
        num += len;
      }
      HEAP32[((pnum)>>2)]=num
      return 0;
    }

  function _gettimeofday(ptr) {
      var now = Date.now();
      HEAP32[((ptr)>>2)]=(now/1000)|0; // seconds
      HEAP32[(((ptr)+(4))>>2)]=((now % 1000)*1000)|0; // microseconds
      return 0;
    }

  var _emscripten_get_now;if (ENVIRONMENT_IS_NODE) {
    _emscripten_get_now = function() {
      var t = process['hrtime']();
      return t[0] * 1e3 + t[1] / 1e6;
    };
  } else if (typeof dateNow !== 'undefined') {
    _emscripten_get_now = dateNow;
  } else _emscripten_get_now = function() { return performance.now(); }
  ;
  function _usleep(useconds) {
      // int usleep(useconds_t useconds);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/usleep.html
      // We're single-threaded, so use a busy loop. Super-ugly.
      var start = _emscripten_get_now();
      while (_emscripten_get_now() - start < useconds / 1000) {
        // Do nothing.
      }
    }
  
  function setErrNo(value) {
      HEAP32[((___errno_location())>>2)]=value;
      return value;
    }
  function _nanosleep(rqtp, rmtp) {
      // int nanosleep(const struct timespec  *rqtp, struct timespec *rmtp);
      if (rqtp === 0) {
        setErrNo(28);
        return -1;
      }
      var seconds = HEAP32[((rqtp)>>2)];
      var nanoseconds = HEAP32[(((rqtp)+(4))>>2)];
      if (nanoseconds < 0 || nanoseconds > 999999999 || seconds < 0) {
        setErrNo(28);
        return -1;
      }
      if (rmtp !== 0) {
        HEAP32[((rmtp)>>2)]=0;
        HEAP32[(((rmtp)+(4))>>2)]=0;
      }
      return _usleep((seconds * 1e6) + (nanoseconds / 1000));
    }

  function _pthread_attr_destroy(attr) {
      /* int pthread_attr_destroy(pthread_attr_t *attr); */
      //FIXME: should destroy the pthread_attr_t struct
      return 0;
    }

  function _pthread_attr_init(attr) {
      /* int pthread_attr_init(pthread_attr_t *attr); */
      //FIXME: should allocate a pthread_attr_t
      return 0;
    }

  function _pthread_attr_setstacksize() {}

  function _pthread_cancel() {}

  function _pthread_create() {
      return 6;
    }

  function _exit(status) {
      // void _exit(int status);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/exit.html
      exit(status);
    }
  function _pthread_exit(status) {
      _exit(status);
    }

  function _pthread_join() {}

  function _pthread_mutexattr_destroy() {}

  function _pthread_mutexattr_init() {}

  function _pthread_mutexattr_setprotocol() {}

  function _pthread_mutexattr_settype() {}

  function _setTempRet0($i) {
      setTempRet0(($i) | 0);
    }

  var readAsmConstArgsArray=[];
  function readAsmConstArgs(sigPtr, buf) {
      // Nobody should have mutated _readAsmConstArgsArray underneath us to be something else than an array.
      assert(Array.isArray(readAsmConstArgsArray));
      // The input buffer is allocated on the stack, so it must be stack-aligned.
      assert(buf % 16 == 0);
      readAsmConstArgsArray.length = 0;
      var ch;
      // Most arguments are i32s, so shift the buffer pointer so it is a plain
      // index into HEAP32.
      buf >>= 2;
      while (ch = HEAPU8[sigPtr++]) {
        assert(ch === 100/*'d'*/ || ch === 102/*'f'*/ || ch === 105 /*'i'*/);
        // A double takes two 32-bit slots, and must also be aligned - the backend
        // will emit padding to avoid that.
        var double = ch < 105;
        if (double && (buf & 1)) buf++;
        readAsmConstArgsArray.push(double ? HEAPF64[buf++ >> 1] : HEAP32[buf]);
        ++buf;
      }
      return readAsmConstArgsArray;
    }
var ASSERTIONS = true;



/** @type {function(string, boolean=, number=)} */
function intArrayFromString(stringy, dontAddNull, length) {
  var len = length > 0 ? length : lengthBytesUTF8(stringy)+1;
  var u8array = new Array(len);
  var numBytesWritten = stringToUTF8Array(stringy, u8array, 0, u8array.length);
  if (dontAddNull) u8array.length = numBytesWritten;
  return u8array;
}

function intArrayToString(array) {
  var ret = [];
  for (var i = 0; i < array.length; i++) {
    var chr = array[i];
    if (chr > 0xFF) {
      if (ASSERTIONS) {
        assert(false, 'Character code ' + chr + ' (' + String.fromCharCode(chr) + ')  at offset ' + i + ' not in 0x00-0xFF.');
      }
      chr &= 0xFF;
    }
    ret.push(String.fromCharCode(chr));
  }
  return ret.join('');
}



__ATINIT__.push({ func: function() { ___wasm_call_ctors() } });
var asmLibraryArg = {
  "__cxa_allocate_exception": ___cxa_allocate_exception,
  "__cxa_throw": ___cxa_throw,
  "abort": _abort,
  "array_bounds_check_error": array_bounds_check_error,
  "emscripten_asm_const_int": _emscripten_asm_const_int,
  "emscripten_memcpy_big": _emscripten_memcpy_big,
  "emscripten_resize_heap": _emscripten_resize_heap,
  "fd_write": _fd_write,
  "gettimeofday": _gettimeofday,
  "memory": wasmMemory,
  "nanosleep": _nanosleep,
  "pthread_attr_destroy": _pthread_attr_destroy,
  "pthread_attr_init": _pthread_attr_init,
  "pthread_attr_setstacksize": _pthread_attr_setstacksize,
  "pthread_cancel": _pthread_cancel,
  "pthread_create": _pthread_create,
  "pthread_exit": _pthread_exit,
  "pthread_join": _pthread_join,
  "pthread_mutexattr_destroy": _pthread_mutexattr_destroy,
  "pthread_mutexattr_init": _pthread_mutexattr_init,
  "pthread_mutexattr_setprotocol": _pthread_mutexattr_setprotocol,
  "pthread_mutexattr_settype": _pthread_mutexattr_settype,
  "setTempRet0": _setTempRet0
};
var asm = createWasm();
/** @type {function(...*):?} */
var ___wasm_call_ctors = Module["___wasm_call_ctors"] = createExportWrapper("__wasm_call_ctors");

/** @type {function(...*):?} */
var ___em_js__array_bounds_check_error = Module["___em_js__array_bounds_check_error"] = createExportWrapper("__em_js__array_bounds_check_error");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_release_0 = Module["_emscripten_bind_PxBase_release_0"] = createExportWrapper("emscripten_bind_PxBase_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_getConcreteTypeName_0 = Module["_emscripten_bind_PxBase_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxBase_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_getConcreteType_0 = Module["_emscripten_bind_PxBase_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxBase_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_setBaseFlag_2 = Module["_emscripten_bind_PxBase_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxBase_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_setBaseFlags_1 = Module["_emscripten_bind_PxBase_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxBase_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_getBaseFlags_0 = Module["_emscripten_bind_PxBase_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxBase_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBase_isReleasable_0 = Module["_emscripten_bind_PxBase_isReleasable_0"] = createExportWrapper("emscripten_bind_PxBase_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getType_0 = Module["_emscripten_bind_PxActor_getType_0"] = createExportWrapper("emscripten_bind_PxActor_getType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getScene_0 = Module["_emscripten_bind_PxActor_getScene_0"] = createExportWrapper("emscripten_bind_PxActor_getScene_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_setName_1 = Module["_emscripten_bind_PxActor_setName_1"] = createExportWrapper("emscripten_bind_PxActor_setName_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getName_0 = Module["_emscripten_bind_PxActor_getName_0"] = createExportWrapper("emscripten_bind_PxActor_getName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getWorldBounds_0 = Module["_emscripten_bind_PxActor_getWorldBounds_0"] = createExportWrapper("emscripten_bind_PxActor_getWorldBounds_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getWorldBounds_1 = Module["_emscripten_bind_PxActor_getWorldBounds_1"] = createExportWrapper("emscripten_bind_PxActor_getWorldBounds_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_setActorFlags_1 = Module["_emscripten_bind_PxActor_setActorFlags_1"] = createExportWrapper("emscripten_bind_PxActor_setActorFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getActorFlags_0 = Module["_emscripten_bind_PxActor_getActorFlags_0"] = createExportWrapper("emscripten_bind_PxActor_getActorFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_setDominanceGroup_1 = Module["_emscripten_bind_PxActor_setDominanceGroup_1"] = createExportWrapper("emscripten_bind_PxActor_setDominanceGroup_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getDominanceGroup_0 = Module["_emscripten_bind_PxActor_getDominanceGroup_0"] = createExportWrapper("emscripten_bind_PxActor_getDominanceGroup_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_setOwnerClient_1 = Module["_emscripten_bind_PxActor_setOwnerClient_1"] = createExportWrapper("emscripten_bind_PxActor_setOwnerClient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getOwnerClient_0 = Module["_emscripten_bind_PxActor_getOwnerClient_0"] = createExportWrapper("emscripten_bind_PxActor_getOwnerClient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_release_0 = Module["_emscripten_bind_PxActor_release_0"] = createExportWrapper("emscripten_bind_PxActor_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getConcreteTypeName_0 = Module["_emscripten_bind_PxActor_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxActor_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getConcreteType_0 = Module["_emscripten_bind_PxActor_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxActor_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_setBaseFlag_2 = Module["_emscripten_bind_PxActor_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxActor_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_setBaseFlags_1 = Module["_emscripten_bind_PxActor_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxActor_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_getBaseFlags_0 = Module["_emscripten_bind_PxActor_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxActor_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActor_isReleasable_0 = Module["_emscripten_bind_PxActor_isReleasable_0"] = createExportWrapper("emscripten_bind_PxActor_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorShape_get_actor_0 = Module["_emscripten_bind_PxActorShape_get_actor_0"] = createExportWrapper("emscripten_bind_PxActorShape_get_actor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorShape_set_actor_1 = Module["_emscripten_bind_PxActorShape_set_actor_1"] = createExportWrapper("emscripten_bind_PxActorShape_set_actor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorShape_get_shape_0 = Module["_emscripten_bind_PxActorShape_get_shape_0"] = createExportWrapper("emscripten_bind_PxActorShape_get_shape_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorShape_set_shape_1 = Module["_emscripten_bind_PxActorShape_set_shape_1"] = createExportWrapper("emscripten_bind_PxActorShape_set_shape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorShape___destroy___0 = Module["_emscripten_bind_PxActorShape___destroy___0"] = createExportWrapper("emscripten_bind_PxActorShape___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit_get_faceIndex_0 = Module["_emscripten_bind_PxQueryHit_get_faceIndex_0"] = createExportWrapper("emscripten_bind_PxQueryHit_get_faceIndex_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit_set_faceIndex_1 = Module["_emscripten_bind_PxQueryHit_set_faceIndex_1"] = createExportWrapper("emscripten_bind_PxQueryHit_set_faceIndex_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit_get_actor_0 = Module["_emscripten_bind_PxQueryHit_get_actor_0"] = createExportWrapper("emscripten_bind_PxQueryHit_get_actor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit_set_actor_1 = Module["_emscripten_bind_PxQueryHit_set_actor_1"] = createExportWrapper("emscripten_bind_PxQueryHit_set_actor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit_get_shape_0 = Module["_emscripten_bind_PxQueryHit_get_shape_0"] = createExportWrapper("emscripten_bind_PxQueryHit_get_shape_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit_set_shape_1 = Module["_emscripten_bind_PxQueryHit_set_shape_1"] = createExportWrapper("emscripten_bind_PxQueryHit_set_shape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQueryHit___destroy___0 = Module["_emscripten_bind_PxQueryHit___destroy___0"] = createExportWrapper("emscripten_bind_PxQueryHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getGlobalPose_0 = Module["_emscripten_bind_PxRigidActor_getGlobalPose_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getGlobalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setGlobalPose_1 = Module["_emscripten_bind_PxRigidActor_setGlobalPose_1"] = createExportWrapper("emscripten_bind_PxRigidActor_setGlobalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setGlobalPose_2 = Module["_emscripten_bind_PxRigidActor_setGlobalPose_2"] = createExportWrapper("emscripten_bind_PxRigidActor_setGlobalPose_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_attachShape_1 = Module["_emscripten_bind_PxRigidActor_attachShape_1"] = createExportWrapper("emscripten_bind_PxRigidActor_attachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_detachShape_1 = Module["_emscripten_bind_PxRigidActor_detachShape_1"] = createExportWrapper("emscripten_bind_PxRigidActor_detachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_detachShape_2 = Module["_emscripten_bind_PxRigidActor_detachShape_2"] = createExportWrapper("emscripten_bind_PxRigidActor_detachShape_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getNbShapes_0 = Module["_emscripten_bind_PxRigidActor_getNbShapes_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getNbShapes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getShapes_3 = Module["_emscripten_bind_PxRigidActor_getShapes_3"] = createExportWrapper("emscripten_bind_PxRigidActor_getShapes_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getType_0 = Module["_emscripten_bind_PxRigidActor_getType_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getScene_0 = Module["_emscripten_bind_PxRigidActor_getScene_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getScene_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setName_1 = Module["_emscripten_bind_PxRigidActor_setName_1"] = createExportWrapper("emscripten_bind_PxRigidActor_setName_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getName_0 = Module["_emscripten_bind_PxRigidActor_getName_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getWorldBounds_0 = Module["_emscripten_bind_PxRigidActor_getWorldBounds_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getWorldBounds_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getWorldBounds_1 = Module["_emscripten_bind_PxRigidActor_getWorldBounds_1"] = createExportWrapper("emscripten_bind_PxRigidActor_getWorldBounds_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setActorFlags_1 = Module["_emscripten_bind_PxRigidActor_setActorFlags_1"] = createExportWrapper("emscripten_bind_PxRigidActor_setActorFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getActorFlags_0 = Module["_emscripten_bind_PxRigidActor_getActorFlags_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getActorFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setDominanceGroup_1 = Module["_emscripten_bind_PxRigidActor_setDominanceGroup_1"] = createExportWrapper("emscripten_bind_PxRigidActor_setDominanceGroup_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getDominanceGroup_0 = Module["_emscripten_bind_PxRigidActor_getDominanceGroup_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getDominanceGroup_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setOwnerClient_1 = Module["_emscripten_bind_PxRigidActor_setOwnerClient_1"] = createExportWrapper("emscripten_bind_PxRigidActor_setOwnerClient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getOwnerClient_0 = Module["_emscripten_bind_PxRigidActor_getOwnerClient_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getOwnerClient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_release_0 = Module["_emscripten_bind_PxRigidActor_release_0"] = createExportWrapper("emscripten_bind_PxRigidActor_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getConcreteTypeName_0 = Module["_emscripten_bind_PxRigidActor_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getConcreteType_0 = Module["_emscripten_bind_PxRigidActor_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setBaseFlag_2 = Module["_emscripten_bind_PxRigidActor_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxRigidActor_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_setBaseFlags_1 = Module["_emscripten_bind_PxRigidActor_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxRigidActor_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_getBaseFlags_0 = Module["_emscripten_bind_PxRigidActor_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxRigidActor_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidActor_isReleasable_0 = Module["_emscripten_bind_PxRigidActor_isReleasable_0"] = createExportWrapper("emscripten_bind_PxRigidActor_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSimulationEventCallback___destroy___0 = Module["_emscripten_bind_PxSimulationEventCallback___destroy___0"] = createExportWrapper("emscripten_bind_PxSimulationEventCallback___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_getVehicleType_0 = Module["_emscripten_bind_PxVehicleWheels_getVehicleType_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_getVehicleType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_getRigidDynamicActor_0 = Module["_emscripten_bind_PxVehicleWheels_getRigidDynamicActor_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_getRigidDynamicActor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_computeForwardSpeed_0 = Module["_emscripten_bind_PxVehicleWheels_computeForwardSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_computeForwardSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_computeSidewaysSpeed_0 = Module["_emscripten_bind_PxVehicleWheels_computeSidewaysSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_computeSidewaysSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_getNbNonDrivenWheels_0 = Module["_emscripten_bind_PxVehicleWheels_getNbNonDrivenWheels_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_getNbNonDrivenWheels_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_release_0 = Module["_emscripten_bind_PxVehicleWheels_release_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_getConcreteTypeName_0 = Module["_emscripten_bind_PxVehicleWheels_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_getConcreteType_0 = Module["_emscripten_bind_PxVehicleWheels_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_setBaseFlag_2 = Module["_emscripten_bind_PxVehicleWheels_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxVehicleWheels_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_setBaseFlags_1 = Module["_emscripten_bind_PxVehicleWheels_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxVehicleWheels_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_getBaseFlags_0 = Module["_emscripten_bind_PxVehicleWheels_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_isReleasable_0 = Module["_emscripten_bind_PxVehicleWheels_isReleasable_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_get_mWheelsSimData_0 = Module["_emscripten_bind_PxVehicleWheels_get_mWheelsSimData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_get_mWheelsSimData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_set_mWheelsSimData_1 = Module["_emscripten_bind_PxVehicleWheels_set_mWheelsSimData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheels_set_mWheelsSimData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_get_mWheelsDynData_0 = Module["_emscripten_bind_PxVehicleWheels_get_mWheelsDynData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheels_get_mWheelsDynData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheels_set_mWheelsDynData_1 = Module["_emscripten_bind_PxVehicleWheels_set_mWheelsDynData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheels_set_mWheelsDynData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_flags_0 = Module["_emscripten_bind_PxLocationHit_get_flags_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_flags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_flags_1 = Module["_emscripten_bind_PxLocationHit_set_flags_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_flags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_position_0 = Module["_emscripten_bind_PxLocationHit_get_position_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_position_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_position_1 = Module["_emscripten_bind_PxLocationHit_set_position_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_position_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_normal_0 = Module["_emscripten_bind_PxLocationHit_get_normal_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_normal_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_normal_1 = Module["_emscripten_bind_PxLocationHit_set_normal_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_normal_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_distance_0 = Module["_emscripten_bind_PxLocationHit_get_distance_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_distance_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_distance_1 = Module["_emscripten_bind_PxLocationHit_set_distance_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_distance_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_actor_0 = Module["_emscripten_bind_PxLocationHit_get_actor_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_actor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_actor_1 = Module["_emscripten_bind_PxLocationHit_set_actor_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_actor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_shape_0 = Module["_emscripten_bind_PxLocationHit_get_shape_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_shape_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_shape_1 = Module["_emscripten_bind_PxLocationHit_set_shape_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_shape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_get_faceIndex_0 = Module["_emscripten_bind_PxLocationHit_get_faceIndex_0"] = createExportWrapper("emscripten_bind_PxLocationHit_get_faceIndex_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit_set_faceIndex_1 = Module["_emscripten_bind_PxLocationHit_set_faceIndex_1"] = createExportWrapper("emscripten_bind_PxLocationHit_set_faceIndex_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxLocationHit___destroy___0 = Module["_emscripten_bind_PxLocationHit___destroy___0"] = createExportWrapper("emscripten_bind_PxLocationHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setCMassLocalPose_1 = Module["_emscripten_bind_PxRigidBody_setCMassLocalPose_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setCMassLocalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getCMassLocalPose_0 = Module["_emscripten_bind_PxRigidBody_getCMassLocalPose_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getCMassLocalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMass_1 = Module["_emscripten_bind_PxRigidBody_setMass_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMass_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMass_0 = Module["_emscripten_bind_PxRigidBody_getMass_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getInvMass_0 = Module["_emscripten_bind_PxRigidBody_getInvMass_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getInvMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMassSpaceInertiaTensor_1 = Module["_emscripten_bind_PxRigidBody_setMassSpaceInertiaTensor_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMassSpaceInertiaTensor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMassSpaceInertiaTensor_0 = Module["_emscripten_bind_PxRigidBody_getMassSpaceInertiaTensor_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMassSpaceInertiaTensor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMassSpaceInvInertiaTensor_0 = Module["_emscripten_bind_PxRigidBody_getMassSpaceInvInertiaTensor_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMassSpaceInvInertiaTensor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setLinearDamping_1 = Module["_emscripten_bind_PxRigidBody_setLinearDamping_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setLinearDamping_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getLinearDamping_0 = Module["_emscripten_bind_PxRigidBody_getLinearDamping_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getLinearDamping_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setAngularDamping_1 = Module["_emscripten_bind_PxRigidBody_setAngularDamping_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setAngularDamping_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getAngularDamping_0 = Module["_emscripten_bind_PxRigidBody_getAngularDamping_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getAngularDamping_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getLinearVelocity_0 = Module["_emscripten_bind_PxRigidBody_getLinearVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getLinearVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setLinearVelocity_1 = Module["_emscripten_bind_PxRigidBody_setLinearVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setLinearVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setLinearVelocity_2 = Module["_emscripten_bind_PxRigidBody_setLinearVelocity_2"] = createExportWrapper("emscripten_bind_PxRigidBody_setLinearVelocity_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getAngularVelocity_0 = Module["_emscripten_bind_PxRigidBody_getAngularVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getAngularVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setAngularVelocity_1 = Module["_emscripten_bind_PxRigidBody_setAngularVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setAngularVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setAngularVelocity_2 = Module["_emscripten_bind_PxRigidBody_setAngularVelocity_2"] = createExportWrapper("emscripten_bind_PxRigidBody_setAngularVelocity_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMaxLinearVelocity_0 = Module["_emscripten_bind_PxRigidBody_getMaxLinearVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMaxLinearVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMaxLinearVelocity_1 = Module["_emscripten_bind_PxRigidBody_setMaxLinearVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMaxLinearVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMaxAngularVelocity_0 = Module["_emscripten_bind_PxRigidBody_getMaxAngularVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMaxAngularVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMaxAngularVelocity_1 = Module["_emscripten_bind_PxRigidBody_setMaxAngularVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMaxAngularVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_addForce_1 = Module["_emscripten_bind_PxRigidBody_addForce_1"] = createExportWrapper("emscripten_bind_PxRigidBody_addForce_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_addForce_2 = Module["_emscripten_bind_PxRigidBody_addForce_2"] = createExportWrapper("emscripten_bind_PxRigidBody_addForce_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_addForce_3 = Module["_emscripten_bind_PxRigidBody_addForce_3"] = createExportWrapper("emscripten_bind_PxRigidBody_addForce_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_addTorque_1 = Module["_emscripten_bind_PxRigidBody_addTorque_1"] = createExportWrapper("emscripten_bind_PxRigidBody_addTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_addTorque_2 = Module["_emscripten_bind_PxRigidBody_addTorque_2"] = createExportWrapper("emscripten_bind_PxRigidBody_addTorque_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_addTorque_3 = Module["_emscripten_bind_PxRigidBody_addTorque_3"] = createExportWrapper("emscripten_bind_PxRigidBody_addTorque_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_clearForce_1 = Module["_emscripten_bind_PxRigidBody_clearForce_1"] = createExportWrapper("emscripten_bind_PxRigidBody_clearForce_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_clearTorque_1 = Module["_emscripten_bind_PxRigidBody_clearTorque_1"] = createExportWrapper("emscripten_bind_PxRigidBody_clearTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setForceAndTorque_2 = Module["_emscripten_bind_PxRigidBody_setForceAndTorque_2"] = createExportWrapper("emscripten_bind_PxRigidBody_setForceAndTorque_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setForceAndTorque_3 = Module["_emscripten_bind_PxRigidBody_setForceAndTorque_3"] = createExportWrapper("emscripten_bind_PxRigidBody_setForceAndTorque_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setRigidBodyFlag_2 = Module["_emscripten_bind_PxRigidBody_setRigidBodyFlag_2"] = createExportWrapper("emscripten_bind_PxRigidBody_setRigidBodyFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setRigidBodyFlags_1 = Module["_emscripten_bind_PxRigidBody_setRigidBodyFlags_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setRigidBodyFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getRigidBodyFlags_0 = Module["_emscripten_bind_PxRigidBody_getRigidBodyFlags_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getRigidBodyFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMinCCDAdvanceCoefficient_1 = Module["_emscripten_bind_PxRigidBody_setMinCCDAdvanceCoefficient_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMinCCDAdvanceCoefficient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMinCCDAdvanceCoefficient_0 = Module["_emscripten_bind_PxRigidBody_getMinCCDAdvanceCoefficient_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMinCCDAdvanceCoefficient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMaxDepenetrationVelocity_1 = Module["_emscripten_bind_PxRigidBody_setMaxDepenetrationVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMaxDepenetrationVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMaxDepenetrationVelocity_0 = Module["_emscripten_bind_PxRigidBody_getMaxDepenetrationVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMaxDepenetrationVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setMaxContactImpulse_1 = Module["_emscripten_bind_PxRigidBody_setMaxContactImpulse_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setMaxContactImpulse_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getMaxContactImpulse_0 = Module["_emscripten_bind_PxRigidBody_getMaxContactImpulse_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getMaxContactImpulse_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getInternalIslandNodeIndex_0 = Module["_emscripten_bind_PxRigidBody_getInternalIslandNodeIndex_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getInternalIslandNodeIndex_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getType_0 = Module["_emscripten_bind_PxRigidBody_getType_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getScene_0 = Module["_emscripten_bind_PxRigidBody_getScene_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getScene_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setName_1 = Module["_emscripten_bind_PxRigidBody_setName_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setName_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getName_0 = Module["_emscripten_bind_PxRigidBody_getName_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getWorldBounds_0 = Module["_emscripten_bind_PxRigidBody_getWorldBounds_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getWorldBounds_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getWorldBounds_1 = Module["_emscripten_bind_PxRigidBody_getWorldBounds_1"] = createExportWrapper("emscripten_bind_PxRigidBody_getWorldBounds_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setActorFlags_1 = Module["_emscripten_bind_PxRigidBody_setActorFlags_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setActorFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getActorFlags_0 = Module["_emscripten_bind_PxRigidBody_getActorFlags_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getActorFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setDominanceGroup_1 = Module["_emscripten_bind_PxRigidBody_setDominanceGroup_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setDominanceGroup_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getDominanceGroup_0 = Module["_emscripten_bind_PxRigidBody_getDominanceGroup_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getDominanceGroup_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setOwnerClient_1 = Module["_emscripten_bind_PxRigidBody_setOwnerClient_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setOwnerClient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getOwnerClient_0 = Module["_emscripten_bind_PxRigidBody_getOwnerClient_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getOwnerClient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_release_0 = Module["_emscripten_bind_PxRigidBody_release_0"] = createExportWrapper("emscripten_bind_PxRigidBody_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getConcreteTypeName_0 = Module["_emscripten_bind_PxRigidBody_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getConcreteType_0 = Module["_emscripten_bind_PxRigidBody_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setBaseFlag_2 = Module["_emscripten_bind_PxRigidBody_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxRigidBody_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setBaseFlags_1 = Module["_emscripten_bind_PxRigidBody_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getBaseFlags_0 = Module["_emscripten_bind_PxRigidBody_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_isReleasable_0 = Module["_emscripten_bind_PxRigidBody_isReleasable_0"] = createExportWrapper("emscripten_bind_PxRigidBody_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getGlobalPose_0 = Module["_emscripten_bind_PxRigidBody_getGlobalPose_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getGlobalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setGlobalPose_1 = Module["_emscripten_bind_PxRigidBody_setGlobalPose_1"] = createExportWrapper("emscripten_bind_PxRigidBody_setGlobalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_setGlobalPose_2 = Module["_emscripten_bind_PxRigidBody_setGlobalPose_2"] = createExportWrapper("emscripten_bind_PxRigidBody_setGlobalPose_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_attachShape_1 = Module["_emscripten_bind_PxRigidBody_attachShape_1"] = createExportWrapper("emscripten_bind_PxRigidBody_attachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_detachShape_1 = Module["_emscripten_bind_PxRigidBody_detachShape_1"] = createExportWrapper("emscripten_bind_PxRigidBody_detachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_detachShape_2 = Module["_emscripten_bind_PxRigidBody_detachShape_2"] = createExportWrapper("emscripten_bind_PxRigidBody_detachShape_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getNbShapes_0 = Module["_emscripten_bind_PxRigidBody_getNbShapes_0"] = createExportWrapper("emscripten_bind_PxRigidBody_getNbShapes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBody_getShapes_3 = Module["_emscripten_bind_PxRigidBody_getShapes_3"] = createExportWrapper("emscripten_bind_PxRigidBody_getShapes_3");

/** @type {function(...*):?} */
var _emscripten_bind_SimplePxSimulationEventCallback_cbFun_1 = Module["_emscripten_bind_SimplePxSimulationEventCallback_cbFun_1"] = createExportWrapper("emscripten_bind_SimplePxSimulationEventCallback_cbFun_1");

/** @type {function(...*):?} */
var _emscripten_bind_SimplePxSimulationEventCallback___destroy___0 = Module["_emscripten_bind_SimplePxSimulationEventCallback___destroy___0"] = createExportWrapper("emscripten_bind_SimplePxSimulationEventCallback___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_release_0 = Module["_emscripten_bind_PxVehicleDrive_release_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_getConcreteTypeName_0 = Module["_emscripten_bind_PxVehicleDrive_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_getConcreteType_0 = Module["_emscripten_bind_PxVehicleDrive_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_setBaseFlag_2 = Module["_emscripten_bind_PxVehicleDrive_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxVehicleDrive_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_setBaseFlags_1 = Module["_emscripten_bind_PxVehicleDrive_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_getBaseFlags_0 = Module["_emscripten_bind_PxVehicleDrive_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_isReleasable_0 = Module["_emscripten_bind_PxVehicleDrive_isReleasable_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_getVehicleType_0 = Module["_emscripten_bind_PxVehicleDrive_getVehicleType_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_getVehicleType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_getRigidDynamicActor_0 = Module["_emscripten_bind_PxVehicleDrive_getRigidDynamicActor_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_getRigidDynamicActor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_computeForwardSpeed_0 = Module["_emscripten_bind_PxVehicleDrive_computeForwardSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_computeForwardSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_computeSidewaysSpeed_0 = Module["_emscripten_bind_PxVehicleDrive_computeSidewaysSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_computeSidewaysSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_getNbNonDrivenWheels_0 = Module["_emscripten_bind_PxVehicleDrive_getNbNonDrivenWheels_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_getNbNonDrivenWheels_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_get_mDriveDynData_0 = Module["_emscripten_bind_PxVehicleDrive_get_mDriveDynData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_get_mDriveDynData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_set_mDriveDynData_1 = Module["_emscripten_bind_PxVehicleDrive_set_mDriveDynData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive_set_mDriveDynData_1");

/** @type {function(...*):?} */
var _memcpy = Module["_memcpy"] = createExportWrapper("memcpy");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_get_mWheelsSimData_0 = Module["_emscripten_bind_PxVehicleDrive_get_mWheelsSimData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_get_mWheelsSimData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_set_mWheelsSimData_1 = Module["_emscripten_bind_PxVehicleDrive_set_mWheelsSimData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive_set_mWheelsSimData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_get_mWheelsDynData_0 = Module["_emscripten_bind_PxVehicleDrive_get_mWheelsDynData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive_get_mWheelsDynData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive_set_mWheelsDynData_1 = Module["_emscripten_bind_PxVehicleDrive_set_mWheelsDynData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive_set_mWheelsDynData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_PxVehicleDriveSimData_0 = Module["_emscripten_bind_PxVehicleDriveSimData_PxVehicleDriveSimData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_PxVehicleDriveSimData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_getEngineData_0 = Module["_emscripten_bind_PxVehicleDriveSimData_getEngineData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_getEngineData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_setEngineData_1 = Module["_emscripten_bind_PxVehicleDriveSimData_setEngineData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_setEngineData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_getGearsData_0 = Module["_emscripten_bind_PxVehicleDriveSimData_getGearsData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_getGearsData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_setGearsData_1 = Module["_emscripten_bind_PxVehicleDriveSimData_setGearsData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_setGearsData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_getClutchData_0 = Module["_emscripten_bind_PxVehicleDriveSimData_getClutchData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_getClutchData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_setClutchData_1 = Module["_emscripten_bind_PxVehicleDriveSimData_setClutchData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_setClutchData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_getAutoBoxData_0 = Module["_emscripten_bind_PxVehicleDriveSimData_getAutoBoxData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_getAutoBoxData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData_setAutoBoxData_1 = Module["_emscripten_bind_PxVehicleDriveSimData_setAutoBoxData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData_setAutoBoxData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData___destroy___0 = Module["_emscripten_bind_PxVehicleDriveSimData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxGeometry___destroy___0 = Module["_emscripten_bind_PxGeometry___destroy___0"] = createExportWrapper("emscripten_bind_PxGeometry___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxCpuDispatcher___destroy___0 = Module["_emscripten_bind_PxCpuDispatcher___destroy___0"] = createExportWrapper("emscripten_bind_PxCpuDispatcher___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_release_0 = Module["_emscripten_bind_PxJoint_release_0"] = createExportWrapper("emscripten_bind_PxJoint_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_getConcreteTypeName_0 = Module["_emscripten_bind_PxJoint_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxJoint_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_getConcreteType_0 = Module["_emscripten_bind_PxJoint_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxJoint_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_setBaseFlag_2 = Module["_emscripten_bind_PxJoint_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxJoint_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_setBaseFlags_1 = Module["_emscripten_bind_PxJoint_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxJoint_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_getBaseFlags_0 = Module["_emscripten_bind_PxJoint_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxJoint_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxJoint_isReleasable_0 = Module["_emscripten_bind_PxJoint_isReleasable_0"] = createExportWrapper("emscripten_bind_PxJoint_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_VoidPtr___destroy___0 = Module["_emscripten_bind_VoidPtr___destroy___0"] = createExportWrapper("emscripten_bind_VoidPtr___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_DefaultFilterShader_0 = Module["_emscripten_bind_PxTopLevelFunctions_DefaultFilterShader_0"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_DefaultFilterShader_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPreFilterBlocking_0 = Module["_emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPreFilterBlocking_0"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPreFilterBlocking_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPostFilterBlocking_0 = Module["_emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPostFilterBlocking_0"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPostFilterBlocking_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_CreateCooking_3 = Module["_emscripten_bind_PxTopLevelFunctions_CreateCooking_3"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_CreateCooking_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_CreateFoundation_3 = Module["_emscripten_bind_PxTopLevelFunctions_CreateFoundation_3"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_CreateFoundation_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_CreatePhysics_3 = Module["_emscripten_bind_PxTopLevelFunctions_CreatePhysics_3"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_CreatePhysics_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_DefaultCpuDispatcherCreate_1 = Module["_emscripten_bind_PxTopLevelFunctions_DefaultCpuDispatcherCreate_1"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_DefaultCpuDispatcherCreate_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_InitExtensions_1 = Module["_emscripten_bind_PxTopLevelFunctions_InitExtensions_1"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_InitExtensions_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_RevoluteJointCreate_5 = Module["_emscripten_bind_PxTopLevelFunctions_RevoluteJointCreate_5"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_RevoluteJointCreate_5");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_getU8At_2 = Module["_emscripten_bind_PxTopLevelFunctions_getU8At_2"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_getU8At_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_getVec3At_2 = Module["_emscripten_bind_PxTopLevelFunctions_getVec3At_2"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_getVec3At_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions_get_PHYSICS_VERSION_0 = Module["_emscripten_bind_PxTopLevelFunctions_get_PHYSICS_VERSION_0"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions_get_PHYSICS_VERSION_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTopLevelFunctions___destroy___0 = Module["_emscripten_bind_PxTopLevelFunctions___destroy___0"] = createExportWrapper("emscripten_bind_PxTopLevelFunctions___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorFlags_PxActorFlags_1 = Module["_emscripten_bind_PxActorFlags_PxActorFlags_1"] = createExportWrapper("emscripten_bind_PxActorFlags_PxActorFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorFlags_isSet_1 = Module["_emscripten_bind_PxActorFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxActorFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorFlags_set_1 = Module["_emscripten_bind_PxActorFlags_set_1"] = createExportWrapper("emscripten_bind_PxActorFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorFlags_clear_1 = Module["_emscripten_bind_PxActorFlags_clear_1"] = createExportWrapper("emscripten_bind_PxActorFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxActorFlags___destroy___0 = Module["_emscripten_bind_PxActorFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxActorFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_execute_0 = Module["_emscripten_bind_PxBatchQuery_execute_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_execute_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_getPreFilterShader_0 = Module["_emscripten_bind_PxBatchQuery_getPreFilterShader_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_getPreFilterShader_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_getPostFilterShader_0 = Module["_emscripten_bind_PxBatchQuery_getPostFilterShader_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_getPostFilterShader_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_getFilterShaderData_0 = Module["_emscripten_bind_PxBatchQuery_getFilterShaderData_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_getFilterShaderData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_getFilterShaderDataSize_0 = Module["_emscripten_bind_PxBatchQuery_getFilterShaderDataSize_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_getFilterShaderDataSize_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_setUserMemory_1 = Module["_emscripten_bind_PxBatchQuery_setUserMemory_1"] = createExportWrapper("emscripten_bind_PxBatchQuery_setUserMemory_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_getUserMemory_0 = Module["_emscripten_bind_PxBatchQuery_getUserMemory_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_getUserMemory_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQuery_release_0 = Module["_emscripten_bind_PxBatchQuery_release_0"] = createExportWrapper("emscripten_bind_PxBatchQuery_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_PxBatchQueryDesc_3 = Module["_emscripten_bind_PxBatchQueryDesc_PxBatchQueryDesc_3"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_PxBatchQueryDesc_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_isValid_0 = Module["_emscripten_bind_PxBatchQueryDesc_isValid_0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_isValid_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_get_filterShaderData_0 = Module["_emscripten_bind_PxBatchQueryDesc_get_filterShaderData_0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_get_filterShaderData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_set_filterShaderData_1 = Module["_emscripten_bind_PxBatchQueryDesc_set_filterShaderData_1"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_set_filterShaderData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_get_filterShaderDataSize_0 = Module["_emscripten_bind_PxBatchQueryDesc_get_filterShaderDataSize_0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_get_filterShaderDataSize_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_set_filterShaderDataSize_1 = Module["_emscripten_bind_PxBatchQueryDesc_set_filterShaderDataSize_1"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_set_filterShaderDataSize_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_get_preFilterShader_0 = Module["_emscripten_bind_PxBatchQueryDesc_get_preFilterShader_0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_get_preFilterShader_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_set_preFilterShader_1 = Module["_emscripten_bind_PxBatchQueryDesc_set_preFilterShader_1"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_set_preFilterShader_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_get_postFilterShader_0 = Module["_emscripten_bind_PxBatchQueryDesc_get_postFilterShader_0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_get_postFilterShader_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_set_postFilterShader_1 = Module["_emscripten_bind_PxBatchQueryDesc_set_postFilterShader_1"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_set_postFilterShader_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_get_queryMemory_0 = Module["_emscripten_bind_PxBatchQueryDesc_get_queryMemory_0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_get_queryMemory_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc_set_queryMemory_1 = Module["_emscripten_bind_PxBatchQueryDesc_set_queryMemory_1"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc_set_queryMemory_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryDesc___destroy___0 = Module["_emscripten_bind_PxBatchQueryDesc___destroy___0"] = createExportWrapper("emscripten_bind_PxBatchQueryDesc___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_userRaycastResultBuffer_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_userRaycastResultBuffer_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_userRaycastResultBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_userRaycastResultBuffer_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_userRaycastResultBuffer_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_userRaycastResultBuffer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_userRaycastTouchBuffer_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_userRaycastTouchBuffer_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_userRaycastTouchBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_userRaycastTouchBuffer_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_userRaycastTouchBuffer_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_userRaycastTouchBuffer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_userSweepResultBuffer_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_userSweepResultBuffer_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_userSweepResultBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_userSweepResultBuffer_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_userSweepResultBuffer_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_userSweepResultBuffer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_userSweepTouchBuffer_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_userSweepTouchBuffer_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_userSweepTouchBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_userSweepTouchBuffer_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_userSweepTouchBuffer_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_userSweepTouchBuffer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_userOverlapResultBuffer_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_userOverlapResultBuffer_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_userOverlapResultBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_userOverlapResultBuffer_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_userOverlapResultBuffer_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_userOverlapResultBuffer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_userOverlapTouchBuffer_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_userOverlapTouchBuffer_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_userOverlapTouchBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_userOverlapTouchBuffer_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_userOverlapTouchBuffer_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_userOverlapTouchBuffer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_raycastTouchBufferSize_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_raycastTouchBufferSize_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_raycastTouchBufferSize_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_raycastTouchBufferSize_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_raycastTouchBufferSize_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_raycastTouchBufferSize_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_sweepTouchBufferSize_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_sweepTouchBufferSize_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_sweepTouchBufferSize_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_sweepTouchBufferSize_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_sweepTouchBufferSize_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_sweepTouchBufferSize_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_get_overlapTouchBufferSize_0 = Module["_emscripten_bind_PxBatchQueryMemory_get_overlapTouchBufferSize_0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_get_overlapTouchBufferSize_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory_set_overlapTouchBufferSize_1 = Module["_emscripten_bind_PxBatchQueryMemory_set_overlapTouchBufferSize_1"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory_set_overlapTouchBufferSize_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryMemory___destroy___0 = Module["_emscripten_bind_PxBatchQueryMemory___destroy___0"] = createExportWrapper("emscripten_bind_PxBatchQueryMemory___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryPostFilterShader___destroy___0 = Module["_emscripten_bind_PxBatchQueryPostFilterShader___destroy___0"] = createExportWrapper("emscripten_bind_PxBatchQueryPostFilterShader___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBatchQueryPreFilterShader___destroy___0 = Module["_emscripten_bind_PxBatchQueryPreFilterShader___destroy___0"] = createExportWrapper("emscripten_bind_PxBatchQueryPreFilterShader___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_PxFilterData_0 = Module["_emscripten_bind_PxFilterData_PxFilterData_0"] = createExportWrapper("emscripten_bind_PxFilterData_PxFilterData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_PxFilterData_4 = Module["_emscripten_bind_PxFilterData_PxFilterData_4"] = createExportWrapper("emscripten_bind_PxFilterData_PxFilterData_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_get_word0_0 = Module["_emscripten_bind_PxFilterData_get_word0_0"] = createExportWrapper("emscripten_bind_PxFilterData_get_word0_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_set_word0_1 = Module["_emscripten_bind_PxFilterData_set_word0_1"] = createExportWrapper("emscripten_bind_PxFilterData_set_word0_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_get_word1_0 = Module["_emscripten_bind_PxFilterData_get_word1_0"] = createExportWrapper("emscripten_bind_PxFilterData_get_word1_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_set_word1_1 = Module["_emscripten_bind_PxFilterData_set_word1_1"] = createExportWrapper("emscripten_bind_PxFilterData_set_word1_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_get_word2_0 = Module["_emscripten_bind_PxFilterData_get_word2_0"] = createExportWrapper("emscripten_bind_PxFilterData_get_word2_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_set_word2_1 = Module["_emscripten_bind_PxFilterData_set_word2_1"] = createExportWrapper("emscripten_bind_PxFilterData_set_word2_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_get_word3_0 = Module["_emscripten_bind_PxFilterData_get_word3_0"] = createExportWrapper("emscripten_bind_PxFilterData_get_word3_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData_set_word3_1 = Module["_emscripten_bind_PxFilterData_set_word3_1"] = createExportWrapper("emscripten_bind_PxFilterData_set_word3_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxFilterData___destroy___0 = Module["_emscripten_bind_PxFilterData___destroy___0"] = createExportWrapper("emscripten_bind_PxFilterData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxHitFlags_PxHitFlags_1 = Module["_emscripten_bind_PxHitFlags_PxHitFlags_1"] = createExportWrapper("emscripten_bind_PxHitFlags_PxHitFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHitFlags_isSet_1 = Module["_emscripten_bind_PxHitFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxHitFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHitFlags_set_1 = Module["_emscripten_bind_PxHitFlags_set_1"] = createExportWrapper("emscripten_bind_PxHitFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHitFlags_clear_1 = Module["_emscripten_bind_PxHitFlags_clear_1"] = createExportWrapper("emscripten_bind_PxHitFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHitFlags___destroy___0 = Module["_emscripten_bind_PxHitFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxHitFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapHit___destroy___0 = Module["_emscripten_bind_PxOverlapHit___destroy___0"] = createExportWrapper("emscripten_bind_PxOverlapHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_getNbAnyHits_0 = Module["_emscripten_bind_PxOverlapQueryResult_getNbAnyHits_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_getNbAnyHits_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_getAnyHit_1 = Module["_emscripten_bind_PxOverlapQueryResult_getAnyHit_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_getAnyHit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_get_block_0 = Module["_emscripten_bind_PxOverlapQueryResult_get_block_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_get_block_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_set_block_1 = Module["_emscripten_bind_PxOverlapQueryResult_set_block_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_set_block_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_get_touches_0 = Module["_emscripten_bind_PxOverlapQueryResult_get_touches_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_get_touches_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_set_touches_1 = Module["_emscripten_bind_PxOverlapQueryResult_set_touches_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_set_touches_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_get_nbTouches_0 = Module["_emscripten_bind_PxOverlapQueryResult_get_nbTouches_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_get_nbTouches_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_set_nbTouches_1 = Module["_emscripten_bind_PxOverlapQueryResult_set_nbTouches_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_set_nbTouches_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_get_userData_0 = Module["_emscripten_bind_PxOverlapQueryResult_get_userData_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_get_userData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_set_userData_1 = Module["_emscripten_bind_PxOverlapQueryResult_set_userData_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_set_userData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_get_queryStatus_0 = Module["_emscripten_bind_PxOverlapQueryResult_get_queryStatus_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_get_queryStatus_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_set_queryStatus_1 = Module["_emscripten_bind_PxOverlapQueryResult_set_queryStatus_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_set_queryStatus_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_get_hasBlock_0 = Module["_emscripten_bind_PxOverlapQueryResult_get_hasBlock_0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_get_hasBlock_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult_set_hasBlock_1 = Module["_emscripten_bind_PxOverlapQueryResult_set_hasBlock_1"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult_set_hasBlock_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxOverlapQueryResult___destroy___0 = Module["_emscripten_bind_PxOverlapQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_PxOverlapQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_release_0 = Module["_emscripten_bind_PxMaterial_release_0"] = createExportWrapper("emscripten_bind_PxMaterial_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_getConcreteTypeName_0 = Module["_emscripten_bind_PxMaterial_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxMaterial_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_getConcreteType_0 = Module["_emscripten_bind_PxMaterial_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxMaterial_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_setBaseFlag_2 = Module["_emscripten_bind_PxMaterial_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxMaterial_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_setBaseFlags_1 = Module["_emscripten_bind_PxMaterial_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxMaterial_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_getBaseFlags_0 = Module["_emscripten_bind_PxMaterial_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxMaterial_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterial_isReleasable_0 = Module["_emscripten_bind_PxMaterial_isReleasable_0"] = createExportWrapper("emscripten_bind_PxMaterial_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_getFoundation_0 = Module["_emscripten_bind_PxPhysics_getFoundation_0"] = createExportWrapper("emscripten_bind_PxPhysics_getFoundation_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_getTolerancesScale_0 = Module["_emscripten_bind_PxPhysics_getTolerancesScale_0"] = createExportWrapper("emscripten_bind_PxPhysics_getTolerancesScale_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createScene_1 = Module["_emscripten_bind_PxPhysics_createScene_1"] = createExportWrapper("emscripten_bind_PxPhysics_createScene_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createRigidStatic_1 = Module["_emscripten_bind_PxPhysics_createRigidStatic_1"] = createExportWrapper("emscripten_bind_PxPhysics_createRigidStatic_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createRigidDynamic_1 = Module["_emscripten_bind_PxPhysics_createRigidDynamic_1"] = createExportWrapper("emscripten_bind_PxPhysics_createRigidDynamic_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createShape_2 = Module["_emscripten_bind_PxPhysics_createShape_2"] = createExportWrapper("emscripten_bind_PxPhysics_createShape_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createShape_3 = Module["_emscripten_bind_PxPhysics_createShape_3"] = createExportWrapper("emscripten_bind_PxPhysics_createShape_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createShape_4 = Module["_emscripten_bind_PxPhysics_createShape_4"] = createExportWrapper("emscripten_bind_PxPhysics_createShape_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_getNbShapes_0 = Module["_emscripten_bind_PxPhysics_getNbShapes_0"] = createExportWrapper("emscripten_bind_PxPhysics_getNbShapes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_createMaterial_3 = Module["_emscripten_bind_PxPhysics_createMaterial_3"] = createExportWrapper("emscripten_bind_PxPhysics_createMaterial_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics_getPhysicsInsertionCallback_0 = Module["_emscripten_bind_PxPhysics_getPhysicsInsertionCallback_0"] = createExportWrapper("emscripten_bind_PxPhysics_getPhysicsInsertionCallback_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPhysics___destroy___0 = Module["_emscripten_bind_PxPhysics___destroy___0"] = createExportWrapper("emscripten_bind_PxPhysics___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_PxRaycastHit_0 = Module["_emscripten_bind_PxRaycastHit_PxRaycastHit_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_PxRaycastHit_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_u_0 = Module["_emscripten_bind_PxRaycastHit_get_u_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_u_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_u_1 = Module["_emscripten_bind_PxRaycastHit_set_u_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_u_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_v_0 = Module["_emscripten_bind_PxRaycastHit_get_v_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_v_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_v_1 = Module["_emscripten_bind_PxRaycastHit_set_v_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_v_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_actor_0 = Module["_emscripten_bind_PxRaycastHit_get_actor_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_actor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_actor_1 = Module["_emscripten_bind_PxRaycastHit_set_actor_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_actor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_shape_0 = Module["_emscripten_bind_PxRaycastHit_get_shape_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_shape_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_shape_1 = Module["_emscripten_bind_PxRaycastHit_set_shape_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_shape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_flags_0 = Module["_emscripten_bind_PxRaycastHit_get_flags_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_flags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_flags_1 = Module["_emscripten_bind_PxRaycastHit_set_flags_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_flags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_position_0 = Module["_emscripten_bind_PxRaycastHit_get_position_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_position_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_position_1 = Module["_emscripten_bind_PxRaycastHit_set_position_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_position_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_normal_0 = Module["_emscripten_bind_PxRaycastHit_get_normal_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_normal_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_normal_1 = Module["_emscripten_bind_PxRaycastHit_set_normal_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_normal_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_distance_0 = Module["_emscripten_bind_PxRaycastHit_get_distance_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_distance_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_distance_1 = Module["_emscripten_bind_PxRaycastHit_set_distance_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_distance_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_get_faceIndex_0 = Module["_emscripten_bind_PxRaycastHit_get_faceIndex_0"] = createExportWrapper("emscripten_bind_PxRaycastHit_get_faceIndex_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit_set_faceIndex_1 = Module["_emscripten_bind_PxRaycastHit_set_faceIndex_1"] = createExportWrapper("emscripten_bind_PxRaycastHit_set_faceIndex_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastHit___destroy___0 = Module["_emscripten_bind_PxRaycastHit___destroy___0"] = createExportWrapper("emscripten_bind_PxRaycastHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_getNbAnyHits_0 = Module["_emscripten_bind_PxRaycastQueryResult_getNbAnyHits_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_getNbAnyHits_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_getAnyHit_1 = Module["_emscripten_bind_PxRaycastQueryResult_getAnyHit_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_getAnyHit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_get_block_0 = Module["_emscripten_bind_PxRaycastQueryResult_get_block_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_get_block_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_set_block_1 = Module["_emscripten_bind_PxRaycastQueryResult_set_block_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_set_block_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_get_touches_0 = Module["_emscripten_bind_PxRaycastQueryResult_get_touches_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_get_touches_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_set_touches_1 = Module["_emscripten_bind_PxRaycastQueryResult_set_touches_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_set_touches_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_get_nbTouches_0 = Module["_emscripten_bind_PxRaycastQueryResult_get_nbTouches_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_get_nbTouches_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_set_nbTouches_1 = Module["_emscripten_bind_PxRaycastQueryResult_set_nbTouches_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_set_nbTouches_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_get_userData_0 = Module["_emscripten_bind_PxRaycastQueryResult_get_userData_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_get_userData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_set_userData_1 = Module["_emscripten_bind_PxRaycastQueryResult_set_userData_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_set_userData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_get_queryStatus_0 = Module["_emscripten_bind_PxRaycastQueryResult_get_queryStatus_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_get_queryStatus_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_set_queryStatus_1 = Module["_emscripten_bind_PxRaycastQueryResult_set_queryStatus_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_set_queryStatus_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_get_hasBlock_0 = Module["_emscripten_bind_PxRaycastQueryResult_get_hasBlock_0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_get_hasBlock_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult_set_hasBlock_1 = Module["_emscripten_bind_PxRaycastQueryResult_set_hasBlock_1"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult_set_hasBlock_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRaycastQueryResult___destroy___0 = Module["_emscripten_bind_PxRaycastQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_PxRaycastQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBodyFlags_PxRigidBodyFlags_1 = Module["_emscripten_bind_PxRigidBodyFlags_PxRigidBodyFlags_1"] = createExportWrapper("emscripten_bind_PxRigidBodyFlags_PxRigidBodyFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBodyFlags_isSet_1 = Module["_emscripten_bind_PxRigidBodyFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxRigidBodyFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBodyFlags_set_1 = Module["_emscripten_bind_PxRigidBodyFlags_set_1"] = createExportWrapper("emscripten_bind_PxRigidBodyFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBodyFlags_clear_1 = Module["_emscripten_bind_PxRigidBodyFlags_clear_1"] = createExportWrapper("emscripten_bind_PxRigidBodyFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidBodyFlags___destroy___0 = Module["_emscripten_bind_PxRigidBodyFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxRigidBodyFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_isSleeping_0 = Module["_emscripten_bind_PxRigidDynamic_isSleeping_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_isSleeping_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setSleepThreshold_1 = Module["_emscripten_bind_PxRigidDynamic_setSleepThreshold_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setSleepThreshold_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getSleepThreshold_0 = Module["_emscripten_bind_PxRigidDynamic_getSleepThreshold_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getSleepThreshold_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setStabilizationThreshold_1 = Module["_emscripten_bind_PxRigidDynamic_setStabilizationThreshold_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setStabilizationThreshold_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getStabilizationThreshold_0 = Module["_emscripten_bind_PxRigidDynamic_getStabilizationThreshold_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getStabilizationThreshold_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getRigidDynamicLockFlags_0 = Module["_emscripten_bind_PxRigidDynamic_getRigidDynamicLockFlags_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getRigidDynamicLockFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlag_2 = Module["_emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlag_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlags_1 = Module["_emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlags_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setWakeCounter_1 = Module["_emscripten_bind_PxRigidDynamic_setWakeCounter_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setWakeCounter_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getWakeCounter_0 = Module["_emscripten_bind_PxRigidDynamic_getWakeCounter_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getWakeCounter_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_wakeUp_0 = Module["_emscripten_bind_PxRigidDynamic_wakeUp_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_wakeUp_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_putToSleep_0 = Module["_emscripten_bind_PxRigidDynamic_putToSleep_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_putToSleep_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setSolverIterationCounts_1 = Module["_emscripten_bind_PxRigidDynamic_setSolverIterationCounts_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setSolverIterationCounts_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setSolverIterationCounts_2 = Module["_emscripten_bind_PxRigidDynamic_setSolverIterationCounts_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setSolverIterationCounts_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getContactReportThreshold_0 = Module["_emscripten_bind_PxRigidDynamic_getContactReportThreshold_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getContactReportThreshold_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setContactReportThreshold_1 = Module["_emscripten_bind_PxRigidDynamic_setContactReportThreshold_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setContactReportThreshold_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getType_0 = Module["_emscripten_bind_PxRigidDynamic_getType_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getScene_0 = Module["_emscripten_bind_PxRigidDynamic_getScene_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getScene_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setName_1 = Module["_emscripten_bind_PxRigidDynamic_setName_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setName_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getName_0 = Module["_emscripten_bind_PxRigidDynamic_getName_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getWorldBounds_0 = Module["_emscripten_bind_PxRigidDynamic_getWorldBounds_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getWorldBounds_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getWorldBounds_1 = Module["_emscripten_bind_PxRigidDynamic_getWorldBounds_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getWorldBounds_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setActorFlags_1 = Module["_emscripten_bind_PxRigidDynamic_setActorFlags_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setActorFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getActorFlags_0 = Module["_emscripten_bind_PxRigidDynamic_getActorFlags_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getActorFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setDominanceGroup_1 = Module["_emscripten_bind_PxRigidDynamic_setDominanceGroup_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setDominanceGroup_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getDominanceGroup_0 = Module["_emscripten_bind_PxRigidDynamic_getDominanceGroup_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getDominanceGroup_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setOwnerClient_1 = Module["_emscripten_bind_PxRigidDynamic_setOwnerClient_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setOwnerClient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getOwnerClient_0 = Module["_emscripten_bind_PxRigidDynamic_getOwnerClient_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getOwnerClient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_release_0 = Module["_emscripten_bind_PxRigidDynamic_release_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getConcreteTypeName_0 = Module["_emscripten_bind_PxRigidDynamic_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getConcreteType_0 = Module["_emscripten_bind_PxRigidDynamic_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setBaseFlag_2 = Module["_emscripten_bind_PxRigidDynamic_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setBaseFlags_1 = Module["_emscripten_bind_PxRigidDynamic_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getBaseFlags_0 = Module["_emscripten_bind_PxRigidDynamic_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_isReleasable_0 = Module["_emscripten_bind_PxRigidDynamic_isReleasable_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getGlobalPose_0 = Module["_emscripten_bind_PxRigidDynamic_getGlobalPose_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getGlobalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setGlobalPose_1 = Module["_emscripten_bind_PxRigidDynamic_setGlobalPose_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setGlobalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setGlobalPose_2 = Module["_emscripten_bind_PxRigidDynamic_setGlobalPose_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setGlobalPose_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_attachShape_1 = Module["_emscripten_bind_PxRigidDynamic_attachShape_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_attachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_detachShape_1 = Module["_emscripten_bind_PxRigidDynamic_detachShape_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_detachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_detachShape_2 = Module["_emscripten_bind_PxRigidDynamic_detachShape_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_detachShape_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getNbShapes_0 = Module["_emscripten_bind_PxRigidDynamic_getNbShapes_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getNbShapes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getShapes_3 = Module["_emscripten_bind_PxRigidDynamic_getShapes_3"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getShapes_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setCMassLocalPose_1 = Module["_emscripten_bind_PxRigidDynamic_setCMassLocalPose_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setCMassLocalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getCMassLocalPose_0 = Module["_emscripten_bind_PxRigidDynamic_getCMassLocalPose_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getCMassLocalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMass_1 = Module["_emscripten_bind_PxRigidDynamic_setMass_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMass_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMass_0 = Module["_emscripten_bind_PxRigidDynamic_getMass_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getInvMass_0 = Module["_emscripten_bind_PxRigidDynamic_getInvMass_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getInvMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMassSpaceInertiaTensor_1 = Module["_emscripten_bind_PxRigidDynamic_setMassSpaceInertiaTensor_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMassSpaceInertiaTensor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMassSpaceInertiaTensor_0 = Module["_emscripten_bind_PxRigidDynamic_getMassSpaceInertiaTensor_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMassSpaceInertiaTensor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMassSpaceInvInertiaTensor_0 = Module["_emscripten_bind_PxRigidDynamic_getMassSpaceInvInertiaTensor_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMassSpaceInvInertiaTensor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setLinearDamping_1 = Module["_emscripten_bind_PxRigidDynamic_setLinearDamping_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setLinearDamping_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getLinearDamping_0 = Module["_emscripten_bind_PxRigidDynamic_getLinearDamping_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getLinearDamping_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setAngularDamping_1 = Module["_emscripten_bind_PxRigidDynamic_setAngularDamping_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setAngularDamping_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getAngularDamping_0 = Module["_emscripten_bind_PxRigidDynamic_getAngularDamping_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getAngularDamping_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getLinearVelocity_0 = Module["_emscripten_bind_PxRigidDynamic_getLinearVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getLinearVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setLinearVelocity_1 = Module["_emscripten_bind_PxRigidDynamic_setLinearVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setLinearVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setLinearVelocity_2 = Module["_emscripten_bind_PxRigidDynamic_setLinearVelocity_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setLinearVelocity_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getAngularVelocity_0 = Module["_emscripten_bind_PxRigidDynamic_getAngularVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getAngularVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setAngularVelocity_1 = Module["_emscripten_bind_PxRigidDynamic_setAngularVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setAngularVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setAngularVelocity_2 = Module["_emscripten_bind_PxRigidDynamic_setAngularVelocity_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setAngularVelocity_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMaxLinearVelocity_0 = Module["_emscripten_bind_PxRigidDynamic_getMaxLinearVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMaxLinearVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMaxLinearVelocity_1 = Module["_emscripten_bind_PxRigidDynamic_setMaxLinearVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMaxLinearVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMaxAngularVelocity_0 = Module["_emscripten_bind_PxRigidDynamic_getMaxAngularVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMaxAngularVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMaxAngularVelocity_1 = Module["_emscripten_bind_PxRigidDynamic_setMaxAngularVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMaxAngularVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_addForce_1 = Module["_emscripten_bind_PxRigidDynamic_addForce_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_addForce_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_addForce_2 = Module["_emscripten_bind_PxRigidDynamic_addForce_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_addForce_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_addForce_3 = Module["_emscripten_bind_PxRigidDynamic_addForce_3"] = createExportWrapper("emscripten_bind_PxRigidDynamic_addForce_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_addTorque_1 = Module["_emscripten_bind_PxRigidDynamic_addTorque_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_addTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_addTorque_2 = Module["_emscripten_bind_PxRigidDynamic_addTorque_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_addTorque_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_addTorque_3 = Module["_emscripten_bind_PxRigidDynamic_addTorque_3"] = createExportWrapper("emscripten_bind_PxRigidDynamic_addTorque_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_clearForce_1 = Module["_emscripten_bind_PxRigidDynamic_clearForce_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_clearForce_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_clearTorque_1 = Module["_emscripten_bind_PxRigidDynamic_clearTorque_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_clearTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setForceAndTorque_2 = Module["_emscripten_bind_PxRigidDynamic_setForceAndTorque_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setForceAndTorque_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setForceAndTorque_3 = Module["_emscripten_bind_PxRigidDynamic_setForceAndTorque_3"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setForceAndTorque_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setRigidBodyFlag_2 = Module["_emscripten_bind_PxRigidDynamic_setRigidBodyFlag_2"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setRigidBodyFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setRigidBodyFlags_1 = Module["_emscripten_bind_PxRigidDynamic_setRigidBodyFlags_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setRigidBodyFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getRigidBodyFlags_0 = Module["_emscripten_bind_PxRigidDynamic_getRigidBodyFlags_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getRigidBodyFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMinCCDAdvanceCoefficient_1 = Module["_emscripten_bind_PxRigidDynamic_setMinCCDAdvanceCoefficient_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMinCCDAdvanceCoefficient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMinCCDAdvanceCoefficient_0 = Module["_emscripten_bind_PxRigidDynamic_getMinCCDAdvanceCoefficient_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMinCCDAdvanceCoefficient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMaxDepenetrationVelocity_1 = Module["_emscripten_bind_PxRigidDynamic_setMaxDepenetrationVelocity_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMaxDepenetrationVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMaxDepenetrationVelocity_0 = Module["_emscripten_bind_PxRigidDynamic_getMaxDepenetrationVelocity_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMaxDepenetrationVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_setMaxContactImpulse_1 = Module["_emscripten_bind_PxRigidDynamic_setMaxContactImpulse_1"] = createExportWrapper("emscripten_bind_PxRigidDynamic_setMaxContactImpulse_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getMaxContactImpulse_0 = Module["_emscripten_bind_PxRigidDynamic_getMaxContactImpulse_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getMaxContactImpulse_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamic_getInternalIslandNodeIndex_0 = Module["_emscripten_bind_PxRigidDynamic_getInternalIslandNodeIndex_0"] = createExportWrapper("emscripten_bind_PxRigidDynamic_getInternalIslandNodeIndex_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamicLockFlags_PxRigidDynamicLockFlags_1 = Module["_emscripten_bind_PxRigidDynamicLockFlags_PxRigidDynamicLockFlags_1"] = createExportWrapper("emscripten_bind_PxRigidDynamicLockFlags_PxRigidDynamicLockFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamicLockFlags_isSet_1 = Module["_emscripten_bind_PxRigidDynamicLockFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxRigidDynamicLockFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamicLockFlags_set_1 = Module["_emscripten_bind_PxRigidDynamicLockFlags_set_1"] = createExportWrapper("emscripten_bind_PxRigidDynamicLockFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamicLockFlags_clear_1 = Module["_emscripten_bind_PxRigidDynamicLockFlags_clear_1"] = createExportWrapper("emscripten_bind_PxRigidDynamicLockFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidDynamicLockFlags___destroy___0 = Module["_emscripten_bind_PxRigidDynamicLockFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxRigidDynamicLockFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getType_0 = Module["_emscripten_bind_PxRigidStatic_getType_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getScene_0 = Module["_emscripten_bind_PxRigidStatic_getScene_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getScene_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setName_1 = Module["_emscripten_bind_PxRigidStatic_setName_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_setName_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getName_0 = Module["_emscripten_bind_PxRigidStatic_getName_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getWorldBounds_0 = Module["_emscripten_bind_PxRigidStatic_getWorldBounds_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getWorldBounds_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getWorldBounds_1 = Module["_emscripten_bind_PxRigidStatic_getWorldBounds_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_getWorldBounds_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setActorFlags_1 = Module["_emscripten_bind_PxRigidStatic_setActorFlags_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_setActorFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getActorFlags_0 = Module["_emscripten_bind_PxRigidStatic_getActorFlags_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getActorFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setDominanceGroup_1 = Module["_emscripten_bind_PxRigidStatic_setDominanceGroup_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_setDominanceGroup_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getDominanceGroup_0 = Module["_emscripten_bind_PxRigidStatic_getDominanceGroup_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getDominanceGroup_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setOwnerClient_1 = Module["_emscripten_bind_PxRigidStatic_setOwnerClient_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_setOwnerClient_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getOwnerClient_0 = Module["_emscripten_bind_PxRigidStatic_getOwnerClient_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getOwnerClient_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_release_0 = Module["_emscripten_bind_PxRigidStatic_release_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getConcreteTypeName_0 = Module["_emscripten_bind_PxRigidStatic_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getConcreteType_0 = Module["_emscripten_bind_PxRigidStatic_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setBaseFlag_2 = Module["_emscripten_bind_PxRigidStatic_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxRigidStatic_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setBaseFlags_1 = Module["_emscripten_bind_PxRigidStatic_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getBaseFlags_0 = Module["_emscripten_bind_PxRigidStatic_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_isReleasable_0 = Module["_emscripten_bind_PxRigidStatic_isReleasable_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getGlobalPose_0 = Module["_emscripten_bind_PxRigidStatic_getGlobalPose_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getGlobalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setGlobalPose_1 = Module["_emscripten_bind_PxRigidStatic_setGlobalPose_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_setGlobalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_setGlobalPose_2 = Module["_emscripten_bind_PxRigidStatic_setGlobalPose_2"] = createExportWrapper("emscripten_bind_PxRigidStatic_setGlobalPose_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_attachShape_1 = Module["_emscripten_bind_PxRigidStatic_attachShape_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_attachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_detachShape_1 = Module["_emscripten_bind_PxRigidStatic_detachShape_1"] = createExportWrapper("emscripten_bind_PxRigidStatic_detachShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_detachShape_2 = Module["_emscripten_bind_PxRigidStatic_detachShape_2"] = createExportWrapper("emscripten_bind_PxRigidStatic_detachShape_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getNbShapes_0 = Module["_emscripten_bind_PxRigidStatic_getNbShapes_0"] = createExportWrapper("emscripten_bind_PxRigidStatic_getNbShapes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRigidStatic_getShapes_3 = Module["_emscripten_bind_PxRigidStatic_getShapes_3"] = createExportWrapper("emscripten_bind_PxRigidStatic_getShapes_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_addActor_1 = Module["_emscripten_bind_PxScene_addActor_1"] = createExportWrapper("emscripten_bind_PxScene_addActor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_addActor_2 = Module["_emscripten_bind_PxScene_addActor_2"] = createExportWrapper("emscripten_bind_PxScene_addActor_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_removeActor_1 = Module["_emscripten_bind_PxScene_removeActor_1"] = createExportWrapper("emscripten_bind_PxScene_removeActor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_removeActor_2 = Module["_emscripten_bind_PxScene_removeActor_2"] = createExportWrapper("emscripten_bind_PxScene_removeActor_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_simulate_1 = Module["_emscripten_bind_PxScene_simulate_1"] = createExportWrapper("emscripten_bind_PxScene_simulate_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_simulate_2 = Module["_emscripten_bind_PxScene_simulate_2"] = createExportWrapper("emscripten_bind_PxScene_simulate_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_simulate_3 = Module["_emscripten_bind_PxScene_simulate_3"] = createExportWrapper("emscripten_bind_PxScene_simulate_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_simulate_4 = Module["_emscripten_bind_PxScene_simulate_4"] = createExportWrapper("emscripten_bind_PxScene_simulate_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_simulate_5 = Module["_emscripten_bind_PxScene_simulate_5"] = createExportWrapper("emscripten_bind_PxScene_simulate_5");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_fetchResults_0 = Module["_emscripten_bind_PxScene_fetchResults_0"] = createExportWrapper("emscripten_bind_PxScene_fetchResults_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_fetchResults_1 = Module["_emscripten_bind_PxScene_fetchResults_1"] = createExportWrapper("emscripten_bind_PxScene_fetchResults_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_setGravity_1 = Module["_emscripten_bind_PxScene_setGravity_1"] = createExportWrapper("emscripten_bind_PxScene_setGravity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_getGravity_0 = Module["_emscripten_bind_PxScene_getGravity_0"] = createExportWrapper("emscripten_bind_PxScene_getGravity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxScene_createBatchQuery_1 = Module["_emscripten_bind_PxScene_createBatchQuery_1"] = createExportWrapper("emscripten_bind_PxScene_createBatchQuery_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_PxSceneDesc_1 = Module["_emscripten_bind_PxSceneDesc_PxSceneDesc_1"] = createExportWrapper("emscripten_bind_PxSceneDesc_PxSceneDesc_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_get_gravity_0 = Module["_emscripten_bind_PxSceneDesc_get_gravity_0"] = createExportWrapper("emscripten_bind_PxSceneDesc_get_gravity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_set_gravity_1 = Module["_emscripten_bind_PxSceneDesc_set_gravity_1"] = createExportWrapper("emscripten_bind_PxSceneDesc_set_gravity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_get_simulationEventCallback_0 = Module["_emscripten_bind_PxSceneDesc_get_simulationEventCallback_0"] = createExportWrapper("emscripten_bind_PxSceneDesc_get_simulationEventCallback_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_set_simulationEventCallback_1 = Module["_emscripten_bind_PxSceneDesc_set_simulationEventCallback_1"] = createExportWrapper("emscripten_bind_PxSceneDesc_set_simulationEventCallback_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_get_filterShader_0 = Module["_emscripten_bind_PxSceneDesc_get_filterShader_0"] = createExportWrapper("emscripten_bind_PxSceneDesc_get_filterShader_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_set_filterShader_1 = Module["_emscripten_bind_PxSceneDesc_set_filterShader_1"] = createExportWrapper("emscripten_bind_PxSceneDesc_set_filterShader_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_get_cpuDispatcher_0 = Module["_emscripten_bind_PxSceneDesc_get_cpuDispatcher_0"] = createExportWrapper("emscripten_bind_PxSceneDesc_get_cpuDispatcher_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_set_cpuDispatcher_1 = Module["_emscripten_bind_PxSceneDesc_set_cpuDispatcher_1"] = createExportWrapper("emscripten_bind_PxSceneDesc_set_cpuDispatcher_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_get_flags_0 = Module["_emscripten_bind_PxSceneDesc_get_flags_0"] = createExportWrapper("emscripten_bind_PxSceneDesc_get_flags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc_set_flags_1 = Module["_emscripten_bind_PxSceneDesc_set_flags_1"] = createExportWrapper("emscripten_bind_PxSceneDesc_set_flags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneDesc___destroy___0 = Module["_emscripten_bind_PxSceneDesc___destroy___0"] = createExportWrapper("emscripten_bind_PxSceneDesc___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneFlags_PxSceneFlags_1 = Module["_emscripten_bind_PxSceneFlags_PxSceneFlags_1"] = createExportWrapper("emscripten_bind_PxSceneFlags_PxSceneFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneFlags_isSet_1 = Module["_emscripten_bind_PxSceneFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxSceneFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneFlags_set_1 = Module["_emscripten_bind_PxSceneFlags_set_1"] = createExportWrapper("emscripten_bind_PxSceneFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneFlags_clear_1 = Module["_emscripten_bind_PxSceneFlags_clear_1"] = createExportWrapper("emscripten_bind_PxSceneFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSceneFlags___destroy___0 = Module["_emscripten_bind_PxSceneFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxSceneFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_setLocalPose_1 = Module["_emscripten_bind_PxShape_setLocalPose_1"] = createExportWrapper("emscripten_bind_PxShape_setLocalPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_getLocalPose_0 = Module["_emscripten_bind_PxShape_getLocalPose_0"] = createExportWrapper("emscripten_bind_PxShape_getLocalPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_setSimulationFilterData_1 = Module["_emscripten_bind_PxShape_setSimulationFilterData_1"] = createExportWrapper("emscripten_bind_PxShape_setSimulationFilterData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_getSimulationFilterData_0 = Module["_emscripten_bind_PxShape_getSimulationFilterData_0"] = createExportWrapper("emscripten_bind_PxShape_getSimulationFilterData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_setQueryFilterData_1 = Module["_emscripten_bind_PxShape_setQueryFilterData_1"] = createExportWrapper("emscripten_bind_PxShape_setQueryFilterData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_getQueryFilterData_0 = Module["_emscripten_bind_PxShape_getQueryFilterData_0"] = createExportWrapper("emscripten_bind_PxShape_getQueryFilterData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_release_0 = Module["_emscripten_bind_PxShape_release_0"] = createExportWrapper("emscripten_bind_PxShape_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_getConcreteTypeName_0 = Module["_emscripten_bind_PxShape_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxShape_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_getConcreteType_0 = Module["_emscripten_bind_PxShape_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxShape_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_setBaseFlag_2 = Module["_emscripten_bind_PxShape_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxShape_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_setBaseFlags_1 = Module["_emscripten_bind_PxShape_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxShape_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_getBaseFlags_0 = Module["_emscripten_bind_PxShape_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxShape_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShape_isReleasable_0 = Module["_emscripten_bind_PxShape_isReleasable_0"] = createExportWrapper("emscripten_bind_PxShape_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxShapeFlags_PxShapeFlags_1 = Module["_emscripten_bind_PxShapeFlags_PxShapeFlags_1"] = createExportWrapper("emscripten_bind_PxShapeFlags_PxShapeFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShapeFlags_isSet_1 = Module["_emscripten_bind_PxShapeFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxShapeFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShapeFlags_set_1 = Module["_emscripten_bind_PxShapeFlags_set_1"] = createExportWrapper("emscripten_bind_PxShapeFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShapeFlags_clear_1 = Module["_emscripten_bind_PxShapeFlags_clear_1"] = createExportWrapper("emscripten_bind_PxShapeFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxShapeFlags___destroy___0 = Module["_emscripten_bind_PxShapeFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxShapeFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_JsPxSimulationEventCallback_JsPxSimulationEventCallback_0 = Module["_emscripten_bind_JsPxSimulationEventCallback_JsPxSimulationEventCallback_0"] = createExportWrapper("emscripten_bind_JsPxSimulationEventCallback_JsPxSimulationEventCallback_0");

/** @type {function(...*):?} */
var _emscripten_bind_JsPxSimulationEventCallback_cbFun_1 = Module["_emscripten_bind_JsPxSimulationEventCallback_cbFun_1"] = createExportWrapper("emscripten_bind_JsPxSimulationEventCallback_cbFun_1");

/** @type {function(...*):?} */
var _emscripten_bind_JsPxSimulationEventCallback___destroy___0 = Module["_emscripten_bind_JsPxSimulationEventCallback___destroy___0"] = createExportWrapper("emscripten_bind_JsPxSimulationEventCallback___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSimulationFilterShader___destroy___0 = Module["_emscripten_bind_PxSimulationFilterShader___destroy___0"] = createExportWrapper("emscripten_bind_PxSimulationFilterShader___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_actor_0 = Module["_emscripten_bind_PxSweepHit_get_actor_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_actor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_actor_1 = Module["_emscripten_bind_PxSweepHit_set_actor_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_actor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_shape_0 = Module["_emscripten_bind_PxSweepHit_get_shape_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_shape_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_shape_1 = Module["_emscripten_bind_PxSweepHit_set_shape_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_shape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_flags_0 = Module["_emscripten_bind_PxSweepHit_get_flags_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_flags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_flags_1 = Module["_emscripten_bind_PxSweepHit_set_flags_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_flags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_position_0 = Module["_emscripten_bind_PxSweepHit_get_position_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_position_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_position_1 = Module["_emscripten_bind_PxSweepHit_set_position_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_position_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_normal_0 = Module["_emscripten_bind_PxSweepHit_get_normal_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_normal_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_normal_1 = Module["_emscripten_bind_PxSweepHit_set_normal_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_normal_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_distance_0 = Module["_emscripten_bind_PxSweepHit_get_distance_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_distance_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_distance_1 = Module["_emscripten_bind_PxSweepHit_set_distance_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_distance_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_get_faceIndex_0 = Module["_emscripten_bind_PxSweepHit_get_faceIndex_0"] = createExportWrapper("emscripten_bind_PxSweepHit_get_faceIndex_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit_set_faceIndex_1 = Module["_emscripten_bind_PxSweepHit_set_faceIndex_1"] = createExportWrapper("emscripten_bind_PxSweepHit_set_faceIndex_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepHit___destroy___0 = Module["_emscripten_bind_PxSweepHit___destroy___0"] = createExportWrapper("emscripten_bind_PxSweepHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_getNbAnyHits_0 = Module["_emscripten_bind_PxSweepQueryResult_getNbAnyHits_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_getNbAnyHits_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_getAnyHit_1 = Module["_emscripten_bind_PxSweepQueryResult_getAnyHit_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_getAnyHit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_get_block_0 = Module["_emscripten_bind_PxSweepQueryResult_get_block_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_get_block_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_set_block_1 = Module["_emscripten_bind_PxSweepQueryResult_set_block_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_set_block_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_get_touches_0 = Module["_emscripten_bind_PxSweepQueryResult_get_touches_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_get_touches_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_set_touches_1 = Module["_emscripten_bind_PxSweepQueryResult_set_touches_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_set_touches_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_get_nbTouches_0 = Module["_emscripten_bind_PxSweepQueryResult_get_nbTouches_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_get_nbTouches_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_set_nbTouches_1 = Module["_emscripten_bind_PxSweepQueryResult_set_nbTouches_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_set_nbTouches_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_get_userData_0 = Module["_emscripten_bind_PxSweepQueryResult_get_userData_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_get_userData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_set_userData_1 = Module["_emscripten_bind_PxSweepQueryResult_set_userData_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_set_userData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_get_queryStatus_0 = Module["_emscripten_bind_PxSweepQueryResult_get_queryStatus_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_get_queryStatus_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_set_queryStatus_1 = Module["_emscripten_bind_PxSweepQueryResult_set_queryStatus_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_set_queryStatus_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_get_hasBlock_0 = Module["_emscripten_bind_PxSweepQueryResult_get_hasBlock_0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_get_hasBlock_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult_set_hasBlock_1 = Module["_emscripten_bind_PxSweepQueryResult_set_hasBlock_1"] = createExportWrapper("emscripten_bind_PxSweepQueryResult_set_hasBlock_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSweepQueryResult___destroy___0 = Module["_emscripten_bind_PxSweepQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_PxSweepQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_InitVehicleSDK_1 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_InitVehicleSDK_1"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_InitVehicleSDK_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleComputeSprungMasses_6 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleComputeSprungMasses_6"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleComputeSprungMasses_6");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleSuspensionRaycasts_4 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleSuspensionRaycasts_4"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleSuspensionRaycasts_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleUpdates_5 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleUpdates_5"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleUpdates_5");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetBasisVectors_2 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetBasisVectors_2"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetBasisVectors_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetUpdateMode_1 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetUpdateMode_1"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetUpdateMode_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_getFrictionVsSlipGraph_3 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_getFrictionVsSlipGraph_3"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_getFrictionVsSlipGraph_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_setFrictionVsSlipGraph_4 = Module["_emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_setFrictionVsSlipGraph_4"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_setFrictionVsSlipGraph_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTopLevelFunctions___destroy___0 = Module["_emscripten_bind_PxVehicleTopLevelFunctions___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleTopLevelFunctions___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_PxVehicleAckermannGeometryData_0 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_PxVehicleAckermannGeometryData_0"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_PxVehicleAckermannGeometryData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_get_mAccuracy_0 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_get_mAccuracy_0"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_get_mAccuracy_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_set_mAccuracy_1 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_set_mAccuracy_1"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_set_mAccuracy_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_get_mFrontWidth_0 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_get_mFrontWidth_0"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_get_mFrontWidth_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_set_mFrontWidth_1 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_set_mFrontWidth_1"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_set_mFrontWidth_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_get_mRearWidth_0 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_get_mRearWidth_0"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_get_mRearWidth_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_set_mRearWidth_1 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_set_mRearWidth_1"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_set_mRearWidth_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_get_mAxleSeparation_0 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_get_mAxleSeparation_0"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_get_mAxleSeparation_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData_set_mAxleSeparation_1 = Module["_emscripten_bind_PxVehicleAckermannGeometryData_set_mAxleSeparation_1"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData_set_mAxleSeparation_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAckermannGeometryData___destroy___0 = Module["_emscripten_bind_PxVehicleAckermannGeometryData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleAckermannGeometryData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_PxVehicleAntiRollBarData_0 = Module["_emscripten_bind_PxVehicleAntiRollBarData_PxVehicleAntiRollBarData_0"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_PxVehicleAntiRollBarData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_get_mWheel0_0 = Module["_emscripten_bind_PxVehicleAntiRollBarData_get_mWheel0_0"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_get_mWheel0_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_set_mWheel0_1 = Module["_emscripten_bind_PxVehicleAntiRollBarData_set_mWheel0_1"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_set_mWheel0_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_get_mWheel1_0 = Module["_emscripten_bind_PxVehicleAntiRollBarData_get_mWheel1_0"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_get_mWheel1_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_set_mWheel1_1 = Module["_emscripten_bind_PxVehicleAntiRollBarData_set_mWheel1_1"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_set_mWheel1_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_get_mStiffness_0 = Module["_emscripten_bind_PxVehicleAntiRollBarData_get_mStiffness_0"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_get_mStiffness_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData_set_mStiffness_1 = Module["_emscripten_bind_PxVehicleAntiRollBarData_set_mStiffness_1"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData_set_mStiffness_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAntiRollBarData___destroy___0 = Module["_emscripten_bind_PxVehicleAntiRollBarData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleAntiRollBarData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_PxVehicleAutoBoxData_0 = Module["_emscripten_bind_PxVehicleAutoBoxData_PxVehicleAutoBoxData_0"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_PxVehicleAutoBoxData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_setLatency_1 = Module["_emscripten_bind_PxVehicleAutoBoxData_setLatency_1"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_setLatency_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_getLatency_0 = Module["_emscripten_bind_PxVehicleAutoBoxData_getLatency_0"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_getLatency_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_getUpRatios_1 = Module["_emscripten_bind_PxVehicleAutoBoxData_getUpRatios_1"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_getUpRatios_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_setUpRatios_2 = Module["_emscripten_bind_PxVehicleAutoBoxData_setUpRatios_2"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_setUpRatios_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_getDownRatios_1 = Module["_emscripten_bind_PxVehicleAutoBoxData_getDownRatios_1"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_getDownRatios_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_setDownRatios_2 = Module["_emscripten_bind_PxVehicleAutoBoxData_setDownRatios_2"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_setDownRatios_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_get_mUpRatios_1 = Module["_emscripten_bind_PxVehicleAutoBoxData_get_mUpRatios_1"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_get_mUpRatios_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_set_mUpRatios_2 = Module["_emscripten_bind_PxVehicleAutoBoxData_set_mUpRatios_2"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_set_mUpRatios_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_get_mDownRatios_1 = Module["_emscripten_bind_PxVehicleAutoBoxData_get_mDownRatios_1"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_get_mDownRatios_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData_set_mDownRatios_2 = Module["_emscripten_bind_PxVehicleAutoBoxData_set_mDownRatios_2"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData_set_mDownRatios_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleAutoBoxData___destroy___0 = Module["_emscripten_bind_PxVehicleAutoBoxData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleAutoBoxData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_PxVehicleChassisData_0 = Module["_emscripten_bind_PxVehicleChassisData_PxVehicleChassisData_0"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_PxVehicleChassisData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_get_mMOI_0 = Module["_emscripten_bind_PxVehicleChassisData_get_mMOI_0"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_get_mMOI_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_set_mMOI_1 = Module["_emscripten_bind_PxVehicleChassisData_set_mMOI_1"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_set_mMOI_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_get_mMass_0 = Module["_emscripten_bind_PxVehicleChassisData_get_mMass_0"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_get_mMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_set_mMass_1 = Module["_emscripten_bind_PxVehicleChassisData_set_mMass_1"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_set_mMass_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_get_mCMOffset_0 = Module["_emscripten_bind_PxVehicleChassisData_get_mCMOffset_0"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_get_mCMOffset_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData_set_mCMOffset_1 = Module["_emscripten_bind_PxVehicleChassisData_set_mCMOffset_1"] = createExportWrapper("emscripten_bind_PxVehicleChassisData_set_mCMOffset_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleChassisData___destroy___0 = Module["_emscripten_bind_PxVehicleChassisData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleChassisData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_PxVehicleClutchData_0 = Module["_emscripten_bind_PxVehicleClutchData_PxVehicleClutchData_0"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_PxVehicleClutchData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_get_mStrength_0 = Module["_emscripten_bind_PxVehicleClutchData_get_mStrength_0"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_get_mStrength_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_set_mStrength_1 = Module["_emscripten_bind_PxVehicleClutchData_set_mStrength_1"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_set_mStrength_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_get_mAccuracyMode_0 = Module["_emscripten_bind_PxVehicleClutchData_get_mAccuracyMode_0"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_get_mAccuracyMode_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_set_mAccuracyMode_1 = Module["_emscripten_bind_PxVehicleClutchData_set_mAccuracyMode_1"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_set_mAccuracyMode_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_get_mEstimateIterations_0 = Module["_emscripten_bind_PxVehicleClutchData_get_mEstimateIterations_0"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_get_mEstimateIterations_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData_set_mEstimateIterations_1 = Module["_emscripten_bind_PxVehicleClutchData_set_mEstimateIterations_1"] = createExportWrapper("emscripten_bind_PxVehicleClutchData_set_mEstimateIterations_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleClutchData___destroy___0 = Module["_emscripten_bind_PxVehicleClutchData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleClutchData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_PxVehicleDifferential4WData_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_PxVehicleDifferential4WData_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_PxVehicleDifferential4WData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mFrontRearSplit_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mFrontRearSplit_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mFrontRearSplit_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mFrontRearSplit_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mFrontRearSplit_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mFrontRearSplit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mFrontLeftRightSplit_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mFrontLeftRightSplit_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mFrontLeftRightSplit_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mFrontLeftRightSplit_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mFrontLeftRightSplit_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mFrontLeftRightSplit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mRearLeftRightSplit_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mRearLeftRightSplit_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mRearLeftRightSplit_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mRearLeftRightSplit_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mRearLeftRightSplit_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mRearLeftRightSplit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mCentreBias_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mCentreBias_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mCentreBias_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mCentreBias_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mCentreBias_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mCentreBias_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mFrontBias_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mFrontBias_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mFrontBias_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mFrontBias_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mFrontBias_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mFrontBias_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mRearBias_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mRearBias_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mRearBias_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mRearBias_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mRearBias_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mRearBias_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_get_mType_0 = Module["_emscripten_bind_PxVehicleDifferential4WData_get_mType_0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_get_mType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData_set_mType_1 = Module["_emscripten_bind_PxVehicleDifferential4WData_set_mType_1"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData_set_mType_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDifferential4WData___destroy___0 = Module["_emscripten_bind_PxVehicleDifferential4WData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleDifferential4WData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_allocate_2 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_allocate_2"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_allocate_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setup_4 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setup_4"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setup_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_release_0 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_release_0"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setTypePairFriction_3 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setTypePairFriction_3"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setTypePairFriction_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getTypePairFriction_2 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getTypePairFriction_2"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getTypePairFriction_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbSurfaceTypes_0 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbSurfaceTypes_0"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbSurfaceTypes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbTireTypes_0 = Module["_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbTireTypes_0"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbTireTypes_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceType_PxVehicleDrivableSurfaceType_0 = Module["_emscripten_bind_PxVehicleDrivableSurfaceType_PxVehicleDrivableSurfaceType_0"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceType_PxVehicleDrivableSurfaceType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceType_get_mType_0 = Module["_emscripten_bind_PxVehicleDrivableSurfaceType_get_mType_0"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceType_get_mType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceType_set_mType_1 = Module["_emscripten_bind_PxVehicleDrivableSurfaceType_set_mType_1"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceType_set_mType_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrivableSurfaceType___destroy___0 = Module["_emscripten_bind_PxVehicleDrivableSurfaceType___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleDrivableSurfaceType___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_allocate_1 = Module["_emscripten_bind_PxVehicleDrive4W_allocate_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_allocate_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_free_0 = Module["_emscripten_bind_PxVehicleDrive4W_free_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_free_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_setup_5 = Module["_emscripten_bind_PxVehicleDrive4W_setup_5"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_setup_5");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_setToRestState_0 = Module["_emscripten_bind_PxVehicleDrive4W_setToRestState_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_setToRestState_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_release_0 = Module["_emscripten_bind_PxVehicleDrive4W_release_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_getConcreteTypeName_0 = Module["_emscripten_bind_PxVehicleDrive4W_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_getConcreteType_0 = Module["_emscripten_bind_PxVehicleDrive4W_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_setBaseFlag_2 = Module["_emscripten_bind_PxVehicleDrive4W_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_setBaseFlags_1 = Module["_emscripten_bind_PxVehicleDrive4W_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_getBaseFlags_0 = Module["_emscripten_bind_PxVehicleDrive4W_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_isReleasable_0 = Module["_emscripten_bind_PxVehicleDrive4W_isReleasable_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_getVehicleType_0 = Module["_emscripten_bind_PxVehicleDrive4W_getVehicleType_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_getVehicleType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_getRigidDynamicActor_0 = Module["_emscripten_bind_PxVehicleDrive4W_getRigidDynamicActor_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_getRigidDynamicActor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_computeForwardSpeed_0 = Module["_emscripten_bind_PxVehicleDrive4W_computeForwardSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_computeForwardSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_computeSidewaysSpeed_0 = Module["_emscripten_bind_PxVehicleDrive4W_computeSidewaysSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_computeSidewaysSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_getNbNonDrivenWheels_0 = Module["_emscripten_bind_PxVehicleDrive4W_getNbNonDrivenWheels_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_getNbNonDrivenWheels_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_get_mDriveSimData_0 = Module["_emscripten_bind_PxVehicleDrive4W_get_mDriveSimData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_get_mDriveSimData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_set_mDriveSimData_1 = Module["_emscripten_bind_PxVehicleDrive4W_set_mDriveSimData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_set_mDriveSimData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_get_mDriveDynData_0 = Module["_emscripten_bind_PxVehicleDrive4W_get_mDriveDynData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_get_mDriveDynData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_set_mDriveDynData_1 = Module["_emscripten_bind_PxVehicleDrive4W_set_mDriveDynData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_set_mDriveDynData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_get_mWheelsSimData_0 = Module["_emscripten_bind_PxVehicleDrive4W_get_mWheelsSimData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_get_mWheelsSimData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_set_mWheelsSimData_1 = Module["_emscripten_bind_PxVehicleDrive4W_set_mWheelsSimData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_set_mWheelsSimData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_get_mWheelsDynData_0 = Module["_emscripten_bind_PxVehicleDrive4W_get_mWheelsDynData_0"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_get_mWheelsDynData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDrive4W_set_mWheelsDynData_1 = Module["_emscripten_bind_PxVehicleDrive4W_set_mWheelsDynData_1"] = createExportWrapper("emscripten_bind_PxVehicleDrive4W_set_mWheelsDynData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setToRestState_0 = Module["_emscripten_bind_PxVehicleDriveDynData_setToRestState_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setToRestState_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setAnalogInput_2 = Module["_emscripten_bind_PxVehicleDriveDynData_setAnalogInput_2"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setAnalogInput_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getAnalogInput_1 = Module["_emscripten_bind_PxVehicleDriveDynData_getAnalogInput_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getAnalogInput_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setGearUp_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setGearUp_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setGearUp_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setGearDown_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setGearDown_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setGearDown_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getGearUp_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getGearUp_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getGearUp_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getGearDown_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getGearDown_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getGearDown_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setUseAutoGears_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setUseAutoGears_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setUseAutoGears_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getUseAutoGears_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getUseAutoGears_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getUseAutoGears_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_toggleAutoGears_0 = Module["_emscripten_bind_PxVehicleDriveDynData_toggleAutoGears_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_toggleAutoGears_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setCurrentGear_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setCurrentGear_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setCurrentGear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getCurrentGear_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getCurrentGear_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getCurrentGear_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setTargetGear_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setTargetGear_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setTargetGear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getTargetGear_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getTargetGear_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getTargetGear_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_startGearChange_1 = Module["_emscripten_bind_PxVehicleDriveDynData_startGearChange_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_startGearChange_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_forceGearChange_1 = Module["_emscripten_bind_PxVehicleDriveDynData_forceGearChange_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_forceGearChange_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setEngineRotationSpeed_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setEngineRotationSpeed_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setEngineRotationSpeed_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getEngineRotationSpeed_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getEngineRotationSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getEngineRotationSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getGearSwitchTime_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getGearSwitchTime_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getGearSwitchTime_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getAutoBoxSwitchTime_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getAutoBoxSwitchTime_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getAutoBoxSwitchTime_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getNbAnalogInput_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getNbAnalogInput_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getNbAnalogInput_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setGearChange_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setGearChange_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setGearChange_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_getGearChange_0 = Module["_emscripten_bind_PxVehicleDriveDynData_getGearChange_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_getGearChange_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setGearSwitchTime_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setGearSwitchTime_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setGearSwitchTime_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_setAutoBoxSwitchTime_1 = Module["_emscripten_bind_PxVehicleDriveDynData_setAutoBoxSwitchTime_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_setAutoBoxSwitchTime_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mControlAnalogVals_1 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mControlAnalogVals_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mControlAnalogVals_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mControlAnalogVals_2 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mControlAnalogVals_2"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mControlAnalogVals_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mUseAutoGears_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mUseAutoGears_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mUseAutoGears_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mUseAutoGears_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mUseAutoGears_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mUseAutoGears_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mGearUpPressed_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mGearUpPressed_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mGearUpPressed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mGearUpPressed_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mGearUpPressed_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mGearUpPressed_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mGearDownPressed_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mGearDownPressed_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mGearDownPressed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mGearDownPressed_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mGearDownPressed_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mGearDownPressed_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mCurrentGear_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mCurrentGear_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mCurrentGear_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mCurrentGear_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mCurrentGear_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mCurrentGear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mTargetGear_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mTargetGear_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mTargetGear_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mTargetGear_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mTargetGear_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mTargetGear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mEnginespeed_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mEnginespeed_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mEnginespeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mEnginespeed_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mEnginespeed_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mEnginespeed_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mGearSwitchTime_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mGearSwitchTime_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mGearSwitchTime_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mGearSwitchTime_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mGearSwitchTime_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mGearSwitchTime_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_get_mAutoBoxSwitchTime_0 = Module["_emscripten_bind_PxVehicleDriveDynData_get_mAutoBoxSwitchTime_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_get_mAutoBoxSwitchTime_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData_set_mAutoBoxSwitchTime_1 = Module["_emscripten_bind_PxVehicleDriveDynData_set_mAutoBoxSwitchTime_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData_set_mAutoBoxSwitchTime_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveDynData___destroy___0 = Module["_emscripten_bind_PxVehicleDriveDynData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleDriveDynData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_PxVehicleDriveSimData4W_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_PxVehicleDriveSimData4W_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_PxVehicleDriveSimData4W_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_getDiffData_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_getDiffData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_getDiffData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_getAckermannGeometryData_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_getAckermannGeometryData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_getAckermannGeometryData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_setDiffData_1 = Module["_emscripten_bind_PxVehicleDriveSimData4W_setDiffData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_setDiffData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_setAckermannGeometryData_1 = Module["_emscripten_bind_PxVehicleDriveSimData4W_setAckermannGeometryData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_setAckermannGeometryData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_getEngineData_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_getEngineData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_getEngineData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_setEngineData_1 = Module["_emscripten_bind_PxVehicleDriveSimData4W_setEngineData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_setEngineData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_getGearsData_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_getGearsData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_getGearsData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_setGearsData_1 = Module["_emscripten_bind_PxVehicleDriveSimData4W_setGearsData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_setGearsData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_getClutchData_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_getClutchData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_getClutchData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_setClutchData_1 = Module["_emscripten_bind_PxVehicleDriveSimData4W_setClutchData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_setClutchData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_getAutoBoxData_0 = Module["_emscripten_bind_PxVehicleDriveSimData4W_getAutoBoxData_0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_getAutoBoxData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W_setAutoBoxData_1 = Module["_emscripten_bind_PxVehicleDriveSimData4W_setAutoBoxData_1"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W_setAutoBoxData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleDriveSimData4W___destroy___0 = Module["_emscripten_bind_PxVehicleDriveSimData4W___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleDriveSimData4W___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_PxVehicleEngineData_0 = Module["_emscripten_bind_PxVehicleEngineData_PxVehicleEngineData_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_PxVehicleEngineData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mTorqueCurve_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mTorqueCurve_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mTorqueCurve_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mTorqueCurve_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mTorqueCurve_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mTorqueCurve_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mMOI_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mMOI_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mMOI_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mMOI_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mMOI_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mMOI_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mPeakTorque_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mPeakTorque_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mPeakTorque_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mPeakTorque_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mPeakTorque_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mPeakTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mMaxOmega_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mMaxOmega_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mMaxOmega_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mMaxOmega_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mMaxOmega_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mMaxOmega_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mDampingRateFullThrottle_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mDampingRateFullThrottle_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mDampingRateFullThrottle_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mDampingRateFullThrottle_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mDampingRateFullThrottle_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mDampingRateFullThrottle_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchEngaged_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchEngaged_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchEngaged_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchEngaged_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchEngaged_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchEngaged_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchDisengaged_0 = Module["_emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchDisengaged_0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchDisengaged_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchDisengaged_1 = Module["_emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchDisengaged_1"] = createExportWrapper("emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchDisengaged_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleEngineData___destroy___0 = Module["_emscripten_bind_PxVehicleEngineData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleEngineData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_PxEngineTorqueLookupTable_0 = Module["_emscripten_bind_PxEngineTorqueLookupTable_PxEngineTorqueLookupTable_0"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_PxEngineTorqueLookupTable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_addPair_2 = Module["_emscripten_bind_PxEngineTorqueLookupTable_addPair_2"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_addPair_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_getYVal_1 = Module["_emscripten_bind_PxEngineTorqueLookupTable_getYVal_1"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_getYVal_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_getNbDataPairs_0 = Module["_emscripten_bind_PxEngineTorqueLookupTable_getNbDataPairs_0"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_getNbDataPairs_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_clear_0 = Module["_emscripten_bind_PxEngineTorqueLookupTable_clear_0"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_clear_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_getX_1 = Module["_emscripten_bind_PxEngineTorqueLookupTable_getX_1"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_getX_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_getY_1 = Module["_emscripten_bind_PxEngineTorqueLookupTable_getY_1"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_getY_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_get_mDataPairs_1 = Module["_emscripten_bind_PxEngineTorqueLookupTable_get_mDataPairs_1"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_get_mDataPairs_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_set_mDataPairs_2 = Module["_emscripten_bind_PxEngineTorqueLookupTable_set_mDataPairs_2"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_set_mDataPairs_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_get_mNbDataPairs_0 = Module["_emscripten_bind_PxEngineTorqueLookupTable_get_mNbDataPairs_0"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_get_mNbDataPairs_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable_set_mNbDataPairs_1 = Module["_emscripten_bind_PxEngineTorqueLookupTable_set_mNbDataPairs_1"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable_set_mNbDataPairs_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxEngineTorqueLookupTable___destroy___0 = Module["_emscripten_bind_PxEngineTorqueLookupTable___destroy___0"] = createExportWrapper("emscripten_bind_PxEngineTorqueLookupTable___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_PxVehicleGearsData_0 = Module["_emscripten_bind_PxVehicleGearsData_PxVehicleGearsData_0"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_PxVehicleGearsData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_getGearRatio_1 = Module["_emscripten_bind_PxVehicleGearsData_getGearRatio_1"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_getGearRatio_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_setGearRatio_2 = Module["_emscripten_bind_PxVehicleGearsData_setGearRatio_2"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_setGearRatio_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_get_mRatios_1 = Module["_emscripten_bind_PxVehicleGearsData_get_mRatios_1"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_get_mRatios_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_set_mRatios_2 = Module["_emscripten_bind_PxVehicleGearsData_set_mRatios_2"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_set_mRatios_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_get_mFinalRatio_0 = Module["_emscripten_bind_PxVehicleGearsData_get_mFinalRatio_0"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_get_mFinalRatio_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_set_mFinalRatio_1 = Module["_emscripten_bind_PxVehicleGearsData_set_mFinalRatio_1"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_set_mFinalRatio_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_get_mNbRatios_0 = Module["_emscripten_bind_PxVehicleGearsData_get_mNbRatios_0"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_get_mNbRatios_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_set_mNbRatios_1 = Module["_emscripten_bind_PxVehicleGearsData_set_mNbRatios_1"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_set_mNbRatios_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_get_mSwitchTime_0 = Module["_emscripten_bind_PxVehicleGearsData_get_mSwitchTime_0"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_get_mSwitchTime_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData_set_mSwitchTime_1 = Module["_emscripten_bind_PxVehicleGearsData_set_mSwitchTime_1"] = createExportWrapper("emscripten_bind_PxVehicleGearsData_set_mSwitchTime_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleGearsData___destroy___0 = Module["_emscripten_bind_PxVehicleGearsData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleGearsData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_PxVehicleSuspensionData_0 = Module["_emscripten_bind_PxVehicleSuspensionData_PxVehicleSuspensionData_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_PxVehicleSuspensionData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_setMassAndPreserveNaturalFrequency_1 = Module["_emscripten_bind_PxVehicleSuspensionData_setMassAndPreserveNaturalFrequency_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_setMassAndPreserveNaturalFrequency_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mSpringStrength_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mSpringStrength_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mSpringStrength_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mSpringStrength_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mSpringStrength_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mSpringStrength_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mSpringDamperRate_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mSpringDamperRate_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mSpringDamperRate_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mSpringDamperRate_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mSpringDamperRate_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mSpringDamperRate_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mMaxCompression_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mMaxCompression_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mMaxCompression_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mMaxCompression_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mMaxCompression_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mMaxCompression_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mMaxDroop_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mMaxDroop_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mMaxDroop_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mMaxDroop_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mMaxDroop_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mMaxDroop_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mSprungMass_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mSprungMass_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mSprungMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mSprungMass_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mSprungMass_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mSprungMass_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mCamberAtRest_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mCamberAtRest_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mCamberAtRest_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mCamberAtRest_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mCamberAtRest_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mCamberAtRest_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxCompression_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxCompression_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxCompression_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxCompression_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxCompression_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxCompression_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxDroop_0 = Module["_emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxDroop_0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxDroop_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxDroop_1 = Module["_emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxDroop_1"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxDroop_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleSuspensionData___destroy___0 = Module["_emscripten_bind_PxVehicleSuspensionData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleSuspensionData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_PxVehicleTireData_0 = Module["_emscripten_bind_PxVehicleTireData_PxVehicleTireData_0"] = createExportWrapper("emscripten_bind_PxVehicleTireData_PxVehicleTireData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_get_mLatStiffX_0 = Module["_emscripten_bind_PxVehicleTireData_get_mLatStiffX_0"] = createExportWrapper("emscripten_bind_PxVehicleTireData_get_mLatStiffX_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_set_mLatStiffX_1 = Module["_emscripten_bind_PxVehicleTireData_set_mLatStiffX_1"] = createExportWrapper("emscripten_bind_PxVehicleTireData_set_mLatStiffX_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_get_mLatStiffY_0 = Module["_emscripten_bind_PxVehicleTireData_get_mLatStiffY_0"] = createExportWrapper("emscripten_bind_PxVehicleTireData_get_mLatStiffY_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_set_mLatStiffY_1 = Module["_emscripten_bind_PxVehicleTireData_set_mLatStiffY_1"] = createExportWrapper("emscripten_bind_PxVehicleTireData_set_mLatStiffY_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_get_mLongitudinalStiffnessPerUnitGravity_0 = Module["_emscripten_bind_PxVehicleTireData_get_mLongitudinalStiffnessPerUnitGravity_0"] = createExportWrapper("emscripten_bind_PxVehicleTireData_get_mLongitudinalStiffnessPerUnitGravity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_set_mLongitudinalStiffnessPerUnitGravity_1 = Module["_emscripten_bind_PxVehicleTireData_set_mLongitudinalStiffnessPerUnitGravity_1"] = createExportWrapper("emscripten_bind_PxVehicleTireData_set_mLongitudinalStiffnessPerUnitGravity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_get_mCamberStiffnessPerUnitGravity_0 = Module["_emscripten_bind_PxVehicleTireData_get_mCamberStiffnessPerUnitGravity_0"] = createExportWrapper("emscripten_bind_PxVehicleTireData_get_mCamberStiffnessPerUnitGravity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_set_mCamberStiffnessPerUnitGravity_1 = Module["_emscripten_bind_PxVehicleTireData_set_mCamberStiffnessPerUnitGravity_1"] = createExportWrapper("emscripten_bind_PxVehicleTireData_set_mCamberStiffnessPerUnitGravity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_get_mType_0 = Module["_emscripten_bind_PxVehicleTireData_get_mType_0"] = createExportWrapper("emscripten_bind_PxVehicleTireData_get_mType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData_set_mType_1 = Module["_emscripten_bind_PxVehicleTireData_set_mType_1"] = createExportWrapper("emscripten_bind_PxVehicleTireData_set_mType_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireData___destroy___0 = Module["_emscripten_bind_PxVehicleTireData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleTireData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_PxVehicleTireLoadFilterData_0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_PxVehicleTireLoadFilterData_0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_PxVehicleTireLoadFilterData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_getDenominator_0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_getDenominator_0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_getDenominator_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_get_mMinNormalisedLoad_0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_get_mMinNormalisedLoad_0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_get_mMinNormalisedLoad_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_set_mMinNormalisedLoad_1 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_set_mMinNormalisedLoad_1"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_set_mMinNormalisedLoad_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_get_mMinFilteredNormalisedLoad_0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_get_mMinFilteredNormalisedLoad_0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_get_mMinFilteredNormalisedLoad_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_set_mMinFilteredNormalisedLoad_1 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_set_mMinFilteredNormalisedLoad_1"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_set_mMinFilteredNormalisedLoad_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxNormalisedLoad_0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxNormalisedLoad_0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxNormalisedLoad_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxNormalisedLoad_1 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxNormalisedLoad_1"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxNormalisedLoad_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxFilteredNormalisedLoad_0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxFilteredNormalisedLoad_0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxFilteredNormalisedLoad_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxFilteredNormalisedLoad_1 = Module["_emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxFilteredNormalisedLoad_1"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxFilteredNormalisedLoad_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleTireLoadFilterData___destroy___0 = Module["_emscripten_bind_PxVehicleTireLoadFilterData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleTireLoadFilterData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_PxVehicleWheelData_0 = Module["_emscripten_bind_PxVehicleWheelData_PxVehicleWheelData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_PxVehicleWheelData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mRadius_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mRadius_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mRadius_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mRadius_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mRadius_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mRadius_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mWidth_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mWidth_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mWidth_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mWidth_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mWidth_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mWidth_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mMass_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mMass_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mMass_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mMass_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mMass_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mMass_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mMOI_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mMOI_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mMOI_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mMOI_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mMOI_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mMOI_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mDampingRate_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mDampingRate_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mDampingRate_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mDampingRate_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mDampingRate_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mDampingRate_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mMaxBrakeTorque_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mMaxBrakeTorque_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mMaxBrakeTorque_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mMaxBrakeTorque_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mMaxBrakeTorque_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mMaxBrakeTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mMaxHandBrakeTorque_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mMaxHandBrakeTorque_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mMaxHandBrakeTorque_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mMaxHandBrakeTorque_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mMaxHandBrakeTorque_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mMaxHandBrakeTorque_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mMaxSteer_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mMaxSteer_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mMaxSteer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mMaxSteer_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mMaxSteer_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mMaxSteer_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_get_mToeAngle_0 = Module["_emscripten_bind_PxVehicleWheelData_get_mToeAngle_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_get_mToeAngle_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData_set_mToeAngle_1 = Module["_emscripten_bind_PxVehicleWheelData_set_mToeAngle_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelData_set_mToeAngle_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelData___destroy___0 = Module["_emscripten_bind_PxVehicleWheelData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleWheelData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelQueryResult_PxVehicleWheelQueryResult_0 = Module["_emscripten_bind_PxVehicleWheelQueryResult_PxVehicleWheelQueryResult_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelQueryResult_PxVehicleWheelQueryResult_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelQueryResult_get_wheelQueryResults_0 = Module["_emscripten_bind_PxVehicleWheelQueryResult_get_wheelQueryResults_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelQueryResult_get_wheelQueryResults_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelQueryResult_set_wheelQueryResults_1 = Module["_emscripten_bind_PxVehicleWheelQueryResult_set_wheelQueryResults_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelQueryResult_set_wheelQueryResults_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelQueryResult_get_nbWheelQueryResults_0 = Module["_emscripten_bind_PxVehicleWheelQueryResult_get_nbWheelQueryResults_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelQueryResult_get_nbWheelQueryResults_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelQueryResult_set_nbWheelQueryResults_1 = Module["_emscripten_bind_PxVehicleWheelQueryResult_set_nbWheelQueryResults_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelQueryResult_set_nbWheelQueryResults_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_setToRestState_0 = Module["_emscripten_bind_PxVehicleWheelsDynData_setToRestState_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_setToRestState_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_setWheelRotationSpeed_2 = Module["_emscripten_bind_PxVehicleWheelsDynData_setWheelRotationSpeed_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_setWheelRotationSpeed_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_getWheelRotationSpeed_1 = Module["_emscripten_bind_PxVehicleWheelsDynData_getWheelRotationSpeed_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_getWheelRotationSpeed_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_setWheelRotationAngle_2 = Module["_emscripten_bind_PxVehicleWheelsDynData_setWheelRotationAngle_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_setWheelRotationAngle_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_getWheelRotationAngle_1 = Module["_emscripten_bind_PxVehicleWheelsDynData_getWheelRotationAngle_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_getWheelRotationAngle_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_copy_3 = Module["_emscripten_bind_PxVehicleWheelsDynData_copy_3"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_copy_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationSpeed_0 = Module["_emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationAngle_0 = Module["_emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationAngle_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationAngle_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsDynData___destroy___0 = Module["_emscripten_bind_PxVehicleWheelsDynData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsDynData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_allocate_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_allocate_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_allocate_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setChassisMass_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setChassisMass_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setChassisMass_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_free_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_free_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_free_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_copy_3 = Module["_emscripten_bind_PxVehicleWheelsSimData_copy_3"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_copy_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbWheels_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbWheels_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbWheels_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getSuspensionData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getSuspensionData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getSuspensionData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getWheelData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getWheelData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getWheelData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getTireData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getTireData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getTireData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getSuspTravelDirection_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getSuspTravelDirection_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getSuspTravelDirection_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getSuspForceAppPointOffset_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getSuspForceAppPointOffset_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getSuspForceAppPointOffset_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getTireForceAppPointOffset_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getTireForceAppPointOffset_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getTireForceAppPointOffset_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getWheelCentreOffset_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getWheelCentreOffset_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getWheelCentreOffset_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getWheelShapeMapping_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getWheelShapeMapping_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getWheelShapeMapping_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getSceneQueryFilterData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getSceneQueryFilterData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getSceneQueryFilterData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getAntiRollBarData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getAntiRollBarData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getAntiRollBarData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getTireLoadFilterData_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getTireLoadFilterData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getTireLoadFilterData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setSuspensionData_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setSuspensionData_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setSuspensionData_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setWheelData_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setWheelData_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setWheelData_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setTireData_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setTireData_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setTireData_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setSuspTravelDirection_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setSuspTravelDirection_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setSuspTravelDirection_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setSuspForceAppPointOffset_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setSuspForceAppPointOffset_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setSuspForceAppPointOffset_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setTireForceAppPointOffset_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setTireForceAppPointOffset_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setTireForceAppPointOffset_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setWheelCentreOffset_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setWheelCentreOffset_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setWheelCentreOffset_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setWheelShapeMapping_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setWheelShapeMapping_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setWheelShapeMapping_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setSceneQueryFilterData_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setSceneQueryFilterData_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setSceneQueryFilterData_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setTireLoadFilterData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setTireLoadFilterData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setTireLoadFilterData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_addAntiRollBarData_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_addAntiRollBarData_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_addAntiRollBarData_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_disableWheel_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_disableWheel_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_disableWheel_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_enableWheel_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_enableWheel_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_enableWheel_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getIsWheelDisabled_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getIsWheelDisabled_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getIsWheelDisabled_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setSubStepCount_3 = Module["_emscripten_bind_PxVehicleWheelsSimData_setSubStepCount_3"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setSubStepCount_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setMinLongSlipDenominator_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setMinLongSlipDenominator_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setMinLongSlipDenominator_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setFlags_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setFlags_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getFlags_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getFlags_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbWheels4_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbWheels4_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbWheels4_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbSuspensionData_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbSuspensionData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbSuspensionData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbWheelData_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbWheelData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbWheelData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbSuspTravelDirection_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbSuspTravelDirection_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbSuspTravelDirection_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbTireData_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbTireData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbTireData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbSuspForceAppPointOffset_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbSuspForceAppPointOffset_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbSuspForceAppPointOffset_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbTireForceAppPointOffset_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbTireForceAppPointOffset_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbTireForceAppPointOffset_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbWheelCentreOffset_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbWheelCentreOffset_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbWheelCentreOffset_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbWheelShapeMapping_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbWheelShapeMapping_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbWheelShapeMapping_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbSceneQueryFilterData_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbSceneQueryFilterData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbSceneQueryFilterData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getMinLongSlipDenominator_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getMinLongSlipDenominator_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getMinLongSlipDenominator_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setThresholdLongSpeed_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setThresholdLongSpeed_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setThresholdLongSpeed_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getThresholdLongSpeed_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getThresholdLongSpeed_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getThresholdLongSpeed_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setLowForwardSpeedSubStepCount_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setLowForwardSpeedSubStepCount_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setLowForwardSpeedSubStepCount_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getLowForwardSpeedSubStepCount_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getLowForwardSpeedSubStepCount_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getLowForwardSpeedSubStepCount_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setHighForwardSpeedSubStepCount_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_setHighForwardSpeedSubStepCount_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setHighForwardSpeedSubStepCount_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getHighForwardSpeedSubStepCount_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getHighForwardSpeedSubStepCount_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getHighForwardSpeedSubStepCount_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setWheelEnabledState_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setWheelEnabledState_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setWheelEnabledState_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getWheelEnabledState_1 = Module["_emscripten_bind_PxVehicleWheelsSimData_getWheelEnabledState_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getWheelEnabledState_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbWheelEnabledState_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbWheelEnabledState_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbWheelEnabledState_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars4_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars4_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars4_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBarData_0 = Module["_emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBarData_0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBarData_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData_setAntiRollBarData_2 = Module["_emscripten_bind_PxVehicleWheelsSimData_setAntiRollBarData_2"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData_setAntiRollBarData_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimData___destroy___0 = Module["_emscripten_bind_PxVehicleWheelsSimData___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimFlags_PxVehicleWheelsSimFlags_1 = Module["_emscripten_bind_PxVehicleWheelsSimFlags_PxVehicleWheelsSimFlags_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimFlags_PxVehicleWheelsSimFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimFlags_isSet_1 = Module["_emscripten_bind_PxVehicleWheelsSimFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimFlags_set_1 = Module["_emscripten_bind_PxVehicleWheelsSimFlags_set_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimFlags_clear_1 = Module["_emscripten_bind_PxVehicleWheelsSimFlags_clear_1"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsSimFlags___destroy___0 = Module["_emscripten_bind_PxVehicleWheelsSimFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsSimFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_PxWheelQueryResult_0 = Module["_emscripten_bind_PxWheelQueryResult_PxWheelQueryResult_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_PxWheelQueryResult_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_suspLineStart_0 = Module["_emscripten_bind_PxWheelQueryResult_get_suspLineStart_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_suspLineStart_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_suspLineStart_1 = Module["_emscripten_bind_PxWheelQueryResult_set_suspLineStart_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_suspLineStart_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_suspLineDir_0 = Module["_emscripten_bind_PxWheelQueryResult_get_suspLineDir_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_suspLineDir_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_suspLineDir_1 = Module["_emscripten_bind_PxWheelQueryResult_set_suspLineDir_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_suspLineDir_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_suspLineLength_0 = Module["_emscripten_bind_PxWheelQueryResult_get_suspLineLength_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_suspLineLength_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_suspLineLength_1 = Module["_emscripten_bind_PxWheelQueryResult_set_suspLineLength_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_suspLineLength_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_isInAir_0 = Module["_emscripten_bind_PxWheelQueryResult_get_isInAir_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_isInAir_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_isInAir_1 = Module["_emscripten_bind_PxWheelQueryResult_set_isInAir_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_isInAir_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireContactActor_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireContactActor_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireContactActor_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireContactActor_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireContactActor_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireContactActor_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireContactShape_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireContactShape_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireContactShape_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireContactShape_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireContactShape_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireContactShape_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireSurfaceMaterial_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireSurfaceMaterial_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireSurfaceMaterial_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireSurfaceMaterial_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireSurfaceMaterial_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireSurfaceMaterial_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireSurfaceType_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireSurfaceType_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireSurfaceType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireSurfaceType_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireSurfaceType_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireSurfaceType_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireContactPoint_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireContactPoint_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireContactPoint_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireContactPoint_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireContactPoint_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireContactPoint_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireContactNormal_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireContactNormal_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireContactNormal_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireContactNormal_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireContactNormal_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireContactNormal_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireFriction_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireFriction_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireFriction_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireFriction_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireFriction_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireFriction_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_suspJounce_0 = Module["_emscripten_bind_PxWheelQueryResult_get_suspJounce_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_suspJounce_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_suspJounce_1 = Module["_emscripten_bind_PxWheelQueryResult_set_suspJounce_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_suspJounce_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_suspSpringForce_0 = Module["_emscripten_bind_PxWheelQueryResult_get_suspSpringForce_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_suspSpringForce_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_suspSpringForce_1 = Module["_emscripten_bind_PxWheelQueryResult_set_suspSpringForce_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_suspSpringForce_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireLongitudinalDir_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireLongitudinalDir_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireLongitudinalDir_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireLongitudinalDir_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireLongitudinalDir_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireLongitudinalDir_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_tireLateralDir_0 = Module["_emscripten_bind_PxWheelQueryResult_get_tireLateralDir_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_tireLateralDir_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_tireLateralDir_1 = Module["_emscripten_bind_PxWheelQueryResult_set_tireLateralDir_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_tireLateralDir_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_longitudinalSlip_0 = Module["_emscripten_bind_PxWheelQueryResult_get_longitudinalSlip_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_longitudinalSlip_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_longitudinalSlip_1 = Module["_emscripten_bind_PxWheelQueryResult_set_longitudinalSlip_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_longitudinalSlip_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_lateralSlip_0 = Module["_emscripten_bind_PxWheelQueryResult_get_lateralSlip_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_lateralSlip_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_lateralSlip_1 = Module["_emscripten_bind_PxWheelQueryResult_set_lateralSlip_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_lateralSlip_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_steerAngle_0 = Module["_emscripten_bind_PxWheelQueryResult_get_steerAngle_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_steerAngle_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_steerAngle_1 = Module["_emscripten_bind_PxWheelQueryResult_set_steerAngle_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_steerAngle_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_get_localPose_0 = Module["_emscripten_bind_PxWheelQueryResult_get_localPose_0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_get_localPose_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult_set_localPose_1 = Module["_emscripten_bind_PxWheelQueryResult_set_localPose_1"] = createExportWrapper("emscripten_bind_PxWheelQueryResult_set_localPose_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxWheelQueryResult___destroy___0 = Module["_emscripten_bind_PxWheelQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_PxWheelQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoxGeometry_PxBoxGeometry_3 = Module["_emscripten_bind_PxBoxGeometry_PxBoxGeometry_3"] = createExportWrapper("emscripten_bind_PxBoxGeometry_PxBoxGeometry_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoxGeometry___destroy___0 = Module["_emscripten_bind_PxBoxGeometry___destroy___0"] = createExportWrapper("emscripten_bind_PxBoxGeometry___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_release_0 = Module["_emscripten_bind_PxBVHStructure_release_0"] = createExportWrapper("emscripten_bind_PxBVHStructure_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_getConcreteTypeName_0 = Module["_emscripten_bind_PxBVHStructure_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxBVHStructure_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_getConcreteType_0 = Module["_emscripten_bind_PxBVHStructure_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxBVHStructure_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_setBaseFlag_2 = Module["_emscripten_bind_PxBVHStructure_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxBVHStructure_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_setBaseFlags_1 = Module["_emscripten_bind_PxBVHStructure_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxBVHStructure_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_getBaseFlags_0 = Module["_emscripten_bind_PxBVHStructure_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxBVHStructure_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBVHStructure_isReleasable_0 = Module["_emscripten_bind_PxBVHStructure_isReleasable_0"] = createExportWrapper("emscripten_bind_PxBVHStructure_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxCapsuleGeometry_PxCapsuleGeometry_2 = Module["_emscripten_bind_PxCapsuleGeometry_PxCapsuleGeometry_2"] = createExportWrapper("emscripten_bind_PxCapsuleGeometry_PxCapsuleGeometry_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxCapsuleGeometry___destroy___0 = Module["_emscripten_bind_PxCapsuleGeometry___destroy___0"] = createExportWrapper("emscripten_bind_PxCapsuleGeometry___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getNbVertices_0 = Module["_emscripten_bind_PxConvexMesh_getNbVertices_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getNbVertices_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getVertices_0 = Module["_emscripten_bind_PxConvexMesh_getVertices_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getVertices_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getIndexBuffer_0 = Module["_emscripten_bind_PxConvexMesh_getIndexBuffer_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getIndexBuffer_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getNbPolygons_0 = Module["_emscripten_bind_PxConvexMesh_getNbPolygons_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getNbPolygons_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getPolygonData_2 = Module["_emscripten_bind_PxConvexMesh_getPolygonData_2"] = createExportWrapper("emscripten_bind_PxConvexMesh_getPolygonData_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getReferenceCount_0 = Module["_emscripten_bind_PxConvexMesh_getReferenceCount_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getReferenceCount_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_acquireReference_0 = Module["_emscripten_bind_PxConvexMesh_acquireReference_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_acquireReference_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getLocalBounds_0 = Module["_emscripten_bind_PxConvexMesh_getLocalBounds_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getLocalBounds_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_isGpuCompatible_0 = Module["_emscripten_bind_PxConvexMesh_isGpuCompatible_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_isGpuCompatible_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_release_0 = Module["_emscripten_bind_PxConvexMesh_release_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getConcreteTypeName_0 = Module["_emscripten_bind_PxConvexMesh_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getConcreteType_0 = Module["_emscripten_bind_PxConvexMesh_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_setBaseFlag_2 = Module["_emscripten_bind_PxConvexMesh_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxConvexMesh_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_setBaseFlags_1 = Module["_emscripten_bind_PxConvexMesh_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxConvexMesh_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_getBaseFlags_0 = Module["_emscripten_bind_PxConvexMesh_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMesh_isReleasable_0 = Module["_emscripten_bind_PxConvexMesh_isReleasable_0"] = createExportWrapper("emscripten_bind_PxConvexMesh_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_1 = Module["_emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_1"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_2 = Module["_emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_2"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_3 = Module["_emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_3"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometry___destroy___0 = Module["_emscripten_bind_PxConvexMeshGeometry___destroy___0"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometry___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometryFlags_PxConvexMeshGeometryFlags_1 = Module["_emscripten_bind_PxConvexMeshGeometryFlags_PxConvexMeshGeometryFlags_1"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometryFlags_PxConvexMeshGeometryFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometryFlags_isSet_1 = Module["_emscripten_bind_PxConvexMeshGeometryFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometryFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometryFlags_set_1 = Module["_emscripten_bind_PxConvexMeshGeometryFlags_set_1"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometryFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometryFlags_clear_1 = Module["_emscripten_bind_PxConvexMeshGeometryFlags_clear_1"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometryFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshGeometryFlags___destroy___0 = Module["_emscripten_bind_PxConvexMeshGeometryFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxConvexMeshGeometryFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_PxHullPolygon_0 = Module["_emscripten_bind_PxHullPolygon_PxHullPolygon_0"] = createExportWrapper("emscripten_bind_PxHullPolygon_PxHullPolygon_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_get_mPlane_1 = Module["_emscripten_bind_PxHullPolygon_get_mPlane_1"] = createExportWrapper("emscripten_bind_PxHullPolygon_get_mPlane_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_set_mPlane_2 = Module["_emscripten_bind_PxHullPolygon_set_mPlane_2"] = createExportWrapper("emscripten_bind_PxHullPolygon_set_mPlane_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_get_mNbVerts_0 = Module["_emscripten_bind_PxHullPolygon_get_mNbVerts_0"] = createExportWrapper("emscripten_bind_PxHullPolygon_get_mNbVerts_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_set_mNbVerts_1 = Module["_emscripten_bind_PxHullPolygon_set_mNbVerts_1"] = createExportWrapper("emscripten_bind_PxHullPolygon_set_mNbVerts_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_get_mIndexBase_0 = Module["_emscripten_bind_PxHullPolygon_get_mIndexBase_0"] = createExportWrapper("emscripten_bind_PxHullPolygon_get_mIndexBase_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon_set_mIndexBase_1 = Module["_emscripten_bind_PxHullPolygon_set_mIndexBase_1"] = createExportWrapper("emscripten_bind_PxHullPolygon_set_mIndexBase_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxHullPolygon___destroy___0 = Module["_emscripten_bind_PxHullPolygon___destroy___0"] = createExportWrapper("emscripten_bind_PxHullPolygon___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMeshScale_PxMeshScale_0 = Module["_emscripten_bind_PxMeshScale_PxMeshScale_0"] = createExportWrapper("emscripten_bind_PxMeshScale_PxMeshScale_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMeshScale_PxMeshScale_1 = Module["_emscripten_bind_PxMeshScale_PxMeshScale_1"] = createExportWrapper("emscripten_bind_PxMeshScale_PxMeshScale_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxMeshScale_PxMeshScale_2 = Module["_emscripten_bind_PxMeshScale_PxMeshScale_2"] = createExportWrapper("emscripten_bind_PxMeshScale_PxMeshScale_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxMeshScale___destroy___0 = Module["_emscripten_bind_PxMeshScale___destroy___0"] = createExportWrapper("emscripten_bind_PxMeshScale___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPlaneGeometry_PxPlaneGeometry_0 = Module["_emscripten_bind_PxPlaneGeometry_PxPlaneGeometry_0"] = createExportWrapper("emscripten_bind_PxPlaneGeometry_PxPlaneGeometry_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxPlaneGeometry___destroy___0 = Module["_emscripten_bind_PxPlaneGeometry___destroy___0"] = createExportWrapper("emscripten_bind_PxPlaneGeometry___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxSphereGeometry_PxSphereGeometry_1 = Module["_emscripten_bind_PxSphereGeometry_PxSphereGeometry_1"] = createExportWrapper("emscripten_bind_PxSphereGeometry_PxSphereGeometry_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxSphereGeometry___destroy___0 = Module["_emscripten_bind_PxSphereGeometry___destroy___0"] = createExportWrapper("emscripten_bind_PxSphereGeometry___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexFlags_PxConvexFlags_1 = Module["_emscripten_bind_PxConvexFlags_PxConvexFlags_1"] = createExportWrapper("emscripten_bind_PxConvexFlags_PxConvexFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexFlags_isSet_1 = Module["_emscripten_bind_PxConvexFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxConvexFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexFlags_set_1 = Module["_emscripten_bind_PxConvexFlags_set_1"] = createExportWrapper("emscripten_bind_PxConvexFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexFlags_clear_1 = Module["_emscripten_bind_PxConvexFlags_clear_1"] = createExportWrapper("emscripten_bind_PxConvexFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexFlags___destroy___0 = Module["_emscripten_bind_PxConvexFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxConvexFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshDesc_PxConvexMeshDesc_0 = Module["_emscripten_bind_PxConvexMeshDesc_PxConvexMeshDesc_0"] = createExportWrapper("emscripten_bind_PxConvexMeshDesc_PxConvexMeshDesc_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshDesc_get_points_0 = Module["_emscripten_bind_PxConvexMeshDesc_get_points_0"] = createExportWrapper("emscripten_bind_PxConvexMeshDesc_get_points_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshDesc_set_points_1 = Module["_emscripten_bind_PxConvexMeshDesc_set_points_1"] = createExportWrapper("emscripten_bind_PxConvexMeshDesc_set_points_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshDesc_get_flags_0 = Module["_emscripten_bind_PxConvexMeshDesc_get_flags_0"] = createExportWrapper("emscripten_bind_PxConvexMeshDesc_get_flags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshDesc_set_flags_1 = Module["_emscripten_bind_PxConvexMeshDesc_set_flags_1"] = createExportWrapper("emscripten_bind_PxConvexMeshDesc_set_flags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxConvexMeshDesc___destroy___0 = Module["_emscripten_bind_PxConvexMeshDesc___destroy___0"] = createExportWrapper("emscripten_bind_PxConvexMeshDesc___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxCooking_createConvexMesh_2 = Module["_emscripten_bind_PxCooking_createConvexMesh_2"] = createExportWrapper("emscripten_bind_PxCooking_createConvexMesh_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxCookingParams_PxCookingParams_1 = Module["_emscripten_bind_PxCookingParams_PxCookingParams_1"] = createExportWrapper("emscripten_bind_PxCookingParams_PxCookingParams_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxCookingParams___destroy___0 = Module["_emscripten_bind_PxCookingParams___destroy___0"] = createExportWrapper("emscripten_bind_PxCookingParams___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBaseFlags_PxBaseFlags_1 = Module["_emscripten_bind_PxBaseFlags_PxBaseFlags_1"] = createExportWrapper("emscripten_bind_PxBaseFlags_PxBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBaseFlags_isSet_1 = Module["_emscripten_bind_PxBaseFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxBaseFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBaseFlags_set_1 = Module["_emscripten_bind_PxBaseFlags_set_1"] = createExportWrapper("emscripten_bind_PxBaseFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBaseFlags_clear_1 = Module["_emscripten_bind_PxBaseFlags_clear_1"] = createExportWrapper("emscripten_bind_PxBaseFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBaseFlags___destroy___0 = Module["_emscripten_bind_PxBaseFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxBaseFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBaseTask___destroy___0 = Module["_emscripten_bind_PxBaseTask___destroy___0"] = createExportWrapper("emscripten_bind_PxBaseTask___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData_get_count_0 = Module["_emscripten_bind_PxBoundedData_get_count_0"] = createExportWrapper("emscripten_bind_PxBoundedData_get_count_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData_set_count_1 = Module["_emscripten_bind_PxBoundedData_set_count_1"] = createExportWrapper("emscripten_bind_PxBoundedData_set_count_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData_get_stride_0 = Module["_emscripten_bind_PxBoundedData_get_stride_0"] = createExportWrapper("emscripten_bind_PxBoundedData_get_stride_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData_set_stride_1 = Module["_emscripten_bind_PxBoundedData_set_stride_1"] = createExportWrapper("emscripten_bind_PxBoundedData_set_stride_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData_get_data_0 = Module["_emscripten_bind_PxBoundedData_get_data_0"] = createExportWrapper("emscripten_bind_PxBoundedData_get_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData_set_data_1 = Module["_emscripten_bind_PxBoundedData_set_data_1"] = createExportWrapper("emscripten_bind_PxBoundedData_set_data_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBoundedData___destroy___0 = Module["_emscripten_bind_PxBoundedData___destroy___0"] = createExportWrapper("emscripten_bind_PxBoundedData___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_PxBounds3_0 = Module["_emscripten_bind_PxBounds3_PxBounds3_0"] = createExportWrapper("emscripten_bind_PxBounds3_PxBounds3_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_PxBounds3_2 = Module["_emscripten_bind_PxBounds3_PxBounds3_2"] = createExportWrapper("emscripten_bind_PxBounds3_PxBounds3_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_setEmpty_0 = Module["_emscripten_bind_PxBounds3_setEmpty_0"] = createExportWrapper("emscripten_bind_PxBounds3_setEmpty_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_setMaximal_0 = Module["_emscripten_bind_PxBounds3_setMaximal_0"] = createExportWrapper("emscripten_bind_PxBounds3_setMaximal_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_include_1 = Module["_emscripten_bind_PxBounds3_include_1"] = createExportWrapper("emscripten_bind_PxBounds3_include_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_isEmpty_0 = Module["_emscripten_bind_PxBounds3_isEmpty_0"] = createExportWrapper("emscripten_bind_PxBounds3_isEmpty_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_intersects_1 = Module["_emscripten_bind_PxBounds3_intersects_1"] = createExportWrapper("emscripten_bind_PxBounds3_intersects_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_intersects1D_2 = Module["_emscripten_bind_PxBounds3_intersects1D_2"] = createExportWrapper("emscripten_bind_PxBounds3_intersects1D_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_contains_1 = Module["_emscripten_bind_PxBounds3_contains_1"] = createExportWrapper("emscripten_bind_PxBounds3_contains_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_isInside_1 = Module["_emscripten_bind_PxBounds3_isInside_1"] = createExportWrapper("emscripten_bind_PxBounds3_isInside_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_getCenter_0 = Module["_emscripten_bind_PxBounds3_getCenter_0"] = createExportWrapper("emscripten_bind_PxBounds3_getCenter_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_getDimensions_0 = Module["_emscripten_bind_PxBounds3_getDimensions_0"] = createExportWrapper("emscripten_bind_PxBounds3_getDimensions_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_getExtents_0 = Module["_emscripten_bind_PxBounds3_getExtents_0"] = createExportWrapper("emscripten_bind_PxBounds3_getExtents_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_scaleSafe_1 = Module["_emscripten_bind_PxBounds3_scaleSafe_1"] = createExportWrapper("emscripten_bind_PxBounds3_scaleSafe_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_scaleFast_1 = Module["_emscripten_bind_PxBounds3_scaleFast_1"] = createExportWrapper("emscripten_bind_PxBounds3_scaleFast_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_fattenSafe_1 = Module["_emscripten_bind_PxBounds3_fattenSafe_1"] = createExportWrapper("emscripten_bind_PxBounds3_fattenSafe_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_fattenFast_1 = Module["_emscripten_bind_PxBounds3_fattenFast_1"] = createExportWrapper("emscripten_bind_PxBounds3_fattenFast_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_isFinite_0 = Module["_emscripten_bind_PxBounds3_isFinite_0"] = createExportWrapper("emscripten_bind_PxBounds3_isFinite_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_isValid_0 = Module["_emscripten_bind_PxBounds3_isValid_0"] = createExportWrapper("emscripten_bind_PxBounds3_isValid_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_get_minimum_0 = Module["_emscripten_bind_PxBounds3_get_minimum_0"] = createExportWrapper("emscripten_bind_PxBounds3_get_minimum_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_set_minimum_1 = Module["_emscripten_bind_PxBounds3_set_minimum_1"] = createExportWrapper("emscripten_bind_PxBounds3_set_minimum_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_get_maximum_0 = Module["_emscripten_bind_PxBounds3_get_maximum_0"] = createExportWrapper("emscripten_bind_PxBounds3_get_maximum_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3_set_maximum_1 = Module["_emscripten_bind_PxBounds3_set_maximum_1"] = createExportWrapper("emscripten_bind_PxBounds3_set_maximum_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxBounds3___destroy___0 = Module["_emscripten_bind_PxBounds3___destroy___0"] = createExportWrapper("emscripten_bind_PxBounds3___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxDefaultErrorCallback_PxDefaultErrorCallback_0 = Module["_emscripten_bind_PxDefaultErrorCallback_PxDefaultErrorCallback_0"] = createExportWrapper("emscripten_bind_PxDefaultErrorCallback_PxDefaultErrorCallback_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxDefaultErrorCallback___destroy___0 = Module["_emscripten_bind_PxDefaultErrorCallback___destroy___0"] = createExportWrapper("emscripten_bind_PxDefaultErrorCallback___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_PxQuat_0 = Module["_emscripten_bind_PxQuat_PxQuat_0"] = createExportWrapper("emscripten_bind_PxQuat_PxQuat_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_PxQuat_4 = Module["_emscripten_bind_PxQuat_PxQuat_4"] = createExportWrapper("emscripten_bind_PxQuat_PxQuat_4");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_get_x_0 = Module["_emscripten_bind_PxQuat_get_x_0"] = createExportWrapper("emscripten_bind_PxQuat_get_x_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_set_x_1 = Module["_emscripten_bind_PxQuat_set_x_1"] = createExportWrapper("emscripten_bind_PxQuat_set_x_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_get_y_0 = Module["_emscripten_bind_PxQuat_get_y_0"] = createExportWrapper("emscripten_bind_PxQuat_get_y_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_set_y_1 = Module["_emscripten_bind_PxQuat_set_y_1"] = createExportWrapper("emscripten_bind_PxQuat_set_y_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_get_z_0 = Module["_emscripten_bind_PxQuat_get_z_0"] = createExportWrapper("emscripten_bind_PxQuat_get_z_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_set_z_1 = Module["_emscripten_bind_PxQuat_set_z_1"] = createExportWrapper("emscripten_bind_PxQuat_set_z_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_get_w_0 = Module["_emscripten_bind_PxQuat_get_w_0"] = createExportWrapper("emscripten_bind_PxQuat_get_w_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat_set_w_1 = Module["_emscripten_bind_PxQuat_set_w_1"] = createExportWrapper("emscripten_bind_PxQuat_set_w_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxQuat___destroy___0 = Module["_emscripten_bind_PxQuat___destroy___0"] = createExportWrapper("emscripten_bind_PxQuat___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTolerancesScale_PxTolerancesScale_0 = Module["_emscripten_bind_PxTolerancesScale_PxTolerancesScale_0"] = createExportWrapper("emscripten_bind_PxTolerancesScale_PxTolerancesScale_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTolerancesScale___destroy___0 = Module["_emscripten_bind_PxTolerancesScale___destroy___0"] = createExportWrapper("emscripten_bind_PxTolerancesScale___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform_PxTransform_1 = Module["_emscripten_bind_PxTransform_PxTransform_1"] = createExportWrapper("emscripten_bind_PxTransform_PxTransform_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform_PxTransform_2 = Module["_emscripten_bind_PxTransform_PxTransform_2"] = createExportWrapper("emscripten_bind_PxTransform_PxTransform_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform_get_q_0 = Module["_emscripten_bind_PxTransform_get_q_0"] = createExportWrapper("emscripten_bind_PxTransform_get_q_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform_set_q_1 = Module["_emscripten_bind_PxTransform_set_q_1"] = createExportWrapper("emscripten_bind_PxTransform_set_q_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform_get_p_0 = Module["_emscripten_bind_PxTransform_get_p_0"] = createExportWrapper("emscripten_bind_PxTransform_get_p_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform_set_p_1 = Module["_emscripten_bind_PxTransform_set_p_1"] = createExportWrapper("emscripten_bind_PxTransform_set_p_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxTransform___destroy___0 = Module["_emscripten_bind_PxTransform___destroy___0"] = createExportWrapper("emscripten_bind_PxTransform___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRealPtr___destroy___0 = Module["_emscripten_bind_PxRealPtr___destroy___0"] = createExportWrapper("emscripten_bind_PxRealPtr___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxU8Ptr___destroy___0 = Module["_emscripten_bind_PxU8Ptr___destroy___0"] = createExportWrapper("emscripten_bind_PxU8Ptr___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_PxVec3_0 = Module["_emscripten_bind_PxVec3_PxVec3_0"] = createExportWrapper("emscripten_bind_PxVec3_PxVec3_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_PxVec3_3 = Module["_emscripten_bind_PxVec3_PxVec3_3"] = createExportWrapper("emscripten_bind_PxVec3_PxVec3_3");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_get_x_0 = Module["_emscripten_bind_PxVec3_get_x_0"] = createExportWrapper("emscripten_bind_PxVec3_get_x_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_set_x_1 = Module["_emscripten_bind_PxVec3_set_x_1"] = createExportWrapper("emscripten_bind_PxVec3_set_x_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_get_y_0 = Module["_emscripten_bind_PxVec3_get_y_0"] = createExportWrapper("emscripten_bind_PxVec3_get_y_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_set_y_1 = Module["_emscripten_bind_PxVec3_set_y_1"] = createExportWrapper("emscripten_bind_PxVec3_set_y_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_get_z_0 = Module["_emscripten_bind_PxVec3_get_z_0"] = createExportWrapper("emscripten_bind_PxVec3_get_z_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3_set_z_1 = Module["_emscripten_bind_PxVec3_set_z_1"] = createExportWrapper("emscripten_bind_PxVec3_set_z_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxVec3___destroy___0 = Module["_emscripten_bind_PxVec3___destroy___0"] = createExportWrapper("emscripten_bind_PxVec3___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxDefaultAllocator_PxDefaultAllocator_0 = Module["_emscripten_bind_PxDefaultAllocator_PxDefaultAllocator_0"] = createExportWrapper("emscripten_bind_PxDefaultAllocator_PxDefaultAllocator_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxDefaultAllocator___destroy___0 = Module["_emscripten_bind_PxDefaultAllocator___destroy___0"] = createExportWrapper("emscripten_bind_PxDefaultAllocator___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxDefaultCpuDispatcher___destroy___0 = Module["_emscripten_bind_PxDefaultCpuDispatcher___destroy___0"] = createExportWrapper("emscripten_bind_PxDefaultCpuDispatcher___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setDriveVelocity_1 = Module["_emscripten_bind_PxRevoluteJoint_setDriveVelocity_1"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setDriveVelocity_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setDriveVelocity_2 = Module["_emscripten_bind_PxRevoluteJoint_setDriveVelocity_2"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setDriveVelocity_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getDriveVelocity_0 = Module["_emscripten_bind_PxRevoluteJoint_getDriveVelocity_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getDriveVelocity_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setDriveForceLimit_1 = Module["_emscripten_bind_PxRevoluteJoint_setDriveForceLimit_1"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setDriveForceLimit_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getDriveForceLimit_0 = Module["_emscripten_bind_PxRevoluteJoint_getDriveForceLimit_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getDriveForceLimit_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setDriveGearRatio_1 = Module["_emscripten_bind_PxRevoluteJoint_setDriveGearRatio_1"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setDriveGearRatio_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getDriveGearRatio_0 = Module["_emscripten_bind_PxRevoluteJoint_getDriveGearRatio_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getDriveGearRatio_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setRevoluteJointFlags_1 = Module["_emscripten_bind_PxRevoluteJoint_setRevoluteJointFlags_1"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setRevoluteJointFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getRevoluteJointFlags_0 = Module["_emscripten_bind_PxRevoluteJoint_getRevoluteJointFlags_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getRevoluteJointFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_release_0 = Module["_emscripten_bind_PxRevoluteJoint_release_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_release_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getConcreteTypeName_0 = Module["_emscripten_bind_PxRevoluteJoint_getConcreteTypeName_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getConcreteTypeName_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getConcreteType_0 = Module["_emscripten_bind_PxRevoluteJoint_getConcreteType_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getConcreteType_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setBaseFlag_2 = Module["_emscripten_bind_PxRevoluteJoint_setBaseFlag_2"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setBaseFlag_2");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_setBaseFlags_1 = Module["_emscripten_bind_PxRevoluteJoint_setBaseFlags_1"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_setBaseFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_getBaseFlags_0 = Module["_emscripten_bind_PxRevoluteJoint_getBaseFlags_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_getBaseFlags_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint_isReleasable_0 = Module["_emscripten_bind_PxRevoluteJoint_isReleasable_0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint_isReleasable_0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJoint___destroy___0 = Module["_emscripten_bind_PxRevoluteJoint___destroy___0"] = createExportWrapper("emscripten_bind_PxRevoluteJoint___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJointFlags_PxRevoluteJointFlags_1 = Module["_emscripten_bind_PxRevoluteJointFlags_PxRevoluteJointFlags_1"] = createExportWrapper("emscripten_bind_PxRevoluteJointFlags_PxRevoluteJointFlags_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJointFlags_isSet_1 = Module["_emscripten_bind_PxRevoluteJointFlags_isSet_1"] = createExportWrapper("emscripten_bind_PxRevoluteJointFlags_isSet_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJointFlags_set_1 = Module["_emscripten_bind_PxRevoluteJointFlags_set_1"] = createExportWrapper("emscripten_bind_PxRevoluteJointFlags_set_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJointFlags_clear_1 = Module["_emscripten_bind_PxRevoluteJointFlags_clear_1"] = createExportWrapper("emscripten_bind_PxRevoluteJointFlags_clear_1");

/** @type {function(...*):?} */
var _emscripten_bind_PxRevoluteJointFlags___destroy___0 = Module["_emscripten_bind_PxRevoluteJointFlags___destroy___0"] = createExportWrapper("emscripten_bind_PxRevoluteJointFlags___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxMaterialPtr___destroy___0 = Module["_emscripten_bind_PxMaterialPtr___destroy___0"] = createExportWrapper("emscripten_bind_PxMaterialPtr___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_0 = Module["_emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_0"] = createExportWrapper("emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_1 = Module["_emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_1"] = createExportWrapper("emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial_at_1 = Module["_emscripten_bind_Vector_PxMaterial_at_1"] = createExportWrapper("emscripten_bind_Vector_PxMaterial_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial_data_0 = Module["_emscripten_bind_Vector_PxMaterial_data_0"] = createExportWrapper("emscripten_bind_Vector_PxMaterial_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial_size_0 = Module["_emscripten_bind_Vector_PxMaterial_size_0"] = createExportWrapper("emscripten_bind_Vector_PxMaterial_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial_push_back_1 = Module["_emscripten_bind_Vector_PxMaterial_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxMaterial_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxMaterial___destroy___0 = Module["_emscripten_bind_Vector_PxMaterial___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxMaterial___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal_Vector_PxReal_0 = Module["_emscripten_bind_Vector_PxReal_Vector_PxReal_0"] = createExportWrapper("emscripten_bind_Vector_PxReal_Vector_PxReal_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal_Vector_PxReal_1 = Module["_emscripten_bind_Vector_PxReal_Vector_PxReal_1"] = createExportWrapper("emscripten_bind_Vector_PxReal_Vector_PxReal_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal_at_1 = Module["_emscripten_bind_Vector_PxReal_at_1"] = createExportWrapper("emscripten_bind_Vector_PxReal_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal_data_0 = Module["_emscripten_bind_Vector_PxReal_data_0"] = createExportWrapper("emscripten_bind_Vector_PxReal_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal_size_0 = Module["_emscripten_bind_Vector_PxReal_size_0"] = createExportWrapper("emscripten_bind_Vector_PxReal_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal_push_back_1 = Module["_emscripten_bind_Vector_PxReal_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxReal_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxReal___destroy___0 = Module["_emscripten_bind_Vector_PxReal___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxReal___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3_Vector_PxVec3_0 = Module["_emscripten_bind_Vector_PxVec3_Vector_PxVec3_0"] = createExportWrapper("emscripten_bind_Vector_PxVec3_Vector_PxVec3_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3_Vector_PxVec3_1 = Module["_emscripten_bind_Vector_PxVec3_Vector_PxVec3_1"] = createExportWrapper("emscripten_bind_Vector_PxVec3_Vector_PxVec3_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3_at_1 = Module["_emscripten_bind_Vector_PxVec3_at_1"] = createExportWrapper("emscripten_bind_Vector_PxVec3_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3_data_0 = Module["_emscripten_bind_Vector_PxVec3_data_0"] = createExportWrapper("emscripten_bind_Vector_PxVec3_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3_size_0 = Module["_emscripten_bind_Vector_PxVec3_size_0"] = createExportWrapper("emscripten_bind_Vector_PxVec3_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3_push_back_1 = Module["_emscripten_bind_Vector_PxVec3_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxVec3_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVec3___destroy___0 = Module["_emscripten_bind_Vector_PxVec3___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxVec3___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_0 = Module["_emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_1 = Module["_emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_1"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult_at_1 = Module["_emscripten_bind_Vector_PxRaycastQueryResult_at_1"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult_data_0 = Module["_emscripten_bind_Vector_PxRaycastQueryResult_data_0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult_size_0 = Module["_emscripten_bind_Vector_PxRaycastQueryResult_size_0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult_push_back_1 = Module["_emscripten_bind_Vector_PxRaycastQueryResult_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastQueryResult___destroy___0 = Module["_emscripten_bind_Vector_PxRaycastQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_0 = Module["_emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_0"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_1 = Module["_emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_1"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult_at_1 = Module["_emscripten_bind_Vector_PxSweepQueryResult_at_1"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult_data_0 = Module["_emscripten_bind_Vector_PxSweepQueryResult_data_0"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult_size_0 = Module["_emscripten_bind_Vector_PxSweepQueryResult_size_0"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult_push_back_1 = Module["_emscripten_bind_Vector_PxSweepQueryResult_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepQueryResult___destroy___0 = Module["_emscripten_bind_Vector_PxSweepQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxSweepQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_0 = Module["_emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_1 = Module["_emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_1"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit_at_1 = Module["_emscripten_bind_Vector_PxRaycastHit_at_1"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit_data_0 = Module["_emscripten_bind_Vector_PxRaycastHit_data_0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit_size_0 = Module["_emscripten_bind_Vector_PxRaycastHit_size_0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit_push_back_1 = Module["_emscripten_bind_Vector_PxRaycastHit_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxRaycastHit___destroy___0 = Module["_emscripten_bind_Vector_PxRaycastHit___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxRaycastHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_0 = Module["_emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_0"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_1 = Module["_emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_1"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit_at_1 = Module["_emscripten_bind_Vector_PxSweepHit_at_1"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit_data_0 = Module["_emscripten_bind_Vector_PxSweepHit_data_0"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit_size_0 = Module["_emscripten_bind_Vector_PxSweepHit_size_0"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit_push_back_1 = Module["_emscripten_bind_Vector_PxSweepHit_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxSweepHit___destroy___0 = Module["_emscripten_bind_Vector_PxSweepHit___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxSweepHit___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_0 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_1 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_1"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_at_1 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_at_1"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_data_0 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_data_0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_size_0 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_size_0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_push_back_1 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleDrivableSurfaceType___destroy___0 = Module["_emscripten_bind_Vector_PxVehicleDrivableSurfaceType___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleDrivableSurfaceType___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_0 = Module["_emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_0"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_1 = Module["_emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_1"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult_at_1 = Module["_emscripten_bind_Vector_PxWheelQueryResult_at_1"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult_data_0 = Module["_emscripten_bind_Vector_PxWheelQueryResult_data_0"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult_size_0 = Module["_emscripten_bind_Vector_PxWheelQueryResult_size_0"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult_push_back_1 = Module["_emscripten_bind_Vector_PxWheelQueryResult_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxWheelQueryResult___destroy___0 = Module["_emscripten_bind_Vector_PxWheelQueryResult___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxWheelQueryResult___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_PxVehicleWheelsPtr___destroy___0 = Module["_emscripten_bind_PxVehicleWheelsPtr___destroy___0"] = createExportWrapper("emscripten_bind_PxVehicleWheelsPtr___destroy___0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_0 = Module["_emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_1 = Module["_emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_1"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels_at_1 = Module["_emscripten_bind_Vector_PxVehicleWheels_at_1"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels_at_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels_data_0 = Module["_emscripten_bind_Vector_PxVehicleWheels_data_0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels_data_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels_size_0 = Module["_emscripten_bind_Vector_PxVehicleWheels_size_0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels_size_0");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels_push_back_1 = Module["_emscripten_bind_Vector_PxVehicleWheels_push_back_1"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels_push_back_1");

/** @type {function(...*):?} */
var _emscripten_bind_Vector_PxVehicleWheels___destroy___0 = Module["_emscripten_bind_Vector_PxVehicleWheels___destroy___0"] = createExportWrapper("emscripten_bind_Vector_PxVehicleWheels___destroy___0");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorFlagEnum_eVISUALIZATION = Module["_emscripten_enum_PxActorFlagEnum_eVISUALIZATION"] = createExportWrapper("emscripten_enum_PxActorFlagEnum_eVISUALIZATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY = Module["_emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY"] = createExportWrapper("emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES = Module["_emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES"] = createExportWrapper("emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION = Module["_emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION"] = createExportWrapper("emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorTypeEnum_eRIGID_STATIC = Module["_emscripten_enum_PxActorTypeEnum_eRIGID_STATIC"] = createExportWrapper("emscripten_enum_PxActorTypeEnum_eRIGID_STATIC");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC = Module["_emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC"] = createExportWrapper("emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK = Module["_emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK"] = createExportWrapper("emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorTypeEnum_eACTOR_COUNT = Module["_emscripten_enum_PxActorTypeEnum_eACTOR_COUNT"] = createExportWrapper("emscripten_enum_PxActorTypeEnum_eACTOR_COUNT");

/** @type {function(...*):?} */
var _emscripten_enum_PxActorTypeEnum_eACTOR_FORCE_DWORD = Module["_emscripten_enum_PxActorTypeEnum_eACTOR_FORCE_DWORD"] = createExportWrapper("emscripten_enum_PxActorTypeEnum_eACTOR_FORCE_DWORD");

/** @type {function(...*):?} */
var _emscripten_enum_PxForceModeEnum_eFORCE = Module["_emscripten_enum_PxForceModeEnum_eFORCE"] = createExportWrapper("emscripten_enum_PxForceModeEnum_eFORCE");

/** @type {function(...*):?} */
var _emscripten_enum_PxForceModeEnum_eIMPULSE = Module["_emscripten_enum_PxForceModeEnum_eIMPULSE"] = createExportWrapper("emscripten_enum_PxForceModeEnum_eIMPULSE");

/** @type {function(...*):?} */
var _emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE = Module["_emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE"] = createExportWrapper("emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE");

/** @type {function(...*):?} */
var _emscripten_enum_PxForceModeEnum_eACCELERATION = Module["_emscripten_enum_PxForceModeEnum_eACCELERATION"] = createExportWrapper("emscripten_enum_PxForceModeEnum_eACCELERATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_ePOSITION = Module["_emscripten_enum_PxHitFlagEnum_ePOSITION"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_ePOSITION");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eNORMAL = Module["_emscripten_enum_PxHitFlagEnum_eNORMAL"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eNORMAL");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eUV = Module["_emscripten_enum_PxHitFlagEnum_eUV"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eUV");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP = Module["_emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE = Module["_emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eMESH_ANY = Module["_emscripten_enum_PxHitFlagEnum_eMESH_ANY"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eMESH_ANY");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES = Module["_emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP = Module["_emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eMTD = Module["_emscripten_enum_PxHitFlagEnum_eMTD"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eMTD");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eFACE_INDEX = Module["_emscripten_enum_PxHitFlagEnum_eFACE_INDEX"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eFACE_INDEX");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eDEFAULT = Module["_emscripten_enum_PxHitFlagEnum_eDEFAULT"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eDEFAULT");

/** @type {function(...*):?} */
var _emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS = Module["_emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS"] = createExportWrapper("emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC = Module["_emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES = Module["_emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD = Module["_emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION = Module["_emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW = Module["_emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD = Module["_emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE = Module["_emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS = Module["_emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS"] = createExportWrapper("emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X = Module["_emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X"] = createExportWrapper("emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y = Module["_emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y"] = createExportWrapper("emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z = Module["_emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z"] = createExportWrapper("emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X = Module["_emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X"] = createExportWrapper("emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y = Module["_emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y"] = createExportWrapper("emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y");

/** @type {function(...*):?} */
var _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z = Module["_emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z"] = createExportWrapper("emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_CCD = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_CCD"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_CCD");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP = Module["_emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eADAPTIVE_FORCE = Module["_emscripten_enum_PxSceneFlagEnum_eADAPTIVE_FORCE"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eADAPTIVE_FORCE");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_PCM = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_PCM"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_PCM");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE = Module["_emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE = Module["_emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK = Module["_emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS = Module["_emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION = Module["_emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS = Module["_emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS"] = createExportWrapper("emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS");

/** @type {function(...*):?} */
var _emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE = Module["_emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE"] = createExportWrapper("emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE");

/** @type {function(...*):?} */
var _emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE = Module["_emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE"] = createExportWrapper("emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE");

/** @type {function(...*):?} */
var _emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE = Module["_emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE"] = createExportWrapper("emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE");

/** @type {function(...*):?} */
var _emscripten_enum_PxShapeFlagEnum_eVISUALIZATION = Module["_emscripten_enum_PxShapeFlagEnum_eVISUALIZATION"] = createExportWrapper("emscripten_enum_PxShapeFlagEnum_eVISUALIZATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE = Module["_emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE"] = createExportWrapper("emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE = Module["_emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE"] = createExportWrapper("emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_4WD = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_4WD"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_4WD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_FRONTWD = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_FRONTWD"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_FRONTWD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_REARWD = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_REARWD"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_REARWD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_4WD = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_4WD"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_4WD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_FRONTWD = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_FRONTWD"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_FRONTWD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_REARWD = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_REARWD"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_REARWD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDifferential4WDataEnum_eMAX_NB_DIFF_TYPES = Module["_emscripten_enum_PxVehicleDifferential4WDataEnum_eMAX_NB_DIFF_TYPES"] = createExportWrapper("emscripten_enum_PxVehicleDifferential4WDataEnum_eMAX_NB_DIFF_TYPES");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_ACCEL = Module["_emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_ACCEL"] = createExportWrapper("emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_ACCEL");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_BRAKE = Module["_emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_BRAKE"] = createExportWrapper("emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_BRAKE");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_HANDBRAKE = Module["_emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_HANDBRAKE"] = createExportWrapper("emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_HANDBRAKE");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_LEFT = Module["_emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_LEFT"] = createExportWrapper("emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_LEFT");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_RIGHT = Module["_emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_RIGHT"] = createExportWrapper("emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_RIGHT");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleDrive4WControlEnum_eMAX_NB_DRIVE4W_ANALOG_INPUTS = Module["_emscripten_enum_PxVehicleDrive4WControlEnum_eMAX_NB_DRIVE4W_ANALOG_INPUTS"] = createExportWrapper("emscripten_enum_PxVehicleDrive4WControlEnum_eMAX_NB_DRIVE4W_ANALOG_INPUTS");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eREVERSE = Module["_emscripten_enum_PxVehicleGearEnum_eREVERSE"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eREVERSE");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eNEUTRAL = Module["_emscripten_enum_PxVehicleGearEnum_eNEUTRAL"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eNEUTRAL");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eFIRST = Module["_emscripten_enum_PxVehicleGearEnum_eFIRST"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eFIRST");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eSECOND = Module["_emscripten_enum_PxVehicleGearEnum_eSECOND"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eSECOND");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTHIRD = Module["_emscripten_enum_PxVehicleGearEnum_eTHIRD"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTHIRD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eFOURTH = Module["_emscripten_enum_PxVehicleGearEnum_eFOURTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eFOURTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eFIFTH = Module["_emscripten_enum_PxVehicleGearEnum_eFIFTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eFIFTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eSIXTH = Module["_emscripten_enum_PxVehicleGearEnum_eSIXTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eSIXTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eSEVENTH = Module["_emscripten_enum_PxVehicleGearEnum_eSEVENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eSEVENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eEIGHTH = Module["_emscripten_enum_PxVehicleGearEnum_eEIGHTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eEIGHTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eNINTH = Module["_emscripten_enum_PxVehicleGearEnum_eNINTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eNINTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTENTH = Module["_emscripten_enum_PxVehicleGearEnum_eTENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eELEVENTH = Module["_emscripten_enum_PxVehicleGearEnum_eELEVENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eELEVENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWELFTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWELFTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWELFTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTHIRTEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eTHIRTEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTHIRTEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eFOURTEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eFOURTEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eFOURTEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eFIFTEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eFIFTEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eFIFTEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eSIXTEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eSIXTEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eSIXTEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eSEVENTEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eSEVENTEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eSEVENTEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eEIGHTEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eEIGHTEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eEIGHTEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eNINETEENTH = Module["_emscripten_enum_PxVehicleGearEnum_eNINETEENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eNINETEENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTIETH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTIETH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTIETH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYFIRST = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYFIRST"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYFIRST");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYSECOND = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYSECOND"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYSECOND");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYTHIRD = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYTHIRD"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYTHIRD");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYFOURTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYFOURTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYFOURTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYFIFTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYFIFTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYFIFTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYSIXTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYSIXTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYSIXTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYSEVENTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYSEVENTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYSEVENTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYEIGHTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYEIGHTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYEIGHTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTWENTYNINTH = Module["_emscripten_enum_PxVehicleGearEnum_eTWENTYNINTH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTWENTYNINTH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eTHIRTIETH = Module["_emscripten_enum_PxVehicleGearEnum_eTHIRTIETH"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eTHIRTIETH");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleGearEnum_eGEARSRATIO_COUNT = Module["_emscripten_enum_PxVehicleGearEnum_eGEARSRATIO_COUNT"] = createExportWrapper("emscripten_enum_PxVehicleGearEnum_eGEARSRATIO_COUNT");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleUpdateModeEnum_eVELOCITY_CHANGE = Module["_emscripten_enum_PxVehicleUpdateModeEnum_eVELOCITY_CHANGE"] = createExportWrapper("emscripten_enum_PxVehicleUpdateModeEnum_eVELOCITY_CHANGE");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleUpdateModeEnum_eACCELERATION = Module["_emscripten_enum_PxVehicleUpdateModeEnum_eACCELERATION"] = createExportWrapper("emscripten_enum_PxVehicleUpdateModeEnum_eACCELERATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxVehicleWheelsSimFlagEnum_eLIMIT_SUSPENSION_EXPANSION_VELOCITY = Module["_emscripten_enum_PxVehicleWheelsSimFlagEnum_eLIMIT_SUSPENSION_EXPANSION_VELOCITY"] = createExportWrapper("emscripten_enum_PxVehicleWheelsSimFlagEnum_eLIMIT_SUSPENSION_EXPANSION_VELOCITY");

/** @type {function(...*):?} */
var _emscripten_enum_VehicleSurfaceTypeMask_DRIVABLE_SURFACE = Module["_emscripten_enum_VehicleSurfaceTypeMask_DRIVABLE_SURFACE"] = createExportWrapper("emscripten_enum_VehicleSurfaceTypeMask_DRIVABLE_SURFACE");

/** @type {function(...*):?} */
var _emscripten_enum_VehicleSurfaceTypeMask_UNDRIVABLE_SURFACE = Module["_emscripten_enum_VehicleSurfaceTypeMask_UNDRIVABLE_SURFACE"] = createExportWrapper("emscripten_enum_VehicleSurfaceTypeMask_UNDRIVABLE_SURFACE");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS = Module["_emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS"] = createExportWrapper("emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES = Module["_emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX = Module["_emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES = Module["_emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT = Module["_emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION = Module["_emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING = Module["_emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION = Module["_emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eGPU_COMPATIBLE = Module["_emscripten_enum_PxConvexFlagEnum_eGPU_COMPATIBLE"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eGPU_COMPATIBLE");

/** @type {function(...*):?} */
var _emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES = Module["_emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES"] = createExportWrapper("emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES");

/** @type {function(...*):?} */
var _emscripten_enum_PxBaseFlagEnum_eOWNS_MEMORY = Module["_emscripten_enum_PxBaseFlagEnum_eOWNS_MEMORY"] = createExportWrapper("emscripten_enum_PxBaseFlagEnum_eOWNS_MEMORY");

/** @type {function(...*):?} */
var _emscripten_enum_PxBaseFlagEnum_eIS_RELEASABLE = Module["_emscripten_enum_PxBaseFlagEnum_eIS_RELEASABLE"] = createExportWrapper("emscripten_enum_PxBaseFlagEnum_eIS_RELEASABLE");

/** @type {function(...*):?} */
var _emscripten_enum_PxIDENTITYEnum_PxIdentity = Module["_emscripten_enum_PxIDENTITYEnum_PxIdentity"] = createExportWrapper("emscripten_enum_PxIDENTITYEnum_PxIdentity");

/** @type {function(...*):?} */
var _emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED = Module["_emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED"] = createExportWrapper("emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED");

/** @type {function(...*):?} */
var _emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED = Module["_emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED"] = createExportWrapper("emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED");

/** @type {function(...*):?} */
var _emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN = Module["_emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN"] = createExportWrapper("emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN");

/** @type {function(...*):?} */
var _free = Module["_free"] = createExportWrapper("free");

/** @type {function(...*):?} */
var _ntohs = Module["_ntohs"] = createExportWrapper("ntohs");

/** @type {function(...*):?} */
var _htons = Module["_htons"] = createExportWrapper("htons");

/** @type {function(...*):?} */
var _htonl = Module["_htonl"] = createExportWrapper("htonl");

/** @type {function(...*):?} */
var ___errno_location = Module["___errno_location"] = createExportWrapper("__errno_location");

/** @type {function(...*):?} */
var _malloc = Module["_malloc"] = createExportWrapper("malloc");

/** @type {function(...*):?} */
var stackSave = Module["stackSave"] = createExportWrapper("stackSave");

/** @type {function(...*):?} */
var stackRestore = Module["stackRestore"] = createExportWrapper("stackRestore");

/** @type {function(...*):?} */
var stackAlloc = Module["stackAlloc"] = createExportWrapper("stackAlloc");

/** @type {function(...*):?} */
var _setThrew = Module["_setThrew"] = createExportWrapper("setThrew");

/** @type {function(...*):?} */
var _emscripten_main_thread_process_queued_calls = Module["_emscripten_main_thread_process_queued_calls"] = createExportWrapper("emscripten_main_thread_process_queued_calls");

/** @type {function(...*):?} */
var dynCall_iifiiiijii = Module["dynCall_iifiiiijii"] = createExportWrapper("dynCall_iifiiiijii");

/** @type {function(...*):?} */
var dynCall_vifijii = Module["dynCall_vifijii"] = createExportWrapper("dynCall_vifijii");

/** @type {function(...*):?} */
var dynCall_jiji = Module["dynCall_jiji"] = createExportWrapper("dynCall_jiji");





// === Auto-generated postamble setup entry stuff ===

if (!Object.getOwnPropertyDescriptor(Module, "intArrayFromString")) Module["intArrayFromString"] = function() { abort("'intArrayFromString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "intArrayToString")) Module["intArrayToString"] = function() { abort("'intArrayToString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "ccall")) Module["ccall"] = function() { abort("'ccall' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "cwrap")) Module["cwrap"] = function() { abort("'cwrap' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "setValue")) Module["setValue"] = function() { abort("'setValue' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getValue")) Module["getValue"] = function() { abort("'getValue' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "allocate")) Module["allocate"] = function() { abort("'allocate' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "UTF8ArrayToString")) Module["UTF8ArrayToString"] = function() { abort("'UTF8ArrayToString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "UTF8ToString")) Module["UTF8ToString"] = function() { abort("'UTF8ToString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stringToUTF8Array")) Module["stringToUTF8Array"] = function() { abort("'stringToUTF8Array' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stringToUTF8")) Module["stringToUTF8"] = function() { abort("'stringToUTF8' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "lengthBytesUTF8")) Module["lengthBytesUTF8"] = function() { abort("'lengthBytesUTF8' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stackTrace")) Module["stackTrace"] = function() { abort("'stackTrace' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addOnPreRun")) Module["addOnPreRun"] = function() { abort("'addOnPreRun' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addOnInit")) Module["addOnInit"] = function() { abort("'addOnInit' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addOnPreMain")) Module["addOnPreMain"] = function() { abort("'addOnPreMain' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addOnExit")) Module["addOnExit"] = function() { abort("'addOnExit' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addOnPostRun")) Module["addOnPostRun"] = function() { abort("'addOnPostRun' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeStringToMemory")) Module["writeStringToMemory"] = function() { abort("'writeStringToMemory' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeArrayToMemory")) Module["writeArrayToMemory"] = function() { abort("'writeArrayToMemory' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeAsciiToMemory")) Module["writeAsciiToMemory"] = function() { abort("'writeAsciiToMemory' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addRunDependency")) Module["addRunDependency"] = function() { abort("'addRunDependency' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "removeRunDependency")) Module["removeRunDependency"] = function() { abort("'removeRunDependency' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createFolder")) Module["FS_createFolder"] = function() { abort("'FS_createFolder' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createPath")) Module["FS_createPath"] = function() { abort("'FS_createPath' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createDataFile")) Module["FS_createDataFile"] = function() { abort("'FS_createDataFile' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createPreloadedFile")) Module["FS_createPreloadedFile"] = function() { abort("'FS_createPreloadedFile' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createLazyFile")) Module["FS_createLazyFile"] = function() { abort("'FS_createLazyFile' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createLink")) Module["FS_createLink"] = function() { abort("'FS_createLink' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_createDevice")) Module["FS_createDevice"] = function() { abort("'FS_createDevice' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "FS_unlink")) Module["FS_unlink"] = function() { abort("'FS_unlink' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ). Alternatively, forcing filesystem support (-s FORCE_FILESYSTEM=1) can export this for you") };
if (!Object.getOwnPropertyDescriptor(Module, "getLEB")) Module["getLEB"] = function() { abort("'getLEB' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getFunctionTables")) Module["getFunctionTables"] = function() { abort("'getFunctionTables' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "alignFunctionTables")) Module["alignFunctionTables"] = function() { abort("'alignFunctionTables' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "registerFunctions")) Module["registerFunctions"] = function() { abort("'registerFunctions' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "addFunction")) Module["addFunction"] = function() { abort("'addFunction' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "removeFunction")) Module["removeFunction"] = function() { abort("'removeFunction' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getFuncWrapper")) Module["getFuncWrapper"] = function() { abort("'getFuncWrapper' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "prettyPrint")) Module["prettyPrint"] = function() { abort("'prettyPrint' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "makeBigInt")) Module["makeBigInt"] = function() { abort("'makeBigInt' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "dynCall")) Module["dynCall"] = function() { abort("'dynCall' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getCompilerSetting")) Module["getCompilerSetting"] = function() { abort("'getCompilerSetting' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "print")) Module["print"] = function() { abort("'print' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "printErr")) Module["printErr"] = function() { abort("'printErr' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getTempRet0")) Module["getTempRet0"] = function() { abort("'getTempRet0' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "setTempRet0")) Module["setTempRet0"] = function() { abort("'setTempRet0' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "callMain")) Module["callMain"] = function() { abort("'callMain' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "abort")) Module["abort"] = function() { abort("'abort' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stringToNewUTF8")) Module["stringToNewUTF8"] = function() { abort("'stringToNewUTF8' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "abortOnCannotGrowMemory")) Module["abortOnCannotGrowMemory"] = function() { abort("'abortOnCannotGrowMemory' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "emscripten_realloc_buffer")) Module["emscripten_realloc_buffer"] = function() { abort("'emscripten_realloc_buffer' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "ENV")) Module["ENV"] = function() { abort("'ENV' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "ERRNO_CODES")) Module["ERRNO_CODES"] = function() { abort("'ERRNO_CODES' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "ERRNO_MESSAGES")) Module["ERRNO_MESSAGES"] = function() { abort("'ERRNO_MESSAGES' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "setErrNo")) Module["setErrNo"] = function() { abort("'setErrNo' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "DNS")) Module["DNS"] = function() { abort("'DNS' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getHostByName")) Module["getHostByName"] = function() { abort("'getHostByName' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "GAI_ERRNO_MESSAGES")) Module["GAI_ERRNO_MESSAGES"] = function() { abort("'GAI_ERRNO_MESSAGES' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "Protocols")) Module["Protocols"] = function() { abort("'Protocols' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "Sockets")) Module["Sockets"] = function() { abort("'Sockets' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getRandomDevice")) Module["getRandomDevice"] = function() { abort("'getRandomDevice' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "traverseStack")) Module["traverseStack"] = function() { abort("'traverseStack' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "UNWIND_CACHE")) Module["UNWIND_CACHE"] = function() { abort("'UNWIND_CACHE' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "withBuiltinMalloc")) Module["withBuiltinMalloc"] = function() { abort("'withBuiltinMalloc' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "readAsmConstArgsArray")) Module["readAsmConstArgsArray"] = function() { abort("'readAsmConstArgsArray' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "readAsmConstArgs")) Module["readAsmConstArgs"] = function() { abort("'readAsmConstArgs' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "mainThreadEM_ASM")) Module["mainThreadEM_ASM"] = function() { abort("'mainThreadEM_ASM' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "jstoi_q")) Module["jstoi_q"] = function() { abort("'jstoi_q' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "jstoi_s")) Module["jstoi_s"] = function() { abort("'jstoi_s' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getExecutableName")) Module["getExecutableName"] = function() { abort("'getExecutableName' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "listenOnce")) Module["listenOnce"] = function() { abort("'listenOnce' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "autoResumeAudioContext")) Module["autoResumeAudioContext"] = function() { abort("'autoResumeAudioContext' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "dynCallLegacy")) Module["dynCallLegacy"] = function() { abort("'dynCallLegacy' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getDynCaller")) Module["getDynCaller"] = function() { abort("'getDynCaller' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "dynCall")) Module["dynCall"] = function() { abort("'dynCall' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "callRuntimeCallbacks")) Module["callRuntimeCallbacks"] = function() { abort("'callRuntimeCallbacks' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "abortStackOverflow")) Module["abortStackOverflow"] = function() { abort("'abortStackOverflow' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "reallyNegative")) Module["reallyNegative"] = function() { abort("'reallyNegative' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "unSign")) Module["unSign"] = function() { abort("'unSign' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "reSign")) Module["reSign"] = function() { abort("'reSign' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "formatString")) Module["formatString"] = function() { abort("'formatString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "PATH")) Module["PATH"] = function() { abort("'PATH' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "PATH_FS")) Module["PATH_FS"] = function() { abort("'PATH_FS' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "SYSCALLS")) Module["SYSCALLS"] = function() { abort("'SYSCALLS' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "syscallMmap2")) Module["syscallMmap2"] = function() { abort("'syscallMmap2' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "syscallMunmap")) Module["syscallMunmap"] = function() { abort("'syscallMunmap' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "flush_NO_FILESYSTEM")) Module["flush_NO_FILESYSTEM"] = function() { abort("'flush_NO_FILESYSTEM' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "JSEvents")) Module["JSEvents"] = function() { abort("'JSEvents' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "specialHTMLTargets")) Module["specialHTMLTargets"] = function() { abort("'specialHTMLTargets' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "maybeCStringToJsString")) Module["maybeCStringToJsString"] = function() { abort("'maybeCStringToJsString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "findEventTarget")) Module["findEventTarget"] = function() { abort("'findEventTarget' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "findCanvasEventTarget")) Module["findCanvasEventTarget"] = function() { abort("'findCanvasEventTarget' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "polyfillSetImmediate")) Module["polyfillSetImmediate"] = function() { abort("'polyfillSetImmediate' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "demangle")) Module["demangle"] = function() { abort("'demangle' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "demangleAll")) Module["demangleAll"] = function() { abort("'demangleAll' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "jsStackTrace")) Module["jsStackTrace"] = function() { abort("'jsStackTrace' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stackTrace")) Module["stackTrace"] = function() { abort("'stackTrace' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getEnvStrings")) Module["getEnvStrings"] = function() { abort("'getEnvStrings' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "checkWasiClock")) Module["checkWasiClock"] = function() { abort("'checkWasiClock' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeI53ToI64")) Module["writeI53ToI64"] = function() { abort("'writeI53ToI64' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeI53ToI64Clamped")) Module["writeI53ToI64Clamped"] = function() { abort("'writeI53ToI64Clamped' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeI53ToI64Signaling")) Module["writeI53ToI64Signaling"] = function() { abort("'writeI53ToI64Signaling' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeI53ToU64Clamped")) Module["writeI53ToU64Clamped"] = function() { abort("'writeI53ToU64Clamped' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeI53ToU64Signaling")) Module["writeI53ToU64Signaling"] = function() { abort("'writeI53ToU64Signaling' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "readI53FromI64")) Module["readI53FromI64"] = function() { abort("'readI53FromI64' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "readI53FromU64")) Module["readI53FromU64"] = function() { abort("'readI53FromU64' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "convertI32PairToI53")) Module["convertI32PairToI53"] = function() { abort("'convertI32PairToI53' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "convertU32PairToI53")) Module["convertU32PairToI53"] = function() { abort("'convertU32PairToI53' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "exceptionLast")) Module["exceptionLast"] = function() { abort("'exceptionLast' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "exceptionCaught")) Module["exceptionCaught"] = function() { abort("'exceptionCaught' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "ExceptionInfoAttrs")) Module["ExceptionInfoAttrs"] = function() { abort("'ExceptionInfoAttrs' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "ExceptionInfo")) Module["ExceptionInfo"] = function() { abort("'ExceptionInfo' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "CatchInfo")) Module["CatchInfo"] = function() { abort("'CatchInfo' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "exception_addRef")) Module["exception_addRef"] = function() { abort("'exception_addRef' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "exception_decRef")) Module["exception_decRef"] = function() { abort("'exception_decRef' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "Browser")) Module["Browser"] = function() { abort("'Browser' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "funcWrappers")) Module["funcWrappers"] = function() { abort("'funcWrappers' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "getFuncWrapper")) Module["getFuncWrapper"] = function() { abort("'getFuncWrapper' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "setMainLoop")) Module["setMainLoop"] = function() { abort("'setMainLoop' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "tempFixedLengthArray")) Module["tempFixedLengthArray"] = function() { abort("'tempFixedLengthArray' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "miniTempWebGLFloatBuffers")) Module["miniTempWebGLFloatBuffers"] = function() { abort("'miniTempWebGLFloatBuffers' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "heapObjectForWebGLType")) Module["heapObjectForWebGLType"] = function() { abort("'heapObjectForWebGLType' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "heapAccessShiftForWebGLHeap")) Module["heapAccessShiftForWebGLHeap"] = function() { abort("'heapAccessShiftForWebGLHeap' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "GL")) Module["GL"] = function() { abort("'GL' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "emscriptenWebGLGet")) Module["emscriptenWebGLGet"] = function() { abort("'emscriptenWebGLGet' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "computeUnpackAlignedImageSize")) Module["computeUnpackAlignedImageSize"] = function() { abort("'computeUnpackAlignedImageSize' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "emscriptenWebGLGetTexPixelData")) Module["emscriptenWebGLGetTexPixelData"] = function() { abort("'emscriptenWebGLGetTexPixelData' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "emscriptenWebGLGetUniform")) Module["emscriptenWebGLGetUniform"] = function() { abort("'emscriptenWebGLGetUniform' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "emscriptenWebGLGetVertexAttrib")) Module["emscriptenWebGLGetVertexAttrib"] = function() { abort("'emscriptenWebGLGetVertexAttrib' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "writeGLArray")) Module["writeGLArray"] = function() { abort("'writeGLArray' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "AL")) Module["AL"] = function() { abort("'AL' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "SDL_unicode")) Module["SDL_unicode"] = function() { abort("'SDL_unicode' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "SDL_ttfContext")) Module["SDL_ttfContext"] = function() { abort("'SDL_ttfContext' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "SDL_audio")) Module["SDL_audio"] = function() { abort("'SDL_audio' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "SDL")) Module["SDL"] = function() { abort("'SDL' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "SDL_gfx")) Module["SDL_gfx"] = function() { abort("'SDL_gfx' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "GLUT")) Module["GLUT"] = function() { abort("'GLUT' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "EGL")) Module["EGL"] = function() { abort("'EGL' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "GLFW_Window")) Module["GLFW_Window"] = function() { abort("'GLFW_Window' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "GLFW")) Module["GLFW"] = function() { abort("'GLFW' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "GLEW")) Module["GLEW"] = function() { abort("'GLEW' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "IDBStore")) Module["IDBStore"] = function() { abort("'IDBStore' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "runAndAbortIfError")) Module["runAndAbortIfError"] = function() { abort("'runAndAbortIfError' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "warnOnce")) Module["warnOnce"] = function() { abort("'warnOnce' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stackSave")) Module["stackSave"] = function() { abort("'stackSave' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stackRestore")) Module["stackRestore"] = function() { abort("'stackRestore' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stackAlloc")) Module["stackAlloc"] = function() { abort("'stackAlloc' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "AsciiToString")) Module["AsciiToString"] = function() { abort("'AsciiToString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stringToAscii")) Module["stringToAscii"] = function() { abort("'stringToAscii' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "UTF16ToString")) Module["UTF16ToString"] = function() { abort("'UTF16ToString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stringToUTF16")) Module["stringToUTF16"] = function() { abort("'stringToUTF16' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "lengthBytesUTF16")) Module["lengthBytesUTF16"] = function() { abort("'lengthBytesUTF16' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "UTF32ToString")) Module["UTF32ToString"] = function() { abort("'UTF32ToString' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "stringToUTF32")) Module["stringToUTF32"] = function() { abort("'stringToUTF32' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "lengthBytesUTF32")) Module["lengthBytesUTF32"] = function() { abort("'lengthBytesUTF32' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "allocateUTF8")) Module["allocateUTF8"] = function() { abort("'allocateUTF8' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
if (!Object.getOwnPropertyDescriptor(Module, "allocateUTF8OnStack")) Module["allocateUTF8OnStack"] = function() { abort("'allocateUTF8OnStack' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") };
Module["writeStackCookie"] = writeStackCookie;
Module["checkStackCookie"] = checkStackCookie;
if (!Object.getOwnPropertyDescriptor(Module, "ALLOC_NORMAL")) Object.defineProperty(Module, "ALLOC_NORMAL", { configurable: true, get: function() { abort("'ALLOC_NORMAL' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") } });
if (!Object.getOwnPropertyDescriptor(Module, "ALLOC_STACK")) Object.defineProperty(Module, "ALLOC_STACK", { configurable: true, get: function() { abort("'ALLOC_STACK' was not exported. add it to EXTRA_EXPORTED_RUNTIME_METHODS (see the FAQ)") } });


var calledRun;

/**
 * @constructor
 * @this {ExitStatus}
 */
function ExitStatus(status) {
  this.name = "ExitStatus";
  this.message = "Program terminated with exit(" + status + ")";
  this.status = status;
}

var calledMain = false;


dependenciesFulfilled = function runCaller() {
  // If run has never been called, and we should call run (INVOKE_RUN is true, and Module.noInitialRun is not false)
  if (!calledRun) run();
  if (!calledRun) dependenciesFulfilled = runCaller; // try this again later, after new deps are fulfilled
};


/** @type {function(Array=)} */
function run(args) {
  args = args || arguments_;

  if (runDependencies > 0) {
    return;
  }

  writeStackCookie();

  preRun();

  if (runDependencies > 0) return; // a preRun added a dependency, run will be called later

  function doRun() {
    // run may have just been called through dependencies being fulfilled just in this very frame,
    // or while the async setStatus time below was happening
    if (calledRun) return;
    calledRun = true;
    Module['calledRun'] = true;

    if (ABORT) return;

    initRuntime();

    preMain();

    readyPromiseResolve(Module);
    if (Module['onRuntimeInitialized']) Module['onRuntimeInitialized']();

    assert(!Module['_main'], 'compiled without a main, but one is present. if you added it from JS, use Module["onRuntimeInitialized"]');

    postRun();
  }

  if (Module['setStatus']) {
    Module['setStatus']('Running...');
    setTimeout(function() {
      setTimeout(function() {
        Module['setStatus']('');
      }, 1);
      doRun();
    }, 1);
  } else
  {
    doRun();
  }
  checkStackCookie();
}
Module['run'] = run;

function checkUnflushedContent() {
  // Compiler settings do not allow exiting the runtime, so flushing
  // the streams is not possible. but in ASSERTIONS mode we check
  // if there was something to flush, and if so tell the user they
  // should request that the runtime be exitable.
  // Normally we would not even include flush() at all, but in ASSERTIONS
  // builds we do so just for this check, and here we see if there is any
  // content to flush, that is, we check if there would have been
  // something a non-ASSERTIONS build would have not seen.
  // How we flush the streams depends on whether we are in SYSCALLS_REQUIRE_FILESYSTEM=0
  // mode (which has its own special function for this; otherwise, all
  // the code is inside libc)
  var print = out;
  var printErr = err;
  var has = false;
  out = err = function(x) {
    has = true;
  }
  try { // it doesn't matter if it fails
    var flush = flush_NO_FILESYSTEM;
    if (flush) flush();
  } catch(e) {}
  out = print;
  err = printErr;
  if (has) {
    warnOnce('stdio streams had content in them that was not flushed. you should set EXIT_RUNTIME to 1 (see the FAQ), or make sure to emit a newline when you printf etc.');
    warnOnce('(this may also be due to not including full filesystem support - try building with -s FORCE_FILESYSTEM=1)');
  }
}

/** @param {boolean|number=} implicit */
function exit(status, implicit) {
  checkUnflushedContent();

  // if this is just main exit-ing implicitly, and the status is 0, then we
  // don't need to do anything here and can just leave. if the status is
  // non-zero, though, then we need to report it.
  // (we may have warned about this earlier, if a situation justifies doing so)
  if (implicit && noExitRuntime && status === 0) {
    return;
  }

  if (noExitRuntime) {
    // if exit() was called, we may warn the user if the runtime isn't actually being shut down
    if (!implicit) {
      var msg = 'program exited (with status: ' + status + '), but EXIT_RUNTIME is not set, so halting execution but not exiting the runtime or preventing further async execution (build with EXIT_RUNTIME=1, if you want a true shutdown)';
      readyPromiseReject(msg);
      err(msg);
    }
  } else {

    EXITSTATUS = status;

    exitRuntime();

    if (Module['onExit']) Module['onExit'](status);

    ABORT = true;
  }

  quit_(status, new ExitStatus(status));
}

if (Module['preInit']) {
  if (typeof Module['preInit'] == 'function') Module['preInit'] = [Module['preInit']];
  while (Module['preInit'].length > 0) {
    Module['preInit'].pop()();
  }
}


  noExitRuntime = true;

run();









// Bindings utilities

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function WrapperObject() {
}
WrapperObject.prototype = Object.create(WrapperObject.prototype);
WrapperObject.prototype.constructor = WrapperObject;
WrapperObject.prototype.__class__ = WrapperObject;
WrapperObject.__cache__ = {};
Module['WrapperObject'] = WrapperObject;

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant)
    @param {*=} __class__ */
function getCache(__class__) {
  return (__class__ || WrapperObject).__cache__;
}
Module['getCache'] = getCache;

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant)
    @param {*=} __class__ */
function wrapPointer(ptr, __class__) {
  var cache = getCache(__class__);
  var ret = cache[ptr];
  if (ret) return ret;
  ret = Object.create((__class__ || WrapperObject).prototype);
  ret.ptr = ptr;
  return cache[ptr] = ret;
}
Module['wrapPointer'] = wrapPointer;

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function castObject(obj, __class__) {
  return wrapPointer(obj.ptr, __class__);
}
Module['castObject'] = castObject;

Module['NULL'] = wrapPointer(0);

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function destroy(obj) {
  if (!obj['__destroy__']) throw 'Error: Cannot destroy object. (Did you create it yourself?)';
  obj['__destroy__']();
  // Remove from cache, so the object can be GC'd and refs added onto it released
  delete getCache(obj.__class__)[obj.ptr];
}
Module['destroy'] = destroy;

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function compare(obj1, obj2) {
  return obj1.ptr === obj2.ptr;
}
Module['compare'] = compare;

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function getPointer(obj) {
  return obj.ptr;
}
Module['getPointer'] = getPointer;

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function getClass(obj) {
  return obj.__class__;
}
Module['getClass'] = getClass;

// Converts big (string or array) values into a C-style storage, in temporary space

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
var ensureCache = {
  buffer: 0,  // the main buffer of temporary storage
  size: 0,   // the size of buffer
  pos: 0,    // the next free offset in buffer
  temps: [], // extra allocations
  needed: 0, // the total size we need next time

  prepare: function() {
    if (ensureCache.needed) {
      // clear the temps
      for (var i = 0; i < ensureCache.temps.length; i++) {
        Module['_free'](ensureCache.temps[i]);
      }
      ensureCache.temps.length = 0;
      // prepare to allocate a bigger buffer
      Module['_free'](ensureCache.buffer);
      ensureCache.buffer = 0;
      ensureCache.size += ensureCache.needed;
      // clean up
      ensureCache.needed = 0;
    }
    if (!ensureCache.buffer) { // happens first time, or when we need to grow
      ensureCache.size += 128; // heuristic, avoid many small grow events
      ensureCache.buffer = Module['_malloc'](ensureCache.size);
      assert(ensureCache.buffer);
    }
    ensureCache.pos = 0;
  },
  alloc: function(array, view) {
    assert(ensureCache.buffer);
    var bytes = view.BYTES_PER_ELEMENT;
    var len = array.length * bytes;
    len = (len + 7) & -8; // keep things aligned to 8 byte boundaries
    var ret;
    if (ensureCache.pos + len >= ensureCache.size) {
      // we failed to allocate in the buffer, ensureCache time around :(
      assert(len > 0); // null terminator, at least
      ensureCache.needed += len;
      ret = Module['_malloc'](len);
      ensureCache.temps.push(ret);
    } else {
      // we can allocate in the buffer
      ret = ensureCache.buffer + ensureCache.pos;
      ensureCache.pos += len;
    }
    return ret;
  },
  copy: function(array, view, offset) {
    offset >>>= 0;
    var bytes = view.BYTES_PER_ELEMENT;
    switch (bytes) {
      case 2: offset >>>= 1; break;
      case 4: offset >>>= 2; break;
      case 8: offset >>>= 3; break;
    }
    for (var i = 0; i < array.length; i++) {
      view[offset + i] = array[i];
    }
  },
};

/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function ensureString(value) {
  if (typeof value === 'string') {
    var intArray = intArrayFromString(value);
    var offset = ensureCache.alloc(intArray, HEAP8);
    ensureCache.copy(intArray, HEAP8, offset);
    return offset;
  }
  return value;
}
/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function ensureInt8(value) {
  if (typeof value === 'object') {
    var offset = ensureCache.alloc(value, HEAP8);
    ensureCache.copy(value, HEAP8, offset);
    return offset;
  }
  return value;
}
/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function ensureInt16(value) {
  if (typeof value === 'object') {
    var offset = ensureCache.alloc(value, HEAP16);
    ensureCache.copy(value, HEAP16, offset);
    return offset;
  }
  return value;
}
/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function ensureInt32(value) {
  if (typeof value === 'object') {
    var offset = ensureCache.alloc(value, HEAP32);
    ensureCache.copy(value, HEAP32, offset);
    return offset;
  }
  return value;
}
/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function ensureFloat32(value) {
  if (typeof value === 'object') {
    var offset = ensureCache.alloc(value, HEAPF32);
    ensureCache.copy(value, HEAPF32, offset);
    return offset;
  }
  return value;
}
/** @suppress {duplicate} (TODO: avoid emitting this multiple times, it is redundant) */
function ensureFloat64(value) {
  if (typeof value === 'object') {
    var offset = ensureCache.alloc(value, HEAPF64);
    ensureCache.copy(value, HEAPF64, offset);
    return offset;
  }
  return value;
}


// PxBase
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBase() { throw "cannot construct a PxBase, no constructor in IDL" }
PxBase.prototype = Object.create(WrapperObject.prototype);
PxBase.prototype.constructor = PxBase;
PxBase.prototype.__class__ = PxBase;
PxBase.__cache__ = {};
Module['PxBase'] = PxBase;

PxBase.prototype['release'] = PxBase.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBase_release_0(self);
};;

PxBase.prototype['getConcreteTypeName'] = PxBase.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxBase_getConcreteTypeName_0(self));
};;

PxBase.prototype['getConcreteType'] = PxBase.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBase_getConcreteType_0(self);
};;

PxBase.prototype['setBaseFlag'] = PxBase.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxBase_setBaseFlag_2(self, flag, value);
};;

PxBase.prototype['setBaseFlags'] = PxBase.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxBase_setBaseFlags_1(self, inFlags);
};;

PxBase.prototype['getBaseFlags'] = PxBase.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBase_getBaseFlags_0(self), PxBaseFlags);
};;

PxBase.prototype['isReleasable'] = PxBase.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxBase_isReleasable_0(self));
};;

// PxActor
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxActor() { throw "cannot construct a PxActor, no constructor in IDL" }
PxActor.prototype = Object.create(PxBase.prototype);
PxActor.prototype.constructor = PxActor;
PxActor.prototype.__class__ = PxActor;
PxActor.__cache__ = {};
Module['PxActor'] = PxActor;

PxActor.prototype['getType'] = PxActor.prototype.getType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxActor_getType_0(self);
};;

PxActor.prototype['getScene'] = PxActor.prototype.getScene = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxActor_getScene_0(self), PxScene);
};;

PxActor.prototype['setName'] = PxActor.prototype.setName = /** @suppress {undefinedVars, duplicate} @this{Object} */function(name) {
  var self = this.ptr;
  ensureCache.prepare();
  if (name && typeof name === 'object') name = name.ptr;
  else name = ensureString(name);
  _emscripten_bind_PxActor_setName_1(self, name);
};;

PxActor.prototype['getName'] = PxActor.prototype.getName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxActor_getName_0(self));
};;

PxActor.prototype['getWorldBounds'] = PxActor.prototype.getWorldBounds = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inflation) {
  var self = this.ptr;
  if (inflation && typeof inflation === 'object') inflation = inflation.ptr;
  if (inflation === undefined) { return wrapPointer(_emscripten_bind_PxActor_getWorldBounds_0(self), PxBounds3) }
  return wrapPointer(_emscripten_bind_PxActor_getWorldBounds_1(self, inflation), PxBounds3);
};;

PxActor.prototype['setActorFlags'] = PxActor.prototype.setActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxActor_setActorFlags_1(self, flags);
};;

PxActor.prototype['getActorFlags'] = PxActor.prototype.getActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxActor_getActorFlags_0(self), PxActorFlags);
};;

PxActor.prototype['setDominanceGroup'] = PxActor.prototype.setDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(dominanceGroup) {
  var self = this.ptr;
  if (dominanceGroup && typeof dominanceGroup === 'object') dominanceGroup = dominanceGroup.ptr;
  _emscripten_bind_PxActor_setDominanceGroup_1(self, dominanceGroup);
};;

PxActor.prototype['getDominanceGroup'] = PxActor.prototype.getDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxActor_getDominanceGroup_0(self);
};;

PxActor.prototype['setOwnerClient'] = PxActor.prototype.setOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inClient) {
  var self = this.ptr;
  if (inClient && typeof inClient === 'object') inClient = inClient.ptr;
  _emscripten_bind_PxActor_setOwnerClient_1(self, inClient);
};;

PxActor.prototype['getOwnerClient'] = PxActor.prototype.getOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxActor_getOwnerClient_0(self);
};;

PxActor.prototype['release'] = PxActor.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxActor_release_0(self);
};;

PxActor.prototype['getConcreteTypeName'] = PxActor.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxActor_getConcreteTypeName_0(self));
};;

PxActor.prototype['getConcreteType'] = PxActor.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxActor_getConcreteType_0(self);
};;

PxActor.prototype['setBaseFlag'] = PxActor.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxActor_setBaseFlag_2(self, flag, value);
};;

PxActor.prototype['setBaseFlags'] = PxActor.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxActor_setBaseFlags_1(self, inFlags);
};;

PxActor.prototype['getBaseFlags'] = PxActor.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxActor_getBaseFlags_0(self), PxBaseFlags);
};;

PxActor.prototype['isReleasable'] = PxActor.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxActor_isReleasable_0(self));
};;

// PxActorShape
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxActorShape() { throw "cannot construct a PxActorShape, no constructor in IDL" }
PxActorShape.prototype = Object.create(WrapperObject.prototype);
PxActorShape.prototype.constructor = PxActorShape;
PxActorShape.prototype.__class__ = PxActorShape;
PxActorShape.__cache__ = {};
Module['PxActorShape'] = PxActorShape;

  PxActorShape.prototype['get_actor'] = PxActorShape.prototype.get_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxActorShape_get_actor_0(self), PxRigidActor);
};
    PxActorShape.prototype['set_actor'] = PxActorShape.prototype.set_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxActorShape_set_actor_1(self, arg0);
};
    Object.defineProperty(PxActorShape.prototype, 'actor', { get: PxActorShape.prototype.get_actor, set: PxActorShape.prototype.set_actor });
  PxActorShape.prototype['get_shape'] = PxActorShape.prototype.get_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxActorShape_get_shape_0(self), PxShape);
};
    PxActorShape.prototype['set_shape'] = PxActorShape.prototype.set_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxActorShape_set_shape_1(self, arg0);
};
    Object.defineProperty(PxActorShape.prototype, 'shape', { get: PxActorShape.prototype.get_shape, set: PxActorShape.prototype.set_shape });
  PxActorShape.prototype['__destroy__'] = PxActorShape.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxActorShape___destroy___0(self);
};
// PxQueryHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxQueryHit() { throw "cannot construct a PxQueryHit, no constructor in IDL" }
PxQueryHit.prototype = Object.create(PxActorShape.prototype);
PxQueryHit.prototype.constructor = PxQueryHit;
PxQueryHit.prototype.__class__ = PxQueryHit;
PxQueryHit.__cache__ = {};
Module['PxQueryHit'] = PxQueryHit;

  PxQueryHit.prototype['get_faceIndex'] = PxQueryHit.prototype.get_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxQueryHit_get_faceIndex_0(self);
};
    PxQueryHit.prototype['set_faceIndex'] = PxQueryHit.prototype.set_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQueryHit_set_faceIndex_1(self, arg0);
};
    Object.defineProperty(PxQueryHit.prototype, 'faceIndex', { get: PxQueryHit.prototype.get_faceIndex, set: PxQueryHit.prototype.set_faceIndex });
  PxQueryHit.prototype['get_actor'] = PxQueryHit.prototype.get_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxQueryHit_get_actor_0(self), PxRigidActor);
};
    PxQueryHit.prototype['set_actor'] = PxQueryHit.prototype.set_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQueryHit_set_actor_1(self, arg0);
};
    Object.defineProperty(PxQueryHit.prototype, 'actor', { get: PxQueryHit.prototype.get_actor, set: PxQueryHit.prototype.set_actor });
  PxQueryHit.prototype['get_shape'] = PxQueryHit.prototype.get_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxQueryHit_get_shape_0(self), PxShape);
};
    PxQueryHit.prototype['set_shape'] = PxQueryHit.prototype.set_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQueryHit_set_shape_1(self, arg0);
};
    Object.defineProperty(PxQueryHit.prototype, 'shape', { get: PxQueryHit.prototype.get_shape, set: PxQueryHit.prototype.set_shape });
  PxQueryHit.prototype['__destroy__'] = PxQueryHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxQueryHit___destroy___0(self);
};
// PxRigidActor
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRigidActor() { throw "cannot construct a PxRigidActor, no constructor in IDL" }
PxRigidActor.prototype = Object.create(PxActor.prototype);
PxRigidActor.prototype.constructor = PxRigidActor;
PxRigidActor.prototype.__class__ = PxRigidActor;
PxRigidActor.__cache__ = {};
Module['PxRigidActor'] = PxRigidActor;

PxRigidActor.prototype['getGlobalPose'] = PxRigidActor.prototype.getGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidActor_getGlobalPose_0(self), PxTransform);
};;

PxRigidActor.prototype['setGlobalPose'] = PxRigidActor.prototype.setGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose, autowake) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidActor_setGlobalPose_1(self, pose);  return }
  _emscripten_bind_PxRigidActor_setGlobalPose_2(self, pose, autowake);
};;

PxRigidActor.prototype['attachShape'] = PxRigidActor.prototype.attachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  return !!(_emscripten_bind_PxRigidActor_attachShape_1(self, shape));
};;

PxRigidActor.prototype['detachShape'] = PxRigidActor.prototype.detachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape, wakeOnLostTouch) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  if (wakeOnLostTouch && typeof wakeOnLostTouch === 'object') wakeOnLostTouch = wakeOnLostTouch.ptr;
  if (wakeOnLostTouch === undefined) { _emscripten_bind_PxRigidActor_detachShape_1(self, shape);  return }
  _emscripten_bind_PxRigidActor_detachShape_2(self, shape, wakeOnLostTouch);
};;

PxRigidActor.prototype['getNbShapes'] = PxRigidActor.prototype.getNbShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidActor_getNbShapes_0(self);
};;

PxRigidActor.prototype['getShapes'] = PxRigidActor.prototype.getShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function(userBuffer, bufferSize, startIndex) {
  var self = this.ptr;
  ensureCache.prepare();
  if (bufferSize && typeof bufferSize === 'object') bufferSize = bufferSize.ptr;
  if (startIndex && typeof startIndex === 'object') startIndex = startIndex.ptr;
  return _emscripten_bind_PxRigidActor_getShapes_3(self, userBuffer, bufferSize, startIndex);
};;

PxRigidActor.prototype['getType'] = PxRigidActor.prototype.getType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidActor_getType_0(self);
};;

PxRigidActor.prototype['getScene'] = PxRigidActor.prototype.getScene = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidActor_getScene_0(self), PxScene);
};;

PxRigidActor.prototype['setName'] = PxRigidActor.prototype.setName = /** @suppress {undefinedVars, duplicate} @this{Object} */function(name) {
  var self = this.ptr;
  ensureCache.prepare();
  if (name && typeof name === 'object') name = name.ptr;
  else name = ensureString(name);
  _emscripten_bind_PxRigidActor_setName_1(self, name);
};;

PxRigidActor.prototype['getName'] = PxRigidActor.prototype.getName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidActor_getName_0(self));
};;

PxRigidActor.prototype['getWorldBounds'] = PxRigidActor.prototype.getWorldBounds = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inflation) {
  var self = this.ptr;
  if (inflation && typeof inflation === 'object') inflation = inflation.ptr;
  if (inflation === undefined) { return wrapPointer(_emscripten_bind_PxRigidActor_getWorldBounds_0(self), PxBounds3) }
  return wrapPointer(_emscripten_bind_PxRigidActor_getWorldBounds_1(self, inflation), PxBounds3);
};;

PxRigidActor.prototype['setActorFlags'] = PxRigidActor.prototype.setActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxRigidActor_setActorFlags_1(self, flags);
};;

PxRigidActor.prototype['getActorFlags'] = PxRigidActor.prototype.getActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidActor_getActorFlags_0(self), PxActorFlags);
};;

PxRigidActor.prototype['setDominanceGroup'] = PxRigidActor.prototype.setDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(dominanceGroup) {
  var self = this.ptr;
  if (dominanceGroup && typeof dominanceGroup === 'object') dominanceGroup = dominanceGroup.ptr;
  _emscripten_bind_PxRigidActor_setDominanceGroup_1(self, dominanceGroup);
};;

PxRigidActor.prototype['getDominanceGroup'] = PxRigidActor.prototype.getDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidActor_getDominanceGroup_0(self);
};;

PxRigidActor.prototype['setOwnerClient'] = PxRigidActor.prototype.setOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inClient) {
  var self = this.ptr;
  if (inClient && typeof inClient === 'object') inClient = inClient.ptr;
  _emscripten_bind_PxRigidActor_setOwnerClient_1(self, inClient);
};;

PxRigidActor.prototype['getOwnerClient'] = PxRigidActor.prototype.getOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidActor_getOwnerClient_0(self);
};;

PxRigidActor.prototype['release'] = PxRigidActor.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidActor_release_0(self);
};;

PxRigidActor.prototype['getConcreteTypeName'] = PxRigidActor.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidActor_getConcreteTypeName_0(self));
};;

PxRigidActor.prototype['getConcreteType'] = PxRigidActor.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidActor_getConcreteType_0(self);
};;

PxRigidActor.prototype['setBaseFlag'] = PxRigidActor.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidActor_setBaseFlag_2(self, flag, value);
};;

PxRigidActor.prototype['setBaseFlags'] = PxRigidActor.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRigidActor_setBaseFlags_1(self, inFlags);
};;

PxRigidActor.prototype['getBaseFlags'] = PxRigidActor.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidActor_getBaseFlags_0(self), PxBaseFlags);
};;

PxRigidActor.prototype['isReleasable'] = PxRigidActor.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRigidActor_isReleasable_0(self));
};;

// PxSimulationEventCallback
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSimulationEventCallback() { throw "cannot construct a PxSimulationEventCallback, no constructor in IDL" }
PxSimulationEventCallback.prototype = Object.create(WrapperObject.prototype);
PxSimulationEventCallback.prototype.constructor = PxSimulationEventCallback;
PxSimulationEventCallback.prototype.__class__ = PxSimulationEventCallback;
PxSimulationEventCallback.__cache__ = {};
Module['PxSimulationEventCallback'] = PxSimulationEventCallback;

  PxSimulationEventCallback.prototype['__destroy__'] = PxSimulationEventCallback.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSimulationEventCallback___destroy___0(self);
};
// PxVehicleWheels
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheels() { throw "cannot construct a PxVehicleWheels, no constructor in IDL" }
PxVehicleWheels.prototype = Object.create(PxBase.prototype);
PxVehicleWheels.prototype.constructor = PxVehicleWheels;
PxVehicleWheels.prototype.__class__ = PxVehicleWheels;
PxVehicleWheels.__cache__ = {};
Module['PxVehicleWheels'] = PxVehicleWheels;

PxVehicleWheels.prototype['getVehicleType'] = PxVehicleWheels.prototype.getVehicleType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheels_getVehicleType_0(self);
};;

PxVehicleWheels.prototype['getRigidDynamicActor'] = PxVehicleWheels.prototype.getRigidDynamicActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheels_getRigidDynamicActor_0(self), PxRigidDynamic);
};;

PxVehicleWheels.prototype['computeForwardSpeed'] = PxVehicleWheels.prototype.computeForwardSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheels_computeForwardSpeed_0(self);
};;

PxVehicleWheels.prototype['computeSidewaysSpeed'] = PxVehicleWheels.prototype.computeSidewaysSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheels_computeSidewaysSpeed_0(self);
};;

PxVehicleWheels.prototype['getNbNonDrivenWheels'] = PxVehicleWheels.prototype.getNbNonDrivenWheels = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheels_getNbNonDrivenWheels_0(self);
};;

PxVehicleWheels.prototype['release'] = PxVehicleWheels.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheels_release_0(self);
};;

PxVehicleWheels.prototype['getConcreteTypeName'] = PxVehicleWheels.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxVehicleWheels_getConcreteTypeName_0(self));
};;

PxVehicleWheels.prototype['getConcreteType'] = PxVehicleWheels.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheels_getConcreteType_0(self);
};;

PxVehicleWheels.prototype['setBaseFlag'] = PxVehicleWheels.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxVehicleWheels_setBaseFlag_2(self, flag, value);
};;

PxVehicleWheels.prototype['setBaseFlags'] = PxVehicleWheels.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxVehicleWheels_setBaseFlags_1(self, inFlags);
};;

PxVehicleWheels.prototype['getBaseFlags'] = PxVehicleWheels.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheels_getBaseFlags_0(self), PxBaseFlags);
};;

PxVehicleWheels.prototype['isReleasable'] = PxVehicleWheels.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleWheels_isReleasable_0(self));
};;

  PxVehicleWheels.prototype['get_mWheelsSimData'] = PxVehicleWheels.prototype.get_mWheelsSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheels_get_mWheelsSimData_0(self), PxVehicleWheelsSimData);
};
    PxVehicleWheels.prototype['set_mWheelsSimData'] = PxVehicleWheels.prototype.set_mWheelsSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheels_set_mWheelsSimData_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheels.prototype, 'mWheelsSimData', { get: PxVehicleWheels.prototype.get_mWheelsSimData, set: PxVehicleWheels.prototype.set_mWheelsSimData });
  PxVehicleWheels.prototype['get_mWheelsDynData'] = PxVehicleWheels.prototype.get_mWheelsDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheels_get_mWheelsDynData_0(self), PxVehicleWheelsDynData);
};
    PxVehicleWheels.prototype['set_mWheelsDynData'] = PxVehicleWheels.prototype.set_mWheelsDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheels_set_mWheelsDynData_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheels.prototype, 'mWheelsDynData', { get: PxVehicleWheels.prototype.get_mWheelsDynData, set: PxVehicleWheels.prototype.set_mWheelsDynData });
// PxLocationHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxLocationHit() { throw "cannot construct a PxLocationHit, no constructor in IDL" }
PxLocationHit.prototype = Object.create(PxQueryHit.prototype);
PxLocationHit.prototype.constructor = PxLocationHit;
PxLocationHit.prototype.__class__ = PxLocationHit;
PxLocationHit.__cache__ = {};
Module['PxLocationHit'] = PxLocationHit;

  PxLocationHit.prototype['get_flags'] = PxLocationHit.prototype.get_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxLocationHit_get_flags_0(self), PxHitFlags);
};
    PxLocationHit.prototype['set_flags'] = PxLocationHit.prototype.set_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_flags_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'flags', { get: PxLocationHit.prototype.get_flags, set: PxLocationHit.prototype.set_flags });
  PxLocationHit.prototype['get_position'] = PxLocationHit.prototype.get_position = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxLocationHit_get_position_0(self), PxVec3);
};
    PxLocationHit.prototype['set_position'] = PxLocationHit.prototype.set_position = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_position_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'position', { get: PxLocationHit.prototype.get_position, set: PxLocationHit.prototype.set_position });
  PxLocationHit.prototype['get_normal'] = PxLocationHit.prototype.get_normal = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxLocationHit_get_normal_0(self), PxVec3);
};
    PxLocationHit.prototype['set_normal'] = PxLocationHit.prototype.set_normal = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_normal_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'normal', { get: PxLocationHit.prototype.get_normal, set: PxLocationHit.prototype.set_normal });
  PxLocationHit.prototype['get_distance'] = PxLocationHit.prototype.get_distance = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxLocationHit_get_distance_0(self);
};
    PxLocationHit.prototype['set_distance'] = PxLocationHit.prototype.set_distance = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_distance_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'distance', { get: PxLocationHit.prototype.get_distance, set: PxLocationHit.prototype.set_distance });
  PxLocationHit.prototype['get_actor'] = PxLocationHit.prototype.get_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxLocationHit_get_actor_0(self), PxRigidActor);
};
    PxLocationHit.prototype['set_actor'] = PxLocationHit.prototype.set_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_actor_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'actor', { get: PxLocationHit.prototype.get_actor, set: PxLocationHit.prototype.set_actor });
  PxLocationHit.prototype['get_shape'] = PxLocationHit.prototype.get_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxLocationHit_get_shape_0(self), PxShape);
};
    PxLocationHit.prototype['set_shape'] = PxLocationHit.prototype.set_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_shape_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'shape', { get: PxLocationHit.prototype.get_shape, set: PxLocationHit.prototype.set_shape });
  PxLocationHit.prototype['get_faceIndex'] = PxLocationHit.prototype.get_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxLocationHit_get_faceIndex_0(self);
};
    PxLocationHit.prototype['set_faceIndex'] = PxLocationHit.prototype.set_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxLocationHit_set_faceIndex_1(self, arg0);
};
    Object.defineProperty(PxLocationHit.prototype, 'faceIndex', { get: PxLocationHit.prototype.get_faceIndex, set: PxLocationHit.prototype.set_faceIndex });
  PxLocationHit.prototype['__destroy__'] = PxLocationHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxLocationHit___destroy___0(self);
};
// PxRigidBody
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRigidBody() { throw "cannot construct a PxRigidBody, no constructor in IDL" }
PxRigidBody.prototype = Object.create(PxRigidActor.prototype);
PxRigidBody.prototype.constructor = PxRigidBody;
PxRigidBody.prototype.__class__ = PxRigidBody;
PxRigidBody.__cache__ = {};
Module['PxRigidBody'] = PxRigidBody;

PxRigidBody.prototype['setCMassLocalPose'] = PxRigidBody.prototype.setCMassLocalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  _emscripten_bind_PxRigidBody_setCMassLocalPose_1(self, pose);
};;

PxRigidBody.prototype['getCMassLocalPose'] = PxRigidBody.prototype.getCMassLocalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getCMassLocalPose_0(self), PxTransform);
};;

PxRigidBody.prototype['setMass'] = PxRigidBody.prototype.setMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function(mass) {
  var self = this.ptr;
  if (mass && typeof mass === 'object') mass = mass.ptr;
  _emscripten_bind_PxRigidBody_setMass_1(self, mass);
};;

PxRigidBody.prototype['getMass'] = PxRigidBody.prototype.getMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getMass_0(self);
};;

PxRigidBody.prototype['getInvMass'] = PxRigidBody.prototype.getInvMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getInvMass_0(self);
};;

PxRigidBody.prototype['setMassSpaceInertiaTensor'] = PxRigidBody.prototype.setMassSpaceInertiaTensor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(m) {
  var self = this.ptr;
  if (m && typeof m === 'object') m = m.ptr;
  _emscripten_bind_PxRigidBody_setMassSpaceInertiaTensor_1(self, m);
};;

PxRigidBody.prototype['getMassSpaceInertiaTensor'] = PxRigidBody.prototype.getMassSpaceInertiaTensor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getMassSpaceInertiaTensor_0(self), PxVec3);
};;

PxRigidBody.prototype['getMassSpaceInvInertiaTensor'] = PxRigidBody.prototype.getMassSpaceInvInertiaTensor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getMassSpaceInvInertiaTensor_0(self), PxVec3);
};;

PxRigidBody.prototype['setLinearDamping'] = PxRigidBody.prototype.setLinearDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function(linDamp) {
  var self = this.ptr;
  if (linDamp && typeof linDamp === 'object') linDamp = linDamp.ptr;
  _emscripten_bind_PxRigidBody_setLinearDamping_1(self, linDamp);
};;

PxRigidBody.prototype['getLinearDamping'] = PxRigidBody.prototype.getLinearDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getLinearDamping_0(self);
};;

PxRigidBody.prototype['setAngularDamping'] = PxRigidBody.prototype.setAngularDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function(angDamp) {
  var self = this.ptr;
  if (angDamp && typeof angDamp === 'object') angDamp = angDamp.ptr;
  _emscripten_bind_PxRigidBody_setAngularDamping_1(self, angDamp);
};;

PxRigidBody.prototype['getAngularDamping'] = PxRigidBody.prototype.getAngularDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getAngularDamping_0(self);
};;

PxRigidBody.prototype['getLinearVelocity'] = PxRigidBody.prototype.getLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getLinearVelocity_0(self), PxVec3);
};;

PxRigidBody.prototype['setLinearVelocity'] = PxRigidBody.prototype.setLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(linVel, autowake) {
  var self = this.ptr;
  if (linVel && typeof linVel === 'object') linVel = linVel.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidBody_setLinearVelocity_1(self, linVel);  return }
  _emscripten_bind_PxRigidBody_setLinearVelocity_2(self, linVel, autowake);
};;

PxRigidBody.prototype['getAngularVelocity'] = PxRigidBody.prototype.getAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getAngularVelocity_0(self), PxVec3);
};;

PxRigidBody.prototype['setAngularVelocity'] = PxRigidBody.prototype.setAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(angVel, autowake) {
  var self = this.ptr;
  if (angVel && typeof angVel === 'object') angVel = angVel.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidBody_setAngularVelocity_1(self, angVel);  return }
  _emscripten_bind_PxRigidBody_setAngularVelocity_2(self, angVel, autowake);
};;

PxRigidBody.prototype['getMaxLinearVelocity'] = PxRigidBody.prototype.getMaxLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getMaxLinearVelocity_0(self);
};;

PxRigidBody.prototype['setMaxLinearVelocity'] = PxRigidBody.prototype.setMaxLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxLinVel) {
  var self = this.ptr;
  if (maxLinVel && typeof maxLinVel === 'object') maxLinVel = maxLinVel.ptr;
  _emscripten_bind_PxRigidBody_setMaxLinearVelocity_1(self, maxLinVel);
};;

PxRigidBody.prototype['getMaxAngularVelocity'] = PxRigidBody.prototype.getMaxAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getMaxAngularVelocity_0(self);
};;

PxRigidBody.prototype['setMaxAngularVelocity'] = PxRigidBody.prototype.setMaxAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxAngVel) {
  var self = this.ptr;
  if (maxAngVel && typeof maxAngVel === 'object') maxAngVel = maxAngVel.ptr;
  _emscripten_bind_PxRigidBody_setMaxAngularVelocity_1(self, maxAngVel);
};;

PxRigidBody.prototype['addForce'] = PxRigidBody.prototype.addForce = /** @suppress {undefinedVars, duplicate} @this{Object} */function(force, mode, autowake) {
  var self = this.ptr;
  if (force && typeof force === 'object') force = force.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (mode === undefined) { _emscripten_bind_PxRigidBody_addForce_1(self, force);  return }
  if (autowake === undefined) { _emscripten_bind_PxRigidBody_addForce_2(self, force, mode);  return }
  _emscripten_bind_PxRigidBody_addForce_3(self, force, mode, autowake);
};;

PxRigidBody.prototype['addTorque'] = PxRigidBody.prototype.addTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(torque, mode, autowake) {
  var self = this.ptr;
  if (torque && typeof torque === 'object') torque = torque.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (mode === undefined) { _emscripten_bind_PxRigidBody_addTorque_1(self, torque);  return }
  if (autowake === undefined) { _emscripten_bind_PxRigidBody_addTorque_2(self, torque, mode);  return }
  _emscripten_bind_PxRigidBody_addTorque_3(self, torque, mode, autowake);
};;

PxRigidBody.prototype['clearForce'] = PxRigidBody.prototype.clearForce = /** @suppress {undefinedVars, duplicate} @this{Object} */function(mode) {
  var self = this.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  _emscripten_bind_PxRigidBody_clearForce_1(self, mode);
};;

PxRigidBody.prototype['clearTorque'] = PxRigidBody.prototype.clearTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(mode) {
  var self = this.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  _emscripten_bind_PxRigidBody_clearTorque_1(self, mode);
};;

PxRigidBody.prototype['setForceAndTorque'] = PxRigidBody.prototype.setForceAndTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(force, torque, mode) {
  var self = this.ptr;
  if (force && typeof force === 'object') force = force.ptr;
  if (torque && typeof torque === 'object') torque = torque.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  if (mode === undefined) { _emscripten_bind_PxRigidBody_setForceAndTorque_2(self, force, torque);  return }
  _emscripten_bind_PxRigidBody_setForceAndTorque_3(self, force, torque, mode);
};;

PxRigidBody.prototype['setRigidBodyFlag'] = PxRigidBody.prototype.setRigidBodyFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidBody_setRigidBodyFlag_2(self, flag, value);
};;

PxRigidBody.prototype['setRigidBodyFlags'] = PxRigidBody.prototype.setRigidBodyFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRigidBody_setRigidBodyFlags_1(self, inFlags);
};;

PxRigidBody.prototype['getRigidBodyFlags'] = PxRigidBody.prototype.getRigidBodyFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getRigidBodyFlags_0(self), PxRigidBodyFlags);
};;

PxRigidBody.prototype['setMinCCDAdvanceCoefficient'] = PxRigidBody.prototype.setMinCCDAdvanceCoefficient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(advanceCoefficient) {
  var self = this.ptr;
  if (advanceCoefficient && typeof advanceCoefficient === 'object') advanceCoefficient = advanceCoefficient.ptr;
  _emscripten_bind_PxRigidBody_setMinCCDAdvanceCoefficient_1(self, advanceCoefficient);
};;

PxRigidBody.prototype['getMinCCDAdvanceCoefficient'] = PxRigidBody.prototype.getMinCCDAdvanceCoefficient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getMinCCDAdvanceCoefficient_0(self);
};;

PxRigidBody.prototype['setMaxDepenetrationVelocity'] = PxRigidBody.prototype.setMaxDepenetrationVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(biasClamp) {
  var self = this.ptr;
  if (biasClamp && typeof biasClamp === 'object') biasClamp = biasClamp.ptr;
  _emscripten_bind_PxRigidBody_setMaxDepenetrationVelocity_1(self, biasClamp);
};;

PxRigidBody.prototype['getMaxDepenetrationVelocity'] = PxRigidBody.prototype.getMaxDepenetrationVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getMaxDepenetrationVelocity_0(self);
};;

PxRigidBody.prototype['setMaxContactImpulse'] = PxRigidBody.prototype.setMaxContactImpulse = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxImpulse) {
  var self = this.ptr;
  if (maxImpulse && typeof maxImpulse === 'object') maxImpulse = maxImpulse.ptr;
  _emscripten_bind_PxRigidBody_setMaxContactImpulse_1(self, maxImpulse);
};;

PxRigidBody.prototype['getMaxContactImpulse'] = PxRigidBody.prototype.getMaxContactImpulse = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getMaxContactImpulse_0(self);
};;

PxRigidBody.prototype['getInternalIslandNodeIndex'] = PxRigidBody.prototype.getInternalIslandNodeIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getInternalIslandNodeIndex_0(self);
};;

PxRigidBody.prototype['getType'] = PxRigidBody.prototype.getType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getType_0(self);
};;

PxRigidBody.prototype['getScene'] = PxRigidBody.prototype.getScene = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getScene_0(self), PxScene);
};;

PxRigidBody.prototype['setName'] = PxRigidBody.prototype.setName = /** @suppress {undefinedVars, duplicate} @this{Object} */function(name) {
  var self = this.ptr;
  ensureCache.prepare();
  if (name && typeof name === 'object') name = name.ptr;
  else name = ensureString(name);
  _emscripten_bind_PxRigidBody_setName_1(self, name);
};;

PxRigidBody.prototype['getName'] = PxRigidBody.prototype.getName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidBody_getName_0(self));
};;

PxRigidBody.prototype['getWorldBounds'] = PxRigidBody.prototype.getWorldBounds = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inflation) {
  var self = this.ptr;
  if (inflation && typeof inflation === 'object') inflation = inflation.ptr;
  if (inflation === undefined) { return wrapPointer(_emscripten_bind_PxRigidBody_getWorldBounds_0(self), PxBounds3) }
  return wrapPointer(_emscripten_bind_PxRigidBody_getWorldBounds_1(self, inflation), PxBounds3);
};;

PxRigidBody.prototype['setActorFlags'] = PxRigidBody.prototype.setActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxRigidBody_setActorFlags_1(self, flags);
};;

PxRigidBody.prototype['getActorFlags'] = PxRigidBody.prototype.getActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getActorFlags_0(self), PxActorFlags);
};;

PxRigidBody.prototype['setDominanceGroup'] = PxRigidBody.prototype.setDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(dominanceGroup) {
  var self = this.ptr;
  if (dominanceGroup && typeof dominanceGroup === 'object') dominanceGroup = dominanceGroup.ptr;
  _emscripten_bind_PxRigidBody_setDominanceGroup_1(self, dominanceGroup);
};;

PxRigidBody.prototype['getDominanceGroup'] = PxRigidBody.prototype.getDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getDominanceGroup_0(self);
};;

PxRigidBody.prototype['setOwnerClient'] = PxRigidBody.prototype.setOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inClient) {
  var self = this.ptr;
  if (inClient && typeof inClient === 'object') inClient = inClient.ptr;
  _emscripten_bind_PxRigidBody_setOwnerClient_1(self, inClient);
};;

PxRigidBody.prototype['getOwnerClient'] = PxRigidBody.prototype.getOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getOwnerClient_0(self);
};;

PxRigidBody.prototype['release'] = PxRigidBody.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidBody_release_0(self);
};;

PxRigidBody.prototype['getConcreteTypeName'] = PxRigidBody.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidBody_getConcreteTypeName_0(self));
};;

PxRigidBody.prototype['getConcreteType'] = PxRigidBody.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getConcreteType_0(self);
};;

PxRigidBody.prototype['setBaseFlag'] = PxRigidBody.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidBody_setBaseFlag_2(self, flag, value);
};;

PxRigidBody.prototype['setBaseFlags'] = PxRigidBody.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRigidBody_setBaseFlags_1(self, inFlags);
};;

PxRigidBody.prototype['getBaseFlags'] = PxRigidBody.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getBaseFlags_0(self), PxBaseFlags);
};;

PxRigidBody.prototype['isReleasable'] = PxRigidBody.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRigidBody_isReleasable_0(self));
};;

PxRigidBody.prototype['getGlobalPose'] = PxRigidBody.prototype.getGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidBody_getGlobalPose_0(self), PxTransform);
};;

PxRigidBody.prototype['setGlobalPose'] = PxRigidBody.prototype.setGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose, autowake) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidBody_setGlobalPose_1(self, pose);  return }
  _emscripten_bind_PxRigidBody_setGlobalPose_2(self, pose, autowake);
};;

PxRigidBody.prototype['attachShape'] = PxRigidBody.prototype.attachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  return !!(_emscripten_bind_PxRigidBody_attachShape_1(self, shape));
};;

PxRigidBody.prototype['detachShape'] = PxRigidBody.prototype.detachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape, wakeOnLostTouch) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  if (wakeOnLostTouch && typeof wakeOnLostTouch === 'object') wakeOnLostTouch = wakeOnLostTouch.ptr;
  if (wakeOnLostTouch === undefined) { _emscripten_bind_PxRigidBody_detachShape_1(self, shape);  return }
  _emscripten_bind_PxRigidBody_detachShape_2(self, shape, wakeOnLostTouch);
};;

PxRigidBody.prototype['getNbShapes'] = PxRigidBody.prototype.getNbShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidBody_getNbShapes_0(self);
};;

PxRigidBody.prototype['getShapes'] = PxRigidBody.prototype.getShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function(userBuffer, bufferSize, startIndex) {
  var self = this.ptr;
  ensureCache.prepare();
  if (bufferSize && typeof bufferSize === 'object') bufferSize = bufferSize.ptr;
  if (startIndex && typeof startIndex === 'object') startIndex = startIndex.ptr;
  return _emscripten_bind_PxRigidBody_getShapes_3(self, userBuffer, bufferSize, startIndex);
};;

// SimplePxSimulationEventCallback
/** @suppress {undefinedVars, duplicate} @this{Object} */function SimplePxSimulationEventCallback() { throw "cannot construct a SimplePxSimulationEventCallback, no constructor in IDL" }
SimplePxSimulationEventCallback.prototype = Object.create(PxSimulationEventCallback.prototype);
SimplePxSimulationEventCallback.prototype.constructor = SimplePxSimulationEventCallback;
SimplePxSimulationEventCallback.prototype.__class__ = SimplePxSimulationEventCallback;
SimplePxSimulationEventCallback.__cache__ = {};
Module['SimplePxSimulationEventCallback'] = SimplePxSimulationEventCallback;

SimplePxSimulationEventCallback.prototype['cbFun'] = SimplePxSimulationEventCallback.prototype.cbFun = /** @suppress {undefinedVars, duplicate} @this{Object} */function(count) {
  var self = this.ptr;
  if (count && typeof count === 'object') count = count.ptr;
  _emscripten_bind_SimplePxSimulationEventCallback_cbFun_1(self, count);
};;

  SimplePxSimulationEventCallback.prototype['__destroy__'] = SimplePxSimulationEventCallback.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_SimplePxSimulationEventCallback___destroy___0(self);
};
// PxVehicleDrive
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDrive() { throw "cannot construct a PxVehicleDrive, no constructor in IDL" }
PxVehicleDrive.prototype = Object.create(PxVehicleWheels.prototype);
PxVehicleDrive.prototype.constructor = PxVehicleDrive;
PxVehicleDrive.prototype.__class__ = PxVehicleDrive;
PxVehicleDrive.__cache__ = {};
Module['PxVehicleDrive'] = PxVehicleDrive;

PxVehicleDrive.prototype['release'] = PxVehicleDrive.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDrive_release_0(self);
};;

PxVehicleDrive.prototype['getConcreteTypeName'] = PxVehicleDrive.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxVehicleDrive_getConcreteTypeName_0(self));
};;

PxVehicleDrive.prototype['getConcreteType'] = PxVehicleDrive.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive_getConcreteType_0(self);
};;

PxVehicleDrive.prototype['setBaseFlag'] = PxVehicleDrive.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxVehicleDrive_setBaseFlag_2(self, flag, value);
};;

PxVehicleDrive.prototype['setBaseFlags'] = PxVehicleDrive.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxVehicleDrive_setBaseFlags_1(self, inFlags);
};;

PxVehicleDrive.prototype['getBaseFlags'] = PxVehicleDrive.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive_getBaseFlags_0(self), PxBaseFlags);
};;

PxVehicleDrive.prototype['isReleasable'] = PxVehicleDrive.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDrive_isReleasable_0(self));
};;

PxVehicleDrive.prototype['getVehicleType'] = PxVehicleDrive.prototype.getVehicleType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive_getVehicleType_0(self);
};;

PxVehicleDrive.prototype['getRigidDynamicActor'] = PxVehicleDrive.prototype.getRigidDynamicActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive_getRigidDynamicActor_0(self), PxRigidDynamic);
};;

PxVehicleDrive.prototype['computeForwardSpeed'] = PxVehicleDrive.prototype.computeForwardSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive_computeForwardSpeed_0(self);
};;

PxVehicleDrive.prototype['computeSidewaysSpeed'] = PxVehicleDrive.prototype.computeSidewaysSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive_computeSidewaysSpeed_0(self);
};;

PxVehicleDrive.prototype['getNbNonDrivenWheels'] = PxVehicleDrive.prototype.getNbNonDrivenWheels = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive_getNbNonDrivenWheels_0(self);
};;

  PxVehicleDrive.prototype['get_mDriveDynData'] = PxVehicleDrive.prototype.get_mDriveDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive_get_mDriveDynData_0(self), PxVehicleDriveDynData);
};
    PxVehicleDrive.prototype['set_mDriveDynData'] = PxVehicleDrive.prototype.set_mDriveDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive_set_mDriveDynData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive.prototype, 'mDriveDynData', { get: PxVehicleDrive.prototype.get_mDriveDynData, set: PxVehicleDrive.prototype.set_mDriveDynData });
  PxVehicleDrive.prototype['get_mWheelsSimData'] = PxVehicleDrive.prototype.get_mWheelsSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive_get_mWheelsSimData_0(self), PxVehicleWheelsSimData);
};
    PxVehicleDrive.prototype['set_mWheelsSimData'] = PxVehicleDrive.prototype.set_mWheelsSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive_set_mWheelsSimData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive.prototype, 'mWheelsSimData', { get: PxVehicleDrive.prototype.get_mWheelsSimData, set: PxVehicleDrive.prototype.set_mWheelsSimData });
  PxVehicleDrive.prototype['get_mWheelsDynData'] = PxVehicleDrive.prototype.get_mWheelsDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive_get_mWheelsDynData_0(self), PxVehicleWheelsDynData);
};
    PxVehicleDrive.prototype['set_mWheelsDynData'] = PxVehicleDrive.prototype.set_mWheelsDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive_set_mWheelsDynData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive.prototype, 'mWheelsDynData', { get: PxVehicleDrive.prototype.get_mWheelsDynData, set: PxVehicleDrive.prototype.set_mWheelsDynData });
// PxVehicleDriveSimData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDriveSimData() {
  this.ptr = _emscripten_bind_PxVehicleDriveSimData_PxVehicleDriveSimData_0();
  getCache(PxVehicleDriveSimData)[this.ptr] = this;
};;
PxVehicleDriveSimData.prototype = Object.create(WrapperObject.prototype);
PxVehicleDriveSimData.prototype.constructor = PxVehicleDriveSimData;
PxVehicleDriveSimData.prototype.__class__ = PxVehicleDriveSimData;
PxVehicleDriveSimData.__cache__ = {};
Module['PxVehicleDriveSimData'] = PxVehicleDriveSimData;

PxVehicleDriveSimData.prototype['getEngineData'] = PxVehicleDriveSimData.prototype.getEngineData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData_getEngineData_0(self), PxVehicleEngineData);
};;

PxVehicleDriveSimData.prototype['setEngineData'] = PxVehicleDriveSimData.prototype.setEngineData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(engine) {
  var self = this.ptr;
  if (engine && typeof engine === 'object') engine = engine.ptr;
  _emscripten_bind_PxVehicleDriveSimData_setEngineData_1(self, engine);
};;

PxVehicleDriveSimData.prototype['getGearsData'] = PxVehicleDriveSimData.prototype.getGearsData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData_getGearsData_0(self), PxVehicleGearsData);
};;

PxVehicleDriveSimData.prototype['setGearsData'] = PxVehicleDriveSimData.prototype.setGearsData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(gears) {
  var self = this.ptr;
  if (gears && typeof gears === 'object') gears = gears.ptr;
  _emscripten_bind_PxVehicleDriveSimData_setGearsData_1(self, gears);
};;

PxVehicleDriveSimData.prototype['getClutchData'] = PxVehicleDriveSimData.prototype.getClutchData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData_getClutchData_0(self), PxVehicleClutchData);
};;

PxVehicleDriveSimData.prototype['setClutchData'] = PxVehicleDriveSimData.prototype.setClutchData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(clutch) {
  var self = this.ptr;
  if (clutch && typeof clutch === 'object') clutch = clutch.ptr;
  _emscripten_bind_PxVehicleDriveSimData_setClutchData_1(self, clutch);
};;

PxVehicleDriveSimData.prototype['getAutoBoxData'] = PxVehicleDriveSimData.prototype.getAutoBoxData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData_getAutoBoxData_0(self), PxVehicleAutoBoxData);
};;

PxVehicleDriveSimData.prototype['setAutoBoxData'] = PxVehicleDriveSimData.prototype.setAutoBoxData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(clutch) {
  var self = this.ptr;
  if (clutch && typeof clutch === 'object') clutch = clutch.ptr;
  _emscripten_bind_PxVehicleDriveSimData_setAutoBoxData_1(self, clutch);
};;

  PxVehicleDriveSimData.prototype['__destroy__'] = PxVehicleDriveSimData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDriveSimData___destroy___0(self);
};
// PxGeometry
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxGeometry() { throw "cannot construct a PxGeometry, no constructor in IDL" }
PxGeometry.prototype = Object.create(WrapperObject.prototype);
PxGeometry.prototype.constructor = PxGeometry;
PxGeometry.prototype.__class__ = PxGeometry;
PxGeometry.__cache__ = {};
Module['PxGeometry'] = PxGeometry;

  PxGeometry.prototype['__destroy__'] = PxGeometry.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxGeometry___destroy___0(self);
};
// PxCpuDispatcher
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxCpuDispatcher() { throw "cannot construct a PxCpuDispatcher, no constructor in IDL" }
PxCpuDispatcher.prototype = Object.create(WrapperObject.prototype);
PxCpuDispatcher.prototype.constructor = PxCpuDispatcher;
PxCpuDispatcher.prototype.__class__ = PxCpuDispatcher;
PxCpuDispatcher.__cache__ = {};
Module['PxCpuDispatcher'] = PxCpuDispatcher;

  PxCpuDispatcher.prototype['__destroy__'] = PxCpuDispatcher.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxCpuDispatcher___destroy___0(self);
};
// PxJoint
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxJoint() { throw "cannot construct a PxJoint, no constructor in IDL" }
PxJoint.prototype = Object.create(PxBase.prototype);
PxJoint.prototype.constructor = PxJoint;
PxJoint.prototype.__class__ = PxJoint;
PxJoint.__cache__ = {};
Module['PxJoint'] = PxJoint;

PxJoint.prototype['release'] = PxJoint.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxJoint_release_0(self);
};;

PxJoint.prototype['getConcreteTypeName'] = PxJoint.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxJoint_getConcreteTypeName_0(self));
};;

PxJoint.prototype['getConcreteType'] = PxJoint.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxJoint_getConcreteType_0(self);
};;

PxJoint.prototype['setBaseFlag'] = PxJoint.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxJoint_setBaseFlag_2(self, flag, value);
};;

PxJoint.prototype['setBaseFlags'] = PxJoint.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxJoint_setBaseFlags_1(self, inFlags);
};;

PxJoint.prototype['getBaseFlags'] = PxJoint.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxJoint_getBaseFlags_0(self), PxBaseFlags);
};;

PxJoint.prototype['isReleasable'] = PxJoint.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxJoint_isReleasable_0(self));
};;

// VoidPtr
/** @suppress {undefinedVars, duplicate} @this{Object} */function VoidPtr() { throw "cannot construct a VoidPtr, no constructor in IDL" }
VoidPtr.prototype = Object.create(WrapperObject.prototype);
VoidPtr.prototype.constructor = VoidPtr;
VoidPtr.prototype.__class__ = VoidPtr;
VoidPtr.__cache__ = {};
Module['VoidPtr'] = VoidPtr;

  VoidPtr.prototype['__destroy__'] = VoidPtr.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_VoidPtr___destroy___0(self);
};
// PxTopLevelFunctions
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxTopLevelFunctions() { throw "cannot construct a PxTopLevelFunctions, no constructor in IDL" }
PxTopLevelFunctions.prototype = Object.create(WrapperObject.prototype);
PxTopLevelFunctions.prototype.constructor = PxTopLevelFunctions;
PxTopLevelFunctions.prototype.__class__ = PxTopLevelFunctions;
PxTopLevelFunctions.__cache__ = {};
Module['PxTopLevelFunctions'] = PxTopLevelFunctions;

PxTopLevelFunctions.prototype['DefaultFilterShader'] = PxTopLevelFunctions.prototype.DefaultFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_DefaultFilterShader_0(self), PxSimulationFilterShader);
};;

PxTopLevelFunctions.prototype['DefaultWheelSceneQueryPreFilterBlocking'] = PxTopLevelFunctions.prototype.DefaultWheelSceneQueryPreFilterBlocking = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPreFilterBlocking_0(self), PxBatchQueryPreFilterShader);
};;

PxTopLevelFunctions.prototype['DefaultWheelSceneQueryPostFilterBlocking'] = PxTopLevelFunctions.prototype.DefaultWheelSceneQueryPostFilterBlocking = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_DefaultWheelSceneQueryPostFilterBlocking_0(self), PxBatchQueryPostFilterShader);
};;

PxTopLevelFunctions.prototype['CreateCooking'] = PxTopLevelFunctions.prototype.CreateCooking = /** @suppress {undefinedVars, duplicate} @this{Object} */function(version, foundation, scale) {
  var self = this.ptr;
  if (version && typeof version === 'object') version = version.ptr;
  if (foundation && typeof foundation === 'object') foundation = foundation.ptr;
  if (scale && typeof scale === 'object') scale = scale.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_CreateCooking_3(self, version, foundation, scale), PxCooking);
};;

PxTopLevelFunctions.prototype['CreateFoundation'] = PxTopLevelFunctions.prototype.CreateFoundation = /** @suppress {undefinedVars, duplicate} @this{Object} */function(version, allocator, errorCallback) {
  var self = this.ptr;
  if (version && typeof version === 'object') version = version.ptr;
  if (allocator && typeof allocator === 'object') allocator = allocator.ptr;
  if (errorCallback && typeof errorCallback === 'object') errorCallback = errorCallback.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_CreateFoundation_3(self, version, allocator, errorCallback), PxFoundation);
};;

PxTopLevelFunctions.prototype['CreatePhysics'] = PxTopLevelFunctions.prototype.CreatePhysics = /** @suppress {undefinedVars, duplicate} @this{Object} */function(version, foundation, params) {
  var self = this.ptr;
  if (version && typeof version === 'object') version = version.ptr;
  if (foundation && typeof foundation === 'object') foundation = foundation.ptr;
  if (params && typeof params === 'object') params = params.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_CreatePhysics_3(self, version, foundation, params), PxPhysics);
};;

PxTopLevelFunctions.prototype['DefaultCpuDispatcherCreate'] = PxTopLevelFunctions.prototype.DefaultCpuDispatcherCreate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(numThreads) {
  var self = this.ptr;
  if (numThreads && typeof numThreads === 'object') numThreads = numThreads.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_DefaultCpuDispatcherCreate_1(self, numThreads), PxDefaultCpuDispatcher);
};;

PxTopLevelFunctions.prototype['InitExtensions'] = PxTopLevelFunctions.prototype.InitExtensions = /** @suppress {undefinedVars, duplicate} @this{Object} */function(physics) {
  var self = this.ptr;
  if (physics && typeof physics === 'object') physics = physics.ptr;
  return !!(_emscripten_bind_PxTopLevelFunctions_InitExtensions_1(self, physics));
};;

PxTopLevelFunctions.prototype['RevoluteJointCreate'] = PxTopLevelFunctions.prototype.RevoluteJointCreate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(physics, actor0, localFrame0, actor1, localFrame1) {
  var self = this.ptr;
  if (physics && typeof physics === 'object') physics = physics.ptr;
  if (actor0 && typeof actor0 === 'object') actor0 = actor0.ptr;
  if (localFrame0 && typeof localFrame0 === 'object') localFrame0 = localFrame0.ptr;
  if (actor1 && typeof actor1 === 'object') actor1 = actor1.ptr;
  if (localFrame1 && typeof localFrame1 === 'object') localFrame1 = localFrame1.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_RevoluteJointCreate_5(self, physics, actor0, localFrame0, actor1, localFrame1), PxRevoluteJoint);
};;

PxTopLevelFunctions.prototype['getU8At'] = PxTopLevelFunctions.prototype.getU8At = /** @suppress {undefinedVars, duplicate} @this{Object} */function(base, index) {
  var self = this.ptr;
  if (base && typeof base === 'object') base = base.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return _emscripten_bind_PxTopLevelFunctions_getU8At_2(self, base, index);
};;

PxTopLevelFunctions.prototype['getVec3At'] = PxTopLevelFunctions.prototype.getVec3At = /** @suppress {undefinedVars, duplicate} @this{Object} */function(base, index) {
  var self = this.ptr;
  if (base && typeof base === 'object') base = base.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_PxTopLevelFunctions_getVec3At_2(self, base, index), PxVec3);
};;

  PxTopLevelFunctions.prototype['get_PHYSICS_VERSION'] = PxTopLevelFunctions.prototype.get_PHYSICS_VERSION = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxTopLevelFunctions_get_PHYSICS_VERSION_0(self);
};
    Object.defineProperty(PxTopLevelFunctions.prototype, 'PHYSICS_VERSION', { get: PxTopLevelFunctions.prototype.get_PHYSICS_VERSION });
  PxTopLevelFunctions.prototype['__destroy__'] = PxTopLevelFunctions.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxTopLevelFunctions___destroy___0(self);
};
// PxActorFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxActorFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxActorFlags_PxActorFlags_1(flags);
  getCache(PxActorFlags)[this.ptr] = this;
};;
PxActorFlags.prototype = Object.create(WrapperObject.prototype);
PxActorFlags.prototype.constructor = PxActorFlags;
PxActorFlags.prototype.__class__ = PxActorFlags;
PxActorFlags.__cache__ = {};
Module['PxActorFlags'] = PxActorFlags;

PxActorFlags.prototype['isSet'] = PxActorFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxActorFlags_isSet_1(self, flag));
};;

PxActorFlags.prototype['set'] = PxActorFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxActorFlags_set_1(self, flag);
};;

PxActorFlags.prototype['clear'] = PxActorFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxActorFlags_clear_1(self, flag);
};;

  PxActorFlags.prototype['__destroy__'] = PxActorFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxActorFlags___destroy___0(self);
};
// PxBatchQuery
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBatchQuery() { throw "cannot construct a PxBatchQuery, no constructor in IDL" }
PxBatchQuery.prototype = Object.create(WrapperObject.prototype);
PxBatchQuery.prototype.constructor = PxBatchQuery;
PxBatchQuery.prototype.__class__ = PxBatchQuery;
PxBatchQuery.__cache__ = {};
Module['PxBatchQuery'] = PxBatchQuery;

PxBatchQuery.prototype['execute'] = PxBatchQuery.prototype.execute = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBatchQuery_execute_0(self);
};;

PxBatchQuery.prototype['getPreFilterShader'] = PxBatchQuery.prototype.getPreFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQuery_getPreFilterShader_0(self), PxBatchQueryPreFilterShader);
};;

PxBatchQuery.prototype['getPostFilterShader'] = PxBatchQuery.prototype.getPostFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQuery_getPostFilterShader_0(self), PxBatchQueryPostFilterShader);
};;

PxBatchQuery.prototype['getFilterShaderData'] = PxBatchQuery.prototype.getFilterShaderData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQuery_getFilterShaderData_0(self);
};;

PxBatchQuery.prototype['getFilterShaderDataSize'] = PxBatchQuery.prototype.getFilterShaderDataSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQuery_getFilterShaderDataSize_0(self);
};;

PxBatchQuery.prototype['setUserMemory'] = PxBatchQuery.prototype.setUserMemory = /** @suppress {undefinedVars, duplicate} @this{Object} */function(userMemory) {
  var self = this.ptr;
  if (userMemory && typeof userMemory === 'object') userMemory = userMemory.ptr;
  _emscripten_bind_PxBatchQuery_setUserMemory_1(self, userMemory);
};;

PxBatchQuery.prototype['getUserMemory'] = PxBatchQuery.prototype.getUserMemory = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQuery_getUserMemory_0(self), PxBatchQueryMemory);
};;

PxBatchQuery.prototype['release'] = PxBatchQuery.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBatchQuery_release_0(self);
};;

// PxBatchQueryDesc
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBatchQueryDesc(maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute) {
  if (maxRaycastsPerExecute && typeof maxRaycastsPerExecute === 'object') maxRaycastsPerExecute = maxRaycastsPerExecute.ptr;
  if (maxSweepsPerExecute && typeof maxSweepsPerExecute === 'object') maxSweepsPerExecute = maxSweepsPerExecute.ptr;
  if (maxOverlapsPerExecute && typeof maxOverlapsPerExecute === 'object') maxOverlapsPerExecute = maxOverlapsPerExecute.ptr;
  this.ptr = _emscripten_bind_PxBatchQueryDesc_PxBatchQueryDesc_3(maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute);
  getCache(PxBatchQueryDesc)[this.ptr] = this;
};;
PxBatchQueryDesc.prototype = Object.create(WrapperObject.prototype);
PxBatchQueryDesc.prototype.constructor = PxBatchQueryDesc;
PxBatchQueryDesc.prototype.__class__ = PxBatchQueryDesc;
PxBatchQueryDesc.__cache__ = {};
Module['PxBatchQueryDesc'] = PxBatchQueryDesc;

PxBatchQueryDesc.prototype['isValid'] = PxBatchQueryDesc.prototype.isValid = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxBatchQueryDesc_isValid_0(self));
};;

  PxBatchQueryDesc.prototype['get_filterShaderData'] = PxBatchQueryDesc.prototype.get_filterShaderData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQueryDesc_get_filterShaderData_0(self);
};
    PxBatchQueryDesc.prototype['set_filterShaderData'] = PxBatchQueryDesc.prototype.set_filterShaderData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryDesc_set_filterShaderData_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryDesc.prototype, 'filterShaderData', { get: PxBatchQueryDesc.prototype.get_filterShaderData, set: PxBatchQueryDesc.prototype.set_filterShaderData });
  PxBatchQueryDesc.prototype['get_filterShaderDataSize'] = PxBatchQueryDesc.prototype.get_filterShaderDataSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQueryDesc_get_filterShaderDataSize_0(self);
};
    PxBatchQueryDesc.prototype['set_filterShaderDataSize'] = PxBatchQueryDesc.prototype.set_filterShaderDataSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryDesc_set_filterShaderDataSize_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryDesc.prototype, 'filterShaderDataSize', { get: PxBatchQueryDesc.prototype.get_filterShaderDataSize, set: PxBatchQueryDesc.prototype.set_filterShaderDataSize });
  PxBatchQueryDesc.prototype['get_preFilterShader'] = PxBatchQueryDesc.prototype.get_preFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryDesc_get_preFilterShader_0(self), PxBatchQueryPreFilterShader);
};
    PxBatchQueryDesc.prototype['set_preFilterShader'] = PxBatchQueryDesc.prototype.set_preFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryDesc_set_preFilterShader_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryDesc.prototype, 'preFilterShader', { get: PxBatchQueryDesc.prototype.get_preFilterShader, set: PxBatchQueryDesc.prototype.set_preFilterShader });
  PxBatchQueryDesc.prototype['get_postFilterShader'] = PxBatchQueryDesc.prototype.get_postFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryDesc_get_postFilterShader_0(self), PxBatchQueryPostFilterShader);
};
    PxBatchQueryDesc.prototype['set_postFilterShader'] = PxBatchQueryDesc.prototype.set_postFilterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryDesc_set_postFilterShader_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryDesc.prototype, 'postFilterShader', { get: PxBatchQueryDesc.prototype.get_postFilterShader, set: PxBatchQueryDesc.prototype.set_postFilterShader });
  PxBatchQueryDesc.prototype['get_queryMemory'] = PxBatchQueryDesc.prototype.get_queryMemory = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryDesc_get_queryMemory_0(self), PxBatchQueryMemory);
};
    PxBatchQueryDesc.prototype['set_queryMemory'] = PxBatchQueryDesc.prototype.set_queryMemory = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryDesc_set_queryMemory_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryDesc.prototype, 'queryMemory', { get: PxBatchQueryDesc.prototype.get_queryMemory, set: PxBatchQueryDesc.prototype.set_queryMemory });
  PxBatchQueryDesc.prototype['__destroy__'] = PxBatchQueryDesc.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBatchQueryDesc___destroy___0(self);
};
// PxBatchQueryMemory
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBatchQueryMemory() { throw "cannot construct a PxBatchQueryMemory, no constructor in IDL" }
PxBatchQueryMemory.prototype = Object.create(WrapperObject.prototype);
PxBatchQueryMemory.prototype.constructor = PxBatchQueryMemory;
PxBatchQueryMemory.prototype.__class__ = PxBatchQueryMemory;
PxBatchQueryMemory.__cache__ = {};
Module['PxBatchQueryMemory'] = PxBatchQueryMemory;

  PxBatchQueryMemory.prototype['get_userRaycastResultBuffer'] = PxBatchQueryMemory.prototype.get_userRaycastResultBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryMemory_get_userRaycastResultBuffer_0(self), PxRaycastQueryResult);
};
    PxBatchQueryMemory.prototype['set_userRaycastResultBuffer'] = PxBatchQueryMemory.prototype.set_userRaycastResultBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_userRaycastResultBuffer_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'userRaycastResultBuffer', { get: PxBatchQueryMemory.prototype.get_userRaycastResultBuffer, set: PxBatchQueryMemory.prototype.set_userRaycastResultBuffer });
  PxBatchQueryMemory.prototype['get_userRaycastTouchBuffer'] = PxBatchQueryMemory.prototype.get_userRaycastTouchBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryMemory_get_userRaycastTouchBuffer_0(self), PxRaycastHit);
};
    PxBatchQueryMemory.prototype['set_userRaycastTouchBuffer'] = PxBatchQueryMemory.prototype.set_userRaycastTouchBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_userRaycastTouchBuffer_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'userRaycastTouchBuffer', { get: PxBatchQueryMemory.prototype.get_userRaycastTouchBuffer, set: PxBatchQueryMemory.prototype.set_userRaycastTouchBuffer });
  PxBatchQueryMemory.prototype['get_userSweepResultBuffer'] = PxBatchQueryMemory.prototype.get_userSweepResultBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryMemory_get_userSweepResultBuffer_0(self), PxSweepQueryResult);
};
    PxBatchQueryMemory.prototype['set_userSweepResultBuffer'] = PxBatchQueryMemory.prototype.set_userSweepResultBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_userSweepResultBuffer_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'userSweepResultBuffer', { get: PxBatchQueryMemory.prototype.get_userSweepResultBuffer, set: PxBatchQueryMemory.prototype.set_userSweepResultBuffer });
  PxBatchQueryMemory.prototype['get_userSweepTouchBuffer'] = PxBatchQueryMemory.prototype.get_userSweepTouchBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryMemory_get_userSweepTouchBuffer_0(self), PxSweepHit);
};
    PxBatchQueryMemory.prototype['set_userSweepTouchBuffer'] = PxBatchQueryMemory.prototype.set_userSweepTouchBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_userSweepTouchBuffer_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'userSweepTouchBuffer', { get: PxBatchQueryMemory.prototype.get_userSweepTouchBuffer, set: PxBatchQueryMemory.prototype.set_userSweepTouchBuffer });
  PxBatchQueryMemory.prototype['get_userOverlapResultBuffer'] = PxBatchQueryMemory.prototype.get_userOverlapResultBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryMemory_get_userOverlapResultBuffer_0(self), PxOverlapQueryResult);
};
    PxBatchQueryMemory.prototype['set_userOverlapResultBuffer'] = PxBatchQueryMemory.prototype.set_userOverlapResultBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_userOverlapResultBuffer_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'userOverlapResultBuffer', { get: PxBatchQueryMemory.prototype.get_userOverlapResultBuffer, set: PxBatchQueryMemory.prototype.set_userOverlapResultBuffer });
  PxBatchQueryMemory.prototype['get_userOverlapTouchBuffer'] = PxBatchQueryMemory.prototype.get_userOverlapTouchBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBatchQueryMemory_get_userOverlapTouchBuffer_0(self), PxOverlapHit);
};
    PxBatchQueryMemory.prototype['set_userOverlapTouchBuffer'] = PxBatchQueryMemory.prototype.set_userOverlapTouchBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_userOverlapTouchBuffer_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'userOverlapTouchBuffer', { get: PxBatchQueryMemory.prototype.get_userOverlapTouchBuffer, set: PxBatchQueryMemory.prototype.set_userOverlapTouchBuffer });
  PxBatchQueryMemory.prototype['get_raycastTouchBufferSize'] = PxBatchQueryMemory.prototype.get_raycastTouchBufferSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQueryMemory_get_raycastTouchBufferSize_0(self);
};
    PxBatchQueryMemory.prototype['set_raycastTouchBufferSize'] = PxBatchQueryMemory.prototype.set_raycastTouchBufferSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_raycastTouchBufferSize_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'raycastTouchBufferSize', { get: PxBatchQueryMemory.prototype.get_raycastTouchBufferSize, set: PxBatchQueryMemory.prototype.set_raycastTouchBufferSize });
  PxBatchQueryMemory.prototype['get_sweepTouchBufferSize'] = PxBatchQueryMemory.prototype.get_sweepTouchBufferSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQueryMemory_get_sweepTouchBufferSize_0(self);
};
    PxBatchQueryMemory.prototype['set_sweepTouchBufferSize'] = PxBatchQueryMemory.prototype.set_sweepTouchBufferSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_sweepTouchBufferSize_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'sweepTouchBufferSize', { get: PxBatchQueryMemory.prototype.get_sweepTouchBufferSize, set: PxBatchQueryMemory.prototype.set_sweepTouchBufferSize });
  PxBatchQueryMemory.prototype['get_overlapTouchBufferSize'] = PxBatchQueryMemory.prototype.get_overlapTouchBufferSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBatchQueryMemory_get_overlapTouchBufferSize_0(self);
};
    PxBatchQueryMemory.prototype['set_overlapTouchBufferSize'] = PxBatchQueryMemory.prototype.set_overlapTouchBufferSize = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBatchQueryMemory_set_overlapTouchBufferSize_1(self, arg0);
};
    Object.defineProperty(PxBatchQueryMemory.prototype, 'overlapTouchBufferSize', { get: PxBatchQueryMemory.prototype.get_overlapTouchBufferSize, set: PxBatchQueryMemory.prototype.set_overlapTouchBufferSize });
  PxBatchQueryMemory.prototype['__destroy__'] = PxBatchQueryMemory.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBatchQueryMemory___destroy___0(self);
};
// PxBatchQueryPostFilterShader
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBatchQueryPostFilterShader() { throw "cannot construct a PxBatchQueryPostFilterShader, no constructor in IDL" }
PxBatchQueryPostFilterShader.prototype = Object.create(WrapperObject.prototype);
PxBatchQueryPostFilterShader.prototype.constructor = PxBatchQueryPostFilterShader;
PxBatchQueryPostFilterShader.prototype.__class__ = PxBatchQueryPostFilterShader;
PxBatchQueryPostFilterShader.__cache__ = {};
Module['PxBatchQueryPostFilterShader'] = PxBatchQueryPostFilterShader;

  PxBatchQueryPostFilterShader.prototype['__destroy__'] = PxBatchQueryPostFilterShader.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBatchQueryPostFilterShader___destroy___0(self);
};
// PxBatchQueryPreFilterShader
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBatchQueryPreFilterShader() { throw "cannot construct a PxBatchQueryPreFilterShader, no constructor in IDL" }
PxBatchQueryPreFilterShader.prototype = Object.create(WrapperObject.prototype);
PxBatchQueryPreFilterShader.prototype.constructor = PxBatchQueryPreFilterShader;
PxBatchQueryPreFilterShader.prototype.__class__ = PxBatchQueryPreFilterShader;
PxBatchQueryPreFilterShader.__cache__ = {};
Module['PxBatchQueryPreFilterShader'] = PxBatchQueryPreFilterShader;

  PxBatchQueryPreFilterShader.prototype['__destroy__'] = PxBatchQueryPreFilterShader.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBatchQueryPreFilterShader___destroy___0(self);
};
// PxFilterData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxFilterData(w0, w1, w2, w3) {
  if (w0 && typeof w0 === 'object') w0 = w0.ptr;
  if (w1 && typeof w1 === 'object') w1 = w1.ptr;
  if (w2 && typeof w2 === 'object') w2 = w2.ptr;
  if (w3 && typeof w3 === 'object') w3 = w3.ptr;
  if (w0 === undefined) { this.ptr = _emscripten_bind_PxFilterData_PxFilterData_0(); getCache(PxFilterData)[this.ptr] = this;return }
  if (w1 === undefined) { this.ptr = _emscripten_bind_PxFilterData_PxFilterData_1(w0); getCache(PxFilterData)[this.ptr] = this;return }
  if (w2 === undefined) { this.ptr = _emscripten_bind_PxFilterData_PxFilterData_2(w0, w1); getCache(PxFilterData)[this.ptr] = this;return }
  if (w3 === undefined) { this.ptr = _emscripten_bind_PxFilterData_PxFilterData_3(w0, w1, w2); getCache(PxFilterData)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxFilterData_PxFilterData_4(w0, w1, w2, w3);
  getCache(PxFilterData)[this.ptr] = this;
};;
PxFilterData.prototype = Object.create(WrapperObject.prototype);
PxFilterData.prototype.constructor = PxFilterData;
PxFilterData.prototype.__class__ = PxFilterData;
PxFilterData.__cache__ = {};
Module['PxFilterData'] = PxFilterData;

  PxFilterData.prototype['get_word0'] = PxFilterData.prototype.get_word0 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxFilterData_get_word0_0(self);
};
    PxFilterData.prototype['set_word0'] = PxFilterData.prototype.set_word0 = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxFilterData_set_word0_1(self, arg0);
};
    Object.defineProperty(PxFilterData.prototype, 'word0', { get: PxFilterData.prototype.get_word0, set: PxFilterData.prototype.set_word0 });
  PxFilterData.prototype['get_word1'] = PxFilterData.prototype.get_word1 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxFilterData_get_word1_0(self);
};
    PxFilterData.prototype['set_word1'] = PxFilterData.prototype.set_word1 = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxFilterData_set_word1_1(self, arg0);
};
    Object.defineProperty(PxFilterData.prototype, 'word1', { get: PxFilterData.prototype.get_word1, set: PxFilterData.prototype.set_word1 });
  PxFilterData.prototype['get_word2'] = PxFilterData.prototype.get_word2 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxFilterData_get_word2_0(self);
};
    PxFilterData.prototype['set_word2'] = PxFilterData.prototype.set_word2 = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxFilterData_set_word2_1(self, arg0);
};
    Object.defineProperty(PxFilterData.prototype, 'word2', { get: PxFilterData.prototype.get_word2, set: PxFilterData.prototype.set_word2 });
  PxFilterData.prototype['get_word3'] = PxFilterData.prototype.get_word3 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxFilterData_get_word3_0(self);
};
    PxFilterData.prototype['set_word3'] = PxFilterData.prototype.set_word3 = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxFilterData_set_word3_1(self, arg0);
};
    Object.defineProperty(PxFilterData.prototype, 'word3', { get: PxFilterData.prototype.get_word3, set: PxFilterData.prototype.set_word3 });
  PxFilterData.prototype['__destroy__'] = PxFilterData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxFilterData___destroy___0(self);
};
// PxHitFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxHitFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxHitFlags_PxHitFlags_1(flags);
  getCache(PxHitFlags)[this.ptr] = this;
};;
PxHitFlags.prototype = Object.create(WrapperObject.prototype);
PxHitFlags.prototype.constructor = PxHitFlags;
PxHitFlags.prototype.__class__ = PxHitFlags;
PxHitFlags.__cache__ = {};
Module['PxHitFlags'] = PxHitFlags;

PxHitFlags.prototype['isSet'] = PxHitFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxHitFlags_isSet_1(self, flag));
};;

PxHitFlags.prototype['set'] = PxHitFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxHitFlags_set_1(self, flag);
};;

PxHitFlags.prototype['clear'] = PxHitFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxHitFlags_clear_1(self, flag);
};;

  PxHitFlags.prototype['__destroy__'] = PxHitFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxHitFlags___destroy___0(self);
};
// PxOverlapHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxOverlapHit() { throw "cannot construct a PxOverlapHit, no constructor in IDL" }
PxOverlapHit.prototype = Object.create(WrapperObject.prototype);
PxOverlapHit.prototype.constructor = PxOverlapHit;
PxOverlapHit.prototype.__class__ = PxOverlapHit;
PxOverlapHit.__cache__ = {};
Module['PxOverlapHit'] = PxOverlapHit;

  PxOverlapHit.prototype['__destroy__'] = PxOverlapHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxOverlapHit___destroy___0(self);
};
// PxOverlapQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxOverlapQueryResult() { throw "cannot construct a PxOverlapQueryResult, no constructor in IDL" }
PxOverlapQueryResult.prototype = Object.create(WrapperObject.prototype);
PxOverlapQueryResult.prototype.constructor = PxOverlapQueryResult;
PxOverlapQueryResult.prototype.__class__ = PxOverlapQueryResult;
PxOverlapQueryResult.__cache__ = {};
Module['PxOverlapQueryResult'] = PxOverlapQueryResult;

PxOverlapQueryResult.prototype['getNbAnyHits'] = PxOverlapQueryResult.prototype.getNbAnyHits = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxOverlapQueryResult_getNbAnyHits_0(self);
};;

PxOverlapQueryResult.prototype['getAnyHit'] = PxOverlapQueryResult.prototype.getAnyHit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_PxOverlapQueryResult_getAnyHit_1(self, index), PxOverlapHit);
};;

  PxOverlapQueryResult.prototype['get_block'] = PxOverlapQueryResult.prototype.get_block = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxOverlapQueryResult_get_block_0(self), PxOverlapHit);
};
    PxOverlapQueryResult.prototype['set_block'] = PxOverlapQueryResult.prototype.set_block = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxOverlapQueryResult_set_block_1(self, arg0);
};
    Object.defineProperty(PxOverlapQueryResult.prototype, 'block', { get: PxOverlapQueryResult.prototype.get_block, set: PxOverlapQueryResult.prototype.set_block });
  PxOverlapQueryResult.prototype['get_touches'] = PxOverlapQueryResult.prototype.get_touches = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxOverlapQueryResult_get_touches_0(self), PxOverlapHit);
};
    PxOverlapQueryResult.prototype['set_touches'] = PxOverlapQueryResult.prototype.set_touches = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxOverlapQueryResult_set_touches_1(self, arg0);
};
    Object.defineProperty(PxOverlapQueryResult.prototype, 'touches', { get: PxOverlapQueryResult.prototype.get_touches, set: PxOverlapQueryResult.prototype.set_touches });
  PxOverlapQueryResult.prototype['get_nbTouches'] = PxOverlapQueryResult.prototype.get_nbTouches = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxOverlapQueryResult_get_nbTouches_0(self);
};
    PxOverlapQueryResult.prototype['set_nbTouches'] = PxOverlapQueryResult.prototype.set_nbTouches = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxOverlapQueryResult_set_nbTouches_1(self, arg0);
};
    Object.defineProperty(PxOverlapQueryResult.prototype, 'nbTouches', { get: PxOverlapQueryResult.prototype.get_nbTouches, set: PxOverlapQueryResult.prototype.set_nbTouches });
  PxOverlapQueryResult.prototype['get_userData'] = PxOverlapQueryResult.prototype.get_userData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxOverlapQueryResult_get_userData_0(self);
};
    PxOverlapQueryResult.prototype['set_userData'] = PxOverlapQueryResult.prototype.set_userData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxOverlapQueryResult_set_userData_1(self, arg0);
};
    Object.defineProperty(PxOverlapQueryResult.prototype, 'userData', { get: PxOverlapQueryResult.prototype.get_userData, set: PxOverlapQueryResult.prototype.set_userData });
  PxOverlapQueryResult.prototype['get_queryStatus'] = PxOverlapQueryResult.prototype.get_queryStatus = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxOverlapQueryResult_get_queryStatus_0(self);
};
    PxOverlapQueryResult.prototype['set_queryStatus'] = PxOverlapQueryResult.prototype.set_queryStatus = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxOverlapQueryResult_set_queryStatus_1(self, arg0);
};
    Object.defineProperty(PxOverlapQueryResult.prototype, 'queryStatus', { get: PxOverlapQueryResult.prototype.get_queryStatus, set: PxOverlapQueryResult.prototype.set_queryStatus });
  PxOverlapQueryResult.prototype['get_hasBlock'] = PxOverlapQueryResult.prototype.get_hasBlock = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxOverlapQueryResult_get_hasBlock_0(self));
};
    PxOverlapQueryResult.prototype['set_hasBlock'] = PxOverlapQueryResult.prototype.set_hasBlock = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxOverlapQueryResult_set_hasBlock_1(self, arg0);
};
    Object.defineProperty(PxOverlapQueryResult.prototype, 'hasBlock', { get: PxOverlapQueryResult.prototype.get_hasBlock, set: PxOverlapQueryResult.prototype.set_hasBlock });
  PxOverlapQueryResult.prototype['__destroy__'] = PxOverlapQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxOverlapQueryResult___destroy___0(self);
};
// PxMaterial
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxMaterial() { throw "cannot construct a PxMaterial, no constructor in IDL" }
PxMaterial.prototype = Object.create(PxBase.prototype);
PxMaterial.prototype.constructor = PxMaterial;
PxMaterial.prototype.__class__ = PxMaterial;
PxMaterial.__cache__ = {};
Module['PxMaterial'] = PxMaterial;

PxMaterial.prototype['release'] = PxMaterial.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxMaterial_release_0(self);
};;

PxMaterial.prototype['getConcreteTypeName'] = PxMaterial.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxMaterial_getConcreteTypeName_0(self));
};;

PxMaterial.prototype['getConcreteType'] = PxMaterial.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxMaterial_getConcreteType_0(self);
};;

PxMaterial.prototype['setBaseFlag'] = PxMaterial.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxMaterial_setBaseFlag_2(self, flag, value);
};;

PxMaterial.prototype['setBaseFlags'] = PxMaterial.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxMaterial_setBaseFlags_1(self, inFlags);
};;

PxMaterial.prototype['getBaseFlags'] = PxMaterial.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxMaterial_getBaseFlags_0(self), PxBaseFlags);
};;

PxMaterial.prototype['isReleasable'] = PxMaterial.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxMaterial_isReleasable_0(self));
};;

// PxPhysics
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxPhysics() { throw "cannot construct a PxPhysics, no constructor in IDL" }
PxPhysics.prototype = Object.create(WrapperObject.prototype);
PxPhysics.prototype.constructor = PxPhysics;
PxPhysics.prototype.__class__ = PxPhysics;
PxPhysics.__cache__ = {};
Module['PxPhysics'] = PxPhysics;

PxPhysics.prototype['getFoundation'] = PxPhysics.prototype.getFoundation = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_getFoundation_0(self), PxFoundation);
};;

PxPhysics.prototype['getTolerancesScale'] = PxPhysics.prototype.getTolerancesScale = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_getTolerancesScale_0(self), PxTolerancesScale);
};;

PxPhysics.prototype['createScene'] = PxPhysics.prototype.createScene = /** @suppress {undefinedVars, duplicate} @this{Object} */function(sceneDesc) {
  var self = this.ptr;
  if (sceneDesc && typeof sceneDesc === 'object') sceneDesc = sceneDesc.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_createScene_1(self, sceneDesc), PxScene);
};;

PxPhysics.prototype['createRigidStatic'] = PxPhysics.prototype.createRigidStatic = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_createRigidStatic_1(self, pose), PxRigidStatic);
};;

PxPhysics.prototype['createRigidDynamic'] = PxPhysics.prototype.createRigidDynamic = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_createRigidDynamic_1(self, pose), PxRigidDynamic);
};;

PxPhysics.prototype['createShape'] = PxPhysics.prototype.createShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(geometry, material, isExclusive, shapeFlags) {
  var self = this.ptr;
  if (geometry && typeof geometry === 'object') geometry = geometry.ptr;
  if (material && typeof material === 'object') material = material.ptr;
  if (isExclusive && typeof isExclusive === 'object') isExclusive = isExclusive.ptr;
  if (shapeFlags && typeof shapeFlags === 'object') shapeFlags = shapeFlags.ptr;
  if (isExclusive === undefined) { return wrapPointer(_emscripten_bind_PxPhysics_createShape_2(self, geometry, material), PxShape) }
  if (shapeFlags === undefined) { return wrapPointer(_emscripten_bind_PxPhysics_createShape_3(self, geometry, material, isExclusive), PxShape) }
  return wrapPointer(_emscripten_bind_PxPhysics_createShape_4(self, geometry, material, isExclusive, shapeFlags), PxShape);
};;

PxPhysics.prototype['getNbShapes'] = PxPhysics.prototype.getNbShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxPhysics_getNbShapes_0(self);
};;

PxPhysics.prototype['createMaterial'] = PxPhysics.prototype.createMaterial = /** @suppress {undefinedVars, duplicate} @this{Object} */function(staticFriction, dynamicFriction, restitution) {
  var self = this.ptr;
  if (staticFriction && typeof staticFriction === 'object') staticFriction = staticFriction.ptr;
  if (dynamicFriction && typeof dynamicFriction === 'object') dynamicFriction = dynamicFriction.ptr;
  if (restitution && typeof restitution === 'object') restitution = restitution.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_createMaterial_3(self, staticFriction, dynamicFriction, restitution), PxMaterial);
};;

PxPhysics.prototype['getPhysicsInsertionCallback'] = PxPhysics.prototype.getPhysicsInsertionCallback = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxPhysics_getPhysicsInsertionCallback_0(self), PxPhysicsInsertionCallback);
};;

  PxPhysics.prototype['__destroy__'] = PxPhysics.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxPhysics___destroy___0(self);
};
// PxRaycastHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRaycastHit() {
  this.ptr = _emscripten_bind_PxRaycastHit_PxRaycastHit_0();
  getCache(PxRaycastHit)[this.ptr] = this;
};;
PxRaycastHit.prototype = Object.create(PxQueryHit.prototype);
PxRaycastHit.prototype.constructor = PxRaycastHit;
PxRaycastHit.prototype.__class__ = PxRaycastHit;
PxRaycastHit.__cache__ = {};
Module['PxRaycastHit'] = PxRaycastHit;

  PxRaycastHit.prototype['get_u'] = PxRaycastHit.prototype.get_u = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastHit_get_u_0(self);
};
    PxRaycastHit.prototype['set_u'] = PxRaycastHit.prototype.set_u = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_u_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'u', { get: PxRaycastHit.prototype.get_u, set: PxRaycastHit.prototype.set_u });
  PxRaycastHit.prototype['get_v'] = PxRaycastHit.prototype.get_v = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastHit_get_v_0(self);
};
    PxRaycastHit.prototype['set_v'] = PxRaycastHit.prototype.set_v = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_v_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'v', { get: PxRaycastHit.prototype.get_v, set: PxRaycastHit.prototype.set_v });
  PxRaycastHit.prototype['get_actor'] = PxRaycastHit.prototype.get_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastHit_get_actor_0(self), PxRigidActor);
};
    PxRaycastHit.prototype['set_actor'] = PxRaycastHit.prototype.set_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_actor_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'actor', { get: PxRaycastHit.prototype.get_actor, set: PxRaycastHit.prototype.set_actor });
  PxRaycastHit.prototype['get_shape'] = PxRaycastHit.prototype.get_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastHit_get_shape_0(self), PxShape);
};
    PxRaycastHit.prototype['set_shape'] = PxRaycastHit.prototype.set_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_shape_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'shape', { get: PxRaycastHit.prototype.get_shape, set: PxRaycastHit.prototype.set_shape });
  PxRaycastHit.prototype['get_flags'] = PxRaycastHit.prototype.get_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastHit_get_flags_0(self), PxHitFlags);
};
    PxRaycastHit.prototype['set_flags'] = PxRaycastHit.prototype.set_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_flags_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'flags', { get: PxRaycastHit.prototype.get_flags, set: PxRaycastHit.prototype.set_flags });
  PxRaycastHit.prototype['get_position'] = PxRaycastHit.prototype.get_position = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastHit_get_position_0(self), PxVec3);
};
    PxRaycastHit.prototype['set_position'] = PxRaycastHit.prototype.set_position = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_position_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'position', { get: PxRaycastHit.prototype.get_position, set: PxRaycastHit.prototype.set_position });
  PxRaycastHit.prototype['get_normal'] = PxRaycastHit.prototype.get_normal = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastHit_get_normal_0(self), PxVec3);
};
    PxRaycastHit.prototype['set_normal'] = PxRaycastHit.prototype.set_normal = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_normal_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'normal', { get: PxRaycastHit.prototype.get_normal, set: PxRaycastHit.prototype.set_normal });
  PxRaycastHit.prototype['get_distance'] = PxRaycastHit.prototype.get_distance = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastHit_get_distance_0(self);
};
    PxRaycastHit.prototype['set_distance'] = PxRaycastHit.prototype.set_distance = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_distance_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'distance', { get: PxRaycastHit.prototype.get_distance, set: PxRaycastHit.prototype.set_distance });
  PxRaycastHit.prototype['get_faceIndex'] = PxRaycastHit.prototype.get_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastHit_get_faceIndex_0(self);
};
    PxRaycastHit.prototype['set_faceIndex'] = PxRaycastHit.prototype.set_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastHit_set_faceIndex_1(self, arg0);
};
    Object.defineProperty(PxRaycastHit.prototype, 'faceIndex', { get: PxRaycastHit.prototype.get_faceIndex, set: PxRaycastHit.prototype.set_faceIndex });
  PxRaycastHit.prototype['__destroy__'] = PxRaycastHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRaycastHit___destroy___0(self);
};
// PxRaycastQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRaycastQueryResult() { throw "cannot construct a PxRaycastQueryResult, no constructor in IDL" }
PxRaycastQueryResult.prototype = Object.create(WrapperObject.prototype);
PxRaycastQueryResult.prototype.constructor = PxRaycastQueryResult;
PxRaycastQueryResult.prototype.__class__ = PxRaycastQueryResult;
PxRaycastQueryResult.__cache__ = {};
Module['PxRaycastQueryResult'] = PxRaycastQueryResult;

PxRaycastQueryResult.prototype['getNbAnyHits'] = PxRaycastQueryResult.prototype.getNbAnyHits = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastQueryResult_getNbAnyHits_0(self);
};;

PxRaycastQueryResult.prototype['getAnyHit'] = PxRaycastQueryResult.prototype.getAnyHit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastQueryResult_getAnyHit_1(self, index), PxRaycastHit);
};;

  PxRaycastQueryResult.prototype['get_block'] = PxRaycastQueryResult.prototype.get_block = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastQueryResult_get_block_0(self), PxRaycastHit);
};
    PxRaycastQueryResult.prototype['set_block'] = PxRaycastQueryResult.prototype.set_block = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastQueryResult_set_block_1(self, arg0);
};
    Object.defineProperty(PxRaycastQueryResult.prototype, 'block', { get: PxRaycastQueryResult.prototype.get_block, set: PxRaycastQueryResult.prototype.set_block });
  PxRaycastQueryResult.prototype['get_touches'] = PxRaycastQueryResult.prototype.get_touches = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRaycastQueryResult_get_touches_0(self), PxRaycastHit);
};
    PxRaycastQueryResult.prototype['set_touches'] = PxRaycastQueryResult.prototype.set_touches = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastQueryResult_set_touches_1(self, arg0);
};
    Object.defineProperty(PxRaycastQueryResult.prototype, 'touches', { get: PxRaycastQueryResult.prototype.get_touches, set: PxRaycastQueryResult.prototype.set_touches });
  PxRaycastQueryResult.prototype['get_nbTouches'] = PxRaycastQueryResult.prototype.get_nbTouches = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastQueryResult_get_nbTouches_0(self);
};
    PxRaycastQueryResult.prototype['set_nbTouches'] = PxRaycastQueryResult.prototype.set_nbTouches = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastQueryResult_set_nbTouches_1(self, arg0);
};
    Object.defineProperty(PxRaycastQueryResult.prototype, 'nbTouches', { get: PxRaycastQueryResult.prototype.get_nbTouches, set: PxRaycastQueryResult.prototype.set_nbTouches });
  PxRaycastQueryResult.prototype['get_userData'] = PxRaycastQueryResult.prototype.get_userData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastQueryResult_get_userData_0(self);
};
    PxRaycastQueryResult.prototype['set_userData'] = PxRaycastQueryResult.prototype.set_userData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastQueryResult_set_userData_1(self, arg0);
};
    Object.defineProperty(PxRaycastQueryResult.prototype, 'userData', { get: PxRaycastQueryResult.prototype.get_userData, set: PxRaycastQueryResult.prototype.set_userData });
  PxRaycastQueryResult.prototype['get_queryStatus'] = PxRaycastQueryResult.prototype.get_queryStatus = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRaycastQueryResult_get_queryStatus_0(self);
};
    PxRaycastQueryResult.prototype['set_queryStatus'] = PxRaycastQueryResult.prototype.set_queryStatus = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastQueryResult_set_queryStatus_1(self, arg0);
};
    Object.defineProperty(PxRaycastQueryResult.prototype, 'queryStatus', { get: PxRaycastQueryResult.prototype.get_queryStatus, set: PxRaycastQueryResult.prototype.set_queryStatus });
  PxRaycastQueryResult.prototype['get_hasBlock'] = PxRaycastQueryResult.prototype.get_hasBlock = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRaycastQueryResult_get_hasBlock_0(self));
};
    PxRaycastQueryResult.prototype['set_hasBlock'] = PxRaycastQueryResult.prototype.set_hasBlock = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxRaycastQueryResult_set_hasBlock_1(self, arg0);
};
    Object.defineProperty(PxRaycastQueryResult.prototype, 'hasBlock', { get: PxRaycastQueryResult.prototype.get_hasBlock, set: PxRaycastQueryResult.prototype.set_hasBlock });
  PxRaycastQueryResult.prototype['__destroy__'] = PxRaycastQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRaycastQueryResult___destroy___0(self);
};
// PxRigidBodyFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRigidBodyFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxRigidBodyFlags_PxRigidBodyFlags_1(flags);
  getCache(PxRigidBodyFlags)[this.ptr] = this;
};;
PxRigidBodyFlags.prototype = Object.create(WrapperObject.prototype);
PxRigidBodyFlags.prototype.constructor = PxRigidBodyFlags;
PxRigidBodyFlags.prototype.__class__ = PxRigidBodyFlags;
PxRigidBodyFlags.__cache__ = {};
Module['PxRigidBodyFlags'] = PxRigidBodyFlags;

PxRigidBodyFlags.prototype['isSet'] = PxRigidBodyFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxRigidBodyFlags_isSet_1(self, flag));
};;

PxRigidBodyFlags.prototype['set'] = PxRigidBodyFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxRigidBodyFlags_set_1(self, flag);
};;

PxRigidBodyFlags.prototype['clear'] = PxRigidBodyFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxRigidBodyFlags_clear_1(self, flag);
};;

  PxRigidBodyFlags.prototype['__destroy__'] = PxRigidBodyFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidBodyFlags___destroy___0(self);
};
// PxRigidDynamic
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRigidDynamic() { throw "cannot construct a PxRigidDynamic, no constructor in IDL" }
PxRigidDynamic.prototype = Object.create(PxRigidBody.prototype);
PxRigidDynamic.prototype.constructor = PxRigidDynamic;
PxRigidDynamic.prototype.__class__ = PxRigidDynamic;
PxRigidDynamic.__cache__ = {};
Module['PxRigidDynamic'] = PxRigidDynamic;

PxRigidDynamic.prototype['isSleeping'] = PxRigidDynamic.prototype.isSleeping = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRigidDynamic_isSleeping_0(self));
};;

PxRigidDynamic.prototype['setSleepThreshold'] = PxRigidDynamic.prototype.setSleepThreshold = /** @suppress {undefinedVars, duplicate} @this{Object} */function(threshold) {
  var self = this.ptr;
  if (threshold && typeof threshold === 'object') threshold = threshold.ptr;
  _emscripten_bind_PxRigidDynamic_setSleepThreshold_1(self, threshold);
};;

PxRigidDynamic.prototype['getSleepThreshold'] = PxRigidDynamic.prototype.getSleepThreshold = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getSleepThreshold_0(self);
};;

PxRigidDynamic.prototype['setStabilizationThreshold'] = PxRigidDynamic.prototype.setStabilizationThreshold = /** @suppress {undefinedVars, duplicate} @this{Object} */function(threshold) {
  var self = this.ptr;
  if (threshold && typeof threshold === 'object') threshold = threshold.ptr;
  _emscripten_bind_PxRigidDynamic_setStabilizationThreshold_1(self, threshold);
};;

PxRigidDynamic.prototype['getStabilizationThreshold'] = PxRigidDynamic.prototype.getStabilizationThreshold = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getStabilizationThreshold_0(self);
};;

PxRigidDynamic.prototype['getRigidDynamicLockFlags'] = PxRigidDynamic.prototype.getRigidDynamicLockFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getRigidDynamicLockFlags_0(self), PxRigidDynamicLockFlags);
};;

PxRigidDynamic.prototype['setRigidDynamicLockFlag'] = PxRigidDynamic.prototype.setRigidDynamicLockFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlag_2(self, flag, value);
};;

PxRigidDynamic.prototype['setRigidDynamicLockFlags'] = PxRigidDynamic.prototype.setRigidDynamicLockFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxRigidDynamic_setRigidDynamicLockFlags_1(self, flags);
};;

PxRigidDynamic.prototype['setWakeCounter'] = PxRigidDynamic.prototype.setWakeCounter = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wakeCounterValue) {
  var self = this.ptr;
  if (wakeCounterValue && typeof wakeCounterValue === 'object') wakeCounterValue = wakeCounterValue.ptr;
  _emscripten_bind_PxRigidDynamic_setWakeCounter_1(self, wakeCounterValue);
};;

PxRigidDynamic.prototype['getWakeCounter'] = PxRigidDynamic.prototype.getWakeCounter = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getWakeCounter_0(self);
};;

PxRigidDynamic.prototype['wakeUp'] = PxRigidDynamic.prototype.wakeUp = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidDynamic_wakeUp_0(self);
};;

PxRigidDynamic.prototype['putToSleep'] = PxRigidDynamic.prototype.putToSleep = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidDynamic_putToSleep_0(self);
};;

PxRigidDynamic.prototype['setSolverIterationCounts'] = PxRigidDynamic.prototype.setSolverIterationCounts = /** @suppress {undefinedVars, duplicate} @this{Object} */function(minPositionIters, minVelocityIters) {
  var self = this.ptr;
  if (minPositionIters && typeof minPositionIters === 'object') minPositionIters = minPositionIters.ptr;
  if (minVelocityIters && typeof minVelocityIters === 'object') minVelocityIters = minVelocityIters.ptr;
  if (minVelocityIters === undefined) { _emscripten_bind_PxRigidDynamic_setSolverIterationCounts_1(self, minPositionIters);  return }
  _emscripten_bind_PxRigidDynamic_setSolverIterationCounts_2(self, minPositionIters, minVelocityIters);
};;

PxRigidDynamic.prototype['getContactReportThreshold'] = PxRigidDynamic.prototype.getContactReportThreshold = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getContactReportThreshold_0(self);
};;

PxRigidDynamic.prototype['setContactReportThreshold'] = PxRigidDynamic.prototype.setContactReportThreshold = /** @suppress {undefinedVars, duplicate} @this{Object} */function(threshold) {
  var self = this.ptr;
  if (threshold && typeof threshold === 'object') threshold = threshold.ptr;
  _emscripten_bind_PxRigidDynamic_setContactReportThreshold_1(self, threshold);
};;

PxRigidDynamic.prototype['getType'] = PxRigidDynamic.prototype.getType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getType_0(self);
};;

PxRigidDynamic.prototype['getScene'] = PxRigidDynamic.prototype.getScene = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getScene_0(self), PxScene);
};;

PxRigidDynamic.prototype['setName'] = PxRigidDynamic.prototype.setName = /** @suppress {undefinedVars, duplicate} @this{Object} */function(name) {
  var self = this.ptr;
  ensureCache.prepare();
  if (name && typeof name === 'object') name = name.ptr;
  else name = ensureString(name);
  _emscripten_bind_PxRigidDynamic_setName_1(self, name);
};;

PxRigidDynamic.prototype['getName'] = PxRigidDynamic.prototype.getName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidDynamic_getName_0(self));
};;

PxRigidDynamic.prototype['getWorldBounds'] = PxRigidDynamic.prototype.getWorldBounds = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inflation) {
  var self = this.ptr;
  if (inflation && typeof inflation === 'object') inflation = inflation.ptr;
  if (inflation === undefined) { return wrapPointer(_emscripten_bind_PxRigidDynamic_getWorldBounds_0(self), PxBounds3) }
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getWorldBounds_1(self, inflation), PxBounds3);
};;

PxRigidDynamic.prototype['setActorFlags'] = PxRigidDynamic.prototype.setActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxRigidDynamic_setActorFlags_1(self, flags);
};;

PxRigidDynamic.prototype['getActorFlags'] = PxRigidDynamic.prototype.getActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getActorFlags_0(self), PxActorFlags);
};;

PxRigidDynamic.prototype['setDominanceGroup'] = PxRigidDynamic.prototype.setDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(dominanceGroup) {
  var self = this.ptr;
  if (dominanceGroup && typeof dominanceGroup === 'object') dominanceGroup = dominanceGroup.ptr;
  _emscripten_bind_PxRigidDynamic_setDominanceGroup_1(self, dominanceGroup);
};;

PxRigidDynamic.prototype['getDominanceGroup'] = PxRigidDynamic.prototype.getDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getDominanceGroup_0(self);
};;

PxRigidDynamic.prototype['setOwnerClient'] = PxRigidDynamic.prototype.setOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inClient) {
  var self = this.ptr;
  if (inClient && typeof inClient === 'object') inClient = inClient.ptr;
  _emscripten_bind_PxRigidDynamic_setOwnerClient_1(self, inClient);
};;

PxRigidDynamic.prototype['getOwnerClient'] = PxRigidDynamic.prototype.getOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getOwnerClient_0(self);
};;

PxRigidDynamic.prototype['release'] = PxRigidDynamic.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidDynamic_release_0(self);
};;

PxRigidDynamic.prototype['getConcreteTypeName'] = PxRigidDynamic.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidDynamic_getConcreteTypeName_0(self));
};;

PxRigidDynamic.prototype['getConcreteType'] = PxRigidDynamic.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getConcreteType_0(self);
};;

PxRigidDynamic.prototype['setBaseFlag'] = PxRigidDynamic.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidDynamic_setBaseFlag_2(self, flag, value);
};;

PxRigidDynamic.prototype['setBaseFlags'] = PxRigidDynamic.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRigidDynamic_setBaseFlags_1(self, inFlags);
};;

PxRigidDynamic.prototype['getBaseFlags'] = PxRigidDynamic.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getBaseFlags_0(self), PxBaseFlags);
};;

PxRigidDynamic.prototype['isReleasable'] = PxRigidDynamic.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRigidDynamic_isReleasable_0(self));
};;

PxRigidDynamic.prototype['getGlobalPose'] = PxRigidDynamic.prototype.getGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getGlobalPose_0(self), PxTransform);
};;

PxRigidDynamic.prototype['setGlobalPose'] = PxRigidDynamic.prototype.setGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose, autowake) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidDynamic_setGlobalPose_1(self, pose);  return }
  _emscripten_bind_PxRigidDynamic_setGlobalPose_2(self, pose, autowake);
};;

PxRigidDynamic.prototype['attachShape'] = PxRigidDynamic.prototype.attachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  return !!(_emscripten_bind_PxRigidDynamic_attachShape_1(self, shape));
};;

PxRigidDynamic.prototype['detachShape'] = PxRigidDynamic.prototype.detachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape, wakeOnLostTouch) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  if (wakeOnLostTouch && typeof wakeOnLostTouch === 'object') wakeOnLostTouch = wakeOnLostTouch.ptr;
  if (wakeOnLostTouch === undefined) { _emscripten_bind_PxRigidDynamic_detachShape_1(self, shape);  return }
  _emscripten_bind_PxRigidDynamic_detachShape_2(self, shape, wakeOnLostTouch);
};;

PxRigidDynamic.prototype['getNbShapes'] = PxRigidDynamic.prototype.getNbShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getNbShapes_0(self);
};;

PxRigidDynamic.prototype['getShapes'] = PxRigidDynamic.prototype.getShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function(userBuffer, bufferSize, startIndex) {
  var self = this.ptr;
  ensureCache.prepare();
  if (bufferSize && typeof bufferSize === 'object') bufferSize = bufferSize.ptr;
  if (startIndex && typeof startIndex === 'object') startIndex = startIndex.ptr;
  return _emscripten_bind_PxRigidDynamic_getShapes_3(self, userBuffer, bufferSize, startIndex);
};;

PxRigidDynamic.prototype['setCMassLocalPose'] = PxRigidDynamic.prototype.setCMassLocalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  _emscripten_bind_PxRigidDynamic_setCMassLocalPose_1(self, pose);
};;

PxRigidDynamic.prototype['getCMassLocalPose'] = PxRigidDynamic.prototype.getCMassLocalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getCMassLocalPose_0(self), PxTransform);
};;

PxRigidDynamic.prototype['setMass'] = PxRigidDynamic.prototype.setMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function(mass) {
  var self = this.ptr;
  if (mass && typeof mass === 'object') mass = mass.ptr;
  _emscripten_bind_PxRigidDynamic_setMass_1(self, mass);
};;

PxRigidDynamic.prototype['getMass'] = PxRigidDynamic.prototype.getMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getMass_0(self);
};;

PxRigidDynamic.prototype['getInvMass'] = PxRigidDynamic.prototype.getInvMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getInvMass_0(self);
};;

PxRigidDynamic.prototype['setMassSpaceInertiaTensor'] = PxRigidDynamic.prototype.setMassSpaceInertiaTensor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(m) {
  var self = this.ptr;
  if (m && typeof m === 'object') m = m.ptr;
  _emscripten_bind_PxRigidDynamic_setMassSpaceInertiaTensor_1(self, m);
};;

PxRigidDynamic.prototype['getMassSpaceInertiaTensor'] = PxRigidDynamic.prototype.getMassSpaceInertiaTensor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getMassSpaceInertiaTensor_0(self), PxVec3);
};;

PxRigidDynamic.prototype['getMassSpaceInvInertiaTensor'] = PxRigidDynamic.prototype.getMassSpaceInvInertiaTensor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getMassSpaceInvInertiaTensor_0(self), PxVec3);
};;

PxRigidDynamic.prototype['setLinearDamping'] = PxRigidDynamic.prototype.setLinearDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function(linDamp) {
  var self = this.ptr;
  if (linDamp && typeof linDamp === 'object') linDamp = linDamp.ptr;
  _emscripten_bind_PxRigidDynamic_setLinearDamping_1(self, linDamp);
};;

PxRigidDynamic.prototype['getLinearDamping'] = PxRigidDynamic.prototype.getLinearDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getLinearDamping_0(self);
};;

PxRigidDynamic.prototype['setAngularDamping'] = PxRigidDynamic.prototype.setAngularDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function(angDamp) {
  var self = this.ptr;
  if (angDamp && typeof angDamp === 'object') angDamp = angDamp.ptr;
  _emscripten_bind_PxRigidDynamic_setAngularDamping_1(self, angDamp);
};;

PxRigidDynamic.prototype['getAngularDamping'] = PxRigidDynamic.prototype.getAngularDamping = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getAngularDamping_0(self);
};;

PxRigidDynamic.prototype['getLinearVelocity'] = PxRigidDynamic.prototype.getLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getLinearVelocity_0(self), PxVec3);
};;

PxRigidDynamic.prototype['setLinearVelocity'] = PxRigidDynamic.prototype.setLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(linVel, autowake) {
  var self = this.ptr;
  if (linVel && typeof linVel === 'object') linVel = linVel.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidDynamic_setLinearVelocity_1(self, linVel);  return }
  _emscripten_bind_PxRigidDynamic_setLinearVelocity_2(self, linVel, autowake);
};;

PxRigidDynamic.prototype['getAngularVelocity'] = PxRigidDynamic.prototype.getAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getAngularVelocity_0(self), PxVec3);
};;

PxRigidDynamic.prototype['setAngularVelocity'] = PxRigidDynamic.prototype.setAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(angVel, autowake) {
  var self = this.ptr;
  if (angVel && typeof angVel === 'object') angVel = angVel.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidDynamic_setAngularVelocity_1(self, angVel);  return }
  _emscripten_bind_PxRigidDynamic_setAngularVelocity_2(self, angVel, autowake);
};;

PxRigidDynamic.prototype['getMaxLinearVelocity'] = PxRigidDynamic.prototype.getMaxLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getMaxLinearVelocity_0(self);
};;

PxRigidDynamic.prototype['setMaxLinearVelocity'] = PxRigidDynamic.prototype.setMaxLinearVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxLinVel) {
  var self = this.ptr;
  if (maxLinVel && typeof maxLinVel === 'object') maxLinVel = maxLinVel.ptr;
  _emscripten_bind_PxRigidDynamic_setMaxLinearVelocity_1(self, maxLinVel);
};;

PxRigidDynamic.prototype['getMaxAngularVelocity'] = PxRigidDynamic.prototype.getMaxAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getMaxAngularVelocity_0(self);
};;

PxRigidDynamic.prototype['setMaxAngularVelocity'] = PxRigidDynamic.prototype.setMaxAngularVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxAngVel) {
  var self = this.ptr;
  if (maxAngVel && typeof maxAngVel === 'object') maxAngVel = maxAngVel.ptr;
  _emscripten_bind_PxRigidDynamic_setMaxAngularVelocity_1(self, maxAngVel);
};;

PxRigidDynamic.prototype['addForce'] = PxRigidDynamic.prototype.addForce = /** @suppress {undefinedVars, duplicate} @this{Object} */function(force, mode, autowake) {
  var self = this.ptr;
  if (force && typeof force === 'object') force = force.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (mode === undefined) { _emscripten_bind_PxRigidDynamic_addForce_1(self, force);  return }
  if (autowake === undefined) { _emscripten_bind_PxRigidDynamic_addForce_2(self, force, mode);  return }
  _emscripten_bind_PxRigidDynamic_addForce_3(self, force, mode, autowake);
};;

PxRigidDynamic.prototype['addTorque'] = PxRigidDynamic.prototype.addTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(torque, mode, autowake) {
  var self = this.ptr;
  if (torque && typeof torque === 'object') torque = torque.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (mode === undefined) { _emscripten_bind_PxRigidDynamic_addTorque_1(self, torque);  return }
  if (autowake === undefined) { _emscripten_bind_PxRigidDynamic_addTorque_2(self, torque, mode);  return }
  _emscripten_bind_PxRigidDynamic_addTorque_3(self, torque, mode, autowake);
};;

PxRigidDynamic.prototype['clearForce'] = PxRigidDynamic.prototype.clearForce = /** @suppress {undefinedVars, duplicate} @this{Object} */function(mode) {
  var self = this.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  _emscripten_bind_PxRigidDynamic_clearForce_1(self, mode);
};;

PxRigidDynamic.prototype['clearTorque'] = PxRigidDynamic.prototype.clearTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(mode) {
  var self = this.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  _emscripten_bind_PxRigidDynamic_clearTorque_1(self, mode);
};;

PxRigidDynamic.prototype['setForceAndTorque'] = PxRigidDynamic.prototype.setForceAndTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(force, torque, mode) {
  var self = this.ptr;
  if (force && typeof force === 'object') force = force.ptr;
  if (torque && typeof torque === 'object') torque = torque.ptr;
  if (mode && typeof mode === 'object') mode = mode.ptr;
  if (mode === undefined) { _emscripten_bind_PxRigidDynamic_setForceAndTorque_2(self, force, torque);  return }
  _emscripten_bind_PxRigidDynamic_setForceAndTorque_3(self, force, torque, mode);
};;

PxRigidDynamic.prototype['setRigidBodyFlag'] = PxRigidDynamic.prototype.setRigidBodyFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidDynamic_setRigidBodyFlag_2(self, flag, value);
};;

PxRigidDynamic.prototype['setRigidBodyFlags'] = PxRigidDynamic.prototype.setRigidBodyFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRigidDynamic_setRigidBodyFlags_1(self, inFlags);
};;

PxRigidDynamic.prototype['getRigidBodyFlags'] = PxRigidDynamic.prototype.getRigidBodyFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidDynamic_getRigidBodyFlags_0(self), PxRigidBodyFlags);
};;

PxRigidDynamic.prototype['setMinCCDAdvanceCoefficient'] = PxRigidDynamic.prototype.setMinCCDAdvanceCoefficient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(advanceCoefficient) {
  var self = this.ptr;
  if (advanceCoefficient && typeof advanceCoefficient === 'object') advanceCoefficient = advanceCoefficient.ptr;
  _emscripten_bind_PxRigidDynamic_setMinCCDAdvanceCoefficient_1(self, advanceCoefficient);
};;

PxRigidDynamic.prototype['getMinCCDAdvanceCoefficient'] = PxRigidDynamic.prototype.getMinCCDAdvanceCoefficient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getMinCCDAdvanceCoefficient_0(self);
};;

PxRigidDynamic.prototype['setMaxDepenetrationVelocity'] = PxRigidDynamic.prototype.setMaxDepenetrationVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(biasClamp) {
  var self = this.ptr;
  if (biasClamp && typeof biasClamp === 'object') biasClamp = biasClamp.ptr;
  _emscripten_bind_PxRigidDynamic_setMaxDepenetrationVelocity_1(self, biasClamp);
};;

PxRigidDynamic.prototype['getMaxDepenetrationVelocity'] = PxRigidDynamic.prototype.getMaxDepenetrationVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getMaxDepenetrationVelocity_0(self);
};;

PxRigidDynamic.prototype['setMaxContactImpulse'] = PxRigidDynamic.prototype.setMaxContactImpulse = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxImpulse) {
  var self = this.ptr;
  if (maxImpulse && typeof maxImpulse === 'object') maxImpulse = maxImpulse.ptr;
  _emscripten_bind_PxRigidDynamic_setMaxContactImpulse_1(self, maxImpulse);
};;

PxRigidDynamic.prototype['getMaxContactImpulse'] = PxRigidDynamic.prototype.getMaxContactImpulse = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getMaxContactImpulse_0(self);
};;

PxRigidDynamic.prototype['getInternalIslandNodeIndex'] = PxRigidDynamic.prototype.getInternalIslandNodeIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidDynamic_getInternalIslandNodeIndex_0(self);
};;

// PxRigidDynamicLockFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRigidDynamicLockFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxRigidDynamicLockFlags_PxRigidDynamicLockFlags_1(flags);
  getCache(PxRigidDynamicLockFlags)[this.ptr] = this;
};;
PxRigidDynamicLockFlags.prototype = Object.create(WrapperObject.prototype);
PxRigidDynamicLockFlags.prototype.constructor = PxRigidDynamicLockFlags;
PxRigidDynamicLockFlags.prototype.__class__ = PxRigidDynamicLockFlags;
PxRigidDynamicLockFlags.__cache__ = {};
Module['PxRigidDynamicLockFlags'] = PxRigidDynamicLockFlags;

PxRigidDynamicLockFlags.prototype['isSet'] = PxRigidDynamicLockFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxRigidDynamicLockFlags_isSet_1(self, flag));
};;

PxRigidDynamicLockFlags.prototype['set'] = PxRigidDynamicLockFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxRigidDynamicLockFlags_set_1(self, flag);
};;

PxRigidDynamicLockFlags.prototype['clear'] = PxRigidDynamicLockFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxRigidDynamicLockFlags_clear_1(self, flag);
};;

  PxRigidDynamicLockFlags.prototype['__destroy__'] = PxRigidDynamicLockFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidDynamicLockFlags___destroy___0(self);
};
// PxRigidStatic
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRigidStatic() { throw "cannot construct a PxRigidStatic, no constructor in IDL" }
PxRigidStatic.prototype = Object.create(PxRigidActor.prototype);
PxRigidStatic.prototype.constructor = PxRigidStatic;
PxRigidStatic.prototype.__class__ = PxRigidStatic;
PxRigidStatic.__cache__ = {};
Module['PxRigidStatic'] = PxRigidStatic;

PxRigidStatic.prototype['getType'] = PxRigidStatic.prototype.getType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidStatic_getType_0(self);
};;

PxRigidStatic.prototype['getScene'] = PxRigidStatic.prototype.getScene = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidStatic_getScene_0(self), PxScene);
};;

PxRigidStatic.prototype['setName'] = PxRigidStatic.prototype.setName = /** @suppress {undefinedVars, duplicate} @this{Object} */function(name) {
  var self = this.ptr;
  ensureCache.prepare();
  if (name && typeof name === 'object') name = name.ptr;
  else name = ensureString(name);
  _emscripten_bind_PxRigidStatic_setName_1(self, name);
};;

PxRigidStatic.prototype['getName'] = PxRigidStatic.prototype.getName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidStatic_getName_0(self));
};;

PxRigidStatic.prototype['getWorldBounds'] = PxRigidStatic.prototype.getWorldBounds = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inflation) {
  var self = this.ptr;
  if (inflation && typeof inflation === 'object') inflation = inflation.ptr;
  if (inflation === undefined) { return wrapPointer(_emscripten_bind_PxRigidStatic_getWorldBounds_0(self), PxBounds3) }
  return wrapPointer(_emscripten_bind_PxRigidStatic_getWorldBounds_1(self, inflation), PxBounds3);
};;

PxRigidStatic.prototype['setActorFlags'] = PxRigidStatic.prototype.setActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxRigidStatic_setActorFlags_1(self, flags);
};;

PxRigidStatic.prototype['getActorFlags'] = PxRigidStatic.prototype.getActorFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidStatic_getActorFlags_0(self), PxActorFlags);
};;

PxRigidStatic.prototype['setDominanceGroup'] = PxRigidStatic.prototype.setDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(dominanceGroup) {
  var self = this.ptr;
  if (dominanceGroup && typeof dominanceGroup === 'object') dominanceGroup = dominanceGroup.ptr;
  _emscripten_bind_PxRigidStatic_setDominanceGroup_1(self, dominanceGroup);
};;

PxRigidStatic.prototype['getDominanceGroup'] = PxRigidStatic.prototype.getDominanceGroup = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidStatic_getDominanceGroup_0(self);
};;

PxRigidStatic.prototype['setOwnerClient'] = PxRigidStatic.prototype.setOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inClient) {
  var self = this.ptr;
  if (inClient && typeof inClient === 'object') inClient = inClient.ptr;
  _emscripten_bind_PxRigidStatic_setOwnerClient_1(self, inClient);
};;

PxRigidStatic.prototype['getOwnerClient'] = PxRigidStatic.prototype.getOwnerClient = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidStatic_getOwnerClient_0(self);
};;

PxRigidStatic.prototype['release'] = PxRigidStatic.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRigidStatic_release_0(self);
};;

PxRigidStatic.prototype['getConcreteTypeName'] = PxRigidStatic.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRigidStatic_getConcreteTypeName_0(self));
};;

PxRigidStatic.prototype['getConcreteType'] = PxRigidStatic.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidStatic_getConcreteType_0(self);
};;

PxRigidStatic.prototype['setBaseFlag'] = PxRigidStatic.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRigidStatic_setBaseFlag_2(self, flag, value);
};;

PxRigidStatic.prototype['setBaseFlags'] = PxRigidStatic.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRigidStatic_setBaseFlags_1(self, inFlags);
};;

PxRigidStatic.prototype['getBaseFlags'] = PxRigidStatic.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidStatic_getBaseFlags_0(self), PxBaseFlags);
};;

PxRigidStatic.prototype['isReleasable'] = PxRigidStatic.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRigidStatic_isReleasable_0(self));
};;

PxRigidStatic.prototype['getGlobalPose'] = PxRigidStatic.prototype.getGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRigidStatic_getGlobalPose_0(self), PxTransform);
};;

PxRigidStatic.prototype['setGlobalPose'] = PxRigidStatic.prototype.setGlobalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose, autowake) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRigidStatic_setGlobalPose_1(self, pose);  return }
  _emscripten_bind_PxRigidStatic_setGlobalPose_2(self, pose, autowake);
};;

PxRigidStatic.prototype['attachShape'] = PxRigidStatic.prototype.attachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  return !!(_emscripten_bind_PxRigidStatic_attachShape_1(self, shape));
};;

PxRigidStatic.prototype['detachShape'] = PxRigidStatic.prototype.detachShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(shape, wakeOnLostTouch) {
  var self = this.ptr;
  if (shape && typeof shape === 'object') shape = shape.ptr;
  if (wakeOnLostTouch && typeof wakeOnLostTouch === 'object') wakeOnLostTouch = wakeOnLostTouch.ptr;
  if (wakeOnLostTouch === undefined) { _emscripten_bind_PxRigidStatic_detachShape_1(self, shape);  return }
  _emscripten_bind_PxRigidStatic_detachShape_2(self, shape, wakeOnLostTouch);
};;

PxRigidStatic.prototype['getNbShapes'] = PxRigidStatic.prototype.getNbShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRigidStatic_getNbShapes_0(self);
};;

PxRigidStatic.prototype['getShapes'] = PxRigidStatic.prototype.getShapes = /** @suppress {undefinedVars, duplicate} @this{Object} */function(userBuffer, bufferSize, startIndex) {
  var self = this.ptr;
  ensureCache.prepare();
  if (bufferSize && typeof bufferSize === 'object') bufferSize = bufferSize.ptr;
  if (startIndex && typeof startIndex === 'object') startIndex = startIndex.ptr;
  return _emscripten_bind_PxRigidStatic_getShapes_3(self, userBuffer, bufferSize, startIndex);
};;

// PxScene
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxScene() { throw "cannot construct a PxScene, no constructor in IDL" }
PxScene.prototype = Object.create(WrapperObject.prototype);
PxScene.prototype.constructor = PxScene;
PxScene.prototype.__class__ = PxScene;
PxScene.__cache__ = {};
Module['PxScene'] = PxScene;

PxScene.prototype['addActor'] = PxScene.prototype.addActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(actor, bvhStructure) {
  var self = this.ptr;
  if (actor && typeof actor === 'object') actor = actor.ptr;
  if (bvhStructure && typeof bvhStructure === 'object') bvhStructure = bvhStructure.ptr;
  if (bvhStructure === undefined) { _emscripten_bind_PxScene_addActor_1(self, actor);  return }
  _emscripten_bind_PxScene_addActor_2(self, actor, bvhStructure);
};;

PxScene.prototype['removeActor'] = PxScene.prototype.removeActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(actor, wakeOnLostTouch) {
  var self = this.ptr;
  if (actor && typeof actor === 'object') actor = actor.ptr;
  if (wakeOnLostTouch && typeof wakeOnLostTouch === 'object') wakeOnLostTouch = wakeOnLostTouch.ptr;
  if (wakeOnLostTouch === undefined) { _emscripten_bind_PxScene_removeActor_1(self, actor);  return }
  _emscripten_bind_PxScene_removeActor_2(self, actor, wakeOnLostTouch);
};;

PxScene.prototype['simulate'] = PxScene.prototype.simulate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(elapsedTime, completionTask, scratchMemBlock, scratchMemBlockSize, controlSimulation) {
  var self = this.ptr;
  if (elapsedTime && typeof elapsedTime === 'object') elapsedTime = elapsedTime.ptr;
  if (completionTask && typeof completionTask === 'object') completionTask = completionTask.ptr;
  if (scratchMemBlock && typeof scratchMemBlock === 'object') scratchMemBlock = scratchMemBlock.ptr;
  if (scratchMemBlockSize && typeof scratchMemBlockSize === 'object') scratchMemBlockSize = scratchMemBlockSize.ptr;
  if (controlSimulation && typeof controlSimulation === 'object') controlSimulation = controlSimulation.ptr;
  if (completionTask === undefined) { _emscripten_bind_PxScene_simulate_1(self, elapsedTime);  return }
  if (scratchMemBlock === undefined) { _emscripten_bind_PxScene_simulate_2(self, elapsedTime, completionTask);  return }
  if (scratchMemBlockSize === undefined) { _emscripten_bind_PxScene_simulate_3(self, elapsedTime, completionTask, scratchMemBlock);  return }
  if (controlSimulation === undefined) { _emscripten_bind_PxScene_simulate_4(self, elapsedTime, completionTask, scratchMemBlock, scratchMemBlockSize);  return }
  _emscripten_bind_PxScene_simulate_5(self, elapsedTime, completionTask, scratchMemBlock, scratchMemBlockSize, controlSimulation);
};;

PxScene.prototype['fetchResults'] = PxScene.prototype.fetchResults = /** @suppress {undefinedVars, duplicate} @this{Object} */function(block) {
  var self = this.ptr;
  if (block && typeof block === 'object') block = block.ptr;
  if (block === undefined) { return !!(_emscripten_bind_PxScene_fetchResults_0(self)) }
  return !!(_emscripten_bind_PxScene_fetchResults_1(self, block));
};;

PxScene.prototype['setGravity'] = PxScene.prototype.setGravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(vec) {
  var self = this.ptr;
  if (vec && typeof vec === 'object') vec = vec.ptr;
  _emscripten_bind_PxScene_setGravity_1(self, vec);
};;

PxScene.prototype['getGravity'] = PxScene.prototype.getGravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxScene_getGravity_0(self), PxVec3);
};;

PxScene.prototype['createBatchQuery'] = PxScene.prototype.createBatchQuery = /** @suppress {undefinedVars, duplicate} @this{Object} */function(desc) {
  var self = this.ptr;
  if (desc && typeof desc === 'object') desc = desc.ptr;
  return wrapPointer(_emscripten_bind_PxScene_createBatchQuery_1(self, desc), PxBatchQuery);
};;

// PxSceneDesc
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSceneDesc(scale) {
  if (scale && typeof scale === 'object') scale = scale.ptr;
  this.ptr = _emscripten_bind_PxSceneDesc_PxSceneDesc_1(scale);
  getCache(PxSceneDesc)[this.ptr] = this;
};;
PxSceneDesc.prototype = Object.create(WrapperObject.prototype);
PxSceneDesc.prototype.constructor = PxSceneDesc;
PxSceneDesc.prototype.__class__ = PxSceneDesc;
PxSceneDesc.__cache__ = {};
Module['PxSceneDesc'] = PxSceneDesc;

  PxSceneDesc.prototype['get_gravity'] = PxSceneDesc.prototype.get_gravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSceneDesc_get_gravity_0(self), PxVec3);
};
    PxSceneDesc.prototype['set_gravity'] = PxSceneDesc.prototype.set_gravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSceneDesc_set_gravity_1(self, arg0);
};
    Object.defineProperty(PxSceneDesc.prototype, 'gravity', { get: PxSceneDesc.prototype.get_gravity, set: PxSceneDesc.prototype.set_gravity });
  PxSceneDesc.prototype['get_simulationEventCallback'] = PxSceneDesc.prototype.get_simulationEventCallback = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSceneDesc_get_simulationEventCallback_0(self), PxSimulationEventCallback);
};
    PxSceneDesc.prototype['set_simulationEventCallback'] = PxSceneDesc.prototype.set_simulationEventCallback = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSceneDesc_set_simulationEventCallback_1(self, arg0);
};
    Object.defineProperty(PxSceneDesc.prototype, 'simulationEventCallback', { get: PxSceneDesc.prototype.get_simulationEventCallback, set: PxSceneDesc.prototype.set_simulationEventCallback });
  PxSceneDesc.prototype['get_filterShader'] = PxSceneDesc.prototype.get_filterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSceneDesc_get_filterShader_0(self), PxSimulationFilterShader);
};
    PxSceneDesc.prototype['set_filterShader'] = PxSceneDesc.prototype.set_filterShader = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSceneDesc_set_filterShader_1(self, arg0);
};
    Object.defineProperty(PxSceneDesc.prototype, 'filterShader', { get: PxSceneDesc.prototype.get_filterShader, set: PxSceneDesc.prototype.set_filterShader });
  PxSceneDesc.prototype['get_cpuDispatcher'] = PxSceneDesc.prototype.get_cpuDispatcher = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSceneDesc_get_cpuDispatcher_0(self), PxCpuDispatcher);
};
    PxSceneDesc.prototype['set_cpuDispatcher'] = PxSceneDesc.prototype.set_cpuDispatcher = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSceneDesc_set_cpuDispatcher_1(self, arg0);
};
    Object.defineProperty(PxSceneDesc.prototype, 'cpuDispatcher', { get: PxSceneDesc.prototype.get_cpuDispatcher, set: PxSceneDesc.prototype.set_cpuDispatcher });
  PxSceneDesc.prototype['get_flags'] = PxSceneDesc.prototype.get_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSceneDesc_get_flags_0(self), PxSceneFlags);
};
    PxSceneDesc.prototype['set_flags'] = PxSceneDesc.prototype.set_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSceneDesc_set_flags_1(self, arg0);
};
    Object.defineProperty(PxSceneDesc.prototype, 'flags', { get: PxSceneDesc.prototype.get_flags, set: PxSceneDesc.prototype.set_flags });
  PxSceneDesc.prototype['__destroy__'] = PxSceneDesc.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSceneDesc___destroy___0(self);
};
// PxSceneFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSceneFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxSceneFlags_PxSceneFlags_1(flags);
  getCache(PxSceneFlags)[this.ptr] = this;
};;
PxSceneFlags.prototype = Object.create(WrapperObject.prototype);
PxSceneFlags.prototype.constructor = PxSceneFlags;
PxSceneFlags.prototype.__class__ = PxSceneFlags;
PxSceneFlags.__cache__ = {};
Module['PxSceneFlags'] = PxSceneFlags;

PxSceneFlags.prototype['isSet'] = PxSceneFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxSceneFlags_isSet_1(self, flag));
};;

PxSceneFlags.prototype['set'] = PxSceneFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxSceneFlags_set_1(self, flag);
};;

PxSceneFlags.prototype['clear'] = PxSceneFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxSceneFlags_clear_1(self, flag);
};;

  PxSceneFlags.prototype['__destroy__'] = PxSceneFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSceneFlags___destroy___0(self);
};
// PxShape
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxShape() { throw "cannot construct a PxShape, no constructor in IDL" }
PxShape.prototype = Object.create(PxBase.prototype);
PxShape.prototype.constructor = PxShape;
PxShape.prototype.__class__ = PxShape;
PxShape.__cache__ = {};
Module['PxShape'] = PxShape;

PxShape.prototype['setLocalPose'] = PxShape.prototype.setLocalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(pose) {
  var self = this.ptr;
  if (pose && typeof pose === 'object') pose = pose.ptr;
  _emscripten_bind_PxShape_setLocalPose_1(self, pose);
};;

PxShape.prototype['getLocalPose'] = PxShape.prototype.getLocalPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxShape_getLocalPose_0(self), PxTransform);
};;

PxShape.prototype['setSimulationFilterData'] = PxShape.prototype.setSimulationFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(data) {
  var self = this.ptr;
  if (data && typeof data === 'object') data = data.ptr;
  _emscripten_bind_PxShape_setSimulationFilterData_1(self, data);
};;

PxShape.prototype['getSimulationFilterData'] = PxShape.prototype.getSimulationFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxShape_getSimulationFilterData_0(self), PxFilterData);
};;

PxShape.prototype['setQueryFilterData'] = PxShape.prototype.setQueryFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(data) {
  var self = this.ptr;
  if (data && typeof data === 'object') data = data.ptr;
  _emscripten_bind_PxShape_setQueryFilterData_1(self, data);
};;

PxShape.prototype['getQueryFilterData'] = PxShape.prototype.getQueryFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxShape_getQueryFilterData_0(self), PxFilterData);
};;

PxShape.prototype['release'] = PxShape.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxShape_release_0(self);
};;

PxShape.prototype['getConcreteTypeName'] = PxShape.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxShape_getConcreteTypeName_0(self));
};;

PxShape.prototype['getConcreteType'] = PxShape.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxShape_getConcreteType_0(self);
};;

PxShape.prototype['setBaseFlag'] = PxShape.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxShape_setBaseFlag_2(self, flag, value);
};;

PxShape.prototype['setBaseFlags'] = PxShape.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxShape_setBaseFlags_1(self, inFlags);
};;

PxShape.prototype['getBaseFlags'] = PxShape.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxShape_getBaseFlags_0(self), PxBaseFlags);
};;

PxShape.prototype['isReleasable'] = PxShape.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxShape_isReleasable_0(self));
};;

// PxShapeFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxShapeFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxShapeFlags_PxShapeFlags_1(flags);
  getCache(PxShapeFlags)[this.ptr] = this;
};;
PxShapeFlags.prototype = Object.create(WrapperObject.prototype);
PxShapeFlags.prototype.constructor = PxShapeFlags;
PxShapeFlags.prototype.__class__ = PxShapeFlags;
PxShapeFlags.__cache__ = {};
Module['PxShapeFlags'] = PxShapeFlags;

PxShapeFlags.prototype['isSet'] = PxShapeFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxShapeFlags_isSet_1(self, flag));
};;

PxShapeFlags.prototype['set'] = PxShapeFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxShapeFlags_set_1(self, flag);
};;

PxShapeFlags.prototype['clear'] = PxShapeFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxShapeFlags_clear_1(self, flag);
};;

  PxShapeFlags.prototype['__destroy__'] = PxShapeFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxShapeFlags___destroy___0(self);
};
// JsPxSimulationEventCallback
/** @suppress {undefinedVars, duplicate} @this{Object} */function JsPxSimulationEventCallback() {
  this.ptr = _emscripten_bind_JsPxSimulationEventCallback_JsPxSimulationEventCallback_0();
  getCache(JsPxSimulationEventCallback)[this.ptr] = this;
};;
JsPxSimulationEventCallback.prototype = Object.create(SimplePxSimulationEventCallback.prototype);
JsPxSimulationEventCallback.prototype.constructor = JsPxSimulationEventCallback;
JsPxSimulationEventCallback.prototype.__class__ = JsPxSimulationEventCallback;
JsPxSimulationEventCallback.__cache__ = {};
Module['JsPxSimulationEventCallback'] = JsPxSimulationEventCallback;

JsPxSimulationEventCallback.prototype['cbFun'] = JsPxSimulationEventCallback.prototype.cbFun = /** @suppress {undefinedVars, duplicate} @this{Object} */function(count) {
  var self = this.ptr;
  if (count && typeof count === 'object') count = count.ptr;
  _emscripten_bind_JsPxSimulationEventCallback_cbFun_1(self, count);
};;

  JsPxSimulationEventCallback.prototype['__destroy__'] = JsPxSimulationEventCallback.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_JsPxSimulationEventCallback___destroy___0(self);
};
// PxSimulationFilterShader
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSimulationFilterShader() { throw "cannot construct a PxSimulationFilterShader, no constructor in IDL" }
PxSimulationFilterShader.prototype = Object.create(WrapperObject.prototype);
PxSimulationFilterShader.prototype.constructor = PxSimulationFilterShader;
PxSimulationFilterShader.prototype.__class__ = PxSimulationFilterShader;
PxSimulationFilterShader.__cache__ = {};
Module['PxSimulationFilterShader'] = PxSimulationFilterShader;

  PxSimulationFilterShader.prototype['__destroy__'] = PxSimulationFilterShader.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSimulationFilterShader___destroy___0(self);
};
// PxSweepHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSweepHit() { throw "cannot construct a PxSweepHit, no constructor in IDL" }
PxSweepHit.prototype = Object.create(PxLocationHit.prototype);
PxSweepHit.prototype.constructor = PxSweepHit;
PxSweepHit.prototype.__class__ = PxSweepHit;
PxSweepHit.__cache__ = {};
Module['PxSweepHit'] = PxSweepHit;

  PxSweepHit.prototype['get_actor'] = PxSweepHit.prototype.get_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepHit_get_actor_0(self), PxRigidActor);
};
    PxSweepHit.prototype['set_actor'] = PxSweepHit.prototype.set_actor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_actor_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'actor', { get: PxSweepHit.prototype.get_actor, set: PxSweepHit.prototype.set_actor });
  PxSweepHit.prototype['get_shape'] = PxSweepHit.prototype.get_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepHit_get_shape_0(self), PxShape);
};
    PxSweepHit.prototype['set_shape'] = PxSweepHit.prototype.set_shape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_shape_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'shape', { get: PxSweepHit.prototype.get_shape, set: PxSweepHit.prototype.set_shape });
  PxSweepHit.prototype['get_flags'] = PxSweepHit.prototype.get_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepHit_get_flags_0(self), PxHitFlags);
};
    PxSweepHit.prototype['set_flags'] = PxSweepHit.prototype.set_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_flags_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'flags', { get: PxSweepHit.prototype.get_flags, set: PxSweepHit.prototype.set_flags });
  PxSweepHit.prototype['get_position'] = PxSweepHit.prototype.get_position = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepHit_get_position_0(self), PxVec3);
};
    PxSweepHit.prototype['set_position'] = PxSweepHit.prototype.set_position = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_position_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'position', { get: PxSweepHit.prototype.get_position, set: PxSweepHit.prototype.set_position });
  PxSweepHit.prototype['get_normal'] = PxSweepHit.prototype.get_normal = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepHit_get_normal_0(self), PxVec3);
};
    PxSweepHit.prototype['set_normal'] = PxSweepHit.prototype.set_normal = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_normal_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'normal', { get: PxSweepHit.prototype.get_normal, set: PxSweepHit.prototype.set_normal });
  PxSweepHit.prototype['get_distance'] = PxSweepHit.prototype.get_distance = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxSweepHit_get_distance_0(self);
};
    PxSweepHit.prototype['set_distance'] = PxSweepHit.prototype.set_distance = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_distance_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'distance', { get: PxSweepHit.prototype.get_distance, set: PxSweepHit.prototype.set_distance });
  PxSweepHit.prototype['get_faceIndex'] = PxSweepHit.prototype.get_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxSweepHit_get_faceIndex_0(self);
};
    PxSweepHit.prototype['set_faceIndex'] = PxSweepHit.prototype.set_faceIndex = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepHit_set_faceIndex_1(self, arg0);
};
    Object.defineProperty(PxSweepHit.prototype, 'faceIndex', { get: PxSweepHit.prototype.get_faceIndex, set: PxSweepHit.prototype.set_faceIndex });
  PxSweepHit.prototype['__destroy__'] = PxSweepHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSweepHit___destroy___0(self);
};
// PxSweepQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSweepQueryResult() { throw "cannot construct a PxSweepQueryResult, no constructor in IDL" }
PxSweepQueryResult.prototype = Object.create(WrapperObject.prototype);
PxSweepQueryResult.prototype.constructor = PxSweepQueryResult;
PxSweepQueryResult.prototype.__class__ = PxSweepQueryResult;
PxSweepQueryResult.__cache__ = {};
Module['PxSweepQueryResult'] = PxSweepQueryResult;

PxSweepQueryResult.prototype['getNbAnyHits'] = PxSweepQueryResult.prototype.getNbAnyHits = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxSweepQueryResult_getNbAnyHits_0(self);
};;

PxSweepQueryResult.prototype['getAnyHit'] = PxSweepQueryResult.prototype.getAnyHit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_PxSweepQueryResult_getAnyHit_1(self, index), PxSweepHit);
};;

  PxSweepQueryResult.prototype['get_block'] = PxSweepQueryResult.prototype.get_block = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepQueryResult_get_block_0(self), PxSweepHit);
};
    PxSweepQueryResult.prototype['set_block'] = PxSweepQueryResult.prototype.set_block = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepQueryResult_set_block_1(self, arg0);
};
    Object.defineProperty(PxSweepQueryResult.prototype, 'block', { get: PxSweepQueryResult.prototype.get_block, set: PxSweepQueryResult.prototype.set_block });
  PxSweepQueryResult.prototype['get_touches'] = PxSweepQueryResult.prototype.get_touches = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxSweepQueryResult_get_touches_0(self), PxSweepHit);
};
    PxSweepQueryResult.prototype['set_touches'] = PxSweepQueryResult.prototype.set_touches = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepQueryResult_set_touches_1(self, arg0);
};
    Object.defineProperty(PxSweepQueryResult.prototype, 'touches', { get: PxSweepQueryResult.prototype.get_touches, set: PxSweepQueryResult.prototype.set_touches });
  PxSweepQueryResult.prototype['get_nbTouches'] = PxSweepQueryResult.prototype.get_nbTouches = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxSweepQueryResult_get_nbTouches_0(self);
};
    PxSweepQueryResult.prototype['set_nbTouches'] = PxSweepQueryResult.prototype.set_nbTouches = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepQueryResult_set_nbTouches_1(self, arg0);
};
    Object.defineProperty(PxSweepQueryResult.prototype, 'nbTouches', { get: PxSweepQueryResult.prototype.get_nbTouches, set: PxSweepQueryResult.prototype.set_nbTouches });
  PxSweepQueryResult.prototype['get_userData'] = PxSweepQueryResult.prototype.get_userData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxSweepQueryResult_get_userData_0(self);
};
    PxSweepQueryResult.prototype['set_userData'] = PxSweepQueryResult.prototype.set_userData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepQueryResult_set_userData_1(self, arg0);
};
    Object.defineProperty(PxSweepQueryResult.prototype, 'userData', { get: PxSweepQueryResult.prototype.get_userData, set: PxSweepQueryResult.prototype.set_userData });
  PxSweepQueryResult.prototype['get_queryStatus'] = PxSweepQueryResult.prototype.get_queryStatus = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxSweepQueryResult_get_queryStatus_0(self);
};
    PxSweepQueryResult.prototype['set_queryStatus'] = PxSweepQueryResult.prototype.set_queryStatus = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepQueryResult_set_queryStatus_1(self, arg0);
};
    Object.defineProperty(PxSweepQueryResult.prototype, 'queryStatus', { get: PxSweepQueryResult.prototype.get_queryStatus, set: PxSweepQueryResult.prototype.set_queryStatus });
  PxSweepQueryResult.prototype['get_hasBlock'] = PxSweepQueryResult.prototype.get_hasBlock = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxSweepQueryResult_get_hasBlock_0(self));
};
    PxSweepQueryResult.prototype['set_hasBlock'] = PxSweepQueryResult.prototype.set_hasBlock = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxSweepQueryResult_set_hasBlock_1(self, arg0);
};
    Object.defineProperty(PxSweepQueryResult.prototype, 'hasBlock', { get: PxSweepQueryResult.prototype.get_hasBlock, set: PxSweepQueryResult.prototype.set_hasBlock });
  PxSweepQueryResult.prototype['__destroy__'] = PxSweepQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSweepQueryResult___destroy___0(self);
};
// PxVehicleTopLevelFunctions
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleTopLevelFunctions() { throw "cannot construct a PxVehicleTopLevelFunctions, no constructor in IDL" }
PxVehicleTopLevelFunctions.prototype = Object.create(WrapperObject.prototype);
PxVehicleTopLevelFunctions.prototype.constructor = PxVehicleTopLevelFunctions;
PxVehicleTopLevelFunctions.prototype.__class__ = PxVehicleTopLevelFunctions;
PxVehicleTopLevelFunctions.__cache__ = {};
Module['PxVehicleTopLevelFunctions'] = PxVehicleTopLevelFunctions;

PxVehicleTopLevelFunctions.prototype['InitVehicleSDK'] = PxVehicleTopLevelFunctions.prototype.InitVehicleSDK = /** @suppress {undefinedVars, duplicate} @this{Object} */function(physics) {
  var self = this.ptr;
  if (physics && typeof physics === 'object') physics = physics.ptr;
  return !!(_emscripten_bind_PxVehicleTopLevelFunctions_InitVehicleSDK_1(self, physics));
};;

PxVehicleTopLevelFunctions.prototype['PxVehicleComputeSprungMasses'] = PxVehicleTopLevelFunctions.prototype.PxVehicleComputeSprungMasses = /** @suppress {undefinedVars, duplicate} @this{Object} */function(nbSprungMasses, sprungMassCoordinates, centreOfMass, totalMass, gravityDirection, sprungMasses) {
  var self = this.ptr;
  if (nbSprungMasses && typeof nbSprungMasses === 'object') nbSprungMasses = nbSprungMasses.ptr;
  if (sprungMassCoordinates && typeof sprungMassCoordinates === 'object') sprungMassCoordinates = sprungMassCoordinates.ptr;
  if (centreOfMass && typeof centreOfMass === 'object') centreOfMass = centreOfMass.ptr;
  if (totalMass && typeof totalMass === 'object') totalMass = totalMass.ptr;
  if (gravityDirection && typeof gravityDirection === 'object') gravityDirection = gravityDirection.ptr;
  if (sprungMasses && typeof sprungMasses === 'object') sprungMasses = sprungMasses.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleComputeSprungMasses_6(self, nbSprungMasses, sprungMassCoordinates, centreOfMass, totalMass, gravityDirection, sprungMasses);
};;

PxVehicleTopLevelFunctions.prototype['PxVehicleSuspensionRaycasts'] = PxVehicleTopLevelFunctions.prototype.PxVehicleSuspensionRaycasts = /** @suppress {undefinedVars, duplicate} @this{Object} */function(batchQuery, vehicles, nbSceneQueryResults, sceneQueryResults) {
  var self = this.ptr;
  if (batchQuery && typeof batchQuery === 'object') batchQuery = batchQuery.ptr;
  if (vehicles && typeof vehicles === 'object') vehicles = vehicles.ptr;
  if (nbSceneQueryResults && typeof nbSceneQueryResults === 'object') nbSceneQueryResults = nbSceneQueryResults.ptr;
  if (sceneQueryResults && typeof sceneQueryResults === 'object') sceneQueryResults = sceneQueryResults.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleSuspensionRaycasts_4(self, batchQuery, vehicles, nbSceneQueryResults, sceneQueryResults);
};;

PxVehicleTopLevelFunctions.prototype['PxVehicleUpdates'] = PxVehicleTopLevelFunctions.prototype.PxVehicleUpdates = /** @suppress {undefinedVars, duplicate} @this{Object} */function(timestep, gravity, vehicleDrivableSurfaceToTireFrictionPairs, vehicles, vehicleWheelQueryResults) {
  var self = this.ptr;
  if (timestep && typeof timestep === 'object') timestep = timestep.ptr;
  if (gravity && typeof gravity === 'object') gravity = gravity.ptr;
  if (vehicleDrivableSurfaceToTireFrictionPairs && typeof vehicleDrivableSurfaceToTireFrictionPairs === 'object') vehicleDrivableSurfaceToTireFrictionPairs = vehicleDrivableSurfaceToTireFrictionPairs.ptr;
  if (vehicles && typeof vehicles === 'object') vehicles = vehicles.ptr;
  if (vehicleWheelQueryResults && typeof vehicleWheelQueryResults === 'object') vehicleWheelQueryResults = vehicleWheelQueryResults.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleUpdates_5(self, timestep, gravity, vehicleDrivableSurfaceToTireFrictionPairs, vehicles, vehicleWheelQueryResults);
};;

PxVehicleTopLevelFunctions.prototype['VehicleSetBasisVectors'] = PxVehicleTopLevelFunctions.prototype.VehicleSetBasisVectors = /** @suppress {undefinedVars, duplicate} @this{Object} */function(up, forward) {
  var self = this.ptr;
  if (up && typeof up === 'object') up = up.ptr;
  if (forward && typeof forward === 'object') forward = forward.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetBasisVectors_2(self, up, forward);
};;

PxVehicleTopLevelFunctions.prototype['VehicleSetUpdateMode'] = PxVehicleTopLevelFunctions.prototype.VehicleSetUpdateMode = /** @suppress {undefinedVars, duplicate} @this{Object} */function(vehicleUpdateMode) {
  var self = this.ptr;
  if (vehicleUpdateMode && typeof vehicleUpdateMode === 'object') vehicleUpdateMode = vehicleUpdateMode.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions_VehicleSetUpdateMode_1(self, vehicleUpdateMode);
};;

PxVehicleTopLevelFunctions.prototype['PxVehicleTireData_getFrictionVsSlipGraph'] = PxVehicleTopLevelFunctions.prototype.PxVehicleTireData_getFrictionVsSlipGraph = /** @suppress {undefinedVars, duplicate} @this{Object} */function(tireData, m, n) {
  var self = this.ptr;
  if (tireData && typeof tireData === 'object') tireData = tireData.ptr;
  if (m && typeof m === 'object') m = m.ptr;
  if (n && typeof n === 'object') n = n.ptr;
  return _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_getFrictionVsSlipGraph_3(self, tireData, m, n);
};;

PxVehicleTopLevelFunctions.prototype['PxVehicleTireData_setFrictionVsSlipGraph'] = PxVehicleTopLevelFunctions.prototype.PxVehicleTireData_setFrictionVsSlipGraph = /** @suppress {undefinedVars, duplicate} @this{Object} */function(tireData, m, n, value) {
  var self = this.ptr;
  if (tireData && typeof tireData === 'object') tireData = tireData.ptr;
  if (m && typeof m === 'object') m = m.ptr;
  if (n && typeof n === 'object') n = n.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions_PxVehicleTireData_setFrictionVsSlipGraph_4(self, tireData, m, n, value);
};;

  PxVehicleTopLevelFunctions.prototype['__destroy__'] = PxVehicleTopLevelFunctions.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleTopLevelFunctions___destroy___0(self);
};
// PxVehicleAckermannGeometryData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleAckermannGeometryData() {
  this.ptr = _emscripten_bind_PxVehicleAckermannGeometryData_PxVehicleAckermannGeometryData_0();
  getCache(PxVehicleAckermannGeometryData)[this.ptr] = this;
};;
PxVehicleAckermannGeometryData.prototype = Object.create(WrapperObject.prototype);
PxVehicleAckermannGeometryData.prototype.constructor = PxVehicleAckermannGeometryData;
PxVehicleAckermannGeometryData.prototype.__class__ = PxVehicleAckermannGeometryData;
PxVehicleAckermannGeometryData.__cache__ = {};
Module['PxVehicleAckermannGeometryData'] = PxVehicleAckermannGeometryData;

  PxVehicleAckermannGeometryData.prototype['get_mAccuracy'] = PxVehicleAckermannGeometryData.prototype.get_mAccuracy = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAckermannGeometryData_get_mAccuracy_0(self);
};
    PxVehicleAckermannGeometryData.prototype['set_mAccuracy'] = PxVehicleAckermannGeometryData.prototype.set_mAccuracy = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAckermannGeometryData_set_mAccuracy_1(self, arg0);
};
    Object.defineProperty(PxVehicleAckermannGeometryData.prototype, 'mAccuracy', { get: PxVehicleAckermannGeometryData.prototype.get_mAccuracy, set: PxVehicleAckermannGeometryData.prototype.set_mAccuracy });
  PxVehicleAckermannGeometryData.prototype['get_mFrontWidth'] = PxVehicleAckermannGeometryData.prototype.get_mFrontWidth = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAckermannGeometryData_get_mFrontWidth_0(self);
};
    PxVehicleAckermannGeometryData.prototype['set_mFrontWidth'] = PxVehicleAckermannGeometryData.prototype.set_mFrontWidth = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAckermannGeometryData_set_mFrontWidth_1(self, arg0);
};
    Object.defineProperty(PxVehicleAckermannGeometryData.prototype, 'mFrontWidth', { get: PxVehicleAckermannGeometryData.prototype.get_mFrontWidth, set: PxVehicleAckermannGeometryData.prototype.set_mFrontWidth });
  PxVehicleAckermannGeometryData.prototype['get_mRearWidth'] = PxVehicleAckermannGeometryData.prototype.get_mRearWidth = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAckermannGeometryData_get_mRearWidth_0(self);
};
    PxVehicleAckermannGeometryData.prototype['set_mRearWidth'] = PxVehicleAckermannGeometryData.prototype.set_mRearWidth = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAckermannGeometryData_set_mRearWidth_1(self, arg0);
};
    Object.defineProperty(PxVehicleAckermannGeometryData.prototype, 'mRearWidth', { get: PxVehicleAckermannGeometryData.prototype.get_mRearWidth, set: PxVehicleAckermannGeometryData.prototype.set_mRearWidth });
  PxVehicleAckermannGeometryData.prototype['get_mAxleSeparation'] = PxVehicleAckermannGeometryData.prototype.get_mAxleSeparation = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAckermannGeometryData_get_mAxleSeparation_0(self);
};
    PxVehicleAckermannGeometryData.prototype['set_mAxleSeparation'] = PxVehicleAckermannGeometryData.prototype.set_mAxleSeparation = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAckermannGeometryData_set_mAxleSeparation_1(self, arg0);
};
    Object.defineProperty(PxVehicleAckermannGeometryData.prototype, 'mAxleSeparation', { get: PxVehicleAckermannGeometryData.prototype.get_mAxleSeparation, set: PxVehicleAckermannGeometryData.prototype.set_mAxleSeparation });
  PxVehicleAckermannGeometryData.prototype['__destroy__'] = PxVehicleAckermannGeometryData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleAckermannGeometryData___destroy___0(self);
};
// PxVehicleAntiRollBarData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleAntiRollBarData() {
  this.ptr = _emscripten_bind_PxVehicleAntiRollBarData_PxVehicleAntiRollBarData_0();
  getCache(PxVehicleAntiRollBarData)[this.ptr] = this;
};;
PxVehicleAntiRollBarData.prototype = Object.create(WrapperObject.prototype);
PxVehicleAntiRollBarData.prototype.constructor = PxVehicleAntiRollBarData;
PxVehicleAntiRollBarData.prototype.__class__ = PxVehicleAntiRollBarData;
PxVehicleAntiRollBarData.__cache__ = {};
Module['PxVehicleAntiRollBarData'] = PxVehicleAntiRollBarData;

  PxVehicleAntiRollBarData.prototype['get_mWheel0'] = PxVehicleAntiRollBarData.prototype.get_mWheel0 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAntiRollBarData_get_mWheel0_0(self);
};
    PxVehicleAntiRollBarData.prototype['set_mWheel0'] = PxVehicleAntiRollBarData.prototype.set_mWheel0 = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAntiRollBarData_set_mWheel0_1(self, arg0);
};
    Object.defineProperty(PxVehicleAntiRollBarData.prototype, 'mWheel0', { get: PxVehicleAntiRollBarData.prototype.get_mWheel0, set: PxVehicleAntiRollBarData.prototype.set_mWheel0 });
  PxVehicleAntiRollBarData.prototype['get_mWheel1'] = PxVehicleAntiRollBarData.prototype.get_mWheel1 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAntiRollBarData_get_mWheel1_0(self);
};
    PxVehicleAntiRollBarData.prototype['set_mWheel1'] = PxVehicleAntiRollBarData.prototype.set_mWheel1 = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAntiRollBarData_set_mWheel1_1(self, arg0);
};
    Object.defineProperty(PxVehicleAntiRollBarData.prototype, 'mWheel1', { get: PxVehicleAntiRollBarData.prototype.get_mWheel1, set: PxVehicleAntiRollBarData.prototype.set_mWheel1 });
  PxVehicleAntiRollBarData.prototype['get_mStiffness'] = PxVehicleAntiRollBarData.prototype.get_mStiffness = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAntiRollBarData_get_mStiffness_0(self);
};
    PxVehicleAntiRollBarData.prototype['set_mStiffness'] = PxVehicleAntiRollBarData.prototype.set_mStiffness = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleAntiRollBarData_set_mStiffness_1(self, arg0);
};
    Object.defineProperty(PxVehicleAntiRollBarData.prototype, 'mStiffness', { get: PxVehicleAntiRollBarData.prototype.get_mStiffness, set: PxVehicleAntiRollBarData.prototype.set_mStiffness });
  PxVehicleAntiRollBarData.prototype['__destroy__'] = PxVehicleAntiRollBarData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleAntiRollBarData___destroy___0(self);
};
// PxVehicleAutoBoxData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleAutoBoxData() {
  this.ptr = _emscripten_bind_PxVehicleAutoBoxData_PxVehicleAutoBoxData_0();
  getCache(PxVehicleAutoBoxData)[this.ptr] = this;
};;
PxVehicleAutoBoxData.prototype = Object.create(WrapperObject.prototype);
PxVehicleAutoBoxData.prototype.constructor = PxVehicleAutoBoxData;
PxVehicleAutoBoxData.prototype.__class__ = PxVehicleAutoBoxData;
PxVehicleAutoBoxData.__cache__ = {};
Module['PxVehicleAutoBoxData'] = PxVehicleAutoBoxData;

PxVehicleAutoBoxData.prototype['setLatency'] = PxVehicleAutoBoxData.prototype.setLatency = /** @suppress {undefinedVars, duplicate} @this{Object} */function(latency) {
  var self = this.ptr;
  if (latency && typeof latency === 'object') latency = latency.ptr;
  _emscripten_bind_PxVehicleAutoBoxData_setLatency_1(self, latency);
};;

PxVehicleAutoBoxData.prototype['getLatency'] = PxVehicleAutoBoxData.prototype.getLatency = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleAutoBoxData_getLatency_0(self);
};;

PxVehicleAutoBoxData.prototype['getUpRatios'] = PxVehicleAutoBoxData.prototype.getUpRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(a) {
  var self = this.ptr;
  if (a && typeof a === 'object') a = a.ptr;
  return _emscripten_bind_PxVehicleAutoBoxData_getUpRatios_1(self, a);
};;

PxVehicleAutoBoxData.prototype['setUpRatios'] = PxVehicleAutoBoxData.prototype.setUpRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(a, ratio) {
  var self = this.ptr;
  if (a && typeof a === 'object') a = a.ptr;
  if (ratio && typeof ratio === 'object') ratio = ratio.ptr;
  _emscripten_bind_PxVehicleAutoBoxData_setUpRatios_2(self, a, ratio);
};;

PxVehicleAutoBoxData.prototype['getDownRatios'] = PxVehicleAutoBoxData.prototype.getDownRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(a) {
  var self = this.ptr;
  if (a && typeof a === 'object') a = a.ptr;
  return _emscripten_bind_PxVehicleAutoBoxData_getDownRatios_1(self, a);
};;

PxVehicleAutoBoxData.prototype['setDownRatios'] = PxVehicleAutoBoxData.prototype.setDownRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(a, ratio) {
  var self = this.ptr;
  if (a && typeof a === 'object') a = a.ptr;
  if (ratio && typeof ratio === 'object') ratio = ratio.ptr;
  _emscripten_bind_PxVehicleAutoBoxData_setDownRatios_2(self, a, ratio);
};;

  PxVehicleAutoBoxData.prototype['get_mUpRatios'] = PxVehicleAutoBoxData.prototype.get_mUpRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  return _emscripten_bind_PxVehicleAutoBoxData_get_mUpRatios_1(self, arg0);
};
    PxVehicleAutoBoxData.prototype['set_mUpRatios'] = PxVehicleAutoBoxData.prototype.set_mUpRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0, arg1) {
  var self = this.ptr;
  ensureCache.prepare();
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  if (arg1 && typeof arg1 === 'object') arg1 = arg1.ptr;
  _emscripten_bind_PxVehicleAutoBoxData_set_mUpRatios_2(self, arg0, arg1);
};
    Object.defineProperty(PxVehicleAutoBoxData.prototype, 'mUpRatios', { get: PxVehicleAutoBoxData.prototype.get_mUpRatios, set: PxVehicleAutoBoxData.prototype.set_mUpRatios });
  PxVehicleAutoBoxData.prototype['get_mDownRatios'] = PxVehicleAutoBoxData.prototype.get_mDownRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  return _emscripten_bind_PxVehicleAutoBoxData_get_mDownRatios_1(self, arg0);
};
    PxVehicleAutoBoxData.prototype['set_mDownRatios'] = PxVehicleAutoBoxData.prototype.set_mDownRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0, arg1) {
  var self = this.ptr;
  ensureCache.prepare();
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  if (arg1 && typeof arg1 === 'object') arg1 = arg1.ptr;
  _emscripten_bind_PxVehicleAutoBoxData_set_mDownRatios_2(self, arg0, arg1);
};
    Object.defineProperty(PxVehicleAutoBoxData.prototype, 'mDownRatios', { get: PxVehicleAutoBoxData.prototype.get_mDownRatios, set: PxVehicleAutoBoxData.prototype.set_mDownRatios });
  PxVehicleAutoBoxData.prototype['__destroy__'] = PxVehicleAutoBoxData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleAutoBoxData___destroy___0(self);
};
// PxVehicleChassisData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleChassisData() {
  this.ptr = _emscripten_bind_PxVehicleChassisData_PxVehicleChassisData_0();
  getCache(PxVehicleChassisData)[this.ptr] = this;
};;
PxVehicleChassisData.prototype = Object.create(WrapperObject.prototype);
PxVehicleChassisData.prototype.constructor = PxVehicleChassisData;
PxVehicleChassisData.prototype.__class__ = PxVehicleChassisData;
PxVehicleChassisData.__cache__ = {};
Module['PxVehicleChassisData'] = PxVehicleChassisData;

  PxVehicleChassisData.prototype['get_mMOI'] = PxVehicleChassisData.prototype.get_mMOI = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleChassisData_get_mMOI_0(self), PxVec3);
};
    PxVehicleChassisData.prototype['set_mMOI'] = PxVehicleChassisData.prototype.set_mMOI = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleChassisData_set_mMOI_1(self, arg0);
};
    Object.defineProperty(PxVehicleChassisData.prototype, 'mMOI', { get: PxVehicleChassisData.prototype.get_mMOI, set: PxVehicleChassisData.prototype.set_mMOI });
  PxVehicleChassisData.prototype['get_mMass'] = PxVehicleChassisData.prototype.get_mMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleChassisData_get_mMass_0(self);
};
    PxVehicleChassisData.prototype['set_mMass'] = PxVehicleChassisData.prototype.set_mMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleChassisData_set_mMass_1(self, arg0);
};
    Object.defineProperty(PxVehicleChassisData.prototype, 'mMass', { get: PxVehicleChassisData.prototype.get_mMass, set: PxVehicleChassisData.prototype.set_mMass });
  PxVehicleChassisData.prototype['get_mCMOffset'] = PxVehicleChassisData.prototype.get_mCMOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleChassisData_get_mCMOffset_0(self), PxVec3);
};
    PxVehicleChassisData.prototype['set_mCMOffset'] = PxVehicleChassisData.prototype.set_mCMOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleChassisData_set_mCMOffset_1(self, arg0);
};
    Object.defineProperty(PxVehicleChassisData.prototype, 'mCMOffset', { get: PxVehicleChassisData.prototype.get_mCMOffset, set: PxVehicleChassisData.prototype.set_mCMOffset });
  PxVehicleChassisData.prototype['__destroy__'] = PxVehicleChassisData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleChassisData___destroy___0(self);
};
// PxVehicleClutchData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleClutchData() {
  this.ptr = _emscripten_bind_PxVehicleClutchData_PxVehicleClutchData_0();
  getCache(PxVehicleClutchData)[this.ptr] = this;
};;
PxVehicleClutchData.prototype = Object.create(WrapperObject.prototype);
PxVehicleClutchData.prototype.constructor = PxVehicleClutchData;
PxVehicleClutchData.prototype.__class__ = PxVehicleClutchData;
PxVehicleClutchData.__cache__ = {};
Module['PxVehicleClutchData'] = PxVehicleClutchData;

  PxVehicleClutchData.prototype['get_mStrength'] = PxVehicleClutchData.prototype.get_mStrength = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleClutchData_get_mStrength_0(self);
};
    PxVehicleClutchData.prototype['set_mStrength'] = PxVehicleClutchData.prototype.set_mStrength = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleClutchData_set_mStrength_1(self, arg0);
};
    Object.defineProperty(PxVehicleClutchData.prototype, 'mStrength', { get: PxVehicleClutchData.prototype.get_mStrength, set: PxVehicleClutchData.prototype.set_mStrength });
  PxVehicleClutchData.prototype['get_mAccuracyMode'] = PxVehicleClutchData.prototype.get_mAccuracyMode = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleClutchData_get_mAccuracyMode_0(self);
};
    PxVehicleClutchData.prototype['set_mAccuracyMode'] = PxVehicleClutchData.prototype.set_mAccuracyMode = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleClutchData_set_mAccuracyMode_1(self, arg0);
};
    Object.defineProperty(PxVehicleClutchData.prototype, 'mAccuracyMode', { get: PxVehicleClutchData.prototype.get_mAccuracyMode, set: PxVehicleClutchData.prototype.set_mAccuracyMode });
  PxVehicleClutchData.prototype['get_mEstimateIterations'] = PxVehicleClutchData.prototype.get_mEstimateIterations = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleClutchData_get_mEstimateIterations_0(self);
};
    PxVehicleClutchData.prototype['set_mEstimateIterations'] = PxVehicleClutchData.prototype.set_mEstimateIterations = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleClutchData_set_mEstimateIterations_1(self, arg0);
};
    Object.defineProperty(PxVehicleClutchData.prototype, 'mEstimateIterations', { get: PxVehicleClutchData.prototype.get_mEstimateIterations, set: PxVehicleClutchData.prototype.set_mEstimateIterations });
  PxVehicleClutchData.prototype['__destroy__'] = PxVehicleClutchData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleClutchData___destroy___0(self);
};
// PxVehicleDifferential4WData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDifferential4WData() {
  this.ptr = _emscripten_bind_PxVehicleDifferential4WData_PxVehicleDifferential4WData_0();
  getCache(PxVehicleDifferential4WData)[this.ptr] = this;
};;
PxVehicleDifferential4WData.prototype = Object.create(WrapperObject.prototype);
PxVehicleDifferential4WData.prototype.constructor = PxVehicleDifferential4WData;
PxVehicleDifferential4WData.prototype.__class__ = PxVehicleDifferential4WData;
PxVehicleDifferential4WData.__cache__ = {};
Module['PxVehicleDifferential4WData'] = PxVehicleDifferential4WData;

  PxVehicleDifferential4WData.prototype['get_mFrontRearSplit'] = PxVehicleDifferential4WData.prototype.get_mFrontRearSplit = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mFrontRearSplit_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mFrontRearSplit'] = PxVehicleDifferential4WData.prototype.set_mFrontRearSplit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mFrontRearSplit_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mFrontRearSplit', { get: PxVehicleDifferential4WData.prototype.get_mFrontRearSplit, set: PxVehicleDifferential4WData.prototype.set_mFrontRearSplit });
  PxVehicleDifferential4WData.prototype['get_mFrontLeftRightSplit'] = PxVehicleDifferential4WData.prototype.get_mFrontLeftRightSplit = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mFrontLeftRightSplit_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mFrontLeftRightSplit'] = PxVehicleDifferential4WData.prototype.set_mFrontLeftRightSplit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mFrontLeftRightSplit_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mFrontLeftRightSplit', { get: PxVehicleDifferential4WData.prototype.get_mFrontLeftRightSplit, set: PxVehicleDifferential4WData.prototype.set_mFrontLeftRightSplit });
  PxVehicleDifferential4WData.prototype['get_mRearLeftRightSplit'] = PxVehicleDifferential4WData.prototype.get_mRearLeftRightSplit = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mRearLeftRightSplit_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mRearLeftRightSplit'] = PxVehicleDifferential4WData.prototype.set_mRearLeftRightSplit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mRearLeftRightSplit_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mRearLeftRightSplit', { get: PxVehicleDifferential4WData.prototype.get_mRearLeftRightSplit, set: PxVehicleDifferential4WData.prototype.set_mRearLeftRightSplit });
  PxVehicleDifferential4WData.prototype['get_mCentreBias'] = PxVehicleDifferential4WData.prototype.get_mCentreBias = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mCentreBias_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mCentreBias'] = PxVehicleDifferential4WData.prototype.set_mCentreBias = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mCentreBias_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mCentreBias', { get: PxVehicleDifferential4WData.prototype.get_mCentreBias, set: PxVehicleDifferential4WData.prototype.set_mCentreBias });
  PxVehicleDifferential4WData.prototype['get_mFrontBias'] = PxVehicleDifferential4WData.prototype.get_mFrontBias = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mFrontBias_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mFrontBias'] = PxVehicleDifferential4WData.prototype.set_mFrontBias = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mFrontBias_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mFrontBias', { get: PxVehicleDifferential4WData.prototype.get_mFrontBias, set: PxVehicleDifferential4WData.prototype.set_mFrontBias });
  PxVehicleDifferential4WData.prototype['get_mRearBias'] = PxVehicleDifferential4WData.prototype.get_mRearBias = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mRearBias_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mRearBias'] = PxVehicleDifferential4WData.prototype.set_mRearBias = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mRearBias_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mRearBias', { get: PxVehicleDifferential4WData.prototype.get_mRearBias, set: PxVehicleDifferential4WData.prototype.set_mRearBias });
  PxVehicleDifferential4WData.prototype['get_mType'] = PxVehicleDifferential4WData.prototype.get_mType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDifferential4WData_get_mType_0(self);
};
    PxVehicleDifferential4WData.prototype['set_mType'] = PxVehicleDifferential4WData.prototype.set_mType = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDifferential4WData_set_mType_1(self, arg0);
};
    Object.defineProperty(PxVehicleDifferential4WData.prototype, 'mType', { get: PxVehicleDifferential4WData.prototype.get_mType, set: PxVehicleDifferential4WData.prototype.set_mType });
  PxVehicleDifferential4WData.prototype['__destroy__'] = PxVehicleDifferential4WData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDifferential4WData___destroy___0(self);
};
// PxVehicleDrivableSurfaceToTireFrictionPairs
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDrivableSurfaceToTireFrictionPairs() { throw "cannot construct a PxVehicleDrivableSurfaceToTireFrictionPairs, no constructor in IDL" }
PxVehicleDrivableSurfaceToTireFrictionPairs.prototype = Object.create(WrapperObject.prototype);
PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.constructor = PxVehicleDrivableSurfaceToTireFrictionPairs;
PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.__class__ = PxVehicleDrivableSurfaceToTireFrictionPairs;
PxVehicleDrivableSurfaceToTireFrictionPairs.__cache__ = {};
Module['PxVehicleDrivableSurfaceToTireFrictionPairs'] = PxVehicleDrivableSurfaceToTireFrictionPairs;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['allocate'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.allocate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(maxNbTireTypes, maxNbSurfaceTypes) {
  var self = this.ptr;
  if (maxNbTireTypes && typeof maxNbTireTypes === 'object') maxNbTireTypes = maxNbTireTypes.ptr;
  if (maxNbSurfaceTypes && typeof maxNbSurfaceTypes === 'object') maxNbSurfaceTypes = maxNbSurfaceTypes.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_allocate_2(self, maxNbTireTypes, maxNbSurfaceTypes), PxVehicleDrivableSurfaceToTireFrictionPairs);
};;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['setup'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.setup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(nbTireTypes, nbSurfaceTypes, drivableSurfaceMaterials, drivableSurfaceTypes) {
  var self = this.ptr;
  if (nbTireTypes && typeof nbTireTypes === 'object') nbTireTypes = nbTireTypes.ptr;
  if (nbSurfaceTypes && typeof nbSurfaceTypes === 'object') nbSurfaceTypes = nbSurfaceTypes.ptr;
  if (drivableSurfaceMaterials && typeof drivableSurfaceMaterials === 'object') drivableSurfaceMaterials = drivableSurfaceMaterials.ptr;
  if (drivableSurfaceTypes && typeof drivableSurfaceTypes === 'object') drivableSurfaceTypes = drivableSurfaceTypes.ptr;
  _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setup_4(self, nbTireTypes, nbSurfaceTypes, drivableSurfaceMaterials, drivableSurfaceTypes);
};;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['release'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_release_0(self);
};;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['setTypePairFriction'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.setTypePairFriction = /** @suppress {undefinedVars, duplicate} @this{Object} */function(surfaceType, tireType, value) {
  var self = this.ptr;
  if (surfaceType && typeof surfaceType === 'object') surfaceType = surfaceType.ptr;
  if (tireType && typeof tireType === 'object') tireType = tireType.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_setTypePairFriction_3(self, surfaceType, tireType, value);
};;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['getTypePairFriction'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.getTypePairFriction = /** @suppress {undefinedVars, duplicate} @this{Object} */function(surfaceType, tireType) {
  var self = this.ptr;
  if (surfaceType && typeof surfaceType === 'object') surfaceType = surfaceType.ptr;
  if (tireType && typeof tireType === 'object') tireType = tireType.ptr;
  return _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getTypePairFriction_2(self, surfaceType, tireType);
};;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['getMaxNbSurfaceTypes'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.getMaxNbSurfaceTypes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbSurfaceTypes_0(self);
};;

PxVehicleDrivableSurfaceToTireFrictionPairs.prototype['getMaxNbTireTypes'] = PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.getMaxNbTireTypes = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrivableSurfaceToTireFrictionPairs_getMaxNbTireTypes_0(self);
};;

// PxVehicleDrivableSurfaceType
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDrivableSurfaceType() {
  this.ptr = _emscripten_bind_PxVehicleDrivableSurfaceType_PxVehicleDrivableSurfaceType_0();
  getCache(PxVehicleDrivableSurfaceType)[this.ptr] = this;
};;
PxVehicleDrivableSurfaceType.prototype = Object.create(WrapperObject.prototype);
PxVehicleDrivableSurfaceType.prototype.constructor = PxVehicleDrivableSurfaceType;
PxVehicleDrivableSurfaceType.prototype.__class__ = PxVehicleDrivableSurfaceType;
PxVehicleDrivableSurfaceType.__cache__ = {};
Module['PxVehicleDrivableSurfaceType'] = PxVehicleDrivableSurfaceType;

  PxVehicleDrivableSurfaceType.prototype['get_mType'] = PxVehicleDrivableSurfaceType.prototype.get_mType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrivableSurfaceType_get_mType_0(self);
};
    PxVehicleDrivableSurfaceType.prototype['set_mType'] = PxVehicleDrivableSurfaceType.prototype.set_mType = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrivableSurfaceType_set_mType_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrivableSurfaceType.prototype, 'mType', { get: PxVehicleDrivableSurfaceType.prototype.get_mType, set: PxVehicleDrivableSurfaceType.prototype.set_mType });
  PxVehicleDrivableSurfaceType.prototype['__destroy__'] = PxVehicleDrivableSurfaceType.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDrivableSurfaceType___destroy___0(self);
};
// PxVehicleDrive4W
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDrive4W() { throw "cannot construct a PxVehicleDrive4W, no constructor in IDL" }
PxVehicleDrive4W.prototype = Object.create(PxVehicleDrive.prototype);
PxVehicleDrive4W.prototype.constructor = PxVehicleDrive4W;
PxVehicleDrive4W.prototype.__class__ = PxVehicleDrive4W;
PxVehicleDrive4W.__cache__ = {};
Module['PxVehicleDrive4W'] = PxVehicleDrive4W;

PxVehicleDrive4W.prototype['allocate'] = PxVehicleDrive4W.prototype.allocate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(nbWheels) {
  var self = this.ptr;
  if (nbWheels && typeof nbWheels === 'object') nbWheels = nbWheels.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_allocate_1(self, nbWheels), PxVehicleDrive4W);
};;

PxVehicleDrive4W.prototype['free'] = PxVehicleDrive4W.prototype.free = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDrive4W_free_0(self);
};;

PxVehicleDrive4W.prototype['setup'] = PxVehicleDrive4W.prototype.setup = /** @suppress {undefinedVars, duplicate} @this{Object} */function(physics, vehActor, wheelsData, driveData, nbNonDrivenWheels) {
  var self = this.ptr;
  if (physics && typeof physics === 'object') physics = physics.ptr;
  if (vehActor && typeof vehActor === 'object') vehActor = vehActor.ptr;
  if (wheelsData && typeof wheelsData === 'object') wheelsData = wheelsData.ptr;
  if (driveData && typeof driveData === 'object') driveData = driveData.ptr;
  if (nbNonDrivenWheels && typeof nbNonDrivenWheels === 'object') nbNonDrivenWheels = nbNonDrivenWheels.ptr;
  _emscripten_bind_PxVehicleDrive4W_setup_5(self, physics, vehActor, wheelsData, driveData, nbNonDrivenWheels);
};;

PxVehicleDrive4W.prototype['setToRestState'] = PxVehicleDrive4W.prototype.setToRestState = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDrive4W_setToRestState_0(self);
};;

PxVehicleDrive4W.prototype['release'] = PxVehicleDrive4W.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDrive4W_release_0(self);
};;

PxVehicleDrive4W.prototype['getConcreteTypeName'] = PxVehicleDrive4W.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxVehicleDrive4W_getConcreteTypeName_0(self));
};;

PxVehicleDrive4W.prototype['getConcreteType'] = PxVehicleDrive4W.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive4W_getConcreteType_0(self);
};;

PxVehicleDrive4W.prototype['setBaseFlag'] = PxVehicleDrive4W.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxVehicleDrive4W_setBaseFlag_2(self, flag, value);
};;

PxVehicleDrive4W.prototype['setBaseFlags'] = PxVehicleDrive4W.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxVehicleDrive4W_setBaseFlags_1(self, inFlags);
};;

PxVehicleDrive4W.prototype['getBaseFlags'] = PxVehicleDrive4W.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_getBaseFlags_0(self), PxBaseFlags);
};;

PxVehicleDrive4W.prototype['isReleasable'] = PxVehicleDrive4W.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDrive4W_isReleasable_0(self));
};;

PxVehicleDrive4W.prototype['getVehicleType'] = PxVehicleDrive4W.prototype.getVehicleType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive4W_getVehicleType_0(self);
};;

PxVehicleDrive4W.prototype['getRigidDynamicActor'] = PxVehicleDrive4W.prototype.getRigidDynamicActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_getRigidDynamicActor_0(self), PxRigidDynamic);
};;

PxVehicleDrive4W.prototype['computeForwardSpeed'] = PxVehicleDrive4W.prototype.computeForwardSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive4W_computeForwardSpeed_0(self);
};;

PxVehicleDrive4W.prototype['computeSidewaysSpeed'] = PxVehicleDrive4W.prototype.computeSidewaysSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive4W_computeSidewaysSpeed_0(self);
};;

PxVehicleDrive4W.prototype['getNbNonDrivenWheels'] = PxVehicleDrive4W.prototype.getNbNonDrivenWheels = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDrive4W_getNbNonDrivenWheels_0(self);
};;

  PxVehicleDrive4W.prototype['get_mDriveSimData'] = PxVehicleDrive4W.prototype.get_mDriveSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_get_mDriveSimData_0(self), PxVehicleDriveSimData4W);
};
    PxVehicleDrive4W.prototype['set_mDriveSimData'] = PxVehicleDrive4W.prototype.set_mDriveSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive4W_set_mDriveSimData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive4W.prototype, 'mDriveSimData', { get: PxVehicleDrive4W.prototype.get_mDriveSimData, set: PxVehicleDrive4W.prototype.set_mDriveSimData });
  PxVehicleDrive4W.prototype['get_mDriveDynData'] = PxVehicleDrive4W.prototype.get_mDriveDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_get_mDriveDynData_0(self), PxVehicleDriveDynData);
};
    PxVehicleDrive4W.prototype['set_mDriveDynData'] = PxVehicleDrive4W.prototype.set_mDriveDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive4W_set_mDriveDynData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive4W.prototype, 'mDriveDynData', { get: PxVehicleDrive4W.prototype.get_mDriveDynData, set: PxVehicleDrive4W.prototype.set_mDriveDynData });
  PxVehicleDrive4W.prototype['get_mWheelsSimData'] = PxVehicleDrive4W.prototype.get_mWheelsSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_get_mWheelsSimData_0(self), PxVehicleWheelsSimData);
};
    PxVehicleDrive4W.prototype['set_mWheelsSimData'] = PxVehicleDrive4W.prototype.set_mWheelsSimData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive4W_set_mWheelsSimData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive4W.prototype, 'mWheelsSimData', { get: PxVehicleDrive4W.prototype.get_mWheelsSimData, set: PxVehicleDrive4W.prototype.set_mWheelsSimData });
  PxVehicleDrive4W.prototype['get_mWheelsDynData'] = PxVehicleDrive4W.prototype.get_mWheelsDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDrive4W_get_mWheelsDynData_0(self), PxVehicleWheelsDynData);
};
    PxVehicleDrive4W.prototype['set_mWheelsDynData'] = PxVehicleDrive4W.prototype.set_mWheelsDynData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDrive4W_set_mWheelsDynData_1(self, arg0);
};
    Object.defineProperty(PxVehicleDrive4W.prototype, 'mWheelsDynData', { get: PxVehicleDrive4W.prototype.get_mWheelsDynData, set: PxVehicleDrive4W.prototype.set_mWheelsDynData });
// PxVehicleDriveDynData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDriveDynData() { throw "cannot construct a PxVehicleDriveDynData, no constructor in IDL" }
PxVehicleDriveDynData.prototype = Object.create(WrapperObject.prototype);
PxVehicleDriveDynData.prototype.constructor = PxVehicleDriveDynData;
PxVehicleDriveDynData.prototype.__class__ = PxVehicleDriveDynData;
PxVehicleDriveDynData.__cache__ = {};
Module['PxVehicleDriveDynData'] = PxVehicleDriveDynData;

PxVehicleDriveDynData.prototype['setToRestState'] = PxVehicleDriveDynData.prototype.setToRestState = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setToRestState_0(self);
};;

PxVehicleDriveDynData.prototype['setAnalogInput'] = PxVehicleDriveDynData.prototype.setAnalogInput = /** @suppress {undefinedVars, duplicate} @this{Object} */function(type, analogVal) {
  var self = this.ptr;
  if (type && typeof type === 'object') type = type.ptr;
  if (analogVal && typeof analogVal === 'object') analogVal = analogVal.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setAnalogInput_2(self, type, analogVal);
};;

PxVehicleDriveDynData.prototype['getAnalogInput'] = PxVehicleDriveDynData.prototype.getAnalogInput = /** @suppress {undefinedVars, duplicate} @this{Object} */function(type) {
  var self = this.ptr;
  if (type && typeof type === 'object') type = type.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getAnalogInput_1(self, type);
};;

PxVehicleDriveDynData.prototype['setGearUp'] = PxVehicleDriveDynData.prototype.setGearUp = /** @suppress {undefinedVars, duplicate} @this{Object} */function(digitalVal) {
  var self = this.ptr;
  if (digitalVal && typeof digitalVal === 'object') digitalVal = digitalVal.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setGearUp_1(self, digitalVal);
};;

PxVehicleDriveDynData.prototype['setGearDown'] = PxVehicleDriveDynData.prototype.setGearDown = /** @suppress {undefinedVars, duplicate} @this{Object} */function(digitalVal) {
  var self = this.ptr;
  if (digitalVal && typeof digitalVal === 'object') digitalVal = digitalVal.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setGearDown_1(self, digitalVal);
};;

PxVehicleDriveDynData.prototype['getGearUp'] = PxVehicleDriveDynData.prototype.getGearUp = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDriveDynData_getGearUp_0(self));
};;

PxVehicleDriveDynData.prototype['getGearDown'] = PxVehicleDriveDynData.prototype.getGearDown = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDriveDynData_getGearDown_0(self));
};;

PxVehicleDriveDynData.prototype['setUseAutoGears'] = PxVehicleDriveDynData.prototype.setUseAutoGears = /** @suppress {undefinedVars, duplicate} @this{Object} */function(useAutoGears) {
  var self = this.ptr;
  if (useAutoGears && typeof useAutoGears === 'object') useAutoGears = useAutoGears.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setUseAutoGears_1(self, useAutoGears);
};;

PxVehicleDriveDynData.prototype['getUseAutoGears'] = PxVehicleDriveDynData.prototype.getUseAutoGears = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDriveDynData_getUseAutoGears_0(self));
};;

PxVehicleDriveDynData.prototype['toggleAutoGears'] = PxVehicleDriveDynData.prototype.toggleAutoGears = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDriveDynData_toggleAutoGears_0(self);
};;

PxVehicleDriveDynData.prototype['setCurrentGear'] = PxVehicleDriveDynData.prototype.setCurrentGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(currentGear) {
  var self = this.ptr;
  if (currentGear && typeof currentGear === 'object') currentGear = currentGear.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setCurrentGear_1(self, currentGear);
};;

PxVehicleDriveDynData.prototype['getCurrentGear'] = PxVehicleDriveDynData.prototype.getCurrentGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getCurrentGear_0(self);
};;

PxVehicleDriveDynData.prototype['setTargetGear'] = PxVehicleDriveDynData.prototype.setTargetGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(targetGear) {
  var self = this.ptr;
  if (targetGear && typeof targetGear === 'object') targetGear = targetGear.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setTargetGear_1(self, targetGear);
};;

PxVehicleDriveDynData.prototype['getTargetGear'] = PxVehicleDriveDynData.prototype.getTargetGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getTargetGear_0(self);
};;

PxVehicleDriveDynData.prototype['startGearChange'] = PxVehicleDriveDynData.prototype.startGearChange = /** @suppress {undefinedVars, duplicate} @this{Object} */function(targetGear) {
  var self = this.ptr;
  if (targetGear && typeof targetGear === 'object') targetGear = targetGear.ptr;
  _emscripten_bind_PxVehicleDriveDynData_startGearChange_1(self, targetGear);
};;

PxVehicleDriveDynData.prototype['forceGearChange'] = PxVehicleDriveDynData.prototype.forceGearChange = /** @suppress {undefinedVars, duplicate} @this{Object} */function(targetGear) {
  var self = this.ptr;
  if (targetGear && typeof targetGear === 'object') targetGear = targetGear.ptr;
  _emscripten_bind_PxVehicleDriveDynData_forceGearChange_1(self, targetGear);
};;

PxVehicleDriveDynData.prototype['setEngineRotationSpeed'] = PxVehicleDriveDynData.prototype.setEngineRotationSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(speed) {
  var self = this.ptr;
  if (speed && typeof speed === 'object') speed = speed.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setEngineRotationSpeed_1(self, speed);
};;

PxVehicleDriveDynData.prototype['getEngineRotationSpeed'] = PxVehicleDriveDynData.prototype.getEngineRotationSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getEngineRotationSpeed_0(self);
};;

PxVehicleDriveDynData.prototype['getGearSwitchTime'] = PxVehicleDriveDynData.prototype.getGearSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getGearSwitchTime_0(self);
};;

PxVehicleDriveDynData.prototype['getAutoBoxSwitchTime'] = PxVehicleDriveDynData.prototype.getAutoBoxSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getAutoBoxSwitchTime_0(self);
};;

PxVehicleDriveDynData.prototype['getNbAnalogInput'] = PxVehicleDriveDynData.prototype.getNbAnalogInput = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getNbAnalogInput_0(self);
};;

PxVehicleDriveDynData.prototype['setGearChange'] = PxVehicleDriveDynData.prototype.setGearChange = /** @suppress {undefinedVars, duplicate} @this{Object} */function(gearChange) {
  var self = this.ptr;
  if (gearChange && typeof gearChange === 'object') gearChange = gearChange.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setGearChange_1(self, gearChange);
};;

PxVehicleDriveDynData.prototype['getGearChange'] = PxVehicleDriveDynData.prototype.getGearChange = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_getGearChange_0(self);
};;

PxVehicleDriveDynData.prototype['setGearSwitchTime'] = PxVehicleDriveDynData.prototype.setGearSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function(switchTime) {
  var self = this.ptr;
  if (switchTime && typeof switchTime === 'object') switchTime = switchTime.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setGearSwitchTime_1(self, switchTime);
};;

PxVehicleDriveDynData.prototype['setAutoBoxSwitchTime'] = PxVehicleDriveDynData.prototype.setAutoBoxSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function(autoBoxSwitchTime) {
  var self = this.ptr;
  if (autoBoxSwitchTime && typeof autoBoxSwitchTime === 'object') autoBoxSwitchTime = autoBoxSwitchTime.ptr;
  _emscripten_bind_PxVehicleDriveDynData_setAutoBoxSwitchTime_1(self, autoBoxSwitchTime);
};;

  PxVehicleDriveDynData.prototype['get_mControlAnalogVals'] = PxVehicleDriveDynData.prototype.get_mControlAnalogVals = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_get_mControlAnalogVals_1(self, arg0);
};
    PxVehicleDriveDynData.prototype['set_mControlAnalogVals'] = PxVehicleDriveDynData.prototype.set_mControlAnalogVals = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0, arg1) {
  var self = this.ptr;
  ensureCache.prepare();
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  if (arg1 && typeof arg1 === 'object') arg1 = arg1.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mControlAnalogVals_2(self, arg0, arg1);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mControlAnalogVals', { get: PxVehicleDriveDynData.prototype.get_mControlAnalogVals, set: PxVehicleDriveDynData.prototype.set_mControlAnalogVals });
  PxVehicleDriveDynData.prototype['get_mUseAutoGears'] = PxVehicleDriveDynData.prototype.get_mUseAutoGears = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDriveDynData_get_mUseAutoGears_0(self));
};
    PxVehicleDriveDynData.prototype['set_mUseAutoGears'] = PxVehicleDriveDynData.prototype.set_mUseAutoGears = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mUseAutoGears_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mUseAutoGears', { get: PxVehicleDriveDynData.prototype.get_mUseAutoGears, set: PxVehicleDriveDynData.prototype.set_mUseAutoGears });
  PxVehicleDriveDynData.prototype['get_mGearUpPressed'] = PxVehicleDriveDynData.prototype.get_mGearUpPressed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDriveDynData_get_mGearUpPressed_0(self));
};
    PxVehicleDriveDynData.prototype['set_mGearUpPressed'] = PxVehicleDriveDynData.prototype.set_mGearUpPressed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mGearUpPressed_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mGearUpPressed', { get: PxVehicleDriveDynData.prototype.get_mGearUpPressed, set: PxVehicleDriveDynData.prototype.set_mGearUpPressed });
  PxVehicleDriveDynData.prototype['get_mGearDownPressed'] = PxVehicleDriveDynData.prototype.get_mGearDownPressed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxVehicleDriveDynData_get_mGearDownPressed_0(self));
};
    PxVehicleDriveDynData.prototype['set_mGearDownPressed'] = PxVehicleDriveDynData.prototype.set_mGearDownPressed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mGearDownPressed_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mGearDownPressed', { get: PxVehicleDriveDynData.prototype.get_mGearDownPressed, set: PxVehicleDriveDynData.prototype.set_mGearDownPressed });
  PxVehicleDriveDynData.prototype['get_mCurrentGear'] = PxVehicleDriveDynData.prototype.get_mCurrentGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_get_mCurrentGear_0(self);
};
    PxVehicleDriveDynData.prototype['set_mCurrentGear'] = PxVehicleDriveDynData.prototype.set_mCurrentGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mCurrentGear_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mCurrentGear', { get: PxVehicleDriveDynData.prototype.get_mCurrentGear, set: PxVehicleDriveDynData.prototype.set_mCurrentGear });
  PxVehicleDriveDynData.prototype['get_mTargetGear'] = PxVehicleDriveDynData.prototype.get_mTargetGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_get_mTargetGear_0(self);
};
    PxVehicleDriveDynData.prototype['set_mTargetGear'] = PxVehicleDriveDynData.prototype.set_mTargetGear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mTargetGear_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mTargetGear', { get: PxVehicleDriveDynData.prototype.get_mTargetGear, set: PxVehicleDriveDynData.prototype.set_mTargetGear });
  PxVehicleDriveDynData.prototype['get_mEnginespeed'] = PxVehicleDriveDynData.prototype.get_mEnginespeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_get_mEnginespeed_0(self);
};
    PxVehicleDriveDynData.prototype['set_mEnginespeed'] = PxVehicleDriveDynData.prototype.set_mEnginespeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mEnginespeed_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mEnginespeed', { get: PxVehicleDriveDynData.prototype.get_mEnginespeed, set: PxVehicleDriveDynData.prototype.set_mEnginespeed });
  PxVehicleDriveDynData.prototype['get_mGearSwitchTime'] = PxVehicleDriveDynData.prototype.get_mGearSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_get_mGearSwitchTime_0(self);
};
    PxVehicleDriveDynData.prototype['set_mGearSwitchTime'] = PxVehicleDriveDynData.prototype.set_mGearSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mGearSwitchTime_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mGearSwitchTime', { get: PxVehicleDriveDynData.prototype.get_mGearSwitchTime, set: PxVehicleDriveDynData.prototype.set_mGearSwitchTime });
  PxVehicleDriveDynData.prototype['get_mAutoBoxSwitchTime'] = PxVehicleDriveDynData.prototype.get_mAutoBoxSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleDriveDynData_get_mAutoBoxSwitchTime_0(self);
};
    PxVehicleDriveDynData.prototype['set_mAutoBoxSwitchTime'] = PxVehicleDriveDynData.prototype.set_mAutoBoxSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleDriveDynData_set_mAutoBoxSwitchTime_1(self, arg0);
};
    Object.defineProperty(PxVehicleDriveDynData.prototype, 'mAutoBoxSwitchTime', { get: PxVehicleDriveDynData.prototype.get_mAutoBoxSwitchTime, set: PxVehicleDriveDynData.prototype.set_mAutoBoxSwitchTime });
  PxVehicleDriveDynData.prototype['__destroy__'] = PxVehicleDriveDynData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDriveDynData___destroy___0(self);
};
// PxVehicleDriveSimData4W
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleDriveSimData4W() {
  this.ptr = _emscripten_bind_PxVehicleDriveSimData4W_PxVehicleDriveSimData4W_0();
  getCache(PxVehicleDriveSimData4W)[this.ptr] = this;
};;
PxVehicleDriveSimData4W.prototype = Object.create(PxVehicleDriveSimData.prototype);
PxVehicleDriveSimData4W.prototype.constructor = PxVehicleDriveSimData4W;
PxVehicleDriveSimData4W.prototype.__class__ = PxVehicleDriveSimData4W;
PxVehicleDriveSimData4W.__cache__ = {};
Module['PxVehicleDriveSimData4W'] = PxVehicleDriveSimData4W;

PxVehicleDriveSimData4W.prototype['getDiffData'] = PxVehicleDriveSimData4W.prototype.getDiffData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData4W_getDiffData_0(self), PxVehicleDifferential4WData);
};;

PxVehicleDriveSimData4W.prototype['getAckermannGeometryData'] = PxVehicleDriveSimData4W.prototype.getAckermannGeometryData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData4W_getAckermannGeometryData_0(self), PxVehicleAckermannGeometryData);
};;

PxVehicleDriveSimData4W.prototype['setDiffData'] = PxVehicleDriveSimData4W.prototype.setDiffData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(diff) {
  var self = this.ptr;
  if (diff && typeof diff === 'object') diff = diff.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W_setDiffData_1(self, diff);
};;

PxVehicleDriveSimData4W.prototype['setAckermannGeometryData'] = PxVehicleDriveSimData4W.prototype.setAckermannGeometryData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(ackermannData) {
  var self = this.ptr;
  if (ackermannData && typeof ackermannData === 'object') ackermannData = ackermannData.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W_setAckermannGeometryData_1(self, ackermannData);
};;

PxVehicleDriveSimData4W.prototype['getEngineData'] = PxVehicleDriveSimData4W.prototype.getEngineData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData4W_getEngineData_0(self), PxVehicleEngineData);
};;

PxVehicleDriveSimData4W.prototype['setEngineData'] = PxVehicleDriveSimData4W.prototype.setEngineData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(engine) {
  var self = this.ptr;
  if (engine && typeof engine === 'object') engine = engine.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W_setEngineData_1(self, engine);
};;

PxVehicleDriveSimData4W.prototype['getGearsData'] = PxVehicleDriveSimData4W.prototype.getGearsData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData4W_getGearsData_0(self), PxVehicleGearsData);
};;

PxVehicleDriveSimData4W.prototype['setGearsData'] = PxVehicleDriveSimData4W.prototype.setGearsData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(gears) {
  var self = this.ptr;
  if (gears && typeof gears === 'object') gears = gears.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W_setGearsData_1(self, gears);
};;

PxVehicleDriveSimData4W.prototype['getClutchData'] = PxVehicleDriveSimData4W.prototype.getClutchData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData4W_getClutchData_0(self), PxVehicleClutchData);
};;

PxVehicleDriveSimData4W.prototype['setClutchData'] = PxVehicleDriveSimData4W.prototype.setClutchData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(clutch) {
  var self = this.ptr;
  if (clutch && typeof clutch === 'object') clutch = clutch.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W_setClutchData_1(self, clutch);
};;

PxVehicleDriveSimData4W.prototype['getAutoBoxData'] = PxVehicleDriveSimData4W.prototype.getAutoBoxData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleDriveSimData4W_getAutoBoxData_0(self), PxVehicleAutoBoxData);
};;

PxVehicleDriveSimData4W.prototype['setAutoBoxData'] = PxVehicleDriveSimData4W.prototype.setAutoBoxData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(clutch) {
  var self = this.ptr;
  if (clutch && typeof clutch === 'object') clutch = clutch.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W_setAutoBoxData_1(self, clutch);
};;

  PxVehicleDriveSimData4W.prototype['__destroy__'] = PxVehicleDriveSimData4W.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleDriveSimData4W___destroy___0(self);
};
// PxVehicleEngineData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleEngineData() {
  this.ptr = _emscripten_bind_PxVehicleEngineData_PxVehicleEngineData_0();
  getCache(PxVehicleEngineData)[this.ptr] = this;
};;
PxVehicleEngineData.prototype = Object.create(WrapperObject.prototype);
PxVehicleEngineData.prototype.constructor = PxVehicleEngineData;
PxVehicleEngineData.prototype.__class__ = PxVehicleEngineData;
PxVehicleEngineData.__cache__ = {};
Module['PxVehicleEngineData'] = PxVehicleEngineData;

  PxVehicleEngineData.prototype['get_mTorqueCurve'] = PxVehicleEngineData.prototype.get_mTorqueCurve = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleEngineData_get_mTorqueCurve_0(self), PxEngineTorqueLookupTable);
};
    PxVehicleEngineData.prototype['set_mTorqueCurve'] = PxVehicleEngineData.prototype.set_mTorqueCurve = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mTorqueCurve_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mTorqueCurve', { get: PxVehicleEngineData.prototype.get_mTorqueCurve, set: PxVehicleEngineData.prototype.set_mTorqueCurve });
  PxVehicleEngineData.prototype['get_mMOI'] = PxVehicleEngineData.prototype.get_mMOI = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleEngineData_get_mMOI_0(self);
};
    PxVehicleEngineData.prototype['set_mMOI'] = PxVehicleEngineData.prototype.set_mMOI = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mMOI_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mMOI', { get: PxVehicleEngineData.prototype.get_mMOI, set: PxVehicleEngineData.prototype.set_mMOI });
  PxVehicleEngineData.prototype['get_mPeakTorque'] = PxVehicleEngineData.prototype.get_mPeakTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleEngineData_get_mPeakTorque_0(self);
};
    PxVehicleEngineData.prototype['set_mPeakTorque'] = PxVehicleEngineData.prototype.set_mPeakTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mPeakTorque_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mPeakTorque', { get: PxVehicleEngineData.prototype.get_mPeakTorque, set: PxVehicleEngineData.prototype.set_mPeakTorque });
  PxVehicleEngineData.prototype['get_mMaxOmega'] = PxVehicleEngineData.prototype.get_mMaxOmega = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleEngineData_get_mMaxOmega_0(self);
};
    PxVehicleEngineData.prototype['set_mMaxOmega'] = PxVehicleEngineData.prototype.set_mMaxOmega = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mMaxOmega_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mMaxOmega', { get: PxVehicleEngineData.prototype.get_mMaxOmega, set: PxVehicleEngineData.prototype.set_mMaxOmega });
  PxVehicleEngineData.prototype['get_mDampingRateFullThrottle'] = PxVehicleEngineData.prototype.get_mDampingRateFullThrottle = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleEngineData_get_mDampingRateFullThrottle_0(self);
};
    PxVehicleEngineData.prototype['set_mDampingRateFullThrottle'] = PxVehicleEngineData.prototype.set_mDampingRateFullThrottle = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mDampingRateFullThrottle_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mDampingRateFullThrottle', { get: PxVehicleEngineData.prototype.get_mDampingRateFullThrottle, set: PxVehicleEngineData.prototype.set_mDampingRateFullThrottle });
  PxVehicleEngineData.prototype['get_mDampingRateZeroThrottleClutchEngaged'] = PxVehicleEngineData.prototype.get_mDampingRateZeroThrottleClutchEngaged = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchEngaged_0(self);
};
    PxVehicleEngineData.prototype['set_mDampingRateZeroThrottleClutchEngaged'] = PxVehicleEngineData.prototype.set_mDampingRateZeroThrottleClutchEngaged = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchEngaged_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mDampingRateZeroThrottleClutchEngaged', { get: PxVehicleEngineData.prototype.get_mDampingRateZeroThrottleClutchEngaged, set: PxVehicleEngineData.prototype.set_mDampingRateZeroThrottleClutchEngaged });
  PxVehicleEngineData.prototype['get_mDampingRateZeroThrottleClutchDisengaged'] = PxVehicleEngineData.prototype.get_mDampingRateZeroThrottleClutchDisengaged = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleEngineData_get_mDampingRateZeroThrottleClutchDisengaged_0(self);
};
    PxVehicleEngineData.prototype['set_mDampingRateZeroThrottleClutchDisengaged'] = PxVehicleEngineData.prototype.set_mDampingRateZeroThrottleClutchDisengaged = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleEngineData_set_mDampingRateZeroThrottleClutchDisengaged_1(self, arg0);
};
    Object.defineProperty(PxVehicleEngineData.prototype, 'mDampingRateZeroThrottleClutchDisengaged', { get: PxVehicleEngineData.prototype.get_mDampingRateZeroThrottleClutchDisengaged, set: PxVehicleEngineData.prototype.set_mDampingRateZeroThrottleClutchDisengaged });
  PxVehicleEngineData.prototype['__destroy__'] = PxVehicleEngineData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleEngineData___destroy___0(self);
};
// PxEngineTorqueLookupTable
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxEngineTorqueLookupTable() {
  this.ptr = _emscripten_bind_PxEngineTorqueLookupTable_PxEngineTorqueLookupTable_0();
  getCache(PxEngineTorqueLookupTable)[this.ptr] = this;
};;
PxEngineTorqueLookupTable.prototype = Object.create(WrapperObject.prototype);
PxEngineTorqueLookupTable.prototype.constructor = PxEngineTorqueLookupTable;
PxEngineTorqueLookupTable.prototype.__class__ = PxEngineTorqueLookupTable;
PxEngineTorqueLookupTable.__cache__ = {};
Module['PxEngineTorqueLookupTable'] = PxEngineTorqueLookupTable;

PxEngineTorqueLookupTable.prototype['addPair'] = PxEngineTorqueLookupTable.prototype.addPair = /** @suppress {undefinedVars, duplicate} @this{Object} */function(x, y) {
  var self = this.ptr;
  if (x && typeof x === 'object') x = x.ptr;
  if (y && typeof y === 'object') y = y.ptr;
  _emscripten_bind_PxEngineTorqueLookupTable_addPair_2(self, x, y);
};;

PxEngineTorqueLookupTable.prototype['getYVal'] = PxEngineTorqueLookupTable.prototype.getYVal = /** @suppress {undefinedVars, duplicate} @this{Object} */function(x) {
  var self = this.ptr;
  if (x && typeof x === 'object') x = x.ptr;
  return _emscripten_bind_PxEngineTorqueLookupTable_getYVal_1(self, x);
};;

PxEngineTorqueLookupTable.prototype['getNbDataPairs'] = PxEngineTorqueLookupTable.prototype.getNbDataPairs = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxEngineTorqueLookupTable_getNbDataPairs_0(self);
};;

PxEngineTorqueLookupTable.prototype['clear'] = PxEngineTorqueLookupTable.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxEngineTorqueLookupTable_clear_0(self);
};;

PxEngineTorqueLookupTable.prototype['getX'] = PxEngineTorqueLookupTable.prototype.getX = /** @suppress {undefinedVars, duplicate} @this{Object} */function(i) {
  var self = this.ptr;
  if (i && typeof i === 'object') i = i.ptr;
  return _emscripten_bind_PxEngineTorqueLookupTable_getX_1(self, i);
};;

PxEngineTorqueLookupTable.prototype['getY'] = PxEngineTorqueLookupTable.prototype.getY = /** @suppress {undefinedVars, duplicate} @this{Object} */function(i) {
  var self = this.ptr;
  if (i && typeof i === 'object') i = i.ptr;
  return _emscripten_bind_PxEngineTorqueLookupTable_getY_1(self, i);
};;

  PxEngineTorqueLookupTable.prototype['get_mDataPairs'] = PxEngineTorqueLookupTable.prototype.get_mDataPairs = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  return _emscripten_bind_PxEngineTorqueLookupTable_get_mDataPairs_1(self, arg0);
};
    PxEngineTorqueLookupTable.prototype['set_mDataPairs'] = PxEngineTorqueLookupTable.prototype.set_mDataPairs = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0, arg1) {
  var self = this.ptr;
  ensureCache.prepare();
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  if (arg1 && typeof arg1 === 'object') arg1 = arg1.ptr;
  _emscripten_bind_PxEngineTorqueLookupTable_set_mDataPairs_2(self, arg0, arg1);
};
    Object.defineProperty(PxEngineTorqueLookupTable.prototype, 'mDataPairs', { get: PxEngineTorqueLookupTable.prototype.get_mDataPairs, set: PxEngineTorqueLookupTable.prototype.set_mDataPairs });
  PxEngineTorqueLookupTable.prototype['get_mNbDataPairs'] = PxEngineTorqueLookupTable.prototype.get_mNbDataPairs = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxEngineTorqueLookupTable_get_mNbDataPairs_0(self);
};
    PxEngineTorqueLookupTable.prototype['set_mNbDataPairs'] = PxEngineTorqueLookupTable.prototype.set_mNbDataPairs = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxEngineTorqueLookupTable_set_mNbDataPairs_1(self, arg0);
};
    Object.defineProperty(PxEngineTorqueLookupTable.prototype, 'mNbDataPairs', { get: PxEngineTorqueLookupTable.prototype.get_mNbDataPairs, set: PxEngineTorqueLookupTable.prototype.set_mNbDataPairs });
  PxEngineTorqueLookupTable.prototype['__destroy__'] = PxEngineTorqueLookupTable.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxEngineTorqueLookupTable___destroy___0(self);
};
// PxVehicleGearsData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleGearsData() {
  this.ptr = _emscripten_bind_PxVehicleGearsData_PxVehicleGearsData_0();
  getCache(PxVehicleGearsData)[this.ptr] = this;
};;
PxVehicleGearsData.prototype = Object.create(WrapperObject.prototype);
PxVehicleGearsData.prototype.constructor = PxVehicleGearsData;
PxVehicleGearsData.prototype.__class__ = PxVehicleGearsData;
PxVehicleGearsData.__cache__ = {};
Module['PxVehicleGearsData'] = PxVehicleGearsData;

PxVehicleGearsData.prototype['getGearRatio'] = PxVehicleGearsData.prototype.getGearRatio = /** @suppress {undefinedVars, duplicate} @this{Object} */function(a) {
  var self = this.ptr;
  if (a && typeof a === 'object') a = a.ptr;
  return _emscripten_bind_PxVehicleGearsData_getGearRatio_1(self, a);
};;

PxVehicleGearsData.prototype['setGearRatio'] = PxVehicleGearsData.prototype.setGearRatio = /** @suppress {undefinedVars, duplicate} @this{Object} */function(a, ratio) {
  var self = this.ptr;
  if (a && typeof a === 'object') a = a.ptr;
  if (ratio && typeof ratio === 'object') ratio = ratio.ptr;
  _emscripten_bind_PxVehicleGearsData_setGearRatio_2(self, a, ratio);
};;

  PxVehicleGearsData.prototype['get_mRatios'] = PxVehicleGearsData.prototype.get_mRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  return _emscripten_bind_PxVehicleGearsData_get_mRatios_1(self, arg0);
};
    PxVehicleGearsData.prototype['set_mRatios'] = PxVehicleGearsData.prototype.set_mRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0, arg1) {
  var self = this.ptr;
  ensureCache.prepare();
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  if (arg1 && typeof arg1 === 'object') arg1 = arg1.ptr;
  _emscripten_bind_PxVehicleGearsData_set_mRatios_2(self, arg0, arg1);
};
    Object.defineProperty(PxVehicleGearsData.prototype, 'mRatios', { get: PxVehicleGearsData.prototype.get_mRatios, set: PxVehicleGearsData.prototype.set_mRatios });
  PxVehicleGearsData.prototype['get_mFinalRatio'] = PxVehicleGearsData.prototype.get_mFinalRatio = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleGearsData_get_mFinalRatio_0(self);
};
    PxVehicleGearsData.prototype['set_mFinalRatio'] = PxVehicleGearsData.prototype.set_mFinalRatio = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleGearsData_set_mFinalRatio_1(self, arg0);
};
    Object.defineProperty(PxVehicleGearsData.prototype, 'mFinalRatio', { get: PxVehicleGearsData.prototype.get_mFinalRatio, set: PxVehicleGearsData.prototype.set_mFinalRatio });
  PxVehicleGearsData.prototype['get_mNbRatios'] = PxVehicleGearsData.prototype.get_mNbRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleGearsData_get_mNbRatios_0(self);
};
    PxVehicleGearsData.prototype['set_mNbRatios'] = PxVehicleGearsData.prototype.set_mNbRatios = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleGearsData_set_mNbRatios_1(self, arg0);
};
    Object.defineProperty(PxVehicleGearsData.prototype, 'mNbRatios', { get: PxVehicleGearsData.prototype.get_mNbRatios, set: PxVehicleGearsData.prototype.set_mNbRatios });
  PxVehicleGearsData.prototype['get_mSwitchTime'] = PxVehicleGearsData.prototype.get_mSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleGearsData_get_mSwitchTime_0(self);
};
    PxVehicleGearsData.prototype['set_mSwitchTime'] = PxVehicleGearsData.prototype.set_mSwitchTime = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleGearsData_set_mSwitchTime_1(self, arg0);
};
    Object.defineProperty(PxVehicleGearsData.prototype, 'mSwitchTime', { get: PxVehicleGearsData.prototype.get_mSwitchTime, set: PxVehicleGearsData.prototype.set_mSwitchTime });
  PxVehicleGearsData.prototype['__destroy__'] = PxVehicleGearsData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleGearsData___destroy___0(self);
};
// PxVehicleSuspensionData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleSuspensionData() {
  this.ptr = _emscripten_bind_PxVehicleSuspensionData_PxVehicleSuspensionData_0();
  getCache(PxVehicleSuspensionData)[this.ptr] = this;
};;
PxVehicleSuspensionData.prototype = Object.create(WrapperObject.prototype);
PxVehicleSuspensionData.prototype.constructor = PxVehicleSuspensionData;
PxVehicleSuspensionData.prototype.__class__ = PxVehicleSuspensionData;
PxVehicleSuspensionData.__cache__ = {};
Module['PxVehicleSuspensionData'] = PxVehicleSuspensionData;

PxVehicleSuspensionData.prototype['setMassAndPreserveNaturalFrequency'] = PxVehicleSuspensionData.prototype.setMassAndPreserveNaturalFrequency = /** @suppress {undefinedVars, duplicate} @this{Object} */function(newSprungMass) {
  var self = this.ptr;
  if (newSprungMass && typeof newSprungMass === 'object') newSprungMass = newSprungMass.ptr;
  _emscripten_bind_PxVehicleSuspensionData_setMassAndPreserveNaturalFrequency_1(self, newSprungMass);
};;

  PxVehicleSuspensionData.prototype['get_mSpringStrength'] = PxVehicleSuspensionData.prototype.get_mSpringStrength = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mSpringStrength_0(self);
};
    PxVehicleSuspensionData.prototype['set_mSpringStrength'] = PxVehicleSuspensionData.prototype.set_mSpringStrength = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mSpringStrength_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mSpringStrength', { get: PxVehicleSuspensionData.prototype.get_mSpringStrength, set: PxVehicleSuspensionData.prototype.set_mSpringStrength });
  PxVehicleSuspensionData.prototype['get_mSpringDamperRate'] = PxVehicleSuspensionData.prototype.get_mSpringDamperRate = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mSpringDamperRate_0(self);
};
    PxVehicleSuspensionData.prototype['set_mSpringDamperRate'] = PxVehicleSuspensionData.prototype.set_mSpringDamperRate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mSpringDamperRate_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mSpringDamperRate', { get: PxVehicleSuspensionData.prototype.get_mSpringDamperRate, set: PxVehicleSuspensionData.prototype.set_mSpringDamperRate });
  PxVehicleSuspensionData.prototype['get_mMaxCompression'] = PxVehicleSuspensionData.prototype.get_mMaxCompression = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mMaxCompression_0(self);
};
    PxVehicleSuspensionData.prototype['set_mMaxCompression'] = PxVehicleSuspensionData.prototype.set_mMaxCompression = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mMaxCompression_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mMaxCompression', { get: PxVehicleSuspensionData.prototype.get_mMaxCompression, set: PxVehicleSuspensionData.prototype.set_mMaxCompression });
  PxVehicleSuspensionData.prototype['get_mMaxDroop'] = PxVehicleSuspensionData.prototype.get_mMaxDroop = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mMaxDroop_0(self);
};
    PxVehicleSuspensionData.prototype['set_mMaxDroop'] = PxVehicleSuspensionData.prototype.set_mMaxDroop = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mMaxDroop_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mMaxDroop', { get: PxVehicleSuspensionData.prototype.get_mMaxDroop, set: PxVehicleSuspensionData.prototype.set_mMaxDroop });
  PxVehicleSuspensionData.prototype['get_mSprungMass'] = PxVehicleSuspensionData.prototype.get_mSprungMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mSprungMass_0(self);
};
    PxVehicleSuspensionData.prototype['set_mSprungMass'] = PxVehicleSuspensionData.prototype.set_mSprungMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mSprungMass_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mSprungMass', { get: PxVehicleSuspensionData.prototype.get_mSprungMass, set: PxVehicleSuspensionData.prototype.set_mSprungMass });
  PxVehicleSuspensionData.prototype['get_mCamberAtRest'] = PxVehicleSuspensionData.prototype.get_mCamberAtRest = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mCamberAtRest_0(self);
};
    PxVehicleSuspensionData.prototype['set_mCamberAtRest'] = PxVehicleSuspensionData.prototype.set_mCamberAtRest = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mCamberAtRest_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mCamberAtRest', { get: PxVehicleSuspensionData.prototype.get_mCamberAtRest, set: PxVehicleSuspensionData.prototype.set_mCamberAtRest });
  PxVehicleSuspensionData.prototype['get_mCamberAtMaxCompression'] = PxVehicleSuspensionData.prototype.get_mCamberAtMaxCompression = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxCompression_0(self);
};
    PxVehicleSuspensionData.prototype['set_mCamberAtMaxCompression'] = PxVehicleSuspensionData.prototype.set_mCamberAtMaxCompression = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxCompression_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mCamberAtMaxCompression', { get: PxVehicleSuspensionData.prototype.get_mCamberAtMaxCompression, set: PxVehicleSuspensionData.prototype.set_mCamberAtMaxCompression });
  PxVehicleSuspensionData.prototype['get_mCamberAtMaxDroop'] = PxVehicleSuspensionData.prototype.get_mCamberAtMaxDroop = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleSuspensionData_get_mCamberAtMaxDroop_0(self);
};
    PxVehicleSuspensionData.prototype['set_mCamberAtMaxDroop'] = PxVehicleSuspensionData.prototype.set_mCamberAtMaxDroop = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleSuspensionData_set_mCamberAtMaxDroop_1(self, arg0);
};
    Object.defineProperty(PxVehicleSuspensionData.prototype, 'mCamberAtMaxDroop', { get: PxVehicleSuspensionData.prototype.get_mCamberAtMaxDroop, set: PxVehicleSuspensionData.prototype.set_mCamberAtMaxDroop });
  PxVehicleSuspensionData.prototype['__destroy__'] = PxVehicleSuspensionData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleSuspensionData___destroy___0(self);
};
// PxVehicleTireData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleTireData() {
  this.ptr = _emscripten_bind_PxVehicleTireData_PxVehicleTireData_0();
  getCache(PxVehicleTireData)[this.ptr] = this;
};;
PxVehicleTireData.prototype = Object.create(WrapperObject.prototype);
PxVehicleTireData.prototype.constructor = PxVehicleTireData;
PxVehicleTireData.prototype.__class__ = PxVehicleTireData;
PxVehicleTireData.__cache__ = {};
Module['PxVehicleTireData'] = PxVehicleTireData;

  PxVehicleTireData.prototype['get_mLatStiffX'] = PxVehicleTireData.prototype.get_mLatStiffX = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireData_get_mLatStiffX_0(self);
};
    PxVehicleTireData.prototype['set_mLatStiffX'] = PxVehicleTireData.prototype.set_mLatStiffX = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireData_set_mLatStiffX_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireData.prototype, 'mLatStiffX', { get: PxVehicleTireData.prototype.get_mLatStiffX, set: PxVehicleTireData.prototype.set_mLatStiffX });
  PxVehicleTireData.prototype['get_mLatStiffY'] = PxVehicleTireData.prototype.get_mLatStiffY = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireData_get_mLatStiffY_0(self);
};
    PxVehicleTireData.prototype['set_mLatStiffY'] = PxVehicleTireData.prototype.set_mLatStiffY = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireData_set_mLatStiffY_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireData.prototype, 'mLatStiffY', { get: PxVehicleTireData.prototype.get_mLatStiffY, set: PxVehicleTireData.prototype.set_mLatStiffY });
  PxVehicleTireData.prototype['get_mLongitudinalStiffnessPerUnitGravity'] = PxVehicleTireData.prototype.get_mLongitudinalStiffnessPerUnitGravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireData_get_mLongitudinalStiffnessPerUnitGravity_0(self);
};
    PxVehicleTireData.prototype['set_mLongitudinalStiffnessPerUnitGravity'] = PxVehicleTireData.prototype.set_mLongitudinalStiffnessPerUnitGravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireData_set_mLongitudinalStiffnessPerUnitGravity_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireData.prototype, 'mLongitudinalStiffnessPerUnitGravity', { get: PxVehicleTireData.prototype.get_mLongitudinalStiffnessPerUnitGravity, set: PxVehicleTireData.prototype.set_mLongitudinalStiffnessPerUnitGravity });
  PxVehicleTireData.prototype['get_mCamberStiffnessPerUnitGravity'] = PxVehicleTireData.prototype.get_mCamberStiffnessPerUnitGravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireData_get_mCamberStiffnessPerUnitGravity_0(self);
};
    PxVehicleTireData.prototype['set_mCamberStiffnessPerUnitGravity'] = PxVehicleTireData.prototype.set_mCamberStiffnessPerUnitGravity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireData_set_mCamberStiffnessPerUnitGravity_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireData.prototype, 'mCamberStiffnessPerUnitGravity', { get: PxVehicleTireData.prototype.get_mCamberStiffnessPerUnitGravity, set: PxVehicleTireData.prototype.set_mCamberStiffnessPerUnitGravity });
  PxVehicleTireData.prototype['get_mType'] = PxVehicleTireData.prototype.get_mType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireData_get_mType_0(self);
};
    PxVehicleTireData.prototype['set_mType'] = PxVehicleTireData.prototype.set_mType = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireData_set_mType_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireData.prototype, 'mType', { get: PxVehicleTireData.prototype.get_mType, set: PxVehicleTireData.prototype.set_mType });
  PxVehicleTireData.prototype['__destroy__'] = PxVehicleTireData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleTireData___destroy___0(self);
};
// PxVehicleTireLoadFilterData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleTireLoadFilterData() {
  this.ptr = _emscripten_bind_PxVehicleTireLoadFilterData_PxVehicleTireLoadFilterData_0();
  getCache(PxVehicleTireLoadFilterData)[this.ptr] = this;
};;
PxVehicleTireLoadFilterData.prototype = Object.create(WrapperObject.prototype);
PxVehicleTireLoadFilterData.prototype.constructor = PxVehicleTireLoadFilterData;
PxVehicleTireLoadFilterData.prototype.__class__ = PxVehicleTireLoadFilterData;
PxVehicleTireLoadFilterData.__cache__ = {};
Module['PxVehicleTireLoadFilterData'] = PxVehicleTireLoadFilterData;

PxVehicleTireLoadFilterData.prototype['getDenominator'] = PxVehicleTireLoadFilterData.prototype.getDenominator = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireLoadFilterData_getDenominator_0(self);
};;

  PxVehicleTireLoadFilterData.prototype['get_mMinNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.get_mMinNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireLoadFilterData_get_mMinNormalisedLoad_0(self);
};
    PxVehicleTireLoadFilterData.prototype['set_mMinNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.set_mMinNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireLoadFilterData_set_mMinNormalisedLoad_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireLoadFilterData.prototype, 'mMinNormalisedLoad', { get: PxVehicleTireLoadFilterData.prototype.get_mMinNormalisedLoad, set: PxVehicleTireLoadFilterData.prototype.set_mMinNormalisedLoad });
  PxVehicleTireLoadFilterData.prototype['get_mMinFilteredNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.get_mMinFilteredNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireLoadFilterData_get_mMinFilteredNormalisedLoad_0(self);
};
    PxVehicleTireLoadFilterData.prototype['set_mMinFilteredNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.set_mMinFilteredNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireLoadFilterData_set_mMinFilteredNormalisedLoad_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireLoadFilterData.prototype, 'mMinFilteredNormalisedLoad', { get: PxVehicleTireLoadFilterData.prototype.get_mMinFilteredNormalisedLoad, set: PxVehicleTireLoadFilterData.prototype.set_mMinFilteredNormalisedLoad });
  PxVehicleTireLoadFilterData.prototype['get_mMaxNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.get_mMaxNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxNormalisedLoad_0(self);
};
    PxVehicleTireLoadFilterData.prototype['set_mMaxNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.set_mMaxNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxNormalisedLoad_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireLoadFilterData.prototype, 'mMaxNormalisedLoad', { get: PxVehicleTireLoadFilterData.prototype.get_mMaxNormalisedLoad, set: PxVehicleTireLoadFilterData.prototype.set_mMaxNormalisedLoad });
  PxVehicleTireLoadFilterData.prototype['get_mMaxFilteredNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.get_mMaxFilteredNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleTireLoadFilterData_get_mMaxFilteredNormalisedLoad_0(self);
};
    PxVehicleTireLoadFilterData.prototype['set_mMaxFilteredNormalisedLoad'] = PxVehicleTireLoadFilterData.prototype.set_mMaxFilteredNormalisedLoad = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleTireLoadFilterData_set_mMaxFilteredNormalisedLoad_1(self, arg0);
};
    Object.defineProperty(PxVehicleTireLoadFilterData.prototype, 'mMaxFilteredNormalisedLoad', { get: PxVehicleTireLoadFilterData.prototype.get_mMaxFilteredNormalisedLoad, set: PxVehicleTireLoadFilterData.prototype.set_mMaxFilteredNormalisedLoad });
  PxVehicleTireLoadFilterData.prototype['__destroy__'] = PxVehicleTireLoadFilterData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleTireLoadFilterData___destroy___0(self);
};
// PxVehicleWheelData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheelData() {
  this.ptr = _emscripten_bind_PxVehicleWheelData_PxVehicleWheelData_0();
  getCache(PxVehicleWheelData)[this.ptr] = this;
};;
PxVehicleWheelData.prototype = Object.create(WrapperObject.prototype);
PxVehicleWheelData.prototype.constructor = PxVehicleWheelData;
PxVehicleWheelData.prototype.__class__ = PxVehicleWheelData;
PxVehicleWheelData.__cache__ = {};
Module['PxVehicleWheelData'] = PxVehicleWheelData;

  PxVehicleWheelData.prototype['get_mRadius'] = PxVehicleWheelData.prototype.get_mRadius = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mRadius_0(self);
};
    PxVehicleWheelData.prototype['set_mRadius'] = PxVehicleWheelData.prototype.set_mRadius = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mRadius_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mRadius', { get: PxVehicleWheelData.prototype.get_mRadius, set: PxVehicleWheelData.prototype.set_mRadius });
  PxVehicleWheelData.prototype['get_mWidth'] = PxVehicleWheelData.prototype.get_mWidth = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mWidth_0(self);
};
    PxVehicleWheelData.prototype['set_mWidth'] = PxVehicleWheelData.prototype.set_mWidth = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mWidth_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mWidth', { get: PxVehicleWheelData.prototype.get_mWidth, set: PxVehicleWheelData.prototype.set_mWidth });
  PxVehicleWheelData.prototype['get_mMass'] = PxVehicleWheelData.prototype.get_mMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mMass_0(self);
};
    PxVehicleWheelData.prototype['set_mMass'] = PxVehicleWheelData.prototype.set_mMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mMass_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mMass', { get: PxVehicleWheelData.prototype.get_mMass, set: PxVehicleWheelData.prototype.set_mMass });
  PxVehicleWheelData.prototype['get_mMOI'] = PxVehicleWheelData.prototype.get_mMOI = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mMOI_0(self);
};
    PxVehicleWheelData.prototype['set_mMOI'] = PxVehicleWheelData.prototype.set_mMOI = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mMOI_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mMOI', { get: PxVehicleWheelData.prototype.get_mMOI, set: PxVehicleWheelData.prototype.set_mMOI });
  PxVehicleWheelData.prototype['get_mDampingRate'] = PxVehicleWheelData.prototype.get_mDampingRate = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mDampingRate_0(self);
};
    PxVehicleWheelData.prototype['set_mDampingRate'] = PxVehicleWheelData.prototype.set_mDampingRate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mDampingRate_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mDampingRate', { get: PxVehicleWheelData.prototype.get_mDampingRate, set: PxVehicleWheelData.prototype.set_mDampingRate });
  PxVehicleWheelData.prototype['get_mMaxBrakeTorque'] = PxVehicleWheelData.prototype.get_mMaxBrakeTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mMaxBrakeTorque_0(self);
};
    PxVehicleWheelData.prototype['set_mMaxBrakeTorque'] = PxVehicleWheelData.prototype.set_mMaxBrakeTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mMaxBrakeTorque_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mMaxBrakeTorque', { get: PxVehicleWheelData.prototype.get_mMaxBrakeTorque, set: PxVehicleWheelData.prototype.set_mMaxBrakeTorque });
  PxVehicleWheelData.prototype['get_mMaxHandBrakeTorque'] = PxVehicleWheelData.prototype.get_mMaxHandBrakeTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mMaxHandBrakeTorque_0(self);
};
    PxVehicleWheelData.prototype['set_mMaxHandBrakeTorque'] = PxVehicleWheelData.prototype.set_mMaxHandBrakeTorque = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mMaxHandBrakeTorque_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mMaxHandBrakeTorque', { get: PxVehicleWheelData.prototype.get_mMaxHandBrakeTorque, set: PxVehicleWheelData.prototype.set_mMaxHandBrakeTorque });
  PxVehicleWheelData.prototype['get_mMaxSteer'] = PxVehicleWheelData.prototype.get_mMaxSteer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mMaxSteer_0(self);
};
    PxVehicleWheelData.prototype['set_mMaxSteer'] = PxVehicleWheelData.prototype.set_mMaxSteer = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mMaxSteer_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mMaxSteer', { get: PxVehicleWheelData.prototype.get_mMaxSteer, set: PxVehicleWheelData.prototype.set_mMaxSteer });
  PxVehicleWheelData.prototype['get_mToeAngle'] = PxVehicleWheelData.prototype.get_mToeAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelData_get_mToeAngle_0(self);
};
    PxVehicleWheelData.prototype['set_mToeAngle'] = PxVehicleWheelData.prototype.set_mToeAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelData_set_mToeAngle_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelData.prototype, 'mToeAngle', { get: PxVehicleWheelData.prototype.get_mToeAngle, set: PxVehicleWheelData.prototype.set_mToeAngle });
  PxVehicleWheelData.prototype['__destroy__'] = PxVehicleWheelData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelData___destroy___0(self);
};
// PxVehicleWheelQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheelQueryResult() {
  this.ptr = _emscripten_bind_PxVehicleWheelQueryResult_PxVehicleWheelQueryResult_0();
  getCache(PxVehicleWheelQueryResult)[this.ptr] = this;
};;
PxVehicleWheelQueryResult.prototype = Object.create(WrapperObject.prototype);
PxVehicleWheelQueryResult.prototype.constructor = PxVehicleWheelQueryResult;
PxVehicleWheelQueryResult.prototype.__class__ = PxVehicleWheelQueryResult;
PxVehicleWheelQueryResult.__cache__ = {};
Module['PxVehicleWheelQueryResult'] = PxVehicleWheelQueryResult;

  PxVehicleWheelQueryResult.prototype['get_wheelQueryResults'] = PxVehicleWheelQueryResult.prototype.get_wheelQueryResults = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelQueryResult_get_wheelQueryResults_0(self), PxWheelQueryResult);
};
    PxVehicleWheelQueryResult.prototype['set_wheelQueryResults'] = PxVehicleWheelQueryResult.prototype.set_wheelQueryResults = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelQueryResult_set_wheelQueryResults_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelQueryResult.prototype, 'wheelQueryResults', { get: PxVehicleWheelQueryResult.prototype.get_wheelQueryResults, set: PxVehicleWheelQueryResult.prototype.set_wheelQueryResults });
  PxVehicleWheelQueryResult.prototype['get_nbWheelQueryResults'] = PxVehicleWheelQueryResult.prototype.get_nbWheelQueryResults = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelQueryResult_get_nbWheelQueryResults_0(self);
};
    PxVehicleWheelQueryResult.prototype['set_nbWheelQueryResults'] = PxVehicleWheelQueryResult.prototype.set_nbWheelQueryResults = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVehicleWheelQueryResult_set_nbWheelQueryResults_1(self, arg0);
};
    Object.defineProperty(PxVehicleWheelQueryResult.prototype, 'nbWheelQueryResults', { get: PxVehicleWheelQueryResult.prototype.get_nbWheelQueryResults, set: PxVehicleWheelQueryResult.prototype.set_nbWheelQueryResults });
// PxVehicleWheelsDynData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheelsDynData() { throw "cannot construct a PxVehicleWheelsDynData, no constructor in IDL" }
PxVehicleWheelsDynData.prototype = Object.create(WrapperObject.prototype);
PxVehicleWheelsDynData.prototype.constructor = PxVehicleWheelsDynData;
PxVehicleWheelsDynData.prototype.__class__ = PxVehicleWheelsDynData;
PxVehicleWheelsDynData.__cache__ = {};
Module['PxVehicleWheelsDynData'] = PxVehicleWheelsDynData;

PxVehicleWheelsDynData.prototype['setToRestState'] = PxVehicleWheelsDynData.prototype.setToRestState = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelsDynData_setToRestState_0(self);
};;

PxVehicleWheelsDynData.prototype['setWheelRotationSpeed'] = PxVehicleWheelsDynData.prototype.setWheelRotationSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheelIdx, speed) {
  var self = this.ptr;
  if (wheelIdx && typeof wheelIdx === 'object') wheelIdx = wheelIdx.ptr;
  if (speed && typeof speed === 'object') speed = speed.ptr;
  _emscripten_bind_PxVehicleWheelsDynData_setWheelRotationSpeed_2(self, wheelIdx, speed);
};;

PxVehicleWheelsDynData.prototype['getWheelRotationSpeed'] = PxVehicleWheelsDynData.prototype.getWheelRotationSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheelIdx) {
  var self = this.ptr;
  if (wheelIdx && typeof wheelIdx === 'object') wheelIdx = wheelIdx.ptr;
  return _emscripten_bind_PxVehicleWheelsDynData_getWheelRotationSpeed_1(self, wheelIdx);
};;

PxVehicleWheelsDynData.prototype['setWheelRotationAngle'] = PxVehicleWheelsDynData.prototype.setWheelRotationAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheelIdx, angle) {
  var self = this.ptr;
  if (wheelIdx && typeof wheelIdx === 'object') wheelIdx = wheelIdx.ptr;
  if (angle && typeof angle === 'object') angle = angle.ptr;
  _emscripten_bind_PxVehicleWheelsDynData_setWheelRotationAngle_2(self, wheelIdx, angle);
};;

PxVehicleWheelsDynData.prototype['getWheelRotationAngle'] = PxVehicleWheelsDynData.prototype.getWheelRotationAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheelIdx) {
  var self = this.ptr;
  if (wheelIdx && typeof wheelIdx === 'object') wheelIdx = wheelIdx.ptr;
  return _emscripten_bind_PxVehicleWheelsDynData_getWheelRotationAngle_1(self, wheelIdx);
};;

PxVehicleWheelsDynData.prototype['copy'] = PxVehicleWheelsDynData.prototype.copy = /** @suppress {undefinedVars, duplicate} @this{Object} */function(src, srcWheel, trgWheel) {
  var self = this.ptr;
  if (src && typeof src === 'object') src = src.ptr;
  if (srcWheel && typeof srcWheel === 'object') srcWheel = srcWheel.ptr;
  if (trgWheel && typeof trgWheel === 'object') trgWheel = trgWheel.ptr;
  _emscripten_bind_PxVehicleWheelsDynData_copy_3(self, src, srcWheel, trgWheel);
};;

PxVehicleWheelsDynData.prototype['getNbWheelRotationSpeed'] = PxVehicleWheelsDynData.prototype.getNbWheelRotationSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationSpeed_0(self);
};;

PxVehicleWheelsDynData.prototype['getNbWheelRotationAngle'] = PxVehicleWheelsDynData.prototype.getNbWheelRotationAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsDynData_getNbWheelRotationAngle_0(self);
};;

  PxVehicleWheelsDynData.prototype['__destroy__'] = PxVehicleWheelsDynData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelsDynData___destroy___0(self);
};
// PxVehicleWheelsSimData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheelsSimData() { throw "cannot construct a PxVehicleWheelsSimData, no constructor in IDL" }
PxVehicleWheelsSimData.prototype = Object.create(WrapperObject.prototype);
PxVehicleWheelsSimData.prototype.constructor = PxVehicleWheelsSimData;
PxVehicleWheelsSimData.prototype.__class__ = PxVehicleWheelsSimData;
PxVehicleWheelsSimData.__cache__ = {};
Module['PxVehicleWheelsSimData'] = PxVehicleWheelsSimData;

PxVehicleWheelsSimData.prototype['allocate'] = PxVehicleWheelsSimData.prototype.allocate = /** @suppress {undefinedVars, duplicate} @this{Object} */function(nbWheels) {
  var self = this.ptr;
  if (nbWheels && typeof nbWheels === 'object') nbWheels = nbWheels.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_allocate_1(self, nbWheels), PxVehicleWheelsSimData);
};;

PxVehicleWheelsSimData.prototype['setChassisMass'] = PxVehicleWheelsSimData.prototype.setChassisMass = /** @suppress {undefinedVars, duplicate} @this{Object} */function(chassisMass) {
  var self = this.ptr;
  if (chassisMass && typeof chassisMass === 'object') chassisMass = chassisMass.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setChassisMass_1(self, chassisMass);
};;

PxVehicleWheelsSimData.prototype['free'] = PxVehicleWheelsSimData.prototype.free = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_free_0(self);
};;

PxVehicleWheelsSimData.prototype['copy'] = PxVehicleWheelsSimData.prototype.copy = /** @suppress {undefinedVars, duplicate} @this{Object} */function(src, srcWheel, trgWheel) {
  var self = this.ptr;
  if (src && typeof src === 'object') src = src.ptr;
  if (srcWheel && typeof srcWheel === 'object') srcWheel = srcWheel.ptr;
  if (trgWheel && typeof trgWheel === 'object') trgWheel = trgWheel.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_copy_3(self, src, srcWheel, trgWheel);
};;

PxVehicleWheelsSimData.prototype['getNbWheels'] = PxVehicleWheelsSimData.prototype.getNbWheels = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbWheels_0(self);
};;

PxVehicleWheelsSimData.prototype['getSuspensionData'] = PxVehicleWheelsSimData.prototype.getSuspensionData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getSuspensionData_1(self, id), PxVehicleSuspensionData);
};;

PxVehicleWheelsSimData.prototype['getWheelData'] = PxVehicleWheelsSimData.prototype.getWheelData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getWheelData_1(self, id), PxVehicleWheelData);
};;

PxVehicleWheelsSimData.prototype['getTireData'] = PxVehicleWheelsSimData.prototype.getTireData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getTireData_1(self, id), PxVehicleTireData);
};;

PxVehicleWheelsSimData.prototype['getSuspTravelDirection'] = PxVehicleWheelsSimData.prototype.getSuspTravelDirection = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getSuspTravelDirection_1(self, id), PxVec3);
};;

PxVehicleWheelsSimData.prototype['getSuspForceAppPointOffset'] = PxVehicleWheelsSimData.prototype.getSuspForceAppPointOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getSuspForceAppPointOffset_1(self, id), PxVec3);
};;

PxVehicleWheelsSimData.prototype['getTireForceAppPointOffset'] = PxVehicleWheelsSimData.prototype.getTireForceAppPointOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getTireForceAppPointOffset_1(self, id), PxVec3);
};;

PxVehicleWheelsSimData.prototype['getWheelCentreOffset'] = PxVehicleWheelsSimData.prototype.getWheelCentreOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getWheelCentreOffset_1(self, id), PxVec3);
};;

PxVehicleWheelsSimData.prototype['getWheelShapeMapping'] = PxVehicleWheelsSimData.prototype.getWheelShapeMapping = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheelId) {
  var self = this.ptr;
  if (wheelId && typeof wheelId === 'object') wheelId = wheelId.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getWheelShapeMapping_1(self, wheelId);
};;

PxVehicleWheelsSimData.prototype['getSceneQueryFilterData'] = PxVehicleWheelsSimData.prototype.getSceneQueryFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(suspId) {
  var self = this.ptr;
  if (suspId && typeof suspId === 'object') suspId = suspId.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getSceneQueryFilterData_1(self, suspId), PxFilterData);
};;

PxVehicleWheelsSimData.prototype['getNbAntiRollBars'] = PxVehicleWheelsSimData.prototype.getNbAntiRollBars = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars_0(self);
};;

PxVehicleWheelsSimData.prototype['getAntiRollBarData'] = PxVehicleWheelsSimData.prototype.getAntiRollBarData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(antiRollId) {
  var self = this.ptr;
  if (antiRollId && typeof antiRollId === 'object') antiRollId = antiRollId.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getAntiRollBarData_1(self, antiRollId), PxVehicleAntiRollBarData);
};;

PxVehicleWheelsSimData.prototype['getTireLoadFilterData'] = PxVehicleWheelsSimData.prototype.getTireLoadFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getTireLoadFilterData_0(self), PxVehicleTireLoadFilterData);
};;

PxVehicleWheelsSimData.prototype['setSuspensionData'] = PxVehicleWheelsSimData.prototype.setSuspensionData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, susp) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (susp && typeof susp === 'object') susp = susp.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setSuspensionData_2(self, id, susp);
};;

PxVehicleWheelsSimData.prototype['setWheelData'] = PxVehicleWheelsSimData.prototype.setWheelData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, wheel) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (wheel && typeof wheel === 'object') wheel = wheel.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setWheelData_2(self, id, wheel);
};;

PxVehicleWheelsSimData.prototype['setTireData'] = PxVehicleWheelsSimData.prototype.setTireData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, tire) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (tire && typeof tire === 'object') tire = tire.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setTireData_2(self, id, tire);
};;

PxVehicleWheelsSimData.prototype['setSuspTravelDirection'] = PxVehicleWheelsSimData.prototype.setSuspTravelDirection = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, dir) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (dir && typeof dir === 'object') dir = dir.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setSuspTravelDirection_2(self, id, dir);
};;

PxVehicleWheelsSimData.prototype['setSuspForceAppPointOffset'] = PxVehicleWheelsSimData.prototype.setSuspForceAppPointOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, offset) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (offset && typeof offset === 'object') offset = offset.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setSuspForceAppPointOffset_2(self, id, offset);
};;

PxVehicleWheelsSimData.prototype['setTireForceAppPointOffset'] = PxVehicleWheelsSimData.prototype.setTireForceAppPointOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, offset) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (offset && typeof offset === 'object') offset = offset.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setTireForceAppPointOffset_2(self, id, offset);
};;

PxVehicleWheelsSimData.prototype['setWheelCentreOffset'] = PxVehicleWheelsSimData.prototype.setWheelCentreOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, offset) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (offset && typeof offset === 'object') offset = offset.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setWheelCentreOffset_2(self, id, offset);
};;

PxVehicleWheelsSimData.prototype['setWheelShapeMapping'] = PxVehicleWheelsSimData.prototype.setWheelShapeMapping = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheelId, shapeId) {
  var self = this.ptr;
  if (wheelId && typeof wheelId === 'object') wheelId = wheelId.ptr;
  if (shapeId && typeof shapeId === 'object') shapeId = shapeId.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setWheelShapeMapping_2(self, wheelId, shapeId);
};;

PxVehicleWheelsSimData.prototype['setSceneQueryFilterData'] = PxVehicleWheelsSimData.prototype.setSceneQueryFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(suspId, sqFilterData) {
  var self = this.ptr;
  if (suspId && typeof suspId === 'object') suspId = suspId.ptr;
  if (sqFilterData && typeof sqFilterData === 'object') sqFilterData = sqFilterData.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setSceneQueryFilterData_2(self, suspId, sqFilterData);
};;

PxVehicleWheelsSimData.prototype['setTireLoadFilterData'] = PxVehicleWheelsSimData.prototype.setTireLoadFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(tireLoadFilter) {
  var self = this.ptr;
  if (tireLoadFilter && typeof tireLoadFilter === 'object') tireLoadFilter = tireLoadFilter.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setTireLoadFilterData_1(self, tireLoadFilter);
};;

PxVehicleWheelsSimData.prototype['addAntiRollBarData'] = PxVehicleWheelsSimData.prototype.addAntiRollBarData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(antiRoll) {
  var self = this.ptr;
  if (antiRoll && typeof antiRoll === 'object') antiRoll = antiRoll.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_addAntiRollBarData_1(self, antiRoll);
};;

PxVehicleWheelsSimData.prototype['disableWheel'] = PxVehicleWheelsSimData.prototype.disableWheel = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheel) {
  var self = this.ptr;
  if (wheel && typeof wheel === 'object') wheel = wheel.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_disableWheel_1(self, wheel);
};;

PxVehicleWheelsSimData.prototype['enableWheel'] = PxVehicleWheelsSimData.prototype.enableWheel = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheel) {
  var self = this.ptr;
  if (wheel && typeof wheel === 'object') wheel = wheel.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_enableWheel_1(self, wheel);
};;

PxVehicleWheelsSimData.prototype['getIsWheelDisabled'] = PxVehicleWheelsSimData.prototype.getIsWheelDisabled = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheel) {
  var self = this.ptr;
  if (wheel && typeof wheel === 'object') wheel = wheel.ptr;
  return !!(_emscripten_bind_PxVehicleWheelsSimData_getIsWheelDisabled_1(self, wheel));
};;

PxVehicleWheelsSimData.prototype['setSubStepCount'] = PxVehicleWheelsSimData.prototype.setSubStepCount = /** @suppress {undefinedVars, duplicate} @this{Object} */function(thresholdLongitudinalSpeed, lowForwardSpeedSubStepCount, highForwardSpeedSubStepCount) {
  var self = this.ptr;
  if (thresholdLongitudinalSpeed && typeof thresholdLongitudinalSpeed === 'object') thresholdLongitudinalSpeed = thresholdLongitudinalSpeed.ptr;
  if (lowForwardSpeedSubStepCount && typeof lowForwardSpeedSubStepCount === 'object') lowForwardSpeedSubStepCount = lowForwardSpeedSubStepCount.ptr;
  if (highForwardSpeedSubStepCount && typeof highForwardSpeedSubStepCount === 'object') highForwardSpeedSubStepCount = highForwardSpeedSubStepCount.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setSubStepCount_3(self, thresholdLongitudinalSpeed, lowForwardSpeedSubStepCount, highForwardSpeedSubStepCount);
};;

PxVehicleWheelsSimData.prototype['setMinLongSlipDenominator'] = PxVehicleWheelsSimData.prototype.setMinLongSlipDenominator = /** @suppress {undefinedVars, duplicate} @this{Object} */function(minLongSlipDenominator) {
  var self = this.ptr;
  if (minLongSlipDenominator && typeof minLongSlipDenominator === 'object') minLongSlipDenominator = minLongSlipDenominator.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setMinLongSlipDenominator_1(self, minLongSlipDenominator);
};;

PxVehicleWheelsSimData.prototype['setFlags'] = PxVehicleWheelsSimData.prototype.setFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setFlags_1(self, flags);
};;

PxVehicleWheelsSimData.prototype['getFlags'] = PxVehicleWheelsSimData.prototype.getFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxVehicleWheelsSimData_getFlags_0(self), PxVehicleWheelsSimFlags);
};;

PxVehicleWheelsSimData.prototype['getNbWheels4'] = PxVehicleWheelsSimData.prototype.getNbWheels4 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbWheels4_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbSuspensionData'] = PxVehicleWheelsSimData.prototype.getNbSuspensionData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbSuspensionData_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbWheelData'] = PxVehicleWheelsSimData.prototype.getNbWheelData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbWheelData_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbSuspTravelDirection'] = PxVehicleWheelsSimData.prototype.getNbSuspTravelDirection = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbSuspTravelDirection_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbTireData'] = PxVehicleWheelsSimData.prototype.getNbTireData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbTireData_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbSuspForceAppPointOffset'] = PxVehicleWheelsSimData.prototype.getNbSuspForceAppPointOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbSuspForceAppPointOffset_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbTireForceAppPointOffset'] = PxVehicleWheelsSimData.prototype.getNbTireForceAppPointOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbTireForceAppPointOffset_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbWheelCentreOffset'] = PxVehicleWheelsSimData.prototype.getNbWheelCentreOffset = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbWheelCentreOffset_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbWheelShapeMapping'] = PxVehicleWheelsSimData.prototype.getNbWheelShapeMapping = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbWheelShapeMapping_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbSceneQueryFilterData'] = PxVehicleWheelsSimData.prototype.getNbSceneQueryFilterData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbSceneQueryFilterData_0(self);
};;

PxVehicleWheelsSimData.prototype['getMinLongSlipDenominator'] = PxVehicleWheelsSimData.prototype.getMinLongSlipDenominator = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getMinLongSlipDenominator_0(self);
};;

PxVehicleWheelsSimData.prototype['setThresholdLongSpeed'] = PxVehicleWheelsSimData.prototype.setThresholdLongSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function(f) {
  var self = this.ptr;
  if (f && typeof f === 'object') f = f.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setThresholdLongSpeed_1(self, f);
};;

PxVehicleWheelsSimData.prototype['getThresholdLongSpeed'] = PxVehicleWheelsSimData.prototype.getThresholdLongSpeed = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getThresholdLongSpeed_0(self);
};;

PxVehicleWheelsSimData.prototype['setLowForwardSpeedSubStepCount'] = PxVehicleWheelsSimData.prototype.setLowForwardSpeedSubStepCount = /** @suppress {undefinedVars, duplicate} @this{Object} */function(f) {
  var self = this.ptr;
  if (f && typeof f === 'object') f = f.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setLowForwardSpeedSubStepCount_1(self, f);
};;

PxVehicleWheelsSimData.prototype['getLowForwardSpeedSubStepCount'] = PxVehicleWheelsSimData.prototype.getLowForwardSpeedSubStepCount = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getLowForwardSpeedSubStepCount_0(self);
};;

PxVehicleWheelsSimData.prototype['setHighForwardSpeedSubStepCount'] = PxVehicleWheelsSimData.prototype.setHighForwardSpeedSubStepCount = /** @suppress {undefinedVars, duplicate} @this{Object} */function(f) {
  var self = this.ptr;
  if (f && typeof f === 'object') f = f.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setHighForwardSpeedSubStepCount_1(self, f);
};;

PxVehicleWheelsSimData.prototype['getHighForwardSpeedSubStepCount'] = PxVehicleWheelsSimData.prototype.getHighForwardSpeedSubStepCount = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getHighForwardSpeedSubStepCount_0(self);
};;

PxVehicleWheelsSimData.prototype['setWheelEnabledState'] = PxVehicleWheelsSimData.prototype.setWheelEnabledState = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheel, state) {
  var self = this.ptr;
  if (wheel && typeof wheel === 'object') wheel = wheel.ptr;
  if (state && typeof state === 'object') state = state.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setWheelEnabledState_2(self, wheel, state);
};;

PxVehicleWheelsSimData.prototype['getWheelEnabledState'] = PxVehicleWheelsSimData.prototype.getWheelEnabledState = /** @suppress {undefinedVars, duplicate} @this{Object} */function(wheel) {
  var self = this.ptr;
  if (wheel && typeof wheel === 'object') wheel = wheel.ptr;
  return !!(_emscripten_bind_PxVehicleWheelsSimData_getWheelEnabledState_1(self, wheel));
};;

PxVehicleWheelsSimData.prototype['getNbWheelEnabledState'] = PxVehicleWheelsSimData.prototype.getNbWheelEnabledState = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbWheelEnabledState_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbAntiRollBars4'] = PxVehicleWheelsSimData.prototype.getNbAntiRollBars4 = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBars4_0(self);
};;

PxVehicleWheelsSimData.prototype['getNbAntiRollBarData'] = PxVehicleWheelsSimData.prototype.getNbAntiRollBarData = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVehicleWheelsSimData_getNbAntiRollBarData_0(self);
};;

PxVehicleWheelsSimData.prototype['setAntiRollBarData'] = PxVehicleWheelsSimData.prototype.setAntiRollBarData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(id, antiRoll) {
  var self = this.ptr;
  if (id && typeof id === 'object') id = id.ptr;
  if (antiRoll && typeof antiRoll === 'object') antiRoll = antiRoll.ptr;
  _emscripten_bind_PxVehicleWheelsSimData_setAntiRollBarData_2(self, id, antiRoll);
};;

  PxVehicleWheelsSimData.prototype['__destroy__'] = PxVehicleWheelsSimData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelsSimData___destroy___0(self);
};
// PxVehicleWheelsSimFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheelsSimFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxVehicleWheelsSimFlags_PxVehicleWheelsSimFlags_1(flags);
  getCache(PxVehicleWheelsSimFlags)[this.ptr] = this;
};;
PxVehicleWheelsSimFlags.prototype = Object.create(WrapperObject.prototype);
PxVehicleWheelsSimFlags.prototype.constructor = PxVehicleWheelsSimFlags;
PxVehicleWheelsSimFlags.prototype.__class__ = PxVehicleWheelsSimFlags;
PxVehicleWheelsSimFlags.__cache__ = {};
Module['PxVehicleWheelsSimFlags'] = PxVehicleWheelsSimFlags;

PxVehicleWheelsSimFlags.prototype['isSet'] = PxVehicleWheelsSimFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxVehicleWheelsSimFlags_isSet_1(self, flag));
};;

PxVehicleWheelsSimFlags.prototype['set'] = PxVehicleWheelsSimFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxVehicleWheelsSimFlags_set_1(self, flag);
};;

PxVehicleWheelsSimFlags.prototype['clear'] = PxVehicleWheelsSimFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxVehicleWheelsSimFlags_clear_1(self, flag);
};;

  PxVehicleWheelsSimFlags.prototype['__destroy__'] = PxVehicleWheelsSimFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelsSimFlags___destroy___0(self);
};
// PxWheelQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxWheelQueryResult() {
  this.ptr = _emscripten_bind_PxWheelQueryResult_PxWheelQueryResult_0();
  getCache(PxWheelQueryResult)[this.ptr] = this;
};;
PxWheelQueryResult.prototype = Object.create(WrapperObject.prototype);
PxWheelQueryResult.prototype.constructor = PxWheelQueryResult;
PxWheelQueryResult.prototype.__class__ = PxWheelQueryResult;
PxWheelQueryResult.__cache__ = {};
Module['PxWheelQueryResult'] = PxWheelQueryResult;

  PxWheelQueryResult.prototype['get_suspLineStart'] = PxWheelQueryResult.prototype.get_suspLineStart = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_suspLineStart_0(self), PxVec3);
};
    PxWheelQueryResult.prototype['set_suspLineStart'] = PxWheelQueryResult.prototype.set_suspLineStart = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_suspLineStart_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'suspLineStart', { get: PxWheelQueryResult.prototype.get_suspLineStart, set: PxWheelQueryResult.prototype.set_suspLineStart });
  PxWheelQueryResult.prototype['get_suspLineDir'] = PxWheelQueryResult.prototype.get_suspLineDir = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_suspLineDir_0(self), PxVec3);
};
    PxWheelQueryResult.prototype['set_suspLineDir'] = PxWheelQueryResult.prototype.set_suspLineDir = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_suspLineDir_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'suspLineDir', { get: PxWheelQueryResult.prototype.get_suspLineDir, set: PxWheelQueryResult.prototype.set_suspLineDir });
  PxWheelQueryResult.prototype['get_suspLineLength'] = PxWheelQueryResult.prototype.get_suspLineLength = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_suspLineLength_0(self);
};
    PxWheelQueryResult.prototype['set_suspLineLength'] = PxWheelQueryResult.prototype.set_suspLineLength = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_suspLineLength_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'suspLineLength', { get: PxWheelQueryResult.prototype.get_suspLineLength, set: PxWheelQueryResult.prototype.set_suspLineLength });
  PxWheelQueryResult.prototype['get_isInAir'] = PxWheelQueryResult.prototype.get_isInAir = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxWheelQueryResult_get_isInAir_0(self));
};
    PxWheelQueryResult.prototype['set_isInAir'] = PxWheelQueryResult.prototype.set_isInAir = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_isInAir_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'isInAir', { get: PxWheelQueryResult.prototype.get_isInAir, set: PxWheelQueryResult.prototype.set_isInAir });
  PxWheelQueryResult.prototype['get_tireContactActor'] = PxWheelQueryResult.prototype.get_tireContactActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireContactActor_0(self), PxActor);
};
    PxWheelQueryResult.prototype['set_tireContactActor'] = PxWheelQueryResult.prototype.set_tireContactActor = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireContactActor_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireContactActor', { get: PxWheelQueryResult.prototype.get_tireContactActor, set: PxWheelQueryResult.prototype.set_tireContactActor });
  PxWheelQueryResult.prototype['get_tireContactShape'] = PxWheelQueryResult.prototype.get_tireContactShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireContactShape_0(self), PxShape);
};
    PxWheelQueryResult.prototype['set_tireContactShape'] = PxWheelQueryResult.prototype.set_tireContactShape = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireContactShape_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireContactShape', { get: PxWheelQueryResult.prototype.get_tireContactShape, set: PxWheelQueryResult.prototype.set_tireContactShape });
  PxWheelQueryResult.prototype['get_tireSurfaceMaterial'] = PxWheelQueryResult.prototype.get_tireSurfaceMaterial = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireSurfaceMaterial_0(self), PxMaterial);
};
    PxWheelQueryResult.prototype['set_tireSurfaceMaterial'] = PxWheelQueryResult.prototype.set_tireSurfaceMaterial = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireSurfaceMaterial_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireSurfaceMaterial', { get: PxWheelQueryResult.prototype.get_tireSurfaceMaterial, set: PxWheelQueryResult.prototype.set_tireSurfaceMaterial });
  PxWheelQueryResult.prototype['get_tireSurfaceType'] = PxWheelQueryResult.prototype.get_tireSurfaceType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_tireSurfaceType_0(self);
};
    PxWheelQueryResult.prototype['set_tireSurfaceType'] = PxWheelQueryResult.prototype.set_tireSurfaceType = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireSurfaceType_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireSurfaceType', { get: PxWheelQueryResult.prototype.get_tireSurfaceType, set: PxWheelQueryResult.prototype.set_tireSurfaceType });
  PxWheelQueryResult.prototype['get_tireContactPoint'] = PxWheelQueryResult.prototype.get_tireContactPoint = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireContactPoint_0(self), PxVec3);
};
    PxWheelQueryResult.prototype['set_tireContactPoint'] = PxWheelQueryResult.prototype.set_tireContactPoint = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireContactPoint_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireContactPoint', { get: PxWheelQueryResult.prototype.get_tireContactPoint, set: PxWheelQueryResult.prototype.set_tireContactPoint });
  PxWheelQueryResult.prototype['get_tireContactNormal'] = PxWheelQueryResult.prototype.get_tireContactNormal = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireContactNormal_0(self), PxVec3);
};
    PxWheelQueryResult.prototype['set_tireContactNormal'] = PxWheelQueryResult.prototype.set_tireContactNormal = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireContactNormal_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireContactNormal', { get: PxWheelQueryResult.prototype.get_tireContactNormal, set: PxWheelQueryResult.prototype.set_tireContactNormal });
  PxWheelQueryResult.prototype['get_tireFriction'] = PxWheelQueryResult.prototype.get_tireFriction = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_tireFriction_0(self);
};
    PxWheelQueryResult.prototype['set_tireFriction'] = PxWheelQueryResult.prototype.set_tireFriction = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireFriction_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireFriction', { get: PxWheelQueryResult.prototype.get_tireFriction, set: PxWheelQueryResult.prototype.set_tireFriction });
  PxWheelQueryResult.prototype['get_suspJounce'] = PxWheelQueryResult.prototype.get_suspJounce = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_suspJounce_0(self);
};
    PxWheelQueryResult.prototype['set_suspJounce'] = PxWheelQueryResult.prototype.set_suspJounce = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_suspJounce_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'suspJounce', { get: PxWheelQueryResult.prototype.get_suspJounce, set: PxWheelQueryResult.prototype.set_suspJounce });
  PxWheelQueryResult.prototype['get_suspSpringForce'] = PxWheelQueryResult.prototype.get_suspSpringForce = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_suspSpringForce_0(self);
};
    PxWheelQueryResult.prototype['set_suspSpringForce'] = PxWheelQueryResult.prototype.set_suspSpringForce = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_suspSpringForce_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'suspSpringForce', { get: PxWheelQueryResult.prototype.get_suspSpringForce, set: PxWheelQueryResult.prototype.set_suspSpringForce });
  PxWheelQueryResult.prototype['get_tireLongitudinalDir'] = PxWheelQueryResult.prototype.get_tireLongitudinalDir = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireLongitudinalDir_0(self), PxVec3);
};
    PxWheelQueryResult.prototype['set_tireLongitudinalDir'] = PxWheelQueryResult.prototype.set_tireLongitudinalDir = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireLongitudinalDir_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireLongitudinalDir', { get: PxWheelQueryResult.prototype.get_tireLongitudinalDir, set: PxWheelQueryResult.prototype.set_tireLongitudinalDir });
  PxWheelQueryResult.prototype['get_tireLateralDir'] = PxWheelQueryResult.prototype.get_tireLateralDir = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_tireLateralDir_0(self), PxVec3);
};
    PxWheelQueryResult.prototype['set_tireLateralDir'] = PxWheelQueryResult.prototype.set_tireLateralDir = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_tireLateralDir_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'tireLateralDir', { get: PxWheelQueryResult.prototype.get_tireLateralDir, set: PxWheelQueryResult.prototype.set_tireLateralDir });
  PxWheelQueryResult.prototype['get_longitudinalSlip'] = PxWheelQueryResult.prototype.get_longitudinalSlip = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_longitudinalSlip_0(self);
};
    PxWheelQueryResult.prototype['set_longitudinalSlip'] = PxWheelQueryResult.prototype.set_longitudinalSlip = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_longitudinalSlip_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'longitudinalSlip', { get: PxWheelQueryResult.prototype.get_longitudinalSlip, set: PxWheelQueryResult.prototype.set_longitudinalSlip });
  PxWheelQueryResult.prototype['get_lateralSlip'] = PxWheelQueryResult.prototype.get_lateralSlip = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_lateralSlip_0(self);
};
    PxWheelQueryResult.prototype['set_lateralSlip'] = PxWheelQueryResult.prototype.set_lateralSlip = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_lateralSlip_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'lateralSlip', { get: PxWheelQueryResult.prototype.get_lateralSlip, set: PxWheelQueryResult.prototype.set_lateralSlip });
  PxWheelQueryResult.prototype['get_steerAngle'] = PxWheelQueryResult.prototype.get_steerAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxWheelQueryResult_get_steerAngle_0(self);
};
    PxWheelQueryResult.prototype['set_steerAngle'] = PxWheelQueryResult.prototype.set_steerAngle = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_steerAngle_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'steerAngle', { get: PxWheelQueryResult.prototype.get_steerAngle, set: PxWheelQueryResult.prototype.set_steerAngle });
  PxWheelQueryResult.prototype['get_localPose'] = PxWheelQueryResult.prototype.get_localPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxWheelQueryResult_get_localPose_0(self), PxTransform);
};
    PxWheelQueryResult.prototype['set_localPose'] = PxWheelQueryResult.prototype.set_localPose = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxWheelQueryResult_set_localPose_1(self, arg0);
};
    Object.defineProperty(PxWheelQueryResult.prototype, 'localPose', { get: PxWheelQueryResult.prototype.get_localPose, set: PxWheelQueryResult.prototype.set_localPose });
  PxWheelQueryResult.prototype['__destroy__'] = PxWheelQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxWheelQueryResult___destroy___0(self);
};
// PxBoxGeometry
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBoxGeometry(hx, hy, hz) {
  if (hx && typeof hx === 'object') hx = hx.ptr;
  if (hy && typeof hy === 'object') hy = hy.ptr;
  if (hz && typeof hz === 'object') hz = hz.ptr;
  this.ptr = _emscripten_bind_PxBoxGeometry_PxBoxGeometry_3(hx, hy, hz);
  getCache(PxBoxGeometry)[this.ptr] = this;
};;
PxBoxGeometry.prototype = Object.create(PxGeometry.prototype);
PxBoxGeometry.prototype.constructor = PxBoxGeometry;
PxBoxGeometry.prototype.__class__ = PxBoxGeometry;
PxBoxGeometry.__cache__ = {};
Module['PxBoxGeometry'] = PxBoxGeometry;

  PxBoxGeometry.prototype['__destroy__'] = PxBoxGeometry.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBoxGeometry___destroy___0(self);
};
// PxBVHStructure
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBVHStructure() { throw "cannot construct a PxBVHStructure, no constructor in IDL" }
PxBVHStructure.prototype = Object.create(PxBase.prototype);
PxBVHStructure.prototype.constructor = PxBVHStructure;
PxBVHStructure.prototype.__class__ = PxBVHStructure;
PxBVHStructure.__cache__ = {};
Module['PxBVHStructure'] = PxBVHStructure;

PxBVHStructure.prototype['release'] = PxBVHStructure.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBVHStructure_release_0(self);
};;

PxBVHStructure.prototype['getConcreteTypeName'] = PxBVHStructure.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxBVHStructure_getConcreteTypeName_0(self));
};;

PxBVHStructure.prototype['getConcreteType'] = PxBVHStructure.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBVHStructure_getConcreteType_0(self);
};;

PxBVHStructure.prototype['setBaseFlag'] = PxBVHStructure.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxBVHStructure_setBaseFlag_2(self, flag, value);
};;

PxBVHStructure.prototype['setBaseFlags'] = PxBVHStructure.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxBVHStructure_setBaseFlags_1(self, inFlags);
};;

PxBVHStructure.prototype['getBaseFlags'] = PxBVHStructure.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBVHStructure_getBaseFlags_0(self), PxBaseFlags);
};;

PxBVHStructure.prototype['isReleasable'] = PxBVHStructure.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxBVHStructure_isReleasable_0(self));
};;

// PxCapsuleGeometry
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxCapsuleGeometry(radius, halfHeight) {
  if (radius && typeof radius === 'object') radius = radius.ptr;
  if (halfHeight && typeof halfHeight === 'object') halfHeight = halfHeight.ptr;
  this.ptr = _emscripten_bind_PxCapsuleGeometry_PxCapsuleGeometry_2(radius, halfHeight);
  getCache(PxCapsuleGeometry)[this.ptr] = this;
};;
PxCapsuleGeometry.prototype = Object.create(PxGeometry.prototype);
PxCapsuleGeometry.prototype.constructor = PxCapsuleGeometry;
PxCapsuleGeometry.prototype.__class__ = PxCapsuleGeometry;
PxCapsuleGeometry.__cache__ = {};
Module['PxCapsuleGeometry'] = PxCapsuleGeometry;

  PxCapsuleGeometry.prototype['__destroy__'] = PxCapsuleGeometry.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxCapsuleGeometry___destroy___0(self);
};
// PxConvexMesh
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxConvexMesh() { throw "cannot construct a PxConvexMesh, no constructor in IDL" }
PxConvexMesh.prototype = Object.create(PxBase.prototype);
PxConvexMesh.prototype.constructor = PxConvexMesh;
PxConvexMesh.prototype.__class__ = PxConvexMesh;
PxConvexMesh.__cache__ = {};
Module['PxConvexMesh'] = PxConvexMesh;

PxConvexMesh.prototype['getNbVertices'] = PxConvexMesh.prototype.getNbVertices = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxConvexMesh_getNbVertices_0(self);
};;

PxConvexMesh.prototype['getVertices'] = PxConvexMesh.prototype.getVertices = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxConvexMesh_getVertices_0(self), PxVec3);
};;

PxConvexMesh.prototype['getIndexBuffer'] = PxConvexMesh.prototype.getIndexBuffer = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxConvexMesh_getIndexBuffer_0(self), PxU8Ptr);
};;

PxConvexMesh.prototype['getNbPolygons'] = PxConvexMesh.prototype.getNbPolygons = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxConvexMesh_getNbPolygons_0(self);
};;

PxConvexMesh.prototype['getPolygonData'] = PxConvexMesh.prototype.getPolygonData = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index, data) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  if (data && typeof data === 'object') data = data.ptr;
  return !!(_emscripten_bind_PxConvexMesh_getPolygonData_2(self, index, data));
};;

PxConvexMesh.prototype['getReferenceCount'] = PxConvexMesh.prototype.getReferenceCount = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxConvexMesh_getReferenceCount_0(self);
};;

PxConvexMesh.prototype['acquireReference'] = PxConvexMesh.prototype.acquireReference = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxConvexMesh_acquireReference_0(self);
};;

PxConvexMesh.prototype['getLocalBounds'] = PxConvexMesh.prototype.getLocalBounds = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxConvexMesh_getLocalBounds_0(self), PxBounds3);
};;

PxConvexMesh.prototype['isGpuCompatible'] = PxConvexMesh.prototype.isGpuCompatible = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxConvexMesh_isGpuCompatible_0(self));
};;

PxConvexMesh.prototype['release'] = PxConvexMesh.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxConvexMesh_release_0(self);
};;

PxConvexMesh.prototype['getConcreteTypeName'] = PxConvexMesh.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxConvexMesh_getConcreteTypeName_0(self));
};;

PxConvexMesh.prototype['getConcreteType'] = PxConvexMesh.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxConvexMesh_getConcreteType_0(self);
};;

PxConvexMesh.prototype['setBaseFlag'] = PxConvexMesh.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxConvexMesh_setBaseFlag_2(self, flag, value);
};;

PxConvexMesh.prototype['setBaseFlags'] = PxConvexMesh.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxConvexMesh_setBaseFlags_1(self, inFlags);
};;

PxConvexMesh.prototype['getBaseFlags'] = PxConvexMesh.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxConvexMesh_getBaseFlags_0(self), PxBaseFlags);
};;

PxConvexMesh.prototype['isReleasable'] = PxConvexMesh.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxConvexMesh_isReleasable_0(self));
};;

// PxConvexMeshGeometry
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxConvexMeshGeometry(mesh, scaling, flags) {
  if (mesh && typeof mesh === 'object') mesh = mesh.ptr;
  if (scaling && typeof scaling === 'object') scaling = scaling.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  if (scaling === undefined) { this.ptr = _emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_1(mesh); getCache(PxConvexMeshGeometry)[this.ptr] = this;return }
  if (flags === undefined) { this.ptr = _emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_2(mesh, scaling); getCache(PxConvexMeshGeometry)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxConvexMeshGeometry_PxConvexMeshGeometry_3(mesh, scaling, flags);
  getCache(PxConvexMeshGeometry)[this.ptr] = this;
};;
PxConvexMeshGeometry.prototype = Object.create(PxGeometry.prototype);
PxConvexMeshGeometry.prototype.constructor = PxConvexMeshGeometry;
PxConvexMeshGeometry.prototype.__class__ = PxConvexMeshGeometry;
PxConvexMeshGeometry.__cache__ = {};
Module['PxConvexMeshGeometry'] = PxConvexMeshGeometry;

  PxConvexMeshGeometry.prototype['__destroy__'] = PxConvexMeshGeometry.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxConvexMeshGeometry___destroy___0(self);
};
// PxConvexMeshGeometryFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxConvexMeshGeometryFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxConvexMeshGeometryFlags_PxConvexMeshGeometryFlags_1(flags);
  getCache(PxConvexMeshGeometryFlags)[this.ptr] = this;
};;
PxConvexMeshGeometryFlags.prototype = Object.create(WrapperObject.prototype);
PxConvexMeshGeometryFlags.prototype.constructor = PxConvexMeshGeometryFlags;
PxConvexMeshGeometryFlags.prototype.__class__ = PxConvexMeshGeometryFlags;
PxConvexMeshGeometryFlags.__cache__ = {};
Module['PxConvexMeshGeometryFlags'] = PxConvexMeshGeometryFlags;

PxConvexMeshGeometryFlags.prototype['isSet'] = PxConvexMeshGeometryFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxConvexMeshGeometryFlags_isSet_1(self, flag));
};;

PxConvexMeshGeometryFlags.prototype['set'] = PxConvexMeshGeometryFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxConvexMeshGeometryFlags_set_1(self, flag);
};;

PxConvexMeshGeometryFlags.prototype['clear'] = PxConvexMeshGeometryFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxConvexMeshGeometryFlags_clear_1(self, flag);
};;

  PxConvexMeshGeometryFlags.prototype['__destroy__'] = PxConvexMeshGeometryFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxConvexMeshGeometryFlags___destroy___0(self);
};
// PxHullPolygon
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxHullPolygon() {
  this.ptr = _emscripten_bind_PxHullPolygon_PxHullPolygon_0();
  getCache(PxHullPolygon)[this.ptr] = this;
};;
PxHullPolygon.prototype = Object.create(WrapperObject.prototype);
PxHullPolygon.prototype.constructor = PxHullPolygon;
PxHullPolygon.prototype.__class__ = PxHullPolygon;
PxHullPolygon.__cache__ = {};
Module['PxHullPolygon'] = PxHullPolygon;

  PxHullPolygon.prototype['get_mPlane'] = PxHullPolygon.prototype.get_mPlane = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  return _emscripten_bind_PxHullPolygon_get_mPlane_1(self, arg0);
};
    PxHullPolygon.prototype['set_mPlane'] = PxHullPolygon.prototype.set_mPlane = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0, arg1) {
  var self = this.ptr;
  ensureCache.prepare();
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  if (arg1 && typeof arg1 === 'object') arg1 = arg1.ptr;
  _emscripten_bind_PxHullPolygon_set_mPlane_2(self, arg0, arg1);
};
    Object.defineProperty(PxHullPolygon.prototype, 'mPlane', { get: PxHullPolygon.prototype.get_mPlane, set: PxHullPolygon.prototype.set_mPlane });
  PxHullPolygon.prototype['get_mNbVerts'] = PxHullPolygon.prototype.get_mNbVerts = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxHullPolygon_get_mNbVerts_0(self);
};
    PxHullPolygon.prototype['set_mNbVerts'] = PxHullPolygon.prototype.set_mNbVerts = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxHullPolygon_set_mNbVerts_1(self, arg0);
};
    Object.defineProperty(PxHullPolygon.prototype, 'mNbVerts', { get: PxHullPolygon.prototype.get_mNbVerts, set: PxHullPolygon.prototype.set_mNbVerts });
  PxHullPolygon.prototype['get_mIndexBase'] = PxHullPolygon.prototype.get_mIndexBase = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxHullPolygon_get_mIndexBase_0(self);
};
    PxHullPolygon.prototype['set_mIndexBase'] = PxHullPolygon.prototype.set_mIndexBase = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxHullPolygon_set_mIndexBase_1(self, arg0);
};
    Object.defineProperty(PxHullPolygon.prototype, 'mIndexBase', { get: PxHullPolygon.prototype.get_mIndexBase, set: PxHullPolygon.prototype.set_mIndexBase });
  PxHullPolygon.prototype['__destroy__'] = PxHullPolygon.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxHullPolygon___destroy___0(self);
};
// PxMeshScale
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxMeshScale(s, r) {
  if (s && typeof s === 'object') s = s.ptr;
  if (r && typeof r === 'object') r = r.ptr;
  if (s === undefined) { this.ptr = _emscripten_bind_PxMeshScale_PxMeshScale_0(); getCache(PxMeshScale)[this.ptr] = this;return }
  if (r === undefined) { this.ptr = _emscripten_bind_PxMeshScale_PxMeshScale_1(s); getCache(PxMeshScale)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxMeshScale_PxMeshScale_2(s, r);
  getCache(PxMeshScale)[this.ptr] = this;
};;
PxMeshScale.prototype = Object.create(WrapperObject.prototype);
PxMeshScale.prototype.constructor = PxMeshScale;
PxMeshScale.prototype.__class__ = PxMeshScale;
PxMeshScale.__cache__ = {};
Module['PxMeshScale'] = PxMeshScale;

  PxMeshScale.prototype['__destroy__'] = PxMeshScale.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxMeshScale___destroy___0(self);
};
// PxPlaneGeometry
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxPlaneGeometry() {
  this.ptr = _emscripten_bind_PxPlaneGeometry_PxPlaneGeometry_0();
  getCache(PxPlaneGeometry)[this.ptr] = this;
};;
PxPlaneGeometry.prototype = Object.create(PxGeometry.prototype);
PxPlaneGeometry.prototype.constructor = PxPlaneGeometry;
PxPlaneGeometry.prototype.__class__ = PxPlaneGeometry;
PxPlaneGeometry.__cache__ = {};
Module['PxPlaneGeometry'] = PxPlaneGeometry;

  PxPlaneGeometry.prototype['__destroy__'] = PxPlaneGeometry.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxPlaneGeometry___destroy___0(self);
};
// PxSphereGeometry
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxSphereGeometry(ir) {
  if (ir && typeof ir === 'object') ir = ir.ptr;
  this.ptr = _emscripten_bind_PxSphereGeometry_PxSphereGeometry_1(ir);
  getCache(PxSphereGeometry)[this.ptr] = this;
};;
PxSphereGeometry.prototype = Object.create(PxGeometry.prototype);
PxSphereGeometry.prototype.constructor = PxSphereGeometry;
PxSphereGeometry.prototype.__class__ = PxSphereGeometry;
PxSphereGeometry.__cache__ = {};
Module['PxSphereGeometry'] = PxSphereGeometry;

  PxSphereGeometry.prototype['__destroy__'] = PxSphereGeometry.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxSphereGeometry___destroy___0(self);
};
// PxConvexFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxConvexFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxConvexFlags_PxConvexFlags_1(flags);
  getCache(PxConvexFlags)[this.ptr] = this;
};;
PxConvexFlags.prototype = Object.create(WrapperObject.prototype);
PxConvexFlags.prototype.constructor = PxConvexFlags;
PxConvexFlags.prototype.__class__ = PxConvexFlags;
PxConvexFlags.__cache__ = {};
Module['PxConvexFlags'] = PxConvexFlags;

PxConvexFlags.prototype['isSet'] = PxConvexFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxConvexFlags_isSet_1(self, flag));
};;

PxConvexFlags.prototype['set'] = PxConvexFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxConvexFlags_set_1(self, flag);
};;

PxConvexFlags.prototype['clear'] = PxConvexFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxConvexFlags_clear_1(self, flag);
};;

  PxConvexFlags.prototype['__destroy__'] = PxConvexFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxConvexFlags___destroy___0(self);
};
// PxConvexMeshDesc
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxConvexMeshDesc() {
  this.ptr = _emscripten_bind_PxConvexMeshDesc_PxConvexMeshDesc_0();
  getCache(PxConvexMeshDesc)[this.ptr] = this;
};;
PxConvexMeshDesc.prototype = Object.create(WrapperObject.prototype);
PxConvexMeshDesc.prototype.constructor = PxConvexMeshDesc;
PxConvexMeshDesc.prototype.__class__ = PxConvexMeshDesc;
PxConvexMeshDesc.__cache__ = {};
Module['PxConvexMeshDesc'] = PxConvexMeshDesc;

  PxConvexMeshDesc.prototype['get_points'] = PxConvexMeshDesc.prototype.get_points = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxConvexMeshDesc_get_points_0(self), PxBoundedData);
};
    PxConvexMeshDesc.prototype['set_points'] = PxConvexMeshDesc.prototype.set_points = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxConvexMeshDesc_set_points_1(self, arg0);
};
    Object.defineProperty(PxConvexMeshDesc.prototype, 'points', { get: PxConvexMeshDesc.prototype.get_points, set: PxConvexMeshDesc.prototype.set_points });
  PxConvexMeshDesc.prototype['get_flags'] = PxConvexMeshDesc.prototype.get_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxConvexMeshDesc_get_flags_0(self), PxConvexFlags);
};
    PxConvexMeshDesc.prototype['set_flags'] = PxConvexMeshDesc.prototype.set_flags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxConvexMeshDesc_set_flags_1(self, arg0);
};
    Object.defineProperty(PxConvexMeshDesc.prototype, 'flags', { get: PxConvexMeshDesc.prototype.get_flags, set: PxConvexMeshDesc.prototype.set_flags });
  PxConvexMeshDesc.prototype['__destroy__'] = PxConvexMeshDesc.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxConvexMeshDesc___destroy___0(self);
};
// PxCooking
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxCooking() { throw "cannot construct a PxCooking, no constructor in IDL" }
PxCooking.prototype = Object.create(WrapperObject.prototype);
PxCooking.prototype.constructor = PxCooking;
PxCooking.prototype.__class__ = PxCooking;
PxCooking.__cache__ = {};
Module['PxCooking'] = PxCooking;

PxCooking.prototype['createConvexMesh'] = PxCooking.prototype.createConvexMesh = /** @suppress {undefinedVars, duplicate} @this{Object} */function(desc, insertionCallback) {
  var self = this.ptr;
  if (desc && typeof desc === 'object') desc = desc.ptr;
  if (insertionCallback && typeof insertionCallback === 'object') insertionCallback = insertionCallback.ptr;
  return wrapPointer(_emscripten_bind_PxCooking_createConvexMesh_2(self, desc, insertionCallback), PxConvexMesh);
};;

// PxCookingParams
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxCookingParams(sc) {
  if (sc && typeof sc === 'object') sc = sc.ptr;
  this.ptr = _emscripten_bind_PxCookingParams_PxCookingParams_1(sc);
  getCache(PxCookingParams)[this.ptr] = this;
};;
PxCookingParams.prototype = Object.create(WrapperObject.prototype);
PxCookingParams.prototype.constructor = PxCookingParams;
PxCookingParams.prototype.__class__ = PxCookingParams;
PxCookingParams.__cache__ = {};
Module['PxCookingParams'] = PxCookingParams;

  PxCookingParams.prototype['__destroy__'] = PxCookingParams.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxCookingParams___destroy___0(self);
};
// PxBaseFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBaseFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxBaseFlags_PxBaseFlags_1(flags);
  getCache(PxBaseFlags)[this.ptr] = this;
};;
PxBaseFlags.prototype = Object.create(WrapperObject.prototype);
PxBaseFlags.prototype.constructor = PxBaseFlags;
PxBaseFlags.prototype.__class__ = PxBaseFlags;
PxBaseFlags.__cache__ = {};
Module['PxBaseFlags'] = PxBaseFlags;

PxBaseFlags.prototype['isSet'] = PxBaseFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxBaseFlags_isSet_1(self, flag));
};;

PxBaseFlags.prototype['set'] = PxBaseFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxBaseFlags_set_1(self, flag);
};;

PxBaseFlags.prototype['clear'] = PxBaseFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxBaseFlags_clear_1(self, flag);
};;

  PxBaseFlags.prototype['__destroy__'] = PxBaseFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBaseFlags___destroy___0(self);
};
// PxBaseTask
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBaseTask() { throw "cannot construct a PxBaseTask, no constructor in IDL" }
PxBaseTask.prototype = Object.create(WrapperObject.prototype);
PxBaseTask.prototype.constructor = PxBaseTask;
PxBaseTask.prototype.__class__ = PxBaseTask;
PxBaseTask.__cache__ = {};
Module['PxBaseTask'] = PxBaseTask;

  PxBaseTask.prototype['__destroy__'] = PxBaseTask.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBaseTask___destroy___0(self);
};
// PxBoundedData
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBoundedData() { throw "cannot construct a PxBoundedData, no constructor in IDL" }
PxBoundedData.prototype = Object.create(WrapperObject.prototype);
PxBoundedData.prototype.constructor = PxBoundedData;
PxBoundedData.prototype.__class__ = PxBoundedData;
PxBoundedData.__cache__ = {};
Module['PxBoundedData'] = PxBoundedData;

  PxBoundedData.prototype['get_count'] = PxBoundedData.prototype.get_count = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBoundedData_get_count_0(self);
};
    PxBoundedData.prototype['set_count'] = PxBoundedData.prototype.set_count = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBoundedData_set_count_1(self, arg0);
};
    Object.defineProperty(PxBoundedData.prototype, 'count', { get: PxBoundedData.prototype.get_count, set: PxBoundedData.prototype.set_count });
  PxBoundedData.prototype['get_stride'] = PxBoundedData.prototype.get_stride = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxBoundedData_get_stride_0(self);
};
    PxBoundedData.prototype['set_stride'] = PxBoundedData.prototype.set_stride = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBoundedData_set_stride_1(self, arg0);
};
    Object.defineProperty(PxBoundedData.prototype, 'stride', { get: PxBoundedData.prototype.get_stride, set: PxBoundedData.prototype.set_stride });
  PxBoundedData.prototype['get_data'] = PxBoundedData.prototype.get_data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBoundedData_get_data_0(self), VoidPtr);
};
    PxBoundedData.prototype['set_data'] = PxBoundedData.prototype.set_data = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBoundedData_set_data_1(self, arg0);
};
    Object.defineProperty(PxBoundedData.prototype, 'data', { get: PxBoundedData.prototype.get_data, set: PxBoundedData.prototype.set_data });
  PxBoundedData.prototype['__destroy__'] = PxBoundedData.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBoundedData___destroy___0(self);
};
// PxBounds3
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxBounds3(minimum, maximum) {
  if (minimum && typeof minimum === 'object') minimum = minimum.ptr;
  if (maximum && typeof maximum === 'object') maximum = maximum.ptr;
  if (minimum === undefined) { this.ptr = _emscripten_bind_PxBounds3_PxBounds3_0(); getCache(PxBounds3)[this.ptr] = this;return }
  if (maximum === undefined) { this.ptr = _emscripten_bind_PxBounds3_PxBounds3_1(minimum); getCache(PxBounds3)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxBounds3_PxBounds3_2(minimum, maximum);
  getCache(PxBounds3)[this.ptr] = this;
};;
PxBounds3.prototype = Object.create(WrapperObject.prototype);
PxBounds3.prototype.constructor = PxBounds3;
PxBounds3.prototype.__class__ = PxBounds3;
PxBounds3.__cache__ = {};
Module['PxBounds3'] = PxBounds3;

PxBounds3.prototype['setEmpty'] = PxBounds3.prototype.setEmpty = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBounds3_setEmpty_0(self);
};;

PxBounds3.prototype['setMaximal'] = PxBounds3.prototype.setMaximal = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBounds3_setMaximal_0(self);
};;

PxBounds3.prototype['include'] = PxBounds3.prototype.include = /** @suppress {undefinedVars, duplicate} @this{Object} */function(v) {
  var self = this.ptr;
  if (v && typeof v === 'object') v = v.ptr;
  _emscripten_bind_PxBounds3_include_1(self, v);
};;

PxBounds3.prototype['isEmpty'] = PxBounds3.prototype.isEmpty = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxBounds3_isEmpty_0(self));
};;

PxBounds3.prototype['intersects'] = PxBounds3.prototype.intersects = /** @suppress {undefinedVars, duplicate} @this{Object} */function(b) {
  var self = this.ptr;
  if (b && typeof b === 'object') b = b.ptr;
  return !!(_emscripten_bind_PxBounds3_intersects_1(self, b));
};;

PxBounds3.prototype['intersects1D'] = PxBounds3.prototype.intersects1D = /** @suppress {undefinedVars, duplicate} @this{Object} */function(b, axis) {
  var self = this.ptr;
  if (b && typeof b === 'object') b = b.ptr;
  if (axis && typeof axis === 'object') axis = axis.ptr;
  return !!(_emscripten_bind_PxBounds3_intersects1D_2(self, b, axis));
};;

PxBounds3.prototype['contains'] = PxBounds3.prototype.contains = /** @suppress {undefinedVars, duplicate} @this{Object} */function(v) {
  var self = this.ptr;
  if (v && typeof v === 'object') v = v.ptr;
  return !!(_emscripten_bind_PxBounds3_contains_1(self, v));
};;

PxBounds3.prototype['isInside'] = PxBounds3.prototype.isInside = /** @suppress {undefinedVars, duplicate} @this{Object} */function(box) {
  var self = this.ptr;
  if (box && typeof box === 'object') box = box.ptr;
  return !!(_emscripten_bind_PxBounds3_isInside_1(self, box));
};;

PxBounds3.prototype['getCenter'] = PxBounds3.prototype.getCenter = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBounds3_getCenter_0(self), PxVec3);
};;

PxBounds3.prototype['getDimensions'] = PxBounds3.prototype.getDimensions = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBounds3_getDimensions_0(self), PxVec3);
};;

PxBounds3.prototype['getExtents'] = PxBounds3.prototype.getExtents = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBounds3_getExtents_0(self), PxVec3);
};;

PxBounds3.prototype['scaleSafe'] = PxBounds3.prototype.scaleSafe = /** @suppress {undefinedVars, duplicate} @this{Object} */function(scale) {
  var self = this.ptr;
  if (scale && typeof scale === 'object') scale = scale.ptr;
  _emscripten_bind_PxBounds3_scaleSafe_1(self, scale);
};;

PxBounds3.prototype['scaleFast'] = PxBounds3.prototype.scaleFast = /** @suppress {undefinedVars, duplicate} @this{Object} */function(scale) {
  var self = this.ptr;
  if (scale && typeof scale === 'object') scale = scale.ptr;
  _emscripten_bind_PxBounds3_scaleFast_1(self, scale);
};;

PxBounds3.prototype['fattenSafe'] = PxBounds3.prototype.fattenSafe = /** @suppress {undefinedVars, duplicate} @this{Object} */function(distance) {
  var self = this.ptr;
  if (distance && typeof distance === 'object') distance = distance.ptr;
  _emscripten_bind_PxBounds3_fattenSafe_1(self, distance);
};;

PxBounds3.prototype['fattenFast'] = PxBounds3.prototype.fattenFast = /** @suppress {undefinedVars, duplicate} @this{Object} */function(distance) {
  var self = this.ptr;
  if (distance && typeof distance === 'object') distance = distance.ptr;
  _emscripten_bind_PxBounds3_fattenFast_1(self, distance);
};;

PxBounds3.prototype['isFinite'] = PxBounds3.prototype.isFinite = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxBounds3_isFinite_0(self));
};;

PxBounds3.prototype['isValid'] = PxBounds3.prototype.isValid = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxBounds3_isValid_0(self));
};;

  PxBounds3.prototype['get_minimum'] = PxBounds3.prototype.get_minimum = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBounds3_get_minimum_0(self), PxVec3);
};
    PxBounds3.prototype['set_minimum'] = PxBounds3.prototype.set_minimum = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBounds3_set_minimum_1(self, arg0);
};
    Object.defineProperty(PxBounds3.prototype, 'minimum', { get: PxBounds3.prototype.get_minimum, set: PxBounds3.prototype.set_minimum });
  PxBounds3.prototype['get_maximum'] = PxBounds3.prototype.get_maximum = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxBounds3_get_maximum_0(self), PxVec3);
};
    PxBounds3.prototype['set_maximum'] = PxBounds3.prototype.set_maximum = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxBounds3_set_maximum_1(self, arg0);
};
    Object.defineProperty(PxBounds3.prototype, 'maximum', { get: PxBounds3.prototype.get_maximum, set: PxBounds3.prototype.set_maximum });
  PxBounds3.prototype['__destroy__'] = PxBounds3.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxBounds3___destroy___0(self);
};
// PxDefaultErrorCallback
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxDefaultErrorCallback() {
  this.ptr = _emscripten_bind_PxDefaultErrorCallback_PxDefaultErrorCallback_0();
  getCache(PxDefaultErrorCallback)[this.ptr] = this;
};;
PxDefaultErrorCallback.prototype = Object.create(WrapperObject.prototype);
PxDefaultErrorCallback.prototype.constructor = PxDefaultErrorCallback;
PxDefaultErrorCallback.prototype.__class__ = PxDefaultErrorCallback;
PxDefaultErrorCallback.__cache__ = {};
Module['PxDefaultErrorCallback'] = PxDefaultErrorCallback;

  PxDefaultErrorCallback.prototype['__destroy__'] = PxDefaultErrorCallback.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxDefaultErrorCallback___destroy___0(self);
};
// PxFoundation
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxFoundation() { throw "cannot construct a PxFoundation, no constructor in IDL" }
PxFoundation.prototype = Object.create(WrapperObject.prototype);
PxFoundation.prototype.constructor = PxFoundation;
PxFoundation.prototype.__class__ = PxFoundation;
PxFoundation.__cache__ = {};
Module['PxFoundation'] = PxFoundation;

// PxPhysicsInsertionCallback
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxPhysicsInsertionCallback() { throw "cannot construct a PxPhysicsInsertionCallback, no constructor in IDL" }
PxPhysicsInsertionCallback.prototype = Object.create(WrapperObject.prototype);
PxPhysicsInsertionCallback.prototype.constructor = PxPhysicsInsertionCallback;
PxPhysicsInsertionCallback.prototype.__class__ = PxPhysicsInsertionCallback;
PxPhysicsInsertionCallback.__cache__ = {};
Module['PxPhysicsInsertionCallback'] = PxPhysicsInsertionCallback;

// PxQuat
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxQuat(x, y, z, w) {
  if (x && typeof x === 'object') x = x.ptr;
  if (y && typeof y === 'object') y = y.ptr;
  if (z && typeof z === 'object') z = z.ptr;
  if (w && typeof w === 'object') w = w.ptr;
  if (x === undefined) { this.ptr = _emscripten_bind_PxQuat_PxQuat_0(); getCache(PxQuat)[this.ptr] = this;return }
  if (y === undefined) { this.ptr = _emscripten_bind_PxQuat_PxQuat_1(x); getCache(PxQuat)[this.ptr] = this;return }
  if (z === undefined) { this.ptr = _emscripten_bind_PxQuat_PxQuat_2(x, y); getCache(PxQuat)[this.ptr] = this;return }
  if (w === undefined) { this.ptr = _emscripten_bind_PxQuat_PxQuat_3(x, y, z); getCache(PxQuat)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxQuat_PxQuat_4(x, y, z, w);
  getCache(PxQuat)[this.ptr] = this;
};;
PxQuat.prototype = Object.create(WrapperObject.prototype);
PxQuat.prototype.constructor = PxQuat;
PxQuat.prototype.__class__ = PxQuat;
PxQuat.__cache__ = {};
Module['PxQuat'] = PxQuat;

  PxQuat.prototype['get_x'] = PxQuat.prototype.get_x = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxQuat_get_x_0(self);
};
    PxQuat.prototype['set_x'] = PxQuat.prototype.set_x = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQuat_set_x_1(self, arg0);
};
    Object.defineProperty(PxQuat.prototype, 'x', { get: PxQuat.prototype.get_x, set: PxQuat.prototype.set_x });
  PxQuat.prototype['get_y'] = PxQuat.prototype.get_y = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxQuat_get_y_0(self);
};
    PxQuat.prototype['set_y'] = PxQuat.prototype.set_y = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQuat_set_y_1(self, arg0);
};
    Object.defineProperty(PxQuat.prototype, 'y', { get: PxQuat.prototype.get_y, set: PxQuat.prototype.set_y });
  PxQuat.prototype['get_z'] = PxQuat.prototype.get_z = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxQuat_get_z_0(self);
};
    PxQuat.prototype['set_z'] = PxQuat.prototype.set_z = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQuat_set_z_1(self, arg0);
};
    Object.defineProperty(PxQuat.prototype, 'z', { get: PxQuat.prototype.get_z, set: PxQuat.prototype.set_z });
  PxQuat.prototype['get_w'] = PxQuat.prototype.get_w = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxQuat_get_w_0(self);
};
    PxQuat.prototype['set_w'] = PxQuat.prototype.set_w = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxQuat_set_w_1(self, arg0);
};
    Object.defineProperty(PxQuat.prototype, 'w', { get: PxQuat.prototype.get_w, set: PxQuat.prototype.set_w });
  PxQuat.prototype['__destroy__'] = PxQuat.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxQuat___destroy___0(self);
};
// PxTolerancesScale
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxTolerancesScale() {
  this.ptr = _emscripten_bind_PxTolerancesScale_PxTolerancesScale_0();
  getCache(PxTolerancesScale)[this.ptr] = this;
};;
PxTolerancesScale.prototype = Object.create(WrapperObject.prototype);
PxTolerancesScale.prototype.constructor = PxTolerancesScale;
PxTolerancesScale.prototype.__class__ = PxTolerancesScale;
PxTolerancesScale.__cache__ = {};
Module['PxTolerancesScale'] = PxTolerancesScale;

  PxTolerancesScale.prototype['__destroy__'] = PxTolerancesScale.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxTolerancesScale___destroy___0(self);
};
// PxTransform
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxTransform(p0, q0) {
  if (p0 && typeof p0 === 'object') p0 = p0.ptr;
  if (q0 && typeof q0 === 'object') q0 = q0.ptr;
  if (q0 === undefined) { this.ptr = _emscripten_bind_PxTransform_PxTransform_1(p0); getCache(PxTransform)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxTransform_PxTransform_2(p0, q0);
  getCache(PxTransform)[this.ptr] = this;
};;
PxTransform.prototype = Object.create(WrapperObject.prototype);
PxTransform.prototype.constructor = PxTransform;
PxTransform.prototype.__class__ = PxTransform;
PxTransform.__cache__ = {};
Module['PxTransform'] = PxTransform;

  PxTransform.prototype['get_q'] = PxTransform.prototype.get_q = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxTransform_get_q_0(self), PxQuat);
};
    PxTransform.prototype['set_q'] = PxTransform.prototype.set_q = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxTransform_set_q_1(self, arg0);
};
    Object.defineProperty(PxTransform.prototype, 'q', { get: PxTransform.prototype.get_q, set: PxTransform.prototype.set_q });
  PxTransform.prototype['get_p'] = PxTransform.prototype.get_p = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxTransform_get_p_0(self), PxVec3);
};
    PxTransform.prototype['set_p'] = PxTransform.prototype.set_p = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxTransform_set_p_1(self, arg0);
};
    Object.defineProperty(PxTransform.prototype, 'p', { get: PxTransform.prototype.get_p, set: PxTransform.prototype.set_p });
  PxTransform.prototype['__destroy__'] = PxTransform.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxTransform___destroy___0(self);
};
// PxRealPtr
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRealPtr() { throw "cannot construct a PxRealPtr, no constructor in IDL" }
PxRealPtr.prototype = Object.create(WrapperObject.prototype);
PxRealPtr.prototype.constructor = PxRealPtr;
PxRealPtr.prototype.__class__ = PxRealPtr;
PxRealPtr.__cache__ = {};
Module['PxRealPtr'] = PxRealPtr;

  PxRealPtr.prototype['__destroy__'] = PxRealPtr.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRealPtr___destroy___0(self);
};
// PxU8Ptr
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxU8Ptr() { throw "cannot construct a PxU8Ptr, no constructor in IDL" }
PxU8Ptr.prototype = Object.create(WrapperObject.prototype);
PxU8Ptr.prototype.constructor = PxU8Ptr;
PxU8Ptr.prototype.__class__ = PxU8Ptr;
PxU8Ptr.__cache__ = {};
Module['PxU8Ptr'] = PxU8Ptr;

  PxU8Ptr.prototype['__destroy__'] = PxU8Ptr.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxU8Ptr___destroy___0(self);
};
// PxVec3
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVec3(x, y, z) {
  if (x && typeof x === 'object') x = x.ptr;
  if (y && typeof y === 'object') y = y.ptr;
  if (z && typeof z === 'object') z = z.ptr;
  if (x === undefined) { this.ptr = _emscripten_bind_PxVec3_PxVec3_0(); getCache(PxVec3)[this.ptr] = this;return }
  if (y === undefined) { this.ptr = _emscripten_bind_PxVec3_PxVec3_1(x); getCache(PxVec3)[this.ptr] = this;return }
  if (z === undefined) { this.ptr = _emscripten_bind_PxVec3_PxVec3_2(x, y); getCache(PxVec3)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_PxVec3_PxVec3_3(x, y, z);
  getCache(PxVec3)[this.ptr] = this;
};;
PxVec3.prototype = Object.create(WrapperObject.prototype);
PxVec3.prototype.constructor = PxVec3;
PxVec3.prototype.__class__ = PxVec3;
PxVec3.__cache__ = {};
Module['PxVec3'] = PxVec3;

  PxVec3.prototype['get_x'] = PxVec3.prototype.get_x = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVec3_get_x_0(self);
};
    PxVec3.prototype['set_x'] = PxVec3.prototype.set_x = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVec3_set_x_1(self, arg0);
};
    Object.defineProperty(PxVec3.prototype, 'x', { get: PxVec3.prototype.get_x, set: PxVec3.prototype.set_x });
  PxVec3.prototype['get_y'] = PxVec3.prototype.get_y = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVec3_get_y_0(self);
};
    PxVec3.prototype['set_y'] = PxVec3.prototype.set_y = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVec3_set_y_1(self, arg0);
};
    Object.defineProperty(PxVec3.prototype, 'y', { get: PxVec3.prototype.get_y, set: PxVec3.prototype.set_y });
  PxVec3.prototype['get_z'] = PxVec3.prototype.get_z = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxVec3_get_z_0(self);
};
    PxVec3.prototype['set_z'] = PxVec3.prototype.set_z = /** @suppress {undefinedVars, duplicate} @this{Object} */function(arg0) {
  var self = this.ptr;
  if (arg0 && typeof arg0 === 'object') arg0 = arg0.ptr;
  _emscripten_bind_PxVec3_set_z_1(self, arg0);
};
    Object.defineProperty(PxVec3.prototype, 'z', { get: PxVec3.prototype.get_z, set: PxVec3.prototype.set_z });
  PxVec3.prototype['__destroy__'] = PxVec3.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVec3___destroy___0(self);
};
// PxDefaultAllocator
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxDefaultAllocator() {
  this.ptr = _emscripten_bind_PxDefaultAllocator_PxDefaultAllocator_0();
  getCache(PxDefaultAllocator)[this.ptr] = this;
};;
PxDefaultAllocator.prototype = Object.create(WrapperObject.prototype);
PxDefaultAllocator.prototype.constructor = PxDefaultAllocator;
PxDefaultAllocator.prototype.__class__ = PxDefaultAllocator;
PxDefaultAllocator.__cache__ = {};
Module['PxDefaultAllocator'] = PxDefaultAllocator;

  PxDefaultAllocator.prototype['__destroy__'] = PxDefaultAllocator.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxDefaultAllocator___destroy___0(self);
};
// PxDefaultCpuDispatcher
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxDefaultCpuDispatcher() { throw "cannot construct a PxDefaultCpuDispatcher, no constructor in IDL" }
PxDefaultCpuDispatcher.prototype = Object.create(PxCpuDispatcher.prototype);
PxDefaultCpuDispatcher.prototype.constructor = PxDefaultCpuDispatcher;
PxDefaultCpuDispatcher.prototype.__class__ = PxDefaultCpuDispatcher;
PxDefaultCpuDispatcher.__cache__ = {};
Module['PxDefaultCpuDispatcher'] = PxDefaultCpuDispatcher;

  PxDefaultCpuDispatcher.prototype['__destroy__'] = PxDefaultCpuDispatcher.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxDefaultCpuDispatcher___destroy___0(self);
};
// PxRevoluteJoint
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRevoluteJoint() { throw "cannot construct a PxRevoluteJoint, no constructor in IDL" }
PxRevoluteJoint.prototype = Object.create(PxJoint.prototype);
PxRevoluteJoint.prototype.constructor = PxRevoluteJoint;
PxRevoluteJoint.prototype.__class__ = PxRevoluteJoint;
PxRevoluteJoint.__cache__ = {};
Module['PxRevoluteJoint'] = PxRevoluteJoint;

PxRevoluteJoint.prototype['setDriveVelocity'] = PxRevoluteJoint.prototype.setDriveVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function(velocity, autowake) {
  var self = this.ptr;
  if (velocity && typeof velocity === 'object') velocity = velocity.ptr;
  if (autowake && typeof autowake === 'object') autowake = autowake.ptr;
  if (autowake === undefined) { _emscripten_bind_PxRevoluteJoint_setDriveVelocity_1(self, velocity);  return }
  _emscripten_bind_PxRevoluteJoint_setDriveVelocity_2(self, velocity, autowake);
};;

PxRevoluteJoint.prototype['getDriveVelocity'] = PxRevoluteJoint.prototype.getDriveVelocity = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRevoluteJoint_getDriveVelocity_0(self);
};;

PxRevoluteJoint.prototype['setDriveForceLimit'] = PxRevoluteJoint.prototype.setDriveForceLimit = /** @suppress {undefinedVars, duplicate} @this{Object} */function(limit) {
  var self = this.ptr;
  if (limit && typeof limit === 'object') limit = limit.ptr;
  _emscripten_bind_PxRevoluteJoint_setDriveForceLimit_1(self, limit);
};;

PxRevoluteJoint.prototype['getDriveForceLimit'] = PxRevoluteJoint.prototype.getDriveForceLimit = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRevoluteJoint_getDriveForceLimit_0(self);
};;

PxRevoluteJoint.prototype['setDriveGearRatio'] = PxRevoluteJoint.prototype.setDriveGearRatio = /** @suppress {undefinedVars, duplicate} @this{Object} */function(ratio) {
  var self = this.ptr;
  if (ratio && typeof ratio === 'object') ratio = ratio.ptr;
  _emscripten_bind_PxRevoluteJoint_setDriveGearRatio_1(self, ratio);
};;

PxRevoluteJoint.prototype['getDriveGearRatio'] = PxRevoluteJoint.prototype.getDriveGearRatio = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRevoluteJoint_getDriveGearRatio_0(self);
};;

PxRevoluteJoint.prototype['setRevoluteJointFlags'] = PxRevoluteJoint.prototype.setRevoluteJointFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flags) {
  var self = this.ptr;
  if (flags && typeof flags === 'object') flags = flags.ptr;
  _emscripten_bind_PxRevoluteJoint_setRevoluteJointFlags_1(self, flags);
};;

PxRevoluteJoint.prototype['getRevoluteJointFlags'] = PxRevoluteJoint.prototype.getRevoluteJointFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRevoluteJoint_getRevoluteJointFlags_0(self), PxRevoluteJointFlags);
};;

PxRevoluteJoint.prototype['release'] = PxRevoluteJoint.prototype.release = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRevoluteJoint_release_0(self);
};;

PxRevoluteJoint.prototype['getConcreteTypeName'] = PxRevoluteJoint.prototype.getConcreteTypeName = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return UTF8ToString(_emscripten_bind_PxRevoluteJoint_getConcreteTypeName_0(self));
};;

PxRevoluteJoint.prototype['getConcreteType'] = PxRevoluteJoint.prototype.getConcreteType = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_PxRevoluteJoint_getConcreteType_0(self);
};;

PxRevoluteJoint.prototype['setBaseFlag'] = PxRevoluteJoint.prototype.setBaseFlag = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag, value) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_PxRevoluteJoint_setBaseFlag_2(self, flag, value);
};;

PxRevoluteJoint.prototype['setBaseFlags'] = PxRevoluteJoint.prototype.setBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function(inFlags) {
  var self = this.ptr;
  if (inFlags && typeof inFlags === 'object') inFlags = inFlags.ptr;
  _emscripten_bind_PxRevoluteJoint_setBaseFlags_1(self, inFlags);
};;

PxRevoluteJoint.prototype['getBaseFlags'] = PxRevoluteJoint.prototype.getBaseFlags = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_PxRevoluteJoint_getBaseFlags_0(self), PxBaseFlags);
};;

PxRevoluteJoint.prototype['isReleasable'] = PxRevoluteJoint.prototype.isReleasable = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return !!(_emscripten_bind_PxRevoluteJoint_isReleasable_0(self));
};;

  PxRevoluteJoint.prototype['__destroy__'] = PxRevoluteJoint.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRevoluteJoint___destroy___0(self);
};
// PxRevoluteJointFlags
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxRevoluteJointFlags(flags) {
  if (flags && typeof flags === 'object') flags = flags.ptr;
  this.ptr = _emscripten_bind_PxRevoluteJointFlags_PxRevoluteJointFlags_1(flags);
  getCache(PxRevoluteJointFlags)[this.ptr] = this;
};;
PxRevoluteJointFlags.prototype = Object.create(WrapperObject.prototype);
PxRevoluteJointFlags.prototype.constructor = PxRevoluteJointFlags;
PxRevoluteJointFlags.prototype.__class__ = PxRevoluteJointFlags;
PxRevoluteJointFlags.__cache__ = {};
Module['PxRevoluteJointFlags'] = PxRevoluteJointFlags;

PxRevoluteJointFlags.prototype['isSet'] = PxRevoluteJointFlags.prototype.isSet = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  return !!(_emscripten_bind_PxRevoluteJointFlags_isSet_1(self, flag));
};;

PxRevoluteJointFlags.prototype['set'] = PxRevoluteJointFlags.prototype.set = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxRevoluteJointFlags_set_1(self, flag);
};;

PxRevoluteJointFlags.prototype['clear'] = PxRevoluteJointFlags.prototype.clear = /** @suppress {undefinedVars, duplicate} @this{Object} */function(flag) {
  var self = this.ptr;
  if (flag && typeof flag === 'object') flag = flag.ptr;
  _emscripten_bind_PxRevoluteJointFlags_clear_1(self, flag);
};;

  PxRevoluteJointFlags.prototype['__destroy__'] = PxRevoluteJointFlags.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxRevoluteJointFlags___destroy___0(self);
};
// PxMaterialPtr
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxMaterialPtr() { throw "cannot construct a PxMaterialPtr, no constructor in IDL" }
PxMaterialPtr.prototype = Object.create(WrapperObject.prototype);
PxMaterialPtr.prototype.constructor = PxMaterialPtr;
PxMaterialPtr.prototype.__class__ = PxMaterialPtr;
PxMaterialPtr.__cache__ = {};
Module['PxMaterialPtr'] = PxMaterialPtr;

  PxMaterialPtr.prototype['__destroy__'] = PxMaterialPtr.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxMaterialPtr___destroy___0(self);
};
// Vector_PxMaterial
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxMaterial(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_0(); getCache(Vector_PxMaterial)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxMaterial_Vector_PxMaterial_1(size);
  getCache(Vector_PxMaterial)[this.ptr] = this;
};;
Vector_PxMaterial.prototype = Object.create(WrapperObject.prototype);
Vector_PxMaterial.prototype.constructor = Vector_PxMaterial;
Vector_PxMaterial.prototype.__class__ = Vector_PxMaterial;
Vector_PxMaterial.__cache__ = {};
Module['Vector_PxMaterial'] = Vector_PxMaterial;

Vector_PxMaterial.prototype['at'] = Vector_PxMaterial.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxMaterial_at_1(self, index), PxMaterial);
};;

Vector_PxMaterial.prototype['data'] = Vector_PxMaterial.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxMaterial_data_0(self), PxMaterialPtr);
};;

Vector_PxMaterial.prototype['size'] = Vector_PxMaterial.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxMaterial_size_0(self);
};;

Vector_PxMaterial.prototype['push_back'] = Vector_PxMaterial.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxMaterial_push_back_1(self, value);
};;

  Vector_PxMaterial.prototype['__destroy__'] = Vector_PxMaterial.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxMaterial___destroy___0(self);
};
// Vector_PxReal
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxReal(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxReal_Vector_PxReal_0(); getCache(Vector_PxReal)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxReal_Vector_PxReal_1(size);
  getCache(Vector_PxReal)[this.ptr] = this;
};;
Vector_PxReal.prototype = Object.create(WrapperObject.prototype);
Vector_PxReal.prototype.constructor = Vector_PxReal;
Vector_PxReal.prototype.__class__ = Vector_PxReal;
Vector_PxReal.__cache__ = {};
Module['Vector_PxReal'] = Vector_PxReal;

Vector_PxReal.prototype['at'] = Vector_PxReal.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return _emscripten_bind_Vector_PxReal_at_1(self, index);
};;

Vector_PxReal.prototype['data'] = Vector_PxReal.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxReal_data_0(self), PxRealPtr);
};;

Vector_PxReal.prototype['size'] = Vector_PxReal.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxReal_size_0(self);
};;

Vector_PxReal.prototype['push_back'] = Vector_PxReal.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxReal_push_back_1(self, value);
};;

  Vector_PxReal.prototype['__destroy__'] = Vector_PxReal.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxReal___destroy___0(self);
};
// Vector_PxVec3
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxVec3(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxVec3_Vector_PxVec3_0(); getCache(Vector_PxVec3)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxVec3_Vector_PxVec3_1(size);
  getCache(Vector_PxVec3)[this.ptr] = this;
};;
Vector_PxVec3.prototype = Object.create(WrapperObject.prototype);
Vector_PxVec3.prototype.constructor = Vector_PxVec3;
Vector_PxVec3.prototype.__class__ = Vector_PxVec3;
Vector_PxVec3.__cache__ = {};
Module['Vector_PxVec3'] = Vector_PxVec3;

Vector_PxVec3.prototype['at'] = Vector_PxVec3.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxVec3_at_1(self, index), PxVec3);
};;

Vector_PxVec3.prototype['data'] = Vector_PxVec3.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxVec3_data_0(self), PxVec3);
};;

Vector_PxVec3.prototype['size'] = Vector_PxVec3.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxVec3_size_0(self);
};;

Vector_PxVec3.prototype['push_back'] = Vector_PxVec3.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxVec3_push_back_1(self, value);
};;

  Vector_PxVec3.prototype['__destroy__'] = Vector_PxVec3.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxVec3___destroy___0(self);
};
// Vector_PxRaycastQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxRaycastQueryResult(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_0(); getCache(Vector_PxRaycastQueryResult)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxRaycastQueryResult_Vector_PxRaycastQueryResult_1(size);
  getCache(Vector_PxRaycastQueryResult)[this.ptr] = this;
};;
Vector_PxRaycastQueryResult.prototype = Object.create(WrapperObject.prototype);
Vector_PxRaycastQueryResult.prototype.constructor = Vector_PxRaycastQueryResult;
Vector_PxRaycastQueryResult.prototype.__class__ = Vector_PxRaycastQueryResult;
Vector_PxRaycastQueryResult.__cache__ = {};
Module['Vector_PxRaycastQueryResult'] = Vector_PxRaycastQueryResult;

Vector_PxRaycastQueryResult.prototype['at'] = Vector_PxRaycastQueryResult.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxRaycastQueryResult_at_1(self, index), PxRaycastQueryResult);
};;

Vector_PxRaycastQueryResult.prototype['data'] = Vector_PxRaycastQueryResult.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxRaycastQueryResult_data_0(self), PxRaycastQueryResult);
};;

Vector_PxRaycastQueryResult.prototype['size'] = Vector_PxRaycastQueryResult.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxRaycastQueryResult_size_0(self);
};;

Vector_PxRaycastQueryResult.prototype['push_back'] = Vector_PxRaycastQueryResult.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxRaycastQueryResult_push_back_1(self, value);
};;

  Vector_PxRaycastQueryResult.prototype['__destroy__'] = Vector_PxRaycastQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxRaycastQueryResult___destroy___0(self);
};
// Vector_PxSweepQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxSweepQueryResult(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_0(); getCache(Vector_PxSweepQueryResult)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxSweepQueryResult_Vector_PxSweepQueryResult_1(size);
  getCache(Vector_PxSweepQueryResult)[this.ptr] = this;
};;
Vector_PxSweepQueryResult.prototype = Object.create(WrapperObject.prototype);
Vector_PxSweepQueryResult.prototype.constructor = Vector_PxSweepQueryResult;
Vector_PxSweepQueryResult.prototype.__class__ = Vector_PxSweepQueryResult;
Vector_PxSweepQueryResult.__cache__ = {};
Module['Vector_PxSweepQueryResult'] = Vector_PxSweepQueryResult;

Vector_PxSweepQueryResult.prototype['at'] = Vector_PxSweepQueryResult.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxSweepQueryResult_at_1(self, index), PxSweepQueryResult);
};;

Vector_PxSweepQueryResult.prototype['data'] = Vector_PxSweepQueryResult.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxSweepQueryResult_data_0(self), PxSweepQueryResult);
};;

Vector_PxSweepQueryResult.prototype['size'] = Vector_PxSweepQueryResult.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxSweepQueryResult_size_0(self);
};;

Vector_PxSweepQueryResult.prototype['push_back'] = Vector_PxSweepQueryResult.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxSweepQueryResult_push_back_1(self, value);
};;

  Vector_PxSweepQueryResult.prototype['__destroy__'] = Vector_PxSweepQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxSweepQueryResult___destroy___0(self);
};
// Vector_PxRaycastHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxRaycastHit(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_0(); getCache(Vector_PxRaycastHit)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxRaycastHit_Vector_PxRaycastHit_1(size);
  getCache(Vector_PxRaycastHit)[this.ptr] = this;
};;
Vector_PxRaycastHit.prototype = Object.create(WrapperObject.prototype);
Vector_PxRaycastHit.prototype.constructor = Vector_PxRaycastHit;
Vector_PxRaycastHit.prototype.__class__ = Vector_PxRaycastHit;
Vector_PxRaycastHit.__cache__ = {};
Module['Vector_PxRaycastHit'] = Vector_PxRaycastHit;

Vector_PxRaycastHit.prototype['at'] = Vector_PxRaycastHit.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxRaycastHit_at_1(self, index), PxRaycastHit);
};;

Vector_PxRaycastHit.prototype['data'] = Vector_PxRaycastHit.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxRaycastHit_data_0(self), PxRaycastHit);
};;

Vector_PxRaycastHit.prototype['size'] = Vector_PxRaycastHit.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxRaycastHit_size_0(self);
};;

Vector_PxRaycastHit.prototype['push_back'] = Vector_PxRaycastHit.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxRaycastHit_push_back_1(self, value);
};;

  Vector_PxRaycastHit.prototype['__destroy__'] = Vector_PxRaycastHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxRaycastHit___destroy___0(self);
};
// Vector_PxSweepHit
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxSweepHit(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_0(); getCache(Vector_PxSweepHit)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxSweepHit_Vector_PxSweepHit_1(size);
  getCache(Vector_PxSweepHit)[this.ptr] = this;
};;
Vector_PxSweepHit.prototype = Object.create(WrapperObject.prototype);
Vector_PxSweepHit.prototype.constructor = Vector_PxSweepHit;
Vector_PxSweepHit.prototype.__class__ = Vector_PxSweepHit;
Vector_PxSweepHit.__cache__ = {};
Module['Vector_PxSweepHit'] = Vector_PxSweepHit;

Vector_PxSweepHit.prototype['at'] = Vector_PxSweepHit.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxSweepHit_at_1(self, index), PxSweepHit);
};;

Vector_PxSweepHit.prototype['data'] = Vector_PxSweepHit.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxSweepHit_data_0(self), PxSweepHit);
};;

Vector_PxSweepHit.prototype['size'] = Vector_PxSweepHit.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxSweepHit_size_0(self);
};;

Vector_PxSweepHit.prototype['push_back'] = Vector_PxSweepHit.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxSweepHit_push_back_1(self, value);
};;

  Vector_PxSweepHit.prototype['__destroy__'] = Vector_PxSweepHit.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxSweepHit___destroy___0(self);
};
// Vector_PxVehicleDrivableSurfaceType
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxVehicleDrivableSurfaceType(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_0(); getCache(Vector_PxVehicleDrivableSurfaceType)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_Vector_PxVehicleDrivableSurfaceType_1(size);
  getCache(Vector_PxVehicleDrivableSurfaceType)[this.ptr] = this;
};;
Vector_PxVehicleDrivableSurfaceType.prototype = Object.create(WrapperObject.prototype);
Vector_PxVehicleDrivableSurfaceType.prototype.constructor = Vector_PxVehicleDrivableSurfaceType;
Vector_PxVehicleDrivableSurfaceType.prototype.__class__ = Vector_PxVehicleDrivableSurfaceType;
Vector_PxVehicleDrivableSurfaceType.__cache__ = {};
Module['Vector_PxVehicleDrivableSurfaceType'] = Vector_PxVehicleDrivableSurfaceType;

Vector_PxVehicleDrivableSurfaceType.prototype['at'] = Vector_PxVehicleDrivableSurfaceType.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_at_1(self, index), PxVehicleDrivableSurfaceType);
};;

Vector_PxVehicleDrivableSurfaceType.prototype['data'] = Vector_PxVehicleDrivableSurfaceType.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxVehicleDrivableSurfaceType_data_0(self), PxVehicleDrivableSurfaceType);
};;

Vector_PxVehicleDrivableSurfaceType.prototype['size'] = Vector_PxVehicleDrivableSurfaceType.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_size_0(self);
};;

Vector_PxVehicleDrivableSurfaceType.prototype['push_back'] = Vector_PxVehicleDrivableSurfaceType.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxVehicleDrivableSurfaceType_push_back_1(self, value);
};;

  Vector_PxVehicleDrivableSurfaceType.prototype['__destroy__'] = Vector_PxVehicleDrivableSurfaceType.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxVehicleDrivableSurfaceType___destroy___0(self);
};
// Vector_PxWheelQueryResult
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxWheelQueryResult(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_0(); getCache(Vector_PxWheelQueryResult)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxWheelQueryResult_Vector_PxWheelQueryResult_1(size);
  getCache(Vector_PxWheelQueryResult)[this.ptr] = this;
};;
Vector_PxWheelQueryResult.prototype = Object.create(WrapperObject.prototype);
Vector_PxWheelQueryResult.prototype.constructor = Vector_PxWheelQueryResult;
Vector_PxWheelQueryResult.prototype.__class__ = Vector_PxWheelQueryResult;
Vector_PxWheelQueryResult.__cache__ = {};
Module['Vector_PxWheelQueryResult'] = Vector_PxWheelQueryResult;

Vector_PxWheelQueryResult.prototype['at'] = Vector_PxWheelQueryResult.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxWheelQueryResult_at_1(self, index), PxWheelQueryResult);
};;

Vector_PxWheelQueryResult.prototype['data'] = Vector_PxWheelQueryResult.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxWheelQueryResult_data_0(self), PxWheelQueryResult);
};;

Vector_PxWheelQueryResult.prototype['size'] = Vector_PxWheelQueryResult.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxWheelQueryResult_size_0(self);
};;

Vector_PxWheelQueryResult.prototype['push_back'] = Vector_PxWheelQueryResult.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxWheelQueryResult_push_back_1(self, value);
};;

  Vector_PxWheelQueryResult.prototype['__destroy__'] = Vector_PxWheelQueryResult.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxWheelQueryResult___destroy___0(self);
};
// PxVehicleWheelsPtr
/** @suppress {undefinedVars, duplicate} @this{Object} */function PxVehicleWheelsPtr() { throw "cannot construct a PxVehicleWheelsPtr, no constructor in IDL" }
PxVehicleWheelsPtr.prototype = Object.create(WrapperObject.prototype);
PxVehicleWheelsPtr.prototype.constructor = PxVehicleWheelsPtr;
PxVehicleWheelsPtr.prototype.__class__ = PxVehicleWheelsPtr;
PxVehicleWheelsPtr.__cache__ = {};
Module['PxVehicleWheelsPtr'] = PxVehicleWheelsPtr;

  PxVehicleWheelsPtr.prototype['__destroy__'] = PxVehicleWheelsPtr.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_PxVehicleWheelsPtr___destroy___0(self);
};
// Vector_PxVehicleWheels
/** @suppress {undefinedVars, duplicate} @this{Object} */function Vector_PxVehicleWheels(size) {
  if (size && typeof size === 'object') size = size.ptr;
  if (size === undefined) { this.ptr = _emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_0(); getCache(Vector_PxVehicleWheels)[this.ptr] = this;return }
  this.ptr = _emscripten_bind_Vector_PxVehicleWheels_Vector_PxVehicleWheels_1(size);
  getCache(Vector_PxVehicleWheels)[this.ptr] = this;
};;
Vector_PxVehicleWheels.prototype = Object.create(WrapperObject.prototype);
Vector_PxVehicleWheels.prototype.constructor = Vector_PxVehicleWheels;
Vector_PxVehicleWheels.prototype.__class__ = Vector_PxVehicleWheels;
Vector_PxVehicleWheels.__cache__ = {};
Module['Vector_PxVehicleWheels'] = Vector_PxVehicleWheels;

Vector_PxVehicleWheels.prototype['at'] = Vector_PxVehicleWheels.prototype.at = /** @suppress {undefinedVars, duplicate} @this{Object} */function(index) {
  var self = this.ptr;
  if (index && typeof index === 'object') index = index.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxVehicleWheels_at_1(self, index), PxVehicleWheels);
};;

Vector_PxVehicleWheels.prototype['data'] = Vector_PxVehicleWheels.prototype.data = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return wrapPointer(_emscripten_bind_Vector_PxVehicleWheels_data_0(self), PxVehicleWheelsPtr);
};;

Vector_PxVehicleWheels.prototype['size'] = Vector_PxVehicleWheels.prototype.size = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  return _emscripten_bind_Vector_PxVehicleWheels_size_0(self);
};;

Vector_PxVehicleWheels.prototype['push_back'] = Vector_PxVehicleWheels.prototype.push_back = /** @suppress {undefinedVars, duplicate} @this{Object} */function(value) {
  var self = this.ptr;
  if (value && typeof value === 'object') value = value.ptr;
  _emscripten_bind_Vector_PxVehicleWheels_push_back_1(self, value);
};;

  Vector_PxVehicleWheels.prototype['__destroy__'] = Vector_PxVehicleWheels.prototype.__destroy__ = /** @suppress {undefinedVars, duplicate} @this{Object} */function() {
  var self = this.ptr;
  _emscripten_bind_Vector_PxVehicleWheels___destroy___0(self);
};
(function() {
  function setupEnums() {
    

    // PxActorFlagEnum

    Module['eVISUALIZATION'] = _emscripten_enum_PxActorFlagEnum_eVISUALIZATION();

    Module['eDISABLE_GRAVITY'] = _emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY();

    Module['eSEND_SLEEP_NOTIFIES'] = _emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES();

    Module['eDISABLE_SIMULATION'] = _emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION();

    

    // PxActorTypeEnum

    Module['eRIGID_STATIC'] = _emscripten_enum_PxActorTypeEnum_eRIGID_STATIC();

    Module['eRIGID_DYNAMIC'] = _emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC();

    Module['eARTICULATION_LINK'] = _emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK();

    Module['eACTOR_COUNT'] = _emscripten_enum_PxActorTypeEnum_eACTOR_COUNT();

    Module['eACTOR_FORCE_DWORD'] = _emscripten_enum_PxActorTypeEnum_eACTOR_FORCE_DWORD();

    

    // PxForceModeEnum

    Module['eFORCE'] = _emscripten_enum_PxForceModeEnum_eFORCE();

    Module['eIMPULSE'] = _emscripten_enum_PxForceModeEnum_eIMPULSE();

    Module['eVELOCITY_CHANGE'] = _emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE();

    Module['eACCELERATION'] = _emscripten_enum_PxForceModeEnum_eACCELERATION();

    

    // PxHitFlagEnum

    Module['ePOSITION'] = _emscripten_enum_PxHitFlagEnum_ePOSITION();

    Module['eNORMAL'] = _emscripten_enum_PxHitFlagEnum_eNORMAL();

    Module['eUV'] = _emscripten_enum_PxHitFlagEnum_eUV();

    Module['eASSUME_NO_INITIAL_OVERLAP'] = _emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP();

    Module['eMESH_MULTIPLE'] = _emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE();

    Module['eMESH_ANY'] = _emscripten_enum_PxHitFlagEnum_eMESH_ANY();

    Module['eMESH_BOTH_SIDES'] = _emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES();

    Module['ePRECISE_SWEEP'] = _emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP();

    Module['eMTD'] = _emscripten_enum_PxHitFlagEnum_eMTD();

    Module['eFACE_INDEX'] = _emscripten_enum_PxHitFlagEnum_eFACE_INDEX();

    Module['eDEFAULT'] = _emscripten_enum_PxHitFlagEnum_eDEFAULT();

    Module['eMODIFIABLE_FLAGS'] = _emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS();

    

    // PxRigidBodyFlagEnum

    Module['eKINEMATIC'] = _emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC();

    Module['eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES'] = _emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES();

    Module['eENABLE_CCD'] = _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD();

    Module['eENABLE_CCD_FRICTION'] = _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION();

    Module['eENABLE_POSE_INTEGRATION_PREVIEW'] = _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW();

    Module['eENABLE_SPECULATIVE_CCD'] = _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD();

    Module['eENABLE_CCD_MAX_CONTACT_IMPULSE'] = _emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE();

    Module['eRETAIN_ACCELERATIONS'] = _emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS();

    

    // PxRigidDynamicLockFlagEnum

    Module['eLOCK_LINEAR_X'] = _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X();

    Module['eLOCK_LINEAR_Y'] = _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y();

    Module['eLOCK_LINEAR_Z'] = _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z();

    Module['eLOCK_ANGULAR_X'] = _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X();

    Module['eLOCK_ANGULAR_Y'] = _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y();

    Module['eLOCK_ANGULAR_Z'] = _emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z();

    

    // PxSceneFlagEnum

    Module['eENABLE_ACTIVE_ACTORS'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS();

    Module['eENABLE_CCD'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_CCD();

    Module['eDISABLE_CCD_RESWEEP'] = _emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP();

    Module['eADAPTIVE_FORCE'] = _emscripten_enum_PxSceneFlagEnum_eADAPTIVE_FORCE();

    Module['eENABLE_PCM'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_PCM();

    Module['eDISABLE_CONTACT_REPORT_BUFFER_RESIZE'] = _emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE();

    Module['eDISABLE_CONTACT_CACHE'] = _emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE();

    Module['eREQUIRE_RW_LOCK'] = _emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK();

    Module['eENABLE_STABILIZATION'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION();

    Module['eENABLE_AVERAGE_POINT'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT();

    Module['eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS'] = _emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS();

    Module['eENABLE_GPU_DYNAMICS'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS();

    Module['eENABLE_ENHANCED_DETERMINISM'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM();

    Module['eENABLE_FRICTION_EVERY_ITERATION'] = _emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION();

    Module['eMUTABLE_FLAGS'] = _emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS();

    

    // PxShapeFlagEnum

    Module['eSIMULATION_SHAPE'] = _emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE();

    Module['eSCENE_QUERY_SHAPE'] = _emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE();

    Module['eTRIGGER_SHAPE'] = _emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE();

    Module['eVISUALIZATION'] = _emscripten_enum_PxShapeFlagEnum_eVISUALIZATION();

    

    // PxVehicleClutchAccuracyModeEnum

    Module['eESTIMATE'] = _emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE();

    Module['eBEST_POSSIBLE'] = _emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE();

    

    // PxVehicleDifferential4WDataEnum

    Module['eDIFF_TYPE_LS_4WD'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_4WD();

    Module['eDIFF_TYPE_LS_FRONTWD'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_FRONTWD();

    Module['eDIFF_TYPE_LS_REARWD'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_REARWD();

    Module['eDIFF_TYPE_OPEN_4WD'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_4WD();

    Module['eDIFF_TYPE_OPEN_FRONTWD'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_FRONTWD();

    Module['eDIFF_TYPE_OPEN_REARWD'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_REARWD();

    Module['eMAX_NB_DIFF_TYPES'] = _emscripten_enum_PxVehicleDifferential4WDataEnum_eMAX_NB_DIFF_TYPES();

    

    // PxVehicleDrive4WControlEnum

    Module['eANALOG_INPUT_ACCEL'] = _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_ACCEL();

    Module['eANALOG_INPUT_BRAKE'] = _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_BRAKE();

    Module['eANALOG_INPUT_HANDBRAKE'] = _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_HANDBRAKE();

    Module['eANALOG_INPUT_STEER_LEFT'] = _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_LEFT();

    Module['eANALOG_INPUT_STEER_RIGHT'] = _emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_RIGHT();

    Module['eMAX_NB_DRIVE4W_ANALOG_INPUTS'] = _emscripten_enum_PxVehicleDrive4WControlEnum_eMAX_NB_DRIVE4W_ANALOG_INPUTS();

    

    // PxVehicleGearEnum

    Module['eREVERSE'] = _emscripten_enum_PxVehicleGearEnum_eREVERSE();

    Module['eNEUTRAL'] = _emscripten_enum_PxVehicleGearEnum_eNEUTRAL();

    Module['eFIRST'] = _emscripten_enum_PxVehicleGearEnum_eFIRST();

    Module['eSECOND'] = _emscripten_enum_PxVehicleGearEnum_eSECOND();

    Module['eTHIRD'] = _emscripten_enum_PxVehicleGearEnum_eTHIRD();

    Module['eFOURTH'] = _emscripten_enum_PxVehicleGearEnum_eFOURTH();

    Module['eFIFTH'] = _emscripten_enum_PxVehicleGearEnum_eFIFTH();

    Module['eSIXTH'] = _emscripten_enum_PxVehicleGearEnum_eSIXTH();

    Module['eSEVENTH'] = _emscripten_enum_PxVehicleGearEnum_eSEVENTH();

    Module['eEIGHTH'] = _emscripten_enum_PxVehicleGearEnum_eEIGHTH();

    Module['eNINTH'] = _emscripten_enum_PxVehicleGearEnum_eNINTH();

    Module['eTENTH'] = _emscripten_enum_PxVehicleGearEnum_eTENTH();

    Module['eELEVENTH'] = _emscripten_enum_PxVehicleGearEnum_eELEVENTH();

    Module['eTWELFTH'] = _emscripten_enum_PxVehicleGearEnum_eTWELFTH();

    Module['eTHIRTEENTH'] = _emscripten_enum_PxVehicleGearEnum_eTHIRTEENTH();

    Module['eFOURTEENTH'] = _emscripten_enum_PxVehicleGearEnum_eFOURTEENTH();

    Module['eFIFTEENTH'] = _emscripten_enum_PxVehicleGearEnum_eFIFTEENTH();

    Module['eSIXTEENTH'] = _emscripten_enum_PxVehicleGearEnum_eSIXTEENTH();

    Module['eSEVENTEENTH'] = _emscripten_enum_PxVehicleGearEnum_eSEVENTEENTH();

    Module['eEIGHTEENTH'] = _emscripten_enum_PxVehicleGearEnum_eEIGHTEENTH();

    Module['eNINETEENTH'] = _emscripten_enum_PxVehicleGearEnum_eNINETEENTH();

    Module['eTWENTIETH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTIETH();

    Module['eTWENTYFIRST'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYFIRST();

    Module['eTWENTYSECOND'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYSECOND();

    Module['eTWENTYTHIRD'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYTHIRD();

    Module['eTWENTYFOURTH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYFOURTH();

    Module['eTWENTYFIFTH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYFIFTH();

    Module['eTWENTYSIXTH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYSIXTH();

    Module['eTWENTYSEVENTH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYSEVENTH();

    Module['eTWENTYEIGHTH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYEIGHTH();

    Module['eTWENTYNINTH'] = _emscripten_enum_PxVehicleGearEnum_eTWENTYNINTH();

    Module['eTHIRTIETH'] = _emscripten_enum_PxVehicleGearEnum_eTHIRTIETH();

    Module['eGEARSRATIO_COUNT'] = _emscripten_enum_PxVehicleGearEnum_eGEARSRATIO_COUNT();

    

    // PxVehicleUpdateModeEnum

    Module['eVELOCITY_CHANGE'] = _emscripten_enum_PxVehicleUpdateModeEnum_eVELOCITY_CHANGE();

    Module['eACCELERATION'] = _emscripten_enum_PxVehicleUpdateModeEnum_eACCELERATION();

    

    // PxVehicleWheelsSimFlagEnum

    Module['eLIMIT_SUSPENSION_EXPANSION_VELOCITY'] = _emscripten_enum_PxVehicleWheelsSimFlagEnum_eLIMIT_SUSPENSION_EXPANSION_VELOCITY();

    

    // VehicleSurfaceTypeMask

    Module['DRIVABLE_SURFACE'] = _emscripten_enum_VehicleSurfaceTypeMask_DRIVABLE_SURFACE();

    Module['UNDRIVABLE_SURFACE'] = _emscripten_enum_VehicleSurfaceTypeMask_UNDRIVABLE_SURFACE();

    

    // PxConvexMeshGeometryFlagEnum

    Module['eTIGHT_BOUNDS'] = _emscripten_enum_PxConvexMeshGeometryFlagEnum_eTIGHT_BOUNDS();

    

    // PxConvexFlagEnum

    Module['e16_BIT_INDICES'] = _emscripten_enum_PxConvexFlagEnum_e16_BIT_INDICES();

    Module['eCOMPUTE_CONVEX'] = _emscripten_enum_PxConvexFlagEnum_eCOMPUTE_CONVEX();

    Module['eCHECK_ZERO_AREA_TRIANGLES'] = _emscripten_enum_PxConvexFlagEnum_eCHECK_ZERO_AREA_TRIANGLES();

    Module['eQUANTIZE_INPUT'] = _emscripten_enum_PxConvexFlagEnum_eQUANTIZE_INPUT();

    Module['eDISABLE_MESH_VALIDATION'] = _emscripten_enum_PxConvexFlagEnum_eDISABLE_MESH_VALIDATION();

    Module['ePLANE_SHIFTING'] = _emscripten_enum_PxConvexFlagEnum_ePLANE_SHIFTING();

    Module['eFAST_INERTIA_COMPUTATION'] = _emscripten_enum_PxConvexFlagEnum_eFAST_INERTIA_COMPUTATION();

    Module['eGPU_COMPATIBLE'] = _emscripten_enum_PxConvexFlagEnum_eGPU_COMPATIBLE();

    Module['eSHIFT_VERTICES'] = _emscripten_enum_PxConvexFlagEnum_eSHIFT_VERTICES();

    

    // PxBaseFlagEnum

    Module['eOWNS_MEMORY'] = _emscripten_enum_PxBaseFlagEnum_eOWNS_MEMORY();

    Module['eIS_RELEASABLE'] = _emscripten_enum_PxBaseFlagEnum_eIS_RELEASABLE();

    

    // PxIDENTITYEnum

    Module['PxIdentity'] = _emscripten_enum_PxIDENTITYEnum_PxIdentity();

    

    // PxRevoluteJointFlagEnum

    Module['eLIMIT_ENABLED'] = _emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED();

    Module['eDRIVE_ENABLED'] = _emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED();

    Module['eDRIVE_FREESPIN'] = _emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN();

  }
  if (runtimeInitialized) setupEnums();
  else addOnPreMain(setupEnums);
})();

// Reassign global PhysX to the loaded module:
this['PhysX'] = Module;


  return PhysX.ready
}
);
})();
if (typeof exports === 'object' && typeof module === 'object')
      module.exports = PhysX;
    else if (typeof define === 'function' && define['amd'])
      define([], function() { return PhysX; });
    else if (typeof exports === 'object')
      exports["PhysX"] = PhysX;
    