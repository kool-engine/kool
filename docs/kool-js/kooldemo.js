define(['exports', 'kotlin', 'kool'], function (_, Kotlin, $module$kool) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Unit = Kotlin.kotlin.Unit;
  var sphericalInputTransform = $module$kool.de.fabmax.kool.scene.sphericalInputTransform_6sxffc$;
  var CollisionWorld = $module$kool.de.fabmax.kool.physics.CollisionWorld;
  var staticBox = $module$kool.de.fabmax.kool.physics.staticBox_y2kzbl$;
  var BoxMesh = $module$kool.de.fabmax.kool.physics.BoxMesh;
  var uniformMassBox = $module$kool.de.fabmax.kool.physics.uniformMassBox_7b5o5w$;
  var CascadedShadowMap = $module$kool.de.fabmax.kool.util.CascadedShadowMap;
  var ensureNotNull = Kotlin.ensureNotNull;
  var Color = $module$kool.de.fabmax.kool.util.Color;
  var Vec3f = $module$kool.de.fabmax.kool.math.Vec3f;
  var LightModel = $module$kool.de.fabmax.kool.shading.LightModel;
  var ColorModel = $module$kool.de.fabmax.kool.shading.ColorModel;
  var basicShader = $module$kool.de.fabmax.kool.shading.basicShader_n50u2h$;
  var lineMesh = $module$kool.de.fabmax.kool.util.lineMesh_6a24eg$;
  var UiTheme = $module$kool.de.fabmax.kool.scene.ui.UiTheme;
  var BlankComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlankComponentUi;
  var theme = $module$kool.de.fabmax.kool.scene.ui.theme_vvurn$;
  var zero = $module$kool.de.fabmax.kool.scene.ui.zero;
  var dps = $module$kool.de.fabmax.kool.scene.ui.dps_8ca0d4$;
  var SimpleComponentUi = $module$kool.de.fabmax.kool.scene.ui.SimpleComponentUi;
  var numberToInt = Kotlin.numberToInt;
  var plus = Kotlin.kotlin.collections.plus_qloxvw$;
  var formatFloat = $module$kool.de.fabmax.kool.formatFloat_vjorfl$;
  var Alignment = $module$kool.de.fabmax.kool.scene.ui.Alignment;
  var Gravity = $module$kool.de.fabmax.kool.scene.ui.Gravity;
  var uiScene = $module$kool.de.fabmax.kool.scene.ui.uiScene_7c31we$;
  var MutableVec3f = $module$kool.de.fabmax.kool.math.MutableVec3f;
  var MutableVec2f = $module$kool.de.fabmax.kool.math.MutableVec2f;
  var Vec2f = $module$kool.de.fabmax.kool.math.Vec2f;
  var mutableListOf = Kotlin.kotlin.collections.mutableListOf_i5x0yv$;
  var last = Kotlin.kotlin.collections.last_2p1efm$;
  var MutableVec2f_init = $module$kool.de.fabmax.kool.math.MutableVec2f_init_czzhjp$;
  var Group = $module$kool.de.fabmax.kool.scene.Group;
  var Random = $module$kool.de.fabmax.kool.math.Random;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var BlurredComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlurredComponentUi;
  var getCallableRef = Kotlin.getCallableRef;
  var pcs = $module$kool.de.fabmax.kool.scene.ui.pcs_8ca0d4$;
  var debugOverlay = $module$kool.de.fabmax.kool.util.debugOverlay_n8mrtu$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mutableMapOf = Kotlin.kotlin.collections.mutableMapOf_qfcya0$;
  var PerspectiveCamera = $module$kool.de.fabmax.kool.scene.PerspectiveCamera;
  var math = Kotlin.kotlin.math;
  var TransformGroupDp = $module$kool.de.fabmax.kool.scene.doubleprec.TransformGroupDp;
  var round = Kotlin.kotlin.math.round_14dthe$;
  var abs = Kotlin.kotlin.math.abs_za3lpa$;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var MutableVec3f_init = $module$kool.de.fabmax.kool.math.MutableVec3f_init;
  var SphericalInputTransform$DragMethod = $module$kool.de.fabmax.kool.scene.SphericalInputTransform.DragMethod;
  var SphericalInputTransform$ZoomMethod = $module$kool.de.fabmax.kool.scene.SphericalInputTransform.ZoomMethod;
  var DoublePrecisionRoot = $module$kool.de.fabmax.kool.scene.doubleprec.DoublePrecisionRoot;
  var throwUPAE = Kotlin.throwUPAE;
  var Font = $module$kool.de.fabmax.kool.util.Font;
  var FontProps = $module$kool.de.fabmax.kool.util.FontProps;
  var Margin = $module$kool.de.fabmax.kool.scene.ui.Margin;
  var color = $module$kool.de.fabmax.kool.util.color_61zpoe$;
  var formatDouble = $module$kool.de.fabmax.kool.formatDouble_12fank$;
  var Vec3d = $module$kool.de.fabmax.kool.math.Vec3d;
  var MutableVec2f_init_0 = $module$kool.de.fabmax.kool.math.MutableVec2f_init;
  var Mat4d = $module$kool.de.fabmax.kool.math.Mat4d;
  var Ray = $module$kool.de.fabmax.kool.math.Ray;
  var MutableVec3d_init = $module$kool.de.fabmax.kool.math.MutableVec3d_init;
  var InputManager$DragHandler = $module$kool.de.fabmax.kool.InputManager.DragHandler;
  var Mesh = $module$kool.de.fabmax.kool.scene.Mesh;
  var Attribute = $module$kool.de.fabmax.kool.shading.Attribute;
  var MeshData_init = $module$kool.de.fabmax.kool.scene.MeshData_init_j0mu7e$;
  var MutableVec3f_init_0 = $module$kool.de.fabmax.kool.math.MutableVec3f_init_czzhiu$;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var equals = Kotlin.equals;
  var hashCode = Kotlin.hashCode;
  var get_indices = Kotlin.kotlin.collections.get_indices_gzk92b$;
  var randomI = $module$kool.de.fabmax.kool.math.randomI_n8acyv$;
  var assetTexture = $module$kool.de.fabmax.kool.assetTexture_2gt2x8$;
  var TextureProps_init = $module$kool.de.fabmax.kool.TextureProps_init_3m52m6$;
  var assetTexture_0 = $module$kool.de.fabmax.kool.assetTexture_513zl8$;
  var textureMesh = $module$kool.de.fabmax.kool.scene.textureMesh_pyaqjj$;
  var group = $module$kool.de.fabmax.kool.scene.group_2ylazs$;
  var TransformGroup = $module$kool.de.fabmax.kool.scene.TransformGroup;
  var loadMesh = $module$kool.de.fabmax.kool.util.serialization.loadMesh_fqrh44$;
  var Armature = $module$kool.de.fabmax.kool.scene.animation.Armature;
  var Label = $module$kool.de.fabmax.kool.scene.ui.Label;
  var uns = $module$kool.de.fabmax.kool.scene.ui.uns_8ca0d4$;
  var Slider = $module$kool.de.fabmax.kool.scene.ui.Slider;
  var embeddedUi = $module$kool.de.fabmax.kool.scene.ui.embeddedUi_o1x1d9$;
  var transformGroup = $module$kool.de.fabmax.kool.scene.transformGroup_zaezuq$;
  var KoolContext$Viewport = $module$kool.de.fabmax.kool.KoolContext.Viewport;
  var InRadiusTraverser_init = $module$kool.de.fabmax.kool.math.InRadiusTraverser_init_h816bs$;
  var BillboardMesh = $module$kool.de.fabmax.kool.util.BillboardMesh;
  var randomF = $module$kool.de.fabmax.kool.math.randomF_dleff0$;
  var PerfTimer = $module$kool.de.fabmax.kool.util.PerfTimer;
  var BoundingBox_init = $module$kool.de.fabmax.kool.util.BoundingBox_init_4lfkt4$;
  var CubicPointDistribution = $module$kool.de.fabmax.kool.math.CubicPointDistribution;
  var pointMesh = $module$kool.de.fabmax.kool.util.pointMesh_h6khem$;
  var pointTree = $module$kool.de.fabmax.kool.math.pointTree_ffk80x$;
  var Pair = Kotlin.kotlin.Pair;
  var Vec3f_init = $module$kool.de.fabmax.kool.math.Vec3f_init_mx4ult$;
  var InterpolatedFloat = $module$kool.de.fabmax.kool.util.InterpolatedFloat;
  var CosAnimator = $module$kool.de.fabmax.kool.util.CosAnimator;
  var Animator = $module$kool.de.fabmax.kool.util.Animator;
  var BasicShader = $module$kool.de.fabmax.kool.shading.BasicShader;
  var throwCCE = Kotlin.throwCCE;
  var LinearAnimator = $module$kool.de.fabmax.kool.util.LinearAnimator;
  var colorMesh = $module$kool.de.fabmax.kool.scene.colorMesh_gp9ews$;
  var textMesh = $module$kool.de.fabmax.kool.scene.textMesh_8mgi8m$;
  var reversed = Kotlin.kotlin.ranges.reversed_zf1xzc$;
  var UiContainer = $module$kool.de.fabmax.kool.scene.ui.UiContainer;
  var SampleNode = $module$kool.de.fabmax.kool.audio.SampleNode;
  var Wave = $module$kool.de.fabmax.kool.audio.Wave;
  var Oscillator = $module$kool.de.fabmax.kool.audio.Oscillator;
  var MoodFilter = $module$kool.de.fabmax.kool.audio.MoodFilter;
  var Button = $module$kool.de.fabmax.kool.scene.ui.Button;
  var MutableColor_init = $module$kool.de.fabmax.kool.util.MutableColor_init_d7aj7k$;
  var InterpolatedColor = $module$kool.de.fabmax.kool.util.InterpolatedColor;
  var MutableColor_init_0 = $module$kool.de.fabmax.kool.util.MutableColor_init;
  var Scene = $module$kool.de.fabmax.kool.scene.Scene;
  var ColorGradient = $module$kool.de.fabmax.kool.util.ColorGradient;
  var LineMesh = $module$kool.de.fabmax.kool.util.LineMesh;
  var Shaker = $module$kool.de.fabmax.kool.audio.Shaker;
  var Kick = $module$kool.de.fabmax.kool.audio.Kick;
  var Pad = $module$kool.de.fabmax.kool.audio.Pad;
  var AudioGenerator = $module$kool.de.fabmax.kool.audio.AudioGenerator;
  var TextureProps_init_0 = $module$kool.de.fabmax.kool.TextureProps_init_wfrsr4$;
  var CullMethod = $module$kool.de.fabmax.kool.scene.CullMethod;
  var MeshBuilder = $module$kool.de.fabmax.kool.util.MeshBuilder;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var Vec3f_init_0 = $module$kool.de.fabmax.kool.math.Vec3f_init_czzhiu$;
  var InRadiusTraverser = $module$kool.de.fabmax.kool.math.InRadiusTraverser;
  var math_0 = $module$kool.de.fabmax.kool.math;
  var PointDistribution = $module$kool.de.fabmax.kool.math.PointDistribution;
  var BSplineVec2f = $module$kool.de.fabmax.kool.math.BSplineVec2f;
  var ToggleButton = $module$kool.de.fabmax.kool.scene.ui.ToggleButton;
  var TextField = $module$kool.de.fabmax.kool.scene.ui.TextField;
  var createContext = $module$kool.de.fabmax.kool.createContext;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  BoxWorld.prototype = Object.create(Group.prototype);
  BoxWorld.prototype.constructor = BoxWorld;
  Globe.prototype = Object.create(TransformGroupDp.prototype);
  Globe.prototype.constructor = Globe;
  TileFrame.prototype = Object.create(TransformGroupDp.prototype);
  TileFrame.prototype.constructor = TileFrame;
  TileMesh.prototype = Object.create(Mesh.prototype);
  TileMesh.prototype.constructor = TileMesh;
  OsmTexImageTileShaderProvider.prototype = Object.create(TexImageTileShaderProvider.prototype);
  OsmTexImageTileShaderProvider.prototype.constructor = OsmTexImageTileShaderProvider;
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
  SynthieScene.prototype = Object.create(Scene.prototype);
  SynthieScene.prototype.constructor = SynthieScene;
  TreeGenerator$AttractionPoint.prototype = Object.create(MutableVec3f.prototype);
  TreeGenerator$AttractionPoint.prototype.constructor = TreeGenerator$AttractionPoint;
  TreeGenerator$TreeNode.prototype = Object.create(MutableVec3f.prototype);
  TreeGenerator$TreeNode.prototype.constructor = TreeGenerator$TreeNode;
  TreeTopPointDistribution.prototype = Object.create(PointDistribution.prototype);
  TreeTopPointDistribution.prototype.constructor = TreeTopPointDistribution;
  function basicCollisionDemo$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.maxZoom = 50.0;
      $receiver.resetZoom_mx4ult$(4.0);
      $receiver.setMouseRotation_dleff0$(45.0, -30.0);
      return Unit;
    };
  }
  function basicCollisionDemo$lambda$lambda_0(closure$world) {
    return function ($receiver, ctx) {
      closure$world.stepSimulation_mx4ult$(ctx.deltaT);
      return Unit;
    };
  }
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  function basicCollisionDemo(ctx) {
    var scenes = ArrayList_init();
    var ARRAY_SIZE_Y = 2;
    var ARRAY_SIZE_X = 1;
    var ARRAY_SIZE_Z = 1;
    var $receiver = new Scene(null);
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, basicCollisionDemo$lambda$lambda($receiver)));
    var world = new CollisionWorld();
    world.gravity.set_y2kzbl$(0.0, -10.0, 0.0);
    $receiver.onPreRender.add_11rb$(basicCollisionDemo$lambda$lambda_0(world));
    var tmp$ = world.bodies;
    var $receiver_0 = staticBox(100.0, 100.0, 100.0);
    $receiver_0.centerOfMass.set_y2kzbl$(0.0, -50.0, 0.0);
    $receiver.plusAssign_f1kmr1$(new BoxMesh($receiver_0));
    tmp$.add_11rb$($receiver_0);
    var c = {v: 0};
    for (var k = 0; k < ARRAY_SIZE_Y; k++) {
      for (var i = 0; i < ARRAY_SIZE_X; i++) {
        for (var j = 0; j < ARRAY_SIZE_Z; j++) {
          var tmp$_0 = world.bodies;
          var $receiver_1 = uniformMassBox(0.2, 0.2, 0.2, 1.0);
          $receiver_1.centerOfMass.set_y2kzbl$(0.2 * i, 2 + 0.2 * k, 0.2 * j);
          var tmp$_1;
          $receiver.plusAssign_f1kmr1$(new BoxMesh($receiver_1, BOX_COLORS.get_za3lpa$((tmp$_1 = c.v, c.v = tmp$_1 + 1 | 0, tmp$_1) % BOX_COLORS.size)));
          tmp$_0.add_11rb$($receiver_1);
        }
      }
    }
    scenes.add_11rb$($receiver);
    return scenes;
  }
  function collisionDemo$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.maxZoom = 50.0;
      $receiver.resetZoom_mx4ult$(50.0);
      $receiver.setMouseRotation_dleff0$(45.0, -30.0);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.lightModel = LightModel.NO_LIGHTING;
      $receiver.colorModel = ColorModel.VERTEX_COLOR;
      $receiver.shadowMap = this$.defaultShadowMap;
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda_0(this$) {
    return function ($receiver) {
      var sz = 25;
      var y = 0.005;
      for (var i = -sz | 0; i <= sz; i++) {
        var color = Color.Companion.MD_GREY_600.withAlpha_mx4ult$(0.5);
        $receiver.addLine_b8opkg$(new Vec3f(i, y, -sz), color, new Vec3f(i, y, sz), color);
        $receiver.addLine_b8opkg$(new Vec3f(-sz, y, i), color, new Vec3f(sz, y, i), color);
      }
      $receiver.isCastingShadow = false;
      $receiver.shader = basicShader(collisionDemo$lambda$lambda$lambda(this$));
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda_0(it) {
    return new BlankComponentUi();
  }
  function collisionDemo$lambda$lambda$lambda_1(it) {
    return new BlankComponentUi();
  }
  function collisionDemo$lambda$lambda_1($receiver) {
    $receiver.componentUi_mloaa0$(collisionDemo$lambda$lambda$lambda_0);
    $receiver.containerUi_2t3ptw$(collisionDemo$lambda$lambda$lambda_1);
    return Unit;
  }
  function collisionDemo$lambda$disableCamDrag$lambda(closure$boxScene) {
    return function ($receiver, f, f_0, f_1) {
      closure$boxScene.isPickingEnabled = false;
      return Unit;
    };
  }
  function collisionDemo$lambda$disableCamDrag$lambda_0(closure$boxScene) {
    return function ($receiver, f, f_0, f_1) {
      closure$boxScene.isPickingEnabled = true;
      return Unit;
    };
  }
  function collisionDemo$lambda$disableCamDrag(closure$boxScene) {
    return function ($receiver) {
      var $receiver_0 = $receiver.onHoverEnter;
      var element = collisionDemo$lambda$disableCamDrag$lambda(closure$boxScene);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = $receiver.onHoverExit;
      var element_0 = collisionDemo$lambda$disableCamDrag$lambda_0(closure$boxScene);
      $receiver_1.add_11rb$(element_0);
    };
  }
  function collisionDemo$lambda$lambda$lambda_2($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(75.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(80.0, true), dps(35.0, true), zero());
    return Unit;
  }
  function collisionDemo$lambda$lambda$lambda_3(closure$boxCnt) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(210.0, true), dps(75.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(50.0, true), dps(35.0, true), zero());
      $receiver.text = closure$boxCnt.v.toString();
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda$lambda(closure$boxCnt, closure$boxCntLbl) {
    return function ($receiver, value) {
      closure$boxCnt.v = numberToInt(value);
      closure$boxCntLbl.text = closure$boxCnt.v.toString();
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda_4(closure$disableCamDrag, closure$boxCnt, closure$boxCntLbl) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(80.0, true), dps(75.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(150.0, true), dps(35.0, true), zero());
      closure$disableCamDrag($receiver);
      $receiver.onValueChanged = plus($receiver.onValueChanged, collisionDemo$lambda$lambda$lambda$lambda(closure$boxCnt, closure$boxCntLbl));
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda_5($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(40.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(80.0, true), dps(35.0, true), zero());
    return Unit;
  }
  function collisionDemo$lambda$lambda$lambda_6(closure$boxWorld) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(210.0, true), dps(40.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(50.0, true), dps(35.0, true), zero());
      $receiver.text = formatFloat(ensureNotNull(closure$boxWorld.v).world.gravity.length(), 2);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda$lambda_0(closure$boxWorld, closure$gravityLbl) {
    return function ($receiver, value) {
      var grav = ensureNotNull(closure$boxWorld.v).world.gravity;
      grav.set_y2kzbl$(0.0, -value, 0.0);
      closure$gravityLbl.text = formatFloat(grav.length(), 2);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda_7(closure$disableCamDrag, closure$boxWorld, closure$gravityLbl) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(80.0, true), dps(40.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(150.0, true), dps(35.0, true), zero());
      closure$disableCamDrag($receiver);
      $receiver.onValueChanged = plus($receiver.onValueChanged, collisionDemo$lambda$lambda$lambda$lambda_0(closure$boxWorld, closure$gravityLbl));
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda$lambda_1(closure$boxWorld, closure$boxCnt) {
    return function ($receiver, f, f_0, ctx) {
      ensureNotNull(closure$boxWorld.v).clearBoxes_aemszp$(ctx);
      ensureNotNull(closure$boxWorld.v).createBoxes_za3lpa$(closure$boxCnt.v);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda_8(closure$boxWorld, closure$boxCnt) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(5.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(150.0, true), dps(35.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = collisionDemo$lambda$lambda$lambda$lambda_1(closure$boxWorld, closure$boxCnt);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda_2(this$, closure$boxCnt, closure$disableCamDrag, closure$boxWorld) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(280.0), dps(115.0), zero());
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Boxes:', collisionDemo$lambda$lambda$lambda_2));
      var boxCntLbl = this$.label_tokfmu$('boxCntLbl', collisionDemo$lambda$lambda$lambda_3(closure$boxCnt));
      $receiver.unaryPlus_uv0sim$(boxCntLbl);
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('boxCnt', 1.0, 200.0, 40.0, collisionDemo$lambda$lambda$lambda_4(closure$disableCamDrag, closure$boxCnt, boxCntLbl)));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Gravity:', collisionDemo$lambda$lambda$lambda_5));
      var gravityLbl = this$.label_tokfmu$('gravityLbl', collisionDemo$lambda$lambda$lambda_6(closure$boxWorld));
      $receiver.unaryPlus_uv0sim$(gravityLbl);
      $receiver.unaryPlus_uv0sim$(this$.slider_91a1dk$('gravity', 0.0, 10.0, ensureNotNull(closure$boxWorld.v).world.gravity.length(), collisionDemo$lambda$lambda$lambda_7(closure$disableCamDrag, closure$boxWorld, gravityLbl)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('Reset Boxes!', collisionDemo$lambda$lambda$lambda_8(closure$boxWorld, closure$boxCnt)));
      return Unit;
    };
  }
  function collisionDemo$lambda(closure$boxScene, closure$boxCnt, closure$boxWorld) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK_SIMPLE, collisionDemo$lambda$lambda_1);
      var disableCamDrag = collisionDemo$lambda$disableCamDrag(closure$boxScene);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu', collisionDemo$lambda$lambda_2($receiver, closure$boxCnt, disableCamDrag, closure$boxWorld)));
      return Unit;
    };
  }
  function collisionDemo(ctx) {
    var boxWorld = {v: null};
    var scenes = ArrayList_init();
    var boxCnt = {v: 40};
    var $receiver = new Scene(null);
    $receiver.light.direction.set_y2kzbl$(1.0, 0.8, 0.4);
    $receiver.defaultShadowMap = CascadedShadowMap.Companion.defaultCascadedShadowMap3();
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, collisionDemo$lambda$lambda($receiver)));
    boxWorld.v = new BoxWorld($receiver.defaultShadowMap);
    ensureNotNull(boxWorld.v).createBoxes_za3lpa$(boxCnt.v);
    $receiver.unaryPlus_uv0sim$(ensureNotNull(boxWorld.v));
    $receiver.unaryPlus_uv0sim$(lineMesh(void 0, collisionDemo$lambda$lambda_0($receiver)));
    var boxScene = $receiver;
    scenes.add_11rb$(boxScene);
    var element = uiScene(ctx.screenDpi, void 0, collisionDemo$lambda(boxScene, boxCnt, boxWorld));
    scenes.add_11rb$(element);
    return scenes;
  }
  function BoxWorld(shadowMap) {
    Group.call(this);
    this.shadowMap_0 = shadowMap;
    this.world = new CollisionWorld();
    this.rand_0 = new Random(20);
    this.world.gravity.set_y2kzbl$(0.0, -2.5, 0.0);
    this.onPreRender.add_11rb$(BoxWorld_init$lambda(this));
  }
  BoxWorld.prototype.clearBoxes_aemszp$ = function (ctx) {
    var tmp$;
    tmp$ = this.children.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.dispose_aemszp$(ctx);
    }
    this.children.clear();
    this.world.bodies.clear();
  };
  BoxWorld.prototype.createBoxes_za3lpa$ = function (n) {
    var tmp$;
    var stacks = (n / 50 | 0) + 1 | 0;
    var centers = this.makeCenters_0(stacks);
    tmp$ = n - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var x = this.rand_0.randomF_dleff0$(1.0, 2.0);
      var y = this.rand_0.randomF_dleff0$(1.0, 2.0);
      var z = this.rand_0.randomF_dleff0$(1.0, 2.0);
      var $receiver = uniformMassBox(x, y, z, x * y * z);
      $receiver.centerOfMass.x = centers.get_za3lpa$(i % centers.size).x + this.rand_0.randomF_dleff0$(-0.5, 0.5);
      $receiver.centerOfMass.z = centers.get_za3lpa$(i % centers.size).y + this.rand_0.randomF_dleff0$(-0.5, 0.5);
      $receiver.centerOfMass.y = ((n - i | 0) / stacks | 0) * 3.0 + 3;
      $receiver.worldTransform.rotate_ad55pp$(this.rand_0.randomF_dleff0$(0.0, 360.0), (new MutableVec3f(this.rand_0.randomF_dleff0$(-1.0, 1.0), this.rand_0.randomF_dleff0$(-1.0, 1.0), this.rand_0.randomF_dleff0$(-1.0, 1.0))).norm());
      var box = $receiver;
      this.world.bodies.add_11rb$(box);
      this.plusAssign_f1kmr1$(new BoxMesh(box, BOX_COLORS.get_za3lpa$(i % BOX_COLORS.size), this.shadowMap_0));
    }
    this.createGround_0();
  };
  BoxWorld.prototype.makeCenters_0 = function (stacks) {
    var tmp$, tmp$_0;
    var dir = new MutableVec2f(4.0, 0.0);
    var centers = mutableListOf([new Vec2f(0.0, 0.0)]);
    var j = 0;
    var steps = 1;
    var stepsSteps = 1;
    while (j < (stacks - 1 | 0)) {
      tmp$ = steps;
      for (var i = 1; i <= tmp$; i++) {
        var element = MutableVec2f_init(last(centers)).add_czzhjp$(dir);
        centers.add_11rb$(element);
        j = j + 1 | 0;
      }
      dir.rotate_mx4ult$(90.0);
      if ((tmp$_0 = stepsSteps, stepsSteps = tmp$_0 + 1 | 0, tmp$_0) === 2) {
        stepsSteps = 1;
        steps = steps + 1 | 0;
      }
    }
    return centers;
  };
  BoxWorld.prototype.createGround_0 = function () {
    var $receiver = staticBox(50.0, 1.0, 50.0);
    $receiver.centerOfMass.set_y2kzbl$(0.0, -0.5, 0.0);
    var groundBox = $receiver;
    this.world.bodies.add_11rb$(groundBox);
    this.plusAssign_f1kmr1$(new BoxMesh(groundBox, Color.Companion.MD_GREY, this.shadowMap_0));
    var $receiver_0 = staticBox(1.0, 4.0, 50.0);
    $receiver_0.centerOfMass.set_y2kzbl$(-25.5, 2.0, 0.0);
    var borderLt = $receiver_0;
    this.world.bodies.add_11rb$(borderLt);
    this.plusAssign_f1kmr1$(new BoxMesh(borderLt, Color.Companion.MD_ORANGE, this.shadowMap_0));
    var $receiver_1 = staticBox(1.0, 4.0, 50.0);
    $receiver_1.centerOfMass.set_y2kzbl$(25.5, 2.0, 0.0);
    var borderRt = $receiver_1;
    this.world.bodies.add_11rb$(borderRt);
    this.plusAssign_f1kmr1$(new BoxMesh(borderRt, Color.Companion.MD_ORANGE, this.shadowMap_0));
    var $receiver_2 = staticBox(52.0, 4.0, 1.0);
    $receiver_2.centerOfMass.set_y2kzbl$(0.0, 2.0, -25.5);
    var borderBk = $receiver_2;
    this.world.bodies.add_11rb$(borderBk);
    this.plusAssign_f1kmr1$(new BoxMesh(borderBk, Color.Companion.MD_ORANGE, this.shadowMap_0));
    var $receiver_3 = staticBox(52.0, 4.0, 1.0);
    $receiver_3.centerOfMass.set_y2kzbl$(0.0, 2.0, 25.5);
    var borderFt = $receiver_3;
    this.world.bodies.add_11rb$(borderFt);
    this.plusAssign_f1kmr1$(new BoxMesh(borderFt, Color.Companion.MD_ORANGE, this.shadowMap_0));
  };
  function BoxWorld_init$lambda(this$BoxWorld) {
    return function ($receiver, ctx) {
      this$BoxWorld.world.stepSimulation_mx4ult$(ctx.deltaT);
      return Unit;
    };
  }
  BoxWorld.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BoxWorld',
    interfaces: [Group]
  };
  var BOX_COLORS;
  var Map = Kotlin.kotlin.collections.Map;
  function Demo(ctx, startScene) {
    if (startScene === void 0)
      startScene = null;
    this.dbgOverlay_0 = debugOverlay(ctx, true);
    this.newScenes_0 = ArrayList_init();
    this.currentScenes_0 = ArrayList_init();
    this.defaultScene_0 = new Demo$DemoEntry('Simple Demo', Demo$defaultScene$lambda);
    this.demos_0 = mutableMapOf([to('simpleDemo', this.defaultScene_0), to('multiDemo', new Demo$DemoEntry('Split Viewport Demo', Demo$demos$lambda)), to('pointDemo', new Demo$DemoEntry('Point Cloud Demo', Demo$demos$lambda_0)), to('synthieDemo', new Demo$DemoEntry('Synthie Demo', Demo$demos$lambda_1)), to('globeDemo', new Demo$DemoEntry('Globe Demo', Demo$demos$lambda_2)), to('modelDemo', new Demo$DemoEntry('Model Demo', Demo$demos$lambda_3)), to('treeDemo', new Demo$DemoEntry('Tree Demo', Demo$demos$lambda_4)), to('boxDemo', new Demo$DemoEntry('Physics Demo', Demo$demos$lambda_5))]);
    var tmp$;
    var $receiver = ctx.scenes;
    var element = this.demoOverlay_0(ctx);
    $receiver.add_11rb$(element);
    var $receiver_0 = ctx.scenes;
    var element_0 = this.dbgOverlay_0;
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
        ctx.scenes.add_wxm5ur$((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0), item);
        this.currentScenes_0.add_11rb$(item);
      }
      this.newScenes_0.clear();
    }
  };
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
  function Demo$demoOverlay$lambda$lambda$lambda$lambda(closure$demo, this$Demo, closure$ctx, this$) {
    return function ($receiver, f, f_0, f_1) {
      closure$demo.value.loadScene(this$Demo.newScenes_0, closure$ctx);
      this$.isOpen = false;
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda_0(closure$y, closure$demo, this$Demo, closure$ctx, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$y.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0, true), dps(30.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      $receiver.text = closure$demo.value.label;
      closure$y.v -= 35.0;
      var $receiver_0 = $receiver.onClick;
      var element = Demo$demoOverlay$lambda$lambda$lambda$lambda(closure$demo, this$Demo, closure$ctx, this$);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda$lambda_0(this$, this$Demo) {
    return function ($receiver, f, f_0, f_1) {
      this$Demo.dbgOverlay_0.isVisible = this$.isEnabled;
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda_1(this$Demo) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(10.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0, true), dps(30.0, true), zero());
      $receiver.text = 'Debug Info';
      $receiver.isEnabled = this$Demo.dbgOverlay_0.isVisible;
      var $receiver_0 = $receiver.onClick;
      var element = Demo$demoOverlay$lambda$lambda$lambda$lambda_0($receiver, this$Demo);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda_0(this$Demo, closure$ctx, this$) {
    return function ($receiver) {
      var tmp$;
      var y = {v: -105.0};
      tmp$ = this$Demo.demos_0.entries.iterator();
      while (tmp$.hasNext()) {
        var demo = tmp$.next();
        $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$(demo.key, Demo$demoOverlay$lambda$lambda$lambda_0(y, demo, this$Demo, closure$ctx, $receiver)));
      }
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
    return uiScene(ctx.screenDpi, void 0, Demo$demoOverlay$lambda(this, ctx));
  };
  function Demo$DemoEntry(label, loadScene) {
    this.label = label;
    this.loadScene = loadScene;
  }
  Demo$DemoEntry.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DemoEntry',
    interfaces: []
  };
  function Demo$defaultScene$lambda($receiver, it) {
    $receiver.add_11rb$(simpleShapesScene(it));
    return Unit;
  }
  function Demo$demos$lambda($receiver, it) {
    $receiver.addAll_brywnq$(multiScene(it));
    return Unit;
  }
  function Demo$demos$lambda_0($receiver, it) {
    $receiver.add_11rb$(pointScene());
    return Unit;
  }
  function Demo$demos$lambda_1($receiver, it) {
    $receiver.addAll_brywnq$(synthieScene(it));
    return Unit;
  }
  function Demo$demos$lambda_2($receiver, it) {
    $receiver.addAll_brywnq$(globeScene(it));
    return Unit;
  }
  function Demo$demos$lambda_3($receiver, it) {
    $receiver.add_11rb$(modelScene(it));
    return Unit;
  }
  function Demo$demos$lambda_4($receiver, it) {
    $receiver.addAll_brywnq$(treeScene(it));
    return Unit;
  }
  function Demo$demos$lambda_5($receiver, it) {
    $receiver.addAll_brywnq$(collisionDemo(it));
    return Unit;
  }
  Demo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Demo',
    interfaces: []
  };
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  function Globe(radius, name) {
    Globe$Companion_getInstance();
    if (name === void 0)
      name = null;
    TransformGroupDp.call(this, name);
    this.radius = radius;
    this.meterPerPxLvl0 = 156000.0;
    this.maxTiles = 300;
    this.minZoomLvl = 3;
    this.maxZoomLvl = 19;
    this.frameZoomLvl = 11;
    this.frameZoomThresh = 14;
    this.centerLat_p7ui4s$_0 = 0.0;
    this.centerLon_p7u7w8$_0 = 0.0;
    this.cameraHeight_brtjyi$_0 = 0.0;
    this.meshGenerator_0 = new FlatTileMeshGenerator();
    this.tileShaderProvider = new OsmTexImageTileShaderProvider();
    this.tileFrames_0 = LinkedHashMap_init();
    this.zoomGroups_0 = ArrayList_init();
    this.tiles_0 = LinkedHashMap_init();
    this.loadingTiles_0 = LinkedHashSet_init();
    this.removableTiles_0 = LinkedHashMap_init();
    this.removeTiles_0 = ArrayList_init();
    this.camPosition_0 = MutableVec3f_init();
    this.camDirection_0 = MutableVec3f_init();
    this.center_0 = new TileName(0, 0, 1);
    this.prevCamHeight_0 = 0.0;
    this.prevLat_0 = 0.0;
    this.prevLon_0 = 0.0;
    this.tmpVec_0 = MutableVec3f_init();
    var tmp$, tmp$_0;
    this.translate_yvo9jy$(0.0, 0.0, -this.radius);
    tmp$ = this.minZoomLvl;
    tmp$_0 = this.frameZoomThresh;
    for (var i = tmp$; i <= tmp$_0; i++) {
      var grp = new Group();
      this.zoomGroups_0.add_11rb$(grp);
      this.unaryPlus_uv0sim$(grp);
    }
  }
  Object.defineProperty(Globe.prototype, 'centerLat', {
    get: function () {
      return this.centerLat_p7ui4s$_0;
    },
    set: function (centerLat) {
      this.centerLat_p7ui4s$_0 = centerLat;
    }
  });
  Object.defineProperty(Globe.prototype, 'centerLon', {
    get: function () {
      return this.centerLon_p7u7w8$_0;
    },
    set: function (centerLon) {
      this.centerLon_p7u7w8$_0 = centerLon;
    }
  });
  Object.defineProperty(Globe.prototype, 'cameraHeight', {
    get: function () {
      return this.cameraHeight_brtjyi$_0;
    },
    set: function (cameraHeight) {
      this.cameraHeight_brtjyi$_0 = cameraHeight;
    }
  });
  var Math_0 = Math;
  Globe.prototype.preRenderDp_oxz17o$ = function (ctx, modelMatDp) {
    var tmp$, tmp$_0;
    var cam = (tmp$ = this.scene) != null ? tmp$.camera : null;
    if (cam != null && Kotlin.isType(cam, PerspectiveCamera)) {
      this.toGlobalCoords_w1lst9$(this.tmpVec_0.set_czzhiu$(Vec3f.Companion.ZERO));
      this.tmpVec_0.subtract_czzhiu$(cam.globalPos);
      this.cameraHeight = this.tmpVec_0.length() - this.radius;
      var camDist = cam.globalPos.length();
      this.camPosition_0.set_czzhiu$(Vec3f.Companion.Z_AXIS).scale_mx4ult$(camDist);
      this.toLocalCoords_w1lst9$(this.camPosition_0);
      this.camPosition_0.norm_5s4mqq$(this.camDirection_0);
      cam.clipNear = camDist * 0.05;
      cam.clipFar = camDist * 10.0;
      if (camDist > this.prevCamHeight_0) {
        tmp$_0 = this.prevCamHeight_0 / camDist;
      }
       else {
        tmp$_0 = camDist / this.prevCamHeight_0;
      }
      var dh = tmp$_0;
      this.prevCamHeight_0 = camDist;
      var tmp$_1 = math.PI * 0.5;
      var x = this.camDirection_0.y;
      var $receiver = tmp$_1 - Math_0.acos(x);
      var min = -Globe$Companion_getInstance().RAD_85_0;
      var max = Globe$Companion_getInstance().RAD_85_0;
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
      var lat = clamp$result;
      var y = this.camDirection_0.x;
      var x_0 = this.camDirection_0.z;
      var lon = Math_0.atan2(y, x_0);
      var tmp$_2 = dh < 0.99;
      if (!tmp$_2) {
        var x_1 = lat - this.prevLat_0;
        tmp$_2 = Math_0.abs(x_1) > 1.0E-5;
      }
      var tmp$_3 = tmp$_2;
      if (!tmp$_3) {
        var x_2 = lon - this.prevLon_0;
        tmp$_3 = Math_0.abs(x_2) > 1.0E-5;
      }
      var isMoving = tmp$_3;
      this.prevLat_0 = lat;
      this.prevLon_0 = lon;
      this.centerLat = lat * math_0.RAD_2_DEG;
      this.centerLon = lon * math_0.RAD_2_DEG;
      this.camDirection_0.scale_mx4ult$(this.radius);
      var camHeight = this.camDirection_0.distance_czzhiu$(this.camPosition_0);
      var x_3 = cam.fovy * math_0.DEG_2_RAD * 0.5;
      var meterPerPx = camHeight * Math_0.tan(x_3) * 2.0 / (ctx.viewport.height * 96.0 / ctx.screenDpi);
      var centerZoom = this.getBestZoom_0(meterPerPx, lat);
      var newCenter = TileName$Companion_getInstance().forLatLon_syxxoe$(lat * math_0.RAD_2_DEG, lon * math_0.RAD_2_DEG, centerZoom);
      if (!(newCenter != null ? newCenter.equals(this.center_0) : null) && (this.tiles_0.size < this.maxTiles || !isMoving)) {
        this.center_0 = newCenter;
        this.rebuildMesh_0(ctx);
      }
    }
    TransformGroupDp.prototype.preRenderDp_oxz17o$.call(this, ctx, modelMatDp);
    if (!this.removeTiles_0.isEmpty()) {
      var tmp$_4;
      tmp$_4 = this.removeTiles_0.iterator();
      while (tmp$_4.hasNext()) {
        var element = tmp$_4.next();
        this.loadingTiles_0.remove_11rb$(element.key);
        this.tiles_0.remove_11rb$(element.key);
        this.removableTiles_0.remove_11rb$(element.key);
        this.deleteTile_0(element);
        element.dispose_aemszp$(ctx);
      }
      this.removeTiles_0.clear();
    }
  };
  Globe.prototype.renderDp_oxz17o$ = function (ctx, modelMatDp) {
    ctx.pushAttributes();
    ctx.depthFunc = 519;
    ctx.applyAttributes();
    TransformGroupDp.prototype.renderDp_oxz17o$.call(this, ctx, modelMatDp);
    ctx.popAttributes();
  };
  var util = $module$kool.de.fabmax.kool.util;
  var Log$Level = $module$kool.de.fabmax.kool.util.Log.Level;
  Globe.prototype.deleteTile_0 = function (tile) {
    if (tile.tileName.zoom >= this.frameZoomThresh) {
      var frame = this.getTileFrame_sdbw1w$(tile.tileName);
      frame.removeTile_sdcfxe$(tile);
      if (frame.tileCount === 0) {
        this.tileFrames_0.remove_11rb$(frame.tileName.fusedKey);
        this.minusAssign_v64n5s$(frame);
        var $this = util.Log;
        var level = Log$Level.DEBUG;
        var tag = Kotlin.getKClassFromExpression(this).simpleName;
        if (level.level >= $this.level.level) {
          $this.printer(level, tag, 'removed tile frame ' + frame.tileName + ', ' + this.tileFrames_0.size + ' frames remaining');
        }
      }
    }
     else {
      this.getZoomGroup_za3lpa$(tile.tileName.zoom).removeNode_f1kmr1$(tile);
    }
  };
  Globe.prototype.getBestZoom_0 = function (meterPerPx, lat) {
    var x = this.meterPerPxLvl0 / meterPerPx * Math_0.cos(lat);
    var $receiver = round(0.2 + Math_0.log2(x));
    var min = this.minZoomLvl;
    var max = this.maxZoomLvl;
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
    return numberToInt(clamp$result);
  };
  function Globe$rebuildMesh$lambda(this$Globe) {
    return function (m) {
      if (!m.isTexLoaded) {
        return -2147483648;
      }
       else {
        return -abs(m.tileName.zoom - this$Globe.center_0.zoom | 0) | 0;
      }
    };
  }
  var sortWith = Kotlin.kotlin.collections.sortWith_nqfjgj$;
  var wrapFunction = Kotlin.wrapFunction;
  var compareBy$lambda = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(a), selector(b));
      };
    };
  });
  var Comparator = Kotlin.kotlin.Comparator;
  function Comparator$ObjectLiteral(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  Globe.prototype.rebuildMesh_0 = function (ctx) {
    var tmp$;
    this.removableTiles_0.putAll_a2k3zr$(this.tiles_0);
    var rng = 5;
    var zoom = this.center_0.zoom;
    var xStart = this.center_0.x - rng + 1 & -2;
    var xEnd = (this.center_0.x + rng + 1 & -2) - 1 | 0;
    var yStart = this.center_0.y - rng + 1 & -2;
    var yEnd = (this.center_0.y + rng + 1 & -2) - 1 | 0;
    this.addMeshesWrappingX_0(xStart, xEnd, yStart, yEnd, zoom, ctx);
    for (var i = 1; i <= 4; i++) {
      zoom = zoom - 1 | 0;
      if (zoom >= this.minZoomLvl) {
        var xStShf = xStart >> 1;
        var xEdShf = xEnd + 1 >> 1;
        var yStShf = yStart >> 1;
        var yEdShf = yEnd + 1 >> 1;
        xStart = xStShf - 1 & -2;
        xEnd = (xEdShf & -2) + 1 | 0;
        yStart = yStShf - 1 & -2;
        yEnd = (yEdShf & -2) + 1 | 0;
        this.addMeshesWrappingX_0(xStart, xStShf - 1 | 0, yStart, yEnd, zoom, ctx);
        this.addMeshesWrappingX_0(xEdShf, xEnd, yStart, yEnd, zoom, ctx);
        this.addMeshesWrappingX_0(xStShf, xEdShf - 1 | 0, yStart, yStShf - 1 | 0, zoom, ctx);
        this.addMeshesWrappingX_0(xStShf, xEdShf - 1 | 0, yEdShf, yEnd, zoom, ctx);
      }
       else {
        break;
      }
    }
    var forceRemoveThresh = numberToInt(this.maxTiles * 1.3);
    if (this.tiles_0.size > forceRemoveThresh) {
      var $receiver = ArrayList_init();
      $receiver.addAll_brywnq$(this.removableTiles_0.values);
      var rmQueue = $receiver;
      if (rmQueue.size > 1) {
        sortWith(rmQueue, new Comparator$ObjectLiteral(compareBy$lambda(Globe$rebuildMesh$lambda(this))));
      }
      tmp$ = this.tiles_0.size - forceRemoveThresh | 0;
      for (var i_0 = 0; i_0 <= tmp$; i_0++) {
        this.removeTileMesh_0(rmQueue.get_za3lpa$(i_0), true);
      }
    }
  };
  Globe.prototype.addMeshesWrappingX_0 = function (xStart, xEnd, yStart, yEnd, zoom, ctx) {
    var size = 1 << zoom;
    var ys = Math_0.max(0, yStart);
    var a = size - 1 | 0;
    var ye = Math_0.min(a, yEnd);
    var tmp$ = Math_0.max(0, xStart);
    var a_0 = size - 1 | 0;
    this.addMeshes_0(new IntRange(tmp$, Math_0.min(a_0, xEnd)), new IntRange(ys, ye), zoom, ctx);
    if (xStart < 0 && xEnd < (size - 1 | 0)) {
      var a_1 = size + xStart | 0;
      this.addMeshes_0(until(Math_0.max(a_1, xEnd), size), new IntRange(ys, ye), zoom, ctx);
    }
     else if (xStart > 0 && xEnd > (size - 1 | 0)) {
      var b = xEnd - (size - 1) | 0;
      this.addMeshes_0(new IntRange(0, Math_0.min(xStart, b)), new IntRange(ys, ye), zoom, ctx);
    }
  };
  Globe.prototype.addMeshes_0 = function (xRng, yRng, zoom, ctx) {
    if ((xRng.last - xRng.first | 0) > 2 && (yRng.last - yRng.first | 0) > 2) {
      this.addMeshesCircular_0(xRng, yRng, zoom, ctx);
    }
     else {
      this.addMeshesRectRange_0(xRng, yRng, zoom, ctx);
    }
  };
  Globe.prototype.addMeshesRectRange_0 = function (xRng, yRng, zoom, ctx) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    tmp$ = xRng.first;
    tmp$_0 = xRng.last;
    tmp$_1 = xRng.step;
    for (var x = tmp$; x <= tmp$_0; x += tmp$_1) {
      tmp$_2 = yRng.first;
      tmp$_3 = yRng.last;
      tmp$_4 = yRng.step;
      for (var y = tmp$_2; y <= tmp$_3; y += tmp$_4) {
        this.addTile_0(x, y, zoom, xRng, yRng, ctx);
      }
    }
  };
  Globe.prototype.addMeshesCircular_0 = function (xRng, yRng, zoom, ctx) {
    var tmp$, tmp$_0;
    var cx = xRng.first + ((xRng.last - xRng.first | 0) / 2 | 0) | 0;
    var cy = yRng.first + ((yRng.last - yRng.first | 0) / 2 | 0) | 0;
    var a = cx - xRng.first | 0;
    var b = xRng.last - cx | 0;
    var tmp$_1 = Math_0.max(a, b);
    var a_0 = cy - yRng.first | 0;
    var b_0 = yRng.last - cy | 0;
    var b_1 = Math_0.max(a_0, b_0);
    var r = Math_0.max(tmp$_1, b_1);
    for (var i = 0; i <= r; i++) {
      tmp$ = cx + i | 0;
      for (var x = cx - i | 0; x <= tmp$; x++) {
        this.addTile_0(x, cy - i | 0, zoom, xRng, yRng, ctx);
        if (i > 0) {
          this.addTile_0(x, cy + i | 0, zoom, xRng, yRng, ctx);
        }
      }
      if (i > 0) {
        tmp$_0 = cy + i - 1 | 0;
        for (var y = cy - i + 1 | 0; y <= tmp$_0; y++) {
          this.addTile_0(cx - i | 0, y, zoom, xRng, yRng, ctx);
          this.addTile_0(cx + i | 0, y, zoom, xRng, yRng, ctx);
        }
      }
    }
  };
  Globe.prototype.addTile_0 = function (x, y, zoom, xRng, yRng, ctx) {
    if (xRng.contains_mef7kx$(x) && yRng.contains_mef7kx$(y)) {
      var key = TileName$Companion_getInstance().fuesdKey_qt1dr2$(x, y, zoom);
      var existing = this.tiles_0.get_11rb$(key);
      if (existing != null) {
        this.removableTiles_0.remove_11rb$(key);
        existing.isFadingOut = false;
        if (!existing.isLoaded) {
          this.loadingTiles_0.add_11rb$(key);
        }
      }
       else {
        var mesh = new TileMesh(this, new TileName(x, y, zoom), ctx);
        var parentFrame = this.meshGenerator_0.generateMesh_urq3fk$(this, mesh);
        this.tiles_0.put_xwzc9p$(key, mesh);
        this.loadingTiles_0.add_11rb$(key);
        if (parentFrame != null) {
          parentFrame.addTile_sdcfxe$(mesh);
        }
         else {
          this.getZoomGroup_za3lpa$(zoom).plusAssign_f1kmr1$(mesh);
        }
      }
    }
  };
  Globe.prototype.removeTileMesh_0 = function (mesh, forceRemove) {
    if (mesh.isCurrentlyVisible && !forceRemove) {
      mesh.isFadingOut = true;
    }
     else {
      this.removeTiles_0.add_11rb$(mesh);
    }
  };
  Globe.prototype.getZoomGroup_za3lpa$ = function (level) {
    return this.zoomGroups_0.get_za3lpa$(level - this.minZoomLvl | 0);
  };
  Globe.prototype.getTileFrame_sdbw1w$ = function (tileName) {
    var div = 1 << tileName.zoom - this.frameZoomLvl;
    var frameX = tileName.x / div | 0;
    var frameY = tileName.y / div | 0;
    var frameKey = TileName$Companion_getInstance().fuesdKey_qt1dr2$(frameX, frameY, this.frameZoomLvl);
    var $receiver = this.tileFrames_0;
    var tmp$;
    var value = $receiver.get_11rb$(frameKey);
    if (value == null) {
      var frame = new TileFrame(new TileName(frameX, frameY, this.frameZoomLvl), this);
      this.plusAssign_v64n5s$(frame);
      var $this = util.Log;
      var level = Log$Level.DEBUG;
      var tag = Kotlin.getKClassFromExpression(this).simpleName;
      if (level.level >= $this.level.level) {
        $this.printer(level, tag, 'added tile frame ' + frame.tileName);
      }
      var answer = frame;
      $receiver.put_xwzc9p$(frameKey, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    return tmp$;
  };
  Globe.prototype.tileFadedOut_sdcfxe$ = function (tileMesh) {
    this.removeTileMesh_0(tileMesh, true);
  };
  Globe.prototype.tileLoaded_sdcfxe$ = function (tileMesh) {
    this.removeObsoleteTilesBelow_0(tileMesh.tileName);
    this.loadingTiles_0.remove_11rb$(tileMesh.key);
    if (this.loadingTiles_0.isEmpty() && !this.removableTiles_0.isEmpty()) {
      var tmp$;
      tmp$ = this.removableTiles_0.values.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        this.removeTileMesh_0(element, false);
      }
      this.removableTiles_0.clear();
    }
  };
  Globe.prototype.removeObsoleteTilesBelow_0 = function (tileName) {
    var it = this.removableTiles_0.values.iterator();
    while (it.hasNext()) {
      var mesh = it.next();
      if (mesh.tileName.zoom > tileName.zoom) {
        var projX = mesh.tileName.x >> mesh.tileName.zoom - tileName.zoom;
        var projY = mesh.tileName.y >> mesh.tileName.zoom - tileName.zoom;
        if (projX === tileName.x && projY === tileName.y) {
          this.removeTileMesh_0(mesh, false);
          it.remove();
        }
      }
    }
  };
  function Globe$Companion() {
    Globe$Companion_instance = this;
    this.RAD_85_0 = 85.0 * math_0.DEG_2_RAD;
  }
  Globe$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Globe$Companion_instance = null;
  function Globe$Companion_getInstance() {
    if (Globe$Companion_instance === null) {
      new Globe$Companion();
    }
    return Globe$Companion_instance;
  }
  Globe.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Globe',
    interfaces: [TransformGroupDp]
  };
  function globeScene$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.leftDragMethod = SphericalInputTransform$DragMethod.NONE;
      $receiver.rightDragMethod = SphericalInputTransform$DragMethod.ROTATE;
      $receiver.zoomMethod = SphericalInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.minZoom = 20.0;
      $receiver.maxZoom = 2.0E7;
      $receiver.zoom = 1.0E7;
      $receiver.zoomAnimator.set_mx4ult$($receiver.zoom);
      $receiver.verticalAxis = Vec3f.Companion.Z_AXIS;
      $receiver.minHorizontalRot = 0.0;
      $receiver.maxHorizontalRot = 85.0;
      $receiver.updateTransform();
      $receiver.unaryPlus_uv0sim$(this$.camera);
      return Unit;
    };
  }
  function globeScene(ctx) {
    var scenes = ArrayList_init();
    var globe = {v: null};
    var $receiver = new Scene(null);
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, globeScene$lambda$lambda($receiver)));
    var earthRadius = 6371000.8;
    globe.v = new Globe(earthRadius);
    var dpGroup = new DoublePrecisionRoot(ensureNotNull(globe.v));
    $receiver.unaryPlus_uv0sim$(dpGroup);
    $receiver.registerDragHandler_dsvxak$(new GlobeDragHandler(ensureNotNull(globe.v)));
    scenes.add_11rb$($receiver);
    var ui = new GlobeUi(ensureNotNull(globe.v), ctx);
    var element = ui.scene;
    scenes.add_11rb$(element);
    return scenes;
  }
  function GlobeUi(globe, ctx) {
    this.globe = globe;
    this.ctx = ctx;
    this.attributionText_ef2tc2$_0 = this.attributionText_ef2tc2$_0;
    this.attribWidth_0 = 0.0;
    this.posWidth_0 = 0.0;
    this.attribution = '\xA9 OpenStreetMap';
    this.attributionUrl = 'http://www.openstreetmap.org/copyright';
    this.scene = uiScene(void 0, void 0, GlobeUi$scene$lambda(this));
  }
  Object.defineProperty(GlobeUi.prototype, 'attributionText_0', {
    get: function () {
      if (this.attributionText_ef2tc2$_0 == null)
        return throwUPAE('attributionText');
      return this.attributionText_ef2tc2$_0;
    },
    set: function (attributionText) {
      this.attributionText_ef2tc2$_0 = attributionText;
    }
  });
  function GlobeUi$scene$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function GlobeUi$scene$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(getCallableRef('SimpleComponentUi', function (component) {
      return new SimpleComponentUi(component);
    }));
    $receiver.containerUi_2t3ptw$(GlobeUi$scene$lambda$lambda$lambda);
    $receiver.standardFont_ttufcy$(new FontProps(Font.Companion.SYSTEM_FONT, 12.0));
    return Unit;
  }
  function GlobeUi$scene$lambda$lambda$lambda_0(this$GlobeUi, this$) {
    return function ($receiver, it) {
      var tmp$, tmp$_0;
      this$.text = this$GlobeUi.attribution;
      var w = (tmp$_0 = (tmp$ = this$.font.apply()) != null ? tmp$.textWidth_61zpoe$(this$.text) : null) != null ? tmp$_0 : 0.0;
      if (w !== this$GlobeUi.attribWidth_0) {
        this$GlobeUi.attribWidth_0 = w;
        this$.layoutSpec.setSize_4ujscr$(dps(w + 8, true), dps(18.0), zero());
        this$GlobeUi.posWidth_0 = 0.0;
      }
      return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda$lambda_1(this$GlobeUi) {
    return function ($receiver, f, f_0, f_1) {
      if (!(this$GlobeUi.attributionUrl.length === 0)) {
        this$GlobeUi.ctx.openUrl_61zpoe$(this$GlobeUi.attributionUrl);
      }
      return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda_0(this$GlobeUi) {
    return function ($receiver) {
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textColor.setCustom_11rb$(Color.Companion.LIME);
      $receiver.textColorHovered.setCustom_11rb$(color('#42A5F5'));
      var $receiver_0 = $receiver.onRender;
      var element = GlobeUi$scene$lambda$lambda$lambda_0(this$GlobeUi, $receiver);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = $receiver.onClick;
      var element_0 = GlobeUi$scene$lambda$lambda$lambda_1(this$GlobeUi);
      $receiver_1.add_11rb$(element_0);
      return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda$lambda_2(this$GlobeUi, this$, this$_0) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1;
      var lat = formatDouble(this$GlobeUi.globe.centerLat, 5);
      var lon = formatDouble(this$GlobeUi.globe.centerLon, 5);
      if (this$GlobeUi.globe.cameraHeight > 10000) {
        tmp$ = formatDouble(this$GlobeUi.globe.cameraHeight / 1000.0, 1) + ' km';
      }
       else {
        tmp$ = formatDouble(this$GlobeUi.globe.cameraHeight, 1) + ' m';
      }
      var hgt = tmp$;
      this$.text = lat + '\xB0, ' + lon + '\xB0  ' + hgt;
      var w = (tmp$_1 = (tmp$_0 = this$.font.apply()) != null ? tmp$_0.textWidth_61zpoe$(this$.text) : null) != null ? tmp$_1 : 0.0;
      if (w !== this$GlobeUi.posWidth_0) {
        this$GlobeUi.posWidth_0 = w;
        var xOri = dps(-w - 8, true);
        this$.layoutSpec.setSize_4ujscr$(dps(w + 8, true), dps(18.0), zero());
        this$.layoutSpec.setOrigin_4ujscr$(xOri, dps(0.0), zero());
        this$GlobeUi.attributionText_0.layoutSpec.setOrigin_4ujscr$(xOri.minus_m986jv$(this$GlobeUi.attributionText_0.layoutSpec.width), zero(), zero());
        this$_0.content.requestLayout();
      }
      return Unit;
    };
  }
  function GlobeUi$scene$lambda$lambda_1(this$GlobeUi, this$) {
    return function ($receiver) {
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(0.0, true));
      var $receiver_0 = $receiver.onRender;
      var element = GlobeUi$scene$lambda$lambda$lambda_2(this$GlobeUi, $receiver, this$);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function GlobeUi$scene$lambda(this$GlobeUi) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK, GlobeUi$scene$lambda$lambda);
      this$GlobeUi.attributionText_0 = $receiver.button_9zrh0o$('attributionText', GlobeUi$scene$lambda$lambda_0(this$GlobeUi));
      $receiver.unaryPlus_uv0sim$(this$GlobeUi.attributionText_0);
      $receiver.unaryPlus_uv0sim$($receiver.label_tokfmu$('posLabel', GlobeUi$scene$lambda$lambda_1(this$GlobeUi, $receiver)));
      return Unit;
    };
  }
  GlobeUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlobeUi',
    interfaces: []
  };
  function GlobeDragHandler(globe) {
    GlobeDragHandler$Companion_getInstance();
    this.globe = globe;
    this.steadyScreenPt_0 = MutableVec2f_init_0();
    this.steadyScreenPtMode_0 = 0;
    this.startTransform_0 = new Mat4d();
    this.ptOrientation_0 = new Mat4d();
    this.mouseRotationStart_0 = new Mat4d();
    this.isDragging_0 = false;
    this.pickRay_0 = new Ray();
    this.tmpVec_0 = MutableVec3d_init();
    this.tmpVecRt_0 = MutableVec3d_init();
    this.tmpVecUp_0 = MutableVec3d_init();
    this.tmpVecY_0 = MutableVec3d_init();
    this.tmpVecf_0 = MutableVec3f_init();
    this.tmpRayO_0 = MutableVec3d_init();
    this.tmpRayL_0 = MutableVec3d_init();
    this.globe.onPreRender.add_11rb$(GlobeDragHandler_init$lambda(this));
  }
  GlobeDragHandler.prototype.handleDrag_kin2e3$ = function (dragPtrs, ctx) {
    if (dragPtrs.size === 1 && dragPtrs.get_za3lpa$(0).isInViewport_aemszp$(ctx)) {
      var ptrX = dragPtrs.get_za3lpa$(0).x;
      var ptrY = dragPtrs.get_za3lpa$(0).y;
      if (dragPtrs.get_za3lpa$(0).isLeftButtonDown) {
        this.steadyScreenPtMode_0 = 0;
        if (dragPtrs.get_za3lpa$(0).isLeftButtonEvent) {
          this.isDragging_0 = this.computePointOrientation_0(ptrX, ptrY, ctx);
          this.ptOrientation_0.transpose_d4zu6l$(this.mouseRotationStart_0);
          this.globe.getTransform_d4zu6l$(this.startTransform_0);
        }
         else if (this.isDragging_0) {
          this.globe.set_d4zu6l$(this.startTransform_0);
          var valid = this.computePointOrientation_0(ptrX, ptrY, ctx);
          if (valid) {
            this.ptOrientation_0.mul_d4zu6l$(this.mouseRotationStart_0);
          }
          this.globe.mul_d4zu6l$(this.ptOrientation_0);
          this.isDragging_0 = valid;
        }
      }
       else if (dragPtrs.get_za3lpa$(0).deltaScroll !== 0.0 || (dragPtrs.get_za3lpa$(0).isRightButtonEvent && dragPtrs.get_za3lpa$(0).isRightButtonDown)) {
        if (this.steadyScreenPtMode_0 === 0 || ptrX !== this.steadyScreenPt_0.x || ptrY !== this.steadyScreenPt_0.y) {
          this.setSteadyPoint_0(ptrX, ptrY);
        }
      }
    }
    return 0;
  };
  GlobeDragHandler.prototype.onPreRender_0 = function (ctx) {
    if (this.steadyScreenPtMode_0 === 1 && this.computePointOrientation_0(this.steadyScreenPt_0.x, this.steadyScreenPt_0.y, ctx)) {
      this.steadyScreenPtMode_0 = 2;
      this.ptOrientation_0.transpose_d4zu6l$(this.mouseRotationStart_0);
      this.globe.getTransform_d4zu6l$(this.startTransform_0);
    }
     else if (this.steadyScreenPtMode_0 === 2) {
      this.globe.set_d4zu6l$(this.startTransform_0);
      if (this.computePointOrientation_0(this.steadyScreenPt_0.x, this.steadyScreenPt_0.y, ctx)) {
        this.ptOrientation_0.mul_d4zu6l$(this.mouseRotationStart_0);
      }
       else {
        this.steadyScreenPtMode_0 = 0;
      }
      this.globe.mul_d4zu6l$(this.ptOrientation_0);
    }
  };
  GlobeDragHandler.prototype.setSteadyPoint_0 = function (screenX, screenY) {
    this.steadyScreenPt_0.set_dleff0$(screenX, screenY);
    this.steadyScreenPtMode_0 = 1;
  };
  GlobeDragHandler.prototype.computePointOrientation_0 = function (screenX, screenY, ctx) {
    var tmp$, tmp$_0;
    if (((tmp$_0 = (tmp$ = this.globe.scene) != null ? tmp$.camera : null) != null ? tmp$_0.computePickRay_jker1g$(this.pickRay_0, screenX, screenY, ctx) : null) === true) {
      this.pickRay_0.origin.toMutableVec3d_5s4mqs$(this.tmpRayO_0);
      this.pickRay_0.direction.toMutableVec3d_5s4mqs$(this.tmpRayL_0);
      this.globe.toLocalCoordsDp_j7uy7i$(this.tmpRayO_0, 1.0);
      this.globe.toLocalCoordsDp_j7uy7i$(this.tmpRayL_0, 0.0);
      this.globe.toLocalCoordsDp_j7uy7i$(this.tmpVecY_0.set_czzhiw$(Vec3d.Companion.Y_AXIS), 0.0);
      var ldo = this.tmpRayL_0.times_czzhiw$(this.tmpRayO_0);
      var sqr = ldo * ldo - this.tmpRayO_0.sqrLength() + this.globe.radius * this.globe.radius;
      if (sqr > 0) {
        var d = -ldo - Math_0.sqrt(sqr);
        this.tmpRayL_0.scale_b0flbq$(d, this.tmpVec_0).add_czzhiw$(this.tmpRayO_0);
        this.tmpVec_0.norm();
        if (this.tmpVec_0.isFuzzyEqual_6nz8ey$(this.tmpVecY_0)) {
          return false;
        }
        this.tmpVecY_0.cross_vgki2o$(this.tmpVec_0, this.tmpVecRt_0).norm();
        this.tmpVec_0.cross_vgki2o$(this.tmpVecRt_0, this.tmpVecUp_0);
        this.ptOrientation_0.setColVec_umtdzk$(0, this.tmpVec_0, 0.0);
        this.ptOrientation_0.setColVec_umtdzk$(1, this.tmpVecRt_0, 0.0);
        this.ptOrientation_0.setColVec_umtdzk$(2, this.tmpVecUp_0, 0.0);
        this.ptOrientation_0.setColVec_umtdzk$(3, Vec3d.Companion.ZERO, 1.0);
        return true;
      }
    }
    return false;
  };
  function GlobeDragHandler$Companion() {
    GlobeDragHandler$Companion_instance = this;
    this.STEADY_SCREEN_PT_OFF_0 = 0;
    this.STEADY_SCREEN_PT_INIT_0 = 1;
    this.STEADY_SCREEN_PT_HOLD_0 = 2;
  }
  GlobeDragHandler$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var GlobeDragHandler$Companion_instance = null;
  function GlobeDragHandler$Companion_getInstance() {
    if (GlobeDragHandler$Companion_instance === null) {
      new GlobeDragHandler$Companion();
    }
    return GlobeDragHandler$Companion_instance;
  }
  function GlobeDragHandler_init$lambda(this$GlobeDragHandler) {
    return function ($receiver, it) {
      this$GlobeDragHandler.onPreRender_0(it);
      return Unit;
    };
  }
  GlobeDragHandler.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlobeDragHandler',
    interfaces: [InputManager$DragHandler]
  };
  function TileFrame(tileName, globe) {
    TileFrame$Companion_getInstance();
    TransformGroupDp.call(this);
    this.tileName = tileName;
    this.globe_0 = globe;
    this.zoomGroups = ArrayList_init();
    this.tileCount_gbn3bp$_0 = 0;
    var tmp$, tmp$_0;
    this.rotate_6y0v78$(this.tileName.center.lon, 0.0, 1.0, 0.0);
    this.rotate_6y0v78$(90.0 - this.tileName.center.lat, 1.0, 0.0, 0.0);
    this.translate_yvo9jy$(0.0, this.globe_0.radius, 0.0);
    this.checkInverse();
    tmp$ = this.tileName.zoom;
    tmp$_0 = this.globe_0.maxZoomLvl;
    for (var i = tmp$; i <= tmp$_0; i++) {
      var grp = new Group();
      this.zoomGroups.add_11rb$(grp);
      this.unaryPlus_uv0sim$(grp);
    }
  }
  Object.defineProperty(TileFrame.prototype, 'transformToLocal', {
    get: function () {
      return this.invTransform;
    }
  });
  Object.defineProperty(TileFrame.prototype, 'transformToGlobal', {
    get: function () {
      return this.transform;
    }
  });
  Object.defineProperty(TileFrame.prototype, 'tileCount', {
    get: function () {
      return this.tileCount_gbn3bp$_0;
    },
    set: function (tileCount) {
      this.tileCount_gbn3bp$_0 = tileCount;
    }
  });
  TileFrame.prototype.addTile_sdcfxe$ = function (tile) {
    this.getZoomGroup_0(tile.tileName.zoom).plusAssign_f1kmr1$(tile);
    this.tileCount = this.tileCount + 1 | 0;
  };
  TileFrame.prototype.removeTile_sdcfxe$ = function (tile) {
    this.getZoomGroup_0(tile.tileName.zoom).minusAssign_f1kmr1$(tile);
    this.tileCount = this.tileCount - 1 | 0;
  };
  TileFrame.prototype.getZoomGroup_0 = function (level) {
    return this.zoomGroups.get_za3lpa$(level - this.tileName.zoom | 0);
  };
  TileFrame.prototype.toLocalPosition_bwm9xk$ = function (latRad, lonRad, result) {
    return this.transformToLocal.transform_j7uy7i$(TileFrame$Companion_getInstance().latLonToCartesian_dp1656$(latRad, lonRad, this.globe_0.radius, result));
  };
  function TileFrame$Companion() {
    TileFrame$Companion_instance = this;
  }
  TileFrame$Companion.prototype.latLonToCartesian_dp1656$ = function (latRad, lonRad, radius, result) {
    var theta = math.PI * 0.5 - latRad;
    result.x = Math_0.sin(theta) * Math_0.sin(lonRad) * radius;
    result.z = Math_0.sin(theta) * Math_0.cos(lonRad) * radius;
    result.y = Math_0.cos(theta) * radius;
    return result;
  };
  TileFrame$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TileFrame$Companion_instance = null;
  function TileFrame$Companion_getInstance() {
    if (TileFrame$Companion_instance === null) {
      new TileFrame$Companion();
    }
    return TileFrame$Companion_instance;
  }
  TileFrame.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TileFrame',
    interfaces: [TransformGroupDp]
  };
  function TileMesh(globe, tileName, ctx) {
    Mesh.call(this, MeshData_init([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS, Attribute.Companion.TEXTURE_COORDS]), tileName.toString());
    this.globe = globe;
    this.tileName = tileName;
    this.centerNormal = MutableVec3f_init_0(Vec3f.Companion.Z_AXIS);
    this.tileShader_0 = null;
    this.tmpVec_0 = MutableVec3f_init();
    this.isFadingOut = false;
    this.isLoaded_cknz5n$_0 = false;
    this.isTexLoaded_393coo$_0 = false;
    this.tileShader_0 = this.globe.tileShaderProvider.getShader_jjvqbv$(this.tileName, ctx);
    this.shader = this.tileShader_0;
  }
  Object.defineProperty(TileMesh.prototype, 'key', {
    get: function () {
      return this.tileName.fusedKey;
    }
  });
  Object.defineProperty(TileMesh.prototype, 'isCurrentlyVisible', {
    get: function () {
      return this.isRendered;
    }
  });
  Object.defineProperty(TileMesh.prototype, 'isLoaded', {
    get: function () {
      return this.isLoaded_cknz5n$_0;
    },
    set: function (isLoaded) {
      this.isLoaded_cknz5n$_0 = isLoaded;
    }
  });
  Object.defineProperty(TileMesh.prototype, 'isTexLoaded', {
    get: function () {
      return this.isTexLoaded_393coo$_0;
    },
    set: function (isTexLoaded) {
      this.isTexLoaded_393coo$_0 = isTexLoaded;
    }
  });
  TileMesh.prototype.preRender_aemszp$ = function (ctx) {
    var targetAlpha = 1.0;
    if (this.isTexLoaded && !this.isFadingOut && this.tileShader_0.alpha < targetAlpha) {
      this.tileShader_0.alpha = this.tileShader_0.alpha + ctx.deltaT;
      if (this.tileShader_0.alpha >= targetAlpha) {
        this.tileShader_0.alpha = targetAlpha;
        this.isLoaded = true;
        this.globe.tileLoaded_sdcfxe$(this);
      }
    }
     else if (this.isFadingOut && this.tileShader_0.alpha > 0.0) {
      this.tileShader_0.alpha = this.tileShader_0.alpha - ctx.deltaT;
      if (this.tileShader_0.alpha <= 0.0) {
        this.tileShader_0.alpha = 0.0;
        this.globe.tileFadedOut_sdcfxe$(this);
      }
    }
    Mesh.prototype.preRender_aemszp$.call(this, ctx);
  };
  TileMesh.prototype.checkIsVisible_aemszp$ = function (ctx) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
    tmp$ = this.tileShader_0.texture;
    if (tmp$ == null) {
      return true;
    }
    var tex = tmp$;
    this.isTexLoaded = (tmp$_1 = (tmp$_0 = tex.res) != null ? tmp$_0.isLoaded : null) != null ? tmp$_1 : false;
    var visible = this.isTexLoaded && Mesh.prototype.checkIsVisible_aemszp$.call(this, ctx);
    if (visible) {
      this.toGlobalCoords_w1lst9$(this.tmpVec_0.set_czzhiu$(this.centerNormal), 0.0);
      var cos = (tmp$_5 = (tmp$_4 = (tmp$_3 = (tmp$_2 = this.scene) != null ? tmp$_2.camera : null) != null ? tmp$_3.globalLookDir : null) != null ? tmp$_4.dot_czzhiu$(this.tmpVec_0) : null) != null ? tmp$_5 : 0.0;
      return cos < 0.1;
    }
     else if (!this.isTexLoaded) {
      ctx.textureMgr.bindTexture_xyx3x4$(tex, ctx);
    }
    return false;
  };
  TileMesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TileMesh',
    interfaces: [Mesh]
  };
  function TileMeshGenerator() {
  }
  TileMeshGenerator.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TileMeshGenerator',
    interfaces: []
  };
  function FlatTileMeshGenerator() {
  }
  FlatTileMeshGenerator.prototype.generateMesh_urq3fk$ = function (globe, tileMesh) {
    var tmp$;
    if (tileMesh.tileName.zoom < globe.frameZoomThresh) {
      this.generateMesh_mc2053$_0(globe, tileMesh, null);
      tmp$ = null;
    }
     else {
      var div = 1 << tileMesh.tileName.zoom - globe.frameZoomLvl;
      var frameTile = new TileName(tileMesh.tileName.x / div | 0, tileMesh.tileName.y / div | 0, globe.frameZoomLvl);
      var frame = globe.getTileFrame_sdbw1w$(frameTile);
      this.generateMesh_mc2053$_0(globe, tileMesh, frame);
      tmp$ = frame;
    }
    return tmp$;
  };
  function FlatTileMeshGenerator$generateMesh$lambda(closure$tileMesh, closure$globe, closure$frame) {
    return function ($receiver) {
      var uvScale = 255.0 / 256.0;
      var uvOff = 0.5 / 256.0;
      var stepsExp = 4;
      var steps = 1 << stepsExp;
      var zoomDiv = 2 * math.PI / (1 << closure$tileMesh.tileName.zoom + stepsExp);
      var pos = MutableVec3d_init();
      var nrm = MutableVec3d_init();
      var posf = MutableVec3f_init();
      var nrmf = MutableVec3f_init();
      for (var row = 0; row <= steps; row++) {
        var tys = Kotlin.imul(closure$tileMesh.tileName.y + 1 | 0, steps) - row | 0;
        var x = math.PI - tys * zoomDiv;
        var x_0 = Math_0.sinh(x);
        var lat = Math_0.atan(x_0);
        for (var i = 0; i <= steps; i++) {
          var lon = (Kotlin.imul(closure$tileMesh.tileName.x, steps) + i | 0) * zoomDiv - math.PI;
          TileFrame$Companion_getInstance().latLonToCartesian_dp1656$(lat, lon, closure$globe.radius, pos);
          pos.norm_5s4mqs$(nrm);
          if (closure$frame != null) {
            closure$frame.transformToLocal.transform_j7uy7i$(pos, 1.0);
            closure$frame.transformToLocal.transform_j7uy7i$(nrm, 0.0);
          }
          var uv = new Vec2f(i / steps * uvScale + uvOff, 1.0 - (row / steps * uvScale + uvOff));
          var iv = $receiver.vertex_n440gp$(pos.toMutableVec3f_5s4mqq$(posf), nrm.toMutableVec3f_5s4mqq$(nrmf), uv);
          if (i > 0 && row > 0) {
            $receiver.meshData.addTriIndices_qt1dr2$(iv - steps - 2 | 0, iv, iv - 1 | 0);
            $receiver.meshData.addTriIndices_qt1dr2$(iv - steps - 2 | 0, iv - steps - 1 | 0, iv);
            if (row === (steps / 2 | 0) && i === (steps / 2 | 0)) {
              nrm.toMutableVec3f_5s4mqq$(closure$tileMesh.centerNormal);
            }
          }
        }
      }
      return Unit;
    };
  }
  FlatTileMeshGenerator.prototype.generateMesh_mc2053$_0 = function (globe, tileMesh, frame) {
    tileMesh.generator = FlatTileMeshGenerator$generateMesh$lambda(tileMesh, globe, frame);
    tileMesh.generateGeometry();
  };
  FlatTileMeshGenerator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FlatTileMeshGenerator',
    interfaces: [TileMeshGenerator]
  };
  function TileName(x, y, zoom) {
    TileName$Companion_getInstance();
    this.x = x;
    this.y = y;
    this.zoom = zoom;
    this.ne = null;
    this.sw = null;
    this.center = null;
    this.fusedKey = TileName$Companion_getInstance().fuesdKey_qt1dr2$(this.x, this.y, this.zoom);
    var zp = 1 << this.zoom;
    var x_0 = math.PI - (this.y + 1 | 0) / (1 << this.zoom) * 2 * math.PI;
    var x_1 = Math_0.sinh(x_0);
    var s = Math_0.atan(x_1) * math_0.RAD_2_DEG;
    var w = (this.x + 1 | 0) / zp * 360 - 180;
    var x_2 = math.PI - this.y / (1 << this.zoom) * 2 * math.PI;
    var x_3 = Math_0.sinh(x_2);
    var n = Math_0.atan(x_3) * math_0.RAD_2_DEG;
    var e = this.x / zp * 360 - 180;
    this.sw = new LatLon(s, w);
    this.ne = new LatLon(n, e);
    this.center = new LatLon(this.sw.lat + (this.ne.lat - this.sw.lat) / 2, this.sw.lon + (this.ne.lon - this.sw.lon) / 2);
  }
  function TileName$Companion() {
    TileName$Companion_instance = this;
  }
  TileName$Companion.prototype.forLatLon_c67stb$ = function (latLon, zoom) {
    return this.forLatLon_syxxoe$(latLon.lat, latLon.lon, zoom);
  };
  TileName$Companion.prototype.forLatLon_syxxoe$ = function (lat, lon, zoom) {
    var latRad = lat * math_0.DEG_2_RAD;
    var zp = 1 << zoom;
    var $receiver = numberToInt((lon + 180.0) / 360 * zp);
    var max = zp - 1 | 0;
    var clamp$result;
    if ($receiver < 0) {
      clamp$result = 0;
    }
     else if ($receiver > max) {
      clamp$result = max;
    }
     else {
      clamp$result = $receiver;
    }
    var x = clamp$result;
    var x_0 = Math_0.tan(latRad) + 1 / Math_0.cos(latRad);
    var $receiver_0 = numberToInt((1 - Math_0.log(x_0) / math.PI) / 2 * zp);
    var max_0 = zp - 1 | 0;
    var clamp$result_0;
    if ($receiver_0 < 0) {
      clamp$result_0 = 0;
    }
     else if ($receiver_0 > max_0) {
      clamp$result_0 = max_0;
    }
     else {
      clamp$result_0 = $receiver_0;
    }
    var y = clamp$result_0;
    return new TileName(x, y, zoom);
  };
  TileName$Companion.prototype.fuesdKey_qt1dr2$ = function (tx, ty, tz) {
    return Kotlin.Long.fromInt(tz).shiftLeft(58).or(Kotlin.Long.fromInt(tx & 536870911).shiftLeft(29)).or(Kotlin.Long.fromInt(ty & 536870911));
  };
  TileName$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TileName$Companion_instance = null;
  function TileName$Companion_getInstance() {
    if (TileName$Companion_instance === null) {
      new TileName$Companion();
    }
    return TileName$Companion_instance;
  }
  TileName.prototype.toString = function () {
    return this.zoom.toString() + '/' + this.x + '/' + this.y;
  };
  TileName.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, TileName))
      return false;
    if (!equals(this.fusedKey, other.fusedKey))
      return false;
    return true;
  };
  TileName.prototype.hashCode = function () {
    return hashCode(this.fusedKey);
  };
  TileName.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TileName',
    interfaces: []
  };
  function LatLon(lat, lon) {
    this.lat = lat;
    this.lon = lon;
  }
  LatLon.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LatLon',
    interfaces: []
  };
  LatLon.prototype.component1 = function () {
    return this.lat;
  };
  LatLon.prototype.component2 = function () {
    return this.lon;
  };
  LatLon.prototype.copy_lu1900$ = function (lat, lon) {
    return new LatLon(lat === void 0 ? this.lat : lat, lon === void 0 ? this.lon : lon);
  };
  LatLon.prototype.toString = function () {
    return 'LatLon(lat=' + Kotlin.toString(this.lat) + (', lon=' + Kotlin.toString(this.lon)) + ')';
  };
  LatLon.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.lat) | 0;
    result = result * 31 + Kotlin.hashCode(this.lon) | 0;
    return result;
  };
  LatLon.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.lat, other.lat) && Kotlin.equals(this.lon, other.lon)))));
  };
  function TileShaderProvider() {
  }
  TileShaderProvider.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TileShaderProvider',
    interfaces: []
  };
  function TexImageTileShaderProvider() {
  }
  function TexImageTileShaderProvider$getShader$lambda(closure$tileName, closure$ctx, this$TexImageTileShaderProvider) {
    return function ($receiver) {
      $receiver.colorModel = ColorModel.TEXTURE_COLOR;
      $receiver.lightModel = LightModel.NO_LIGHTING;
      $receiver.isAlpha = true;
      $receiver.alpha = 0.0;
      $receiver.texture = this$TexImageTileShaderProvider.getTexture_jjvqbv$(closure$tileName, closure$ctx);
      return Unit;
    };
  }
  TexImageTileShaderProvider.prototype.getShader_jjvqbv$ = function (tileName, ctx) {
    return basicShader(TexImageTileShaderProvider$getShader$lambda(tileName, ctx, this));
  };
  TexImageTileShaderProvider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TexImageTileShaderProvider',
    interfaces: [TileShaderProvider]
  };
  function OsmTexImageTileShaderProvider() {
    TexImageTileShaderProvider.call(this);
    this.tileUrls = mutableListOf(['tile.openstreetmap.org']);
  }
  OsmTexImageTileShaderProvider.prototype.getTexture_jjvqbv$ = function (tileName, ctx) {
    return assetTexture('https://' + this.tileUrls.get_za3lpa$(randomI(get_indices(this.tileUrls))) + '/' + tileName.zoom + '/' + tileName.x + '/' + tileName.y + '.png', ctx);
  };
  OsmTexImageTileShaderProvider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OsmTexImageTileShaderProvider',
    interfaces: [TexImageTileShaderProvider]
  };
  function makeGroundGrid$lambda$lambda$lambda(closure$groundExt, closure$y) {
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
      $receiver.meshData.generateTangents();
      return Unit;
    };
  }
  function makeGroundGrid$lambda$lambda$lambda_0(closure$shadows, closure$ctx) {
    return function ($receiver) {
      $receiver.lightModel = LightModel.PHONG_LIGHTING;
      $receiver.colorModel = ColorModel.TEXTURE_COLOR;
      $receiver.shadowMap = closure$shadows;
      $receiver.isNormalMapped = true;
      $receiver.specularIntensity = 0.25;
      var props = TextureProps_init('ground_nrm.png', 9729, 10497);
      $receiver.normalMap = assetTexture_0(props, closure$ctx);
      var colorProps = TextureProps_init('ground_color.png', 9729, 10497);
      $receiver.texture = assetTexture_0(colorProps, closure$ctx);
      return Unit;
    };
  }
  function makeGroundGrid$lambda$lambda(closure$groundExt, closure$y, closure$shadows, closure$ctx) {
    return function ($receiver) {
      $receiver.isCastingShadow = false;
      $receiver.generator = makeGroundGrid$lambda$lambda$lambda(closure$groundExt, closure$y);
      $receiver.shader = basicShader(makeGroundGrid$lambda$lambda$lambda_0(closure$shadows, closure$ctx));
      return Unit;
    };
  }
  function makeGroundGrid$lambda(closure$cells, closure$y, closure$shadows, closure$ctx) {
    return function ($receiver) {
      var groundExt = closure$cells / 2 | 0;
      $receiver.unaryPlus_uv0sim$(textureMesh(void 0, true, makeGroundGrid$lambda$lambda(groundExt, closure$y, closure$shadows, closure$ctx)));
      return Unit;
    };
  }
  function makeGroundGrid(cells, ctx, shadows, y) {
    if (shadows === void 0)
      shadows = null;
    if (y === void 0)
      y = 0.0;
    return group(void 0, makeGroundGrid$lambda(cells, y, shadows, ctx));
  }
  function modelScene$lambda$lambda$lambda$lambda(this$, closure$mesh) {
    return function ($receiver) {
      $receiver.lightModel = LightModel.PHONG_LIGHTING;
      $receiver.colorModel = ColorModel.STATIC_COLOR;
      $receiver.staticColor = Color.Companion.GRAY;
      $receiver.shadowMap = this$.defaultShadowMap;
      if (Kotlin.isType(closure$mesh, Armature) && !closure$mesh.isCpuAnimated) {
        $receiver.numBones = closure$mesh.bones.size;
      }
      return Unit;
    };
  }
  function modelScene$lambda$lambda$lambda$lambda_0(closure$movementSpeed, closure$slowMotion, this$, closure$mesh) {
    return function ($receiver, ctx) {
      var $receiver_0 = ctx.deltaT;
      var clamp$result;
      if ($receiver_0 < 0.0) {
        clamp$result = 0.0;
      }
       else if ($receiver_0 > 0.1) {
        clamp$result = 0.1;
      }
       else {
        clamp$result = $receiver_0;
      }
      var dt = clamp$result;
      this$.translate_y2kzbl$(0.0, -dt * closure$movementSpeed.v * closure$slowMotion.v * 5.0, 0.0);
      this$.rotate_ad55pp$(dt * closure$movementSpeed.v * closure$slowMotion.v * 50.0, Vec3f.Companion.Z_AXIS);
      closure$mesh.animationSpeed = closure$slowMotion.v;
      return Unit;
    };
  }
  function modelScene$lambda$lambda$lambda(closure$model, this$, closure$armature, closure$movementSpeed, closure$slowMotion, this$_0) {
    return function (data) {
      var tmp$;
      var mesh = loadMesh(data);
      closure$model.plusAssign_f1kmr1$(mesh);
      mesh.shader = basicShader(modelScene$lambda$lambda$lambda$lambda(this$, mesh));
      if (Kotlin.isType(mesh, Armature)) {
        closure$armature.v = mesh;
        (tmp$ = mesh.getAnimation_61zpoe$('Armature|walk')) != null ? (tmp$.weight = 1.0) : null;
        var $receiver = mesh.onPreRender;
        var element = modelScene$lambda$lambda$lambda$lambda_0(closure$movementSpeed, closure$slowMotion, this$_0, mesh);
        $receiver.add_11rb$(element);
      }
      return Unit;
    };
  }
  function modelScene$lambda$lambda$lambda_0(this$) {
    return function ($receiver) {
      $receiver.verticalAxis = Vec3f.Companion.Z_AXIS;
      $receiver.minHorizontalRot = 0.0;
      $receiver.maxHorizontalRot = 180.0;
      $receiver.zoomMethod = SphericalInputTransform$ZoomMethod.ZOOM_CENTER;
      $receiver.rightDragMethod = SphericalInputTransform$DragMethod.NONE;
      $receiver.translation.set_y2kzbl$(0.5, 0.0, 1.0);
      $receiver.setMouseRotation_dleff0$(20.0, 75.0);
      $receiver.resetZoom_mx4ult$(2.0);
      $receiver.unaryPlus_uv0sim$(this$.camera);
      return Unit;
    };
  }
  function modelScene$lambda$lambda$lambda$lambda$lambda$lambda(closure$movementSpeed, closure$armature, closure$speedLabel) {
    return function ($receiver, value) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
      closure$movementSpeed.v = value * value;
      if (closure$armature.v != null) {
        var $receiver_0 = 1.0 - value * 2.0;
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
        var idleWeight = clamp$result;
        var $receiver_1 = (value - 0.5) * 2.0;
        var clamp$result_0;
        if ($receiver_1 < 0.0) {
          clamp$result_0 = 0.0;
        }
         else if ($receiver_1 > 1.0) {
          clamp$result_0 = 1.0;
        }
         else {
          clamp$result_0 = $receiver_1;
        }
        var runWeight = clamp$result_0;
        if (runWeight > 0.0)
          tmp$ = 1.0 - runWeight;
        else
          tmp$ = 1.0 - idleWeight;
        var walkWeight = tmp$;
        closure$speedLabel.text = formatFloat(value, 2);
        (tmp$_0 = ensureNotNull(closure$armature.v).getAnimation_61zpoe$('Armature|idle')) != null ? (tmp$_0.weight = idleWeight) : null;
        (tmp$_1 = ensureNotNull(closure$armature.v).getAnimation_61zpoe$('Armature|walk')) != null ? (tmp$_1.weight = walkWeight) : null;
        (tmp$_2 = ensureNotNull(closure$armature.v).getAnimation_61zpoe$('Armature|run')) != null ? (tmp$_2.weight = runWeight) : null;
      }
      return Unit;
    };
  }
  function modelScene$lambda$lambda$lambda$lambda$lambda$lambda_0(closure$slowMotion, closure$slowMoLabel) {
    return function ($receiver, value) {
      closure$slowMotion.v = value;
      closure$slowMoLabel.text = formatFloat(closure$slowMotion.v, 2);
      return Unit;
    };
  }
  function modelScene$lambda$lambda$lambda_1(closure$movementSpeed, closure$armature, closure$slowMotion) {
    return function ($receiver) {
      $receiver.globalWidth = 0.75;
      $receiver.globalHeight = 1.0;
      var $receiver_0 = $receiver.content;
      var closure$movementSpeed_0 = closure$movementSpeed;
      var closure$armature_0 = closure$armature;
      var closure$slowMotion_0 = closure$slowMotion;
      $receiver_0.rotate_ad55pp$(90.0, Vec3f.Companion.X_AXIS);
      $receiver_0.translate_y2kzbl$(0.5, 1.2, 0.0);
      var $receiver_1 = new Label('label1', $receiver_0.root);
      $receiver_1.layoutSpec.setOrigin_4ujscr$(uns(0.0), dps(140.0), uns(0.0));
      $receiver_1.layoutSpec.setSize_4ujscr$(pcs(75.0), dps(35.0), uns(0.0));
      $receiver_1.textAlignment = new Gravity(Alignment.START, Alignment.END);
      $receiver_1.padding.bottom = dps(4.0);
      $receiver_1.text = 'Movement Speed:';
      $receiver_0.unaryPlus_uv0sim$($receiver_1);
      var $receiver_2 = new Label('speedLabel', $receiver_0.root);
      $receiver_2.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(140.0), uns(0.0));
      $receiver_2.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(35.0), uns(0.0));
      $receiver_2.textAlignment = new Gravity(Alignment.START, Alignment.END);
      $receiver_2.padding.bottom = dps(4.0);
      var x = closure$movementSpeed_0.v;
      $receiver_2.text = formatFloat(Math_0.sqrt(x), 2);
      var speedLabel = $receiver_2;
      $receiver_0.unaryPlus_uv0sim$(speedLabel);
      var x_0 = closure$movementSpeed_0.v;
      var $receiver_3 = new Slider('speedSlider', 0.0, 1.0, Math_0.sqrt(x_0), $receiver_0.root);
      $receiver_3.layoutSpec.setOrigin_4ujscr$(uns(0.0), dps(90.0), uns(0.0));
      $receiver_3.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(50.0), uns(0.0));
      $receiver_3.onValueChanged = plus($receiver_3.onValueChanged, modelScene$lambda$lambda$lambda$lambda$lambda$lambda(closure$movementSpeed_0, closure$armature_0, speedLabel));
      $receiver_0.unaryPlus_uv0sim$($receiver_3);
      var $receiver_4 = new Label('label2', $receiver_0.root);
      $receiver_4.layoutSpec.setOrigin_4ujscr$(uns(0.0), dps(50.0), uns(0.0));
      $receiver_4.layoutSpec.setSize_4ujscr$(pcs(75.0), dps(40.0), uns(0.0));
      $receiver_4.textAlignment = new Gravity(Alignment.START, Alignment.END);
      $receiver_4.padding.bottom = dps(4.0);
      $receiver_4.text = 'Slow Motion:';
      $receiver_0.unaryPlus_uv0sim$($receiver_4);
      var $receiver_5 = new Label('slowMotion', $receiver_0.root);
      $receiver_5.layoutSpec.setOrigin_4ujscr$(pcs(75.0), dps(50.0), uns(0.0));
      $receiver_5.layoutSpec.setSize_4ujscr$(pcs(25.0), dps(40.0), uns(0.0));
      $receiver_5.textAlignment = new Gravity(Alignment.START, Alignment.END);
      $receiver_5.padding.bottom = dps(4.0);
      $receiver_5.text = formatFloat(closure$slowMotion_0.v, 2);
      var slowMoLabel = $receiver_5;
      $receiver_0.unaryPlus_uv0sim$(slowMoLabel);
      var $receiver_6 = new Slider('slowMoSlider', 0.0, 1.0, closure$slowMotion_0.v, $receiver_0.root);
      $receiver_6.layoutSpec.setOrigin_4ujscr$(uns(0.0), uns(0.0), uns(0.0));
      $receiver_6.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(50.0), uns(0.0));
      $receiver_6.onValueChanged = plus($receiver_6.onValueChanged, modelScene$lambda$lambda$lambda$lambda$lambda$lambda_0(closure$slowMotion_0, slowMoLabel));
      $receiver_0.unaryPlus_uv0sim$($receiver_6);
      return Unit;
    };
  }
  function modelScene$lambda$lambda(closure$ctx, this$) {
    return function ($receiver) {
      var model = new TransformGroup();
      var movementSpeed = {v: 0.25};
      var slowMotion = {v: 1.0};
      var armature = {v: null};
      $receiver.unaryPlus_uv0sim$(model);
      closure$ctx.assetMgr.loadAsset_jrww91$('player.kmf', modelScene$lambda$lambda$lambda(model, this$, armature, movementSpeed, slowMotion, $receiver));
      $receiver.rotate_ad55pp$(-90.0, Vec3f.Companion.X_AXIS);
      $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, modelScene$lambda$lambda$lambda_0(this$)));
      $receiver.unaryPlus_uv0sim$(embeddedUi(dps(400.0), void 0, modelScene$lambda$lambda$lambda_1(movementSpeed, armature, slowMotion)));
      return Unit;
    };
  }
  function modelScene(ctx) {
    var $receiver = new Scene(null);
    $receiver.defaultShadowMap = CascadedShadowMap.Companion.defaultCascadedShadowMap3();
    $receiver.unaryPlus_uv0sim$(makeGroundGrid(40, ctx, $receiver.defaultShadowMap));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, modelScene$lambda$lambda(ctx, $receiver)));
    return $receiver;
  }
  function multiScene$lambda($receiver, ctx) {
    var vp = ctx.viewport;
    var width = numberToInt(vp.width * 0.5);
    ctx.pushAttributes();
    ctx.viewport = new KoolContext$Viewport(vp.x, vp.y, width, vp.height);
    ctx.applyAttributes();
    return Unit;
  }
  function multiScene$lambda_0($receiver, ctx) {
    ctx.popAttributes();
    return Unit;
  }
  function multiScene$lambda_1($receiver, ctx) {
    var vp = ctx.viewport;
    var width = numberToInt(vp.width * 0.5);
    ctx.pushAttributes();
    ctx.viewport = new KoolContext$Viewport(width, vp.y, width, vp.height);
    ctx.applyAttributes();
    return Unit;
  }
  function multiScene$lambda_2($receiver, ctx) {
    ctx.popAttributes();
    return Unit;
  }
  function multiScene(koolCtx) {
    var leftScene = simpleShapesScene(koolCtx);
    var rightScene = uiDemoScene();
    leftScene.onPreRender.add_11rb$(multiScene$lambda);
    leftScene.onPostRender.add_11rb$(multiScene$lambda_0);
    rightScene.clearMask = 0;
    rightScene.onPreRender.add_11rb$(multiScene$lambda_1);
    rightScene.onPostRender.add_11rb$(multiScene$lambda_2);
    return listOf([leftScene, rightScene]);
  }
  function pointScene$lambda$lambda(closure$frameCnt, closure$data, closure$trav, closure$ptVertCnt, closure$tree) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2;
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
        closure$tree.traverse_klhj8v$(closure$trav);
        var searchT = t.takeMs();
        t.reset();
        var color = Color.Companion.fromHsv_7b5o5w$(randomF(0.0, 360.0), 1.0, 1.0, 1.0);
        tmp$_1 = closure$trav.result.iterator();
        while (tmp$_1.hasNext()) {
          var point_0 = tmp$_1.next();
          tmp$_2 = closure$ptVertCnt;
          for (var i_0 = 0; i_0 < tmp$_2; i_0++) {
            vert.index = point_0.index + i_0 | 0;
            vert.color.set_d7aj7k$(color);
          }
        }
        var updateT = t.takeMs();
        var $this = util.Log;
        var level = Log$Level.INFO;
        var tag = Kotlin.getKClassFromExpression($receiver).simpleName;
        if (level.level >= $this.level.level) {
          $this.printer(level, tag, 'In-radius search retrieved ' + closure$trav.result.size + ' points, ' + ('took ' + formatDouble(searchT, 3) + ' ms; ') + ('Point update took ' + formatDouble(updateT, 3) + ' ms'));
        }
        closure$data.isSyncRequired = true;
      }
      return Unit;
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
    var trav = InRadiusTraverser_init(Vec3f.Companion.ZERO, 1.0);
    var data = pointMesh.meshData;
    var ptVertCnt = Kotlin.isType(pointMesh, BillboardMesh) ? 4 : 1;
    var frameCnt = {v: 30};
    var $receiver = new Scene(null);
    $receiver.onPreRender.add_11rb$(pointScene$lambda$lambda(frameCnt, data, trav, ptVertCnt, tree));
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, pointScene$lambda$lambda_0($receiver)));
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
      $receiver.pointSize = 3.0;
      var dist = new CubicPointDistribution(5.0);
      for (var i = 1; i <= 100000; i++) {
        var pt = dist.nextPoint();
        var idx = $receiver.addPoint_hvwyd1$(makePointMesh$lambda$lambda(pt));
        closure$points.add_11rb$(new MeshPoint(pt.x, pt.y, pt.z, idx));
      }
      return Unit;
    };
  }
  var now = $module$kool.de.fabmax.kool.now;
  function makePointMesh() {
    var points = ArrayList_init();
    var mesh = pointMesh(void 0, makePointMesh$lambda(points));
    var message = 'Constructed k-d-Tree with ' + points.size + ' points in ';
    var tag;
    var level;
    tag = 'PerfTimer';
    level = Log$Level.INFO;
    var t = now();
    var ret = pointTree(points);
    var $this = util.Log;
    if (level.level >= $this.level.level) {
      $this.printer(level, tag, message + ' ' + formatDouble(now() - t, 3) + ' ms');
    }
    var tree = ret;
    return new Pair(mesh, tree);
  }
  function makeBillboardPointMesh() {
    var mesh = new BillboardMesh();
    mesh.billboardSize = 3.0;
    var points = ArrayList_init();
    for (var i = 1; i <= 100000; i++) {
      var x = randomF(-2.5, 2.5);
      var z = randomF(-2.5, 2.5);
      var y = randomF(-2.5, 2.5);
      mesh.addQuad_4sqmhu$(new Vec3f(x, y, z), Color.Companion.DARK_GRAY);
      points.add_11rb$(new MeshPoint(x, y, z, (i - 1 | 0) * 4 | 0));
    }
    var message = 'Constructed k-d-Tree with ' + points.size + ' points in ';
    var tag;
    var level;
    tag = 'PerfTimer';
    level = Log$Level.INFO;
    var t = now();
    var ret = pointTree(points);
    var $this = util.Log;
    if (level.level >= $this.level.level) {
      $this.printer(level, tag, message + ' ' + formatDouble(now() - t, 3) + ' ms');
    }
    var tree = ret;
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
  function simpleShapesScene$lambda$lambda$lambda_0(closure$ctx) {
    return function ($receiver) {
      var tmp$;
      $receiver.generator = simpleShapesScene$lambda$lambda$lambda$lambda;
      (Kotlin.isType(tmp$ = $receiver.shader, BasicShader) ? tmp$ : throwCCE()).texture = assetTexture('world.jpg', closure$ctx);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda_0(closure$ctx) {
    return function ($receiver) {
      var animator = new CosAnimator(new InterpolatedFloat(-1.0, 1.0));
      animator.repeating = Animator.Companion.REPEAT_TOGGLE_DIR;
      $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda($receiver, animator));
      $receiver.unaryPlus_uv0sim$(textureMesh('Sphere', void 0, simpleShapesScene$lambda$lambda$lambda_0(closure$ctx)));
      return Unit;
    };
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
  function simpleShapesScene$lambda$lambda$lambda$lambda_0(closure$cubeAnimator, this$) {
    return function (v) {
      var tmp$;
      closure$cubeAnimator.speed = v;
      (Kotlin.isType(tmp$ = this$.shader, BasicShader) ? tmp$ : throwCCE()).saturation = v;
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_1($receiver) {
    $receiver.colorModel = ColorModel.VERTEX_COLOR;
    $receiver.lightModel = LightModel.PHONG_LIGHTING;
    $receiver.isSaturation = true;
    return Unit;
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_2($receiver) {
    $receiver.scale_y2kzbl$(2.0, 2.0, 2.0);
    var $receiver_0 = $receiver.cubeProps.defaults();
    $receiver_0.colorCube();
    $receiver_0.centerOrigin();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_3(closure$speedAnimator) {
    return function ($receiver, ctx) {
      closure$speedAnimator.tick_aemszp$(ctx);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_4(closure$speedAnimator) {
    return function ($receiver, f, f_0, f_1) {
      closure$speedAnimator.speed = 1.0;
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda$lambda_5(closure$speedAnimator) {
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
      speedAnimator.value.onUpdate = simpleShapesScene$lambda$lambda$lambda$lambda_0(closure$cubeAnimator, $receiver);
      $receiver.shader = basicShader(simpleShapesScene$lambda$lambda$lambda$lambda_1);
      $receiver.generator = simpleShapesScene$lambda$lambda$lambda$lambda_2;
      $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda$lambda_3(speedAnimator));
      $receiver.onHoverEnter.add_11rb$(simpleShapesScene$lambda$lambda$lambda$lambda_4(speedAnimator));
      $receiver.onHoverExit.add_11rb$(simpleShapesScene$lambda$lambda$lambda$lambda_5(speedAnimator));
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
  var TextProps_init = $module$kool.de.fabmax.kool.util.TextProps;
  function simpleShapesScene$lambda$lambda$lambda$lambda_6(closure$font) {
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
      $receiver.text_lis6zk$(props);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda$lambda_4(closure$font) {
    return function ($receiver) {
      $receiver.generator = simpleShapesScene$lambda$lambda$lambda$lambda_6(closure$font);
      return Unit;
    };
  }
  function simpleShapesScene$lambda$lambda_2(closure$ctx) {
    return function ($receiver) {
      var animator = new CosAnimator(new InterpolatedFloat(0.75, 1.25));
      animator.repeating = Animator.Companion.REPEAT_TOGGLE_DIR;
      animator.duration = 0.75;
      $receiver.onPreRender.add_11rb$(simpleShapesScene$lambda$lambda$lambda_3(animator, $receiver));
      var font = new Font(new FontProps(Font.Companion.SYSTEM_FONT, 72.0, 0, 1.5), closure$ctx);
      $receiver.unaryPlus_uv0sim$(textMesh(font, void 0, simpleShapesScene$lambda$lambda$lambda_4(font)));
      return Unit;
    };
  }
  function simpleShapesScene(ctx) {
    var $receiver = new Scene('simpleShapes');
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, simpleShapesScene$lambda$lambda($receiver)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, simpleShapesScene$lambda$lambda_0(ctx)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, simpleShapesScene$lambda$lambda_1));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, simpleShapesScene$lambda$lambda_2(ctx)));
    return $receiver;
  }
  function synthieScene(ctx) {
    var content = new SynthieScene(ctx);
    var menu = synthieMenu(content, ctx);
    return listOf([content, menu]);
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
      $receiver.layoutSpec.setSize_4ujscr$(dps(320.0), pcs(100.0), zero());
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
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), zero());
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
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), zero());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      $receiver.onValueChanged = plus($receiver.onValueChanged, synthieMenu$lambda$lambda$lambda$lambda$lambda(closure$content));
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_1($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), dps(80.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), zero());
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
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), zero());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      $receiver.onValueChanged = plus($receiver.onValueChanged, synthieMenu$lambda$lambda$lambda$lambda$lambda_0(closure$content));
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_3($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), dps(40.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), zero());
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
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), zero());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      $receiver.onValueChanged = plus($receiver.onValueChanged, synthieMenu$lambda$lambda$lambda$lambda$lambda_1(closure$content));
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda$lambda$lambda_5($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(5.0), zero(), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(70.0, true), dps(40.0, true), zero());
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
      $receiver.layoutSpec.setSize_4ujscr$(dps(170.0), dps(40.0), zero());
      $receiver.padding = new Margin(zero(), zero(), zero(), zero());
      $receiver.onValueChanged = plus($receiver.onValueChanged, synthieMenu$lambda$lambda$lambda$lambda$lambda_2(closure$content));
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
      }
      return Unit;
    };
  }
  function synthieMenu$lambda$lambda_0(closure$content, this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(260.0), zero());
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.unaryPlus_uv0sim$(this$.container_t34sov$('sequencer', synthieMenu$lambda$lambda$lambda_1(closure$content, this$)));
      var $receiver_0 = new VerticalLayout('volumes', this$);
      var this$_0 = this$;
      var closure$content_0 = closure$content;
      $receiver_0.layoutSpec.setOrigin_4ujscr$(pcs(50.0, true).plus_m986jv$(dps(250.0, true)), dps(10.0, true), zero());
      $receiver_0.layoutSpec.setSize_4ujscr$(dps(240.0, true), dps(160.0, true), zero());
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
    return uiScene(ctx.screenDpi, void 0, synthieMenu$lambda(content));
  }
  function VerticalLayout(name, root) {
    UiContainer.call(this, name, root);
  }
  VerticalLayout.prototype.doLayout_sq5703$ = function (bounds, ctx) {
    if (!(bounds != null ? bounds.equals(this.contentBounds) : null)) {
      this.contentBounds.clear();
    }
    UiContainer.prototype.doLayout_sq5703$.call(this, bounds, ctx);
    if (!(bounds != null ? bounds.equals(this.contentBounds) : null)) {
      this.translate_y2kzbl$(-this.posInParent.x, -this.posInParent.y, -this.posInParent.z);
      this.rotate_ad55pp$(90.0, Vec3f.Companion.Z_AXIS);
      this.translate_y2kzbl$(this.posInParent.y, -this.posInParent.x, this.posInParent.z);
    }
  };
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
    }
    return this.moodFilter_0.filter_7b5o5w$(this.lfo1_0.next_mx4ult$(dt) + 1050, this.lfo2_0.next_mx4ult$(dt), SampleNode.Companion.perc_7b5o5w$(osc, 48.0, this.t % 0.125), dt) * 0.25;
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
    this.colorAnimator_0 = new CosAnimator(new InterpolatedColor(MutableColor_init(Color.Companion.WHITE.withAlpha_mx4ult$(0.2)), MutableColor_init(Color.Companion.LIME.withAlpha_mx4ult$(0.6))));
    this.wasHovered_0 = false;
    this.layoutSpec.setOrigin_4ujscr$(dps(this.col * 20.0), dps(this.row * 15.0), zero());
    this.layoutSpec.setSize_4ujscr$(dps(18.0), dps(13.0), zero());
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
      }
       else {
        this.melody.sequence[this.col] = this.row;
      }
    }
     else if (!this.wasHovered_0 && ptr.isLeftButtonDown) {
      this.melody.sequence[this.col] = this.row;
    }
    this.wasHovered_0 = true;
  };
  SequenceButton.prototype.render_aemszp$ = function (ctx) {
    if (this.melody.sequence[this.col] === this.row) {
      this.colorAnimator_0.speed = 1.0;
    }
     else {
      this.colorAnimator_0.speed = -1.0;
    }
    this.colorAnimator_0.tick_aemszp$(ctx);
    this.background_0.bgColor.set_d7aj7k$(this.colorAnimator_0.value.value);
    if (this.isHovered) {
      this.background_0.bgColor.a = this.background_0.bgColor.a + 0.4;
    }
     else {
      var a = this.melody.index - this.col;
      if (a > 0 && a <= 1) {
        var tmp$ = this.background_0.bgColor;
        var tmp$_0 = this.background_0.bgColor.a;
        var x = a - 0.5;
        var $receiver = 0.5 - Math_0.abs(x);
        var clamp$result;
        if ($receiver < 0.0) {
          clamp$result = 0.0;
        }
         else if ($receiver > 0.1) {
          clamp$result = 0.1;
        }
         else {
          clamp$result = $receiver;
        }
        tmp$.a = tmp$_0 + clamp$result;
      }
    }
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
    this.bgColor = MutableColor_init_0();
  }
  SequenceButtonUi.prototype.onRender_aemszp$ = function (ctx) {
    var tmp$;
    (Kotlin.isType(tmp$ = this.shader, BasicShader) ? tmp$ : throwCCE()).staticColor.set_czzhhz$(this.bgColor);
    SimpleComponentUi.prototype.onRender_aemszp$.call(this, ctx);
  };
  SequenceButtonUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SequenceButtonUi',
    interfaces: [SimpleComponentUi]
  };
  function SynthieScene(ctx) {
    Scene.call(this);
    this.melody = new Melody();
    this.shaker = new Shaker(60.0);
    this.kick = new Kick(120.0);
    this.pad = new Pad();
    this.audioGen_0 = null;
    this.waveform_0 = new SynthieScene$Waveform(this, 2048, 48000);
    this.unaryPlus_uv0sim$(this.waveform_0);
    this.unaryPlus_uv0sim$(new SynthieScene$Heightmap(this, 256, 256));
    this.unaryPlus_uv0sim$(sphericalInputTransform(void 0, SynthieScene_init$lambda(this)));
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
    Scene.prototype.dispose_aemszp$.call(this, ctx);
  };
  function SynthieScene$Heightmap($outer, width, length) {
    this.$outer = $outer;
    TransformGroup.call(this);
    this.width = width;
    this.length = length;
    this.quads = colorMesh(void 0, SynthieScene$Heightmap$quads$lambda(this));
    this.quadV = this.quads.meshData.get_za3lpa$(0);
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
        }
         else if ($receiver > 0.0) {
          clamp$result = 0.0;
        }
         else {
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
      }
      this.zPos += 1.0;
      if (this.zPos > 1000) {
        this.zPos = -1000.0;
      }
      this.quads.meshData.isSyncRequired = true;
      this.quads.bounds.set_w8lrqs$((-this.width | 0) / 2.0, 0.0, this.zPos - this.length, this.width / 2.0, 50.0, this.zPos);
    }
    this.setIdentity();
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
      $receiver.meshData.usage = 35048;
      $receiver.generator = SynthieScene$Heightmap$quads$lambda$lambda(this$Heightmap);
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
  var Array_0 = Array;
  function SynthieScene$Waveform($outer, points, sampleRate) {
    this.$outer = $outer;
    Group.call(this);
    this.points = points;
    this.sampleRate = sampleRate;
    var array = Array_0(5);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var $receiver = new LineMesh();
      var tmp$_0, tmp$_1;
      this.unaryPlus_uv0sim$($receiver);
      tmp$_0 = this.points;
      for (var i_0 = 1; i_0 <= tmp$_0; i_0++) {
        var $this = $receiver.meshData;
        var idx = {v: 0};
        $this.isSyncRequired = true;
        var $this_0 = $this.vertexList;
        var updateBounds = $this.bounds;
        var tmp$_2, tmp$_0_0, tmp$_1_0;
        $this_0.checkBufferSizes();
        tmp$_2 = $this_0.vertexSizeF;
        for (var i_1 = 1; i_1 <= tmp$_2; i_1++) {
          $this_0.dataF.plusAssign_mx4ult$(0.0);
        }
        tmp$_0_0 = $this_0.vertexSizeI;
        for (var i_0_0 = 1; i_0_0 <= tmp$_0_0; i_0_0++) {
          $this_0.dataI.plusAssign_za3lpa$(0);
        }
        $this_0.vertexIt.index = (tmp$_1_0 = $this_0.size, $this_0.size = tmp$_1_0 + 1 | 0, tmp$_1_0);
        $this_0.vertexIt.position.set_y2kzbl$((i_0 - (this.points / 2 | 0) | 0) / 256.0, 1.0, 0.0);
        updateBounds != null ? updateBounds.add_czzhiu$($this_0.vertexIt.position) : null;
        idx.v = $this_0.size - 1 | 0;
        var idx_0 = idx.v;
        if (i_0 > 1) {
          $receiver.meshData.addIndices_pmhfmb$(new Int32Array([idx_0 - 1 | 0, idx_0]));
        }
      }
      $receiver.lineWidth = 1.0;
      $receiver.shader = basicShader(SynthieScene$Waveform$lines$lambda$lambda$lambda);
      (Kotlin.isType(tmp$_1 = $receiver.shader, BasicShader) ? tmp$_1 : throwCCE()).staticColor.set_czzhhz$(Color.Companion.LIME);
      $receiver.meshData.usage = 35048;
      array[i] = $receiver;
    }
    this.lines = array;
    var array_0 = Array_0(this.lines.length);
    var tmp$_3;
    tmp$_3 = array_0.length - 1 | 0;
    for (var i_2 = 0; i_2 <= tmp$_3; i_2++) {
      array_0[i_2] = this.lines[i_2].meshData.get_za3lpa$(0);
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
    }
  };
  SynthieScene$Waveform.prototype.render_aemszp$ = function (ctx) {
    var tmp$;
    this.playT += ctx.deltaT;
    if ((this.updateFrms = this.updateFrms - 1 | 0, this.updateFrms) === 0) {
      this.updateFrms = 2;
      this.lineIdx = (this.lineIdx + 1 | 0) % this.lines.length;
      this.drawTimeDomain_0();
      this.lines[this.lineIdx].meshData.isSyncRequired = true;
      tmp$ = this.lines;
      for (var i = 0; i !== tmp$.length; ++i) {
        var tmp$_0;
        var idx = this.lineIdx - i | 0;
        if (idx < 0) {
          idx = idx + this.lines.length | 0;
        }
        (Kotlin.isType(tmp$_0 = this.lines[idx].shader, BasicShader) ? tmp$_0 : throwCCE()).staticColor.w = 1.0 - i / this.lines.length;
      }
    }
    Group.prototype.render_aemszp$.call(this, ctx);
  };
  SynthieScene$Waveform.prototype.drawTimeDomain_0 = function () {
    var tmp$, tmp$_0;
    var end = numberToInt(this.playT * this.sampleRate % this.sampleBuf.length);
    var pos = end - this.points | 0;
    if (pos < 0) {
      pos = pos + this.sampleBuf.length | 0;
    }
    tmp$ = this.points;
    for (var i = 0; i < tmp$; i++) {
      this.vertices[this.lineIdx].index = i;
      this.vertices[this.lineIdx].position.y = this.sampleBuf[tmp$_0 = pos, pos = tmp$_0 + 1 | 0, tmp$_0] * 2.0 + 2.0;
      if (pos >= this.sampleBuf.length) {
        pos = 0;
      }
    }
  };
  function SynthieScene$Waveform$lines$lambda$lambda$lambda($receiver) {
    $receiver.colorModel = ColorModel.STATIC_COLOR;
    $receiver.lightModel = LightModel.NO_LIGHTING;
    return Unit;
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
    interfaces: [Scene]
  };
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
        $this.printer(level, tag, 'Generated ' + ($receiver.meshData.numIndices / 3 | 0) + ' trunk triangles in' + ' ' + formatDouble(now() - t, 3) + ' ms');
      }
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_0(this$, closure$ctx) {
    return function ($receiver) {
      $receiver.colorModel = ColorModel.TEXTURE_COLOR;
      $receiver.lightModel = LightModel.PHONG_LIGHTING;
      $receiver.shadowMap = this$.defaultShadowMap;
      $receiver.isNormalMapped = true;
      $receiver.specularIntensity = 0.25;
      var textureProps = TextureProps_init_0('tree_bark.png', 9729, 10497, 16);
      var nrmMapProps = TextureProps_init_0('tree_bark_nrm.png', 9729, 10497, 16);
      $receiver.texture = assetTexture_0(textureProps, closure$ctx);
      $receiver.normalMap = assetTexture_0(nrmMapProps, closure$ctx);
      return Unit;
    };
  }
  function treeScene$lambda$lambda(closure$treeGen, this$, closure$ctx) {
    return function ($receiver) {
      $receiver.generator = treeScene$lambda$lambda$lambda(closure$treeGen);
      $receiver.shader = basicShader(treeScene$lambda$lambda$lambda_0(this$, closure$ctx));
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_1(closure$treeGen, this$) {
    return function ($receiver) {
      var level;
      level = Log$Level.INFO;
      var t = now();
      closure$treeGen.buildLeafMesh_d7s9uf$($receiver, this$.light.direction);
      var ret = Unit;
      var $this = util.Log;
      var tag = Kotlin.getKClassFromExpression($receiver).simpleName;
      if (level.level >= $this.level.level) {
        $this.printer(level, tag, 'Generated ' + ($receiver.meshData.numIndices / 3 | 0) + ' leaf triangles in' + ' ' + formatDouble(now() - t, 3) + ' ms');
      }
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_2(this$, closure$ctx) {
    return function ($receiver) {
      $receiver.colorModel = ColorModel.TEXTURE_COLOR;
      $receiver.lightModel = LightModel.PHONG_LIGHTING;
      $receiver.shadowMap = this$.defaultShadowMap;
      $receiver.specularIntensity = 0.1;
      $receiver.isDiscardTranslucent = true;
      $receiver.texture = assetTexture('leaf.png', closure$ctx);
      return Unit;
    };
  }
  function treeScene$lambda$lambda_0(closure$treeGen, this$, closure$ctx) {
    return function ($receiver) {
      $receiver.generator = treeScene$lambda$lambda$lambda_1(closure$treeGen, this$);
      $receiver.cullMethod = CullMethod.NO_CULLING;
      $receiver.shader = basicShader(treeScene$lambda$lambda$lambda_2(this$, closure$ctx));
      return Unit;
    };
  }
  function treeScene$lambda$lambda_1(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.setMouseRotation_dleff0$(0.0, -30.0);
      $receiver.minZoom = 1.0;
      $receiver.maxZoom = 25.0;
      $receiver.translationBounds = BoundingBox_init(new Vec3f(-10.0, -10.0, -10.0), new Vec3f(10.0, 10.0, 10.0));
      $receiver.translate_y2kzbl$(0.0, 2.0, 0.0);
      return Unit;
    };
  }
  function treeScene$disableCamDrag$lambda(closure$treeScene) {
    return function ($receiver, f, f_0, f_1) {
      closure$treeScene.isPickingEnabled = false;
      return Unit;
    };
  }
  function treeScene$disableCamDrag$lambda_0(closure$treeScene) {
    return function ($receiver, f, f_0, f_1) {
      closure$treeScene.isPickingEnabled = true;
      return Unit;
    };
  }
  function treeScene$disableCamDrag(closure$treeScene) {
    return function ($receiver) {
      var $receiver_0 = $receiver.onHoverEnter;
      var element = treeScene$disableCamDrag$lambda(closure$treeScene);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = $receiver.onHoverExit;
      var element_0 = treeScene$disableCamDrag$lambda_0(closure$treeScene);
      $receiver_1.add_11rb$(element_0);
    };
  }
  function treeScene$lambda$lambda$lambda_3(it) {
    return new BlankComponentUi();
  }
  function treeScene$lambda$lambda$lambda_4(it) {
    return new BlankComponentUi();
  }
  function treeScene$lambda$lambda_2($receiver) {
    $receiver.componentUi_mloaa0$(treeScene$lambda$lambda$lambda_3);
    $receiver.containerUi_2t3ptw$(treeScene$lambda$lambda$lambda_4);
    return Unit;
  }
  function treeScene$lambda$lambda$lambda_5($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(110.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    return Unit;
  }
  function treeScene$lambda$lambda$lambda_6(closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(380.0, true), dps(110.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(50.0, true), dps(35.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      $receiver.text = formatFloat(closure$treeGen.growDistance, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda(closure$treeGen, closure$growDistVal) {
    return function ($receiver, value) {
      closure$treeGen.growDistance = value;
      closure$growDistVal.text = formatFloat(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_7(closure$treeGen, closure$disableCamDrag, closure$growDistVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(200.0, true), dps(110.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
      $receiver.setValue_y2kzbl$(0.05, 0.4, closure$treeGen.growDistance);
      closure$disableCamDrag($receiver);
      $receiver.onValueChanged = plus($receiver.onValueChanged, treeScene$lambda$lambda$lambda$lambda(closure$treeGen, closure$growDistVal));
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_8($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(75.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    return Unit;
  }
  function treeScene$lambda$lambda$lambda_9(closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(380.0, true), dps(75.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(50.0, true), dps(35.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      $receiver.text = formatFloat(closure$treeGen.killDistance, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_0(closure$treeGen, closure$killDistVal) {
    return function ($receiver, value) {
      closure$treeGen.killDistance = value;
      closure$killDistVal.text = formatFloat(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_10(closure$treeGen, closure$disableCamDrag, closure$killDistVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(200.0, true), dps(75.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
      $receiver.setValue_y2kzbl$(1.0, 4.0, closure$treeGen.killDistance);
      closure$disableCamDrag($receiver);
      $receiver.onValueChanged = plus($receiver.onValueChanged, treeScene$lambda$lambda$lambda$lambda_0(closure$treeGen, closure$killDistVal));
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_11($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(40.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    return Unit;
  }
  function treeScene$lambda$lambda$lambda_12(closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(380.0, true), dps(40.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(50.0, true), dps(35.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      $receiver.text = closure$treeGen.numberOfAttractionPoints.toString();
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_1(closure$treeGen, closure$attractPtsVal) {
    return function ($receiver, value) {
      closure$treeGen.numberOfAttractionPoints = numberToInt(value);
      closure$attractPtsVal.text = numberToInt(value).toString();
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_13(closure$treeGen, closure$disableCamDrag, closure$attractPtsVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(200.0, true), dps(40.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
      $receiver.setValue_y2kzbl$(100.0, 10000.0, closure$treeGen.numberOfAttractionPoints);
      closure$disableCamDrag($receiver);
      $receiver.onValueChanged = plus($receiver.onValueChanged, treeScene$lambda$lambda$lambda$lambda_1(closure$treeGen, closure$attractPtsVal));
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_14($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(0.0, true), dps(5.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
    $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
    return Unit;
  }
  function treeScene$lambda$lambda$lambda_15(closure$treeGen) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(380.0, true), dps(5.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(50.0, true), dps(35.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      $receiver.text = formatFloat(closure$treeGen.radiusOfInfluence, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_2(closure$treeGen, closure$infRadiusVal) {
    return function ($receiver, value) {
      closure$treeGen.radiusOfInfluence = value;
      closure$infRadiusVal.text = formatFloat(value, 2);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_16(closure$treeGen, closure$disableCamDrag, closure$infRadiusVal) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(200.0, true), dps(5.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(200.0, true), dps(35.0, true), zero());
      $receiver.setValue_y2kzbl$(0.25, 10.0, closure$treeGen.radiusOfInfluence);
      closure$disableCamDrag($receiver);
      $receiver.onValueChanged = plus($receiver.onValueChanged, treeScene$lambda$lambda$lambda$lambda_2(closure$treeGen, closure$infRadiusVal));
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_3(closure$treeGen, closure$trunkMesh, closure$leafMesh) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$, tmp$_0;
      closure$treeGen.generate_za3lpa$();
      if ((tmp$ = closure$trunkMesh.v) != null) {
        var closure$treeGen_0 = closure$treeGen;
        var $this = tmp$.meshData;
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
          $this_0.printer(level, tag, 'Generated ' + ($this.numIndices / 3 | 0) + ' trunk triangles in' + ' ' + formatDouble(now() - t, 3) + ' ms');
        }
        $this.isSyncRequired = true;
        $this.isBatchUpdate = wasBatchUpdate;
      }
      if ((tmp$_0 = closure$leafMesh.v) != null) {
        var closure$treeGen_1 = closure$treeGen;
        var $this_1 = tmp$_0.meshData;
        var wasBatchUpdate_0 = $this_1.isBatchUpdate;
        $this_1.isBatchUpdate = true;
        $this_1.clear();
        var builder_0 = new MeshBuilder($this_1);
        var level_0;
        level_0 = Log$Level.INFO;
        var t_0 = now();
        var tmp$_1, tmp$_2, tmp$_3;
        closure$treeGen_1.buildLeafMesh_d7s9uf$(builder_0, (tmp$_3 = (tmp$_2 = (tmp$_1 = tmp$_0.scene) != null ? tmp$_1.light : null) != null ? tmp$_2.direction : null) != null ? tmp$_3 : Vec3f.Companion.ZERO);
        var ret_0 = Unit;
        var $this_2 = util.Log;
        var tag_0 = Kotlin.getKClassFromExpression($this_1).simpleName;
        if (level_0.level >= $this_2.level.level) {
          $this_2.printer(level_0, tag_0, 'Generated ' + ($this_1.numIndices / 3 | 0) + ' leaf triangles in' + ' ' + formatDouble(now() - t_0, 3) + ' ms');
        }
        $this_1.isSyncRequired = true;
        $this_1.isBatchUpdate = wasBatchUpdate_0;
      }
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_17(closure$treeGen, closure$trunkMesh, closure$leafMesh) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(470.0, true), dps(110.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(220.0, true), dps(40.0, true), zero());
      $receiver.text = 'Generate Tree!';
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      var $receiver_0 = $receiver.onClick;
      var element = treeScene$lambda$lambda$lambda$lambda_3(closure$treeGen, closure$trunkMesh, closure$leafMesh);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda$lambda_4(this$, closure$leafMesh) {
    return function ($receiver, f, f_0, f_1) {
      var tmp$;
      (tmp$ = closure$leafMesh.v) != null ? (tmp$.isVisible = this$.isEnabled) : null;
      return Unit;
    };
  }
  function treeScene$lambda$lambda$lambda_18(closure$leafMesh) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(dps(470.0, true), dps(75.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(230.0, true), dps(40.0, true), zero());
      $receiver.text = 'Toggle Leafs';
      $receiver.isEnabled = true;
      var $receiver_0 = $receiver.onClick;
      var element = treeScene$lambda$lambda$lambda$lambda_4($receiver, closure$leafMesh);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function treeScene$lambda$lambda_3(this$, closure$treeGen, closure$disableCamDrag, closure$trunkMesh, closure$leafMesh) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0), dps(150.0), zero());
      $receiver.ui.setCustom_11rb$(new SimpleComponentUi($receiver));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Grow Distance:', treeScene$lambda$lambda$lambda_5));
      var growDistVal = this$.label_tokfmu$('growDistVal', treeScene$lambda$lambda$lambda_6(closure$treeGen));
      $receiver.unaryPlus_uv0sim$(growDistVal);
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('growDist', treeScene$lambda$lambda$lambda_7(closure$treeGen, closure$disableCamDrag, growDistVal)));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Kill Distance:', treeScene$lambda$lambda$lambda_8));
      var killDistVal = this$.label_tokfmu$('killDistVal', treeScene$lambda$lambda$lambda_9(closure$treeGen));
      $receiver.unaryPlus_uv0sim$(killDistVal);
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('killDist', treeScene$lambda$lambda$lambda_10(closure$treeGen, closure$disableCamDrag, killDistVal)));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Attraction Points:', treeScene$lambda$lambda$lambda_11));
      var attractPtsVal = this$.label_tokfmu$('attractPtsVal', treeScene$lambda$lambda$lambda_12(closure$treeGen));
      $receiver.unaryPlus_uv0sim$(attractPtsVal);
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('attractPts', treeScene$lambda$lambda$lambda_13(closure$treeGen, closure$disableCamDrag, attractPtsVal)));
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('Radius of Influence:', treeScene$lambda$lambda$lambda_14));
      var infRadiusVal = this$.label_tokfmu$('killDistVal', treeScene$lambda$lambda$lambda_15(closure$treeGen));
      $receiver.unaryPlus_uv0sim$(infRadiusVal);
      $receiver.unaryPlus_uv0sim$(this$.slider_87iqh3$('killDist', treeScene$lambda$lambda$lambda_16(closure$treeGen, closure$disableCamDrag, infRadiusVal)));
      $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$('generate', treeScene$lambda$lambda$lambda_17(closure$treeGen, closure$trunkMesh, closure$leafMesh)));
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('toggleLeafs', treeScene$lambda$lambda$lambda_18(closure$leafMesh)));
      return Unit;
    };
  }
  function treeScene$lambda(closure$treeGen, closure$disableCamDrag, closure$trunkMesh, closure$leafMesh) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK_SIMPLE, treeScene$lambda$lambda_2);
      $receiver.unaryPlus_uv0sim$($receiver.container_t34sov$('menu', treeScene$lambda$lambda_3($receiver, closure$treeGen, closure$disableCamDrag, closure$trunkMesh, closure$leafMesh)));
      return Unit;
    };
  }
  function treeScene(ctx) {
    var scenes = ArrayList_init();
    var w = 3.0;
    var h = 3.5;
    var dist = new TreeTopPointDistribution(1.0 + h / 2.0, w, h);
    var treeGen = new TreeGenerator(dist);
    treeGen.generate_za3lpa$();
    var trunkMesh = {v: null};
    var leafMesh = {v: null};
    var $receiver = new Scene(null);
    $receiver.defaultShadowMap = CascadedShadowMap.Companion.defaultCascadedShadowMap3();
    $receiver.unaryPlus_uv0sim$(makeGroundGrid(40, ctx, $receiver.defaultShadowMap));
    trunkMesh.v = textureMesh(void 0, true, treeScene$lambda$lambda(treeGen, $receiver, ctx));
    leafMesh.v = textureMesh(void 0, void 0, treeScene$lambda$lambda_0(treeGen, $receiver, ctx));
    $receiver.unaryPlus_uv0sim$(ensureNotNull(trunkMesh.v));
    $receiver.unaryPlus_uv0sim$(ensureNotNull(leafMesh.v));
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, treeScene$lambda$lambda_1($receiver)));
    var treeScene = $receiver;
    scenes.add_11rb$(treeScene);
    var disableCamDrag = treeScene$disableCamDrag(treeScene);
    var element = uiScene(ctx.screenDpi, void 0, treeScene$lambda(treeGen, disableCamDrag, trunkMesh, leafMesh));
    scenes.add_11rb$(element);
    return scenes;
  }
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  function TreeGenerator(distribution, baseTop, baseBot) {
    if (baseTop === void 0)
      baseTop = new Vec3f(0.0, 1.0, 0.0);
    if (baseBot === void 0)
      baseBot = Vec3f.Companion.ZERO;
    this.distribution = distribution;
    this.baseTop = baseTop;
    this.baseBot = baseBot;
    this.radiusOfInfluence = 1.0;
    this.growDistance = 0.15;
    this.killDistance = 1.5;
    this.numberOfAttractionPoints = 3000;
    this.attractionPoints_0 = ArrayList_init();
    this.attractionPointsTree_0 = pointTree(emptyList());
    this.attractionPointTrav_0 = new InRadiusTraverser();
    this.treeNodes_0 = ArrayList_init();
    this.root_0 = new TreeGenerator$TreeNode();
  }
  Object.defineProperty(TreeGenerator.prototype, 'actualKillDistance', {
    get: function () {
      return this.growDistance * this.killDistance;
    }
  });
  TreeGenerator.prototype.seedTree = function () {
    this.populateAttractionPoints_0();
    this.treeNodes_0.clear();
    this.root_0 = new TreeGenerator$TreeNode();
    this.root_0.set_czzhiu$(this.baseBot);
    var $receiver = this.treeNodes_0;
    var element = this.root_0;
    $receiver.add_11rb$(element);
    var d = this.baseTop.subtract_2gj7b4$(this.baseBot, MutableVec3f_init()).norm().scale_mx4ult$(this.growDistance);
    var prev = this.root_0;
    while (prev.distance_czzhiu$(this.baseTop) > this.growDistance) {
      var newNd = new TreeGenerator$TreeNode();
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
      $this.printer(level, tag, 'Generation done, took ' + i.v + ' iterations, ' + this.treeNodes_0.size + ' nodes in' + ' ' + formatDouble(now() - t, 3) + ' ms');
    }
  };
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
        this.attractionPointsTree_0.traverse_klhj8v$(this.attractionPointTrav_0.reset_2qa7tb$(node, this.radiusOfInfluence));
        tmp$_0 = this.attractionPointTrav_0.result.iterator();
        while (tmp$_0.hasNext()) {
          var attracPt = tmp$_0.next();
          if (attracPt.isOpen) {
            attracPt.checkNearest_15eqn9$(node);
          }
        }
      }
    }
    tmp$_1 = this.attractionPoints_0.iterator();
    while (tmp$_1.hasNext()) {
      var attracPt_0 = tmp$_1.next();
      if (attracPt_0.isOpen) {
        (tmp$_3 = (tmp$_2 = attracPt_0.nearestNode) != null ? tmp$_2.influencingPts : null) != null ? tmp$_3.add_11rb$(attracPt_0) : null;
      }
    }
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
        var newNode = new TreeGenerator$TreeNode();
        newNode.set_czzhiu$(node_0).add_czzhiu$(growDir);
        if (!node_0.containsChild_15eqn9$(newNode)) {
          node_0.addChild_15eqn9$(newNode);
          newNodes.add_11rb$(newNode);
          this.attractionPointsTree_0.traverse_klhj8v$(this.attractionPointTrav_0.reset_2qa7tb$(newNode, this.actualKillDistance));
          var tmp$_7;
          tmp$_7 = this.attractionPointTrav_0.result.iterator();
          while (tmp$_7.hasNext()) {
            var element_0 = tmp$_7.next();
            element_0.isOpen = false;
          }
          changed = true;
        }
      }
       else {
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
        $receiver.x = $receiver.x + randomF(-0.01, 0.01);
        $receiver.y = $receiver.y + randomF(-0.01, 0.01);
        $receiver.z = $receiver.z + randomF(-0.01, 0.01);
      }
      $receiver.computeTrunkRadiusAndDepth();
      $receiver.computeCircumPoints();
      return Unit;
    };
  }
  function TreeGenerator$finishTree$lambda_0($receiver) {
    if ($receiver.parent != null) {
      var baseV = ensureNotNull($receiver.parent).texV;
      $receiver.texV = baseV + $receiver.distance_czzhiu$(ensureNotNull($receiver.parent)) / ($receiver.radius * 2.0 * math.PI * 1.5);
    }
    return Unit;
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
  function TreeGenerator$buildLeafMesh$lambda(closure$ld) {
    return function ($receiver) {
      if ($receiver.normal.times_czzhiu$(closure$ld) < 0) {
        $receiver.normal.scale_mx4ult$(-1.0);
      }
      return Unit;
    };
  }
  TreeGenerator.prototype.buildLeafMesh_d7s9uf$ = function (target, lightDir) {
    var oldModFun = target.vertexModFun;
    var ld = lightDir.norm_5s4mqq$(MutableVec3f_init());
    target.vertexModFun = TreeGenerator$buildLeafMesh$lambda(ld);
    var tmp$;
    tmp$ = this.treeNodes_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.buildLeafMesh_84rojv$(target);
    }
    target.vertexModFun = oldModFun;
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
    this.attractionPointsTree_0 = pointTree(this.attractionPoints_0);
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
      }
    }
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
    }
  };
  TreeGenerator$AttractionPoint.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AttractionPoint',
    interfaces: [MutableVec3f]
  };
  function TreeGenerator$TreeNode() {
    MutableVec3f_init(this);
    this.children = ArrayList_init();
    this.parent = null;
    this.branchDepth = 0;
    this.influencingPts = ArrayList_init();
    this.isFinished = false;
    this.radius = 0.005;
    this.texV = 0.0;
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
      }
    }
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
    }
     else {
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
      }
       else {
        var $receiver_0 = this.children;
        var maxBy$result;
        maxBy$break: do {
          var iterator = $receiver_0.iterator();
          if (!iterator.hasNext()) {
            maxBy$result = null;
            break maxBy$break;
          }
          var maxElem = iterator.next();
          var maxValue = maxElem.branchDepth;
          while (iterator.hasNext()) {
            var e = iterator.next();
            var v = e.branchDepth;
            if (Kotlin.compareTo(maxValue, v) < 0) {
              maxElem = e;
              maxValue = v;
            }
          }
          maxBy$result = maxElem;
        }
         while (false);
        tmp$_1 = ((tmp$_0 = (tmp$ = maxBy$result) != null ? tmp$.branchDepth : null) != null ? tmp$_0 : 0) + 1 | 0;
      }
      this.branchDepth = tmp$_1;
    }
  };
  TreeGenerator$TreeNode.prototype.computeCircumPoints = function () {
    var tmp$;
    this.circumPts.clear();
    if (this.parent != null) {
      tmp$ = this.subtract_2gj7b4$(ensureNotNull(this.parent), MutableVec3f_init()).norm();
    }
     else {
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
    var idcs = ArrayList_init();
    if (this.parent != null) {
      if (this.children.isEmpty()) {
        var $this = target.meshData;
        var idx = {v: 0};
        $this.isSyncRequired = true;
        var $this_0 = $this.vertexList;
        var updateBounds = $this.bounds;
        var tmp$, tmp$_0, tmp$_1;
        $this_0.checkBufferSizes();
        tmp$ = $this_0.vertexSizeF;
        for (var i = 1; i <= tmp$; i++) {
          $this_0.dataF.plusAssign_mx4ult$(0.0);
        }
        tmp$_0 = $this_0.vertexSizeI;
        for (var i_0 = 1; i_0 <= tmp$_0; i_0++) {
          $this_0.dataI.plusAssign_za3lpa$(0);
        }
        $this_0.vertexIt.index = (tmp$_1 = $this_0.size, $this_0.size = tmp$_1 + 1 | 0, tmp$_1);
        var $receiver = $this_0.vertexIt;
        $receiver.position.set_czzhiu$(this);
        this.subtract_2gj7b4$(ensureNotNull(this.parent), $receiver.normal).norm();
        $receiver.texCoord.set_dleff0$(0.0, this.texV);
        updateBounds != null ? updateBounds.add_czzhiu$($this_0.vertexIt.position) : null;
        idx.v = $this_0.size - 1 | 0;
        var tipIdx = idx.v;
        for (var i_1 = 0; i_1 <= 8; i_1++) {
          var $this_1 = target.meshData;
          var idx_0 = {v: 0};
          $this_1.isSyncRequired = true;
          var $this_2 = $this_1.vertexList;
          var updateBounds_0 = $this_1.bounds;
          var tmp$_2, tmp$_0_0, tmp$_1_0;
          $this_2.checkBufferSizes();
          tmp$_2 = $this_2.vertexSizeF;
          for (var i_2 = 1; i_2 <= tmp$_2; i_2++) {
            $this_2.dataF.plusAssign_mx4ult$(0.0);
          }
          tmp$_0_0 = $this_2.vertexSizeI;
          for (var i_0_0 = 1; i_0_0 <= tmp$_0_0; i_0_0++) {
            $this_2.dataI.plusAssign_za3lpa$(0);
          }
          $this_2.vertexIt.index = (tmp$_1_0 = $this_2.size, $this_2.size = tmp$_1_0 + 1 | 0, tmp$_1_0);
          var $receiver_0 = $this_2.vertexIt;
          $receiver_0.position.set_czzhiu$(ensureNotNull(this.parent).circumPts.get_za3lpa$(i_1 % 8));
          ensureNotNull(this.parent).circumPts.get_za3lpa$(i_1 % 8).subtract_2gj7b4$(ensureNotNull(this.parent), $receiver_0.normal).norm();
          $receiver_0.texCoord.set_dleff0$(i_1 / 8.0, ensureNotNull(this.parent).texV);
          updateBounds_0 != null ? updateBounds_0.add_czzhiu$($this_2.vertexIt.position) : null;
          idx_0.v = $this_2.size - 1 | 0;
          var element = idx_0.v;
          idcs.add_11rb$(element);
        }
        for (var i_3 = 0; i_3 < 8; i_3++) {
          target.meshData.addTriIndices_qt1dr2$(tipIdx, idcs.get_za3lpa$(i_3), idcs.get_za3lpa$(i_3 + 1 | 0));
        }
      }
       else {
        for (var i_4 = 0; i_4 <= 8; i_4++) {
          var $this_3 = target.meshData;
          var idx_1 = {v: 0};
          $this_3.isSyncRequired = true;
          var $this_4 = $this_3.vertexList;
          var updateBounds_1 = $this_3.bounds;
          var tmp$_3, tmp$_0_1, tmp$_1_1;
          $this_4.checkBufferSizes();
          tmp$_3 = $this_4.vertexSizeF;
          for (var i_5 = 1; i_5 <= tmp$_3; i_5++) {
            $this_4.dataF.plusAssign_mx4ult$(0.0);
          }
          tmp$_0_1 = $this_4.vertexSizeI;
          for (var i_0_1 = 1; i_0_1 <= tmp$_0_1; i_0_1++) {
            $this_4.dataI.plusAssign_za3lpa$(0);
          }
          $this_4.vertexIt.index = (tmp$_1_1 = $this_4.size, $this_4.size = tmp$_1_1 + 1 | 0, tmp$_1_1);
          var $receiver_1 = $this_4.vertexIt;
          $receiver_1.position.set_czzhiu$(this.circumPts.get_za3lpa$(i_4 % 8));
          this.circumPts.get_za3lpa$(i_4 % 8).subtract_2gj7b4$(this, $receiver_1.normal).norm();
          $receiver_1.texCoord.set_dleff0$(i_4 / 8.0, this.texV);
          updateBounds_1 != null ? updateBounds_1.add_czzhiu$($this_4.vertexIt.position) : null;
          idx_1.v = $this_4.size - 1 | 0;
          var element_0 = idx_1.v;
          idcs.add_11rb$(element_0);
          var $this_5 = target.meshData;
          var idx_2 = {v: 0};
          $this_5.isSyncRequired = true;
          var $this_6 = $this_5.vertexList;
          var updateBounds_2 = $this_5.bounds;
          var tmp$_4, tmp$_0_2, tmp$_1_2;
          $this_6.checkBufferSizes();
          tmp$_4 = $this_6.vertexSizeF;
          for (var i_6 = 1; i_6 <= tmp$_4; i_6++) {
            $this_6.dataF.plusAssign_mx4ult$(0.0);
          }
          tmp$_0_2 = $this_6.vertexSizeI;
          for (var i_0_2 = 1; i_0_2 <= tmp$_0_2; i_0_2++) {
            $this_6.dataI.plusAssign_za3lpa$(0);
          }
          $this_6.vertexIt.index = (tmp$_1_2 = $this_6.size, $this_6.size = tmp$_1_2 + 1 | 0, tmp$_1_2);
          var $receiver_2 = $this_6.vertexIt;
          $receiver_2.position.set_czzhiu$(ensureNotNull(this.parent).circumPts.get_za3lpa$(i_4 % 8));
          ensureNotNull(this.parent).circumPts.get_za3lpa$(i_4 % 8).subtract_2gj7b4$(ensureNotNull(this.parent), $receiver_2.normal).norm();
          $receiver_2.texCoord.set_dleff0$(i_4 / 8.0, ensureNotNull(this.parent).texV);
          updateBounds_2 != null ? updateBounds_2.add_czzhiu$($this_6.vertexIt.position) : null;
          idx_2.v = $this_6.size - 1 | 0;
          var element_1 = idx_2.v;
          idcs.add_11rb$(element_1);
        }
        for (var i_7 = 0; i_7 < 8; i_7++) {
          target.meshData.addTriIndices_qt1dr2$(idcs.get_za3lpa$(i_7 * 2 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 1 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 2 | 0));
          target.meshData.addTriIndices_qt1dr2$(idcs.get_za3lpa$((i_7 * 2 | 0) + 1 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 3 | 0), idcs.get_za3lpa$((i_7 * 2 | 0) + 2 | 0));
        }
      }
    }
  };
  TreeGenerator$TreeNode.prototype.buildLeafMesh_84rojv$ = function (target) {
    if (this.branchDepth <= 1 && this.parent != null) {
      var n = this.subtract_2gj7b4$(ensureNotNull(this.parent), MutableVec3f_init());
      var len = n.length();
      n.norm();
      for (var i = 1; i <= 20; i++) {
        target.transform.push();
        var r = MutableVec3f_init_0(this.circumPts.get_za3lpa$(0)).subtract_czzhiu$(this).norm().scale_mx4ult$(this.radius + randomF(0.0, 0.15));
        r.rotate_ad55pp$(randomF(0.0, 360.0), n);
        var p = MutableVec3f_init_0(n).scale_mx4ult$(randomF(0.0, len)).add_czzhiu$(r).add_czzhiu$(this);
        target.translate_czzhiu$(p);
        target.rotate_ad55pp$(randomF(0.0, 360.0), n);
        var i0 = target.vertex_n440gp$(new Vec3f(0.0, -0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(0.0, 0.0));
        var i1 = target.vertex_n440gp$(new Vec3f(0.0, 0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(0.0, 1.0));
        var i2 = target.vertex_n440gp$(new Vec3f(0.1, 0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(1.0, 1.0));
        var i3 = target.vertex_n440gp$(new Vec3f(0.1, -0.022, 0.0), Vec3f.Companion.NEG_Z_AXIS, new Vec2f(1.0, 0.0));
        target.meshData.addIndices_pmhfmb$(new Int32Array([i0, i1, i2, i0, i2, i3]));
        target.transform.pop();
      }
    }
  };
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
    this.tmpPt2_0 = MutableVec2f_init_0();
    this.e00_0 = MutableVec2f_init_0();
    this.e01_0 = MutableVec2f_init_0();
    this.e10_0 = MutableVec2f_init_0();
    this.e11_0 = MutableVec2f_init_0();
    var tmp$;
    for (var j = 1; j <= 8; j++) {
      var spline = new BSplineVec2f(3);
      var n = 7;
      for (var i = 0; i <= n; i++) {
        var a = i / n * math.PI;
        if (1 <= i && i <= (n - 1 | 0)) {
          tmp$ = randomF(0.4, 0.6);
        }
         else {
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
        var element_0 = spline.evaluate_f6p79m$(i_0 / m, MutableVec2f_init_0());
        pts.add_11rb$(element_0);
      }
      this.borders_0.add_11rb$(pts);
    }
  }
  TreeTopPointDistribution.prototype.drawBorders_acte6c$ = function (target) {
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
  TreeTopPointDistribution.prototype.nextPoint = function () {
    var w = this.width * 0.5;
    var h = this.height * 0.5;
    while (true) {
      this.tmpPt1_0.set_y2kzbl$(this.random_0.randomF_dleff0$(-w, w), this.centerY + this.random_0.randomF_dleff0$(-h, h), randomF(-w, w));
      var x = this.tmpPt1_0.x * this.tmpPt1_0.x + this.tmpPt1_0.z * this.tmpPt1_0.z;
      var px = Math_0.sqrt(x);
      var py = this.tmpPt1_0.y;
      var y = this.tmpPt1_0.z;
      var x_0 = this.tmpPt1_0.x;
      var $receiver = Math_0.atan2(y, x_0) / (2.0 * math.PI) + 0.5;
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
      var a = clamp$result * this.borders_0.size;
      var i0 = numberToInt(a);
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
      }
    }
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
      }
    }
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
    }
     else if (l > 1) {
      var dx_0 = e1.x - px;
      var dy_0 = e1.y - py;
      var x_0 = dx_0 * dx_0 + dy_0 * dy_0;
      tmp$ = Math_0.sqrt(x_0);
    }
     else {
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
    $receiver_0.centerOrigin();
    $receiver_0.colorCube();
    $receiver.cube_lhbb6w$($receiver.cubeProps);
    return Unit;
  }
  function uiDemoScene$lambda$lambda$lambda_0($receiver) {
    $receiver.generator = uiDemoScene$lambda$lambda$lambda$lambda;
    return Unit;
  }
  function uiDemoScene$lambda$lambda_0($receiver) {
    $receiver.onPreRender.add_11rb$(uiDemoScene$lambda$lambda$lambda($receiver));
    $receiver.unaryPlus_uv0sim$(colorMesh(void 0, uiDemoScene$lambda$lambda$lambda_0));
    return Unit;
  }
  function uiDemoScene$lambda$lambda_1($receiver) {
    $receiver.globalWidth = 10.0;
    $receiver.globalHeight = 10.0;
    uiDemoContent($receiver.content, $receiver);
    return Unit;
  }
  function uiDemoScene() {
    var $receiver = new Scene('UI Demo');
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, uiDemoScene$lambda$lambda($receiver)));
    $receiver.unaryPlus_uv0sim$(transformGroup(void 0, uiDemoScene$lambda$lambda_0));
    $receiver.unaryPlus_uv0sim$(embeddedUi(dps(400.0), void 0, uiDemoScene$lambda$lambda_1));
    return $receiver;
  }
  function uiDemoContent$lambda$lambda($receiver, value) {
    $receiver.root.content.alpha = value;
    return Unit;
  }
  function uiDemoContent$lambda$lambda_0(closure$uiRoot) {
    return function ($receiver, f, f_0, f_1) {
      if (equals(closure$uiRoot.theme, UiTheme.Companion.DARK)) {
        closure$uiRoot.theme = UiTheme.Companion.LIGHT;
      }
       else {
        closure$uiRoot.theme = UiTheme.Companion.DARK;
      }
      return Unit;
    };
  }
  function uiDemoContent($receiver, uiRoot) {
    $receiver.translate_y2kzbl$(-uiRoot.globalWidth / 2, -uiRoot.globalHeight / 2, 0.0);
    var $receiver_0 = new ToggleButton('toggle-button', $receiver.root);
    $receiver_0.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-25.0), uns(0.0));
    $receiver_0.layoutSpec.setSize_4ujscr$(pcs(70.0), pcs(15.0), uns(0.0));
    $receiver_0.text = 'Toggle Button';
    $receiver.unaryPlus_uv0sim$($receiver_0);
    var $receiver_1 = new Label('label', $receiver.root);
    $receiver_1.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-45.0), uns(0.0));
    $receiver_1.layoutSpec.setSize_4ujscr$(pcs(20.0), pcs(15.0), uns(0.0));
    $receiver_1.text = 'Slider';
    $receiver.unaryPlus_uv0sim$($receiver_1);
    var $receiver_2 = new Slider('slider', 0.4, 1.0, 1.0, $receiver.root);
    $receiver_2.layoutSpec.setOrigin_4ujscr$(pcs(35.0), pcs(-45.0), uns(0.0));
    $receiver_2.layoutSpec.setSize_4ujscr$(pcs(50.0), pcs(15.0), uns(0.0));
    $receiver_2.padding.left = uns(0.0);
    $receiver_2.onValueChanged = plus($receiver_2.onValueChanged, uiDemoContent$lambda$lambda);
    $receiver.unaryPlus_uv0sim$($receiver_2);
    var $receiver_3 = new TextField('text-field', $receiver.root);
    $receiver_3.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-65.0), uns(0.0));
    $receiver_3.layoutSpec.setSize_4ujscr$(pcs(70.0), pcs(15.0), uns(0.0));
    $receiver.unaryPlus_uv0sim$($receiver_3);
    var $receiver_4 = new Button('toggle-theme', $receiver.root);
    $receiver_4.layoutSpec.setOrigin_4ujscr$(pcs(15.0), pcs(-85.0), uns(0.0));
    $receiver_4.layoutSpec.setSize_4ujscr$(pcs(70.0), pcs(15.0), uns(0.0));
    $receiver_4.text = 'Toggle Theme';
    $receiver_4.onClick.add_11rb$(uiDemoContent$lambda$lambda_0(uiRoot));
    $receiver.unaryPlus_uv0sim$($receiver_4);
  }
  function main() {
    var ctx = createContext();
    ctx.assetMgr.assetsBaseDir = '../assets';
    new Demo(ctx, getParams().get_11rb$('demo'));
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
        }
      }
    }
    return params;
  }
  $$importsForInline$$.kool = $module$kool;
  var package$de = _.de || (_.de = {});
  var package$fabmax = package$de.fabmax || (package$de.fabmax = {});
  var package$kool = package$fabmax.kool || (package$fabmax.kool = {});
  var package$demo = package$kool.demo || (package$kool.demo = {});
  package$demo.basicCollisionDemo_aemszp$ = basicCollisionDemo;
  package$demo.collisionDemo_aemszp$ = collisionDemo;
  package$demo.BoxWorld = BoxWorld;
  package$demo.Demo = Demo;
  Object.defineProperty(Globe, 'Companion', {
    get: Globe$Companion_getInstance
  });
  var package$globe = package$demo.globe || (package$demo.globe = {});
  package$globe.Globe = Globe;
  package$globe.globeScene_aemszp$ = globeScene;
  package$globe.GlobeUi = GlobeUi;
  Object.defineProperty(GlobeDragHandler, 'Companion', {
    get: GlobeDragHandler$Companion_getInstance
  });
  package$globe.GlobeDragHandler = GlobeDragHandler;
  Object.defineProperty(TileFrame, 'Companion', {
    get: TileFrame$Companion_getInstance
  });
  package$globe.TileFrame = TileFrame;
  package$globe.TileMesh = TileMesh;
  package$globe.TileMeshGenerator = TileMeshGenerator;
  package$globe.FlatTileMeshGenerator = FlatTileMeshGenerator;
  Object.defineProperty(TileName, 'Companion', {
    get: TileName$Companion_getInstance
  });
  package$globe.TileName = TileName;
  package$globe.LatLon = LatLon;
  package$globe.TileShaderProvider = TileShaderProvider;
  package$globe.TexImageTileShaderProvider = TexImageTileShaderProvider;
  package$globe.OsmTexImageTileShaderProvider = OsmTexImageTileShaderProvider;
  package$demo.makeGroundGrid_l8pxk$ = makeGroundGrid;
  package$demo.modelScene_aemszp$ = modelScene;
  package$demo.multiScene_aemszp$ = multiScene;
  package$demo.pointScene = pointScene;
  package$demo.makePointMesh = makePointMesh;
  package$demo.makeBillboardPointMesh = makeBillboardPointMesh;
  package$demo.MeshPoint = MeshPoint;
  package$demo.simpleShapesScene_aemszp$ = simpleShapesScene;
  package$demo.synthieScene_aemszp$ = synthieScene;
  package$demo.treeScene_aemszp$ = treeScene;
  package$demo.TreeGenerator = TreeGenerator;
  package$demo.TreeTopPointDistribution = TreeTopPointDistribution;
  package$demo.uiDemoScene = uiDemoScene;
  package$demo.uiDemoContent_d6jo3u$ = uiDemoContent;
  _.main = main;
  _.getParams = getParams;
  BOX_COLORS = listOf([Color.Companion.MD_YELLOW, Color.Companion.MD_AMBER, Color.Companion.MD_ORANGE, Color.Companion.MD_DEEP_ORANGE, Color.Companion.MD_RED, Color.Companion.MD_PINK, Color.Companion.MD_PURPLE, Color.Companion.MD_DEEP_PURPLE, Color.Companion.MD_INDIGO, Color.Companion.MD_BLUE, Color.Companion.MD_LIGHT_BLUE, Color.Companion.MD_CYAN, Color.Companion.MD_TEAL, Color.Companion.MD_GREEN, Color.Companion.MD_LIGHT_GREEN, Color.Companion.MD_LIME]);
  Kotlin.defineModule('kooldemo', _);
  return _;
});
