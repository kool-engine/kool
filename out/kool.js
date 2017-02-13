if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'kool'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kool'.");
}
var kool = function (Kotlin) {
  'use strict';
  var _ = Kotlin.defineRootPackage(null, /** @lends _ */ {
    de: Kotlin.definePackage(null, /** @lends _.de */ {
      fabmax: Kotlin.definePackage(null, /** @lends _.de.fabmax */ {
        kool: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool */ {
          js: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.js */ {
            main_kand9s$: function (args) {
              _.de.fabmax.kool.platform.PlatformImpl.Companion.init();
              var ctx = _.de.fabmax.kool.platform.Platform.Companion.createContext_ihrqo7$(new _.de.fabmax.kool.platform.js.JsContext.InitProps());
              _.de.fabmax.kool.demo.textDemo_qk1xvd$(ctx);
            }
          }),
          platform: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.platform */ {
            js: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.platform.js */ {
              GenericBuffer: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.Buffer];
              }, function GenericBuffer(capacity, create) {
                this.buffer = create();
                this.capacity_3rw3p2$_0 = capacity;
                this.limit_3rw3p2$_0 = capacity;
                this.position_3rw3p2$_0 = 0;
              }, /** @lends _.de.fabmax.kool.platform.js.GenericBuffer.prototype */ {
                capacity: {
                  get: function () {
                    return this.capacity_3rw3p2$_0;
                  }
                },
                limit: {
                  get: function () {
                    return this.limit_3rw3p2$_0;
                  },
                  set: function (value) {
                    if (value < 0 || value > this.capacity) {
                      throw new _.de.fabmax.kool.KoolException('Limit is out of bounds: ' + value + ' (capacity: ' + this.capacity + ')');
                    }
                    this.limit_3rw3p2$_0 = value;
                    if (this.position > value) {
                      this.position = value;
                    }
                  }
                },
                position: {
                  get: function () {
                    return this.position_3rw3p2$_0;
                  },
                  set: function (position_0) {
                    this.position_3rw3p2$_0 = position_0;
                  }
                },
                remaining: {
                  get: function () {
                    return this.limit - this.position;
                  }
                },
                flip: function () {
                  this.limit = this.position;
                  this.position = 0;
                },
                clear: function () {
                  this.limit = this.capacity;
                  this.position = 0;
                }
              }),
              Uint8BufferImpl: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.js.GenericBuffer, _.de.fabmax.kool.platform.Uint8Buffer];
              }, function Uint8BufferImpl(capacity) {
                Uint8BufferImpl.baseInitializer.call(this, capacity, _.de.fabmax.kool.platform.js.Uint8BufferImpl.Uint8BufferImpl$f(capacity));
              }, /** @lends _.de.fabmax.kool.platform.js.Uint8BufferImpl.prototype */ {
                put_mj6st8$: function (data, offset, len) {
                  var tmp$0;
                  tmp$0 = offset + len - 1;
                  for (var i = offset; i <= tmp$0; i++) {
                    this.buffer[this.position++] = data[i];
                  }
                  return this;
                },
                put_za3rmp$: function (value) {
                  this.buffer[this.position++] = value;
                  return this;
                },
                get_za3lpa$: function (i) {
                  return this.buffer[i];
                },
                set_vux3hl$: function (i, value) {
                  this.buffer[i] = value;
                }
              }, /** @lends _.de.fabmax.kool.platform.js.Uint8BufferImpl */ {
                Uint8BufferImpl$f: function (closure$capacity) {
                  return function () {
                    return new Uint8Array(closure$capacity);
                  };
                }
              }),
              Uint16BufferImpl: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.js.GenericBuffer, _.de.fabmax.kool.platform.Uint16Buffer];
              }, function Uint16BufferImpl(capacity) {
                Uint16BufferImpl.baseInitializer.call(this, capacity, _.de.fabmax.kool.platform.js.Uint16BufferImpl.Uint16BufferImpl$f(capacity));
              }, /** @lends _.de.fabmax.kool.platform.js.Uint16BufferImpl.prototype */ {
                put_359eei$: function (data, offset, len) {
                  var tmp$0;
                  tmp$0 = offset + len - 1;
                  for (var i = offset; i <= tmp$0; i++) {
                    this.buffer[this.position++] = data[i];
                  }
                  return this;
                },
                put_za3rmp$: function (value) {
                  this.buffer[this.position++] = value;
                  return this;
                },
                get_za3lpa$: function (i) {
                  return this.buffer[i];
                },
                set_vux3hl$: function (i, value) {
                  this.buffer[i] = value;
                }
              }, /** @lends _.de.fabmax.kool.platform.js.Uint16BufferImpl */ {
                Uint16BufferImpl$f: function (closure$capacity) {
                  return function () {
                    return new Uint16Array(closure$capacity);
                  };
                }
              }),
              Uint32BufferImpl: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.js.GenericBuffer, _.de.fabmax.kool.platform.Uint32Buffer];
              }, function Uint32BufferImpl(capacity) {
                Uint32BufferImpl.baseInitializer.call(this, capacity, _.de.fabmax.kool.platform.js.Uint32BufferImpl.Uint32BufferImpl$f(capacity));
              }, /** @lends _.de.fabmax.kool.platform.js.Uint32BufferImpl.prototype */ {
                put_nd5v6f$: function (data, offset, len) {
                  var tmp$0;
                  tmp$0 = offset + len - 1;
                  for (var i = offset; i <= tmp$0; i++) {
                    this.buffer[this.position++] = data[i];
                  }
                  return this;
                },
                put_za3rmp$: function (value) {
                  this.buffer[this.position++] = value;
                  return this;
                },
                get_za3lpa$: function (i) {
                  return this.buffer[i];
                },
                set_vux3hl$: function (i, value) {
                  this.buffer[i] = value;
                }
              }, /** @lends _.de.fabmax.kool.platform.js.Uint32BufferImpl */ {
                Uint32BufferImpl$f: function (closure$capacity) {
                  return function () {
                    return new Uint32Array(closure$capacity);
                  };
                }
              }),
              Float32BufferImpl: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.js.GenericBuffer, _.de.fabmax.kool.platform.Float32Buffer];
              }, function Float32BufferImpl(capacity) {
                Float32BufferImpl.baseInitializer.call(this, capacity, _.de.fabmax.kool.platform.js.Float32BufferImpl.Float32BufferImpl$f(capacity));
              }, /** @lends _.de.fabmax.kool.platform.js.Float32BufferImpl.prototype */ {
                put_kgymra$: function (data, offset, len) {
                  var tmp$0;
                  tmp$0 = offset + len - 1;
                  for (var i = offset; i <= tmp$0; i++) {
                    this.buffer[this.position++] = data[i];
                  }
                  return this;
                },
                put_za3rmp$: function (value) {
                  this.buffer[this.position++] = value;
                  return this;
                },
                get_za3lpa$: function (i) {
                  return this.buffer[i];
                },
                set_vux3hl$: function (i, value) {
                  this.buffer[i] = value;
                }
              }, /** @lends _.de.fabmax.kool.platform.js.Float32BufferImpl */ {
                Float32BufferImpl$f: function (closure$capacity) {
                  return function () {
                    return new Float32Array(closure$capacity);
                  };
                }
              }),
              FontMapGenerator: Kotlin.createClass(null, function FontMapGenerator() {
                var tmp$0, tmp$1;
                this.canvas_0 = Kotlin.isType(tmp$0 = document.createElement('canvas'), HTMLCanvasElement) ? tmp$0 : Kotlin.throwCCE();
                this.canvas_0.width = _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH;
                this.canvas_0.height = _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT;
                this.canvasCtx_0 = Kotlin.isType(tmp$1 = this.canvas_0.getContext('2d'), CanvasRenderingContext2D) ? tmp$1 : Kotlin.throwCCE();
              }, /** @lends _.de.fabmax.kool.platform.js.FontMapGenerator.prototype */ {
                createCharMap_34dg9o$: function (font, chars) {
                  var tmp$0;
                  this.canvasCtx_0.fillStyle = 'transparent';
                  this.canvasCtx_0.fillRect(0.0, 0.0, _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH, _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT);
                  var style = '';
                  if ((font.style & _.de.fabmax.kool.util.Font.Companion.BOLD) !== 0) {
                    style = 'bold ';
                  }
                  if ((font.style & _.de.fabmax.kool.util.Font.Companion.ITALIC) !== 0) {
                    style += 'italic ';
                  }
                  var t = _.de.fabmax.kool.platform.Platform.Companion.currentTimeMillis();
                  var metrics = this.makeMap_0(chars, font.family, font.size, style);
                  var props = new _.de.fabmax.kool.TextureResource.Props(_.de.fabmax.kool.platform.GL.Companion.LINEAR, _.de.fabmax.kool.platform.GL.Companion.LINEAR, _.de.fabmax.kool.platform.GL.Companion.CLAMP_TO_EDGE, _.de.fabmax.kool.platform.GL.Companion.CLAMP_TO_EDGE);
                  var data = this.canvasCtx_0.getImageData(0.0, 0.0, _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH, _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT);
                  var buffer = _.de.fabmax.kool.platform.Platform.Companion.createUint8Buffer_za3lpa$(_.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH * _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT * 4);
                  tmp$0 = buffer.capacity - 1;
                  for (var i = 0; i <= tmp$0; i++) {
                    buffer.put_za3rmp$(data.data[i]);
                  }
                  var map = new _.de.fabmax.kool.util.CharMap(new _.de.fabmax.kool.BufferedTexture2d(buffer, _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH, _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT, _.de.fabmax.kool.platform.GL.Companion.RGBA, props), metrics);
                  Kotlin.println('generated font tex: ' + font.family + ' ' + style + ' ' + font.size + 'px, took ' + _.de.fabmax.kool.platform.Platform.Companion.currentTimeMillis().subtract(t) + 'ms');
                  return map;
                },
                makeMap_0: function (chars, family, size, style) {
                  var tmp$0;
                  this.canvasCtx_0.font = style + size + 'px ' + '"' + family + '"';
                  this.canvasCtx_0.fillStyle = '#ffffff';
                  this.canvasCtx_0.strokeStyle = '#ff0000';
                  var padding = 3.0;
                  var hab = Math.round(size * 1.1);
                  var hbb = Math.round(size * 0.5);
                  var height = Math.round(size * 1.6);
                  var map = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
                  var x = 0.0;
                  var y = hab;
                  tmp$0 = Kotlin.kotlin.text.iterator_gw00vq$(chars);
                  while (tmp$0.hasNext()) {
                    var c = tmp$0.next();
                    var txt = c.toString();
                    var charW = this.canvasCtx_0.measureText(txt).width;
                    var paddedWidth = Math.round(charW + padding * 2);
                    if (x + paddedWidth > _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH) {
                      x = 0.0;
                      y += height + 10;
                      if (y + hbb > _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT) {
                        break;
                      }
                    }
                    var metrics = new _.de.fabmax.kool.util.CharMetrics();
                    metrics.width = charW;
                    metrics.height = height;
                    metrics.xOffset = 0.0;
                    metrics.yBaseline = hab;
                    metrics.advance = charW;
                    metrics.uvMin.set_dleff0$((x + padding) / _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH, (y - hab) / _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT);
                    metrics.uvMax.set_dleff0$((x + padding + metrics.width) / _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH, (y - hab + metrics.height) / _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT);
                    map.put_wn2jw4$(c, metrics);
                    this.canvasCtx_0.fillText(txt, x + padding, y);
                    x += paddedWidth;
                  }
                  return map;
                }
              }, /** @lends _.de.fabmax.kool.platform.js.FontMapGenerator */ {
                Companion: Kotlin.createObject(null, function Companion() {
                  _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_WIDTH = 1024;
                  _.de.fabmax.kool.platform.js.FontMapGenerator.Companion.MAXIMUM_TEX_HEIGHT = 1024;
                }),
                object_initializer$: function () {
                  _.de.fabmax.kool.platform.js.FontMapGenerator.Companion;
                }
              }),
              JsContext: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.RenderContext];
              }, function JsContext(props) {
                JsContext.baseInitializer.call(this);
                var tmp$0, tmp$1;
                this.canvas_0 = Kotlin.isType(tmp$0 = document.getElementById(props.canvasName), HTMLCanvasElement) ? tmp$0 : Kotlin.throwCCE();
                var webGlCtx = this.canvas_0.getContext('webgl');
                if (webGlCtx == null) {
                  webGlCtx = this.canvas_0.getContext('experimental-webgl');
                  if (webGlCtx == null) {
                    alert('Unable to initialize WebGL. Your browser may not support it.');
                  }
                }
                this.gl_0 = Kotlin.isType(tmp$1 = webGlCtx, WebGLRenderingContext) ? tmp$1 : Kotlin.throwCCE();
                this.supportsUint32Indices_0 = this.gl_0.getExtension('OES_element_index_uint') != null;
                this.gl_0.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, _.de.fabmax.kool.platform.GL.Companion.TRUE);
                this.viewportWidth = this.canvas_0.width;
                this.viewportHeight = this.canvas_0.height;
                this.canvas_0.onmousemove = _.de.fabmax.kool.platform.js.JsContext.JsContext$f(this);
                this.canvas_0.onmousedown = _.de.fabmax.kool.platform.js.JsContext.JsContext$f_0(this);
                this.canvas_0.onmouseup = _.de.fabmax.kool.platform.js.JsContext.JsContext$f_1(this);
                this.canvas_0.onmouseenter = _.de.fabmax.kool.platform.js.JsContext.JsContext$f_2(this);
                this.canvas_0.onmouseleave = _.de.fabmax.kool.platform.js.JsContext.JsContext$f_3(this);
                this.canvas_0.onwheel = _.de.fabmax.kool.platform.js.JsContext.JsContext$f_4(this);
              }, /** @lends _.de.fabmax.kool.platform.js.JsContext.prototype */ {
                run: function () {
                  setInterval(_.de.fabmax.kool.platform.js.JsContext.Companion.webGlRender, 15);
                },
                render: function () {
                  this.viewportWidth = this.canvas_0.clientWidth;
                  this.viewportHeight = this.canvas_0.clientHeight;
                  if (this.viewportWidth !== this.canvas_0.width || this.viewportHeight !== this.canvas_0.height) {
                    this.canvas_0.width = this.viewportWidth;
                    this.canvas_0.height = this.viewportHeight;
                  }
                  _.de.fabmax.kool.platform.RenderContext.prototype.render.call(this);
                },
                destroy: function () {
                }
              }, /** @lends _.de.fabmax.kool.platform.js.JsContext */ {
                Companion: Kotlin.createObject(null, function Companion() {
                }, /** @lends _.de.fabmax.kool.platform.js.JsContext.Companion.prototype */ {
                  webGlRender: function () {
                    var tmp$0;
                    ((tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.jsContext_0) != null ? tmp$0 : Kotlin.throwNPE()).render();
                    _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.finish();
                  }
                }),
                object_initializer$: function () {
                  _.de.fabmax.kool.platform.js.JsContext.Companion;
                },
                InitProps: Kotlin.createClass(function () {
                  return [_.de.fabmax.kool.platform.RenderContext.InitProps];
                }, function InitProps() {
                  InitProps.baseInitializer.call(this);
                  this.canvasName = 'glCanvas';
                }),
                JsContext$f: function (this$JsContext) {
                  return function (ev) {
                    var tmp$0;
                    Kotlin.isType(tmp$0 = ev, MouseEvent) ? tmp$0 : Kotlin.throwCCE();
                    var bounds = this$JsContext.canvas_0.getBoundingClientRect();
                    this$JsContext.inputHandler.updatePointerPos_w4xg1m$(_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER, ev.clientX - bounds.left, ev.clientY - bounds.top);
                  };
                },
                JsContext$f_0: function (this$JsContext) {
                  return function (ev) {
                    var tmp$0;
                    Kotlin.isType(tmp$0 = ev, MouseEvent) ? tmp$0 : Kotlin.throwCCE();
                    this$JsContext.inputHandler.updatePointerButtonStates_vux9f0$(_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER, ev.buttons);
                  };
                },
                JsContext$f_1: function (this$JsContext) {
                  return function (ev) {
                    var tmp$0;
                    Kotlin.isType(tmp$0 = ev, MouseEvent) ? tmp$0 : Kotlin.throwCCE();
                    this$JsContext.inputHandler.updatePointerButtonStates_vux9f0$(_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER, ev.buttons);
                  };
                },
                JsContext$f_2: function (this$JsContext) {
                  return function (ev) {
                    this$JsContext.inputHandler.updatePointerValid_fzusl$(_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER, true);
                  };
                },
                JsContext$f_3: function (this$JsContext) {
                  return function (ev) {
                    this$JsContext.inputHandler.updatePointerValid_fzusl$(_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER, false);
                  };
                },
                JsContext$f_4: function (this$JsContext) {
                  return function (ev) {
                    var tmp$0;
                    Kotlin.isType(tmp$0 = ev, WheelEvent) ? tmp$0 : Kotlin.throwCCE();
                    var ticks = ev.deltaY / 3.0;
                    if (ev.deltaMode === 0) {
                      ticks /= 30;
                    }
                    this$JsContext.inputHandler.updatePointerScrollPos_5wr77w$(_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER, ticks);
                    return false;
                  };
                }
              }),
              WebGlImpl: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.GL.Impl];
              }, function WebGlImpl() {
              }, /** @lends _.de.fabmax.kool.platform.js.WebGlImpl.prototype */ {
                isAvailable: function () {
                  return true;
                },
                activeTexture_za3lpa$: function (texture) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.activeTexture(texture);
                },
                attachShader_bb91mv$: function (program, shader) {
                  var tmp$0, tmp$1;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.attachShader(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE(), Kotlin.isType(tmp$1 = shader.glRef, WebGLShader) ? tmp$1 : Kotlin.throwCCE());
                },
                bindBuffer_5qhhd9$: function (target, buffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bindBuffer(target, (tmp$0 = buffer != null ? buffer.glRef : null) == null || Kotlin.isType(tmp$0, WebGLBuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                bindFramebuffer_ean2c$: function (target, framebuffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bindFramebuffer(target, (tmp$0 = framebuffer != null ? framebuffer.glRef : null) == null || Kotlin.isType(tmp$0, WebGLFramebuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                bindRenderbuffer_oni39f$: function (target, renderbuffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bindRenderbuffer(target, (tmp$0 = renderbuffer != null ? renderbuffer.glRef : null) == null || Kotlin.isType(tmp$0, WebGLRenderbuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                bindTexture_qdzudy$: function (target, texture) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bindTexture(target, (tmp$0 = texture != null ? texture.glRef : null) == null || Kotlin.isType(tmp$0, WebGLTexture) ? tmp$0 : Kotlin.throwCCE());
                },
                blendFunc_vux9f0$: function (sfactor, dfactor) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.blendFunc(sfactor, dfactor);
                },
                bufferData_9rgdn0$: function (target, data, usage) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bufferData(target, (Kotlin.isType(tmp$0 = data, _.de.fabmax.kool.platform.js.Uint8BufferImpl) ? tmp$0 : Kotlin.throwCCE()).buffer, usage);
                },
                bufferData_4jc8ml$: function (target, data, usage) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bufferData(target, (Kotlin.isType(tmp$0 = data, _.de.fabmax.kool.platform.js.Uint16BufferImpl) ? tmp$0 : Kotlin.throwCCE()).buffer, usage);
                },
                bufferData_xhmpuh$: function (target, data, usage) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bufferData(target, (Kotlin.isType(tmp$0 = data, _.de.fabmax.kool.platform.js.Uint32BufferImpl) ? tmp$0 : Kotlin.throwCCE()).buffer, usage);
                },
                bufferData_axcqa7$: function (target, data, usage) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.bufferData(target, (Kotlin.isType(tmp$0 = data, _.de.fabmax.kool.platform.js.Float32BufferImpl) ? tmp$0 : Kotlin.throwCCE()).buffer, usage);
                },
                clear_za3lpa$: function (mask) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.clear(mask);
                },
                clearColor_7b5o5w$: function (red, green, blue, alpha) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.clearColor(red, green, blue, alpha);
                },
                compileShader_as565g$: function (shader) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.compileShader(Kotlin.isType(tmp$0 = shader.glRef, WebGLShader) ? tmp$0 : Kotlin.throwCCE());
                },
                createBuffer: function () {
                  var tmp$0;
                  return (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.createBuffer()) != null ? tmp$0 : Kotlin.throwNPE();
                },
                createFramebuffer: function () {
                  var tmp$0;
                  return (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.createFramebuffer()) != null ? tmp$0 : Kotlin.throwNPE();
                },
                createRenderbuffer: function () {
                  var tmp$0;
                  return (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.createRenderbuffer()) != null ? tmp$0 : Kotlin.throwNPE();
                },
                createProgram: function () {
                  var tmp$0;
                  return (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.createProgram()) != null ? tmp$0 : Kotlin.throwNPE();
                },
                createShader_za3lpa$: function (type) {
                  var tmp$0;
                  return (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.createShader(type)) != null ? tmp$0 : Kotlin.throwNPE();
                },
                createTexture: function () {
                  var tmp$0;
                  return (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.createTexture()) != null ? tmp$0 : Kotlin.throwNPE();
                },
                deleteBuffer_jifzef$: function (buffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.deleteBuffer(Kotlin.isType(tmp$0 = buffer.glRef, WebGLBuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                deleteFramebuffer_4s0d2q$: function (framebuffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.deleteFramebuffer(Kotlin.isType(tmp$0 = framebuffer.glRef, WebGLFramebuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                deleteProgram_slyaif$: function (program) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.deleteProgram(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE());
                },
                deleteRenderbuffer_s0cyb7$: function (renderbuffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.deleteRenderbuffer(Kotlin.isType(tmp$0 = renderbuffer.glRef, WebGLRenderbuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                deleteShader_as565g$: function (shader) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.deleteShader(Kotlin.isType(tmp$0 = shader.glRef, WebGLShader) ? tmp$0 : Kotlin.throwCCE());
                },
                deleteTexture_phyn8g$: function (texture) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.deleteTexture(Kotlin.isType(tmp$0 = texture.glRef, WebGLTexture) ? tmp$0 : Kotlin.throwCCE());
                },
                depthFunc_za3lpa$: function (func) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.depthFunc(func);
                },
                depthMask_6taknv$: function (enabled) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.depthMask(enabled);
                },
                disable_za3lpa$: function (cap) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.disable(cap);
                },
                disableVertexAttribArray_za3lpa$: function (index) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.disableVertexAttribArray(index);
                },
                drawElements_tjonv8$: function (mode, count, type, offset) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.drawElements(mode, count, type, offset);
                },
                drawElementsInstanced_4qozqa$: function (mode, count, type, indicesOffset, instanceCount) {
                  throw new Kotlin.UnsupportedOperationException('not available on WebGL');
                },
                enable_za3lpa$: function (cap) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.enable(cap);
                },
                enableVertexAttribArray_za3lpa$: function (index) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.enableVertexAttribArray(index);
                },
                framebufferRenderbuffer_upsiir$: function (target, attachment, renderbuffertarget, renderbuffer) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.framebufferRenderbuffer(target, attachment, renderbuffertarget, Kotlin.isType(tmp$0 = renderbuffer.glRef, WebGLRenderbuffer) ? tmp$0 : Kotlin.throwCCE());
                },
                framebufferTexture2D_et0qlg$: function (target, attachment, textarget, texture, level) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.framebufferTexture2D(target, attachment, textarget, Kotlin.isType(tmp$0 = texture.glRef, WebGLTexture) ? tmp$0 : Kotlin.throwCCE(), level);
                },
                generateMipmap_za3lpa$: function (target) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.generateMipmap(target);
                },
                getAttribLocation_dlxpy3$: function (program, name) {
                  var tmp$0;
                  return _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getAttribLocation(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE(), name);
                },
                getError: function () {
                  return _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getError();
                },
                getProgrami_i0tefv$: function (program, pname) {
                  var tmp$0, tmp$1;
                  var res = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getProgramParameter(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE(), pname);
                  if (pname === _.de.fabmax.kool.platform.GL.Companion.LINK_STATUS) {
                    if (typeof (tmp$1 = res) === 'boolean' ? tmp$1 : Kotlin.throwCCE()) {
                      return _.de.fabmax.kool.platform.GL.Companion.TRUE;
                    }
                     else {
                      return _.de.fabmax.kool.platform.GL.Companion.FALSE;
                    }
                  }
                  return 0;
                },
                getShaderi_4x9xq$: function (shader, pname) {
                  var tmp$0, tmp$1;
                  var res = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getShaderParameter(Kotlin.isType(tmp$0 = shader.glRef, WebGLShader) ? tmp$0 : Kotlin.throwCCE(), pname);
                  if (pname === _.de.fabmax.kool.platform.GL.Companion.COMPILE_STATUS) {
                    if (typeof (tmp$1 = res) === 'boolean' ? tmp$1 : Kotlin.throwCCE()) {
                      return _.de.fabmax.kool.platform.GL.Companion.TRUE;
                    }
                     else {
                      return _.de.fabmax.kool.platform.GL.Companion.FALSE;
                    }
                  }
                  return 0;
                },
                getProgramInfoLog_slyaif$: function (program) {
                  var tmp$0, tmp$1;
                  return (tmp$1 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getProgramInfoLog(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE())) != null ? tmp$1 : '';
                },
                getShaderInfoLog_as565g$: function (shader) {
                  var tmp$0, tmp$1;
                  return (tmp$1 = _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getShaderInfoLog(Kotlin.isType(tmp$0 = shader.glRef, WebGLShader) ? tmp$0 : Kotlin.throwCCE())) != null ? tmp$1 : '';
                },
                getUniformLocation_dlxpy3$: function (program, name) {
                  var tmp$0;
                  return _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.getUniformLocation(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE(), name);
                },
                lineWidth_mx4ult$: function (width) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.lineWidth(width);
                },
                linkProgram_slyaif$: function (program) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.linkProgram(Kotlin.isType(tmp$0 = program.glRef, WebGLProgram) ? tmp$0 : Kotlin.throwCCE());
                },
                pointSize_mx4ult$: function (size) {
                },
                renderbufferStorage_tjonv8$: function (target, internalformat, width, height) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.renderbufferStorage(target, internalformat, width, height);
                },
                renderbufferStorageMultisample_4qozqa$: function (target, samples, internalformat, width, height) {
                  throw new Kotlin.UnsupportedOperationException('not available on WebGL');
                },
                shaderSource_lfmudu$: function (shader, source) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.shaderSource(Kotlin.isType(tmp$0 = shader.glRef, WebGLShader) ? tmp$0 : Kotlin.throwCCE(), source);
                },
                texImage2D_bz3wcs$: function (target, level, internalformat, width, height, border, format, type, pixels) {
                  var tmp$0, tmp$1;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.texImage2D(target, level, internalformat, width, height, border, format, type, (tmp$1 = (tmp$0 = pixels) == null || Kotlin.isType(tmp$0, _.de.fabmax.kool.platform.js.Uint8BufferImpl) ? tmp$0 : Kotlin.throwCCE()) != null ? tmp$1.buffer : null);
                },
                texImage2D_mbbdyh$: function (target, level, internalformat, format, type, pixels) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.texImage2D(target, level, internalformat, format, type, (tmp$0 = pixels) == null || Kotlin.isType(tmp$0, HTMLImageElement) ? tmp$0 : Kotlin.throwCCE());
                },
                texParameteri_qt1dr2$: function (target, pname, param) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.texParameteri(target, pname, param);
                },
                uniform1f_rvcsvw$: function (location, x) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.uniform1f((tmp$0 = location) == null || Kotlin.isType(tmp$0, WebGLUniformLocation) ? tmp$0 : Kotlin.throwCCE(), x);
                },
                uniform1i_wn2dyp$: function (location, x) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.uniform1i((tmp$0 = location) == null || Kotlin.isType(tmp$0, WebGLUniformLocation) ? tmp$0 : Kotlin.throwCCE(), x);
                },
                uniform2f_zcqyrj$: function (location, x, y) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.uniform2f((tmp$0 = location) == null || Kotlin.isType(tmp$0, WebGLUniformLocation) ? tmp$0 : Kotlin.throwCCE(), x, y);
                },
                uniform3f_ig0gt8$: function (location, x, y, z) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.uniform3f((tmp$0 = location) == null || Kotlin.isType(tmp$0, WebGLUniformLocation) ? tmp$0 : Kotlin.throwCCE(), x, y, z);
                },
                uniform4f_k644h$: function (location, x, y, z, w) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.uniform4f((tmp$0 = location) == null || Kotlin.isType(tmp$0, WebGLUniformLocation) ? tmp$0 : Kotlin.throwCCE(), x, y, z, w);
                },
                uniformMatrix4fv_rrdrnp$: function (location, transpose, value) {
                  var tmp$0, tmp$1;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.uniformMatrix4fv((tmp$0 = location) == null || Kotlin.isType(tmp$0, WebGLUniformLocation) ? tmp$0 : Kotlin.throwCCE(), transpose, (Kotlin.isType(tmp$1 = value, _.de.fabmax.kool.platform.js.Float32BufferImpl) ? tmp$1 : Kotlin.throwCCE()).buffer);
                },
                useProgram_slyaif$: function (program) {
                  var tmp$0;
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.useProgram((tmp$0 = program != null ? program.glRef : null) == null || Kotlin.isType(tmp$0, WebGLProgram) ? tmp$0 : Kotlin.throwCCE());
                },
                vertexAttribDivisor_vux9f0$: function (index, divisor) {
                  throw new Kotlin.UnsupportedOperationException('not available on WebGL');
                },
                vertexAttribPointer_owihk5$: function (indx, size, type, normalized, stride, offset) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.vertexAttribPointer(indx, size, type, normalized, stride, offset);
                },
                viewport_tjonv8$: function (x, y, width, height) {
                  _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.viewport(x, y, width, height);
                }
              }, /** @lends _.de.fabmax.kool.platform.js.WebGlImpl */ {
                Companion: Kotlin.createObject(null, function Companion() {
                  _.de.fabmax.kool.platform.js.WebGlImpl.Companion.instance = new _.de.fabmax.kool.platform.js.WebGlImpl();
                }),
                object_initializer$: function () {
                  _.de.fabmax.kool.platform.js.WebGlImpl.Companion;
                }
              })
            }),
            PlatformImpl: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.platform.Platform];
            }, function PlatformImpl() {
              PlatformImpl.baseInitializer.call(this);
              this.fontGenerator_0 = new _.de.fabmax.kool.platform.js.FontMapGenerator();
              this.supportsMultiContext_izw80b$_0 = false;
            }, /** @lends _.de.fabmax.kool.platform.PlatformImpl.prototype */ {
              supportsMultiContext: {
                get: function () {
                  return this.supportsMultiContext_izw80b$_0;
                }
              },
              supportsUint32Indices: {
                get: function () {
                  var tmp$0, tmp$1;
                  tmp$1 = (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.jsContext_0) != null ? tmp$0.supportsUint32Indices_0 : null;
                  if (tmp$1 == null) {
                    throw new _.de.fabmax.kool.KoolException('Platform.createContext() not called');
                  }
                  return tmp$1;
                }
              },
              createContext_ihrqo7$: function (props) {
                var ctx = _.de.fabmax.kool.platform.PlatformImpl.Companion.jsContext_0;
                if (ctx == null) {
                  if (Kotlin.isType(props, _.de.fabmax.kool.platform.js.JsContext.InitProps)) {
                    ctx = new _.de.fabmax.kool.platform.js.JsContext(props);
                    _.de.fabmax.kool.platform.PlatformImpl.Companion.jsContext_0 = ctx;
                  }
                   else {
                    throw new Kotlin.IllegalArgumentException('Props must be of JsContext.InitProps');
                  }
                }
                return ctx;
              },
              createDefaultShaderGenerator: function () {
                return new _.de.fabmax.kool.util.GlslGenerator(new _.de.fabmax.kool.platform.PlatformImpl.createDefaultShaderGenerator$f());
              },
              getGlImpl: function () {
                return _.de.fabmax.kool.platform.js.WebGlImpl.Companion.instance;
              },
              createUint8Buffer_za3lpa$: function (capacity) {
                return new _.de.fabmax.kool.platform.js.Uint8BufferImpl(capacity);
              },
              createUint16Buffer_za3lpa$: function (capacity) {
                return new _.de.fabmax.kool.platform.js.Uint16BufferImpl(capacity);
              },
              createUint32Buffer_za3lpa$: function (capacity) {
                return new _.de.fabmax.kool.platform.js.Uint32BufferImpl(capacity);
              },
              createFloat32Buffer_za3lpa$: function (capacity) {
                return new _.de.fabmax.kool.platform.js.Float32BufferImpl(capacity);
              },
              currentTimeMillis: function () {
                return Kotlin.Long.fromInt((new Date()).getTime());
              },
              loadTexture_2lkaa8$: function (path, props) {
                var img = new Image();
                var data = new _.de.fabmax.kool.platform.ImageTexture2d(img, props);
                img.src = path;
                return data;
              },
              createCharMap_34dg9o$: function (font, chars) {
                return this.fontGenerator_0.createCharMap_34dg9o$(font, chars);
              }
            }, /** @lends _.de.fabmax.kool.platform.PlatformImpl */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.platform.PlatformImpl.Companion.jsContext_0 = null;
              }, /** @lends _.de.fabmax.kool.platform.PlatformImpl.Companion.prototype */ {
                gl_0: {
                  get: function () {
                    var tmp$0, tmp$1;
                    tmp$1 = (tmp$0 = _.de.fabmax.kool.platform.PlatformImpl.Companion.jsContext_0) != null ? tmp$0.gl_0 : null;
                    if (tmp$1 == null) {
                      throw new _.de.fabmax.kool.KoolException('Platform.createContext() not called');
                    }
                    return tmp$1;
                  }
                },
                init: function () {
                  _.de.fabmax.kool.platform.Platform.Companion.initPlatform_rbiqat$(new _.de.fabmax.kool.platform.PlatformImpl());
                }
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.platform.PlatformImpl.Companion;
              },
              createDefaultShaderGenerator$f: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.util.GlslGenerator.Customization];
              }, function () {
              }, /** @lends _.de.fabmax.kool.platform.PlatformImpl.createDefaultShaderGenerator$f.prototype */ {
                fragmentShaderStart_ktj0m0$: function (shaderProps, text_0) {
                  text_0.append('precision highp float;');
                }
              }, /** @lends _.de.fabmax.kool.platform.PlatformImpl.createDefaultShaderGenerator$f */ {
              })
            }),
            ImageTexture2d: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.Texture2d];
            }, function ImageTexture2d(image, props) {
              ImageTexture2d.baseInitializer.call(this, props);
              this.image = image;
            }, /** @lends _.de.fabmax.kool.platform.ImageTexture2d.prototype */ {
              isAvailable: {
                get: function () {
                  return this.image.complete;
                },
                set: function (value) {
                }
              },
              loadData_d9oqyh$: function (target, level, ctx) {
                var tmp$0;
                var av = this.isAvailable;
                _.de.fabmax.kool.platform.PlatformImpl.Companion.gl_0.texImage2D(target, level, _.de.fabmax.kool.platform.GL.Companion.RGBA, _.de.fabmax.kool.platform.GL.Companion.RGBA, _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_BYTE, this.image);
                if (!av) {
                  Kotlin.println('loadData() called although not available!');
                }
                var size = this.image.width * this.image.height * 4;
                ctx.memoryMgr.memoryAllocated_0((tmp$0 = this.res) != null ? tmp$0 : Kotlin.throwNPE(), size);
              }
            }),
            Buffer: Kotlin.createTrait(null, /** @lends _.de.fabmax.kool.platform.Buffer.prototype */ {
              plusAssign_za3rmp$: function (value) {
                this.put_za3rmp$(value);
              }
            }),
            Uint8Buffer: Kotlin.createTrait(function () {
              return [_.de.fabmax.kool.platform.Buffer];
            }, /** @lends _.de.fabmax.kool.platform.Uint8Buffer.prototype */ {
              put_fqrh44$: function (data) {
                return this.put_mj6st8$(data, 0, data.length);
              }
            }),
            Uint16Buffer: Kotlin.createTrait(function () {
              return [_.de.fabmax.kool.platform.Buffer];
            }, /** @lends _.de.fabmax.kool.platform.Uint16Buffer.prototype */ {
              put_gmedm2$: function (data) {
                return this.put_359eei$(data, 0, data.length);
              }
            }),
            Uint32Buffer: Kotlin.createTrait(function () {
              return [_.de.fabmax.kool.platform.Buffer];
            }, /** @lends _.de.fabmax.kool.platform.Uint32Buffer.prototype */ {
              put_q5rwfd$: function (data) {
                return this.put_nd5v6f$(data, 0, data.length);
              }
            }),
            Float32Buffer: Kotlin.createTrait(function () {
              return [_.de.fabmax.kool.platform.Buffer];
            }, /** @lends _.de.fabmax.kool.platform.Float32Buffer.prototype */ {
              put_q3cr5i$: function (data) {
                return this.put_kgymra$(data, 0, data.length);
              }
            }),
            GL: Kotlin.createClass(null, function GL() {
            }, null, /** @lends _.de.fabmax.kool.platform.GL */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.platform.GL.Companion.impl_0 = _.de.fabmax.kool.platform.Platform.Companion.getGlImpl();
                _.de.fabmax.kool.platform.GL.Companion.ACTIVE_TEXTURE = 34016;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_BUFFER_BIT = 256;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BUFFER_BIT = 1024;
                _.de.fabmax.kool.platform.GL.Companion.COLOR_BUFFER_BIT = 16384;
                _.de.fabmax.kool.platform.GL.Companion.FALSE = 0;
                _.de.fabmax.kool.platform.GL.Companion.TRUE = 1;
                _.de.fabmax.kool.platform.GL.Companion.POINTS = 0;
                _.de.fabmax.kool.platform.GL.Companion.LINES = 1;
                _.de.fabmax.kool.platform.GL.Companion.LINE_LOOP = 2;
                _.de.fabmax.kool.platform.GL.Companion.LINE_STRIP = 3;
                _.de.fabmax.kool.platform.GL.Companion.TRIANGLES = 4;
                _.de.fabmax.kool.platform.GL.Companion.TRIANGLE_STRIP = 5;
                _.de.fabmax.kool.platform.GL.Companion.TRIANGLE_FAN = 6;
                _.de.fabmax.kool.platform.GL.Companion.ZERO = 0;
                _.de.fabmax.kool.platform.GL.Companion.ONE = 1;
                _.de.fabmax.kool.platform.GL.Companion.SRC_COLOR = 768;
                _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_SRC_COLOR = 769;
                _.de.fabmax.kool.platform.GL.Companion.SRC_ALPHA = 770;
                _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_SRC_ALPHA = 771;
                _.de.fabmax.kool.platform.GL.Companion.DST_ALPHA = 772;
                _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_DST_ALPHA = 773;
                _.de.fabmax.kool.platform.GL.Companion.DST_COLOR = 774;
                _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_DST_COLOR = 775;
                _.de.fabmax.kool.platform.GL.Companion.SRC_ALPHA_SATURATE = 776;
                _.de.fabmax.kool.platform.GL.Companion.FUNC_ADD = 32774;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_EQUATION = 32777;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_EQUATION_RGB = 32777;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_EQUATION_ALPHA = 34877;
                _.de.fabmax.kool.platform.GL.Companion.FUNC_SUBTRACT = 32778;
                _.de.fabmax.kool.platform.GL.Companion.FUNC_REVERSE_SUBTRACT = 32779;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_DST_RGB = 32968;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_SRC_RGB = 32969;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_DST_ALPHA = 32970;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_SRC_ALPHA = 32971;
                _.de.fabmax.kool.platform.GL.Companion.CONSTANT_COLOR = 32769;
                _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_CONSTANT_COLOR = 32770;
                _.de.fabmax.kool.platform.GL.Companion.CONSTANT_ALPHA = 32771;
                _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_CONSTANT_ALPHA = 32772;
                _.de.fabmax.kool.platform.GL.Companion.BLEND_COLOR = 32773;
                _.de.fabmax.kool.platform.GL.Companion.ARRAY_BUFFER = 34962;
                _.de.fabmax.kool.platform.GL.Companion.ELEMENT_ARRAY_BUFFER = 34963;
                _.de.fabmax.kool.platform.GL.Companion.ARRAY_BUFFER_BINDING = 34964;
                _.de.fabmax.kool.platform.GL.Companion.ELEMENT_ARRAY_BUFFER_BINDING = 34965;
                _.de.fabmax.kool.platform.GL.Companion.STREAM_DRAW = 35040;
                _.de.fabmax.kool.platform.GL.Companion.STATIC_DRAW = 35044;
                _.de.fabmax.kool.platform.GL.Companion.DYNAMIC_DRAW = 35048;
                _.de.fabmax.kool.platform.GL.Companion.BUFFER_SIZE = 34660;
                _.de.fabmax.kool.platform.GL.Companion.BUFFER_USAGE = 34661;
                _.de.fabmax.kool.platform.GL.Companion.CURRENT_VERTEX_ATTRIB = 34342;
                _.de.fabmax.kool.platform.GL.Companion.FRONT = 1028;
                _.de.fabmax.kool.platform.GL.Companion.BACK = 1029;
                _.de.fabmax.kool.platform.GL.Companion.FRONT_AND_BACK = 1032;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_2D = 3553;
                _.de.fabmax.kool.platform.GL.Companion.CULL_FACE = 2884;
                _.de.fabmax.kool.platform.GL.Companion.BLEND = 3042;
                _.de.fabmax.kool.platform.GL.Companion.DITHER = 3024;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_TEST = 2960;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_TEST = 2929;
                _.de.fabmax.kool.platform.GL.Companion.SCISSOR_TEST = 3089;
                _.de.fabmax.kool.platform.GL.Companion.POLYGON_OFFSET_FILL = 32823;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLE_ALPHA_TO_COVERAGE = 32926;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLE_COVERAGE = 32928;
                _.de.fabmax.kool.platform.GL.Companion.NO_ERROR = 0;
                _.de.fabmax.kool.platform.GL.Companion.INVALID_ENUM = 1280;
                _.de.fabmax.kool.platform.GL.Companion.INVALID_VALUE = 1281;
                _.de.fabmax.kool.platform.GL.Companion.INVALID_OPERATION = 1282;
                _.de.fabmax.kool.platform.GL.Companion.OUT_OF_MEMORY = 1285;
                _.de.fabmax.kool.platform.GL.Companion.CW = 2304;
                _.de.fabmax.kool.platform.GL.Companion.CCW = 2305;
                _.de.fabmax.kool.platform.GL.Companion.LINE_WIDTH = 2849;
                _.de.fabmax.kool.platform.GL.Companion.ALIASED_POINT_SIZE_RANGE = 33901;
                _.de.fabmax.kool.platform.GL.Companion.ALIASED_LINE_WIDTH_RANGE = 33902;
                _.de.fabmax.kool.platform.GL.Companion.CULL_FACE_MODE = 2885;
                _.de.fabmax.kool.platform.GL.Companion.FRONT_FACE = 2886;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_RANGE = 2928;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_WRITEMASK = 2930;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_CLEAR_VALUE = 2931;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_FUNC = 2932;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_CLEAR_VALUE = 2961;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_FUNC = 2962;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_FAIL = 2964;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_PASS_DEPTH_FAIL = 2965;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_PASS_DEPTH_PASS = 2966;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_REF = 2967;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_VALUE_MASK = 2963;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_WRITEMASK = 2968;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_FUNC = 34816;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_FAIL = 34817;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_PASS_DEPTH_FAIL = 34818;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_PASS_DEPTH_PASS = 34819;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_REF = 36003;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_VALUE_MASK = 36004;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BACK_WRITEMASK = 36005;
                _.de.fabmax.kool.platform.GL.Companion.VIEWPORT = 2978;
                _.de.fabmax.kool.platform.GL.Companion.SCISSOR_BOX = 3088;
                _.de.fabmax.kool.platform.GL.Companion.COLOR_CLEAR_VALUE = 3106;
                _.de.fabmax.kool.platform.GL.Companion.COLOR_WRITEMASK = 3107;
                _.de.fabmax.kool.platform.GL.Companion.UNPACK_ALIGNMENT = 3317;
                _.de.fabmax.kool.platform.GL.Companion.PACK_ALIGNMENT = 3333;
                _.de.fabmax.kool.platform.GL.Companion.MAX_TEXTURE_SIZE = 3379;
                _.de.fabmax.kool.platform.GL.Companion.MAX_VIEWPORT_DIMS = 3386;
                _.de.fabmax.kool.platform.GL.Companion.SUBPIXEL_BITS = 3408;
                _.de.fabmax.kool.platform.GL.Companion.RED_BITS = 3410;
                _.de.fabmax.kool.platform.GL.Companion.GREEN_BITS = 3411;
                _.de.fabmax.kool.platform.GL.Companion.BLUE_BITS = 3412;
                _.de.fabmax.kool.platform.GL.Companion.ALPHA_BITS = 3413;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_BITS = 3414;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_BITS = 3415;
                _.de.fabmax.kool.platform.GL.Companion.POLYGON_OFFSET_UNITS = 10752;
                _.de.fabmax.kool.platform.GL.Companion.POLYGON_OFFSET_FACTOR = 32824;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_BINDING_2D = 32873;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLE_BUFFERS = 32936;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLES = 32937;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLE_COVERAGE_VALUE = 32938;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLE_COVERAGE_INVERT = 32939;
                _.de.fabmax.kool.platform.GL.Companion.NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
                _.de.fabmax.kool.platform.GL.Companion.COMPRESSED_TEXTURE_FORMATS = 34467;
                _.de.fabmax.kool.platform.GL.Companion.DONT_CARE = 4352;
                _.de.fabmax.kool.platform.GL.Companion.FASTEST = 4353;
                _.de.fabmax.kool.platform.GL.Companion.NICEST = 4354;
                _.de.fabmax.kool.platform.GL.Companion.GENERATE_MIPMAP_HINT = 33170;
                _.de.fabmax.kool.platform.GL.Companion.BYTE = 5120;
                _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_BYTE = 5121;
                _.de.fabmax.kool.platform.GL.Companion.SHORT = 5122;
                _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_SHORT = 5123;
                _.de.fabmax.kool.platform.GL.Companion.INT = 5124;
                _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_INT = 5125;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT = 5126;
                _.de.fabmax.kool.platform.GL.Companion.FIXED = 5132;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_COMPONENT = 6402;
                _.de.fabmax.kool.platform.GL.Companion.ALPHA = 6406;
                _.de.fabmax.kool.platform.GL.Companion.RGB = 6407;
                _.de.fabmax.kool.platform.GL.Companion.RGBA = 6408;
                _.de.fabmax.kool.platform.GL.Companion.LUMINANCE = 6409;
                _.de.fabmax.kool.platform.GL.Companion.LUMINANCE_ALPHA = 6410;
                _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_SHORT_4_4_4_4 = 32819;
                _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_SHORT_5_5_5_1 = 32820;
                _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_SHORT_5_6_5 = 33635;
                _.de.fabmax.kool.platform.GL.Companion.FRAGMENT_SHADER = 35632;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_SHADER = 35633;
                _.de.fabmax.kool.platform.GL.Companion.MAX_VERTEX_ATTRIBS = 34921;
                _.de.fabmax.kool.platform.GL.Companion.MAX_VERTEX_UNIFORM_VECTORS = 36347;
                _.de.fabmax.kool.platform.GL.Companion.MAX_VARYING_VECTORS = 36348;
                _.de.fabmax.kool.platform.GL.Companion.MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
                _.de.fabmax.kool.platform.GL.Companion.MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660;
                _.de.fabmax.kool.platform.GL.Companion.MAX_TEXTURE_IMAGE_UNITS = 34930;
                _.de.fabmax.kool.platform.GL.Companion.MAX_FRAGMENT_UNIFORM_VECTORS = 36349;
                _.de.fabmax.kool.platform.GL.Companion.SHADER_TYPE = 35663;
                _.de.fabmax.kool.platform.GL.Companion.DELETE_STATUS = 35712;
                _.de.fabmax.kool.platform.GL.Companion.LINK_STATUS = 35714;
                _.de.fabmax.kool.platform.GL.Companion.VALIDATE_STATUS = 35715;
                _.de.fabmax.kool.platform.GL.Companion.ATTACHED_SHADERS = 35717;
                _.de.fabmax.kool.platform.GL.Companion.ACTIVE_UNIFORMS = 35718;
                _.de.fabmax.kool.platform.GL.Companion.ACTIVE_UNIFORM_MAX_LENGTH = 35719;
                _.de.fabmax.kool.platform.GL.Companion.ACTIVE_ATTRIBUTES = 35721;
                _.de.fabmax.kool.platform.GL.Companion.ACTIVE_ATTRIBUTE_MAX_LENGTH = 35722;
                _.de.fabmax.kool.platform.GL.Companion.SHADING_LANGUAGE_VERSION = 35724;
                _.de.fabmax.kool.platform.GL.Companion.CURRENT_PROGRAM = 35725;
                _.de.fabmax.kool.platform.GL.Companion.NEVER = 512;
                _.de.fabmax.kool.platform.GL.Companion.LESS = 513;
                _.de.fabmax.kool.platform.GL.Companion.EQUAL = 514;
                _.de.fabmax.kool.platform.GL.Companion.LEQUAL = 515;
                _.de.fabmax.kool.platform.GL.Companion.GREATER = 516;
                _.de.fabmax.kool.platform.GL.Companion.NOTEQUAL = 517;
                _.de.fabmax.kool.platform.GL.Companion.GEQUAL = 518;
                _.de.fabmax.kool.platform.GL.Companion.ALWAYS = 519;
                _.de.fabmax.kool.platform.GL.Companion.KEEP = 7680;
                _.de.fabmax.kool.platform.GL.Companion.REPLACE = 7681;
                _.de.fabmax.kool.platform.GL.Companion.INCR = 7682;
                _.de.fabmax.kool.platform.GL.Companion.DECR = 7683;
                _.de.fabmax.kool.platform.GL.Companion.INVERT = 5386;
                _.de.fabmax.kool.platform.GL.Companion.INCR_WRAP = 34055;
                _.de.fabmax.kool.platform.GL.Companion.DECR_WRAP = 34056;
                _.de.fabmax.kool.platform.GL.Companion.VENDOR = 7936;
                _.de.fabmax.kool.platform.GL.Companion.RENDERER = 7937;
                _.de.fabmax.kool.platform.GL.Companion.VERSION = 7938;
                _.de.fabmax.kool.platform.GL.Companion.EXTENSIONS = 7939;
                _.de.fabmax.kool.platform.GL.Companion.NEAREST = 9728;
                _.de.fabmax.kool.platform.GL.Companion.LINEAR = 9729;
                _.de.fabmax.kool.platform.GL.Companion.NEAREST_MIPMAP_NEAREST = 9984;
                _.de.fabmax.kool.platform.GL.Companion.LINEAR_MIPMAP_NEAREST = 9985;
                _.de.fabmax.kool.platform.GL.Companion.NEAREST_MIPMAP_LINEAR = 9986;
                _.de.fabmax.kool.platform.GL.Companion.LINEAR_MIPMAP_LINEAR = 9987;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_MAG_FILTER = 10240;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_MIN_FILTER = 10241;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_WRAP_S = 10242;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_WRAP_T = 10243;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE = 5890;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP = 34067;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_BINDING_CUBE_MAP = 34068;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
                _.de.fabmax.kool.platform.GL.Companion.MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE0 = 33984;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE1 = 33985;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE2 = 33986;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE3 = 33987;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE4 = 33988;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE5 = 33989;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE6 = 33990;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE7 = 33991;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE8 = 33992;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE9 = 33993;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE10 = 33994;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE11 = 33995;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE12 = 33996;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE13 = 33997;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE14 = 33998;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE15 = 33999;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE16 = 34000;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE17 = 34001;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE18 = 34002;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE19 = 34003;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE20 = 34004;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE21 = 34005;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE22 = 34006;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE23 = 34007;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE24 = 34008;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE25 = 34009;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE26 = 34010;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE27 = 34011;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE28 = 34012;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE29 = 34013;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE30 = 34014;
                _.de.fabmax.kool.platform.GL.Companion.TEXTURE31 = 34015;
                _.de.fabmax.kool.platform.GL.Companion.REPEAT = 10497;
                _.de.fabmax.kool.platform.GL.Companion.CLAMP_TO_EDGE = 33071;
                _.de.fabmax.kool.platform.GL.Companion.MIRRORED_REPEAT = 33648;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT_VEC2 = 35664;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT_VEC3 = 35665;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT_VEC4 = 35666;
                _.de.fabmax.kool.platform.GL.Companion.INT_VEC2 = 35667;
                _.de.fabmax.kool.platform.GL.Companion.INT_VEC3 = 35668;
                _.de.fabmax.kool.platform.GL.Companion.INT_VEC4 = 35669;
                _.de.fabmax.kool.platform.GL.Companion.BOOL = 35670;
                _.de.fabmax.kool.platform.GL.Companion.BOOL_VEC2 = 35671;
                _.de.fabmax.kool.platform.GL.Companion.BOOL_VEC3 = 35672;
                _.de.fabmax.kool.platform.GL.Companion.BOOL_VEC4 = 35673;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT_MAT2 = 35674;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT_MAT3 = 35675;
                _.de.fabmax.kool.platform.GL.Companion.FLOAT_MAT4 = 35676;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLER_2D = 35678;
                _.de.fabmax.kool.platform.GL.Companion.SAMPLER_CUBE = 35680;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_ENABLED = 34338;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_SIZE = 34339;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_STRIDE = 34340;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_TYPE = 34341;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_POINTER = 34373;
                _.de.fabmax.kool.platform.GL.Companion.VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
                _.de.fabmax.kool.platform.GL.Companion.IMPLEMENTATION_COLOR_READ_TYPE = 35738;
                _.de.fabmax.kool.platform.GL.Companion.IMPLEMENTATION_COLOR_READ_FORMAT = 35739;
                _.de.fabmax.kool.platform.GL.Companion.COMPILE_STATUS = 35713;
                _.de.fabmax.kool.platform.GL.Companion.INFO_LOG_LENGTH = 35716;
                _.de.fabmax.kool.platform.GL.Companion.SHADER_SOURCE_LENGTH = 35720;
                _.de.fabmax.kool.platform.GL.Companion.SHADER_COMPILER = 36346;
                _.de.fabmax.kool.platform.GL.Companion.SHADER_BINARY_FORMATS = 36344;
                _.de.fabmax.kool.platform.GL.Companion.NUM_SHADER_BINARY_FORMATS = 36345;
                _.de.fabmax.kool.platform.GL.Companion.LOW_FLOAT = 36336;
                _.de.fabmax.kool.platform.GL.Companion.MEDIUM_FLOAT = 36337;
                _.de.fabmax.kool.platform.GL.Companion.HIGH_FLOAT = 36338;
                _.de.fabmax.kool.platform.GL.Companion.LOW_INT = 36339;
                _.de.fabmax.kool.platform.GL.Companion.MEDIUM_INT = 36340;
                _.de.fabmax.kool.platform.GL.Companion.HIGH_INT = 36341;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER = 36160;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER = 36161;
                _.de.fabmax.kool.platform.GL.Companion.RGBA4 = 32854;
                _.de.fabmax.kool.platform.GL.Companion.RGB5_A1 = 32855;
                _.de.fabmax.kool.platform.GL.Companion.RGB565 = 36194;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_COMPONENT16 = 33189;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_INDEX8 = 36168;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_WIDTH = 36162;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_HEIGHT = 36163;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_INTERNAL_FORMAT = 36164;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_RED_SIZE = 36176;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_GREEN_SIZE = 36177;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_BLUE_SIZE = 36178;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_ALPHA_SIZE = 36179;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_DEPTH_SIZE = 36180;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_STENCIL_SIZE = 36181;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051;
                _.de.fabmax.kool.platform.GL.Companion.COLOR_ATTACHMENT0 = 36064;
                _.de.fabmax.kool.platform.GL.Companion.DEPTH_ATTACHMENT = 36096;
                _.de.fabmax.kool.platform.GL.Companion.STENCIL_ATTACHMENT = 36128;
                _.de.fabmax.kool.platform.GL.Companion.NONE = 0;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_COMPLETE = 36053;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 36057;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_UNSUPPORTED = 36061;
                _.de.fabmax.kool.platform.GL.Companion.FRAMEBUFFER_BINDING = 36006;
                _.de.fabmax.kool.platform.GL.Companion.RENDERBUFFER_BINDING = 36007;
                _.de.fabmax.kool.platform.GL.Companion.MAX_RENDERBUFFER_SIZE = 34024;
                _.de.fabmax.kool.platform.GL.Companion.INVALID_FRAMEBUFFER_OPERATION = 1286;
              }, /** @lends _.de.fabmax.kool.platform.GL.Companion.prototype */ {
                isAvailable: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.isAvailable();
                },
                activeTexture_za3lpa$: function (texture) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.activeTexture_za3lpa$(texture);
                },
                attachShader_bb91mv$: function (program, shader) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.attachShader_bb91mv$(program, shader);
                },
                bindBuffer_5qhhd9$: function (target, buffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bindBuffer_5qhhd9$(target, buffer);
                },
                bindFramebuffer_ean2c$: function (target, framebuffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bindFramebuffer_ean2c$(target, framebuffer);
                },
                bindRenderbuffer_oni39f$: function (target, renderbuffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bindRenderbuffer_oni39f$(target, renderbuffer);
                },
                bindTexture_qdzudy$: function (target, texture) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bindTexture_qdzudy$(target, texture);
                },
                blendFunc_vux9f0$: function (sfactor, dfactor) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.blendFunc_vux9f0$(sfactor, dfactor);
                },
                bufferData_9rgdn0$: function (target, data, usage) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bufferData_9rgdn0$(target, data, usage);
                },
                bufferData_4jc8ml$: function (target, data, usage) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bufferData_4jc8ml$(target, data, usage);
                },
                bufferData_xhmpuh$: function (target, data, usage) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bufferData_xhmpuh$(target, data, usage);
                },
                bufferData_axcqa7$: function (target, data, usage) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.bufferData_axcqa7$(target, data, usage);
                },
                clear_za3lpa$: function (mask) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.clear_za3lpa$(mask);
                },
                clearColor_7b5o5w$: function (red, green, blue, alpha) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.clearColor_7b5o5w$(red, green, blue, alpha);
                },
                compileShader_as565g$: function (shader) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.compileShader_as565g$(shader);
                },
                createBuffer: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.createBuffer();
                },
                createFramebuffer: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.createFramebuffer();
                },
                createRenderbuffer: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.createRenderbuffer();
                },
                createProgram: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.createProgram();
                },
                createShader_za3lpa$: function (type) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.createShader_za3lpa$(type);
                },
                createTexture: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.createTexture();
                },
                deleteBuffer_jifzef$: function (buffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.deleteBuffer_jifzef$(buffer);
                },
                deleteFramebuffer_4s0d2q$: function (framebuffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.deleteFramebuffer_4s0d2q$(framebuffer);
                },
                deleteProgram_slyaif$: function (program) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.deleteProgram_slyaif$(program);
                },
                deleteRenderbuffer_s0cyb7$: function (renderbuffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.deleteRenderbuffer_s0cyb7$(renderbuffer);
                },
                deleteShader_as565g$: function (shader) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.deleteShader_as565g$(shader);
                },
                deleteTexture_phyn8g$: function (texture) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.deleteTexture_phyn8g$(texture);
                },
                depthFunc_za3lpa$: function (func) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.depthFunc_za3lpa$(func);
                },
                depthMask_6taknv$: function (enabled) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.depthMask_6taknv$(enabled);
                },
                disable_za3lpa$: function (cap) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.disable_za3lpa$(cap);
                },
                disableVertexAttribArray_za3lpa$: function (index) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.disableVertexAttribArray_za3lpa$(index);
                },
                drawElements_tjonv8$: function (mode, count, type, offset) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.drawElements_tjonv8$(mode, count, type, offset);
                },
                drawElementsInstanced_4qozqa$: function (mode, count, type, indicesOffset, instanceCount) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.drawElementsInstanced_4qozqa$(mode, count, type, indicesOffset, instanceCount);
                },
                enable_za3lpa$: function (cap) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.enable_za3lpa$(cap);
                },
                enableVertexAttribArray_za3lpa$: function (index) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.enableVertexAttribArray_za3lpa$(index);
                },
                framebufferRenderbuffer_upsiir$: function (target, attachment, renderbuffertarget, renderbuffer) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.framebufferRenderbuffer_upsiir$(target, attachment, renderbuffertarget, renderbuffer);
                },
                framebufferTexture2D_et0qlg$: function (target, attachment, textarget, texture, level) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.framebufferTexture2D_et0qlg$(target, attachment, textarget, texture, level);
                },
                generateMipmap_za3lpa$: function (target) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.generateMipmap_za3lpa$(target);
                },
                getAttribLocation_dlxpy3$: function (program, name) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getAttribLocation_dlxpy3$(program, name);
                },
                getError: function () {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getError();
                },
                getProgrami_i0tefv$: function (program, pname) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getProgrami_i0tefv$(program, pname);
                },
                getShaderi_4x9xq$: function (shader, pname) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getShaderi_4x9xq$(shader, pname);
                },
                getProgramInfoLog_slyaif$: function (program) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getProgramInfoLog_slyaif$(program);
                },
                getShaderInfoLog_as565g$: function (shader) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getShaderInfoLog_as565g$(shader);
                },
                getUniformLocation_dlxpy3$: function (program, name) {
                  return _.de.fabmax.kool.platform.GL.Companion.impl_0.getUniformLocation_dlxpy3$(program, name);
                },
                lineWidth_mx4ult$: function (width) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.lineWidth_mx4ult$(width);
                },
                linkProgram_slyaif$: function (program) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.linkProgram_slyaif$(program);
                },
                pointSize_mx4ult$: function (size) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.pointSize_mx4ult$(size);
                },
                renderbufferStorage_tjonv8$: function (target, internalformat, width, height) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.renderbufferStorage_tjonv8$(target, internalformat, width, height);
                },
                renderbufferStorageMultisample_4qozqa$: function (target, samples, internalformat, width, height) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.renderbufferStorageMultisample_4qozqa$(target, samples, internalformat, width, height);
                },
                shaderSource_lfmudu$: function (shader, source) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.shaderSource_lfmudu$(shader, source);
                },
                texImage2D_bz3wcs$: function (target, level, internalformat, width, height, border, format, type, pixels) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.texImage2D_bz3wcs$(target, level, internalformat, width, height, border, format, type, pixels);
                },
                texParameteri_qt1dr2$: function (target, pname, param) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.texParameteri_qt1dr2$(target, pname, param);
                },
                uniform1f_rvcsvw$: function (location, x) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.uniform1f_rvcsvw$(location, x);
                },
                uniform1i_wn2dyp$: function (location, x) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.uniform1i_wn2dyp$(location, x);
                },
                uniform2f_zcqyrj$: function (location, x, y) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.uniform2f_zcqyrj$(location, x, y);
                },
                uniform3f_ig0gt8$: function (location, x, y, z) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.uniform3f_ig0gt8$(location, x, y, z);
                },
                uniform4f_k644h$: function (location, x, y, z, w) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.uniform4f_k644h$(location, x, y, z, w);
                },
                uniformMatrix4fv_rrdrnp$: function (location, transpose, value) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.uniformMatrix4fv_rrdrnp$(location, transpose, value);
                },
                useProgram_slyaif$: function (program) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.useProgram_slyaif$(program);
                },
                vertexAttribDivisor_vux9f0$: function (index, divisor) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.vertexAttribDivisor_vux9f0$(index, divisor);
                },
                vertexAttribPointer_owihk5$: function (indx, size, type, normalized, stride, offset) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.vertexAttribPointer_owihk5$(indx, size, type, normalized, stride, offset);
                },
                viewport_tjonv8$: function (x, y, width, height) {
                  _.de.fabmax.kool.platform.GL.Companion.impl_0.viewport_tjonv8$(x, y, width, height);
                }
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.platform.GL.Companion;
              },
              Impl: Kotlin.createTrait(null)
            }),
            Platform: Kotlin.createClass(null, function Platform() {
            }, null, /** @lends _.de.fabmax.kool.platform.Platform */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.platform.Platform.Companion.instance_0 = new _.de.fabmax.kool.platform.Platform.NoopPlatform();
              }, /** @lends _.de.fabmax.kool.platform.Platform.Companion.prototype */ {
                supportsMultiContext: {
                  get: function () {
                    return _.de.fabmax.kool.platform.Platform.Companion.instance_0.supportsMultiContext;
                  }
                },
                supportsUint32Indices: {
                  get: function () {
                    return _.de.fabmax.kool.platform.Platform.Companion.instance_0.supportsUint32Indices;
                  }
                },
                initPlatform_rbiqat$: function (platform_0) {
                  _.de.fabmax.kool.platform.Platform.Companion.instance_0 = platform_0;
                },
                createContext_ihrqo7$: function (props) {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createContext_ihrqo7$(props);
                },
                createDefaultShaderGenerator: function () {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createDefaultShaderGenerator();
                },
                getGlImpl: function () {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.getGlImpl();
                },
                createUint8Buffer_za3lpa$: function (capacity) {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createUint8Buffer_za3lpa$(capacity);
                },
                createUint16Buffer_za3lpa$: function (capacity) {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createUint16Buffer_za3lpa$(capacity);
                },
                createUint32Buffer_za3lpa$: function (capacity) {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createUint32Buffer_za3lpa$(capacity);
                },
                createFloat32Buffer_za3lpa$: function (capacity) {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createFloat32Buffer_za3lpa$(capacity);
                },
                currentTimeMillis: function () {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.currentTimeMillis();
                },
                loadTexture_2lkaa8$: function (path, props) {
                  if (props === void 0)
                    props = _.de.fabmax.kool.TextureResource.Companion.DEFAULT_PROPERTIES;
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.loadTexture_2lkaa8$(path, props);
                },
                createCharMap_34dg9o$: function (font, chars) {
                  return _.de.fabmax.kool.platform.Platform.Companion.instance_0.createCharMap_34dg9o$(font, chars);
                }
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.platform.Platform.Companion;
              },
              NoopPlatform: Kotlin.createClass(function () {
                return [_.de.fabmax.kool.platform.Platform];
              }, function NoopPlatform() {
                NoopPlatform.baseInitializer.call(this);
              }, /** @lends _.de.fabmax.kool.platform.Platform.NoopPlatform.prototype */ {
                supportsMultiContext: {
                  get: function () {
                    throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                  }
                },
                supportsUint32Indices: {
                  get: function () {
                    throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                  }
                },
                createDefaultShaderGenerator: function () {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                getGlImpl: function () {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                createContext_ihrqo7$: function (props) {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                createUint8Buffer_za3lpa$: function (capacity) {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                createUint16Buffer_za3lpa$: function (capacity) {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                createUint32Buffer_za3lpa$: function (capacity) {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                createFloat32Buffer_za3lpa$: function (capacity) {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                currentTimeMillis: function () {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                loadTexture_2lkaa8$: function (path, props) {
                  throw new Kotlin.UnsupportedOperationException('No platform set, call PlatformImpl.init() first');
                },
                createCharMap_34dg9o$: function (font, chars) {
                  throw new Kotlin.UnsupportedOperationException('not implemented');
                }
              })
            }),
            RenderContext: Kotlin.createClass(null, function RenderContext() {
              this.inputHandler = new _.de.fabmax.kool.InputHandler();
              this.memoryMgr = new _.de.fabmax.kool.MemoryManager();
              this.shaderMgr = new _.de.fabmax.kool.ShaderManager();
              this.textureMgr = new _.de.fabmax.kool.TextureManager();
              this.mvpState = new _.de.fabmax.kool.MvpState();
              this.startTimeMillis = Kotlin.Long.ZERO;
              this.time_t3ohqr$_0 = 0.0;
              this.deltaT_t3ohqr$_0 = 0.0;
              this.scene = new _.de.fabmax.kool.scene.Scene();
              this.viewportWidthProp_t3ohqr$_0 = new _.de.fabmax.kool.util.Property(0);
              this.viewportWidth$delegate = this.viewportWidthProp_t3ohqr$_0;
              this.viewportHeightProp_t3ohqr$_0 = new _.de.fabmax.kool.util.Property(0);
              this.viewportHeight$delegate = this.viewportHeightProp_t3ohqr$_0;
              this.clearColorProp_t3ohqr$_0 = new _.de.fabmax.kool.util.Property(_.de.fabmax.kool.util.Color.Companion.DARK_CYAN);
              this.clearColor$delegate = this.clearColorProp_t3ohqr$_0;
              this.clearMask = _.de.fabmax.kool.platform.GL.Companion.COLOR_BUFFER_BIT | _.de.fabmax.kool.platform.GL.Companion.DEPTH_BUFFER_BIT;
              this.boundBuffers_t3ohqr$_0 = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
            }, /** @lends _.de.fabmax.kool.platform.RenderContext.prototype */ {
              time: {
                get: function () {
                  return this.time_t3ohqr$_0;
                },
                set: function (time_0) {
                  this.time_t3ohqr$_0 = time_0;
                }
              },
              deltaT: {
                get: function () {
                  return this.deltaT_t3ohqr$_0;
                },
                set: function (deltaT_0) {
                  this.deltaT_t3ohqr$_0 = deltaT_0;
                }
              },
              viewportWidth: {
                get: function () {
                  return this.viewportWidth$delegate.getValue_dsk1ci$(this, new Kotlin.PropertyMetadata('viewportWidth'));
                },
                set: function (viewportWidth_0) {
                  this.viewportWidth$delegate.setValue_w32e13$(this, new Kotlin.PropertyMetadata('viewportWidth'), viewportWidth_0);
                }
              },
              viewportHeight: {
                get: function () {
                  return this.viewportHeight$delegate.getValue_dsk1ci$(this, new Kotlin.PropertyMetadata('viewportHeight'));
                },
                set: function (viewportHeight_0) {
                  this.viewportHeight$delegate.setValue_w32e13$(this, new Kotlin.PropertyMetadata('viewportHeight'), viewportHeight_0);
                }
              },
              clearColor: {
                get: function () {
                  return this.clearColor$delegate.getValue_dsk1ci$(this, new Kotlin.PropertyMetadata('clearColor'));
                },
                set: function (clearColor_0) {
                  this.clearColor$delegate.setValue_w32e13$(this, new Kotlin.PropertyMetadata('clearColor'), clearColor_0);
                }
              },
              onNewFrame: function () {
                var now = _.de.fabmax.kool.platform.Platform.Companion.currentTimeMillis();
                if (Kotlin.equals(this.startTimeMillis, Kotlin.Long.ZERO)) {
                  this.startTimeMillis = now;
                }
                var t = now.subtract(this.startTimeMillis).toNumber() / 1000.0;
                this.deltaT = t - this.time;
                this.time = t;
                this.inputHandler.onNewFrame_0();
                if (this.viewportWidthProp_t3ohqr$_0.valueChanged || this.viewportHeightProp_t3ohqr$_0.valueChanged) {
                  _.de.fabmax.kool.platform.GL.Companion.viewport_tjonv8$(0, 0, this.viewportWidthProp_t3ohqr$_0.clear, this.viewportHeightProp_t3ohqr$_0.clear);
                }
                if (this.clearColorProp_t3ohqr$_0.valueChanged) {
                  var color = this.clearColorProp_t3ohqr$_0.clear;
                  _.de.fabmax.kool.platform.GL.Companion.clearColor_7b5o5w$(color.r, color.g, color.b, color.a);
                }
                if (this.clearMask !== 0) {
                  _.de.fabmax.kool.platform.GL.Companion.clear_za3lpa$(this.clearMask);
                }
                _.de.fabmax.kool.platform.GL.Companion.enable_za3lpa$(_.de.fabmax.kool.platform.GL.Companion.DEPTH_TEST);
                _.de.fabmax.kool.platform.GL.Companion.enable_za3lpa$(_.de.fabmax.kool.platform.GL.Companion.CULL_FACE);
                _.de.fabmax.kool.platform.GL.Companion.enable_za3lpa$(_.de.fabmax.kool.platform.GL.Companion.BLEND);
                _.de.fabmax.kool.platform.GL.Companion.blendFunc_vux9f0$(_.de.fabmax.kool.platform.GL.Companion.ONE, _.de.fabmax.kool.platform.GL.Companion.ONE_MINUS_SRC_ALPHA);
              },
              render: function () {
                this.onNewFrame();
                this.scene.onRender_qk1xvd$(this);
              }
            }, /** @lends _.de.fabmax.kool.platform.RenderContext */ {
              InitProps: Kotlin.createClass(null, function InitProps() {
              })
            }),
            ShaderGenerator: Kotlin.createClass(null, function ShaderGenerator() {
              this.uniformMvpMatrix = new _.de.fabmax.kool.shading.UniformMatrix4(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MVP_MATRIX);
              this.uniformModelMatrix = new _.de.fabmax.kool.shading.UniformMatrix4(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX);
              this.uniformViewMatrix = new _.de.fabmax.kool.shading.UniformMatrix4(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX);
              this.uniformLightColor = new _.de.fabmax.kool.shading.Uniform3f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR);
              this.uniformLightDirection = new _.de.fabmax.kool.shading.Uniform3f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_DIRECTION);
              this.uniformCameraPosition = new _.de.fabmax.kool.shading.Uniform3f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_CAMERA_POSITION);
              this.uniformShininess = new _.de.fabmax.kool.shading.Uniform1f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SHININESS);
              this.uniformSpecularIntensity = new _.de.fabmax.kool.shading.Uniform1f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SPECULAR_INTENSITY);
              this.uniformStaticColor = new _.de.fabmax.kool.shading.Uniform4f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_STATIC_COLOR);
              this.uniformTexture = new _.de.fabmax.kool.shading.UniformTexture2D(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_TEXTURE_0);
              this.uniformAlpha = new _.de.fabmax.kool.shading.Uniform1f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_ALPHA);
              this.uniformSaturation = new _.de.fabmax.kool.shading.Uniform1f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SATURATION);
              this.uniformFogRange = new _.de.fabmax.kool.shading.Uniform1f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_RANGE);
              this.uniformFogColor = new _.de.fabmax.kool.shading.Uniform4f(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_COLOR);
            }, /** @lends _.de.fabmax.kool.platform.ShaderGenerator.prototype */ {
              generate_9em3xa$: function (shaderProps) {
                this.uniformMvpMatrix.location = null;
                this.uniformModelMatrix.location = null;
                this.uniformViewMatrix.location = null;
                this.uniformLightColor.location = null;
                this.uniformLightDirection.location = null;
                this.uniformCameraPosition.location = null;
                this.uniformShininess.location = null;
                this.uniformSpecularIntensity.location = null;
                this.uniformStaticColor.location = null;
                this.uniformTexture.location = null;
                this.uniformAlpha.location = null;
                this.uniformSaturation.location = null;
                this.uniformFogRange.location = null;
                this.uniformFogColor.location = null;
                return this.generateSource_9em3xa$(shaderProps);
              }
            }, /** @lends _.de.fabmax.kool.platform.ShaderGenerator */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MVP_MATRIX = 'uMvpMatrix';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX = 'uModelMatrix';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX = 'uViewMatrix';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_DIRECTION = 'uLightDirection';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR = 'uLightColor';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SHININESS = 'uShininess';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SPECULAR_INTENSITY = 'uSpecularIntensity';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_CAMERA_POSITION = 'uCameraPosition';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_COLOR = 'uFogColor';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_RANGE = 'uFogRange';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_TEXTURE_0 = 'uTexture0';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_STATIC_COLOR = 'uStaticColor';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_ALPHA = 'uAlpha';
                _.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SATURATION = 'uSaturation';
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.platform.ShaderGenerator.Companion;
              }
            })
          }),
          demo: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.demo */ {
            f: function (closure$ctx) {
              return function () {
                this.setRotation_dleff0$(20.0, -30.0);
                this.unaryPlus_uv0sin$(closure$ctx.scene.camera);
              };
            },
            f_0: function (ctx) {
              this.setIdentity();
              this.translate_y2kzbl$(-5.0, Math.sin(ctx.time * 5), 0.0);
              this.rotate_ag3lbb$(ctx.time * 19, _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
            },
            f_1: function () {
              this.color.set_d7aj7k$(new _.de.fabmax.kool.util.Color((this.normal.x + 1) / 2, (this.normal.y + 1) / 2, (this.normal.z + 1) / 2, 1.0));
            },
            f_2: function () {
              this.radius = 1.5;
            },
            f_3: function () {
              this.vertexModFun = _.de.fabmax.kool.demo.f_1;
              this.sphere_s9x6gh$(_.de.fabmax.kool.demo.f_2);
            },
            f_4: function () {
              this.animation = _.de.fabmax.kool.demo.f_0;
              this.unaryPlus_uv0sin$(_.de.fabmax.kool.util.colorMesh_gac8tm$(void 0, _.de.fabmax.kool.demo.f_3));
            },
            f_5: function (ctx) {
              this.setIdentity();
              this.translate_y2kzbl$(5.0, 0.0, 0.0);
              this.rotate_ag3lbb$(ctx.time * 90, _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
              this.rotate_ag3lbb$(ctx.time * 19, _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
            },
            f_6: function () {
              this.frontColor = _.de.fabmax.kool.util.Color.Companion.RED;
              this.rightColor = _.de.fabmax.kool.util.Color.Companion.GREEN;
              this.backColor = _.de.fabmax.kool.util.Color.Companion.BLUE;
              this.leftColor = _.de.fabmax.kool.util.Color.Companion.YELLOW;
              this.topColor = _.de.fabmax.kool.util.Color.Companion.MAGENTA;
              this.bottomColor = _.de.fabmax.kool.util.Color.Companion.CYAN;
            },
            f_7: function () {
              this.scale_y2kzbl$(2.0, 2.0, 2.0);
              this.translate_y2kzbl$(-0.5, -0.5, -0.5);
              this.cube_9hfdbr$(_.de.fabmax.kool.demo.f_6);
            },
            f_8: function () {
              this.animation = _.de.fabmax.kool.demo.f_5;
              this.unaryPlus_uv0sin$(_.de.fabmax.kool.util.colorMesh_gac8tm$(void 0, _.de.fabmax.kool.demo.f_7));
            },
            f_9: function (ctx) {
              this.setIdentity();
              this.translate_y2kzbl$(0.0, 0.0, -5.0);
              var s = 1.0 + Math.sin(ctx.time * 3) * 0.5;
              this.scale_y2kzbl$(s, s, s);
            },
            f_10: function () {
              this.origin.set_y2kzbl$(0.0, -1.5, 0.0);
              this.height = 3.0;
              this.topRadius = 0.5;
              this.bottomRadius = 1.0;
            },
            f_11: function () {
              this.color = _.de.fabmax.kool.util.Color.Companion.LIME;
              this.cylinder_7jdbew$(_.de.fabmax.kool.demo.f_10);
            },
            f_12: function () {
              this.animation = _.de.fabmax.kool.demo.f_9;
              this.unaryPlus_uv0sin$(_.de.fabmax.kool.util.colorMesh_gac8tm$(void 0, _.de.fabmax.kool.demo.f_11));
            },
            simpleShapesDemo_qk1xvd$f: function (closure$ctx) {
              return function () {
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.scene.sphericalInputTransform_n6upd5$(void 0, _.de.fabmax.kool.demo.f(closure$ctx)));
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.scene.transformGroup_2byx8j$(void 0, _.de.fabmax.kool.demo.f_4));
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.scene.transformGroup_2byx8j$(void 0, _.de.fabmax.kool.demo.f_8));
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.scene.transformGroup_2byx8j$('back', _.de.fabmax.kool.demo.f_12));
              };
            },
            simpleShapesDemo_qk1xvd$: function (ctx) {
              ctx.scene.root = _.de.fabmax.kool.scene.group_30wlp3$(void 0, _.de.fabmax.kool.demo.simpleShapesDemo_qk1xvd$f(ctx));
              ctx.clearColor = new _.de.fabmax.kool.util.Color(0.05000000074505806, 0.15000000596046448, 0.25, 1.0);
              ctx.run();
            },
            f_13: function (closure$ctx) {
              return function () {
                this.unaryPlus_uv0sin$(closure$ctx.scene.camera);
              };
            },
            f_14: function (closure$metrics) {
              return function () {
                this.width = closure$metrics.width / 100;
                this.height = closure$metrics.height / 100;
                this.texCoordUpperLeft.set_cx11y3$(closure$metrics.uvMin);
                this.texCoordUpperRight.set_dleff0$(closure$metrics.uvMax.x, closure$metrics.uvMin.y);
                this.texCoordLowerLeft.set_dleff0$(closure$metrics.uvMin.x, closure$metrics.uvMax.y);
                this.texCoordLowerRight.set_cx11y3$(closure$metrics.uvMax);
              };
            },
            f_15: function (closure$font, closure$str) {
              return function () {
                var tmp$0, tmp$1, tmp$2;
                (tmp$0 = this.shader) != null ? (tmp$0.texture = closure$font.charMap.fontTexture) : null;
                this.translate_y2kzbl$(-1.0, -0.25, 0.0);
                tmp$1 = Kotlin.kotlin.text.iterator_gw00vq$(closure$str);
                while (tmp$1.hasNext()) {
                  var c = tmp$1.next();
                  var metrics = (tmp$2 = closure$font.charMap.get_za3rmp$(c)) != null ? tmp$2 : Kotlin.throwNPE();
                  this.rect_ud8hiy$(_.de.fabmax.kool.demo.f_14(metrics));
                  this.translate_y2kzbl$(metrics.advance / 100, 0.0, 0.0);
                }
              };
            },
            textDemo_qk1xvd$f: function (closure$ctx, closure$font, closure$str) {
              return function () {
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.scene.sphericalInputTransform_n6upd5$(void 0, _.de.fabmax.kool.demo.f_13(closure$ctx)));
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.util.textureMesh_gac8tm$(void 0, _.de.fabmax.kool.demo.f_15(closure$font, closure$str)));
              };
            },
            textDemo_qk1xvd$: function (ctx) {
              var font = new _.de.fabmax.kool.util.Font('Segoe UI', 48.0);
              var str = 'Hello j World!';
              ctx.scene.root = _.de.fabmax.kool.scene.group_30wlp3$(void 0, _.de.fabmax.kool.demo.textDemo_qk1xvd$f(ctx, font, str));
              ctx.clearColor = new _.de.fabmax.kool.util.Color(0.05000000074505806, 0.15000000596046448, 0.25, 1.0);
              ctx.run();
            },
            f_16: function () {
              this.width = 4.0;
              this.height = 4.0;
            },
            f_17: function (closure$i) {
              return function () {
                var tmp$0;
                this.translate_y2kzbl$(-2.0 + closure$i * 5, -2.0, 0.0);
                this.rect_ud8hiy$(_.de.fabmax.kool.demo.f_16);
                (tmp$0 = this.shader) != null ? (tmp$0.texture = new _.de.fabmax.kool.SharedAssetTexture('test.png')) : null;
              };
            },
            textureDemo_qk1xvd$f: function () {
              for (var i = -1; i <= 1; i++) {
                this.unaryPlus_uv0sin$(_.de.fabmax.kool.util.textureMesh_gac8tm$(void 0, _.de.fabmax.kool.demo.f_17(i)));
              }
            },
            textureDemo_qk1xvd$: function (ctx) {
              ctx.scene.root = _.de.fabmax.kool.scene.transformGroup_2byx8j$(void 0, _.de.fabmax.kool.demo.textureDemo_qk1xvd$f);
              ctx.clearColor = new _.de.fabmax.kool.util.Color(0.05000000074505806, 0.15000000596046448, 0.25, 1.0);
              ctx.scene.camera.position.set_y2kzbl$(0.0, 0.0, 15.0);
              ctx.run();
            }
          }),
          GlObject: Kotlin.createClass(null, function GlObject() {
            this.res_pdmqdp$_0 = null;
          }, /** @lends _.de.fabmax.kool.GlObject.prototype */ {
            res: {
              get: function () {
                return this.res_pdmqdp$_0;
              },
              set: function (res_0) {
                this.res_pdmqdp$_0 = res_0;
              }
            },
            isValid: {
              get: function () {
                return this.res != null;
              }
            },
            delete_qk1xvd$: function (ctx) {
              var tmp$0;
              (tmp$0 = this.res) != null ? tmp$0.delete_qk1xvd$(ctx) : null;
              this.res = null;
            }
          }),
          GlResource: Kotlin.createClass(null, function GlResource(glRef, type) {
            this.type = type;
            this.glRef_h4fcf2$_0 = glRef;
          }, /** @lends _.de.fabmax.kool.GlResource.prototype */ {
            glRef: {
              get: function () {
                return this.glRef_h4fcf2$_0;
              },
              set: function (glRef_0) {
                this.glRef_h4fcf2$_0 = glRef_0;
              }
            },
            isValid: {
              get: function () {
                return this.glRef != null;
              }
            },
            delete_qk1xvd$: function (ctx) {
              ctx.memoryMgr.deleted_0(this);
              this.glRef = null;
            }
          }, /** @lends _.de.fabmax.kool.GlResource */ {
            Type: Kotlin.createEnumClass(function () {
              return [Kotlin.Enum];
            }, function Type() {
              Type.baseInitializer.call(this);
            }, function () {
              return {
                BUFFER: function () {
                  return new _.de.fabmax.kool.GlResource.Type();
                },
                FRAMEBUFFER: function () {
                  return new _.de.fabmax.kool.GlResource.Type();
                },
                PROGRAM: function () {
                  return new _.de.fabmax.kool.GlResource.Type();
                },
                RENDERBUFFER: function () {
                  return new _.de.fabmax.kool.GlResource.Type();
                },
                SHADER: function () {
                  return new _.de.fabmax.kool.GlResource.Type();
                },
                TEXTURE: function () {
                  return new _.de.fabmax.kool.GlResource.Type();
                }
              };
            })
          }),
          BufferResource: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlResource];
          }, function BufferResource(glRef, target, ctx) {
            BufferResource.baseInitializer.call(this, glRef, _.de.fabmax.kool.GlResource.Type.BUFFER);
            this.target = target;
          }, /** @lends _.de.fabmax.kool.BufferResource.prototype */ {
            delete_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.deleteBuffer_jifzef$(this);
              _.de.fabmax.kool.GlResource.prototype.delete_qk1xvd$.call(this, ctx);
            },
            bind_qk1xvd$: function (ctx) {
              if (!Kotlin.equals(ctx.boundBuffers_t3ohqr$_0.get_za3rmp$(this.target), this)) {
                _.de.fabmax.kool.platform.GL.Companion.bindBuffer_5qhhd9$(this.target, this);
                ctx.boundBuffers_t3ohqr$_0.put_wn2jw4$(this.target, this);
              }
            },
            setData_o0h6ow$: function (data, usage, ctx) {
              var limit = data.limit;
              var pos = data.position;
              data.flip();
              this.bind_qk1xvd$(ctx);
              _.de.fabmax.kool.platform.GL.Companion.bufferData_axcqa7$(this.target, data, usage);
              ctx.memoryMgr.memoryAllocated_0(this, pos * 4);
              data.limit = limit;
              data.position = pos;
            },
            setData_4ndwmb$: function (data, usage, ctx) {
              var limit = data.limit;
              var pos = data.position;
              data.flip();
              this.bind_qk1xvd$(ctx);
              _.de.fabmax.kool.platform.GL.Companion.bufferData_9rgdn0$(this.target, data, usage);
              ctx.memoryMgr.memoryAllocated_0(this, pos);
              data.limit = limit;
              data.position = pos;
            },
            setData_qev988$: function (data, usage, ctx) {
              var limit = data.limit;
              var pos = data.position;
              data.flip();
              this.bind_qk1xvd$(ctx);
              _.de.fabmax.kool.platform.GL.Companion.bufferData_4jc8ml$(this.target, data, usage);
              ctx.memoryMgr.memoryAllocated_0(this, pos * 2);
              data.limit = limit;
              data.position = pos;
            },
            setData_p3ykg2$: function (data, usage, ctx) {
              var limit = data.limit;
              var pos = data.position;
              data.flip();
              this.bind_qk1xvd$(ctx);
              _.de.fabmax.kool.platform.GL.Companion.bufferData_xhmpuh$(this.target, data, usage);
              ctx.memoryMgr.memoryAllocated_0(this, pos * 4);
              data.limit = limit;
              data.position = pos;
            },
            unbind_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.bindBuffer_5qhhd9$(this.target, null);
              ctx.boundBuffers_t3ohqr$_0.put_wn2jw4$(this.target, null);
            }
          }, /** @lends _.de.fabmax.kool.BufferResource */ {
            Companion: Kotlin.createObject(null, function Companion() {
            }, /** @lends _.de.fabmax.kool.BufferResource.Companion.prototype */ {
              create_8ffywt$: function (target, ctx) {
                return new _.de.fabmax.kool.BufferResource(_.de.fabmax.kool.platform.GL.Companion.createBuffer(), target, ctx);
              }
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.BufferResource.Companion;
            }
          }),
          FramebufferResource: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlResource];
          }, function FramebufferResource(glRef, ctx) {
            FramebufferResource.baseInitializer.call(this, glRef, _.de.fabmax.kool.GlResource.Type.FRAMEBUFFER);
          }, /** @lends _.de.fabmax.kool.FramebufferResource.prototype */ {
            delete_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.deleteFramebuffer_4s0d2q$(this);
              _.de.fabmax.kool.GlResource.prototype.delete_qk1xvd$.call(this, ctx);
            }
          }, /** @lends _.de.fabmax.kool.FramebufferResource */ {
            Companion: Kotlin.createObject(null, function Companion() {
            }, /** @lends _.de.fabmax.kool.FramebufferResource.Companion.prototype */ {
              create_qk1xvd$: function (ctx) {
                return new _.de.fabmax.kool.FramebufferResource(_.de.fabmax.kool.platform.GL.Companion.createFramebuffer(), ctx);
              }
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.FramebufferResource.Companion;
            }
          }),
          ProgramResource: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlResource];
          }, function ProgramResource(glRef, ctx) {
            ProgramResource.baseInitializer.call(this, glRef, _.de.fabmax.kool.GlResource.Type.PROGRAM);
          }, /** @lends _.de.fabmax.kool.ProgramResource.prototype */ {
            delete_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.deleteProgram_slyaif$(this);
              _.de.fabmax.kool.GlResource.prototype.delete_qk1xvd$.call(this, ctx);
            },
            attachShader_45k95$: function (shader, ctx) {
              _.de.fabmax.kool.platform.GL.Companion.attachShader_bb91mv$(this, shader);
            },
            link_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.linkProgram_slyaif$(this);
              return _.de.fabmax.kool.platform.GL.Companion.getProgrami_i0tefv$(this, _.de.fabmax.kool.platform.GL.Companion.LINK_STATUS) === _.de.fabmax.kool.platform.GL.Companion.TRUE;
            },
            getInfoLog_qk1xvd$: function (ctx) {
              return _.de.fabmax.kool.platform.GL.Companion.getProgramInfoLog_slyaif$(this);
            }
          }, /** @lends _.de.fabmax.kool.ProgramResource */ {
            Companion: Kotlin.createObject(null, function Companion() {
            }, /** @lends _.de.fabmax.kool.ProgramResource.Companion.prototype */ {
              create_qk1xvd$: function (ctx) {
                return new _.de.fabmax.kool.ProgramResource(_.de.fabmax.kool.platform.GL.Companion.createProgram(), ctx);
              }
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.ProgramResource.Companion;
            }
          }),
          RenderbufferResource: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlResource];
          }, function RenderbufferResource(glRef, ctx) {
            RenderbufferResource.baseInitializer.call(this, glRef, _.de.fabmax.kool.GlResource.Type.RENDERBUFFER);
          }, /** @lends _.de.fabmax.kool.RenderbufferResource.prototype */ {
            delete_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.deleteRenderbuffer_s0cyb7$(this);
              _.de.fabmax.kool.GlResource.prototype.delete_qk1xvd$.call(this, ctx);
            }
          }, /** @lends _.de.fabmax.kool.RenderbufferResource */ {
            Companion: Kotlin.createObject(null, function Companion() {
            }, /** @lends _.de.fabmax.kool.RenderbufferResource.Companion.prototype */ {
              create_qk1xvd$: function (ctx) {
                return new _.de.fabmax.kool.RenderbufferResource(_.de.fabmax.kool.platform.GL.Companion.createRenderbuffer(), ctx);
              }
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.RenderbufferResource.Companion;
            }
          }),
          ShaderResource: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlResource];
          }, function ShaderResource(glRef, ctx) {
            ShaderResource.baseInitializer.call(this, glRef, _.de.fabmax.kool.GlResource.Type.SHADER);
          }, /** @lends _.de.fabmax.kool.ShaderResource.prototype */ {
            delete_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.deleteShader_as565g$(this);
              _.de.fabmax.kool.GlResource.prototype.delete_qk1xvd$.call(this, ctx);
            },
            shaderSource_vfqc6f$: function (source, ctx) {
              _.de.fabmax.kool.platform.GL.Companion.shaderSource_lfmudu$(this, source);
            },
            compile_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.compileShader_as565g$(this);
              return _.de.fabmax.kool.platform.GL.Companion.getShaderi_4x9xq$(this, _.de.fabmax.kool.platform.GL.Companion.COMPILE_STATUS) === _.de.fabmax.kool.platform.GL.Companion.TRUE;
            },
            getInfoLog_qk1xvd$: function (ctx) {
              return _.de.fabmax.kool.platform.GL.Companion.getShaderInfoLog_as565g$(this);
            }
          }, /** @lends _.de.fabmax.kool.ShaderResource */ {
            Companion: Kotlin.createObject(null, function Companion() {
            }, /** @lends _.de.fabmax.kool.ShaderResource.Companion.prototype */ {
              createFragmentShader_qk1xvd$: function (ctx) {
                return new _.de.fabmax.kool.ShaderResource(_.de.fabmax.kool.platform.GL.Companion.createShader_za3lpa$(_.de.fabmax.kool.platform.GL.Companion.FRAGMENT_SHADER), ctx);
              },
              createVertexShader_qk1xvd$: function (ctx) {
                return new _.de.fabmax.kool.ShaderResource(_.de.fabmax.kool.platform.GL.Companion.createShader_za3lpa$(_.de.fabmax.kool.platform.GL.Companion.VERTEX_SHADER), ctx);
              }
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.ShaderResource.Companion;
            }
          }),
          TextureResource: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlResource];
          }, function TextureResource(glRef, target, props, ctx) {
            TextureResource.baseInitializer.call(this, glRef, _.de.fabmax.kool.GlResource.Type.TEXTURE);
            this.target = target;
            this.props = props;
            _.de.fabmax.kool.platform.GL.Companion.bindTexture_qdzudy$(_.de.fabmax.kool.platform.GL.Companion.TEXTURE_2D, this);
            _.de.fabmax.kool.platform.GL.Companion.texParameteri_qt1dr2$(this.target, _.de.fabmax.kool.platform.GL.Companion.TEXTURE_MIN_FILTER, this.props.minFilter);
            _.de.fabmax.kool.platform.GL.Companion.texParameteri_qt1dr2$(this.target, _.de.fabmax.kool.platform.GL.Companion.TEXTURE_MAG_FILTER, this.props.magFilter);
            _.de.fabmax.kool.platform.GL.Companion.texParameteri_qt1dr2$(this.target, _.de.fabmax.kool.platform.GL.Companion.TEXTURE_WRAP_S, this.props.xWrapping);
            _.de.fabmax.kool.platform.GL.Companion.texParameteri_qt1dr2$(this.target, _.de.fabmax.kool.platform.GL.Companion.TEXTURE_WRAP_T, this.props.yWrapping);
          }, /** @lends _.de.fabmax.kool.TextureResource.prototype */ {
            delete_qk1xvd$: function (ctx) {
              _.de.fabmax.kool.platform.GL.Companion.deleteTexture_phyn8g$(this);
              _.de.fabmax.kool.GlResource.prototype.delete_qk1xvd$.call(this, ctx);
            }
          }, /** @lends _.de.fabmax.kool.TextureResource */ {
            Companion: Kotlin.createObject(null, function Companion() {
              _.de.fabmax.kool.TextureResource.Companion.DEFAULT_PROPERTIES = _.de.fabmax.kool.TextureResource.Props_init();
            }, /** @lends _.de.fabmax.kool.TextureResource.Companion.prototype */ {
              create_cr2655$: function (target, props, ctx) {
                return new _.de.fabmax.kool.TextureResource(_.de.fabmax.kool.platform.GL.Companion.createTexture(), target, props, ctx);
              }
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.TextureResource.Companion;
            },
            Props: Kotlin.createClass(null, function Props(minFilter, magFilter, xWrapping, yWrapping) {
              this.minFilter = minFilter;
              this.magFilter = magFilter;
              this.xWrapping = xWrapping;
              this.yWrapping = yWrapping;
            }, /** @lends _.de.fabmax.kool.TextureResource.Props.prototype */ {
              component1: function () {
                return this.minFilter;
              },
              component2: function () {
                return this.magFilter;
              },
              component3: function () {
                return this.xWrapping;
              },
              component4: function () {
                return this.yWrapping;
              },
              copy_tjonv8$: function (minFilter, magFilter, xWrapping, yWrapping) {
                return new _.de.fabmax.kool.TextureResource.Props_init(minFilter === void 0 ? this.minFilter : minFilter, magFilter === void 0 ? this.magFilter : magFilter, xWrapping === void 0 ? this.xWrapping : xWrapping, yWrapping === void 0 ? this.yWrapping : yWrapping);
              },
              toString: function () {
                return 'Props(minFilter=' + Kotlin.toString(this.minFilter) + (', magFilter=' + Kotlin.toString(this.magFilter)) + (', xWrapping=' + Kotlin.toString(this.xWrapping)) + (', yWrapping=' + Kotlin.toString(this.yWrapping)) + ')';
              },
              hashCode: function () {
                var result = 0;
                result = result * 31 + Kotlin.hashCode(this.minFilter) | 0;
                result = result * 31 + Kotlin.hashCode(this.magFilter) | 0;
                result = result * 31 + Kotlin.hashCode(this.xWrapping) | 0;
                result = result * 31 + Kotlin.hashCode(this.yWrapping) | 0;
                return result;
              },
              equals_za3rmp$: function (other) {
                return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.minFilter, other.minFilter) && Kotlin.equals(this.magFilter, other.magFilter) && Kotlin.equals(this.xWrapping, other.xWrapping) && Kotlin.equals(this.yWrapping, other.yWrapping)))));
              }
            }),
            Props_init: function ($this) {
              $this = $this || Object.create(_.de.fabmax.kool.TextureResource.Props.prototype);
              _.de.fabmax.kool.TextureResource.Props.call($this, _.de.fabmax.kool.platform.GL.Companion.LINEAR_MIPMAP_LINEAR, _.de.fabmax.kool.platform.GL.Companion.LINEAR, _.de.fabmax.kool.platform.GL.Companion.CLAMP_TO_EDGE, _.de.fabmax.kool.platform.GL.Companion.CLAMP_TO_EDGE);
              return $this;
            }
          }),
          InputHandler: Kotlin.createClass(null, function InputHandler() {
            this.tmpPointers_0 = Kotlin.arrayFromFun(_.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS, Kotlin.getCallableRefForConstructor(_.de.fabmax.kool.InputHandler.Pointer));
            this.pointers_0 = Kotlin.arrayFromFun(_.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS, Kotlin.getCallableRefForConstructor(_.de.fabmax.kool.InputHandler.Pointer));
            this.primaryPointer = this.pointers_0[_.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER];
          }, /** @lends _.de.fabmax.kool.InputHandler.prototype */ {
            onNewFrame_0: function () {
              var tmp$0, tmp$1, tmp$2, tmp$3;
              tmp$0 = Kotlin.kotlin.collections.get_indices_eg9ybj$(this.pointers_0);
              tmp$1 = tmp$0.first;
              tmp$2 = tmp$0.last;
              tmp$3 = tmp$0.step;
              for (var i = tmp$1; i <= tmp$2; i += tmp$3) {
                this.pointers_0[i].updateFrom_0(this.tmpPointers_0[i]);
              }
            },
            updatePointerPos_w4xg1m$: function (pointer, x, y) {
              if (pointer >= 0 && pointer < _.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS) {
                var ptr = this.tmpPointers_0[pointer];
                ptr.isValid = true;
                ptr.x = x;
                ptr.y = y;
              }
            },
            updatePointerButtonState_ydzd23$: function (pointer, button, down) {
              if (pointer >= 0 && pointer < _.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS) {
                var ptr = this.tmpPointers_0[pointer];
                ptr.isValid = true;
                if (down) {
                  ptr.buttonMask = ptr.buttonMask | 1 << button;
                }
                 else {
                  ptr.buttonMask = ptr.buttonMask & ~(1 << button);
                }
              }
            },
            updatePointerButtonStates_vux9f0$: function (pointer, mask) {
              if (pointer >= 0 && pointer < _.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS) {
                var ptr = this.tmpPointers_0[pointer];
                ptr.isValid = true;
                ptr.buttonMask = mask;
              }
            },
            updatePointerScrollPos_5wr77w$: function (pointer, ticks) {
              if (pointer >= 0 && pointer < _.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS) {
                var ptr = this.tmpPointers_0[pointer];
                ptr.isValid = true;
                ptr.scrollPos = ptr.scrollPos + ticks;
              }
            },
            updatePointerValid_fzusl$: function (pointer, valid) {
              if (pointer >= 0 && pointer < _.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS) {
                this.tmpPointers_0[pointer].isValid = valid;
              }
            }
          }, /** @lends _.de.fabmax.kool.InputHandler */ {
            Companion: Kotlin.createObject(null, function Companion() {
              _.de.fabmax.kool.InputHandler.Companion.LEFT_BUTTON = 0;
              _.de.fabmax.kool.InputHandler.Companion.LEFT_BUTTON_MASK = 1;
              _.de.fabmax.kool.InputHandler.Companion.RIGHT_BUTTON = 1;
              _.de.fabmax.kool.InputHandler.Companion.RIGHT_BUTTON_MASK = 2;
              _.de.fabmax.kool.InputHandler.Companion.MIDDLE_BUTTON = 2;
              _.de.fabmax.kool.InputHandler.Companion.MIDDLE_BUTTON_MASK = 4;
              _.de.fabmax.kool.InputHandler.Companion.BACK_BUTTON = 3;
              _.de.fabmax.kool.InputHandler.Companion.BACK_BUTTON_MASK = 8;
              _.de.fabmax.kool.InputHandler.Companion.FORWARD_BUTTON = 4;
              _.de.fabmax.kool.InputHandler.Companion.FORWARD_BUTTON_MASK = 16;
              _.de.fabmax.kool.InputHandler.Companion.MAX_POINTERS = 10;
              _.de.fabmax.kool.InputHandler.Companion.PRIMARY_POINTER = 0;
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.InputHandler.Companion;
            },
            Pointer: Kotlin.createClass(null, function Pointer(id) {
              this.id = id;
              this.x_ssyq76$_0 = 0.0;
              this.deltaX_ssyq76$_0 = 0.0;
              this.y_ssyq76$_0 = 0.0;
              this.deltaY_ssyq76$_0 = 0.0;
              this.scrollPos_ssyq76$_0 = 0.0;
              this.deltaScroll_ssyq76$_0 = 0.0;
              this.buttonMask_ssyq76$_0 = 0;
              this.isValid_ssyq76$_0 = false;
            }, /** @lends _.de.fabmax.kool.InputHandler.Pointer.prototype */ {
              x: {
                get: function () {
                  return this.x_ssyq76$_0;
                },
                set: function (x_0) {
                  this.x_ssyq76$_0 = x_0;
                }
              },
              deltaX: {
                get: function () {
                  return this.deltaX_ssyq76$_0;
                },
                set: function (deltaX_0) {
                  this.deltaX_ssyq76$_0 = deltaX_0;
                }
              },
              y: {
                get: function () {
                  return this.y_ssyq76$_0;
                },
                set: function (y_0) {
                  this.y_ssyq76$_0 = y_0;
                }
              },
              deltaY: {
                get: function () {
                  return this.deltaY_ssyq76$_0;
                },
                set: function (deltaY_0) {
                  this.deltaY_ssyq76$_0 = deltaY_0;
                }
              },
              scrollPos: {
                get: function () {
                  return this.scrollPos_ssyq76$_0;
                },
                set: function (scrollPos_0) {
                  this.scrollPos_ssyq76$_0 = scrollPos_0;
                }
              },
              deltaScroll: {
                get: function () {
                  return this.deltaScroll_ssyq76$_0;
                },
                set: function (deltaScroll_0) {
                  this.deltaScroll_ssyq76$_0 = deltaScroll_0;
                }
              },
              buttonMask: {
                get: function () {
                  return this.buttonMask_ssyq76$_0;
                },
                set: function (buttonMask_0) {
                  this.buttonMask_ssyq76$_0 = buttonMask_0;
                }
              },
              isValid: {
                get: function () {
                  return this.isValid_ssyq76$_0;
                },
                set: function (isValid_0) {
                  this.isValid_ssyq76$_0 = isValid_0;
                }
              },
              isLeftButtonDown: {
                get: function () {
                  return (this.buttonMask & _.de.fabmax.kool.InputHandler.Companion.LEFT_BUTTON_MASK) !== 0;
                }
              },
              isRightButtonDown: {
                get: function () {
                  return (this.buttonMask & _.de.fabmax.kool.InputHandler.Companion.RIGHT_BUTTON_MASK) !== 0;
                }
              },
              isMiddleButtonDown: {
                get: function () {
                  return (this.buttonMask & _.de.fabmax.kool.InputHandler.Companion.MIDDLE_BUTTON_MASK) !== 0;
                }
              },
              isBackButtonDown: {
                get: function () {
                  return (this.buttonMask & _.de.fabmax.kool.InputHandler.Companion.BACK_BUTTON_MASK) !== 0;
                }
              },
              isForwardButtonDown: {
                get: function () {
                  return (this.buttonMask & _.de.fabmax.kool.InputHandler.Companion.FORWARD_BUTTON_MASK) !== 0;
                }
              },
              updateFrom_0: function (ptr) {
                this.deltaX = ptr.x - this.x;
                this.deltaY = ptr.y - this.y;
                this.deltaScroll = ptr.scrollPos - this.scrollPos;
                this.x = ptr.x;
                this.y = ptr.y;
                this.scrollPos = ptr.scrollPos;
                this.buttonMask = ptr.buttonMask;
                this.isValid = ptr.isValid;
              }
            })
          }),
          KoolException: Kotlin.createClass(function () {
            return [Kotlin.Exception];
          }, function KoolException(message) {
            KoolException.baseInitializer.call(this, message);
          }),
          MemoryManager: Kotlin.createClass(null, function MemoryManager() {
            this.allocationMap_0 = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
          }, /** @lends _.de.fabmax.kool.MemoryManager.prototype */ {
            memoryAllocated_0: function (resource, memory) {
              var resMap = this.allocationMap_0.get_za3rmp$(resource.type);
              if (resMap == null) {
                resMap = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
                this.allocationMap_0.put_wn2jw4$(resource.type, resMap);
              }
              resMap.put_wn2jw4$(resource, memory);
            },
            deleted_0: function (resource) {
              var tmp$0;
              var memory = (tmp$0 = this.allocationMap_0.get_za3rmp$(resource.type)) != null ? tmp$0.remove_za3rmp$(resource) : null;
              if (memory != null) {
                Kotlin.println(resource.type + ' deleted: ' + Kotlin.toString(memory) + ' bytes');
              }
            }
          }),
          MvpState: Kotlin.createClass(null, function MvpState() {
            this.projMatrix = new _.de.fabmax.kool.util.Mat4fStack();
            this.projMatrixBuffer_rlyduh$_0 = _.de.fabmax.kool.platform.Platform.Companion.createFloat32Buffer_za3lpa$(16);
            this.viewMatrix = new _.de.fabmax.kool.util.Mat4fStack();
            this.viewMatrixBuffer_rlyduh$_0 = _.de.fabmax.kool.platform.Platform.Companion.createFloat32Buffer_za3lpa$(16);
            this.modelMatrix = new _.de.fabmax.kool.util.Mat4fStack();
            this.modelMatrixBuffer_rlyduh$_0 = _.de.fabmax.kool.platform.Platform.Companion.createFloat32Buffer_za3lpa$(16);
            this.mvpMatrix = new _.de.fabmax.kool.util.Mat4f();
            this.mvpMatrixBuffer_rlyduh$_0 = _.de.fabmax.kool.platform.Platform.Companion.createFloat32Buffer_za3lpa$(16);
            this.tempMatrix_0 = new _.de.fabmax.kool.util.Mat4f();
            this.reset();
          }, /** @lends _.de.fabmax.kool.MvpState.prototype */ {
            projMatrixBuffer: {
              get: function () {
                this.projMatrix.toBuffer_m7ytxz$(this.projMatrixBuffer_rlyduh$_0);
                return this.projMatrixBuffer_rlyduh$_0;
              }
            },
            viewMatrixBuffer: {
              get: function () {
                this.viewMatrix.toBuffer_m7ytxz$(this.viewMatrixBuffer_rlyduh$_0);
                return this.viewMatrixBuffer_rlyduh$_0;
              }
            },
            modelMatrixBuffer: {
              get: function () {
                this.modelMatrix.toBuffer_m7ytxz$(this.modelMatrixBuffer_rlyduh$_0);
                return this.modelMatrixBuffer_rlyduh$_0;
              }
            },
            mvpMatrixBuffer: {
              get: function () {
                this.mvpMatrix.toBuffer_m7ytxz$(this.mvpMatrixBuffer_rlyduh$_0);
                return this.mvpMatrixBuffer_rlyduh$_0;
              }
            },
            reset: function () {
              this.projMatrix.reset();
              this.viewMatrix.reset();
              this.modelMatrix.reset();
              this.mvpMatrix.setIdentity();
            },
            update_qk1xvd$: function (ctx) {
              var tmp$0;
              this.projMatrix.mul_y010fm$(this.mvpMatrix, this.viewMatrix.mul_y010fm$(this.tempMatrix_0, this.modelMatrix));
              (tmp$0 = ctx.shaderMgr.boundShader) != null ? tmp$0.onMatrixUpdate_qk1xvd$(ctx) : null;
            }
          }),
          ShaderManager: Kotlin.createClass(null, function ShaderManager() {
            this.boundShader_33oxtx$_0 = null;
            this.shaderProgramMap_0 = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
          }, /** @lends _.de.fabmax.kool.ShaderManager.prototype */ {
            boundShader: {
              get: function () {
                return this.boundShader_33oxtx$_0;
              },
              set: function (boundShader_0) {
                this.boundShader_33oxtx$_0 = boundShader_0;
              }
            },
            bindShader_fyq94j$: function (shader, ctx) {
              var tmp$0, tmp$1, tmp$2;
              if (shader != null) {
                if (!shader.isValid) {
                  shader.onLoad_qk1xvd$(ctx);
                }
                if (!Kotlin.equals(shader, this.boundShader)) {
                  if (!Kotlin.equals((tmp$0 = shader.res) != null ? tmp$0.glRef : null, (tmp$2 = (tmp$1 = this.boundShader) != null ? tmp$1.res : null) != null ? tmp$2.glRef : null)) {
                    _.de.fabmax.kool.platform.GL.Companion.useProgram_slyaif$(shader.res);
                  }
                  this.boundShader = shader;
                  shader.onBind_qk1xvd$(ctx);
                }
              }
               else if (this.boundShader != null) {
                _.de.fabmax.kool.platform.GL.Companion.useProgram_slyaif$(null);
                this.boundShader = null;
              }
            },
            deleteShader_0: function (shader, ctx) {
              var ref = this.shaderProgramMap_0.get_za3rmp$(shader.source);
              if (ref != null) {
                if ((ref.referenceCount = ref.referenceCount - 1, ref.referenceCount) === 0) {
                  ref.prog.delete_qk1xvd$(ctx);
                  this.shaderProgramMap_0.remove_za3rmp$(shader.source);
                }
              }
            },
            compile_0: function (source, ctx) {
              var tmp$0, tmp$1;
              var res = this.shaderProgramMap_0.get_za3rmp$(source);
              if (res == null) {
                var vertShader = _.de.fabmax.kool.ShaderResource.Companion.createVertexShader_qk1xvd$(ctx);
                vertShader.shaderSource_vfqc6f$(source.vertexSrc, ctx);
                if (!vertShader.compile_qk1xvd$(ctx)) {
                  var log = vertShader.getInfoLog_qk1xvd$(ctx);
                  vertShader.delete_qk1xvd$(ctx);
                  throw new _.de.fabmax.kool.KoolException('Vertex shader compilation failed: ' + log);
                }
                var fragShader = _.de.fabmax.kool.ShaderResource.Companion.createFragmentShader_qk1xvd$(ctx);
                fragShader.shaderSource_vfqc6f$(source.fragmentSrc, ctx);
                if (!fragShader.compile_qk1xvd$(ctx)) {
                  var log_0 = fragShader.getInfoLog_qk1xvd$(ctx);
                  fragShader.delete_qk1xvd$(ctx);
                  throw new _.de.fabmax.kool.KoolException('Fragment shader compilation failed: ' + log_0);
                }
                var prog = _.de.fabmax.kool.ProgramResource.Companion.create_qk1xvd$(ctx);
                prog.attachShader_45k95$(vertShader, ctx);
                prog.attachShader_45k95$(fragShader, ctx);
                var success = prog.link_qk1xvd$(ctx);
                vertShader.delete_qk1xvd$(ctx);
                fragShader.delete_qk1xvd$(ctx);
                if (!success) {
                  var log_1 = prog.getInfoLog_qk1xvd$(ctx);
                  prog.delete_qk1xvd$(ctx);
                  throw new _.de.fabmax.kool.KoolException('Shader linkage failed: ' + log_1);
                }
                res = new _.de.fabmax.kool.ShaderManager.ShaderReferenceCounter(prog, 0);
                this.shaderProgramMap_0.put_wn2jw4$(source, res);
              }
              tmp$0 = res.referenceCount, tmp$1 = tmp$0, res.referenceCount = tmp$0 + 1, tmp$1;
              return res.prog;
            }
          }, /** @lends _.de.fabmax.kool.ShaderManager */ {
            ShaderReferenceCounter: Kotlin.createClass(null, function ShaderReferenceCounter(prog, referenceCount) {
              this.prog = prog;
              this.referenceCount = referenceCount;
            }, /** @lends _.de.fabmax.kool.ShaderManager.ShaderReferenceCounter.prototype */ {
              component1: function () {
                return this.prog;
              },
              component2: function () {
                return this.referenceCount;
              },
              copy_i0tefv$: function (prog, referenceCount) {
                return new _.de.fabmax.kool.ShaderManager.ShaderReferenceCounter(prog === void 0 ? this.prog : prog, referenceCount === void 0 ? this.referenceCount : referenceCount);
              },
              toString: function () {
                return 'ShaderReferenceCounter(prog=' + Kotlin.toString(this.prog) + (', referenceCount=' + Kotlin.toString(this.referenceCount)) + ')';
              },
              hashCode: function () {
                var result = 0;
                result = result * 31 + Kotlin.hashCode(this.prog) | 0;
                result = result * 31 + Kotlin.hashCode(this.referenceCount) | 0;
                return result;
              },
              equals_za3rmp$: function (other) {
                return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.prog, other.prog) && Kotlin.equals(this.referenceCount, other.referenceCount)))));
              }
            })
          }),
          Texture2d: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.GlObject];
          }, function Texture2d(props) {
            Texture2d.baseInitializer.call(this);
            this.props = props;
            this.isAvailable_9z2wh6$_0 = false;
            this.isLoaded_9z2wh6$_0 = false;
          }, /** @lends _.de.fabmax.kool.Texture2d.prototype */ {
            isAvailable: {
              get: function () {
                return this.isAvailable_9z2wh6$_0;
              },
              set: function (isAvailable_0) {
                this.isAvailable_9z2wh6$_0 = isAvailable_0;
              }
            },
            isLoaded: {
              get: function () {
                return this.isLoaded_9z2wh6$_0;
              },
              set: function (isLoaded_0) {
                this.isLoaded_9z2wh6$_0 = isLoaded_0;
              }
            },
            create_qk1xvd$: function (ctx) {
              this.res = _.de.fabmax.kool.TextureResource.Companion.create_cr2655$(_.de.fabmax.kool.platform.GL.Companion.TEXTURE_2D, this.props, ctx);
            },
            load_qk1xvd$: function (ctx) {
              var res = this.res;
              if (res != null && this.isAvailable) {
                this.loadData_d9oqyh$(res.target, 0, ctx);
                if (this.props.minFilter === _.de.fabmax.kool.platform.GL.Companion.LINEAR_MIPMAP_LINEAR) {
                  _.de.fabmax.kool.platform.GL.Companion.generateMipmap_za3lpa$(res.target);
                }
                this.isLoaded = true;
              }
            }
          }),
          BufferedTexture2d: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.Texture2d];
          }, function BufferedTexture2d(buffer, width, height, format, props) {
            if (props === void 0)
              props = _.de.fabmax.kool.TextureResource.Companion.DEFAULT_PROPERTIES;
            BufferedTexture2d.baseInitializer.call(this, props);
            this.buffer = buffer;
            this.width = width;
            this.height = height;
            this.format = format;
            this.isAvailable = true;
          }, /** @lends _.de.fabmax.kool.BufferedTexture2d.prototype */ {
            loadData_d9oqyh$: function (target, level, ctx) {
              var tmp$0;
              var limit = this.buffer.limit;
              var pos = this.buffer.position;
              this.buffer.flip();
              _.de.fabmax.kool.platform.GL.Companion.texImage2D_bz3wcs$(target, level, this.format, this.width, this.height, 0, this.format, _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_BYTE, this.buffer);
              this.buffer.limit = limit;
              this.buffer.position = pos;
              ctx.memoryMgr.memoryAllocated_0((tmp$0 = this.res) != null ? tmp$0 : Kotlin.throwNPE(), this.buffer.position);
            }
          }),
          SharedTexture: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.Texture2d];
          }, function SharedTexture(props) {
            SharedTexture.baseInitializer.call(this, props);
            this.texture = null;
          }, /** @lends _.de.fabmax.kool.SharedTexture.prototype */ {
            res: {
              get: function () {
                var tmp$0;
                return (tmp$0 = this.texture) != null ? tmp$0.res : null;
              },
              set: function (value) {
              }
            },
            isAvailable: {
              get: function () {
                var tmp$0, tmp$1;
                return (tmp$1 = (tmp$0 = this.texture) != null ? tmp$0.isAvailable : null) != null ? tmp$1 : false;
              },
              set: function (value) {
              }
            },
            isLoaded: {
              get: function () {
                var tmp$0, tmp$1;
                return (tmp$1 = (tmp$0 = this.texture) != null ? tmp$0.isLoaded : null) != null ? tmp$1 : false;
              },
              set: function (value) {
              }
            },
            loadData_d9oqyh$: function (target, level, ctx) {
              throw new Kotlin.UnsupportedOperationException("SharedTexture doesn't load any data, call must be forwarded to texture");
            },
            load_qk1xvd$: function (ctx) {
              var tmp$0;
              (tmp$0 = this.texture) != null ? tmp$0.load_qk1xvd$(ctx) : null;
            },
            create_qk1xvd$: function (ctx) {
              var tmp$0, tmp$1, tmp$2;
              if (!((tmp$1 = (tmp$0 = this.texture) != null ? tmp$0.isValid : null) != null ? tmp$1 : true)) {
                (tmp$2 = this.texture) != null ? tmp$2.create_qk1xvd$(ctx) : null;
              }
            },
            delete_qk1xvd$: function (ctx) {
              this.texture = null;
            }
          }),
          SharedAssetTexture: Kotlin.createClass(function () {
            return [_.de.fabmax.kool.SharedTexture];
          }, function SharedAssetTexture(assetPath, props) {
            if (props === void 0)
              props = _.de.fabmax.kool.TextureResource.Companion.DEFAULT_PROPERTIES;
            SharedAssetTexture.baseInitializer.call(this, props);
            this.assetPath = assetPath;
          }, /** @lends _.de.fabmax.kool.SharedAssetTexture.prototype */ {
            create_qk1xvd$: function (ctx) {
              if (this.texture == null) {
                this.texture = ctx.textureMgr.getAssetTexture_0(this.assetPath, this.props);
              }
              _.de.fabmax.kool.SharedTexture.prototype.create_qk1xvd$.call(this, ctx);
            },
            delete_qk1xvd$: function (ctx) {
              ctx.textureMgr.deleteReference_0(this.texture, ctx);
              _.de.fabmax.kool.SharedTexture.prototype.delete_qk1xvd$.call(this, ctx);
            }
          }),
          TextureManager: Kotlin.createClass(null, function TextureManager() {
            this.texReferenceMap_0 = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
            this.assetTexMap_0 = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
            this.assetPaths_0 = Kotlin.kotlin.collections.mutableMapOf_eoa9s7$([]);
            this.activeTexUnit_0 = 0;
            this.boundTextures_0 = Kotlin.arrayFromFun(_.de.fabmax.kool.TextureManager.Companion.TEXTURE_UNITS, _.de.fabmax.kool.TextureManager.boundTextures_0$f);
          }, /** @lends _.de.fabmax.kool.TextureManager.prototype */ {
            deleteReference_0: function (tex, ctx) {
              if (tex != null) {
                var counter = this.texReferenceMap_0.get_za3rmp$(tex);
                if (counter != null) {
                  if ((counter.referenceCount = counter.referenceCount - 1, counter.referenceCount) === 0) {
                    tex.delete_qk1xvd$(ctx);
                    this.texReferenceMap_0.remove_za3rmp$(tex);
                    var assetPath = this.assetPaths_0.get_za3rmp$(tex);
                    if (assetPath != null) {
                      this.assetTexMap_0.remove_za3rmp$(assetPath);
                    }
                  }
                }
              }
            },
            getAssetTexture_0: function (path, props) {
              var tmp$0, tmp$1;
              var tex = this.assetTexMap_0.get_za3rmp$(path);
              if (tex == null) {
                tex = _.de.fabmax.kool.platform.Platform.Companion.loadTexture_2lkaa8$(path, props);
                this.assetTexMap_0.put_wn2jw4$(path, tex);
                this.assetPaths_0.put_wn2jw4$(tex, path);
              }
              var counter = this.texReferenceMap_0.get_za3rmp$(tex);
              if (counter == null) {
                counter = new _.de.fabmax.kool.TextureManager.TextureReferenceCounter(tex, 0);
                this.texReferenceMap_0.put_wn2jw4$(tex, counter);
              }
              tmp$0 = counter.referenceCount, tmp$1 = tmp$0, counter.referenceCount = tmp$0 + 1, tmp$1;
              return tex;
            },
            bindTexture2d_k73rlx$: function (texture, texUnit, ctx) {
              if (texUnit !== this.activeTexUnit_0) {
                _.de.fabmax.kool.platform.GL.Companion.activeTexture_za3lpa$(_.de.fabmax.kool.platform.GL.Companion.TEXTURE0 + texUnit);
                this.activeTexUnit_0 = texUnit;
              }
              if (texture != null && !texture.isValid) {
                texture.create_qk1xvd$(ctx);
                this.boundTextures_0[texUnit] = texture.res;
              }
              var texRes = texture != null ? texture.res : null;
              if (!Kotlin.equals(texRes, this.boundTextures_0[texUnit])) {
                _.de.fabmax.kool.platform.GL.Companion.bindTexture_qdzudy$(_.de.fabmax.kool.platform.GL.Companion.TEXTURE_2D, texRes);
                this.boundTextures_0[texUnit] = texRes;
              }
              if (texture != null && !texture.isLoaded && texture.isAvailable) {
                texture.load_qk1xvd$(ctx);
              }
            }
          }, /** @lends _.de.fabmax.kool.TextureManager */ {
            Companion: Kotlin.createObject(null, function Companion() {
              _.de.fabmax.kool.TextureManager.Companion.TEXTURE_UNITS = 32;
            }),
            object_initializer$: function () {
              _.de.fabmax.kool.TextureManager.Companion;
            },
            TextureReferenceCounter: Kotlin.createClass(null, function TextureReferenceCounter(tex, referenceCount) {
              this.tex = tex;
              this.referenceCount = referenceCount;
            }, /** @lends _.de.fabmax.kool.TextureManager.TextureReferenceCounter.prototype */ {
              component1: function () {
                return this.tex;
              },
              component2: function () {
                return this.referenceCount;
              },
              copy_ca97km$: function (tex, referenceCount) {
                return new _.de.fabmax.kool.TextureManager.TextureReferenceCounter(tex === void 0 ? this.tex : tex, referenceCount === void 0 ? this.referenceCount : referenceCount);
              },
              toString: function () {
                return 'TextureReferenceCounter(tex=' + Kotlin.toString(this.tex) + (', referenceCount=' + Kotlin.toString(this.referenceCount)) + ')';
              },
              hashCode: function () {
                var result = 0;
                result = result * 31 + Kotlin.hashCode(this.tex) | 0;
                result = result * 31 + Kotlin.hashCode(this.referenceCount) | 0;
                return result;
              },
              equals_za3rmp$: function (other) {
                return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.tex, other.tex) && Kotlin.equals(this.referenceCount, other.referenceCount)))));
              }
            }),
            boundTextures_0$f: function (i) {
              return null;
            }
          }),
          scene: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.scene */ {
            Camera: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.scene.Node];
            }, function Camera(name) {
              if (name === void 0)
                name = 'camera';
              Camera.baseInitializer.call(this, name);
              this.position = new _.de.fabmax.kool.util.MutableVec3f(0.0, 0.0, 10.0);
              this.lookAt = _.de.fabmax.kool.util.MutableVec3f_init_cx11x8$(_.de.fabmax.kool.util.Vec3f.Companion.ZERO);
              this.up = _.de.fabmax.kool.util.MutableVec3f_init_cx11x8$(_.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
              this.fovy = 60.0;
              this.clipNear = 0.20000000298023224;
              this.clipFar = 200.0;
              this.aspectRatio_6x5k8a$_0 = 1.0;
              this.tmpPos_0 = _.de.fabmax.kool.util.MutableVec3f_init();
              this.tmpLookAt_0 = _.de.fabmax.kool.util.MutableVec3f_init();
              this.tmpUp_0 = _.de.fabmax.kool.util.MutableVec3f_init();
            }, /** @lends _.de.fabmax.kool.scene.Camera.prototype */ {
              aspectRatio: {
                get: function () {
                  return this.aspectRatio_6x5k8a$_0;
                },
                set: function (aspectRatio_0) {
                  this.aspectRatio_6x5k8a$_0 = aspectRatio_0;
                }
              },
              updateCamera_qk1xvd$: function (ctx) {
                this.aspectRatio = ctx.viewportWidth / ctx.viewportHeight;
                this.updateViewMatrix_qk1xvd$(ctx);
                this.updateProjectionMatrix_qk1xvd$(ctx);
                ctx.mvpState.update_qk1xvd$(ctx);
              },
              updateViewMatrix_qk1xvd$: function (ctx) {
                this.toGlobalCoords_64rgyf$(this.tmpPos_0.set_cx11x8$(this.position));
                this.toGlobalCoords_64rgyf$(this.tmpLookAt_0.set_cx11x8$(this.lookAt));
                this.toGlobalCoords_64rgyf$(this.tmpUp_0.set_cx11x8$(this.up), 0.0);
                ctx.mvpState.viewMatrix.setLookAt_hd42to$(this.tmpPos_0, this.tmpLookAt_0, this.tmpUp_0);
              },
              updateProjectionMatrix_qk1xvd$: function (ctx) {
                ctx.mvpState.projMatrix.setPerspective_7b5o5w$(this.fovy, this.aspectRatio, this.clipNear, this.clipFar);
              }
            }),
            group_30wlp3$: function (name, block) {
              if (name === void 0)
                name = null;
              var grp = new _.de.fabmax.kool.scene.Group(name);
              block.call(grp);
              return grp;
            },
            Group: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.scene.Node];
            }, function Group(name) {
              if (name === void 0)
                name = null;
              Group.baseInitializer.call(this, name);
              this.children = Kotlin.kotlin.collections.mutableListOf_9mqe4v$([]);
            }, /** @lends _.de.fabmax.kool.scene.Group.prototype */ {
              render_qk1xvd$: function (ctx) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                tmp$0 = Kotlin.kotlin.collections.get_indices_mwto7b$(this.children);
                tmp$1 = tmp$0.first;
                tmp$2 = tmp$0.last;
                tmp$3 = tmp$0.step;
                for (var i = tmp$1; i <= tmp$2; i += tmp$3) {
                  this.children.get_za3lpa$(i).render_qk1xvd$(ctx);
                }
              },
              delete_qk1xvd$: function (ctx) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                tmp$0 = Kotlin.kotlin.collections.get_indices_mwto7b$(this.children);
                tmp$1 = tmp$0.first;
                tmp$2 = tmp$0.last;
                tmp$3 = tmp$0.step;
                for (var i = tmp$1; i <= tmp$2; i += tmp$3) {
                  this.children.get_za3lpa$(i).delete_qk1xvd$(ctx);
                }
              },
              findByName_61zpoe$: function (name) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                if (Kotlin.equals(name, this.name)) {
                  return this;
                }
                tmp$0 = Kotlin.kotlin.collections.get_indices_mwto7b$(this.children);
                tmp$1 = tmp$0.first;
                tmp$2 = tmp$0.last;
                tmp$3 = tmp$0.step;
                for (var i = tmp$1; i <= tmp$2; i += tmp$3) {
                  var nd = this.children.get_za3lpa$(i).findByName_61zpoe$(name);
                  if (nd != null) {
                    return nd;
                  }
                }
                return null;
              },
              addNode_f1kmr1$: function (node) {
                this.children.add_za3rmp$(node);
                node.parent = this;
              },
              removeNode_f1kmr1$: function (node) {
                if (this.children.remove_za3rmp$(node)) {
                  node.parent = null;
                  return true;
                }
                return false;
              },
              plusAssign_f1kmr1$: function (node) {
                this.addNode_f1kmr1$(node);
              },
              minusAssign_f1kmr1$: function (node) {
                this.removeNode_f1kmr1$(node);
              },
              unaryPlus_uv0sin$: function ($receiver) {
                this.addNode_f1kmr1$($receiver);
              }
            }),
            Light: Kotlin.createClass(null, function Light() {
              this.direction = new _.de.fabmax.kool.util.MutableVec3f(1.0, 1.0, 1.0);
              this.color = _.de.fabmax.kool.util.MutableColor_init_d7aj7k$(_.de.fabmax.kool.util.Color.Companion.WHITE);
            }),
            Mesh: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.scene.Node];
            }, function Mesh(hasNormals, hasColors, hasTexCoords, name) {
              if (name === void 0)
                name = null;
              Mesh.baseInitializer.call(this, name);
              this.hasNormals = hasNormals;
              this.hasColors = hasColors;
              this.hasTexCoords = hasTexCoords;
              this.data = new _.de.fabmax.kool.util.IndexedVertexList(this.hasNormals, this.hasColors, this.hasTexCoords);
              this.dataBuf = null;
              this.idxBuf = null;
              this.idxCount = 0;
              this.syncBuffers = false;
              this.batchUpdate_uvqnry$_0 = false;
              this.positionBinder = null;
              this.normalBinder = null;
              this.texCoordBinder = null;
              this.colorBinder = null;
              this.shader = null;
            }, /** @lends _.de.fabmax.kool.scene.Mesh.prototype */ {
              batchUpdate: {
                get: function () {
                  return this.batchUpdate_uvqnry$_0;
                },
                set: function (value) {
                  this.batchUpdate_uvqnry$_0 = value;
                }
              },
              addVertex_570ote$: function (init) {
                var idx = {v: 0};
                this.syncBuffers = true;
                idx.v = this.data.addVertex_570ote$(init);
                return idx.v;
              },
              addVertex_f9a7mx$: function (position, normal, color, texCoord) {
                if (normal === void 0)
                  normal = null;
                if (color === void 0)
                  color = null;
                if (texCoord === void 0)
                  texCoord = null;
                var idx = {v: 0};
                var closure$normal = normal;
                var closure$color = color;
                var closure$texCoord = texCoord;
                this.syncBuffers = true;
                idx.v = this.data.addVertex_f9a7mx$(position, closure$normal, closure$color, closure$texCoord);
                return idx.v;
              },
              addIndex_za3lpa$: function (idx) {
                this.data.addIndex_za3lpa$(idx);
                this.syncBuffers = true;
              },
              addTriIndices_qt1dr2$: function (i0, i1, i2) {
                this.data.addIndex_za3lpa$(i0);
                this.data.addIndex_za3lpa$(i1);
                this.data.addIndex_za3lpa$(i2);
                this.syncBuffers = true;
              },
              addIndices_q5rwfd$: function (indices) {
                this.data.addIndices_q5rwfd$(indices);
                this.syncBuffers = true;
              },
              clear: function () {
                this.data.clear();
                this.syncBuffers = true;
              },
              delete_qk1xvd$: function (ctx) {
                var tmp$0, tmp$1;
                (tmp$0 = this.idxBuf) != null ? tmp$0.delete_qk1xvd$(ctx) : null;
                (tmp$1 = this.dataBuf) != null ? tmp$1.delete_qk1xvd$(ctx) : null;
              },
              render_qk1xvd$: function (ctx) {
                if (!this.isVisible) {
                  return;
                }
                this.checkBuffers_n9i84b$_0(ctx);
                if (this.positionBinder == null) {
                  throw new Kotlin.IllegalStateException('Vertex positions attribute binder is null');
                }
                ctx.shaderMgr.bindShader_fyq94j$(this.shader, ctx);
                var boundShader = ctx.shaderMgr.boundShader;
                if (boundShader != null) {
                  boundShader.bindMesh_ovcwxf$(this, ctx);
                  this.drawElements_qk1xvd$(ctx);
                  boundShader.unbindMesh_qk1xvd$(ctx);
                }
              },
              drawElements_qk1xvd$: function (ctx) {
                var tmp$0;
                (tmp$0 = this.idxBuf) != null ? tmp$0.bind_qk1xvd$(ctx) : null;
                _.de.fabmax.kool.platform.GL.Companion.drawElements_tjonv8$(_.de.fabmax.kool.platform.GL.Companion.TRIANGLES, this.idxCount, _.de.fabmax.kool.platform.GL.Companion.UNSIGNED_INT, 0);
              },
              checkBuffers_n9i84b$_0: function (ctx) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                if (this.idxBuf == null) {
                  this.idxBuf = _.de.fabmax.kool.BufferResource.Companion.create_8ffywt$(_.de.fabmax.kool.platform.GL.Companion.ELEMENT_ARRAY_BUFFER, ctx);
                }
                if (this.dataBuf == null) {
                  this.dataBuf = _.de.fabmax.kool.BufferResource.Companion.create_8ffywt$(_.de.fabmax.kool.platform.GL.Companion.ARRAY_BUFFER, ctx);
                  this.positionBinder = new _.de.fabmax.kool.shading.VboBinder((tmp$0 = this.dataBuf) != null ? tmp$0 : Kotlin.throwNPE(), 3, this.data.strideBytes);
                  if (this.hasNormals) {
                    this.normalBinder = new _.de.fabmax.kool.shading.VboBinder((tmp$1 = this.dataBuf) != null ? tmp$1 : Kotlin.throwNPE(), 3, this.data.strideBytes, this.data.normalOffset);
                  }
                  if (this.hasColors) {
                    this.colorBinder = new _.de.fabmax.kool.shading.VboBinder((tmp$2 = this.dataBuf) != null ? tmp$2 : Kotlin.throwNPE(), 4, this.data.strideBytes, this.data.colorOffset);
                  }
                  if (this.hasTexCoords) {
                    this.texCoordBinder = new _.de.fabmax.kool.shading.VboBinder((tmp$3 = this.dataBuf) != null ? tmp$3 : Kotlin.throwNPE(), 2, this.data.strideBytes, this.data.texCoordOffset);
                  }
                }
                if (this.syncBuffers && !this.batchUpdate) {
                  var tmp$5, tmp$4;
                  if (!this.batchUpdate) {
                    this.idxCount = this.data.indices.position;
                    (tmp$5 = this.idxBuf) != null ? tmp$5.setData_p3ykg2$(this.data.indices, _.de.fabmax.kool.platform.GL.Companion.STATIC_DRAW, ctx) : null;
                    (tmp$4 = this.dataBuf) != null ? tmp$4.setData_o0h6ow$(this.data.data, _.de.fabmax.kool.platform.GL.Companion.STATIC_DRAW, ctx) : null;
                    this.syncBuffers = false;
                  }
                }
              }
            }, /** @lends _.de.fabmax.kool.scene.Mesh */ {
            }),
            Node: Kotlin.createClass(null, function Node$(name) {
              if (name === void 0)
                name = null;
              this.name = name;
              this.parent = null;
              this.isVisible = true;
            }, /** @lends _.de.fabmax.kool.scene.Node.prototype */ {
              render_qk1xvd$: function (ctx) {
              },
              delete_qk1xvd$: function (ctx) {
              },
              toGlobalCoords_64rgyf$: function (vec, w) {
                var tmp$0;
                if (w === void 0)
                  w = 1.0;
                (tmp$0 = this.parent) != null ? tmp$0.toGlobalCoords_64rgyf$(vec, w) : null;
                return vec;
              },
              toLocalCoords_64rgyf$: function (vec, w) {
                var tmp$0;
                if (w === void 0)
                  w = 1.0;
                (tmp$0 = this.parent) != null ? tmp$0.toLocalCoords_64rgyf$(vec) : null;
                return vec;
              },
              findByName_61zpoe$: function (name) {
                if (Kotlin.equals(name, this.name)) {
                  return this;
                }
                return null;
              }
            }),
            Scene: Kotlin.createClass(null, function Scene() {
              this.camera = new _.de.fabmax.kool.scene.Camera();
              this.light = new _.de.fabmax.kool.scene.Light();
              this.root = null;
            }, /** @lends _.de.fabmax.kool.scene.Scene.prototype */ {
              onRender_qk1xvd$: function (ctx) {
                var tmp$0;
                this.camera.updateCamera_qk1xvd$(ctx);
                (tmp$0 = this.root) != null ? tmp$0.render_qk1xvd$(ctx) : null;
              }
            }),
            sphericalInputTransform_n6upd5$: function (name, block) {
              if (name === void 0)
                name = null;
              var sit = new _.de.fabmax.kool.scene.SphericalInputTransform(name);
              block.call(sit);
              return sit;
            },
            SphericalInputTransform: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.scene.TransformGroup];
            }, function SphericalInputTransform(name) {
              if (name === void 0)
                name = null;
              SphericalInputTransform.baseInitializer.call(this, name);
              this.stiffness_sydd1i$_0 = 0.0;
              this.damping_sydd1i$_0 = 0.0;
              this.animRotV_sydd1i$_0 = new _.de.fabmax.kool.scene.SphericalInputTransform.AnimatedVal(this, 0.0);
              this.animRotH_sydd1i$_0 = new _.de.fabmax.kool.scene.SphericalInputTransform.AnimatedVal(this, 0.0);
              this.animZoom_sydd1i$_0 = new _.de.fabmax.kool.scene.SphericalInputTransform.AnimatedVal(this, 1.0);
              this.verticalAxis = _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS;
              this.horizontalAxis = _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS;
              this.verticalRotation = 0.0;
              this.horizontalRotation = 0.0;
              this.zoom = 1.0;
              this.minZoom = 0.10000000149011612;
              this.maxZoom = 10.0;
              this.smoothness_sydd1i$_0 = 0.0;
              this.smoothness = 0.10000000149011612;
            }, /** @lends _.de.fabmax.kool.scene.SphericalInputTransform.prototype */ {
              smoothness: {
                get: function () {
                  return this.smoothness_sydd1i$_0;
                },
                set: function (value) {
                  this.smoothness_sydd1i$_0 = value;
                  if (!_.de.fabmax.kool.util.isZero_mx4ult$(value)) {
                    this.stiffness_sydd1i$_0 = 10.0 / value;
                    this.damping_sydd1i$_0 = 2.0 * Math.sqrt(this.stiffness_sydd1i$_0);
                  }
                }
              },
              setRotation_dleff0$: function (vertical, horizontal) {
                this.animRotV_sydd1i$_0.set_mx4ult$(vertical);
                this.animRotH_sydd1i$_0.set_mx4ult$(horizontal);
                this.verticalRotation = vertical;
                this.horizontalRotation = horizontal;
              },
              render_qk1xvd$: function (ctx) {
                var pointer = ctx.inputHandler.primaryPointer;
                if (pointer.isValid) {
                  if (!_.de.fabmax.kool.util.isZero_14dthe$(pointer.deltaScroll)) {
                    this.zoom *= 1.0 + pointer.deltaScroll / 10.0;
                    this.zoom = _.de.fabmax.kool.util.clamp_y2kzbl$(this.zoom, this.minZoom, this.maxZoom);
                  }
                  if (pointer.isValid && pointer.isLeftButtonDown) {
                    this.verticalRotation -= pointer.deltaX / 3;
                    this.horizontalRotation -= pointer.deltaY / 3;
                    this.horizontalRotation = _.de.fabmax.kool.util.clamp_y2kzbl$(this.horizontalRotation, -90.0, 90.0);
                  }
                  this.animRotV_sydd1i$_0.desired = this.verticalRotation;
                  this.animRotH_sydd1i$_0.desired = this.horizontalRotation;
                  this.animZoom_sydd1i$_0.desired = this.zoom;
                }
                var z = this.animZoom_sydd1i$_0.animate_mx4ult$(ctx.deltaT);
                this.setIdentity();
                this.scale_y2kzbl$(z, z, z);
                this.rotate_ag3lbb$(this.animRotV_sydd1i$_0.animate_mx4ult$(ctx.deltaT), this.verticalAxis);
                this.rotate_ag3lbb$(this.animRotH_sydd1i$_0.animate_mx4ult$(ctx.deltaT), this.horizontalAxis);
                _.de.fabmax.kool.scene.TransformGroup.prototype.render_qk1xvd$.call(this, ctx);
              }
            }, /** @lends _.de.fabmax.kool.scene.SphericalInputTransform */ {
              AnimatedVal: Kotlin.createClass(null, function AnimatedVal($outer, value) {
                this.$outer = $outer;
                this.desired = value;
                this.actual = value;
                this.speed = 0.0;
              }, /** @lends _.de.fabmax.kool.scene.SphericalInputTransform.AnimatedVal.prototype */ {
                set_mx4ult$: function (value) {
                  this.desired = value;
                  this.actual = value;
                },
                animate_mx4ult$: function (deltaT) {
                  if (_.de.fabmax.kool.util.isZero_mx4ult$(this.$outer.smoothness) || deltaT > 0.20000000298023224) {
                    this.actual = this.desired;
                    return this.actual;
                  }
                  var t = 0.0;
                  while (t < deltaT) {
                    var dt = Math.min(0.05, deltaT - t);
                    t += dt + 0.0010000000474974513;
                    var err = this.desired - this.actual;
                    this.speed += (err * this.$outer.stiffness_sydd1i$_0 - this.speed * this.$outer.damping_sydd1i$_0) * dt;
                    this.actual += this.speed * dt;
                  }
                  return this.actual;
                }
              })
            }),
            transformGroup_2byx8j$: function (name, block) {
              if (name === void 0)
                name = null;
              var tg = new _.de.fabmax.kool.scene.TransformGroup(name);
              block.call(tg);
              return tg;
            },
            TransformGroup: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.scene.Group];
            }, function TransformGroup(name) {
              if (name === void 0)
                name = null;
              TransformGroup.baseInitializer.call(this, name);
              this.transform = new _.de.fabmax.kool.util.Mat4f();
              this.invTransform = new _.de.fabmax.kool.util.Mat4f();
              this.transformDirty = true;
              this.animation_s8s9p8$_0 = null;
            }, /** @lends _.de.fabmax.kool.scene.TransformGroup.prototype */ {
              animation: {
                get: function () {
                  return this.animation_s8s9p8$_0;
                },
                set: function (animation_0) {
                  this.animation_s8s9p8$_0 = animation_0;
                }
              },
              checkInverse: function () {
                if (this.transformDirty) {
                  this.transform.invert_d21ekx$(this.invTransform);
                  this.transformDirty = false;
                }
              },
              render_qk1xvd$: function (ctx) {
                var tmp$0;
                (tmp$0 = this.animation) != null ? tmp$0.call(this, ctx) : null;
                ctx.mvpState.modelMatrix.push();
                ctx.mvpState.modelMatrix.mul_d21ekx$(this.transform);
                ctx.mvpState.update_qk1xvd$(ctx);
                _.de.fabmax.kool.scene.Group.prototype.render_qk1xvd$.call(this, ctx);
                ctx.mvpState.modelMatrix.pop();
                ctx.mvpState.update_qk1xvd$(ctx);
              },
              toGlobalCoords_64rgyf$: function (vec, w) {
                if (w === void 0)
                  w = 1.0;
                this.transform.transform_64rgyf$(vec, w);
                return _.de.fabmax.kool.scene.Group.prototype.toGlobalCoords_64rgyf$.call(this, vec, w);
              },
              toLocalCoords_64rgyf$: function (vec, w) {
                if (w === void 0)
                  w = 1.0;
                this.checkInverse();
                _.de.fabmax.kool.scene.Group.prototype.toLocalCoords_64rgyf$.call(this, vec, w);
                return this.invTransform.transform_64rgyf$(vec, w);
              },
              translate_y2kzbl$: function (tx, ty, tz) {
                this.transform.translate_y2kzbl$(tx, ty, tz);
                this.transformDirty = true;
                return this;
              },
              rotate_ag3lbb$: function (angleDeg, axis) {
                return this.rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
              },
              rotate_7b5o5w$: function (angleDeg, axX, axY, axZ) {
                this.transform.rotate_7b5o5w$(angleDeg, axX, axY, axZ);
                this.transformDirty = true;
                return this;
              },
              rotateEuler_y2kzbl$: function (xDeg, yDeg, zDeg) {
                this.transform.rotateEuler_y2kzbl$(xDeg, yDeg, zDeg);
                this.transformDirty = true;
                return this;
              },
              scale_y2kzbl$: function (sx, sy, sz) {
                this.transform.scale_y2kzbl$(sx, sy, sz);
                this.transformDirty = true;
                return this;
              },
              mul_d21ekx$: function (mat) {
                this.transform.mul_d21ekx$(mat);
                this.transformDirty = true;
                return this;
              },
              set_d21ekx$: function (mat) {
                this.transform.set_d21ekx$(mat);
                this.transformDirty = true;
                return this;
              },
              setIdentity: function () {
                this.transform.setIdentity();
                this.transformDirty = true;
                return this;
              }
            })
          }),
          shading: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.shading */ {
            basicShader_9yb6un$: function (propsInit) {
              return new _.de.fabmax.kool.shading.BasicShader(new _.de.fabmax.kool.shading.ShaderProps(propsInit));
            },
            BasicShader: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Shader];
            }, function BasicShader(props, generator) {
              if (generator === void 0)
                generator = _.de.fabmax.kool.platform.Platform.Companion.createDefaultShaderGenerator();
              BasicShader.baseInitializer.call(this, generator.generate_9em3xa$(props));
              this.generator_x3ny4s$_0 = generator;
              this.shininess = 20.0;
              this.specularIntensity = 0.75;
              this.staticColor.set_cx11wd$(_.de.fabmax.kool.util.Color.Companion.RED);
              this.alpha = 1.0;
              this.saturation = 1.0;
              this.fogRange = 250.0;
              this.fogColor.set_cx11wd$(_.de.fabmax.kool.util.Color.Companion.LIGHT_GRAY);
            }, /** @lends _.de.fabmax.kool.shading.BasicShader.prototype */ {
              lightColor: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformLightColor.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformLightColor.value.set_cx11x8$(value);
                }
              },
              lightDirection: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformLightDirection.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformLightDirection.value.set_cx11x8$(value);
                }
              },
              cameraPosition: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformCameraPosition.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformCameraPosition.value.set_cx11x8$(value);
                }
              },
              shininess: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformShininess.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformShininess.value = value;
                }
              },
              specularIntensity: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformSpecularIntensity.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformSpecularIntensity.value = value;
                }
              },
              staticColor: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformStaticColor.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformStaticColor.value.set_cx11wd$(value);
                }
              },
              texture: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformTexture.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformTexture.value = value;
                }
              },
              alpha: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformAlpha.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformAlpha.value = value;
                }
              },
              saturation: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformSaturation.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformSaturation.value = value;
                }
              },
              fogColor: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformFogColor.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformFogColor.value.set_cx11wd$(value);
                }
              },
              fogRange: {
                get: function () {
                  return this.generator_x3ny4s$_0.uniformFogRange.value;
                },
                set: function (value) {
                  this.generator_x3ny4s$_0.uniformFogRange.value = value;
                }
              },
              onLoad_qk1xvd$: function (ctx) {
                _.de.fabmax.kool.shading.Shader.prototype.onLoad_qk1xvd$.call(this, ctx);
                this.generator_x3ny4s$_0.onLoad_spwa6e$(this);
              },
              onBind_qk1xvd$: function (ctx) {
                this.onMatrixUpdate_qk1xvd$(ctx);
                this.cameraPosition.set_y2kzbl$(0.0, 0.0, 0.0);
                this.generator_x3ny4s$_0.uniformCameraPosition.bind_cbpn8s$_0(ctx);
                var light = ctx.scene.light;
                this.lightDirection.set_cx11x8$(light.direction);
                this.generator_x3ny4s$_0.uniformLightDirection.bind_cbpn8s$_0(ctx);
                this.lightColor.set_y2kzbl$(light.color.r, light.color.g, light.color.b);
                this.generator_x3ny4s$_0.uniformLightColor.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformFogColor.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformFogRange.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformSaturation.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformAlpha.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformShininess.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformSpecularIntensity.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformStaticColor.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformTexture.bind_cbpn8s$_0(ctx);
              },
              onMatrixUpdate_qk1xvd$: function (ctx) {
                this.generator_x3ny4s$_0.uniformMvpMatrix.value = ctx.mvpState.mvpMatrixBuffer;
                this.generator_x3ny4s$_0.uniformMvpMatrix.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformViewMatrix.value = ctx.mvpState.viewMatrixBuffer;
                this.generator_x3ny4s$_0.uniformViewMatrix.bind_cbpn8s$_0(ctx);
                this.generator_x3ny4s$_0.uniformModelMatrix.value = ctx.mvpState.modelMatrixBuffer;
                this.generator_x3ny4s$_0.uniformModelMatrix.bind_cbpn8s$_0(ctx);
              }
            }),
            Shader: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.GlObject];
            }, function Shader(source) {
              Shader.baseInitializer.call(this);
              this.source_ffq6a0$_0 = source;
              this.attributePositions = -1;
              this.attributeNormals = -1;
              this.attributeTexCoords = -1;
              this.attributeColors = -1;
            }, /** @lends _.de.fabmax.kool.shading.Shader.prototype */ {
              source: {
                get: function () {
                  return this.source_ffq6a0$_0;
                },
                set: function (source_0) {
                  this.source_ffq6a0$_0 = source_0;
                }
              },
              isBound_qk1xvd$: function (ctx) {
                return ctx.shaderMgr.boundShader === this;
              },
              onLoad_qk1xvd$: function (ctx) {
                this.res = ctx.shaderMgr.compile_0(this.source, ctx);
              },
              findAttributeLocation_61zpoe$: function (attribName) {
                var ref = this.res;
                if (ref != null) {
                  return _.de.fabmax.kool.platform.GL.Companion.getAttribLocation_dlxpy3$(ref, attribName);
                }
                 else {
                  return -1;
                }
              },
              enableAttribute_91bdg2$: function (attribute, attribName) {
                var location = this.findAttributeLocation_61zpoe$(attribName);
                this.enableAttribute_iixlpu$(attribute, location);
                return location >= 0;
              },
              enableAttribute_iixlpu$: function (attribute, location) {
                if (Kotlin.equals(attribute, _.de.fabmax.kool.shading.Shader.Attribute.POSITIONS))
                  this.attributePositions = location;
                else if (Kotlin.equals(attribute, _.de.fabmax.kool.shading.Shader.Attribute.NORMALS))
                  this.attributeNormals = location;
                else if (Kotlin.equals(attribute, _.de.fabmax.kool.shading.Shader.Attribute.TEXTURE_COORDS))
                  this.attributeTexCoords = location;
                else if (Kotlin.equals(attribute, _.de.fabmax.kool.shading.Shader.Attribute.COLORS))
                  this.attributeColors = location;
              },
              findUniformLocation_61zpoe$: function (uniformName) {
                var ref = this.res;
                if (ref != null) {
                  return _.de.fabmax.kool.platform.GL.Companion.getUniformLocation_dlxpy3$(ref, uniformName);
                }
                 else {
                  return null;
                }
              },
              bindMesh_ovcwxf$: function (mesh, ctx) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                if (this.attributePositions !== -1) {
                  tmp$0 = mesh.positionBinder;
                  if (tmp$0 == null) {
                    throw new Kotlin.NullPointerException('Mesh must supply an attribute binder for vertex positions');
                  }
                  var binder = tmp$0;
                  _.de.fabmax.kool.platform.GL.Companion.enableVertexAttribArray_za3lpa$(this.attributePositions);
                  binder.bindAttribute_8ffywt$(this.attributePositions, ctx);
                }
                if (this.attributeNormals !== -1) {
                  tmp$1 = mesh.normalBinder;
                  if (tmp$1 == null) {
                    throw new Kotlin.NullPointerException('Mesh must supply an attribute binder for vertex normals');
                  }
                  var binder_0 = tmp$1;
                  _.de.fabmax.kool.platform.GL.Companion.enableVertexAttribArray_za3lpa$(this.attributeNormals);
                  binder_0.bindAttribute_8ffywt$(this.attributeNormals, ctx);
                }
                if (this.attributeTexCoords !== -1) {
                  tmp$2 = mesh.texCoordBinder;
                  if (tmp$2 == null) {
                    throw new Kotlin.NullPointerException('Mesh must supply an attribute binder for vertex texture coordinates');
                  }
                  var binder_1 = tmp$2;
                  _.de.fabmax.kool.platform.GL.Companion.enableVertexAttribArray_za3lpa$(this.attributeTexCoords);
                  binder_1.bindAttribute_8ffywt$(this.attributeTexCoords, ctx);
                }
                if (this.attributeColors !== -1) {
                  tmp$3 = mesh.colorBinder;
                  if (tmp$3 == null) {
                    throw new Kotlin.NullPointerException('Mesh must supply an attribute binder for vertex colors');
                  }
                  var binder_2 = tmp$3;
                  _.de.fabmax.kool.platform.GL.Companion.enableVertexAttribArray_za3lpa$(this.attributeColors);
                  binder_2.bindAttribute_8ffywt$(this.attributeColors, ctx);
                }
              },
              unbindMesh_qk1xvd$: function (ctx) {
                if (this.attributePositions !== -1) {
                  _.de.fabmax.kool.platform.GL.Companion.disableVertexAttribArray_za3lpa$(this.attributePositions);
                }
                if (this.attributeNormals !== -1) {
                  _.de.fabmax.kool.platform.GL.Companion.disableVertexAttribArray_za3lpa$(this.attributeNormals);
                }
                if (this.attributeTexCoords !== -1) {
                  _.de.fabmax.kool.platform.GL.Companion.disableVertexAttribArray_za3lpa$(this.attributeTexCoords);
                }
                if (this.attributeColors !== -1) {
                  _.de.fabmax.kool.platform.GL.Companion.disableVertexAttribArray_za3lpa$(this.attributeColors);
                }
              },
              delete_qk1xvd$: function (ctx) {
                if (this.isValid) {
                  ctx.shaderMgr.deleteShader_0(this, ctx);
                  this.res = null;
                }
              }
            }, /** @lends _.de.fabmax.kool.shading.Shader */ {
              Source: Kotlin.createClass(null, function Source(vertexSrc, fragmentSrc) {
                this.vertexSrc = vertexSrc;
                this.fragmentSrc = fragmentSrc;
              }, /** @lends _.de.fabmax.kool.shading.Shader.Source.prototype */ {
                component1: function () {
                  return this.vertexSrc;
                },
                component2: function () {
                  return this.fragmentSrc;
                },
                copy_puj7f4$: function (vertexSrc, fragmentSrc) {
                  return new _.de.fabmax.kool.shading.Shader.Source(vertexSrc === void 0 ? this.vertexSrc : vertexSrc, fragmentSrc === void 0 ? this.fragmentSrc : fragmentSrc);
                },
                toString: function () {
                  return 'Source(vertexSrc=' + Kotlin.toString(this.vertexSrc) + (', fragmentSrc=' + Kotlin.toString(this.fragmentSrc)) + ')';
                },
                hashCode: function () {
                  var result = 0;
                  result = result * 31 + Kotlin.hashCode(this.vertexSrc) | 0;
                  result = result * 31 + Kotlin.hashCode(this.fragmentSrc) | 0;
                  return result;
                },
                equals_za3rmp$: function (other) {
                  return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.vertexSrc, other.vertexSrc) && Kotlin.equals(this.fragmentSrc, other.fragmentSrc)))));
                }
              }),
              Attribute: Kotlin.createEnumClass(function () {
                return [Kotlin.Enum];
              }, function Attribute() {
                Attribute.baseInitializer.call(this);
              }, function () {
                return {
                  POSITIONS: function () {
                    return new _.de.fabmax.kool.shading.Shader.Attribute();
                  },
                  NORMALS: function () {
                    return new _.de.fabmax.kool.shading.Shader.Attribute();
                  },
                  TEXTURE_COORDS: function () {
                    return new _.de.fabmax.kool.shading.Shader.Attribute();
                  },
                  COLORS: function () {
                    return new _.de.fabmax.kool.shading.Shader.Attribute();
                  }
                };
              })
            }),
            LightModel: Kotlin.createEnumClass(function () {
              return [Kotlin.Enum];
            }, function LightModel() {
              LightModel.baseInitializer.call(this);
            }, function () {
              return {
                PHONG_LIGHTING: function () {
                  return new _.de.fabmax.kool.shading.LightModel();
                },
                GOURAUD_LIGHTING: function () {
                  return new _.de.fabmax.kool.shading.LightModel();
                },
                NO_LIGHTING: function () {
                  return new _.de.fabmax.kool.shading.LightModel();
                }
              };
            }),
            ColorModel: Kotlin.createEnumClass(function () {
              return [Kotlin.Enum];
            }, function ColorModel() {
              ColorModel.baseInitializer.call(this);
            }, function () {
              return {
                VERTEX_COLOR: function () {
                  return new _.de.fabmax.kool.shading.ColorModel();
                },
                TEXTURE_COLOR: function () {
                  return new _.de.fabmax.kool.shading.ColorModel();
                },
                STATIC_COLOR: function () {
                  return new _.de.fabmax.kool.shading.ColorModel();
                }
              };
            }),
            FogModel: Kotlin.createEnumClass(function () {
              return [Kotlin.Enum];
            }, function FogModel() {
              FogModel.baseInitializer.call(this);
            }, function () {
              return {
                FOG_OFF: function () {
                  return new _.de.fabmax.kool.shading.FogModel();
                },
                FOG_ON: function () {
                  return new _.de.fabmax.kool.shading.FogModel();
                }
              };
            }),
            ShaderProps: Kotlin.createClass(null, function ShaderProps(init) {
              if (init === void 0)
                init = _.de.fabmax.kool.shading.ShaderProps.ShaderProps$f;
              this.lightModel = _.de.fabmax.kool.shading.LightModel.PHONG_LIGHTING;
              this.colorModel = _.de.fabmax.kool.shading.ColorModel.VERTEX_COLOR;
              this.fogModel = _.de.fabmax.kool.shading.FogModel.FOG_OFF;
              this.isAlpha = false;
              this.isSaturation = false;
              init.call(this);
            }, null, /** @lends _.de.fabmax.kool.shading.ShaderProps */ {
              ShaderProps$f: function () {
              }
            }),
            Uniform: Kotlin.createClass(null, function Uniform(name, value) {
              this.name = name;
              this.value_b77g8d$_0 = value;
              this.location = null;
            }, /** @lends _.de.fabmax.kool.shading.Uniform.prototype */ {
              value: {
                get: function () {
                  return this.value_b77g8d$_0;
                },
                set: function (value_0) {
                  this.value_b77g8d$_0 = value_0;
                }
              },
              isValid: {
                get: function () {
                  return this.location != null;
                }
              },
              bind_cbpn8s$_0: function (ctx) {
                if (this.isValid) {
                  this.doBind_qk1xvd$(ctx);
                }
              }
            }),
            UniformTexture2D: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Uniform];
            }, function UniformTexture2D(name) {
              UniformTexture2D.baseInitializer.call(this, name, null);
              this.type_shcorw$_0 = 'sampler2D';
              this.texUnit = 0;
              this.value_shcorw$_0 = null;
            }, /** @lends _.de.fabmax.kool.shading.UniformTexture2D.prototype */ {
              type: {
                get: function () {
                  return this.type_shcorw$_0;
                }
              },
              value: {
                get: function () {
                  return this.value_shcorw$_0;
                },
                set: function (value_0) {
                  this.value_shcorw$_0 = value_0;
                }
              },
              doBind_qk1xvd$: function (ctx) {
                var tex = this.value;
                if (tex != null) {
                  ctx.textureMgr.bindTexture2d_k73rlx$(this.value, this.texUnit, ctx);
                  _.de.fabmax.kool.platform.GL.Companion.uniform1i_wn2dyp$(this.location, this.texUnit);
                }
              }
            }),
            Uniform1i: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Uniform];
            }, function Uniform1i(name) {
              Uniform1i.baseInitializer.call(this, name, 0);
              this.type_xb7wxn$_0 = 'int';
              this.value_xb7wxn$_0 = 0;
            }, /** @lends _.de.fabmax.kool.shading.Uniform1i.prototype */ {
              type: {
                get: function () {
                  return this.type_xb7wxn$_0;
                }
              },
              value: {
                get: function () {
                  return this.value_xb7wxn$_0;
                },
                set: function (value_0) {
                  this.value_xb7wxn$_0 = value_0;
                }
              },
              doBind_qk1xvd$: function (ctx) {
                _.de.fabmax.kool.platform.GL.Companion.uniform1i_wn2dyp$(this.location, this.value);
              }
            }),
            Uniform1f: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Uniform];
            }, function Uniform1f(name) {
              Uniform1f.baseInitializer.call(this, name, 0.0);
              this.type_xb7wv2$_0 = 'float';
              this.value_xb7wv2$_0 = 0.0;
            }, /** @lends _.de.fabmax.kool.shading.Uniform1f.prototype */ {
              type: {
                get: function () {
                  return this.type_xb7wv2$_0;
                }
              },
              value: {
                get: function () {
                  return this.value_xb7wv2$_0;
                },
                set: function (value_0) {
                  this.value_xb7wv2$_0 = value_0;
                }
              },
              doBind_qk1xvd$: function (ctx) {
                _.de.fabmax.kool.platform.GL.Companion.uniform1f_rvcsvw$(this.location, this.value);
              }
            }),
            Uniform3f: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Uniform];
            }, function Uniform3f(name) {
              Uniform3f.baseInitializer.call(this, name, _.de.fabmax.kool.util.MutableVec3f_init());
              this.type_xb7ycg$_0 = 'vec3';
            }, /** @lends _.de.fabmax.kool.shading.Uniform3f.prototype */ {
              type: {
                get: function () {
                  return this.type_xb7ycg$_0;
                }
              },
              doBind_qk1xvd$: function (ctx) {
                _.de.fabmax.kool.platform.GL.Companion.uniform3f_ig0gt8$(this.location, this.value.x, this.value.y, this.value.z);
              }
            }),
            Uniform4f: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Uniform];
            }, function Uniform4f(name) {
              Uniform4f.baseInitializer.call(this, name, _.de.fabmax.kool.util.MutableVec4f_init());
              this.type_xb7z35$_0 = 'vec4';
            }, /** @lends _.de.fabmax.kool.shading.Uniform4f.prototype */ {
              type: {
                get: function () {
                  return this.type_xb7z35$_0;
                }
              },
              doBind_qk1xvd$: function (ctx) {
                _.de.fabmax.kool.platform.GL.Companion.uniform4f_k644h$(this.location, this.value.x, this.value.y, this.value.z, this.value.w);
              }
            }),
            UniformMatrix4: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.shading.Uniform];
            }, function UniformMatrix4(name) {
              UniformMatrix4.baseInitializer.call(this, name, null);
              this.type_pbbige$_0 = 'mat4';
              this.value_pbbige$_0 = null;
            }, /** @lends _.de.fabmax.kool.shading.UniformMatrix4.prototype */ {
              type: {
                get: function () {
                  return this.type_pbbige$_0;
                }
              },
              value: {
                get: function () {
                  return this.value_pbbige$_0;
                },
                set: function (value_0) {
                  this.value_pbbige$_0 = value_0;
                }
              },
              doBind_qk1xvd$: function (ctx) {
                var buf = this.value;
                if (buf != null) {
                  _.de.fabmax.kool.platform.GL.Companion.uniformMatrix4fv_rrdrnp$(this.location, false, buf);
                }
              }
            }),
            VboBinder: Kotlin.createClass(null, function VboBinder(vbo, elemSize, strideBytes, offset, type) {
              if (offset === void 0)
                offset = 0;
              if (type === void 0)
                type = _.de.fabmax.kool.platform.GL.Companion.FLOAT;
              this.vbo = vbo;
              this.elemSize = elemSize;
              this.strideBytes = strideBytes;
              this.offset = offset;
              this.type = type;
            }, /** @lends _.de.fabmax.kool.shading.VboBinder.prototype */ {
              bindAttribute_8ffywt$: function (target, ctx) {
                this.vbo.bind_qk1xvd$(ctx);
                _.de.fabmax.kool.platform.GL.Companion.vertexAttribPointer_owihk5$(target, this.elemSize, this.type, false, this.strideBytes, this.offset * 4);
              }
            })
          }),
          util: Kotlin.definePackage(null, /** @lends _.de.fabmax.kool.util */ {
            Color: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.Vec4f];
            }, function Color(r, g, b, a) {
              Color.baseInitializer.call(this, r, g, b, a);
            }, /** @lends _.de.fabmax.kool.util.Color.prototype */ {
              r: {
                get: function () {
                  return this.x;
                },
                set: function (value) {
                  this.x = value;
                }
              },
              g: {
                get: function () {
                  return this.y;
                },
                set: function (value) {
                  this.y = value;
                }
              },
              b: {
                get: function () {
                  return this.z;
                },
                set: function (value) {
                  this.z = value;
                }
              },
              a: {
                get: function () {
                  return this.w;
                },
                set: function (value) {
                  this.w = value;
                }
              }
            }, /** @lends _.de.fabmax.kool.util.Color */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.Color.Companion.BLACK = new _.de.fabmax.kool.util.Color(0.0, 0.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_GRAY = new _.de.fabmax.kool.util.Color(0.25, 0.25, 0.25, 1.0);
                _.de.fabmax.kool.util.Color.Companion.GRAY = new _.de.fabmax.kool.util.Color(0.5, 0.5, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_GRAY = new _.de.fabmax.kool.util.Color(0.75, 0.75, 0.75, 1.0);
                _.de.fabmax.kool.util.Color.Companion.WHITE = new _.de.fabmax.kool.util.Color(1.0, 1.0, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.RED = new _.de.fabmax.kool.util.Color(1.0, 0.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.GREEN = new _.de.fabmax.kool.util.Color(0.0, 1.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.BLUE = new _.de.fabmax.kool.util.Color(0.0, 0.0, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.YELLOW = new _.de.fabmax.kool.util.Color(1.0, 1.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.CYAN = new _.de.fabmax.kool.util.Color(0.0, 1.0, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.MAGENTA = new _.de.fabmax.kool.util.Color(1.0, 0.0, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.ORANGE = new _.de.fabmax.kool.util.Color(1.0, 0.5, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIME = new _.de.fabmax.kool.util.Color(0.699999988079071, 1.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_RED = new _.de.fabmax.kool.util.Color(1.0, 0.5, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_GREEN = new _.de.fabmax.kool.util.Color(0.5, 1.0, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_BLUE = new _.de.fabmax.kool.util.Color(0.5, 0.5, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_YELLOW = new _.de.fabmax.kool.util.Color(1.0, 1.0, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_CYAN = new _.de.fabmax.kool.util.Color(0.5, 1.0, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_MAGENTA = new _.de.fabmax.kool.util.Color(1.0, 0.5, 1.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.LIGHT_ORANGE = new _.de.fabmax.kool.util.Color(1.0, 0.75, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_RED = new _.de.fabmax.kool.util.Color(0.5, 0.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_GREEN = new _.de.fabmax.kool.util.Color(0.0, 0.5, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_BLUE = new _.de.fabmax.kool.util.Color(0.0, 0.0, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_YELLOW = new _.de.fabmax.kool.util.Color(0.5, 0.5, 0.0, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_CYAN = new _.de.fabmax.kool.util.Color(0.0, 0.5, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_MAGENTA = new _.de.fabmax.kool.util.Color(0.5, 0.0, 0.5, 1.0);
                _.de.fabmax.kool.util.Color.Companion.DARK_ORANGE = new _.de.fabmax.kool.util.Color(0.5, 0.25, 0.0, 1.0);
              }, /** @lends _.de.fabmax.kool.util.Color.Companion.prototype */ {
                fromHsv_7b5o5w$: function (h, s, v, a) {
                  var color = _.de.fabmax.kool.util.MutableColor_init();
                  return color.setHsv_7b5o5w$(h, s, v, a);
                }
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.Color.Companion;
              }
            }),
            MutableColor: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.Color];
            }, function MutableColor(r, g, b, a) {
              MutableColor.baseInitializer.call(this, r, g, b, a);
            }, /** @lends _.de.fabmax.kool.util.MutableColor.prototype */ {
              r: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Color, 'r');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Color, 'r', value);
                }
              },
              g: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Color, 'g');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Color, 'g', value);
                }
              },
              b: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Color, 'b');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Color, 'b', value);
                }
              },
              a: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Color, 'a');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Color, 'a', value);
                }
              },
              set_7b5o5w$: function (r, g, b, a) {
                this.r = r;
                this.g = g;
                this.b = b;
                this.a = a;
                return this;
              },
              set_d7aj7k$: function (other) {
                this.r = other.r;
                this.g = other.g;
                this.b = other.b;
                this.a = other.a;
                return this;
              },
              setHsv_7b5o5w$: function (h, s, v, a) {
                var hue = h % 360.0;
                if (hue < 0) {
                  hue += 360.0;
                }
                var hi = hue / 60.0 | 0;
                var f = hue / 60.0 - hi;
                var p = v * (1 - s);
                var q = v * (1 - s * f);
                var t = v * (1 - s * (1 - f));
                if (hi === 1)
                  this.set_7b5o5w$(q, v, p, a);
                else if (hi === 2)
                  this.set_7b5o5w$(p, v, t, a);
                else if (hi === 3)
                  this.set_7b5o5w$(p, q, v, a);
                else if (hi === 4)
                  this.set_7b5o5w$(t, p, v, a);
                else if (hi === 5)
                  this.set_7b5o5w$(v, p, q, a);
                else
                  this.set_7b5o5w$(v, t, p, a);
                return this;
              }
            }),
            MutableColor_init: function ($this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableColor.prototype);
              _.de.fabmax.kool.util.MutableColor.call($this, 0.0, 0.0, 0.0, 1.0);
              return $this;
            },
            MutableColor_init_d7aj7k$: function (color, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableColor.prototype);
              _.de.fabmax.kool.util.MutableColor.call($this, color.r, color.g, color.b, color.a);
              return $this;
            },
            Font: Kotlin.createClass(null, function Font(family, size, style) {
              if (style === void 0)
                style = _.de.fabmax.kool.util.Font.Companion.PLAIN;
              this.family = family;
              this.size = size;
              this.style = style;
              this.charMap = _.de.fabmax.kool.platform.Platform.Companion.createCharMap_34dg9o$(this, _.de.fabmax.kool.util.Font.Companion.STD_CHARS_0);
            }, null, /** @lends _.de.fabmax.kool.util.Font */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.Font.Companion.PLAIN = 0;
                _.de.fabmax.kool.util.Font.Companion.BOLD = 1;
                _.de.fabmax.kool.util.Font.Companion.ITALIC = 2;
                var str = '';
                for (var i = 32; i <= 126; i++) {
                  str += Kotlin.toChar(i);
                }
                str += '\xE4\xC4\xF6\xD6\xFC\xDC\xDF';
                _.de.fabmax.kool.util.Font.Companion.STD_CHARS_0 = str;
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.Font.Companion;
              }
            }),
            CharMetrics: Kotlin.createClass(null, function CharMetrics() {
              this.width = 0.0;
              this.height = 0.0;
              this.xOffset = 0.0;
              this.yBaseline = 0.0;
              this.advance = 0.0;
              this.uvMin = _.de.fabmax.kool.util.MutableVec2f_init();
              this.uvMax = _.de.fabmax.kool.util.MutableVec2f_init();
            }),
            CharMap: Kotlin.createClass(function () {
              return [Kotlin.kotlin.collections.Map];
            }, function CharMap(fontTexture, map) {
              this.fontTexture = fontTexture;
              this.map_0 = map;
            }, /** @lends _.de.fabmax.kool.util.CharMap.prototype */ {
              entries: {
                get: function () {
                  return this.map_0.entries;
                }
              },
              keys: {
                get: function () {
                  return this.map_0.keys;
                }
              },
              size: {
                get: function () {
                  return this.map_0.size;
                }
              },
              values: {
                get: function () {
                  return this.map_0.values;
                }
              },
              containsKey_za3rmp$: function (key) {
                return this.map_0.containsKey_za3rmp$(key);
              },
              containsValue_za3rmp$: function (value) {
                return this.map_0.containsValue_za3rmp$(value);
              },
              get_za3rmp$: function (key) {
                return this.map_0.get_za3rmp$(key);
              },
              isEmpty: function () {
                return this.map_0.isEmpty();
              }
            }),
            GlslGenerator: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.platform.ShaderGenerator];
            }, function GlslGenerator(customization) {
              if (customization === void 0)
                customization = null;
              GlslGenerator.baseInitializer.call(this);
              this.customization_uvu2u0$_0 = customization;
            }, /** @lends _.de.fabmax.kool.util.GlslGenerator.prototype */ {
              onLoad_spwa6e$: function (shader) {
                shader.enableAttribute_91bdg2$(_.de.fabmax.kool.shading.Shader.Attribute.POSITIONS, _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION);
                shader.enableAttribute_91bdg2$(_.de.fabmax.kool.shading.Shader.Attribute.NORMALS, _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_NORMAL);
                shader.enableAttribute_91bdg2$(_.de.fabmax.kool.shading.Shader.Attribute.TEXTURE_COORDS, _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_TEX_COORD);
                shader.enableAttribute_91bdg2$(_.de.fabmax.kool.shading.Shader.Attribute.COLORS, _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_COLOR);
                this.setUniformLocation_t4z91s$(shader, this.uniformMvpMatrix);
                this.setUniformLocation_t4z91s$(shader, this.uniformModelMatrix);
                this.setUniformLocation_t4z91s$(shader, this.uniformViewMatrix);
                this.setUniformLocation_t4z91s$(shader, this.uniformLightDirection);
                this.setUniformLocation_t4z91s$(shader, this.uniformLightColor);
                this.setUniformLocation_t4z91s$(shader, this.uniformShininess);
                this.setUniformLocation_t4z91s$(shader, this.uniformSpecularIntensity);
                this.setUniformLocation_t4z91s$(shader, this.uniformCameraPosition);
                this.setUniformLocation_t4z91s$(shader, this.uniformFogColor);
                this.setUniformLocation_t4z91s$(shader, this.uniformFogRange);
                this.setUniformLocation_t4z91s$(shader, this.uniformTexture);
                this.setUniformLocation_t4z91s$(shader, this.uniformStaticColor);
                this.setUniformLocation_t4z91s$(shader, this.uniformAlpha);
                this.setUniformLocation_t4z91s$(shader, this.uniformSaturation);
              },
              setUniformLocation_t4z91s$: function (shader, uniform) {
                uniform.location = shader.findUniformLocation_61zpoe$(uniform.name);
              },
              generateSource_9em3xa$: function (shaderProps) {
                return new _.de.fabmax.kool.shading.Shader.Source(this.generateVertShader_spqb9i$_0(shaderProps), this.generateFragShader_spqb9i$_0(shaderProps));
              },
              generateVertShader_spqb9i$_0: function (shaderProps) {
                var tmp$0, tmp$1, tmp$2;
                var text_0 = new Kotlin.StringBuilder('// Generated vertex shader code\n');
                (tmp$0 = this.customization_uvu2u0$_0) != null ? tmp$0.vertexShaderStart_ktj0m0$(shaderProps, text_0) : null;
                this.generateVertInputCode_y4evk$_0(shaderProps, text_0);
                (tmp$1 = this.customization_uvu2u0$_0) != null ? tmp$1.vertexShaderAfterInput_ktj0m0$(shaderProps, text_0) : null;
                this.generateVertBodyCode_y4evk$_0(shaderProps, text_0);
                (tmp$2 = this.customization_uvu2u0$_0) != null ? tmp$2.vertexShaderEnd_ktj0m0$(shaderProps, text_0) : null;
                return text_0.toString();
              },
              generateFragShader_spqb9i$_0: function (shaderProps) {
                var tmp$0, tmp$1, tmp$2;
                var text_0 = new Kotlin.StringBuilder('// Generated fragment shader code\n');
                (tmp$0 = this.customization_uvu2u0$_0) != null ? tmp$0.fragmentShaderStart_ktj0m0$(shaderProps, text_0) : null;
                this.generateFragInputCode_y4evk$_0(shaderProps, text_0);
                (tmp$1 = this.customization_uvu2u0$_0) != null ? tmp$1.fragmentShaderAfterInput_ktj0m0$(shaderProps, text_0) : null;
                this.generateFragBodyCode_y4evk$_0(shaderProps, text_0);
                (tmp$2 = this.customization_uvu2u0$_0) != null ? tmp$2.fragmentShaderEnd_ktj0m0$(shaderProps, text_0) : null;
                return text_0.toString();
              },
              generateVertInputCode_y4evk$_0: function (shaderProps, text_0) {
                text_0.append('attribute vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION).append(';\n');
                text_0.append('uniform mat4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MVP_MATRIX).append(';\n');
                text_0.append('uniform mat4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(';\n');
                text_0.append('uniform mat4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(';\n');
                if (shaderProps.lightModel !== _.de.fabmax.kool.shading.LightModel.NO_LIGHTING) {
                  text_0.append('attribute vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_NORMAL).append(';\n');
                  text_0.append('uniform vec3 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_DIRECTION).append(';\n');
                  if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.PHONG_LIGHTING) {
                    text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_EYE_DIRECTION).append(';\n');
                    text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_LIGHT_DIRECTION).append(';\n');
                    text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_NORMAL).append(';\n');
                  }
                   else {
                    text_0.append('uniform vec3 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR).append(';\n');
                    text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SHININESS).append(';\n');
                    text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SPECULAR_INTENSITY).append(';\n');
                    text_0.append('varying vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(';\n');
                    text_0.append('varying vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_SPECULAR_LIGHT_COLOR).append(';\n');
                  }
                }
                if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.TEXTURE_COLOR) {
                  text_0.append('attribute vec2 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_TEX_COORD).append(';\n');
                  text_0.append('varying vec2 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_TEX_COORD).append(';\n');
                }
                 else if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.VERTEX_COLOR) {
                  text_0.append('attribute vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_COLOR).append(';\n');
                  text_0.append('varying vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(';\n');
                }
                if (shaderProps.fogModel !== _.de.fabmax.kool.shading.FogModel.FOG_OFF) {
                  text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_POSITION_WORLDSPACE).append(';\n');
                }
              },
              generateVertBodyCode_y4evk$_0: function (shaderProps, text_0) {
                text_0.append('\nvoid main() {\n');
                text_0.append('gl_Position = ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MVP_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION).append(', 1.0);\n');
                if (shaderProps.fogModel !== _.de.fabmax.kool.shading.FogModel.FOG_OFF) {
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_POSITION_WORLDSPACE).append(' = (').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION).append(', 1.0)).xyz;\n');
                }
                if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.TEXTURE_COLOR) {
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_TEX_COORD).append(' = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_TEX_COORD).append(';\n');
                }
                 else if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.VERTEX_COLOR) {
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_COLOR).append(';\n');
                }
                if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.PHONG_LIGHTING) {
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_EYE_DIRECTION).append(' = -(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(' * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION).append(', 1.0)).xyz;\n');
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_LIGHT_DIRECTION).append(' = (').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_DIRECTION).append(', 0.0)).xyz;\n');
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_NORMAL).append(' = (').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(' * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_NORMAL).append(', 0.0)).xyz;\n');
                }
                 else if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.GOURAUD_LIGHTING) {
                  text_0.append('vec3 e = normalize(-(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(' * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION).append(', 1.0)).xyz);\n');
                  text_0.append('vec3 l = normalize((').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_DIRECTION).append(', 0.0)).xyz);\n');
                  text_0.append('vec3 n = normalize((').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(' * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(' * vec4(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_NORMAL).append(', 0.0)).xyz);\n');
                  text_0.append('float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n');
                  text_0.append('vec3 r = reflect(-l, n);\n');
                  text_0.append('float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n');
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(' = vec4(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR).append(', 1.0) * cosTheta;\n');
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_SPECULAR_LIGHT_COLOR).append(' = vec4(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR).append(' * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SPECULAR_INTENSITY).append(', 0.0) * pow(cosAlpha, ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SHININESS).append(');\n');
                }
                text_0.append('}\n');
              },
              generateFragInputCode_y4evk$_0: function (shaderProps, text_0) {
                text_0.append('uniform mat4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_MODEL_MATRIX).append(';\n');
                text_0.append('uniform mat4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_VIEW_MATRIX).append(';\n');
                if (shaderProps.isAlpha) {
                  text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_ALPHA).append(';\n');
                }
                if (shaderProps.isSaturation) {
                  text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SATURATION).append(';\n');
                }
                if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.PHONG_LIGHTING) {
                  text_0.append('uniform vec3 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR).append(';\n');
                  text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SHININESS).append(';\n');
                  text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SPECULAR_INTENSITY).append(';\n');
                  text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_EYE_DIRECTION).append(';\n');
                  text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_LIGHT_DIRECTION).append(';\n');
                  text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_NORMAL).append(';\n');
                }
                 else if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.GOURAUD_LIGHTING) {
                  text_0.append('varying vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(';\n');
                  text_0.append('varying vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_SPECULAR_LIGHT_COLOR).append(';\n');
                }
                if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.TEXTURE_COLOR) {
                  text_0.append('uniform sampler2D ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_TEXTURE_0).append(';\n');
                  text_0.append('varying vec2 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_TEX_COORD).append(';\n');
                }
                 else if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.VERTEX_COLOR) {
                  text_0.append('varying vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(';\n');
                }
                 else {
                  text_0.append('uniform vec4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_STATIC_COLOR).append(';\n');
                }
                if (shaderProps.fogModel !== _.de.fabmax.kool.shading.FogModel.FOG_OFF) {
                  text_0.append('uniform vec3 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_CAMERA_POSITION).append(';\n');
                  text_0.append('uniform vec4 ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_COLOR).append(';\n');
                  text_0.append('uniform float ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_RANGE).append(';\n');
                  text_0.append('varying vec3 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_POSITION_WORLDSPACE).append(';\n');
                }
              },
              generateFragBodyCode_y4evk$_0: function (shaderProps, text_0) {
                text_0.append('\nvoid main() {\n');
                if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.TEXTURE_COLOR) {
                  text_0.append('vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' = texture2D(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_TEXTURE_0).append(', ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_TEX_COORD).append(');\n');
                }
                 else if (shaderProps.colorModel === _.de.fabmax.kool.shading.ColorModel.STATIC_COLOR) {
                  text_0.append('vec4 ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' = ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_STATIC_COLOR).append(';\n');
                }
                if (shaderProps.colorModel !== _.de.fabmax.kool.shading.ColorModel.TEXTURE_COLOR) {
                  text_0.append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append('.rgb *= ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append('.a;');
                }
                if (shaderProps.lightModel !== _.de.fabmax.kool.shading.LightModel.NO_LIGHTING) {
                  if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.PHONG_LIGHTING) {
                    text_0.append('vec3 e = normalize(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_EYE_DIRECTION).append(');\n');
                    text_0.append('vec3 l = normalize(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_LIGHT_DIRECTION).append(');\n');
                    text_0.append('vec3 n = normalize(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_NORMAL).append(');\n');
                    text_0.append('float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n');
                    text_0.append('vec3 r = reflect(-l, n);\n');
                    text_0.append('float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n');
                    text_0.append('vec4 materialAmbientColor = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' * vec4(0.4, 0.4, 0.4, 1.0);\n');
                    text_0.append('vec4 materialDiffuseColor = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' * vec4(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR).append(', 1.0) * (cosTheta + 0.2);\n');
                    text_0.append('vec4 materialSpecularColor = vec4(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_LIGHT_COLOR).append(' * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SPECULAR_INTENSITY).append(', 0.0) * pow(cosAlpha, ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SHININESS).append(') * clamp(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append('.a * 2.0, 0.0, 1.0);\n');
                  }
                   else if (shaderProps.lightModel === _.de.fabmax.kool.shading.LightModel.GOURAUD_LIGHTING) {
                    text_0.append('vec4 materialAmbientColor = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' * vec4(0.4, 0.4, 0.4, 1.0);\n');
                    text_0.append('vec4 materialDiffuseColor = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(' * ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(';\n');
                    text_0.append('vec4 materialSpecularColor = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_SPECULAR_LIGHT_COLOR).append(';\n').append(' * clamp(').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append('.a * 2.0, 0.0, 1.0);\n');
                  }
                  text_0.append('gl_FragColor = materialAmbientColor + materialDiffuseColor + materialSpecularColor;\n');
                }
                 else {
                  text_0.append('gl_FragColor = ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR).append(';\n');
                }
                if (shaderProps.fogModel !== _.de.fabmax.kool.shading.FogModel.FOG_OFF) {
                  text_0.append('float d = 1.0 - clamp(length(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_CAMERA_POSITION).append(' - ').append(_.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_POSITION_WORLDSPACE).append(') / ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_RANGE).append(', 0.0, 1.0);\n');
                  text_0.append('gl_FragColor.rgb = mix(').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_COLOR).append('.rgb, gl_FragColor.rgb, d * d * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_FOG_COLOR).append('.a);\n');
                }
                if (shaderProps.isAlpha) {
                  text_0.append('gl_FragColor.a = gl_FragColor.a * ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_ALPHA).append(';\n');
                }
                if (shaderProps.isSaturation) {
                  text_0.append('float avgColor = (gl_FragColor.r + gl_FragColor.g + gl_FragColor.b) * 0.333;\n');
                  text_0.append('gl_FragColor.rgb = mix(vec3(avgColor), gl_FragColor.rgb, ').append(_.de.fabmax.kool.platform.ShaderGenerator.Companion.UNIFORM_SATURATION).append(');\n');
                }
                text_0.append('}\n');
              }
            }, /** @lends _.de.fabmax.kool.util.GlslGenerator */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_POSITION = 'aVertexPosition_modelspace';
                _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_NORMAL = 'aVertexNormal_modelspace';
                _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_TEX_COORD = 'aVertexTexCoord';
                _.de.fabmax.kool.util.GlslGenerator.Companion.ATTRIBUTE_NAME_COLOR = 'aVertexColor';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_TEX_COORD = 'vTexCoord';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_EYE_DIRECTION = 'vEyeDirection_cameraspace';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_LIGHT_DIRECTION = 'vLightDirection_cameraspace';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_NORMAL = 'vNormal_cameraspace';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_COLOR = 'vFragmentColor';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_DIFFUSE_LIGHT_COLOR = 'vDiffuseLightColor';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_SPECULAR_LIGHT_COLOR = 'vSpecularLightColor';
                _.de.fabmax.kool.util.GlslGenerator.Companion.VARYING_NAME_POSITION_WORLDSPACE = 'vPositionWorldspace';
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.GlslGenerator.Companion;
              },
              Customization: Kotlin.createTrait(null, /** @lends _.de.fabmax.kool.util.GlslGenerator.Customization.prototype */ {
                vertexShaderStart_ktj0m0$: function (shaderProps, text_0) {
                },
                vertexShaderAfterInput_ktj0m0$: function (shaderProps, text_0) {
                },
                vertexShaderEnd_ktj0m0$: function (shaderProps, text_0) {
                },
                fragmentShaderStart_ktj0m0$: function (shaderProps, text_0) {
                },
                fragmentShaderAfterInput_ktj0m0$: function (shaderProps, text_0) {
                },
                fragmentShaderEnd_ktj0m0$: function (shaderProps, text_0) {
                }
              })
            }),
            IndexedVertexList: Kotlin.createClass(null, function IndexedVertexList(hasNormals, hasColors, hasTexCoords) {
              this.hasNormals = hasNormals;
              this.hasColors = hasColors;
              this.hasTexCoords = hasTexCoords;
              this.positionOffset = 0;
              this.elements_8s1iga$_0 = 0;
              this.size_8s1iga$_0 = 0;
              this.indices_8s1iga$_0 = _.de.fabmax.kool.platform.Platform.Companion.createUint32Buffer_za3lpa$(_.de.fabmax.kool.util.IndexedVertexList.Companion.INITIAL_SIZE_0);
              var cnt = 3;
              if (this.hasNormals) {
                this.normalOffset = cnt;
                cnt += 3;
              }
               else {
                this.normalOffset = -1;
              }
              if (this.hasColors) {
                this.colorOffset = cnt;
                cnt += 4;
              }
               else {
                this.colorOffset = -1;
              }
              if (this.hasTexCoords) {
                this.texCoordOffset = cnt;
                cnt += 2;
              }
               else {
                this.texCoordOffset = -1;
              }
              this.vertexSize = cnt;
              this.strideBytes = this.vertexSize * 4;
              this.data = _.de.fabmax.kool.platform.Platform.Companion.createFloat32Buffer_za3lpa$(cnt * _.de.fabmax.kool.util.IndexedVertexList.Companion.INITIAL_SIZE_0);
              this.addItem_0 = new _.de.fabmax.kool.util.IndexedVertexList.Item(this, 0);
            }, /** @lends _.de.fabmax.kool.util.IndexedVertexList.prototype */ {
              elements: {
                get: function () {
                  return this.elements_8s1iga$_0;
                },
                set: function (elements_0) {
                  this.elements_8s1iga$_0 = elements_0;
                }
              },
              size: {
                get: function () {
                  return this.size_8s1iga$_0;
                },
                set: function (size_0) {
                  this.size_8s1iga$_0 = size_0;
                }
              },
              data: {
                get: function () {
                  return this.data_8s1iga$_0;
                },
                set: function (data_0) {
                  this.data_8s1iga$_0 = data_0;
                }
              },
              indices: {
                get: function () {
                  return this.indices_8s1iga$_0;
                },
                set: function (indices_0) {
                  this.indices_8s1iga$_0 = indices_0;
                }
              },
              increaseDataSize_0: function () {
                var tmp$0;
                var newData = _.de.fabmax.kool.platform.Platform.Companion.createFloat32Buffer_za3lpa$(Math.round(this.data.capacity * _.de.fabmax.kool.util.IndexedVertexList.Companion.GROW_FACTOR_0));
                tmp$0 = this.data.capacity - 1;
                for (var i = 0; i <= tmp$0; i++) {
                  newData.set_vux3hl$(i, this.data.get_za3lpa$(i));
                }
                newData.position = this.data.position;
                this.data = newData;
              },
              increaseIndicesSize_0: function () {
                var tmp$0;
                var newIdxs = _.de.fabmax.kool.platform.Platform.Companion.createUint32Buffer_za3lpa$(Math.round(this.indices.capacity * _.de.fabmax.kool.util.IndexedVertexList.Companion.GROW_FACTOR_0));
                tmp$0 = this.indices.capacity - 1;
                for (var i = 0; i <= tmp$0; i++) {
                  newIdxs.set_vux3hl$(i, this.indices.get_za3lpa$(i));
                }
                newIdxs.position = this.indices.position;
                this.indices = newIdxs;
              },
              addVertex_570ote$: function (init) {
                var tmp$0;
                if (this.data.remaining < this.vertexSize) {
                  this.increaseDataSize_0();
                }
                tmp$0 = this.vertexSize;
                for (var i = 1; i <= tmp$0; i++) {
                  this.data.plusAssign_za3rmp$(0.0);
                }
                this.addItem_0.index = this.elements++;
                this.size += this.vertexSize;
                init.call(this.addItem_0);
                return this.elements - 1;
              },
              addVertex_f9a7mx$: function (position, normal, color, texCoord) {
                if (normal === void 0)
                  normal = null;
                if (color === void 0)
                  color = null;
                if (texCoord === void 0)
                  texCoord = null;
                return this.addVertex_570ote$(_.de.fabmax.kool.util.IndexedVertexList.addVertex_f9a7mx$f(position, normal, color, texCoord));
              },
              addIndex_za3lpa$: function (idx) {
                if (this.indices.remaining === 0) {
                  this.increaseIndicesSize_0();
                }
                this.indices.plusAssign_za3rmp$(idx);
              },
              addIndices_q5rwfd$: function (indices) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                tmp$0 = Kotlin.kotlin.collections.get_indices_tmsbgp$(indices);
                tmp$1 = tmp$0.first;
                tmp$2 = tmp$0.last;
                tmp$3 = tmp$0.step;
                for (var idx = tmp$1; idx <= tmp$2; idx += tmp$3) {
                  this.addIndex_za3lpa$(indices[idx]);
                }
              },
              clear: function () {
                this.size = 0;
                this.elements = 0;
                this.data.position = 0;
                this.data.limit = this.data.capacity;
                this.indices.position = 0;
                this.indices.limit = this.indices.capacity;
              },
              get_za3lpa$: function (i) {
                if (i < 0 || i >= this.data.capacity) {
                  throw new _.de.fabmax.kool.KoolException('Vertex index out of bounds: ' + i);
                }
                return new _.de.fabmax.kool.util.IndexedVertexList.Item(this, i);
              }
            }, /** @lends _.de.fabmax.kool.util.IndexedVertexList */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.IndexedVertexList.Companion.INITIAL_SIZE_0 = 1000;
                _.de.fabmax.kool.util.IndexedVertexList.Companion.GROW_FACTOR_0 = 2.0;
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.IndexedVertexList.Companion;
              },
              addVertex_f9a7mx$f: function (closure$position, closure$normal, closure$color, closure$texCoord) {
                return function () {
                  this.position.set_cx11x8$(closure$position);
                  if (closure$normal != null) {
                    this.normal.set_cx11x8$(closure$normal);
                  }
                  if (closure$color != null) {
                    this.color.set_d7aj7k$(closure$color);
                  }
                  if (closure$texCoord != null) {
                    this.texCoord.set_cx11y3$(closure$texCoord);
                  }
                };
              },
              Item: Kotlin.createClass(null, function Item($outer, index) {
                this.$outer = $outer;
                this.offset_0 = index * this.$outer.vertexSize;
                this.position = new _.de.fabmax.kool.util.IndexedVertexList.Item.Vec3fView(this, this.$outer.positionOffset);
                this.normal = new _.de.fabmax.kool.util.IndexedVertexList.Item.Vec3fView(this, this.$outer.normalOffset);
                this.color = new _.de.fabmax.kool.util.IndexedVertexList.Item.ColorView(this, this.$outer.colorOffset);
                this.texCoord = new _.de.fabmax.kool.util.IndexedVertexList.Item.Vec2fView(this, this.$outer.texCoordOffset);
                this.index_y71guh$_0 = index;
              }, /** @lends _.de.fabmax.kool.util.IndexedVertexList.Item.prototype */ {
                index: {
                  get: function () {
                    return this.index_y71guh$_0;
                  },
                  set: function (value) {
                    this.index_y71guh$_0 = value;
                    this.offset_0 = value * this.$outer.vertexSize;
                  }
                }
              }, /** @lends _.de.fabmax.kool.util.IndexedVertexList.Item */ {
                Vec2fView: Kotlin.createClass(function () {
                  return [_.de.fabmax.kool.util.MutableVec2f];
                }, function Vec2fView($outer, componentOffset) {
                  this.$outer = $outer;
                  _.de.fabmax.kool.util.MutableVec2f_init(this);
                  this.componentOffset_0 = componentOffset;
                }, /** @lends _.de.fabmax.kool.util.IndexedVertexList.Item.Vec2fView.prototype */ {
                  x: {
                    get: function () {
                      if (this.componentOffset_0 < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset_0);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset_0 >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset_0, value);
                      }
                    }
                  },
                  y: {
                    get: function () {
                      if (this.componentOffset_0 < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset_0 + 1);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset_0 >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset_0 + 1, value);
                      }
                    }
                  }
                }),
                Vec3fView: Kotlin.createClass(function () {
                  return [_.de.fabmax.kool.util.MutableVec3f];
                }, function Vec3fView($outer, componentOffset) {
                  this.$outer = $outer;
                  _.de.fabmax.kool.util.MutableVec3f_init(this);
                  this.componentOffset = componentOffset;
                }, /** @lends _.de.fabmax.kool.util.IndexedVertexList.Item.Vec3fView.prototype */ {
                  x: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset, value);
                      }
                    }
                  },
                  y: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset + 1);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset + 1, value);
                      }
                    }
                  },
                  z: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset + 2);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset + 2, value);
                      }
                    }
                  }
                }),
                ColorView: Kotlin.createClass(function () {
                  return [_.de.fabmax.kool.util.MutableColor];
                }, function ColorView($outer, componentOffset) {
                  this.$outer = $outer;
                  _.de.fabmax.kool.util.MutableColor_init(this);
                  this.componentOffset = componentOffset;
                }, /** @lends _.de.fabmax.kool.util.IndexedVertexList.Item.ColorView.prototype */ {
                  x: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset, value);
                      }
                    }
                  },
                  y: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset + 1);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset + 1, value);
                      }
                    }
                  },
                  z: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset + 2);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset + 2, value);
                      }
                    }
                  },
                  w: {
                    get: function () {
                      if (this.componentOffset < 0) {
                        return 0.0;
                      }
                       else {
                        return this.$outer.$outer.data.get_za3lpa$(this.$outer.offset_0 + this.componentOffset + 3);
                      }
                    },
                    set: function (value) {
                      if (this.componentOffset >= 0) {
                        this.$outer.$outer.data.set_vux3hl$(this.$outer.offset_0 + this.componentOffset + 3, value);
                      }
                    }
                  }
                })
              })
            }),
            Mat4f: Kotlin.createClass(null, function Mat4f() {
              this.matrix_lfgrvv$_0 = Kotlin.numberArrayOfSize(16);
              this.offset_lfgrvv$_0 = 0;
              this.setIdentity();
            }, /** @lends _.de.fabmax.kool.util.Mat4f.prototype */ {
              matrix: {
                get: function () {
                  return this.matrix_lfgrvv$_0;
                },
                set: function (matrix_0) {
                  this.matrix_lfgrvv$_0 = matrix_0;
                }
              },
              offset: {
                get: function () {
                  return this.offset_lfgrvv$_0;
                },
                set: function (offset_0) {
                  this.offset_lfgrvv$_0 = offset_0;
                }
              },
              translate_y2kzbl$: function (tx, ty, tz) {
                _.de.fabmax.kool.util.MatrixMath.translateM_h6lr6x$(this.matrix, this.offset, tx, ty, tz);
                return this;
              },
              translate_qmeyd0$: function (result, tx, ty, tz) {
                _.de.fabmax.kool.util.MatrixMath.translateM_uypkq7$(result.matrix, result.offset, this.matrix, this.offset, tx, ty, tz);
                return result;
              },
              rotate_ag3lbb$: function (angleDeg, axis) {
                return this.rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
              },
              rotate_7b5o5w$: function (angleDeg, axX, axY, axZ) {
                _.de.fabmax.kool.util.MatrixMath.rotateM_b0ilq4$(this.matrix, this.offset, angleDeg, axX, axY, axZ);
                return this;
              },
              rotate_hw9m9w$: function (result, angleDeg, axis) {
                return this.rotate_r7nahd$(result, angleDeg, axis.x, axis.y, axis.z);
              },
              rotate_r7nahd$: function (result, angleDeg, axX, axY, axZ) {
                _.de.fabmax.kool.util.MatrixMath.rotateM_96b1ik$(result.matrix, result.offset, this.matrix, this.offset, angleDeg, axX, axY, axZ);
                return result;
              },
              rotateEuler_y2kzbl$: function (xDeg, yDeg, zDeg) {
                _.de.fabmax.kool.util.MatrixMath.rotateEulerM_h6lr6x$(this.matrix, this.offset, xDeg, yDeg, zDeg);
                return this;
              },
              rotateEuler_qmeyd0$: function (result, xDeg, yDeg, zDeg) {
                _.de.fabmax.kool.util.MatrixMath.rotateEulerM_uypkq7$(result.matrix, result.offset, this.matrix, this.offset, xDeg, yDeg, zDeg);
                return result;
              },
              scale_y2kzbl$: function (sx, sy, sz) {
                _.de.fabmax.kool.util.MatrixMath.scaleM_h6lr6x$(this.matrix, this.offset, sx, sy, sz);
                return this;
              },
              scale_qmeyd0$: function (result, sx, sy, sz) {
                _.de.fabmax.kool.util.MatrixMath.scaleM_uypkq7$(result.matrix, result.offset, this.matrix, this.offset, sx, sy, sz);
                return result;
              },
              transpose: function () {
                _.de.fabmax.kool.util.MatrixMath.transposeM_9752rg$(this.matrix, this.offset);
                return this;
              },
              transpose_d21ekx$: function (result) {
                _.de.fabmax.kool.util.MatrixMath.transposeM_41jbro$(result.matrix, result.offset, this.matrix, this.offset);
                return result;
              },
              invert: function () {
                return _.de.fabmax.kool.util.MatrixMath.invertM_9752rg$(this.matrix, this.offset);
              },
              invert_d21ekx$: function (result) {
                return _.de.fabmax.kool.util.MatrixMath.invertM_41jbro$(result.matrix, result.offset, this.matrix, this.offset);
              },
              transform_64rgyf$: function (vec, w) {
                if (w === void 0)
                  w = 1.0;
                var x = vec.x * this.get_vux9f0$(0, 0) + vec.y * this.get_vux9f0$(1, 0) + vec.z * this.get_vux9f0$(2, 0) + w * this.get_vux9f0$(3, 0);
                var y = vec.x * this.get_vux9f0$(0, 1) + vec.y * this.get_vux9f0$(1, 1) + vec.z * this.get_vux9f0$(2, 1) + w * this.get_vux9f0$(3, 1);
                var z = vec.x * this.get_vux9f0$(0, 2) + vec.y * this.get_vux9f0$(1, 2) + vec.z * this.get_vux9f0$(2, 2) + w * this.get_vux9f0$(3, 2);
                return vec.set_y2kzbl$(x, y, z);
              },
              transform_wntem7$: function (result, vec, w) {
                if (w === void 0)
                  w = 1.0;
                result.x = vec.x * this.get_vux9f0$(0, 0) + vec.y * this.get_vux9f0$(1, 0) + vec.z * this.get_vux9f0$(2, 0) + w * this.get_vux9f0$(3, 0);
                result.y = vec.x * this.get_vux9f0$(0, 1) + vec.y * this.get_vux9f0$(1, 1) + vec.z * this.get_vux9f0$(2, 1) + w * this.get_vux9f0$(3, 1);
                result.z = vec.x * this.get_vux9f0$(0, 2) + vec.y * this.get_vux9f0$(1, 2) + vec.z * this.get_vux9f0$(2, 2) + w * this.get_vux9f0$(3, 2);
                return result;
              },
              mul_d21ekx$: function (other) {
                _.de.fabmax.kool.util.MatrixMath.multiplyMM_41jbro$(this.matrix, this.offset, other.matrix, other.offset);
                return this;
              },
              mul_y010fm$: function (result, other) {
                _.de.fabmax.kool.util.MatrixMath.multiplyMM_3w9et8$(result.matrix, result.offset, this.matrix, this.offset, other.matrix, other.offset);
                return result;
              },
              set_d21ekx$: function (other) {
                for (var i = 0; i <= 15; i++) {
                  this.matrix[this.offset + i] = other.matrix[other.offset + i];
                }
                return this;
              },
              setIdentity: function () {
                _.de.fabmax.kool.util.MatrixMath.setIdentityM_9752rg$(this.matrix, this.offset);
                return this;
              },
              setLookAt_hd42to$: function (position, lookAt, up) {
                _.de.fabmax.kool.util.MatrixMath.setLookAtM_u6vibb$(this.matrix, this.offset, position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
                return this;
              },
              setPerspective_7b5o5w$: function (fovy, aspect, near, far) {
                _.de.fabmax.kool.util.MatrixMath.setPerspectiveM_b0ilq4$(this.matrix, this.offset, fovy, aspect, near, far);
                return this;
              },
              get_za3lpa$: function (i) {
                return this.matrix[this.offset + i];
              },
              get_vux9f0$: function (col, row) {
                return this.matrix[this.offset + col * 4 + row];
              },
              set_24o109$: function (i, value) {
                this.matrix[this.offset + i] = value;
              },
              set_n0b4r3$: function (col, row, value) {
                this.matrix[this.offset + col * 4 + row] = value;
              },
              toBuffer_m7ytxz$: function (buffer) {
                buffer.put_kgymra$(this.matrix, this.offset, 16);
                buffer.flip();
                return buffer;
              }
            }),
            Mat4fStack: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.Mat4f];
            }, function Mat4fStack(stackSize) {
              if (stackSize === void 0)
                stackSize = _.de.fabmax.kool.util.Mat4fStack.Companion.DEFAULT_STACK_SIZE;
              Mat4fStack.baseInitializer.call(this);
              this.stackSize = stackSize;
              this.stackIndex_nmkxr$_0 = 0;
              this.matrix = Kotlin.numberArrayOfSize(16 * this.stackSize);
              this.setIdentity();
            }, /** @lends _.de.fabmax.kool.util.Mat4fStack.prototype */ {
              stackIndex: {
                get: function () {
                  return this.stackIndex_nmkxr$_0;
                },
                set: function (value) {
                  this.stackIndex_nmkxr$_0 = value;
                  this.offset = value * 16;
                }
              },
              push: function () {
                if (this.stackIndex >= this.stackSize) {
                  throw new _.de.fabmax.kool.KoolException('Matrix stack overflow');
                }
                for (var i = 0; i <= 15; i++) {
                  this.matrix[this.offset + 16 + i] = this.matrix[this.offset + i];
                }
                this.stackIndex++;
              },
              pop: function () {
                if (this.stackIndex <= 0) {
                  throw new _.de.fabmax.kool.KoolException('Matrix stack underflow');
                }
                this.stackIndex--;
              },
              reset: function () {
                this.stackIndex = 0;
                this.setIdentity();
                return this;
              }
            }, /** @lends _.de.fabmax.kool.util.Mat4fStack */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.Mat4fStack.Companion.DEFAULT_STACK_SIZE = 32;
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.Mat4fStack.Companion;
              }
            }),
            toDeg_mx4ult$: function (rad) {
              return rad / Math.PI * 180.0;
            },
            toRad_mx4ult$: function (rad) {
              return rad / 180.0 * Math.PI;
            },
            clamp_qt1dr2$: function (value, min, max) {
              if (min === void 0)
                min = 0;
              if (max === void 0)
                max = 1;
              if (value < min) {
                return min;
              }
               else if (value > max) {
                return max;
              }
               else {
                return value;
              }
            },
            clamp_y2kzbl$: function (value, min, max) {
              if (min === void 0)
                min = 0.0;
              if (max === void 0)
                max = 1.0;
              if (value < min) {
                return min;
              }
               else if (value > max) {
                return max;
              }
               else {
                return value;
              }
            },
            clamp_yvo9jy$: function (value, min, max) {
              if (min === void 0)
                min = 0.0;
              if (max === void 0)
                max = 1.0;
              if (value < min) {
                return min;
              }
               else if (value > max) {
                return max;
              }
               else {
                return value;
              }
            },
            isZero_mx4ult$: function (value) {
              return Math.abs(value) < 1.0E-5;
            },
            isZero_14dthe$: function (value) {
              return Math.abs(value) < 1.0E-10;
            },
            MatrixMath: Kotlin.createObject(null, function MatrixMath() {
              this.temp_0 = Kotlin.numberArrayOfSize(32);
            }, /** @lends _.de.fabmax.kool.util.MatrixMath.prototype */ {
              multiplyMM_41jbro$: function (m, mOffset, rhs, rhsOffset) {
                this.temp_0;
                this.multiplyMM_3w9et8$(this.temp_0, 0, m, mOffset, rhs, rhsOffset);
                for (var i = 0; i <= 15; i++) {
                  m[mOffset + i] = this.temp_0[i];
                }
              },
              multiplyMM_3w9et8$: function (result, resultOffset, lhs, lhsOffset, rhs, rhsOffset) {
                for (var i = 0; i <= 3; i++) {
                  for (var j = 0; j <= 3; j++) {
                    var x = 0.0;
                    for (var k = 0; k <= 3; k++) {
                      x += lhs[lhsOffset + j + k * 4] * rhs[rhsOffset + i * 4 + k];
                    }
                    result[resultOffset + i * 4 + j] = x;
                  }
                }
              },
              multiplyMV_3w9et8$: function (resultVec, resultVecOffset, lhsMat, lhsMatOffset, rhsVec, rhsVecOffset) {
                resultVec[resultVecOffset + 0] = lhsMat[lhsMatOffset + 0] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 4] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 8] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 12] * rhsVec[rhsVecOffset + 3];
                resultVec[resultVecOffset + 1] = lhsMat[lhsMatOffset + 1] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 5] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 9] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 13] * rhsVec[rhsVecOffset + 3];
                resultVec[resultVecOffset + 2] = lhsMat[lhsMatOffset + 2] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 6] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 10] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 14] * rhsVec[rhsVecOffset + 3];
                resultVec[resultVecOffset + 3] = lhsMat[lhsMatOffset + 3] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 7] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 11] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 15] * rhsVec[rhsVecOffset + 3];
              },
              multiplyMV_41jbro$: function (lhsMat, lhsMatOffset, rhsVec, rhsVecOffset) {
                var x = lhsMat[lhsMatOffset + 0] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 4] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 8] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 12] * rhsVec[rhsVecOffset + 3];
                var y = lhsMat[lhsMatOffset + 1] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 5] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 9] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 13] * rhsVec[rhsVecOffset + 3];
                var z = lhsMat[lhsMatOffset + 2] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 6] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 10] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 14] * rhsVec[rhsVecOffset + 3];
                var w = lhsMat[lhsMatOffset + 3] * rhsVec[rhsVecOffset + 0] + lhsMat[lhsMatOffset + 7] * rhsVec[rhsVecOffset + 1] + lhsMat[lhsMatOffset + 11] * rhsVec[rhsVecOffset + 2] + lhsMat[lhsMatOffset + 15] * rhsVec[rhsVecOffset + 3];
                rhsVec[rhsVecOffset + 0] = x;
                rhsVec[rhsVecOffset + 1] = y;
                rhsVec[rhsVecOffset + 2] = z;
                rhsVec[rhsVecOffset + 3] = w;
              },
              transposeM_41jbro$: function (mTrans, mTransOffset, m, mOffset) {
                for (var i = 0; i <= 3; i++) {
                  var mBase = i * 4 + mOffset;
                  mTrans[i + mTransOffset] = m[mBase];
                  mTrans[i + 4 + mTransOffset] = m[mBase + 1];
                  mTrans[i + 8 + mTransOffset] = m[mBase + 2];
                  mTrans[i + 12 + mTransOffset] = m[mBase + 3];
                }
              },
              transposeM_9752rg$: function (m, mOffset) {
                this.temp_0;
                this.transposeM_41jbro$(this.temp_0, 0, m, mOffset);
                for (var i = 0; i <= 15; i++) {
                  m[mOffset + i] = this.temp_0[i];
                }
              },
              invertM_9752rg$: function (m, mOffset) {
                var success = {v: false};
                this.temp_0;
                success.v = this.invertM_41jbro$(this.temp_0, 0, m, mOffset);
                if (success.v) {
                  for (var i = 0; i <= 15; i++) {
                    m[mOffset + i] = this.temp_0[i];
                  }
                }
                return success.v;
              },
              invertM_41jbro$: function (mInv, mInvOffset, m, mOffset) {
                var src0 = m[mOffset + 0];
                var src4 = m[mOffset + 1];
                var src8 = m[mOffset + 2];
                var src12 = m[mOffset + 3];
                var src1 = m[mOffset + 4];
                var src5 = m[mOffset + 5];
                var src9 = m[mOffset + 6];
                var src13 = m[mOffset + 7];
                var src2 = m[mOffset + 8];
                var src6 = m[mOffset + 9];
                var src10 = m[mOffset + 10];
                var src14 = m[mOffset + 11];
                var src3 = m[mOffset + 12];
                var src7 = m[mOffset + 13];
                var src11 = m[mOffset + 14];
                var src15 = m[mOffset + 15];
                var atmp0 = src10 * src15;
                var atmp1 = src11 * src14;
                var atmp2 = src9 * src15;
                var atmp3 = src11 * src13;
                var atmp4 = src9 * src14;
                var atmp5 = src10 * src13;
                var atmp6 = src8 * src15;
                var atmp7 = src11 * src12;
                var atmp8 = src8 * src14;
                var atmp9 = src10 * src12;
                var atmp10 = src8 * src13;
                var atmp11 = src9 * src12;
                var dst0 = atmp0 * src5 + atmp3 * src6 + atmp4 * src7 - (atmp1 * src5 + atmp2 * src6 + atmp5 * src7);
                var dst1 = atmp1 * src4 + atmp6 * src6 + atmp9 * src7 - (atmp0 * src4 + atmp7 * src6 + atmp8 * src7);
                var dst2 = atmp2 * src4 + atmp7 * src5 + atmp10 * src7 - (atmp3 * src4 + atmp6 * src5 + atmp11 * src7);
                var dst3 = atmp5 * src4 + atmp8 * src5 + atmp11 * src6 - (atmp4 * src4 + atmp9 * src5 + atmp10 * src6);
                var dst4 = atmp1 * src1 + atmp2 * src2 + atmp5 * src3 - (atmp0 * src1 + atmp3 * src2 + atmp4 * src3);
                var dst5 = atmp0 * src0 + atmp7 * src2 + atmp8 * src3 - (atmp1 * src0 + atmp6 * src2 + atmp9 * src3);
                var dst6 = atmp3 * src0 + atmp6 * src1 + atmp11 * src3 - (atmp2 * src0 + atmp7 * src1 + atmp10 * src3);
                var dst7 = atmp4 * src0 + atmp9 * src1 + atmp10 * src2 - (atmp5 * src0 + atmp8 * src1 + atmp11 * src2);
                var btmp0 = src2 * src7;
                var btmp1 = src3 * src6;
                var btmp2 = src1 * src7;
                var btmp3 = src3 * src5;
                var btmp4 = src1 * src6;
                var btmp5 = src2 * src5;
                var btmp6 = src0 * src7;
                var btmp7 = src3 * src4;
                var btmp8 = src0 * src6;
                var btmp9 = src2 * src4;
                var btmp10 = src0 * src5;
                var btmp11 = src1 * src4;
                var dst8 = btmp0 * src13 + btmp3 * src14 + btmp4 * src15 - (btmp1 * src13 + btmp2 * src14 + btmp5 * src15);
                var dst9 = btmp1 * src12 + btmp6 * src14 + btmp9 * src15 - (btmp0 * src12 + btmp7 * src14 + btmp8 * src15);
                var dst10 = btmp2 * src12 + btmp7 * src13 + btmp10 * src15 - (btmp3 * src12 + btmp6 * src13 + btmp11 * src15);
                var dst11 = btmp5 * src12 + btmp8 * src13 + btmp11 * src14 - (btmp4 * src12 + btmp9 * src13 + btmp10 * src14);
                var dst12 = btmp2 * src10 + btmp5 * src11 + btmp1 * src9 - (btmp4 * src11 + btmp0 * src9 + btmp3 * src10);
                var dst13 = btmp8 * src11 + btmp0 * src8 + btmp7 * src10 - (btmp6 * src10 + btmp9 * src11 + btmp1 * src8);
                var dst14 = btmp6 * src9 + btmp11 * src11 + btmp3 * src8 - (btmp10 * src11 + btmp2 * src8 + btmp7 * src9);
                var dst15 = btmp10 * src10 + btmp4 * src8 + btmp9 * src9 - (btmp8 * src9 + btmp11 * src10 + btmp5 * src8);
                var det = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;
                if (det === 0.0) {
                  return false;
                }
                var invdet = 1.0 / det;
                mInv[mInvOffset] = dst0 * invdet;
                mInv[1 + mInvOffset] = dst1 * invdet;
                mInv[2 + mInvOffset] = dst2 * invdet;
                mInv[3 + mInvOffset] = dst3 * invdet;
                mInv[4 + mInvOffset] = dst4 * invdet;
                mInv[5 + mInvOffset] = dst5 * invdet;
                mInv[6 + mInvOffset] = dst6 * invdet;
                mInv[7 + mInvOffset] = dst7 * invdet;
                mInv[8 + mInvOffset] = dst8 * invdet;
                mInv[9 + mInvOffset] = dst9 * invdet;
                mInv[10 + mInvOffset] = dst10 * invdet;
                mInv[11 + mInvOffset] = dst11 * invdet;
                mInv[12 + mInvOffset] = dst12 * invdet;
                mInv[13 + mInvOffset] = dst13 * invdet;
                mInv[14 + mInvOffset] = dst14 * invdet;
                mInv[15 + mInvOffset] = dst15 * invdet;
                return true;
              },
              orthoM_iloig4$: function (m, mOffset, left, right, bottom, top, near, far) {
                if (left === right) {
                  throw new Kotlin.IllegalArgumentException('left == right');
                }
                if (bottom === top) {
                  throw new Kotlin.IllegalArgumentException('bottom == top');
                }
                if (near === far) {
                  throw new Kotlin.IllegalArgumentException('near == far');
                }
                var r_width = 1.0 / (right - left);
                var r_height = 1.0 / (top - bottom);
                var r_depth = 1.0 / (far - near);
                var x = 2.0 * r_width;
                var y = 2.0 * r_height;
                var z = -2.0 * r_depth;
                var tx = -(right + left) * r_width;
                var ty = -(top + bottom) * r_height;
                var tz = -(far + near) * r_depth;
                m[mOffset + 0] = x;
                m[mOffset + 5] = y;
                m[mOffset + 10] = z;
                m[mOffset + 12] = tx;
                m[mOffset + 13] = ty;
                m[mOffset + 14] = tz;
                m[mOffset + 15] = 1.0;
                m[mOffset + 1] = 0.0;
                m[mOffset + 2] = 0.0;
                m[mOffset + 3] = 0.0;
                m[mOffset + 4] = 0.0;
                m[mOffset + 6] = 0.0;
                m[mOffset + 7] = 0.0;
                m[mOffset + 8] = 0.0;
                m[mOffset + 9] = 0.0;
                m[mOffset + 11] = 0.0;
              },
              frustumM_iloig4$: function (m, offset, left, right, bottom, top, near, far) {
                if (left === right) {
                  throw new Kotlin.IllegalArgumentException('left == right');
                }
                if (top === bottom) {
                  throw new Kotlin.IllegalArgumentException('top == bottom');
                }
                if (near === far) {
                  throw new Kotlin.IllegalArgumentException('near == far');
                }
                if (near <= 0.0) {
                  throw new Kotlin.IllegalArgumentException('near <= 0.0f');
                }
                if (far <= 0.0) {
                  throw new Kotlin.IllegalArgumentException('far <= 0.0f');
                }
                var r_width = 1.0 / (right - left);
                var r_height = 1.0 / (top - bottom);
                var r_depth = 1.0 / (near - far);
                var x = 2.0 * (near * r_width);
                var y = 2.0 * (near * r_height);
                var A = (right + left) * r_width;
                var B = (top + bottom) * r_height;
                var C = (far + near) * r_depth;
                var D = 2.0 * (far * near * r_depth);
                m[offset + 0] = x;
                m[offset + 5] = y;
                m[offset + 8] = A;
                m[offset + 9] = B;
                m[offset + 10] = C;
                m[offset + 14] = D;
                m[offset + 11] = -1.0;
                m[offset + 1] = 0.0;
                m[offset + 2] = 0.0;
                m[offset + 3] = 0.0;
                m[offset + 4] = 0.0;
                m[offset + 6] = 0.0;
                m[offset + 7] = 0.0;
                m[offset + 12] = 0.0;
                m[offset + 13] = 0.0;
                m[offset + 15] = 0.0;
              },
              setPerspectiveM_b0ilq4$: function (m, offset, fovy, aspect, zNear, zFar) {
                var f = 1.0 / Math.tan(fovy * (Math.PI / 360.0));
                var rangeReciprocal = 1.0 / (zNear - zFar);
                m[offset + 0] = f / aspect;
                m[offset + 1] = 0.0;
                m[offset + 2] = 0.0;
                m[offset + 3] = 0.0;
                m[offset + 4] = 0.0;
                m[offset + 5] = f;
                m[offset + 6] = 0.0;
                m[offset + 7] = 0.0;
                m[offset + 8] = 0.0;
                m[offset + 9] = 0.0;
                m[offset + 10] = (zFar + zNear) * rangeReciprocal;
                m[offset + 11] = -1.0;
                m[offset + 12] = 0.0;
                m[offset + 13] = 0.0;
                m[offset + 14] = 2.0 * zFar * zNear * rangeReciprocal;
                m[offset + 15] = 0.0;
              },
              length_0: function (x, y, z) {
                return Math.sqrt(x * x + y * y + z * z);
              },
              setIdentityM_9752rg$: function (sm, smOffset) {
                for (var i = 0; i <= 15; i++) {
                  sm[smOffset + i] = 0.0;
                }
                var i_0 = 0;
                while (i_0 < 16) {
                  sm[smOffset + i_0] = 1.0;
                  i_0 += 5;
                }
              },
              scaleM_uypkq7$: function (sm, smOffset, m, mOffset, x, y, z) {
                for (var i = 0; i <= 3; i++) {
                  var smi = smOffset + i;
                  var mi = mOffset + i;
                  sm[smi] = m[mi] * x;
                  sm[4 + smi] = m[4 + mi] * y;
                  sm[8 + smi] = m[8 + mi] * z;
                  sm[12 + smi] = m[12 + mi];
                }
              },
              scaleM_h6lr6x$: function (m, mOffset, x, y, z) {
                for (var i = 0; i <= 3; i++) {
                  var mi = mOffset + i;
                  m[mi] = m[mi] * x;
                  m[4 + mi] = m[4 + mi] * y;
                  m[8 + mi] = m[8 + mi] * z;
                }
              },
              translateM_uypkq7$: function (tm, tmOffset, m, mOffset, x, y, z) {
                for (var i = 0; i <= 11; i++) {
                  tm[tmOffset + i] = m[mOffset + i];
                }
                for (var i_0 = 0; i_0 <= 3; i_0++) {
                  var tmi = tmOffset + i_0;
                  var mi = mOffset + i_0;
                  tm[12 + tmi] = m[mi] * x + m[4 + mi] * y + m[8 + mi] * z + m[12 + mi];
                }
              },
              translateM_h6lr6x$: function (m, mOffset, x, y, z) {
                for (var i = 0; i <= 3; i++) {
                  var mi = mOffset + i;
                  m[12 + mi] = m[12 + mi] + (m[mi] * x + m[4 + mi] * y + m[8 + mi] * z);
                }
              },
              rotateM_96b1ik$: function (rm, rmOffset, m, mOffset, a, x, y, z) {
                this.temp_0;
                this.setRotateM_b0ilq4$(this.temp_0, 0, a, x, y, z);
                this.multiplyMM_3w9et8$(rm, rmOffset, m, mOffset, this.temp_0, 0);
              },
              rotateM_b0ilq4$: function (m, mOffset, a, x, y, z) {
                this.temp_0;
                this.setRotateM_b0ilq4$(this.temp_0, 0, a, x, y, z);
                this.multiplyMM_3w9et8$(this.temp_0, 16, m, mOffset, this.temp_0, 0);
                for (var i = 0; i <= 15; i++) {
                  m[mOffset + i] = this.temp_0[16 + i];
                }
              },
              rotateEulerM_uypkq7$: function (rm, rmOffset, m, mOffset, rotX, rotY, rotZ) {
                this.temp_0;
                this.setRotateEulerM_h6lr6x$(this.temp_0, 0, rotX, rotY, rotZ);
                this.multiplyMM_3w9et8$(rm, rmOffset, m, mOffset, this.temp_0, 0);
              },
              rotateEulerM_h6lr6x$: function (m, mOffset, rotX, rotY, rotZ) {
                this.temp_0;
                this.setRotateEulerM_h6lr6x$(this.temp_0, 0, rotX, rotY, rotZ);
                this.multiplyMM_3w9et8$(this.temp_0, 16, m, mOffset, this.temp_0, 0);
                for (var i = 0; i <= 15; i++) {
                  m[mOffset + i] = this.temp_0[16 + i];
                }
              },
              setRotateM_b0ilq4$: function (rm, rmOffset, rotA, axX, axY, axZ) {
                var a = rotA;
                var x = axX;
                var y = axY;
                var z = axZ;
                rm[rmOffset + 3] = 0.0;
                rm[rmOffset + 7] = 0.0;
                rm[rmOffset + 11] = 0.0;
                rm[rmOffset + 12] = 0.0;
                rm[rmOffset + 13] = 0.0;
                rm[rmOffset + 14] = 0.0;
                rm[rmOffset + 15] = 1.0;
                a *= Math.PI / 180.0;
                var s = Math.sin(a);
                var c = Math.cos(a);
                if (1.0 === x && 0.0 === y && 0.0 === z) {
                  rm[rmOffset + 5] = c;
                  rm[rmOffset + 10] = c;
                  rm[rmOffset + 6] = s;
                  rm[rmOffset + 9] = -s;
                  rm[rmOffset + 1] = 0.0;
                  rm[rmOffset + 2] = 0.0;
                  rm[rmOffset + 4] = 0.0;
                  rm[rmOffset + 8] = 0.0;
                  rm[rmOffset + 0] = 1.0;
                }
                 else if (0.0 === x && 1.0 === y && 0.0 === z) {
                  rm[rmOffset + 0] = c;
                  rm[rmOffset + 10] = c;
                  rm[rmOffset + 8] = s;
                  rm[rmOffset + 2] = -s;
                  rm[rmOffset + 1] = 0.0;
                  rm[rmOffset + 4] = 0.0;
                  rm[rmOffset + 6] = 0.0;
                  rm[rmOffset + 9] = 0.0;
                  rm[rmOffset + 5] = 1.0;
                }
                 else if (0.0 === x && 0.0 === y && 1.0 === z) {
                  rm[rmOffset + 0] = c;
                  rm[rmOffset + 5] = c;
                  rm[rmOffset + 1] = s;
                  rm[rmOffset + 4] = -s;
                  rm[rmOffset + 2] = 0.0;
                  rm[rmOffset + 6] = 0.0;
                  rm[rmOffset + 8] = 0.0;
                  rm[rmOffset + 9] = 0.0;
                  rm[rmOffset + 10] = 1.0;
                }
                 else {
                  var len = this.length_0(x, y, z);
                  if (1.0 !== len) {
                    var recipLen = 1.0 / len;
                    x *= recipLen;
                    y *= recipLen;
                    z *= recipLen;
                  }
                  var nc = 1.0 - c;
                  var xy = x * y;
                  var yz = y * z;
                  var zx = z * x;
                  var xs = x * s;
                  var ys = y * s;
                  var zs = z * s;
                  rm[rmOffset + 0] = x * x * nc + c;
                  rm[rmOffset + 4] = xy * nc - zs;
                  rm[rmOffset + 8] = zx * nc + ys;
                  rm[rmOffset + 1] = xy * nc + zs;
                  rm[rmOffset + 5] = y * y * nc + c;
                  rm[rmOffset + 9] = yz * nc - xs;
                  rm[rmOffset + 2] = zx * nc - ys;
                  rm[rmOffset + 6] = yz * nc + xs;
                  rm[rmOffset + 10] = z * z * nc + c;
                }
              },
              setRotateEulerM_h6lr6x$: function (rm, rmOffset, rotX, rotY, rotZ) {
                var x = rotX;
                var y = rotY;
                var z = rotZ;
                x *= Math.PI / 180.0;
                y *= Math.PI / 180.0;
                z *= Math.PI / 180.0;
                var cx = Math.cos(x);
                var sx = Math.sin(x);
                var cy = Math.cos(y);
                var sy = Math.sin(y);
                var cz = Math.cos(z);
                var sz = Math.sin(z);
                var cxsy = cx * sy;
                var sxsy = sx * sy;
                rm[rmOffset + 0] = cy * cz;
                rm[rmOffset + 1] = -cy * sz;
                rm[rmOffset + 2] = sy;
                rm[rmOffset + 3] = 0.0;
                rm[rmOffset + 4] = cxsy * cz + cx * sz;
                rm[rmOffset + 5] = -cxsy * sz + cx * cz;
                rm[rmOffset + 6] = -sx * cy;
                rm[rmOffset + 7] = 0.0;
                rm[rmOffset + 8] = -sxsy * cz + sx * sz;
                rm[rmOffset + 9] = sxsy * sz + sx * cz;
                rm[rmOffset + 10] = cx * cy;
                rm[rmOffset + 11] = 0.0;
                rm[rmOffset + 12] = 0.0;
                rm[rmOffset + 13] = 0.0;
                rm[rmOffset + 14] = 0.0;
                rm[rmOffset + 15] = 1.0;
              },
              setLookAtM_u6vibb$: function (rm, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ) {
                var fx = centerX - eyeX;
                var fy = centerY - eyeY;
                var fz = centerZ - eyeZ;
                var rlf = 1.0 / _.de.fabmax.kool.util.MatrixMath.length_0(fx, fy, fz);
                fx *= rlf;
                fy *= rlf;
                fz *= rlf;
                var sx = fy * upZ - fz * upY;
                var sy = fz * upX - fx * upZ;
                var sz = fx * upY - fy * upX;
                var rls = 1.0 / _.de.fabmax.kool.util.MatrixMath.length_0(sx, sy, sz);
                sx *= rls;
                sy *= rls;
                sz *= rls;
                var ux = sy * fz - sz * fy;
                var uy = sz * fx - sx * fz;
                var uz = sx * fy - sy * fx;
                rm[rmOffset + 0] = sx;
                rm[rmOffset + 1] = ux;
                rm[rmOffset + 2] = -fx;
                rm[rmOffset + 3] = 0.0;
                rm[rmOffset + 4] = sy;
                rm[rmOffset + 5] = uy;
                rm[rmOffset + 6] = -fy;
                rm[rmOffset + 7] = 0.0;
                rm[rmOffset + 8] = sz;
                rm[rmOffset + 9] = uz;
                rm[rmOffset + 10] = -fz;
                rm[rmOffset + 11] = 0.0;
                rm[rmOffset + 12] = 0.0;
                rm[rmOffset + 13] = 0.0;
                rm[rmOffset + 14] = 0.0;
                rm[rmOffset + 15] = 1.0;
                this.translateM_h6lr6x$(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
              }
            }, /** @lends _.de.fabmax.kool.util.MatrixMath */ {
            }),
            mesh_3sqmh1$: function (withNormals, withColors, withTexCoords, name, block) {
              if (name === void 0)
                name = null;
              var mesh = new _.de.fabmax.kool.scene.Mesh(withNormals, withColors, withTexCoords, name);
              var builder = new _.de.fabmax.kool.util.MeshBuilder(mesh);
              mesh.batchUpdate = true;
              block.call(builder);
              if (builder.shader != null) {
                mesh.shader = builder.shader;
              }
              mesh.batchUpdate = false;
              return mesh;
            },
            textureMesh_gac8tm$: function (name, block) {
              if (name === void 0)
                name = null;
              return _.de.fabmax.kool.util.mesh_3sqmh1$(true, false, true, name, block);
            },
            colorMesh_gac8tm$: function (name, block) {
              if (name === void 0)
                name = null;
              return _.de.fabmax.kool.util.mesh_3sqmh1$(true, true, false, name, block);
            },
            MeshBuilder: Kotlin.createClass(null, function MeshBuilder(mesh) {
              this.mesh = mesh;
              this.transform = new _.de.fabmax.kool.util.Mat4fStack();
              this.color = _.de.fabmax.kool.util.Color.Companion.BLACK;
              this.vertexModFun = null;
              this.shader = null;
              this.tmpPos_w0m2r5$_0 = _.de.fabmax.kool.util.MutableVec3f_init();
              this.tmpNrm_w0m2r5$_0 = _.de.fabmax.kool.util.MutableVec3f_init();
              this.circleProps_w0m2r5$_0 = new _.de.fabmax.kool.util.CircleProps();
              this.cubeProps_w0m2r5$_0 = new _.de.fabmax.kool.util.CubeProps();
              this.cylinderProps_w0m2r5$_0 = new _.de.fabmax.kool.util.CylinderProps();
              this.rectProps_w0m2r5$_0 = new _.de.fabmax.kool.util.RectProps();
              this.sphereProps_w0m2r5$_0 = new _.de.fabmax.kool.util.SphereProps();
              this.shader = _.de.fabmax.kool.shading.basicShader_9yb6un$(_.de.fabmax.kool.util.MeshBuilder.MeshBuilder$f(this));
            }, /** @lends _.de.fabmax.kool.util.MeshBuilder.prototype */ {
              vertex_hd42uj$: function (pos, nrm, uv) {
                if (uv === void 0)
                  uv = _.de.fabmax.kool.util.Vec2f.Companion.ZERO;
                return this.mesh.addVertex_570ote$(_.de.fabmax.kool.util.MeshBuilder.vertex_hd42uj$f(pos, nrm, uv, this));
              },
              withTransform_mngb98$: function (block) {
                this.transform.push();
                block.call(this);
                this.transform.pop();
              },
              withColor_fs365k$: function (color, block) {
                var c = this.color;
                if (color != null) {
                  this.color = color;
                }
                block.call(this);
                this.color = c;
              },
              translate_cx11x8$: function (t) {
                return this.transform.translate_y2kzbl$(t.x, t.y, t.z);
              },
              translate_y2kzbl$: function (x, y, z) {
                return this.transform.translate_y2kzbl$(x, y, z);
              },
              rotate_ag3lbb$: function (angleDeg, axis) {
                return this.transform.rotate_ag3lbb$(angleDeg, axis);
              },
              rotate_7b5o5w$: function (angleDeg, axX, axY, axZ) {
                return this.transform.rotate_7b5o5w$(angleDeg, axX, axY, axZ);
              },
              rotateEuler_y2kzbl$: function (xDeg, yDeg, zDeg) {
                return this.transform.rotateEuler_y2kzbl$(xDeg, yDeg, zDeg);
              },
              scale_y2kzbl$: function (x, y, z) {
                return this.transform.scale_y2kzbl$(x, y, z);
              },
              setCoordSystem_bcrh1o$: function (origin, right, up, top) {
                if (top === void 0)
                  top = null;
                var topV = top;
                if (topV == null) {
                  topV = _.de.fabmax.kool.util.cross_winjqc$(right, up);
                }
                this.transform.setIdentity();
                this.transform.set_n0b4r3$(0, 0, right.x);
                this.transform.set_n0b4r3$(0, 1, right.y);
                this.transform.set_n0b4r3$(0, 2, right.z);
                this.transform.set_n0b4r3$(1, 0, up.x);
                this.transform.set_n0b4r3$(1, 1, up.y);
                this.transform.set_n0b4r3$(1, 2, up.z);
                this.transform.set_n0b4r3$(2, 0, topV.x);
                this.transform.set_n0b4r3$(2, 1, topV.y);
                this.transform.set_n0b4r3$(2, 2, topV.z);
                this.transform.set_n0b4r3$(3, 0, origin.x);
                this.transform.set_n0b4r3$(3, 1, origin.y);
                this.transform.set_n0b4r3$(3, 2, origin.z);
              },
              circle_edvpwy$: function (props) {
                props.call(this.circleProps_w0m2r5$_0.defaults());
                this.circle_59f34t$(this.circleProps_w0m2r5$_0);
              },
              circle_59f34t$: function (props) {
                var tmp$0;
                var i0 = 0;
                var i1 = 0;
                tmp$0 = props.steps;
                for (var i = 0; i <= tmp$0; i++) {
                  var px = props.center.x + props.radius * Math.cos(i * Math.PI * 2 / props.steps);
                  var py = props.center.y + props.radius * Math.sin(i * Math.PI * 2 / props.steps);
                  var idx = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(px, py, props.center.z), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS);
                  if (i === 0) {
                    i0 = idx;
                  }
                   else if (i === 1) {
                    i1 = idx;
                  }
                   else {
                    this.mesh.addTriIndices_qt1dr2$(i0, i1, idx);
                    i1 = idx;
                  }
                }
              },
              sphere_s9x6gh$: function (props) {
                props.call(this.sphereProps_w0m2r5$_0.defaults());
                this.sphere_mojs8w$(this.sphereProps_w0m2r5$_0);
              },
              sphere_mojs8w$: function (props) {
                var tmp$0, tmp$1, tmp$2, tmp$3;
                var steps = Math.max(props.steps / 2 | 0, 4);
                var prevIndices = Kotlin.numberArrayOfSize(steps * 2 + 1);
                var rowIndices = Kotlin.numberArrayOfSize(steps * 2 + 1);
                var iCenter = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(props.center.x, props.center.y - props.radius, props.center.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Y_AXIS);
                var r = Math.sin(Math.PI / steps) * props.radius;
                var y = -Math.cos(Math.PI / steps) * props.radius;
                tmp$0 = steps * 2;
                for (var i = 0; i <= tmp$0; i++) {
                  var x = Math.cos(Math.PI * i / steps) * r;
                  var z = Math.sin(Math.PI * i / steps) * r;
                  rowIndices[i] = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(x, y, z), this.tmpNrm_w0m2r5$_0.set_y2kzbl$(x, y, z).scale_mx4ult$(1.0 / props.radius));
                  if (i > 0) {
                    this.mesh.addTriIndices_qt1dr2$(iCenter, rowIndices[i - 1], rowIndices[i]);
                  }
                }
                tmp$1 = steps - 1;
                for (var row = 2; row <= tmp$1; row++) {
                  var tmp = prevIndices;
                  prevIndices = rowIndices;
                  rowIndices = tmp;
                  r = Math.sin(Math.PI * row / steps) * props.radius;
                  y = -Math.cos(Math.PI * row / steps) * props.radius;
                  tmp$2 = steps * 2;
                  for (var i_0 = 0; i_0 <= tmp$2; i_0++) {
                    var x_0 = Math.cos(Math.PI * i_0 / steps) * r;
                    var z_0 = Math.sin(Math.PI * i_0 / steps) * r;
                    rowIndices[i_0] = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(x_0, y, z_0), this.tmpNrm_w0m2r5$_0.set_y2kzbl$(x_0, y, z_0).scale_mx4ult$(1.0 / props.radius));
                    if (i_0 > 0) {
                      this.mesh.addTriIndices_qt1dr2$(prevIndices[i_0 - 1], rowIndices[i_0 - 1], rowIndices[i_0]);
                      this.mesh.addTriIndices_qt1dr2$(prevIndices[i_0 - 1], rowIndices[i_0], prevIndices[i_0]);
                    }
                  }
                }
                iCenter = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(props.center.x, props.center.y + props.radius, props.center.z), _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
                tmp$3 = steps * 2;
                for (var i_1 = 1; i_1 <= tmp$3; i_1++) {
                  this.mesh.addTriIndices_qt1dr2$(iCenter, rowIndices[i_1], rowIndices[i_1 - 1]);
                }
              },
              rect_ud8hiy$: function (props) {
                props.call(this.rectProps_w0m2r5$_0.defaults());
                this.rect_e5k3t5$(this.rectProps_w0m2r5$_0);
              },
              rect_e5k3t5$: function (props) {
                props.fixNegativeSize();
                var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS, props.texCoordLowerLeft);
                var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(props.origin.x + props.width, props.origin.y, props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS, props.texCoordLowerRight);
                var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(props.origin.x + props.width, props.origin.y + props.height, props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS, props.texCoordUpperRight);
                var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.height, props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS, props.texCoordUpperLeft);
                this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
              },
              cube_9hfdbr$: function (props) {
                props.call(this.cubeProps_w0m2r5$_0.defaults());
                this.cube_lhbb6w$(this.cubeProps_w0m2r5$_0);
              },
              cube_lhbb6w$: function (props) {
                props.fixNegativeSize();
                this.withColor_fs365k$(props.frontColor, _.de.fabmax.kool.util.MeshBuilder.cube_lhbb6w$f(props));
                this.withColor_fs365k$(props.rightColor, _.de.fabmax.kool.util.MeshBuilder.cube_lhbb6w$f_0(props));
                this.withColor_fs365k$(props.backColor, _.de.fabmax.kool.util.MeshBuilder.cube_lhbb6w$f_1(props));
                this.withColor_fs365k$(props.leftColor, _.de.fabmax.kool.util.MeshBuilder.cube_lhbb6w$f_2(props));
                this.withColor_fs365k$(props.topColor, _.de.fabmax.kool.util.MeshBuilder.cube_lhbb6w$f_3(props));
                this.withColor_fs365k$(props.bottomColor, _.de.fabmax.kool.util.MeshBuilder.cube_lhbb6w$f_4(props));
              },
              cylinder_7jdbew$: function (props) {
                props.call(this.cylinderProps_w0m2r5$_0.defaults());
                this.cylinder_tnt2h$(this.cylinderProps_w0m2r5$_0);
              },
              cylinder_tnt2h$: function (props) {
                var tmp$0;
                props.fixNegativeSize();
                this.withTransform_mngb98$(_.de.fabmax.kool.util.MeshBuilder.cylinder_tnt2h$f(props));
                this.withTransform_mngb98$(_.de.fabmax.kool.util.MeshBuilder.cylinder_tnt2h$f_0(props));
                var dr = props.bottomRadius - props.topRadius;
                var nrmAng = 90.0 - _.de.fabmax.kool.util.toDeg_mx4ult$(Math.acos(dr / Math.sqrt(dr * dr + props.height * props.height)));
                var i0 = 0;
                var i1 = 0;
                tmp$0 = props.steps;
                for (var i = 0; i <= tmp$0; i++) {
                  var c = Math.cos(i * Math.PI * 2 / props.steps);
                  var s = Math.sin(i * Math.PI * 2 / props.steps);
                  var px2 = props.origin.x + props.bottomRadius * c;
                  var pz2 = props.origin.z + props.bottomRadius * s;
                  var px3 = props.origin.x + props.topRadius * c;
                  var pz3 = props.origin.z + props.topRadius * s;
                  this.tmpNrm_w0m2r5$_0.set_y2kzbl$(c, 0.0, s).rotate_7b5o5w$(nrmAng, s, 0.0, c);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(px2, props.origin.y, pz2), this.tmpNrm_w0m2r5$_0);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(px3, props.origin.y + props.height, pz3), this.tmpNrm_w0m2r5$_0);
                  if (i > 0) {
                    this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                    this.mesh.addTriIndices_qt1dr2$(i1, i3, i2);
                  }
                  i0 = i2;
                  i1 = i3;
                }
              }
            }, /** @lends _.de.fabmax.kool.util.MeshBuilder */ {
              vertex_hd42uj$f: function (closure$pos, closure$nrm, closure$uv, this$MeshBuilder) {
                return function () {
                  var tmp$0;
                  this.position.set_cx11x8$(closure$pos);
                  this.normal.set_cx11x8$(closure$nrm);
                  this.texCoord.set_cx11y3$(closure$uv);
                  this.color.set_d7aj7k$(this$MeshBuilder.color);
                  (tmp$0 = this$MeshBuilder.vertexModFun) != null ? tmp$0.call(this) : null;
                  this$MeshBuilder.transform.transform_64rgyf$(this.position);
                  if (this$MeshBuilder.mesh.hasNormals) {
                    this$MeshBuilder.transform.transform_64rgyf$(this.normal, 0.0);
                    this.normal.norm();
                  }
                };
              },
              cube_lhbb6w$f: function (closure$props) {
                return function () {
                  var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS);
                  var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y + closure$props.height, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS);
                  this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                  this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
                };
              },
              cube_lhbb6w$f_0: function (closure$props) {
                return function () {
                  var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
                  var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y + closure$props.height, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y + closure$props.height, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
                  this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                  this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
                };
              },
              cube_lhbb6w$f_1: function (closure$props) {
                return function () {
                  var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Z_AXIS);
                  var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y + closure$props.height, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Z_AXIS);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Z_AXIS);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Z_AXIS);
                  this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                  this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
                };
              },
              cube_lhbb6w$f_2: function (closure$props) {
                return function () {
                  var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.NEG_X_AXIS);
                  var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.NEG_X_AXIS);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_X_AXIS);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_X_AXIS);
                  this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                  this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
                };
              },
              cube_lhbb6w$f_3: function (closure$props) {
                return function () {
                  var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
                  var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y + closure$props.height, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y + closure$props.height, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS);
                  this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                  this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
                };
              },
              cube_lhbb6w$f_4: function (closure$props) {
                return function () {
                  var i0 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Y_AXIS);
                  var i1 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y, closure$props.origin.z), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Y_AXIS);
                  var i2 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x + closure$props.width, closure$props.origin.y, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Y_AXIS);
                  var i3 = this.vertex_hd42uj$(this.tmpPos_w0m2r5$_0.set_y2kzbl$(closure$props.origin.x, closure$props.origin.y, closure$props.origin.z + closure$props.depth), _.de.fabmax.kool.util.Vec3f.Companion.NEG_Y_AXIS);
                  this.mesh.addTriIndices_qt1dr2$(i0, i1, i2);
                  this.mesh.addTriIndices_qt1dr2$(i0, i2, i3);
                };
              },
              f: function (closure$props) {
                return function () {
                  this.steps = closure$props.steps;
                  this.radius = closure$props.bottomRadius;
                };
              },
              cylinder_tnt2h$f: function (closure$props) {
                return function () {
                  this.translate_cx11x8$(closure$props.origin);
                  this.rotate_ag3lbb$(90.0, _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
                  this.circle_edvpwy$(_.de.fabmax.kool.util.MeshBuilder.f(closure$props));
                };
              },
              f_0: function (closure$props) {
                return function () {
                  this.steps = closure$props.steps;
                  this.radius = closure$props.topRadius;
                };
              },
              cylinder_tnt2h$f_0: function (closure$props) {
                return function () {
                  this.translate_y2kzbl$(closure$props.origin.x, closure$props.origin.y + closure$props.height, closure$props.origin.z);
                  this.rotate_ag3lbb$(-90.0, _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS);
                  this.circle_edvpwy$(_.de.fabmax.kool.util.MeshBuilder.f_0(closure$props));
                };
              },
              MeshBuilder$f: function (this$MeshBuilder) {
                return function () {
                  if (this$MeshBuilder.mesh.hasNormals) {
                    this.lightModel = _.de.fabmax.kool.shading.LightModel.PHONG_LIGHTING;
                  }
                   else {
                    this.lightModel = _.de.fabmax.kool.shading.LightModel.NO_LIGHTING;
                  }
                  if (this$MeshBuilder.mesh.hasTexCoords) {
                    this.colorModel = _.de.fabmax.kool.shading.ColorModel.TEXTURE_COLOR;
                  }
                   else if (this$MeshBuilder.mesh.hasColors) {
                    this.colorModel = _.de.fabmax.kool.shading.ColorModel.VERTEX_COLOR;
                  }
                   else {
                    this.colorModel = _.de.fabmax.kool.shading.ColorModel.STATIC_COLOR;
                  }
                };
              }
            }),
            CircleProps: Kotlin.createClass(null, function CircleProps() {
              this.radius = 1.0;
              this.steps = 20;
              this.center = _.de.fabmax.kool.util.MutableVec3f_init();
            }, /** @lends _.de.fabmax.kool.util.CircleProps.prototype */ {
              defaults: function () {
                this.radius = 1.0;
                this.steps = 20;
                this.center.set_cx11x8$(_.de.fabmax.kool.util.Vec3f.Companion.ZERO);
                return this;
              }
            }),
            SphereProps: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.CircleProps];
            }, function SphereProps() {
              SphereProps.baseInitializer.call(this);
            }, /** @lends _.de.fabmax.kool.util.SphereProps.prototype */ {
              defaults: function () {
                _.de.fabmax.kool.util.CircleProps.prototype.defaults.call(this);
                return this;
              }
            }),
            RectProps: Kotlin.createClass(null, function RectProps() {
              this.width = 1.0;
              this.height = 1.0;
              this.origin = _.de.fabmax.kool.util.MutableVec3f_init();
              this.texCoordUpperLeft = new _.de.fabmax.kool.util.MutableVec2f(0.0, 0.0);
              this.texCoordUpperRight = new _.de.fabmax.kool.util.MutableVec2f(1.0, 0.0);
              this.texCoordLowerLeft = new _.de.fabmax.kool.util.MutableVec2f(0.0, 1.0);
              this.texCoordLowerRight = new _.de.fabmax.kool.util.MutableVec2f(1.0, 1.0);
            }, /** @lends _.de.fabmax.kool.util.RectProps.prototype */ {
              fixNegativeSize: function () {
                if (this.width < 0) {
                  this.origin.x = this.origin.x + this.width;
                  this.width = -this.width;
                }
                if (this.height < 0) {
                  this.origin.y = this.origin.y + this.height;
                  this.height = -this.height;
                }
              },
              defaults: function () {
                this.width = 1.0;
                this.height = 1.0;
                this.origin.set_cx11x8$(_.de.fabmax.kool.util.Vec3f.Companion.ZERO);
                return this;
              }
            }),
            CubeProps: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.RectProps];
            }, function CubeProps() {
              CubeProps.baseInitializer.call(this);
              this.depth = 1.0;
              this.topColor = null;
              this.bottomColor = null;
              this.leftColor = null;
              this.rightColor = null;
              this.frontColor = null;
              this.backColor = null;
            }, /** @lends _.de.fabmax.kool.util.CubeProps.prototype */ {
              fixNegativeSize: function () {
                _.de.fabmax.kool.util.RectProps.prototype.fixNegativeSize.call(this);
                if (this.depth < 0) {
                  this.origin.z = this.origin.z + this.depth;
                  this.depth = -this.depth;
                }
              },
              defaults: function () {
                _.de.fabmax.kool.util.RectProps.prototype.defaults.call(this);
                this.depth = 1.0;
                this.topColor = null;
                this.bottomColor = null;
                this.leftColor = null;
                this.rightColor = null;
                this.frontColor = null;
                this.backColor = null;
                return this;
              }
            }),
            CylinderProps: Kotlin.createClass(null, function CylinderProps() {
              this.bottomRadius = 1.0;
              this.topRadius = 1.0;
              this.steps = 20;
              this.height = 1.0;
              this.origin = _.de.fabmax.kool.util.MutableVec3f_init();
            }, /** @lends _.de.fabmax.kool.util.CylinderProps.prototype */ {
              defaults: function () {
                this.bottomRadius = 1.0;
                this.topRadius = 1.0;
                this.steps = 20;
                this.height = 1.0;
                this.origin.set_cx11x8$(_.de.fabmax.kool.util.Vec3f.Companion.ZERO);
                return this;
              },
              fixNegativeSize: function () {
                if (this.height < 0) {
                  this.origin.y = this.origin.y + this.height;
                  this.height = -this.height;
                }
              }
            }),
            Property: Kotlin.createClass(null, function Property(value) {
              this.value_0 = value;
              this.valueChanged_g2zh5q$_0 = true;
            }, /** @lends _.de.fabmax.kool.util.Property.prototype */ {
              clear: {
                get: function () {
                  this.valueChanged = false;
                  return this.value_0;
                }
              },
              valueChanged: {
                get: function () {
                  return this.valueChanged_g2zh5q$_0;
                },
                set: function (valueChanged_0) {
                  this.valueChanged_g2zh5q$_0 = valueChanged_0;
                }
              },
              getValue_dsk1ci$: function (thisRef, property) {
                return this.value_0;
              },
              setValue_w32e13$: function (thisRef, property, value) {
                if (!Kotlin.equals(value, this.value_0)) {
                  this.value_0 = value;
                  this.valueChanged = true;
                }
              }
            }),
            add_winjqc$: function (a, b) {
              return a.add_s0th1g$(_.de.fabmax.kool.util.MutableVec3f_init(), b);
            },
            subtract_winjqc$: function (a, b) {
              return a.subtract_s0th1g$(_.de.fabmax.kool.util.MutableVec3f_init(), b);
            },
            scale_oaxj1z$: function (a, fac) {
              return a.scale_64rgyf$(_.de.fabmax.kool.util.MutableVec3f_init(), fac);
            },
            norm_cx11x8$: function (a) {
              return a.norm_aq7j6k$(_.de.fabmax.kool.util.MutableVec3f_init());
            },
            cross_winjqc$: function (a, b) {
              return a.cross_s0th1g$(_.de.fabmax.kool.util.MutableVec3f_init(), b);
            },
            Vec2f: Kotlin.createClass(null, function Vec2f(x, y) {
              this.x_pqrnjp$_0 = x;
              this.y_pqrnjp$_0 = y;
            }, /** @lends _.de.fabmax.kool.util.Vec2f.prototype */ {
              x: {
                get: function () {
                  return this.x_pqrnjp$_0;
                },
                set: function (x_0) {
                  this.x_pqrnjp$_0 = x_0;
                }
              },
              y: {
                get: function () {
                  return this.y_pqrnjp$_0;
                },
                set: function (y_0) {
                  this.y_pqrnjp$_0 = y_0;
                }
              },
              get_za3lpa$: function (i) {
                if (i === 0)
                  return this.x;
                else if (i === 1)
                  return this.y;
                else
                  throw new _.de.fabmax.kool.KoolException('Invalid index: ' + i);
              },
              toString: function () {
                return '(' + this.x + ', ' + this.y + ')';
              }
            }, /** @lends _.de.fabmax.kool.util.Vec2f */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.Vec2f.Companion.ZERO = _.de.fabmax.kool.util.Vec2f_init_mx4ult$(0.0);
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.Vec2f.Companion;
              }
            }),
            Vec2f_init_mx4ult$: function (f, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.Vec2f.prototype);
              _.de.fabmax.kool.util.Vec2f.call($this, f, f);
              return $this;
            },
            MutableVec2f: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.Vec2f];
            }, function MutableVec2f(x, y) {
              MutableVec2f.baseInitializer.call(this, x, y);
            }, /** @lends _.de.fabmax.kool.util.MutableVec2f.prototype */ {
              x: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec2f, 'x');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec2f, 'x', value);
                }
              },
              y: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec2f, 'y');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec2f, 'y', value);
                }
              },
              set_dleff0$: function (x, y) {
                this.x = x;
                this.y = y;
                return this;
              },
              set_cx11y3$: function (other) {
                this.x = other.x;
                this.y = other.y;
                return this;
              },
              set_24o109$: function (i, v) {
                if (i === 0)
                  this.x = v;
                else if (i === 1)
                  this.y = v;
                else
                  throw new _.de.fabmax.kool.KoolException('Invalid index: ' + i);
              }
            }),
            MutableVec2f_init: function ($this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableVec2f.prototype);
              _.de.fabmax.kool.util.MutableVec2f.call($this, 0.0, 0.0);
              return $this;
            },
            MutableVec2f_init_cx11y3$: function (other, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableVec2f.prototype);
              _.de.fabmax.kool.util.MutableVec2f.call($this, other.x, other.y);
              return $this;
            },
            Vec3f: Kotlin.createClass(null, function Vec3f(x, y, z) {
              this.x_pqroae$_0 = x;
              this.y_pqroae$_0 = y;
              this.z_pqroae$_0 = z;
            }, /** @lends _.de.fabmax.kool.util.Vec3f.prototype */ {
              x: {
                get: function () {
                  return this.x_pqroae$_0;
                },
                set: function (x_0) {
                  this.x_pqroae$_0 = x_0;
                }
              },
              y: {
                get: function () {
                  return this.y_pqroae$_0;
                },
                set: function (y_0) {
                  this.y_pqroae$_0 = y_0;
                }
              },
              z: {
                get: function () {
                  return this.z_pqroae$_0;
                },
                set: function (z_0) {
                  this.z_pqroae$_0 = z_0;
                }
              },
              get_za3lpa$: function (i) {
                if (i === 0)
                  return this.x;
                else if (i === 1)
                  return this.y;
                else if (i === 2)
                  return this.z;
                else
                  throw new _.de.fabmax.kool.KoolException('Invalid index: ' + i);
              },
              times_cx11x8$: function (other) {
                return this.x * other.x + this.y * other.y + this.z * other.z;
              },
              sqrLength: function () {
                return this.x * this.x + this.y * this.y + this.z * this.z;
              },
              length: function () {
                return Math.sqrt(this.sqrLength());
              },
              add_s0th1g$: function (result, other) {
                result.x = this.x + other.x;
                result.y = this.y + other.y;
                result.z = this.z + other.z;
                return result;
              },
              subtract_s0th1g$: function (result, other) {
                result.x = this.x - other.x;
                result.y = this.y - other.y;
                result.z = this.z - other.z;
                return result;
              },
              scale_64rgyf$: function (result, factor) {
                result.x = this.x * factor;
                result.y = this.y * factor;
                result.z = this.z * factor;
                return result;
              },
              norm_aq7j6k$: function (result) {
                var lenReciproc = 1.0 / this.length();
                result.x = this.x * lenReciproc;
                result.y = this.y * lenReciproc;
                result.z = this.z * lenReciproc;
                return result;
              },
              cross_s0th1g$: function (result, other) {
                result.x = this.y * other.z - this.z * other.y;
                result.y = this.z * other.x - this.x * other.z;
                result.z = this.x * other.x - this.y * other.x;
                return result;
              },
              toString: function () {
                return '(' + this.x + ', ' + this.y + ', ' + this.z + ')';
              }
            }, /** @lends _.de.fabmax.kool.util.Vec3f */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.Vec3f.Companion.X_AXIS = new _.de.fabmax.kool.util.Vec3f(1.0, 0.0, 0.0);
                _.de.fabmax.kool.util.Vec3f.Companion.Y_AXIS = new _.de.fabmax.kool.util.Vec3f(0.0, 1.0, 0.0);
                _.de.fabmax.kool.util.Vec3f.Companion.Z_AXIS = new _.de.fabmax.kool.util.Vec3f(0.0, 0.0, 1.0);
                _.de.fabmax.kool.util.Vec3f.Companion.NEG_X_AXIS = new _.de.fabmax.kool.util.Vec3f(-1.0, 0.0, 0.0);
                _.de.fabmax.kool.util.Vec3f.Companion.NEG_Y_AXIS = new _.de.fabmax.kool.util.Vec3f(0.0, -1.0, 0.0);
                _.de.fabmax.kool.util.Vec3f.Companion.NEG_Z_AXIS = new _.de.fabmax.kool.util.Vec3f(0.0, 0.0, -1.0);
                _.de.fabmax.kool.util.Vec3f.Companion.ZERO = _.de.fabmax.kool.util.Vec3f_init_mx4ult$(0.0);
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.Vec3f.Companion;
              }
            }),
            Vec3f_init_mx4ult$: function (f, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.Vec3f.prototype);
              _.de.fabmax.kool.util.Vec3f.call($this, f, f, f);
              return $this;
            },
            MutableVec3f: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.Vec3f];
            }, function MutableVec3f(x, y, z) {
              MutableVec3f.baseInitializer.call(this, x, y, z);
            }, /** @lends _.de.fabmax.kool.util.MutableVec3f.prototype */ {
              x: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec3f, 'x');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec3f, 'x', value);
                }
              },
              y: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec3f, 'y');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec3f, 'y', value);
                }
              },
              z: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec3f, 'z');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec3f, 'z', value);
                }
              },
              set_y2kzbl$: function (x, y, z) {
                this.x = x;
                this.y = y;
                this.z = z;
                return this;
              },
              set_cx11x8$: function (other) {
                this.x = other.x;
                this.y = other.y;
                this.z = other.z;
                return this;
              },
              add_cx11x8$: function (other) {
                this.x += other.x;
                this.y += other.y;
                this.z += other.z;
                return this;
              },
              plusAssign_cx11x8$: function (other) {
                this.add_cx11x8$(other);
              },
              subtract_cx11x8$: function (other) {
                this.x -= other.x;
                this.y -= other.y;
                this.z -= other.z;
                return this;
              },
              minusAssign_cx11x8$: function (other) {
                this.subtract_cx11x8$(other);
              },
              scale_mx4ult$: function (factor) {
                this.x *= factor;
                this.y *= factor;
                this.z *= factor;
                return this;
              },
              timesAssign_mx4ult$: function (factor) {
                this.scale_mx4ult$(factor);
              },
              divAssign_mx4ult$: function (div) {
                this.scale_mx4ult$(1.0 / div);
              },
              norm: function () {
                this.scale_mx4ult$(1.0 / this.length());
                return this;
              },
              rotate_ag3lbb$: function (angleDeg, axis) {
                return this.rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
              },
              rotate_7b5o5w$: function (angleDeg, axisX, axisY, axisZ) {
                var rad = _.de.fabmax.kool.util.toRad_mx4ult$(angleDeg);
                var c = Math.cos(rad);
                var c1 = 1.0 - c;
                var s = Math.sin(rad);
                var tx = this.x * (axisX * axisX * c1 + c) + this.y * (axisX * axisY * c1 - axisZ * s) + this.z * (axisX * axisZ * c1 + axisY * s);
                var ty = this.x * (axisY * axisX * c1 + axisZ * s) + this.y * (axisY * axisY * c1 + c) + this.z * (axisY * axisZ * c1 - axisX * s);
                var tz = this.x * (axisX * axisZ * c1 - axisY * s) + this.y * (axisY * axisZ * c1 + axisX * s) + this.z * (axisZ * axisZ * c1 + c);
                this.x = tx;
                this.y = ty;
                this.z = tz;
                return this;
              },
              set_24o109$: function (i, v) {
                if (i === 0)
                  this.x = v;
                else if (i === 1)
                  this.y = v;
                else if (i === 2)
                  this.z = v;
                else
                  throw new _.de.fabmax.kool.KoolException('Invalid index: ' + i);
              }
            }),
            MutableVec3f_init: function ($this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableVec3f.prototype);
              _.de.fabmax.kool.util.MutableVec3f.call($this, 0.0, 0.0, 0.0);
              return $this;
            },
            MutableVec3f_init_cx11x8$: function (other, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableVec3f.prototype);
              _.de.fabmax.kool.util.MutableVec3f.call($this, other.x, other.y, other.z);
              return $this;
            },
            Vec4f: Kotlin.createClass(null, function Vec4f(x, y, z, w) {
              this.x_pqrp13$_0 = x;
              this.y_pqrp13$_0 = y;
              this.z_pqrp13$_0 = z;
              this.w_pqrp13$_0 = w;
            }, /** @lends _.de.fabmax.kool.util.Vec4f.prototype */ {
              x: {
                get: function () {
                  return this.x_pqrp13$_0;
                },
                set: function (x_0) {
                  this.x_pqrp13$_0 = x_0;
                }
              },
              y: {
                get: function () {
                  return this.y_pqrp13$_0;
                },
                set: function (y_0) {
                  this.y_pqrp13$_0 = y_0;
                }
              },
              z: {
                get: function () {
                  return this.z_pqrp13$_0;
                },
                set: function (z_0) {
                  this.z_pqrp13$_0 = z_0;
                }
              },
              w: {
                get: function () {
                  return this.w_pqrp13$_0;
                },
                set: function (w_0) {
                  this.w_pqrp13$_0 = w_0;
                }
              },
              get_za3lpa$: function (i) {
                if (i === 0)
                  return this.x;
                else if (i === 1)
                  return this.y;
                else if (i === 2)
                  return this.z;
                else if (i === 3)
                  return this.w;
                else
                  throw new _.de.fabmax.kool.KoolException('Invalid index: ' + i);
              },
              toString: function () {
                return '(' + this.x + ', ' + this.y + ', ' + this.z + ', ' + this.w + ')';
              }
            }, /** @lends _.de.fabmax.kool.util.Vec4f */ {
              Companion: Kotlin.createObject(null, function Companion() {
                _.de.fabmax.kool.util.Vec4f.Companion.ZERO = _.de.fabmax.kool.util.Vec4f_init_mx4ult$(0.0);
              }),
              object_initializer$: function () {
                _.de.fabmax.kool.util.Vec4f.Companion;
              }
            }),
            Vec4f_init_mx4ult$: function (f, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.Vec4f.prototype);
              _.de.fabmax.kool.util.Vec4f.call($this, f, f, f, f);
              return $this;
            },
            MutableVec4f: Kotlin.createClass(function () {
              return [_.de.fabmax.kool.util.Vec4f];
            }, function MutableVec4f(x, y, z, w) {
              MutableVec4f.baseInitializer.call(this, x, y, z, w);
            }, /** @lends _.de.fabmax.kool.util.MutableVec4f.prototype */ {
              x: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec4f, 'x');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec4f, 'x', value);
                }
              },
              y: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec4f, 'y');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec4f, 'y', value);
                }
              },
              z: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec4f, 'z');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec4f, 'z', value);
                }
              },
              w: {
                get: function () {
                  return Kotlin.callGetter(this, _.de.fabmax.kool.util.Vec4f, 'w');
                },
                set: function (value) {
                  Kotlin.callSetter(this, _.de.fabmax.kool.util.Vec4f, 'w', value);
                }
              },
              set_7b5o5w$: function (x, y, z, w) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.w = w;
                return this;
              },
              set_cx11wd$: function (other) {
                this.x = other.x;
                this.y = other.y;
                this.z = other.z;
                this.w = other.w;
                return this;
              },
              set_24o109$: function (i, v) {
                if (i === 0)
                  this.x = v;
                else if (i === 1)
                  this.y = v;
                else if (i === 2)
                  this.z = v;
                else if (i === 3)
                  this.w = v;
                else
                  throw new _.de.fabmax.kool.KoolException('Invalid index: ' + i);
              }
            }),
            MutableVec4f_init: function ($this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableVec4f.prototype);
              _.de.fabmax.kool.util.MutableVec4f.call($this, 0.0, 0.0, 0.0, 0.0);
              return $this;
            },
            MutableVec4f_init_cx11wd$: function (other, $this) {
              $this = $this || Object.create(_.de.fabmax.kool.util.MutableVec4f.prototype);
              _.de.fabmax.kool.util.MutableVec4f.call($this, other.x, other.y, other.z, other.w);
              return $this;
            }
          })
        })
      })
    })
  });
  Kotlin.defineModule('kool', _);
  _.de.fabmax.kool.js.main_kand9s$([]);
  return _;
}(kotlin);
