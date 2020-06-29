define(['exports', 'kotlin', 'kool'], function (_, Kotlin, $module$kool) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var throwUPAE = Kotlin.throwUPAE;
  var Unit = Kotlin.kotlin.Unit;
  var orbitInputTransform = $module$kool.de.fabmax.kool.scene.orbitInputTransform_uj7ww7$;
  var SimpleShadowMap = $module$kool.de.fabmax.kool.util.SimpleShadowMap;
  var AoPipeline = $module$kool.de.fabmax.kool.util.ao.AoPipeline;
  var IrradianceMapPass = $module$kool.de.fabmax.kool.util.ibl.IrradianceMapPass;
  var ReflectionMapPass = $module$kool.de.fabmax.kool.util.ibl.ReflectionMapPass;
  var BrdfLutPass = $module$kool.de.fabmax.kool.util.ibl.BrdfLutPass;
  var abs = Kotlin.kotlin.math.abs_za3lpa$;
  var Color = $module$kool.de.fabmax.kool.util.Color;
  var Vec3f = $module$kool.de.fabmax.kool.math.Vec3f;
  var Albedo = $module$kool.de.fabmax.kool.pipeline.shading.Albedo;
  var pbrShader = $module$kool.de.fabmax.kool.pipeline.shading.pbrShader_zfwrfj$;
  var colorMesh = $module$kool.de.fabmax.kool.scene.colorMesh_gp9ews$;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var Texture = $module$kool.de.fabmax.kool.pipeline.Texture;
  var textureMesh = $module$kool.de.fabmax.kool.scene.textureMesh_pyaqjj$;
  var Skybox = $module$kool.de.fabmax.kool.scene.Skybox;
  var FilterMethod = $module$kool.de.fabmax.kool.pipeline.FilterMethod;
  var TextureProps = $module$kool.de.fabmax.kool.pipeline.TextureProps;
  var GltfFile$ModelGenerateConfig = $module$kool.de.fabmax.kool.util.gltf.GltfFile.ModelGenerateConfig;
  var first = Kotlin.kotlin.collections.first_7wnvza$;
  var loadGltfModel = $module$kool.de.fabmax.kool.util.gltf.loadGltfModel_4v1054$;
  var scale = $module$kool.de.fabmax.kool.math.scale_2qa7tb$;
  var Font = $module$kool.de.fabmax.kool.util.Font;
  var FontProps = $module$kool.de.fabmax.kool.util.FontProps;
  var uiFont = $module$kool.de.fabmax.kool.util.uiFont_a4r08d$;
  var UiTheme = $module$kool.de.fabmax.kool.scene.ui.UiTheme;
  var BlankComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlankComponentUi;
  var theme = $module$kool.de.fabmax.kool.scene.ui.theme_vvurn$;
  var ModeledShader$TextureColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.TextureColor;
  var transformGroup = $module$kool.de.fabmax.kool.scene.transformGroup_zaezuq$;
  var SimpleComponentUi = $module$kool.de.fabmax.kool.scene.ui.SimpleComponentUi;
  var dps = $module$kool.de.fabmax.kool.scene.ui.dps_8ca0d4$;
  var zero = $module$kool.de.fabmax.kool.scene.ui.zero;
  var full = $module$kool.de.fabmax.kool.scene.ui.full;
  var pcs = $module$kool.de.fabmax.kool.scene.ui.pcs_8ca0d4$;
  var Alignment = $module$kool.de.fabmax.kool.scene.ui.Alignment;
  var Gravity = $module$kool.de.fabmax.kool.scene.ui.Gravity;
  var toString = $module$kool.de.fabmax.kool.toString_lcymw2$;
  var uiScene = $module$kool.de.fabmax.kool.scene.ui.uiScene_m9o5w1$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var ShaderModel = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderModel;
  var ShaderNode = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderNode;
  var ModelVar4f = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar4f;
  var ShaderNodeIoVar = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderNodeIoVar;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var BufferedTextureData = $module$kool.de.fabmax.kool.pipeline.BufferedTextureData;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var Math_0 = Math;
  var math = $module$kool.de.fabmax.kool.math;
  var addAll = Kotlin.kotlin.collections.addAll_ipc267$;
  var Scene_init = $module$kool.de.fabmax.kool.scene.Scene;
  var roundToInt = Kotlin.kotlin.math.roundToInt_yrwdxr$;
  var ShaderModel$ShaderModel$VertexStageBuilder_init = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderModel.VertexStageBuilder;
  var ShaderModel$ShaderModel$FragmentStageBuilder_init = $module$kool.de.fabmax.kool.pipeline.shadermodel.ShaderModel.FragmentStageBuilder;
  var DeferredMrtPass = $module$kool.de.fabmax.kool.util.deferred.DeferredMrtPass;
  var PbrSceneShader$DeferredPbrConfig = $module$kool.de.fabmax.kool.util.deferred.PbrSceneShader.DeferredPbrConfig;
  var PbrLightingPass = $module$kool.de.fabmax.kool.util.deferred.PbrLightingPass;
  var DeferredOutputShader = $module$kool.de.fabmax.kool.util.deferred.DeferredOutputShader;
  var ModeledShader = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader;
  var wireframeMesh = $module$kool.de.fabmax.kool.scene.wireframeMesh_hjnvxh$;
  var MeshInstanceList = $module$kool.de.fabmax.kool.util.MeshInstanceList;
  var Attribute = $module$kool.de.fabmax.kool.pipeline.Attribute;
  var Mat4f = $module$kool.de.fabmax.kool.math.Mat4f;
  var IndexedVertexList_init = $module$kool.de.fabmax.kool.util.IndexedVertexList_init_5jr6ei$;
  var MeshBuilder = $module$kool.de.fabmax.kool.util.MeshBuilder;
  var get_indices = Kotlin.kotlin.collections.get_indices_gzk92b$;
  var DeferredMrtShader = $module$kool.de.fabmax.kool.util.deferred.DeferredMrtShader;
  var DeferredMrtShader$MrtPbrConfig = $module$kool.de.fabmax.kool.util.deferred.DeferredMrtShader.MrtPbrConfig;
  var get_lastIndex = Kotlin.kotlin.collections.get_lastIndex_55thoc$;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var Vec2f = $module$kool.de.fabmax.kool.math.Vec2f;
  var ModeledShader$StaticColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.StaticColor;
  var numberToInt = Kotlin.numberToInt;
  var MutableVec3f_init = $module$kool.de.fabmax.kool.math.MutableVec3f_init;
  var ModelVar1fConst = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar1fConst;
  var TextureNode = $module$kool.de.fabmax.kool.pipeline.shadermodel.TextureNode;
  var ensureNotNull = Kotlin.ensureNotNull;
  var GlslType = $module$kool.de.fabmax.kool.pipeline.GlslType;
  var Random = $module$kool.de.fabmax.kool.math.Random;
  var listOf_0 = Kotlin.kotlin.collections.listOf_mh5how$;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var DeferredPointLights$DeferredPointLights$PointLight_init = $module$kool.de.fabmax.kool.util.deferred.DeferredPointLights.PointLight;
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  var ShaderStage = $module$kool.de.fabmax.kool.pipeline.ShaderStage;
  var wrapFunction = Kotlin.wrapFunction;
  var equals = Kotlin.equals;
  var throwCCE = Kotlin.throwCCE;
  var createDefaultContext = $module$kool.de.fabmax.kool.createDefaultContext;
  var BlurredComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlurredComponentUi;
  var getCallableRef = Kotlin.getCallableRef;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var DebugOverlay$Position = $module$kool.de.fabmax.kool.util.DebugOverlay.Position;
  var DebugOverlay = $module$kool.de.fabmax.kool.util.DebugOverlay;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mutableMapOf = Kotlin.kotlin.collections.mutableMapOf_qfcya0$;
  var List = Kotlin.kotlin.collections.List;
  var Map = Kotlin.kotlin.collections.Map;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var MutableVec3d_init = $module$kool.de.fabmax.kool.math.MutableVec3d_init_czzhiw$;
  var Light = $module$kool.de.fabmax.kool.scene.Light;
  var Vec3d = $module$kool.de.fabmax.kool.math.Vec3d;
  var math_0 = Kotlin.kotlin.math;
  var MutableVec2f = $module$kool.de.fabmax.kool.math.MutableVec2f;
  var TransformGroup = $module$kool.de.fabmax.kool.scene.TransformGroup;
  var defaultCamTransform = $module$kool.de.fabmax.kool.scene.defaultCamTransform_v4keia$;
  var PerspectiveCamera = $module$kool.de.fabmax.kool.scene.PerspectiveCamera;
  var OrbitInputTransform$ZoomMethod = $module$kool.de.fabmax.kool.scene.OrbitInputTransform.ZoomMethod;
  var Vec3f_init = $module$kool.de.fabmax.kool.math.Vec3f_init_mx4ult$;
  var PhongShader = $module$kool.de.fabmax.kool.pipeline.shading.PhongShader;
  var randomF = $module$kool.de.fabmax.kool.math.randomF_dleff0$;
  var MutableVec3f = $module$kool.de.fabmax.kool.math.MutableVec3f;
  var InstancedLodController$Instance = $module$kool.de.fabmax.kool.util.InstancedLodController.Instance;
  var MutableColor_init = $module$kool.de.fabmax.kool.util.MutableColor_init;
  var InstancedLodController = $module$kool.de.fabmax.kool.util.InstancedLodController;
  var MutableColor_init_0 = $module$kool.de.fabmax.kool.util.MutableColor_init_d7aj7k$;
  var mutableListOf = Kotlin.kotlin.collections.mutableListOf_i5x0yv$;
  var PbrShader = $module$kool.de.fabmax.kool.pipeline.shading.PbrShader;
  var PbrShader$PbrConfig = $module$kool.de.fabmax.kool.pipeline.shading.PbrShader.PbrConfig;
  var PhongShader$PhongConfig = $module$kool.de.fabmax.kool.pipeline.shading.PhongShader.PhongConfig;
  var LineMesh = $module$kool.de.fabmax.kool.scene.LineMesh;
  var group = $module$kool.de.fabmax.kool.scene.group_2ylazs$;
  var IndexedVertexList_init_0 = $module$kool.de.fabmax.kool.util.IndexedVertexList;
  var Mesh_init = $module$kool.de.fabmax.kool.scene.Mesh;
  var Array_0 = Array;
  var SingleColorTexture = $module$kool.de.fabmax.kool.pipeline.SingleColorTexture;
  var PerfTimer = $module$kool.de.fabmax.kool.util.PerfTimer;
  var ListEdgeHandler = $module$kool.de.fabmax.kool.modules.mesh.ListEdgeHandler;
  var HalfEdgeMesh = $module$kool.de.fabmax.kool.modules.mesh.HalfEdgeMesh;
  var terminateOnFaceCountRel = $module$kool.de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel_14dthe$;
  var simplify = $module$kool.de.fabmax.kool.modules.mesh.simplification.simplify_hem9$;
  var toString_0 = $module$kool.de.fabmax.kool.toString_j6vyb1$;
  var lastIndexOf = Kotlin.kotlin.text.lastIndexOf_8eortd$;
  var lastIndexOf_0 = Kotlin.kotlin.text.lastIndexOf_l5u8uk$;
  var substring = Kotlin.kotlin.text.substring_fc3b62$;
  var toString_1 = Kotlin.toString;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  var util = $module$kool.de.fabmax.kool.util;
  var Log$Level = $module$kool.de.fabmax.kool.util.Log.Level;
  var CascadedShadowMap = $module$kool.de.fabmax.kool.util.CascadedShadowMap;
  var AlphaModeOpaque = $module$kool.de.fabmax.kool.pipeline.shading.AlphaModeOpaque;
  var PushConstantNode1f = $module$kool.de.fabmax.kool.pipeline.shadermodel.PushConstantNode1f;
  var AlphaModeMask = $module$kool.de.fabmax.kool.pipeline.shading.AlphaModeMask;
  var CullMethod = $module$kool.de.fabmax.kool.pipeline.CullMethod;
  var ModelVar3fConst = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar3fConst;
  var ModelVar3f = $module$kool.de.fabmax.kool.pipeline.shadermodel.ModelVar3f;
  var AlphaModeBlend = $module$kool.de.fabmax.kool.pipeline.shading.AlphaModeBlend;
  var now = $module$kool.de.fabmax.kool.now;
  var MutableVec3f_init_0 = $module$kool.de.fabmax.kool.math.MutableVec3f_init_czzhiu$;
  var pointKdTree = $module$kool.de.fabmax.kool.util.pointKdTree_ffk80x$;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var Vec3f_init_0 = $module$kool.de.fabmax.kool.math.Vec3f_init_czzhiu$;
  var InRadiusTraverser = $module$kool.de.fabmax.kool.util.InRadiusTraverser;
  var PointDistribution = $module$kool.de.fabmax.kool.math.PointDistribution;
  var MutableVec2f_init = $module$kool.de.fabmax.kool.math.MutableVec2f_init;
  var BSplineVec2f = $module$kool.de.fabmax.kool.math.BSplineVec2f;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var ModeledShader$VertexColor = $module$kool.de.fabmax.kool.pipeline.shading.ModeledShader.VertexColor;
  var dp = $module$kool.de.fabmax.kool.scene.ui.dp_wl4j30$;
  var uns = $module$kool.de.fabmax.kool.scene.ui.uns_8ca0d4$;
  var embeddedUi = $module$kool.de.fabmax.kool.scene.ui.embeddedUi_4gy91$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  AoDemo$Companion$Red2GrayNode.prototype = Object.create(ShaderNode.prototype);
  AoDemo$Companion$Red2GrayNode.prototype.constructor = AoDemo$Companion$Red2GrayNode;
  DeferredDemo$MetalRoughAoTex.prototype = Object.create(ModeledShader.prototype);
  DeferredDemo$MetalRoughAoTex.prototype.constructor = DeferredDemo$MetalRoughAoTex;
  InstanceDemo$BunnyInstance.prototype = Object.create(InstancedLodController$Instance.prototype);
  InstanceDemo$BunnyInstance.prototype.constructor = InstanceDemo$BunnyInstance;
  MultiLightDemo$LightMesh.prototype = Object.create(TransformGroup.prototype);
  MultiLightDemo$LightMesh.prototype.constructor = MultiLightDemo$LightMesh;
  ColorGridContent.prototype = Object.create(PbrDemo$PbrContent.prototype);
  ColorGridContent.prototype.constructor = ColorGridContent;
  PbrMaterialContent.prototype = Object.create(PbrDemo$PbrContent.prototype);
  PbrMaterialContent.prototype.constructor = PbrMaterialContent;
  RoughnesMetalGridContent.prototype = Object.create(PbrDemo$PbrContent.prototype);
  RoughnesMetalGridContent.prototype.constructor = RoughnesMetalGridContent;
  WindNode.prototype = Object.create(ShaderNode.prototype);
  WindNode.prototype.constructor = WindNode;
  TreeGenerator$AttractionPoint.prototype = Object.create(MutableVec3f.prototype);
  TreeGenerator$AttractionPoint.prototype.constructor = TreeGenerator$AttractionPoint;
  TreeGenerator$TreeNode.prototype = Object.create(MutableVec3f.prototype);
  TreeGenerator$TreeNode.prototype.constructor = TreeGenerator$TreeNode;
  TreeTopPointDistribution.prototype = Object.create(PointDistribution.prototype);
  TreeTopPointDistribution.prototype.constructor = TreeTopPointDistribution;
  function aoDemo(ctx) {
    var aoDemo = new AoDemo(ctx);
    return listOf([aoDemo.mainScene, aoDemo.menu]);
  }
  function AoDemo(ctx) {
    AoDemo$Companion_getInstance();
    this.mainScene = null;
    this.menu = null;
    this.autoRotate_0 = true;
    this.spotLight_0 = true;
    this.noAoMap_0 = new Texture(void 0, void 0, AoDemo$noAoMap$lambda);
    this.aoPipeline_953qvj$_0 = this.aoPipeline_953qvj$_0;
    this.shadows_0 = ArrayList_init();
    this.mainScene = this.makeMainScene_0(ctx);
    this.menu = this.menu_0(ctx);
    this.updateLighting_0();
  }
  Object.defineProperty(AoDemo.prototype, 'aoPipeline_0', {
    get: function () {
      if (this.aoPipeline_953qvj$_0 == null)
        return throwUPAE('aoPipeline');
      return this.aoPipeline_953qvj$_0;
    },
    set: function (aoPipeline) {
      this.aoPipeline_953qvj$_0 = aoPipeline;
    }
  });
  function AoDemo$makeMainScene$lambda$lambda$lambda(this$AoDemo, closure$ctx, this$) {
    return function ($receiver, f, f_0) {
      if (this$AoDemo.autoRotate_0) {
        this$.verticalRotation += closure$ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda(this$, this$AoDemo, closure$ctx) {
    return function ($receiver) {
      $receiver.setMouseRotation_dleff0$(0.0, -20.0);
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.zoom = 8.0;
      var $receiver_0 = $receiver.onUpdate;
      var element = AoDemo$makeMainScene$lambda$lambda$lambda(this$AoDemo, closure$ctx, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda_0(this$AoDemo) {
    return function ($receiver, it) {
      this$AoDemo.noAoMap_0.dispose();
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda(closure$teapotMesh) {
    return function ($receiver) {
      for (var x = -3; x <= 3; x++) {
        for (var y = -3; y <= 3; y++) {
          var h = Math_0.atan2(y, x) * math.RAD_2_DEG;
          var a = abs(x);
          var b = abs(y);
          var s = Math_0.max(a, b) / 5.0;
          $receiver.color = Color.Companion.fromHsv_7b5o5w$(h, s, 0.75, 1.0).toLinear();
          $receiver.transform.push();
          var closure$teapotMesh_0 = closure$teapotMesh;
          $receiver.translate_y2kzbl$(x, 0.0, y);
          $receiver.scale_y2kzbl$(0.25, 0.25, 0.25);
          $receiver.rotate_ad55pp$(-37.5, Vec3f.Companion.Y_AXIS);
          $receiver.geometry_ejqx55$(closure$teapotMesh_0.geometry);
          $receiver.transform.pop();
        }
      }
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda_0(this$AoDemo, closure$irrMapPass, closure$reflMapPass, closure$brdfLutPass) {
    return function ($receiver) {
      $receiver.albedoSource = Albedo.VERTEX_ALBEDO;
      addAll($receiver.shadowMaps, this$AoDemo.shadows_0);
      $receiver.roughness = 0.1;
      $receiver.isScrSpcAmbientOcclusion = true;
      $receiver.scrSpcAmbientOcclusionMap = this$AoDemo.aoPipeline_0.aoMap;
      $receiver.isImageBasedLighting = true;
      $receiver.irradianceMap = closure$irrMapPass.colorTextureCube;
      $receiver.reflectionMap = closure$reflMapPass.colorTextureCube;
      $receiver.brdfLut = closure$brdfLutPass.colorTexture;
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda_1(this$AoDemo, closure$shader) {
    return function ($receiver, f, f_0) {
      if (this$AoDemo.aoPipeline_0.aoPass.isEnabled) {
        closure$shader.scrSpcAmbientOcclusionMap = this$AoDemo.aoPipeline_0.aoMap;
      } else {
        closure$shader.scrSpcAmbientOcclusionMap = this$AoDemo.noAoMap_0;
      }
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda_0(closure$teapotMesh, this$AoDemo, closure$irrMapPass, closure$reflMapPass, closure$brdfLutPass) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(AoDemo$makeMainScene$lambda$lambda$lambda$lambda(closure$teapotMesh));
      var shader = pbrShader(AoDemo$makeMainScene$lambda$lambda$lambda$lambda_0(this$AoDemo, closure$irrMapPass, closure$reflMapPass, closure$brdfLutPass));
      $receiver.pipelineLoader = shader;
      var $receiver_0 = $receiver.onUpdate;
      var element = AoDemo$makeMainScene$lambda$lambda$lambda$lambda_1(this$AoDemo, shader);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda_2(this$AoDemo) {
    return function ($receiver) {
      var texScale = 0.1955;
      $receiver.transform.push();
      var this$AoDemo_0 = this$AoDemo;
      $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.NEG_X_AXIS);
      var $receiver_0 = $receiver.rectProps.defaults();
      $receiver_0.size.set_dleff0$(12.0, 12.0);
      $receiver_0.origin.set_y2kzbl$($receiver_0.size.x, $receiver_0.size.y, 0.0).scale_mx4ult$(-0.5);
      this$AoDemo_0.setUvs_0($receiver_0, 0.06, 0.0, $receiver_0.size.x * texScale, $receiver_0.size.y * texScale);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      $receiver.transform.push();
      var this$AoDemo_1 = this$AoDemo;
      $receiver.translate_y2kzbl$(0.0, -0.25, 0.0);
      $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.X_AXIS);
      var $receiver_1 = $receiver.rectProps.defaults();
      $receiver_1.size.set_dleff0$(12.0, 12.0);
      $receiver_1.origin.set_y2kzbl$($receiver_1.size.x, $receiver_1.size.y, 0.0).scale_mx4ult$(-0.5);
      this$AoDemo_1.setUvs_0($receiver_1, 0.06, 0.0, $receiver_1.size.x * texScale, $receiver_1.size.y * texScale);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      $receiver.transform.push();
      var this$AoDemo_2 = this$AoDemo;
      $receiver.translate_y2kzbl$(-6.0, -0.125, 0.0);
      $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.NEG_Y_AXIS);
      $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.Z_AXIS);
      var $receiver_2 = $receiver.rectProps.defaults();
      $receiver_2.size.set_dleff0$(0.25, 12.0);
      $receiver_2.origin.set_y2kzbl$($receiver_2.size.x, $receiver_2.size.y, 0.0).scale_mx4ult$(-0.5);
      this$AoDemo_2.setUvs_0($receiver_2, 0.06 - $receiver_2.size.x * texScale, 0.0, $receiver_2.size.x * texScale, $receiver_2.size.y * texScale);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      $receiver.transform.push();
      var this$AoDemo_3 = this$AoDemo;
      $receiver.translate_y2kzbl$(6.0, -0.125, 0.0);
      $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.Y_AXIS);
      $receiver.rotate_ad55pp$(-90.0, Vec3f.Companion.Z_AXIS);
      var $receiver_3 = $receiver.rectProps.defaults();
      $receiver_3.size.set_dleff0$(0.25, 12.0);
      $receiver_3.origin.set_y2kzbl$($receiver_3.size.x, $receiver_3.size.y, 0.0).scale_mx4ult$(-0.5);
      this$AoDemo_3.setUvs_0($receiver_3, 0.06 + 12 * texScale, 0.0, $receiver_3.size.x * texScale, $receiver_3.size.y * texScale);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      $receiver.transform.push();
      var this$AoDemo_4 = this$AoDemo;
      $receiver.translate_y2kzbl$(0.0, -0.125, 6.0);
      var $receiver_4 = $receiver.rectProps.defaults();
      $receiver_4.size.set_dleff0$(12.0, 0.25);
      $receiver_4.origin.set_y2kzbl$($receiver_4.size.x, $receiver_4.size.y, 0.0).scale_mx4ult$(-0.5);
      this$AoDemo_4.setUvs_0($receiver_4, 0.06, 12.0 * texScale, $receiver_4.size.x * texScale, $receiver_4.size.y * texScale);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      $receiver.transform.push();
      var this$AoDemo_5 = this$AoDemo;
      $receiver.translate_y2kzbl$(0.0, -0.125, -6.0);
      $receiver.rotate_ad55pp$(180.0, Vec3f.Companion.X_AXIS);
      var $receiver_5 = $receiver.rectProps.defaults();
      $receiver_5.size.set_dleff0$(12.0, 0.25);
      $receiver_5.origin.set_y2kzbl$($receiver_5.size.x, $receiver_5.size.y, 0.0).scale_mx4ult$(-0.5);
      this$AoDemo_5.setUvs_0($receiver_5, 0.06, -0.25 * texScale, $receiver_5.size.x * texScale, $receiver_5.size.y * texScale);
      $receiver.rect_e5k3t5$($receiver.rectProps);
      $receiver.transform.pop();
      return Unit;
    };
  }
  function Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda.prototype.constructor = Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda;
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_planks_03/brown_planks_03_diff_2k.jpg', this);
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
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0.prototype.constructor = Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0;
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_planks_03/brown_planks_03_AO_2k.jpg', this);
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
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1.prototype.constructor = Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1;
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_planks_03/brown_planks_03_Nor_2k.jpg', this);
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
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2.prototype.constructor = Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2;
  Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/brown_planks_03/brown_planks_03_rough_2k.jpg', this);
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
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda_3(this$AoDemo, closure$irrMapPass, closure$reflMapPass, closure$brdfLutPass) {
    return function ($receiver) {
      $receiver.albedoSource = Albedo.TEXTURE_ALBEDO;
      addAll($receiver.shadowMaps, this$AoDemo.shadows_0);
      $receiver.isScrSpcAmbientOcclusion = true;
      $receiver.scrSpcAmbientOcclusionMap = this$AoDemo.aoPipeline_0.aoMap;
      $receiver.isImageBasedLighting = true;
      $receiver.irradianceMap = closure$irrMapPass.colorTextureCube;
      $receiver.reflectionMap = closure$reflMapPass.colorTextureCube;
      $receiver.brdfLut = closure$brdfLutPass.colorTexture;
      $receiver.isNormalMapped = true;
      $receiver.isRoughnessMapped = true;
      $receiver.isAmbientOcclusionMapped = true;
      $receiver.albedoMap = new Texture(void 0, void 0, AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda);
      $receiver.ambientOcclusionMap = new Texture(void 0, void 0, AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_0);
      $receiver.normalMap = new Texture(void 0, void 0, AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_1);
      $receiver.roughnessMap = new Texture(void 0, void 0, AoDemo$makeMainScene$lambda$lambda$lambda$lambda$lambda_2);
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda_4(closure$shader, closure$hdriMap) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
      (tmp$ = closure$shader.albedoMap) != null ? (tmp$.dispose(), Unit) : null;
      (tmp$_0 = closure$shader.ambientOcclusionMap) != null ? (tmp$_0.dispose(), Unit) : null;
      (tmp$_1 = closure$shader.normalMap) != null ? (tmp$_1.dispose(), Unit) : null;
      (tmp$_2 = closure$shader.roughnessMap) != null ? (tmp$_2.dispose(), Unit) : null;
      (tmp$_3 = closure$shader.metallicMap) != null ? (tmp$_3.dispose(), Unit) : null;
      (tmp$_4 = closure$shader.displacementMap) != null ? (tmp$_4.dispose(), Unit) : null;
      closure$hdriMap.dispose();
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda$lambda_5(this$AoDemo, closure$shader) {
    return function ($receiver, f, f_0) {
      if (this$AoDemo.aoPipeline_0.aoPass.isEnabled) {
        closure$shader.scrSpcAmbientOcclusionMap = this$AoDemo.aoPipeline_0.aoMap;
      } else {
        closure$shader.scrSpcAmbientOcclusionMap = this$AoDemo.noAoMap_0;
      }
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda$lambda_1(this$AoDemo, closure$irrMapPass, closure$reflMapPass, closure$brdfLutPass, closure$hdriMap) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(AoDemo$makeMainScene$lambda$lambda$lambda$lambda_2(this$AoDemo));
      var shader = pbrShader(AoDemo$makeMainScene$lambda$lambda$lambda$lambda_3(this$AoDemo, closure$irrMapPass, closure$reflMapPass, closure$brdfLutPass));
      $receiver.pipelineLoader = shader;
      var $receiver_0 = $receiver.onDispose;
      var element = AoDemo$makeMainScene$lambda$lambda$lambda$lambda_4(shader, closure$hdriMap);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = $receiver.onUpdate;
      var element_0 = AoDemo$makeMainScene$lambda$lambda$lambda$lambda_5(this$AoDemo, shader);
      $receiver_1.add_11rb$(element_0);
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda_1(this$, this$AoDemo) {
    return function (teapotMesh, hdriMap) {
      var irrMapPass = new IrradianceMapPass(this$, hdriMap);
      var reflMapPass = new ReflectionMapPass(this$, hdriMap);
      var brdfLutPass = new BrdfLutPass(this$);
      this$.unaryPlus_uv0sim$(colorMesh('teapots', AoDemo$makeMainScene$lambda$lambda$lambda_0(teapotMesh, this$AoDemo, irrMapPass, reflMapPass, brdfLutPass)));
      this$.unaryPlus_uv0sim$(textureMesh('ground', true, AoDemo$makeMainScene$lambda$lambda$lambda_1(this$AoDemo, irrMapPass, reflMapPass, brdfLutPass, hdriMap)));
      this$.plusAssign_f1kmr1$(new Skybox(reflMapPass.colorTextureCube, 1.0));
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda_2(closure$loadingAssets) {
    return function (tex) {
      closure$loadingAssets.hdriMap = tex;
      return Unit;
    };
  }
  function AoDemo$makeMainScene$lambda$lambda_3(closure$loadingAssets) {
    return function (it) {
      var tmp$, tmp$_0, tmp$_1;
      var modelCfg = new GltfFile$ModelGenerateConfig(true, void 0, void 0, void 0, void 0, false);
      closure$loadingAssets.teapotMesh = (tmp$_1 = (tmp$_0 = (tmp$ = it != null ? it.makeModel_m0hq3v$(modelCfg) : null) != null ? tmp$.meshes : null) != null ? tmp$_0.values : null) != null ? first(tmp$_1) : null;
      return Unit;
    };
  }
  AoDemo.prototype.makeMainScene_0 = function (ctx) {
    var $receiver = new Scene_init(null);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform($receiver, void 0, AoDemo$makeMainScene$lambda$lambda($receiver, this, ctx)));
    this.shadows_0.add_11rb$(new SimpleShadowMap($receiver, 0, 2048));
    this.aoPipeline_0 = AoPipeline.Companion.createForward_ushge7$($receiver);
    $receiver.onDispose.add_11rb$(AoDemo$makeMainScene$lambda$lambda_0(this));
    var loadingAssets = new AoDemo$LoadingAssets(AoDemo$makeMainScene$lambda$lambda_1($receiver, this));
    var hdriTexProps = new TextureProps(void 0, void 0, void 0, void 0, FilterMethod.NEAREST, FilterMethod.NEAREST, true);
    ctx.assetMgr.loadAndPrepareTexture_hd4f6p$(Demo$Companion_getInstance().envMapBasePath + '/mossy_forest_1k.rgbe.png', hdriTexProps, AoDemo$makeMainScene$lambda$lambda_2(loadingAssets));
    loadGltfModel(ctx.assetMgr, Demo$Companion_getInstance().modelBasePath + '/teapot.gltf.gz', AoDemo$makeMainScene$lambda$lambda_3(loadingAssets));
    return $receiver;
  };
  AoDemo.prototype.setUvs_0 = function ($receiver, u, v, width, height) {
    $receiver.texCoordUpperLeft.set_dleff0$(u, v);
    $receiver.texCoordUpperRight.set_dleff0$(u + width, v);
    $receiver.texCoordLowerLeft.set_dleff0$(u, v + height);
    $receiver.texCoordLowerRight.set_dleff0$(u + width, v + height);
  };
  function AoDemo$updateLighting$lambda($receiver) {
    var p = new Vec3f(6.0, 10.0, -6.0);
    $receiver.setSpot_nve3wz$(p, scale(p, -1.0).norm(), 40.0);
    $receiver.setColor_y83vuj$(Color.Companion.WHITE.mix_y83vuj$(Color.Companion.MD_AMBER, 0.2).toLinear(), 500.0);
    return Unit;
  }
  AoDemo.prototype.updateLighting_0 = function () {
    if (this.spotLight_0) {
      this.mainScene.lighting.singleLight_q9zcvo$(AoDemo$updateLighting$lambda);
    } else {
      this.mainScene.lighting.lights.clear();
    }
    var tmp$;
    tmp$ = this.shadows_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.isShadowMapEnabled = this.spotLight_0;
    }
  };
  function AoDemo$menu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function AoDemo$menu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function AoDemo$menu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(AoDemo$menu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(AoDemo$menu$lambda$lambda$lambda_0);
    return Unit;
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda($receiver) {
    var $receiver_0 = $receiver.rectProps.defaults();
    $receiver_0.size.set_dleff0$(1.0, 1.0);
    $receiver_0.mirrorTexCoordsY();
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function AoDemo$menu$lambda$lambda$lambda_1(this$AoDemo) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(AoDemo$menu$lambda$lambda$lambda$lambda);
      $receiver.pipelineLoader = new ModeledShader$TextureColor(this$AoDemo.aoPipeline_0.aoMap, 'colorTex', AoDemo$Companion_getInstance().aoMapColorModel());
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_2(this$AoDemo, this$) {
    return function ($receiver, rp, f) {
      var screenSz = 0.33;
      var scaleX = rp.viewport.width * screenSz;
      var scaleY = scaleX * (this$AoDemo.aoPipeline_0.denoisePass.texHeight / this$AoDemo.aoPipeline_0.denoisePass.texWidth);
      this$.setIdentity();
      var margin = rp.viewport.height * 0.05;
      this$.translate_y2kzbl$(margin, margin, 0.0);
      this$.scale_y2kzbl$(scaleX, scaleY, 1.0);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda_0(this$AoDemo) {
    return function ($receiver) {
      $receiver.isVisible = false;
      $receiver.unaryPlus_uv0sim$(textureMesh(void 0, void 0, AoDemo$menu$lambda$lambda$lambda_1(this$AoDemo)));
      var $receiver_0 = $receiver.onUpdate;
      var element = AoDemo$menu$lambda$lambda$lambda_2(this$AoDemo, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_3(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_0(this$AoDemo) {
    return function ($receiver) {
      this$AoDemo.aoPipeline_0.setEnabled_6taknv$($receiver.isEnabled);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_4(closure$y, this$AoDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$AoDemo.aoPipeline_0.aoPass.isEnabled;
      var $receiver_0 = $receiver.onStateChange;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_0(this$AoDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_1(closure$aoMap) {
    return function ($receiver) {
      closure$aoMap.isVisible = $receiver.isEnabled;
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_5(closure$y, closure$aoMap) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = closure$aoMap.isVisible;
      var $receiver_0 = $receiver.onStateChange;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_1(closure$aoMap);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_6(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_7(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_2(closure$radiusVal, this$AoDemo) {
    return function ($receiver, it) {
      closure$radiusVal.text = toString($receiver.value, 2);
      this$AoDemo.aoPipeline_0.radius = $receiver.value;
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_8(closure$y, closure$radiusVal, this$AoDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_2(closure$radiusVal, this$AoDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_9(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_10(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_3(closure$intensityVal, this$AoDemo) {
    return function ($receiver, it) {
      closure$intensityVal.text = toString($receiver.value, 2);
      this$AoDemo.aoPipeline_0.intensity = $receiver.value;
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_11(closure$y, closure$intensityVal, this$AoDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_3(closure$intensityVal, this$AoDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_12(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_13(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_4(closure$biasVal, this$AoDemo) {
    return function ($receiver, it) {
      closure$biasVal.text = toString($receiver.value, 2);
      this$AoDemo.aoPipeline_0.bias = $receiver.value;
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_14(closure$y, closure$biasVal, this$AoDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_4(closure$biasVal, this$AoDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_15(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_16(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_5(this$AoDemo, closure$kernelSzVal) {
    return function ($receiver, it) {
      this$AoDemo.aoPipeline_0.aoPass.kernelSz = roundToInt($receiver.value);
      closure$kernelSzVal.text = this$AoDemo.aoPipeline_0.kernelSz.toString();
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_17(closure$y, this$AoDemo, closure$kernelSzVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_5(this$AoDemo, closure$kernelSzVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_18(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_19(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_6(this$AoDemo, closure$mapSzVal) {
    return function ($receiver, it) {
      this$AoDemo.aoPipeline_0.size = roundToInt($receiver.value) / 10.0;
      closure$mapSzVal.text = toString(this$AoDemo.aoPipeline_0.size, 1) + ' x';
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_20(closure$y, this$AoDemo, closure$mapSzVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_6(this$AoDemo, closure$mapSzVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_21(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_7(this$AoDemo) {
    return function ($receiver) {
      this$AoDemo.autoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_22(closure$y, this$AoDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$AoDemo.autoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_7(this$AoDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda$lambda_8(this$AoDemo) {
    return function ($receiver) {
      this$AoDemo.spotLight_0 = $receiver.isEnabled;
      this$AoDemo.updateLighting_0();
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda$lambda_23(closure$y, this$AoDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$AoDemo.spotLight_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = AoDemo$menu$lambda$lambda$lambda$lambda_8(this$AoDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function AoDemo$menu$lambda$lambda_1(closure$smallFont, this$, this$AoDemo, closure$aoMap) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-370.0), dps(-705.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(250.0), dps(585.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Ambient Occulsion', AoDemo$menu$lambda$lambda$lambda_3(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Enabled', AoDemo$menu$lambda$lambda$lambda_4(y, this$AoDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Show AO Map', AoDemo$menu$lambda$lambda$lambda_5(y, closure$aoMap)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Radius:', AoDemo$menu$lambda$lambda$lambda_6(y)));
      var radiusVal = this$.label_tokfmu$(toString(this$AoDemo.aoPipeline_0.aoPass.radius, 2), AoDemo$menu$lambda$lambda$lambda_7(y));
      $receiver.unaryPlus_uv0sim$(radiusVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('radiusSlider', 0.1, 3.0, this$AoDemo.aoPipeline_0.aoPass.radius, AoDemo$menu$lambda$lambda$lambda_8(y, radiusVal, this$AoDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Intensity:', AoDemo$menu$lambda$lambda$lambda_9(y)));
      var intensityVal = this$.label_tokfmu$(toString(this$AoDemo.aoPipeline_0.aoPass.intensity, 2), AoDemo$menu$lambda$lambda$lambda_10(y));
      $receiver.unaryPlus_uv0sim$(intensityVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('intensitySlider', 0.0, 5.0, this$AoDemo.aoPipeline_0.aoPass.intensity, AoDemo$menu$lambda$lambda$lambda_11(y, intensityVal, this$AoDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Bias:', AoDemo$menu$lambda$lambda$lambda_12(y)));
      var biasVal = this$.label_tokfmu$(toString(this$AoDemo.aoPipeline_0.aoPass.bias, 2), AoDemo$menu$lambda$lambda$lambda_13(y));
      $receiver.unaryPlus_uv0sim$(biasVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('biasSlider', -0.5, 0.5, this$AoDemo.aoPipeline_0.aoPass.bias, AoDemo$menu$lambda$lambda$lambda_14(y, biasVal, this$AoDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('AO Samples:', AoDemo$menu$lambda$lambda$lambda_15(y)));
      var kernelSzVal = this$.label_tokfmu$(this$AoDemo.aoPipeline_0.aoPass.kernelSz.toString(), AoDemo$menu$lambda$lambda$lambda_16(y));
      $receiver.unaryPlus_uv0sim$(kernelSzVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('kernelSlider', 4.0, 128.0, this$AoDemo.aoPipeline_0.aoPass.kernelSz, AoDemo$menu$lambda$lambda$lambda_17(y, this$AoDemo, kernelSzVal)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Map Size:', AoDemo$menu$lambda$lambda$lambda_18(y)));
      var mapSzVal = this$.label_tokfmu$(toString(this$AoDemo.aoPipeline_0.size, 1) + ' x', AoDemo$menu$lambda$lambda$lambda_19(y));
      $receiver.unaryPlus_uv0sim$(mapSzVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('mapSizeSlider', 1.0, 10.0, this$AoDemo.aoPipeline_0.size * 10, AoDemo$menu$lambda$lambda$lambda_20(y, this$AoDemo, mapSzVal)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Scene', AoDemo$menu$lambda$lambda$lambda_21(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', AoDemo$menu$lambda$lambda$lambda_22(y, this$AoDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Spot Light', AoDemo$menu$lambda$lambda$lambda_23(y, this$AoDemo)));
      return Unit;
    };
  }
  function AoDemo$menu$lambda(closure$ctx, this$AoDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, AoDemo$menu$lambda$lambda);
      var aoMap = transformGroup(void 0, AoDemo$menu$lambda$lambda_0(this$AoDemo));
      $receiver.unaryPlus_uv0sim$(aoMap);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', AoDemo$menu$lambda$lambda_1(smallFont, $receiver, this$AoDemo, aoMap)));
      return Unit;
    };
  }
  AoDemo.prototype.menu_0 = function (ctx) {
    return uiScene(void 0, void 0, void 0, AoDemo$menu$lambda(ctx, this));
  };
  function AoDemo$LoadingAssets(block) {
    this.block = block;
    this.teapotMesh_cuy05e$_0 = null;
    this.hdriMap_1c7tuj$_0 = null;
  }
  Object.defineProperty(AoDemo$LoadingAssets.prototype, 'teapotMesh', {
    get: function () {
      return this.teapotMesh_cuy05e$_0;
    },
    set: function (value) {
      this.teapotMesh_cuy05e$_0 = value;
      this.check();
    }
  });
  Object.defineProperty(AoDemo$LoadingAssets.prototype, 'hdriMap', {
    get: function () {
      return this.hdriMap_1c7tuj$_0;
    },
    set: function (value) {
      this.hdriMap_1c7tuj$_0 = value;
      this.check();
    }
  });
  AoDemo$LoadingAssets.prototype.check = function () {
    var mesh = this.teapotMesh;
    var hdri = this.hdriMap;
    if (mesh != null && hdri != null) {
      this.block(mesh, hdri);
    }};
  AoDemo$LoadingAssets.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LoadingAssets',
    interfaces: []
  };
  function AoDemo$Companion() {
    AoDemo$Companion_instance = this;
  }
  AoDemo$Companion.prototype.aoMapColorModel = function () {
    var $receiver = new ShaderModel('aoMap');
    var ifTexCoords = {v: null};
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    ifTexCoords.v = $receiver_0.stageInterfaceNode_iikjwn$('ifTexCoords', $receiver_0.attrTexCoords().output);
    $receiver_0.positionOutput = $receiver_0.simpleVertexPositionNode().outVec4;
    var $receiver_1 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    var sampler = $receiver_1.textureSamplerNode_ce41yx$($receiver_1.textureNode_61zpoe$('colorTex'), ifTexCoords.v.output);
    var gray = $receiver_1.addNode_u9w9by$(new AoDemo$Companion$Red2GrayNode(sampler.outColor, $receiver_1.stage)).outGray;
    $receiver_1.colorOutput_a3v4si$(gray);
    return $receiver;
  };
  function AoDemo$Companion$Red2GrayNode(inRed, graph) {
    ShaderNode.call(this, 'red2gray', graph);
    this.inRed = inRed;
    this.outGray = new ShaderNodeIoVar(new ModelVar4f('outGray'), this);
  }
  AoDemo$Companion$Red2GrayNode.prototype.setup_llmhyc$ = function (shaderGraph) {
    ShaderNode.prototype.setup_llmhyc$.call(this, shaderGraph);
    this.dependsOn_7qvs0d$(this.inRed);
  };
  AoDemo$Companion$Red2GrayNode.prototype.generateCode_626509$ = function (generator) {
    generator.appendMain_61zpoe$(this.outGray.declare() + ' = vec4(' + this.inRed.ref1f() + ', ' + this.inRed.ref1f() + ', ' + this.inRed.ref1f() + ', 1.0);');
  };
  AoDemo$Companion$Red2GrayNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Red2GrayNode',
    interfaces: [ShaderNode]
  };
  AoDemo$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var AoDemo$Companion_instance = null;
  function AoDemo$Companion_getInstance() {
    if (AoDemo$Companion_instance === null) {
      new AoDemo$Companion();
    }return AoDemo$Companion_instance;
  }
  function Coroutine$AoDemo$noAoMap$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
  }
  Coroutine$AoDemo$noAoMap$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$AoDemo$noAoMap$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$AoDemo$noAoMap$lambda.prototype.constructor = Coroutine$AoDemo$noAoMap$lambda;
  Coroutine$AoDemo$noAoMap$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return BufferedTextureData.Companion.singleColor_d7aj7k$(Color.Companion.WHITE);
          case 1:
            throw this.exception_0;
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
  function AoDemo$noAoMap$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$AoDemo$noAoMap$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  AoDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AoDemo',
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
  function deferredScene(ctx) {
    var deferredDemo = new DeferredDemo(ctx);
    return listOf([deferredDemo.mainScene, deferredDemo.menu]);
  }
  function DeferredDemo(ctx) {
    DeferredDemo$Companion_getInstance();
    this.mainScene = null;
    this.menu = null;
    this.aoPipeline_gxwqo$_0 = this.aoPipeline_gxwqo$_0;
    this.mrtPass_8d2jok$_0 = this.mrtPass_8d2jok$_0;
    this.pbrPass_13qagj$_0 = this.pbrPass_13qagj$_0;
    this.objects_v6o6kw$_0 = this.objects_v6o6kw$_0;
    this.objectShader_kt711w$_0 = this.objectShader_kt711w$_0;
    this.noAoMap_0 = new Texture(void 0, void 0, DeferredDemo$noAoMap$lambda);
    this.lightPositionMesh_cedig8$_0 = this.lightPositionMesh_cedig8$_0;
    this.lightVolumeMesh_k6bbw9$_0 = this.lightVolumeMesh_k6bbw9$_0;
    this.autoRotate_0 = true;
    this.rand_0 = new Random(1337);
    this.lightCount_0 = 2000;
    this.lights_0 = ArrayList_init();
    var $receiver = new Cycler(listOf([new DeferredDemo$ColorMap('Colorful', listOf([Color.Companion.MD_RED, Color.Companion.MD_PINK, Color.Companion.MD_PURPLE, Color.Companion.MD_DEEP_PURPLE, Color.Companion.MD_INDIGO, Color.Companion.MD_BLUE, Color.Companion.MD_LIGHT_BLUE, Color.Companion.MD_CYAN, Color.Companion.MD_TEAL, Color.Companion.MD_GREEN, Color.Companion.MD_LIGHT_GREEN, Color.Companion.MD_LIME, Color.Companion.MD_YELLOW, Color.Companion.MD_AMBER, Color.Companion.MD_ORANGE, Color.Companion.MD_DEEP_ORANGE])), new DeferredDemo$ColorMap('Hot-Cold', listOf([Color.Companion.MD_PINK, Color.Companion.MD_CYAN])), new DeferredDemo$ColorMap('Summer', listOf([Color.Companion.MD_ORANGE, Color.Companion.MD_BLUE, Color.Companion.MD_GREEN])), new DeferredDemo$ColorMap('White', listOf_0(Color.Companion.WHITE))]));
    $receiver.index = 1;
    this.colorMap_0 = $receiver;
    this.mainScene = this.makeDeferredScene_0();
    this.menu = this.makeMenu_0(ctx);
    this.updateLights_0();
  }
  Object.defineProperty(DeferredDemo.prototype, 'aoPipeline_0', {
    get: function () {
      if (this.aoPipeline_gxwqo$_0 == null)
        return throwUPAE('aoPipeline');
      return this.aoPipeline_gxwqo$_0;
    },
    set: function (aoPipeline) {
      this.aoPipeline_gxwqo$_0 = aoPipeline;
    }
  });
  Object.defineProperty(DeferredDemo.prototype, 'mrtPass_0', {
    get: function () {
      if (this.mrtPass_8d2jok$_0 == null)
        return throwUPAE('mrtPass');
      return this.mrtPass_8d2jok$_0;
    },
    set: function (mrtPass) {
      this.mrtPass_8d2jok$_0 = mrtPass;
    }
  });
  Object.defineProperty(DeferredDemo.prototype, 'pbrPass_0', {
    get: function () {
      if (this.pbrPass_13qagj$_0 == null)
        return throwUPAE('pbrPass');
      return this.pbrPass_13qagj$_0;
    },
    set: function (pbrPass) {
      this.pbrPass_13qagj$_0 = pbrPass;
    }
  });
  Object.defineProperty(DeferredDemo.prototype, 'objects_0', {
    get: function () {
      if (this.objects_v6o6kw$_0 == null)
        return throwUPAE('objects');
      return this.objects_v6o6kw$_0;
    },
    set: function (objects) {
      this.objects_v6o6kw$_0 = objects;
    }
  });
  Object.defineProperty(DeferredDemo.prototype, 'objectShader_0', {
    get: function () {
      if (this.objectShader_kt711w$_0 == null)
        return throwUPAE('objectShader');
      return this.objectShader_kt711w$_0;
    },
    set: function (objectShader) {
      this.objectShader_kt711w$_0 = objectShader;
    }
  });
  Object.defineProperty(DeferredDemo.prototype, 'lightPositionMesh_0', {
    get: function () {
      if (this.lightPositionMesh_cedig8$_0 == null)
        return throwUPAE('lightPositionMesh');
      return this.lightPositionMesh_cedig8$_0;
    },
    set: function (lightPositionMesh) {
      this.lightPositionMesh_cedig8$_0 = lightPositionMesh;
    }
  });
  Object.defineProperty(DeferredDemo.prototype, 'lightVolumeMesh_0', {
    get: function () {
      if (this.lightVolumeMesh_k6bbw9$_0 == null)
        return throwUPAE('lightVolumeMesh');
      return this.lightVolumeMesh_k6bbw9$_0;
    },
    set: function (lightVolumeMesh) {
      this.lightVolumeMesh_k6bbw9$_0 = lightVolumeMesh;
    }
  });
  function DeferredDemo$makeDeferredScene$lambda$lambda$lambda($receiver) {
    $receiver.rectProps.defaults().mirrorTexCoordsY();
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function DeferredDemo$makeDeferredScene$lambda$lambda(this$DeferredDemo) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(DeferredDemo$makeDeferredScene$lambda$lambda$lambda);
      $receiver.pipelineLoader = new DeferredOutputShader(this$DeferredDemo.pbrPass_0.colorTexture);
      return Unit;
    };
  }
  function DeferredDemo$makeDeferredScene$lambda$lambda_0(this$DeferredDemo) {
    return function ($receiver, f, ctx) {
      var tmp$;
      tmp$ = this$DeferredDemo.lights_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.animate_mx4ult$(ctx.deltaT);
      }
      return Unit;
    };
  }
  function DeferredDemo$makeDeferredScene$lambda$lambda_1(this$DeferredDemo) {
    return function ($receiver, it) {
      this$DeferredDemo.noAoMap_0.dispose();
      return Unit;
    };
  }
  DeferredDemo.prototype.makeDeferredScene_0 = function () {
    var $receiver = new Scene_init(null);
    this.mrtPass_0 = new DeferredMrtPass();
    $receiver.addOffscreenPass_m1c2kf$(this.mrtPass_0);
    this.makeContent_0(this.mrtPass_0, $receiver);
    this.aoPipeline_0 = AoPipeline.Companion.createDeferred_4lrvgg$($receiver, this.mrtPass_0);
    this.aoPipeline_0.intensity = 1.2;
    this.aoPipeline_0.kernelSz = 32;
    var $receiver_0 = new PbrSceneShader$DeferredPbrConfig();
    $receiver_0.isScrSpcAmbientOcclusion = true;
    $receiver_0.scrSpcAmbientOcclusionMap = this.aoPipeline_0.aoMap;
    var cfg = $receiver_0;
    this.pbrPass_0 = new PbrLightingPass($receiver, this.mrtPass_0, cfg);
    this.makeLightOverlays_0(this.pbrPass_0);
    $receiver.unaryPlus_uv0sim$(textureMesh(void 0, void 0, DeferredDemo$makeDeferredScene$lambda$lambda(this)));
    $receiver.onUpdate.add_11rb$(DeferredDemo$makeDeferredScene$lambda$lambda_0(this));
    $receiver.onDispose.add_11rb$(DeferredDemo$makeDeferredScene$lambda$lambda_1(this));
    return $receiver;
  };
  function DeferredDemo$makeLightOverlays$lambda$lambda$lambda($receiver) {
    $receiver.color = Color.Companion.RED;
    var $receiver_0 = $receiver.sphereProps.icoDefaults();
    $receiver_0.steps = 1;
    $receiver_0.radius = 0.05;
    $receiver_0.center.set_czzhiu$(Vec3f.Companion.ZERO);
    $receiver.icoSphere_mojs8w$($receiver.sphereProps);
    return Unit;
  }
  function DeferredDemo$makeLightOverlays$lambda$lambda(this$DeferredDemo) {
    return function ($receiver) {
      $receiver.isFrustumChecked = false;
      $receiver.isVisible = true;
      $receiver.generate_v2sixm$(DeferredDemo$makeLightOverlays$lambda$lambda$lambda);
      $receiver.pipelineLoader = new ModeledShader(this$DeferredDemo.instancedLightIndicatorModel_0());
      return Unit;
    };
  }
  function DeferredDemo$makeLightOverlays$lambda$lambda_0(this$DeferredDemo, closure$lightPosInsts, closure$lightVolInsts, this$makeLightOverlays, closure$lightModelMat) {
    return function ($receiver, f, f_0) {
      if (this$DeferredDemo.lightPositionMesh_0.isVisible || this$DeferredDemo.lightVolumeMesh_0.isVisible) {
        closure$lightPosInsts.clear();
        closure$lightVolInsts.clear();
        var $receiver_0 = this$makeLightOverlays.dynamicPointLights.lightInstances;
        var tmp$;
        tmp$ = $receiver_0.iterator();
        while (tmp$.hasNext()) {
          var element = tmp$.next();
          var closure$lightModelMat_0 = closure$lightModelMat;
          var this$DeferredDemo_0 = this$DeferredDemo;
          var closure$lightPosInsts_0 = closure$lightPosInsts;
          var closure$lightVolInsts_0 = closure$lightVolInsts;
          closure$lightModelMat_0.setIdentity();
          closure$lightModelMat_0.translate_czzhiu$(element.position);
          if (this$DeferredDemo_0.lightPositionMesh_0.isVisible) {
            closure$lightPosInsts_0.checkBufferSize_za3lpa$();
            var szBefore = closure$lightPosInsts_0.dataF.position;
            var $receiver_1 = closure$lightPosInsts_0.dataF;
            $receiver_1.put_q3cr5i$(closure$lightModelMat_0.matrix);
            $receiver_1.put_q3cr5i$(element.color.array);
            var growSz = closure$lightPosInsts_0.dataF.position - szBefore | 0;
            if (growSz !== closure$lightPosInsts_0.instanceSizeF) {
              throw IllegalStateException_init('Expected data to grow by ' + closure$lightPosInsts_0.instanceSizeF + ' elements, instead it grew by ' + growSz);
            }closure$lightPosInsts_0.numInstances = closure$lightPosInsts_0.numInstances + 1 | 0;
            closure$lightPosInsts_0.hasChanged = true;
          }if (this$DeferredDemo_0.lightVolumeMesh_0.isVisible) {
            var x = element.intensity;
            var s = Math_0.sqrt(x);
            closure$lightModelMat_0.scale_y2kzbl$(s, s, s);
            closure$lightVolInsts_0.checkBufferSize_za3lpa$();
            var szBefore_0 = closure$lightVolInsts_0.dataF.position;
            var $receiver_2 = closure$lightVolInsts_0.dataF;
            $receiver_2.put_q3cr5i$(closure$lightModelMat_0.matrix);
            $receiver_2.put_q3cr5i$(element.color.array);
            var growSz_0 = closure$lightVolInsts_0.dataF.position - szBefore_0 | 0;
            if (growSz_0 !== closure$lightVolInsts_0.instanceSizeF) {
              throw IllegalStateException_init('Expected data to grow by ' + closure$lightVolInsts_0.instanceSizeF + ' elements, instead it grew by ' + growSz_0);
            }closure$lightVolInsts_0.numInstances = closure$lightVolInsts_0.numInstances + 1 | 0;
            closure$lightVolInsts_0.hasChanged = true;
          }}
      }return Unit;
    };
  }
  DeferredDemo.prototype.makeLightOverlays_0 = function ($receiver) {
    var $receiver_0 = $receiver.content;
    this.lightPositionMesh_0 = colorMesh(void 0, DeferredDemo$makeLightOverlays$lambda$lambda(this));
    $receiver_0.unaryPlus_uv0sim$(this.lightPositionMesh_0);
    var $receiver_1 = wireframeMesh($receiver.dynamicPointLights.mesh.geometry);
    $receiver_1.isFrustumChecked = false;
    $receiver_1.isVisible = false;
    $receiver_1.pipelineLoader = new ModeledShader(this.instancedLightIndicatorModel_0());
    this.lightVolumeMesh_0 = $receiver_1;
    $receiver_0.unaryPlus_uv0sim$(this.lightVolumeMesh_0);
    var lightPosInsts = new MeshInstanceList(listOf([MeshInstanceList.Companion.MODEL_MAT, Attribute.Companion.COLORS]), 5000);
    var lightVolInsts = new MeshInstanceList(listOf([MeshInstanceList.Companion.MODEL_MAT, Attribute.Companion.COLORS]), 5000);
    this.lightPositionMesh_0.instances = lightPosInsts;
    this.lightVolumeMesh_0.instances = lightVolInsts;
    var lightModelMat = new Mat4f();
    $receiver_0.onUpdate.add_11rb$(DeferredDemo$makeLightOverlays$lambda$lambda_0(this, lightPosInsts, lightVolInsts, $receiver, lightModelMat));
  };
  function DeferredDemo$makeContent$lambda$lambda$lambda(this$DeferredDemo, this$) {
    return function ($receiver, f, ctx) {
      if (this$DeferredDemo.autoRotate_0) {
        this$.verticalRotation += ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function DeferredDemo$makeContent$lambda$lambda(this$makeContent, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.setMouseRotation_dleff0$(0.0, -40.0);
      $receiver.unaryPlus_uv0sim$(this$makeContent.camera);
      $receiver.zoom = 28.0;
      $receiver.maxZoom = 50.0;
      $receiver.translation.set_yvo9jy$(0.0, -11.0, 0.0);
      var $receiver_0 = $receiver.onUpdate;
      var element = DeferredDemo$makeContent$lambda$lambda$lambda(this$DeferredDemo, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeContent$lambda$lambda$lambda_0(this$DeferredDemo) {
    return function ($receiver) {
      var sphereProtos = ArrayList_init();
      for (var i = 0; i <= 10; i++) {
        var builder = new MeshBuilder(IndexedVertexList_init([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS, Attribute.Companion.COLORS]));
        var element = builder.geometry;
        sphereProtos.add_11rb$(element);
        var this$DeferredDemo_0 = this$DeferredDemo;
        var $receiver_0 = builder.sphereProps.icoDefaults();
        $receiver_0.steps = 3;
        $receiver_0.radius = this$DeferredDemo_0.rand_0.randomF_dleff0$(0.3, 0.4);
        $receiver_0.center.set_y2kzbl$(0.0, 0.1 + $receiver_0.radius, 0.0);
        builder.icoSphere_mojs8w$(builder.sphereProps);
      }
      for (var x = -19; x <= 19; x++) {
        for (var y = -19; y <= 19; y++) {
          $receiver.color = Color.Companion.WHITE;
          $receiver.transform.push();
          var this$DeferredDemo_1 = this$DeferredDemo;
          $receiver.translate_y2kzbl$(x, 0.0, y);
          if ((x + 100 | 0) % 2 === (y + 100 | 0) % 2) {
            var $receiver_1 = $receiver.cubeProps.defaults();
            $receiver_1.size.set_y2kzbl$(this$DeferredDemo_1.rand_0.randomF_dleff0$(0.6, 0.8), this$DeferredDemo_1.rand_0.randomF_dleff0$(0.6, 0.95), this$DeferredDemo_1.rand_0.randomF_dleff0$(0.6, 0.8));
            $receiver_1.origin.set_y2kzbl$(-$receiver_1.size.x / 2, 0.1, -$receiver_1.size.z / 2);
            $receiver.cube_lhbb6w$($receiver.cubeProps);
          } else {
            $receiver.geometry_ejqx55$(sphereProtos.get_za3lpa$(this$DeferredDemo_1.rand_0.randomI_n8acyv$(get_indices(sphereProtos))));
          }
          $receiver.transform.pop();
        }
      }
      return Unit;
    };
  }
  function DeferredDemo$makeContent$lambda$lambda_0(this$DeferredDemo) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(DeferredDemo$makeContent$lambda$lambda$lambda_0(this$DeferredDemo));
      var $receiver_0 = new DeferredMrtShader$MrtPbrConfig();
      $receiver_0.roughness = 0.15;
      var mrtCfg = $receiver_0;
      this$DeferredDemo.objectShader_0 = new DeferredMrtShader(mrtCfg);
      $receiver.pipelineLoader = this$DeferredDemo.objectShader_0;
      return Unit;
    };
  }
  function DeferredDemo$makeContent$lambda$lambda$lambda_1($receiver) {
    $receiver.rotate_ad55pp$(90.0, Vec3f.Companion.NEG_X_AXIS);
    $receiver.color = Color.Companion.WHITE;
    var $receiver_0 = $receiver.rectProps.defaults();
    $receiver_0.size.set_dleff0$(40.0, 40.0);
    $receiver_0.origin.set_y2kzbl$($receiver_0.size.x, $receiver_0.size.y, 0.0).scale_mx4ult$(-0.5);
    $receiver_0.generateTexCoords_mx4ult$(30.0);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda.prototype.constructor = Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda;
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/futuristic-panels1/futuristic-panels1-albedo1.jpg', this);
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
  function DeferredDemo$makeContent$lambda$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0.prototype.constructor = Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0;
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/futuristic-panels1/futuristic-panels1-normal.jpg', this);
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
  function DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1.prototype.constructor = Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1;
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/futuristic-panels1/futuristic-panels1-roughness.jpg', this);
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
  function DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2.prototype.constructor = Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2;
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/futuristic-panels1/futuristic-panels1-metallic.jpg', this);
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
  function DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3.prototype.constructor = Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3;
  Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/futuristic-panels1/futuristic-panels1-ao.jpg', this);
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
  function DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function DeferredDemo$makeContent$lambda$lambda$lambda_2(closure$groundShader) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
      (tmp$ = closure$groundShader.albedoMap) != null ? (tmp$.dispose(), Unit) : null;
      (tmp$_0 = closure$groundShader.ambientOcclusionMap) != null ? (tmp$_0.dispose(), Unit) : null;
      (tmp$_1 = closure$groundShader.normalMap) != null ? (tmp$_1.dispose(), Unit) : null;
      (tmp$_2 = closure$groundShader.metallicMap) != null ? (tmp$_2.dispose(), Unit) : null;
      (tmp$_3 = closure$groundShader.roughnessMap) != null ? (tmp$_3.dispose(), Unit) : null;
      (tmp$_4 = closure$groundShader.displacementMap) != null ? (tmp$_4.dispose(), Unit) : null;
      return Unit;
    };
  }
  function DeferredDemo$makeContent$lambda$lambda_1($receiver) {
    $receiver.generate_v2sixm$(DeferredDemo$makeContent$lambda$lambda$lambda_1);
    var $receiver_0 = new DeferredMrtShader$MrtPbrConfig();
    $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
    $receiver_0.isNormalMapped = true;
    $receiver_0.isRoughnessMapped = true;
    $receiver_0.isMetallicMapped = true;
    $receiver_0.isAmbientOcclusionMapped = true;
    $receiver_0.albedoMap = new Texture(void 0, void 0, DeferredDemo$makeContent$lambda$lambda$lambda$lambda);
    $receiver_0.normalMap = new Texture(void 0, void 0, DeferredDemo$makeContent$lambda$lambda$lambda$lambda_0);
    $receiver_0.roughnessMap = new Texture(void 0, void 0, DeferredDemo$makeContent$lambda$lambda$lambda$lambda_1);
    $receiver_0.metallicMap = new Texture(void 0, void 0, DeferredDemo$makeContent$lambda$lambda$lambda$lambda_2);
    $receiver_0.ambientOcclusionMap = new Texture(void 0, void 0, DeferredDemo$makeContent$lambda$lambda$lambda$lambda_3);
    var mrtCfg = $receiver_0;
    var groundShader = new DeferredMrtShader(mrtCfg);
    $receiver.pipelineLoader = groundShader;
    $receiver.onDispose.add_11rb$(DeferredDemo$makeContent$lambda$lambda$lambda_2(groundShader));
    return Unit;
  }
  DeferredDemo.prototype.makeContent_0 = function ($receiver, scene) {
    var $receiver_0 = $receiver.content;
    $receiver_0.unaryPlus_uv0sim$(orbitInputTransform(scene, void 0, DeferredDemo$makeContent$lambda$lambda($receiver, this)));
    this.objects_0 = colorMesh(void 0, DeferredDemo$makeContent$lambda$lambda_0(this));
    $receiver_0.unaryPlus_uv0sim$(this.objects_0);
    $receiver_0.unaryPlus_uv0sim$(textureMesh(void 0, true, DeferredDemo$makeContent$lambda$lambda_1));
  };
  DeferredDemo.prototype.updateLights_0 = function (forced) {
    if (forced === void 0)
      forced = false;
    var rows = 41;
    var travel = rows;
    var start = travel / 2;
    var objOffset = this.objects_0.isVisible ? 0.7 : 0.0;
    var lightGroups = listOf([new DeferredDemo$LightGroup(this, new Vec3f(1 - start, 0.45, -start), new Vec3f(1.0, 0.0, 0.0), new Vec3f(0.0, 0.0, 1.0), rows - 1 | 0), new DeferredDemo$LightGroup(this, new Vec3f(-start, 0.45, 1 - start), new Vec3f(0.0, 0.0, 1.0), new Vec3f(1.0, 0.0, 0.0), rows - 1 | 0), new DeferredDemo$LightGroup(this, new Vec3f(1.5 - start, 0.45 + objOffset, start), new Vec3f(1.0, 0.0, 0.0), new Vec3f(0.0, 0.0, -1.0), rows - 2 | 0), new DeferredDemo$LightGroup(this, new Vec3f(start, 0.45 + objOffset, 1.5 - start), new Vec3f(0.0, 0.0, 1.0), new Vec3f(-1.0, 0.0, 0.0), rows - 2 | 0)]);
    if (forced) {
      this.lights_0.clear();
      this.pbrPass_0.dynamicPointLights.lightInstances.clear();
    } else {
      while (this.lights_0.size > this.lightCount_0) {
        this.lights_0.removeAt_za3lpa$(get_lastIndex(this.lights_0));
        this.pbrPass_0.dynamicPointLights.lightInstances.removeAt_za3lpa$(get_lastIndex(this.pbrPass_0.dynamicPointLights.lightInstances));
      }
    }
    while (this.lights_0.size < this.lightCount_0) {
      var grp = lightGroups.get_za3lpa$(this.rand_0.randomI_n8acyv$(get_indices(lightGroups)));
      var x = this.rand_0.randomI_n8acyv$(until(0, grp.rows));
      var $this = this.pbrPass_0.dynamicPointLights;
      var light = new DeferredPointLights$DeferredPointLights$PointLight_init();
      light.intensity = 1.0;
      light.color.set_d7aj7k$(this.colorMap_0.current.colors.get_za3lpa$(this.rand_0.randomI_n8acyv$(get_indices(this.colorMap_0.current.colors))).toLinear());
      $this.addPointLight_at6cwi$(light);
      var light_0 = light;
      var animLight = new DeferredDemo$AnimatedLight(light_0);
      this.lights_0.add_11rb$(animLight);
      grp.setupLight_u83zut$(animLight, x, travel, this.rand_0.randomF());
    }
  };
  DeferredDemo.prototype.updateLightColors_0 = function () {
    var tmp$;
    tmp$ = this.lights_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.light.color.set_d7aj7k$(this.colorMap_0.current.colors.get_za3lpa$(this.rand_0.randomI_n8acyv$(get_indices(this.colorMap_0.current.colors))).toLinear());
    }
  };
  DeferredDemo.prototype.setAoState_0 = function (enabled) {
    this.aoPipeline_0.setEnabled_6taknv$(enabled);
    if (enabled) {
      this.pbrPass_0.sceneShader.scrSpcAmbientOcclusionMap = this.aoPipeline_0.aoMap;
    } else {
      this.pbrPass_0.sceneShader.scrSpcAmbientOcclusionMap = this.noAoMap_0;
    }
  };
  function DeferredDemo$makeMenu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function DeferredDemo$makeMenu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(DeferredDemo$makeMenu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(DeferredDemo$makeMenu$lambda$lambda$lambda_0);
    return Unit;
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda$lambda(closure$p) {
    return function ($receiver) {
      var closure$p_0 = closure$p;
      var $receiver_0 = $receiver.rectProps.defaults();
      $receiver_0.origin.set_y2kzbl$(closure$p_0.x, closure$p_0.y, 0.0);
      $receiver_0.size.set_dleff0$(1.0, 1.0);
      $receiver_0.mirrorTexCoordsY();
      $receiver.rect_e5k3t5$($receiver.rectProps);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda(closure$p, closure$i, this$DeferredDemo) {
    return function ($receiver) {
      var tmp$;
      $receiver.generate_v2sixm$(DeferredDemo$makeMenu$lambda$lambda$lambda$lambda$lambda(closure$p));
      switch (closure$i) {
        case 0:
          tmp$ = new ModeledShader$TextureColor(this$DeferredDemo.mrtPass_0.albedoMetal, 'colorTex', this$DeferredDemo.rgbMapColorModel_0(0.0, 1.0));
          break;
        case 1:
          tmp$ = new ModeledShader$TextureColor(this$DeferredDemo.mrtPass_0.normalRoughness, 'colorTex', this$DeferredDemo.rgbMapColorModel_0(1.0, 0.5));
          break;
        case 2:
          tmp$ = new ModeledShader$TextureColor(this$DeferredDemo.mrtPass_0.positionAo, 'colorTex', this$DeferredDemo.rgbMapColorModel_0(10.0, 0.05));
          break;
        case 3:
          tmp$ = new ModeledShader$TextureColor(this$DeferredDemo.aoPipeline_0.aoMap, 'colorTex', AoDemo$Companion_getInstance().aoMapColorModel());
          break;
        case 4:
          tmp$ = new DeferredDemo$MetalRoughAoTex(this$DeferredDemo.mrtPass_0);
          break;
        default:tmp$ = new ModeledShader$StaticColor(Color.Companion.MAGENTA);
          break;
      }
      $receiver.pipelineLoader = tmp$;
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_1(this$) {
    return function ($receiver, rp, ctx) {
      var mapSz = 0.26;
      var scaleX = rp.viewport.width * mapSz;
      var scaleY = scaleX * (rp.viewport.height / rp.viewport.width);
      var margin = rp.viewport.height * 0.05;
      this$.setIdentity();
      this$.translate_y2kzbl$(margin, margin, 0.0);
      this$.scale_y2kzbl$(scaleX, scaleY, 1.0);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda_0(this$DeferredDemo) {
    return function ($receiver) {
      $receiver.isVisible = false;
      var positions = listOf([new Vec2f(0.0, 0.0), new Vec2f(0.0, 1.2), new Vec2f(0.0, 2.4), new Vec2f(1.1, 1.2), new Vec2f(1.1, 2.4)]);
      var tmp$, tmp$_0;
      var index = 0;
      tmp$ = positions.iterator();
      while (tmp$.hasNext()) {
        var item = tmp$.next();
        var this$DeferredDemo_0 = this$DeferredDemo;
        $receiver.unaryPlus_uv0sim$(textureMesh(void 0, void 0, DeferredDemo$makeMenu$lambda$lambda$lambda$lambda(item, checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0)), this$DeferredDemo_0)));
      }
      $receiver.onUpdate.add_11rb$(DeferredDemo$makeMenu$lambda$lambda$lambda_1($receiver));
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_2(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_3(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_4(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_0(this$DeferredDemo, closure$lightCntVal) {
    return function ($receiver, it) {
      this$DeferredDemo.lightCount_0 = numberToInt($receiver.value);
      closure$lightCntVal.text = this$DeferredDemo.lightCount_0.toString();
      this$DeferredDemo.updateLights_0();
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_5(closure$y, this$DeferredDemo, closure$lightCntVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_0(this$DeferredDemo, closure$lightCntVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_1(this$DeferredDemo) {
    return function ($receiver) {
      this$DeferredDemo.lightPositionMesh_0.isVisible = $receiver.isEnabled;
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_6(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$DeferredDemo.lightPositionMesh_0.isVisible;
      var $receiver_0 = $receiver.onStateChange;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_1(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_2(this$DeferredDemo) {
    return function ($receiver) {
      this$DeferredDemo.lightVolumeMesh_0.isVisible = $receiver.isEnabled;
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_7(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$DeferredDemo.lightVolumeMesh_0.isVisible;
      var $receiver_0 = $receiver.onStateChange;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_2(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_8(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_3(this$DeferredDemo) {
    return function ($receiver, f, f_0, f_1) {
      $receiver.text = this$DeferredDemo.colorMap_0.next().name;
      this$DeferredDemo.updateLightColors_0();
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_9(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      var $receiver_0 = $receiver.onClick;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_3(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_4(this$DeferredDemo) {
    return function ($receiver, f, f_0, f_1) {
      this$DeferredDemo.colorMap_0.prev();
      this$DeferredDemo.updateLightColors_0();
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_10(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_4(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_5(this$DeferredDemo) {
    return function ($receiver, f, f_0, f_1) {
      this$DeferredDemo.colorMap_0.next();
      this$DeferredDemo.updateLightColors_0();
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_11(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_5(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_12(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_6(closure$mapGroup) {
    return function ($receiver) {
      closure$mapGroup.isVisible = $receiver.isEnabled;
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_13(closure$y, closure$mapGroup) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = closure$mapGroup.isVisible;
      var $receiver_0 = $receiver.onStateChange;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_6(closure$mapGroup);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_7(this$DeferredDemo) {
    return function ($receiver) {
      this$DeferredDemo.setAoState_0($receiver.isEnabled);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_14(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$DeferredDemo.aoPipeline_0.aoPass.isEnabled;
      var $receiver_0 = $receiver.onStateChange;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_7(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_15(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_8(this$DeferredDemo) {
    return function ($receiver) {
      this$DeferredDemo.autoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_16(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$DeferredDemo.autoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_8(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_9(this$DeferredDemo) {
    return function ($receiver) {
      this$DeferredDemo.objects_0.isVisible = $receiver.isEnabled;
      this$DeferredDemo.updateLights_0(true);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_17(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$DeferredDemo.objects_0.isVisible;
      var $receiver_0 = $receiver.onStateChange;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_9(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_18(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_10(this$DeferredDemo) {
    return function ($receiver, it) {
      this$DeferredDemo.objectShader_0.roughness = $receiver.value;
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda$lambda_19(closure$y, this$DeferredDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.value = this$DeferredDemo.objectShader_0.roughness;
      var $receiver_0 = $receiver.onValueChanged;
      var element = DeferredDemo$makeMenu$lambda$lambda$lambda$lambda_10(this$DeferredDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda$lambda_1(closure$smallFont, this$, this$DeferredDemo, closure$mapGroup) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-370.0), dps(-675.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(250.0), dps(555.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Dynamic Lights', DeferredDemo$makeMenu$lambda$lambda$lambda_2(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Light Count:', DeferredDemo$makeMenu$lambda$lambda$lambda_3(y)));
      var lightCntVal = this$.label_tokfmu$(this$DeferredDemo.lightCount_0.toString(), DeferredDemo$makeMenu$lambda$lambda$lambda_4(y));
      $receiver.unaryPlus_uv0sim$(lightCntVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('lightCntSlider', 100.0, 5000, this$DeferredDemo.lightCount_0, DeferredDemo$makeMenu$lambda$lambda$lambda_5(y, this$DeferredDemo, lightCntVal)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Light Positions', DeferredDemo$makeMenu$lambda$lambda$lambda_6(y, this$DeferredDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Light Volumes', DeferredDemo$makeMenu$lambda$lambda$lambda_7(y, this$DeferredDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Color Map:', DeferredDemo$makeMenu$lambda$lambda$lambda_8(y)));
      y.v -= 35.0;
      var colorMapLabel = this$.button_9zrh0o$(this$DeferredDemo.colorMap_0.current.name, DeferredDemo$makeMenu$lambda$lambda$lambda_9(y, this$DeferredDemo));
      $receiver.unaryPlus_uv0sim$(colorMapLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('colors-left', DeferredDemo$makeMenu$lambda$lambda$lambda_10(y, this$DeferredDemo)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('colors-right', DeferredDemo$makeMenu$lambda$lambda$lambda_11(y, this$DeferredDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Deferred Shading', DeferredDemo$makeMenu$lambda$lambda$lambda_12(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Show Maps', DeferredDemo$makeMenu$lambda$lambda$lambda_13(y, closure$mapGroup)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Ambient Occlusion', DeferredDemo$makeMenu$lambda$lambda$lambda_14(y, this$DeferredDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Scene', DeferredDemo$makeMenu$lambda$lambda$lambda_15(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', DeferredDemo$makeMenu$lambda$lambda$lambda_16(y, this$DeferredDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Show Objects', DeferredDemo$makeMenu$lambda$lambda$lambda_17(y, this$DeferredDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Object Roughness:', DeferredDemo$makeMenu$lambda$lambda$lambda_18(y)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('roughnessSlider', 0.0, 1.0, this$DeferredDemo.lightCount_0, DeferredDemo$makeMenu$lambda$lambda$lambda_19(y, this$DeferredDemo)));
      return Unit;
    };
  }
  function DeferredDemo$makeMenu$lambda(closure$ctx, this$DeferredDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, DeferredDemo$makeMenu$lambda$lambda);
      var mapGroup = transformGroup(void 0, DeferredDemo$makeMenu$lambda$lambda_0(this$DeferredDemo));
      $receiver.unaryPlus_uv0sim$(mapGroup);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', DeferredDemo$makeMenu$lambda$lambda_1(smallFont, $receiver, this$DeferredDemo, mapGroup)));
      return Unit;
    };
  }
  DeferredDemo.prototype.makeMenu_0 = function (ctx) {
    return uiScene(void 0, void 0, void 0, DeferredDemo$makeMenu$lambda(ctx, this));
  };
  function DeferredDemo$LightGroup($outer, startConst, startIt, travelDir, rows) {
    this.$outer = $outer;
    this.startConst = startConst;
    this.startIt = startIt;
    this.travelDir = travelDir;
    this.rows = rows;
  }
  DeferredDemo$LightGroup.prototype.setupLight_u83zut$ = function (light, x, travelDist, travelPos) {
    light.startPos.set_czzhiu$(this.startIt).scale_mx4ult$(x).add_czzhiu$(this.startConst);
    light.dir.set_czzhiu$(this.travelDir);
    light.travelDist = travelDist;
    light.travelPos = travelPos * travelDist;
    light.speed = this.$outer.rand_0.randomF_dleff0$(1.0, 3.0) * 0.25;
  };
  DeferredDemo$LightGroup.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LightGroup',
    interfaces: []
  };
  function DeferredDemo$AnimatedLight(light) {
    this.light = light;
    this.startPos = MutableVec3f_init();
    this.dir = MutableVec3f_init();
    this.speed = 1.5;
    this.travelPos = 0.0;
    this.travelDist = 10.0;
  }
  DeferredDemo$AnimatedLight.prototype.animate_mx4ult$ = function (deltaT) {
    this.travelPos += deltaT * this.speed;
    if (this.travelPos > this.travelDist) {
      this.travelPos -= this.travelDist;
    }this.light.position.set_czzhiu$(this.dir).scale_mx4ult$(this.travelPos).add_czzhiu$(this.startPos);
  };
  DeferredDemo$AnimatedLight.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AnimatedLight',
    interfaces: []
  };
  function DeferredDemo$ColorMap(name, colors) {
    this.name = name;
    this.colors = colors;
  }
  DeferredDemo$ColorMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorMap',
    interfaces: []
  };
  DeferredDemo.prototype.instancedLightIndicatorModel_0 = function () {
    var $receiver = new ShaderModel('instancedLightIndicators');
    var ifColors = {v: null};
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    ifColors.v = $receiver_0.stageInterfaceNode_iikjwn$('ifColors', $receiver_0.instanceAttributeNode_nm2vx5$(Attribute.Companion.COLORS).output);
    var modelMvp = $receiver_0.premultipliedMvpNode().outMvpMat;
    var instMvp = $receiver_0.multiplyNode_ze33is$(modelMvp, $receiver_0.instanceAttrModelMat().output).output;
    $receiver_0.positionOutput = $receiver_0.vec4TransformNode_9krp9t$($receiver_0.attrPositions().output, instMvp).outVec4;
    var $receiver_1 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    $receiver_1.colorOutput_a3v4si$($receiver_1.unlitMaterialNode_r20yfm$(ifColors.v.output).outColor);
    return $receiver;
  };
  DeferredDemo.prototype.rgbMapColorModel_0 = function (offset, scale) {
    var $receiver = new ShaderModel('rgbMap');
    var ifTexCoords = {v: null};
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    ifTexCoords.v = $receiver_0.stageInterfaceNode_iikjwn$('ifTexCoords', $receiver_0.attrTexCoords().output);
    $receiver_0.positionOutput = $receiver_0.simpleVertexPositionNode().outVec4;
    var $receiver_1 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    var sampler = $receiver_1.textureSamplerNode_ce41yx$($receiver_1.textureNode_61zpoe$('colorTex'), ifTexCoords.v.output);
    var rgb = $receiver_1.splitNode_500t7j$(sampler.outColor, 'rgb').output;
    var scaled = $receiver_1.multiplyNode_tuikh5$($receiver_1.addNode_ze33is$(rgb, new ShaderNodeIoVar(new ModelVar1fConst(offset))).output, scale);
    $receiver_1.colorOutput_a3v4si$(scaled.output, void 0, new ShaderNodeIoVar(new ModelVar1fConst(1.0)));
    return $receiver;
  };
  function DeferredDemo$MetalRoughAoTex(mrtPass) {
    DeferredDemo$MetalRoughAoTex$Companion_getInstance();
    ModeledShader.call(this, DeferredDemo$MetalRoughAoTex$Companion_getInstance().shaderModel());
    this.mrtPass = mrtPass;
  }
  DeferredDemo$MetalRoughAoTex.prototype.onPipelineCreated_lfrgcb$ = function (pipeline) {
    var $this = this.model;
    var name = 'positionAo';
    var stage;
    var findNode_3klnlw$result;
    findNode_3klnlw$break: do {
      stage = ShaderStage.ALL;
      var tmp$;
      tmp$ = $this.stages.values.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if ((element.stage.mask & stage.mask) !== 0) {
          var tmp$_0;
          var $receiver = element.nodes;
          var firstOrNull$result;
          firstOrNull$break: do {
            var tmp$_1;
            tmp$_1 = $receiver.iterator();
            while (tmp$_1.hasNext()) {
              var element_0 = tmp$_1.next();
              if (equals(element_0.name, name) && Kotlin.isType(element_0, TextureNode)) {
                firstOrNull$result = element_0;
                break firstOrNull$break;
              }}
            firstOrNull$result = null;
          }
           while (false);
          var node = (tmp$_0 = firstOrNull$result) == null || Kotlin.isType(tmp$_0, TextureNode) ? tmp$_0 : throwCCE();
          if (node != null) {
            findNode_3klnlw$result = node;
            break findNode_3klnlw$break;
          }}}
      findNode_3klnlw$result = null;
    }
     while (false);
    ensureNotNull(findNode_3klnlw$result).sampler.texture = this.mrtPass.positionAo;
    var $this_0 = this.model;
    var name_0 = 'normalRough';
    var stage_0;
    var findNode_3klnlw$result_0;
    findNode_3klnlw$break: do {
      stage_0 = ShaderStage.ALL;
      var tmp$_2;
      tmp$_2 = $this_0.stages.values.iterator();
      while (tmp$_2.hasNext()) {
        var element_1 = tmp$_2.next();
        if ((element_1.stage.mask & stage_0.mask) !== 0) {
          var tmp$_0_0;
          var $receiver_0 = element_1.nodes;
          var firstOrNull$result_0;
          firstOrNull$break: do {
            var tmp$_1_0;
            tmp$_1_0 = $receiver_0.iterator();
            while (tmp$_1_0.hasNext()) {
              var element_0_0 = tmp$_1_0.next();
              if (equals(element_0_0.name, name_0) && Kotlin.isType(element_0_0, TextureNode)) {
                firstOrNull$result_0 = element_0_0;
                break firstOrNull$break;
              }}
            firstOrNull$result_0 = null;
          }
           while (false);
          var node_0 = (tmp$_0_0 = firstOrNull$result_0) == null || Kotlin.isType(tmp$_0_0, TextureNode) ? tmp$_0_0 : throwCCE();
          if (node_0 != null) {
            findNode_3klnlw$result_0 = node_0;
            break findNode_3klnlw$break;
          }}}
      findNode_3klnlw$result_0 = null;
    }
     while (false);
    ensureNotNull(findNode_3klnlw$result_0).sampler.texture = this.mrtPass.normalRoughness;
    var $this_1 = this.model;
    var name_1 = 'albedoMetal';
    var stage_1;
    var findNode_3klnlw$result_1;
    findNode_3klnlw$break: do {
      stage_1 = ShaderStage.ALL;
      var tmp$_3;
      tmp$_3 = $this_1.stages.values.iterator();
      while (tmp$_3.hasNext()) {
        var element_2 = tmp$_3.next();
        if ((element_2.stage.mask & stage_1.mask) !== 0) {
          var tmp$_0_1;
          var $receiver_1 = element_2.nodes;
          var firstOrNull$result_1;
          firstOrNull$break: do {
            var tmp$_1_1;
            tmp$_1_1 = $receiver_1.iterator();
            while (tmp$_1_1.hasNext()) {
              var element_0_1 = tmp$_1_1.next();
              if (equals(element_0_1.name, name_1) && Kotlin.isType(element_0_1, TextureNode)) {
                firstOrNull$result_1 = element_0_1;
                break firstOrNull$break;
              }}
            firstOrNull$result_1 = null;
          }
           while (false);
          var node_1 = (tmp$_0_1 = firstOrNull$result_1) == null || Kotlin.isType(tmp$_0_1, TextureNode) ? tmp$_0_1 : throwCCE();
          if (node_1 != null) {
            findNode_3klnlw$result_1 = node_1;
            break findNode_3klnlw$break;
          }}}
      findNode_3klnlw$result_1 = null;
    }
     while (false);
    ensureNotNull(findNode_3klnlw$result_1).sampler.texture = this.mrtPass.albedoMetal;
    ModeledShader.prototype.onPipelineCreated_lfrgcb$.call(this, pipeline);
  };
  function DeferredDemo$MetalRoughAoTex$Companion() {
    DeferredDemo$MetalRoughAoTex$Companion_instance = this;
  }
  DeferredDemo$MetalRoughAoTex$Companion.prototype.shaderModel = function () {
    var $receiver = new ShaderModel();
    var ifTexCoords = {v: null};
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    ifTexCoords.v = $receiver_0.stageInterfaceNode_iikjwn$('ifTexCoords', $receiver_0.attrTexCoords().output);
    $receiver_0.positionOutput = $receiver_0.simpleVertexPositionNode().outVec4;
    var $receiver_1 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    var aoSampler = $receiver_1.textureSamplerNode_ce41yx$($receiver_1.textureNode_61zpoe$('positionAo'), ifTexCoords.v.output);
    var roughSampler = $receiver_1.textureSamplerNode_ce41yx$($receiver_1.textureNode_61zpoe$('normalRough'), ifTexCoords.v.output);
    var metalSampler = $receiver_1.textureSamplerNode_ce41yx$($receiver_1.textureNode_61zpoe$('albedoMetal'), ifTexCoords.v.output);
    var ao = $receiver_1.splitNode_500t7j$(aoSampler.outColor, 'a').output;
    var rough = $receiver_1.splitNode_500t7j$(roughSampler.outColor, 'a').output;
    var metal = $receiver_1.splitNode_500t7j$(metalSampler.outColor, 'a').output;
    var $receiver_2 = $receiver_1.combineNode_m7a9qd$(GlslType.VEC_3F);
    $receiver_2.inX = ao;
    $receiver_2.inY = rough;
    $receiver_2.inZ = metal;
    var outColor = $receiver_2;
    $receiver_1.colorOutput_a3v4si$(outColor.output, void 0, new ShaderNodeIoVar(new ModelVar1fConst(1.0)));
    return $receiver;
  };
  DeferredDemo$MetalRoughAoTex$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DeferredDemo$MetalRoughAoTex$Companion_instance = null;
  function DeferredDemo$MetalRoughAoTex$Companion_getInstance() {
    if (DeferredDemo$MetalRoughAoTex$Companion_instance === null) {
      new DeferredDemo$MetalRoughAoTex$Companion();
    }return DeferredDemo$MetalRoughAoTex$Companion_instance;
  }
  DeferredDemo$MetalRoughAoTex.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MetalRoughAoTex',
    interfaces: [ModeledShader]
  };
  function DeferredDemo$Companion() {
    DeferredDemo$Companion_instance = this;
    this.MAX_LIGHTS = 5000;
  }
  DeferredDemo$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var DeferredDemo$Companion_instance = null;
  function DeferredDemo$Companion_getInstance() {
    if (DeferredDemo$Companion_instance === null) {
      new DeferredDemo$Companion();
    }return DeferredDemo$Companion_instance;
  }
  function Coroutine$DeferredDemo$noAoMap$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
  }
  Coroutine$DeferredDemo$noAoMap$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$DeferredDemo$noAoMap$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$DeferredDemo$noAoMap$lambda.prototype.constructor = Coroutine$DeferredDemo$noAoMap$lambda;
  Coroutine$DeferredDemo$noAoMap$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return BufferedTextureData.Companion.singleColor_d7aj7k$(Color.Companion.WHITE);
          case 1:
            throw this.exception_0;
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
  function DeferredDemo$noAoMap$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$DeferredDemo$noAoMap$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  DeferredDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DeferredDemo',
    interfaces: []
  };
  function demo(startScene, ctx) {
    if (startScene === void 0)
      startScene = null;
    if (ctx === void 0)
      ctx = createDefaultContext();
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
    this.dbgOverlay_0 = new DebugOverlay(ctx, DebugOverlay$Position.LOWER_RIGHT);
    this.newScenes_0 = ArrayList_init();
    this.currentScenes_0 = ArrayList_init();
    this.defaultScene_0 = new Demo$DemoEntry('glTF Models', void 0, Demo$defaultScene$lambda);
    this.demos_0 = mutableMapOf([to('deferredDemo', new Demo$DemoEntry('Deferred Shading', void 0, Demo$demos$lambda)), to('gltfDemo', new Demo$DemoEntry('glTF Models', void 0, Demo$demos$lambda_0)), to('pbrDemo', new Demo$DemoEntry('PBR Materials', void 0, Demo$demos$lambda_1)), to('aoDemo', new Demo$DemoEntry('Ambient Occlusion', void 0, Demo$demos$lambda_2)), to('multiShadowDemo', new Demo$DemoEntry('Multi Shadow', void 0, Demo$demos$lambda_3)), to('treeDemo', new Demo$DemoEntry('Procedural Tree', void 0, Demo$demos$lambda_4)), to('simplificationDemo', new Demo$DemoEntry('Simplification', void 0, Demo$demos$lambda_5)), to('instanceDemo', new Demo$DemoEntry('Instanced Drawing', void 0, Demo$demos$lambda_6)), to('helloWorldDemo', new Demo$DemoEntry('Hello World', true, Demo$demos$lambda_7))]);
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
  Object.defineProperty(Demo$Companion.prototype, 'envMapBasePath', {
    get: function () {
      var key = 'pbrDemo.envMaps';
      var default_0 = 'https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/hdri';
      var tmp$, tmp$_0;
      return (tmp$_0 = typeof (tmp$ = this.demoProps.get_11rb$(key)) === 'string' ? tmp$ : null) != null ? tmp$_0 : default_0;
    }
  });
  Object.defineProperty(Demo$Companion.prototype, 'pbrBasePath', {
    get: function () {
      var key = 'pbrDemo.materials';
      var default_0 = 'https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/materials';
      var tmp$, tmp$_0;
      return (tmp$_0 = typeof (tmp$ = this.demoProps.get_11rb$(key)) === 'string' ? tmp$ : null) != null ? tmp$_0 : default_0;
    }
  });
  Object.defineProperty(Demo$Companion.prototype, 'modelBasePath', {
    get: function () {
      var key = 'pbrDemo.models';
      var default_0 = 'https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/models';
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
    $receiver.addAll_brywnq$(gltfDemo(it));
    return Unit;
  }
  function Demo$demos$lambda($receiver, it) {
    $receiver.addAll_brywnq$(deferredScene(it));
    return Unit;
  }
  function Demo$demos$lambda_0($receiver, it) {
    $receiver.addAll_brywnq$(gltfDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_1($receiver, it) {
    $receiver.addAll_brywnq$(pbrDemoScene(it));
    return Unit;
  }
  function Demo$demos$lambda_2($receiver, it) {
    $receiver.addAll_brywnq$(aoDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_3($receiver, it) {
    $receiver.addAll_brywnq$(multiLightDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_4($receiver, it) {
    $receiver.addAll_brywnq$(treeScene(it));
    return Unit;
  }
  function Demo$demos$lambda_5($receiver, it) {
    $receiver.addAll_brywnq$(simplificationDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_6($receiver, it) {
    $receiver.addAll_brywnq$(instanceDemo(it));
    return Unit;
  }
  function Demo$demos$lambda_7($receiver, it) {
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
  function Cycler_init(elements, $this) {
    $this = $this || Object.create(Cycler.prototype);
    Cycler.call($this, listOf(elements.slice()));
    return $this;
  }
  function gltfDemo(ctx) {
    var demo = new GltfDemo(ctx);
    return listOf([demo.mainScene, demo.menu]);
  }
  function GltfDemo(ctx) {
    this.mainScene = null;
    this.menu = null;
    this.models_0 = Cycler_init([new GltfDemo$GltfModel(this, 'Flight Helmet', Demo$Companion_getInstance().modelBasePath + '/flight_helmet/FlightHelmet.gltf', 4.0, Vec3f.Companion.ZERO, false, new Vec3d(0.0, 1.25, 0.0), 3.5), new GltfDemo$GltfModel(this, 'Camera', Demo$Companion_getInstance().modelBasePath + '/camera.glb', 20.0, Vec3f.Companion.ZERO, true, new Vec3d(0.0, 0.5, 0.0), 5.0), new GltfDemo$GltfModel(this, 'Animated Box', Demo$Companion_getInstance().modelBasePath + '/BoxAnimated.gltf', 1.0, new Vec3f(0.0, 0.5, 0.0), false, new Vec3d(0.0, 1.5, 0.0), 5.0), new GltfDemo$GltfModel(this, 'Interpolation Test', Demo$Companion_getInstance().modelBasePath + '/InterpolationTest.glb', 0.5, new Vec3f(0.0, 1.25, 0.0), false, new Vec3d(0.0, 3.5, 0.0), 7.0), new GltfDemo$GltfModel(this, 'Tangent Test', Demo$Companion_getInstance().modelBasePath + '/NormalTangentMirrorTest.glb', 1.0, new Vec3f(0.0, 1.2, 0.0), false, new Vec3d(0.0, 1.25, 0.0), 3.5), new GltfDemo$GltfModel(this, 'Alpha Mode Test', Demo$Companion_getInstance().modelBasePath + '/AlphaBlendModeTest.glb', 0.5, new Vec3f(0.0, 0.06, 0.0), false, new Vec3d(0.0, 1.25, 0.0), 3.5)]);
    this.autoRotate_0 = true;
    this.animationSpeed_0 = 0.5;
    this.animationTime_0 = 0.0;
    this.orbitTransform_w8joii$_0 = this.orbitTransform_w8joii$_0;
    this.camTranslationTarget_0 = null;
    this.contentGroup_0 = new TransformGroup();
    this.irrMapPass_0 = null;
    this.reflMapPass_0 = null;
    this.brdfLutPass_0 = null;
    this.shadows_0 = ArrayList_init();
    this.aoPipeline_0 = null;
    this.models_0.current.isVisible = true;
    this.mainScene = this.makeMainScene_0(ctx);
    this.menu = this.menu_0(ctx);
  }
  Object.defineProperty(GltfDemo.prototype, 'orbitTransform_0', {
    get: function () {
      if (this.orbitTransform_w8joii$_0 == null)
        return throwUPAE('orbitTransform');
      return this.orbitTransform_w8joii$_0;
    },
    set: function (orbitTransform) {
      this.orbitTransform_w8joii$_0 = orbitTransform;
    }
  });
  function GltfDemo$makeMainScene$lambda$lambda$lambda(this$GltfDemo, closure$ctx, this$) {
    return function ($receiver, f, f_0) {
      var tmp$;
      if (this$GltfDemo.autoRotate_0) {
        this$.verticalRotation -= closure$ctx.deltaT * 3.0;
      }if ((tmp$ = this$GltfDemo.camTranslationTarget_0) != null) {
        var this$_0 = this$;
        var this$GltfDemo_0 = this$GltfDemo;
        var v = MutableVec3d_init(this$_0.translation).scale_14dthe$(0.9).add_czzhiw$(MutableVec3d_init(tmp$).scale_14dthe$(0.1));
        this$_0.translation.set_czzhiw$(v);
        if (v.distance_czzhiw$(tmp$) < 0.01) {
          this$GltfDemo_0.camTranslationTarget_0 = null;
        }}return Unit;
    };
  }
  function GltfDemo$makeMainScene$lambda$lambda(this$, this$GltfDemo, closure$ctx) {
    return function ($receiver) {
      $receiver.setMouseRotation_dleff0$(0.0, -30.0);
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.zoom = this$GltfDemo.models_0.current.zoom;
      $receiver.translation.set_czzhiw$(this$GltfDemo.models_0.current.lookAt);
      var $receiver_0 = $receiver.onUpdate;
      var element = GltfDemo$makeMainScene$lambda$lambda$lambda(this$GltfDemo, closure$ctx, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GltfDemo$makeMainScene$lambda$lambda$lambda_0(closure$hdri) {
    return function ($receiver, it) {
      closure$hdri.dispose();
      return Unit;
    };
  }
  function GltfDemo$makeMainScene$lambda$lambda_0(this$, this$GltfDemo, closure$ctx) {
    return function (hdri) {
      this$GltfDemo.irrMapPass_0 = new IrradianceMapPass(this$, hdri);
      this$GltfDemo.reflMapPass_0 = new ReflectionMapPass(this$, hdri);
      this$GltfDemo.brdfLutPass_0 = new BrdfLutPass(this$);
      this$.onDispose.add_11rb$(GltfDemo$makeMainScene$lambda$lambda$lambda_0(hdri));
      this$.unaryPlus_uv0sim$(new Skybox(ensureNotNull(this$GltfDemo.reflMapPass_0).colorTextureCube, 1.0));
      this$GltfDemo.setupContentGroup_0();
      var $receiver = this$GltfDemo.models_0;
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.load_aemszp$(closure$ctx);
      }
      this$.unaryPlus_uv0sim$(this$GltfDemo.contentGroup_0);
      return Unit;
    };
  }
  function GltfDemo$makeMainScene$lambda$lambda_1(this$GltfDemo) {
    return function ($receiver, f, ctx) {
      this$GltfDemo.animationTime_0 += ctx.deltaT * this$GltfDemo.animationSpeed_0;
      return Unit;
    };
  }
  GltfDemo.prototype.makeMainScene_0 = function (ctx) {
    var $receiver = new Scene_init(null);
    this.orbitTransform_0 = orbitInputTransform($receiver, void 0, GltfDemo$makeMainScene$lambda$lambda($receiver, this, ctx));
    $receiver.unaryPlus_uv0sim$(this.orbitTransform_0);
    $receiver.lighting.lights.clear();
    var tmp$ = $receiver.lighting.lights;
    var $receiver_0 = new Light();
    var pos = new Vec3f(7.0, 8.0, 8.0);
    var lookAt = Vec3f.Companion.ZERO;
    $receiver_0.setSpot_nve3wz$(pos, lookAt.subtract_2gj7b4$(pos, MutableVec3f_init()).norm(), 25.0);
    $receiver_0.setColor_y83vuj$(Color.Companion.WHITE.mix_y83vuj$(Color.Companion.MD_AMBER, 0.3).toLinear(), 500.0);
    tmp$.add_11rb$($receiver_0);
    var tmp$_0 = $receiver.lighting.lights;
    var $receiver_1 = new Light();
    var pos_0 = new Vec3f(-7.0, 8.0, 8.0);
    var lookAt_0 = Vec3f.Companion.ZERO;
    $receiver_1.setSpot_nve3wz$(pos_0, lookAt_0.subtract_2gj7b4$(pos_0, MutableVec3f_init()).norm(), 25.0);
    $receiver_1.setColor_y83vuj$(Color.Companion.WHITE.mix_y83vuj$(Color.Companion.MD_AMBER, 0.3).toLinear(), 500.0);
    tmp$_0.add_11rb$($receiver_1);
    addAll(this.shadows_0, listOf([new SimpleShadowMap($receiver, 0, 2048), new SimpleShadowMap($receiver, 1, 2048)]));
    this.aoPipeline_0 = AoPipeline.Companion.createForward_ushge7$($receiver);
    var hdriTexProps = new TextureProps(void 0, void 0, void 0, void 0, FilterMethod.NEAREST, FilterMethod.NEAREST, true);
    ctx.assetMgr.loadAndPrepareTexture_hd4f6p$(Demo$Companion_getInstance().envMapBasePath + '/shanghai_bund_1k.rgbe.png', hdriTexProps, GltfDemo$makeMainScene$lambda$lambda_0($receiver, this, ctx));
    $receiver.onUpdate.add_11rb$(GltfDemo$makeMainScene$lambda$lambda_1(this));
    return $receiver;
  };
  function GltfDemo$setupContentGroup$lambda$lambda(this$GltfDemo, this$) {
    return function ($receiver, f, ctx) {
      if (this$GltfDemo.autoRotate_0) {
        this$.rotate_5820x2$(ctx.deltaT * 3, Vec3d.Companion.Y_AXIS);
      }return Unit;
    };
  }
  function GltfDemo$setupContentGroup$lambda$lambda$lambda(this$GltfDemo) {
    return function ($receiver) {
      this$GltfDemo.roundCylinder_0($receiver, 4.1, 0.2);
      return Unit;
    };
  }
  function Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda.prototype.constructor = Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda;
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/Fabric030/Fabric030_1K_Color2.jpg', this);
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
  function GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0.prototype.constructor = Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0;
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/Fabric030/Fabric030_1K_Normal.jpg', this);
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
  function GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1.prototype.constructor = Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1;
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/Fabric030/Fabric030_1K_Roughness.jpg', this);
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
  function GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2.prototype.constructor = Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2;
  Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/Fabric030/Fabric030_1K_AmbientOcclusion.jpg', this);
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
  function GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_3(this$) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
      (tmp$ = this$.albedoMap) != null ? (tmp$.dispose(), Unit) : null;
      (tmp$_0 = this$.normalMap) != null ? (tmp$_0.dispose(), Unit) : null;
      (tmp$_1 = this$.roughnessMap) != null ? (tmp$_1.dispose(), Unit) : null;
      (tmp$_2 = this$.ambientOcclusionMap) != null ? (tmp$_2.dispose(), Unit) : null;
      return Unit;
    };
  }
  function GltfDemo$setupContentGroup$lambda$lambda$lambda_0(this$GltfDemo, this$) {
    return function ($receiver) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
      addAll($receiver.shadowMaps, this$GltfDemo.shadows_0);
      $receiver.isScrSpcAmbientOcclusion = true;
      $receiver.scrSpcAmbientOcclusionMap = (tmp$ = this$GltfDemo.aoPipeline_0) != null ? tmp$.aoMap : null;
      $receiver.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver.isAmbientOcclusionMapped = true;
      $receiver.isNormalMapped = true;
      $receiver.isRoughnessMapped = true;
      $receiver.albedoMap = new Texture(void 0, void 0, GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda);
      $receiver.normalMap = new Texture(void 0, void 0, GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_0);
      $receiver.roughnessMap = new Texture(void 0, void 0, GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_1);
      $receiver.ambientOcclusionMap = new Texture(void 0, void 0, GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_2);
      $receiver.setImageBasedLighting_5m8fyp$((tmp$_0 = this$GltfDemo.irrMapPass_0) != null ? tmp$_0.colorTextureCube : null, (tmp$_1 = this$GltfDemo.reflMapPass_0) != null ? tmp$_1.colorTextureCube : null, (tmp$_2 = this$GltfDemo.brdfLutPass_0) != null ? tmp$_2.colorTexture : null);
      this$.onDispose.add_11rb$(GltfDemo$setupContentGroup$lambda$lambda$lambda$lambda_3($receiver));
      return Unit;
    };
  }
  function GltfDemo$setupContentGroup$lambda$lambda_0(this$GltfDemo) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(GltfDemo$setupContentGroup$lambda$lambda$lambda(this$GltfDemo));
      $receiver.pipelineLoader = pbrShader(GltfDemo$setupContentGroup$lambda$lambda$lambda_0(this$GltfDemo, $receiver));
      return Unit;
    };
  }
  GltfDemo.prototype.setupContentGroup_0 = function () {
    var $receiver = this.contentGroup_0;
    $receiver.rotate_5820x2$(-60.0, Vec3d.Companion.Y_AXIS);
    $receiver.onUpdate.add_11rb$(GltfDemo$setupContentGroup$lambda$lambda(this, $receiver));
    $receiver.unaryPlus_uv0sim$(textureMesh(void 0, true, GltfDemo$setupContentGroup$lambda$lambda_0(this)));
  };
  function GltfDemo$menu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function GltfDemo$menu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function GltfDemo$menu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(GltfDemo$menu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(GltfDemo$menu$lambda$lambda$lambda_0);
    return Unit;
  }
  function GltfDemo$menu$lambda$lambda$lambda_1(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_2(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda$lambda(this$GltfDemo) {
    return function ($receiver, f, f_0, f_1) {
      this$GltfDemo.models_0.current.isVisible = false;
      $receiver.text = this$GltfDemo.models_0.next().name;
      this$GltfDemo.models_0.current.isVisible = true;
      this$GltfDemo.orbitTransform_0.zoom = this$GltfDemo.models_0.current.zoom;
      this$GltfDemo.camTranslationTarget_0 = this$GltfDemo.models_0.current.lookAt;
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_3(closure$y, this$GltfDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(15.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      var $receiver_0 = $receiver.onClick;
      var element = GltfDemo$menu$lambda$lambda$lambda$lambda(this$GltfDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda$lambda_0(this$GltfDemo, closure$modelName) {
    return function ($receiver, f, f_0, f_1) {
      this$GltfDemo.models_0.current.isVisible = false;
      closure$modelName.text = this$GltfDemo.models_0.prev().name;
      this$GltfDemo.models_0.current.isVisible = true;
      this$GltfDemo.orbitTransform_0.zoom = this$GltfDemo.models_0.current.zoom;
      this$GltfDemo.camTranslationTarget_0 = this$GltfDemo.models_0.current.lookAt;
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_4(closure$y, this$GltfDemo, closure$modelName) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = GltfDemo$menu$lambda$lambda$lambda$lambda_0(this$GltfDemo, closure$modelName);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda$lambda_1(this$GltfDemo, closure$modelName) {
    return function ($receiver, f, f_0, f_1) {
      this$GltfDemo.models_0.current.isVisible = false;
      closure$modelName.text = this$GltfDemo.models_0.next().name;
      this$GltfDemo.models_0.current.isVisible = true;
      this$GltfDemo.orbitTransform_0.zoom = this$GltfDemo.models_0.current.zoom;
      this$GltfDemo.camTranslationTarget_0 = this$GltfDemo.models_0.current.lookAt;
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_5(closure$y, this$GltfDemo, closure$modelName) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = GltfDemo$menu$lambda$lambda$lambda$lambda_1(this$GltfDemo, closure$modelName);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_6(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_7(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda$lambda_2(closure$speedVal, this$GltfDemo) {
    return function ($receiver, it) {
      closure$speedVal.text = toString($receiver.value, 2);
      this$GltfDemo.animationSpeed_0 = $receiver.value;
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_8(closure$y, closure$speedVal, this$GltfDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      var $receiver_0 = $receiver.onValueChanged;
      var element = GltfDemo$menu$lambda$lambda$lambda$lambda_2(closure$speedVal, this$GltfDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda$lambda_3(this$GltfDemo) {
    return function ($receiver) {
      this$GltfDemo.autoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda$lambda_9(closure$y, this$GltfDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$GltfDemo.autoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = GltfDemo$menu$lambda$lambda$lambda$lambda_3(this$GltfDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GltfDemo$menu$lambda$lambda_0(closure$smallFont, this$, this$GltfDemo) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-370.0), dps(-350.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(250.0), dps(230.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('glTF Models', GltfDemo$menu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Model:', GltfDemo$menu$lambda$lambda$lambda_2(y)));
      y.v -= 35.0;
      var modelName = this$.button_9zrh0o$(this$GltfDemo.models_0.current.name, GltfDemo$menu$lambda$lambda$lambda_3(y, this$GltfDemo));
      $receiver.unaryPlus_uv0sim$(modelName);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('prevModel', GltfDemo$menu$lambda$lambda$lambda_4(y, this$GltfDemo, modelName)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('nextModel', GltfDemo$menu$lambda$lambda$lambda_5(y, this$GltfDemo, modelName)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Animation Speed:', GltfDemo$menu$lambda$lambda$lambda_6(y)));
      var speedVal = this$.label_tokfmu$(toString(this$GltfDemo.animationSpeed_0, 2), GltfDemo$menu$lambda$lambda$lambda_7(y));
      $receiver.unaryPlus_uv0sim$(speedVal);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('speedSlider', 0.05, 2.0, this$GltfDemo.animationSpeed_0, GltfDemo$menu$lambda$lambda$lambda_8(y, speedVal, this$GltfDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', GltfDemo$menu$lambda$lambda$lambda_9(y, this$GltfDemo)));
      return Unit;
    };
  }
  function GltfDemo$menu$lambda(closure$ctx, this$GltfDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, GltfDemo$menu$lambda$lambda);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', GltfDemo$menu$lambda$lambda_0(smallFont, $receiver, this$GltfDemo)));
      return Unit;
    };
  }
  GltfDemo.prototype.menu_0 = function (ctx) {
    return uiScene(void 0, void 0, void 0, GltfDemo$menu$lambda(ctx, this));
  };
  GltfDemo.prototype.roundCylinder_0 = function ($receiver, radius, height) {
    var nCorner = 20;
    var cornerR = height / 2;
    var cornerPts = ArrayList_init();
    for (var i = 0; i <= nCorner; i++) {
      var a = math_0.PI / nCorner * i;
      var x = Math_0.sin(a) * cornerR + radius;
      var y = Math_0.cos(a) * cornerR - cornerR;
      var element = new Vec3f(x, y, 0.0);
      cornerPts.add_11rb$(element);
    }
    var uvScale = 0.3;
    var nCyl = 100;
    var firstI = {v: 0};
    for (var i_0 = 0; i_0 <= nCyl; i_0++) {
      var a_0 = math_0.PI / nCyl * i_0 * 2;
      var tmp$, tmp$_0;
      var index = 0;
      tmp$ = cornerPts.iterator();
      while (tmp$.hasNext()) {
        var item = tmp$.next();
        var ci = checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0));
        var uv = new MutableVec2f(radius + ci / cornerPts.size * math_0.PI * cornerR, 0.0);
        uv.scale_mx4ult$(uvScale);
        uv.rotate_mx4ult$(a_0 * math.RAD_2_DEG);
        var pt = item.rotate_vqa64j$(a_0 * math.RAD_2_DEG, Vec3f.Companion.Y_AXIS, MutableVec3f_init());
        var iv = $receiver.vertex_n440gp$(pt, Vec3f.Companion.ZERO, uv);
        if (i_0 > 0 && ci > 0) {
          $receiver.geometry.addTriIndices_qt1dr2$(iv - 1 | 0, iv - cornerPts.size - 1 | 0, iv - cornerPts.size | 0);
          $receiver.geometry.addTriIndices_qt1dr2$(iv, iv - 1 | 0, iv - cornerPts.size | 0);
        }if (i_0 === 0 && ci === 0) {
          firstI.v = iv;
        }}
    }
    var firstIBot = firstI.v + cornerPts.size - 1 | 0;
    for (var i_1 = 2; i_1 <= nCyl; i_1++) {
      $receiver.geometry.addTriIndices_qt1dr2$(firstI.v, firstI.v + Kotlin.imul(i_1 - 1 | 0, cornerPts.size) | 0, firstI.v + Kotlin.imul(i_1, cornerPts.size) | 0);
      $receiver.geometry.addTriIndices_qt1dr2$(firstIBot, firstIBot + Kotlin.imul(i_1, cornerPts.size) | 0, firstIBot + Kotlin.imul(i_1 - 1 | 0, cornerPts.size) | 0);
    }
    $receiver.geometry.generateNormals();
  };
  function GltfDemo$GltfModel($outer, name, assetPath, scale, translation, generateNormals, lookAt, zoom) {
    this.$outer = $outer;
    this.name = name;
    this.assetPath = assetPath;
    this.scale = scale;
    this.translation = translation;
    this.generateNormals = generateNormals;
    this.lookAt = lookAt;
    this.zoom = zoom;
    this.model = null;
    this.isVisible_eht3mo$_0 = false;
  }
  Object.defineProperty(GltfDemo$GltfModel.prototype, 'isVisible', {
    get: function () {
      return this.isVisible_eht3mo$_0;
    },
    set: function (value) {
      var tmp$;
      this.isVisible_eht3mo$_0 = value;
      (tmp$ = this.model) != null ? (tmp$.isVisible = value) : null;
    }
  });
  function GltfDemo$GltfModel$load$lambda$lambda$lambda(this$GltfDemo) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
      addAll($receiver.shadowMaps, this$GltfDemo.shadows_0);
      $receiver.scrSpcAmbientOcclusionMap = (tmp$ = this$GltfDemo.aoPipeline_0) != null ? tmp$.aoMap : null;
      $receiver.isScrSpcAmbientOcclusion = true;
      $receiver.setImageBasedLighting_5m8fyp$((tmp$_0 = this$GltfDemo.irrMapPass_0) != null ? tmp$_0.colorTextureCube : null, (tmp$_1 = this$GltfDemo.reflMapPass_0) != null ? tmp$_1.colorTextureCube : null, (tmp$_2 = this$GltfDemo.brdfLutPass_0) != null ? tmp$_2.colorTexture : null);
      return Unit;
    };
  }
  function GltfDemo$GltfModel$load$lambda$lambda$lambda$lambda(this$, this$GltfDemo) {
    return function ($receiver, f, f_0) {
      var $receiver_0 = this$.animations;
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.apply_14dthe$(this$GltfDemo.animationTime_0);
      }
      return Unit;
    };
  }
  function GltfDemo$GltfModel$load$lambda(this$GltfModel, this$GltfDemo) {
    return function (gltf) {
      if (gltf != null) {
        var this$GltfModel_0 = this$GltfModel;
        var this$GltfDemo_0 = this$GltfDemo;
        var modelCfg = new GltfFile$ModelGenerateConfig(this$GltfModel_0.generateNormals, void 0, true, true, void 0, void 0, GltfDemo$GltfModel$load$lambda$lambda$lambda(this$GltfDemo_0));
        var $receiver = gltf.makeModel_m0hq3v$(modelCfg);
        $receiver.translate_czzhiu$(this$GltfModel_0.translation);
        $receiver.scale_y2kzbl$(this$GltfModel_0.scale, this$GltfModel_0.scale, this$GltfModel_0.scale);
        $receiver.isVisible = this$GltfModel_0.isVisible;
        this$GltfDemo_0.contentGroup_0.plusAssign_f1kmr1$($receiver);
        if (!$receiver.animations.isEmpty()) {
          $receiver.onUpdate.add_11rb$(GltfDemo$GltfModel$load$lambda$lambda$lambda$lambda($receiver, this$GltfDemo_0));
        }this$GltfModel_0.model = $receiver;
      }return Unit;
    };
  }
  GltfDemo$GltfModel.prototype.load_aemszp$ = function (ctx) {
    loadGltfModel(ctx.assetMgr, this.assetPath, GltfDemo$GltfModel$load$lambda(this, this.$outer));
  };
  GltfDemo$GltfModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GltfModel',
    interfaces: []
  };
  GltfDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GltfDemo',
    interfaces: []
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
  function instanceDemo(ctx) {
    return (new InstanceDemo(ctx)).scenes;
  }
  function InstanceDemo(ctx) {
    this.scenes = ArrayList_init();
    this.nBunnies_0 = 10;
    this.isLodColors_0 = false;
    this.isAutoRotate_0 = true;
    this.modelCenter_0 = MutableVec3f_init();
    this.modelRadius_0 = 1.0;
    this.lodController_0 = new InstancedLodController();
    this.lods_0 = mutableListOf([new InstanceDemo$Lod(8, 10.0, MutableColor_init_0(Color.Companion.MD_PURPLE)), new InstanceDemo$Lod(32, 20.0, MutableColor_init_0(Color.Companion.MD_RED)), new InstanceDemo$Lod(128, 30.0, MutableColor_init_0(Color.Companion.MD_AMBER)), new InstanceDemo$Lod(500, 40.0, MutableColor_init_0(Color.Companion.MD_LIME)), new InstanceDemo$Lod(2000, 50.0, MutableColor_init_0(Color.Companion.MD_GREEN)), new InstanceDemo$Lod(10000, 1000.0, MutableColor_init_0(Color.Companion.MD_BLUE))]);
    var $receiver = this.scenes;
    var element = this.mainScene_0(ctx);
    $receiver.add_11rb$(element);
    var $receiver_0 = this.scenes;
    var element_0 = this.menu_0(ctx);
    $receiver_0.add_11rb$(element_0);
  }
  function InstanceDemo$mainScene$lambda$lambda$lambda(this$InstanceDemo, this$) {
    return function ($receiver, f, ctx) {
      if (this$InstanceDemo.isAutoRotate_0) {
        this$.verticalRotation += ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function InstanceDemo$mainScene$lambda$lambda(this$, this$InstanceDemo) {
    return function ($receiver) {
      var $receiver_0 = this$.camera;
      var tmp$;
      Kotlin.isType(tmp$ = $receiver_0, PerspectiveCamera) ? tmp$ : throwCCE();
      $receiver_0.clipNear = 1.0;
      $receiver_0.clipFar = 500.0;
      $receiver.unaryPlus_uv0sim$($receiver_0);
      $receiver.minZoom = 1.0;
      $receiver.maxZoom = 250.0;
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 40.0;
      $receiver.setMouseRotation_dleff0$(30.0, -40.0);
      var $receiver_1 = $receiver.onUpdate;
      var element = InstanceDemo$mainScene$lambda$lambda$lambda(this$InstanceDemo, $receiver);
      $receiver_1.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$mainScene$lambda$lambda_0($receiver) {
    $receiver.setDirectional_czzhiu$(Vec3f_init(-1.0));
    $receiver.setColor_y83vuj$(Color.Companion.WHITE, 1.0);
    return Unit;
  }
  function InstanceDemo$mainScene$lambda$lambda_1(this$InstanceDemo) {
    return function (gltf) {
      if (gltf != null) {
        this$InstanceDemo.addLods_0(gltf);
      }return Unit;
    };
  }
  InstanceDemo.prototype.mainScene_0 = function (ctx) {
    var $receiver = new Scene_init(null);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform($receiver, void 0, InstanceDemo$mainScene$lambda$lambda($receiver, this)));
    $receiver.lighting.singleLight_q9zcvo$(InstanceDemo$mainScene$lambda$lambda_0);
    $receiver.unaryPlus_uv0sim$(this.lodController_0);
    loadGltfModel(ctx.assetMgr, Demo$Companion_getInstance().modelBasePath + '/bunny.gltf.gz', InstanceDemo$mainScene$lambda$lambda_1(this));
    return $receiver;
  };
  InstanceDemo.prototype.addLods_0 = function (model) {
    var tmp$;
    tmp$ = model.scenes;
    for (var i = 0; i !== tmp$.size; ++i) {
      var modelCfg = new GltfFile$ModelGenerateConfig(true, void 0, void 0, void 0, void 0, false);
      var mesh = first(model.makeModel_m0hq3v$(modelCfg, i).meshes.values);
      var $this = mesh.geometry;
      var tmp$_0;
      tmp$_0 = $this.numVertices;
      for (var i_0 = 0; i_0 < tmp$_0; i_0++) {
        $this.vertexIt.index = i_0;
        $this.vertexIt.position.scale_mx4ult$(0.3).add_czzhiu$(new Vec3f(0.0, -1.0, 0.0));
      }
      mesh.geometry.rebuildBounds();
      if (i === 0) {
        this.modelCenter_0.set_czzhiu$(mesh.geometry.bounds.center);
        this.modelRadius_0 = mesh.geometry.bounds.max.distance_czzhiu$(mesh.geometry.bounds.center);
      }mesh.pipelineLoader = new PhongShader(void 0, this.instanceColorPhongModel_0());
      mesh.isFrustumChecked = false;
      this.lods_0.get_za3lpa$(i).mesh = mesh;
      mesh.instances = new MeshInstanceList(listOf([MeshInstanceList.Companion.MODEL_MAT, Attribute.Companion.COLORS]), this.lods_0.get_za3lpa$(i).maxInsts);
      this.lodController_0.addLod_od45r7$(mesh, this.lods_0.get_za3lpa$(i).maxDist);
    }
    this.setupInstances_0(this.lodController_0);
  };
  InstanceDemo.prototype.setupInstances_0 = function ($receiver) {
    var tmp$, tmp$_0, tmp$_1;
    var colors = listOf([Color.Companion.WHITE.toLinear(), Color.Companion.MD_RED.toLinear(), Color.Companion.MD_PINK.toLinear(), Color.Companion.MD_PURPLE.toLinear(), Color.Companion.MD_DEEP_PURPLE.toLinear(), Color.Companion.MD_INDIGO.toLinear(), Color.Companion.MD_BLUE.toLinear(), Color.Companion.MD_CYAN.toLinear(), Color.Companion.MD_TEAL.toLinear(), Color.Companion.MD_GREEN.toLinear(), Color.Companion.MD_LIGHT_GREEN.toLinear(), Color.Companion.MD_LIME.toLinear(), Color.Companion.MD_YELLOW.toLinear(), Color.Companion.MD_AMBER.toLinear(), Color.Companion.MD_ORANGE.toLinear(), Color.Companion.MD_DEEP_ORANGE.toLinear(), Color.Companion.MD_BROWN.toLinear(), Color.Companion.MD_GREY.toLinear(), Color.Companion.MD_BLUE_GREY.toLinear()]);
    $receiver.instances.clear();
    var rand = new Random(17);
    var off = (this.nBunnies_0 - 1 | 0) * 0.5;
    tmp$ = this.nBunnies_0;
    for (var x = 0; x < tmp$; x++) {
      tmp$_0 = this.nBunnies_0;
      for (var y = 0; y < tmp$_0; y++) {
        tmp$_1 = this.nBunnies_0;
        for (var z = 0; z < tmp$_1; z++) {
          var position = new MutableVec3f((x - off) * 5.0 + randomF(-2.0, 2.0), (y - off) * 5.0 + randomF(-2.0, 2.0), (z - off) * 5.0 + randomF(-2.0, 2.0));
          var rotAxis = new MutableVec3f(randomF(-1.0, 1.0), randomF(-1.0, 1.0), randomF(-1.0, 1.0));
          var tmp$_2 = $receiver.instances;
          var $receiver_0 = new InstanceDemo$BunnyInstance(this, position, rotAxis);
          $receiver_0.color.set_d7aj7k$(colors.get_za3lpa$(rand.randomI_n8acyv$(get_indices(colors))));
          $receiver_0.center.set_czzhiu$(this.modelCenter_0);
          $receiver_0.radius = this.modelRadius_0;
          tmp$_2.add_11rb$($receiver_0);
        }
      }
    }
  };
  InstanceDemo.prototype.instanceColorPhongModel_0 = function () {
    var $receiver = new ShaderModel('instanceColorPhongModel()');
    var ifNormals = {v: null};
    var ifColors = {v: null};
    var ifFragPos = {v: null};
    var mvpNode = {v: null};
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    ifColors.v = $receiver_0.stageInterfaceNode_iikjwn$('ifColors', $receiver_0.instanceAttributeNode_nm2vx5$(Attribute.Companion.COLORS).output);
    mvpNode.v = $receiver_0.mvpNode();
    var modelMat = $receiver_0.multiplyNode_ze33is$(mvpNode.v.outModelMat, $receiver_0.instanceAttrModelMat().output).output;
    var mvpMat = $receiver_0.multiplyNode_ze33is$(mvpNode.v.outMvpMat, $receiver_0.instanceAttrModelMat().output).output;
    var nrm = $receiver_0.vec3TransformNode_vid4wo$($receiver_0.attrNormals().output, modelMat, 0.0);
    ifNormals.v = $receiver_0.stageInterfaceNode_iikjwn$('ifNormals', nrm.outVec3);
    var worldPos = $receiver_0.vec3TransformNode_vid4wo$($receiver_0.attrPositions().output, modelMat, 1.0).outVec3;
    ifFragPos.v = $receiver_0.stageInterfaceNode_iikjwn$('ifFragPos', worldPos);
    $receiver_0.positionOutput = $receiver_0.vec4TransformNode_9krp9t$($receiver_0.attrPositions().output, mvpMat).outVec4;
    var $receiver_1 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    var mvpFrag = mvpNode.v.addToStage_llmhyc$($receiver.fragmentStageGraph);
    var lightNode = $receiver_1.multiLightNode_za3lpa$();
    var albedo = ifColors.v.output;
    var normal = ifNormals.v.output;
    var $receiver_2 = $receiver_1.phongMaterialNode_8rwtp1$(albedo, normal, ifFragPos.v.output, mvpFrag.outCamPos, lightNode);
    $receiver_2.inShininess = $receiver_1.pushConstantNode1f_61zpoe$('uShininess').output;
    $receiver_2.inSpecularIntensity = $receiver_1.pushConstantNode1f_61zpoe$('uSpecularIntensity').output;
    var phongMat = $receiver_2;
    $receiver_1.colorOutput_a3v4si$(phongMat.outColor);
    return $receiver;
  };
  function InstanceDemo$Lod(maxInsts, maxDist, color) {
    this.maxInsts = maxInsts;
    this.maxDist = maxDist;
    this.color = color;
    this.mesh = null;
  }
  InstanceDemo$Lod.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Lod',
    interfaces: []
  };
  function InstanceDemo$BunnyInstance($outer, position, rotAxis) {
    this.$outer = $outer;
    InstancedLodController$Instance.call(this);
    this.position = position;
    this.rotSpeed = rotAxis.length() * 120.0;
    this.rotAxis = rotAxis.norm_5s4mqq$(MutableVec3f_init());
    this.color = MutableColor_init();
  }
  InstanceDemo$BunnyInstance.prototype.update_l2cg23$ = function (lodCtrl, cam, ctx) {
    this.instanceModelMat.setIdentity().translate_czzhiu$(this.position).rotate_ad55pp$(ctx.time * this.rotSpeed, this.rotAxis);
    InstancedLodController$Instance.prototype.update_l2cg23$.call(this, lodCtrl, cam, ctx);
  };
  InstanceDemo$BunnyInstance.prototype.addInstanceData_bgc5cs$ = function (lod, instanceList, ctx) {
    this.$outer;
    instanceList.checkBufferSize_za3lpa$();
    var szBefore = instanceList.dataF.position;
    var this$InstanceDemo = this.$outer;
    var $receiver = instanceList.dataF;
    $receiver.put_q3cr5i$(this.instanceModelMat.matrix);
    if (this$InstanceDemo.isLodColors_0) {
      $receiver.put_q3cr5i$(this$InstanceDemo.lods_0.get_za3lpa$(lod).color.array);
    } else {
      $receiver.put_q3cr5i$(this.color.array);
    }
    var growSz = instanceList.dataF.position - szBefore | 0;
    if (growSz !== instanceList.instanceSizeF) {
      throw IllegalStateException_init('Expected data to grow by ' + instanceList.instanceSizeF + ' elements, instead it grew by ' + growSz);
    }instanceList.numInstances = instanceList.numInstances + 1 | 0;
    instanceList.hasChanged = true;
  };
  InstanceDemo$BunnyInstance.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BunnyInstance',
    interfaces: [InstancedLodController$Instance]
  };
  function InstanceDemo$menu$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function InstanceDemo$menu$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function InstanceDemo$menu$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(InstanceDemo$menu$lambda$lambda$lambda);
    $receiver.containerUi_2t3ptw$(InstanceDemo$menu$lambda$lambda$lambda_0);
    return Unit;
  }
  function InstanceDemo$menu$lambda$lambda$lambda_1(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_2(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda$lambda(this$InstanceDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      if (this$InstanceDemo.nBunnies_0 < 20) {
        tmp$ = this$InstanceDemo.nBunnies_0;
        this$InstanceDemo.nBunnies_0 = tmp$ + 1 | 0;
        $receiver.text = Kotlin.imul(Kotlin.imul(this$InstanceDemo.nBunnies_0, this$InstanceDemo.nBunnies_0), this$InstanceDemo.nBunnies_0).toString();
        this$InstanceDemo.setupInstances_0(this$InstanceDemo.lodController_0);
      }return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_3(closure$y, this$InstanceDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(45.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(40.0), dps(35.0), full());
      $receiver.text = Kotlin.imul(Kotlin.imul(this$InstanceDemo.nBunnies_0, this$InstanceDemo.nBunnies_0), this$InstanceDemo.nBunnies_0).toString();
      var $receiver_0 = $receiver.onClick;
      var element = InstanceDemo$menu$lambda$lambda$lambda$lambda(this$InstanceDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda$lambda_0(this$InstanceDemo, closure$btnBunnyCnt) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      if (this$InstanceDemo.nBunnies_0 > 5) {
        tmp$ = this$InstanceDemo.nBunnies_0;
        this$InstanceDemo.nBunnies_0 = tmp$ - 1 | 0;
        closure$btnBunnyCnt.text = Kotlin.imul(Kotlin.imul(this$InstanceDemo.nBunnies_0, this$InstanceDemo.nBunnies_0), this$InstanceDemo.nBunnies_0).toString();
        this$InstanceDemo.setupInstances_0(this$InstanceDemo.lodController_0);
      }return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_4(closure$y, this$InstanceDemo, closure$btnBunnyCnt) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(35.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = InstanceDemo$menu$lambda$lambda$lambda$lambda_0(this$InstanceDemo, closure$btnBunnyCnt);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda$lambda_1(this$InstanceDemo, closure$btnBunnyCnt) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      if (this$InstanceDemo.nBunnies_0 < 20) {
        tmp$ = this$InstanceDemo.nBunnies_0;
        this$InstanceDemo.nBunnies_0 = tmp$ + 1 | 0;
        closure$btnBunnyCnt.text = Kotlin.imul(Kotlin.imul(this$InstanceDemo.nBunnies_0, this$InstanceDemo.nBunnies_0), this$InstanceDemo.nBunnies_0).toString();
        this$InstanceDemo.setupInstances_0(this$InstanceDemo.lodController_0);
      }return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_5(closure$y, this$InstanceDemo, closure$btnBunnyCnt) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(85.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = InstanceDemo$menu$lambda$lambda$lambda$lambda_1(this$InstanceDemo, closure$btnBunnyCnt);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda$lambda_2(this$InstanceDemo) {
    return function ($receiver) {
      this$InstanceDemo.isLodColors_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_6(closure$y, this$InstanceDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$InstanceDemo.isLodColors_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = InstanceDemo$menu$lambda$lambda$lambda$lambda_2(this$InstanceDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda$lambda_3(this$InstanceDemo) {
    return function ($receiver) {
      this$InstanceDemo.isAutoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_7(closure$y, this$InstanceDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$InstanceDemo.isAutoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = InstanceDemo$menu$lambda$lambda$lambda$lambda_3(this$InstanceDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_8(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_9(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(30.0), full());
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda$lambda_4(this$InstanceDemo, closure$i, this$) {
    return function ($receiver, f, f_0) {
      var tmp$, tmp$_0, tmp$_1;
      var cnt = this$InstanceDemo.lodController_0.getInstanceCount_za3lpa$(closure$i);
      var tris = Kotlin.imul(cnt, (tmp$_1 = (tmp$_0 = (tmp$ = this$InstanceDemo.lods_0.get_za3lpa$(closure$i).mesh) != null ? tmp$.geometry : null) != null ? tmp$_0.numPrimitives : null) != null ? tmp$_1 : 0);
      this$.text = cnt.toString() + ' insts / ' + tris + ' tris';
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda$lambda_10(closure$y, this$InstanceDemo, closure$i) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(80.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(20.0), dps(30.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      var $receiver_0 = $receiver.onUpdate;
      var element = InstanceDemo$menu$lambda$lambda$lambda$lambda_4(this$InstanceDemo, closure$i, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda$lambda_0(closure$smallFont, this$, this$InstanceDemo) {
    return function ($receiver) {
      var tmp$;
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-450.0), dps(-535.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(330.0), dps(415.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Scene', InstanceDemo$menu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Bunnies:', InstanceDemo$menu$lambda$lambda$lambda_2(y)));
      var btnBunnyCnt = this$.button_9zrh0o$('bunnyCnt', InstanceDemo$menu$lambda$lambda$lambda_3(y, this$InstanceDemo));
      $receiver.unaryPlus_uv0sim$(btnBunnyCnt);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('decBunnyCnt', InstanceDemo$menu$lambda$lambda$lambda_4(y, this$InstanceDemo, btnBunnyCnt)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('incBunnyCnt', InstanceDemo$menu$lambda$lambda$lambda_5(y, this$InstanceDemo, btnBunnyCnt)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Color by LOD', InstanceDemo$menu$lambda$lambda$lambda_6(y, this$InstanceDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', InstanceDemo$menu$lambda$lambda$lambda_7(y, this$InstanceDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Info', InstanceDemo$menu$lambda$lambda$lambda_8(y, closure$smallFont, this$)));
      tmp$ = this$InstanceDemo.lods_0;
      for (var i = 0; i !== tmp$.size; ++i) {
        y.v -= 35.0;
        $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('LOD ' + i + ':', InstanceDemo$menu$lambda$lambda$lambda_9(y)));
        $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('LOD ' + i + ':', InstanceDemo$menu$lambda$lambda$lambda_10(y, this$InstanceDemo, i)));
      }
      return Unit;
    };
  }
  function InstanceDemo$menu$lambda(closure$ctx, this$InstanceDemo) {
    return function ($receiver) {
      var smallFontProps = new FontProps(Font.Companion.SYSTEM_FONT, 14.0);
      var smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, $receiver.uiDpi, closure$ctx, smallFontProps.style, smallFontProps.chars);
      $receiver.theme = theme(UiTheme.Companion.DARK, InstanceDemo$menu$lambda$lambda);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', InstanceDemo$menu$lambda$lambda_0(smallFont, $receiver, this$InstanceDemo)));
      return Unit;
    };
  }
  InstanceDemo.prototype.menu_0 = function (ctx) {
    return uiScene(void 0, void 0, void 0, InstanceDemo$menu$lambda(ctx, this));
  };
  InstanceDemo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InstanceDemo',
    interfaces: []
  };
  function multiLightDemo(ctx) {
    return (new MultiLightDemo(ctx)).scenes;
  }
  function MultiLightDemo(ctx) {
    MultiLightDemo$Companion_getInstance();
    this.scenes = ArrayList_init();
    this.mainScene_0 = new Scene_init();
    this.lights_0 = listOf([new MultiLightDemo$LightMesh(this, Color.Companion.MD_CYAN), new MultiLightDemo$LightMesh(this, Color.Companion.MD_RED), new MultiLightDemo$LightMesh(this, Color.Companion.MD_AMBER), new MultiLightDemo$LightMesh(this, Color.Companion.MD_GREEN)]);
    this.shadowMaps_0 = ArrayList_init();
    this.lightCount_0 = 4;
    this.lightPower_0 = 500.0;
    this.lightSaturation_0 = 0.4;
    this.lightRandomness_0 = 0.3;
    this.isPbrShading_0 = true;
    this.autoRotate_0 = true;
    this.showLightIndicators_0 = true;
    var $receiver = new Cycler(MultiLightDemo$Companion_getInstance().matColors_0);
    $receiver.index = 1;
    this.colorCycler_0 = $receiver;
    this.roughness_0 = 0.1;
    this.bunnyMesh_0 = null;
    this.groundMesh_0 = null;
    this.modelShader_0 = null;
    var tmp$;
    this.initMainScene_0(ctx);
    var $receiver_0 = this.scenes;
    var element = this.mainScene_0;
    $receiver_0.add_11rb$(element);
    var $receiver_1 = this.scenes;
    var element_0 = this.menu_0(ctx);
    $receiver_1.add_11rb$(element_0);
    tmp$ = this.lights_0;
    for (var i = 0; i !== tmp$.size; ++i) {
      var $receiver_2 = this.shadowMaps_0;
      var element_1 = new SimpleShadowMap(this.mainScene_0, i);
      $receiver_2.add_11rb$(element_1);
    }
  }
  function MultiLightDemo$initMainScene$lambda$lambda$lambda(this$MultiLightDemo, this$) {
    return function ($receiver, f, ctx) {
      if (this$MultiLightDemo.autoRotate_0) {
        this$.verticalRotation += ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function MultiLightDemo$initMainScene$lambda$lambda(this$, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 17.0;
      $receiver.translation.set_yvo9jy$(0.0, 2.0, 0.0);
      $receiver.setMouseRotation_dleff0$(0.0, -20.0);
      var $receiver_0 = $receiver.onUpdate;
      var element = MultiLightDemo$initMainScene$lambda$lambda$lambda(this$MultiLightDemo, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$initMainScene$lambda$lambda_0(this$MultiLightDemo, this$) {
    return function (gltf) {
      if (gltf != null) {
        var this$MultiLightDemo_0 = this$MultiLightDemo;
        var this$_0 = this$;
        var modelCfg = new GltfFile$ModelGenerateConfig(true, void 0, void 0, void 0, void 0, false);
        var model = gltf.makeModel_m0hq3v$(modelCfg);
        this$MultiLightDemo_0.bunnyMesh_0 = first(model.meshes.values);
        this$MultiLightDemo_0.applyShaders_0();
        this$_0.unaryPlus_uv0sim$(model);
      }return Unit;
    };
  }
  function MultiLightDemo$initMainScene$lambda$lambda$lambda_0($receiver) {
    var $receiver_0 = $receiver.rectProps.defaults();
    $receiver.rotate_ad55pp$(-90.0, Vec3f.Companion.X_AXIS);
    $receiver_0.size.set_dleff0$(100.0, 100.0);
    $receiver_0.origin.set_y2kzbl$(-$receiver_0.size.x / 2, -$receiver_0.size.y / 2, 0.0);
    $receiver_0.generateTexCoords_mx4ult$(4.0);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    return Unit;
  }
  function MultiLightDemo$initMainScene$lambda$lambda_1(this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(MultiLightDemo$initMainScene$lambda$lambda$lambda_0);
      $receiver.isCastingShadow = false;
      this$MultiLightDemo.groundMesh_0 = $receiver;
      this$MultiLightDemo.applyShaders_0();
      return Unit;
    };
  }
  MultiLightDemo.prototype.initMainScene_0 = function (ctx) {
    var $receiver = this.mainScene_0;
    $receiver.unaryPlus_uv0sim$(orbitInputTransform($receiver, void 0, MultiLightDemo$initMainScene$lambda$lambda($receiver, this)));
    $receiver.lighting.lights.clear();
    var tmp$;
    tmp$ = this.lights_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      $receiver.unaryPlus_uv0sim$(element);
    }
    this.updateLighting_0();
    loadGltfModel(ctx.assetMgr, Demo$Companion_getInstance().modelBasePath + '/bunny.gltf.gz', MultiLightDemo$initMainScene$lambda$lambda_0(this, $receiver));
    $receiver.unaryPlus_uv0sim$(textureMesh(void 0, true, MultiLightDemo$initMainScene$lambda$lambda_1(this)));
  };
  MultiLightDemo.prototype.applyShaders_0 = function () {
    if (this.isPbrShading_0) {
      this.applyPbrShaderBunny_0();
      this.applyPbrShaderGround_0();
    } else {
      this.applyPhongShaderBunny_0();
      this.applyPhongShaderGround_0();
    }
    this.updateModelColor_0();
    this.updateModelRoughness_0();
    this.updateLighting_0();
  };
  MultiLightDemo.prototype.applyPbrShaderBunny_0 = function () {
    var tmp$;
    if ((tmp$ = this.bunnyMesh_0) != null) {
      var $receiver = new PbrShader$PbrConfig();
      $receiver.albedoSource = Albedo.STATIC_ALBEDO;
      $receiver.maxLights = this.shadowMaps_0.size;
      addAll($receiver.shadowMaps, this.shadowMaps_0);
      $receiver.metallic = 0.0;
      var cfg = $receiver;
      this.modelShader_0 = new PbrShader(cfg);
      tmp$.pipelineLoader = this.modelShader_0;
    }};
  MultiLightDemo.prototype.applyPhongShaderBunny_0 = function () {
    var tmp$;
    if ((tmp$ = this.bunnyMesh_0) != null) {
      var $receiver = new PhongShader$PhongConfig();
      $receiver.albedoSource = Albedo.STATIC_ALBEDO;
      addAll($receiver.shadowMaps, this.shadowMaps_0);
      var cfg = $receiver;
      this.modelShader_0 = new PhongShader(cfg);
      tmp$.pipelineLoader = this.modelShader_0;
    }};
  function Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda.prototype.constructor = Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda;
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda.prototype.doResume = function () {
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
  function MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0.prototype.constructor = Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0;
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0.prototype.doResume = function () {
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
  function MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1.prototype.constructor = Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1;
  Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1.prototype.doResume = function () {
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
  function MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_2(this$) {
    return function ($receiver, it) {
      ensureNotNull(this$.albedoMap).dispose();
      ensureNotNull(this$.normalMap).dispose();
      ensureNotNull(this$.roughnessMap).dispose();
      return Unit;
    };
  }
  MultiLightDemo.prototype.applyPbrShaderGround_0 = function () {
    var tmp$;
    if ((tmp$ = this.groundMesh_0) != null) {
      var $receiver = new PbrShader$PbrConfig();
      $receiver.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver.isNormalMapped = true;
      $receiver.isRoughnessMapped = true;
      addAll($receiver.shadowMaps, this.shadowMaps_0);
      $receiver.albedoMap = new Texture(void 0, void 0, MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda);
      $receiver.normalMap = new Texture(void 0, void 0, MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_0);
      $receiver.roughnessMap = new Texture(void 0, void 0, MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_1);
      $receiver.metallic = 0.0;
      var cfg = $receiver;
      var $receiver_0 = new PbrShader(cfg);
      tmp$.onDispose.add_11rb$(MultiLightDemo$applyPbrShaderGround$lambda$lambda$lambda_2($receiver_0));
      tmp$.pipelineLoader = $receiver_0;
    }};
  function Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda.prototype.constructor = Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda;
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda.prototype.doResume = function () {
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
  function MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0($receiver_0, it_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0.prototype.constructor = Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0;
  Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0.prototype.doResume = function () {
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
  function MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0($receiver_0, it_0, continuation_0, suspended) {
    var instance = new Coroutine$MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0($receiver_0, it_0, this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_1(this$) {
    return function ($receiver, it) {
      ensureNotNull(this$.albedoMap).dispose();
      ensureNotNull(this$.normalMap).dispose();
      return Unit;
    };
  }
  MultiLightDemo.prototype.applyPhongShaderGround_0 = function () {
    var tmp$;
    if ((tmp$ = this.groundMesh_0) != null) {
      var $receiver = new PhongShader$PhongConfig();
      $receiver.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver.isNormalMapped = true;
      addAll($receiver.shadowMaps, this.shadowMaps_0);
      $receiver.albedoMap = new Texture(void 0, void 0, MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda);
      $receiver.normalMap = new Texture(void 0, void 0, MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_0);
      $receiver.shininess = 100.0;
      var cfg = $receiver;
      var $receiver_0 = new PhongShader(cfg);
      tmp$.onDispose.add_11rb$(MultiLightDemo$applyPhongShaderGround$lambda$lambda$lambda_1($receiver_0));
      tmp$.pipelineLoader = $receiver_0;
    }};
  MultiLightDemo.prototype.updateModelColor_0 = function () {
    var shader = this.modelShader_0;
    if (Kotlin.isType(shader, PbrShader))
      shader.albedo = this.colorCycler_0.current.linColor;
    else if (Kotlin.isType(shader, PhongShader))
      shader.albedo = this.colorCycler_0.current.linColor.toSrgb();
  };
  MultiLightDemo.prototype.updateModelRoughness_0 = function () {
    var shader = this.modelShader_0;
    if (Kotlin.isType(shader, PbrShader))
      shader.roughness = this.roughness_0;
    else if (Kotlin.isType(shader, PhongShader)) {
      var $receiver = 1.0 - this.roughness_0;
      shader.shininess = Math_0.pow($receiver, 2) * 100 + 1;
    }};
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
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_5(this$, this$MultiLightDemo, closure$tbPhong) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      this$MultiLightDemo.isPbrShading_0 = this$.isEnabled;
      this$MultiLightDemo.applyShaders_0();
      (tmp$ = closure$tbPhong.v) != null ? (tmp$.isEnabled = !this$.isEnabled) : null;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_13(closure$y, this$MultiLightDemo, closure$tbPhong) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$MultiLightDemo.isPbrShading_0;
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_5($receiver, this$MultiLightDemo, closure$tbPhong);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_6(this$, this$MultiLightDemo, closure$tbPbr) {
    return function ($receiver, f, f_0, f_1) {
      this$MultiLightDemo.isPbrShading_0 = !this$.isEnabled;
      this$MultiLightDemo.applyShaders_0();
      closure$tbPbr.isEnabled = !this$.isEnabled;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_14(closure$y, this$MultiLightDemo, closure$tbPbr) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = !this$MultiLightDemo.isPbrShading_0;
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_6($receiver, this$MultiLightDemo, closure$tbPbr);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_15(closure$y, closure$smallFont, this$) {
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
  function MultiLightDemo$menu$lambda$lambda$lambda_16(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.text = 'Color:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_7(this$MultiLightDemo) {
    return function ($receiver, f, f_0, f_1) {
      $receiver.text = this$MultiLightDemo.colorCycler_0.next().name;
      this$MultiLightDemo.updateModelColor_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_17(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(45.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(40.0), dps(35.0), full());
      $receiver.text = this$MultiLightDemo.colorCycler_0.current.name;
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_7(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_8(this$MultiLightDemo, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$matLabel.text = this$MultiLightDemo.colorCycler_0.prev().name;
      this$MultiLightDemo.updateModelColor_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_18(closure$y, this$MultiLightDemo, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(35.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '<';
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_8(this$MultiLightDemo, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_9(this$MultiLightDemo, closure$matLabel) {
    return function ($receiver, f, f_0, f_1) {
      closure$matLabel.text = this$MultiLightDemo.colorCycler_0.next().name;
      this$MultiLightDemo.updateModelColor_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_19(closure$y, this$MultiLightDemo, closure$matLabel) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(85.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(10.0), dps(35.0), full());
      $receiver.text = '>';
      var $receiver_0 = $receiver.onClick;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_9(this$MultiLightDemo, closure$matLabel);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_20(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), full());
      $receiver.text = 'Roughness:';
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_10(this$MultiLightDemo) {
    return function ($receiver, it) {
      this$MultiLightDemo.roughness_0 = $receiver.value / 100.0;
      this$MultiLightDemo.updateModelRoughness_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_21(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(30.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(70.0), dps(35.0), full());
      $receiver.value = 10.0;
      var $receiver_0 = $receiver.onValueChanged;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_10(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_22(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_11(this$MultiLightDemo) {
    return function ($receiver) {
      this$MultiLightDemo.autoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_23(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$MultiLightDemo.autoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_11(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda$lambda_12(this$MultiLightDemo) {
    return function ($receiver) {
      this$MultiLightDemo.showLightIndicators_0 = $receiver.isEnabled;
      this$MultiLightDemo.updateLighting_0();
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda$lambda_24(closure$y, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.isEnabled = this$MultiLightDemo.showLightIndicators_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = MultiLightDemo$menu$lambda$lambda$lambda$lambda_12(this$MultiLightDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function MultiLightDemo$menu$lambda$lambda_0(closure$smallFont, this$, this$MultiLightDemo) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-450.0), dps(-645.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(330.0), dps(525.0), full());
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
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Shading', MultiLightDemo$menu$lambda$lambda$lambda_12(y, closure$smallFont, this$)));
      y.v -= 35.0;
      var tbPbr;
      var tbPhong = {v: null};
      tbPbr = this$.toggleButton_6j87po$('Physical Based', MultiLightDemo$menu$lambda$lambda$lambda_13(y, this$MultiLightDemo, tbPhong));
      y.v -= 35.0;
      tbPhong.v = this$.toggleButton_6j87po$('Phong', MultiLightDemo$menu$lambda$lambda$lambda_14(y, this$MultiLightDemo, tbPbr));
      $receiver.unaryPlus_uv0sim$(tbPbr);
      $receiver.unaryPlus_uv0sim$(tbPhong.v);
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('material', MultiLightDemo$menu$lambda$lambda$lambda_15(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('colorLbl', MultiLightDemo$menu$lambda$lambda$lambda_16(y)));
      var matLabel = this$.button_9zrh0o$('selected-color', MultiLightDemo$menu$lambda$lambda$lambda_17(y, this$MultiLightDemo));
      $receiver.unaryPlus_uv0sim$(matLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('color-left', MultiLightDemo$menu$lambda$lambda$lambda_18(y, this$MultiLightDemo, matLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('color-right', MultiLightDemo$menu$lambda$lambda$lambda_19(y, this$MultiLightDemo, matLabel)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('roughnessLbl', MultiLightDemo$menu$lambda$lambda$lambda_20(y)));
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('roughhnessSlider', MultiLightDemo$menu$lambda$lambda$lambda_21(y, this$MultiLightDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Scene', MultiLightDemo$menu$lambda$lambda$lambda_22(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', MultiLightDemo$menu$lambda$lambda$lambda_23(y, this$MultiLightDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Light Indicators', MultiLightDemo$menu$lambda$lambda$lambda_24(y, this$MultiLightDemo)));
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
    var tmp$_0, tmp$_0_0;
    var index = 0;
    tmp$_0 = this.lights_0.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
      if (i < this.shadowMaps_0.size) {
        this.shadowMaps_0.get_za3lpa$(i).isShadowMapEnabled = false;
      }item.disable_kc3nav$(this.mainScene_0.lighting);
    }
    var pos = 0.0;
    var step = 360.0 / this.lightCount_0;
    var a = this.lightCount_0;
    var b = this.lights_0.size;
    tmp$ = Math_0.min(a, b);
    for (var i_0 = 0; i_0 < tmp$; i_0++) {
      this.lights_0.get_za3lpa$(i_0).setup_mx4ult$(pos);
      this.lights_0.get_za3lpa$(i_0).enable_kc3nav$(this.mainScene_0.lighting);
      pos += step;
      if (i_0 < this.shadowMaps_0.size) {
        this.shadowMaps_0.get_za3lpa$(i_0).isShadowMapEnabled = true;
      }}
    var tmp$_1;
    tmp$_1 = this.lights_0.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      element.updateVisibility();
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
    var $receiver_0 = this.onUpdate;
    var element = MultiLightDemo$MultiLightDemo$LightMesh_init$lambda_0(this.$outer, this);
    $receiver_0.add_11rb$(element);
  }
  MultiLightDemo$LightMesh.prototype.updateSpotAngleMesh_0 = function () {
    var x = this.light.spotAngle * math.DEG_2_RAD / 2;
    var r = 1.0 * Math_0.tan(x);
    var c = this.lightMeshShader_0.color;
    var n = 40;
    this.spotAngleMesh_0.clear();
    for (var i = 0; i < n; i++) {
      var a0 = i / n * 2 * math_0.PI;
      var a1 = (i + 1 | 0) / n * 2 * math_0.PI;
      this.spotAngleMesh_0.addLine_b8opkg$(new Vec3f(-1.0, Math_0.cos(a0) * r, Math_0.sin(a0) * r), c, new Vec3f(-1.0, Math_0.cos(a1) * r, Math_0.sin(a1) * r), c);
    }
    var x_0 = 45.0 * math.DEG_2_RAD;
    var e = Math_0.cos(x_0) * r;
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, e, e), c);
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, e, -e), c);
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, -e, -e), c);
    this.spotAngleMesh_0.addLine_b8opkg$(Vec3f.Companion.ZERO, c, new Vec3f(-1.0, -e, e), c);
  };
  MultiLightDemo$LightMesh.prototype.setup_mx4ult$ = function (angPos) {
    var x = angPos * math.DEG_2_RAD;
    var x_0 = Math_0.cos(x) * 10.0;
    var x_1 = angPos * math.DEG_2_RAD;
    var z = Math_0.sin(x_1) * 10.0;
    this.meshPos_0.set_y2kzbl$(x_0, 9.0, -z);
    this.anglePos_0 = angPos;
    var color = Color.Companion.WHITE.mix_jpxij3$(this.color, this.$outer.lightSaturation_0, MutableColor_init());
    if (this.$outer.isPbrShading_0) {
      this.light.setColor_y83vuj$(color.toLinear(), this.$outer.lightPower_0);
    } else {
      this.light.setColor_y83vuj$(color, this.$outer.lightPower_0 / 5.0);
    }
    this.lightMeshShader_0.color = color;
    this.updateSpotAngleMesh_0();
  };
  MultiLightDemo$LightMesh.prototype.enable_kc3nav$ = function (lighting) {
    this.isEnabled_0 = true;
    var $receiver = lighting.lights;
    if (!$receiver.contains_11rb$(this.light)) {
      $receiver.add_11rb$(this.light);
    }this.updateVisibility();
  };
  MultiLightDemo$LightMesh.prototype.disable_kc3nav$ = function (lighting) {
    this.isEnabled_0 = false;
    lighting.lights.remove_11rb$(this.light);
    this.updateVisibility();
  };
  MultiLightDemo$LightMesh.prototype.updateVisibility = function () {
    this.isVisible = (this.isEnabled_0 && this.$outer.showLightIndicators_0);
  };
  function MultiLightDemo$MultiLightDemo$LightMesh_init$lambda$lambda($receiver) {
    $receiver.sphereProps.uvDefaults().radius = 0.1;
    $receiver.uvSphere_mojs8w$($receiver.sphereProps);
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
    return function ($receiver, f, ctx) {
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
    this.matColors_0 = listOf([new MultiLightDemo$MatColor('White', Color.Companion.WHITE.toLinear()), new MultiLightDemo$MatColor('Red', Color.Companion.MD_RED.toLinear()), new MultiLightDemo$MatColor('Pink', Color.Companion.MD_PINK.toLinear()), new MultiLightDemo$MatColor('Purple', Color.Companion.MD_PURPLE.toLinear()), new MultiLightDemo$MatColor('Deep Purple', Color.Companion.MD_DEEP_PURPLE.toLinear()), new MultiLightDemo$MatColor('Indigo', Color.Companion.MD_INDIGO.toLinear()), new MultiLightDemo$MatColor('Blue', Color.Companion.MD_BLUE.toLinear()), new MultiLightDemo$MatColor('Cyan', Color.Companion.MD_CYAN.toLinear()), new MultiLightDemo$MatColor('Teal', Color.Companion.MD_TEAL.toLinear()), new MultiLightDemo$MatColor('Green', Color.Companion.MD_GREEN.toLinear()), new MultiLightDemo$MatColor('Light Green', Color.Companion.MD_LIGHT_GREEN.toLinear()), new MultiLightDemo$MatColor('Lime', Color.Companion.MD_LIME.toLinear()), new MultiLightDemo$MatColor('Yellow', Color.Companion.MD_YELLOW.toLinear()), new MultiLightDemo$MatColor('Amber', Color.Companion.MD_AMBER.toLinear()), new MultiLightDemo$MatColor('Orange', Color.Companion.MD_ORANGE.toLinear()), new MultiLightDemo$MatColor('Deep Orange', Color.Companion.MD_DEEP_ORANGE.toLinear()), new MultiLightDemo$MatColor('Brown', Color.Companion.MD_BROWN.toLinear()), new MultiLightDemo$MatColor('Grey', Color.Companion.MD_GREY.toLinear()), new MultiLightDemo$MatColor('Blue Grey', Color.Companion.MD_BLUE_GREY.toLinear()), new MultiLightDemo$MatColor('Almost Black', (new Color(0.1, 0.1, 0.1)).toLinear())]);
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
      var $receiver_0 = $receiver.sphereProps.uvDefaults();
      $receiver_0.steps = 100;
      $receiver_0.center.set_y2kzbl$(((-(closure$nCols_0 - 1 | 0) | 0) * 0.5 + closure$x_0) * closure$spacing_0, ((closure$nRows_0 - 1 | 0) * 0.5 - closure$y_0) * closure$spacing_0, 0.0);
      $receiver_0.radius = 1.5;
      $receiver.uvSphere_mojs8w$($receiver.sphereProps);
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
          var attributes = listOf([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]);
          var mesh = new Mesh_init(new IndexedVertexList_init_0(attributes), null);
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
    this.lightCycler_0 = new Cycler(PbrDemo$Companion_getInstance().lightSetups_0);
    this.hdriCycler_0 = new Cycler(PbrDemo$Companion_getInstance().hdriTextures_0);
    var array = Array_0(PbrDemo$Companion_getInstance().hdriTextures_0.size);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = null;
    }
    this.loadedHdris_0 = array;
    this.pbrContentCycler_0 = new Cycler(listOf([new PbrMaterialContent(), new ColorGridContent(), new RoughnesMetalGridContent()]));
    this.autoRotate_4vo62z$_0 = true;
    var nextHdriKeyListener = this.ctx.inputMgr.registerKeyListener_aviy8w$(-25, 'Next environment map', PbrDemo_init$lambda, PbrDemo_init$lambda_0(this));
    var prevHdriKeyListener = this.ctx.inputMgr.registerKeyListener_aviy8w$(-26, 'Prev environment map', PbrDemo_init$lambda_1, PbrDemo_init$lambda_2(this));
    this.contentScene_0 = this.setupScene_0();
    this.contentScene_0.onDispose.add_11rb$(PbrDemo_init$lambda_3(this, nextHdriKeyListener, prevHdriKeyListener));
    var $receiver = this.scenes;
    var element = this.contentScene_0;
    $receiver.add_11rb$(element);
    var $receiver_0 = this.scenes;
    var element_0 = this.pbrMenu_0();
    $receiver_0.add_11rb$(element_0);
  }
  Object.defineProperty(PbrDemo.prototype, 'autoRotate_0', {
    get: function () {
      return this.autoRotate_4vo62z$_0;
    },
    set: function (value) {
      this.autoRotate_4vo62z$_0 = value;
      var tmp$;
      tmp$ = this.pbrContentCycler_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.autoRotate = value;
      }
    }
  });
  function PbrDemo$setupScene$lambda$lambda$lambda(this$PbrDemo, this$) {
    return function ($receiver, f, ctx) {
      if (this$PbrDemo.autoRotate_0) {
        this$.verticalRotation += ctx.deltaT * 2.0;
      }return Unit;
    };
  }
  function PbrDemo$setupScene$lambda$lambda(this$, this$PbrDemo) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      var $receiver_0 = $receiver.onUpdate;
      var element = PbrDemo$setupScene$lambda$lambda$lambda(this$PbrDemo, $receiver);
      $receiver_0.add_11rb$(element);
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 20.0;
      return Unit;
    };
  }
  function PbrDemo$setupScene$lambda$lambda_0(this$, this$PbrDemo) {
    return function (tex) {
      var irrMapPass = new IrradianceMapPass(this$, tex);
      var reflMapPass = new ReflectionMapPass(this$, tex);
      var brdfLutPass = new BrdfLutPass(this$);
      this$PbrDemo.irradianceMapPass_0 = irrMapPass;
      this$PbrDemo.reflectionMapPass_0 = reflMapPass;
      this$PbrDemo.brdfLut_0 = brdfLutPass;
      this$.plusAssign_f1kmr1$(new Skybox(reflMapPass.colorTextureCube, 1.25));
      var $receiver = this$PbrDemo.pbrContentCycler_0;
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var this$_0 = this$;
        var this$PbrDemo_0 = this$PbrDemo;
        this$_0.unaryPlus_uv0sim$(element.createContent_wb8610$(this$_0, irrMapPass.colorTextureCube, reflMapPass.colorTextureCube, brdfLutPass.colorTexture, this$PbrDemo_0.ctx));
      }
      this$PbrDemo.pbrContentCycler_0.current.show();
      return Unit;
    };
  }
  PbrDemo.prototype.setupScene_0 = function () {
    var $receiver = new Scene_init(null);
    $receiver.mainRenderPass.clearColor = null;
    this.lightCycler_0.current.setup($receiver);
    $receiver.unaryPlus_uv0sim$(orbitInputTransform($receiver, void 0, PbrDemo$setupScene$lambda$lambda($receiver, this)));
    this.loadHdri_0(this.hdriCycler_0.index, PbrDemo$setupScene$lambda$lambda_0($receiver, this));
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
      $receiver.isEnabled = true;
      var $receiver_0 = $receiver.onClick;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_2(this$PbrDemo, $receiver);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_7(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_8(closure$y, this$PbrDemo) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_9(closure$y, this$PbrDemo, closure$lightLabel) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_10(closure$y, this$PbrDemo, closure$lightLabel) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_11(closure$y, closure$smallFont, this$) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_12(closure$y, this$PbrDemo) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_13(closure$y, this$PbrDemo, closure$contentLabel) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda_14(closure$y, this$PbrDemo, closure$contentLabel) {
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
  function PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_9(this$PbrDemo) {
    return function ($receiver) {
      this$PbrDemo.autoRotate_0 = $receiver.isEnabled;
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda$lambda_15(closure$y, this$PbrDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(8.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(84.0), dps(35.0), full());
      $receiver.isEnabled = this$PbrDemo.autoRotate_0;
      var $receiver_0 = $receiver.onStateChange;
      var element = PbrDemo$pbrMenu$lambda$lambda$lambda$lambda_9(this$PbrDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function PbrDemo$pbrMenu$lambda$lambda_0(closure$smallFont, this$, this$PbrDemo) {
    return function ($receiver) {
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-370.0), dps(-555.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(250.0), dps(435.0), full());
      var y = {v: -35.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Environment', PbrDemo$pbrMenu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var envLabel = this$.button_9zrh0o$('selected-env', PbrDemo$pbrMenu$lambda$lambda$lambda_2(y, this$PbrDemo));
      $receiver.unaryPlus_uv0sim$(envLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('env-left', PbrDemo$pbrMenu$lambda$lambda$lambda_3(y, this$PbrDemo, envLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('env-right', PbrDemo$pbrMenu$lambda$lambda$lambda_4(y, this$PbrDemo, envLabel)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Image Based Lighting', PbrDemo$pbrMenu$lambda$lambda$lambda_5(y, closure$smallFont, this$)));
      y.v -= 30.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('IBL Enabled', PbrDemo$pbrMenu$lambda$lambda$lambda_6(y, this$PbrDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Discrete Lighting', PbrDemo$pbrMenu$lambda$lambda$lambda_7(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var lightLabel = this$.button_9zrh0o$('selected-light', PbrDemo$pbrMenu$lambda$lambda$lambda_8(y, this$PbrDemo));
      $receiver.unaryPlus_uv0sim$(lightLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('light-left', PbrDemo$pbrMenu$lambda$lambda$lambda_9(y, this$PbrDemo, lightLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('light-right', PbrDemo$pbrMenu$lambda$lambda$lambda_10(y, this$PbrDemo, lightLabel)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('content-lbl', PbrDemo$pbrMenu$lambda$lambda$lambda_11(y, closure$smallFont, this$)));
      y.v -= 30.0;
      var contentLabel = this$.button_9zrh0o$('selected-content', PbrDemo$pbrMenu$lambda$lambda$lambda_12(y, this$PbrDemo));
      $receiver.unaryPlus_uv0sim$(contentLabel);
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('content-left', PbrDemo$pbrMenu$lambda$lambda$lambda_13(y, this$PbrDemo, contentLabel)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('content-right', PbrDemo$pbrMenu$lambda$lambda$lambda_14(y, this$PbrDemo, contentLabel)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', PbrDemo$pbrMenu$lambda$lambda$lambda_15(y, this$PbrDemo)));
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
        tmp$.hdriTexture = tex;
        tmp$.update();
      }if ((tmp$_0 = this$PbrDemo.reflectionMapPass_0) != null) {
        tmp$_0.hdriTexture = tex;
        tmp$_0.update();
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
    this.autoRotate = true;
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
    this.hdriTextures_0 = listOf([new PbrDemo$EnvironmentMap(Demo$Companion_getInstance().envMapBasePath + '/syferfontein_0d_clear_1k.rgbe.png', 'South Africa'), new PbrDemo$EnvironmentMap(Demo$Companion_getInstance().envMapBasePath + '/circus_arena_1k.rgbe.png', 'Circus'), new PbrDemo$EnvironmentMap(Demo$Companion_getInstance().envMapBasePath + '/newport_loft.rgbe.png', 'Loft'), new PbrDemo$EnvironmentMap(Demo$Companion_getInstance().envMapBasePath + '/shanghai_bund_1k.rgbe.png', 'Shanghai'), new PbrDemo$EnvironmentMap(Demo$Companion_getInstance().envMapBasePath + '/mossy_forest_1k.rgbe.png', 'Mossy Forest')]);
    this.lightStrength_0 = 250.0;
    this.lightExtent_0 = 10.0;
    this.lightSetups_0 = listOf([new PbrDemo$LightSetup('Off', PbrDemo$Companion$lightSetups$lambda), new PbrDemo$LightSetup('Front x1', PbrDemo$Companion$lightSetups$lambda_0(this)), new PbrDemo$LightSetup('Front x4', PbrDemo$Companion$lightSetups$lambda_1(this)), new PbrDemo$LightSetup('Top x1', PbrDemo$Companion$lightSetups$lambda_2(this)), new PbrDemo$LightSetup('Top x4', PbrDemo$Companion$lightSetups$lambda_3(this))]);
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
  PbrMaterialContent.prototype.nextMaterial_0 = function () {
    this.currentMat.disposeMaps();
    this.matCycler.next();
    this.updatePbrMaterial_0();
    return this.currentMat;
  };
  PbrMaterialContent.prototype.prevMaterial_0 = function () {
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
      $receiver.text = this$PbrMaterialContent.nextMaterial_0().name;
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
      closure$matLabel.text = this$PbrMaterialContent.prevMaterial_0().name;
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
      closure$matLabel.text = this$PbrMaterialContent.nextMaterial_0().name;
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
  function PbrMaterialContent$createContent$lambda$lambda(this$PbrMaterialContent, this$) {
    return function ($receiver, f, ctx) {
      if (this$PbrMaterialContent.autoRotate) {
        this$.rotate_ad55pp$(-2.0 * ctx.deltaT, Vec3f.Companion.Y_AXIS);
      }return Unit;
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
      var $receiver_1 = $receiver.onUpdate;
      var element = PbrMaterialContent$createContent$lambda$lambda(this$PbrMaterialContent, $receiver);
      $receiver_1.add_11rb$(element);
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
    var $receiver_0 = $receiver.sphereProps.uvDefaults();
    $receiver_0.steps = 700;
    $receiver_0.radius = 7.0;
    $receiver.uvSphere_mojs8w$($receiver.sphereProps);
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
      pbrConfig.irradianceMap = closure$irradianceMap;
      pbrConfig.reflectionMap = closure$reflectionMap;
      pbrConfig.brdfLut = closure$brdfLut;
      var $receiver_0 = new PbrShader(pbrConfig);
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
    this.materials_0 = mutableListOf([new PbrMaterialContent$MaterialMaps('Bamboo', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_0(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_1(this)), null, new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_2(this)), null), new PbrMaterialContent$MaterialMaps('Castle Brick', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_3(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_4(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_5(this)), null, new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_6(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_7(this))), new PbrMaterialContent$MaterialMaps('Granite', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_8(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_9(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_10(this)), null, null, null), new PbrMaterialContent$MaterialMaps('Weave Steel', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_11(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_12(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_13(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_14(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_15(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_16(this))), new PbrMaterialContent$MaterialMaps('Scuffed Plastic', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_17(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_18(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_19(this)), null, new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_20(this)), null), new PbrMaterialContent$MaterialMaps('Snow Covered Path', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_21(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_22(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_23(this)), null, new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_24(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_25(this))), new PbrMaterialContent$MaterialMaps('Marble', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_26(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_27(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_28(this)), null, null, null), new PbrMaterialContent$MaterialMaps('Onyx Tiles', new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_29(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_30(this)), new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_31(this)), null, null, new Texture(void 0, void 0, PbrMaterialContent$Companion$materials$lambda_32(this)))]);
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
      var $receiver_0 = $receiver.sphereProps.uvDefaults();
      $receiver_0.steps = 100;
      $receiver_0.center.set_y2kzbl$(((-(closure$nCols_0 - 1 | 0) | 0) * 0.5 + closure$x_0) * closure$spacing_0, ((-(closure$nRows_0 - 1 | 0) | 0) * 0.5 + closure$y_0) * closure$spacing_0, 0.0);
      $receiver_0.radius = 1.0;
      $receiver.uvSphere_mojs8w$($receiver.sphereProps);
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
          var attributes = listOf([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]);
          var mesh = new Mesh_init(new IndexedVertexList_init_0(attributes), null);
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
    this.matColors_0 = listOf([new RoughnesMetalGridContent$MatColor('Red', Color.Companion.MD_RED.toLinear()), new RoughnesMetalGridContent$MatColor('Pink', Color.Companion.MD_PINK.toLinear()), new RoughnesMetalGridContent$MatColor('Purple', Color.Companion.MD_PURPLE.toLinear()), new RoughnesMetalGridContent$MatColor('Deep Purple', Color.Companion.MD_DEEP_PURPLE.toLinear()), new RoughnesMetalGridContent$MatColor('Indigo', Color.Companion.MD_INDIGO.toLinear()), new RoughnesMetalGridContent$MatColor('Blue', Color.Companion.MD_BLUE.toLinear()), new RoughnesMetalGridContent$MatColor('Cyan', Color.Companion.MD_CYAN.toLinear()), new RoughnesMetalGridContent$MatColor('Teal', Color.Companion.MD_TEAL.toLinear()), new RoughnesMetalGridContent$MatColor('Green', Color.Companion.MD_GREEN.toLinear()), new RoughnesMetalGridContent$MatColor('Light Green', Color.Companion.MD_LIGHT_GREEN.toLinear()), new RoughnesMetalGridContent$MatColor('Lime', Color.Companion.MD_LIME.toLinear()), new RoughnesMetalGridContent$MatColor('Yellow', Color.Companion.MD_YELLOW.toLinear()), new RoughnesMetalGridContent$MatColor('Amber', Color.Companion.MD_AMBER.toLinear()), new RoughnesMetalGridContent$MatColor('Orange', Color.Companion.MD_ORANGE.toLinear()), new RoughnesMetalGridContent$MatColor('Deep Orange', Color.Companion.MD_DEEP_ORANGE.toLinear()), new RoughnesMetalGridContent$MatColor('Brown', Color.Companion.MD_BROWN.toLinear()), new RoughnesMetalGridContent$MatColor('White', Color.Companion.WHITE.toLinear()), new RoughnesMetalGridContent$MatColor('Grey', Color.Companion.MD_GREY.toLinear()), new RoughnesMetalGridContent$MatColor('Blue Grey', Color.Companion.MD_BLUE_GREY.toLinear()), new RoughnesMetalGridContent$MatColor('Almost Black', (new Color(0.1, 0.1, 0.1)).toLinear())]);
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
    this.dispModel = new Mesh_init(IndexedVertexList_init([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]));
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
    this.loadModel_0(Demo$Companion_getInstance().modelBasePath + '/bunny.gltf.gz', 1.0, new Vec3f(0.0, -3.0, 0.0), ctx);
    this.loadModel_0(Demo$Companion_getInstance().modelBasePath + '/cow.gltf.gz', 1.0, Vec3f.Companion.ZERO, ctx);
    this.loadModel_0(Demo$Companion_getInstance().modelBasePath + '/teapot.gltf.gz', 1.0, new Vec3f(0.0, -1.5, 0.0), ctx);
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
    return function ($receiver, f, f_0) {
      if (this$SimplificationDemo.autoRotate) {
        this$.rotate_ad55pp$(closure$ctx.deltaT * 3.0, Vec3f.Companion.Y_AXIS);
      }return Unit;
    };
  }
  function SimplificationDemo$mainScene$lambda$lambda(this$SimplificationDemo, closure$ctx) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.dispModel);
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.modelWireframe);
      var $receiver_0 = $receiver.onUpdate;
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
    this.timeValLbl.text = toString_0(time, 2) + ' s';
    $this.hasChanged = true;
    $this.isBatchUpdate = wasBatchUpdate;
    if (false) {
      $this.rebuildBounds();
    }};
  function SimplificationDemo$loadModel$lambda(closure$scale, closure$offset, closure$name, this$SimplificationDemo) {
    return function (model) {
      var tmp$;
      if (model != null) {
        var modelCfg = new GltfFile$ModelGenerateConfig(true, void 0, void 0, void 0, void 0, false);
        var mesh = first(model.makeModel_m0hq3v$(modelCfg).meshes.values);
        var geometry = mesh.geometry;
        tmp$ = geometry.numVertices;
        for (var i = 0; i < tmp$; i++) {
          geometry.vertexIt.index = i;
          geometry.vertexIt.position.scale_mx4ult$(closure$scale).add_czzhiu$(closure$offset);
        }
        var modelKey = substring(closure$name, until(lastIndexOf(closure$name, 47) + 1 | 0, lastIndexOf_0(closure$name, '.gltf.gz')));
        this$SimplificationDemo.models.put_xwzc9p$(modelKey, geometry);
        var $receiver = this$SimplificationDemo.loadingModels;
        var element = closure$name;
        $receiver.remove_11rb$(element);
        var $receiver_0 = this$SimplificationDemo;
        var $this = util.Log;
        var level = Log$Level.DEBUG;
        var tag = Kotlin.getKClassFromExpression($receiver_0).simpleName;
        if (level.level >= $this.level.level) {
          var tmp$_0 = $this.printer;
          var closure$name_0 = closure$name;
          var tmp$_1;
          tmp$_0.call($this, level, tag, 'loaded: ' + closure$name_0 + ', bounds: ' + toString_1((tmp$_1 = this$SimplificationDemo.models.get_11rb$(closure$name_0)) != null ? tmp$_1.bounds : null));
        }}return Unit;
    };
  }
  SimplificationDemo.prototype.loadModel_0 = function (name, scale, offset, ctx) {
    this.loadingModels.add_11rb$(name);
    loadGltfModel(ctx.assetMgr, name, SimplificationDemo$loadModel$lambda(scale, offset, name, this));
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
    var builder = new MeshBuilder(IndexedVertexList_init([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS]));
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
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('cow')) != null ? tmp$ : this$SimplificationDemo.srcModel;
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
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('teapot')) != null ? tmp$ : this$SimplificationDemo.srcModel;
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
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('bunny')) != null ? tmp$ : this$SimplificationDemo.srcModel;
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
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_2(this$SimplificationDemo) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      this$SimplificationDemo.srcModel = (tmp$ = this$SimplificationDemo.models.get_11rb$('cos')) != null ? tmp$ : this$SimplificationDemo.srcModel;
      this$SimplificationDemo.simplify();
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_5(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_2(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_6(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = this$SimplificationDemo.dispModel.isVisible;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_7(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = this$SimplificationDemo.modelWireframe.isVisible;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_8(this$SimplificationDemo, closure$tbDrawWireframe) {
    return function ($receiver) {
      this$SimplificationDemo.dispModel.isVisible = $receiver.isEnabled;
      if (!$receiver.isEnabled && !closure$tbDrawWireframe.isEnabled) {
        closure$tbDrawWireframe.isEnabled = true;
      }return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_9(this$SimplificationDemo, closure$tbDrawSolid) {
    return function ($receiver) {
      this$SimplificationDemo.modelWireframe.isVisible = $receiver.isEnabled;
      if (!$receiver.isEnabled && !closure$tbDrawSolid.isEnabled) {
        closure$tbDrawSolid.isEnabled = true;
      }return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_3(this$SimplificationDemo) {
    return function ($receiver) {
      this$SimplificationDemo.autoRotate = $receiver.isEnabled;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_10(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.isEnabled = this$SimplificationDemo.autoRotate;
      var $receiver_0 = $receiver.onStateChange;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_3(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_11(closure$y, closure$smallFont, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(30.0), full());
      $receiver.font.setCustom_11rb$(closure$smallFont);
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_12(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_13(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '100 %';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_4(closure$faceCntVal, this$SimplificationDemo) {
    return function ($receiver, value) {
      closure$faceCntVal.text = toString(value * 100.0, 0) + ' %';
      this$SimplificationDemo.simplifcationGrade = value;
      if (this$SimplificationDemo.autoRun.isEnabled) {
        this$SimplificationDemo.simplify();
      }return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_14(closure$y, closure$faceCntVal, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.01, 1.0, 1.0);
      var $receiver_0 = $receiver.onValueChanged;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_4(closure$faceCntVal, this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda$lambda_5(this$SimplificationDemo) {
    return function ($receiver, f, f_0, f_1) {
      this$SimplificationDemo.simplify();
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_15(closure$y, this$SimplificationDemo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(35.0), full());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = SimplificationDemo$menu$lambda$lambda$lambda$lambda_5(this$SimplificationDemo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_16(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.isEnabled = true;
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_17(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_18(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_19(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_20(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.textAlignment = new Gravity(Alignment.END, Alignment.CENTER);
      $receiver.text = '';
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_21(closure$y) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      return Unit;
    };
  }
  function SimplificationDemo$menu$lambda$lambda$lambda_22(closure$y) {
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
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(-450.0), dps(-705.0), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(330.0), dps(585.0), full());
      var y = {v: -40.0};
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('lights', SimplificationDemo$menu$lambda$lambda$lambda_1(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Cow', SimplificationDemo$menu$lambda$lambda$lambda_2(y, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Teapot', SimplificationDemo$menu$lambda$lambda$lambda_3(y, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Bunny', SimplificationDemo$menu$lambda$lambda$lambda_4(y, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Cosine Grid', SimplificationDemo$menu$lambda$lambda$lambda_5(y, this$SimplificationDemo)));
      y.v -= 45.0;
      var tbDrawSolid = this$.toggleButton_6j87po$('Draw Solid', SimplificationDemo$menu$lambda$lambda$lambda_6(y, this$SimplificationDemo));
      y.v -= 35.0;
      var tbDrawWireframe = this$.toggleButton_6j87po$('Draw Wireframe', SimplificationDemo$menu$lambda$lambda$lambda_7(y, this$SimplificationDemo));
      var $receiver_0 = tbDrawSolid.onStateChange;
      var element = SimplificationDemo$menu$lambda$lambda$lambda_8(this$SimplificationDemo, tbDrawWireframe);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = tbDrawWireframe.onStateChange;
      var element_0 = SimplificationDemo$menu$lambda$lambda$lambda_9(this$SimplificationDemo, tbDrawSolid);
      $receiver_1.add_11rb$(element_0);
      $receiver.unaryPlus_uv0sim$(tbDrawSolid);
      $receiver.unaryPlus_uv0sim$(tbDrawWireframe);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('Auto Rotate', SimplificationDemo$menu$lambda$lambda$lambda_10(y, this$SimplificationDemo)));
      y.v -= 40.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Simplification', SimplificationDemo$menu$lambda$lambda$lambda_11(y, closure$smallFont, this$)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Ratio:', SimplificationDemo$menu$lambda$lambda$lambda_12(y)));
      var faceCntVal = this$.label_tokfmu$('faceCntVal', SimplificationDemo$menu$lambda$lambda$lambda_13(y));
      $receiver.unaryPlus_uv0sim$(faceCntVal);
      y.v -= 25.0;
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('faceCnt', SimplificationDemo$menu$lambda$lambda$lambda_14(y, faceCntVal, this$SimplificationDemo)));
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Update Mesh', SimplificationDemo$menu$lambda$lambda$lambda_15(y, this$SimplificationDemo)));
      y.v -= 35.0;
      this$SimplificationDemo.autoRun = this$.toggleButton_6j87po$('Auto Update', SimplificationDemo$menu$lambda$lambda$lambda_16(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.autoRun);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Faces:', SimplificationDemo$menu$lambda$lambda$lambda_17(y)));
      this$SimplificationDemo.facesValLbl = this$.label_tokfmu$('facesValLbl', SimplificationDemo$menu$lambda$lambda$lambda_18(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.facesValLbl);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Vertices:', SimplificationDemo$menu$lambda$lambda$lambda_19(y)));
      this$SimplificationDemo.vertsValLbl = this$.label_tokfmu$('verticesValLbl', SimplificationDemo$menu$lambda$lambda$lambda_20(y));
      $receiver.unaryPlus_uv0sim$(this$SimplificationDemo.vertsValLbl);
      y.v -= 35.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Time:', SimplificationDemo$menu$lambda$lambda$lambda_21(y)));
      this$SimplificationDemo.timeValLbl = this$.label_tokfmu$('timeValLbl', SimplificationDemo$menu$lambda$lambda$lambda_22(y));
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
  var ShaderModel$findNode$lambda_0 = wrapFunction(function () {
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
        $this.printer(level, tag, 'Generated ' + ($receiver.geometry.numIndices / 3 | 0) + ' trunk triangles in' + ' ' + toString_0(now() - t, 3) + ' ms');
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
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
      (tmp$ = this$.albedoMap) != null ? (tmp$.dispose(), Unit) : null;
      (tmp$_0 = this$.ambientOcclusionMap) != null ? (tmp$_0.dispose(), Unit) : null;
      (tmp$_1 = this$.normalMap) != null ? (tmp$_1.dispose(), Unit) : null;
      (tmp$_2 = this$.roughnessMap) != null ? (tmp$_2.dispose(), Unit) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_4(this$, closure$uWindSpeed, closure$uWindStrength) {
    return function (it) {
      var tmp$, tmp$_0;
      var tmp$_1 = closure$uWindSpeed;
      var $this = this$.model;
      var name = 'windAnim';
      var stage;
      var findNode_3klnlw$result;
      findNode_3klnlw$break: do {
        stage = ShaderStage.ALL;
        var tmp$_2;
        tmp$_2 = $this.stages.values.iterator();
        while (tmp$_2.hasNext()) {
          var element = tmp$_2.next();
          if ((element.stage.mask & stage.mask) !== 0) {
            var tmp$_0_0;
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
            var node = (tmp$_0_0 = firstOrNull$result) == null || Kotlin.isType(tmp$_0_0, PushConstantNode1f) ? tmp$_0_0 : throwCCE();
            if (node != null) {
              findNode_3klnlw$result = node;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result = null;
      }
       while (false);
      tmp$_1.v = (tmp$ = findNode_3klnlw$result) != null ? tmp$.uniform : null;
      var tmp$_3 = closure$uWindStrength;
      var $this_0 = this$.model;
      var name_0 = 'windStrength';
      var stage_0;
      var findNode_3klnlw$result_0;
      findNode_3klnlw$break: do {
        stage_0 = ShaderStage.ALL;
        var tmp$_4;
        tmp$_4 = $this_0.stages.values.iterator();
        while (tmp$_4.hasNext()) {
          var element_1 = tmp$_4.next();
          if ((element_1.stage.mask & stage_0.mask) !== 0) {
            var tmp$_0_1;
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
            var node_0 = (tmp$_0_1 = firstOrNull$result_0) == null || Kotlin.isType(tmp$_0_1, PushConstantNode1f) ? tmp$_0_1 : throwCCE();
            if (node_0 != null) {
              findNode_3klnlw$result_0 = node_0;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result_0 = null;
      }
       while (false);
      tmp$_3.v = (tmp$_0 = findNode_3klnlw$result_0) != null ? tmp$_0.uniform : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_0(closure$windSpeed, closure$windAnimationPos, closure$uWindSpeed, closure$windStrength, closure$uWindStrength) {
    return function ($receiver, f, ctx) {
      var tmp$, tmp$_0;
      closure$windAnimationPos.v += ctx.deltaT * closure$windSpeed.v;
      (tmp$ = closure$uWindSpeed.v) != null ? (tmp$.value = closure$windAnimationPos.v) : null;
      (tmp$_0 = closure$uWindStrength.v) != null ? (tmp$_0.value = closure$windStrength.v) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda(closure$treeGen, this$, closure$shadowMaps, closure$windSpeed, closure$windAnimationPos, closure$windStrength) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(treeScene$lambda$lambda$lambda(closure$treeGen));
      var uWindSpeed = {v: null};
      var uWindStrength = {v: null};
      var $receiver_0 = new PbrShader$PbrConfig();
      var this$_0 = this$;
      var closure$shadowMaps_0 = closure$shadowMaps;
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.alphaMode = new AlphaModeOpaque();
      $receiver_0.isNormalMapped = true;
      $receiver_0.isAmbientOcclusionMapped = true;
      $receiver_0.isRoughnessMapped = true;
      $receiver_0.maxLights = this$_0.lighting.lights.size;
      $receiver_0.shadowMaps.addAll_brywnq$(closure$shadowMaps_0);
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg, treePbrModel(pbrCfg));
      $receiver_1.albedoMap = new Texture(void 0, void 0, treeScene$lambda$lambda$lambda$lambda);
      $receiver_1.ambientOcclusionMap = new Texture(void 0, void 0, treeScene$lambda$lambda$lambda$lambda_0);
      $receiver_1.normalMap = new Texture(void 0, void 0, treeScene$lambda$lambda$lambda$lambda_1);
      $receiver_1.roughnessMap = new Texture(void 0, void 0, treeScene$lambda$lambda$lambda$lambda_2);
      $receiver.onDispose.add_11rb$(treeScene$lambda$lambda$lambda$lambda_3($receiver_1));
      $receiver_1.onCreated.add_11rb$(treeScene$lambda$lambda$lambda$lambda_4($receiver_1, uWindSpeed, uWindStrength));
      $receiver.pipelineLoader = $receiver_1;
      var $receiver_2 = $receiver.onUpdate;
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
        $this.printer(level, tag, 'Generated ' + ($receiver.geometry.numIndices / 3 | 0) + ' leaf triangles in' + ' ' + toString_0(now() - t, 3) + ' ms');
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
            this.result_0 = this.local$it.loadTextureData_61zpoe$(Demo$Companion_getInstance().pbrBasePath + '/leaf.png', this);
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
  function treeScene$lambda$lambda$lambda$lambda_7(this$, closure$uWindSpeed, closure$uWindStrength) {
    return function (it) {
      var tmp$, tmp$_0;
      var tmp$_1 = closure$uWindSpeed;
      var $this = this$.model;
      var name = 'windAnim';
      var stage;
      var findNode_3klnlw$result;
      findNode_3klnlw$break: do {
        stage = ShaderStage.ALL;
        var tmp$_2;
        tmp$_2 = $this.stages.values.iterator();
        while (tmp$_2.hasNext()) {
          var element = tmp$_2.next();
          if ((element.stage.mask & stage.mask) !== 0) {
            var tmp$_0_0;
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
            var node = (tmp$_0_0 = firstOrNull$result) == null || Kotlin.isType(tmp$_0_0, PushConstantNode1f) ? tmp$_0_0 : throwCCE();
            if (node != null) {
              findNode_3klnlw$result = node;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result = null;
      }
       while (false);
      tmp$_1.v = (tmp$ = findNode_3klnlw$result) != null ? tmp$.uniform : null;
      var tmp$_3 = closure$uWindStrength;
      var $this_0 = this$.model;
      var name_0 = 'windStrength';
      var stage_0;
      var findNode_3klnlw$result_0;
      findNode_3klnlw$break: do {
        stage_0 = ShaderStage.ALL;
        var tmp$_4;
        tmp$_4 = $this_0.stages.values.iterator();
        while (tmp$_4.hasNext()) {
          var element_1 = tmp$_4.next();
          if ((element_1.stage.mask & stage_0.mask) !== 0) {
            var tmp$_0_1;
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
            var node_0 = (tmp$_0_1 = firstOrNull$result_0) == null || Kotlin.isType(tmp$_0_1, PushConstantNode1f) ? tmp$_0_1 : throwCCE();
            if (node_0 != null) {
              findNode_3klnlw$result_0 = node_0;
              break findNode_3klnlw$break;
            }}}
        findNode_3klnlw$result_0 = null;
      }
       while (false);
      tmp$_3.v = (tmp$_0 = findNode_3klnlw$result_0) != null ? tmp$_0.uniform : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_2(closure$windAnimationPos, closure$uWindSpeed, closure$windStrength, closure$uWindStrength) {
    return function ($receiver, f, ctx) {
      var tmp$, tmp$_0;
      (tmp$ = closure$uWindSpeed.v) != null ? (tmp$.value = closure$windAnimationPos.v) : null;
      (tmp$_0 = closure$uWindStrength.v) != null ? (tmp$_0.value = closure$windStrength.v) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda_0(closure$treeGen, this$, closure$shadowMaps, closure$windAnimationPos, closure$windStrength) {
    return function ($receiver) {
      $receiver.generate_v2sixm$(treeScene$lambda$lambda$lambda_1(closure$treeGen));
      var uWindSpeed = {v: null};
      var uWindStrength = {v: null};
      var $receiver_0 = new PbrShader$PbrConfig();
      var this$_0 = this$;
      var closure$shadowMaps_0 = closure$shadowMaps;
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.alphaMode = new AlphaModeMask(0.5);
      $receiver_0.maxLights = this$_0.lighting.lights.size;
      $receiver_0.lightBacksides = true;
      $receiver_0.cullMethod = CullMethod.NO_CULLING;
      $receiver_0.shadowMaps.addAll_brywnq$(closure$shadowMaps_0);
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg, treePbrModel(pbrCfg));
      $receiver_1.albedoMap = new Texture(void 0, void 0, treeScene$lambda$lambda$lambda$lambda_5);
      $receiver_1.roughness = 0.5;
      $receiver.onDispose.add_11rb$(treeScene$lambda$lambda$lambda$lambda_6($receiver_1));
      $receiver_1.onCreated.add_11rb$(treeScene$lambda$lambda$lambda$lambda_7($receiver_1, uWindSpeed, uWindStrength));
      $receiver.pipelineLoader = $receiver_1;
      var $receiver_2 = $receiver.onUpdate;
      var element = treeScene$lambda$lambda$lambda_2(closure$windAnimationPos, uWindSpeed, closure$windStrength, uWindStrength);
      $receiver_2.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_3(closure$autoRotate, this$) {
    return function ($receiver, f, ctx) {
      if (closure$autoRotate.v) {
        this$.verticalRotation += ctx.deltaT * 3.0;
      }return Unit;
    };
  }
  function treeScene$lambda$lambda_1(this$, closure$autoRotate) {
    return function ($receiver) {
      var tmp$;
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.minZoom = 1.0;
      $receiver.maxZoom = 50.0;
      $receiver.zoomMethod = OrbitInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.zoom = 6.0;
      $receiver.setMouseRotation_dleff0$(0.0, -10.0);
      $receiver.setMouseTranslation_y2kzbl$(0.0, 2.0, 0.0);
      (Kotlin.isType(tmp$ = this$.camera, PerspectiveCamera) ? tmp$ : throwCCE()).clipFar = 50.0;
      var $receiver_0 = $receiver.onUpdate;
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
  function treeScene$lambda$lambda_2($receiver) {
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
      $receiver.text = toString(closure$treeGen.growDistance, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_8(closure$treeGen, closure$growDistVal) {
    return function ($receiver, value) {
      closure$treeGen.growDistance = value;
      closure$growDistVal.text = toString(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_9(closure$y, closure$treeGen, closure$growDistVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.1, 0.4, closure$treeGen.growDistance);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_8(closure$treeGen, closure$growDistVal);
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
      $receiver.text = toString(closure$treeGen.killDistance, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_9(closure$treeGen, closure$killDistVal) {
    return function ($receiver, value) {
      closure$treeGen.killDistance = value;
      closure$killDistVal.text = toString(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_12(closure$y, closure$treeGen, closure$killDistVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(1.0, 4.0, closure$treeGen.killDistance);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_9(closure$treeGen, closure$killDistVal);
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
  function treeScene$lambda$lambda$lambda$lambda_10(closure$treeGen, closure$attractPtsVal) {
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
      var element = treeScene$lambda$lambda$lambda$lambda_10(closure$treeGen, closure$attractPtsVal);
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
      $receiver.text = toString(closure$treeGen.radiusOfInfluence, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_11(closure$treeGen, closure$infRadiusVal) {
    return function ($receiver, value) {
      closure$treeGen.radiusOfInfluence = value;
      closure$infRadiusVal.text = toString(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_18(closure$y, closure$treeGen, closure$infRadiusVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.25, 10.0, closure$treeGen.radiusOfInfluence);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_11(closure$treeGen, closure$infRadiusVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_12(closure$treeGen, closure$trunkMesh, closure$leafMesh) {
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
        $this.generateTangents_mx4ult$();
        var ret = Unit;
        var $this_0 = util.Log;
        var tag = Kotlin.getKClassFromExpression($this).simpleName;
        if (level.level >= $this_0.level.level) {
          $this_0.printer(level, tag, 'Generated ' + ($this.numIndices / 3 | 0) + ' trunk triangles in' + ' ' + toString_0(now() - t, 3) + ' ms');
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
          $this_2.printer(level_0, tag_0, 'Generated ' + ($this_1.numIndices / 3 | 0) + ' leaf triangles in' + ' ' + toString_0(now() - t_0, 3) + ' ms');
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
      var element = treeScene$lambda$lambda$lambda$lambda_12(closure$treeGen, closure$trunkMesh, closure$leafMesh);
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
      $receiver.text = toString(closure$windSpeed.v, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_13(closure$windSpeed, closure$windSpeedVal) {
    return function ($receiver, value) {
      closure$windSpeed.v = value;
      closure$windSpeedVal.text = toString(value, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_23(closure$y, closure$windSpeed, closure$windSpeedVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.0, 10.0, closure$windSpeed.v);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_13(closure$windSpeed, closure$windSpeedVal);
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
      $receiver.text = toString(closure$windStrength.v, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_14(closure$windStrength, closure$windStrengthVal) {
    return function ($receiver, value) {
      closure$windStrength.v = value;
      closure$windStrengthVal.text = toString(value, 1);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_26(closure$y, closure$treeGen, closure$windStrength, closure$windStrengthVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0), dps(closure$y.v), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(25.0), full());
      $receiver.setValue_y2kzbl$(0.0, 5.0, closure$treeGen.radiusOfInfluence);
      var $receiver_0 = $receiver.onValueChanged;
      var element = treeScene$lambda$lambda$lambda$lambda_14(closure$windStrength, closure$windStrengthVal);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_15(closure$leafMesh) {
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
      var element = treeScene$lambda$lambda$lambda$lambda_15(closure$leafMesh);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_16(closure$autoRotate) {
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
      var element = treeScene$lambda$lambda$lambda$lambda_16(closure$autoRotate);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda_3(closure$smallFont, this$, closure$treeGen, closure$trunkMesh, closure$leafMesh, closure$windSpeed, closure$windStrength, closure$autoRotate) {
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
      $receiver.theme = theme(UiTheme.Companion.DARK, treeScene$lambda$lambda_2);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu container', treeScene$lambda$lambda_3(smallFont, $receiver, closure$treeGen, closure$trunkMesh, closure$leafMesh, closure$windSpeed, closure$windStrength, closure$autoRotate)));
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
    var backLightDirection = new Vec3f(1.0, -1.5, 1.0);
    var sunLightDirection = new Vec3f(-1.0, -1.5, -1.0);
    var $receiver_0 = $receiver.lighting.lights;
    $receiver_0.clear();
    $receiver_0.add_11rb$((new Light()).setDirectional_czzhiu$(sunLightDirection.norm_5s4mqq$(MutableVec3f_init())).setColor_y83vuj$(Color.Companion.MD_AMBER.mix_y83vuj$(Color.Companion.WHITE, 0.6).toLinear(), 3.0));
    $receiver_0.add_11rb$((new Light()).setDirectional_czzhiu$(backLightDirection.norm_5s4mqq$(MutableVec3f_init())).setColor_y83vuj$(Color.Companion.MD_AMBER.mix_y83vuj$(Color.Companion.WHITE, 0.6).toLinear(), 0.25));
    var $receiver_1 = new CascadedShadowMap($receiver, 0, void 0, 2048);
    $receiver_1.maxRange = 50.0;
    var shadowMaps = mutableListOf([$receiver_1]);
    $receiver.unaryPlus_uv0sim$(makeTreeGroundGrid(10, shadowMaps));
    trunkMesh.v = textureMesh(void 0, true, treeScene$lambda$lambda(treeGen, $receiver, shadowMaps, windSpeed, windAnimationPos, windStrength));
    leafMesh.v = textureMesh(void 0, void 0, treeScene$lambda$lambda_0(treeGen, $receiver, shadowMaps, windAnimationPos, windStrength));
    $receiver.unaryPlus_uv0sim$(ensureNotNull(trunkMesh.v));
    $receiver.unaryPlus_uv0sim$(ensureNotNull(leafMesh.v));
    $receiver.unaryPlus_uv0sim$(orbitInputTransform($receiver, void 0, treeScene$lambda$lambda_1($receiver, autoRotate)));
    var treeScene = $receiver;
    scenes.add_11rb$(treeScene);
    var element = uiScene(void 0, void 0, void 0, treeScene$lambda(ctx, treeGen, trunkMesh, leafMesh, windSpeed, windStrength, autoRotate));
    scenes.add_11rb$(element);
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
    var mvpNode = {v: null};
    var shadowMapNodes = ArrayList_init();
    var $receiver_0 = new ShaderModel$ShaderModel$VertexStageBuilder_init($receiver);
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    mvpNode.v = $receiver_0.mvpNode();
    var nrm = $receiver_0.vec3TransformNode_vid4wo$($receiver_0.attrNormals().output, mvpNode.v.outModelMat, 0.0);
    ifNormals.v = $receiver_0.stageInterfaceNode_iikjwn$('ifNormals', nrm.outVec3);
    if (cfg.requiresTexCoords()) {
      tmp$ = $receiver_0.stageInterfaceNode_iikjwn$('ifTexCoords', $receiver_0.attrTexCoords().output);
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
    var staticLocalPos = tmp$_0;
    var $receiver_2 = $receiver_0.addNode_u9w9by$(new WindNode($receiver.vertexStageGraph));
    $receiver_2.inputPos = staticLocalPos;
    $receiver_2.inputAnim = $receiver_0.pushConstantNode1f_61zpoe$('windAnim').output;
    $receiver_2.inputStrength = $receiver_0.pushConstantNode1f_61zpoe$('windStrength').output;
    var windNd = $receiver_2;
    var localPos = windNd.outputPos;
    var worldPos = $receiver_0.vec3TransformNode_vid4wo$(localPos, mvpNode.v.outModelMat, 1.0).outVec3;
    ifFragPos.v = $receiver_0.stageInterfaceNode_iikjwn$('ifFragPos', worldPos);
    if (cfg.albedoSource === Albedo.VERTEX_ALBEDO) {
      tmp$_1 = $receiver_0.stageInterfaceNode_iikjwn$('ifColors', $receiver_0.attrColors().output);
    } else {
      tmp$_1 = null;
    }
    ifColors.v = tmp$_1;
    if (cfg.isNormalMapped) {
      var tanAttr = $receiver_0.attrTangents().output;
      var tan = $receiver_0.vec3TransformNode_vid4wo$($receiver_0.splitNode_500t7j$(tanAttr, 'xyz').output, mvpNode.v.outModelMat, 0.0);
      var tan4 = $receiver_0.combineXyzWNode_ze33is$(tan.outVec3, $receiver_0.splitNode_500t7j$(tanAttr, 'w').output);
      tmp$_2 = $receiver_0.stageInterfaceNode_iikjwn$('ifTangents', tan4.output);
    } else {
      tmp$_2 = null;
    }
    ifTangents.v = tmp$_2;
    var viewPos = $receiver_0.vec4TransformNode_9krp9t$(worldPos, mvpNode.v.outViewMat).outVec4;
    var tmp$_3, tmp$_0_0;
    var index = 0;
    tmp$_3 = cfg.shadowMaps.iterator();
    while (tmp$_3.hasNext()) {
      var item = tmp$_3.next();
      var i = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
      if (Kotlin.isType(item, CascadedShadowMap)) {
        var element = $receiver_0.cascadedShadowMapNode_zw58l$(item, 'depthMap_' + i, viewPos, worldPos);
        shadowMapNodes.add_11rb$(element);
      } else if (Kotlin.isType(item, SimpleShadowMap)) {
        var element_0 = $receiver_0.simpleShadowMapNode_7yd351$(item, 'depthMap_' + i, worldPos);
        shadowMapNodes.add_11rb$(element_0);
      }}
    $receiver_0.positionOutput = $receiver_0.vec4TransformNode_9krp9t$(localPos, mvpNode.v.outMvpMat).outVec4;
    var $receiver_3 = new ShaderModel$ShaderModel$FragmentStageBuilder_init($receiver);
    var tmp$_4, tmp$_5, tmp$_6, tmp$_7;
    switch (cfg.albedoSource.name) {
      case 'VERTEX_ALBEDO':
        tmp$_4 = ensureNotNull(ifColors.v).output;
        break;
      case 'STATIC_ALBEDO':
        tmp$_4 = $receiver_3.pushConstantNodeColor_61zpoe$('uAlbedo').output;
        break;
      case 'TEXTURE_ALBEDO':
        var albedoSampler = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$('tAlbedo'), ensureNotNull(ifTexCoords.v).output);
        tmp$_4 = $receiver_3.gammaNode_r20yfm$(albedoSampler.outColor).outColor;
        break;
      default:tmp$_4 = Kotlin.noWhenBranchMatched();
        break;
    }
    var albedo = {v: tmp$_4};
    if ((tmp$_6 = Kotlin.isType(tmp$_5 = cfg.alphaMode, AlphaModeMask) ? tmp$_5 : null) != null) {
      $receiver_3.discardAlpha_ze33is$($receiver_3.splitNode_500t7j$(albedo.v, 'a').output, $receiver_3.constFloat_mx4ult$(tmp$_6.cutOff));
    }var $receiver_4 = $receiver_3.combineNode_m7a9qd$(GlslType.VEC_4F);
    $receiver_4.inX = $receiver_3.splitNode_500t7j$(albedo.v, 'r').output;
    $receiver_4.inY = $receiver_3.splitNode_500t7j$(albedo.v, 'g').output;
    $receiver_4.inZ = $receiver_3.splitNode_500t7j$(albedo.v, 'b').output;
    $receiver_4.inW = $receiver_3.constFloat_mx4ult$(1.0);
    albedo.v = $receiver_4.output;
    var mvpFrag = mvpNode.v.addToStage_llmhyc$($receiver.fragmentStageGraph);
    var lightNode = $receiver_3.multiLightNode_za3lpa$(cfg.maxLights);
    var tmp$_8;
    tmp$_8 = shadowMapNodes.iterator();
    while (tmp$_8.hasNext()) {
      var element_1 = tmp$_8.next();
      lightNode.inShaodwFacs[element_1.lightIndex] = element_1.outShadowFac;
    }
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
    var $receiver_5 = $receiver_3.pbrMaterialNode_od0lt5$(lightNode, reflMap, brdfLut);
    var closure$irrSampler = irrSampler;
    var tmp$_9, tmp$_10;
    $receiver_5.lightBacksides = cfg.lightBacksides;
    $receiver_5.inFragPos = ifFragPos.v.output;
    $receiver_5.inCamPos = mvpFrag.outCamPos;
    $receiver_5.inIrradiance = (tmp$_9 = closure$irrSampler != null ? closure$irrSampler.outColor : null) != null ? tmp$_9 : $receiver_3.pushConstantNodeColor_61zpoe$('uAmbient').output;
    $receiver_5.inAlbedo = albedo.v;
    if (cfg.isNormalMapped && ifTangents.v != null) {
      var bumpNormal = $receiver_3.normalMapNode_j8913i$($receiver_3.textureNode_61zpoe$('tNormal'), ensureNotNull(ifTexCoords.v).output, ifNormals.v.output, ifTangents.v.output);
      bumpNormal.inStrength = new ShaderNodeIoVar(new ModelVar1fConst(cfg.normalStrength));
      tmp$_10 = bumpNormal.outNormal;
    } else {
      tmp$_10 = ifNormals.v.output;
    }
    $receiver_5.inNormal = tmp$_10;
    var rmoSamplers = LinkedHashMap_init();
    if (cfg.isRoughnessMapped) {
      var roughness = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$(cfg.roughnessTexName), ensureNotNull(ifTexCoords.v).output).outColor;
      var key = cfg.roughnessTexName;
      rmoSamplers.put_xwzc9p$(key, roughness);
      $receiver_5.inRoughness = $receiver_3.splitNode_500t7j$(roughness, cfg.roughnessChannel).output;
    } else {
      $receiver_5.inRoughness = $receiver_3.pushConstantNode1f_61zpoe$('uRoughness').output;
    }
    if (cfg.isMetallicMapped) {
      var key_0 = cfg.metallicTexName;
      var tmp$_11;
      var value = rmoSamplers.get_11rb$(key_0);
      if (value == null) {
        var answer = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$(cfg.metallicTexName), ensureNotNull(ifTexCoords.v).output).outColor;
        rmoSamplers.put_xwzc9p$(key_0, answer);
        tmp$_11 = answer;
      } else {
        tmp$_11 = value;
      }
      var metallic = tmp$_11;
      var key_1 = cfg.metallicTexName;
      rmoSamplers.put_xwzc9p$(key_1, metallic);
      $receiver_5.inMetallic = $receiver_3.splitNode_500t7j$(metallic, cfg.metallicChannel).output;
    } else {
      $receiver_5.inMetallic = $receiver_3.pushConstantNode1f_61zpoe$('uMetallic').output;
    }
    if (cfg.isAmbientOcclusionMapped) {
      var key_2 = cfg.ambientOcclusionTexName;
      var tmp$_12;
      var value_0 = rmoSamplers.get_11rb$(key_2);
      if (value_0 == null) {
        var answer_0 = $receiver_3.textureSamplerNode_ce41yx$($receiver_3.textureNode_61zpoe$(cfg.ambientOcclusionTexName), ensureNotNull(ifTexCoords.v).output).outColor;
        rmoSamplers.put_xwzc9p$(key_2, answer_0);
        tmp$_12 = answer_0;
      } else {
        tmp$_12 = value_0;
      }
      var occlusion = tmp$_12;
      var key_3 = cfg.ambientOcclusionTexName;
      rmoSamplers.put_xwzc9p$(key_3, occlusion);
      $receiver_5.inAmbientOccl = $receiver_3.splitNode_500t7j$(occlusion, cfg.ambientOcclusionChannel).output;
    }var mat = $receiver_5;
    tmp$_7 = cfg.alphaMode;
    if (Kotlin.isType(tmp$_7, AlphaModeBlend))
      $receiver_3.colorOutput_a3v4si$($receiver_3.hdrToLdrNode_r20yfm$(mat.outColor).outColor);
    else if (Kotlin.isType(tmp$_7, AlphaModeMask))
      $receiver_3.colorOutput_a3v4si$($receiver_3.hdrToLdrNode_r20yfm$(mat.outColor).outColor, void 0, $receiver_3.constFloat_mx4ult$(1.0));
    else if (Kotlin.isType(tmp$_7, AlphaModeOpaque))
      $receiver_3.colorOutput_a3v4si$($receiver_3.hdrToLdrNode_r20yfm$(mat.outColor).outColor, void 0, $receiver_3.constFloat_mx4ult$(1.0));
    else
      Kotlin.noWhenBranchMatched();
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
      $receiver.geometry.generateTangents_mx4ult$();
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
  function makeTreeGroundGrid$lambda$lambda$lambda_4(this$) {
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
      var closure$shadowMaps_0 = closure$shadowMaps;
      $receiver_0.albedoSource = Albedo.TEXTURE_ALBEDO;
      $receiver_0.isNormalMapped = true;
      $receiver_0.isAmbientOcclusionMapped = true;
      $receiver_0.isRoughnessMapped = true;
      $receiver_0.isDisplacementMapped = true;
      $receiver_0.maxLights = 2;
      $receiver_0.shadowMaps.addAll_brywnq$(closure$shadowMaps_0);
      var pbrCfg = $receiver_0;
      var $receiver_1 = new PbrShader(pbrCfg);
      $receiver_1.albedoMap = new Texture(void 0, void 0, makeTreeGroundGrid$lambda$lambda$lambda);
      $receiver_1.normalMap = new Texture(void 0, void 0, makeTreeGroundGrid$lambda$lambda$lambda_0);
      $receiver_1.roughnessMap = new Texture(void 0, void 0, makeTreeGroundGrid$lambda$lambda$lambda_1);
      $receiver_1.ambientOcclusionMap = new Texture(void 0, void 0, makeTreeGroundGrid$lambda$lambda$lambda_2);
      $receiver_1.displacementMap = new Texture(void 0, void 0, makeTreeGroundGrid$lambda$lambda$lambda_3);
      $receiver.onDispose.add_11rb$(makeTreeGroundGrid$lambda$lambda$lambda_4($receiver_1));
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
      random = math.defaultRandomInstance;
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
      $this.printer(level, tag, 'Generation done, took ' + i.v + ' iterations, ' + this.treeNodes_0.size + ' nodes in' + ' ' + toString_0(now() - t, 3) + ' ms');
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
      $receiver.texV = baseV + $receiver.distance_czzhiu$(ensureNotNull($receiver.parent)) / ($receiver.radius * 2.0 * math_0.PI * 1.5);
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
      random = math.defaultRandomInstance;
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
        var a = i / n * math_0.PI;
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
      var a = i / this.borders_0.size * 2.0 * math_0.PI;
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
      var $receiver = Math_0.atan2(y, x_0) / (2.0 * math_0.PI) + 0.5;
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
    return function ($receiver, f, ctx) {
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
    $receiver.onUpdate.add_11rb$(uiDemoScene$lambda$lambda$lambda($receiver));
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
    $receiver.unaryPlus_uv0sim$(orbitInputTransform($receiver, void 0, uiDemoScene$lambda$lambda($receiver)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, uiDemoScene$lambda$lambda_0));
    $receiver.unaryPlus_uv0sim$(embeddedUi($receiver, 10.0, 10.0, dps(400.0), void 0, uiDemoScene$lambda$lambda_1));
    return $receiver;
  }
  function main() {
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
  var package$de = _.de || (_.de = {});
  var package$fabmax = package$de.fabmax || (package$de.fabmax = {});
  var package$kool = package$fabmax.kool || (package$fabmax.kool = {});
  var package$demo = package$kool.demo || (package$kool.demo = {});
  package$demo.aoDemo_aemszp$ = aoDemo;
  $$importsForInline$$.kool = $module$kool;
  Object.defineProperty(AoDemo, 'Companion', {
    get: AoDemo$Companion_getInstance
  });
  package$demo.AoDemo = AoDemo;
  package$demo.deferredScene_aemszp$ = deferredScene;
  Object.defineProperty(DeferredDemo, 'Companion', {
    get: DeferredDemo$Companion_getInstance
  });
  package$demo.DeferredDemo = DeferredDemo;
  $$importsForInline$$.kooldemo = _;
  package$demo.demo_tisexc$ = demo;
  Object.defineProperty(Demo, 'Companion', {
    get: Demo$Companion_getInstance
  });
  package$demo.Demo = Demo;
  package$demo.Cycler_init_i5x0yv$ = Cycler_init;
  package$demo.Cycler = Cycler;
  package$demo.gltfDemo_aemszp$ = gltfDemo;
  package$demo.GltfDemo = GltfDemo;
  package$demo.helloWorldScene = helloWorldScene;
  package$demo.instanceDemo_aemszp$ = instanceDemo;
  package$demo.InstanceDemo = InstanceDemo;
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
  package$demo.simplificationDemo_aemszp$ = simplificationDemo;
  package$demo.SimplificationDemo = SimplificationDemo;
  package$demo.treeScene_aemszp$ = treeScene;
  package$demo.TreeGenerator = TreeGenerator;
  package$demo.TreeTopPointDistribution = TreeTopPointDistribution;
  package$demo.uiDemoScene = uiDemoScene;
  _.main = main;
  _.getParams = getParams;
  main();
  Kotlin.defineModule('kooldemo', _);
  return _;
});
