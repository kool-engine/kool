define(['exports', 'kotlin', 'kool'], function (_, Kotlin, $module$kool) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var createContext = $module$kool.de.fabmax.kool.createContext;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var CascadedShadowMap = $module$kool.de.fabmax.kool.util.CascadedShadowMap;
  var Unit = Kotlin.kotlin.Unit;
  var sphericalInputTransform = $module$kool.de.fabmax.kool.scene.sphericalInputTransform_6sxffc$;
  var ColorModel = $module$kool.de.fabmax.kool.shading.ColorModel;
  var LightModel = $module$kool.de.fabmax.kool.shading.LightModel;
  var gl = $module$kool.de.fabmax.kool.gl;
  var TextureProps_init = $module$kool.de.fabmax.kool.TextureProps_init_3m52m6$;
  var assetTexture = $module$kool.de.fabmax.kool.assetTexture_513zl8$;
  var basicShader = $module$kool.de.fabmax.kool.shading.basicShader_n50u2h$;
  var Color = $module$kool.de.fabmax.kool.util.Color;
  var Vec3f = $module$kool.de.fabmax.kool.math.Vec3f;
  var lineMesh = $module$kool.de.fabmax.kool.util.lineMesh_6a24eg$;
  var CollisionWorld = $module$kool.de.fabmax.kool.physics.CollisionWorld;
  var uniformMassBox = $module$kool.de.fabmax.kool.physics.uniformMassBox_7b5o5w$;
  var staticBox = $module$kool.de.fabmax.kool.physics.staticBox_y2kzbl$;
  var BoxMesh = $module$kool.de.fabmax.kool.physics.BoxMesh;
  var UiTheme = $module$kool.de.fabmax.kool.scene.ui.UiTheme;
  var BlankComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlankComponentUi;
  var BlurredComponentUi = $module$kool.de.fabmax.kool.scene.ui.BlurredComponentUi;
  var getCallableRef = Kotlin.getCallableRef;
  var theme = $module$kool.de.fabmax.kool.scene.ui.theme_vvurn$;
  var dps = $module$kool.de.fabmax.kool.scene.ui.dps_8ca0d4$;
  var zero = $module$kool.de.fabmax.kool.scene.ui.zero;
  var pcs = $module$kool.de.fabmax.kool.scene.ui.pcs_8ca0d4$;
  var Alignment = $module$kool.de.fabmax.kool.scene.ui.Alignment;
  var Gravity = $module$kool.de.fabmax.kool.scene.ui.Gravity;
  var titleFont = $module$kool.de.fabmax.kool.scene.ui.titleFont_mt8j6u$;
  var SimpleComponentUi = $module$kool.de.fabmax.kool.scene.ui.SimpleComponentUi;
  var uiScene = $module$kool.de.fabmax.kool.scene.ui.uiScene_7c31we$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var debugOverlay = $module$kool.de.fabmax.kool.util.debugOverlay_n8mrtu$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mutableMapOf = Kotlin.kotlin.collections.mutableMapOf_qfcya0$;
  var ToggleButtonUi = $module$kool.de.fabmax.kool.scene.ui.ToggleButtonUi;
  var dp = $module$kool.de.fabmax.kool.scene.ui.dp_wl4j30$;
  var InterpolatedFloat = $module$kool.de.fabmax.kool.util.InterpolatedFloat;
  var CosAnimator = $module$kool.de.fabmax.kool.util.CosAnimator;
  var PerspectiveCamera = $module$kool.de.fabmax.kool.scene.PerspectiveCamera;
  var math = Kotlin.kotlin.math;
  var TransformGroup = $module$kool.de.fabmax.kool.scene.TransformGroup;
  var round = Kotlin.kotlin.math.round_14dthe$;
  var numberToInt = Kotlin.numberToInt;
  var kotlin_js_internal_IntCompanionObject = Kotlin.kotlin.js.internal.IntCompanionObject;
  var abs = Kotlin.kotlin.math.abs_za3lpa$;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Group = $module$kool.de.fabmax.kool.scene.Group;
  var MutableVec3f_init = $module$kool.de.fabmax.kool.math.MutableVec3f_init;
  var Mat4f = $module$kool.de.fabmax.kool.math.Mat4f;
  var Ray = $module$kool.de.fabmax.kool.math.Ray;
  var MutableVec2f_init = $module$kool.de.fabmax.kool.math.MutableVec2f_init;
  var InputManager$DragHandler = $module$kool.de.fabmax.kool.InputManager.DragHandler;
  var SphericalInputTransform$DragMethod = $module$kool.de.fabmax.kool.scene.SphericalInputTransform.DragMethod;
  var SphericalInputTransform$ZoomMethod = $module$kool.de.fabmax.kool.scene.SphericalInputTransform.ZoomMethod;
  var ensureNotNull = Kotlin.ensureNotNull;
  var throwUPAE = Kotlin.throwUPAE;
  var Font = $module$kool.de.fabmax.kool.util.Font;
  var FontProps = $module$kool.de.fabmax.kool.util.FontProps;
  var Margin = $module$kool.de.fabmax.kool.scene.ui.Margin;
  var color = $module$kool.de.fabmax.kool.util.color_61zpoe$;
  var formatDouble = $module$kool.de.fabmax.kool.formatDouble_12fank$;
  var assetTexture_0 = $module$kool.de.fabmax.kool.assetTexture_2gt2x8$;
  var Mesh = $module$kool.de.fabmax.kool.scene.Mesh;
  var Attribute = $module$kool.de.fabmax.kool.shading.Attribute;
  var MeshData_init = $module$kool.de.fabmax.kool.scene.MeshData_init_j0mu7e$;
  var Vec3f_init = $module$kool.de.fabmax.kool.math.Vec3f_init_mx4ult$;
  var Vec2f = $module$kool.de.fabmax.kool.math.Vec2f;
  var MutableVec3f = $module$kool.de.fabmax.kool.math.MutableVec3f;
  var Vec3f_init_0 = $module$kool.de.fabmax.kool.math.Vec3f_init_czzhiu$;
  var textureMesh = $module$kool.de.fabmax.kool.scene.textureMesh_pyaqjj$;
  var group = $module$kool.de.fabmax.kool.scene.group_2ylazs$;
  var loadMesh = $module$kool.de.fabmax.kool.util.serialization.loadMesh_fqrh44$;
  var Armature = $module$kool.de.fabmax.kool.scene.animation.Armature;
  var Label = $module$kool.de.fabmax.kool.scene.ui.Label;
  var uns = $module$kool.de.fabmax.kool.scene.ui.uns_8ca0d4$;
  var formatFloat = $module$kool.de.fabmax.kool.formatFloat_vjorfl$;
  var Slider = $module$kool.de.fabmax.kool.scene.ui.Slider;
  var plus = Kotlin.kotlin.collections.plus_qloxvw$;
  var embeddedUi = $module$kool.de.fabmax.kool.scene.ui.embeddedUi_o1x1d9$;
  var transformGroup = $module$kool.de.fabmax.kool.scene.transformGroup_zaezuq$;
  var KoolContext$Viewport = $module$kool.de.fabmax.kool.KoolContext.Viewport;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var InRadiusTraverser_init = $module$kool.de.fabmax.kool.math.InRadiusTraverser_init_34hdy3$;
  var BillboardMesh = $module$kool.de.fabmax.kool.util.BillboardMesh;
  var randomF = $module$kool.de.fabmax.kool.math.randomF_dleff0$;
  var PerfTimer = $module$kool.de.fabmax.kool.util.PerfTimer;
  var BoundingBox_init = $module$kool.de.fabmax.kool.util.BoundingBox_init_4lfkt4$;
  var CubicPointDistribution = $module$kool.de.fabmax.kool.math.CubicPointDistribution;
  var pointMesh = $module$kool.de.fabmax.kool.util.pointMesh_h6khem$;
  var pointTree = $module$kool.de.fabmax.kool.math.pointTree_ffk80x$;
  var Pair = Kotlin.kotlin.Pair;
  var Animator = $module$kool.de.fabmax.kool.util.Animator;
  var BasicShader = $module$kool.de.fabmax.kool.shading.BasicShader;
  var throwCCE = Kotlin.throwCCE;
  var LinearAnimator = $module$kool.de.fabmax.kool.util.LinearAnimator;
  var colorMesh = $module$kool.de.fabmax.kool.scene.colorMesh_gp9ews$;
  var textMesh = $module$kool.de.fabmax.kool.scene.textMesh_8mgi8m$;
  var reversed = Kotlin.kotlin.ranges.reversed_zf1xzc$;
  var equals = Kotlin.equals;
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
  var MutableVec3f_init_0 = $module$kool.de.fabmax.kool.math.MutableVec3f_init_czzhiu$;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var InRadiusTraverser = $module$kool.de.fabmax.kool.math.InRadiusTraverser;
  var math_0 = $module$kool.de.fabmax.kool.math;
  var PointDistribution = $module$kool.de.fabmax.kool.math.PointDistribution;
  var BSplineVec2f = $module$kool.de.fabmax.kool.math.BSplineVec2f;
  var MutableVec2f = $module$kool.de.fabmax.kool.math.MutableVec2f;
  var ToggleButton = $module$kool.de.fabmax.kool.scene.ui.ToggleButton;
  var TextField = $module$kool.de.fabmax.kool.scene.ui.TextField;
  MenuButtonUi.prototype = Object.create(ToggleButtonUi.prototype);
  MenuButtonUi.prototype.constructor = MenuButtonUi;
  Earth$TileGroup.prototype = Object.create(Group.prototype);
  Earth$TileGroup.prototype.constructor = Earth$TileGroup;
  Earth.prototype = Object.create(TransformGroup.prototype);
  Earth.prototype.constructor = Earth;
  TileMesh.prototype = Object.create(Mesh.prototype);
  TileMesh.prototype.constructor = TileMesh;
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
  function main() {
    new Demo(createContext(), getParams().get_11rb$('demo'));
  }
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
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
  function collisionDemo$lambda$lambda(this$) {
    return function ($receiver) {
      $receiver.unaryPlus_uv0sim$(this$.camera);
      $receiver.setMouseRotation_dleff0$(20.0, -20.0);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda(this$, closure$ctx) {
    return function ($receiver) {
      $receiver.colorModel = ColorModel.VERTEX_COLOR;
      $receiver.lightModel = LightModel.PHONG_LIGHTING;
      $receiver.shadowMap = this$.defaultShadowMap;
      $receiver.isNormalMapped = true;
      $receiver.specularIntensity = 0.25;
      var props = TextureProps_init('perlin_nrm.png', gl.GL_LINEAR, gl.GL_REPEAT);
      $receiver.normalMap = assetTexture(props, closure$ctx);
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda$lambda_0(this$) {
    return function ($receiver) {
      $receiver.lightModel = LightModel.NO_LIGHTING;
      $receiver.colorModel = ColorModel.VERTEX_COLOR;
      $receiver.shadowMap = this$.defaultShadowMap;
      return Unit;
    };
  }
  function collisionDemo$lambda$lambda_0(this$) {
    return function ($receiver) {
      $receiver.isCastingShadow = false;
      for (var i = -5; i <= 5; i++) {
        var color = Color.Companion.MD_GREY_600.withAlpha_mx4ult$(0.5);
        var y = -1.995;
        $receiver.addLine_b8opkg$(new Vec3f(i, y, -5.0), color, new Vec3f(i, y, 5.0), color);
        $receiver.addLine_b8opkg$(new Vec3f(-5.0, y, i), color, new Vec3f(5.0, y, i), color);
      }
      $receiver.shader = basicShader(collisionDemo$lambda$lambda$lambda_0(this$));
      return Unit;
    };
  }
  function collisionDemo(ctx) {
    var $receiver = new Scene(null);
    $receiver.light.direction.set_y2kzbl$(1.0, 0.8, 0.4);
    $receiver.defaultShadowMap = CascadedShadowMap.Companion.defaultCascadedShadowMap3();
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, collisionDemo$lambda$lambda($receiver)));
    var $receiver_0 = twoBoxes();
    $receiver_0.shader = basicShader(collisionDemo$lambda$lambda$lambda($receiver, ctx));
    $receiver.unaryPlus_uv0sim$($receiver_0);
    $receiver.unaryPlus_uv0sim$(lineMesh(void 0, collisionDemo$lambda$lambda_0($receiver)));
    return $receiver;
  }
  function twoBoxes$lambda$lambda(closure$world, this$) {
    return function ($receiver, ctx) {
      closure$world.stepSimulation_mx4ult$(ctx.deltaT);
      this$.updateBoxes();
      return Unit;
    };
  }
  function twoBoxes() {
    var world = new CollisionWorld();
    world.gravity.set_y2kzbl$(0.0, -1.0, 0.0);
    var box1 = uniformMassBox(1.0, 1.0, 1.0, 1.0);
    box1.name = 'smallBox';
    var $receiver = box1.shape;
    $receiver.center.set_y2kzbl$(0.75, 3.0, 0.0);
    $receiver.transform.rotate_ad55pp$(-10.0, Vec3f.Companion.Z_AXIS);
    $receiver.transform.rotate_ad55pp$(-5.0, Vec3f.Companion.Y_AXIS);
    var box2 = uniformMassBox(2.5, 1.0, 3.0, 7.5);
    box2.name = 'bigBox';
    var $receiver_0 = box2.shape;
    $receiver_0.center.set_y2kzbl$(0.0, 1.5, 0.0);
    $receiver_0.transform.rotate_ad55pp$(5.0, Vec3f.Companion.X_AXIS);
    $receiver_0.transform.rotate_ad55pp$(10.0, Vec3f.Companion.Z_AXIS);
    world.bodies.add_11rb$(box1);
    world.bodies.add_11rb$(box2);
    var tmp$ = world.bodies;
    var $receiver_1 = staticBox(10.0, 0.2, 10.0);
    $receiver_1.centerOfMass.set_y2kzbl$(0.0, -2.1, 0.0);
    $receiver_1.name = 'Ground';
    tmp$.add_11rb$($receiver_1);
    var $receiver_2 = new BoxMesh(world);
    $receiver_2.onPreRender.add_11rb$(twoBoxes$lambda$lambda(world, $receiver_2));
    return $receiver_2;
  }
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var Map = Kotlin.kotlin.collections.Map;
  function Demo(ctx, startScene) {
    if (startScene === void 0)
      startScene = null;
    this.dbgOverlay_0 = debugOverlay(ctx, true);
    this.newScenes_0 = ArrayList_init();
    this.currentScenes_0 = ArrayList_init();
    this.defaultScene_0 = new Demo$DemoEntry('Simple Demo', Demo$defaultScene$lambda);
    this.demos_0 = mutableMapOf([to('simpleDemo', this.defaultScene_0), to('multiDemo', new Demo$DemoEntry('Split Viewport Demo', Demo$demos$lambda)), to('pointDemo', new Demo$DemoEntry('Point Cloud Demo', Demo$demos$lambda_0)), to('synthieDemo', new Demo$DemoEntry('Synthie Demo', Demo$demos$lambda_1)), to('earthDemo', new Demo$DemoEntry('Earth Demo', Demo$demos$lambda_2)), to('modelDemo', new Demo$DemoEntry('Model Demo', Demo$demos$lambda_3)), to('treeDemo', new Demo$DemoEntry('Tree Demo', Demo$demos$lambda_4)), to('boxDemo', new Demo$DemoEntry('Physics Demo', Demo$demos$lambda_5))]);
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
  function Demo$demoOverlay$lambda$lambda_0($receiver) {
    $receiver.layoutSpec.setOrigin_4ujscr$(dps(10.0, true), dps(-50.0, true), zero());
    $receiver.layoutSpec.setSize_4ujscr$(dps(40.0, true), dps(40.0, true), zero());
    return Unit;
  }
  function Demo$demoOverlay$lambda$lambda$lambda_0(this$, closure$ctx) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(-50.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0, true), dps(40.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.CENTER, Alignment.CENTER);
      $receiver.text = 'Demos';
      $receiver.textColor.setCustom_11rb$(this$.theme.accentColor);
      $receiver.font.setCustom_11rb$(titleFont($receiver, closure$ctx));
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda_1(this$) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(pcs(5.0), dps(-60.0, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(90.0), dps(1.0, true), zero());
      var bg = new SimpleComponentUi($receiver);
      bg.color.setCustom_11rb$(this$.theme.accentColor);
      $receiver.ui.setCustom_11rb$(bg);
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda$lambda(closure$demo, this$Demo, closure$ctx, closure$menuButton) {
    return function ($receiver, f, f_0, f_1) {
      closure$demo.value.loadScene(this$Demo.newScenes_0, closure$ctx);
      closure$menuButton.isEnabled = false;
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda$lambda$lambda_2(closure$y, closure$demo, this$Demo, closure$ctx, closure$menuButton) {
    return function ($receiver) {
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), dps(closure$y.v, true), zero());
      $receiver.layoutSpec.setSize_4ujscr$(pcs(100.0, true), dps(30.0, true), zero());
      $receiver.textAlignment = new Gravity(Alignment.START, Alignment.CENTER);
      $receiver.text = closure$demo.value.label;
      closure$y.v -= 35.0;
      var $receiver_0 = $receiver.onClick;
      var element = Demo$demoOverlay$lambda$lambda$lambda$lambda(closure$demo, this$Demo, closure$ctx, closure$menuButton);
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
  function Demo$demoOverlay$lambda$lambda$lambda_3(this$Demo) {
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
  function Demo$demoOverlay$lambda$lambda_1(this$, closure$ctx, this$Demo, closure$menuButton) {
    return function ($receiver) {
      var tmp$;
      $receiver.layoutSpec.setOrigin_4ujscr$(zero(), zero(), zero());
      $receiver.layoutSpec.setSize_4ujscr$(dps(250.0, true), pcs(100.0, true), zero());
      $receiver.alpha = 0.0;
      $receiver.unaryPlus_uv0sim$(this$.label_tokfmu$('title', Demo$demoOverlay$lambda$lambda$lambda_0(this$, closure$ctx)));
      $receiver.unaryPlus_uv0sim$(this$.component_qphi6d$('divider', Demo$demoOverlay$lambda$lambda$lambda_1(this$)));
      var y = {v: -105.0};
      tmp$ = this$Demo.demos_0.entries.iterator();
      while (tmp$.hasNext()) {
        var demo = tmp$.next();
        $receiver.unaryPlus_uv0sim$(this$.button_9zrh0o$(demo.key, Demo$demoOverlay$lambda$lambda$lambda_2(y, demo, this$Demo, closure$ctx, closure$menuButton)));
      }
      $receiver.unaryPlus_uv0sim$(this$.toggleButton_6j87po$('showDbg', Demo$demoOverlay$lambda$lambda$lambda_3(this$Demo)));
      return Unit;
    };
  }
  function Demo$demoOverlay$lambda(closure$ctx, this$Demo) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK, Demo$demoOverlay$lambda$lambda);
      $receiver.content.ui.setCustom_11rb$(new BlankComponentUi());
      var menuButton = $receiver.toggleButton_6j87po$('menuButton', Demo$demoOverlay$lambda$lambda_0);
      var menu = $receiver.container_t34sov$('menu', Demo$demoOverlay$lambda$lambda_1($receiver, closure$ctx, this$Demo, menuButton));
      $receiver.unaryPlus_uv0sim$(menu);
      menuButton.ui.setCustom_11rb$(new MenuButtonUi(menuButton, menu));
      $receiver.unaryPlus_uv0sim$(menuButton);
      return Unit;
    };
  }
  Demo.prototype.demoOverlay_0 = function (ctx) {
    return uiScene(ctx.screenDpi, void 0, Demo$demoOverlay$lambda(ctx, this));
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
    $receiver.addAll_brywnq$(earthScene(it));
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
    $receiver.add_11rb$(collisionDemo(it));
    return Unit;
  }
  Demo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Demo',
    interfaces: []
  };
  function MenuButtonUi(tb, menu) {
    ToggleButtonUi.call(this, tb, new BlankComponentUi());
    this.menu_0 = menu;
    this.menuAnimator_0 = new CosAnimator(new InterpolatedFloat(0.0, 1.0));
  }
  function MenuButtonUi$createUi$lambda(this$MenuButtonUi) {
    return function (v) {
      this$MenuButtonUi.menu_0.setIdentity();
      this$MenuButtonUi.menu_0.translate_y2kzbl$(this$MenuButtonUi.menu_0.posInParent.x + dp(this$MenuButtonUi.tb, -40.0) * (1.0 - v), this$MenuButtonUi.menu_0.posInParent.y, this$MenuButtonUi.menu_0.posInParent.z);
      this$MenuButtonUi.menu_0.alpha = v;
      return Unit;
    };
  }
  function MenuButtonUi$createUi$lambda_0(this$MenuButtonUi) {
    return function ($receiver) {
      this$MenuButtonUi.menuAnimator_0.speed = this$MenuButtonUi.tb.isEnabled ? 1.0 : -1.0;
      return Unit;
    };
  }
  function MenuButtonUi$createUi$lambda_1(this$MenuButtonUi) {
    return function ($receiver) {
      $receiver.colorModel = ColorModel.VERTEX_COLOR;
      $receiver.lightModel = this$MenuButtonUi.tb.root.shaderLightModel;
      return Unit;
    };
  }
  MenuButtonUi.prototype.createUi_aemszp$ = function (ctx) {
    ToggleButtonUi.prototype.createUi_aemszp$.call(this, ctx);
    this.knobAnimator.duration = 0.5;
    this.knobAnimator.value.onUpdate;
    this.menuAnimator_0.duration = 0.25;
    this.menuAnimator_0.speed = -1.0;
    this.menuAnimator_0.value.onUpdate = MenuButtonUi$createUi$lambda(this);
    this.tb.onStateChange.add_11rb$(MenuButtonUi$createUi$lambda_0(this));
    this.mesh.shader = basicShader(MenuButtonUi$createUi$lambda_1(this));
  };
  MenuButtonUi.prototype.onRender_aemszp$ = function (ctx) {
    ToggleButtonUi.prototype.onRender_aemszp$.call(this, ctx);
    this.menuAnimator_0.tick_aemszp$(ctx);
  };
  var Math_0 = Math;
  MenuButtonUi.prototype.updateUi_aemszp$ = function (ctx) {
    var hw = this.tb.width * 0.5;
    var hh = this.tb.height * 0.18;
    var hx = -hw / 2.0;
    var ph = dp(this.tb, 2.5);
    this.updateTextColor();
    this.tb.setupBuilder_84rojv$(this.meshBuilder);
    var $receiver = this.meshBuilder;
    $receiver.color = new Color(0.0, 0.0, 0.0, 0.0);
    var $receiver_0 = $receiver.circleProps.defaults();
    var a = this.tb.width;
    var b = this.tb.height;
    $receiver_0.radius = Math_0.min(a, b) / 2.0;
    $receiver_0.center.set_y2kzbl$(this.tb.width / 2.0, this.tb.height / 2.0, -4.0);
    $receiver_0.steps = 30;
    $receiver.circle_59f34t$($receiver.circleProps);
    var tx = this.knobAnimator.value.value * -hw * 0.1;
    var w = hw - this.knobAnimator.value.value * hw * 0.4;
    $receiver.color = this.textColor;
    $receiver.translate_y2kzbl$(this.tb.width / 2.0, this.tb.height / 2.0, 0.0);
    $receiver.rotate_ad55pp$(180.0 - this.knobAnimator.value.value * 180.0, Vec3f.Companion.Z_AXIS);
    $receiver.transform.push();
    $receiver.translate_y2kzbl$(tx, hh, 0.0);
    $receiver.rotate_ad55pp$(this.knobAnimator.value.value * 45.0, Vec3f.Companion.Z_AXIS);
    var $receiver_1 = $receiver.rectProps.defaults();
    $receiver_1.origin.set_y2kzbl$(hx, -ph / 2.0, 0.0);
    $receiver_1.size.set_dleff0$(w, ph);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    $receiver.transform.pop();
    var $receiver_2 = $receiver.rectProps.defaults();
    $receiver_2.origin.set_y2kzbl$(hx, -ph / 2.0, 0.0);
    $receiver_2.size.set_dleff0$(hw, ph);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    $receiver.transform.push();
    $receiver.translate_y2kzbl$(tx, -hh, 0.0);
    $receiver.rotate_ad55pp$(this.knobAnimator.value.value * -45.0, Vec3f.Companion.Z_AXIS);
    var $receiver_3 = $receiver.rectProps.defaults();
    $receiver_3.origin.set_y2kzbl$(hx, -ph / 2.0, 0.0);
    $receiver_3.size.set_dleff0$(w, ph);
    $receiver.rect_e5k3t5$($receiver.rectProps);
    $receiver.transform.pop();
  };
  MenuButtonUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MenuButtonUi',
    interfaces: [ToggleButtonUi]
  };
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  function Earth(name) {
    Earth$Companion_getInstance();
    if (name === void 0)
      name = null;
    TransformGroup.call(this, name);
    this.meterPerPxLvl0 = 156000.0;
    this.centerLat_tivji2$_0 = 0.0;
    this.centerLon_tivtqm$_0 = 0.0;
    this.cameraHeight_tezuws$_0 = 0.0;
    this.attribution = '\xA9 OpenStreetMap';
    this.attributionUrl = 'http://www.openstreetmap.org/copyright';
    this.tileGroup_0 = new Earth$TileGroup();
    this.zoomGroups_0 = ArrayList_init();
    this.tiles_0 = LinkedHashMap_init();
    this.removableTiles_0 = LinkedHashMap_init();
    this.loadingTiles_0 = LinkedHashSet_init();
    this.removeTiles_0 = ArrayList_init();
    this.camPosition_0 = MutableVec3f_init();
    this.camDirection_0 = MutableVec3f_init();
    this.startTransform_0 = new Mat4f();
    this.ptOrientation_0 = new Mat4f();
    this.mouseRotationStart_0 = new Mat4f();
    this.pickRay_0 = new Ray();
    this.isDragging_0 = false;
    this.tmpVec_0 = MutableVec3f_init();
    this.tmpVecRt_0 = MutableVec3f_init();
    this.tmpVecUp_0 = MutableVec3f_init();
    this.tmpVecY_0 = MutableVec3f_init();
    this.steadyScreenPt_0 = MutableVec2f_init();
    this.steadyScreenPtMode_0 = Earth$Companion_getInstance().STEADY_SCREEN_PT_OFF_0;
    this.center_0 = new TileName(0, 0, 1);
    this.prevCamHeight_0 = 0.0;
    this.prevLat_0 = 0.0;
    this.prevLon_0 = 0.0;
    var tmp$, tmp$_0;
    this.unaryPlus_uv0sim$(this.tileGroup_0);
    tmp$ = Earth$Companion_getInstance().MIN_ZOOM_LEVEL;
    tmp$_0 = Earth$Companion_getInstance().MAX_ZOOM_LEVEL;
    for (var i = tmp$; i <= tmp$_0; i++) {
      var zoomGroup = new Group((name != null ? name : 'earth') + '-zoom-' + i);
      this.zoomGroups_0.add_11rb$(zoomGroup);
      this.tileGroup_0.plusAssign_f1kmr1$(zoomGroup);
    }
  }
  Object.defineProperty(Earth.prototype, 'centerLat', {
    get: function () {
      return this.centerLat_tivji2$_0;
    },
    set: function (centerLat) {
      this.centerLat_tivji2$_0 = centerLat;
    }
  });
  Object.defineProperty(Earth.prototype, 'centerLon', {
    get: function () {
      return this.centerLon_tivtqm$_0;
    },
    set: function (centerLon) {
      this.centerLon_tivtqm$_0 = centerLon;
    }
  });
  Object.defineProperty(Earth.prototype, 'cameraHeight', {
    get: function () {
      return this.cameraHeight_tezuws$_0;
    },
    set: function (cameraHeight) {
      this.cameraHeight_tezuws$_0 = cameraHeight;
    }
  });
  Earth.prototype.getZoomGroup_0 = function (level) {
    return this.zoomGroups_0.get_za3lpa$(level - Earth$Companion_getInstance().MIN_ZOOM_LEVEL | 0);
  };
  Earth.prototype.setSteadyPoint_dleff0$ = function (screenX, screenY) {
    this.steadyScreenPt_0.set_dleff0$(screenX, screenY);
    this.steadyScreenPtMode_0 = Earth$Companion_getInstance().STEADY_SCREEN_PT_INIT_0;
  };
  Earth.prototype.render_aemszp$ = function (ctx) {
    var tmp$, tmp$_0;
    var cam = (tmp$ = this.scene) != null ? tmp$.camera : null;
    if (cam != null && Kotlin.isType(cam, PerspectiveCamera)) {
      this.toGlobalCoords_w1lst9$(this.tmpVec_0.set_czzhiu$(Vec3f.Companion.ZERO));
      this.tmpVec_0.subtract_czzhiu$(cam.globalPos);
      this.cameraHeight = this.tmpVec_0.length() - Earth$Companion_getInstance().EARTH_R;
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
      var min = -Earth$Companion_getInstance().RAD_85_0;
      var max = Earth$Companion_getInstance().RAD_85_0;
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
      this.camDirection_0.scale_mx4ult$(Earth$Companion_getInstance().EARTH_R);
      var camHeight = this.camDirection_0.distance_czzhiu$(this.camPosition_0);
      var x_3 = cam.fovy * math_0.DEG_2_RAD * 0.5;
      var meterPerPx = camHeight * Math_0.tan(x_3) * 2.0 / (ctx.viewport.height * 96.0 / ctx.screenDpi);
      var centerZoom = this.getBestZoom_0(meterPerPx, lat);
      var newCenter = TileName$Companion_getInstance().forLatLng_syxxoe$(lat * math_0.RAD_2_DEG, lon * math_0.RAD_2_DEG, centerZoom);
      if (!(newCenter != null ? newCenter.equals(this.center_0) : null) && (this.tiles_0.size < 300 || !isMoving)) {
        this.center_0 = newCenter;
        this.rebuildMesh_0(ctx);
      }
    }
    if (this.steadyScreenPtMode_0 === Earth$Companion_getInstance().STEADY_SCREEN_PT_INIT_0 && this.computePointOrientation_0(this.steadyScreenPt_0.x, this.steadyScreenPt_0.y, ctx)) {
      this.steadyScreenPtMode_0 = Earth$Companion_getInstance().STEADY_SCREEN_PT_HOLD_0;
      this.ptOrientation_0.transpose_d4zu6j$(this.mouseRotationStart_0);
      this.startTransform_0.set_d4zu6j$(this.transform);
    }
     else if (this.steadyScreenPtMode_0 === Earth$Companion_getInstance().STEADY_SCREEN_PT_HOLD_0) {
      this.set_d4zu6j$(this.startTransform_0);
      if (this.computePointOrientation_0(this.steadyScreenPt_0.x, this.steadyScreenPt_0.y, ctx)) {
        this.ptOrientation_0.mul_d4zu6j$(this.mouseRotationStart_0);
      }
       else {
        this.steadyScreenPtMode_0 = Earth$Companion_getInstance().STEADY_SCREEN_PT_OFF_0;
      }
      this.mul_d4zu6j$(this.ptOrientation_0);
    }
    TransformGroup.prototype.render_aemszp$.call(this, ctx);
    if (!this.removeTiles_0.isEmpty()) {
      var tmp$_4;
      tmp$_4 = this.removeTiles_0.iterator();
      while (tmp$_4.hasNext()) {
        var element = tmp$_4.next();
        this.loadingTiles_0.remove_11rb$(element.key);
        this.tiles_0.remove_11rb$(element.key);
        this.removableTiles_0.remove_11rb$(element.key);
        this.getZoomGroup_0(element.tz).removeNode_f1kmr1$(element);
        element.dispose_aemszp$(ctx);
      }
      this.removeTiles_0.clear();
    }
  };
  Earth.prototype.getBestZoom_0 = function (meterPerPx, lat) {
    var x = this.meterPerPxLvl0 / meterPerPx * Math_0.cos(lat);
    var $receiver = round(0.2 + Math_0.log2(x));
    var min = Earth$Companion_getInstance().MIN_ZOOM_LEVEL;
    var max = Earth$Companion_getInstance().MAX_ZOOM_LEVEL;
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
  function Earth$rebuildMesh$lambda(this$Earth) {
    return function (m) {
      if (!m.isTexLoaded) {
        return kotlin_js_internal_IntCompanionObject.MIN_VALUE;
      }
       else {
        return -abs(m.tz - this$Earth.center_0.zoom | 0) | 0;
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
  Earth.prototype.rebuildMesh_0 = function (ctx) {
    var tmp$;
    this.removableTiles_0.putAll_a2k3zr$(this.tiles_0);
    var rng = 5;
    var zoom = this.center_0.zoom;
    var xStart = this.center_0.x - rng + 1 & ~1;
    var xEnd = (this.center_0.x + rng + 1 & ~1) - 1 | 0;
    var yStart = this.center_0.y - rng + 1 & ~1;
    var yEnd = (this.center_0.y + rng + 1 & ~1) - 1 | 0;
    this.addMeshesWrappingX_0(xStart, xEnd, yStart, yEnd, zoom, ctx);
    for (var i = 1; i <= 4; i++) {
      zoom = zoom - 1 | 0;
      if (zoom >= Earth$Companion_getInstance().MIN_ZOOM_LEVEL) {
        var xStShf = xStart >> 1;
        var xEdShf = xEnd + 1 >> 1;
        var yStShf = yStart >> 1;
        var yEdShf = yEnd + 1 >> 1;
        xStart = xStShf - 1 & ~1;
        xEnd = (xEdShf & ~1) + 1 | 0;
        yStart = yStShf - 1 & ~1;
        yEnd = (yEdShf & ~1) + 1 | 0;
        this.addMeshesWrappingX_0(xStart, xStShf - 1 | 0, yStart, yEnd, zoom, ctx);
        this.addMeshesWrappingX_0(xEdShf, xEnd, yStart, yEnd, zoom, ctx);
        this.addMeshesWrappingX_0(xStShf, xEdShf - 1 | 0, yStart, yStShf - 1 | 0, zoom, ctx);
        this.addMeshesWrappingX_0(xStShf, xEdShf - 1 | 0, yEdShf, yEnd, zoom, ctx);
      }
       else {
        break;
      }
    }
    if (this.tiles_0.size > 400) {
      var $receiver = ArrayList_init();
      $receiver.addAll_brywnq$(this.removableTiles_0.values);
      var rmQueue = $receiver;
      if (rmQueue.size > 1) {
        sortWith(rmQueue, new Comparator$ObjectLiteral(compareBy$lambda(Earth$rebuildMesh$lambda(this))));
      }
      tmp$ = this.tiles_0.size - 400 | 0;
      for (var i_0 = 0; i_0 <= tmp$; i_0++) {
        this.removeTileMesh_0(rmQueue.get_za3lpa$(i_0), false);
      }
    }
  };
  Earth.prototype.addMeshesWrappingX_0 = function (xStart, xEnd, yStart, yEnd, zoom, ctx) {
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
  Earth.prototype.addMeshes_0 = function (xRng, yRng, zoom, ctx) {
    if ((xRng.last - xRng.first | 0) > 2 && (yRng.last - yRng.first | 0) > 2) {
      this.addMeshesCircular_0(xRng, yRng, zoom, ctx);
    }
     else {
      this.addMeshesRectRange_0(xRng, yRng, zoom, ctx);
    }
  };
  Earth.prototype.addMeshesRectRange_0 = function (xRng, yRng, zoom, ctx) {
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
  Earth.prototype.addMeshesCircular_0 = function (xRng, yRng, zoom, ctx) {
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
  Earth.prototype.addTile_0 = function (x, y, zoom, xRng, yRng, ctx) {
    if (xRng.contains_mef7kx$(x) && yRng.contains_mef7kx$(y)) {
      var key = TileMesh$Companion_getInstance().tileKey_qt1dr2$(x, y, zoom);
      var existing = this.tiles_0.get_11rb$(key);
      if (existing != null) {
        this.removableTiles_0.remove_11rb$(key);
        existing.isFadingOut = false;
        if (!existing.isLoaded) {
          this.loadingTiles_0.add_11rb$(key);
        }
      }
       else {
        var mesh = new TileMesh(this, x, y, zoom, ctx);
        this.tiles_0.put_xwzc9p$(key, mesh);
        this.getZoomGroup_0(zoom).plusAssign_f1kmr1$(mesh);
        this.loadingTiles_0.add_11rb$(key);
      }
    }
  };
  Earth.prototype.tileFadedOut_o2opt$ = function (tileMesh) {
    this.removeTiles_0.add_11rb$(tileMesh);
  };
  Earth.prototype.tileLoaded_o2opt$ = function (tileMesh) {
    this.removeObsoleteTilesBelow_0(tileMesh.tx, tileMesh.ty, tileMesh.tz);
    this.loadingTiles_0.remove_11rb$(tileMesh.key);
    if (this.loadingTiles_0.isEmpty() && !this.removableTiles_0.isEmpty()) {
      var tmp$;
      tmp$ = this.removableTiles_0.values.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        this.removeTileMesh_0(element, true);
      }
      this.removableTiles_0.clear();
    }
  };
  Earth.prototype.removeTileMesh_0 = function (mesh, fadeOut) {
    if (mesh.isCurrentlyVisible && fadeOut) {
      mesh.isFadingOut = true;
    }
     else {
      this.removeTiles_0.add_11rb$(mesh);
    }
  };
  Earth.prototype.removeObsoleteTilesBelow_0 = function (x, y, zoom) {
    var it = this.removableTiles_0.values.iterator();
    while (it.hasNext()) {
      var mesh = it.next();
      if (mesh.tz > zoom) {
        var projX = mesh.tx >> mesh.tz - zoom;
        var projY = mesh.ty >> mesh.tz - zoom;
        if (projX === x && projY === y) {
          this.removeTileMesh_0(mesh, true);
          it.remove();
        }
      }
    }
  };
  Earth.prototype.onSceneChanged_9srkog$ = function (oldScene, newScene) {
    TransformGroup.prototype.onSceneChanged_9srkog$.call(this, oldScene, newScene);
    oldScene != null ? (oldScene.removeDragHandler_dsvxak$(this), Unit) : null;
    newScene != null ? (newScene.registerDragHandler_dsvxak$(this), Unit) : null;
  };
  Earth.prototype.handleDrag_kin2e3$ = function (dragPtrs, ctx) {
    if (dragPtrs.size === 1 && dragPtrs.get_za3lpa$(0).isInViewport_aemszp$(ctx)) {
      var ptrX = dragPtrs.get_za3lpa$(0).x;
      var ptrY = dragPtrs.get_za3lpa$(0).y;
      if (dragPtrs.get_za3lpa$(0).isLeftButtonDown) {
        this.steadyScreenPtMode_0 = Earth$Companion_getInstance().STEADY_SCREEN_PT_OFF_0;
        if (dragPtrs.get_za3lpa$(0).isLeftButtonEvent) {
          this.isDragging_0 = this.computePointOrientation_0(ptrX, ptrY, ctx);
          this.ptOrientation_0.transpose_d4zu6j$(this.mouseRotationStart_0);
          this.startTransform_0.set_d4zu6j$(this.transform);
        }
         else if (this.isDragging_0) {
          this.set_d4zu6j$(this.startTransform_0);
          var valid = this.computePointOrientation_0(ptrX, ptrY, ctx);
          if (valid) {
            this.ptOrientation_0.mul_d4zu6j$(this.mouseRotationStart_0);
          }
          this.mul_d4zu6j$(this.ptOrientation_0);
          this.isDragging_0 = valid;
        }
      }
       else if (dragPtrs.get_za3lpa$(0).deltaScroll !== 0.0 || (dragPtrs.get_za3lpa$(0).isRightButtonEvent && dragPtrs.get_za3lpa$(0).isRightButtonDown)) {
        if (this.steadyScreenPtMode_0 === Earth$Companion_getInstance().STEADY_SCREEN_PT_OFF_0 || ptrX !== this.steadyScreenPt_0.x || ptrY !== this.steadyScreenPt_0.y) {
          this.setSteadyPoint_dleff0$(ptrX, ptrY);
        }
      }
    }
    return 0;
  };
  Earth.prototype.computePointOrientation_0 = function (screenX, screenY, ctx) {
    var tmp$, tmp$_0, tmp$_1;
    if ((tmp$_1 = (tmp$_0 = (tmp$ = this.scene) != null ? tmp$.camera : null) != null ? tmp$_0.computePickRay_jker1g$(this.pickRay_0, screenX, screenY, ctx) : null) != null ? tmp$_1 : false) {
      var o = this.pickRay_0.origin;
      var l = this.pickRay_0.direction;
      this.toLocalCoords_w1lst9$(this.pickRay_0.origin);
      this.toLocalCoords_w1lst9$(this.pickRay_0.direction, 0.0);
      this.toLocalCoords_w1lst9$(this.tmpVecY_0.set_czzhiu$(Vec3f.Companion.Y_AXIS), 0.0);
      var ldo = l.times_czzhiu$(o);
      var sqr = ldo * ldo - o.sqrLength() + Earth$Companion_getInstance().EARTH_R * Earth$Companion_getInstance().EARTH_R;
      if (sqr > 0) {
        var d = -ldo - Math_0.sqrt(sqr);
        l.scale_749b8l$(d, this.tmpVec_0).add_czzhiu$(o);
        this.tmpVec_0.norm();
        if (this.tmpVec_0.isFuzzyEqual_2qa7tb$(this.tmpVecY_0)) {
          return false;
        }
        this.tmpVecY_0.cross_2gj7b4$(this.tmpVec_0, this.tmpVecRt_0).norm();
        this.tmpVec_0.cross_2gj7b4$(this.tmpVecRt_0, this.tmpVecUp_0);
        this.ptOrientation_0.setColVec_gdg6t7$(0, this.tmpVec_0, 0.0);
        this.ptOrientation_0.setColVec_gdg6t7$(1, this.tmpVecRt_0, 0.0);
        this.ptOrientation_0.setColVec_gdg6t7$(2, this.tmpVecUp_0, 0.0);
        this.ptOrientation_0.setColVec_gdg6t7$(3, Vec3f.Companion.ZERO, 1.0);
        return true;
      }
    }
    return false;
  };
  function Earth$Companion() {
    Earth$Companion_instance = this;
    this.EARTH_R = 6371000.8;
    this.MIN_ZOOM_LEVEL = 3;
    this.MAX_ZOOM_LEVEL = 19;
    this.RAD_85_0 = 85.0 * math_0.DEG_2_RAD;
    this.STEADY_SCREEN_PT_OFF_0 = 0;
    this.STEADY_SCREEN_PT_INIT_0 = 1;
    this.STEADY_SCREEN_PT_HOLD_0 = 2;
  }
  Earth$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Earth$Companion_instance = null;
  function Earth$Companion_getInstance() {
    if (Earth$Companion_instance === null) {
      new Earth$Companion();
    }
    return Earth$Companion_instance;
  }
  function Earth$TileGroup() {
    Group.call(this);
  }
  Earth$TileGroup.prototype.render_aemszp$ = function (ctx) {
    ctx.pushAttributes();
    ctx.depthFunc = gl.GL_ALWAYS;
    ctx.applyAttributes();
    Group.prototype.render_aemszp$.call(this, ctx);
    ctx.popAttributes();
  };
  Earth$TileGroup.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TileGroup',
    interfaces: [Group]
  };
  Earth.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Earth',
    interfaces: [InputManager$DragHandler, TransformGroup]
  };
  function earthScene$lambda$lambda(this$) {
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
  function earthScene(ctx) {
    var scenes = ArrayList_init();
    var earth = {v: null};
    var $receiver = new Scene(null);
    $receiver.unaryPlus_uv0sim$(sphericalInputTransform(void 0, earthScene$lambda$lambda($receiver)));
    var $receiver_0 = new Earth();
    $receiver_0.translate_y2kzbl$(0.0, 0.0, -Earth$Companion_getInstance().EARTH_R);
    earth.v = $receiver_0;
    $receiver.unaryPlus_uv0sim$(ensureNotNull(earth.v));
    scenes.add_11rb$($receiver);
    var ui = new EarthUi(ensureNotNull(earth.v), ctx);
    var element = ui.scene;
    scenes.add_11rb$(element);
    return scenes;
  }
  function EarthUi(earth, ctx) {
    this.earth = earth;
    this.ctx = ctx;
    this.attributionText_6dibss$_0 = this.attributionText_6dibss$_0;
    this.attribWidth_0 = 0.0;
    this.posWidth_0 = 0.0;
    this.scene = uiScene(void 0, void 0, EarthUi$scene$lambda(this));
  }
  Object.defineProperty(EarthUi.prototype, 'attributionText_0', {
    get: function () {
      if (this.attributionText_6dibss$_0 == null)
        return throwUPAE('attributionText');
      return this.attributionText_6dibss$_0;
    },
    set: function (attributionText) {
      this.attributionText_6dibss$_0 = attributionText;
    }
  });
  function EarthUi$scene$lambda$lambda$lambda(it) {
    return new BlankComponentUi();
  }
  function EarthUi$scene$lambda$lambda($receiver) {
    $receiver.componentUi_mloaa0$(getCallableRef('SimpleComponentUi', function (component) {
      return new SimpleComponentUi(component);
    }));
    $receiver.containerUi_2t3ptw$(EarthUi$scene$lambda$lambda$lambda);
    $receiver.standardFont_ttufcy$(new FontProps(Font.Companion.SYSTEM_FONT, 12.0));
    return Unit;
  }
  function EarthUi$scene$lambda$lambda$lambda_0(this$EarthUi, this$) {
    return function ($receiver, it) {
      var tmp$, tmp$_0;
      this$.text = this$EarthUi.earth.attribution;
      var w = (tmp$_0 = (tmp$ = this$.font.apply()) != null ? tmp$.textWidth_61zpoe$(this$.text) : null) != null ? tmp$_0 : 0.0;
      if (w !== this$EarthUi.attribWidth_0) {
        this$EarthUi.attribWidth_0 = w;
        this$.layoutSpec.setSize_4ujscr$(dps(w + 8, true), dps(18.0), zero());
        this$EarthUi.posWidth_0 = 0.0;
      }
      return Unit;
    };
  }
  function EarthUi$scene$lambda$lambda$lambda_1(this$EarthUi) {
    return function ($receiver, f, f_0, f_1) {
      if (!(this$EarthUi.earth.attributionUrl.length === 0)) {
        this$EarthUi.ctx.openUrl_61zpoe$(this$EarthUi.earth.attributionUrl);
      }
      return Unit;
    };
  }
  function EarthUi$scene$lambda$lambda_0(this$EarthUi) {
    return function ($receiver) {
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(4.0, true));
      $receiver.textColor.setCustom_11rb$(Color.Companion.LIME);
      $receiver.textColorHovered.setCustom_11rb$(color('#42A5F5'));
      var $receiver_0 = $receiver.onRender;
      var element = EarthUi$scene$lambda$lambda$lambda_0(this$EarthUi, $receiver);
      $receiver_0.add_11rb$(element);
      var $receiver_1 = $receiver.onClick;
      var element_0 = EarthUi$scene$lambda$lambda$lambda_1(this$EarthUi);
      $receiver_1.add_11rb$(element_0);
      return Unit;
    };
  }
  function EarthUi$scene$lambda$lambda$lambda_2(this$EarthUi, this$, this$_0) {
    return function ($receiver, it) {
      var tmp$, tmp$_0, tmp$_1;
      var lat = formatDouble(this$EarthUi.earth.centerLat, 5);
      var lon = formatDouble(this$EarthUi.earth.centerLon, 5);
      if (this$EarthUi.earth.cameraHeight > 10000) {
        tmp$ = formatDouble(this$EarthUi.earth.cameraHeight / 1000.0, 1) + ' km';
      }
       else {
        tmp$ = formatDouble(this$EarthUi.earth.cameraHeight, 1) + ' m';
      }
      var hgt = tmp$;
      this$.text = lat + '\xB0, ' + lon + '\xB0  ' + hgt;
      var w = (tmp$_1 = (tmp$_0 = this$.font.apply()) != null ? tmp$_0.textWidth_61zpoe$(this$.text) : null) != null ? tmp$_1 : 0.0;
      if (w !== this$EarthUi.posWidth_0) {
        this$EarthUi.posWidth_0 = w;
        var xOri = dps(-w - 8, true);
        this$.layoutSpec.setSize_4ujscr$(dps(w + 8, true), dps(18.0), zero());
        this$.layoutSpec.setOrigin_4ujscr$(xOri, dps(0.0), zero());
        this$EarthUi.attributionText_0.layoutSpec.setOrigin_4ujscr$(xOri.minus_m986jv$(this$EarthUi.attributionText_0.layoutSpec.width), zero(), zero());
        this$_0.content.requestLayout();
      }
      return Unit;
    };
  }
  function EarthUi$scene$lambda$lambda_1(this$EarthUi, this$) {
    return function ($receiver) {
      $receiver.padding = new Margin(zero(), zero(), dps(4.0, true), dps(0.0, true));
      var $receiver_0 = $receiver.onRender;
      var element = EarthUi$scene$lambda$lambda$lambda_2(this$EarthUi, $receiver, this$);
      $receiver_0.add_11rb$(element);
      return Unit;
    };
  }
  function EarthUi$scene$lambda(this$EarthUi) {
    return function ($receiver) {
      $receiver.theme = theme(UiTheme.Companion.DARK, EarthUi$scene$lambda$lambda);
      this$EarthUi.attributionText_0 = $receiver.button_9zrh0o$('attributionText', EarthUi$scene$lambda$lambda_0(this$EarthUi));
      $receiver.unaryPlus_uv0sim$(this$EarthUi.attributionText_0);
      $receiver.unaryPlus_uv0sim$($receiver.label_tokfmu$('posLabel', EarthUi$scene$lambda$lambda_1(this$EarthUi, $receiver)));
      return Unit;
    };
  }
  EarthUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EarthUi',
    interfaces: []
  };
  function TileMesh(earth, tx, ty, tz, ctx) {
    TileMesh$Companion_getInstance();
    Mesh.call(this, MeshData_init([Attribute.Companion.POSITIONS, Attribute.Companion.NORMALS, Attribute.Companion.TEXTURE_COORDS]), tz.toString() + '/' + tx + '/' + ty);
    this.earth = earth;
    this.tx = tx;
    this.ty = ty;
    this.tz = tz;
    this.key = TileMesh$Companion_getInstance().tileKey_qt1dr2$(this.tx, this.ty, this.tz);
    this.tileShader_0 = null;
    this.centerNormal_0 = Vec3f_init(0.0);
    this.tmpVec_0 = MutableVec3f_init();
    this.tmpBndsMin_0 = MutableVec3f_init();
    this.tmpBndsMax_0 = MutableVec3f_init();
    this.isFadingOut = false;
    this.isLoaded_dzczzy$_0 = false;
    this.isTexLoaded_ux45ej$_0 = false;
    this.generator = TileMesh_init$lambda(this);
    this.tileShader_0 = basicShader(TileMesh_init$lambda_0);
    this.tileShader_0.alpha = 0.0;
    this.shader = this.tileShader_0;
    this.loadTileTex_0(this.tx, this.ty, this.tz, ctx);
    this.generateGeometry();
  }
  Object.defineProperty(TileMesh.prototype, 'isCurrentlyVisible', {
    get: function () {
      return this.isRendered;
    }
  });
  Object.defineProperty(TileMesh.prototype, 'isLoaded', {
    get: function () {
      return this.isLoaded_dzczzy$_0;
    },
    set: function (isLoaded) {
      this.isLoaded_dzczzy$_0 = isLoaded;
    }
  });
  Object.defineProperty(TileMesh.prototype, 'isTexLoaded', {
    get: function () {
      return this.isTexLoaded_ux45ej$_0;
    },
    set: function (isTexLoaded) {
      this.isTexLoaded_ux45ej$_0 = isTexLoaded;
    }
  });
  TileMesh.prototype.loadTileTex_0 = function (x, y, z, ctx) {
    this.tileShader_0.texture = assetTexture_0('http://tile.openstreetmap.org/' + z + '/' + x + '/' + y + '.png', ctx);
  };
  TileMesh.prototype.render_aemszp$ = function (ctx) {
    var targetAlpha = 1.0;
    if (this.isTexLoaded && !this.isFadingOut && this.tileShader_0.alpha < targetAlpha) {
      this.tileShader_0.alpha = this.tileShader_0.alpha + ctx.deltaT;
      if (this.tileShader_0.alpha >= targetAlpha) {
        this.tileShader_0.alpha = targetAlpha;
        this.isLoaded = true;
        this.earth.tileLoaded_o2opt$(this);
      }
    }
     else if (this.isFadingOut && this.tileShader_0.alpha > 0.0) {
      this.tileShader_0.alpha = this.tileShader_0.alpha - ctx.deltaT;
      if (this.tileShader_0.alpha <= 0.0) {
        this.tileShader_0.alpha = 0.0;
        this.earth.tileFadedOut_o2opt$(this);
      }
    }
    Mesh.prototype.render_aemszp$.call(this, ctx);
  };
  TileMesh.prototype.checkIsVisible_aemszp$ = function (ctx) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
    tmp$ = this.tileShader_0.texture;
    if (tmp$ == null) {
      return false;
    }
    var tex = tmp$;
    this.isTexLoaded = (tmp$_1 = (tmp$_0 = tex.res) != null ? tmp$_0.isLoaded : null) != null ? tmp$_1 : false;
    var visible = this.isTexLoaded && Mesh.prototype.checkIsVisible_aemszp$.call(this, ctx);
    if (visible) {
      this.toGlobalCoords_w1lst9$(this.tmpVec_0.set_czzhiu$(this.centerNormal_0), 0.0);
      var cos = (tmp$_5 = (tmp$_4 = (tmp$_3 = (tmp$_2 = this.scene) != null ? tmp$_2.camera : null) != null ? tmp$_3.globalLookDir : null) != null ? tmp$_4.dot_czzhiu$(this.tmpVec_0) : null) != null ? tmp$_5 : 0.0;
      return cos < 0.1;
    }
     else if (!this.isTexLoaded) {
      ctx.textureMgr.bindTexture_xyx3x4$(tex, ctx);
    }
    return false;
  };
  function TileMesh$Companion() {
    TileMesh$Companion_instance = this;
  }
  TileMesh$Companion.prototype.tileKey_qt1dr2$ = function (tx, ty, tz) {
    return Kotlin.Long.fromInt(tz).shiftLeft(58).or(Kotlin.Long.fromInt(tx & 536870911).shiftLeft(29)).or(Kotlin.Long.fromInt(ty & 536870911));
  };
  TileMesh$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var TileMesh$Companion_instance = null;
  function TileMesh$Companion_getInstance() {
    if (TileMesh$Companion_instance === null) {
      new TileMesh$Companion();
    }
    return TileMesh$Companion_instance;
  }
  function TileMesh_init$lambda(this$TileMesh) {
    return function ($receiver) {
      var lonW = this$TileMesh.tx / (1 << this$TileMesh.tz) * 2 * math.PI - math.PI;
      var lonE = (this$TileMesh.tx + 1 | 0) / (1 << this$TileMesh.tz) * 2 * math.PI - math.PI;
      var uvScale = 255.0 / 256.0;
      var uvOff = 0.5 / 256.0;
      var stepsExp = 4;
      var steps = 1 << stepsExp;
      var tysFac = 1.0 / (1 << this$TileMesh.tz + stepsExp) * 2 * math.PI;
      var prevIndices = new Int32Array(steps + 1 | 0);
      var rowIndices = new Int32Array(steps + 1 | 0);
      for (var row = 0; row <= steps; row++) {
        var tmp = prevIndices;
        prevIndices = rowIndices;
        rowIndices = tmp;
        var tys = Kotlin.imul(this$TileMesh.ty + 1 | 0, steps) - row | 0;
        var tmp$ = math.PI * 0.5;
        var x = math.PI - tys * tysFac;
        var x_0 = Math_0.sinh(x);
        var lat = tmp$ - Math_0.atan(x_0);
        var r = Math_0.sin(lat) * Earth$Companion_getInstance().EARTH_R;
        var y = Math_0.cos(lat) * Earth$Companion_getInstance().EARTH_R;
        for (var i = 0; i <= steps; i++) {
          var phi = lonW + (lonE - lonW) * i / steps;
          var x_1 = Math_0.sin(phi) * r;
          var z = Math_0.cos(phi) * r;
          var uv = new Vec2f(i / steps * uvScale + uvOff, 1.0 - (row / steps * uvScale + uvOff));
          var fx = x_1;
          var fy = y;
          var fz = z;
          var nrm = (new MutableVec3f(fx, fy, fz)).norm();
          rowIndices[i] = $receiver.vertex_n440gp$(new Vec3f(fx, fy, fz), nrm, uv);
          if (row === (steps / 2 | 0) && i === (steps / 2 | 0)) {
            this$TileMesh.centerNormal_0 = Vec3f_init_0(nrm);
          }
          if (i > 0 && row > 0) {
            $receiver.meshData.addTriIndices_qt1dr2$(prevIndices[i - 1 | 0], rowIndices[i], rowIndices[i - 1 | 0]);
            $receiver.meshData.addTriIndices_qt1dr2$(prevIndices[i - 1 | 0], prevIndices[i], rowIndices[i]);
          }
        }
      }
      return Unit;
    };
  }
  function TileMesh_init$lambda_0($receiver) {
    $receiver.colorModel = ColorModel.TEXTURE_COLOR;
    $receiver.lightModel = LightModel.NO_LIGHTING;
    $receiver.isAlpha = true;
    return Unit;
  }
  TileMesh.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TileMesh',
    interfaces: [Mesh]
  };
  function TileName(x, y, zoom) {
    TileName$Companion_getInstance();
    this.x = x;
    this.y = y;
    this.zoom = zoom;
    this.ne = null;
    this.sw = null;
    this.center = null;
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
  TileName$Companion.prototype.forLatLng_ofy4p0$ = function (latLon, zoom) {
    return this.forLatLng_syxxoe$(latLon.lat, latLon.lon, zoom);
  };
  TileName$Companion.prototype.forLatLng_syxxoe$ = function (lat, lon, zoom) {
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
  TileName.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TileName',
    interfaces: []
  };
  TileName.prototype.component1 = function () {
    return this.x;
  };
  TileName.prototype.component2 = function () {
    return this.y;
  };
  TileName.prototype.component3 = function () {
    return this.zoom;
  };
  TileName.prototype.copy_qt1dr2$ = function (x, y, zoom) {
    return new TileName(x === void 0 ? this.x : x, y === void 0 ? this.y : y, zoom === void 0 ? this.zoom : zoom);
  };
  TileName.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.zoom) | 0;
    return result;
  };
  TileName.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.zoom, other.zoom)))));
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
      var props = TextureProps_init('ground_nrm.png', gl.GL_LINEAR, gl.GL_REPEAT);
      $receiver.normalMap = assetTexture(props, closure$ctx);
      var colorProps = TextureProps_init('ground_color.png', gl.GL_LINEAR, gl.GL_REPEAT);
      $receiver.texture = assetTexture(colorProps, closure$ctx);
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
  var util = $module$kool.de.fabmax.kool.util;
  var Log$Level = $module$kool.de.fabmax.kool.util.Log.Level;
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
        closure$tree.traverse_vqgpt3$(closure$trav);
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
      (Kotlin.isType(tmp$ = $receiver.shader, BasicShader) ? tmp$ : throwCCE()).texture = assetTexture_0('world.jpg', closure$ctx);
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
      var font = new Font(new FontProps(Font.Companion.SYSTEM_FONT, 72.0, Font.Companion.PLAIN, 1.5), closure$ctx);
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
    if (!equals(bounds, this.contentBounds)) {
      this.contentBounds.clear();
    }
    UiContainer.prototype.doLayout_sq5703$.call(this, bounds, ctx);
    if (!equals(bounds, this.contentBounds)) {
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
    this.zPos = -10000.0;
    this.sampleInterval = 0.05;
    this.nextSample = 0.0;
    this.unaryPlus_uv0sim$(this.quads);
  }
  SynthieScene$Heightmap.prototype.render_aemszp$ = function (ctx) {
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
      if (this.zPos > 10000) {
        this.zPos = -10000.0;
      }
      this.quads.meshData.isSyncRequired = true;
    }
    this.setIdentity();
    this.scale_y2kzbl$(1.0 / 32.0, 1.0 / 32.0, 1.0 / 32.0);
    this.translate_y2kzbl$(0.0, -32.0, -this.zPos + this.length / 5.0);
    TransformGroup.prototype.render_aemszp$.call(this, ctx);
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
      $receiver.meshData.usage = gl.GL_DYNAMIC_DRAW;
      $receiver.generator = SynthieScene$Heightmap$quads$lambda$lambda(this$Heightmap);
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
        var idx = $receiver.meshData.addVertex_hvwyd1$(SynthieScene$Waveform$lines$lambda$lambda$lambda(i_0, this));
        if (i_0 > 1) {
          $receiver.meshData.addIndices_pmhfmb$(new Int32Array([idx - 1 | 0, idx]));
        }
      }
      $receiver.lineWidth = 1.0;
      $receiver.shader = basicShader(SynthieScene$Waveform$lines$lambda$lambda$lambda_0);
      (Kotlin.isType(tmp$_1 = $receiver.shader, BasicShader) ? tmp$_1 : throwCCE()).staticColor.set_czzhhz$(Color.Companion.LIME);
      $receiver.meshData.usage = gl.GL_DYNAMIC_DRAW;
      array[i] = $receiver;
    }
    this.lines = array;
    var array_0 = Array_0(this.lines.length);
    var tmp$_2;
    tmp$_2 = array_0.length - 1 | 0;
    for (var i_1 = 0; i_1 <= tmp$_2; i_1++) {
      array_0[i_1] = this.lines[i_1].meshData.get_za3lpa$(0);
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
  function SynthieScene$Waveform$lines$lambda$lambda$lambda(closure$i, this$Waveform) {
    return function ($receiver) {
      $receiver.position.set_y2kzbl$((closure$i - (this$Waveform.points / 2 | 0) | 0) / 256.0, 1.0, 0.0);
      return Unit;
    };
  }
  function SynthieScene$Waveform$lines$lambda$lambda$lambda_0($receiver) {
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
      var textureProps = TextureProps_init_0('tree_bark.png', gl.GL_LINEAR, gl.GL_REPEAT, 16);
      var nrmMapProps = TextureProps_init_0('tree_bark_nrm.png', gl.GL_LINEAR, gl.GL_REPEAT, 16);
      $receiver.texture = assetTexture(textureProps, closure$ctx);
      $receiver.normalMap = assetTexture(nrmMapProps, closure$ctx);
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
      $receiver.texture = assetTexture_0('leaf.png', closure$ctx);
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
        this.attractionPointsTree_0.traverse_vqgpt3$(this.attractionPointTrav_0.reset_2qa7tb$(node, this.radiusOfInfluence));
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
          this.attractionPointsTree_0.traverse_vqgpt3$(this.attractionPointTrav_0.reset_2qa7tb$(newNode, this.actualKillDistance));
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
  function TreeGenerator$TreeNode$buildTrunkMesh$lambda(this$TreeNode) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(this$TreeNode);
      this$TreeNode.subtract_2gj7b4$(ensureNotNull(this$TreeNode.parent), $receiver.normal).norm();
      $receiver.texCoord.set_dleff0$(0.0, this$TreeNode.texV);
      return Unit;
    };
  }
  function TreeGenerator$TreeNode$buildTrunkMesh$lambda_0(this$TreeNode, closure$i) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(ensureNotNull(this$TreeNode.parent).circumPts.get_za3lpa$(closure$i % 8));
      ensureNotNull(this$TreeNode.parent).circumPts.get_za3lpa$(closure$i % 8).subtract_2gj7b4$(ensureNotNull(this$TreeNode.parent), $receiver.normal).norm();
      $receiver.texCoord.set_dleff0$(closure$i / 8.0, ensureNotNull(this$TreeNode.parent).texV);
      return Unit;
    };
  }
  function TreeGenerator$TreeNode$buildTrunkMesh$lambda_1(this$TreeNode, closure$i) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(this$TreeNode.circumPts.get_za3lpa$(closure$i % 8));
      this$TreeNode.circumPts.get_za3lpa$(closure$i % 8).subtract_2gj7b4$(this$TreeNode, $receiver.normal).norm();
      $receiver.texCoord.set_dleff0$(closure$i / 8.0, this$TreeNode.texV);
      return Unit;
    };
  }
  function TreeGenerator$TreeNode$buildTrunkMesh$lambda_2(this$TreeNode, closure$i) {
    return function ($receiver) {
      $receiver.position.set_czzhiu$(ensureNotNull(this$TreeNode.parent).circumPts.get_za3lpa$(closure$i % 8));
      ensureNotNull(this$TreeNode.parent).circumPts.get_za3lpa$(closure$i % 8).subtract_2gj7b4$(ensureNotNull(this$TreeNode.parent), $receiver.normal).norm();
      $receiver.texCoord.set_dleff0$(closure$i / 8.0, ensureNotNull(this$TreeNode.parent).texV);
      return Unit;
    };
  }
  TreeGenerator$TreeNode.prototype.buildTrunkMesh_84rojv$ = function (target) {
    var idcs = ArrayList_init();
    if (this.parent != null) {
      if (this.children.isEmpty()) {
        var tipIdx = target.meshData.addVertex_hvwyd1$(TreeGenerator$TreeNode$buildTrunkMesh$lambda(this));
        for (var i = 0; i <= 8; i++) {
          var element = target.meshData.addVertex_hvwyd1$(TreeGenerator$TreeNode$buildTrunkMesh$lambda_0(this, i));
          idcs.add_11rb$(element);
        }
        for (var i_0 = 0; i_0 < 8; i_0++) {
          target.meshData.addTriIndices_qt1dr2$(tipIdx, idcs.get_za3lpa$(i_0), idcs.get_za3lpa$(i_0 + 1 | 0));
        }
      }
       else {
        for (var i_1 = 0; i_1 <= 8; i_1++) {
          var element_0 = target.meshData.addVertex_hvwyd1$(TreeGenerator$TreeNode$buildTrunkMesh$lambda_1(this, i_1));
          idcs.add_11rb$(element_0);
          var element_1 = target.meshData.addVertex_hvwyd1$(TreeGenerator$TreeNode$buildTrunkMesh$lambda_2(this, i_1));
          idcs.add_11rb$(element_1);
        }
        for (var i_2 = 0; i_2 < 8; i_2++) {
          target.meshData.addTriIndices_qt1dr2$(idcs.get_za3lpa$(i_2 * 2 | 0), idcs.get_za3lpa$((i_2 * 2 | 0) + 1 | 0), idcs.get_za3lpa$((i_2 * 2 | 0) + 2 | 0));
          target.meshData.addTriIndices_qt1dr2$(idcs.get_za3lpa$((i_2 * 2 | 0) + 1 | 0), idcs.get_za3lpa$((i_2 * 2 | 0) + 3 | 0), idcs.get_za3lpa$((i_2 * 2 | 0) + 2 | 0));
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
    this.tmpPt2_0 = MutableVec2f_init();
    this.e00_0 = MutableVec2f_init();
    this.e01_0 = MutableVec2f_init();
    this.e10_0 = MutableVec2f_init();
    this.e11_0 = MutableVec2f_init();
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
        var element_0 = spline.evaluate_f6p79m$(i_0 / m, MutableVec2f_init());
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
  _.main = main;
  _.getParams = getParams;
  $$importsForInline$$.kool = $module$kool;
  var package$de = _.de || (_.de = {});
  var package$fabmax = package$de.fabmax || (package$de.fabmax = {});
  var package$kool = package$fabmax.kool || (package$fabmax.kool = {});
  var package$demo = package$kool.demo || (package$kool.demo = {});
  package$demo.collisionDemo_aemszp$ = collisionDemo;
  package$demo.twoBoxes = twoBoxes;
  package$demo.Demo = Demo;
  package$demo.MenuButtonUi = MenuButtonUi;
  Object.defineProperty(Earth, 'Companion', {
    get: Earth$Companion_getInstance
  });
  var package$earth = package$demo.earth || (package$demo.earth = {});
  package$earth.Earth = Earth;
  package$earth.earthScene_aemszp$ = earthScene;
  package$earth.EarthUi = EarthUi;
  Object.defineProperty(TileMesh, 'Companion', {
    get: TileMesh$Companion_getInstance
  });
  package$earth.TileMesh = TileMesh;
  Object.defineProperty(TileName, 'Companion', {
    get: TileName$Companion_getInstance
  });
  package$earth.TileName = TileName;
  package$earth.LatLon = LatLon;
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
  Kotlin.defineModule('kooldemo', _);
  return _;
});
