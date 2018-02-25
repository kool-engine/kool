define(['exports', 'kotlin', 'kotlinx-serialization-runtime-js'], function (_, Kotlin, $module$kotlinx_serialization_runtime_js) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Unit = Kotlin.kotlin.Unit;
  var toBoxedChar = Kotlin.toBoxedChar;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var unboxChar = Kotlin.unboxChar;
  var toChar = Kotlin.toChar;
  var getCallableRef = Kotlin.getCallableRef;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var Exception = Kotlin.kotlin.Exception;
  var ensureNotNull = Kotlin.ensureNotNull;
  var PropertyMetadata = Kotlin.PropertyMetadata;
  var equals = Kotlin.equals;
  var throwCCE = Kotlin.throwCCE;
  var RuntimeException_init = Kotlin.kotlin.RuntimeException_init_pdl1vj$;
  var mutableListOf = Kotlin.kotlin.collections.mutableListOf_i5x0yv$;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var math = Kotlin.kotlin.math;
  var numberToInt = Kotlin.numberToInt;
  var first = Kotlin.kotlin.collections.first_2p1efm$;
  var last = Kotlin.kotlin.collections.last_2p1efm$;
  var get_indices = Kotlin.kotlin.collections.get_indices_gzk92b$;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var Any = Object;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var abs = Kotlin.kotlin.math.abs_za3lpa$;
  var kotlin_js_internal_IntCompanionObject = Kotlin.kotlin.js.internal.IntCompanionObject;
  var abs_0 = Kotlin.kotlin.math.abs_s8cxhz$;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var toString = Kotlin.toString;
  var mutableSetOf = Kotlin.kotlin.collections.mutableSetOf_i5x0yv$;
  var toHashSet = Kotlin.kotlin.collections.toHashSet_us0mfu$;
  var toShort = Kotlin.toShort;
  var get_lastIndex = Kotlin.kotlin.collections.get_lastIndex_55thoc$;
  var hashCode = Kotlin.hashCode;
  var lastIndexOf = Kotlin.kotlin.text.lastIndexOf_8eortd$;
  var indexOf = Kotlin.kotlin.text.indexOf_8eortd$;
  var StringBuilder = Kotlin.kotlin.text.StringBuilder;
  var toInt = Kotlin.kotlin.text.toInt_6ic1pp$;
  var get_indices_0 = Kotlin.kotlin.text.get_indices_gw00vp$;
  var Map = Kotlin.kotlin.collections.Map;
  var iterator = Kotlin.kotlin.text.iterator_gw00vp$;
  var SerialClassDescImpl = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.SerialClassDescImpl;
  var KSerializer = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.KSerializer;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var ProtoBuf = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.protobuf.ProtoBuf;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
  var round = Kotlin.kotlin.math.round_14dthe$;
  var startsWith = Kotlin.kotlin.text.startsWith_7epoxm$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mutableMapOf = Kotlin.kotlin.collections.mutableMapOf_qfcya0$;
  KoolException.prototype = Object.create(Exception.prototype);
  KoolException.prototype.constructor = KoolException;
  RenderPass.prototype = Object.create(Enum.prototype);
  RenderPass.prototype.constructor = RenderPass;
  ShaderManager.prototype = Object.create(SharedResManager.prototype);
  ShaderManager.prototype.constructor = ShaderManager;
  BufferedTextureData.prototype = Object.create(TextureData.prototype);
  BufferedTextureData.prototype.constructor = BufferedTextureData;
  Texture.prototype = Object.create(GlObject.prototype);
  Texture.prototype.constructor = Texture;
  TextureManager.prototype = Object.create(SharedResManager.prototype);
  TextureManager.prototype.constructor = TextureManager;
  LowPassFilter.prototype = Object.create(SampleNode.prototype);
  LowPassFilter.prototype.constructor = LowPassFilter;
  HighPassFilter.prototype = Object.create(SampleNode.prototype);
  HighPassFilter.prototype.constructor = HighPassFilter;
  MoodFilter.prototype = Object.create(SampleNode.prototype);
  MoodFilter.prototype.constructor = MoodFilter;
  HiHat.prototype = Object.create(SampleNode.prototype);
  HiHat.prototype.constructor = HiHat;
  Kick.prototype = Object.create(SampleNode.prototype);
  Kick.prototype.constructor = Kick;
  Melody.prototype = Object.create(SampleNode.prototype);
  Melody.prototype.constructor = Melody;
  Oscillator.prototype = Object.create(SampleNode.prototype);
  Oscillator.prototype.constructor = Oscillator;
  Pad.prototype = Object.create(SampleNode.prototype);
  Pad.prototype.constructor = Pad;
  Shaker.prototype = Object.create(SampleNode.prototype);
  Shaker.prototype.constructor = Shaker;
  Snare.prototype = Object.create(SampleNode.prototype);
  Snare.prototype.constructor = Snare;
  BufferResource.prototype = Object.create(GlResource.prototype);
  BufferResource.prototype.constructor = BufferResource;
  FramebufferResource.prototype = Object.create(GlResource.prototype);
  FramebufferResource.prototype.constructor = FramebufferResource;
  FbColorTexData.prototype = Object.create(TextureData.prototype);
  FbColorTexData.prototype.constructor = FbColorTexData;
  FbDepthTexData.prototype = Object.create(TextureData.prototype);
  FbDepthTexData.prototype.constructor = FbDepthTexData;
  GlResource$Type.prototype = Object.create(Enum.prototype);
  GlResource$Type.prototype.constructor = GlResource$Type;
  ProgramResource.prototype = Object.create(GlResource.prototype);
  ProgramResource.prototype.constructor = ProgramResource;
  RenderbufferResource.prototype = Object.create(GlResource.prototype);
  RenderbufferResource.prototype.constructor = RenderbufferResource;
  ShaderResource.prototype = Object.create(GlResource.prototype);
  ShaderResource.prototype.constructor = ShaderResource;
  TextureResource.prototype = Object.create(GlResource.prototype);
  TextureResource.prototype.constructor = TextureResource;
  BSplineVec2f.prototype = Object.create(BSpline.prototype);
  BSplineVec2f.prototype.constructor = BSplineVec2f;
  BSplineVec3f.prototype = Object.create(BSpline.prototype);
  BSplineVec3f.prototype.constructor = BSplineVec3f;
  Mat4fStack.prototype = Object.create(Mat4f.prototype);
  Mat4fStack.prototype.constructor = Mat4fStack;
  CubicPointDistribution.prototype = Object.create(PointDistribution.prototype);
  CubicPointDistribution.prototype.constructor = CubicPointDistribution;
  SphericalPointDistribution.prototype = Object.create(PointDistribution.prototype);
  SphericalPointDistribution.prototype.constructor = SphericalPointDistribution;
  MutableVec2f.prototype = Object.create(Vec2f.prototype);
  MutableVec2f.prototype.constructor = MutableVec2f;
  MutableVec3f.prototype = Object.create(Vec3f.prototype);
  MutableVec3f.prototype.constructor = MutableVec3f;
  MutableVec4f.prototype = Object.create(Vec4f.prototype);
  MutableVec4f.prototype.constructor = MutableVec4f;
  Camera.prototype = Object.create(Node.prototype);
  Camera.prototype.constructor = Camera;
  OrthographicCamera.prototype = Object.create(Camera.prototype);
  OrthographicCamera.prototype.constructor = OrthographicCamera;
  PerspectiveCamera.prototype = Object.create(Camera.prototype);
  PerspectiveCamera.prototype.constructor = PerspectiveCamera;
  Group.prototype = Object.create(Node.prototype);
  Group.prototype.constructor = Group;
  CullMethod.prototype = Object.create(Enum.prototype);
  CullMethod.prototype.constructor = CullMethod;
  Mesh.prototype = Object.create(Node.prototype);
  Mesh.prototype.constructor = Mesh;
  TransformGroup.prototype = Object.create(Group.prototype);
  TransformGroup.prototype.constructor = TransformGroup;
  Model.prototype = Object.create(TransformGroup.prototype);
  Model.prototype.constructor = Model;
  Scene.prototype = Object.create(Group.prototype);
  Scene.prototype.constructor = Scene;
  SphericalInputTransform$DragMethod.prototype = Object.create(Enum.prototype);
  SphericalInputTransform$DragMethod.prototype.constructor = SphericalInputTransform$DragMethod;
  SphericalInputTransform$ZoomMethod.prototype = Object.create(Enum.prototype);
  SphericalInputTransform$ZoomMethod.prototype.constructor = SphericalInputTransform$ZoomMethod;
  SphericalInputTransform.prototype = Object.create(TransformGroup.prototype);
  SphericalInputTransform.prototype.constructor = SphericalInputTransform;
  CameraOrthogonalPan.prototype = Object.create(PanBase.prototype);
  CameraOrthogonalPan.prototype.constructor = CameraOrthogonalPan;
  FixedPlanePan.prototype = Object.create(PanBase.prototype);
  FixedPlanePan.prototype.constructor = FixedPlanePan;
  Armature.prototype = Object.create(Mesh.prototype);
  Armature.prototype.constructor = Armature;
  RotationKey.prototype = Object.create(AnimationKey.prototype);
  RotationKey.prototype.constructor = RotationKey;
  PositionKey.prototype = Object.create(AnimationKey.prototype);
  PositionKey.prototype.constructor = PositionKey;
  ScalingKey.prototype = Object.create(AnimationKey.prototype);
  ScalingKey.prototype.constructor = ScalingKey;
  UiComponent.prototype = Object.create(TransformGroup.prototype);
  UiComponent.prototype.constructor = UiComponent;
  Label.prototype = Object.create(UiComponent.prototype);
  Label.prototype.constructor = Label;
  Button.prototype = Object.create(Label.prototype);
  Button.prototype.constructor = Button;
  ButtonUi.prototype = Object.create(LabelUi.prototype);
  ButtonUi.prototype.constructor = ButtonUi;
  BlurredComponentUi.prototype = Object.create(SimpleComponentUi.prototype);
  BlurredComponentUi.prototype.constructor = BlurredComponentUi;
  SizeUnit.prototype = Object.create(Enum.prototype);
  SizeUnit.prototype.constructor = SizeUnit;
  CombSizeSpec.prototype = Object.create(SizeSpec.prototype);
  CombSizeSpec.prototype.constructor = CombSizeSpec;
  Alignment.prototype = Object.create(Enum.prototype);
  Alignment.prototype.constructor = Alignment;
  Slider.prototype = Object.create(UiComponent.prototype);
  Slider.prototype.constructor = Slider;
  TextField.prototype = Object.create(Label.prototype);
  TextField.prototype.constructor = TextField;
  TextFieldUi.prototype = Object.create(LabelUi.prototype);
  TextFieldUi.prototype.constructor = TextFieldUi;
  ToggleButton.prototype = Object.create(Button.prototype);
  ToggleButton.prototype.constructor = ToggleButton;
  ToggleButtonUi.prototype = Object.create(ButtonUi.prototype);
  ToggleButtonUi.prototype.constructor = ToggleButtonUi;
  UiContainer.prototype = Object.create(UiComponent.prototype);
  UiContainer.prototype.constructor = UiContainer;
  UiRoot.prototype = Object.create(Node.prototype);
  UiRoot.prototype.constructor = UiRoot;
  ThemeBuilder.prototype = Object.create(UiTheme.prototype);
  ThemeBuilder.prototype.constructor = ThemeBuilder;
  AttributeType.prototype = Object.create(Enum.prototype);
  AttributeType.prototype.constructor = AttributeType;
  Shader.prototype = Object.create(GlObject.prototype);
  Shader.prototype.constructor = Shader;
  BasicShader.prototype = Object.create(Shader.prototype);
  BasicShader.prototype.constructor = BasicShader;
  BasicPointShader.prototype = Object.create(BasicShader.prototype);
  BasicPointShader.prototype.constructor = BasicPointShader;
  BlurShader.prototype = Object.create(BasicShader.prototype);
  BlurShader.prototype.constructor = BlurShader;
  BlurredBackgroundHelper$BlurMethod.prototype = Object.create(Enum.prototype);
  BlurredBackgroundHelper$BlurMethod.prototype.constructor = BlurredBackgroundHelper$BlurMethod;
  BlurredBackgroundHelper$BlurredBgTextureData.prototype = Object.create(TextureData.prototype);
  BlurredBackgroundHelper$BlurredBgTextureData.prototype.constructor = BlurredBackgroundHelper$BlurredBgTextureData;
  BlurredBackgroundHelper$BlurQuadShader.prototype = Object.create(Shader.prototype);
  BlurredBackgroundHelper$BlurQuadShader.prototype.constructor = BlurredBackgroundHelper$BlurQuadShader;
  LightModel.prototype = Object.create(Enum.prototype);
  LightModel.prototype.constructor = LightModel;
  ColorModel.prototype = Object.create(Enum.prototype);
  ColorModel.prototype.constructor = ColorModel;
  FogModel.prototype = Object.create(Enum.prototype);
  FogModel.prototype.constructor = FogModel;
  PreferredLightModel.prototype = Object.create(Enum.prototype);
  PreferredLightModel.prototype.constructor = PreferredLightModel;
  PreferredShadowMethod.prototype = Object.create(Enum.prototype);
  PreferredShadowMethod.prototype.constructor = PreferredShadowMethod;
  UniformTexture2D.prototype = Object.create(Uniform.prototype);
  UniformTexture2D.prototype.constructor = UniformTexture2D;
  UniformTexture2Dv.prototype = Object.create(Uniform.prototype);
  UniformTexture2Dv.prototype.constructor = UniformTexture2Dv;
  Uniform1i.prototype = Object.create(Uniform.prototype);
  Uniform1i.prototype.constructor = Uniform1i;
  Uniform1iv.prototype = Object.create(Uniform.prototype);
  Uniform1iv.prototype.constructor = Uniform1iv;
  Uniform1f.prototype = Object.create(Uniform.prototype);
  Uniform1f.prototype.constructor = Uniform1f;
  Uniform1fv.prototype = Object.create(Uniform.prototype);
  Uniform1fv.prototype.constructor = Uniform1fv;
  Uniform2f.prototype = Object.create(Uniform.prototype);
  Uniform2f.prototype.constructor = Uniform2f;
  Uniform3f.prototype = Object.create(Uniform.prototype);
  Uniform3f.prototype.constructor = Uniform3f;
  Uniform4f.prototype = Object.create(Uniform.prototype);
  Uniform4f.prototype.constructor = Uniform4f;
  UniformMatrix4.prototype = Object.create(Uniform.prototype);
  UniformMatrix4.prototype.constructor = UniformMatrix4;
  LinearAnimator.prototype = Object.create(Animator.prototype);
  LinearAnimator.prototype.constructor = LinearAnimator;
  CosAnimator.prototype = Object.create(Animator.prototype);
  CosAnimator.prototype.constructor = CosAnimator;
  InterpolatedFloat.prototype = Object.create(InterpolatedValue.prototype);
  InterpolatedFloat.prototype.constructor = InterpolatedFloat;
  InterpolatedColor.prototype = Object.create(InterpolatedValue.prototype);
  InterpolatedColor.prototype.constructor = InterpolatedColor;
  BillboardMesh.prototype = Object.create(Mesh.prototype);
  BillboardMesh.prototype.constructor = BillboardMesh;
  BillboardShader.prototype = Object.create(BasicShader.prototype);
  BillboardShader.prototype.constructor = BillboardShader;
  Color.prototype = Object.create(Vec4f.prototype);
  Color.prototype.constructor = Color;
  MutableColor.prototype = Object.create(Color.prototype);
  MutableColor.prototype.constructor = MutableColor;
  DeltaTGraph.prototype = Object.create(UiComponent.prototype);
  DeltaTGraph.prototype.constructor = DeltaTGraph;
  Font.prototype = Object.create(Texture.prototype);
  Font.prototype.constructor = Font;
  IndexedVertexList$Vertex$Vec2fView.prototype = Object.create(MutableVec2f.prototype);
  IndexedVertexList$Vertex$Vec2fView.prototype.constructor = IndexedVertexList$Vertex$Vec2fView;
  IndexedVertexList$Vertex$Vec3fView.prototype = Object.create(MutableVec3f.prototype);
  IndexedVertexList$Vertex$Vec3fView.prototype.constructor = IndexedVertexList$Vertex$Vec3fView;
  IndexedVertexList$Vertex$Vec4fView.prototype = Object.create(MutableVec4f.prototype);
  IndexedVertexList$Vertex$Vec4fView.prototype.constructor = IndexedVertexList$Vertex$Vec4fView;
  IndexedVertexList$Vertex$ColorView.prototype = Object.create(MutableColor.prototype);
  IndexedVertexList$Vertex$ColorView.prototype.constructor = IndexedVertexList$Vertex$ColorView;
  LineMesh.prototype = Object.create(Mesh.prototype);
  LineMesh.prototype.constructor = LineMesh;
  PointMesh.prototype = Object.create(Mesh.prototype);
  PointMesh.prototype.constructor = PointMesh;
  ImageTextureData.prototype = Object.create(TextureData.prototype);
  ImageTextureData.prototype.constructor = ImageTextureData;
  JsContext$InitProps.prototype = Object.create(RenderContext$InitProps.prototype);
  JsContext$InitProps.prototype.constructor = JsContext$InitProps;
  JsContext.prototype = Object.create(RenderContext.prototype);
  JsContext.prototype.constructor = JsContext;
  Uint8BufferImpl.prototype = Object.create(GenericBuffer.prototype);
  Uint8BufferImpl.prototype.constructor = Uint8BufferImpl;
  Uint16BufferImpl.prototype = Object.create(GenericBuffer.prototype);
  Uint16BufferImpl.prototype.constructor = Uint16BufferImpl;
  Uint32BufferImpl.prototype = Object.create(GenericBuffer.prototype);
  Uint32BufferImpl.prototype.constructor = Uint32BufferImpl;
  Float32BufferImpl.prototype = Object.create(GenericBuffer.prototype);
  Float32BufferImpl.prototype.constructor = Float32BufferImpl;
  function GlCapabilities(uint32Indices, shaderIntAttribs, depthTextures, depthComponentIntFormat, depthFilterMethod, framebufferWithoutColor, anisotropicTexFilterInfo, glslDialect, glVersion) {
    GlCapabilities$Companion_getInstance();
    this.uint32Indices = uint32Indices;
    this.shaderIntAttribs = shaderIntAttribs;
    this.depthTextures = depthTextures;
    this.depthComponentIntFormat = depthComponentIntFormat;
    this.depthFilterMethod = depthFilterMethod;
    this.framebufferWithoutColor = framebufferWithoutColor;
    this.anisotropicTexFilterInfo = anisotropicTexFilterInfo;
    this.glslDialect = glslDialect;
    this.glVersion = glVersion;
  }
  function GlCapabilities$Companion() {
    GlCapabilities$Companion_instance = this;
    this.UNKNOWN_CAPABILITIES = new GlCapabilities(false, false, false, 0, 0, false, AnisotropicTexFilterInfo$Companion_getInstance().NOT_SUPPORTED, GlslDialect$Companion_getInstance().GLSL_DIALECT_100, new GlVersion('Unknown', 0, 0));
  }
  GlCapabilities$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var GlCapabilities$Companion_instance = null;
  function GlCapabilities$Companion_getInstance() {
    if (GlCapabilities$Companion_instance === null) {
      new GlCapabilities$Companion();
    }
    return GlCapabilities$Companion_instance;
  }
  GlCapabilities.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlCapabilities',
    interfaces: []
  };
  GlCapabilities.prototype.component1 = function () {
    return this.uint32Indices;
  };
  GlCapabilities.prototype.component2 = function () {
    return this.shaderIntAttribs;
  };
  GlCapabilities.prototype.component3 = function () {
    return this.depthTextures;
  };
  GlCapabilities.prototype.component4 = function () {
    return this.depthComponentIntFormat;
  };
  GlCapabilities.prototype.component5 = function () {
    return this.depthFilterMethod;
  };
  GlCapabilities.prototype.component6 = function () {
    return this.framebufferWithoutColor;
  };
  GlCapabilities.prototype.component7 = function () {
    return this.anisotropicTexFilterInfo;
  };
  GlCapabilities.prototype.component8 = function () {
    return this.glslDialect;
  };
  GlCapabilities.prototype.component9 = function () {
    return this.glVersion;
  };
  GlCapabilities.prototype.copy_x5djhs$ = function (uint32Indices, shaderIntAttribs, depthTextures, depthComponentIntFormat, depthFilterMethod, framebufferWithoutColor, anisotropicTexFilterInfo, glslDialect, glVersion) {
    return new GlCapabilities(uint32Indices === void 0 ? this.uint32Indices : uint32Indices, shaderIntAttribs === void 0 ? this.shaderIntAttribs : shaderIntAttribs, depthTextures === void 0 ? this.depthTextures : depthTextures, depthComponentIntFormat === void 0 ? this.depthComponentIntFormat : depthComponentIntFormat, depthFilterMethod === void 0 ? this.depthFilterMethod : depthFilterMethod, framebufferWithoutColor === void 0 ? this.framebufferWithoutColor : framebufferWithoutColor, anisotropicTexFilterInfo === void 0 ? this.anisotropicTexFilterInfo : anisotropicTexFilterInfo, glslDialect === void 0 ? this.glslDialect : glslDialect, glVersion === void 0 ? this.glVersion : glVersion);
  };
  GlCapabilities.prototype.toString = function () {
    return 'GlCapabilities(uint32Indices=' + Kotlin.toString(this.uint32Indices) + (', shaderIntAttribs=' + Kotlin.toString(this.shaderIntAttribs)) + (', depthTextures=' + Kotlin.toString(this.depthTextures)) + (', depthComponentIntFormat=' + Kotlin.toString(this.depthComponentIntFormat)) + (', depthFilterMethod=' + Kotlin.toString(this.depthFilterMethod)) + (', framebufferWithoutColor=' + Kotlin.toString(this.framebufferWithoutColor)) + (', anisotropicTexFilterInfo=' + Kotlin.toString(this.anisotropicTexFilterInfo)) + (', glslDialect=' + Kotlin.toString(this.glslDialect)) + (', glVersion=' + Kotlin.toString(this.glVersion)) + ')';
  };
  GlCapabilities.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.uint32Indices) | 0;
    result = result * 31 + Kotlin.hashCode(this.shaderIntAttribs) | 0;
    result = result * 31 + Kotlin.hashCode(this.depthTextures) | 0;
    result = result * 31 + Kotlin.hashCode(this.depthComponentIntFormat) | 0;
    result = result * 31 + Kotlin.hashCode(this.depthFilterMethod) | 0;
    result = result * 31 + Kotlin.hashCode(this.framebufferWithoutColor) | 0;
    result = result * 31 + Kotlin.hashCode(this.anisotropicTexFilterInfo) | 0;
    result = result * 31 + Kotlin.hashCode(this.glslDialect) | 0;
    result = result * 31 + Kotlin.hashCode(this.glVersion) | 0;
    return result;
  };
  GlCapabilities.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.uint32Indices, other.uint32Indices) && Kotlin.equals(this.shaderIntAttribs, other.shaderIntAttribs) && Kotlin.equals(this.depthTextures, other.depthTextures) && Kotlin.equals(this.depthComponentIntFormat, other.depthComponentIntFormat) && Kotlin.equals(this.depthFilterMethod, other.depthFilterMethod) && Kotlin.equals(this.framebufferWithoutColor, other.framebufferWithoutColor) && Kotlin.equals(this.anisotropicTexFilterInfo, other.anisotropicTexFilterInfo) && Kotlin.equals(this.glslDialect, other.glslDialect) && Kotlin.equals(this.glVersion, other.glVersion)))));
  };
  function GlVersion(glDialect, versionMajor, versionMinor) {
    this.glDialect = glDialect;
    this.versionMajor = versionMajor;
    this.versionMinor = versionMinor;
  }
  GlVersion.prototype.toString = function () {
    return this.glDialect + ' ' + this.versionMajor + '.' + this.versionMinor;
  };
  GlVersion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlVersion',
    interfaces: []
  };
  GlVersion.prototype.component1 = function () {
    return this.glDialect;
  };
  GlVersion.prototype.component2 = function () {
    return this.versionMajor;
  };
  GlVersion.prototype.component3 = function () {
    return this.versionMinor;
  };
  GlVersion.prototype.copy_3m52m6$ = function (glDialect, versionMajor, versionMinor) {
    return new GlVersion(glDialect === void 0 ? this.glDialect : glDialect, versionMajor === void 0 ? this.versionMajor : versionMajor, versionMinor === void 0 ? this.versionMinor : versionMinor);
  };
  GlVersion.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.glDialect) | 0;
    result = result * 31 + Kotlin.hashCode(this.versionMajor) | 0;
    result = result * 31 + Kotlin.hashCode(this.versionMinor) | 0;
    return result;
  };
  GlVersion.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.glDialect, other.glDialect) && Kotlin.equals(this.versionMajor, other.versionMajor) && Kotlin.equals(this.versionMinor, other.versionMinor)))));
  };
  function AnisotropicTexFilterInfo(maxAnisotropy, TEXTURE_MAX_ANISOTROPY_EXT) {
    AnisotropicTexFilterInfo$Companion_getInstance();
    this.maxAnisotropy = maxAnisotropy;
    this.TEXTURE_MAX_ANISOTROPY_EXT = TEXTURE_MAX_ANISOTROPY_EXT;
  }
  Object.defineProperty(AnisotropicTexFilterInfo.prototype, 'isSupported', {
    get: function () {
      return this.TEXTURE_MAX_ANISOTROPY_EXT !== 0;
    }
  });
  function AnisotropicTexFilterInfo$Companion() {
    AnisotropicTexFilterInfo$Companion_instance = this;
    this.NOT_SUPPORTED = new AnisotropicTexFilterInfo(0.0, 0);
  }
  AnisotropicTexFilterInfo$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var AnisotropicTexFilterInfo$Companion_instance = null;
  function AnisotropicTexFilterInfo$Companion_getInstance() {
    if (AnisotropicTexFilterInfo$Companion_instance === null) {
      new AnisotropicTexFilterInfo$Companion();
    }
    return AnisotropicTexFilterInfo$Companion_instance;
  }
  AnisotropicTexFilterInfo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AnisotropicTexFilterInfo',
    interfaces: []
  };
  AnisotropicTexFilterInfo.prototype.component1 = function () {
    return this.maxAnisotropy;
  };
  AnisotropicTexFilterInfo.prototype.component2 = function () {
    return this.TEXTURE_MAX_ANISOTROPY_EXT;
  };
  AnisotropicTexFilterInfo.prototype.copy_vjorfl$ = function (maxAnisotropy, TEXTURE_MAX_ANISOTROPY_EXT) {
    return new AnisotropicTexFilterInfo(maxAnisotropy === void 0 ? this.maxAnisotropy : maxAnisotropy, TEXTURE_MAX_ANISOTROPY_EXT === void 0 ? this.TEXTURE_MAX_ANISOTROPY_EXT : TEXTURE_MAX_ANISOTROPY_EXT);
  };
  AnisotropicTexFilterInfo.prototype.toString = function () {
    return 'AnisotropicTexFilterInfo(maxAnisotropy=' + Kotlin.toString(this.maxAnisotropy) + (', TEXTURE_MAX_ANISOTROPY_EXT=' + Kotlin.toString(this.TEXTURE_MAX_ANISOTROPY_EXT)) + ')';
  };
  AnisotropicTexFilterInfo.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.maxAnisotropy) | 0;
    result = result * 31 + Kotlin.hashCode(this.TEXTURE_MAX_ANISOTROPY_EXT) | 0;
    return result;
  };
  AnisotropicTexFilterInfo.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.maxAnisotropy, other.maxAnisotropy) && Kotlin.equals(this.TEXTURE_MAX_ANISOTROPY_EXT, other.TEXTURE_MAX_ANISOTROPY_EXT)))));
  };
  function GlslDialect(version, vsIn, vsOut, fsIn, fragColorHead, fragColorBody, texSampler) {
    GlslDialect$Companion_getInstance();
    this.version = version;
    this.vsIn = vsIn;
    this.vsOut = vsOut;
    this.fsIn = fsIn;
    this.fragColorHead = fragColorHead;
    this.fragColorBody = fragColorBody;
    this.texSampler = texSampler;
  }
  function GlslDialect$Companion() {
    GlslDialect$Companion_instance = this;
    this.GLSL_DIALECT_100 = new GlslDialect('#version 100', 'attribute', 'varying', 'varying', '', 'gl_FragColor', 'texture2D');
    this.GLSL_DIALECT_330 = new GlslDialect('#version 330', 'in', 'out', 'in', 'out vec4 fragColor;', 'fragColor', 'texture');
    this.GLSL_DIALECT_300_ES = new GlslDialect('#version 300 es', 'in', 'out', 'in', 'out vec4 fragColor;', 'fragColor', 'texture');
  }
  GlslDialect$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var GlslDialect$Companion_instance = null;
  function GlslDialect$Companion_getInstance() {
    if (GlslDialect$Companion_instance === null) {
      new GlslDialect$Companion();
    }
    return GlslDialect$Companion_instance;
  }
  GlslDialect.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslDialect',
    interfaces: []
  };
  GlslDialect.prototype.component1 = function () {
    return this.version;
  };
  GlslDialect.prototype.component2 = function () {
    return this.vsIn;
  };
  GlslDialect.prototype.component3 = function () {
    return this.vsOut;
  };
  GlslDialect.prototype.component4 = function () {
    return this.fsIn;
  };
  GlslDialect.prototype.component5 = function () {
    return this.fragColorHead;
  };
  GlslDialect.prototype.component6 = function () {
    return this.fragColorBody;
  };
  GlslDialect.prototype.component7 = function () {
    return this.texSampler;
  };
  GlslDialect.prototype.copy_blz5pm$ = function (version, vsIn, vsOut, fsIn, fragColorHead, fragColorBody, texSampler) {
    return new GlslDialect(version === void 0 ? this.version : version, vsIn === void 0 ? this.vsIn : vsIn, vsOut === void 0 ? this.vsOut : vsOut, fsIn === void 0 ? this.fsIn : fsIn, fragColorHead === void 0 ? this.fragColorHead : fragColorHead, fragColorBody === void 0 ? this.fragColorBody : fragColorBody, texSampler === void 0 ? this.texSampler : texSampler);
  };
  GlslDialect.prototype.toString = function () {
    return 'GlslDialect(version=' + Kotlin.toString(this.version) + (', vsIn=' + Kotlin.toString(this.vsIn)) + (', vsOut=' + Kotlin.toString(this.vsOut)) + (', fsIn=' + Kotlin.toString(this.fsIn)) + (', fragColorHead=' + Kotlin.toString(this.fragColorHead)) + (', fragColorBody=' + Kotlin.toString(this.fragColorBody)) + (', texSampler=' + Kotlin.toString(this.texSampler)) + ')';
  };
  GlslDialect.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.version) | 0;
    result = result * 31 + Kotlin.hashCode(this.vsIn) | 0;
    result = result * 31 + Kotlin.hashCode(this.vsOut) | 0;
    result = result * 31 + Kotlin.hashCode(this.fsIn) | 0;
    result = result * 31 + Kotlin.hashCode(this.fragColorHead) | 0;
    result = result * 31 + Kotlin.hashCode(this.fragColorBody) | 0;
    result = result * 31 + Kotlin.hashCode(this.texSampler) | 0;
    return result;
  };
  GlslDialect.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.version, other.version) && Kotlin.equals(this.vsIn, other.vsIn) && Kotlin.equals(this.vsOut, other.vsOut) && Kotlin.equals(this.fsIn, other.fsIn) && Kotlin.equals(this.fragColorHead, other.fragColorHead) && Kotlin.equals(this.fragColorBody, other.fragColorBody) && Kotlin.equals(this.texSampler, other.texSampler)))));
  };
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var Array_0 = Array;
  function InputManager() {
    InputManager$Companion_getInstance();
    this.queuedKeyEvents_0 = ArrayList_init();
    this.keyEvents = ArrayList_init();
    var array = Array_0(InputManager$Companion_getInstance().MAX_POINTERS);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = new InputManager$Pointer(i);
    }
    this.tmpPointers_0 = array;
    var array_0 = Array_0(InputManager$Companion_getInstance().MAX_POINTERS);
    var tmp$_0;
    tmp$_0 = array_0.length - 1 | 0;
    for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
      array_0[i_0] = new InputManager$Pointer(i_0);
    }
    this.pointers = array_0;
    this.primaryPointer = this.pointers[InputManager$Companion_getInstance().PRIMARY_POINTER];
  }
  function InputManager$DragHandler() {
    InputManager$DragHandler$Companion_getInstance();
  }
  function InputManager$DragHandler$Companion() {
    InputManager$DragHandler$Companion_instance = this;
    this.HANDLED = 1;
    this.REMOVE_HANDLER = 2;
  }
  InputManager$DragHandler$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var InputManager$DragHandler$Companion_instance = null;
  function InputManager$DragHandler$Companion_getInstance() {
    if (InputManager$DragHandler$Companion_instance === null) {
      new InputManager$DragHandler$Companion();
    }
    return InputManager$DragHandler$Companion_instance;
  }
  InputManager$DragHandler.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DragHandler',
    interfaces: []
  };
  InputManager.prototype.onNewFrame_8be2vx$ = function () {
    var tmp$;
    tmp$ = this.pointers;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.pointers[i].updateFrom_cftk8y$(this.tmpPointers_0[i]);
      this.tmpPointers_0[i].buttonEventMask = 0;
    }
    this.keyEvents.clear();
    this.keyEvents.addAll_brywnq$(this.queuedKeyEvents_0);
    this.queuedKeyEvents_0.clear();
  };
  InputManager.prototype.keyEvent_qt1dr2$ = function (keyCode, modifiers, event) {
    var ev = new InputManager$KeyEvent();
    ev.keyCode = keyCode;
    ev.event = event;
    ev.modifiers = modifiers;
    this.queuedKeyEvents_0.add_11rb$(ev);
  };
  InputManager.prototype.charTyped_s8itvh$ = function (typedChar) {
    var ev = new InputManager$KeyEvent();
    ev.event = InputManager$Companion_getInstance().KEY_EV_CHAR_TYPED;
    ev.typedChar = toBoxedChar(typedChar);
    this.queuedKeyEvents_0.add_11rb$(ev);
  };
  InputManager.prototype.updatePointerPos_nhq4am$ = function (pointer, x, y) {
    var tmp$;
    tmp$ = InputManager$Companion_getInstance().MAX_POINTERS - 1 | 0;
    if (0 <= pointer && pointer <= tmp$) {
      var ptr = this.tmpPointers_0[pointer];
      ptr.isValid = true;
      ptr.x = x;
      ptr.y = y;
    }
  };
  InputManager.prototype.updatePointerButtonState_ydzd23$ = function (pointer, button, down) {
    var tmp$;
    tmp$ = InputManager$Companion_getInstance().MAX_POINTERS - 1 | 0;
    if (0 <= pointer && pointer <= tmp$) {
      var ptr = this.tmpPointers_0[pointer];
      ptr.isValid = true;
      if (down) {
        ptr.buttonMask = ptr.buttonMask | 1 << button;
      }
       else {
        ptr.buttonMask = ptr.buttonMask & ~(1 << button);
      }
    }
  };
  InputManager.prototype.updatePointerButtonStates_vux9f0$ = function (pointer, mask) {
    var tmp$;
    tmp$ = InputManager$Companion_getInstance().MAX_POINTERS - 1 | 0;
    if (0 <= pointer && pointer <= tmp$) {
      var ptr = this.tmpPointers_0[pointer];
      ptr.isValid = true;
      ptr.buttonMask = mask;
    }
  };
  InputManager.prototype.updatePointerScrollPos_24o109$ = function (pointer, ticks) {
    var tmp$;
    tmp$ = InputManager$Companion_getInstance().MAX_POINTERS - 1 | 0;
    if (0 <= pointer && pointer <= tmp$) {
      var ptr = this.tmpPointers_0[pointer];
      ptr.isValid = true;
      ptr.scrollPos = ptr.scrollPos + ticks;
    }
  };
  InputManager.prototype.updatePointerValid_fzusl$ = function (pointer, valid) {
    var tmp$;
    tmp$ = InputManager$Companion_getInstance().MAX_POINTERS - 1 | 0;
    if (0 <= pointer && pointer <= tmp$) {
      this.tmpPointers_0[pointer].isValid = valid;
    }
  };
  function InputManager$Pointer(id) {
    this.id = id;
    this.x_5rhhjp$_0 = 0.0;
    this.y_5rhhkk$_0 = 0.0;
    this.scrollPos_8f8kwq$_0 = 0.0;
    this.deltaX = 0.0;
    this.deltaY = 0.0;
    this.deltaScroll = 0.0;
    this.buttonMask_uq1vpl$_0 = 0;
    this.buttonEventMask_dc8ngn$_0 = 0;
    this.wasValid_ugj0rw$_0 = false;
    this.isValid_cde91$_0 = false;
  }
  Object.defineProperty(InputManager$Pointer.prototype, 'x', {
    get: function () {
      return this.x_5rhhjp$_0;
    },
    set: function (x) {
      this.x_5rhhjp$_0 = x;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'y', {
    get: function () {
      return this.y_5rhhkk$_0;
    },
    set: function (y) {
      this.y_5rhhkk$_0 = y;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'scrollPos', {
    get: function () {
      return this.scrollPos_8f8kwq$_0;
    },
    set: function (scrollPos) {
      this.scrollPos_8f8kwq$_0 = scrollPos;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'buttonMask', {
    get: function () {
      return this.buttonMask_uq1vpl$_0;
    },
    set: function (value) {
      this.buttonEventMask = this.buttonEventMask | this.buttonMask_uq1vpl$_0 ^ value;
      this.buttonMask_uq1vpl$_0 = value;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'buttonEventMask', {
    get: function () {
      return this.buttonEventMask_dc8ngn$_0;
    },
    set: function (buttonEventMask) {
      this.buttonEventMask_dc8ngn$_0 = buttonEventMask;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'wasValid', {
    get: function () {
      return this.wasValid_ugj0rw$_0;
    },
    set: function (wasValid) {
      this.wasValid_ugj0rw$_0 = wasValid;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isValid', {
    get: function () {
      return this.isValid_cde91$_0;
    },
    set: function (isValid) {
      this.isValid_cde91$_0 = isValid;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isLeftButtonDown', {
    get: function () {
      return (this.buttonMask & InputManager$Companion_getInstance().LEFT_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isRightButtonDown', {
    get: function () {
      return (this.buttonMask & InputManager$Companion_getInstance().RIGHT_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isMiddleButtonDown', {
    get: function () {
      return (this.buttonMask & InputManager$Companion_getInstance().MIDDLE_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isBackButtonDown', {
    get: function () {
      return (this.buttonMask & InputManager$Companion_getInstance().BACK_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isForwardButtonDown', {
    get: function () {
      return (this.buttonMask & InputManager$Companion_getInstance().FORWARD_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isLeftButtonEvent', {
    get: function () {
      return (this.buttonEventMask & InputManager$Companion_getInstance().LEFT_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isRightButtonEvent', {
    get: function () {
      return (this.buttonEventMask & InputManager$Companion_getInstance().RIGHT_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isMiddleButtonEvent', {
    get: function () {
      return (this.buttonEventMask & InputManager$Companion_getInstance().MIDDLE_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isBackButtonEvent', {
    get: function () {
      return (this.buttonEventMask & InputManager$Companion_getInstance().BACK_BUTTON_MASK) !== 0;
    }
  });
  Object.defineProperty(InputManager$Pointer.prototype, 'isForwardButtonEvent', {
    get: function () {
      return (this.buttonEventMask & InputManager$Companion_getInstance().FORWARD_BUTTON_MASK) !== 0;
    }
  });
  InputManager$Pointer.prototype.updateFrom_cftk8y$ = function (ptr) {
    this.deltaX = ptr.x - this.x;
    this.deltaY = ptr.y - this.y;
    this.deltaScroll = ptr.scrollPos - this.scrollPos;
    this.x = ptr.x;
    this.y = ptr.y;
    this.scrollPos = ptr.scrollPos;
    this.buttonMask = ptr.buttonMask;
    this.buttonEventMask = ptr.buttonEventMask;
    this.wasValid = this.isValid;
    this.isValid = ptr.isValid;
  };
  InputManager$Pointer.prototype.isInViewport_evfofk$ = function (ctx) {
    var ptrY = ctx.windowHeight - this.y;
    return (this.isValid || this.wasValid) && ctx.viewport.isInViewport_dleff0$(this.x, ptrY);
  };
  InputManager$Pointer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Pointer',
    interfaces: []
  };
  function InputManager$KeyEvent() {
    this.keyCode_56t57j$_0 = 0;
    this.modifiers_cmws0f$_0 = 0;
    this.event_4ycnw1$_0 = 0;
    this.typedChar_ne8il1$_0 = toChar(0);
  }
  Object.defineProperty(InputManager$KeyEvent.prototype, 'keyCode', {
    get: function () {
      return this.keyCode_56t57j$_0;
    },
    set: function (keyCode) {
      this.keyCode_56t57j$_0 = keyCode;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'modifiers', {
    get: function () {
      return this.modifiers_cmws0f$_0;
    },
    set: function (modifiers) {
      this.modifiers_cmws0f$_0 = modifiers;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'event', {
    get: function () {
      return this.event_4ycnw1$_0;
    },
    set: function (event) {
      this.event_4ycnw1$_0 = event;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'typedChar', {
    get: function () {
      return toBoxedChar(this.typedChar_ne8il1$_0);
    },
    set: function (typedChar) {
      this.typedChar_ne8il1$_0 = unboxChar(typedChar);
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isPressed', {
    get: function () {
      return (this.event & InputManager$Companion_getInstance().KEY_EV_DOWN) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isRepeated', {
    get: function () {
      return (this.event & InputManager$Companion_getInstance().KEY_EV_REPEATED) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isReleased', {
    get: function () {
      return (this.event & InputManager$Companion_getInstance().KEY_EV_UP) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isCharTyped', {
    get: function () {
      return (this.event & InputManager$Companion_getInstance().KEY_EV_CHAR_TYPED) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isShiftDown', {
    get: function () {
      return (this.modifiers & InputManager$Companion_getInstance().KEY_MOD_SHIFT) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isCtrlDown', {
    get: function () {
      return (this.modifiers & InputManager$Companion_getInstance().KEY_MOD_CTRL) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isAltDown', {
    get: function () {
      return (this.modifiers & InputManager$Companion_getInstance().KEY_MOD_ALT) !== 0;
    }
  });
  Object.defineProperty(InputManager$KeyEvent.prototype, 'isSuperDown', {
    get: function () {
      return (this.modifiers & InputManager$Companion_getInstance().KEY_MOD_SUPER) !== 0;
    }
  });
  InputManager$KeyEvent.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KeyEvent',
    interfaces: []
  };
  function InputManager$Companion() {
    InputManager$Companion_instance = this;
    this.LEFT_BUTTON = 0;
    this.LEFT_BUTTON_MASK = 1;
    this.RIGHT_BUTTON = 1;
    this.RIGHT_BUTTON_MASK = 2;
    this.MIDDLE_BUTTON = 2;
    this.MIDDLE_BUTTON_MASK = 4;
    this.BACK_BUTTON = 3;
    this.BACK_BUTTON_MASK = 8;
    this.FORWARD_BUTTON = 4;
    this.FORWARD_BUTTON_MASK = 16;
    this.MAX_POINTERS = 10;
    this.PRIMARY_POINTER = 0;
    this.KEY_EV_UP = 1;
    this.KEY_EV_DOWN = 2;
    this.KEY_EV_REPEATED = 4;
    this.KEY_EV_CHAR_TYPED = 8;
    this.KEY_MOD_SHIFT = 1;
    this.KEY_MOD_CTRL = 2;
    this.KEY_MOD_ALT = 4;
    this.KEY_MOD_SUPER = 8;
    this.KEY_CTRL_LEFT = -1;
    this.KEY_CTRL_RIGHT = -2;
    this.KEY_SHIFT_LEFT = -3;
    this.KEY_SHIFT_RIGHT = -4;
    this.KEY_ALT_LEFT = -5;
    this.KEY_ALT_RIGHT = -6;
    this.KEY_SUPER_LEFT = -7;
    this.KEY_SUPER_RIGHT = -8;
    this.KEY_ESC = -9;
    this.KEY_MENU = -10;
    this.KEY_ENTER = -11;
    this.KEY_NP_ENTER = -12;
    this.KEY_NP_DIV = -13;
    this.KEY_NP_MUL = -14;
    this.KEY_NP_PLUS = -15;
    this.KEY_NP_MINUS = -16;
    this.KEY_BACKSPACE = -17;
    this.KEY_TAB = -18;
    this.KEY_DEL = -19;
    this.KEY_INSERT = -20;
    this.KEY_HOME = -21;
    this.KEY_END = -22;
    this.KEY_PAGE_UP = -23;
    this.KEY_PAGE_DOWN = -24;
    this.KEY_CURSOR_LEFT = -25;
    this.KEY_CURSOR_RIGHT = -26;
    this.KEY_CURSOR_UP = -27;
    this.KEY_CURSOR_DOWN = -28;
    this.KEY_F1 = -29;
    this.KEY_F2 = -30;
    this.KEY_F3 = -31;
    this.KEY_F4 = -32;
    this.KEY_F5 = -33;
    this.KEY_F6 = -34;
    this.KEY_F7 = -35;
    this.KEY_F8 = -36;
    this.KEY_F9 = -37;
    this.KEY_F10 = -38;
    this.KEY_F11 = -39;
    this.KEY_F12 = -40;
  }
  InputManager$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var InputManager$Companion_instance = null;
  function InputManager$Companion_getInstance() {
    if (InputManager$Companion_instance === null) {
      new InputManager$Companion();
    }
    return InputManager$Companion_instance;
  }
  InputManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InputManager',
    interfaces: []
  };
  function KoolException(message) {
    Exception_init(message, this);
    this.name = 'KoolException';
  }
  KoolException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KoolException',
    interfaces: [Exception]
  };
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  function MemoryManager() {
    this.allocationMap_0 = LinkedHashMap_init();
    this.totalMem_0 = LinkedHashMap_init();
    var tmp$, tmp$_0;
    tmp$ = GlResource$Type$values();
    for (tmp$_0 = 0; tmp$_0 !== tmp$.length; ++tmp$_0) {
      var type = tmp$[tmp$_0];
      this.allocationMap_0.put_xwzc9p$(type, LinkedHashMap_init());
      this.totalMem_0.put_xwzc9p$(type, 0.0);
    }
  }
  Object.defineProperty(MemoryManager.prototype, 'numTextures', {
    get: function () {
      return ensureNotNull(this.allocationMap_0.get_11rb$(GlResource$Type$TEXTURE_getInstance())).size;
    }
  });
  Object.defineProperty(MemoryManager.prototype, 'numShaders', {
    get: function () {
      return ensureNotNull(this.allocationMap_0.get_11rb$(GlResource$Type$PROGRAM_getInstance())).size;
    }
  });
  Object.defineProperty(MemoryManager.prototype, 'numBuffers', {
    get: function () {
      return ensureNotNull(this.allocationMap_0.get_11rb$(GlResource$Type$BUFFER_getInstance())).size;
    }
  });
  MemoryManager.prototype.getTotalMemory_b1qrxn$ = function (type) {
    return ensureNotNull(this.totalMem_0.get_11rb$(type));
  };
  MemoryManager.prototype.memoryAllocated_927jj9$ = function (resource, memory) {
    var tmp$;
    var prevAlloc = (tmp$ = ensureNotNull(this.allocationMap_0.get_11rb$(resource.type)).put_xwzc9p$(resource, memory)) != null ? tmp$ : 0;
    if (prevAlloc !== memory) {
      var newTotal = ensureNotNull(this.totalMem_0.get_11rb$(resource.type)) + memory - prevAlloc;
      this.totalMem_0.put_xwzc9p$(resource.type, newTotal);
    }
  };
  MemoryManager.prototype.deleted_esgzal$ = function (resource) {
    var tmp$;
    var memory = (tmp$ = this.allocationMap_0.get_11rb$(resource.type)) != null ? tmp$.remove_11rb$(resource) : null;
    if (memory != null) {
      var newTotal = ensureNotNull(this.totalMem_0.get_11rb$(resource.type)) - memory;
      this.totalMem_0.put_xwzc9p$(resource.type, newTotal);
    }
  };
  MemoryManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MemoryManager',
    interfaces: []
  };
  function MvpState() {
    this.projMatrix = new Mat4fStack();
    this.projMatrixBuffer_tikfj5$_0 = createFloat32Buffer(16);
    this.viewMatrix = new Mat4fStack();
    this.viewMatrixBuffer_nu8w9l$_0 = createFloat32Buffer(16);
    this.modelMatrix = new Mat4fStack();
    this.modelMatrixBuffer_9b4lcl$_0 = createFloat32Buffer(16);
    this.mvpMatrix = new Mat4f();
    this.mvpMatrixBuffer_nw2k6l$_0 = createFloat32Buffer(16);
    this.tempMatrix_0 = new Mat4f();
    this.reset();
  }
  Object.defineProperty(MvpState.prototype, 'projMatrixBuffer', {
    get: function () {
      this.projMatrix.toBuffer_he122g$(this.projMatrixBuffer_tikfj5$_0);
      return this.projMatrixBuffer_tikfj5$_0;
    }
  });
  Object.defineProperty(MvpState.prototype, 'viewMatrixBuffer', {
    get: function () {
      this.viewMatrix.toBuffer_he122g$(this.viewMatrixBuffer_nu8w9l$_0);
      return this.viewMatrixBuffer_nu8w9l$_0;
    }
  });
  Object.defineProperty(MvpState.prototype, 'modelMatrixBuffer', {
    get: function () {
      this.modelMatrix.toBuffer_he122g$(this.modelMatrixBuffer_9b4lcl$_0);
      return this.modelMatrixBuffer_9b4lcl$_0;
    }
  });
  Object.defineProperty(MvpState.prototype, 'mvpMatrixBuffer', {
    get: function () {
      this.mvpMatrix.toBuffer_he122g$(this.mvpMatrixBuffer_nw2k6l$_0);
      return this.mvpMatrixBuffer_nw2k6l$_0;
    }
  });
  MvpState.prototype.reset = function () {
    this.projMatrix.reset();
    this.viewMatrix.reset();
    this.modelMatrix.reset();
    this.mvpMatrix.setIdentity();
  };
  MvpState.prototype.pushMatrices = function () {
    this.projMatrix.push();
    this.viewMatrix.push();
    this.modelMatrix.push();
  };
  MvpState.prototype.popMatrices = function () {
    this.projMatrix.pop();
    this.viewMatrix.pop();
    this.modelMatrix.pop();
  };
  MvpState.prototype.update_evfofk$ = function (ctx) {
    var tmp$;
    this.projMatrix.mul_93v2ma$(this.viewMatrix.mul_93v2ma$(this.modelMatrix, this.tempMatrix_0), this.mvpMatrix);
    (tmp$ = ctx.shaderMgr.boundShader) != null ? (tmp$.onMatrixUpdate_evfofk$(ctx), Unit) : null;
  };
  MvpState.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MvpState',
    interfaces: []
  };
  function RenderContext() {
    this.screenDpi = 96.0;
    this.inputMgr = new InputManager();
    this.memoryMgr = new MemoryManager();
    this.shaderMgr = new ShaderManager();
    this.textureMgr = new TextureManager();
    this.mvpState = new MvpState();
    this.onRender = ArrayList_init();
    this.renderPass = RenderPass$SCREEN_getInstance();
    this.time_dnkisv$_0 = 0.0;
    this.dt_mxdlcc$_0 = 0.0;
    this.frameIdx_wel5xo$_0 = 0;
    this.fps_fm2a7$_0 = 60.0;
    this.scenes = ArrayList_init();
    this.attribs_xlw4q9$_0 = new RenderContext$Attribs();
    var array = Array_0(16);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = new RenderContext$Attribs();
    }
    this.attribsStack_ei9uxv$_0 = array;
    this.attribsStackIdx_qxqem2$_0 = 0;
    var array_0 = new Float64Array(25);
    var tmp$_0;
    tmp$_0 = array_0.length - 1 | 0;
    for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
      array_0[i_0] = 0.017;
    }
    this.frameTimes_z5q5nn$_0 = array_0;
    this.viewport_2d1bbe$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('viewport');
    this.clearColor_3a9g92$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('clearColor');
    this.cullFace_dwxjrh$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('cullFace');
    this.depthFunc_iak3rl$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('depthFunc');
    this.isDepthTest_g2mlkj$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('isDepthTest');
    this.isDepthMask_g6jpvx$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('isDepthMask');
    this.isCullFace_nv0lkt$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('isCullFace');
    this.isBlend_ggv2wh$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('isBlend');
    this.lineWidth_dkrwiu$_0 = this.attribs_xlw4q9$_0.get_ytbaoo$('lineWidth');
    this.boundBuffers_8be2vx$ = LinkedHashMap_init();
  }
  Object.defineProperty(RenderContext.prototype, 'time', {
    get: function () {
      return this.time_dnkisv$_0;
    },
    set: function (time) {
      this.time_dnkisv$_0 = time;
    }
  });
  Object.defineProperty(RenderContext.prototype, 'deltaT', {
    get: function () {
      if (this.renderPass.increaseTime) {
        return this.dt_mxdlcc$_0;
      }
       else {
        return 0.0;
      }
    }
  });
  Object.defineProperty(RenderContext.prototype, 'frameIdx', {
    get: function () {
      return this.frameIdx_wel5xo$_0;
    },
    set: function (frameIdx) {
      this.frameIdx_wel5xo$_0 = frameIdx;
    }
  });
  Object.defineProperty(RenderContext.prototype, 'fps', {
    get: function () {
      return this.fps_fm2a7$_0;
    },
    set: function (fps) {
      this.fps_fm2a7$_0 = fps;
    }
  });
  var RenderContext$viewport_metadata = new PropertyMetadata('viewport');
  Object.defineProperty(RenderContext.prototype, 'viewport', {
    get: function () {
      return this.viewport_2d1bbe$_0.getValue_n5byny$(this, RenderContext$viewport_metadata);
    },
    set: function (viewport) {
      this.viewport_2d1bbe$_0.setValue_sq4zib$(this, RenderContext$viewport_metadata, viewport);
    }
  });
  var RenderContext$clearColor_metadata = new PropertyMetadata('clearColor');
  Object.defineProperty(RenderContext.prototype, 'clearColor', {
    get: function () {
      return this.clearColor_3a9g92$_0.getValue_n5byny$(this, RenderContext$clearColor_metadata);
    },
    set: function (clearColor) {
      this.clearColor_3a9g92$_0.setValue_sq4zib$(this, RenderContext$clearColor_metadata, clearColor);
    }
  });
  var RenderContext$cullFace_metadata = new PropertyMetadata('cullFace');
  Object.defineProperty(RenderContext.prototype, 'cullFace', {
    get: function () {
      return this.cullFace_dwxjrh$_0.getValue_n5byny$(this, RenderContext$cullFace_metadata);
    },
    set: function (cullFace) {
      this.cullFace_dwxjrh$_0.setValue_sq4zib$(this, RenderContext$cullFace_metadata, cullFace);
    }
  });
  var RenderContext$depthFunc_metadata = new PropertyMetadata('depthFunc');
  Object.defineProperty(RenderContext.prototype, 'depthFunc', {
    get: function () {
      return this.depthFunc_iak3rl$_0.getValue_n5byny$(this, RenderContext$depthFunc_metadata);
    },
    set: function (depthFunc) {
      this.depthFunc_iak3rl$_0.setValue_sq4zib$(this, RenderContext$depthFunc_metadata, depthFunc);
    }
  });
  var RenderContext$isDepthTest_metadata = new PropertyMetadata('isDepthTest');
  Object.defineProperty(RenderContext.prototype, 'isDepthTest', {
    get: function () {
      return this.isDepthTest_g2mlkj$_0.getValue_n5byny$(this, RenderContext$isDepthTest_metadata);
    },
    set: function (isDepthTest) {
      this.isDepthTest_g2mlkj$_0.setValue_sq4zib$(this, RenderContext$isDepthTest_metadata, isDepthTest);
    }
  });
  var RenderContext$isDepthMask_metadata = new PropertyMetadata('isDepthMask');
  Object.defineProperty(RenderContext.prototype, 'isDepthMask', {
    get: function () {
      return this.isDepthMask_g6jpvx$_0.getValue_n5byny$(this, RenderContext$isDepthMask_metadata);
    },
    set: function (isDepthMask) {
      this.isDepthMask_g6jpvx$_0.setValue_sq4zib$(this, RenderContext$isDepthMask_metadata, isDepthMask);
    }
  });
  var RenderContext$isCullFace_metadata = new PropertyMetadata('isCullFace');
  Object.defineProperty(RenderContext.prototype, 'isCullFace', {
    get: function () {
      return this.isCullFace_nv0lkt$_0.getValue_n5byny$(this, RenderContext$isCullFace_metadata);
    },
    set: function (isCullFace) {
      this.isCullFace_nv0lkt$_0.setValue_sq4zib$(this, RenderContext$isCullFace_metadata, isCullFace);
    }
  });
  var RenderContext$isBlend_metadata = new PropertyMetadata('isBlend');
  Object.defineProperty(RenderContext.prototype, 'isBlend', {
    get: function () {
      return this.isBlend_ggv2wh$_0.getValue_n5byny$(this, RenderContext$isBlend_metadata);
    },
    set: function (isBlend) {
      this.isBlend_ggv2wh$_0.setValue_sq4zib$(this, RenderContext$isBlend_metadata, isBlend);
    }
  });
  var RenderContext$lineWidth_metadata = new PropertyMetadata('lineWidth');
  Object.defineProperty(RenderContext.prototype, 'lineWidth', {
    get: function () {
      return this.lineWidth_dkrwiu$_0.getValue_n5byny$(this, RenderContext$lineWidth_metadata);
    },
    set: function (lineWidth) {
      this.lineWidth_dkrwiu$_0.setValue_sq4zib$(this, RenderContext$lineWidth_metadata, lineWidth);
    }
  });
  function RenderContext$InitProps() {
  }
  RenderContext$InitProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InitProps',
    interfaces: []
  };
  RenderContext.prototype.render_14dthe$ = function (dt) {
    var tmp$, tmp$_0, tmp$_1;
    this.dt_mxdlcc$_0 = dt;
    this.time = this.time + dt;
    this.frameIdx = this.frameIdx + 1 | 0;
    this.frameTimes_z5q5nn$_0[this.frameIdx % this.frameTimes_z5q5nn$_0.length] = dt;
    var sum = 0.0;
    tmp$ = this.frameTimes_z5q5nn$_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      sum += this.frameTimes_z5q5nn$_0[i];
    }
    this.fps = this.frameTimes_z5q5nn$_0.length / sum * 0.1 + this.fps * 0.9;
    this.inputMgr.onNewFrame_8be2vx$();
    this.shaderMgr.bindShader_wa3i41$(null, this);
    this.viewport = new RenderContext$Viewport(0, 0, this.windowWidth, this.windowHeight);
    this.applyAttributes();
    tmp$_0 = this.onRender;
    for (var i_0 = 0; i_0 !== tmp$_0.size; ++i_0) {
      this.onRender.get_za3lpa$(i_0)(this);
    }
    tmp$_1 = this.scenes;
    for (var i_1 = 0; i_1 !== tmp$_1.size; ++i_1) {
      this.scenes.get_za3lpa$(i_1).renderScene_evfofk$(this);
    }
  };
  RenderContext.prototype.pushAttributes = function () {
    var tmp$;
    this.attribsStack_ei9uxv$_0[tmp$ = this.attribsStackIdx_qxqem2$_0, this.attribsStackIdx_qxqem2$_0 = tmp$ + 1 | 0, tmp$].set_jkllyj$(this.attribs_xlw4q9$_0);
  };
  RenderContext.prototype.popAttributes = function () {
    this.attribs_xlw4q9$_0.set_jkllyj$(this.attribsStack_ei9uxv$_0[this.attribsStackIdx_qxqem2$_0 = this.attribsStackIdx_qxqem2$_0 - 1 | 0, this.attribsStackIdx_qxqem2$_0]);
    this.applyAttributes();
  };
  RenderContext.prototype.applyAttributes = function () {
    this.attribs_xlw4q9$_0.apply();
  };
  function RenderContext$Attribs() {
    this.attribs_0 = mutableListOf([new Property('viewport', new RenderContext$Viewport(0, 0, 0, 0), RenderContext$Attribs$attribs$lambda), new Property('clearColor', new Color(0.05, 0.15, 0.25, 1.0), RenderContext$Attribs$attribs$lambda_0), new Property('cullFace', GL_BACK, RenderContext$Attribs$attribs$lambda_1), new Property('depthFunc', GL_LEQUAL, RenderContext$Attribs$attribs$lambda_2), new Property('isDepthTest', true, RenderContext$Attribs$attribs$lambda_3), new Property('isDepthMask', true, RenderContext$Attribs$attribs$lambda_4), new Property('isCullFace', true, RenderContext$Attribs$attribs$lambda_5), new Property('isBlend', true, RenderContext$Attribs$attribs$lambda_6), new Property('lineWidth', 1.0, RenderContext$Attribs$attribs$lambda_7)]);
  }
  RenderContext$Attribs.prototype.get_ytbaoo$ = function (name) {
    var tmp$;
    tmp$ = this.attribs_0;
    for (var i = 0; i !== tmp$.size; ++i) {
      var tmp$_0;
      if (equals(this.attribs_0.get_za3lpa$(i).name, name)) {
        return Kotlin.isType(tmp$_0 = this.attribs_0.get_za3lpa$(i), Property) ? tmp$_0 : throwCCE();
      }
    }
    throw RuntimeException_init('Attribute not found: ' + name);
  };
  RenderContext$Attribs.prototype.apply = function () {
    var tmp$;
    tmp$ = this.attribs_0;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.attribs_0.get_za3lpa$(i).applyIfChanged();
    }
  };
  RenderContext$Attribs.prototype.set_jkllyj$ = function (other) {
    var tmp$;
    tmp$ = this.attribs_0;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.attribs_0.get_za3lpa$(i).copy_lshn67$(other.attribs_0.get_za3lpa$(i), false);
    }
  };
  function RenderContext$Attribs$attribs$lambda($receiver) {
    var dimen = $receiver.clear;
    glViewport(dimen.x, dimen.y, dimen.width, dimen.height);
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_0($receiver) {
    var color = $receiver.clear;
    glClearColor(color.r, color.g, color.b, color.a);
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_1($receiver) {
    glCullFace($receiver.clear);
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_2($receiver) {
    glDepthFunc($receiver.clear);
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_3($receiver) {
    if ($receiver.clear) {
      glEnable(GL_DEPTH_TEST);
    }
     else {
      glDisable(GL_DEPTH_TEST);
    }
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_4($receiver) {
    glDepthMask($receiver.clear);
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_5($receiver) {
    if ($receiver.clear) {
      glEnable(GL_CULL_FACE);
    }
     else {
      glDisable(GL_CULL_FACE);
    }
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_6($receiver) {
    if ($receiver.clear) {
      glEnable(GL_BLEND);
      glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }
     else {
      glDisable(GL_BLEND);
    }
    return Unit;
  }
  function RenderContext$Attribs$attribs$lambda_7($receiver) {
    glLineWidth($receiver.clear);
    return Unit;
  }
  RenderContext$Attribs.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Attribs',
    interfaces: []
  };
  function RenderContext$Viewport(x, y, width, height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  Object.defineProperty(RenderContext$Viewport.prototype, 'aspectRatio', {
    get: function () {
      return this.width / this.height;
    }
  });
  RenderContext$Viewport.prototype.isInViewport_dleff0$ = function (x, y) {
    return x >= this.x && x < (this.x + this.width | 0) && y >= this.y && y < (this.y + this.height | 0);
  };
  RenderContext$Viewport.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Viewport',
    interfaces: []
  };
  RenderContext$Viewport.prototype.component1 = function () {
    return this.x;
  };
  RenderContext$Viewport.prototype.component2 = function () {
    return this.y;
  };
  RenderContext$Viewport.prototype.component3 = function () {
    return this.width;
  };
  RenderContext$Viewport.prototype.component4 = function () {
    return this.height;
  };
  RenderContext$Viewport.prototype.copy_tjonv8$ = function (x, y, width, height) {
    return new RenderContext$Viewport(x === void 0 ? this.x : x, y === void 0 ? this.y : y, width === void 0 ? this.width : width, height === void 0 ? this.height : height);
  };
  RenderContext$Viewport.prototype.toString = function () {
    return 'Viewport(x=' + Kotlin.toString(this.x) + (', y=' + Kotlin.toString(this.y)) + (', width=' + Kotlin.toString(this.width)) + (', height=' + Kotlin.toString(this.height)) + ')';
  };
  RenderContext$Viewport.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.width) | 0;
    result = result * 31 + Kotlin.hashCode(this.height) | 0;
    return result;
  };
  RenderContext$Viewport.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.width, other.width) && Kotlin.equals(this.height, other.height)))));
  };
  RenderContext.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RenderContext',
    interfaces: []
  };
  function RenderPass(name, ordinal, increaseTime) {
    Enum.call(this);
    this.increaseTime = increaseTime;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function RenderPass_initFields() {
    RenderPass_initFields = function () {
    };
    RenderPass$SHADOW_instance = new RenderPass('SHADOW', 0, false);
    RenderPass$SCREEN_instance = new RenderPass('SCREEN', 1, true);
  }
  var RenderPass$SHADOW_instance;
  function RenderPass$SHADOW_getInstance() {
    RenderPass_initFields();
    return RenderPass$SHADOW_instance;
  }
  var RenderPass$SCREEN_instance;
  function RenderPass$SCREEN_getInstance() {
    RenderPass_initFields();
    return RenderPass$SCREEN_instance;
  }
  RenderPass.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RenderPass',
    interfaces: [Enum]
  };
  function RenderPass$values() {
    return [RenderPass$SHADOW_getInstance(), RenderPass$SCREEN_getInstance()];
  }
  RenderPass.values = RenderPass$values;
  function RenderPass$valueOf(name) {
    switch (name) {
      case 'SHADOW':
        return RenderPass$SHADOW_getInstance();
      case 'SCREEN':
        return RenderPass$SCREEN_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.RenderPass.' + name);
    }
  }
  RenderPass.valueOf_61zpoe$ = RenderPass$valueOf;
  function ShaderManager() {
    SharedResManager.call(this);
    this.boundShader_u35sjq$_0 = null;
    this.shadingHints = new ShadingHints(PreferredLightModel$PHONG_getInstance(), PreferredShadowMethod$NO_SHADOW_getInstance());
  }
  Object.defineProperty(ShaderManager.prototype, 'boundShader', {
    get: function () {
      return this.boundShader_u35sjq$_0;
    },
    set: function (boundShader) {
      this.boundShader_u35sjq$_0 = boundShader;
    }
  });
  ShaderManager.prototype.bindShader_wa3i41$ = function (shader, ctx) {
    var tmp$, tmp$_0, tmp$_1;
    if (shader != null) {
      if (!shader.isValid) {
        shader.onLoad_evfofk$(ctx);
      }
      if (!shader.isBound_evfofk$(ctx)) {
        if (!equals((tmp$ = shader.res) != null ? tmp$.glRef : null, (tmp$_1 = (tmp$_0 = this.boundShader) != null ? tmp$_0.res : null) != null ? tmp$_1.glRef : null)) {
          glUseProgram(shader.res);
        }
        this.boundShader = shader;
        shader.onBind_evfofk$(ctx);
      }
    }
     else if (this.boundShader != null) {
      glUseProgram(null);
      this.boundShader = null;
    }
  };
  ShaderManager.prototype.createShader_saxrfs$ = function (source, ctx) {
    return this.addReference_cmpczv$(source, ctx);
  };
  ShaderManager.prototype.deleteShader_v7cqlj$ = function (shader, ctx) {
    var res = shader.res;
    if (res != null) {
      this.removeReference_cmpczv$(shader.source, ctx);
    }
  };
  ShaderManager.prototype.createResource_cmpczv$ = function (key, ctx) {
    var vertShader = ShaderResource$Companion_getInstance().createVertexShader_evfofk$(ctx);
    vertShader.shaderSource_myr2gy$(key.vertexSrc, ctx);
    if (!vertShader.compile_evfofk$(ctx)) {
      var log = vertShader.getInfoLog_evfofk$(ctx);
      vertShader.delete_evfofk$(ctx);
      println(log);
      println(key.vertexSrc);
      throw new KoolException('Vertex shader compilation failed: ' + log);
    }
    var fragShader = ShaderResource$Companion_getInstance().createFragmentShader_evfofk$(ctx);
    fragShader.shaderSource_myr2gy$(key.fragmentSrc, ctx);
    if (!fragShader.compile_evfofk$(ctx)) {
      var log_0 = fragShader.getInfoLog_evfofk$(ctx);
      fragShader.delete_evfofk$(ctx);
      println(log_0);
      println(key.fragmentSrc);
      throw new KoolException('Fragment shader compilation failed: ' + log_0);
    }
    var prog = ProgramResource$Companion_getInstance().create_evfofk$(ctx);
    prog.attachShader_j05587$(vertShader, ctx);
    prog.attachShader_j05587$(fragShader, ctx);
    var success = prog.link_evfofk$(ctx);
    vertShader.delete_evfofk$(ctx);
    fragShader.delete_evfofk$(ctx);
    if (!success) {
      var log_1 = prog.getInfoLog_evfofk$(ctx);
      prog.delete_evfofk$(ctx);
      throw new KoolException('Shader linkage failed: ' + log_1);
    }
    return prog;
  };
  ShaderManager.prototype.deleteResource_x4m4qh$ = function (key, res, ctx) {
    res.delete_evfofk$(ctx);
  };
  ShaderManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderManager',
    interfaces: [SharedResManager]
  };
  function SharedResManager() {
    this.resources = LinkedHashMap_init();
  }
  SharedResManager.prototype.addReference_cmpczv$ = function (key, ctx) {
    var res = this.resources.get_11rb$(key);
    if (res == null) {
      res = new SharedResManager$SharedResource(this.createResource_cmpczv$(key, ctx));
      var $receiver = this.resources;
      var value = res;
      $receiver.put_xwzc9p$(key, value);
    }
    res.refCount = res.refCount + 1 | 0;
    return res.resource;
  };
  SharedResManager.prototype.removeReference_cmpczv$ = function (key, ctx) {
    var res = this.resources.get_11rb$(key);
    if (res != null) {
      if ((res.refCount = res.refCount - 1 | 0, res.refCount) === 0) {
        this.deleteResource_x4m4qh$(key, res.resource, ctx);
        this.resources.remove_11rb$(key);
      }
    }
  };
  function SharedResManager$SharedResource(resource) {
    this.resource = resource;
    this.refCount = 0;
  }
  SharedResManager$SharedResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SharedResource',
    interfaces: []
  };
  SharedResManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SharedResManager',
    interfaces: []
  };
  function defaultProps(id) {
    return TextureProps_init(id, GL_LINEAR, GL_CLAMP_TO_EDGE);
  }
  function TextureProps(id, minFilter, magFilter, xWrapping, yWrapping, anisotropy) {
    TextureProps$Companion_getInstance();
    this.id = id;
    this.minFilter = minFilter;
    this.magFilter = magFilter;
    this.xWrapping = xWrapping;
    this.yWrapping = yWrapping;
    this.anisotropy = anisotropy;
  }
  function TextureProps$Companion() {
    TextureProps$Companion_instance = this;
    this.DEFAULT_MIN = GL_LINEAR_MIPMAP_LINEAR;
    this.DEFAULT_MAG = GL_LINEAR;
    this.DEFAULT_X_WRAP = GL_CLAMP_TO_EDGE;
    this.DEFAULT_Y_WRAP = GL_CLAMP_TO_EDGE;
  }
  TextureProps$Companion.prototype.magFilter_0 = function (filter) {
    if (filter === GL_NEAREST)
      return GL_NEAREST;
    else
      return this.DEFAULT_MAG;
  };
  TextureProps$Companion.prototype.minFilter_0 = function (filter) {
    if (filter === GL_NEAREST)
      return GL_NEAREST;
    else
      return this.DEFAULT_MIN;
  };
  TextureProps$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TextureProps$Companion_instance = null;
  function TextureProps$Companion_getInstance() {
    if (TextureProps$Companion_instance === null) {
      new TextureProps$Companion();
    }
    return TextureProps$Companion_instance;
  }
  TextureProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextureProps',
    interfaces: []
  };
  function TextureProps_init(id, filter, wrapping, $this) {
    $this = $this || Object.create(TextureProps.prototype);
    TextureProps.call($this, id, TextureProps$Companion_getInstance().minFilter_0(filter), TextureProps$Companion_getInstance().magFilter_0(filter), wrapping, wrapping, 16);
    return $this;
  }
  function TextureProps_init_0(id, filter, wrapping, anisotropy, $this) {
    $this = $this || Object.create(TextureProps.prototype);
    TextureProps.call($this, id, TextureProps$Companion_getInstance().minFilter_0(filter), TextureProps$Companion_getInstance().magFilter_0(filter), wrapping, wrapping, anisotropy);
    return $this;
  }
  TextureProps.prototype.component1 = function () {
    return this.id;
  };
  TextureProps.prototype.component2 = function () {
    return this.minFilter;
  };
  TextureProps.prototype.component3 = function () {
    return this.magFilter;
  };
  TextureProps.prototype.component4 = function () {
    return this.xWrapping;
  };
  TextureProps.prototype.component5 = function () {
    return this.yWrapping;
  };
  TextureProps.prototype.component6 = function () {
    return this.anisotropy;
  };
  TextureProps.prototype.copy_4g72b4$ = function (id, minFilter, magFilter, xWrapping, yWrapping, anisotropy) {
    return new TextureProps(id === void 0 ? this.id : id, minFilter === void 0 ? this.minFilter : minFilter, magFilter === void 0 ? this.magFilter : magFilter, xWrapping === void 0 ? this.xWrapping : xWrapping, yWrapping === void 0 ? this.yWrapping : yWrapping, anisotropy === void 0 ? this.anisotropy : anisotropy);
  };
  TextureProps.prototype.toString = function () {
    return 'TextureProps(id=' + Kotlin.toString(this.id) + (', minFilter=' + Kotlin.toString(this.minFilter)) + (', magFilter=' + Kotlin.toString(this.magFilter)) + (', xWrapping=' + Kotlin.toString(this.xWrapping)) + (', yWrapping=' + Kotlin.toString(this.yWrapping)) + (', anisotropy=' + Kotlin.toString(this.anisotropy)) + ')';
  };
  TextureProps.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.id) | 0;
    result = result * 31 + Kotlin.hashCode(this.minFilter) | 0;
    result = result * 31 + Kotlin.hashCode(this.magFilter) | 0;
    result = result * 31 + Kotlin.hashCode(this.xWrapping) | 0;
    result = result * 31 + Kotlin.hashCode(this.yWrapping) | 0;
    result = result * 31 + Kotlin.hashCode(this.anisotropy) | 0;
    return result;
  };
  TextureProps.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.id, other.id) && Kotlin.equals(this.minFilter, other.minFilter) && Kotlin.equals(this.magFilter, other.magFilter) && Kotlin.equals(this.xWrapping, other.xWrapping) && Kotlin.equals(this.yWrapping, other.yWrapping) && Kotlin.equals(this.anisotropy, other.anisotropy)))));
  };
  function TextureData() {
    this.isAvailable_kzjj4d$_0 = false;
    this.width_de9922$_0 = 0;
    this.height_20fnnl$_0 = 0;
  }
  Object.defineProperty(TextureData.prototype, 'isAvailable', {
    get: function () {
      return this.isAvailable_kzjj4d$_0;
    },
    set: function (isAvailable) {
      this.isAvailable_kzjj4d$_0 = isAvailable;
    }
  });
  Object.defineProperty(TextureData.prototype, 'width', {
    get: function () {
      return this.width_de9922$_0;
    },
    set: function (width) {
      this.width_de9922$_0 = width;
    }
  });
  Object.defineProperty(TextureData.prototype, 'height', {
    get: function () {
      return this.height_20fnnl$_0;
    },
    set: function (height) {
      this.height_20fnnl$_0 = height;
    }
  });
  TextureData.prototype.loadData_vbwzqr$ = function (texture, ctx) {
    this.onLoad_4yp9vu$(texture, ctx);
    ensureNotNull(texture.res).isLoaded = true;
    if (texture.props.minFilter === GL_LINEAR_MIPMAP_LINEAR) {
      glGenerateMipmap(ensureNotNull(texture.res).target);
    }
  };
  TextureData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextureData',
    interfaces: []
  };
  function BufferedTextureData(buffer, width, height, format) {
    TextureData.call(this);
    this.buffer = buffer;
    this.format = format;
    this.isAvailable = true;
    this.width = width;
    this.height = height;
  }
  BufferedTextureData.prototype.onLoad_4yp9vu$ = function (texture, ctx) {
    var tmp$;
    tmp$ = texture.res;
    if (tmp$ == null) {
      throw new KoolException("Texture wasn't created");
    }
    var res = tmp$;
    var limit = this.buffer.limit;
    var pos = this.buffer.position;
    this.buffer.flip();
    glTexImage2D(res.target, 0, this.format, this.width, this.height, 0, this.format, GL_UNSIGNED_BYTE, this.buffer);
    this.buffer.limit = limit;
    this.buffer.position = pos;
    ctx.memoryMgr.memoryAllocated_927jj9$(res, this.buffer.position);
  };
  BufferedTextureData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BufferedTextureData',
    interfaces: [TextureData]
  };
  function Texture(props, generator) {
    GlObject.call(this);
    this.props = props;
    this.generator = generator;
    this.width_uigb2o$_0 = 0;
    this.height_vdup1l$_0 = 0;
  }
  Object.defineProperty(Texture.prototype, 'width', {
    get: function () {
      return this.width_uigb2o$_0;
    },
    set: function (width) {
      this.width_uigb2o$_0 = width;
    }
  });
  Object.defineProperty(Texture.prototype, 'height', {
    get: function () {
      return this.height_vdup1l$_0;
    },
    set: function (height) {
      this.height_vdup1l$_0 = height;
    }
  });
  Texture.prototype.onCreate_p81l1v$ = function (ctx) {
    this.res = ctx.textureMgr.createTexture_nmo479$(this.props, ctx);
  };
  Texture.prototype.dispose_evfofk$ = function (ctx) {
    if (this.isValid) {
      ctx.textureMgr.deleteTexture_vbwzqr$(this, ctx);
      this.res = null;
    }
  };
  Texture.prototype.loadData_3vby0t$ = function (texData, ctx) {
    if (!texData.isAvailable) {
      throw new KoolException('Texture data is not available');
    }
    this.width = texData.width;
    this.height = texData.height;
    texData.loadData_vbwzqr$(this, ctx);
  };
  Texture.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Texture',
    interfaces: [GlObject]
  };
  function assetTexture(assetPath) {
    return assetTexture_0(defaultProps(assetPath));
  }
  function assetTexture$lambda(closure$props) {
    return function ($receiver) {
      return loadTextureAsset(closure$props.id);
    };
  }
  function assetTexture_0(props) {
    return new Texture(props, assetTexture$lambda(props));
  }
  function TextureManager() {
    TextureManager$Companion_getInstance();
    SharedResManager.call(this);
    this.activeTexUnit_0 = 0;
    var array = Array_0(TextureManager$Companion_getInstance().TEXTURE_UNITS);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = null;
    }
    this.boundTextures_0 = array;
    this.loadingTextures_0 = LinkedHashMap_init();
  }
  function TextureManager$Companion() {
    TextureManager$Companion_instance = this;
    this.TEXTURE_UNITS = 32;
  }
  TextureManager$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TextureManager$Companion_instance = null;
  function TextureManager$Companion_getInstance() {
    if (TextureManager$Companion_instance === null) {
      new TextureManager$Companion();
    }
    return TextureManager$Companion_instance;
  }
  TextureManager.prototype.bindTexture_4yp9vu$ = function (texture, ctx) {
    var tmp$;
    if (!texture.isValid) {
      this.nextTexUnit_0();
      texture.onCreate_p81l1v$(ctx);
    }
    tmp$ = texture.res;
    if (tmp$ == null) {
      throw new KoolException('TextureResource is null although it was created');
    }
    var texRes = tmp$;
    if (texRes.texUnit < 0) {
      this.nextTexUnit_0();
      this.bindToActiveTexUnit_0(texture.res);
    }
    if (!texRes.isLoaded) {
      this.loadTexture_0(texture, ctx);
    }
    return texRes.texUnit;
  };
  TextureManager.prototype.createTexture_nmo479$ = function (props, ctx) {
    return this.addReference_cmpczv$(props, ctx);
  };
  TextureManager.prototype.deleteTexture_vbwzqr$ = function (texture, ctx) {
    var res = texture.res;
    if (res != null) {
      this.removeReference_cmpczv$(texture.props, ctx);
    }
  };
  TextureManager.prototype.nextTexUnit_0 = function () {
    this.activateTexUnit_0((this.activeTexUnit_0 + 1 | 0) % TextureManager$Companion_getInstance().TEXTURE_UNITS);
  };
  TextureManager.prototype.activateTexUnit_0 = function (unit) {
    this.activeTexUnit_0 = unit;
    glActiveTexture(GL_TEXTURE0 + unit | 0);
  };
  TextureManager.prototype.bindToActiveTexUnit_0 = function (texRes) {
    var tmp$;
    (tmp$ = this.boundTextures_0[this.activeTexUnit_0]) != null ? (tmp$.texUnit = -1) : null;
    glBindTexture(GL_TEXTURE_2D, texRes);
    texRes != null ? (texRes.texUnit = this.activeTexUnit_0) : null;
    this.boundTextures_0[this.activeTexUnit_0] = texRes;
  };
  TextureManager.prototype.loadTexture_0 = function (texture, ctx) {
    var tmp$;
    tmp$ = texture.res;
    if (tmp$ == null) {
      throw new KoolException("Can't load a texture that wasn't created");
    }
    var res = tmp$;
    var data = this.loadingTextures_0.get_11rb$(texture.props.id);
    if (data == null) {
      data = texture.generator(texture);
      var $receiver = this.loadingTextures_0;
      var key = texture.props.id;
      var value = data;
      $receiver.put_xwzc9p$(key, value);
    }
    if (data.isAvailable) {
      if (res.texUnit !== this.activeTexUnit_0) {
        this.activateTexUnit_0(ensureNotNull(texture.res).texUnit);
      }
      texture.loadData_3vby0t$(data, ctx);
      this.loadingTextures_0.remove_11rb$(texture.props.id);
    }
  };
  TextureManager.prototype.createResource_cmpczv$ = function (key, ctx) {
    var texRes = TextureResource$Companion_getInstance().create_wmc4vc$(GL_TEXTURE_2D, key, ctx);
    this.bindToActiveTexUnit_0(texRes);
    return texRes;
  };
  TextureManager.prototype.deleteResource_x4m4qh$ = function (key, res, ctx) {
    res.delete_evfofk$(ctx);
  };
  TextureManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextureManager',
    interfaces: [SharedResManager]
  };
  function LowPassFilter(coeff, input) {
    SampleNode.call(this);
    this.coeff = coeff;
    this.input = input;
  }
  LowPassFilter.prototype.generate_mx4ult$ = function (dt) {
    return this.filter_mx4ult$(this.input.next_mx4ult$(dt));
  };
  LowPassFilter.prototype.filter_mx4ult$ = function (input) {
    this.sample = this.sample + (input - this.sample) / this.coeff;
    return this.sample;
  };
  LowPassFilter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LowPassFilter',
    interfaces: [SampleNode]
  };
  function HighPassFilter(coeff, input) {
    SampleNode.call(this);
    this.coeff = coeff;
    this.input = input;
  }
  HighPassFilter.prototype.generate_mx4ult$ = function (dt) {
    return this.filter_mx4ult$(this.input.next_mx4ult$(dt));
  };
  HighPassFilter.prototype.filter_mx4ult$ = function (input) {
    this.sample = this.sample + (input - this.sample * this.coeff);
    return this.sample;
  };
  HighPassFilter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HighPassFilter',
    interfaces: [SampleNode]
  };
  function MoodFilter(input) {
    MoodFilter$Companion_getInstance();
    SampleNode.call(this);
    this.input = input;
    this.cutoff = 1000.0;
    this.res = 0.05;
    this.y1_0 = 0.0;
    this.y2_0 = 0.0;
    this.y3_0 = 0.0;
    this.y4_0 = 0.0;
    this.oldx_0 = 0.0;
    this.oldy1_0 = 0.0;
    this.oldy2_0 = 0.0;
    this.oldy3_0 = 0.0;
  }
  MoodFilter.prototype.generate_mx4ult$ = function (dt) {
    return this.filter_dleff0$(this.input.current(), dt);
  };
  MoodFilter.prototype.filter_7b5o5w$ = function (cutoff, res, input, dt) {
    this.cutoff = cutoff;
    this.res = res;
    return this.filter_dleff0$(input, dt);
  };
  var Math_0 = Math;
  MoodFilter.prototype.filter_dleff0$ = function (input, dt) {
    var cut = 2 * this.cutoff * dt;
    var p = cut * (MoodFilter$Companion_getInstance().C1_0 - MoodFilter$Companion_getInstance().C2_0 * cut);
    var x = cut * math.PI * 0.5;
    var k = 2 * Math_0.sin(x) - 1;
    var t1 = (1 - p) * MoodFilter$Companion_getInstance().C3_0;
    var t2 = 12 + t1 * t1;
    var r = this.res * (t2 + 6 * t1) / (t2 - 6 * t1);
    var x_0 = input - r * this.y4_0;
    this.y1_0 = x_0 * p + this.oldx_0 * p - k * this.y1_0;
    this.y2_0 = this.y1_0 * p + this.oldy1_0 * p - k * this.y2_0;
    this.y3_0 = this.y2_0 * p + this.oldy2_0 * p - k * this.y3_0;
    this.y4_0 = this.y3_0 * p + this.oldy3_0 * p - k * this.y4_0;
    this.y4_0 -= this.y4_0 * this.y4_0 * this.y4_0 / 6;
    this.oldx_0 = x_0;
    this.oldy1_0 = this.y1_0;
    this.oldy2_0 = this.y2_0;
    this.oldy3_0 = this.y3_0;
    return this.y4_0;
  };
  function MoodFilter$Companion() {
    MoodFilter$Companion_instance = this;
    this.C1_0 = 1.8;
    this.C2_0 = 0.8;
    this.C3_0 = 1.386;
  }
  MoodFilter$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MoodFilter$Companion_instance = null;
  function MoodFilter$Companion_getInstance() {
    if (MoodFilter$Companion_instance === null) {
      new MoodFilter$Companion();
    }
    return MoodFilter$Companion_instance;
  }
  MoodFilter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MoodFilter',
    interfaces: [SampleNode]
  };
  function HiHat(bpm) {
    SampleNode.call(this);
    this.bpm = bpm;
    this.highPass_0 = new HighPassFilter(1.7, this);
  }
  HiHat.prototype.generate_mx4ult$ = function (dt) {
    var noise = randomF_0(-1.0, 1.0);
    var pc2 = this.t % (60.0 / this.bpm);
    var pc1 = 266.0;
    if (this.t / 2 % 0.5 > 0.25) {
      pc1 = 106.0;
    }
    return this.highPass_0.filter_mx4ult$(SampleNode$Companion_getInstance().perc_7b5o5w$(noise, pc1, pc2)) * 0.2;
  };
  HiHat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HiHat',
    interfaces: [SampleNode]
  };
  function Kick(bpm) {
    SampleNode.call(this);
    this.bpm = bpm;
    var $receiver = new Oscillator(Wave$Companion_getInstance().SINE);
    $receiver.frequency = 50.0;
    this.osc1_0 = $receiver;
    var $receiver_0 = new Oscillator(Wave$Companion_getInstance().SAW);
    $receiver_0.frequency = 10.0;
    this.osc2_0 = $receiver_0;
    this.lowPass_0 = new LowPassFilter(240.0, this);
  }
  Kick.prototype.generate_mx4ult$ = function (dt) {
    var osc = SampleNode$Companion_getInstance().clip_dleff0$(SampleNode$Companion_getInstance().clip_dleff0$(this.osc1_0.next_mx4ult$(dt), 0.37) * 2 + SampleNode$Companion_getInstance().clip_dleff0$(this.osc2_0.next_mx4ult$(dt), 0.07) * 4, 0.6);
    var s = SampleNode$Companion_getInstance().perc_7b5o5w$(osc, 54.0, this.t % (60.0 / this.bpm)) * 2;
    return this.lowPass_0.filter_mx4ult$(s) + this.click_0(60.0 / this.bpm, this.t) * 0.055;
  };
  Kick.prototype.click_0 = function (x, t) {
    return 1.0 - 2 * (t % x) / x;
  };
  Kick.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Kick',
    interfaces: [SampleNode]
  };
  function Melody() {
    SampleNode.call(this);
    var $receiver = new Oscillator(Wave$Companion_getInstance().SINE, 1.0 / 32.0);
    $receiver.gain = 140.0;
    this.lfo1_0 = $receiver;
    var $receiver_0 = new Oscillator(Wave$Companion_getInstance().SINE, 0.5);
    $receiver_0.gain = 0.2;
    $receiver_0.phaseShift = 0.5;
    this.lfo2_0 = $receiver_0;
    var $receiver_1 = new Oscillator(Wave$Companion_getInstance().SAW);
    $receiver_1.gain = 0.7;
    this.osc1_0 = $receiver_1;
    var $receiver_2 = new Oscillator(Wave$Companion_getInstance().SQUARE);
    $receiver_2.gain = 0.4;
    this.osc2_0 = $receiver_2;
    var $receiver_3 = new Oscillator(Wave$Companion_getInstance().SINE);
    $receiver_3.gain = 0.8;
    this.osc3_0 = $receiver_3;
    var $receiver_4 = new Oscillator(Wave$Companion_getInstance().SQUARE);
    $receiver_4.gain = 1.2;
    this.osc4_0 = $receiver_4;
    this.moodFilter_0 = new MoodFilter(this);
    this.chords_0 = [7, 7, 7, 12, 10, 10, 10, 15, 7, 7, 7, 15, 15, 17, 10, 29, 7, 7, 7, 24, 10, 10, 10, 19, 7, 7, 7, 15, 29, 24, 15, 10];
  }
  Melody.prototype.generate_mx4ult$ = function (dt) {
    var f = SampleNode$Companion_getInstance().note_vux9f0$(this.chords_0[numberToInt(this.t * 4) % this.chords_0.length], 0);
    var osc = this.osc1_0.next_dleff0$(dt, f) + this.osc2_0.next_dleff0$(dt, f / 2.0) + this.osc3_0.next_dleff0$(dt, f / 2.0) + this.osc4_0.next_dleff0$(dt, f * 3.0);
    return this.moodFilter_0.filter_7b5o5w$(this.lfo1_0.next_mx4ult$(dt) + 1050, this.lfo2_0.next_mx4ult$(dt), SampleNode$Companion_getInstance().perc_7b5o5w$(osc, 48.0, this.t % 0.125), dt) * 0.25;
  };
  Melody.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Melody',
    interfaces: [SampleNode]
  };
  function Oscillator(shape, frequency) {
    if (frequency === void 0)
      frequency = 440.0;
    SampleNode.call(this);
    this.shape = shape;
    this.frequency = frequency;
    this.pos = 0.0;
    this.phaseShift_nj8deu$_0 = 0.0;
  }
  Object.defineProperty(Oscillator.prototype, 'phaseShift', {
    get: function () {
      return this.phaseShift_nj8deu$_0;
    },
    set: function (value) {
      var clamp$result;
      if (value < 0.0) {
        clamp$result = 0.0;
      }
       else if (value > 1.0) {
        clamp$result = 1.0;
      }
       else {
        clamp$result = value;
      }
      this.phaseShift_nj8deu$_0 = clamp$result;
    }
  });
  Oscillator.prototype.generate_mx4ult$ = function (dt) {
    this.pos += dt * this.frequency;
    if (this.pos > 1) {
      this.pos -= 1;
    }
    return this.shape.get_mx4ult$(this.pos + this.phaseShift);
  };
  Oscillator.prototype.next_dleff0$ = function (dt, freq) {
    this.frequency = freq;
    return this.next_mx4ult$(dt);
  };
  Oscillator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Oscillator',
    interfaces: [SampleNode]
  };
  function Pad() {
    SampleNode.call(this);
    var $receiver = new Oscillator(Wave$Companion_getInstance().SINE, 2.0);
    $receiver.gain = 0.2;
    this.lfo1_0 = $receiver;
    var $receiver_0 = new Oscillator(Wave$Companion_getInstance().SINE, 2.0);
    $receiver_0.gain = 150.0;
    this.lfo2_0 = $receiver_0;
    var $receiver_1 = new Oscillator(Wave$Companion_getInstance().SAW);
    $receiver_1.gain = 5.1;
    this.osc1_0 = $receiver_1;
    var $receiver_2 = new Oscillator(Wave$Companion_getInstance().SAW);
    $receiver_2.gain = 3.9;
    this.osc2_0 = $receiver_2;
    var $receiver_3 = new Oscillator(Wave$Companion_getInstance().SAW);
    $receiver_3.gain = 4.0;
    this.osc3_0 = $receiver_3;
    var $receiver_4 = new Oscillator(Wave$Companion_getInstance().SQUARE);
    $receiver_4.gain = 3.0;
    this.osc4_0 = $receiver_4;
    this.highPass_0 = new HighPassFilter(0.5, this);
    this.moodFilter_0 = new MoodFilter(this);
    this.chords_0 = [new Int32Array([7, 12, 17, 10]), new Int32Array([10, 15, 19, 24])];
  }
  Pad.prototype.generate_mx4ult$ = function (dt) {
    var n = this.chords_0[numberToInt(this.t / 4) % this.chords_0.length];
    var osc = this.osc1_0.next_dleff0$(dt, SampleNode$Companion_getInstance().note_vux9f0$(n[0], 1)) + this.osc2_0.next_dleff0$(dt, SampleNode$Companion_getInstance().note_vux9f0$(n[1], 2)) + this.osc3_0.next_dleff0$(dt, SampleNode$Companion_getInstance().note_vux9f0$(n[2], 1)) + this.osc4_0.next_dleff0$(dt, SampleNode$Companion_getInstance().note_vux9f0$(n[3], 0)) + SampleNode$Companion_getInstance().noise_mx4ult$(0.7);
    var s = this.moodFilter_0.filter_7b5o5w$(this.lfo2_0.next_mx4ult$(dt) + 1100, 0.05, osc / 33.0, dt);
    return (this.lfo1_0.next_mx4ult$(dt) + 0.5) * this.highPass_0.filter_mx4ult$(s) * 0.15;
  };
  Pad.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Pad',
    interfaces: [SampleNode]
  };
  function SampleNode() {
    SampleNode$Companion_getInstance();
    this.gain = 1.0;
    this.t_ltenjb$_0 = 0.0;
    this.sample = 0.0;
  }
  Object.defineProperty(SampleNode.prototype, 't', {
    get: function () {
      return this.t_ltenjb$_0;
    },
    set: function (t) {
      this.t_ltenjb$_0 = t;
    }
  });
  SampleNode.prototype.current = function () {
    return this.sample;
  };
  SampleNode.prototype.next_mx4ult$ = function (dt) {
    this.t = this.t + dt;
    this.sample = this.generate_mx4ult$(dt) * this.gain;
    return this.sample;
  };
  function SampleNode$Companion() {
    SampleNode$Companion_instance = this;
    var array = Array_0(15);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var array_0 = new Float32Array(100);
      var tmp$_0;
      tmp$_0 = array_0.length - 1 | 0;
      for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
        var Math_0 = Math;
        var x = ((i_0 - 20 | 0) - 33.0 + 12.0 * (i - 5 | 0)) / 12.0;
        array_0[i_0] = Math_0.pow(2.0, x) * 440.0;
      }
      array[i] = array_0;
    }
    this.NOTE_TABLE_0 = array;
  }
  SampleNode$Companion.prototype.clip_dleff0$ = function (value, clip) {
    var min = -clip;
    var clamp$result;
    if (value < min) {
      clamp$result = min;
    }
     else if (value > clip) {
      clamp$result = clip;
    }
     else {
      clamp$result = value;
    }
    return clamp$result;
  };
  SampleNode$Companion.prototype.noise_mx4ult$ = function (amplitude) {
    if (amplitude === void 0)
      amplitude = 1.0;
    return randomF_0(-amplitude, amplitude);
  };
  SampleNode$Companion.prototype.note_vux9f0$ = function (note, octave) {
    var clamp$result;
    if (octave < -5) {
      clamp$result = -5;
    }
     else if (octave > 9) {
      clamp$result = 9;
    }
     else {
      clamp$result = octave;
    }
    var o = clamp$result + 5 | 0;
    var clamp$result_0;
    if (note < -20) {
      clamp$result_0 = -20;
    }
     else if (note > 79) {
      clamp$result_0 = 79;
    }
     else {
      clamp$result_0 = note;
    }
    var n = clamp$result_0 + 20 | 0;
    return this.NOTE_TABLE_0[o][n];
  };
  SampleNode$Companion.prototype.perc_7b5o5w$ = function (sample, decay, f, c) {
    if (c === void 0)
      c = 0.889;
    var b = c - f * decay / (f * decay + 1);
    return sample * Math_0.max(0.0, b);
  };
  SampleNode$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var SampleNode$Companion_instance = null;
  function SampleNode$Companion_getInstance() {
    if (SampleNode$Companion_instance === null) {
      new SampleNode$Companion();
    }
    return SampleNode$Companion_instance;
  }
  SampleNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SampleNode',
    interfaces: []
  };
  function Shaker(bpm) {
    SampleNode.call(this);
    this.bpm = bpm;
    this.highPass_0 = new HighPassFilter(1.5, this);
  }
  Shaker.prototype.generate_mx4ult$ = function (dt) {
    var pc2 = this.t % (60.0 / this.bpm) / 8;
    var pc1 = 230.0;
    if ((this.t + 0.5) % 0.5 > 0.25) {
      pc1 = 150.0;
    }
    return this.highPass_0.filter_mx4ult$(SampleNode$Companion_getInstance().perc_7b5o5w$(SampleNode$Companion_getInstance().noise_mx4ult$(), pc1, pc2, 0.95)) * 0.1;
  };
  Shaker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shaker',
    interfaces: [SampleNode]
  };
  function Snare(bpm) {
    SampleNode.call(this);
    this.bpm = bpm;
    var $receiver = new Oscillator(Wave$Companion_getInstance().SQUARE, 175.0);
    $receiver.gain = 0.156;
    this.osc_0 = $receiver;
    this.lowPass_0 = new LowPassFilter(30.0, this);
  }
  Snare.prototype.generate_mx4ult$ = function (dt) {
    var s = this.osc_0.next_mx4ult$(dt) + SampleNode$Companion_getInstance().noise_mx4ult$(0.73);
    var pc2 = (this.t + 0.5) % (60.0 / this.bpm);
    var pc1 = 120.0;
    if (this.t % 2 > 1) {
      pc1 = 105.0;
    }
    return this.lowPass_0.filter_mx4ult$(SampleNode$Companion_getInstance().perc_7b5o5w$(s, pc1, pc2) * 0.6) * 5;
  };
  Snare.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Snare',
    interfaces: [SampleNode]
  };
  function Wave(tableSize, generator) {
    Wave$Companion_getInstance();
    this.tableSize = tableSize;
    this.table_0 = new Float32Array(this.tableSize);
    var tmp$;
    tmp$ = this.tableSize;
    for (var i = 0; i < tmp$; i++) {
      this.table_0[i] = generator(i / this.tableSize);
    }
  }
  Wave.prototype.get_mx4ult$ = function (index) {
    return this.table_0[numberToInt(index * this.tableSize) % this.tableSize];
  };
  function Wave$Companion() {
    Wave$Companion_instance = this;
    this.DEFAULT_TABLE_SIZE = 2048;
    this.SINE = new Wave(this.DEFAULT_TABLE_SIZE, Wave$Companion$SINE$lambda);
    this.SAW = new Wave(this.DEFAULT_TABLE_SIZE, Wave$Companion$SAW$lambda);
    this.RAMP = new Wave(this.DEFAULT_TABLE_SIZE, Wave$Companion$RAMP$lambda);
    this.TRIANGLE = new Wave(this.DEFAULT_TABLE_SIZE, Wave$Companion$TRIANGLE$lambda);
    this.SQUARE = new Wave(this.DEFAULT_TABLE_SIZE, Wave$Companion$SQUARE$lambda);
  }
  function Wave$Companion$SINE$lambda(p) {
    var x = p * math.PI * 2;
    return Math_0.sin(x);
  }
  function Wave$Companion$SAW$lambda(p) {
    return -2.0 * (p - round(p));
  }
  function Wave$Companion$RAMP$lambda(p) {
    return 2.0 * (p - round(p));
  }
  function Wave$Companion$TRIANGLE$lambda(p) {
    var x = round(p) - p;
    return 1.0 - 4.0 * Math_0.abs(x);
  }
  function Wave$Companion$SQUARE$lambda(p) {
    return p < 0.5 ? 1.0 : -1.0;
  }
  Wave$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Wave$Companion_instance = null;
  function Wave$Companion_getInstance() {
    if (Wave$Companion_instance === null) {
      new Wave$Companion();
    }
    return Wave$Companion_instance;
  }
  Wave.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Wave',
    interfaces: []
  };
  var GL_ACTIVE_TEXTURE;
  var GL_DEPTH_BUFFER_BIT;
  var GL_STENCIL_BUFFER_BIT;
  var GL_COLOR_BUFFER_BIT;
  var GL_FALSE;
  var GL_TRUE;
  var GL_POINTS;
  var GL_LINES;
  var GL_LINE_LOOP;
  var GL_LINE_STRIP;
  var GL_TRIANGLES;
  var GL_TRIANGLE_STRIP;
  var GL_TRIANGLE_FAN;
  var GL_ZERO;
  var GL_ONE;
  var GL_SRC_COLOR;
  var GL_ONE_MINUS_SRC_COLOR;
  var GL_SRC_ALPHA;
  var GL_ONE_MINUS_SRC_ALPHA;
  var GL_DST_ALPHA;
  var GL_ONE_MINUS_DST_ALPHA;
  var GL_DST_COLOR;
  var GL_ONE_MINUS_DST_COLOR;
  var GL_SRC_ALPHA_SATURATE;
  var GL_FUNC_ADD;
  var GL_BLEND_EQUATION;
  var GL_BLEND_EQUATION_RGB;
  var GL_BLEND_EQUATION_ALPHA;
  var GL_FUNC_SUBTRACT;
  var GL_FUNC_REVERSE_SUBTRACT;
  var GL_BLEND_DST_RGB;
  var GL_BLEND_SRC_RGB;
  var GL_BLEND_DST_ALPHA;
  var GL_BLEND_SRC_ALPHA;
  var GL_CONSTANT_COLOR;
  var GL_ONE_MINUS_CONSTANT_COLOR;
  var GL_CONSTANT_ALPHA;
  var GL_ONE_MINUS_CONSTANT_ALPHA;
  var GL_BLEND_COLOR;
  var GL_ARRAY_BUFFER;
  var GL_ELEMENT_ARRAY_BUFFER;
  var GL_ARRAY_BUFFER_BINDING;
  var GL_ELEMENT_ARRAY_BUFFER_BINDING;
  var GL_STREAM_DRAW;
  var GL_STATIC_DRAW;
  var GL_DYNAMIC_DRAW;
  var GL_BUFFER_SIZE;
  var GL_BUFFER_USAGE;
  var GL_CURRENT_VERTEX_ATTRIB;
  var GL_FRONT;
  var GL_BACK;
  var GL_FRONT_AND_BACK;
  var GL_TEXTURE_2D;
  var GL_CULL_FACE;
  var GL_BLEND;
  var GL_DITHER;
  var GL_STENCIL_TEST;
  var GL_DEPTH_TEST;
  var GL_SCISSOR_TEST;
  var GL_POLYGON_OFFSET_FILL;
  var GL_SAMPLE_ALPHA_TO_COVERAGE;
  var GL_SAMPLE_COVERAGE;
  var GL_NO_ERROR;
  var GL_INVALID_ENUM;
  var GL_INVALID_VALUE;
  var GL_INVALID_OPERATION;
  var GL_OUT_OF_MEMORY;
  var GL_CW;
  var GL_CCW;
  var GL_LINE_WIDTH;
  var GL_ALIASED_POINT_SIZE_RANGE;
  var GL_ALIASED_LINE_WIDTH_RANGE;
  var GL_CULL_FACE_MODE;
  var GL_FRONT_FACE;
  var GL_DEPTH_RANGE;
  var GL_DEPTH_WRITEMASK;
  var GL_DEPTH_CLEAR_VALUE;
  var GL_DEPTH_FUNC;
  var GL_STENCIL_CLEAR_VALUE;
  var GL_STENCIL_FUNC;
  var GL_STENCIL_FAIL;
  var GL_STENCIL_PASS_DEPTH_FAIL;
  var GL_STENCIL_PASS_DEPTH_PASS;
  var GL_STENCIL_REF;
  var GL_STENCIL_VALUE_MASK;
  var GL_STENCIL_WRITEMASK;
  var GL_STENCIL_BACK_FUNC;
  var GL_STENCIL_BACK_FAIL;
  var GL_STENCIL_BACK_PASS_DEPTH_FAIL;
  var GL_STENCIL_BACK_PASS_DEPTH_PASS;
  var GL_STENCIL_BACK_REF;
  var GL_STENCIL_BACK_VALUE_MASK;
  var GL_STENCIL_BACK_WRITEMASK;
  var GL_VIEWPORT;
  var GL_SCISSOR_BOX;
  var GL_COLOR_CLEAR_VALUE;
  var GL_COLOR_WRITEMASK;
  var GL_UNPACK_ALIGNMENT;
  var GL_PACK_ALIGNMENT;
  var GL_MAX_TEXTURE_SIZE;
  var GL_MAX_VIEWPORT_DIMS;
  var GL_SUBPIXEL_BITS;
  var GL_RED_BITS;
  var GL_GREEN_BITS;
  var GL_BLUE_BITS;
  var GL_ALPHA_BITS;
  var GL_DEPTH_BITS;
  var GL_STENCIL_BITS;
  var GL_POLYGON_OFFSET_UNITS;
  var GL_POLYGON_OFFSET_FACTOR;
  var GL_TEXTURE_BINDING_2D;
  var GL_SAMPLE_BUFFERS;
  var GL_SAMPLES;
  var GL_SAMPLE_COVERAGE_VALUE;
  var GL_SAMPLE_COVERAGE_INVERT;
  var GL_NUM_COMPRESSED_TEXTURE_FORMATS;
  var GL_COMPRESSED_TEXTURE_FORMATS;
  var GL_DONT_CARE;
  var GL_FASTEST;
  var GL_NICEST;
  var GL_GENERATE_MIPMAP_HINT;
  var GL_BYTE;
  var GL_UNSIGNED_BYTE;
  var GL_SHORT;
  var GL_UNSIGNED_SHORT;
  var GL_INT;
  var GL_UNSIGNED_INT;
  var GL_FLOAT;
  var GL_FIXED;
  var GL_DEPTH_COMPONENT;
  var GL_DEPTH_COMPONENT24;
  var GL_DEPTH_COMPONENT32F;
  var GL_ALPHA;
  var GL_RGB;
  var GL_RGBA;
  var GL_LUMINANCE;
  var GL_LUMINANCE_ALPHA;
  var GL_UNSIGNED_SHORT_4_4_4_4;
  var GL_UNSIGNED_SHORT_5_5_5_1;
  var GL_UNSIGNED_SHORT_5_6_5;
  var GL_FRAGMENT_SHADER;
  var GL_VERTEX_SHADER;
  var GL_MAX_VERTEX_ATTRIBS;
  var GL_MAX_VERTEX_UNIFORM_VECTORS;
  var GL_MAX_VARYING_VECTORS;
  var GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
  var GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
  var GL_MAX_TEXTURE_IMAGE_UNITS;
  var GL_MAX_FRAGMENT_UNIFORM_VECTORS;
  var GL_SHADER_TYPE;
  var GL_DELETE_STATUS;
  var GL_LINK_STATUS;
  var GL_VALIDATE_STATUS;
  var GL_ATTACHED_SHADERS;
  var GL_ACTIVE_UNIFORMS;
  var GL_ACTIVE_UNIFORM_MAX_LENGTH;
  var GL_ACTIVE_ATTRIBUTES;
  var GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
  var GL_SHADING_LANGUAGE_VERSION;
  var GL_CURRENT_PROGRAM;
  var GL_NEVER;
  var GL_LESS;
  var GL_EQUAL;
  var GL_LEQUAL;
  var GL_GREATER;
  var GL_NOTEQUAL;
  var GL_GEQUAL;
  var GL_ALWAYS;
  var GL_KEEP;
  var GL_REPLACE;
  var GL_INCR;
  var GL_DECR;
  var GL_INVERT;
  var GL_INCR_WRAP;
  var GL_DECR_WRAP;
  var GL_VENDOR;
  var GL_RENDERER;
  var GL_VERSION;
  var GL_EXTENSIONS;
  var GL_NEAREST;
  var GL_LINEAR;
  var GL_NEAREST_MIPMAP_NEAREST;
  var GL_LINEAR_MIPMAP_NEAREST;
  var GL_NEAREST_MIPMAP_LINEAR;
  var GL_LINEAR_MIPMAP_LINEAR;
  var GL_TEXTURE_MAG_FILTER;
  var GL_TEXTURE_MIN_FILTER;
  var GL_TEXTURE_WRAP_S;
  var GL_TEXTURE_WRAP_T;
  var GL_TEXTURE;
  var GL_TEXTURE_CUBE_MAP;
  var GL_TEXTURE_BINDING_CUBE_MAP;
  var GL_TEXTURE_CUBE_MAP_POSITIVE_X;
  var GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
  var GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
  var GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
  var GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
  var GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
  var GL_MAX_CUBE_MAP_TEXTURE_SIZE;
  var GL_TEXTURE0;
  var GL_TEXTURE1;
  var GL_TEXTURE2;
  var GL_TEXTURE3;
  var GL_TEXTURE4;
  var GL_TEXTURE5;
  var GL_TEXTURE6;
  var GL_TEXTURE7;
  var GL_TEXTURE8;
  var GL_TEXTURE9;
  var GL_TEXTURE10;
  var GL_TEXTURE11;
  var GL_TEXTURE12;
  var GL_TEXTURE13;
  var GL_TEXTURE14;
  var GL_TEXTURE15;
  var GL_TEXTURE16;
  var GL_TEXTURE17;
  var GL_TEXTURE18;
  var GL_TEXTURE19;
  var GL_TEXTURE20;
  var GL_TEXTURE21;
  var GL_TEXTURE22;
  var GL_TEXTURE23;
  var GL_TEXTURE24;
  var GL_TEXTURE25;
  var GL_TEXTURE26;
  var GL_TEXTURE27;
  var GL_TEXTURE28;
  var GL_TEXTURE29;
  var GL_TEXTURE30;
  var GL_TEXTURE31;
  var GL_REPEAT;
  var GL_CLAMP_TO_EDGE;
  var GL_MIRRORED_REPEAT;
  var GL_FLOAT_VEC2;
  var GL_FLOAT_VEC3;
  var GL_FLOAT_VEC4;
  var GL_INT_VEC2;
  var GL_INT_VEC3;
  var GL_INT_VEC4;
  var GL_BOOL;
  var GL_BOOL_VEC2;
  var GL_BOOL_VEC3;
  var GL_BOOL_VEC4;
  var GL_FLOAT_MAT2;
  var GL_FLOAT_MAT3;
  var GL_FLOAT_MAT4;
  var GL_SAMPLER_2D;
  var GL_SAMPLER_CUBE;
  var GL_VERTEX_ATTRIB_ARRAY_ENABLED;
  var GL_VERTEX_ATTRIB_ARRAY_SIZE;
  var GL_VERTEX_ATTRIB_ARRAY_STRIDE;
  var GL_VERTEX_ATTRIB_ARRAY_TYPE;
  var GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
  var GL_VERTEX_ATTRIB_ARRAY_POINTER;
  var GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
  var GL_IMPLEMENTATION_COLOR_READ_TYPE;
  var GL_IMPLEMENTATION_COLOR_READ_FORMAT;
  var GL_COMPILE_STATUS;
  var GL_INFO_LOG_LENGTH;
  var GL_SHADER_SOURCE_LENGTH;
  var GL_SHADER_COMPILER;
  var GL_SHADER_BINARY_FORMATS;
  var GL_NUM_SHADER_BINARY_FORMATS;
  var GL_LOW_FLOAT;
  var GL_MEDIUM_FLOAT;
  var GL_HIGH_FLOAT;
  var GL_LOW_INT;
  var GL_MEDIUM_INT;
  var GL_HIGH_INT;
  var GL_FRAMEBUFFER;
  var GL_RENDERBUFFER;
  var GL_RGBA4;
  var GL_RGB5_A1;
  var GL_RGB565;
  var GL_DEPTH_COMPONENT16;
  var GL_STENCIL_INDEX8;
  var GL_RENDERBUFFER_WIDTH;
  var GL_RENDERBUFFER_HEIGHT;
  var GL_RENDERBUFFER_INTERNAL_FORMAT;
  var GL_RENDERBUFFER_RED_SIZE;
  var GL_RENDERBUFFER_GREEN_SIZE;
  var GL_RENDERBUFFER_BLUE_SIZE;
  var GL_RENDERBUFFER_ALPHA_SIZE;
  var GL_RENDERBUFFER_DEPTH_SIZE;
  var GL_RENDERBUFFER_STENCIL_SIZE;
  var GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE;
  var GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME;
  var GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL;
  var GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE;
  var GL_COLOR_ATTACHMENT0;
  var GL_DEPTH_ATTACHMENT;
  var GL_STENCIL_ATTACHMENT;
  var GL_NONE;
  var GL_FRAMEBUFFER_COMPLETE;
  var GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
  var GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
  var GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS;
  var GL_FRAMEBUFFER_UNSUPPORTED;
  var GL_FRAMEBUFFER_BINDING;
  var GL_RENDERBUFFER_BINDING;
  var GL_MAX_RENDERBUFFER_SIZE;
  var GL_INVALID_FRAMEBUFFER_OPERATION;
  function BufferResource(glRef, target, ctx) {
    BufferResource$Companion_getInstance();
    GlResource.call(this, glRef, GlResource$Type$BUFFER_getInstance(), ctx);
    this.target = target;
  }
  function BufferResource$Companion() {
    BufferResource$Companion_instance = this;
  }
  BufferResource$Companion.prototype.create_gre2l6$ = function (target, ctx) {
    return new BufferResource(glCreateBuffer(), target, ctx);
  };
  BufferResource$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BufferResource$Companion_instance = null;
  function BufferResource$Companion_getInstance() {
    if (BufferResource$Companion_instance === null) {
      new BufferResource$Companion();
    }
    return BufferResource$Companion_instance;
  }
  BufferResource.prototype.delete_evfofk$ = function (ctx) {
    glDeleteBuffer(this);
    GlResource.prototype.delete_evfofk$.call(this, ctx);
  };
  BufferResource.prototype.bind_evfofk$ = function (ctx) {
    if (!equals(ctx.boundBuffers_8be2vx$.get_11rb$(this.target), this)) {
      glBindBuffer(this.target, this);
      var $receiver = ctx.boundBuffers_8be2vx$;
      var key = this.target;
      $receiver.put_xwzc9p$(key, this);
    }
  };
  BufferResource.prototype.setData_i6at0a$ = function (data, usage, ctx) {
    var limit = data.limit;
    var pos = data.position;
    data.flip();
    this.bind_evfofk$(ctx);
    glBufferData_2(this.target, data, usage);
    ctx.memoryMgr.memoryAllocated_927jj9$(this, pos * 4 | 0);
    data.limit = limit;
    data.position = pos;
  };
  BufferResource.prototype.setData_a5dm9j$ = function (data, usage, ctx) {
    var limit = data.limit;
    var pos = data.position;
    data.flip();
    this.bind_evfofk$(ctx);
    glBufferData(this.target, data, usage);
    ctx.memoryMgr.memoryAllocated_927jj9$(this, pos);
    data.limit = limit;
    data.position = pos;
  };
  BufferResource.prototype.setData_wsx8y8$ = function (data, usage, ctx) {
    var limit = data.limit;
    var pos = data.position;
    data.flip();
    this.bind_evfofk$(ctx);
    glBufferData_0(this.target, data, usage);
    ctx.memoryMgr.memoryAllocated_927jj9$(this, pos * 2 | 0);
    data.limit = limit;
    data.position = pos;
  };
  BufferResource.prototype.setData_5irzgq$ = function (data, usage, ctx) {
    var limit = data.limit;
    var pos = data.position;
    data.flip();
    this.bind_evfofk$(ctx);
    glBufferData_1(this.target, data, usage);
    ctx.memoryMgr.memoryAllocated_927jj9$(this, pos * 4 | 0);
    data.limit = limit;
    data.position = pos;
  };
  BufferResource.prototype.unbind_evfofk$ = function (ctx) {
    glBindBuffer(this.target, null);
    var $receiver = ctx.boundBuffers_8be2vx$;
    var key = this.target;
    $receiver.put_xwzc9p$(key, null);
  };
  BufferResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BufferResource',
    interfaces: [GlResource]
  };
  function Framebuffer(width, height) {
    this.width = width;
    this.height = height;
    this.fbId_0 = UniqueId_getInstance().nextId();
    this.fbResource_iqa21b$_0 = null;
    this.colorAttachment_5z0qtn$_0 = null;
    this.depthAttachment_cd2pcl$_0 = null;
  }
  Object.defineProperty(Framebuffer.prototype, 'fbResource', {
    get: function () {
      return this.fbResource_iqa21b$_0;
    },
    set: function (fbResource) {
      this.fbResource_iqa21b$_0 = fbResource;
    }
  });
  Object.defineProperty(Framebuffer.prototype, 'colorAttachment', {
    get: function () {
      return this.colorAttachment_5z0qtn$_0;
    },
    set: function (colorAttachment) {
      this.colorAttachment_5z0qtn$_0 = colorAttachment;
    }
  });
  Object.defineProperty(Framebuffer.prototype, 'depthAttachment', {
    get: function () {
      return this.depthAttachment_cd2pcl$_0;
    },
    set: function (depthAttachment) {
      this.depthAttachment_cd2pcl$_0 = depthAttachment;
    }
  });
  Framebuffer.prototype.withColor = function () {
    if (this.colorAttachment == null) {
      this.colorAttachment = FbColorTexData$Companion_getInstance().colorTex_ld7r1l$(this.width, this.height, this.fbId_0);
    }
    return this;
  };
  function Framebuffer$withDepth$lambda(this$Framebuffer) {
    return function ($receiver) {
      return new FbDepthTexData(this$Framebuffer.width, this$Framebuffer.height);
    };
  }
  Framebuffer.prototype.withDepth = function () {
    if (this.depthAttachment == null) {
      var filterMethod = glCapabilities.depthFilterMethod;
      var depthProps = new TextureProps('framebuffer-' + this.fbId_0 + '-depth', filterMethod, filterMethod, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0);
      this.depthAttachment = new Texture(depthProps, Framebuffer$withDepth$lambda(this));
    }
    return this;
  };
  Framebuffer.prototype.delete_evfofk$ = function (ctx) {
    var tmp$, tmp$_0, tmp$_1;
    (tmp$ = this.fbResource) != null ? (tmp$.delete_evfofk$(ctx), Unit) : null;
    (tmp$_0 = this.colorAttachment) != null ? (tmp$_0.dispose_evfofk$(ctx), Unit) : null;
    (tmp$_1 = this.depthAttachment) != null ? (tmp$_1.dispose_evfofk$(ctx), Unit) : null;
    this.fbResource = null;
    this.colorAttachment = null;
    this.depthAttachment = null;
  };
  Framebuffer.prototype.bind_evfofk$ = function (ctx) {
    var tmp$;
    var tmp$_0;
    if ((tmp$ = this.fbResource) != null)
      tmp$_0 = tmp$;
    else {
      var $receiver = FramebufferResource$Companion_getInstance().create_uavttc$(this.width, this.height, ctx);
      this.fbResource = $receiver;
      $receiver.colorAttachment = this.colorAttachment;
      $receiver.depthAttachment = this.depthAttachment;
      tmp$_0 = $receiver;
    }
    var fb = tmp$_0;
    fb.bind_evfofk$(ctx);
  };
  Framebuffer.prototype.unbind_evfofk$ = function (ctx) {
    var tmp$;
    (tmp$ = this.fbResource) != null ? (tmp$.unbind_evfofk$(ctx), Unit) : null;
  };
  Framebuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Framebuffer',
    interfaces: []
  };
  function FramebufferResource(glRef, width, height, ctx) {
    FramebufferResource$Companion_getInstance();
    GlResource.call(this, glRef, GlResource$Type$FRAMEBUFFER_getInstance(), ctx);
    this.width = width;
    this.height = height;
    this.fbId_0 = UniqueId_getInstance().nextId();
    this.colorAttachment = null;
    this.depthAttachment = null;
    this.isFbComplete_0 = false;
  }
  function FramebufferResource$Companion() {
    FramebufferResource$Companion_instance = this;
  }
  FramebufferResource$Companion.prototype.create_uavttc$ = function (width, height, ctx) {
    return new FramebufferResource(glCreateFramebuffer(), width, height, ctx);
  };
  FramebufferResource$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var FramebufferResource$Companion_instance = null;
  function FramebufferResource$Companion_getInstance() {
    if (FramebufferResource$Companion_instance === null) {
      new FramebufferResource$Companion();
    }
    return FramebufferResource$Companion_instance;
  }
  FramebufferResource.prototype.delete_evfofk$ = function (ctx) {
    glDeleteFramebuffer(this);
    GlResource.prototype.delete_evfofk$.call(this, ctx);
  };
  FramebufferResource.prototype.bind_evfofk$ = function (ctx) {
    glBindFramebuffer(GL_FRAMEBUFFER, this);
    if (!this.isFbComplete_0) {
      this.isFbComplete_0 = true;
      if (this.colorAttachment == null && this.depthAttachment != null && !glCapabilities.framebufferWithoutColor) {
        this.colorAttachment = FbColorTexData$Companion_getInstance().colorTex_ld7r1l$(this.width, this.height, this.fbId_0);
      }
      var color = this.colorAttachment;
      if (color != null) {
        ctx.textureMgr.bindTexture_4yp9vu$(color, ctx);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ensureNotNull(color.res), 0);
      }
       else {
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
      }
      var depth = this.depthAttachment;
      if (depth != null) {
        ctx.textureMgr.bindTexture_4yp9vu$(depth, ctx);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, ensureNotNull(depth.res), 0);
      }
    }
    ctx.pushAttributes();
    ctx.viewport = new RenderContext$Viewport(0, 0, this.width, this.height);
    ctx.applyAttributes();
  };
  FramebufferResource.prototype.unbind_evfofk$ = function (ctx) {
    glBindFramebuffer(GL_FRAMEBUFFER, null);
    ctx.popAttributes();
  };
  FramebufferResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FramebufferResource',
    interfaces: [GlResource]
  };
  function FbColorTexData(width, height) {
    FbColorTexData$Companion_getInstance();
    TextureData.call(this);
    this.isAvailable = true;
    this.width = width;
    this.height = height;
  }
  FbColorTexData.prototype.onLoad_4yp9vu$ = function (texture, ctx) {
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
  };
  function FbColorTexData$Companion() {
    FbColorTexData$Companion_instance = this;
  }
  function FbColorTexData$Companion$colorTex$lambda(closure$sizeX, closure$sizeY) {
    return function ($receiver) {
      return new FbColorTexData(closure$sizeX, closure$sizeY);
    };
  }
  FbColorTexData$Companion.prototype.colorTex_ld7r1l$ = function (sizeX, sizeY, fbId) {
    var colorProps = new TextureProps('framebuffer-' + fbId + '-color', GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0);
    return new Texture(colorProps, FbColorTexData$Companion$colorTex$lambda(sizeX, sizeY));
  };
  FbColorTexData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var FbColorTexData$Companion_instance = null;
  function FbColorTexData$Companion_getInstance() {
    if (FbColorTexData$Companion_instance === null) {
      new FbColorTexData$Companion();
    }
    return FbColorTexData$Companion_instance;
  }
  FbColorTexData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FbColorTexData',
    interfaces: [TextureData]
  };
  function FbDepthTexData(width, height) {
    FbDepthTexData$Companion_getInstance();
    TextureData.call(this);
    this.isAvailable = true;
    this.width = width;
    this.height = height;
  }
  FbDepthTexData.prototype.onLoad_4yp9vu$ = function (texture, ctx) {
    glTexImage2D(GL_TEXTURE_2D, 0, glCapabilities.depthComponentIntFormat, this.width, this.height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null);
  };
  function FbDepthTexData$Companion() {
    FbDepthTexData$Companion_instance = this;
  }
  function FbDepthTexData$Companion$depthTex$lambda(closure$sizeX, closure$sizeY) {
    return function ($receiver) {
      return new FbDepthTexData(closure$sizeX, closure$sizeY);
    };
  }
  FbDepthTexData$Companion.prototype.depthTex_ld7r1l$ = function (sizeX, sizeY, fbId) {
    var filterMethod = glCapabilities.depthFilterMethod;
    var depthProps = new TextureProps('framebuffer-' + fbId + '-depth', filterMethod, filterMethod, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0);
    return new Texture(depthProps, FbDepthTexData$Companion$depthTex$lambda(sizeX, sizeY));
  };
  FbDepthTexData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var FbDepthTexData$Companion_instance = null;
  function FbDepthTexData$Companion_getInstance() {
    if (FbDepthTexData$Companion_instance === null) {
      new FbDepthTexData$Companion();
    }
    return FbDepthTexData$Companion_instance;
  }
  FbDepthTexData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FbDepthTexData',
    interfaces: [TextureData]
  };
  function GlObject() {
    this.res_1nanwc$_0 = null;
  }
  Object.defineProperty(GlObject.prototype, 'res', {
    get: function () {
      return this.res_1nanwc$_0;
    },
    set: function (res) {
      this.res_1nanwc$_0 = res;
    }
  });
  Object.defineProperty(GlObject.prototype, 'isValid', {
    get: function () {
      return this.res != null;
    }
  });
  GlObject.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$;
    (tmp$ = this.res) != null ? (tmp$.delete_evfofk$(ctx), Unit) : null;
    this.res = null;
  };
  GlObject.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlObject',
    interfaces: []
  };
  function GlResource(glRef, type, ctx) {
    this.type = type;
    this.glRef_p5t3zt$_0 = glRef;
    ctx.memoryMgr.memoryAllocated_927jj9$(this, 0);
  }
  function GlResource$Type(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function GlResource$Type_initFields() {
    GlResource$Type_initFields = function () {
    };
    GlResource$Type$BUFFER_instance = new GlResource$Type('BUFFER', 0);
    GlResource$Type$FRAMEBUFFER_instance = new GlResource$Type('FRAMEBUFFER', 1);
    GlResource$Type$PROGRAM_instance = new GlResource$Type('PROGRAM', 2);
    GlResource$Type$RENDERBUFFER_instance = new GlResource$Type('RENDERBUFFER', 3);
    GlResource$Type$SHADER_instance = new GlResource$Type('SHADER', 4);
    GlResource$Type$TEXTURE_instance = new GlResource$Type('TEXTURE', 5);
  }
  var GlResource$Type$BUFFER_instance;
  function GlResource$Type$BUFFER_getInstance() {
    GlResource$Type_initFields();
    return GlResource$Type$BUFFER_instance;
  }
  var GlResource$Type$FRAMEBUFFER_instance;
  function GlResource$Type$FRAMEBUFFER_getInstance() {
    GlResource$Type_initFields();
    return GlResource$Type$FRAMEBUFFER_instance;
  }
  var GlResource$Type$PROGRAM_instance;
  function GlResource$Type$PROGRAM_getInstance() {
    GlResource$Type_initFields();
    return GlResource$Type$PROGRAM_instance;
  }
  var GlResource$Type$RENDERBUFFER_instance;
  function GlResource$Type$RENDERBUFFER_getInstance() {
    GlResource$Type_initFields();
    return GlResource$Type$RENDERBUFFER_instance;
  }
  var GlResource$Type$SHADER_instance;
  function GlResource$Type$SHADER_getInstance() {
    GlResource$Type_initFields();
    return GlResource$Type$SHADER_instance;
  }
  var GlResource$Type$TEXTURE_instance;
  function GlResource$Type$TEXTURE_getInstance() {
    GlResource$Type_initFields();
    return GlResource$Type$TEXTURE_instance;
  }
  GlResource$Type.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Type',
    interfaces: [Enum]
  };
  function GlResource$Type$values() {
    return [GlResource$Type$BUFFER_getInstance(), GlResource$Type$FRAMEBUFFER_getInstance(), GlResource$Type$PROGRAM_getInstance(), GlResource$Type$RENDERBUFFER_getInstance(), GlResource$Type$SHADER_getInstance(), GlResource$Type$TEXTURE_getInstance()];
  }
  GlResource$Type.values = GlResource$Type$values;
  function GlResource$Type$valueOf(name) {
    switch (name) {
      case 'BUFFER':
        return GlResource$Type$BUFFER_getInstance();
      case 'FRAMEBUFFER':
        return GlResource$Type$FRAMEBUFFER_getInstance();
      case 'PROGRAM':
        return GlResource$Type$PROGRAM_getInstance();
      case 'RENDERBUFFER':
        return GlResource$Type$RENDERBUFFER_getInstance();
      case 'SHADER':
        return GlResource$Type$SHADER_getInstance();
      case 'TEXTURE':
        return GlResource$Type$TEXTURE_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.gl.GlResource.Type.' + name);
    }
  }
  GlResource$Type.valueOf_61zpoe$ = GlResource$Type$valueOf;
  Object.defineProperty(GlResource.prototype, 'glRef', {
    get: function () {
      return this.glRef_p5t3zt$_0;
    },
    set: function (glRef) {
      this.glRef_p5t3zt$_0 = glRef;
    }
  });
  Object.defineProperty(GlResource.prototype, 'isValid', {
    get: function () {
      return this.glRef != null;
    }
  });
  GlResource.prototype.delete_evfofk$ = function (ctx) {
    ctx.memoryMgr.deleted_esgzal$(this);
    this.glRef = null;
  };
  GlResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlResource',
    interfaces: []
  };
  function ProgramResource(glRef, ctx) {
    ProgramResource$Companion_getInstance();
    GlResource.call(this, glRef, GlResource$Type$PROGRAM_getInstance(), ctx);
    ctx.memoryMgr.memoryAllocated_927jj9$(this, 1);
  }
  function ProgramResource$Companion() {
    ProgramResource$Companion_instance = this;
  }
  ProgramResource$Companion.prototype.create_evfofk$ = function (ctx) {
    return new ProgramResource(glCreateProgram(), ctx);
  };
  ProgramResource$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ProgramResource$Companion_instance = null;
  function ProgramResource$Companion_getInstance() {
    if (ProgramResource$Companion_instance === null) {
      new ProgramResource$Companion();
    }
    return ProgramResource$Companion_instance;
  }
  ProgramResource.prototype.delete_evfofk$ = function (ctx) {
    glDeleteProgram(this);
    GlResource.prototype.delete_evfofk$.call(this, ctx);
  };
  ProgramResource.prototype.attachShader_j05587$ = function (shader, ctx) {
    glAttachShader(this, shader);
  };
  ProgramResource.prototype.link_evfofk$ = function (ctx) {
    glLinkProgram(this);
    return glGetProgrami(this, GL_LINK_STATUS) === GL_TRUE;
  };
  ProgramResource.prototype.getInfoLog_evfofk$ = function (ctx) {
    return glGetProgramInfoLog(this);
  };
  ProgramResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProgramResource',
    interfaces: [GlResource]
  };
  function RenderbufferResource(glRef, ctx) {
    RenderbufferResource$Companion_getInstance();
    GlResource.call(this, glRef, GlResource$Type$RENDERBUFFER_getInstance(), ctx);
  }
  function RenderbufferResource$Companion() {
    RenderbufferResource$Companion_instance = this;
  }
  RenderbufferResource$Companion.prototype.create_evfofk$ = function (ctx) {
    return new RenderbufferResource(glCreateRenderbuffer(), ctx);
  };
  RenderbufferResource$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var RenderbufferResource$Companion_instance = null;
  function RenderbufferResource$Companion_getInstance() {
    if (RenderbufferResource$Companion_instance === null) {
      new RenderbufferResource$Companion();
    }
    return RenderbufferResource$Companion_instance;
  }
  RenderbufferResource.prototype.delete_evfofk$ = function (ctx) {
    glDeleteRenderbuffer(this);
    GlResource.prototype.delete_evfofk$.call(this, ctx);
  };
  RenderbufferResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RenderbufferResource',
    interfaces: [GlResource]
  };
  function ShaderResource(glRef, ctx) {
    ShaderResource$Companion_getInstance();
    GlResource.call(this, glRef, GlResource$Type$SHADER_getInstance(), ctx);
    ctx.memoryMgr.memoryAllocated_927jj9$(this, 1);
  }
  function ShaderResource$Companion() {
    ShaderResource$Companion_instance = this;
  }
  ShaderResource$Companion.prototype.createFragmentShader_evfofk$ = function (ctx) {
    return new ShaderResource(glCreateShader(GL_FRAGMENT_SHADER), ctx);
  };
  ShaderResource$Companion.prototype.createVertexShader_evfofk$ = function (ctx) {
    return new ShaderResource(glCreateShader(GL_VERTEX_SHADER), ctx);
  };
  ShaderResource$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ShaderResource$Companion_instance = null;
  function ShaderResource$Companion_getInstance() {
    if (ShaderResource$Companion_instance === null) {
      new ShaderResource$Companion();
    }
    return ShaderResource$Companion_instance;
  }
  ShaderResource.prototype.delete_evfofk$ = function (ctx) {
    glDeleteShader(this);
    GlResource.prototype.delete_evfofk$.call(this, ctx);
  };
  ShaderResource.prototype.shaderSource_myr2gy$ = function (source, ctx) {
    glShaderSource(this, source);
  };
  ShaderResource.prototype.compile_evfofk$ = function (ctx) {
    glCompileShader(this);
    return glGetShaderi(this, GL_COMPILE_STATUS) === GL_TRUE;
  };
  ShaderResource.prototype.getInfoLog_evfofk$ = function (ctx) {
    return glGetShaderInfoLog(this);
  };
  ShaderResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderResource',
    interfaces: [GlResource]
  };
  function TextureResource(glRef, target, props, ctx) {
    TextureResource$Companion_getInstance();
    GlResource.call(this, glRef, GlResource$Type$TEXTURE_getInstance(), ctx);
    this.target = target;
    this.props = props;
    this.isLoaded = false;
    this.texUnit_xdyr2s$_0 = -1;
    glBindTexture(GL_TEXTURE_2D, this);
    glTexParameteri(this.target, GL_TEXTURE_MIN_FILTER, this.props.minFilter);
    glTexParameteri(this.target, GL_TEXTURE_MAG_FILTER, this.props.magFilter);
    glTexParameteri(this.target, GL_TEXTURE_WRAP_S, this.props.xWrapping);
    glTexParameteri(this.target, GL_TEXTURE_WRAP_T, this.props.yWrapping);
    if (this.props.anisotropy > 1 && glCapabilities.anisotropicTexFilterInfo.isSupported) {
      var a = numberToInt(glCapabilities.anisotropicTexFilterInfo.maxAnisotropy);
      var b = this.props.anisotropy;
      var anisotropy = Math_0.max(a, b);
      glTexParameteri(this.target, glCapabilities.anisotropicTexFilterInfo.TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
    }
  }
  function TextureResource$Companion() {
    TextureResource$Companion_instance = this;
  }
  TextureResource$Companion.prototype.create_wmc4vc$ = function (target, props, ctx) {
    return new TextureResource(glCreateTexture(), target, props, ctx);
  };
  TextureResource$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TextureResource$Companion_instance = null;
  function TextureResource$Companion_getInstance() {
    if (TextureResource$Companion_instance === null) {
      new TextureResource$Companion();
    }
    return TextureResource$Companion_instance;
  }
  Object.defineProperty(TextureResource.prototype, 'texUnit', {
    get: function () {
      return this.texUnit_xdyr2s$_0;
    },
    set: function (texUnit) {
      this.texUnit_xdyr2s$_0 = texUnit;
    }
  });
  TextureResource.prototype.delete_evfofk$ = function (ctx) {
    glDeleteTexture(this);
    GlResource.prototype.delete_evfofk$.call(this, ctx);
  };
  TextureResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextureResource',
    interfaces: [GlResource]
  };
  function BSpline(degree, factory, copy, mix) {
    this.degree = degree;
    this.factory_o8y0vc$_0 = factory;
    this.copy_1qqihf$_0 = copy;
    this.mix_rjp6wa$_0 = mix;
    this.ctrlPoints = ArrayList_init();
    this.knots_ks2m93$_0 = ArrayList_init();
    this.d_c5f19u$_0 = ArrayList_init();
  }
  BSpline.prototype.addInterpolationEndpoints = function () {
    var tmp$;
    tmp$ = this.degree;
    for (var i = 0; i < tmp$; i++) {
      this.ctrlPoints.add_wxm5ur$(0, first(this.ctrlPoints));
      var $receiver = this.ctrlPoints;
      var element = last(this.ctrlPoints);
      $receiver.add_11rb$(element);
    }
  };
  BSpline.prototype.evaluate_f6p79m$ = function (x, result) {
    if (x <= 0.0)
      this.copy_1qqihf$_0(first(this.ctrlPoints), result);
    else if (x >= 1.0)
      this.copy_1qqihf$_0(last(this.ctrlPoints), result);
    else {
      this.checkTemps_dp4s6j$_0();
      var xx = this.degree + x * (this.ctrlPoints.size - (this.degree * 2 | 0) + 1 | 0);
      this.deBoor_47z15v$_0(numberToInt(xx), xx, result);
    }
    return result;
  };
  BSpline.prototype.deBoor_47z15v$_0 = function (k, t, result) {
    var tmp$, tmp$_0;
    var min = this.degree;
    var max = this.ctrlPoints.size - 1 | 0;
    var clamp$result;
    if (k < min) {
      clamp$result = min;
    }
     else if (k > max) {
      clamp$result = max;
    }
     else {
      clamp$result = k;
    }
    var kk = clamp$result;
    tmp$ = this.degree;
    for (var j = 0; j <= tmp$; j++) {
      this.copy_1qqihf$_0(this.ctrlPoints.get_za3lpa$(j + kk - this.degree | 0), this.d_c5f19u$_0.get_za3lpa$(j));
    }
    tmp$_0 = this.degree;
    for (var r = 1; r <= tmp$_0; r++) {
      for (var j_0 = this.degree; j_0 >= r; j_0--) {
        var alpha = (t - this.knots_ks2m93$_0.get_za3lpa$(j_0 + kk - this.degree | 0)) / (this.knots_ks2m93$_0.get_za3lpa$(j_0 + 1 + kk - r | 0) - this.knots_ks2m93$_0.get_za3lpa$(j_0 + kk - this.degree | 0));
        this.mix_rjp6wa$_0(1.0 - alpha, this.d_c5f19u$_0.get_za3lpa$(j_0 - 1 | 0), alpha, this.d_c5f19u$_0.get_za3lpa$(j_0), this.d_c5f19u$_0.get_za3lpa$(j_0));
      }
    }
    this.copy_1qqihf$_0(this.d_c5f19u$_0.get_za3lpa$(this.degree), result);
  };
  BSpline.prototype.checkTemps_dp4s6j$_0 = function () {
    var tmp$, tmp$_0;
    if (this.knots_ks2m93$_0.size !== (this.ctrlPoints.size + this.degree | 0)) {
      this.knots_ks2m93$_0.clear();
      tmp$ = this.ctrlPoints.size + this.degree | 0;
      for (var i = 0; i < tmp$; i++) {
        this.knots_ks2m93$_0.add_11rb$(i);
      }
    }
    if (this.d_c5f19u$_0.size !== (this.degree + 1 | 0)) {
      this.d_c5f19u$_0.clear();
      tmp$_0 = this.degree;
      for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
        var $receiver = this.d_c5f19u$_0;
        var element = this.factory_o8y0vc$_0();
        $receiver.add_11rb$(element);
      }
    }
  };
  BSpline.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BSpline',
    interfaces: []
  };
  function BSplineVec2f(degree) {
    BSpline.call(this, degree, BSplineVec2f_init$lambda, BSplineVec2f_init$lambda_0, BSplineVec2f_init$lambda_1);
  }
  function BSplineVec2f_init$lambda() {
    return MutableVec2f_init();
  }
  function BSplineVec2f_init$lambda_0(src, dst) {
    dst.set_czzhjp$(src);
    return Unit;
  }
  function BSplineVec2f_init$lambda_1(w0, p0, w1, p1, result) {
    result.x = p0.x * w0 + p1.x * w1;
    result.y = p0.y * w0 + p1.y * w1;
    return Unit;
  }
  BSplineVec2f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BSplineVec2f',
    interfaces: [BSpline]
  };
  function BSplineVec3f(degree) {
    BSpline.call(this, degree, BSplineVec3f_init$lambda, BSplineVec3f_init$lambda_0, BSplineVec3f_init$lambda_1);
  }
  function BSplineVec3f_init$lambda() {
    return MutableVec3f_init();
  }
  function BSplineVec3f_init$lambda_0(src, dst) {
    dst.set_czzhiu$(src);
    return Unit;
  }
  function BSplineVec3f_init$lambda_1(w0, p0, w1, p1, result) {
    result.x = p0.x * w0 + p1.x * w1;
    result.y = p0.y * w0 + p1.y * w1;
    result.z = p0.z * w0 + p1.z * w1;
    return Unit;
  }
  BSplineVec3f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BSplineVec3f',
    interfaces: [BSpline]
  };
  function pointTree$lambda($receiver) {
    return $receiver.x;
  }
  function pointTree$lambda_0($receiver) {
    return $receiver.y;
  }
  function pointTree$lambda_1($receiver) {
    return $receiver.z;
  }
  function pointTree$lambda_2($receiver) {
    return 0.0;
  }
  function pointTree$lambda_3($receiver) {
    return 0.0;
  }
  function pointTree$lambda_4($receiver) {
    return 0.0;
  }
  function pointTree(items, bucketSz) {
    if (bucketSz === void 0)
      bucketSz = 20;
    return new KdTree(items, pointTree$lambda, pointTree$lambda_0, pointTree$lambda_1, pointTree$lambda_2, pointTree$lambda_3, pointTree$lambda_4, bucketSz);
  }
  function KdTreeTraverser() {
  }
  KdTreeTraverser.prototype.onStart_xuddlr$ = function (tree) {
  };
  KdTreeTraverser.prototype.traversalOrder_b90i4h$ = function (tree, left, right) {
    return KdTree$Companion_getInstance().TRAV_NO_PREFERENCE;
  };
  KdTreeTraverser.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KdTreeTraverser',
    interfaces: []
  };
  function InRadiusTraverser() {
    this.result = ArrayList_init();
    this.center = MutableVec3f_init();
    this.radius_lr9jnq$_0 = 1.0;
    this.radiusSqr_0 = 1.0;
  }
  Object.defineProperty(InRadiusTraverser.prototype, 'radius', {
    get: function () {
      return this.radius_lr9jnq$_0;
    },
    set: function (value) {
      this.radius_lr9jnq$_0 = value;
      this.radiusSqr_0 = value * value;
    }
  });
  InRadiusTraverser.prototype.onStart_xuddlr$ = function (tree) {
    this.result.clear();
  };
  InRadiusTraverser.prototype.traversalOrder_b90i4h$ = function (tree, left, right) {
    var dLeft = left.bounds.pointDistanceSqr_czzhiu$(this.center);
    var dRight = right.bounds.pointDistanceSqr_czzhiu$(this.center);
    if (dLeft > this.radiusSqr_0 && dRight > this.radiusSqr_0) {
      return KdTree$Companion_getInstance().TRAV_NONE;
    }
     else if (dLeft > this.radiusSqr_0) {
      return KdTree$Companion_getInstance().TRAV_RIGHT_ONLY;
    }
     else if (dRight > this.radiusSqr_0) {
      return KdTree$Companion_getInstance().TRAV_LEFT_ONLY;
    }
    return KdTree$Companion_getInstance().TRAV_NO_PREFERENCE;
  };
  InRadiusTraverser.prototype.traverseLeaf_pyxcm4$ = function (tree, leaf) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = leaf.indices;
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
      var it = tree.items.get_za3lpa$(i);
      var dx = tree.getX(it) - this.center.x;
      var dy = tree.getY(it) - this.center.y;
      var dz = tree.getZ(it) - this.center.z;
      if (dx * dx + dy * dy + dz * dz < this.radiusSqr_0) {
        this.result.add_11rb$(it);
      }
    }
  };
  InRadiusTraverser.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InRadiusTraverser',
    interfaces: [KdTreeTraverser]
  };
  function InRadiusTraverser_init(center, radius, $this) {
    $this = $this || Object.create(InRadiusTraverser.prototype);
    InRadiusTraverser.call($this);
    $this.center.set_czzhiu$(center);
    $this.radius = radius;
    return $this;
  }
  function KdTree(items, getX, getY, getZ, getSzX, getSzY, getSzZ, bucketSz) {
    KdTree$Companion_getInstance();
    if (bucketSz === void 0)
      bucketSz = 20;
    this.getX = getX;
    this.getY = getY;
    this.getZ = getZ;
    this.getSzX = getSzX;
    this.getSzY = getSzY;
    this.getSzZ = getSzZ;
    this.root = null;
    this.mutItems_0 = ArrayList_init();
    this.cmpX_0 = KdTree$cmpX$lambda(this);
    this.cmpY_0 = KdTree$cmpY$lambda(this);
    this.cmpZ_0 = KdTree$cmpZ$lambda(this);
    this.mutItems_0.addAll_brywnq$(items);
    this.root = new KdTree$Node(this, get_indices(this.mutItems_0), 0, bucketSz);
  }
  Object.defineProperty(KdTree.prototype, 'items', {
    get: function () {
      return this.mutItems_0;
    }
  });
  function KdTree$Companion() {
    KdTree$Companion_instance = this;
    this.TRAV_NO_PREFERENCE = 0;
    this.TRAV_LEFT_FIRST = 1;
    this.TRAV_LEFT_ONLY = 2;
    this.TRAV_RIGHT_FIRST = 3;
    this.TRAV_RIGHT_ONLY = 4;
    this.TRAV_NONE = 5;
  }
  KdTree$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var KdTree$Companion_instance = null;
  function KdTree$Companion_getInstance() {
    if (KdTree$Companion_instance === null) {
      new KdTree$Companion();
    }
    return KdTree$Companion_instance;
  }
  KdTree.prototype.traverse_vqgpt3$ = function (traverser) {
    traverser.onStart_xuddlr$(this);
    this.root.traverse_vqhcsm$(traverser);
  };
  KdTree.prototype.inRadius_29jwp2$ = function (center, radius, result) {
    result.clear();
    this.root.inRadius_29j9pj$(center, radius * radius, result);
  };
  function KdTree$Node($outer, indices, depth, bucketSz) {
    this.$outer = $outer;
    this.indices = indices;
    this.depth = depth;
    this.isLeaf = false;
    this.left = null;
    this.right = null;
    this.bounds = new BoundingBox();
    this.tmpVec_0 = MutableVec3f_init();
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.bounds.batchUpdate = true;
    tmp$ = this.indices;
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
      var it = this.$outer.mutItems_0.get_za3lpa$(i);
      this.bounds.add_czzhiu$(this.tmpVec_0.set_y2kzbl$(this.$outer.getX(it), this.$outer.getY(it), this.$outer.getZ(it)));
      this.tmpVec_0.x = this.tmpVec_0.x + this.$outer.getSzX(it);
      this.tmpVec_0.y = this.tmpVec_0.y + this.$outer.getSzY(it);
      this.tmpVec_0.z = this.tmpVec_0.z + this.$outer.getSzZ(it);
      this.bounds.add_czzhiu$(this.tmpVec_0);
    }
    this.bounds.batchUpdate = false;
    if ((this.indices.last - this.indices.first | 0) < bucketSz) {
      this.isLeaf = true;
      this.left = null;
      this.right = null;
    }
     else {
      this.isLeaf = false;
      var cmp = this.$outer.cmpX_0;
      if (this.bounds.size.y > this.bounds.size.x && this.bounds.size.y > this.bounds.size.z) {
        cmp = this.$outer.cmpY_0;
      }
       else if (this.bounds.size.z > this.bounds.size.x && this.bounds.size.z > this.bounds.size.y) {
        cmp = this.$outer.cmpZ_0;
      }
      var k = this.indices.first + ((this.indices.last - this.indices.first | 0) / 2 | 0) | 0;
      this.partition_0(this.indices.first, this.indices.last, k, cmp);
      this.left = new KdTree$Node(this.$outer, new IntRange(this.indices.first, k), this.depth + 1 | 0, bucketSz);
      this.right = new KdTree$Node(this.$outer, new IntRange(k + 1 | 0, this.indices.last), this.depth + 1 | 0, bucketSz);
    }
  }
  KdTree$Node.prototype.traverse_vqhcsm$ = function (traverser) {
    if (this.isLeaf) {
      traverser.traverseLeaf_pyxcm4$(this.$outer, this);
    }
     else {
      var pref = traverser.traversalOrder_b90i4h$(this.$outer, ensureNotNull(this.left), ensureNotNull(this.right));
      switch (pref) {
        case 5:
          return;
        case 2:
          this.left.traverse_vqhcsm$(traverser);
          break;
        case 4:
          this.right.traverse_vqhcsm$(traverser);
          break;
        case 3:
          this.right.traverse_vqhcsm$(traverser);
          this.left.traverse_vqhcsm$(traverser);
          break;
        default:this.left.traverse_vqhcsm$(traverser);
          this.right.traverse_vqhcsm$(traverser);
          break;
      }
    }
  };
  KdTree$Node.prototype.inRadius_29j9pj$ = function (center, sqrRadius, result) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    if (this.isLeaf) {
      tmp$ = this.indices;
      tmp$_0 = tmp$.first;
      tmp$_1 = tmp$.last;
      tmp$_2 = tmp$.step;
      for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
        var dx = center.x - this.$outer.getX(this.$outer.mutItems_0.get_za3lpa$(i));
        var dy = center.y - this.$outer.getY(this.$outer.mutItems_0.get_za3lpa$(i));
        var dz = center.z - this.$outer.getZ(this.$outer.mutItems_0.get_za3lpa$(i));
        if (dx * dx + dy * dy + dz * dz < sqrRadius) {
          result.add_11rb$(this.$outer.mutItems_0.get_za3lpa$(i));
        }
      }
    }
     else {
      if (ensureNotNull(this.left).bounds.pointDistanceSqr_czzhiu$(center) < sqrRadius) {
        this.left.inRadius_29j9pj$(center, sqrRadius, result);
      }
      if (ensureNotNull(this.right).bounds.pointDistanceSqr_czzhiu$(center) < sqrRadius) {
        this.right.inRadius_29j9pj$(center, sqrRadius, result);
      }
    }
  };
  KdTree$Node.prototype.partition_0 = function (lt, rt, k, cmp) {
    var left = lt;
    var right = rt;
    while (right > left) {
      if ((right - left | 0) > 600) {
        var n = right - left + 1 | 0;
        var i = k - left + 1 | 0;
        var z = Math_0.log(n);
        var x = 2.0 * z / 3.0;
        var s = 0.5 * Math_0.exp(x);
        var x_0 = z * s * (n - s) / n;
        var tmp$ = 0.5 * Math_0.sqrt(x_0);
        var x_1 = i - n / 2.0;
        var sd = tmp$ * Math_0.sign(x_1);
        var a = left;
        var b = numberToInt(k - i * s / n + sd);
        var newLeft = Math_0.max(a, b);
        var a_0 = right;
        var b_0 = numberToInt(k + (n - i | 0) * s / n + sd);
        var newRight = Math_0.min(a_0, b_0);
        this.partition_0(newLeft, newRight, k, cmp);
      }
      var t = this.$outer.mutItems_0.get_za3lpa$(k);
      var i_0 = left;
      var j = right;
      this.swapPts_0(left, k);
      if (cmp(this.$outer.mutItems_0.get_za3lpa$(right), t) > 0) {
        this.swapPts_0(right, left);
      }
      while (i_0 < j) {
        this.swapPts_0(i_0, j);
        i_0 = i_0 + 1 | 0;
        j = j - 1 | 0;
        while (cmp(this.$outer.mutItems_0.get_za3lpa$(i_0), t) < 0) {
          i_0 = i_0 + 1 | 0;
        }
        while (cmp(this.$outer.mutItems_0.get_za3lpa$(j), t) > 0) {
          j = j - 1 | 0;
        }
      }
      if (cmp(this.$outer.mutItems_0.get_za3lpa$(left), t) === 0) {
        this.swapPts_0(left, j);
      }
       else {
        j = j + 1 | 0;
        this.swapPts_0(j, right);
      }
      if (j <= k) {
        left = j + 1 | 0;
      }
      if (k <= j) {
        right = j - 1 | 0;
      }
    }
  };
  KdTree$Node.prototype.swapPts_0 = function (a, b) {
    var tmp = this.$outer.mutItems_0.get_za3lpa$(a);
    this.$outer.mutItems_0.set_wxm5ur$(a, this.$outer.mutItems_0.get_za3lpa$(b));
    this.$outer.mutItems_0.set_wxm5ur$(b, tmp);
  };
  KdTree$Node.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Node',
    interfaces: []
  };
  function KdTree$cmpX$lambda(this$KdTree) {
    return function (a, b) {
      return Kotlin.primitiveCompareTo(this$KdTree.getX(a), this$KdTree.getX(b));
    };
  }
  function KdTree$cmpY$lambda(this$KdTree) {
    return function (a, b) {
      return Kotlin.primitiveCompareTo(this$KdTree.getY(a), this$KdTree.getY(b));
    };
  }
  function KdTree$cmpZ$lambda(this$KdTree) {
    return function (a, b) {
      return Kotlin.primitiveCompareTo(this$KdTree.getZ(a), this$KdTree.getZ(b));
    };
  }
  KdTree.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KdTree',
    interfaces: []
  };
  function Mat4f() {
    Mat4f$Companion_getInstance();
    this.matrix_cl37xc$_0 = new Float32Array(16);
    this.offset_ix8w3y$_0 = 0;
    this.setIdentity();
  }
  Object.defineProperty(Mat4f.prototype, 'matrix', {
    get: function () {
      return this.matrix_cl37xc$_0;
    },
    set: function (matrix) {
      this.matrix_cl37xc$_0 = matrix;
    }
  });
  Object.defineProperty(Mat4f.prototype, 'offset', {
    get: function () {
      return this.offset_ix8w3y$_0;
    },
    set: function (offset) {
      this.offset_ix8w3y$_0 = offset;
    }
  });
  Mat4f.prototype.translate_y2kzbl$ = function (tx, ty, tz) {
    for (var i = 0; i <= 3; i++) {
      var mi = this.offset + i | 0;
      this.matrix[12 + mi | 0] = this.matrix[12 + mi | 0] + (this.matrix[mi] * tx + this.matrix[4 + mi | 0] * ty + this.matrix[8 + mi | 0] * tz);
    }
    return this;
  };
  Mat4f.prototype.translate_czzhiu$ = function (t) {
    return this.translate_y2kzbl$(t.x, t.y, t.z);
  };
  Mat4f.prototype.translate_g84k6w$ = function (tx, ty, tz, result) {
    for (var i = 0; i <= 11; i++) {
      result.matrix[result.offset + i | 0] = this.matrix[this.offset + i | 0];
    }
    for (var i_0 = 0; i_0 <= 3; i_0++) {
      var mi = this.offset + i_0 | 0;
      result.matrix[result.offset + 12 + i_0 | 0] = this.matrix[mi] * tx + this.matrix[4 + mi | 0] * ty + this.matrix[8 + mi | 0] * tz + this.matrix[12 + mi | 0];
    }
    return result;
  };
  Mat4f.prototype.rotate_7b5o5w$ = function (angleDeg, axX, axY, axZ) {
    Mat4f$Companion_getInstance().tmpMatLock_0;
    Mat4f$Companion_getInstance().tmpMatA_0.setRotate_7b5o5w$(angleDeg, axX, axY, axZ);
    this.set_d4zu6j$(this.mul_93v2ma$(Mat4f$Companion_getInstance().tmpMatA_0, Mat4f$Companion_getInstance().tmpMatB_0));
    return this;
  };
  Mat4f.prototype.rotate_ad55pp$ = function (angleDeg, axis) {
    return this.rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
  };
  Mat4f.prototype.rotate_c240mt$ = function (angleDeg, axX, axY, axZ, result) {
    Mat4f$Companion_getInstance().tmpMatLock_0;
    Mat4f$Companion_getInstance().tmpMatA_0.setRotate_7b5o5w$(angleDeg, axX, axY, axZ);
    this.mul_93v2ma$(Mat4f$Companion_getInstance().tmpMatA_0, result);
    return result;
  };
  Mat4f.prototype.rotate_phvhhm$ = function (angleDeg, axis, result) {
    return this.rotate_c240mt$(angleDeg, axis.x, axis.y, axis.z, result);
  };
  Mat4f.prototype.scale_y2kzbl$ = function (sx, sy, sz) {
    for (var i = 0; i <= 3; i++) {
      var mi = this.offset + i | 0;
      this.matrix[mi] = this.matrix[mi] * sx;
      this.matrix[4 + mi | 0] = this.matrix[4 + mi | 0] * sy;
      this.matrix[8 + mi | 0] = this.matrix[8 + mi | 0] * sz;
    }
    return this;
  };
  Mat4f.prototype.scale_czzhiu$ = function (scale) {
    return this.scale_y2kzbl$(scale.x, scale.y, scale.z);
  };
  Mat4f.prototype.scale_g84k6w$ = function (sx, sy, sz, result) {
    for (var i = 0; i <= 3; i++) {
      var smi = result.offset + i | 0;
      var mi = this.offset + i | 0;
      result.matrix[smi] = this.matrix[mi] * sx;
      result.matrix[4 + smi | 0] = this.matrix[4 + mi | 0] * sy;
      result.matrix[8 + smi | 0] = this.matrix[8 + mi | 0] * sz;
      result.matrix[12 + smi | 0] = this.matrix[12 + mi | 0];
    }
    return result;
  };
  Mat4f.prototype.transpose = function () {
    Mat4f$Companion_getInstance().tmpMatLock_0;
    this.set_d4zu6j$(this.transpose_d4zu6j$(Mat4f$Companion_getInstance().tmpMatA_0));
    return this;
  };
  Mat4f.prototype.transpose_d4zu6j$ = function (result) {
    for (var i = 0; i <= 3; i++) {
      var mBase = (i * 4 | 0) + this.offset | 0;
      result.matrix[i + result.offset | 0] = this.matrix[mBase];
      result.matrix[i + 4 + result.offset | 0] = this.matrix[mBase + 1 | 0];
      result.matrix[i + 8 + result.offset | 0] = this.matrix[mBase + 2 | 0];
      result.matrix[i + 12 + result.offset | 0] = this.matrix[mBase + 3 | 0];
    }
    return result;
  };
  Mat4f.prototype.invert = function () {
    var success = {v: false};
    Mat4f$Companion_getInstance().tmpMatLock_0;
    success.v = this.invert_d4zu6j$(Mat4f$Companion_getInstance().tmpMatA_0);
    if (success.v) {
      this.set_d4zu6j$(Mat4f$Companion_getInstance().tmpMatA_0);
    }
    return success.v;
  };
  Mat4f.prototype.invert_d4zu6j$ = function (result) {
    var src0 = this.matrix[this.offset + 0 | 0];
    var src4 = this.matrix[this.offset + 1 | 0];
    var src8 = this.matrix[this.offset + 2 | 0];
    var src12 = this.matrix[this.offset + 3 | 0];
    var src1 = this.matrix[this.offset + 4 | 0];
    var src5 = this.matrix[this.offset + 5 | 0];
    var src9 = this.matrix[this.offset + 6 | 0];
    var src13 = this.matrix[this.offset + 7 | 0];
    var src2 = this.matrix[this.offset + 8 | 0];
    var src6 = this.matrix[this.offset + 9 | 0];
    var src10 = this.matrix[this.offset + 10 | 0];
    var src14 = this.matrix[this.offset + 11 | 0];
    var src3 = this.matrix[this.offset + 12 | 0];
    var src7 = this.matrix[this.offset + 13 | 0];
    var src11 = this.matrix[this.offset + 14 | 0];
    var src15 = this.matrix[this.offset + 15 | 0];
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
    result.matrix[result.offset] = dst0 * invdet;
    result.matrix[1 + result.offset | 0] = dst1 * invdet;
    result.matrix[2 + result.offset | 0] = dst2 * invdet;
    result.matrix[3 + result.offset | 0] = dst3 * invdet;
    result.matrix[4 + result.offset | 0] = dst4 * invdet;
    result.matrix[5 + result.offset | 0] = dst5 * invdet;
    result.matrix[6 + result.offset | 0] = dst6 * invdet;
    result.matrix[7 + result.offset | 0] = dst7 * invdet;
    result.matrix[8 + result.offset | 0] = dst8 * invdet;
    result.matrix[9 + result.offset | 0] = dst9 * invdet;
    result.matrix[10 + result.offset | 0] = dst10 * invdet;
    result.matrix[11 + result.offset | 0] = dst11 * invdet;
    result.matrix[12 + result.offset | 0] = dst12 * invdet;
    result.matrix[13 + result.offset | 0] = dst13 * invdet;
    result.matrix[14 + result.offset | 0] = dst14 * invdet;
    result.matrix[15 + result.offset | 0] = dst15 * invdet;
    return true;
  };
  Mat4f.prototype.transform_w1lst9$ = function (vec, w) {
    if (w === void 0)
      w = 1.0;
    var x = vec.x * this.get_vux9f0$(0, 0) + vec.y * this.get_vux9f0$(0, 1) + vec.z * this.get_vux9f0$(0, 2) + w * this.get_vux9f0$(0, 3);
    var y = vec.x * this.get_vux9f0$(1, 0) + vec.y * this.get_vux9f0$(1, 1) + vec.z * this.get_vux9f0$(1, 2) + w * this.get_vux9f0$(1, 3);
    var z = vec.x * this.get_vux9f0$(2, 0) + vec.y * this.get_vux9f0$(2, 1) + vec.z * this.get_vux9f0$(2, 2) + w * this.get_vux9f0$(2, 3);
    return vec.set_y2kzbl$(x, y, z);
  };
  Mat4f.prototype.transform_a6wx89$ = function (vec, w, result) {
    if (w === void 0)
      w = 1.0;
    result.x = vec.x * this.get_vux9f0$(0, 0) + vec.y * this.get_vux9f0$(0, 1) + vec.z * this.get_vux9f0$(0, 2) + w * this.get_vux9f0$(0, 3);
    result.y = vec.x * this.get_vux9f0$(1, 0) + vec.y * this.get_vux9f0$(1, 1) + vec.z * this.get_vux9f0$(1, 2) + w * this.get_vux9f0$(1, 3);
    result.z = vec.x * this.get_vux9f0$(2, 0) + vec.y * this.get_vux9f0$(2, 1) + vec.z * this.get_vux9f0$(2, 2) + w * this.get_vux9f0$(2, 3);
    return result;
  };
  Mat4f.prototype.transform_5s4mpv$ = function (vec) {
    var x = vec.x * this.get_vux9f0$(0, 0) + vec.y * this.get_vux9f0$(0, 1) + vec.z * this.get_vux9f0$(0, 2) + vec.w * this.get_vux9f0$(0, 3);
    var y = vec.x * this.get_vux9f0$(1, 0) + vec.y * this.get_vux9f0$(1, 1) + vec.z * this.get_vux9f0$(1, 2) + vec.w * this.get_vux9f0$(1, 3);
    var z = vec.x * this.get_vux9f0$(2, 0) + vec.y * this.get_vux9f0$(2, 1) + vec.z * this.get_vux9f0$(2, 2) + vec.w * this.get_vux9f0$(2, 3);
    var w = vec.x * this.get_vux9f0$(3, 0) + vec.y * this.get_vux9f0$(3, 1) + vec.z * this.get_vux9f0$(3, 2) + vec.w * this.get_vux9f0$(3, 3);
    return vec.set_7b5o5w$(x, y, z, w);
  };
  Mat4f.prototype.transform_uzu8ww$ = function (vec, result) {
    result.x = vec.x * this.get_vux9f0$(0, 0) + vec.y * this.get_vux9f0$(0, 1) + vec.z * this.get_vux9f0$(0, 2) + vec.w * this.get_vux9f0$(0, 3);
    result.y = vec.x * this.get_vux9f0$(1, 0) + vec.y * this.get_vux9f0$(1, 1) + vec.z * this.get_vux9f0$(1, 2) + vec.w * this.get_vux9f0$(1, 3);
    result.z = vec.x * this.get_vux9f0$(2, 0) + vec.y * this.get_vux9f0$(2, 1) + vec.z * this.get_vux9f0$(2, 2) + vec.w * this.get_vux9f0$(2, 3);
    result.w = vec.x * this.get_vux9f0$(3, 0) + vec.y * this.get_vux9f0$(3, 1) + vec.z * this.get_vux9f0$(3, 2) + vec.w * this.get_vux9f0$(3, 3);
    return result;
  };
  Mat4f.prototype.mul_d4zu6j$ = function (other) {
    Mat4f$Companion_getInstance().tmpMatLock_0;
    this.mul_93v2ma$(other, Mat4f$Companion_getInstance().tmpMatA_0);
    this.set_d4zu6j$(Mat4f$Companion_getInstance().tmpMatA_0);
    return this;
  };
  Mat4f.prototype.mul_93v2ma$ = function (other, result) {
    for (var i = 0; i <= 3; i++) {
      for (var j = 0; j <= 3; j++) {
        var x = 0.0;
        for (var k = 0; k <= 3; k++) {
          x += this.matrix[this.offset + j + (k * 4 | 0) | 0] * other.matrix[other.offset + (i * 4 | 0) + k | 0];
        }
        result.matrix[result.offset + (i * 4 | 0) + j | 0] = x;
      }
    }
    return result;
  };
  Mat4f.prototype.set_d4zu6j$ = function (other) {
    for (var i = 0; i <= 15; i++) {
      this.matrix[this.offset + i | 0] = other.matrix[other.offset + i | 0];
    }
    return this;
  };
  Mat4f.prototype.set_hcyabg$ = function (floats) {
    for (var i = 0; i <= 15; i++) {
      this.matrix[this.offset + i | 0] = floats.get_za3lpa$(i);
    }
  };
  Mat4f.prototype.setIdentity = function () {
    for (var i = 1; i <= 15; i++) {
      this.matrix[this.offset + i | 0] = 0.0;
    }
    for (var i_0 = 0; i_0 <= 15; i_0 += 5) {
      this.matrix[this.offset + i_0 | 0] = 1.0;
    }
    return this;
  };
  Mat4f.prototype.setRotate_7b5o5w$ = function (rotA, axX, axY, axZ) {
    var a = rotA * package$math.DEG_2_RAD;
    var x = axX;
    var y = axY;
    var z = axZ;
    this.matrix[this.offset + 3 | 0] = 0.0;
    this.matrix[this.offset + 7 | 0] = 0.0;
    this.matrix[this.offset + 11 | 0] = 0.0;
    this.matrix[this.offset + 12 | 0] = 0.0;
    this.matrix[this.offset + 13 | 0] = 0.0;
    this.matrix[this.offset + 14 | 0] = 0.0;
    this.matrix[this.offset + 15 | 0] = 1.0;
    var s = Math_0.sin(a);
    var c = Math_0.cos(a);
    if (1.0 === x && 0.0 === y && 0.0 === z) {
      this.matrix[this.offset + 5 | 0] = c;
      this.matrix[this.offset + 10 | 0] = c;
      this.matrix[this.offset + 6 | 0] = s;
      this.matrix[this.offset + 9 | 0] = -s;
      this.matrix[this.offset + 1 | 0] = 0.0;
      this.matrix[this.offset + 2 | 0] = 0.0;
      this.matrix[this.offset + 4 | 0] = 0.0;
      this.matrix[this.offset + 8 | 0] = 0.0;
      this.matrix[this.offset + 0 | 0] = 1.0;
    }
     else if (0.0 === x && 1.0 === y && 0.0 === z) {
      this.matrix[this.offset + 0 | 0] = c;
      this.matrix[this.offset + 10 | 0] = c;
      this.matrix[this.offset + 8 | 0] = s;
      this.matrix[this.offset + 2 | 0] = -s;
      this.matrix[this.offset + 1 | 0] = 0.0;
      this.matrix[this.offset + 4 | 0] = 0.0;
      this.matrix[this.offset + 6 | 0] = 0.0;
      this.matrix[this.offset + 9 | 0] = 0.0;
      this.matrix[this.offset + 5 | 0] = 1.0;
    }
     else if (0.0 === x && 0.0 === y && 1.0 === z) {
      this.matrix[this.offset + 0 | 0] = c;
      this.matrix[this.offset + 5 | 0] = c;
      this.matrix[this.offset + 1 | 0] = s;
      this.matrix[this.offset + 4 | 0] = -s;
      this.matrix[this.offset + 2 | 0] = 0.0;
      this.matrix[this.offset + 6 | 0] = 0.0;
      this.matrix[this.offset + 8 | 0] = 0.0;
      this.matrix[this.offset + 9 | 0] = 0.0;
      this.matrix[this.offset + 10 | 0] = 1.0;
    }
     else {
      var x_0 = x * x + y * y + z * z;
      var len = Math_0.sqrt(x_0);
      var $receiver = len - 1.0;
      if (!(Math_0.abs($receiver) < 1.0E-5)) {
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
      this.matrix[this.offset + 0 | 0] = x * x * nc + c;
      this.matrix[this.offset + 4 | 0] = xy * nc - zs;
      this.matrix[this.offset + 8 | 0] = zx * nc + ys;
      this.matrix[this.offset + 1 | 0] = xy * nc + zs;
      this.matrix[this.offset + 5 | 0] = y * y * nc + c;
      this.matrix[this.offset + 9 | 0] = yz * nc - xs;
      this.matrix[this.offset + 2 | 0] = zx * nc - ys;
      this.matrix[this.offset + 6 | 0] = yz * nc + xs;
      this.matrix[this.offset + 10 | 0] = z * z * nc + c;
    }
    return this;
  };
  Mat4f.prototype.setRotate_czzhhz$ = function (quaternion) {
    var r = quaternion.w;
    var i = quaternion.x;
    var j = quaternion.y;
    var k = quaternion.z;
    var x = r * r + i * i + j * j + k * k;
    var s = Math_0.sqrt(x);
    s = 1.0 / (s * s);
    this.set_n0b4r3$(0, 0, 1 - 2 * s * (j * j + k * k));
    this.set_n0b4r3$(0, 1, 2 * s * (i * j - k * r));
    this.set_n0b4r3$(0, 2, 2 * s * (i * k + j * r));
    this.set_n0b4r3$(0, 3, 0.0);
    this.set_n0b4r3$(1, 0, 2 * s * (i * j + k * r));
    this.set_n0b4r3$(1, 1, 1 - 2 * s * (i * i + k * k));
    this.set_n0b4r3$(1, 2, 2 * s * (j * k - i * r));
    this.set_n0b4r3$(1, 3, 0.0);
    this.set_n0b4r3$(2, 0, 2 * s * (i * k - j * r));
    this.set_n0b4r3$(2, 1, 2 * s * (j * k + i * r));
    this.set_n0b4r3$(2, 2, 1 - 2 * s * (i * i + j * j));
    this.set_n0b4r3$(2, 3, 0.0);
    this.set_n0b4r3$(3, 0, 0.0);
    this.set_n0b4r3$(3, 1, 0.0);
    this.set_n0b4r3$(3, 2, 0.0);
    this.set_n0b4r3$(3, 3, 1.0);
    return this;
  };
  Mat4f.prototype.setLookAt_n440fu$ = function (position, lookAt, up) {
    var fx = lookAt.x - position.x;
    var fy = lookAt.y - position.y;
    var fz = lookAt.z - position.z;
    var x = fx * fx + fy * fy + fz * fz;
    var rlf = 1.0 / Math_0.sqrt(x);
    fx *= rlf;
    fy *= rlf;
    fz *= rlf;
    var sx = fy * up.z - fz * up.y;
    var sy = fz * up.x - fx * up.z;
    var sz = fx * up.y - fy * up.x;
    var x_0 = sx * sx + sy * sy + sz * sz;
    var rls = 1.0 / Math_0.sqrt(x_0);
    sx *= rls;
    sy *= rls;
    sz *= rls;
    var ux = sy * fz - sz * fy;
    var uy = sz * fx - sx * fz;
    var uz = sx * fy - sy * fx;
    this.matrix[this.offset + 0 | 0] = sx;
    this.matrix[this.offset + 1 | 0] = ux;
    this.matrix[this.offset + 2 | 0] = -fx;
    this.matrix[this.offset + 3 | 0] = 0.0;
    this.matrix[this.offset + 4 | 0] = sy;
    this.matrix[this.offset + 5 | 0] = uy;
    this.matrix[this.offset + 6 | 0] = -fy;
    this.matrix[this.offset + 7 | 0] = 0.0;
    this.matrix[this.offset + 8 | 0] = sz;
    this.matrix[this.offset + 9 | 0] = uz;
    this.matrix[this.offset + 10 | 0] = -fz;
    this.matrix[this.offset + 11 | 0] = 0.0;
    this.matrix[this.offset + 12 | 0] = 0.0;
    this.matrix[this.offset + 13 | 0] = 0.0;
    this.matrix[this.offset + 14 | 0] = 0.0;
    this.matrix[this.offset + 15 | 0] = 1.0;
    return this.translate_y2kzbl$(-position.x, -position.y, -position.z);
  };
  Mat4f.prototype.setOrthographic_w8lrqs$ = function (left, right, bottom, top, near, far) {
    if (left === right) {
      throw IllegalArgumentException_init('left == right');
    }
    if (bottom === top) {
      throw IllegalArgumentException_init('bottom == top');
    }
    if (near === far) {
      throw IllegalArgumentException_init('near == far');
    }
    var width = 1.0 / (right - left);
    var height = 1.0 / (top - bottom);
    var depth = 1.0 / (far - near);
    var x = 2.0 * width;
    var y = 2.0 * height;
    var z = -2.0 * depth;
    var tx = -(right + left) * width;
    var ty = -(top + bottom) * height;
    var tz = -(far + near) * depth;
    this.matrix[this.offset + 0 | 0] = x;
    this.matrix[this.offset + 5 | 0] = y;
    this.matrix[this.offset + 10 | 0] = z;
    this.matrix[this.offset + 12 | 0] = tx;
    this.matrix[this.offset + 13 | 0] = ty;
    this.matrix[this.offset + 14 | 0] = tz;
    this.matrix[this.offset + 15 | 0] = 1.0;
    this.matrix[this.offset + 1 | 0] = 0.0;
    this.matrix[this.offset + 2 | 0] = 0.0;
    this.matrix[this.offset + 3 | 0] = 0.0;
    this.matrix[this.offset + 4 | 0] = 0.0;
    this.matrix[this.offset + 6 | 0] = 0.0;
    this.matrix[this.offset + 7 | 0] = 0.0;
    this.matrix[this.offset + 8 | 0] = 0.0;
    this.matrix[this.offset + 9 | 0] = 0.0;
    this.matrix[this.offset + 11 | 0] = 0.0;
    return this;
  };
  Mat4f.prototype.setPerspective_7b5o5w$ = function (fovy, aspect, near, far) {
    var x = fovy * (math.PI / 360.0);
    var f = 1.0 / Math_0.tan(x);
    var rangeReciprocal = 1.0 / (near - far);
    this.matrix[this.offset + 0 | 0] = f / aspect;
    this.matrix[this.offset + 1 | 0] = 0.0;
    this.matrix[this.offset + 2 | 0] = 0.0;
    this.matrix[this.offset + 3 | 0] = 0.0;
    this.matrix[this.offset + 4 | 0] = 0.0;
    this.matrix[this.offset + 5 | 0] = f;
    this.matrix[this.offset + 6 | 0] = 0.0;
    this.matrix[this.offset + 7 | 0] = 0.0;
    this.matrix[this.offset + 8 | 0] = 0.0;
    this.matrix[this.offset + 9 | 0] = 0.0;
    this.matrix[this.offset + 10 | 0] = (far + near) * rangeReciprocal;
    this.matrix[this.offset + 11 | 0] = -1.0;
    this.matrix[this.offset + 12 | 0] = 0.0;
    this.matrix[this.offset + 13 | 0] = 0.0;
    this.matrix[this.offset + 14 | 0] = 2.0 * far * near * rangeReciprocal;
    this.matrix[this.offset + 15 | 0] = 0.0;
    return this;
  };
  Mat4f.prototype.get_za3lpa$ = function (i) {
    return this.matrix[this.offset + i | 0];
  };
  Mat4f.prototype.get_vux9f0$ = function (row, col) {
    return this.matrix[this.offset + (col * 4 | 0) + row | 0];
  };
  Mat4f.prototype.set_24o109$ = function (i, value) {
    this.matrix[this.offset + i | 0] = value;
  };
  Mat4f.prototype.set_n0b4r3$ = function (row, col, value) {
    this.matrix[this.offset + (col * 4 | 0) + row | 0] = value;
  };
  Mat4f.prototype.setColVec_gdg6t7$ = function (col, vec, w) {
    this.set_n0b4r3$(0, col, vec.x);
    this.set_n0b4r3$(1, col, vec.y);
    this.set_n0b4r3$(2, col, vec.z);
    this.set_n0b4r3$(3, col, w);
  };
  Mat4f.prototype.setColVec_ky00rj$ = function (col, value) {
    this.set_n0b4r3$(0, col, value.x);
    this.set_n0b4r3$(1, col, value.y);
    this.set_n0b4r3$(2, col, value.z);
    this.set_n0b4r3$(3, col, value.w);
  };
  Mat4f.prototype.getColVec_8irwu1$ = function (col, result) {
    result.x = this.get_vux9f0$(0, col);
    result.y = this.get_vux9f0$(1, col);
    result.z = this.get_vux9f0$(2, col);
    result.w = this.get_vux9f0$(3, col);
  };
  Mat4f.prototype.toBuffer_he122g$ = function (buffer) {
    buffer.put_kgymra$(this.matrix, this.offset, 16);
    buffer.flip();
    return buffer;
  };
  function Mat4f$Companion() {
    Mat4f$Companion_instance = this;
    this.tmpMatLock_0 = new Any();
    this.tmpMatA_0 = new Mat4f();
    this.tmpMatB_0 = new Mat4f();
  }
  Mat4f$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Mat4f$Companion_instance = null;
  function Mat4f$Companion_getInstance() {
    if (Mat4f$Companion_instance === null) {
      new Mat4f$Companion();
    }
    return Mat4f$Companion_instance;
  }
  Mat4f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mat4f',
    interfaces: []
  };
  function Mat4fStack(stackSize) {
    Mat4fStack$Companion_getInstance();
    if (stackSize === void 0)
      stackSize = Mat4fStack$Companion_getInstance().DEFAULT_STACK_SIZE;
    Mat4f.call(this);
    this.stackSize = stackSize;
    this.stackIndex_rxi7m3$_0 = 0;
    this.matrix = new Float32Array(16 * this.stackSize | 0);
    this.setIdentity();
  }
  function Mat4fStack$Companion() {
    Mat4fStack$Companion_instance = this;
    this.DEFAULT_STACK_SIZE = 32;
  }
  Mat4fStack$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Mat4fStack$Companion_instance = null;
  function Mat4fStack$Companion_getInstance() {
    if (Mat4fStack$Companion_instance === null) {
      new Mat4fStack$Companion();
    }
    return Mat4fStack$Companion_instance;
  }
  Object.defineProperty(Mat4fStack.prototype, 'stackIndex', {
    get: function () {
      return this.stackIndex_rxi7m3$_0;
    },
    set: function (value) {
      this.stackIndex_rxi7m3$_0 = value;
      this.offset = value * 16 | 0;
    }
  });
  Mat4fStack.prototype.push = function () {
    if (this.stackIndex >= this.stackSize) {
      throw new KoolException('Matrix stack overflow');
    }
    for (var i = 0; i <= 15; i++) {
      this.matrix[this.offset + 16 + i | 0] = this.matrix[this.offset + i | 0];
    }
    this.stackIndex = this.stackIndex + 1 | 0;
  };
  Mat4fStack.prototype.pop = function () {
    if (this.stackIndex <= 0) {
      throw new KoolException('Matrix stack underflow');
    }
    this.stackIndex = this.stackIndex - 1 | 0;
  };
  Mat4fStack.prototype.reset = function () {
    this.stackIndex = 0;
    this.setIdentity();
    return this;
  };
  Mat4fStack.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mat4fStack',
    interfaces: [Mat4f]
  };
  var DEG_2_RAD;
  var RAD_2_DEG;
  var toDeg = defineInlineFunction('kool.de.fabmax.kool.math.toDeg_81szk$', wrapFunction(function () {
    var math = _.de.fabmax.kool.math;
    return function ($receiver) {
      return $receiver * math.RAD_2_DEG;
    };
  }));
  var toRad = defineInlineFunction('kool.de.fabmax.kool.math.toRad_81szk$', wrapFunction(function () {
    var math = _.de.fabmax.kool.math;
    return function ($receiver) {
      return $receiver * math.DEG_2_RAD;
    };
  }));
  var toDeg_0 = defineInlineFunction('kool.de.fabmax.kool.math.toDeg_yrwdxr$', wrapFunction(function () {
    var math = _.de.fabmax.kool.math;
    return function ($receiver) {
      return $receiver * math.RAD_2_DEG;
    };
  }));
  var toRad_0 = defineInlineFunction('kool.de.fabmax.kool.math.toRad_yrwdxr$', wrapFunction(function () {
    var math = _.de.fabmax.kool.math;
    return function ($receiver) {
      return $receiver * math.DEG_2_RAD;
    };
  }));
  var isEqual = defineInlineFunction('kool.de.fabmax.kool.math.isEqual_dleff0$', wrapFunction(function () {
    var Math_0 = Math;
    return function (a, b) {
      var $receiver = a - b;
      return Math_0.abs($receiver) < 1.0E-5;
    };
  }));
  var isEqual_0 = defineInlineFunction('kool.de.fabmax.kool.math.isEqual_lu1900$', wrapFunction(function () {
    var Math_0 = Math;
    return function (a, b) {
      var $receiver = a - b;
      return Math_0.abs($receiver) < 1.0E-10;
    };
  }));
  var isZero = defineInlineFunction('kool.de.fabmax.kool.math.isZero_81szk$', wrapFunction(function () {
    var Math_0 = Math;
    return function ($receiver) {
      return Math_0.abs($receiver) < 1.0E-5;
    };
  }));
  var isZero_0 = defineInlineFunction('kool.de.fabmax.kool.math.isZero_yrwdxr$', wrapFunction(function () {
    var Math_0 = Math;
    return function ($receiver) {
      return Math_0.abs($receiver) < 1.0E-10;
    };
  }));
  var clamp = defineInlineFunction('kool.de.fabmax.kool.math.clamp_e4yvb3$', function ($receiver, min, max) {
    if ($receiver < min)
      return min;
    else if ($receiver > max)
      return max;
    else
      return $receiver;
  });
  var clamp_0 = defineInlineFunction('kool.de.fabmax.kool.math.clamp_wj6e7o$', function ($receiver, min, max) {
    if (min === void 0)
      min = 0.0;
    if (max === void 0)
      max = 1.0;
    if ($receiver < min)
      return min;
    else if ($receiver > max)
      return max;
    else
      return $receiver;
  });
  var clamp_1 = defineInlineFunction('kool.de.fabmax.kool.math.clamp_nig4hr$', function ($receiver, min, max) {
    if (min === void 0)
      min = 0.0;
    if (max === void 0)
      max = 1.0;
    if ($receiver < min)
      return min;
    else if ($receiver > max)
      return max;
    else
      return $receiver;
  });
  function Plane() {
    this.p = MutableVec3f_init();
    this.n = MutableVec3f_init_0(Vec3f$Companion_getInstance().Y_AXIS);
  }
  Plane.prototype.intersectionPoint_m2314x$ = function (result, ray) {
    var denom = this.n.dot_czzhiu$(ray.direction);
    if (!(Math_0.abs(denom) < 1.0E-5)) {
      var t = this.p.subtract_2gj7b4$(ray.origin, result).dot_czzhiu$(this.n) / denom;
      result.set_czzhiu$(ray.direction).scale_mx4ult$(t).add_czzhiu$(ray.origin);
      return t >= 0;
    }
    return false;
  };
  Plane.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Plane',
    interfaces: []
  };
  function PointDistribution() {
  }
  PointDistribution.prototype.nextPoints_za3lpa$ = function (n) {
    var points = ArrayList_init();
    for (var i = 1; i <= n; i++) {
      var element = this.nextPoint();
      points.add_11rb$(element);
    }
    return points;
  };
  PointDistribution.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PointDistribution',
    interfaces: []
  };
  function CubicPointDistribution(size, center, random) {
    if (size === void 0)
      size = 1.0;
    if (center === void 0)
      center = Vec3f$Companion_getInstance().ZERO;
    if (random === void 0)
      random = defaultRandomInstance;
    PointDistribution.call(this);
    this.size = size;
    this.center = center;
    this.random = random;
    this.s_0 = this.size * 0.5;
  }
  CubicPointDistribution.prototype.nextPoint = function () {
    return new Vec3f(this.center.x + this.random.randomF_dleff0$(-this.s_0, this.s_0), this.center.y + this.random.randomF_dleff0$(-this.s_0, this.s_0), this.center.z + this.random.randomF_dleff0$(-this.s_0, this.s_0));
  };
  CubicPointDistribution.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CubicPointDistribution',
    interfaces: [PointDistribution]
  };
  function SphericalPointDistribution(radius, center, random) {
    if (radius === void 0)
      radius = 1.0;
    if (center === void 0)
      center = Vec3f$Companion_getInstance().ZERO;
    if (random === void 0)
      random = defaultRandomInstance;
    PointDistribution.call(this);
    this.radius = radius;
    this.center = center;
    this.random = random;
    this.rSqr_0 = this.radius * this.radius;
  }
  SphericalPointDistribution.prototype.nextPoint = function () {
    while (true) {
      var x = this.random.randomF_dleff0$(-this.radius, this.radius);
      var y = this.random.randomF_dleff0$(-this.radius, this.radius);
      var z = this.random.randomF_dleff0$(-this.radius, this.radius);
      if (x * x + y * y + z * z < this.rSqr_0) {
        return new Vec3f(this.center.x + x, this.center.y + y, this.center.z + z);
      }
    }
  };
  SphericalPointDistribution.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SphericalPointDistribution',
    interfaces: [PointDistribution]
  };
  var defaultRandomInstance;
  function randomI() {
    return defaultRandomInstance.randomI();
  }
  function randomI_0(min, max) {
    return defaultRandomInstance.randomI_vux9f0$(min, max);
  }
  function randomD() {
    return defaultRandomInstance.randomD();
  }
  function randomD_0(min, max) {
    return defaultRandomInstance.randomD_lu1900$(min, max);
  }
  function randomF() {
    return defaultRandomInstance.randomF();
  }
  function randomF_0(min, max) {
    return defaultRandomInstance.randomF_dleff0$(min, max);
  }
  function Random(seed) {
    this.x_i5bv4$_0 = seed;
    this.y_i5bu9$_0 = 362436000;
    this.z_i5bte$_0 = 521288629;
    this.c_i5cd7$_0 = 7654321;
    this.lock_un6ybj$_0 = new Any();
  }
  Random.prototype.randomI = function () {
    this.x_i5bv4$_0 = (69069 * this.x_i5bv4$_0 | 0) + 12345 | 0;
    this.y_i5bu9$_0 = this.y_i5bu9$_0 ^ this.y_i5bu9$_0 << 13;
    this.y_i5bu9$_0 = this.y_i5bu9$_0 ^ this.y_i5bu9$_0 >> 17;
    this.y_i5bu9$_0 = this.y_i5bu9$_0 ^ this.y_i5bu9$_0 << 5;
    var t = Kotlin.Long.fromInt(698769069).multiply(Kotlin.Long.fromInt(this.z_i5bte$_0)).add(Kotlin.Long.fromInt(this.c_i5cd7$_0));
    this.c_i5cd7$_0 = t.shiftRight(32).toInt();
    this.z_i5bte$_0 = t.toInt();
    return this.x_i5bv4$_0 + this.y_i5bu9$_0 + this.z_i5bte$_0 | 0;
  };
  Random.prototype.randomI_vux9f0$ = function (min, max) {
    return abs(this.randomI()) % (max - min + 1 | 0) + min | 0;
  };
  Random.prototype.randomF = function () {
    return abs(this.randomI()) / kotlin_js_internal_IntCompanionObject.MAX_VALUE;
  };
  Random.prototype.randomF_dleff0$ = function (min, max) {
    return this.randomF() * (max - min) + min;
  };
  Random.prototype.randomD = function () {
    var l = abs_0(Kotlin.Long.fromInt(this.randomI())).shiftLeft(32).or(abs_0(Kotlin.Long.fromInt(this.randomI())));
    return abs_0(l).toNumber() / (new Kotlin.Long(-1, 2147483647)).toNumber();
  };
  Random.prototype.randomD_lu1900$ = function (min, max) {
    return this.randomD() * (max - min) + min;
  };
  Random.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Random',
    interfaces: []
  };
  function Ray() {
    this.origin = MutableVec3f_init();
    this.direction = MutableVec3f_init();
  }
  Ray.prototype.set_nvyeur$ = function (other) {
    this.origin.set_czzhiu$(other.origin);
    this.direction.set_czzhiu$(other.direction);
  };
  Ray.prototype.setFromLookAt_4lfkt4$ = function (origin, lookAt) {
    this.origin.set_czzhiu$(origin);
    this.direction.set_czzhiu$(lookAt).subtract_czzhiu$(origin).norm();
  };
  Ray.prototype.nearestPointOnRay_ud3oas$ = function (result, point) {
    var d = (point.dot_czzhiu$(this.direction) - this.origin.dot_czzhiu$(this.direction)) / this.direction.dot_czzhiu$(this.direction);
    if (d > 0) {
      result.set_czzhiu$(this.direction).scale_mx4ult$(d).add_czzhiu$(this.origin);
    }
     else {
      result.set_czzhiu$(this.origin);
    }
  };
  Ray.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Ray',
    interfaces: []
  };
  function RayTest() {
    this.ray = new Ray();
    this.hitPosition = MutableVec3f_init();
    this.hitPositionLocal = MutableVec3f_init();
    this.hitNode = null;
    this.hitDistanceSqr = kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
  }
  Object.defineProperty(RayTest.prototype, 'isHit', {
    get: function () {
      return this.hitDistanceSqr < kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
    }
  });
  RayTest.prototype.clear = function () {
    this.hitPosition.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.hitPositionLocal.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.hitNode = null;
    this.hitDistanceSqr = kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
  };
  RayTest.prototype.computeHitPosition = function () {
    if (this.isHit) {
      var x = this.hitDistanceSqr;
      var dist = Math_0.sqrt(x);
      this.hitPosition.set_czzhiu$(this.ray.direction).norm().scale_mx4ult$(dist).add_czzhiu$(this.ray.origin);
    }
  };
  RayTest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RayTest',
    interfaces: []
  };
  function add(a, b) {
    return a.add_q2ruao$(b, MutableVec2f_init());
  }
  function add_0(a, b) {
    return a.add_2gj7b4$(b, MutableVec3f_init());
  }
  function add_1(a, b) {
    return a.add_uzu8ww$(b, MutableVec4f_init());
  }
  function subtract(a, b) {
    return a.subtract_q2ruao$(b, MutableVec2f_init());
  }
  function subtract_0(a, b) {
    return a.subtract_2gj7b4$(b, MutableVec3f_init());
  }
  function subtract_1(a, b) {
    return a.subtract_uzu8ww$(b, MutableVec4f_init());
  }
  function scale(a, fac) {
    return a.scale_749b9g$(fac, MutableVec2f_init());
  }
  function scale_0(a, fac) {
    return a.scale_749b8l$(fac, MutableVec3f_init());
  }
  function scale_1(a, fac) {
    return a.scale_749b7q$(fac, MutableVec4f_init());
  }
  function norm(a) {
    return a.norm_5s4mrl$(MutableVec2f_init());
  }
  function norm_0(a) {
    return a.norm_5s4mqq$(MutableVec3f_init());
  }
  function cross(a, b) {
    return a.cross_2gj7b4$(b, MutableVec3f_init());
  }
  var slerpTmpA;
  var slerpTmpB;
  var slerpTmpC;
  function slerp(quatA, quatB, f, result) {
    quatA.norm_5s4mpv$(slerpTmpA);
    quatB.norm_5s4mpv$(slerpTmpB);
    var clamp$result;
    if (f < 0.0) {
      clamp$result = 0.0;
    }
     else if (f > 1.0) {
      clamp$result = 1.0;
    }
     else {
      clamp$result = f;
    }
    var t = clamp$result;
    var $receiver = slerpTmpA.dot_czzhhz$(slerpTmpB);
    var min = -1.0;
    var clamp$result_0;
    if ($receiver < min) {
      clamp$result_0 = min;
    }
     else if ($receiver > 1.0) {
      clamp$result_0 = 1.0;
    }
     else {
      clamp$result_0 = $receiver;
    }
    var dot = clamp$result_0;
    if (dot < 0) {
      slerpTmpA.scale_mx4ult$(-1.0);
      dot = -dot;
    }
    if (dot > 0.9995) {
      slerpTmpB.subtract_uzu8ww$(slerpTmpA, result).scale_mx4ult$(t).add_czzhhz$(slerpTmpA).norm();
    }
     else {
      var x = dot;
      var theta0 = Math_0.acos(x);
      var theta = theta0 * t;
      slerpTmpA.scale_749b7q$(-dot, slerpTmpC).add_czzhhz$(slerpTmpB).norm();
      slerpTmpA.scale_mx4ult$(Math_0.cos(theta));
      slerpTmpC.scale_mx4ult$(Math_0.sin(theta));
      result.set_czzhhz$(slerpTmpA).add_czzhhz$(slerpTmpC);
    }
    return result;
  }
  function Vec2f(x, y) {
    Vec2f$Companion_getInstance();
    this.xField = x;
    this.yField = y;
  }
  Object.defineProperty(Vec2f.prototype, 'x', {
    get: function () {
      return this.xField;
    }
  });
  Object.defineProperty(Vec2f.prototype, 'y', {
    get: function () {
      return this.yField;
    }
  });
  Vec2f.prototype.add_q2ruao$ = function (other, result) {
    return result.set_czzhjp$(this).add_czzhjp$(other);
  };
  Vec2f.prototype.distance_czzhjp$ = function (other) {
    var x = this.sqrDistance_czzhjp$(other);
    return Math_0.sqrt(x);
  };
  Vec2f.prototype.dot_czzhjp$ = function (other) {
    return this.x * other.x + this.y * other.y;
  };
  Vec2f.prototype.isEqual_czzhjp$ = function (other) {
    var $receiver = this.x - other.x;
    var tmp$ = Math_0.abs($receiver) < 1.0E-5;
    if (tmp$) {
      var $receiver_0 = this.y - other.y;
      tmp$ = Math_0.abs($receiver_0) < 1.0E-5;
    }
    return tmp$;
  };
  Vec2f.prototype.length = function () {
    var x = this.sqrLength();
    return Math_0.sqrt(x);
  };
  Vec2f.prototype.norm_5s4mrl$ = function (result) {
    return result.set_czzhjp$(this).norm();
  };
  Vec2f.prototype.rotate_749b9g$ = function (angleDeg, result) {
    return result.set_czzhjp$(this).rotate_mx4ult$(angleDeg);
  };
  Vec2f.prototype.scale_749b9g$ = function (factor, result) {
    return result.set_czzhjp$(this).scale_mx4ult$(factor);
  };
  Vec2f.prototype.sqrDistance_czzhjp$ = function (other) {
    var dx = this.x - other.x;
    var dy = this.y - other.y;
    return dx * dx + dy * dy;
  };
  Vec2f.prototype.sqrLength = function () {
    return this.x * this.x + this.y * this.y;
  };
  Vec2f.prototype.subtract_q2ruao$ = function (other, result) {
    return result.set_czzhjp$(this).subtract_czzhjp$(other);
  };
  Vec2f.prototype.get_za3lpa$ = function (i) {
    var tmp$;
    switch (i) {
      case 0:
        tmp$ = this.x;
        break;
      case 1:
        tmp$ = this.y;
        break;
      default:throw new KoolException('Invalid index: ' + toString(i));
    }
    return tmp$;
  };
  Vec2f.prototype.times_czzhjp$ = function (other) {
    return this.dot_czzhjp$(other);
  };
  Vec2f.prototype.toString = function () {
    return '(' + this.x + ', ' + this.y + ')';
  };
  function Vec2f$Companion() {
    Vec2f$Companion_instance = this;
    this.ZERO = Vec2f_init(0.0);
    this.X_AXIS = new Vec2f(1.0, 0.0);
    this.Y_AXIS = new Vec2f(0.0, 1.0);
    this.NEG_X_AXIS = new Vec2f(-1.0, 0.0);
    this.NEG_Y_AXIS = new Vec2f(0.0, -1.0);
  }
  Vec2f$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vec2f$Companion_instance = null;
  function Vec2f$Companion_getInstance() {
    if (Vec2f$Companion_instance === null) {
      new Vec2f$Companion();
    }
    return Vec2f$Companion_instance;
  }
  Vec2f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec2f',
    interfaces: []
  };
  function Vec2f_init(f, $this) {
    $this = $this || Object.create(Vec2f.prototype);
    Vec2f.call($this, f, f);
    return $this;
  }
  function Vec2f_init_0(v, $this) {
    $this = $this || Object.create(Vec2f.prototype);
    Vec2f.call($this, v.x, v.y);
    return $this;
  }
  function MutableVec2f(x, y) {
    Vec2f.call(this, x, y);
  }
  Object.defineProperty(MutableVec2f.prototype, 'x', {
    get: function () {
      return this.xField;
    },
    set: function (value) {
      this.xField = value;
    }
  });
  Object.defineProperty(MutableVec2f.prototype, 'y', {
    get: function () {
      return this.yField;
    },
    set: function (value) {
      this.yField = value;
    }
  });
  MutableVec2f.prototype.add_czzhjp$ = function (other) {
    this.x = this.x + other.x;
    this.y = this.y + other.y;
    return this;
  };
  MutableVec2f.prototype.norm = function () {
    return this.scale_mx4ult$(1.0 / this.length());
  };
  MutableVec2f.prototype.rotate_mx4ult$ = function (angleDeg) {
    var rad = angleDeg * package$math.DEG_2_RAD;
    var cos = Math_0.cos(rad);
    var sin = Math_0.sin(rad);
    var rx = this.x * cos - this.y * sin;
    var ry = this.x * sin + this.y * cos;
    this.x = rx;
    this.y = ry;
    return this;
  };
  MutableVec2f.prototype.scale_mx4ult$ = function (factor) {
    this.x = this.x * factor;
    this.y = this.y * factor;
    return this;
  };
  MutableVec2f.prototype.set_dleff0$ = function (x, y) {
    this.x = x;
    this.y = y;
    return this;
  };
  MutableVec2f.prototype.set_czzhjp$ = function (other) {
    this.x = other.x;
    this.y = other.y;
    return this;
  };
  MutableVec2f.prototype.subtract_czzhjp$ = function (other) {
    this.x = this.x - other.x;
    this.y = this.y - other.y;
    return this;
  };
  MutableVec2f.prototype.divAssign_mx4ult$ = function (div) {
    this.scale_mx4ult$(1.0 / div);
  };
  MutableVec2f.prototype.minusAssign_czzhjp$ = function (other) {
    this.subtract_czzhjp$(other);
  };
  MutableVec2f.prototype.plusAssign_czzhjp$ = function (other) {
    this.add_czzhjp$(other);
  };
  MutableVec2f.prototype.set_24o109$ = function (i, v) {
    switch (i) {
      case 0:
        this.x = v;
        break;
      case 1:
        this.y = v;
        break;
      default:throw new KoolException('Invalid index: ' + toString(i));
    }
  };
  MutableVec2f.prototype.timesAssign_mx4ult$ = function (factor) {
    this.scale_mx4ult$(factor);
  };
  MutableVec2f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MutableVec2f',
    interfaces: [Vec2f]
  };
  function MutableVec2f_init($this) {
    $this = $this || Object.create(MutableVec2f.prototype);
    MutableVec2f.call($this, 0.0, 0.0);
    return $this;
  }
  function MutableVec2f_init_0(v, $this) {
    $this = $this || Object.create(MutableVec2f.prototype);
    MutableVec2f.call($this, v.x, v.y);
    return $this;
  }
  function Vec3f(x, y, z) {
    Vec3f$Companion_getInstance();
    this.xField = x;
    this.yField = y;
    this.zField = z;
  }
  Object.defineProperty(Vec3f.prototype, 'x', {
    get: function () {
      return this.xField;
    }
  });
  Object.defineProperty(Vec3f.prototype, 'y', {
    get: function () {
      return this.yField;
    }
  });
  Object.defineProperty(Vec3f.prototype, 'z', {
    get: function () {
      return this.zField;
    }
  });
  Vec3f.prototype.add_2gj7b4$ = function (other, result) {
    return result.set_czzhiu$(this).add_czzhiu$(other);
  };
  Vec3f.prototype.cross_2gj7b4$ = function (other, result) {
    result.x = this.y * other.z - this.z * other.y;
    result.y = this.z * other.x - this.x * other.z;
    result.z = this.x * other.y - this.y * other.x;
    return result;
  };
  Vec3f.prototype.distance_czzhiu$ = function (other) {
    var x = this.sqrDistance_czzhiu$(other);
    return Math_0.sqrt(x);
  };
  Vec3f.prototype.dot_czzhiu$ = function (other) {
    return this.x * other.x + this.y * other.y + this.z * other.z;
  };
  Vec3f.prototype.isEqual_czzhiu$ = function (other) {
    var $receiver = this.x - other.x;
    var tmp$ = Math_0.abs($receiver) < 1.0E-5;
    if (tmp$) {
      var $receiver_0 = this.y - other.y;
      tmp$ = Math_0.abs($receiver_0) < 1.0E-5;
    }
    var tmp$_0 = tmp$;
    if (tmp$_0) {
      var $receiver_1 = this.z - other.z;
      tmp$_0 = Math_0.abs($receiver_1) < 1.0E-5;
    }
    return tmp$_0;
  };
  Vec3f.prototype.length = function () {
    var x = this.sqrLength();
    return Math_0.sqrt(x);
  };
  Vec3f.prototype.norm_5s4mqq$ = function (result) {
    return result.set_czzhiu$(this).norm();
  };
  Vec3f.prototype.rotate_hx2y1u$ = function (angleDeg, axisX, axisY, axisZ, result) {
    return result.set_czzhiu$(this).rotate_7b5o5w$(angleDeg, axisX, axisY, axisZ);
  };
  Vec3f.prototype.rotate_vqa64j$ = function (angleDeg, axis, result) {
    return result.set_czzhiu$(this).rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
  };
  Vec3f.prototype.scale_749b8l$ = function (factor, result) {
    return result.set_czzhiu$(this).scale_mx4ult$(factor);
  };
  Vec3f.prototype.sqrDistance_czzhiu$ = function (other) {
    var dx = this.x - other.x;
    var dy = this.y - other.y;
    var dz = this.z - other.z;
    return dx * dx + dy * dy + dz * dz;
  };
  Vec3f.prototype.sqrLength = function () {
    return this.x * this.x + this.y * this.y + this.z * this.z;
  };
  Vec3f.prototype.subtract_2gj7b4$ = function (other, result) {
    return result.set_czzhiu$(this).subtract_czzhiu$(other);
  };
  Vec3f.prototype.get_za3lpa$ = function (i) {
    var tmp$;
    switch (i) {
      case 0:
        tmp$ = this.x;
        break;
      case 1:
        tmp$ = this.y;
        break;
      case 2:
        tmp$ = this.z;
        break;
      default:throw new KoolException('Invalid index: ' + toString(i));
    }
    return tmp$;
  };
  Vec3f.prototype.times_czzhiu$ = function (other) {
    return this.dot_czzhiu$(other);
  };
  Vec3f.prototype.toString = function () {
    return '(' + this.x + ', ' + this.y + ', ' + this.z + ')';
  };
  function Vec3f$Companion() {
    Vec3f$Companion_instance = this;
    this.ZERO = Vec3f_init(0.0);
    this.X_AXIS = new Vec3f(1.0, 0.0, 0.0);
    this.Y_AXIS = new Vec3f(0.0, 1.0, 0.0);
    this.Z_AXIS = new Vec3f(0.0, 0.0, 1.0);
    this.NEG_X_AXIS = new Vec3f(-1.0, 0.0, 0.0);
    this.NEG_Y_AXIS = new Vec3f(0.0, -1.0, 0.0);
    this.NEG_Z_AXIS = new Vec3f(0.0, 0.0, -1.0);
  }
  Vec3f$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vec3f$Companion_instance = null;
  function Vec3f$Companion_getInstance() {
    if (Vec3f$Companion_instance === null) {
      new Vec3f$Companion();
    }
    return Vec3f$Companion_instance;
  }
  Vec3f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec3f',
    interfaces: []
  };
  function Vec3f_init(f, $this) {
    $this = $this || Object.create(Vec3f.prototype);
    Vec3f.call($this, f, f, f);
    return $this;
  }
  function Vec3f_init_0(v, $this) {
    $this = $this || Object.create(Vec3f.prototype);
    Vec3f.call($this, v.x, v.y, v.z);
    return $this;
  }
  function MutableVec3f(x, y, z) {
    Vec3f.call(this, x, y, z);
  }
  Object.defineProperty(MutableVec3f.prototype, 'x', {
    get: function () {
      return this.xField;
    },
    set: function (value) {
      this.xField = value;
    }
  });
  Object.defineProperty(MutableVec3f.prototype, 'y', {
    get: function () {
      return this.yField;
    },
    set: function (value) {
      this.yField = value;
    }
  });
  Object.defineProperty(MutableVec3f.prototype, 'z', {
    get: function () {
      return this.zField;
    },
    set: function (value) {
      this.zField = value;
    }
  });
  MutableVec3f.prototype.add_czzhiu$ = function (other) {
    this.x = this.x + other.x;
    this.y = this.y + other.y;
    this.z = this.z + other.z;
    return this;
  };
  MutableVec3f.prototype.norm = function () {
    return this.scale_mx4ult$(1.0 / this.length());
  };
  MutableVec3f.prototype.rotate_7b5o5w$ = function (angleDeg, axisX, axisY, axisZ) {
    var rad = angleDeg * package$math.DEG_2_RAD;
    var c = Math_0.cos(rad);
    var c1 = 1.0 - c;
    var s = Math_0.sin(rad);
    var rx = this.x * (axisX * axisX * c1 + c) + this.y * (axisX * axisY * c1 - axisZ * s) + this.z * (axisX * axisZ * c1 + axisY * s);
    var ry = this.x * (axisY * axisX * c1 + axisZ * s) + this.y * (axisY * axisY * c1 + c) + this.z * (axisY * axisZ * c1 - axisX * s);
    var rz = this.x * (axisX * axisZ * c1 - axisY * s) + this.y * (axisY * axisZ * c1 + axisX * s) + this.z * (axisZ * axisZ * c1 + c);
    this.x = rx;
    this.y = ry;
    this.z = rz;
    return this;
  };
  MutableVec3f.prototype.rotate_ad55pp$ = function (angleDeg, axis) {
    return this.rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
  };
  MutableVec3f.prototype.scale_mx4ult$ = function (factor) {
    this.x = this.x * factor;
    this.y = this.y * factor;
    this.z = this.z * factor;
    return this;
  };
  MutableVec3f.prototype.set_y2kzbl$ = function (x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  };
  MutableVec3f.prototype.set_czzhiu$ = function (other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
    return this;
  };
  MutableVec3f.prototype.subtract_czzhiu$ = function (other) {
    this.x = this.x - other.x;
    this.y = this.y - other.y;
    this.z = this.z - other.z;
    return this;
  };
  MutableVec3f.prototype.divAssign_mx4ult$ = function (div) {
    this.scale_mx4ult$(1.0 / div);
  };
  MutableVec3f.prototype.minusAssign_czzhiu$ = function (other) {
    this.subtract_czzhiu$(other);
  };
  MutableVec3f.prototype.plusAssign_czzhiu$ = function (other) {
    this.add_czzhiu$(other);
  };
  MutableVec3f.prototype.set_24o109$ = function (i, v) {
    switch (i) {
      case 0:
        this.x = v;
        break;
      case 1:
        this.y = v;
        break;
      case 2:
        this.z = v;
        break;
      default:throw new KoolException('Invalid index: ' + toString(i));
    }
  };
  MutableVec3f.prototype.timesAssign_mx4ult$ = function (factor) {
    this.scale_mx4ult$(factor);
  };
  MutableVec3f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MutableVec3f',
    interfaces: [Vec3f]
  };
  function MutableVec3f_init($this) {
    $this = $this || Object.create(MutableVec3f.prototype);
    MutableVec3f.call($this, 0.0, 0.0, 0.0);
    return $this;
  }
  function MutableVec3f_init_0(v, $this) {
    $this = $this || Object.create(MutableVec3f.prototype);
    MutableVec3f.call($this, v.x, v.y, v.z);
    return $this;
  }
  function Vec4f(x, y, z, w) {
    Vec4f$Companion_getInstance();
    this.xField = x;
    this.yField = y;
    this.zField = z;
    this.wField = w;
  }
  Object.defineProperty(Vec4f.prototype, 'x', {
    get: function () {
      return this.xField;
    }
  });
  Object.defineProperty(Vec4f.prototype, 'y', {
    get: function () {
      return this.yField;
    }
  });
  Object.defineProperty(Vec4f.prototype, 'z', {
    get: function () {
      return this.zField;
    }
  });
  Object.defineProperty(Vec4f.prototype, 'w', {
    get: function () {
      return this.wField;
    }
  });
  Vec4f.prototype.add_uzu8ww$ = function (other, result) {
    return result.set_czzhhz$(this).add_czzhhz$(other);
  };
  Vec4f.prototype.distance_czzhhz$ = function (other) {
    var x = this.sqrDistance_czzhhz$(other);
    return Math_0.sqrt(x);
  };
  Vec4f.prototype.dot_czzhhz$ = function (other) {
    return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
  };
  Vec4f.prototype.isEqual_czzhhz$ = function (other) {
    var $receiver = this.x - other.x;
    var tmp$ = Math_0.abs($receiver) < 1.0E-5;
    if (tmp$) {
      var $receiver_0 = this.y - other.y;
      tmp$ = Math_0.abs($receiver_0) < 1.0E-5;
    }
    var tmp$_0 = tmp$;
    if (tmp$_0) {
      var $receiver_1 = this.z - other.z;
      tmp$_0 = Math_0.abs($receiver_1) < 1.0E-5;
    }
    var tmp$_1 = tmp$_0;
    if (tmp$_1) {
      var $receiver_2 = this.w - other.w;
      tmp$_1 = Math_0.abs($receiver_2) < 1.0E-5;
    }
    return tmp$_1;
  };
  Vec4f.prototype.length = function () {
    var x = this.sqrLength();
    return Math_0.sqrt(x);
  };
  Vec4f.prototype.norm_5s4mpv$ = function (result) {
    return result.set_czzhhz$(this).norm();
  };
  Vec4f.prototype.scale_749b7q$ = function (factor, result) {
    return result.set_czzhhz$(this).scale_mx4ult$(factor);
  };
  Vec4f.prototype.sqrDistance_czzhhz$ = function (other) {
    var dx = this.x - other.x;
    var dy = this.y - other.y;
    var dz = this.z - other.z;
    var dw = this.z - other.w;
    return dx * dx + dy * dy + dz * dz + dw * dw;
  };
  Vec4f.prototype.sqrLength = function () {
    return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
  };
  Vec4f.prototype.subtract_uzu8ww$ = function (other, result) {
    return result.set_czzhhz$(this).subtract_czzhhz$(other);
  };
  Vec4f.prototype.get_za3lpa$ = function (i) {
    var tmp$;
    switch (i) {
      case 0:
        tmp$ = this.x;
        break;
      case 1:
        tmp$ = this.y;
        break;
      case 2:
        tmp$ = this.z;
        break;
      case 3:
        tmp$ = this.w;
        break;
      default:throw new KoolException('Invalid index: ' + toString(i));
    }
    return tmp$;
  };
  Vec4f.prototype.toString = function () {
    return '(' + this.x + ', ' + this.y + ', ' + this.z + ', ' + this.w + ')';
  };
  function Vec4f$Companion() {
    Vec4f$Companion_instance = this;
    this.ZERO = Vec4f_init(0.0);
    this.X_AXIS = new Vec4f(1.0, 0.0, 0.0, 0.0);
    this.Y_AXIS = new Vec4f(0.0, 1.0, 0.0, 0.0);
    this.Z_AXIS = new Vec4f(0.0, 0.0, 1.0, 0.0);
    this.W_AXIS = new Vec4f(0.0, 0.0, 0.0, 1.0);
    this.NEG_X_AXIS = new Vec4f(-1.0, 0.0, 0.0, 0.0);
    this.NEG_Y_AXIS = new Vec4f(0.0, -1.0, 0.0, 0.0);
    this.NEG_Z_AXIS = new Vec4f(0.0, 0.0, -1.0, 0.0);
    this.NEG_W_AXIS = new Vec4f(0.0, 0.0, 0.0, -1.0);
  }
  Vec4f$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vec4f$Companion_instance = null;
  function Vec4f$Companion_getInstance() {
    if (Vec4f$Companion_instance === null) {
      new Vec4f$Companion();
    }
    return Vec4f$Companion_instance;
  }
  Vec4f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec4f',
    interfaces: []
  };
  function Vec4f_init(f, $this) {
    $this = $this || Object.create(Vec4f.prototype);
    Vec4f.call($this, f, f, f, f);
    return $this;
  }
  function Vec4f_init_0(v, $this) {
    $this = $this || Object.create(Vec4f.prototype);
    Vec4f.call($this, v.x, v.y, v.z, v.w);
    return $this;
  }
  function MutableVec4f(x, y, z, w) {
    Vec4f.call(this, x, y, z, w);
  }
  Object.defineProperty(MutableVec4f.prototype, 'x', {
    get: function () {
      return this.xField;
    },
    set: function (value) {
      this.xField = value;
    }
  });
  Object.defineProperty(MutableVec4f.prototype, 'y', {
    get: function () {
      return this.yField;
    },
    set: function (value) {
      this.yField = value;
    }
  });
  Object.defineProperty(MutableVec4f.prototype, 'z', {
    get: function () {
      return this.zField;
    },
    set: function (value) {
      this.zField = value;
    }
  });
  Object.defineProperty(MutableVec4f.prototype, 'w', {
    get: function () {
      return this.wField;
    },
    set: function (value) {
      this.wField = value;
    }
  });
  MutableVec4f.prototype.add_czzhhz$ = function (other) {
    this.x = this.x + other.x;
    this.y = this.y + other.y;
    this.z = this.z + other.z;
    this.w = this.w + other.w;
    return this;
  };
  MutableVec4f.prototype.norm = function () {
    return this.scale_mx4ult$(1.0 / this.length());
  };
  MutableVec4f.prototype.scale_mx4ult$ = function (factor) {
    this.x = this.x * factor;
    this.y = this.y * factor;
    this.z = this.z * factor;
    this.w = this.w * factor;
    return this;
  };
  MutableVec4f.prototype.set_7b5o5w$ = function (x, y, z, w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    return this;
  };
  MutableVec4f.prototype.set_czzhhz$ = function (other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
    this.w = other.w;
    return this;
  };
  MutableVec4f.prototype.subtract_czzhhz$ = function (other) {
    this.x = this.x - other.x;
    this.y = this.y - other.y;
    this.z = this.z - other.z;
    this.w = this.w - other.w;
    return this;
  };
  MutableVec4f.prototype.plusAssign_czzhhz$ = function (other) {
    this.add_czzhhz$(other);
  };
  MutableVec4f.prototype.minusAssign_czzhhz$ = function (other) {
    this.subtract_czzhhz$(other);
  };
  MutableVec4f.prototype.set_24o109$ = function (i, v) {
    switch (i) {
      case 0:
        this.x = v;
        break;
      case 1:
        this.y = v;
        break;
      case 2:
        this.z = v;
        break;
      case 3:
        this.w = v;
        break;
      default:throw new KoolException('Invalid index: ' + toString(i));
    }
  };
  MutableVec4f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MutableVec4f',
    interfaces: [Vec4f]
  };
  function MutableVec4f_init($this) {
    $this = $this || Object.create(MutableVec4f.prototype);
    MutableVec4f.call($this, 0.0, 0.0, 0.0, 0.0);
    return $this;
  }
  function MutableVec4f_init_0(other, $this) {
    $this = $this || Object.create(MutableVec4f.prototype);
    MutableVec4f.call($this, other.x, other.y, other.z, other.w);
    return $this;
  }
  function Camera(name) {
    if (name === void 0)
      name = 'camera';
    Node.call(this, name);
    this.position = new MutableVec3f(0.0, 0.0, 1.0);
    this.lookAt = MutableVec3f_init_0(Vec3f$Companion_getInstance().ZERO);
    this.up = MutableVec3f_init_0(Vec3f$Companion_getInstance().Y_AXIS);
    this.aspectRatio_147bkr$_0 = 1.0;
    this.globalRange_4hi0xu$_0 = 0.0;
    this.globalPosMut = MutableVec3f_init();
    this.globalLookAtMut = MutableVec3f_init();
    this.globalUpMut = MutableVec3f_init();
    this.globalRightMut = MutableVec3f_init();
    this.globalLookDirMut = MutableVec3f_init();
    this.view = new Mat4f();
    this.invView = new Mat4f();
    this.mvp = new Mat4f();
    this.invMvp = new Mat4f();
    this.tmpVec3_txabpe$_0 = MutableVec3f_init();
    this.tmpVec4_txabq9$_0 = MutableVec4f_init();
  }
  Object.defineProperty(Camera.prototype, 'aspectRatio', {
    get: function () {
      return this.aspectRatio_147bkr$_0;
    },
    set: function (aspectRatio) {
      this.aspectRatio_147bkr$_0 = aspectRatio;
    }
  });
  Object.defineProperty(Camera.prototype, 'globalPos', {
    get: function () {
      return this.globalPosMut;
    }
  });
  Object.defineProperty(Camera.prototype, 'globalLookAt', {
    get: function () {
      return this.globalLookAtMut;
    }
  });
  Object.defineProperty(Camera.prototype, 'globalUp', {
    get: function () {
      return this.globalUpMut;
    }
  });
  Object.defineProperty(Camera.prototype, 'globalRight', {
    get: function () {
      return this.globalRightMut;
    }
  });
  Object.defineProperty(Camera.prototype, 'globalLookDir', {
    get: function () {
      return this.globalLookDirMut;
    }
  });
  Object.defineProperty(Camera.prototype, 'globalRange', {
    get: function () {
      return this.globalRange_4hi0xu$_0;
    },
    set: function (globalRange) {
      this.globalRange_4hi0xu$_0 = globalRange;
    }
  });
  Camera.prototype.updateCamera_evfofk$ = function (ctx) {
    this.aspectRatio = ctx.viewport.aspectRatio;
    this.updateViewMatrix_evfofk$(ctx);
    this.updateProjectionMatrix_evfofk$(ctx);
    ctx.mvpState.update_evfofk$(ctx);
    this.mvp.set_d4zu6j$(ctx.mvpState.mvpMatrix);
    this.mvp.invert_d4zu6j$(this.invMvp);
  };
  Camera.prototype.updateViewMatrix_evfofk$ = function (ctx) {
    this.toGlobalCoords_w1lst9$(this.globalPosMut.set_czzhiu$(this.position));
    this.toGlobalCoords_w1lst9$(this.globalLookAtMut.set_czzhiu$(this.lookAt));
    this.toGlobalCoords_w1lst9$(this.globalUpMut.set_czzhiu$(this.up), 0.0).norm();
    this.globalLookDirMut.set_czzhiu$(this.globalLookAtMut).subtract_czzhiu$(this.globalPosMut);
    this.globalRange = this.globalLookDirMut.length();
    this.globalLookDirMut.norm();
    this.globalLookDirMut.cross_2gj7b4$(this.globalUpMut, this.globalRightMut).norm();
    this.globalRightMut.cross_2gj7b4$(this.globalLookDirMut, this.globalUpMut).norm();
    this.view.setLookAt_n440fu$(this.globalPosMut, this.globalLookAtMut, this.globalUpMut);
    this.view.invert_d4zu6j$(this.invView);
    ctx.mvpState.viewMatrix.set_d4zu6j$(this.view);
  };
  Camera.prototype.computePickRay_ywr59c$ = function (pickRay, ptr, ctx) {
    return ptr.isValid && this.computePickRay_jieknl$(pickRay, ptr.x, ptr.y, ctx);
  };
  Camera.prototype.computePickRay_jieknl$ = function (pickRay, screenX, screenY, ctx) {
    var valid = this.unProjectScreen_54xt5k$(this.tmpVec3_txabpe$_0.set_y2kzbl$(screenX, screenY, 0.0), ctx, pickRay.origin);
    valid = (valid && this.unProjectScreen_54xt5k$(this.tmpVec3_txabpe$_0.set_y2kzbl$(screenX, screenY, 1.0), ctx, pickRay.direction));
    if (valid) {
      pickRay.direction.subtract_czzhiu$(pickRay.origin);
      pickRay.direction.norm();
    }
    return valid;
  };
  Camera.prototype.initRayTes_fpzm1e$ = function (rayTest, ptr, ctx) {
    return ptr.isValid && this.initRayTes_gkn0xv$(rayTest, ptr.x, ptr.y, ctx);
  };
  Camera.prototype.initRayTes_gkn0xv$ = function (rayTest, screenX, screenY, ctx) {
    rayTest.clear();
    return this.computePickRay_jieknl$(rayTest.ray, screenX, screenY, ctx);
  };
  Camera.prototype.project_2gj7b4$ = function (world, result) {
    this.tmpVec4_txabq9$_0.set_7b5o5w$(world.x, world.y, world.z, 1.0);
    this.mvp.transform_5s4mpv$(this.tmpVec4_txabq9$_0);
    var $receiver = this.tmpVec4_txabq9$_0.w;
    if (Math_0.abs($receiver) < 1.0E-5) {
      return false;
    }
    result.set_y2kzbl$(this.tmpVec4_txabq9$_0.x, this.tmpVec4_txabq9$_0.y, this.tmpVec4_txabq9$_0.z).scale_mx4ult$(1.0 / this.tmpVec4_txabq9$_0.w);
    return true;
  };
  Camera.prototype.project_2gj7bz$ = function (world, result) {
    return this.mvp.transform_5s4mpv$(result.set_7b5o5w$(world.x, world.y, world.z, 1.0));
  };
  Camera.prototype.projectScreen_54xt5k$ = function (world, ctx, result) {
    if (!this.project_2gj7b4$(world, result)) {
      return false;
    }
    result.x = (1 + result.x) * 0.5 * ctx.viewport.width + ctx.viewport.x;
    result.y = ctx.windowHeight - ((1 + result.y) * 0.5 * ctx.viewport.height + ctx.viewport.y);
    result.z = (1 + result.z) * 0.5;
    return true;
  };
  Camera.prototype.unProjectScreen_54xt5k$ = function (screen, ctx, result) {
    var x = screen.x - ctx.viewport.x;
    var y = ctx.windowHeight - screen.y - ctx.viewport.y;
    this.tmpVec4_txabq9$_0.set_7b5o5w$(2.0 * x / ctx.viewport.width - 1.0, 2.0 * y / ctx.viewport.height - 1.0, 2.0 * screen.z - 1.0, 1.0);
    this.invMvp.transform_5s4mpv$(this.tmpVec4_txabq9$_0);
    var s = 1.0 / this.tmpVec4_txabq9$_0.w;
    result.set_y2kzbl$(this.tmpVec4_txabq9$_0.x * s, this.tmpVec4_txabq9$_0.y * s, this.tmpVec4_txabq9$_0.z * s);
    return true;
  };
  Camera.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Camera',
    interfaces: [Node]
  };
  function OrthographicCamera(name) {
    if (name === void 0)
      name = 'orthographicCam';
    Camera.call(this, name);
    this.left = -10.0;
    this.right = 10.0;
    this.bottom = -10.0;
    this.top = 10.0;
    this.near = -10.0;
    this.far = 10.0;
    this.clipToViewport = false;
    this.keepAspectRatio = true;
    this.tmpNodeCenter_0 = MutableVec3f_init();
    this.tmpNodeExtent_0 = MutableVec3f_init();
  }
  OrthographicCamera.prototype.setCentered_y2kzbl$ = function (height, near, far) {
    this.top = height * 0.5;
    this.bottom = -this.top;
    this.right = this.aspectRatio * this.top;
    this.left = -this.right;
    this.near = near;
    this.far = far;
  };
  OrthographicCamera.prototype.updateViewMatrix_evfofk$ = function (ctx) {
    Camera.prototype.updateViewMatrix_evfofk$.call(this, ctx);
    this.globalLookDir;
  };
  OrthographicCamera.prototype.updateProjectionMatrix_evfofk$ = function (ctx) {
    if (this.clipToViewport) {
      this.left = 0.0;
      this.right = ctx.viewport.width;
      this.bottom = 0.0;
      this.top = ctx.viewport.height;
    }
     else if (this.keepAspectRatio) {
      var h = this.top - this.bottom;
      var w = this.aspectRatio * h;
      var xCenter = this.left + (this.right - this.left) * 0.5;
      this.left = xCenter - w * 0.5;
      this.right = xCenter + w * 0.5;
    }
    if (this.left !== this.right && this.bottom !== this.top && this.near !== this.far) {
      ctx.mvpState.projMatrix.setOrthographic_w8lrqs$(this.left, this.right, this.bottom, this.top, this.near, this.far);
    }
  };
  OrthographicCamera.prototype.computeFrustumPlane_jwr40o$ = function (z, result) {
    var zd = this.near + (this.far - this.near) * z;
    this.invView.transform_w1lst9$(result.upperLeft.set_y2kzbl$(this.left, this.top, -zd));
    this.invView.transform_w1lst9$(result.upperRight.set_y2kzbl$(this.right, this.top, -zd));
    this.invView.transform_w1lst9$(result.lowerLeft.set_y2kzbl$(this.left, this.bottom, -zd));
    this.invView.transform_w1lst9$(result.lowerRight.set_y2kzbl$(this.right, this.bottom, -zd));
  };
  OrthographicCamera.prototype.isInFrustum_f1kmr1$ = function (node) {
    var nodeRadius = node.globalRadius;
    this.tmpNodeCenter_0.set_czzhiu$(node.globalCenter);
    this.tmpNodeCenter_0.subtract_czzhiu$(this.globalPos);
    var x = this.tmpNodeCenter_0.dot_czzhiu$(this.globalRight);
    if (x > this.right + nodeRadius || x < this.left - nodeRadius) {
      return false;
    }
    var y = this.tmpNodeCenter_0.dot_czzhiu$(this.globalUp);
    if (y > this.top + nodeRadius || y < this.bottom - nodeRadius) {
      return false;
    }
    var z = this.tmpNodeCenter_0.dot_czzhiu$(this.globalLookDir);
    if (z > this.far + nodeRadius || z < this.near - nodeRadius) {
      return false;
    }
    return true;
  };
  OrthographicCamera.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OrthographicCamera',
    interfaces: [Camera]
  };
  function PerspectiveCamera(name) {
    if (name === void 0)
      name = 'perspectiveCam';
    Camera.call(this, name);
    this.clipNear = 0.1;
    this.clipFar = 100.0;
    this.fovy = 60.0;
    this.fovX_7k9npl$_0 = 0.0;
    this.sphereFacX_0 = 1.0;
    this.speherFacY_0 = 1.0;
    this.tangX_0 = 1.0;
    this.tangY_0 = 1.0;
    this.tmpNodeCenter_0 = MutableVec3f_init();
    this.tmpNodeExtent_0 = MutableVec3f_init();
  }
  Object.defineProperty(PerspectiveCamera.prototype, 'fovX', {
    get: function () {
      return this.fovX_7k9npl$_0;
    },
    set: function (fovX) {
      this.fovX_7k9npl$_0 = fovX;
    }
  });
  PerspectiveCamera.prototype.updateProjectionMatrix_evfofk$ = function (ctx) {
    ctx.mvpState.projMatrix.setPerspective_7b5o5w$(this.fovy, this.aspectRatio, this.clipNear, this.clipFar);
    var angY = this.fovy * package$math.DEG_2_RAD / 2.0;
    this.speherFacY_0 = 1.0 / Math_0.cos(angY);
    this.tangY_0 = Math_0.tan(angY);
    var x = this.tangY_0 * this.aspectRatio;
    var angX = Math_0.atan(x);
    this.sphereFacX_0 = 1.0 / Math_0.cos(angX);
    this.tangX_0 = Math_0.tan(angX);
    this.fovX = angX * 2 * package$math.RAD_2_DEG;
  };
  PerspectiveCamera.prototype.computeFrustumPlane_jwr40o$ = function (z, result) {
    var zd = this.clipNear + (this.clipFar - this.clipNear) * z;
    var x = zd * this.tangX_0;
    var y = zd * this.tangY_0;
    this.invView.transform_w1lst9$(result.upperLeft.set_y2kzbl$(-x, y, -zd));
    this.invView.transform_w1lst9$(result.upperRight.set_y2kzbl$(x, y, -zd));
    this.invView.transform_w1lst9$(result.lowerLeft.set_y2kzbl$(-x, -y, -zd));
    this.invView.transform_w1lst9$(result.lowerRight.set_y2kzbl$(x, -y, -zd));
  };
  PerspectiveCamera.prototype.isInFrustum_f1kmr1$ = function (node) {
    var nodeRadius = node.globalRadius;
    this.tmpNodeCenter_0.set_czzhiu$(node.globalCenter);
    this.tmpNodeCenter_0.subtract_czzhiu$(this.globalPos);
    var z = this.tmpNodeCenter_0.dot_czzhiu$(this.globalLookDir);
    if (z > this.clipFar + nodeRadius || z < this.clipNear - nodeRadius) {
      return false;
    }
    var y = this.tmpNodeCenter_0.dot_czzhiu$(this.globalUp);
    var d = nodeRadius * this.speherFacY_0;
    z *= this.tangY_0;
    if (y > z + d || y < -z - d) {
      return false;
    }
    var x = this.tmpNodeCenter_0.dot_czzhiu$(this.globalRight);
    d = nodeRadius * this.sphereFacX_0;
    z *= this.aspectRatio;
    if (x > z + d || x < -z - d) {
      return false;
    }
    return true;
  };
  PerspectiveCamera.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PerspectiveCamera',
    interfaces: [Camera]
  };
  function FrustumPlane() {
    this.upperLeft = MutableVec3f_init();
    this.upperRight = MutableVec3f_init();
    this.lowerLeft = MutableVec3f_init();
    this.lowerRight = MutableVec3f_init();
  }
  FrustumPlane.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FrustumPlane',
    interfaces: []
  };
  function group(name, block) {
    if (name === void 0)
      name = null;
    var grp = new Group(name);
    block(grp);
    return grp;
  }
  function Group(name) {
    if (name === void 0)
      name = null;
    Node.call(this, name);
    this.children = ArrayList_init();
    this.tmpBounds = new BoundingBox();
  }
  Object.defineProperty(Group.prototype, 'isFrustumChecked', {
    get: function () {
      return false;
    },
    set: function (value) {
    }
  });
  Group.prototype.onSceneChanged_9srkog$ = function (oldScene, newScene) {
    var tmp$;
    Node.prototype.onSceneChanged_9srkog$.call(this, oldScene, newScene);
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.children.get_za3lpa$(i).scene = newScene;
    }
  };
  Group.prototype.render_evfofk$ = function (ctx) {
    var tmp$;
    if (!this.isVisible) {
      return;
    }
    Node.prototype.render_evfofk$.call(this, ctx);
    this.tmpBounds.clear();
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      if (ctx.renderPass !== RenderPass$SHADOW_getInstance() || this.children.get_za3lpa$(i).isCastingShadow) {
        this.children.get_za3lpa$(i).render_evfofk$(ctx);
      }
      this.tmpBounds.add_ea4od8$(this.children.get_za3lpa$(i).bounds);
    }
    this.bounds.set_ea4od8$(this.tmpBounds);
  };
  Group.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$;
    Node.prototype.dispose_evfofk$.call(this, ctx);
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.children.get_za3lpa$(i).dispose_evfofk$(ctx);
    }
  };
  Group.prototype.get_61zpoe$ = function (name) {
    var tmp$;
    if (equals(name, this.name)) {
      return this;
    }
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      var node = this.children.get_za3lpa$(i).get_61zpoe$(name);
      if (node != null) {
        return node;
      }
    }
    return null;
  };
  Group.prototype.rayTest_jljx4v$ = function (test) {
    var tmp$;
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      var child = this.children.get_za3lpa$(i);
      if (child.isPickable) {
        var d = child.bounds.hitDistanceSqr_nvyeur$(test.ray);
        if (d < kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY && d <= test.hitDistanceSqr) {
          child.rayTest_jljx4v$(test);
        }
      }
    }
  };
  Group.prototype.addNode_xtids1$$default = function (node, index) {
    if (index >= 0) {
      this.children.add_wxm5ur$(index, node);
    }
     else {
      this.children.add_11rb$(node);
    }
    node.parent = this;
    this.bounds.add_ea4od8$(node.bounds);
    this.bounds.add_ea4od8$(node.bounds);
  };
  Group.prototype.addNode_xtids1$ = function (node, index, callback$default) {
    if (index === void 0)
      index = -1;
    callback$default ? callback$default(node, index) : this.addNode_xtids1$$default(node, index);
  };
  Group.prototype.removeNode_f1kmr1$ = function (node) {
    if (this.children.remove_11rb$(node)) {
      node.parent = null;
      return true;
    }
    return false;
  };
  Group.prototype.containsNode_f1kmr1$ = function (node) {
    return this.children.contains_11rb$(node);
  };
  Group.prototype.plusAssign_f1kmr1$ = function (node) {
    this.addNode_xtids1$(node);
  };
  Group.prototype.minusAssign_f1kmr1$ = function (node) {
    this.removeNode_f1kmr1$(node);
  };
  Group.prototype.unaryPlus_uv0sim$ = function ($receiver) {
    this.addNode_xtids1$($receiver);
  };
  Group.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Group',
    interfaces: [Node]
  };
  function Light() {
    this.direction = new MutableVec3f(1.0, 1.0, 1.0);
    this.color = MutableColor_init_0(Color$Companion_getInstance().WHITE);
  }
  Light.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Light',
    interfaces: []
  };
  var mesh = defineInlineFunction('kool.de.fabmax.kool.scene.mesh_ki35ir$', wrapFunction(function () {
    var Attribute = _.de.fabmax.kool.shading.Attribute;
    var mutableSetOf = Kotlin.kotlin.collections.mutableSetOf_i5x0yv$;
    var MeshData_init = _.de.fabmax.kool.scene.MeshData;
    var Mesh_init = _.de.fabmax.kool.scene.Mesh;
    var LightModel = _.de.fabmax.kool.shading.LightModel;
    var ColorModel = _.de.fabmax.kool.shading.ColorModel;
    var Unit = Kotlin.kotlin.Unit;
    var basicShader = _.de.fabmax.kool.shading.basicShader_n50u2h$;
    function mesh$lambda(closure$attributes) {
      return function ($receiver) {
        if (closure$attributes.contains_11rb$(Attribute.Companion.NORMALS)) {
          $receiver.lightModel = LightModel.PHONG_LIGHTING;
        }
         else {
          $receiver.lightModel = LightModel.NO_LIGHTING;
        }
        if (closure$attributes.contains_11rb$(Attribute.Companion.TEXTURE_COORDS)) {
          $receiver.colorModel = ColorModel.TEXTURE_COLOR;
        }
         else if (closure$attributes.contains_11rb$(Attribute.Companion.COLORS)) {
          $receiver.colorModel = ColorModel.VERTEX_COLOR;
        }
         else {
          $receiver.colorModel = ColorModel.STATIC_COLOR;
        }
        return Unit;
      };
    }
    return function (withNormals, withColors, withTexCoords, name, block) {
      if (name === void 0)
        name = null;
      var attributes = mutableSetOf([Attribute.Companion.POSITIONS]);
      if (withNormals) {
        var element = Attribute.Companion.NORMALS;
        attributes.add_11rb$(element);
      }
      if (withColors) {
        var element_0 = Attribute.Companion.COLORS;
        attributes.add_11rb$(element_0);
      }
      if (withTexCoords) {
        var element_1 = Attribute.Companion.TEXTURE_COORDS;
        attributes.add_11rb$(element_1);
      }
      var mesh = new Mesh_init(new MeshData_init(attributes), name);
      mesh.shader = basicShader(mesh$lambda(attributes));
      block(mesh);
      mesh.generateGeometry();
      return mesh;
    };
  }));
  var mesh_0 = defineInlineFunction('kool.de.fabmax.kool.scene.mesh_tok25s$', wrapFunction(function () {
    var toHashSet = Kotlin.kotlin.collections.toHashSet_us0mfu$;
    var MeshData_init = _.de.fabmax.kool.scene.MeshData;
    var Mesh_init = _.de.fabmax.kool.scene.Mesh;
    var Attribute = _.de.fabmax.kool.shading.Attribute;
    var LightModel = _.de.fabmax.kool.shading.LightModel;
    var ColorModel = _.de.fabmax.kool.shading.ColorModel;
    var Unit = Kotlin.kotlin.Unit;
    var basicShader = _.de.fabmax.kool.shading.basicShader_n50u2h$;
    function mesh$lambda(closure$attributes) {
      return function ($receiver) {
        if (closure$attributes.contains_11rb$(Attribute.Companion.NORMALS)) {
          $receiver.lightModel = LightModel.PHONG_LIGHTING;
        }
         else {
          $receiver.lightModel = LightModel.NO_LIGHTING;
        }
        if (closure$attributes.contains_11rb$(Attribute.Companion.TEXTURE_COORDS)) {
          $receiver.colorModel = ColorModel.TEXTURE_COLOR;
        }
         else if (closure$attributes.contains_11rb$(Attribute.Companion.COLORS)) {
          $receiver.colorModel = ColorModel.VERTEX_COLOR;
        }
         else {
          $receiver.colorModel = ColorModel.STATIC_COLOR;
        }
        return Unit;
      };
    }
    return function (name, attributes, block) {
      if (name === void 0)
        name = null;
      var attributes_0 = toHashSet(attributes);
      var mesh = new Mesh_init(new MeshData_init(attributes_0), name);
      mesh.shader = basicShader(mesh$lambda(attributes_0));
      block(mesh);
      mesh.generateGeometry();
      return mesh;
    };
  }));
  var mesh_1 = defineInlineFunction('kool.de.fabmax.kool.scene.mesh_w2gyes$', wrapFunction(function () {
    var MeshData_init = _.de.fabmax.kool.scene.MeshData;
    var Mesh_init = _.de.fabmax.kool.scene.Mesh;
    var Attribute = _.de.fabmax.kool.shading.Attribute;
    var LightModel = _.de.fabmax.kool.shading.LightModel;
    var ColorModel = _.de.fabmax.kool.shading.ColorModel;
    var Unit = Kotlin.kotlin.Unit;
    var basicShader = _.de.fabmax.kool.shading.basicShader_n50u2h$;
    function mesh$lambda(closure$attributes) {
      return function ($receiver) {
        if (closure$attributes.contains_11rb$(Attribute.Companion.NORMALS)) {
          $receiver.lightModel = LightModel.PHONG_LIGHTING;
        }
         else {
          $receiver.lightModel = LightModel.NO_LIGHTING;
        }
        if (closure$attributes.contains_11rb$(Attribute.Companion.TEXTURE_COORDS)) {
          $receiver.colorModel = ColorModel.TEXTURE_COLOR;
        }
         else if (closure$attributes.contains_11rb$(Attribute.Companion.COLORS)) {
          $receiver.colorModel = ColorModel.VERTEX_COLOR;
        }
         else {
          $receiver.colorModel = ColorModel.STATIC_COLOR;
        }
        return Unit;
      };
    }
    return function (name, attributes, block) {
      if (name === void 0)
        name = null;
      var mesh = new Mesh_init(new MeshData_init(attributes), name);
      mesh.shader = basicShader(mesh$lambda(attributes));
      block(mesh);
      mesh.generateGeometry();
      return mesh;
    };
  }));
  function mesh$lambda(closure$attributes) {
    return function ($receiver) {
      if (closure$attributes.contains_11rb$(Attribute.Companion.NORMALS)) {
        $receiver.lightModel = LightModel.PHONG_LIGHTING;
      }
       else {
        $receiver.lightModel = LightModel.NO_LIGHTING;
      }
      if (closure$attributes.contains_11rb$(Attribute.Companion.TEXTURE_COORDS)) {
        $receiver.colorModel = ColorModel.TEXTURE_COLOR;
      }
       else if (closure$attributes.contains_11rb$(Attribute.Companion.COLORS)) {
        $receiver.colorModel = ColorModel.VERTEX_COLOR;
      }
       else {
        $receiver.colorModel = ColorModel.STATIC_COLOR;
      }
      return Unit;
    };
  }
  function colorMesh(name, generate) {
    if (name === void 0)
      name = null;
    var attributes = mutableSetOf([Attribute.Companion.POSITIONS]);
    if (true) {
      var element = Attribute.Companion.NORMALS;
      attributes.add_11rb$(element);
    }
    if (true) {
      var element_0 = Attribute.Companion.COLORS;
      attributes.add_11rb$(element_0);
    }
    if (false) {
      var element_1 = Attribute.Companion.TEXTURE_COORDS;
      attributes.add_11rb$(element_1);
    }
    var mesh = new Mesh(new MeshData(attributes), name);
    mesh.shader = basicShader(mesh$lambda(attributes));
    generate(mesh);
    mesh.generateGeometry();
    return mesh;
  }
  function textMesh$lambda($receiver) {
    $receiver.lightModel = LightModel$NO_LIGHTING_getInstance();
    return Unit;
  }
  function textMesh(font, name, generate) {
    if (name === void 0)
      name = null;
    var attributes = mutableSetOf([Attribute.Companion.POSITIONS]);
    if (true) {
      var element = Attribute.Companion.NORMALS;
      attributes.add_11rb$(element);
    }
    if (true) {
      var element_0 = Attribute.Companion.COLORS;
      attributes.add_11rb$(element_0);
    }
    if (true) {
      var element_1 = Attribute.Companion.TEXTURE_COORDS;
      attributes.add_11rb$(element_1);
    }
    var mesh = new Mesh(new MeshData(attributes), name);
    mesh.shader = basicShader(mesh$lambda(attributes));
    generate(mesh);
    mesh.generateGeometry();
    var text = mesh;
    text.shader = fontShader(font, textMesh$lambda);
    return text;
  }
  function mesh$lambda_0(closure$attributes) {
    return function ($receiver) {
      if (closure$attributes.contains_11rb$(Attribute.Companion.NORMALS)) {
        $receiver.lightModel = LightModel.PHONG_LIGHTING;
      }
       else {
        $receiver.lightModel = LightModel.NO_LIGHTING;
      }
      if (closure$attributes.contains_11rb$(Attribute.Companion.TEXTURE_COORDS)) {
        $receiver.colorModel = ColorModel.TEXTURE_COLOR;
      }
       else if (closure$attributes.contains_11rb$(Attribute.Companion.COLORS)) {
        $receiver.colorModel = ColorModel.VERTEX_COLOR;
      }
       else {
        $receiver.colorModel = ColorModel.STATIC_COLOR;
      }
      return Unit;
    };
  }
  function textureMesh(name, isNormalMapped, generate) {
    if (name === void 0)
      name = null;
    if (isNormalMapped === void 0)
      isNormalMapped = false;
    var attributes = mutableSetOf([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    if (isNormalMapped) {
      var element = Attribute$Companion_getInstance().TANGENTS;
      attributes.add_11rb$(element);
    }
    var mesh = new Mesh(new MeshData(attributes), name);
    mesh.shader = basicShader(mesh$lambda_0(attributes));
    generate(mesh);
    mesh.generateGeometry();
    var mesh_0 = mesh;
    if (isNormalMapped) {
      mesh_0.meshData.generateTangents();
    }
    return mesh_0;
  }
  function CullMethod(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function CullMethod_initFields() {
    CullMethod_initFields = function () {
    };
    CullMethod$DEFAULT_instance = new CullMethod('DEFAULT', 0);
    CullMethod$CULL_BACK_FACES_instance = new CullMethod('CULL_BACK_FACES', 1);
    CullMethod$CULL_FRONT_FACES_instance = new CullMethod('CULL_FRONT_FACES', 2);
    CullMethod$NO_CULLING_instance = new CullMethod('NO_CULLING', 3);
  }
  var CullMethod$DEFAULT_instance;
  function CullMethod$DEFAULT_getInstance() {
    CullMethod_initFields();
    return CullMethod$DEFAULT_instance;
  }
  var CullMethod$CULL_BACK_FACES_instance;
  function CullMethod$CULL_BACK_FACES_getInstance() {
    CullMethod_initFields();
    return CullMethod$CULL_BACK_FACES_instance;
  }
  var CullMethod$CULL_FRONT_FACES_instance;
  function CullMethod$CULL_FRONT_FACES_getInstance() {
    CullMethod_initFields();
    return CullMethod$CULL_FRONT_FACES_instance;
  }
  var CullMethod$NO_CULLING_instance;
  function CullMethod$NO_CULLING_getInstance() {
    CullMethod_initFields();
    return CullMethod$NO_CULLING_instance;
  }
  CullMethod.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CullMethod',
    interfaces: [Enum]
  };
  function CullMethod$values() {
    return [CullMethod$DEFAULT_getInstance(), CullMethod$CULL_BACK_FACES_getInstance(), CullMethod$CULL_FRONT_FACES_getInstance(), CullMethod$NO_CULLING_getInstance()];
  }
  CullMethod.values = CullMethod$values;
  function CullMethod$valueOf(name) {
    switch (name) {
      case 'DEFAULT':
        return CullMethod$DEFAULT_getInstance();
      case 'CULL_BACK_FACES':
        return CullMethod$CULL_BACK_FACES_getInstance();
      case 'CULL_FRONT_FACES':
        return CullMethod$CULL_FRONT_FACES_getInstance();
      case 'NO_CULLING':
        return CullMethod$NO_CULLING_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.scene.CullMethod.' + name);
    }
  }
  CullMethod.valueOf_61zpoe$ = CullMethod$valueOf;
  function Mesh(meshData, name) {
    if (name === void 0)
      name = null;
    Node.call(this, name);
    this.meshData = meshData;
    this.shader_cdun6p$_0 = null;
    this.primitiveType_5at6b3$_0 = GL_TRIANGLES;
    this.cullMethod_4r2xn5$_0 = CullMethod$DEFAULT_getInstance();
    this.meshData.incrementReferenceCount();
  }
  Object.defineProperty(Mesh.prototype, 'generator', {
    get: function () {
      return this.meshData.generator;
    },
    set: function (value) {
      this.meshData.generator = value;
    }
  });
  Object.defineProperty(Mesh.prototype, 'shader', {
    get: function () {
      return this.shader_cdun6p$_0;
    },
    set: function (shader) {
      this.shader_cdun6p$_0 = shader;
    }
  });
  Object.defineProperty(Mesh.prototype, 'primitiveType', {
    get: function () {
      return this.primitiveType_5at6b3$_0;
    },
    set: function (primitiveType) {
      this.primitiveType_5at6b3$_0 = primitiveType;
    }
  });
  Object.defineProperty(Mesh.prototype, 'cullMethod', {
    get: function () {
      return this.cullMethod_4r2xn5$_0;
    },
    set: function (cullMethod) {
      this.cullMethod_4r2xn5$_0 = cullMethod;
    }
  });
  Object.defineProperty(Mesh.prototype, 'bounds', {
    get: function () {
      return this.meshData.bounds;
    }
  });
  Mesh.prototype.generateGeometry = function () {
    this.meshData.generateGeometry();
  };
  Mesh.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$;
    Node.prototype.dispose_evfofk$.call(this, ctx);
    this.meshData.dispose_evfofk$(ctx);
    (tmp$ = this.shader) != null ? (tmp$.dispose_evfofk$(ctx), Unit) : null;
  };
  Mesh.prototype.render_evfofk$ = function (ctx) {
    var tmp$;
    Node.prototype.render_evfofk$.call(this, ctx);
    if (!this.isRendered || (!ctx.isDepthTest && ctx.renderPass === RenderPass$SHADOW_getInstance())) {
      return;
    }
    this.meshData.checkBuffers_evfofk$(ctx);
    ctx.shaderMgr.bindShader_wa3i41$(this.shader, ctx);
    var boundShader = ctx.shaderMgr.boundShader;
    if (boundShader != null) {
      boundShader.bindMesh_lij8m4$(this, ctx);
      if (this.cullMethod !== CullMethod$DEFAULT_getInstance()) {
        ctx.pushAttributes();
        switch (this.cullMethod.name) {
          case 'CULL_BACK_FACES':
            ctx.isCullFace = true;
            ctx.cullFace = GL_BACK;
            break;
          case 'CULL_FRONT_FACES':
            ctx.isCullFace = true;
            ctx.cullFace = GL_FRONT;
            break;
          default:ctx.isCullFace = false;
            break;
        }
        ctx.applyAttributes();
      }
      (tmp$ = this.meshData.indexBuffer) != null ? (tmp$.bind_evfofk$(ctx), Unit) : null;
      glDrawElements(this.primitiveType, this.meshData.numIndices, GL_UNSIGNED_INT, 0);
      boundShader.unbindMesh_evfofk$(ctx);
      if (this.cullMethod !== CullMethod$DEFAULT_getInstance()) {
        ctx.popAttributes();
      }
    }
  };
  Mesh.prototype.rayTest_jljx4v$ = function (test) {
    var distSqr = this.bounds.hitDistanceSqr_nvyeur$(test.ray);
    if (distSqr < kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY && distSqr <= test.hitDistanceSqr) {
      test.hitDistanceSqr = distSqr;
      test.hitNode = this;
      test.hitPositionLocal.set_czzhiu$(test.ray.direction).scale_mx4ult$(Math_0.sqrt(distSqr)).add_czzhiu$(test.ray.origin);
    }
  };
  Mesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mesh',
    interfaces: [Node]
  };
  function MeshData(vertexAttributes) {
    this.vertexAttributes = vertexAttributes;
    this.vertexList = new IndexedVertexList(this.vertexAttributes);
    this.bounds = new BoundingBox();
    this.generator = null;
    this.referenceCount_0 = 0;
    this.usage = GL_STATIC_DRAW;
    this.dataBufferF = null;
    this.dataBufferI = null;
    this.indexBuffer = null;
    this.isSyncRequired = false;
    this.isBatchUpdate_ubntbn$_0 = false;
    this.attributeBinders = LinkedHashMap_init();
  }
  Object.defineProperty(MeshData.prototype, 'numIndices', {
    get: function () {
      return this.vertexList.indices.position;
    }
  });
  Object.defineProperty(MeshData.prototype, 'numVertices', {
    get: function () {
      return this.vertexList.size;
    }
  });
  Object.defineProperty(MeshData.prototype, 'isBatchUpdate', {
    get: function () {
      return this.isBatchUpdate_ubntbn$_0;
    },
    set: function (value) {
      this.isBatchUpdate_ubntbn$_0 = value;
    }
  });
  MeshData.prototype.hasAttribute_mczodr$ = function (attribute) {
    return this.vertexAttributes.contains_11rb$(attribute);
  };
  MeshData.prototype.generateGeometry = function () {
    var gen = this.generator;
    if (gen != null) {
      this.isBatchUpdate = true;
      this.clear();
      var builder = new MeshBuilder(this);
      gen(builder);
      this.isBatchUpdate = false;
    }
  };
  MeshData.prototype.generateTangents = function () {
    var tmp$, tmp$_0, tmp$_1;
    var v0 = this.get_za3lpa$(0);
    var v1 = this.get_za3lpa$(1);
    var v2 = this.get_za3lpa$(2);
    var e1 = MutableVec3f_init();
    var e2 = MutableVec3f_init();
    var tan = MutableVec3f_init();
    tmp$ = this.numVertices;
    for (var i = 0; i < tmp$; i++) {
      v0.index = i;
      v0.tangent.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    }
    tmp$_0 = this.numIndices;
    for (var i_0 = 0; i_0 < tmp$_0; i_0 += 3) {
      v0.index = this.vertexList.indices.get_za3lpa$(i_0);
      v1.index = this.vertexList.indices.get_za3lpa$(i_0 + 1 | 0);
      v2.index = this.vertexList.indices.get_za3lpa$(i_0 + 2 | 0);
      v1.position.subtract_2gj7b4$(v0.position, e1);
      v2.position.subtract_2gj7b4$(v0.position, e2);
      var du1 = v1.texCoord.x - v0.texCoord.x;
      var dv1 = v1.texCoord.y - v0.texCoord.y;
      var du2 = v2.texCoord.x - v0.texCoord.x;
      var dv2 = v2.texCoord.y - v0.texCoord.y;
      var f = 1.0 / (du1 * dv2 - du2 * dv1);
      tan.x = f * (dv2 * e1.x - dv1 * e2.x);
      tan.y = f * (dv2 * e1.y - dv1 * e2.y);
      tan.z = f * (dv2 * e1.z - dv1 * e2.z);
      v0.tangent.plusAssign_czzhiu$(tan);
      v1.tangent.plusAssign_czzhiu$(tan);
      v2.tangent.plusAssign_czzhiu$(tan);
    }
    tmp$_1 = this.numVertices;
    for (var i_1 = 0; i_1 < tmp$_1; i_1++) {
      v0.index = i_1;
      v0.tangent.norm();
    }
  };
  MeshData.prototype.addVertex_hvwyd1$ = function (block) {
    var idx = {v: 0};
    this.isSyncRequired = true;
    idx.v = this.vertexList.addVertex_z2do90$(this.bounds, block);
    return idx.v;
  };
  MeshData.prototype.addVertex_lv7vxo$ = function (position, normal, color, texCoord) {
    if (normal === void 0)
      normal = null;
    if (color === void 0)
      color = null;
    if (texCoord === void 0)
      texCoord = null;
    var idx = {v: 0};
    this.isSyncRequired = true;
    idx.v = this.vertexList.addVertex_lv7vxo$(position, normal, color, texCoord);
    this.bounds.add_czzhiu$(position);
    return idx.v;
  };
  MeshData.prototype.addIndex_za3lpa$ = function (idx) {
    this.vertexList.addIndex_za3lpa$(idx);
    this.isSyncRequired = true;
  };
  MeshData.prototype.addTriIndices_qt1dr2$ = function (i0, i1, i2) {
    this.vertexList.addIndex_za3lpa$(i0);
    this.vertexList.addIndex_za3lpa$(i1);
    this.vertexList.addIndex_za3lpa$(i2);
    this.isSyncRequired = true;
  };
  MeshData.prototype.addIndices_pmhfmb$ = function (indices) {
    this.vertexList.addIndices_q5rwfd$(indices);
    this.isSyncRequired = true;
  };
  MeshData.prototype.clear = function () {
    this.vertexList.clear();
    this.bounds.clear();
    this.isSyncRequired = true;
  };
  MeshData.prototype.get_za3lpa$ = function (i) {
    return this.vertexList.get_za3lpa$(i);
  };
  MeshData.prototype.incrementReferenceCount = function () {
    this.referenceCount_0 = this.referenceCount_0 + 1 | 0;
  };
  MeshData.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$, tmp$_0, tmp$_1;
    if ((this.referenceCount_0 = this.referenceCount_0 - 1 | 0, this.referenceCount_0) === 0) {
      (tmp$ = this.indexBuffer) != null ? (tmp$.delete_evfofk$(ctx), Unit) : null;
      (tmp$_0 = this.dataBufferF) != null ? (tmp$_0.delete_evfofk$(ctx), Unit) : null;
      (tmp$_1 = this.dataBufferI) != null ? (tmp$_1.delete_evfofk$(ctx), Unit) : null;
      this.indexBuffer = null;
      this.dataBufferF = null;
      this.dataBufferI = null;
    }
  };
  MeshData.prototype.checkBuffers_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    if (this.indexBuffer == null) {
      this.indexBuffer = BufferResource$Companion_getInstance().create_gre2l6$(GL_ELEMENT_ARRAY_BUFFER, ctx);
    }
    var hasIntData = false;
    if (this.dataBufferF == null) {
      this.dataBufferF = BufferResource$Companion_getInstance().create_gre2l6$(GL_ARRAY_BUFFER, ctx);
      tmp$ = this.vertexAttributes.iterator();
      while (tmp$.hasNext()) {
        var vertexAttrib = tmp$.next();
        if (vertexAttrib.type.isInt) {
          hasIntData = true;
        }
         else {
          var $receiver = this.attributeBinders;
          var value = new VboBinder(ensureNotNull(this.dataBufferF), vertexAttrib.type.size, this.vertexList.strideBytesF, ensureNotNull(this.vertexList.attributeOffsets.get_11rb$(vertexAttrib)), vertexAttrib.type.glType);
          $receiver.put_xwzc9p$(vertexAttrib, value);
        }
      }
    }
    if (hasIntData && this.dataBufferI == null) {
      this.dataBufferI = BufferResource$Companion_getInstance().create_gre2l6$(GL_ARRAY_BUFFER, ctx);
      tmp$_0 = this.vertexAttributes.iterator();
      while (tmp$_0.hasNext()) {
        var vertexAttrib_0 = tmp$_0.next();
        if (vertexAttrib_0.type.isInt) {
          var $receiver_0 = this.attributeBinders;
          var value_0 = new VboBinder(ensureNotNull(this.dataBufferI), vertexAttrib_0.type.size, this.vertexList.strideBytesI, ensureNotNull(this.vertexList.attributeOffsets.get_11rb$(vertexAttrib_0)), vertexAttrib_0.type.glType);
          $receiver_0.put_xwzc9p$(vertexAttrib_0, value_0);
        }
      }
    }
    if (this.isSyncRequired && !this.isBatchUpdate) {
      var tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
      if (!this.isBatchUpdate) {
        if (!glCapabilities.uint32Indices) {
          var uint16Buffer = createUint16Buffer(this.numIndices);
          tmp$_1 = this.vertexList.indices.position - 1 | 0;
          for (var i = 0; i <= tmp$_1; i++) {
            uint16Buffer.put_11rb$(toShort(this.vertexList.indices.get_za3lpa$(i)));
          }
          (tmp$_2 = this.indexBuffer) != null ? (tmp$_2.setData_wsx8y8$(uint16Buffer, this.usage, ctx), Unit) : null;
        }
         else {
          (tmp$_3 = this.indexBuffer) != null ? (tmp$_3.setData_5irzgq$(this.vertexList.indices, this.usage, ctx), Unit) : null;
        }
        (tmp$_4 = this.dataBufferF) != null ? (tmp$_4.setData_i6at0a$(this.vertexList.dataF, this.usage, ctx), Unit) : null;
        (tmp$_5 = this.dataBufferI) != null ? (tmp$_5.setData_5irzgq$(this.vertexList.dataI, this.usage, ctx), Unit) : null;
        this.isSyncRequired = false;
      }
    }
  };
  MeshData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MeshData',
    interfaces: []
  };
  function MeshData_init(vertexAttributes, $this) {
    $this = $this || Object.create(MeshData.prototype);
    MeshData.call($this, toHashSet(vertexAttributes));
    return $this;
  }
  function Model(name) {
    TransformGroup.call(this, name);
    this.geometries_0 = ArrayList_init();
    this.subModels_0 = ArrayList_init();
    this.initGeometries_0 = true;
    this.shaderFab = null;
    this.shader = null;
  }
  Model.prototype.copyInstance = function () {
    var tmp$;
    var copy = new Model(this.name);
    copy.initGeometries_0 = this.initGeometries_0;
    copy.shaderFab = this.shaderFab;
    copy.geometries_0.addAll_brywnq$(this.geometries_0);
    tmp$ = this.subModels_0.iterator();
    while (tmp$.hasNext()) {
      var child = tmp$.next();
      copy.addSubModel_uvkl2a$(child.copyInstance());
    }
    return copy;
  };
  Model.prototype.addGeometry_wxnge0$ = function (meshData, block) {
    var tmp$ = this.geometries_0;
    var $receiver = new Model$Geometry(meshData);
    block != null ? block($receiver) : null;
    meshData.generateGeometry();
    tmp$.add_11rb$($receiver);
  };
  Model.prototype.addColorGeometry_fk74jz$ = function (block) {
    var meshData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().COLORS]);
    this.addGeometry_wxnge0$(meshData, block);
  };
  Model.prototype.addTextGeometry_fk74jz$ = function (block) {
    var meshData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().COLORS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    this.addGeometry_wxnge0$(meshData, block);
  };
  Model.prototype.addTextureGeometry_fk74jz$ = function (block) {
    var meshData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    this.addGeometry_wxnge0$(meshData, block);
  };
  Model.prototype.addSubModel_uvkl2a$ = function (child) {
    this.subModels_0.add_11rb$(child);
    this.addNode_xtids1$(child);
  };
  Model.prototype.render_evfofk$ = function (ctx) {
    var tmp$;
    if (this.initGeometries_0) {
      this.initGeometries_0 = false;
      if (this.shader == null) {
        var p = this.parent;
        var fab = this.shaderFab;
        if (fab != null) {
          this.shader = fab();
        }
         else if (Kotlin.isType(p, Model)) {
          this.shader = p.shader;
        }
      }
      tmp$ = this.geometries_0.iterator();
      while (tmp$.hasNext()) {
        var geom = tmp$.next();
        var mesh = geom.makeMesh();
        if (mesh.shader == null) {
          mesh.shader = this.shader;
        }
        this.addNode_xtids1$(mesh);
      }
    }
    TransformGroup.prototype.render_evfofk$.call(this, ctx);
  };
  function Model$Geometry(meshData) {
    this.meshData = meshData;
    this.name = null;
    this.meshFab = Model$Geometry$meshFab$lambda(this);
    this.shaderFab = null;
  }
  Model$Geometry.prototype.makeMesh = function () {
    var tmp$;
    var mesh = this.meshFab(this.meshData);
    mesh.shader = (tmp$ = this.shaderFab) != null ? tmp$() : null;
    return mesh;
  };
  function Model$Geometry$meshFab$lambda(this$Geometry) {
    return function (meshData) {
      return new Mesh(meshData, this$Geometry.name);
    };
  }
  Model$Geometry.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Geometry',
    interfaces: []
  };
  Model.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Model',
    interfaces: [TransformGroup]
  };
  function Node(name) {
    if (name === void 0)
      name = null;
    this.name = name;
    this.onRender = ArrayList_init();
    this.onHoverEnter = ArrayList_init();
    this.onHover = ArrayList_init();
    this.onHoverExit = ArrayList_init();
    this.onDispose = ArrayList_init();
    this.bounds_ba5obo$_0 = new BoundingBox();
    this.globalRadius_3g00fw$_0 = 0.0;
    this.globalCenterMut_sys4u1$_0 = MutableVec3f_init();
    this.globalExtentMut_72l7vo$_0 = MutableVec3f_init();
    this.parent_302581$_0 = null;
    this.scene_lkcnox$_0 = null;
    this.isVisible_mqrc8j$_0 = true;
    this.isCastingShadow = true;
    this.isPickable_b77vbo$_0 = true;
    this.isFrustumChecked_un4x7a$_0 = true;
    this.isRendered = true;
  }
  Object.defineProperty(Node.prototype, 'bounds', {
    get: function () {
      return this.bounds_ba5obo$_0;
    }
  });
  Object.defineProperty(Node.prototype, 'globalCenter', {
    get: function () {
      return this.globalCenterMut_sys4u1$_0;
    }
  });
  Object.defineProperty(Node.prototype, 'globalRadius', {
    get: function () {
      return this.globalRadius_3g00fw$_0;
    },
    set: function (globalRadius) {
      this.globalRadius_3g00fw$_0 = globalRadius;
    }
  });
  Object.defineProperty(Node.prototype, 'parent', {
    get: function () {
      return this.parent_302581$_0;
    },
    set: function (value) {
      if (value !== this.parent_302581$_0) {
        this.onParentChanged_etw0z0$(this.parent_302581$_0, value);
        this.parent_302581$_0 = value;
        var tmp$;
        var p = this.parent;
        while (p != null && !(p == null || Kotlin.isType(p, Scene))) {
          p = p.parent;
        }
        this.scene = (tmp$ = p) == null || Kotlin.isType(tmp$, Scene) ? tmp$ : throwCCE();
      }
    }
  });
  Object.defineProperty(Node.prototype, 'scene', {
    get: function () {
      return this.scene_lkcnox$_0;
    },
    set: function (value) {
      if (value !== this.scene_lkcnox$_0) {
        this.onSceneChanged_9srkog$(this.scene_lkcnox$_0, value);
        this.scene_lkcnox$_0 = value;
      }
    }
  });
  Object.defineProperty(Node.prototype, 'isVisible', {
    get: function () {
      return this.isVisible_mqrc8j$_0;
    },
    set: function (isVisible) {
      this.isVisible_mqrc8j$_0 = isVisible;
    }
  });
  Object.defineProperty(Node.prototype, 'isPickable', {
    get: function () {
      return this.isPickable_b77vbo$_0;
    },
    set: function (isPickable) {
      this.isPickable_b77vbo$_0 = isPickable;
    }
  });
  Object.defineProperty(Node.prototype, 'isFrustumChecked', {
    get: function () {
      return this.isFrustumChecked_un4x7a$_0;
    },
    set: function (isFrustumChecked) {
      this.isFrustumChecked_un4x7a$_0 = isFrustumChecked;
    }
  });
  Node.prototype.render_evfofk$ = function (ctx) {
    var tmp$;
    this.globalCenterMut_sys4u1$_0.set_czzhiu$(this.bounds.center);
    this.globalExtentMut_72l7vo$_0.set_czzhiu$(this.bounds.max);
    ctx.mvpState.modelMatrix.transform_w1lst9$(this.globalCenterMut_sys4u1$_0);
    ctx.mvpState.modelMatrix.transform_w1lst9$(this.globalExtentMut_72l7vo$_0);
    this.globalRadius = this.globalCenter.distance_czzhiu$(this.globalExtentMut_72l7vo$_0);
    this.isRendered = this.checkIsVisible_evfofk$(ctx);
    if (this.isRendered) {
      if (!this.onRender.isEmpty()) {
        tmp$ = this.onRender;
        for (var i = 0; i !== tmp$.size; ++i) {
          this.onRender.get_za3lpa$(i)(this, ctx);
        }
      }
    }
  };
  Node.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$;
    tmp$ = this.onDispose;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.onDispose.get_za3lpa$(i)(this, ctx);
    }
  };
  Node.prototype.toGlobalCoords_w1lst9$$default = function (vec, w) {
    var tmp$;
    (tmp$ = this.parent) != null ? tmp$.toGlobalCoords_w1lst9$(vec, w) : null;
    return vec;
  };
  Node.prototype.toGlobalCoords_w1lst9$ = function (vec, w, callback$default) {
    if (w === void 0)
      w = 1.0;
    return callback$default ? callback$default(vec, w) : this.toGlobalCoords_w1lst9$$default(vec, w);
  };
  Node.prototype.toLocalCoords_w1lst9$$default = function (vec, w) {
    var tmp$;
    (tmp$ = this.parent) != null ? tmp$.toLocalCoords_w1lst9$(vec) : null;
    return vec;
  };
  Node.prototype.toLocalCoords_w1lst9$ = function (vec, w, callback$default) {
    if (w === void 0)
      w = 1.0;
    return callback$default ? callback$default(vec, w) : this.toLocalCoords_w1lst9$$default(vec, w);
  };
  Node.prototype.rayTest_jljx4v$ = function (test) {
  };
  Node.prototype.get_61zpoe$ = function (name) {
    if (equals(name, this.name)) {
      return this;
    }
    return null;
  };
  Node.prototype.checkIsVisible_evfofk$ = function (ctx) {
    var tmp$, tmp$_0, tmp$_1;
    if (!this.isVisible) {
      return false;
    }
     else if (this.isFrustumChecked && !this.bounds.isEmpty) {
      return (tmp$_1 = (tmp$_0 = (tmp$ = this.scene) != null ? tmp$.camera : null) != null ? tmp$_0.isInFrustum_f1kmr1$(this) : null) != null ? tmp$_1 : true;
    }
    return true;
  };
  Node.prototype.findParentOfType_287e2$ = defineInlineFunction('kool.de.fabmax.kool.scene.Node.findParentOfType_287e2$', wrapFunction(function () {
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT) {
      var tmp$;
      var p = this.parent;
      while (p != null && !isT(p)) {
        p = p.parent;
      }
      return isT(tmp$ = p) ? tmp$ : throwCCE();
    };
  }));
  Node.prototype.onSceneChanged_9srkog$ = function (oldScene, newScene) {
  };
  Node.prototype.onParentChanged_etw0z0$ = function (oldParent, newParent) {
  };
  Node.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Node',
    interfaces: []
  };
  var scene = defineInlineFunction('kool.de.fabmax.kool.scene.scene_13di2z$', wrapFunction(function () {
    var Scene_init = _.de.fabmax.kool.scene.Scene;
    return function (name, block) {
      if (name === void 0)
        name = null;
      var $receiver = new Scene_init(name);
      block($receiver);
      return $receiver;
    };
  }));
  function Scene(name) {
    if (name === void 0)
      name = null;
    Group.call(this, name);
    this.onPreRender = ArrayList_init();
    this.onPostRender = ArrayList_init();
    this.camera = new PerspectiveCamera();
    this.light = new Light();
    this.defaultShadowMap_jajs2a$_0 = null;
    this.clearMask = GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT;
    this.isPickingEnabled = true;
    this.rayTest_odjp91$_0 = new RayTest();
    this.hoverNode_ab2f3d$_0 = null;
    this.dragPtrs_mbcqtw$_0 = ArrayList_init();
    this.dragHandlers_ipew8g$_0 = ArrayList_init();
    this.scene = this;
  }
  function Scene$set_Scene$defaultShadowMap$lambda(closure$value) {
    return function ($receiver, ctx) {
      closure$value.renderShadowMap_jkftm7$($receiver, ctx);
      return Unit;
    };
  }
  function Scene$set_Scene$defaultShadowMap$lambda_0(closure$value) {
    return function ($receiver, ctx) {
      closure$value.dispose_evfofk$(ctx);
      return Unit;
    };
  }
  Object.defineProperty(Scene.prototype, 'defaultShadowMap', {
    get: function () {
      return this.defaultShadowMap_jajs2a$_0;
    },
    set: function (value) {
      this.defaultShadowMap_jajs2a$_0 = value;
      if (value != null) {
        this.onPreRender.add_11rb$(Scene$set_Scene$defaultShadowMap$lambda(value));
        this.onDispose.add_11rb$(Scene$set_Scene$defaultShadowMap$lambda_0(value));
      }
    }
  });
  Scene.prototype.renderScene_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    if (!this.isVisible) {
      return;
    }
    tmp$ = this.onPreRender;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.onPreRender.get_za3lpa$(i)(this, ctx);
    }
    this.camera.updateCamera_evfofk$(ctx);
    this.handleInput_xkom3v$_0(ctx);
    if (this.clearMask !== 0) {
      glClear(this.clearMask);
    }
    this.render_evfofk$(ctx);
    tmp$_0 = this.onPostRender;
    for (var i_0 = 0; i_0 !== tmp$_0.size; ++i_0) {
      this.onPostRender.get_za3lpa$(i_0)(this, ctx);
    }
  };
  Scene.prototype.registerDragHandler_dsvxak$ = function (handler) {
    if (!this.dragHandlers_ipew8g$_0.contains_11rb$(handler)) {
      this.dragHandlers_ipew8g$_0.add_11rb$(handler);
    }
  };
  Scene.prototype.removeDragHandler_dsvxak$ = function (handler) {
    this.dragHandlers_ipew8g$_0.remove_11rb$(handler);
  };
  Scene.prototype.handleInput_xkom3v$_0 = function (ctx) {
    var tmp$, tmp$_0, tmp$_1;
    var hovered = null;
    var prevHovered = this.hoverNode_ab2f3d$_0;
    var ptr = ctx.inputMgr.primaryPointer;
    if (this.isPickingEnabled && ptr.isInViewport_evfofk$(ctx) && this.camera.initRayTes_fpzm1e$(this.rayTest_odjp91$_0, ptr, ctx)) {
      this.rayTest_jljx4v$(this.rayTest_odjp91$_0);
      if (this.rayTest_odjp91$_0.isHit) {
        this.rayTest_odjp91$_0.computeHitPosition();
        hovered = this.rayTest_odjp91$_0.hitNode;
      }
    }
    if (!equals(prevHovered, hovered)) {
      if (prevHovered != null) {
        tmp$ = prevHovered.onHoverExit;
        for (var i = 0; i !== tmp$.size; ++i) {
          prevHovered.onHoverExit.get_za3lpa$(i)(prevHovered, ptr, this.rayTest_odjp91$_0, ctx);
        }
      }
      if (hovered != null) {
        tmp$_0 = hovered.onHoverEnter;
        for (var i_0 = 0; i_0 !== tmp$_0.size; ++i_0) {
          hovered.onHoverEnter.get_za3lpa$(i_0)(hovered, ptr, this.rayTest_odjp91$_0, ctx);
        }
      }
      this.hoverNode_ab2f3d$_0 = hovered;
    }
    if (hovered != null && equals(prevHovered, hovered)) {
      tmp$_1 = hovered.onHover;
      for (var i_1 = 0; i_1 !== tmp$_1.size; ++i_1) {
        hovered.onHover.get_za3lpa$(i_1)(hovered, ptr, this.rayTest_odjp91$_0, ctx);
      }
    }
    if (this.isPickingEnabled) {
      this.handleDrag_unv5i9$_0(ctx);
    }
  };
  Scene.prototype.handleDrag_unv5i9$_0 = function (ctx) {
    var tmp$;
    this.dragPtrs_mbcqtw$_0.clear();
    tmp$ = ctx.inputMgr.pointers;
    for (var i = 0; i !== tmp$.length; ++i) {
      var ptr = ctx.inputMgr.pointers[i];
      if (ptr.isInViewport_evfofk$(ctx) && (ptr.buttonMask !== 0 || ptr.buttonEventMask !== 0 || ptr.deltaScroll !== 0.0)) {
        this.dragPtrs_mbcqtw$_0.add_11rb$(ctx.inputMgr.pointers[i]);
      }
    }
    var handlerIdx = get_lastIndex(this.dragHandlers_ipew8g$_0);
    while (handlerIdx >= 0) {
      var result = this.dragHandlers_ipew8g$_0.get_za3lpa$(handlerIdx).handleDrag_t4w9y2$(this.dragPtrs_mbcqtw$_0, ctx);
      if ((result & InputManager$DragHandler$Companion_getInstance().REMOVE_HANDLER) !== 0) {
        this.dragHandlers_ipew8g$_0.removeAt_za3lpa$(handlerIdx);
      }
      if ((result & InputManager$DragHandler$Companion_getInstance().HANDLED) !== 0) {
        break;
      }
      handlerIdx = handlerIdx - 1 | 0;
    }
  };
  Scene.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Scene',
    interfaces: [Group]
  };
  function sphericalInputTransform(name, block) {
    if (name === void 0)
      name = null;
    var sit = new SphericalInputTransform(name);
    block(sit);
    return sit;
  }
  function SphericalInputTransform(name) {
    if (name === void 0)
      name = null;
    TransformGroup.call(this, name);
    this.leftDragMethod = SphericalInputTransform$DragMethod$ROTATE_getInstance();
    this.middleDragMethod = SphericalInputTransform$DragMethod$NONE_getInstance();
    this.rightDragMethod = SphericalInputTransform$DragMethod$PAN_getInstance();
    this.zoomMethod = SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_getInstance();
    this.verticalAxis = Vec3f$Companion_getInstance().Y_AXIS;
    this.horizontalAxis = Vec3f$Companion_getInstance().X_AXIS;
    this.minHorizontalRot = -90.0;
    this.maxHorizontalRot = 90.0;
    this.translation = MutableVec3f_init();
    this.verticalRotation = 0.0;
    this.horizontalRotation = 0.0;
    this.zoom_q2oenn$_0 = 10.0;
    this.invertRotX = false;
    this.invertRotY = false;
    this.minZoom = 1.0;
    this.maxZoom = 100.0;
    this.translationBounds = null;
    this.panMethod = new CameraOrthogonalPan();
    this.vertRotAnimator = new SphericalInputTransform$AnimatedVal(this, 0.0);
    this.horiRotAnimator = new SphericalInputTransform$AnimatedVal(this, 0.0);
    this.zoomAnimator = new SphericalInputTransform$AnimatedVal(this, this.zoom);
    this.prevButtonMask_tpqbjl$_0 = 0;
    this.dragMethod_dbmj85$_0 = SphericalInputTransform$DragMethod$NONE_getInstance();
    this.dragStart_48qwuy$_0 = false;
    this.deltaPos_c9l5fg$_0 = MutableVec2f_init();
    this.deltaScroll_4ldocx$_0 = 0.0;
    this.ptrPos_e3vj7e$_0 = MutableVec2f_init();
    this.panPlane_nfvt5t$_0 = new Plane();
    this.pointerHitStart_kpvvnc$_0 = MutableVec3f_init();
    this.pointerHit_wr4hnu$_0 = MutableVec3f_init();
    this.tmpVec1_mu7x28$_0 = MutableVec3f_init();
    this.tmpVec2_mu7x33$_0 = MutableVec3f_init();
    this.mouseTransform_xgc6g7$_0 = new Mat4f();
    this.mouseTransformInv_b94dfu$_0 = new Mat4f();
    this.smoothness_e1amor$_0 = 0.0;
    this.stiffness_u0yfe3$_0 = 0.0;
    this.damping_nsq0tu$_0 = 0.0;
    this.smoothness = 0.5;
    this.panPlane_nfvt5t$_0.p.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.panPlane_nfvt5t$_0.n.set_czzhiu$(Vec3f$Companion_getInstance().Y_AXIS);
  }
  Object.defineProperty(SphericalInputTransform.prototype, 'zoom', {
    get: function () {
      return this.zoom_q2oenn$_0;
    },
    set: function (value) {
      var min = this.minZoom;
      var max = this.maxZoom;
      var clamp$result;
      if (value < min) {
        clamp$result = min;
      }
       else if (value > max) {
        clamp$result = max;
      }
       else {
        clamp$result = value;
      }
      this.zoom_q2oenn$_0 = clamp$result;
    }
  });
  Object.defineProperty(SphericalInputTransform.prototype, 'smoothness', {
    get: function () {
      return this.smoothness_e1amor$_0;
    },
    set: function (value) {
      this.smoothness_e1amor$_0 = value;
      if (!(Math_0.abs(value) < 1.0E-5)) {
        this.stiffness_u0yfe3$_0 = 50.0 / value;
        var x = this.stiffness_u0yfe3$_0;
        this.damping_nsq0tu$_0 = 2.0 * Math_0.sqrt(x);
      }
    }
  });
  SphericalInputTransform.prototype.setMouseRotation_dleff0$ = function (vertical, horizontal) {
    this.vertRotAnimator.set_mx4ult$(vertical);
    this.horiRotAnimator.set_mx4ult$(horizontal);
    this.verticalRotation = vertical;
    this.horizontalRotation = horizontal;
  };
  SphericalInputTransform.prototype.setMouseTranslation_y2kzbl$ = function (x, y, z) {
    this.translation.set_y2kzbl$(x, y, z);
  };
  SphericalInputTransform.prototype.resetZoom_mx4ult$ = function (newZoom) {
    this.zoom = newZoom;
    this.zoomAnimator.set_mx4ult$(this.zoom);
  };
  SphericalInputTransform.prototype.updateTransform = function () {
    var tmp$;
    (tmp$ = this.translationBounds) != null ? (tmp$.clampToBounds_5s4mqq$(this.translation), Unit) : null;
    this.mouseTransform_xgc6g7$_0.invert_d4zu6j$(this.mouseTransformInv_b94dfu$_0);
    this.mul_d4zu6j$(this.mouseTransformInv_b94dfu$_0);
    var z = this.zoomAnimator.actual;
    var vr = this.vertRotAnimator.actual;
    var hr = this.horiRotAnimator.actual;
    this.mouseTransform_xgc6g7$_0.setIdentity();
    this.mouseTransform_xgc6g7$_0.translate_y2kzbl$(this.translation.x, this.translation.y, this.translation.z);
    this.mouseTransform_xgc6g7$_0.scale_y2kzbl$(z, z, z);
    this.mouseTransform_xgc6g7$_0.rotate_ad55pp$(vr, this.verticalAxis);
    this.mouseTransform_xgc6g7$_0.rotate_ad55pp$(hr, this.horizontalAxis);
    this.mul_d4zu6j$(this.mouseTransform_xgc6g7$_0);
  };
  SphericalInputTransform.prototype.render_evfofk$ = function (ctx) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
    tmp$ = this.scene;
    if (tmp$ == null) {
      return;
    }
    var scene = tmp$;
    if (this.panMethod.computePanPoint_h7ddm6$(this.pointerHit_wr4hnu$_0, scene, this.ptrPos_e3vj7e$_0, ctx)) {
      if (this.dragStart_48qwuy$_0) {
        this.dragStart_48qwuy$_0 = false;
        this.pointerHitStart_kpvvnc$_0.set_czzhiu$(this.pointerHit_wr4hnu$_0);
        this.stopSmoothMotion_qs2eoq$_0();
      }
       else if (this.dragMethod_dbmj85$_0 === SphericalInputTransform$DragMethod$PAN_getInstance()) {
        var $receiver = 1 - this.smoothness;
        var clamp$result;
        if ($receiver < 0.1) {
          clamp$result = 0.1;
        }
         else if ($receiver > 1.0) {
          clamp$result = 1.0;
        }
         else {
          clamp$result = $receiver;
        }
        var s = clamp$result;
        this.tmpVec1_mu7x28$_0.set_czzhiu$(this.pointerHitStart_kpvvnc$_0).subtract_czzhiu$(this.pointerHit_wr4hnu$_0).scale_mx4ult$(s);
        var tLen = this.tmpVec1_mu7x28$_0.length();
        if (tLen > scene.camera.globalRange * 0.5) {
          this.tmpVec1_mu7x28$_0.scale_mx4ult$(scene.camera.globalRange * 0.5 / tLen);
        }
        this.translation.add_czzhiu$(this.tmpVec1_mu7x28$_0);
      }
    }
     else {
      this.pointerHit_wr4hnu$_0.set_czzhiu$(scene.camera.globalLookAt);
    }
    var $receiver_0 = this.deltaScroll_4ldocx$_0;
    if (!(Math_0.abs($receiver_0) < 1.0E-5)) {
      this.zoom = this.zoom * (1.0 - this.deltaScroll_4ldocx$_0 / 10.0);
      this.deltaScroll_4ldocx$_0 = 0.0;
    }
    if (this.dragMethod_dbmj85$_0 === SphericalInputTransform$DragMethod$ROTATE_getInstance()) {
      tmp$_2 = this.verticalRotation;
      tmp$_1 = this.deltaPos_c9l5fg$_0.x / 3;
      if (this.invertRotX) {
        tmp$_0 = -1.0;
      }
       else {
        tmp$_0 = 1.0;
      }
      this.verticalRotation = tmp$_2 - tmp$_1 * tmp$_0;
      tmp$_5 = this.horizontalRotation;
      tmp$_4 = this.deltaPos_c9l5fg$_0.y / 3;
      if (this.invertRotY) {
        tmp$_3 = -1.0;
      }
       else {
        tmp$_3 = 1.0;
      }
      this.horizontalRotation = tmp$_5 - tmp$_4 * tmp$_3;
      var $receiver_1 = this.horizontalRotation;
      var min = this.minHorizontalRot;
      var max = this.maxHorizontalRot;
      var clamp$result_0;
      if ($receiver_1 < min) {
        clamp$result_0 = min;
      }
       else if ($receiver_1 > max) {
        clamp$result_0 = max;
      }
       else {
        clamp$result_0 = $receiver_1;
      }
      this.horizontalRotation = clamp$result_0;
      this.deltaPos_c9l5fg$_0.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
    }
    this.vertRotAnimator.desired = this.verticalRotation;
    this.horiRotAnimator.desired = this.horizontalRotation;
    this.zoomAnimator.desired = this.zoom;
    var oldZ = this.zoomAnimator.actual;
    var z = this.zoomAnimator.animate_14dthe$(ctx.deltaT);
    var $receiver_2 = oldZ - z;
    if (!(Math_0.abs($receiver_2) < 1.0E-5) && this.zoomMethod === SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_getInstance()) {
      this.computeZoomTranslationPerspective_sqm467$(scene, oldZ, z);
    }
    this.vertRotAnimator.animate_14dthe$(ctx.deltaT);
    this.horiRotAnimator.animate_14dthe$(ctx.deltaT);
    this.updateTransform();
    TransformGroup.prototype.render_evfofk$.call(this, ctx);
  };
  SphericalInputTransform.prototype.computeZoomTranslationPerspective_sqm467$ = function (scene, oldZoom, newZoom) {
    scene.camera.globalPos.subtract_2gj7b4$(this.pointerHit_wr4hnu$_0, this.tmpVec1_mu7x28$_0).scale_mx4ult$(newZoom / oldZoom).add_czzhiu$(this.pointerHit_wr4hnu$_0);
    scene.camera.globalPos.subtract_2gj7b4$(scene.camera.globalLookAt, this.tmpVec2_mu7x33$_0).scale_mx4ult$(newZoom / oldZoom).add_czzhiu$(scene.camera.globalLookAt);
    this.translation.add_czzhiu$(this.tmpVec1_mu7x28$_0).subtract_czzhiu$(this.tmpVec2_mu7x33$_0);
  };
  SphericalInputTransform.prototype.stopSmoothMotion_qs2eoq$_0 = function () {
    this.vertRotAnimator.set_mx4ult$(this.vertRotAnimator.actual);
    this.horiRotAnimator.set_mx4ult$(this.horiRotAnimator.actual);
    this.zoomAnimator.set_mx4ult$(this.zoomAnimator.actual);
    this.verticalRotation = this.vertRotAnimator.actual;
    this.horizontalRotation = this.horiRotAnimator.actual;
    this.zoom = this.zoomAnimator.actual;
  };
  SphericalInputTransform.prototype.onSceneChanged_9srkog$ = function (oldScene, newScene) {
    TransformGroup.prototype.onSceneChanged_9srkog$.call(this, oldScene, newScene);
    oldScene != null ? (oldScene.removeDragHandler_dsvxak$(this), Unit) : null;
    newScene != null ? (newScene.registerDragHandler_dsvxak$(this), Unit) : null;
  };
  SphericalInputTransform.prototype.handleDrag_t4w9y2$ = function (dragPtrs, ctx) {
    if (dragPtrs.size === 1 && dragPtrs.get_za3lpa$(0).isInViewport_evfofk$(ctx)) {
      if (dragPtrs.get_za3lpa$(0).buttonEventMask !== 0 || dragPtrs.get_za3lpa$(0).buttonMask !== this.prevButtonMask_tpqbjl$_0) {
        if (dragPtrs.get_za3lpa$(0).isLeftButtonDown) {
          this.dragMethod_dbmj85$_0 = this.leftDragMethod;
        }
         else if (dragPtrs.get_za3lpa$(0).isRightButtonDown) {
          this.dragMethod_dbmj85$_0 = this.rightDragMethod;
        }
         else if (dragPtrs.get_za3lpa$(0).isMiddleButtonDown) {
          this.dragMethod_dbmj85$_0 = this.middleDragMethod;
        }
         else {
          this.dragMethod_dbmj85$_0 = SphericalInputTransform$DragMethod$NONE_getInstance();
        }
        this.dragStart_48qwuy$_0 = this.dragMethod_dbmj85$_0 !== SphericalInputTransform$DragMethod$NONE_getInstance();
      }
      this.prevButtonMask_tpqbjl$_0 = dragPtrs.get_za3lpa$(0).buttonMask;
      this.ptrPos_e3vj7e$_0.set_dleff0$(dragPtrs.get_za3lpa$(0).x, dragPtrs.get_za3lpa$(0).y);
      this.deltaPos_c9l5fg$_0.set_dleff0$(dragPtrs.get_za3lpa$(0).deltaX, dragPtrs.get_za3lpa$(0).deltaY);
      this.deltaScroll_4ldocx$_0 = dragPtrs.get_za3lpa$(0).deltaScroll;
    }
     else {
      this.deltaPos_c9l5fg$_0.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
      this.deltaScroll_4ldocx$_0 = 0.0;
    }
    return 0;
  };
  function SphericalInputTransform$DragMethod(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function SphericalInputTransform$DragMethod_initFields() {
    SphericalInputTransform$DragMethod_initFields = function () {
    };
    SphericalInputTransform$DragMethod$NONE_instance = new SphericalInputTransform$DragMethod('NONE', 0);
    SphericalInputTransform$DragMethod$ROTATE_instance = new SphericalInputTransform$DragMethod('ROTATE', 1);
    SphericalInputTransform$DragMethod$PAN_instance = new SphericalInputTransform$DragMethod('PAN', 2);
  }
  var SphericalInputTransform$DragMethod$NONE_instance;
  function SphericalInputTransform$DragMethod$NONE_getInstance() {
    SphericalInputTransform$DragMethod_initFields();
    return SphericalInputTransform$DragMethod$NONE_instance;
  }
  var SphericalInputTransform$DragMethod$ROTATE_instance;
  function SphericalInputTransform$DragMethod$ROTATE_getInstance() {
    SphericalInputTransform$DragMethod_initFields();
    return SphericalInputTransform$DragMethod$ROTATE_instance;
  }
  var SphericalInputTransform$DragMethod$PAN_instance;
  function SphericalInputTransform$DragMethod$PAN_getInstance() {
    SphericalInputTransform$DragMethod_initFields();
    return SphericalInputTransform$DragMethod$PAN_instance;
  }
  SphericalInputTransform$DragMethod.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DragMethod',
    interfaces: [Enum]
  };
  function SphericalInputTransform$DragMethod$values() {
    return [SphericalInputTransform$DragMethod$NONE_getInstance(), SphericalInputTransform$DragMethod$ROTATE_getInstance(), SphericalInputTransform$DragMethod$PAN_getInstance()];
  }
  SphericalInputTransform$DragMethod.values = SphericalInputTransform$DragMethod$values;
  function SphericalInputTransform$DragMethod$valueOf(name) {
    switch (name) {
      case 'NONE':
        return SphericalInputTransform$DragMethod$NONE_getInstance();
      case 'ROTATE':
        return SphericalInputTransform$DragMethod$ROTATE_getInstance();
      case 'PAN':
        return SphericalInputTransform$DragMethod$PAN_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.scene.SphericalInputTransform.DragMethod.' + name);
    }
  }
  SphericalInputTransform$DragMethod.valueOf_61zpoe$ = SphericalInputTransform$DragMethod$valueOf;
  function SphericalInputTransform$ZoomMethod(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function SphericalInputTransform$ZoomMethod_initFields() {
    SphericalInputTransform$ZoomMethod_initFields = function () {
    };
    SphericalInputTransform$ZoomMethod$ZOOM_CENTER_instance = new SphericalInputTransform$ZoomMethod('ZOOM_CENTER', 0);
    SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_instance = new SphericalInputTransform$ZoomMethod('ZOOM_TRANSLATE', 1);
  }
  var SphericalInputTransform$ZoomMethod$ZOOM_CENTER_instance;
  function SphericalInputTransform$ZoomMethod$ZOOM_CENTER_getInstance() {
    SphericalInputTransform$ZoomMethod_initFields();
    return SphericalInputTransform$ZoomMethod$ZOOM_CENTER_instance;
  }
  var SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_instance;
  function SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_getInstance() {
    SphericalInputTransform$ZoomMethod_initFields();
    return SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_instance;
  }
  SphericalInputTransform$ZoomMethod.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ZoomMethod',
    interfaces: [Enum]
  };
  function SphericalInputTransform$ZoomMethod$values() {
    return [SphericalInputTransform$ZoomMethod$ZOOM_CENTER_getInstance(), SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_getInstance()];
  }
  SphericalInputTransform$ZoomMethod.values = SphericalInputTransform$ZoomMethod$values;
  function SphericalInputTransform$ZoomMethod$valueOf(name) {
    switch (name) {
      case 'ZOOM_CENTER':
        return SphericalInputTransform$ZoomMethod$ZOOM_CENTER_getInstance();
      case 'ZOOM_TRANSLATE':
        return SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.scene.SphericalInputTransform.ZoomMethod.' + name);
    }
  }
  SphericalInputTransform$ZoomMethod.valueOf_61zpoe$ = SphericalInputTransform$ZoomMethod$valueOf;
  function SphericalInputTransform$AnimatedVal($outer, value) {
    this.$outer = $outer;
    this.desired = value;
    this.actual = value;
    this.speed = 0.0;
  }
  SphericalInputTransform$AnimatedVal.prototype.set_mx4ult$ = function (value) {
    this.desired = value;
    this.actual = value;
    this.speed = 0.0;
  };
  SphericalInputTransform$AnimatedVal.prototype.animate_14dthe$ = function (deltaT) {
    var $receiver = this.$outer.smoothness;
    if (Math_0.abs($receiver) < 1.0E-5 || deltaT > 0.2) {
      this.actual = this.desired;
      return this.actual;
    }
    var t = 0.0;
    while (t < deltaT) {
      var b = deltaT - t;
      var dt = Math_0.min(0.05, b);
      t += dt + 0.001;
      var err = this.desired - this.actual;
      this.speed += (err * this.$outer.stiffness_u0yfe3$_0 - this.speed * this.$outer.damping_nsq0tu$_0) * dt;
      this.actual += this.speed * dt;
    }
    return this.actual;
  };
  SphericalInputTransform$AnimatedVal.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AnimatedVal',
    interfaces: []
  };
  SphericalInputTransform.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SphericalInputTransform',
    interfaces: [InputManager$DragHandler, TransformGroup]
  };
  function PanBase() {
  }
  PanBase.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PanBase',
    interfaces: []
  };
  function CameraOrthogonalPan() {
    PanBase.call(this);
    this.panPlane = new Plane();
    this.pointerRay_0 = new Ray();
  }
  CameraOrthogonalPan.prototype.computePanPoint_h7ddm6$ = function (result, scene, ptrPos, ctx) {
    this.panPlane.p.set_czzhiu$(scene.camera.globalLookAt);
    this.panPlane.n.set_czzhiu$(scene.camera.globalLookDir);
    return scene.camera.computePickRay_jieknl$(this.pointerRay_0, ptrPos.x, ptrPos.y, ctx) && this.panPlane.intersectionPoint_m2314x$(result, this.pointerRay_0);
  };
  CameraOrthogonalPan.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CameraOrthogonalPan',
    interfaces: [PanBase]
  };
  function FixedPlanePan(planeNormal) {
    PanBase.call(this);
    this.panPlane = new Plane();
    this.pointerRay_0 = new Ray();
    this.panPlane.n.set_czzhiu$(planeNormal);
  }
  FixedPlanePan.prototype.computePanPoint_h7ddm6$ = function (result, scene, ptrPos, ctx) {
    this.panPlane.p.set_czzhiu$(scene.camera.globalLookAt);
    return scene.camera.computePickRay_jieknl$(this.pointerRay_0, ptrPos.x, ptrPos.y, ctx) && this.panPlane.intersectionPoint_m2314x$(result, this.pointerRay_0);
  };
  FixedPlanePan.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FixedPlanePan',
    interfaces: [PanBase]
  };
  function xPlanePan() {
    return new FixedPlanePan(Vec3f$Companion_getInstance().X_AXIS);
  }
  function yPlanePan() {
    return new FixedPlanePan(Vec3f$Companion_getInstance().Y_AXIS);
  }
  function zPlanePan() {
    return new FixedPlanePan(Vec3f$Companion_getInstance().Z_AXIS);
  }
  function transformGroup(name, block) {
    if (name === void 0)
      name = null;
    var tg = new TransformGroup(name);
    block(tg);
    return tg;
  }
  function TransformGroup(name) {
    if (name === void 0)
      name = null;
    Group.call(this, name);
    this.transform = new Mat4f();
    this.invTransform = new Mat4f();
    this.isIdentity = false;
    this.isDirty = false;
    this.animation_kl0jay$_0 = null;
    this.tmpTransformVec_xv9rzf$_0 = MutableVec3f_init();
  }
  Object.defineProperty(TransformGroup.prototype, 'animation', {
    get: function () {
      return this.animation_kl0jay$_0;
    },
    set: function (animation) {
      this.animation_kl0jay$_0 = animation;
    }
  });
  TransformGroup.prototype.checkInverse = function () {
    if (this.isDirty) {
      this.transform.invert_d4zu6j$(this.invTransform);
      this.isDirty = false;
    }
  };
  TransformGroup.prototype.setDirty = function () {
    this.isDirty = true;
    this.isIdentity = false;
  };
  TransformGroup.prototype.render_evfofk$ = function (ctx) {
    var tmp$;
    if (!this.isVisible) {
      return;
    }
    (tmp$ = this.animation) != null ? tmp$(this, ctx) : null;
    var wasIdentity = this.isIdentity;
    if (!wasIdentity) {
      ctx.mvpState.modelMatrix.push();
      ctx.mvpState.modelMatrix.mul_d4zu6j$(this.transform);
      ctx.mvpState.update_evfofk$(ctx);
    }
    Group.prototype.render_evfofk$.call(this, ctx);
    if (!this.bounds.isEmpty && !wasIdentity) {
      this.tmpBounds.clear();
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.min.x, this.bounds.min.y, this.bounds.min.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.min.x, this.bounds.min.y, this.bounds.max.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.min.x, this.bounds.max.y, this.bounds.min.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.min.x, this.bounds.max.y, this.bounds.max.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.max.x, this.bounds.min.y, this.bounds.min.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.max.x, this.bounds.min.y, this.bounds.max.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.max.x, this.bounds.max.y, this.bounds.min.z), 1.0));
      this.tmpBounds.add_czzhiu$(this.transform.transform_w1lst9$(this.tmpTransformVec_xv9rzf$_0.set_y2kzbl$(this.bounds.max.x, this.bounds.max.y, this.bounds.max.z), 1.0));
      this.bounds.set_ea4od8$(this.tmpBounds);
    }
    if (!wasIdentity) {
      ctx.mvpState.modelMatrix.pop();
      ctx.mvpState.update_evfofk$(ctx);
    }
  };
  TransformGroup.prototype.toGlobalCoords_w1lst9$$default = function (vec, w) {
    if (!this.isIdentity) {
      this.transform.transform_w1lst9$(vec, w);
    }
    return this.toGlobalCoords_w1lst9$(vec, w, Group.prototype.toGlobalCoords_w1lst9$$default.bind(this));
  };
  TransformGroup.prototype.toLocalCoords_w1lst9$$default = function (vec, w) {
    this.toLocalCoords_w1lst9$(vec, w, Group.prototype.toLocalCoords_w1lst9$$default.bind(this));
    if (!this.isIdentity) {
      this.checkInverse();
      return this.invTransform.transform_w1lst9$(vec, w);
    }
     else {
      return vec;
    }
  };
  TransformGroup.prototype.rayTest_jljx4v$ = function (test) {
    if (!this.isIdentity) {
      this.checkInverse();
      this.invTransform.transform_w1lst9$(test.ray.origin, 1.0);
      this.invTransform.transform_w1lst9$(test.ray.direction, 0.0);
    }
    Group.prototype.rayTest_jljx4v$.call(this, test);
    if (!this.isIdentity) {
      this.transform.transform_w1lst9$(test.ray.origin, 1.0);
      this.transform.transform_w1lst9$(test.ray.direction, 0.0);
    }
  };
  TransformGroup.prototype.translate_czzhiu$ = function (t) {
    return this.translate_y2kzbl$(t.x, t.y, t.z);
  };
  TransformGroup.prototype.translate_y2kzbl$ = function (tx, ty, tz) {
    this.transform.translate_y2kzbl$(tx, ty, tz);
    this.setDirty();
    return this;
  };
  TransformGroup.prototype.rotate_ad55pp$ = function (angleDeg, axis) {
    return this.rotate_7b5o5w$(angleDeg, axis.x, axis.y, axis.z);
  };
  TransformGroup.prototype.rotate_7b5o5w$ = function (angleDeg, axX, axY, axZ) {
    this.transform.rotate_7b5o5w$(angleDeg, axX, axY, axZ);
    this.setDirty();
    return this;
  };
  TransformGroup.prototype.scale_y2kzbl$ = function (sx, sy, sz) {
    this.transform.scale_y2kzbl$(sx, sy, sz);
    this.setDirty();
    return this;
  };
  TransformGroup.prototype.mul_d4zu6j$ = function (mat) {
    this.transform.mul_d4zu6j$(mat);
    this.setDirty();
    return this;
  };
  TransformGroup.prototype.set_d4zu6j$ = function (mat) {
    this.transform.set_d4zu6j$(mat);
    this.setDirty();
    return this;
  };
  TransformGroup.prototype.setIdentity = function () {
    this.transform.setIdentity();
    this.invTransform.setIdentity();
    this.isDirty = false;
    this.isIdentity = true;
    return this;
  };
  TransformGroup.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TransformGroup',
    interfaces: [Group]
  };
  function Animation(duration) {
    this.duration = duration;
    this.channels = ArrayList_init();
    this.weight = 0.0;
  }
  Animation.prototype.apply_8ca0d4$ = function (pos, clearTransform) {
    var tmp$;
    tmp$ = this.channels;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.channels.get_za3lpa$(i).apply_g1oyt7$(pos * this.duration, this.weight, clearTransform);
    }
  };
  Animation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Animation',
    interfaces: []
  };
  function Armature(meshData, name) {
    Armature$Companion_getInstance();
    Mesh.call(this, meshData, name);
    this.rootBones = ArrayList_init();
    this.bones = LinkedHashMap_init();
    this.indexedBones_0 = ArrayList_init();
    this.boneTransforms_0 = null;
    this.isCpuAnimated = !glCapabilities.shaderIntAttribs;
    this.animations_0 = LinkedHashMap_init();
    this.animationList_0 = ArrayList_init();
    this.animationPos_0 = 0.0;
    this.animationSpeed = 1.0;
    this.transform_0 = new Mat4fStack();
    this.tmpTransform_0 = new Mat4f();
    this.tmpVec_0 = MutableVec3f_init();
    this.originalMeshData_0 = meshData;
    this.meshV_0 = null;
    this.origV_0 = null;
    var tmp$, tmp$_0;
    var armatureAttribs = mutableSetOf([Armature$Companion_getInstance().BONE_WEIGHTS, Armature$Companion_getInstance().BONE_INDICES]);
    armatureAttribs.addAll_brywnq$(meshData.vertexAttributes);
    this.meshData = new MeshData(armatureAttribs);
    this.origV_0 = this.originalMeshData_0.get_za3lpa$(0);
    tmp$ = this.originalMeshData_0.numVertices;
    for (var i = 0; i < tmp$; i++) {
      this.origV_0.index = i;
      this.meshData.addVertex_hvwyd1$(Armature_init$lambda(this));
    }
    tmp$_0 = this.originalMeshData_0.numIndices;
    for (var i_0 = 0; i_0 < tmp$_0; i_0++) {
      this.meshData.addIndex_za3lpa$(this.originalMeshData_0.vertexList.indices.get_za3lpa$(i_0));
    }
    this.meshV_0 = this.meshData.get_za3lpa$(0);
  }
  function Armature$BoneWeight(weight, id) {
    this.weight = weight;
    this.id = id;
  }
  Armature$BoneWeight.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BoneWeight',
    interfaces: []
  };
  Armature$BoneWeight.prototype.component1 = function () {
    return this.weight;
  };
  Armature$BoneWeight.prototype.component2 = function () {
    return this.id;
  };
  Armature$BoneWeight.prototype.copy_vjorfl$ = function (weight, id) {
    return new Armature$BoneWeight(weight === void 0 ? this.weight : weight, id === void 0 ? this.id : id);
  };
  Armature$BoneWeight.prototype.toString = function () {
    return 'BoneWeight(weight=' + Kotlin.toString(this.weight) + (', id=' + Kotlin.toString(this.id)) + ')';
  };
  Armature$BoneWeight.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.weight) | 0;
    result = result * 31 + Kotlin.hashCode(this.id) | 0;
    return result;
  };
  Armature$BoneWeight.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.weight, other.weight) && Kotlin.equals(this.id, other.id)))));
  };
  Armature.prototype.addBoneWeight_0 = function (boneWeights, boneId, boneWeight) {
    for (var i = 0; i <= 3; i++) {
      if (boneWeight > boneWeights[i].weight) {
        boneWeights[i].weight = boneWeight;
        boneWeights[i].id = boneId;
        break;
      }
    }
  };
  Armature.prototype.updateBones = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.indexedBones_0.clear();
    this.indexedBones_0.addAll_brywnq$(this.bones.values);
    this.boneTransforms_0 = createFloat32Buffer(this.indexedBones_0.size * 16 | 0);
    this.tmpTransform_0.setIdentity();
    var array = Array_0(this.meshData.numVertices);
    var tmp$_3;
    tmp$_3 = array.length - 1 | 0;
    for (var i = 0; i <= tmp$_3; i++) {
      var array_0 = Array_0(4);
      var tmp$_4;
      tmp$_4 = array_0.length - 1 | 0;
      for (var i_0 = 0; i_0 <= tmp$_4; i_0++) {
        array_0[i_0] = new Armature$BoneWeight(0.0, 0);
      }
      array[i] = array_0;
    }
    var boneWeights = array;
    var tmp$_5, tmp$_0_0;
    var index = 0;
    tmp$_5 = this.indexedBones_0.iterator();
    while (tmp$_5.hasNext()) {
      var item = tmp$_5.next();
      var boneId = (tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0);
      var tmp$_6;
      item.id = boneId;
      ensureNotNull(this.boneTransforms_0).position = boneId * 16 | 0;
      this.tmpTransform_0.toBuffer_he122g$(ensureNotNull(this.boneTransforms_0));
      ensureNotNull(this.boneTransforms_0).limit = ensureNotNull(this.boneTransforms_0).capacity;
      tmp$_6 = item.vertexIds;
      for (var i_1 = 0; i_1 !== tmp$_6.length; ++i_1) {
        var vertexId = item.vertexIds[i_1];
        this.addBoneWeight_0(boneWeights[vertexId], boneId, item.vertexWeights[i_1]);
      }
    }
    for (tmp$ = 0; tmp$ !== boneWeights.length; ++tmp$) {
      var vertexBoneWeights = boneWeights[tmp$];
      var tmp$_7;
      var sum = 0.0;
      for (tmp$_7 = 0; tmp$_7 !== vertexBoneWeights.length; ++tmp$_7) {
        var element = vertexBoneWeights[tmp$_7];
        sum += element.weight;
      }
      var weightSum = sum;
      var tmp$_8;
      for (tmp$_8 = 0; tmp$_8 !== vertexBoneWeights.length; ++tmp$_8) {
        var element_0 = vertexBoneWeights[tmp$_8];
        element_0.weight = element_0.weight / weightSum;
      }
    }
    var tmp$_9, tmp$_0_1;
    var index_0 = 0;
    tmp$_9 = this.indexedBones_0.iterator();
    while (tmp$_9.hasNext()) {
      var item_0 = tmp$_9.next();
      var boneId_0 = (tmp$_0_1 = index_0, index_0 = tmp$_0_1 + 1 | 0, tmp$_0_1);
      var tmp$_10;
      tmp$_10 = item_0.vertexIds;
      for (var i_2 = 0; i_2 !== tmp$_10.length; ++i_2) {
        var tmp$_11, tmp$_12;
        var vertexId_0 = item_0.vertexIds[i_2];
        item_0.vertexWeights[i_2] = 0.0;
        tmp$_11 = boneWeights[vertexId_0];
        for (tmp$_12 = 0; tmp$_12 !== tmp$_11.length; ++tmp$_12) {
          var boneW = tmp$_11[tmp$_12];
          if (boneW.id === boneId_0) {
            item_0.vertexWeights[i_2] = boneW.weight;
            break;
          }
        }
      }
    }
    tmp$_0 = this.meshData.numVertices;
    for (var i_3 = 0; i_3 < tmp$_0; i_3++) {
      var boneWs = boneWeights[i_3];
      this.meshV_0.index = i_3;
      (tmp$_1 = this.meshV_0.getVec4fAttribute_mczodr$(Armature$Companion_getInstance().BONE_WEIGHTS)) != null ? tmp$_1.set_7b5o5w$(boneWs[0].weight, boneWs[1].weight, boneWs[2].weight, boneWs[3].weight) : null;
      (tmp$_2 = this.meshV_0.getVec4iAttribute_mczodr$(Armature$Companion_getInstance().BONE_INDICES)) != null ? (tmp$_2.set_tjonv8$(boneWs[0].id, boneWs[1].id, boneWs[2].id, boneWs[3].id), Unit) : null;
    }
  };
  Armature.prototype.getAnimation_61zpoe$ = function (name) {
    return this.animations_0.get_11rb$(name);
  };
  Armature.prototype.addAnimation_z5ltv$ = function (name, animation) {
    this.animations_0.put_xwzc9p$(name, animation);
    this.animationList_0.add_11rb$(animation);
  };
  var MutableCollection = Kotlin.kotlin.collections.MutableCollection;
  Armature.prototype.removeAnimation_61zpoe$ = function (name) {
    var $receiver = this.animationList_0;
    var element = this.animations_0.remove_11rb$(name);
    var tmp$;
    (Kotlin.isType(tmp$ = $receiver, MutableCollection) ? tmp$ : throwCCE()).remove_11rb$(element);
  };
  Armature.prototype.render_evfofk$ = function (ctx) {
    var shader = this.shader;
    if (Kotlin.isType(shader, BasicShader)) {
      shader.bones = this.boneTransforms_0;
    }
    Mesh.prototype.render_evfofk$.call(this, ctx);
    if (ctx.deltaT > 0) {
      this.applyAnimation_0(ctx.deltaT);
    }
  };
  Armature.prototype.applyAnimation_0 = function (deltaT) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var update = false;
    var weightedDuration = 0.0;
    tmp$ = this.animationList_0;
    for (var i = 0; i !== tmp$.size; ++i) {
      var anim = this.animationList_0.get_za3lpa$(i);
      if (anim.weight > 0) {
        weightedDuration += anim.duration * anim.weight;
      }
    }
    this.animationPos_0 = (this.animationPos_0 + deltaT / weightedDuration * this.animationSpeed) % 1.0;
    tmp$_0 = this.animationList_0;
    for (var i_0 = 0; i_0 !== tmp$_0.size; ++i_0) {
      var anim_0 = this.animationList_0.get_za3lpa$(i_0);
      if (anim_0.weight > 0) {
        anim_0.apply_8ca0d4$(this.animationPos_0, !update);
        update = true;
      }
    }
    if (update) {
      if (this.isCpuAnimated) {
        this.meshData.isBatchUpdate = true;
        this.meshData.isSyncRequired = true;
        this.clearMesh_0();
        tmp$_1 = this.rootBones;
        for (var i_1 = 0; i_1 !== tmp$_1.size; ++i_1) {
          this.applyBone_0(this.rootBones.get_za3lpa$(i_1), this.transform_0, this.isCpuAnimated);
        }
        this.meshData.isBatchUpdate = false;
      }
       else {
        tmp$_2 = this.rootBones;
        for (var i_2 = 0; i_2 !== tmp$_2.size; ++i_2) {
          this.applyBone_0(this.rootBones.get_za3lpa$(i_2), this.transform_0, this.isCpuAnimated);
        }
      }
    }
  };
  Armature.prototype.applyBone_0 = function (bone, transform, updateMesh) {
    var tmp$;
    transform.push();
    transform.mul_d4zu6j$(bone.transform).mul_93v2ma$(bone.offsetMatrix, this.tmpTransform_0);
    if (updateMesh) {
      this.softTransformMesh_0(bone, this.tmpTransform_0);
    }
     else {
      ensureNotNull(this.boneTransforms_0).position = 16 * bone.id | 0;
      this.tmpTransform_0.toBuffer_he122g$(ensureNotNull(this.boneTransforms_0));
      ensureNotNull(this.boneTransforms_0).limit = ensureNotNull(this.boneTransforms_0).capacity;
    }
    tmp$ = bone.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.applyBone_0(bone.children.get_za3lpa$(i), transform, updateMesh);
    }
    transform.pop();
  };
  Armature.prototype.clearMesh_0 = function () {
    var tmp$;
    tmp$ = this.meshData.numVertices;
    for (var i = 0; i < tmp$; i++) {
      this.meshV_0.index = i;
      this.meshV_0.position.set_y2kzbl$(0.0, 0.0, 0.0);
      this.meshV_0.normal.set_y2kzbl$(0.0, 0.0, 0.0);
    }
  };
  Armature.prototype.softTransformMesh_0 = function (bone, transform) {
    var tmp$;
    tmp$ = bone.vertexIds;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.meshV_0.index = bone.vertexIds[i];
      this.origV_0.index = bone.vertexIds[i];
      this.tmpVec_0.set_czzhiu$(this.origV_0.position);
      transform.transform_w1lst9$(this.tmpVec_0);
      this.tmpVec_0.timesAssign_mx4ult$(bone.vertexWeights[i]);
      this.meshV_0.position.plusAssign_czzhiu$(this.tmpVec_0);
      this.tmpVec_0.set_czzhiu$(this.origV_0.normal);
      transform.transform_w1lst9$(this.tmpVec_0, 0.0);
      this.tmpVec_0.timesAssign_mx4ult$(bone.vertexWeights[i]);
      this.meshV_0.normal.plusAssign_czzhiu$(this.tmpVec_0);
    }
  };
  function Armature$Companion() {
    Armature$Companion_instance = this;
    this.BONE_WEIGHTS = new Attribute('attrib_bone_weights', AttributeType$VEC_4F_getInstance());
    this.BONE_INDICES = new Attribute('attrib_bone_indices', AttributeType$VEC_4I_getInstance());
  }
  Armature$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Armature$Companion_instance = null;
  function Armature$Companion_getInstance() {
    if (Armature$Companion_instance === null) {
      new Armature$Companion();
    }
    return Armature$Companion_instance;
  }
  function Armature_init$lambda(this$Armature) {
    return function ($receiver) {
      $receiver.set_j5bz6$(this$Armature.origV_0);
      return Unit;
    };
  }
  Armature.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Armature',
    interfaces: [Mesh]
  };
  function Bone(name, numVertices) {
    this.name = name;
    this.parent = null;
    this.id = 0;
    this.children = ArrayList_init();
    this.offsetMatrix = new Mat4f();
    this.transform = new Mat4f();
    this.vertexIds = new Int32Array(numVertices);
    this.vertexWeights = new Float32Array(numVertices);
  }
  Bone.prototype.clearTransform = function () {
    for (var i = 0; i <= 15; i++) {
      this.transform.matrix[i] = 0.0;
    }
  };
  Bone.prototype.addTransform_8kv2li$ = function (transform, weight) {
    for (var i = 0; i <= 15; i++) {
      this.transform.matrix[i] = this.transform.matrix[i] + transform.get_za3lpa$(i) * weight;
    }
  };
  Bone.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Bone',
    interfaces: [AnimatedNode]
  };
  function AnimatedNode() {
  }
  AnimatedNode.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'AnimatedNode',
    interfaces: []
  };
  function NodeAnimation(name, node) {
    this.name = name;
    this.node = node;
    this.rotationKeys = ArrayList_init();
    this.positionKeys = ArrayList_init();
    this.scalingKeys = ArrayList_init();
    this.tmpTransform_0 = new Mat4f();
    this.tmpMat_0 = new Mat4f();
  }
  NodeAnimation.prototype.apply_g1oyt7$ = function (time, weight, clearTransform) {
    this.tmpTransform_0.setIdentity();
    this.mul_0(this.positionKeys, time);
    this.mul_0(this.rotationKeys, time);
    this.mul_0(this.scalingKeys, time);
    if (clearTransform) {
      this.node.clearTransform();
    }
    this.node.addTransform_8kv2li$(this.tmpTransform_0, weight);
  };
  NodeAnimation.prototype.mul_0 = function (keys, time) {
    var tmp$;
    if (!keys.isEmpty()) {
      var idx = this.findIndex_0(time, keys);
      if ((idx + 1 | 0) < keys.size) {
        tmp$ = keys.get_za3lpa$(idx + 1 | 0);
      }
       else {
        tmp$ = null;
      }
      var next = tmp$;
      this.tmpTransform_0.mul_d4zu6j$(keys.get_za3lpa$(idx).mixAndSet_yh9mvs$(time, next, this.tmpMat_0));
    }
  };
  NodeAnimation.prototype.findIndex_0 = function (time, keys) {
    for (var i = 0; i !== keys.size; ++i) {
      if (keys.get_za3lpa$(i).time > time) {
        var b = i - 1 | 0;
        return Math_0.max(0, b);
      }
    }
    return keys.size - 1 | 0;
  };
  NodeAnimation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NodeAnimation',
    interfaces: []
  };
  function AnimationKey(time, value) {
    this.time = time;
    this.value = value;
  }
  AnimationKey.prototype.weight_dleff0$ = function (pos, nextTime) {
    return (pos - this.time) / (nextTime - this.time);
  };
  AnimationKey.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AnimationKey',
    interfaces: []
  };
  function RotationKey(time, rotation) {
    AnimationKey.call(this, time, rotation);
    this.tmpRotation_0 = MutableVec4f_init();
  }
  RotationKey.prototype.mixAndSet_yh9mvs$ = function (time, next, result) {
    if (next == null) {
      result.setRotate_czzhhz$(this.value);
    }
     else {
      slerp(this.value, next.value, this.weight_dleff0$(time, next.time), this.tmpRotation_0);
      result.setRotate_czzhhz$(this.tmpRotation_0);
    }
    return result;
  };
  RotationKey.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RotationKey',
    interfaces: [AnimationKey]
  };
  function PositionKey(time, position) {
    AnimationKey.call(this, time, position);
    this.tmpPosition_0 = MutableVec3f_init();
  }
  PositionKey.prototype.mixAndSet_yh9mvs$ = function (time, next, result) {
    if (next == null) {
      result.setIdentity().translate_czzhiu$(this.value);
    }
     else {
      next.value.subtract_2gj7b4$(this.value, this.tmpPosition_0).scale_mx4ult$(this.weight_dleff0$(time, next.time)).add_czzhiu$(this.value);
      result.setIdentity().translate_czzhiu$(this.tmpPosition_0);
    }
    return result;
  };
  PositionKey.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PositionKey',
    interfaces: [AnimationKey]
  };
  function ScalingKey(time, scaling) {
    AnimationKey.call(this, time, scaling);
    this.tmpScaling_0 = MutableVec3f_init();
  }
  ScalingKey.prototype.mixAndSet_yh9mvs$ = function (time, next, result) {
    if (next == null) {
      result.setIdentity().scale_czzhiu$(this.value);
    }
     else {
      next.value.subtract_2gj7b4$(this.value, this.tmpScaling_0).scale_mx4ult$(this.weight_dleff0$(time, next.time)).add_czzhiu$(this.value);
      result.setIdentity().scale_czzhiu$(this.tmpScaling_0);
    }
    return result;
  };
  ScalingKey.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ScalingKey',
    interfaces: [AnimationKey]
  };
  function Button(name, root) {
    Label.call(this, name, root);
    this.onClick = ArrayList_init();
    this.textColorHovered = new ThemeOrCustomProp(Color$Companion_getInstance().WHITE);
    this.isPressed_xi0anj$_0 = false;
    this.isHovered_k6sd62$_0 = false;
    this.ptrDownPos = MutableVec2f_init();
    this.textAlignment = new Gravity(Alignment$CENTER_getInstance(), Alignment$CENTER_getInstance());
    this.onHoverEnter.add_11rb$(Button_init$lambda(this));
    this.onHoverExit.add_11rb$(Button_init$lambda_0(this));
    this.onHover.add_11rb$(Button_init$lambda_1(this));
  }
  Object.defineProperty(Button.prototype, 'isPressed', {
    get: function () {
      return this.isPressed_xi0anj$_0;
    },
    set: function (isPressed) {
      this.isPressed_xi0anj$_0 = isPressed;
    }
  });
  Object.defineProperty(Button.prototype, 'isHovered', {
    get: function () {
      return this.isHovered_k6sd62$_0;
    },
    set: function (isHovered) {
      this.isHovered_k6sd62$_0 = isHovered;
    }
  });
  Button.prototype.fireOnClick_1tlxzm$ = function (ptr, rt, ctx) {
    var tmp$;
    tmp$ = this.onClick;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.onClick.get_za3lpa$(i)(this, ptr, rt, ctx);
    }
  };
  Button.prototype.setThemeProps = function () {
    Label.prototype.setThemeProps.call(this);
    this.textColorHovered.setTheme_11rb$(this.root.theme.accentColor);
  };
  Button.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.newButtonUi_t4rm0v$(this);
  };
  function Button_init$lambda(this$Button) {
    return function ($receiver, f, f_0, f_1) {
      this$Button.isHovered = true;
      return Unit;
    };
  }
  function Button_init$lambda_0(this$Button) {
    return function ($receiver, f, f_0, f_1) {
      this$Button.isHovered = false;
      this$Button.isPressed = false;
      return Unit;
    };
  }
  function Button_init$lambda_1(this$Button) {
    return function ($receiver, ptr, rt, ctx) {
      if (ptr.isLeftButtonEvent) {
        if (ptr.isLeftButtonDown) {
          this$Button.ptrDownPos.set_dleff0$(rt.hitPositionLocal.x, rt.hitPositionLocal.y);
          this$Button.isPressed = true;
        }
         else if (this$Button.isPressed) {
          this$Button.isPressed = false;
          this$Button.ptrDownPos.x = this$Button.ptrDownPos.x - rt.hitPositionLocal.x;
          this$Button.ptrDownPos.y = this$Button.ptrDownPos.y - rt.hitPositionLocal.y;
          if (this$Button.ptrDownPos.length() < dp_0(this$Button, 5.0)) {
            this$Button.fireOnClick_1tlxzm$(ptr, rt, ctx);
          }
        }
      }
      return Unit;
    };
  }
  Button.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Button',
    interfaces: [Label]
  };
  function ButtonUi(button, baseUi) {
    LabelUi.call(this, button, baseUi);
    this.button = button;
    this.hoverAnimator = new LinearAnimator(new InterpolatedFloat(0.0, 1.0));
    this.colorWeightStd = 1.0;
    this.colorWeightHovered = 0.0;
    this.hoverEnterListener = ButtonUi$hoverEnterListener$lambda(this);
    this.hoverExitListener = ButtonUi$hoverExitListener$lambda(this);
  }
  function ButtonUi$createUi$lambda(this$ButtonUi) {
    return function (v) {
      this$ButtonUi.colorWeightHovered = v;
      this$ButtonUi.colorWeightStd = 1.0 - v;
      this$ButtonUi.button.requestUiUpdate();
      return Unit;
    };
  }
  ButtonUi.prototype.createUi_evfofk$ = function (ctx) {
    LabelUi.prototype.createUi_evfofk$.call(this, ctx);
    this.hoverAnimator.speed = 0.0;
    this.hoverAnimator.value.onUpdate = ButtonUi$createUi$lambda(this);
    var $receiver = this.button.onHoverEnter;
    var element = this.hoverEnterListener;
    $receiver.add_11rb$(element);
    var $receiver_0 = this.button.onHoverExit;
    var element_0 = this.hoverExitListener;
    $receiver_0.add_11rb$(element_0);
  };
  ButtonUi.prototype.updateTextColor = function () {
    this.textColor.clear();
    this.textColor.add_y83vuj$(this.button.textColor.apply(), this.colorWeightStd);
    this.textColor.add_y83vuj$(this.button.textColorHovered.apply(), this.colorWeightHovered);
  };
  ButtonUi.prototype.disposeUi_evfofk$ = function (ctx) {
    LabelUi.prototype.disposeUi_evfofk$.call(this, ctx);
    var $receiver = this.button.onHoverEnter;
    var element = this.hoverEnterListener;
    $receiver.remove_11rb$(element);
    var $receiver_0 = this.button.onHoverExit;
    var element_0 = this.hoverExitListener;
    $receiver_0.remove_11rb$(element_0);
  };
  ButtonUi.prototype.onRender_evfofk$ = function (ctx) {
    LabelUi.prototype.onRender_evfofk$.call(this, ctx);
    this.hoverAnimator.tick_evfofk$(ctx);
  };
  function ButtonUi$hoverEnterListener$lambda(this$ButtonUi) {
    return function ($receiver, f, f_0, f_1) {
      this$ButtonUi.hoverAnimator.duration = 0.1;
      this$ButtonUi.hoverAnimator.speed = 1.0;
      return Unit;
    };
  }
  function ButtonUi$hoverExitListener$lambda(this$ButtonUi) {
    return function ($receiver, f, f_0, f_1) {
      this$ButtonUi.hoverAnimator.duration = 0.2;
      this$ButtonUi.hoverAnimator.speed = -1.0;
      return Unit;
    };
  }
  ButtonUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ButtonUi',
    interfaces: [LabelUi]
  };
  function ComponentUi() {
  }
  ComponentUi.prototype.updateComponentAlpha = function () {
  };
  ComponentUi.prototype.createUi_evfofk$ = function (ctx) {
  };
  ComponentUi.prototype.updateUi_evfofk$ = function (ctx) {
  };
  ComponentUi.prototype.disposeUi_evfofk$ = function (ctx) {
  };
  ComponentUi.prototype.onRender_evfofk$ = function (ctx) {
  };
  ComponentUi.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ComponentUi',
    interfaces: []
  };
  function BlankComponentUi() {
  }
  BlankComponentUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlankComponentUi',
    interfaces: [ComponentUi]
  };
  function SimpleComponentUi(component) {
    this.component = component;
    this.shader = null;
    this.meshData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().COLORS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    this.meshBuilder = new MeshBuilder(this.meshData);
    this.mesh = new Mesh(this.meshData);
    this.color = new ThemeOrCustomProp(Color$Companion_getInstance().BLACK.withAlpha_mx4ult$(0.5));
  }
  SimpleComponentUi.prototype.updateComponentAlpha = function () {
    var tmp$;
    (tmp$ = this.shader) != null ? (tmp$.alpha = this.component.alpha) : null;
  };
  SimpleComponentUi.prototype.createUi_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    this.color.setTheme_11rb$(this.component.root.theme.backgroundColor).apply();
    this.shader = this.createShader_evfofk$(ctx);
    (tmp$_0 = (tmp$ = this.shader) != null ? tmp$.staticColor : null) != null ? tmp$_0.set_czzhhz$(this.color.prop) : null;
    this.mesh.shader = this.shader;
    this.component.addNode_xtids1$(this.mesh, 0);
  };
  SimpleComponentUi.prototype.disposeUi_evfofk$ = function (ctx) {
    this.component.minusAssign_f1kmr1$(this.mesh);
    this.mesh.dispose_evfofk$(ctx);
  };
  SimpleComponentUi.prototype.updateUi_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    this.color.setTheme_11rb$(this.component.root.theme.backgroundColor).apply();
    (tmp$_0 = (tmp$ = this.shader) != null ? tmp$.staticColor : null) != null ? tmp$_0.set_czzhhz$(this.color.prop) : null;
    this.component.setupBuilder_84rojv$(this.meshBuilder);
    this.meshBuilder.color = this.color.prop;
    var $this = this.meshBuilder;
    var $receiver = $this.rectProps.defaults();
    $receiver.size.set_dleff0$(this.component.width, this.component.height);
    $receiver.fullTexCoords();
    $this.rect_e5k3t5$($this.rectProps);
  };
  function SimpleComponentUi$createShader$lambda(this$SimpleComponentUi) {
    return function ($receiver) {
      $receiver.lightModel = this$SimpleComponentUi.component.root.shaderLightModel;
      $receiver.colorModel = ColorModel$STATIC_COLOR_getInstance();
      $receiver.isAlpha = true;
      return Unit;
    };
  }
  SimpleComponentUi.prototype.createShader_evfofk$ = function (ctx) {
    return basicShader(SimpleComponentUi$createShader$lambda(this));
  };
  SimpleComponentUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimpleComponentUi',
    interfaces: [ComponentUi]
  };
  function BlurredComponentUi(component) {
    SimpleComponentUi.call(this, component);
  }
  function BlurredComponentUi$createShader$lambda(this$BlurredComponentUi) {
    return function ($receiver) {
      $receiver.lightModel = this$BlurredComponentUi.component.root.shaderLightModel;
      $receiver.colorModel = ColorModel$STATIC_COLOR_getInstance();
      $receiver.isAlpha = true;
      return Unit;
    };
  }
  BlurredComponentUi.prototype.createShader_evfofk$ = function (ctx) {
    var $receiver = blurShader(BlurredComponentUi$createShader$lambda(this));
    $receiver.blurHelper = this.component.root.createBlurHelper();
    return $receiver;
  };
  BlurredComponentUi.prototype.updateUi_evfofk$ = function (ctx) {
    SimpleComponentUi.prototype.updateUi_evfofk$.call(this, ctx);
    var bs = this.shader;
    if (Kotlin.isType(bs, BlurShader)) {
      bs.colorMix = bs.staticColor.w;
      bs.staticColor.w = 1.0;
    }
  };
  BlurredComponentUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlurredComponentUi',
    interfaces: [SimpleComponentUi]
  };
  function Label(name, root) {
    UiComponent.call(this, name, root);
    this.text_qvo458$_0 = name;
    this.textAlignment_ux8xbn$_0 = new Gravity(Alignment$START_getInstance(), Alignment$CENTER_getInstance());
    this.font = new ThemeOrCustomProp(Font$Companion_getInstance().DEFAULT_FONT);
    this.textColor = new ThemeOrCustomProp(Color$Companion_getInstance().WHITE);
  }
  Object.defineProperty(Label.prototype, 'text', {
    get: function () {
      return this.text_qvo458$_0;
    },
    set: function (value) {
      if (!equals(value, this.text_qvo458$_0)) {
        this.text_qvo458$_0 = value;
        this.requestUiUpdate();
      }
    }
  });
  Object.defineProperty(Label.prototype, 'textAlignment', {
    get: function () {
      return this.textAlignment_ux8xbn$_0;
    },
    set: function (value) {
      if (!(value != null ? value.equals(this.textAlignment_ux8xbn$_0) : null)) {
        this.textAlignment_ux8xbn$_0 = value;
        this.requestUiUpdate();
      }
    }
  });
  Label.prototype.setThemeProps = function () {
    UiComponent.prototype.setThemeProps.call(this);
    this.font.setTheme_11rb$(this.root.theme.standardFont_mx4ult$(this.dpi));
    this.textColor.setTheme_11rb$(this.root.theme.foregroundColor);
  };
  Label.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.newLabelUi_wviu0r$(this);
  };
  Label.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Label',
    interfaces: [UiComponent]
  };
  function LabelUi(label, baseUi) {
    this.label = label;
    this.baseUi_tctiu8$_0 = baseUi;
    this.meshData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().COLORS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    this.meshBuilder = new MeshBuilder(this.meshData);
    this.mesh = new Mesh(this.meshData);
    this.meshAdded = false;
    this.font = this.label.font.prop;
    this.textColor = MutableColor_init();
    this.textStartX = 0.0;
    this.textWidth = 0.0;
    this.textBaseline = 0.0;
  }
  LabelUi.prototype.updateComponentAlpha = function () {
    this.baseUi_tctiu8$_0.updateComponentAlpha();
    var shader = this.mesh.shader;
    if (Kotlin.isType(shader, BasicShader)) {
      shader.alpha = this.label.alpha;
    }
  };
  function LabelUi$createUi$lambda(this$LabelUi) {
    return function ($receiver) {
      $receiver.lightModel = this$LabelUi.label.root.shaderLightModel;
      $receiver.colorModel = ColorModel$VERTEX_COLOR_getInstance();
      $receiver.isAlpha = true;
      return Unit;
    };
  }
  LabelUi.prototype.createUi_evfofk$ = function (ctx) {
    this.baseUi_tctiu8$_0.createUi_evfofk$(ctx);
    this.mesh.shader = fontShader(void 0, LabelUi$createUi$lambda(this));
    this.label.plusAssign_f1kmr1$(this.mesh);
  };
  LabelUi.prototype.updateUi_evfofk$ = function (ctx) {
    this.baseUi_tctiu8$_0.updateUi_evfofk$(ctx);
    if (this.label.font.isUpdate) {
      this.label.font.prop.dispose_evfofk$(ctx);
      this.font = this.label.font.apply();
    }
    var shader = this.mesh.shader;
    if (Kotlin.isType(shader, BasicShader)) {
      shader.texture = this.font;
    }
    this.label.setupBuilder_84rojv$(this.meshBuilder);
    this.updateTextColor();
    this.computeTextMetrics();
    this.renderText_evfofk$(ctx);
  };
  LabelUi.prototype.disposeUi_evfofk$ = function (ctx) {
    this.baseUi_tctiu8$_0.disposeUi_evfofk$(ctx);
    this.label.minusAssign_f1kmr1$(this.mesh);
    this.mesh.dispose_evfofk$(ctx);
  };
  LabelUi.prototype.computeTextMetrics = function () {
    var tmp$, tmp$_0;
    this.textWidth = this.font.textWidth_61zpoe$(this.label.text);
    switch (this.label.textAlignment.xAlignment.name) {
      case 'START':
        tmp$ = this.label.padding.left.toUnits_dleff0$(this.label.width, this.label.dpi);
        break;
      case 'CENTER':
        tmp$ = (this.label.width - this.textWidth) / 2.0;
        break;
      case 'END':
        tmp$ = this.label.width - this.textWidth - this.label.padding.right.toUnits_dleff0$(this.label.width, this.label.dpi);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    this.textStartX = tmp$;
    switch (this.label.textAlignment.yAlignment.name) {
      case 'START':
        tmp$_0 = this.label.height - this.label.padding.top.toUnits_dleff0$(this.label.width, this.label.dpi) - this.font.normHeight;
        break;
      case 'CENTER':
        tmp$_0 = (this.label.height - this.font.normHeight) / 2.0;
        break;
      case 'END':
        tmp$_0 = this.label.padding.bottom.toUnits_dleff0$(this.label.height, this.label.dpi);
        break;
      default:tmp$_0 = Kotlin.noWhenBranchMatched();
        break;
    }
    this.textBaseline = tmp$_0;
  };
  LabelUi.prototype.renderText_evfofk$ = function (ctx) {
    this.meshBuilder.color = this.textColor;
    var $this = this.meshBuilder;
    var font = this.font;
    $this.textProps.defaults();
    $this.textProps.font = font;
    var $receiver = $this.textProps;
    $receiver.origin.set_y2kzbl$(this.textStartX, this.textBaseline, dp_0(this.label, 4.0));
    $receiver.text = this.label.text;
    $this.text_lis6zk$($this.textProps);
  };
  LabelUi.prototype.updateTextColor = function () {
    this.textColor.set_d7aj7k$(this.label.textColor.apply());
  };
  LabelUi.prototype.onRender_evfofk$ = function (ctx) {
    return this.baseUi_tctiu8$_0.onRender_evfofk$(ctx);
  };
  LabelUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LabelUi',
    interfaces: [ComponentUi]
  };
  function LayoutSpec() {
    this.width = uns(0.0);
    this.height = uns(0.0);
    this.depth = uns(0.0);
    this.x = uns(0.0);
    this.y = uns(0.0);
    this.z = uns(0.0);
  }
  LayoutSpec.prototype.setOrigin_4ujscr$ = function (x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
  };
  LayoutSpec.prototype.setSize_4ujscr$ = function (width, height, depth) {
    this.width = width;
    this.height = height;
    this.depth = depth;
  };
  LayoutSpec.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LayoutSpec',
    interfaces: []
  };
  function SizeUnit(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function SizeUnit_initFields() {
    SizeUnit_initFields = function () {
    };
    SizeUnit$UN_instance = new SizeUnit('UN', 0);
    SizeUnit$DP_instance = new SizeUnit('DP', 1);
    SizeUnit$MM_instance = new SizeUnit('MM', 2);
    SizeUnit$PC_instance = new SizeUnit('PC', 3);
  }
  var SizeUnit$UN_instance;
  function SizeUnit$UN_getInstance() {
    SizeUnit_initFields();
    return SizeUnit$UN_instance;
  }
  var SizeUnit$DP_instance;
  function SizeUnit$DP_getInstance() {
    SizeUnit_initFields();
    return SizeUnit$DP_instance;
  }
  var SizeUnit$MM_instance;
  function SizeUnit$MM_getInstance() {
    SizeUnit_initFields();
    return SizeUnit$MM_instance;
  }
  var SizeUnit$PC_instance;
  function SizeUnit$PC_getInstance() {
    SizeUnit_initFields();
    return SizeUnit$PC_instance;
  }
  SizeUnit.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SizeUnit',
    interfaces: [Enum]
  };
  function SizeUnit$values() {
    return [SizeUnit$UN_getInstance(), SizeUnit$DP_getInstance(), SizeUnit$MM_getInstance(), SizeUnit$PC_getInstance()];
  }
  SizeUnit.values = SizeUnit$values;
  function SizeUnit$valueOf(name) {
    switch (name) {
      case 'UN':
        return SizeUnit$UN_getInstance();
      case 'DP':
        return SizeUnit$DP_getInstance();
      case 'MM':
        return SizeUnit$MM_getInstance();
      case 'PC':
        return SizeUnit$PC_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.scene.ui.SizeUnit.' + name);
    }
  }
  SizeUnit.valueOf_61zpoe$ = SizeUnit$valueOf;
  function zero() {
    return new SizeSpec(0.0, SizeUnit$UN_getInstance());
  }
  function uns(value, roundToUnit) {
    if (roundToUnit === void 0)
      roundToUnit = false;
    return new SizeSpec(value, SizeUnit$UN_getInstance(), roundToUnit);
  }
  function dps(value, roundToUnit) {
    if (roundToUnit === void 0)
      roundToUnit = false;
    return new SizeSpec(value, SizeUnit$DP_getInstance(), roundToUnit);
  }
  function mms(value, roundToUnit) {
    if (roundToUnit === void 0)
      roundToUnit = false;
    return new SizeSpec(value, SizeUnit$MM_getInstance(), roundToUnit);
  }
  function pcs(value, roundToUnit) {
    if (roundToUnit === void 0)
      roundToUnit = false;
    return new SizeSpec(value, SizeUnit$PC_getInstance(), roundToUnit);
  }
  function pc(pc, size) {
    return size * pc / 100.0;
  }
  function dp(dp, dpi) {
    return dp * dpi / 96.0;
  }
  function mm(mm, dpi) {
    return mm * dpi / 25.4;
  }
  function pcW($receiver, pc_0) {
    return pc(pc_0, $receiver.width);
  }
  function pcH($receiver, pc_0) {
    return pc(pc_0, $receiver.height);
  }
  function dp_0($receiver, pc) {
    return dp(pc, $receiver.dpi);
  }
  function mm_0($receiver, pc) {
    return mm(pc, $receiver.dpi);
  }
  function pcR(pc, size) {
    return round(size * pc / 100.0);
  }
  function dpR(dp, dpi) {
    return round(dp * dpi / 96.0);
  }
  function mmR(mm, dpi) {
    return round(mm * dpi / 25.4);
  }
  function pcWR($receiver, pc) {
    return pcR(pc, $receiver.width);
  }
  function pcHR($receiver, pc) {
    return pcR(pc, $receiver.height);
  }
  function dpR_0($receiver, pc) {
    return dpR(pc, $receiver.dpi);
  }
  function mmR_0($receiver, pc) {
    return mmR(pc, $receiver.dpi);
  }
  function SizeSpec(value, unit, roundToUnit) {
    if (roundToUnit === void 0)
      roundToUnit = false;
    this.value = value;
    this.unit = unit;
    this.roundToUnit = roundToUnit;
  }
  SizeSpec.prototype.toUnits_dleff0$ = function (size, dpi) {
    var tmp$;
    if (this.roundToUnit) {
      switch (this.unit.name) {
        case 'UN':
          tmp$ = round(this.value);
          break;
        case 'DP':
          tmp$ = dpR(this.value, dpi);
          break;
        case 'MM':
          tmp$ = mmR(this.value, dpi);
          break;
        case 'PC':
          tmp$ = pcR(this.value, size);
          break;
        default:tmp$ = Kotlin.noWhenBranchMatched();
          break;
      }
    }
     else {
      switch (this.unit.name) {
        case 'UN':
          tmp$ = this.value;
          break;
        case 'DP':
          tmp$ = dp(this.value, dpi);
          break;
        case 'MM':
          tmp$ = mm(this.value, dpi);
          break;
        case 'PC':
          tmp$ = pc(this.value, size);
          break;
        default:tmp$ = Kotlin.noWhenBranchMatched();
          break;
      }
    }
    return tmp$;
  };
  SizeSpec.prototype.plus_m986jv$ = function (size) {
    return new CombSizeSpec(this, size, true);
  };
  SizeSpec.prototype.minus_m986jv$ = function (size) {
    return new CombSizeSpec(this, size, false);
  };
  SizeSpec.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, SizeSpec))
      return false;
    if (this.value !== other.value)
      return false;
    if (this.unit !== other.unit)
      return false;
    if (this.roundToUnit !== other.roundToUnit)
      return false;
    return true;
  };
  SizeSpec.prototype.hashCode = function () {
    var result = hashCode(this.value);
    result = (31 * result | 0) + this.unit.hashCode() | 0;
    result = (31 * result | 0) + hashCode(this.roundToUnit) | 0;
    return result;
  };
  SizeSpec.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SizeSpec',
    interfaces: []
  };
  function CombSizeSpec(left, right, add) {
    SizeSpec.call(this, 0.0, SizeUnit$UN_getInstance());
    this.left = left;
    this.right = right;
    this.add = add;
  }
  CombSizeSpec.prototype.toUnits_dleff0$ = function (size, dpi) {
    var leftUns = this.left.toUnits_dleff0$(size, dpi);
    var rightUns = this.right.toUnits_dleff0$(size, dpi);
    if (this.add) {
      return leftUns + rightUns;
    }
     else {
      return leftUns - rightUns;
    }
  };
  CombSizeSpec.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CombSizeSpec',
    interfaces: [SizeSpec]
  };
  function Margin(top, bottom, left, right) {
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
  }
  Margin.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Margin',
    interfaces: []
  };
  Margin.prototype.component1 = function () {
    return this.top;
  };
  Margin.prototype.component2 = function () {
    return this.bottom;
  };
  Margin.prototype.component3 = function () {
    return this.left;
  };
  Margin.prototype.component4 = function () {
    return this.right;
  };
  Margin.prototype.copy_107250$ = function (top, bottom, left, right) {
    return new Margin(top === void 0 ? this.top : top, bottom === void 0 ? this.bottom : bottom, left === void 0 ? this.left : left, right === void 0 ? this.right : right);
  };
  Margin.prototype.toString = function () {
    return 'Margin(top=' + Kotlin.toString(this.top) + (', bottom=' + Kotlin.toString(this.bottom)) + (', left=' + Kotlin.toString(this.left)) + (', right=' + Kotlin.toString(this.right)) + ')';
  };
  Margin.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.top) | 0;
    result = result * 31 + Kotlin.hashCode(this.bottom) | 0;
    result = result * 31 + Kotlin.hashCode(this.left) | 0;
    result = result * 31 + Kotlin.hashCode(this.right) | 0;
    return result;
  };
  Margin.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.top, other.top) && Kotlin.equals(this.bottom, other.bottom) && Kotlin.equals(this.left, other.left) && Kotlin.equals(this.right, other.right)))));
  };
  function Gravity(xAlignment, yAlignment) {
    this.xAlignment = xAlignment;
    this.yAlignment = yAlignment;
  }
  Gravity.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Gravity',
    interfaces: []
  };
  Gravity.prototype.component1 = function () {
    return this.xAlignment;
  };
  Gravity.prototype.component2 = function () {
    return this.yAlignment;
  };
  Gravity.prototype.copy_9pkgn0$ = function (xAlignment, yAlignment) {
    return new Gravity(xAlignment === void 0 ? this.xAlignment : xAlignment, yAlignment === void 0 ? this.yAlignment : yAlignment);
  };
  Gravity.prototype.toString = function () {
    return 'Gravity(xAlignment=' + Kotlin.toString(this.xAlignment) + (', yAlignment=' + Kotlin.toString(this.yAlignment)) + ')';
  };
  Gravity.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.xAlignment) | 0;
    result = result * 31 + Kotlin.hashCode(this.yAlignment) | 0;
    return result;
  };
  Gravity.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.xAlignment, other.xAlignment) && Kotlin.equals(this.yAlignment, other.yAlignment)))));
  };
  function Alignment(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function Alignment_initFields() {
    Alignment_initFields = function () {
    };
    Alignment$START_instance = new Alignment('START', 0);
    Alignment$CENTER_instance = new Alignment('CENTER', 1);
    Alignment$END_instance = new Alignment('END', 2);
  }
  var Alignment$START_instance;
  function Alignment$START_getInstance() {
    Alignment_initFields();
    return Alignment$START_instance;
  }
  var Alignment$CENTER_instance;
  function Alignment$CENTER_getInstance() {
    Alignment_initFields();
    return Alignment$CENTER_instance;
  }
  var Alignment$END_instance;
  function Alignment$END_getInstance() {
    Alignment_initFields();
    return Alignment$END_instance;
  }
  Alignment.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Alignment',
    interfaces: [Enum]
  };
  function Alignment$values() {
    return [Alignment$START_getInstance(), Alignment$CENTER_getInstance(), Alignment$END_getInstance()];
  }
  Alignment.values = Alignment$values;
  function Alignment$valueOf(name) {
    switch (name) {
      case 'START':
        return Alignment$START_getInstance();
      case 'CENTER':
        return Alignment$CENTER_getInstance();
      case 'END':
        return Alignment$END_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.scene.ui.Alignment.' + name);
    }
  }
  Alignment.valueOf_61zpoe$ = Alignment$valueOf;
  function Slider(name, min, max, value, root) {
    UiComponent.call(this, name, root);
    this.onValueChanged = ArrayList_init();
    this.trackColor = Color$Companion_getInstance().GRAY;
    this.trackColorHighlighted = new ThemeOrCustomProp(Color$Companion_getInstance().LIGHT_GRAY);
    this.knobColor = new ThemeOrCustomProp(Color$Companion_getInstance().WHITE);
    this.trackWidth = 0.0;
    this.knobSize = 0.0;
    this.knobPosition = MutableVec2f_init();
    this.min_c38r2y$_0 = min;
    this.max_c38wrw$_0 = max;
    this.value_ymubdz$_0 = value;
    this.prevHit_0 = MutableVec2f_init();
    this.hitDelta_0 = MutableVec2f_init();
    this.onHover.add_11rb$(Slider_init$lambda(this));
    this.onHoverExit.add_11rb$(Slider_init$lambda_0(this));
  }
  Object.defineProperty(Slider.prototype, 'min', {
    get: function () {
      return this.min_c38r2y$_0;
    },
    set: function (value) {
      if (value !== this.min_c38r2y$_0) {
        this.min_c38r2y$_0 = value;
        this.requestUiUpdate();
      }
    }
  });
  Object.defineProperty(Slider.prototype, 'max', {
    get: function () {
      return this.max_c38wrw$_0;
    },
    set: function (value) {
      if (value !== this.max_c38wrw$_0) {
        this.max_c38wrw$_0 = value;
        this.requestUiUpdate();
      }
    }
  });
  Object.defineProperty(Slider.prototype, 'value', {
    get: function () {
      return this.value_ymubdz$_0;
    },
    set: function (value) {
      var tmp$;
      if (value !== this.value_ymubdz$_0) {
        var min = this.min;
        var max = this.max;
        var clamp$result;
        if (value < min) {
          clamp$result = min;
        }
         else if (value > max) {
          clamp$result = max;
        }
         else {
          clamp$result = value;
        }
        this.value_ymubdz$_0 = clamp$result;
        this.requestUiUpdate();
        tmp$ = this.onValueChanged;
        for (var i = 0; i !== tmp$.size; ++i) {
          this.onValueChanged.get_za3lpa$(i)(this, this.value_ymubdz$_0);
        }
      }
    }
  });
  Slider.prototype.setValue_y2kzbl$ = function (min, max, value) {
    this.min = min;
    this.max = max;
    this.value = value;
  };
  Slider.prototype.isOverKnob_0 = function (x, y) {
    var dx = x - this.knobPosition.x;
    var dy = y - this.knobPosition.y;
    return dx * dx + dy * dy < this.knobSize * this.knobSize;
  };
  Slider.prototype.handleDrag_t4w9y2$ = function (dragPtrs, ctx) {
    if (dragPtrs.size === 1 && dragPtrs.get_za3lpa$(0).isValid && dragPtrs.get_za3lpa$(0).isLeftButtonDown) {
      this.value = this.value + this.hitDelta_0.x / this.trackWidth * (this.max - this.min);
      return InputManager$DragHandler$Companion_getInstance().HANDLED;
    }
     else {
      return InputManager$DragHandler$Companion_getInstance().REMOVE_HANDLER;
    }
  };
  Slider.prototype.setThemeProps = function () {
    UiComponent.prototype.setThemeProps.call(this);
    this.knobColor.setTheme_11rb$(this.root.theme.accentColor);
    this.trackColorHighlighted.setTheme_11rb$(MutableColor_init().add_y83vuj$(this.root.theme.accentColor, 0.4));
  };
  Slider.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.newSliderUi_l85jm8$(this);
  };
  function Slider_init$lambda(this$Slider) {
    return function ($receiver, ptr, rt, ctx) {
      var tmp$;
      this$Slider.hitDelta_0.set_dleff0$(rt.hitPositionLocal.x, rt.hitPositionLocal.y).subtract_czzhjp$(this$Slider.prevHit_0);
      this$Slider.prevHit_0.set_dleff0$(rt.hitPositionLocal.x, rt.hitPositionLocal.y);
      if (ptr.isLeftButtonEvent && ptr.isLeftButtonDown && this$Slider.isOverKnob_0(this$Slider.prevHit_0.x, this$Slider.prevHit_0.y)) {
        (tmp$ = $receiver.scene) != null ? (tmp$.registerDragHandler_dsvxak$(this$Slider), Unit) : null;
      }
      return Unit;
    };
  }
  function Slider_init$lambda_0(this$Slider) {
    return function ($receiver, f, f_0, f_1) {
      this$Slider.hitDelta_0.set_dleff0$(0.0, 0.0);
      return Unit;
    };
  }
  Slider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Slider',
    interfaces: [InputManager$DragHandler, UiComponent]
  };
  function SliderUi(slider, baseUi) {
    this.slider = slider;
    this.baseUi = baseUi;
    this.meshData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().NORMALS, Attribute$Companion_getInstance().COLORS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    this.meshBuilder = new MeshBuilder(this.meshData);
    this.mesh = new Mesh(this.meshData);
  }
  SliderUi.prototype.updateComponentAlpha = function () {
    this.baseUi.updateComponentAlpha();
    var shader = this.mesh.shader;
    if (Kotlin.isType(shader, BasicShader)) {
      shader.alpha = this.slider.alpha;
    }
  };
  function SliderUi$createUi$lambda($receiver) {
    $receiver.lightModel = LightModel$PHONG_LIGHTING_getInstance();
    $receiver.colorModel = ColorModel$VERTEX_COLOR_getInstance();
    $receiver.isAlpha = true;
    return Unit;
  }
  SliderUi.prototype.createUi_evfofk$ = function (ctx) {
    this.baseUi.createUi_evfofk$(ctx);
    this.mesh.shader = basicShader(SliderUi$createUi$lambda);
    this.slider.plusAssign_f1kmr1$(this.mesh);
  };
  SliderUi.prototype.disposeUi_evfofk$ = function (ctx) {
    this.baseUi.disposeUi_evfofk$(ctx);
    this.mesh.dispose_evfofk$(ctx);
    this.slider.minusAssign_f1kmr1$(this.mesh);
  };
  SliderUi.prototype.updateUi_evfofk$ = function (ctx) {
    this.baseUi.updateUi_evfofk$(ctx);
    this.slider.knobSize = dp_0(this.slider, 10.0);
    var trackH = dp_0(this.slider, 6.0);
    var x = this.slider.padding.left.toUnits_dleff0$(this.slider.width, this.slider.dpi) + this.slider.knobSize;
    var y = (this.slider.height - trackH) / 2;
    var p = (this.slider.value - this.slider.min) / (this.slider.max - this.slider.min);
    this.slider.trackWidth = this.slider.width - x - this.slider.knobSize - this.slider.padding.right.toUnits_dleff0$(this.slider.width, this.slider.dpi);
    this.slider.knobPosition.set_dleff0$(x + this.slider.trackWidth * p, this.slider.height / 2.0);
    this.slider.setupBuilder_84rojv$(this.meshBuilder);
    if (this.slider.value > this.slider.min) {
      this.meshBuilder.color = this.slider.trackColorHighlighted.apply();
      var $this = this.meshBuilder;
      var $receiver = $this.rectProps.defaults();
      $receiver.origin.set_y2kzbl$(x, y, dp_0(this.slider, 4.0));
      $receiver.size.set_dleff0$(this.slider.knobPosition.x - x + trackH, trackH);
      $receiver.cornerRadius = trackH / 2.0;
      $receiver.cornerSteps = 4;
      $this.rect_e5k3t5$($this.rectProps);
    }
    if (this.slider.value < this.slider.max) {
      this.meshBuilder.color = this.slider.trackColor;
      var $this_0 = this.meshBuilder;
      var $receiver_0 = $this_0.rectProps.defaults();
      $receiver_0.origin.set_y2kzbl$(this.slider.knobPosition.x - trackH, y, dp_0(this.slider, 4.0));
      $receiver_0.size.set_dleff0$(this.slider.trackWidth - this.slider.knobPosition.x + x + trackH, trackH);
      $receiver_0.cornerRadius = trackH / 2.0;
      $receiver_0.cornerSteps = 4;
      $this_0.rect_e5k3t5$($this_0.rectProps);
    }
    this.meshBuilder.color = this.slider.knobColor.apply();
    var $this_1 = this.meshBuilder;
    var $receiver_1 = $this_1.circleProps.defaults();
    $receiver_1.center.set_y2kzbl$(this.slider.knobPosition.x, this.slider.knobPosition.y, dp_0(this.slider, 6.0));
    $receiver_1.radius = this.slider.knobSize;
    $receiver_1.steps = 30;
    $this_1.circle_59f34t$($this_1.circleProps);
  };
  SliderUi.prototype.onRender_evfofk$ = function (ctx) {
    return this.baseUi.onRender_evfofk$(ctx);
  };
  SliderUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SliderUi',
    interfaces: [ComponentUi]
  };
  function TextField(name, root) {
    Label.call(this, name, root);
    this.editText = new EditableText();
    this.onRender.add_11rb$(TextField_init$lambda(this));
  }
  TextField.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.newTextFieldUi_p39bdq$(this);
  };
  function TextField_init$lambda(this$TextField) {
    return function ($receiver, ctx) {
      var tmp$;
      if (!ctx.inputMgr.keyEvents.isEmpty()) {
        tmp$ = ctx.inputMgr.keyEvents.iterator();
        while (tmp$.hasNext()) {
          var e = tmp$.next();
          if (e.isCharTyped) {
            this$TextField.editText.charTyped_s8itvh$(unboxChar(e.typedChar));
          }
           else if (e.isPressed) {
            switch (e.keyCode) {
              case -17:
                this$TextField.editText.backspace();
                break;
              case -19:
                this$TextField.editText.delete();
                break;
              case -25:
                if (e.isCtrlDown) {
                  this$TextField.editText.moveCaret_fzusl$(EditableText$Companion_getInstance().MOVE_WORD_LEFT, e.isShiftDown);
                }
                 else {
                  this$TextField.editText.moveCaret_fzusl$(EditableText$Companion_getInstance().MOVE_LEFT, e.isShiftDown);
                }

                break;
              case -26:
                if (e.isCtrlDown) {
                  this$TextField.editText.moveCaret_fzusl$(EditableText$Companion_getInstance().MOVE_WORD_RIGHT, e.isShiftDown);
                }
                 else {
                  this$TextField.editText.moveCaret_fzusl$(EditableText$Companion_getInstance().MOVE_RIGHT, e.isShiftDown);
                }

                break;
              case -21:
                this$TextField.editText.moveCaret_fzusl$(EditableText$Companion_getInstance().MOVE_START, e.isShiftDown);
                break;
              case -22:
                this$TextField.editText.moveCaret_fzusl$(EditableText$Companion_getInstance().MOVE_END, e.isShiftDown);
                break;
            }
          }
        }
        this$TextField.text = this$TextField.editText.toString();
      }
      return Unit;
    };
  }
  TextField.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextField',
    interfaces: [Label]
  };
  function TextFieldUi(textField, baseUi) {
    LabelUi.call(this, textField, baseUi);
    this.textField = textField;
    this.caretAlphaAnimator_mmbcs$_0 = new CosAnimator(new InterpolatedFloat(0.0, 1.0));
    this.caretColor_qpd03i$_0 = MutableColor_init();
    this.caretDrawPos_hkh11t$_0 = new InterpolatedFloat(0.0, 0.0);
    this.caretPosAnimator_3ucebe$_0 = new LinearAnimator(this.caretDrawPos_hkh11t$_0);
    this.caretAlphaAnimator_mmbcs$_0.duration = 0.5;
    this.caretAlphaAnimator_mmbcs$_0.repeating = Animator$Companion_getInstance().REPEAT_TOGGLE_DIR;
    this.caretPosAnimator_3ucebe$_0.duration = 0.1;
  }
  TextFieldUi.prototype.onRender_evfofk$ = function (ctx) {
    this.textField.requestUiUpdate();
    LabelUi.prototype.onRender_evfofk$.call(this, ctx);
  };
  TextFieldUi.prototype.renderText_evfofk$ = function (ctx) {
    var tmp$;
    var x1 = this.label.padding.left.toUnits_dleff0$(this.label.width, this.label.dpi);
    var x2 = this.label.width - this.label.padding.right.toUnits_dleff0$(this.label.width, this.label.dpi);
    var y = this.textBaseline - this.font.fontProps.sizeUnits * 0.2;
    var caretX = {v: this.textStartX};
    var selectionX = {v: this.textStartX};
    if (this.textField.editText.caretPosition > 0 || this.textField.editText.selectionStart > 0) {
      var a = this.textField.editText.caretPosition;
      var b = this.textField.editText.selectionStart;
      tmp$ = Math_0.max(a, b) - 1 | 0;
      for (var i = 0; i <= tmp$; i++) {
        var w = this.font.charWidth_s8itvh$(unboxChar(this.textField.editText.get_za3lpa$(i)));
        if (i < this.textField.editText.caretPosition) {
          caretX.v += w;
        }
        if (i < this.textField.editText.selectionStart) {
          selectionX.v += w;
        }
      }
    }
    if (caretX.v !== this.caretDrawPos_hkh11t$_0.to) {
      this.caretDrawPos_hkh11t$_0.from = this.caretDrawPos_hkh11t$_0.value;
      this.caretDrawPos_hkh11t$_0.to = caretX.v;
      this.caretPosAnimator_3ucebe$_0.progress = 0.0;
      this.caretPosAnimator_3ucebe$_0.speed = 1.0;
    }
    caretX.v = this.caretPosAnimator_3ucebe$_0.tick_evfofk$(ctx);
    var $this = this.meshBuilder;
    $this.transform.push();
    $this.translate_y2kzbl$(0.0, 0.0, dp_0(this.label, 4.0));
    this.meshBuilder.color = this.label.root.theme.accentColor;
    this.meshBuilder.line_s2l86p$(x1, y, x2, y, dp_0(this.label, 1.5));
    if (this.textField.editText.selectionStart !== this.textField.editText.caretPosition) {
      this.caretColor_qpd03i$_0.set_d7aj7k$(this.label.root.theme.accentColor);
      this.caretColor_qpd03i$_0.a = 0.4;
      this.meshBuilder.color = this.caretColor_qpd03i$_0;
      var $this_0 = this.meshBuilder;
      var $receiver = $this_0.rectProps.defaults();
      $receiver.origin.set_y2kzbl$(caretX.v, y, 0.0);
      $receiver.size.set_dleff0$(selectionX.v - caretX.v, this.font.fontProps.sizeUnits * 1.2);
      $this_0.rect_e5k3t5$($this_0.rectProps);
    }
    this.caretColor_qpd03i$_0.set_d7aj7k$(this.label.root.theme.accentColor);
    this.caretColor_qpd03i$_0.a = this.caretAlphaAnimator_mmbcs$_0.tick_evfofk$(ctx);
    this.meshBuilder.color = this.caretColor_qpd03i$_0;
    this.meshBuilder.line_s2l86p$(caretX.v, y, caretX.v, this.textBaseline + this.font.fontProps.sizeUnits, dp_0(this.label, 1.5));
    $this.transform.pop();
    LabelUi.prototype.renderText_evfofk$.call(this, ctx);
  };
  TextFieldUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextFieldUi',
    interfaces: [LabelUi]
  };
  function EditableText(txt) {
    EditableText$Companion_getInstance();
    if (txt === void 0)
      txt = '';
    this.text_x0rs8x$_0 = txt;
    this.caretPosition_lco9r8$_0 = 0;
    this.selectionStart_trwhxy$_0 = 0;
  }
  Object.defineProperty(EditableText.prototype, 'text', {
    get: function () {
      return this.text_x0rs8x$_0;
    },
    set: function (value) {
      if (this.caretPosition > value.length) {
        this.caretPosition = value.length;
      }
      if (this.selectionStart > value.length) {
        this.selectionStart = value.length;
      }
      this.text_x0rs8x$_0 = value;
    }
  });
  Object.defineProperty(EditableText.prototype, 'caretPosition', {
    get: function () {
      return this.caretPosition_lco9r8$_0;
    },
    set: function (value) {
      var max = this.text.length;
      var clamp$result;
      if (value < 0) {
        clamp$result = 0;
      }
       else if (value > max) {
        clamp$result = max;
      }
       else {
        clamp$result = value;
      }
      this.caretPosition_lco9r8$_0 = clamp$result;
    }
  });
  Object.defineProperty(EditableText.prototype, 'selectionStart', {
    get: function () {
      return this.selectionStart_trwhxy$_0;
    },
    set: function (value) {
      var max = this.text.length;
      var clamp$result;
      if (value < 0) {
        clamp$result = 0;
      }
       else if (value > max) {
        clamp$result = max;
      }
       else {
        clamp$result = value;
      }
      this.selectionStart_trwhxy$_0 = clamp$result;
    }
  });
  EditableText.prototype.charTyped_s8itvh$ = function (c) {
    this.replaceSelection_61zpoe$(String.fromCharCode(c));
  };
  EditableText.prototype.moveCaret_fzusl$ = function (mode, selection) {
    switch (mode) {
      case 1:
        this.caretPosition = this.caretPosition - 1 | 0;
        break;
      case 2:
        this.caretPosition = this.caretPosition + 1 | 0;
        break;
      case 5:
        this.caretPosition = 0;
        break;
      case 6:
        this.caretPosition = this.text.length;
        break;
      case 3:
        this.moveWordLeft_0();
        break;
      case 4:
        this.moveWordRight_0();
        break;
    }
    if (!selection) {
      this.selectionStart = this.caretPosition;
    }
  };
  EditableText.prototype.moveWordLeft_0 = function () {
    if (this.caretPosition > 0) {
      var $receiver = this.text;
      var endIndex = this.caretPosition;
      var idx = lastIndexOf($receiver.substring(0, endIndex), 32);
      if (idx < 0) {
        this.caretPosition = 0;
      }
       else {
        this.caretPosition = idx;
      }
    }
  };
  EditableText.prototype.moveWordRight_0 = function () {
    if (this.caretPosition < this.text.length) {
      var idx = indexOf(this.text, 32, this.caretPosition);
      if (idx < 0) {
        this.caretPosition = this.text.length;
      }
       else {
        this.caretPosition = idx + 1 | 0;
      }
    }
  };
  EditableText.prototype.backspace = function () {
    if (this.selectionStart !== this.caretPosition) {
      this.replaceSelection_61zpoe$('');
    }
     else if (this.caretPosition > 0) {
      this.selectionStart = (this.caretPosition = this.caretPosition - 1 | 0, this.caretPosition);
      var $receiver = this.text;
      var endIndex = this.caretPosition;
      var tmp$ = $receiver.substring(0, endIndex);
      var $receiver_0 = this.text;
      var startIndex = this.caretPosition + 1 | 0;
      this.text = tmp$ + $receiver_0.substring(startIndex);
    }
  };
  EditableText.prototype.delete = function () {
    if (this.selectionStart !== this.caretPosition) {
      this.replaceSelection_61zpoe$('');
    }
     else if (this.caretPosition < this.text.length) {
      var $receiver = this.text;
      var endIndex = this.caretPosition;
      var tmp$ = $receiver.substring(0, endIndex);
      var $receiver_0 = this.text;
      var startIndex = this.caretPosition + 1 | 0;
      this.text = tmp$ + $receiver_0.substring(startIndex);
    }
  };
  EditableText.prototype.replaceSelection_61zpoe$ = function (string) {
    var a = this.selectionStart;
    var b = this.caretPosition;
    var start = Math_0.min(a, b);
    var a_0 = this.selectionStart;
    var b_0 = this.caretPosition;
    var end = Math_0.max(a_0, b_0);
    this.text = this.text.substring(0, start) + string + this.text.substring(end);
    var a_1 = this.selectionStart;
    var b_1 = this.caretPosition;
    this.caretPosition = Math_0.min(a_1, b_1) + string.length | 0;
    this.selectionStart = this.caretPosition;
  };
  EditableText.prototype.get_za3lpa$ = function (index) {
    return toBoxedChar(this.text.charCodeAt(index));
  };
  EditableText.prototype.toString = function () {
    return this.text;
  };
  function EditableText$Companion() {
    EditableText$Companion_instance = this;
    this.MOVE_LEFT = 1;
    this.MOVE_RIGHT = 2;
    this.MOVE_WORD_LEFT = 3;
    this.MOVE_WORD_RIGHT = 4;
    this.MOVE_START = 5;
    this.MOVE_END = 6;
  }
  EditableText$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var EditableText$Companion_instance = null;
  function EditableText$Companion_getInstance() {
    if (EditableText$Companion_instance === null) {
      new EditableText$Companion();
    }
    return EditableText$Companion_instance;
  }
  EditableText.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EditableText',
    interfaces: []
  };
  function ToggleButton(name, root, initState) {
    if (initState === void 0)
      initState = false;
    Button.call(this, name, root);
    this.onStateChange = ArrayList_init();
    this.knobColorOn = Color$Companion_getInstance().WHITE;
    this.knobColorOff = Color$Companion_getInstance().LIGHT_GRAY;
    this.trackColor = Color$Companion_getInstance().GRAY;
    this.isEnabled_gffhto$_0 = initState;
    this.textAlignment = new Gravity(Alignment$START_getInstance(), Alignment$CENTER_getInstance());
  }
  Object.defineProperty(ToggleButton.prototype, 'isEnabled', {
    get: function () {
      return this.isEnabled_gffhto$_0;
    },
    set: function (value) {
      if (value !== this.isEnabled_gffhto$_0) {
        this.isEnabled_gffhto$_0 = value;
        this.fireStateChanged();
      }
    }
  });
  ToggleButton.prototype.fireStateChanged = function () {
    var tmp$;
    tmp$ = this.onStateChange;
    for (var i = 0; i !== tmp$.size; ++i) {
      this.onStateChange.get_za3lpa$(i)(this);
    }
  };
  ToggleButton.prototype.fireOnClick_1tlxzm$ = function (ptr, rt, ctx) {
    this.isEnabled = !this.isEnabled;
    Button.prototype.fireOnClick_1tlxzm$.call(this, ptr, rt, ctx);
  };
  ToggleButton.prototype.setThemeProps = function () {
    Button.prototype.setThemeProps.call(this);
    this.knobColorOn = this.root.theme.accentColor;
  };
  ToggleButton.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.newToggleButtonUi_r83hfv$(this);
  };
  ToggleButton.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ToggleButton',
    interfaces: [Button]
  };
  function ToggleButtonUi(tb, baseUi) {
    ButtonUi.call(this, tb, baseUi);
    this.tb = tb;
    this.knobAnimator = new CosAnimator(new InterpolatedFloat(0.0, 1.0));
    this.knobColor = MutableColor_init();
    this.stateChangedListener = ToggleButtonUi$stateChangedListener$lambda(this);
  }
  function ToggleButtonUi$createUi$lambda(this$ToggleButtonUi) {
    return function (it) {
      this$ToggleButtonUi.tb.requestUiUpdate();
      return Unit;
    };
  }
  ToggleButtonUi.prototype.createUi_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    ButtonUi.prototype.createUi_evfofk$.call(this, ctx);
    this.knobAnimator.speed = 0.0;
    this.knobAnimator.duration = 0.15;
    tmp$_0 = this.knobAnimator.value;
    if (this.tb.isEnabled) {
      tmp$ = 1.0;
    }
     else {
      tmp$ = 0.0;
    }
    tmp$_0.value = tmp$;
    this.knobAnimator.value.onUpdate = ToggleButtonUi$createUi$lambda(this);
    var $receiver = this.tb.onStateChange;
    var element = this.stateChangedListener;
    $receiver.add_11rb$(element);
  };
  ToggleButtonUi.prototype.disposeUi_evfofk$ = function (ctx) {
    ButtonUi.prototype.disposeUi_evfofk$.call(this, ctx);
    var $receiver = this.tb.onStateChange;
    var element = this.stateChangedListener;
    $receiver.remove_11rb$(element);
  };
  ToggleButtonUi.prototype.updateUi_evfofk$ = function (ctx) {
    ButtonUi.prototype.updateUi_evfofk$.call(this, ctx);
    var paddingR = this.tb.padding.right.toUnits_dleff0$(this.tb.width, this.tb.dpi);
    var trackW = dp_0(this.tb, 24.0);
    var trackH = dp_0(this.tb, 6.0);
    var knobR = dp_0(this.tb, 10.0);
    var x = this.tb.width - paddingR - trackW - knobR;
    var y = (this.tb.height - trackH) / 2.0;
    this.meshBuilder.color = this.tb.trackColor;
    var $this = this.meshBuilder;
    var $receiver = $this.rectProps.defaults();
    $receiver.origin.set_y2kzbl$(x, y, dp_0(this.tb, 4.0));
    $receiver.size.set_dleff0$(trackW, trackH);
    $receiver.cornerRadius = trackH / 2.0;
    $receiver.cornerSteps = 4;
    $this.rect_e5k3t5$($this.rectProps);
    var anim = this.knobAnimator.value.value;
    this.knobColor.clear();
    this.knobColor.add_y83vuj$(this.tb.knobColorOff, 1.0 - anim);
    this.knobColor.add_y83vuj$(this.tb.knobColorOn, anim);
    this.meshBuilder.color = this.knobColor;
    var $this_0 = this.meshBuilder;
    var $receiver_0 = $this_0.circleProps.defaults();
    $receiver_0.center.set_y2kzbl$(x + trackW * anim, y + trackH / 2.0, dp_0(this.tb, 6.0));
    $receiver_0.radius = knobR;
    $receiver_0.steps = 30;
    $this_0.circle_59f34t$($this_0.circleProps);
  };
  ToggleButtonUi.prototype.onRender_evfofk$ = function (ctx) {
    ButtonUi.prototype.onRender_evfofk$.call(this, ctx);
    this.knobAnimator.tick_evfofk$(ctx);
  };
  function ToggleButtonUi$stateChangedListener$lambda(this$ToggleButtonUi) {
    return function ($receiver) {
      if ($receiver.isEnabled) {
        this$ToggleButtonUi.knobAnimator.speed = 1.0;
      }
       else {
        this$ToggleButtonUi.knobAnimator.speed = -1.0;
      }
      return Unit;
    };
  }
  ToggleButtonUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ToggleButtonUi',
    interfaces: [ButtonUi]
  };
  function UiComponent(name, root) {
    TransformGroup.call(this, name);
    this.root = root;
    this.contentBounds = new BoundingBox();
    this.layoutSpec = new LayoutSpec();
    this.padding_iyk9nn$_0 = new Margin(dps(16.0), dps(16.0), dps(16.0), dps(16.0));
    this.ui = new ThemeOrCustomProp(new BlankComponentUi());
    this.alpha_e9jtsg$_0 = 1.0;
    this.isThemeUpdate_yv706e$_0 = true;
    this.isUiUpdate_l4k16n$_0 = true;
  }
  Object.defineProperty(UiComponent.prototype, 'posX', {
    get: function () {
      return this.contentBounds.min.x;
    }
  });
  Object.defineProperty(UiComponent.prototype, 'posY', {
    get: function () {
      return this.contentBounds.min.y;
    }
  });
  Object.defineProperty(UiComponent.prototype, 'posZ', {
    get: function () {
      return this.contentBounds.min.z;
    }
  });
  Object.defineProperty(UiComponent.prototype, 'width', {
    get: function () {
      return this.contentBounds.size.x;
    }
  });
  Object.defineProperty(UiComponent.prototype, 'height', {
    get: function () {
      return this.contentBounds.size.y;
    }
  });
  Object.defineProperty(UiComponent.prototype, 'depth', {
    get: function () {
      return this.contentBounds.size.z;
    }
  });
  Object.defineProperty(UiComponent.prototype, 'padding', {
    get: function () {
      return this.padding_iyk9nn$_0;
    },
    set: function (value) {
      var tmp$;
      if (!((tmp$ = this.padding_iyk9nn$_0) != null ? tmp$.equals(value) : null)) {
        this.padding_iyk9nn$_0 = value;
        this.isUiUpdate_l4k16n$_0 = true;
      }
    }
  });
  Object.defineProperty(UiComponent.prototype, 'alpha', {
    get: function () {
      return this.alpha_e9jtsg$_0;
    },
    set: function (value) {
      if (this.alpha_e9jtsg$_0 !== value) {
        this.alpha_e9jtsg$_0 = value;
        this.updateComponentAlpha();
      }
    }
  });
  Object.defineProperty(UiComponent.prototype, 'dpi', {
    get: function () {
      return this.root.uiDpi;
    }
  });
  UiComponent.prototype.setupBuilder_84rojv$ = function (builder) {
    builder.clear();
    builder.identity();
    builder.translate_czzhiu$(this.contentBounds.min);
  };
  UiComponent.prototype.requestThemeUpdate = function () {
    this.isThemeUpdate_yv706e$_0 = true;
  };
  UiComponent.prototype.requestUiUpdate = function () {
    this.isUiUpdate_l4k16n$_0 = true;
  };
  UiComponent.prototype.updateComponentAlpha = function () {
    this.ui.prop.updateComponentAlpha();
  };
  UiComponent.prototype.updateUi_evfofk$ = function (ctx) {
    this.ui.prop.updateUi_evfofk$(ctx);
  };
  UiComponent.prototype.updateTheme_evfofk$ = function (ctx) {
    this.ui.prop.disposeUi_evfofk$(ctx);
    this.ui.setTheme_11rb$(this.createThemeUi_evfofk$(ctx)).apply();
    this.setThemeProps();
    this.ui.prop.createUi_evfofk$(ctx);
    this.ui.prop.updateComponentAlpha();
    this.requestUiUpdate();
  };
  UiComponent.prototype.setThemeProps = function () {
  };
  UiComponent.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.componentUi(this);
  };
  UiComponent.prototype.render_evfofk$ = function (ctx) {
    if (this.isThemeUpdate_yv706e$_0) {
      this.isThemeUpdate_yv706e$_0 = false;
      this.updateTheme_evfofk$(ctx);
    }
    if (this.isUiUpdate_l4k16n$_0) {
      this.isUiUpdate_l4k16n$_0 = false;
      this.updateUi_evfofk$(ctx);
    }
    if (this.alpha !== 0.0) {
      this.ui.prop.onRender_evfofk$(ctx);
      TransformGroup.prototype.render_evfofk$.call(this, ctx);
    }
  };
  UiComponent.prototype.doLayout_oytkew$ = function (bounds, ctx) {
    if (!this.contentBounds.isEqual_ea4od8$(bounds)) {
      this.contentBounds.set_ea4od8$(bounds);
      this.requestUiUpdate();
    }
  };
  UiComponent.prototype.rayTest_jljx4v$ = function (test) {
    var hitNode = test.hitNode;
    TransformGroup.prototype.rayTest_jljx4v$.call(this, test);
    if (!equals(hitNode, test.hitNode) && !Kotlin.isType(test.hitNode, UiComponent)) {
      test.hitNode = this;
      test.hitPositionLocal.subtract_czzhiu$(this.contentBounds.min);
    }
  };
  UiComponent.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UiComponent',
    interfaces: [TransformGroup]
  };
  function UiContainer(name, root) {
    UiComponent.call(this, name, root);
    this.posInParent = MutableVec3f_init();
    this.isLayoutNeeded_exywcf$_0 = true;
    this.tmpChildBounds_j2uogg$_0 = new BoundingBox();
  }
  UiContainer.prototype.requestLayout = function () {
    this.isLayoutNeeded_exywcf$_0 = true;
  };
  UiContainer.prototype.updateTheme_evfofk$ = function (ctx) {
    var tmp$;
    UiComponent.prototype.updateTheme_evfofk$.call(this, ctx);
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      var child = this.children.get_za3lpa$(i);
      if (Kotlin.isType(child, UiComponent)) {
        child.requestThemeUpdate();
      }
    }
  };
  UiContainer.prototype.updateComponentAlpha = function () {
    var tmp$;
    UiComponent.prototype.updateComponentAlpha.call(this);
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      var child = this.children.get_za3lpa$(i);
      if (Kotlin.isType(child, UiComponent)) {
        child.alpha = this.alpha;
      }
    }
  };
  UiContainer.prototype.render_evfofk$ = function (ctx) {
    if (this.isLayoutNeeded_exywcf$_0) {
      this.isLayoutNeeded_exywcf$_0 = false;
      this.doLayout_oytkew$(this.contentBounds, ctx);
    }
    UiComponent.prototype.render_evfofk$.call(this, ctx);
  };
  UiContainer.prototype.doLayout_oytkew$ = function (bounds, ctx) {
    var tmp$;
    this.applyBounds_oytkew$(bounds, ctx);
    tmp$ = this.children;
    for (var i = 0; i !== tmp$.size; ++i) {
      var child = this.children.get_za3lpa$(i);
      if (Kotlin.isType(child, UiComponent)) {
        this.computeChildLayoutBounds_hs15d6$(this.tmpChildBounds_j2uogg$_0, child, ctx);
        child.doLayout_oytkew$(this.tmpChildBounds_j2uogg$_0, ctx);
      }
    }
  };
  UiContainer.prototype.createThemeUi_evfofk$ = function (ctx) {
    return this.root.theme.containerUi(this);
  };
  UiContainer.prototype.applyBounds_oytkew$ = function (bounds, ctx) {
    if (!bounds.size.isEqual_czzhiu$(this.contentBounds.size) || !bounds.min.isEqual_czzhiu$(this.contentBounds.min)) {
      this.posInParent.set_czzhiu$(bounds.min);
      this.setIdentity().translate_czzhiu$(bounds.min);
      this.contentBounds.set_4lfkt4$(Vec3f$Companion_getInstance().ZERO, bounds.size);
      this.requestUiUpdate();
    }
  };
  UiContainer.prototype.computeChildLayoutBounds_hs15d6$ = function (result, child, ctx) {
    var x = child.layoutSpec.x.toUnits_dleff0$(this.contentBounds.size.x, this.dpi);
    var y = child.layoutSpec.y.toUnits_dleff0$(this.contentBounds.size.y, this.dpi);
    var z = child.layoutSpec.z.toUnits_dleff0$(this.contentBounds.size.z, this.dpi);
    var w = child.layoutSpec.width.toUnits_dleff0$(this.contentBounds.size.x, this.dpi);
    var h = child.layoutSpec.height.toUnits_dleff0$(this.contentBounds.size.y, this.dpi);
    var d = child.layoutSpec.depth.toUnits_dleff0$(this.contentBounds.size.z, this.dpi);
    if (x < 0) {
      x += this.width;
    }
    if (y < 0) {
      y += this.height;
    }
    if (z < 0) {
      z += this.depth;
    }
    result.set_w8lrqs$(x, y, z, x + w, y + h, z + d);
  };
  UiContainer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UiContainer',
    interfaces: [UiComponent]
  };
  function embeddedUi(contentHeight, dpi, block) {
    if (dpi === void 0)
      dpi = 300.0;
    var ui = new UiRoot(dpi);
    ui.contentHeight = contentHeight;
    block(ui);
    return ui;
  }
  function uiScene$lambda$lambda(closure$block) {
    return function ($receiver) {
      $receiver.isFillViewport = true;
      closure$block($receiver);
      return Unit;
    };
  }
  function uiScene(dpi, overlay, block) {
    if (dpi === void 0)
      dpi = 96.0;
    if (overlay === void 0)
      overlay = true;
    var $receiver = new Scene(null);
    var $receiver_0 = new OrthographicCamera();
    $receiver_0.clipToViewport = true;
    $receiver_0.near = -1000.0;
    $receiver_0.far = 1000.0;
    $receiver.camera = $receiver_0;
    if (overlay) {
      $receiver.clearMask = GL_DEPTH_BUFFER_BIT;
    }
    $receiver.unaryPlus_uv0sim$(embeddedUi(null, dpi, uiScene$lambda$lambda(block)));
    return $receiver;
  }
  function UiRoot(uiDpi, name) {
    if (name === void 0)
      name = 'UiRoot';
    Node.call(this, name);
    this.uiDpi = uiDpi;
    this.globalWidth_rlfw80$_0 = 1.0;
    this.globalHeight_5sxs8p$_0 = 1.0;
    this.globalDepth_ijlk77$_0 = 1.0;
    this.isFillViewport_fr53nm$_0 = false;
    this.theme_208sxy$_0 = UiTheme$Companion_getInstance().DARK;
    this.shaderLightModel = LightModel$NO_LIGHTING_getInstance();
    this.content = new UiContainer(name + '-content', this);
    this.contentHeight_17xsyl$_0 = null;
    this.blurHelper_0 = null;
    this.isLayoutNeeded_0 = true;
    this.content.parent = this;
    this.content.layoutSpec.setSize_4ujscr$(pcs(100.0), pcs(100.0), pcs(100.0));
  }
  Object.defineProperty(UiRoot.prototype, 'globalWidth', {
    get: function () {
      return this.globalWidth_rlfw80$_0;
    },
    set: function (value) {
      if (value !== this.globalWidth_rlfw80$_0) {
        this.globalWidth_rlfw80$_0 = value;
        this.isLayoutNeeded_0 = true;
      }
    }
  });
  Object.defineProperty(UiRoot.prototype, 'globalHeight', {
    get: function () {
      return this.globalHeight_5sxs8p$_0;
    },
    set: function (value) {
      if (value !== this.globalHeight_5sxs8p$_0) {
        this.globalHeight_5sxs8p$_0 = value;
        this.isLayoutNeeded_0 = true;
      }
    }
  });
  Object.defineProperty(UiRoot.prototype, 'globalDepth', {
    get: function () {
      return this.globalDepth_ijlk77$_0;
    },
    set: function (value) {
      if (value !== this.globalDepth_ijlk77$_0) {
        this.globalDepth_ijlk77$_0 = value;
        this.isLayoutNeeded_0 = true;
      }
    }
  });
  Object.defineProperty(UiRoot.prototype, 'isFillViewport', {
    get: function () {
      return this.isFillViewport_fr53nm$_0;
    },
    set: function (value) {
      if (value !== this.isFillViewport_fr53nm$_0) {
        this.isFillViewport_fr53nm$_0 = value;
        this.isLayoutNeeded_0 = true;
      }
    }
  });
  Object.defineProperty(UiRoot.prototype, 'theme', {
    get: function () {
      return this.theme_208sxy$_0;
    },
    set: function (value) {
      if (!equals(value, this.theme_208sxy$_0)) {
        this.theme_208sxy$_0 = value;
        this.content.requestThemeUpdate();
      }
    }
  });
  Object.defineProperty(UiRoot.prototype, 'contentHeight', {
    get: function () {
      return this.contentHeight_17xsyl$_0;
    },
    set: function (value) {
      this.contentHeight_17xsyl$_0 = value;
      this.isLayoutNeeded_0 = true;
    }
  });
  UiRoot.prototype.onSceneChanged_9srkog$ = function (oldScene, newScene) {
    Node.prototype.onSceneChanged_9srkog$.call(this, oldScene, newScene);
    this.content.scene = newScene;
  };
  UiRoot.prototype.createBlurHelper = function () {
    var tmp$;
    var helper = (tmp$ = this.blurHelper_0) != null ? tmp$ : new BlurredBackgroundHelper();
    if (this.blurHelper_0 == null) {
      this.blurHelper_0 = helper;
    }
    return helper;
  };
  UiRoot.prototype.setGlobalSize_y2kzbl$ = function (width, height, depth) {
    this.isFillViewport = false;
    this.globalWidth = width;
    this.globalHeight = height;
    this.globalDepth = depth;
  };
  UiRoot.prototype.render_evfofk$ = function (ctx) {
    var tmp$;
    if (this.isFillViewport && (this.globalWidth !== ctx.viewport.width || this.globalHeight !== ctx.viewport.height)) {
      this.globalWidth = ctx.viewport.width;
      this.globalHeight = ctx.viewport.height;
    }
    if (this.isLayoutNeeded_0) {
      this.isLayoutNeeded_0 = false;
      var contentScale = 1.0;
      var ch = this.contentHeight;
      if (ch != null) {
        contentScale = 1.0 / (ch.toUnits_dleff0$(this.globalHeight, this.uiDpi) / this.globalHeight);
        this.content.scale_y2kzbl$(contentScale, contentScale, contentScale);
      }
      this.content.contentBounds.set_w8lrqs$(0.0, 0.0, 0.0, this.globalWidth / contentScale, this.globalHeight / contentScale, this.globalDepth / contentScale);
      this.content.requestLayout();
    }
    (tmp$ = this.blurHelper_0) != null ? (tmp$.updateDistortionTexture_vcyfmv$(this, ctx, this.content.bounds), Unit) : null;
    ctx.pushAttributes();
    ctx.isCullFace = false;
    ctx.applyAttributes();
    Node.prototype.render_evfofk$.call(this, ctx);
    this.content.render_evfofk$(ctx);
    this.bounds.set_ea4od8$(this.content.bounds);
    ctx.popAttributes();
  };
  UiRoot.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$;
    Node.prototype.dispose_evfofk$.call(this, ctx);
    this.content.dispose_evfofk$(ctx);
    (tmp$ = this.blurHelper_0) != null ? (tmp$.dispose_evfofk$(ctx), Unit) : null;
  };
  UiRoot.prototype.rayTest_jljx4v$ = function (test) {
    Node.prototype.rayTest_jljx4v$.call(this, test);
    this.content.rayTest_jljx4v$(test);
  };
  UiRoot.prototype.unaryPlus_uv0sim$ = function ($receiver) {
    this.content.addNode_xtids1$($receiver);
  };
  UiRoot.prototype.component_qphi6d$ = function (name, block) {
    var $receiver = new UiComponent(name, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.prototype.container_t34sov$ = function (name, block) {
    var $receiver = new UiContainer(name, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.prototype.button_9zrh0o$ = function (name, block) {
    var $receiver = new Button(name, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.prototype.label_tokfmu$ = function (name, block) {
    var $receiver = new Label(name, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.prototype.slider_87iqh3$ = function (name, block) {
    return this.slider_91a1dk$(name, 0.0, 100.0, 50.0, block);
  };
  UiRoot.prototype.slider_91a1dk$ = function (name, min, max, value, block) {
    var $receiver = new Slider(name, min, max, value, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.prototype.textField_peizi7$ = function (name, block) {
    var $receiver = new TextField(name, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.prototype.toggleButton_6j87po$ = function (name, block) {
    var $receiver = new ToggleButton(name, this);
    block($receiver);
    return $receiver;
  };
  UiRoot.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UiRoot',
    interfaces: [Node]
  };
  function UiTheme() {
    UiTheme$Companion_getInstance();
    this.backgroundColor_yt8dmb$_0 = Color$Companion_getInstance().BLACK;
    this.foregroundColor_sxfy5a$_0 = Color$Companion_getInstance().WHITE;
    this.accentColor_2rzgrt$_0 = Color$Companion_getInstance().LIME;
    this.standardFontProps_v4eg6a$_0 = new FontProps(Font$Companion_getInstance().SYSTEM_FONT, 20.0);
    this.titleFontProps_idgbxl$_0 = new FontProps(Font$Companion_getInstance().SYSTEM_FONT, 28.0);
    this.componentUi_rare29$_0 = getCallableRef('BlurredComponentUi', function (component) {
      return new BlurredComponentUi(component);
    });
    this.containerUi_233tf$_0 = UiTheme$containerUi$lambda;
    this.buttonUi_667cek$_0 = getCallableRef('ButtonUi', function (button, baseUi) {
      return new ButtonUi(button, baseUi);
    });
    this.labelUi_9aobqe$_0 = getCallableRef('LabelUi', function (label, baseUi) {
      return new LabelUi(label, baseUi);
    });
    this.sliderUi_76d1mr$_0 = getCallableRef('SliderUi', function (slider, baseUi) {
      return new SliderUi(slider, baseUi);
    });
    this.textFieldUi_fr4mch$_0 = getCallableRef('TextFieldUi', function (textField, baseUi) {
      return new TextFieldUi(textField, baseUi);
    });
    this.toggleButtonUi_2tk5m0$_0 = getCallableRef('ToggleButtonUi', function (tb, baseUi) {
      return new ToggleButtonUi(tb, baseUi);
    });
  }
  Object.defineProperty(UiTheme.prototype, 'backgroundColor', {
    get: function () {
      return this.backgroundColor_yt8dmb$_0;
    },
    set: function (backgroundColor) {
      this.backgroundColor_yt8dmb$_0 = backgroundColor;
    }
  });
  Object.defineProperty(UiTheme.prototype, 'foregroundColor', {
    get: function () {
      return this.foregroundColor_sxfy5a$_0;
    },
    set: function (foregroundColor) {
      this.foregroundColor_sxfy5a$_0 = foregroundColor;
    }
  });
  Object.defineProperty(UiTheme.prototype, 'accentColor', {
    get: function () {
      return this.accentColor_2rzgrt$_0;
    },
    set: function (accentColor) {
      this.accentColor_2rzgrt$_0 = accentColor;
    }
  });
  UiTheme.prototype.standardFont_mx4ult$ = function (dpi) {
    return uiFont(this.standardFontProps.family, this.standardFontProps.sizePts, dpi, this.standardFontProps.style, this.standardFontProps.chars);
  };
  Object.defineProperty(UiTheme.prototype, 'standardFontProps', {
    get: function () {
      return this.standardFontProps_v4eg6a$_0;
    },
    set: function (standardFontProps) {
      this.standardFontProps_v4eg6a$_0 = standardFontProps;
    }
  });
  UiTheme.prototype.titleFont_mx4ult$ = function (dpi) {
    return uiFont(this.titleFontProps.family, this.titleFontProps.sizePts, dpi, this.titleFontProps.style, this.titleFontProps.chars);
  };
  Object.defineProperty(UiTheme.prototype, 'titleFontProps', {
    get: function () {
      return this.titleFontProps_idgbxl$_0;
    },
    set: function (titleFontProps) {
      this.titleFontProps_idgbxl$_0 = titleFontProps;
    }
  });
  UiTheme.prototype.newComponentUi_gzmg0q$ = function (c) {
    return this.componentUi(c);
  };
  Object.defineProperty(UiTheme.prototype, 'componentUi', {
    get: function () {
      return this.componentUi_rare29$_0;
    },
    set: function (componentUi) {
      this.componentUi_rare29$_0 = componentUi;
    }
  });
  UiTheme.prototype.newContainerUi_xcf31a$ = function (c) {
    return this.containerUi(c);
  };
  Object.defineProperty(UiTheme.prototype, 'containerUi', {
    get: function () {
      return this.containerUi_233tf$_0;
    },
    set: function (containerUi) {
      this.containerUi_233tf$_0 = containerUi;
    }
  });
  UiTheme.prototype.newButtonUi_t4rm0v$ = function (c) {
    return this.buttonUi(c, this.newComponentUi_gzmg0q$(c));
  };
  Object.defineProperty(UiTheme.prototype, 'buttonUi', {
    get: function () {
      return this.buttonUi_667cek$_0;
    },
    set: function (buttonUi) {
      this.buttonUi_667cek$_0 = buttonUi;
    }
  });
  UiTheme.prototype.newLabelUi_wviu0r$ = function (c) {
    return this.labelUi(c, this.newComponentUi_gzmg0q$(c));
  };
  Object.defineProperty(UiTheme.prototype, 'labelUi', {
    get: function () {
      return this.labelUi_9aobqe$_0;
    },
    set: function (labelUi) {
      this.labelUi_9aobqe$_0 = labelUi;
    }
  });
  UiTheme.prototype.newSliderUi_l85jm8$ = function (c) {
    return this.sliderUi(c, this.newComponentUi_gzmg0q$(c));
  };
  Object.defineProperty(UiTheme.prototype, 'sliderUi', {
    get: function () {
      return this.sliderUi_76d1mr$_0;
    },
    set: function (sliderUi) {
      this.sliderUi_76d1mr$_0 = sliderUi;
    }
  });
  UiTheme.prototype.newTextFieldUi_p39bdq$ = function (c) {
    return this.textFieldUi(c, this.newComponentUi_gzmg0q$(c));
  };
  Object.defineProperty(UiTheme.prototype, 'textFieldUi', {
    get: function () {
      return this.textFieldUi_fr4mch$_0;
    },
    set: function (textFieldUi) {
      this.textFieldUi_fr4mch$_0 = textFieldUi;
    }
  });
  UiTheme.prototype.newToggleButtonUi_r83hfv$ = function (c) {
    return this.toggleButtonUi(c, this.newComponentUi_gzmg0q$(c));
  };
  Object.defineProperty(UiTheme.prototype, 'toggleButtonUi', {
    get: function () {
      return this.toggleButtonUi_2tk5m0$_0;
    },
    set: function (toggleButtonUi) {
      this.toggleButtonUi_2tk5m0$_0 = toggleButtonUi;
    }
  });
  function UiTheme$Companion() {
    UiTheme$Companion_instance = this;
    this.DARK = theme(void 0, UiTheme$Companion$DARK$lambda);
    this.DARK_SIMPLE = theme(this.DARK, UiTheme$Companion$DARK_SIMPLE$lambda);
    this.LIGHT = theme(void 0, UiTheme$Companion$LIGHT$lambda);
    this.LIGHT_SIMPLE = theme(this.LIGHT, UiTheme$Companion$LIGHT_SIMPLE$lambda);
  }
  function UiTheme$Companion$DARK$lambda($receiver) {
    $receiver.backgroundColor_d7aj7k$(color('00141980'));
    $receiver.foregroundColor_d7aj7k$(Color$Companion_getInstance().WHITE);
    $receiver.accentColor_d7aj7k$(Color$Companion_getInstance().LIME);
    return Unit;
  }
  function UiTheme$Companion$DARK_SIMPLE$lambda($receiver) {
    $receiver.componentUi_mloaa0$(getCallableRef('SimpleComponentUi', function (component) {
      return new SimpleComponentUi(component);
    }));
    return Unit;
  }
  function UiTheme$Companion$LIGHT$lambda($receiver) {
    $receiver.backgroundColor_d7aj7k$(Color$Companion_getInstance().WHITE.withAlpha_mx4ult$(0.6));
    $receiver.foregroundColor_d7aj7k$(color('3E2723'));
    $receiver.accentColor_d7aj7k$(color('BF360C'));
    return Unit;
  }
  function UiTheme$Companion$LIGHT_SIMPLE$lambda($receiver) {
    $receiver.componentUi_mloaa0$(getCallableRef('SimpleComponentUi', function (component) {
      return new SimpleComponentUi(component);
    }));
    return Unit;
  }
  UiTheme$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var UiTheme$Companion_instance = null;
  function UiTheme$Companion_getInstance() {
    if (UiTheme$Companion_instance === null) {
      new UiTheme$Companion();
    }
    return UiTheme$Companion_instance;
  }
  function UiTheme$containerUi$lambda(it) {
    return new BlankComponentUi();
  }
  UiTheme.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UiTheme',
    interfaces: []
  };
  function theme(base, block) {
    if (base === void 0)
      base = null;
    var builder = new ThemeBuilder(base);
    block(builder);
    return builder;
  }
  function ThemeBuilder(base) {
    UiTheme.call(this);
    if (base != null) {
      this.backgroundColor = base.backgroundColor;
      this.foregroundColor = base.foregroundColor;
      this.accentColor = base.accentColor;
      this.standardFontProps = base.standardFontProps;
      this.titleFontProps = base.titleFontProps;
      this.componentUi = base.componentUi;
      this.containerUi = base.containerUi;
      this.buttonUi = base.buttonUi;
      this.labelUi = base.labelUi;
      this.sliderUi = base.sliderUi;
      this.textFieldUi = base.textFieldUi;
      this.toggleButtonUi = base.toggleButtonUi;
    }
  }
  ThemeBuilder.prototype.backgroundColor_d7aj7k$ = function (bgColor) {
    this.backgroundColor = bgColor;
  };
  ThemeBuilder.prototype.foregroundColor_d7aj7k$ = function (fgColor) {
    this.foregroundColor = fgColor;
  };
  ThemeBuilder.prototype.accentColor_d7aj7k$ = function (fgColor) {
    this.accentColor = fgColor;
  };
  ThemeBuilder.prototype.standardFont_ttufcy$ = function (props) {
    this.standardFontProps = props;
  };
  ThemeBuilder.prototype.titleFont_ttufcy$ = function (props) {
    this.titleFontProps = props;
  };
  ThemeBuilder.prototype.componentUi_mloaa0$ = function (fab) {
    this.componentUi = fab;
  };
  ThemeBuilder.prototype.containerUi_2t3ptw$ = function (fab) {
    this.containerUi = fab;
  };
  ThemeBuilder.prototype.buttonUi_layl6g$ = function (fab) {
    this.buttonUi = fab;
  };
  ThemeBuilder.prototype.labelUi_zicoma$ = function (fab) {
    this.labelUi = fab;
  };
  ThemeBuilder.prototype.sliderUi_artm94$ = function (fab) {
    this.sliderUi = fab;
  };
  ThemeBuilder.prototype.textFieldUi_5ax6ok$ = function (fab) {
    this.textFieldUi = fab;
  };
  ThemeBuilder.prototype.toggleButtonUi_50ge0o$ = function (fab) {
    this.toggleButtonUi = fab;
  };
  ThemeBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ThemeBuilder',
    interfaces: [UiTheme]
  };
  function ThemeOrCustomProp(initVal) {
    this.prop_5ushja$_0 = initVal;
    this.themeVal_jacv1h$_0 = initVal;
    this.isThemeSet_n9zltm$_0 = false;
    this.customVal_5f7u1l$_0 = initVal;
    this.isCustom_s2c3c2$_0 = false;
  }
  Object.defineProperty(ThemeOrCustomProp.prototype, 'prop', {
    get: function () {
      return this.prop_5ushja$_0;
    },
    set: function (prop) {
      this.prop_5ushja$_0 = prop;
    }
  });
  Object.defineProperty(ThemeOrCustomProp.prototype, 'themeVal', {
    get: function () {
      return this.themeVal_jacv1h$_0;
    },
    set: function (themeVal) {
      this.themeVal_jacv1h$_0 = themeVal;
    }
  });
  Object.defineProperty(ThemeOrCustomProp.prototype, 'isThemeSet', {
    get: function () {
      return this.isThemeSet_n9zltm$_0;
    },
    set: function (isThemeSet) {
      this.isThemeSet_n9zltm$_0 = isThemeSet;
    }
  });
  Object.defineProperty(ThemeOrCustomProp.prototype, 'customVal', {
    get: function () {
      return this.customVal_5f7u1l$_0;
    },
    set: function (customVal) {
      this.customVal_5f7u1l$_0 = customVal;
    }
  });
  Object.defineProperty(ThemeOrCustomProp.prototype, 'isCustom', {
    get: function () {
      return this.isCustom_s2c3c2$_0;
    },
    set: function (isCustom) {
      this.isCustom_s2c3c2$_0 = isCustom;
    }
  });
  Object.defineProperty(ThemeOrCustomProp.prototype, 'isUpdate', {
    get: function () {
      return this.isCustom && !equals(this.prop, this.customVal) || (this.isThemeSet && !equals(this.prop, this.themeVal));
    }
  });
  ThemeOrCustomProp.prototype.setTheme_11rb$ = function (themeVal) {
    this.themeVal = themeVal;
    this.isThemeSet = true;
    return this;
  };
  ThemeOrCustomProp.prototype.setCustom_11rb$ = function (customVal) {
    this.customVal = customVal;
    this.isCustom = true;
    return this;
  };
  ThemeOrCustomProp.prototype.clearCustom = function () {
    this.isCustom = false;
  };
  ThemeOrCustomProp.prototype.apply = function () {
    if (this.isCustom) {
      this.prop = this.customVal;
    }
     else if (this.isThemeSet) {
      this.prop = this.themeVal;
    }
    return this.prop;
  };
  ThemeOrCustomProp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ThemeOrCustomProp',
    interfaces: []
  };
  function AttributeType(name, ordinal, size, isInt, glType) {
    Enum.call(this);
    this.size = size;
    this.isInt = isInt;
    this.glType = glType;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function AttributeType_initFields() {
    AttributeType_initFields = function () {
    };
    AttributeType$FLOAT_instance = new AttributeType('FLOAT', 0, 1, false, GL_FLOAT);
    AttributeType$VEC_2F_instance = new AttributeType('VEC_2F', 1, 2, false, GL_FLOAT);
    AttributeType$VEC_3F_instance = new AttributeType('VEC_3F', 2, 3, false, GL_FLOAT);
    AttributeType$VEC_4F_instance = new AttributeType('VEC_4F', 3, 4, false, GL_FLOAT);
    AttributeType$COLOR_4F_instance = new AttributeType('COLOR_4F', 4, 4, false, GL_FLOAT);
    AttributeType$INT_instance = new AttributeType('INT', 5, 1, true, GL_INT);
    AttributeType$VEC_2I_instance = new AttributeType('VEC_2I', 6, 2, true, GL_INT);
    AttributeType$VEC_3I_instance = new AttributeType('VEC_3I', 7, 3, true, GL_INT);
    AttributeType$VEC_4I_instance = new AttributeType('VEC_4I', 8, 4, true, GL_INT);
  }
  var AttributeType$FLOAT_instance;
  function AttributeType$FLOAT_getInstance() {
    AttributeType_initFields();
    return AttributeType$FLOAT_instance;
  }
  var AttributeType$VEC_2F_instance;
  function AttributeType$VEC_2F_getInstance() {
    AttributeType_initFields();
    return AttributeType$VEC_2F_instance;
  }
  var AttributeType$VEC_3F_instance;
  function AttributeType$VEC_3F_getInstance() {
    AttributeType_initFields();
    return AttributeType$VEC_3F_instance;
  }
  var AttributeType$VEC_4F_instance;
  function AttributeType$VEC_4F_getInstance() {
    AttributeType_initFields();
    return AttributeType$VEC_4F_instance;
  }
  var AttributeType$COLOR_4F_instance;
  function AttributeType$COLOR_4F_getInstance() {
    AttributeType_initFields();
    return AttributeType$COLOR_4F_instance;
  }
  var AttributeType$INT_instance;
  function AttributeType$INT_getInstance() {
    AttributeType_initFields();
    return AttributeType$INT_instance;
  }
  var AttributeType$VEC_2I_instance;
  function AttributeType$VEC_2I_getInstance() {
    AttributeType_initFields();
    return AttributeType$VEC_2I_instance;
  }
  var AttributeType$VEC_3I_instance;
  function AttributeType$VEC_3I_getInstance() {
    AttributeType_initFields();
    return AttributeType$VEC_3I_instance;
  }
  var AttributeType$VEC_4I_instance;
  function AttributeType$VEC_4I_getInstance() {
    AttributeType_initFields();
    return AttributeType$VEC_4I_instance;
  }
  AttributeType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AttributeType',
    interfaces: [Enum]
  };
  function AttributeType$values() {
    return [AttributeType$FLOAT_getInstance(), AttributeType$VEC_2F_getInstance(), AttributeType$VEC_3F_getInstance(), AttributeType$VEC_4F_getInstance(), AttributeType$COLOR_4F_getInstance(), AttributeType$INT_getInstance(), AttributeType$VEC_2I_getInstance(), AttributeType$VEC_3I_getInstance(), AttributeType$VEC_4I_getInstance()];
  }
  AttributeType.values = AttributeType$values;
  function AttributeType$valueOf(name) {
    switch (name) {
      case 'FLOAT':
        return AttributeType$FLOAT_getInstance();
      case 'VEC_2F':
        return AttributeType$VEC_2F_getInstance();
      case 'VEC_3F':
        return AttributeType$VEC_3F_getInstance();
      case 'VEC_4F':
        return AttributeType$VEC_4F_getInstance();
      case 'COLOR_4F':
        return AttributeType$COLOR_4F_getInstance();
      case 'INT':
        return AttributeType$INT_getInstance();
      case 'VEC_2I':
        return AttributeType$VEC_2I_getInstance();
      case 'VEC_3I':
        return AttributeType$VEC_3I_getInstance();
      case 'VEC_4I':
        return AttributeType$VEC_4I_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.AttributeType.' + name);
    }
  }
  AttributeType.valueOf_61zpoe$ = AttributeType$valueOf;
  function Attribute(name, type) {
    Attribute$Companion_getInstance();
    this.name = name;
    this.type = type;
  }
  function Attribute$Companion() {
    Attribute$Companion_instance = this;
    this.POSITIONS = new Attribute('attrib_positions', AttributeType$VEC_3F_getInstance());
    this.NORMALS = new Attribute('attrib_normals', AttributeType$VEC_3F_getInstance());
    this.TANGENTS = new Attribute('attrib_tangents', AttributeType$VEC_3F_getInstance());
    this.TEXTURE_COORDS = new Attribute('attrib_texture_coords', AttributeType$VEC_2F_getInstance());
    this.COLORS = new Attribute('attrib_colors', AttributeType$COLOR_4F_getInstance());
  }
  Attribute$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Attribute$Companion_instance = null;
  function Attribute$Companion_getInstance() {
    if (Attribute$Companion_instance === null) {
      new Attribute$Companion();
    }
    return Attribute$Companion_instance;
  }
  Attribute.prototype.toString = function () {
    return this.name;
  };
  Attribute.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Attribute',
    interfaces: []
  };
  Attribute.prototype.component1 = function () {
    return this.name;
  };
  Attribute.prototype.component2 = function () {
    return this.type;
  };
  Attribute.prototype.copy_giwn2h$ = function (name, type) {
    return new Attribute(name === void 0 ? this.name : name, type === void 0 ? this.type : type);
  };
  Attribute.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.type) | 0;
    return result;
  };
  Attribute.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.type, other.type)))));
  };
  function basicShader(propsInit) {
    var $receiver = new ShaderProps();
    propsInit($receiver);
    return new BasicShader($receiver);
  }
  function BasicShader(props, generator) {
    if (generator === void 0)
      generator = new GlslGenerator();
    Shader.call(this);
    this.props = props;
    this.generator = generator;
    this.uMvpMatrix = this.addUniform_1ybs2r$(new UniformMatrix4(GlslGenerator$Companion_getInstance().U_MVP_MATRIX));
    this.uModelMatrix = this.addUniform_1ybs2r$(new UniformMatrix4(GlslGenerator$Companion_getInstance().U_MODEL_MATRIX));
    this.uViewMatrix = this.addUniform_1ybs2r$(new UniformMatrix4(GlslGenerator$Companion_getInstance().U_VIEW_MATRIX));
    this.uLightColor = this.addUniform_1ybs2r$(new Uniform3f(GlslGenerator$Companion_getInstance().U_LIGHT_COLOR));
    this.uLightDirection = this.addUniform_1ybs2r$(new Uniform3f(GlslGenerator$Companion_getInstance().U_LIGHT_DIRECTION));
    this.uCamPosition = this.addUniform_1ybs2r$(new Uniform3f(GlslGenerator$Companion_getInstance().U_CAMERA_POSITION));
    this.uShininess = this.addUniform_1ybs2r$(new Uniform1f(GlslGenerator$Companion_getInstance().U_SHININESS));
    this.uSpecularIntensity = this.addUniform_1ybs2r$(new Uniform1f(GlslGenerator$Companion_getInstance().U_SPECULAR_INTENSITY));
    this.uStaticColor = this.addUniform_1ybs2r$(new Uniform4f(GlslGenerator$Companion_getInstance().U_STATIC_COLOR));
    this.uTexture = this.addUniform_1ybs2r$(new UniformTexture2D(GlslGenerator$Companion_getInstance().U_TEXTURE_0));
    this.uNormalMap = this.addUniform_1ybs2r$(new UniformTexture2D(GlslGenerator$Companion_getInstance().U_NORMAL_MAP_0));
    this.uAlpha = this.addUniform_1ybs2r$(new Uniform1f(GlslGenerator$Companion_getInstance().U_ALPHA));
    this.uSaturation = this.addUniform_1ybs2r$(new Uniform1f(GlslGenerator$Companion_getInstance().U_SATURATION));
    this.uFogColor = this.addUniform_1ybs2r$(new Uniform4f(GlslGenerator$Companion_getInstance().U_FOG_COLOR));
    this.uFogRange = this.addUniform_1ybs2r$(new Uniform1f(GlslGenerator$Companion_getInstance().U_FOG_RANGE));
    this.uBones = this.addUniform_1ybs2r$(new UniformMatrix4(GlslGenerator$Companion_getInstance().U_BONES));
    this.uShadowMvp = this.addUniform_1ybs2r$(new UniformMatrix4(GlslGenerator$Companion_getInstance().U_SHADOW_MVP));
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
    this.uShadowTexSz = this.addUniform_1ybs2r$(new Uniform1iv(GlslGenerator$Companion_getInstance().U_SHADOW_TEX_SZ, (tmp$_1 = (tmp$_0 = (tmp$ = this.props.shadowMap) != null ? tmp$.subMaps : null) != null ? tmp$_0.length : null) != null ? tmp$_1 : 0));
    this.uClipSpaceFarZ = this.addUniform_1ybs2r$(new Uniform1fv(GlslGenerator$Companion_getInstance().U_CLIP_SPACE_FAR_Z, (tmp$_4 = (tmp$_3 = (tmp$_2 = this.props.shadowMap) != null ? tmp$_2.subMaps : null) != null ? tmp$_3.length : null) != null ? tmp$_4 : 0));
    this.uShadowTex = ArrayList_init();
    this.shadowMap_6x93ay$_0 = null;
    this.scene_i3j15i$_0 = null;
    this.shininess = this.props.shininess;
    this.specularIntensity = this.props.specularIntensity;
    this.staticColor.set_czzhhz$(this.props.staticColor);
    this.texture = this.props.texture;
    this.normalMap = this.props.normalMap;
    this.alpha = this.props.alpha;
    this.saturation = this.props.saturation;
    this.shadowMap_6x93ay$_0 = this.props.shadowMap;
    if (this.shadowMap_6x93ay$_0 != null) {
      this.uShadowMvp.value = this.shadowMap_6x93ay$_0.shadowMvp;
      tmp$_5 = this.shadowMap_6x93ay$_0.subMaps.length;
      for (var i = 0; i < tmp$_5; i++) {
        var shadowTex = this.addUniform_1ybs2r$(new UniformTexture2D(GlslGenerator$Companion_getInstance().U_SHADOW_TEX + '_' + i));
        this.uShadowTex.add_11rb$(shadowTex);
        shadowTex.value = this.shadowMap_6x93ay$_0.subMaps[i].depthTexture;
        this.uShadowTexSz.value[i] = this.shadowMap_6x93ay$_0.subMaps[i].texSize;
      }
    }
  }
  Object.defineProperty(BasicShader.prototype, 'shininess', {
    get: function () {
      return this.uShininess.value;
    },
    set: function (value) {
      this.uShininess.value = value;
    }
  });
  Object.defineProperty(BasicShader.prototype, 'specularIntensity', {
    get: function () {
      return this.uSpecularIntensity.value;
    },
    set: function (value) {
      this.uSpecularIntensity.value = value;
    }
  });
  Object.defineProperty(BasicShader.prototype, 'staticColor', {
    get: function () {
      return this.uStaticColor.value;
    },
    set: function (value) {
      this.uStaticColor.value.set_czzhhz$(value);
    }
  });
  Object.defineProperty(BasicShader.prototype, 'texture', {
    get: function () {
      return this.uTexture.value;
    },
    set: function (value) {
      this.uTexture.value = value;
    }
  });
  Object.defineProperty(BasicShader.prototype, 'normalMap', {
    get: function () {
      return this.uNormalMap.value;
    },
    set: function (value) {
      this.uNormalMap.value = value;
    }
  });
  Object.defineProperty(BasicShader.prototype, 'alpha', {
    get: function () {
      return this.uAlpha.value;
    },
    set: function (value) {
      this.uAlpha.value = value;
    }
  });
  Object.defineProperty(BasicShader.prototype, 'saturation', {
    get: function () {
      return this.uSaturation.value;
    },
    set: function (value) {
      this.uSaturation.value = value;
    }
  });
  Object.defineProperty(BasicShader.prototype, 'bones', {
    get: function () {
      return this.uBones.value;
    },
    set: function (value) {
      this.uBones.value = value;
    }
  });
  BasicShader.prototype.generate_evfofk$ = function (ctx) {
    this.source = this.generator.generate_md635r$(this.props, ctx.shaderMgr.shadingHints);
    this.attributes.clear();
    this.attributes.add_11rb$(Attribute$Companion_getInstance().POSITIONS);
    this.attributes.add_11rb$(Attribute$Companion_getInstance().NORMALS);
    this.attributes.add_11rb$(Attribute$Companion_getInstance().TEXTURE_COORDS);
    this.attributes.add_11rb$(Attribute$Companion_getInstance().COLORS);
    if (this.props.isNormalMapped) {
      this.attributes.add_11rb$(Attribute$Companion_getInstance().TANGENTS);
    }
    if (this.props.numBones > 0) {
      this.attributes.add_11rb$(Armature$Companion_getInstance().BONE_INDICES);
      this.attributes.add_11rb$(Armature$Companion_getInstance().BONE_WEIGHTS);
    }
  };
  BasicShader.prototype.onBind_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    this.onMatrixUpdate_evfofk$(ctx);
    this.scene_i3j15i$_0 = null;
    this.uFogColor.bind_evfofk$(ctx);
    this.uFogRange.bind_evfofk$(ctx);
    this.uSaturation.bind_evfofk$(ctx);
    this.uAlpha.bind_evfofk$(ctx);
    this.uShininess.bind_evfofk$(ctx);
    this.uSpecularIntensity.bind_evfofk$(ctx);
    this.uStaticColor.bind_evfofk$(ctx);
    this.uTexture.bind_evfofk$(ctx);
    this.uNormalMap.bind_evfofk$(ctx);
    this.uBones.bind_evfofk$(ctx);
    if (this.shadowMap_6x93ay$_0 != null) {
      if (ctx.renderPass === RenderPass$SHADOW_getInstance()) {
        tmp$ = this.shadowMap_6x93ay$_0.subMaps.length;
        for (var i = 0; i < tmp$; i++) {
          this.uShadowTex.get_za3lpa$(i).value = null;
          this.uShadowTex.get_za3lpa$(i).bind_evfofk$(ctx);
        }
      }
       else {
        tmp$_0 = this.shadowMap_6x93ay$_0.subMaps.length;
        for (var i_0 = 0; i_0 < tmp$_0; i_0++) {
          this.uClipSpaceFarZ.value[i_0] = this.shadowMap_6x93ay$_0.subMaps[i_0].clipSpaceFarZ;
          this.uShadowTex.get_za3lpa$(i_0).value = this.shadowMap_6x93ay$_0.subMaps[i_0].depthTexture;
          this.uShadowTex.get_za3lpa$(i_0).bind_evfofk$(ctx);
        }
        this.uShadowMvp.bind_evfofk$(ctx);
        this.uShadowTexSz.bind_evfofk$(ctx);
        this.uClipSpaceFarZ.bind_evfofk$(ctx);
      }
    }
  };
  BasicShader.prototype.bindMesh_lij8m4$ = function (mesh, ctx) {
    if (!equals(this.scene_i3j15i$_0, mesh.scene)) {
      this.scene_i3j15i$_0 = mesh.scene;
      if (this.scene_i3j15i$_0 != null) {
        this.uCamPosition.value.set_czzhiu$(ensureNotNull(this.scene_i3j15i$_0).camera.globalPos);
        this.uCamPosition.bind_evfofk$(ctx);
        var light = ensureNotNull(this.scene_i3j15i$_0).light;
        this.uLightDirection.value.set_czzhiu$(light.direction);
        this.uLightDirection.bind_evfofk$(ctx);
        this.uLightColor.value.set_y2kzbl$(light.color.r, light.color.g, light.color.b);
        this.uLightColor.bind_evfofk$(ctx);
      }
    }
    Shader.prototype.bindMesh_lij8m4$.call(this, mesh, ctx);
  };
  BasicShader.prototype.onMatrixUpdate_evfofk$ = function (ctx) {
    this.uMvpMatrix.value = ctx.mvpState.mvpMatrixBuffer;
    this.uMvpMatrix.bind_evfofk$(ctx);
    this.uViewMatrix.value = ctx.mvpState.viewMatrixBuffer;
    this.uViewMatrix.bind_evfofk$(ctx);
    this.uModelMatrix.value = ctx.mvpState.modelMatrixBuffer;
    this.uModelMatrix.bind_evfofk$(ctx);
  };
  BasicShader.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    Shader.prototype.dispose_evfofk$.call(this, ctx);
    (tmp$ = this.texture) != null ? (tmp$.dispose_evfofk$(ctx), Unit) : null;
    (tmp$_0 = this.normalMap) != null ? (tmp$_0.dispose_evfofk$(ctx), Unit) : null;
  };
  BasicShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BasicShader',
    interfaces: [Shader]
  };
  function basicPointShader(propsInit) {
    var $receiver = new ShaderProps();
    propsInit($receiver);
    return new BasicPointShader($receiver, new GlslGenerator());
  }
  function BasicPointShader(props, generator) {
    BasicPointShader$Companion_getInstance();
    BasicShader.call(this, props, generator);
    this.uPointSz = this.addUniform_1ybs2r$(new Uniform1f(BasicPointShader$Companion_getInstance().U_POINT_SIZE));
    var $receiver = generator.customUniforms;
    var element = this.uPointSz;
    $receiver.add_11rb$(element);
    var $receiver_0 = generator.injectors;
    var element_0 = new BasicPointShader_init$ObjectLiteral();
    $receiver_0.add_11rb$(element_0);
    this.pointSize = 1.0;
  }
  function BasicPointShader$Companion() {
    BasicPointShader$Companion_instance = this;
    this.U_POINT_SIZE = 'uPointSz';
  }
  BasicPointShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BasicPointShader$Companion_instance = null;
  function BasicPointShader$Companion_getInstance() {
    if (BasicPointShader$Companion_instance === null) {
      new BasicPointShader$Companion();
    }
    return BasicPointShader$Companion_instance;
  }
  Object.defineProperty(BasicPointShader.prototype, 'pointSize', {
    get: function () {
      return this.uPointSz.value;
    },
    set: function (value) {
      this.uPointSz.value = value;
    }
  });
  BasicPointShader.prototype.onBind_evfofk$ = function (ctx) {
    BasicShader.prototype.onBind_evfofk$.call(this, ctx);
    this.uPointSz.bind_evfofk$(ctx);
  };
  function BasicPointShader_init$ObjectLiteral() {
  }
  BasicPointShader_init$ObjectLiteral.prototype.vsAfterProj_3c8d48$ = function (shaderProps, text) {
    text.append_gw00v9$('gl_PointSize = uPointSz;\n');
  };
  BasicPointShader_init$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [GlslGenerator$GlslInjector]
  };
  BasicPointShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BasicPointShader',
    interfaces: [BasicShader]
  };
  function blurShader$lambda($receiver) {
    return Unit;
  }
  function blurShader(propsInit) {
    if (propsInit === void 0)
      propsInit = blurShader$lambda;
    var $receiver = new ShaderProps();
    propsInit($receiver);
    return new BlurShader($receiver, new GlslGenerator());
  }
  function BlurShader(props, generator) {
    BasicShader.call(this, props, generator);
    this.uBlurTex_0 = this.addUniform_1ybs2r$(new UniformTexture2D('uBlurTexture'));
    this.uColorMix_0 = this.addUniform_1ybs2r$(new Uniform1f('uColorMix'));
    this.uTexPos_0 = this.addUniform_1ybs2r$(new Uniform2f('uTexPos'));
    this.uTexSz_0 = this.addUniform_1ybs2r$(new Uniform2f('uTexSz'));
    this.blurHelper = null;
    this.colorMix = 0.0;
    var $receiver = generator.customUniforms;
    var element = this.uBlurTex_0;
    $receiver.add_11rb$(element);
    var $receiver_0 = generator.customUniforms;
    var element_0 = this.uColorMix_0;
    $receiver_0.add_11rb$(element_0);
    var $receiver_1 = generator.customUniforms;
    var element_1 = this.uTexPos_0;
    $receiver_1.add_11rb$(element_1);
    var $receiver_2 = generator.customUniforms;
    var element_2 = this.uTexSz_0;
    $receiver_2.add_11rb$(element_2);
    var $receiver_3 = generator.injectors;
    var element_3 = new BlurShader_init$ObjectLiteral();
    $receiver_3.add_11rb$(element_3);
  }
  Object.defineProperty(BlurShader.prototype, 'colorMix', {
    get: function () {
      return this.uColorMix_0.value;
    },
    set: function (value) {
      this.uColorMix_0.value = value;
    }
  });
  BlurShader.prototype.onBind_evfofk$ = function (ctx) {
    BasicShader.prototype.onBind_evfofk$.call(this, ctx);
    var helper = this.blurHelper;
    if (helper != null) {
      helper.isInUse_8be2vx$ = true;
      this.uBlurTex_0.value = helper.getOutputTexture();
      this.uTexPos_0.value.set_dleff0$(helper.capturedScrX, helper.capturedScrY);
      this.uTexSz_0.value.set_dleff0$(helper.capturedScrW, helper.capturedScrH);
    }
    this.uBlurTex_0.bind_evfofk$(ctx);
    this.uColorMix_0.bind_evfofk$(ctx);
    this.uTexPos_0.bind_evfofk$(ctx);
    this.uTexSz_0.bind_evfofk$(ctx);
  };
  function BlurShader_init$ObjectLiteral() {
  }
  BlurShader_init$ObjectLiteral.prototype.fsAfterSampling_3c8d48$ = function (shaderProps, text) {
    text.append_gw00v9$('vec2 blurSamplePos = vec2((gl_FragCoord.x - uTexPos.x) / uTexSz.x, ').append_gw00v9$('1.0 - (gl_FragCoord.y - uTexPos.y) / uTexSz.y);\n').append_gw00v9$(glCapabilities.glslDialect.fragColorBody + ' = ' + glCapabilities.glslDialect.texSampler + '(').append_gw00v9$('uBlurTexture, blurSamplePos) * (1.0 - uColorMix) + ').append_gw00v9$(glCapabilities.glslDialect.fragColorBody + ' * uColorMix;' + '\n');
  };
  BlurShader_init$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [GlslGenerator$GlslInjector]
  };
  BlurShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlurShader',
    interfaces: [BasicShader]
  };
  function BlurredBackgroundHelper(texSize, blurMethod) {
    if (texSize === void 0)
      texSize = 256;
    if (blurMethod === void 0)
      blurMethod = BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_getInstance();
    this.texSize_0 = texSize;
    this.blurMethod_0 = blurMethod;
    this.tmpRes_0 = MutableVec3f_init();
    this.tmpVec_0 = MutableVec3f_init();
    this.texBounds_0 = new BoundingBox();
    this.copyTex_0 = null;
    this.copyTexData_0 = new BlurredBackgroundHelper$BlurredBgTextureData();
    this.texMesh_0 = null;
    this.texMeshFlipped_0 = null;
    this.blurFb1_0 = (new Framebuffer(this.texSize_0, this.texSize_0)).withColor();
    this.blurFb2_0 = (new Framebuffer(this.texSize_0, this.texSize_0)).withColor();
    this.blurX_0 = null;
    this.blurY_0 = null;
    this.isForceUpdateTex = false;
    this.isInUse_8be2vx$ = true;
    this.numPasses = 2;
    var id = UniqueId_getInstance().nextId();
    var texProps = new TextureProps('DistortedBackground-' + id, GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0);
    this.copyTex_0 = new Texture(texProps, BlurredBackgroundHelper_init$lambda(this));
    this.texMesh_0 = textureMesh(void 0, void 0, BlurredBackgroundHelper_init$lambda_0);
    this.texMeshFlipped_0 = textureMesh(void 0, void 0, BlurredBackgroundHelper_init$lambda_1);
  }
  function BlurredBackgroundHelper$BlurMethod(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function BlurredBackgroundHelper$BlurMethod_initFields() {
    BlurredBackgroundHelper$BlurMethod_initFields = function () {
    };
    BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_instance = new BlurredBackgroundHelper$BlurMethod('BLUR_9_TAP', 0);
    BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_instance = new BlurredBackgroundHelper$BlurMethod('BLUR_13_TAP', 1);
  }
  var BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_instance;
  function BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_getInstance() {
    BlurredBackgroundHelper$BlurMethod_initFields();
    return BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_instance;
  }
  var BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_instance;
  function BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_getInstance() {
    BlurredBackgroundHelper$BlurMethod_initFields();
    return BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_instance;
  }
  BlurredBackgroundHelper$BlurMethod.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlurMethod',
    interfaces: [Enum]
  };
  function BlurredBackgroundHelper$BlurMethod$values() {
    return [BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_getInstance(), BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_getInstance()];
  }
  BlurredBackgroundHelper$BlurMethod.values = BlurredBackgroundHelper$BlurMethod$values;
  function BlurredBackgroundHelper$BlurMethod$valueOf(name) {
    switch (name) {
      case 'BLUR_9_TAP':
        return BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_getInstance();
      case 'BLUR_13_TAP':
        return BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.BlurredBackgroundHelper.BlurMethod.' + name);
    }
  }
  BlurredBackgroundHelper$BlurMethod.valueOf_61zpoe$ = BlurredBackgroundHelper$BlurMethod$valueOf;
  Object.defineProperty(BlurredBackgroundHelper.prototype, 'capturedScrX', {
    get: function () {
      return this.copyTexData_0.x;
    }
  });
  Object.defineProperty(BlurredBackgroundHelper.prototype, 'capturedScrY', {
    get: function () {
      return this.copyTexData_0.y;
    }
  });
  Object.defineProperty(BlurredBackgroundHelper.prototype, 'capturedScrW', {
    get: function () {
      return this.copyTexData_0.width;
    }
  });
  Object.defineProperty(BlurredBackgroundHelper.prototype, 'capturedScrH', {
    get: function () {
      return this.copyTexData_0.height;
    }
  });
  BlurredBackgroundHelper.prototype.getOutputTexture = function () {
    var tmp$;
    return (tmp$ = this.blurFb2_0) != null ? tmp$.colorAttachment : null;
  };
  BlurredBackgroundHelper.prototype.updateDistortionTexture_vcyfmv$ = function (node, ctx, bounds) {
    if (bounds === void 0)
      bounds = node.bounds;
    var tmp$, tmp$_0, tmp$_1;
    if (!this.isInUse_8be2vx$ || this.isForceUpdateTex || ctx.renderPass !== RenderPass$SCREEN_getInstance()) {
      return;
    }
    this.isInUse_8be2vx$ = false;
    tmp$_0 = (tmp$ = node.scene) != null ? tmp$.camera : null;
    if (tmp$_0 == null) {
      return;
    }
    var cam = tmp$_0;
    this.texBounds_0.clear();
    this.addToTexBounds_0(cam, node, bounds.min.x, bounds.min.y, bounds.min.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.min.x, bounds.min.y, bounds.max.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.min.x, bounds.max.y, bounds.min.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.min.x, bounds.max.y, bounds.max.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.max.x, bounds.min.y, bounds.min.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.max.x, bounds.min.y, bounds.max.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.max.x, bounds.max.y, bounds.min.z, ctx);
    this.addToTexBounds_0(cam, node, bounds.max.x, bounds.max.y, bounds.max.z, ctx);
    var a = numberToInt(this.texBounds_0.min.x);
    var minScrX = Math_0.max(a, 0);
    var a_0 = numberToInt(this.texBounds_0.max.x);
    var b = ctx.windowWidth - 1 | 0;
    var maxScrX = Math_0.min(a_0, b);
    var a_1 = ctx.windowHeight - numberToInt(this.texBounds_0.max.y) | 0;
    var minScrY = Math_0.max(a_1, 0);
    var a_2 = ctx.windowHeight - numberToInt(this.texBounds_0.min.y) | 0;
    var b_0 = ctx.windowHeight - 1 | 0;
    var maxScrY = Math_0.min(a_2, b_0);
    var a_3 = maxScrX - minScrX | 0;
    var sizeX = Math_0.max(a_3, 16);
    var a_4 = maxScrY - minScrY | 0;
    var sizeY = Math_0.max(a_4, 16);
    if (maxScrX > 0 && minScrX < ctx.windowWidth && maxScrY > 0 && minScrY < ctx.windowHeight && this.texBounds_0.min.z < 1 && this.texBounds_0.max.z > 0) {
      if (sizeX > sizeY) {
        var a_5 = sizeX;
        var b_1 = ctx.windowHeight - 1 | 0;
        sizeY = Math_0.min(a_5, b_1);
        if ((minScrY + sizeY | 0) >= ctx.windowHeight) {
          minScrY = ctx.windowHeight - sizeY - 1 | 0;
        }
      }
       else if (sizeY > sizeX) {
        var a_6 = sizeY;
        var b_2 = ctx.windowWidth - 1 | 0;
        sizeX = Math_0.min(a_6, b_2);
        if ((minScrX + sizeX | 0) >= ctx.windowWidth) {
          minScrX = ctx.windowWidth - sizeX - 1 | 0;
        }
      }
      (tmp$_1 = this.copyTex_0.res) != null ? (tmp$_1.isLoaded = false) : null;
      this.copyTexData_0.x = minScrX;
      this.copyTexData_0.y = minScrY;
      this.copyTexData_0.setCopyWidth_za3lpa$(sizeX);
      this.copyTexData_0.setCopyHeight_za3lpa$(sizeY);
      this.doBlurring_0(ctx);
    }
  };
  BlurredBackgroundHelper.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    this.blurFb1_0.delete_evfofk$(ctx);
    this.blurFb2_0.delete_evfofk$(ctx);
    (tmp$ = this.blurX_0) != null ? (tmp$.dispose_evfofk$(ctx), Unit) : null;
    (tmp$_0 = this.blurY_0) != null ? (tmp$_0.dispose_evfofk$(ctx), Unit) : null;
    this.copyTex_0.dispose_evfofk$(ctx);
    this.texMesh_0.dispose_evfofk$(ctx);
    this.texMeshFlipped_0.dispose_evfofk$(ctx);
  };
  BlurredBackgroundHelper.prototype.doBlurring_0 = function (ctx) {
    var tmp$, tmp$_0, tmp$_1;
    ctx.textureMgr.bindTexture_4yp9vu$(this.copyTex_0, ctx);
    var tmp$_2;
    if ((tmp$ = this.blurX_0) != null)
      tmp$_2 = tmp$;
    else {
      var $receiver = new BlurredBackgroundHelper$BlurQuadShader(this.blurMethod_0);
      $receiver.uTexture.value = this.copyTex_0;
      $receiver.uDirection.value.set_dleff0$(1.0 / this.texSize_0, 0.0);
      this.blurX_0 = $receiver;
      tmp$_2 = $receiver;
    }
    var blrX = tmp$_2;
    var tmp$_3;
    if ((tmp$_0 = this.blurY_0) != null)
      tmp$_3 = tmp$_0;
    else {
      var $receiver_0 = new BlurredBackgroundHelper$BlurQuadShader(this.blurMethod_0);
      $receiver_0.uTexture.value = this.blurFb1_0.colorAttachment;
      $receiver_0.uDirection.value.set_dleff0$(0.0, 1.0 / this.texSize_0);
      this.blurY_0 = $receiver_0;
      tmp$_3 = $receiver_0;
    }
    var blrY = tmp$_3;
    ctx.pushAttributes();
    ctx.clearColor = Color$Companion_getInstance().BLACK;
    ctx.applyAttributes();
    ctx.shaderMgr.bindShader_wa3i41$(null, ctx);
    ctx.mvpState.pushMatrices();
    ctx.mvpState.projMatrix.setIdentity();
    ctx.mvpState.viewMatrix.setIdentity();
    ctx.mvpState.modelMatrix.setIdentity();
    ctx.mvpState.update_evfofk$(ctx);
    blrX.uTexture.value = this.copyTex_0;
    this.renderFb_0(this.blurFb1_0, this.texMeshFlipped_0, blrX, ctx);
    this.renderFb_0(this.blurFb2_0, this.texMesh_0, blrY, ctx);
    blrX.uTexture.value = this.blurFb2_0.colorAttachment;
    tmp$_1 = this.numPasses;
    for (var i = 1; i < tmp$_1; i++) {
      this.renderFb_0(this.blurFb1_0, this.texMesh_0, blrX, ctx);
      this.renderFb_0(this.blurFb2_0, this.texMesh_0, blrY, ctx);
    }
    ctx.mvpState.popMatrices();
    ctx.mvpState.update_evfofk$(ctx);
    ctx.popAttributes();
  };
  BlurredBackgroundHelper.prototype.renderFb_0 = function (fb, mesh, shader, ctx) {
    fb.bind_evfofk$(ctx);
    glClear(GL_COLOR_BUFFER_BIT);
    mesh.shader = shader;
    mesh.render_evfofk$(ctx);
    fb.unbind_evfofk$(ctx);
  };
  BlurredBackgroundHelper.prototype.addToTexBounds_0 = function (cam, node, x, y, z, ctx) {
    this.tmpVec_0.set_y2kzbl$(x, y, z);
    node.toGlobalCoords_w1lst9$(this.tmpVec_0);
    cam.projectScreen_54xt5k$(this.tmpVec_0, ctx, this.tmpRes_0);
    this.texBounds_0.add_czzhiu$(this.tmpRes_0);
  };
  function BlurredBackgroundHelper$BlurredBgTextureData() {
    TextureData.call(this);
    this.x = 0;
    this.y = 0;
    this.isAvailable = true;
  }
  BlurredBackgroundHelper$BlurredBgTextureData.prototype.setCopyWidth_za3lpa$ = function (value) {
    this.width = value;
  };
  BlurredBackgroundHelper$BlurredBgTextureData.prototype.setCopyHeight_za3lpa$ = function (value) {
    this.height = value;
  };
  BlurredBackgroundHelper$BlurredBgTextureData.prototype.onLoad_4yp9vu$ = function (texture, ctx) {
    var tmp$;
    tmp$ = texture.res;
    if (tmp$ == null) {
      throw new KoolException("Texture wasn't created");
    }
    var res = tmp$;
    glCopyTexImage2D(res.target, 0, GL_RGBA, this.x, this.y, this.width, this.height, 0);
  };
  BlurredBackgroundHelper$BlurredBgTextureData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlurredBgTextureData',
    interfaces: [TextureData]
  };
  function BlurredBackgroundHelper$BlurQuadShader(blurMethod) {
    Shader.call(this);
    this.blurMethod_0 = blurMethod;
    this.uTexture = this.addUniform_1ybs2r$(new UniformTexture2D('uTexture'));
    this.uDirection = this.addUniform_1ybs2r$(new Uniform2f('uDirection'));
  }
  BlurredBackgroundHelper$BlurQuadShader.prototype.generate_evfofk$ = function (ctx) {
    this.attributes.add_11rb$(Attribute$Companion_getInstance().POSITIONS);
    this.attributes.add_11rb$(Attribute$Companion_getInstance().TEXTURE_COORDS);
    var vs = glCapabilities.glslDialect.version + '\n' + (glCapabilities.glslDialect.vsIn + ' vec3 ' + Attribute$Companion_getInstance().POSITIONS.name + ';' + '\n') + (glCapabilities.glslDialect.vsIn + ' vec2 ' + Attribute$Companion_getInstance().TEXTURE_COORDS.name + ';' + '\n') + (glCapabilities.glslDialect.vsOut + ' vec2 vTexCoord;' + '\n') + 'void main() {\n' + ('  gl_Position = vec4(' + Attribute$Companion_getInstance().POSITIONS.name + ', 1.0);' + '\n') + ('  vTexCoord = ' + Attribute$Companion_getInstance().TEXTURE_COORDS.name + ';' + '\n') + '}';
    var fs;
    var fragColor = glCapabilities.glslDialect.fragColorBody;
    var sampler = glCapabilities.glslDialect.texSampler;
    if (this.blurMethod_0 === BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_getInstance()) {
      fs = glCapabilities.glslDialect.version + '\n' + 'precision mediump float;\n' + 'uniform sampler2D uTexture;\n' + 'uniform vec2 uDirection;\n' + 'uniform float uTexSize;\n' + (glCapabilities.glslDialect.fsIn + ' vec2 vTexCoord;') + (glCapabilities.glslDialect.fragColorHead + '\n') + 'void main() {\n' + '  vec2 off1 = vec2(1.3846153) * uDirection;\n' + '  vec2 off2 = vec2(3.2307692) * uDirection;\n' + ('  ' + fragColor + ' = ' + sampler + '(uTexture, vTexCoord) * 0.2270270;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord + off1) * 0.3162162;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord - off1) * 0.3162162;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord + off2) * 0.0702702;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord - off2) * 0.0702702;' + '\n') + ('  ' + fragColor + '.rgb *= ' + glCapabilities.glslDialect.fragColorBody + '.a;' + '\n') + '}';
    }
     else {
      fs = glCapabilities.glslDialect.version + '\n' + 'precision mediump float;\n' + 'uniform sampler2D uTexture;\n' + 'uniform vec2 uDirection;\n' + 'uniform float uTexSize;\n' + (glCapabilities.glslDialect.fsIn + ' vec2 vTexCoord;') + (glCapabilities.glslDialect.fragColorHead + '\n') + 'void main() {\n' + '  vec2 off1 = vec2(1.4117647) * uDirection;\n' + '  vec2 off2 = vec2(3.2941176) * uDirection;\n' + '  vec2 off3 = vec2(5.1764705) * uDirection;\n' + ('  ' + fragColor + ' = ' + sampler + '(uTexture, vTexCoord) * 0.1968255;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord + off1) * 0.2969069;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord - off1) * 0.2969069;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord + off2) * 0.0944703;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord - off2) * 0.0944703;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord - off3) * 0.0103813;' + '\n') + ('  ' + fragColor + ' += ' + sampler + '(uTexture, vTexCoord - off3) * 0.0103813;' + '\n') + ('  ' + fragColor + '.rgb *= ' + glCapabilities.glslDialect.fragColorBody + '.a;' + '\n') + '}';
    }
    this.source = Shader$Shader$Source_init(vs, fs);
  };
  BlurredBackgroundHelper$BlurQuadShader.prototype.onBind_evfofk$ = function (ctx) {
    this.uTexture.bind_evfofk$(ctx);
    this.uDirection.bind_evfofk$(ctx);
  };
  BlurredBackgroundHelper$BlurQuadShader.prototype.onMatrixUpdate_evfofk$ = function (ctx) {
  };
  BlurredBackgroundHelper$BlurQuadShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlurQuadShader',
    interfaces: [Shader]
  };
  function BlurredBackgroundHelper_init$lambda(this$BlurredBackgroundHelper) {
    return function ($receiver) {
      return this$BlurredBackgroundHelper.copyTexData_0;
    };
  }
  function BlurredBackgroundHelper_init$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.rectProps.defaults();
    $receiver_0.size.set_dleff0$(2.0, 2.0);
    $receiver_0.origin.set_y2kzbl$(-1.0, -1.0, 0.0);
    $receiver_0.fullTexCoords();
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function BlurredBackgroundHelper_init$lambda_0($receiver) {
    $receiver.generator = BlurredBackgroundHelper_init$lambda$lambda;
    return Unit;
  }
  function BlurredBackgroundHelper_init$lambda$lambda_0($receiver) {
    var $receiver_0 = $receiver.rectProps.defaults();
    $receiver_0.size.set_dleff0$(2.0, 2.0);
    $receiver_0.origin.set_y2kzbl$(-1.0, -1.0, 0.0);
    $receiver_0.texCoordUpperLeft.set_dleff0$(0.0, 1.0);
    $receiver_0.texCoordUpperRight.set_dleff0$(1.0, 1.0);
    $receiver_0.texCoordLowerLeft.set_dleff0$(0.0, 0.0);
    $receiver_0.texCoordLowerRight.set_dleff0$(1.0, 0.0);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function BlurredBackgroundHelper_init$lambda_1($receiver) {
    $receiver.generator = BlurredBackgroundHelper_init$lambda$lambda_0;
    return Unit;
  }
  BlurredBackgroundHelper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BlurredBackgroundHelper',
    interfaces: []
  };
  function GlslGenerator() {
    GlslGenerator$Companion_getInstance();
    this.injectors = ArrayList_init();
    this.customUniforms = ArrayList_init();
    this.vsIn_5467de$_0 = glCapabilities.glslDialect.vsIn;
    this.vsOut_gj894f$_0 = glCapabilities.glslDialect.vsOut;
    this.fsIn_4vdhvm$_0 = glCapabilities.glslDialect.fsIn;
    this.fsOut_8yiatb$_0 = glCapabilities.glslDialect.fragColorHead;
    this.fsOutBody_rbmxbx$_0 = glCapabilities.glslDialect.fragColorBody;
    this.texSampler_1a4zdf$_0 = glCapabilities.glslDialect.texSampler;
  }
  function GlslGenerator$Companion() {
    GlslGenerator$Companion_instance = this;
    this.U_MVP_MATRIX = 'uMvpMatrix';
    this.U_MODEL_MATRIX = 'uModelMatrix';
    this.U_VIEW_MATRIX = 'uViewMatrix';
    this.U_LIGHT_DIRECTION = 'uLightDirection';
    this.U_LIGHT_COLOR = 'uLightColor';
    this.U_SHININESS = 'uShininess';
    this.U_SPECULAR_INTENSITY = 'uSpecularIntensity';
    this.U_CAMERA_POSITION = 'uCameraPosition';
    this.U_FOG_COLOR = 'uFogColor';
    this.U_FOG_RANGE = 'uFogRange';
    this.U_TEXTURE_0 = 'uTexture0';
    this.U_STATIC_COLOR = 'uStaticColor';
    this.U_ALPHA = 'uAlpha';
    this.U_SATURATION = 'uSaturation';
    this.U_BONES = 'uBones';
    this.U_SHADOW_MVP = 'uShadowMvp';
    this.U_SHADOW_TEX = 'uShadowTex';
    this.U_SHADOW_TEX_SZ = 'uShadowTexSz';
    this.U_CLIP_SPACE_FAR_Z = 'uClipSpaceFarZ';
    this.U_NORMAL_MAP_0 = 'uNormalMap0';
    this.V_TEX_COORD = 'vTexCoord';
    this.V_EYE_DIRECTION = 'vEyeDirection_cameraspace';
    this.V_LIGHT_DIRECTION = 'vLightDirection_cameraspace';
    this.V_NORMAL = 'vNormal_cameraspace';
    this.V_COLOR = 'vFragmentColor';
    this.V_DIFFUSE_LIGHT_COLOR = 'vDiffuseLightColor';
    this.V_SPECULAR_LIGHT_COLOR = 'vSpecularLightColor';
    this.V_POSITION_WORLDSPACE = 'vPositionWorldspace';
    this.V_POSITION_LIGHTSPACE = 'vPositionLightspace';
    this.V_POSITION_CLIPSPACE_Z = 'vPositionClipspaceZ';
    this.V_TANGENT = 'vTangent';
    this.L_TEX_COLOR = 'texColor';
    this.L_VERTEX_COLOR = 'vertColor';
    this.L_STATIC_COLOR = 'staticColor';
  }
  GlslGenerator$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var GlslGenerator$Companion_instance = null;
  function GlslGenerator$Companion_getInstance() {
    if (GlslGenerator$Companion_instance === null) {
      new GlslGenerator$Companion();
    }
    return GlslGenerator$Companion_instance;
  }
  function GlslGenerator$GlslInjector() {
  }
  GlslGenerator$GlslInjector.prototype.vsHeader_irqrwq$ = function (text) {
  };
  GlslGenerator$GlslInjector.prototype.vsStart_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.vsAfterInput_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.vsBeforeProj_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.vsAfterProj_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.vsEnd_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.fsHeader_irqrwq$ = function (text) {
  };
  GlslGenerator$GlslInjector.prototype.fsStart_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.fsAfterInput_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.fsBeforeSampling_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.fsAfterSampling_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.prototype.fsEnd_3c8d48$ = function (shaderProps, text) {
  };
  GlslGenerator$GlslInjector.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'GlslInjector',
    interfaces: []
  };
  GlslGenerator.prototype.generate_md635r$ = function (shaderProps, hints) {
    return Shader$Shader$Source_init(this.generateVertShader_uwpm6f$_0(shaderProps), this.generateFragShader_4yhy3q$_0(shaderProps));
  };
  GlslGenerator.prototype.generateVertShader_uwpm6f$_0 = function (shaderProps) {
    var text = new StringBuilder(glCapabilities.glslDialect.version + '\n');
    var tmp$;
    tmp$ = this.injectors.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.vsHeader_irqrwq$(text);
    }
    var tmp$_0;
    tmp$_0 = this.injectors.iterator();
    while (tmp$_0.hasNext()) {
      var element_0 = tmp$_0.next();
      element_0.vsStart_3c8d48$(shaderProps, text);
    }
    this.generateVertInputCode_bk8pu1$_0(shaderProps, text);
    var tmp$_1;
    tmp$_1 = this.injectors.iterator();
    while (tmp$_1.hasNext()) {
      var element_1 = tmp$_1.next();
      element_1.vsAfterInput_3c8d48$(shaderProps, text);
    }
    this.generateVertBodyCode_fozol5$_0(shaderProps, text);
    return text.toString();
  };
  GlslGenerator.prototype.generateFragShader_4yhy3q$_0 = function (shaderProps) {
    var text = new StringBuilder(glCapabilities.glslDialect.version + '\n');
    var tmp$;
    tmp$ = this.injectors.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.fsHeader_irqrwq$(text);
    }
    var tmp$_0;
    tmp$_0 = this.injectors.iterator();
    while (tmp$_0.hasNext()) {
      var element_0 = tmp$_0.next();
      element_0.fsStart_3c8d48$(shaderProps, text);
    }
    this.generateFragInputCode_7emddk$_0(shaderProps, text);
    var tmp$_1;
    tmp$_1 = this.injectors.iterator();
    while (tmp$_1.hasNext()) {
      var element_1 = tmp$_1.next();
      element_1.fsAfterInput_3c8d48$(shaderProps, text);
    }
    this.generateFragBodyCode_1c1dne$_0(shaderProps, text);
    return text.toString();
  };
  GlslGenerator.prototype.generateVertInputCode_bk8pu1$_0 = function (shaderProps, text) {
    var tmp$;
    text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec3 ' + Attribute$Companion_getInstance().POSITIONS.name + ';' + '\n');
    text.append_gw00v9$('uniform mat4 uMvpMatrix;\n');
    text.append_gw00v9$('uniform mat4 uModelMatrix;\n');
    text.append_gw00v9$('uniform mat4 uViewMatrix;\n');
    if (shaderProps.lightModel !== LightModel$NO_LIGHTING_getInstance()) {
      text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec3 ' + Attribute$Companion_getInstance().NORMALS.name + ';' + '\n');
      text.append_gw00v9$('uniform vec3 uLightDirection;\n');
      if (shaderProps.lightModel === LightModel$PHONG_LIGHTING_getInstance()) {
        text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_EYE_DIRECTION + ';' + '\n');
        text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_LIGHT_DIRECTION + ';' + '\n');
        text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_NORMAL + ';' + '\n');
        if (shaderProps.isNormalMapped) {
          text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec3 ' + Attribute$Companion_getInstance().TANGENTS.name + ';' + '\n');
          text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_TANGENT + ';' + '\n');
        }
      }
       else {
        text.append_gw00v9$('uniform vec3 uLightColor;\n');
        text.append_gw00v9$('uniform float uShininess;\n');
        text.append_gw00v9$('uniform float uSpecularIntensity;\n');
        text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_DIFFUSE_LIGHT_COLOR + ';' + '\n');
        text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_SPECULAR_LIGHT_COLOR + ';' + '\n');
      }
    }
    if (shaderProps.isTextureColor) {
      text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec2 ' + Attribute$Companion_getInstance().TEXTURE_COORDS.name + ';' + '\n');
      text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec2 ' + GlslGenerator$Companion_getInstance().V_TEX_COORD + ';' + '\n');
    }
    if (shaderProps.isVertexColor) {
      text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec4 ' + Attribute$Companion_getInstance().COLORS.name + ';' + '\n');
      text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec4 ' + GlslGenerator$Companion_getInstance().V_COLOR + ';' + '\n');
    }
    if (shaderProps.numBones > 0) {
      text.append_gw00v9$(this.vsIn_5467de$_0 + ' ivec4 ' + Armature$Companion_getInstance().BONE_INDICES.name + ';' + '\n');
      text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec4 ' + Armature$Companion_getInstance().BONE_WEIGHTS.name + ';' + '\n');
      text.append_gw00v9$('uniform mat4 ' + GlslGenerator$Companion_getInstance().U_BONES + '[' + shaderProps.numBones + '];' + '\n');
    }
    var shadowMap = shaderProps.shadowMap;
    if (shadowMap != null) {
      text.append_gw00v9$('uniform mat4 ' + GlslGenerator$Companion_getInstance().U_SHADOW_MVP + '[' + shadowMap.subMaps.length + '];' + '\n');
      text.append_gw00v9$(this.vsOut_gj894f$_0 + ' vec4 ' + GlslGenerator$Companion_getInstance().V_POSITION_LIGHTSPACE + '[' + shadowMap.subMaps.length + '];' + '\n');
      text.append_gw00v9$(this.vsOut_gj894f$_0 + ' float ' + GlslGenerator$Companion_getInstance().V_POSITION_CLIPSPACE_Z + ';' + '\n');
    }
    if (shaderProps.fogModel !== FogModel$FOG_OFF_getInstance()) {
      text.append_gw00v9$(this.vsIn_5467de$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_POSITION_WORLDSPACE + ';' + '\n');
    }
    tmp$ = this.customUniforms.iterator();
    while (tmp$.hasNext()) {
      var uniform = tmp$.next();
      text.append_gw00v9$('uniform ' + uniform.type + ' ' + uniform.name + ';' + '\n');
    }
  };
  GlslGenerator.prototype.generateVertBodyCode_fozol5$_0 = function (shaderProps, text) {
    var tmp$;
    text.append_gw00v9$('\nvoid main() {\n');
    text.append_gw00v9$('vec4 position = vec4(' + Attribute$Companion_getInstance().POSITIONS + ', 1.0);' + '\n');
    if (shaderProps.lightModel !== LightModel$NO_LIGHTING_getInstance()) {
      text.append_gw00v9$('vec4 normal = vec4(' + Attribute$Companion_getInstance().NORMALS + ', 0.0);' + '\n');
      if (shaderProps.isNormalMapped) {
        text.append_gw00v9$('vec4 tangent = vec4(' + Attribute$Companion_getInstance().TANGENTS + ', 0.0);' + '\n');
      }
    }
    var tmp$_0;
    tmp$_0 = this.injectors.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      element.vsBeforeProj_3c8d48$(shaderProps, text);
    }
    if (shaderProps.numBones > 0) {
      text.append_gw00v9$('mat4 boneT = ' + GlslGenerator$Companion_getInstance().U_BONES + '[' + Armature$Companion_getInstance().BONE_INDICES + '[0]] * ' + Armature$Companion_getInstance().BONE_WEIGHTS + '[0];' + '\n');
      text.append_gw00v9$('boneT += ' + GlslGenerator$Companion_getInstance().U_BONES + '[' + Armature$Companion_getInstance().BONE_INDICES + '[1]] * ' + Armature$Companion_getInstance().BONE_WEIGHTS + '[1];' + '\n');
      text.append_gw00v9$('boneT += ' + GlslGenerator$Companion_getInstance().U_BONES + '[' + Armature$Companion_getInstance().BONE_INDICES + '[2]] * ' + Armature$Companion_getInstance().BONE_WEIGHTS + '[2];' + '\n');
      text.append_gw00v9$('boneT += ' + GlslGenerator$Companion_getInstance().U_BONES + '[' + Armature$Companion_getInstance().BONE_INDICES + '[3]] * ' + Armature$Companion_getInstance().BONE_WEIGHTS + '[3];' + '\n');
      text.append_gw00v9$('position = boneT * position;\n');
      if (shaderProps.lightModel !== LightModel$NO_LIGHTING_getInstance()) {
        text.append_gw00v9$('normal = boneT * normal;\n');
        if (shaderProps.isNormalMapped) {
          text.append_gw00v9$('tangent = boneT * tangent;\n');
        }
      }
    }
    text.append_gw00v9$('gl_Position = uMvpMatrix * position;\n');
    var tmp$_1;
    tmp$_1 = this.injectors.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.vsAfterProj_3c8d48$(shaderProps, text);
    }
    var shadowMap = shaderProps.shadowMap;
    if (shadowMap != null) {
      tmp$ = shadowMap.subMaps;
      for (var i = 0; i !== tmp$.length; ++i) {
        text.append_gw00v9$(GlslGenerator$Companion_getInstance().V_POSITION_LIGHTSPACE + '[' + i + '] = ' + GlslGenerator$Companion_getInstance().U_SHADOW_MVP + '[' + i + '] * (' + GlslGenerator$Companion_getInstance().U_MODEL_MATRIX + ' * position);' + '\n');
      }
      text.append_gw00v9$('vPositionClipspaceZ = gl_Position.z;\n');
    }
    if (shaderProps.fogModel !== FogModel$FOG_OFF_getInstance()) {
      text.append_gw00v9$('vPositionWorldspace = (uModelMatrix * position).xyz;\n');
    }
    if (shaderProps.isTextureColor) {
      text.append_gw00v9$(GlslGenerator$Companion_getInstance().V_TEX_COORD + ' = ' + Attribute$Companion_getInstance().TEXTURE_COORDS.name + ';' + '\n');
    }
    if (shaderProps.isVertexColor) {
      text.append_gw00v9$(GlslGenerator$Companion_getInstance().V_COLOR + ' = ' + Attribute$Companion_getInstance().COLORS.name + ';' + '\n');
    }
    if (shaderProps.lightModel === LightModel$PHONG_LIGHTING_getInstance()) {
      text.append_gw00v9$('vEyeDirection_cameraspace = -(uViewMatrix * (uModelMatrix * position)).xyz;\n');
      text.append_gw00v9$('vLightDirection_cameraspace = (uViewMatrix * vec4(uLightDirection, 0.0)).xyz;\n');
      text.append_gw00v9$('vNormal_cameraspace = (uViewMatrix * (uModelMatrix * normal)).xyz;\n');
      if (shaderProps.isNormalMapped) {
        text.append_gw00v9$('vTangent = (uViewMatrix * (uModelMatrix * tangent)).xyz;\n');
      }
    }
     else if (shaderProps.lightModel === LightModel$GOURAUD_LIGHTING_getInstance()) {
      text.append_gw00v9$('vec3 e = normalize(-(uViewMatrix * (uModelMatrix * position)).xyz);\n');
      text.append_gw00v9$('vec3 l = normalize((uViewMatrix * vec4(uLightDirection, 0.0)).xyz);\n');
      text.append_gw00v9$('vec3 n = normalize((uViewMatrix * (uModelMatrix * normal)).xyz);\n');
      text.append_gw00v9$('float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n');
      text.append_gw00v9$('vec3 r = reflect(-l, n);\n');
      text.append_gw00v9$('float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n');
      text.append_gw00v9$('vDiffuseLightColor = uLightColor * cosTheta;\n');
      text.append_gw00v9$('vSpecularLightColor = uLightColor * uSpecularIntensity * pow(cosAlpha, uShininess);\n');
    }
    var tmp$_2;
    tmp$_2 = this.injectors.iterator();
    while (tmp$_2.hasNext()) {
      var element_1 = tmp$_2.next();
      element_1.vsEnd_3c8d48$(shaderProps, text);
    }
    text.append_gw00v9$('}\n');
  };
  GlslGenerator.prototype.generateFragInputCode_7emddk$_0 = function (shaderProps, text) {
    var tmp$, tmp$_0;
    text.append_gw00v9$('precision highp float;\n');
    text.append_gw00v9$('uniform mat4 uModelMatrix;\n');
    text.append_gw00v9$('uniform mat4 uViewMatrix;\n');
    if (shaderProps.isAlpha) {
      text.append_gw00v9$('uniform float uAlpha;\n');
    }
    if (shaderProps.isSaturation) {
      text.append_gw00v9$('uniform float uSaturation;\n');
    }
    if (shaderProps.lightModel === LightModel$PHONG_LIGHTING_getInstance()) {
      text.append_gw00v9$('uniform vec3 uLightColor;\n');
      text.append_gw00v9$('uniform float uShininess;\n');
      text.append_gw00v9$('uniform float uSpecularIntensity;\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_EYE_DIRECTION + ';' + '\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_LIGHT_DIRECTION + ';' + '\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_NORMAL + ';' + '\n');
      if (shaderProps.isNormalMapped) {
        text.append_gw00v9$('uniform sampler2D uNormalMap0;\n');
        text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_TANGENT + ';' + '\n');
      }
    }
     else if (shaderProps.lightModel === LightModel$GOURAUD_LIGHTING_getInstance()) {
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_DIFFUSE_LIGHT_COLOR + ';' + '\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_SPECULAR_LIGHT_COLOR + ';' + '\n');
    }
    if (shaderProps.isTextureColor) {
      text.append_gw00v9$('uniform sampler2D uTexture0;\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec2 ' + GlslGenerator$Companion_getInstance().V_TEX_COORD + ';' + '\n');
    }
    if (shaderProps.isVertexColor) {
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec4 ' + GlslGenerator$Companion_getInstance().V_COLOR + ';' + '\n');
    }
    if (shaderProps.isStaticColor) {
      text.append_gw00v9$('uniform vec4 uStaticColor;\n');
    }
    var shadowMap = shaderProps.shadowMap;
    if (shadowMap != null) {
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec4 ' + GlslGenerator$Companion_getInstance().V_POSITION_LIGHTSPACE + '[' + shadowMap.subMaps.length + '];' + '\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' float ' + GlslGenerator$Companion_getInstance().V_POSITION_CLIPSPACE_Z + ';' + '\n');
      tmp$ = shadowMap.subMaps;
      for (var i = 0; i !== tmp$.length; ++i) {
        text.append_gw00v9$('uniform sampler2D ' + GlslGenerator$Companion_getInstance().U_SHADOW_TEX + '_' + i + ';' + '\n');
      }
      text.append_gw00v9$('uniform int ' + GlslGenerator$Companion_getInstance().U_SHADOW_TEX_SZ + '[' + shadowMap.subMaps.length + '];' + '\n');
      text.append_gw00v9$('uniform float ' + GlslGenerator$Companion_getInstance().U_CLIP_SPACE_FAR_Z + '[' + shadowMap.subMaps.length + '];' + '\n');
    }
    if (shaderProps.fogModel !== FogModel$FOG_OFF_getInstance()) {
      text.append_gw00v9$('uniform vec3 uCameraPosition;\n');
      text.append_gw00v9$('uniform vec4 uFogColor;\n');
      text.append_gw00v9$('uniform float uFogRange;\n');
      text.append_gw00v9$(this.fsIn_4vdhvm$_0 + ' vec3 ' + GlslGenerator$Companion_getInstance().V_POSITION_WORLDSPACE + ';' + '\n');
    }
    tmp$_0 = this.customUniforms.iterator();
    while (tmp$_0.hasNext()) {
      var uniform = tmp$_0.next();
      text.append_gw00v9$('uniform ' + uniform.type + ' ' + uniform.name + ';' + '\n');
    }
    text.append_gw00v9$(this.fsOut_8yiatb$_0);
  };
  function GlslGenerator$generateFragBodyCode$addSample(closure$text, this$GlslGenerator) {
    return function (x, y) {
      closure$text.append_gw00v9$('shadowMapDepth = ' + this$GlslGenerator.texSampler_1a4zdf$_0 + '(shadowTex, projPos.xy + vec2(float(' + x + ') * off, float(' + y + ') * off)).x;' + '\n');
      closure$text.append_gw00v9$('factor += clamp((shadowMapDepth - (projPos.z - accLvl)) * 1e6, 0.0, 1.0);\n');
    };
  }
  GlslGenerator.prototype.generateFragBodyCode_1c1dne$_0 = function (shaderProps, text) {
    var tmp$;
    var shadowMap = shaderProps.shadowMap;
    if (shadowMap != null) {
      var addSample = GlslGenerator$generateFragBodyCode$addSample(text, this);
      text.append_gw00v9$('float calcShadowFactor(sampler2D shadowTex, vec3 projPos, float off, float accLvl) {\n');
      text.append_gw00v9$('  float factor = 0.0;\n');
      text.append_gw00v9$('  float shadowMapDepth = 0.0;\n');
      for (var y = -1; y <= 1; y++) {
        for (var x = -1; x <= 1; x++) {
          addSample(x, y);
        }
      }
      text.append_gw00v9$('  return factor / 9.0;\n');
      text.append_gw00v9$('}\n');
    }
    if (shaderProps.isNormalMapped) {
      text.append_gw00v9$('vec3 calcBumpedNormal() {\n');
      text.append_gw00v9$('  vec3 normal = normalize(vNormal_cameraspace);\n');
      text.append_gw00v9$('  vec3 tangent = normalize(vTangent);\n');
      text.append_gw00v9$('  tangent = normalize(tangent - dot(tangent, normal) * normal);\n');
      text.append_gw00v9$('  vec3 bitangent = cross(tangent, normal);\n');
      text.append_gw00v9$('  vec3 bumpMapNormal = ' + this.texSampler_1a4zdf$_0 + '(' + GlslGenerator$Companion_getInstance().U_NORMAL_MAP_0 + ', ' + GlslGenerator$Companion_getInstance().V_TEX_COORD + ').xyz;' + '\n');
      text.append_gw00v9$('  bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);\n');
      text.append_gw00v9$('  mat3 tbn = mat3(tangent, bitangent, normal);\n');
      text.append_gw00v9$('  return normalize(tbn * bumpMapNormal);\n');
      text.append_gw00v9$('}\n');
    }
    text.append_gw00v9$('\nvoid main() {\n');
    text.append_gw00v9$('float shadowFactor = 1.0;\n');
    var tmp$_0;
    tmp$_0 = this.injectors.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      element.fsBeforeSampling_3c8d48$(shaderProps, text);
    }
    if (shaderProps.isTextureColor) {
      text.append_gw00v9$('vec4 ' + GlslGenerator$Companion_getInstance().L_TEX_COLOR + ' = ' + this.texSampler_1a4zdf$_0 + '(' + GlslGenerator$Companion_getInstance().U_TEXTURE_0 + ', ' + GlslGenerator$Companion_getInstance().V_TEX_COORD + ');' + '\n');
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + ' = ' + GlslGenerator$Companion_getInstance().L_TEX_COLOR + ';' + '\n');
    }
    if (shaderProps.isVertexColor) {
      text.append_gw00v9$('vec4 vertColor = vFragmentColor;\n');
      text.append_gw00v9$('vertColor.rgb *= vertColor.a;\n');
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + ' = ' + GlslGenerator$Companion_getInstance().L_VERTEX_COLOR + ';' + '\n');
    }
    if (shaderProps.isStaticColor) {
      text.append_gw00v9$('vec4 staticColor = uStaticColor;\n');
      text.append_gw00v9$('staticColor.rgb *= staticColor.a;\n');
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + ' = ' + GlslGenerator$Companion_getInstance().L_STATIC_COLOR + ';' + '\n');
    }
    var tmp$_1;
    tmp$_1 = this.injectors.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.fsAfterSampling_3c8d48$(shaderProps, text);
    }
    if (shaderProps.isDiscardTranslucent) {
      text.append_gw00v9$('if (' + this.fsOutBody_rbmxbx$_0 + '.a == 0.0) { discard; }');
    }
    if (shadowMap != null) {
      tmp$ = shadowMap.subMaps;
      for (var i = 0; i !== tmp$.length; ++i) {
        text.append_gw00v9$('if (' + GlslGenerator$Companion_getInstance().V_POSITION_CLIPSPACE_Z + ' <= ' + GlslGenerator$Companion_getInstance().U_CLIP_SPACE_FAR_Z + '[' + i + ']) {' + '\n');
        text.append_gw00v9$('  vec3 projPos = ' + GlslGenerator$Companion_getInstance().V_POSITION_LIGHTSPACE + '[' + i + '].xyz / ' + GlslGenerator$Companion_getInstance().V_POSITION_LIGHTSPACE + '[' + i + '].w;' + '\n');
        text.append_gw00v9$('  float off = 1.0 / float(' + GlslGenerator$Companion_getInstance().U_SHADOW_TEX_SZ + '[' + i + ']);' + '\n');
        text.append_gw00v9$('  shadowFactor = calcShadowFactor(' + GlslGenerator$Companion_getInstance().U_SHADOW_TEX + '_' + i + ', projPos, off, ' + (i + 1 | 0) + '.0 * 0.001);' + '\n');
        text.append_gw00v9$('}\n');
        if (i < (shadowMap.subMaps.length - 1 | 0)) {
          text.append_gw00v9$('else ');
        }
      }
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + '.xyz *= shadowFactor / 2.0 + 0.5;' + '\n');
    }
    if (shaderProps.lightModel !== LightModel$NO_LIGHTING_getInstance()) {
      if (shaderProps.lightModel === LightModel$PHONG_LIGHTING_getInstance()) {
        text.append_gw00v9$('vec3 e = normalize(vEyeDirection_cameraspace);\n');
        text.append_gw00v9$('vec3 l = normalize(vLightDirection_cameraspace);\n');
        if (shaderProps.isNormalMapped) {
          text.append_gw00v9$('vec3 n = calcBumpedNormal();\n');
        }
         else {
          text.append_gw00v9$('vec3 n = normalize(vNormal_cameraspace);\n');
        }
        text.append_gw00v9$('float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n');
        text.append_gw00v9$('vec3 r = reflect(-l, n);\n');
        text.append_gw00v9$('float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n');
        text.append_gw00v9$('vec3 materialAmbientColor = ' + this.fsOutBody_rbmxbx$_0 + '.rgb * vec3(0.42);' + '\n');
        text.append_gw00v9$('vec3 materialDiffuseColor = ' + this.fsOutBody_rbmxbx$_0 + '.rgb * ' + GlslGenerator$Companion_getInstance().U_LIGHT_COLOR + ' * cosTheta;' + '\n');
        text.append_gw00v9$('vec3 materialSpecularColor = ' + GlslGenerator$Companion_getInstance().U_LIGHT_COLOR + ' * ' + GlslGenerator$Companion_getInstance().U_SPECULAR_INTENSITY + ' * pow(cosAlpha, ' + GlslGenerator$Companion_getInstance().U_SHININESS + ') * ' + this.fsOutBody_rbmxbx$_0 + '.a * shadowFactor;' + '\n');
      }
       else if (shaderProps.lightModel === LightModel$GOURAUD_LIGHTING_getInstance()) {
        text.append_gw00v9$('vec3 materialAmbientColor = ' + this.fsOutBody_rbmxbx$_0 + '.rgb * vec3(0.42);' + '\n');
        text.append_gw00v9$('vec3 materialDiffuseColor = ' + this.fsOutBody_rbmxbx$_0 + '.rgb * ' + GlslGenerator$Companion_getInstance().V_DIFFUSE_LIGHT_COLOR + ';' + '\n');
        text.append_gw00v9$('vec3 materialSpecularColor = ' + GlslGenerator$Companion_getInstance().V_SPECULAR_LIGHT_COLOR + ' * ' + this.fsOutBody_rbmxbx$_0 + '.a * shadowFactor;' + '\n');
      }
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + ' = vec4(materialAmbientColor + materialDiffuseColor + materialSpecularColor, ' + this.fsOutBody_rbmxbx$_0 + '.a);' + '\n');
    }
    if (shaderProps.fogModel !== FogModel$FOG_OFF_getInstance()) {
      text.append_gw00v9$('float d = 1.0 - clamp(length(uCameraPosition - vPositionWorldspace / uFogRange), 0.0, 1.0);\n');
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + '.rgb = mix(' + GlslGenerator$Companion_getInstance().U_FOG_COLOR + '.rgb, ' + this.fsOutBody_rbmxbx$_0 + '.rgb, d * d * ' + GlslGenerator$Companion_getInstance().U_FOG_COLOR + '.a);' + '\n');
    }
    if (shaderProps.isAlpha) {
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + ' *= ' + GlslGenerator$Companion_getInstance().U_ALPHA + ';' + '\n');
    }
    if (shaderProps.isSaturation) {
      text.append_gw00v9$('float avgColor = (' + this.fsOutBody_rbmxbx$_0 + '.r + ' + this.fsOutBody_rbmxbx$_0 + '.g + ' + this.fsOutBody_rbmxbx$_0 + '.b) * 0.333;' + '\n');
      text.append_gw00v9$(this.fsOutBody_rbmxbx$_0 + '.rgb = mix(vec3(avgColor), ' + this.fsOutBody_rbmxbx$_0 + '.rgb, ' + GlslGenerator$Companion_getInstance().U_SATURATION + ');' + '\n');
    }
    var tmp$_2;
    tmp$_2 = this.injectors.iterator();
    while (tmp$_2.hasNext()) {
      var element_1 = tmp$_2.next();
      element_1.fsEnd_3c8d48$(shaderProps, text);
    }
    text.append_gw00v9$('}\n');
  };
  GlslGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslGenerator',
    interfaces: []
  };
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  function Shader() {
    GlObject.call(this);
    this.source_ow0gcj$_0 = new Shader$Source('', '', '');
    this.attributeLocations = ArrayList_init();
    this.attributes = LinkedHashSet_init();
    this.uniforms = LinkedHashMap_init();
  }
  Object.defineProperty(Shader.prototype, 'source', {
    get: function () {
      return this.source_ow0gcj$_0;
    },
    set: function (source) {
      this.source_ow0gcj$_0 = source;
    }
  });
  function Shader$Source(vertexSrc, geometrySrc, fragmentSrc) {
    this.vertexSrc = vertexSrc;
    this.geometrySrc = geometrySrc;
    this.fragmentSrc = fragmentSrc;
  }
  Shader$Source.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Source',
    interfaces: []
  };
  function Shader$Shader$Source_init(vertexSrc, fragmentSrc, $this) {
    $this = $this || Object.create(Shader$Source.prototype);
    Shader$Source.call($this, vertexSrc, '', fragmentSrc);
    return $this;
  }
  Shader$Source.prototype.component1 = function () {
    return this.vertexSrc;
  };
  Shader$Source.prototype.component2 = function () {
    return this.geometrySrc;
  };
  Shader$Source.prototype.component3 = function () {
    return this.fragmentSrc;
  };
  Shader$Source.prototype.copy_6hosri$ = function (vertexSrc, geometrySrc, fragmentSrc) {
    return new Shader$Source(vertexSrc === void 0 ? this.vertexSrc : vertexSrc, geometrySrc === void 0 ? this.geometrySrc : geometrySrc, fragmentSrc === void 0 ? this.fragmentSrc : fragmentSrc);
  };
  Shader$Source.prototype.toString = function () {
    return 'Source(vertexSrc=' + Kotlin.toString(this.vertexSrc) + (', geometrySrc=' + Kotlin.toString(this.geometrySrc)) + (', fragmentSrc=' + Kotlin.toString(this.fragmentSrc)) + ')';
  };
  Shader$Source.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.vertexSrc) | 0;
    result = result * 31 + Kotlin.hashCode(this.geometrySrc) | 0;
    result = result * 31 + Kotlin.hashCode(this.fragmentSrc) | 0;
    return result;
  };
  Shader$Source.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.vertexSrc, other.vertexSrc) && Kotlin.equals(this.geometrySrc, other.geometrySrc) && Kotlin.equals(this.fragmentSrc, other.fragmentSrc)))));
  };
  function Shader$AttributeLocation(descr, location) {
    this.descr = descr;
    this.location = location;
  }
  Shader$AttributeLocation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AttributeLocation',
    interfaces: []
  };
  Shader$AttributeLocation.prototype.component1 = function () {
    return this.descr;
  };
  Shader$AttributeLocation.prototype.component2 = function () {
    return this.location;
  };
  Shader$AttributeLocation.prototype.copy_myfgg1$ = function (descr, location) {
    return new Shader$AttributeLocation(descr === void 0 ? this.descr : descr, location === void 0 ? this.location : location);
  };
  Shader$AttributeLocation.prototype.toString = function () {
    return 'AttributeLocation(descr=' + Kotlin.toString(this.descr) + (', location=' + Kotlin.toString(this.location)) + ')';
  };
  Shader$AttributeLocation.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.descr) | 0;
    result = result * 31 + Kotlin.hashCode(this.location) | 0;
    return result;
  };
  Shader$AttributeLocation.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.descr, other.descr) && Kotlin.equals(this.location, other.location)))));
  };
  Shader.prototype.isBound_evfofk$ = function (ctx) {
    return ctx.shaderMgr.boundShader === this;
  };
  Shader.prototype.onLoad_evfofk$ = function (ctx) {
    var tmp$, tmp$_0;
    this.generate_evfofk$(ctx);
    this.res = ctx.shaderMgr.createShader_saxrfs$(this.source, ctx);
    this.attributeLocations.clear();
    tmp$ = this.attributes.iterator();
    while (tmp$.hasNext()) {
      var attrib = tmp$.next();
      this.enableAttribute_h6qn1s$_0(attrib, ctx);
    }
    tmp$_0 = this.uniforms.values.iterator();
    while (tmp$_0.hasNext()) {
      var uniform = tmp$_0.next();
      uniform.location = this.findUniformLocation_myr2gy$(uniform.name, ctx);
    }
  };
  Shader.prototype.findAttributeLocation_myr2gy$ = function (attribName, ctx) {
    var ref = this.res;
    if (ref != null) {
      return glGetAttribLocation(ref, attribName);
    }
     else {
      return -1;
    }
  };
  Shader.prototype.enableAttribute_h6qn1s$_0 = function (attribute, ctx) {
    var location = this.findAttributeLocation_myr2gy$(attribute.name, ctx);
    this.enableAttribute_l20pqy$_0(attribute, location, ctx);
    return location >= 0;
  };
  Shader.prototype.enableAttribute_l20pqy$_0 = function (attribute, location, ctx) {
    if (location >= 0) {
      this.attributeLocations.add_11rb$(new Shader$AttributeLocation(attribute, location));
    }
  };
  Shader.prototype.addUniform_1ybs2r$ = function (uniform) {
    var $receiver = this.uniforms;
    var key = uniform.name;
    $receiver.put_xwzc9p$(key, uniform);
    return uniform;
  };
  Shader.prototype.getUniform_7yby8k$ = function (name) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.uniforms.get_11rb$(name), Uniform) ? tmp$ : null;
  };
  Shader.prototype.findUniformLocation_myr2gy$ = function (uniformName, ctx) {
    var ref = this.res;
    if (ref != null) {
      return glGetUniformLocation(ref, uniformName);
    }
     else {
      return null;
    }
  };
  Shader.prototype.bindMesh_lij8m4$ = function (mesh, ctx) {
    var tmp$;
    tmp$ = this.attributeLocations;
    for (var i = 0; i !== tmp$.size; ++i) {
      var tmp$_0;
      var attrib = this.attributeLocations.get_za3lpa$(i);
      tmp$_0 = mesh.meshData.attributeBinders.get_11rb$(attrib.descr);
      if (tmp$_0 == null) {
        throw new KoolException('Mesh must supply an attribute binder for attribute ' + attrib.descr.name);
      }
      var binder = tmp$_0;
      glEnableVertexAttribArray(attrib.location);
      binder.bindAttribute_gre2l6$(attrib.location, ctx);
    }
  };
  Shader.prototype.unbindMesh_evfofk$ = function (ctx) {
    var tmp$;
    tmp$ = this.attributeLocations;
    for (var i = 0; i !== tmp$.size; ++i) {
      glDisableVertexAttribArray(this.attributeLocations.get_za3lpa$(i).location);
    }
  };
  Shader.prototype.dispose_evfofk$ = function (ctx) {
    if (this.isValid) {
      ctx.shaderMgr.deleteShader_v7cqlj$(this, ctx);
      this.res = null;
    }
  };
  Shader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shader',
    interfaces: [GlObject]
  };
  function LightModel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function LightModel_initFields() {
    LightModel_initFields = function () {
    };
    LightModel$PHONG_LIGHTING_instance = new LightModel('PHONG_LIGHTING', 0);
    LightModel$GOURAUD_LIGHTING_instance = new LightModel('GOURAUD_LIGHTING', 1);
    LightModel$NO_LIGHTING_instance = new LightModel('NO_LIGHTING', 2);
  }
  var LightModel$PHONG_LIGHTING_instance;
  function LightModel$PHONG_LIGHTING_getInstance() {
    LightModel_initFields();
    return LightModel$PHONG_LIGHTING_instance;
  }
  var LightModel$GOURAUD_LIGHTING_instance;
  function LightModel$GOURAUD_LIGHTING_getInstance() {
    LightModel_initFields();
    return LightModel$GOURAUD_LIGHTING_instance;
  }
  var LightModel$NO_LIGHTING_instance;
  function LightModel$NO_LIGHTING_getInstance() {
    LightModel_initFields();
    return LightModel$NO_LIGHTING_instance;
  }
  LightModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LightModel',
    interfaces: [Enum]
  };
  function LightModel$values() {
    return [LightModel$PHONG_LIGHTING_getInstance(), LightModel$GOURAUD_LIGHTING_getInstance(), LightModel$NO_LIGHTING_getInstance()];
  }
  LightModel.values = LightModel$values;
  function LightModel$valueOf(name) {
    switch (name) {
      case 'PHONG_LIGHTING':
        return LightModel$PHONG_LIGHTING_getInstance();
      case 'GOURAUD_LIGHTING':
        return LightModel$GOURAUD_LIGHTING_getInstance();
      case 'NO_LIGHTING':
        return LightModel$NO_LIGHTING_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.LightModel.' + name);
    }
  }
  LightModel.valueOf_61zpoe$ = LightModel$valueOf;
  function ColorModel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ColorModel_initFields() {
    ColorModel_initFields = function () {
    };
    ColorModel$VERTEX_COLOR_instance = new ColorModel('VERTEX_COLOR', 0);
    ColorModel$TEXTURE_COLOR_instance = new ColorModel('TEXTURE_COLOR', 1);
    ColorModel$STATIC_COLOR_instance = new ColorModel('STATIC_COLOR', 2);
  }
  var ColorModel$VERTEX_COLOR_instance;
  function ColorModel$VERTEX_COLOR_getInstance() {
    ColorModel_initFields();
    return ColorModel$VERTEX_COLOR_instance;
  }
  var ColorModel$TEXTURE_COLOR_instance;
  function ColorModel$TEXTURE_COLOR_getInstance() {
    ColorModel_initFields();
    return ColorModel$TEXTURE_COLOR_instance;
  }
  var ColorModel$STATIC_COLOR_instance;
  function ColorModel$STATIC_COLOR_getInstance() {
    ColorModel_initFields();
    return ColorModel$STATIC_COLOR_instance;
  }
  ColorModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorModel',
    interfaces: [Enum]
  };
  function ColorModel$values() {
    return [ColorModel$VERTEX_COLOR_getInstance(), ColorModel$TEXTURE_COLOR_getInstance(), ColorModel$STATIC_COLOR_getInstance()];
  }
  ColorModel.values = ColorModel$values;
  function ColorModel$valueOf(name) {
    switch (name) {
      case 'VERTEX_COLOR':
        return ColorModel$VERTEX_COLOR_getInstance();
      case 'TEXTURE_COLOR':
        return ColorModel$TEXTURE_COLOR_getInstance();
      case 'STATIC_COLOR':
        return ColorModel$STATIC_COLOR_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.ColorModel.' + name);
    }
  }
  ColorModel.valueOf_61zpoe$ = ColorModel$valueOf;
  function FogModel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function FogModel_initFields() {
    FogModel_initFields = function () {
    };
    FogModel$FOG_OFF_instance = new FogModel('FOG_OFF', 0);
    FogModel$FOG_ON_instance = new FogModel('FOG_ON', 1);
  }
  var FogModel$FOG_OFF_instance;
  function FogModel$FOG_OFF_getInstance() {
    FogModel_initFields();
    return FogModel$FOG_OFF_instance;
  }
  var FogModel$FOG_ON_instance;
  function FogModel$FOG_ON_getInstance() {
    FogModel_initFields();
    return FogModel$FOG_ON_instance;
  }
  FogModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FogModel',
    interfaces: [Enum]
  };
  function FogModel$values() {
    return [FogModel$FOG_OFF_getInstance(), FogModel$FOG_ON_getInstance()];
  }
  FogModel.values = FogModel$values;
  function FogModel$valueOf(name) {
    switch (name) {
      case 'FOG_OFF':
        return FogModel$FOG_OFF_getInstance();
      case 'FOG_ON':
        return FogModel$FOG_ON_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.FogModel.' + name);
    }
  }
  FogModel.valueOf_61zpoe$ = FogModel$valueOf;
  function ShaderProps() {
    this.lightModel = LightModel$PHONG_LIGHTING_getInstance();
    this.colorModel_3v9dr8$_0 = ColorModel$STATIC_COLOR_getInstance();
    this.fogModel = FogModel$FOG_OFF_getInstance();
    this.isVertexColor = false;
    this.isTextureColor = false;
    this.isStaticColor = true;
    this.isAlpha = false;
    this.isSaturation = false;
    this.isDiscardTranslucent = false;
    this.numBones = 0;
    this.shadowMap_15c6aa$_0 = null;
    this.isNormalMapped = false;
    this.shininess = 20.0;
    this.specularIntensity = 0.75;
    this.staticColor = Color$Companion_getInstance().BLACK;
    this.alpha = 1.0;
    this.saturation = 1.0;
    this.texture = null;
    this.normalMap = null;
  }
  Object.defineProperty(ShaderProps.prototype, 'colorModel', {
    get: function () {
      return this.colorModel_3v9dr8$_0;
    },
    set: function (value) {
      switch (value.name) {
        case 'VERTEX_COLOR':
          this.isVertexColor = true;
          this.isTextureColor = false;
          this.isStaticColor = false;
          break;
        case 'TEXTURE_COLOR':
          this.isVertexColor = false;
          this.isTextureColor = true;
          this.isStaticColor = false;
          break;
        case 'STATIC_COLOR':
          this.isVertexColor = false;
          this.isTextureColor = false;
          this.isStaticColor = true;
          break;
      }
    }
  });
  Object.defineProperty(ShaderProps.prototype, 'shadowMap', {
    get: function () {
      if (glCapabilities.depthTextures) {
        return this.shadowMap_15c6aa$_0;
      }
       else {
        return null;
      }
    },
    set: function (shadowMap) {
      this.shadowMap_15c6aa$_0 = shadowMap;
    }
  });
  ShaderProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderProps',
    interfaces: []
  };
  function ShadingHints(preferredLightModel, preferredShadowMethod) {
    this.preferredLightModel = preferredLightModel;
    this.preferredShadowMethod = preferredShadowMethod;
  }
  ShadingHints.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShadingHints',
    interfaces: []
  };
  ShadingHints.prototype.component1 = function () {
    return this.preferredLightModel;
  };
  ShadingHints.prototype.component2 = function () {
    return this.preferredShadowMethod;
  };
  ShadingHints.prototype.copy_m0yo6y$ = function (preferredLightModel, preferredShadowMethod) {
    return new ShadingHints(preferredLightModel === void 0 ? this.preferredLightModel : preferredLightModel, preferredShadowMethod === void 0 ? this.preferredShadowMethod : preferredShadowMethod);
  };
  ShadingHints.prototype.toString = function () {
    return 'ShadingHints(preferredLightModel=' + Kotlin.toString(this.preferredLightModel) + (', preferredShadowMethod=' + Kotlin.toString(this.preferredShadowMethod)) + ')';
  };
  ShadingHints.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.preferredLightModel) | 0;
    result = result * 31 + Kotlin.hashCode(this.preferredShadowMethod) | 0;
    return result;
  };
  ShadingHints.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.preferredLightModel, other.preferredLightModel) && Kotlin.equals(this.preferredShadowMethod, other.preferredShadowMethod)))));
  };
  function PreferredLightModel(name, ordinal, level) {
    Enum.call(this);
    this.level = level;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function PreferredLightModel_initFields() {
    PreferredLightModel_initFields = function () {
    };
    PreferredLightModel$NO_LIGHTING_instance = new PreferredLightModel('NO_LIGHTING', 0, 0);
    PreferredLightModel$GOURAUD_instance = new PreferredLightModel('GOURAUD', 1, 1);
    PreferredLightModel$PHONG_instance = new PreferredLightModel('PHONG', 2, 2);
  }
  var PreferredLightModel$NO_LIGHTING_instance;
  function PreferredLightModel$NO_LIGHTING_getInstance() {
    PreferredLightModel_initFields();
    return PreferredLightModel$NO_LIGHTING_instance;
  }
  var PreferredLightModel$GOURAUD_instance;
  function PreferredLightModel$GOURAUD_getInstance() {
    PreferredLightModel_initFields();
    return PreferredLightModel$GOURAUD_instance;
  }
  var PreferredLightModel$PHONG_instance;
  function PreferredLightModel$PHONG_getInstance() {
    PreferredLightModel_initFields();
    return PreferredLightModel$PHONG_instance;
  }
  PreferredLightModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PreferredLightModel',
    interfaces: [Enum]
  };
  function PreferredLightModel$values() {
    return [PreferredLightModel$NO_LIGHTING_getInstance(), PreferredLightModel$GOURAUD_getInstance(), PreferredLightModel$PHONG_getInstance()];
  }
  PreferredLightModel.values = PreferredLightModel$values;
  function PreferredLightModel$valueOf(name) {
    switch (name) {
      case 'NO_LIGHTING':
        return PreferredLightModel$NO_LIGHTING_getInstance();
      case 'GOURAUD':
        return PreferredLightModel$GOURAUD_getInstance();
      case 'PHONG':
        return PreferredLightModel$PHONG_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.PreferredLightModel.' + name);
    }
  }
  PreferredLightModel.valueOf_61zpoe$ = PreferredLightModel$valueOf;
  function PreferredShadowMethod(name, ordinal, level) {
    Enum.call(this);
    this.level = level;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function PreferredShadowMethod_initFields() {
    PreferredShadowMethod_initFields = function () {
    };
    PreferredShadowMethod$NO_SHADOW_instance = new PreferredShadowMethod('NO_SHADOW', 0, 0);
    PreferredShadowMethod$SINGLE_SHADOW_MAP_instance = new PreferredShadowMethod('SINGLE_SHADOW_MAP', 1, 1);
    PreferredShadowMethod$CASCADED_SHADOW_MAP_instance = new PreferredShadowMethod('CASCADED_SHADOW_MAP', 2, 2);
  }
  var PreferredShadowMethod$NO_SHADOW_instance;
  function PreferredShadowMethod$NO_SHADOW_getInstance() {
    PreferredShadowMethod_initFields();
    return PreferredShadowMethod$NO_SHADOW_instance;
  }
  var PreferredShadowMethod$SINGLE_SHADOW_MAP_instance;
  function PreferredShadowMethod$SINGLE_SHADOW_MAP_getInstance() {
    PreferredShadowMethod_initFields();
    return PreferredShadowMethod$SINGLE_SHADOW_MAP_instance;
  }
  var PreferredShadowMethod$CASCADED_SHADOW_MAP_instance;
  function PreferredShadowMethod$CASCADED_SHADOW_MAP_getInstance() {
    PreferredShadowMethod_initFields();
    return PreferredShadowMethod$CASCADED_SHADOW_MAP_instance;
  }
  PreferredShadowMethod.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PreferredShadowMethod',
    interfaces: [Enum]
  };
  function PreferredShadowMethod$values() {
    return [PreferredShadowMethod$NO_SHADOW_getInstance(), PreferredShadowMethod$SINGLE_SHADOW_MAP_getInstance(), PreferredShadowMethod$CASCADED_SHADOW_MAP_getInstance()];
  }
  PreferredShadowMethod.values = PreferredShadowMethod$values;
  function PreferredShadowMethod$valueOf(name) {
    switch (name) {
      case 'NO_SHADOW':
        return PreferredShadowMethod$NO_SHADOW_getInstance();
      case 'SINGLE_SHADOW_MAP':
        return PreferredShadowMethod$SINGLE_SHADOW_MAP_getInstance();
      case 'CASCADED_SHADOW_MAP':
        return PreferredShadowMethod$CASCADED_SHADOW_MAP_getInstance();
      default:throwISE('No enum constant de.fabmax.kool.shading.PreferredShadowMethod.' + name);
    }
  }
  PreferredShadowMethod.valueOf_61zpoe$ = PreferredShadowMethod$valueOf;
  function Uniform(name, value) {
    this.name = name;
    this.value_xe1c$_0 = value;
    this.location = null;
  }
  Object.defineProperty(Uniform.prototype, 'value', {
    get: function () {
      return this.value_xe1c$_0;
    },
    set: function (value) {
      this.value_xe1c$_0 = value;
    }
  });
  Object.defineProperty(Uniform.prototype, 'isValid', {
    get: function () {
      return isValidUniformLocation(this.location);
    }
  });
  Uniform.prototype.bind_evfofk$ = function (ctx) {
    if (this.isValid) {
      this.doBind_evfofk$(ctx);
    }
  };
  Uniform.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform',
    interfaces: []
  };
  function UniformTexture2D(name) {
    Uniform.call(this, name, null);
    this.type_1l8eyg$_0 = 'sampler2D';
    this.value_l616rz$_0 = null;
  }
  Object.defineProperty(UniformTexture2D.prototype, 'type', {
    get: function () {
      return this.type_1l8eyg$_0;
    }
  });
  Object.defineProperty(UniformTexture2D.prototype, 'value', {
    get: function () {
      return this.value_l616rz$_0;
    },
    set: function (value) {
      this.value_l616rz$_0 = value;
    }
  });
  UniformTexture2D.prototype.doBind_evfofk$ = function (ctx) {
    var tex = this.value;
    if (tex != null) {
      var unit = ctx.textureMgr.bindTexture_4yp9vu$(tex, ctx);
      if (tex.isValid && ensureNotNull(tex.res).isLoaded) {
        glUniform1i(this.location, unit);
      }
    }
     else {
      glUniform1i(this.location, GL_NONE);
    }
  };
  UniformTexture2D.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UniformTexture2D',
    interfaces: [Uniform]
  };
  function UniformTexture2Dv(name, size) {
    var array = Array_0(size);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = null;
    }
    Uniform.call(this, name, array);
    this.type_7edzmq$_0 = 'sampler2D';
    this.texNames_0 = new Int32Array(size);
  }
  Object.defineProperty(UniformTexture2Dv.prototype, 'type', {
    get: function () {
      return this.type_7edzmq$_0;
    }
  });
  UniformTexture2Dv.prototype.doBind_evfofk$ = function (ctx) {
    var tmp$;
    tmp$ = this.value;
    for (var i = 0; i !== tmp$.length; ++i) {
      var tmp$_0;
      var tex = this.value[i];
      if (tex != null) {
        tmp$_0 = ctx.textureMgr.bindTexture_4yp9vu$(tex, ctx);
      }
       else {
        tmp$_0 = GL_NONE;
      }
      this.texNames_0[i] = tmp$_0;
    }
    glUniform1iv(this.location, this.texNames_0);
  };
  UniformTexture2Dv.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UniformTexture2Dv',
    interfaces: [Uniform]
  };
  function Uniform1i(name) {
    Uniform.call(this, name, 0);
    this.type_cwd6wh$_0 = 'int';
    this.value_pqheco$_0 = 0;
  }
  Object.defineProperty(Uniform1i.prototype, 'type', {
    get: function () {
      return this.type_cwd6wh$_0;
    }
  });
  Object.defineProperty(Uniform1i.prototype, 'value', {
    get: function () {
      return this.value_pqheco$_0;
    },
    set: function (value) {
      this.value_pqheco$_0 = value;
    }
  });
  Uniform1i.prototype.doBind_evfofk$ = function (ctx) {
    glUniform1i(this.location, this.value);
  };
  Uniform1i.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform1i',
    interfaces: [Uniform]
  };
  function Uniform1iv(name, size) {
    Uniform.call(this, name, new Int32Array(size));
    this.type_2txs21$_0 = 'int';
  }
  Object.defineProperty(Uniform1iv.prototype, 'type', {
    get: function () {
      return this.type_2txs21$_0;
    }
  });
  Uniform1iv.prototype.doBind_evfofk$ = function (ctx) {
    glUniform1iv(this.location, this.value);
  };
  Uniform1iv.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform1iv',
    interfaces: [Uniform]
  };
  function Uniform1f(name) {
    Uniform.call(this, name, 0.0);
    this.type_v4tnrm$_0 = 'float';
    this.value_tuvaez$_0 = 0.0;
  }
  Object.defineProperty(Uniform1f.prototype, 'type', {
    get: function () {
      return this.type_v4tnrm$_0;
    }
  });
  Object.defineProperty(Uniform1f.prototype, 'value', {
    get: function () {
      return this.value_tuvaez$_0;
    },
    set: function (value) {
      this.value_tuvaez$_0 = value;
    }
  });
  Uniform1f.prototype.doBind_evfofk$ = function (ctx) {
    glUniform1f(this.location, this.value);
  };
  Uniform1f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform1f',
    interfaces: [Uniform]
  };
  function Uniform1fv(name, size) {
    Uniform.call(this, name, new Float32Array(size));
    this.type_cltl5g$_0 = 'float';
  }
  Object.defineProperty(Uniform1fv.prototype, 'type', {
    get: function () {
      return this.type_cltl5g$_0;
    }
  });
  Uniform1fv.prototype.doBind_evfofk$ = function (ctx) {
    glUniform1fv(this.location, this.value);
  };
  Uniform1fv.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform1fv',
    interfaces: [Uniform]
  };
  function Uniform2f(name) {
    Uniform.call(this, name, MutableVec2f_init());
    this.type_2b76pf$_0 = 'vec2';
  }
  Object.defineProperty(Uniform2f.prototype, 'type', {
    get: function () {
      return this.type_2b76pf$_0;
    }
  });
  Uniform2f.prototype.doBind_evfofk$ = function (ctx) {
    glUniform2f(this.location, this.value.x, this.value.y);
  };
  Uniform2f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform2f',
    interfaces: [Uniform]
  };
  function Uniform3f(name) {
    Uniform.call(this, name, MutableVec3f_init());
    this.type_qifacs$_0 = 'vec3';
  }
  Object.defineProperty(Uniform3f.prototype, 'type', {
    get: function () {
      return this.type_qifacs$_0;
    }
  });
  Uniform3f.prototype.doBind_evfofk$ = function (ctx) {
    glUniform3f(this.location, this.value.x, this.value.y, this.value.z);
  };
  Uniform3f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform3f',
    interfaces: [Uniform]
  };
  function Uniform4f(name) {
    Uniform.call(this, name, MutableVec4f_init());
    this.type_fp2ak5$_0 = 'vec4';
  }
  Object.defineProperty(Uniform4f.prototype, 'type', {
    get: function () {
      return this.type_fp2ak5$_0;
    }
  });
  Uniform4f.prototype.doBind_evfofk$ = function (ctx) {
    glUniform4f(this.location, this.value.x, this.value.y, this.value.z, this.value.w);
  };
  Uniform4f.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform4f',
    interfaces: [Uniform]
  };
  function UniformMatrix4(name) {
    Uniform.call(this, name, null);
    this.type_abfjj6$_0 = 'mat4';
    this.value_z53m2d$_0 = null;
  }
  Object.defineProperty(UniformMatrix4.prototype, 'type', {
    get: function () {
      return this.type_abfjj6$_0;
    }
  });
  Object.defineProperty(UniformMatrix4.prototype, 'value', {
    get: function () {
      return this.value_z53m2d$_0;
    },
    set: function (value) {
      this.value_z53m2d$_0 = value;
    }
  });
  UniformMatrix4.prototype.doBind_evfofk$ = function (ctx) {
    var buf = this.value;
    if (buf != null) {
      glUniformMatrix4fv(this.location, false, buf);
    }
  };
  UniformMatrix4.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UniformMatrix4',
    interfaces: [Uniform]
  };
  function VboBinder(vbo, elemSize, strideBytes, offset, type) {
    if (offset === void 0)
      offset = 0;
    if (type === void 0)
      type = GL_FLOAT;
    this.vbo = vbo;
    this.elemSize = elemSize;
    this.strideBytes = strideBytes;
    this.offset = offset;
    this.type = type;
  }
  VboBinder.prototype.bindAttribute_gre2l6$ = function (target, ctx) {
    this.vbo.bind_evfofk$(ctx);
    if (this.type === GL_INT || this.type === GL_UNSIGNED_INT) {
      glVertexAttribIPointer(target, this.elemSize, this.type, this.strideBytes, this.offset * 4 | 0);
    }
     else {
      glVertexAttribPointer(target, this.elemSize, this.type, false, this.strideBytes, this.offset * 4 | 0);
    }
  };
  VboBinder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VboBinder',
    interfaces: []
  };
  function Animator(value) {
    Animator$Companion_getInstance();
    this.value = value;
    this.duration = 1.0;
    this.speed = 1.0;
    this.repeating = Animator$Companion_getInstance().ONCE;
    this.progress = 0.0;
  }
  function Animator$Companion() {
    Animator$Companion_instance = this;
    this.ONCE = 1;
    this.REPEAT = 2;
    this.REPEAT_TOGGLE_DIR = 3;
  }
  Animator$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Animator$Companion_instance = null;
  function Animator$Companion_getInstance() {
    if (Animator$Companion_instance === null) {
      new Animator$Companion();
    }
    return Animator$Companion_instance;
  }
  Animator.prototype.tick_evfofk$ = function (ctx) {
    var $receiver = this.speed;
    if (!(Math_0.abs($receiver) < 1.0E-5)) {
      this.progress += ctx.deltaT * this.speed / this.duration;
      if (this.progress >= 1.0 && this.speed > 0) {
        switch (this.repeating) {
          case 1:
            this.progress = 1.0;
            this.speed = 0.0;
            break;
          case 2:
            this.progress = 0.0;
            break;
          case 3:
            this.progress = 1.0;
            this.speed = -this.speed;
            break;
        }
      }
       else if (this.progress <= 0.0 && this.speed < 0) {
        switch (this.repeating) {
          case 1:
            this.progress = 0.0;
            this.speed = 0.0;
            break;
          case 2:
            this.progress = 1.0;
            break;
          case 3:
            this.progress = 0.0;
            this.speed = -this.speed;
            break;
        }
      }
      var $receiver_0 = this.progress;
      var clamp$result;
      if ($receiver_0 < 0.0) {
        clamp$result = 0.0;
      }
       else if ($receiver_0 > 1.0) {
        clamp$result = 1.0;
      }
       else {
        clamp$result = $receiver_0;
      }
      this.progress = clamp$result;
      this.value.interpolate_mx4ult$(this.interpolate_mx4ult$(this.progress));
    }
    return this.value.value;
  };
  Animator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Animator',
    interfaces: []
  };
  function LinearAnimator(value) {
    Animator.call(this, value);
  }
  LinearAnimator.prototype.interpolate_mx4ult$ = function (progress) {
    return progress;
  };
  LinearAnimator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinearAnimator',
    interfaces: [Animator]
  };
  function CosAnimator(value) {
    Animator.call(this, value);
  }
  CosAnimator.prototype.interpolate_mx4ult$ = function (progress) {
    var x = progress * math.PI;
    return 0.5 - Math_0.cos(x) * 0.5;
  };
  CosAnimator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CosAnimator',
    interfaces: [Animator]
  };
  function InterpolatedValue(initial) {
    this.value = initial;
    this.onUpdate = null;
  }
  InterpolatedValue.prototype.interpolate_mx4ult$ = function (progress) {
    var tmp$;
    this.updateValue_mx4ult$(progress);
    (tmp$ = this.onUpdate) != null ? tmp$(this.value) : null;
  };
  InterpolatedValue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InterpolatedValue',
    interfaces: []
  };
  function InterpolatedFloat(from, to) {
    InterpolatedValue.call(this, from);
    this.from = from;
    this.to = to;
  }
  InterpolatedFloat.prototype.updateValue_mx4ult$ = function (interpolationPos) {
    this.value = this.from + (this.to - this.from) * interpolationPos;
  };
  InterpolatedFloat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InterpolatedFloat',
    interfaces: [InterpolatedValue]
  };
  function InterpolatedColor(from, to) {
    InterpolatedValue.call(this, MutableColor_init());
    this.from = from;
    this.to = to;
    this.value.set_d7aj7k$(this.from);
  }
  InterpolatedColor.prototype.updateValue_mx4ult$ = function (interpolationPos) {
    this.value.set_d7aj7k$(this.to).subtract_czzhhz$(this.from).scale_mx4ult$(interpolationPos).add_d7aj7k$(this.from);
  };
  InterpolatedColor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InterpolatedColor',
    interfaces: [InterpolatedValue]
  };
  function BillboardMesh(data, name) {
    if (data === void 0)
      data = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().COLORS, Attribute$Companion_getInstance().TEXTURE_COORDS]);
    if (name === void 0)
      name = '';
    Mesh.call(this, data, name);
    this.shader = billboardShader(BillboardMesh_init$lambda);
    this.builder_0 = new MeshBuilder(data);
  }
  Object.defineProperty(BillboardMesh.prototype, 'billboardSize', {
    get: function () {
      var tmp$;
      return (Kotlin.isType(tmp$ = this.shader, BillboardShader) ? tmp$ : throwCCE()).billboardSize;
    },
    set: function (value) {
      var tmp$;
      (Kotlin.isType(tmp$ = this.shader, BillboardShader) ? tmp$ : throwCCE()).billboardSize = value;
    }
  });
  BillboardMesh.prototype.addQuad_4sqmhu$ = function (centerPosition, color) {
    this.builder_0.color = color;
    var $this = this.builder_0;
    var $receiver = $this.rectProps.defaults();
    $receiver.fullTexCoords();
    $receiver.origin.set_czzhiu$(centerPosition);
    $receiver.size.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
    $this.rect_e5k3t5$($this.rectProps);
  };
  function BillboardMesh_init$lambda($receiver) {
    $receiver.colorModel = ColorModel$VERTEX_COLOR_getInstance();
    $receiver.lightModel = LightModel$NO_LIGHTING_getInstance();
    return Unit;
  }
  BillboardMesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BillboardMesh',
    interfaces: [Mesh]
  };
  function billboardShader$lambda($receiver) {
    return Unit;
  }
  function billboardShader(propsInit) {
    if (propsInit === void 0)
      propsInit = billboardShader$lambda;
    var props = new ShaderProps();
    propsInit(props);
    props.isTextureColor = true;
    return new BillboardShader(props, new GlslGenerator());
  }
  function BillboardShader(props, generator) {
    BasicShader.call(this, props, generator);
    this.uViewportSz_0 = this.addUniform_1ybs2r$(new Uniform2f('uViewportSz'));
    this.billboardSize = 1.0;
    var $receiver = generator.customUniforms;
    var element = this.uViewportSz_0;
    $receiver.add_11rb$(element);
    var $receiver_0 = generator.injectors;
    var element_0 = new BillboardShader_init$ObjectLiteral();
    $receiver_0.add_11rb$(element_0);
  }
  BillboardShader.prototype.onBind_evfofk$ = function (ctx) {
    BasicShader.prototype.onBind_evfofk$.call(this, ctx);
    this.uViewportSz_0.value.set_dleff0$(0.5 * ctx.viewport.width / this.billboardSize, 0.5 * ctx.viewport.height / this.billboardSize);
    this.uViewportSz_0.bind_evfofk$(ctx);
  };
  function BillboardShader_init$ObjectLiteral() {
  }
  BillboardShader_init$ObjectLiteral.prototype.vsAfterProj_3c8d48$ = function (shaderProps, text) {
    text.append_gw00v9$('gl_Position.x += (' + Attribute$Companion_getInstance().TEXTURE_COORDS.name + '.x - 0.5) * gl_Position.w / uViewportSz.x;' + '\n').append_gw00v9$('gl_Position.y -= (' + Attribute$Companion_getInstance().TEXTURE_COORDS.name + '.y - 0.5) * gl_Position.w / uViewportSz.y;' + '\n');
  };
  BillboardShader_init$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [GlslGenerator$GlslInjector]
  };
  BillboardShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BillboardShader',
    interfaces: [BasicShader]
  };
  function BoundingBox() {
    this.mutMin_0 = MutableVec3f_init();
    this.mutMax_0 = MutableVec3f_init();
    this.mutSize_0 = MutableVec3f_init();
    this.mutCenter_0 = MutableVec3f_init();
    this.isEmpty_xiuk4h$_0 = true;
    this.min = this.mutMin_0;
    this.max = this.mutMax_0;
    this.size = this.mutSize_0;
    this.center = this.mutCenter_0;
    this.batchUpdate_xnma69$_0 = false;
  }
  Object.defineProperty(BoundingBox.prototype, 'isEmpty', {
    get: function () {
      return this.isEmpty_xiuk4h$_0;
    },
    set: function (isEmpty) {
      this.isEmpty_xiuk4h$_0 = isEmpty;
    }
  });
  Object.defineProperty(BoundingBox.prototype, 'batchUpdate', {
    get: function () {
      return this.batchUpdate_xnma69$_0;
    },
    set: function (value) {
      this.batchUpdate_xnma69$_0 = value;
      this.updateSizeAndCenter_0();
    }
  });
  BoundingBox.prototype.updateSizeAndCenter_0 = function () {
    if (!this.batchUpdate) {
      this.mutMax_0.subtract_2gj7b4$(this.mutMin_0, this.mutSize_0);
      this.size.scale_749b8l$(0.5, this.mutCenter_0).add_czzhiu$(this.min);
    }
  };
  BoundingBox.prototype.addPoint_0 = function (point) {
    if (this.isEmpty) {
      this.mutMin_0.set_czzhiu$(point);
      this.mutMax_0.set_czzhiu$(point);
      this.isEmpty = false;
    }
     else {
      if (point.x < this.min.x) {
        this.mutMin_0.x = point.x;
      }
      if (point.y < this.min.y) {
        this.mutMin_0.y = point.y;
      }
      if (point.z < this.min.z) {
        this.mutMin_0.z = point.z;
      }
      if (point.x > this.max.x) {
        this.mutMax_0.x = point.x;
      }
      if (point.y > this.max.y) {
        this.mutMax_0.y = point.y;
      }
      if (point.z > this.max.z) {
        this.mutMax_0.z = point.z;
      }
    }
  };
  BoundingBox.prototype.isEqual_ea4od8$ = function (other) {
    return this.isEmpty === other.isEmpty && this.min.isEqual_czzhiu$(other.min) && this.max.isEqual_czzhiu$(other.max);
  };
  BoundingBox.prototype.clear = function () {
    this.isEmpty = true;
    this.mutMin_0.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.mutMax_0.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.updateSizeAndCenter_0();
  };
  BoundingBox.prototype.add_czzhiu$ = function (point) {
    this.addPoint_0(point);
    this.updateSizeAndCenter_0();
  };
  BoundingBox.prototype.add_uqk38n$ = function (points) {
    this.add_3y53ni$(points, get_indices(points));
  };
  BoundingBox.prototype.add_3y53ni$ = function (points, range) {
    var tmp$, tmp$_0, tmp$_1;
    tmp$ = range.first;
    tmp$_0 = range.last;
    tmp$_1 = range.step;
    for (var i = tmp$; i <= tmp$_0; i += tmp$_1) {
      this.addPoint_0(points.get_za3lpa$(i));
    }
    this.updateSizeAndCenter_0();
  };
  BoundingBox.prototype.add_ea4od8$ = function (aabb) {
    if (!aabb.isEmpty) {
      this.addPoint_0(aabb.min);
      this.addPoint_0(aabb.max);
      this.updateSizeAndCenter_0();
    }
  };
  BoundingBox.prototype.set_ea4od8$ = function (other) {
    this.mutMin_0.set_czzhiu$(other.min);
    this.mutMax_0.set_czzhiu$(other.max);
    this.mutSize_0.set_czzhiu$(other.size);
    this.mutCenter_0.set_czzhiu$(other.center);
    this.isEmpty = other.isEmpty;
  };
  BoundingBox.prototype.set_4lfkt4$ = function (min, max) {
    this.isEmpty = false;
    this.mutMin_0.set_czzhiu$(min);
    this.mutMax_0.set_czzhiu$(max);
    this.updateSizeAndCenter_0();
  };
  BoundingBox.prototype.set_w8lrqs$ = function (minX, minY, minZ, maxX, maxY, maxZ) {
    this.isEmpty = false;
    this.mutMin_0.set_y2kzbl$(minX, minY, minZ);
    this.mutMax_0.set_y2kzbl$(maxX, maxY, maxZ);
    this.updateSizeAndCenter_0();
  };
  BoundingBox.prototype.isIncluding_czzhiu$ = function (point) {
    return point.x >= this.min.x && point.x <= this.max.x && point.y >= this.min.y && point.y <= this.max.y && point.z >= this.min.z && point.z <= this.max.z;
  };
  BoundingBox.prototype.clampToBounds_5s4mqq$ = function (point) {
    var $receiver = point.x;
    var min = this.min.x;
    var max = this.max.x;
    var clamp$result;
    if ($receiver < min) {
      clamp$result = min;
    }
     else if ($receiver > max) {
      clamp$result = max;
    }
     else {
      clamp$result = $receiver;
    }
    point.x = clamp$result;
    var $receiver_0 = point.y;
    var min_0 = this.min.y;
    var max_0 = this.max.y;
    var clamp$result_0;
    if ($receiver_0 < min_0) {
      clamp$result_0 = min_0;
    }
     else if ($receiver_0 > max_0) {
      clamp$result_0 = max_0;
    }
     else {
      clamp$result_0 = $receiver_0;
    }
    point.y = clamp$result_0;
    var $receiver_1 = point.z;
    var min_1 = this.min.z;
    var max_1 = this.max.z;
    var clamp$result_1;
    if ($receiver_1 < min_1) {
      clamp$result_1 = min_1;
    }
     else if ($receiver_1 > max_1) {
      clamp$result_1 = max_1;
    }
     else {
      clamp$result_1 = $receiver_1;
    }
    point.z = clamp$result_1;
  };
  BoundingBox.prototype.pointDistance_czzhiu$ = function (pt) {
    var x = this.pointDistanceSqr_czzhiu$(pt);
    return Math_0.sqrt(x);
  };
  BoundingBox.prototype.pointDistanceSqr_czzhiu$ = function (pt) {
    if (this.isIncluding_czzhiu$(pt)) {
      return 0.0;
    }
    var x = 0.0;
    var y = 0.0;
    var z = 0.0;
    var tmp = pt.x - this.min.x;
    if (tmp < 0) {
      x = tmp;
    }
     else {
      tmp = this.max.x - pt.x;
      if (tmp < 0) {
        x = tmp;
      }
    }
    tmp = pt.y - this.min.y;
    if (tmp < 0) {
      y = tmp;
    }
     else {
      tmp = this.max.y - pt.y;
      if (tmp < 0) {
        y = tmp;
      }
    }
    tmp = pt.z - this.min.z;
    if (tmp < 0) {
      z = tmp;
    }
     else {
      tmp = this.max.z - pt.z;
      if (tmp < 0) {
        z = tmp;
      }
    }
    return x * x + y * y + z * z;
  };
  BoundingBox.prototype.hitDistanceSqr_nvyeur$ = function (ray) {
    var tmin;
    var tmax;
    var tymin;
    var tymax;
    var tzmin;
    var tzmax;
    if (this.isEmpty) {
      return kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
    }
    if (this.isIncluding_czzhiu$(ray.origin)) {
      return 0.0;
    }
    var div = 1.0 / ray.direction.x;
    if (div >= 0.0) {
      tmin = (this.min.x - ray.origin.x) * div;
      tmax = (this.max.x - ray.origin.x) * div;
    }
     else {
      tmin = (this.max.x - ray.origin.x) * div;
      tmax = (this.min.x - ray.origin.x) * div;
    }
    div = 1.0 / ray.direction.y;
    if (div >= 0.0) {
      tymin = (this.min.y - ray.origin.y) * div;
      tymax = (this.max.y - ray.origin.y) * div;
    }
     else {
      tymin = (this.max.y - ray.origin.y) * div;
      tymax = (this.min.y - ray.origin.y) * div;
    }
    if (tmin > tymax || tymin > tmax) {
      return kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
    }
    if (tymin > tmin) {
      tmin = tymin;
    }
    if (tymax < tmax) {
      tmax = tymax;
    }
    div = 1.0 / ray.direction.z;
    if (div >= 0.0) {
      tzmin = (this.min.z - ray.origin.z) * div;
      tzmax = (this.max.z - ray.origin.z) * div;
    }
     else {
      tzmin = (this.max.z - ray.origin.z) * div;
      tzmax = (this.min.z - ray.origin.z) * div;
    }
    if (tmin > tzmax || tzmin > tmax) {
      return kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
    }
    if (tzmin > tmin) {
      tmin = tzmin;
    }
    if (tmin > 0) {
      var comp = ray.direction.x * tmin;
      var dist = comp * comp;
      comp = ray.direction.y * tmin;
      dist += comp * comp;
      comp = ray.direction.z * tmin;
      dist += comp * comp;
      return dist / ray.direction.sqrLength();
    }
     else {
      return kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY;
    }
  };
  BoundingBox.prototype.toString = function () {
    if (this.isEmpty) {
      return '[empty]';
    }
     else {
      return '[min=' + this.min + ', max=' + this.max + ']';
    }
  };
  BoundingBox.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BoundingBox',
    interfaces: []
  };
  function BoundingBox_init(min, max, $this) {
    $this = $this || Object.create(BoundingBox.prototype);
    BoundingBox.call($this);
    $this.mutMin_0.set_czzhiu$(min);
    $this.mutMax_0.set_czzhiu$(max);
    $this.updateSizeAndCenter_0();
    return $this;
  }
  function Buffer() {
  }
  Buffer.prototype.plusAssign_11rb$ = function (value) {
    this.put_11rb$(value);
  };
  Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Buffer',
    interfaces: []
  };
  function Uint8Buffer() {
  }
  Uint8Buffer.prototype.put_fqrh44$ = function (data) {
    return this.put_mj6st8$(data, 0, data.length);
  };
  Uint8Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Uint8Buffer',
    interfaces: [Buffer]
  };
  function Uint16Buffer() {
  }
  Uint16Buffer.prototype.put_gmedm2$ = function (data) {
    return this.put_359eei$(data, 0, data.length);
  };
  Uint16Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Uint16Buffer',
    interfaces: [Buffer]
  };
  function Uint32Buffer() {
  }
  Uint32Buffer.prototype.put_q5rwfd$ = function (data) {
    return this.put_nd5v6f$(data, 0, data.length);
  };
  Uint32Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Uint32Buffer',
    interfaces: [Buffer]
  };
  function Float32Buffer() {
  }
  Float32Buffer.prototype.put_q3cr5i$ = function (data) {
    return this.put_kgymra$(data, 0, data.length);
  };
  Float32Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Float32Buffer',
    interfaces: [Buffer]
  };
  function Color(r, g, b, a) {
    Color$Companion_getInstance();
    if (a === void 0)
      a = 1.0;
    Vec4f.call(this, r, g, b, a);
  }
  Object.defineProperty(Color.prototype, 'r', {
    get: function () {
      return this.xField;
    }
  });
  Object.defineProperty(Color.prototype, 'g', {
    get: function () {
      return this.yField;
    }
  });
  Object.defineProperty(Color.prototype, 'b', {
    get: function () {
      return this.zField;
    }
  });
  Object.defineProperty(Color.prototype, 'a', {
    get: function () {
      return this.wField;
    }
  });
  Color.prototype.withAlpha_mx4ult$ = function (alpha) {
    return new MutableColor(this.r, this.g, this.b, alpha);
  };
  function Color$Companion() {
    Color$Companion_instance = this;
    this.BLACK = new Color(0.0, 0.0, 0.0, 1.0);
    this.DARK_GRAY = new Color(0.25, 0.25, 0.25, 1.0);
    this.GRAY = new Color(0.5, 0.5, 0.5, 1.0);
    this.LIGHT_GRAY = new Color(0.75, 0.75, 0.75, 1.0);
    this.WHITE = new Color(1.0, 1.0, 1.0, 1.0);
    this.RED = new Color(1.0, 0.0, 0.0, 1.0);
    this.GREEN = new Color(0.0, 1.0, 0.0, 1.0);
    this.BLUE = new Color(0.0, 0.0, 1.0, 1.0);
    this.YELLOW = new Color(1.0, 1.0, 0.0, 1.0);
    this.CYAN = new Color(0.0, 1.0, 1.0, 1.0);
    this.MAGENTA = new Color(1.0, 0.0, 1.0, 1.0);
    this.ORANGE = new Color(1.0, 0.5, 0.0, 1.0);
    this.LIME = new Color(0.7, 1.0, 0.0, 1.0);
    this.LIGHT_RED = new Color(1.0, 0.5, 0.5, 1.0);
    this.LIGHT_GREEN = new Color(0.5, 1.0, 0.5, 1.0);
    this.LIGHT_BLUE = new Color(0.5, 0.5, 1.0, 1.0);
    this.LIGHT_YELLOW = new Color(1.0, 1.0, 0.5, 1.0);
    this.LIGHT_CYAN = new Color(0.5, 1.0, 1.0, 1.0);
    this.LIGHT_MAGENTA = new Color(1.0, 0.5, 1.0, 1.0);
    this.LIGHT_ORANGE = new Color(1.0, 0.75, 0.5, 1.0);
    this.DARK_RED = new Color(0.5, 0.0, 0.0, 1.0);
    this.DARK_GREEN = new Color(0.0, 0.5, 0.0, 1.0);
    this.DARK_BLUE = new Color(0.0, 0.0, 0.5, 1.0);
    this.DARK_YELLOW = new Color(0.5, 0.5, 0.0, 1.0);
    this.DARK_CYAN = new Color(0.0, 0.5, 0.5, 1.0);
    this.DARK_MAGENTA = new Color(0.5, 0.0, 0.5, 1.0);
    this.DARK_ORANGE = new Color(0.5, 0.25, 0.0, 1.0);
  }
  Color$Companion.prototype.fromHsv_7b5o5w$ = function (h, s, v, a) {
    var color = MutableColor_init();
    return color.setHsv_7b5o5w$(h, s, v, a);
  };
  Color$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Color$Companion_instance = null;
  function Color$Companion_getInstance() {
    if (Color$Companion_instance === null) {
      new Color$Companion();
    }
    return Color$Companion_instance;
  }
  Color.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Color',
    interfaces: [Vec4f]
  };
  function MutableColor(r, g, b, a) {
    Color.call(this, r, g, b, a);
  }
  Object.defineProperty(MutableColor.prototype, 'r', {
    get: function () {
      return this.xField;
    },
    set: function (value) {
      this.xField = value;
    }
  });
  Object.defineProperty(MutableColor.prototype, 'g', {
    get: function () {
      return this.yField;
    },
    set: function (value) {
      this.yField = value;
    }
  });
  Object.defineProperty(MutableColor.prototype, 'b', {
    get: function () {
      return this.zField;
    },
    set: function (value) {
      this.zField = value;
    }
  });
  Object.defineProperty(MutableColor.prototype, 'a', {
    get: function () {
      return this.wField;
    },
    set: function (value) {
      this.wField = value;
    }
  });
  MutableColor.prototype.add_d7aj7k$ = function (other) {
    this.r = this.r + other.r;
    this.g = this.g + other.g;
    this.b = this.b + other.b;
    this.a = this.a + other.a;
    return this;
  };
  MutableColor.prototype.add_y83vuj$ = function (other, weight) {
    this.r = this.r + other.r * weight;
    this.g = this.g + other.g * weight;
    this.b = this.b + other.b * weight;
    this.a = this.a + other.a * weight;
    return this;
  };
  MutableColor.prototype.subtract_czzhhz$ = function (other) {
    this.r = this.r - other.x;
    this.g = this.g - other.y;
    this.b = this.b - other.z;
    this.a = this.a - other.w;
    return this;
  };
  MutableColor.prototype.scale_mx4ult$ = function (factor) {
    this.r = this.r * factor;
    this.g = this.g * factor;
    this.b = this.b * factor;
    this.a = this.a * factor;
    return this;
  };
  MutableColor.prototype.clear = function () {
    this.set_7b5o5w$(0.0, 0.0, 0.0, 0.0);
    return this;
  };
  MutableColor.prototype.set_7b5o5w$ = function (r, g, b, a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
    return this;
  };
  MutableColor.prototype.set_d7aj7k$ = function (other) {
    this.r = other.r;
    this.g = other.g;
    this.b = other.b;
    this.a = other.a;
    return this;
  };
  MutableColor.prototype.setHsv_7b5o5w$ = function (h, s, v, a) {
    var hue = h % 360.0;
    if (hue < 0) {
      hue += 360.0;
    }
    var hi = numberToInt(hue / 60.0);
    var f = hue / 60.0 - hi;
    var p = v * (1 - s);
    var q = v * (1 - s * f);
    var t = v * (1 - s * (1 - f));
    switch (hi) {
      case 1:
        this.set_7b5o5w$(q, v, p, a);
        break;
      case 2:
        this.set_7b5o5w$(p, v, t, a);
        break;
      case 3:
        this.set_7b5o5w$(p, q, v, a);
        break;
      case 4:
        this.set_7b5o5w$(t, p, v, a);
        break;
      case 5:
        this.set_7b5o5w$(v, p, q, a);
        break;
      default:this.set_7b5o5w$(v, t, p, a);
        break;
    }
    return this;
  };
  MutableColor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MutableColor',
    interfaces: [Color]
  };
  function MutableColor_init($this) {
    $this = $this || Object.create(MutableColor.prototype);
    MutableColor.call($this, 0.0, 0.0, 0.0, 1.0);
    return $this;
  }
  function MutableColor_init_0(color, $this) {
    $this = $this || Object.create(MutableColor.prototype);
    MutableColor.call($this, color.r, color.g, color.b, color.a);
    return $this;
  }
  function color(hex) {
    if (hex.length === 0) {
      return Color$Companion_getInstance().BLACK;
    }
    var str = hex;
    if (str.charCodeAt(0) === 35) {
      str = str.substring(1);
    }
    var r = 0.0;
    var g = 0.0;
    var b = 0.0;
    var a = 1.0;
    if (str.length === 3) {
      var r4 = toInt(str.substring(0, 1), 16);
      var g4 = toInt(str.substring(1, 2), 16);
      var b4 = toInt(str.substring(2, 3), 16);
      r = (r4 | r4 << 4) / 255.0;
      g = (g4 | g4 << 4) / 255.0;
      b = (b4 | b4 << 4) / 255.0;
    }
     else if (str.length === 4) {
      var r4_0 = toInt(str.substring(0, 1), 16);
      var g4_0 = toInt(str.substring(1, 2), 16);
      var b4_0 = toInt(str.substring(2, 3), 16);
      var a4 = toInt(str.substring(2, 3), 16);
      r = (r4_0 | r4_0 << 4) / 255.0;
      g = (g4_0 | g4_0 << 4) / 255.0;
      b = (b4_0 | b4_0 << 4) / 255.0;
      a = (a4 | a4 << 4) / 255.0;
    }
     else if (str.length === 6) {
      r = toInt(str.substring(0, 2), 16) / 255.0;
      g = toInt(str.substring(2, 4), 16) / 255.0;
      b = toInt(str.substring(4, 6), 16) / 255.0;
    }
     else if (str.length === 8) {
      r = toInt(str.substring(0, 2), 16) / 255.0;
      g = toInt(str.substring(2, 4), 16) / 255.0;
      b = toInt(str.substring(4, 6), 16) / 255.0;
      a = toInt(str.substring(6, 8), 16) / 255.0;
    }
    return new Color(r, g, b, a);
  }
  function ColorGradient(colors) {
    ColorGradient$Companion_getInstance();
    this.gradient_0 = null;
    var len = colors.length;
    var steps = 0;
    var stepsn = new Int32Array(len - 1 | 0);
    for (var i = 1; i < len; i++) {
      var c1 = colors[i - 1 | 0];
      var c2 = colors[i];
      var x = c1.a - c2.a;
      var $receiver = Math_0.abs(x);
      var clamp$result;
      if ($receiver < 0.0) {
        clamp$result = 0.0;
      }
       else if ($receiver > 1.0) {
        clamp$result = 1.0;
      }
       else {
        clamp$result = $receiver;
      }
      var da = round(clamp$result * 255.0);
      var x_0 = c1.r - c2.r;
      var $receiver_0 = Math_0.abs(x_0);
      var clamp$result_0;
      if ($receiver_0 < 0.0) {
        clamp$result_0 = 0.0;
      }
       else if ($receiver_0 > 1.0) {
        clamp$result_0 = 1.0;
      }
       else {
        clamp$result_0 = $receiver_0;
      }
      var dr = round(clamp$result_0 * 255.0);
      var x_1 = c1.g - c2.g;
      var $receiver_1 = Math_0.abs(x_1);
      var clamp$result_1;
      if ($receiver_1 < 0.0) {
        clamp$result_1 = 0.0;
      }
       else if ($receiver_1 > 1.0) {
        clamp$result_1 = 1.0;
      }
       else {
        clamp$result_1 = $receiver_1;
      }
      var dg = round(clamp$result_1 * 255.0);
      var x_2 = c1.b - c2.b;
      var $receiver_2 = Math_0.abs(x_2);
      var clamp$result_2;
      if ($receiver_2 < 0.0) {
        clamp$result_2 = 0.0;
      }
       else if ($receiver_2 > 1.0) {
        clamp$result_2 = 1.0;
      }
       else {
        clamp$result_2 = $receiver_2;
      }
      var db = round(clamp$result_2 * 255.0);
      var m = Math_0.max(da, dr);
      var a = m;
      m = Math_0.max(a, dg);
      var a_0 = m;
      m = Math_0.max(a_0, db);
      steps = steps + numberToInt(m) | 0;
      stepsn[i - 1 | 0] = numberToInt(m);
    }
    var array = Array_0(steps);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i_0 = 0; i_0 <= tmp$; i_0++) {
      array[i_0] = MutableColor_init();
    }
    this.gradient_0 = array;
    var n = 0;
    for (var i_1 = 0; i_1 !== stepsn.length; ++i_1) {
      var $receiver_3 = colors[i_1].a;
      var clamp$result_3;
      if ($receiver_3 < 0.0) {
        clamp$result_3 = 0.0;
      }
       else if ($receiver_3 > 1.0) {
        clamp$result_3 = 1.0;
      }
       else {
        clamp$result_3 = $receiver_3;
      }
      var a0 = round(clamp$result_3 * 255.0);
      var $receiver_4 = colors[i_1].r;
      var clamp$result_4;
      if ($receiver_4 < 0.0) {
        clamp$result_4 = 0.0;
      }
       else if ($receiver_4 > 1.0) {
        clamp$result_4 = 1.0;
      }
       else {
        clamp$result_4 = $receiver_4;
      }
      var r0 = round(clamp$result_4 * 255.0);
      var $receiver_5 = colors[i_1].g;
      var clamp$result_5;
      if ($receiver_5 < 0.0) {
        clamp$result_5 = 0.0;
      }
       else if ($receiver_5 > 1.0) {
        clamp$result_5 = 1.0;
      }
       else {
        clamp$result_5 = $receiver_5;
      }
      var g0 = round(clamp$result_5 * 255.0);
      var $receiver_6 = colors[i_1].b;
      var clamp$result_6;
      if ($receiver_6 < 0.0) {
        clamp$result_6 = 0.0;
      }
       else if ($receiver_6 > 1.0) {
        clamp$result_6 = 1.0;
      }
       else {
        clamp$result_6 = $receiver_6;
      }
      var b0 = round(clamp$result_6 * 255.0);
      var $receiver_7 = colors[i_1 + 1 | 0].a;
      var clamp$result_7;
      if ($receiver_7 < 0.0) {
        clamp$result_7 = 0.0;
      }
       else if ($receiver_7 > 1.0) {
        clamp$result_7 = 1.0;
      }
       else {
        clamp$result_7 = $receiver_7;
      }
      var a1 = round(clamp$result_7 * 255.0);
      var $receiver_8 = colors[i_1 + 1 | 0].r;
      var clamp$result_8;
      if ($receiver_8 < 0.0) {
        clamp$result_8 = 0.0;
      }
       else if ($receiver_8 > 1.0) {
        clamp$result_8 = 1.0;
      }
       else {
        clamp$result_8 = $receiver_8;
      }
      var r1 = round(clamp$result_8 * 255.0);
      var $receiver_9 = colors[i_1 + 1 | 0].g;
      var clamp$result_9;
      if ($receiver_9 < 0.0) {
        clamp$result_9 = 0.0;
      }
       else if ($receiver_9 > 1.0) {
        clamp$result_9 = 1.0;
      }
       else {
        clamp$result_9 = $receiver_9;
      }
      var g1 = round(clamp$result_9 * 255.0);
      var $receiver_10 = colors[i_1 + 1 | 0].b;
      var clamp$result_10;
      if ($receiver_10 < 0.0) {
        clamp$result_10 = 0.0;
      }
       else if ($receiver_10 > 1.0) {
        clamp$result_10 = 1.0;
      }
       else {
        clamp$result_10 = $receiver_10;
      }
      var b1 = round(clamp$result_10 * 255.0);
      var da_0 = (a1 - a0) / stepsn[i_1];
      var dr_0 = (r1 - r0) / stepsn[i_1];
      var dg_0 = (g1 - g0) / stepsn[i_1];
      var db_0 = (b1 - b0) / stepsn[i_1];
      var j = 0;
      while (j < stepsn[i_1]) {
        this.gradient_0[n].a = (a0 + round(da_0 * j)) / 255.0;
        this.gradient_0[n].r = (r0 + round(dr_0 * j)) / 255.0;
        this.gradient_0[n].g = (g0 + round(dg_0 * j)) / 255.0;
        this.gradient_0[n].b = (b0 + round(db_0 * j)) / 255.0;
        j = j + 1 | 0;
        n = n + 1 | 0;
      }
    }
  }
  ColorGradient.prototype.getColor_y2kzbl$ = function (value, min, max) {
    if (min === void 0)
      min = 0.0;
    if (max === void 0)
      max = 1.0;
    var $receiver = numberToInt((value - min) / (max - min) * this.gradient_0.length);
    var max_0 = this.gradient_0.length - 1 | 0;
    var clamp$result;
    if ($receiver < 0) {
      clamp$result = 0;
    }
     else if ($receiver > max_0) {
      clamp$result = max_0;
    }
     else {
      clamp$result = $receiver;
    }
    var i = clamp$result;
    return this.gradient_0[i];
  };
  function ColorGradient$Companion() {
    ColorGradient$Companion_instance = this;
    this.AMPEL = new ColorGradient([Color$Companion_getInstance().RED, Color$Companion_getInstance().YELLOW, Color$Companion_getInstance().GREEN]);
    this.BLUE_WHITE_RED = new ColorGradient([new Color(0.35, 0.0, 0.0, 1.0), new Color(1.0, 0.5, 0.0, 1.0), Color$Companion_getInstance().WHITE, new Color(0.0, 0.5, 1.0, 1.0), new Color(0.0, 45 / 255.0, 120 / 255.0, 1.0)]);
    this.JET = new ColorGradient([Color$Companion_getInstance().BLUE, Color$Companion_getInstance().CYAN, Color$Companion_getInstance().GREEN, Color$Companion_getInstance().YELLOW, Color$Companion_getInstance().RED, Color$Companion_getInstance().MAGENTA]);
    this.PLASMA = new ColorGradient([new Color(0.050383214, 0.029802898, 0.5279749, 1.0), new Color(0.06353636, 0.028425973, 0.5331237, 1.0), new Color(0.07535312, 0.027206372, 0.538007, 1.0), new Color(0.0862218, 0.026125321, 0.5426577, 1.0), new Color(0.09637861, 0.025165098, 0.54710346, 1.0), new Color(0.1059797, 0.024309244, 0.5513679, 1.0), new Color(0.115123644, 0.02355625, 0.5554677, 1.0), new Color(0.1239029, 0.022878101, 0.5594235, 1.0), new Color(0.13238072, 0.022258377, 0.5632501, 1.0), new Color(0.14060308, 0.021686668, 0.5669595, 1.0), new Color(0.14860652, 0.021153588, 0.5705617, 1.0), new Color(0.15642065, 0.020650716, 0.57406545, 1.0), new Color(0.16406973, 0.020170532, 0.57747805, 1.0), new Color(0.17157392, 0.019706342, 0.5808059, 1.0), new Color(0.1789502, 0.019252224, 0.58405423, 1.0), new Color(0.18621296, 0.018802976, 0.58722764, 1.0), new Color(0.19337445, 0.01835406, 0.59032995, 1.0), new Color(0.20044526, 0.017901551, 0.5933643, 1.0), new Color(0.20743455, 0.017442109, 0.5963333, 1.0), new Color(0.2143503, 0.016972927, 0.59923923, 1.0), new Color(0.22119676, 0.016497048, 0.6020833, 1.0), new Color(0.22798297, 0.016007151, 0.6048674, 1.0), new Color(0.23471454, 0.015501507, 0.60759246, 1.0), new Color(0.24139625, 0.0149791045, 0.6102591, 1.0), new Color(0.24803238, 0.014439358, 0.6128678, 1.0), new Color(0.2546267, 0.013882092, 0.61541855, 1.0), new Color(0.26118258, 0.0133075155, 0.6179114, 1.0), new Color(0.267703, 0.012716216, 0.620346, 1.0), new Color(0.27419066, 0.012109143, 0.6227219, 1.0), new Color(0.28064796, 0.011487591, 0.62503844, 1.0), new Color(0.28707606, 0.010855487, 0.62729496, 1.0), new Color(0.29347768, 0.010212885, 0.6294905, 1.0), new Color(0.2998551, 0.0095607955, 0.6316239, 1.0), new Color(0.30620983, 0.008901853, 0.6336941, 1.0), new Color(0.31254312, 0.008239007, 0.63569975, 1.0), new Color(0.31885618, 0.0075755105, 0.6376395, 1.0), new Color(0.32515, 0.0069149174, 0.639512, 1.0), new Color(0.33142555, 0.006261074, 0.64131564, 1.0), new Color(0.33768344, 0.0056183087, 0.64304894, 1.0), new Color(0.34392458, 0.0049905307, 0.6447102, 1.0), new Color(0.3501497, 0.0043820255, 0.6462977, 1.0), new Color(0.3563592, 0.0037978175, 0.64780974, 1.0), new Color(0.36255348, 0.0032431958, 0.64924467, 1.0), new Color(0.36873275, 0.0027237071, 0.65060055, 1.0), new Color(0.37489727, 0.0022451489, 0.65187573, 1.0), new Color(0.38104713, 0.001813562, 0.6530685, 1.0), new Color(0.38718265, 0.0014344692, 0.6541768, 1.0), new Color(0.39330402, 0.0011138826, 0.65519875, 1.0), new Color(0.3994108, 8.594208E-4, 0.6561328, 1.0), new Color(0.40550292, 6.7809154E-4, 0.6569773, 1.0), new Color(0.4115801, 5.771017E-4, 0.6577304, 1.0), new Color(0.41764206, 5.638475E-4, 0.65839046, 1.0), new Color(0.42368856, 6.4590276E-4, 0.658956, 1.0), new Color(0.42971918, 8.310082E-4, 0.6594254, 1.0), new Color(0.4357336, 0.0011270588, 0.6597971, 1.0), new Color(0.4417321, 0.0015398478, 0.660069, 1.0), new Color(0.4477136, 0.0020795474, 0.66024035, 1.0), new Color(0.4536774, 0.002754703, 0.66031, 1.0), new Color(0.45962295, 0.003573744, 0.66027665, 1.0), new Color(0.46554962, 0.004545181, 0.6601394, 1.0), new Color(0.47145686, 0.005677588, 0.6598972, 1.0), new Color(0.47734392, 0.0069795875, 0.6595493, 1.0), new Color(0.4832102, 0.008459835, 0.659095, 1.0), new Color(0.48905495, 0.010127, 0.6585337, 1.0), new Color(0.49487746, 0.011989749, 0.6578649, 1.0), new Color(0.5006777, 0.014055064, 0.65708756, 1.0), new Color(0.50645417, 0.016333343, 0.6562023, 1.0), new Color(0.512206, 0.018833224, 0.65520924, 1.0), new Color(0.5179326, 0.021563191, 0.6541085, 1.0), new Color(0.523633, 0.024531648, 0.65290064, 1.0), new Color(0.5293065, 0.027746873, 0.651586, 1.0), new Color(0.5349522, 0.03121703, 0.6501654, 1.0), new Color(0.5405695, 0.03495013, 0.6486397, 1.0), new Color(0.5461575, 0.038954034, 0.6470099, 1.0), new Color(0.55171543, 0.04313648, 0.64527726, 1.0), new Color(0.5572425, 0.04733076, 0.6434432, 1.0), new Color(0.5627381, 0.051544808, 0.6415094, 1.0), new Color(0.56820136, 0.05577767, 0.63947743, 1.0), new Color(0.5736319, 0.060028136, 0.63734883, 1.0), new Color(0.57902867, 0.06429555, 0.6351261, 1.0), new Color(0.5843911, 0.068579026, 0.6328116, 1.0), new Color(0.5897186, 0.072877586, 0.63040775, 1.0), new Color(0.5950105, 0.07719029, 0.627917, 1.0), new Color(0.6002663, 0.08151619, 0.6253421, 1.0), new Color(0.60548544, 0.085854374, 0.62268573, 1.0), new Color(0.61066747, 0.09020393, 0.61995083, 1.0), new Color(0.615812, 0.09456398, 0.61714035, 1.0), new Color(0.6209186, 0.098933674, 0.61425745, 1.0), new Color(0.6259869, 0.10331216, 0.6113052, 1.0), new Color(0.6310166, 0.10769864, 0.6082868, 1.0), new Color(0.63600755, 0.11209234, 0.6052055, 1.0), new Color(0.64095944, 0.116492495, 0.6020646, 1.0), new Color(0.6458722, 0.1208984, 0.5988674, 1.0), new Color(0.6507456, 0.12530938, 0.5956173, 1.0), new Color(0.6555796, 0.12972479, 0.5923175, 1.0), new Color(0.6603743, 0.134144, 0.5889713, 1.0), new Color(0.6651295, 0.13856643, 0.5855823, 1.0), new Color(0.6698454, 0.14299154, 0.58215356, 1.0), new Color(0.67452204, 0.14741884, 0.57868826, 1.0), new Color(0.67915964, 0.15184785, 0.5751894, 1.0), new Color(0.6837584, 0.15627816, 0.57166016, 1.0), new Color(0.68831843, 0.16070938, 0.5681034, 1.0), new Color(0.6928401, 0.16514118, 0.56452197, 1.0), new Color(0.6973236, 0.16957322, 0.5609187, 1.0), new Color(0.70176935, 0.17400524, 0.55729616, 1.0), new Color(0.7061776, 0.178437, 0.553657, 1.0), new Color(0.71054876, 0.1828683, 0.5500036, 1.0), new Color(0.7148832, 0.18729898, 0.5463383, 1.0), new Color(0.71918136, 0.1917289, 0.54266334, 1.0), new Color(0.7234436, 0.19615796, 0.5389808, 1.0), new Color(0.72767043, 0.20058608, 0.5352926, 1.0), new Color(0.73186225, 0.20501317, 0.531601, 1.0), new Color(0.73601943, 0.20943907, 0.52790844, 1.0), new Color(0.7401426, 0.21386397, 0.5242155, 1.0), new Color(0.7442321, 0.2182879, 0.5205238, 1.0), new Color(0.7482885, 0.22271094, 0.5168345, 1.0), new Color(0.7523123, 0.22713318, 0.51314896, 1.0), new Color(0.75630397, 0.23155475, 0.5094683, 1.0), new Color(0.76026386, 0.23597577, 0.5057936, 1.0), new Color(0.7641925, 0.2403964, 0.5021256, 1.0), new Color(0.76809037, 0.24481681, 0.4984653, 1.0), new Color(0.77195793, 0.24923722, 0.49481335, 1.0), new Color(0.7757955, 0.2536578, 0.49117053, 1.0), new Color(0.7796036, 0.2580784, 0.4875391, 1.0), new Color(0.78338265, 0.26249966, 0.48391774, 1.0), new Color(0.787133, 0.26692185, 0.4803067, 1.0), new Color(0.790855, 0.27134526, 0.47670633, 1.0), new Color(0.7945491, 0.2757702, 0.4731168, 1.0), new Color(0.79821557, 0.2801969, 0.46953827, 1.0), new Color(0.8018547, 0.28462574, 0.46597087, 1.0), new Color(0.80546695, 0.28905705, 0.4624146, 1.0), new Color(0.8090524, 0.29349113, 0.45886958, 1.0), new Color(0.8126115, 0.29792786, 0.45533755, 1.0), new Color(0.8161444, 0.30236813, 0.45181638, 1.0), new Color(0.81965125, 0.3068123, 0.44830588, 1.0), new Color(0.82313234, 0.3112607, 0.44480577, 1.0), new Color(0.8265877, 0.3157138, 0.4413159, 1.0), new Color(0.83001757, 0.32017192, 0.43783596, 1.0), new Color(0.83342206, 0.3246355, 0.43436563, 1.0), new Color(0.83680123, 0.32910484, 0.43090504, 1.0), new Color(0.8401553, 0.3335801, 0.42745483, 1.0), new Color(0.8434841, 0.3380621, 0.42401305, 1.0), new Color(0.84678775, 0.34255126, 0.42057934, 1.0), new Color(0.8500661, 0.347048, 0.41715327, 1.0), new Color(0.8533193, 0.3515528, 0.41373444, 1.0), new Color(0.8565471, 0.35606608, 0.41032246, 1.0), new Color(0.8597495, 0.36058822, 0.40691698, 1.0), new Color(0.86292654, 0.3651194, 0.4035188, 1.0), new Color(0.8660779, 0.36966044, 0.40012604, 1.0), new Color(0.86920345, 0.3742118, 0.3967382, 1.0), new Color(0.8723029, 0.3787739, 0.39335495, 1.0), new Color(0.87537616, 0.38334724, 0.38997585, 1.0), new Color(0.8784229, 0.38793224, 0.38660046, 1.0), new Color(0.8814429, 0.39252934, 0.38322863, 1.0), new Color(0.884436, 0.39713886, 0.37986025, 1.0), new Color(0.8874017, 0.4017615, 0.37649423, 1.0), new Color(0.8903397, 0.4063977, 0.37313023, 1.0), new Color(0.89324963, 0.41104788, 0.3697679, 1.0), new Color(0.8961312, 0.41571248, 0.36640692, 1.0), new Color(0.89898396, 0.42039198, 0.36304697, 1.0), new Color(0.9018074, 0.4250868, 0.35968775, 1.0), new Color(0.9046013, 0.42979744, 0.3563288, 1.0), new Color(0.907365, 0.43452433, 0.35296977, 1.0), new Color(0.9100981, 0.4392679, 0.34961048, 1.0), new Color(0.9128001, 0.4440286, 0.34625065, 1.0), new Color(0.91547054, 0.44880673, 0.34289014, 1.0), new Color(0.9181088, 0.45360282, 0.33952877, 1.0), new Color(0.9207144, 0.45841742, 0.33616558, 1.0), new Color(0.9232867, 0.46325082, 0.33280084, 1.0), new Color(0.9258251, 0.46810338, 0.3294345, 1.0), new Color(0.9283293, 0.47297546, 0.32606655, 1.0), new Color(0.9307985, 0.47786742, 0.32269686, 1.0), new Color(0.9332321, 0.4827796, 0.31932545, 1.0), new Color(0.93562967, 0.48771235, 0.3159522, 1.0), new Color(0.93799, 0.49266654, 0.31257543, 1.0), new Color(0.9403129, 0.49764204, 0.30919662, 1.0), new Color(0.94259775, 0.5026392, 0.30581582, 1.0), new Color(0.9448439, 0.5076582, 0.3024331, 1.0), new Color(0.9470507, 0.51269937, 0.29904854, 1.0), new Color(0.94921744, 0.5177631, 0.2956623, 1.0), new Color(0.95134354, 0.5228495, 0.2922745, 1.0), new Color(0.95342773, 0.5279595, 0.28888345, 1.0), new Color(0.95546967, 0.5330931, 0.2854904, 1.0), new Color(0.95746875, 0.53825015, 0.28209615, 1.0), new Color(0.95942444, 0.54343104, 0.27870098, 1.0), new Color(0.96133596, 0.5486359, 0.2753052, 1.0), new Color(0.9632026, 0.55386496, 0.27190915, 1.0), new Color(0.96502364, 0.55911833, 0.2685132, 1.0), new Color(0.9667985, 0.5643963, 0.26511776, 1.0), new Color(0.96852565, 0.56969965, 0.2617215, 1.0), new Color(0.9702046, 0.57502824, 0.25832543, 1.0), new Color(0.971835, 0.580382, 0.25493124, 1.0), new Color(0.97341615, 0.585761, 0.25153962, 1.0), new Color(0.9749473, 0.5911654, 0.2481512, 1.0), new Color(0.9764276, 0.5965953, 0.24476677, 1.0), new Color(0.9778564, 0.6020508, 0.24138719, 1.0), new Color(0.9792329, 0.6075321, 0.23801336, 1.0), new Color(0.98055637, 0.6130392, 0.23464632, 1.0), new Color(0.9818259, 0.61857224, 0.23128718, 1.0), new Color(0.98304075, 0.6241314, 0.22793715, 1.0), new Color(0.9841989, 0.6297175, 0.22459501, 1.0), new Color(0.9853008, 0.6353299, 0.22126488, 1.0), new Color(0.9863454, 0.6409685, 0.21794845, 1.0), new Color(0.98733205, 0.64663345, 0.21464753, 1.0), new Color(0.98825985, 0.65232486, 0.21136412, 1.0), new Color(0.9891279, 0.6580426, 0.20810042, 1.0), new Color(0.98993534, 0.6637869, 0.20485885, 1.0), new Color(0.9906813, 0.6695577, 0.20164205, 1.0), new Color(0.9913648, 0.6753551, 0.1984529, 1.0), new Color(0.99198496, 0.68117905, 0.19529457, 1.0), new Color(0.99254096, 0.68702954, 0.1921705, 1.0), new Color(0.9930317, 0.69290674, 0.18908446, 1.0), new Color(0.9934563, 0.69881046, 0.18604054, 1.0), new Color(0.9938138, 0.7047409, 0.18304318, 1.0), new Color(0.99410325, 0.7106978, 0.1800972, 1.0), new Color(0.9943236, 0.71668136, 0.17720783, 1.0), new Color(0.99447393, 0.72269136, 0.17438066, 1.0), new Color(0.99455327, 0.7287279, 0.17162174, 1.0), new Color(0.9945606, 0.7347908, 0.16893752, 1.0), new Color(0.994495, 0.74088, 0.16633491, 1.0), new Color(0.99435544, 0.74699545, 0.16382125, 1.0), new Color(0.994141, 0.75313693, 0.16140422, 1.0), new Color(0.99385077, 0.7593044, 0.15909198, 1.0), new Color(0.9934822, 0.7654986, 0.15689063, 1.0), new Color(0.99303323, 0.7717198, 0.15480758, 1.0), new Color(0.9925052, 0.7779668, 0.15285486, 1.0), new Color(0.9918973, 0.7842391, 0.15104158, 1.0), new Color(0.9912087, 0.7905366, 0.14937688, 1.0), new Color(0.9904388, 0.7968588, 0.14786981, 1.0), new Color(0.98958707, 0.8032053, 0.14652912, 1.0), new Color(0.98864776, 0.8095786, 0.14535728, 1.0), new Color(0.98762053, 0.81597793, 0.14436264, 1.0), new Color(0.9865094, 0.8224006, 0.14355668, 1.0), new Color(0.9853142, 0.828846, 0.14294511, 1.0), new Color(0.98403114, 0.83531535, 0.14252838, 1.0), new Color(0.98265284, 0.8418117, 0.14230265, 1.0), new Color(0.9811904, 0.8483289, 0.14227861, 1.0), new Color(0.97964364, 0.85486645, 0.14245343, 1.0), new Color(0.9779949, 0.8614323, 0.14280818, 1.0), new Color(0.97626495, 0.868016, 0.14335094, 1.0), new Color(0.974443, 0.87462217, 0.14406116, 1.0), new Color(0.97253, 0.8812501, 0.14492291, 1.0), new Color(0.97053295, 0.8878961, 0.14591867, 1.0), new Color(0.96844345, 0.894564, 0.14701444, 1.0), new Color(0.9662712, 0.90124935, 0.14817964, 1.0), new Color(0.9640211, 0.9079504, 0.14937043, 1.0), new Color(0.9616815, 0.9146725, 0.15052034, 1.0), new Color(0.95927566, 0.9214065, 0.15156601, 1.0), new Color(0.9568081, 0.9281521, 0.1524095, 1.0), new Color(0.9542868, 0.93490773, 0.15292116, 1.0), new Color(0.9517261, 0.9416706, 0.15292536, 1.0), new Color(0.94915056, 0.9484349, 0.1521776, 1.0), new Color(0.9466023, 0.9551899, 0.15032795, 1.0), new Color(0.94415176, 0.9619165, 0.1468608, 1.0), new Color(0.94189614, 0.96858984, 0.14095561, 1.0), new Color(0.9400151, 0.97515833, 0.13132551, 1.0)]);
    this.VIRIDIS = new ColorGradient([new Color(0.267004, 0.00487433, 0.3294152, 1.0), new Color(0.2685105, 0.00960483, 0.3354265, 1.0), new Color(0.26994383, 0.01462494, 0.34137896, 1.0), new Color(0.27130488, 0.01994186, 0.3472686, 1.0), new Color(0.27259383, 0.02556309, 0.35309303, 1.0), new Color(0.27380934, 0.03149748, 0.35885257, 1.0), new Color(0.2749524, 0.03775181, 0.36454323, 1.0), new Color(0.27602237, 0.04416723, 0.3701642, 1.0), new Color(0.2770184, 0.05034437, 0.3757145, 1.0), new Color(0.27794144, 0.05632444, 0.38119075, 1.0), new Color(0.27879068, 0.06214536, 0.38659203, 1.0), new Color(0.2795655, 0.06783587, 0.39191723, 1.0), new Color(0.28026658, 0.07341724, 0.39716348, 1.0), new Color(0.2808936, 0.07890703, 0.40232944, 1.0), new Color(0.2814458, 0.0843197, 0.40741405, 1.0), new Color(0.2819236, 0.08966622, 0.4124152, 1.0), new Color(0.28232738, 0.09495545, 0.41733086, 1.0), new Color(0.28265634, 0.10019576, 0.42216033, 1.0), new Color(0.2829105, 0.10539345, 0.42690203, 1.0), new Color(0.28309095, 0.11055307, 0.43155375, 1.0), new Color(0.28319705, 0.11567966, 0.43611482, 1.0), new Color(0.2832288, 0.12077701, 0.44058403, 1.0), new Color(0.28318685, 0.125848, 0.44496, 1.0), new Color(0.283072, 0.13089477, 0.44924128, 1.0), new Color(0.28288388, 0.13592005, 0.45342734, 1.0), new Color(0.28262296, 0.14092556, 0.45751727, 1.0), new Color(0.28229037, 0.14591233, 0.46150994, 1.0), new Color(0.28188676, 0.15088147, 0.46540475, 1.0), new Color(0.28141227, 0.15583424, 0.46920127, 1.0), new Color(0.28086773, 0.16077133, 0.47289908, 1.0), new Color(0.2802547, 0.16569272, 0.47649762, 1.0), new Color(0.27957398, 0.17059883, 0.47999674, 1.0), new Color(0.27882618, 0.1754902, 0.48339653, 1.0), new Color(0.27801237, 0.18036684, 0.48669702, 1.0), new Color(0.27713436, 0.18522836, 0.48989832, 1.0), new Color(0.27619377, 0.19007447, 0.49300075, 1.0), new Color(0.27519116, 0.1949054, 0.49600488, 1.0), new Color(0.27412802, 0.19972086, 0.49891132, 1.0), new Color(0.27300596, 0.2045205, 0.5017208, 1.0), new Color(0.27182811, 0.20930307, 0.5044341, 1.0), new Color(0.27059472, 0.214069, 0.5070524, 1.0), new Color(0.26930755, 0.21881782, 0.5095768, 1.0), new Color(0.26796845, 0.22354911, 0.5120084, 1.0), new Color(0.26657984, 0.2282621, 0.5143487, 1.0), new Color(0.2651445, 0.23295593, 0.5165993, 1.0), new Color(0.2636632, 0.23763078, 0.51876163, 1.0), new Color(0.262138, 0.24228619, 0.52083737, 1.0), new Color(0.26057103, 0.2469217, 0.5228282, 1.0), new Color(0.2589645, 0.25153685, 0.5247361, 1.0), new Color(0.25732243, 0.2561304, 0.52656335, 1.0), new Color(0.2556452, 0.26070285, 0.5283115, 1.0), new Color(0.25393498, 0.26525384, 0.52998275, 1.0), new Color(0.25219405, 0.26978305, 0.5315791, 1.0), new Color(0.25042462, 0.27429023, 0.53310263, 1.0), new Color(0.24862899, 0.2787751, 0.5345556, 1.0), new Color(0.2468114, 0.28323662, 0.53594095, 1.0), new Color(0.24497208, 0.28767547, 0.5372602, 1.0), new Color(0.24311323, 0.29209155, 0.5385156, 1.0), new Color(0.24123707, 0.2964847, 0.53970945, 1.0), new Color(0.23934574, 0.30085495, 0.54084396, 1.0), new Color(0.23744138, 0.30520222, 0.5419214, 1.0), new Color(0.23552606, 0.30952656, 0.54294395, 1.0), new Color(0.23360278, 0.31382772, 0.54391426, 1.0), new Color(0.2316735, 0.3181058, 0.54483443, 1.0), new Color(0.22973926, 0.32236126, 0.54570633, 1.0), new Color(0.22780192, 0.32659432, 0.546532, 1.0), new Color(0.2258633, 0.33080515, 0.5473135, 1.0), new Color(0.22392514, 0.334994, 0.5480529, 1.0), new Color(0.22198915, 0.33916113, 0.5487521, 1.0), new Color(0.2200569, 0.34330687, 0.549413, 1.0), new Color(0.21812995, 0.34743154, 0.55003756, 1.0), new Color(0.21620971, 0.35153547, 0.5506274, 1.0), new Color(0.21429756, 0.35561907, 0.5511844, 1.0), new Color(0.21239477, 0.35968274, 0.5517101, 1.0), new Color(0.2105031, 0.3637267, 0.55220646, 1.0), new Color(0.20862342, 0.3677515, 0.5526749, 1.0), new Color(0.20675628, 0.37175775, 0.5531165, 1.0), new Color(0.20490257, 0.3757459, 0.55353284, 1.0), new Color(0.20306309, 0.37971643, 0.55392504, 1.0), new Color(0.20123854, 0.38366988, 0.5542944, 1.0), new Color(0.1994295, 0.38760677, 0.554642, 1.0), new Color(0.1976365, 0.39152762, 0.5549691, 1.0), new Color(0.19585992, 0.39543298, 0.5552764, 1.0), new Color(0.1941001, 0.39932337, 0.55556494, 1.0), new Color(0.1923572, 0.40319934, 0.5558356, 1.0), new Color(0.19063134, 0.4070615, 0.55608904, 1.0), new Color(0.18892258, 0.41091034, 0.55632603, 1.0), new Color(0.18723083, 0.41474646, 0.55654716, 1.0), new Color(0.18555593, 0.4185704, 0.5567529, 1.0), new Color(0.18389763, 0.42238274, 0.5569438, 1.0), new Color(0.18225561, 0.42618406, 0.5571201, 1.0), new Color(0.18062949, 0.42997485, 0.5572822, 1.0), new Color(0.1790188, 0.43375573, 0.5574303, 1.0), new Color(0.17742299, 0.4375272, 0.5575647, 1.0), new Color(0.17584148, 0.4412898, 0.55768526, 1.0), new Color(0.17427363, 0.4450441, 0.5577922, 1.0), new Color(0.17271876, 0.4487906, 0.55788535, 1.0), new Color(0.17117615, 0.4525298, 0.5579646, 1.0), new Color(0.16964573, 0.45626208, 0.55803037, 1.0), new Color(0.1681264, 0.45998803, 0.558082, 1.0), new Color(0.1666171, 0.46370813, 0.5581191, 1.0), new Color(0.16511703, 0.4674229, 0.5581414, 1.0), new Color(0.16362543, 0.47113279, 0.55814844, 1.0), new Color(0.16214155, 0.4748382, 0.5581397, 1.0), new Color(0.16066466, 0.47853962, 0.55811465, 1.0), new Color(0.15919413, 0.4822374, 0.5580728, 1.0), new Color(0.15772933, 0.48593196, 0.5580135, 1.0), new Color(0.15626973, 0.4896237, 0.557936, 1.0), new Color(0.15481488, 0.49331293, 0.5578397, 1.0), new Color(0.15336445, 0.49700004, 0.5577237, 1.0), new Color(0.1519182, 0.5006853, 0.5575873, 1.0), new Color(0.15047605, 0.504369, 0.5574297, 1.0), new Color(0.14903918, 0.50805134, 0.5572505, 1.0), new Color(0.14760731, 0.51173264, 0.5570486, 1.0), new Color(0.14618026, 0.51541317, 0.5568227, 1.0), new Color(0.14475863, 0.5190932, 0.5565718, 1.0), new Color(0.14334327, 0.5227729, 0.5562949, 1.0), new Color(0.14193527, 0.52645254, 0.555991, 1.0), new Color(0.140536, 0.5301322, 0.55565894, 1.0), new Color(0.13914707, 0.533812, 0.55529773, 1.0), new Color(0.13777047, 0.53749216, 0.55490625, 1.0), new Color(0.1364085, 0.5411726, 0.5544834, 1.0), new Color(0.13506562, 0.5448533, 0.55402905, 1.0), new Color(0.13374299, 0.5485346, 0.55354106, 1.0), new Color(0.13244401, 0.55221635, 0.5530183, 1.0), new Color(0.1311725, 0.5558987, 0.5524595, 1.0), new Color(0.1299327, 0.55958164, 0.55186355, 1.0), new Color(0.12872937, 0.563265, 0.5512293, 1.0), new Color(0.12756771, 0.5669489, 0.5505555, 1.0), new Color(0.12645338, 0.5706332, 0.5498411, 1.0), new Color(0.12539382, 0.5743175, 0.5490856, 1.0), new Color(0.12439474, 0.57800204, 0.5482874, 1.0), new Color(0.12346281, 0.5816866, 0.547445, 1.0), new Color(0.12260562, 0.5853711, 0.54655725, 1.0), new Color(0.12183122, 0.5890552, 0.545623, 1.0), new Color(0.12114807, 0.59273887, 0.54464114, 1.0), new Color(0.12056501, 0.5964219, 0.5436106, 1.0), new Color(0.12009154, 0.60010386, 0.5425304, 1.0), new Color(0.11973756, 0.60378456, 0.5414, 1.0), new Color(0.11951163, 0.6074639, 0.5402175, 1.0), new Color(0.11942341, 0.61114144, 0.5389819, 1.0), new Color(0.11948255, 0.614817, 0.5376922, 1.0), new Color(0.11969858, 0.6184903, 0.5363473, 1.0), new Color(0.12008079, 0.6221608, 0.5349463, 1.0), new Color(0.12063824, 0.6258283, 0.53348833, 1.0), new Color(0.12137972, 0.6294924, 0.53197277, 1.0), new Color(0.12231244, 0.6331528, 0.5303981, 1.0), new Color(0.12344358, 0.636809, 0.5287634, 1.0), new Color(0.12477953, 0.64046067, 0.5270679, 1.0), new Color(0.12632582, 0.64410746, 0.5253107, 1.0), new Color(0.12808703, 0.6477488, 0.5234909, 1.0), new Color(0.13006689, 0.65138435, 0.52160794, 1.0), new Color(0.13226797, 0.6550136, 0.51966083, 1.0), new Color(0.13469183, 0.6586362, 0.5176488, 1.0), new Color(0.1373392, 0.6622516, 0.515571, 1.0), new Color(0.14020991, 0.6658593, 0.5134268, 1.0), new Color(0.1433029, 0.6694588, 0.5112155, 1.0), new Color(0.1466164, 0.6730497, 0.50893646, 1.0), new Color(0.15014783, 0.6766314, 0.5065889, 1.0), new Color(0.15389405, 0.68020344, 0.50417215, 1.0), new Color(0.15785146, 0.68376523, 0.50168574, 1.0), new Color(0.16201597, 0.6873163, 0.49912906, 1.0), new Color(0.1663832, 0.6908561, 0.49650162, 1.0), new Color(0.1709484, 0.69438404, 0.49380293, 1.0), new Color(0.17570671, 0.6978996, 0.4910325, 1.0), new Color(0.18065314, 0.70140225, 0.48818937, 1.0), new Color(0.18578266, 0.7048913, 0.48527327, 1.0), new Color(0.19109018, 0.70836633, 0.48228395, 1.0), new Color(0.19657063, 0.7118267, 0.47922108, 1.0), new Color(0.20221902, 0.7152718, 0.47608432, 1.0), new Color(0.20803045, 0.71870095, 0.4728733, 1.0), new Color(0.21400015, 0.7221137, 0.46958774, 1.0), new Color(0.22012381, 0.72550946, 0.46622637, 1.0), new Color(0.2263969, 0.72888756, 0.46278933, 1.0), new Color(0.23281498, 0.73224735, 0.45927674, 1.0), new Color(0.2393739, 0.73558825, 0.4556884, 1.0), new Color(0.24606968, 0.7389097, 0.45202404, 1.0), new Color(0.2528985, 0.74221104, 0.44828355, 1.0), new Color(0.25985676, 0.7454916, 0.44446674, 1.0), new Color(0.26694128, 0.74875087, 0.44057283, 1.0), new Color(0.2741492, 0.75198805, 0.4366009, 1.0), new Color(0.2814768, 0.75520265, 0.43255207, 1.0), new Color(0.28892103, 0.758394, 0.42842627, 1.0), new Color(0.296479, 0.7615614, 0.42422342, 1.0), new Color(0.30414796, 0.76470435, 0.41994345, 1.0), new Color(0.31192535, 0.7678221, 0.41558638, 1.0), new Color(0.3198086, 0.770914, 0.41115215, 1.0), new Color(0.3277958, 0.77397954, 0.4066401, 1.0), new Color(0.33588538, 0.7770179, 0.40204918, 1.0), new Color(0.3440741, 0.7800285, 0.39738104, 1.0), new Color(0.35235986, 0.78301084, 0.3926358, 1.0), new Color(0.36074054, 0.7859642, 0.38781354, 1.0), new Color(0.3692142, 0.7888879, 0.3829144, 1.0), new Color(0.37777892, 0.7917815, 0.3779385, 1.0), new Color(0.38643283, 0.7946442, 0.37288606, 1.0), new Color(0.3951741, 0.7974754, 0.36775726, 1.0), new Color(0.404001, 0.8002746, 0.36255223, 1.0), new Color(0.4129135, 0.803041, 0.35726893, 1.0), new Color(0.42190814, 0.8057741, 0.35191008, 1.0), new Color(0.43098316, 0.8084734, 0.34647608, 1.0), new Color(0.4401369, 0.81113833, 0.3409673, 1.0), new Color(0.44936764, 0.8137683, 0.33538425, 1.0), new Color(0.45867363, 0.81636286, 0.3297275, 1.0), new Color(0.46805313, 0.81892145, 0.32399762, 1.0), new Color(0.47750446, 0.8214435, 0.31819528, 1.0), new Color(0.4870258, 0.8239286, 0.31232134, 1.0), new Color(0.49661535, 0.8263763, 0.3063766, 1.0), new Color(0.5062713, 0.8287862, 0.3003621, 1.0), new Color(0.5159918, 0.83115786, 0.2942789, 1.0), new Color(0.5257762, 0.83349067, 0.2881265, 1.0), new Color(0.5356211, 0.8357845, 0.28190833, 1.0), new Color(0.5455244, 0.83803916, 0.27562603, 1.0), new Color(0.555484, 0.84025437, 0.26928148, 1.0), new Color(0.5654976, 0.8424299, 0.26287684, 1.0), new Color(0.57556295, 0.84456563, 0.25641456, 1.0), new Color(0.58567774, 0.8466614, 0.24989748, 1.0), new Color(0.5958393, 0.8487172, 0.24332878, 1.0), new Color(0.6060453, 0.8507331, 0.23671214, 1.0), new Color(0.61629283, 0.8527091, 0.23005179, 1.0), new Color(0.6265792, 0.85464543, 0.22335258, 1.0), new Color(0.63690156, 0.8565423, 0.21662012, 1.0), new Color(0.64725685, 0.8583999, 0.20986086, 1.0), new Color(0.65764195, 0.86021876, 0.2030823, 1.0), new Color(0.6680537, 0.86199933, 0.19629307, 1.0), new Color(0.6784887, 0.8637421, 0.18950327, 1.0), new Color(0.6889435, 0.8654478, 0.18272455, 1.0), new Color(0.6994146, 0.8671171, 0.17597055, 1.0), new Color(0.7098984, 0.86875093, 0.16925712, 1.0), new Color(0.72039115, 0.8703501, 0.16260272, 1.0), new Color(0.730889, 0.8719158, 0.15602894, 1.0), new Color(0.741388, 0.8734492, 0.149561, 1.0), new Color(0.75188416, 0.8749514, 0.14322828, 1.0), new Color(0.76237345, 0.8764239, 0.13706449, 1.0), new Color(0.7728518, 0.87786806, 0.13110864, 1.0), new Color(0.78331536, 0.87928545, 0.12540539, 1.0), new Color(0.79375994, 0.88067764, 0.12000532, 1.0), new Color(0.8041816, 0.88204634, 0.11496505, 1.0), new Color(0.8145763, 0.8833933, 0.11034678, 1.0), new Color(0.82494026, 0.8847204, 0.10621724, 1.0), new Color(0.8352696, 0.8860294, 0.1026459, 1.0), new Color(0.84556055, 0.8873224, 0.09970219, 1.0), new Color(0.8558096, 0.88860136, 0.09745186, 1.0), new Color(0.8660132, 0.88986814, 0.09595277, 1.0), new Color(0.87616825, 0.89112484, 0.09525046, 1.0), new Color(0.8862715, 0.8923735, 0.09537439, 1.0), new Color(0.89632004, 0.89361614, 0.09633538, 1.0), new Color(0.9063112, 0.89485466, 0.09812496, 1.0), new Color(0.9162421, 0.8960913, 0.1007168, 1.0), new Color(0.9261058, 0.89732975, 0.10407067, 1.0), new Color(0.93590444, 0.8985704, 0.10813094, 1.0), new Color(0.9456363, 0.899815, 0.11283773, 1.0), new Color(0.95529974, 0.90106535, 0.11812832, 1.0), new Color(0.9648935, 0.9023231, 0.12394051, 1.0), new Color(0.9744167, 0.9035899, 0.13021494, 1.0), new Color(0.9838683, 0.90486723, 0.13689671, 1.0), new Color(0.99324787, 0.9061566, 0.1439362, 1.0)]);
  }
  ColorGradient$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ColorGradient$Companion_instance = null;
  function ColorGradient$Companion_getInstance() {
    if (ColorGradient$Companion_instance === null) {
      new ColorGradient$Companion();
    }
    return ColorGradient$Companion_instance;
  }
  ColorGradient.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorGradient',
    interfaces: []
  };
  function debugOverlay$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function debugOverlay$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(debugOverlay$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(getCallableRef('SimpleComponentUi', function (component) {
      return new SimpleComponentUi(component);
    }));
    $receiver.standardFont_ttufcy$(new FontProps(Font$Companion_getInstance().SYSTEM_FONT, 12.0));
    return Unit;
  }
  function debugOverlay$lambda$lambda$lambda$lambda(this$) {
    return function ($receiver, c) {
      this$.text = numberToInt(c.fps).toString() + '.' + numberToInt(c.fps * 10.0) % 10 + ' fps';
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_0(closure$ctx) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(-37.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(37.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$CENTER_getInstance(), Alignment$CENTER_getInstance());
      $receiver.text = '';
      $receiver.font.setCustom_11rb$(UiTheme$Companion_getInstance().DARK_SIMPLE.standardFont_mx4ult$(closure$ctx.screenDpi));
      $receiver.textColor.setCustom_11rb$($receiver.root.theme.accentColor);
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda($receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_1(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      $receiver.text = 'GL Version: ' + glCapabilities.glVersion;
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda$lambda_0(this$) {
    return function ($receiver, it) {
      this$.text = getMemoryInfo();
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_2(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda_0($receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda$lambda_1(closure$lastWndW, closure$lastWndH, this$) {
    return function ($receiver, c) {
      if (c.windowWidth !== closure$lastWndW.v || c.windowHeight !== closure$lastWndH.v) {
        closure$lastWndW.v = c.windowWidth;
        closure$lastWndH.v = c.windowHeight;
        this$.text = 'Viewport: ' + c.windowWidth + 'x' + c.windowHeight;
      }
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_3(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      var lastWndW = {v: -1};
      var lastWndH = {v: -1};
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda_1(lastWndW, lastWndH, $receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda$lambda_2(closure$updateT, this$) {
    return function ($receiver, c) {
      closure$updateT.v -= c.deltaT;
      if (closure$updateT.v < 0) {
        closure$updateT.v += 1.0;
        var hh = '' + toString(numberToInt(c.time / 3600.0));
        if (hh.length === 1) {
          hh = '0' + hh;
        }
        var mm = '' + toString(numberToInt(c.time % 3600.0 / 60.0));
        if (mm.length === 1) {
          mm = '0' + mm;
        }
        var ss = '' + toString(numberToInt(c.time % 60.0));
        if (ss.length === 1) {
          ss = '0' + ss;
        }
        this$.text = 'Up: ' + hh + ':' + mm + '.' + ss;
      }
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_4(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      $receiver.text = 'Up: 00:00.00';
      var updateT = {v: 1.0};
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda_2(updateT, $receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda$lambda_3(closure$last, closure$lastMem, this$) {
    return function ($receiver, c) {
      var num = c.memoryMgr.numTextures;
      var mem = c.memoryMgr.getTotalMemory_b1qrxn$(GlResource$Type$TEXTURE_getInstance());
      if (num !== closure$last.v || mem !== closure$lastMem.v) {
        closure$last.v = num;
        closure$lastMem.v = mem;
        var mb = (mem / (1024 * 1024 | 0) + 0.05).toString();
        var pt = indexOf(mb, 46);
        var $receiver_0 = mb;
        var endIndex = pt + 2 | 0;
        mb = $receiver_0.substring(0, endIndex);
        this$.text = num.toString() + ' Textures: ' + mb + 'M';
      }
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_5(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      var last = {v: -1};
      var lastMem = {v: -1.0};
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda_3(last, lastMem, $receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda$lambda_4(closure$last, closure$lastMem, this$) {
    return function ($receiver, c) {
      var num = c.memoryMgr.numBuffers;
      var mem = c.memoryMgr.getTotalMemory_b1qrxn$(GlResource$Type$BUFFER_getInstance());
      if (num !== closure$last.v || mem !== closure$lastMem.v) {
        closure$last.v = num;
        closure$lastMem.v = mem;
        var mb = (mem / (1024 * 1024 | 0) + 0.05).toString();
        var pt = indexOf(mb, 46);
        var $receiver_0 = mb;
        var endIndex = pt + 2 | 0;
        mb = $receiver_0.substring(0, endIndex);
        this$.text = num.toString() + ' Buffers: ' + mb + 'M';
      }
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_6(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      var last = {v: -1};
      var lastMem = {v: -1.0};
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda_4(last, lastMem, $receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda$lambda_5(closure$last, this$) {
    return function ($receiver, c) {
      var num = c.memoryMgr.numShaders;
      if (num !== closure$last.v) {
        closure$last.v = num;
        this$.text = num.toString() + ' Shaders';
      }
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda$lambda_7(closure$yOri) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$yOri.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(18.0, true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment$END_getInstance(), Alignment$CENTER_getInstance());
      var last = {v: -1};
      $receiver.onRender.add_11rb$(debugOverlay$lambda$lambda$lambda$lambda_5(last, $receiver));
      return Unit;
    };
  }
  function debugOverlay$lambda$lambda_0(closure$alignBottom, this$, closure$ctx) {
    return function ($receiver) {
      var tmp$;
      var hasMemInfo = !(getMemoryInfo().length === 0);
      if (hasMemInfo) {
        tmp$ = 168.0;
      }
       else {
        tmp$ = 150.0;
      }
      var height = tmp$;
      if (closure$alignBottom) {
        $receiver.layoutSpec.setOrigin_4ujscr$(dps(-140.0, true), dps(0.0, true), zero());
      }
       else {
        $receiver.layoutSpec.setOrigin_4ujscr$(dps(-120.0, true), dps(-150.0, true), zero());
      }
      $receiver.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(height, true), zero());
      var $receiver_0 = new DeltaTGraph(this$);
      $receiver_0.layoutSpec.setOrigin_4ujscr$(zero(), dps(-40.0, true), zero());
      $receiver_0.layoutSpec.setSize_4ujscr$(dps(140.0, true), dps(40.0, true), zero());
      $receiver.unaryPlus_uv0sim$($receiver_0);
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblFps', debugOverlay$lambda$lambda$lambda_0(closure$ctx)));
      var yOri = {v: -60.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblVersion', debugOverlay$lambda$lambda$lambda_1(yOri)));
      if (hasMemInfo) {
        yOri.v -= 18.0;
        $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblMemInfo', debugOverlay$lambda$lambda$lambda_2(yOri)));
      }
      yOri.v -= 18.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblVpSize', debugOverlay$lambda$lambda$lambda_3(yOri)));
      yOri.v -= 18.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblUpTime', debugOverlay$lambda$lambda$lambda_4(yOri)));
      yOri.v -= 18.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblNumTextures', debugOverlay$lambda$lambda$lambda_5(yOri)));
      yOri.v -= 18.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblNumBuffers', debugOverlay$lambda$lambda$lambda_6(yOri)));
      yOri.v -= 18.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lblNumShaders', debugOverlay$lambda$lambda$lambda_7(yOri)));
      return Unit;
    };
  }
  function debugOverlay$lambda(closure$alignBottom, closure$ctx) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme$Companion_getInstance().DARK, debugOverlay$lambda$lambda);
      $receiver.content.ui.setCustom_11rb$(new BlankComponentUi());
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('dbgPanel', debugOverlay$lambda$lambda_0(closure$alignBottom, $receiver, closure$ctx)));
      return Unit;
    };
  }
  function debugOverlay(ctx, alignBottom) {
    if (alignBottom === void 0)
      alignBottom = false;
    var dbgOverlay = uiScene(ctx.screenDpi, void 0, debugOverlay$lambda(alignBottom, ctx));
    dbgOverlay.isPickingEnabled = false;
    return dbgOverlay;
  }
  function DeltaTGraph(root) {
    UiComponent.call(this, 'deltaT', root);
    this.graphMesh = null;
    this.graphData = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().COLORS]);
    this.graphBuilder = new MeshBuilder(this.graphData);
    this.graphVertex = this.graphData.get_za3lpa$(0);
    this.graphIdx = 0;
    this.prevDeltaT = 0.0;
    this.graphMesh = new Mesh(this.graphData);
    this.graphMesh.meshData.usage = GL_DYNAMIC_DRAW;
    this.graphMesh.shader = basicShader(DeltaTGraph_init$lambda);
  }
  DeltaTGraph.prototype.render_evfofk$ = function (ctx) {
    var color = Color$Companion_getInstance().WHITE;
    if (this.prevDeltaT > 0.05) {
      color = Color$Companion_getInstance().RED;
    }
     else if (this.prevDeltaT > 0.025) {
      color = Color$Companion_getInstance().YELLOW;
    }
    this.setCurrentBarColor_d7aj7k$(color);
    this.prevDeltaT = ctx.deltaT;
    this.graphIdx = (this.graphIdx + 4 | 0) % (numberToInt(this.width) * 4 | 0);
    this.graphVertex.index = this.graphIdx;
    var y0 = this.graphVertex.position.y;
    var a = ctx.deltaT * 250;
    var b = this.height;
    var h = Math_0.min(a, b);
    var tmp$;
    tmp$ = this.graphVertex;
    tmp$.index = tmp$.index + 1 | 0;
    this.graphVertex.position.y = y0 + h;
    var tmp$_0;
    tmp$_0 = this.graphVertex;
    tmp$_0.index = tmp$_0.index + 1 | 0;
    this.graphVertex.position.y = y0 + h;
    this.setCurrentBarColor_d7aj7k$(Color$Companion_getInstance().MAGENTA);
    this.graphData.isSyncRequired = true;
    UiComponent.prototype.render_evfofk$.call(this, ctx);
  };
  DeltaTGraph.prototype.setCurrentBarColor_d7aj7k$ = function (color) {
    this.graphVertex.index = this.graphIdx;
    for (var i = 0; i <= 3; i++) {
      this.graphVertex.index = this.graphIdx + i | 0;
      this.graphVertex.color.set_d7aj7k$(color);
    }
  };
  DeltaTGraph.prototype.updateUi_evfofk$ = function (ctx) {
    var tmp$;
    UiComponent.prototype.updateUi_evfofk$.call(this, ctx);
    this.setupBuilder_84rojv$(this.graphBuilder);
    this.graphBuilder.color = Color$Companion_getInstance().WHITE;
    tmp$ = numberToInt(this.width);
    for (var i = 1; i <= tmp$; i++) {
      this.graphBuilder.line_s2l86p$(i - 0.5, 0.0, i - 0.5, 1.0, 1.0);
    }
  };
  DeltaTGraph.prototype.updateTheme_evfofk$ = function (ctx) {
    UiComponent.prototype.updateTheme_evfofk$.call(this, ctx);
    this.minusAssign_f1kmr1$(this.graphMesh);
    this.plusAssign_f1kmr1$(this.graphMesh);
  };
  function DeltaTGraph_init$lambda($receiver) {
    $receiver.colorModel = ColorModel$VERTEX_COLOR_getInstance();
    $receiver.lightModel = LightModel$NO_LIGHTING_getInstance();
    return Unit;
  }
  DeltaTGraph.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DeltaTGraph',
    interfaces: [UiComponent]
  };
  function uiFont(family, sizeDp, dpi, style, chars) {
    if (style === void 0)
      style = Font$Companion_getInstance().PLAIN;
    if (chars === void 0)
      chars = Font$Companion_getInstance().STD_CHARS;
    var pts = sizeDp * dpi / 96.0;
    return new Font(new FontProps(family, pts, style, pts, chars));
  }
  function fontShader$lambda($receiver) {
    return Unit;
  }
  function fontShader$ObjectLiteral() {
  }
  fontShader$ObjectLiteral.prototype.fsAfterSampling_3c8d48$ = function (shaderProps, text) {
    text.append_gw00v9$(glCapabilities.glslDialect.fragColorBody + ' = ' + GlslGenerator$Companion_getInstance().L_VERTEX_COLOR + ' * ' + GlslGenerator$Companion_getInstance().L_TEX_COLOR + '.a;' + '\n');
  };
  fontShader$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [GlslGenerator$GlslInjector]
  };
  function fontShader(font, propsInit) {
    if (font === void 0)
      font = null;
    if (propsInit === void 0)
      propsInit = fontShader$lambda;
    var props = new ShaderProps();
    propsInit(props);
    props.isVertexColor = true;
    props.isTextureColor = true;
    props.isDiscardTranslucent = true;
    var generator = new GlslGenerator();
    var $receiver = generator.injectors;
    var element = new fontShader$ObjectLiteral();
    $receiver.add_11rb$(element);
    var shader = new BasicShader(props, generator);
    shader.texture = font;
    return shader;
  }
  function FontProps(family, sizePts, style, sizeUnits, chars) {
    if (style === void 0)
      style = Font$Companion_getInstance().PLAIN;
    if (sizeUnits === void 0)
      sizeUnits = sizePts;
    if (chars === void 0)
      chars = Font$Companion_getInstance().STD_CHARS;
    this.family = family;
    this.sizePts = sizePts;
    this.style = style;
    this.sizeUnits = sizeUnits;
    this.chars = chars;
  }
  FontProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FontProps',
    interfaces: []
  };
  FontProps.prototype.component1 = function () {
    return this.family;
  };
  FontProps.prototype.component2 = function () {
    return this.sizePts;
  };
  FontProps.prototype.component3 = function () {
    return this.style;
  };
  FontProps.prototype.component4 = function () {
    return this.sizeUnits;
  };
  FontProps.prototype.component5 = function () {
    return this.chars;
  };
  FontProps.prototype.copy_ogezb0$ = function (family, sizePts, style, sizeUnits, chars) {
    return new FontProps(family === void 0 ? this.family : family, sizePts === void 0 ? this.sizePts : sizePts, style === void 0 ? this.style : style, sizeUnits === void 0 ? this.sizeUnits : sizeUnits, chars === void 0 ? this.chars : chars);
  };
  FontProps.prototype.toString = function () {
    return 'FontProps(family=' + Kotlin.toString(this.family) + (', sizePts=' + Kotlin.toString(this.sizePts)) + (', style=' + Kotlin.toString(this.style)) + (', sizeUnits=' + Kotlin.toString(this.sizeUnits)) + (', chars=' + Kotlin.toString(this.chars)) + ')';
  };
  FontProps.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.family) | 0;
    result = result * 31 + Kotlin.hashCode(this.sizePts) | 0;
    result = result * 31 + Kotlin.hashCode(this.style) | 0;
    result = result * 31 + Kotlin.hashCode(this.sizeUnits) | 0;
    result = result * 31 + Kotlin.hashCode(this.chars) | 0;
    return result;
  };
  FontProps.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.family, other.family) && Kotlin.equals(this.sizePts, other.sizePts) && Kotlin.equals(this.style, other.style) && Kotlin.equals(this.sizeUnits, other.sizeUnits) && Kotlin.equals(this.chars, other.chars)))));
  };
  function Font(fontProps) {
    Font$Companion_getInstance();
    Texture.call(this, defaultProps(fontProps.toString()), Font_init$lambda(fontProps));
    this.fontProps = fontProps;
    this.charMap = Font$Companion_getInstance().getCharMap_0(this.fontProps);
    this.lineSpace = this.fontProps.sizeUnits * 1.2;
    this.normHeight = this.fontProps.sizeUnits * 0.7;
  }
  function Font$Companion() {
    Font$Companion_instance = this;
    this.PLAIN = 0;
    this.BOLD = 1;
    this.ITALIC = 2;
    this.DEFAULT_FONT = null;
    this.STD_CHARS = null;
    this.SYSTEM_FONT = '-apple-system, "Segoe UI", Roboto, Helvetica, Arial, sans-serif';
    this.charMaps_0 = LinkedHashMap_init();
    var str = '';
    for (var i = 32; i <= 126; i++) {
      str += String.fromCharCode(toChar(i));
    }
    str += '\xE4\xC4\xF6\xD6\xFC\xDC\xDF\xB0\xA9';
    this.STD_CHARS = str;
    this.DEFAULT_FONT = new Font(new FontProps(this.SYSTEM_FONT, 12.0));
  }
  Font$Companion.prototype.getCharMap_0 = function (fontProps) {
    var map = this.charMaps_0.get_11rb$(fontProps);
    if (map == null) {
      map = createCharMap(fontProps);
      var $receiver = this.charMaps_0;
      var value = map;
      $receiver.put_xwzc9p$(fontProps, value);
    }
    return map;
  };
  Font$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Font$Companion_instance = null;
  function Font$Companion_getInstance() {
    if (Font$Companion_instance === null) {
      new Font$Companion();
    }
    return Font$Companion_instance;
  }
  Font.prototype.textWidth_61zpoe$ = function (string) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var width = 0.0;
    var maxWidth = 0.0;
    tmp$ = get_indices_0(string);
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
      var c = string.charCodeAt(i);
      width += this.charWidth_s8itvh$(c);
      if (width > maxWidth) {
        maxWidth = width;
      }
      if (c === 10) {
        width = 0.0;
      }
    }
    return maxWidth;
  };
  Font.prototype.charWidth_s8itvh$ = function (char) {
    var tmp$, tmp$_0;
    return (tmp$_0 = (tmp$ = this.charMap.get_11rb$(toBoxedChar(char))) != null ? tmp$.advance : null) != null ? tmp$_0 : 0.0;
  };
  Font.prototype.toString = function () {
    return 'Font(' + this.fontProps.family + ', ' + this.fontProps.sizePts + 'pts, ' + this.fontProps.style + ')';
  };
  function Font_init$lambda(closure$fontProps) {
    return function ($receiver) {
      return Font$Companion_getInstance().getCharMap_0(closure$fontProps).textureData;
    };
  }
  Font.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Font',
    interfaces: [Texture]
  };
  function CharMetrics() {
    this.width = 0.0;
    this.height = 0.0;
    this.xOffset = 0.0;
    this.yBaseline = 0.0;
    this.advance = 0.0;
    this.uvMin = MutableVec2f_init();
    this.uvMax = MutableVec2f_init();
  }
  CharMetrics.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CharMetrics',
    interfaces: []
  };
  function CharMap(textureData, map) {
    this.textureData = textureData;
    this.map_0 = map;
  }
  Object.defineProperty(CharMap.prototype, 'entries', {
    get: function () {
      return this.map_0.entries;
    }
  });
  Object.defineProperty(CharMap.prototype, 'keys', {
    get: function () {
      return this.map_0.keys;
    }
  });
  Object.defineProperty(CharMap.prototype, 'size', {
    get: function () {
      return this.map_0.size;
    }
  });
  Object.defineProperty(CharMap.prototype, 'values', {
    get: function () {
      return this.map_0.values;
    }
  });
  CharMap.prototype.containsKey_11rb$ = function (key) {
    return this.map_0.containsKey_11rb$(key);
  };
  CharMap.prototype.containsValue_11rc$ = function (value) {
    return this.map_0.containsValue_11rc$(value);
  };
  CharMap.prototype.get_11rb$ = function (key) {
    return this.map_0.get_11rb$(key);
  };
  CharMap.prototype.isEmpty = function () {
    return this.map_0.isEmpty();
  };
  CharMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CharMap',
    interfaces: [Map]
  };
  function IndexedVertexList(vertexAttributes) {
    IndexedVertexList$Companion_getInstance();
    this.vertexSizeF = 0;
    this.strideBytesF = 0;
    this.vertexSizeI = 0;
    this.strideBytesI = 0;
    this.size_yty8aj$_0 = 0;
    this.dataF_73lq7w$_0 = null;
    this.dataI_73lqah$_0 = null;
    this.indices_nfuoz3$_0 = createUint32Buffer(IndexedVertexList$Companion_getInstance().INITIAL_SIZE_8be2vx$);
    this.attributeOffsets = null;
    this.tmpVertex_0 = null;
    var tmp$;
    var cntF = 0;
    var cntI = 0;
    var offsets = LinkedHashMap_init();
    tmp$ = vertexAttributes.iterator();
    while (tmp$.hasNext()) {
      var attrib = tmp$.next();
      if (attrib.type.isInt) {
        var value = cntI;
        offsets.put_xwzc9p$(attrib, value);
        cntI = cntI + attrib.type.size | 0;
      }
       else {
        var value_0 = cntF;
        offsets.put_xwzc9p$(attrib, value_0);
        cntF = cntF + attrib.type.size | 0;
      }
    }
    this.attributeOffsets = offsets;
    this.vertexSizeF = cntF;
    this.strideBytesF = this.vertexSizeF * 4 | 0;
    this.vertexSizeI = cntI;
    this.strideBytesI = this.vertexSizeI * 4 | 0;
    this.dataF = createFloat32Buffer(Kotlin.imul(cntF, IndexedVertexList$Companion_getInstance().INITIAL_SIZE_8be2vx$));
    this.dataI = createUint32Buffer(Kotlin.imul(cntI, IndexedVertexList$Companion_getInstance().INITIAL_SIZE_8be2vx$));
    this.tmpVertex_0 = new IndexedVertexList$Vertex(this, 0);
  }
  function IndexedVertexList$Companion() {
    IndexedVertexList$Companion_instance = this;
    this.INITIAL_SIZE_8be2vx$ = 1000;
    this.GROW_FACTOR_8be2vx$ = 2.0;
  }
  IndexedVertexList$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var IndexedVertexList$Companion_instance = null;
  function IndexedVertexList$Companion_getInstance() {
    if (IndexedVertexList$Companion_instance === null) {
      new IndexedVertexList$Companion();
    }
    return IndexedVertexList$Companion_instance;
  }
  Object.defineProperty(IndexedVertexList.prototype, 'size', {
    get: function () {
      return this.size_yty8aj$_0;
    },
    set: function (size) {
      this.size_yty8aj$_0 = size;
    }
  });
  Object.defineProperty(IndexedVertexList.prototype, 'lastIndex', {
    get: function () {
      return this.size - 1 | 0;
    }
  });
  Object.defineProperty(IndexedVertexList.prototype, 'dataF', {
    get: function () {
      return this.dataF_73lq7w$_0;
    },
    set: function (dataF) {
      this.dataF_73lq7w$_0 = dataF;
    }
  });
  Object.defineProperty(IndexedVertexList.prototype, 'dataI', {
    get: function () {
      return this.dataI_73lqah$_0;
    },
    set: function (dataI) {
      this.dataI_73lqah$_0 = dataI;
    }
  });
  Object.defineProperty(IndexedVertexList.prototype, 'indices', {
    get: function () {
      return this.indices_nfuoz3$_0;
    },
    set: function (indices) {
      this.indices_nfuoz3$_0 = indices;
    }
  });
  IndexedVertexList.prototype.increaseDataSizeF_0 = function () {
    var tmp$;
    var newData = createFloat32Buffer(numberToInt(round(this.dataF.capacity * IndexedVertexList$Companion_getInstance().GROW_FACTOR_8be2vx$)));
    tmp$ = this.dataF.capacity;
    for (var i = 0; i < tmp$; i++) {
      newData.set_wxm5ur$(i, this.dataF.get_za3lpa$(i));
    }
    newData.position = this.dataF.position;
    this.dataF = newData;
  };
  IndexedVertexList.prototype.increaseDataSizeI_0 = function () {
    var tmp$;
    var newData = createUint32Buffer(numberToInt(round(this.dataI.capacity * IndexedVertexList$Companion_getInstance().GROW_FACTOR_8be2vx$)));
    tmp$ = this.dataI.capacity;
    for (var i = 0; i < tmp$; i++) {
      newData.set_wxm5ur$(i, this.dataI.get_za3lpa$(i));
    }
    newData.position = this.dataI.position;
    this.dataI = newData;
  };
  IndexedVertexList.prototype.increaseIndicesSize_0 = function () {
    var tmp$;
    var newIdxs = createUint32Buffer(numberToInt(round(this.indices.capacity * IndexedVertexList$Companion_getInstance().GROW_FACTOR_8be2vx$)));
    tmp$ = this.indices.capacity;
    for (var i = 0; i < tmp$; i++) {
      newIdxs.set_wxm5ur$(i, this.indices.get_za3lpa$(i));
    }
    newIdxs.position = this.indices.position;
    this.indices = newIdxs;
  };
  IndexedVertexList.prototype.addVertex_z2do90$ = function (updateBounds, block) {
    if (updateBounds === void 0)
      updateBounds = null;
    var tmp$, tmp$_0, tmp$_1;
    if (this.dataF.remaining < this.vertexSizeF) {
      this.increaseDataSizeF_0();
    }
    if (this.dataI.remaining < this.vertexSizeI) {
      this.increaseDataSizeI_0();
    }
    tmp$ = this.vertexSizeF;
    for (var i = 1; i <= tmp$; i++) {
      this.dataF.plusAssign_11rb$(0.0);
    }
    tmp$_0 = this.vertexSizeI;
    for (var i_0 = 1; i_0 <= tmp$_0; i_0++) {
      this.dataI.plusAssign_11rb$(0);
    }
    this.tmpVertex_0.index = (tmp$_1 = this.size, this.size = tmp$_1 + 1 | 0, tmp$_1);
    block(this.tmpVertex_0);
    updateBounds != null ? (updateBounds.add_czzhiu$(this.tmpVertex_0.position), Unit) : null;
    return this.size - 1 | 0;
  };
  function IndexedVertexList$addVertex$lambda(closure$position, closure$normal, closure$color, closure$texCoord) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(closure$position);
      if (closure$normal != null) {
        $receiver.normal.set_czzhiu$(closure$normal);
      }
      if (closure$color != null) {
        $receiver.color.set_d7aj7k$(closure$color);
      }
      if (closure$texCoord != null) {
        $receiver.texCoord.set_czzhjp$(closure$texCoord);
      }
      return Unit;
    };
  }
  IndexedVertexList.prototype.addVertex_lv7vxo$ = function (position, normal, color, texCoord) {
    if (normal === void 0)
      normal = null;
    if (color === void 0)
      color = null;
    if (texCoord === void 0)
      texCoord = null;
    return this.addVertex_z2do90$(void 0, IndexedVertexList$addVertex$lambda(position, normal, color, texCoord));
  };
  IndexedVertexList.prototype.addIndex_za3lpa$ = function (idx) {
    if (this.indices.remaining === 0) {
      this.increaseIndicesSize_0();
    }
    this.indices.plusAssign_11rb$(idx);
  };
  IndexedVertexList.prototype.addIndices_q5rwfd$ = function (indices) {
    for (var idx = 0; idx !== indices.length; ++idx) {
      this.addIndex_za3lpa$(indices[idx]);
    }
  };
  IndexedVertexList.prototype.clear = function () {
    this.size = 0;
    this.dataF.position = 0;
    this.dataF.limit = this.dataF.capacity;
    this.dataI.position = 0;
    this.dataI.limit = this.dataI.capacity;
    this.indices.position = 0;
    this.indices.limit = this.indices.capacity;
  };
  IndexedVertexList.prototype.get_za3lpa$ = function (i) {
    if (i < 0 || i >= (this.dataF.capacity / this.vertexSizeF | 0)) {
      throw new KoolException('Vertex index out of bounds: ' + i);
    }
    return new IndexedVertexList$Vertex(this, i);
  };
  function IndexedVertexList$Vertex($outer, index) {
    this.$outer = $outer;
    this.offsetF_0 = Kotlin.imul(index, this.$outer.vertexSizeF);
    this.offsetI_0 = Kotlin.imul(index, this.$outer.vertexSizeI);
    this.index_3yqnyk$_0 = index;
    this.position = null;
    this.normal = null;
    this.tangent = null;
    this.color = null;
    this.texCoord = null;
    this.attributeViews_0 = null;
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    var attribViews = LinkedHashMap_init();
    this.attributeViews_0 = attribViews;
    tmp$ = this.$outer.attributeOffsets.entries.iterator();
    while (tmp$.hasNext()) {
      var offset = tmp$.next();
      switch (offset.key.type.name) {
        case 'FLOAT':
          var key = offset.key;
          var value = new IndexedVertexList$Vertex$FloatView(this, offset.value);
          attribViews.put_xwzc9p$(key, value);
          break;
        case 'VEC_2F':
          var key_0 = offset.key;
          var value_0 = new IndexedVertexList$Vertex$Vec2fView(this, offset.value);
          attribViews.put_xwzc9p$(key_0, value_0);
          break;
        case 'VEC_3F':
          var key_1 = offset.key;
          var value_1 = new IndexedVertexList$Vertex$Vec3fView(this, offset.value);
          attribViews.put_xwzc9p$(key_1, value_1);
          break;
        case 'VEC_4F':
          var key_2 = offset.key;
          var value_2 = new IndexedVertexList$Vertex$Vec4fView(this, offset.value);
          attribViews.put_xwzc9p$(key_2, value_2);
          break;
        case 'COLOR_4F':
          var key_3 = offset.key;
          var value_3 = new IndexedVertexList$Vertex$ColorView(this, offset.value);
          attribViews.put_xwzc9p$(key_3, value_3);
          break;
        case 'INT':
          var key_4 = offset.key;
          var value_4 = new IndexedVertexList$Vertex$IntView(this, offset.value);
          attribViews.put_xwzc9p$(key_4, value_4);
          break;
        case 'VEC_2I':
          var key_5 = offset.key;
          var value_5 = new IndexedVertexList$Vertex$Vec2iView(this, offset.value);
          attribViews.put_xwzc9p$(key_5, value_5);
          break;
        case 'VEC_3I':
          var key_6 = offset.key;
          var value_6 = new IndexedVertexList$Vertex$Vec3iView(this, offset.value);
          attribViews.put_xwzc9p$(key_6, value_6);
          break;
        case 'VEC_4I':
          var key_7 = offset.key;
          var value_7 = new IndexedVertexList$Vertex$Vec4iView(this, offset.value);
          attribViews.put_xwzc9p$(key_7, value_7);
          break;
      }
    }
    this.position = (tmp$_0 = this.getVec3fAttribute_mczodr$(Attribute$Companion_getInstance().POSITIONS)) != null ? tmp$_0 : new IndexedVertexList$Vertex$Vec3fView(this, -1);
    this.normal = (tmp$_1 = this.getVec3fAttribute_mczodr$(Attribute$Companion_getInstance().NORMALS)) != null ? tmp$_1 : new IndexedVertexList$Vertex$Vec3fView(this, -1);
    this.tangent = (tmp$_2 = this.getVec3fAttribute_mczodr$(Attribute$Companion_getInstance().TANGENTS)) != null ? tmp$_2 : new IndexedVertexList$Vertex$Vec3fView(this, -1);
    this.texCoord = (tmp$_3 = this.getVec2fAttribute_mczodr$(Attribute$Companion_getInstance().TEXTURE_COORDS)) != null ? tmp$_3 : new IndexedVertexList$Vertex$Vec2fView(this, -1);
    this.color = (tmp$_4 = this.getColorAttribute_mczodr$(Attribute$Companion_getInstance().COLORS)) != null ? tmp$_4 : new IndexedVertexList$Vertex$ColorView(this, -1);
  }
  Object.defineProperty(IndexedVertexList$Vertex.prototype, 'index', {
    get: function () {
      return this.index_3yqnyk$_0;
    },
    set: function (value) {
      this.index_3yqnyk$_0 = value;
      this.offsetF_0 = Kotlin.imul(value, this.$outer.vertexSizeF);
      this.offsetI_0 = Kotlin.imul(value, this.$outer.vertexSizeI);
    }
  });
  IndexedVertexList$Vertex.prototype.set_j5bz6$ = function (other) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5, tmp$_6, tmp$_7, tmp$_8;
    tmp$ = this.attributeViews_0.keys.iterator();
    while (tmp$.hasNext()) {
      var attrib = tmp$.next();
      var view = other.attributeViews_0.get_11rb$(attrib);
      if (view != null) {
        if (Kotlin.isType(view, IndexedVertexList$Vertex$FloatView))
          (Kotlin.isType(tmp$_0 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$FloatView) ? tmp$_0 : throwCCE()).f = view.f;
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$Vec2fView))
          (Kotlin.isType(tmp$_1 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$Vec2fView) ? tmp$_1 : throwCCE()).set_czzhjp$(view);
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$Vec3fView))
          (Kotlin.isType(tmp$_2 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$Vec3fView) ? tmp$_2 : throwCCE()).set_czzhiu$(view);
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$Vec4fView))
          (Kotlin.isType(tmp$_3 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$Vec4fView) ? tmp$_3 : throwCCE()).set_czzhhz$(view);
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$ColorView))
          (Kotlin.isType(tmp$_4 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$ColorView) ? tmp$_4 : throwCCE()).set_d7aj7k$(view);
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$IntView))
          (Kotlin.isType(tmp$_5 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$IntView) ? tmp$_5 : throwCCE()).i = view.i;
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$Vec2iView))
          (Kotlin.isType(tmp$_6 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$Vec2iView) ? tmp$_6 : throwCCE()).set_c4i3fg$(view);
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$Vec3iView))
          (Kotlin.isType(tmp$_7 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$Vec3iView) ? tmp$_7 : throwCCE()).set_bngh19$(view);
        else if (Kotlin.isType(view, IndexedVertexList$Vertex$Vec4iView))
          (Kotlin.isType(tmp$_8 = this.attributeViews_0.get_11rb$(attrib), IndexedVertexList$Vertex$Vec4iView) ? tmp$_8 : throwCCE()).set_b6eun2$(view);
      }
    }
  };
  IndexedVertexList$Vertex.prototype.getFloatAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$FloatView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getVec2fAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$Vec2fView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getVec3fAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$Vec3fView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getVec4fAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$Vec4fView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getColorAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$ColorView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getIntAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$IntView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getVec2iAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$Vec2iView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getVec3iAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$Vec3iView) ? tmp$ : throwCCE();
  };
  IndexedVertexList$Vertex.prototype.getVec4iAttribute_mczodr$ = function (attribute) {
    var tmp$;
    return (tmp$ = this.attributeViews_0.get_11rb$(attribute)) == null || Kotlin.isType(tmp$, IndexedVertexList$Vertex$Vec4iView) ? tmp$ : throwCCE();
  };
  function IndexedVertexList$Vertex$FloatView($outer, attribOffset) {
    this.$outer = $outer;
    this.attribOffset_0 = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$FloatView.prototype, 'f', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset_0 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset_0 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$FloatView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FloatView',
    interfaces: []
  };
  function IndexedVertexList$Vertex$Vec2fView($outer, attribOffset) {
    this.$outer = $outer;
    MutableVec2f_init(this);
    this.attribOffset_0 = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$Vec2fView.prototype, 'x', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset_0 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset_0 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec2fView.prototype, 'y', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset_0 + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset_0 + 1 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$Vec2fView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec2fView',
    interfaces: [MutableVec2f]
  };
  function IndexedVertexList$Vertex$Vec3fView($outer, attribOffset) {
    this.$outer = $outer;
    MutableVec3f_init(this);
    this.attribOffset = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$Vec3fView.prototype, 'x', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec3fView.prototype, 'y', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec3fView.prototype, 'z', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$Vec3fView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec3fView',
    interfaces: [MutableVec3f]
  };
  function IndexedVertexList$Vertex$Vec4fView($outer, attribOffset) {
    this.$outer = $outer;
    MutableVec4f_init(this);
    this.attribOffset = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$Vec4fView.prototype, 'x', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec4fView.prototype, 'y', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec4fView.prototype, 'z', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec4fView.prototype, 'w', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 3 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 3 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$Vec4fView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec4fView',
    interfaces: [MutableVec4f]
  };
  function IndexedVertexList$Vertex$ColorView($outer, attribOffset) {
    this.$outer = $outer;
    MutableColor_init(this);
    this.attribOffset = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'r', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'x', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'g', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'y', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 1 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'b', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'z', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 2 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'a', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 3 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 3 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$ColorView.prototype, 'w', {
    get: function () {
      if (this.attribOffset < 0) {
        return 0.0;
      }
       else {
        return this.$outer.$outer.dataF.get_za3lpa$(this.$outer.offsetF_0 + this.attribOffset + 3 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset >= 0) {
        this.$outer.$outer.dataF.set_wxm5ur$(this.$outer.offsetF_0 + this.attribOffset + 3 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$ColorView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorView',
    interfaces: [MutableColor]
  };
  function IndexedVertexList$Vertex$IntView($outer, attribOffset) {
    this.$outer = $outer;
    this.attribOffset_0 = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$IntView.prototype, 'i', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$IntView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IntView',
    interfaces: []
  };
  function IndexedVertexList$Vertex$Vec2iView($outer, attribOffset) {
    this.$outer = $outer;
    this.attribOffset_0 = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$Vec2iView.prototype, 'x', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec2iView.prototype, 'y', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 + 1 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$Vec2iView.prototype.set_vux9f0$ = function (x, y) {
    this.x = x;
    this.y = y;
  };
  IndexedVertexList$Vertex$Vec2iView.prototype.set_c4i3fg$ = function (other) {
    this.x = other.x;
    this.y = other.y;
  };
  IndexedVertexList$Vertex$Vec2iView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec2iView',
    interfaces: []
  };
  function IndexedVertexList$Vertex$Vec3iView($outer, attribOffset) {
    this.$outer = $outer;
    this.attribOffset_0 = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$Vec3iView.prototype, 'x', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec3iView.prototype, 'y', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 + 1 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec3iView.prototype, 'z', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 + 2 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 + 2 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$Vec3iView.prototype.set_bngh19$ = function (other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
  };
  IndexedVertexList$Vertex$Vec3iView.prototype.set_qt1dr2$ = function (x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
  };
  IndexedVertexList$Vertex$Vec3iView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec3iView',
    interfaces: []
  };
  function IndexedVertexList$Vertex$Vec4iView($outer, attribOffset) {
    this.$outer = $outer;
    this.attribOffset_0 = attribOffset;
  }
  Object.defineProperty(IndexedVertexList$Vertex$Vec4iView.prototype, 'x', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec4iView.prototype, 'y', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 + 1 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 + 1 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec4iView.prototype, 'z', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 + 2 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 + 2 | 0, value);
      }
    }
  });
  Object.defineProperty(IndexedVertexList$Vertex$Vec4iView.prototype, 'w', {
    get: function () {
      if (this.attribOffset_0 < 0) {
        return 0;
      }
       else {
        return this.$outer.$outer.dataI.get_za3lpa$(this.$outer.offsetI_0 + this.attribOffset_0 + 3 | 0);
      }
    },
    set: function (value) {
      if (this.attribOffset_0 >= 0) {
        this.$outer.$outer.dataI.set_wxm5ur$(this.$outer.offsetI_0 + this.attribOffset_0 + 3 | 0, value);
      }
    }
  });
  IndexedVertexList$Vertex$Vec4iView.prototype.set_b6eun2$ = function (other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
    this.w = other.w;
  };
  IndexedVertexList$Vertex$Vec4iView.prototype.set_tjonv8$ = function (x, y, z, w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  };
  IndexedVertexList$Vertex$Vec4iView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec4iView',
    interfaces: []
  };
  IndexedVertexList$Vertex.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vertex',
    interfaces: []
  };
  IndexedVertexList.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IndexedVertexList',
    interfaces: []
  };
  function lineMesh(name, block) {
    if (name === void 0)
      name = null;
    var $receiver = new LineMesh(void 0, name);
    block($receiver);
    return $receiver;
  }
  function LineMesh(data, name) {
    if (data === void 0)
      data = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().COLORS]);
    if (name === void 0)
      name = null;
    Mesh.call(this, data, name);
    this.primitiveType = GL_LINES;
    this.shader = basicShader(LineMesh_init$lambda);
    this.isXray = false;
    this.lineWidth = 1.0;
  }
  LineMesh.prototype.addLine_b8opkg$ = function (point0, color0, point1, color1) {
    this.meshData.isBatchUpdate = true;
    var idx = this.meshData.addVertex_lv7vxo$(point0, null, color0, null);
    this.meshData.addIndex_za3lpa$(idx);
    idx = this.meshData.addVertex_lv7vxo$(point1, null, color1, null);
    this.meshData.addIndex_za3lpa$(idx);
    this.meshData.isBatchUpdate = false;
  };
  LineMesh.prototype.render_evfofk$ = function (ctx) {
    ctx.pushAttributes();
    ctx.lineWidth = this.lineWidth;
    if (this.isXray) {
      ctx.depthFunc = GL_ALWAYS;
    }
    ctx.applyAttributes();
    Mesh.prototype.render_evfofk$.call(this, ctx);
    ctx.popAttributes();
  };
  function LineMesh_init$lambda($receiver) {
    $receiver.colorModel = ColorModel$VERTEX_COLOR_getInstance();
    $receiver.lightModel = LightModel$NO_LIGHTING_getInstance();
    return Unit;
  }
  LineMesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LineMesh',
    interfaces: [Mesh]
  };
  function MeshBuilder(meshData) {
    this.meshData = meshData;
    this.transform = new Mat4fStack();
    this.color = Color$Companion_getInstance().BLACK;
    this.vertexModFun = null;
    this.hasNormals_c51zdn$_0 = this.meshData.hasAttribute_mczodr$(Attribute$Companion_getInstance().NORMALS);
    this.tmpPos_5ud41y$_0 = MutableVec3f_init();
    this.tmpNrm_5ueby3$_0 = MutableVec3f_init();
    this.tmpUv_b9nk9x$_0 = MutableVec2f_init();
    this.circleProps = new CircleProps();
    this.cubeProps = new CubeProps();
    this.cylinderProps = new CylinderProps();
    this.rectProps = new RectProps();
    this.sphereProps = new SphereProps();
    this.textProps = new TextProps();
  }
  function MeshBuilder$vertex$lambda(closure$pos, closure$nrm, closure$uv, this$MeshBuilder) {
    return function ($receiver) {
      var tmp$;
      $receiver.position.set_czzhiu$(closure$pos);
      $receiver.normal.set_czzhiu$(closure$nrm);
      $receiver.texCoord.set_czzhjp$(closure$uv);
      $receiver.color.set_d7aj7k$(this$MeshBuilder.color);
      (tmp$ = this$MeshBuilder.vertexModFun) != null ? tmp$($receiver) : null;
      this$MeshBuilder.transform.transform_w1lst9$($receiver.position);
      if (this$MeshBuilder.hasNormals_c51zdn$_0) {
        this$MeshBuilder.transform.transform_w1lst9$($receiver.normal, 0.0);
        $receiver.normal.norm();
      }
      return Unit;
    };
  }
  MeshBuilder.prototype.vertex_n440gp$$default = function (pos, nrm, uv) {
    return this.meshData.addVertex_hvwyd1$(MeshBuilder$vertex$lambda(pos, nrm, uv, this));
  };
  MeshBuilder.prototype.vertex_n440gp$ = function (pos, nrm, uv, callback$default) {
    if (uv === void 0)
      uv = Vec2f$Companion_getInstance().ZERO;
    return callback$default ? callback$default(pos, nrm, uv) : this.vertex_n440gp$$default(pos, nrm, uv);
  };
  MeshBuilder.prototype.withTransform_v2sixm$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.withTransform_v2sixm$', function (block) {
    this.transform.push();
    block(this);
    this.transform.pop();
  });
  MeshBuilder.prototype.withColor_2f8443$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.withColor_2f8443$', function (color, block) {
    var c = this.color;
    if (color != null) {
      this.color = color;
    }
    block(this);
    this.color = c;
  });
  MeshBuilder.prototype.clear = function () {
    this.meshData.clear();
    this.identity();
  };
  MeshBuilder.prototype.identity = function () {
    return this.transform.setIdentity();
  };
  MeshBuilder.prototype.translate_czzhiu$ = function (t) {
    return this.transform.translate_y2kzbl$(t.x, t.y, t.z);
  };
  MeshBuilder.prototype.translate_y2kzbl$ = function (x, y, z) {
    return this.transform.translate_y2kzbl$(x, y, z);
  };
  MeshBuilder.prototype.rotate_ad55pp$ = function (angleDeg, axis) {
    return this.transform.rotate_ad55pp$(angleDeg, axis);
  };
  MeshBuilder.prototype.rotate_7b5o5w$ = function (angleDeg, axX, axY, axZ) {
    return this.transform.rotate_7b5o5w$(angleDeg, axX, axY, axZ);
  };
  MeshBuilder.prototype.scale_y2kzbl$ = function (x, y, z) {
    return this.transform.scale_y2kzbl$(x, y, z);
  };
  MeshBuilder.prototype.setCoordSystem_xq1mqt$ = function (origin, right, up, top) {
    if (top === void 0)
      top = null;
    var topV = top;
    if (topV == null) {
      topV = cross(right, up);
    }
    this.transform.setIdentity();
    this.transform.set_n0b4r3$(0, 0, right.x);
    this.transform.set_n0b4r3$(1, 0, right.y);
    this.transform.set_n0b4r3$(2, 0, right.z);
    this.transform.set_n0b4r3$(0, 1, up.x);
    this.transform.set_n0b4r3$(1, 1, up.y);
    this.transform.set_n0b4r3$(2, 1, up.z);
    this.transform.set_n0b4r3$(0, 2, topV.x);
    this.transform.set_n0b4r3$(1, 2, topV.y);
    this.transform.set_n0b4r3$(2, 2, topV.z);
    this.transform.set_n0b4r3$(0, 3, origin.x);
    this.transform.set_n0b4r3$(1, 3, origin.y);
    this.transform.set_n0b4r3$(2, 3, origin.z);
  };
  MeshBuilder.prototype.circle_5yji8k$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.circle_5yji8k$', function (props) {
    props(this.circleProps.defaults());
    this.circle_59f34t$(this.circleProps);
  });
  MeshBuilder.prototype.circle_59f34t$ = function (props) {
    var tmp$;
    var i1 = 0;
    var iCenter = this.vertex_n440gp$(props.center, Vec3f$Companion_getInstance().Z_AXIS, props.uvCenter);
    tmp$ = props.steps;
    for (var i = 0; i <= tmp$; i++) {
      var ang = (props.startDeg + props.sweepDeg * i / props.steps) * package$math.DEG_2_RAD;
      var cos = Math_0.cos(ang);
      var sin = Math_0.sin(ang);
      var px = props.center.x + props.radius * cos;
      var py = props.center.y + props.radius * sin;
      this.tmpUv_b9nk9x$_0.set_dleff0$(cos, -sin).scale_mx4ult$(props.uvRadius).add_czzhjp$(props.uvCenter);
      var idx = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(px, py, props.center.z), Vec3f$Companion_getInstance().Z_AXIS, this.tmpUv_b9nk9x$_0);
      if (i > 0) {
        this.meshData.addTriIndices_qt1dr2$(iCenter, i1, idx);
      }
      i1 = idx;
    }
  };
  MeshBuilder.prototype.sphere_ybunu9$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.sphere_ybunu9$', function (props) {
    props(this.sphereProps.defaults());
    this.sphere_mojs8w$(this.sphereProps);
  });
  MeshBuilder.prototype.sphere_mojs8w$ = function (props) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var a = props.steps / 2 | 0;
    var steps = Math_0.max(a, 4);
    var prevIndices = new Int32Array((steps * 2 | 0) + 1 | 0);
    var rowIndices = new Int32Array((steps * 2 | 0) + 1 | 0);
    var theta = math.PI * (steps - 1 | 0) / steps;
    var x = theta;
    var r = Math_0.sin(x) * props.radius;
    var x_0 = theta;
    var y = Math_0.cos(x_0) * props.radius;
    tmp$ = steps * 2 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var phi = math.PI * i / steps;
      var x_1 = -phi;
      var x_2 = Math_0.cos(x_1) * r;
      var x_3 = -phi;
      var z = Math_0.sin(x_3) * r;
      var uv = props.texCoordGenerator(theta, phi);
      rowIndices[i] = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(x_2, y, z).add_czzhiu$(props.center), this.tmpNrm_5ueby3$_0.set_y2kzbl$(x_2, y, z).scale_mx4ult$(1.0 / props.radius), uv);
      if (i > 0) {
        uv = props.texCoordGenerator(math.PI, phi);
        this.tmpPos_5ud41y$_0.set_y2kzbl$(props.center.x, props.center.y - props.radius, props.center.z);
        var iCenter = this.vertex_n440gp$(this.tmpPos_5ud41y$_0, Vec3f$Companion_getInstance().NEG_Y_AXIS, uv);
        this.meshData.addTriIndices_qt1dr2$(iCenter, rowIndices[i], rowIndices[i - 1 | 0]);
      }
    }
    tmp$_0 = steps - 1 | 0;
    for (var row = 2; row <= tmp$_0; row++) {
      var tmp = prevIndices;
      prevIndices = rowIndices;
      rowIndices = tmp;
      theta = math.PI * (steps - row | 0) / steps;
      var x_4 = theta;
      r = Math_0.sin(x_4) * props.radius;
      var x_5 = theta;
      y = Math_0.cos(x_5) * props.radius;
      tmp$_1 = steps * 2 | 0;
      for (var i_0 = 0; i_0 <= tmp$_1; i_0++) {
        var phi_0 = math.PI * i_0 / steps;
        var x_6 = -phi_0;
        var x_7 = Math_0.cos(x_6) * r;
        var x_8 = -phi_0;
        var z_0 = Math_0.sin(x_8) * r;
        var uv_0 = props.texCoordGenerator(theta, phi_0);
        rowIndices[i_0] = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(x_7, y, z_0).add_czzhiu$(props.center), this.tmpNrm_5ueby3$_0.set_y2kzbl$(x_7, y, z_0).scale_mx4ult$(1.0 / props.radius), uv_0);
        if (i_0 > 0) {
          this.meshData.addTriIndices_qt1dr2$(prevIndices[i_0 - 1 | 0], rowIndices[i_0], rowIndices[i_0 - 1 | 0]);
          this.meshData.addTriIndices_qt1dr2$(prevIndices[i_0 - 1 | 0], prevIndices[i_0], rowIndices[i_0]);
        }
      }
    }
    tmp$_2 = steps * 2 | 0;
    for (var i_1 = 1; i_1 <= tmp$_2; i_1++) {
      var uv_1 = props.texCoordGenerator(0.0, math.PI * i_1 / steps);
      var iCenter_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.center.x, props.center.y + props.radius, props.center.z), Vec3f$Companion_getInstance().Y_AXIS, uv_1);
      this.meshData.addTriIndices_qt1dr2$(iCenter_0, rowIndices[i_1 - 1 | 0], rowIndices[i_1]);
    }
  };
  MeshBuilder.prototype.rect_6h1xlk$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.rect_6h1xlk$', function (props) {
    props(this.rectProps.defaults());
    this.rect_e5k3t5$(this.rectProps);
  });
  MeshBuilder.prototype.rect_e5k3t5$ = function (props) {
    props.fixNegativeSize();
    if (props.cornerRadius === 0.0) {
      var i0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().Z_AXIS, props.texCoordLowerLeft);
      var i1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().Z_AXIS, props.texCoordLowerRight);
      var i2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().Z_AXIS, props.texCoordUpperRight);
      var i3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().Z_AXIS, props.texCoordUpperLeft);
      this.meshData.addTriIndices_qt1dr2$(i0, i1, i2);
      this.meshData.addTriIndices_qt1dr2$(i0, i2, i3);
    }
     else {
      var x = props.origin.x;
      var y = props.origin.y;
      var z = props.origin.z;
      var w = props.size.x;
      var h = props.size.y;
      var xI = x + props.cornerRadius;
      var yI = y + props.cornerRadius;
      var wI = w - props.cornerRadius * 2;
      var hI = h - props.cornerRadius * 2;
      var nrm = Vec3f$Companion_getInstance().Z_AXIS;
      var uI = (props.texCoordUpperRight.x - props.texCoordUpperLeft.x) * props.cornerRadius / w;
      var vI = (props.texCoordUpperRight.y - props.texCoordLowerRight.y) * props.cornerRadius / h;
      if (hI > 0) {
        var i0_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(x, yI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(0.0, vI).add_czzhjp$(props.texCoordLowerLeft));
        var i1_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(x + w, yI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(0.0, vI).add_czzhjp$(props.texCoordLowerRight));
        var i2_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(x + w, yI + hI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(0.0, -vI).add_czzhjp$(props.texCoordUpperRight));
        var i3_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(x, yI + hI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(0.0, -vI).add_czzhjp$(props.texCoordUpperLeft));
        this.meshData.addTriIndices_qt1dr2$(i0_0, i1_0, i2_0);
        this.meshData.addTriIndices_qt1dr2$(i0_0, i2_0, i3_0);
      }
      if (wI > 0) {
        var i0_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI, y, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(uI, 0.0).add_czzhjp$(props.texCoordLowerLeft));
        var i1_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI + wI, y, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(-uI, 0.0).add_czzhjp$(props.texCoordLowerRight));
        var i2_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI + wI, yI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(-uI, vI).add_czzhjp$(props.texCoordLowerRight));
        var i3_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI, yI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(uI, vI).add_czzhjp$(props.texCoordLowerLeft));
        this.meshData.addTriIndices_qt1dr2$(i0_1, i1_1, i2_1);
        this.meshData.addTriIndices_qt1dr2$(i0_1, i2_1, i3_1);
        i0_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI, yI + hI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(uI, -vI).add_czzhjp$(props.texCoordUpperLeft));
        i1_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI + wI, yI + hI, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(-uI, -vI).add_czzhjp$(props.texCoordUpperRight));
        i2_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI + wI, y + h, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(-uI, 0.0).add_czzhjp$(props.texCoordUpperRight));
        i3_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(xI, y + h, z), nrm, this.tmpUv_b9nk9x$_0.set_dleff0$(uI, 0.0).add_czzhjp$(props.texCoordUpperLeft));
        this.meshData.addTriIndices_qt1dr2$(i0_1, i1_1, i2_1);
        this.meshData.addTriIndices_qt1dr2$(i0_1, i2_1, i3_1);
      }
      var $receiver = this.circleProps.defaults();
      $receiver.center.set_y2kzbl$(xI + wI, yI + hI, z);
      $receiver.startDeg = 0.0;
      $receiver.sweepDeg = 90.0;
      $receiver.radius = props.cornerRadius;
      $receiver.steps = props.cornerSteps;
      $receiver.uvCenter.set_dleff0$(-uI, -vI).add_czzhjp$(props.texCoordUpperRight);
      $receiver.uvRadius = uI;
      this.circle_59f34t$(this.circleProps);
      var $receiver_0 = this.circleProps.defaults();
      $receiver_0.center.set_y2kzbl$(xI, yI + hI, z);
      $receiver_0.startDeg = 90.0;
      $receiver_0.sweepDeg = 90.0;
      $receiver_0.radius = props.cornerRadius;
      $receiver_0.steps = props.cornerSteps;
      $receiver_0.uvCenter.set_dleff0$(uI, -vI).add_czzhjp$(props.texCoordUpperLeft);
      $receiver_0.uvRadius = uI;
      this.circle_59f34t$(this.circleProps);
      var $receiver_1 = this.circleProps.defaults();
      $receiver_1.center.set_y2kzbl$(xI, yI, z);
      $receiver_1.startDeg = 180.0;
      $receiver_1.sweepDeg = 90.0;
      $receiver_1.radius = props.cornerRadius;
      $receiver_1.steps = props.cornerSteps;
      $receiver_1.uvCenter.set_dleff0$(uI, vI).add_czzhjp$(props.texCoordLowerLeft);
      $receiver_1.uvRadius = uI;
      this.circle_59f34t$(this.circleProps);
      var $receiver_2 = this.circleProps.defaults();
      $receiver_2.center.set_y2kzbl$(xI + wI, yI, z);
      $receiver_2.startDeg = 270.0;
      $receiver_2.sweepDeg = 90.0;
      $receiver_2.radius = props.cornerRadius;
      $receiver_2.steps = props.cornerSteps;
      $receiver_2.uvCenter.set_dleff0$(-uI, vI).add_czzhjp$(props.texCoordLowerRight);
      $receiver_2.uvRadius = uI;
      this.circle_59f34t$(this.circleProps);
    }
  };
  MeshBuilder.prototype.line_uy9yj5$ = function (pt1, pt2, width) {
    this.line_s2l86p$(pt1.x, pt1.y, pt2.x, pt2.y, width);
  };
  MeshBuilder.prototype.line_s2l86p$ = function (x1, y1, x2, y2, width) {
    var dx = x2 - x1;
    var dy = y2 - y1;
    var x = dx * dx + dy * dy;
    var len = Math_0.sqrt(x);
    var addX = width * 0.25 * dx / len;
    var addY = width * 0.25 * dy / len;
    dx += addX + addX;
    dy += addY + addY;
    len += width * 0.5;
    var dxu = dx / len * width / 2;
    var dyu = dy / len * width / 2;
    var qx0 = x1 - addX + dyu;
    var qy0 = y1 - addY - dxu;
    var qx1 = x2 + addX + dyu;
    var qy1 = y2 + addY - dxu;
    var qx2 = x2 + addX - dyu;
    var qy2 = y2 + addY + dxu;
    var qx3 = x1 - addX - dyu;
    var qy3 = y1 - addY + dxu;
    var i0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(qx0, qy0, 0.0), Vec3f$Companion_getInstance().Z_AXIS);
    var i1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(qx1, qy1, 0.0), Vec3f$Companion_getInstance().Z_AXIS);
    var i2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(qx2, qy2, 0.0), Vec3f$Companion_getInstance().Z_AXIS);
    var i3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(qx3, qy3, 0.0), Vec3f$Companion_getInstance().Z_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0, i1, i2);
    this.meshData.addTriIndices_qt1dr2$(i0, i2, i3);
  };
  MeshBuilder.prototype.cube_xdlx95$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.cube_xdlx95$', function (props) {
    props(this.cubeProps.defaults());
    this.cube_lhbb6w$(this.cubeProps);
  });
  MeshBuilder.prototype.cube_lhbb6w$ = function (props) {
    props.fixNegativeSize();
    var color = props.frontColor;
    var c = this.color;
    if (color != null) {
      this.color = color;
    }
    var i0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().Z_AXIS);
    var i1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().Z_AXIS);
    var i2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().Z_AXIS);
    var i3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().Z_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0, i1, i2);
    this.meshData.addTriIndices_qt1dr2$(i0, i2, i3);
    this.color = c;
    var color_0 = props.rightColor;
    var c_0 = this.color;
    if (color_0 != null) {
      this.color = color_0;
    }
    var i0_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().X_AXIS);
    var i1_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().X_AXIS);
    var i2_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().X_AXIS);
    var i3_0 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().X_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0_0, i1_0, i2_0);
    this.meshData.addTriIndices_qt1dr2$(i0_0, i2_0, i3_0);
    this.color = c_0;
    var color_1 = props.backColor;
    var c_1 = this.color;
    if (color_1 != null) {
      this.color = color_1;
    }
    var i0_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().NEG_Z_AXIS);
    var i1_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().NEG_Z_AXIS);
    var i2_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().NEG_Z_AXIS);
    var i3_1 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().NEG_Z_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0_1, i1_1, i2_1);
    this.meshData.addTriIndices_qt1dr2$(i0_1, i2_1, i3_1);
    this.color = c_1;
    var color_2 = props.leftColor;
    var c_2 = this.color;
    if (color_2 != null) {
      this.color = color_2;
    }
    var i0_2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().NEG_X_AXIS);
    var i1_2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().NEG_X_AXIS);
    var i2_2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().NEG_X_AXIS);
    var i3_2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().NEG_X_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0_2, i1_2, i2_2);
    this.meshData.addTriIndices_qt1dr2$(i0_2, i2_2, i3_2);
    this.color = c_2;
    var color_3 = props.topColor;
    var c_3 = this.color;
    if (color_3 != null) {
      this.color = color_3;
    }
    var i0_3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().Y_AXIS);
    var i1_3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().Y_AXIS);
    var i2_3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().Y_AXIS);
    var i3_3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f$Companion_getInstance().Y_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0_3, i1_3, i2_3);
    this.meshData.addTriIndices_qt1dr2$(i0_3, i2_3, i3_3);
    this.color = c_3;
    var color_4 = props.bottomColor;
    var c_4 = this.color;
    if (color_4 != null) {
      this.color = color_4;
    }
    var i0_4 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().NEG_Y_AXIS);
    var i1_4 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f$Companion_getInstance().NEG_Y_AXIS);
    var i2_4 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x + props.size.x, props.origin.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().NEG_Y_AXIS);
    var i3_4 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(props.origin.x, props.origin.y, props.origin.z + props.size.z), Vec3f$Companion_getInstance().NEG_Y_AXIS);
    this.meshData.addTriIndices_qt1dr2$(i0_4, i1_4, i2_4);
    this.meshData.addTriIndices_qt1dr2$(i0_4, i2_4, i3_4);
    this.color = c_4;
  };
  MeshBuilder.prototype.cylinder_z0gg86$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.cylinder_z0gg86$', function (props) {
    props(this.cylinderProps.defaults());
    this.cylinder_tnt2h$(this.cylinderProps);
  });
  MeshBuilder.prototype.cylinder_tnt2h$ = function (props) {
    var tmp$;
    props.fixNegativeSize();
    this.transform.push();
    this.translate_czzhiu$(props.origin);
    this.rotate_ad55pp$(90.0, Vec3f$Companion_getInstance().X_AXIS);
    var $receiver = this.circleProps.defaults();
    $receiver.steps = props.steps;
    $receiver.radius = props.bottomRadius;
    this.circle_59f34t$(this.circleProps);
    this.transform.pop();
    this.transform.push();
    this.translate_y2kzbl$(props.origin.x, props.origin.y + props.height, props.origin.z);
    this.rotate_ad55pp$(-90.0, Vec3f$Companion_getInstance().X_AXIS);
    var $receiver_0 = this.circleProps.defaults();
    $receiver_0.steps = props.steps;
    $receiver_0.radius = props.topRadius;
    this.circle_59f34t$(this.circleProps);
    this.transform.pop();
    var dr = props.bottomRadius - props.topRadius;
    var x = dr * dr + props.height * props.height;
    var x_0 = dr / Math_0.sqrt(x);
    var nrmAng = 90.0 - Math_0.acos(x_0) * package$math.RAD_2_DEG;
    var i0 = 0;
    var i1 = 0;
    tmp$ = props.steps;
    for (var i = 0; i <= tmp$; i++) {
      var x_1 = i * math.PI * 2 / props.steps;
      var c = Math_0.cos(x_1);
      var x_2 = i * math.PI * 2 / props.steps;
      var s = Math_0.sin(x_2);
      var px2 = props.origin.x + props.bottomRadius * c;
      var pz2 = props.origin.z + props.bottomRadius * s;
      var px3 = props.origin.x + props.topRadius * c;
      var pz3 = props.origin.z + props.topRadius * s;
      this.tmpNrm_5ueby3$_0.set_y2kzbl$(c, 0.0, s).rotate_7b5o5w$(nrmAng, s, 0.0, c);
      var i2 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(px2, props.origin.y, pz2), this.tmpNrm_5ueby3$_0);
      var i3 = this.vertex_n440gp$(this.tmpPos_5ud41y$_0.set_y2kzbl$(px3, props.origin.y + props.height, pz3), this.tmpNrm_5ueby3$_0);
      if (i > 0) {
        this.meshData.addTriIndices_qt1dr2$(i0, i1, i2);
        this.meshData.addTriIndices_qt1dr2$(i1, i3, i2);
      }
      i0 = i2;
      i1 = i3;
    }
  };
  MeshBuilder.prototype.text_neimsr$ = defineInlineFunction('kool.de.fabmax.kool.util.MeshBuilder.text_neimsr$', function (font, props) {
    this.textProps.defaults();
    this.textProps.font = font;
    props(this.textProps);
    this.text_lis6zk$(this.textProps);
  });
  MeshBuilder.prototype.text_lis6zk$ = function (props) {
    this.transform.push();
    var tmp$;
    this.translate_czzhiu$(props.origin);
    var advanced = {v: 0.0};
    tmp$ = iterator(props.text);
    while (tmp$.hasNext()) {
      var c = unboxChar(tmp$.next());
      if (c === 10) {
        this.translate_y2kzbl$(0.0, -props.font.lineSpace, 0.0);
        advanced.v = 0.0;
      }
      var metrics = props.font.charMap.get_11rb$(toBoxedChar(c));
      if (metrics != null) {
        var $receiver = this.rectProps.defaults();
        $receiver.origin.set_y2kzbl$(advanced.v - metrics.xOffset, metrics.yBaseline - metrics.height, 0.0);
        $receiver.size.set_dleff0$(metrics.width, metrics.height);
        $receiver.texCoordUpperLeft.set_czzhjp$(metrics.uvMin);
        $receiver.texCoordUpperRight.set_dleff0$(metrics.uvMax.x, metrics.uvMin.y);
        $receiver.texCoordLowerLeft.set_dleff0$(metrics.uvMin.x, metrics.uvMax.y);
        $receiver.texCoordLowerRight.set_czzhjp$(metrics.uvMax);
        this.rect_e5k3t5$(this.rectProps);
        advanced.v += metrics.advance;
      }
    }
    this.transform.pop();
  };
  MeshBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MeshBuilder',
    interfaces: []
  };
  function CircleProps() {
    this.radius = 1.0;
    this.steps = 20;
    this.center = MutableVec3f_init();
    this.startDeg = 0.0;
    this.sweepDeg = 360.0;
    this.uvCenter = MutableVec2f_init();
    this.uvRadius = 0.0;
  }
  CircleProps.prototype.defaults = function () {
    this.radius = 1.0;
    this.steps = 20;
    this.center.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.startDeg = 0.0;
    this.sweepDeg = 360.0;
    this.zeroTexCoords();
    return this;
  };
  CircleProps.prototype.zeroTexCoords = function () {
    this.uvCenter.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
    this.uvRadius = 0.0;
  };
  CircleProps.prototype.fullTexCoords = function () {
    this.uvCenter.set_dleff0$(0.5, 0.5);
    this.uvRadius = 0.5;
  };
  CircleProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CircleProps',
    interfaces: []
  };
  function SphereProps() {
    this.radius = 1.0;
    this.steps = 20;
    this.center = MutableVec3f_init();
    this.uv_0 = MutableVec2f_init();
    this.texCoordGenerator = SphereProps$texCoordGenerator$lambda(this);
  }
  SphereProps.prototype.defaultTexCoordGenerator_0 = function (theta, phi) {
    return this.uv_0.set_dleff0$(phi / (math.PI * 2.0), theta / math.PI);
  };
  function SphereProps$defaults$lambda(this$SphereProps) {
    return function (t, p) {
      return this$SphereProps.defaultTexCoordGenerator_0(t, p);
    };
  }
  SphereProps.prototype.defaults = function () {
    this.radius = 1.0;
    this.steps = 20;
    this.center.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.texCoordGenerator = SphereProps$defaults$lambda(this);
    return this;
  };
  function SphereProps$texCoordGenerator$lambda(this$SphereProps) {
    return function (t, p) {
      return this$SphereProps.defaultTexCoordGenerator_0(t, p);
    };
  }
  SphereProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SphereProps',
    interfaces: []
  };
  function RectProps() {
    this.cornerRadius = 0.0;
    this.cornerSteps = 8;
    this.origin = MutableVec3f_init();
    this.size = MutableVec2f_init();
    this.texCoordUpperLeft = MutableVec2f_init();
    this.texCoordUpperRight = MutableVec2f_init();
    this.texCoordLowerLeft = MutableVec2f_init();
    this.texCoordLowerRight = MutableVec2f_init();
  }
  Object.defineProperty(RectProps.prototype, 'width', {
    get: function () {
      return this.size.x;
    },
    set: function (value) {
      this.size.x = value;
    }
  });
  Object.defineProperty(RectProps.prototype, 'height', {
    get: function () {
      return this.size.y;
    },
    set: function (value) {
      this.size.y = value;
    }
  });
  RectProps.prototype.fixNegativeSize = function () {
    if (this.size.x < 0) {
      this.origin.x = this.origin.x + this.size.x;
      this.size.x = -this.size.x;
    }
    if (this.size.y < 0) {
      this.origin.y = this.origin.y + this.size.y;
      this.size.y = -this.size.y;
    }
  };
  RectProps.prototype.zeroTexCoords = function () {
    this.texCoordUpperLeft.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
    this.texCoordUpperRight.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
    this.texCoordLowerLeft.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
    this.texCoordLowerRight.set_czzhjp$(Vec2f$Companion_getInstance().ZERO);
  };
  RectProps.prototype.fullTexCoords = function () {
    this.texCoordUpperLeft.set_dleff0$(0.0, 0.0);
    this.texCoordUpperRight.set_dleff0$(1.0, 0.0);
    this.texCoordLowerLeft.set_dleff0$(0.0, 1.0);
    this.texCoordLowerRight.set_dleff0$(1.0, 1.0);
  };
  RectProps.prototype.defaults = function () {
    this.cornerRadius = 0.0;
    this.cornerSteps = 8;
    this.origin.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.size.set_dleff0$(1.0, 1.0);
    this.zeroTexCoords();
    return this;
  };
  RectProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RectProps',
    interfaces: []
  };
  function CubeProps() {
    this.origin = MutableVec3f_init();
    this.size = MutableVec3f_init();
    this.topColor = null;
    this.bottomColor = null;
    this.leftColor = null;
    this.rightColor = null;
    this.frontColor = null;
    this.backColor = null;
  }
  Object.defineProperty(CubeProps.prototype, 'width', {
    get: function () {
      return this.size.x;
    },
    set: function (value) {
      this.size.x = value;
    }
  });
  Object.defineProperty(CubeProps.prototype, 'height', {
    get: function () {
      return this.size.y;
    },
    set: function (value) {
      this.size.y = value;
    }
  });
  Object.defineProperty(CubeProps.prototype, 'depth', {
    get: function () {
      return this.size.z;
    },
    set: function (value) {
      this.size.z = value;
    }
  });
  CubeProps.prototype.fixNegativeSize = function () {
    if (this.size.x < 0) {
      this.origin.x = this.origin.x + this.size.x;
      this.size.x = -this.size.x;
    }
    if (this.size.y < 0) {
      this.origin.y = this.origin.y + this.size.y;
      this.size.y = -this.size.y;
    }
    if (this.size.z < 0) {
      this.origin.z = this.origin.z + this.size.z;
      this.size.z = -this.size.z;
    }
  };
  CubeProps.prototype.centerOrigin = function () {
    this.origin.x = this.origin.x - this.size.x / 2.0;
    this.origin.y = this.origin.y - this.size.y / 2.0;
    this.origin.z = this.origin.z - this.size.z / 2.0;
  };
  CubeProps.prototype.colorCube = function () {
    this.frontColor = Color$Companion_getInstance().RED;
    this.rightColor = Color$Companion_getInstance().GREEN;
    this.backColor = Color$Companion_getInstance().BLUE;
    this.leftColor = Color$Companion_getInstance().YELLOW;
    this.topColor = Color$Companion_getInstance().MAGENTA;
    this.bottomColor = Color$Companion_getInstance().CYAN;
  };
  CubeProps.prototype.defaults = function () {
    this.size.x = 1.0;
    this.size.y = 1.0;
    this.size.z = 1.0;
    this.origin.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    this.topColor = null;
    this.bottomColor = null;
    this.leftColor = null;
    this.rightColor = null;
    this.frontColor = null;
    this.backColor = null;
    return this;
  };
  CubeProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CubeProps',
    interfaces: []
  };
  function CylinderProps() {
    this.bottomRadius = 1.0;
    this.topRadius = 1.0;
    this.steps = 20;
    this.height = 1.0;
    this.origin = MutableVec3f_init();
  }
  CylinderProps.prototype.defaults = function () {
    this.bottomRadius = 1.0;
    this.topRadius = 1.0;
    this.steps = 20;
    this.height = 1.0;
    this.origin.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    return this;
  };
  CylinderProps.prototype.fixNegativeSize = function () {
    if (this.height < 0) {
      this.origin.y = this.origin.y + this.height;
      this.height = -this.height;
    }
  };
  CylinderProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CylinderProps',
    interfaces: []
  };
  function TextProps() {
    this.text = '';
    this.font = Font$Companion_getInstance().DEFAULT_FONT;
    this.origin = MutableVec3f_init();
  }
  TextProps.prototype.defaults = function () {
    this.text = '';
    this.font = Font$Companion_getInstance().DEFAULT_FONT;
    this.origin.set_czzhiu$(Vec3f$Companion_getInstance().ZERO);
    return this;
  };
  TextProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextProps',
    interfaces: []
  };
  function pointMesh(name, block) {
    if (name === void 0)
      name = null;
    var $receiver = new PointMesh(void 0, name);
    block($receiver);
    return $receiver;
  }
  function PointMesh(data, name) {
    if (data === void 0)
      data = MeshData_init([Attribute$Companion_getInstance().POSITIONS, Attribute$Companion_getInstance().COLORS]);
    if (name === void 0)
      name = null;
    Mesh.call(this, data, name);
    this.primitiveType = GL_POINTS;
    this.shader = basicPointShader(PointMesh_init$lambda);
  }
  Object.defineProperty(PointMesh.prototype, 'pointSize', {
    get: function () {
      var tmp$;
      return (Kotlin.isType(tmp$ = this.shader, BasicPointShader) ? tmp$ : throwCCE()).pointSize;
    },
    set: function (value) {
      var tmp$;
      (Kotlin.isType(tmp$ = this.shader, BasicPointShader) ? tmp$ : throwCCE()).pointSize = value;
    }
  });
  PointMesh.prototype.addPoint_hvwyd1$ = function (block) {
    var idx = this.meshData.addVertex_hvwyd1$(block);
    this.meshData.addIndex_za3lpa$(idx);
    return idx;
  };
  PointMesh.prototype.addPoint_4sqmhu$ = function (position, color) {
    var idx = this.meshData.addVertex_lv7vxo$(position, null, color, null);
    this.meshData.addIndex_za3lpa$(idx);
    return idx;
  };
  function PointMesh_init$lambda($receiver) {
    $receiver.colorModel = ColorModel$VERTEX_COLOR_getInstance();
    $receiver.lightModel = LightModel$NO_LIGHTING_getInstance();
    return Unit;
  }
  PointMesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PointMesh',
    interfaces: [Mesh]
  };
  function Property(name, value, onChange) {
    this.name = name;
    this.onChange_0 = onChange;
    this.value_7dkhmp$_0 = value;
    this.valueChanged_5x454x$_0 = true;
  }
  Object.defineProperty(Property.prototype, 'value', {
    get: function () {
      return this.value_7dkhmp$_0;
    },
    set: function (value) {
      this.value_7dkhmp$_0 = value;
    }
  });
  Object.defineProperty(Property.prototype, 'clear', {
    get: function () {
      this.valueChanged = false;
      return this.value;
    }
  });
  Object.defineProperty(Property.prototype, 'valueChanged', {
    get: function () {
      return this.valueChanged_5x454x$_0;
    },
    set: function (valueChanged) {
      this.valueChanged_5x454x$_0 = valueChanged;
    }
  });
  Property.prototype.getValue_n5byny$ = function (thisRef, property) {
    return this.value;
  };
  Property.prototype.setValue_sq4zib$ = function (thisRef, property, value) {
    if (!equals(value, this.value)) {
      this.value = value;
      this.valueChanged = true;
    }
  };
  Property.prototype.copy_lshn67$ = function (other, maintainChangeFlag) {
    var tmp$;
    if (maintainChangeFlag) {
      this.valueChanged = other.valueChanged;
    }
     else {
      this.valueChanged = !equals(this.value, other.value);
    }
    this.value = (tmp$ = other.value) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  Property.prototype.applyIfChanged = function () {
    if (this.valueChanged) {
      this.onChange_0(this);
    }
  };
  Property.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Property',
    interfaces: []
  };
  function ShadowMap(near, far, texSize) {
    ShadowMap$Companion_getInstance();
    if (near === void 0)
      near = 0.0;
    if (far === void 0)
      far = 1.0;
    if (texSize === void 0)
      texSize = 1024;
    this.near = near;
    this.far = far;
    this.texSize = texSize;
    this.depthCam_0 = new OrthographicCamera();
    this.depthMvpMat_0 = new Mat4f();
    this.depthView_0 = new Mat4f();
    this.nearPlane_0 = new FrustumPlane();
    this.farPlane_0 = new FrustumPlane();
    this.bounds_0 = new BoundingBox();
    this.tmpVec4_0 = MutableVec4f_init();
    this.fbo_0 = (new Framebuffer(this.texSize, this.texSize)).withDepth();
    this.depthMvp = createFloat32Buffer(16);
    this.clipSpaceFarZ_3g3t06$_0 = 0.0;
  }
  Object.defineProperty(ShadowMap.prototype, 'depthTexture', {
    get: function () {
      return this.fbo_0.depthAttachment;
    }
  });
  Object.defineProperty(ShadowMap.prototype, 'clipSpaceFarZ', {
    get: function () {
      return this.clipSpaceFarZ_3g3t06$_0;
    },
    set: function (clipSpaceFarZ) {
      this.clipSpaceFarZ_3g3t06$_0 = clipSpaceFarZ;
    }
  });
  ShadowMap.prototype.renderShadowMap_jkftm7$ = function (nodeToRender, ctx) {
    var tmp$;
    if (!glCapabilities.depthTextures) {
      return;
    }
    tmp$ = nodeToRender.scene;
    if (tmp$ == null) {
      return;
    }
    var scene = tmp$;
    var camera = scene.camera;
    this.depthCam_0.position.set_czzhiu$(scene.light.direction);
    this.depthCam_0.lookAt.set_y2kzbl$(0.0, 0.0, 0.0);
    this.depthView_0.setLookAt_n440fu$(this.depthCam_0.position, this.depthCam_0.lookAt, this.depthCam_0.up);
    camera.computeFrustumPlane_jwr40o$(this.near, this.nearPlane_0);
    camera.computeFrustumPlane_jwr40o$(this.far, this.farPlane_0);
    this.clipSpaceFarZ = camera.project_2gj7bz$(this.farPlane_0.upperLeft, this.tmpVec4_0).z;
    transform(this.depthView_0, this.nearPlane_0);
    transform(this.depthView_0, this.farPlane_0);
    setPlanes(this.bounds_0, this.nearPlane_0, this.farPlane_0);
    this.depthCam_0.left = this.bounds_0.min.x;
    this.depthCam_0.right = this.bounds_0.max.x;
    this.depthCam_0.bottom = this.bounds_0.min.y;
    this.depthCam_0.top = this.bounds_0.max.y;
    this.depthCam_0.near = -this.bounds_0.max.z - 10;
    this.depthCam_0.far = -this.bounds_0.min.z;
    this.fbo_0.bind_evfofk$(ctx);
    glClear(GL_DEPTH_BUFFER_BIT);
    ctx.mvpState.pushMatrices();
    ctx.mvpState.projMatrix.setIdentity();
    ctx.mvpState.viewMatrix.setIdentity();
    ctx.mvpState.modelMatrix.setIdentity();
    this.depthCam_0.updateCamera_evfofk$(ctx);
    ShadowMap$Companion_getInstance().BIAS_MATRIX_0.mul_93v2ma$(ctx.mvpState.mvpMatrix, this.depthMvpMat_0).toBuffer_he122g$(this.depthMvp);
    var prevRenderPass = ctx.renderPass;
    ctx.renderPass = RenderPass$SHADOW_getInstance();
    scene.camera = this.depthCam_0;
    nodeToRender.render_evfofk$(ctx);
    scene.camera = camera;
    ctx.renderPass = prevRenderPass;
    ctx.mvpState.popMatrices();
    ctx.mvpState.update_evfofk$(ctx);
    this.fbo_0.unbind_evfofk$(ctx);
  };
  ShadowMap.prototype.dispose_evfofk$ = function (ctx) {
    this.fbo_0.delete_evfofk$(ctx);
  };
  function ShadowMap$Companion() {
    ShadowMap$Companion_instance = this;
    this.BIAS_MATRIX_0 = new Mat4f();
    this.BIAS_MATRIX_0.setIdentity();
    this.BIAS_MATRIX_0.translate_y2kzbl$(0.5, 0.5, 0.5);
    this.BIAS_MATRIX_0.scale_y2kzbl$(0.5, 0.5, 0.5);
  }
  ShadowMap$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ShadowMap$Companion_instance = null;
  function ShadowMap$Companion_getInstance() {
    if (ShadowMap$Companion_instance === null) {
      new ShadowMap$Companion();
    }
    return ShadowMap$Companion_instance;
  }
  ShadowMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShadowMap',
    interfaces: []
  };
  function CascadedShadowMap(subMaps) {
    CascadedShadowMap$Companion_getInstance();
    this.subMaps = subMaps;
    this.shadowMvp = createFloat32Buffer(16 * this.subMaps.length | 0);
  }
  CascadedShadowMap.prototype.renderShadowMap_jkftm7$ = function (nodeToRender, ctx) {
    var tmp$;
    tmp$ = this.subMaps;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.subMaps[i].renderShadowMap_jkftm7$(nodeToRender, ctx);
      this.shadowMvp.put_axfmcw$(this.subMaps[i].depthMvp);
    }
    this.shadowMvp.flip();
  };
  CascadedShadowMap.prototype.dispose_evfofk$ = function (ctx) {
    var tmp$;
    tmp$ = this.subMaps;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.subMaps[i].dispose_evfofk$(ctx);
    }
  };
  function CascadedShadowMap$Companion() {
    CascadedShadowMap$Companion_instance = this;
  }
  CascadedShadowMap$Companion.prototype.defaultCascadedShadowMap3 = function () {
    var subMaps = [new ShadowMap(0.0, 0.1), new ShadowMap(0.1, 0.25), new ShadowMap(0.25, 1.0)];
    return new CascadedShadowMap(subMaps);
  };
  CascadedShadowMap$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var CascadedShadowMap$Companion_instance = null;
  function CascadedShadowMap$Companion_getInstance() {
    if (CascadedShadowMap$Companion_instance === null) {
      new CascadedShadowMap$Companion();
    }
    return CascadedShadowMap$Companion_instance;
  }
  CascadedShadowMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CascadedShadowMap',
    interfaces: []
  };
  function transform($receiver, plane) {
    $receiver.transform_w1lst9$(plane.upperLeft);
    $receiver.transform_w1lst9$(plane.upperRight);
    $receiver.transform_w1lst9$(plane.lowerLeft);
    $receiver.transform_w1lst9$(plane.lowerRight);
  }
  function setPlanes($receiver, near, far) {
    $receiver.batchUpdate = true;
    $receiver.clear();
    $receiver.add_czzhiu$(near.upperLeft);
    $receiver.add_czzhiu$(near.upperRight);
    $receiver.add_czzhiu$(near.lowerLeft);
    $receiver.add_czzhiu$(near.lowerRight);
    $receiver.add_czzhiu$(far.upperLeft);
    $receiver.add_czzhiu$(far.upperRight);
    $receiver.add_czzhiu$(far.lowerLeft);
    $receiver.add_czzhiu$(far.lowerRight);
    $receiver.batchUpdate = false;
  }
  function UniqueId() {
    UniqueId_instance = this;
    this.nextId_0 = Kotlin.Long.ONE;
    this.idLock_0 = new Any();
  }
  UniqueId.prototype.nextId = function () {
    var id = {v: Kotlin.Long.ZERO};
    this.idLock_0;
    id.v = (this.nextId_0 = this.nextId_0.inc(), this.nextId_0);
    return id.v;
  };
  UniqueId.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'UniqueId',
    interfaces: []
  };
  var UniqueId_instance = null;
  function UniqueId_getInstance() {
    if (UniqueId_instance === null) {
      new UniqueId();
    }
    return UniqueId_instance;
  }
  function AnimationData(name, duration, channels) {
    AnimationData$Companion_getInstance();
    this.name = name;
    this.duration = duration;
    this.channels = channels;
  }
  AnimationData.prototype.getAnimation_wev6wz$ = function (nodes) {
    var anim = new Animation(this.duration);
    var tmp$;
    tmp$ = this.channels.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var node = nodes.get_11rb$(element.name);
      if (node != null) {
        var $receiver = anim.channels;
        var element_0 = element.getNodeAnimation_dm1hwa$(node);
        $receiver.add_11rb$(element_0);
      }
    }
    return anim;
  };
  function AnimationData$Companion() {
    AnimationData$Companion_instance = this;
  }
  AnimationData$Companion.prototype.serializer = function () {
    return _.de.fabmax.kool.util.serialization.AnimationData.$serializer;
  };
  AnimationData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var AnimationData$Companion_instance = null;
  function AnimationData$Companion_getInstance() {
    if (AnimationData$Companion_instance === null) {
      new AnimationData$Companion();
    }
    return AnimationData$Companion_instance;
  }
  function AnimationData$$serializer() {
    this.serialClassDesc_lcp80u$_0 = new SerialClassDescImpl('de.fabmax.kool.util.serialization.AnimationData');
    this.serialClassDesc.addElement_61zpoe$('name');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(1));
    this.serialClassDesc.addElement_61zpoe$('duration');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(2));
    this.serialClassDesc.addElement_61zpoe$('channels');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(3));
    AnimationData$$serializer_instance = this;
  }
  Object.defineProperty(AnimationData$$serializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_lcp80u$_0;
    }
  });
  AnimationData$$serializer.prototype.save_ejfkry$ = function (output_0, obj) {
    var output = output_0.writeBegin_276rha$(this.serialClassDesc, []);
    output.writeStringElementValue_k4mjep$(this.serialClassDesc, 0, obj.name);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 1, obj.duration);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.NodeAnimationData.$serializer), obj.channels);
    output.writeEnd_f6e2p$(this.serialClassDesc);
  };
  AnimationData$$serializer.prototype.load_ljkqvg$ = function (input_0) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2;
    var input = input_0.readBegin_276rha$(this.serialClassDesc, []);
    loopLabel: while (true) {
      index = input.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.readStringElementValue_xvmgof$(this.serialClassDesc, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.NodeAnimationData.$serializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.NodeAnimationData.$serializer), local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException(index);
      }
    }
    input.readEnd_f6e2p$(this.serialClassDesc);
    return AnimationData_init(bitMask0, local0, local1, local2, null);
  };
  AnimationData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [KSerializer]
  };
  var AnimationData$$serializer_instance = null;
  function AnimationData$$serializer_getInstance() {
    if (AnimationData$$serializer_instance === null) {
      new AnimationData$$serializer();
    }
    return AnimationData$$serializer_instance;
  }
  function AnimationData_init(seen, name, duration, channels, serializationConstructorMarker) {
    var $this = Object.create(_.de.fabmax.kool.util.serialization.AnimationData.prototype);
    if ((seen & 1) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('name');
    else
      $this.name = name;
    if ((seen & 2) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('duration');
    else
      $this.duration = duration;
    if ((seen & 4) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('channels');
    else
      $this.channels = channels;
    return $this;
  }
  AnimationData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AnimationData',
    interfaces: []
  };
  AnimationData.prototype.component1 = function () {
    return this.name;
  };
  AnimationData.prototype.component2 = function () {
    return this.duration;
  };
  AnimationData.prototype.component3 = function () {
    return this.channels;
  };
  AnimationData.prototype.copy_2yxch5$ = function (name, duration, channels) {
    return new AnimationData(name === void 0 ? this.name : name, duration === void 0 ? this.duration : duration, channels === void 0 ? this.channels : channels);
  };
  AnimationData.prototype.toString = function () {
    return 'AnimationData(name=' + Kotlin.toString(this.name) + (', duration=' + Kotlin.toString(this.duration)) + (', channels=' + Kotlin.toString(this.channels)) + ')';
  };
  AnimationData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.duration) | 0;
    result = result * 31 + Kotlin.hashCode(this.channels) | 0;
    return result;
  };
  AnimationData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.duration, other.duration) && Kotlin.equals(this.channels, other.channels)))));
  };
  function NodeAnimationData(name, positionKeys, rotationKeys, scalingKeys) {
    NodeAnimationData$Companion_getInstance();
    this.name = name;
    this.positionKeys = positionKeys;
    this.rotationKeys = rotationKeys;
    this.scalingKeys = scalingKeys;
  }
  NodeAnimationData.prototype.getNodeAnimation_dm1hwa$ = function (node) {
    var nodeAnim = new NodeAnimation(this.name, node);
    var tmp$;
    tmp$ = this.positionKeys.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var $receiver = nodeAnim.positionKeys;
      var element_0 = element.getPositionKey();
      $receiver.add_11rb$(element_0);
    }
    var tmp$_0;
    tmp$_0 = this.rotationKeys.iterator();
    while (tmp$_0.hasNext()) {
      var element_1 = tmp$_0.next();
      var $receiver_0 = nodeAnim.rotationKeys;
      var element_2 = element_1.getRotationKey();
      $receiver_0.add_11rb$(element_2);
    }
    var tmp$_1;
    tmp$_1 = this.scalingKeys.iterator();
    while (tmp$_1.hasNext()) {
      var element_3 = tmp$_1.next();
      var $receiver_1 = nodeAnim.scalingKeys;
      var element_4 = element_3.getScalingKey();
      $receiver_1.add_11rb$(element_4);
    }
    return nodeAnim;
  };
  function NodeAnimationData$Companion() {
    NodeAnimationData$Companion_instance = this;
  }
  NodeAnimationData$Companion.prototype.serializer = function () {
    return _.de.fabmax.kool.util.serialization.NodeAnimationData.$serializer;
  };
  NodeAnimationData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var NodeAnimationData$Companion_instance = null;
  function NodeAnimationData$Companion_getInstance() {
    if (NodeAnimationData$Companion_instance === null) {
      new NodeAnimationData$Companion();
    }
    return NodeAnimationData$Companion_instance;
  }
  function NodeAnimationData$$serializer() {
    this.serialClassDesc_vnv9c0$_0 = new SerialClassDescImpl('de.fabmax.kool.util.serialization.NodeAnimationData');
    this.serialClassDesc.addElement_61zpoe$('name');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(1));
    this.serialClassDesc.addElement_61zpoe$('positionKeys');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(2));
    this.serialClassDesc.addElement_61zpoe$('rotationKeys');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(3));
    this.serialClassDesc.addElement_61zpoe$('scalingKeys');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(4));
    NodeAnimationData$$serializer_instance = this;
  }
  Object.defineProperty(NodeAnimationData$$serializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_vnv9c0$_0;
    }
  });
  NodeAnimationData$$serializer.prototype.save_ejfkry$ = function (output_0, obj) {
    var output = output_0.writeBegin_276rha$(this.serialClassDesc, []);
    output.writeStringElementValue_k4mjep$(this.serialClassDesc, 0, obj.name);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 1, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer), obj.positionKeys);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec4KeyData.$serializer), obj.rotationKeys);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer), obj.scalingKeys);
    output.writeEnd_f6e2p$(this.serialClassDesc);
  };
  NodeAnimationData$$serializer.prototype.load_ljkqvg$ = function (input_0) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3;
    var input = input_0.readBegin_276rha$(this.serialClassDesc, []);
    loopLabel: while (true) {
      index = input.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.readStringElementValue_xvmgof$(this.serialClassDesc, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 1, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 1, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec4KeyData.$serializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec4KeyData.$serializer), local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = (bitMask0 & 8) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer), local3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException(index);
      }
    }
    input.readEnd_f6e2p$(this.serialClassDesc);
    return NodeAnimationData_init(bitMask0, local0, local1, local2, local3, null);
  };
  NodeAnimationData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [KSerializer]
  };
  var NodeAnimationData$$serializer_instance = null;
  function NodeAnimationData$$serializer_getInstance() {
    if (NodeAnimationData$$serializer_instance === null) {
      new NodeAnimationData$$serializer();
    }
    return NodeAnimationData$$serializer_instance;
  }
  function NodeAnimationData_init(seen, name, positionKeys, rotationKeys, scalingKeys, serializationConstructorMarker) {
    var $this = Object.create(_.de.fabmax.kool.util.serialization.NodeAnimationData.prototype);
    if ((seen & 1) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('name');
    else
      $this.name = name;
    if ((seen & 2) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('positionKeys');
    else
      $this.positionKeys = positionKeys;
    if ((seen & 4) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('rotationKeys');
    else
      $this.rotationKeys = rotationKeys;
    if ((seen & 8) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('scalingKeys');
    else
      $this.scalingKeys = scalingKeys;
    return $this;
  }
  NodeAnimationData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NodeAnimationData',
    interfaces: []
  };
  NodeAnimationData.prototype.component1 = function () {
    return this.name;
  };
  NodeAnimationData.prototype.component2 = function () {
    return this.positionKeys;
  };
  NodeAnimationData.prototype.component3 = function () {
    return this.rotationKeys;
  };
  NodeAnimationData.prototype.component4 = function () {
    return this.scalingKeys;
  };
  NodeAnimationData.prototype.copy_2njh0z$ = function (name, positionKeys, rotationKeys, scalingKeys) {
    return new NodeAnimationData(name === void 0 ? this.name : name, positionKeys === void 0 ? this.positionKeys : positionKeys, rotationKeys === void 0 ? this.rotationKeys : rotationKeys, scalingKeys === void 0 ? this.scalingKeys : scalingKeys);
  };
  NodeAnimationData.prototype.toString = function () {
    return 'NodeAnimationData(name=' + Kotlin.toString(this.name) + (', positionKeys=' + Kotlin.toString(this.positionKeys)) + (', rotationKeys=' + Kotlin.toString(this.rotationKeys)) + (', scalingKeys=' + Kotlin.toString(this.scalingKeys)) + ')';
  };
  NodeAnimationData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.positionKeys) | 0;
    result = result * 31 + Kotlin.hashCode(this.rotationKeys) | 0;
    result = result * 31 + Kotlin.hashCode(this.scalingKeys) | 0;
    return result;
  };
  NodeAnimationData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.positionKeys, other.positionKeys) && Kotlin.equals(this.rotationKeys, other.rotationKeys) && Kotlin.equals(this.scalingKeys, other.scalingKeys)))));
  };
  function Vec3KeyData(time, x, y, z) {
    Vec3KeyData$Companion_getInstance();
    this.time = time;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  Vec3KeyData.prototype.getPositionKey = function () {
    return new PositionKey(this.time, new Vec3f(this.x, this.y, this.z));
  };
  Vec3KeyData.prototype.getScalingKey = function () {
    return new ScalingKey(this.time, new Vec3f(this.x, this.y, this.z));
  };
  function Vec3KeyData$Companion() {
    Vec3KeyData$Companion_instance = this;
  }
  Vec3KeyData$Companion.prototype.serializer = function () {
    return _.de.fabmax.kool.util.serialization.Vec3KeyData.$serializer;
  };
  Vec3KeyData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vec3KeyData$Companion_instance = null;
  function Vec3KeyData$Companion_getInstance() {
    if (Vec3KeyData$Companion_instance === null) {
      new Vec3KeyData$Companion();
    }
    return Vec3KeyData$Companion_instance;
  }
  function Vec3KeyData$$serializer() {
    this.serialClassDesc_m4yowu$_0 = new SerialClassDescImpl('de.fabmax.kool.util.serialization.Vec3KeyData');
    this.serialClassDesc.addElement_61zpoe$('time');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(1));
    this.serialClassDesc.addElement_61zpoe$('x');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(2));
    this.serialClassDesc.addElement_61zpoe$('y');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(3));
    this.serialClassDesc.addElement_61zpoe$('z');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(4));
    Vec3KeyData$$serializer_instance = this;
  }
  Object.defineProperty(Vec3KeyData$$serializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_m4yowu$_0;
    }
  });
  Vec3KeyData$$serializer.prototype.save_ejfkry$ = function (output_0, obj) {
    var output = output_0.writeBegin_276rha$(this.serialClassDesc, []);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 0, obj.time);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 1, obj.x);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 2, obj.y);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 3, obj.z);
    output.writeEnd_f6e2p$(this.serialClassDesc);
  };
  Vec3KeyData$$serializer.prototype.load_ljkqvg$ = function (input_0) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3;
    var input = input_0.readBegin_276rha$(this.serialClassDesc, []);
    loopLabel: while (true) {
      index = input.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException(index);
      }
    }
    input.readEnd_f6e2p$(this.serialClassDesc);
    return Vec3KeyData_init(bitMask0, local0, local1, local2, local3, null);
  };
  Vec3KeyData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [KSerializer]
  };
  var Vec3KeyData$$serializer_instance = null;
  function Vec3KeyData$$serializer_getInstance() {
    if (Vec3KeyData$$serializer_instance === null) {
      new Vec3KeyData$$serializer();
    }
    return Vec3KeyData$$serializer_instance;
  }
  function Vec3KeyData_init(seen, time, x, y, z, serializationConstructorMarker) {
    var $this = Object.create(_.de.fabmax.kool.util.serialization.Vec3KeyData.prototype);
    if ((seen & 1) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('time');
    else
      $this.time = time;
    if ((seen & 2) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('x');
    else
      $this.x = x;
    if ((seen & 4) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('y');
    else
      $this.y = y;
    if ((seen & 8) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('z');
    else
      $this.z = z;
    return $this;
  }
  Vec3KeyData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec3KeyData',
    interfaces: []
  };
  Vec3KeyData.prototype.component1 = function () {
    return this.time;
  };
  Vec3KeyData.prototype.component2 = function () {
    return this.x;
  };
  Vec3KeyData.prototype.component3 = function () {
    return this.y;
  };
  Vec3KeyData.prototype.component4 = function () {
    return this.z;
  };
  Vec3KeyData.prototype.copy_7b5o5w$ = function (time, x, y, z) {
    return new Vec3KeyData(time === void 0 ? this.time : time, x === void 0 ? this.x : x, y === void 0 ? this.y : y, z === void 0 ? this.z : z);
  };
  Vec3KeyData.prototype.toString = function () {
    return 'Vec3KeyData(time=' + Kotlin.toString(this.time) + (', x=' + Kotlin.toString(this.x)) + (', y=' + Kotlin.toString(this.y)) + (', z=' + Kotlin.toString(this.z)) + ')';
  };
  Vec3KeyData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.time) | 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.z) | 0;
    return result;
  };
  Vec3KeyData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.time, other.time) && Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.z, other.z)))));
  };
  function Vec4KeyData(time, x, y, z, w) {
    Vec4KeyData$Companion_getInstance();
    this.time = time;
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  Vec4KeyData.prototype.getRotationKey = function () {
    return new RotationKey(this.time, new Vec4f(this.x, this.y, this.z, this.w));
  };
  function Vec4KeyData$Companion() {
    Vec4KeyData$Companion_instance = this;
  }
  Vec4KeyData$Companion.prototype.serializer = function () {
    return _.de.fabmax.kool.util.serialization.Vec4KeyData.$serializer;
  };
  Vec4KeyData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vec4KeyData$Companion_instance = null;
  function Vec4KeyData$Companion_getInstance() {
    if (Vec4KeyData$Companion_instance === null) {
      new Vec4KeyData$Companion();
    }
    return Vec4KeyData$Companion_instance;
  }
  function Vec4KeyData$$serializer() {
    this.serialClassDesc_gomo0t$_0 = new SerialClassDescImpl('de.fabmax.kool.util.serialization.Vec4KeyData');
    this.serialClassDesc.addElement_61zpoe$('time');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(1));
    this.serialClassDesc.addElement_61zpoe$('x');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(2));
    this.serialClassDesc.addElement_61zpoe$('y');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(3));
    this.serialClassDesc.addElement_61zpoe$('z');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(4));
    this.serialClassDesc.addElement_61zpoe$('w');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(5));
    Vec4KeyData$$serializer_instance = this;
  }
  Object.defineProperty(Vec4KeyData$$serializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_gomo0t$_0;
    }
  });
  Vec4KeyData$$serializer.prototype.save_ejfkry$ = function (output_0, obj) {
    var output = output_0.writeBegin_276rha$(this.serialClassDesc, []);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 0, obj.time);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 1, obj.x);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 2, obj.y);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 3, obj.z);
    output.writeFloatElementValue_r1rln8$(this.serialClassDesc, 4, obj.w);
    output.writeEnd_f6e2p$(this.serialClassDesc);
  };
  Vec4KeyData$$serializer.prototype.load_ljkqvg$ = function (input_0) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3
    , local4;
    var input = input_0.readBegin_276rha$(this.serialClassDesc, []);
    loopLabel: while (true) {
      index = input.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case 4:
          local4 = input.readFloatElementValue_xvmgof$(this.serialClassDesc, 4);
          bitMask0 |= 16;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException(index);
      }
    }
    input.readEnd_f6e2p$(this.serialClassDesc);
    return Vec4KeyData_init(bitMask0, local0, local1, local2, local3, local4, null);
  };
  Vec4KeyData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [KSerializer]
  };
  var Vec4KeyData$$serializer_instance = null;
  function Vec4KeyData$$serializer_getInstance() {
    if (Vec4KeyData$$serializer_instance === null) {
      new Vec4KeyData$$serializer();
    }
    return Vec4KeyData$$serializer_instance;
  }
  function Vec4KeyData_init(seen, time, x, y, z, w, serializationConstructorMarker) {
    var $this = Object.create(_.de.fabmax.kool.util.serialization.Vec4KeyData.prototype);
    if ((seen & 1) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('time');
    else
      $this.time = time;
    if ((seen & 2) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('x');
    else
      $this.x = x;
    if ((seen & 4) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('y');
    else
      $this.y = y;
    if ((seen & 8) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('z');
    else
      $this.z = z;
    if ((seen & 16) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('w');
    else
      $this.w = w;
    return $this;
  }
  Vec4KeyData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vec4KeyData',
    interfaces: []
  };
  Vec4KeyData.prototype.component1 = function () {
    return this.time;
  };
  Vec4KeyData.prototype.component2 = function () {
    return this.x;
  };
  Vec4KeyData.prototype.component3 = function () {
    return this.y;
  };
  Vec4KeyData.prototype.component4 = function () {
    return this.z;
  };
  Vec4KeyData.prototype.component5 = function () {
    return this.w;
  };
  Vec4KeyData.prototype.copy_s2l86p$ = function (time, x, y, z, w) {
    return new Vec4KeyData(time === void 0 ? this.time : time, x === void 0 ? this.x : x, y === void 0 ? this.y : y, z === void 0 ? this.z : z, w === void 0 ? this.w : w);
  };
  Vec4KeyData.prototype.toString = function () {
    return 'Vec4KeyData(time=' + Kotlin.toString(this.time) + (', x=' + Kotlin.toString(this.x)) + (', y=' + Kotlin.toString(this.y)) + (', z=' + Kotlin.toString(this.z)) + (', w=' + Kotlin.toString(this.w)) + ')';
  };
  Vec4KeyData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.time) | 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.z) | 0;
    result = result * 31 + Kotlin.hashCode(this.w) | 0;
    return result;
  };
  Vec4KeyData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.time, other.time) && Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.z, other.z) && Kotlin.equals(this.w, other.w)))));
  };
  function BoneData(name, parent, children, offsetMatrix, vertexIds, vertexWeights) {
    BoneData$Companion_getInstance();
    if (children === void 0)
      children = emptyList();
    this.name = name;
    this.parent = parent;
    this.children = children;
    this.offsetMatrix = offsetMatrix;
    this.vertexIds = vertexIds;
    this.vertexWeights = vertexWeights;
  }
  function BoneData$Companion() {
    BoneData$Companion_instance = this;
  }
  BoneData$Companion.prototype.serializer = function () {
    return _.de.fabmax.kool.util.serialization.BoneData.$serializer;
  };
  BoneData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BoneData$Companion_instance = null;
  function BoneData$Companion_getInstance() {
    if (BoneData$Companion_instance === null) {
      new BoneData$Companion();
    }
    return BoneData$Companion_instance;
  }
  function BoneData$$serializer() {
    this.serialClassDesc_t0qu10$_0 = new SerialClassDescImpl('de.fabmax.kool.util.serialization.BoneData');
    this.serialClassDesc.addElement_61zpoe$('name');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(1));
    this.serialClassDesc.addElement_61zpoe$('parent');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(2));
    this.serialClassDesc.addElement_61zpoe$('children');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(3));
    this.serialClassDesc.addElement_61zpoe$('offsetMatrix');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(4));
    this.serialClassDesc.addElement_61zpoe$('vertexIds');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(5));
    this.serialClassDesc.addElement_61zpoe$('vertexWeights');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(6));
    BoneData$$serializer_instance = this;
  }
  Object.defineProperty(BoneData$$serializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_t0qu10$_0;
    }
  });
  BoneData$$serializer.prototype.save_ejfkry$ = function (output_0, obj) {
    var output = output_0.writeBegin_276rha$(this.serialClassDesc, []);
    output.writeStringElementValue_k4mjep$(this.serialClassDesc, 0, obj.name);
    output.writeStringElementValue_k4mjep$(this.serialClassDesc, 1, obj.parent);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.StringSerializer), obj.children);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), obj.offsetMatrix);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 4, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.IntSerializer), obj.vertexIds);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 5, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), obj.vertexWeights);
    output.writeEnd_f6e2p$(this.serialClassDesc);
  };
  BoneData$$serializer.prototype.load_ljkqvg$ = function (input_0) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3
    , local4
    , local5;
    var input = input_0.readBegin_276rha$(this.serialClassDesc, []);
    loopLabel: while (true) {
      index = input.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.readStringElementValue_xvmgof$(this.serialClassDesc, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.readStringElementValue_xvmgof$(this.serialClassDesc, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.StringSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.StringSerializer), local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = (bitMask0 & 8) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), local3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case 4:
          local4 = (bitMask0 & 16) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 4, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.IntSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 4, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.IntSerializer), local4);
          bitMask0 |= 16;
          if (!readAll)
            break;
        case 5:
          local5 = (bitMask0 & 32) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 5, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 5, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), local5);
          bitMask0 |= 32;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException(index);
      }
    }
    input.readEnd_f6e2p$(this.serialClassDesc);
    return BoneData_init(bitMask0, local0, local1, local2, local3, local4, local5, null);
  };
  BoneData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [KSerializer]
  };
  var BoneData$$serializer_instance = null;
  function BoneData$$serializer_getInstance() {
    if (BoneData$$serializer_instance === null) {
      new BoneData$$serializer();
    }
    return BoneData$$serializer_instance;
  }
  function BoneData_init(seen, name, parent, children, offsetMatrix, vertexIds, vertexWeights, serializationConstructorMarker) {
    var $this = Object.create(_.de.fabmax.kool.util.serialization.BoneData.prototype);
    if ((seen & 1) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('name');
    else
      $this.name = name;
    if ((seen & 2) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('parent');
    else
      $this.parent = parent;
    if ((seen & 4) === 0)
      $this.children = emptyList();
    else
      $this.children = children;
    if ((seen & 8) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('offsetMatrix');
    else
      $this.offsetMatrix = offsetMatrix;
    if ((seen & 16) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('vertexIds');
    else
      $this.vertexIds = vertexIds;
    if ((seen & 32) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('vertexWeights');
    else
      $this.vertexWeights = vertexWeights;
    return $this;
  }
  BoneData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BoneData',
    interfaces: []
  };
  BoneData.prototype.component1 = function () {
    return this.name;
  };
  BoneData.prototype.component2 = function () {
    return this.parent;
  };
  BoneData.prototype.component3 = function () {
    return this.children;
  };
  BoneData.prototype.component4 = function () {
    return this.offsetMatrix;
  };
  BoneData.prototype.component5 = function () {
    return this.vertexIds;
  };
  BoneData.prototype.component6 = function () {
    return this.vertexWeights;
  };
  BoneData.prototype.copy_n6d2h0$ = function (name, parent, children, offsetMatrix, vertexIds, vertexWeights) {
    return new BoneData(name === void 0 ? this.name : name, parent === void 0 ? this.parent : parent, children === void 0 ? this.children : children, offsetMatrix === void 0 ? this.offsetMatrix : offsetMatrix, vertexIds === void 0 ? this.vertexIds : vertexIds, vertexWeights === void 0 ? this.vertexWeights : vertexWeights);
  };
  BoneData.prototype.toString = function () {
    return 'BoneData(name=' + Kotlin.toString(this.name) + (', parent=' + Kotlin.toString(this.parent)) + (', children=' + Kotlin.toString(this.children)) + (', offsetMatrix=' + Kotlin.toString(this.offsetMatrix)) + (', vertexIds=' + Kotlin.toString(this.vertexIds)) + (', vertexWeights=' + Kotlin.toString(this.vertexWeights)) + ')';
  };
  BoneData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.parent) | 0;
    result = result * 31 + Kotlin.hashCode(this.children) | 0;
    result = result * 31 + Kotlin.hashCode(this.offsetMatrix) | 0;
    result = result * 31 + Kotlin.hashCode(this.vertexIds) | 0;
    result = result * 31 + Kotlin.hashCode(this.vertexWeights) | 0;
    return result;
  };
  BoneData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.parent, other.parent) && Kotlin.equals(this.children, other.children) && Kotlin.equals(this.offsetMatrix, other.offsetMatrix) && Kotlin.equals(this.vertexIds, other.vertexIds) && Kotlin.equals(this.vertexWeights, other.vertexWeights)))));
  };
  var getKClass = Kotlin.getKClass;
  var klassSerializer = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.klassSerializer_yop3xi$;
  function loadMesh(data) {
    var $this = ProtoBuf.Companion.plain;
    return loadMesh_0($this.load_8dtdds$(klassSerializer($this.context, getKClass(MeshData_0)), data));
  }
  function loadMesh$lambda(closure$data, closure$i) {
    return function ($receiver) {
      $receiver.position.set_y2kzbl$(closure$data.positions.get_za3lpa$(closure$i * 3 | 0), closure$data.positions.get_za3lpa$((closure$i * 3 | 0) + 1 | 0), closure$data.positions.get_za3lpa$((closure$i * 3 | 0) + 2 | 0));
      if (closure$data.hasNormals()) {
        $receiver.normal.set_y2kzbl$(closure$data.normals.get_za3lpa$(closure$i * 3 | 0), closure$data.normals.get_za3lpa$((closure$i * 3 | 0) + 1 | 0), closure$data.normals.get_za3lpa$((closure$i * 3 | 0) + 2 | 0));
      }
      if (closure$data.hasTexCoords()) {
        $receiver.texCoord.set_dleff0$(closure$data.uvs.get_za3lpa$(closure$i * 2 | 0), closure$data.uvs.get_za3lpa$((closure$i * 2 | 0) + 1 | 0));
      }
      if (closure$data.hasColors()) {
        $receiver.color.set_7b5o5w$(closure$data.colors.get_za3lpa$(closure$i * 4 | 0), closure$data.colors.get_za3lpa$((closure$i * 4 | 0) + 1 | 0), closure$data.colors.get_za3lpa$((closure$i * 4 | 0) + 2 | 0), closure$data.colors.get_za3lpa$((closure$i * 4 | 0) + 3 | 0));
      }
      return Unit;
    };
  }
  function loadMesh_0(data) {
    var tmp$, tmp$_0;
    var attributes = mutableSetOf([Attribute$Companion_getInstance().POSITIONS]);
    if (data.hasNormals()) {
      var element = Attribute$Companion_getInstance().NORMALS;
      attributes.add_11rb$(element);
    }
    if (data.hasColors()) {
      var element_0 = Attribute$Companion_getInstance().COLORS;
      attributes.add_11rb$(element_0);
    }
    if (data.hasTexCoords()) {
      var element_1 = Attribute$Companion_getInstance().TEXTURE_COORDS;
      attributes.add_11rb$(element_1);
    }
    var meshData = new MeshData(attributes);
    if (!data.armature.isEmpty()) {
      meshData.usage = GL_DYNAMIC_DRAW;
    }
    tmp$ = data.positions.size / 3 | 0;
    for (var i = 0; i < tmp$; i++) {
      meshData.addVertex_hvwyd1$(loadMesh$lambda(data, i));
    }
    tmp$_0 = data.triangles.size;
    for (var i_0 = 0; i_0 < tmp$_0; i_0 += 3) {
      meshData.addTriIndices_qt1dr2$(data.triangles.get_za3lpa$(i_0), data.triangles.get_za3lpa$(i_0 + 1 | 0), data.triangles.get_za3lpa$(i_0 + 2 | 0));
    }
    var mesh;
    if (!data.armature.isEmpty()) {
      mesh = new Armature(meshData, data.name);
      var tmp$_1;
      tmp$_1 = data.armature.iterator();
      while (tmp$_1.hasNext()) {
        var element_2 = tmp$_1.next();
        var closure$mesh = mesh;
        var tmp$_2;
        var bone = new Bone(element_2.name, element_2.vertexIds.size);
        var $receiver = closure$mesh.bones;
        var key = bone.name;
        $receiver.put_xwzc9p$(key, bone);
        bone.offsetMatrix.set_hcyabg$(element_2.offsetMatrix);
        tmp$_2 = element_2.vertexIds;
        for (var i_1 = 0; i_1 !== tmp$_2.size; ++i_1) {
          bone.vertexIds[i_1] = element_2.vertexIds.get_za3lpa$(i_1);
          bone.vertexWeights[i_1] = element_2.vertexWeights.get_za3lpa$(i_1);
        }
      }
      var tmp$_3;
      tmp$_3 = data.armature.iterator();
      while (tmp$_3.hasNext()) {
        var element_3 = tmp$_3.next();
        var closure$mesh_0 = mesh;
        var bone_0 = ensureNotNull(closure$mesh_0.bones.get_11rb$(element_3.name));
        bone_0.parent = closure$mesh_0.bones.get_11rb$(element_3.parent);
        if (bone_0.parent == null) {
          closure$mesh_0.rootBones.add_11rb$(bone_0);
        }
        var tmp$_4;
        tmp$_4 = element_3.children.iterator();
        while (tmp$_4.hasNext()) {
          var element_4 = tmp$_4.next();
          var child = closure$mesh_0.bones.get_11rb$(element_4);
          if (child != null) {
            bone_0.children.add_11rb$(child);
          }
        }
      }
      mesh.updateBones();
      var tmp$_5;
      tmp$_5 = data.animations.iterator();
      while (tmp$_5.hasNext()) {
        var element_5 = tmp$_5.next();
        var closure$mesh_1 = mesh;
        closure$mesh_1.addAnimation_z5ltv$(element_5.name, element_5.getAnimation_wev6wz$(closure$mesh_1.bones));
      }
    }
     else {
      mesh = new Mesh(meshData, data.name);
    }
    return mesh;
  }
  function MeshData_0(name, triangles, positions, normals, uvs, colors, armature, animations) {
    MeshData$Companion_getInstance();
    if (normals === void 0)
      normals = emptyList();
    if (uvs === void 0)
      uvs = emptyList();
    if (colors === void 0)
      colors = emptyList();
    if (armature === void 0)
      armature = emptyList();
    if (animations === void 0)
      animations = emptyList();
    this.name = name;
    this.triangles = triangles;
    this.positions = positions;
    this.normals = normals;
    this.uvs = uvs;
    this.colors = colors;
    this.armature = armature;
    this.animations = animations;
  }
  MeshData_0.prototype.hasNormals = function () {
    return !this.normals.isEmpty();
  };
  MeshData_0.prototype.hasTexCoords = function () {
    return !this.uvs.isEmpty();
  };
  MeshData_0.prototype.hasColors = function () {
    return !this.colors.isEmpty();
  };
  function MeshData$Companion() {
    MeshData$Companion_instance = this;
  }
  MeshData$Companion.prototype.serializer = function () {
    return _.de.fabmax.kool.util.serialization.MeshData.$serializer;
  };
  MeshData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MeshData$Companion_instance = null;
  function MeshData$Companion_getInstance() {
    if (MeshData$Companion_instance === null) {
      new MeshData$Companion();
    }
    return MeshData$Companion_instance;
  }
  function MeshData$$serializer() {
    this.serialClassDesc_qzcrd9$_0 = new SerialClassDescImpl('de.fabmax.kool.util.serialization.MeshData');
    this.serialClassDesc.addElement_61zpoe$('name');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(1));
    this.serialClassDesc.addElement_61zpoe$('triangles');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(2));
    this.serialClassDesc.addElement_61zpoe$('positions');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(3));
    this.serialClassDesc.addElement_61zpoe$('normals');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(4));
    this.serialClassDesc.addElement_61zpoe$('uvs');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(5));
    this.serialClassDesc.addElement_61zpoe$('colors');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(6));
    this.serialClassDesc.addElement_61zpoe$('armature');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(7));
    this.serialClassDesc.addElement_61zpoe$('animations');
    this.serialClassDesc.pushAnnotation_yj921w$(new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.SerialId(8));
    MeshData$$serializer_instance = this;
  }
  Object.defineProperty(MeshData$$serializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_qzcrd9$_0;
    }
  });
  MeshData$$serializer.prototype.save_ejfkry$ = function (output_0, obj) {
    var output = output_0.writeBegin_276rha$(this.serialClassDesc, []);
    output.writeStringElementValue_k4mjep$(this.serialClassDesc, 0, obj.name);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 1, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.IntSerializer), obj.triangles);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), obj.positions);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), obj.normals);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 4, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), obj.uvs);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 5, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), obj.colors);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 6, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.BoneData.$serializer), obj.armature);
    output.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 7, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.AnimationData.$serializer), obj.animations);
    output.writeEnd_f6e2p$(this.serialClassDesc);
  };
  MeshData$$serializer.prototype.load_ljkqvg$ = function (input_0) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3
    , local4
    , local5
    , local6
    , local7;
    var input = input_0.readBegin_276rha$(this.serialClassDesc, []);
    loopLabel: while (true) {
      index = input.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.readStringElementValue_xvmgof$(this.serialClassDesc, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 1, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.IntSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 1, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.IntSerializer), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 2, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = (bitMask0 & 8) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 3, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), local3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case 4:
          local4 = (bitMask0 & 16) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 4, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 4, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), local4);
          bitMask0 |= 16;
          if (!readAll)
            break;
        case 5:
          local5 = (bitMask0 & 32) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 5, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 5, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer($module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.FloatSerializer), local5);
          bitMask0 |= 32;
          if (!readAll)
            break;
        case 6:
          local6 = (bitMask0 & 64) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 6, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.BoneData.$serializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 6, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.BoneData.$serializer), local6);
          bitMask0 |= 64;
          if (!readAll)
            break;
        case 7:
          local7 = (bitMask0 & 128) === 0 ? input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 7, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.AnimationData.$serializer)) : input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, 7, new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.ArrayListSerializer(_.de.fabmax.kool.util.serialization.AnimationData.$serializer), local7);
          bitMask0 |= 128;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException(index);
      }
    }
    input.readEnd_f6e2p$(this.serialClassDesc);
    return MeshData_init_0(bitMask0, local0, local1, local2, local3, local4, local5, local6, local7, null);
  };
  MeshData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [KSerializer]
  };
  var MeshData$$serializer_instance = null;
  function MeshData$$serializer_getInstance() {
    if (MeshData$$serializer_instance === null) {
      new MeshData$$serializer();
    }
    return MeshData$$serializer_instance;
  }
  function MeshData_init_0(seen, name, triangles, positions, normals, uvs, colors, armature, animations, serializationConstructorMarker) {
    var $this = Object.create(_.de.fabmax.kool.util.serialization.MeshData.prototype);
    if ((seen & 1) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('name');
    else
      $this.name = name;
    if ((seen & 2) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('triangles');
    else
      $this.triangles = triangles;
    if ((seen & 4) === 0)
      throw new $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException('positions');
    else
      $this.positions = positions;
    if ((seen & 8) === 0)
      $this.normals = emptyList();
    else
      $this.normals = normals;
    if ((seen & 16) === 0)
      $this.uvs = emptyList();
    else
      $this.uvs = uvs;
    if ((seen & 32) === 0)
      $this.colors = emptyList();
    else
      $this.colors = colors;
    if ((seen & 64) === 0)
      $this.armature = emptyList();
    else
      $this.armature = armature;
    if ((seen & 128) === 0)
      $this.animations = emptyList();
    else
      $this.animations = animations;
    return $this;
  }
  MeshData_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MeshData',
    interfaces: []
  };
  MeshData_0.prototype.component1 = function () {
    return this.name;
  };
  MeshData_0.prototype.component2 = function () {
    return this.triangles;
  };
  MeshData_0.prototype.component3 = function () {
    return this.positions;
  };
  MeshData_0.prototype.component4 = function () {
    return this.normals;
  };
  MeshData_0.prototype.component5 = function () {
    return this.uvs;
  };
  MeshData_0.prototype.component6 = function () {
    return this.colors;
  };
  MeshData_0.prototype.component7 = function () {
    return this.armature;
  };
  MeshData_0.prototype.component8 = function () {
    return this.animations;
  };
  MeshData_0.prototype.copy_6syyd3$ = function (name, triangles, positions, normals, uvs, colors, armature, animations) {
    return new MeshData_0(name === void 0 ? this.name : name, triangles === void 0 ? this.triangles : triangles, positions === void 0 ? this.positions : positions, normals === void 0 ? this.normals : normals, uvs === void 0 ? this.uvs : uvs, colors === void 0 ? this.colors : colors, armature === void 0 ? this.armature : armature, animations === void 0 ? this.animations : animations);
  };
  MeshData_0.prototype.toString = function () {
    return 'MeshData(name=' + Kotlin.toString(this.name) + (', triangles=' + Kotlin.toString(this.triangles)) + (', positions=' + Kotlin.toString(this.positions)) + (', normals=' + Kotlin.toString(this.normals)) + (', uvs=' + Kotlin.toString(this.uvs)) + (', colors=' + Kotlin.toString(this.colors)) + (', armature=' + Kotlin.toString(this.armature)) + (', animations=' + Kotlin.toString(this.animations)) + ')';
  };
  MeshData_0.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.triangles) | 0;
    result = result * 31 + Kotlin.hashCode(this.positions) | 0;
    result = result * 31 + Kotlin.hashCode(this.normals) | 0;
    result = result * 31 + Kotlin.hashCode(this.uvs) | 0;
    result = result * 31 + Kotlin.hashCode(this.colors) | 0;
    result = result * 31 + Kotlin.hashCode(this.armature) | 0;
    result = result * 31 + Kotlin.hashCode(this.animations) | 0;
    return result;
  };
  MeshData_0.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.triangles, other.triangles) && Kotlin.equals(this.positions, other.positions) && Kotlin.equals(this.normals, other.normals) && Kotlin.equals(this.uvs, other.uvs) && Kotlin.equals(this.colors, other.colors) && Kotlin.equals(this.armature, other.armature) && Kotlin.equals(this.animations, other.animations)))));
  };
  var glCapabilities;
  function createContext() {
    return createContext_0(new JsContext$InitProps());
  }
  function createContext_0(props) {
    return JsImpl_getInstance().createContext_izggji$(props);
  }
  function createCharMap(fontProps) {
    return JsImpl_getInstance().fontGenerator.createCharMap_ttufcy$(fontProps);
  }
  function currentTimeMillis() {
    return Kotlin.Long.fromNumber((new Date()).getTime());
  }
  function loadAsset$lambda(closure$req, closure$onLoad) {
    return function (evt) {
      var tmp$, tmp$_0;
      var array = new Uint8Array(Kotlin.isType(tmp$ = closure$req.response, ArrayBuffer) ? tmp$ : throwCCE());
      var bytes = new Int8Array(array.length);
      tmp$_0 = array.length;
      for (var i = 0; i < tmp$_0; i++) {
        bytes[i] = array[i];
      }
      closure$onLoad(bytes);
      return Unit;
    };
  }
  function loadAsset(assetPath, onLoad) {
    var req = new XMLHttpRequest();
    req.open('GET', assetPath);
    req.responseType = 'arraybuffer';
    req.onload = loadAsset$lambda(req, onLoad);
    req.send();
  }
  function loadTextureAsset(assetPath) {
    var img = new Image();
    var data = new ImageTextureData(img);
    img.crossOrigin = '';
    img.src = assetPath;
    return data;
  }
  function openUrl(url) {
    window.open(url);
  }
  function getMemoryInfo() {
    return '';
  }
  function JsImpl() {
    JsImpl_instance = this;
    this.MAX_GENERATED_TEX_WIDTH_0 = 1024;
    this.MAX_GENERATED_TEX_HEIGHT_0 = 1024;
    this.isWebGl2Context = false;
    this.dpi = 0;
    this.ctx = null;
    this.fontGenerator_1ovgb6$_0 = lazy(JsImpl$fontGenerator$lambda(this));
    var tmp$;
    var measure = document.getElementById('dpiMeasure');
    if (measure == null) {
      println('dpiMeasure element not found, falling back to 96 dpi');
      println('Add this hidden div to your html:');
      println('<div id="dpiMeasure" style="height: 1in; width: 1in; left: 100%; position: fixed; top: 100%;"><\/div>');
      this.dpi = 96.0;
    }
     else {
      this.dpi = (Kotlin.isType(tmp$ = measure, HTMLDivElement) ? tmp$ : throwCCE()).offsetWidth;
    }
  }
  Object.defineProperty(JsImpl.prototype, 'gl', {
    get: function () {
      var tmp$, tmp$_0;
      tmp$_0 = (tmp$ = this.ctx) != null ? tmp$.gl_8be2vx$ : null;
      if (tmp$_0 == null) {
        throw new KoolException('Platform.createContext() not called');
      }
      return tmp$_0;
    }
  });
  Object.defineProperty(JsImpl.prototype, 'fontGenerator', {
    get: function () {
      return this.fontGenerator_1ovgb6$_0.value;
    }
  });
  JsImpl.prototype.createContext_izggji$ = function (props) {
    if (this.ctx != null) {
      throw new KoolException('Context was already creates (multi-context is currently not supported in js');
    }
    if (Kotlin.isType(props, JsContext$InitProps)) {
      this.ctx = new JsContext(props);
      return ensureNotNull(this.ctx);
    }
     else {
      throw IllegalArgumentException_init('Props must be of JsContext.InitProps');
    }
  };
  function JsImpl$fontGenerator$lambda(this$JsImpl) {
    return function () {
      return new FontMapGenerator(this$JsImpl.MAX_GENERATED_TEX_WIDTH_0, this$JsImpl.MAX_GENERATED_TEX_HEIGHT_0);
    };
  }
  JsImpl.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsImpl',
    interfaces: []
  };
  var JsImpl_instance = null;
  function JsImpl_getInstance() {
    if (JsImpl_instance === null) {
      new JsImpl();
    }
    return JsImpl_instance;
  }
  function AudioGenerator(generatorFun) {
    this.audioCtx_0 = new (window.AudioContext || window.webkitAudioContext)();
    this.sampleRate = this.audioCtx_0.sampleRate;
    this.isPaused_p8nrgy$_0 = false;
    this.source_0 = null;
    this.scriptNode_0 = null;
    this.analyserNode_0 = null;
    var tmp$;
    this.powerSpectrum_0 = Kotlin.isType(tmp$ = createFloat32Buffer(1), Float32BufferImpl) ? tmp$ : throwCCE();
    this.dt_0 = 1.0 / this.sampleRate;
    this.scriptNode_0 = this.audioCtx_0.createScriptProcessor(4096, 1, 1);
    var buffer = this.audioCtx_0.createBuffer(1, this.scriptNode_0.bufferSize, this.sampleRate);
    this.scriptNode_0.onaudioprocess = AudioGenerator_init$lambda(generatorFun, this);
    this.analyserNode_0 = null;
    this.source_0 = this.audioCtx_0.createBufferSource();
    this.source_0.buffer = buffer;
    this.source_0.loop = true;
    this.source_0.connect(this.scriptNode_0);
    this.scriptNode_0.connect(this.audioCtx_0.destination);
    this.source_0.start();
  }
  Object.defineProperty(AudioGenerator.prototype, 'isPaused', {
    get: function () {
      return this.isPaused_p8nrgy$_0;
    },
    set: function (value) {
      if (this.isPaused_p8nrgy$_0 !== value) {
        this.isPaused_p8nrgy$_0 = value;
        if (value) {
          this.source_0.stop();
        }
         else {
          this.source_0.start();
        }
      }
    }
  });
  AudioGenerator.prototype.stop = function () {
    this.scriptNode_0.disconnect();
    this.source_0.loop = false;
    this.source_0.disconnect();
    this.source_0.stop();
  };
  AudioGenerator.prototype.enableFftComputation_za3lpa$ = function (nSamples) {
    var tmp$, tmp$_0;
    if (nSamples <= 0) {
      (tmp$ = this.analyserNode_0) != null ? tmp$.disconnect() : null;
      this.analyserNode_0 = null;
    }
     else {
      if (this.analyserNode_0 == null) {
        this.analyserNode_0 = this.audioCtx_0.createAnalyser();
        this.analyserNode_0.minDecibels = -90;
        this.analyserNode_0.maxDecibels = 0;
        this.analyserNode_0.smoothingTimeConstant = 0.5;
        this.scriptNode_0.connect(this.analyserNode_0);
      }
      this.analyserNode_0.fftSize = nSamples;
      this.powerSpectrum_0 = Kotlin.isType(tmp$_0 = createFloat32Buffer(this.analyserNode_0.frequencyBinCount), Float32BufferImpl) ? tmp$_0 : throwCCE();
    }
  };
  AudioGenerator.prototype.getPowerSpectrum = function () {
    this.analyserNode_0.getFloatFrequencyData(this.powerSpectrum_0.buffer);
    return this.powerSpectrum_0;
  };
  function AudioGenerator_init$lambda(closure$generatorFun, this$AudioGenerator) {
    return function (ev) {
      var tmp$;
      var outputBuffer = ev.outputBuffer;
      var data = outputBuffer.getChannelData(0);
      tmp$ = outputBuffer.length;
      for (var i = 0; i < tmp$; i++) {
        data[i] = closure$generatorFun(this$AudioGenerator, this$AudioGenerator.dt_0);
      }
      return Unit;
    };
  }
  AudioGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AudioGenerator',
    interfaces: []
  };
  function glActiveTexture(texture) {
    JsImpl_getInstance().gl.activeTexture(texture);
  }
  function glAttachShader(program, shader) {
    var tmp$, tmp$_0;
    JsImpl_getInstance().gl.attachShader(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE(), Kotlin.isType(tmp$_0 = shader.glRef, WebGLShader) ? tmp$_0 : throwCCE());
  }
  function glBindBuffer(target, buffer) {
    var tmp$;
    JsImpl_getInstance().gl.bindBuffer(target, (tmp$ = buffer != null ? buffer.glRef : null) == null || Kotlin.isType(tmp$, WebGLBuffer) ? tmp$ : throwCCE());
  }
  function glBindFramebuffer(target, framebuffer) {
    var tmp$;
    JsImpl_getInstance().gl.bindFramebuffer(target, (tmp$ = framebuffer != null ? framebuffer.glRef : null) == null || Kotlin.isType(tmp$, WebGLFramebuffer) ? tmp$ : throwCCE());
  }
  function glBindRenderbuffer(target, renderbuffer) {
    var tmp$;
    JsImpl_getInstance().gl.bindRenderbuffer(target, (tmp$ = renderbuffer != null ? renderbuffer.glRef : null) == null || Kotlin.isType(tmp$, WebGLRenderbuffer) ? tmp$ : throwCCE());
  }
  function glBindTexture(target, texture) {
    var tmp$;
    JsImpl_getInstance().gl.bindTexture(target, (tmp$ = texture != null ? texture.glRef : null) == null || Kotlin.isType(tmp$, WebGLTexture) ? tmp$ : throwCCE());
  }
  function glBlendFunc(sfactor, dfactor) {
    JsImpl_getInstance().gl.blendFunc(sfactor, dfactor);
  }
  function glBufferData(target, data, usage) {
    var tmp$;
    JsImpl_getInstance().gl.bufferData(target, (Kotlin.isType(tmp$ = data, Uint8BufferImpl) ? tmp$ : throwCCE()).buffer, usage);
  }
  function glBufferData_0(target, data, usage) {
    var tmp$;
    JsImpl_getInstance().gl.bufferData(target, (Kotlin.isType(tmp$ = data, Uint16BufferImpl) ? tmp$ : throwCCE()).buffer, usage);
  }
  function glBufferData_1(target, data, usage) {
    var tmp$;
    JsImpl_getInstance().gl.bufferData(target, (Kotlin.isType(tmp$ = data, Uint32BufferImpl) ? tmp$ : throwCCE()).buffer, usage);
  }
  function glBufferData_2(target, data, usage) {
    var tmp$;
    JsImpl_getInstance().gl.bufferData(target, (Kotlin.isType(tmp$ = data, Float32BufferImpl) ? tmp$ : throwCCE()).buffer, usage);
  }
  function glClear(mask) {
    JsImpl_getInstance().gl.clear(mask);
  }
  function glClearColor(red, green, blue, alpha) {
    JsImpl_getInstance().gl.clearColor(red, green, blue, alpha);
  }
  function glCompileShader(shader) {
    var tmp$;
    JsImpl_getInstance().gl.compileShader(Kotlin.isType(tmp$ = shader.glRef, WebGLShader) ? tmp$ : throwCCE());
  }
  function glCopyTexImage2D(target, level, internalformat, x, y, width, height, border) {
    JsImpl_getInstance().gl.copyTexImage2D(target, level, internalformat, x, y, width, height, border);
  }
  function glCreateBuffer() {
    return ensureNotNull(JsImpl_getInstance().gl.createBuffer());
  }
  function glCreateFramebuffer() {
    return ensureNotNull(JsImpl_getInstance().gl.createFramebuffer());
  }
  function glCreateRenderbuffer() {
    return ensureNotNull(JsImpl_getInstance().gl.createRenderbuffer());
  }
  function glCreateProgram() {
    return ensureNotNull(JsImpl_getInstance().gl.createProgram());
  }
  function glCreateShader(type) {
    return ensureNotNull(JsImpl_getInstance().gl.createShader(type));
  }
  function glCreateTexture() {
    return ensureNotNull(JsImpl_getInstance().gl.createTexture());
  }
  function glCullFace(mode) {
    JsImpl_getInstance().gl.cullFace(mode);
  }
  function glDeleteBuffer(buffer) {
    var tmp$;
    JsImpl_getInstance().gl.deleteBuffer(Kotlin.isType(tmp$ = buffer.glRef, WebGLBuffer) ? tmp$ : throwCCE());
  }
  function glDeleteFramebuffer(framebuffer) {
    var tmp$;
    JsImpl_getInstance().gl.deleteFramebuffer(Kotlin.isType(tmp$ = framebuffer.glRef, WebGLFramebuffer) ? tmp$ : throwCCE());
  }
  function glDeleteProgram(program) {
    var tmp$;
    JsImpl_getInstance().gl.deleteProgram(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE());
  }
  function glDeleteRenderbuffer(renderbuffer) {
    var tmp$;
    JsImpl_getInstance().gl.deleteRenderbuffer(Kotlin.isType(tmp$ = renderbuffer.glRef, WebGLRenderbuffer) ? tmp$ : throwCCE());
  }
  function glDeleteShader(shader) {
    var tmp$;
    JsImpl_getInstance().gl.deleteShader(Kotlin.isType(tmp$ = shader.glRef, WebGLShader) ? tmp$ : throwCCE());
  }
  function glDeleteTexture(texture) {
    var tmp$;
    JsImpl_getInstance().gl.deleteTexture(Kotlin.isType(tmp$ = texture.glRef, WebGLTexture) ? tmp$ : throwCCE());
  }
  function glDepthFunc(func) {
    JsImpl_getInstance().gl.depthFunc(func);
  }
  function glDepthMask(enabled) {
    JsImpl_getInstance().gl.depthMask(enabled);
  }
  function glDisable(cap) {
    JsImpl_getInstance().gl.disable(cap);
  }
  function glDisableVertexAttribArray(index) {
    JsImpl_getInstance().gl.disableVertexAttribArray(index);
  }
  function glDrawBuffer(buf) {
    var tmp$;
    if (JsImpl_getInstance().isWebGl2Context) {
      (Kotlin.isType(tmp$ = JsImpl_getInstance().gl, WebGL2RenderingContext) ? tmp$ : throwCCE()).drawBuffers(new Int32Array([buf]));
    }
  }
  function glDrawElements(mode, count, type, offset) {
    JsImpl_getInstance().gl.drawElements(mode, count, type, offset);
  }
  function glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount) {
    throw UnsupportedOperationException_init('not available on WebGL');
  }
  function glEnable(cap) {
    JsImpl_getInstance().gl.enable(cap);
  }
  function glEnableVertexAttribArray(index) {
    JsImpl_getInstance().gl.enableVertexAttribArray(index);
  }
  function glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer) {
    var tmp$;
    JsImpl_getInstance().gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, Kotlin.isType(tmp$ = renderbuffer.glRef, WebGLRenderbuffer) ? tmp$ : throwCCE());
  }
  function glFramebufferTexture2D(target, attachment, textarget, texture, level) {
    var tmp$;
    JsImpl_getInstance().gl.framebufferTexture2D(target, attachment, textarget, Kotlin.isType(tmp$ = texture.glRef, WebGLTexture) ? tmp$ : throwCCE(), level);
  }
  function glGenerateMipmap(target) {
    JsImpl_getInstance().gl.generateMipmap(target);
  }
  function glGetAttribLocation(program, name) {
    var tmp$;
    return JsImpl_getInstance().gl.getAttribLocation(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE(), name);
  }
  function glGetError() {
    return JsImpl_getInstance().gl.getError();
  }
  function glGetProgrami(program, pname) {
    var tmp$, tmp$_0, tmp$_1;
    var res = JsImpl_getInstance().gl.getProgramParameter(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE(), pname);
    if (pname === GL_LINK_STATUS) {
      if (typeof (tmp$_0 = res) === 'boolean' ? tmp$_0 : throwCCE()) {
        tmp$_1 = GL_TRUE;
      }
       else {
        tmp$_1 = GL_FALSE;
      }
      return tmp$_1;
    }
    return 0;
  }
  function glGetShaderi(shader, pname) {
    var tmp$, tmp$_0, tmp$_1;
    var res = JsImpl_getInstance().gl.getShaderParameter(Kotlin.isType(tmp$ = shader.glRef, WebGLShader) ? tmp$ : throwCCE(), pname);
    if (pname === GL_COMPILE_STATUS) {
      if (typeof (tmp$_0 = res) === 'boolean' ? tmp$_0 : throwCCE()) {
        tmp$_1 = GL_TRUE;
      }
       else {
        tmp$_1 = GL_FALSE;
      }
      return tmp$_1;
    }
    return 0;
  }
  function glGetProgramInfoLog(program) {
    var tmp$, tmp$_0;
    return (tmp$_0 = JsImpl_getInstance().gl.getProgramInfoLog(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE())) != null ? tmp$_0 : '';
  }
  function glGetShaderInfoLog(shader) {
    var tmp$, tmp$_0;
    return (tmp$_0 = JsImpl_getInstance().gl.getShaderInfoLog(Kotlin.isType(tmp$ = shader.glRef, WebGLShader) ? tmp$ : throwCCE())) != null ? tmp$_0 : '';
  }
  function glGetUniformLocation(program, name) {
    var tmp$;
    return JsImpl_getInstance().gl.getUniformLocation(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE(), name);
  }
  function glLineWidth(width) {
    JsImpl_getInstance().gl.lineWidth(width);
  }
  function glLinkProgram(program) {
    var tmp$;
    JsImpl_getInstance().gl.linkProgram(Kotlin.isType(tmp$ = program.glRef, WebGLProgram) ? tmp$ : throwCCE());
  }
  function glPointSize(size) {
  }
  function glReadBuffer(src) {
    var tmp$;
    if (JsImpl_getInstance().isWebGl2Context) {
      (Kotlin.isType(tmp$ = JsImpl_getInstance().gl, WebGL2RenderingContext) ? tmp$ : throwCCE()).readBuffer(src);
    }
  }
  function glRenderbufferStorage(target, internalformat, width, height) {
    JsImpl_getInstance().gl.renderbufferStorage(target, internalformat, width, height);
  }
  function glRenderbufferStorageMultisample(target, samples, internalformat, width, height) {
    throw UnsupportedOperationException_init('not available on WebGL');
  }
  function glShaderSource(shader, source) {
    var tmp$;
    JsImpl_getInstance().gl.shaderSource(Kotlin.isType(tmp$ = shader.glRef, WebGLShader) ? tmp$ : throwCCE(), source);
  }
  function glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels) {
    var tmp$, tmp$_0;
    JsImpl_getInstance().gl.texImage2D(target, level, internalformat, width, height, border, format, type, (tmp$_0 = (tmp$ = pixels) == null || Kotlin.isType(tmp$, Uint8BufferImpl) ? tmp$ : throwCCE()) != null ? tmp$_0.buffer : null);
  }
  function glTexImage2D_0(target, level, internalformat, format, type, pixels) {
    var tmp$;
    JsImpl_getInstance().gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, GL_TRUE);
    JsImpl_getInstance().gl.texImage2D(target, level, internalformat, format, type, (tmp$ = pixels) == null || Kotlin.isType(tmp$, HTMLImageElement) ? tmp$ : throwCCE());
    JsImpl_getInstance().gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, GL_FALSE);
  }
  function glTexParameteri(target, pname, param) {
    JsImpl_getInstance().gl.texParameteri(target, pname, param);
  }
  function glUniform1f(location, x) {
    var tmp$;
    JsImpl_getInstance().gl.uniform1f((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), x);
  }
  function glUniform1fv(location, x) {
    var tmp$;
    var array = Array_0(x.length);
    var tmp$_0;
    tmp$_0 = array.length - 1 | 0;
    for (var i = 0; i <= tmp$_0; i++) {
      array[i] = x[i];
    }
    var tmp = array;
    JsImpl_getInstance().gl.uniform1fv((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), tmp);
  }
  function glUniform1i(location, x) {
    var tmp$;
    JsImpl_getInstance().gl.uniform1i((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), x);
  }
  function glUniform1iv(location, x) {
    var tmp$;
    var array = Array_0(x.length);
    var tmp$_0;
    tmp$_0 = array.length - 1 | 0;
    for (var i = 0; i <= tmp$_0; i++) {
      array[i] = x[i];
    }
    var tmp = array;
    JsImpl_getInstance().gl.uniform1iv((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), tmp);
  }
  function glUniform2f(location, x, y) {
    var tmp$;
    JsImpl_getInstance().gl.uniform2f((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), x, y);
  }
  function glUniform3f(location, x, y, z) {
    var tmp$;
    JsImpl_getInstance().gl.uniform3f((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), x, y, z);
  }
  function glUniform4f(location, x, y, z, w) {
    var tmp$;
    JsImpl_getInstance().gl.uniform4f((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), x, y, z, w);
  }
  function glUniformMatrix4fv(location, transpose, value) {
    var tmp$, tmp$_0;
    JsImpl_getInstance().gl.uniformMatrix4fv((tmp$ = location) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE(), transpose, (Kotlin.isType(tmp$_0 = value, Float32BufferImpl) ? tmp$_0 : throwCCE()).buffer);
  }
  function glUseProgram(program) {
    var tmp$;
    JsImpl_getInstance().gl.useProgram((tmp$ = program != null ? program.glRef : null) == null || Kotlin.isType(tmp$, WebGLProgram) ? tmp$ : throwCCE());
  }
  function glVertexAttribDivisor(index, divisor) {
    throw UnsupportedOperationException_init('not available on WebGL');
  }
  function glVertexAttribPointer(index, size, type, normalized, stride, offset) {
    JsImpl_getInstance().gl.vertexAttribPointer(index, size, type, normalized, stride, offset);
  }
  function glVertexAttribIPointer(index, size, type, stride, offset) {
    var tmp$;
    if (JsImpl_getInstance().isWebGl2Context) {
      (Kotlin.isType(tmp$ = JsImpl_getInstance().gl, WebGL2RenderingContext) ? tmp$ : throwCCE()).vertexAttribIPointer(index, size, type, stride, offset);
    }
     else {
      throw new KoolException('This function requires WebGL2 support');
    }
  }
  function glViewport(x, y, width, height) {
    JsImpl_getInstance().gl.viewport(x, y, width, height);
  }
  function isValidUniformLocation(location) {
    return location != null && Kotlin.isType(location, WebGLUniformLocation);
  }
  function FontMapGenerator(maxWidth, maxHeight) {
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    var tmp$, tmp$_0;
    this.canvas_0 = Kotlin.isType(tmp$ = document.createElement('canvas'), HTMLCanvasElement) ? tmp$ : throwCCE();
    this.canvasCtx_0 = null;
    this.canvas_0.width = this.maxWidth;
    this.canvas_0.height = this.maxHeight;
    this.canvasCtx_0 = Kotlin.isType(tmp$_0 = this.canvas_0.getContext('2d'), CanvasRenderingContext2D) ? tmp$_0 : throwCCE();
  }
  FontMapGenerator.prototype.createCharMap_ttufcy$ = function (fontProps) {
    var tmp$;
    this.canvasCtx_0.clearRect(0.0, 0.0, this.maxWidth, this.maxHeight);
    var style = 'lighter ';
    if ((fontProps.style & Font$Companion_getInstance().BOLD) !== 0) {
      style = 'bold ';
    }
    if ((fontProps.style & Font$Companion_getInstance().ITALIC) !== 0) {
      style += 'italic ';
    }
    var metrics = LinkedHashMap_init();
    var texHeight = this.makeMap_0(fontProps, style, metrics);
    var data = this.canvasCtx_0.getImageData(0.0, 0.0, this.maxWidth, texHeight);
    var buffer = createUint8Buffer(Kotlin.imul(this.maxWidth, texHeight));
    tmp$ = buffer.capacity;
    for (var i = 0; i < tmp$; i++) {
      buffer.put_11rb$(data.data[(i * 4 | 0) + 3 | 0]);
    }
    return new CharMap(new BufferedTextureData(buffer, this.maxWidth, texHeight, GL_ALPHA), metrics);
  };
  FontMapGenerator.prototype.makeMap_0 = function (fontProps, style, map) {
    var tmp$, tmp$_0;
    this.canvasCtx_0.font = style + fontProps.sizePts + 'px ' + fontProps.family;
    this.canvasCtx_0.fillStyle = '#ffffff';
    var padding = 3.0;
    var hab = round(fontProps.sizePts * 1.1);
    var hbb = round(fontProps.sizePts * 0.5);
    var height = round(fontProps.sizePts * 1.6);
    this.canvasCtx_0.beginPath();
    this.canvasCtx_0.moveTo(0.5, 0.0);
    this.canvasCtx_0.lineTo(0.5, 1.0);
    this.canvasCtx_0.stroke();
    var x = 1.0;
    var y = hab;
    tmp$ = iterator(fontProps.chars);
    while (tmp$.hasNext()) {
      var c = unboxChar(tmp$.next());
      if (c === 106) {
        x += fontProps.sizePts * 0.1;
      }
      var txt = String.fromCharCode(c);
      var charW = round(this.canvasCtx_0.measureText(txt).width);
      var paddedWidth = round(charW + padding * 2);
      if (x + paddedWidth > this.maxWidth) {
        x = 0.0;
        y += height + 10;
        if (y + hbb > this.maxHeight) {
          break;
        }
      }
      var widthPx = charW;
      var heightPx = height;
      var metrics = new CharMetrics();
      metrics.width = widthPx * fontProps.sizeUnits / fontProps.sizePts;
      metrics.height = heightPx * fontProps.sizeUnits / fontProps.sizePts;
      metrics.xOffset = 0.0;
      metrics.yBaseline = hab * fontProps.sizeUnits / fontProps.sizePts;
      metrics.advance = metrics.width;
      metrics.uvMin.set_dleff0$(x + padding, y - hab);
      metrics.uvMax.set_dleff0$(x + padding + widthPx, y - hab + heightPx);
      map.put_xwzc9p$(toBoxedChar(c), metrics);
      this.canvasCtx_0.fillText(txt, x + padding, y);
      x += paddedWidth;
    }
    var texW = this.maxWidth;
    var texH = this.nextPow2_0(y + hbb);
    tmp$_0 = map.values.iterator();
    while (tmp$_0.hasNext()) {
      var cm = tmp$_0.next();
      cm.uvMin.x = cm.uvMin.x / texW;
      cm.uvMin.y = cm.uvMin.y / texH;
      cm.uvMax.x = cm.uvMax.x / texW;
      cm.uvMax.y = cm.uvMax.y / texH;
    }
    return texH;
  };
  FontMapGenerator.prototype.nextPow2_0 = function (value) {
    var pow2 = 16;
    while (pow2 < value && pow2 < this.maxHeight) {
      pow2 = pow2 << 1;
    }
    return pow2;
  };
  FontMapGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FontMapGenerator',
    interfaces: []
  };
  function ImageTextureData(image) {
    TextureData.call(this);
    this.image = image;
  }
  Object.defineProperty(ImageTextureData.prototype, 'isAvailable', {
    get: function () {
      return this.image.complete;
    },
    set: function (value) {
    }
  });
  ImageTextureData.prototype.onLoad_4yp9vu$ = function (texture, ctx) {
    JsImpl_getInstance().gl.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, this.image);
    this.width = this.image.width;
    this.height = this.image.height;
    var size = Kotlin.imul(this.width, this.height) * 4 | 0;
    ctx.memoryMgr.memoryAllocated_927jj9$(ensureNotNull(texture.res), size);
  };
  ImageTextureData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ImageTextureData',
    interfaces: [TextureData]
  };
  function JsContext(props) {
    JsContext$Companion_getInstance();
    RenderContext.call(this);
    this.props = props;
    this.windowWidth_7sdnb6$_0 = 0;
    this.windowHeight_xsf9ux$_0 = 0;
    this.canvas_8be2vx$ = null;
    this.gl_8be2vx$ = null;
    this.animationMillis_0 = 0.0;
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    this.canvas_8be2vx$ = Kotlin.isType(tmp$ = document.getElementById(this.props.canvasName), HTMLCanvasElement) ? tmp$ : throwCCE();
    var webGlCtx = this.canvas_8be2vx$.getContext('webgl2');
    if (webGlCtx == null) {
      webGlCtx = this.canvas_8be2vx$.getContext('experimental-webgl2');
    }
    JsImpl_getInstance().isWebGl2Context = webGlCtx != null;
    var uint32Indices = false;
    var depthTextures = false;
    var shaderIntAttribs = false;
    var depthComponentIntFormat = GL_DEPTH_COMPONENT;
    var depthFilterMethod = GL_NEAREST;
    var framebufferWithoutColor = false;
    var anisotropicTexFilterInfo = AnisotropicTexFilterInfo$Companion_getInstance().NOT_SUPPORTED;
    var glslDialect = GlslDialect$Companion_getInstance().GLSL_DIALECT_100;
    var glVersion = new GlVersion('WebGL', 1, 0);
    if (webGlCtx != null) {
      this.gl_8be2vx$ = Kotlin.isType(tmp$_0 = webGlCtx, WebGL2RenderingContext) ? tmp$_0 : throwCCE();
      uint32Indices = true;
      depthTextures = true;
      shaderIntAttribs = true;
      depthComponentIntFormat = GL_DEPTH_COMPONENT24;
      framebufferWithoutColor = true;
      glslDialect = GlslDialect$Companion_getInstance().GLSL_DIALECT_300_ES;
      glVersion = new GlVersion('WebGL', 2, 0);
    }
     else {
      webGlCtx = this.canvas_8be2vx$.getContext('webgl');
      if (webGlCtx == null) {
        webGlCtx = this.canvas_8be2vx$.getContext('experimental-webgl');
      }
      if (webGlCtx == null) {
        alert('Unable to initialize WebGL. Your browser may not support it.');
      }
      this.gl_8be2vx$ = Kotlin.isType(tmp$_1 = webGlCtx, WebGLRenderingContext) ? tmp$_1 : throwCCE();
      uint32Indices = this.gl_8be2vx$.getExtension('OES_element_index_uint') != null;
      depthTextures = this.gl_8be2vx$.getExtension('WEBGL_depth_texture') != null;
    }
    var extAnisotropic = (tmp$_3 = (tmp$_2 = this.gl_8be2vx$.getExtension('EXT_texture_filter_anisotropic')) != null ? tmp$_2 : this.gl_8be2vx$.getExtension('MOZ_EXT_texture_filter_anisotropic')) != null ? tmp$_3 : this.gl_8be2vx$.getExtension('WEBKIT_EXT_texture_filter_anisotropic');
    if (extAnisotropic != null) {
      var max = typeof (tmp$_4 = this.gl_8be2vx$.getParameter(extAnisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT)) === 'number' ? tmp$_4 : throwCCE();
      anisotropicTexFilterInfo = new AnisotropicTexFilterInfo(max, extAnisotropic.TEXTURE_MAX_ANISOTROPY_EXT);
    }
    glCapabilities = new GlCapabilities(uint32Indices, shaderIntAttribs, depthTextures, depthComponentIntFormat, depthFilterMethod, framebufferWithoutColor, anisotropicTexFilterInfo, glslDialect, glVersion);
    this.screenDpi = JsImpl_getInstance().dpi;
    this.windowWidth = this.canvas_8be2vx$.clientWidth;
    this.windowHeight = this.canvas_8be2vx$.clientHeight;
    this.canvas_8be2vx$.onmousemove = JsContext_init$lambda(this);
    this.canvas_8be2vx$.onmousedown = JsContext_init$lambda_0(this);
    this.canvas_8be2vx$.onmouseup = JsContext_init$lambda_1(this);
    this.canvas_8be2vx$.oncontextmenu = getCallableRef('preventDefault', function ($receiver) {
      return $receiver.preventDefault(), Unit;
    });
    this.canvas_8be2vx$.onmouseenter = JsContext_init$lambda_2(this);
    this.canvas_8be2vx$.onmouseleave = JsContext_init$lambda_3(this);
    this.canvas_8be2vx$.onwheel = JsContext_init$lambda_4(this);
    document.onkeydown = JsContext_init$lambda_5(this);
    document.onkeyup = JsContext_init$lambda_6(this);
  }
  Object.defineProperty(JsContext.prototype, 'windowWidth', {
    get: function () {
      return this.windowWidth_7sdnb6$_0;
    },
    set: function (windowWidth) {
      this.windowWidth_7sdnb6$_0 = windowWidth;
    }
  });
  Object.defineProperty(JsContext.prototype, 'windowHeight', {
    get: function () {
      return this.windowHeight_xsf9ux$_0;
    },
    set: function (windowHeight) {
      this.windowHeight_xsf9ux$_0 = windowHeight;
    }
  });
  JsContext.prototype.handleKeyDown_0 = function (ev) {
    var code = this.translateKeyCode_0(ev.code);
    if (code !== 0) {
      var mods = 0;
      if (ev.altKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_ALT;
      }
      if (ev.ctrlKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_CTRL;
      }
      if (ev.shiftKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_SHIFT;
      }
      if (ev.metaKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_SUPER;
      }
      var event = InputManager$Companion_getInstance().KEY_EV_DOWN;
      if (ev.repeat) {
        event = event | InputManager$Companion_getInstance().KEY_EV_REPEATED;
      }
      this.inputMgr.keyEvent_qt1dr2$(code, mods, event);
    }
    if (ev.key.length === 1) {
      this.inputMgr.charTyped_s8itvh$(ev.key.charCodeAt(0));
    }
    if (!this.props.excludedKeyCodes.contains_11rb$(ev.code)) {
      ev.preventDefault();
    }
  };
  JsContext.prototype.handleKeyUp_0 = function (ev) {
    var code = this.translateKeyCode_0(ev.code);
    if (code !== 0) {
      var mods = 0;
      if (ev.altKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_ALT;
      }
      if (ev.ctrlKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_CTRL;
      }
      if (ev.shiftKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_SHIFT;
      }
      if (ev.metaKey) {
        mods = mods | InputManager$Companion_getInstance().KEY_MOD_SUPER;
      }
      this.inputMgr.keyEvent_qt1dr2$(code, mods, InputManager$Companion_getInstance().KEY_EV_UP);
    }
    if (!this.props.excludedKeyCodes.contains_11rb$(ev.code)) {
      ev.preventDefault();
    }
  };
  JsContext.prototype.translateKeyCode_0 = function (code) {
    var tmp$;
    if (code.length === 4 && startsWith(code, 'Key')) {
      return code.charCodeAt(3) | 0;
    }
     else {
      return (tmp$ = JsContext$Companion_getInstance().KEY_CODE_MAP.get_11rb$(code)) != null ? tmp$ : 0;
    }
  };
  function JsContext$renderFrame$lambda(this$JsContext) {
    return function (t) {
      this$JsContext.renderFrame_0(t);
      return Unit;
    };
  }
  JsContext.prototype.renderFrame_0 = function (time) {
    var dt = (time - this.animationMillis_0) / 1000.0;
    this.animationMillis_0 = time;
    this.windowWidth = this.canvas_8be2vx$.clientWidth;
    this.windowHeight = this.canvas_8be2vx$.clientHeight;
    if (this.windowWidth !== this.canvas_8be2vx$.width || this.windowHeight !== this.canvas_8be2vx$.height) {
      this.canvas_8be2vx$.width = this.windowWidth;
      this.canvas_8be2vx$.height = this.windowHeight;
    }
    this.render_14dthe$(dt);
    this.gl_8be2vx$.finish();
    window.requestAnimationFrame(JsContext$renderFrame$lambda(this));
  };
  function JsContext$run$lambda(this$JsContext) {
    return function (t) {
      this$JsContext.renderFrame_0(t);
      return Unit;
    };
  }
  JsContext.prototype.run = function () {
    window.requestAnimationFrame(JsContext$run$lambda(this));
  };
  JsContext.prototype.destroy = function () {
  };
  function JsContext$InitProps() {
    RenderContext$InitProps.call(this);
    this.canvasName = 'glCanvas';
    this.excludedKeyCodes = mutableSetOf(['F5']);
  }
  JsContext$InitProps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InitProps',
    interfaces: [RenderContext$InitProps]
  };
  function JsContext$Companion() {
    JsContext$Companion_instance = this;
    this.KEY_CODE_MAP = mutableMapOf([to('ControlLeft', InputManager$Companion_getInstance().KEY_CTRL_LEFT), to('ControlRight', InputManager$Companion_getInstance().KEY_CTRL_RIGHT), to('ShiftLeft', InputManager$Companion_getInstance().KEY_SHIFT_LEFT), to('ShiftRight', InputManager$Companion_getInstance().KEY_SHIFT_RIGHT), to('AltLeft', InputManager$Companion_getInstance().KEY_ALT_LEFT), to('AltRight', InputManager$Companion_getInstance().KEY_ALT_RIGHT), to('MetaLeft', InputManager$Companion_getInstance().KEY_SUPER_LEFT), to('MetaRight', InputManager$Companion_getInstance().KEY_SUPER_RIGHT), to('Escape', InputManager$Companion_getInstance().KEY_ESC), to('ContextMenu', InputManager$Companion_getInstance().KEY_MENU), to('Enter', InputManager$Companion_getInstance().KEY_ENTER), to('NumpadEnter', InputManager$Companion_getInstance().KEY_NP_ENTER), to('NumpadDivide', InputManager$Companion_getInstance().KEY_NP_DIV), to('NumpadMultiply', InputManager$Companion_getInstance().KEY_NP_MUL), to('NumpadAdd', InputManager$Companion_getInstance().KEY_NP_PLUS), to('NumpadSubtract', InputManager$Companion_getInstance().KEY_NP_MINUS), to('Backspace', InputManager$Companion_getInstance().KEY_BACKSPACE), to('Tab', InputManager$Companion_getInstance().KEY_TAB), to('Delete', InputManager$Companion_getInstance().KEY_DEL), to('Insert', InputManager$Companion_getInstance().KEY_INSERT), to('Home', InputManager$Companion_getInstance().KEY_HOME), to('End', InputManager$Companion_getInstance().KEY_END), to('PageUp', InputManager$Companion_getInstance().KEY_PAGE_UP), to('PageDown', InputManager$Companion_getInstance().KEY_PAGE_DOWN), to('ArrowLeft', InputManager$Companion_getInstance().KEY_CURSOR_LEFT), to('ArrowRight', InputManager$Companion_getInstance().KEY_CURSOR_RIGHT), to('ArrowUp', InputManager$Companion_getInstance().KEY_CURSOR_UP), to('ArrowDown', InputManager$Companion_getInstance().KEY_CURSOR_DOWN), to('F1', InputManager$Companion_getInstance().KEY_F1), to('F2', InputManager$Companion_getInstance().KEY_F2), to('F3', InputManager$Companion_getInstance().KEY_F3), to('F4', InputManager$Companion_getInstance().KEY_F4), to('F5', InputManager$Companion_getInstance().KEY_F5), to('F6', InputManager$Companion_getInstance().KEY_F6), to('F7', InputManager$Companion_getInstance().KEY_F7), to('F8', InputManager$Companion_getInstance().KEY_F8), to('F9', InputManager$Companion_getInstance().KEY_F9), to('F10', InputManager$Companion_getInstance().KEY_F10), to('F11', InputManager$Companion_getInstance().KEY_F11), to('F12', InputManager$Companion_getInstance().KEY_F12), to('Space', 32 | 0)]);
  }
  JsContext$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsContext$Companion_instance = null;
  function JsContext$Companion_getInstance() {
    if (JsContext$Companion_instance === null) {
      new JsContext$Companion();
    }
    return JsContext$Companion_instance;
  }
  function JsContext_init$lambda(this$JsContext) {
    return function (ev) {
      var tmp$;
      Kotlin.isType(tmp$ = ev, MouseEvent) ? tmp$ : throwCCE();
      var bounds = this$JsContext.canvas_8be2vx$.getBoundingClientRect();
      var x = ev.clientX - bounds.left;
      var y = ev.clientY - bounds.top;
      this$JsContext.inputMgr.updatePointerPos_nhq4am$(InputManager$Companion_getInstance().PRIMARY_POINTER, x, y);
      return Unit;
    };
  }
  function JsContext_init$lambda_0(this$JsContext) {
    return function (ev) {
      var tmp$;
      Kotlin.isType(tmp$ = ev, MouseEvent) ? tmp$ : throwCCE();
      this$JsContext.inputMgr.updatePointerButtonStates_vux9f0$(InputManager$Companion_getInstance().PRIMARY_POINTER, ev.buttons);
      return Unit;
    };
  }
  function JsContext_init$lambda_1(this$JsContext) {
    return function (ev) {
      var tmp$;
      Kotlin.isType(tmp$ = ev, MouseEvent) ? tmp$ : throwCCE();
      this$JsContext.inputMgr.updatePointerButtonStates_vux9f0$(InputManager$Companion_getInstance().PRIMARY_POINTER, ev.buttons);
      return Unit;
    };
  }
  function JsContext_init$lambda_2(this$JsContext) {
    return function (it) {
      this$JsContext.inputMgr.updatePointerValid_fzusl$(InputManager$Companion_getInstance().PRIMARY_POINTER, true);
      return Unit;
    };
  }
  function JsContext_init$lambda_3(this$JsContext) {
    return function (it) {
      this$JsContext.inputMgr.updatePointerValid_fzusl$(InputManager$Companion_getInstance().PRIMARY_POINTER, false);
      return Unit;
    };
  }
  function JsContext_init$lambda_4(this$JsContext) {
    return function (ev) {
      var tmp$;
      Kotlin.isType(tmp$ = ev, WheelEvent) ? tmp$ : throwCCE();
      var ticks = -ev.deltaY / 3.0;
      if (ev.deltaMode === 0) {
        ticks /= 30.0;
      }
      this$JsContext.inputMgr.updatePointerScrollPos_24o109$(InputManager$Companion_getInstance().PRIMARY_POINTER, ticks);
      ev.preventDefault();
      return Unit;
    };
  }
  function JsContext_init$lambda_5(this$JsContext) {
    return function (ev) {
      var tmp$, tmp$_0;
      tmp$_0 = Kotlin.isType(tmp$ = ev, KeyboardEvent) ? tmp$ : throwCCE();
      this$JsContext.handleKeyDown_0(tmp$_0);
      return Unit;
    };
  }
  function JsContext_init$lambda_6(this$JsContext) {
    return function (ev) {
      var tmp$, tmp$_0;
      tmp$_0 = Kotlin.isType(tmp$ = ev, KeyboardEvent) ? tmp$ : throwCCE();
      this$JsContext.handleKeyUp_0(tmp$_0);
      return Unit;
    };
  }
  JsContext.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsContext',
    interfaces: [RenderContext]
  };
  function GenericBuffer(capacity, create) {
    this.buffer = create();
    this.capacity_tfbtga$_0 = capacity;
    this.limit_wr5sjr$_0 = capacity;
    this.position_xkur2v$_0 = 0;
  }
  Object.defineProperty(GenericBuffer.prototype, 'capacity', {
    get: function () {
      return this.capacity_tfbtga$_0;
    }
  });
  Object.defineProperty(GenericBuffer.prototype, 'limit', {
    get: function () {
      return this.limit_wr5sjr$_0;
    },
    set: function (value) {
      if (value < 0 || value > this.capacity) {
        throw new KoolException('Limit is out of bounds: ' + value + ' (capacity: ' + this.capacity + ')');
      }
      this.limit_wr5sjr$_0 = value;
      if (this.position > value) {
        this.position = value;
      }
    }
  });
  Object.defineProperty(GenericBuffer.prototype, 'position', {
    get: function () {
      return this.position_xkur2v$_0;
    },
    set: function (position) {
      this.position_xkur2v$_0 = position;
    }
  });
  Object.defineProperty(GenericBuffer.prototype, 'remaining', {
    get: function () {
      return this.limit - this.position | 0;
    }
  });
  GenericBuffer.prototype.flip = function () {
    this.limit = this.position;
    this.position = 0;
  };
  GenericBuffer.prototype.clear = function () {
    this.limit = this.capacity;
    this.position = 0;
  };
  GenericBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GenericBuffer',
    interfaces: [Buffer]
  };
  function Uint8BufferImpl(capacity) {
    GenericBuffer.call(this, capacity, Uint8BufferImpl_init$lambda(capacity));
  }
  Uint8BufferImpl.prototype.put_mj6st8$ = function (data, offset, len) {
    var tmp$, tmp$_0;
    tmp$ = offset + len - 1 | 0;
    for (var i = offset; i <= tmp$; i++) {
      this.buffer[tmp$_0 = this.position, this.position = tmp$_0 + 1 | 0, tmp$_0] = data[i];
    }
    return this;
  };
  Uint8BufferImpl.prototype.put_11rb$ = function (value) {
    var tmp$;
    this.buffer[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = value;
    return this;
  };
  Uint8BufferImpl.prototype.put_axfmcw$ = function (data) {
    var tmp$, tmp$_0;
    tmp$ = data.position;
    tmp$_0 = data.limit;
    for (var i = tmp$; i < tmp$_0; i++) {
      this.put_11rb$(data.get_za3lpa$(i));
    }
    return this;
  };
  Uint8BufferImpl.prototype.get_za3lpa$ = function (i) {
    return this.buffer[i];
  };
  Uint8BufferImpl.prototype.set_wxm5ur$ = function (i, value) {
    this.buffer[i] = value;
  };
  function Uint8BufferImpl_init$lambda(closure$capacity) {
    return function () {
      return new Uint8Array(closure$capacity);
    };
  }
  Uint8BufferImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uint8BufferImpl',
    interfaces: [GenericBuffer, Uint8Buffer]
  };
  function Uint16BufferImpl(capacity) {
    GenericBuffer.call(this, capacity, Uint16BufferImpl_init$lambda(capacity));
  }
  Uint16BufferImpl.prototype.put_359eei$ = function (data, offset, len) {
    var tmp$, tmp$_0;
    tmp$ = offset + len - 1 | 0;
    for (var i = offset; i <= tmp$; i++) {
      this.buffer[tmp$_0 = this.position, this.position = tmp$_0 + 1 | 0, tmp$_0] = data[i];
    }
    return this;
  };
  Uint16BufferImpl.prototype.put_11rb$ = function (value) {
    var tmp$;
    this.buffer[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = value;
    return this;
  };
  Uint16BufferImpl.prototype.put_axfmcw$ = function (data) {
    var tmp$, tmp$_0;
    tmp$ = data.position;
    tmp$_0 = data.limit;
    for (var i = tmp$; i < tmp$_0; i++) {
      this.put_11rb$(data.get_za3lpa$(i));
    }
    return this;
  };
  Uint16BufferImpl.prototype.get_za3lpa$ = function (i) {
    return this.buffer[i];
  };
  Uint16BufferImpl.prototype.set_wxm5ur$ = function (i, value) {
    this.buffer[i] = value;
  };
  function Uint16BufferImpl_init$lambda(closure$capacity) {
    return function () {
      return new Uint16Array(closure$capacity);
    };
  }
  Uint16BufferImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uint16BufferImpl',
    interfaces: [GenericBuffer, Uint16Buffer]
  };
  function Uint32BufferImpl(capacity) {
    GenericBuffer.call(this, capacity, Uint32BufferImpl_init$lambda(capacity));
  }
  Uint32BufferImpl.prototype.put_nd5v6f$ = function (data, offset, len) {
    var tmp$, tmp$_0;
    tmp$ = offset + len - 1 | 0;
    for (var i = offset; i <= tmp$; i++) {
      this.buffer[tmp$_0 = this.position, this.position = tmp$_0 + 1 | 0, tmp$_0] = data[i];
    }
    return this;
  };
  Uint32BufferImpl.prototype.put_11rb$ = function (value) {
    var tmp$;
    this.buffer[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = value;
    return this;
  };
  Uint32BufferImpl.prototype.put_axfmcw$ = function (data) {
    var tmp$, tmp$_0;
    tmp$ = data.position;
    tmp$_0 = data.limit;
    for (var i = tmp$; i < tmp$_0; i++) {
      this.put_11rb$(data.get_za3lpa$(i));
    }
    return this;
  };
  Uint32BufferImpl.prototype.get_za3lpa$ = function (i) {
    return this.buffer[i];
  };
  Uint32BufferImpl.prototype.set_wxm5ur$ = function (i, value) {
    this.buffer[i] = value;
  };
  function Uint32BufferImpl_init$lambda(closure$capacity) {
    return function () {
      return new Uint32Array(closure$capacity);
    };
  }
  Uint32BufferImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uint32BufferImpl',
    interfaces: [GenericBuffer, Uint32Buffer]
  };
  function Float32BufferImpl(capacity) {
    GenericBuffer.call(this, capacity, Float32BufferImpl_init$lambda(capacity));
  }
  Float32BufferImpl.prototype.put_kgymra$ = function (data, offset, len) {
    var tmp$, tmp$_0;
    tmp$ = offset + len - 1 | 0;
    for (var i = offset; i <= tmp$; i++) {
      this.buffer[tmp$_0 = this.position, this.position = tmp$_0 + 1 | 0, tmp$_0] = data[i];
    }
    return this;
  };
  Float32BufferImpl.prototype.put_11rb$ = function (value) {
    var tmp$;
    this.buffer[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = value;
    return this;
  };
  Float32BufferImpl.prototype.put_axfmcw$ = function (data) {
    var tmp$, tmp$_0;
    tmp$ = data.position;
    tmp$_0 = data.limit;
    for (var i = tmp$; i < tmp$_0; i++) {
      this.put_11rb$(data.get_za3lpa$(i));
    }
    return this;
  };
  Float32BufferImpl.prototype.get_za3lpa$ = function (i) {
    return this.buffer[i];
  };
  Float32BufferImpl.prototype.set_wxm5ur$ = function (i, value) {
    this.buffer[i] = value;
  };
  function Float32BufferImpl_init$lambda(closure$capacity) {
    return function () {
      return new Float32Array(closure$capacity);
    };
  }
  Float32BufferImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Float32BufferImpl',
    interfaces: [GenericBuffer, Float32Buffer]
  };
  function createUint8Buffer(capacity) {
    return new Uint8BufferImpl(capacity);
  }
  function createUint16Buffer(capacity) {
    return new Uint16BufferImpl(capacity);
  }
  function createUint32Buffer(capacity) {
    return new Uint32BufferImpl(capacity);
  }
  function createFloat32Buffer(capacity) {
    return new Float32BufferImpl(capacity);
  }
  Object.defineProperty(GlCapabilities, 'Companion', {
    get: GlCapabilities$Companion_getInstance
  });
  var package$de = _.de || (_.de = {});
  var package$fabmax = package$de.fabmax || (package$de.fabmax = {});
  var package$kool = package$fabmax.kool || (package$fabmax.kool = {});
  package$kool.GlCapabilities = GlCapabilities;
  package$kool.GlVersion = GlVersion;
  Object.defineProperty(AnisotropicTexFilterInfo, 'Companion', {
    get: AnisotropicTexFilterInfo$Companion_getInstance
  });
  package$kool.AnisotropicTexFilterInfo = AnisotropicTexFilterInfo;
  Object.defineProperty(GlslDialect, 'Companion', {
    get: GlslDialect$Companion_getInstance
  });
  package$kool.GlslDialect = GlslDialect;
  Object.defineProperty(InputManager$DragHandler, 'Companion', {
    get: InputManager$DragHandler$Companion_getInstance
  });
  InputManager.DragHandler = InputManager$DragHandler;
  InputManager.Pointer = InputManager$Pointer;
  InputManager.KeyEvent = InputManager$KeyEvent;
  Object.defineProperty(InputManager, 'Companion', {
    get: InputManager$Companion_getInstance
  });
  package$kool.InputManager = InputManager;
  package$kool.KoolException = KoolException;
  package$kool.MemoryManager = MemoryManager;
  package$kool.MvpState = MvpState;
  RenderContext.InitProps = RenderContext$InitProps;
  RenderContext.Viewport = RenderContext$Viewport;
  package$kool.RenderContext = RenderContext;
  Object.defineProperty(RenderPass, 'SHADOW', {
    get: RenderPass$SHADOW_getInstance
  });
  Object.defineProperty(RenderPass, 'SCREEN', {
    get: RenderPass$SCREEN_getInstance
  });
  package$kool.RenderPass = RenderPass;
  package$kool.ShaderManager = ShaderManager;
  SharedResManager.SharedResource = SharedResManager$SharedResource;
  package$kool.SharedResManager = SharedResManager;
  package$kool.defaultProps_61zpoe$ = defaultProps;
  Object.defineProperty(TextureProps, 'Companion', {
    get: TextureProps$Companion_getInstance
  });
  package$kool.TextureProps_init_3m52m6$ = TextureProps_init;
  package$kool.TextureProps_init_wfrsr4$ = TextureProps_init_0;
  package$kool.TextureProps = TextureProps;
  package$kool.TextureData = TextureData;
  package$kool.BufferedTextureData = BufferedTextureData;
  package$kool.Texture = Texture;
  package$kool.assetTexture_61zpoe$ = assetTexture;
  package$kool.assetTexture_46ie3i$ = assetTexture_0;
  Object.defineProperty(TextureManager, 'Companion', {
    get: TextureManager$Companion_getInstance
  });
  package$kool.TextureManager = TextureManager;
  var package$audio = package$kool.audio || (package$kool.audio = {});
  package$audio.LowPassFilter = LowPassFilter;
  package$audio.HighPassFilter = HighPassFilter;
  Object.defineProperty(MoodFilter, 'Companion', {
    get: MoodFilter$Companion_getInstance
  });
  package$audio.MoodFilter = MoodFilter;
  package$audio.HiHat = HiHat;
  package$audio.Kick = Kick;
  package$audio.Melody = Melody;
  $$importsForInline$$.kool = _;
  package$audio.Oscillator = Oscillator;
  package$audio.Pad = Pad;
  Object.defineProperty(SampleNode, 'Companion', {
    get: SampleNode$Companion_getInstance
  });
  package$audio.SampleNode = SampleNode;
  package$audio.Shaker = Shaker;
  package$audio.Snare = Snare;
  Object.defineProperty(Wave, 'Companion', {
    get: Wave$Companion_getInstance
  });
  package$audio.Wave = Wave;
  var package$gl = package$kool.gl || (package$kool.gl = {});
  Object.defineProperty(package$gl, 'GL_ACTIVE_TEXTURE', {
    get: function () {
      return GL_ACTIVE_TEXTURE;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_BUFFER_BIT', {
    get: function () {
      return GL_DEPTH_BUFFER_BIT;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BUFFER_BIT', {
    get: function () {
      return GL_STENCIL_BUFFER_BIT;
    }
  });
  Object.defineProperty(package$gl, 'GL_COLOR_BUFFER_BIT', {
    get: function () {
      return GL_COLOR_BUFFER_BIT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FALSE', {
    get: function () {
      return GL_FALSE;
    }
  });
  Object.defineProperty(package$gl, 'GL_TRUE', {
    get: function () {
      return GL_TRUE;
    }
  });
  Object.defineProperty(package$gl, 'GL_POINTS', {
    get: function () {
      return GL_POINTS;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINES', {
    get: function () {
      return GL_LINES;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINE_LOOP', {
    get: function () {
      return GL_LINE_LOOP;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINE_STRIP', {
    get: function () {
      return GL_LINE_STRIP;
    }
  });
  Object.defineProperty(package$gl, 'GL_TRIANGLES', {
    get: function () {
      return GL_TRIANGLES;
    }
  });
  Object.defineProperty(package$gl, 'GL_TRIANGLE_STRIP', {
    get: function () {
      return GL_TRIANGLE_STRIP;
    }
  });
  Object.defineProperty(package$gl, 'GL_TRIANGLE_FAN', {
    get: function () {
      return GL_TRIANGLE_FAN;
    }
  });
  Object.defineProperty(package$gl, 'GL_ZERO', {
    get: function () {
      return GL_ZERO;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE', {
    get: function () {
      return GL_ONE;
    }
  });
  Object.defineProperty(package$gl, 'GL_SRC_COLOR', {
    get: function () {
      return GL_SRC_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE_MINUS_SRC_COLOR', {
    get: function () {
      return GL_ONE_MINUS_SRC_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_SRC_ALPHA', {
    get: function () {
      return GL_SRC_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE_MINUS_SRC_ALPHA', {
    get: function () {
      return GL_ONE_MINUS_SRC_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_DST_ALPHA', {
    get: function () {
      return GL_DST_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE_MINUS_DST_ALPHA', {
    get: function () {
      return GL_ONE_MINUS_DST_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_DST_COLOR', {
    get: function () {
      return GL_DST_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE_MINUS_DST_COLOR', {
    get: function () {
      return GL_ONE_MINUS_DST_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_SRC_ALPHA_SATURATE', {
    get: function () {
      return GL_SRC_ALPHA_SATURATE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FUNC_ADD', {
    get: function () {
      return GL_FUNC_ADD;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_EQUATION', {
    get: function () {
      return GL_BLEND_EQUATION;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_EQUATION_RGB', {
    get: function () {
      return GL_BLEND_EQUATION_RGB;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_EQUATION_ALPHA', {
    get: function () {
      return GL_BLEND_EQUATION_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_FUNC_SUBTRACT', {
    get: function () {
      return GL_FUNC_SUBTRACT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FUNC_REVERSE_SUBTRACT', {
    get: function () {
      return GL_FUNC_REVERSE_SUBTRACT;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_DST_RGB', {
    get: function () {
      return GL_BLEND_DST_RGB;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_SRC_RGB', {
    get: function () {
      return GL_BLEND_SRC_RGB;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_DST_ALPHA', {
    get: function () {
      return GL_BLEND_DST_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_SRC_ALPHA', {
    get: function () {
      return GL_BLEND_SRC_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_CONSTANT_COLOR', {
    get: function () {
      return GL_CONSTANT_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE_MINUS_CONSTANT_COLOR', {
    get: function () {
      return GL_ONE_MINUS_CONSTANT_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_CONSTANT_ALPHA', {
    get: function () {
      return GL_CONSTANT_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_ONE_MINUS_CONSTANT_ALPHA', {
    get: function () {
      return GL_ONE_MINUS_CONSTANT_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND_COLOR', {
    get: function () {
      return GL_BLEND_COLOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_ARRAY_BUFFER', {
    get: function () {
      return GL_ARRAY_BUFFER;
    }
  });
  Object.defineProperty(package$gl, 'GL_ELEMENT_ARRAY_BUFFER', {
    get: function () {
      return GL_ELEMENT_ARRAY_BUFFER;
    }
  });
  Object.defineProperty(package$gl, 'GL_ARRAY_BUFFER_BINDING', {
    get: function () {
      return GL_ARRAY_BUFFER_BINDING;
    }
  });
  Object.defineProperty(package$gl, 'GL_ELEMENT_ARRAY_BUFFER_BINDING', {
    get: function () {
      return GL_ELEMENT_ARRAY_BUFFER_BINDING;
    }
  });
  Object.defineProperty(package$gl, 'GL_STREAM_DRAW', {
    get: function () {
      return GL_STREAM_DRAW;
    }
  });
  Object.defineProperty(package$gl, 'GL_STATIC_DRAW', {
    get: function () {
      return GL_STATIC_DRAW;
    }
  });
  Object.defineProperty(package$gl, 'GL_DYNAMIC_DRAW', {
    get: function () {
      return GL_DYNAMIC_DRAW;
    }
  });
  Object.defineProperty(package$gl, 'GL_BUFFER_SIZE', {
    get: function () {
      return GL_BUFFER_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_BUFFER_USAGE', {
    get: function () {
      return GL_BUFFER_USAGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_CURRENT_VERTEX_ATTRIB', {
    get: function () {
      return GL_CURRENT_VERTEX_ATTRIB;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRONT', {
    get: function () {
      return GL_FRONT;
    }
  });
  Object.defineProperty(package$gl, 'GL_BACK', {
    get: function () {
      return GL_BACK;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRONT_AND_BACK', {
    get: function () {
      return GL_FRONT_AND_BACK;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_2D', {
    get: function () {
      return GL_TEXTURE_2D;
    }
  });
  Object.defineProperty(package$gl, 'GL_CULL_FACE', {
    get: function () {
      return GL_CULL_FACE;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLEND', {
    get: function () {
      return GL_BLEND;
    }
  });
  Object.defineProperty(package$gl, 'GL_DITHER', {
    get: function () {
      return GL_DITHER;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_TEST', {
    get: function () {
      return GL_STENCIL_TEST;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_TEST', {
    get: function () {
      return GL_DEPTH_TEST;
    }
  });
  Object.defineProperty(package$gl, 'GL_SCISSOR_TEST', {
    get: function () {
      return GL_SCISSOR_TEST;
    }
  });
  Object.defineProperty(package$gl, 'GL_POLYGON_OFFSET_FILL', {
    get: function () {
      return GL_POLYGON_OFFSET_FILL;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLE_ALPHA_TO_COVERAGE', {
    get: function () {
      return GL_SAMPLE_ALPHA_TO_COVERAGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLE_COVERAGE', {
    get: function () {
      return GL_SAMPLE_COVERAGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_NO_ERROR', {
    get: function () {
      return GL_NO_ERROR;
    }
  });
  Object.defineProperty(package$gl, 'GL_INVALID_ENUM', {
    get: function () {
      return GL_INVALID_ENUM;
    }
  });
  Object.defineProperty(package$gl, 'GL_INVALID_VALUE', {
    get: function () {
      return GL_INVALID_VALUE;
    }
  });
  Object.defineProperty(package$gl, 'GL_INVALID_OPERATION', {
    get: function () {
      return GL_INVALID_OPERATION;
    }
  });
  Object.defineProperty(package$gl, 'GL_OUT_OF_MEMORY', {
    get: function () {
      return GL_OUT_OF_MEMORY;
    }
  });
  Object.defineProperty(package$gl, 'GL_CW', {
    get: function () {
      return GL_CW;
    }
  });
  Object.defineProperty(package$gl, 'GL_CCW', {
    get: function () {
      return GL_CCW;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINE_WIDTH', {
    get: function () {
      return GL_LINE_WIDTH;
    }
  });
  Object.defineProperty(package$gl, 'GL_ALIASED_POINT_SIZE_RANGE', {
    get: function () {
      return GL_ALIASED_POINT_SIZE_RANGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_ALIASED_LINE_WIDTH_RANGE', {
    get: function () {
      return GL_ALIASED_LINE_WIDTH_RANGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_CULL_FACE_MODE', {
    get: function () {
      return GL_CULL_FACE_MODE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRONT_FACE', {
    get: function () {
      return GL_FRONT_FACE;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_RANGE', {
    get: function () {
      return GL_DEPTH_RANGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_WRITEMASK', {
    get: function () {
      return GL_DEPTH_WRITEMASK;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_CLEAR_VALUE', {
    get: function () {
      return GL_DEPTH_CLEAR_VALUE;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_FUNC', {
    get: function () {
      return GL_DEPTH_FUNC;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_CLEAR_VALUE', {
    get: function () {
      return GL_STENCIL_CLEAR_VALUE;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_FUNC', {
    get: function () {
      return GL_STENCIL_FUNC;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_FAIL', {
    get: function () {
      return GL_STENCIL_FAIL;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_PASS_DEPTH_FAIL', {
    get: function () {
      return GL_STENCIL_PASS_DEPTH_FAIL;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_PASS_DEPTH_PASS', {
    get: function () {
      return GL_STENCIL_PASS_DEPTH_PASS;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_REF', {
    get: function () {
      return GL_STENCIL_REF;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_VALUE_MASK', {
    get: function () {
      return GL_STENCIL_VALUE_MASK;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_WRITEMASK', {
    get: function () {
      return GL_STENCIL_WRITEMASK;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_FUNC', {
    get: function () {
      return GL_STENCIL_BACK_FUNC;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_FAIL', {
    get: function () {
      return GL_STENCIL_BACK_FAIL;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_PASS_DEPTH_FAIL', {
    get: function () {
      return GL_STENCIL_BACK_PASS_DEPTH_FAIL;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_PASS_DEPTH_PASS', {
    get: function () {
      return GL_STENCIL_BACK_PASS_DEPTH_PASS;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_REF', {
    get: function () {
      return GL_STENCIL_BACK_REF;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_VALUE_MASK', {
    get: function () {
      return GL_STENCIL_BACK_VALUE_MASK;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BACK_WRITEMASK', {
    get: function () {
      return GL_STENCIL_BACK_WRITEMASK;
    }
  });
  Object.defineProperty(package$gl, 'GL_VIEWPORT', {
    get: function () {
      return GL_VIEWPORT;
    }
  });
  Object.defineProperty(package$gl, 'GL_SCISSOR_BOX', {
    get: function () {
      return GL_SCISSOR_BOX;
    }
  });
  Object.defineProperty(package$gl, 'GL_COLOR_CLEAR_VALUE', {
    get: function () {
      return GL_COLOR_CLEAR_VALUE;
    }
  });
  Object.defineProperty(package$gl, 'GL_COLOR_WRITEMASK', {
    get: function () {
      return GL_COLOR_WRITEMASK;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNPACK_ALIGNMENT', {
    get: function () {
      return GL_UNPACK_ALIGNMENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_PACK_ALIGNMENT', {
    get: function () {
      return GL_PACK_ALIGNMENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_TEXTURE_SIZE', {
    get: function () {
      return GL_MAX_TEXTURE_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_VIEWPORT_DIMS', {
    get: function () {
      return GL_MAX_VIEWPORT_DIMS;
    }
  });
  Object.defineProperty(package$gl, 'GL_SUBPIXEL_BITS', {
    get: function () {
      return GL_SUBPIXEL_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_RED_BITS', {
    get: function () {
      return GL_RED_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_GREEN_BITS', {
    get: function () {
      return GL_GREEN_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_BLUE_BITS', {
    get: function () {
      return GL_BLUE_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_ALPHA_BITS', {
    get: function () {
      return GL_ALPHA_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_BITS', {
    get: function () {
      return GL_DEPTH_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_BITS', {
    get: function () {
      return GL_STENCIL_BITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_POLYGON_OFFSET_UNITS', {
    get: function () {
      return GL_POLYGON_OFFSET_UNITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_POLYGON_OFFSET_FACTOR', {
    get: function () {
      return GL_POLYGON_OFFSET_FACTOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_BINDING_2D', {
    get: function () {
      return GL_TEXTURE_BINDING_2D;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLE_BUFFERS', {
    get: function () {
      return GL_SAMPLE_BUFFERS;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLES', {
    get: function () {
      return GL_SAMPLES;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLE_COVERAGE_VALUE', {
    get: function () {
      return GL_SAMPLE_COVERAGE_VALUE;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLE_COVERAGE_INVERT', {
    get: function () {
      return GL_SAMPLE_COVERAGE_INVERT;
    }
  });
  Object.defineProperty(package$gl, 'GL_NUM_COMPRESSED_TEXTURE_FORMATS', {
    get: function () {
      return GL_NUM_COMPRESSED_TEXTURE_FORMATS;
    }
  });
  Object.defineProperty(package$gl, 'GL_COMPRESSED_TEXTURE_FORMATS', {
    get: function () {
      return GL_COMPRESSED_TEXTURE_FORMATS;
    }
  });
  Object.defineProperty(package$gl, 'GL_DONT_CARE', {
    get: function () {
      return GL_DONT_CARE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FASTEST', {
    get: function () {
      return GL_FASTEST;
    }
  });
  Object.defineProperty(package$gl, 'GL_NICEST', {
    get: function () {
      return GL_NICEST;
    }
  });
  Object.defineProperty(package$gl, 'GL_GENERATE_MIPMAP_HINT', {
    get: function () {
      return GL_GENERATE_MIPMAP_HINT;
    }
  });
  Object.defineProperty(package$gl, 'GL_BYTE', {
    get: function () {
      return GL_BYTE;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNSIGNED_BYTE', {
    get: function () {
      return GL_UNSIGNED_BYTE;
    }
  });
  Object.defineProperty(package$gl, 'GL_SHORT', {
    get: function () {
      return GL_SHORT;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNSIGNED_SHORT', {
    get: function () {
      return GL_UNSIGNED_SHORT;
    }
  });
  Object.defineProperty(package$gl, 'GL_INT', {
    get: function () {
      return GL_INT;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNSIGNED_INT', {
    get: function () {
      return GL_UNSIGNED_INT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT', {
    get: function () {
      return GL_FLOAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FIXED', {
    get: function () {
      return GL_FIXED;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_COMPONENT', {
    get: function () {
      return GL_DEPTH_COMPONENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_COMPONENT24', {
    get: function () {
      return GL_DEPTH_COMPONENT24;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_COMPONENT32F', {
    get: function () {
      return GL_DEPTH_COMPONENT32F;
    }
  });
  Object.defineProperty(package$gl, 'GL_ALPHA', {
    get: function () {
      return GL_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_RGB', {
    get: function () {
      return GL_RGB;
    }
  });
  Object.defineProperty(package$gl, 'GL_RGBA', {
    get: function () {
      return GL_RGBA;
    }
  });
  Object.defineProperty(package$gl, 'GL_LUMINANCE', {
    get: function () {
      return GL_LUMINANCE;
    }
  });
  Object.defineProperty(package$gl, 'GL_LUMINANCE_ALPHA', {
    get: function () {
      return GL_LUMINANCE_ALPHA;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNSIGNED_SHORT_4_4_4_4', {
    get: function () {
      return GL_UNSIGNED_SHORT_4_4_4_4;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNSIGNED_SHORT_5_5_5_1', {
    get: function () {
      return GL_UNSIGNED_SHORT_5_5_5_1;
    }
  });
  Object.defineProperty(package$gl, 'GL_UNSIGNED_SHORT_5_6_5', {
    get: function () {
      return GL_UNSIGNED_SHORT_5_6_5;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAGMENT_SHADER', {
    get: function () {
      return GL_FRAGMENT_SHADER;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_SHADER', {
    get: function () {
      return GL_VERTEX_SHADER;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_VERTEX_ATTRIBS', {
    get: function () {
      return GL_MAX_VERTEX_ATTRIBS;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_VERTEX_UNIFORM_VECTORS', {
    get: function () {
      return GL_MAX_VERTEX_UNIFORM_VECTORS;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_VARYING_VECTORS', {
    get: function () {
      return GL_MAX_VARYING_VECTORS;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS', {
    get: function () {
      return GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS', {
    get: function () {
      return GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_TEXTURE_IMAGE_UNITS', {
    get: function () {
      return GL_MAX_TEXTURE_IMAGE_UNITS;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_FRAGMENT_UNIFORM_VECTORS', {
    get: function () {
      return GL_MAX_FRAGMENT_UNIFORM_VECTORS;
    }
  });
  Object.defineProperty(package$gl, 'GL_SHADER_TYPE', {
    get: function () {
      return GL_SHADER_TYPE;
    }
  });
  Object.defineProperty(package$gl, 'GL_DELETE_STATUS', {
    get: function () {
      return GL_DELETE_STATUS;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINK_STATUS', {
    get: function () {
      return GL_LINK_STATUS;
    }
  });
  Object.defineProperty(package$gl, 'GL_VALIDATE_STATUS', {
    get: function () {
      return GL_VALIDATE_STATUS;
    }
  });
  Object.defineProperty(package$gl, 'GL_ATTACHED_SHADERS', {
    get: function () {
      return GL_ATTACHED_SHADERS;
    }
  });
  Object.defineProperty(package$gl, 'GL_ACTIVE_UNIFORMS', {
    get: function () {
      return GL_ACTIVE_UNIFORMS;
    }
  });
  Object.defineProperty(package$gl, 'GL_ACTIVE_UNIFORM_MAX_LENGTH', {
    get: function () {
      return GL_ACTIVE_UNIFORM_MAX_LENGTH;
    }
  });
  Object.defineProperty(package$gl, 'GL_ACTIVE_ATTRIBUTES', {
    get: function () {
      return GL_ACTIVE_ATTRIBUTES;
    }
  });
  Object.defineProperty(package$gl, 'GL_ACTIVE_ATTRIBUTE_MAX_LENGTH', {
    get: function () {
      return GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
    }
  });
  Object.defineProperty(package$gl, 'GL_SHADING_LANGUAGE_VERSION', {
    get: function () {
      return GL_SHADING_LANGUAGE_VERSION;
    }
  });
  Object.defineProperty(package$gl, 'GL_CURRENT_PROGRAM', {
    get: function () {
      return GL_CURRENT_PROGRAM;
    }
  });
  Object.defineProperty(package$gl, 'GL_NEVER', {
    get: function () {
      return GL_NEVER;
    }
  });
  Object.defineProperty(package$gl, 'GL_LESS', {
    get: function () {
      return GL_LESS;
    }
  });
  Object.defineProperty(package$gl, 'GL_EQUAL', {
    get: function () {
      return GL_EQUAL;
    }
  });
  Object.defineProperty(package$gl, 'GL_LEQUAL', {
    get: function () {
      return GL_LEQUAL;
    }
  });
  Object.defineProperty(package$gl, 'GL_GREATER', {
    get: function () {
      return GL_GREATER;
    }
  });
  Object.defineProperty(package$gl, 'GL_NOTEQUAL', {
    get: function () {
      return GL_NOTEQUAL;
    }
  });
  Object.defineProperty(package$gl, 'GL_GEQUAL', {
    get: function () {
      return GL_GEQUAL;
    }
  });
  Object.defineProperty(package$gl, 'GL_ALWAYS', {
    get: function () {
      return GL_ALWAYS;
    }
  });
  Object.defineProperty(package$gl, 'GL_KEEP', {
    get: function () {
      return GL_KEEP;
    }
  });
  Object.defineProperty(package$gl, 'GL_REPLACE', {
    get: function () {
      return GL_REPLACE;
    }
  });
  Object.defineProperty(package$gl, 'GL_INCR', {
    get: function () {
      return GL_INCR;
    }
  });
  Object.defineProperty(package$gl, 'GL_DECR', {
    get: function () {
      return GL_DECR;
    }
  });
  Object.defineProperty(package$gl, 'GL_INVERT', {
    get: function () {
      return GL_INVERT;
    }
  });
  Object.defineProperty(package$gl, 'GL_INCR_WRAP', {
    get: function () {
      return GL_INCR_WRAP;
    }
  });
  Object.defineProperty(package$gl, 'GL_DECR_WRAP', {
    get: function () {
      return GL_DECR_WRAP;
    }
  });
  Object.defineProperty(package$gl, 'GL_VENDOR', {
    get: function () {
      return GL_VENDOR;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERER', {
    get: function () {
      return GL_RENDERER;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERSION', {
    get: function () {
      return GL_VERSION;
    }
  });
  Object.defineProperty(package$gl, 'GL_EXTENSIONS', {
    get: function () {
      return GL_EXTENSIONS;
    }
  });
  Object.defineProperty(package$gl, 'GL_NEAREST', {
    get: function () {
      return GL_NEAREST;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINEAR', {
    get: function () {
      return GL_LINEAR;
    }
  });
  Object.defineProperty(package$gl, 'GL_NEAREST_MIPMAP_NEAREST', {
    get: function () {
      return GL_NEAREST_MIPMAP_NEAREST;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINEAR_MIPMAP_NEAREST', {
    get: function () {
      return GL_LINEAR_MIPMAP_NEAREST;
    }
  });
  Object.defineProperty(package$gl, 'GL_NEAREST_MIPMAP_LINEAR', {
    get: function () {
      return GL_NEAREST_MIPMAP_LINEAR;
    }
  });
  Object.defineProperty(package$gl, 'GL_LINEAR_MIPMAP_LINEAR', {
    get: function () {
      return GL_LINEAR_MIPMAP_LINEAR;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_MAG_FILTER', {
    get: function () {
      return GL_TEXTURE_MAG_FILTER;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_MIN_FILTER', {
    get: function () {
      return GL_TEXTURE_MIN_FILTER;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_WRAP_S', {
    get: function () {
      return GL_TEXTURE_WRAP_S;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_WRAP_T', {
    get: function () {
      return GL_TEXTURE_WRAP_T;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE', {
    get: function () {
      return GL_TEXTURE;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_BINDING_CUBE_MAP', {
    get: function () {
      return GL_TEXTURE_BINDING_CUBE_MAP;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP_POSITIVE_X', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_POSITIVE_X;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP_NEGATIVE_X', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP_POSITIVE_Y', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP_NEGATIVE_Y', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP_POSITIVE_Z', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE_CUBE_MAP_NEGATIVE_Z', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_CUBE_MAP_TEXTURE_SIZE', {
    get: function () {
      return GL_MAX_CUBE_MAP_TEXTURE_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE0', {
    get: function () {
      return GL_TEXTURE0;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE1', {
    get: function () {
      return GL_TEXTURE1;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE2', {
    get: function () {
      return GL_TEXTURE2;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE3', {
    get: function () {
      return GL_TEXTURE3;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE4', {
    get: function () {
      return GL_TEXTURE4;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE5', {
    get: function () {
      return GL_TEXTURE5;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE6', {
    get: function () {
      return GL_TEXTURE6;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE7', {
    get: function () {
      return GL_TEXTURE7;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE8', {
    get: function () {
      return GL_TEXTURE8;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE9', {
    get: function () {
      return GL_TEXTURE9;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE10', {
    get: function () {
      return GL_TEXTURE10;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE11', {
    get: function () {
      return GL_TEXTURE11;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE12', {
    get: function () {
      return GL_TEXTURE12;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE13', {
    get: function () {
      return GL_TEXTURE13;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE14', {
    get: function () {
      return GL_TEXTURE14;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE15', {
    get: function () {
      return GL_TEXTURE15;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE16', {
    get: function () {
      return GL_TEXTURE16;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE17', {
    get: function () {
      return GL_TEXTURE17;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE18', {
    get: function () {
      return GL_TEXTURE18;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE19', {
    get: function () {
      return GL_TEXTURE19;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE20', {
    get: function () {
      return GL_TEXTURE20;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE21', {
    get: function () {
      return GL_TEXTURE21;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE22', {
    get: function () {
      return GL_TEXTURE22;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE23', {
    get: function () {
      return GL_TEXTURE23;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE24', {
    get: function () {
      return GL_TEXTURE24;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE25', {
    get: function () {
      return GL_TEXTURE25;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE26', {
    get: function () {
      return GL_TEXTURE26;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE27', {
    get: function () {
      return GL_TEXTURE27;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE28', {
    get: function () {
      return GL_TEXTURE28;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE29', {
    get: function () {
      return GL_TEXTURE29;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE30', {
    get: function () {
      return GL_TEXTURE30;
    }
  });
  Object.defineProperty(package$gl, 'GL_TEXTURE31', {
    get: function () {
      return GL_TEXTURE31;
    }
  });
  Object.defineProperty(package$gl, 'GL_REPEAT', {
    get: function () {
      return GL_REPEAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_CLAMP_TO_EDGE', {
    get: function () {
      return GL_CLAMP_TO_EDGE;
    }
  });
  Object.defineProperty(package$gl, 'GL_MIRRORED_REPEAT', {
    get: function () {
      return GL_MIRRORED_REPEAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT_VEC2', {
    get: function () {
      return GL_FLOAT_VEC2;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT_VEC3', {
    get: function () {
      return GL_FLOAT_VEC3;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT_VEC4', {
    get: function () {
      return GL_FLOAT_VEC4;
    }
  });
  Object.defineProperty(package$gl, 'GL_INT_VEC2', {
    get: function () {
      return GL_INT_VEC2;
    }
  });
  Object.defineProperty(package$gl, 'GL_INT_VEC3', {
    get: function () {
      return GL_INT_VEC3;
    }
  });
  Object.defineProperty(package$gl, 'GL_INT_VEC4', {
    get: function () {
      return GL_INT_VEC4;
    }
  });
  Object.defineProperty(package$gl, 'GL_BOOL', {
    get: function () {
      return GL_BOOL;
    }
  });
  Object.defineProperty(package$gl, 'GL_BOOL_VEC2', {
    get: function () {
      return GL_BOOL_VEC2;
    }
  });
  Object.defineProperty(package$gl, 'GL_BOOL_VEC3', {
    get: function () {
      return GL_BOOL_VEC3;
    }
  });
  Object.defineProperty(package$gl, 'GL_BOOL_VEC4', {
    get: function () {
      return GL_BOOL_VEC4;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT_MAT2', {
    get: function () {
      return GL_FLOAT_MAT2;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT_MAT3', {
    get: function () {
      return GL_FLOAT_MAT3;
    }
  });
  Object.defineProperty(package$gl, 'GL_FLOAT_MAT4', {
    get: function () {
      return GL_FLOAT_MAT4;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLER_2D', {
    get: function () {
      return GL_SAMPLER_2D;
    }
  });
  Object.defineProperty(package$gl, 'GL_SAMPLER_CUBE', {
    get: function () {
      return GL_SAMPLER_CUBE;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_ENABLED', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_ENABLED;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_SIZE', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_STRIDE', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_STRIDE;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_TYPE', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_TYPE;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_NORMALIZED', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_POINTER', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_POINTER;
    }
  });
  Object.defineProperty(package$gl, 'GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
    }
  });
  Object.defineProperty(package$gl, 'GL_IMPLEMENTATION_COLOR_READ_TYPE', {
    get: function () {
      return GL_IMPLEMENTATION_COLOR_READ_TYPE;
    }
  });
  Object.defineProperty(package$gl, 'GL_IMPLEMENTATION_COLOR_READ_FORMAT', {
    get: function () {
      return GL_IMPLEMENTATION_COLOR_READ_FORMAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_COMPILE_STATUS', {
    get: function () {
      return GL_COMPILE_STATUS;
    }
  });
  Object.defineProperty(package$gl, 'GL_INFO_LOG_LENGTH', {
    get: function () {
      return GL_INFO_LOG_LENGTH;
    }
  });
  Object.defineProperty(package$gl, 'GL_SHADER_SOURCE_LENGTH', {
    get: function () {
      return GL_SHADER_SOURCE_LENGTH;
    }
  });
  Object.defineProperty(package$gl, 'GL_SHADER_COMPILER', {
    get: function () {
      return GL_SHADER_COMPILER;
    }
  });
  Object.defineProperty(package$gl, 'GL_SHADER_BINARY_FORMATS', {
    get: function () {
      return GL_SHADER_BINARY_FORMATS;
    }
  });
  Object.defineProperty(package$gl, 'GL_NUM_SHADER_BINARY_FORMATS', {
    get: function () {
      return GL_NUM_SHADER_BINARY_FORMATS;
    }
  });
  Object.defineProperty(package$gl, 'GL_LOW_FLOAT', {
    get: function () {
      return GL_LOW_FLOAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_MEDIUM_FLOAT', {
    get: function () {
      return GL_MEDIUM_FLOAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_HIGH_FLOAT', {
    get: function () {
      return GL_HIGH_FLOAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_LOW_INT', {
    get: function () {
      return GL_LOW_INT;
    }
  });
  Object.defineProperty(package$gl, 'GL_MEDIUM_INT', {
    get: function () {
      return GL_MEDIUM_INT;
    }
  });
  Object.defineProperty(package$gl, 'GL_HIGH_INT', {
    get: function () {
      return GL_HIGH_INT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER', {
    get: function () {
      return GL_FRAMEBUFFER;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER', {
    get: function () {
      return GL_RENDERBUFFER;
    }
  });
  Object.defineProperty(package$gl, 'GL_RGBA4', {
    get: function () {
      return GL_RGBA4;
    }
  });
  Object.defineProperty(package$gl, 'GL_RGB5_A1', {
    get: function () {
      return GL_RGB5_A1;
    }
  });
  Object.defineProperty(package$gl, 'GL_RGB565', {
    get: function () {
      return GL_RGB565;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_COMPONENT16', {
    get: function () {
      return GL_DEPTH_COMPONENT16;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_INDEX8', {
    get: function () {
      return GL_STENCIL_INDEX8;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_WIDTH', {
    get: function () {
      return GL_RENDERBUFFER_WIDTH;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_HEIGHT', {
    get: function () {
      return GL_RENDERBUFFER_HEIGHT;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_INTERNAL_FORMAT', {
    get: function () {
      return GL_RENDERBUFFER_INTERNAL_FORMAT;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_RED_SIZE', {
    get: function () {
      return GL_RENDERBUFFER_RED_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_GREEN_SIZE', {
    get: function () {
      return GL_RENDERBUFFER_GREEN_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_BLUE_SIZE', {
    get: function () {
      return GL_RENDERBUFFER_BLUE_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_ALPHA_SIZE', {
    get: function () {
      return GL_RENDERBUFFER_ALPHA_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_DEPTH_SIZE', {
    get: function () {
      return GL_RENDERBUFFER_DEPTH_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_STENCIL_SIZE', {
    get: function () {
      return GL_RENDERBUFFER_STENCIL_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE', {
    get: function () {
      return GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME', {
    get: function () {
      return GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL', {
    get: function () {
      return GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE', {
    get: function () {
      return GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE;
    }
  });
  Object.defineProperty(package$gl, 'GL_COLOR_ATTACHMENT0', {
    get: function () {
      return GL_COLOR_ATTACHMENT0;
    }
  });
  Object.defineProperty(package$gl, 'GL_DEPTH_ATTACHMENT', {
    get: function () {
      return GL_DEPTH_ATTACHMENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_STENCIL_ATTACHMENT', {
    get: function () {
      return GL_STENCIL_ATTACHMENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_NONE', {
    get: function () {
      return GL_NONE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_COMPLETE', {
    get: function () {
      return GL_FRAMEBUFFER_COMPLETE;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_UNSUPPORTED', {
    get: function () {
      return GL_FRAMEBUFFER_UNSUPPORTED;
    }
  });
  Object.defineProperty(package$gl, 'GL_FRAMEBUFFER_BINDING', {
    get: function () {
      return GL_FRAMEBUFFER_BINDING;
    }
  });
  Object.defineProperty(package$gl, 'GL_RENDERBUFFER_BINDING', {
    get: function () {
      return GL_RENDERBUFFER_BINDING;
    }
  });
  Object.defineProperty(package$gl, 'GL_MAX_RENDERBUFFER_SIZE', {
    get: function () {
      return GL_MAX_RENDERBUFFER_SIZE;
    }
  });
  Object.defineProperty(package$gl, 'GL_INVALID_FRAMEBUFFER_OPERATION', {
    get: function () {
      return GL_INVALID_FRAMEBUFFER_OPERATION;
    }
  });
  Object.defineProperty(BufferResource, 'Companion', {
    get: BufferResource$Companion_getInstance
  });
  package$gl.BufferResource = BufferResource;
  package$gl.Framebuffer = Framebuffer;
  Object.defineProperty(FramebufferResource, 'Companion', {
    get: FramebufferResource$Companion_getInstance
  });
  package$gl.FramebufferResource = FramebufferResource;
  package$gl.GlObject = GlObject;
  Object.defineProperty(GlResource$Type, 'BUFFER', {
    get: GlResource$Type$BUFFER_getInstance
  });
  Object.defineProperty(GlResource$Type, 'FRAMEBUFFER', {
    get: GlResource$Type$FRAMEBUFFER_getInstance
  });
  Object.defineProperty(GlResource$Type, 'PROGRAM', {
    get: GlResource$Type$PROGRAM_getInstance
  });
  Object.defineProperty(GlResource$Type, 'RENDERBUFFER', {
    get: GlResource$Type$RENDERBUFFER_getInstance
  });
  Object.defineProperty(GlResource$Type, 'SHADER', {
    get: GlResource$Type$SHADER_getInstance
  });
  Object.defineProperty(GlResource$Type, 'TEXTURE', {
    get: GlResource$Type$TEXTURE_getInstance
  });
  GlResource.Type = GlResource$Type;
  package$gl.GlResource = GlResource;
  Object.defineProperty(ProgramResource, 'Companion', {
    get: ProgramResource$Companion_getInstance
  });
  package$gl.ProgramResource = ProgramResource;
  Object.defineProperty(RenderbufferResource, 'Companion', {
    get: RenderbufferResource$Companion_getInstance
  });
  package$gl.RenderbufferResource = RenderbufferResource;
  Object.defineProperty(ShaderResource, 'Companion', {
    get: ShaderResource$Companion_getInstance
  });
  package$gl.ShaderResource = ShaderResource;
  Object.defineProperty(TextureResource, 'Companion', {
    get: TextureResource$Companion_getInstance
  });
  package$gl.TextureResource = TextureResource;
  var package$math = package$kool.math || (package$kool.math = {});
  package$math.BSpline = BSpline;
  package$math.BSplineVec2f = BSplineVec2f;
  package$math.BSplineVec3f = BSplineVec3f;
  package$math.pointTree_ffk80x$ = pointTree;
  package$math.KdTreeTraverser = KdTreeTraverser;
  package$math.InRadiusTraverser_init_34hdy3$ = InRadiusTraverser_init;
  package$math.InRadiusTraverser = InRadiusTraverser;
  Object.defineProperty(KdTree, 'Companion', {
    get: KdTree$Companion_getInstance
  });
  KdTree.Node = KdTree$Node;
  package$math.KdTree = KdTree;
  Object.defineProperty(Mat4f, 'Companion', {
    get: Mat4f$Companion_getInstance
  });
  package$math.Mat4f = Mat4f;
  Object.defineProperty(Mat4fStack, 'Companion', {
    get: Mat4fStack$Companion_getInstance
  });
  package$math.Mat4fStack = Mat4fStack;
  Object.defineProperty(package$math, 'DEG_2_RAD', {
    get: function () {
      return DEG_2_RAD;
    }
  });
  Object.defineProperty(package$math, 'RAD_2_DEG', {
    get: function () {
      return RAD_2_DEG;
    }
  });
  package$math.toDeg_81szk$ = toDeg;
  package$math.toRad_81szk$ = toRad;
  package$math.toDeg_yrwdxr$ = toDeg_0;
  package$math.toRad_yrwdxr$ = toRad_0;
  package$math.isZero_81szk$ = isZero;
  package$math.isEqual_dleff0$ = isEqual;
  package$math.isZero_yrwdxr$ = isZero_0;
  package$math.isEqual_lu1900$ = isEqual_0;
  package$math.clamp_e4yvb3$ = clamp;
  package$math.clamp_wj6e7o$ = clamp_0;
  package$math.clamp_nig4hr$ = clamp_1;
  package$math.Plane = Plane;
  package$math.PointDistribution = PointDistribution;
  package$math.CubicPointDistribution = CubicPointDistribution;
  package$math.SphericalPointDistribution = SphericalPointDistribution;
  Object.defineProperty(package$math, 'defaultRandomInstance', {
    get: function () {
      return defaultRandomInstance;
    }
  });
  package$math.randomI = randomI;
  package$math.randomI_vux9f0$ = randomI_0;
  package$math.randomD = randomD;
  package$math.randomD_lu1900$ = randomD_0;
  package$math.randomF = randomF;
  package$math.randomF_dleff0$ = randomF_0;
  package$math.Random = Random;
  package$math.Ray = Ray;
  package$math.RayTest = RayTest;
  package$math.add_drouu$ = add;
  package$math.add_4lfkt4$ = add_0;
  package$math.add_8t3gre$ = add_1;
  package$math.subtract_drouu$ = subtract;
  package$math.subtract_4lfkt4$ = subtract_0;
  package$math.subtract_8t3gre$ = subtract_1;
  package$math.scale_rnua8g$ = scale;
  package$math.scale_2qa7tb$ = scale_0;
  package$math.scale_m79ulu$ = scale_1;
  package$math.norm_czzhjp$ = norm;
  package$math.norm_czzhiu$ = norm_0;
  package$math.cross_4lfkt4$ = cross;
  package$math.slerp_m26pjg$ = slerp;
  Object.defineProperty(Vec2f, 'Companion', {
    get: Vec2f$Companion_getInstance
  });
  package$math.Vec2f_init_mx4ult$ = Vec2f_init;
  package$math.Vec2f_init_czzhjp$ = Vec2f_init_0;
  package$math.Vec2f = Vec2f;
  package$math.MutableVec2f_init = MutableVec2f_init;
  package$math.MutableVec2f_init_czzhjp$ = MutableVec2f_init_0;
  package$math.MutableVec2f = MutableVec2f;
  Object.defineProperty(Vec3f, 'Companion', {
    get: Vec3f$Companion_getInstance
  });
  package$math.Vec3f_init_mx4ult$ = Vec3f_init;
  package$math.Vec3f_init_czzhiu$ = Vec3f_init_0;
  package$math.Vec3f = Vec3f;
  package$math.MutableVec3f_init = MutableVec3f_init;
  package$math.MutableVec3f_init_czzhiu$ = MutableVec3f_init_0;
  package$math.MutableVec3f = MutableVec3f;
  Object.defineProperty(Vec4f, 'Companion', {
    get: Vec4f$Companion_getInstance
  });
  package$math.Vec4f_init_mx4ult$ = Vec4f_init;
  package$math.Vec4f_init_czzhhz$ = Vec4f_init_0;
  package$math.Vec4f = Vec4f;
  package$math.MutableVec4f_init = MutableVec4f_init;
  package$math.MutableVec4f_init_czzhhz$ = MutableVec4f_init_0;
  package$math.MutableVec4f = MutableVec4f;
  var package$scene = package$kool.scene || (package$kool.scene = {});
  package$scene.Camera = Camera;
  package$scene.OrthographicCamera = OrthographicCamera;
  package$scene.PerspectiveCamera = PerspectiveCamera;
  package$scene.FrustumPlane = FrustumPlane;
  package$scene.group_2ylazs$ = group;
  package$scene.Group = Group;
  package$scene.Light = Light;
  var package$shading = package$kool.shading || (package$kool.shading = {});
  package$shading.Attribute = Attribute;
  package$scene.mesh_w2gyes$ = mesh_1;
  package$scene.mesh_ki35ir$ = mesh;
  package$scene.mesh_tok25s$ = mesh_0;
  package$shading.LightModel = LightModel;
  package$shading.ColorModel = ColorModel;
  package$shading.basicShader_n50u2h$ = basicShader;
  package$scene.colorMesh_gp9ews$ = colorMesh;
  package$scene.textMesh_8mgi8m$ = textMesh;
  package$scene.textureMesh_pyaqjj$ = textureMesh;
  Object.defineProperty(CullMethod, 'DEFAULT', {
    get: CullMethod$DEFAULT_getInstance
  });
  Object.defineProperty(CullMethod, 'CULL_BACK_FACES', {
    get: CullMethod$CULL_BACK_FACES_getInstance
  });
  Object.defineProperty(CullMethod, 'CULL_FRONT_FACES', {
    get: CullMethod$CULL_FRONT_FACES_getInstance
  });
  Object.defineProperty(CullMethod, 'NO_CULLING', {
    get: CullMethod$NO_CULLING_getInstance
  });
  package$scene.CullMethod = CullMethod;
  package$scene.Mesh = Mesh;
  package$scene.MeshData_init_j0mu7e$ = MeshData_init;
  package$scene.MeshData = MeshData;
  Model.Geometry = Model$Geometry;
  package$scene.Model = Model;
  package$scene.Node = Node;
  package$scene.scene_13di2z$ = scene;
  package$scene.Scene = Scene;
  package$scene.sphericalInputTransform_6sxffc$ = sphericalInputTransform;
  Object.defineProperty(SphericalInputTransform$DragMethod, 'NONE', {
    get: SphericalInputTransform$DragMethod$NONE_getInstance
  });
  Object.defineProperty(SphericalInputTransform$DragMethod, 'ROTATE', {
    get: SphericalInputTransform$DragMethod$ROTATE_getInstance
  });
  Object.defineProperty(SphericalInputTransform$DragMethod, 'PAN', {
    get: SphericalInputTransform$DragMethod$PAN_getInstance
  });
  SphericalInputTransform.DragMethod = SphericalInputTransform$DragMethod;
  Object.defineProperty(SphericalInputTransform$ZoomMethod, 'ZOOM_CENTER', {
    get: SphericalInputTransform$ZoomMethod$ZOOM_CENTER_getInstance
  });
  Object.defineProperty(SphericalInputTransform$ZoomMethod, 'ZOOM_TRANSLATE', {
    get: SphericalInputTransform$ZoomMethod$ZOOM_TRANSLATE_getInstance
  });
  SphericalInputTransform.ZoomMethod = SphericalInputTransform$ZoomMethod;
  SphericalInputTransform.AnimatedVal = SphericalInputTransform$AnimatedVal;
  package$scene.SphericalInputTransform = SphericalInputTransform;
  package$scene.PanBase = PanBase;
  package$scene.CameraOrthogonalPan = CameraOrthogonalPan;
  package$scene.FixedPlanePan = FixedPlanePan;
  package$scene.xPlanePan = xPlanePan;
  package$scene.yPlanePan = yPlanePan;
  package$scene.zPlanePan = zPlanePan;
  package$scene.transformGroup_zaezuq$ = transformGroup;
  package$scene.TransformGroup = TransformGroup;
  var package$animation = package$scene.animation || (package$scene.animation = {});
  package$animation.Animation = Animation;
  Object.defineProperty(Armature, 'Companion', {
    get: Armature$Companion_getInstance
  });
  package$animation.Armature = Armature;
  package$animation.Bone = Bone;
  package$animation.AnimatedNode = AnimatedNode;
  package$animation.NodeAnimation = NodeAnimation;
  package$animation.AnimationKey = AnimationKey;
  package$animation.RotationKey = RotationKey;
  package$animation.PositionKey = PositionKey;
  package$animation.ScalingKey = ScalingKey;
  var package$ui = package$scene.ui || (package$scene.ui = {});
  package$ui.Button = Button;
  package$ui.ButtonUi = ButtonUi;
  package$ui.ComponentUi = ComponentUi;
  package$ui.BlankComponentUi = BlankComponentUi;
  package$ui.SimpleComponentUi = SimpleComponentUi;
  package$ui.BlurredComponentUi = BlurredComponentUi;
  package$ui.Label = Label;
  package$ui.LabelUi = LabelUi;
  package$ui.LayoutSpec = LayoutSpec;
  Object.defineProperty(SizeUnit, 'UN', {
    get: SizeUnit$UN_getInstance
  });
  Object.defineProperty(SizeUnit, 'DP', {
    get: SizeUnit$DP_getInstance
  });
  Object.defineProperty(SizeUnit, 'MM', {
    get: SizeUnit$MM_getInstance
  });
  Object.defineProperty(SizeUnit, 'PC', {
    get: SizeUnit$PC_getInstance
  });
  package$ui.SizeUnit = SizeUnit;
  package$ui.zero = zero;
  package$ui.uns_8ca0d4$ = uns;
  package$ui.dps_8ca0d4$ = dps;
  package$ui.mms_8ca0d4$ = mms;
  package$ui.pcs_8ca0d4$ = pcs;
  package$ui.pc_dleff0$ = pc;
  package$ui.dp_dleff0$ = dp;
  package$ui.mm_dleff0$ = mm;
  package$ui.pcW_wl4j30$ = pcW;
  package$ui.pcH_wl4j30$ = pcH;
  package$ui.dp_wl4j30$ = dp_0;
  package$ui.mm_wl4j30$ = mm_0;
  package$ui.pcR_dleff0$ = pcR;
  package$ui.dpR_dleff0$ = dpR;
  package$ui.mmR_dleff0$ = mmR;
  package$ui.pcWR_wl4j30$ = pcWR;
  package$ui.pcHR_wl4j30$ = pcHR;
  package$ui.dpR_wl4j30$ = dpR_0;
  package$ui.mmR_wl4j30$ = mmR_0;
  package$ui.SizeSpec = SizeSpec;
  package$ui.Margin = Margin;
  package$ui.Gravity = Gravity;
  Object.defineProperty(Alignment, 'START', {
    get: Alignment$START_getInstance
  });
  Object.defineProperty(Alignment, 'CENTER', {
    get: Alignment$CENTER_getInstance
  });
  Object.defineProperty(Alignment, 'END', {
    get: Alignment$END_getInstance
  });
  package$ui.Alignment = Alignment;
  package$ui.Slider = Slider;
  package$ui.SliderUi = SliderUi;
  package$ui.TextField = TextField;
  package$ui.TextFieldUi = TextFieldUi;
  Object.defineProperty(EditableText, 'Companion', {
    get: EditableText$Companion_getInstance
  });
  package$ui.EditableText = EditableText;
  package$ui.ToggleButton = ToggleButton;
  package$ui.ToggleButtonUi = ToggleButtonUi;
  package$ui.UiComponent = UiComponent;
  package$ui.UiContainer = UiContainer;
  package$ui.embeddedUi_o1x1d9$ = embeddedUi;
  package$ui.uiScene_7c31we$ = uiScene;
  package$ui.UiRoot = UiRoot;
  Object.defineProperty(UiTheme, 'Companion', {
    get: UiTheme$Companion_getInstance
  });
  package$ui.UiTheme = UiTheme;
  package$ui.theme_vvurn$ = theme;
  package$ui.ThemeBuilder = ThemeBuilder;
  package$ui.ThemeOrCustomProp = ThemeOrCustomProp;
  Object.defineProperty(AttributeType, 'FLOAT', {
    get: AttributeType$FLOAT_getInstance
  });
  Object.defineProperty(AttributeType, 'VEC_2F', {
    get: AttributeType$VEC_2F_getInstance
  });
  Object.defineProperty(AttributeType, 'VEC_3F', {
    get: AttributeType$VEC_3F_getInstance
  });
  Object.defineProperty(AttributeType, 'VEC_4F', {
    get: AttributeType$VEC_4F_getInstance
  });
  Object.defineProperty(AttributeType, 'COLOR_4F', {
    get: AttributeType$COLOR_4F_getInstance
  });
  Object.defineProperty(AttributeType, 'INT', {
    get: AttributeType$INT_getInstance
  });
  Object.defineProperty(AttributeType, 'VEC_2I', {
    get: AttributeType$VEC_2I_getInstance
  });
  Object.defineProperty(AttributeType, 'VEC_3I', {
    get: AttributeType$VEC_3I_getInstance
  });
  Object.defineProperty(AttributeType, 'VEC_4I', {
    get: AttributeType$VEC_4I_getInstance
  });
  package$shading.AttributeType = AttributeType;
  Object.defineProperty(Attribute, 'Companion', {
    get: Attribute$Companion_getInstance
  });
  package$shading.BasicShader = BasicShader;
  package$shading.basicPointShader_n50u2h$ = basicPointShader;
  Object.defineProperty(BasicPointShader, 'Companion', {
    get: BasicPointShader$Companion_getInstance
  });
  package$shading.BasicPointShader = BasicPointShader;
  package$shading.blurShader_n50u2h$ = blurShader;
  package$shading.BlurShader = BlurShader;
  Object.defineProperty(BlurredBackgroundHelper$BlurMethod, 'BLUR_9_TAP', {
    get: BlurredBackgroundHelper$BlurMethod$BLUR_9_TAP_getInstance
  });
  Object.defineProperty(BlurredBackgroundHelper$BlurMethod, 'BLUR_13_TAP', {
    get: BlurredBackgroundHelper$BlurMethod$BLUR_13_TAP_getInstance
  });
  BlurredBackgroundHelper.BlurMethod = BlurredBackgroundHelper$BlurMethod;
  package$shading.BlurredBackgroundHelper = BlurredBackgroundHelper;
  Object.defineProperty(GlslGenerator, 'Companion', {
    get: GlslGenerator$Companion_getInstance
  });
  GlslGenerator.GlslInjector = GlslGenerator$GlslInjector;
  package$shading.GlslGenerator = GlslGenerator;
  Shader.Source_init_puj7f4$ = Shader$Shader$Source_init;
  Shader.Source = Shader$Source;
  Shader.AttributeLocation = Shader$AttributeLocation;
  package$shading.Shader = Shader;
  Object.defineProperty(LightModel, 'PHONG_LIGHTING', {
    get: LightModel$PHONG_LIGHTING_getInstance
  });
  Object.defineProperty(LightModel, 'GOURAUD_LIGHTING', {
    get: LightModel$GOURAUD_LIGHTING_getInstance
  });
  Object.defineProperty(LightModel, 'NO_LIGHTING', {
    get: LightModel$NO_LIGHTING_getInstance
  });
  Object.defineProperty(ColorModel, 'VERTEX_COLOR', {
    get: ColorModel$VERTEX_COLOR_getInstance
  });
  Object.defineProperty(ColorModel, 'TEXTURE_COLOR', {
    get: ColorModel$TEXTURE_COLOR_getInstance
  });
  Object.defineProperty(ColorModel, 'STATIC_COLOR', {
    get: ColorModel$STATIC_COLOR_getInstance
  });
  Object.defineProperty(FogModel, 'FOG_OFF', {
    get: FogModel$FOG_OFF_getInstance
  });
  Object.defineProperty(FogModel, 'FOG_ON', {
    get: FogModel$FOG_ON_getInstance
  });
  package$shading.FogModel = FogModel;
  package$shading.ShaderProps = ShaderProps;
  package$shading.ShadingHints = ShadingHints;
  Object.defineProperty(PreferredLightModel, 'NO_LIGHTING', {
    get: PreferredLightModel$NO_LIGHTING_getInstance
  });
  Object.defineProperty(PreferredLightModel, 'GOURAUD', {
    get: PreferredLightModel$GOURAUD_getInstance
  });
  Object.defineProperty(PreferredLightModel, 'PHONG', {
    get: PreferredLightModel$PHONG_getInstance
  });
  package$shading.PreferredLightModel = PreferredLightModel;
  Object.defineProperty(PreferredShadowMethod, 'NO_SHADOW', {
    get: PreferredShadowMethod$NO_SHADOW_getInstance
  });
  Object.defineProperty(PreferredShadowMethod, 'SINGLE_SHADOW_MAP', {
    get: PreferredShadowMethod$SINGLE_SHADOW_MAP_getInstance
  });
  Object.defineProperty(PreferredShadowMethod, 'CASCADED_SHADOW_MAP', {
    get: PreferredShadowMethod$CASCADED_SHADOW_MAP_getInstance
  });
  package$shading.PreferredShadowMethod = PreferredShadowMethod;
  package$shading.Uniform = Uniform;
  package$shading.UniformTexture2D = UniformTexture2D;
  package$shading.UniformTexture2Dv = UniformTexture2Dv;
  package$shading.Uniform1i = Uniform1i;
  package$shading.Uniform1iv = Uniform1iv;
  package$shading.Uniform1f = Uniform1f;
  package$shading.Uniform1fv = Uniform1fv;
  package$shading.Uniform2f = Uniform2f;
  package$shading.Uniform3f = Uniform3f;
  package$shading.Uniform4f = Uniform4f;
  package$shading.UniformMatrix4 = UniformMatrix4;
  package$shading.VboBinder = VboBinder;
  Object.defineProperty(Animator, 'Companion', {
    get: Animator$Companion_getInstance
  });
  var package$util = package$kool.util || (package$kool.util = {});
  package$util.Animator = Animator;
  package$util.LinearAnimator = LinearAnimator;
  package$util.CosAnimator = CosAnimator;
  package$util.InterpolatedValue = InterpolatedValue;
  package$util.InterpolatedFloat = InterpolatedFloat;
  package$util.InterpolatedColor = InterpolatedColor;
  package$util.BillboardMesh = BillboardMesh;
  package$util.billboardShader_n50u2h$ = billboardShader;
  package$util.BillboardShader = BillboardShader;
  package$util.BoundingBox_init_4lfkt4$ = BoundingBox_init;
  package$util.BoundingBox = BoundingBox;
  package$util.Buffer = Buffer;
  package$util.Uint8Buffer = Uint8Buffer;
  package$util.Uint16Buffer = Uint16Buffer;
  package$util.Uint32Buffer = Uint32Buffer;
  package$util.Float32Buffer = Float32Buffer;
  Object.defineProperty(Color, 'Companion', {
    get: Color$Companion_getInstance
  });
  package$util.Color = Color;
  package$util.MutableColor_init = MutableColor_init;
  package$util.MutableColor_init_d7aj7k$ = MutableColor_init_0;
  package$util.MutableColor = MutableColor;
  package$util.color_61zpoe$ = color;
  Object.defineProperty(ColorGradient, 'Companion', {
    get: ColorGradient$Companion_getInstance
  });
  package$util.ColorGradient = ColorGradient;
  package$util.debugOverlay_3i7a9j$ = debugOverlay;
  package$util.uiFont_j4p0la$ = uiFont;
  package$util.fontShader_s2xzqe$ = fontShader;
  package$util.FontProps = FontProps;
  Object.defineProperty(Font, 'Companion', {
    get: Font$Companion_getInstance
  });
  package$util.Font = Font;
  package$util.CharMetrics = CharMetrics;
  package$util.CharMap = CharMap;
  Object.defineProperty(IndexedVertexList, 'Companion', {
    get: IndexedVertexList$Companion_getInstance
  });
  IndexedVertexList$Vertex.FloatView = IndexedVertexList$Vertex$FloatView;
  IndexedVertexList$Vertex.Vec2fView = IndexedVertexList$Vertex$Vec2fView;
  IndexedVertexList$Vertex.Vec3fView = IndexedVertexList$Vertex$Vec3fView;
  IndexedVertexList$Vertex.Vec4fView = IndexedVertexList$Vertex$Vec4fView;
  IndexedVertexList$Vertex.ColorView = IndexedVertexList$Vertex$ColorView;
  IndexedVertexList$Vertex.IntView = IndexedVertexList$Vertex$IntView;
  IndexedVertexList$Vertex.Vec2iView = IndexedVertexList$Vertex$Vec2iView;
  IndexedVertexList$Vertex.Vec3iView = IndexedVertexList$Vertex$Vec3iView;
  IndexedVertexList$Vertex.Vec4iView = IndexedVertexList$Vertex$Vec4iView;
  IndexedVertexList.Vertex = IndexedVertexList$Vertex;
  package$util.IndexedVertexList = IndexedVertexList;
  package$util.lineMesh_6a24eg$ = lineMesh;
  package$util.LineMesh = LineMesh;
  package$util.MeshBuilder = MeshBuilder;
  package$util.CircleProps = CircleProps;
  package$util.SphereProps = SphereProps;
  package$util.RectProps = RectProps;
  package$util.CubeProps = CubeProps;
  package$util.CylinderProps = CylinderProps;
  package$util.TextProps = TextProps;
  package$util.pointMesh_h6khem$ = pointMesh;
  package$util.PointMesh = PointMesh;
  package$util.Property = Property;
  Object.defineProperty(ShadowMap, 'Companion', {
    get: ShadowMap$Companion_getInstance
  });
  package$util.ShadowMap = ShadowMap;
  Object.defineProperty(CascadedShadowMap, 'Companion', {
    get: CascadedShadowMap$Companion_getInstance
  });
  package$util.CascadedShadowMap = CascadedShadowMap;
  Object.defineProperty(package$util, 'UniqueId', {
    get: UniqueId_getInstance
  });
  Object.defineProperty(AnimationData, 'Companion', {
    get: AnimationData$Companion_getInstance
  });
  Object.defineProperty(AnimationData, '$serializer', {
    get: AnimationData$$serializer_getInstance
  });
  var package$serialization = package$util.serialization || (package$util.serialization = {});
  package$serialization.AnimationData = AnimationData;
  Object.defineProperty(NodeAnimationData, 'Companion', {
    get: NodeAnimationData$Companion_getInstance
  });
  Object.defineProperty(NodeAnimationData, '$serializer', {
    get: NodeAnimationData$$serializer_getInstance
  });
  package$serialization.NodeAnimationData = NodeAnimationData;
  Object.defineProperty(Vec3KeyData, 'Companion', {
    get: Vec3KeyData$Companion_getInstance
  });
  Object.defineProperty(Vec3KeyData, '$serializer', {
    get: Vec3KeyData$$serializer_getInstance
  });
  package$serialization.Vec3KeyData = Vec3KeyData;
  Object.defineProperty(Vec4KeyData, 'Companion', {
    get: Vec4KeyData$Companion_getInstance
  });
  Object.defineProperty(Vec4KeyData, '$serializer', {
    get: Vec4KeyData$$serializer_getInstance
  });
  package$serialization.Vec4KeyData = Vec4KeyData;
  Object.defineProperty(BoneData, 'Companion', {
    get: BoneData$Companion_getInstance
  });
  Object.defineProperty(BoneData, '$serializer', {
    get: BoneData$$serializer_getInstance
  });
  package$serialization.BoneData = BoneData;
  $$importsForInline$$['kotlinx-serialization-runtime-js'] = $module$kotlinx_serialization_runtime_js;
  package$serialization.loadMesh_fqrh44$ = loadMesh;
  package$serialization.loadMesh_gs472g$ = loadMesh_0;
  Object.defineProperty(MeshData_0, 'Companion', {
    get: MeshData$Companion_getInstance
  });
  Object.defineProperty(MeshData_0, '$serializer', {
    get: MeshData$$serializer_getInstance
  });
  package$serialization.MeshData = MeshData_0;
  Object.defineProperty(package$kool, 'glCapabilities', {
    get: function () {
      return glCapabilities;
    },
    set: function (value) {
      glCapabilities = value;
    }
  });
  package$kool.createContext = createContext;
  package$kool.createContext_izggji$ = createContext_0;
  package$kool.createCharMap_ttufcy$ = createCharMap;
  package$kool.currentTimeMillis = currentTimeMillis;
  package$kool.loadAsset_jrww91$ = loadAsset;
  package$kool.loadTextureAsset_61zpoe$ = loadTextureAsset;
  package$kool.openUrl_61zpoe$ = openUrl;
  package$kool.getMemoryInfo = getMemoryInfo;
  Object.defineProperty(package$kool, 'JsImpl', {
    get: JsImpl_getInstance
  });
  package$audio.AudioGenerator = AudioGenerator;
  package$gl.glActiveTexture_za3lpa$ = glActiveTexture;
  package$gl.glAttachShader_c10c6r$ = glAttachShader;
  package$gl.glBindBuffer_4ablaf$ = glBindBuffer;
  package$gl.glBindFramebuffer_7zhe8e$ = glBindFramebuffer;
  package$gl.glBindRenderbuffer_r7ndcf$ = glBindRenderbuffer;
  package$gl.glBindTexture_hhrqcg$ = glBindTexture;
  package$gl.glBlendFunc_vux9f0$ = glBlendFunc;
  package$gl.glBufferData_i0kgf1$ = glBufferData;
  package$gl.glBufferData_wta2e6$ = glBufferData_0;
  package$gl.glBufferData_57ow2w$ = glBufferData_1;
  package$gl.glBufferData_d9f8rk$ = glBufferData_2;
  package$gl.glClear_za3lpa$ = glClear;
  package$gl.glClearColor_7b5o5w$ = glClearColor;
  package$gl.glCompileShader_4nv1hv$ = glCompileShader;
  package$gl.glCopyTexImage2D_wrdw30$ = glCopyTexImage2D;
  package$gl.glCreateBuffer = glCreateBuffer;
  package$gl.glCreateFramebuffer = glCreateFramebuffer;
  package$gl.glCreateRenderbuffer = glCreateRenderbuffer;
  package$gl.glCreateProgram = glCreateProgram;
  package$gl.glCreateShader_za3lpa$ = glCreateShader;
  package$gl.glCreateTexture = glCreateTexture;
  package$gl.glCullFace_za3lpa$ = glCullFace;
  package$gl.glDeleteBuffer_yyg71q$ = glDeleteBuffer;
  package$gl.glDeleteFramebuffer_e3u3vp$ = glDeleteFramebuffer;
  package$gl.glDeleteProgram_nzjype$ = glDeleteProgram;
  package$gl.glDeleteRenderbuffer_puextw$ = glDeleteRenderbuffer;
  package$gl.glDeleteShader_4nv1hv$ = glDeleteShader;
  package$gl.glDeleteTexture_r3jlzd$ = glDeleteTexture;
  package$gl.glDepthFunc_za3lpa$ = glDepthFunc;
  package$gl.glDepthMask_6taknv$ = glDepthMask;
  package$gl.glDisable_za3lpa$ = glDisable;
  package$gl.glDisableVertexAttribArray_za3lpa$ = glDisableVertexAttribArray;
  package$gl.glDrawBuffer_za3lpa$ = glDrawBuffer;
  package$gl.glDrawElements_tjonv8$ = glDrawElements;
  package$gl.glDrawElementsInstanced_4qozqa$ = glDrawElementsInstanced;
  package$gl.glEnable_za3lpa$ = glEnable;
  package$gl.glEnableVertexAttribArray_za3lpa$ = glEnableVertexAttribArray;
  package$gl.glFramebufferRenderbuffer_h0cs4y$ = glFramebufferRenderbuffer;
  package$gl.glFramebufferTexture2D_4b5mi7$ = glFramebufferTexture2D;
  package$gl.glGenerateMipmap_za3lpa$ = glGenerateMipmap;
  package$gl.glGetAttribLocation_xq0tfo$ = glGetAttribLocation;
  package$gl.glGetError = glGetError;
  package$gl.glGetProgrami_it7c4k$ = glGetProgrami;
  package$gl.glGetShaderi_dwrjj9$ = glGetShaderi;
  package$gl.glGetProgramInfoLog_nzjype$ = glGetProgramInfoLog;
  package$gl.glGetShaderInfoLog_4nv1hv$ = glGetShaderInfoLog;
  package$gl.glGetUniformLocation_xq0tfo$ = glGetUniformLocation;
  package$gl.glLineWidth_mx4ult$ = glLineWidth;
  package$gl.glLinkProgram_nzjype$ = glLinkProgram;
  package$gl.glPointSize_mx4ult$ = glPointSize;
  package$gl.glReadBuffer_za3lpa$ = glReadBuffer;
  package$gl.glRenderbufferStorage_tjonv8$ = glRenderbufferStorage;
  package$gl.glRenderbufferStorageMultisample_4qozqa$ = glRenderbufferStorageMultisample;
  package$gl.glShaderSource_24lj51$ = glShaderSource;
  package$gl.glTexImage2D_ffh0n8$ = glTexImage2D;
  package$gl.glTexImage2D_ikbj0q$ = glTexImage2D_0;
  package$gl.glTexParameteri_qt1dr2$ = glTexParameteri;
  package$gl.glUniform1f_s9wk91$ = glUniform1f;
  package$gl.glUniform1fv_oyoqla$ = glUniform1fv;
  package$gl.glUniform1i_cypnoy$ = glUniform1i;
  package$gl.glUniform1iv_d3jnx1$ = glUniform1iv;
  package$gl.glUniform2f_ig41i8$ = glUniform2f;
  package$gl.glUniform3f_w792mz$ = glUniform3f;
  package$gl.glUniform4f_eixcow$ = glUniform4f;
  package$gl.glUniformMatrix4fv_ugl0b7$ = glUniformMatrix4fv;
  package$gl.glUseProgram_xb2c5p$ = glUseProgram;
  package$gl.glVertexAttribDivisor_vux9f0$ = glVertexAttribDivisor;
  package$gl.glVertexAttribPointer_owihk5$ = glVertexAttribPointer;
  package$gl.glVertexAttribIPointer_4qozqa$ = glVertexAttribIPointer;
  package$gl.glViewport_tjonv8$ = glViewport;
  package$gl.isValidUniformLocation_s8jyv4$ = isValidUniformLocation;
  var package$platform = package$kool.platform || (package$kool.platform = {});
  package$platform.FontMapGenerator = FontMapGenerator;
  package$platform.ImageTextureData = ImageTextureData;
  JsContext.InitProps = JsContext$InitProps;
  Object.defineProperty(JsContext, 'Companion', {
    get: JsContext$Companion_getInstance
  });
  package$platform.JsContext = JsContext;
  package$util.GenericBuffer = GenericBuffer;
  package$util.Uint8BufferImpl = Uint8BufferImpl;
  package$util.Uint16BufferImpl = Uint16BufferImpl;
  package$util.Uint32BufferImpl = Uint32BufferImpl;
  package$util.Float32BufferImpl = Float32BufferImpl;
  package$util.createUint8Buffer_za3lpa$ = createUint8Buffer;
  package$util.createUint16Buffer_za3lpa$ = createUint16Buffer;
  package$util.createUint32Buffer_za3lpa$ = createUint32Buffer;
  package$util.createFloat32Buffer_za3lpa$ = createFloat32Buffer;
  BlankComponentUi.prototype.updateComponentAlpha = ComponentUi.prototype.updateComponentAlpha;
  BlankComponentUi.prototype.createUi_evfofk$ = ComponentUi.prototype.createUi_evfofk$;
  BlankComponentUi.prototype.updateUi_evfofk$ = ComponentUi.prototype.updateUi_evfofk$;
  BlankComponentUi.prototype.disposeUi_evfofk$ = ComponentUi.prototype.disposeUi_evfofk$;
  BlankComponentUi.prototype.onRender_evfofk$ = ComponentUi.prototype.onRender_evfofk$;
  SimpleComponentUi.prototype.onRender_evfofk$ = ComponentUi.prototype.onRender_evfofk$;
  BasicPointShader_init$ObjectLiteral.prototype.vsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.vsHeader_irqrwq$;
  BasicPointShader_init$ObjectLiteral.prototype.vsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsStart_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.vsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsAfterInput_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.vsBeforeProj_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsBeforeProj_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.vsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsEnd_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.fsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.fsHeader_irqrwq$;
  BasicPointShader_init$ObjectLiteral.prototype.fsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsStart_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.fsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsAfterInput_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.fsBeforeSampling_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsBeforeSampling_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.fsAfterSampling_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsAfterSampling_3c8d48$;
  BasicPointShader_init$ObjectLiteral.prototype.fsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsEnd_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.vsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.vsHeader_irqrwq$;
  BlurShader_init$ObjectLiteral.prototype.vsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsStart_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.vsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsAfterInput_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.vsBeforeProj_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsBeforeProj_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.vsAfterProj_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsAfterProj_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.vsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsEnd_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.fsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.fsHeader_irqrwq$;
  BlurShader_init$ObjectLiteral.prototype.fsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsStart_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.fsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsAfterInput_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.fsBeforeSampling_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsBeforeSampling_3c8d48$;
  BlurShader_init$ObjectLiteral.prototype.fsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsEnd_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.vsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.vsHeader_irqrwq$;
  BillboardShader_init$ObjectLiteral.prototype.vsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsStart_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.vsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsAfterInput_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.vsBeforeProj_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsBeforeProj_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.vsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsEnd_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.fsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.fsHeader_irqrwq$;
  BillboardShader_init$ObjectLiteral.prototype.fsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsStart_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.fsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsAfterInput_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.fsBeforeSampling_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsBeforeSampling_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.fsAfterSampling_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsAfterSampling_3c8d48$;
  BillboardShader_init$ObjectLiteral.prototype.fsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsEnd_3c8d48$;
  Uint8Buffer.prototype.plusAssign_11rb$ = Buffer.prototype.plusAssign_11rb$;
  Uint16Buffer.prototype.plusAssign_11rb$ = Buffer.prototype.plusAssign_11rb$;
  Uint32Buffer.prototype.plusAssign_11rb$ = Buffer.prototype.plusAssign_11rb$;
  Float32Buffer.prototype.plusAssign_11rb$ = Buffer.prototype.plusAssign_11rb$;
  fontShader$ObjectLiteral.prototype.vsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.vsHeader_irqrwq$;
  fontShader$ObjectLiteral.prototype.vsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsStart_3c8d48$;
  fontShader$ObjectLiteral.prototype.vsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsAfterInput_3c8d48$;
  fontShader$ObjectLiteral.prototype.vsBeforeProj_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsBeforeProj_3c8d48$;
  fontShader$ObjectLiteral.prototype.vsAfterProj_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsAfterProj_3c8d48$;
  fontShader$ObjectLiteral.prototype.vsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.vsEnd_3c8d48$;
  fontShader$ObjectLiteral.prototype.fsHeader_irqrwq$ = GlslGenerator$GlslInjector.prototype.fsHeader_irqrwq$;
  fontShader$ObjectLiteral.prototype.fsStart_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsStart_3c8d48$;
  fontShader$ObjectLiteral.prototype.fsAfterInput_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsAfterInput_3c8d48$;
  fontShader$ObjectLiteral.prototype.fsBeforeSampling_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsBeforeSampling_3c8d48$;
  fontShader$ObjectLiteral.prototype.fsEnd_3c8d48$ = GlslGenerator$GlslInjector.prototype.fsEnd_3c8d48$;
  AnimationData$$serializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  NodeAnimationData$$serializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  Vec3KeyData$$serializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  Vec4KeyData$$serializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  BoneData$$serializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  MeshData$$serializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  GenericBuffer.prototype.plusAssign_11rb$ = Buffer.prototype.plusAssign_11rb$;
  Uint8BufferImpl.prototype.put_fqrh44$ = Uint8Buffer.prototype.put_fqrh44$;
  Uint8BufferImpl.prototype.plusAssign_11rb$ = Uint8Buffer.prototype.plusAssign_11rb$;
  Uint16BufferImpl.prototype.put_gmedm2$ = Uint16Buffer.prototype.put_gmedm2$;
  Uint16BufferImpl.prototype.plusAssign_11rb$ = Uint16Buffer.prototype.plusAssign_11rb$;
  Uint32BufferImpl.prototype.put_q5rwfd$ = Uint32Buffer.prototype.put_q5rwfd$;
  Uint32BufferImpl.prototype.plusAssign_11rb$ = Uint32Buffer.prototype.plusAssign_11rb$;
  Float32BufferImpl.prototype.put_q3cr5i$ = Float32Buffer.prototype.put_q3cr5i$;
  Float32BufferImpl.prototype.plusAssign_11rb$ = Float32Buffer.prototype.plusAssign_11rb$;
  GL_ACTIVE_TEXTURE = 34016;
  GL_DEPTH_BUFFER_BIT = 256;
  GL_STENCIL_BUFFER_BIT = 1024;
  GL_COLOR_BUFFER_BIT = 16384;
  GL_FALSE = 0;
  GL_TRUE = 1;
  GL_POINTS = 0;
  GL_LINES = 1;
  GL_LINE_LOOP = 2;
  GL_LINE_STRIP = 3;
  GL_TRIANGLES = 4;
  GL_TRIANGLE_STRIP = 5;
  GL_TRIANGLE_FAN = 6;
  GL_ZERO = 0;
  GL_ONE = 1;
  GL_SRC_COLOR = 768;
  GL_ONE_MINUS_SRC_COLOR = 769;
  GL_SRC_ALPHA = 770;
  GL_ONE_MINUS_SRC_ALPHA = 771;
  GL_DST_ALPHA = 772;
  GL_ONE_MINUS_DST_ALPHA = 773;
  GL_DST_COLOR = 774;
  GL_ONE_MINUS_DST_COLOR = 775;
  GL_SRC_ALPHA_SATURATE = 776;
  GL_FUNC_ADD = 32774;
  GL_BLEND_EQUATION = 32777;
  GL_BLEND_EQUATION_RGB = 32777;
  GL_BLEND_EQUATION_ALPHA = 34877;
  GL_FUNC_SUBTRACT = 32778;
  GL_FUNC_REVERSE_SUBTRACT = 32779;
  GL_BLEND_DST_RGB = 32968;
  GL_BLEND_SRC_RGB = 32969;
  GL_BLEND_DST_ALPHA = 32970;
  GL_BLEND_SRC_ALPHA = 32971;
  GL_CONSTANT_COLOR = 32769;
  GL_ONE_MINUS_CONSTANT_COLOR = 32770;
  GL_CONSTANT_ALPHA = 32771;
  GL_ONE_MINUS_CONSTANT_ALPHA = 32772;
  GL_BLEND_COLOR = 32773;
  GL_ARRAY_BUFFER = 34962;
  GL_ELEMENT_ARRAY_BUFFER = 34963;
  GL_ARRAY_BUFFER_BINDING = 34964;
  GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
  GL_STREAM_DRAW = 35040;
  GL_STATIC_DRAW = 35044;
  GL_DYNAMIC_DRAW = 35048;
  GL_BUFFER_SIZE = 34660;
  GL_BUFFER_USAGE = 34661;
  GL_CURRENT_VERTEX_ATTRIB = 34342;
  GL_FRONT = 1028;
  GL_BACK = 1029;
  GL_FRONT_AND_BACK = 1032;
  GL_TEXTURE_2D = 3553;
  GL_CULL_FACE = 2884;
  GL_BLEND = 3042;
  GL_DITHER = 3024;
  GL_STENCIL_TEST = 2960;
  GL_DEPTH_TEST = 2929;
  GL_SCISSOR_TEST = 3089;
  GL_POLYGON_OFFSET_FILL = 32823;
  GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
  GL_SAMPLE_COVERAGE = 32928;
  GL_NO_ERROR = 0;
  GL_INVALID_ENUM = 1280;
  GL_INVALID_VALUE = 1281;
  GL_INVALID_OPERATION = 1282;
  GL_OUT_OF_MEMORY = 1285;
  GL_CW = 2304;
  GL_CCW = 2305;
  GL_LINE_WIDTH = 2849;
  GL_ALIASED_POINT_SIZE_RANGE = 33901;
  GL_ALIASED_LINE_WIDTH_RANGE = 33902;
  GL_CULL_FACE_MODE = 2885;
  GL_FRONT_FACE = 2886;
  GL_DEPTH_RANGE = 2928;
  GL_DEPTH_WRITEMASK = 2930;
  GL_DEPTH_CLEAR_VALUE = 2931;
  GL_DEPTH_FUNC = 2932;
  GL_STENCIL_CLEAR_VALUE = 2961;
  GL_STENCIL_FUNC = 2962;
  GL_STENCIL_FAIL = 2964;
  GL_STENCIL_PASS_DEPTH_FAIL = 2965;
  GL_STENCIL_PASS_DEPTH_PASS = 2966;
  GL_STENCIL_REF = 2967;
  GL_STENCIL_VALUE_MASK = 2963;
  GL_STENCIL_WRITEMASK = 2968;
  GL_STENCIL_BACK_FUNC = 34816;
  GL_STENCIL_BACK_FAIL = 34817;
  GL_STENCIL_BACK_PASS_DEPTH_FAIL = 34818;
  GL_STENCIL_BACK_PASS_DEPTH_PASS = 34819;
  GL_STENCIL_BACK_REF = 36003;
  GL_STENCIL_BACK_VALUE_MASK = 36004;
  GL_STENCIL_BACK_WRITEMASK = 36005;
  GL_VIEWPORT = 2978;
  GL_SCISSOR_BOX = 3088;
  GL_COLOR_CLEAR_VALUE = 3106;
  GL_COLOR_WRITEMASK = 3107;
  GL_UNPACK_ALIGNMENT = 3317;
  GL_PACK_ALIGNMENT = 3333;
  GL_MAX_TEXTURE_SIZE = 3379;
  GL_MAX_VIEWPORT_DIMS = 3386;
  GL_SUBPIXEL_BITS = 3408;
  GL_RED_BITS = 3410;
  GL_GREEN_BITS = 3411;
  GL_BLUE_BITS = 3412;
  GL_ALPHA_BITS = 3413;
  GL_DEPTH_BITS = 3414;
  GL_STENCIL_BITS = 3415;
  GL_POLYGON_OFFSET_UNITS = 10752;
  GL_POLYGON_OFFSET_FACTOR = 32824;
  GL_TEXTURE_BINDING_2D = 32873;
  GL_SAMPLE_BUFFERS = 32936;
  GL_SAMPLES = 32937;
  GL_SAMPLE_COVERAGE_VALUE = 32938;
  GL_SAMPLE_COVERAGE_INVERT = 32939;
  GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
  GL_COMPRESSED_TEXTURE_FORMATS = 34467;
  GL_DONT_CARE = 4352;
  GL_FASTEST = 4353;
  GL_NICEST = 4354;
  GL_GENERATE_MIPMAP_HINT = 33170;
  GL_BYTE = 5120;
  GL_UNSIGNED_BYTE = 5121;
  GL_SHORT = 5122;
  GL_UNSIGNED_SHORT = 5123;
  GL_INT = 5124;
  GL_UNSIGNED_INT = 5125;
  GL_FLOAT = 5126;
  GL_FIXED = 5132;
  GL_DEPTH_COMPONENT = 6402;
  GL_DEPTH_COMPONENT24 = 33190;
  GL_DEPTH_COMPONENT32F = 36012;
  GL_ALPHA = 6406;
  GL_RGB = 6407;
  GL_RGBA = 6408;
  GL_LUMINANCE = 6409;
  GL_LUMINANCE_ALPHA = 6410;
  GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
  GL_UNSIGNED_SHORT_5_5_5_1 = 32820;
  GL_UNSIGNED_SHORT_5_6_5 = 33635;
  GL_FRAGMENT_SHADER = 35632;
  GL_VERTEX_SHADER = 35633;
  GL_MAX_VERTEX_ATTRIBS = 34921;
  GL_MAX_VERTEX_UNIFORM_VECTORS = 36347;
  GL_MAX_VARYING_VECTORS = 36348;
  GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
  GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660;
  GL_MAX_TEXTURE_IMAGE_UNITS = 34930;
  GL_MAX_FRAGMENT_UNIFORM_VECTORS = 36349;
  GL_SHADER_TYPE = 35663;
  GL_DELETE_STATUS = 35712;
  GL_LINK_STATUS = 35714;
  GL_VALIDATE_STATUS = 35715;
  GL_ATTACHED_SHADERS = 35717;
  GL_ACTIVE_UNIFORMS = 35718;
  GL_ACTIVE_UNIFORM_MAX_LENGTH = 35719;
  GL_ACTIVE_ATTRIBUTES = 35721;
  GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 35722;
  GL_SHADING_LANGUAGE_VERSION = 35724;
  GL_CURRENT_PROGRAM = 35725;
  GL_NEVER = 512;
  GL_LESS = 513;
  GL_EQUAL = 514;
  GL_LEQUAL = 515;
  GL_GREATER = 516;
  GL_NOTEQUAL = 517;
  GL_GEQUAL = 518;
  GL_ALWAYS = 519;
  GL_KEEP = 7680;
  GL_REPLACE = 7681;
  GL_INCR = 7682;
  GL_DECR = 7683;
  GL_INVERT = 5386;
  GL_INCR_WRAP = 34055;
  GL_DECR_WRAP = 34056;
  GL_VENDOR = 7936;
  GL_RENDERER = 7937;
  GL_VERSION = 7938;
  GL_EXTENSIONS = 7939;
  GL_NEAREST = 9728;
  GL_LINEAR = 9729;
  GL_NEAREST_MIPMAP_NEAREST = 9984;
  GL_LINEAR_MIPMAP_NEAREST = 9985;
  GL_NEAREST_MIPMAP_LINEAR = 9986;
  GL_LINEAR_MIPMAP_LINEAR = 9987;
  GL_TEXTURE_MAG_FILTER = 10240;
  GL_TEXTURE_MIN_FILTER = 10241;
  GL_TEXTURE_WRAP_S = 10242;
  GL_TEXTURE_WRAP_T = 10243;
  GL_TEXTURE = 5890;
  GL_TEXTURE_CUBE_MAP = 34067;
  GL_TEXTURE_BINDING_CUBE_MAP = 34068;
  GL_TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
  GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
  GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
  GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
  GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
  GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
  GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
  GL_TEXTURE0 = 33984;
  GL_TEXTURE1 = 33985;
  GL_TEXTURE2 = 33986;
  GL_TEXTURE3 = 33987;
  GL_TEXTURE4 = 33988;
  GL_TEXTURE5 = 33989;
  GL_TEXTURE6 = 33990;
  GL_TEXTURE7 = 33991;
  GL_TEXTURE8 = 33992;
  GL_TEXTURE9 = 33993;
  GL_TEXTURE10 = 33994;
  GL_TEXTURE11 = 33995;
  GL_TEXTURE12 = 33996;
  GL_TEXTURE13 = 33997;
  GL_TEXTURE14 = 33998;
  GL_TEXTURE15 = 33999;
  GL_TEXTURE16 = 34000;
  GL_TEXTURE17 = 34001;
  GL_TEXTURE18 = 34002;
  GL_TEXTURE19 = 34003;
  GL_TEXTURE20 = 34004;
  GL_TEXTURE21 = 34005;
  GL_TEXTURE22 = 34006;
  GL_TEXTURE23 = 34007;
  GL_TEXTURE24 = 34008;
  GL_TEXTURE25 = 34009;
  GL_TEXTURE26 = 34010;
  GL_TEXTURE27 = 34011;
  GL_TEXTURE28 = 34012;
  GL_TEXTURE29 = 34013;
  GL_TEXTURE30 = 34014;
  GL_TEXTURE31 = 34015;
  GL_REPEAT = 10497;
  GL_CLAMP_TO_EDGE = 33071;
  GL_MIRRORED_REPEAT = 33648;
  GL_FLOAT_VEC2 = 35664;
  GL_FLOAT_VEC3 = 35665;
  GL_FLOAT_VEC4 = 35666;
  GL_INT_VEC2 = 35667;
  GL_INT_VEC3 = 35668;
  GL_INT_VEC4 = 35669;
  GL_BOOL = 35670;
  GL_BOOL_VEC2 = 35671;
  GL_BOOL_VEC3 = 35672;
  GL_BOOL_VEC4 = 35673;
  GL_FLOAT_MAT2 = 35674;
  GL_FLOAT_MAT3 = 35675;
  GL_FLOAT_MAT4 = 35676;
  GL_SAMPLER_2D = 35678;
  GL_SAMPLER_CUBE = 35680;
  GL_VERTEX_ATTRIB_ARRAY_ENABLED = 34338;
  GL_VERTEX_ATTRIB_ARRAY_SIZE = 34339;
  GL_VERTEX_ATTRIB_ARRAY_STRIDE = 34340;
  GL_VERTEX_ATTRIB_ARRAY_TYPE = 34341;
  GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922;
  GL_VERTEX_ATTRIB_ARRAY_POINTER = 34373;
  GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
  GL_IMPLEMENTATION_COLOR_READ_TYPE = 35738;
  GL_IMPLEMENTATION_COLOR_READ_FORMAT = 35739;
  GL_COMPILE_STATUS = 35713;
  GL_INFO_LOG_LENGTH = 35716;
  GL_SHADER_SOURCE_LENGTH = 35720;
  GL_SHADER_COMPILER = 36346;
  GL_SHADER_BINARY_FORMATS = 36344;
  GL_NUM_SHADER_BINARY_FORMATS = 36345;
  GL_LOW_FLOAT = 36336;
  GL_MEDIUM_FLOAT = 36337;
  GL_HIGH_FLOAT = 36338;
  GL_LOW_INT = 36339;
  GL_MEDIUM_INT = 36340;
  GL_HIGH_INT = 36341;
  GL_FRAMEBUFFER = 36160;
  GL_RENDERBUFFER = 36161;
  GL_RGBA4 = 32854;
  GL_RGB5_A1 = 32855;
  GL_RGB565 = 36194;
  GL_DEPTH_COMPONENT16 = 33189;
  GL_STENCIL_INDEX8 = 36168;
  GL_RENDERBUFFER_WIDTH = 36162;
  GL_RENDERBUFFER_HEIGHT = 36163;
  GL_RENDERBUFFER_INTERNAL_FORMAT = 36164;
  GL_RENDERBUFFER_RED_SIZE = 36176;
  GL_RENDERBUFFER_GREEN_SIZE = 36177;
  GL_RENDERBUFFER_BLUE_SIZE = 36178;
  GL_RENDERBUFFER_ALPHA_SIZE = 36179;
  GL_RENDERBUFFER_DEPTH_SIZE = 36180;
  GL_RENDERBUFFER_STENCIL_SIZE = 36181;
  GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048;
  GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049;
  GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050;
  GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051;
  GL_COLOR_ATTACHMENT0 = 36064;
  GL_DEPTH_ATTACHMENT = 36096;
  GL_STENCIL_ATTACHMENT = 36128;
  GL_NONE = 0;
  GL_FRAMEBUFFER_COMPLETE = 36053;
  GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
  GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
  GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 36057;
  GL_FRAMEBUFFER_UNSUPPORTED = 36061;
  GL_FRAMEBUFFER_BINDING = 36006;
  GL_RENDERBUFFER_BINDING = 36007;
  GL_MAX_RENDERBUFFER_SIZE = 34024;
  GL_INVALID_FRAMEBUFFER_OPERATION = 1286;
  DEG_2_RAD = math.PI / 180.0;
  RAD_2_DEG = 180.0 / math.PI;
  defaultRandomInstance = new Random(currentTimeMillis().toInt());
  slerpTmpA = MutableVec4f_init();
  slerpTmpB = MutableVec4f_init();
  slerpTmpC = MutableVec4f_init();
  glCapabilities = GlCapabilities$Companion_getInstance().UNKNOWN_CAPABILITIES;
  Kotlin.defineModule('kool', _);
  return _;
});
