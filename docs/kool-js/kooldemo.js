define(['exports', 'kotlin', 'kool', 'kotlinx-serialization-kotlinx-serialization-protobuf', 'kotlinx-serialization-kotlinx-serialization-runtime'], function (_, Kotlin, $module$kool, $module$kotlinx_serialization_kotlinx_serialization_protobuf, $module$kotlinx_serialization_kotlinx_serialization_runtime) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var createDefaultContext = $module$kool.de.fabmax.kool.createDefaultContext;
  var Unit = Kotlin.kotlin.Unit;
  var UiTheme = $module$kool.de.fabmax.kool.scene.ui.UiTheme;
  var BlankComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlankComponentUi;
  var BlurredComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlurredComponentUi;
  var getCallableRef = Kotlin.getCallableRef;
  var theme = $module$kool.de.fabmax.kool.scene.ui.theme_vvurn$;
  var zero = $module$kool.de.fabmax.kool.scene.ui.zero;
  var dps = $module$kool.de.fabmax.kool.scene.ui.dps_8ca0d4$;
  var full = $module$kool.de.fabmax.kool.scene.ui.full;
  var pcs = $module$kool.de.fabmax.kool.scene.ui.pcs_8ca0d4$;
  var Alignment = $module$kool.de.fabmax.kool.scene.ui.Alignment;
  var Gravity = $module$kool.de.fabmax.kool.scene.ui.Gravity;
  var uiScene = $module$kool.de.fabmax.kool.scene.ui.uiScene_m9o5w1$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Position = $module$kool.de.fabmax.kool.util.Position;
  var DebugOverlay = $module$kool.de.fabmax.kool.util.DebugOverlay;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mutableMapOf = Kotlin.kotlin.collections.mutableMapOf_qfcya0$;
  var List = Kotlin.kotlin.collections.List;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var Map = Kotlin.kotlin.collections.Map;
  var throwCCE = Kotlin.throwCCE;
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var defaultCamTransform = $module$kool.de.fabmax.kool.scene.defaultCamTransform_v4keia$;
  var Albedo = $module$kool.de.fabmax.kool.pipeline.shading.Albedo;
  var pbrShader = $module$kool.de.fabmax.kool.pipeline.shading.pbrShader_zfwrfj$;
  var colorMesh = $module$kool.de.fabmax.kool.scene.colorMesh_gp9ews$;
  var Vec3f = $module$kool.de.fabmax.kool.math.Vec3f;
  var Color = $module$kool.de.fabmax.kool.util.Color;
  var Scene_init = $module$kool.de.fabmax.kool.scene.Scene;
  var MeshInstanceList = $module$kool.de.fabmax.kool.util.MeshInstanceList;
  var listOf = Kotlin.kotlin.collections.listOf_mh5how$;
  var Mat4f = $module$kool.de.fabmax.kool.math.Mat4f;
  var ShaderModel = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderModel;
  var ModeledShader = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader;
  var ShaderNode = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderNode;
  var ModelVar4f = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar4f;
  var ShaderNodeIoVar = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderNodeIoVar;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var ShaderModel$ShaderModel$VertexStageBuilder_init = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderModel.VertexStageBuilder;
  var ShaderModel$ShaderModel$FragmentStageBuilder_init = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderModel.FragmentStageBuilder;
  var OrbitInputTransform$ZoomMethod = $module$kool.de.fabmax.kool.scene.OrbitInputTransform.ZoomMethod;
  var orbitInputTransform = $module$kool.de.fabmax.kool.scene.orbitInputTransform_iiyuln$;
  var PbrShader = $module$kool.de.fabmax.kool.pipeline.shading.PbrShader;
  var PbrShader$PbrConfig = $module$kool.de.fabmax.kool.pipeline.shading.PbrShader.PbrConfig;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var Texture = $module$kool.de.fabmax.kool.pipeline.Texture;
  var ensureNotNull = Kotlin.ensureNotNull;
  var textureMesh = $module$kool.de.fabmax.kool.scene.textureMesh_pyaqjj$;
  var Font = $module$kool.de.fabmax.kool.util.Font;
  var FontProps = $module$kool.de.fabmax.kool.util.FontProps;
  var uiFont = $module$kool.de.fabmax.kool.util.uiFont_a4r08d$;
  var SimpleComponentUi = $module$kool.de.fabmax.kool.scene.ui.SimpleComponentUi;
  var math = Kotlin.kotlin.math;
  var MutableColor_init = $module$kool.de.fabmax.kool.util.MutableColor_init;
  var TransformGroup = $module$kool.de.fabmax.kool.scene.TransformGroup;
  var Light = $module$kool.de.fabmax.kool.scene.Light;
  var LineMesh = $module$kool.de.fabmax.kool.scene.LineMesh;
  var ModeledShader$StaticColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.StaticColor;
  var MutableVec3f_init = $module$kool.de.fabmax.kool.math.MutableVec3f_init;
  var randomF = $module$kool.de.fabmax.kool.math.randomF_dleff0$;
  var listOf_0 = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var ShadowMapPass = $module$kool.de.fabmax.kool.util.ShadowMapPass;
  var Math_0 = Math;
  var math_0 = $module$kool.de.fabmax.kool.math;
  var transformGroup = $module$kool.de.fabmax.kool.scene.transformGroup_zaezuq$;
  var Attribute = $module$kool.de.fabmax.kool.pipeline.Attribute;
  var group = $module$kool.de.fabmax.kool.scene.group_2ylazs$;
  var addAll = Kotlin.kotlin.collections.addAll_ipc267$;
  var IndexedVertexList_init = $module$kool.de.fabmax.kool.util.IndexedVertexList;
  var Mesh_init = $module$kool.de.fabmax.kool.scene.Mesh;
  var IrradianceMapPass = $module$kool.de.fabmax.kool.util.pbrMapGen.IrradianceMapPass;
  var ReflectionMapPass = $module$kool.de.fabmax.kool.util.pbrMapGen.ReflectionMapPass;
  var BrdfLutPass = $module$kool.de.fabmax.kool.util.pbrMapGen.BrdfLutPass;
  var Skybox = $module$kool.de.fabmax.kool.scene.Skybox;
  var FilterMethod = $module$kool.de.fabmax.kool.pipeline.FilterMethod;
  var TextureProps = $module$kool.de.fabmax.kool.pipeline.TextureProps;
  var Array_0 = Array;
  var SingleColorTexture = $module$kool.de.fabmax.kool.pipeline.SingleColorTexture;
  var mutableListOf = Kotlin.kotlin.collections.mutableListOf_i5x0yv$;
  var Globe = $module$kool.de.fabmax.kool.modules.globe.Globe;
  var DoublePrecisionRoot = $module$kool.de.fabmax.kool.scene.doubleprec.DoublePrecisionRoot;
  var GlobeCamHandler = $module$kool.de.fabmax.kool.modules.globe.GlobeCamHandler;
  var ProtoBuf = $module$kotlinx_serialization_kotlinx_serialization_protobuf.kotlinx.serialization.protobuf.ProtoBuf;
  var ElevationMapMetaHierarchy = $module$kool.de.fabmax.kool.modules.globe.elevation.ElevationMapMetaHierarchy;
  var getKClass = Kotlin.getKClass;
  var ElevationMapHierarchy = $module$kool.de.fabmax.kool.modules.globe.elevation.ElevationMapHierarchy;
  var Margin = $module$kool.de.fabmax.kool.scene.ui.Margin;
  var Pair = Kotlin.kotlin.Pair;
  var toString = $module$kool.de.fabmax.kool.toString_j6vyb1$;
  var equals = Kotlin.equals;
  var getContextualOrDefault = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
  var util = $module$kool.de.fabmax.kool.util;
  var Log$Level = $module$kool.de.fabmax.kool.util.Log.Level;
  var ParticleSystem = $module$kool.de.fabmax.kool.util.ParticleSystem;
  var CubicPointDistribution = $module$kool.de.fabmax.kool.math.CubicPointDistribution;
  var Vec2f = $module$kool.de.fabmax.kool.math.Vec2f;
  var ColorGradient = $module$kool.de.fabmax.kool.util.ColorGradient;
  var randomF_0 = $module$kool.de.fabmax.kool.math.randomF;
  var ParticleSystem$Type = $module$kool.de.fabmax.kool.util.ParticleSystem.Type;
  var BillboardMesh$DrawOrder = $module$kool.de.fabmax.kool.scene.BillboardMesh.DrawOrder;
  var numberToInt = Kotlin.numberToInt;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var randomI = $module$kool.de.fabmax.kool.math.randomI_n8acyv$;
  var round = Kotlin.kotlin.math.round_14dthe$;
  var InRadiusTraverser = $module$kool.de.fabmax.kool.util.InRadiusTraverser;
  var BillboardMesh = $module$kool.de.fabmax.kool.scene.BillboardMesh;
  var PerfTimer = $module$kool.de.fabmax.kool.util.PerfTimer;
  var BoundingBox_init = $module$kool.de.fabmax.kool.util.BoundingBox_init_4lfkt4$;
  var pointMesh = $module$kool.de.fabmax.kool.scene.pointMesh_zhxjay$;
  var pointKdTree = $module$kool.de.fabmax.kool.util.pointKdTree_ffk80x$;
  var now = $module$kool.de.fabmax.kool.now;
  var Skybox_init = $module$kool.de.fabmax.kool.scene.Skybox_init_r3y0ew$;
  var yPlanePan = $module$kool.de.fabmax.kool.scene.yPlanePan;
  var Vec3f_init = $module$kool.de.fabmax.kool.math.Vec3f_init_mx4ult$;
  var InterpolatedFloat = $module$kool.de.fabmax.kool.util.InterpolatedFloat;
  var CosAnimator = $module$kool.de.fabmax.kool.util.CosAnimator;
  var Animator = $module$kool.de.fabmax.kool.util.Animator;
  var LinearAnimator = $module$kool.de.fabmax.kool.util.LinearAnimator;
  var Font_init = $module$kool.de.fabmax.kool.util.Font_init_8tzsp9$;
  var textMesh = $module$kool.de.fabmax.kool.scene.textMesh_8mgi8m$;
  var TextProps_init = $module$kool.de.fabmax.kool.util.TextProps;
  var reversed = Kotlin.kotlin.ranges.reversed_zf1xzc$;
  var UiContainer = $module$kool.de.fabmax.kool.scene.ui.UiContainer;
  var SampleNode = $module$kool.de.fabmax.kool.modules.audio.SampleNode;
  var Wave = $module$kool.de.fabmax.kool.modules.audio.Wave;
  var Oscillator = $module$kool.de.fabmax.kool.modules.audio.Oscillator;
  var MoodFilter = $module$kool.de.fabmax.kool.modules.audio.MoodFilter;
  var Button = $module$kool.de.fabmax.kool.scene.ui.Button;
  var MutableColor_init_0 = $module$kool.de.fabmax.kool.util.MutableColor_init_d7aj7k$;
  var InterpolatedColor = $module$kool.de.fabmax.kool.util.InterpolatedColor;
  var Group = $module$kool.de.fabmax.kool.scene.Group;
  var lineMesh = $module$kool.de.fabmax.kool.scene.lineMesh_sokaj4$;
  var Shaker = $module$kool.de.fabmax.kool.modules.audio.Shaker;
  var Kick = $module$kool.de.fabmax.kool.modules.audio.Kick;
  var Pad = $module$kool.de.fabmax.kool.modules.audio.Pad;
  var AudioGenerator = $module$kool.de.fabmax.kool.modules.audio.AudioGenerator;
  var throwUPAE = Kotlin.throwUPAE;
  var ListEdgeHandler = $module$kool.de.fabmax.kool.modules.mesh.ListEdgeHandler;
  var HalfEdgeMesh = $module$kool.de.fabmax.kool.modules.mesh.HalfEdgeMesh;
  var terminateOnFaceCountRel = $module$kool.de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel_14dthe$;
  var simplify = $module$kool.de.fabmax.kool.modules.mesh.simplification.simplify_hem9$;
  var toString_0 = Kotlin.toString;
  var IndexedVertexList_init_0 = $module$kool.de.fabmax.kool.util.IndexedVertexList_init_5jr6ei$;
  var MeshBuilder = $module$kool.de.fabmax.kool.util.MeshBuilder;
  var toString_1 = $module$kool.de.fabmax.kool.toString_lcymw2$;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  var MutableVec3f = $module$kool.de.fabmax.kool.math.MutableVec3f;
  var PushConstantNode1f = $module$kool.de.fabmax.kool.pipeline.shadermodel.PushConstantNode1f;
  var CullMethod = $module$kool.de.fabmax.kool.pipeline.CullMethod;
  var ModelVar3fConst = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar3fConst;
  var ModelVar1fConst = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar1fConst;
  var ModelVar3f = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar3f;
  var ShaderStage = $module$kool.de.fabmax.kool.pipeline.ShaderStage;
  var wrapFunction = Kotlin.wrapFunction;
  var MutableVec3f_init_0 = $module$kool.de.fabmax.kool.math.MutableVec3f_init_czzhiu$;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var Vec3f_init_0 = $module$kool.de.fabmax.kool.math.Vec3f_init_czzhiu$;
  var PointDistribution = $module$kool.de.fabmax.kool.math.PointDistribution;
  var MutableVec2f_init = $module$kool.de.fabmax.kool.math.MutableVec2f_init;
  var BSplineVec2f = $module$kool.de.fabmax.kool.math.BSplineVec2f;
  var MutableVec2f = $module$kool.de.fabmax.kool.math.MutableVec2f;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var ModeledShader$VertexColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.VertexColor;
  var dp = $module$kool.de.fabmax.kool.scene.ui.dp_wl4j30$;
  var uns = $module$kool.de.fabmax.kool.scene.ui.uns_8ca0d4$;
  var embeddedUi = $module$kool.de.fabmax.kool.scene.ui.embeddedUi_y4avn7$;
  var ModelVar2f = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar2f;
  var ModelVar4fConst = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar4fConst;
  var ModeledShader$TextureColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.TextureColor;
  var ModeledShader$CubeMapColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.CubeMapColor;
  var CubeMapTexture = $module$kool.de.fabmax.kool.pipeline.CubeMapTexture;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  MatColorNode.prototype = Object.create(ShaderNode.prototype);
  MatColorNode.prototype.constructor = MatColorNode;
  MultiLightDemo$LightMesh.prototype = Object.create(TransformGroup.prototype);
  MultiLightDemo$LightMesh.prototype.constructor = MultiLightDemo$LightMesh;
  ColorGridContent.prototype = Object.create(PbrDemo$PbrContent.prototype);
  ColorGridContent.prototype.constructor = ColorGridContent;
  PbrMaterialContent.prototype = Object.create(PbrDemo$PbrContent.prototype);
  PbrMaterialContent.prototype.constructor = PbrMaterialContent;
  RoughnesMetalGridContent.prototype = Object.create(PbrDemo$PbrContent.prototype);
  RoughnesMetalGridContent.prototype.constructor = RoughnesMetalGridContent;
  MeshPoint.prototype = Object.create(Vec3f.prototype);
  MeshPoint.prototype.constructor = MeshPoint;
  VerticalLayout.prototype = Object.create(UiContainer.prototype);
  VerticalLayout.prototype.constructor = VerticalLayout;
  Melody.prototype = Object.create(SampleNode.prototype);
  Melody.prototype.constructor = Melody;
  SequenceButton.prototype = Object.create(Button.prototype);
  SequenceButton.prototype.constructor = SequenceButton;
  SequenceButtonUi.prototype = Object.create(SimpleComponentUi.prototype);
  SequenceButtonUi.prototype.constructor = SequenceButtonUi;
  SynthieScene$Heightmap.prototype = Object.create(TransformGroup.prototype);
  SynthieScene$Heightmap.prototype.constructor = SynthieScene$Heightmap;
  SynthieScene$Waveform.prototype = Object.create(Group.prototype);
  SynthieScene$Waveform.prototype.constructor = SynthieScene$Waveform;
  SynthieScene.prototype = Object.create(Scene_init.prototype);
  SynthieScene.prototype.constructor = SynthieScene;
  WindNode.prototype = Object.create(ShaderNode.prototype);
  WindNode.prototype.constructor = WindNode;
  TreeGenerator$AttractionPoint.prototype = Object.create(MutableVec3f.prototype);
  TreeGenerator$AttractionPoint.prototype.constructor = TreeGenerator$AttractionPoint;
  TreeGenerator$TreeNode.prototype = Object.create(MutableVec3f.prototype);
  TreeGenerator$TreeNode.prototype.constructor = TreeGenerator$TreeNode;
  TreeTopPointDistribution.prototype = Object.create(PointDistribution.prototype);
  TreeTopPointDistribution.prototype.constructor = TreeTopPointDistribution;
  EquiRectCoords.prototype = Object.create(ShaderNode.prototype);
  EquiRectCoords.prototype.constructor = EquiRectCoords;
  DecodeRgbeNode.prototype = Object.create(ShaderNode.prototype);
  DecodeRgbeNode.prototype.constructor = DecodeRgbeNode;
  DecodeAndToneMapRgbeNode.prototype = Object.create(ShaderNode.prototype);
  DecodeAndToneMapRgbeNode.prototype.constructor = DecodeAndToneMapRgbeNode;
  function demo(startScene) {
    if (startScene === void 0)
      startScene = null;
    var ctx = createDefaultContext();
    var $this = Demo$Companion_getInstance();
    var key = 'assetsBaseDir';
    var tmp$, tmp$_0;
    var assetsBaseDir = (tmp$_0 = typeof (tmp$ = $this.demoProps.get_11rb$(key)) === 'string' ? tmp$ : null) != null ? tmp$_0 : '';
    if (assetsBaseDir.length > 0) {
      ctx.assetMgr.assetsBaseDir = assetsBaseDir;
    }new Demo(ctx, startScene);
  }
  function Demo(ctx, startScene) {
    Demo$Companion_getInstance();
    if (startScene === void 0)
      startScene = null;
    this.dbgOverlay_0 = new DebugOverlay(ctx, Position.LOWER_RIGHT);
    this.newScenes_0 = ArrayList_init();
    this.currentScenes_0 = ArrayList_init();
    this.defaultScene_0 = new Demo$DemoEntry('PBR/IBL Demo', void 0, Demo$defaultScene$lambda);
    this.demos_0 = mutableMapOf([to('pbrDemo', new Demo$DemoEntry('PBR/IBL Demo', void 0, Demo$demos$lambda)), to('multiLightDemo', new Demo$DemoEntry('Multi Light Demo', void 0, Demo$demos$lambda_0)), to('simplificationDemo', new Demo$DemoEntry('Simplification Demo', void 0, Demo$demos$lambda_1)), to('treeDemo', new Demo$DemoEntry('Tree Demo', void 0, Demo$demos$lambda_2)), to('helloWorldDemo', new Demo$DemoEntry('Hello World', true, Demo$demos$lambda_3))]);
    var tmp$;
    var $receiver = ctx.scenes;
    var element = this.dbgOverlay_0.ui;
    $receiver.add_11rb$(element);
    var $receiver_0 = ctx.scenes;
    var element_0 = this.demoOverlay_0(ctx);
    $receiver_0.add_11rb$(element_0);
    var $receiver_1 = ctx.onRender;
    var element_1 = getCallableRef('onRender', function ($receiver, ctx) {
      return $receiver.onRender_0(ctx), Unit;
    }.bind(null, this));
    $receiver_1.add_11rb$(element_1);
    var $receiver_2 = this.demos_0;
    var key = startScene;
    var tmp$_0;
    ((tmp$ = (Kotlin.isType(tmp$_0 = $receiver_2, Map) ? tmp$_0 : throwCCE()).get_11rb$(key)) != null ? tmp$ : this.defaultScene_0).loadScene(this.newScenes_0, ctx);
    ctx.run();
  }
  Demo.prototype.onRender_0 = function (ctx) {
    if (!this.newScenes_0.isEmpty()) {
      var tmp$;
      tmp$ = this.currentScenes_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        ctx.scenes.remove_11rb$(element);
        element.dispose_aemszp$(ctx);
      }
      this.currentScenes_0.clear();
      var tmp$_0, tmp$_0_0;
      var index = 0;
      tmp$_0 = this.newScenes_0.iterator();
      while (tmp$_0.hasNext()) {
        var item = tmp$_0.next();
        ctx.scenes.add_wxm5ur$(checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0)), item);
        this.currentScenes_0.add_11rb$(item);
      }
      this.newScenes_0.clear();
    }};
  function Demo$demoOverlay$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function Demo$demoOverlay$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(Demo$demoOverlay$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(getCallableRef('BlurredComponentUi', function (component) {
      return new BlurredComponentUi(component);
    }));
    return Unit;
  }
  function Demo$demoOverlay$lambda$lambda$lambda$lambda$lambda$lambda(closure$demo, this$Demo, closure$ctx, this$) {
    return function ($receiver, f, f_0, f_1) {
      closure$demo.loadScene(this$Demo.newScenes_0, closure$ctx);
      this$.isOpen = false;
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda$lambda$lambda(closure$y, closure$demo, this$Demo, closure$ctx, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$y.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0, true), dps(30.0, true), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      closure$y.v -= 35.0;
      var $receiver_0 = $receiver.onClick;
      var element = Demo$demoOverlay$lambda$lambda$lambda$lambda$lambda$lambda(closure$demo, this$Demo, closure$ctx, this$);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda_0(this$Demo, closure$ctx, this$, this$_0) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new BlankComponentUi());
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(45.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(full(), pcs(100.0, true).minus_m986jv$(dps(110.0, true)), full());
      var y = {v: -30.0};
      var $receiver_0 = this$Demo.demos_0.values;
      var destination = ArrayList_init();
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (!element.isHidden)
          destination.add_11rb$(element);
      }
      var tmp$_0;
      tmp$_0 = destination.iterator();
      while (tmp$_0.hasNext()) {
        var element_0 = tmp$_0.next();
        var this$Demo_0 = this$Demo;
        var closure$ctx_0 = closure$ctx;
        var this$_1 = this$;
        $receiver.unaryPlus_uv0sim$(this$_0.button_9zrh0o$(element_0.label, Demo$demoOverlay$lambda$lambda$lambda$lambda$lambda(y, element_0, this$Demo_0, closure$ctx_0, this$_1)));
      }
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda$lambda(this$, this$Demo) {
    return function ($receiver, f, f_0, f_1) {
      this$Demo.dbgOverlay_0.ui.isVisible = this$.isEnabled;
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda_1(this$Demo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(10.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0, true), dps(30.0, true), full());
      $receiver.text = 'Debug Info';
      $receiver.isEnabled = this$Demo.dbgOverlay_0.ui.isVisible;
      var $receiver_0 = $receiver.onClick;
      var element = Demo$demoOverlay$lambda$lambda$lambda$lambda($receiver, this$Demo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda_0(this$Demo, closure$ctx, this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.container_t34sov$('demos', Demo$demoOverlay$lambda$lambda$lambda_0(this$Demo, closure$ctx, $receiver, this$)));
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('showDbg', Demo$demoOverlay$lambda$lambda$lambda_1(this$Demo)));
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda(this$Demo, closure$ctx) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK, Demo$demoOverlay$lambda$lambda);
      $receiver.content.ui.setCustom_11rb$(new BlankComponentUi());
      $receiver.unaryPlus_63m4fk$($receiver.drawerMenu_enmky4$('menu', 'Demos', void 0, Demo$demoOverlay$lambda$lambda_0(this$Demo, closure$ctx, $receiver)));
      return Unit;
    };
  }
  Demo.prototype.demoOverlay_0 = function (ctx) {
    return uiScene(ctx.screenDpi, 'demo-overlay', void 0, Demo$demoOverlay$lambda(this, ctx));
  };
  function Demo$DemoEntry(label, isHidden, loadScene) {
    if (isHidden === void 0)
      isHidden = false;
    this.label = label;
    this.isHidden = isHidden;
    this.loadScene = loadScene;
  }
  Demo$DemoEntry.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DemoEntry',
    interfaces: []
  };
  function Demo$Companion() {
    Demo$Companion_instance = this;
    this.demoProps = LinkedHashMap_init();
  }
  Object.defineProperty(Demo$Companion.prototype, 'pbrBasePath', {
    get: function () {
      var key = 'pbrDemo.materials';
      var default_0 = 'https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/materials';
      var tmp$, tmp$_0;
      return (tmp$_0 = typeof (tmp$ = this.demoProps.get_11rb$(key)) === 'string' ? tmp$ : null) != null ? tmp$_0 : default_0;
    }
  });
  Demo$Companion.prototype.setProperty_bm4g0d$ = function (key, value) {
    this.demoProps.put_xwzc9p$(key, value);
  };
  Demo$Companion.prototype.getProperty_umlfku$ = defineInlineFunction('kooldemo.de.fabmax.kool.demo.Demo.Companion.getProperty_umlfku$', function (T_0, isT, key, default_0) {
    var tmp$, tmp$_0;
    return (tmp$_0 = isT(tmp$ = this.demoProps.get_11rb$(key)) ? tmp$ : null) != null ? tmp$_0 : default_0;
  });
  Demo$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Demo$Companion_instance = null;
  function Demo$Companion_getInstance() {
    if (Demo$Companion_instance === null) {
      new Demo$Companion();
    }return Demo$Companion_instance;
  }
  function Demo$defaultScene$lambda($receiver, it) {
    $receiver.addAll_brywnq$(pbrDemoScene(it));
    return Unit;
  }
  function Demo$demos$lambda($receiver, it) {
    $receiver.addAll_brywnq$(pbrDemoScene(it));
    return Unit;
  }
  function Demo$demos$lambda_0($receiver, it) {
    $receiver.addAll_brywnq$(multiLightDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_1($receiver, it) {
    $receiver.addAll_brywnq$(simplificationDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_2($receiver, it) {
    $receiver.addAll_brywnq$(treeScene(it));
    return Unit;
  }
  function Demo$demos$lambda_3($receiver, it) {
    $receiver.add_11rb$(helloWorldScene());
    return Unit;
  }
  Demo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Demo',
    interfaces: []
  };
  function Cycler(elements) {
    this.$delegate_ubybqq$_0 = elements;
    this.index = 0;
  }
  Object.defineProperty(Cycler.prototype, 'current', {
    get: function () {
      return this.get_za3lpa$(this.index);
    }
  });
  Cycler.prototype.next = function () {
    this.index = (this.index + 1 | 0) % this.size;
    return this.current;
  };
  Cycler.prototype.prev = function () {
    this.index = (this.index + this.size - 1 + this.size | 0) % this.size;
    return this.current;
  };
  Object.defineProperty(Cycler.prototype, 'size', {
    get: function () {
      return this.$delegate_ubybqq$_0.size;
    }
  });
  Cycler.prototype.contains_11rb$ = function (element) {
    return this.$delegate_ubybqq$_0.contains_11rb$(element);
  };
  Cycler.prototype.containsAll_brywnq$ = function (elements) {
    return this.$delegate_ubybqq$_0.containsAll_brywnq$(elements);
  };
  Cycler.prototype.get_za3lpa$ = function (index) {
    return this.$delegate_ubybqq$_0.get_za3lpa$(index);
  };
  Cycler.prototype.indexOf_11rb$ = function (element) {
    return this.$delegate_ubybqq$_0.indexOf_11rb$(element);
  };
  Cycler.prototype.isEmpty = function () {
    return this.$delegate_ubybqq$_0.isEmpty();
  };
  Cycler.prototype.iterator = function () {
    return this.$delegate_ubybqq$_0.iterator();
  };
  Cycler.prototype.lastIndexOf_11rb$ = function (element) {
    return this.$delegate_ubybqq$_0.lastIndexOf_11rb$(element);
  };
  Cycler.prototype.listIterator = function () {
    return this.$delegate_ubybqq$_0.listIterator();
  };
  Cycler.prototype.listIterator_za3lpa$ = function (index) {
    return this.$delegate_ubybqq$_0.listIterator_za3lpa$(index);
  };
  Cycler.prototype.subList_vux9f0$ = function (fromIndex, toIndex) {
    return this.$delegate_ubybqq$_0.subList_vux9f0$(fromIndex, toIndex);
  };
  Cycler.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Cycler',
    interfaces: [List]
  };
  function helloWorldScene$lambda$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.colored_6taknv$();
    $receiver_0.centered();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function helloWorldScene$lambda$lambda$lambda_0($receiver) {
    $receiver.albedoSource = Albedo.VERTEX_ALBEDO;
    $receiver.metallic = 0.0;
    $receiver.roughness = 0.25;
    return Unit;
  }
  function helloWorldScene$lambda$lambda($receiver) {
    $receiver.generate_v2sixm$(helloWorldScene$lambda$lambda$lambda);
    $receiver.pipelineLoader = pbrShader(helloWorldScene$lambda$lambda$lambda_0);
    return Unit;
  }
  function helloWorldScene$lambda$lambda_0($receiver) {
    $receiver.setDirectional_czzhiu$(new Vec3f(-1.0, -1.0, -1.0));
    $receiver.setColor_y83vuj$(Color.Companion.WHITE, 5.0);
    return Unit;
  }
  function helloWorldScene() {
    var $receiver = new Scene_init('Hello World Demo');
    defaultCamTransform($receiver);
    $receiver.unaryPlus_uv0sim$(colorMesh(void 0, helloWorldScene$lambda$lambda));
    $receiver.lighting.singleLight_q9zcvo$(helloWorldScene$lambda$lambda_0);
    return $receiver;
  }
  function instanceTest$lambda$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.colored_6taknv$(false);
    $receiver_0.centered();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function instanceTest$lambda$lambda($receiver) {
    $receiver.generate_v2sixm$(instanceTest$lambda$lambda$lambda);
    var $receiver_0 = new MeshInstanceList(listOf(MeshInstanceList.Companion.MODEL_MAT));
    for (var x = -1; x <= 1; x++) {
      for (var y = -1; y <= 1; y++) {
        $receiver_0.checkBufferSize_za3lpa$();
        var szBefore = $receiver_0.dataF.position;
        $receiver_0.dataF.put_q3cr5i$((new Mat4f()).translate_y2kzbl$(x * 3.0, y * 3.0, 0.0).matrix);
        var growSz = $receiver_0.dataF.position - szBefore | 0;
        if (growSz !== $receiver_0.instanceSizeF) {
          throw IllegalStateException_init('Expected data to grow by ' + $receiver_0.instanceSizeF + ' elements, instead it grew by ' + growSz);
        }$receiver_0.numInstances = $receiver_0.numInstances + 1 | 0;
      }
    }
    $receiver.instances = $receiver_0;
    var $receiver_1 = new ShaderModel();
    var ifColors = {v: null};
    var $receiver_2 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver_1);
    ifColors.v = $receiver_2.stageInterfaceNode_wtmwsg$('ifColors', $receiver_2.attrColors().output);
    var instModelMat = $receiver_2.instanceAttrModelMat().output;
    var mvpMat = $receiver_2.premultipliedMvpNode().outMvpMat;
    var mvp = $receiver_2.multiplyNode_ze33is$(mvpMat, instModelMat).output;
    $receiver_2.positionOutput = $receiver_2.vertexPositionNode_ze33is$($receiver_2.attrPositions().output, mvp).outPosition;
    var $receiver_3 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver_1);
    $receiver_3.colorOutput = $receiver_3.unlitMaterialNode_r20yfm$(ifColors.v.output).outColor;
    $receiver.pipelineLoader = new ModeledShader($receiver_1);
    return Unit;
  }
  function instanceTest() {
    var ctx = createDefaultContext();
    var tmp$ = ctx.scenes;
    var $receiver = new Scene_init(null);
    defaultCamTransform($receiver);
    $receiver.unaryPlus_uv0sim$(colorMesh(void 0, instanceTest$lambda$lambda));
    tmp$.add_11rb$($receiver);
    ctx.run();
  }
  function MatColorNode(graph, inputMat) {
    ShaderNode.call(this, 'matColor', graph);
    this.inputMat = inputMat;
    this.output = new ShaderNodeIoVar(new ModelVar4f('matOutCol'));
  }
  MatColorNode.prototype.setup_llmhyc$ = function (shaderGraph) {
    ShaderNode.prototype.setup_llmhyc$.call(this, shaderGraph);
    this.dependsOn_7qvs0d$(this.inputMat);
  };
  MatColorNode.prototype.generateCode_626509$ = function (generator) {
    generator.appendMain_61zpoe$(this.output.declare() + ' = vec4(' + this.inputMat.name + '[1].xyz, 1.0);');
  };
  MatColorNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MatColorNode',
    interfaces: [ShaderNode]
  };
  function multiLightDemo(ctx) {
    return (new MultiLightDemo(ctx)).scenes;
  }
  function MultiLightDemo(ctx) {
    MultiLightDemo$Companion_getInstance();
    this.scenes = ArrayList_init();
    this.mainScene_0 = null;
    this.lights_0 = listOf_0([new MultiLightDemo$LightMesh(this, Color.Companion.MD_CYAN), new MultiLightDemo$LightMesh(this, Color.Companion.MD_RED), new MultiLightDemo$LightMesh(this, Color.Companion.MD_AMBER), new MultiLightDemo$LightMesh(this, Color.Companion.MD_GREEN)]);
    this.depthPasses_0 = ArrayList_init();
    this.lightCount_0 = 4;
    this.lightPower_0 = 500.0;
    this.lightSaturation_0 = 0.4;
    this.lightRandomness_0 = 0.3;
    this.autoRotate_0 = true;
    this.showLightIndicators_0 = true;
    var $receiver = new Cycler(MultiLightDemo$Companion_getInstance().matColors_0);
    $receiver.index = 1;
    this.colorCycler_0 = $receiver;
    this.modelShader_0 = null;
    this.mainScene_0 = this.mainScene_1(ctx);
    var $receiver_0 = this.scenes;
    var element = this.mainScene_0;
    $receiver_0.add_11rb$(element);
    var $receiver_1 = this.scenes;
    var element_0 = this.menu_0(ctx);
    $receiver_1.add_11rb$(element_0);
    var tmp$;
    tmp$ = this.lights_0.iterator();
    while (tmp$.hasNext()) {
      var element_1 = tmp$.next();
      var pass = new ShadowMapPass(this.mainScene_0, element_1.light);
      this.depthPasses_0.add_11rb$(pass);
      var $receiver_2 = ctx.offscreenPasses;
      var element_2 = pass.offscreenPass;
      $receiver_2.add_11rb$(element_2);
    }
    this.mainScene_0.onDispose.add_11rb$(MultiLightDemo_init$lambda(this, ctx));
  }
  function MultiLightDemo$mainScene$lambda$lambda$lambda(this$MultiLightDemo, this$) {
    return function ($receiver, ctx) {
      if (this$MultiLightDemo.autoRotate_0) {
        this$.verticalRotation += ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function MultiLightDemo$mainScene$lambda$lambda(this$, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 17.0;
      $receiver.translation.set_y2kzbl$(0.0, 2.0, 0.0);
      $receiver.setMouseRotation_dleff0$(0.0, -20.0);
      var $receiver_0 = $receiver.onPreRender;
      var element = MultiLightDemo$mainScene$lambda$lambda$lambda(this$MultiLightDemo, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$mainScene$lambda$lambda$lambda$lambda(this$, this$MultiLightDemo) {
    return function (it) {
      var tmp$;
      if ((tmp$ = this$.depthMaps) != null) {
        var tmp$_0, tmp$_0_0;
        var index = 0;
        tmp$_0 = this$MultiLightDemo.depthPasses_0.iterator();
        while (tmp$_0.hasNext()) {
          var item = tmp$_0.next();
          var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
          if (i < tmp$.length) {
            tmp$[i] = item.offscreenPass.impl.depthTexture;
          }}
      }return Unit;
    };
  }
  function MultiLightDemo$mainScene$lambda$lambda_0(this$, this$MultiLightDemo) {
    return function (it) {
      this$.unaryPlus_uv0sim$(it);
      var $receiver = new PbrShader$PbrConfig();
      $receiver.albedoSource = Albedo.STATIC_ALBEDO;
      $receiver.isReceivingShadows = true;
      var cfg = $receiver;
      var tmp$ = this$MultiLightDemo;
      var $receiver_0 = new PbrShader(cfg);
      var this$MultiLightDemo_0 = this$MultiLightDemo;
      $receiver_0.albedo = this$MultiLightDemo_0.colorCycler_0.current.linColor;
      $receiver_0.roughness = 0.1;
      $receiver_0.metallic = 0.0;
      $receiver_0.onCreated.add_11rb$(MultiLightDemo$mainScene$lambda$lambda$lambda$lambda($receiver_0, this$MultiLightDemo_0));
      tmp$.modelShader_0 = $receiver_0;
      it.pipelineLoader = this$MultiLightDemo.modelShader_0;
      return Unit;
    };
  }
  function MultiLightDemo$mainScene$lambda$lambda$lambda_0($receiver) {
    var $receiver_0 = $receiver.rectProps.defaults();
    $receiver.rotate_ad55pp$(-90.0, Vec3f.Companion.X_AXIS);
    $receiver_0.size.set_dleff0$(100.0, 100.0);
    $receiver_0.origin.set_y2kzbl$(-$receiver_0.size.x / 2, -$receiver_0.size.y / 2, 0.0);
    $receiver_0.fullTexCoords_mx4ult$(4.0);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda.prototype.constructor = Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda;
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/woodfloor/WoodFlooringMahoganyAfricanSanded001_COL_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0.prototype.constructor = Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0;
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/woodfloor/WoodFlooringMahoganyAfricanSanded001_NRM_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1.prototype.constructor = Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1;
  Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/woodfloor/WoodFlooringMahoganyAfricanSanded001_REFL_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_2($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_3(this$) {
    return function ($receiver, it) {
      ensureNotNull(this$.albedoMap).dispose();
      ensureNotNull(this$.normalMap).dispose();
      ensureNotNull(this$.roughnessMap).dispose();
      return Unit;
    };
  }
  function MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_4(this$, this$MultiLightDemo) {
    return function (it) {
      var tmp$;
      if ((tmp$ = this$.depthMaps) != null) {
        var tmp$_0, tmp$_0_0;
        var index = 0;
        tmp$_0 = this$MultiLightDemo.depthPasses_0.iterator();
        while (tmp$_0.hasNext()) {
          var item = tmp$_0.next();
          var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
          if (i < tmp$.length) {
            tmp$[i] = item.offscreenPass.impl.depthTexture;
          }}
      }return Unit;
    };
  }
  function MultiLightDemo$mainScene$lambda$lambda_1(this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.isCastingShadow = false;
      $receiver.generate_v2sixm$(MultiLightDemo$mainScene$lambda$lambda$lambda_0);
      var $receiver_0 = new PbrShader$PbrConfig();
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.isNormalMapped = true;
      $receiver_0.isRoughnessMapped = true;
      $receiver_0.isReceivingShadows = true;
      var cfg = $receiver_0;
      var $receiver_1 = new PbrShader(cfg);
      var this$MultiLightDemo_0 = this$MultiLightDemo;
      $receiver_1.albedoMap = new Texture(void 0, MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_0);
      $receiver_1.normalMap = new Texture(void 0, MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_1);
      $receiver_1.roughnessMap = new Texture(void 0, MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_2);
      $receiver_1.metallic = 0.0;
      $receiver.onDispose.add_11rb$(MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_3($receiver_1));
      $receiver_1.onCreated.add_11rb$(MultiLightDemo$mainScene$lambda$lambda$lambda$lambda_4($receiver_1, this$MultiLightDemo_0));
      $receiver.pipelineLoader = $receiver_1;
      return Unit;
    };
  }
  MultiLightDemo.prototype.mainScene_1 = function (ctx) {
    var $receiver = new Scene_init(null);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, MultiLightDemo$mainScene$lambda$lambda($receiver, this)));
    $receiver.lighting.lights.clear();
    var tmp$;
    tmp$ = this.lights_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      $receiver.unaryPlus_uv0sim$(element);
    }
    this.updateLighting_0();
    this.loadModel_0(ctx, 'bunny.kmfz', 0.05, new Vec3f(0.0, 3.75, 0.0), MultiLightDemo$mainScene$lambda$lambda_0($receiver, this));
    $receiver.unaryPlus_uv0sim$(textureMesh(void 0, true, MultiLightDemo$mainScene$lambda$lambda_1(this)));
    return $receiver;
  };
  function MultiLightDemo$menu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function MultiLightDemo$menu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(MultiLightDemo$menu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(MultiLightDemo$menu$lambda$lambda$lambda_0);
    return Unit;
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_1(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Lights';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_2(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.text = 'Lights:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda(this$MultiLightDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      tmp$ = this$MultiLightDemo.lightCount_0;
      this$MultiLightDemo.lightCount_0 = tmp$ + 1 | 0;
      if (this$MultiLightDemo.lightCount_0 > 4) {
        this$MultiLightDemo.lightCount_0 = 1;
      }$receiver.text = this$MultiLightDemo.lightCount_0.toString();
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_3(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(45.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(40.0), dps(35.0), full());
      $receiver.text = this$MultiLightDemo.lightCount_0.toString();
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_0(this$MultiLightDemo, closure$btnLightCnt) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      tmp$ = this$MultiLightDemo.lightCount_0;
      this$MultiLightDemo.lightCount_0 = tmp$ - 1 | 0;
      if (this$MultiLightDemo.lightCount_0 < 1) {
        this$MultiLightDemo.lightCount_0 = 4;
      }closure$btnLightCnt.text = this$MultiLightDemo.lightCount_0.toString();
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_4(closure$y, this$MultiLightDemo, closure$btnLightCnt) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(35.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_0(this$MultiLightDemo, closure$btnLightCnt);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_1(this$MultiLightDemo, closure$btnLightCnt) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      tmp$ = this$MultiLightDemo.lightCount_0;
      this$MultiLightDemo.lightCount_0 = tmp$ + 1 | 0;
      if (this$MultiLightDemo.lightCount_0 > 4) {
        this$MultiLightDemo.lightCount_0 = 1;
      }closure$btnLightCnt.text = this$MultiLightDemo.lightCount_0.toString();
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_5(closure$y, this$MultiLightDemo, closure$btnLightCnt) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(85.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_1(this$MultiLightDemo, closure$btnLightCnt);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_6(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.text = 'Strength:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_2(this$MultiLightDemo) {
    return function ($receiver, it) {
      this$MultiLightDemo.lightPower_0 = $receiver.value * 10.0;
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_7(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(30.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.value = this$MultiLightDemo.lightPower_0 / 10.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_2(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_8(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.text = 'Saturation:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_3(this$MultiLightDemo) {
    return function ($receiver, it) {
      this$MultiLightDemo.lightSaturation_0 = $receiver.value / 100.0;
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_9(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(30.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.value = this$MultiLightDemo.lightSaturation_0 * 100.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_3(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_10(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.text = 'Random:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_4(this$MultiLightDemo) {
    return function ($receiver, it) {
      this$MultiLightDemo.lightRandomness_0 = $receiver.value / 100.0;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_11(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(30.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.value = this$MultiLightDemo.lightRandomness_0 * 100.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_4(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_12(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Material';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_13(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.text = 'Color:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_5(this$MultiLightDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      $receiver.text = this$MultiLightDemo.colorCycler_0.next().name;
      (tmp$ = this$MultiLightDemo.modelShader_0) != null ? (tmp$.albedo = this$MultiLightDemo.colorCycler_0.current.linColor) : null;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_14(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(45.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(40.0), dps(35.0), full());
      $receiver.text = this$MultiLightDemo.colorCycler_0.current.name;
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_5(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_6(this$MultiLightDemo, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      closure$matLabel.text = this$MultiLightDemo.colorCycler_0.prev().name;
      (tmp$ = this$MultiLightDemo.modelShader_0) != null ? (tmp$.albedo = this$MultiLightDemo.colorCycler_0.current.linColor) : null;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_15(closure$y, this$MultiLightDemo, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(35.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_6(this$MultiLightDemo, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_7(this$MultiLightDemo, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      closure$matLabel.text = this$MultiLightDemo.colorCycler_0.next().name;
      (tmp$ = this$MultiLightDemo.modelShader_0) != null ? (tmp$.albedo = this$MultiLightDemo.colorCycler_0.current.linColor) : null;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_16(closure$y, this$MultiLightDemo, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(85.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_7(this$MultiLightDemo, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_17(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.text = 'Roughness:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_8(this$MultiLightDemo) {
    return function ($receiver, it) {
      var tmp$;
      (tmp$ = this$MultiLightDemo.modelShader_0) != null ? (tmp$.roughness = $receiver.value / 100.0) : null;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_18(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(30.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.value = 10.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_8(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_19(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Scene';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_9(this$MultiLightDemo) {
    return function ($receiver) {
      this$MultiLightDemo.autoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_20(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.text = 'Auto Rotate';
      $receiver.isEnabled = this$MultiLightDemo.autoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_9(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_10(this$MultiLightDemo) {
    return function ($receiver) {
      this$MultiLightDemo.showLightIndicators_0 = $receiver.isEnabled;
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_21(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.text = 'Light Indicators';
      $receiver.isEnabled = this$MultiLightDemo.showLightIndicators_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_10(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda_0(closure$smallFont, this$, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-450.0), dps(-535.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(330.0), dps(415.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lights', MultiLightDemo$menu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lightCntLbl', MultiLightDemo$menu$lambda$lambda$lambda_2(y)));
      var btnLightCnt = this$.button_9zrh0o$('lightCnt', MultiLightDemo$menu$lambda$lambda$lambda_3(y, this$MultiLightDemo));
      $receiver.unaryPlus_uv0sim$(btnLightCnt);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('decLightCnt', MultiLightDemo$menu$lambda$lambda$lambda_4(y, this$MultiLightDemo, btnLightCnt)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('incLightCnt', MultiLightDemo$menu$lambda$lambda$lambda_5(y, this$MultiLightDemo, btnLightCnt)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lightPowerLbl', MultiLightDemo$menu$lambda$lambda$lambda_6(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('lightPowerSlider', MultiLightDemo$menu$lambda$lambda$lambda_7(y, this$MultiLightDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('saturationLbl', MultiLightDemo$menu$lambda$lambda$lambda_8(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('saturationSlider', MultiLightDemo$menu$lambda$lambda$lambda_9(y, this$MultiLightDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('randomLbl', MultiLightDemo$menu$lambda$lambda$lambda_10(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('randomSlider', MultiLightDemo$menu$lambda$lambda$lambda_11(y, this$MultiLightDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('material', MultiLightDemo$menu$lambda$lambda$lambda_12(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('colorLbl', MultiLightDemo$menu$lambda$lambda$lambda_13(y)));
      var matLabel = this$.button_9zrh0o$('selected-color', MultiLightDemo$menu$lambda$lambda$lambda_14(y, this$MultiLightDemo));
      $receiver.unaryPlus_uv0sim$(matLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('color-left', MultiLightDemo$menu$lambda$lambda$lambda_15(y, this$MultiLightDemo, matLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('color-right', MultiLightDemo$menu$lambda$lambda$lambda_16(y, this$MultiLightDemo, matLabel)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('roughnessLbl', MultiLightDemo$menu$lambda$lambda$lambda_17(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('roughhnessSlider', MultiLightDemo$menu$lambda$lambda$lambda_18(y, this$MultiLightDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('scene', MultiLightDemo$menu$lambda$lambda$lambda_19(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('tbAutoRot', MultiLightDemo$menu$lambda$lambda$lambda_20(y, this$MultiLightDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('tbLightIndicators', MultiLightDemo$menu$lambda$lambda$lambda_21(y, this$MultiLightDemo)));
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda(closure$ctx, this$MultiLightDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, MultiLightDemo$menu$lambda$lambda);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', MultiLightDemo$menu$lambda$lambda_0(smallFont, $receiver, this$MultiLightDemo)));
      return Unit;
    };
  }
  MultiLightDemo.prototype.menu_0 = function (ctx) {
    return uiScene(void 0, void 0, void 0, MultiLightDemo$menu$lambda(ctx, this));
  };
  MultiLightDemo.prototype.updateLighting_0 = function () {
    var tmp$;
    var tmp$_0;
    tmp$_0 = this.lights_0.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      element.disable();
    }
    var pos = 0.0;
    var step = 360.0 / this.lightCount_0;
    var a = this.lightCount_0;
    var b = this.lights_0.size;
    tmp$ = Math_0.min(a, b);
    for (var i = 0; i < tmp$; i++) {
      this.lights_0.get_za3lpa$(i).setup_mx4ult$(pos);
      this.lights_0.get_za3lpa$(i).enable();
      pos += step;
    }
    var tmp$_1;
    tmp$_1 = this.lights_0.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.updateVisibility();
    }
  };
  function MultiLightDemo$LightMesh($outer, color) {
    this.$outer = $outer;
    TransformGroup.call(this);
    this.color = color;
    this.light = new Light();
    var $receiver = new LineMesh();
    $receiver.isCastingShadow = false;
    this.spotAngleMesh_0 = $receiver;
    this.isEnabled_0 = true;
    this.animPos_0 = 0.0;
    this.lightMeshShader_0 = new ModeledShader$StaticColor();
    this.meshPos_0 = MutableVec3f_init();
    this.anglePos_0 = 0.0;
    this.rotOff_0 = randomF(0.0, 3.0);
    this.light.setSpot_nve3wz$(Vec3f.Companion.ZERO, Vec3f.Companion.X_AXIS, 50.0);
    var lightMesh = colorMesh(void 0, MultiLightDemo$MultiLightDemo$LightMesh_init$lambda(this));
    this.unaryPlus_uv0sim$(lightMesh);
    this.unaryPlus_uv0sim$(this.spotAngleMesh_0);
    var $receiver_0 = this.onPreRender;
    var element = MultiLightDemo$MultiLightDemo$LightMesh_init$lambda_0(this.$outer, this);
    $receiver_0.add_11rb$(element);
  }
  MultiLightDemo$LightMesh.prototype.updateSpotAngleMesh_0 = function () {
    var x = this.light.spotAngle * math_0.DEG_2_RAD / 2;
    var r = 1.0 * Math_0.tan(x);
    var c = this.lightMeshShader_0.color;
    var n = 40;
    this.spotAngleMesh_0.clear();
    for (var i = 0; i < n; i++) {
      var a0 = i / n * 2 * math.PI;
      var a1 = (i + 1 | 0) / n * 2 * math.PI;
      this.spotAngleMesh_0.addLine_b8opkg$(new Vec3f(-1.0, Math_0.cos(a0) * r, Math_0.sin(a0) * r), c, new Vec3f(-1.0, Math_0.cos(a1) * r, Math_0.sin(a1) * r), c);
    }
    var x_0 = 45.0 * math_0.DEG_2_RAD;
    var e = Math_0.cos(x_0) * r;
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, e, e), c);
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, e, -e), c);
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, -e, -e), c);
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, -e, e), c);
  };
  MultiLightDemo$LightMesh.prototype.setup_mx4ult$ = function (angPos) {
    var x = angPos * math_0.DEG_2_RAD;
    var x_0 = Math_0.cos(x) * 10.0;
    var x_1 = angPos * math_0.DEG_2_RAD;
    var z = Math_0.sin(x_1) * 10.0;
    this.meshPos_0.set_y2kzbl$(x_0, 9.0, -z);
    this.anglePos_0 = angPos;
    var color = Color.Companion.WHITE.mix_jpxij3$(this.color, this.$outer.lightSaturation_0, MutableColor_init());
    this.light.setColor_y83vuj$(color.toLinear(), this.$outer.lightPower_0);
    this.lightMeshShader_0.color = color;
    this.updateSpotAngleMesh_0();
  };
  MultiLightDemo$LightMesh.prototype.enable = function () {
    var tmp$, tmp$_0, tmp$_1;
    this.isEnabled_0 = true;
    if ((tmp$_1 = (tmp$_0 = (tmp$ = this.scene) != null ? tmp$.lighting : null) != null ? tmp$_0.lights : null) != null) {
      if (!tmp$_1.contains_11rb$(this.light)) {
        tmp$_1.add_11rb$(this.light);
      }}this.updateVisibility();
  };
  MultiLightDemo$LightMesh.prototype.disable = function () {
    var tmp$, tmp$_0, tmp$_1;
    this.isEnabled_0 = false;
    (tmp$_1 = (tmp$_0 = (tmp$ = this.scene) != null ? tmp$.lighting : null) != null ? tmp$_0.lights : null) != null ? tmp$_1.remove_11rb$(this.light) : null;
    this.updateVisibility();
  };
  MultiLightDemo$LightMesh.prototype.updateVisibility = function () {
    this.isVisible = (this.isEnabled_0 && this.$outer.showLightIndicators_0);
  };
  function MultiLightDemo$MultiLightDemo$LightMesh_init$lambda$lambda($receiver) {
    $receiver.sphereProps.defaults().radius = 0.1;
    $receiver.sphere_mojs8w$($receiver.sphereProps);
    $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.Z_AXIS);
    var $receiver_0 = $receiver.cylinderProps.defaults();
    $receiver_0.bottomRadius = 0.015;
    $receiver_0.topRadius = 0.015;
    $receiver_0.height = 0.85;
    $receiver_0.steps = 4;
    $receiver.cylinder_tnt2h$($receiver.cylinderProps);
    $receiver.translate_y2kzbl$(0.0, 0.85, 0.0);
    var $receiver_1 = $receiver.cylinderProps.defaults();
    $receiver_1.bottomRadius = 0.1;
    $receiver_1.topRadius = 0.0025;
    $receiver_1.height = 0.15;
    $receiver.cylinder_tnt2h$($receiver.cylinderProps);
    return Unit;
  }
  function MultiLightDemo$MultiLightDemo$LightMesh_init$lambda(this$LightMesh) {
    return function ($receiver) {
      $receiver.isCastingShadow = false;
      $receiver.generate_v2sixm$(MultiLightDemo$MultiLightDemo$LightMesh_init$lambda$lambda);
      $receiver.pipelineLoader = this$LightMesh.lightMeshShader_0;
      return Unit;
    };
  }
  function MultiLightDemo$MultiLightDemo$LightMesh_init$lambda_0(this$MultiLightDemo, this$LightMesh) {
    return function ($receiver, ctx) {
      if (this$MultiLightDemo.autoRotate_0) {
        this$LightMesh.animPos_0 += ctx.deltaT;
      }var x = this$LightMesh.animPos_0 / 15 + this$LightMesh.rotOff_0;
      var r = Math_0.cos(x) * this$MultiLightDemo.lightRandomness_0;
      this$LightMesh.light.spotAngle = 60.0 - r * 20.0;
      this$LightMesh.updateSpotAngleMesh_0();
      this$LightMesh.setIdentity();
      this$LightMesh.rotate_ad55pp$(this$LightMesh.animPos_0 * -10.0, Vec3f.Companion.Y_AXIS);
      this$LightMesh.translate_czzhiu$(this$LightMesh.meshPos_0);
      this$LightMesh.rotate_ad55pp$(this$LightMesh.anglePos_0, Vec3f.Companion.Y_AXIS);
      this$LightMesh.rotate_ad55pp$(30.0 + 20.0 * r, Vec3f.Companion.Z_AXIS);
      this$LightMesh.transform.transform_w1lst9$(this$LightMesh.light.position.set_czzhiu$(Vec3f.Companion.ZERO), 1.0);
      this$LightMesh.transform.transform_w1lst9$(this$LightMesh.light.direction.set_czzhiu$(Vec3f.Companion.NEG_X_AXIS), 0.0);
      return Unit;
    };
  }
  MultiLightDemo$LightMesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LightMesh',
    interfaces: [TransformGroup]
  };
  function MultiLightDemo$loadModel$lambda(closure$scale, closure$translation, closure$recv) {
    return function (model) {
      if (model != null) {
        var mesh = model.meshes.get_za3lpa$(0).toMesh_8p8ifh$();
        var $this = mesh.geometry;
        var tmp$;
        tmp$ = $this.numVertices;
        for (var i = 0; i < tmp$; i++) {
          $this.vertexIt.index = i;
          var closure$scale_0 = closure$scale;
          var closure$translation_0 = closure$translation;
          $this.vertexIt.position.scale_mx4ult$(closure$scale_0).add_czzhiu$(closure$translation_0);
        }
        closure$recv(mesh);
      }return Unit;
    };
  }
  MultiLightDemo.prototype.loadModel_0 = function ($receiver, path, scale, translation, recv) {
    $receiver.assetMgr.loadModel_v5uqdg$(path, MultiLightDemo$loadModel$lambda(scale, translation, recv));
  };
  function MultiLightDemo$MatColor(name, linColor) {
    this.name = name;
    this.linColor = linColor;
  }
  MultiLightDemo$MatColor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MatColor',
    interfaces: []
  };
  MultiLightDemo$MatColor.prototype.component1 = function () {
    return this.name;
  };
  MultiLightDemo$MatColor.prototype.component2 = function () {
    return this.linColor;
  };
  MultiLightDemo$MatColor.prototype.copy_guvy9a$ = function (name, linColor) {
    return new MultiLightDemo$MatColor(name === void 0 ? this.name : name, linColor === void 0 ? this.linColor : linColor);
  };
  MultiLightDemo$MatColor.prototype.toString = function () {
    return 'MatColor(name=' + Kotlin.toString(this.name) + (', linColor=' + Kotlin.toString(this.linColor)) + ')';
  };
  MultiLightDemo$MatColor.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.linColor) | 0;
    return result;
  };
  MultiLightDemo$MatColor.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.linColor, other.linColor)))));
  };
  function MultiLightDemo$Companion() {
    MultiLightDemo$Companion_instance = this;
    this.matColors_0 = listOf_0([new MultiLightDemo$MatColor('White', Color.Companion.WHITE.toLinear()), new MultiLightDemo$MatColor('Red', Color.Companion.MD_RED.toLinear()), new MultiLightDemo$MatColor('Pink', Color.Companion.MD_PINK.toLinear()), new MultiLightDemo$MatColor('Purple', Color.Companion.MD_PURPLE.toLinear()), new MultiLightDemo$MatColor('Deep Purple', Color.Companion.MD_DEEP_PURPLE.toLinear()), new MultiLightDemo$MatColor('Indigo', Color.Companion.MD_INDIGO.toLinear()), new MultiLightDemo$MatColor('Blue', Color.Companion.MD_BLUE.toLinear()), new MultiLightDemo$MatColor('Cyan', Color.Companion.MD_CYAN.toLinear()), new MultiLightDemo$MatColor('Teal', Color.Companion.MD_TEAL.toLinear()), new MultiLightDemo$MatColor('Green', Color.Companion.MD_GREEN.toLinear()), new MultiLightDemo$MatColor('Light Green', Color.Companion.MD_LIGHT_GREEN.toLinear()), new MultiLightDemo$MatColor('Lime', Color.Companion.MD_LIME.toLinear()), new MultiLightDemo$MatColor('Yellow', Color.Companion.MD_YELLOW.toLinear()), new MultiLightDemo$MatColor('Amber', Color.Companion.MD_AMBER.toLinear()), new MultiLightDemo$MatColor('Orange', Color.Companion.MD_ORANGE.toLinear()), new MultiLightDemo$MatColor('Deep Orange', Color.Companion.MD_DEEP_ORANGE.toLinear()), new MultiLightDemo$MatColor('Brown', Color.Companion.MD_BROWN.toLinear()), new MultiLightDemo$MatColor('Grey', Color.Companion.MD_GREY.toLinear()), new MultiLightDemo$MatColor('Blue Grey', Color.Companion.MD_BLUE_GREY.toLinear()), new MultiLightDemo$MatColor('Almost Black', (new Color(0.1, 0.1, 0.1)).toLinear())]);
  }
  MultiLightDemo$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MultiLightDemo$Companion_instance = null;
  function MultiLightDemo$Companion_getInstance() {
    if (MultiLightDemo$Companion_instance === null) {
      new MultiLightDemo$Companion();
    }return MultiLightDemo$Companion_instance;
  }
  function MultiLightDemo_init$lambda(this$MultiLightDemo, closure$ctx) {
    return function ($receiver, it) {
      var $receiver_0 = this$MultiLightDemo.depthPasses_0;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var closure$ctx_0 = closure$ctx;
        var $receiver_1 = closure$ctx_0.offscreenPasses;
        var element_0 = element.offscreenPass;
        $receiver_1.remove_11rb$(element_0);
        element.dispose_aemszp$(closure$ctx_0);
      }
      return Unit;
    };
  }
  MultiLightDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MultiLightDemo',
    interfaces: []
  };
  function ColorGridContent() {
    PbrDemo$PbrContent.call(this, 'Color Grid');
    this.shaders_0 = ArrayList_init();
    this.iblContent_0 = null;
    this.nonIblContent_0 = null;
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Material';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda_0(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = 'R:';
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda$lambda(this$ColorGridContent) {
    return function ($receiver, it) {
      var tmp$;
      tmp$ = this$ColorGridContent.shaders_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.roughness = $receiver.value / 100.0;
      }
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda_1(closure$y, this$ColorGridContent) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(85.0), dps(35.0), full());
      $receiver.value = 10.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = ColorGridContent$createMenu$lambda$lambda$lambda$lambda(this$ColorGridContent);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda_2(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = 'M:';
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda$lambda_0(this$ColorGridContent) {
    return function ($receiver, it) {
      var tmp$;
      tmp$ = this$ColorGridContent.shaders_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.metallic = $receiver.value / 100.0;
      }
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda$lambda_3(closure$y, this$ColorGridContent) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(85.0), dps(35.0), full());
      $receiver.value = 0.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = ColorGridContent$createMenu$lambda$lambda$lambda$lambda_0(this$ColorGridContent);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function ColorGridContent$createMenu$lambda$lambda(closure$yPos, closure$smallFont, this$, this$ColorGridContent) {
    return function ($receiver) {
      $receiver.isVisible = false;
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$yPos - 100.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(100.0), full());
      var y = {v: -30.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('mat-lbl', ColorGridContent$createMenu$lambda$lambda$lambda(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('mat-roughness-lbl', ColorGridContent$createMenu$lambda$lambda$lambda_0(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('mat-roughness-slider', ColorGridContent$createMenu$lambda$lambda$lambda_1(y, this$ColorGridContent)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('mat-metallic-lbl', ColorGridContent$createMenu$lambda$lambda$lambda_2(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('mat-metallic-slider', ColorGridContent$createMenu$lambda$lambda$lambda_3(y, this$ColorGridContent)));
      return Unit;
    };
  }
  ColorGridContent.prototype.createMenu_7fm8wr$ = function (parent, smallFont, yPos) {
    var $receiver = parent.root;
    this.ui = $receiver.container_t34sov$('pbr-color-container', ColorGridContent$createMenu$lambda$lambda(yPos, smallFont, $receiver, this));
    parent.plusAssign_f1kmr1$(ensureNotNull(this.ui));
  };
  ColorGridContent.prototype.setUseImageBasedLighting_6taknv$ = function (enabled) {
    var tmp$, tmp$_0;
    (tmp$ = this.iblContent_0) != null ? (tmp$.isVisible = enabled) : null;
    (tmp$_0 = this.nonIblContent_0) != null ? (tmp$_0.isVisible = !enabled) : null;
  };
  function ColorGridContent$createContent$lambda(closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$ColorGridContent) {
    return function ($receiver) {
      $receiver.isVisible = false;
      var ibl = this$ColorGridContent.makeSpheres_0(true, closure$irradianceMap, closure$reflectionMap, closure$brdfLut);
      var $receiver_0 = this$ColorGridContent.makeSpheres_0(false, closure$irradianceMap, closure$reflectionMap, closure$brdfLut);
      $receiver_0.isVisible = false;
      var nonIbl = $receiver_0;
      $receiver.unaryPlus_uv0sim$(ibl);
      $receiver.unaryPlus_uv0sim$(nonIbl);
      this$ColorGridContent.iblContent_0 = ibl;
      this$ColorGridContent.nonIblContent_0 = nonIbl;
      return Unit;
    };
  }
  ColorGridContent.prototype.createContent_wb8610$ = function (scene, irradianceMap, reflectionMap, brdfLut, ctx) {
    this.content = transformGroup(void 0, ColorGridContent$createContent$lambda(irradianceMap, reflectionMap, brdfLut, this));
    return ensureNotNull(this.content);
  };
  function ColorGridContent$makeSpheres$lambda$lambda$lambda(closure$nCols, closure$x, closure$spacing, closure$nRows, closure$y) {
    return function ($receiver) {
      var closure$nCols_0 = closure$nCols;
      var closure$x_0 = closure$x;
      var closure$spacing_0 = closure$spacing;
      var closure$nRows_0 = closure$nRows;
      var closure$y_0 = closure$y;
      var $receiver_0 = $receiver.sphereProps.defaults();
      $receiver_0.steps = 100;
      $receiver_0.center.set_y2kzbl$(((-(closure$nCols_0 - 1 | 0) | 0) * 0.5 + closure$x_0) * closure$spacing_0, ((closure$nRows_0 - 1 | 0) * 0.5 - closure$y_0) * closure$spacing_0, 0.0);
      $receiver_0.radius = 1.5;
      $receiver.sphere_mojs8w$($receiver.sphereProps);
      return Unit;
    };
  }
  function ColorGridContent$makeSpheres$lambda(closure$withIbl, closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$ColorGridContent) {
    return function ($receiver) {
      var nRows = 4;
      var nCols = 5;
      var spacing = 4.5;
      var colors = ArrayList_init();
      addAll(colors, Color.Companion.MD_COLORS);
      colors.remove_11rb$(Color.Companion.MD_LIGHT_BLUE);
      colors.remove_11rb$(Color.Companion.MD_GREY);
      colors.remove_11rb$(Color.Companion.MD_BLUE_GREY);
      var element = Color.Companion.WHITE;
      colors.add_11rb$(element);
      var element_0 = Color.Companion.MD_GREY;
      colors.add_11rb$(element_0);
      var element_1 = Color.Companion.MD_BLUE_GREY;
      colors.add_11rb$(element_1);
      var element_2 = new Color(0.1, 0.1, 0.1);
      colors.add_11rb$(element_2);
      for (var y = 0; y < nRows; y++) {
        for (var x = 0; x < nCols; x++) {
          var attributes = listOf_0([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]);
          var mesh = new Mesh_init(new IndexedVertexList_init(attributes), null);
          var closure$withIbl_0 = closure$withIbl;
          var closure$irradianceMap_0 = closure$irradianceMap;
          var closure$reflectionMap_0 = closure$reflectionMap;
          var closure$brdfLut_0 = closure$brdfLut;
          var this$ColorGridContent_0 = this$ColorGridContent;
          mesh.generate_v2sixm$(ColorGridContent$makeSpheres$lambda$lambda$lambda(nCols, x, spacing, nRows, y));
          var pbrConfig = new PbrShader$PbrConfig();
          pbrConfig.albedoSource = Albedo.STATIC_ALBEDO;
          pbrConfig.isImageBasedLighting = closure$withIbl_0;
          var shader = new PbrShader(pbrConfig);
          shader.albedo = colors.get_za3lpa$((Kotlin.imul(y, nCols) + x | 0) % colors.size).toLinear();
          shader.irradianceMap = closure$irradianceMap_0;
          shader.reflectionMap = closure$reflectionMap_0;
          shader.brdfLut = closure$brdfLut_0;
          shader.roughness = 0.1;
          shader.metallic = 0.0;
          mesh.pipelineLoader = shader;
          this$ColorGridContent_0.shaders_0.add_11rb$(shader);
          $receiver.unaryPlus_uv0sim$(mesh);
        }
      }
      return Unit;
    };
  }
  ColorGridContent.prototype.makeSpheres_0 = function (withIbl, irradianceMap, reflectionMap, brdfLut) {
    return group(void 0, ColorGridContent$makeSpheres$lambda(withIbl, irradianceMap, reflectionMap, brdfLut, this));
  };
  ColorGridContent.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorGridContent',
    interfaces: [PbrDemo$PbrContent]
  };
  function pbrDemoScene(ctx) {
    return (new PbrDemo(ctx)).scenes;
  }
  function PbrDemo(ctx) {
    PbrDemo$Companion_getInstance();
    this.ctx = ctx;
    this.scenes = ArrayList_init();
    this.contentScene_0 = null;
    this.irradianceMapPass_0 = null;
    this.reflectionMapPass_0 = null;
    this.brdfLut_0 = null;
    var $receiver = new Cycler(PbrDemo$Companion_getInstance().lightSetups_0);
    $receiver.index = 2;
    this.lightCycler_0 = $receiver;
    this.hdriCycler_0 = new Cycler(PbrDemo$Companion_getInstance().hdriTextures_0);
    var array = Array_0(PbrDemo$Companion_getInstance().hdriTextures_0.size);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = null;
    }
    this.loadedHdris_0 = array;
    this.pbrContentCycler_0 = new Cycler(listOf_0([new PbrMaterialContent(), new ColorGridContent(), new RoughnesMetalGridContent()]));
    var nextHdriKeyListener = this.ctx.inputMgr.registerKeyListener_aviy8w$(-25, 'Next environment map', PbrDemo_init$lambda, PbrDemo_init$lambda_0(this));
    var prevHdriKeyListener = this.ctx.inputMgr.registerKeyListener_aviy8w$(-26, 'Prev environment map', PbrDemo_init$lambda_1, PbrDemo_init$lambda_2(this));
    this.contentScene_0 = this.setupScene_0();
    this.contentScene_0.onDispose.add_11rb$(PbrDemo_init$lambda_3(this, nextHdriKeyListener, prevHdriKeyListener));
    var $receiver_0 = this.scenes;
    var element = this.contentScene_0;
    $receiver_0.add_11rb$(element);
    var $receiver_1 = this.scenes;
    var element_0 = this.pbrMenu_0();
    $receiver_1.add_11rb$(element_0);
  }
  function PbrDemo$setupScene$lambda$lambda$lambda(this$) {
    return function ($receiver, ctx) {
      this$.verticalRotation += ctx.deltaT * 2.0;
      return Unit;
    };
  }
  function PbrDemo$setupScene$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.onPreRender.add_11rb$(PbrDemo$setupScene$lambda$lambda$lambda($receiver));
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 20.0;
      return Unit;
    };
  }
  function PbrDemo$setupScene$lambda$lambda_0(this$PbrDemo, this$) {
    return function (tex) {
      var irrMapPass = new IrradianceMapPass(tex);
      var reflMapPass = new ReflectionMapPass(tex);
      var brdfLutPass = new BrdfLutPass();
      this$PbrDemo.irradianceMapPass_0 = irrMapPass;
      this$PbrDemo.reflectionMapPass_0 = reflMapPass;
      this$PbrDemo.brdfLut_0 = brdfLutPass;
      var $receiver = this$PbrDemo.ctx.offscreenPasses;
      var element = irrMapPass.offscreenPass;
      $receiver.add_11rb$(element);
      var $receiver_0 = this$PbrDemo.ctx.offscreenPasses;
      var element_0 = reflMapPass.offscreenPass;
      $receiver_0.add_11rb$(element_0);
      var $receiver_1 = this$PbrDemo.ctx.offscreenPasses;
      var element_1 = brdfLutPass.offscreenPass;
      $receiver_1.add_11rb$(element_1);
      this$.plusAssign_f1kmr1$(new Skybox(reflMapPass.reflectionMap, 1.25));
      var $receiver_2 = this$PbrDemo.pbrContentCycler_0;
      var tmp$;
      tmp$ = $receiver_2.iterator();
      while (tmp$.hasNext()) {
        var element_2 = tmp$.next();
        var this$_0 = this$;
        var this$PbrDemo_0 = this$PbrDemo;
        this$_0.unaryPlus_uv0sim$(element_2.createContent_wb8610$(this$_0, irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut, this$PbrDemo_0.ctx));
      }
      this$PbrDemo.pbrContentCycler_0.current.show();
      return Unit;
    };
  }
  PbrDemo.prototype.setupScene_0 = function () {
    var $receiver = new Scene_init(null);
    this.lightCycler_0.current.setup($receiver);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, PbrDemo$setupScene$lambda$lambda($receiver)));
    this.loadHdri_0(this.hdriCycler_0.index, PbrDemo$setupScene$lambda$lambda_0(this, $receiver));
    return $receiver;
  };
  function PbrDemo$pbrMenu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function PbrDemo$pbrMenu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(PbrDemo$pbrMenu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(PbrDemo$pbrMenu$lambda$lambda$lambda_0);
    return Unit;
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_1(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Environment';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda(this$PbrDemo) {
    return function ($receiver, f, f_0, f_1) {
      $receiver.text = this$PbrDemo.hdriCycler_0.next().name;
      this$PbrDemo.updateHdri_0(this$PbrDemo.hdriCycler_0.index);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_2(closure$y, this$PbrDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.text = this$PbrDemo.hdriCycler_0.current.name;
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda(this$PbrDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_0(this$PbrDemo, closure$envLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$envLabel.text = this$PbrDemo.hdriCycler_0.prev().name;
      this$PbrDemo.updateHdri_0(this$PbrDemo.hdriCycler_0.index);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_3(closure$y, this$PbrDemo, closure$envLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_0(this$PbrDemo, closure$envLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_1(this$PbrDemo, closure$envLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$envLabel.text = this$PbrDemo.hdriCycler_0.next().name;
      this$PbrDemo.updateHdri_0(this$PbrDemo.hdriCycler_0.index);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_4(closure$y, this$PbrDemo, closure$envLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_1(this$PbrDemo, closure$envLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_5(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Lighting';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_2(this$PbrDemo, this$) {
    return function ($receiver, f, f_0, f_1) {
      var $receiver_0 = this$PbrDemo.pbrContentCycler_0;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.setUseImageBasedLighting_6taknv$(this$.isEnabled);
      }
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_6(closure$y, this$PbrDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(8.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(84.0), dps(35.0), full());
      $receiver.text = 'Image Based';
      $receiver.isEnabled = true;
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_2(this$PbrDemo, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_3(this$PbrDemo) {
    return function ($receiver, f, f_0, f_1) {
      $receiver.text = this$PbrDemo.lightCycler_0.next().name;
      this$PbrDemo.lightCycler_0.current.setup(this$PbrDemo.contentScene_0);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_7(closure$y, this$PbrDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.text = this$PbrDemo.lightCycler_0.current.name;
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_3(this$PbrDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_4(this$PbrDemo, closure$lightLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$lightLabel.text = this$PbrDemo.lightCycler_0.prev().name;
      this$PbrDemo.lightCycler_0.current.setup(this$PbrDemo.contentScene_0);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_8(closure$y, this$PbrDemo, closure$lightLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_4(this$PbrDemo, closure$lightLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_5(this$PbrDemo, closure$lightLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$lightLabel.text = this$PbrDemo.lightCycler_0.next().name;
      this$PbrDemo.lightCycler_0.current.setup(this$PbrDemo.contentScene_0);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_9(closure$y, this$PbrDemo, closure$lightLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_5(this$PbrDemo, closure$lightLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_10(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Scene Content';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_6(this$PbrDemo) {
    return function ($receiver, f, f_0, f_1) {
      this$PbrDemo.pbrContentCycler_0.current.hide();
      this$PbrDemo.pbrContentCycler_0.next().show();
      $receiver.text = this$PbrDemo.pbrContentCycler_0.current.name;
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_11(closure$y, this$PbrDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.text = this$PbrDemo.pbrContentCycler_0.current.name;
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_6(this$PbrDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_7(this$PbrDemo, closure$contentLabel) {
    return function ($receiver, f, f_0, f_1) {
      this$PbrDemo.pbrContentCycler_0.current.hide();
      this$PbrDemo.pbrContentCycler_0.prev().show();
      closure$contentLabel.text = this$PbrDemo.pbrContentCycler_0.current.name;
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_12(closure$y, this$PbrDemo, closure$contentLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_7(this$PbrDemo, closure$contentLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_8(this$PbrDemo, closure$contentLabel) {
    return function ($receiver, f, f_0, f_1) {
      this$PbrDemo.pbrContentCycler_0.current.hide();
      this$PbrDemo.pbrContentCycler_0.next().show();
      closure$contentLabel.text = this$PbrDemo.pbrContentCycler_0.current.name;
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_13(closure$y, this$PbrDemo, closure$contentLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_8(this$PbrDemo, closure$contentLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda_0(closure$smallFont, this$, this$PbrDemo) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-370.0), dps(-480.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(250.0), dps(360.0), full());
      var y = {v: -35.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('env-lbl', PbrDemo$pbrMenu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var envLabel = this$.button_9zrh0o$('selected-env', PbrDemo$pbrMenu$lambda$lambda$lambda_2(y, this$PbrDemo));
      $receiver.unaryPlus_uv0sim$(envLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('env-left', PbrDemo$pbrMenu$lambda$lambda$lambda_3(y, this$PbrDemo, envLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('env-right', PbrDemo$pbrMenu$lambda$lambda$lambda_4(y, this$PbrDemo, envLabel)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('light-lbl', PbrDemo$pbrMenu$lambda$lambda$lambda_5(y, closure$smallFont, this$)));
      y.v -= 30.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('env-lighting', PbrDemo$pbrMenu$lambda$lambda$lambda_6(y, this$PbrDemo)));
      y.v -= 30.0;
      var lightLabel = this$.button_9zrh0o$('selected-light', PbrDemo$pbrMenu$lambda$lambda$lambda_7(y, this$PbrDemo));
      $receiver.unaryPlus_uv0sim$(lightLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('light-left', PbrDemo$pbrMenu$lambda$lambda$lambda_8(y, this$PbrDemo, lightLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('light-right', PbrDemo$pbrMenu$lambda$lambda$lambda_9(y, this$PbrDemo, lightLabel)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('content-lbl', PbrDemo$pbrMenu$lambda$lambda$lambda_10(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var contentLabel = this$.button_9zrh0o$('selected-content', PbrDemo$pbrMenu$lambda$lambda$lambda_11(y, this$PbrDemo));
      $receiver.unaryPlus_uv0sim$(contentLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('content-left', PbrDemo$pbrMenu$lambda$lambda$lambda_12(y, this$PbrDemo, contentLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('content-right', PbrDemo$pbrMenu$lambda$lambda$lambda_13(y, this$PbrDemo, contentLabel)));
      y.v -= 10.0;
      var $receiver_0 = this$PbrDemo.pbrContentCycler_0;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.createMenu_7fm8wr$($receiver, closure$smallFont, y.v);
      }
      this$PbrDemo.pbrContentCycler_0.current.show();
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda(this$PbrDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, this$PbrDemo.ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, PbrDemo$pbrMenu$lambda$lambda);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', PbrDemo$pbrMenu$lambda$lambda_0(smallFont, $receiver, this$PbrDemo)));
      return Unit;
    };
  }
  PbrDemo.prototype.pbrMenu_0 = function () {
    return uiScene(void 0, void 0, void 0, PbrDemo$pbrMenu$lambda(this));
  };
  function PbrDemo$updateHdri$lambda(this$PbrDemo) {
    return function (tex) {
      var tmp$, tmp$_0;
      if ((tmp$ = this$PbrDemo.irradianceMapPass_0) != null) {
        var this$PbrDemo_0 = this$PbrDemo;
        tmp$.hdriTexture = tex;
        tmp$.update_aemszp$(this$PbrDemo_0.ctx);
      }if ((tmp$_0 = this$PbrDemo.reflectionMapPass_0) != null) {
        var this$PbrDemo_1 = this$PbrDemo;
        tmp$_0.hdriTexture = tex;
        tmp$_0.update_aemszp$(this$PbrDemo_1.ctx);
      }return Unit;
    };
  }
  PbrDemo.prototype.updateHdri_0 = function (idx) {
    this.loadHdri_0(idx, PbrDemo$updateHdri$lambda(this));
  };
  function PbrDemo$loadHdri$lambda(this$PbrDemo, closure$idx, closure$recv) {
    return function (it) {
      this$PbrDemo.loadedHdris_0[closure$idx] = it;
      closure$recv(it);
      return Unit;
    };
  }
  PbrDemo.prototype.loadHdri_0 = function (idx, recv) {
    var tex = this.loadedHdris_0[idx];
    if (tex == null) {
      this.ctx.assetMgr.loadAndPrepareTexture_hd4f6p$(PbrDemo$Companion_getInstance().hdriTextures_0.get_za3lpa$(idx).hdriPath, PbrDemo$Companion_getInstance().hdriTexProps_0, PbrDemo$loadHdri$lambda(this, idx, recv));
    } else {
      recv(tex);
    }
  };
  function PbrDemo$EnvironmentMap(hdriPath, name) {
    this.hdriPath = hdriPath;
    this.name = name;
  }
  PbrDemo$EnvironmentMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EnvironmentMap',
    interfaces: []
  };
  function PbrDemo$LightSetup(name, setup) {
    this.name = name;
    this.setup = setup;
  }
  PbrDemo$LightSetup.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LightSetup',
    interfaces: []
  };
  function PbrDemo$PbrContent(name) {
    this.name = name;
    this.content = null;
    this.ui = null;
  }
  PbrDemo$PbrContent.prototype.show = function () {
    var tmp$, tmp$_0;
    (tmp$ = this.content) != null ? (tmp$.isVisible = true) : null;
    (tmp$_0 = this.ui) != null ? (tmp$_0.isVisible = true) : null;
  };
  PbrDemo$PbrContent.prototype.hide = function () {
    var tmp$, tmp$_0;
    (tmp$ = this.content) != null ? (tmp$.isVisible = false) : null;
    (tmp$_0 = this.ui) != null ? (tmp$_0.isVisible = false) : null;
  };
  PbrDemo$PbrContent.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PbrContent',
    interfaces: []
  };
  function PbrDemo$Companion() {
    PbrDemo$Companion_instance = this;
    this.hdriTexProps_0 = new TextureProps(void 0, void 0, void 0, void 0, FilterMethod.NEAREST, FilterMethod.NEAREST, true);
    var $this = Demo$Companion_getInstance();
    var key = 'pbrDemo.envMaps';
    var default_0 = 'https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/hdri';
    var tmp$, tmp$_0;
    this.hdriPath_0 = (tmp$_0 = typeof (tmp$ = $this.demoProps.get_11rb$(key)) === 'string' ? tmp$ : null) != null ? tmp$_0 : default_0;
    this.hdriTextures_0 = listOf_0([new PbrDemo$EnvironmentMap(this.hdriPath_0 + '/circus_arena_1k.rgbe.png', 'Circus'), new PbrDemo$EnvironmentMap(this.hdriPath_0 + '/newport_loft.rgbe.png', 'Loft'), new PbrDemo$EnvironmentMap(this.hdriPath_0 + '/spruit_sunrise_1k.rgbe.png', 'Sunrise'), new PbrDemo$EnvironmentMap(this.hdriPath_0 + '/shanghai_bund_1k.rgbe.png', 'Shanghai')]);
    this.lightStrength_0 = 250.0;
    this.lightExtent_0 = 10.0;
    this.lightSetups_0 = listOf_0([new PbrDemo$LightSetup('Off', PbrDemo$Companion$lightSetups$lambda), new PbrDemo$LightSetup('Front x1', PbrDemo$Companion$lightSetups$lambda_0(this)), new PbrDemo$LightSetup('Front x4', PbrDemo$Companion$lightSetups$lambda_1(this)), new PbrDemo$LightSetup('Top x1', PbrDemo$Companion$lightSetups$lambda_2(this)), new PbrDemo$LightSetup('Top x4', PbrDemo$Companion$lightSetups$lambda_3(this))]);
  }
  function PbrDemo$Companion$lightSetups$lambda($receiver) {
    $receiver.lighting.lights.clear();
    return Unit;
  }
  function PbrDemo$Companion$lightSetups$lambda_0(this$PbrDemo$) {
    return function ($receiver) {
      var light1 = (new Light()).setPoint_czzhiu$(new Vec3f(0.0, 0.0, this$PbrDemo$.lightExtent_0 * 1.5)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0 * 2.0);
      $receiver.lighting.lights.clear();
      $receiver.lighting.lights.add_11rb$(light1);
      return Unit;
    };
  }
  function PbrDemo$Companion$lightSetups$lambda_1(this$PbrDemo$) {
    return function ($receiver) {
      var light1 = (new Light()).setPoint_czzhiu$(new Vec3f(this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      var light2 = (new Light()).setPoint_czzhiu$(new Vec3f(-this$PbrDemo$.lightExtent_0, -this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      var light3 = (new Light()).setPoint_czzhiu$(new Vec3f(-this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      var light4 = (new Light()).setPoint_czzhiu$(new Vec3f(this$PbrDemo$.lightExtent_0, -this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      $receiver.lighting.lights.clear();
      $receiver.lighting.lights.add_11rb$(light1);
      $receiver.lighting.lights.add_11rb$(light2);
      $receiver.lighting.lights.add_11rb$(light3);
      $receiver.lighting.lights.add_11rb$(light4);
      return Unit;
    };
  }
  function PbrDemo$Companion$lightSetups$lambda_2(this$PbrDemo$) {
    return function ($receiver) {
      var light1 = (new Light()).setPoint_czzhiu$(new Vec3f(0.0, this$PbrDemo$.lightExtent_0 * 1.5, 0.0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0 * 2.0);
      $receiver.lighting.lights.clear();
      $receiver.lighting.lights.add_11rb$(light1);
      return Unit;
    };
  }
  function PbrDemo$Companion$lightSetups$lambda_3(this$PbrDemo$) {
    return function ($receiver) {
      var light1 = (new Light()).setPoint_czzhiu$(new Vec3f(this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      var light2 = (new Light()).setPoint_czzhiu$(new Vec3f(-this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0, -this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      var light3 = (new Light()).setPoint_czzhiu$(new Vec3f(-this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      var light4 = (new Light()).setPoint_czzhiu$(new Vec3f(this$PbrDemo$.lightExtent_0, this$PbrDemo$.lightExtent_0, -this$PbrDemo$.lightExtent_0)).setColor_y83vuj$(Color.Companion.WHITE, this$PbrDemo$.lightStrength_0);
      $receiver.lighting.lights.clear();
      $receiver.lighting.lights.add_11rb$(light1);
      $receiver.lighting.lights.add_11rb$(light2);
      $receiver.lighting.lights.add_11rb$(light3);
      $receiver.lighting.lights.add_11rb$(light4);
      return Unit;
    };
  }
  PbrDemo$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PbrDemo$Companion_instance = null;
  function PbrDemo$Companion_getInstance() {
    if (PbrDemo$Companion_instance === null) {
      new PbrDemo$Companion();
    }return PbrDemo$Companion_instance;
  }
  function PbrDemo_init$lambda(it) {
    return it.isPressed;
  }
  function PbrDemo_init$lambda_0(this$PbrDemo) {
    return function (it) {
      this$PbrDemo.hdriCycler_0.next();
      this$PbrDemo.updateHdri_0(this$PbrDemo.hdriCycler_0.index);
      return Unit;
    };
  }
  function PbrDemo_init$lambda_1(it) {
    return it.isPressed;
  }
  function PbrDemo_init$lambda_2(this$PbrDemo) {
    return function (it) {
      this$PbrDemo.hdriCycler_0.prev();
      this$PbrDemo.updateHdri_0(this$PbrDemo.hdriCycler_0.index);
      return Unit;
    };
  }
  function PbrDemo_init$lambda_3(this$PbrDemo, closure$nextHdriKeyListener, closure$prevHdriKeyListener) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1;
      this$PbrDemo.ctx.inputMgr.removeKeyListener_abhb69$(closure$nextHdriKeyListener);
      this$PbrDemo.ctx.inputMgr.removeKeyListener_abhb69$(closure$prevHdriKeyListener);
      var $receiver_0 = this$PbrDemo.loadedHdris_0;
      var tmp$_2;
      for (tmp$_2 = 0; tmp$_2 !== $receiver_0.length; ++tmp$_2) {
        var element = $receiver_0[tmp$_2];
        element != null ? (element.dispose(), Unit) : null;
      }
      (tmp$ = this$PbrDemo.irradianceMapPass_0) != null ? (tmp$.dispose_aemszp$(this$PbrDemo.ctx), Unit) : null;
      (tmp$_0 = this$PbrDemo.reflectionMapPass_0) != null ? (tmp$_0.dispose_aemszp$(this$PbrDemo.ctx), Unit) : null;
      (tmp$_1 = this$PbrDemo.brdfLut_0) != null ? (tmp$_1.dispose_aemszp$(this$PbrDemo.ctx), Unit) : null;
      return Unit;
    };
  }
  PbrDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PbrDemo',
    interfaces: []
  };
  function PbrMaterialContent() {
    PbrMaterialContent$Companion_getInstance();
    PbrDemo$PbrContent.call(this, 'PBR Material');
    var $receiver = new Cycler(PbrMaterialContent$Companion_getInstance().materials_0);
    $receiver.index = 3;
    this.matCycler = $receiver;
    this.shaders_0 = ArrayList_init();
    this.iblContent_0 = null;
    this.nonIblContent_0 = null;
  }
  Object.defineProperty(PbrMaterialContent.prototype, 'currentMat', {
    get: function () {
      return this.matCycler.current;
    }
  });
  PbrMaterialContent.prototype.nextMaterial = function () {
    this.currentMat.disposeMaps();
    this.matCycler.next();
    this.updatePbrMaterial_0();
    return this.currentMat;
  };
  PbrMaterialContent.prototype.prevMaterial = function () {
    this.currentMat.disposeMaps();
    this.matCycler.prev();
    this.updatePbrMaterial_0();
    return this.currentMat;
  };
  PbrMaterialContent.prototype.updatePbrMaterial_0 = function () {
    var tmp$;
    tmp$ = this.shaders_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0, tmp$_1, tmp$_2;
      element.albedoMap = this.currentMat.albedo;
      element.normalMap = this.currentMat.normal;
      element.roughnessMap = this.currentMat.roughness;
      element.metallicMap = (tmp$_0 = this.currentMat.metallic) != null ? tmp$_0 : PbrMaterialContent$Companion_getInstance().defaultMetallicTex_0;
      element.ambientOcclusionMap = (tmp$_1 = this.currentMat.ao) != null ? tmp$_1 : PbrMaterialContent$Companion_getInstance().defaultAoTex_0;
      element.displacementMap = (tmp$_2 = this.currentMat.displacement) != null ? tmp$_2 : PbrMaterialContent$Companion_getInstance().defaultDispTex_0;
    }
  };
  function PbrMaterialContent$createMenu$lambda$lambda$lambda(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Material';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda$lambda$lambda(this$PbrMaterialContent) {
    return function ($receiver, f, f_0, f_1) {
      $receiver.text = this$PbrMaterialContent.nextMaterial().name;
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda$lambda_0(closure$y, this$PbrMaterialContent) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.text = this$PbrMaterialContent.currentMat.name;
      var $receiver_0 = $receiver.onClick;
      var element = PbrMaterialContent$createMenu$lambda$lambda$lambda$lambda(this$PbrMaterialContent);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda$lambda$lambda_0(this$PbrMaterialContent, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$matLabel.text = this$PbrMaterialContent.prevMaterial().name;
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda$lambda_1(closure$y, this$PbrMaterialContent, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = PbrMaterialContent$createMenu$lambda$lambda$lambda$lambda_0(this$PbrMaterialContent, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda$lambda$lambda_1(this$PbrMaterialContent, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$matLabel.text = this$PbrMaterialContent.nextMaterial().name;
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda$lambda_2(closure$y, this$PbrMaterialContent, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = PbrMaterialContent$createMenu$lambda$lambda$lambda$lambda_1(this$PbrMaterialContent, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrMaterialContent$createMenu$lambda$lambda(closure$yPos, closure$smallFont, this$, this$PbrMaterialContent) {
    return function ($receiver) {
      $receiver.isVisible = false;
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$yPos - 60.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(60.0), full());
      var y = {v: -30.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('mat-lbl', PbrMaterialContent$createMenu$lambda$lambda$lambda(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var matLabel = this$.button_9zrh0o$('selected-mat', PbrMaterialContent$createMenu$lambda$lambda$lambda_0(y, this$PbrMaterialContent));
      $receiver.unaryPlus_uv0sim$(matLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('mat-left', PbrMaterialContent$createMenu$lambda$lambda$lambda_1(y, this$PbrMaterialContent, matLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('mat-right', PbrMaterialContent$createMenu$lambda$lambda$lambda_2(y, this$PbrMaterialContent, matLabel)));
      return Unit;
    };
  }
  PbrMaterialContent.prototype.createMenu_7fm8wr$ = function (parent, smallFont, yPos) {
    var $receiver = parent.root;
    this.ui = $receiver.container_t34sov$('pbr-mat-container', PbrMaterialContent$createMenu$lambda$lambda(yPos, smallFont, $receiver, this));
    parent.plusAssign_f1kmr1$(ensureNotNull(this.ui));
  };
  PbrMaterialContent.prototype.setUseImageBasedLighting_6taknv$ = function (enabled) {
    var tmp$, tmp$_0;
    (tmp$ = this.iblContent_0) != null ? (tmp$.isVisible = enabled) : null;
    (tmp$_0 = this.nonIblContent_0) != null ? (tmp$_0.isVisible = !enabled) : null;
  };
  function PbrMaterialContent$createContent$lambda$lambda(this$) {
    return function ($receiver, ctx) {
      this$.rotate_ad55pp$(-2.0 * ctx.deltaT, Vec3f.Companion.Y_AXIS);
      return Unit;
    };
  }
  function PbrMaterialContent$createContent$lambda(closure$scene, closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$PbrMaterialContent) {
    return function ($receiver) {
      $receiver.isVisible = false;
      var ibl = this$PbrMaterialContent.makeSphere_0(true, closure$scene, closure$irradianceMap, closure$reflectionMap, closure$brdfLut);
      var $receiver_0 = this$PbrMaterialContent.makeSphere_0(false, closure$scene, closure$irradianceMap, closure$reflectionMap, closure$brdfLut);
      $receiver_0.isVisible = false;
      var nonIbl = $receiver_0;
      $receiver.unaryPlus_uv0sim$(ibl);
      $receiver.unaryPlus_uv0sim$(nonIbl);
      this$PbrMaterialContent.iblContent_0 = ibl;
      this$PbrMaterialContent.nonIblContent_0 = nonIbl;
      $receiver.onPreRender.add_11rb$(PbrMaterialContent$createContent$lambda$lambda($receiver));
      return Unit;
    };
  }
  PbrMaterialContent.prototype.createContent_wb8610$ = function (scene, irradianceMap, reflectionMap, brdfLut, ctx) {
    this.content = transformGroup(void 0, PbrMaterialContent$createContent$lambda(scene, irradianceMap, reflectionMap, brdfLut, this));
    return ensureNotNull(this.content);
  };
  function PbrMaterialContent$makeSphere$lambda$lambda$lambda$lambda($receiver) {
    $receiver.texCoord.x = $receiver.texCoord.x * 4;
    $receiver.texCoord.y = $receiver.texCoord.y * 2;
    return Unit;
  }
  function PbrMaterialContent$makeSphere$lambda$lambda$lambda($receiver) {
    $receiver.vertexModFun = PbrMaterialContent$makeSphere$lambda$lambda$lambda$lambda;
    var $receiver_0 = $receiver.sphereProps.defaults();
    $receiver_0.steps = 700;
    $receiver_0.radius = 7.0;
    $receiver.sphere_mojs8w$($receiver.sphereProps);
    return Unit;
  }
  function PbrMaterialContent$makeSphere$lambda$lambda$lambda_0(this$PbrMaterialContent) {
    return function ($receiver, it) {
      var tmp$;
      tmp$ = this$PbrMaterialContent.matCycler.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.disposeMaps();
      }
      return Unit;
    };
  }
  function PbrMaterialContent$makeSphere$lambda$lambda(closure$withIbl, closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$PbrMaterialContent, closure$scene) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(PbrMaterialContent$makeSphere$lambda$lambda$lambda);
      var pbrConfig = new PbrShader$PbrConfig();
      pbrConfig.isImageBasedLighting = closure$withIbl;
      pbrConfig.albedoSource = Albedo.TEXTURE_ALBEDO;
      pbrConfig.isNormalMapped = true;
      pbrConfig.isRoughnessMapped = true;
      pbrConfig.isMetallicMapped = true;
      pbrConfig.isAmbientOcclusionMapped = true;
      pbrConfig.isDisplacementMapped = true;
      var $receiver_0 = new PbrShader(pbrConfig);
      var closure$irradianceMap_0 = closure$irradianceMap;
      var closure$reflectionMap_0 = closure$reflectionMap;
      var closure$brdfLut_0 = closure$brdfLut;
      $receiver_0.irradianceMap = closure$irradianceMap_0;
      $receiver_0.reflectionMap = closure$reflectionMap_0;
      $receiver_0.brdfLut = closure$brdfLut_0;
      $receiver_0.displacementStrength = 0.25;
      var shader = $receiver_0;
      $receiver.pipelineLoader = shader;
      this$PbrMaterialContent.shaders_0.add_11rb$(shader);
      this$PbrMaterialContent.updatePbrMaterial_0();
      var $receiver_1 = closure$scene.onDispose;
      var element = PbrMaterialContent$makeSphere$lambda$lambda$lambda_0(this$PbrMaterialContent);
      $receiver_1.add_11rb$(element);
      return Unit;
    };
  }
  function PbrMaterialContent$makeSphere$lambda(closure$withIbl, closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$PbrMaterialContent, closure$scene) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(textureMesh(void 0, true, PbrMaterialContent$makeSphere$lambda$lambda(closure$withIbl, closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$PbrMaterialContent, closure$scene)));
      return Unit;
    };
  }
  PbrMaterialContent.prototype.makeSphere_0 = function (withIbl, scene, irradianceMap, reflectionMap, brdfLut) {
    return group(void 0, PbrMaterialContent$makeSphere$lambda(withIbl, irradianceMap, reflectionMap, brdfLut, this, scene));
  };
  function PbrMaterialContent$MaterialMaps(name, albedo, normal, roughness, metallic, ao, displacement) {
    this.name = name;
    this.albedo = albedo;
    this.normal = normal;
    this.roughness = roughness;
    this.metallic = metallic;
    this.ao = ao;
    this.displacement = displacement;
  }
  PbrMaterialContent$MaterialMaps.prototype.disposeMaps = function () {
    var tmp$, tmp$_0, tmp$_1;
    this.albedo.dispose();
    this.normal.dispose();
    this.roughness.dispose();
    (tmp$ = this.metallic) != null ? (tmp$.dispose(), Unit) : null;
    (tmp$_0 = this.ao) != null ? (tmp$_0.dispose(), Unit) : null;
    (tmp$_1 = this.displacement) != null ? (tmp$_1.dispose(), Unit) : null;
  };
  PbrMaterialContent$MaterialMaps.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MaterialMaps',
    interfaces: []
  };
  PbrMaterialContent$MaterialMaps.prototype.component1 = function () {
    return this.name;
  };
  PbrMaterialContent$MaterialMaps.prototype.component2 = function () {
    return this.albedo;
  };
  PbrMaterialContent$MaterialMaps.prototype.component3 = function () {
    return this.normal;
  };
  PbrMaterialContent$MaterialMaps.prototype.component4 = function () {
    return this.roughness;
  };
  PbrMaterialContent$MaterialMaps.prototype.component5 = function () {
    return this.metallic;
  };
  PbrMaterialContent$MaterialMaps.prototype.component6 = function () {
    return this.ao;
  };
  PbrMaterialContent$MaterialMaps.prototype.component7 = function () {
    return this.displacement;
  };
  PbrMaterialContent$MaterialMaps.prototype.copy_chv25t$ = function (name, albedo, normal, roughness, metallic, ao, displacement) {
    return new PbrMaterialContent$MaterialMaps(name === void 0 ? this.name : name, albedo === void 0 ? this.albedo : albedo, normal === void 0 ? this.normal : normal, roughness === void 0 ? this.roughness : roughness, metallic === void 0 ? this.metallic : metallic, ao === void 0 ? this.ao : ao, displacement === void 0 ? this.displacement : displacement);
  };
  PbrMaterialContent$MaterialMaps.prototype.toString = function () {
    return 'MaterialMaps(name=' + Kotlin.toString(this.name) + (', albedo=' + Kotlin.toString(this.albedo)) + (', normal=' + Kotlin.toString(this.normal)) + (', roughness=' + Kotlin.toString(this.roughness)) + (', metallic=' + Kotlin.toString(this.metallic)) + (', ao=' + Kotlin.toString(this.ao)) + (', displacement=' + Kotlin.toString(this.displacement)) + ')';
  };
  PbrMaterialContent$MaterialMaps.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.albedo) | 0;
    result = result * 31 + Kotlin.hashCode(this.normal) | 0;
    result = result * 31 + Kotlin.hashCode(this.roughness) | 0;
    result = result * 31 + Kotlin.hashCode(this.metallic) | 0;
    result = result * 31 + Kotlin.hashCode(this.ao) | 0;
    result = result * 31 + Kotlin.hashCode(this.displacement) | 0;
    return result;
  };
  PbrMaterialContent$MaterialMaps.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.albedo, other.albedo) && Kotlin.equals(this.normal, other.normal) && Kotlin.equals(this.roughness, other.roughness) && Kotlin.equals(this.metallic, other.metallic) && Kotlin.equals(this.ao, other.ao) && Kotlin.equals(this.displacement, other.displacement)))));
  };
  function PbrMaterialContent$Companion() {
    PbrMaterialContent$Companion_instance = this;
    this.defaultMetallicTex_0 = new SingleColorTexture(Color.Companion.BLACK);
    this.defaultAoTex_0 = new SingleColorTexture(Color.Companion.WHITE);
    this.defaultDispTex_0 = new SingleColorTexture(Color.Companion.BLACK);
    this.assetPath_0 = Demo$Companion_getInstance().pbrBasePath;
    this.materials_0 = mutableListOf([new PbrMaterialContent$MaterialMaps('Bamboo', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_0(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_1(this)), null, new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_2(this)), null), new PbrMaterialContent$MaterialMaps('Castle Brick', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_3(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_4(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_5(this)), null, new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_6(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_7(this))), new PbrMaterialContent$MaterialMaps('Granite', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_8(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_9(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_10(this)), null, null, null), new PbrMaterialContent$MaterialMaps('Weave Steel', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_11(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_12(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_13(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_14(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_15(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_16(this))), new PbrMaterialContent$MaterialMaps('Scuffed Plastic', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_17(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_18(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_19(this)), null, new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_20(this)), null), new PbrMaterialContent$MaterialMaps('Snow Covered Path', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_21(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_22(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_23(this)), null, new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_24(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_25(this))), new PbrMaterialContent$MaterialMaps('Marble', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_26(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_27(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_28(this)), null, null, null), new PbrMaterialContent$MaterialMaps('Onyx Tiles', new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_29(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_30(this)), new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_31(this)), null, null, new Texture(void 0, PbrMaterialContent$Companion$materials$lambda_32(this)))]);
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda;
  Coroutine$PbrMaterialContent$Companion$materials$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_0(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_0.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_0;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_0(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_0(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_1(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_1.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_1;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_1(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_1(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_2(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_2.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_2;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_2(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_2(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_3(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_3.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_3.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_3.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_3;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_3.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/castle_brick/castle_brick_02_red_diff_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_3(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_3(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_4(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_4.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_4.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_4.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_4;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_4.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/castle_brick/castle_brick_02_red_nor_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_4(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_4(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_5(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_5.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_5.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_5.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_5;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_5.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/castle_brick/castle_brick_02_red_rough_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_5(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_5(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_6(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_6.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_6.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_6.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_6;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_6.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/castle_brick/castle_brick_02_red_ao_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_6(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_6(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_7(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_7.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_7.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_7.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_7;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_7.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/castle_brick/castle_brick_02_red_disp_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_7(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_7(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_8(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_8.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_8.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_8.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_8;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_8.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/granitesmooth1/granitesmooth1-albedo4.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_8(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_8(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_9(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_9.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_9.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_9.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_9;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_9.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/granitesmooth1/granitesmooth1-normal2.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_9(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_9(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_10(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_10.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_10.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_10.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_10;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_10.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/granitesmooth1/granitesmooth1-roughness3.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_10(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_10(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_11(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_11.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_11.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_11.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_11;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_11.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_11(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_11(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_12(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_12.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_12.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_12.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_12;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_12.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_12(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_12(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_13(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_13.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_13.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_13.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_13;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_13.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_13(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_13(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_14(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_14.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_14.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_14.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_14;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_14.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_14(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_14(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_15(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_15.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_15.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_15.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_15;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_15.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_15(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_15(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_16(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_16.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_16.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_16.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_16;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_16.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_DISP_2K_METALNESS.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_16(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_16(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_17(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_17.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_17.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_17.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_17;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_17.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/scuffed-plastic-1/scuffed-plastic4-alb.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_17(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_17(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_18(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_18.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_18.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_18.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_18;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_18.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/scuffed-plastic-1/scuffed-plastic-normal.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_18(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_18(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_19(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_19.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_19.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_19.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_19;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_19.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/scuffed-plastic-1/scuffed-plastic-rough.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_19(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_19(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_20(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_20.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_20.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_20.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_20;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_20.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/scuffed-plastic-1/scuffed-plastic-ao.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_20(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_20(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_21(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_21.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_21.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_21.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_21;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_21.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/snowcoveredpath/snowcoveredpath_albedo.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_21(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_21(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_22(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_22.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_22.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_22.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_22;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_22.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/snowcoveredpath/snowcoveredpath_normal-dx.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_22(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_22(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_23(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_23.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_23.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_23.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_23;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_23.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/snowcoveredpath/snowcoveredpath_roughness.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_23(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_23(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_24(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_24.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_24.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_24.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_24;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_24.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/snowcoveredpath/snowcoveredpath_ao.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_24(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_24(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_25(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_25.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_25.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_25.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_25;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_25.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/snowcoveredpath/snowcoveredpath_height.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_25(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_25(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_26(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_26.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_26.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_26.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_26;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_26.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/streaked-marble/streaked-marble-albedo2.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_26(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_26(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_27(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_27.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_27.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_27.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_27;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_27.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/streaked-marble/streaked-marble-normal.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_27(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_27(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_28(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_28.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_28.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_28.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_28;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_28.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/streaked-marble/streaked-marble-roughness1.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_28(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_28(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_29(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_29.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_29.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_29.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_29;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_29.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_COL_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_29(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_29(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_30(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_30.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_30.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_30.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_30;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_30.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_NRM_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_30(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_30(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_31(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_31.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_31.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_31.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_31;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_31.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_REFL_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_31(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_31(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$PbrMaterialContent$Companion$materials$lambda_32(this$PbrMaterialContent$_0, $receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$PbrMaterialContent$ = this$PbrMaterialContent$_0;
    this.local$it = it_0;
  }
  Coroutine$PbrMaterialContent$Companion$materials$lambda_32.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$PbrMaterialContent$Companion$materials$lambda_32.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$PbrMaterialContent$Companion$materials$lambda_32.prototype.constructor = Coroutine$PbrMaterialContent$Companion$materials$lambda_32;
  Coroutine$PbrMaterialContent$Companion$materials$lambda_32.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(this.local$this$PbrMaterialContent$.assetPath_0 + '/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_DISP_2K.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function PbrMaterialContent$Companion$materials$lambda_32(this$PbrMaterialContent$_0) {
    return function ($receiver_0, it_0, continuation_0, suspended) {
      var instance = new Coroutine$PbrMaterialContent$Companion$materials$lambda_32(this$PbrMaterialContent$_0, $receiver_0, it_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  PbrMaterialContent$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PbrMaterialContent$Companion_instance = null;
  function PbrMaterialContent$Companion_getInstance() {
    if (PbrMaterialContent$Companion_instance === null) {
      new PbrMaterialContent$Companion();
    }return PbrMaterialContent$Companion_instance;
  }
  PbrMaterialContent.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PbrMaterialContent',
    interfaces: [PbrDemo$PbrContent]
  };
  function RoughnesMetalGridContent() {
    RoughnesMetalGridContent$Companion_getInstance();
    PbrDemo$PbrContent.call(this, 'Roughness / Metal');
    this.colors_0 = new Cycler(RoughnesMetalGridContent$Companion_getInstance().matColors_0);
    this.shaders_0 = ArrayList_init();
    this.iblContent_0 = null;
    this.nonIblContent_0 = null;
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Color';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda$lambda(this$RoughnesMetalGridContent) {
    return function ($receiver, f, f_0, f_1) {
      $receiver.text = this$RoughnesMetalGridContent.colors_0.next().name;
      var $receiver_0 = this$RoughnesMetalGridContent.shaders_0;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.albedo = this$RoughnesMetalGridContent.colors_0.current.linColor;
      }
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda_0(closure$y, this$RoughnesMetalGridContent) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.text = this$RoughnesMetalGridContent.colors_0.current.name;
      var $receiver_0 = $receiver.onClick;
      var element = RoughnesMetalGridContent$createMenu$lambda$lambda$lambda$lambda(this$RoughnesMetalGridContent);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda$lambda_0(this$RoughnesMetalGridContent, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$matLabel.text = this$RoughnesMetalGridContent.colors_0.prev().name;
      var $receiver_0 = this$RoughnesMetalGridContent.shaders_0;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.albedo = this$RoughnesMetalGridContent.colors_0.current.linColor;
      }
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda_1(closure$y, this$RoughnesMetalGridContent, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = RoughnesMetalGridContent$createMenu$lambda$lambda$lambda$lambda_0(this$RoughnesMetalGridContent, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda$lambda_1(this$RoughnesMetalGridContent, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$matLabel.text = this$RoughnesMetalGridContent.colors_0.next().name;
      var $receiver_0 = this$RoughnesMetalGridContent.shaders_0;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.albedo = this$RoughnesMetalGridContent.colors_0.current.linColor;
      }
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda$lambda_2(closure$y, this$RoughnesMetalGridContent, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = RoughnesMetalGridContent$createMenu$lambda$lambda$lambda$lambda_1(this$RoughnesMetalGridContent, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function RoughnesMetalGridContent$createMenu$lambda$lambda(closure$yPos, closure$smallFont, this$, this$RoughnesMetalGridContent) {
    return function ($receiver) {
      $receiver.isVisible = false;
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$yPos - 60.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(60.0), full());
      var y = {v: -30.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('color-lbl', RoughnesMetalGridContent$createMenu$lambda$lambda$lambda(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var matLabel = this$.button_9zrh0o$('selected-color', RoughnesMetalGridContent$createMenu$lambda$lambda$lambda_0(y, this$RoughnesMetalGridContent));
      $receiver.unaryPlus_uv0sim$(matLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('color-left', RoughnesMetalGridContent$createMenu$lambda$lambda$lambda_1(y, this$RoughnesMetalGridContent, matLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('color-right', RoughnesMetalGridContent$createMenu$lambda$lambda$lambda_2(y, this$RoughnesMetalGridContent, matLabel)));
      return Unit;
    };
  }
  RoughnesMetalGridContent.prototype.createMenu_7fm8wr$ = function (parent, smallFont, yPos) {
    var $receiver = parent.root;
    this.ui = $receiver.container_t34sov$('pbr-rough-metal-container', RoughnesMetalGridContent$createMenu$lambda$lambda(yPos, smallFont, $receiver, this));
    parent.plusAssign_f1kmr1$(ensureNotNull(this.ui));
  };
  RoughnesMetalGridContent.prototype.setUseImageBasedLighting_6taknv$ = function (enabled) {
    var tmp$, tmp$_0;
    (tmp$ = this.iblContent_0) != null ? (tmp$.isVisible = enabled) : null;
    (tmp$_0 = this.nonIblContent_0) != null ? (tmp$_0.isVisible = !enabled) : null;
  };
  function RoughnesMetalGridContent$createContent$lambda(closure$irradianceMap, closure$reflectionMap, closure$brdfLut, this$RoughnesMetalGridContent) {
    return function ($receiver) {
      $receiver.isVisible = false;
      var ibl = this$RoughnesMetalGridContent.makeSpheres_0(true, closure$irradianceMap, closure$reflectionMap, closure$brdfLut);
      var $receiver_0 = this$RoughnesMetalGridContent.makeSpheres_0(false, closure$irradianceMap, closure$reflectionMap, closure$brdfLut);
      $receiver_0.isVisible = false;
      var nonIbl = $receiver_0;
      $receiver.unaryPlus_uv0sim$(ibl);
      $receiver.unaryPlus_uv0sim$(nonIbl);
      this$RoughnesMetalGridContent.iblContent_0 = ibl;
      this$RoughnesMetalGridContent.nonIblContent_0 = nonIbl;
      return Unit;
    };
  }
  RoughnesMetalGridContent.prototype.createContent_wb8610$ = function (scene, irradianceMap, reflectionMap, brdfLut, ctx) {
    this.content = transformGroup(void 0, RoughnesMetalGridContent$createContent$lambda(irradianceMap, reflectionMap, brdfLut, this));
    return ensureNotNull(this.content);
  };
  function RoughnesMetalGridContent$makeSpheres$lambda$lambda$lambda(closure$nCols, closure$x, closure$spacing, closure$nRows, closure$y) {
    return function ($receiver) {
      var closure$nCols_0 = closure$nCols;
      var closure$x_0 = closure$x;
      var closure$spacing_0 = closure$spacing;
      var closure$nRows_0 = closure$nRows;
      var closure$y_0 = closure$y;
      var $receiver_0 = $receiver.sphereProps.defaults();
      $receiver_0.steps = 100;
      $receiver_0.center.set_y2kzbl$(((-(closure$nCols_0 - 1 | 0) | 0) * 0.5 + closure$x_0) * closure$spacing_0, ((-(closure$nRows_0 - 1 | 0) | 0) * 0.5 + closure$y_0) * closure$spacing_0, 0.0);
      $receiver_0.radius = 1.0;
      $receiver.sphere_mojs8w$($receiver.sphereProps);
      return Unit;
    };
  }
  function RoughnesMetalGridContent$makeSpheres$lambda(closure$withIbl, this$RoughnesMetalGridContent, closure$irradianceMap, closure$reflectionMap, closure$brdfLut) {
    return function ($receiver) {
      var nRows = 7;
      var nCols = 7;
      var spacing = 2.5;
      for (var y = 0; y < nRows; y++) {
        for (var x = 0; x < nCols; x++) {
          var attributes = listOf_0([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]);
          var mesh = new Mesh_init(new IndexedVertexList_init(attributes), null);
          var closure$withIbl_0 = closure$withIbl;
          var this$RoughnesMetalGridContent_0 = this$RoughnesMetalGridContent;
          var closure$irradianceMap_0 = closure$irradianceMap;
          var closure$reflectionMap_0 = closure$reflectionMap;
          var closure$brdfLut_0 = closure$brdfLut;
          mesh.generate_v2sixm$(RoughnesMetalGridContent$makeSpheres$lambda$lambda$lambda(nCols, x, spacing, nRows, y));
          var pbrConfig = new PbrShader$PbrConfig();
          pbrConfig.albedoSource = Albedo.STATIC_ALBEDO;
          pbrConfig.isImageBasedLighting = closure$withIbl_0;
          var shader = new PbrShader(pbrConfig);
          shader.albedo = this$RoughnesMetalGridContent_0.colors_0.current.linColor;
          shader.irradianceMap = closure$irradianceMap_0;
          shader.reflectionMap = closure$reflectionMap_0;
          shader.brdfLut = closure$brdfLut_0;
          var a = x / (nCols - 1 | 0);
          shader.roughness = Math_0.max(a, 0.05);
          shader.metallic = y / (nRows - 1 | 0);
          mesh.pipelineLoader = shader;
          this$RoughnesMetalGridContent_0.shaders_0.add_11rb$(shader);
          $receiver.unaryPlus_uv0sim$(mesh);
        }
      }
      return Unit;
    };
  }
  RoughnesMetalGridContent.prototype.makeSpheres_0 = function (withIbl, irradianceMap, reflectionMap, brdfLut) {
    return group(void 0, RoughnesMetalGridContent$makeSpheres$lambda(withIbl, this, irradianceMap, reflectionMap, brdfLut));
  };
  function RoughnesMetalGridContent$MatColor(name, linColor) {
    this.name = name;
    this.linColor = linColor;
  }
  RoughnesMetalGridContent$MatColor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MatColor',
    interfaces: []
  };
  RoughnesMetalGridContent$MatColor.prototype.component1 = function () {
    return this.name;
  };
  RoughnesMetalGridContent$MatColor.prototype.component2 = function () {
    return this.linColor;
  };
  RoughnesMetalGridContent$MatColor.prototype.copy_guvy9a$ = function (name, linColor) {
    return new RoughnesMetalGridContent$MatColor(name === void 0 ? this.name : name, linColor === void 0 ? this.linColor : linColor);
  };
  RoughnesMetalGridContent$MatColor.prototype.toString = function () {
    return 'MatColor(name=' + Kotlin.toString(this.name) + (', linColor=' + Kotlin.toString(this.linColor)) + ')';
  };
  RoughnesMetalGridContent$MatColor.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.linColor) | 0;
    return result;
  };
  RoughnesMetalGridContent$MatColor.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.linColor, other.linColor)))));
  };
  function RoughnesMetalGridContent$Companion() {
    RoughnesMetalGridContent$Companion_instance = this;
    this.matColors_0 = listOf_0([new RoughnesMetalGridContent$MatColor('Red', Color.Companion.MD_RED.toLinear()), new RoughnesMetalGridContent$MatColor('Pink', Color.Companion.MD_PINK.toLinear()), new RoughnesMetalGridContent$MatColor('Purple', Color.Companion.MD_PURPLE.toLinear()), new RoughnesMetalGridContent$MatColor('Deep Purple', Color.Companion.MD_DEEP_PURPLE.toLinear()), new RoughnesMetalGridContent$MatColor('Indigo', Color.Companion.MD_INDIGO.toLinear()), new RoughnesMetalGridContent$MatColor('Blue', Color.Companion.MD_BLUE.toLinear()), new RoughnesMetalGridContent$MatColor('Cyan', Color.Companion.MD_CYAN.toLinear()), new RoughnesMetalGridContent$MatColor('Teal', Color.Companion.MD_TEAL.toLinear()), new RoughnesMetalGridContent$MatColor('Green', Color.Companion.MD_GREEN.toLinear()), new RoughnesMetalGridContent$MatColor('Light Green', Color.Companion.MD_LIGHT_GREEN.toLinear()), new RoughnesMetalGridContent$MatColor('Lime', Color.Companion.MD_LIME.toLinear()), new RoughnesMetalGridContent$MatColor('Yellow', Color.Companion.MD_YELLOW.toLinear()), new RoughnesMetalGridContent$MatColor('Amber', Color.Companion.MD_AMBER.toLinear()), new RoughnesMetalGridContent$MatColor('Orange', Color.Companion.MD_ORANGE.toLinear()), new RoughnesMetalGridContent$MatColor('Deep Orange', Color.Companion.MD_DEEP_ORANGE.toLinear()), new RoughnesMetalGridContent$MatColor('Brown', Color.Companion.MD_BROWN.toLinear()), new RoughnesMetalGridContent$MatColor('White', Color.Companion.WHITE.toLinear()), new RoughnesMetalGridContent$MatColor('Grey', Color.Companion.MD_GREY.toLinear()), new RoughnesMetalGridContent$MatColor('Blue Grey', Color.Companion.MD_BLUE_GREY.toLinear()), new RoughnesMetalGridContent$MatColor('Almost Black', (new Color(0.1, 0.1, 0.1)).toLinear())]);
  }
  RoughnesMetalGridContent$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var RoughnesMetalGridContent$Companion_instance = null;
  function RoughnesMetalGridContent$Companion_getInstance() {
    if (RoughnesMetalGridContent$Companion_instance === null) {
      new RoughnesMetalGridContent$Companion();
    }return RoughnesMetalGridContent$Companion_instance;
  }
  RoughnesMetalGridContent.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RoughnesMetalGridContent',
    interfaces: [PbrDemo$PbrContent]
  };
  function globeScene$lambda$lambda(closure$elevationUrl, closure$ctx, closure$globe, this$, closure$globeGroup) {
    return function (data) {
      if (data != null) {
        var $receiver = ProtoBuf.Default;
        var metaHierarchy = $receiver.load_dntfbn$(getContextualOrDefault($receiver.context, getKClass(ElevationMapMetaHierarchy)), data);
        closure$globe.elevationMapProvider = new ElevationMapHierarchy(closure$elevationUrl, metaHierarchy, closure$ctx.assetMgr);
        closure$globe.meshDetailLevel = 6;
      } else {
        var $receiver_0 = this$;
        var $this = util.Log;
        var level = Log$Level.WARN;
        var tag = Kotlin.getKClassFromExpression($receiver_0).simpleName;
        if (level.level >= $this.level.level) {
          $this.printer(level, tag, 'Height map data not available');
        }}
      this$.unaryPlus_uv0sim$(closure$globeGroup);
      return Unit;
    };
  }
  function globeScene(ctx) {
    var scenes = ArrayList_init();
    var ui = new GlobeUi(null, ctx);
    var $receiver = new Scene_init(null);
    $receiver.lighting.lights.get_za3lpa$(0).color.set_d7aj7k$(Color.Companion.LIGHT_GRAY);
    var globe = new Globe(6371000.8);
    var globeGroup = new DoublePrecisionRoot(globe);
    ui.globe = globe;
    var $receiver_0 = new GlobeCamHandler(globe, $receiver, ctx);
    globe.setCenter_lu1900$(47.05, 9.48);
    $receiver_0.resetZoom_mx4ult$(15000.0);
    $receiver_0.horizontalRotation = 60.0;
    $receiver_0.verticalRotation = -30.0;
    $receiver.unaryPlus_uv0sim$($receiver_0);
    var $this = Demo$Companion_getInstance();
    var key = 'globe.elevationUrl';
    var default_0 = 'elevation';
    var tmp$, tmp$_0;
    var elevationUrl = (tmp$_0 = typeof (tmp$ = $this.demoProps.get_11rb$(key)) === 'string' ? tmp$ : null) != null ? tmp$_0 : default_0;
    ctx.assetMgr.loadAsset_us385g$(elevationUrl + '/meta.pb', globeScene$lambda$lambda(elevationUrl, ctx, globe, $receiver, globeGroup));
    scenes.add_11rb$($receiver);
    var element = ui.scene;
    scenes.add_11rb$(element);
    return scenes;
  }
  function GlobeUi(globe, ctx) {
    this.globe = globe;
    this.ctx = ctx;
    this.containerWidth_0 = 0.0;
    this.scene = uiScene(void 0, void 0, void 0, GlobeUi$scene$lambda(this));
  }
  function GlobeUi$scene$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function GlobeUi$scene$lambda$lambda($receiver) {
    $receiver.containerUi_2t3ptw$(getCallableRef('SimpleComponentUi', function (component) {
      return new SimpleComponentUi(component);
    }));
    $receiver.componentUi_mloaa0$(GlobeUi$scene$lambda$lambda$lambda);
    $receiver.standardFont_ttufcy$(new FontProps(Font.Companion.SYSTEM_FONT, 12.0));
    return Unit;
  }
  function GlobeUi$scene$lambda$lambda$lambda_0($receiver) {
    $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(18.0), full());
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(-200.0, true), zero(), zero());
    $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
    $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
    return Unit;
  }
  function GlobeUi$scene$lambda$lambda$lambda$lambda(closure$attributions, closure$i, this$GlobeUi) {
    return function ($receiver, f, f_0, f_1) {
      if (!(closure$attributions.get_za3lpa$(closure$i).second.length === 0)) {
        this$GlobeUi.ctx.openUrl_61zpoe$(closure$attributions.get_za3lpa$(closure$i).second);
      }return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda$lambda_1(closure$i, closure$attributions, this$GlobeUi) {
    return function ($receiver) {
      $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(18.0), full());
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-200.0, true), dps(18.0 * (closure$i + 1 | 0), true), zero());
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.textColor.setCustom_11rb$(Color.Companion.LIME);
      $receiver.textColorHovered.setCustom_11rb$(Color.Companion.fromHex_61zpoe$('#42A5F5'));
      $receiver.text = '';
      var $receiver_0 = $receiver.onClick;
      var element = GlobeUi$scene$lambda$lambda$lambda$lambda(closure$attributions, closure$i, this$GlobeUi);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda$lambda_2(this$GlobeUi, closure$posLbl, closure$attributions, this$) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
      var width = {v: 0.0};
      var lines = {v: 1};
      var globe = this$GlobeUi.globe;
      if (globe != null) {
        var lat = toString(globe.centerLat, 5);
        var lon = toString(globe.centerLon, 5);
        if (globe.cameraHeight > 10000) {
          tmp$ = toString(globe.cameraHeight / 1000.0, 1) + ' km';
        } else {
          tmp$ = toString(globe.cameraHeight, 1) + ' m';
        }
        var hgt = tmp$;
        closure$posLbl.text = 'Center: ' + lat + '\xB0, ' + lon + '\xB0  ' + hgt;
        width.v = (tmp$_1 = (tmp$_0 = closure$posLbl.font.apply()) != null ? tmp$_0.textWidth_61zpoe$(closure$posLbl.text) : null) != null ? tmp$_1 : 0.0;
      }if ((tmp$_3 = (tmp$_2 = globe != null ? globe.tileManager : null) != null ? tmp$_2.getCenterTile() : null) != null) {
        var closure$attributions_0 = closure$attributions;
        for (var i = 0; i !== closure$attributions_0.size; ++i) {
          closure$attributions_0.get_za3lpa$(i).first.text = '';
        }
        var tmp$_4, tmp$_0_0;
        var index = 0;
        tmp$_4 = tmp$_3.attributionInfo.iterator();
        while (tmp$_4.hasNext()) {
          var item = tmp$_4.next();
          var i_0 = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
          var tmp$_5, tmp$_6;
          if (i_0 < closure$attributions_0.size) {
            closure$attributions_0.get_za3lpa$(i_0).first.text = item.text;
            if (!equals(item.url, closure$attributions_0.get_za3lpa$(i_0).second)) {
              var $receiver_0 = closure$attributions_0.get_za3lpa$(i_0);
              closure$attributions_0.set_wxm5ur$(i_0, new Pair($receiver_0.first, $receiver_0.second));
            }var w = (tmp$_6 = (tmp$_5 = closure$attributions_0.get_za3lpa$(i_0).first.font.apply()) != null ? tmp$_5.textWidth_61zpoe$(closure$attributions_0.get_za3lpa$(i_0).first.text) : null) != null ? tmp$_6 : 0.0;
            var a = width.v;
            width.v = Math_0.max(a, w);
            lines.v = lines.v + 1 | 0;
          }}
      }if (width.v !== this$GlobeUi.containerWidth_0) {
        this$GlobeUi.containerWidth_0 = width.v;
        this$.layoutSpec.setSize_4ujscr$(dps(this$GlobeUi.containerWidth_0 + 8, true), dps(18.0 * lines.v), full());
        this$.layoutSpec.setOrigin_4ujscr$(dps(-this$GlobeUi.containerWidth_0 - 8, true), zero(), zero());
        this$.root.requestLayout();
      }return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda_0(this$, closure$maxAttributions, closure$attributions, this$GlobeUi) {
    return function ($receiver) {
      var tmp$;
      var posLbl = this$.label_tokfmu$('posLabel', GlobeUi$scene$lambda$lambda$lambda_0);
      $receiver.unaryPlus_uv0sim$(posLbl);
      tmp$ = closure$maxAttributions;
      for (var i = 0; i < tmp$; i++) {
        var button = this$.button_9zrh0o$('attributionText_' + i, GlobeUi$scene$lambda$lambda$lambda_1(i, closure$attributions, this$GlobeUi));
        var $receiver_0 = closure$attributions;
        var element = new Pair(button, '');
        $receiver_0.add_11rb$(element);
        $receiver.unaryPlus_uv0sim$(button);
      }
      var $receiver_1 = $receiver.onPreRender;
      var element_0 = GlobeUi$scene$lambda$lambda$lambda_2(this$GlobeUi, posLbl, closure$attributions, $receiver);
      $receiver_1.add_11rb$(element_0);
      return Unit;
    };
  }
  function GlobeUi$scene$lambda(this$GlobeUi) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK, GlobeUi$scene$lambda$lambda);
      $receiver.content.ui.setCustom_11rb$(new BlankComponentUi());
      var attributions = ArrayList_init();
      var maxAttributions = 2;
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('globeUI', GlobeUi$scene$lambda$lambda_0($receiver, maxAttributions, attributions, this$GlobeUi)));
      return Unit;
    };
  }
  GlobeUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlobeUi',
    interfaces: []
  };
  function makeGroundGrid$lambda$lambda(closure$groundExt, closure$y) {
    return function ($receiver) {
      $receiver.transform.push();
      var closure$groundExt_0 = closure$groundExt;
      var closure$y_0 = closure$y;
      $receiver.rotate_ad55pp$(-90.0, Vec3f.Companion.X_AXIS);
      $receiver.color = Color.Companion.LIGHT_GRAY.withAlpha_mx4ult$(0.2);
      var $receiver_0 = $receiver.rectProps.defaults();
      $receiver_0.origin.set_y2kzbl$(-closure$groundExt_0, -closure$groundExt_0, closure$y_0);
      $receiver_0.width = closure$groundExt_0 * 2.0;
      $receiver_0.height = closure$groundExt_0 * 2.0;
      var uv = closure$groundExt_0 / 2;
      $receiver_0.texCoordUpperLeft.set_dleff0$(-uv, -uv);
      $receiver_0.texCoordUpperRight.set_dleff0$(uv, -uv);
      $receiver_0.texCoordLowerLeft.set_dleff0$(-uv, uv);
      $receiver_0.texCoordLowerRight.set_dleff0$(uv, uv);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      $receiver.geometry.generateTangents();
      return Unit;
    };
  }
  function Coroutine$makeGroundGrid$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeGroundGrid$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeGroundGrid$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeGroundGrid$lambda$lambda$lambda.prototype.constructor = Coroutine$makeGroundGrid$lambda$lambda$lambda;
  Coroutine$makeGroundGrid$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$('ground_color.png', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeGroundGrid$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeGroundGrid$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$makeGroundGrid$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeGroundGrid$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeGroundGrid$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeGroundGrid$lambda$lambda$lambda_0.prototype.constructor = Coroutine$makeGroundGrid$lambda$lambda$lambda_0;
  Coroutine$makeGroundGrid$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$('ground_nrm.png', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeGroundGrid$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeGroundGrid$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function makeGroundGrid$lambda(closure$groundExt, closure$y) {
    return function ($receiver) {
      $receiver.isCastingShadow = false;
      $receiver.generate_v2sixm$(makeGroundGrid$lambda$lambda(closure$groundExt, closure$y));
      var $receiver_0 = new PbrShader$PbrConfig();
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.isNormalMapped = true;
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg);
      $receiver_1.albedoMap = new Texture(void 0, makeGroundGrid$lambda$lambda$lambda);
      $receiver_1.normalMap = new Texture(void 0, makeGroundGrid$lambda$lambda$lambda_0);
      $receiver.pipelineLoader = $receiver_1;
      return Unit;
    };
  }
  function makeGroundGrid(cells, y) {
    if (y === void 0)
      y = 0.0;
    var groundExt = cells / 2 | 0;
    return textureMesh(void 0, true, makeGroundGrid$lambda(groundExt, y));
  }
  function multiScene(koolCtx) {
    var leftScene = simpleShapesScene(koolCtx);
    var rightScene = uiDemoScene();
    return listOf_0([leftScene, rightScene]);
  }
  function particleDemo$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.setMouseRotation_dleff0$(20.0, -30.0);
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.resetZoom_mx4ult$(25.0);
      $receiver.translation.set_y2kzbl$(2.5, 2.5, 2.5);
      return Unit;
    };
  }
  function Coroutine$particleDemo$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$particleDemo$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$particleDemo$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$particleDemo$lambda$lambda.prototype.constructor = Coroutine$particleDemo$lambda$lambda;
  Coroutine$particleDemo$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$('snowflakes.png', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function particleDemo$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$particleDemo$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function particleDemo$lambda$lambda$lambda(closure$dist, closure$isColored) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(closure$dist.nextPoint());
      $receiver.position.y = 15.0;
      var sz = randomF(0.1, 0.2);
      $receiver.size.set_dleff0$(sz, sz);
      $receiver.velocity.set_y2kzbl$(0.0, -randomF(0.6, 1.4), 0.0);
      $receiver.angularVelocity = randomF(-45.0, 45.0);
      if (closure$isColored.v) {
        $receiver.color.set_d7aj7k$(ColorGradient.Companion.JET_MD.getColor_y2kzbl$(randomF_0()));
      } else {
        $receiver.color.set_d7aj7k$(Color.Companion.WHITE);
      }
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_0(closure$types, closure$i) {
    return function ($receiver, it) {
      if ($receiver.position.y < $receiver.size.y / 2) {
        $receiver.replaceBy_1rs1w0$(closure$types.get_za3lpa$(closure$i + 25 | 0));
        $receiver.lifeTime = 0.0;
      }return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_1($receiver) {
    $receiver.position.y = $receiver.size.y / 2;
    $receiver.velocity.set_czzhiu$(Vec3f.Companion.ZERO);
    $receiver.angularVelocity = 0.0;
    return Unit;
  }
  function particleDemo$lambda$lambda$lambda_2($receiver, it) {
    var tmp$ = $receiver.color;
    var $receiver_0 = 5.0 - $receiver.lifeTime;
    var clamp$result;
    if ($receiver_0 < 0.0) {
      clamp$result = 0.0;
    } else if ($receiver_0 > 1.0) {
      clamp$result = 1.0;
    } else {
      clamp$result = $receiver_0;
    }
    tmp$.a = clamp$result;
    if ($receiver.lifeTime > 5.0) {
      $receiver.die();
    }return Unit;
  }
  function particleDemo$lambda$lambda$lambda_3(closure$isSorted, this$, closure$currentParticleCount, closure$maxParticleCount, closure$types) {
    return function ($receiver, ctx) {
      this$.drawOrder = closure$isSorted.v ? BillboardMesh$DrawOrder.FAR_FIRST : BillboardMesh$DrawOrder.AS_IS;
      closure$currentParticleCount.v = this$.numParticles;
      if (this$.numParticles < closure$maxParticleCount.v) {
        var a = numberToInt((closure$maxParticleCount.v / 15 | 0) * ctx.deltaT);
        var b = closure$maxParticleCount.v - this$.numParticles | 0;
        var spawnCount = Math_0.min(a, b);
        for (var i = 1; i <= spawnCount; i++) {
          this$.spawnParticle_1rs1w0$(closure$types.get_za3lpa$(randomI(new IntRange(0, 24))));
        }
      }return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_4(it) {
    return new BlankComponentUi();
  }
  function particleDemo$lambda$lambda$lambda_5(it) {
    return new BlankComponentUi();
  }
  function particleDemo$lambda$lambda_1($receiver) {
    $receiver.componentUi_mloaa0$(particleDemo$lambda$lambda$lambda_4);
    $receiver.containerUi_2t3ptw$(particleDemo$lambda$lambda$lambda_5);
    return Unit;
  }
  function particleDemo$lambda$lambda$lambda_6(closure$posY, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(40.0, true), full());
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_7(closure$posY, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(5.0), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(90.0), dps(1.0, true), full());
      var bg = new SimpleComponentUi($receiver);
      bg.color.setCustom_11rb$(this$.theme.accentColor);
      $receiver.ui.setCustom_11rb$(bg);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda$lambda(closure$isColored) {
    return function ($receiver) {
      closure$isColored.v = $receiver.isEnabled;
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_8(closure$posY, closure$isColored) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      $receiver.isEnabled = closure$isColored.v;
      var $receiver_0 = $receiver.onStateChange;
      var element = particleDemo$lambda$lambda$lambda$lambda(closure$isColored);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda$lambda_0(closure$isSorted) {
    return function ($receiver) {
      closure$isSorted.v = $receiver.isEnabled;
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_9(closure$posY, closure$isSorted) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      $receiver.isEnabled = closure$isSorted.v;
      var $receiver_0 = $receiver.onStateChange;
      var element = particleDemo$lambda$lambda$lambda$lambda_0(closure$isSorted);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_10(closure$posY) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda$lambda_1(closure$currentParticleCount, this$) {
    return function ($receiver, it) {
      this$.text = closure$currentParticleCount.v.toString();
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_11(closure$posY, closure$currentParticleCount) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      var $receiver_0 = $receiver.onPreRender;
      var element = particleDemo$lambda$lambda$lambda$lambda_1(closure$currentParticleCount, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_12(closure$posY) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda$lambda_2(closure$maxParticleCount, this$) {
    return function ($receiver, it) {
      this$.text = closure$maxParticleCount.v.toString();
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_13(closure$posY, closure$maxParticleCount) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      var $receiver_0 = $receiver.onPreRender;
      var element = particleDemo$lambda$lambda$lambda$lambda_2(closure$maxParticleCount, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda$lambda_3(closure$maxParticleCount) {
    return function ($receiver, value) {
      closure$maxParticleCount.v = numberToInt(round(value)) * 1000 | 0;
      return Unit;
    };
  }
  function particleDemo$lambda$lambda$lambda_14(closure$posY, closure$maxParticleCount) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(closure$posY.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0, true), full());
      $receiver.setValue_y2kzbl$(1.0, 50.0, 10.0);
      var $receiver_0 = $receiver.onValueChanged;
      var element = particleDemo$lambda$lambda$lambda$lambda_3(closure$maxParticleCount);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function particleDemo$lambda$lambda_2(this$, closure$isColored, closure$isSorted, closure$currentParticleCount, closure$maxParticleCount) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-200.0, true), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), pcs(100.0), full());
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      var posY = {v: -45.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Settings', particleDemo$lambda$lambda$lambda_6(posY, this$)));
      $receiver.unaryPlus_uv0sim$(this$.component_qphi6d$('divider', particleDemo$lambda$lambda$lambda_7(posY, this$)));
      posY.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Colorful', particleDemo$lambda$lambda$lambda_8(posY, closure$isColored)));
      posY.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Z-Sorted', particleDemo$lambda$lambda$lambda_9(posY, closure$isSorted)));
      posY.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Current:', particleDemo$lambda$lambda$lambda_10(posY)));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('currentCnt', particleDemo$lambda$lambda$lambda_11(posY, closure$currentParticleCount)));
      posY.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Max:', particleDemo$lambda$lambda$lambda_12(posY)));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('maxCnt', particleDemo$lambda$lambda$lambda_13(posY, closure$maxParticleCount)));
      posY.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('particleCnt', particleDemo$lambda$lambda$lambda_14(posY, closure$maxParticleCount)));
      return Unit;
    };
  }
  function particleDemo$lambda(closure$isColored, closure$isSorted, closure$currentParticleCount, closure$maxParticleCount) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK_SIMPLE, particleDemo$lambda$lambda_1);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('particle-menu', particleDemo$lambda$lambda_2($receiver, closure$isColored, closure$isSorted, closure$currentParticleCount, closure$maxParticleCount)));
      return Unit;
    };
  }
  function particleDemo(ctx) {
    var isColored = {v: false};
    var isSorted = {v: false};
    var currentParticleCount = {v: 0};
    var maxParticleCount = {v: 10000};
    var $receiver = new Scene_init(null);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, particleDemo$lambda$lambda($receiver)));
    $receiver.unaryPlus_uv0sim$(makeGroundGrid(40));
    var $receiver_0 = new ParticleSystem(new Texture(void 0, particleDemo$lambda$lambda_0), 50000);
    $receiver_0.isDepthMask = false;
    var dist = new CubicPointDistribution(40.0);
    var types = ArrayList_init();
    for (var i = 0; i <= 24; i++) {
      var texCenter = new Vec2f(0.1 + i % 5 / 5.0, 0.1 + (i / 5 | 0) / 5.0);
      var element = new ParticleSystem$Type('FallingSnowflake-' + i, texCenter, new Vec2f(0.2, 0.2), void 0, particleDemo$lambda$lambda$lambda(dist, isColored), particleDemo$lambda$lambda$lambda_0(types, i));
      types.add_11rb$(element);
    }
    for (var i_0 = 0; i_0 <= 24; i_0++) {
      var texCenter_0 = new Vec2f(0.1 + i_0 % 5 / 5.0, 0.1 + (i_0 / 5 | 0) / 5.0);
      var element_0 = new ParticleSystem$Type('MeltingSnowflake-' + i_0, texCenter_0, new Vec2f(0.2, 0.2), void 0, particleDemo$lambda$lambda$lambda_1, particleDemo$lambda$lambda$lambda_2);
      types.add_11rb$(element_0);
    }
    $receiver_0.onPreRender.add_11rb$(particleDemo$lambda$lambda$lambda_3(isSorted, $receiver_0, currentParticleCount, maxParticleCount, types));
    $receiver.unaryPlus_uv0sim$($receiver_0);
    var scene = $receiver;
    var ui = uiScene(ctx.screenDpi, void 0, void 0, particleDemo$lambda(isColored, isSorted, currentParticleCount, maxParticleCount));
    return listOf_0([scene, ui]);
  }
  function pointScene$lambda$lambda(closure$frameCnt, closure$data, closure$trav, closure$ptVertCnt, closure$tree) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1;
      if ((closure$frameCnt.v = closure$frameCnt.v - 1 | 0, closure$frameCnt.v) === 0) {
        closure$frameCnt.v = 30;
        var vert = closure$data.get_za3lpa$(0);
        tmp$ = closure$trav.result.iterator();
        while (tmp$.hasNext()) {
          var point = tmp$.next();
          tmp$_0 = closure$ptVertCnt;
          for (var i = 0; i < tmp$_0; i++) {
            vert.index = point.index + i | 0;
            vert.color.set_d7aj7k$(Color.Companion.DARK_GRAY);
          }
        }
        closure$trav.center.set_y2kzbl$(randomF(-1.0, 1.0), randomF(-1.0, 1.0), randomF(-1.0, 1.0));
        var t = new PerfTimer();
        closure$trav.traverse_m6hlto$(closure$tree);
        var searchT = t.takeMs();
        t.reset();
        tmp$_1 = closure$trav.result;
        for (var i_0 = 0; i_0 !== tmp$_1.size; ++i_0) {
          var tmp$_2;
          tmp$_2 = closure$ptVertCnt;
          for (var vi = 0; vi < tmp$_2; vi++) {
            vert.index = closure$trav.result.get_za3lpa$(i_0).index + vi | 0;
            vert.color.set_d7aj7k$(ColorGradient.Companion.PLASMA.getColor_y2kzbl$(i_0 / closure$trav.result.size));
          }
        }
        var updateT = t.takeMs();
        var $this = util.Log;
        var level = Log$Level.INFO;
        var tag = Kotlin.getKClassFromExpression($receiver).simpleName;
        if (level.level >= $this.level.level) {
          $this.printer(level, tag, 'Tree traversal retrieved ' + closure$trav.result.size + ' points, ' + ('took ' + toString(searchT, 3) + ' ms; ') + ('Point update took ' + toString(updateT, 3) + ' ms'));
        }closure$data.hasChanged = true;
      }return Unit;
    };
  }
  function pointScene$lambda$lambda_0(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.setMouseRotation_dleff0$(0.0, -30.0);
      $receiver.minZoom = 5.0;
      $receiver.maxZoom = 25.0;
      $receiver.translationBounds = BoundingBox_init(new Vec3f(-10.0, -10.0, -10.0), new Vec3f(10.0, 10.0, 10.0));
      return Unit;
    };
  }
  function pointScene$lambda$lambda$lambda(this$) {
    return function ($receiver, it) {
      this$.rotate_ad55pp$(it.deltaT * 45, Vec3f.Companion.Y_AXIS);
      return Unit;
    };
  }
  function pointScene$lambda$lambda_1(closure$pointMesh) {
    return function ($receiver) {
      $receiver.onPreRender.add_11rb$(pointScene$lambda$lambda$lambda($receiver));
      $receiver.unaryPlus_uv0sim$(closure$pointMesh);
      return Unit;
    };
  }
  function pointScene() {
    var tmp$ = makePointMesh();
    var pointMesh = tmp$.component1()
    , tree = tmp$.component2();
    var trav = (new InRadiusTraverser()).setup_2qa7tb$(Vec3f.Companion.ZERO, 1.0);
    var data = pointMesh.geometry;
    var ptVertCnt = Kotlin.isType(pointMesh, BillboardMesh) ? 4 : 1;
    var frameCnt = {v: 30};
    var $receiver = new Scene_init(null);
    $receiver.onPreRender.add_11rb$(pointScene$lambda$lambda(frameCnt, data, trav, ptVertCnt, tree));
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, pointScene$lambda$lambda_0($receiver)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, pointScene$lambda$lambda_1(pointMesh)));
    return $receiver;
  }
  function makePointMesh$lambda$lambda(closure$pt) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(closure$pt);
      $receiver.color.set_d7aj7k$(Color.Companion.DARK_GRAY);
      return Unit;
    };
  }
  function makePointMesh$lambda(closure$points) {
    return function ($receiver) {
      var dist = new CubicPointDistribution(5.0);
      for (var i = 1; i <= 100000; i++) {
        var pt = dist.nextPoint();
        var idx = $receiver.addPoint_tohjaj$(makePointMesh$lambda$lambda(pt));
        closure$points.add_11rb$(new MeshPoint(pt.x, pt.y, pt.z, idx));
      }
      return Unit;
    };
  }
  function makePointMesh() {
    var points = ArrayList_init();
    var mesh = pointMesh(void 0, makePointMesh$lambda(points));
    var message = 'Constructed spatial tree with ' + points.size + ' points in ';
    var tag;
    var level;
    tag = 'PerfTimer';
    level = Log$Level.INFO;
    var t = now();
    var ret = pointKdTree(points);
    var $this = util.Log;
    if (level.level >= $this.level.level) {
      $this.printer(level, tag, message + ' ' + toString(now() - t, 3) + ' ms');
    }var tree = ret;
    return new Pair(mesh, tree);
  }
  function MeshPoint(x, y, z, index) {
    Vec3f.call(this, x, y, z);
    this.index = index;
  }
  MeshPoint.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MeshPoint',
    interfaces: [Vec3f]
  };
  function reflectionDemo$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.panMethod = yPlanePan();
      $receiver.translationBounds = BoundingBox_init(new Vec3f(-20.0, 0.0, -20.0), new Vec3f(20.0, 0.0, 20.0));
      $receiver.setMouseRotation_dleff0$(20.0, -15.0);
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.minZoom = 1.0;
      $receiver.maxZoom = 40.0;
      $receiver.resetZoom_mx4ult$(20.0);
      return Unit;
    };
  }
  function reflectionDemo$lambda$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.sphereProps.defaults();
    $receiver_0.center.set_y2kzbl$(0.0, 3.0, 0.0);
    $receiver_0.radius = 2.5;
    $receiver_0.steps = 100;
    $receiver.sphere_mojs8w$($receiver.sphereProps);
    return Unit;
  }
  function reflectionDemo$lambda$lambda_0($receiver) {
    $receiver.generate_v2sixm$(reflectionDemo$lambda$lambda$lambda);
    return Unit;
  }
  function reflectionDemo$lambda$lambda$lambda$lambda($receiver) {
    $receiver.transform.push();
    $receiver.color = Color.Companion.MD_PINK;
    $receiver.translate_y2kzbl$(-8.0, 1.0, -8.0);
    $receiver.rotate_ad55pp$(18.0, Vec3f.Companion.Y_AXIS);
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.size.set_y2kzbl$(3.0, 4.0, 3.0);
    $receiver_0.origin.set_y2kzbl$(-$receiver_0.size.x / 2.0, -$receiver_0.size.y / 2.0, 0.0);
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    $receiver.transform.pop();
    $receiver.transform.push();
    $receiver.color = Color.Companion.MD_LIGHT_BLUE;
    $receiver.translate_y2kzbl$(-8.0, 1.0, 8.0);
    $receiver.rotate_ad55pp$(36.0, Vec3f.Companion.Y_AXIS);
    var $receiver_1 = $receiver.cubeProps.defaults();
    $receiver_1.size.set_y2kzbl$(4.0, 3.0, 3.0);
    $receiver_1.origin.set_y2kzbl$(-$receiver_1.size.x / 2.0, -$receiver_1.size.y / 2.0, 0.0);
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    $receiver.transform.pop();
    $receiver.transform.push();
    $receiver.color = Color.Companion.MD_LIME;
    $receiver.translate_y2kzbl$(8.0, 1.0, 8.0);
    $receiver.rotate_ad55pp$(54.0, Vec3f.Companion.Y_AXIS);
    var $receiver_2 = $receiver.cubeProps.defaults();
    $receiver_2.size.set_y2kzbl$(3.0, 3.0, 4.0);
    $receiver_2.origin.set_y2kzbl$(-$receiver_2.size.x / 2.0, -$receiver_2.size.y / 2.0, 0.0);
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    $receiver.transform.pop();
    $receiver.transform.push();
    $receiver.color = Color.Companion.MD_DEEP_PURPLE;
    $receiver.translate_y2kzbl$(8.0, 1.0, -8.0);
    $receiver.rotate_ad55pp$(72.0, Vec3f.Companion.Y_AXIS);
    var $receiver_3 = $receiver.cubeProps.defaults();
    $receiver_3.size.set_y2kzbl$(3.0, 3.0, 3.0);
    $receiver_3.origin.set_y2kzbl$(-$receiver_3.size.x / 2.0, -$receiver_3.size.y / 2.0, 0.0);
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    $receiver.transform.pop();
    return Unit;
  }
  function reflectionDemo$lambda$lambda$lambda_0($receiver) {
    $receiver.generate_v2sixm$(reflectionDemo$lambda$lambda$lambda$lambda);
    return Unit;
  }
  function reflectionDemo$lambda$lambda$lambda_1(closure$ctx, this$) {
    return function ($receiver, it) {
      this$.rotate_ad55pp$(closure$ctx.deltaT * 90.0, Vec3f.Companion.Y_AXIS);
      return Unit;
    };
  }
  function reflectionDemo$lambda$lambda_1(closure$reflectedObjects, closure$ctx) {
    return function ($receiver) {
      closure$reflectedObjects.add_11rb$($receiver);
      $receiver.unaryPlus_uv0sim$(colorMesh(void 0, reflectionDemo$lambda$lambda$lambda_0));
      var $receiver_0 = $receiver.onPreRender;
      var element = reflectionDemo$lambda$lambda$lambda_1(closure$ctx, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function reflectionDemo(ctx) {
    var reflectedObjects = ArrayList_init();
    var $receiver = new Scene_init(null);
    var $receiver_0 = Skybox_init('skybox/y-up/sky_ft.jpg', 'skybox/y-up/sky_bk.jpg', 'skybox/y-up/sky_lt.jpg', 'skybox/y-up/sky_rt.jpg', 'skybox/y-up/sky_up.jpg', 'skybox/y-up/sky_dn.jpg');
    reflectedObjects.add_11rb$($receiver_0);
    $receiver.unaryPlus_uv0sim$($receiver_0);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, reflectionDemo$lambda$lambda($receiver)));
    var $receiver_1 = makeGroundGrid(100);
    reflectedObjects.add_11rb$($receiver_1);
    $receiver.unaryPlus_uv0sim$($receiver_1);
    var mesh = colorMesh(void 0, reflectionDemo$lambda$lambda_0);
    $receiver.unaryPlus_uv0sim$(mesh);
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, reflectionDemo$lambda$lambda_1(reflectedObjects, ctx)));
    var mainScene = $receiver;
    return listOf(mainScene);
  }
  function simpleShapesScene$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.setMouseRotation_dleff0$(20.0, -30.0);
      $receiver.translationBounds = BoundingBox_init(Vec3f_init(-50.0), Vec3f_init(50.0));
      $receiver.unaryPlus_uv0sim$(this$.camera);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda(this$, closure$animator) {
    return function ($receiver, ctx) {
      this$.setIdentity();
      this$.translate_y2kzbl$(-5.0, closure$animator.tick_aemszp$(ctx), 0.0);
      this$.rotate_ad55pp$(ctx.time * 19, Vec3f.Companion.Y_AXIS);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.sphereProps.defaults();
    $receiver_0.radius = 1.5;
    $receiver_0.steps = 50;
    $receiver.sphere_mojs8w$($receiver.sphereProps);
    return Unit;
  }
  function simpleShapesScene$lambda$lambda$lambda_0($receiver) {
    $receiver.generate_v2sixm$(simpleShapesScene$lambda$lambda$lambda$lambda);
    return Unit;
  }
  function simpleShapesScene$lambda$lambda_0($receiver) {
    var animator = new CosAnimator(new InterpolatedFloat(-1.0, 1.0));
    animator.repeating = Animator.Companion.REPEAT_TOGGLE_DIR;
    $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda($receiver, animator));
    $receiver.unaryPlus_uv0sim$(textureMesh('Sphere', void 0, simpleShapesScene$lambda$lambda$lambda_0));
    return Unit;
  }
  function simpleShapesScene$lambda$lambda$lambda_1(closure$cubeAnimator, this$) {
    return function ($receiver, ctx) {
      var angle = closure$cubeAnimator.tick_aemszp$(ctx);
      this$.setIdentity();
      this$.translate_y2kzbl$(5.0, 0.0, 0.0);
      this$.rotate_ad55pp$(angle * 5, Vec3f.Companion.Y_AXIS);
      this$.rotate_ad55pp$(angle, Vec3f.Companion.X_AXIS);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_0(closure$cubeAnimator) {
    return function (v) {
      closure$cubeAnimator.speed = v;
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_1($receiver) {
    $receiver.scale_y2kzbl$(2.0, 2.0, 2.0);
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.colored_6taknv$();
    $receiver_0.centered();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_2(closure$speedAnimator) {
    return function ($receiver, ctx) {
      closure$speedAnimator.tick_aemszp$(ctx);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_3(closure$speedAnimator) {
    return function ($receiver, f, f_0, f_1) {
      closure$speedAnimator.speed = 1.0;
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_4(closure$speedAnimator) {
    return function ($receiver, f, f_0, f_1) {
      closure$speedAnimator.speed = -1.0;
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda_2(closure$cubeAnimator) {
    return function ($receiver) {
      var speedAnimator = new CosAnimator(new InterpolatedFloat(0.0, 1.0));
      speedAnimator.speed = -1.0;
      speedAnimator.duration = 0.5;
      speedAnimator.value.onUpdate = simpleShapesScene$lambda$lambda$lambda$lambda_0(closure$cubeAnimator);
      $receiver.generate_v2sixm$(simpleShapesScene$lambda$lambda$lambda$lambda_1);
      $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda$lambda_2(speedAnimator));
      $receiver.onHoverEnter.add_11rb$(simpleShapesScene$lambda$lambda$lambda$lambda_3(speedAnimator));
      $receiver.onHoverExit.add_11rb$(simpleShapesScene$lambda$lambda$lambda$lambda_4(speedAnimator));
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda_1($receiver) {
    var cubeAnimator = new LinearAnimator(new InterpolatedFloat(0.0, 360.0));
    cubeAnimator.repeating = Animator.Companion.REPEAT;
    cubeAnimator.duration = 20.0;
    $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda_1(cubeAnimator, $receiver));
    $receiver.unaryPlus_uv0sim$(colorMesh('Cube', simpleShapesScene$lambda$lambda$lambda_2(cubeAnimator)));
    return Unit;
  }
  function simpleShapesScene$lambda$lambda$lambda_3(closure$animator, this$) {
    return function ($receiver, ctx) {
      var s = closure$animator.tick_aemszp$(ctx);
      this$.setIdentity();
      this$.translate_y2kzbl$(0.0, 0.0, -5.0);
      this$.scale_y2kzbl$(s, s, s);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_5(closure$font) {
    return function ($receiver) {
      $receiver.color = Color.Companion.LIME;
      var font = closure$font;
      var tmp$;
      var tmp$_0;
      if ((tmp$ = $receiver.textProps) != null)
        tmp$_0 = tmp$;
      else {
        var $receiver_0 = new TextProps_init(font);
        $receiver.textProps = $receiver_0;
        tmp$_0 = $receiver_0;
      }
      var props = tmp$_0;
      props.defaults();
      props.font = font;
      var closure$font_0 = closure$font;
      props.text = 'kool Text!';
      props.origin.set_y2kzbl$(-closure$font_0.textWidth_61zpoe$(props.text) / 2.0, 0.0, 0.0);
      $receiver.text_s8z339$(props, 0.0);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda_4(closure$font) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(simpleShapesScene$lambda$lambda$lambda$lambda_5(closure$font));
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda_2(closure$ctx) {
    return function ($receiver) {
      var animator = new CosAnimator(new InterpolatedFloat(0.75, 1.25));
      animator.repeating = Animator.Companion.REPEAT_TOGGLE_DIR;
      animator.duration = 0.75;
      $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda_3(animator, $receiver));
      var font = Font_init(new FontProps(Font.Companion.SYSTEM_FONT, 72.0, 0), closure$ctx);
      $receiver.unaryPlus_uv0sim$(textMesh(font, void 0, simpleShapesScene$lambda$lambda$lambda_4(font)));
      return Unit;
    };
  }
  function simpleShapesScene(ctx) {
    var $receiver = new Scene_init('simpleShapes');
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, simpleShapesScene$lambda$lambda($receiver)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, simpleShapesScene$lambda$lambda_0));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, simpleShapesScene$lambda$lambda_1));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, simpleShapesScene$lambda$lambda_2(ctx)));
    return $receiver;
  }
  function synthieScene(ctx) {
    var content = new SynthieScene(ctx);
    var menu = synthieMenu(content, ctx);
    return listOf_0([content, menu]);
  }
  function synthieMenu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function synthieMenu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function synthieMenu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(synthieMenu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(synthieMenu$lambda$lambda$lambda_0);
    return Unit;
  }
  function synthieMenu$lambda$lambda$lambda_1(closure$content, this$) {
    return function ($receiver) {
      var tmp$;
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(50.0).minus_m986jv$(dps(240.0)), dps(10.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(320.0), pcs(100.0), full());
      tmp$ = closure$content.melody.sequence;
      for (var col = 0; col !== tmp$.length; ++col) {
        var tmp$_0;
        tmp$_0 = reversed(new IntRange(0, 15)).iterator();
        while (tmp$_0.hasNext()) {
          var row = tmp$_0.next();
          $receiver.unaryPlus_uv0sim$(new SequenceButton(col, row, closure$content.melody, this$));
        }
      }
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), dps(120.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), full());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    $receiver.padding = new Margin(zero(), zero(), zero(), zero());
    $receiver.text = 'Melody';
    return Unit;
  }
  function synthieMenu$lambda$lambda$lambda$lambda$lambda(closure$content) {
    return function ($receiver, value) {
      closure$content.melody.gain = value / 50.0;
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_0(closure$content) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(70.0), dps(120.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), full());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      var $receiver_0 = $receiver.onValueChanged;
      var element = synthieMenu$lambda$lambda$lambda$lambda$lambda(closure$content);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_1($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), dps(80.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), full());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    $receiver.padding = new Margin(zero(), zero(), zero(), zero());
    $receiver.text = 'Pad';
    return Unit;
  }
  function synthieMenu$lambda$lambda$lambda$lambda$lambda_0(closure$content) {
    return function ($receiver, value) {
      closure$content.pad.gain = value / 50.0;
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_2(closure$content) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(70.0), dps(80.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), full());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      var $receiver_0 = $receiver.onValueChanged;
      var element = synthieMenu$lambda$lambda$lambda$lambda$lambda_0(closure$content);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_3($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), dps(40.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), full());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    $receiver.padding = new Margin(zero(), zero(), zero(), zero());
    $receiver.text = 'Shaker';
    return Unit;
  }
  function synthieMenu$lambda$lambda$lambda$lambda$lambda_1(closure$content) {
    return function ($receiver, value) {
      closure$content.shaker.gain = value / 50.0;
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_4(closure$content) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(70.0), dps(40.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), full());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      var $receiver_0 = $receiver.onValueChanged;
      var element = synthieMenu$lambda$lambda$lambda$lambda$lambda_1(closure$content);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_5($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), zero(), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), full());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    $receiver.padding = new Margin(zero(), zero(), zero(), zero());
    $receiver.text = 'Kick';
    return Unit;
  }
  function synthieMenu$lambda$lambda$lambda$lambda$lambda_2(closure$content) {
    return function ($receiver, value) {
      closure$content.kick.gain = value / 50.0;
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_6(closure$content) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(70.0), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), full());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      var $receiver_0 = $receiver.onValueChanged;
      var element = synthieMenu$lambda$lambda$lambda$lambda$lambda_2(closure$content);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda_2(closure$content) {
    return function ($receiver, f, f_0, f_1) {
      closure$content.isPickingEnabled = false;
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda_3(closure$content) {
    return function ($receiver, f, rt, f_0) {
      if (!rt.isHit) {
        closure$content.isPickingEnabled = true;
      }return Unit;
    };
  }
  function synthieMenu$lambda$lambda_0(closure$content, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(260.0), full());
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.unaryPlus_uv0sim$(this$.container_t34sov$('sequencer', synthieMenu$lambda$lambda$lambda_1(closure$content, this$)));
      var $receiver_0 = new VerticalLayout('volumes', this$);
      var this$_0 = this$;
      var closure$content_0 = closure$content;
      $receiver_0.layoutSpec.setOrigin_4ujscr$(pcs(50.0, true).plus_m986jv$(dps(250.0, true)), dps(10.0, true), zero());
      $receiver_0.layoutSpec.setSize_4ujscr$(dps(240.0, true), dps(160.0, true), full());
      $receiver_0.unaryPlus_uv0sim$(this$_0.label_tokfmu$('meloLbl', synthieMenu$lambda$lambda$lambda$lambda));
      $receiver_0.unaryPlus_uv0sim$(this$_0.slider_87iqh3$('melo', synthieMenu$lambda$lambda$lambda$lambda_0(closure$content_0)));
      $receiver_0.unaryPlus_uv0sim$(this$_0.label_tokfmu$('padLbl', synthieMenu$lambda$lambda$lambda$lambda_1));
      $receiver_0.unaryPlus_uv0sim$(this$_0.slider_87iqh3$('pad', synthieMenu$lambda$lambda$lambda$lambda_2(closure$content_0)));
      $receiver_0.unaryPlus_uv0sim$(this$_0.label_tokfmu$('shkLbl', synthieMenu$lambda$lambda$lambda$lambda_3));
      $receiver_0.unaryPlus_uv0sim$(this$_0.slider_87iqh3$('shk', synthieMenu$lambda$lambda$lambda$lambda_4(closure$content_0)));
      $receiver_0.unaryPlus_uv0sim$(this$_0.label_tokfmu$('kickLbl', synthieMenu$lambda$lambda$lambda$lambda_5));
      $receiver_0.unaryPlus_uv0sim$(this$_0.slider_87iqh3$('kick', synthieMenu$lambda$lambda$lambda$lambda_6(closure$content_0)));
      $receiver.unaryPlus_uv0sim$($receiver_0);
      var $receiver_1 = $receiver.onHoverEnter;
      var element = synthieMenu$lambda$lambda$lambda_2(closure$content);
      $receiver_1.add_11rb$(element);
      var $receiver_2 = $receiver.onHoverExit;
      var element_0 = synthieMenu$lambda$lambda$lambda_3(closure$content);
      $receiver_2.add_11rb$(element_0);
      return Unit;
    };
  }
  function synthieMenu$lambda(closure$content) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK, synthieMenu$lambda$lambda);
      var menu = $receiver.container_t34sov$('menu', synthieMenu$lambda$lambda_0(closure$content, $receiver));
      $receiver.unaryPlus_uv0sim$(menu);
      return Unit;
    };
  }
  function synthieMenu(content, ctx) {
    return uiScene(ctx.screenDpi, void 0, void 0, synthieMenu$lambda(content));
  }
  function VerticalLayout(name, root) {
    UiContainer.call(this, name, root);
  }
  VerticalLayout.prototype.doLayout_sq5703$ = function (layoutBounds, ctx) {
    if (!(layoutBounds != null ? layoutBounds.equals(this.componentBounds) : null)) {
      this.componentBounds.clear();
    }UiContainer.prototype.doLayout_sq5703$.call(this, layoutBounds, ctx);
    if (!(layoutBounds != null ? layoutBounds.equals(this.componentBounds) : null)) {
      this.translate_y2kzbl$(-this.posInParent.x, -this.posInParent.y, -this.posInParent.z);
      this.rotate_ad55pp$(90.0, Vec3f.Companion.Z_AXIS);
      this.translate_y2kzbl$(this.posInParent.y, -this.posInParent.x, this.posInParent.z);
    }};
  VerticalLayout.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VerticalLayout',
    interfaces: [UiContainer]
  };
  function Melody() {
    SampleNode.call(this);
    this.sequence = [0, 0, 0, 5, 3, 3, 3, 8, 0, 0, 0, 8, 8, 10, 3, 15];
    this.index = 0.0;
    var $receiver = new Oscillator(Wave.Companion.SINE, 1.0 / 32.0);
    $receiver.gain = 140.0;
    this.lfo1_0 = $receiver;
    var $receiver_0 = new Oscillator(Wave.Companion.SINE, 0.5);
    $receiver_0.gain = 0.2;
    $receiver_0.phaseShift = 0.5;
    this.lfo2_0 = $receiver_0;
    var $receiver_1 = new Oscillator(Wave.Companion.SAW);
    $receiver_1.gain = 0.7;
    this.osc1_0 = $receiver_1;
    var $receiver_2 = new Oscillator(Wave.Companion.SQUARE);
    $receiver_2.gain = 0.4;
    this.osc2_0 = $receiver_2;
    var $receiver_3 = new Oscillator(Wave.Companion.SINE);
    $receiver_3.gain = 0.8;
    this.osc3_0 = $receiver_3;
    var $receiver_4 = new Oscillator(Wave.Companion.SQUARE);
    $receiver_4.gain = 1.2;
    this.osc4_0 = $receiver_4;
    this.moodFilter_0 = new MoodFilter(this);
  }
  Melody.prototype.generate_mx4ult$ = function (dt) {
    var p = this.t * 4;
    var r = p - numberToInt(p);
    var i = numberToInt(p) % this.sequence.length;
    this.index = i + r;
    var n = this.sequence[i];
    var osc = 0.0;
    if (n >= 0) {
      var f = SampleNode.Companion.note_vux9f0$(n + 7 | 0, 0);
      osc = this.osc1_0.next_dleff0$(dt, f) + this.osc2_0.next_dleff0$(dt, f / 2.0) + this.osc3_0.next_dleff0$(dt, f / 2.0) + this.osc4_0.next_dleff0$(dt, f * 3.0);
    }return this.moodFilter_0.filter_7b5o5w$(this.lfo1_0.next_mx4ult$(dt) + 1050, this.lfo2_0.next_mx4ult$(dt), SampleNode.Companion.perc_7b5o5w$(osc, 48.0, this.t % 0.125), dt) * 0.25;
  };
  Melody.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Melody',
    interfaces: [SampleNode]
  };
  function SequenceButton(col, row, melody, root) {
    Button.call(this, 'seq-' + col + '-' + row, root);
    this.col = col;
    this.row = row;
    this.melody = melody;
    this.background_0 = new SequenceButtonUi(this);
    this.colorAnimator_0 = new CosAnimator(new InterpolatedColor(MutableColor_init_0(Color.Companion.WHITE.withAlpha_mx4ult$(0.2)), MutableColor_init_0(Color.Companion.LIME.withAlpha_mx4ult$(0.6))));
    this.wasHovered_0 = false;
    this.layoutSpec.setOrigin_4ujscr$(dps(this.col * 20.0), dps(this.row * 15.0), zero());
    this.layoutSpec.setSize_4ujscr$(dps(18.0), dps(13.0), full());
    this.ui.setCustom_11rb$(this.background_0);
    this.colorAnimator_0.duration = 0.3;
    this.colorAnimator_0.speed = -1.0;
    this.onHover.add_11rb$(SequenceButton_init$lambda(this));
    this.onHoverExit.add_11rb$(SequenceButton_init$lambda_0(this));
  }
  SequenceButton.prototype.onHover_0 = function (ptr) {
    if (ptr.isLeftButtonEvent && ptr.isLeftButtonDown) {
      if (this.melody.sequence[this.col] === this.row) {
        this.melody.sequence[this.col] = -1;
      } else {
        this.melody.sequence[this.col] = this.row;
      }
    } else if (!this.wasHovered_0 && ptr.isLeftButtonDown) {
      this.melody.sequence[this.col] = this.row;
    }this.wasHovered_0 = true;
  };
  SequenceButton.prototype.render_aemszp$ = function (ctx) {
    if (this.melody.sequence[this.col] === this.row) {
      this.colorAnimator_0.speed = 1.0;
    } else {
      this.colorAnimator_0.speed = -1.0;
    }
    this.colorAnimator_0.tick_aemszp$(ctx);
    this.background_0.bgColor.set_d7aj7k$(this.colorAnimator_0.value.value);
    if (this.isHovered) {
      this.background_0.bgColor.a = this.background_0.bgColor.a + 0.4;
    } else {
      var a = this.melody.index - this.col;
      if (a > 0 && a <= 1) {
        var tmp$ = this.background_0.bgColor;
        var tmp$_0 = this.background_0.bgColor.a;
        var x = a - 0.5;
        var $receiver = 0.5 - Math_0.abs(x);
        var clamp$result;
        if ($receiver < 0.0) {
          clamp$result = 0.0;
        } else if ($receiver > 0.1) {
          clamp$result = 0.1;
        } else {
          clamp$result = $receiver;
        }
        tmp$.a = tmp$_0 + clamp$result;
      }}
    Button.prototype.render_aemszp$.call(this, ctx);
  };
  function SequenceButton_init$lambda(this$SequenceButton) {
    return function ($receiver, ptr, f, f_0) {
      this$SequenceButton.onHover_0(ptr);
      return Unit;
    };
  }
  function SequenceButton_init$lambda_0(this$SequenceButton) {
    return function ($receiver, f, f_0, f_1) {
      this$SequenceButton.wasHovered_0 = false;
      return Unit;
    };
  }
  SequenceButton.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SequenceButton',
    interfaces: [Button]
  };
  function SequenceButtonUi(btn) {
    SimpleComponentUi.call(this, btn);
    this.bgColor = MutableColor_init();
  }
  SequenceButtonUi.prototype.onRender_aemszp$ = function (ctx) {
    SimpleComponentUi.prototype.onRender_aemszp$.call(this, ctx);
  };
  SequenceButtonUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SequenceButtonUi',
    interfaces: [SimpleComponentUi]
  };
  function SynthieScene(ctx) {
    Scene_init.call(this);
    this.melody = new Melody();
    this.shaker = new Shaker(60.0);
    this.kick = new Kick(120.0);
    this.pad = new Pad();
    this.audioGen_0 = null;
    this.waveform_0 = new SynthieScene$Waveform(this, 2048, 48000);
    this.unaryPlus_uv0sim$(this.waveform_0);
    this.unaryPlus_uv0sim$(new SynthieScene$Heightmap(this, 256, 256));
    this.unaryPlus_uv0sim$(orbitInputTransform(void 0, SynthieScene_init$lambda(this)));
    this.audioGen_0 = new AudioGenerator(ctx, SynthieScene_init$lambda_0(this));
    this.audioGen_0.enableFftComputation_za3lpa$(1024);
  }
  SynthieScene.prototype.nextSample_0 = function (dt) {
    var sample = this.kick.next_mx4ult$(dt) + this.shaker.next_mx4ult$(dt) + this.pad.next_mx4ult$(dt) + this.melody.next_mx4ult$(dt);
    this.waveform_0.updateSample_mx4ult$(sample);
    return sample;
  };
  SynthieScene.prototype.dispose_aemszp$ = function (ctx) {
    this.audioGen_0.stop();
    Scene_init.prototype.dispose_aemszp$.call(this, ctx);
  };
  function SynthieScene$Heightmap($outer, width, length) {
    this.$outer = $outer;
    TransformGroup.call(this);
    this.width = width;
    this.length = length;
    this.quads = colorMesh(void 0, SynthieScene$Heightmap$quads$lambda(this));
    this.quadV = this.quads.geometry.get_za3lpa$(0);
    this.zPos = -1000.0;
    this.sampleInterval = 0.05;
    this.nextSample = 0.0;
    this.unaryPlus_uv0sim$(this.quads);
    this.onPreRender.add_11rb$(SynthieScene$SynthieScene$Heightmap_init$lambda(this));
  }
  SynthieScene$Heightmap.prototype.updateQuads_aemszp$ = function (ctx) {
    var tmp$;
    this.nextSample -= ctx.deltaT;
    if (this.nextSample <= 0) {
      var a = this.sampleInterval;
      var b = -this.nextSample;
      this.nextSample += Math_0.max(a, b);
      var freqData = this.$outer.audioGen_0.getPowerSpectrum();
      tmp$ = this.width;
      for (var i = 0; i < tmp$; i++) {
        var $receiver = freqData.get_za3lpa$(i) / 90.0;
        var min = -1.0;
        var clamp$result;
        if ($receiver < min) {
          clamp$result = min;
        } else if ($receiver > 0.0) {
          clamp$result = 0.0;
        } else {
          clamp$result = $receiver;
        }
        var c = clamp$result + 1.0;
        var h = c * 50.0;
        var x = i - this.width * 0.5;
        var color = ColorGradient.Companion.VIRIDIS.getColor_y2kzbl$(c + 0.05, 0.0, 0.7);
        this.quadV.position.set_y2kzbl$(x, h, this.zPos);
        this.quadV.color.set_d7aj7k$(color);
        var tmp$_0;
        tmp$_0 = this.quadV;
        tmp$_0.index = tmp$_0.index + 1 | 0;
        this.quadV.position.set_y2kzbl$(x, h, this.zPos + 0.9);
        this.quadV.color.set_d7aj7k$(color);
        var tmp$_1;
        tmp$_1 = this.quadV;
        tmp$_1.index = tmp$_1.index + 1 | 0;
        this.quadV.position.set_y2kzbl$(x + 0.9, h, this.zPos + 0.9);
        this.quadV.color.set_d7aj7k$(color);
        var tmp$_2;
        tmp$_2 = this.quadV;
        tmp$_2.index = tmp$_2.index + 1 | 0;
        this.quadV.position.set_y2kzbl$(x + 0.9, h, this.zPos);
        this.quadV.color.set_d7aj7k$(color);
        var tmp$_3;
        tmp$_3 = this.quadV;
        tmp$_3.index = tmp$_3.index + 1 | 0;
      }
      if (this.quadV.index === (Kotlin.imul(this.width, this.length) * 4 | 0)) {
        this.quadV.index = 0;
      }this.zPos += 1.0;
      if (this.zPos > 1000) {
        this.zPos = -1000.0;
      }this.quads.geometry.hasChanged = true;
      this.quads.bounds.set_w8lrqs$((-this.width | 0) / 2.0, 0.0, this.zPos - this.length, this.width / 2.0, 50.0, this.zPos);
    }this.setIdentity();
    this.scale_y2kzbl$(1.0 / 32.0, 1.0 / 32.0, 1.0 / 32.0);
    this.translate_y2kzbl$(0.0, -32.0, -this.zPos + this.length / 5.0);
  };
  function SynthieScene$Heightmap$quads$lambda$lambda$lambda($receiver) {
    $receiver.normal.set_czzhiu$(Vec3f.Companion.Y_AXIS);
    return Unit;
  }
  function SynthieScene$Heightmap$quads$lambda$lambda(this$Heightmap) {
    return function ($receiver) {
      var tmp$, tmp$_0;
      $receiver.vertexModFun = SynthieScene$Heightmap$quads$lambda$lambda$lambda;
      tmp$ = this$Heightmap.length;
      for (var z = 1; z <= tmp$; z++) {
        tmp$_0 = this$Heightmap.width;
        for (var x = 1; x <= tmp$_0; x++) {
          $receiver.rectProps.defaults().size.set_dleff0$(0.0, 0.0);
          $receiver.rect_e5k3t5$($receiver.rectProps);
        }
      }
      return Unit;
    };
  }
  function SynthieScene$Heightmap$quads$lambda(this$Heightmap) {
    return function ($receiver) {
      $receiver.isFrustumChecked = false;
      $receiver.generate_v2sixm$(SynthieScene$Heightmap$quads$lambda$lambda(this$Heightmap));
      return Unit;
    };
  }
  function SynthieScene$SynthieScene$Heightmap_init$lambda(this$Heightmap) {
    return function ($receiver, ctx) {
      this$Heightmap.updateQuads_aemszp$(ctx);
      return Unit;
    };
  }
  SynthieScene$Heightmap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Heightmap',
    interfaces: [TransformGroup]
  };
  function SynthieScene$Waveform($outer, points, sampleRate) {
    this.$outer = $outer;
    Group.call(this);
    this.points = points;
    this.sampleRate = sampleRate;
    var array = Array_0(5);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = lineMesh(void 0, SynthieScene$Waveform$lines$lambda$lambda(this));
    }
    this.lines = array;
    var array_0 = Array_0(this.lines.length);
    var tmp$_0;
    tmp$_0 = array_0.length - 1 | 0;
    for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
      array_0[i_0] = this.lines[i_0].geometry.get_za3lpa$(0);
    }
    this.vertices = array_0;
    this.sampleBuf = new Float32Array(this.sampleRate);
    this.sampleIdx = 0;
    this.updateFrms = 2;
    this.playT = 0.0;
    this.lineIdx = 0;
  }
  SynthieScene$Waveform.prototype.updateSample_mx4ult$ = function (value) {
    var tmp$;
    this.sampleBuf[tmp$ = this.sampleIdx, this.sampleIdx = tmp$ + 1 | 0, tmp$] = value;
    if (this.sampleIdx === this.sampleBuf.length) {
      this.sampleIdx = 0;
    }};
  SynthieScene$Waveform.prototype.render_aemszp$ = function (ctx) {
    var tmp$;
    this.playT += ctx.deltaT;
    if ((this.updateFrms = this.updateFrms - 1 | 0, this.updateFrms) === 0) {
      this.updateFrms = 2;
      this.lineIdx = (this.lineIdx + 1 | 0) % this.lines.length;
      this.drawTimeDomain_0();
      this.lines[this.lineIdx].geometry.hasChanged = true;
      tmp$ = this.lines;
      for (var i = 0; i !== tmp$.length; ++i) {
        var idx = this.lineIdx - i | 0;
        if (idx < 0) {
          idx = idx + this.lines.length | 0;
        }}
    }Group.prototype.render_aemszp$.call(this, ctx);
  };
  SynthieScene$Waveform.prototype.drawTimeDomain_0 = function () {
    var tmp$, tmp$_0;
    var end = numberToInt(this.playT * this.sampleRate % this.sampleBuf.length);
    var pos = end - this.points | 0;
    if (pos < 0) {
      pos = pos + this.sampleBuf.length | 0;
    }tmp$ = this.points;
    for (var i = 0; i < tmp$; i++) {
      this.vertices[this.lineIdx].index = i;
      this.vertices[this.lineIdx].position.y = this.sampleBuf[tmp$_0 = pos, pos = tmp$_0 + 1 | 0, tmp$_0] * 2.0 + 2.0;
      if (pos >= this.sampleBuf.length) {
        pos = 0;
      }}
  };
  function SynthieScene$Waveform$lines$lambda$lambda(this$Waveform) {
    return function ($receiver) {
      var tmp$;
      this$Waveform.unaryPlus_uv0sim$($receiver);
      tmp$ = this$Waveform.points;
      for (var i = 1; i <= tmp$; i++) {
        var $this = $receiver.geometry;
        var tmp$_0, tmp$_0_0, tmp$_1;
        $this.checkBufferSizes_za3lpa$();
        tmp$_0 = $this.vertexSizeF;
        for (var i_0 = 1; i_0 <= tmp$_0; i_0++) {
          $this.dataF.plusAssign_mx4ult$(0.0);
        }
        tmp$_0_0 = $this.vertexSizeI;
        for (var i_0_0 = 1; i_0_0 <= tmp$_0_0; i_0_0++) {
          $this.dataI.plusAssign_za3lpa$(0);
        }
        $this.vertexIt.index = (tmp$_1 = $this.numVertices, $this.numVertices = tmp$_1 + 1 | 0, tmp$_1);
        var this$Waveform_0 = this$Waveform;
        $this.vertexIt.position.set_y2kzbl$((i - (this$Waveform_0.points / 2 | 0) | 0) / 256.0, 1.0, 0.0);
        $this.bounds.add_czzhiu$($this.vertexIt.position);
        $this.hasChanged = true;
        var idx = $this.numVertices - 1 | 0;
        if (i > 1) {
          $receiver.geometry.addIndices_pmhfmb$(new Int32Array([idx - 1 | 0, idx]));
        }}
      return Unit;
    };
  }
  SynthieScene$Waveform.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Waveform',
    interfaces: [Group]
  };
  function SynthieScene_init$lambda(this$SynthieScene) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$SynthieScene.camera);
      $receiver.setMouseRotation_dleff0$(20.0, -20.0);
      $receiver.zoom = 8.0;
      return Unit;
    };
  }
  function SynthieScene_init$lambda_0(this$SynthieScene) {
    return function ($receiver, dt) {
      return this$SynthieScene.nextSample_0(dt);
    };
  }
  SynthieScene.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SynthieScene',
    interfaces: [Scene_init]
  };
  function simplificationDemo(ctx) {
    return (new SimplificationDemo(ctx)).scenes;
  }
  function SimplificationDemo(ctx) {
    this.simplificationScene = null;
    this.scenes = ArrayList_init();
    this.models = LinkedHashMap_init();
    this.loadingModels = LinkedHashSet_init();
    this.modelWireframe = new LineMesh();
    this.srcModel = null;
    this.dispModel = new Mesh_init(IndexedVertexList_init_0([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]));
    this.heMesh = null;
    this.simplifcationGrade = 1.0;
    this.autoRun_z6bycc$_0 = this.autoRun_z6bycc$_0;
    this.facesValLbl_1vvnvx$_0 = this.facesValLbl_1vvnvx$_0;
    this.vertsValLbl_bpoadb$_0 = this.vertsValLbl_bpoadb$_0;
    this.timeValLbl_itlx8a$_0 = this.timeValLbl_itlx8a$_0;
    this.autoRotate = true;
    var tmp$ = this.dispModel;
    var $receiver = new PbrShader$PbrConfig();
    $receiver.albedoSource = Albedo.STATIC_ALBEDO;
    var $receiver_0 = new PbrShader($receiver);
    $receiver_0.albedo = Color.Companion.WHITE;
    $receiver_0.roughness = 0.15;
    tmp$.pipelineLoader = $receiver_0;
    this.srcModel = this.makeCosGrid_0();
    var $receiver_1 = this.models;
    var value = this.srcModel;
    $receiver_1.put_xwzc9p$('cos', value);
    this.heMesh = new HalfEdgeMesh(this.srcModel);
    this.loadModel_0('bunny.kmfz', 0.05, ctx);
    this.loadModel_0('cow.kmfz', 1.0, ctx);
    this.simplificationScene = this.mainScene_0(ctx);
    var $receiver_2 = this.scenes;
    var element = this.simplificationScene;
    $receiver_2.add_11rb$(element);
    var $receiver_3 = this.scenes;
    var element_0 = this.menu_aemszp$(ctx);
    $receiver_3.add_11rb$(element_0);
    this.simplify();
  }
  Object.defineProperty(SimplificationDemo.prototype, 'autoRun', {
    get: function () {
      if (this.autoRun_z6bycc$_0 == null)
        return throwUPAE('autoRun');
      return this.autoRun_z6bycc$_0;
    },
    set: function (autoRun) {
      this.autoRun_z6bycc$_0 = autoRun;
    }
  });
  Object.defineProperty(SimplificationDemo.prototype, 'facesValLbl', {
    get: function () {
      if (this.facesValLbl_1vvnvx$_0 == null)
        return throwUPAE('facesValLbl');
      return this.facesValLbl_1vvnvx$_0;
    },
    set: function (facesValLbl) {
      this.facesValLbl_1vvnvx$_0 = facesValLbl;
    }
  });
  Object.defineProperty(SimplificationDemo.prototype, 'vertsValLbl', {
    get: function () {
      if (this.vertsValLbl_bpoadb$_0 == null)
        return throwUPAE('vertsValLbl');
      return this.vertsValLbl_bpoadb$_0;
    },
    set: function (vertsValLbl) {
      this.vertsValLbl_bpoadb$_0 = vertsValLbl;
    }
  });
  Object.defineProperty(SimplificationDemo.prototype, 'timeValLbl', {
    get: function () {
      if (this.timeValLbl_itlx8a$_0 == null)
        return throwUPAE('timeValLbl');
      return this.timeValLbl_itlx8a$_0;
    },
    set: function (timeValLbl) {
      this.timeValLbl_itlx8a$_0 = timeValLbl;
    }
  });
  function SimplificationDemo$mainScene$lambda$lambda$lambda(this$SimplificationDemo, closure$ctx, this$) {
    return function ($receiver, it) {
      if (this$SimplificationDemo.autoRotate) {
        this$.rotate_ad55pp$(closure$ctx.deltaT * 3.0, Vec3f.Companion.Y_AXIS);
      }return Unit;
    };
  }
  function SimplificationDemo$mainScene$lambda$lambda(this$SimplificationDemo, closure$ctx) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.dispModel);
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.modelWireframe);
      var $receiver_0 = $receiver.onPreRender;
      var element = SimplificationDemo$mainScene$lambda$lambda$lambda(this$SimplificationDemo, closure$ctx, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  SimplificationDemo.prototype.mainScene_0 = function (ctx) {
    var $receiver = new Scene_init(null);
    defaultCamTransform($receiver);
    var $receiver_0 = $receiver.lighting.lights;
    $receiver_0.clear();
    $receiver_0.add_11rb$((new Light()).setDirectional_czzhiu$(new Vec3f(-1.0, -1.0, 1.0)).setColor_y83vuj$(Color.Companion.MD_RED.mix_y83vuj$(Color.Companion.WHITE, 0.25).toLinear(), 2.0));
    $receiver_0.add_11rb$((new Light()).setDirectional_czzhiu$(new Vec3f(1.0, -1.0, -1.0)).setColor_y83vuj$(Color.Companion.MD_CYAN.mix_y83vuj$(Color.Companion.WHITE, 0.25).toLinear(), 2.0));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, SimplificationDemo$mainScene$lambda$lambda(this, ctx)));
    return $receiver;
  };
  SimplificationDemo.prototype.simplify = function () {
    var pt = new PerfTimer();
    var $this = this.dispModel.geometry;
    var wasBatchUpdate = $this.isBatchUpdate;
    $this.isBatchUpdate = true;
    this.dispModel.geometry.clear();
    this.dispModel.geometry.addGeometry_r7nl2o$(this.srcModel);
    this.heMesh = new HalfEdgeMesh(this.dispModel.geometry, new ListEdgeHandler());
    if (this.simplifcationGrade < 0.999) {
      simplify(this.heMesh, terminateOnFaceCountRel(this.simplifcationGrade));
    }var $this_0 = this.modelWireframe.geometry;
    var wasBatchUpdate_0 = $this_0.isBatchUpdate;
    $this_0.isBatchUpdate = true;
    this.modelWireframe.clear();
    this.heMesh.generateWireframe_6olbr4$(this.modelWireframe, Color.Companion.MD_LIME);
    $this_0.hasChanged = true;
    $this_0.isBatchUpdate = wasBatchUpdate_0;
    if (false) {
      $this_0.rebuildBounds();
    }var time = pt.takeSecs();
    if (time > 0.5) {
      this.autoRun.isEnabled = false;
    }this.facesValLbl.text = this.heMesh.faceCount.toString();
    this.vertsValLbl.text = this.heMesh.vertCount.toString();
    this.timeValLbl.text = toString(time, 2) + ' s';
    $this.hasChanged = true;
    $this.isBatchUpdate = wasBatchUpdate;
    if (false) {
      $this.rebuildBounds();
    }};
  function SimplificationDemo$loadModel$lambda(closure$scale, this$SimplificationDemo, closure$name) {
    return function (model) {
      var tmp$;
      if (model != null) {
        var mesh = model.meshes.get_za3lpa$(0).toMesh_8p8ifh$();
        var geometry = mesh.geometry;
        tmp$ = geometry.numVertices;
        for (var i = 0; i < tmp$; i++) {
          geometry.vertexIt.index = i;
          geometry.vertexIt.position.scale_mx4ult$(closure$scale);
        }
        var $receiver = this$SimplificationDemo.models;
        var key = closure$name;
        $receiver.put_xwzc9p$(key, geometry);
        var $receiver_0 = this$SimplificationDemo.loadingModels;
        var element = closure$name;
        $receiver_0.remove_11rb$(element);
        var $receiver_1 = this$SimplificationDemo;
        var $this = util.Log;
        var level = Log$Level.DEBUG;
        var tag = Kotlin.getKClassFromExpression($receiver_1).simpleName;
        if (level.level >= $this.level.level) {
          var tmp$_0 = $this.printer;
          var closure$name_0 = closure$name;
          var tmp$_1;
          tmp$_0.call($this, level, tag, 'loaded: ' + closure$name_0 + ', bounds: ' + toString_0((tmp$_1 = this$SimplificationDemo.models.get_11rb$(closure$name_0)) != null ? tmp$_1.bounds : null));
        }}return Unit;
    };
  }
  SimplificationDemo.prototype.loadModel_0 = function (name, scale, ctx) {
    this.loadingModels.add_11rb$(name);
    ctx.assetMgr.loadModel_v5uqdg$(name, SimplificationDemo$loadModel$lambda(scale, this, name));
  };
  function SimplificationDemo$makeCosGrid$lambda$lambda(this$) {
    return function (x, y) {
      var fx = (x / this$.stepsX - 0.5) * 10.0;
      var fy = (y / this$.stepsY - 0.5) * 10.0;
      var x_0 = fx * fx + fy * fy;
      var x_1 = Math_0.sqrt(x_0);
      return Math_0.cos(x_1) * 2;
    };
  }
  SimplificationDemo.prototype.makeCosGrid_0 = function () {
    var builder = new MeshBuilder(IndexedVertexList_init_0([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]));
    builder.color = Color.Companion.MD_RED;
    var $receiver = builder.gridProps.defaults();
    $receiver.sizeX = 10.0;
    $receiver.sizeY = 10.0;
    $receiver.stepsX = 30;
    $receiver.stepsY = 30;
    $receiver.heightFun = SimplificationDemo$makeCosGrid$lambda$lambda($receiver);
    builder.grid_gtbnl3$(builder.gridProps);
    return builder.geometry;
  };
  function SimplificationDemo$menu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function SimplificationDemo$menu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(SimplificationDemo$menu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(SimplificationDemo$menu$lambda$lambda$lambda_0);
    return Unit;
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_1(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.text = 'Model';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda(this$SimplificationDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('cow.kmfz')) != null ? tmp$ : this$SimplificationDemo.srcModel;
      this$SimplificationDemo.simplify();
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_2(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_0(this$SimplificationDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('bunny.kmfz')) != null ? tmp$ : this$SimplificationDemo.srcModel;
      this$SimplificationDemo.simplify();
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_3(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_0(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_1(this$SimplificationDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('cos')) != null ? tmp$ : this$SimplificationDemo.srcModel;
      this$SimplificationDemo.simplify();
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_4(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_1(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_5(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = this$SimplificationDemo.dispModel.isVisible;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_6(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = this$SimplificationDemo.modelWireframe.isVisible;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_7(this$SimplificationDemo, closure$tbDrawWireframe) {
    return function ($receiver) {
      this$SimplificationDemo.dispModel.isVisible = $receiver.isEnabled;
      if (!$receiver.isEnabled && !closure$tbDrawWireframe.isEnabled) {
        closure$tbDrawWireframe.isEnabled = true;
      }return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_8(this$SimplificationDemo, closure$tbDrawSolid) {
    return function ($receiver) {
      this$SimplificationDemo.modelWireframe.isVisible = $receiver.isEnabled;
      if (!$receiver.isEnabled && !closure$tbDrawSolid.isEnabled) {
        closure$tbDrawSolid.isEnabled = true;
      }return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_2(this$SimplificationDemo) {
    return function ($receiver) {
      this$SimplificationDemo.autoRotate = $receiver.isEnabled;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_9(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = this$SimplificationDemo.autoRotate;
      var $receiver_0 = $receiver.onStateChange;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_2(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_10(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_11(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_12(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '100 %';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_3(closure$faceCntVal, this$SimplificationDemo) {
    return function ($receiver, value) {
      closure$faceCntVal.text = toString_1(value * 100.0, 0) + ' %';
      this$SimplificationDemo.simplifcationGrade = value;
      if (this$SimplificationDemo.autoRun.isEnabled) {
        this$SimplificationDemo.simplify();
      }return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_13(closure$y, closure$faceCntVal, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.01, 1.0, 1.0);
      var $receiver_0 = $receiver.onValueChanged;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_3(closure$faceCntVal, this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_4(this$SimplificationDemo) {
    return function ($receiver, f, f_0, f_1) {
      this$SimplificationDemo.simplify();
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_14(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_4(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_15(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.isEnabled = true;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_16(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_17(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_18(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_19(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_20(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_21(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda_0(closure$smallFont, this$, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-450.0), dps(-670.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(330.0), dps(550.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lights', SimplificationDemo$menu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Cow', SimplificationDemo$menu$lambda$lambda$lambda_2(y, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Bunny', SimplificationDemo$menu$lambda$lambda$lambda_3(y, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Cosine Grid', SimplificationDemo$menu$lambda$lambda$lambda_4(y, this$SimplificationDemo)));
      y.v -= 45.0;
      var tbDrawSolid = this$.toggleButton_6j87po$('Draw Solid', SimplificationDemo$menu$lambda$lambda$lambda_5(y, this$SimplificationDemo));
      y.v -= 35.0;
      var tbDrawWireframe = this$.toggleButton_6j87po$('Draw Wireframe', SimplificationDemo$menu$lambda$lambda$lambda_6(y, this$SimplificationDemo));
      var $receiver_0 = tbDrawSolid.onStateChange;
      var element = SimplificationDemo$menu$lambda$lambda$lambda_7(this$SimplificationDemo, tbDrawWireframe);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = tbDrawWireframe.onStateChange;
      var element_0 = SimplificationDemo$menu$lambda$lambda$lambda_8(this$SimplificationDemo, tbDrawSolid);
      $receiver_1.add_11rb$(element_0);
      $receiver.unaryPlus_uv0sim$(tbDrawSolid);
      $receiver.unaryPlus_uv0sim$(tbDrawWireframe);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', SimplificationDemo$menu$lambda$lambda$lambda_9(y, this$SimplificationDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Simplification', SimplificationDemo$menu$lambda$lambda$lambda_10(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Ratio:', SimplificationDemo$menu$lambda$lambda$lambda_11(y)));
      var faceCntVal = this$.label_tokfmu$('faceCntVal', SimplificationDemo$menu$lambda$lambda$lambda_12(y));
      $receiver.unaryPlus_uv0sim$(faceCntVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('faceCnt', SimplificationDemo$menu$lambda$lambda$lambda_13(y, faceCntVal, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Update Mesh', SimplificationDemo$menu$lambda$lambda$lambda_14(y, this$SimplificationDemo)));
      y.v -= 35.0;
      this$SimplificationDemo.autoRun = this$.toggleButton_6j87po$('Auto Update', SimplificationDemo$menu$lambda$lambda$lambda_15(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.autoRun);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Faces:', SimplificationDemo$menu$lambda$lambda$lambda_16(y)));
      this$SimplificationDemo.facesValLbl = this$.label_tokfmu$('facesValLbl', SimplificationDemo$menu$lambda$lambda$lambda_17(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.facesValLbl);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Vertices:', SimplificationDemo$menu$lambda$lambda$lambda_18(y)));
      this$SimplificationDemo.vertsValLbl = this$.label_tokfmu$('verticesValLbl', SimplificationDemo$menu$lambda$lambda$lambda_19(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.vertsValLbl);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Time:', SimplificationDemo$menu$lambda$lambda$lambda_20(y)));
      this$SimplificationDemo.timeValLbl = this$.label_tokfmu$('timeValLbl', SimplificationDemo$menu$lambda$lambda$lambda_21(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.timeValLbl);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda(closure$ctx, this$SimplificationDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, SimplificationDemo$menu$lambda$lambda);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', SimplificationDemo$menu$lambda$lambda_0(smallFont, $receiver, this$SimplificationDemo)));
      return Unit;
    };
  }
  SimplificationDemo.prototype.menu_aemszp$ = function (ctx) {
    return uiScene(void 0, void 0, void 0, SimplificationDemo$menu$lambda(ctx, this));
  };
  SimplificationDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimplificationDemo',
    interfaces: []
  };
  var ShaderModel$findNode$lambda = wrapFunction(function () {
    var equals = Kotlin.equals;
    var throwCCE = Kotlin.throwCCE;
    return function (closure$stage, closure$name, typeClosure$T, isT) {
      return function (it) {
        if ((it.stage.mask & closure$stage.mask) !== 0) {
          var isT_0 = isT;
          var name = closure$name;
          var tmp$;
          var $receiver = it.nodes;
          var firstOrNull$result;
          firstOrNull$break: do {
            var tmp$_0;
            tmp$_0 = $receiver.iterator();
            while (tmp$_0.hasNext()) {
              var element = tmp$_0.next();
              if (equals(element.name, name) && isT_0(element)) {
                firstOrNull$result = element;
                break firstOrNull$break;
              }}
            firstOrNull$result = null;
          }
           while (false);
          var node = (tmp$ = firstOrNull$result) == null || isT_0(tmp$) ? tmp$ : throwCCE();
          if (node != null) {
            return node;
          }}return Unit;
      };
    };
  });
  function treeScene$lambda$lambda(closure$shadowMaps, closure$ctx) {
    return function ($receiver, it) {
      var $receiver_0 = closure$shadowMaps;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var closure$ctx_0 = closure$ctx;
        var $receiver_1 = closure$ctx_0.offscreenPasses;
        var element_0 = element.offscreenPass;
        $receiver_1.remove_11rb$(element_0);
        element.dispose_aemszp$(closure$ctx_0);
      }
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda(closure$treeGen) {
    return function ($receiver) {
      var level;
      level = Log$Level.INFO;
      var t = now();
      closure$treeGen.buildTrunkMesh_84rojv$($receiver);
      var ret = Unit;
      var $this = util.Log;
      var tag = Kotlin.getKClassFromExpression($receiver).simpleName;
      if (level.level >= $this.level.level) {
        $this.printer(level, tag, 'Generated ' + ($receiver.geometry.numIndices / 3 | 0) + ' trunk triangles in' + ' ' + toString(now() - t, 3) + ' ms');
      }return Unit;
    };
  }
  function Coroutine$treeScene$lambda$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$treeScene$lambda$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$treeScene$lambda$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$treeScene$lambda$lambda$lambda$lambda.prototype.constructor = Coroutine$treeScene$lambda$lambda$lambda$lambda;
  Coroutine$treeScene$lambda$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/bark_pine/Bark_Pine_baseColor.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function treeScene$lambda$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$treeScene$lambda$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$treeScene$lambda$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$treeScene$lambda$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$treeScene$lambda$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$treeScene$lambda$lambda$lambda$lambda_0.prototype.constructor = Coroutine$treeScene$lambda$lambda$lambda$lambda_0;
  Coroutine$treeScene$lambda$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/bark_pine/Bark_Pine_ambientOcclusion.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function treeScene$lambda$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$treeScene$lambda$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$treeScene$lambda$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$treeScene$lambda$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$treeScene$lambda$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$treeScene$lambda$lambda$lambda$lambda_1.prototype.constructor = Coroutine$treeScene$lambda$lambda$lambda$lambda_1;
  Coroutine$treeScene$lambda$lambda$lambda$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/bark_pine/Bark_Pine_normal.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function treeScene$lambda$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$treeScene$lambda$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$treeScene$lambda$lambda$lambda$lambda_2($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$treeScene$lambda$lambda$lambda$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$treeScene$lambda$lambda$lambda$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$treeScene$lambda$lambda$lambda$lambda_2.prototype.constructor = Coroutine$treeScene$lambda$lambda$lambda$lambda_2;
  Coroutine$treeScene$lambda$lambda$lambda$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/bark_pine/Bark_Pine_roughness.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function treeScene$lambda$lambda$lambda$lambda_2($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$treeScene$lambda$lambda$lambda$lambda_2($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function treeScene$lambda$lambda$lambda$lambda_3(this$) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1;
      (tmp$ = this$.albedoMap) != null ? (tmp$.dispose(), Unit) : null;
      (tmp$_0 = this$.normalMap) != null ? (tmp$_0.dispose(), Unit) : null;
      (tmp$_1 = this$.roughnessMap) != null ? (tmp$_1.dispose(), Unit) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_4(this$, closure$shadowMaps, closure$uWindSpeed, closure$uWindStrength) {
    return function (it) {
      var tmp$, tmp$_0, tmp$_1;
      if ((tmp$ = this$.depthMaps) != null) {
        var tmp$_2, tmp$_0_0;
        var index = 0;
        tmp$_2 = closure$shadowMaps.iterator();
        while (tmp$_2.hasNext()) {
          var item = tmp$_2.next();
          var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
          if (i < tmp$.length) {
            tmp$[i] = item.offscreenPass.impl.depthTexture;
          }}
      }var tmp$_3 = closure$uWindSpeed;
      var $this = this$.model;
      var name = 'windAnim';
      var stage;
      var findNode_3klnlw$result;
      findNode_3klnlw$break: do {
        stage = ShaderStage.ALL;
        var tmp$_4;
        tmp$_4 = $this.stages.values.iterator();
        while (tmp$_4.hasNext()) {
          var element = tmp$_4.next();
          if ((element.stage.mask & stage.mask) !== 0) {
            var tmp$_0_1;
            var $receiver = element.nodes;
            var firstOrNull$result;
            firstOrNull$break: do {
              var tmp$_1_0;
              tmp$_1_0 = $receiver.iterator();
              while (tmp$_1_0.hasNext()) {
                var element_0 = tmp$_1_0.next();
                if (equals(element_0.name, name) && Kotlin.isType(element_0, PushConstantNode1f)) {
                  firstOrNull$result = element_0;
                  break firstOrNull$break;
                }}
              firstOrNull$result = null;
            }
             while (false);
            var node = (tmp$_0_1 = firstOrNull$result) == null || Kotlin.isType(tmp$_0_1, PushConstantNode1f) ? tmp$_0_1 : throwCCE();
            if (node != null) {
              findNode_3klnlw$result = node;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result = null;
      }
       while (false);
      tmp$_3.v = (tmp$_0 = findNode_3klnlw$result) != null ? tmp$_0.uniform : null;
      var tmp$_5 = closure$uWindStrength;
      var $this_0 = this$.model;
      var name_0 = 'windStrength';
      var stage_0;
      var findNode_3klnlw$result_0;
      findNode_3klnlw$break: do {
        stage_0 = ShaderStage.ALL;
        var tmp$_6;
        tmp$_6 = $this_0.stages.values.iterator();
        while (tmp$_6.hasNext()) {
          var element_1 = tmp$_6.next();
          if ((element_1.stage.mask & stage_0.mask) !== 0) {
            var tmp$_0_2;
            var $receiver_0 = element_1.nodes;
            var firstOrNull$result_0;
            firstOrNull$break: do {
              var tmp$_1_1;
              tmp$_1_1 = $receiver_0.iterator();
              while (tmp$_1_1.hasNext()) {
                var element_0_0 = tmp$_1_1.next();
                if (equals(element_0_0.name, name_0) && Kotlin.isType(element_0_0, PushConstantNode1f)) {
                  firstOrNull$result_0 = element_0_0;
                  break firstOrNull$break;
                }}
              firstOrNull$result_0 = null;
            }
             while (false);
            var node_0 = (tmp$_0_2 = firstOrNull$result_0) == null || Kotlin.isType(tmp$_0_2, PushConstantNode1f) ? tmp$_0_2 : throwCCE();
            if (node_0 != null) {
              findNode_3klnlw$result_0 = node_0;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result_0 = null;
      }
       while (false);
      tmp$_5.v = (tmp$_1 = findNode_3klnlw$result_0) != null ? tmp$_1.uniform : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_0(closure$windSpeed, closure$windAnimationPos, closure$uWindSpeed, closure$windStrength, closure$uWindStrength) {
    return function ($receiver, ctx) {
      var tmp$, tmp$_0;
      closure$windAnimationPos.v += ctx.deltaT * closure$windSpeed.v;
      (tmp$ = closure$uWindSpeed.v) != null ? (tmp$.value = closure$windAnimationPos.v) : null;
      (tmp$_0 = closure$uWindStrength.v) != null ? (tmp$_0.value = closure$windStrength.v) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda_0(closure$treeGen, closure$shadowMaps, closure$windSpeed, closure$windAnimationPos, closure$windStrength) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(treeScene$lambda$lambda$lambda(closure$treeGen));
      var uWindSpeed = {v: null};
      var uWindStrength = {v: null};
      var $receiver_0 = new PbrShader$PbrConfig();
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.isNormalMapped = true;
      $receiver_0.isAmbientOcclusionMapped = true;
      $receiver_0.isRoughnessMapped = true;
      $receiver_0.isReceivingShadows = true;
      $receiver_0.maxLights = 2;
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg, treePbrModel(pbrCfg));
      var closure$shadowMaps_0 = closure$shadowMaps;
      $receiver_1.albedoMap = new Texture(void 0, treeScene$lambda$lambda$lambda$lambda);
      $receiver_1.ambientOcclusionMap = new Texture(void 0, treeScene$lambda$lambda$lambda$lambda_0);
      $receiver_1.normalMap = new Texture(void 0, treeScene$lambda$lambda$lambda$lambda_1);
      $receiver_1.roughnessMap = new Texture(void 0, treeScene$lambda$lambda$lambda$lambda_2);
      $receiver_1.ambient = new Color(0.15, 0.15, 0.15);
      $receiver.onDispose.add_11rb$(treeScene$lambda$lambda$lambda$lambda_3($receiver_1));
      $receiver_1.onCreated.add_11rb$(treeScene$lambda$lambda$lambda$lambda_4($receiver_1, closure$shadowMaps_0, uWindSpeed, uWindStrength));
      $receiver.pipelineLoader = $receiver_1;
      var $receiver_2 = $receiver.onPreRender;
      var element = treeScene$lambda$lambda$lambda_0(closure$windSpeed, closure$windAnimationPos, uWindSpeed, closure$windStrength, uWindStrength);
      $receiver_2.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_1(closure$treeGen) {
    return function ($receiver) {
      var level;
      level = Log$Level.INFO;
      var t = now();
      closure$treeGen.buildLeafMesh_84rojv$($receiver);
      var ret = Unit;
      var $this = util.Log;
      var tag = Kotlin.getKClassFromExpression($receiver).simpleName;
      if (level.level >= $this.level.level) {
        $this.printer(level, tag, 'Generated ' + ($receiver.geometry.numIndices / 3 | 0) + ' leaf triangles in' + ' ' + toString(now() - t, 3) + ' ms');
      }return Unit;
    };
  }
  function Coroutine$treeScene$lambda$lambda$lambda$lambda_3($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$treeScene$lambda$lambda$lambda$lambda_3.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$treeScene$lambda$lambda$lambda$lambda_3.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$treeScene$lambda$lambda$lambda$lambda_3.prototype.constructor = Coroutine$treeScene$lambda$lambda$lambda$lambda_3;
  Coroutine$treeScene$lambda$lambda$lambda$lambda_3.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$('leaf.png', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function treeScene$lambda$lambda$lambda$lambda_5($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$treeScene$lambda$lambda$lambda$lambda_3($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function treeScene$lambda$lambda$lambda$lambda_6(this$) {
    return function ($receiver, it) {
      ensureNotNull(this$.albedoMap).dispose();
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_7(it) {
    it.cullMethod = CullMethod.NO_CULLING;
    return Unit;
  }
  function treeScene$lambda$lambda$lambda$lambda_8(this$, closure$shadowMaps, closure$uWindSpeed, closure$uWindStrength) {
    return function (it) {
      var tmp$, tmp$_0, tmp$_1;
      if ((tmp$ = this$.depthMaps) != null) {
        var tmp$_2, tmp$_0_0;
        var index = 0;
        tmp$_2 = closure$shadowMaps.iterator();
        while (tmp$_2.hasNext()) {
          var item = tmp$_2.next();
          var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
          if (i < tmp$.length) {
            tmp$[i] = item.offscreenPass.impl.depthTexture;
          }}
      }var tmp$_3 = closure$uWindSpeed;
      var $this = this$.model;
      var name = 'windAnim';
      var stage;
      var findNode_3klnlw$result;
      findNode_3klnlw$break: do {
        stage = ShaderStage.ALL;
        var tmp$_4;
        tmp$_4 = $this.stages.values.iterator();
        while (tmp$_4.hasNext()) {
          var element = tmp$_4.next();
          if ((element.stage.mask & stage.mask) !== 0) {
            var tmp$_0_1;
            var $receiver = element.nodes;
            var firstOrNull$result;
            firstOrNull$break: do {
              var tmp$_1_0;
              tmp$_1_0 = $receiver.iterator();
              while (tmp$_1_0.hasNext()) {
                var element_0 = tmp$_1_0.next();
                if (equals(element_0.name, name) && Kotlin.isType(element_0, PushConstantNode1f)) {
                  firstOrNull$result = element_0;
                  break firstOrNull$break;
                }}
              firstOrNull$result = null;
            }
             while (false);
            var node = (tmp$_0_1 = firstOrNull$result) == null || Kotlin.isType(tmp$_0_1, PushConstantNode1f) ? tmp$_0_1 : throwCCE();
            if (node != null) {
              findNode_3klnlw$result = node;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result = null;
      }
       while (false);
      tmp$_3.v = (tmp$_0 = findNode_3klnlw$result) != null ? tmp$_0.uniform : null;
      var tmp$_5 = closure$uWindStrength;
      var $this_0 = this$.model;
      var name_0 = 'windStrength';
      var stage_0;
      var findNode_3klnlw$result_0;
      findNode_3klnlw$break: do {
        stage_0 = ShaderStage.ALL;
        var tmp$_6;
        tmp$_6 = $this_0.stages.values.iterator();
        while (tmp$_6.hasNext()) {
          var element_1 = tmp$_6.next();
          if ((element_1.stage.mask & stage_0.mask) !== 0) {
            var tmp$_0_2;
            var $receiver_0 = element_1.nodes;
            var firstOrNull$result_0;
            firstOrNull$break: do {
              var tmp$_1_1;
              tmp$_1_1 = $receiver_0.iterator();
              while (tmp$_1_1.hasNext()) {
                var element_0_0 = tmp$_1_1.next();
                if (equals(element_0_0.name, name_0) && Kotlin.isType(element_0_0, PushConstantNode1f)) {
                  firstOrNull$result_0 = element_0_0;
                  break firstOrNull$break;
                }}
              firstOrNull$result_0 = null;
            }
             while (false);
            var node_0 = (tmp$_0_2 = firstOrNull$result_0) == null || Kotlin.isType(tmp$_0_2, PushConstantNode1f) ? tmp$_0_2 : throwCCE();
            if (node_0 != null) {
              findNode_3klnlw$result_0 = node_0;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result_0 = null;
      }
       while (false);
      tmp$_5.v = (tmp$_1 = findNode_3klnlw$result_0) != null ? tmp$_1.uniform : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_2(closure$windAnimationPos, closure$uWindSpeed, closure$windStrength, closure$uWindStrength) {
    return function ($receiver, ctx) {
      var tmp$, tmp$_0;
      (tmp$ = closure$uWindSpeed.v) != null ? (tmp$.value = closure$windAnimationPos.v) : null;
      (tmp$_0 = closure$uWindStrength.v) != null ? (tmp$_0.value = closure$windStrength.v) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda_1(closure$treeGen, closure$shadowMaps, closure$windAnimationPos, closure$windStrength) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(treeScene$lambda$lambda$lambda_1(closure$treeGen));
      var uWindSpeed = {v: null};
      var uWindStrength = {v: null};
      var $receiver_0 = new PbrShader$PbrConfig();
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.isReceivingShadows = true;
      $receiver_0.maxLights = 2;
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg, treePbrModel(pbrCfg));
      var closure$shadowMaps_0 = closure$shadowMaps;
      $receiver_1.albedoMap = new Texture(void 0, treeScene$lambda$lambda$lambda$lambda_5);
      $receiver_1.roughness = 0.5;
      $receiver_1.ambient = new Color(0.15, 0.15, 0.15);
      $receiver.onDispose.add_11rb$(treeScene$lambda$lambda$lambda$lambda_6($receiver_1));
      $receiver_1.onSetup.add_11rb$(treeScene$lambda$lambda$lambda$lambda_7);
      $receiver_1.onCreated.add_11rb$(treeScene$lambda$lambda$lambda$lambda_8($receiver_1, closure$shadowMaps_0, uWindSpeed, uWindStrength));
      $receiver.pipelineLoader = $receiver_1;
      var $receiver_2 = $receiver.onPreRender;
      var element = treeScene$lambda$lambda$lambda_2(closure$windAnimationPos, uWindSpeed, closure$windStrength, uWindStrength);
      $receiver_2.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_3(closure$autoRotate, this$) {
    return function ($receiver, ctx) {
      if (closure$autoRotate.v) {
        this$.verticalRotation += ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function treeScene$lambda$lambda_2(this$, closure$autoRotate) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.minZoom = 1.0;
      $receiver.maxZoom = 25.0;
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 6.0;
      $receiver.setMouseRotation_dleff0$(0.0, -10.0);
      $receiver.setMouseTranslation_y2kzbl$(0.0, 2.0, 0.0);
      var $receiver_0 = $receiver.onPreRender;
      var element = treeScene$lambda$lambda$lambda_3(closure$autoRotate, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_4(it) {
    return new BlankComponentUi();
  }
  function treeScene$lambda$lambda$lambda_5(it) {
    return new BlankComponentUi();
  }
  function treeScene$lambda$lambda_3($receiver) {
    $receiver.componentUi_mloaa0$(treeScene$lambda$lambda$lambda_4);
    $receiver.containerUi_2t3ptw$(treeScene$lambda$lambda$lambda_5);
    return Unit;
  }
  function treeScene$lambda$lambda$lambda_6(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_7(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_8(closure$y, closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(70.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(30.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = toString_1(closure$treeGen.growDistance, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_9(closure$treeGen, closure$growDistVal) {
    return function ($receiver, value) {
      closure$treeGen.growDistance = value;
      closure$growDistVal.text = toString_1(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_9(closure$y, closure$treeGen, closure$growDistVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.1, 0.4, closure$treeGen.growDistance);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_9(closure$treeGen, closure$growDistVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_10(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_11(closure$y, closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(70.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(30.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = toString_1(closure$treeGen.killDistance, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_10(closure$treeGen, closure$killDistVal) {
    return function ($receiver, value) {
      closure$treeGen.killDistance = value;
      closure$killDistVal.text = toString_1(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_12(closure$y, closure$treeGen, closure$killDistVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(1.0, 4.0, closure$treeGen.killDistance);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_10(closure$treeGen, closure$killDistVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_13(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_14(closure$y, closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(70.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(30.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = closure$treeGen.numberOfAttractionPoints.toString();
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_11(closure$treeGen, closure$attractPtsVal) {
    return function ($receiver, value) {
      closure$treeGen.numberOfAttractionPoints = numberToInt(value);
      closure$attractPtsVal.text = numberToInt(value).toString();
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_15(closure$y, closure$treeGen, closure$attractPtsVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(100.0, 10000.0, closure$treeGen.numberOfAttractionPoints);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_11(closure$treeGen, closure$attractPtsVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_16(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_17(closure$y, closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(70.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(30.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = toString_1(closure$treeGen.radiusOfInfluence, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_12(closure$treeGen, closure$infRadiusVal) {
    return function ($receiver, value) {
      closure$treeGen.radiusOfInfluence = value;
      closure$infRadiusVal.text = toString_1(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_18(closure$y, closure$treeGen, closure$infRadiusVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.25, 10.0, closure$treeGen.radiusOfInfluence);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_12(closure$treeGen, closure$infRadiusVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_13(closure$treeGen, closure$trunkMesh, closure$leafMesh) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$, tmp$_0;
      closure$treeGen.generate_za3lpa$();
      if ((tmp$ = closure$trunkMesh.v) != null) {
        var closure$treeGen_0 = closure$treeGen;
        var $this = tmp$.geometry;
        var wasBatchUpdate = $this.isBatchUpdate;
        $this.isBatchUpdate = true;
        $this.clear();
        var builder = new MeshBuilder($this);
        var level;
        level = Log$Level.INFO;
        var t = now();
        closure$treeGen_0.buildTrunkMesh_84rojv$(builder);
        $this.generateTangents();
        var ret = Unit;
        var $this_0 = util.Log;
        var tag = Kotlin.getKClassFromExpression($this).simpleName;
        if (level.level >= $this_0.level.level) {
          $this_0.printer(level, tag, 'Generated ' + ($this.numIndices / 3 | 0) + ' trunk triangles in' + ' ' + toString(now() - t, 3) + ' ms');
        }$this.hasChanged = true;
        $this.isBatchUpdate = wasBatchUpdate;
        if (false) {
          $this.rebuildBounds();
        }}if ((tmp$_0 = closure$leafMesh.v) != null) {
        var closure$treeGen_1 = closure$treeGen;
        var $this_1 = tmp$_0.geometry;
        var wasBatchUpdate_0 = $this_1.isBatchUpdate;
        $this_1.isBatchUpdate = true;
        $this_1.clear();
        var builder_0 = new MeshBuilder($this_1);
        var level_0;
        level_0 = Log$Level.INFO;
        var t_0 = now();
        closure$treeGen_1.buildLeafMesh_84rojv$(builder_0);
        var ret_0 = Unit;
        var $this_2 = util.Log;
        var tag_0 = Kotlin.getKClassFromExpression($this_1).simpleName;
        if (level_0.level >= $this_2.level.level) {
          $this_2.printer(level_0, tag_0, 'Generated ' + ($this_1.numIndices / 3 | 0) + ' leaf triangles in' + ' ' + toString(now() - t_0, 3) + ' ms');
        }$this_1.hasChanged = true;
        $this_1.isBatchUpdate = wasBatchUpdate_0;
        if (false) {
          $this_1.rebuildBounds();
        }}return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_19(closure$y, closure$treeGen, closure$trunkMesh, closure$leafMesh) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onClick;
      var element = treeScene$lambda$lambda$lambda$lambda_13(closure$treeGen, closure$trunkMesh, closure$leafMesh);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_20(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_21(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_22(closure$y, closure$windSpeed) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(70.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(30.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = toString_1(closure$windSpeed.v, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_14(closure$windSpeed, closure$windSpeedVal) {
    return function ($receiver, value) {
      closure$windSpeed.v = value;
      closure$windSpeedVal.text = toString_1(value, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_23(closure$y, closure$windSpeed, closure$windSpeedVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.0, 10.0, closure$windSpeed.v);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_14(closure$windSpeed, closure$windSpeedVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_24(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_25(closure$y, closure$windStrength) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(70.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(30.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = toString_1(closure$windStrength.v, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_15(closure$windStrength, closure$windStrengthVal) {
    return function ($receiver, value) {
      closure$windStrength.v = value;
      closure$windStrengthVal.text = toString_1(value, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_26(closure$y, closure$treeGen, closure$windStrength, closure$windStrengthVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.0, 5.0, closure$treeGen.radiusOfInfluence);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_15(closure$windStrength, closure$windStrengthVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_16(closure$leafMesh) {
    return function ($receiver) {
      var tmp$;
      (tmp$ = closure$leafMesh.v) != null ? (tmp$.isVisible = $receiver.isEnabled) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_27(closure$y, closure$leafMesh) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = true;
      var $receiver_0 = $receiver.onStateChange;
      var element = treeScene$lambda$lambda$lambda$lambda_16(closure$leafMesh);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_17(closure$autoRotate) {
    return function ($receiver) {
      closure$autoRotate.v = $receiver.isEnabled;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_28(closure$y, closure$autoRotate) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = closure$autoRotate.v;
      var $receiver_0 = $receiver.onStateChange;
      var element = treeScene$lambda$lambda$lambda$lambda_17(closure$autoRotate);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda_4(closure$smallFont, this$, closure$treeGen, closure$trunkMesh, closure$leafMesh, closure$windSpeed, closure$windStrength, closure$autoRotate) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-450.0), dps(-680.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(330.0), dps(560.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Generator Settings', treeScene$lambda$lambda$lambda_6(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Grow Distance:', treeScene$lambda$lambda$lambda_7(y)));
      var growDistVal = this$.label_tokfmu$('growDistVal', treeScene$lambda$lambda$lambda_8(y, closure$treeGen));
      $receiver.unaryPlus_uv0sim$(growDistVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('growDist', treeScene$lambda$lambda$lambda_9(y, closure$treeGen, growDistVal)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Kill Distance:', treeScene$lambda$lambda$lambda_10(y)));
      var killDistVal = this$.label_tokfmu$('killDistVal', treeScene$lambda$lambda$lambda_11(y, closure$treeGen));
      $receiver.unaryPlus_uv0sim$(killDistVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('killDist', treeScene$lambda$lambda$lambda_12(y, closure$treeGen, killDistVal)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Attraction Points:', treeScene$lambda$lambda$lambda_13(y)));
      var attractPtsVal = this$.label_tokfmu$('attractPtsVal', treeScene$lambda$lambda$lambda_14(y, closure$treeGen));
      $receiver.unaryPlus_uv0sim$(attractPtsVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('attractPts', treeScene$lambda$lambda$lambda_15(y, closure$treeGen, attractPtsVal)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Radius of Influence:', treeScene$lambda$lambda$lambda_16(y)));
      var infRadiusVal = this$.label_tokfmu$('infRadiusVal', treeScene$lambda$lambda$lambda_17(y, closure$treeGen));
      $receiver.unaryPlus_uv0sim$(infRadiusVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('killDist', treeScene$lambda$lambda$lambda_18(y, closure$treeGen, infRadiusVal)));
      y.v -= 45.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Generate Tree!', treeScene$lambda$lambda$lambda_19(y, closure$treeGen, closure$trunkMesh, closure$leafMesh)));
      y.v -= 40;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Scene', treeScene$lambda$lambda$lambda_20(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Animation Speed:', treeScene$lambda$lambda$lambda_21(y)));
      var windSpeedVal = this$.label_tokfmu$('windSpeedVal', treeScene$lambda$lambda$lambda_22(y, closure$windSpeed));
      $receiver.unaryPlus_uv0sim$(windSpeedVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('windSpeed', treeScene$lambda$lambda$lambda_23(y, closure$windSpeed, windSpeedVal)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Animation Strength:', treeScene$lambda$lambda$lambda_24(y)));
      var windStrengthVal = this$.label_tokfmu$('windStrengthVal', treeScene$lambda$lambda$lambda_25(y, closure$windStrength));
      $receiver.unaryPlus_uv0sim$(windStrengthVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('windStrength', treeScene$lambda$lambda$lambda_26(y, closure$treeGen, closure$windStrength, windStrengthVal)));
      y.v -= 35;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Toggle Leafs', treeScene$lambda$lambda$lambda_27(y, closure$leafMesh)));
      y.v -= 35;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', treeScene$lambda$lambda$lambda_28(y, closure$autoRotate)));
      return Unit;
    };
  }
  function treeScene$lambda(closure$ctx, closure$treeGen, closure$trunkMesh, closure$leafMesh, closure$windSpeed, closure$windStrength, closure$autoRotate) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, treeScene$lambda$lambda_3);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', treeScene$lambda$lambda_4(smallFont, $receiver, closure$treeGen, closure$trunkMesh, closure$leafMesh, closure$windSpeed, closure$windStrength, closure$autoRotate)));
      return Unit;
    };
  }
  function treeScene(ctx) {
    var scenes = ArrayList_init();
    var w = 3.0;
    var h = 3.5;
    var dist = new TreeTopPointDistribution(1.0 + h / 2.0, w, h);
    var treeGen = new TreeGenerator(dist, void 0, void 0, (new MutableVec3f(-1.0, -1.5, -1.0)).norm());
    treeGen.generate_za3lpa$();
    var trunkMesh = {v: null};
    var leafMesh = {v: null};
    var autoRotate = {v: true};
    var windSpeed = {v: 2.5};
    var windAnimationPos = {v: 0.0};
    var windStrength = {v: 1.0};
    var $receiver = new Scene_init(null);
    var dirLighDirection = new Vec3f(1.0, -1.5, 1.0);
    var spotLightPos = new Vec3f(10.0, 15.0, 10.0);
    var $receiver_0 = $receiver.lighting.lights;
    $receiver_0.clear();
    $receiver_0.add_11rb$((new Light()).setSpot_nve3wz$(spotLightPos, spotLightPos.scale_749b8l$(-1.0, MutableVec3f_init()).norm(), 45.0).setColor_y83vuj$(Color.Companion.YELLOW.mix_y83vuj$(Color.Companion.WHITE, 0.6), 1000.0));
    $receiver_0.add_11rb$((new Light()).setDirectional_czzhiu$(dirLighDirection.norm_5s4mqq$(MutableVec3f_init())).setColor_y83vuj$(Color.Companion.LIGHT_BLUE.mix_y83vuj$(Color.Companion.WHITE, 0.5), 1.0));
    var shadowMaps = mutableListOf([new ShadowMapPass($receiver, $receiver.lighting.lights.get_za3lpa$(0), 2048), new ShadowMapPass($receiver, $receiver.lighting.lights.get_za3lpa$(1), 32)]);
    var tmp$;
    tmp$ = shadowMaps.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var $receiver_1 = ctx.offscreenPasses;
      var element_0 = element.offscreenPass;
      $receiver_1.add_11rb$(element_0);
    }
    $receiver.onDispose.add_11rb$(treeScene$lambda$lambda(shadowMaps, ctx));
    $receiver.unaryPlus_uv0sim$(makeTreeGroundGrid(10, shadowMaps));
    trunkMesh.v = textureMesh(void 0, true, treeScene$lambda$lambda_0(treeGen, shadowMaps, windSpeed, windAnimationPos, windStrength));
    leafMesh.v = textureMesh(void 0, void 0, treeScene$lambda$lambda_1(treeGen, shadowMaps, windAnimationPos, windStrength));
    $receiver.unaryPlus_uv0sim$(ensureNotNull(trunkMesh.v));
    $receiver.unaryPlus_uv0sim$(ensureNotNull(leafMesh.v));
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, treeScene$lambda$lambda_2($receiver, autoRotate)));
    var treeScene = $receiver;
    scenes.add_11rb$(treeScene);
    var element_1 = uiScene(void 0, void 0, void 0, treeScene$lambda(ctx, treeGen, trunkMesh, leafMesh, windSpeed, windStrength, autoRotate));
    scenes.add_11rb$(element_1);
    return scenes;
  }
  function WindNode(graph) {
    ShaderNode.call(this, 'windNode', graph);
    this.inputPos = new ShaderNodeIoVar(new ModelVar3fConst(Vec3f.Companion.ZERO));
    this.inputAnim = new ShaderNodeIoVar(new ModelVar1fConst(0.0));
    this.inputStrength = new ShaderNodeIoVar(new ModelVar1fConst(1.0));
    this.outputPos = new ShaderNodeIoVar(new ModelVar3f('windOutPos'), this);
  }
  WindNode.prototype.setup_llmhyc$ = function (shaderGraph) {
    ShaderNode.prototype.setup_llmhyc$.call(this, shaderGraph);
    this.dependsOn_8ak6wm$([this.inputPos, this.inputAnim, this.inputStrength]);
  };
  WindNode.prototype.generateCode_626509$ = function (generator) {
    generator.appendMain_61zpoe$('\n' + '            float r = clamp(sqrt(' + this.inputPos.ref3f() + '.x * ' + this.inputPos.ref3f() + '.x + ' + this.inputPos.ref3f() + '.z * ' + this.inputPos.ref3f() + '.z) - 0.25, 0.0, 2.0) + ' + '\n' + '                        clamp(' + this.inputPos.ref3f() + '.z - 1.0, 0.0, 1.0);' + '\n' + '            float windTx = cos(' + this.inputPos.ref3f() + '.x * 10.0 + ' + this.inputPos.ref3f() + '.y * 2.0 + ' + this.inputAnim.ref1f() + ') * r * 0.01 * ' + this.inputStrength.ref1f() + ';' + '\n' + '            float windTz = sin(' + this.inputPos.ref3f() + '.z * 10.0 - ' + this.inputPos.ref3f() + '.y * 2.0 + ' + this.inputAnim.ref1f() + ' * 1.1f) * r * 0.01 * ' + this.inputStrength.ref1f() + ';' + '\n' + '            ' + this.outputPos.declare() + ' = ' + this.inputPos.ref3f() + ' + vec3(windTx, 0.0, windTz);' + '\n' + '        ');
  };
  WindNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WindNode',
    interfaces: [ShaderNode]
  };
  function treePbrModel(cfg) {
    var $receiver = new ShaderModel('treePbrModel()');
    var ifColors = {v: null};
    var ifNormals = {v: null};
    var ifTangents = {v: null};
    var ifFragPos = {v: null};
    var ifTexCoords = {v: null};
    var mvp = {v: null};
    var shadowedLightNode = {v: null};
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
    mvp.v = $receiver_0.mvpNode();
    var nrm = $receiver_0.transformNode_vid4wo$($receiver_0.attrNormals().output, mvp.v.outModelMat, 0.0);
    ifNormals.v = $receiver_0.stageInterfaceNode_wtmwsg$('ifNormals', nrm.output);
    if (cfg.requiresTexCoords()) {
      tmp$ = $receiver_0.stageInterfaceNode_wtmwsg$('ifTexCoords', $receiver_0.attrTexCoords().output);
    } else {
      tmp$ = null;
    }
    ifTexCoords.v = tmp$;
    if (cfg.isDisplacementMapped) {
      var dispTex = $receiver_0.textureNode_61zpoe$('tDisplacement');
      var $receiver_1 = $receiver_0.displacementMapNode_7fvjbk$(dispTex, ensureNotNull(ifTexCoords.v).input, $receiver_0.attrPositions().output, $receiver_0.attrNormals().output);
      $receiver_1.inStrength = $receiver_0.pushConstantNode1f_61zpoe$('uDispStrength').output;
      var dispNd = $receiver_1;
      tmp$_0 = dispNd.outPosition;
    } else {
      tmp$_0 = $receiver_0.attrPositions().output;
    }
    var staticWorldPos = tmp$_0;
    var $receiver_2 = $receiver_0.addNode_u9w9by$(new WindNode($receiver.vertexStageGraph));
    $receiver_2.inputPos = staticWorldPos;
    $receiver_2.inputAnim = $receiver_0.pushConstantNode1f_61zpoe$('windAnim').output;
    $receiver_2.inputStrength = $receiver_0.pushConstantNode1f_61zpoe$('windStrength').output;
    var windNd = $receiver_2;
    var worldPos = windNd.outputPos;
    var pos = $receiver_0.transformNode_vid4wo$(worldPos, mvp.v.outModelMat, 1.0).output;
    ifFragPos.v = $receiver_0.stageInterfaceNode_wtmwsg$('ifFragPos', pos);
    if (cfg.albedoSource === Albedo.VERTEX_ALBEDO) {
      tmp$_1 = $receiver_0.stageInterfaceNode_wtmwsg$('ifColors', $receiver_0.attrColors().output);
    } else {
      tmp$_1 = null;
    }
    ifColors.v = tmp$_1;
    if (cfg.isNormalMapped) {
      var tan = $receiver_0.transformNode_vid4wo$($receiver_0.attrTangents().output, mvp.v.outModelMat, 0.0);
      tmp$_2 = $receiver_0.stageInterfaceNode_wtmwsg$('ifTangents', tan.output);
    } else {
      tmp$_2 = null;
    }
    ifTangents.v = tmp$_2;
    if (cfg.isReceivingShadows) {
      tmp$_3 = $receiver_0.shadowedLightNode_jciouo$(worldPos, mvp.v.outModelMat, 'depthMap', cfg.maxLights);
    } else {
      tmp$_3 = null;
    }
    shadowedLightNode.v = tmp$_3;
    $receiver_0.positionOutput = $receiver_0.vertexPositionNode_ze33is$(worldPos, mvp.v.outMvpMat).outPosition;
    var $receiver_3 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    var tmp$_4, tmp$_5;
    var mvpFrag = mvp.v.addToStage_llmhyc$($receiver.fragmentStageGraph);
    var lightNode = (tmp$_5 = (tmp$_4 = shadowedLightNode.v) != null ? tmp$_4.fragmentNode : null) != null ? tmp$_5 : $receiver_3.defaultLightNode_za3lpa$(cfg.maxLights);
    var reflMap;
    var brdfLut;
    var irrSampler;
    if (cfg.isImageBasedLighting) {
      var irrMap = $receiver_3.cubeMapNode_61zpoe$('irradianceMap');
      irrSampler = $receiver_3.cubeMapSamplerNode_2z3a2t$(irrMap, ifNormals.v.output, false);
      reflMap = $receiver_3.cubeMapNode_61zpoe$('reflectionMap');
      brdfLut = $receiver_3.textureNode_61zpoe$('brdfLut');
    } else {
      irrSampler = null;
      reflMap = null;
      brdfLut = null;
    }
    var $receiver_4 = $receiver_3.pbrMaterialNode_od0lt5$(lightNode, reflMap, brdfLut);
    var closure$irrSampler = irrSampler;
    var tmp$_6, tmp$_7, tmp$_8, tmp$_9, tmp$_10;
    $receiver_4.inFragPos = ifFragPos.v.output;
    $receiver_4.inCamPos = mvpFrag.outCamPos;
    $receiver_4.inIrradiance = (tmp$_6 = closure$irrSampler != null ? closure$irrSampler.outColor : null) != null ? tmp$_6 : $receiver_3.pushConstantNodeColor_61zpoe$('uAmbient').output;
    switch (cfg.albedoSource.name) {
      case 'VERTEX_ALBEDO':
        tmp$_7 = ensureNotNull(ifColors.v).output;
        break;
      case 'STATIC_ALBEDO':
        tmp$_7 = $receiver_3.pushConstantNodeColor_61zpoe$('uAlbedo').output;
        break;
      case 'TEXTURE_ALBEDO':
        var albedoSampler = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$('tAlbedo'), ensureNotNull(ifTexCoords.v).output, false);
        var albedoLin = $receiver_3.gammaNode_r20yfm$(albedoSampler.outColor);
        tmp$_7 = albedoLin.outColor;
        break;
      default:tmp$_7 = Kotlin.noWhenBranchMatched();
        break;
    }
    $receiver_4.inAlbedo = tmp$_7;
    if (cfg.isNormalMapped && ifTangents.v != null) {
      var bumpNormal = $receiver_3.normalMapNode_j8913i$($receiver_3.textureNode_61zpoe$('tNormal'), ensureNotNull(ifTexCoords.v).output, ifNormals.v.output, ifTangents.v.output);
      bumpNormal.inStrength = new ShaderNodeIoVar(new ModelVar1fConst(cfg.normalStrength));
      tmp$_8 = bumpNormal.outNormal;
    } else {
      tmp$_8 = ifNormals.v.output;
    }
    $receiver_4.inNormal = tmp$_8;
    if (cfg.isMetallicMapped) {
      tmp$_9 = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$('tMetallic'), ensureNotNull(ifTexCoords.v).output, false).outColor;
    } else {
      tmp$_9 = $receiver_3.pushConstantNode1f_61zpoe$('uMetallic').output;
    }
    $receiver_4.inMetallic = tmp$_9;
    if (cfg.isRoughnessMapped) {
      tmp$_10 = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$('tRoughness'), ensureNotNull(ifTexCoords.v).output, false).outColor;
    } else {
      tmp$_10 = $receiver_3.pushConstantNode1f_61zpoe$('uRoughness').output;
    }
    $receiver_4.inRoughness = tmp$_10;
    if (cfg.isAmbientOcclusionMapped) {
      $receiver_4.inAmbientOccl = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$('tAmbOccl'), ensureNotNull(ifTexCoords.v).output, false).outColor;
    }var mat = $receiver_4;
    var hdrToLdr = $receiver_3.hdrToLdrNode_r20yfm$(mat.outColor);
    $receiver_3.colorOutput = hdrToLdr.outColor;
    return $receiver;
  }
  function makeTreeGroundGrid$lambda$lambda$lambda$lambda($receiver) {
    $receiver.texCoord.set_dleff0$($receiver.position.x, $receiver.position.z).scale_mx4ult$(0.2);
    return Unit;
  }
  function makeTreeGroundGrid$lambda$lambda$lambda$lambda$lambda(this$) {
    return function (x, y) {
      var fx = (x / this$.stepsX - 0.5) * 7.0;
      var fy = (y / this$.stepsY - 0.5) * 7.0;
      var x_0 = fx * fx + fy * fy;
      var x_1 = Math_0.sqrt(x_0);
      return Math_0.cos(x_1) * 0.2 - 0.2;
    };
  }
  function makeTreeGroundGrid$lambda$lambda(closure$groundExt) {
    return function ($receiver) {
      $receiver.transform.push();
      var closure$groundExt_0 = closure$groundExt;
      $receiver.color = Color.Companion.LIGHT_GRAY.withAlpha_mx4ult$(0.2);
      $receiver.vertexModFun = makeTreeGroundGrid$lambda$lambda$lambda$lambda;
      var $receiver_0 = $receiver.gridProps.defaults();
      $receiver_0.sizeX = closure$groundExt_0 * 2.0;
      $receiver_0.sizeY = closure$groundExt_0 * 2.0;
      $receiver_0.stepsX = 200;
      $receiver_0.stepsY = 200;
      $receiver_0.heightFun = makeTreeGroundGrid$lambda$lambda$lambda$lambda$lambda($receiver_0);
      $receiver.grid_gtbnl3$($receiver.gridProps);
      $receiver.transform.pop();
      $receiver.geometry.generateTangents();
      return Unit;
    };
  }
  function Coroutine$makeTreeGroundGrid$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda.prototype.constructor = Coroutine$makeTreeGroundGrid$lambda$lambda$lambda;
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeTreeGroundGrid$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeTreeGroundGrid$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0.prototype.constructor = Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0;
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_mud_leaves_01/brown_mud_leaves_01_Nor_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeTreeGroundGrid$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1.prototype.constructor = Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1;
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeTreeGroundGrid$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2.prototype.constructor = Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2;
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_mud_leaves_01/brown_mud_leaves_01_AO_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeTreeGroundGrid$lambda$lambda$lambda_2($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_2($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3.prototype.constructor = Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3;
  Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function makeTreeGroundGrid$lambda$lambda$lambda_3($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$makeTreeGroundGrid$lambda$lambda$lambda_3($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function makeTreeGroundGrid$lambda$lambda$lambda_4(this$, closure$shadowMaps) {
    return function (it) {
      var tmp$;
      if ((tmp$ = this$.depthMaps) != null) {
        var tmp$_0, tmp$_0_0;
        var index = 0;
        tmp$_0 = closure$shadowMaps.iterator();
        while (tmp$_0.hasNext()) {
          var item = tmp$_0.next();
          var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
          if (i < tmp$.length) {
            tmp$[i] = item.offscreenPass.impl.depthTexture;
          }}
      }return Unit;
    };
  }
  function makeTreeGroundGrid$lambda$lambda$lambda_5(this$) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
      (tmp$ = this$.albedoMap) != null ? (tmp$.dispose(), Unit) : null;
      (tmp$_0 = this$.normalMap) != null ? (tmp$_0.dispose(), Unit) : null;
      (tmp$_1 = this$.roughnessMap) != null ? (tmp$_1.dispose(), Unit) : null;
      (tmp$_2 = this$.ambientOcclusionMap) != null ? (tmp$_2.dispose(), Unit) : null;
      (tmp$_3 = this$.displacementMap) != null ? (tmp$_3.dispose(), Unit) : null;
      return Unit;
    };
  }
  function makeTreeGroundGrid$lambda(closure$groundExt, closure$shadowMaps) {
    return function ($receiver) {
      $receiver.isCastingShadow = false;
      $receiver.generate_v2sixm$(makeTreeGroundGrid$lambda$lambda(closure$groundExt));
      var $receiver_0 = new PbrShader$PbrConfig();
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.isNormalMapped = true;
      $receiver_0.isAmbientOcclusionMapped = true;
      $receiver_0.isRoughnessMapped = true;
      $receiver_0.isDisplacementMapped = true;
      $receiver_0.isReceivingShadows = true;
      $receiver_0.maxLights = 2;
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg);
      var closure$shadowMaps_0 = closure$shadowMaps;
      $receiver_1.albedoMap = new Texture(void 0, makeTreeGroundGrid$lambda$lambda$lambda);
      $receiver_1.normalMap = new Texture(void 0, makeTreeGroundGrid$lambda$lambda$lambda_0);
      $receiver_1.roughnessMap = new Texture(void 0, makeTreeGroundGrid$lambda$lambda$lambda_1);
      $receiver_1.ambientOcclusionMap = new Texture(void 0, makeTreeGroundGrid$lambda$lambda$lambda_2);
      $receiver_1.displacementMap = new Texture(void 0, makeTreeGroundGrid$lambda$lambda$lambda_3);
      $receiver_1.onCreated.add_11rb$(makeTreeGroundGrid$lambda$lambda$lambda_4($receiver_1, closure$shadowMaps_0));
      $receiver.onDispose.add_11rb$(makeTreeGroundGrid$lambda$lambda$lambda_5($receiver_1));
      $receiver.pipelineLoader = $receiver_1;
      return Unit;
    };
  }
  function makeTreeGroundGrid(cells, shadowMaps) {
    var groundExt = cells / 2 | 0;
    return textureMesh(void 0, true, makeTreeGroundGrid$lambda(groundExt, shadowMaps));
  }
  function TreeGenerator(distribution, baseTop, baseBot, primaryLightDir, random) {
    if (baseTop === void 0)
      baseTop = new Vec3f(0.0, 1.0, 0.0);
    if (baseBot === void 0)
      baseBot = Vec3f.Companion.ZERO;
    if (primaryLightDir === void 0)
      primaryLightDir = null;
    if (random === void 0)
      random = math_0.defaultRandomInstance;
    this.distribution = distribution;
    this.baseTop = baseTop;
    this.baseBot = baseBot;
    this.primaryLightDir = primaryLightDir;
    this.random = random;
    this.radiusOfInfluence = 1.0;
    this.growDistance = 0.15;
    this.killDistance = 1.5;
    this.numberOfAttractionPoints = 3000;
    this.attractionPoints_0 = ArrayList_init();
    this.attractionPointsTree_0 = pointKdTree(emptyList());
    this.attractionPointTrav_0 = new InRadiusTraverser();
    this.treeNodes_0 = ArrayList_init();
    this.root_0 = new TreeGenerator$TreeNode(this);
  }
  Object.defineProperty(TreeGenerator.prototype, 'actualKillDistance', {
    get: function () {
      return this.growDistance * this.killDistance;
    }
  });
  TreeGenerator.prototype.seedTree = function () {
    this.populateAttractionPoints_0();
    this.treeNodes_0.clear();
    this.root_0 = new TreeGenerator$TreeNode(this);
    this.root_0.set_czzhiu$(this.baseBot);
    var $receiver = this.treeNodes_0;
    var element = this.root_0;
    $receiver.add_11rb$(element);
    var d = this.baseTop.subtract_2gj7b4$(this.baseBot, MutableVec3f_init()).norm().scale_mx4ult$(this.growDistance);
    var prev = this.root_0;
    while (prev.distance_czzhiu$(this.baseTop) > this.growDistance) {
      var newNd = new TreeGenerator$TreeNode(this);
      newNd.set_czzhiu$(prev).add_czzhiu$(d);
      prev.addChild_15eqn9$(newNd);
      this.treeNodes_0.add_11rb$(newNd);
      prev = newNd;
    }
  };
  TreeGenerator.prototype.generate_za3lpa$ = function (maxIterations) {
    if (maxIterations === void 0)
      maxIterations = 1000;
    var i = {v: 0};
    var level;
    level = Log$Level.INFO;
    var t = now();
    var tmp$;
    this.seedTree();
    while ((tmp$ = i.v, i.v = tmp$ + 1 | 0, tmp$) < maxIterations && this.growSingleStep()) {
    }
    this.finishTree();
    var ret = Unit;
    var $this = util.Log;
    var tag = Kotlin.getKClassFromExpression(this).simpleName;
    if (level.level >= $this.level.level) {
      $this.printer(level, tag, 'Generation done, took ' + i.v + ' iterations, ' + this.treeNodes_0.size + ' nodes in' + ' ' + toString(now() - t, 3) + ' ms');
    }};
  TreeGenerator.prototype.growSingleStep = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
    var tmp$_6;
    tmp$_6 = this.attractionPoints_0.iterator();
    while (tmp$_6.hasNext()) {
      var element = tmp$_6.next();
      element.nearestNode = null;
    }
    tmp$ = this.treeNodes_0.iterator();
    while (tmp$.hasNext()) {
      var node = tmp$.next();
      node.influencingPts.clear();
      if (!node.isFinished) {
        this.attractionPointTrav_0.setup_2qa7tb$(node, this.radiusOfInfluence).traverse_m6hlto$(this.attractionPointsTree_0);
        tmp$_0 = this.attractionPointTrav_0.result.iterator();
        while (tmp$_0.hasNext()) {
          var attracPt = tmp$_0.next();
          if (attracPt.isOpen) {
            attracPt.checkNearest_15eqn9$(node);
          }}
      }}
    tmp$_1 = this.attractionPoints_0.iterator();
    while (tmp$_1.hasNext()) {
      var attracPt_0 = tmp$_1.next();
      if (attracPt_0.isOpen) {
        (tmp$_3 = (tmp$_2 = attracPt_0.nearestNode) != null ? tmp$_2.influencingPts : null) != null ? tmp$_3.add_11rb$(attracPt_0) : null;
      }}
    var newNodes = ArrayList_init();
    var changed = false;
    tmp$_4 = this.treeNodes_0.iterator();
    while (tmp$_4.hasNext()) {
      var node_0 = tmp$_4.next();
      if (!node_0.influencingPts.isEmpty()) {
        var growDir = MutableVec3f_init();
        tmp$_5 = node_0.influencingPts.iterator();
        while (tmp$_5.hasNext()) {
          var attracPt_1 = tmp$_5.next();
          growDir.plusAssign_czzhiu$(attracPt_1.subtract_2gj7b4$(node_0, MutableVec3f_init()).norm());
        }
        growDir.norm().scale_mx4ult$(this.growDistance);
        var newNode = new TreeGenerator$TreeNode(this);
        newNode.set_czzhiu$(node_0).add_czzhiu$(growDir);
        if (!node_0.containsChild_15eqn9$(newNode)) {
          node_0.addChild_15eqn9$(newNode);
          newNodes.add_11rb$(newNode);
          this.attractionPointTrav_0.setup_2qa7tb$(newNode, this.actualKillDistance).traverse_m6hlto$(this.attractionPointsTree_0);
          var tmp$_7;
          tmp$_7 = this.attractionPointTrav_0.result.iterator();
          while (tmp$_7.hasNext()) {
            var element_0 = tmp$_7.next();
            element_0.isOpen = false;
          }
          changed = true;
        }} else {
        node_0.isFinished = true;
      }
    }
    this.treeNodes_0.addAll_brywnq$(newNodes);
    return changed;
  };
  function TreeGenerator$finishTree$lambda(this$TreeGenerator) {
    return function ($receiver) {
      if ($receiver.parent != null) {
        $receiver.plusAssign_czzhiu$(MutableVec3f_init_0(ensureNotNull($receiver.parent)).subtract_czzhiu$($receiver).norm().scale_mx4ult$(this$TreeGenerator.growDistance * 0.5));
        $receiver.x = $receiver.x + this$TreeGenerator.random.randomF_dleff0$(-0.01, 0.01);
        $receiver.y = $receiver.y + this$TreeGenerator.random.randomF_dleff0$(-0.01, 0.01);
        $receiver.z = $receiver.z + this$TreeGenerator.random.randomF_dleff0$(-0.01, 0.01);
      }$receiver.computeTrunkRadiusAndDepth();
      $receiver.computeCircumPoints();
      return Unit;
    };
  }
  function TreeGenerator$finishTree$lambda_0($receiver) {
    if ($receiver.parent != null) {
      var baseV = ensureNotNull($receiver.parent).texV;
      $receiver.texV = baseV + $receiver.distance_czzhiu$(ensureNotNull($receiver.parent)) / ($receiver.radius * 2.0 * math.PI * 1.5);
    }return Unit;
  }
  TreeGenerator.prototype.finishTree = function () {
    this.root_0.forEachTopDown_ttqnr0$(TreeGenerator$finishTree$lambda(this));
    this.root_0.forEachBottomUp_ttqnr0$(TreeGenerator$finishTree$lambda_0);
  };
  TreeGenerator.prototype.buildTrunkMesh_84rojv$ = function (target) {
    var tmp$;
    tmp$ = this.treeNodes_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.buildTrunkMesh_84rojv$(target);
    }
  };
  TreeGenerator.prototype.buildLeafMesh_84rojv$ = function (target) {
    var tmp$;
    tmp$ = this.treeNodes_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.buildLeafMesh_84rojv$(target);
    }
    var $this = target.geometry;
    var tmp$_0;
    tmp$_0 = $this.numVertices;
    for (var i = 0; i < tmp$_0; i++) {
      $this.vertexIt.index = i;
      var it = $this.vertexIt;
      if (this.primaryLightDir != null) {
        if (it.normal.dot_czzhiu$(this.primaryLightDir) > 0) {
          it.normal.scale_mx4ult$(-1.0);
        }} else {
        if (it.normal.y < 0) {
          it.normal.scale_mx4ult$(-1.0);
        }}
    }
  };
  TreeGenerator.prototype.populateAttractionPoints_0 = function () {
    var tmp$;
    this.attractionPoints_0.clear();
    tmp$ = this.distribution.nextPoints_za3lpa$(this.numberOfAttractionPoints).iterator();
    while (tmp$.hasNext()) {
      var pt = tmp$.next();
      var $receiver = this.attractionPoints_0;
      var element = new TreeGenerator$AttractionPoint(pt);
      $receiver.add_11rb$(element);
    }
    this.attractionPointsTree_0 = pointKdTree(this.attractionPoints_0);
  };
  function TreeGenerator$AttractionPoint(pt) {
    MutableVec3f_init_0(pt, this);
    this.nearestNode_3mwxa2$_0 = null;
    this.nearestNodeDist_dewf4c$_0 = kotlin_js_internal_FloatCompanionObject.MAX_VALUE;
    this.isOpen = true;
  }
  Object.defineProperty(TreeGenerator$AttractionPoint.prototype, 'nearestNode', {
    get: function () {
      return this.nearestNode_3mwxa2$_0;
    },
    set: function (value) {
      this.nearestNode_3mwxa2$_0 = value;
      if (value == null) {
        this.nearestNodeDist = kotlin_js_internal_FloatCompanionObject.MAX_VALUE;
      }}
  });
  Object.defineProperty(TreeGenerator$AttractionPoint.prototype, 'nearestNodeDist', {
    get: function () {
      return this.nearestNodeDist_dewf4c$_0;
    },
    set: function (nearestNodeDist) {
      this.nearestNodeDist_dewf4c$_0 = nearestNodeDist;
    }
  });
  TreeGenerator$AttractionPoint.prototype.checkNearest_15eqn9$ = function (node) {
    var dist = this.distance_czzhiu$(node);
    if (dist < this.nearestNodeDist) {
      this.nearestNode = node;
      this.nearestNodeDist = dist;
    }};
  TreeGenerator$AttractionPoint.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AttractionPoint',
    interfaces: [MutableVec3f]
  };
  function TreeGenerator$TreeNode($outer) {
    this.$outer = $outer;
    MutableVec3f_init(this);
    this.children = ArrayList_init();
    this.parent = null;
    this.branchDepth = 0;
    this.influencingPts = ArrayList_init();
    this.isFinished = false;
    this.radius = 0.005;
    this.texV = 0.0;
    this.uScale = 1.0;
    this.vScale = 3.0;
    this.circumPts = ArrayList_init();
  }
  TreeGenerator$TreeNode.prototype.addChild_15eqn9$ = function (node) {
    this.children.add_11rb$(node);
    node.parent = this;
  };
  TreeGenerator$TreeNode.prototype.containsChild_15eqn9$ = function (node) {
    var tmp$;
    tmp$ = this.children.iterator();
    while (tmp$.hasNext()) {
      var c = tmp$.next();
      if (c.isFuzzyEqual_2qa7tb$(node)) {
        return true;
      }}
    return false;
  };
  TreeGenerator$TreeNode.prototype.forEachBottomUp_ttqnr0$ = function (block) {
    block(this);
    var tmp$;
    tmp$ = this.children.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.forEachBottomUp_ttqnr0$(block);
    }
  };
  TreeGenerator$TreeNode.prototype.forEachTopDown_ttqnr0$ = function (block) {
    var tmp$;
    tmp$ = this.children.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.forEachTopDown_ttqnr0$(block);
    }
    block(this);
  };
  TreeGenerator$TreeNode.prototype.computeTrunkRadiusAndDepth = function () {
    var tmp$, tmp$_0, tmp$_1;
    var p = 2.25;
    if (this.children.isEmpty()) {
      this.radius = 0.01;
      this.branchDepth = 0;
    } else {
      var tmp$_2;
      var sum = 0.0;
      tmp$_2 = this.children.iterator();
      while (tmp$_2.hasNext()) {
        var element = tmp$_2.next();
        var $receiver = element.radius;
        sum += Math_0.pow($receiver, p);
      }
      var x = 1.0 / p;
      this.radius = Math_0.pow(sum, x);
      if (this.children.size === 1) {
        tmp$_1 = this.children.get_za3lpa$(0).branchDepth;
      } else {
        var $receiver_0 = this.children;
        var maxBy$result;
        maxBy$break: do {
          var iterator = $receiver_0.iterator();
          if (!iterator.hasNext()) {
            maxBy$result = null;
            break maxBy$break;
          }var maxElem = iterator.next();
          if (!iterator.hasNext()) {
            maxBy$result = maxElem;
            break maxBy$break;
          }var maxValue = maxElem.branchDepth;
          do {
            var e = iterator.next();
            var v = e.branchDepth;
            if (Kotlin.compareTo(maxValue, v) < 0) {
              maxElem = e;
              maxValue = v;
            }}
           while (iterator.hasNext());
          maxBy$result = maxElem;
        }
         while (false);
        tmp$_1 = ((tmp$_0 = (tmp$ = maxBy$result) != null ? tmp$.branchDepth : null) != null ? tmp$_0 : 0) + 1 | 0;
      }
      this.branchDepth = tmp$_1;
    }
    if (this.parent == null) {
      this.radius *= 3.0;
      this.children.get_za3lpa$(0).radius = this.children.get_za3lpa$(0).radius * 1.5;
      this.children.get_za3lpa$(0).computeCircumPoints();
    }};
  TreeGenerator$TreeNode.prototype.computeCircumPoints = function () {
    var tmp$;
    this.circumPts.clear();
    if (this.parent != null) {
      tmp$ = this.subtract_2gj7b4$(ensureNotNull(this.parent), MutableVec3f_init()).norm();
    } else {
      tmp$ = this.children.get_za3lpa$(0).subtract_2gj7b4$(this, MutableVec3f_init()).norm();
    }
    var n = tmp$;
    var c = MutableVec3f_init_0(n).scale_mx4ult$(-n.times_czzhiu$(Vec3f.Companion.Z_AXIS)).add_czzhiu$(Vec3f.Companion.Z_AXIS).norm().scale_mx4ult$(this.radius);
    var y = c.z;
    var x = c.x;
    c.rotate_ad55pp$(-Math_0.atan2(y, x), n);
    for (var i = 0; i < 8; i++) {
      var pt = MutableVec3f_init_0(c).add_czzhiu$(this);
      this.circumPts.add_11rb$(Vec3f_init_0(pt));
      c.rotate_ad55pp$(360.0 / 8, n);
    }
  };
  TreeGenerator$TreeNode.prototype.buildTrunkMesh_84rojv$ = function (target) {
    var tmp$;
    if (this.radius > 0.05) {
      tmp$ = 2.0;
    } else {
      tmp$ = 1.0;
    }
    var uScale = tmp$ * this.uScale;
    var idcs = ArrayList_init();
    if (this.parent != null) {
      if (this.children.isEmpty()) {
        var $this = target.geometry;
        var tmp$_0, tmp$_0_0, tmp$_1;
        $this.checkBufferSizes_za3lpa$();
        tmp$_0 = $this.vertexSizeF;
        for (var i = 1; i <= tmp$_0; i++) {
          $this.dataF.plusAssign_mx4ult$(0.0);
        }
        tmp$_0_0 = $this.vertexSizeI;
        for (var i_0 = 1; i_0 <= tmp$_0_0; i_0++) {
          $this.dataI.plusAssign_za3lpa$(0);
        }
        $this.vertexIt.index = (tmp$_1 = $this.numVertices, $this.numVertices = tmp$_1 + 1 | 0, tmp$_1);
        var $receiver = $this.vertexIt;
        $receiver.position.set_czzhiu$(this);
        this.subtract_2gj7b4$(ensureNotNull(this.parent), $receiver.normal).norm();
        $receiver.texCoord.set_dleff0$(0.0, this.texV * this.vScale);
        $this.bounds.add_czzhiu$($this.vertexIt.position);
        $this.hasChanged = true;
        var tipIdx = $this.numVertices - 1 | 0;
        for (var i_1 = 0; i_1 <= 8; i_1++) {
          var $this_0 = target.geometry;
          var tmp$_2, tmp$_0_1, tmp$_1_0;
          $this_0.checkBufferSizes_za3lpa$();
          tmp$_2 = $this_0.vertexSizeF;
          for (var i_2 = 1; i_2 <= tmp$_2; i_2++) {
            $this_0.dataF.plusAssign_mx4ult$(0.0);
          }
          tmp$_0_1 = $this_0.vertexSizeI;
          for (var i_0_0 = 1; i_0_0 <= tmp$_0_1; i_0_0++) {
            $this_0.dataI.plusAssign_za3lpa$(0);
          }
          $this_0.vertexIt.index = (tmp$_1_0 = $this_0.numVertices, $this_0.numVertices = tmp$_1_0 + 1 | 0, tmp$_1_0);
          var $receiver_0 = $this_0.vertexIt;
          $receiver_0.position.set_czzhiu$(ensureNotNull(this.parent).circumPts.get_za3lpa$(i_1 % 8));
          ensureNotNull(this.parent).circumPts.get_za3lpa$(i_1 % 8).subtract_2gj7b4$(ensureNotNull(this.parent), $receiver_0.normal).norm();
          $receiver_0.texCoord.set_dleff0$(i_1 / 8.0 * uScale, ensureNotNull(this.parent).texV * this.vScale);
          $this_0.bounds.add_czzhiu$($this_0.vertexIt.position);
          $this_0.hasChanged = true;
          var element = $this_0.numVertices - 1 | 0;
          idcs.add_11rb$(element);
        }
        for (var i_3 = 0; i_3 < 8; i_3++) {
          target.geometry.addTriIndices_qt1dr2$(tipIdx, idcs.get_za3lpa$(i_3), idcs.get_za3lpa$(i_3 + 1 | 0));
        }
      } else {
        for (var i_4 = 0; i_4 <= 8; i_4++) {
          var $this_1 = target.geometry;
          var tmp$_3, tmp$_0_2, tmp$_1_1;
          $this_1.checkBufferSizes_za3lpa$();
          tmp$_3 = $this_1.vertexSizeF;
          for (var i_5 = 1; i_5 <= tmp$_3; i_5++) {
            $this_1.dataF.plusAssign_mx4ult$(0.0);
          }
          tmp$_0_2 = $this_1.vertexSizeI;
          for (var i_0_1 = 1; i_0_1 <= tmp$_0_2; i_0_1++) {
            $this_1.dataI.plusAssign_za3lpa$(0);
          }
          $this_1.vertexIt.index = (tmp$_1_1 = $this_1.numVertices, $this_1.numVertices = tmp$_1_1 + 1 | 0, tmp$_1_1);
          var $receiver_1 = $this_1.vertexIt;
          $receiver_1.position.set_czzhiu$(this.circumPts.get_za3lpa$(i_4 % 8));
          this.circumPts.get_za3lpa$(i_4 % 8).subtract_2gj7b4$(this, $receiver_1.normal).norm();
          $receiver_1.texCoord.set_dleff0$(i_4 / 8.0 * uScale, this.texV * this.vScale);
          $this_1.bounds.add_czzhiu$($this_1.vertexIt.position);
          $this_1.hasChanged = true;
          var element_0 = $this_1.numVertices - 1 | 0;
          idcs.add_11rb$(element_0);
          var $this_2 = target.geometry;
          var tmp$_4, tmp$_0_3, tmp$_1_2;
          $this_2.checkBufferSizes_za3lpa$();
          tmp$_4 = $this_2.vertexSizeF;
          for (var i_6 = 1; i_6 <= tmp$_4; i_6++) {
            $this_2.dataF.plusAssign_mx4ult$(0.0);
          }
          tmp$_0_3 = $this_2.vertexSizeI;
          for (var i_0_2 = 1; i_0_2 <= tmp$_0_3; i_0_2++) {
            $this_2.dataI.plusAssign_za3lpa$(0);
          }
          $this_2.vertexIt.index = (tmp$_1_2 = $this_2.numVertices, $this_2.numVertices = tmp$_1_2 + 1 | 0, tmp$_1_2);
          var $receiver_2 = $this_2.vertexIt;
          $receiver_2.position.set_czzhiu$(ensureNotNull(this.parent).circumPts.get_za3lpa$(i_4 % 8));
          ensureNotNull(this.parent).circumPts.get_za3lpa$(i_4 % 8).subtract_2gj7b4$(ensureNotNull(this.parent), $receiver_2.normal).norm();
          $receiver_2.texCoord.set_dleff0$(i_4 / 8.0 * uScale, ensureNotNull(this.parent).texV * this.vScale);
          $this_2.bounds.add_czzhiu$($this_2.vertexIt.position);
          $this_2.hasChanged = true;
          var element_1 = $this_2.numVertices - 1 | 0;
          idcs.add_11rb$(element_1);
        }
        for (var i_7 = 0; i_7 < 8; i_7++) {
          target.geometry.addTriIndices_qt1dr2$(idcs.get_za3lpa$(i_7 * 2 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 1 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 2 | 0));
          target.geometry.addTriIndices_qt1dr2$(idcs.get_za3lpa$((i_7 * 2 | 0) + 1 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 3 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 2 | 0));
        }
      }
    }};
  TreeGenerator$TreeNode.prototype.buildLeafMesh_84rojv$ = function (target) {
    if (this.branchDepth <= 1 && this.parent != null) {
      var n = this.subtract_2gj7b4$(ensureNotNull(this.parent), MutableVec3f_init());
      var len = n.length();
      n.norm();
      for (var i = 1; i <= 20; i++) {
        this.$outer;
        target.transform.push();
        var this$TreeGenerator = this.$outer;
        var tmp$;
        var r = MutableVec3f_init_0(this.circumPts.get_za3lpa$(0)).subtract_czzhiu$(this).norm().scale_mx4ult$(this.radius + this$TreeGenerator.random.randomF_dleff0$(0.0, 0.15));
        r.rotate_ad55pp$(this$TreeGenerator.random.randomF_dleff0$(0.0, 360.0), n);
        var p = MutableVec3f_init_0(n).scale_mx4ult$(this$TreeGenerator.random.randomF_dleff0$(0.0, len)).add_czzhiu$(r).add_czzhiu$(this);
        target.translate_czzhiu$(p);
        var tries = 0;
        do {
          if (n.dot_czzhiu$(Vec3f.Companion.X_AXIS) < n.dot_czzhiu$(Vec3f.Companion.Z_AXIS)) {
            target.rotate_ad55pp$(this$TreeGenerator.random.randomF_dleff0$(0.0, 360.0), Vec3f.Companion.X_AXIS);
          } else {
            target.rotate_ad55pp$(this$TreeGenerator.random.randomF_dleff0$(0.0, 360.0), Vec3f.Companion.Z_AXIS);
          }
          target.rotate_ad55pp$(this$TreeGenerator.random.randomF_dleff0$(0.0, 360.0), n);
          var tmp$_0 = this$TreeGenerator.primaryLightDir != null;
          if (tmp$_0) {
            var x = target.transform.transform_w1lst9$(MutableVec3f_init_0(Vec3f.Companion.NEG_Z_AXIS), 0.0).dot_czzhiu$(this$TreeGenerator.primaryLightDir);
            tmp$_0 = Math_0.abs(x) < 0.1;
          }}
         while (tmp$_0 && (tmp$ = tries, tries = tmp$ + 1 | 0, tmp$) < 3);
        var i0 = target.vertex_n440gp$(new Vec3f(0.0, -0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(0.0, 0.0));
        var i1 = target.vertex_n440gp$(new Vec3f(0.0, 0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(0.0, 1.0));
        var i2 = target.vertex_n440gp$(new Vec3f(0.1, 0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(1.0, 1.0));
        var i3 = target.vertex_n440gp$(new Vec3f(0.1, -0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(1.0, 0.0));
        target.geometry.addIndices_pmhfmb$(new Int32Array([i0, i1, i2, i0, i2, i3]));
        target.transform.pop();
      }
    }};
  TreeGenerator$TreeNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TreeNode',
    interfaces: [MutableVec3f]
  };
  TreeGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TreeGenerator',
    interfaces: []
  };
  function TreeTopPointDistribution(centerY, width, height, random) {
    if (random === void 0)
      random = math_0.defaultRandomInstance;
    PointDistribution.call(this);
    this.centerY = centerY;
    this.width = width;
    this.height = height;
    this.random_0 = random;
    this.borders_0 = ArrayList_init();
    this.tmpPt1_0 = MutableVec3f_init();
    this.tmpPt2_0 = MutableVec2f_init();
    this.e00_0 = MutableVec2f_init();
    this.e01_0 = MutableVec2f_init();
    this.e10_0 = MutableVec2f_init();
    this.e11_0 = MutableVec2f_init();
    this.seedPts_0 = ArrayList_init();
    var tmp$;
    for (var j = 1; j <= 8; j++) {
      var spline = new BSplineVec2f(3);
      var n = 7;
      for (var i = 0; i <= n; i++) {
        var a = i / n * math.PI;
        if (1 <= i && i < n) {
          tmp$ = this.random_0.randomF_dleff0$(0.4, 0.6);
        } else {
          tmp$ = 0.5;
        }
        var f = tmp$;
        var x = Math_0.sin(a) * (this.width - 0.4) * f + 0.2;
        var y = Math_0.cos(a) * this.height * f + this.centerY;
        var $receiver = spline.ctrlPoints;
        var element = new MutableVec2f(x, y);
        $receiver.add_11rb$(element);
      }
      spline.ctrlPoints.add_wxm5ur$(0, new MutableVec2f(0.0, this.centerY + this.height * 0.5));
      spline.ctrlPoints.add_11rb$(new MutableVec2f(0.0, this.centerY - this.height * 0.5));
      spline.addInterpolationEndpoints();
      var pts = ArrayList_init();
      var m = 20;
      for (var i_0 = 0; i_0 <= m; i_0++) {
        var element_0 = spline.evaluate_f6p79m$(i_0 / m, MutableVec2f_init());
        pts.add_11rb$(element_0);
      }
      this.borders_0.add_11rb$(pts);
    }
    this.seed_0();
  }
  TreeTopPointDistribution.prototype.seed_0 = function () {
    this.seedPts_0.clear();
    for (var i = 1; i <= 10; i++) {
      var $receiver = this.seedPts_0;
      var element = this.nextPointInBounds_0();
      $receiver.add_11rb$(element);
    }
  };
  TreeTopPointDistribution.prototype.drawBorders_nl2my4$ = function (target) {
    var tmp$;
    tmp$ = this.borders_0;
    for (var i = 0; i !== tmp$.size; ++i) {
      var tmp$_0;
      var a = i / this.borders_0.size * 2.0 * math.PI;
      var pts = this.borders_0.get_za3lpa$(i);
      tmp$_0 = pts.size;
      for (var j = 1; j < tmp$_0; j++) {
        var p0 = new Vec3f(-Math_0.cos(a) * pts.get_za3lpa$(j - 1 | 0).x, pts.get_za3lpa$(j - 1 | 0).y, -Math_0.sin(a) * pts.get_za3lpa$(j - 1 | 0).x);
        var p1 = new Vec3f(-Math_0.cos(a) * pts.get_za3lpa$(j).x, pts.get_za3lpa$(j).y, -Math_0.sin(a) * pts.get_za3lpa$(j).x);
        target.addLine_b8opkg$(p0, Color.Companion.ORANGE, p1, Color.Companion.ORANGE);
      }
    }
  };
  TreeTopPointDistribution.prototype.nextPoints_za3lpa$ = function (n) {
    this.seed_0();
    return PointDistribution.prototype.nextPoints_za3lpa$.call(this, n);
  };
  TreeTopPointDistribution.prototype.nextPoint = function () {
    var pt = {v: null};
    loop_label: while (true) {
      pt.v = this.nextPointInBounds_0();
      var $receiver = this.seedPts_0;
      var minBy$result;
      minBy$break: do {
        var iterator = $receiver.iterator();
        if (!iterator.hasNext()) {
          minBy$result = null;
          break minBy$break;
        }var minElem = iterator.next();
        if (!iterator.hasNext()) {
          minBy$result = minElem;
          break minBy$break;
        }var minValue = minElem.sqrDistance_czzhiu$(pt.v);
        do {
          var e = iterator.next();
          var v = e.sqrDistance_czzhiu$(pt.v);
          if (Kotlin.compareTo(minValue, v) > 0) {
            minElem = e;
            minValue = v;
          }}
         while (iterator.hasNext());
        minBy$result = minElem;
      }
       while (false);
      var d = ensureNotNull(minBy$result).distance_czzhiu$(pt.v);
      if (d < this.random_0.randomF()) {
        break loop_label;
      }}
    return pt.v;
  };
  TreeTopPointDistribution.prototype.nextPointInBounds_0 = function () {
    var w = this.width * 0.5;
    var h = this.height * 0.5;
    while (true) {
      this.tmpPt1_0.set_y2kzbl$(this.random_0.randomF_dleff0$(-w, w), this.centerY + this.random_0.randomF_dleff0$(-h, h), this.random_0.randomF_dleff0$(-w, w));
      var x = this.tmpPt1_0.x * this.tmpPt1_0.x + this.tmpPt1_0.z * this.tmpPt1_0.z;
      var px = Math_0.sqrt(x);
      var py = this.tmpPt1_0.y;
      var y = this.tmpPt1_0.z;
      var x_0 = this.tmpPt1_0.x;
      var $receiver = Math_0.atan2(y, x_0) / (2.0 * math.PI) + 0.5;
      var clamp$result;
      if ($receiver < 0.0) {
        clamp$result = 0.0;
      } else if ($receiver > 1.0) {
        clamp$result = 1.0;
      } else {
        clamp$result = $receiver;
      }
      var a = clamp$result * this.borders_0.size;
      var a_0 = numberToInt(a);
      var b = this.borders_0.size - 1 | 0;
      var i0 = Math_0.min(a_0, b);
      var i1 = (i0 + 1 | 0) % this.borders_0.size;
      var w1 = a - i0;
      var w0 = 1.0 - w1;
      this.nearestEdge_0(px, py, this.borders_0.get_za3lpa$(i0), this.e00_0, this.e01_0);
      this.nearestEdge_0(px, py, this.borders_0.get_za3lpa$(i1), this.e10_0, this.e11_0);
      this.e00_0.scale_mx4ult$(w0).add_czzhjp$(this.e10_0.scale_mx4ult$(w1));
      this.e01_0.scale_mx4ult$(w0).add_czzhjp$(this.e11_0.scale_mx4ult$(w1));
      var d = (px - this.e00_0.x) * (this.e01_0.y - this.e00_0.y) - (py - this.e00_0.y) * (this.e01_0.x - this.e00_0.x);
      if (d > 0) {
        return Vec3f_init_0(this.tmpPt1_0);
      }}
  };
  TreeTopPointDistribution.prototype.nearestEdge_0 = function (px, py, pts, e0, e1) {
    var tmp$;
    var minDist = kotlin_js_internal_FloatCompanionObject.MAX_VALUE;
    var ni = 0;
    tmp$ = pts.size - 1 | 0;
    for (var i = 0; i < tmp$; i++) {
      var d = this.edgeDist_0(px, py, e0.set_czzhjp$(pts.get_za3lpa$(i)), e1.set_czzhjp$(pts.get_za3lpa$(i + 1 | 0)));
      if (d < minDist) {
        minDist = d;
        ni = i;
      }}
    e0.set_czzhjp$(pts.get_za3lpa$(ni));
    e1.set_czzhjp$(pts.get_za3lpa$(ni + 1 | 0));
  };
  TreeTopPointDistribution.prototype.edgeDist_0 = function (px, py, e0, e1) {
    var tmp$;
    e1.subtract_q2ruao$(e0, this.tmpPt2_0);
    var l = (px * this.tmpPt2_0.x + py * this.tmpPt2_0.y - e0.times_czzhjp$(this.tmpPt2_0)) / this.tmpPt2_0.times_czzhjp$(this.tmpPt2_0);
    if (l < 0) {
      var dx = e0.x - px;
      var dy = e0.y - py;
      var x = dx * dx + dy * dy;
      tmp$ = Math_0.sqrt(x);
    } else if (l > 1) {
      var dx_0 = e1.x - px;
      var dy_0 = e1.y - py;
      var x_0 = dx_0 * dx_0 + dy_0 * dy_0;
      tmp$ = Math_0.sqrt(x_0);
    } else {
      this.tmpPt2_0.scale_mx4ult$(l).add_czzhjp$(e0);
      var dx_1 = this.tmpPt2_0.x - px;
      var dy_1 = this.tmpPt2_0.y - py;
      var x_1 = dx_1 * dx_1 + dy_1 * dy_1;
      tmp$ = Math_0.sqrt(x_1);
    }
    return tmp$;
  };
  TreeTopPointDistribution.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TreeTopPointDistribution',
    interfaces: [PointDistribution]
  };
  function uiDemoScene$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      return Unit;
    };
  }
  function uiDemoScene$lambda$lambda$lambda(this$) {
    return function ($receiver, ctx) {
      this$.setIdentity();
      this$.translate_y2kzbl$(0.0, 0.0, -7.0);
      this$.rotate_ad55pp$(ctx.time * 60, Vec3f.Companion.X_AXIS);
      this$.rotate_ad55pp$(ctx.time * 17, Vec3f.Companion.Y_AXIS);
      return Unit;
    };
  }
  function uiDemoScene$lambda$lambda$lambda$lambda($receiver) {
    $receiver.scale_y2kzbl$(5.0, 5.0, 5.0);
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.centered();
    $receiver_0.colored_6taknv$();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda_0($receiver) {
    $receiver.generate_v2sixm$(uiDemoScene$lambda$lambda$lambda$lambda);
    $receiver.pipelineLoader = new ModeledShader$VertexColor();
    return Unit;
  }
  function uiDemoScene$lambda$lambda_0($receiver) {
    $receiver.onPreRender.add_11rb$(uiDemoScene$lambda$lambda$lambda($receiver));
    $receiver.unaryPlus_uv0sim$(colorMesh(void 0, uiDemoScene$lambda$lambda$lambda_0));
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda_1(this$) {
    return function ($receiver) {
      $receiver.translate_y2kzbl$(-dp(this$.content, 200.0), -dp(this$.content, 200.0), 0.0);
      return Unit;
    };
  }
  function uiDemoScene$lambda$lambda$lambda_2($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-25.0), zero());
    $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), pcs(15.0), full());
    $receiver.text = 'Toggle Button';
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda_3($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-45.0), zero());
    $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), pcs(15.0), full());
    $receiver.text = 'Slider';
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda$lambda_0($receiver, value) {
    $receiver.root.content.alpha = value;
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda_4($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(pcs(35.0), pcs(-45.0), zero());
    $receiver.layoutSpec.setSize_4ujscr$(pcs(50.0), pcs(15.0), full());
    $receiver.padding.left = uns(0.0);
    $receiver.onValueChanged.add_11rb$(uiDemoScene$lambda$lambda$lambda$lambda_0);
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda_5($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-65.0), zero());
    $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), pcs(15.0), full());
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda$lambda_1(this$) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      if (equals(this$.theme, UiTheme.Companion.DARK)) {
        tmp$ = UiTheme.Companion.LIGHT;
      } else {
        tmp$ = UiTheme.Companion.DARK;
      }
      this$.theme = tmp$;
      return Unit;
    };
  }
  function uiDemoScene$lambda$lambda$lambda_6(this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-85.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), pcs(15.0), full());
      $receiver.text = 'Toggle Theme';
      var $receiver_0 = $receiver.onClick;
      var element = uiDemoScene$lambda$lambda$lambda$lambda_1(this$);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function uiDemoScene$lambda$lambda_1($receiver) {
    $receiver.content.customTransform = uiDemoScene$lambda$lambda$lambda_1($receiver);
    $receiver.unaryPlus_uv0sim$($receiver.toggleButton_6j87po$('toggle-button', uiDemoScene$lambda$lambda$lambda_2));
    $receiver.unaryPlus_uv0sim$($receiver.label_tokfmu$('label', uiDemoScene$lambda$lambda$lambda_3));
    $receiver.unaryPlus_uv0sim$($receiver.slider_91a1dk$('slider', 0.4, 1.0, 1.0, uiDemoScene$lambda$lambda$lambda_4));
    $receiver.unaryPlus_uv0sim$($receiver.textField_peizi7$('text-field', uiDemoScene$lambda$lambda$lambda_5));
    $receiver.unaryPlus_uv0sim$($receiver.button_9zrh0o$('toggle-theme', uiDemoScene$lambda$lambda$lambda_6($receiver)));
    return Unit;
  }
  function uiDemoScene() {
    var $receiver = new Scene_init('UI Demo');
    $receiver.unaryPlus_uv0sim$(orbitInputTransform(void 0, uiDemoScene$lambda$lambda($receiver)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, uiDemoScene$lambda$lambda_0));
    $receiver.unaryPlus_uv0sim$(embeddedUi(10.0, 10.0, dps(400.0), void 0, uiDemoScene$lambda$lambda_1));
    return $receiver;
  }
  function EquiRectCoords(graph) {
    ShaderNode.call(this, 'equiRect', graph);
    this.input = new ShaderNodeIoVar(new ModelVar3fConst(Vec3f.Companion.ZERO));
    this.outUv = new ShaderNodeIoVar(new ModelVar2f('equiRect_uv'), this);
  }
  EquiRectCoords.prototype.setup_llmhyc$ = function (shaderGraph) {
    ShaderNode.prototype.setup_llmhyc$.call(this, shaderGraph);
    this.dependsOn_7qvs0d$(this.input);
  };
  EquiRectCoords.prototype.generateCode_626509$ = function (generator) {
    ShaderNode.prototype.generateCode_626509$.call(this, generator);
    generator.appendFunction_puj7f4$('invAtan', 'const vec2 invAtan = vec2(0.1591, 0.3183);');
    generator.appendMain_61zpoe$('\n' + '            vec3 equiRect_in = normalize(' + this.input.ref3f() + ');' + '\n' + '            ' + this.outUv.declare() + ' = vec2(atan(equiRect_in.z, equiRect_in.x), -asin(equiRect_in.y));' + '\n' + '            ' + this.outUv.ref2f() + ' *= invAtan;' + '\n' + '            ' + this.outUv.ref2f() + ' += 0.5;' + '\n' + '        ');
  };
  EquiRectCoords.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EquiRectCoords',
    interfaces: [ShaderNode]
  };
  function DecodeRgbeNode(graph) {
    ShaderNode.call(this, 'decodeRgbe', graph);
    this.input = new ShaderNodeIoVar(new ModelVar4fConst(Color.Companion.MAGENTA));
    this.outColor = new ShaderNodeIoVar(new ModelVar4f('decodeRgbe_outColor'), this);
  }
  DecodeRgbeNode.prototype.setup_llmhyc$ = function (shaderGraph) {
    ShaderNode.prototype.setup_llmhyc$.call(this, shaderGraph);
    this.dependsOn_7qvs0d$(this.input);
  };
  DecodeRgbeNode.prototype.generateCode_626509$ = function (generator) {
    ShaderNode.prototype.generateCode_626509$.call(this, generator);
    generator.appendMain_61zpoe$('\n' + '            // decode rgbe' + '\n' + '            ' + this.outColor.declare() + ' = vec4(' + this.input.ref3f() + ' * pow(2.0, ' + this.input.ref4f() + '.w * 255.0 - 127.0), 1.0);' + '\n' + '        ');
  };
  DecodeRgbeNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DecodeRgbeNode',
    interfaces: [ShaderNode]
  };
  function DecodeAndToneMapRgbeNode(graph) {
    ShaderNode.call(this, 'decodeRgbe', graph);
    this.input = new ShaderNodeIoVar(new ModelVar4fConst(Color.Companion.MAGENTA));
    this.outColor = new ShaderNodeIoVar(new ModelVar4f('decodeRgbe_outColor'), this);
  }
  DecodeAndToneMapRgbeNode.prototype.setup_llmhyc$ = function (shaderGraph) {
    ShaderNode.prototype.setup_llmhyc$.call(this, shaderGraph);
    this.dependsOn_7qvs0d$(this.input);
  };
  DecodeAndToneMapRgbeNode.prototype.generateCode_626509$ = function (generator) {
    ShaderNode.prototype.generateCode_626509$.call(this, generator);
    generator.appendFunction_puj7f4$('uncharted2ToneMap', '\n            vec3 uncharted2Tonemap_func(vec3 x) {\n                float A = 0.15;\n                float B = 0.50;\n                float C = 0.10;\n                float D = 0.20;\n                float E = 0.02;\n                float F = 0.30;\n                \n                return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;\n            }\n            \n            vec3 uncharted2Tonemap(vec3 rgbLinear) {\n                float W = 11.2;\n                \n                float ExposureBias = 2.0;\n                vec3 curr = uncharted2Tonemap_func(ExposureBias * rgbLinear);\n                \n                vec3 whiteScale = 1.0 / uncharted2Tonemap_func(vec3(W));\n                vec3 color = curr * whiteScale;\n                \n                return pow(color, vec3(1.0 / 2.2));\n            }\n        ');
    generator.appendFunction_puj7f4$('acesFilm', '\n            vec3 ACESFilm(vec3 x) {\n                float a = 2.51f;\n                float b = 0.03f;\n                float c = 2.43f;\n                float d = 0.59f;\n                float e = 0.14f;\n                vec3 color = clamp((x*(a*x+b))/(x*(c*x+d)+e), 0.0, 1.0);\n                return pow(color, vec3(1.0 / 2.2));\n            }\n        ');
    generator.appendMain_61zpoe$('\n' + '            // decode rgbe' + '\n' + '            //' + this.outColor.declare() + ' = vec4(' + this.input.ref3f() + ' * pow(2.0, ' + this.input.ref4f() + '.w * 255.0 - 127.0), 1.0);' + '\n' + '            ' + this.outColor.declare() + ' = vec4(' + this.input.ref3f() + ' * pow(2.0, ' + this.input.ref4f() + '.w * 255.0 - 128.0), 1.0);' + '\n' + '\n' + '            //' + this.outColor + '.rgb = 0.5 * pow(' + this.outColor + '.rgb, vec3(0.9));' + '\n' + '            //' + this.outColor + '.rgb = ' + this.outColor + '.rgb / (' + this.outColor + '.rgb + vec3(1.0));' + '\n' + '            ' + '\n' + '            //' + this.outColor + '.rgb = pow(' + this.outColor + '.rgb, vec3(1.0 / 2.2));' + '\n' + '\n' + '            // from: http://filmicworlds.com/blog/filmic-tonemapping-operators/' + '\n' + '            // Jim Hejl and Richard Burgess-Dawson' + '\n' + '            // no need for pow(rgb, 1.0/2.2)!' + '\n' + '            vec3 x = max(vec3(0), ' + this.outColor + '.rgb - 0.004);' + '\n' + '            ' + this.outColor + '.rgb = (x * (6.2 * x + 0.5)) / (x * (6.2 * x + 1.7) + 0.06);' + '\n' + '            ' + '\n' + '            // uncharted 2' + '\n' + '            //' + this.outColor + '.rgb = uncharted2Tonemap(' + this.outColor + '.rgb);' + '\n' + '            ' + '\n' + '            // https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/' + '\n' + '            //' + this.outColor + '.rgb = ACESFilm(' + this.outColor + '.rgb);' + '\n' + '            ' + '\n' + '            // maybe also worth a try:' + '\n' + '            //https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl' + '\n' + '        ');
  };
  DecodeAndToneMapRgbeNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DecodeAndToneMapRgbeNode',
    interfaces: [ShaderNode]
  };
  function testScene$lambda$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.sphereProps.defaults();
    $receiver_0.steps = 4;
    $receiver_0.center.set_y2kzbl$(3.0, 0.0, 0.0);
    $receiver.icoSphere_mojs8w$($receiver.sphereProps);
    return Unit;
  }
  function Coroutine$testScene$lambda$lambda$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda.prototype.constructor = Coroutine$testScene$lambda$lambda$lambda$lambda$lambda;
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$('world.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function testScene$lambda$lambda$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$testScene$lambda$lambda$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function testScene$lambda$lambda$lambda$lambda(this$) {
    return function (it) {
      this$.textureSampler.texture = new Texture(void 0, testScene$lambda$lambda$lambda$lambda$lambda);
      return Unit;
    };
  }
  function testScene$lambda$lambda($receiver) {
    $receiver.generate_v2sixm$(testScene$lambda$lambda$lambda);
    var $receiver_0 = new ModeledShader$TextureColor();
    $receiver_0.onCreated.add_11rb$(testScene$lambda$lambda$lambda$lambda($receiver_0));
    $receiver.pipelineLoader = $receiver_0;
    return Unit;
  }
  function testScene$lambda$lambda$lambda_0($receiver) {
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.colored_6taknv$();
    $receiver_0.centered();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0.prototype.constructor = Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0;
  Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadCubeMapTextureData_r3y0ew$('skybox/y-up/sky_ft.jpg', 'skybox/y-up/sky_bk.jpg', 'skybox/y-up/sky_lt.jpg', 'skybox/y-up/sky_rt.jpg', 'skybox/y-up/sky_up.jpg', 'skybox/y-up/sky_dn.jpg', this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      } catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        } else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function testScene$lambda$lambda$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$testScene$lambda$lambda$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function testScene$lambda$lambda$lambda$lambda_0(this$) {
    return function (it) {
      this$.cubeMapSampler.texture = new CubeMapTexture(void 0, testScene$lambda$lambda$lambda$lambda$lambda_0);
      return Unit;
    };
  }
  function testScene$lambda$lambda_0($receiver) {
    $receiver.generate_v2sixm$(testScene$lambda$lambda$lambda_0);
    var $receiver_0 = new ModeledShader$CubeMapColor();
    $receiver_0.onCreated.add_11rb$(testScene$lambda$lambda$lambda$lambda_0($receiver_0));
    $receiver.pipelineLoader = $receiver_0;
    return Unit;
  }
  function testScene$lambda$lambda$lambda_1($receiver) {
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.colored_6taknv$();
    $receiver_0.centered();
    $receiver_0.origin.x = $receiver_0.origin.x - 3.0;
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function testScene$lambda$lambda_1($receiver) {
    $receiver.generate_v2sixm$(testScene$lambda$lambda$lambda_1);
    $receiver.pipelineLoader = new ModeledShader$VertexColor();
    return Unit;
  }
  function testScene$lambda$lambda_2(closure$ctx, this$) {
    return function (envTex) {
      var cubeMapPass = new ReflectionMapPass(envTex);
      var $receiver = closure$ctx.offscreenPasses;
      var element = cubeMapPass.offscreenPass;
      $receiver.add_11rb$(element);
      var skyTex = cubeMapPass.reflectionMap;
      this$.plusAssign_f1kmr1$(new Skybox(skyTex, 1.2));
      return Unit;
    };
  }
  function testScene() {
    var ctx = createDefaultContext();
    ctx.assetMgr.assetsBaseDir = '../assets';
    var tmp$ = ctx.scenes;
    var $receiver = new Scene_init(null);
    defaultCamTransform($receiver);
    $receiver.unaryPlus_uv0sim$(textureMesh(void 0, void 0, testScene$lambda$lambda));
    $receiver.unaryPlus_uv0sim$(textureMesh(void 0, void 0, testScene$lambda$lambda_0));
    $receiver.unaryPlus_uv0sim$(colorMesh(void 0, testScene$lambda$lambda_1));
    ctx.assetMgr.loadAndPrepareTexture_hd4f6p$('skybox/hdri/newport_loft.rgbe.png', void 0, testScene$lambda$lambda_2(ctx, $receiver));
    tmp$.add_11rb$($receiver);
    ctx.run();
  }
  function main() {
    Demo$Companion_getInstance().setProperty_bm4g0d$('assetsBaseDir', '../assets');
    demo(getParams().get_11rb$('demo'));
  }
  function getParams() {
    var tmp$;
    var params = LinkedHashMap_init();
    if (window.location.search.length > 1) {
      var vars = split(window.location.search.substring(1), ['&']);
      tmp$ = vars.iterator();
      while (tmp$.hasNext()) {
        var pair = tmp$.next();
        var keyVal = split(pair, ['=']);
        if (keyVal.size === 2) {
          var keyEnc = keyVal.get_za3lpa$(0);
          var valEnc = keyVal.get_za3lpa$(1);
          var key = decodeURIComponent(keyEnc).toString();
          var value = decodeURIComponent(valEnc).toString();
          params.put_xwzc9p$(key, value);
        }}
    }return params;
  }
  $$importsForInline$$.kooldemo = _;
  var package$de = _.de || (_.de = {});
  var package$fabmax = package$de.fabmax || (package$de.fabmax = {});
  var package$kool = package$fabmax.kool || (package$fabmax.kool = {});
  var package$demo = package$kool.demo || (package$kool.demo = {});
  package$demo.demo_pdl1vj$ = demo;
  Object.defineProperty(Demo, 'Companion', {
    get: Demo$Companion_getInstance
  });
  package$demo.Demo = Demo;
  package$demo.Cycler = Cycler;
  $$importsForInline$$.kool = $module$kool;
  package$demo.helloWorldScene = helloWorldScene;
  package$demo.instanceTest = instanceTest;
  package$demo.MatColorNode = MatColorNode;
  package$demo.multiLightDemo_aemszp$ = multiLightDemo;
  Object.defineProperty(MultiLightDemo, 'Companion', {
    get: MultiLightDemo$Companion_getInstance
  });
  package$demo.MultiLightDemo = MultiLightDemo;
  var package$pbr = package$demo.pbr || (package$demo.pbr = {});
  package$pbr.ColorGridContent = ColorGridContent;
  package$pbr.pbrDemoScene_aemszp$ = pbrDemoScene;
  PbrDemo.PbrContent = PbrDemo$PbrContent;
  Object.defineProperty(PbrDemo, 'Companion', {
    get: PbrDemo$Companion_getInstance
  });
  package$pbr.PbrDemo = PbrDemo;
  PbrMaterialContent.MaterialMaps = PbrMaterialContent$MaterialMaps;
  Object.defineProperty(PbrMaterialContent, 'Companion', {
    get: PbrMaterialContent$Companion_getInstance
  });
  package$pbr.PbrMaterialContent = PbrMaterialContent;
  Object.defineProperty(RoughnesMetalGridContent, 'Companion', {
    get: RoughnesMetalGridContent$Companion_getInstance
  });
  package$pbr.RoughnesMetalGridContent = RoughnesMetalGridContent;
  $$importsForInline$$['kotlinx-serialization-kotlinx-serialization-runtime'] = $module$kotlinx_serialization_kotlinx_serialization_runtime;
  var package$portingNeeded = package$demo.portingNeeded || (package$demo.portingNeeded = {});
  package$portingNeeded.globeScene_aemszp$ = globeScene;
  package$portingNeeded.GlobeUi = GlobeUi;
  package$portingNeeded.makeGroundGrid_24o109$ = makeGroundGrid;
  package$portingNeeded.multiScene_aemszp$ = multiScene;
  package$portingNeeded.particleDemo_aemszp$ = particleDemo;
  package$portingNeeded.pointScene = pointScene;
  package$portingNeeded.makePointMesh = makePointMesh;
  package$portingNeeded.MeshPoint = MeshPoint;
  package$portingNeeded.reflectionDemo_aemszp$ = reflectionDemo;
  package$portingNeeded.simpleShapesScene_aemszp$ = simpleShapesScene;
  package$portingNeeded.synthieScene_aemszp$ = synthieScene;
  package$demo.simplificationDemo_aemszp$ = simplificationDemo;
  package$demo.SimplificationDemo = SimplificationDemo;
  package$demo.treeScene_aemszp$ = treeScene;
  package$demo.TreeGenerator = TreeGenerator;
  package$demo.TreeTopPointDistribution = TreeTopPointDistribution;
  package$demo.uiDemoScene = uiDemoScene;
  _.EquiRectCoords = EquiRectCoords;
  _.DecodeRgbeNode = DecodeRgbeNode;
  _.DecodeAndToneMapRgbeNode = DecodeAndToneMapRgbeNode;
  _.testScene = testScene;
  _.main = main;
  _.getParams = getParams;
  main();
  Kotlin.defineModule('kooldemo', _);
  return _;
});
