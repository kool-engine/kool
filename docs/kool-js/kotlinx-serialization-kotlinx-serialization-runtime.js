(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-serialization-kotlinx-serialization-runtime'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-serialization-kotlinx-serialization-runtime'.");
    }root['kotlinx-serialization-kotlinx-serialization-runtime'] = factory(typeof this['kotlinx-serialization-kotlinx-serialization-runtime'] === 'undefined' ? {} : this['kotlinx-serialization-kotlinx-serialization-runtime'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Enum = Kotlin.kotlin.Enum;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var throwISE = Kotlin.throwISE;
  var getKClass = Kotlin.getKClass;
  var Annotation = Kotlin.kotlin.Annotation;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var RuntimeException = Kotlin.kotlin.RuntimeException;
  var throwCCE = Kotlin.throwCCE;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var toString = Kotlin.toString;
  var kotlin_js_internal_StringCompanionObject = Kotlin.kotlin.js.internal.StringCompanionObject;
  var Unit = Kotlin.kotlin.Unit;
  var contentToString = Kotlin.arrayToString;
  var zip = Kotlin.kotlin.collections.zip_r9t3v7$;
  var toMap = Kotlin.kotlin.collections.toMap_6hr0sd$;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var Grouping = Kotlin.kotlin.collections.Grouping;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var mapCapacity = Kotlin.kotlin.collections.mapCapacity_za3lpa$;
  var LinkedHashMap_init_0 = Kotlin.kotlin.collections.LinkedHashMap_init_bwtc7$;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var HashSet_init = Kotlin.kotlin.collections.HashSet_init_287e2$;
  var equals = Kotlin.equals;
  var hashCode = Kotlin.hashCode;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var toBooleanArray = Kotlin.kotlin.collections.toBooleanArray_xmyvgf$;
  var withIndex = Kotlin.kotlin.collections.withIndex_us0mfu$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var isBlank = Kotlin.kotlin.text.isBlank_gw00vp$;
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ensureNotNull = Kotlin.ensureNotNull;
  var KClass = Kotlin.kotlin.reflect.KClass;
  var List = Kotlin.kotlin.collections.List;
  var MutableList = Kotlin.kotlin.collections.MutableList;
  var ArrayList = Kotlin.kotlin.collections.ArrayList;
  var HashSet = Kotlin.kotlin.collections.HashSet;
  var Set = Kotlin.kotlin.collections.Set;
  var MutableSet = Kotlin.kotlin.collections.MutableSet;
  var LinkedHashSet = Kotlin.kotlin.collections.LinkedHashSet;
  var HashMap = Kotlin.kotlin.collections.HashMap;
  var Map = Kotlin.kotlin.collections.Map;
  var MutableMap = Kotlin.kotlin.collections.MutableMap;
  var LinkedHashMap = Kotlin.kotlin.collections.LinkedHashMap;
  var Map$Entry = Kotlin.kotlin.collections.Map.Entry;
  var Pair = Kotlin.kotlin.Pair;
  var Triple = Kotlin.kotlin.Triple;
  var toBoxedChar = Kotlin.toBoxedChar;
  var Any = Object;
  var toIntOrNull = Kotlin.kotlin.text.toIntOrNull_pdl1vz$;
  var IndexOutOfBoundsException = Kotlin.kotlin.IndexOutOfBoundsException;
  var getValue = Kotlin.kotlin.collections.getValue_t9ocha$;
  var asList = Kotlin.kotlin.collections.asList_us0mfu$;
  var ArrayList_init_1 = Kotlin.kotlin.collections.ArrayList_init_mqih57$;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_mqih57$;
  var HashSet_init_0 = Kotlin.kotlin.collections.HashSet_init_mqih57$;
  var LinkedHashMap_init_1 = Kotlin.kotlin.collections.LinkedHashMap_init_73mtqc$;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  var HashMap_init_0 = Kotlin.kotlin.collections.HashMap_init_73mtqc$;
  var LinkedHashSet_init_0 = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var indexOf = Kotlin.kotlin.collections.indexOf_mjy6jw$;
  var get_indices = Kotlin.kotlin.collections.get_indices_m7z4lg$;
  var Array_0 = Array;
  var toLong = Kotlin.kotlin.text.toLong_pdl1vz$;
  var kotlin_js_internal_ByteCompanionObject = Kotlin.kotlin.js.internal.ByteCompanionObject;
  var coerceAtLeast = Kotlin.kotlin.ranges.coerceAtLeast_dqglrj$;
  var copyOf = Kotlin.kotlin.collections.copyOf_mrm5p$;
  var kotlin_js_internal_ShortCompanionObject = Kotlin.kotlin.js.internal.ShortCompanionObject;
  var copyOf_0 = Kotlin.kotlin.collections.copyOf_m2jy6x$;
  var kotlin_js_internal_IntCompanionObject = Kotlin.kotlin.js.internal.IntCompanionObject;
  var copyOf_1 = Kotlin.kotlin.collections.copyOf_c03ot6$;
  var kotlin_js_internal_LongCompanionObject = Kotlin.kotlin.js.internal.LongCompanionObject;
  var copyOf_2 = Kotlin.kotlin.collections.copyOf_3aefkx$;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var copyOf_3 = Kotlin.kotlin.collections.copyOf_rblqex$;
  var kotlin_js_internal_DoubleCompanionObject = Kotlin.kotlin.js.internal.DoubleCompanionObject;
  var copyOf_4 = Kotlin.kotlin.collections.copyOf_xgrzbe$;
  var unboxChar = Kotlin.unboxChar;
  var kotlin_js_internal_CharCompanionObject = Kotlin.kotlin.js.internal.CharCompanionObject;
  var copyOf_5 = Kotlin.kotlin.collections.copyOf_gtcw5h$;
  var kotlin_js_internal_BooleanCompanionObject = Kotlin.kotlin.js.internal.BooleanCompanionObject;
  var copyOf_6 = Kotlin.kotlin.collections.copyOf_1qu12l$;
  var PrimitiveClasses$stringClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.stringClass;
  var Char = Kotlin.BoxedChar;
  var PrimitiveClasses$charArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.charArrayClass;
  var PrimitiveClasses$doubleClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.doubleClass;
  var PrimitiveClasses$doubleArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.doubleArrayClass;
  var PrimitiveClasses$floatClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.floatClass;
  var PrimitiveClasses$floatArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.floatArrayClass;
  var Long = Kotlin.Long;
  var PrimitiveClasses$longArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.longArrayClass;
  var PrimitiveClasses$intClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.intClass;
  var PrimitiveClasses$intArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.intArrayClass;
  var PrimitiveClasses$shortClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.shortClass;
  var PrimitiveClasses$shortArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.shortArrayClass;
  var PrimitiveClasses$byteClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.byteClass;
  var PrimitiveClasses$byteArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.byteArrayClass;
  var PrimitiveClasses$booleanClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.booleanClass;
  var PrimitiveClasses$booleanArrayClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.booleanArrayClass;
  var kotlin = Kotlin.kotlin;
  var mapOf = Kotlin.kotlin.collections.mapOf_qfcya0$;
  var capitalize = Kotlin.kotlin.text.capitalize_pdl1vz$;
  var equals_0 = Kotlin.kotlin.text.equals_igcy3c$;
  var trimIndent = Kotlin.kotlin.text.trimIndent_pdl1vz$;
  var last = Kotlin.kotlin.collections.last_2p1efm$;
  var lastOrNull = Kotlin.kotlin.collections.lastOrNull_2p1efm$;
  var get_lastIndex = Kotlin.kotlin.collections.get_lastIndex_55thoc$;
  var toByte = Kotlin.toByte;
  var CharRange = Kotlin.kotlin.ranges.CharRange;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var trimStart = Kotlin.kotlin.text.trimStart_wqw3xr$;
  var HashSet_init_1 = Kotlin.kotlin.collections.HashSet_init_ww73n8$;
  var StringBuilder_init_0 = Kotlin.kotlin.text.StringBuilder_init;
  var toInt = Kotlin.kotlin.text.toInt_pdl1vz$;
  var toLongOrNull = Kotlin.kotlin.text.toLongOrNull_pdl1vz$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var toDoubleOrNull = Kotlin.kotlin.text.toDoubleOrNull_pdl1vz$;
  var getOrNull = Kotlin.kotlin.collections.getOrNull_yzln2o$;
  var coerceAtMost = Kotlin.kotlin.ranges.coerceAtMost_dqglrj$;
  var toChar = Kotlin.toChar;
  var String_0 = Kotlin.kotlin.text.String_8chfmy$;
  var toByte_0 = Kotlin.kotlin.text.toByte_pdl1vz$;
  var toShort = Kotlin.kotlin.text.toShort_pdl1vz$;
  var single = Kotlin.kotlin.text.single_gw00vp$;
  var Throwable = Error;
  var isFinite = Kotlin.kotlin.isFinite_81szk$;
  var isFinite_0 = Kotlin.kotlin.isFinite_yrwdxr$;
  var iterator = Kotlin.kotlin.text.iterator_gw00vp$;
  var toShort_0 = Kotlin.toShort;
  var toList = Kotlin.kotlin.collections.toList_7wnvza$;
  var throwUPAE = Kotlin.throwUPAE;
  var asSequence = Kotlin.kotlin.collections.asSequence_abgq59$;
  var PrimitiveClasses$anyClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.anyClass;
  var IllegalArgumentException = Kotlin.kotlin.IllegalArgumentException;
  var IllegalArgumentException_init_0 = Kotlin.kotlin.IllegalArgumentException_init;
  var L4294967295 = new Kotlin.Long(-1, 0);
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var Exception = Kotlin.kotlin.Exception;
  var IndexOutOfBoundsException_init = Kotlin.kotlin.IndexOutOfBoundsException_init;
  var L0 = Kotlin.Long.ZERO;
  var NullPointerException_init = Kotlin.kotlin.NullPointerException_init;
  var L9007199254740991 = new Kotlin.Long(-1, 2097151);
  var numberToChar = Kotlin.numberToChar;
  var Math_0 = Math;
  var get_js = Kotlin.kotlin.js.get_js_1yb8b7$;
  var toList_0 = Kotlin.kotlin.text.toList_gw00vp$;
  var toByteArray = Kotlin.kotlin.collections.toByteArray_kdx1v$;
  var NotImplementedError = Kotlin.kotlin.NotImplementedError;
  var single_0 = Kotlin.kotlin.collections.single_2p1efm$;
  var KVariance = Kotlin.kotlin.reflect.KVariance;
  var get_indices_0 = Kotlin.kotlin.collections.get_indices_l1lu5t$;
  ByteOrder.prototype = Object.create(Enum.prototype);
  ByteOrder.prototype.constructor = ByteOrder;
  SerializationException.prototype = Object.create(RuntimeException.prototype);
  SerializationException.prototype.constructor = SerializationException;
  MissingFieldException.prototype = Object.create(SerializationException.prototype);
  MissingFieldException.prototype.constructor = MissingFieldException;
  UnknownFieldException.prototype = Object.create(SerializationException.prototype);
  UnknownFieldException.prototype.constructor = UnknownFieldException;
  UpdateNotSupportedException.prototype = Object.create(SerializationException.prototype);
  UpdateNotSupportedException.prototype.constructor = UpdateNotSupportedException;
  UpdateMode.prototype = Object.create(Enum.prototype);
  UpdateMode.prototype.constructor = UpdateMode;
  PolymorphicSerializer.prototype = Object.create(AbstractPolymorphicSerializer.prototype);
  PolymorphicSerializer.prototype.constructor = PolymorphicSerializer;
  SealedClassSerializer.prototype = Object.create(AbstractPolymorphicSerializer.prototype);
  SealedClassSerializer.prototype.constructor = SealedClassSerializer;
  PrimitiveKind.prototype = Object.create(SerialKind.prototype);
  PrimitiveKind.prototype.constructor = PrimitiveKind;
  PrimitiveKind$BOOLEAN.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$BOOLEAN.prototype.constructor = PrimitiveKind$BOOLEAN;
  PrimitiveKind$BYTE.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$BYTE.prototype.constructor = PrimitiveKind$BYTE;
  PrimitiveKind$CHAR.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$CHAR.prototype.constructor = PrimitiveKind$CHAR;
  PrimitiveKind$SHORT.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$SHORT.prototype.constructor = PrimitiveKind$SHORT;
  PrimitiveKind$INT.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$INT.prototype.constructor = PrimitiveKind$INT;
  PrimitiveKind$LONG.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$LONG.prototype.constructor = PrimitiveKind$LONG;
  PrimitiveKind$FLOAT.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$FLOAT.prototype.constructor = PrimitiveKind$FLOAT;
  PrimitiveKind$DOUBLE.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$DOUBLE.prototype.constructor = PrimitiveKind$DOUBLE;
  PrimitiveKind$UNIT.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$UNIT.prototype.constructor = PrimitiveKind$UNIT;
  PrimitiveKind$STRING.prototype = Object.create(PrimitiveKind.prototype);
  PrimitiveKind$STRING.prototype.constructor = PrimitiveKind$STRING;
  StructureKind.prototype = Object.create(SerialKind.prototype);
  StructureKind.prototype.constructor = StructureKind;
  StructureKind$CLASS.prototype = Object.create(StructureKind.prototype);
  StructureKind$CLASS.prototype.constructor = StructureKind$CLASS;
  StructureKind$LIST.prototype = Object.create(StructureKind.prototype);
  StructureKind$LIST.prototype.constructor = StructureKind$LIST;
  StructureKind$MAP.prototype = Object.create(StructureKind.prototype);
  StructureKind$MAP.prototype.constructor = StructureKind$MAP;
  StructureKind$OBJECT.prototype = Object.create(StructureKind.prototype);
  StructureKind$OBJECT.prototype.constructor = StructureKind$OBJECT;
  UnionKind.prototype = Object.create(SerialKind.prototype);
  UnionKind.prototype.constructor = UnionKind;
  UnionKind$ENUM_KIND.prototype = Object.create(UnionKind.prototype);
  UnionKind$ENUM_KIND.prototype.constructor = UnionKind$ENUM_KIND;
  UnionKind$CONTEXTUAL.prototype = Object.create(UnionKind.prototype);
  UnionKind$CONTEXTUAL.prototype.constructor = UnionKind$CONTEXTUAL;
  PolymorphicKind.prototype = Object.create(SerialKind.prototype);
  PolymorphicKind.prototype.constructor = PolymorphicKind;
  PolymorphicKind$SEALED.prototype = Object.create(PolymorphicKind.prototype);
  PolymorphicKind$SEALED.prototype.constructor = PolymorphicKind$SEALED;
  PolymorphicKind$OPEN.prototype = Object.create(PolymorphicKind.prototype);
  PolymorphicKind$OPEN.prototype.constructor = PolymorphicKind$OPEN;
  PrimitiveArrayDescriptor.prototype = Object.create(ListLikeDescriptor.prototype);
  PrimitiveArrayDescriptor.prototype.constructor = PrimitiveArrayDescriptor;
  ArrayClassDesc.prototype = Object.create(ListLikeDescriptor.prototype);
  ArrayClassDesc.prototype.constructor = ArrayClassDesc;
  ArrayListClassDesc.prototype = Object.create(ListLikeDescriptor.prototype);
  ArrayListClassDesc.prototype.constructor = ArrayListClassDesc;
  NamedListClassDescriptor.prototype = Object.create(ListLikeDescriptor.prototype);
  NamedListClassDescriptor.prototype.constructor = NamedListClassDescriptor;
  LinkedHashSetClassDesc.prototype = Object.create(ListLikeDescriptor.prototype);
  LinkedHashSetClassDesc.prototype.constructor = LinkedHashSetClassDesc;
  HashSetClassDesc.prototype = Object.create(ListLikeDescriptor.prototype);
  HashSetClassDesc.prototype.constructor = HashSetClassDesc;
  NamedMapClassDescriptor.prototype = Object.create(MapLikeDescriptor.prototype);
  NamedMapClassDescriptor.prototype.constructor = NamedMapClassDescriptor;
  LinkedHashMapClassDesc.prototype = Object.create(MapLikeDescriptor.prototype);
  LinkedHashMapClassDesc.prototype.constructor = LinkedHashMapClassDesc;
  HashMapClassDesc.prototype = Object.create(MapLikeDescriptor.prototype);
  HashMapClassDesc.prototype.constructor = HashMapClassDesc;
  ListLikeSerializer.prototype = Object.create(AbstractCollectionSerializer.prototype);
  ListLikeSerializer.prototype.constructor = ListLikeSerializer;
  MapLikeSerializer.prototype = Object.create(AbstractCollectionSerializer.prototype);
  MapLikeSerializer.prototype.constructor = MapLikeSerializer;
  PrimitiveArraySerializer.prototype = Object.create(ListLikeSerializer.prototype);
  PrimitiveArraySerializer.prototype.constructor = PrimitiveArraySerializer;
  ReferenceArraySerializer.prototype = Object.create(ListLikeSerializer.prototype);
  ReferenceArraySerializer.prototype.constructor = ReferenceArraySerializer;
  ArrayListSerializer.prototype = Object.create(ListLikeSerializer.prototype);
  ArrayListSerializer.prototype.constructor = ArrayListSerializer;
  LinkedHashSetSerializer.prototype = Object.create(ListLikeSerializer.prototype);
  LinkedHashSetSerializer.prototype.constructor = LinkedHashSetSerializer;
  HashSetSerializer.prototype = Object.create(ListLikeSerializer.prototype);
  HashSetSerializer.prototype.constructor = HashSetSerializer;
  LinkedHashMapSerializer.prototype = Object.create(MapLikeSerializer.prototype);
  LinkedHashMapSerializer.prototype.constructor = LinkedHashMapSerializer;
  HashMapSerializer.prototype = Object.create(MapLikeSerializer.prototype);
  HashMapSerializer.prototype.constructor = HashMapSerializer;
  SerialClassDescImpl.prototype = Object.create(PluginGeneratedSerialDescriptor.prototype);
  SerialClassDescImpl.prototype.constructor = SerialClassDescImpl;
  EnumDescriptor.prototype = Object.create(SerialClassDescImpl.prototype);
  EnumDescriptor.prototype.constructor = EnumDescriptor;
  ByteArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  ByteArraySerializer_0.prototype.constructor = ByteArraySerializer_0;
  ByteArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  ByteArrayBuilder.prototype.constructor = ByteArrayBuilder;
  ShortArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  ShortArraySerializer_0.prototype.constructor = ShortArraySerializer_0;
  ShortArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  ShortArrayBuilder.prototype.constructor = ShortArrayBuilder;
  IntArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  IntArraySerializer_0.prototype.constructor = IntArraySerializer_0;
  IntArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  IntArrayBuilder.prototype.constructor = IntArrayBuilder;
  LongArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  LongArraySerializer_0.prototype.constructor = LongArraySerializer_0;
  LongArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  LongArrayBuilder.prototype.constructor = LongArrayBuilder;
  FloatArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  FloatArraySerializer_0.prototype.constructor = FloatArraySerializer_0;
  FloatArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  FloatArrayBuilder.prototype.constructor = FloatArrayBuilder;
  DoubleArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  DoubleArraySerializer_0.prototype.constructor = DoubleArraySerializer_0;
  DoubleArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  DoubleArrayBuilder.prototype.constructor = DoubleArrayBuilder;
  CharArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  CharArraySerializer_0.prototype.constructor = CharArraySerializer_0;
  CharArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  CharArrayBuilder.prototype.constructor = CharArrayBuilder;
  BooleanArraySerializer_0.prototype = Object.create(PrimitiveArraySerializer.prototype);
  BooleanArraySerializer_0.prototype.constructor = BooleanArraySerializer_0;
  BooleanArrayBuilder.prototype = Object.create(PrimitiveArrayBuilder.prototype);
  BooleanArrayBuilder.prototype.constructor = BooleanArrayBuilder;
  IntDescriptor.prototype = Object.create(Migration.prototype);
  IntDescriptor.prototype.constructor = IntDescriptor;
  UnitDescriptor.prototype = Object.create(Migration.prototype);
  UnitDescriptor.prototype.constructor = UnitDescriptor;
  BooleanDescriptor.prototype = Object.create(Migration.prototype);
  BooleanDescriptor.prototype.constructor = BooleanDescriptor;
  ByteDescriptor.prototype = Object.create(Migration.prototype);
  ByteDescriptor.prototype.constructor = ByteDescriptor;
  ShortDescriptor.prototype = Object.create(Migration.prototype);
  ShortDescriptor.prototype.constructor = ShortDescriptor;
  LongDescriptor.prototype = Object.create(Migration.prototype);
  LongDescriptor.prototype.constructor = LongDescriptor;
  FloatDescriptor.prototype = Object.create(Migration.prototype);
  FloatDescriptor.prototype.constructor = FloatDescriptor;
  DoubleDescriptor.prototype = Object.create(Migration.prototype);
  DoubleDescriptor.prototype.constructor = DoubleDescriptor;
  CharDescriptor.prototype = Object.create(Migration.prototype);
  CharDescriptor.prototype.constructor = CharDescriptor;
  StringDescriptor.prototype = Object.create(Migration.prototype);
  StringDescriptor.prototype.constructor = StringDescriptor;
  NamedValueEncoder.prototype = Object.create(TaggedEncoder.prototype);
  NamedValueEncoder.prototype.constructor = NamedValueEncoder;
  NamedValueDecoder.prototype = Object.create(TaggedDecoder.prototype);
  NamedValueDecoder.prototype.constructor = NamedValueDecoder;
  MapEntrySerializer_0.prototype = Object.create(KeyValueSerializer.prototype);
  MapEntrySerializer_0.prototype.constructor = MapEntrySerializer_0;
  PairSerializer_0.prototype = Object.create(KeyValueSerializer.prototype);
  PairSerializer_0.prototype.constructor = PairSerializer_0;
  JsonPrimitive.prototype = Object.create(JsonElement.prototype);
  JsonPrimitive.prototype.constructor = JsonPrimitive;
  JsonLiteral.prototype = Object.create(JsonPrimitive.prototype);
  JsonLiteral.prototype.constructor = JsonLiteral;
  JsonNull.prototype = Object.create(JsonPrimitive.prototype);
  JsonNull.prototype.constructor = JsonNull;
  JsonObject.prototype = Object.create(JsonElement.prototype);
  JsonObject.prototype.constructor = JsonObject;
  JsonArray.prototype = Object.create(JsonElement.prototype);
  JsonArray.prototype.constructor = JsonArray;
  JsonException.prototype = Object.create(SerializationException.prototype);
  JsonException.prototype.constructor = JsonException;
  JsonDecodingException.prototype = Object.create(JsonException.prototype);
  JsonDecodingException.prototype.constructor = JsonDecodingException;
  JsonEncodingException.prototype = Object.create(JsonException.prototype);
  JsonEncodingException.prototype.constructor = JsonEncodingException;
  StreamingJsonInput.prototype = Object.create(AbstractDecoder.prototype);
  StreamingJsonInput.prototype.constructor = StreamingJsonInput;
  StreamingJsonOutput.prototype = Object.create(AbstractEncoder.prototype);
  StreamingJsonOutput.prototype.constructor = StreamingJsonOutput;
  AbstractJsonTreeInput.prototype = Object.create(NamedValueDecoder.prototype);
  AbstractJsonTreeInput.prototype.constructor = AbstractJsonTreeInput;
  JsonPrimitiveInput.prototype = Object.create(AbstractJsonTreeInput.prototype);
  JsonPrimitiveInput.prototype.constructor = JsonPrimitiveInput;
  JsonTreeInput.prototype = Object.create(AbstractJsonTreeInput.prototype);
  JsonTreeInput.prototype.constructor = JsonTreeInput;
  JsonTreeMapInput.prototype = Object.create(JsonTreeInput.prototype);
  JsonTreeMapInput.prototype.constructor = JsonTreeMapInput;
  JsonTreeListInput.prototype = Object.create(AbstractJsonTreeInput.prototype);
  JsonTreeListInput.prototype.constructor = JsonTreeListInput;
  AbstractJsonTreeOutput.prototype = Object.create(NamedValueEncoder.prototype);
  AbstractJsonTreeOutput.prototype.constructor = AbstractJsonTreeOutput;
  JsonPrimitiveOutput.prototype = Object.create(AbstractJsonTreeOutput.prototype);
  JsonPrimitiveOutput.prototype.constructor = JsonPrimitiveOutput;
  JsonTreeOutput.prototype = Object.create(AbstractJsonTreeOutput.prototype);
  JsonTreeOutput.prototype.constructor = JsonTreeOutput;
  JsonTreeMapOutput.prototype = Object.create(JsonTreeOutput.prototype);
  JsonTreeMapOutput.prototype.constructor = JsonTreeMapOutput;
  JsonTreeListOutput.prototype = Object.create(AbstractJsonTreeOutput.prototype);
  JsonTreeListOutput.prototype.constructor = JsonTreeListOutput;
  WriteMode.prototype = Object.create(Enum.prototype);
  WriteMode.prototype.constructor = WriteMode;
  SerializerAlreadyRegisteredException.prototype = Object.create(IllegalArgumentException.prototype);
  SerializerAlreadyRegisteredException.prototype.constructor = SerializerAlreadyRegisteredException;
  IOException.prototype = Object.create(Exception.prototype);
  IOException.prototype.constructor = IOException;
  ByteArrayInputStream.prototype = Object.create(InputStream.prototype);
  ByteArrayInputStream.prototype.constructor = ByteArrayInputStream;
  ByteArrayOutputStream.prototype = Object.create(OutputStream.prototype);
  ByteArrayOutputStream.prototype.constructor = ByteArrayOutputStream;
  DynamicObjectParser$DynamicInput.prototype = Object.create(NamedValueDecoder.prototype);
  DynamicObjectParser$DynamicInput.prototype.constructor = DynamicObjectParser$DynamicInput;
  DynamicObjectParser$DynamicMapInput.prototype = Object.create(DynamicObjectParser$DynamicInput.prototype);
  DynamicObjectParser$DynamicMapInput.prototype.constructor = DynamicObjectParser$DynamicMapInput;
  DynamicObjectParser$DynamicListInput.prototype = Object.create(DynamicObjectParser$DynamicInput.prototype);
  DynamicObjectParser$DynamicListInput.prototype.constructor = DynamicObjectParser$DynamicListInput;
  function ByteOrder(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ByteOrder_initFields() {
    ByteOrder_initFields = function () {
    };
    ByteOrder$LITTLE_ENDIAN_instance = new ByteOrder('LITTLE_ENDIAN', 0);
    ByteOrder$BIG_ENDIAN_instance = new ByteOrder('BIG_ENDIAN', 1);
  }
  var ByteOrder$LITTLE_ENDIAN_instance;
  function ByteOrder$LITTLE_ENDIAN_getInstance() {
    ByteOrder_initFields();
    return ByteOrder$LITTLE_ENDIAN_instance;
  }
  var ByteOrder$BIG_ENDIAN_instance;
  function ByteOrder$BIG_ENDIAN_getInstance() {
    ByteOrder_initFields();
    return ByteOrder$BIG_ENDIAN_instance;
  }
  ByteOrder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteOrder',
    interfaces: [Enum]
  };
  function ByteOrder$values() {
    return [ByteOrder$LITTLE_ENDIAN_getInstance(), ByteOrder$BIG_ENDIAN_getInstance()];
  }
  ByteOrder.values = ByteOrder$values;
  function ByteOrder$valueOf(name) {
    switch (name) {
      case 'LITTLE_ENDIAN':
        return ByteOrder$LITTLE_ENDIAN_getInstance();
      case 'BIG_ENDIAN':
        return ByteOrder$BIG_ENDIAN_getInstance();
      default:throwISE('No enum constant kotlinx.io.ByteOrder.' + name);
    }
  }
  ByteOrder.valueOf_61zpoe$ = ByteOrder$valueOf;
  var message;
  function Writer() {
  }
  Writer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Writer',
    interfaces: []
  };
  function PrintWriter() {
  }
  PrintWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrintWriter',
    interfaces: []
  };
  function StringWriter() {
  }
  StringWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StringWriter',
    interfaces: []
  };
  function Reader() {
  }
  Reader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Reader',
    interfaces: []
  };
  function StringReader() {
  }
  StringReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StringReader',
    interfaces: []
  };
  function Serializable(with_0) {
    if (with_0 === void 0)
      with_0 = getKClass(KSerializer);
    this.with = with_0;
  }
  Serializable.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Serializable',
    interfaces: [Annotation]
  };
  function Serializer(forClass) {
    this.forClass = forClass;
  }
  Serializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Serializer',
    interfaces: [Annotation]
  };
  function SerialName(value) {
    this.value = value;
  }
  SerialName.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialName',
    interfaces: [Annotation]
  };
  function Optional() {
  }
  Optional.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Optional',
    interfaces: [Annotation]
  };
  function Required() {
  }
  Required.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Required',
    interfaces: [Annotation]
  };
  function Transient() {
  }
  Transient.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Transient',
    interfaces: [Annotation]
  };
  function SerialInfo() {
  }
  SerialInfo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialInfo',
    interfaces: [Annotation]
  };
  function ContextualSerialization(forClasses) {
    this.forClasses = forClasses;
  }
  ContextualSerialization.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ContextualSerialization',
    interfaces: [Annotation]
  };
  function UseSerializers(serializerClasses) {
    this.serializerClasses = serializerClasses;
  }
  UseSerializers.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UseSerializers',
    interfaces: [Annotation]
  };
  function Polymorphic() {
  }
  Polymorphic.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Polymorphic',
    interfaces: [Annotation]
  };
  function InternalSerializationApi() {
  }
  InternalSerializationApi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InternalSerializationApi',
    interfaces: [Annotation]
  };
  function ContextSerializer(serializableClass) {
    this.serializableClass_0 = serializableClass;
    this.descriptor_f98ejb$_0 = SerialDescriptor_0('kotlinx.serialization.ContextSerializer', UnionKind$CONTEXTUAL_getInstance());
  }
  Object.defineProperty(ContextSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_f98ejb$_0;
    }
  });
  ContextSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var serializer = getContextualOrDefault_0(encoder.context, value);
    encoder.encodeSerializableValue_tf03ej$(serializer, value);
  };
  ContextSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var serializer = getContextualOrDefault(decoder.context, this.serializableClass_0);
    return decoder.decodeSerializableValue_w63s0f$(serializer);
  };
  ContextSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ContextSerializer',
    interfaces: [KSerializer]
  };
  function Decoder() {
  }
  Decoder.prototype.decodeSerializableValue_w63s0f$ = function (deserializer) {
    return deserializer.deserialize_nts5qn$(this);
  };
  Decoder.prototype.decodeNullableSerializableValue_aae3ea$ = function (deserializer) {
    return this.decodeNotNullMark() ? this.decodeSerializableValue_w63s0f$(deserializer) : this.decodeNull();
  };
  Decoder.prototype.updateSerializableValue_19c8k5$ = function (deserializer, old) {
    var tmp$;
    switch (this.updateMode.name) {
      case 'BANNED':
        throw new UpdateNotSupportedException(deserializer.descriptor.serialName);
      case 'OVERWRITE':
        tmp$ = this.decodeSerializableValue_w63s0f$(deserializer);
        break;
      case 'UPDATE':
        tmp$ = deserializer.patch_mynpiu$(this, old);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  };
  Decoder.prototype.updateNullableSerializableValue_exmlbs$ = function (deserializer, old) {
    var tmp$;
    if (this.updateMode === UpdateMode$BANNED_getInstance())
      throw new UpdateNotSupportedException(deserializer.descriptor.serialName);
    else if (this.updateMode === UpdateMode$OVERWRITE_getInstance() || old == null)
      tmp$ = this.decodeNullableSerializableValue_aae3ea$(deserializer);
    else if (this.decodeNotNullMark())
      tmp$ = deserializer.patch_mynpiu$(this, old);
    else {
      this.decodeNull();
      tmp$ = old;
    }
    return tmp$;
  };
  Decoder.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Decoder',
    interfaces: []
  };
  function CompositeDecoder() {
    CompositeDecoder$Companion_getInstance();
  }
  function CompositeDecoder$Companion() {
    CompositeDecoder$Companion_instance = this;
    this.READ_DONE = -1;
    this.READ_ALL = -2;
    this.UNKNOWN_NAME = -3;
  }
  CompositeDecoder$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var CompositeDecoder$Companion_instance = null;
  function CompositeDecoder$Companion_getInstance() {
    if (CompositeDecoder$Companion_instance === null) {
      new CompositeDecoder$Companion();
    }return CompositeDecoder$Companion_instance;
  }
  CompositeDecoder.prototype.decodeSequentially = function () {
    return false;
  };
  CompositeDecoder.prototype.decodeCollectionSize_qatsm0$ = function (descriptor) {
    return -1;
  };
  CompositeDecoder.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CompositeDecoder',
    interfaces: []
  };
  function decode($receiver, deserializer) {
    return $receiver.decodeSerializableValue_w63s0f$(deserializer);
  }
  var decode_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.decode_q4riyv$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    var decode = _.kotlinx.serialization.decode_cmswi7$;
    return function (T_0, isT, $receiver) {
      return decode($receiver, serializer(getKClass(T_0)));
    };
  }));
  var decodeStructure = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.decodeStructure_s55izo$', function ($receiver, descriptor, block) {
    var composite = $receiver.beginStructure_r0sa6z$(descriptor, []);
    var result = block(composite);
    composite.endStructure_qatsm0$(descriptor);
    return result;
  });
  function Encoder() {
  }
  Encoder.prototype.encodeNotNullMark = function () {
  };
  Encoder.prototype.beginCollection_gly1x5$ = function (descriptor, collectionSize, typeSerializers) {
    return this.beginStructure_r0sa6z$(descriptor, typeSerializers.slice());
  };
  Encoder.prototype.encodeSerializableValue_tf03ej$ = function (serializer, value) {
    serializer.serialize_awe97i$(this, value);
  };
  Encoder.prototype.encodeNullableSerializableValue_f4686g$ = function (serializer, value) {
    if (value == null) {
      this.encodeNull();
    } else {
      this.encodeNotNullMark();
      this.encodeSerializableValue_tf03ej$(serializer, value);
    }
  };
  Encoder.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Encoder',
    interfaces: []
  };
  function CompositeEncoder() {
  }
  CompositeEncoder.prototype.shouldEncodeElementDefault_3zr2iy$ = function (descriptor, index) {
    return true;
  };
  CompositeEncoder.prototype.encodeNonSerializableElement_4wpkd1$ = function (descriptor, index, value) {
  };
  CompositeEncoder.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CompositeEncoder',
    interfaces: []
  };
  function encode($receiver, strategy, value) {
    $receiver.encodeSerializableValue_tf03ej$(strategy, value);
  }
  var encode_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.encode_w79e6d$', wrapFunction(function () {
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var encode = _.kotlinx.serialization.encode_dt3ugd$;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT, $receiver, obj) {
      var tmp$;
      encode($receiver, Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE(), obj);
    };
  }));
  var encodeStructure = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.encodeStructure_2rdqf3$', function ($receiver, descriptor, block) {
    var composite = $receiver.beginStructure_r0sa6z$(descriptor, []);
    block(composite);
    composite.endStructure_qatsm0$(descriptor);
  });
  function SerializationException(message, cause) {
    if (cause === void 0)
      cause = null;
    RuntimeException.call(this, message, cause);
    this.name = 'SerializationException';
  }
  SerializationException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializationException',
    interfaces: [RuntimeException]
  };
  function MissingFieldException(fieldName) {
    SerializationException.call(this, "Field '" + fieldName + "' is required, but it was missing");
    this.name = 'MissingFieldException';
  }
  MissingFieldException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MissingFieldException',
    interfaces: [SerializationException]
  };
  function UnknownFieldException(index) {
    SerializationException.call(this, 'Unknown field for index ' + index);
    this.name = 'UnknownFieldException';
  }
  UnknownFieldException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnknownFieldException',
    interfaces: [SerializationException]
  };
  function UpdateNotSupportedException(className) {
    SerializationException.call(this, 'Update is not supported for ' + className);
    this.name = 'UpdateNotSupportedException';
  }
  UpdateNotSupportedException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UpdateNotSupportedException',
    interfaces: [SerializationException]
  };
  function KSerializer() {
  }
  KSerializer.prototype.patch_mynpiu$ = function (decoder, old) {
    throw new UpdateNotSupportedException(this.descriptor.serialName);
  };
  KSerializer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KSerializer',
    interfaces: [DeserializationStrategy, SerializationStrategy]
  };
  function SerializationStrategy() {
  }
  SerializationStrategy.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SerializationStrategy',
    interfaces: []
  };
  function DeserializationStrategy() {
  }
  DeserializationStrategy.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DeserializationStrategy',
    interfaces: []
  };
  function UpdateMode(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function UpdateMode_initFields() {
    UpdateMode_initFields = function () {
    };
    UpdateMode$BANNED_instance = new UpdateMode('BANNED', 0);
    UpdateMode$OVERWRITE_instance = new UpdateMode('OVERWRITE', 1);
    UpdateMode$UPDATE_instance = new UpdateMode('UPDATE', 2);
  }
  var UpdateMode$BANNED_instance;
  function UpdateMode$BANNED_getInstance() {
    UpdateMode_initFields();
    return UpdateMode$BANNED_instance;
  }
  var UpdateMode$OVERWRITE_instance;
  function UpdateMode$OVERWRITE_getInstance() {
    UpdateMode_initFields();
    return UpdateMode$OVERWRITE_instance;
  }
  var UpdateMode$UPDATE_instance;
  function UpdateMode$UPDATE_getInstance() {
    UpdateMode_initFields();
    return UpdateMode$UPDATE_instance;
  }
  UpdateMode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UpdateMode',
    interfaces: [Enum]
  };
  function UpdateMode$values() {
    return [UpdateMode$BANNED_getInstance(), UpdateMode$OVERWRITE_getInstance(), UpdateMode$UPDATE_getInstance()];
  }
  UpdateMode.values = UpdateMode$values;
  function UpdateMode$valueOf(name) {
    switch (name) {
      case 'BANNED':
        return UpdateMode$BANNED_getInstance();
      case 'OVERWRITE':
        return UpdateMode$OVERWRITE_getInstance();
      case 'UPDATE':
        return UpdateMode$UPDATE_getInstance();
      default:throwISE('No enum constant kotlinx.serialization.UpdateMode.' + name);
    }
  }
  UpdateMode.valueOf_61zpoe$ = UpdateMode$valueOf;
  function PrimitiveDescriptorWithName(name, original) {
    this.name_8ltsgd$_0 = name;
    this.original = original;
  }
  Object.defineProperty(PrimitiveDescriptorWithName.prototype, 'name', {
    get: function () {
      return this.name_8ltsgd$_0;
    }
  });
  Object.defineProperty(PrimitiveDescriptorWithName.prototype, 'annotations', {
    get: function () {
      return this.original.annotations;
    }
  });
  Object.defineProperty(PrimitiveDescriptorWithName.prototype, 'elementsCount', {
    get: function () {
      return this.original.elementsCount;
    }
  });
  Object.defineProperty(PrimitiveDescriptorWithName.prototype, 'isNullable', {
    get: function () {
      return this.original.isNullable;
    }
  });
  Object.defineProperty(PrimitiveDescriptorWithName.prototype, 'kind', {
    get: function () {
      return this.original.kind;
    }
  });
  Object.defineProperty(PrimitiveDescriptorWithName.prototype, 'serialName', {
    get: function () {
      return this.original.serialName;
    }
  });
  PrimitiveDescriptorWithName.prototype.getElementAnnotations_za3lpa$ = function (index) {
    return this.original.getElementAnnotations_za3lpa$(index);
  };
  PrimitiveDescriptorWithName.prototype.getElementDescriptor_za3lpa$ = function (index) {
    return this.original.getElementDescriptor_za3lpa$(index);
  };
  PrimitiveDescriptorWithName.prototype.getElementIndex_61zpoe$ = function (name) {
    return this.original.getElementIndex_61zpoe$(name);
  };
  PrimitiveDescriptorWithName.prototype.getElementName_za3lpa$ = function (index) {
    return this.original.getElementName_za3lpa$(index);
  };
  PrimitiveDescriptorWithName.prototype.getEntityAnnotations = function () {
    return this.original.getEntityAnnotations();
  };
  PrimitiveDescriptorWithName.prototype.isElementOptional_za3lpa$ = function (index) {
    return this.original.isElementOptional_za3lpa$(index);
  };
  PrimitiveDescriptorWithName.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveDescriptorWithName',
    interfaces: [SerialDescriptor]
  };
  function withName($receiver, name) {
    throw IllegalStateException_init('No longer supported'.toString());
  }
  function get_nullable($receiver) {
    var tmp$;
    return $receiver.descriptor.isNullable ? Kotlin.isType(tmp$ = $receiver, KSerializer) ? tmp$ : throwCCE() : new NullableSerializer($receiver);
  }
  function get_list($receiver) {
    return new ArrayListSerializer($receiver);
  }
  function get_set($receiver) {
    return new LinkedHashSetSerializer($receiver);
  }
  function get_map($receiver) {
    return new LinkedHashMapSerializer($receiver.first, $receiver.second);
  }
  function compiledSerializer($receiver) {
    return compiledSerializerImpl($receiver);
  }
  var enumReflectiveAccessMessage;
  function toNativeArray($receiver, eClass) {
    return toNativeArrayImpl($receiver, eClass);
  }
  var message_0;
  function Mapper() {
  }
  Mapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mapper',
    interfaces: []
  };
  function SerialId(id) {
    this.id = id;
  }
  SerialId.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialId',
    interfaces: [Annotation]
  };
  function serializer($receiver) {
    var tmp$;
    tmp$ = serializerOrNull($receiver);
    if (tmp$ == null) {
      throw new SerializationException("Can't locate argument-less serializer for class " + toString(simpleName_0($receiver)) + '. ' + 'For generic classes, such as lists, please provide serializer explicitly.');
    }return tmp$;
  }
  function serializerOrNull($receiver) {
    var tmp$;
    return (tmp$ = compiledSerializerImpl($receiver)) != null ? tmp$ : builtinSerializerOrNull($receiver);
  }
  function PolymorphicClassDescriptor$lambda($receiver) {
    $receiver.element_re18qg$('type', serializer_10(kotlin_js_internal_StringCompanionObject).descriptor);
    $receiver.element_re18qg$('value', SerialDescriptor_0('kotlinx.serialization.Polymorphic', UnionKind$CONTEXTUAL_getInstance()));
    return Unit;
  }
  var PolymorphicClassDescriptor;
  function PolymorphicSerializer(baseClass) {
    AbstractPolymorphicSerializer.call(this);
    this.baseClass_x5jvam$_0 = baseClass;
    this.descriptor_nog3ww$_0 = SerialDescriptor_0('kotlinx.serialization.Polymorphic', PolymorphicKind$OPEN_getInstance(), PolymorphicSerializer$descriptor$lambda(this));
  }
  Object.defineProperty(PolymorphicSerializer.prototype, 'baseClass', {
    get: function () {
      return this.baseClass_x5jvam$_0;
    }
  });
  Object.defineProperty(PolymorphicSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_nog3ww$_0;
    }
  });
  function PolymorphicSerializer$descriptor$lambda(this$PolymorphicSerializer) {
    return function ($receiver) {
      $receiver.element_re18qg$('type', serializer_10(kotlin_js_internal_StringCompanionObject).descriptor);
      $receiver.element_re18qg$('value', SerialDescriptor_0('kotlinx.serialization.Polymorphic<' + toString(this$PolymorphicSerializer.baseClass.simpleName) + '>', UnionKind$CONTEXTUAL_getInstance()));
      return Unit;
    };
  }
  PolymorphicSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PolymorphicSerializer',
    interfaces: [AbstractPolymorphicSerializer]
  };
  function groupingBy$ObjectLiteral(this$groupingBy, closure$keySelector) {
    this.this$groupingBy = this$groupingBy;
    this.closure$keySelector = closure$keySelector;
  }
  groupingBy$ObjectLiteral.prototype.sourceIterator = function () {
    return this.this$groupingBy.iterator();
  };
  groupingBy$ObjectLiteral.prototype.keyOf_11rb$ = function (element) {
    return this.closure$keySelector(element);
  };
  groupingBy$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [Grouping]};
  function SealedClassSerializer(serialName, baseClass, subclasses, subclassSerializers) {
    AbstractPolymorphicSerializer.call(this);
    this.baseClass_a0a98o$_0 = baseClass;
    this.descriptor_gganzq$_0 = SerialDescriptor_0(serialName, PolymorphicKind$SEALED_getInstance(), SealedClassSerializer$descriptor$lambda(this, subclassSerializers));
    this.class2Serializer_0 = null;
    this.serialName2Serializer_0 = null;
    if (!(subclasses.length === subclassSerializers.length)) {
      var message = 'Arrays of classes and serializers must have the same length,' + (' got arrays: ' + contentToString(subclasses) + ', ' + contentToString(subclassSerializers) + '\n') + 'Please ensure that @Serializable annotation is present on each sealed subclass';
      throw IllegalArgumentException_init(message.toString());
    }this.class2Serializer_0 = toMap(zip(subclasses, subclassSerializers));
    var $receiver = new groupingBy$ObjectLiteral(this.class2Serializer_0.entries, SealedClassSerializer_init$lambda);
    var destination = LinkedHashMap_init();
    var tmp$;
    tmp$ = $receiver.sourceIterator();
    while (tmp$.hasNext()) {
      var e = tmp$.next();
      var key = $receiver.keyOf_11rb$(e);
      var accumulator = destination.get_11rb$(key);
      var tmp$_0 = destination.put_xwzc9p$;
      accumulator == null && !destination.containsKey_11rb$(key);
      if (accumulator != null) {
        throw IllegalStateException_init(("Multiple sealed subclasses of '" + this.baseClass + "' have the same serial name '" + key + "':" + (" '" + accumulator.key + "', '" + e.key + "'")).toString());
      }tmp$_0.call(destination, key, e);
    }
    var destination_0 = LinkedHashMap_init_0(mapCapacity(destination.size));
    var tmp$_1;
    tmp$_1 = destination.entries.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      destination_0.put_xwzc9p$(element.key, element.value.value);
    }
    this.serialName2Serializer_0 = destination_0;
  }
  Object.defineProperty(SealedClassSerializer.prototype, 'baseClass', {
    get: function () {
      return this.baseClass_a0a98o$_0;
    }
  });
  Object.defineProperty(SealedClassSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_gganzq$_0;
    }
  });
  SealedClassSerializer.prototype.findPolymorphicSerializer_b69zac$ = function (decoder, klassName) {
    var tmp$;
    return (tmp$ = this.serialName2Serializer_0.get_11rb$(klassName)) != null ? tmp$ : AbstractPolymorphicSerializer.prototype.findPolymorphicSerializer_b69zac$.call(this, decoder, klassName);
  };
  SealedClassSerializer.prototype.findPolymorphicSerializer_7kuzo6$ = function (encoder, value) {
    var tmp$;
    return (tmp$ = this.class2Serializer_0.get_11rb$(Kotlin.getKClassFromExpression(value))) != null ? tmp$ : AbstractPolymorphicSerializer.prototype.findPolymorphicSerializer_7kuzo6$.call(this, encoder, value);
  };
  function SealedClassSerializer$descriptor$lambda$lambda(closure$subclassSerializers) {
    return function ($receiver) {
      var $receiver_0 = closure$subclassSerializers;
      var tmp$;
      for (tmp$ = 0; tmp$ !== $receiver_0.length; ++tmp$) {
        var element = $receiver_0[tmp$];
        var d = element.descriptor;
        $receiver.element_re18qg$(d.serialName, d);
      }
      return Unit;
    };
  }
  function SealedClassSerializer$descriptor$lambda(this$SealedClassSerializer, closure$subclassSerializers) {
    return function ($receiver) {
      $receiver.element_re18qg$('type', serializer_10(kotlin_js_internal_StringCompanionObject).descriptor);
      var elementDescriptor = SerialDescriptor_0('kotlinx.serialization.Sealed<' + toString(this$SealedClassSerializer.baseClass.simpleName) + '>', UnionKind$CONTEXTUAL_getInstance(), SealedClassSerializer$descriptor$lambda$lambda(closure$subclassSerializers));
      $receiver.element_re18qg$('value', elementDescriptor);
      return Unit;
    };
  }
  function SealedClassSerializer_init$lambda(it) {
    return it.value.descriptor.serialName;
  }
  SealedClassSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SealedClassSerializer',
    interfaces: [AbstractPolymorphicSerializer]
  };
  function SerialDescriptor() {
  }
  Object.defineProperty(SerialDescriptor.prototype, 'name', {
    get: function () {
      return this.serialName;
    }
  });
  Object.defineProperty(SerialDescriptor.prototype, 'isNullable', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(SerialDescriptor.prototype, 'annotations', {
    get: function () {
      return emptyList();
    }
  });
  SerialDescriptor.prototype.getEntityAnnotations = function () {
    return emptyList();
  };
  SerialDescriptor.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SerialDescriptor',
    interfaces: []
  };
  function elementDescriptors($receiver) {
    var size = $receiver.elementsCount;
    var list = ArrayList_init(size);
    for (var index = 0; index < size; index++) {
      list.add_11rb$($receiver.getElementDescriptor_za3lpa$(index));
    }
    return list;
  }
  function elementNames($receiver) {
    var size = $receiver.elementsCount;
    var list = ArrayList_init(size);
    for (var index = 0; index < size; index++) {
      list.add_11rb$($receiver.getElementName_za3lpa$(index));
    }
    return list;
  }
  function getElementIndexOrThrow($receiver, name) {
    var i = $receiver.getElementIndex_61zpoe$(name);
    if (i === -3)
      throw new SerializationException($receiver.serialName + " does not contain element with name '" + name + "'");
    return i;
  }
  function SerialDescriptor$lambda($receiver) {
    return Unit;
  }
  function SerialDescriptor_0(serialName, kind, builder) {
    if (kind === void 0)
      kind = StructureKind$CLASS_getInstance();
    if (builder === void 0)
      builder = SerialDescriptor$lambda;
    if (!!isBlank(serialName)) {
      var message = 'Blank serial names are prohibited';
      throw IllegalArgumentException_init(message.toString());
    }var sdBuilder = new SerialDescriptorBuilder(serialName);
    builder(sdBuilder);
    return new SerialDescriptorImpl(serialName, kind, sdBuilder.elementNames_8be2vx$.size, sdBuilder);
  }
  function PrimitiveDescriptor(serialName, kind) {
    if (!!isBlank(serialName)) {
      var message = 'Blank serial names are prohibited';
      throw IllegalArgumentException_init(message.toString());
    }return PrimitiveDescriptorSafe(serialName, kind);
  }
  function get_nullable_0($receiver) {
    if ($receiver.isNullable)
      return $receiver;
    return new SerialDescriptorForNullable($receiver);
  }
  function SerialDescriptorBuilder(serialName) {
    this.serialName = serialName;
    this.isNullable = false;
    this.annotations = emptyList();
    this.elementNames_8be2vx$ = ArrayList_init_0();
    this.uniqueNames_0 = HashSet_init();
    this.elementDescriptors_8be2vx$ = ArrayList_init_0();
    this.elementAnnotations_8be2vx$ = ArrayList_init_0();
    this.elementOptionality_8be2vx$ = ArrayList_init_0();
  }
  SerialDescriptorBuilder.prototype.element_re18qg$ = function (elementName, descriptor, annotations, isOptional) {
    if (annotations === void 0)
      annotations = emptyList();
    if (isOptional === void 0)
      isOptional = false;
    if (!this.uniqueNames_0.add_11rb$(elementName)) {
      var message = "Element with name '" + elementName + "' is already registered";
      throw IllegalArgumentException_init(message.toString());
    }this.elementNames_8be2vx$.add_11rb$(elementName);
    this.elementDescriptors_8be2vx$.add_11rb$(descriptor);
    this.elementAnnotations_8be2vx$.add_11rb$(annotations);
    this.elementOptionality_8be2vx$.add_11rb$(isOptional);
  };
  SerialDescriptorBuilder.prototype.element_sygqne$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.SerialDescriptorBuilder.element_sygqne$', wrapFunction(function () {
    var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT, elementName, annotations, isOptional) {
      if (annotations === void 0)
        annotations = emptyList();
      if (isOptional === void 0)
        isOptional = false;
      var tmp$;
      var descriptor = (Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE()).descriptor;
      this.element_re18qg$(elementName, descriptor, annotations, isOptional);
    };
  }));
  SerialDescriptorBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialDescriptorBuilder',
    interfaces: []
  };
  var descriptor = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.descriptor_ngfvyu$', wrapFunction(function () {
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT, $receiver) {
      var tmp$;
      return (Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE()).descriptor;
    };
  }));
  function listDescriptor($receiver, typeDescriptor) {
    return new ArrayListClassDesc(typeDescriptor);
  }
  var listDescriptor_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.listDescriptor_ngfvyu$', wrapFunction(function () {
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var listDescriptor = _.kotlinx.serialization.listDescriptor_if675o$;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT, $receiver) {
      var tmp$;
      return listDescriptor($receiver, (Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE()).descriptor);
    };
  }));
  function mapDescriptor($receiver, keyDescriptor, valueDescriptor) {
    return new HashMapClassDesc(keyDescriptor, valueDescriptor);
  }
  var mapDescriptor_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.mapDescriptor_yb1o6n$', wrapFunction(function () {
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var mapDescriptor = _.kotlinx.serialization.mapDescriptor_9axtvk$;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (K_0, isK, V_0, isV, $receiver) {
      var tmp$;
      var tmp$_0;
      return mapDescriptor($receiver, (Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(K_0)), KSerializer) ? tmp$ : throwCCE()).descriptor, (Kotlin.isType(tmp$_0 = serializer(getReifiedTypeParameterKType(V_0)), KSerializer) ? tmp$_0 : throwCCE()).descriptor);
    };
  }));
  function setDescriptor($receiver, typeDescriptor) {
    return new HashSetClassDesc(typeDescriptor);
  }
  var setDescriptor_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.setDescriptor_ngfvyu$', wrapFunction(function () {
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var setDescriptor = _.kotlinx.serialization.setDescriptor_if675o$;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT, $receiver) {
      var tmp$;
      return setDescriptor($receiver, (Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE()).descriptor);
    };
  }));
  function SerialDescriptorImpl(serialName, kind, elementsCount, builder) {
    this.serialName_jd9tzv$_0 = serialName;
    this.kind_yy72ts$_0 = kind;
    this.elementsCount_wx8o20$_0 = elementsCount;
    this.isNullable_ur9fvr$_0 = builder.isNullable;
    this.annotations_t3jslw$_0 = builder.annotations;
    this.elementNames_0 = copyToArray(builder.elementNames_8be2vx$);
    this.elementDescriptors_0 = copyToArray(builder.elementDescriptors_8be2vx$);
    this.elementAnnotations_0 = copyToArray(builder.elementAnnotations_8be2vx$);
    this.elementOptionality_0 = toBooleanArray(builder.elementOptionality_8be2vx$);
    var $receiver = withIndex(this.elementNames_0);
    var destination = ArrayList_init(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(to(item.value, item.index));
    }
    this.name2Index_0 = toMap(destination);
  }
  Object.defineProperty(SerialDescriptorImpl.prototype, 'serialName', {
    get: function () {
      return this.serialName_jd9tzv$_0;
    }
  });
  Object.defineProperty(SerialDescriptorImpl.prototype, 'kind', {
    get: function () {
      return this.kind_yy72ts$_0;
    }
  });
  Object.defineProperty(SerialDescriptorImpl.prototype, 'elementsCount', {
    get: function () {
      return this.elementsCount_wx8o20$_0;
    }
  });
  Object.defineProperty(SerialDescriptorImpl.prototype, 'isNullable', {
    get: function () {
      return this.isNullable_ur9fvr$_0;
    }
  });
  Object.defineProperty(SerialDescriptorImpl.prototype, 'annotations', {
    get: function () {
      return this.annotations_t3jslw$_0;
    }
  });
  SerialDescriptorImpl.prototype.getElementName_za3lpa$ = function (index) {
    return getChecked(this.elementNames_0, index);
  };
  SerialDescriptorImpl.prototype.getElementIndex_61zpoe$ = function (name) {
    var tmp$;
    return (tmp$ = this.name2Index_0.get_11rb$(name)) != null ? tmp$ : -3;
  };
  SerialDescriptorImpl.prototype.getElementAnnotations_za3lpa$ = function (index) {
    return getChecked(this.elementAnnotations_0, index);
  };
  SerialDescriptorImpl.prototype.getElementDescriptor_za3lpa$ = function (index) {
    return getChecked(this.elementDescriptors_0, index);
  };
  SerialDescriptorImpl.prototype.isElementOptional_za3lpa$ = function (index) {
    return getChecked_0(this.elementOptionality_0, index);
  };
  SerialDescriptorImpl.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, SerialDescriptor))
      return false;
    if (!equals(this.serialName, other.serialName))
      return false;
    return true;
  };
  SerialDescriptorImpl.prototype.hashCode = function () {
    return hashCode(this.serialName);
  };
  function SerialDescriptorImpl$toString$lambda(this$SerialDescriptorImpl) {
    return function (it) {
      return this$SerialDescriptorImpl.getElementName_za3lpa$(it) + ': ' + this$SerialDescriptorImpl.getElementDescriptor_za3lpa$(it).serialName;
    };
  }
  SerialDescriptorImpl.prototype.toString = function () {
    return joinToString(until(0, this.elementsCount), ', ', this.serialName + '(', ')', void 0, void 0, SerialDescriptorImpl$toString$lambda(this));
  };
  SerialDescriptorImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialDescriptorImpl',
    interfaces: [SerialDescriptor]
  };
  function SerialFormat() {
  }
  SerialFormat.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SerialFormat',
    interfaces: []
  };
  function AbstractSerialFormat(context) {
    this.context_fzkcjb$_0 = context;
  }
  Object.defineProperty(AbstractSerialFormat.prototype, 'context', {
    get: function () {
      return this.context_fzkcjb$_0;
    }
  });
  AbstractSerialFormat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractSerialFormat',
    interfaces: [SerialFormat]
  };
  function BinaryFormat() {
  }
  BinaryFormat.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'BinaryFormat',
    interfaces: [SerialFormat]
  };
  function dumps($receiver, serializer, value) {
    return InternalHexConverter_getInstance().printHexBinary_1fhb37$($receiver.dump_tf03ej$(serializer, value), true);
  }
  function loads($receiver, deserializer, hex) {
    return $receiver.load_dntfbn$(deserializer, InternalHexConverter_getInstance().parseHexBinary_61zpoe$(hex));
  }
  function StringFormat() {
  }
  StringFormat.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'StringFormat',
    interfaces: [SerialFormat]
  };
  function ImplicitReflectionSerializer() {
  }
  ImplicitReflectionSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ImplicitReflectionSerializer',
    interfaces: [Annotation]
  };
  function UnstableDefault() {
  }
  UnstableDefault.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnstableDefault',
    interfaces: [Annotation]
  };
  var dump = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.dump_nz3mh7$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, $receiver, value) {
      return $receiver.dump_tf03ej$(getContextualOrDefault($receiver.context, getKClass(T_0)), value);
    };
  }));
  var dumps_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.dumps_nz3mh7$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    var dumps = _.kotlinx.serialization.dumps_4yxkwp$;
    return function (T_0, isT, $receiver, value) {
      return dumps($receiver, getContextualOrDefault($receiver.context, getKClass(T_0)), value);
    };
  }));
  var load = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.load_716s99$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, $receiver, raw) {
      return $receiver.load_dntfbn$(getContextualOrDefault($receiver.context, getKClass(T_0)), raw);
    };
  }));
  var loads_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.loads_nps2g3$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    var loads = _.kotlinx.serialization.loads_f786sb$;
    return function (T_0, isT, $receiver, hex) {
      return loads($receiver, getContextualOrDefault($receiver.context, getKClass(T_0)), hex);
    };
  }));
  var stringify = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.stringify_f0yoh1$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, $receiver, value) {
      return $receiver.stringify_tf03ej$(getContextualOrDefault($receiver.context, getKClass(T_0)), value);
    };
  }));
  var stringify_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.stringify_y3khs0$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    var get_list = _.kotlinx.serialization.builtins.get_list_gekvwj$;
    return function (T_0, isT, $receiver, objects) {
      return $receiver.stringify_tf03ej$(get_list(getContextualOrDefault($receiver.context, getKClass(T_0))), objects);
    };
  }));
  var stringify_1 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.stringify_yz7s7b$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    var MapSerializer = _.kotlinx.serialization.builtins.MapSerializer_2yqygg$;
    return function (K_0, isK, V_0, isV, $receiver, map) {
      return $receiver.stringify_tf03ej$(MapSerializer(getContextualOrDefault($receiver.context, getKClass(K_0)), getContextualOrDefault($receiver.context, getKClass(V_0))), map);
    };
  }));
  var parse = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.parse_rw0txp$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, $receiver, str) {
      return $receiver.parse_awif5v$(getContextualOrDefault($receiver.context, getKClass(T_0)), str);
    };
  }));
  var parseList = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.parseList_rw0txp$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    var get_list = _.kotlinx.serialization.builtins.get_list_gekvwj$;
    return function (T_0, isT, $receiver, objects) {
      return $receiver.parse_awif5v$(get_list(getContextualOrDefault($receiver.context, getKClass(T_0))), objects);
    };
  }));
  var parseMap = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.parseMap_egzuvf$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    var MapSerializer = _.kotlinx.serialization.builtins.MapSerializer_2yqygg$;
    return function (K_0, isK, V_0, isV, $receiver, map) {
      return $receiver.parse_awif5v$(MapSerializer(getContextualOrDefault($receiver.context, getKClass(K_0)), getContextualOrDefault($receiver.context, getKClass(V_0))), map);
    };
  }));
  function SerialKind() {
  }
  SerialKind.prototype.toString = function () {
    return ensureNotNull(simpleName_0(Kotlin.getKClassFromExpression(this)));
  };
  SerialKind.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialKind',
    interfaces: []
  };
  function PrimitiveKind() {
    SerialKind.call(this);
  }
  function PrimitiveKind$BOOLEAN() {
    PrimitiveKind$BOOLEAN_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$BOOLEAN.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'BOOLEAN',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$BOOLEAN_instance = null;
  function PrimitiveKind$BOOLEAN_getInstance() {
    if (PrimitiveKind$BOOLEAN_instance === null) {
      new PrimitiveKind$BOOLEAN();
    }return PrimitiveKind$BOOLEAN_instance;
  }
  function PrimitiveKind$BYTE() {
    PrimitiveKind$BYTE_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$BYTE.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'BYTE',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$BYTE_instance = null;
  function PrimitiveKind$BYTE_getInstance() {
    if (PrimitiveKind$BYTE_instance === null) {
      new PrimitiveKind$BYTE();
    }return PrimitiveKind$BYTE_instance;
  }
  function PrimitiveKind$CHAR() {
    PrimitiveKind$CHAR_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$CHAR.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CHAR',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$CHAR_instance = null;
  function PrimitiveKind$CHAR_getInstance() {
    if (PrimitiveKind$CHAR_instance === null) {
      new PrimitiveKind$CHAR();
    }return PrimitiveKind$CHAR_instance;
  }
  function PrimitiveKind$SHORT() {
    PrimitiveKind$SHORT_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$SHORT.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SHORT',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$SHORT_instance = null;
  function PrimitiveKind$SHORT_getInstance() {
    if (PrimitiveKind$SHORT_instance === null) {
      new PrimitiveKind$SHORT();
    }return PrimitiveKind$SHORT_instance;
  }
  function PrimitiveKind$INT() {
    PrimitiveKind$INT_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$INT.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'INT',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$INT_instance = null;
  function PrimitiveKind$INT_getInstance() {
    if (PrimitiveKind$INT_instance === null) {
      new PrimitiveKind$INT();
    }return PrimitiveKind$INT_instance;
  }
  function PrimitiveKind$LONG() {
    PrimitiveKind$LONG_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$LONG.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LONG',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$LONG_instance = null;
  function PrimitiveKind$LONG_getInstance() {
    if (PrimitiveKind$LONG_instance === null) {
      new PrimitiveKind$LONG();
    }return PrimitiveKind$LONG_instance;
  }
  function PrimitiveKind$FLOAT() {
    PrimitiveKind$FLOAT_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$FLOAT.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'FLOAT',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$FLOAT_instance = null;
  function PrimitiveKind$FLOAT_getInstance() {
    if (PrimitiveKind$FLOAT_instance === null) {
      new PrimitiveKind$FLOAT();
    }return PrimitiveKind$FLOAT_instance;
  }
  function PrimitiveKind$DOUBLE() {
    PrimitiveKind$DOUBLE_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$DOUBLE.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DOUBLE',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$DOUBLE_instance = null;
  function PrimitiveKind$DOUBLE_getInstance() {
    if (PrimitiveKind$DOUBLE_instance === null) {
      new PrimitiveKind$DOUBLE();
    }return PrimitiveKind$DOUBLE_instance;
  }
  function PrimitiveKind$UNIT() {
    PrimitiveKind$UNIT_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$UNIT.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'UNIT',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$UNIT_instance = null;
  function PrimitiveKind$UNIT_getInstance() {
    if (PrimitiveKind$UNIT_instance === null) {
      new PrimitiveKind$UNIT();
    }return PrimitiveKind$UNIT_instance;
  }
  function PrimitiveKind$STRING() {
    PrimitiveKind$STRING_instance = this;
    PrimitiveKind.call(this);
  }
  PrimitiveKind$STRING.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'STRING',
    interfaces: [PrimitiveKind]
  };
  var PrimitiveKind$STRING_instance = null;
  function PrimitiveKind$STRING_getInstance() {
    if (PrimitiveKind$STRING_instance === null) {
      new PrimitiveKind$STRING();
    }return PrimitiveKind$STRING_instance;
  }
  PrimitiveKind.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveKind',
    interfaces: [SerialKind]
  };
  function StructureKind() {
    SerialKind.call(this);
  }
  function StructureKind$CLASS() {
    StructureKind$CLASS_instance = this;
    StructureKind.call(this);
  }
  StructureKind$CLASS.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CLASS',
    interfaces: [StructureKind]
  };
  var StructureKind$CLASS_instance = null;
  function StructureKind$CLASS_getInstance() {
    if (StructureKind$CLASS_instance === null) {
      new StructureKind$CLASS();
    }return StructureKind$CLASS_instance;
  }
  function StructureKind$LIST() {
    StructureKind$LIST_instance = this;
    StructureKind.call(this);
  }
  StructureKind$LIST.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LIST',
    interfaces: [StructureKind]
  };
  var StructureKind$LIST_instance = null;
  function StructureKind$LIST_getInstance() {
    if (StructureKind$LIST_instance === null) {
      new StructureKind$LIST();
    }return StructureKind$LIST_instance;
  }
  function StructureKind$MAP() {
    StructureKind$MAP_instance = this;
    StructureKind.call(this);
  }
  StructureKind$MAP.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'MAP',
    interfaces: [StructureKind]
  };
  var StructureKind$MAP_instance = null;
  function StructureKind$MAP_getInstance() {
    if (StructureKind$MAP_instance === null) {
      new StructureKind$MAP();
    }return StructureKind$MAP_instance;
  }
  function StructureKind$OBJECT() {
    StructureKind$OBJECT_instance = this;
    StructureKind.call(this);
  }
  StructureKind$OBJECT.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'OBJECT',
    interfaces: [StructureKind]
  };
  var StructureKind$OBJECT_instance = null;
  function StructureKind$OBJECT_getInstance() {
    if (StructureKind$OBJECT_instance === null) {
      new StructureKind$OBJECT();
    }return StructureKind$OBJECT_instance;
  }
  StructureKind.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StructureKind',
    interfaces: [SerialKind]
  };
  function UnionKind() {
    UnionKind$Companion_getInstance();
    SerialKind.call(this);
  }
  function UnionKind$ENUM_KIND() {
    UnionKind$ENUM_KIND_instance = this;
    UnionKind.call(this);
  }
  UnionKind$ENUM_KIND.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ENUM_KIND',
    interfaces: [UnionKind]
  };
  var UnionKind$ENUM_KIND_instance = null;
  function UnionKind$ENUM_KIND_getInstance() {
    if (UnionKind$ENUM_KIND_instance === null) {
      new UnionKind$ENUM_KIND();
    }return UnionKind$ENUM_KIND_instance;
  }
  function UnionKind$CONTEXTUAL() {
    UnionKind$CONTEXTUAL_instance = this;
    UnionKind.call(this);
  }
  UnionKind$CONTEXTUAL.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CONTEXTUAL',
    interfaces: [UnionKind]
  };
  var UnionKind$CONTEXTUAL_instance = null;
  function UnionKind$CONTEXTUAL_getInstance() {
    if (UnionKind$CONTEXTUAL_instance === null) {
      new UnionKind$CONTEXTUAL();
    }return UnionKind$CONTEXTUAL_instance;
  }
  function UnionKind$Companion() {
    UnionKind$Companion_instance = this;
    this.OBJECT = StructureKind$OBJECT_getInstance();
  }
  UnionKind$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var UnionKind$Companion_instance = null;
  function UnionKind$Companion_getInstance() {
    if (UnionKind$Companion_instance === null) {
      new UnionKind$Companion();
    }return UnionKind$Companion_instance;
  }
  UnionKind.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnionKind',
    interfaces: [SerialKind]
  };
  function PolymorphicKind() {
    SerialKind.call(this);
  }
  function PolymorphicKind$SEALED() {
    PolymorphicKind$SEALED_instance = this;
    PolymorphicKind.call(this);
  }
  PolymorphicKind$SEALED.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SEALED',
    interfaces: [PolymorphicKind]
  };
  var PolymorphicKind$SEALED_instance = null;
  function PolymorphicKind$SEALED_getInstance() {
    if (PolymorphicKind$SEALED_instance === null) {
      new PolymorphicKind$SEALED();
    }return PolymorphicKind$SEALED_instance;
  }
  function PolymorphicKind$OPEN() {
    PolymorphicKind$OPEN_instance = this;
    PolymorphicKind.call(this);
  }
  PolymorphicKind$OPEN.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'OPEN',
    interfaces: [PolymorphicKind]
  };
  var PolymorphicKind$OPEN_instance = null;
  function PolymorphicKind$OPEN_getInstance() {
    if (PolymorphicKind$OPEN_instance === null) {
      new PolymorphicKind$OPEN();
    }return PolymorphicKind$OPEN_instance;
  }
  PolymorphicKind.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PolymorphicKind',
    interfaces: [SerialKind]
  };
  var serializer_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.serializer_287e2$', wrapFunction(function () {
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT) {
      var tmp$;
      return Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE();
    };
  }));
  function serializer$serializerByKTypeImpl(type) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var t = type.classifier;
    if (Kotlin.isType(t, KClass))
      tmp$ = t;
    else {
      throw IllegalStateException_init(('Only KClass supported as classifier, got ' + toString(t)).toString());
    }
    var rootClass = Kotlin.isType(tmp$_0 = tmp$, KClass) ? tmp$_0 : throwCCE();
    var $receiver = type.arguments;
    var destination = ArrayList_init(collectionSizeOrDefault($receiver, 10));
    var tmp$_3;
    tmp$_3 = $receiver.iterator();
    while (tmp$_3.hasNext()) {
      var item = tmp$_3.next();
      var tmp$_4 = destination.add_11rb$;
      var value = item.type;
      var requireNotNull$result;
      if (value == null) {
        var message = 'Star projections are not allowed, had ' + item + ' instead';
        throw IllegalArgumentException_init(message.toString());
      } else {
        requireNotNull$result = value;
      }
      tmp$_4.call(destination, requireNotNull$result);
    }
    var typeArguments = destination;
    if (typeArguments.isEmpty())
      tmp$_2 = serializer(rootClass);
    else {
      var destination_0 = ArrayList_init(collectionSizeOrDefault(typeArguments, 10));
      var tmp$_5;
      tmp$_5 = typeArguments.iterator();
      while (tmp$_5.hasNext()) {
        var item_0 = tmp$_5.next();
        destination_0.add_11rb$(serializer_1(item_0));
      }
      var serializers = destination_0;
      if (equals(rootClass, getKClass(List)) || equals(rootClass, getKClass(MutableList)) || equals(rootClass, getKClass(ArrayList)))
        tmp$_2 = new ArrayListSerializer(serializers.get_za3lpa$(0));
      else if (equals(rootClass, getKClass(HashSet)))
        tmp$_2 = new HashSetSerializer(serializers.get_za3lpa$(0));
      else if (equals(rootClass, getKClass(Set)) || equals(rootClass, getKClass(MutableSet)) || equals(rootClass, getKClass(LinkedHashSet)))
        tmp$_2 = new LinkedHashSetSerializer(serializers.get_za3lpa$(0));
      else if (equals(rootClass, getKClass(HashMap)))
        tmp$_2 = new HashMapSerializer(serializers.get_za3lpa$(0), serializers.get_za3lpa$(1));
      else if (equals(rootClass, getKClass(Map)) || equals(rootClass, getKClass(MutableMap)) || equals(rootClass, getKClass(LinkedHashMap)))
        tmp$_2 = new LinkedHashMapSerializer(serializers.get_za3lpa$(0), serializers.get_za3lpa$(1));
      else if (equals(rootClass, getKClass(Map$Entry)))
        tmp$_2 = MapEntrySerializer(serializers.get_za3lpa$(0), serializers.get_za3lpa$(1));
      else if (equals(rootClass, getKClass(Pair)))
        tmp$_2 = PairSerializer(serializers.get_za3lpa$(0), serializers.get_za3lpa$(1));
      else if (equals(rootClass, getKClass(Triple)))
        tmp$_2 = TripleSerializer(serializers.get_za3lpa$(0), serializers.get_za3lpa$(1), serializers.get_za3lpa$(2));
      else {
        if (isReferenceArray(type, rootClass)) {
          var tmp$_6;
          return Kotlin.isType(tmp$_6 = ArraySerializer_0(Kotlin.isType(tmp$_1 = typeArguments.get_za3lpa$(0).classifier, KClass) ? tmp$_1 : throwCCE(), serializers.get_za3lpa$(0)), KSerializer) ? tmp$_6 : throwCCE();
        }var value_0 = constructSerializerForGivenTypeArgs(rootClass, copyToArray(serializers).slice());
        var requireNotNull$result_0;
        if (value_0 == null) {
          var message_0 = "Can't find a method to construct serializer for type " + toString(simpleName_0(rootClass)) + '. ' + 'Make sure this class is marked as @Serializable or provide serializer explicitly.';
          throw IllegalArgumentException_init(message_0.toString());
        } else {
          requireNotNull$result_0 = value_0;
        }
        tmp$_2 = requireNotNull$result_0;
      }
    }
    var tmp$_7;
    return Kotlin.isType(tmp$_7 = tmp$_2, KSerializer) ? tmp$_7 : throwCCE();
  }
  function serializer_1(type) {
    var serializerByKTypeImpl = serializer$serializerByKTypeImpl;
    var result = serializerByKTypeImpl(type);
    var tmp$;
    if (type.isMarkedNullable)
      tmp$ = get_nullable_1(result);
    else {
      var tmp$_0;
      tmp$ = Kotlin.isType(tmp$_0 = result, KSerializer) ? tmp$_0 : throwCCE();
    }
    return tmp$;
  }
  function AbstractDecoder() {
    this.updateMode_uwb1ev$_0 = UpdateMode$UPDATE_getInstance();
  }
  Object.defineProperty(AbstractDecoder.prototype, 'context', {
    get: function () {
      return EmptyModule_getInstance();
    }
  });
  Object.defineProperty(AbstractDecoder.prototype, 'updateMode', {
    get: function () {
      return this.updateMode_uwb1ev$_0;
    }
  });
  AbstractDecoder.prototype.decodeValue = function () {
    throw new SerializationException(Kotlin.getKClassFromExpression(this).toString() + " can't retrieve untyped values");
  };
  AbstractDecoder.prototype.decodeNotNullMark = function () {
    return true;
  };
  AbstractDecoder.prototype.decodeNull = function () {
    return null;
  };
  AbstractDecoder.prototype.decodeUnit = function () {
    UnitSerializer().deserialize_nts5qn$(this);
  };
  AbstractDecoder.prototype.decodeBoolean = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'boolean' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeByte = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'number' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeShort = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'number' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeInt = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'number' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeLong = function () {
    var tmp$;
    return Kotlin.isType(tmp$ = this.decodeValue(), Kotlin.Long) ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeFloat = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'number' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeDouble = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'number' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeChar = function () {
    var tmp$;
    return Kotlin.isChar(tmp$ = this.decodeValue()) ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeString = function () {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'string' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.decodeEnum_qatsm0$ = function (enumDescriptor) {
    var tmp$;
    return typeof (tmp$ = this.decodeValue()) === 'number' ? tmp$ : throwCCE();
  };
  AbstractDecoder.prototype.beginStructure_r0sa6z$ = function (descriptor, typeParams) {
    return this;
  };
  AbstractDecoder.prototype.endStructure_qatsm0$ = function (descriptor) {
  };
  AbstractDecoder.prototype.decodeUnitElement_3zr2iy$ = function (descriptor, index) {
    this.decodeUnit();
  };
  AbstractDecoder.prototype.decodeBooleanElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeBoolean();
  };
  AbstractDecoder.prototype.decodeByteElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeByte();
  };
  AbstractDecoder.prototype.decodeShortElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeShort();
  };
  AbstractDecoder.prototype.decodeIntElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeInt();
  };
  AbstractDecoder.prototype.decodeLongElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeLong();
  };
  AbstractDecoder.prototype.decodeFloatElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeFloat();
  };
  AbstractDecoder.prototype.decodeDoubleElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeDouble();
  };
  AbstractDecoder.prototype.decodeCharElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeChar();
  };
  AbstractDecoder.prototype.decodeStringElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeString();
  };
  AbstractDecoder.prototype.decodeSerializableElement_s44l7r$ = function (descriptor, index, deserializer) {
    return this.decodeSerializableValue_w63s0f$(deserializer);
  };
  AbstractDecoder.prototype.decodeNullableSerializableElement_cwlm4k$ = function (descriptor, index, deserializer) {
    return this.decodeNullableSerializableValue_aae3ea$(deserializer);
  };
  AbstractDecoder.prototype.updateSerializableElement_ehubvl$ = function (descriptor, index, deserializer, old) {
    return this.updateSerializableValue_19c8k5$(deserializer, old);
  };
  AbstractDecoder.prototype.updateNullableSerializableElement_u33s02$ = function (descriptor, index, deserializer, old) {
    return this.updateNullableSerializableValue_exmlbs$(deserializer, old);
  };
  AbstractDecoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractDecoder',
    interfaces: [CompositeDecoder, Decoder]
  };
  function AbstractEncoder() {
  }
  Object.defineProperty(AbstractEncoder.prototype, 'context', {
    get: function () {
      return EmptyModule_getInstance();
    }
  });
  AbstractEncoder.prototype.beginStructure_r0sa6z$ = function (descriptor, typeSerializers) {
    return this;
  };
  AbstractEncoder.prototype.encodeElement_3zr2iy$ = function (descriptor, index) {
    return true;
  };
  AbstractEncoder.prototype.encodeValue_za3rmp$ = function (value) {
    throw new SerializationException('Non-serializable ' + Kotlin.getKClassFromExpression(value) + ' is not supported by ' + Kotlin.getKClassFromExpression(this) + ' encoder');
  };
  AbstractEncoder.prototype.encodeNull = function () {
    throw new SerializationException("'null' is not supported by default");
  };
  AbstractEncoder.prototype.encodeUnit = function () {
    UnitSerializer().serialize_awe97i$(this, Unit);
  };
  AbstractEncoder.prototype.encodeBoolean_6taknv$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeByte_s8j3t7$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeShort_mq22fl$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeInt_za3lpa$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeLong_s8cxhz$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeFloat_mx4ult$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeDouble_14dthe$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeChar_s8itvh$ = function (value) {
    this.encodeValue_za3rmp$(toBoxedChar(value));
  };
  AbstractEncoder.prototype.encodeString_61zpoe$ = function (value) {
    this.encodeValue_za3rmp$(value);
  };
  AbstractEncoder.prototype.encodeEnum_3zr2iy$ = function (enumDescriptor, index) {
    this.encodeValue_za3rmp$(index);
  };
  AbstractEncoder.prototype.encodeUnitElement_3zr2iy$ = function (descriptor, index) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeUnit();
  };
  AbstractEncoder.prototype.encodeBooleanElement_w1b0nl$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeBoolean_6taknv$(value);
  };
  AbstractEncoder.prototype.encodeByteElement_a3tadb$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeByte_s8j3t7$(value);
  };
  AbstractEncoder.prototype.encodeShortElement_tet9k5$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeShort_mq22fl$(value);
  };
  AbstractEncoder.prototype.encodeIntElement_4wpqag$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeInt_za3lpa$(value);
  };
  AbstractEncoder.prototype.encodeLongElement_a3zgoj$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeLong_s8cxhz$(value);
  };
  AbstractEncoder.prototype.encodeFloatElement_t7qhdx$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeFloat_mx4ult$(value);
  };
  AbstractEncoder.prototype.encodeDoubleElement_imzr5k$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeDouble_14dthe$(value);
  };
  AbstractEncoder.prototype.encodeCharElement_a3tkb1$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeChar_s8itvh$(value);
  };
  AbstractEncoder.prototype.encodeStringElement_bgm7zs$ = function (descriptor, index, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeString_61zpoe$(value);
  };
  AbstractEncoder.prototype.encodeSerializableElement_blecud$ = function (descriptor, index, serializer, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeSerializableValue_tf03ej$(serializer, value);
  };
  AbstractEncoder.prototype.encodeNullableSerializableElement_orpvvi$ = function (descriptor, index, serializer, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeNullableSerializableValue_f4686g$(serializer, value);
  };
  AbstractEncoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractEncoder',
    interfaces: [CompositeEncoder, Encoder]
  };
  function get_nullable_1($receiver) {
    var tmp$;
    return $receiver.descriptor.isNullable ? Kotlin.isType(tmp$ = $receiver, KSerializer) ? tmp$ : throwCCE() : new NullableSerializer($receiver);
  }
  function PairSerializer(keySerializer, valueSerializer) {
    return new PairSerializer_0(keySerializer, valueSerializer);
  }
  function MapEntrySerializer(keySerializer, valueSerializer) {
    return new MapEntrySerializer_0(keySerializer, valueSerializer);
  }
  function TripleSerializer(aSerializer, bSerializer, cSerializer) {
    return new TripleSerializer_0(aSerializer, bSerializer, cSerializer);
  }
  function get_list_0($receiver) {
    return new ArrayListSerializer($receiver);
  }
  function ListSerializer(elementSerializer) {
    return new ArrayListSerializer(elementSerializer);
  }
  function get_set_0($receiver) {
    return new LinkedHashSetSerializer($receiver);
  }
  function SetSerializer(elementSerializer) {
    return new LinkedHashSetSerializer(elementSerializer);
  }
  function MapSerializer(keySerializer, valueSerializer) {
    return new LinkedHashMapSerializer(keySerializer, valueSerializer);
  }
  function serializer_2($receiver) {
    return CharSerializer_getInstance();
  }
  function CharArraySerializer() {
    return CharArraySerializer_getInstance();
  }
  function serializer_3($receiver) {
    return ByteSerializer_getInstance();
  }
  function ByteArraySerializer() {
    return ByteArraySerializer_getInstance();
  }
  function serializer_4($receiver) {
    return ShortSerializer_getInstance();
  }
  function ShortArraySerializer() {
    return ShortArraySerializer_getInstance();
  }
  function serializer_5($receiver) {
    return IntSerializer_getInstance();
  }
  function IntArraySerializer() {
    return IntArraySerializer_getInstance();
  }
  function serializer_6($receiver) {
    return LongSerializer_getInstance();
  }
  function LongArraySerializer() {
    return LongArraySerializer_getInstance();
  }
  function serializer_7($receiver) {
    return FloatSerializer_getInstance();
  }
  function FloatArraySerializer() {
    return FloatArraySerializer_getInstance();
  }
  function serializer_8($receiver) {
    return DoubleSerializer_getInstance();
  }
  function DoubleArraySerializer() {
    return DoubleArraySerializer_getInstance();
  }
  function serializer_9($receiver) {
    return BooleanSerializer_getInstance();
  }
  function BooleanArraySerializer() {
    return BooleanArraySerializer_getInstance();
  }
  function UnitSerializer() {
    return UnitSerializer_getInstance();
  }
  function serializer_10($receiver) {
    return StringSerializer_getInstance();
  }
  var ArraySerializer = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.builtins.ArraySerializer_furkhx$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var ArraySerializer = _.kotlinx.serialization.builtins.ArraySerializer_8tn5u0$;
    return function (T_0, isT, E_0, isE, elementSerializer) {
      return ArraySerializer(getKClass(T_0), elementSerializer);
    };
  }));
  function ArraySerializer_0(kClass, elementSerializer) {
    return new ReferenceArraySerializer(kClass, elementSerializer);
  }
  function AbstractPolymorphicSerializer() {
  }
  function AbstractPolymorphicSerializer$serialize$lambda(this$AbstractPolymorphicSerializer, closure$actualSerializer, closure$value) {
    return function ($receiver) {
      $receiver.encodeStringElement_bgm7zs$(this$AbstractPolymorphicSerializer.descriptor, 0, closure$actualSerializer.descriptor.serialName);
      var tmp$;
      $receiver.encodeSerializableElement_blecud$(this$AbstractPolymorphicSerializer.descriptor, 1, Kotlin.isType(tmp$ = closure$actualSerializer, KSerializer) ? tmp$ : throwCCE(), closure$value);
      return Unit;
    };
  }
  AbstractPolymorphicSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var actualSerializer = this.findPolymorphicSerializer_7kuzo6$(encoder, value);
    var descriptor = this.descriptor;
    var composite = encoder.beginStructure_r0sa6z$(descriptor, []);
    AbstractPolymorphicSerializer$serialize$lambda(this, actualSerializer, value)(composite);
    composite.endStructure_qatsm0$(descriptor);
  };
  function AbstractPolymorphicSerializer$deserialize$lambda(this$AbstractPolymorphicSerializer) {
    return function ($receiver) {
      var tmp$, tmp$_0;
      var klassName = {v: null};
      var value = null;
      if ($receiver.decodeSequentially()) {
        return this$AbstractPolymorphicSerializer.decodeSequentially_lnremq$_0($receiver);
      }mainLoop: while (true) {
        var index = $receiver.decodeElementIndex_qatsm0$(this$AbstractPolymorphicSerializer.descriptor);
        switch (index) {
          case -1:
            break mainLoop;
          case 0:
            klassName.v = $receiver.decodeStringElement_3zr2iy$(this$AbstractPolymorphicSerializer.descriptor, index);
            break;
          case 1:
            var value_0 = klassName.v;
            var requireNotNull$result;
            if (value_0 == null) {
              var message = 'Cannot read polymorphic value before its type token';
              throw IllegalArgumentException_init(message.toString());
            } else {
              requireNotNull$result = value_0;
            }

            klassName.v = requireNotNull$result;
            var serializer = this$AbstractPolymorphicSerializer.findPolymorphicSerializer_b69zac$($receiver, klassName.v);
            value = $receiver.decodeSerializableElement_s44l7r$(this$AbstractPolymorphicSerializer.descriptor, index, serializer);
            break;
          default:throw new SerializationException('Invalid index in polymorphic deserialization of ' + toString((tmp$ = klassName.v) != null ? tmp$ : 'unknown class') + ('\n' + ' Expected 0, 1 or READ_DONE(-1), but found ' + index));
        }
      }
      var value_1 = value;
      var requireNotNull$result_0;
      if (value_1 == null) {
        var message_0 = 'Polymorphic value has not been read for class ' + toString(klassName.v);
        throw IllegalArgumentException_init(message_0.toString());
      } else {
        requireNotNull$result_0 = value_1;
      }
      return Kotlin.isType(tmp$_0 = requireNotNull$result_0, Any) ? tmp$_0 : throwCCE();
    };
  }
  AbstractPolymorphicSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var descriptor = this.descriptor;
    var composite = decoder.beginStructure_r0sa6z$(descriptor, []);
    var result = AbstractPolymorphicSerializer$deserialize$lambda(this)(composite);
    composite.endStructure_qatsm0$(descriptor);
    return result;
  };
  AbstractPolymorphicSerializer.prototype.decodeSequentially_lnremq$_0 = function (compositeDecoder) {
    var klassName = compositeDecoder.decodeStringElement_3zr2iy$(this.descriptor, 0);
    var serializer = this.findPolymorphicSerializer_b69zac$(compositeDecoder, klassName);
    var value = compositeDecoder.decodeSerializableElement_s44l7r$(this.descriptor, 1, serializer);
    compositeDecoder.endStructure_qatsm0$(this.descriptor);
    return value;
  };
  AbstractPolymorphicSerializer.prototype.findPolymorphicSerializer_b69zac$ = function (decoder, klassName) {
    var tmp$;
    return (tmp$ = decoder.context.getPolymorphic_6xtsla$(this.baseClass, klassName)) != null ? tmp$ : throwSubtypeNotRegistered(klassName, this.baseClass);
  };
  AbstractPolymorphicSerializer.prototype.findPolymorphicSerializer_7kuzo6$ = function (encoder, value) {
    var tmp$;
    return (tmp$ = encoder.context.getPolymorphic_b1ce0a$(this.baseClass, value)) != null ? tmp$ : throwSubtypeNotRegistered_0(Kotlin.getKClassFromExpression(value), this.baseClass);
  };
  AbstractPolymorphicSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractPolymorphicSerializer',
    interfaces: [KSerializer]
  };
  function throwSubtypeNotRegistered(subClassName, baseClass) {
    throw new SerializationException(subClassName + ' is not registered for polymorphic serialization in the scope of ' + baseClass);
  }
  function throwSubtypeNotRegistered_0(subClass, baseClass) {
    return throwSubtypeNotRegistered(subClass.toString(), baseClass);
  }
  function ListLikeDescriptor(elementDesc) {
    this.elementDesc = elementDesc;
    this.elementsCount_axr0xc$_0 = 1;
  }
  Object.defineProperty(ListLikeDescriptor.prototype, 'kind', {
    get: function () {
      return StructureKind$LIST_getInstance();
    }
  });
  Object.defineProperty(ListLikeDescriptor.prototype, 'elementsCount', {
    get: function () {
      return this.elementsCount_axr0xc$_0;
    }
  });
  ListLikeDescriptor.prototype.getElementName_za3lpa$ = function (index) {
    return index.toString();
  };
  ListLikeDescriptor.prototype.getElementIndex_61zpoe$ = function (name) {
    var tmp$;
    tmp$ = toIntOrNull(name);
    if (tmp$ == null) {
      throw IllegalArgumentException_init(name + ' is not a valid list index');
    }return tmp$;
  };
  ListLikeDescriptor.prototype.isElementOptional_za3lpa$ = function (index) {
    if (index !== 0)
      throw IllegalStateException_init('List descriptor has only one child element, index: ' + index);
    return false;
  };
  ListLikeDescriptor.prototype.getElementAnnotations_za3lpa$ = function (index) {
    if (index !== 0)
      throw new IndexOutOfBoundsException('List descriptor has only one child element, index: ' + index);
    return emptyList();
  };
  ListLikeDescriptor.prototype.getElementDescriptor_za3lpa$ = function (index) {
    if (index !== 0)
      throw new IndexOutOfBoundsException('List descriptor has only one child element, index: ' + index);
    return this.elementDesc;
  };
  ListLikeDescriptor.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, ListLikeDescriptor))
      return false;
    if (equals(this.elementDesc, other.elementDesc) && equals(this.serialName, other.serialName))
      return true;
    return false;
  };
  ListLikeDescriptor.prototype.hashCode = function () {
    return (hashCode(this.elementDesc) * 31 | 0) + hashCode(this.serialName) | 0;
  };
  ListLikeDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ListLikeDescriptor',
    interfaces: [SerialDescriptor]
  };
  function MapLikeDescriptor(serialName, keyDescriptor, valueDescriptor) {
    this.serialName_ra35nx$_0 = serialName;
    this.keyDescriptor = keyDescriptor;
    this.valueDescriptor = valueDescriptor;
    this.elementsCount_qp2ocq$_0 = 2;
  }
  Object.defineProperty(MapLikeDescriptor.prototype, 'serialName', {
    get: function () {
      return this.serialName_ra35nx$_0;
    }
  });
  Object.defineProperty(MapLikeDescriptor.prototype, 'kind', {
    get: function () {
      return StructureKind$MAP_getInstance();
    }
  });
  Object.defineProperty(MapLikeDescriptor.prototype, 'elementsCount', {
    get: function () {
      return this.elementsCount_qp2ocq$_0;
    }
  });
  MapLikeDescriptor.prototype.getElementName_za3lpa$ = function (index) {
    return index.toString();
  };
  MapLikeDescriptor.prototype.getElementIndex_61zpoe$ = function (name) {
    var tmp$;
    tmp$ = toIntOrNull(name);
    if (tmp$ == null) {
      throw IllegalArgumentException_init(name + ' is not a valid map index');
    }return tmp$;
  };
  MapLikeDescriptor.prototype.isElementOptional_za3lpa$ = function (index) {
    if (!(0 <= index && index <= 1))
      throw IllegalStateException_init('Map descriptor has only two child elements, index: ' + index);
    return false;
  };
  MapLikeDescriptor.prototype.getElementAnnotations_za3lpa$ = function (index) {
    if (!(0 <= index && index <= 1))
      throw new IndexOutOfBoundsException('Map descriptor has only two child elements, index: ' + index);
    return emptyList();
  };
  MapLikeDescriptor.prototype.getElementDescriptor_za3lpa$ = function (index) {
    switch (index) {
      case 0:
        return this.keyDescriptor;
      case 1:
        return this.valueDescriptor;
      default:throw new IndexOutOfBoundsException('Map descriptor has only one child element, index: ' + index);
    }
  };
  MapLikeDescriptor.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, MapLikeDescriptor))
      return false;
    if (!equals(this.serialName, other.serialName))
      return false;
    if (!equals(this.keyDescriptor, other.keyDescriptor))
      return false;
    if (!equals(this.valueDescriptor, other.valueDescriptor))
      return false;
    return true;
  };
  MapLikeDescriptor.prototype.hashCode = function () {
    var result = hashCode(this.serialName);
    result = (31 * result | 0) + hashCode(this.keyDescriptor) | 0;
    result = (31 * result | 0) + hashCode(this.valueDescriptor) | 0;
    return result;
  };
  MapLikeDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapLikeDescriptor',
    interfaces: [SerialDescriptor]
  };
  var ARRAY_NAME;
  var ARRAY_LIST_NAME;
  var LINKED_HASH_SET_NAME;
  var HASH_SET_NAME;
  var LINKED_HASH_MAP_NAME;
  var HASH_MAP_NAME;
  function PrimitiveArrayDescriptor(primitive) {
    ListLikeDescriptor.call(this, primitive);
    this.serialName_reprdi$_0 = primitive.serialName + 'Array';
  }
  Object.defineProperty(PrimitiveArrayDescriptor.prototype, 'serialName', {
    get: function () {
      return this.serialName_reprdi$_0;
    }
  });
  PrimitiveArrayDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveArrayDescriptor',
    interfaces: [ListLikeDescriptor]
  };
  function ArrayClassDesc(elementDesc) {
    ListLikeDescriptor.call(this, elementDesc);
  }
  Object.defineProperty(ArrayClassDesc.prototype, 'serialName', {
    get: function () {
      return ARRAY_NAME;
    }
  });
  ArrayClassDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ArrayClassDesc',
    interfaces: [ListLikeDescriptor]
  };
  function ArrayListClassDesc(elementDesc) {
    ListLikeDescriptor.call(this, elementDesc);
  }
  Object.defineProperty(ArrayListClassDesc.prototype, 'serialName', {
    get: function () {
      return ARRAY_LIST_NAME;
    }
  });
  ArrayListClassDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ArrayListClassDesc',
    interfaces: [ListLikeDescriptor]
  };
  function NamedListClassDescriptor(serialName, elementDescriptor) {
    ListLikeDescriptor.call(this, elementDescriptor);
    this.serialName_ej0k1l$_0 = serialName;
  }
  Object.defineProperty(NamedListClassDescriptor.prototype, 'serialName', {
    get: function () {
      return this.serialName_ej0k1l$_0;
    }
  });
  NamedListClassDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NamedListClassDescriptor',
    interfaces: [ListLikeDescriptor]
  };
  function LinkedHashSetClassDesc(elementDesc) {
    ListLikeDescriptor.call(this, elementDesc);
  }
  Object.defineProperty(LinkedHashSetClassDesc.prototype, 'serialName', {
    get: function () {
      return LINKED_HASH_SET_NAME;
    }
  });
  LinkedHashSetClassDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedHashSetClassDesc',
    interfaces: [ListLikeDescriptor]
  };
  function HashSetClassDesc(elementDesc) {
    ListLikeDescriptor.call(this, elementDesc);
  }
  Object.defineProperty(HashSetClassDesc.prototype, 'serialName', {
    get: function () {
      return HASH_SET_NAME;
    }
  });
  HashSetClassDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HashSetClassDesc',
    interfaces: [ListLikeDescriptor]
  };
  function NamedMapClassDescriptor(name, keyDescriptor, valueDescriptor) {
    MapLikeDescriptor.call(this, name, keyDescriptor, valueDescriptor);
  }
  NamedMapClassDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NamedMapClassDescriptor',
    interfaces: [MapLikeDescriptor]
  };
  function LinkedHashMapClassDesc(keyDesc, valueDesc) {
    MapLikeDescriptor.call(this, LINKED_HASH_MAP_NAME, keyDesc, valueDesc);
  }
  LinkedHashMapClassDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedHashMapClassDesc',
    interfaces: [MapLikeDescriptor]
  };
  function HashMapClassDesc(keyDesc, valueDesc) {
    MapLikeDescriptor.call(this, HASH_MAP_NAME, keyDesc, valueDesc);
  }
  HashMapClassDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HashMapClassDesc',
    interfaces: [MapLikeDescriptor]
  };
  function AbstractCollectionSerializer() {
  }
  AbstractCollectionSerializer.prototype.patch_mynpiu$ = function (decoder, old) {
    var builder = this.toBuilder_wikn$(old);
    var startIndex = this.builderSize_wili$(builder);
    var compositeDecoder = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    if (compositeDecoder.decodeSequentially()) {
      this.readAll_uzf5cf$(compositeDecoder, builder, startIndex, this.readSize_mes5ce$_0(compositeDecoder, builder));
    } else {
      while (true) {
        var index = compositeDecoder.decodeElementIndex_qatsm0$(this.descriptor);
        if (index === -1)
          break;
        this.readElement_ind1ny$(compositeDecoder, startIndex + index | 0, builder);
      }
    }
    compositeDecoder.endStructure_qatsm0$(this.descriptor);
    return this.toResult_wili$(builder);
  };
  AbstractCollectionSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var builder = this.builder();
    return this.patch_mynpiu$(decoder, this.toResult_wili$(builder));
  };
  AbstractCollectionSerializer.prototype.readSize_mes5ce$_0 = function (decoder, builder) {
    var size = decoder.decodeCollectionSize_qatsm0$(this.descriptor);
    this.checkCapacity_rk7bw8$(builder, size);
    return size;
  };
  AbstractCollectionSerializer.prototype.readElement_ind1ny$ = function (decoder, index, builder, checkIndex, callback$default) {
    if (checkIndex === void 0)
      checkIndex = true;
    callback$default ? callback$default(decoder, index, builder, checkIndex) : this.readElement_ind1ny$$default(decoder, index, builder, checkIndex);
  };
  AbstractCollectionSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractCollectionSerializer',
    interfaces: [KSerializer]
  };
  function ListLikeSerializer(elementSerializer) {
    AbstractCollectionSerializer.call(this);
    this.elementSerializer_6sofm1$_0 = elementSerializer;
    this.typeParams_thbhbl$_0 = [this.elementSerializer_6sofm1$_0];
  }
  Object.defineProperty(ListLikeSerializer.prototype, 'typeParams', {
    get: function () {
      return this.typeParams_thbhbl$_0;
    }
  });
  ListLikeSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var size = this.collectionSize_wikn$(value);
    var encoder_0 = encoder.beginCollection_gly1x5$(this.descriptor, size, this.typeParams.slice());
    var iterator = this.collectionIterator_wikn$(value);
    for (var index = 0; index < size; index++)
      encoder_0.encodeSerializableElement_blecud$(this.descriptor, index, this.elementSerializer_6sofm1$_0, iterator.next());
    encoder_0.endStructure_qatsm0$(this.descriptor);
  };
  ListLikeSerializer.prototype.readAll_uzf5cf$ = function (decoder, builder, startIndex, size) {
    if (!(size >= 0)) {
      var message = 'Size must be known in advance when using READ_ALL';
      throw IllegalArgumentException_init(message.toString());
    }for (var index = 0; index < size; index++)
      this.readElement_ind1ny$(decoder, startIndex + index | 0, builder, false);
  };
  ListLikeSerializer.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    this.insert_p422l$(builder, index, decoder.decodeSerializableElement_s44l7r$(this.descriptor, index, this.elementSerializer_6sofm1$_0));
  };
  ListLikeSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ListLikeSerializer',
    interfaces: [AbstractCollectionSerializer]
  };
  function MapLikeSerializer(keySerializer, valueSerializer) {
    AbstractCollectionSerializer.call(this);
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
    this.typeParams_jdi5pn$_0 = [this.keySerializer, this.valueSerializer];
  }
  Object.defineProperty(MapLikeSerializer.prototype, 'typeParams', {
    get: function () {
      return this.typeParams_jdi5pn$_0;
    }
  });
  MapLikeSerializer.prototype.readAll_uzf5cf$ = function (decoder, builder, startIndex, size) {
    var tmp$;
    if (!(size >= 0)) {
      var message = 'Size must be known in advance when using READ_ALL';
      throw IllegalArgumentException_init(message.toString());
    }tmp$ = size * 2 | 0;
    for (var index = 0; index < tmp$; index += 2)
      this.readElement_ind1ny$(decoder, startIndex + index | 0, builder, false);
  };
  MapLikeSerializer.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    var tmp$, tmp$_0;
    var key = decoder.decodeSerializableElement_s44l7r$(this.descriptor, index, this.keySerializer);
    if (checkIndex) {
      var $receiver = decoder.decodeElementIndex_qatsm0$(this.descriptor);
      if (!($receiver === (index + 1 | 0))) {
        var message = 'Value must follow key in a map, index for key: ' + index + ', returned index for value: ' + $receiver;
        throw IllegalArgumentException_init(message.toString());
      }tmp$ = $receiver;
    } else {
      tmp$ = index + 1 | 0;
    }
    var vIndex = tmp$;
    if (builder.containsKey_11rb$(key) && !Kotlin.isType(this.valueSerializer.descriptor.kind, PrimitiveKind)) {
      tmp$_0 = decoder.updateSerializableElement_ehubvl$(this.descriptor, vIndex, this.valueSerializer, getValue(builder, key));
    } else {
      tmp$_0 = decoder.decodeSerializableElement_s44l7r$(this.descriptor, vIndex, this.valueSerializer);
    }
    var value = tmp$_0;
    builder.put_xwzc9p$(key, value);
  };
  MapLikeSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var size = this.collectionSize_wikn$(value);
    var composite = encoder.beginCollection_gly1x5$(this.descriptor, size, this.typeParams.slice());
    var iterator = this.collectionIterator_wikn$(value);
    var index = {v: 0};
    while (iterator.hasNext()) {
      var element = iterator.next();
      var k = element.key;
      var v = element.value;
      var tmp$, tmp$_0;
      composite.encodeSerializableElement_blecud$(this.descriptor, (tmp$ = index.v, index.v = tmp$ + 1 | 0, tmp$), this.keySerializer, k);
      composite.encodeSerializableElement_blecud$(this.descriptor, (tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), this.valueSerializer, v);
    }
    composite.endStructure_qatsm0$(this.descriptor);
  };
  MapLikeSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapLikeSerializer',
    interfaces: [AbstractCollectionSerializer]
  };
  function PrimitiveArrayBuilder() {
  }
  PrimitiveArrayBuilder.prototype.ensureCapacity_za3lpa$ = function (requiredCapacity, callback$default) {
    if (requiredCapacity === void 0)
      requiredCapacity = this.position + 1 | 0;
    callback$default ? callback$default(requiredCapacity) : this.ensureCapacity_za3lpa$$default(requiredCapacity);
  };
  PrimitiveArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveArrayBuilder',
    interfaces: []
  };
  function PrimitiveArraySerializer(primitiveSerializer) {
    ListLikeSerializer.call(this, primitiveSerializer);
    this.descriptor_o3qkn1$_0 = new PrimitiveArrayDescriptor(primitiveSerializer.descriptor);
  }
  Object.defineProperty(PrimitiveArraySerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_o3qkn1$_0;
    }
  });
  PrimitiveArraySerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.position;
  };
  PrimitiveArraySerializer.prototype.toResult_wili$ = function ($receiver) {
    return $receiver.build();
  };
  PrimitiveArraySerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
    $receiver.ensureCapacity_za3lpa$(size);
  };
  PrimitiveArraySerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    throw IllegalStateException_init('This method lead to boxing and must not be used, use writeContents instead'.toString());
  };
  PrimitiveArraySerializer.prototype.insert_p422l$ = function ($receiver, index, element) {
    throw IllegalStateException_init('This method lead to boxing and must not be used, use Builder.append instead'.toString());
  };
  PrimitiveArraySerializer.prototype.builder = function () {
    throw IllegalStateException_init('Use empty().toBuilder() instead'.toString());
  };
  PrimitiveArraySerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var size = this.collectionSize_wikn$(value);
    var encoder_0 = encoder.beginCollection_gly1x5$(this.descriptor, size, this.typeParams.slice());
    this.writeContent_2t417s$(encoder_0, value, size);
    encoder_0.endStructure_qatsm0$(this.descriptor);
  };
  PrimitiveArraySerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return this.patch_mynpiu$(decoder, this.empty());
  };
  PrimitiveArraySerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveArraySerializer',
    interfaces: [ListLikeSerializer]
  };
  function ReferenceArraySerializer(kClass, eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.kClass_0 = kClass;
    this.descriptor_8482jr$_0 = new ArrayClassDesc(eSerializer.descriptor);
  }
  Object.defineProperty(ReferenceArraySerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_8482jr$_0;
    }
  });
  ReferenceArraySerializer.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  ReferenceArraySerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    return Kotlin.arrayIterator($receiver);
  };
  ReferenceArraySerializer.prototype.builder = function () {
    return ArrayList_init_0();
  };
  ReferenceArraySerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  ReferenceArraySerializer.prototype.toResult_wili$ = function ($receiver) {
    return toNativeArrayImpl($receiver, this.kClass_0);
  };
  ReferenceArraySerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    return ArrayList_init_1(asList($receiver));
  };
  ReferenceArraySerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
    $receiver.ensureCapacity_za3lpa$(size);
  };
  ReferenceArraySerializer.prototype.insert_p422l$ = function ($receiver, index, element) {
    $receiver.add_wxm5ur$(index, element);
  };
  ReferenceArraySerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ReferenceArraySerializer',
    interfaces: [ListLikeSerializer]
  };
  function ArrayListSerializer(element) {
    ListLikeSerializer.call(this, element);
    this.descriptor_7uwoa2$_0 = new ArrayListClassDesc(element.descriptor);
  }
  Object.defineProperty(ArrayListSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_7uwoa2$_0;
    }
  });
  ArrayListSerializer.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  ArrayListSerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    return $receiver.iterator();
  };
  ArrayListSerializer.prototype.builder = function () {
    return ArrayList_init_0();
  };
  ArrayListSerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  ArrayListSerializer.prototype.toResult_wili$ = function ($receiver) {
    return $receiver;
  };
  ArrayListSerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, ArrayList) ? tmp$ : null) != null ? tmp$_0 : ArrayList_init_1($receiver);
  };
  ArrayListSerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
    $receiver.ensureCapacity_za3lpa$(size);
  };
  ArrayListSerializer.prototype.insert_p422l$ = function ($receiver, index, element) {
    $receiver.add_wxm5ur$(index, element);
  };
  ArrayListSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ArrayListSerializer',
    interfaces: [ListLikeSerializer]
  };
  function LinkedHashSetSerializer(eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.descriptor_vnfc7m$_0 = new LinkedHashSetClassDesc(eSerializer.descriptor);
  }
  Object.defineProperty(LinkedHashSetSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_vnfc7m$_0;
    }
  });
  LinkedHashSetSerializer.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  LinkedHashSetSerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    return $receiver.iterator();
  };
  LinkedHashSetSerializer.prototype.builder = function () {
    return LinkedHashSet_init_0();
  };
  LinkedHashSetSerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  LinkedHashSetSerializer.prototype.toResult_wili$ = function ($receiver) {
    return $receiver;
  };
  LinkedHashSetSerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, LinkedHashSet) ? tmp$ : null) != null ? tmp$_0 : LinkedHashSet_init($receiver);
  };
  LinkedHashSetSerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
  };
  LinkedHashSetSerializer.prototype.insert_p422l$ = function ($receiver, index, element) {
    $receiver.add_11rb$(element);
  };
  LinkedHashSetSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedHashSetSerializer',
    interfaces: [ListLikeSerializer]
  };
  function HashSetSerializer(eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.descriptor_yqpz47$_0 = new HashSetClassDesc(eSerializer.descriptor);
  }
  Object.defineProperty(HashSetSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_yqpz47$_0;
    }
  });
  HashSetSerializer.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  HashSetSerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    return $receiver.iterator();
  };
  HashSetSerializer.prototype.builder = function () {
    return HashSet_init();
  };
  HashSetSerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  HashSetSerializer.prototype.toResult_wili$ = function ($receiver) {
    return $receiver;
  };
  HashSetSerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, HashSet) ? tmp$ : null) != null ? tmp$_0 : HashSet_init_0($receiver);
  };
  HashSetSerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
  };
  HashSetSerializer.prototype.insert_p422l$ = function ($receiver, index, element) {
    $receiver.add_11rb$(element);
  };
  HashSetSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HashSetSerializer',
    interfaces: [ListLikeSerializer]
  };
  function LinkedHashMapSerializer(kSerializer, vSerializer) {
    MapLikeSerializer.call(this, kSerializer, vSerializer);
    this.descriptor_pixp0o$_0 = new LinkedHashMapClassDesc(kSerializer.descriptor, vSerializer.descriptor);
  }
  Object.defineProperty(LinkedHashMapSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_pixp0o$_0;
    }
  });
  LinkedHashMapSerializer.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  LinkedHashMapSerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    return $receiver.entries.iterator();
  };
  LinkedHashMapSerializer.prototype.builder = function () {
    return LinkedHashMap_init();
  };
  LinkedHashMapSerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  LinkedHashMapSerializer.prototype.toResult_wili$ = function ($receiver) {
    return $receiver;
  };
  LinkedHashMapSerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, LinkedHashMap) ? tmp$ : null) != null ? tmp$_0 : LinkedHashMap_init_1($receiver);
  };
  LinkedHashMapSerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
  };
  LinkedHashMapSerializer.prototype.insertKeyValuePair_fbr58l$ = function ($receiver, index, key, value) {
    $receiver.put_xwzc9p$(key, value);
  };
  LinkedHashMapSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedHashMapSerializer',
    interfaces: [MapLikeSerializer]
  };
  function HashMapSerializer(kSerializer, vSerializer) {
    MapLikeSerializer.call(this, kSerializer, vSerializer);
    this.descriptor_kvyydd$_0 = new HashMapClassDesc(kSerializer.descriptor, vSerializer.descriptor);
  }
  Object.defineProperty(HashMapSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_kvyydd$_0;
    }
  });
  HashMapSerializer.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  HashMapSerializer.prototype.collectionIterator_wikn$ = function ($receiver) {
    return $receiver.entries.iterator();
  };
  HashMapSerializer.prototype.builder = function () {
    return HashMap_init();
  };
  HashMapSerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  HashMapSerializer.prototype.toResult_wili$ = function ($receiver) {
    return $receiver;
  };
  HashMapSerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, HashMap) ? tmp$ : null) != null ? tmp$_0 : HashMap_init_0($receiver);
  };
  HashMapSerializer.prototype.checkCapacity_rk7bw8$ = function ($receiver, size) {
  };
  HashMapSerializer.prototype.insertKeyValuePair_fbr58l$ = function ($receiver, index, key, value) {
    $receiver.put_xwzc9p$(key, value);
  };
  HashMapSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HashMapSerializer',
    interfaces: [MapLikeSerializer]
  };
  function EnumDescriptor(name, elementsCount) {
    SerialClassDescImpl.call(this, name, void 0, elementsCount);
    this.kind_8antlo$_0 = UnionKind$ENUM_KIND_getInstance();
    this.elementDescriptors_r8dl0w$_0 = lazy(EnumDescriptor$elementDescriptors$lambda(elementsCount, name, this));
  }
  Object.defineProperty(EnumDescriptor.prototype, 'kind', {
    get: function () {
      return this.kind_8antlo$_0;
    }
  });
  Object.defineProperty(EnumDescriptor.prototype, 'elementDescriptors_0', {
    get: function () {
      return this.elementDescriptors_r8dl0w$_0.value;
    }
  });
  EnumDescriptor.prototype.getElementDescriptor_za3lpa$ = function (index) {
    return getChecked(this.elementDescriptors_0, index);
  };
  EnumDescriptor.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (other == null)
      return false;
    if (!Kotlin.isType(other, SerialDescriptor))
      return false;
    if (other.kind !== UnionKind$ENUM_KIND_getInstance())
      return false;
    if (!equals(this.serialName, other.serialName))
      return false;
    if (!equals(elementNames(this), elementNames(other)))
      return false;
    return true;
  };
  EnumDescriptor.prototype.toString = function () {
    return joinToString(elementNames(this), ', ', this.serialName + '(', ')');
  };
  EnumDescriptor.prototype.hashCode = function () {
    var result = hashCode(this.serialName);
    result = (31 * result | 0) + hashCode(elementNames(this)) | 0;
    return result;
  };
  function EnumDescriptor$elementDescriptors$lambda(closure$elementsCount, closure$name, this$EnumDescriptor) {
    return function () {
      var size = closure$elementsCount;
      var array = Array_0(size);
      var tmp$;
      tmp$ = array.length - 1 | 0;
      for (var i = 0; i <= tmp$; i++) {
        array[i] = SerialDescriptor_0(closure$name + '.' + this$EnumDescriptor.getElementName_za3lpa$(i), StructureKind$OBJECT_getInstance());
      }
      return array;
    };
  }
  EnumDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EnumDescriptor',
    interfaces: [SerialClassDescImpl]
  };
  function EnumSerializer(serialName, values) {
    this.values_0 = values;
    this.descriptor_ulj9cc$_0 = SerialDescriptor_0(serialName, UnionKind$ENUM_KIND_getInstance(), EnumSerializer$descriptor$lambda(this, serialName));
  }
  Object.defineProperty(EnumSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_ulj9cc$_0;
    }
  });
  EnumSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var index = indexOf(this.values_0, value);
    if (!(index !== -1)) {
      var message = value.toString() + ' is not a valid enum ' + this.descriptor.serialName + ', must be one of ' + contentToString(this.values_0);
      throw IllegalStateException_init(message.toString());
    }encoder.encodeEnum_3zr2iy$(this.descriptor, index);
  };
  EnumSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index = decoder.decodeEnum_qatsm0$(this.descriptor);
    if (!get_indices(this.values_0).contains_mef7kx$(index)) {
      var message = index.toString() + ' is not among valid ' + '$' + this.descriptor.serialName + ' enum values, values size is ' + this.values_0.length;
      throw IllegalStateException_init(message.toString());
    }return this.values_0[index];
  };
  function EnumSerializer$descriptor$lambda(this$EnumSerializer, closure$serialName) {
    return function ($receiver) {
      var $receiver_0 = this$EnumSerializer.values_0;
      var tmp$;
      for (tmp$ = 0; tmp$ !== $receiver_0.length; ++tmp$) {
        var element = $receiver_0[tmp$];
        var fqn = closure$serialName + '.' + element.name;
        var enumMemberDescriptor = SerialDescriptor_0(fqn, StructureKind$OBJECT_getInstance());
        $receiver.element_re18qg$(element.name, enumMemberDescriptor);
      }
      return Unit;
    };
  }
  EnumSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EnumSerializer',
    interfaces: [KSerializer]
  };
  function LongAsStringSerializer() {
    LongAsStringSerializer_instance = this;
    this.descriptor_9ax8ui$_0 = PrimitiveDescriptor('kotlinx.serialization.LongAsStringSerializer', PrimitiveKind$STRING_getInstance());
  }
  Object.defineProperty(LongAsStringSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_9ax8ui$_0;
    }
  });
  LongAsStringSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeString_61zpoe$(value.toString());
  };
  LongAsStringSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return toLong(decoder.decodeString());
  };
  LongAsStringSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LongAsStringSerializer',
    interfaces: [KSerializer]
  };
  var LongAsStringSerializer_instance = null;
  function LongAsStringSerializer_getInstance() {
    if (LongAsStringSerializer_instance === null) {
      new LongAsStringSerializer();
    }return LongAsStringSerializer_instance;
  }
  function makeNullable(actualSerializer) {
    return new NullableSerializer(actualSerializer);
  }
  function NullableSerializer(serializer) {
    this.serializer_0 = serializer;
    this.descriptor_kbvl2k$_0 = new SerialDescriptorForNullable(this.serializer_0.descriptor);
  }
  Object.defineProperty(NullableSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_kbvl2k$_0;
    }
  });
  NullableSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    if (value != null) {
      encoder.encodeNotNullMark();
      encoder.encodeSerializableValue_tf03ej$(this.serializer_0, value);
    } else {
      encoder.encodeNull();
    }
  };
  NullableSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeNotNullMark() ? decoder.decodeSerializableValue_w63s0f$(this.serializer_0) : decoder.decodeNull();
  };
  NullableSerializer.prototype.patch_mynpiu$ = function (decoder, old) {
    var tmp$;
    if (old == null)
      tmp$ = this.deserialize_nts5qn$(decoder);
    else if (decoder.decodeNotNullMark())
      tmp$ = decoder.updateSerializableValue_19c8k5$(this.serializer_0, old);
    else {
      decoder.decodeNull();
      tmp$ = old;
    }
    return tmp$;
  };
  NullableSerializer.prototype.equals = function (other) {
    var tmp$, tmp$_0;
    if (this === other)
      return true;
    if (other == null || !((tmp$ = Kotlin.getKClassFromExpression(this)) != null ? tmp$.equals(Kotlin.getKClassFromExpression(other)) : null))
      return false;
    Kotlin.isType(tmp$_0 = other, NullableSerializer) ? tmp$_0 : throwCCE();
    if (!equals(this.serializer_0, other.serializer_0))
      return false;
    return true;
  };
  NullableSerializer.prototype.hashCode = function () {
    return hashCode(this.serializer_0);
  };
  NullableSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NullableSerializer',
    interfaces: [KSerializer]
  };
  function SerialDescriptorForNullable(original) {
    this.original_0 = original;
    this.serialName_szvoqg$_0 = this.original_0.serialName + '?';
  }
  Object.defineProperty(SerialDescriptorForNullable.prototype, 'serialName', {
    get: function () {
      return this.serialName_szvoqg$_0;
    }
  });
  Object.defineProperty(SerialDescriptorForNullable.prototype, 'isNullable', {
    get: function () {
      return true;
    }
  });
  SerialDescriptorForNullable.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, SerialDescriptorForNullable))
      return false;
    if (!equals(this.original_0, other.original_0))
      return false;
    return true;
  };
  SerialDescriptorForNullable.prototype.toString = function () {
    return this.original_0.toString() + '?';
  };
  SerialDescriptorForNullable.prototype.hashCode = function () {
    return hashCode(this.original_0) * 31 | 0;
  };
  Object.defineProperty(SerialDescriptorForNullable.prototype, 'annotations', {
    get: function () {
      return this.original_0.annotations;
    }
  });
  Object.defineProperty(SerialDescriptorForNullable.prototype, 'elementsCount', {
    get: function () {
      return this.original_0.elementsCount;
    }
  });
  Object.defineProperty(SerialDescriptorForNullable.prototype, 'kind', {
    get: function () {
      return this.original_0.kind;
    }
  });
  Object.defineProperty(SerialDescriptorForNullable.prototype, 'name', {
    get: function () {
      return this.original_0.name;
    }
  });
  SerialDescriptorForNullable.prototype.getElementAnnotations_za3lpa$ = function (index) {
    return this.original_0.getElementAnnotations_za3lpa$(index);
  };
  SerialDescriptorForNullable.prototype.getElementDescriptor_za3lpa$ = function (index) {
    return this.original_0.getElementDescriptor_za3lpa$(index);
  };
  SerialDescriptorForNullable.prototype.getElementIndex_61zpoe$ = function (name) {
    return this.original_0.getElementIndex_61zpoe$(name);
  };
  SerialDescriptorForNullable.prototype.getElementName_za3lpa$ = function (index) {
    return this.original_0.getElementName_za3lpa$(index);
  };
  SerialDescriptorForNullable.prototype.getEntityAnnotations = function () {
    return this.original_0.getEntityAnnotations();
  };
  SerialDescriptorForNullable.prototype.isElementOptional_za3lpa$ = function (index) {
    return this.original_0.isElementOptional_za3lpa$(index);
  };
  SerialDescriptorForNullable.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialDescriptorForNullable',
    interfaces: [SerialDescriptor]
  };
  function ObjectSerializer(serialName, objectInstance) {
    this.objectInstance_0 = objectInstance;
    this.descriptor_uhy216$_0 = SerialDescriptor_0(serialName, StructureKind$OBJECT_getInstance());
  }
  Object.defineProperty(ObjectSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_uhy216$_0;
    }
  });
  ObjectSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.beginStructure_r0sa6z$(this.descriptor, []).endStructure_qatsm0$(this.descriptor);
  };
  ObjectSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    decoder.beginStructure_r0sa6z$(this.descriptor, []).endStructure_qatsm0$(this.descriptor);
    return this.objectInstance_0;
  };
  ObjectSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ObjectSerializer',
    interfaces: [KSerializer]
  };
  function PluginGeneratedSerialDescriptor(serialName, generatedSerializer, elementsCount) {
    if (generatedSerializer === void 0)
      generatedSerializer = null;
    this.serialName_igazkg$_0 = serialName;
    this.generatedSerializer_5mvp8k$_0 = generatedSerializer;
    this.elementsCount_qx3iur$_0 = elementsCount;
    this.added_cw3e1n$_0 = -1;
    var array = Array_0(this.elementsCount);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = '[UNINITIALIZED]';
    }
    this.names_6rzxgj$_0 = array;
    this.propertiesAnnotations_a737os$_0 = Kotlin.newArray(this.elementsCount, null);
    this.classAnnotations_8upbj1$_0 = null;
    this.flags_aeisg2$_0 = Kotlin.booleanArray(this.elementsCount);
    this.indices_73aj4y$_cx9qar$_0 = lazy(PluginGeneratedSerialDescriptor$indices$lambda(this));
  }
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'serialName', {
    get: function () {
      return this.serialName_igazkg$_0;
    }
  });
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'elementsCount', {
    get: function () {
      return this.elementsCount_qx3iur$_0;
    }
  });
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'kind', {
    get: function () {
      return StructureKind$CLASS_getInstance();
    }
  });
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'annotations', {
    get: function () {
      var tmp$;
      return (tmp$ = this.classAnnotations_8upbj1$_0) != null ? tmp$ : emptyList();
    }
  });
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'namesSet_8be2vx$', {
    get: function () {
      return this.indices_73aj4y$_0.keys;
    }
  });
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'indices_73aj4y$_0', {
    get: function () {
      return this.indices_73aj4y$_cx9qar$_0.value;
    }
  });
  PluginGeneratedSerialDescriptor.prototype.addElement_ivxn3r$ = function (name, isOptional) {
    if (isOptional === void 0)
      isOptional = false;
    this.names_6rzxgj$_0[this.added_cw3e1n$_0 = this.added_cw3e1n$_0 + 1 | 0, this.added_cw3e1n$_0] = name;
    this.flags_aeisg2$_0[this.added_cw3e1n$_0] = isOptional;
    this.propertiesAnnotations_a737os$_0[this.added_cw3e1n$_0] = null;
  };
  PluginGeneratedSerialDescriptor.prototype.pushAnnotation_yj921w$ = function (annotation) {
    var it = this.propertiesAnnotations_a737os$_0[this.added_cw3e1n$_0];
    var block$result;
    if (it == null) {
      var result = ArrayList_init(1);
      this.propertiesAnnotations_a737os$_0[this.added_cw3e1n$_0] = result;
      block$result = result;
    } else {
      block$result = it;
    }
    var list = block$result;
    list.add_11rb$(annotation);
  };
  PluginGeneratedSerialDescriptor.prototype.pushClassAnnotation_yj921w$ = function (a) {
    if (this.classAnnotations_8upbj1$_0 == null) {
      this.classAnnotations_8upbj1$_0 = ArrayList_init(1);
    }ensureNotNull(this.classAnnotations_8upbj1$_0).add_11rb$(a);
  };
  PluginGeneratedSerialDescriptor.prototype.getElementDescriptor_za3lpa$ = function (index) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$_2 = (tmp$_1 = (tmp$_0 = (tmp$ = this.generatedSerializer_5mvp8k$_0) != null ? tmp$.childSerializers() : null) != null ? tmp$_0[index] : null) != null ? tmp$_1.descriptor : null;
    if (tmp$_2 == null) {
      throw new IndexOutOfBoundsException(this.serialName + ' descriptor has only ' + this.elementsCount + ' elements, index: ' + index);
    }return tmp$_2;
  };
  PluginGeneratedSerialDescriptor.prototype.isElementOptional_za3lpa$ = function (index) {
    return getChecked_0(this.flags_aeisg2$_0, index);
  };
  PluginGeneratedSerialDescriptor.prototype.getElementAnnotations_za3lpa$ = function (index) {
    var tmp$;
    return (tmp$ = getChecked(this.propertiesAnnotations_a737os$_0, index)) != null ? tmp$ : emptyList();
  };
  PluginGeneratedSerialDescriptor.prototype.getElementName_za3lpa$ = function (index) {
    return getChecked(this.names_6rzxgj$_0, index);
  };
  PluginGeneratedSerialDescriptor.prototype.getElementIndex_61zpoe$ = function (name) {
    var tmp$;
    return (tmp$ = this.indices_73aj4y$_0.get_11rb$(name)) != null ? tmp$ : -3;
  };
  PluginGeneratedSerialDescriptor.prototype.buildIndices_fidiyy$_0 = function () {
    var tmp$;
    var indices = HashMap_init();
    tmp$ = this.names_6rzxgj$_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      var key = this.names_6rzxgj$_0[i];
      indices.put_xwzc9p$(key, i);
    }
    return indices;
  };
  PluginGeneratedSerialDescriptor.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, SerialDescriptor))
      return false;
    if (!equals(this.serialName, other.serialName))
      return false;
    if (!equals(elementDescriptors(this), elementDescriptors(other)))
      return false;
    return true;
  };
  PluginGeneratedSerialDescriptor.prototype.hashCode = function () {
    var result = hashCode(this.serialName);
    result = (31 * result | 0) + hashCode(elementDescriptors(this)) | 0;
    return result;
  };
  function PluginGeneratedSerialDescriptor$toString$lambda(this$PluginGeneratedSerialDescriptor) {
    return function (it) {
      return it.key + ': ' + this$PluginGeneratedSerialDescriptor.getElementDescriptor_za3lpa$(it.value).serialName;
    };
  }
  PluginGeneratedSerialDescriptor.prototype.toString = function () {
    return joinToString(this.indices_73aj4y$_0.entries, ', ', this.serialName + '(', ')', void 0, void 0, PluginGeneratedSerialDescriptor$toString$lambda(this));
  };
  function PluginGeneratedSerialDescriptor$indices$lambda(this$PluginGeneratedSerialDescriptor) {
    return function () {
      return this$PluginGeneratedSerialDescriptor.buildIndices_fidiyy$_0();
    };
  }
  PluginGeneratedSerialDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PluginGeneratedSerialDescriptor',
    interfaces: [SerialDescriptor]
  };
  function GeneratedSerializer() {
  }
  GeneratedSerializer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'GeneratedSerializer',
    interfaces: [KSerializer]
  };
  function SerializerFactory() {
  }
  SerializerFactory.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SerializerFactory',
    interfaces: []
  };
  var INITIAL_SIZE;
  function ByteArraySerializer_0() {
    ByteArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_3(kotlin_js_internal_ByteCompanionObject));
  }
  ByteArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  ByteArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new ByteArrayBuilder($receiver);
  };
  ByteArraySerializer_0.prototype.empty = function () {
    return new Int8Array(0);
  };
  ByteArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_8e99oo$(decoder.decodeByteElement_3zr2iy$(this.descriptor, index));
  };
  ByteArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeByteElement_a3tadb$(this.descriptor, i, content[i]);
  };
  ByteArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ByteArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var ByteArraySerializer_instance = null;
  function ByteArraySerializer_getInstance() {
    if (ByteArraySerializer_instance === null) {
      new ByteArraySerializer_0();
    }return ByteArraySerializer_instance;
  }
  function ByteArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_8vrcnd$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(ByteArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_8vrcnd$_0;
    },
    set: function (position) {
      this.position_8vrcnd$_0 = position;
    }
  });
  ByteArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  ByteArrayBuilder.prototype.append_8e99oo$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  ByteArrayBuilder.prototype.build = function () {
    return copyOf(this.buffer_0, this.position);
  };
  ByteArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function ShortArraySerializer_0() {
    ShortArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_4(kotlin_js_internal_ShortCompanionObject));
  }
  ShortArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  ShortArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new ShortArrayBuilder($receiver);
  };
  ShortArraySerializer_0.prototype.empty = function () {
    return new Int16Array(0);
  };
  ShortArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_o3ifnw$(decoder.decodeShortElement_3zr2iy$(this.descriptor, index));
  };
  ShortArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeShortElement_tet9k5$(this.descriptor, i, content[i]);
  };
  ShortArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ShortArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var ShortArraySerializer_instance = null;
  function ShortArraySerializer_getInstance() {
    if (ShortArraySerializer_instance === null) {
      new ShortArraySerializer_0();
    }return ShortArraySerializer_instance;
  }
  function ShortArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_aswgsb$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(ShortArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_aswgsb$_0;
    },
    set: function (position) {
      this.position_aswgsb$_0 = position;
    }
  });
  ShortArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_0(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  ShortArrayBuilder.prototype.append_o3ifnw$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  ShortArrayBuilder.prototype.build = function () {
    return copyOf_0(this.buffer_0, this.position);
  };
  ShortArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShortArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function IntArraySerializer_0() {
    IntArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_5(kotlin_js_internal_IntCompanionObject));
  }
  IntArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  IntArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new IntArrayBuilder($receiver);
  };
  IntArraySerializer_0.prototype.empty = function () {
    return new Int32Array(0);
  };
  IntArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_kcn2v3$(decoder.decodeIntElement_3zr2iy$(this.descriptor, index));
  };
  IntArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeIntElement_4wpqag$(this.descriptor, i, content[i]);
  };
  IntArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'IntArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var IntArraySerializer_instance = null;
  function IntArraySerializer_getInstance() {
    if (IntArraySerializer_instance === null) {
      new IntArraySerializer_0();
    }return IntArraySerializer_instance;
  }
  function IntArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_9owhjc$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(IntArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_9owhjc$_0;
    },
    set: function (position) {
      this.position_9owhjc$_0 = position;
    }
  });
  IntArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_1(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  IntArrayBuilder.prototype.append_kcn2v3$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  IntArrayBuilder.prototype.build = function () {
    return copyOf_1(this.buffer_0, this.position);
  };
  IntArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IntArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function LongArraySerializer_0() {
    LongArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_6(kotlin_js_internal_LongCompanionObject));
  }
  LongArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  LongArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new LongArrayBuilder($receiver);
  };
  LongArraySerializer_0.prototype.empty = function () {
    return Kotlin.longArray(0);
  };
  LongArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_8e33dg$(decoder.decodeLongElement_3zr2iy$(this.descriptor, index));
  };
  LongArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeLongElement_a3zgoj$(this.descriptor, i, content[i]);
  };
  LongArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LongArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var LongArraySerializer_instance = null;
  function LongArraySerializer_getInstance() {
    if (LongArraySerializer_instance === null) {
      new LongArraySerializer_0();
    }return LongArraySerializer_instance;
  }
  function LongArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_kthxoj$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(LongArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_kthxoj$_0;
    },
    set: function (position) {
      this.position_kthxoj$_0 = position;
    }
  });
  LongArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_2(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  LongArrayBuilder.prototype.append_8e33dg$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  LongArrayBuilder.prototype.build = function () {
    return copyOf_2(this.buffer_0, this.position);
  };
  LongArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LongArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function FloatArraySerializer_0() {
    FloatArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_7(kotlin_js_internal_FloatCompanionObject));
  }
  FloatArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  FloatArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new FloatArrayBuilder($receiver);
  };
  FloatArraySerializer_0.prototype.empty = function () {
    return new Float32Array(0);
  };
  FloatArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_nwfnho$(decoder.decodeFloatElement_3zr2iy$(this.descriptor, index));
  };
  FloatArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeFloatElement_t7qhdx$(this.descriptor, i, content[i]);
  };
  FloatArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'FloatArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var FloatArraySerializer_instance = null;
  function FloatArraySerializer_getInstance() {
    if (FloatArraySerializer_instance === null) {
      new FloatArraySerializer_0();
    }return FloatArraySerializer_instance;
  }
  function FloatArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_gfqw9x$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(FloatArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_gfqw9x$_0;
    },
    set: function (position) {
      this.position_gfqw9x$_0 = position;
    }
  });
  FloatArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_3(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  FloatArrayBuilder.prototype.append_nwfnho$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  FloatArrayBuilder.prototype.build = function () {
    return copyOf_3(this.buffer_0, this.position);
  };
  FloatArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FloatArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function DoubleArraySerializer_0() {
    DoubleArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_8(kotlin_js_internal_DoubleCompanionObject));
  }
  DoubleArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  DoubleArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new DoubleArrayBuilder($receiver);
  };
  DoubleArraySerializer_0.prototype.empty = function () {
    return new Float64Array(0);
  };
  DoubleArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_tq0o01$(decoder.decodeDoubleElement_3zr2iy$(this.descriptor, index));
  };
  DoubleArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeDoubleElement_imzr5k$(this.descriptor, i, content[i]);
  };
  DoubleArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DoubleArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var DoubleArraySerializer_instance = null;
  function DoubleArraySerializer_getInstance() {
    if (DoubleArraySerializer_instance === null) {
      new DoubleArraySerializer_0();
    }return DoubleArraySerializer_instance;
  }
  function DoubleArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_qka0uq$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(DoubleArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_qka0uq$_0;
    },
    set: function (position) {
      this.position_qka0uq$_0 = position;
    }
  });
  DoubleArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_4(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  DoubleArrayBuilder.prototype.append_tq0o01$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  DoubleArrayBuilder.prototype.build = function () {
    return copyOf_4(this.buffer_0, this.position);
  };
  DoubleArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DoubleArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function CharArraySerializer_0() {
    CharArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_2(kotlin_js_internal_CharCompanionObject));
  }
  CharArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  CharArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new CharArrayBuilder($receiver);
  };
  CharArraySerializer_0.prototype.empty = function () {
    return Kotlin.charArray(0);
  };
  CharArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_8e8zqy$(unboxChar(decoder.decodeCharElement_3zr2iy$(this.descriptor, index)));
  };
  CharArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeCharElement_a3tkb1$(this.descriptor, i, content[i]);
  };
  CharArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CharArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var CharArraySerializer_instance = null;
  function CharArraySerializer_getInstance() {
    if (CharArraySerializer_instance === null) {
      new CharArraySerializer_0();
    }return CharArraySerializer_instance;
  }
  function CharArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_tpcwbb$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(CharArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_tpcwbb$_0;
    },
    set: function (position) {
      this.position_tpcwbb$_0 = position;
    }
  });
  CharArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_5(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  CharArrayBuilder.prototype.append_8e8zqy$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  CharArrayBuilder.prototype.build = function () {
    return copyOf_5(this.buffer_0, this.position);
  };
  CharArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CharArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  function BooleanArraySerializer_0() {
    BooleanArraySerializer_instance = this;
    PrimitiveArraySerializer.call(this, serializer_9(kotlin_js_internal_BooleanCompanionObject));
  }
  BooleanArraySerializer_0.prototype.collectionSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  BooleanArraySerializer_0.prototype.toBuilder_wikn$ = function ($receiver) {
    return new BooleanArrayBuilder($receiver);
  };
  BooleanArraySerializer_0.prototype.empty = function () {
    return Kotlin.booleanArray(0);
  };
  BooleanArraySerializer_0.prototype.readElement_ind1ny$$default = function (decoder, index, builder, checkIndex) {
    builder.append_vft4zs$(decoder.decodeBooleanElement_3zr2iy$(this.descriptor, index));
  };
  BooleanArraySerializer_0.prototype.writeContent_2t417s$ = function (encoder, content, size) {
    for (var i = 0; i < size; i++)
      encoder.encodeBooleanElement_w1b0nl$(this.descriptor, i, content[i]);
  };
  BooleanArraySerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'BooleanArraySerializer',
    interfaces: [PrimitiveArraySerializer, KSerializer]
  };
  var BooleanArraySerializer_instance = null;
  function BooleanArraySerializer_getInstance() {
    if (BooleanArraySerializer_instance === null) {
      new BooleanArraySerializer_0();
    }return BooleanArraySerializer_instance;
  }
  function BooleanArrayBuilder(bufferWithData) {
    PrimitiveArrayBuilder.call(this);
    this.buffer_0 = bufferWithData;
    this.position_fkn8lr$_0 = bufferWithData.length;
    this.ensureCapacity_za3lpa$(10);
  }
  Object.defineProperty(BooleanArrayBuilder.prototype, 'position', {
    get: function () {
      return this.position_fkn8lr$_0;
    },
    set: function (position) {
      this.position_fkn8lr$_0 = position;
    }
  });
  BooleanArrayBuilder.prototype.ensureCapacity_za3lpa$$default = function (requiredCapacity) {
    if (this.buffer_0.length < requiredCapacity)
      this.buffer_0 = copyOf_6(this.buffer_0, coerceAtLeast(requiredCapacity, this.buffer_0.length * 2 | 0));
  };
  BooleanArrayBuilder.prototype.append_vft4zs$ = function (c) {
    var tmp$;
    this.ensureCapacity_za3lpa$();
    this.buffer_0[tmp$ = this.position, this.position = tmp$ + 1 | 0, tmp$] = c;
  };
  BooleanArrayBuilder.prototype.build = function () {
    return copyOf_6(this.buffer_0, this.position);
  };
  BooleanArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BooleanArrayBuilder',
    interfaces: [PrimitiveArrayBuilder]
  };
  var BUILTIN_SERIALIZERS;
  function PrimitiveSerialDescriptor(serialName, kind) {
    this.serialName_h9gugr$_0 = serialName;
    this.kind_rqp61y$_0 = kind;
  }
  Object.defineProperty(PrimitiveSerialDescriptor.prototype, 'serialName', {
    get: function () {
      return this.serialName_h9gugr$_0;
    }
  });
  Object.defineProperty(PrimitiveSerialDescriptor.prototype, 'kind', {
    get: function () {
      return this.kind_rqp61y$_0;
    }
  });
  Object.defineProperty(PrimitiveSerialDescriptor.prototype, 'elementsCount', {
    get: function () {
      return 0;
    }
  });
  PrimitiveSerialDescriptor.prototype.getElementName_za3lpa$ = function (index) {
    return this.error_0();
  };
  PrimitiveSerialDescriptor.prototype.getElementIndex_61zpoe$ = function (name) {
    return this.error_0();
  };
  PrimitiveSerialDescriptor.prototype.isElementOptional_za3lpa$ = function (index) {
    return this.error_0();
  };
  PrimitiveSerialDescriptor.prototype.getElementDescriptor_za3lpa$ = function (index) {
    return this.error_0();
  };
  PrimitiveSerialDescriptor.prototype.getElementAnnotations_za3lpa$ = function (index) {
    return this.error_0();
  };
  PrimitiveSerialDescriptor.prototype.toString = function () {
    return 'PrimitiveDescriptor(' + this.serialName + ')';
  };
  PrimitiveSerialDescriptor.prototype.error_0 = function () {
    throw IllegalStateException_init('Primitive descriptor does not have elements');
  };
  PrimitiveSerialDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveSerialDescriptor',
    interfaces: [SerialDescriptor]
  };
  function PrimitiveDescriptorSafe(serialName, kind) {
    checkName(serialName);
    return new PrimitiveSerialDescriptor(serialName, kind);
  }
  function checkName(serialName) {
    var tmp$;
    var keys = BUILTIN_SERIALIZERS.keys;
    tmp$ = keys.iterator();
    while (tmp$.hasNext()) {
      var primitive = tmp$.next();
      var simpleName = capitalize(ensureNotNull(primitive.simpleName));
      var qualifiedName = 'kotlin.' + simpleName;
      if (equals_0(serialName, qualifiedName, true) || equals_0(serialName, simpleName, true)) {
        throw IllegalArgumentException_init(trimIndent('\n' + '                The name of serial descriptor should uniquely identify associated serializer.' + '\n' + '                For serial name ' + serialName + ' there already exist ' + capitalize(simpleName) + 'Serializer.' + '\n' + '                Please refer to SerialDescriptor documentation for additional information.' + '\n' + '            '));
      }}
  }
  function builtinSerializerOrNull($receiver) {
    var tmp$;
    return (tmp$ = BUILTIN_SERIALIZERS.get_11rb$($receiver)) == null || Kotlin.isType(tmp$, KSerializer) ? tmp$ : throwCCE();
  }
  function UnitSerializer_0() {
    UnitSerializer_instance = this;
    this.$delegate_t0wm8i$_0 = new ObjectSerializer('kotlin.Unit', Unit);
  }
  Object.defineProperty(UnitSerializer_0.prototype, 'descriptor', {
    get: function () {
      return this.$delegate_t0wm8i$_0.descriptor;
    }
  });
  UnitSerializer_0.prototype.deserialize_nts5qn$ = function (decoder) {
    return this.$delegate_t0wm8i$_0.deserialize_nts5qn$(decoder);
  };
  UnitSerializer_0.prototype.patch_mynpiu$ = function (decoder, old) {
    return this.$delegate_t0wm8i$_0.patch_mynpiu$(decoder, old);
  };
  UnitSerializer_0.prototype.serialize_awe97i$ = function (encoder, value) {
    return this.$delegate_t0wm8i$_0.serialize_awe97i$(encoder, value);
  };
  UnitSerializer_0.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'UnitSerializer',
    interfaces: [KSerializer]
  };
  var UnitSerializer_instance = null;
  function UnitSerializer_getInstance() {
    if (UnitSerializer_instance === null) {
      new UnitSerializer_0();
    }return UnitSerializer_instance;
  }
  function BooleanSerializer() {
    BooleanSerializer_instance = this;
    this.descriptor_vdtvaz$_0 = new PrimitiveSerialDescriptor('kotlin.Boolean', PrimitiveKind$BOOLEAN_getInstance());
  }
  Object.defineProperty(BooleanSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_vdtvaz$_0;
    }
  });
  BooleanSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeBoolean_6taknv$(value);
  };
  BooleanSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeBoolean();
  };
  BooleanSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'BooleanSerializer',
    interfaces: [KSerializer]
  };
  var BooleanSerializer_instance = null;
  function BooleanSerializer_getInstance() {
    if (BooleanSerializer_instance === null) {
      new BooleanSerializer();
    }return BooleanSerializer_instance;
  }
  function ByteSerializer() {
    ByteSerializer_instance = this;
    this.descriptor_f6vlf1$_0 = new PrimitiveSerialDescriptor('kotlin.Byte', PrimitiveKind$BYTE_getInstance());
  }
  Object.defineProperty(ByteSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_f6vlf1$_0;
    }
  });
  ByteSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeByte_s8j3t7$(value);
  };
  ByteSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeByte();
  };
  ByteSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ByteSerializer',
    interfaces: [KSerializer]
  };
  var ByteSerializer_instance = null;
  function ByteSerializer_getInstance() {
    if (ByteSerializer_instance === null) {
      new ByteSerializer();
    }return ByteSerializer_instance;
  }
  function ShortSerializer() {
    ShortSerializer_instance = this;
    this.descriptor_yvjeup$_0 = new PrimitiveSerialDescriptor('kotlin.Short', PrimitiveKind$SHORT_getInstance());
  }
  Object.defineProperty(ShortSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_yvjeup$_0;
    }
  });
  ShortSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeShort_mq22fl$(value);
  };
  ShortSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeShort();
  };
  ShortSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ShortSerializer',
    interfaces: [KSerializer]
  };
  var ShortSerializer_instance = null;
  function ShortSerializer_getInstance() {
    if (ShortSerializer_instance === null) {
      new ShortSerializer();
    }return ShortSerializer_instance;
  }
  function IntSerializer() {
    IntSerializer_instance = this;
    this.descriptor_xrjflq$_0 = new PrimitiveSerialDescriptor('kotlin.Int', PrimitiveKind$INT_getInstance());
  }
  Object.defineProperty(IntSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_xrjflq$_0;
    }
  });
  IntSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeInt_za3lpa$(value);
  };
  IntSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeInt();
  };
  IntSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'IntSerializer',
    interfaces: [KSerializer]
  };
  var IntSerializer_instance = null;
  function IntSerializer_getInstance() {
    if (IntSerializer_instance === null) {
      new IntSerializer();
    }return IntSerializer_instance;
  }
  function LongSerializer() {
    LongSerializer_instance = this;
    this.descriptor_q4z687$_0 = new PrimitiveSerialDescriptor('kotlin.Long', PrimitiveKind$LONG_getInstance());
  }
  Object.defineProperty(LongSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_q4z687$_0;
    }
  });
  LongSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeLong_s8cxhz$(value);
  };
  LongSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeLong();
  };
  LongSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LongSerializer',
    interfaces: [KSerializer]
  };
  var LongSerializer_instance = null;
  function LongSerializer_getInstance() {
    if (LongSerializer_instance === null) {
      new LongSerializer();
    }return LongSerializer_instance;
  }
  function FloatSerializer() {
    FloatSerializer_instance = this;
    this.descriptor_7mw1sh$_0 = new PrimitiveSerialDescriptor('kotlin.Float', PrimitiveKind$FLOAT_getInstance());
  }
  Object.defineProperty(FloatSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_7mw1sh$_0;
    }
  });
  FloatSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeFloat_mx4ult$(value);
  };
  FloatSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeFloat();
  };
  FloatSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'FloatSerializer',
    interfaces: [KSerializer]
  };
  var FloatSerializer_instance = null;
  function FloatSerializer_getInstance() {
    if (FloatSerializer_instance === null) {
      new FloatSerializer();
    }return FloatSerializer_instance;
  }
  function DoubleSerializer() {
    DoubleSerializer_instance = this;
    this.descriptor_2hn2sc$_0 = new PrimitiveSerialDescriptor('kotlin.Double', PrimitiveKind$DOUBLE_getInstance());
  }
  Object.defineProperty(DoubleSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_2hn2sc$_0;
    }
  });
  DoubleSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeDouble_14dthe$(value);
  };
  DoubleSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeDouble();
  };
  DoubleSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DoubleSerializer',
    interfaces: [KSerializer]
  };
  var DoubleSerializer_instance = null;
  function DoubleSerializer_getInstance() {
    if (DoubleSerializer_instance === null) {
      new DoubleSerializer();
    }return DoubleSerializer_instance;
  }
  function CharSerializer() {
    CharSerializer_instance = this;
    this.descriptor_5mpy8x$_0 = new PrimitiveSerialDescriptor('kotlin.Char', PrimitiveKind$CHAR_getInstance());
  }
  Object.defineProperty(CharSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_5mpy8x$_0;
    }
  });
  CharSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeChar_s8itvh$(value);
  };
  CharSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeChar();
  };
  CharSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CharSerializer',
    interfaces: [KSerializer]
  };
  var CharSerializer_instance = null;
  function CharSerializer_getInstance() {
    if (CharSerializer_instance === null) {
      new CharSerializer();
    }return CharSerializer_instance;
  }
  function StringSerializer() {
    StringSerializer_instance = this;
    this.descriptor_sum718$_0 = new PrimitiveSerialDescriptor('kotlin.String', PrimitiveKind$STRING_getInstance());
  }
  Object.defineProperty(StringSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_sum718$_0;
    }
  });
  StringSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    encoder.encodeString_61zpoe$(value);
  };
  StringSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return decoder.decodeString();
  };
  StringSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'StringSerializer',
    interfaces: [KSerializer]
  };
  var StringSerializer_instance = null;
  function StringSerializer_getInstance() {
    if (StringSerializer_instance === null) {
      new StringSerializer();
    }return StringSerializer_instance;
  }
  function Migration() {
  }
  Object.defineProperty(Migration.prototype, 'serialName', {
    get: function () {
      throw IllegalStateException_init('Class used only for source-level migration'.toString());
    }
  });
  Object.defineProperty(Migration.prototype, 'kind', {
    get: function () {
      throw IllegalStateException_init('Class used only for source-level migration'.toString());
    }
  });
  Object.defineProperty(Migration.prototype, 'elementsCount', {
    get: function () {
      throw IllegalStateException_init('Class used only for source-level migration'.toString());
    }
  });
  Migration.prototype.getElementName_za3lpa$ = function (index) {
    throw IllegalStateException_init('Class used only for source-level migration'.toString());
  };
  Migration.prototype.getElementIndex_61zpoe$ = function (name) {
    throw IllegalStateException_init('Class used only for source-level migration'.toString());
  };
  Migration.prototype.getElementAnnotations_za3lpa$ = function (index) {
    throw IllegalStateException_init('Class used only for source-level migration'.toString());
  };
  Migration.prototype.getElementDescriptor_za3lpa$ = function (index) {
    throw IllegalStateException_init('Class used only for source-level migration'.toString());
  };
  Migration.prototype.isElementOptional_za3lpa$ = function (index) {
    throw IllegalStateException_init('Class used only for source-level migration'.toString());
  };
  Migration.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Migration',
    interfaces: [SerialDescriptor]
  };
  var message_1;
  function IntDescriptor() {
    IntDescriptor_instance = this;
    Migration.call(this);
  }
  IntDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'IntDescriptor',
    interfaces: [Migration]
  };
  var IntDescriptor_instance = null;
  function IntDescriptor_getInstance() {
    if (IntDescriptor_instance === null) {
      new IntDescriptor();
    }return IntDescriptor_instance;
  }
  function UnitDescriptor() {
    UnitDescriptor_instance = this;
    Migration.call(this);
  }
  UnitDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'UnitDescriptor',
    interfaces: [Migration]
  };
  var UnitDescriptor_instance = null;
  function UnitDescriptor_getInstance() {
    if (UnitDescriptor_instance === null) {
      new UnitDescriptor();
    }return UnitDescriptor_instance;
  }
  function BooleanDescriptor() {
    BooleanDescriptor_instance = this;
    Migration.call(this);
  }
  BooleanDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'BooleanDescriptor',
    interfaces: [Migration]
  };
  var BooleanDescriptor_instance = null;
  function BooleanDescriptor_getInstance() {
    if (BooleanDescriptor_instance === null) {
      new BooleanDescriptor();
    }return BooleanDescriptor_instance;
  }
  function ByteDescriptor() {
    ByteDescriptor_instance = this;
    Migration.call(this);
  }
  ByteDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ByteDescriptor',
    interfaces: [Migration]
  };
  var ByteDescriptor_instance = null;
  function ByteDescriptor_getInstance() {
    if (ByteDescriptor_instance === null) {
      new ByteDescriptor();
    }return ByteDescriptor_instance;
  }
  function ShortDescriptor() {
    ShortDescriptor_instance = this;
    Migration.call(this);
  }
  ShortDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ShortDescriptor',
    interfaces: [Migration]
  };
  var ShortDescriptor_instance = null;
  function ShortDescriptor_getInstance() {
    if (ShortDescriptor_instance === null) {
      new ShortDescriptor();
    }return ShortDescriptor_instance;
  }
  function LongDescriptor() {
    LongDescriptor_instance = this;
    Migration.call(this);
  }
  LongDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LongDescriptor',
    interfaces: [Migration]
  };
  var LongDescriptor_instance = null;
  function LongDescriptor_getInstance() {
    if (LongDescriptor_instance === null) {
      new LongDescriptor();
    }return LongDescriptor_instance;
  }
  function FloatDescriptor() {
    FloatDescriptor_instance = this;
    Migration.call(this);
  }
  FloatDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'FloatDescriptor',
    interfaces: [Migration]
  };
  var FloatDescriptor_instance = null;
  function FloatDescriptor_getInstance() {
    if (FloatDescriptor_instance === null) {
      new FloatDescriptor();
    }return FloatDescriptor_instance;
  }
  function DoubleDescriptor() {
    DoubleDescriptor_instance = this;
    Migration.call(this);
  }
  DoubleDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DoubleDescriptor',
    interfaces: [Migration]
  };
  var DoubleDescriptor_instance = null;
  function DoubleDescriptor_getInstance() {
    if (DoubleDescriptor_instance === null) {
      new DoubleDescriptor();
    }return DoubleDescriptor_instance;
  }
  function CharDescriptor() {
    CharDescriptor_instance = this;
    Migration.call(this);
  }
  CharDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CharDescriptor',
    interfaces: [Migration]
  };
  var CharDescriptor_instance = null;
  function CharDescriptor_getInstance() {
    if (CharDescriptor_instance === null) {
      new CharDescriptor();
    }return CharDescriptor_instance;
  }
  function StringDescriptor() {
    StringDescriptor_instance = this;
    Migration.call(this);
  }
  StringDescriptor.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'StringDescriptor',
    interfaces: [Migration]
  };
  var StringDescriptor_instance = null;
  function StringDescriptor_getInstance() {
    if (StringDescriptor_instance === null) {
      new StringDescriptor();
    }return StringDescriptor_instance;
  }
  function SerialClassDescImpl(serialName, generatedSerializer, elementsCount) {
    if (generatedSerializer === void 0)
      generatedSerializer = null;
    PluginGeneratedSerialDescriptor.call(this, serialName, generatedSerializer, elementsCount);
  }
  SerialClassDescImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialClassDescImpl',
    interfaces: [PluginGeneratedSerialDescriptor]
  };
  function SerializationConstructorMarker() {
  }
  SerializationConstructorMarker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializationConstructorMarker',
    interfaces: []
  };
  function SerializationConstructorMarker_0() {
  }
  SerializationConstructorMarker_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializationConstructorMarker',
    interfaces: []
  };
  var unitDeprecated;
  function TaggedEncoder() {
    this.tagStack_cfsfm$_0 = ArrayList_init_0();
  }
  Object.defineProperty(TaggedEncoder.prototype, 'context', {
    get: function () {
      return EmptyModule_getInstance();
    }
  });
  TaggedEncoder.prototype.encodeTaggedValue_dpg7wc$ = function (tag, value) {
    throw new SerializationException('Non-serializable ' + Kotlin.getKClassFromExpression(value) + ' is not supported by ' + Kotlin.getKClassFromExpression(this) + ' encoder');
  };
  TaggedEncoder.prototype.encodeTaggedNotNullMark_11rb$ = function (tag) {
  };
  TaggedEncoder.prototype.encodeTaggedNull_11rb$ = function (tag) {
    throw new SerializationException('null is not supported');
  };
  TaggedEncoder.prototype.encodeTaggedUnit_11rb$ = function (tag) {
    this.encodeTaggedValue_dpg7wc$(tag, Unit);
  };
  TaggedEncoder.prototype.encodeTaggedInt_dpg1yx$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedByte_19qe40$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedShort_veccj0$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedLong_19wkf8$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedFloat_vlf4p8$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedDouble_e37ph5$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedBoolean_iuyhfk$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedChar_19qo1q$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, toBoxedChar(value));
  };
  TaggedEncoder.prototype.encodeTaggedString_l9l8mx$ = function (tag, value) {
    this.encodeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedEncoder.prototype.encodeTaggedEnum_v153v3$ = function (tag, enumDescription, ordinal) {
    this.encodeTaggedValue_dpg7wc$(tag, ordinal);
  };
  TaggedEncoder.prototype.encodeElement_3zr2iy$ = function (desc, index) {
    var tag = this.getTag_m47q6f$(desc, index);
    var shouldWriteElement = this.shouldWriteElement_a5qihn$(desc, tag, index);
    if (shouldWriteElement) {
      this.pushTag_11rb$(tag);
    }return shouldWriteElement;
  };
  TaggedEncoder.prototype.shouldWriteElement_a5qihn$ = function (desc, tag, index) {
    return true;
  };
  TaggedEncoder.prototype.encodeNotNullMark = function () {
    this.encodeTaggedNotNullMark_11rb$(this.currentTag);
  };
  TaggedEncoder.prototype.encodeNull = function () {
    this.encodeTaggedNull_11rb$(this.popTag());
  };
  TaggedEncoder.prototype.encodeUnit = function () {
    UnitSerializer_getInstance().serialize_awe97i$(this, Unit);
  };
  TaggedEncoder.prototype.encodeBoolean_6taknv$ = function (value) {
    this.encodeTaggedBoolean_iuyhfk$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeByte_s8j3t7$ = function (value) {
    this.encodeTaggedByte_19qe40$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeShort_mq22fl$ = function (value) {
    this.encodeTaggedShort_veccj0$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeInt_za3lpa$ = function (value) {
    this.encodeTaggedInt_dpg1yx$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeLong_s8cxhz$ = function (value) {
    this.encodeTaggedLong_19wkf8$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeFloat_mx4ult$ = function (value) {
    this.encodeTaggedFloat_vlf4p8$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeDouble_14dthe$ = function (value) {
    this.encodeTaggedDouble_e37ph5$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeChar_s8itvh$ = function (value) {
    this.encodeTaggedChar_19qo1q$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeString_61zpoe$ = function (value) {
    this.encodeTaggedString_l9l8mx$(this.popTag(), value);
  };
  TaggedEncoder.prototype.encodeEnum_3zr2iy$ = function (enumDescriptor, index) {
    this.encodeTaggedEnum_v153v3$(this.popTag(), enumDescriptor, index);
  };
  TaggedEncoder.prototype.beginStructure_r0sa6z$ = function (descriptor, typeSerializers) {
    return this;
  };
  TaggedEncoder.prototype.endStructure_qatsm0$ = function (descriptor) {
    if (!this.tagStack_cfsfm$_0.isEmpty())
      this.popTag();
    this.endEncode_qatsm0$(descriptor);
  };
  TaggedEncoder.prototype.endEncode_qatsm0$ = function (descriptor) {
  };
  TaggedEncoder.prototype.encodeUnitElement_3zr2iy$ = function (descriptor, index) {
    this.encodeTaggedUnit_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedEncoder.prototype.encodeBooleanElement_w1b0nl$ = function (descriptor, index, value) {
    this.encodeTaggedBoolean_iuyhfk$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeByteElement_a3tadb$ = function (descriptor, index, value) {
    this.encodeTaggedByte_19qe40$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeShortElement_tet9k5$ = function (descriptor, index, value) {
    this.encodeTaggedShort_veccj0$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeIntElement_4wpqag$ = function (descriptor, index, value) {
    this.encodeTaggedInt_dpg1yx$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeLongElement_a3zgoj$ = function (descriptor, index, value) {
    this.encodeTaggedLong_19wkf8$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeFloatElement_t7qhdx$ = function (descriptor, index, value) {
    this.encodeTaggedFloat_vlf4p8$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeDoubleElement_imzr5k$ = function (descriptor, index, value) {
    this.encodeTaggedDouble_e37ph5$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeCharElement_a3tkb1$ = function (descriptor, index, value) {
    this.encodeTaggedChar_19qo1q$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeStringElement_bgm7zs$ = function (descriptor, index, value) {
    this.encodeTaggedString_l9l8mx$(this.getTag_m47q6f$(descriptor, index), value);
  };
  TaggedEncoder.prototype.encodeSerializableElement_blecud$ = function (descriptor, index, serializer, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeSerializableValue_tf03ej$(serializer, value);
  };
  TaggedEncoder.prototype.encodeNullableSerializableElement_orpvvi$ = function (descriptor, index, serializer, value) {
    if (this.encodeElement_3zr2iy$(descriptor, index))
      this.encodeNullableSerializableValue_f4686g$(serializer, value);
  };
  Object.defineProperty(TaggedEncoder.prototype, 'currentTag', {
    get: function () {
      return last(this.tagStack_cfsfm$_0);
    }
  });
  Object.defineProperty(TaggedEncoder.prototype, 'currentTagOrNull', {
    get: function () {
      return lastOrNull(this.tagStack_cfsfm$_0);
    }
  });
  TaggedEncoder.prototype.pushTag_11rb$ = function (name) {
    this.tagStack_cfsfm$_0.add_11rb$(name);
  };
  TaggedEncoder.prototype.popTag = function () {
    if (!this.tagStack_cfsfm$_0.isEmpty())
      return this.tagStack_cfsfm$_0.removeAt_za3lpa$(get_lastIndex(this.tagStack_cfsfm$_0));
    else
      throw new SerializationException('No tag in stack for requested element');
  };
  TaggedEncoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TaggedEncoder',
    interfaces: [CompositeEncoder, Encoder]
  };
  function NamedValueEncoder(rootName) {
    if (rootName === void 0)
      rootName = '';
    TaggedEncoder.call(this);
    this.rootName = rootName;
  }
  NamedValueEncoder.prototype.getTag_m47q6f$ = function ($receiver, index) {
    return this.nested_61zpoe$(this.elementName_3zr2iy$($receiver, index));
  };
  NamedValueEncoder.prototype.nested_61zpoe$ = function (nestedName) {
    var tmp$;
    return this.composeName_puj7f4$((tmp$ = this.currentTagOrNull) != null ? tmp$ : this.rootName, nestedName);
  };
  NamedValueEncoder.prototype.elementName_3zr2iy$ = function (descriptor, index) {
    return descriptor.getElementName_za3lpa$(index);
  };
  NamedValueEncoder.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return parentName.length === 0 ? childName : parentName + '.' + childName;
  };
  NamedValueEncoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NamedValueEncoder',
    interfaces: [TaggedEncoder]
  };
  function TaggedDecoder() {
    this.updateMode_cp3ftw$_0 = UpdateMode$UPDATE_getInstance();
    this.tagStack_h2rpra$_0 = ArrayList_init_0();
    this.flag_kguhq4$_0 = false;
  }
  Object.defineProperty(TaggedDecoder.prototype, 'context', {
    get: function () {
      return EmptyModule_getInstance();
    }
  });
  Object.defineProperty(TaggedDecoder.prototype, 'updateMode', {
    get: function () {
      return this.updateMode_cp3ftw$_0;
    }
  });
  TaggedDecoder.prototype.decodeTaggedValue_11rb$ = function (tag) {
    throw new SerializationException(Kotlin.getKClassFromExpression(this).toString() + " can't retrieve untyped values");
  };
  TaggedDecoder.prototype.decodeTaggedNotNullMark_11rb$ = function (tag) {
    return true;
  };
  TaggedDecoder.prototype.decodeTaggedNull_11rb$ = function (tag) {
    return null;
  };
  TaggedDecoder.prototype.decodeTaggedUnit_11rb$ = function (tag) {
    var tmp$;
    Kotlin.isType(tmp$ = this.decodeTaggedValue_11rb$(tag), Object.getPrototypeOf(kotlin.Unit).constructor) ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedBoolean_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'boolean' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedByte_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedShort_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedInt_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedLong_11rb$ = function (tag) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.decodeTaggedValue_11rb$(tag), Kotlin.Long) ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedFloat_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedDouble_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedChar_11rb$ = function (tag) {
    var tmp$;
    return Kotlin.isChar(tmp$ = this.decodeTaggedValue_11rb$(tag)) ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedString_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'string' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeTaggedEnum_xicdkz$ = function (tag, enumDescription) {
    var tmp$;
    return typeof (tmp$ = this.decodeTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedDecoder.prototype.decodeNotNullMark = function () {
    return this.decodeTaggedNotNullMark_11rb$(this.currentTag);
  };
  TaggedDecoder.prototype.decodeNull = function () {
    return null;
  };
  TaggedDecoder.prototype.decodeUnit = function () {
    UnitSerializer_getInstance().deserialize_nts5qn$(this);
  };
  TaggedDecoder.prototype.decodeBoolean = function () {
    return this.decodeTaggedBoolean_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeByte = function () {
    return this.decodeTaggedByte_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeShort = function () {
    return this.decodeTaggedShort_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeInt = function () {
    return this.decodeTaggedInt_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeLong = function () {
    return this.decodeTaggedLong_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeFloat = function () {
    return this.decodeTaggedFloat_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeDouble = function () {
    return this.decodeTaggedDouble_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeChar = function () {
    return this.decodeTaggedChar_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeString = function () {
    return this.decodeTaggedString_11rb$(this.popTag());
  };
  TaggedDecoder.prototype.decodeEnum_qatsm0$ = function (enumDescriptor) {
    return this.decodeTaggedEnum_xicdkz$(this.popTag(), enumDescriptor);
  };
  TaggedDecoder.prototype.beginStructure_r0sa6z$ = function (descriptor, typeParams) {
    return this;
  };
  TaggedDecoder.prototype.endStructure_qatsm0$ = function (descriptor) {
  };
  TaggedDecoder.prototype.decodeUnitElement_3zr2iy$ = function (descriptor, index) {
    this.decodeTaggedUnit_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeBooleanElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedBoolean_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeByteElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedByte_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeShortElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedShort_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeIntElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedInt_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeLongElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedLong_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeFloatElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedFloat_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeDoubleElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedDouble_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeCharElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedChar_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  TaggedDecoder.prototype.decodeStringElement_3zr2iy$ = function (descriptor, index) {
    return this.decodeTaggedString_11rb$(this.getTag_m47q6f$(descriptor, index));
  };
  function TaggedDecoder$decodeSerializableElement$lambda(closure$deserializer, this$TaggedDecoder) {
    return function () {
      return this$TaggedDecoder.decodeSerializableValue_w63s0f$(closure$deserializer);
    };
  }
  TaggedDecoder.prototype.decodeSerializableElement_s44l7r$ = function (descriptor, index, deserializer) {
    return this.tagBlock_lngyui$_0(this.getTag_m47q6f$(descriptor, index), TaggedDecoder$decodeSerializableElement$lambda(deserializer, this));
  };
  function TaggedDecoder$decodeNullableSerializableElement$lambda(closure$deserializer, this$TaggedDecoder) {
    return function () {
      return this$TaggedDecoder.decodeNullableSerializableValue_aae3ea$(closure$deserializer);
    };
  }
  TaggedDecoder.prototype.decodeNullableSerializableElement_cwlm4k$ = function (descriptor, index, deserializer) {
    return this.tagBlock_lngyui$_0(this.getTag_m47q6f$(descriptor, index), TaggedDecoder$decodeNullableSerializableElement$lambda(deserializer, this));
  };
  function TaggedDecoder$updateSerializableElement$lambda(closure$deserializer, closure$old, this$TaggedDecoder) {
    return function () {
      return this$TaggedDecoder.updateSerializableValue_19c8k5$(closure$deserializer, closure$old);
    };
  }
  TaggedDecoder.prototype.updateSerializableElement_ehubvl$ = function (descriptor, index, deserializer, old) {
    return this.tagBlock_lngyui$_0(this.getTag_m47q6f$(descriptor, index), TaggedDecoder$updateSerializableElement$lambda(deserializer, old, this));
  };
  function TaggedDecoder$updateNullableSerializableElement$lambda(closure$deserializer, closure$old, this$TaggedDecoder) {
    return function () {
      return this$TaggedDecoder.updateNullableSerializableValue_exmlbs$(closure$deserializer, closure$old);
    };
  }
  TaggedDecoder.prototype.updateNullableSerializableElement_u33s02$ = function (descriptor, index, deserializer, old) {
    return this.tagBlock_lngyui$_0(this.getTag_m47q6f$(descriptor, index), TaggedDecoder$updateNullableSerializableElement$lambda(deserializer, old, this));
  };
  TaggedDecoder.prototype.tagBlock_lngyui$_0 = function (tag, block) {
    this.pushTag_11rb$(tag);
    var r = block();
    if (!this.flag_kguhq4$_0) {
      this.popTag();
    }this.flag_kguhq4$_0 = false;
    return r;
  };
  Object.defineProperty(TaggedDecoder.prototype, 'currentTag', {
    get: function () {
      return last(this.tagStack_h2rpra$_0);
    }
  });
  Object.defineProperty(TaggedDecoder.prototype, 'currentTagOrNull', {
    get: function () {
      return lastOrNull(this.tagStack_h2rpra$_0);
    }
  });
  TaggedDecoder.prototype.pushTag_11rb$ = function (name) {
    this.tagStack_h2rpra$_0.add_11rb$(name);
  };
  TaggedDecoder.prototype.copyTagsTo_lgvuxj$ = function (other) {
    other.tagStack_h2rpra$_0.addAll_brywnq$(this.tagStack_h2rpra$_0);
  };
  TaggedDecoder.prototype.popTag = function () {
    var r = this.tagStack_h2rpra$_0.removeAt_za3lpa$(get_lastIndex(this.tagStack_h2rpra$_0));
    this.flag_kguhq4$_0 = true;
    return r;
  };
  TaggedDecoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TaggedDecoder',
    interfaces: [CompositeDecoder, Decoder]
  };
  function NamedValueDecoder(rootName) {
    if (rootName === void 0)
      rootName = '';
    TaggedDecoder.call(this);
    this.rootName = rootName;
  }
  NamedValueDecoder.prototype.getTag_m47q6f$ = function ($receiver, index) {
    return this.nested_61zpoe$(this.elementName_3zr2iy$($receiver, index));
  };
  NamedValueDecoder.prototype.nested_61zpoe$ = function (nestedName) {
    var tmp$;
    return this.composeName_puj7f4$((tmp$ = this.currentTagOrNull) != null ? tmp$ : this.rootName, nestedName);
  };
  NamedValueDecoder.prototype.elementName_3zr2iy$ = function (desc, index) {
    return desc.getElementName_za3lpa$(index);
  };
  NamedValueDecoder.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return parentName.length === 0 ? childName : parentName + '.' + childName;
  };
  NamedValueDecoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NamedValueDecoder',
    interfaces: [TaggedDecoder]
  };
  var NULL;
  var deprecationMessage;
  function KeyValueSerializer(keySerializer, valueSerializer) {
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
  }
  KeyValueSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var structuredEncoder = encoder.beginStructure_r0sa6z$(this.descriptor, [this.keySerializer, this.valueSerializer]);
    structuredEncoder.encodeSerializableElement_blecud$(this.descriptor, 0, this.keySerializer, this.get_key_wili$(value));
    structuredEncoder.encodeSerializableElement_blecud$(this.descriptor, 1, this.valueSerializer, this.get_value_wili$(value));
    structuredEncoder.endStructure_qatsm0$(this.descriptor);
  };
  KeyValueSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var tmp$, tmp$_0;
    var composite = decoder.beginStructure_r0sa6z$(this.descriptor, [this.keySerializer, this.valueSerializer]);
    if (composite.decodeSequentially()) {
      var key = composite.decodeSerializableElement_s44l7r$(this.descriptor, 0, this.keySerializer);
      var value = composite.decodeSerializableElement_s44l7r$(this.descriptor, 1, this.valueSerializer);
      return this.toResult_xwzc9p$(key, value);
    }var key_0 = NULL;
    var value_0 = NULL;
    mainLoop: while (true) {
      var idx = composite.decodeElementIndex_qatsm0$(this.descriptor);
      switch (idx) {
        case -1:
          break mainLoop;
        case 0:
          key_0 = composite.decodeSerializableElement_s44l7r$(this.descriptor, 0, this.keySerializer);
          break;
        case 1:
          value_0 = composite.decodeSerializableElement_s44l7r$(this.descriptor, 1, this.valueSerializer);
          break;
        default:throw new SerializationException('Invalid index: ' + idx);
      }
    }
    composite.endStructure_qatsm0$(this.descriptor);
    if (key_0 === NULL)
      throw new SerializationException("Element 'key' is missing");
    if (value_0 === NULL)
      throw new SerializationException("Element 'value' is missing");
    return this.toResult_xwzc9p$((tmp$ = key_0) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), (tmp$_0 = value_0) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE());
  };
  KeyValueSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KeyValueSerializer',
    interfaces: [KSerializer]
  };
  function MapEntrySerializer_0(keySerializer, valueSerializer) {
    KeyValueSerializer.call(this, keySerializer, valueSerializer);
    this.descriptor_cnmk75$_0 = SerialDescriptor_0('kotlin.collections.Map.Entry', StructureKind$MAP_getInstance(), MapEntrySerializer$descriptor$lambda(keySerializer, valueSerializer));
  }
  function MapEntrySerializer$MapEntry(key, value) {
    this.key_7uv6mv$_0 = key;
    this.value_gjenjd$_0 = value;
  }
  Object.defineProperty(MapEntrySerializer$MapEntry.prototype, 'key', {
    get: function () {
      return this.key_7uv6mv$_0;
    }
  });
  Object.defineProperty(MapEntrySerializer$MapEntry.prototype, 'value', {
    get: function () {
      return this.value_gjenjd$_0;
    }
  });
  MapEntrySerializer$MapEntry.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntry',
    interfaces: [Map$Entry]
  };
  MapEntrySerializer$MapEntry.prototype.component1 = function () {
    return this.key;
  };
  MapEntrySerializer$MapEntry.prototype.component2 = function () {
    return this.value;
  };
  MapEntrySerializer$MapEntry.prototype.copy_xwzc9p$ = function (key, value) {
    return new MapEntrySerializer$MapEntry(key === void 0 ? this.key : key, value === void 0 ? this.value : value);
  };
  MapEntrySerializer$MapEntry.prototype.toString = function () {
    return 'MapEntry(key=' + Kotlin.toString(this.key) + (', value=' + Kotlin.toString(this.value)) + ')';
  };
  MapEntrySerializer$MapEntry.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.key) | 0;
    result = result * 31 + Kotlin.hashCode(this.value) | 0;
    return result;
  };
  MapEntrySerializer$MapEntry.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.key, other.key) && Kotlin.equals(this.value, other.value)))));
  };
  Object.defineProperty(MapEntrySerializer_0.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_cnmk75$_0;
    }
  });
  MapEntrySerializer_0.prototype.get_key_wili$ = function ($receiver) {
    return $receiver.key;
  };
  MapEntrySerializer_0.prototype.get_value_wili$ = function ($receiver) {
    return $receiver.value;
  };
  MapEntrySerializer_0.prototype.toResult_xwzc9p$ = function (key, value) {
    return new MapEntrySerializer$MapEntry(key, value);
  };
  function MapEntrySerializer$descriptor$lambda(closure$keySerializer, closure$valueSerializer) {
    return function ($receiver) {
      $receiver.element_re18qg$('key', closure$keySerializer.descriptor);
      $receiver.element_re18qg$('value', closure$valueSerializer.descriptor);
      return Unit;
    };
  }
  MapEntrySerializer_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntrySerializer',
    interfaces: [KeyValueSerializer]
  };
  function PairSerializer_0(keySerializer, valueSerializer) {
    KeyValueSerializer.call(this, keySerializer, valueSerializer);
    this.descriptor_utc4rp$_0 = SerialDescriptor_0('kotlin.Pair', void 0, PairSerializer$descriptor$lambda(keySerializer, valueSerializer));
  }
  Object.defineProperty(PairSerializer_0.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_utc4rp$_0;
    }
  });
  PairSerializer_0.prototype.get_key_wili$ = function ($receiver) {
    return $receiver.first;
  };
  PairSerializer_0.prototype.get_value_wili$ = function ($receiver) {
    return $receiver.second;
  };
  PairSerializer_0.prototype.toResult_xwzc9p$ = function (key, value) {
    return to(key, value);
  };
  function PairSerializer$descriptor$lambda(closure$keySerializer, closure$valueSerializer) {
    return function ($receiver) {
      $receiver.element_re18qg$('first', closure$keySerializer.descriptor);
      $receiver.element_re18qg$('second', closure$valueSerializer.descriptor);
      return Unit;
    };
  }
  PairSerializer_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PairSerializer',
    interfaces: [KeyValueSerializer]
  };
  function TripleSerializer_0(aSerializer, bSerializer, cSerializer) {
    this.aSerializer_0 = aSerializer;
    this.bSerializer_0 = bSerializer;
    this.cSerializer_0 = cSerializer;
    this.descriptor_73a6vr$_0 = SerialDescriptor_0('kotlin.Triple', void 0, TripleSerializer$descriptor$lambda(this));
  }
  Object.defineProperty(TripleSerializer_0.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_73a6vr$_0;
    }
  });
  TripleSerializer_0.prototype.serialize_awe97i$ = function (encoder, value) {
    var structuredEncoder = encoder.beginStructure_r0sa6z$(this.descriptor, [this.aSerializer_0, this.bSerializer_0, this.cSerializer_0]);
    structuredEncoder.encodeSerializableElement_blecud$(this.descriptor, 0, this.aSerializer_0, value.first);
    structuredEncoder.encodeSerializableElement_blecud$(this.descriptor, 1, this.bSerializer_0, value.second);
    structuredEncoder.encodeSerializableElement_blecud$(this.descriptor, 2, this.cSerializer_0, value.third);
    structuredEncoder.endStructure_qatsm0$(this.descriptor);
  };
  TripleSerializer_0.prototype.deserialize_nts5qn$ = function (decoder) {
    var composite = decoder.beginStructure_r0sa6z$(this.descriptor, [this.aSerializer_0, this.bSerializer_0, this.cSerializer_0]);
    if (composite.decodeSequentially()) {
      return this.decodeSequentially_0(composite);
    }return this.decodeStructure_0(composite);
  };
  TripleSerializer_0.prototype.decodeSequentially_0 = function (composite) {
    var a = composite.decodeSerializableElement_s44l7r$(this.descriptor, 0, this.aSerializer_0);
    var b = composite.decodeSerializableElement_s44l7r$(this.descriptor, 1, this.bSerializer_0);
    var c = composite.decodeSerializableElement_s44l7r$(this.descriptor, 2, this.cSerializer_0);
    composite.endStructure_qatsm0$(this.descriptor);
    return new Triple(a, b, c);
  };
  TripleSerializer_0.prototype.decodeStructure_0 = function (composite) {
    var tmp$, tmp$_0, tmp$_1;
    var a = NULL;
    var b = NULL;
    var c = NULL;
    mainLoop: while (true) {
      var index = composite.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -1:
          break mainLoop;
        case 0:
          a = composite.decodeSerializableElement_s44l7r$(this.descriptor, 0, this.aSerializer_0);
          break;
        case 1:
          b = composite.decodeSerializableElement_s44l7r$(this.descriptor, 1, this.bSerializer_0);
          break;
        case 2:
          c = composite.decodeSerializableElement_s44l7r$(this.descriptor, 2, this.cSerializer_0);
          break;
        default:throw new SerializationException('Unexpected index ' + index);
      }
    }
    composite.endStructure_qatsm0$(this.descriptor);
    if (a === NULL)
      throw new SerializationException("Element 'first' is missing");
    if (b === NULL)
      throw new SerializationException("Element 'second' is missing");
    if (c === NULL)
      throw new SerializationException("Element 'third' is missing");
    return new Triple((tmp$ = a) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), (tmp$_0 = b) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE(), (tmp$_1 = c) == null || Kotlin.isType(tmp$_1, Any) ? tmp$_1 : throwCCE());
  };
  function TripleSerializer$descriptor$lambda(this$TripleSerializer) {
    return function ($receiver) {
      $receiver.element_re18qg$('first', this$TripleSerializer.aSerializer_0.descriptor);
      $receiver.element_re18qg$('second', this$TripleSerializer.bSerializer_0.descriptor);
      $receiver.element_re18qg$('third', this$TripleSerializer.cSerializer_0.descriptor);
      return Unit;
    };
  }
  TripleSerializer_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TripleSerializer',
    interfaces: [KSerializer]
  };
  function readExactNBytes($receiver, bytes) {
    var array = new Int8Array(bytes);
    var read = 0;
    while (read < bytes) {
      var i = $receiver.read_mj6st8$(array, read, bytes - read | 0);
      if (i === -1) {
        throw IllegalStateException_init('Unexpected EOF'.toString());
      }read = read + i | 0;
    }
    return array;
  }
  function InternalHexConverter() {
    InternalHexConverter_instance = this;
    this.hexCode_0 = '0123456789ABCDEF';
  }
  InternalHexConverter.prototype.parseHexBinary_61zpoe$ = function (s) {
    var len = s.length;
    if (!(len % 2 === 0)) {
      var message = 'HexBinary string must be even length';
      throw IllegalArgumentException_init(message.toString());
    }var bytes = new Int8Array(len / 2 | 0);
    var i = {v: 0};
    while (i.v < len) {
      var h = this.hexToInt_0(s.charCodeAt(i.v));
      var l = this.hexToInt_0(s.charCodeAt(i.v + 1 | 0));
      if (!!(h === -1 || l === -1)) {
        var message_0 = 'Invalid hex chars: ' + String.fromCharCode(s.charCodeAt(i.v)) + String.fromCharCode(s.charCodeAt(i.v + 1 | 0));
        throw IllegalArgumentException_init(message_0.toString());
      }bytes[i.v / 2 | 0] = toByte((h << 4) + l | 0);
      i.v = i.v + 2 | 0;
    }
    return bytes;
  };
  InternalHexConverter.prototype.hexToInt_0 = function (ch) {
    if ((new CharRange(48, 57)).contains_mef7kx$(ch))
      return ch - 48;
    else if ((new CharRange(65, 70)).contains_mef7kx$(ch))
      return ch - 65 + 10 | 0;
    else if ((new CharRange(97, 102)).contains_mef7kx$(ch))
      return ch - 97 + 10 | 0;
    else
      return -1;
  };
  InternalHexConverter.prototype.printHexBinary_1fhb37$ = function (data, lowerCase) {
    if (lowerCase === void 0)
      lowerCase = false;
    var tmp$;
    var r = StringBuilder_init(data.length * 2 | 0);
    for (tmp$ = 0; tmp$ !== data.length; ++tmp$) {
      var b = data[tmp$];
      r.append_s8itvh$(this.hexCode_0.charCodeAt(b >> 4 & 15));
      r.append_s8itvh$(this.hexCode_0.charCodeAt(b & 15));
    }
    return lowerCase ? r.toString().toLowerCase() : r.toString();
  };
  InternalHexConverter.prototype.toHexString_za3lpa$ = function (n) {
    var tmp$;
    var arr = new Int8Array(4);
    for (var i = 0; i < 4; i++) {
      arr[i] = toByte(n >> 24 - (i * 8 | 0));
    }
    var $receiver = trimStart(this.printHexBinary_1fhb37$(arr, true), Kotlin.charArrayOf(48));
    return (tmp$ = $receiver.length > 0 ? $receiver : null) != null ? tmp$ : '0';
  };
  InternalHexConverter.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'InternalHexConverter',
    interfaces: []
  };
  var InternalHexConverter_instance = null;
  function InternalHexConverter_getInstance() {
    if (InternalHexConverter_instance === null) {
      new InternalHexConverter();
    }return InternalHexConverter_instance;
  }
  function HexConverter() {
    HexConverter_instance = this;
    this.hexCode_0 = '0123456789ABCDEF';
  }
  HexConverter.prototype.parseHexBinary_61zpoe$ = function (s) {
    return InternalHexConverter_getInstance().parseHexBinary_61zpoe$(s);
  };
  HexConverter.prototype.printHexBinary_1fhb37$ = function (data, lowerCase) {
    if (lowerCase === void 0)
      lowerCase = false;
    return InternalHexConverter_getInstance().printHexBinary_1fhb37$(data, lowerCase);
  };
  HexConverter.prototype.toHexString_za3lpa$ = function (n) {
    return InternalHexConverter_getInstance().toHexString_za3lpa$(n);
  };
  HexConverter.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'HexConverter',
    interfaces: []
  };
  var HexConverter_instance = null;
  function HexConverter_getInstance() {
    if (HexConverter_instance === null) {
      new HexConverter();
    }return HexConverter_instance;
  }
  function cachedSerialNames($receiver) {
    var tmp$;
    if (Kotlin.isType($receiver, PluginGeneratedSerialDescriptor))
      return $receiver.namesSet_8be2vx$;
    var result = HashSet_init_1($receiver.elementsCount);
    tmp$ = $receiver.elementsCount;
    for (var i = 0; i < tmp$; i++) {
      var element = $receiver.getElementName_za3lpa$(i);
      result.add_11rb$(element);
    }
    return result;
  }
  function defer$ObjectLiteral(closure$deferred) {
    this.original_s64f0k$_0 = lazy(closure$deferred);
  }
  Object.defineProperty(defer$ObjectLiteral.prototype, 'original_0', {
    get: function () {
      return this.original_s64f0k$_0.value;
    }
  });
  Object.defineProperty(defer$ObjectLiteral.prototype, 'serialName', {
    get: function () {
      return this.original_0.serialName;
    }
  });
  Object.defineProperty(defer$ObjectLiteral.prototype, 'kind', {
    get: function () {
      return this.original_0.kind;
    }
  });
  Object.defineProperty(defer$ObjectLiteral.prototype, 'elementsCount', {
    get: function () {
      return this.original_0.elementsCount;
    }
  });
  defer$ObjectLiteral.prototype.getElementName_za3lpa$ = function (index) {
    return this.original_0.getElementName_za3lpa$(index);
  };
  defer$ObjectLiteral.prototype.getElementIndex_61zpoe$ = function (name) {
    return this.original_0.getElementIndex_61zpoe$(name);
  };
  defer$ObjectLiteral.prototype.getElementAnnotations_za3lpa$ = function (index) {
    return this.original_0.getElementAnnotations_za3lpa$(index);
  };
  defer$ObjectLiteral.prototype.getElementDescriptor_za3lpa$ = function (index) {
    return this.original_0.getElementDescriptor_za3lpa$(index);
  };
  defer$ObjectLiteral.prototype.isElementOptional_za3lpa$ = function (index) {
    return this.original_0.isElementOptional_za3lpa$(index);
  };
  defer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [SerialDescriptor]
  };
  function defer(deferred) {
    return new defer$ObjectLiteral(deferred);
  }
  var cast = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.internal.cast_irzu8f$', wrapFunction(function () {
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function ($receiver) {
      var tmp$;
      return Kotlin.isType(tmp$ = $receiver, KSerializer) ? tmp$ : throwCCE();
    };
  }));
  function Json(configuration, context) {
    Json$Default_getInstance();
    if (configuration === void 0)
      configuration = JsonConfiguration$Companion_getInstance().Stable;
    if (context === void 0)
      context = EmptyModule_getInstance();
    this.configuration_8be2vx$ = configuration;
    this.context_h744e8$_0 = plus(context, defaultJsonModule);
    this.validateConfiguration_0();
  }
  Object.defineProperty(Json.prototype, 'context', {
    get: function () {
      return this.context_h744e8$_0;
    }
  });
  Json.prototype.stringify_tf03ej$ = function (serializer, value) {
    var result = StringBuilder_init_0();
    var encoder = StreamingJsonOutput_init(result, this, WriteMode$OBJ_getInstance(), Kotlin.newArray(WriteMode$values().length, null));
    encode(encoder, serializer, value);
    return result.toString();
  };
  Json.prototype.toJson_tf03ej$ = function (serializer, value) {
    return writeJson(this, value, serializer);
  };
  Json.prototype.toJson_issdgt$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.Json.toJson_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, value) {
      return this.toJson_tf03ej$(getContextualOrDefault(this.context, getKClass(T_0)), value);
    };
  }));
  Json.prototype.parse_awif5v$ = function (deserializer, string) {
    var reader = new JsonReader(string);
    var input = new StreamingJsonInput(this, WriteMode$OBJ_getInstance(), reader);
    var result = decode(input, deserializer);
    if (!reader.isDone) {
      throw IllegalStateException_init(('Reader has not consumed the whole input: ' + reader).toString());
    }return result;
  };
  Json.prototype.parseJson_61zpoe$ = function (string) {
    return this.parse_awif5v$(JsonElementSerializer_getInstance(), string);
  };
  Json.prototype.fromJson_htt2tq$ = function (deserializer, json) {
    return readJson(this, json, deserializer);
  };
  Json.prototype.fromJson_65rf1y$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.Json.fromJson_65rf1y$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, tree) {
      return this.fromJson_htt2tq$(getContextualOrDefault(this.context, getKClass(T_0)), tree);
    };
  }));
  function Json$Default() {
    Json$Default_instance = this;
    this.message_0 = 'Top-level JSON instances are deprecated for removal in the favour of user-configured one. ' + "You can either use a Json top-level object, configure your own instance  via 'Json {}' builder-like constructor, " + "'Json(JsonConfiguration)' constructor or by tweaking stable configuration 'Json(JsonConfiguration.Stable.copy(prettyPrint = true))'";
    this.plain = new Json(new JsonConfiguration(void 0, void 0, void 0, void 0, void 0, void 0, void 0, void 0, true));
    this.unquoted = new Json(new JsonConfiguration(void 0, true, true, true, void 0, void 0, true, void 0, true));
    this.indented = new Json(new JsonConfiguration(void 0, void 0, void 0, void 0, void 0, true, void 0, void 0, true));
    this.nonstrict = new Json(new JsonConfiguration(void 0, true, true, true, void 0, void 0, void 0, void 0, true));
    this.jsonInstance_0 = new Json(JsonConfiguration$Companion_getInstance().Default);
  }
  Object.defineProperty(Json$Default.prototype, 'context', {
    get: function () {
      return this.jsonInstance_0.context;
    }
  });
  Json$Default.prototype.stringify_tf03ej$ = function (serializer, value) {
    return this.jsonInstance_0.stringify_tf03ej$(serializer, value);
  };
  Json$Default.prototype.parse_awif5v$ = function (deserializer, string) {
    return this.jsonInstance_0.parse_awif5v$(deserializer, string);
  };
  Json$Default.prototype.toJson_tf03ej$ = function (serializer, value) {
    return writeJson(this.jsonInstance_0, value, serializer);
  };
  Json$Default.prototype.toJson_issdgt$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.Json.Default.toJson_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, value) {
      return this.toJson_tf03ej$(getContextualOrDefault(this.context, getKClass(T_0)), value);
    };
  }));
  Json$Default.prototype.parseJson_61zpoe$ = function (string) {
    return this.parse_awif5v$(JsonElementSerializer_getInstance(), string);
  };
  Json$Default.prototype.fromJson_htt2tq$ = function (deserializer, json) {
    return readJson(this.jsonInstance_0, json, deserializer);
  };
  Json$Default.prototype.fromJson_65rf1y$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.Json.Default.fromJson_65rf1y$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, tree) {
      return this.fromJson_htt2tq$(getContextualOrDefault(this.context, getKClass(T_0)), tree);
    };
  }));
  Json$Default.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Default',
    interfaces: [StringFormat]
  };
  var Json$Default_instance = null;
  function Json$Default_getInstance() {
    if (Json$Default_instance === null) {
      new Json$Default();
    }return Json$Default_instance;
  }
  Json.prototype.validateConfiguration_0 = function () {
    if (this.configuration_8be2vx$.useArrayPolymorphism_8be2vx$)
      return;
    var collector = new ContextValidator(this.configuration_8be2vx$.classDiscriminator_8be2vx$);
    this.context.dumpTo_247rdd$(collector);
  };
  Json.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Json',
    interfaces: [StringFormat]
  };
  function Json_init(block, $this) {
    $this = $this || Object.create(Json.prototype);
    var $receiver = new JsonBuilder();
    block($receiver);
    Json_init_1($receiver, $this);
    return $this;
  }
  function Json_init_0($this) {
    $this = $this || Object.create(Json.prototype);
    Json.call($this, new JsonConfiguration(void 0, void 0, void 0, void 0, void 0, void 0, void 0, void 0, true));
    return $this;
  }
  function Json_init_1(builder, $this) {
    $this = $this || Object.create(Json.prototype);
    Json.call($this, builder.buildConfiguration(), builder.buildModule());
    return $this;
  }
  function JsonBuilder() {
    this.encodeDefaults = true;
    this.strictMode = true;
    this.ignoreUnknownKeys = false;
    this.isLenient = false;
    this.serializeSpecialFloatingPointValues = false;
    this.unquoted = false;
    this.allowStructuredMapKeys = false;
    this.prettyPrint = false;
    this.unquotedPrint = false;
    this.indent = '    ';
    this.useArrayPolymorphism = false;
    this.classDiscriminator = 'type';
    this.serialModule = EmptyModule_getInstance();
  }
  JsonBuilder.prototype.buildConfiguration = function () {
    return new JsonConfiguration(this.encodeDefaults, this.ignoreUnknownKeys, this.isLenient, this.serializeSpecialFloatingPointValues, this.allowStructuredMapKeys, this.prettyPrint, this.unquotedPrint, this.indent, this.useArrayPolymorphism, this.classDiscriminator);
  };
  JsonBuilder.prototype.buildModule = function () {
    return this.serialModule;
  };
  JsonBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonBuilder',
    interfaces: []
  };
  var defaultJsonModule;
  var lenientHint;
  function JsonConfiguration(encodeDefaults, ignoreUnknownKeys, isLenient, serializeSpecialFloatingPointValues, allowStructuredMapKeys, prettyPrint, unquotedPrint, indent, useArrayPolymorphism, classDiscriminator, updateMode) {
    JsonConfiguration$Companion_getInstance();
    if (encodeDefaults === void 0)
      encodeDefaults = true;
    if (ignoreUnknownKeys === void 0)
      ignoreUnknownKeys = false;
    if (isLenient === void 0)
      isLenient = false;
    if (serializeSpecialFloatingPointValues === void 0)
      serializeSpecialFloatingPointValues = false;
    if (allowStructuredMapKeys === void 0)
      allowStructuredMapKeys = false;
    if (prettyPrint === void 0)
      prettyPrint = false;
    if (unquotedPrint === void 0)
      unquotedPrint = false;
    if (indent === void 0)
      indent = JsonConfiguration$Companion_getInstance().defaultIndent_0;
    if (useArrayPolymorphism === void 0)
      useArrayPolymorphism = false;
    if (classDiscriminator === void 0)
      classDiscriminator = JsonConfiguration$Companion_getInstance().defaultDiscriminator_0;
    if (updateMode === void 0)
      updateMode = UpdateMode$OVERWRITE_getInstance();
    this.encodeDefaults_8be2vx$ = encodeDefaults;
    this.ignoreUnknownKeys_8be2vx$ = ignoreUnknownKeys;
    this.isLenient_8be2vx$ = isLenient;
    this.serializeSpecialFloatingPointValues_8be2vx$ = serializeSpecialFloatingPointValues;
    this.allowStructuredMapKeys_8be2vx$ = allowStructuredMapKeys;
    this.prettyPrint_8be2vx$ = prettyPrint;
    this.unquotedPrint_8be2vx$ = unquotedPrint;
    this.indent_8be2vx$ = indent;
    this.useArrayPolymorphism_8be2vx$ = useArrayPolymorphism;
    this.classDiscriminator_8be2vx$ = classDiscriminator;
    this.updateMode_8be2vx$ = updateMode;
    if (this.useArrayPolymorphism_8be2vx$) {
      if (!equals(this.classDiscriminator_8be2vx$, JsonConfiguration$Companion_getInstance().defaultDiscriminator_0)) {
        var message = 'Class discriminator should not be specified when array polymorphism is specified';
        throw IllegalArgumentException_init(message.toString());
      }}if (!this.prettyPrint_8be2vx$) {
      if (!equals(this.indent_8be2vx$, JsonConfiguration$Companion_getInstance().defaultIndent_0)) {
        var message_0 = 'Indent should not be specified when default printing mode is used';
        throw IllegalArgumentException_init(message_0.toString());
      }}}
  function JsonConfiguration$Companion() {
    JsonConfiguration$Companion_instance = this;
    this.defaultIndent_0 = '    ';
    this.defaultDiscriminator_0 = 'type';
    this.Default = new JsonConfiguration();
    this.Stable = new JsonConfiguration(true, false, false, false, true, false, false, this.defaultIndent_0, false, this.defaultDiscriminator_0);
  }
  JsonConfiguration$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsonConfiguration$Companion_instance = null;
  function JsonConfiguration$Companion_getInstance() {
    if (JsonConfiguration$Companion_instance === null) {
      new JsonConfiguration$Companion();
    }return JsonConfiguration$Companion_instance;
  }
  JsonConfiguration.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonConfiguration',
    interfaces: []
  };
  JsonConfiguration.prototype.component1_8be2vx$ = function () {
    return this.encodeDefaults_8be2vx$;
  };
  JsonConfiguration.prototype.component2_8be2vx$ = function () {
    return this.ignoreUnknownKeys_8be2vx$;
  };
  JsonConfiguration.prototype.component3_8be2vx$ = function () {
    return this.isLenient_8be2vx$;
  };
  JsonConfiguration.prototype.component4_8be2vx$ = function () {
    return this.serializeSpecialFloatingPointValues_8be2vx$;
  };
  JsonConfiguration.prototype.component5_8be2vx$ = function () {
    return this.allowStructuredMapKeys_8be2vx$;
  };
  JsonConfiguration.prototype.component6_8be2vx$ = function () {
    return this.prettyPrint_8be2vx$;
  };
  JsonConfiguration.prototype.component7_8be2vx$ = function () {
    return this.unquotedPrint_8be2vx$;
  };
  JsonConfiguration.prototype.component8_8be2vx$ = function () {
    return this.indent_8be2vx$;
  };
  JsonConfiguration.prototype.component9_8be2vx$ = function () {
    return this.useArrayPolymorphism_8be2vx$;
  };
  JsonConfiguration.prototype.component10_8be2vx$ = function () {
    return this.classDiscriminator_8be2vx$;
  };
  JsonConfiguration.prototype.component11_8be2vx$ = function () {
    return this.updateMode_8be2vx$;
  };
  JsonConfiguration.prototype.copy_bjakrj$ = function (encodeDefaults, ignoreUnknownKeys, isLenient, serializeSpecialFloatingPointValues, allowStructuredMapKeys, prettyPrint, unquotedPrint, indent, useArrayPolymorphism, classDiscriminator, updateMode) {
    return new JsonConfiguration(encodeDefaults === void 0 ? this.encodeDefaults_8be2vx$ : encodeDefaults, ignoreUnknownKeys === void 0 ? this.ignoreUnknownKeys_8be2vx$ : ignoreUnknownKeys, isLenient === void 0 ? this.isLenient_8be2vx$ : isLenient, serializeSpecialFloatingPointValues === void 0 ? this.serializeSpecialFloatingPointValues_8be2vx$ : serializeSpecialFloatingPointValues, allowStructuredMapKeys === void 0 ? this.allowStructuredMapKeys_8be2vx$ : allowStructuredMapKeys, prettyPrint === void 0 ? this.prettyPrint_8be2vx$ : prettyPrint, unquotedPrint === void 0 ? this.unquotedPrint_8be2vx$ : unquotedPrint, indent === void 0 ? this.indent_8be2vx$ : indent, useArrayPolymorphism === void 0 ? this.useArrayPolymorphism_8be2vx$ : useArrayPolymorphism, classDiscriminator === void 0 ? this.classDiscriminator_8be2vx$ : classDiscriminator, updateMode === void 0 ? this.updateMode_8be2vx$ : updateMode);
  };
  JsonConfiguration.prototype.toString = function () {
    return 'JsonConfiguration(encodeDefaults=' + Kotlin.toString(this.encodeDefaults_8be2vx$) + (', ignoreUnknownKeys=' + Kotlin.toString(this.ignoreUnknownKeys_8be2vx$)) + (', isLenient=' + Kotlin.toString(this.isLenient_8be2vx$)) + (', serializeSpecialFloatingPointValues=' + Kotlin.toString(this.serializeSpecialFloatingPointValues_8be2vx$)) + (', allowStructuredMapKeys=' + Kotlin.toString(this.allowStructuredMapKeys_8be2vx$)) + (', prettyPrint=' + Kotlin.toString(this.prettyPrint_8be2vx$)) + (', unquotedPrint=' + Kotlin.toString(this.unquotedPrint_8be2vx$)) + (', indent=' + Kotlin.toString(this.indent_8be2vx$)) + (', useArrayPolymorphism=' + Kotlin.toString(this.useArrayPolymorphism_8be2vx$)) + (', classDiscriminator=' + Kotlin.toString(this.classDiscriminator_8be2vx$)) + (', updateMode=' + Kotlin.toString(this.updateMode_8be2vx$)) + ')';
  };
  JsonConfiguration.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.encodeDefaults_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.ignoreUnknownKeys_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.isLenient_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.serializeSpecialFloatingPointValues_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.allowStructuredMapKeys_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.prettyPrint_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.unquotedPrint_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.indent_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.useArrayPolymorphism_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.classDiscriminator_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.updateMode_8be2vx$) | 0;
    return result;
  };
  JsonConfiguration.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.encodeDefaults_8be2vx$, other.encodeDefaults_8be2vx$) && Kotlin.equals(this.ignoreUnknownKeys_8be2vx$, other.ignoreUnknownKeys_8be2vx$) && Kotlin.equals(this.isLenient_8be2vx$, other.isLenient_8be2vx$) && Kotlin.equals(this.serializeSpecialFloatingPointValues_8be2vx$, other.serializeSpecialFloatingPointValues_8be2vx$) && Kotlin.equals(this.allowStructuredMapKeys_8be2vx$, other.allowStructuredMapKeys_8be2vx$) && Kotlin.equals(this.prettyPrint_8be2vx$, other.prettyPrint_8be2vx$) && Kotlin.equals(this.unquotedPrint_8be2vx$, other.unquotedPrint_8be2vx$) && Kotlin.equals(this.indent_8be2vx$, other.indent_8be2vx$) && Kotlin.equals(this.useArrayPolymorphism_8be2vx$, other.useArrayPolymorphism_8be2vx$) && Kotlin.equals(this.classDiscriminator_8be2vx$, other.classDiscriminator_8be2vx$) && Kotlin.equals(this.updateMode_8be2vx$, other.updateMode_8be2vx$)))));
  };
  function JsonConfiguration_0(strictMode, unquoted) {
    if (strictMode === void 0)
      strictMode = true;
    if (unquoted === void 0)
      unquoted = false;
    throw IllegalStateException_init('Should not be called'.toString());
  }
  function JsonElement() {
    JsonElement$Companion_getInstance();
  }
  Object.defineProperty(JsonElement.prototype, 'primitive', {
    get: function () {
      return this.error_azfyan$_0('JsonPrimitive');
    }
  });
  Object.defineProperty(JsonElement.prototype, 'jsonObject', {
    get: function () {
      return this.error_azfyan$_0('JsonObject');
    }
  });
  Object.defineProperty(JsonElement.prototype, 'jsonArray', {
    get: function () {
      return this.error_azfyan$_0('JsonArray');
    }
  });
  Object.defineProperty(JsonElement.prototype, 'jsonNull', {
    get: function () {
      return this.error_azfyan$_0('JsonNull');
    }
  });
  Object.defineProperty(JsonElement.prototype, 'isNull', {
    get: function () {
      return this === JsonNull_getInstance();
    }
  });
  JsonElement.prototype.contains_61zpoe$ = function (key) {
    var tmp$ = Kotlin.isType(this, JsonObject);
    if (tmp$) {
      var $receiver = this.content;
      var tmp$_0;
      tmp$ = (Kotlin.isType(tmp$_0 = $receiver, Map) ? tmp$_0 : throwCCE()).containsKey_11rb$(key);
    }return tmp$;
  };
  JsonElement.prototype.error_azfyan$_0 = function (element) {
    throw new JsonException('Element ' + Kotlin.getKClassFromExpression(this) + ' is not a ' + element);
  };
  function JsonElement$Companion() {
    JsonElement$Companion_instance = this;
  }
  JsonElement$Companion.prototype.serializer = function () {
    return JsonElementSerializer_getInstance();
  };
  JsonElement$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsonElement$Companion_instance = null;
  function JsonElement$Companion_getInstance() {
    if (JsonElement$Companion_instance === null) {
      new JsonElement$Companion();
    }return JsonElement$Companion_instance;
  }
  JsonElement.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonElement',
    interfaces: []
  };
  function JsonPrimitive() {
    JsonPrimitive$Companion_getInstance();
    JsonElement.call(this);
    this.primitive_awfpe5$_0 = this;
  }
  Object.defineProperty(JsonPrimitive.prototype, 'primitive', {
    get: function () {
      return this.primitive_awfpe5$_0;
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'int', {
    get: function () {
      return toInt(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'intOrNull', {
    get: function () {
      return toIntOrNull(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'long', {
    get: function () {
      return toLong(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'longOrNull', {
    get: function () {
      return toLongOrNull(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'double', {
    get: function () {
      return toDouble(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'doubleOrNull', {
    get: function () {
      return toDoubleOrNull(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'float', {
    get: function () {
      return toDouble(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'floatOrNull', {
    get: function () {
      return toDoubleOrNull(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'boolean', {
    get: function () {
      return toBooleanStrict(this.content);
    }
  });
  Object.defineProperty(JsonPrimitive.prototype, 'booleanOrNull', {
    get: function () {
      return toBooleanStrictOrNull(this.content);
    }
  });
  JsonPrimitive.prototype.toString = function () {
    return this.content;
  };
  function JsonPrimitive$Companion() {
    JsonPrimitive$Companion_instance = this;
  }
  JsonPrimitive$Companion.prototype.serializer = function () {
    return JsonPrimitiveSerializer_getInstance();
  };
  JsonPrimitive$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsonPrimitive$Companion_instance = null;
  function JsonPrimitive$Companion_getInstance() {
    if (JsonPrimitive$Companion_instance === null) {
      new JsonPrimitive$Companion();
    }return JsonPrimitive$Companion_instance;
  }
  JsonPrimitive.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonPrimitive',
    interfaces: [JsonElement]
  };
  function JsonLiteral(body, isString) {
    JsonLiteral$Companion_getInstance();
    JsonPrimitive.call(this);
    this.body = body;
    this.isString = isString;
    this.content_prrjtz$_0 = this.body.toString();
    this.contentOrNull_mx86gf$_0 = this.content;
  }
  Object.defineProperty(JsonLiteral.prototype, 'content', {
    get: function () {
      return this.content_prrjtz$_0;
    }
  });
  Object.defineProperty(JsonLiteral.prototype, 'contentOrNull', {
    get: function () {
      return this.contentOrNull_mx86gf$_0;
    }
  });
  JsonLiteral.prototype.toString = function () {
    var tmp$;
    if (this.isString) {
      var $receiver = StringBuilder_init_0();
      printQuoted($receiver, this.content);
      tmp$ = $receiver.toString();
    } else
      tmp$ = this.content;
    return tmp$;
  };
  JsonLiteral.prototype.equals = function (other) {
    var tmp$, tmp$_0;
    if (this === other)
      return true;
    if (other == null || !((tmp$ = Kotlin.getKClassFromExpression(this)) != null ? tmp$.equals(Kotlin.getKClassFromExpression(other)) : null))
      return false;
    Kotlin.isType(tmp$_0 = other, JsonLiteral) ? tmp$_0 : throwCCE();
    if (this.isString !== other.isString)
      return false;
    if (!equals(this.content, other.content))
      return false;
    return true;
  };
  JsonLiteral.prototype.hashCode = function () {
    var result = hashCode(this.isString);
    result = (31 * result | 0) + hashCode(this.content) | 0;
    return result;
  };
  function JsonLiteral$Companion() {
    JsonLiteral$Companion_instance = this;
  }
  JsonLiteral$Companion.prototype.serializer = function () {
    return JsonLiteralSerializer_getInstance();
  };
  JsonLiteral$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsonLiteral$Companion_instance = null;
  function JsonLiteral$Companion_getInstance() {
    if (JsonLiteral$Companion_instance === null) {
      new JsonLiteral$Companion();
    }return JsonLiteral$Companion_instance;
  }
  JsonLiteral.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonLiteral',
    interfaces: [JsonPrimitive]
  };
  function JsonLiteral_init(number, $this) {
    $this = $this || Object.create(JsonLiteral.prototype);
    JsonLiteral.call($this, number, false);
    return $this;
  }
  function JsonLiteral_init_0(boolean, $this) {
    $this = $this || Object.create(JsonLiteral.prototype);
    JsonLiteral.call($this, boolean, false);
    return $this;
  }
  function JsonLiteral_init_1(string, $this) {
    $this = $this || Object.create(JsonLiteral.prototype);
    JsonLiteral.call($this, string, true);
    return $this;
  }
  function JsonNull() {
    JsonNull_instance = this;
    JsonPrimitive.call(this);
    this.jsonNull_c8yjib$_0 = this;
    this.content_w1vkof$_0 = 'null';
    this.contentOrNull_egvcud$_0 = null;
  }
  Object.defineProperty(JsonNull.prototype, 'jsonNull', {
    get: function () {
      return this.jsonNull_c8yjib$_0;
    }
  });
  Object.defineProperty(JsonNull.prototype, 'content', {
    get: function () {
      return this.content_w1vkof$_0;
    }
  });
  Object.defineProperty(JsonNull.prototype, 'contentOrNull', {
    get: function () {
      return this.contentOrNull_egvcud$_0;
    }
  });
  JsonNull.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonNull',
    interfaces: [JsonPrimitive]
  };
  var JsonNull_instance = null;
  function JsonNull_getInstance() {
    if (JsonNull_instance === null) {
      new JsonNull();
    }return JsonNull_instance;
  }
  function JsonObject(content) {
    JsonObject$Companion_getInstance();
    JsonElement.call(this);
    this.content = content;
    this.jsonObject_js4yrn$_0 = this;
  }
  Object.defineProperty(JsonObject.prototype, 'jsonObject', {
    get: function () {
      return this.jsonObject_js4yrn$_0;
    }
  });
  JsonObject.prototype.getPrimitive_61zpoe$ = function (key) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = getValue(this, key), JsonPrimitive) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson(key, 'JsonPrimitive');
  };
  JsonObject.prototype.getObject_61zpoe$ = function (key) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = getValue(this, key), JsonObject) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson(key, 'JsonObject');
  };
  JsonObject.prototype.getArray_61zpoe$ = function (key) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = getValue(this, key), JsonArray) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson(key, 'JsonArray');
  };
  JsonObject.prototype.getPrimitiveOrNull_61zpoe$ = function (key) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.content.get_11rb$(key), JsonPrimitive) ? tmp$ : null;
  };
  JsonObject.prototype.getObjectOrNull_61zpoe$ = function (key) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.content.get_11rb$(key), JsonObject) ? tmp$ : null;
  };
  JsonObject.prototype.getArrayOrNull_61zpoe$ = function (key) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.content.get_11rb$(key), JsonArray) ? tmp$ : null;
  };
  JsonObject.prototype.getAs_j069p3$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.JsonObject.getAs_j069p3$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var unexpectedJson = _.kotlinx.serialization.json.unexpectedJson_puj7f4$;
    return function (J_0, isJ, key) {
      var tmp$, tmp$_0;
      return (tmp$_0 = isJ(tmp$ = this.get_11rb$(key)) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson(key, getKClass(J_0).toString());
    };
  }));
  JsonObject.prototype.getAsOrNull_j069p3$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.JsonObject.getAsOrNull_j069p3$', function (J_0, isJ, key) {
    var tmp$;
    return isJ(tmp$ = this.content.get_11rb$(key)) ? tmp$ : null;
  });
  function JsonObject$toString$lambda(f) {
    var k = f.key;
    var v = f.value;
    return '"' + k + '"' + ':' + v;
  }
  JsonObject.prototype.toString = function () {
    return joinToString(this.content.entries, ',', '{', '}', void 0, void 0, JsonObject$toString$lambda);
  };
  JsonObject.prototype.equals = function (other) {
    return equals(this.content, other);
  };
  JsonObject.prototype.hashCode = function () {
    return hashCode(this.content);
  };
  function JsonObject$Companion() {
    JsonObject$Companion_instance = this;
  }
  JsonObject$Companion.prototype.serializer = function () {
    return JsonObjectSerializer_getInstance();
  };
  JsonObject$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsonObject$Companion_instance = null;
  function JsonObject$Companion_getInstance() {
    if (JsonObject$Companion_instance === null) {
      new JsonObject$Companion();
    }return JsonObject$Companion_instance;
  }
  Object.defineProperty(JsonObject.prototype, 'entries', {
    get: function () {
      return this.content.entries;
    }
  });
  Object.defineProperty(JsonObject.prototype, 'keys', {
    get: function () {
      return this.content.keys;
    }
  });
  Object.defineProperty(JsonObject.prototype, 'size', {
    get: function () {
      return this.content.size;
    }
  });
  Object.defineProperty(JsonObject.prototype, 'values', {
    get: function () {
      return this.content.values;
    }
  });
  JsonObject.prototype.containsKey_11rb$ = function (key) {
    return this.content.containsKey_11rb$(key);
  };
  JsonObject.prototype.containsValue_11rc$ = function (value) {
    return this.content.containsValue_11rc$(value);
  };
  JsonObject.prototype.get_11rb$ = function (key) {
    return this.content.get_11rb$(key);
  };
  JsonObject.prototype.isEmpty = function () {
    return this.content.isEmpty();
  };
  JsonObject.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonObject',
    interfaces: [Map, JsonElement]
  };
  JsonObject.prototype.component1 = function () {
    return this.content;
  };
  JsonObject.prototype.copy_fnd918$ = function (content) {
    return new JsonObject(content === void 0 ? this.content : content);
  };
  function JsonArray(content) {
    JsonArray$Companion_getInstance();
    JsonElement.call(this);
    this.content = content;
    this.jsonArray_u1gsrt$_0 = this;
  }
  Object.defineProperty(JsonArray.prototype, 'jsonArray', {
    get: function () {
      return this.jsonArray_u1gsrt$_0;
    }
  });
  JsonArray.prototype.getPrimitive_za3lpa$ = function (index) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = this.content.get_za3lpa$(index), JsonPrimitive) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson('at ' + index, 'JsonPrimitive');
  };
  JsonArray.prototype.getObject_za3lpa$ = function (index) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = this.content.get_za3lpa$(index), JsonObject) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson('at ' + index, 'JsonObject');
  };
  JsonArray.prototype.getArray_za3lpa$ = function (index) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = this.content.get_za3lpa$(index), JsonArray) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson('at ' + index, 'JsonArray');
  };
  JsonArray.prototype.getPrimitiveOrNull_za3lpa$ = function (index) {
    var tmp$;
    return Kotlin.isType(tmp$ = getOrNull(this.content, index), JsonPrimitive) ? tmp$ : null;
  };
  JsonArray.prototype.getObjectOrNull_za3lpa$ = function (index) {
    var tmp$;
    return Kotlin.isType(tmp$ = getOrNull(this.content, index), JsonObject) ? tmp$ : null;
  };
  JsonArray.prototype.getArrayOrNull_za3lpa$ = function (index) {
    var tmp$;
    return Kotlin.isType(tmp$ = getOrNull(this.content, index), JsonArray) ? tmp$ : null;
  };
  JsonArray.prototype.getAs_n86q5$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.JsonArray.getAs_n86q5$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var unexpectedJson = _.kotlinx.serialization.json.unexpectedJson_puj7f4$;
    return function (J_0, isJ, index) {
      var tmp$, tmp$_0;
      return (tmp$_0 = isJ(tmp$ = this.content.get_za3lpa$(index)) ? tmp$ : null) != null ? tmp$_0 : unexpectedJson('at ' + index, getKClass(J_0).toString());
    };
  }));
  JsonArray.prototype.getAsOrNull_n86q5$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.JsonArray.getAsOrNull_n86q5$', wrapFunction(function () {
    var getOrNull = Kotlin.kotlin.collections.getOrNull_yzln2o$;
    return function (J_0, isJ, index) {
      var tmp$;
      return isJ(tmp$ = getOrNull(this.content, index)) ? tmp$ : null;
    };
  }));
  JsonArray.prototype.toString = function () {
    return joinToString(this.content, ',', '[', ']');
  };
  JsonArray.prototype.equals = function (other) {
    return equals(this.content, other);
  };
  JsonArray.prototype.hashCode = function () {
    return hashCode(this.content);
  };
  function JsonArray$Companion() {
    JsonArray$Companion_instance = this;
  }
  JsonArray$Companion.prototype.serializer = function () {
    return JsonArraySerializer_getInstance();
  };
  JsonArray$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsonArray$Companion_instance = null;
  function JsonArray$Companion_getInstance() {
    if (JsonArray$Companion_instance === null) {
      new JsonArray$Companion();
    }return JsonArray$Companion_instance;
  }
  Object.defineProperty(JsonArray.prototype, 'size', {
    get: function () {
      return this.content.size;
    }
  });
  JsonArray.prototype.contains_11rb$ = function (element) {
    return this.content.contains_11rb$(element);
  };
  JsonArray.prototype.containsAll_brywnq$ = function (elements) {
    return this.content.containsAll_brywnq$(elements);
  };
  JsonArray.prototype.get_za3lpa$ = function (index) {
    return this.content.get_za3lpa$(index);
  };
  JsonArray.prototype.indexOf_11rb$ = function (element) {
    return this.content.indexOf_11rb$(element);
  };
  JsonArray.prototype.isEmpty = function () {
    return this.content.isEmpty();
  };
  JsonArray.prototype.iterator = function () {
    return this.content.iterator();
  };
  JsonArray.prototype.lastIndexOf_11rb$ = function (element) {
    return this.content.lastIndexOf_11rb$(element);
  };
  JsonArray.prototype.listIterator = function () {
    return this.content.listIterator();
  };
  JsonArray.prototype.listIterator_za3lpa$ = function (index) {
    return this.content.listIterator_za3lpa$(index);
  };
  JsonArray.prototype.subList_vux9f0$ = function (fromIndex, toIndex) {
    return this.content.subList_vux9f0$(fromIndex, toIndex);
  };
  JsonArray.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonArray',
    interfaces: [List, JsonElement]
  };
  JsonArray.prototype.component1 = function () {
    return this.content;
  };
  JsonArray.prototype.copy_adp4jc$ = function (content) {
    return new JsonArray(content === void 0 ? this.content : content);
  };
  function unexpectedJson(key, expected) {
    throw new JsonException('Element ' + key + ' is not a ' + expected);
  }
  function json(init) {
    var builder = new JsonObjectBuilder();
    init(builder);
    return new JsonObject(builder.content_8be2vx$);
  }
  function jsonArray(init) {
    var builder = new JsonArrayBuilder();
    init(builder);
    return new JsonArray(builder.content_8be2vx$);
  }
  function JsonArrayBuilder() {
    this.content_8be2vx$ = ArrayList_init_0();
  }
  JsonArrayBuilder.prototype.unaryPlus_5cw0du$ = function ($receiver) {
    this.content_8be2vx$.add_11rb$(JsonPrimitive_2($receiver));
  };
  JsonArrayBuilder.prototype.unaryPlus_4sdtmu$ = function ($receiver) {
    this.content_8be2vx$.add_11rb$(JsonPrimitive_1($receiver));
  };
  JsonArrayBuilder.prototype.unaryPlus_d4wkrv$ = function ($receiver) {
    this.content_8be2vx$.add_11rb$(JsonPrimitive_0($receiver));
  };
  JsonArrayBuilder.prototype.unaryPlus_u3sd3g$ = function ($receiver) {
    this.content_8be2vx$.add_11rb$($receiver);
  };
  JsonArrayBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonArrayBuilder',
    interfaces: []
  };
  function JsonObjectBuilder() {
    this.content_8be2vx$ = LinkedHashMap_init();
  }
  JsonObjectBuilder.prototype.to_ahl3kc$ = function ($receiver, value) {
    if (!(this.content_8be2vx$.get_11rb$($receiver) == null)) {
      var message = 'Key ' + $receiver + ' is already registered in builder';
      throw IllegalArgumentException_init(message.toString());
    }this.content_8be2vx$.put_xwzc9p$($receiver, value);
  };
  JsonObjectBuilder.prototype.to_lr5kl6$ = function ($receiver, value) {
    if (!(this.content_8be2vx$.get_11rb$($receiver) == null)) {
      var message = 'Key ' + $receiver + ' is already registered in builder';
      throw IllegalArgumentException_init(message.toString());
    }var $receiver_0 = this.content_8be2vx$;
    var value_0 = JsonPrimitive_1(value);
    $receiver_0.put_xwzc9p$($receiver, value_0);
  };
  JsonObjectBuilder.prototype.to_sg61ir$ = function ($receiver, value) {
    if (!(this.content_8be2vx$.get_11rb$($receiver) == null)) {
      var message = 'Key ' + $receiver + ' is already registered in builder';
      throw IllegalArgumentException_init(message.toString());
    }var $receiver_0 = this.content_8be2vx$;
    var value_0 = JsonPrimitive_0(value);
    $receiver_0.put_xwzc9p$($receiver, value_0);
  };
  JsonObjectBuilder.prototype.to_npuxma$ = function ($receiver, value) {
    if (!(this.content_8be2vx$.get_11rb$($receiver) == null)) {
      var message = 'Key ' + $receiver + ' is already registered in builder';
      throw IllegalArgumentException_init(message.toString());
    }var $receiver_0 = this.content_8be2vx$;
    var value_0 = JsonPrimitive_2(value);
    $receiver_0.put_xwzc9p$($receiver, value_0);
  };
  JsonObjectBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonObjectBuilder',
    interfaces: []
  };
  function JsonElementSerializer() {
    JsonElementSerializer_instance = this;
    this.descriptor_u8kpse$_0 = SerialDescriptor_0('kotlinx.serialization.json.JsonElement', PolymorphicKind$SEALED_getInstance(), JsonElementSerializer$descriptor$lambda);
  }
  Object.defineProperty(JsonElementSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_u8kpse$_0;
    }
  });
  JsonElementSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    verify(encoder);
    if (Kotlin.isType(value, JsonPrimitive))
      encoder.encodeSerializableValue_tf03ej$(JsonPrimitiveSerializer_getInstance(), value);
    else if (Kotlin.isType(value, JsonObject))
      encoder.encodeSerializableValue_tf03ej$(JsonObjectSerializer_getInstance(), value);
    else if (Kotlin.isType(value, JsonArray))
      encoder.encodeSerializableValue_tf03ej$(JsonArraySerializer_getInstance(), value);
  };
  JsonElementSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var input = asJsonInput(decoder);
    return input.decodeJson();
  };
  function JsonElementSerializer$descriptor$lambda$lambda() {
    return JsonPrimitiveSerializer_getInstance().descriptor;
  }
  function JsonElementSerializer$descriptor$lambda$lambda_0() {
    return JsonNullSerializer_getInstance().descriptor;
  }
  function JsonElementSerializer$descriptor$lambda$lambda_1() {
    return JsonLiteralSerializer_getInstance().descriptor;
  }
  function JsonElementSerializer$descriptor$lambda$lambda_2() {
    return JsonObjectSerializer_getInstance().descriptor;
  }
  function JsonElementSerializer$descriptor$lambda$lambda_3() {
    return JsonArraySerializer_getInstance().descriptor;
  }
  function JsonElementSerializer$descriptor$lambda($receiver) {
    $receiver.element_re18qg$('JsonPrimitive', defer(JsonElementSerializer$descriptor$lambda$lambda));
    $receiver.element_re18qg$('JsonNull', defer(JsonElementSerializer$descriptor$lambda$lambda_0));
    $receiver.element_re18qg$('JsonLiteral', defer(JsonElementSerializer$descriptor$lambda$lambda_1));
    $receiver.element_re18qg$('JsonObject', defer(JsonElementSerializer$descriptor$lambda$lambda_2));
    $receiver.element_re18qg$('JsonArray', defer(JsonElementSerializer$descriptor$lambda$lambda_3));
    return Unit;
  }
  JsonElementSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonElementSerializer',
    interfaces: [KSerializer]
  };
  var JsonElementSerializer_instance = null;
  function JsonElementSerializer_getInstance() {
    if (JsonElementSerializer_instance === null) {
      new JsonElementSerializer();
    }return JsonElementSerializer_instance;
  }
  function JsonPrimitiveSerializer() {
    JsonPrimitiveSerializer_instance = this;
    this.descriptor_1d7xi5$_0 = SerialDescriptor_0('kotlinx.serialization.json.JsonPrimitive', PrimitiveKind$STRING_getInstance());
  }
  Object.defineProperty(JsonPrimitiveSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_1d7xi5$_0;
    }
  });
  JsonPrimitiveSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var tmp$, tmp$_0;
    verify(encoder);
    if (Kotlin.isType(value, JsonNull)) {
      tmp$_0 = encoder.encodeSerializableValue_tf03ej$(JsonNullSerializer_getInstance(), JsonNull_getInstance());
    } else {
      tmp$_0 = encoder.encodeSerializableValue_tf03ej$(JsonLiteralSerializer_getInstance(), Kotlin.isType(tmp$ = value, JsonLiteral) ? tmp$ : throwCCE());
    }
    return tmp$_0;
  };
  JsonPrimitiveSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var result = asJsonInput(decoder).decodeJson();
    if (!Kotlin.isType(result, JsonPrimitive))
      throw JsonDecodingException_0(-1, 'Unexpected JSON element, expected JsonPrimitive, had ' + Kotlin.getKClassFromExpression(result), result.toString());
    return result;
  };
  JsonPrimitiveSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonPrimitiveSerializer',
    interfaces: [KSerializer]
  };
  var JsonPrimitiveSerializer_instance = null;
  function JsonPrimitiveSerializer_getInstance() {
    if (JsonPrimitiveSerializer_instance === null) {
      new JsonPrimitiveSerializer();
    }return JsonPrimitiveSerializer_instance;
  }
  function JsonNullSerializer() {
    JsonNullSerializer_instance = this;
    this.descriptor_kuqqdr$_0 = SerialDescriptor_0('kotlinx.serialization.json.JsonNull', UnionKind$ENUM_KIND_getInstance());
  }
  Object.defineProperty(JsonNullSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_kuqqdr$_0;
    }
  });
  JsonNullSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    verify(encoder);
    encoder.encodeNull();
  };
  JsonNullSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    verify_0(decoder);
    decoder.decodeNull();
    return JsonNull_getInstance();
  };
  JsonNullSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonNullSerializer',
    interfaces: [KSerializer]
  };
  var JsonNullSerializer_instance = null;
  function JsonNullSerializer_getInstance() {
    if (JsonNullSerializer_instance === null) {
      new JsonNullSerializer();
    }return JsonNullSerializer_instance;
  }
  function JsonLiteralSerializer() {
    JsonLiteralSerializer_instance = this;
    this.descriptor_fnzu3f$_0 = PrimitiveDescriptor('kotlinx.serialization.json.JsonLiteral', PrimitiveKind$STRING_getInstance());
  }
  Object.defineProperty(JsonLiteralSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_fnzu3f$_0;
    }
  });
  JsonLiteralSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    verify(encoder);
    if (value.isString) {
      return encoder.encodeString_61zpoe$(value.content);
    }var long = value.longOrNull;
    if (long != null) {
      return encoder.encodeLong_s8cxhz$(long);
    }var double = value.doubleOrNull;
    if (double != null) {
      return encoder.encodeDouble_14dthe$(double);
    }var boolean = value.booleanOrNull;
    if (boolean != null) {
      return encoder.encodeBoolean_6taknv$(boolean);
    }encoder.encodeString_61zpoe$(value.content);
  };
  JsonLiteralSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var result = asJsonInput(decoder).decodeJson();
    if (!Kotlin.isType(result, JsonLiteral))
      throw JsonDecodingException_0(-1, 'Unexpected JSON element, expected JsonLiteral, had ' + Kotlin.getKClassFromExpression(result), result.toString());
    return result;
  };
  JsonLiteralSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonLiteralSerializer',
    interfaces: [KSerializer]
  };
  var JsonLiteralSerializer_instance = null;
  function JsonLiteralSerializer_getInstance() {
    if (JsonLiteralSerializer_instance === null) {
      new JsonLiteralSerializer();
    }return JsonLiteralSerializer_instance;
  }
  function JsonObjectSerializer() {
    JsonObjectSerializer_instance = this;
    this.descriptor_a992tj$_0 = new NamedMapClassDescriptor('kotlinx.serialization.json.JsonObject', serializer_10(kotlin_js_internal_StringCompanionObject).descriptor, JsonElementSerializer_getInstance().descriptor);
  }
  Object.defineProperty(JsonObjectSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_a992tj$_0;
    }
  });
  JsonObjectSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    verify(encoder);
    MapSerializer(serializer_10(kotlin_js_internal_StringCompanionObject), JsonElementSerializer_getInstance()).serialize_awe97i$(encoder, value.content);
  };
  JsonObjectSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    verify_0(decoder);
    return new JsonObject(MapSerializer(serializer_10(kotlin_js_internal_StringCompanionObject), JsonElementSerializer_getInstance()).deserialize_nts5qn$(decoder));
  };
  JsonObjectSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonObjectSerializer',
    interfaces: [KSerializer]
  };
  var JsonObjectSerializer_instance = null;
  function JsonObjectSerializer_getInstance() {
    if (JsonObjectSerializer_instance === null) {
      new JsonObjectSerializer();
    }return JsonObjectSerializer_instance;
  }
  function JsonArraySerializer() {
    JsonArraySerializer_instance = this;
    this.descriptor_935ivj$_0 = new NamedListClassDescriptor('kotlinx.serialization.json.JsonArray', JsonElementSerializer_getInstance().descriptor);
  }
  Object.defineProperty(JsonArraySerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_935ivj$_0;
    }
  });
  JsonArraySerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    verify(encoder);
    ListSerializer(JsonElementSerializer_getInstance()).serialize_awe97i$(encoder, value);
  };
  JsonArraySerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    verify_0(decoder);
    return new JsonArray(ListSerializer(JsonElementSerializer_getInstance()).deserialize_nts5qn$(decoder));
  };
  JsonArraySerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'JsonArraySerializer',
    interfaces: [KSerializer]
  };
  var JsonArraySerializer_instance = null;
  function JsonArraySerializer_getInstance() {
    if (JsonArraySerializer_instance === null) {
      new JsonArraySerializer();
    }return JsonArraySerializer_instance;
  }
  function verify(encoder) {
    asJsonOutput(encoder);
  }
  function verify_0(decoder) {
    asJsonInput(decoder);
  }
  function asJsonInput($receiver) {
    var tmp$, tmp$_0;
    tmp$_0 = Kotlin.isType(tmp$ = $receiver, JsonInput) ? tmp$ : null;
    if (tmp$_0 == null) {
      throw IllegalStateException_init('This serializer can be used only with Json format.' + ('Expected Decoder to be JsonInput, got ' + Kotlin.getKClassFromExpression($receiver)));
    }return tmp$_0;
  }
  function asJsonOutput($receiver) {
    var tmp$, tmp$_0;
    tmp$_0 = Kotlin.isType(tmp$ = $receiver, JsonOutput) ? tmp$ : null;
    if (tmp$_0 == null) {
      throw IllegalStateException_init('This serializer can be used only with Json format.' + ('Expected Encoder to be JsonOutput, got ' + Kotlin.getKClassFromExpression($receiver)));
    }return tmp$_0;
  }
  function JsonPrimitive_0(value) {
    if (value == null)
      return JsonNull_getInstance();
    return JsonLiteral_init_0(value);
  }
  function JsonPrimitive_1(value) {
    if (value == null)
      return JsonNull_getInstance();
    return JsonLiteral_init(value);
  }
  function JsonPrimitive_2(value) {
    if (value == null)
      return JsonNull_getInstance();
    return JsonLiteral_init_1(value);
  }
  function get_int($receiver) {
    return $receiver.primitive.int;
  }
  function get_intOrNull($receiver) {
    return $receiver.primitive.intOrNull;
  }
  function get_long($receiver) {
    return $receiver.primitive.long;
  }
  function get_longOrNull($receiver) {
    return $receiver.primitive.longOrNull;
  }
  function get_double($receiver) {
    return $receiver.primitive.double;
  }
  function get_doubleOrNull($receiver) {
    return $receiver.primitive.doubleOrNull;
  }
  function get_float($receiver) {
    return $receiver.primitive.float;
  }
  function get_floatOrNull($receiver) {
    return $receiver.primitive.floatOrNull;
  }
  function get_boolean($receiver) {
    return $receiver.primitive.boolean;
  }
  function get_booleanOrNull($receiver) {
    return $receiver.primitive.booleanOrNull;
  }
  function get_content($receiver) {
    return $receiver.primitive.content;
  }
  function get_contentOrNull($receiver) {
    return $receiver.primitive.contentOrNull;
  }
  function JsonException(message) {
    SerializationException.call(this, message);
    this.name = 'JsonException';
  }
  JsonException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonException',
    interfaces: [SerializationException]
  };
  function JsonDecodingException(offset, message) {
    JsonException.call(this, 'Unexpected JSON token at offset ' + offset + ': ' + message);
    this.name = 'JsonDecodingException';
  }
  JsonDecodingException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonDecodingException',
    interfaces: [JsonException]
  };
  function JsonEncodingException(message) {
    JsonException.call(this, message);
    this.name = 'JsonEncodingException';
  }
  JsonEncodingException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonEncodingException',
    interfaces: [JsonException]
  };
  function JsonDecodingException_0(offset, message, input) {
    return new JsonDecodingException(offset, message + '.' + '\n' + ' JSON input: ' + minify(input, offset));
  }
  function InvalidFloatingPoint(value, type, output) {
    return new JsonEncodingException("'" + value.toString() + "' is not a valid '" + type + "' as per JSON specification. " + "You can enable 'serializeSpecialFloatingPointValues' property to serialize such values\n" + ('Current output: ' + minify(output)));
  }
  function InvalidFloatingPoint_0(value, key, type, output) {
    return new JsonEncodingException("'" + value.toString() + "' with key '" + key + "' is not a valid " + type + ' as per JSON specification. ' + "You can enable 'serializeSpecialFloatingPointValues' property to serialize such values.\n" + ('Current output: ' + minify(output)));
  }
  function UnknownKeyException(key, input) {
    return new JsonDecodingException(-1, "JSON encountered unknown key: '" + key + "'. You can enable 'JsonConfiguration.ignoreUnknownKeys' property to ignore unknown keys." + '\n' + (' JSON input: ' + minify(input)));
  }
  function InvalidKeyKindException(keyDescriptor) {
    return new JsonEncodingException("Value of type '" + keyDescriptor.serialName + "' can't be used in JSON as a key in the map. " + ("It should have either primitive or enum kind, but its kind is '" + keyDescriptor.kind + ".'" + '\n') + "You can convert such maps to arrays [key1, value1, key2, value2,...] using 'allowStructuredMapKeys' property in JsonConfiguration");
  }
  function minify($receiver, offset) {
    if (offset === void 0)
      offset = -1;
    if ($receiver.length < 200)
      return $receiver;
    if (offset === -1) {
      var start = $receiver.length - 60 | 0;
      if (start <= 0)
        return $receiver;
      return '.....' + $receiver.substring(start);
    }var start_0 = offset - 30 | 0;
    var end = offset + 30 | 0;
    var prefix = start_0 <= 0 ? '' : '.....';
    var suffix = end >= $receiver.length ? '' : '.....';
    var startIndex = coerceAtLeast(start_0, 0);
    var endIndex = coerceAtMost(end, $receiver.length);
    return prefix + $receiver.substring(startIndex, endIndex) + suffix;
  }
  function JsonInput() {
  }
  JsonInput.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'JsonInput',
    interfaces: [CompositeDecoder, Decoder]
  };
  function JsonOutput() {
  }
  JsonOutput.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'JsonOutput',
    interfaces: [CompositeEncoder, Encoder]
  };
  function JsonParametricSerializer(baseClass) {
    this.baseClass_yllz6u$_0 = baseClass;
    this.descriptor_2iulbc$_0 = SerialDescriptor_0('JsonParametricSerializer<' + toString(simpleName_0(this.baseClass_yllz6u$_0)) + '>', PolymorphicKind$OPEN_getInstance());
  }
  Object.defineProperty(JsonParametricSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_2iulbc$_0;
    }
  });
  JsonParametricSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var tmp$, tmp$_0, tmp$_1;
    var actualSerializer = (tmp$_0 = (tmp$ = encoder.context.getPolymorphic_b1ce0a$(this.baseClass_yllz6u$_0, value)) != null ? tmp$ : serializerOrNull(Kotlin.getKClassFromExpression(value))) != null ? tmp$_0 : throwSubtypeNotRegistered_0(Kotlin.getKClassFromExpression(value), this.baseClass_yllz6u$_0);
    (Kotlin.isType(tmp$_1 = actualSerializer, KSerializer) ? tmp$_1 : throwCCE()).serialize_awe97i$(encoder, value);
  };
  JsonParametricSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var tmp$;
    var input = asJsonInput(decoder);
    var tree = input.decodeJson();
    var actualSerializer = Kotlin.isType(tmp$ = this.selectSerializer_qiw0cd$(tree), KSerializer) ? tmp$ : throwCCE();
    return input.json.fromJson_htt2tq$(actualSerializer, tree);
  };
  JsonParametricSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonParametricSerializer',
    interfaces: [KSerializer]
  };
  function JsonTransformingSerializer(tSerializer, transformationName) {
    this.tSerializer_nuzucf$_0 = tSerializer;
    this.descriptor_ej45oi$_0 = SerialDescriptor_0('JsonTransformingSerializer<' + this.tSerializer_nuzucf$_0.descriptor.serialName + '>(' + transformationName + ')', this.tSerializer_nuzucf$_0.descriptor.kind);
  }
  Object.defineProperty(JsonTransformingSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_ej45oi$_0;
    }
  });
  JsonTransformingSerializer.prototype.serialize_awe97i$ = function (encoder, value) {
    var output = asJsonOutput(encoder);
    var element = writeJson(output.json, value, this.tSerializer_nuzucf$_0);
    element = this.writeTransform_qiw0cd$(element);
    output.encodeJson_qiw0cd$(element);
  };
  JsonTransformingSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var input = asJsonInput(decoder);
    var element = input.decodeJson();
    element = this.readTransform_qiw0cd$(element);
    return input.json.fromJson_htt2tq$(this.tSerializer_nuzucf$_0, element);
  };
  JsonTransformingSerializer.prototype.readTransform_qiw0cd$ = function (element) {
    return element;
  };
  JsonTransformingSerializer.prototype.writeTransform_qiw0cd$ = function (element) {
    return element;
  };
  JsonTransformingSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTransformingSerializer',
    interfaces: [KSerializer]
  };
  function ContextValidator(discriminator) {
    this.discriminator_0 = discriminator;
  }
  ContextValidator.prototype.contextual_cfhkba$ = function (kClass, serializer) {
  };
  ContextValidator.prototype.polymorphic_kfyidi$ = function (baseClass, actualClass, actualSerializer) {
    var tmp$;
    var descriptor = actualSerializer.descriptor;
    tmp$ = descriptor.elementsCount;
    for (var i = 0; i < tmp$; i++) {
      var name = descriptor.getElementName_za3lpa$(i);
      if (equals(name, this.discriminator_0)) {
        throw IllegalArgumentException_init('Polymorphic serializer for ' + actualClass + " has property '" + name + "' that conflicts " + 'with JSON class discriminator. You can either change class discriminator in JsonConfiguration, ' + 'rename property with @SerialName annotation ' + 'or fall back to array polymorphism');
      }}
  };
  ContextValidator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ContextValidator',
    interfaces: [SerialModuleCollector]
  };
  function JsonParser(configuration, reader) {
    this.reader_0 = reader;
    this.isLenient_0 = configuration.isLenient_8be2vx$;
  }
  JsonParser.prototype.readObject_0 = function () {
    var $this = this.reader_0;
    if ($this.tokenClass !== TC_BEGIN_OBJ) {
      toBoxedChar(toChar($this.tokenClass));
      $this.fail_bm4lxs$('Expected start of the object', $this.tokenPosition_0);
    }this.reader_0.nextToken();
    var $this_0 = this.reader_0;
    var condition = this.reader_0.tokenClass !== TC_COMMA;
    var position = this.reader_0.currentPosition;
    if (!condition) {
      $this_0.fail_bm4lxs$('Unexpected leading comma', position);
    }var result = LinkedHashMap_init();
    var valueExpected = false;
    while (this.reader_0.canBeginValue) {
      valueExpected = false;
      var key = this.isLenient_0 ? this.reader_0.takeString() : this.reader_0.takeStringQuoted();
      var $this_1 = this.reader_0;
      if ($this_1.tokenClass !== TC_COLON) {
        toBoxedChar(toChar($this_1.tokenClass));
        $this_1.fail_bm4lxs$("Expected ':'", $this_1.tokenPosition_0);
      }this.reader_0.nextToken();
      var element = this.read();
      result.put_xwzc9p$(key, element);
      if (this.reader_0.tokenClass !== TC_COMMA) {
        var $this_2 = this.reader_0;
        if ($this_2.tokenClass !== TC_END_OBJ) {
          toBoxedChar(toChar($this_2.tokenClass));
          $this_2.fail_bm4lxs$('Expected end of the object or comma', $this_2.tokenPosition_0);
        }} else {
        valueExpected = true;
        this.reader_0.nextToken();
      }
    }
    var $this_3 = this.reader_0;
    var condition_0 = !valueExpected && this.reader_0.tokenClass === TC_END_OBJ;
    var position_0 = this.reader_0.currentPosition;
    if (!condition_0) {
      $this_3.fail_bm4lxs$('Expected end of the object', position_0);
    }this.reader_0.nextToken();
    return new JsonObject(result);
  };
  JsonParser.prototype.readArray_0 = function () {
    var $this = this.reader_0;
    if ($this.tokenClass !== TC_BEGIN_LIST) {
      toBoxedChar(toChar($this.tokenClass));
      $this.fail_bm4lxs$('Expected start of the array', $this.tokenPosition_0);
    }this.reader_0.nextToken();
    var $this_0 = this.reader_0;
    var condition = this.reader_0.tokenClass !== TC_COMMA;
    var position = this.reader_0.currentPosition;
    if (!condition) {
      $this_0.fail_bm4lxs$('Unexpected leading comma', position);
    }var result = ArrayList_init_0();
    var valueExpected = false;
    while (this.reader_0.canBeginValue) {
      valueExpected = false;
      var element = this.read();
      result.add_11rb$(element);
      if (this.reader_0.tokenClass !== TC_COMMA) {
        var $this_1 = this.reader_0;
        if ($this_1.tokenClass !== TC_END_LIST) {
          toBoxedChar(toChar($this_1.tokenClass));
          $this_1.fail_bm4lxs$('Expected end of the array or comma', $this_1.tokenPosition_0);
        }} else {
        valueExpected = true;
        this.reader_0.nextToken();
      }
    }
    var $this_2 = this.reader_0;
    var condition_0 = !valueExpected;
    var position_0 = this.reader_0.currentPosition;
    if (!condition_0) {
      $this_2.fail_bm4lxs$('Unexpected trailing comma', position_0);
    }this.reader_0.nextToken();
    return new JsonArray(result);
  };
  JsonParser.prototype.readValue_0 = function (isString) {
    var tmp$;
    if (this.isLenient_0) {
      tmp$ = this.reader_0.takeString();
    } else {
      tmp$ = isString ? this.reader_0.takeStringQuoted() : this.reader_0.takeString();
    }
    var str = tmp$;
    return new JsonLiteral(str, isString);
  };
  JsonParser.prototype.read = function () {
    var tmp$;
    if (!this.reader_0.canBeginValue)
      this.reader_0.fail_bm4lxs$("Can't begin reading value from here");
    switch (this.reader_0.tokenClass) {
      case 10:
        var $receiver = JsonNull_getInstance();
        this.reader_0.nextToken();
        tmp$ = $receiver;
        break;
      case 1:
        tmp$ = this.readValue_0(true);
        break;
      case 0:
        tmp$ = this.readValue_0(false);
        break;
      case 6:
        tmp$ = this.readObject_0();
        break;
      case 8:
        tmp$ = this.readArray_0();
        break;
      default:tmp$ = this.reader_0.fail_bm4lxs$("Can't begin reading element, unexpected token");
        break;
    }
    return tmp$;
  };
  JsonParser.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonParser',
    interfaces: []
  };
  var NULL_0;
  var COMMA;
  var COLON;
  var BEGIN_OBJ;
  var END_OBJ;
  var BEGIN_LIST;
  var END_LIST;
  var STRING;
  var STRING_ESC;
  var INVALID;
  var UNICODE_ESC;
  var TC_OTHER;
  var TC_STRING;
  var TC_STRING_ESC;
  var TC_WS;
  var TC_COMMA;
  var TC_COLON;
  var TC_BEGIN_OBJ;
  var TC_END_OBJ;
  var TC_BEGIN_LIST;
  var TC_END_LIST;
  var TC_NULL;
  var TC_INVALID;
  var TC_EOF;
  var CTC_MAX;
  var ESC2C_MAX;
  var C2TC;
  function EscapeCharMappings() {
    EscapeCharMappings_instance = this;
    this.ESCAPE_2_CHAR = Kotlin.charArray(117);
    for (var i = 0; i <= 31; i++) {
      this.initC2ESC_0(i, UNICODE_ESC);
    }
    this.initC2ESC_0(8, 98);
    this.initC2ESC_0(9, 116);
    this.initC2ESC_0(10, 110);
    this.initC2ESC_0(12, 102);
    this.initC2ESC_0(13, 114);
    this.initC2ESC_1(47, 47);
    this.initC2ESC_1(STRING, STRING);
    this.initC2ESC_1(STRING_ESC, STRING_ESC);
  }
  EscapeCharMappings.prototype.initC2ESC_0 = function (c, esc) {
    if (esc !== UNICODE_ESC)
      this.ESCAPE_2_CHAR[esc | 0] = toChar(c);
  };
  EscapeCharMappings.prototype.initC2ESC_1 = function (c, esc) {
    this.initC2ESC_0(c | 0, esc);
  };
  EscapeCharMappings.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'EscapeCharMappings',
    interfaces: []
  };
  var EscapeCharMappings_instance = null;
  function EscapeCharMappings_getInstance() {
    if (EscapeCharMappings_instance === null) {
      new EscapeCharMappings();
    }return EscapeCharMappings_instance;
  }
  function initC2TC($receiver, c, cl) {
    $receiver[c] = cl;
  }
  function initC2TC_0($receiver, c, cl) {
    initC2TC($receiver, c | 0, cl);
  }
  function charToTokenClass(c) {
    return (c | 0) < 126 ? C2TC[c | 0] : TC_OTHER;
  }
  function escapeToChar(c) {
    return unboxChar(c < 117 ? EscapeCharMappings_getInstance().ESCAPE_2_CHAR[c] : INVALID);
  }
  function JsonReader(source) {
    this.source_0 = source;
    this.currentPosition = 0;
    this.tokenClass = TC_EOF;
    this.tokenPosition_0 = 0;
    this.offset_0 = -1;
    this.length_0 = 0;
    this.buf_0 = Kotlin.charArray(16);
    this.nextToken();
  }
  Object.defineProperty(JsonReader.prototype, 'isDone', {
    get: function () {
      return this.tokenClass === TC_EOF;
    }
  });
  Object.defineProperty(JsonReader.prototype, 'canBeginValue', {
    get: function () {
      switch (this.tokenClass) {
        case 8:
        case 6:
        case 0:
        case 1:
        case 10:
          return true;
        default:return false;
      }
    }
  });
  JsonReader.prototype.requireTokenClass_mvfnf3$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.internal.JsonReader.requireTokenClass_mvfnf3$', wrapFunction(function () {
    var toChar = Kotlin.toChar;
    var toBoxedChar = Kotlin.toBoxedChar;
    return function (expected, errorMessage) {
      if (this.tokenClass !== expected)
        this.fail_bm4lxs$(errorMessage(toBoxedChar(toChar(this.tokenClass))), this.tokenPosition_0);
    };
  }));
  JsonReader.prototype.takeString = function () {
    if (this.tokenClass !== TC_OTHER && this.tokenClass !== TC_STRING)
      this.fail_bm4lxs$('Expected string or non-null literal', this.tokenPosition_0);
    return this.takeStringInternal_0();
  };
  JsonReader.prototype.takeStringQuoted = function () {
    if (this.tokenClass !== TC_STRING)
      this.fail_bm4lxs$("Expected string literal with quotes. Use 'JsonConfiguration.isLenient = true' to accept non-compliant JSON", this.tokenPosition_0);
    return this.takeStringInternal_0();
  };
  JsonReader.prototype.takeBooleanStringUnquoted = function () {
    if (this.tokenClass !== TC_OTHER)
      this.fail_bm4lxs$("Expected start of the unquoted boolean literal. Use 'JsonConfiguration.isLenient = true' to accept non-compliant JSON", this.tokenPosition_0);
    return this.takeStringInternal_0();
  };
  JsonReader.prototype.takeStringInternal_0 = function () {
    var tmp$;
    if (this.offset_0 < 0)
      tmp$ = String_0(this.buf_0, 0, this.length_0);
    else {
      var $receiver = this.source_0;
      var startIndex = this.offset_0;
      var endIndex = this.offset_0 + this.length_0 | 0;
      tmp$ = $receiver.substring(startIndex, endIndex);
    }
    var prevStr = tmp$;
    this.nextToken();
    return prevStr;
  };
  JsonReader.prototype.append_0 = function (ch) {
    var tmp$;
    if (this.length_0 >= this.buf_0.length)
      this.buf_0 = copyOf_5(this.buf_0, 2 * this.buf_0.length | 0);
    this.buf_0[tmp$ = this.length_0, this.length_0 = tmp$ + 1 | 0, tmp$] = ch;
  };
  JsonReader.prototype.appendRange_0 = function (source, fromIndex, toIndex) {
    var addLen = toIndex - fromIndex | 0;
    var oldLen = this.length_0;
    var newLen = oldLen + addLen | 0;
    if (newLen > this.buf_0.length)
      this.buf_0 = copyOf_5(this.buf_0, coerceAtLeast(newLen, 2 * this.buf_0.length | 0));
    for (var i = 0; i < addLen; i++)
      this.buf_0[oldLen + i | 0] = source.charCodeAt(fromIndex + i | 0);
    this.length_0 = this.length_0 + addLen | 0;
  };
  JsonReader.prototype.nextToken = function () {
    var source = this.source_0;
    var currentPosition = this.currentPosition;
    while (currentPosition < source.length) {
      var ch = source.charCodeAt(currentPosition);
      var tc = charToTokenClass(ch);
      switch (tc) {
        case 3:
          currentPosition = currentPosition + 1 | 0;
          break;
        case 0:
          this.nextLiteral_0(source, currentPosition);
          return;
        case 1:
          this.nextString_0(source, currentPosition);
          return;
        default:this.tokenPosition_0 = currentPosition;
          this.tokenClass = tc;
          this.currentPosition = currentPosition + 1 | 0;
          return;
      }
    }
    this.tokenPosition_0 = currentPosition;
    this.tokenClass = TC_EOF;
  };
  JsonReader.prototype.nextLiteral_0 = function (source, startPos) {
    this.tokenPosition_0 = startPos;
    this.offset_0 = startPos;
    var currentPosition = startPos;
    while (currentPosition < source.length && charToTokenClass(source.charCodeAt(currentPosition)) === TC_OTHER) {
      currentPosition = currentPosition + 1 | 0;
    }
    this.currentPosition = currentPosition;
    this.length_0 = currentPosition - this.offset_0 | 0;
    this.tokenClass = rangeEquals(source, this.offset_0, this.length_0, NULL_0) ? TC_NULL : TC_OTHER;
  };
  JsonReader.prototype.nextString_0 = function (source, startPosition) {
    this.tokenPosition_0 = startPosition;
    this.length_0 = 0;
    var currentPosition = startPosition + 1 | 0;
    var lastPosition = currentPosition;
    while (source.charCodeAt(currentPosition) !== STRING) {
      if (source.charCodeAt(currentPosition) === STRING_ESC) {
        this.appendRange_0(source, lastPosition, currentPosition);
        var newPosition = this.appendEsc_0(source, currentPosition + 1 | 0);
        currentPosition = newPosition;
        lastPosition = newPosition;
      } else if ((currentPosition = currentPosition + 1 | 0, currentPosition) >= source.length) {
        this.fail_bm4lxs$('EOF', currentPosition);
      }}
    if (lastPosition === (startPosition + 1 | 0)) {
      this.offset_0 = lastPosition;
      this.length_0 = currentPosition - lastPosition | 0;
    } else {
      this.appendRange_0(source, lastPosition, currentPosition);
      this.offset_0 = -1;
    }
    this.currentPosition = currentPosition + 1 | 0;
    this.tokenClass = TC_STRING;
  };
  JsonReader.prototype.appendEsc_0 = function (source, startPosition) {
    var tmp$;
    var currentPosition = startPosition;
    var condition = currentPosition < source.length;
    var position = currentPosition;
    if (!condition) {
      this.fail_bm4lxs$('Unexpected EOF after escape character', position);
    }var currentChar = source.charCodeAt((tmp$ = currentPosition, currentPosition = tmp$ + 1 | 0, tmp$));
    if (currentChar === UNICODE_ESC) {
      return this.appendHex_0(source, currentPosition);
    }var c = escapeToChar(currentChar | 0);
    var condition_0 = c !== INVALID;
    var position_0 = currentPosition;
    if (!condition_0) {
      this.fail_bm4lxs$("Invalid escaped char '" + String.fromCharCode(currentChar) + "'", position_0);
    }this.append_0(c);
    return currentPosition;
  };
  JsonReader.prototype.appendHex_0 = function (source, startPos) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var curPos = startPos;
    this.append_0(toChar((this.fromHexChar_0(source, (tmp$ = curPos, curPos = tmp$ + 1 | 0, tmp$)) << 12) + (this.fromHexChar_0(source, (tmp$_0 = curPos, curPos = tmp$_0 + 1 | 0, tmp$_0)) << 8) + (this.fromHexChar_0(source, (tmp$_1 = curPos, curPos = tmp$_1 + 1 | 0, tmp$_1)) << 4) + this.fromHexChar_0(source, (tmp$_2 = curPos, curPos = tmp$_2 + 1 | 0, tmp$_2)) | 0));
    return curPos;
  };
  JsonReader.prototype.skipElement = function () {
    if (this.tokenClass !== TC_BEGIN_OBJ && this.tokenClass !== TC_BEGIN_LIST) {
      this.nextToken();
      return;
    }var tokenStack = ArrayList_init_0();
    do {
      switch (this.tokenClass) {
        case 8:
        case 6:
          tokenStack.add_11rb$(this.tokenClass);
          break;
        case 9:
          if (last(tokenStack) !== TC_BEGIN_LIST)
            throw JsonDecodingException_0(this.currentPosition, 'found ] instead of }', this.source_0);
          tokenStack.removeAt_za3lpa$(tokenStack.size - 1 | 0);
          break;
        case 7:
          if (last(tokenStack) !== TC_BEGIN_OBJ)
            throw JsonDecodingException_0(this.currentPosition, 'found } instead of ]', this.source_0);
          tokenStack.removeAt_za3lpa$(tokenStack.size - 1 | 0);
          break;
      }
      this.nextToken();
      var isNotEmpty$result;
      isNotEmpty$result = !tokenStack.isEmpty();
    }
     while (isNotEmpty$result);
  };
  JsonReader.prototype.toString = function () {
    return "JsonReader(source='" + this.source_0 + "', currentPosition=" + this.currentPosition + ', tokenClass=' + this.tokenClass + ', tokenPosition=' + this.tokenPosition_0 + ', offset=' + this.offset_0 + ')';
  };
  JsonReader.prototype.fail_bm4lxs$ = function (message, position) {
    if (position === void 0)
      position = this.currentPosition;
    throw JsonDecodingException_0(position, message, this.source_0);
  };
  JsonReader.prototype.require_wqn2ds$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.internal.JsonReader.require_wqn2ds$', function (condition, position, message) {
    if (position === void 0)
      position = this.currentPosition;
    if (!condition)
      this.fail_bm4lxs$(message(), position);
  });
  JsonReader.prototype.fromHexChar_0 = function (source, currentPosition) {
    var tmp$;
    if (!(currentPosition < source.length)) {
      this.fail_bm4lxs$('Unexpected EOF during unicode escape', currentPosition);
    }var curChar = source.charCodeAt(currentPosition);
    if ((new CharRange(48, 57)).contains_mef7kx$(curChar))
      tmp$ = (curChar | 0) - 48 | 0;
    else if ((new CharRange(97, 102)).contains_mef7kx$(curChar))
      tmp$ = (curChar | 0) - 97 + 10 | 0;
    else if ((new CharRange(65, 70)).contains_mef7kx$(curChar))
      tmp$ = (curChar | 0) - 65 + 10 | 0;
    else
      tmp$ = this.fail_bm4lxs$("Invalid toHexChar char '" + String.fromCharCode(curChar) + "' in unicode escape");
    return tmp$;
  };
  JsonReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonReader',
    interfaces: []
  };
  function rangeEquals(source, start, length, str) {
    var n = str.length;
    if (length !== n)
      return false;
    for (var i = 0; i < n; i++)
      if (source.charCodeAt(start + i | 0) !== str.charCodeAt(i))
        return false;
    return true;
  }
  var encodePolymorphically = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.internal.encodePolymorphically_7qn3k2$', wrapFunction(function () {
    var AbstractPolymorphicSerializer = _.kotlinx.serialization.internal.AbstractPolymorphicSerializer;
    var throwCCE = Kotlin.throwCCE;
    var Any = Object;
    var checkKind = _.kotlinx.serialization.json.internal.checkKind_5g0uu2$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    return function ($receiver, serializer, value, ifPolymorphic) {
      var tmp$, tmp$_0;
      if (!Kotlin.isType(serializer, AbstractPolymorphicSerializer) || $receiver.json.configuration_8be2vx$.useArrayPolymorphism_8be2vx$) {
        serializer.serialize_awe97i$($receiver, value);
        return;
      }Kotlin.isType(tmp$ = serializer, AbstractPolymorphicSerializer) ? tmp$ : throwCCE();
      var tmp$_1;
      var actualSerializer = Kotlin.isType(tmp$_1 = serializer.findPolymorphicSerializer_7kuzo6$($receiver, Kotlin.isType(tmp$_0 = value, Any) ? tmp$_0 : throwCCE()), KSerializer) ? tmp$_1 : throwCCE();
      validateIfSealed(serializer, actualSerializer, $receiver.json.configuration_8be2vx$.classDiscriminator_8be2vx$);
      var kind = actualSerializer.descriptor.kind;
      checkKind(kind);
      ifPolymorphic();
      actualSerializer.serialize_awe97i$($receiver, value);
    };
  }));
  function validateIfSealed(serializer, actualSerializer, classDiscriminator) {
    if (!Kotlin.isType(serializer, SealedClassSerializer))
      return;
    if (cachedSerialNames(actualSerializer.descriptor).contains_11rb$(classDiscriminator)) {
      var baseName = serializer.descriptor.serialName;
      var actualName = actualSerializer.descriptor.serialName;
      throw IllegalStateException_init(("Sealed class '" + actualName + "' cannot be serialized as base class '" + baseName + "' because" + (" it has property name that conflicts with JSON class discriminator '" + classDiscriminator + "'. ") + 'You can either change class discriminator in JsonConfiguration, ' + 'rename property with @SerialName annotation or fall back to array polymorphism').toString());
    }}
  function checkKind(kind) {
    if (Kotlin.isType(kind, UnionKind$ENUM_KIND)) {
      throw IllegalStateException_init("Enums cannot be serialized polymorphically with 'type' parameter. You can use 'JsonConfiguration.useArrayPolymorphism' instead".toString());
    }if (Kotlin.isType(kind, PrimitiveKind)) {
      throw IllegalStateException_init("Primitives cannot be serialized polymorphically with 'type' parameter. You can use 'JsonConfiguration.useArrayPolymorphism' instead".toString());
    }if (Kotlin.isType(kind, PolymorphicKind)) {
      throw IllegalStateException_init('Actual serializer for polymorphic cannot be polymorphic itself'.toString());
    }}
  function decodeSerializableValuePolymorphic($receiver, deserializer) {
    var tmp$;
    if (!Kotlin.isType(deserializer, AbstractPolymorphicSerializer) || $receiver.json.configuration_8be2vx$.useArrayPolymorphism_8be2vx$) {
      return deserializer.deserialize_nts5qn$($receiver);
    }var value = $receiver.decodeJson();
    if (!Kotlin.isType(value, JsonObject)) {
      var message = 'Expected ' + getKClass(JsonObject) + ' but found ' + Kotlin.getKClassFromExpression(value);
      throw IllegalStateException_init(message.toString());
    }var jsonTree = value;
    var type = get_content(getValue(jsonTree, $receiver.json.configuration_8be2vx$.classDiscriminator_8be2vx$));
    (Kotlin.isType(tmp$ = jsonTree.content, MutableMap) ? tmp$ : throwCCE()).remove_11rb$($receiver.json.configuration_8be2vx$.classDiscriminator_8be2vx$);
    var tmp$_0;
    var actualSerializer = Kotlin.isType(tmp$_0 = deserializer.findPolymorphicSerializer_b69zac$($receiver, type), KSerializer) ? tmp$_0 : throwCCE();
    return readJson($receiver.json, jsonTree, actualSerializer);
  }
  function StreamingJsonInput(json, mode, reader) {
    AbstractDecoder.call(this);
    this.json_2ev5c4$_0 = json;
    this.mode_0 = mode;
    this.reader_8be2vx$ = reader;
    this.context_nemf95$_0 = this.json.context;
    this.currentIndex_0 = -1;
    this.configuration_0 = this.json.configuration_8be2vx$;
  }
  Object.defineProperty(StreamingJsonInput.prototype, 'json', {
    get: function () {
      return this.json_2ev5c4$_0;
    }
  });
  Object.defineProperty(StreamingJsonInput.prototype, 'context', {
    get: function () {
      return this.context_nemf95$_0;
    }
  });
  StreamingJsonInput.prototype.decodeJson = function () {
    return (new JsonParser(this.json.configuration_8be2vx$, this.reader_8be2vx$)).read();
  };
  Object.defineProperty(StreamingJsonInput.prototype, 'updateMode', {
    get: function () {
      return this.configuration_0.updateMode_8be2vx$;
    }
  });
  StreamingJsonInput.prototype.decodeSerializableValue_w63s0f$ = function (deserializer) {
    return decodeSerializableValuePolymorphic(this, deserializer);
  };
  StreamingJsonInput.prototype.beginStructure_r0sa6z$ = function (descriptor, typeParams) {
    var tmp$;
    var newMode = switchMode(this.json, descriptor);
    if (unboxChar(newMode.begin) !== INVALID) {
      var $this = this.reader_8be2vx$;
      if ($this.tokenClass !== newMode.beginTc) {
        toBoxedChar(toChar($this.tokenClass));
        $this.fail_bm4lxs$("Expected '" + String.fromCharCode(unboxChar(newMode.begin)) + ', kind: ' + descriptor.kind + "'", $this.tokenPosition_0);
      }this.reader_8be2vx$.nextToken();
    }switch (newMode.name) {
      case 'LIST':
      case 'MAP':
      case 'POLY_OBJ':
        tmp$ = new StreamingJsonInput(this.json, newMode, this.reader_8be2vx$);
        break;
      default:tmp$ = this.mode_0 === newMode ? this : new StreamingJsonInput(this.json, newMode, this.reader_8be2vx$);
        break;
    }
    return tmp$;
  };
  StreamingJsonInput.prototype.endStructure_qatsm0$ = function (descriptor) {
    if (unboxChar(this.mode_0.end) !== INVALID) {
      var $this = this.reader_8be2vx$;
      if ($this.tokenClass !== this.mode_0.endTc) {
        toBoxedChar(toChar($this.tokenClass));
        $this.fail_bm4lxs$("Expected '" + String.fromCharCode(unboxChar(this.mode_0.end)) + "'", $this.tokenPosition_0);
      }this.reader_8be2vx$.nextToken();
    }};
  StreamingJsonInput.prototype.decodeNotNullMark = function () {
    return this.reader_8be2vx$.tokenClass !== TC_NULL;
  };
  StreamingJsonInput.prototype.decodeNull = function () {
    var $this = this.reader_8be2vx$;
    if ($this.tokenClass !== TC_NULL) {
      toBoxedChar(toChar($this.tokenClass));
      $this.fail_bm4lxs$("Expected 'null' literal", $this.tokenPosition_0);
    }this.reader_8be2vx$.nextToken();
    return null;
  };
  StreamingJsonInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    var tmp$;
    var tokenClass = this.reader_8be2vx$.tokenClass;
    if (tokenClass === TC_COMMA) {
      var $this = this.reader_8be2vx$;
      var condition = this.currentIndex_0 !== -1;
      var position = this.reader_8be2vx$.currentPosition;
      if (!condition) {
        $this.fail_bm4lxs$('Unexpected leading comma', position);
      }this.reader_8be2vx$.nextToken();
    }switch (this.mode_0.name) {
      case 'LIST':
        tmp$ = this.decodeListIndex_0(tokenClass);
        break;
      case 'MAP':
        tmp$ = this.decodeMapIndex_0(tokenClass);
        break;
      case 'POLY_OBJ':
        switch (this.currentIndex_0 = this.currentIndex_0 + 1 | 0, this.currentIndex_0) {
          case 0:
            tmp$ = 0;
            break;
          case 1:
            tmp$ = 1;
            break;
          default:tmp$ = -1;
            break;
        }

        break;
      default:tmp$ = this.decodeObjectIndex_0(tokenClass, descriptor);
        break;
    }
    return tmp$;
  };
  StreamingJsonInput.prototype.decodeMapIndex_0 = function (tokenClass) {
    var tmp$;
    if (tokenClass !== TC_COMMA && this.currentIndex_0 % 2 === 1) {
      var $this = this.reader_8be2vx$;
      if ($this.tokenClass !== TC_END_OBJ) {
        toBoxedChar(toChar($this.tokenClass));
        $this.fail_bm4lxs$('Expected end of the object or comma', $this.tokenPosition_0);
      }}if (this.currentIndex_0 % 2 === 0) {
      var $this_0 = this.reader_8be2vx$;
      if ($this_0.tokenClass !== TC_COLON) {
        toBoxedChar(toChar($this_0.tokenClass));
        $this_0.fail_bm4lxs$("Expected ':' after the key", $this_0.tokenPosition_0);
      }this.reader_8be2vx$.nextToken();
    }if (!this.reader_8be2vx$.canBeginValue) {
      var $this_1 = this.reader_8be2vx$;
      var condition = tokenClass !== TC_COMMA;
      var position;
      position = $this_1.currentPosition;
      if (!condition) {
        $this_1.fail_bm4lxs$('Unexpected trailing comma', position);
      }tmp$ = -1;
    } else {
      tmp$ = (this.currentIndex_0 = this.currentIndex_0 + 1 | 0, this.currentIndex_0);
    }
    return tmp$;
  };
  StreamingJsonInput.prototype.decodeObjectIndex_0 = function (tokenClass, descriptor) {
    if (tokenClass === TC_COMMA && !this.reader_8be2vx$.canBeginValue) {
      this.reader_8be2vx$.fail_bm4lxs$('Unexpected trailing comma');
    }while (this.reader_8be2vx$.canBeginValue) {
      this.currentIndex_0 = this.currentIndex_0 + 1 | 0;
      var key = this.decodeString();
      var $this = this.reader_8be2vx$;
      if ($this.tokenClass !== TC_COLON) {
        toBoxedChar(toChar($this.tokenClass));
        $this.fail_bm4lxs$("Expected ':'", $this.tokenPosition_0);
      }this.reader_8be2vx$.nextToken();
      var index = descriptor.getElementIndex_61zpoe$(key);
      if (index !== -3) {
        return index;
      }if (!this.configuration_0.ignoreUnknownKeys_8be2vx$) {
        this.reader_8be2vx$.fail_bm4lxs$("Encountered an unknown key '" + key + "'. You can enable 'JsonConfiguration.ignoreUnknownKeys' property" + ' to ignore unknown keys');
      } else {
        this.reader_8be2vx$.skipElement();
      }
      if (this.reader_8be2vx$.tokenClass === TC_COMMA) {
        this.reader_8be2vx$.nextToken();
        var $this_0 = this.reader_8be2vx$;
        var condition = this.reader_8be2vx$.canBeginValue;
        var position = this.reader_8be2vx$.currentPosition;
        if (!condition) {
          $this_0.fail_bm4lxs$('Unexpected trailing comma', position);
        }}}
    return -1;
  };
  StreamingJsonInput.prototype.decodeListIndex_0 = function (tokenClass) {
    var tmp$;
    if (tokenClass !== TC_COMMA && this.currentIndex_0 !== -1) {
      var $this = this.reader_8be2vx$;
      if ($this.tokenClass !== TC_END_LIST) {
        toBoxedChar(toChar($this.tokenClass));
        $this.fail_bm4lxs$('Expected end of the array or comma', $this.tokenPosition_0);
      }}if (!this.reader_8be2vx$.canBeginValue) {
      var $this_0 = this.reader_8be2vx$;
      var condition = tokenClass !== TC_COMMA;
      var position;
      position = $this_0.currentPosition;
      if (!condition) {
        $this_0.fail_bm4lxs$('Unexpected trailing comma', position);
      }tmp$ = -1;
    } else {
      tmp$ = (this.currentIndex_0 = this.currentIndex_0 + 1 | 0, this.currentIndex_0);
    }
    return tmp$;
  };
  StreamingJsonInput.prototype.decodeBoolean = function () {
    var tmp$;
    if (this.configuration_0.isLenient_8be2vx$) {
      tmp$ = toBooleanStrict(this.reader_8be2vx$.takeString());
    } else {
      tmp$ = toBooleanStrict(this.reader_8be2vx$.takeBooleanStringUnquoted());
    }
    return tmp$;
  };
  StreamingJsonInput.prototype.decodeByte = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toByte_0($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'byte' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeShort = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toShort($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'short' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeInt = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toInt($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'int' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeLong = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toLong($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'long' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeFloat = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toDouble($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'float' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeDouble = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toDouble($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'double' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeChar = function () {
    var $receiver = this.reader_8be2vx$.takeString();
    var parse_0$result;
    try {
      parse_0$result = toBoxedChar(single($receiver));
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + 'char' + "'");
      } else
        throw e;
    }
    return parse_0$result;
  };
  StreamingJsonInput.prototype.decodeString = function () {
    var tmp$;
    if (this.configuration_0.isLenient_8be2vx$) {
      tmp$ = this.reader_8be2vx$.takeString();
    } else {
      tmp$ = this.reader_8be2vx$.takeStringQuoted();
    }
    return tmp$;
  };
  StreamingJsonInput.prototype.parse_0 = function ($receiver, type, block) {
    try {
      return block($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        this.reader_8be2vx$.fail_bm4lxs$("Failed to parse '" + type + "'");
      } else
        throw e;
    }
  };
  StreamingJsonInput.prototype.decodeEnum_qatsm0$ = function (enumDescriptor) {
    return getElementIndexOrThrow(enumDescriptor, this.decodeString());
  };
  StreamingJsonInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StreamingJsonInput',
    interfaces: [AbstractDecoder, JsonInput]
  };
  function StreamingJsonOutput(composer, json, mode, modeReuseCache) {
    AbstractEncoder.call(this);
    this.composer_0 = composer;
    this.json_lpdodt$_0 = json;
    this.mode_0 = mode;
    this.modeReuseCache_0 = modeReuseCache;
    this.context_41jdqm$_0 = this.json.context;
    this.configuration_0 = this.json.configuration_8be2vx$;
    this.forceQuoting_0 = false;
    this.writePolymorphic_0 = false;
    var i = this.mode_0.ordinal;
    if (this.modeReuseCache_0[i] !== null || this.modeReuseCache_0[i] !== this)
      this.modeReuseCache_0[i] = this;
  }
  Object.defineProperty(StreamingJsonOutput.prototype, 'json', {
    get: function () {
      return this.json_lpdodt$_0;
    }
  });
  Object.defineProperty(StreamingJsonOutput.prototype, 'context', {
    get: function () {
      return this.context_41jdqm$_0;
    }
  });
  StreamingJsonOutput.prototype.encodeJson_qiw0cd$ = function (element) {
    this.encodeSerializableValue_tf03ej$(JsonElementSerializer_getInstance(), element);
  };
  StreamingJsonOutput.prototype.shouldEncodeElementDefault_3zr2iy$ = function (descriptor, index) {
    return this.configuration_0.encodeDefaults_8be2vx$;
  };
  StreamingJsonOutput.prototype.encodeSerializableValue_tf03ej$ = function (serializer, value) {
    encodePolymorphically$break: do {
      var tmp$, tmp$_0;
      if (!Kotlin.isType(serializer, AbstractPolymorphicSerializer) || this.json.configuration_8be2vx$.useArrayPolymorphism_8be2vx$) {
        serializer.serialize_awe97i$(this, value);
        break encodePolymorphically$break;
      }Kotlin.isType(tmp$ = serializer, AbstractPolymorphicSerializer) ? tmp$ : throwCCE();
      var tmp$_1;
      var actualSerializer = Kotlin.isType(tmp$_1 = serializer.findPolymorphicSerializer_7kuzo6$(this, Kotlin.isType(tmp$_0 = value, Any) ? tmp$_0 : throwCCE()), KSerializer) ? tmp$_1 : throwCCE();
      validateIfSealed(serializer, actualSerializer, this.json.configuration_8be2vx$.classDiscriminator_8be2vx$);
      var kind = actualSerializer.descriptor.kind;
      checkKind(kind);
      this.writePolymorphic_0 = true;
      actualSerializer.serialize_awe97i$(this, value);
    }
     while (false);
  };
  StreamingJsonOutput.prototype.encodeTypeInfo_0 = function (descriptor) {
    this.composer_0.nextItem();
    this.encodeString_61zpoe$(this.configuration_0.classDiscriminator_8be2vx$);
    this.composer_0.print_s8itvh$(COLON);
    this.composer_0.space();
    this.encodeString_61zpoe$(descriptor.serialName);
  };
  StreamingJsonOutput.prototype.beginStructure_r0sa6z$ = function (descriptor, typeSerializers) {
    var tmp$;
    var newMode = switchMode(this.json, descriptor);
    if (unboxChar(newMode.begin) !== INVALID) {
      this.composer_0.print_s8itvh$(unboxChar(newMode.begin));
      this.composer_0.indent();
    }if (this.writePolymorphic_0) {
      this.writePolymorphic_0 = false;
      this.encodeTypeInfo_0(descriptor);
    }if (this.mode_0 === newMode) {
      return this;
    }return (tmp$ = this.modeReuseCache_0[newMode.ordinal]) != null ? tmp$ : new StreamingJsonOutput(this.composer_0, this.json, newMode, this.modeReuseCache_0);
  };
  StreamingJsonOutput.prototype.endStructure_qatsm0$ = function (descriptor) {
    if (unboxChar(this.mode_0.end) !== INVALID) {
      this.composer_0.unIndent();
      this.composer_0.nextItem();
      this.composer_0.print_s8itvh$(unboxChar(this.mode_0.end));
    }};
  StreamingJsonOutput.prototype.encodeElement_3zr2iy$ = function (descriptor, index) {
    var tmp$;
    switch (this.mode_0.name) {
      case 'LIST':
        if (!this.composer_0.writingFirst)
          this.composer_0.print_s8itvh$(COMMA);
        this.composer_0.nextItem();
        break;
      case 'MAP':
        if (!this.composer_0.writingFirst) {
          if (index % 2 === 0) {
            this.composer_0.print_s8itvh$(COMMA);
            this.composer_0.nextItem();
            tmp$ = true;
          } else {
            this.composer_0.print_s8itvh$(COLON);
            this.composer_0.space();
            tmp$ = false;
          }
          this.forceQuoting_0 = tmp$;
        } else {
          this.forceQuoting_0 = true;
          this.composer_0.nextItem();
        }

        break;
      case 'POLY_OBJ':
        if (index === 0)
          this.forceQuoting_0 = true;
        if (index === 1) {
          this.composer_0.print_s8itvh$(COMMA);
          this.composer_0.space();
          this.forceQuoting_0 = false;
        }
        break;
      default:if (!this.composer_0.writingFirst)
          this.composer_0.print_s8itvh$(COMMA);
        this.composer_0.nextItem();
        this.encodeString_61zpoe$(descriptor.getElementName_za3lpa$(index));
        this.composer_0.print_s8itvh$(COLON);
        this.composer_0.space();
        break;
    }
    return true;
  };
  StreamingJsonOutput.prototype.encodeNull = function () {
    this.composer_0.print_61zpoe$(NULL_0);
  };
  StreamingJsonOutput.prototype.encodeBoolean_6taknv$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_6taknv$(value);
  };
  StreamingJsonOutput.prototype.encodeByte_s8j3t7$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_s8j3t7$(value);
  };
  StreamingJsonOutput.prototype.encodeShort_mq22fl$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_mq22fl$(value);
  };
  StreamingJsonOutput.prototype.encodeInt_za3lpa$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_za3lpa$(value);
  };
  StreamingJsonOutput.prototype.encodeLong_s8cxhz$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_s8cxhz$(value);
  };
  StreamingJsonOutput.prototype.encodeFloat_mx4ult$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_mx4ult$(value);
    if (!this.configuration_0.serializeSpecialFloatingPointValues_8be2vx$ && !isFinite(value)) {
      throw InvalidFloatingPoint(value, 'float', this.composer_0.sb_8be2vx$.toString());
    }};
  StreamingJsonOutput.prototype.encodeDouble_14dthe$ = function (value) {
    if (this.forceQuoting_0)
      this.encodeString_61zpoe$(value.toString());
    else
      this.composer_0.print_14dthe$(value);
    if (!this.configuration_0.serializeSpecialFloatingPointValues_8be2vx$ && !isFinite_0(value)) {
      throw InvalidFloatingPoint(value, 'double', this.composer_0.sb_8be2vx$.toString());
    }};
  StreamingJsonOutput.prototype.encodeChar_s8itvh$ = function (value) {
    this.encodeString_61zpoe$(String.fromCharCode(value));
  };
  StreamingJsonOutput.prototype.encodeString_61zpoe$ = function (value) {
    if (this.configuration_0.unquotedPrint_8be2vx$ && !shouldBeQuoted(value)) {
      this.composer_0.print_61zpoe$(value);
    } else {
      this.composer_0.printQuoted_61zpoe$(value);
    }
  };
  StreamingJsonOutput.prototype.encodeEnum_3zr2iy$ = function (enumDescriptor, index) {
    this.encodeString_61zpoe$(enumDescriptor.getElementName_za3lpa$(index));
  };
  function StreamingJsonOutput$Composer(sb, json) {
    this.sb_8be2vx$ = sb;
    this.json_0 = json;
    this.level_0 = 0;
    this.writingFirst_uw5fqz$_0 = true;
  }
  Object.defineProperty(StreamingJsonOutput$Composer.prototype, 'writingFirst', {
    get: function () {
      return this.writingFirst_uw5fqz$_0;
    },
    set: function (writingFirst) {
      this.writingFirst_uw5fqz$_0 = writingFirst;
    }
  });
  StreamingJsonOutput$Composer.prototype.indent = function () {
    this.writingFirst = true;
    this.level_0 = this.level_0 + 1 | 0;
  };
  StreamingJsonOutput$Composer.prototype.unIndent = function () {
    this.level_0 = this.level_0 - 1 | 0;
  };
  StreamingJsonOutput$Composer.prototype.nextItem = function () {
    this.writingFirst = false;
    if (this.json_0.configuration_8be2vx$.prettyPrint_8be2vx$) {
      this.print_61zpoe$('\n');
      var times = this.level_0;
      for (var index = 0; index < times; index++) {
        this.print_61zpoe$(this.json_0.configuration_8be2vx$.indent_8be2vx$);
      }
    }};
  StreamingJsonOutput$Composer.prototype.space = function () {
    if (this.json_0.configuration_8be2vx$.prettyPrint_8be2vx$)
      this.print_s8itvh$(32);
  };
  StreamingJsonOutput$Composer.prototype.print_s8itvh$ = function (v) {
    return this.sb_8be2vx$.append_s8itvh$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_61zpoe$ = function (v) {
    return this.sb_8be2vx$.append_61zpoe$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_mx4ult$ = function (v) {
    return this.sb_8be2vx$.append_s8jyv4$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_14dthe$ = function (v) {
    return this.sb_8be2vx$.append_s8jyv4$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_s8j3t7$ = function (v) {
    return this.sb_8be2vx$.append_s8jyv4$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_mq22fl$ = function (v) {
    return this.sb_8be2vx$.append_s8jyv4$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_za3lpa$ = function (v) {
    return this.sb_8be2vx$.append_s8jyv4$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_s8cxhz$ = function (v) {
    return this.sb_8be2vx$.append_s8jyv4$(v);
  };
  StreamingJsonOutput$Composer.prototype.print_6taknv$ = function (v) {
    return this.sb_8be2vx$.append_6taknv$(v);
  };
  StreamingJsonOutput$Composer.prototype.printQuoted_61zpoe$ = function (value) {
    printQuoted(this.sb_8be2vx$, value);
  };
  StreamingJsonOutput$Composer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Composer',
    interfaces: []
  };
  StreamingJsonOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StreamingJsonOutput',
    interfaces: [AbstractEncoder, JsonOutput]
  };
  function StreamingJsonOutput_init(output, json, mode, modeReuseCache, $this) {
    $this = $this || Object.create(StreamingJsonOutput.prototype);
    StreamingJsonOutput.call($this, new StreamingJsonOutput$Composer(output, json), json, mode, modeReuseCache);
    return $this;
  }
  function toHexChar(i) {
    var d = i & 15;
    return d < 10 ? toChar(d + 48 | 0) : toChar(d - 10 + 97 | 0);
  }
  var ESCAPE_CHARS;
  function printQuoted($receiver, value) {
    var tmp$;
    $receiver.append_s8itvh$(STRING);
    var lastPos = 0;
    var length = value.length;
    for (var i = 0; i < length; i++) {
      var c = value.charCodeAt(i) | 0;
      if (c >= ESCAPE_CHARS.length)
        continue;
      tmp$ = ESCAPE_CHARS[c];
      if (tmp$ == null) {
        continue;
      }var esc = tmp$;
      $receiver.append_ezbsdh$(value, lastPos, i);
      $receiver.append_61zpoe$(esc);
      lastPos = i + 1 | 0;
    }
    $receiver.append_ezbsdh$(value, lastPos, length);
    $receiver.append_s8itvh$(STRING);
  }
  function toBooleanStrict($receiver) {
    var tmp$;
    tmp$ = toBooleanStrictOrNull($receiver);
    if (tmp$ == null) {
      throw IllegalStateException_init($receiver + ' does not represent a Boolean');
    }return tmp$;
  }
  function toBooleanStrictOrNull($receiver) {
    if (equals_0($receiver, 'true', true))
      return true;
    else if (equals_0($receiver, 'false', true))
      return false;
    else
      return null;
  }
  function shouldBeQuoted(str) {
    var tmp$;
    if (equals(str, NULL_0))
      return true;
    tmp$ = iterator(str);
    while (tmp$.hasNext()) {
      var ch = unboxChar(tmp$.next());
      if (charToTokenClass(ch) !== TC_OTHER)
        return true;
    }
    return false;
  }
  function readJson($receiver, element, deserializer) {
    var tmp$, tmp$_0;
    if (Kotlin.isType(element, JsonObject))
      tmp$_0 = new JsonTreeInput($receiver, element);
    else if (Kotlin.isType(element, JsonArray))
      tmp$_0 = new JsonTreeListInput($receiver, element);
    else if (Kotlin.isType(element, JsonLiteral) || equals(element, JsonNull_getInstance())) {
      tmp$_0 = new JsonPrimitiveInput($receiver, Kotlin.isType(tmp$ = element, JsonPrimitive) ? tmp$ : throwCCE());
    } else
      tmp$_0 = Kotlin.noWhenBranchMatched();
    var input = tmp$_0;
    return decode(input, deserializer);
  }
  function AbstractJsonTreeInput(json, value) {
    NamedValueDecoder.call(this);
    this.json_sa61ty$_0 = json;
    this.value_tsgcfp$_0 = value;
    this.configuration_0 = this.json.configuration_8be2vx$;
  }
  Object.defineProperty(AbstractJsonTreeInput.prototype, 'json', {
    get: function () {
      return this.json_sa61ty$_0;
    }
  });
  Object.defineProperty(AbstractJsonTreeInput.prototype, 'value', {
    get: function () {
      return this.value_tsgcfp$_0;
    }
  });
  Object.defineProperty(AbstractJsonTreeInput.prototype, 'context', {
    get: function () {
      return this.json.context;
    }
  });
  AbstractJsonTreeInput.prototype.currentObject_0 = function () {
    var tmp$, tmp$_0;
    return (tmp$_0 = (tmp$ = this.currentTagOrNull) != null ? this.currentElement_61zpoe$(tmp$) : null) != null ? tmp$_0 : this.value;
  };
  AbstractJsonTreeInput.prototype.decodeJson = function () {
    return this.currentObject_0();
  };
  Object.defineProperty(AbstractJsonTreeInput.prototype, 'updateMode', {
    get: function () {
      return this.configuration_0.updateMode_8be2vx$;
    }
  });
  AbstractJsonTreeInput.prototype.decodeSerializableValue_w63s0f$ = function (deserializer) {
    return decodeSerializableValuePolymorphic(this, deserializer);
  };
  AbstractJsonTreeInput.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return childName;
  };
  AbstractJsonTreeInput.prototype.beginStructure_r0sa6z$ = function (descriptor, typeParams) {
    var tmp$, tmp$_0;
    var currentObject = this.currentObject_0();
    tmp$ = descriptor.kind;
    if (equals(tmp$, StructureKind$LIST_getInstance()) || Kotlin.isType(tmp$, PolymorphicKind)) {
      var tmp$_1 = this.json;
      if (!Kotlin.isType(currentObject, JsonArray)) {
        var message = 'Expected ' + getKClass(JsonArray) + ' but found ' + Kotlin.getKClassFromExpression(currentObject);
        throw IllegalStateException_init(message.toString());
      }tmp$_0 = new JsonTreeListInput(tmp$_1, currentObject);
    } else if (equals(tmp$, StructureKind$MAP_getInstance())) {
      var $receiver = this.json;
      var tmp$_2;
      var keyDescriptor = descriptor.getElementDescriptor_za3lpa$(0);
      var keyKind = keyDescriptor.kind;
      if (Kotlin.isType(keyKind, PrimitiveKind) || equals(keyKind, UnionKind.ENUM_KIND)) {
        var tmp$_3 = this.json;
        if (!Kotlin.isType(currentObject, JsonObject)) {
          var message_0 = 'Expected ' + getKClass(JsonObject) + ' but found ' + Kotlin.getKClassFromExpression(currentObject);
          throw IllegalStateException_init(message_0.toString());
        }tmp$_2 = new JsonTreeMapInput(tmp$_3, currentObject);
      } else if ($receiver.configuration_8be2vx$.allowStructuredMapKeys_8be2vx$) {
        var tmp$_4 = this.json;
        if (!Kotlin.isType(currentObject, JsonArray)) {
          var message_1 = 'Expected ' + getKClass(JsonArray) + ' but found ' + Kotlin.getKClassFromExpression(currentObject);
          throw IllegalStateException_init(message_1.toString());
        }tmp$_2 = new JsonTreeListInput(tmp$_4, currentObject);
      } else {
        throw InvalidKeyKindException(keyDescriptor);
      }
      tmp$_0 = tmp$_2;
    } else {
      var tmp$_5 = this.json;
      if (!Kotlin.isType(currentObject, JsonObject)) {
        var message_2 = 'Expected ' + getKClass(JsonObject) + ' but found ' + Kotlin.getKClassFromExpression(currentObject);
        throw IllegalStateException_init(message_2.toString());
      }tmp$_0 = new JsonTreeInput(tmp$_5, currentObject);
    }
    return tmp$_0;
  };
  AbstractJsonTreeInput.prototype.endStructure_qatsm0$ = function (descriptor) {
  };
  AbstractJsonTreeInput.prototype.getValue_61zpoe$ = function (tag) {
    var tmp$, tmp$_0;
    var currentElement = this.currentElement_61zpoe$(tag);
    tmp$_0 = Kotlin.isType(tmp$ = currentElement, JsonPrimitive) ? tmp$ : null;
    if (tmp$_0 == null) {
      throw JsonDecodingException_0(-1, 'Expected JsonPrimitive at ' + tag + ', found ' + currentElement, this.currentObject_0().toString());
    }return tmp$_0;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedEnum_xicdkz$ = function (tag, enumDescription) {
    return getElementIndexOrThrow(enumDescription, this.getValue_61zpoe$(tag).content);
  };
  AbstractJsonTreeInput.prototype.decodeTaggedNull_11rb$ = function (tag) {
    return null;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedNotNullMark_11rb$ = function (tag) {
    return this.currentElement_61zpoe$(tag) !== JsonNull_getInstance();
  };
  AbstractJsonTreeInput.prototype.decodeTaggedUnit_11rb$ = function (tag) {
    return;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedBoolean_11rb$ = function (tag) {
    var tmp$;
    var value = this.getValue_61zpoe$(tag);
    if (!this.json.configuration_8be2vx$.isLenient_8be2vx$) {
      var literal = Kotlin.isType(tmp$ = value, JsonLiteral) ? tmp$ : throwCCE();
      if (literal.isString)
        throw JsonDecodingException_0(-1, "Boolean literal for key '" + tag + "' should be unquoted. " + lenientHint, this.currentObject_0().toString());
    }return value.boolean;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedByte_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = toByte($receiver.int);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'byte' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedShort_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = toShort_0($receiver.int);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'short' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedInt_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = $receiver.int;
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'int' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedLong_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = $receiver.long;
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'long' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedFloat_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = $receiver.float;
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'float' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedDouble_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = $receiver.double;
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'double' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.decodeTaggedChar_11rb$ = function (tag) {
    var $receiver = this.getValue_61zpoe$(tag);
    var primitive_0$result;
    try {
      primitive_0$result = toBoxedChar(single($receiver.content));
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + 'char' + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
    return primitive_0$result;
  };
  AbstractJsonTreeInput.prototype.primitive_0 = function ($receiver, primitive, block) {
    try {
      return block($receiver);
    } catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw JsonDecodingException_0(-1, "Failed to parse '" + primitive + "'", this.currentObject_0().toString());
      } else
        throw e;
    }
  };
  AbstractJsonTreeInput.prototype.decodeTaggedString_11rb$ = function (tag) {
    var tmp$;
    var value = this.getValue_61zpoe$(tag);
    if (!this.json.configuration_8be2vx$.isLenient_8be2vx$) {
      var literal = Kotlin.isType(tmp$ = value, JsonLiteral) ? tmp$ : throwCCE();
      if (!literal.isString)
        throw JsonDecodingException_0(-1, "String literal for key '" + tag + "' should be quoted. " + lenientHint, this.currentObject_0().toString());
    }return value.content;
  };
  AbstractJsonTreeInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractJsonTreeInput',
    interfaces: [JsonInput, NamedValueDecoder]
  };
  function JsonPrimitiveInput(json, value) {
    AbstractJsonTreeInput.call(this, json, value);
    this.value_dw2c30$_0 = value;
    this.pushTag_11rb$(PRIMITIVE_TAG);
  }
  Object.defineProperty(JsonPrimitiveInput.prototype, 'value', {
    get: function () {
      return this.value_dw2c30$_0;
    }
  });
  JsonPrimitiveInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    return 0;
  };
  JsonPrimitiveInput.prototype.currentElement_61zpoe$ = function (tag) {
    if (!(tag === PRIMITIVE_TAG)) {
      var message = "This input can only handle primitives with 'primitive' tag";
      throw IllegalArgumentException_init(message.toString());
    }return this.value;
  };
  JsonPrimitiveInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonPrimitiveInput',
    interfaces: [AbstractJsonTreeInput]
  };
  function JsonTreeInput(json, value) {
    AbstractJsonTreeInput.call(this, json, value);
    this.value_bv0099$_0 = value;
    this.position_0 = 0;
  }
  Object.defineProperty(JsonTreeInput.prototype, 'value', {
    get: function () {
      return this.value_bv0099$_0;
    }
  });
  JsonTreeInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    var tmp$;
    while (this.position_0 < descriptor.elementsCount) {
      var name = this.getTag_m47q6f$(descriptor, (tmp$ = this.position_0, this.position_0 = tmp$ + 1 | 0, tmp$));
      if (this.value.contains_61zpoe$(name)) {
        return this.position_0 - 1 | 0;
      }}
    return -1;
  };
  JsonTreeInput.prototype.currentElement_61zpoe$ = function (tag) {
    return getValue(this.value, tag);
  };
  JsonTreeInput.prototype.endStructure_qatsm0$ = function (descriptor) {
    var tmp$;
    if (this.configuration_0.ignoreUnknownKeys_8be2vx$ || Kotlin.isType(descriptor.kind, PolymorphicKind))
      return;
    var names = cachedSerialNames(descriptor);
    tmp$ = this.value.keys.iterator();
    while (tmp$.hasNext()) {
      var key = tmp$.next();
      if (!names.contains_11rb$(key))
        throw UnknownKeyException(key, this.value.toString());
    }
  };
  JsonTreeInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTreeInput',
    interfaces: [AbstractJsonTreeInput]
  };
  function JsonTreeMapInput(json, value) {
    JsonTreeInput.call(this, json, value);
    this.value_gf5b6l$_0 = value;
    this.keys_0 = toList(this.value.keys);
    this.size_0 = this.keys_0.size * 2 | 0;
    this.position_1 = -1;
  }
  Object.defineProperty(JsonTreeMapInput.prototype, 'value', {
    get: function () {
      return this.value_gf5b6l$_0;
    }
  });
  JsonTreeMapInput.prototype.elementName_3zr2iy$ = function (desc, index) {
    var i = index / 2 | 0;
    return this.keys_0.get_za3lpa$(i);
  };
  JsonTreeMapInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    while (this.position_1 < (this.size_0 - 1 | 0)) {
      this.position_1 = this.position_1 + 1 | 0;
      return this.position_1;
    }
    return -1;
  };
  JsonTreeMapInput.prototype.currentElement_61zpoe$ = function (tag) {
    return this.position_1 % 2 === 0 ? JsonLiteral_init_1(tag) : getValue(this.value, tag);
  };
  JsonTreeMapInput.prototype.endStructure_qatsm0$ = function (descriptor) {
  };
  JsonTreeMapInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTreeMapInput',
    interfaces: [JsonTreeInput]
  };
  function JsonTreeListInput(json, value) {
    AbstractJsonTreeInput.call(this, json, value);
    this.value_nobiq7$_0 = value;
    this.size_0 = this.value.content.size;
    this.currentIndex_0 = -1;
  }
  Object.defineProperty(JsonTreeListInput.prototype, 'value', {
    get: function () {
      return this.value_nobiq7$_0;
    }
  });
  JsonTreeListInput.prototype.elementName_3zr2iy$ = function (desc, index) {
    return index.toString();
  };
  JsonTreeListInput.prototype.currentElement_61zpoe$ = function (tag) {
    return this.value.get_za3lpa$(toInt(tag));
  };
  JsonTreeListInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    while (this.currentIndex_0 < (this.size_0 - 1 | 0)) {
      this.currentIndex_0 = this.currentIndex_0 + 1 | 0;
      return this.currentIndex_0;
    }
    return -1;
  };
  JsonTreeListInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTreeListInput',
    interfaces: [AbstractJsonTreeInput]
  };
  function writeJson$lambda(closure$result) {
    return function (it) {
      closure$result.v = it;
      return Unit;
    };
  }
  function writeJson($receiver, value, serializer) {
    var result = {v: null};
    var encoder = new JsonTreeOutput($receiver, writeJson$lambda(result));
    encode(encoder, serializer, value);
    return result.v == null ? throwUPAE('result') : result.v;
  }
  function AbstractJsonTreeOutput(json, nodeConsumer) {
    NamedValueEncoder.call(this);
    this.json_138ar7$_0 = json;
    this.nodeConsumer = nodeConsumer;
    this.configuration_0 = this.json.configuration_8be2vx$;
    this.writePolymorphic_0 = false;
  }
  Object.defineProperty(AbstractJsonTreeOutput.prototype, 'json', {
    get: function () {
      return this.json_138ar7$_0;
    }
  });
  Object.defineProperty(AbstractJsonTreeOutput.prototype, 'context', {
    get: function () {
      return this.json.context;
    }
  });
  AbstractJsonTreeOutput.prototype.encodeJson_qiw0cd$ = function (element) {
    this.encodeSerializableValue_tf03ej$(JsonElementSerializer_getInstance(), element);
  };
  AbstractJsonTreeOutput.prototype.shouldEncodeElementDefault_3zr2iy$ = function (descriptor, index) {
    return this.configuration_0.encodeDefaults_8be2vx$;
  };
  AbstractJsonTreeOutput.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return childName;
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedNull_11rb$ = function (tag) {
    this.putElement_zafu29$(tag, JsonNull_getInstance());
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedInt_dpg1yx$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init(value));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedByte_19qe40$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init(value));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedShort_veccj0$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init(value));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedLong_19wkf8$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init(value));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedFloat_vlf4p8$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init(value));
    if (!this.configuration_0.serializeSpecialFloatingPointValues_8be2vx$ && !isFinite(value)) {
      throw InvalidFloatingPoint_0(value, tag, 'float', this.getCurrent().toString());
    }};
  AbstractJsonTreeOutput.prototype.encodeSerializableValue_tf03ej$ = function (serializer, value) {
    if (this.currentTagOrNull != null || (!Kotlin.isType(serializer.descriptor.kind, PrimitiveKind) && serializer.descriptor.kind !== UnionKind$ENUM_KIND_getInstance())) {
      encodePolymorphically$break: do {
        var tmp$, tmp$_0;
        if (!Kotlin.isType(serializer, AbstractPolymorphicSerializer) || this.json.configuration_8be2vx$.useArrayPolymorphism_8be2vx$) {
          serializer.serialize_awe97i$(this, value);
          break encodePolymorphically$break;
        }Kotlin.isType(tmp$ = serializer, AbstractPolymorphicSerializer) ? tmp$ : throwCCE();
        var tmp$_1;
        var actualSerializer = Kotlin.isType(tmp$_1 = serializer.findPolymorphicSerializer_7kuzo6$(this, Kotlin.isType(tmp$_0 = value, Any) ? tmp$_0 : throwCCE()), KSerializer) ? tmp$_1 : throwCCE();
        validateIfSealed(serializer, actualSerializer, this.json.configuration_8be2vx$.classDiscriminator_8be2vx$);
        var kind = actualSerializer.descriptor.kind;
        checkKind(kind);
        this.writePolymorphic_0 = true;
        actualSerializer.serialize_awe97i$(this, value);
      }
       while (false);
    } else {
      var $receiver = new JsonPrimitiveOutput(this.json, this.nodeConsumer);
      $receiver.encodeSerializableValue_tf03ej$(serializer, value);
      $receiver.endEncode_qatsm0$(serializer.descriptor);
    }
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedDouble_e37ph5$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init(value));
    if (!this.configuration_0.serializeSpecialFloatingPointValues_8be2vx$ && !isFinite_0(value)) {
      throw InvalidFloatingPoint_0(value, tag, 'double', this.getCurrent().toString());
    }};
  AbstractJsonTreeOutput.prototype.encodeTaggedBoolean_iuyhfk$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init_0(value));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedChar_19qo1q$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init_1(String.fromCharCode(value)));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedString_l9l8mx$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init_1(value));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedEnum_v153v3$ = function (tag, enumDescription, ordinal) {
    this.putElement_zafu29$(tag, JsonLiteral_init_1(enumDescription.getElementName_za3lpa$(ordinal)));
  };
  AbstractJsonTreeOutput.prototype.encodeTaggedValue_dpg7wc$ = function (tag, value) {
    this.putElement_zafu29$(tag, JsonLiteral_init_1(value.toString()));
  };
  function AbstractJsonTreeOutput$beginStructure$lambda(this$AbstractJsonTreeOutput) {
    return function (node) {
      this$AbstractJsonTreeOutput.putElement_zafu29$(this$AbstractJsonTreeOutput.currentTag, node);
      return Unit;
    };
  }
  AbstractJsonTreeOutput.prototype.beginStructure_r0sa6z$ = function (descriptor, typeSerializers) {
    var tmp$, tmp$_0;
    var consumer = this.currentTagOrNull == null ? this.nodeConsumer : AbstractJsonTreeOutput$beginStructure$lambda(this);
    tmp$ = descriptor.kind;
    if (equals(tmp$, StructureKind$LIST_getInstance()) || Kotlin.isType(tmp$, PolymorphicKind))
      tmp$_0 = new JsonTreeListOutput(this.json, consumer);
    else if (equals(tmp$, StructureKind$MAP_getInstance())) {
      var $receiver = this.json;
      var tmp$_1;
      var keyDescriptor = descriptor.getElementDescriptor_za3lpa$(0);
      var keyKind = keyDescriptor.kind;
      if (Kotlin.isType(keyKind, PrimitiveKind) || equals(keyKind, UnionKind.ENUM_KIND)) {
        tmp$_1 = new JsonTreeMapOutput(this.json, consumer);
      } else if ($receiver.configuration_8be2vx$.allowStructuredMapKeys_8be2vx$) {
        tmp$_1 = new JsonTreeListOutput(this.json, consumer);
      } else {
        throw InvalidKeyKindException(keyDescriptor);
      }
      tmp$_0 = tmp$_1;
    } else
      tmp$_0 = new JsonTreeOutput(this.json, consumer);
    var encoder = tmp$_0;
    if (this.writePolymorphic_0) {
      this.writePolymorphic_0 = false;
      encoder.putElement_zafu29$(this.configuration_0.classDiscriminator_8be2vx$, JsonPrimitive_2(descriptor.serialName));
    }return encoder;
  };
  AbstractJsonTreeOutput.prototype.endEncode_qatsm0$ = function (descriptor) {
    this.nodeConsumer(this.getCurrent());
  };
  AbstractJsonTreeOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractJsonTreeOutput',
    interfaces: [JsonOutput, NamedValueEncoder]
  };
  var PRIMITIVE_TAG;
  function JsonPrimitiveOutput(json, nodeConsumer) {
    AbstractJsonTreeOutput.call(this, json, nodeConsumer);
    this.content_0 = null;
    this.pushTag_11rb$(PRIMITIVE_TAG);
  }
  JsonPrimitiveOutput.prototype.putElement_zafu29$ = function (key, element) {
    if (!(key === PRIMITIVE_TAG)) {
      var message = "This output can only consume primitives with 'primitive' tag";
      throw IllegalArgumentException_init(message.toString());
    }if (!(this.content_0 == null)) {
      var message_0 = 'Primitive element was already recorded. Does call to .encodeXxx happen more than once?';
      throw IllegalArgumentException_init(message_0.toString());
    }this.content_0 = element;
  };
  JsonPrimitiveOutput.prototype.getCurrent = function () {
    var value = this.content_0;
    var requireNotNull$result;
    if (value == null) {
      var message = 'Primitive element has not been recorded. Is call to .encodeXxx is missing in serializer?';
      throw IllegalArgumentException_init(message.toString());
    } else {
      requireNotNull$result = value;
    }
    return requireNotNull$result;
  };
  JsonPrimitiveOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonPrimitiveOutput',
    interfaces: [AbstractJsonTreeOutput]
  };
  function JsonTreeOutput(json, nodeConsumer) {
    AbstractJsonTreeOutput.call(this, json, nodeConsumer);
    this.content_0 = LinkedHashMap_init();
  }
  JsonTreeOutput.prototype.putElement_zafu29$ = function (key, element) {
    this.content_0.put_xwzc9p$(key, element);
  };
  JsonTreeOutput.prototype.getCurrent = function () {
    return new JsonObject(this.content_0);
  };
  JsonTreeOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTreeOutput',
    interfaces: [AbstractJsonTreeOutput]
  };
  function JsonTreeMapOutput(json, nodeConsumer) {
    JsonTreeOutput.call(this, json, nodeConsumer);
    this.tag_5cp6c7$_0 = this.tag_5cp6c7$_0;
    this.isKey_0 = true;
  }
  Object.defineProperty(JsonTreeMapOutput.prototype, 'tag_0', {
    get: function () {
      if (this.tag_5cp6c7$_0 == null)
        return throwUPAE('tag');
      return this.tag_5cp6c7$_0;
    },
    set: function (tag) {
      this.tag_5cp6c7$_0 = tag;
    }
  });
  JsonTreeMapOutput.prototype.putElement_zafu29$ = function (key, element) {
    var tmp$;
    if (this.isKey_0) {
      if (Kotlin.isType(element, JsonPrimitive))
        tmp$ = element.content;
      else if (Kotlin.isType(element, JsonObject))
        throw InvalidKeyKindException(JsonObjectSerializer_getInstance().descriptor);
      else if (Kotlin.isType(element, JsonArray))
        throw InvalidKeyKindException(JsonArraySerializer_getInstance().descriptor);
      else
        tmp$ = Kotlin.noWhenBranchMatched();
      this.tag_0 = tmp$;
      this.isKey_0 = false;
    } else {
      var $receiver = this.content_0;
      var key_0 = this.tag_0;
      $receiver.put_xwzc9p$(key_0, element);
      this.isKey_0 = true;
    }
  };
  JsonTreeMapOutput.prototype.getCurrent = function () {
    return new JsonObject(this.content_0);
  };
  JsonTreeMapOutput.prototype.shouldWriteElement_a5qihn$ = function (desc, tag, index) {
    return true;
  };
  JsonTreeMapOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTreeMapOutput',
    interfaces: [JsonTreeOutput]
  };
  function JsonTreeListOutput(json, nodeConsumer) {
    AbstractJsonTreeOutput.call(this, json, nodeConsumer);
    this.array_0 = ArrayList_init_0();
  }
  JsonTreeListOutput.prototype.elementName_3zr2iy$ = function (descriptor, index) {
    return index.toString();
  };
  JsonTreeListOutput.prototype.shouldWriteElement_a5qihn$ = function (desc, tag, index) {
    return true;
  };
  JsonTreeListOutput.prototype.putElement_zafu29$ = function (key, element) {
    var idx = toInt(key);
    this.array_0.add_wxm5ur$(idx, element);
  };
  JsonTreeListOutput.prototype.getCurrent = function () {
    return new JsonArray(this.array_0);
  };
  JsonTreeListOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonTreeListOutput',
    interfaces: [AbstractJsonTreeOutput]
  };
  var cast_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.internal.cast_5s6yet$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
    return function (T_0, isT, value) {
      if (!isT(value)) {
        var message = 'Expected ' + getKClass(T_0) + ' but found ' + Kotlin.getKClassFromExpression(value);
        throw IllegalStateException_init(message.toString());
      }return value;
    };
  }));
  function WriteMode(name, ordinal, begin, end) {
    Enum.call(this);
    this.begin = toBoxedChar(begin);
    this.end = toBoxedChar(end);
    this.name$ = name;
    this.ordinal$ = ordinal;
    this.beginTc = charToTokenClass(unboxChar(this.begin));
    this.endTc = charToTokenClass(unboxChar(this.end));
  }
  function WriteMode_initFields() {
    WriteMode_initFields = function () {
    };
    WriteMode$OBJ_instance = new WriteMode('OBJ', 0, BEGIN_OBJ, END_OBJ);
    WriteMode$LIST_instance = new WriteMode('LIST', 1, BEGIN_LIST, END_LIST);
    WriteMode$MAP_instance = new WriteMode('MAP', 2, BEGIN_OBJ, END_OBJ);
    WriteMode$POLY_OBJ_instance = new WriteMode('POLY_OBJ', 3, BEGIN_LIST, END_LIST);
  }
  var WriteMode$OBJ_instance;
  function WriteMode$OBJ_getInstance() {
    WriteMode_initFields();
    return WriteMode$OBJ_instance;
  }
  var WriteMode$LIST_instance;
  function WriteMode$LIST_getInstance() {
    WriteMode_initFields();
    return WriteMode$LIST_instance;
  }
  var WriteMode$MAP_instance;
  function WriteMode$MAP_getInstance() {
    WriteMode_initFields();
    return WriteMode$MAP_instance;
  }
  var WriteMode$POLY_OBJ_instance;
  function WriteMode$POLY_OBJ_getInstance() {
    WriteMode_initFields();
    return WriteMode$POLY_OBJ_instance;
  }
  WriteMode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WriteMode',
    interfaces: [Enum]
  };
  function WriteMode$values() {
    return [WriteMode$OBJ_getInstance(), WriteMode$LIST_getInstance(), WriteMode$MAP_getInstance(), WriteMode$POLY_OBJ_getInstance()];
  }
  WriteMode.values = WriteMode$values;
  function WriteMode$valueOf(name) {
    switch (name) {
      case 'OBJ':
        return WriteMode$OBJ_getInstance();
      case 'LIST':
        return WriteMode$LIST_getInstance();
      case 'MAP':
        return WriteMode$MAP_getInstance();
      case 'POLY_OBJ':
        return WriteMode$POLY_OBJ_getInstance();
      default:throwISE('No enum constant kotlinx.serialization.json.internal.WriteMode.' + name);
    }
  }
  WriteMode.valueOf_61zpoe$ = WriteMode$valueOf;
  function switchMode($receiver, desc) {
    var tmp$;
    tmp$ = desc.kind;
    if (Kotlin.isType(tmp$, PolymorphicKind))
      return WriteMode$POLY_OBJ_getInstance();
    else if (equals(tmp$, StructureKind$LIST_getInstance()))
      return WriteMode$LIST_getInstance();
    else if (equals(tmp$, StructureKind$MAP_getInstance())) {
      var tmp$_0;
      var keyDescriptor = desc.getElementDescriptor_za3lpa$(0);
      var keyKind = keyDescriptor.kind;
      if (Kotlin.isType(keyKind, PrimitiveKind) || equals(keyKind, UnionKind.ENUM_KIND)) {
        tmp$_0 = WriteMode$MAP_getInstance();
      } else if ($receiver.configuration_8be2vx$.allowStructuredMapKeys_8be2vx$) {
        tmp$_0 = WriteMode$LIST_getInstance();
      } else {
        throw InvalidKeyKindException(keyDescriptor);
      }
      return tmp$_0;
    } else
      return WriteMode$OBJ_getInstance();
  }
  var selectMapMode = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.json.internal.selectMapMode_adhkjx$', wrapFunction(function () {
    var PrimitiveKind = _.kotlinx.serialization.PrimitiveKind;
    var UnionKind = _.kotlinx.serialization.UnionKind;
    var equals = Kotlin.equals;
    var InvalidKeyKindException = _.kotlinx.serialization.json.InvalidKeyKindException_4b8fhx$;
    return function ($receiver, mapDescriptor, ifMap, ifList) {
      var tmp$;
      var keyDescriptor = mapDescriptor.getElementDescriptor_za3lpa$(0);
      var keyKind = keyDescriptor.kind;
      if (Kotlin.isType(keyKind, PrimitiveKind) || equals(keyKind, UnionKind.ENUM_KIND)) {
        tmp$ = ifMap();
      } else if ($receiver.configuration_8be2vx$.allowStructuredMapKeys_8be2vx$) {
        tmp$ = ifList();
      } else {
        throw InvalidKeyKindException(keyDescriptor);
      }
      return tmp$;
    };
  }));
  function PolymorphicModuleBuilder(baseClass, baseSerializer) {
    if (baseSerializer === void 0)
      baseSerializer = null;
    this.baseClass_0 = baseClass;
    this.baseSerializer_0 = baseSerializer;
    this.subclasses_0 = ArrayList_init_0();
  }
  PolymorphicModuleBuilder.prototype.addSubclass_g8f9ns$ = function (subclass, serializer) {
    this.subclasses_0.add_11rb$(to(subclass, serializer));
  };
  PolymorphicModuleBuilder.prototype.addSubclass_97auzv$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.PolymorphicModuleBuilder.addSubclass_97auzv$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, serializer) {
      this.addSubclass_g8f9ns$(getKClass(T_0), serializer);
    };
  }));
  PolymorphicModuleBuilder.prototype.subclass_97auzv$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.PolymorphicModuleBuilder.subclass_97auzv$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, serializer) {
      this.addSubclass_g8f9ns$(getKClass(T_0), serializer);
    };
  }));
  PolymorphicModuleBuilder.prototype.addSubclass_n8yg2x$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.PolymorphicModuleBuilder.addSubclass_n8yg2x$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT) {
      this.addSubclass_g8f9ns$(getKClass(T_0), serializer(getKClass(T_0)));
    };
  }));
  PolymorphicModuleBuilder.prototype.subclass_n8yg2x$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.PolymorphicModuleBuilder.subclass_n8yg2x$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT) {
      var tmp$;
      this.addSubclass_g8f9ns$(getKClass(T_0), Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE());
    };
  }));
  PolymorphicModuleBuilder.prototype.with_kmpi2j$ = function ($receiver, serializer) {
    this.addSubclass_g8f9ns$($receiver, serializer);
  };
  PolymorphicModuleBuilder.prototype.buildTo_dp9i1l$ = function (builder) {
    if (this.baseSerializer_0 != null)
      builder.registerPolymorphicSerializer_yca12w$(this.baseClass_0, this.baseClass_0, this.baseSerializer_0);
    var tmp$;
    tmp$ = this.subclasses_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var kclass = element.component1()
      , serializer = element.component2();
      var tmp$_0;
      var tmp$_1;
      builder.registerPolymorphicSerializer_yca12w$(this.baseClass_0, Kotlin.isType(tmp$_0 = kclass, KClass) ? tmp$_0 : throwCCE(), Kotlin.isType(tmp$_1 = serializer, KSerializer) ? tmp$_1 : throwCCE());
    }
  };
  PolymorphicModuleBuilder.prototype.changeBase_a3p3f0$ = function (newBaseClass, newBaseClassSerializer) {
    if (newBaseClassSerializer === void 0)
      newBaseClassSerializer = null;
    var newModule = new PolymorphicModuleBuilder(newBaseClass, newBaseClassSerializer);
    if (this.baseSerializer_0 != null) {
      var tmp$;
      var tmp$_0;
      newModule.addSubclass_g8f9ns$(Kotlin.isType(tmp$ = this.baseClass_0, KClass) ? tmp$ : throwCCE(), Kotlin.isType(tmp$_0 = this.baseSerializer_0, KSerializer) ? tmp$_0 : throwCCE());
    }var tmp$_1;
    tmp$_1 = this.subclasses_0.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      var k = element.component1()
      , v = element.component2();
      var tmp$_2, tmp$_3;
      newModule.addSubclass_g8f9ns$(Kotlin.isType(tmp$_2 = k, KClass) ? tmp$_2 : throwCCE(), Kotlin.isType(tmp$_3 = v, KSerializer) ? tmp$_3 : throwCCE());
    }
    return newModule;
  };
  PolymorphicModuleBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PolymorphicModuleBuilder',
    interfaces: []
  };
  function SerialModule() {
  }
  SerialModule.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SerialModule',
    interfaces: []
  };
  function EmptyModule() {
    EmptyModule_instance = this;
  }
  EmptyModule.prototype.getContextual_lmshww$ = function (kclass) {
    return null;
  };
  EmptyModule.prototype.getPolymorphic_b1ce0a$ = function (baseClass, value) {
    return null;
  };
  EmptyModule.prototype.getPolymorphic_6xtsla$ = function (baseClass, serializedClassName) {
    return null;
  };
  EmptyModule.prototype.dumpTo_247rdd$ = function (collector) {
  };
  EmptyModule.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'EmptyModule',
    interfaces: [SerialModule]
  };
  var EmptyModule_instance = null;
  function EmptyModule_getInstance() {
    if (EmptyModule_instance === null) {
      new EmptyModule();
    }return EmptyModule_instance;
  }
  function serializersModuleOf$lambda(closure$kClass, closure$serializer) {
    return function ($receiver) {
      $receiver.contextual_cfhkba$(closure$kClass, closure$serializer);
      return Unit;
    };
  }
  function serializersModuleOf(kClass, serializer) {
    return SerializersModule(serializersModuleOf$lambda(kClass, serializer));
  }
  var serializersModule = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.serializersModule_ewacr1$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializersModuleOf = _.kotlinx.serialization.modules.serializersModuleOf_cfhkba$;
    return function (T_0, isT, serializer) {
      return serializersModuleOf(getKClass(T_0), serializer);
    };
  }));
  function serializersModuleOf$lambda_0(closure$map) {
    return function ($receiver) {
      var tmp$;
      tmp$ = closure$map.entries.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var kclass = element.key;
        var serializer = element.value;
        var tmp$_0;
        var tmp$_1;
        $receiver.contextual_cfhkba$(Kotlin.isType(tmp$_0 = kclass, KClass) ? tmp$_0 : throwCCE(), Kotlin.isType(tmp$_1 = serializer, KSerializer) ? tmp$_1 : throwCCE());
      }
      return Unit;
    };
  }
  function serializersModuleOf_0(map) {
    return SerializersModule(serializersModuleOf$lambda_0(map));
  }
  function SerializersModule(buildAction) {
    var builder = new SerializersModuleBuilder();
    buildAction(builder);
    return builder.build_8be2vx$();
  }
  var contextual = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.contextual_mro6cs$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getReifiedTypeParameterKType = Kotlin.getReifiedTypeParameterKType;
    var serializer = _.kotlinx.serialization.serializer_saj79j$;
    var KSerializer = _.kotlinx.serialization.KSerializer;
    var throwCCE = Kotlin.throwCCE;
    return function (T_0, isT, $receiver) {
      var tmp$;
      $receiver.contextual_cfhkba$(getKClass(T_0), Kotlin.isType(tmp$ = serializer(getReifiedTypeParameterKType(T_0)), KSerializer) ? tmp$ : throwCCE());
    };
  }));
  function SerializersModuleBuilder() {
    this.class2Serializer_0 = HashMap_init();
    this.polyBase2Serializers_0 = HashMap_init();
    this.polyBase2NamedSerializers_0 = HashMap_init();
  }
  SerializersModuleBuilder.prototype.contextual_cfhkba$ = function (kClass, serializer) {
    this.registerSerializer_z3bkzg$(kClass, serializer);
  };
  SerializersModuleBuilder.prototype.polymorphic_kfyidi$ = function (baseClass, actualClass, actualSerializer) {
    this.registerPolymorphicSerializer_yca12w$(baseClass, actualClass, actualSerializer);
  };
  SerializersModuleBuilder.prototype.include_stpyu4$ = function (other) {
    other.dumpTo_247rdd$(this);
  };
  function SerializersModuleBuilder$polymorphic$lambda($receiver) {
    return Unit;
  }
  SerializersModuleBuilder.prototype.polymorphic_v5citj$ = function (baseClass, baseSerializer, buildAction) {
    if (baseSerializer === void 0)
      baseSerializer = null;
    if (buildAction === void 0)
      buildAction = SerializersModuleBuilder$polymorphic$lambda;
    var builder = new PolymorphicModuleBuilder(baseClass, baseSerializer);
    buildAction(builder);
    builder.buildTo_dp9i1l$(this);
  };
  SerializersModuleBuilder.prototype.polymorphic_czluys$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.SerializersModuleBuilder.polymorphic_czluys$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var getKClass = Kotlin.getKClass;
    function SerializersModuleBuilder$polymorphic$lambda($receiver) {
      return Unit;
    }
    return function (Base_0, isBase, baseSerializer, buildAction) {
      if (baseSerializer === void 0)
        baseSerializer = null;
      if (buildAction === void 0)
        buildAction = SerializersModuleBuilder$polymorphic$lambda;
      this.polymorphic_v5citj$(getKClass(Base_0), baseSerializer, buildAction);
    };
  }));
  function SerializersModuleBuilder$polymorphic$lambda_0($receiver) {
    return Unit;
  }
  SerializersModuleBuilder.prototype.polymorphic_myr6su$ = function (baseClass, baseClasses, buildAction) {
    if (buildAction === void 0)
      buildAction = SerializersModuleBuilder$polymorphic$lambda_0;
    var tmp$, tmp$_0, tmp$_1;
    var builder = new PolymorphicModuleBuilder(Kotlin.isType(tmp$ = baseClass, KClass) ? tmp$ : throwCCE());
    buildAction(builder);
    builder.buildTo_dp9i1l$(this);
    for (tmp$_0 = 0; tmp$_0 !== baseClasses.length; ++tmp$_0) {
      var base = baseClasses[tmp$_0];
      builder.changeBase_a3p3f0$(Kotlin.isType(tmp$_1 = base, KClass) ? tmp$_1 : throwCCE(), null).buildTo_dp9i1l$(this);
    }
  };
  SerializersModuleBuilder.prototype.registerSerializer_z3bkzg$ = function (forClass, serializer, allowOverwrite) {
    if (allowOverwrite === void 0)
      allowOverwrite = false;
    if (!allowOverwrite) {
      var previous = this.class2Serializer_0.get_11rb$(forClass);
      if (previous != null && !equals(previous, serializer)) {
        var currentName = serializer.descriptor.serialName;
        var previousName = previous.descriptor.serialName;
        throw new SerializerAlreadyRegisteredException('Serializer for ' + forClass + ' already registered in this module: ' + toString(previous) + ' (' + previousName + '), ' + ('attempted to register ' + serializer + ' (' + currentName + ')'));
      }}this.class2Serializer_0.put_xwzc9p$(forClass, serializer);
  };
  SerializersModuleBuilder.prototype.registerPolymorphicSerializer_yca12w$ = function (baseClass, concreteClass, concreteSerializer, allowOverwrite) {
    if (allowOverwrite === void 0)
      allowOverwrite = false;
    var name = concreteSerializer.descriptor.serialName;
    var $receiver = this.polyBase2Serializers_0;
    var tmp$;
    var value = $receiver.get_11rb$(baseClass);
    if (value == null) {
      var answer = HashMap_init();
      $receiver.put_xwzc9p$(baseClass, answer);
      tmp$ = answer;
    } else {
      tmp$ = value;
    }
    var baseClassSerializers = tmp$;
    var previousSerializer = baseClassSerializers.get_11rb$(concreteClass);
    var $receiver_0 = this.polyBase2NamedSerializers_0;
    var tmp$_0;
    var value_0 = $receiver_0.get_11rb$(baseClass);
    if (value_0 == null) {
      var answer_0 = HashMap_init();
      $receiver_0.put_xwzc9p$(baseClass, answer_0);
      tmp$_0 = answer_0;
    } else {
      tmp$_0 = value_0;
    }
    var names = tmp$_0;
    if (allowOverwrite) {
      if (previousSerializer != null) {
        names.remove_11rb$(previousSerializer.descriptor.serialName);
      }baseClassSerializers.put_xwzc9p$(concreteClass, concreteSerializer);
      names.put_xwzc9p$(name, concreteSerializer);
      return;
    }if (previousSerializer != null) {
      if (!equals(previousSerializer, concreteSerializer)) {
        throw SerializerAlreadyRegisteredException_init(baseClass, concreteClass);
      } else {
        names.remove_11rb$(previousSerializer.descriptor.serialName);
      }
    }var previousByName = names.get_11rb$(name);
    if (previousByName != null) {
      var $receiver_1 = asSequence(ensureNotNull(this.polyBase2Serializers_0.get_11rb$(baseClass)));
      var firstOrNull$result;
      firstOrNull$break: do {
        var tmp$_1;
        tmp$_1 = $receiver_1.iterator();
        while (tmp$_1.hasNext()) {
          var element = tmp$_1.next();
          if (element.value === previousByName) {
            firstOrNull$result = element;
            break firstOrNull$break;
          }}
        firstOrNull$result = null;
      }
       while (false);
      var conflictingClass = firstOrNull$result;
      throw IllegalArgumentException_init("Multiple polymorphic serializers for base class '" + baseClass + "' " + ("have the same serial name '" + name + "': '" + concreteClass + "' and '" + toString(conflictingClass) + "'"));
    }baseClassSerializers.put_xwzc9p$(concreteClass, concreteSerializer);
    names.put_xwzc9p$(name, concreteSerializer);
  };
  SerializersModuleBuilder.prototype.build_8be2vx$ = function () {
    return new SerialModuleImpl(this.class2Serializer_0, this.polyBase2Serializers_0, this.polyBase2NamedSerializers_0);
  };
  SerializersModuleBuilder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializersModuleBuilder',
    interfaces: [SerialModuleCollector]
  };
  function SerialModuleCollector() {
  }
  SerialModuleCollector.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SerialModuleCollector',
    interfaces: []
  };
  var contextual_0 = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.contextual_qumwz5$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, $receiver, serializer) {
      $receiver.contextual_cfhkba$(getKClass(T_0), serializer);
    };
  }));
  var getContextual = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.modules.getContextual_5m84rq$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, $receiver) {
      return $receiver.getContextual_lmshww$(getKClass(T_0));
    };
  }));
  function getContextual_0($receiver, value) {
    var tmp$;
    var tmp$_0;
    if ((tmp$ = $receiver.getContextual_lmshww$(Kotlin.getKClassFromExpression(value))) != null) {
      var tmp$_1;
      tmp$_0 = Kotlin.isType(tmp$_1 = tmp$, KSerializer) ? tmp$_1 : throwCCE();
    } else
      tmp$_0 = null;
    return tmp$_0;
  }
  function getContextualOrDefault($receiver, klass) {
    var tmp$;
    return (tmp$ = $receiver.getContextual_lmshww$(klass)) != null ? tmp$ : serializer(klass);
  }
  function getContextualOrDefault_0($receiver, value) {
    var tmp$;
    var tmp$_0;
    if ((tmp$ = getContextual_0($receiver, value)) != null)
      tmp$_0 = tmp$;
    else {
      var tmp$_1;
      tmp$_0 = Kotlin.isType(tmp$_1 = serializer(Kotlin.getKClassFromExpression(value)), KSerializer) ? tmp$_1 : throwCCE();
    }
    return tmp$_0;
  }
  function plus$lambda(this$plus, closure$other) {
    return function ($receiver) {
      $receiver.include_stpyu4$(this$plus);
      $receiver.include_stpyu4$(closure$other);
      return Unit;
    };
  }
  function plus($receiver, other) {
    return SerializersModule(plus$lambda($receiver, other));
  }
  function overwriteWith$lambda$ObjectLiteral(this$) {
    this.this$ = this$;
  }
  overwriteWith$lambda$ObjectLiteral.prototype.contextual_cfhkba$ = function (kClass, serializer) {
    this.this$.registerSerializer_z3bkzg$(kClass, serializer, true);
  };
  overwriteWith$lambda$ObjectLiteral.prototype.polymorphic_kfyidi$ = function (baseClass, actualClass, actualSerializer) {
    this.this$.registerPolymorphicSerializer_yca12w$(baseClass, actualClass, actualSerializer, true);
  };
  overwriteWith$lambda$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [SerialModuleCollector]
  };
  function overwriteWith$lambda(this$overwriteWith, closure$other) {
    return function ($receiver) {
      $receiver.include_stpyu4$(this$overwriteWith);
      closure$other.dumpTo_247rdd$(new overwriteWith$lambda$ObjectLiteral($receiver));
      return Unit;
    };
  }
  function overwriteWith($receiver, other) {
    return SerializersModule(overwriteWith$lambda($receiver, other));
  }
  function SerialModuleImpl(class2Serializer, polyBase2Serializers, polyBase2NamedSerializers) {
    this.class2Serializer_0 = class2Serializer;
    this.polyBase2Serializers_0 = polyBase2Serializers;
    this.polyBase2NamedSerializers_0 = polyBase2NamedSerializers;
  }
  SerialModuleImpl.prototype.getPolymorphic_b1ce0a$ = function (baseClass, value) {
    var tmp$, tmp$_0, tmp$_1;
    if (!isInstanceOf(value, baseClass))
      return null;
    var custom = Kotlin.isType(tmp$_0 = (tmp$ = this.polyBase2Serializers_0.get_11rb$(baseClass)) != null ? tmp$.get_11rb$(Kotlin.getKClassFromExpression(value)) : null, KSerializer) ? tmp$_0 : null;
    if (custom != null)
      return custom;
    if (baseClass != null ? baseClass.equals(PrimitiveClasses$anyClass) : null) {
      var serializer = StandardSubtypesOfAny_getInstance().getSubclassSerializer_kcmwxo$(value);
      return Kotlin.isType(tmp$_1 = serializer, KSerializer) ? tmp$_1 : null;
    }return null;
  };
  SerialModuleImpl.prototype.getPolymorphic_6xtsla$ = function (baseClass, serializedClassName) {
    var tmp$, tmp$_0, tmp$_1;
    var standardPolymorphic = (baseClass != null ? baseClass.equals(PrimitiveClasses$anyClass) : null) ? StandardSubtypesOfAny_getInstance().getDefaultDeserializer_y4putb$(serializedClassName) : null;
    if (standardPolymorphic != null)
      return Kotlin.isType(tmp$ = standardPolymorphic, KSerializer) ? tmp$ : throwCCE();
    return Kotlin.isType(tmp$_1 = (tmp$_0 = this.polyBase2NamedSerializers_0.get_11rb$(baseClass)) != null ? tmp$_0.get_11rb$(serializedClassName) : null, KSerializer) ? tmp$_1 : null;
  };
  SerialModuleImpl.prototype.getContextual_lmshww$ = function (kclass) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.class2Serializer_0.get_11rb$(kclass), KSerializer) ? tmp$ : null;
  };
  SerialModuleImpl.prototype.dumpTo_247rdd$ = function (collector) {
    var tmp$;
    tmp$ = this.class2Serializer_0.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var kclass = element.key;
      var serial = element.value;
      var tmp$_0;
      var tmp$_1;
      collector.contextual_cfhkba$(Kotlin.isType(tmp$_0 = kclass, KClass) ? tmp$_0 : throwCCE(), Kotlin.isType(tmp$_1 = serial, KSerializer) ? tmp$_1 : throwCCE());
    }
    var tmp$_2;
    tmp$_2 = this.polyBase2Serializers_0.entries.iterator();
    while (tmp$_2.hasNext()) {
      var element_0 = tmp$_2.next();
      var baseClass = element_0.key;
      var classMap = element_0.value;
      var tmp$_3;
      tmp$_3 = classMap.entries.iterator();
      while (tmp$_3.hasNext()) {
        var element_1 = tmp$_3.next();
        var actualClass = element_1.key;
        var serializer = element_1.value;
        var tmp$_4, tmp$_5;
        var tmp$_6;
        collector.polymorphic_kfyidi$(Kotlin.isType(tmp$_4 = baseClass, KClass) ? tmp$_4 : throwCCE(), Kotlin.isType(tmp$_5 = actualClass, KClass) ? tmp$_5 : throwCCE(), Kotlin.isType(tmp$_6 = serializer, KSerializer) ? tmp$_6 : throwCCE());
      }
    }
  };
  SerialModuleImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialModuleImpl',
    interfaces: [SerialModule]
  };
  function SerializerAlreadyRegisteredException(msg) {
    IllegalArgumentException_init(msg, this);
    this.name = 'SerializerAlreadyRegisteredException';
  }
  SerializerAlreadyRegisteredException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializerAlreadyRegisteredException',
    interfaces: [IllegalArgumentException]
  };
  function SerializerAlreadyRegisteredException_init(baseClass, concreteClass, $this) {
    $this = $this || Object.create(SerializerAlreadyRegisteredException.prototype);
    SerializerAlreadyRegisteredException.call($this, 'Serializer for ' + concreteClass + ' already registered in the scope of ' + baseClass);
    return $this;
  }
  function SerializerAlreadyRegisteredException_init_0(forClass, $this) {
    $this = $this || Object.create(SerializerAlreadyRegisteredException.prototype);
    SerializerAlreadyRegisteredException.call($this, 'Serializer for ' + forClass + ' already registered in this module');
    return $this;
  }
  function StandardSubtypesOfAny() {
    StandardSubtypesOfAny_instance = this;
    this.map_0 = mapOf([to(getKClass(List), ListSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(LinkedHashSet), SetSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(HashSet), new HashSetSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(Set), SetSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(LinkedHashMap), new LinkedHashMapSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)), get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(HashMap), new HashMapSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)), get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(Map), new LinkedHashMapSerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)), get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(getKClass(Map$Entry), MapEntrySerializer(get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)), get_nullable_1(new PolymorphicSerializer(PrimitiveClasses$anyClass)))), to(PrimitiveClasses$stringClass, serializer_10(kotlin_js_internal_StringCompanionObject)), to(getKClass(Char), serializer_2(kotlin_js_internal_CharCompanionObject)), to(PrimitiveClasses$intClass, serializer_5(kotlin_js_internal_IntCompanionObject)), to(PrimitiveClasses$byteClass, serializer_3(kotlin_js_internal_ByteCompanionObject)), to(PrimitiveClasses$shortClass, serializer_4(kotlin_js_internal_ShortCompanionObject)), to(getKClass(Long), serializer_6(kotlin_js_internal_LongCompanionObject)), to(PrimitiveClasses$doubleClass, serializer_8(kotlin_js_internal_DoubleCompanionObject)), to(PrimitiveClasses$floatClass, serializer_7(kotlin_js_internal_FloatCompanionObject)), to(PrimitiveClasses$booleanClass, serializer_9(kotlin_js_internal_BooleanCompanionObject)), to(getKClass(Object.getPrototypeOf(kotlin.Unit).constructor), UnitSerializer())]);
    var $receiver = this.map_0;
    var destination = LinkedHashMap_init_0(mapCapacity($receiver.size));
    var tmp$;
    tmp$ = $receiver.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0 = destination.put_xwzc9p$;
      var s = element.value;
      tmp$_0.call(destination, s.descriptor.serialName, element.value);
    }
    this.deserializingMap_0 = destination;
  }
  StandardSubtypesOfAny.prototype.getSubclassSerializer_kcmwxo$ = function (objectToCheck) {
    var tmp$;
    tmp$ = this.map_0.entries.iterator();
    while (tmp$.hasNext()) {
      var tmp$_0 = tmp$.next();
      var k = tmp$_0.key;
      var v = tmp$_0.value;
      if (isInstanceOf(objectToCheck, k))
        return v;
    }
    return null;
  };
  StandardSubtypesOfAny.prototype.getDefaultDeserializer_y4putb$ = function (serializedClassName) {
    return this.deserializingMap_0.get_11rb$(serializedClassName);
  };
  StandardSubtypesOfAny.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'StandardSubtypesOfAny',
    interfaces: []
  };
  var StandardSubtypesOfAny_instance = null;
  function StandardSubtypesOfAny_getInstance() {
    if (StandardSubtypesOfAny_instance === null) {
      new StandardSubtypesOfAny();
    }return StandardSubtypesOfAny_instance;
  }
  function ByteBuffer(capacity) {
    ByteBuffer$Companion_getInstance();
    this.capacity = capacity;
    if (!(this.capacity >= 0)) {
      var message = 'Failed requirement.';
      throw IllegalArgumentException_init(message.toString());
    }this.dw_0 = new DataView(new ArrayBuffer(this.capacity), 0, this.capacity);
    this.limit_62obw4$_0 = this.capacity;
    this.position_r0m5ac$_0 = 0;
    this.order = ByteOrder$BIG_ENDIAN_getInstance();
  }
  Object.defineProperty(ByteBuffer.prototype, 'limit', {
    get: function () {
      return this.limit_62obw4$_0;
    },
    set: function (value) {
      var tmp$;
      tmp$ = this.capacity;
      if (!(0 <= value && value <= tmp$)) {
        var message = 'Failed requirement.';
        throw IllegalArgumentException_init(message.toString());
      }this.limit_62obw4$_0 = value;
      if (this.position > value) {
        this.position = value;
      }}
  });
  Object.defineProperty(ByteBuffer.prototype, 'position', {
    get: function () {
      return this.position_r0m5ac$_0;
    },
    set: function (newPosition) {
      var tmp$;
      tmp$ = this.limit;
      if (!(0 <= newPosition && newPosition <= tmp$)) {
        var message = 'Failed requirement.';
        throw IllegalArgumentException_init(message.toString());
      }this.position_r0m5ac$_0 = newPosition;
    }
  });
  ByteBuffer.prototype.clear = function () {
    this.position = 0;
    this.limit = this.capacity;
    return this;
  };
  ByteBuffer.prototype.flip = function () {
    this.limit = this.position;
    this.position = 0;
    return this;
  };
  Object.defineProperty(ByteBuffer.prototype, 'hasRemaining', {
    get: function () {
      return this.position < this.limit;
    }
  });
  Object.defineProperty(ByteBuffer.prototype, 'remaining', {
    get: function () {
      return this.limit - this.position | 0;
    }
  });
  ByteBuffer.prototype.rewind = function () {
    this.position = 0;
    return this;
  };
  ByteBuffer.prototype.order_w2g0y3$ = function (order) {
    this.order = order;
    return this;
  };
  ByteBuffer.prototype.idx_0 = function (index, size) {
    var tmp$;
    if (index === -1) {
      this.position = this.position + size | 0;
      tmp$ = this.position - size | 0;
    } else
      tmp$ = index;
    var i = tmp$;
    if (i > this.limit)
      throw IllegalArgumentException_init_0();
    return i;
  };
  ByteBuffer.prototype.get = function () {
    return this.get_za3lpa$(-1);
  };
  ByteBuffer.prototype.get_za3lpa$ = function (index) {
    var i = this.idx_0(index, 1);
    return this.dw_0.getInt8(i);
  };
  ByteBuffer.prototype.get_mj6st8$ = function (dst, offset, cnt) {
    var pos = this.idx_0(-1, cnt);
    for (var i = 0; i < cnt; i++) {
      dst[offset + i | 0] = this.dw_0.getInt8(pos + i | 0);
    }
  };
  ByteBuffer.prototype.getChar = function () {
    return this.getChar_za3lpa$(-1);
  };
  ByteBuffer.prototype.getChar_za3lpa$ = function (index) {
    var i = this.idx_0(index, 2);
    return toBoxedChar(toChar(this.dw_0.getUint16(i, this.order === ByteOrder$LITTLE_ENDIAN_getInstance())));
  };
  ByteBuffer.prototype.getShort = function () {
    return this.getShort_za3lpa$(-1);
  };
  ByteBuffer.prototype.getShort_za3lpa$ = function (index) {
    var i = this.idx_0(index, 2);
    return this.dw_0.getInt16(i, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
  };
  ByteBuffer.prototype.getInt = function () {
    return this.getInt_za3lpa$(-1);
  };
  ByteBuffer.prototype.getInt_za3lpa$ = function (index) {
    var i = this.idx_0(index, 4);
    return this.dw_0.getInt32(i, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
  };
  ByteBuffer.prototype.getLong = function () {
    return this.getLong_za3lpa$(-1);
  };
  ByteBuffer.prototype.getLong_za3lpa$ = function (index) {
    var low;
    var high;
    var scndIdx = index === -1 ? -1 : index + 4 | 0;
    if (this.order === ByteOrder$LITTLE_ENDIAN_getInstance()) {
      low = this.getInt_za3lpa$(index);
      high = this.getInt_za3lpa$(scndIdx);
    } else {
      high = this.getInt_za3lpa$(index);
      low = this.getInt_za3lpa$(scndIdx);
    }
    return Kotlin.Long.fromInt(high).shiftLeft(32).or(Kotlin.Long.fromInt(low).and(L4294967295));
  };
  ByteBuffer.prototype.getFloat = function () {
    return this.getFloat_za3lpa$(-1);
  };
  ByteBuffer.prototype.getFloat_za3lpa$ = function (index) {
    var i = this.idx_0(index, 4);
    return this.dw_0.getFloat32(i, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
  };
  ByteBuffer.prototype.getDouble = function () {
    return this.getDouble_za3lpa$(-1);
  };
  ByteBuffer.prototype.getDouble_za3lpa$ = function (index) {
    var i = this.idx_0(index, 8);
    return this.dw_0.getFloat64(i, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
  };
  ByteBuffer.prototype.put_s8j3t7$ = function (value) {
    return this.put_pdp8qh$(value, -1);
  };
  ByteBuffer.prototype.put_pdp8qh$ = function (value, index) {
    var i = this.idx_0(index, 1);
    this.dw_0.setInt8(i, value);
    return this;
  };
  ByteBuffer.prototype.put_fqrh44$ = function (src) {
    return this.put_mj6st8$(src, 0, src.length);
  };
  ByteBuffer.prototype.put_mj6st8$ = function (src, offset, cnt) {
    var pos = this.idx_0(-1, cnt);
    for (var i = 0; i < cnt; i++) {
      this.dw_0.setInt8(pos + i | 0, src[offset + i | 0]);
    }
    return this;
  };
  ByteBuffer.prototype.putChar_s8itvh$ = function (value) {
    return this.putChar_s9u7hn$(value, -1);
  };
  ByteBuffer.prototype.putChar_s9u7hn$ = function (value, index) {
    var i = this.idx_0(index, 2);
    this.dw_0.setUint16(i, toShort_0(value | 0), this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
    return this;
  };
  ByteBuffer.prototype.putShort_mq22fl$ = function (value) {
    return this.putShort_vmjj7j$(value, -1);
  };
  ByteBuffer.prototype.putShort_vmjj7j$ = function (value, index) {
    var i = this.idx_0(index, 2);
    this.dw_0.setInt16(i, value, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
    return this;
  };
  ByteBuffer.prototype.putInt_za3lpa$ = function (value) {
    return this.putInt_vux9f0$(value, -1);
  };
  ByteBuffer.prototype.putInt_vux9f0$ = function (value, index) {
    var i = this.idx_0(index, 4);
    this.dw_0.setInt32(i, value, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
    return this;
  };
  ByteBuffer.prototype.putLong_s8cxhz$ = function (value) {
    return this.putLong_yhmem3$(value, -1);
  };
  ByteBuffer.prototype.putLong_yhmem3$ = function (value, index) {
    var high = value.shiftRight(32).toInt();
    var low = value.and(L4294967295).toInt();
    var scndIdx = index === -1 ? -1 : index + 4 | 0;
    if (this.order === ByteOrder$LITTLE_ENDIAN_getInstance()) {
      this.putInt_vux9f0$(low, index);
      this.putInt_vux9f0$(high, scndIdx);
    } else {
      this.putInt_vux9f0$(high, index);
      this.putInt_vux9f0$(low, scndIdx);
    }
    return this;
  };
  ByteBuffer.prototype.putFloat_mx4ult$ = function (value) {
    return this.putFloat_vjorfl$(value, -1);
  };
  ByteBuffer.prototype.putFloat_vjorfl$ = function (value, index) {
    var i = this.idx_0(index, 4);
    this.dw_0.setFloat32(i, value, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
    return this;
  };
  ByteBuffer.prototype.putDouble_14dthe$ = function (value) {
    return this.putDouble_12fank$(value, -1);
  };
  ByteBuffer.prototype.putDouble_12fank$ = function (value, index) {
    var i = this.idx_0(index, 8);
    this.dw_0.setFloat64(i, value, this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
    return this;
  };
  ByteBuffer.prototype.array = function () {
    var tmp$;
    var out = new Int8Array(this.limit);
    tmp$ = this.limit;
    for (var i = 0; i < tmp$; i++) {
      out[i] = this.dw_0.getInt8(i);
    }
    return out;
  };
  function ByteBuffer$Companion() {
    ByteBuffer$Companion_instance = this;
  }
  ByteBuffer$Companion.prototype.allocate_za3lpa$ = function (capacity) {
    return new ByteBuffer(capacity);
  };
  ByteBuffer$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ByteBuffer$Companion_instance = null;
  function ByteBuffer$Companion_getInstance() {
    if (ByteBuffer$Companion_instance === null) {
      new ByteBuffer$Companion();
    }return ByteBuffer$Companion_instance;
  }
  ByteBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteBuffer',
    interfaces: []
  };
  function IOException(message) {
    Exception_init(message, this);
    this.name = 'IOException';
  }
  IOException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IOException',
    interfaces: [Exception]
  };
  function IOException_init($this) {
    $this = $this || Object.create(IOException.prototype);
    IOException.call($this, 'IO Exception');
    return $this;
  }
  function InputStream() {
    InputStream$Companion_getInstance();
  }
  InputStream.prototype.available = function () {
    return 0;
  };
  InputStream.prototype.close = function () {
  };
  InputStream.prototype.read_fqrh44$ = function (b) {
    return this.read_mj6st8$(b, 0, b.length);
  };
  InputStream.prototype.read_mj6st8$ = function (b, offset, len) {
    if (offset > b.length || offset < 0) {
      throw IndexOutOfBoundsException_init();
    }if (len < 0 || len > (b.length - offset | 0)) {
      throw IndexOutOfBoundsException_init();
    }for (var i = 0; i < len; i++) {
      var c = this.read();
      if (c === -1) {
        return i === 0 ? -1 : i;
      }b[offset + i | 0] = toByte(c);
    }
    return len;
  };
  InputStream.prototype.skip_s8cxhz$ = function (n) {
    if (n.toNumber() <= 0) {
      return L0;
    }var skipped = L0;
    var toRead = n.toNumber() < 4096 ? n.toInt() : 4096;
    var localBuf = InputStream$Companion_getInstance().skipBuf_0;
    if (localBuf == null || localBuf.length < toRead) {
      localBuf = new Int8Array(toRead);
      InputStream$Companion_getInstance().skipBuf_0 = localBuf;
    }while (skipped.compareTo_11rb$(n) < 0) {
      var read = this.read_mj6st8$(localBuf, 0, toRead);
      if (read === -1) {
        return skipped;
      }skipped = skipped.add(Kotlin.Long.fromInt(read));
      if (read < toRead) {
        return skipped;
      }if (n.subtract(skipped).toNumber() < toRead) {
        toRead = n.subtract(skipped).toInt();
      }}
    return skipped;
  };
  function InputStream$Companion() {
    InputStream$Companion_instance = this;
    this.skipBuf_0 = null;
  }
  InputStream$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var InputStream$Companion_instance = null;
  function InputStream$Companion_getInstance() {
    if (InputStream$Companion_instance === null) {
      new InputStream$Companion();
    }return InputStream$Companion_instance;
  }
  InputStream.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InputStream',
    interfaces: []
  };
  function ByteArrayInputStream() {
    this.buf_0 = null;
    this.pos_0 = 0;
    this.mark_0 = 0;
    this.count_0 = 0;
  }
  ByteArrayInputStream.prototype.available = function () {
    return this.count_0 - this.pos_0 | 0;
  };
  ByteArrayInputStream.prototype.read = function () {
    var tmp$, tmp$_0;
    if (this.pos_0 < this.count_0) {
      tmp$_0 = this.buf_0[tmp$ = this.pos_0, this.pos_0 = tmp$ + 1 | 0, tmp$] & 255;
    } else
      tmp$_0 = -1;
    return tmp$_0;
  };
  ByteArrayInputStream.prototype.read_nzv2aj$ = function (b, offset, len) {
    if (b == null) {
      throw NullPointerException_init();
    }if (offset < 0 || offset > b.length || len < 0 || len > (b.length - offset | 0)) {
      throw IndexOutOfBoundsException_init();
    }if (this.pos_0 >= this.count_0) {
      return -1;
    }if (len === 0) {
      return 0;
    }var copylen = (this.count_0 - this.pos_0 | 0) < len ? this.count_0 - this.pos_0 | 0 : len;
    arraycopy(this.buf_0, this.pos_0, b, offset, copylen);
    this.pos_0 = this.pos_0 + copylen | 0;
    return copylen;
  };
  ByteArrayInputStream.prototype.skip_s8cxhz$ = function (n) {
    if (n.toNumber() <= 0) {
      return L0;
    }var temp = this.pos_0;
    this.pos_0 = (this.count_0 - this.pos_0 | 0) < n.toNumber() ? this.count_0 : Kotlin.Long.fromInt(this.pos_0).add(n).toInt();
    return Kotlin.Long.fromInt(this.pos_0 - temp | 0);
  };
  ByteArrayInputStream.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayInputStream',
    interfaces: [InputStream]
  };
  function ByteArrayInputStream_init(buf, $this) {
    $this = $this || Object.create(ByteArrayInputStream.prototype);
    InputStream.call($this);
    ByteArrayInputStream.call($this);
    $this.mark_0 = 0;
    $this.buf_0 = buf;
    $this.count_0 = buf.length;
    return $this;
  }
  function ByteArrayInputStream_init_0(buf, offset, length, $this) {
    $this = $this || Object.create(ByteArrayInputStream.prototype);
    InputStream.call($this);
    ByteArrayInputStream.call($this);
    $this.buf_0 = buf;
    $this.pos_0 = offset;
    $this.mark_0 = offset;
    $this.count_0 = (offset + length | 0) > buf.length ? buf.length : offset + length | 0;
    return $this;
  }
  function OutputStream() {
  }
  OutputStream.prototype.close = function () {
  };
  OutputStream.prototype.flush = function () {
  };
  OutputStream.prototype.write_fqrh44$ = function (buffer) {
    this.write_mj6st8$(buffer, 0, buffer.length);
  };
  OutputStream.prototype.write_mj6st8$ = function (buffer, offset, count) {
    var tmp$;
    if (offset > buffer.length || offset < 0 || count < 0 || count > (buffer.length - offset | 0)) {
      throw IndexOutOfBoundsException_init();
    }tmp$ = offset + count - 1 | 0;
    for (var i = offset; i <= tmp$; i++) {
      this.write_za3lpa$(buffer[i]);
    }
  };
  OutputStream.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OutputStream',
    interfaces: []
  };
  function ByteArrayOutputStream() {
    this.buf_0 = null;
    this.count_0 = 0;
  }
  ByteArrayOutputStream.prototype.expand_0 = function (i) {
    if ((this.count_0 + i | 0) <= this.buf_0.length) {
      return;
    }var newbuf = new Int8Array((this.count_0 + i | 0) * 2 | 0);
    arraycopy(this.buf_0, 0, newbuf, 0, this.count_0);
    this.buf_0 = newbuf;
  };
  ByteArrayOutputStream.prototype.size = function () {
    return this.count_0;
  };
  ByteArrayOutputStream.prototype.toByteArray = function () {
    var newArray = new Int8Array(this.count_0);
    arraycopy(this.buf_0, 0, newArray, 0, this.count_0);
    return newArray;
  };
  ByteArrayOutputStream.prototype.write_mj6st8$ = function (buffer, offset, count) {
    if (offset < 0 || offset > buffer.length || count < 0 || count > (buffer.length - offset | 0)) {
      throw IndexOutOfBoundsException_init();
    }if (count === 0) {
      return;
    }this.expand_0(count);
    arraycopy(buffer, offset, this.buf_0, this.count_0, count);
    this.count_0 = this.count_0 + count | 0;
  };
  ByteArrayOutputStream.prototype.write_za3lpa$ = function (oneByte) {
    var tmp$;
    if (this.count_0 === this.buf_0.length) {
      this.expand_0(1);
    }this.buf_0[tmp$ = this.count_0, this.count_0 = tmp$ + 1 | 0, tmp$] = toByte(oneByte);
  };
  ByteArrayOutputStream.prototype.writeTo_tkhtou$ = function (out) {
    out.write_mj6st8$(this.buf_0, 0, this.count_0);
  };
  ByteArrayOutputStream.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayOutputStream',
    interfaces: [OutputStream]
  };
  function ByteArrayOutputStream_init($this) {
    $this = $this || Object.create(ByteArrayOutputStream.prototype);
    OutputStream.call($this);
    ByteArrayOutputStream.call($this);
    $this.buf_0 = new Int8Array(32);
    return $this;
  }
  function ByteArrayOutputStream_init_0(size, $this) {
    $this = $this || Object.create(ByteArrayOutputStream.prototype);
    OutputStream.call($this);
    ByteArrayOutputStream.call($this);
    if (size >= 0) {
      $this.buf_0 = new Int8Array(size);
    } else {
      throw IllegalArgumentException_init_0();
    }
    return $this;
  }
  function arraycopy(src, srcPos, dst, dstPos, len) {
    var tmp$;
    tmp$ = len - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      dst[dstPos + i | 0] = src[srcPos + i | 0];
    }
  }
  var MAX_SAFE_INTEGER;
  function DynamicObjectParser(context, configuration) {
    if (context === void 0)
      context = EmptyModule_getInstance();
    if (configuration === void 0)
      configuration = JsonConfiguration$Companion_getInstance().Default;
    this.context_6c6sdt$_0 = context;
    this.configuration_8be2vx$ = configuration;
  }
  Object.defineProperty(DynamicObjectParser.prototype, 'context', {
    get: function () {
      return this.context_6c6sdt$_0;
    }
  });
  DynamicObjectParser.prototype.parse_pgxeca$ = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-runtime.kotlinx.serialization.DynamicObjectParser.parse_pgxeca$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var getContextualOrDefault = _.kotlinx.serialization.modules.getContextualOrDefault_6za9kt$;
    return function (T_0, isT, obj) {
      return this.parse_tf9272$(obj, getContextualOrDefault(this.context, getKClass(T_0)));
    };
  }));
  DynamicObjectParser.prototype.parse_tf9272$ = function (obj, deserializer) {
    return decode(new DynamicObjectParser$DynamicInput(this, obj), deserializer);
  };
  function DynamicObjectParser$DynamicInput($outer, obj) {
    this.$outer = $outer;
    NamedValueDecoder.call(this);
    this.obj = obj;
    this.pos_0 = 0;
  }
  Object.defineProperty(DynamicObjectParser$DynamicInput.prototype, 'context', {
    get: function () {
      return this.$outer.context;
    }
  });
  DynamicObjectParser$DynamicInput.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return childName;
  };
  DynamicObjectParser$DynamicInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    var tmp$;
    while (this.pos_0 < descriptor.elementsCount) {
      var name = this.getTag_m47q6f$(descriptor, (tmp$ = this.pos_0, this.pos_0 = tmp$ + 1 | 0, tmp$));
      if (this.obj[name] !== undefined)
        return this.pos_0 - 1 | 0;
    }
    return -1;
  };
  DynamicObjectParser$DynamicInput.prototype.decodeTaggedEnum_xicdkz$ = function (tag, enumDescription) {
    var tmp$;
    return getElementIndexOrThrow(enumDescription, typeof (tmp$ = this.getByTag_61zpoe$(tag)) === 'string' ? tmp$ : throwCCE());
  };
  DynamicObjectParser$DynamicInput.prototype.getByTag_61zpoe$ = function (tag) {
    return this.obj[tag];
  };
  DynamicObjectParser$DynamicInput.prototype.decodeTaggedChar_11rb$ = function (tag) {
    var tmp$;
    var o = this.getByTag_61zpoe$(tag);
    if (typeof o === 'string')
      if (o.length === 1)
        tmp$ = o.charCodeAt(0);
      else
        throw new SerializationException(o.toString() + " can't be represented as Char");
    else if (Kotlin.isNumber(o))
      tmp$ = numberToChar(o);
    else
      throw new SerializationException(o.toString() + " can't be represented as Char");
    return toBoxedChar(tmp$);
  };
  DynamicObjectParser$DynamicInput.prototype.decodeTaggedLong_11rb$ = function (tag) {
    var tmp$, tmp$_0;
    var obj = this.getByTag_61zpoe$(tag);
    tmp$_0 = typeof (tmp$ = obj) === 'number' ? tmp$ : null;
    if (tmp$_0 == null) {
      throw new SerializationException(obj.toString() + ' is not a Number');
    }var number = tmp$_0;
    var tmp$_1 = isFinite_0(number);
    if (tmp$_1) {
      tmp$_1 = Math_0.floor(number) === number;
    }var canBeConverted = tmp$_1;
    if (!canBeConverted)
      throw new SerializationException(number.toString() + " can't be represented as Long because it is not finite or has non-zero fractional part");
    var inBound = Math_0.abs(number) <= MAX_SAFE_INTEGER;
    if (!inBound)
      throw new SerializationException(number.toString() + " can't be deserialized to Long due to a potential precision loss");
    return Kotlin.Long.fromNumber(number);
  };
  DynamicObjectParser$DynamicInput.prototype.decodeTaggedValue_11rb$ = function (tag) {
    var tmp$, tmp$_0;
    tmp$ = this.getByTag_61zpoe$(tag);
    if (tmp$ == null) {
      throw new MissingFieldException(tag);
    }var o = tmp$;
    return Kotlin.isType(tmp$_0 = o, Any) ? tmp$_0 : throwCCE();
  };
  DynamicObjectParser$DynamicInput.prototype.decodeTaggedNotNullMark_11rb$ = function (tag) {
    var o = this.getByTag_61zpoe$(tag);
    if (o === undefined)
      throw new MissingFieldException(tag);
    return o != null;
  };
  DynamicObjectParser$DynamicInput.prototype.beginStructure_r0sa6z$ = function (descriptor, typeParams) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var curObj = (tmp$_0 = (tmp$ = this.currentTagOrNull) != null ? this.obj[tmp$] : null) != null ? tmp$_0 : this.obj;
    if (Kotlin.isType(descriptor.kind, PolymorphicKind))
      tmp$_1 = this.$outer.configuration_8be2vx$.useArrayPolymorphism_8be2vx$ ? StructureKind$LIST_getInstance() : StructureKind$MAP_getInstance();
    else
      tmp$_1 = descriptor.kind;
    var kind = tmp$_1;
    if (equals(kind, StructureKind$LIST_getInstance()))
      tmp$_2 = new DynamicObjectParser$DynamicListInput(this.$outer, curObj);
    else if (equals(kind, StructureKind$MAP_getInstance()))
      tmp$_2 = new DynamicObjectParser$DynamicMapInput(this.$outer, curObj);
    else
      tmp$_2 = new DynamicObjectParser$DynamicInput(this.$outer, curObj);
    return tmp$_2;
  };
  DynamicObjectParser$DynamicInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicInput',
    interfaces: [NamedValueDecoder]
  };
  function DynamicObjectParser$DynamicMapInput($outer, obj) {
    this.$outer = $outer;
    DynamicObjectParser$DynamicInput.call(this, this.$outer, obj);
    this.keys_0 = Object.keys(obj);
    var tmp$;
    this.size_0 = (typeof (tmp$ = this.keys_0.length) === 'number' ? tmp$ : throwCCE()) * 2 | 0;
    this.pos_1 = -1;
  }
  DynamicObjectParser$DynamicMapInput.prototype.elementName_3zr2iy$ = function (desc, index) {
    var tmp$;
    var i = index / 2 | 0;
    return typeof (tmp$ = this.keys_0[i]) === 'string' ? tmp$ : throwCCE();
  };
  DynamicObjectParser$DynamicMapInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    var tmp$, tmp$_0;
    while (this.pos_1 < (this.size_0 - 1 | 0)) {
      var i = (tmp$ = this.pos_1, this.pos_1 = tmp$ + 1 | 0, tmp$) / 2 | 0;
      var name = typeof (tmp$_0 = this.keys_0[i]) === 'string' ? tmp$_0 : throwCCE();
      if (this.obj[name] !== undefined)
        return this.pos_1;
    }
    return -1;
  };
  DynamicObjectParser$DynamicMapInput.prototype.getByTag_61zpoe$ = function (tag) {
    return this.pos_1 % 2 === 0 ? tag : this.obj[tag];
  };
  DynamicObjectParser$DynamicMapInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicMapInput',
    interfaces: [DynamicObjectParser$DynamicInput]
  };
  function DynamicObjectParser$DynamicListInput($outer, obj) {
    this.$outer = $outer;
    DynamicObjectParser$DynamicInput.call(this, this.$outer, obj);
    var tmp$;
    this.size_0 = typeof (tmp$ = obj.length) === 'number' ? tmp$ : throwCCE();
    this.pos_1 = -1;
  }
  DynamicObjectParser$DynamicListInput.prototype.elementName_3zr2iy$ = function (desc, index) {
    return index.toString();
  };
  DynamicObjectParser$DynamicListInput.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    while (this.pos_1 < (this.size_0 - 1 | 0)) {
      var o = this.obj[this.pos_1 = this.pos_1 + 1 | 0, this.pos_1];
      if (o !== undefined)
        return this.pos_1;
    }
    return -1;
  };
  DynamicObjectParser$DynamicListInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicListInput',
    interfaces: [DynamicObjectParser$DynamicInput]
  };
  DynamicObjectParser.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicObjectParser',
    interfaces: [SerialFormat]
  };
  function compiledSerializerImpl($receiver) {
    var tmp$, tmp$_0;
    return Kotlin.isType(tmp$_0 = (tmp$ = get_js($receiver).Companion) != null ? tmp$.serializer() : null, KSerializer) ? tmp$_0 : null;
  }
  function toUtf8Bytes($receiver) {
    var tmp$;
    var s = $receiver;
    var block = unescape(encodeURIComponent(s));
    var $receiver_0 = toList_0(typeof (tmp$ = block) === 'string' ? tmp$ : throwCCE());
    var destination = ArrayList_init(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination.add_11rb$(toByte(unboxChar(item) | 0));
    }
    return toByteArray(destination);
  }
  function stringFromUtf8Bytes(bytes) {
    var tmp$;
    var destination = ArrayList_init(bytes.length);
    var tmp$_0;
    for (tmp$_0 = 0; tmp$_0 !== bytes.length; ++tmp$_0) {
      var item = bytes[tmp$_0];
      destination.add_11rb$(toBoxedChar(toChar(item & 255)));
    }
    var s = joinToString(destination, '');
    var ans = decodeURIComponent(escape(s));
    return typeof (tmp$ = ans) === 'string' ? tmp$ : throwCCE();
  }
  function enumFromName(enumClass, value) {
    var tmp$;
    return Kotlin.isType(tmp$ = get_js(enumClass).valueOf_61zpoe$(value), Enum) ? tmp$ : throwCCE();
  }
  function enumFromOrdinal(enumClass, ordinal) {
    var tmp$;
    return (Kotlin.isArray(tmp$ = get_js(enumClass).values()) ? tmp$ : throwCCE())[ordinal];
  }
  function enumClassName($receiver) {
    return get_js($receiver).name;
  }
  function enumMembers($receiver) {
    var tmp$;
    return Kotlin.isArray(tmp$ = get_js($receiver).values()) ? tmp$ : throwCCE();
  }
  function toNativeArrayImpl($receiver, eClass) {
    return copyToArray($receiver);
  }
  function isInstanceOf($receiver, kclass) {
    return kclass.isInstance_s8jyv4$($receiver);
  }
  function simpleName_0($receiver) {
    return $receiver.simpleName;
  }
  function constructSerializerForGivenTypeArgs($receiver, args) {
    throw new NotImplementedError('This method is not supported for Kotlin/JS yet. Please provide serializer explicitly.');
  }
  function isReferenceArray(type, rootClass) {
    var tmp$;
    var typeParameters = type.arguments;
    if (typeParameters.size !== 1)
      return false;
    var parameter = single_0(typeParameters);
    var tmp$_0;
    if ((tmp$ = parameter.variance) != null)
      tmp$_0 = tmp$;
    else {
      throw IllegalStateException_init(('Star projections are forbidden: ' + type).toString());
    }
    var variance = tmp$_0;
    if (parameter.type == null) {
      throw IllegalStateException_init(('Star projections are forbidden: ' + type).toString());
    }var prefix = variance === KVariance.IN || variance === KVariance.OUT ? variance.toString().toLowerCase() + ' ' : '';
    var parameterName = prefix + toString(parameter.type);
    var expectedName = 'Array<' + parameterName + '>';
    if (!equals(type.toString(), expectedName)) {
      return false;
    }return true;
  }
  function getChecked($receiver, index) {
    if (!get_indices($receiver).contains_mef7kx$(index))
      throw new IndexOutOfBoundsException('Index ' + index + ' out of bounds ' + get_indices($receiver));
    return $receiver[index];
  }
  function getChecked_0($receiver, index) {
    if (!get_indices_0($receiver).contains_mef7kx$(index))
      throw new IndexOutOfBoundsException('Index ' + index + ' out of bounds ' + get_indices_0($receiver));
    return $receiver[index];
  }
  Object.defineProperty(ByteOrder, 'LITTLE_ENDIAN', {
    get: ByteOrder$LITTLE_ENDIAN_getInstance
  });
  Object.defineProperty(ByteOrder, 'BIG_ENDIAN', {
    get: ByteOrder$BIG_ENDIAN_getInstance
  });
  var package$kotlinx = _.kotlinx || (_.kotlinx = {});
  var package$io = package$kotlinx.io || (package$kotlinx.io = {});
  package$io.ByteOrder = ByteOrder;
  Object.defineProperty(package$io, 'message_8be2vx$', {
    get: function () {
      return message;
    }
  });
  package$io.Writer = Writer;
  package$io.PrintWriter = PrintWriter;
  package$io.StringWriter = StringWriter;
  package$io.Reader = Reader;
  package$io.StringReader = StringReader;
  var package$serialization = package$kotlinx.serialization || (package$kotlinx.serialization = {});
  package$serialization.Serializable = Serializable;
  package$serialization.Serializer = Serializer;
  package$serialization.SerialName = SerialName;
  package$serialization.Optional = Optional;
  package$serialization.Required = Required;
  package$serialization.Transient = Transient;
  package$serialization.SerialInfo = SerialInfo;
  package$serialization.ContextualSerialization = ContextualSerialization;
  package$serialization.UseSerializers = UseSerializers;
  package$serialization.Polymorphic = Polymorphic;
  package$serialization.InternalSerializationApi = InternalSerializationApi;
  package$serialization.ContextSerializer = ContextSerializer;
  package$serialization.Decoder = Decoder;
  Object.defineProperty(CompositeDecoder, 'Companion', {
    get: CompositeDecoder$Companion_getInstance
  });
  package$serialization.CompositeDecoder = CompositeDecoder;
  package$serialization.decode_cmswi7$ = decode;
  package$serialization.serializer_1yb8b7$ = serializer;
  package$serialization.decodeStructure_s55izo$ = decodeStructure;
  package$serialization.Encoder = Encoder;
  package$serialization.CompositeEncoder = CompositeEncoder;
  package$serialization.encode_dt3ugd$ = encode;
  $$importsForInline$$['kotlinx-serialization-kotlinx-serialization-runtime'] = _;
  package$serialization.encodeStructure_2rdqf3$ = encodeStructure;
  package$serialization.SerializationException = SerializationException;
  package$serialization.MissingFieldException = MissingFieldException;
  package$serialization.UnknownFieldException = UnknownFieldException;
  package$serialization.UpdateNotSupportedException = UpdateNotSupportedException;
  package$serialization.KSerializer = KSerializer;
  package$serialization.SerializationStrategy = SerializationStrategy;
  package$serialization.DeserializationStrategy = DeserializationStrategy;
  Object.defineProperty(UpdateMode, 'BANNED', {
    get: UpdateMode$BANNED_getInstance
  });
  Object.defineProperty(UpdateMode, 'OVERWRITE', {
    get: UpdateMode$OVERWRITE_getInstance
  });
  Object.defineProperty(UpdateMode, 'UPDATE', {
    get: UpdateMode$UPDATE_getInstance
  });
  package$serialization.UpdateMode = UpdateMode;
  package$serialization.PrimitiveDescriptorWithName = PrimitiveDescriptorWithName;
  package$serialization.withName_27vawp$ = withName;
  package$serialization.get_nullable_2418p6$ = get_nullable;
  package$serialization.get_list_gekvwj$ = get_list;
  package$serialization.get_set_gekvwj$ = get_set;
  package$serialization.get_map_kgqhr1$ = get_map;
  package$serialization.compiledSerializer_1yb8b7$ = compiledSerializer;
  package$serialization.toNativeArray_9mvb00$ = toNativeArray;
  package$serialization.Mapper = Mapper;
  package$serialization.SerialId = SerialId;
  package$serialization.serializerOrNull_1yb8b7$ = serializerOrNull;
  Object.defineProperty(package$serialization, 'PolymorphicClassDescriptor', {
    get: function () {
      return PolymorphicClassDescriptor;
    }
  });
  package$serialization.PolymorphicSerializer = PolymorphicSerializer;
  package$serialization.SealedClassSerializer = SealedClassSerializer;
  package$serialization.SerialDescriptor = SerialDescriptor;
  package$serialization.elementDescriptors_xzf193$ = elementDescriptors;
  package$serialization.elementNames_xzf193$ = elementNames;
  package$serialization.getElementIndexOrThrow_27vawp$ = getElementIndexOrThrow;
  package$serialization.SerialDescriptor_dhifv3$ = SerialDescriptor_0;
  package$serialization.PrimitiveDescriptor_87l9oo$ = PrimitiveDescriptor;
  package$serialization.get_nullable_xzf193$ = get_nullable_0;
  package$serialization.SerialDescriptorBuilder = SerialDescriptorBuilder;
  package$serialization.listDescriptor_if675o$ = listDescriptor;
  package$serialization.mapDescriptor_9axtvk$ = mapDescriptor;
  package$serialization.setDescriptor_if675o$ = setDescriptor;
  package$serialization.SerialFormat = SerialFormat;
  package$serialization.AbstractSerialFormat = AbstractSerialFormat;
  package$serialization.BinaryFormat = BinaryFormat;
  package$serialization.dumps_4yxkwp$ = dumps;
  package$serialization.loads_f786sb$ = loads;
  package$serialization.StringFormat = StringFormat;
  package$serialization.ImplicitReflectionSerializer = ImplicitReflectionSerializer;
  package$serialization.UnstableDefault = UnstableDefault;
  var package$modules = package$serialization.modules || (package$serialization.modules = {});
  package$modules.getContextualOrDefault_6za9kt$ = getContextualOrDefault;
  var package$builtins = package$serialization.builtins || (package$serialization.builtins = {});
  package$builtins.get_list_gekvwj$ = get_list_0;
  package$builtins.MapSerializer_2yqygg$ = MapSerializer;
  package$serialization.SerialKind = SerialKind;
  Object.defineProperty(PrimitiveKind, 'BOOLEAN', {
    get: PrimitiveKind$BOOLEAN_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'BYTE', {
    get: PrimitiveKind$BYTE_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'CHAR', {
    get: PrimitiveKind$CHAR_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'SHORT', {
    get: PrimitiveKind$SHORT_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'INT', {
    get: PrimitiveKind$INT_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'LONG', {
    get: PrimitiveKind$LONG_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'FLOAT', {
    get: PrimitiveKind$FLOAT_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'DOUBLE', {
    get: PrimitiveKind$DOUBLE_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'UNIT', {
    get: PrimitiveKind$UNIT_getInstance
  });
  Object.defineProperty(PrimitiveKind, 'STRING', {
    get: PrimitiveKind$STRING_getInstance
  });
  package$serialization.PrimitiveKind = PrimitiveKind;
  Object.defineProperty(StructureKind, 'CLASS', {
    get: StructureKind$CLASS_getInstance
  });
  Object.defineProperty(StructureKind, 'LIST', {
    get: StructureKind$LIST_getInstance
  });
  Object.defineProperty(StructureKind, 'MAP', {
    get: StructureKind$MAP_getInstance
  });
  Object.defineProperty(StructureKind, 'OBJECT', {
    get: StructureKind$OBJECT_getInstance
  });
  package$serialization.StructureKind = StructureKind;
  Object.defineProperty(UnionKind, 'ENUM_KIND', {
    get: UnionKind$ENUM_KIND_getInstance
  });
  Object.defineProperty(UnionKind, 'CONTEXTUAL', {
    get: UnionKind$CONTEXTUAL_getInstance
  });
  Object.defineProperty(UnionKind, 'Companion', {
    get: UnionKind$Companion_getInstance
  });
  package$serialization.UnionKind = UnionKind;
  Object.defineProperty(PolymorphicKind, 'SEALED', {
    get: PolymorphicKind$SEALED_getInstance
  });
  Object.defineProperty(PolymorphicKind, 'OPEN', {
    get: PolymorphicKind$OPEN_getInstance
  });
  package$serialization.PolymorphicKind = PolymorphicKind;
  package$serialization.serializer_saj79j$ = serializer_1;
  var package$internal = package$serialization.internal || (package$serialization.internal = {});
  package$internal.cast_irzu8f$ = cast;
  package$builtins.AbstractDecoder = AbstractDecoder;
  package$builtins.AbstractEncoder = AbstractEncoder;
  package$builtins.get_nullable_2418p6$ = get_nullable_1;
  package$builtins.PairSerializer_2yqygg$ = PairSerializer;
  package$builtins.MapEntrySerializer_2yqygg$ = MapEntrySerializer;
  package$builtins.TripleSerializer_jww85o$ = TripleSerializer;
  package$builtins.ListSerializer_swdriu$ = ListSerializer;
  package$builtins.get_set_gekvwj$ = get_set_0;
  package$builtins.SetSerializer_swdriu$ = SetSerializer;
  package$builtins.serializer_n24eoe$ = serializer_2;
  package$builtins.CharArraySerializer = CharArraySerializer;
  package$builtins.serializer_k5zfx8$ = serializer_3;
  package$builtins.ByteArraySerializer = ByteArraySerializer;
  package$builtins.serializer_qetqea$ = serializer_4;
  package$builtins.ShortArraySerializer = ShortArraySerializer;
  package$builtins.serializer_qn7glr$ = serializer_5;
  package$builtins.IntArraySerializer = IntArraySerializer;
  package$builtins.serializer_vbrujs$ = serializer_6;
  package$builtins.LongArraySerializer = LongArraySerializer;
  package$builtins.serializer_y9phqa$ = serializer_7;
  package$builtins.FloatArraySerializer = FloatArraySerializer;
  package$builtins.serializer_6a53gt$ = serializer_8;
  package$builtins.DoubleArraySerializer = DoubleArraySerializer;
  package$builtins.serializer_jtjczu$ = serializer_9;
  package$builtins.BooleanArraySerializer = BooleanArraySerializer;
  package$builtins.UnitSerializer = UnitSerializer;
  package$builtins.serializer_6eet4j$ = serializer_10;
  package$builtins.ArraySerializer_8tn5u0$ = ArraySerializer_0;
  package$internal.AbstractPolymorphicSerializer = AbstractPolymorphicSerializer;
  package$internal.throwSubtypeNotRegistered_zgnrn5$ = throwSubtypeNotRegistered_0;
  package$internal.ListLikeDescriptor = ListLikeDescriptor;
  package$internal.MapLikeDescriptor = MapLikeDescriptor;
  Object.defineProperty(package$internal, 'ARRAY_NAME_8be2vx$', {
    get: function () {
      return ARRAY_NAME;
    }
  });
  Object.defineProperty(package$internal, 'ARRAY_LIST_NAME_8be2vx$', {
    get: function () {
      return ARRAY_LIST_NAME;
    }
  });
  Object.defineProperty(package$internal, 'LINKED_HASH_SET_NAME_8be2vx$', {
    get: function () {
      return LINKED_HASH_SET_NAME;
    }
  });
  Object.defineProperty(package$internal, 'HASH_SET_NAME_8be2vx$', {
    get: function () {
      return HASH_SET_NAME;
    }
  });
  Object.defineProperty(package$internal, 'LINKED_HASH_MAP_NAME_8be2vx$', {
    get: function () {
      return LINKED_HASH_MAP_NAME;
    }
  });
  Object.defineProperty(package$internal, 'HASH_MAP_NAME_8be2vx$', {
    get: function () {
      return HASH_MAP_NAME;
    }
  });
  package$internal.PrimitiveArrayDescriptor = PrimitiveArrayDescriptor;
  package$internal.ArrayClassDesc = ArrayClassDesc;
  package$internal.ArrayListClassDesc = ArrayListClassDesc;
  package$internal.NamedListClassDescriptor = NamedListClassDescriptor;
  package$internal.LinkedHashSetClassDesc = LinkedHashSetClassDesc;
  package$internal.HashSetClassDesc = HashSetClassDesc;
  package$internal.NamedMapClassDescriptor = NamedMapClassDescriptor;
  package$internal.LinkedHashMapClassDesc = LinkedHashMapClassDesc;
  package$internal.HashMapClassDesc = HashMapClassDesc;
  package$internal.AbstractCollectionSerializer = AbstractCollectionSerializer;
  package$internal.ListLikeSerializer = ListLikeSerializer;
  package$internal.MapLikeSerializer = MapLikeSerializer;
  package$internal.PrimitiveArrayBuilder = PrimitiveArrayBuilder;
  package$internal.PrimitiveArraySerializer = PrimitiveArraySerializer;
  package$internal.ReferenceArraySerializer = ReferenceArraySerializer;
  package$internal.ArrayListSerializer = ArrayListSerializer;
  package$internal.LinkedHashSetSerializer = LinkedHashSetSerializer;
  package$internal.HashSetSerializer = HashSetSerializer;
  package$internal.LinkedHashMapSerializer = LinkedHashMapSerializer;
  package$internal.HashMapSerializer = HashMapSerializer;
  package$internal.EnumDescriptor = EnumDescriptor;
  package$internal.EnumSerializer = EnumSerializer;
  Object.defineProperty(package$internal, 'LongAsStringSerializer', {
    get: LongAsStringSerializer_getInstance
  });
  package$internal.makeNullable_ewacr1$ = makeNullable;
  package$internal.NullableSerializer = NullableSerializer;
  package$internal.SerialDescriptorForNullable = SerialDescriptorForNullable;
  package$internal.ObjectSerializer = ObjectSerializer;
  package$internal.PluginGeneratedSerialDescriptor = PluginGeneratedSerialDescriptor;
  package$internal.GeneratedSerializer = GeneratedSerializer;
  package$internal.SerializerFactory = SerializerFactory;
  Object.defineProperty(package$internal, 'ByteArraySerializer', {
    get: ByteArraySerializer_getInstance
  });
  package$internal.ByteArrayBuilder = ByteArrayBuilder;
  Object.defineProperty(package$internal, 'ShortArraySerializer', {
    get: ShortArraySerializer_getInstance
  });
  package$internal.ShortArrayBuilder = ShortArrayBuilder;
  Object.defineProperty(package$internal, 'IntArraySerializer', {
    get: IntArraySerializer_getInstance
  });
  package$internal.IntArrayBuilder = IntArrayBuilder;
  Object.defineProperty(package$internal, 'LongArraySerializer', {
    get: LongArraySerializer_getInstance
  });
  package$internal.LongArrayBuilder = LongArrayBuilder;
  Object.defineProperty(package$internal, 'FloatArraySerializer', {
    get: FloatArraySerializer_getInstance
  });
  package$internal.FloatArrayBuilder = FloatArrayBuilder;
  Object.defineProperty(package$internal, 'DoubleArraySerializer', {
    get: DoubleArraySerializer_getInstance
  });
  package$internal.DoubleArrayBuilder = DoubleArrayBuilder;
  Object.defineProperty(package$internal, 'CharArraySerializer', {
    get: CharArraySerializer_getInstance
  });
  package$internal.CharArrayBuilder = CharArrayBuilder;
  Object.defineProperty(package$internal, 'BooleanArraySerializer', {
    get: BooleanArraySerializer_getInstance
  });
  package$internal.BooleanArrayBuilder = BooleanArrayBuilder;
  package$internal.PrimitiveSerialDescriptor = PrimitiveSerialDescriptor;
  package$internal.PrimitiveDescriptorSafe_r8sqth$ = PrimitiveDescriptorSafe;
  package$internal.builtinSerializerOrNull_beh9s$ = builtinSerializerOrNull;
  Object.defineProperty(package$internal, 'UnitSerializer', {
    get: UnitSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'BooleanSerializer', {
    get: BooleanSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'ByteSerializer', {
    get: ByteSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'ShortSerializer', {
    get: ShortSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'IntSerializer', {
    get: IntSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'LongSerializer', {
    get: LongSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'FloatSerializer', {
    get: FloatSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'DoubleSerializer', {
    get: DoubleSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'CharSerializer', {
    get: CharSerializer_getInstance
  });
  Object.defineProperty(package$internal, 'StringSerializer', {
    get: StringSerializer_getInstance
  });
  package$internal.Migration = Migration;
  Object.defineProperty(package$internal, 'IntDescriptor', {
    get: IntDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'UnitDescriptor', {
    get: UnitDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'BooleanDescriptor', {
    get: BooleanDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'ByteDescriptor', {
    get: ByteDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'ShortDescriptor', {
    get: ShortDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'LongDescriptor', {
    get: LongDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'FloatDescriptor', {
    get: FloatDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'DoubleDescriptor', {
    get: DoubleDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'CharDescriptor', {
    get: CharDescriptor_getInstance
  });
  Object.defineProperty(package$internal, 'StringDescriptor', {
    get: StringDescriptor_getInstance
  });
  package$internal.SerialClassDescImpl = SerialClassDescImpl;
  package$internal.SerializationConstructorMarker = SerializationConstructorMarker;
  package$serialization.SerializationConstructorMarker = SerializationConstructorMarker_0;
  Object.defineProperty(package$internal, 'unitDeprecated_8be2vx$', {
    get: function () {
      return unitDeprecated;
    }
  });
  package$internal.TaggedEncoder = TaggedEncoder;
  package$internal.NamedValueEncoder = NamedValueEncoder;
  package$internal.TaggedDecoder = TaggedDecoder;
  package$internal.NamedValueDecoder = NamedValueDecoder;
  package$internal.KeyValueSerializer = KeyValueSerializer;
  package$internal.MapEntrySerializer = MapEntrySerializer_0;
  package$internal.PairSerializer = PairSerializer_0;
  package$internal.TripleSerializer = TripleSerializer_0;
  package$internal.readExactNBytes_5u4fs$ = readExactNBytes;
  Object.defineProperty(package$internal, 'InternalHexConverter', {
    get: InternalHexConverter_getInstance
  });
  Object.defineProperty(package$internal, 'HexConverter', {
    get: HexConverter_getInstance
  });
  package$internal.cachedSerialNames_8dyrhi$ = cachedSerialNames;
  package$internal.defer_y1xn0x$ = defer;
  Object.defineProperty(Json, 'Default', {
    get: Json$Default_getInstance
  });
  var package$json = package$serialization.json || (package$serialization.json = {});
  package$json.Json_init_8bzpyt$ = Json_init;
  package$json.Json_init = Json_init_0;
  package$json.Json = Json;
  package$json.JsonBuilder = JsonBuilder;
  Object.defineProperty(package$json, 'lenientHint_8be2vx$', {
    get: function () {
      return lenientHint;
    }
  });
  Object.defineProperty(JsonConfiguration, 'Companion', {
    get: JsonConfiguration$Companion_getInstance
  });
  package$json.JsonConfiguration = JsonConfiguration;
  package$json.JsonConfiguration_dqye30$ = JsonConfiguration_0;
  Object.defineProperty(JsonElement, 'Companion', {
    get: JsonElement$Companion_getInstance
  });
  package$json.JsonElement = JsonElement;
  Object.defineProperty(JsonPrimitive, 'Companion', {
    get: JsonPrimitive$Companion_getInstance
  });
  package$json.JsonPrimitive = JsonPrimitive;
  Object.defineProperty(JsonLiteral, 'Companion', {
    get: JsonLiteral$Companion_getInstance
  });
  package$json.JsonLiteral_init_3p81yu$ = JsonLiteral_init;
  package$json.JsonLiteral_init_6taknv$ = JsonLiteral_init_0;
  package$json.JsonLiteral_init_61zpoe$ = JsonLiteral_init_1;
  package$json.JsonLiteral = JsonLiteral;
  Object.defineProperty(package$json, 'JsonNull', {
    get: JsonNull_getInstance
  });
  package$json.unexpectedJson_puj7f4$ = unexpectedJson;
  Object.defineProperty(JsonObject, 'Companion', {
    get: JsonObject$Companion_getInstance
  });
  package$json.JsonObject = JsonObject;
  Object.defineProperty(JsonArray, 'Companion', {
    get: JsonArray$Companion_getInstance
  });
  package$json.JsonArray = JsonArray;
  package$json.json_s5o6vg$ = json;
  package$json.jsonArray_mb52fq$ = jsonArray;
  package$json.JsonArrayBuilder = JsonArrayBuilder;
  package$json.JsonObjectBuilder = JsonObjectBuilder;
  Object.defineProperty(package$json, 'JsonElementSerializer', {
    get: JsonElementSerializer_getInstance
  });
  Object.defineProperty(package$json, 'JsonPrimitiveSerializer', {
    get: JsonPrimitiveSerializer_getInstance
  });
  Object.defineProperty(package$json, 'JsonNullSerializer', {
    get: JsonNullSerializer_getInstance
  });
  Object.defineProperty(package$json, 'JsonLiteralSerializer', {
    get: JsonLiteralSerializer_getInstance
  });
  Object.defineProperty(package$json, 'JsonObjectSerializer', {
    get: JsonObjectSerializer_getInstance
  });
  Object.defineProperty(package$json, 'JsonArraySerializer', {
    get: JsonArraySerializer_getInstance
  });
  package$json.asJsonInput_q34bbx$ = asJsonInput;
  package$json.asJsonOutput_q30tiz$ = asJsonOutput;
  package$json.JsonPrimitive_1v8dbw$ = JsonPrimitive_0;
  package$json.JsonPrimitive_rcaewn$ = JsonPrimitive_1;
  package$json.JsonPrimitive_pdl1vj$ = JsonPrimitive_2;
  package$json.get_int_u3sd3g$ = get_int;
  package$json.get_intOrNull_u3sd3g$ = get_intOrNull;
  package$json.get_long_u3sd3g$ = get_long;
  package$json.get_longOrNull_u3sd3g$ = get_longOrNull;
  package$json.get_double_u3sd3g$ = get_double;
  package$json.get_doubleOrNull_u3sd3g$ = get_doubleOrNull;
  package$json.get_float_u3sd3g$ = get_float;
  package$json.get_floatOrNull_u3sd3g$ = get_floatOrNull;
  package$json.get_boolean_u3sd3g$ = get_boolean;
  package$json.get_booleanOrNull_u3sd3g$ = get_booleanOrNull;
  package$json.get_content_u3sd3g$ = get_content;
  package$json.get_contentOrNull_u3sd3g$ = get_contentOrNull;
  package$json.JsonException = JsonException;
  package$json.JsonDecodingException = JsonDecodingException;
  package$json.JsonEncodingException = JsonEncodingException;
  package$json.JsonDecodingException_kx378j$ = JsonDecodingException_0;
  package$json.InvalidFloatingPoint_x0xb19$ = InvalidFloatingPoint;
  package$json.InvalidFloatingPoint_puwk29$ = InvalidFloatingPoint_0;
  package$json.UnknownKeyException_wdz5eb$ = UnknownKeyException;
  package$json.InvalidKeyKindException_4b8fhx$ = InvalidKeyKindException;
  package$json.JsonInput = JsonInput;
  package$json.JsonOutput = JsonOutput;
  package$json.JsonParametricSerializer = JsonParametricSerializer;
  package$json.JsonTransformingSerializer = JsonTransformingSerializer;
  var package$internal_0 = package$json.internal || (package$json.internal = {});
  package$internal_0.ContextValidator = ContextValidator;
  package$internal_0.JsonParser = JsonParser;
  Object.defineProperty(package$internal_0, 'NULL_8be2vx$', {
    get: function () {
      return NULL_0;
    }
  });
  Object.defineProperty(package$internal_0, 'COMMA_8be2vx$', {
    get: function () {
      return COMMA;
    }
  });
  Object.defineProperty(package$internal_0, 'COLON_8be2vx$', {
    get: function () {
      return COLON;
    }
  });
  Object.defineProperty(package$internal_0, 'BEGIN_OBJ_8be2vx$', {
    get: function () {
      return BEGIN_OBJ;
    }
  });
  Object.defineProperty(package$internal_0, 'END_OBJ_8be2vx$', {
    get: function () {
      return END_OBJ;
    }
  });
  Object.defineProperty(package$internal_0, 'BEGIN_LIST_8be2vx$', {
    get: function () {
      return BEGIN_LIST;
    }
  });
  Object.defineProperty(package$internal_0, 'END_LIST_8be2vx$', {
    get: function () {
      return END_LIST;
    }
  });
  Object.defineProperty(package$internal_0, 'STRING_8be2vx$', {
    get: function () {
      return STRING;
    }
  });
  Object.defineProperty(package$internal_0, 'STRING_ESC_8be2vx$', {
    get: function () {
      return STRING_ESC;
    }
  });
  Object.defineProperty(package$internal_0, 'INVALID_8be2vx$', {
    get: function () {
      return INVALID;
    }
  });
  Object.defineProperty(package$internal_0, 'UNICODE_ESC_8be2vx$', {
    get: function () {
      return UNICODE_ESC;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_OTHER_8be2vx$', {
    get: function () {
      return TC_OTHER;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_STRING_8be2vx$', {
    get: function () {
      return TC_STRING;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_STRING_ESC_8be2vx$', {
    get: function () {
      return TC_STRING_ESC;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_WS_8be2vx$', {
    get: function () {
      return TC_WS;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_COMMA_8be2vx$', {
    get: function () {
      return TC_COMMA;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_COLON_8be2vx$', {
    get: function () {
      return TC_COLON;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_BEGIN_OBJ_8be2vx$', {
    get: function () {
      return TC_BEGIN_OBJ;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_END_OBJ_8be2vx$', {
    get: function () {
      return TC_END_OBJ;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_BEGIN_LIST_8be2vx$', {
    get: function () {
      return TC_BEGIN_LIST;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_END_LIST_8be2vx$', {
    get: function () {
      return TC_END_LIST;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_NULL_8be2vx$', {
    get: function () {
      return TC_NULL;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_INVALID_8be2vx$', {
    get: function () {
      return TC_INVALID;
    }
  });
  Object.defineProperty(package$internal_0, 'TC_EOF_8be2vx$', {
    get: function () {
      return TC_EOF;
    }
  });
  Object.defineProperty(package$internal_0, 'C2TC_8be2vx$', {
    get: function () {
      return C2TC;
    }
  });
  Object.defineProperty(package$internal_0, 'EscapeCharMappings', {
    get: EscapeCharMappings_getInstance
  });
  package$internal_0.charToTokenClass_8e8zqy$ = charToTokenClass;
  package$internal_0.escapeToChar_kcn2v3$ = escapeToChar;
  package$internal_0.JsonReader = JsonReader;
  package$internal_0.checkKind_5g0uu2$ = checkKind;
  package$internal_0.encodePolymorphically_7qn3k2$ = encodePolymorphically;
  package$internal_0.decodeSerializableValuePolymorphic_ojldma$ = decodeSerializableValuePolymorphic;
  package$internal_0.StreamingJsonInput = StreamingJsonInput;
  StreamingJsonOutput.Composer = StreamingJsonOutput$Composer;
  package$internal_0.StreamingJsonOutput_init_ek5ogp$ = StreamingJsonOutput_init;
  package$internal_0.StreamingJsonOutput = StreamingJsonOutput;
  package$internal_0.printQuoted_jigvc$ = printQuoted;
  package$internal_0.toBooleanStrict_7efafi$ = toBooleanStrict;
  package$internal_0.toBooleanStrictOrNull_7efafi$ = toBooleanStrictOrNull;
  package$internal_0.shouldBeQuoted_y4putb$ = shouldBeQuoted;
  package$internal_0.readJson_ijhaef$ = readJson;
  package$internal_0.writeJson_4dixew$ = writeJson;
  Object.defineProperty(package$internal_0, 'PRIMITIVE_TAG_8be2vx$', {
    get: function () {
      return PRIMITIVE_TAG;
    }
  });
  Object.defineProperty(WriteMode, 'OBJ', {
    get: WriteMode$OBJ_getInstance
  });
  Object.defineProperty(WriteMode, 'LIST', {
    get: WriteMode$LIST_getInstance
  });
  Object.defineProperty(WriteMode, 'MAP', {
    get: WriteMode$MAP_getInstance
  });
  Object.defineProperty(WriteMode, 'POLY_OBJ', {
    get: WriteMode$POLY_OBJ_getInstance
  });
  package$internal_0.WriteMode = WriteMode;
  package$internal_0.switchMode_2zz6bv$ = switchMode;
  package$internal_0.selectMapMode_adhkjx$ = selectMapMode;
  package$modules.PolymorphicModuleBuilder = PolymorphicModuleBuilder;
  package$modules.SerialModule = SerialModule;
  Object.defineProperty(package$modules, 'EmptyModule', {
    get: EmptyModule_getInstance
  });
  package$modules.serializersModuleOf_cfhkba$ = serializersModuleOf;
  package$modules.serializersModuleOf_azm104$ = serializersModuleOf_0;
  package$modules.SerializersModule_q4tcel$ = SerializersModule;
  package$modules.SerializersModuleBuilder = SerializersModuleBuilder;
  package$modules.SerialModuleCollector = SerialModuleCollector;
  package$modules.getContextual_2t8chm$ = getContextual_0;
  package$modules.getContextualOrDefault_2t8chm$ = getContextualOrDefault_0;
  package$modules.plus_7n7cf$ = plus;
  package$modules.overwriteWith_7n7cf$ = overwriteWith;
  package$modules.SerialModuleImpl = SerialModuleImpl;
  package$modules.SerializerAlreadyRegisteredException_init_gfgaic$ = SerializerAlreadyRegisteredException_init;
  package$modules.SerializerAlreadyRegisteredException_init_xo1ogr$ = SerializerAlreadyRegisteredException_init_0;
  package$modules.SerializerAlreadyRegisteredException = SerializerAlreadyRegisteredException;
  Object.defineProperty(package$modules, 'StandardSubtypesOfAny', {
    get: StandardSubtypesOfAny_getInstance
  });
  Object.defineProperty(ByteBuffer, 'Companion', {
    get: ByteBuffer$Companion_getInstance
  });
  package$io.ByteBuffer = ByteBuffer;
  package$io.IOException_init = IOException_init;
  package$io.IOException = IOException;
  Object.defineProperty(InputStream, 'Companion', {
    get: InputStream$Companion_getInstance
  });
  package$io.InputStream = InputStream;
  package$io.ByteArrayInputStream_init_fqrh44$ = ByteArrayInputStream_init;
  package$io.ByteArrayInputStream_init_mj6st8$ = ByteArrayInputStream_init_0;
  package$io.ByteArrayInputStream = ByteArrayInputStream;
  package$io.OutputStream = OutputStream;
  package$io.ByteArrayOutputStream_init = ByteArrayOutputStream_init;
  package$io.ByteArrayOutputStream_init_za3lpa$ = ByteArrayOutputStream_init_0;
  package$io.ByteArrayOutputStream = ByteArrayOutputStream;
  package$io.arraycopy_lwkm2r$ = arraycopy;
  Object.defineProperty(package$serialization, 'MAX_SAFE_INTEGER_8be2vx$', {
    get: function () {
      return MAX_SAFE_INTEGER;
    }
  });
  package$serialization.DynamicObjectParser = DynamicObjectParser;
  package$serialization.compiledSerializerImpl_beh9s$ = compiledSerializerImpl;
  package$serialization.toUtf8Bytes_pdl1vz$ = toUtf8Bytes;
  package$serialization.stringFromUtf8Bytes_fqrh44$ = stringFromUtf8Bytes;
  package$serialization.enumFromName_nim6t3$ = enumFromName;
  package$serialization.enumFromOrdinal_szifu5$ = enumFromOrdinal;
  package$serialization.enumClassName_49fzt8$ = enumClassName;
  package$serialization.enumMembers_49fzt8$ = enumMembers;
  package$serialization.toNativeArrayImpl_wfz7v1$ = toNativeArrayImpl;
  package$serialization.isInstanceOf_ofcvxk$ = isInstanceOf;
  package$serialization.simpleName_beh9s$ = simpleName_0;
  package$serialization.constructSerializerForGivenTypeArgs_f7nown$ = constructSerializerForGivenTypeArgs;
  package$serialization.isReferenceArray_3m2qt6$ = isReferenceArray;
  package$internal.getChecked_4bqw6o$ = getChecked;
  package$internal.getChecked_3zu3yo$ = getChecked_0;
  ContextSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  AbstractPolymorphicSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(SerialDescriptorImpl.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  SerialDescriptorImpl.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  AbstractDecoder.prototype.decodeSerializableValue_w63s0f$ = Decoder.prototype.decodeSerializableValue_w63s0f$;
  AbstractDecoder.prototype.decodeNullableSerializableValue_aae3ea$ = Decoder.prototype.decodeNullableSerializableValue_aae3ea$;
  AbstractDecoder.prototype.updateSerializableValue_19c8k5$ = Decoder.prototype.updateSerializableValue_19c8k5$;
  AbstractDecoder.prototype.updateNullableSerializableValue_exmlbs$ = Decoder.prototype.updateNullableSerializableValue_exmlbs$;
  AbstractDecoder.prototype.decodeSequentially = CompositeDecoder.prototype.decodeSequentially;
  AbstractDecoder.prototype.decodeCollectionSize_qatsm0$ = CompositeDecoder.prototype.decodeCollectionSize_qatsm0$;
  AbstractEncoder.prototype.encodeNotNullMark = Encoder.prototype.encodeNotNullMark;
  AbstractEncoder.prototype.beginCollection_gly1x5$ = Encoder.prototype.beginCollection_gly1x5$;
  AbstractEncoder.prototype.encodeSerializableValue_tf03ej$ = Encoder.prototype.encodeSerializableValue_tf03ej$;
  AbstractEncoder.prototype.encodeNullableSerializableValue_f4686g$ = Encoder.prototype.encodeNullableSerializableValue_f4686g$;
  AbstractEncoder.prototype.shouldEncodeElementDefault_3zr2iy$ = CompositeEncoder.prototype.shouldEncodeElementDefault_3zr2iy$;
  AbstractEncoder.prototype.encodeNonSerializableElement_4wpkd1$ = CompositeEncoder.prototype.encodeNonSerializableElement_4wpkd1$;
  Object.defineProperty(ListLikeDescriptor.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  Object.defineProperty(ListLikeDescriptor.prototype, 'isNullable', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'isNullable'));
  Object.defineProperty(ListLikeDescriptor.prototype, 'annotations', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'annotations'));
  ListLikeDescriptor.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  Object.defineProperty(MapLikeDescriptor.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  Object.defineProperty(MapLikeDescriptor.prototype, 'isNullable', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'isNullable'));
  Object.defineProperty(MapLikeDescriptor.prototype, 'annotations', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'annotations'));
  MapLikeDescriptor.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  Object.defineProperty(PluginGeneratedSerialDescriptor.prototype, 'isNullable', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'isNullable'));
  PluginGeneratedSerialDescriptor.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  EnumSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  LongAsStringSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  ObjectSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  GeneratedSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(PrimitiveSerialDescriptor.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  Object.defineProperty(PrimitiveSerialDescriptor.prototype, 'isNullable', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'isNullable'));
  Object.defineProperty(PrimitiveSerialDescriptor.prototype, 'annotations', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'annotations'));
  PrimitiveSerialDescriptor.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  BooleanSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  ByteSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  ShortSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  IntSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  LongSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  FloatSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  DoubleSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  CharSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  StringSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(Migration.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  Object.defineProperty(Migration.prototype, 'isNullable', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'isNullable'));
  Object.defineProperty(Migration.prototype, 'annotations', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'annotations'));
  Migration.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  TaggedEncoder.prototype.beginCollection_gly1x5$ = Encoder.prototype.beginCollection_gly1x5$;
  TaggedEncoder.prototype.encodeSerializableValue_tf03ej$ = Encoder.prototype.encodeSerializableValue_tf03ej$;
  TaggedEncoder.prototype.encodeNullableSerializableValue_f4686g$ = Encoder.prototype.encodeNullableSerializableValue_f4686g$;
  TaggedEncoder.prototype.shouldEncodeElementDefault_3zr2iy$ = CompositeEncoder.prototype.shouldEncodeElementDefault_3zr2iy$;
  TaggedEncoder.prototype.encodeNonSerializableElement_4wpkd1$ = CompositeEncoder.prototype.encodeNonSerializableElement_4wpkd1$;
  TaggedDecoder.prototype.decodeSerializableValue_w63s0f$ = Decoder.prototype.decodeSerializableValue_w63s0f$;
  TaggedDecoder.prototype.decodeNullableSerializableValue_aae3ea$ = Decoder.prototype.decodeNullableSerializableValue_aae3ea$;
  TaggedDecoder.prototype.updateSerializableValue_19c8k5$ = Decoder.prototype.updateSerializableValue_19c8k5$;
  TaggedDecoder.prototype.updateNullableSerializableValue_exmlbs$ = Decoder.prototype.updateNullableSerializableValue_exmlbs$;
  TaggedDecoder.prototype.decodeSequentially = CompositeDecoder.prototype.decodeSequentially;
  TaggedDecoder.prototype.decodeCollectionSize_qatsm0$ = CompositeDecoder.prototype.decodeCollectionSize_qatsm0$;
  KeyValueSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  TripleSerializer_0.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(defer$ObjectLiteral.prototype, 'name', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'name'));
  Object.defineProperty(defer$ObjectLiteral.prototype, 'isNullable', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'isNullable'));
  Object.defineProperty(defer$ObjectLiteral.prototype, 'annotations', Object.getOwnPropertyDescriptor(SerialDescriptor.prototype, 'annotations'));
  defer$ObjectLiteral.prototype.getEntityAnnotations = SerialDescriptor.prototype.getEntityAnnotations;
  JsonElementSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonPrimitiveSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonNullSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonLiteralSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonObjectSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonArraySerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonInput.prototype.decodeSerializableValue_w63s0f$ = Decoder.prototype.decodeSerializableValue_w63s0f$;
  JsonInput.prototype.decodeNullableSerializableValue_aae3ea$ = Decoder.prototype.decodeNullableSerializableValue_aae3ea$;
  JsonInput.prototype.updateSerializableValue_19c8k5$ = Decoder.prototype.updateSerializableValue_19c8k5$;
  JsonInput.prototype.updateNullableSerializableValue_exmlbs$ = Decoder.prototype.updateNullableSerializableValue_exmlbs$;
  JsonInput.prototype.decodeSequentially = CompositeDecoder.prototype.decodeSequentially;
  JsonInput.prototype.decodeCollectionSize_qatsm0$ = CompositeDecoder.prototype.decodeCollectionSize_qatsm0$;
  JsonOutput.prototype.encodeNotNullMark = Encoder.prototype.encodeNotNullMark;
  JsonOutput.prototype.beginCollection_gly1x5$ = Encoder.prototype.beginCollection_gly1x5$;
  JsonOutput.prototype.encodeSerializableValue_tf03ej$ = Encoder.prototype.encodeSerializableValue_tf03ej$;
  JsonOutput.prototype.encodeNullableSerializableValue_f4686g$ = Encoder.prototype.encodeNullableSerializableValue_f4686g$;
  JsonOutput.prototype.shouldEncodeElementDefault_3zr2iy$ = CompositeEncoder.prototype.shouldEncodeElementDefault_3zr2iy$;
  JsonOutput.prototype.encodeNonSerializableElement_4wpkd1$ = CompositeEncoder.prototype.encodeNonSerializableElement_4wpkd1$;
  JsonParametricSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  JsonTransformingSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  StreamingJsonInput.prototype.decodeNullableSerializableValue_aae3ea$ = JsonInput.prototype.decodeNullableSerializableValue_aae3ea$;
  StreamingJsonInput.prototype.updateSerializableValue_19c8k5$ = JsonInput.prototype.updateSerializableValue_19c8k5$;
  StreamingJsonInput.prototype.updateNullableSerializableValue_exmlbs$ = JsonInput.prototype.updateNullableSerializableValue_exmlbs$;
  StreamingJsonInput.prototype.decodeSequentially = JsonInput.prototype.decodeSequentially;
  StreamingJsonInput.prototype.decodeCollectionSize_qatsm0$ = JsonInput.prototype.decodeCollectionSize_qatsm0$;
  StreamingJsonOutput.prototype.encodeNotNullMark = JsonOutput.prototype.encodeNotNullMark;
  StreamingJsonOutput.prototype.beginCollection_gly1x5$ = JsonOutput.prototype.beginCollection_gly1x5$;
  StreamingJsonOutput.prototype.encodeNullableSerializableValue_f4686g$ = JsonOutput.prototype.encodeNullableSerializableValue_f4686g$;
  StreamingJsonOutput.prototype.encodeNonSerializableElement_4wpkd1$ = JsonOutput.prototype.encodeNonSerializableElement_4wpkd1$;
  message = 'These classes accidentally slipped to the public API surface. ' + 'We neither had intent to provide a production-quality implementation nor have an intent to support them.' + 'They are removed and to migrate, you can either use a corresponding java.io type or just a copy-paste implementation from the GitHub.' + 'If you have a use-case for multiplatform IO, please report it to the https://github.com/Kotlin/kotlinx-io/issues';
  enumReflectiveAccessMessage = 'Deprecated because reflected operations on enums are not supported correctly on Kotlin/JS and Kotlin/Native.\n' + 'Prefer using reified functions or enum serializers.';
  message_0 = 'Mapper was renamed to Properties to better reflect its semantics and extracted to separate artifact kotlinx-serialization-properties';
  PolymorphicClassDescriptor = SerialDescriptor_0('kotlinx.serialization.Polymorphic', PolymorphicKind$OPEN_getInstance(), PolymorphicClassDescriptor$lambda);
  ARRAY_NAME = 'kotlin.Array';
  ARRAY_LIST_NAME = 'kotlin.collections.ArrayList';
  LINKED_HASH_SET_NAME = 'kotlin.collections.LinkedHashSet';
  HASH_SET_NAME = 'kotlin.collections.HashSet';
  LINKED_HASH_MAP_NAME = 'kotlin.collections.LinkedHashMap';
  HASH_MAP_NAME = 'kotlin.collections.HashMap';
  INITIAL_SIZE = 10;
  BUILTIN_SERIALIZERS = mapOf([to(PrimitiveClasses$stringClass, serializer_10(kotlin_js_internal_StringCompanionObject)), to(getKClass(Char), serializer_2(kotlin_js_internal_CharCompanionObject)), to(PrimitiveClasses$charArrayClass, CharArraySerializer()), to(PrimitiveClasses$doubleClass, serializer_8(kotlin_js_internal_DoubleCompanionObject)), to(PrimitiveClasses$doubleArrayClass, DoubleArraySerializer()), to(PrimitiveClasses$floatClass, serializer_7(kotlin_js_internal_FloatCompanionObject)), to(PrimitiveClasses$floatArrayClass, FloatArraySerializer()), to(getKClass(Long), serializer_6(kotlin_js_internal_LongCompanionObject)), to(PrimitiveClasses$longArrayClass, LongArraySerializer()), to(PrimitiveClasses$intClass, serializer_5(kotlin_js_internal_IntCompanionObject)), to(PrimitiveClasses$intArrayClass, IntArraySerializer()), to(PrimitiveClasses$shortClass, serializer_4(kotlin_js_internal_ShortCompanionObject)), to(PrimitiveClasses$shortArrayClass, ShortArraySerializer()), to(PrimitiveClasses$byteClass, serializer_3(kotlin_js_internal_ByteCompanionObject)), to(PrimitiveClasses$byteArrayClass, ByteArraySerializer()), to(PrimitiveClasses$booleanClass, serializer_9(kotlin_js_internal_BooleanCompanionObject)), to(PrimitiveClasses$booleanArrayClass, BooleanArraySerializer()), to(getKClass(Object.getPrototypeOf(kotlin.Unit).constructor), UnitSerializer())]);
  message_1 = 'Top level primitive descriptors are unavailable to avoid accidental misuage. ' + 'Please use kind for comparison and primitive descriptor with a unique name for implementation';
  unitDeprecated = 'This method is deprecated with no replacement. Unit is encoded as an empty object and does not require a dedicated method. ' + 'To migrate, just remove your own implementation of this method';
  NULL = new Any();
  deprecationMessage = 'This class is used only by the plugin in generated code and should not be used directly. Use corresponding factory functions instead';
  defaultJsonModule = serializersModuleOf_0(mapOf([to(getKClass(JsonElement), JsonElementSerializer_getInstance()), to(getKClass(JsonPrimitive), JsonPrimitiveSerializer_getInstance()), to(getKClass(JsonLiteral), JsonLiteralSerializer_getInstance()), to(getKClass(JsonNull), JsonNullSerializer_getInstance()), to(getKClass(JsonObject), JsonObjectSerializer_getInstance()), to(getKClass(JsonArray), JsonArraySerializer_getInstance())]));
  lenientHint = "Use 'JsonConfiguration.isLenient = true' to accept non-compliant JSON";
  NULL_0 = 'null';
  COMMA = 44;
  COLON = 58;
  BEGIN_OBJ = 123;
  END_OBJ = 125;
  BEGIN_LIST = 91;
  END_LIST = 93;
  STRING = 34;
  STRING_ESC = 92;
  INVALID = toChar(0);
  UNICODE_ESC = 117;
  TC_OTHER = 0;
  TC_STRING = 1;
  TC_STRING_ESC = 2;
  TC_WS = 3;
  TC_COMMA = 4;
  TC_COLON = 5;
  TC_BEGIN_OBJ = 6;
  TC_END_OBJ = 7;
  TC_BEGIN_LIST = 8;
  TC_END_LIST = 9;
  TC_NULL = 10;
  TC_INVALID = 11;
  TC_EOF = 12;
  CTC_MAX = 126;
  ESC2C_MAX = 117;
  var $receiver = new Int8Array(126);
  for (var i = 0; i <= 32; i++) {
    initC2TC($receiver, i, TC_INVALID);
  }
  initC2TC($receiver, 9, TC_WS);
  initC2TC($receiver, 10, TC_WS);
  initC2TC($receiver, 13, TC_WS);
  initC2TC($receiver, 32, TC_WS);
  initC2TC_0($receiver, COMMA, TC_COMMA);
  initC2TC_0($receiver, COLON, TC_COLON);
  initC2TC_0($receiver, BEGIN_OBJ, TC_BEGIN_OBJ);
  initC2TC_0($receiver, END_OBJ, TC_END_OBJ);
  initC2TC_0($receiver, BEGIN_LIST, TC_BEGIN_LIST);
  initC2TC_0($receiver, END_LIST, TC_END_LIST);
  initC2TC_0($receiver, STRING, TC_STRING);
  initC2TC_0($receiver, STRING_ESC, TC_STRING_ESC);
  C2TC = $receiver;
  var $receiver_0 = Kotlin.newArray(128, null);
  for (var c = 0; c <= 31; c++) {
    var c1 = toHexChar(c >> 12);
    var c2 = toHexChar(c >> 8);
    var c3 = toHexChar(c >> 4);
    var c4 = toHexChar(c);
    $receiver_0[c] = '\\' + 'u' + String.fromCharCode(c1) + String.fromCharCode(c2) + String.fromCharCode(c3) + String.fromCharCode(c4);
  }
  $receiver_0[34] = '\\"';
  $receiver_0[92] = '\\\\';
  $receiver_0[9] = '\\t';
  $receiver_0[8] = '\\b';
  $receiver_0[10] = '\\n';
  $receiver_0[13] = '\\r';
  $receiver_0[12] = '\\f';
  ESCAPE_CHARS = $receiver_0;
  PRIMITIVE_TAG = 'primitive';
  MAX_SAFE_INTEGER = L9007199254740991.toNumber();
  Kotlin.defineModule('kotlinx-serialization-kotlinx-serialization-runtime', _);
  return _;
}));

//# sourceMappingURL=kotlinx-serialization-kotlinx-serialization-runtime.js.map
