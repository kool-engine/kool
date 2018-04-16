(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-serialization-runtime-js'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-serialization-runtime-js'.");
    }
    root['kotlinx-serialization-runtime-js'] = factory(typeof this['kotlinx-serialization-runtime-js'] === 'undefined' ? {} : this['kotlinx-serialization-runtime-js'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Enum = Kotlin.kotlin.Enum;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var throwISE = Kotlin.throwISE;
  var toString = Kotlin.toString;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var getKClass = Kotlin.getKClass;
  var Annotation = Kotlin.kotlin.Annotation;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var RuntimeException_init = Kotlin.kotlin.RuntimeException_init_pdl1vj$;
  var RuntimeException = Kotlin.kotlin.RuntimeException;
  var Any = Object;
  var throwCCE = Kotlin.throwCCE;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var toBoxedChar = Kotlin.toBoxedChar;
  var unboxChar = Kotlin.unboxChar;
  var Unit = Kotlin.kotlin.Unit;
  var ensureNotNull = Kotlin.ensureNotNull;
  var last = Kotlin.kotlin.collections.last_2p1efm$;
  var lastOrNull = Kotlin.kotlin.collections.lastOrNull_2p1efm$;
  var get_lastIndex = Kotlin.kotlin.collections.get_lastIndex_55thoc$;
  var singleOrNull = Kotlin.kotlin.collections.singleOrNull_2p1efm$;
  var kotlin = Kotlin.kotlin;
  var getValue = Kotlin.kotlin.collections.getValue_t9ocha$;
  var equals = Kotlin.equals;
  var toByte = Kotlin.toByte;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var contains = Kotlin.kotlin.ranges.contains_8sy4e8$;
  var kotlin_js_internal_ByteCompanionObject = Kotlin.kotlin.js.internal.ByteCompanionObject;
  var toShort = Kotlin.toShort;
  var kotlin_js_internal_ShortCompanionObject = Kotlin.kotlin.js.internal.ShortCompanionObject;
  var kotlin_js_internal_IntCompanionObject = Kotlin.kotlin.js.internal.IntCompanionObject;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init;
  var toChar = Kotlin.toChar;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var asList = Kotlin.kotlin.collections.asList_us0mfu$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_mqih57$;
  var ArrayList = Kotlin.kotlin.collections.ArrayList;
  var LinkedHashSet = Kotlin.kotlin.collections.LinkedHashSet;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_mqih57$;
  var HashSet_init = Kotlin.kotlin.collections.HashSet_init_287e2$;
  var HashSet = Kotlin.kotlin.collections.HashSet;
  var HashSet_init_0 = Kotlin.kotlin.collections.HashSet_init_mqih57$;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var LinkedHashMap = Kotlin.kotlin.collections.LinkedHashMap;
  var LinkedHashMap_init_0 = Kotlin.kotlin.collections.LinkedHashMap_init_73mtqc$;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  var HashMap = Kotlin.kotlin.collections.HashMap;
  var HashMap_init_0 = Kotlin.kotlin.collections.HashMap_init_73mtqc$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var toInt = Kotlin.kotlin.text.toInt_pdl1vz$;
  var Map$Entry = Kotlin.kotlin.collections.Map.Entry;
  var Triple = Kotlin.kotlin.Triple;
  var toList = Kotlin.kotlin.collections.toList_7wnvza$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var IllegalArgumentException_init_0 = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var CharRange = Kotlin.kotlin.ranges.CharRange;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var trimStart = Kotlin.kotlin.text.trimStart_wqw3xr$;
  var isFinite = Kotlin.kotlin.isFinite_81szk$;
  var isFinite_0 = Kotlin.kotlin.isFinite_yrwdxr$;
  var iterator = Kotlin.kotlin.text.iterator_gw00vp$;
  var toByte_0 = Kotlin.kotlin.text.toByte_pdl1vz$;
  var toShort_0 = Kotlin.kotlin.text.toShort_pdl1vz$;
  var toLong = Kotlin.kotlin.text.toLong_pdl1vz$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var single = Kotlin.kotlin.text.single_gw00vp$;
  var StringBuilder = Kotlin.kotlin.text.StringBuilder;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var single_0 = Kotlin.kotlin.collections.single_2p1efm$;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var Exception = Kotlin.kotlin.Exception;
  var IndexOutOfBoundsException_init = Kotlin.kotlin.IndexOutOfBoundsException_init;
  var NullPointerException_init = Kotlin.kotlin.NullPointerException_init;
  var toList_0 = Kotlin.kotlin.text.toList_gw00vp$;
  var toCharArray = Kotlin.kotlin.collections.toCharArray_rr68x$;
  var slice = Kotlin.kotlin.collections.slice_bq4su$;
  var numberToChar = Kotlin.numberToChar;
  var get_js = Kotlin.kotlin.js.get_js_1yb8b7$;
  var toByteArray = Kotlin.kotlin.collections.toByteArray_kdx1v$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  ByteOrder.prototype = Object.create(Enum.prototype);
  ByteOrder.prototype.constructor = ByteOrder;
  KSerialClassKind.prototype = Object.create(Enum.prototype);
  KSerialClassKind.prototype.constructor = KSerialClassKind;
  SerializationException.prototype = Object.create(RuntimeException.prototype);
  SerializationException.prototype.constructor = SerializationException;
  MissingFieldException.prototype = Object.create(SerializationException.prototype);
  MissingFieldException.prototype.constructor = MissingFieldException;
  UnknownFieldException.prototype = Object.create(SerializationException.prototype);
  UnknownFieldException.prototype.constructor = UnknownFieldException;
  UpdateMode.prototype = Object.create(Enum.prototype);
  UpdateMode.prototype.constructor = UpdateMode;
  UpdateNotSupportedException.prototype = Object.create(SerializationException.prototype);
  UpdateNotSupportedException.prototype.constructor = UpdateNotSupportedException;
  ElementValueOutput.prototype = Object.create(KOutput.prototype);
  ElementValueOutput.prototype.constructor = ElementValueOutput;
  ElementValueInput.prototype = Object.create(KInput.prototype);
  ElementValueInput.prototype.constructor = ElementValueInput;
  ValueTransformer$Output.prototype = Object.create(KOutput.prototype);
  ValueTransformer$Output.prototype.constructor = ValueTransformer$Output;
  ValueTransformer$Input.prototype = Object.create(KInput.prototype);
  ValueTransformer$Input.prototype.constructor = ValueTransformer$Input;
  TaggedOutput.prototype = Object.create(KOutput.prototype);
  TaggedOutput.prototype.constructor = TaggedOutput;
  IntTaggedOutput.prototype = Object.create(TaggedOutput.prototype);
  IntTaggedOutput.prototype.constructor = IntTaggedOutput;
  StringTaggedOutput.prototype = Object.create(TaggedOutput.prototype);
  StringTaggedOutput.prototype.constructor = StringTaggedOutput;
  NamedValueOutput.prototype = Object.create(TaggedOutput.prototype);
  NamedValueOutput.prototype.constructor = NamedValueOutput;
  TaggedInput.prototype = Object.create(KInput.prototype);
  TaggedInput.prototype.constructor = TaggedInput;
  IntTaggedInput.prototype = Object.create(TaggedInput.prototype);
  IntTaggedInput.prototype.constructor = IntTaggedInput;
  StringTaggedInput.prototype = Object.create(TaggedInput.prototype);
  StringTaggedInput.prototype.constructor = StringTaggedInput;
  NamedValueInput.prototype = Object.create(TaggedInput.prototype);
  NamedValueInput.prototype.constructor = NamedValueInput;
  Mapper$OutMapper.prototype = Object.create(NamedValueOutput.prototype);
  Mapper$OutMapper.prototype.constructor = Mapper$OutMapper;
  Mapper$OutNullableMapper.prototype = Object.create(NamedValueOutput.prototype);
  Mapper$OutNullableMapper.prototype.constructor = Mapper$OutNullableMapper;
  Mapper$InMapper.prototype = Object.create(NamedValueInput.prototype);
  Mapper$InMapper.prototype.constructor = Mapper$InMapper;
  Mapper$InNullableMapper.prototype = Object.create(NamedValueInput.prototype);
  Mapper$InNullableMapper.prototype.constructor = Mapper$InNullableMapper;
  CBOR$CBORWriter.prototype = Object.create(ElementValueOutput.prototype);
  CBOR$CBORWriter.prototype.constructor = CBOR$CBORWriter;
  CBOR$CBOREntryWriter.prototype = Object.create(CBOR$CBORWriter.prototype);
  CBOR$CBOREntryWriter.prototype.constructor = CBOR$CBOREntryWriter;
  CBOR$CBORListWriter.prototype = Object.create(CBOR$CBORWriter.prototype);
  CBOR$CBORListWriter.prototype.constructor = CBOR$CBORListWriter;
  CBOR$CBORMapWriter.prototype = Object.create(CBOR$CBORListWriter.prototype);
  CBOR$CBORMapWriter.prototype.constructor = CBOR$CBORMapWriter;
  CBOR$CBORReader.prototype = Object.create(ElementValueInput.prototype);
  CBOR$CBORReader.prototype.constructor = CBOR$CBORReader;
  CBOR$CBOREntryReader.prototype = Object.create(CBOR$CBORReader.prototype);
  CBOR$CBOREntryReader.prototype.constructor = CBOR$CBOREntryReader;
  CBOR$CBORListReader.prototype = Object.create(CBOR$CBORReader.prototype);
  CBOR$CBORListReader.prototype.constructor = CBOR$CBORListReader;
  CBOR$CBORMapReader.prototype = Object.create(CBOR$CBORListReader.prototype);
  CBOR$CBORMapReader.prototype.constructor = CBOR$CBORMapReader;
  IOException.prototype = Object.create(Exception.prototype);
  IOException.prototype.constructor = IOException;
  CBORParsingException.prototype = Object.create(IOException.prototype);
  CBORParsingException.prototype.constructor = CBORParsingException;
  MapLikeSerializer.prototype = Object.create(ListLikeSerializer.prototype);
  MapLikeSerializer.prototype.constructor = MapLikeSerializer;
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
  MapEntryUpdatingSerializer.prototype = Object.create(KeyValueSerializer.prototype);
  MapEntryUpdatingSerializer.prototype.constructor = MapEntryUpdatingSerializer;
  MapEntrySerializer.prototype = Object.create(KeyValueSerializer.prototype);
  MapEntrySerializer.prototype.constructor = MapEntrySerializer;
  PairSerializer.prototype = Object.create(KeyValueSerializer.prototype);
  PairSerializer.prototype.constructor = PairSerializer;
  ArrayClassDesc.prototype = Object.create(ListLikeDesc.prototype);
  ArrayClassDesc.prototype.constructor = ArrayClassDesc;
  ArrayListClassDesc.prototype = Object.create(ListLikeDesc.prototype);
  ArrayListClassDesc.prototype.constructor = ArrayListClassDesc;
  LinkedHashSetClassDesc.prototype = Object.create(ListLikeDesc.prototype);
  LinkedHashSetClassDesc.prototype.constructor = LinkedHashSetClassDesc;
  HashSetClassDesc.prototype = Object.create(ListLikeDesc.prototype);
  HashSetClassDesc.prototype.constructor = HashSetClassDesc;
  LinkedHashMapClassDesc.prototype = Object.create(ListLikeDesc.prototype);
  LinkedHashMapClassDesc.prototype.constructor = LinkedHashMapClassDesc;
  HashMapClassDesc.prototype = Object.create(ListLikeDesc.prototype);
  HashMapClassDesc.prototype.constructor = HashMapClassDesc;
  MapEntryClassDesc.prototype = Object.create(SerialClassDescImpl.prototype);
  MapEntryClassDesc.prototype.constructor = MapEntryClassDesc;
  PairClassDesc.prototype = Object.create(SerialClassDescImpl.prototype);
  PairClassDesc.prototype.constructor = PairClassDesc;
  TripleSerializer$TripleDesc.prototype = Object.create(SerialClassDescImpl.prototype);
  TripleSerializer$TripleDesc.prototype.constructor = TripleSerializer$TripleDesc;
  JSON$Mode.prototype = Object.create(Enum.prototype);
  JSON$Mode.prototype.constructor = JSON$Mode;
  JSON$JsonOutput.prototype = Object.create(ElementValueOutput.prototype);
  JSON$JsonOutput.prototype.constructor = JSON$JsonOutput;
  PrintWriter.prototype = Object.create(Writer.prototype);
  PrintWriter.prototype.constructor = PrintWriter;
  JSON$Composer.prototype = Object.create(PrintWriter.prototype);
  JSON$Composer.prototype.constructor = JSON$Composer;
  JSON$JsonInput.prototype = Object.create(ElementValueInput.prototype);
  JSON$JsonInput.prototype.constructor = JSON$JsonInput;
  ProtoNumberType.prototype = Object.create(Enum.prototype);
  ProtoNumberType.prototype.constructor = ProtoNumberType;
  ProtoBuf$ProtobufWriter.prototype = Object.create(TaggedOutput.prototype);
  ProtoBuf$ProtobufWriter.prototype.constructor = ProtoBuf$ProtobufWriter;
  ProtoBuf$ObjectWriter.prototype = Object.create(ProtoBuf$ProtobufWriter.prototype);
  ProtoBuf$ObjectWriter.prototype.constructor = ProtoBuf$ObjectWriter;
  ProtoBuf$MapEntryWriter.prototype = Object.create(ProtoBuf$ObjectWriter.prototype);
  ProtoBuf$MapEntryWriter.prototype.constructor = ProtoBuf$MapEntryWriter;
  ProtoBuf$RepeatedWriter.prototype = Object.create(ProtoBuf$ProtobufWriter.prototype);
  ProtoBuf$RepeatedWriter.prototype.constructor = ProtoBuf$RepeatedWriter;
  ProtoBuf$ProtobufReader.prototype = Object.create(TaggedInput.prototype);
  ProtoBuf$ProtobufReader.prototype.constructor = ProtoBuf$ProtobufReader;
  ProtoBuf$RepeatedReader.prototype = Object.create(ProtoBuf$ProtobufReader.prototype);
  ProtoBuf$RepeatedReader.prototype.constructor = ProtoBuf$RepeatedReader;
  ProtoBuf$MapEntryReader.prototype = Object.create(ProtoBuf$ProtobufReader.prototype);
  ProtoBuf$MapEntryReader.prototype.constructor = ProtoBuf$MapEntryReader;
  ProtobufDecodingException.prototype = Object.create(SerializationException.prototype);
  ProtobufDecodingException.prototype.constructor = ProtobufDecodingException;
  ByteArrayInputStream.prototype = Object.create(InputStream.prototype);
  ByteArrayInputStream.prototype.constructor = ByteArrayInputStream;
  ByteArrayOutputStream.prototype = Object.create(OutputStream.prototype);
  ByteArrayOutputStream.prototype.constructor = ByteArrayOutputStream;
  StringWriter.prototype = Object.create(Writer.prototype);
  StringWriter.prototype.constructor = StringWriter;
  StringReader.prototype = Object.create(Reader.prototype);
  StringReader.prototype.constructor = StringReader;
  DynamicObjectParser$DynamicInput.prototype = Object.create(NamedValueInput.prototype);
  DynamicObjectParser$DynamicInput.prototype.constructor = DynamicObjectParser$DynamicInput;
  DynamicObjectParser$DynamicMapValueInput.prototype = Object.create(DynamicObjectParser$DynamicInput.prototype);
  DynamicObjectParser$DynamicMapValueInput.prototype.constructor = DynamicObjectParser$DynamicMapValueInput;
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
  function get_list($receiver) {
    return new ArrayListSerializer($receiver);
  }
  function get_set($receiver) {
    return new LinkedHashSetSerializer($receiver);
  }
  function get_map($receiver) {
    return new LinkedHashMapSerializer($receiver.first, $receiver.second);
  }
  function SerialContext(parentContext) {
    if (parentContext === void 0)
      parentContext = null;
    this.parentContext_0 = parentContext;
    this.classMap_0 = HashMap_init();
  }
  SerialContext.prototype.registerSerializer_cfhkba$ = function (forClass, serializer) {
    this.classMap_0.put_xwzc9p$(forClass, serializer);
  };
  SerialContext.prototype.getSerializer_1yb8b7$ = function ($receiver) {
    return this.getSerializerByClass_lmshww$($receiver);
  };
  SerialContext.prototype.getSerializerByValue_issdgt$ = function (value) {
    var tmp$;
    if (value == null)
      throw new SerializationException('Cannot determine class for value ' + toString(value));
    var t = value;
    var klass = Kotlin.getKClassFromExpression(t);
    return Kotlin.isType(tmp$ = this.getSerializerByClass_lmshww$(klass), KSerializer) ? tmp$ : null;
  };
  SerialContext.prototype.getSerializer_30y1fr$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.SerialContext.getSerializer_30y1fr$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT) {
      return this.getSerializerByClass_lmshww$(getKClass(T_0));
    };
  }));
  SerialContext.prototype.getSerializerByClass_lmshww$ = function (klass) {
    var tmp$, tmp$_0, tmp$_1;
    return (tmp$_1 = Kotlin.isType(tmp$ = this.classMap_0.get_11rb$(klass), KSerializer) ? tmp$ : null) != null ? tmp$_1 : (tmp$_0 = this.parentContext_0) != null ? tmp$_0.getSerializerByClass_lmshww$(klass) : null;
  };
  SerialContext.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialContext',
    interfaces: []
  };
  function klassSerializer($receiver, klass) {
    var tmp$;
    return (tmp$ = $receiver != null ? $receiver.getSerializerByClass_lmshww$(klass) : null) != null ? tmp$ : serializer(klass);
  }
  function valueSerializer($receiver, value) {
    var tmp$;
    return (tmp$ = $receiver != null ? $receiver.getSerializerByValue_issdgt$(value) : null) != null ? tmp$ : serializer(Kotlin.getKClassFromExpression(value));
  }
  function ContextSerializer(serializableClass) {
    this.serializableClass = serializableClass;
  }
  ContextSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeValue_za3rmp$(obj);
  };
  ContextSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readValue_lmshww$(this.serializableClass);
  };
  Object.defineProperty(ContextSerializer.prototype, 'serialClassDesc', {
    get: function () {
      throw new SerializationException('No descriptor');
    }
  });
  ContextSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ContextSerializer',
    interfaces: [KSerializer]
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
  function KSerialClassKind(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function KSerialClassKind_initFields() {
    KSerialClassKind_initFields = function () {
    };
    KSerialClassKind$CLASS_instance = new KSerialClassKind('CLASS', 0);
    KSerialClassKind$OBJECT_instance = new KSerialClassKind('OBJECT', 1);
    KSerialClassKind$UNIT_instance = new KSerialClassKind('UNIT', 2);
    KSerialClassKind$SEALED_instance = new KSerialClassKind('SEALED', 3);
    KSerialClassKind$LIST_instance = new KSerialClassKind('LIST', 4);
    KSerialClassKind$SET_instance = new KSerialClassKind('SET', 5);
    KSerialClassKind$MAP_instance = new KSerialClassKind('MAP', 6);
    KSerialClassKind$ENTRY_instance = new KSerialClassKind('ENTRY', 7);
    KSerialClassKind$POLYMORPHIC_instance = new KSerialClassKind('POLYMORPHIC', 8);
    KSerialClassKind$PRIMITIVE_instance = new KSerialClassKind('PRIMITIVE', 9);
    KSerialClassKind$ENUM_instance = new KSerialClassKind('ENUM', 10);
  }
  var KSerialClassKind$CLASS_instance;
  function KSerialClassKind$CLASS_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$CLASS_instance;
  }
  var KSerialClassKind$OBJECT_instance;
  function KSerialClassKind$OBJECT_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$OBJECT_instance;
  }
  var KSerialClassKind$UNIT_instance;
  function KSerialClassKind$UNIT_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$UNIT_instance;
  }
  var KSerialClassKind$SEALED_instance;
  function KSerialClassKind$SEALED_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$SEALED_instance;
  }
  var KSerialClassKind$LIST_instance;
  function KSerialClassKind$LIST_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$LIST_instance;
  }
  var KSerialClassKind$SET_instance;
  function KSerialClassKind$SET_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$SET_instance;
  }
  var KSerialClassKind$MAP_instance;
  function KSerialClassKind$MAP_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$MAP_instance;
  }
  var KSerialClassKind$ENTRY_instance;
  function KSerialClassKind$ENTRY_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$ENTRY_instance;
  }
  var KSerialClassKind$POLYMORPHIC_instance;
  function KSerialClassKind$POLYMORPHIC_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$POLYMORPHIC_instance;
  }
  var KSerialClassKind$PRIMITIVE_instance;
  function KSerialClassKind$PRIMITIVE_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$PRIMITIVE_instance;
  }
  var KSerialClassKind$ENUM_instance;
  function KSerialClassKind$ENUM_getInstance() {
    KSerialClassKind_initFields();
    return KSerialClassKind$ENUM_instance;
  }
  KSerialClassKind.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KSerialClassKind',
    interfaces: [Enum]
  };
  function KSerialClassKind$values() {
    return [KSerialClassKind$CLASS_getInstance(), KSerialClassKind$OBJECT_getInstance(), KSerialClassKind$UNIT_getInstance(), KSerialClassKind$SEALED_getInstance(), KSerialClassKind$LIST_getInstance(), KSerialClassKind$SET_getInstance(), KSerialClassKind$MAP_getInstance(), KSerialClassKind$ENTRY_getInstance(), KSerialClassKind$POLYMORPHIC_getInstance(), KSerialClassKind$PRIMITIVE_getInstance(), KSerialClassKind$ENUM_getInstance()];
  }
  KSerialClassKind.values = KSerialClassKind$values;
  function KSerialClassKind$valueOf(name) {
    switch (name) {
      case 'CLASS':
        return KSerialClassKind$CLASS_getInstance();
      case 'OBJECT':
        return KSerialClassKind$OBJECT_getInstance();
      case 'UNIT':
        return KSerialClassKind$UNIT_getInstance();
      case 'SEALED':
        return KSerialClassKind$SEALED_getInstance();
      case 'LIST':
        return KSerialClassKind$LIST_getInstance();
      case 'SET':
        return KSerialClassKind$SET_getInstance();
      case 'MAP':
        return KSerialClassKind$MAP_getInstance();
      case 'ENTRY':
        return KSerialClassKind$ENTRY_getInstance();
      case 'POLYMORPHIC':
        return KSerialClassKind$POLYMORPHIC_getInstance();
      case 'PRIMITIVE':
        return KSerialClassKind$PRIMITIVE_getInstance();
      case 'ENUM':
        return KSerialClassKind$ENUM_getInstance();
      default:throwISE('No enum constant kotlinx.serialization.KSerialClassKind.' + name);
    }
  }
  KSerialClassKind.valueOf_61zpoe$ = KSerialClassKind$valueOf;
  function KSerialClassDesc() {
  }
  KSerialClassDesc.prototype.getElementIndexOrThrow_61zpoe$ = function (name) {
    var i = this.getElementIndex_61zpoe$(name);
    if (i === KInput$Companion_getInstance().UNKNOWN_NAME)
      throw new SerializationException("Unknown name '" + name + "'");
    return i;
  };
  KSerialClassDesc.prototype.getAnnotationsForIndex_za3lpa$ = function (index) {
    return emptyList();
  };
  Object.defineProperty(KSerialClassDesc.prototype, 'associatedFieldsCount', {
    get: function () {
      return 0;
    }
  });
  KSerialClassDesc.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KSerialClassDesc',
    interfaces: []
  };
  function KSerialSaver() {
  }
  KSerialSaver.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KSerialSaver',
    interfaces: []
  };
  function KSerialLoader() {
  }
  KSerialLoader.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KSerialLoader',
    interfaces: []
  };
  function KSerializer() {
  }
  KSerializer.prototype.update_qkk2oh$ = function (input, old) {
    throw new UpdateNotSupportedException(this.serialClassDesc.name);
  };
  KSerializer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KSerializer',
    interfaces: [KSerialLoader, KSerialSaver]
  };
  function SerializationConstructorMarker() {
  }
  SerializationConstructorMarker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializationConstructorMarker',
    interfaces: []
  };
  function SerializationException(s) {
    RuntimeException_init(s, this);
    this.name = 'SerializationException';
  }
  SerializationException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerializationException',
    interfaces: [RuntimeException]
  };
  function MissingFieldException(fieldName) {
    SerializationException.call(this, 'Field ' + fieldName + ' is required, but it was missing');
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
  function KOutput() {
    this.context = null;
  }
  KOutput.prototype.write_jsy488$ = function (saver, obj) {
    saver.save_ejfkry$(this, obj);
  };
  KOutput.prototype.write_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.KOutput.write_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, obj) {
      this.write_jsy488$(serializer(getKClass(T_0)), obj);
    };
  }));
  KOutput.prototype.writeNullable_20fw5n$ = function (saver, obj) {
    if (obj == null) {
      this.writeNullValue();
    }
     else {
      this.writeNotNullMark();
      saver.save_ejfkry$(this, obj);
    }
  };
  KOutput.prototype.writeValue_za3rmp$ = function (value) {
    var tmp$;
    var s = (tmp$ = this.context) != null ? tmp$.getSerializerByValue_issdgt$(value) : null;
    if (s != null)
      this.writeSerializableValue_jsy488$(s, value);
    else
      this.writeNonSerializableValue_za3rmp$(value);
  };
  KOutput.prototype.writeEnumValue_wbfx10$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.KOutput.writeEnumValue_wbfx10$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, value) {
      this.writeEnumValue_9pl89b$(getKClass(T_0), value);
    };
  }));
  KOutput.prototype.writeSerializableValue_jsy488$ = function (saver, value) {
    saver.save_ejfkry$(this, value);
  };
  KOutput.prototype.writeNullableSerializableValue_20fw5n$ = function (saver, value) {
    if (value == null) {
      this.writeNullValue();
    }
     else {
      this.writeNotNullMark();
      this.writeSerializableValue_jsy488$(saver, value);
    }
  };
  KOutput.prototype.writeBegin_276rha$ = function (desc, typeParams) {
    return this;
  };
  KOutput.prototype.writeBegin_jqfc32$ = function (desc, collectionSize, typeParams) {
    return this.writeBegin_276rha$(desc, typeParams.slice());
  };
  KOutput.prototype.writeEnd_f6e2p$ = function (desc) {
  };
  KOutput.prototype.writeElementValue_j8uhfo$ = function (desc, index, value) {
    var tmp$;
    var s = (tmp$ = this.context) != null ? tmp$.getSerializerByValue_issdgt$(value) : null;
    if (s != null)
      this.writeSerializableElementValue_k4al2t$(desc, index, s, value);
    else
      this.writeNonSerializableElementValue_j8uhfo$(desc, index, value);
  };
  KOutput.prototype.writeEnumElementValue_v4fwjt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.KOutput.writeEnumElementValue_v4fwjt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, desc, index, value) {
      this.writeEnumElementValue_bta54i$(desc, index, getKClass(T_0), value);
    };
  }));
  KOutput.prototype.writeSerializableElementValue_k4al2t$ = function (desc, index, saver, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeSerializableValue_jsy488$(saver, value);
  };
  KOutput.prototype.writeNullableSerializableElementValue_874a36$ = function (desc, index, saver, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeNullableSerializableValue_20fw5n$(saver, value);
  };
  KOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KOutput',
    interfaces: []
  };
  function KInput() {
    KInput$Companion_getInstance();
    this.context = null;
    this.updateMode_vtcax8$_0 = UpdateMode$UPDATE_getInstance();
  }
  KInput.prototype.read_30y1fr$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.KInput.read_30y1fr$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT) {
      return this.read_rf0fz3$(serializer(getKClass(T_0)));
    };
  }));
  KInput.prototype.read_rf0fz3$ = function (loader) {
    return loader.load_ljkqvg$(this);
  };
  KInput.prototype.readNullable_1n8rgi$ = function (loader) {
    return this.readNotNullMark() ? this.read_rf0fz3$(loader) : this.readNullValue();
  };
  KInput.prototype.readValue_lmshww$ = function (klass) {
    var tmp$, tmp$_0;
    var s = (tmp$ = this.context) != null ? tmp$.getSerializerByClass_lmshww$(klass) : null;
    return s != null ? this.readSerializableValue_rf0fz3$(s) : Kotlin.isType(tmp$_0 = this.readValue(), Any) ? tmp$_0 : throwCCE();
  };
  KInput.prototype.readEnumValue_nxd2ia$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.KInput.readEnumValue_nxd2ia$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT) {
      return this.readEnumValue_xvqrpl$(getKClass(T_0));
    };
  }));
  KInput.prototype.readSerializableValue_rf0fz3$ = function (loader) {
    return loader.load_ljkqvg$(this);
  };
  KInput.prototype.readNullableSerializableValue_1n8rgi$ = function (loader) {
    return this.readNotNullMark() ? this.readSerializableValue_rf0fz3$(loader) : this.readNullValue();
  };
  KInput.prototype.readBegin_276rha$ = function (desc, typeParams) {
    return this;
  };
  KInput.prototype.readEnd_f6e2p$ = function (desc) {
  };
  function KInput$Companion() {
    KInput$Companion_instance = this;
    this.READ_DONE = -1;
    this.READ_ALL = -2;
    this.UNKNOWN_NAME = -3;
  }
  KInput$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var KInput$Companion_instance = null;
  function KInput$Companion_getInstance() {
    if (KInput$Companion_instance === null) {
      new KInput$Companion();
    }
    return KInput$Companion_instance;
  }
  KInput.prototype.readElementValue_lysmpq$ = function (desc, index, klass) {
    var tmp$;
    var s = (tmp$ = this.context) != null ? tmp$.getSerializerByClass_lmshww$(klass) : null;
    return s != null ? this.readSerializableElementValue_nqb5fm$(desc, index, s) : this.readElementValue_xvmgof$(desc, index);
  };
  KInput.prototype.readEnumElementValue_93looz$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.KInput.readEnumElementValue_93looz$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    return function (T_0, isT, desc, index) {
      return this.readEnumElementValue_61hxlg$(desc, index, getKClass(T_0));
    };
  }));
  KInput.prototype.updateSerializableElementValue_2bgl1k$ = function (desc, index, loader, old) {
    return this.updateSerializableValue_3jm06w$(loader, desc, old);
  };
  KInput.prototype.updateNullableSerializableElementValue_xspi39$ = function (desc, index, loader, old) {
    return this.updateNullableSerializableValue_2rkmol$(loader, desc, old);
  };
  KInput.prototype.updateSerializableValue_3jm06w$ = function (loader, desc, old) {
    var tmp$;
    switch (this.updateMode.name) {
      case 'BANNED':
        throw new UpdateNotSupportedException(desc.name);
      case 'OVERWRITE':
        tmp$ = this.readSerializableValue_rf0fz3$(loader);
        break;
      case 'UPDATE':
        tmp$ = loader.update_qkk2oh$(this, old);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  };
  KInput.prototype.updateNullableSerializableValue_2rkmol$ = function (loader, desc, old) {
    var tmp$;
    if (this.updateMode === UpdateMode$BANNED_getInstance())
      throw new UpdateNotSupportedException(desc.name);
    else if (this.updateMode === UpdateMode$OVERWRITE_getInstance() || old == null)
      tmp$ = this.readNullableSerializableValue_1n8rgi$(loader);
    else if (this.readNotNullMark())
      tmp$ = loader.update_qkk2oh$(this, old);
    else {
      this.readNullValue();
      tmp$ = old;
    }
    return tmp$;
  };
  Object.defineProperty(KInput.prototype, 'updateMode', {
    get: function () {
      return this.updateMode_vtcax8$_0;
    }
  });
  KInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KInput',
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
  function UpdateNotSupportedException(className) {
    SerializationException.call(this, 'Update is not supported for ' + className);
    this.name = 'UpdateNotSupportedException';
  }
  UpdateNotSupportedException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UpdateNotSupportedException',
    interfaces: [SerializationException]
  };
  function ElementValueOutput() {
    KOutput.call(this);
  }
  ElementValueOutput.prototype.writeElement_xvmgof$ = function (desc, index) {
    return true;
  };
  ElementValueOutput.prototype.writeNotNullMark = function () {
  };
  ElementValueOutput.prototype.writeNonSerializableValue_za3rmp$ = function (value) {
    throw new SerializationException('"' + value + '"' + ' has no serializer');
  };
  ElementValueOutput.prototype.writeNullableValue_s8jyv4$ = function (value) {
    if (value == null) {
      this.writeNullValue();
    }
     else {
      this.writeNotNullMark();
      this.writeValue_za3rmp$(value);
    }
  };
  ElementValueOutput.prototype.writeNullValue = function () {
    throw new SerializationException('null is not supported');
  };
  ElementValueOutput.prototype.writeUnitValue = function () {
    var output = this.writeBegin_276rha$(UnitSerializer_getInstance().serialClassDesc, []);
    output.writeEnd_f6e2p$(UnitSerializer_getInstance().serialClassDesc);
  };
  ElementValueOutput.prototype.writeBooleanValue_6taknv$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeByteValue_s8j3t7$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeShortValue_mq22fl$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeIntValue_za3lpa$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeLongValue_s8cxhz$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeFloatValue_mx4ult$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeDoubleValue_14dthe$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeCharValue_s8itvh$ = function (value) {
    this.writeValue_za3rmp$(toBoxedChar(value));
  };
  ElementValueOutput.prototype.writeStringValue_61zpoe$ = function (value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeEnumValue_9pl89b$ = function (enumClass, value) {
    this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeNonSerializableElementValue_j8uhfo$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeValue_za3rmp$(value);
  };
  ElementValueOutput.prototype.writeNullableElementValue_sdckn1$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeNullableValue_s8jyv4$(value);
  };
  ElementValueOutput.prototype.writeUnitElementValue_xvmgof$ = function (desc, index) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeUnitValue();
  };
  ElementValueOutput.prototype.writeBooleanElementValue_gw9ugo$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeBooleanValue_6taknv$(value);
  };
  ElementValueOutput.prototype.writeByteElementValue_sdbpl4$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeByteValue_s8j3t7$(value);
  };
  ElementValueOutput.prototype.writeShortElementValue_quoth0$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeShortValue_mq22fl$(value);
  };
  ElementValueOutput.prototype.writeIntElementValue_j8ubi9$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeIntValue_za3lpa$(value);
  };
  ElementValueOutput.prototype.writeLongElementValue_sd5j9w$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeLongValue_s8cxhz$(value);
  };
  ElementValueOutput.prototype.writeFloatElementValue_r1rln8$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeFloatValue_mx4ult$(value);
  };
  ElementValueOutput.prototype.writeDoubleElementValue_cy908x$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeDoubleValue_14dthe$(value);
  };
  ElementValueOutput.prototype.writeCharElementValue_sdbfne$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeCharValue_s8itvh$(value);
  };
  ElementValueOutput.prototype.writeStringElementValue_k4mjep$ = function (desc, index, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeStringValue_61zpoe$(value);
  };
  ElementValueOutput.prototype.writeEnumElementValue_bta54i$ = function (desc, index, enumClass, value) {
    if (this.writeElement_xvmgof$(desc, index))
      this.writeEnumValue_9pl89b$(enumClass, value);
  };
  ElementValueOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ElementValueOutput',
    interfaces: [KOutput]
  };
  function ElementValueInput() {
    KInput.call(this);
  }
  ElementValueInput.prototype.readElement_f6e2p$ = function (desc) {
    return KInput$Companion_getInstance().READ_ALL;
  };
  ElementValueInput.prototype.readNotNullMark = function () {
    return true;
  };
  ElementValueInput.prototype.readNullValue = function () {
    return null;
  };
  ElementValueInput.prototype.readValue = function () {
    throw new SerializationException('Any type is not supported');
  };
  ElementValueInput.prototype.readNullableValue = function () {
    return this.readNotNullMark() ? this.readValue() : this.readNullValue();
  };
  ElementValueInput.prototype.readUnitValue = function () {
    var reader = this.readBegin_276rha$(UnitSerializer_getInstance().serialClassDesc, []);
    reader.readEnd_f6e2p$(UnitSerializer_getInstance().serialClassDesc);
  };
  ElementValueInput.prototype.readBooleanValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'boolean' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readByteValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'number' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readShortValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'number' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readIntValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'number' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readLongValue = function () {
    var tmp$;
    return Kotlin.isType(tmp$ = this.readValue(), Kotlin.Long) ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readFloatValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'number' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readDoubleValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'number' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readCharValue = function () {
    var tmp$;
    return Kotlin.isChar(tmp$ = this.readValue()) ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readStringValue = function () {
    var tmp$;
    return typeof (tmp$ = this.readValue()) === 'string' ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readEnumValue_xvqrpl$ = function (enumClass) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.readValue(), Enum) ? tmp$ : throwCCE();
  };
  ElementValueInput.prototype.readElementValue_xvmgof$ = function (desc, index) {
    return this.readValue();
  };
  ElementValueInput.prototype.readNullableElementValue_xvmgof$ = function (desc, index) {
    return this.readNullableValue();
  };
  ElementValueInput.prototype.readUnitElementValue_xvmgof$ = function (desc, index) {
    this.readUnitValue();
  };
  ElementValueInput.prototype.readBooleanElementValue_xvmgof$ = function (desc, index) {
    return this.readBooleanValue();
  };
  ElementValueInput.prototype.readByteElementValue_xvmgof$ = function (desc, index) {
    return this.readByteValue();
  };
  ElementValueInput.prototype.readShortElementValue_xvmgof$ = function (desc, index) {
    return this.readShortValue();
  };
  ElementValueInput.prototype.readIntElementValue_xvmgof$ = function (desc, index) {
    return this.readIntValue();
  };
  ElementValueInput.prototype.readLongElementValue_xvmgof$ = function (desc, index) {
    return this.readLongValue();
  };
  ElementValueInput.prototype.readFloatElementValue_xvmgof$ = function (desc, index) {
    return this.readFloatValue();
  };
  ElementValueInput.prototype.readDoubleElementValue_xvmgof$ = function (desc, index) {
    return this.readDoubleValue();
  };
  ElementValueInput.prototype.readCharElementValue_xvmgof$ = function (desc, index) {
    return this.readCharValue();
  };
  ElementValueInput.prototype.readStringElementValue_xvmgof$ = function (desc, index) {
    return this.readStringValue();
  };
  ElementValueInput.prototype.readEnumElementValue_61hxlg$ = function (desc, index, enumClass) {
    return this.readEnumValue_xvqrpl$(enumClass);
  };
  ElementValueInput.prototype.readSerializableElementValue_nqb5fm$ = function (desc, index, loader) {
    return this.readSerializableValue_rf0fz3$(loader);
  };
  ElementValueInput.prototype.readNullableSerializableElementValue_fcqp7f$ = function (desc, index, loader) {
    return this.readNullableSerializableValue_1n8rgi$(loader);
  };
  ElementValueInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ElementValueInput',
    interfaces: [KInput]
  };
  function ValueTransformer() {
  }
  ValueTransformer.prototype.transform_nleje8$ = function (serializer, obj) {
    var output = new ValueTransformer$Output(this);
    output.write_jsy488$(serializer, obj);
    var input = new ValueTransformer$Input(this, output.list_8be2vx$);
    return input.read_rf0fz3$(serializer);
  };
  ValueTransformer.prototype.transform_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.ValueTransformer.transform_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, obj) {
      return this.transform_nleje8$(serializer(getKClass(T_0)), obj);
    };
  }));
  ValueTransformer.prototype.transformBooleanValue_gw9ugo$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformByteValue_sdbpl4$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformShortValue_quoth0$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformIntValue_j8ubi9$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformLongValue_sd5j9w$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformFloatValue_r1rln8$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformDoubleValue_cy908x$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformCharValue_sdbfne$ = function (desc, index, value) {
    return toBoxedChar(value);
  };
  ValueTransformer.prototype.transformStringValue_k4mjep$ = function (desc, index, value) {
    return value;
  };
  ValueTransformer.prototype.transformEnumValue_bta54i$ = function (desc, index, enumClass, value) {
    return value;
  };
  ValueTransformer.prototype.isRecursiveTransform = function () {
    return true;
  };
  function ValueTransformer$Output($outer) {
    this.$outer = $outer;
    KOutput.call(this);
    this.list_8be2vx$ = ArrayList_init_0();
  }
  ValueTransformer$Output.prototype.writeNullableValue_s8jyv4$ = function (value) {
    this.list_8be2vx$.add_11rb$(value);
  };
  ValueTransformer$Output.prototype.writeElement_xvmgof$ = function (desc, index) {
    return true;
  };
  ValueTransformer$Output.prototype.writeNotNullMark = function () {
  };
  ValueTransformer$Output.prototype.writeNullValue = function () {
    this.writeNullableValue_s8jyv4$(null);
  };
  ValueTransformer$Output.prototype.writeNonSerializableValue_za3rmp$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeUnitValue = function () {
    this.writeNullableValue_s8jyv4$(Unit);
  };
  ValueTransformer$Output.prototype.writeBooleanValue_6taknv$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeByteValue_s8j3t7$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeShortValue_mq22fl$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeIntValue_za3lpa$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeLongValue_s8cxhz$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeFloatValue_mx4ult$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeDoubleValue_14dthe$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeCharValue_s8itvh$ = function (value) {
    this.writeNullableValue_s8jyv4$(toBoxedChar(value));
  };
  ValueTransformer$Output.prototype.writeStringValue_61zpoe$ = function (value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeEnumValue_9pl89b$ = function (enumClass, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeSerializableValue_jsy488$ = function (saver, value) {
    if (this.$outer.isRecursiveTransform()) {
      saver.save_ejfkry$(this, value);
    }
     else
      this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeNonSerializableElementValue_j8uhfo$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeNullableElementValue_sdckn1$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeUnitElementValue_xvmgof$ = function (desc, index) {
    this.writeNullableValue_s8jyv4$(Unit);
  };
  ValueTransformer$Output.prototype.writeBooleanElementValue_gw9ugo$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeByteElementValue_sdbpl4$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeShortElementValue_quoth0$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeIntElementValue_j8ubi9$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeLongElementValue_sd5j9w$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeFloatElementValue_r1rln8$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeDoubleElementValue_cy908x$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeCharElementValue_sdbfne$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(toBoxedChar(value));
  };
  ValueTransformer$Output.prototype.writeStringElementValue_k4mjep$ = function (desc, index, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.prototype.writeEnumElementValue_bta54i$ = function (desc, index, enumClass, value) {
    this.writeNullableValue_s8jyv4$(value);
  };
  ValueTransformer$Output.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Output',
    interfaces: [KOutput]
  };
  function ValueTransformer$Input($outer, list) {
    this.$outer = $outer;
    KInput.call(this);
    this.list_0 = list;
    this.index_0 = 0;
    this.curDesc_0 = null;
    this.curIndex_0 = 0;
  }
  ValueTransformer$Input.prototype.cur_0 = function (desc, index) {
    this.curDesc_0 = desc;
    this.curIndex_0 = index;
  };
  ValueTransformer$Input.prototype.readNotNullMark = function () {
    return this.list_0.get_za3lpa$(this.index_0) != null;
  };
  ValueTransformer$Input.prototype.readNullValue = function () {
    this.index_0 = this.index_0 + 1 | 0;
    return null;
  };
  ValueTransformer$Input.prototype.readValue = function () {
    var tmp$;
    return ensureNotNull(this.list_0.get_za3lpa$((tmp$ = this.index_0, this.index_0 = tmp$ + 1 | 0, tmp$)));
  };
  ValueTransformer$Input.prototype.readNullableValue = function () {
    var tmp$;
    return this.list_0.get_za3lpa$((tmp$ = this.index_0, this.index_0 = tmp$ + 1 | 0, tmp$));
  };
  ValueTransformer$Input.prototype.readUnitValue = function () {
    this.index_0 = this.index_0 + 1 | 0;
  };
  ValueTransformer$Input.prototype.readBooleanValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'boolean' ? tmp$_1 : throwCCE();
    return this.$outer.transformBooleanValue_gw9ugo$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readByteValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'number' ? tmp$_1 : throwCCE();
    return this.$outer.transformByteValue_sdbpl4$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readShortValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'number' ? tmp$_1 : throwCCE();
    return this.$outer.transformShortValue_quoth0$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readIntValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'number' ? tmp$_1 : throwCCE();
    return this.$outer.transformIntValue_j8ubi9$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readLongValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = Kotlin.isType(tmp$_1 = this.readValue(), Kotlin.Long) ? tmp$_1 : throwCCE();
    return this.$outer.transformLongValue_sd5j9w$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readFloatValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'number' ? tmp$_1 : throwCCE();
    return this.$outer.transformFloatValue_r1rln8$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readDoubleValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'number' ? tmp$_1 : throwCCE();
    return this.$outer.transformDoubleValue_cy908x$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readCharValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = unboxChar(Kotlin.isChar(tmp$_1 = this.readValue()) ? tmp$_1 : throwCCE());
    return this.$outer.transformCharValue_sdbfne$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readStringValue = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = typeof (tmp$_1 = this.readValue()) === 'string' ? tmp$_1 : throwCCE();
    return this.$outer.transformStringValue_k4mjep$(tmp$, tmp$_0, tmp$_2);
  };
  ValueTransformer$Input.prototype.readEnumValue_xvqrpl$ = function (enumClass) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = ensureNotNull(this.curDesc_0);
    tmp$_0 = this.curIndex_0;
    tmp$_2 = Kotlin.isType(tmp$_1 = this.readValue(), Enum) ? tmp$_1 : throwCCE();
    return this.$outer.transformEnumValue_bta54i$(tmp$, tmp$_0, enumClass, tmp$_2);
  };
  ValueTransformer$Input.prototype.readSerializableValue_rf0fz3$ = function (loader) {
    var tmp$;
    if (this.$outer.isRecursiveTransform())
      return loader.load_ljkqvg$(this);
    else
      return (tmp$ = this.readValue()) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  ValueTransformer$Input.prototype.readElement_f6e2p$ = function (desc) {
    return KInput$Companion_getInstance().READ_ALL;
  };
  ValueTransformer$Input.prototype.readElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readValue();
  };
  ValueTransformer$Input.prototype.readNullableElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readNullableValue();
  };
  ValueTransformer$Input.prototype.readUnitElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readUnitValue();
  };
  ValueTransformer$Input.prototype.readBooleanElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readBooleanValue();
  };
  ValueTransformer$Input.prototype.readByteElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readByteValue();
  };
  ValueTransformer$Input.prototype.readShortElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readShortValue();
  };
  ValueTransformer$Input.prototype.readIntElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readIntValue();
  };
  ValueTransformer$Input.prototype.readLongElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readLongValue();
  };
  ValueTransformer$Input.prototype.readFloatElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readFloatValue();
  };
  ValueTransformer$Input.prototype.readDoubleElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readDoubleValue();
  };
  ValueTransformer$Input.prototype.readCharElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readCharValue();
  };
  ValueTransformer$Input.prototype.readStringElementValue_xvmgof$ = function (desc, index) {
    this.cur_0(desc, index);
    return this.readStringValue();
  };
  ValueTransformer$Input.prototype.readEnumElementValue_61hxlg$ = function (desc, index, enumClass) {
    this.cur_0(desc, index);
    return this.readEnumValue_xvqrpl$(enumClass);
  };
  ValueTransformer$Input.prototype.readSerializableElementValue_nqb5fm$ = function (desc, index, loader) {
    this.cur_0(desc, index);
    return this.readSerializableValue_rf0fz3$(loader);
  };
  ValueTransformer$Input.prototype.readNullableSerializableElementValue_fcqp7f$ = function (desc, index, loader) {
    this.cur_0(desc, index);
    return this.readNullableSerializableValue_1n8rgi$(loader);
  };
  ValueTransformer$Input.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Input',
    interfaces: [KInput]
  };
  ValueTransformer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ValueTransformer',
    interfaces: []
  };
  function SerialId(id) {
    this.id = id;
  }
  function SerialId$Impl() {
  }
  SerialId$Impl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Impl',
    interfaces: [SerialId]
  };
  SerialId.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialId',
    interfaces: [Annotation]
  };
  function SerialTag(tag) {
    this.tag = tag;
  }
  function SerialTag$Impl() {
  }
  SerialTag$Impl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Impl',
    interfaces: [SerialTag]
  };
  SerialTag.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialTag',
    interfaces: [Annotation]
  };
  function TaggedOutput() {
    KOutput.call(this);
    this.tagStack_m19g9s$_0 = ArrayList_init_0();
  }
  TaggedOutput.prototype.writeTaggedValue_dpg7wc$ = function (tag, value) {
    throw new SerializationException(value.toString() + ' is not supported');
  };
  TaggedOutput.prototype.writeTaggedNotNullMark_11rb$ = function (tag) {
  };
  TaggedOutput.prototype.writeTaggedNull_11rb$ = function (tag) {
    throw new SerializationException('null is not supported');
  };
  TaggedOutput.prototype.writeTaggedNullable_gmwdpb$_0 = function (tag, value) {
    if (value == null) {
      this.writeTaggedNull_11rb$(tag);
    }
     else {
      this.writeTaggedNotNullMark_11rb$(tag);
      this.writeTaggedValue_dpg7wc$(tag, value);
    }
  };
  TaggedOutput.prototype.writeTaggedUnit_11rb$ = function (tag) {
    this.writeTaggedValue_dpg7wc$(tag, Unit);
  };
  TaggedOutput.prototype.writeTaggedInt_dpg1yx$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedByte_19qe40$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedShort_veccj0$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedLong_19wkf8$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedFloat_vlf4p8$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedDouble_e37ph5$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedBoolean_iuyhfk$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedChar_19qo1q$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, toBoxedChar(value));
  };
  TaggedOutput.prototype.writeTaggedString_l9l8mx$ = function (tag, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeTaggedEnum_qffkiy$ = function (tag, enumClass, value) {
    this.writeTaggedValue_dpg7wc$(tag, value);
  };
  TaggedOutput.prototype.writeElement_xvmgof$ = function (desc, index) {
    var tag = this.getTag_fr5t0y$(desc, index);
    var shouldWriteElement = this.shouldWriteElement_6zine4$(desc, tag, index);
    if (shouldWriteElement) {
      this.pushTag_in68vz$_0(tag);
    }
    return shouldWriteElement;
  };
  TaggedOutput.prototype.shouldWriteElement_6zine4$ = function (desc, tag, index) {
    return true;
  };
  TaggedOutput.prototype.writeNotNullMark = function () {
    this.writeTaggedNotNullMark_11rb$(this.currentTag);
  };
  TaggedOutput.prototype.writeNullValue = function () {
    this.writeTaggedNull_11rb$(this.popTag_jx7gcl$_0());
  };
  TaggedOutput.prototype.writeNonSerializableValue_za3rmp$ = function (value) {
    this.writeTaggedValue_dpg7wc$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeNullableValue_s8jyv4$ = function (value) {
    this.writeTaggedNullable_gmwdpb$_0(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeUnitValue = function () {
    this.writeTaggedUnit_11rb$(this.popTag_jx7gcl$_0());
  };
  TaggedOutput.prototype.writeBooleanValue_6taknv$ = function (value) {
    this.writeTaggedBoolean_iuyhfk$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeByteValue_s8j3t7$ = function (value) {
    this.writeTaggedByte_19qe40$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeShortValue_mq22fl$ = function (value) {
    this.writeTaggedShort_veccj0$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeIntValue_za3lpa$ = function (value) {
    this.writeTaggedInt_dpg1yx$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeLongValue_s8cxhz$ = function (value) {
    this.writeTaggedLong_19wkf8$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeFloatValue_mx4ult$ = function (value) {
    this.writeTaggedFloat_vlf4p8$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeDoubleValue_14dthe$ = function (value) {
    this.writeTaggedDouble_e37ph5$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeCharValue_s8itvh$ = function (value) {
    this.writeTaggedChar_19qo1q$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeStringValue_61zpoe$ = function (value) {
    this.writeTaggedString_l9l8mx$(this.popTag_jx7gcl$_0(), value);
  };
  TaggedOutput.prototype.writeEnumValue_9pl89b$ = function (enumClass, value) {
    this.writeTaggedEnum_qffkiy$(this.popTag_jx7gcl$_0(), enumClass, value);
  };
  TaggedOutput.prototype.writeEnd_f6e2p$ = function (desc) {
    if (!this.tagStack_m19g9s$_0.isEmpty())
      this.popTag_jx7gcl$_0();
    this.writeFinished_f6e2p$(desc);
  };
  TaggedOutput.prototype.writeFinished_f6e2p$ = function (desc) {
  };
  TaggedOutput.prototype.writeNonSerializableElementValue_j8uhfo$ = function (desc, index, value) {
    this.writeTaggedValue_dpg7wc$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeNullableElementValue_sdckn1$ = function (desc, index, value) {
    this.writeTaggedNullable_gmwdpb$_0(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeUnitElementValue_xvmgof$ = function (desc, index) {
    this.writeTaggedUnit_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedOutput.prototype.writeBooleanElementValue_gw9ugo$ = function (desc, index, value) {
    this.writeTaggedBoolean_iuyhfk$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeByteElementValue_sdbpl4$ = function (desc, index, value) {
    this.writeTaggedByte_19qe40$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeShortElementValue_quoth0$ = function (desc, index, value) {
    this.writeTaggedShort_veccj0$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeIntElementValue_j8ubi9$ = function (desc, index, value) {
    this.writeTaggedInt_dpg1yx$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeLongElementValue_sd5j9w$ = function (desc, index, value) {
    this.writeTaggedLong_19wkf8$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeFloatElementValue_r1rln8$ = function (desc, index, value) {
    this.writeTaggedFloat_vlf4p8$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeDoubleElementValue_cy908x$ = function (desc, index, value) {
    this.writeTaggedDouble_e37ph5$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeCharElementValue_sdbfne$ = function (desc, index, value) {
    this.writeTaggedChar_19qo1q$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeStringElementValue_k4mjep$ = function (desc, index, value) {
    this.writeTaggedString_l9l8mx$(this.getTag_fr5t0y$(desc, index), value);
  };
  TaggedOutput.prototype.writeEnumElementValue_bta54i$ = function (desc, index, enumClass, value) {
    this.writeTaggedEnum_qffkiy$(this.getTag_fr5t0y$(desc, index), enumClass, value);
  };
  Object.defineProperty(TaggedOutput.prototype, 'currentTag', {
    get: function () {
      return last(this.tagStack_m19g9s$_0);
    }
  });
  Object.defineProperty(TaggedOutput.prototype, 'currentTagOrNull', {
    get: function () {
      return lastOrNull(this.tagStack_m19g9s$_0);
    }
  });
  TaggedOutput.prototype.pushTag_in68vz$_0 = function (name) {
    this.tagStack_m19g9s$_0.add_11rb$(name);
  };
  TaggedOutput.prototype.popTag_jx7gcl$_0 = function () {
    return this.tagStack_m19g9s$_0.removeAt_za3lpa$(get_lastIndex(this.tagStack_m19g9s$_0));
  };
  TaggedOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TaggedOutput',
    interfaces: [KOutput]
  };
  function IntTaggedOutput() {
    TaggedOutput.call(this);
  }
  IntTaggedOutput.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$;
    var $receiver_0 = $receiver.getAnnotationsForIndex_za3lpa$(index);
    var destination = ArrayList_init_0();
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      if (Kotlin.isType(element, SerialId))
        destination.add_11rb$(element);
    }
    return (tmp$ = singleOrNull(destination)) != null ? tmp$.id : null;
  };
  IntTaggedOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IntTaggedOutput',
    interfaces: [TaggedOutput]
  };
  function StringTaggedOutput() {
    TaggedOutput.call(this);
  }
  StringTaggedOutput.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$;
    var $receiver_0 = $receiver.getAnnotationsForIndex_za3lpa$(index);
    var destination = ArrayList_init_0();
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      if (Kotlin.isType(element, SerialTag))
        destination.add_11rb$(element);
    }
    return (tmp$ = singleOrNull(destination)) != null ? tmp$.tag : null;
  };
  StringTaggedOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StringTaggedOutput',
    interfaces: [TaggedOutput]
  };
  function NamedValueOutput(rootName) {
    if (rootName === void 0)
      rootName = '';
    TaggedOutput.call(this);
    this.rootName = rootName;
  }
  NamedValueOutput.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$;
    return this.composeName_puj7f4$((tmp$ = this.currentTagOrNull) != null ? tmp$ : this.rootName, this.elementName_xvmgof$($receiver, index));
  };
  NamedValueOutput.prototype.elementName_xvmgof$ = function (desc, index) {
    return desc.getElementName_za3lpa$(index);
  };
  NamedValueOutput.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return parentName.length === 0 ? childName : parentName + '.' + childName;
  };
  NamedValueOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NamedValueOutput',
    interfaces: [TaggedOutput]
  };
  function TaggedInput() {
    KInput.call(this);
    this.tagStack_56p8av$_0 = ArrayList_init_0();
    this.flag_expvl5$_0 = false;
  }
  TaggedInput.prototype.readTaggedValue_11rb$ = function (tag) {
    throw new SerializationException('value is not supported for ' + tag);
  };
  TaggedInput.prototype.readTaggedNotNullMark_11rb$ = function (tag) {
    return true;
  };
  TaggedInput.prototype.readTaggedNull_11rb$ = function (tag) {
    return null;
  };
  TaggedInput.prototype.readTaggedNullable_huhdt7$_0 = function (tag) {
    var tmp$;
    if (this.readTaggedNotNullMark_11rb$(tag)) {
      tmp$ = this.readTaggedValue_11rb$(tag);
    }
     else {
      tmp$ = this.readTaggedNull_11rb$(tag);
    }
    return tmp$;
  };
  TaggedInput.prototype.readTaggedUnit_11rb$ = function (tag) {
    var tmp$;
    Kotlin.isType(tmp$ = this.readTaggedValue_11rb$(tag), Object.getPrototypeOf(kotlin.Unit).constructor) ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedBoolean_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'boolean' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedByte_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedShort_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedInt_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedLong_11rb$ = function (tag) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.readTaggedValue_11rb$(tag), Kotlin.Long) ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedFloat_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedDouble_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'number' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedChar_11rb$ = function (tag) {
    var tmp$;
    return Kotlin.isChar(tmp$ = this.readTaggedValue_11rb$(tag)) ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedString_11rb$ = function (tag) {
    var tmp$;
    return typeof (tmp$ = this.readTaggedValue_11rb$(tag)) === 'string' ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readTaggedEnum_bu9nms$ = function (tag, enumClass) {
    var tmp$;
    return Kotlin.isType(tmp$ = this.readTaggedValue_11rb$(tag), Enum) ? tmp$ : throwCCE();
  };
  TaggedInput.prototype.readNotNullMark = function () {
    return this.readTaggedNotNullMark_11rb$(this.currentTag);
  };
  TaggedInput.prototype.readNullValue = function () {
    return null;
  };
  TaggedInput.prototype.readValue = function () {
    return this.readTaggedValue_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readNullableValue = function () {
    return this.readTaggedNullable_huhdt7$_0(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readUnitValue = function () {
    this.readTaggedUnit_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readBooleanValue = function () {
    return this.readTaggedBoolean_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readByteValue = function () {
    return this.readTaggedByte_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readShortValue = function () {
    return this.readTaggedShort_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readIntValue = function () {
    return this.readTaggedInt_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readLongValue = function () {
    return this.readTaggedLong_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readFloatValue = function () {
    return this.readTaggedFloat_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readDoubleValue = function () {
    return this.readTaggedDouble_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readCharValue = function () {
    return this.readTaggedChar_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readStringValue = function () {
    return this.readTaggedString_11rb$(this.popTag_c7udbg$_0());
  };
  TaggedInput.prototype.readEnumValue_xvqrpl$ = function (enumClass) {
    return this.readTaggedEnum_bu9nms$(this.popTag_c7udbg$_0(), enumClass);
  };
  TaggedInput.prototype.readElement_f6e2p$ = function (desc) {
    return KInput$Companion_getInstance().READ_ALL;
  };
  TaggedInput.prototype.readElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedValue_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readNullableElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedNullable_huhdt7$_0(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readUnitElementValue_xvmgof$ = function (desc, index) {
    this.readTaggedUnit_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readBooleanElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedBoolean_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readByteElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedByte_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readShortElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedShort_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readIntElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedInt_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readLongElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedLong_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readFloatElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedFloat_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readDoubleElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedDouble_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readCharElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedChar_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readStringElementValue_xvmgof$ = function (desc, index) {
    return this.readTaggedString_11rb$(this.getTag_fr5t0y$(desc, index));
  };
  TaggedInput.prototype.readEnumElementValue_61hxlg$ = function (desc, index, enumClass) {
    return this.readTaggedEnum_bu9nms$(this.getTag_fr5t0y$(desc, index), enumClass);
  };
  function TaggedInput$readSerializableElementValue$lambda(closure$loader, this$TaggedInput) {
    return function () {
      return this$TaggedInput.readSerializableValue_rf0fz3$(closure$loader);
    };
  }
  TaggedInput.prototype.readSerializableElementValue_nqb5fm$ = function (desc, index, loader) {
    return this.tagBlock_7tnsu5$_0(this.getTag_fr5t0y$(desc, index), TaggedInput$readSerializableElementValue$lambda(loader, this));
  };
  function TaggedInput$readNullableSerializableElementValue$lambda(closure$loader, this$TaggedInput) {
    return function () {
      return this$TaggedInput.readNullableSerializableValue_1n8rgi$(closure$loader);
    };
  }
  TaggedInput.prototype.readNullableSerializableElementValue_fcqp7f$ = function (desc, index, loader) {
    return this.tagBlock_7tnsu5$_0(this.getTag_fr5t0y$(desc, index), TaggedInput$readNullableSerializableElementValue$lambda(loader, this));
  };
  function TaggedInput$updateSerializableElementValue$lambda(closure$loader, closure$desc, closure$old, this$TaggedInput) {
    return function () {
      return this$TaggedInput.updateSerializableValue_3jm06w$(closure$loader, closure$desc, closure$old);
    };
  }
  TaggedInput.prototype.updateSerializableElementValue_2bgl1k$ = function (desc, index, loader, old) {
    return this.tagBlock_7tnsu5$_0(this.getTag_fr5t0y$(desc, index), TaggedInput$updateSerializableElementValue$lambda(loader, desc, old, this));
  };
  function TaggedInput$updateNullableSerializableElementValue$lambda(closure$loader, closure$desc, closure$old, this$TaggedInput) {
    return function () {
      return this$TaggedInput.updateNullableSerializableValue_2rkmol$(closure$loader, closure$desc, closure$old);
    };
  }
  TaggedInput.prototype.updateNullableSerializableElementValue_xspi39$ = function (desc, index, loader, old) {
    return this.tagBlock_7tnsu5$_0(this.getTag_fr5t0y$(desc, index), TaggedInput$updateNullableSerializableElementValue$lambda(loader, desc, old, this));
  };
  TaggedInput.prototype.tagBlock_7tnsu5$_0 = function (tag, block) {
    this.pushTag_ffahlk$_0(tag);
    var r = block();
    if (!this.flag_expvl5$_0) {
      this.popTag_c7udbg$_0();
    }
    this.flag_expvl5$_0 = false;
    return r;
  };
  Object.defineProperty(TaggedInput.prototype, 'currentTag', {
    get: function () {
      return last(this.tagStack_56p8av$_0);
    }
  });
  Object.defineProperty(TaggedInput.prototype, 'currentTagOrNull', {
    get: function () {
      return lastOrNull(this.tagStack_56p8av$_0);
    }
  });
  TaggedInput.prototype.pushTag_ffahlk$_0 = function (name) {
    this.tagStack_56p8av$_0.add_11rb$(name);
  };
  TaggedInput.prototype.popTag_c7udbg$_0 = function () {
    var r = this.tagStack_56p8av$_0.removeAt_za3lpa$(get_lastIndex(this.tagStack_56p8av$_0));
    this.flag_expvl5$_0 = true;
    return r;
  };
  TaggedInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TaggedInput',
    interfaces: [KInput]
  };
  function IntTaggedInput() {
    TaggedInput.call(this);
  }
  IntTaggedInput.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$;
    var $receiver_0 = $receiver.getAnnotationsForIndex_za3lpa$(index);
    var destination = ArrayList_init_0();
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      if (Kotlin.isType(element, SerialId))
        destination.add_11rb$(element);
    }
    return (tmp$ = singleOrNull(destination)) != null ? tmp$.id : null;
  };
  IntTaggedInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IntTaggedInput',
    interfaces: [TaggedInput]
  };
  function StringTaggedInput() {
    TaggedInput.call(this);
  }
  StringTaggedInput.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$;
    var $receiver_0 = $receiver.getAnnotationsForIndex_za3lpa$(index);
    var destination = ArrayList_init_0();
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      if (Kotlin.isType(element, SerialTag))
        destination.add_11rb$(element);
    }
    return (tmp$ = singleOrNull(destination)) != null ? tmp$.tag : null;
  };
  StringTaggedInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StringTaggedInput',
    interfaces: [TaggedInput]
  };
  function NamedValueInput(rootName) {
    if (rootName === void 0)
      rootName = '';
    TaggedInput.call(this);
    this.rootName = rootName;
  }
  NamedValueInput.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$;
    return this.composeName_puj7f4$((tmp$ = this.currentTagOrNull) != null ? tmp$ : this.rootName, this.elementName_xvmgof$($receiver, index));
  };
  NamedValueInput.prototype.elementName_xvmgof$ = function (desc, index) {
    return desc.getElementName_za3lpa$(index);
  };
  NamedValueInput.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return parentName.length === 0 ? childName : parentName + '.' + childName;
  };
  NamedValueInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NamedValueInput',
    interfaces: [TaggedInput]
  };
  function Mapper() {
    Mapper_instance = this;
  }
  function Mapper$OutMapper() {
    NamedValueOutput.call(this);
    this._map_0 = LinkedHashMap_init();
  }
  Object.defineProperty(Mapper$OutMapper.prototype, 'map', {
    get: function () {
      return this._map_0;
    }
  });
  Mapper$OutMapper.prototype.writeTaggedValue_dpg7wc$ = function (tag, value) {
    this._map_0.put_xwzc9p$(tag, value);
  };
  Mapper$OutMapper.prototype.writeTaggedNull_11rb$ = function (tag) {
    throw new SerializationException('null is not supported. use Mapper.mapNullable()/OutNullableMapper instead');
  };
  Mapper$OutMapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OutMapper',
    interfaces: [NamedValueOutput]
  };
  function Mapper$OutNullableMapper() {
    NamedValueOutput.call(this);
    this._map_0 = LinkedHashMap_init();
  }
  Object.defineProperty(Mapper$OutNullableMapper.prototype, 'map', {
    get: function () {
      return this._map_0;
    }
  });
  Mapper$OutNullableMapper.prototype.writeTaggedValue_dpg7wc$ = function (tag, value) {
    this._map_0.put_xwzc9p$(tag, value);
  };
  Mapper$OutNullableMapper.prototype.writeTaggedNull_11rb$ = function (tag) {
    this._map_0.put_xwzc9p$(tag, null);
  };
  Mapper$OutNullableMapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OutNullableMapper',
    interfaces: [NamedValueOutput]
  };
  function Mapper$InMapper(map) {
    NamedValueInput.call(this);
    this.map = map;
  }
  Mapper$InMapper.prototype.readTaggedValue_11rb$ = function (tag) {
    return getValue(this.map, tag);
  };
  Mapper$InMapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InMapper',
    interfaces: [NamedValueInput]
  };
  function Mapper$InNullableMapper(map) {
    NamedValueInput.call(this);
    this.map = map;
  }
  Mapper$InNullableMapper.prototype.readTaggedValue_11rb$ = function (tag) {
    return ensureNotNull(getValue(this.map, tag));
  };
  Mapper$InNullableMapper.prototype.readTaggedNotNullMark_11rb$ = function (tag) {
    return getValue(this.map, tag) != null;
  };
  Mapper$InNullableMapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InNullableMapper',
    interfaces: [NamedValueInput]
  };
  Mapper.prototype.map_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.Mapper.map_issdgt$', wrapFunction(function () {
    var Mapper$Mapper$OutMapper_init = _.kotlinx.serialization.Mapper.OutMapper;
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, obj) {
      var m = new Mapper$Mapper$OutMapper_init();
      m.write_jsy488$(serializer(getKClass(T_0)), obj);
      return m.map;
    };
  }));
  Mapper.prototype.mapNullable_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.Mapper.mapNullable_issdgt$', wrapFunction(function () {
    var Mapper$Mapper$OutNullableMapper_init = _.kotlinx.serialization.Mapper.OutNullableMapper;
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, obj) {
      var m = new Mapper$Mapper$OutNullableMapper_init();
      m.write_jsy488$(serializer(getKClass(T_0)), obj);
      return m.map;
    };
  }));
  Mapper.prototype.unmap_67iyj5$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.Mapper.unmap_67iyj5$', wrapFunction(function () {
    var Mapper$Mapper$InMapper_init = _.kotlinx.serialization.Mapper.InMapper;
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, map) {
      var m = new Mapper$Mapper$InMapper_init(map);
      return m.read_rf0fz3$(serializer(getKClass(T_0)));
    };
  }));
  Mapper.prototype.unmapNullable_mez6f0$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.Mapper.unmapNullable_mez6f0$', wrapFunction(function () {
    var Mapper$Mapper$InNullableMapper_init = _.kotlinx.serialization.Mapper.InNullableMapper;
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, map) {
      var m = new Mapper$Mapper$InNullableMapper_init(map);
      return m.read_rf0fz3$(serializer(getKClass(T_0)));
    };
  }));
  Mapper.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Mapper',
    interfaces: []
  };
  var Mapper_instance = null;
  function Mapper_getInstance() {
    if (Mapper_instance === null) {
      new Mapper();
    }
    return Mapper_instance;
  }
  function CBOR(context, updateMode) {
    CBOR$Companion_getInstance();
    if (context === void 0)
      context = null;
    if (updateMode === void 0)
      updateMode = UpdateMode$BANNED_getInstance();
    this.context = context;
    this.updateMode = updateMode;
  }
  function CBOR$CBOREntryWriter($outer, encoder) {
    this.$outer = $outer;
    CBOR$CBORWriter.call(this, this.$outer, encoder);
  }
  CBOR$CBOREntryWriter.prototype.writeBeginToken = function () {
  };
  CBOR$CBOREntryWriter.prototype.writeEnd_f6e2p$ = function (desc) {
  };
  CBOR$CBOREntryWriter.prototype.writeElement_xvmgof$ = function (desc, index) {
    return true;
  };
  CBOR$CBOREntryWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBOREntryWriter',
    interfaces: [CBOR$CBORWriter]
  };
  function CBOR$CBORMapWriter($outer, encoder) {
    this.$outer = $outer;
    CBOR$CBORListWriter.call(this, this.$outer, encoder);
  }
  CBOR$CBORMapWriter.prototype.writeBeginToken = function () {
    this.encoder.startMap();
  };
  CBOR$CBORMapWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORMapWriter',
    interfaces: [CBOR$CBORListWriter]
  };
  function CBOR$CBORListWriter($outer, encoder) {
    this.$outer = $outer;
    CBOR$CBORWriter.call(this, this.$outer, encoder);
  }
  CBOR$CBORListWriter.prototype.writeBeginToken = function () {
    this.encoder.startArray();
  };
  CBOR$CBORListWriter.prototype.writeElement_xvmgof$ = function (desc, index) {
    return !equals(desc.getElementName_za3lpa$(index), 'size');
  };
  CBOR$CBORListWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORListWriter',
    interfaces: [CBOR$CBORWriter]
  };
  function CBOR$CBORWriter($outer, encoder) {
    this.$outer = $outer;
    ElementValueOutput.call(this);
    this.encoder = encoder;
    this.context = this.$outer.context;
  }
  CBOR$CBORWriter.prototype.writeBeginToken = function () {
    this.encoder.startMap();
  };
  CBOR$CBORWriter.prototype.writeBegin_276rha$ = function (desc, typeParams) {
    var tmp$;
    switch (desc.kind.name) {
      case 'LIST':
      case 'SET':
        tmp$ = new CBOR$CBORListWriter(this.$outer, this.encoder);
        break;
      case 'MAP':
        tmp$ = new CBOR$CBORMapWriter(this.$outer, this.encoder);
        break;
      case 'ENTRY':
        tmp$ = new CBOR$CBOREntryWriter(this.$outer, this.encoder);
        break;
      default:tmp$ = new CBOR$CBORWriter(this.$outer, this.encoder);
        break;
    }
    var writer = tmp$;
    writer.writeBeginToken();
    return writer;
  };
  CBOR$CBORWriter.prototype.writeEnd_f6e2p$ = function (desc) {
    this.encoder.end();
  };
  CBOR$CBORWriter.prototype.writeElement_xvmgof$ = function (desc, index) {
    var name = desc.getElementName_za3lpa$(index);
    this.encoder.encodeString_61zpoe$(name);
    return true;
  };
  CBOR$CBORWriter.prototype.writeStringValue_61zpoe$ = function (value) {
    this.encoder.encodeString_61zpoe$(value);
  };
  CBOR$CBORWriter.prototype.writeFloatValue_mx4ult$ = function (value) {
    this.encoder.encodeFloat_mx4ult$(value);
  };
  CBOR$CBORWriter.prototype.writeDoubleValue_14dthe$ = function (value) {
    this.encoder.encodeDouble_14dthe$(value);
  };
  CBOR$CBORWriter.prototype.writeCharValue_s8itvh$ = function (value) {
    this.encoder.encodeNumber_s8cxhz$(Kotlin.Long.fromInt(value | 0));
  };
  CBOR$CBORWriter.prototype.writeByteValue_s8j3t7$ = function (value) {
    this.encoder.encodeNumber_s8cxhz$(Kotlin.Long.fromInt(value));
  };
  CBOR$CBORWriter.prototype.writeShortValue_mq22fl$ = function (value) {
    this.encoder.encodeNumber_s8cxhz$(Kotlin.Long.fromInt(value));
  };
  CBOR$CBORWriter.prototype.writeIntValue_za3lpa$ = function (value) {
    this.encoder.encodeNumber_s8cxhz$(Kotlin.Long.fromInt(value));
  };
  CBOR$CBORWriter.prototype.writeLongValue_s8cxhz$ = function (value) {
    this.encoder.encodeNumber_s8cxhz$(value);
  };
  CBOR$CBORWriter.prototype.writeBooleanValue_6taknv$ = function (value) {
    this.encoder.encodeBoolean_6taknv$(value);
  };
  CBOR$CBORWriter.prototype.writeNullValue = function () {
    this.encoder.encodeNull();
  };
  CBOR$CBORWriter.prototype.writeEnumValue_9pl89b$ = function (enumClass, value) {
    this.encoder.encodeString_61zpoe$(value.toString());
  };
  CBOR$CBORWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORWriter',
    interfaces: [ElementValueOutput]
  };
  function CBOR$CBOREncoder(output) {
    this.output = output;
  }
  CBOR$CBOREncoder.prototype.startArray = function () {
    this.output.write_za3lpa$(CBOR$Companion_getInstance().BEGIN_ARRAY_0);
  };
  CBOR$CBOREncoder.prototype.startMap = function () {
    this.output.write_za3lpa$(CBOR$Companion_getInstance().BEGIN_MAP_0);
  };
  CBOR$CBOREncoder.prototype.end = function () {
    this.output.write_za3lpa$(CBOR$Companion_getInstance().BREAK_0);
  };
  CBOR$CBOREncoder.prototype.encodeNull = function () {
    this.output.write_za3lpa$(CBOR$Companion_getInstance().NULL_0);
  };
  CBOR$CBOREncoder.prototype.encodeBoolean_6taknv$ = function (value) {
    this.output.write_za3lpa$(value ? CBOR$Companion_getInstance().TRUE_0 : CBOR$Companion_getInstance().FALSE_0);
  };
  CBOR$CBOREncoder.prototype.encodeNumber_s8cxhz$ = function (value) {
    this.output.write_fqrh44$(this.composeNumber_0(value));
  };
  CBOR$CBOREncoder.prototype.encodeString_61zpoe$ = function (value) {
    var data = toUtf8Bytes(value);
    var header = this.composeNumber_0(Kotlin.Long.fromInt(data.length));
    header[0] = toByte(header[0] | CBOR$Companion_getInstance().HEADER_STRING_0);
    this.output.write_fqrh44$(header);
    this.output.write_fqrh44$(data);
  };
  CBOR$CBOREncoder.prototype.encodeFloat_mx4ult$ = function (value) {
    var data = ByteBuffer$Companion_getInstance().allocate_za3lpa$(5).put_s8j3t7$(toByte(CBOR$Companion_getInstance().NEXT_FLOAT_0)).putFloat_mx4ult$(value).array();
    this.output.write_fqrh44$(data);
  };
  CBOR$CBOREncoder.prototype.encodeDouble_14dthe$ = function (value) {
    var data = ByteBuffer$Companion_getInstance().allocate_za3lpa$(9).put_s8j3t7$(toByte(CBOR$Companion_getInstance().NEXT_DOUBLE_0)).putDouble_14dthe$(value).array();
    this.output.write_fqrh44$(data);
  };
  CBOR$CBOREncoder.prototype.composeNumber_0 = function (value) {
    return value.compareTo_11rb$(Kotlin.Long.fromInt(0)) >= 0 ? this.composePositive_0(value) : this.composeNegative_0(value);
  };
  CBOR$CBOREncoder.prototype.composePositive_0 = function (value) {
    if (contains(new IntRange(0, 23), value))
      return new Int8Array([toByte(value.toInt())]);
    else if (contains(new IntRange(24, kotlin_js_internal_ByteCompanionObject.MAX_VALUE), value))
      return new Int8Array([24, toByte(value.toInt())]);
    else if (contains(new IntRange(kotlin_js_internal_ByteCompanionObject.MAX_VALUE + 1, kotlin_js_internal_ShortCompanionObject.MAX_VALUE), value))
      return ByteBuffer$Companion_getInstance().allocate_za3lpa$(3).put_s8j3t7$(toByte(25)).putShort_mq22fl$(toShort(value.toInt())).array();
    else if (contains(new IntRange(kotlin_js_internal_ShortCompanionObject.MAX_VALUE + 1, kotlin_js_internal_IntCompanionObject.MAX_VALUE), value))
      return ByteBuffer$Companion_getInstance().allocate_za3lpa$(5).put_s8j3t7$(toByte(26)).putInt_za3lpa$(value.toInt()).array();
    else {
      var tmp$, tmp$_0;
      tmp$ = new Kotlin.Long(-2147483648, 0);
      tmp$_0 = new Kotlin.Long(-1, 2147483647);
      if (tmp$.lessThanOrEqual(value) && value.lessThanOrEqual(tmp$_0))
        return ByteBuffer$Companion_getInstance().allocate_za3lpa$(9).put_s8j3t7$(toByte(27)).putLong_s8cxhz$(value).array();
      else
        throw IllegalArgumentException_init();
    }
  };
  CBOR$CBOREncoder.prototype.composeNegative_0 = function (value) {
    var aVal = equals(value, new Kotlin.Long(0, -2147483648)) ? new Kotlin.Long(-1, 2147483647) : Kotlin.Long.fromInt(-1).subtract(value);
    var data = this.composePositive_0(aVal);
    data[0] = toByte(data[0] | CBOR$Companion_getInstance().HEADER_NEGATIVE_0);
    return data;
  };
  CBOR$CBOREncoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBOREncoder',
    interfaces: []
  };
  function CBOR$CBOREntryReader($outer, decoder) {
    this.$outer = $outer;
    CBOR$CBORReader.call(this, this.$outer, decoder);
    this.ind_0 = 0;
  }
  CBOR$CBOREntryReader.prototype.skipBeginToken = function () {
  };
  CBOR$CBOREntryReader.prototype.readEnd_f6e2p$ = function (desc) {
  };
  CBOR$CBOREntryReader.prototype.readElement_f6e2p$ = function (desc) {
    var tmp$;
    switch (tmp$ = this.ind_0, this.ind_0 = tmp$ + 1 | 0, tmp$) {
      case 0:
        return 0;
      case 1:
        return 1;
      default:return KInput$Companion_getInstance().READ_DONE;
    }
  };
  CBOR$CBOREntryReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBOREntryReader',
    interfaces: [CBOR$CBORReader]
  };
  function CBOR$CBORMapReader($outer, decoder) {
    this.$outer = $outer;
    CBOR$CBORListReader.call(this, this.$outer, decoder);
  }
  CBOR$CBORMapReader.prototype.skipBeginToken = function () {
    this.decoder.startMap();
  };
  CBOR$CBORMapReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORMapReader',
    interfaces: [CBOR$CBORListReader]
  };
  function CBOR$CBORListReader($outer, decoder) {
    this.$outer = $outer;
    CBOR$CBORReader.call(this, this.$outer, decoder);
    this.ind_0 = 0;
    this.size_0 = -1;
    this.finiteMode_0 = false;
  }
  CBOR$CBORListReader.prototype.skipBeginToken = function () {
    var len = this.decoder.startArray();
    if (len !== -1) {
      this.finiteMode_0 = true;
      this.size_0 = len;
    }
  };
  CBOR$CBORListReader.prototype.readElement_f6e2p$ = function (desc) {
    return !this.finiteMode_0 && this.decoder.isEnd() || (this.finiteMode_0 && this.ind_0 >= this.size_0) ? KInput$Companion_getInstance().READ_DONE : (this.ind_0 = this.ind_0 + 1 | 0, this.ind_0);
  };
  CBOR$CBORListReader.prototype.readEnd_f6e2p$ = function (desc) {
    if (!this.finiteMode_0)
      this.decoder.end();
  };
  CBOR$CBORListReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORListReader',
    interfaces: [CBOR$CBORReader]
  };
  function CBOR$CBORReader($outer, decoder) {
    this.$outer = $outer;
    ElementValueInput.call(this);
    this.decoder = decoder;
    this.context = this.$outer.context;
  }
  Object.defineProperty(CBOR$CBORReader.prototype, 'updateMode', {
    get: function () {
      return this.$outer.updateMode;
    }
  });
  CBOR$CBORReader.prototype.skipBeginToken = function () {
    this.decoder.startMap();
  };
  CBOR$CBORReader.prototype.readBegin_276rha$ = function (desc, typeParams) {
    var tmp$;
    switch (desc.kind.name) {
      case 'LIST':
      case 'SET':
        tmp$ = new CBOR$CBORListReader(this.$outer, this.decoder);
        break;
      case 'MAP':
        tmp$ = new CBOR$CBORMapReader(this.$outer, this.decoder);
        break;
      case 'ENTRY':
        tmp$ = new CBOR$CBOREntryReader(this.$outer, this.decoder);
        break;
      default:tmp$ = new CBOR$CBORReader(this.$outer, this.decoder);
        break;
    }
    var re = tmp$;
    re.skipBeginToken();
    return re;
  };
  CBOR$CBORReader.prototype.readEnd_f6e2p$ = function (desc) {
    this.decoder.end();
  };
  CBOR$CBORReader.prototype.readElement_f6e2p$ = function (desc) {
    if (this.decoder.isEnd())
      return KInput$Companion_getInstance().READ_DONE;
    var elemName = this.decoder.nextString();
    return desc.getElementIndexOrThrow_61zpoe$(elemName);
  };
  CBOR$CBORReader.prototype.readStringValue = function () {
    return this.decoder.nextString();
  };
  CBOR$CBORReader.prototype.readNotNullMark = function () {
    return !this.decoder.isNull();
  };
  CBOR$CBORReader.prototype.readDoubleValue = function () {
    return this.decoder.nextDouble();
  };
  CBOR$CBORReader.prototype.readFloatValue = function () {
    return this.decoder.nextFloat();
  };
  CBOR$CBORReader.prototype.readBooleanValue = function () {
    return this.decoder.nextBoolean();
  };
  CBOR$CBORReader.prototype.readByteValue = function () {
    return toByte(this.decoder.nextNumber().toInt());
  };
  CBOR$CBORReader.prototype.readShortValue = function () {
    return toShort(this.decoder.nextNumber().toInt());
  };
  CBOR$CBORReader.prototype.readCharValue = function () {
    return toBoxedChar(toChar(this.decoder.nextNumber().toInt()));
  };
  CBOR$CBORReader.prototype.readIntValue = function () {
    return this.decoder.nextNumber().toInt();
  };
  CBOR$CBORReader.prototype.readLongValue = function () {
    return this.decoder.nextNumber();
  };
  CBOR$CBORReader.prototype.readNullValue = function () {
    return this.decoder.nextNull();
  };
  CBOR$CBORReader.prototype.readEnumValue_xvqrpl$ = function (enumClass) {
    return enumFromName(enumClass, this.decoder.nextString());
  };
  CBOR$CBORReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORReader',
    interfaces: [ElementValueInput]
  };
  function CBOR$CBORDecoder(input) {
    this.input = input;
    this.curByte_0 = -1;
    this.readByte_0();
  }
  CBOR$CBORDecoder.prototype.readByte_0 = function () {
    this.curByte_0 = this.input.read();
    return this.curByte_0;
  };
  CBOR$CBORDecoder.prototype.skipByte_0 = function (expected) {
    if (this.curByte_0 !== expected)
      throw new CBORParsingException('Expected byte ' + HexConverter_getInstance().toHexString_za3lpa$(expected) + ' , ' + ('but found ' + HexConverter_getInstance().toHexString_za3lpa$(this.curByte_0)));
    this.readByte_0();
  };
  CBOR$CBORDecoder.prototype.isNull = function () {
    return this.curByte_0 === CBOR$Companion_getInstance().NULL_0;
  };
  CBOR$CBORDecoder.prototype.nextNull = function () {
    this.skipByte_0(CBOR$Companion_getInstance().NULL_0);
    return null;
  };
  CBOR$CBORDecoder.prototype.nextBoolean = function () {
    var tmp$;
    switch (this.curByte_0) {
      case 245:
        tmp$ = true;
        break;
      case 244:
        tmp$ = false;
        break;
      default:throw new CBORParsingException('Expected boolean value');
    }
    var ans = tmp$;
    this.readByte_0();
    return ans;
  };
  CBOR$CBORDecoder.prototype.startArray = function () {
    if (this.curByte_0 === CBOR$Companion_getInstance().BEGIN_ARRAY_0) {
      this.skipByte_0(CBOR$Companion_getInstance().BEGIN_ARRAY_0);
      return -1;
    }
    if ((this.curByte_0 & 224) !== CBOR$Companion_getInstance().HEADER_ARRAY_0)
      throw new CBORParsingException('Expected start of array, but found ' + HexConverter_getInstance().toHexString_za3lpa$(this.curByte_0));
    var arrayLen = this.readNumber_0().toInt();
    this.readByte_0();
    return arrayLen;
  };
  CBOR$CBORDecoder.prototype.startMap = function () {
    this.skipByte_0(CBOR$Companion_getInstance().BEGIN_MAP_0);
  };
  CBOR$CBORDecoder.prototype.isEnd = function () {
    return this.curByte_0 === CBOR$Companion_getInstance().BREAK_0;
  };
  CBOR$CBORDecoder.prototype.end = function () {
    this.skipByte_0(CBOR$Companion_getInstance().BREAK_0);
  };
  CBOR$CBORDecoder.prototype.nextString = function () {
    if ((this.curByte_0 & 224) !== CBOR$Companion_getInstance().HEADER_STRING_0)
      throw new CBORParsingException('Expected start of string, but found ' + HexConverter_getInstance().toHexString_za3lpa$(this.curByte_0));
    var strLen = this.readNumber_0().toInt();
    var arr = readExactNBytes(this.input, strLen);
    var ans = stringFromUtf8Bytes(arr);
    this.readByte_0();
    return ans;
  };
  CBOR$CBORDecoder.prototype.nextNumber = function () {
    var res = this.readNumber_0();
    this.readByte_0();
    return res;
  };
  CBOR$CBORDecoder.prototype.readNumber_0 = function () {
    var tmp$, tmp$_0;
    var value = this.curByte_0 & 31;
    var negative = (this.curByte_0 & 224) === CBOR$Companion_getInstance().HEADER_NEGATIVE_0;
    switch (value) {
      case 24:
        tmp$ = 1;
        break;
      case 25:
        tmp$ = 2;
        break;
      case 26:
        tmp$ = 4;
        break;
      case 27:
        tmp$ = 8;
        break;
      default:tmp$ = 0;
        break;
    }
    var bytesToRead = tmp$;
    if (bytesToRead === 0) {
      if (negative)
        return Kotlin.Long.fromInt(value + 1 | 0).unaryMinus();
      else
        return Kotlin.Long.fromInt(value);
    }
    var buf = readToByteBuffer(this.input, bytesToRead);
    switch (bytesToRead) {
      case 1:
        tmp$_0 = Kotlin.Long.fromInt(getUnsignedByte(buf));
        break;
      case 2:
        tmp$_0 = Kotlin.Long.fromInt(getUnsignedShort(buf));
        break;
      case 4:
        tmp$_0 = getUnsignedInt(buf);
        break;
      case 8:
        tmp$_0 = buf.getLong();
        break;
      default:throw IllegalArgumentException_init();
    }
    var res = tmp$_0;
    if (negative)
      return res.add(Kotlin.Long.fromInt(1)).unaryMinus();
    else
      return res;
  };
  CBOR$CBORDecoder.prototype.nextFloat = function () {
    if (this.curByte_0 !== CBOR$Companion_getInstance().NEXT_FLOAT_0)
      throw new CBORParsingException('Expected float header, but found ' + HexConverter_getInstance().toHexString_za3lpa$(this.curByte_0));
    var res = readToByteBuffer(this.input, 4).getFloat();
    this.readByte_0();
    return res;
  };
  CBOR$CBORDecoder.prototype.nextDouble = function () {
    if (this.curByte_0 !== CBOR$Companion_getInstance().NEXT_DOUBLE_0)
      throw new CBORParsingException('Expected double header, but found ' + HexConverter_getInstance().toHexString_za3lpa$(this.curByte_0));
    var res = readToByteBuffer(this.input, 8).getDouble();
    this.readByte_0();
    return res;
  };
  CBOR$CBORDecoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORDecoder',
    interfaces: []
  };
  function CBOR$Companion() {
    CBOR$Companion_instance = this;
    this.FALSE_0 = 244;
    this.TRUE_0 = 245;
    this.NULL_0 = 246;
    this.NEXT_FLOAT_0 = 250;
    this.NEXT_DOUBLE_0 = 251;
    this.BEGIN_ARRAY_0 = 159;
    this.BEGIN_MAP_0 = 191;
    this.BREAK_0 = 255;
    this.HEADER_STRING_0 = 96;
    this.HEADER_NEGATIVE_0 = 32;
    this.HEADER_ARRAY_0 = 128;
    this.plain = new CBOR();
  }
  CBOR$Companion.prototype.dump_20fw5n$ = function (saver, obj) {
    return this.plain.dump_20fw5n$(saver, obj);
  };
  CBOR$Companion.prototype.dump_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.Companion.dump_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      var $this = this.plain;
      return $this.dump_20fw5n$(klassSerializer($this.context, getKClass(T_0)), obj);
    };
  }));
  CBOR$Companion.prototype.dumps_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.Companion.dumps_issdgt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      var $this = this.plain;
      return internal.HexConverter.printHexBinary_1fhb37$($this.dump_20fw5n$(klassSerializer($this.context, getKClass(T_0)), obj), true);
    };
  }));
  CBOR$Companion.prototype.load_8dtdds$ = function (loader, raw) {
    return this.plain.load_8dtdds$(loader, raw);
  };
  CBOR$Companion.prototype.load_5geitx$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.Companion.load_5geitx$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, raw) {
      var $this = this.plain;
      return $this.load_8dtdds$(klassSerializer($this.context, getKClass(T_0)), raw);
    };
  }));
  CBOR$Companion.prototype.loads_3zqiyt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.Companion.loads_3zqiyt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, hex) {
      var $this = this.plain;
      var raw = internal.HexConverter.parseHexBinary_61zpoe$(hex);
      return $this.load_8dtdds$(klassSerializer($this.context, getKClass(T_0)), raw);
    };
  }));
  CBOR$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var CBOR$Companion_instance = null;
  function CBOR$Companion_getInstance() {
    if (CBOR$Companion_instance === null) {
      new CBOR$Companion();
    }
    return CBOR$Companion_instance;
  }
  CBOR.prototype.dump_20fw5n$ = function (saver, obj) {
    var output = ByteArrayOutputStream_init();
    var dumper = new CBOR$CBORWriter(this, new CBOR$CBOREncoder(output));
    dumper.write_jsy488$(saver, obj);
    return output.toByteArray();
  };
  CBOR.prototype.dump_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.dump_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      return this.dump_20fw5n$(klassSerializer(this.context, getKClass(T_0)), obj);
    };
  }));
  CBOR.prototype.dumps_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.dumps_issdgt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      return internal.HexConverter.printHexBinary_1fhb37$(this.dump_20fw5n$(klassSerializer(this.context, getKClass(T_0)), obj), true);
    };
  }));
  CBOR.prototype.load_8dtdds$ = function (loader, raw) {
    var stream = ByteArrayInputStream_init(raw);
    var reader = new CBOR$CBORReader(this, new CBOR$CBORDecoder(stream));
    return reader.read_rf0fz3$(loader);
  };
  CBOR.prototype.load_5geitx$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.load_5geitx$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, raw) {
      return this.load_8dtdds$(klassSerializer(this.context, getKClass(T_0)), raw);
    };
  }));
  CBOR.prototype.loads_3zqiyt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.cbor.CBOR.loads_3zqiyt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, hex) {
      var raw = internal.HexConverter.parseHexBinary_61zpoe$(hex);
      return this.load_8dtdds$(klassSerializer(this.context, getKClass(T_0)), raw);
    };
  }));
  CBOR.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBOR',
    interfaces: []
  };
  function CBORParsingException(message) {
    IOException.call(this, message);
    this.name = 'CBORParsingException';
  }
  CBORParsingException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CBORParsingException',
    interfaces: [IOException]
  };
  function PrimitiveDesc(name) {
    this.name_b89ulf$_0 = name;
    this.kind_b9s3rg$_0 = KSerialClassKind$PRIMITIVE_getInstance();
  }
  Object.defineProperty(PrimitiveDesc.prototype, 'name', {
    get: function () {
      return this.name_b89ulf$_0;
    }
  });
  Object.defineProperty(PrimitiveDesc.prototype, 'kind', {
    get: function () {
      return this.kind_b9s3rg$_0;
    }
  });
  PrimitiveDesc.prototype.getElementName_za3lpa$ = function (index) {
    throw IllegalStateException_init('Primitives do not have fields');
  };
  PrimitiveDesc.prototype.getElementIndex_61zpoe$ = function (name) {
    throw IllegalStateException_init('Primitives do not have fields');
  };
  PrimitiveDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrimitiveDesc',
    interfaces: [KSerialClassDesc]
  };
  function UnitSerializer() {
    UnitSerializer_instance = this;
    this.serialClassDesc_nu93hb$_0 = new PrimitiveDesc('kotlin.Unit');
  }
  Object.defineProperty(UnitSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_nu93hb$_0;
    }
  });
  UnitSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeUnitValue();
  };
  UnitSerializer.prototype.load_ljkqvg$ = function (input) {
    input.readUnitValue();
  };
  UnitSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'UnitSerializer',
    interfaces: [KSerializer]
  };
  var UnitSerializer_instance = null;
  function UnitSerializer_getInstance() {
    if (UnitSerializer_instance === null) {
      new UnitSerializer();
    }
    return UnitSerializer_instance;
  }
  function BooleanSerializer() {
    BooleanSerializer_instance = this;
    this.serialClassDesc_mis5rp$_0 = new PrimitiveDesc('kotlin.Boolean');
  }
  Object.defineProperty(BooleanSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_mis5rp$_0;
    }
  });
  BooleanSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeBooleanValue_6taknv$(obj);
  };
  BooleanSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readBooleanValue();
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
    }
    return BooleanSerializer_instance;
  }
  function ByteSerializer() {
    ByteSerializer_instance = this;
    this.serialClassDesc_drdv4z$_0 = new PrimitiveDesc('kotlin.Byte');
  }
  Object.defineProperty(ByteSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_drdv4z$_0;
    }
  });
  ByteSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeByteValue_s8j3t7$(obj);
  };
  ByteSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readByteValue();
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
    }
    return ByteSerializer_instance;
  }
  function ShortSerializer() {
    ShortSerializer_instance = this;
    this.serialClassDesc_m4jy5b$_0 = new PrimitiveDesc('kotlin.Short');
  }
  Object.defineProperty(ShortSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_m4jy5b$_0;
    }
  });
  ShortSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeShortValue_mq22fl$(obj);
  };
  ShortSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readShortValue();
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
    }
    return ShortSerializer_instance;
  }
  function IntSerializer() {
    IntSerializer_instance = this;
    this.serialClassDesc_evqgaa$_0 = new PrimitiveDesc('kotlin.Int');
  }
  Object.defineProperty(IntSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_evqgaa$_0;
    }
  });
  IntSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeIntValue_za3lpa$(obj);
  };
  IntSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readIntValue();
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
    }
    return IntSerializer_instance;
  }
  function LongSerializer() {
    LongSerializer_instance = this;
    this.serialClassDesc_ytfxef$_0 = new PrimitiveDesc('kotlin.Long');
  }
  Object.defineProperty(LongSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_ytfxef$_0;
    }
  });
  LongSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeLongValue_s8cxhz$(obj);
  };
  LongSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readLongValue();
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
    }
    return LongSerializer_instance;
  }
  function FloatSerializer() {
    FloatSerializer_instance = this;
    this.serialClassDesc_pac2o1$_0 = new PrimitiveDesc('kotlin.Float');
  }
  Object.defineProperty(FloatSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_pac2o1$_0;
    }
  });
  FloatSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeFloatValue_mx4ult$(obj);
  };
  FloatSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readFloatValue();
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
    }
    return FloatSerializer_instance;
  }
  function DoubleSerializer() {
    DoubleSerializer_instance = this;
    this.serialClassDesc_lpoabw$_0 = new PrimitiveDesc('kotlin.Double');
  }
  Object.defineProperty(DoubleSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_lpoabw$_0;
    }
  });
  DoubleSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeDoubleValue_14dthe$(obj);
  };
  DoubleSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readDoubleValue();
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
    }
    return DoubleSerializer_instance;
  }
  function CharSerializer() {
    CharSerializer_instance = this;
    this.serialClassDesc_fbi1b$_0 = new PrimitiveDesc('kotlin.Char');
  }
  Object.defineProperty(CharSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_fbi1b$_0;
    }
  });
  CharSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeCharValue_s8itvh$(obj);
  };
  CharSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readCharValue();
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
    }
    return CharSerializer_instance;
  }
  function StringSerializer() {
    StringSerializer_instance = this;
    this.serialClassDesc_kxd9qk$_0 = new PrimitiveDesc('kotlin.String');
  }
  Object.defineProperty(StringSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_kxd9qk$_0;
    }
  });
  StringSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeStringValue_61zpoe$(obj);
  };
  StringSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readStringValue();
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
    }
    return StringSerializer_instance;
  }
  function EnumDesc(name) {
    this.name_6oeq3z$_0 = name;
    this.kind_6mwgxy$_0 = KSerialClassKind$ENUM_getInstance();
  }
  Object.defineProperty(EnumDesc.prototype, 'name', {
    get: function () {
      return this.name_6oeq3z$_0;
    }
  });
  Object.defineProperty(EnumDesc.prototype, 'kind', {
    get: function () {
      return this.kind_6mwgxy$_0;
    }
  });
  EnumDesc.prototype.getElementName_za3lpa$ = function (index) {
    throw IllegalStateException_init('Primitives does not have fields');
  };
  EnumDesc.prototype.getElementIndex_61zpoe$ = function (name) {
    throw IllegalStateException_init('Primitives does not have fields');
  };
  EnumDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EnumDesc',
    interfaces: [KSerialClassDesc]
  };
  function EnumSerializer(serializableClass) {
    this.serializableClass = serializableClass;
    this.serialClassDesc_knksqk$_0 = new EnumDesc(enumClassName(this.serializableClass));
  }
  Object.defineProperty(EnumSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_knksqk$_0;
    }
  });
  EnumSerializer.prototype.save_ejfkry$ = function (output, obj) {
    output.writeEnumValue_9pl89b$(this.serializableClass, obj);
  };
  EnumSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readEnumValue_xvqrpl$(this.serializableClass);
  };
  EnumSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'EnumSerializer',
    interfaces: [KSerializer]
  };
  function makeNullable(element) {
    return new NullableSerializer(element);
  }
  function NullableSerializer(element) {
    this.element_0 = element;
  }
  Object.defineProperty(NullableSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.element_0.serialClassDesc;
    }
  });
  NullableSerializer.prototype.save_ejfkry$ = function (output, obj) {
    if (obj != null) {
      output.writeNotNullMark();
      this.element_0.save_ejfkry$(output, obj);
    }
     else {
      output.writeNullValue();
    }
  };
  NullableSerializer.prototype.load_ljkqvg$ = function (input) {
    return input.readNotNullMark() ? this.element_0.load_ljkqvg$(input) : input.readNullValue();
  };
  NullableSerializer.prototype.update_qkk2oh$ = function (input, old) {
    var tmp$;
    if (old == null)
      tmp$ = this.load_ljkqvg$(input);
    else if (input.readNotNullMark())
      tmp$ = this.element_0.update_qkk2oh$(input, old);
    else {
      input.readNullValue();
      tmp$ = old;
    }
    return tmp$;
  };
  NullableSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NullableSerializer',
    interfaces: [KSerializer]
  };
  var SIZE_INDEX;
  function ListLikeSerializer(eSerializer) {
    this.eSerializer_jvghxu$_0 = eSerializer;
    this.typeParams_thbhbl$_0 = [this.eSerializer];
  }
  Object.defineProperty(ListLikeSerializer.prototype, 'eSerializer', {
    get: function () {
      return this.eSerializer_jvghxu$_0;
    }
  });
  Object.defineProperty(ListLikeSerializer.prototype, 'typeParams', {
    get: function () {
      return this.typeParams_thbhbl$_0;
    }
  });
  ListLikeSerializer.prototype.save_ejfkry$ = function (output, obj) {
    var size = this.objSize_wikn$(obj);
    var output_0 = output.writeBegin_jqfc32$(this.serialClassDesc, size, this.typeParams.slice());
    if (output_0.writeElement_xvmgof$(this.serialClassDesc, SIZE_INDEX))
      output_0.writeIntValue_za3lpa$(size);
    var iterator = this.objIterator_wikn$(obj);
    for (var index = 1; index <= size; index++)
      output_0.writeSerializableElementValue_k4al2t$(this.serialClassDesc, index, this.eSerializer, iterator.next());
    output_0.writeEnd_f6e2p$(this.serialClassDesc);
  };
  ListLikeSerializer.prototype.update_qkk2oh$ = function (input, old) {
    var builder = this.toBuilder_wikn$(old);
    var startIndex = this.builderSize_wili$(builder);
    var input_0 = input.readBegin_276rha$(this.serialClassDesc, this.typeParams.slice());
    mainLoop: while (true) {
      var index = input_0.readElement_f6e2p$(this.serialClassDesc);
      switch (index) {
        case -2:
          this.readAll_18i1yb$_0(input_0, builder, startIndex);
          break mainLoop;
        case -1:
          break mainLoop;
        case 0:
          this.readSize_os2y47$_0(input_0, builder);
          break;
        default:this.readItem_ieea3b$(input_0, startIndex + index | 0, builder);
          break;
      }
    }
    input_0.readEnd_f6e2p$(this.serialClassDesc);
    return this.toResult_wili$(builder);
  };
  ListLikeSerializer.prototype.load_ljkqvg$ = function (input) {
    var builder = this.builder();
    return this.update_qkk2oh$(input, this.toResult_wili$(builder));
  };
  ListLikeSerializer.prototype.readSize_os2y47$_0 = function (input, builder) {
    var size = input.readIntElementValue_xvmgof$(this.serialClassDesc, SIZE_INDEX);
    this.ensureCapacity_rk7bw8$(builder, size);
    return size;
  };
  ListLikeSerializer.prototype.readItem_ieea3b$ = function (input, index, builder) {
    this.add_p422l$(builder, index - 1 | 0, input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, index, this.eSerializer));
  };
  ListLikeSerializer.prototype.readAll_18i1yb$_0 = function (input, builder, startIndex) {
    var size = this.readSize_os2y47$_0(input, builder);
    for (var index = 1; index <= size; index++)
      this.readItem_ieea3b$(input, startIndex + index | 0, builder);
  };
  ListLikeSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ListLikeSerializer',
    interfaces: [KSerializer]
  };
  function MapLikeSerializer(eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.eSerializer_9aca20$_0 = eSerializer;
  }
  Object.defineProperty(MapLikeSerializer.prototype, 'eSerializer', {
    get: function () {
      return this.eSerializer_9aca20$_0;
    }
  });
  MapLikeSerializer.prototype.readItem_ieea3b$ = function (input, index, builder) {
    input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, index, new MapEntryUpdatingSerializer(this.eSerializer, builder));
  };
  MapLikeSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapLikeSerializer',
    interfaces: [ListLikeSerializer]
  };
  function ReferenceArraySerializer(kClass, eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.kClass_0 = kClass;
    this.serialClassDesc_jqccbt$_0 = ArrayClassDesc_getInstance();
  }
  Object.defineProperty(ReferenceArraySerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_jqccbt$_0;
    }
  });
  ReferenceArraySerializer.prototype.objSize_wikn$ = function ($receiver) {
    return $receiver.length;
  };
  ReferenceArraySerializer.prototype.objIterator_wikn$ = function ($receiver) {
    return Kotlin.arrayIterator($receiver);
  };
  ReferenceArraySerializer.prototype.builder = function () {
    return ArrayList_init_0();
  };
  ReferenceArraySerializer.prototype.builderSize_wili$ = function ($receiver) {
    return $receiver.size;
  };
  ReferenceArraySerializer.prototype.toResult_wili$ = function ($receiver) {
    return toNativeArray($receiver, this.kClass_0);
  };
  ReferenceArraySerializer.prototype.toBuilder_wikn$ = function ($receiver) {
    return ArrayList_init(asList($receiver));
  };
  ReferenceArraySerializer.prototype.ensureCapacity_rk7bw8$ = function ($receiver, size) {
    $receiver.ensureCapacity_za3lpa$(size);
  };
  ReferenceArraySerializer.prototype.add_p422l$ = function ($receiver, index, element) {
    $receiver.add_wxm5ur$(index, element);
  };
  ReferenceArraySerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ReferenceArraySerializer',
    interfaces: [ListLikeSerializer]
  };
  function ArrayListSerializer(element) {
    ListLikeSerializer.call(this, element);
    this.serialClassDesc_37x55y$_0 = ArrayListClassDesc_getInstance();
  }
  Object.defineProperty(ArrayListSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_37x55y$_0;
    }
  });
  ArrayListSerializer.prototype.objSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  ArrayListSerializer.prototype.objIterator_wikn$ = function ($receiver) {
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
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, ArrayList) ? tmp$ : null) != null ? tmp$_0 : ArrayList_init($receiver);
  };
  ArrayListSerializer.prototype.ensureCapacity_rk7bw8$ = function ($receiver, size) {
    $receiver.ensureCapacity_za3lpa$(size);
  };
  ArrayListSerializer.prototype.add_p422l$ = function ($receiver, index, element) {
    $receiver.add_wxm5ur$(index, element);
  };
  ArrayListSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ArrayListSerializer',
    interfaces: [ListLikeSerializer]
  };
  function LinkedHashSetSerializer(eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.serialClassDesc_lh9nz2$_0 = LinkedHashSetClassDesc_getInstance();
  }
  Object.defineProperty(LinkedHashSetSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_lh9nz2$_0;
    }
  });
  LinkedHashSetSerializer.prototype.objSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  LinkedHashSetSerializer.prototype.objIterator_wikn$ = function ($receiver) {
    return $receiver.iterator();
  };
  var LinkedHashSet_init_0 = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
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
  LinkedHashSetSerializer.prototype.ensureCapacity_rk7bw8$ = function ($receiver, size) {
  };
  LinkedHashSetSerializer.prototype.add_p422l$ = function ($receiver, index, element) {
    $receiver.add_11rb$(element);
  };
  LinkedHashSetSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedHashSetSerializer',
    interfaces: [ListLikeSerializer]
  };
  function HashSetSerializer(eSerializer) {
    ListLikeSerializer.call(this, eSerializer);
    this.serialClassDesc_6bcivr$_0 = HashSetClassDesc_getInstance();
  }
  Object.defineProperty(HashSetSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_6bcivr$_0;
    }
  });
  HashSetSerializer.prototype.objSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  HashSetSerializer.prototype.objIterator_wikn$ = function ($receiver) {
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
  HashSetSerializer.prototype.ensureCapacity_rk7bw8$ = function ($receiver, size) {
  };
  HashSetSerializer.prototype.add_p422l$ = function ($receiver, index, element) {
    $receiver.add_11rb$(element);
  };
  HashSetSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HashSetSerializer',
    interfaces: [ListLikeSerializer]
  };
  function LinkedHashMapSerializer(kSerializer, vSerializer) {
    MapLikeSerializer.call(this, new MapEntrySerializer(kSerializer, vSerializer));
    this.serialClassDesc_jfgdns$_0 = LinkedHashMapClassDesc_getInstance();
    this.typeParams_i2xn1l$_0 = [kSerializer, vSerializer];
  }
  Object.defineProperty(LinkedHashMapSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_jfgdns$_0;
    }
  });
  Object.defineProperty(LinkedHashMapSerializer.prototype, 'typeParams', {
    get: function () {
      return this.typeParams_i2xn1l$_0;
    }
  });
  LinkedHashMapSerializer.prototype.objSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  LinkedHashMapSerializer.prototype.objIterator_wikn$ = function ($receiver) {
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
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, LinkedHashMap) ? tmp$ : null) != null ? tmp$_0 : LinkedHashMap_init_0($receiver);
  };
  LinkedHashMapSerializer.prototype.ensureCapacity_rk7bw8$ = function ($receiver, size) {
  };
  LinkedHashMapSerializer.prototype.add_p422l$ = function ($receiver, index, element) {
    $receiver.put_xwzc9p$(element.key, element.value);
  };
  LinkedHashMapSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedHashMapSerializer',
    interfaces: [MapLikeSerializer]
  };
  function HashMapSerializer(kSerializer, vSerializer) {
    MapLikeSerializer.call(this, new MapEntrySerializer(kSerializer, vSerializer));
    this.serialClassDesc_yldir3$_0 = HashMapClassDesc_getInstance();
    this.typeParams_mpwdow$_0 = [kSerializer, vSerializer];
  }
  Object.defineProperty(HashMapSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_yldir3$_0;
    }
  });
  Object.defineProperty(HashMapSerializer.prototype, 'typeParams', {
    get: function () {
      return this.typeParams_mpwdow$_0;
    }
  });
  HashMapSerializer.prototype.objSize_wikn$ = function ($receiver) {
    return $receiver.size;
  };
  HashMapSerializer.prototype.objIterator_wikn$ = function ($receiver) {
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
  HashMapSerializer.prototype.ensureCapacity_rk7bw8$ = function ($receiver, size) {
  };
  HashMapSerializer.prototype.add_p422l$ = function ($receiver, index, element) {
    $receiver.put_xwzc9p$(element.key, element.value);
  };
  HashMapSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HashMapSerializer',
    interfaces: [MapLikeSerializer]
  };
  var KEY_INDEX;
  var VALUE_INDEX;
  function KeyValueSerializer(kSerializer, vSerializer) {
    this.kSerializer = kSerializer;
    this.vSerializer = vSerializer;
  }
  KeyValueSerializer.prototype.save_ejfkry$ = function (output, obj) {
    var output_0 = output.writeBegin_276rha$(this.serialClassDesc, [this.kSerializer, this.vSerializer]);
    output_0.writeSerializableElementValue_k4al2t$(this.serialClassDesc, KEY_INDEX, this.kSerializer, this.get_key_wili$(obj));
    output_0.writeSerializableElementValue_k4al2t$(this.serialClassDesc, VALUE_INDEX, this.vSerializer, this.get_value_wili$(obj));
    output_0.writeEnd_f6e2p$(this.serialClassDesc);
  };
  KeyValueSerializer.prototype.load_ljkqvg$ = function (input) {
    var tmp$, tmp$_0;
    var input_0 = input.readBegin_276rha$(this.serialClassDesc, [this.kSerializer, this.vSerializer]);
    var kSet = false;
    var vSet = false;
    var k = null;
    var v = null;
    mainLoop: while (true) {
      switch (input_0.readElement_f6e2p$(this.serialClassDesc)) {
        case -2:
          k = this.readKey_ljkqvg$(input_0);
          kSet = true;
          v = this.readValue_2qvvsx$(input_0, k, kSet);
          vSet = true;
          break mainLoop;
        case -1:
          break mainLoop;
        case 0:
          k = this.readKey_ljkqvg$(input_0);
          kSet = true;
          break;
        case 1:
          v = this.readValue_2qvvsx$(input_0, k, kSet);
          vSet = true;
          break;
        default:throw new SerializationException('Invalid index');
      }
    }
    input_0.readEnd_f6e2p$(this.serialClassDesc);
    if (!kSet)
      throw new SerializationException('Required key is missing');
    if (!vSet)
      throw new SerializationException('Required value is missing');
    return this.toResult_xwzc9p$((tmp$ = k) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), (tmp$_0 = v) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE());
  };
  KeyValueSerializer.prototype.readKey_ljkqvg$ = function (input) {
    return input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, KEY_INDEX, this.kSerializer);
  };
  KeyValueSerializer.prototype.readValue_2qvvsx$ = function (input, k, kSet) {
    return input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, VALUE_INDEX, this.vSerializer);
  };
  KeyValueSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KeyValueSerializer',
    interfaces: [KSerializer]
  };
  function MapEntryUpdatingSerializer(mSerializer, mapBuilder) {
    KeyValueSerializer.call(this, mSerializer.kSerializer, mSerializer.vSerializer);
    this.mapBuilder_0 = mapBuilder;
    this.serialClassDesc_qoccaf$_0 = MapEntryClassDesc_getInstance();
  }
  Object.defineProperty(MapEntryUpdatingSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_qoccaf$_0;
    }
  });
  MapEntryUpdatingSerializer.prototype.toResult_xwzc9p$ = function (key, value) {
    return new MapEntry(key, value);
  };
  MapEntryUpdatingSerializer.prototype.readValue_2qvvsx$ = function (input, k, kSet) {
    var tmp$, tmp$_0;
    if (!kSet)
      throw new SerializationException('Key must be before value in serialization stream');
    var key = (tmp$ = k) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
    if (this.mapBuilder_0.containsKey_11rb$(key) && this.vSerializer.serialClassDesc.kind !== KSerialClassKind$PRIMITIVE_getInstance()) {
      tmp$_0 = input.updateSerializableElementValue_2bgl1k$(this.serialClassDesc, VALUE_INDEX, this.vSerializer, getValue(this.mapBuilder_0, key));
    }
     else {
      tmp$_0 = input.readSerializableElementValue_nqb5fm$(this.serialClassDesc, VALUE_INDEX, this.vSerializer);
    }
    var v = tmp$_0;
    this.mapBuilder_0.put_xwzc9p$(key, v);
    return v;
  };
  MapEntryUpdatingSerializer.prototype.get_key_wili$ = function ($receiver) {
    return $receiver.key;
  };
  MapEntryUpdatingSerializer.prototype.get_value_wili$ = function ($receiver) {
    return $receiver.value;
  };
  MapEntryUpdatingSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntryUpdatingSerializer',
    interfaces: [KeyValueSerializer]
  };
  function MapEntrySerializer(kSerializer, vSerializer) {
    KeyValueSerializer.call(this, kSerializer, vSerializer);
    this.serialClassDesc_im3vgx$_0 = MapEntryClassDesc_getInstance();
  }
  Object.defineProperty(MapEntrySerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_im3vgx$_0;
    }
  });
  MapEntrySerializer.prototype.toResult_xwzc9p$ = function (key, value) {
    return new MapEntry(key, value);
  };
  MapEntrySerializer.prototype.get_key_wili$ = function ($receiver) {
    return $receiver.key;
  };
  MapEntrySerializer.prototype.get_value_wili$ = function ($receiver) {
    return $receiver.value;
  };
  MapEntrySerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntrySerializer',
    interfaces: [KeyValueSerializer]
  };
  function PairSerializer(kSerializer, vSerializer) {
    KeyValueSerializer.call(this, kSerializer, vSerializer);
    this.serialClassDesc_o6bd3f$_0 = PairClassDesc_getInstance();
  }
  Object.defineProperty(PairSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_o6bd3f$_0;
    }
  });
  PairSerializer.prototype.toResult_xwzc9p$ = function (key, value) {
    return to(key, value);
  };
  PairSerializer.prototype.get_key_wili$ = function ($receiver) {
    return $receiver.first;
  };
  PairSerializer.prototype.get_value_wili$ = function ($receiver) {
    return $receiver.second;
  };
  PairSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PairSerializer',
    interfaces: [KeyValueSerializer]
  };
  function ListLikeDesc() {
  }
  ListLikeDesc.prototype.getElementName_za3lpa$ = function (index) {
    return index === SIZE_INDEX ? 'size' : index.toString();
  };
  ListLikeDesc.prototype.getElementIndex_61zpoe$ = function (name) {
    return equals(name, 'size') ? SIZE_INDEX : toInt(name);
  };
  ListLikeDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ListLikeDesc',
    interfaces: [KSerialClassDesc]
  };
  function ArrayClassDesc() {
    ArrayClassDesc_instance = this;
    ListLikeDesc.call(this);
  }
  Object.defineProperty(ArrayClassDesc.prototype, 'name', {
    get: function () {
      return 'kotlin.Array';
    }
  });
  Object.defineProperty(ArrayClassDesc.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$LIST_getInstance();
    }
  });
  ArrayClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ArrayClassDesc',
    interfaces: [ListLikeDesc]
  };
  var ArrayClassDesc_instance = null;
  function ArrayClassDesc_getInstance() {
    if (ArrayClassDesc_instance === null) {
      new ArrayClassDesc();
    }
    return ArrayClassDesc_instance;
  }
  function ArrayListClassDesc() {
    ArrayListClassDesc_instance = this;
    ListLikeDesc.call(this);
  }
  Object.defineProperty(ArrayListClassDesc.prototype, 'name', {
    get: function () {
      return 'kotlin.collections.ArrayList';
    }
  });
  Object.defineProperty(ArrayListClassDesc.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$LIST_getInstance();
    }
  });
  ArrayListClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ArrayListClassDesc',
    interfaces: [ListLikeDesc]
  };
  var ArrayListClassDesc_instance = null;
  function ArrayListClassDesc_getInstance() {
    if (ArrayListClassDesc_instance === null) {
      new ArrayListClassDesc();
    }
    return ArrayListClassDesc_instance;
  }
  function LinkedHashSetClassDesc() {
    LinkedHashSetClassDesc_instance = this;
    ListLikeDesc.call(this);
  }
  Object.defineProperty(LinkedHashSetClassDesc.prototype, 'name', {
    get: function () {
      return 'kotlin.collections.LinkedHashSet';
    }
  });
  Object.defineProperty(LinkedHashSetClassDesc.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$SET_getInstance();
    }
  });
  LinkedHashSetClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LinkedHashSetClassDesc',
    interfaces: [ListLikeDesc]
  };
  var LinkedHashSetClassDesc_instance = null;
  function LinkedHashSetClassDesc_getInstance() {
    if (LinkedHashSetClassDesc_instance === null) {
      new LinkedHashSetClassDesc();
    }
    return LinkedHashSetClassDesc_instance;
  }
  function HashSetClassDesc() {
    HashSetClassDesc_instance = this;
    ListLikeDesc.call(this);
  }
  Object.defineProperty(HashSetClassDesc.prototype, 'name', {
    get: function () {
      return 'kotlin.collections.HashSet';
    }
  });
  Object.defineProperty(HashSetClassDesc.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$SET_getInstance();
    }
  });
  HashSetClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'HashSetClassDesc',
    interfaces: [ListLikeDesc]
  };
  var HashSetClassDesc_instance = null;
  function HashSetClassDesc_getInstance() {
    if (HashSetClassDesc_instance === null) {
      new HashSetClassDesc();
    }
    return HashSetClassDesc_instance;
  }
  function LinkedHashMapClassDesc() {
    LinkedHashMapClassDesc_instance = this;
    ListLikeDesc.call(this);
  }
  Object.defineProperty(LinkedHashMapClassDesc.prototype, 'name', {
    get: function () {
      return 'kotlin.collections.LinkedHashMap';
    }
  });
  Object.defineProperty(LinkedHashMapClassDesc.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$MAP_getInstance();
    }
  });
  LinkedHashMapClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LinkedHashMapClassDesc',
    interfaces: [ListLikeDesc]
  };
  var LinkedHashMapClassDesc_instance = null;
  function LinkedHashMapClassDesc_getInstance() {
    if (LinkedHashMapClassDesc_instance === null) {
      new LinkedHashMapClassDesc();
    }
    return LinkedHashMapClassDesc_instance;
  }
  function HashMapClassDesc() {
    HashMapClassDesc_instance = this;
    ListLikeDesc.call(this);
  }
  Object.defineProperty(HashMapClassDesc.prototype, 'name', {
    get: function () {
      return 'kotlin.collections.HashMap';
    }
  });
  Object.defineProperty(HashMapClassDesc.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$MAP_getInstance();
    }
  });
  HashMapClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'HashMapClassDesc',
    interfaces: [ListLikeDesc]
  };
  var HashMapClassDesc_instance = null;
  function HashMapClassDesc_getInstance() {
    if (HashMapClassDesc_instance === null) {
      new HashMapClassDesc();
    }
    return HashMapClassDesc_instance;
  }
  function MapEntry(key, value) {
    this.key_qf615j$_0 = key;
    this.value_x17797$_0 = value;
  }
  Object.defineProperty(MapEntry.prototype, 'key', {
    get: function () {
      return this.key_qf615j$_0;
    }
  });
  Object.defineProperty(MapEntry.prototype, 'value', {
    get: function () {
      return this.value_x17797$_0;
    }
  });
  MapEntry.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntry',
    interfaces: [Map$Entry]
  };
  MapEntry.prototype.component1 = function () {
    return this.key;
  };
  MapEntry.prototype.component2 = function () {
    return this.value;
  };
  MapEntry.prototype.copy_xwzc9p$ = function (key, value) {
    return new MapEntry(key === void 0 ? this.key : key, value === void 0 ? this.value : value);
  };
  MapEntry.prototype.toString = function () {
    return 'MapEntry(key=' + Kotlin.toString(this.key) + (', value=' + Kotlin.toString(this.value)) + ')';
  };
  MapEntry.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.key) | 0;
    result = result * 31 + Kotlin.hashCode(this.value) | 0;
    return result;
  };
  MapEntry.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.key, other.key) && Kotlin.equals(this.value, other.value)))));
  };
  function MapEntryClassDesc() {
    MapEntryClassDesc_instance = this;
    SerialClassDescImpl.call(this, 'kotlin.collections.Map.Entry');
    this.kind_6o02kx$_0 = KSerialClassKind$ENTRY_getInstance();
    this.addElement_61zpoe$('key');
    this.addElement_61zpoe$('value');
  }
  Object.defineProperty(MapEntryClassDesc.prototype, 'kind', {
    get: function () {
      return this.kind_6o02kx$_0;
    }
  });
  MapEntryClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'MapEntryClassDesc',
    interfaces: [SerialClassDescImpl]
  };
  var MapEntryClassDesc_instance = null;
  function MapEntryClassDesc_getInstance() {
    if (MapEntryClassDesc_instance === null) {
      new MapEntryClassDesc();
    }
    return MapEntryClassDesc_instance;
  }
  function PairClassDesc() {
    PairClassDesc_instance = this;
    SerialClassDescImpl.call(this, 'kotlin.Pair');
    this.addElement_61zpoe$('first');
    this.addElement_61zpoe$('second');
  }
  PairClassDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'PairClassDesc',
    interfaces: [SerialClassDescImpl]
  };
  var PairClassDesc_instance = null;
  function PairClassDesc_getInstance() {
    if (PairClassDesc_instance === null) {
      new PairClassDesc();
    }
    return PairClassDesc_instance;
  }
  function TripleSerializer(aSerializer, bSerializer, cSerializer) {
    this.aSerializer_0 = aSerializer;
    this.bSerializer_0 = bSerializer;
    this.cSerializer_0 = cSerializer;
    this.serialClassDesc_f2mpdz$_0 = TripleSerializer$TripleDesc_getInstance();
  }
  function TripleSerializer$TripleDesc() {
    TripleSerializer$TripleDesc_instance = this;
    SerialClassDescImpl.call(this, 'kotlin.Triple');
    this.addElement_61zpoe$('first');
    this.addElement_61zpoe$('second');
    this.addElement_61zpoe$('third');
  }
  TripleSerializer$TripleDesc.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'TripleDesc',
    interfaces: [SerialClassDescImpl]
  };
  var TripleSerializer$TripleDesc_instance = null;
  function TripleSerializer$TripleDesc_getInstance() {
    if (TripleSerializer$TripleDesc_instance === null) {
      new TripleSerializer$TripleDesc();
    }
    return TripleSerializer$TripleDesc_instance;
  }
  Object.defineProperty(TripleSerializer.prototype, 'serialClassDesc', {
    get: function () {
      return this.serialClassDesc_f2mpdz$_0;
    }
  });
  TripleSerializer.prototype.save_ejfkry$ = function (output, obj) {
    var output_0 = output.writeBegin_276rha$(this.serialClassDesc, [this.aSerializer_0, this.bSerializer_0, this.cSerializer_0]);
    output_0.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 0, this.aSerializer_0, obj.first);
    output_0.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 1, this.bSerializer_0, obj.second);
    output_0.writeSerializableElementValue_k4al2t$(this.serialClassDesc, 2, this.cSerializer_0, obj.third);
    output_0.writeEnd_f6e2p$(this.serialClassDesc);
  };
  TripleSerializer.prototype.load_ljkqvg$ = function (input) {
    var tmp$, tmp$_0, tmp$_1;
    var input_0 = input.readBegin_276rha$(this.serialClassDesc, [this.aSerializer_0, this.bSerializer_0, this.cSerializer_0]);
    var aSet = false;
    var bSet = false;
    var cSet = false;
    var a = null;
    var b = null;
    var c = null;
    mainLoop: while (true) {
      switch (input_0.readElement_f6e2p$(this.serialClassDesc)) {
        case -2:
          a = input_0.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 0, this.aSerializer_0);
          aSet = true;
          b = input_0.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 1, this.bSerializer_0);
          bSet = true;
          c = input_0.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 2, this.cSerializer_0);
          cSet = true;
          break mainLoop;
        case -1:
          break mainLoop;
        case 0:
          a = input_0.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 0, this.aSerializer_0);
          aSet = true;
          break;
        case 1:
          b = input_0.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 1, this.bSerializer_0);
          bSet = true;
          break;
        case 2:
          c = input_0.readSerializableElementValue_nqb5fm$(this.serialClassDesc, 2, this.cSerializer_0);
          cSet = true;
          break;
        default:throw new SerializationException('Invalid index');
      }
    }
    input_0.readEnd_f6e2p$(this.serialClassDesc);
    if (!aSet)
      throw new SerializationException('Required first is missing');
    if (!bSet)
      throw new SerializationException('Required second is missing');
    if (!cSet)
      throw new SerializationException('Required third is missing');
    return new Triple((tmp$ = a) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), (tmp$_0 = b) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE(), (tmp$_1 = c) == null || Kotlin.isType(tmp$_1, Any) ? tmp$_1 : throwCCE());
  };
  TripleSerializer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TripleSerializer',
    interfaces: [KSerializer]
  };
  function SerialClassDescImpl(name) {
    this.name_l5inc6$_0 = name;
    this.names_gh1kah$_0 = ArrayList_init_0();
    this.annotations_4jiga3$_0 = ArrayList_init_0();
    this._indices_onkk0z$_0 = null;
  }
  Object.defineProperty(SerialClassDescImpl.prototype, 'name', {
    get: function () {
      return this.name_l5inc6$_0;
    }
  });
  Object.defineProperty(SerialClassDescImpl.prototype, 'kind', {
    get: function () {
      return KSerialClassKind$CLASS_getInstance();
    }
  });
  Object.defineProperty(SerialClassDescImpl.prototype, 'indices_jm5tq0$_0', {
    get: function () {
      var tmp$;
      return (tmp$ = this._indices_onkk0z$_0) != null ? tmp$ : this.buildIndices_585r2k$_0();
    }
  });
  SerialClassDescImpl.prototype.addElement_61zpoe$ = function (name) {
    this.names_gh1kah$_0.add_11rb$(name);
    this.annotations_4jiga3$_0.add_11rb$(ArrayList_init_0());
  };
  SerialClassDescImpl.prototype.pushAnnotation_yj921w$ = function (a) {
    last(this.annotations_4jiga3$_0).add_11rb$(a);
  };
  SerialClassDescImpl.prototype.getAnnotationsForIndex_za3lpa$ = function (index) {
    return toList(this.annotations_4jiga3$_0.get_za3lpa$(index));
  };
  Object.defineProperty(SerialClassDescImpl.prototype, 'associatedFieldsCount', {
    get: function () {
      return this.annotations_4jiga3$_0.size;
    }
  });
  SerialClassDescImpl.prototype.getElementName_za3lpa$ = function (index) {
    return this.names_gh1kah$_0.get_za3lpa$(index);
  };
  SerialClassDescImpl.prototype.getElementIndex_61zpoe$ = function (name) {
    var tmp$;
    return (tmp$ = this.indices_jm5tq0$_0.get_11rb$(name)) != null ? tmp$ : KInput$Companion_getInstance().UNKNOWN_NAME;
  };
  SerialClassDescImpl.prototype.buildIndices_585r2k$_0 = function () {
    var tmp$;
    var indices = HashMap_init();
    tmp$ = this.names_gh1kah$_0.size - 1 | 0;
    for (var i = 0; i <= tmp$; i++)
      indices.put_xwzc9p$(this.names_gh1kah$_0.get_za3lpa$(i), i);
    this._indices_onkk0z$_0 = indices;
    return indices;
  };
  SerialClassDescImpl.prototype.toString = function () {
    return this.name + this.names_gh1kah$_0;
  };
  SerialClassDescImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SerialClassDescImpl',
    interfaces: [KSerialClassDesc]
  };
  function onlySingleOrNull($receiver) {
    switch ($receiver.size) {
      case 0:
        return null;
      case 1:
        return $receiver.get_za3lpa$(0);
      default:throw IllegalStateException_init('Too much arguments in list');
    }
  }
  function readExactNBytes($receiver, bytes) {
    var array = new Int8Array(bytes);
    var read = 0;
    while (read < bytes) {
      var i = $receiver.read_mj6st8$(array, read, bytes - read | 0);
      if (i === -1)
        throw new IOException('Unexpected EOF');
      read = read + i | 0;
    }
    return array;
  }
  function readToByteBuffer($receiver, bytes) {
    var arr = readExactNBytes($receiver, bytes);
    var buf = ByteBuffer$Companion_getInstance().allocate_za3lpa$(bytes);
    buf.put_fqrh44$(arr).flip();
    return buf;
  }
  function HexConverter() {
    HexConverter_instance = this;
    this.hexCode_0 = '0123456789ABCDEF';
  }
  HexConverter.prototype.parseHexBinary_61zpoe$ = function (s) {
    var len = s.length;
    if (len % 2 !== 0) {
      throw IllegalArgumentException_init_0('hexBinary needs to be even-length: ' + s);
    }
    var out = new Int8Array(len / 2 | 0);
    var i = 0;
    while (i < len) {
      var h = this.hexToBin_0(s.charCodeAt(i));
      var l = this.hexToBin_0(s.charCodeAt(i + 1 | 0));
      if (h === -1 || l === -1) {
        throw IllegalArgumentException_init_0('contains illegal character for hexBinary: ' + s);
      }
      out[i / 2 | 0] = toByte((h * 16 | 0) + l | 0);
      i = i + 2 | 0;
    }
    return out;
  };
  HexConverter.prototype.hexToBin_0 = function (ch) {
    if ((new CharRange(48, 57)).contains_mef7kx$(ch))
      return ch - 48;
    else if ((new CharRange(65, 70)).contains_mef7kx$(ch))
      return ch - 65 + 10 | 0;
    else if ((new CharRange(97, 102)).contains_mef7kx$(ch))
      return ch - 97 + 10 | 0;
    else
      return -1;
  };
  HexConverter.prototype.printHexBinary_1fhb37$ = function (data, lowerCase) {
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
  HexConverter.prototype.toHexString_za3lpa$ = function (n) {
    var tmp$;
    var $receiver = trimStart(this.printHexBinary_1fhb37$(ByteBuffer$Companion_getInstance().allocate_za3lpa$(4).putInt_za3lpa$(n).flip().array(), true), Kotlin.charArrayOf(48));
    return (tmp$ = $receiver.length > 0 ? $receiver : null) != null ? tmp$ : '0';
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
    }
    return HexConverter_instance;
  }
  function getUnsignedByte($receiver) {
    return $receiver.get() & 255;
  }
  function getUnsignedShort($receiver) {
    return $receiver.getShort() & 65535;
  }
  function getUnsignedInt($receiver) {
    return Kotlin.Long.fromInt($receiver.getInt()).and(new Kotlin.Long(-1, 0));
  }
  function JSON_0(unquoted, indented, indent, nonstrict, updateMode, context) {
    JSON$Companion_getInstance();
    if (unquoted === void 0)
      unquoted = false;
    if (indented === void 0)
      indented = false;
    if (indent === void 0)
      indent = '    ';
    if (nonstrict === void 0)
      nonstrict = false;
    if (updateMode === void 0)
      updateMode = UpdateMode$OVERWRITE_getInstance();
    if (context === void 0)
      context = null;
    this.unquoted_0 = unquoted;
    this.indented_0 = indented;
    this.indent_0 = indent;
    this.nonstrict_8be2vx$ = nonstrict;
    this.updateMode = updateMode;
    this.context = context;
  }
  JSON_0.prototype.stringify_jsy488$ = function (saver, obj) {
    var sw = new StringWriter();
    var output = new JSON$JsonOutput(this, JSON$Mode$OBJ_getInstance(), new JSON$Composer(this, sw));
    output.write_jsy488$(saver, obj);
    return sw.toString();
  };
  JSON_0.prototype.stringify_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.json.JSON.stringify_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      return this.stringify_jsy488$(klassSerializer(this.context, getKClass(T_0)), obj);
    };
  }));
  JSON_0.prototype.parse_67noqb$ = function (loader, str) {
    var parser = new JSON$Parser(new StringReader(str));
    var input = new JSON$JsonInput(this, JSON$Mode$OBJ_getInstance(), parser);
    var result = input.read_rf0fz3$(loader);
    if (!(parser.curTc === JSON$Companion_getInstance().TC_EOF_0)) {
      var message = 'Shall parse complete string';
      throw IllegalStateException_init(message.toString());
    }
    return result;
  };
  JSON_0.prototype.parse_3zqiyt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.json.JSON.parse_3zqiyt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, str) {
      return this.parse_67noqb$(klassSerializer(this.context, getKClass(T_0)), str);
    };
  }));
  function JSON$Companion() {
    JSON$Companion_instance = this;
    this.plain = new JSON_0();
    this.unquoted = new JSON_0(true);
    this.indented = new JSON_0(void 0, true);
    this.nonstrict = new JSON_0(void 0, void 0, void 0, true);
    this.NULL_0 = 'null';
    this.COMMA_0 = 44;
    this.COLON_0 = 58;
    this.BEGIN_OBJ_0 = 123;
    this.END_OBJ_0 = 125;
    this.BEGIN_LIST_0 = 91;
    this.END_LIST_0 = 93;
    this.STRING_0 = 34;
    this.STRING_ESC_0 = 92;
    this.INVALID_0 = toChar(0);
    this.UNICODE_ESC_0 = 117;
    this.TC_OTHER_0 = 0;
    this.TC_EOF_0 = 1;
    this.TC_INVALID_0 = 2;
    this.TC_WS_0 = 3;
    this.TC_COMMA_0 = 4;
    this.TC_COLON_0 = 5;
    this.TC_BEGIN_OBJ_0 = 6;
    this.TC_END_OBJ_0 = 7;
    this.TC_BEGIN_LIST_0 = 8;
    this.TC_END_LIST_0 = 9;
    this.TC_STRING_0 = 10;
    this.TC_STRING_ESC_0 = 11;
    this.TC_NULL_0 = 12;
    this.CTC_MAX_0 = 126;
    this.CTC_OFS_0 = 1;
    this.C2TC_0 = new Int8Array(this.CTC_MAX_0 + this.CTC_OFS_0 | 0);
    this.initC2TC_6t1wet$(-1, this.TC_EOF_0);
    for (var i = 0; i <= 32; i++)
      this.initC2TC_6t1wet$(i, this.TC_INVALID_0);
    this.initC2TC_6t1wet$(9, this.TC_WS_0);
    this.initC2TC_6t1wet$(10, this.TC_WS_0);
    this.initC2TC_6t1wet$(13, this.TC_WS_0);
    this.initC2TC_6t1wet$(32, this.TC_WS_0);
    this.initC2TC_o3jjt8$(this.COMMA_0, this.TC_COMMA_0);
    this.initC2TC_o3jjt8$(this.COLON_0, this.TC_COLON_0);
    this.initC2TC_o3jjt8$(this.BEGIN_OBJ_0, this.TC_BEGIN_OBJ_0);
    this.initC2TC_o3jjt8$(this.END_OBJ_0, this.TC_END_OBJ_0);
    this.initC2TC_o3jjt8$(this.BEGIN_LIST_0, this.TC_BEGIN_LIST_0);
    this.initC2TC_o3jjt8$(this.END_LIST_0, this.TC_END_LIST_0);
    this.initC2TC_o3jjt8$(this.STRING_0, this.TC_STRING_0);
    this.initC2TC_o3jjt8$(this.STRING_ESC_0, this.TC_STRING_ESC_0);
    this.C2ESC_MAX_0 = 93;
    this.ESC2C_MAX_0 = 117;
    this.C2ESC_0 = new Int8Array(this.C2ESC_MAX_0);
    this.ESC2C_0 = new Int8Array(this.ESC2C_MAX_0);
    for (var i_0 = 0; i_0 <= 31; i_0++)
      this.initC2ESC_6t1mh3$(i_0, this.UNICODE_ESC_0);
    this.initC2ESC_6t1mh3$(8, 98);
    this.initC2ESC_6t1mh3$(9, 116);
    this.initC2ESC_6t1mh3$(10, 110);
    this.initC2ESC_6t1mh3$(12, 102);
    this.initC2ESC_6t1mh3$(13, 114);
    this.initC2ESC_o3jtqy$(47, 47);
    this.initC2ESC_o3jtqy$(this.STRING_0, this.STRING_0);
    this.initC2ESC_o3jtqy$(this.STRING_ESC_0, this.STRING_ESC_0);
  }
  JSON$Companion.prototype.stringify_jsy488$ = function (saver, obj) {
    return this.plain.stringify_jsy488$(saver, obj);
  };
  JSON$Companion.prototype.stringify_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.json.JSON.Companion.stringify_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, obj) {
      return this.stringify_jsy488$(serializer(getKClass(T_0)), obj);
    };
  }));
  JSON$Companion.prototype.parse_67noqb$ = function (loader, str) {
    return this.plain.parse_67noqb$(loader, str);
  };
  JSON$Companion.prototype.parse_3zqiyt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.json.JSON.Companion.parse_3zqiyt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var serializer = _.kotlinx.serialization.serializer_1yb8b7$;
    return function (T_0, isT, str) {
      return this.parse_67noqb$(serializer(getKClass(T_0)), str);
    };
  }));
  JSON$Companion.prototype.initC2TC_6t1wet$ = function (c, cl) {
    this.C2TC_0[c + this.CTC_OFS_0 | 0] = cl;
  };
  JSON$Companion.prototype.initC2TC_o3jjt8$ = function (c, cl) {
    this.initC2TC_6t1wet$(c | 0, cl);
  };
  JSON$Companion.prototype.c2tc_za3lpa$ = function (c) {
    return c < this.CTC_MAX_0 ? this.C2TC_0[c + this.CTC_OFS_0 | 0] : this.TC_OTHER_0;
  };
  JSON$Companion.prototype.mustBeQuoted_0 = function (str) {
    var any$result;
    any$break: do {
      var tmp$;
      tmp$ = iterator(str);
      while (tmp$.hasNext()) {
        var element = unboxChar(tmp$.next());
        if (this.c2tc_za3lpa$(unboxChar(toBoxedChar(element)) | 0) !== this.TC_OTHER_0) {
          any$result = true;
          break any$break;
        }
      }
      any$result = false;
    }
     while (false);
    return any$result || equals(str, this.NULL_0);
  };
  JSON$Companion.prototype.initC2ESC_6t1mh3$ = function (c, esc) {
    this.C2ESC_0[c] = toByte(esc | 0);
    if (esc !== this.UNICODE_ESC_0)
      this.ESC2C_0[esc | 0] = toByte(c);
  };
  JSON$Companion.prototype.initC2ESC_o3jtqy$ = function (c, esc) {
    this.initC2ESC_6t1mh3$(c | 0, esc);
  };
  JSON$Companion.prototype.c2esc_s8itvh$ = function (c) {
    return (c | 0) < this.C2ESC_MAX_0 ? toChar(this.C2ESC_0[c | 0]) : this.INVALID_0;
  };
  JSON$Companion.prototype.esc2c_za3lpa$ = function (c) {
    return c < this.ESC2C_MAX_0 ? toChar(this.ESC2C_0[c]) : this.INVALID_0;
  };
  JSON$Companion.prototype.hex_za3lpa$ = function (i) {
    var d = i & 15;
    return toBoxedChar(d < 10 ? toChar(d + (48 | 0) | 0) : toChar(d - 10 + (97 | 0) | 0));
  };
  JSON$Companion.prototype.switchMode_0 = function (mode, desc, typeParams) {
    switch (desc.kind.name) {
      case 'POLYMORPHIC':
        return JSON$Mode$POLY_getInstance();
      case 'LIST':
      case 'SET':
        return JSON$Mode$LIST_getInstance();
      case 'MAP':
        var keyKind = typeParams[0].serialClassDesc.kind;
        return keyKind === KSerialClassKind$PRIMITIVE_getInstance() || keyKind === KSerialClassKind$ENUM_getInstance() ? JSON$Mode$MAP_getInstance() : JSON$Mode$LIST_getInstance();
      case 'ENTRY':
        return mode === JSON$Mode$MAP_getInstance() ? JSON$Mode$ENTRY_getInstance() : JSON$Mode$OBJ_getInstance();
      default:return JSON$Mode$OBJ_getInstance();
    }
  };
  JSON$Companion.prototype.require_0 = function (condition, pos, msg) {
    if (!condition)
      this.fail_0(pos, msg());
  };
  JSON$Companion.prototype.fail_0 = function (pos, msg) {
    throw IllegalArgumentException_init_0('JSON at ' + pos + ': ' + msg);
  };
  JSON$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JSON$Companion_instance = null;
  function JSON$Companion_getInstance() {
    if (JSON$Companion_instance === null) {
      new JSON$Companion();
    }
    return JSON$Companion_instance;
  }
  function JSON$Mode(name, ordinal, begin, end) {
    Enum.call(this);
    this.begin = toBoxedChar(begin);
    this.end = toBoxedChar(end);
    this.name$ = name;
    this.ordinal$ = ordinal;
    this.beginTc = JSON$Companion_getInstance().c2tc_za3lpa$(unboxChar(this.begin) | 0);
    this.endTc = JSON$Companion_getInstance().c2tc_za3lpa$(unboxChar(this.end) | 0);
  }
  function JSON$Mode_initFields() {
    JSON$Mode_initFields = function () {
    };
    JSON$Mode$OBJ_instance = new JSON$Mode('OBJ', 0, JSON$Companion_getInstance().BEGIN_OBJ_0, JSON$Companion_getInstance().END_OBJ_0);
    JSON$Mode$LIST_instance = new JSON$Mode('LIST', 1, JSON$Companion_getInstance().BEGIN_LIST_0, JSON$Companion_getInstance().END_LIST_0);
    JSON$Mode$MAP_instance = new JSON$Mode('MAP', 2, JSON$Companion_getInstance().BEGIN_OBJ_0, JSON$Companion_getInstance().END_OBJ_0);
    JSON$Mode$POLY_instance = new JSON$Mode('POLY', 3, JSON$Companion_getInstance().BEGIN_LIST_0, JSON$Companion_getInstance().END_LIST_0);
    JSON$Mode$ENTRY_instance = new JSON$Mode('ENTRY', 4, JSON$Companion_getInstance().INVALID_0, JSON$Companion_getInstance().INVALID_0);
  }
  var JSON$Mode$OBJ_instance;
  function JSON$Mode$OBJ_getInstance() {
    JSON$Mode_initFields();
    return JSON$Mode$OBJ_instance;
  }
  var JSON$Mode$LIST_instance;
  function JSON$Mode$LIST_getInstance() {
    JSON$Mode_initFields();
    return JSON$Mode$LIST_instance;
  }
  var JSON$Mode$MAP_instance;
  function JSON$Mode$MAP_getInstance() {
    JSON$Mode_initFields();
    return JSON$Mode$MAP_instance;
  }
  var JSON$Mode$POLY_instance;
  function JSON$Mode$POLY_getInstance() {
    JSON$Mode_initFields();
    return JSON$Mode$POLY_instance;
  }
  var JSON$Mode$ENTRY_instance;
  function JSON$Mode$ENTRY_getInstance() {
    JSON$Mode_initFields();
    return JSON$Mode$ENTRY_instance;
  }
  JSON$Mode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mode',
    interfaces: [Enum]
  };
  function JSON$Mode$values() {
    return [JSON$Mode$OBJ_getInstance(), JSON$Mode$LIST_getInstance(), JSON$Mode$MAP_getInstance(), JSON$Mode$POLY_getInstance(), JSON$Mode$ENTRY_getInstance()];
  }
  JSON$Mode.values = JSON$Mode$values;
  function JSON$Mode$valueOf(name) {
    switch (name) {
      case 'OBJ':
        return JSON$Mode$OBJ_getInstance();
      case 'LIST':
        return JSON$Mode$LIST_getInstance();
      case 'MAP':
        return JSON$Mode$MAP_getInstance();
      case 'POLY':
        return JSON$Mode$POLY_getInstance();
      case 'ENTRY':
        return JSON$Mode$ENTRY_getInstance();
      default:throwISE('No enum constant kotlinx.serialization.json.JSON.Mode.' + name);
    }
  }
  JSON$Mode.valueOf_61zpoe$ = JSON$Mode$valueOf;
  function JSON$JsonOutput($outer, mode, w) {
    this.$outer = $outer;
    ElementValueOutput.call(this);
    this.mode = mode;
    this.w = w;
    this.context = this.$outer.context;
    this.forceStr_0 = false;
  }
  JSON$JsonOutput.prototype.writeBegin_276rha$ = function (desc, typeParams) {
    var newMode = JSON$Companion_getInstance().switchMode_0(this.mode, desc, typeParams);
    if (unboxChar(newMode.begin) !== JSON$Companion_getInstance().INVALID_0) {
      this.w.print_s8itvh$(unboxChar(newMode.begin));
      this.w.indent();
    }
    return this.mode === newMode ? this : new JSON$JsonOutput(this.$outer, newMode, this.w);
  };
  JSON$JsonOutput.prototype.writeEnd_f6e2p$ = function (desc) {
    if (unboxChar(this.mode.end) !== JSON$Companion_getInstance().INVALID_0) {
      this.w.unIndent();
      this.w.nextItem();
      this.w.print_s8itvh$(unboxChar(this.mode.end));
    }
  };
  JSON$JsonOutput.prototype.writeElement_xvmgof$ = function (desc, index) {
    switch (this.mode.name) {
      case 'LIST':
      case 'MAP':
        if (index === 0)
          return false;
        if (index > 1)
          this.w.print_s8itvh$(JSON$Companion_getInstance().COMMA_0);
        this.w.nextItem();
        break;
      case 'ENTRY':
      case 'POLY':
        if (index === 0)
          this.forceStr_0 = true;
        if (index === 1) {
          this.w.print_s8itvh$(this.mode === JSON$Mode$ENTRY_getInstance() ? JSON$Companion_getInstance().COLON_0 : JSON$Companion_getInstance().COMMA_0);
          this.w.space();
          this.forceStr_0 = false;
        }

        break;
      default:if (index > 0)
          this.w.print_s8itvh$(JSON$Companion_getInstance().COMMA_0);
        this.w.nextItem();
        this.writeStringValue_61zpoe$(desc.getElementName_za3lpa$(index));
        this.w.print_s8itvh$(JSON$Companion_getInstance().COLON_0);
        this.w.space();
        break;
    }
    return true;
  };
  JSON$JsonOutput.prototype.writeNullValue = function () {
    this.w.print_61zpoe$(JSON$Companion_getInstance().NULL_0);
  };
  JSON$JsonOutput.prototype.writeBooleanValue_6taknv$ = function (value) {
    if (this.forceStr_0)
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_6taknv$(value);
  };
  JSON$JsonOutput.prototype.writeByteValue_s8j3t7$ = function (value) {
    if (this.forceStr_0)
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_s8jyv4$(value);
  };
  JSON$JsonOutput.prototype.writeShortValue_mq22fl$ = function (value) {
    if (this.forceStr_0)
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_s8jyv4$(value);
  };
  JSON$JsonOutput.prototype.writeIntValue_za3lpa$ = function (value) {
    if (this.forceStr_0)
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_za3lpa$(value);
  };
  JSON$JsonOutput.prototype.writeLongValue_s8cxhz$ = function (value) {
    if (this.forceStr_0)
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_s8cxhz$(value);
  };
  JSON$JsonOutput.prototype.writeFloatValue_mx4ult$ = function (value) {
    if (this.forceStr_0 || !isFinite(value))
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_mx4ult$(value);
  };
  JSON$JsonOutput.prototype.writeDoubleValue_14dthe$ = function (value) {
    if (this.forceStr_0 || !isFinite_0(value))
      this.writeStringValue_61zpoe$(value.toString());
    else
      this.w.print_14dthe$(value);
  };
  JSON$JsonOutput.prototype.writeCharValue_s8itvh$ = function (value) {
    this.writeStringValue_61zpoe$(String.fromCharCode(value));
  };
  JSON$JsonOutput.prototype.writeStringValue_61zpoe$ = function (value) {
    var tmp$;
    if (this.$outer.unquoted_0 && !JSON$Companion_getInstance().mustBeQuoted_0(value)) {
      this.w.print_61zpoe$(value);
      return;
    }
    this.w.print_s8itvh$(JSON$Companion_getInstance().STRING_0);
    tmp$ = iterator(value);
    while (tmp$.hasNext()) {
      var c = unboxChar(tmp$.next());
      var esc = unboxChar(JSON$Companion_getInstance().c2esc_s8itvh$(c));
      switch (esc) {
        case 0:
          this.w.print_s8itvh$(c);
          break;
        case 117:
          this.w.print_s8itvh$(JSON$Companion_getInstance().STRING_ESC_0);
          this.w.print_s8itvh$(JSON$Companion_getInstance().UNICODE_ESC_0);
          var code = c | 0;
          this.w.print_s8itvh$(unboxChar(JSON$Companion_getInstance().hex_za3lpa$(code >> 12)));
          this.w.print_s8itvh$(unboxChar(JSON$Companion_getInstance().hex_za3lpa$(code >> 8)));
          this.w.print_s8itvh$(unboxChar(JSON$Companion_getInstance().hex_za3lpa$(code >> 4)));
          this.w.print_s8itvh$(unboxChar(JSON$Companion_getInstance().hex_za3lpa$(code)));
          break;
        default:this.w.print_s8itvh$(JSON$Companion_getInstance().STRING_ESC_0);
          this.w.print_s8itvh$(esc);
          break;
      }
    }
    this.w.print_s8itvh$(JSON$Companion_getInstance().STRING_0);
  };
  JSON$JsonOutput.prototype.writeNonSerializableValue_za3rmp$ = function (value) {
    this.writeStringValue_61zpoe$(value.toString());
  };
  JSON$JsonOutput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonOutput',
    interfaces: [ElementValueOutput]
  };
  function JSON$Composer($outer, w) {
    this.$outer = $outer;
    PrintWriter.call(this, w);
    this.level = 0;
  }
  JSON$Composer.prototype.indent = function () {
    this.level = this.level + 1 | 0;
  };
  JSON$Composer.prototype.unIndent = function () {
    this.level = this.level - 1 | 0;
  };
  JSON$Composer.prototype.nextItem = function () {
    if (this.$outer.indented_0) {
      this.println();
      var times = this.level;
      this.$outer;
      for (var index = 0; index < times; index++) {
        this.print_61zpoe$(this.$outer.indent_0);
      }
    }
  };
  JSON$Composer.prototype.space = function () {
    if (this.$outer.indented_0)
      this.print_s8itvh$(32);
  };
  JSON$Composer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Composer',
    interfaces: [PrintWriter]
  };
  function JSON$JsonInput($outer, mode, p) {
    this.$outer = $outer;
    ElementValueInput.call(this);
    this.mode = mode;
    this.p = p;
    this.curIndex = 0;
    this.entryIndex = 0;
    this.context = this.$outer.context;
  }
  Object.defineProperty(JSON$JsonInput.prototype, 'updateMode', {
    get: function () {
      return this.$outer.updateMode;
    }
  });
  function JSON$JsonInput$readBegin$lambda(closure$newMode, closure$desc) {
    return function () {
      return "Expected '" + String.fromCharCode(unboxChar(closure$newMode.begin)) + ', kind: ' + closure$desc.kind + "'";
    };
  }
  JSON$JsonInput.prototype.readBegin_276rha$ = function (desc, typeParams) {
    var tmp$;
    var newMode = JSON$Companion_getInstance().switchMode_0(this.mode, desc, typeParams);
    if (unboxChar(newMode.begin) !== JSON$Companion_getInstance().INVALID_0) {
      JSON$Companion_getInstance().require_0(this.p.curTc === newMode.beginTc, this.p.tokenPos, JSON$JsonInput$readBegin$lambda(newMode, desc));
      this.p.nextToken();
    }
    switch (newMode.name) {
      case 'LIST':
      case 'MAP':
      case 'POLY':
        tmp$ = new JSON$JsonInput(this.$outer, newMode, this.p);
        break;
      default:tmp$ = this.mode === newMode ? this : new JSON$JsonInput(this.$outer, newMode, this.p);
        break;
    }
    return tmp$;
  };
  function JSON$JsonInput$readEnd$lambda(this$JsonInput) {
    return function () {
      return "Expected '" + String.fromCharCode(unboxChar(this$JsonInput.mode.end)) + "'";
    };
  }
  JSON$JsonInput.prototype.readEnd_f6e2p$ = function (desc) {
    if (unboxChar(this.mode.end) !== JSON$Companion_getInstance().INVALID_0) {
      JSON$Companion_getInstance().require_0(this.p.curTc === this.mode.endTc, this.p.tokenPos, JSON$JsonInput$readEnd$lambda(this));
      this.p.nextToken();
    }
  };
  JSON$JsonInput.prototype.readNotNullMark = function () {
    return this.p.curTc !== JSON$Companion_getInstance().TC_NULL_0;
  };
  function JSON$JsonInput$readNullValue$lambda() {
    return "Expected 'null' literal";
  }
  JSON$JsonInput.prototype.readNullValue = function () {
    JSON$Companion_getInstance().require_0(this.p.curTc === JSON$Companion_getInstance().TC_NULL_0, this.p.tokenPos, JSON$JsonInput$readNullValue$lambda);
    this.p.nextToken();
    return null;
  };
  function JSON$JsonInput$readElement$lambda() {
    return "Expected ':'";
  }
  function JSON$JsonInput$readElement$lambda_0() {
    return "Expected ':'";
  }
  JSON$JsonInput.prototype.readElement_f6e2p$ = function (desc) {
    var tmp$, tmp$_0;
    while (true) {
      if (this.p.curTc === JSON$Companion_getInstance().TC_COMMA_0)
        this.p.nextToken();
      switch (this.mode.name) {
        case 'LIST':
        case 'MAP':
          if (!this.p.canBeginValue)
            return KInput$Companion_getInstance().READ_DONE;
          return this.curIndex = this.curIndex + 1 | 0, this.curIndex;
        case 'POLY':
          switch (tmp$ = this.entryIndex, this.entryIndex = tmp$ + 1 | 0, tmp$) {
            case 0:
              return 0;
            case 1:
              return 1;
            default:this.entryIndex = 0;
              return KInput$Companion_getInstance().READ_DONE;
          }

        case 'ENTRY':
          switch (tmp$_0 = this.entryIndex, this.entryIndex = tmp$_0 + 1 | 0, tmp$_0) {
            case 0:
              return 0;
            case 1:
              JSON$Companion_getInstance().require_0(this.p.curTc === JSON$Companion_getInstance().TC_COLON_0, this.p.tokenPos, JSON$JsonInput$readElement$lambda);
              this.p.nextToken();
              return 1;
            default:this.entryIndex = 0;
              return KInput$Companion_getInstance().READ_DONE;
          }

        default:if (!this.p.canBeginValue)
            return KInput$Companion_getInstance().READ_DONE;
          var key = this.p.takeStr();
          JSON$Companion_getInstance().require_0(this.p.curTc === JSON$Companion_getInstance().TC_COLON_0, this.p.tokenPos, JSON$JsonInput$readElement$lambda_0);
          this.p.nextToken();
          var ind = desc.getElementIndex_61zpoe$(key);
          if (ind !== KInput$Companion_getInstance().UNKNOWN_NAME)
            return ind;
          if (!this.$outer.nonstrict_8be2vx$)
            throw new SerializationException('Strict JSON encountered unknown key: ' + key);
          else
            this.p.skipElement_8be2vx$();
          break;
      }
    }
  };
  JSON$JsonInput.prototype.readBooleanValue = function () {
    return equals(this.p.takeStr(), 'true');
  };
  JSON$JsonInput.prototype.readByteValue = function () {
    return toByte_0(this.p.takeStr());
  };
  JSON$JsonInput.prototype.readShortValue = function () {
    return toShort_0(this.p.takeStr());
  };
  JSON$JsonInput.prototype.readIntValue = function () {
    return toInt(this.p.takeStr());
  };
  JSON$JsonInput.prototype.readLongValue = function () {
    return toLong(this.p.takeStr());
  };
  JSON$JsonInput.prototype.readFloatValue = function () {
    return toDouble(this.p.takeStr());
  };
  JSON$JsonInput.prototype.readDoubleValue = function () {
    return toDouble(this.p.takeStr());
  };
  JSON$JsonInput.prototype.readCharValue = function () {
    return toBoxedChar(single(this.p.takeStr()));
  };
  JSON$JsonInput.prototype.readStringValue = function () {
    return this.p.takeStr();
  };
  JSON$JsonInput.prototype.readEnumValue_xvqrpl$ = function (enumClass) {
    return enumFromName(enumClass, this.p.takeStr());
  };
  JSON$JsonInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsonInput',
    interfaces: [ElementValueInput]
  };
  function JSON$Parser(r) {
    this.r = r;
    this.charPos = 0;
    this.curChar = -1;
    this.tokenPos = 0;
    this.curTc = JSON$Companion_getInstance().TC_EOF_0;
    this.curStr = null;
    this.sb = new StringBuilder();
    this.nextChar_0();
    this.nextToken();
  }
  Object.defineProperty(JSON$Parser.prototype, 'canBeginValue', {
    get: function () {
      switch (this.curTc) {
        case 8:
        case 6:
        case 0:
        case 10:
        case 12:
          return true;
        default:return false;
      }
    }
  });
  JSON$Parser.prototype.takeStr = function () {
    var tmp$;
    var prevStr = (tmp$ = this.curStr) != null ? tmp$ : JSON$Companion_getInstance().fail_0(this.tokenPos, 'Expected string or non-null literal');
    this.nextToken();
    return prevStr;
  };
  JSON$Parser.prototype.nextToken = function () {
    while (true) {
      this.tokenPos = this.charPos;
      this.curTc = JSON$Companion_getInstance().c2tc_za3lpa$(this.curChar);
      switch (this.curTc) {
        case 3:
          this.nextChar_0();
          break;
        case 0:
          this.nextLiteral_0();
          return;
        case 10:
          this.nextString_0();
          return;
        default:this.nextChar_0();
          this.curStr = null;
          return;
      }
    }
  };
  JSON$Parser.prototype.nextChar_0 = function () {
    this.curChar = this.r.read();
    this.charPos = this.charPos + 1 | 0;
  };
  JSON$Parser.prototype.nextLiteral_0 = function () {
    this.sb = new StringBuilder();
    while (true) {
      this.sb.append_s8itvh$(toChar(this.curChar));
      this.nextChar_0();
      if (JSON$Companion_getInstance().c2tc_za3lpa$(this.curChar) !== JSON$Companion_getInstance().TC_OTHER_0)
        break;
    }
    if (equals(JSON$Companion_getInstance().NULL_0, this.sb.toString())) {
      this.curStr = null;
      this.curTc = JSON$Companion_getInstance().TC_NULL_0;
    }
     else {
      this.curStr = this.sb.toString();
      this.curTc = JSON$Companion_getInstance().TC_OTHER_0;
    }
  };
  function JSON$Parser$nextString$lambda() {
    return 'Unexpected end after escape char';
  }
  function JSON$Parser$nextString$lambda_0(this$Parser) {
    return function () {
      return "Invalid escaped char '" + String.fromCharCode(toChar(this$Parser.curChar)) + "'";
    };
  }
  JSON$Parser.prototype.nextString_0 = function () {
    this.sb = new StringBuilder();
    parse: while (true) {
      this.nextChar_0();
      switch (JSON$Companion_getInstance().c2tc_za3lpa$(this.curChar)) {
        case 1:
          JSON$Companion_getInstance().fail_0(this.charPos, 'Unexpected end in string');
          break;
        case 10:
          this.nextChar_0();
          break parse;
        case 11:
          this.nextChar_0();
          JSON$Companion_getInstance().require_0(this.curChar >= 0, this.charPos, JSON$Parser$nextString$lambda);
          if (this.curChar === (JSON$Companion_getInstance().UNICODE_ESC_0 | 0)) {
            this.sb.append_s8itvh$(toChar((this.hex_0() << 12) + (this.hex_0() << 8) + (this.hex_0() << 4) + this.hex_0() | 0));
          }
           else {
            var c = unboxChar(JSON$Companion_getInstance().esc2c_za3lpa$(this.curChar));
            JSON$Companion_getInstance().require_0(c !== JSON$Companion_getInstance().INVALID_0, this.charPos, JSON$Parser$nextString$lambda_0(this));
            this.sb.append_s8itvh$(c);
          }

          break;
        default:this.sb.append_s8itvh$(toChar(this.curChar));
          break;
      }
    }
    this.curStr = this.sb.toString();
    this.curTc = JSON$Companion_getInstance().TC_STRING_0;
  };
  function JSON$Parser$hex$lambda() {
    return 'Unexpected end in unicode escape ';
  }
  JSON$Parser.prototype.hex_0 = function () {
    var tmp$, tmp$_0;
    this.nextChar_0();
    JSON$Companion_getInstance().require_0(this.curChar >= 0, this.charPos, JSON$Parser$hex$lambda);
    tmp$ = toChar(this.curChar);
    if ((new CharRange(48, 57)).contains_mef7kx$(tmp$))
      tmp$_0 = this.curChar - (48 | 0) | 0;
    else if ((new CharRange(97, 102)).contains_mef7kx$(tmp$))
      tmp$_0 = this.curChar - (97 | 0) + 10 | 0;
    else if ((new CharRange(65, 70)).contains_mef7kx$(tmp$))
      tmp$_0 = this.curChar - (65 | 0) + 10 | 0;
    else
      throw JSON$Companion_getInstance().fail_0(this.charPos, "Invalid hex char '" + String.fromCharCode(toChar(this.curChar)) + "' in unicode escape");
    return tmp$_0;
  };
  JSON$Parser.prototype.state_8be2vx$ = function () {
    return 'Parser(charPos=' + this.charPos + ', curChar=' + this.curChar + ', tokenPos=' + this.tokenPos + ', curTc=' + this.curTc + ', curStr=' + toString(this.curStr) + ')';
  };
  JSON$Parser.prototype.skipElement_8be2vx$ = function () {
    if (this.curTc !== JSON$Companion_getInstance().TC_BEGIN_OBJ_0 && this.curTc !== JSON$Companion_getInstance().TC_BEGIN_LIST_0) {
      this.nextToken();
      return;
    }
    var tokenStack = ArrayList_init_0();
    do {
      switch (this.curTc) {
        case 8:
        case 6:
          tokenStack.add_11rb$(this.curTc);
          break;
        case 9:
          if (last(tokenStack) !== JSON$Companion_getInstance().TC_BEGIN_LIST_0)
            throw new SerializationException('Invalid JSON at ' + this.charPos + ': found ] instead of }');
          tokenStack.removeAt_za3lpa$(tokenStack.size - 1 | 0);
          break;
        case 7:
          if (last(tokenStack) !== JSON$Companion_getInstance().TC_BEGIN_OBJ_0)
            throw new SerializationException('Invalid JSON at ' + this.charPos + ': found } instead of ]');
          tokenStack.removeAt_za3lpa$(tokenStack.size - 1 | 0);
          break;
      }
      this.nextToken();
      var isNotEmpty$result;
      isNotEmpty$result = !tokenStack.isEmpty();
    }
     while (isNotEmpty$result);
  };
  JSON$Parser.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Parser',
    interfaces: []
  };
  JSON_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JSON',
    interfaces: []
  };
  JSON_0.prototype.component1_0 = function () {
    return this.unquoted_0;
  };
  JSON_0.prototype.component2_0 = function () {
    return this.indented_0;
  };
  JSON_0.prototype.component3_0 = function () {
    return this.indent_0;
  };
  JSON_0.prototype.component4_8be2vx$ = function () {
    return this.nonstrict_8be2vx$;
  };
  JSON_0.prototype.component5 = function () {
    return this.updateMode;
  };
  JSON_0.prototype.component6 = function () {
    return this.context;
  };
  JSON_0.prototype.copy_4ewq9t$ = function (unquoted, indented, indent, nonstrict, updateMode, context) {
    return new JSON_0(unquoted === void 0 ? this.unquoted_0 : unquoted, indented === void 0 ? this.indented_0 : indented, indent === void 0 ? this.indent_0 : indent, nonstrict === void 0 ? this.nonstrict_8be2vx$ : nonstrict, updateMode === void 0 ? this.updateMode : updateMode, context === void 0 ? this.context : context);
  };
  JSON_0.prototype.toString = function () {
    return 'JSON(unquoted=' + Kotlin.toString(this.unquoted_0) + (', indented=' + Kotlin.toString(this.indented_0)) + (', indent=' + Kotlin.toString(this.indent_0)) + (', nonstrict=' + Kotlin.toString(this.nonstrict_8be2vx$)) + (', updateMode=' + Kotlin.toString(this.updateMode)) + (', context=' + Kotlin.toString(this.context)) + ')';
  };
  JSON_0.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.unquoted_0) | 0;
    result = result * 31 + Kotlin.hashCode(this.indented_0) | 0;
    result = result * 31 + Kotlin.hashCode(this.indent_0) | 0;
    result = result * 31 + Kotlin.hashCode(this.nonstrict_8be2vx$) | 0;
    result = result * 31 + Kotlin.hashCode(this.updateMode) | 0;
    result = result * 31 + Kotlin.hashCode(this.context) | 0;
    return result;
  };
  JSON_0.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.unquoted_0, other.unquoted_0) && Kotlin.equals(this.indented_0, other.indented_0) && Kotlin.equals(this.indent_0, other.indent_0) && Kotlin.equals(this.nonstrict_8be2vx$, other.nonstrict_8be2vx$) && Kotlin.equals(this.updateMode, other.updateMode) && Kotlin.equals(this.context, other.context)))));
  };
  function ProtoNumberType(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ProtoNumberType_initFields() {
    ProtoNumberType_initFields = function () {
    };
    ProtoNumberType$DEFAULT_instance = new ProtoNumberType('DEFAULT', 0);
    ProtoNumberType$SIGNED_instance = new ProtoNumberType('SIGNED', 1);
    ProtoNumberType$FIXED_instance = new ProtoNumberType('FIXED', 2);
  }
  var ProtoNumberType$DEFAULT_instance;
  function ProtoNumberType$DEFAULT_getInstance() {
    ProtoNumberType_initFields();
    return ProtoNumberType$DEFAULT_instance;
  }
  var ProtoNumberType$SIGNED_instance;
  function ProtoNumberType$SIGNED_getInstance() {
    ProtoNumberType_initFields();
    return ProtoNumberType$SIGNED_instance;
  }
  var ProtoNumberType$FIXED_instance;
  function ProtoNumberType$FIXED_getInstance() {
    ProtoNumberType_initFields();
    return ProtoNumberType$FIXED_instance;
  }
  ProtoNumberType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtoNumberType',
    interfaces: [Enum]
  };
  function ProtoNumberType$values() {
    return [ProtoNumberType$DEFAULT_getInstance(), ProtoNumberType$SIGNED_getInstance(), ProtoNumberType$FIXED_getInstance()];
  }
  ProtoNumberType.values = ProtoNumberType$values;
  function ProtoNumberType$valueOf(name) {
    switch (name) {
      case 'DEFAULT':
        return ProtoNumberType$DEFAULT_getInstance();
      case 'SIGNED':
        return ProtoNumberType$SIGNED_getInstance();
      case 'FIXED':
        return ProtoNumberType$FIXED_getInstance();
      default:throwISE('No enum constant kotlinx.serialization.protobuf.ProtoNumberType.' + name);
    }
  }
  ProtoNumberType.valueOf_61zpoe$ = ProtoNumberType$valueOf;
  function ProtoType(type) {
    this.type = type;
  }
  function ProtoType$Impl() {
  }
  ProtoType$Impl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Impl',
    interfaces: [ProtoType]
  };
  ProtoType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtoType',
    interfaces: [Annotation]
  };
  function ProtoBuf(context) {
    ProtoBuf$Companion_getInstance();
    if (context === void 0)
      context = null;
    this.context = context;
  }
  function ProtoBuf$ProtobufWriter($outer, encoder) {
    this.$outer = $outer;
    TaggedOutput.call(this);
    this.encoder = encoder;
    this.context = this.$outer.context;
  }
  ProtoBuf$ProtobufWriter.prototype.writeBegin_276rha$ = function (desc, typeParams) {
    switch (desc.kind.name) {
      case 'LIST':
      case 'MAP':
      case 'SET':
        return new ProtoBuf$RepeatedWriter(this.$outer, this.encoder, this.currentTag);
      case 'CLASS':
      case 'OBJECT':
      case 'SEALED':
      case 'POLYMORPHIC':
        return new ProtoBuf$ObjectWriter(this.$outer, this.currentTagOrNull, this.encoder);
      case 'ENTRY':
        return new ProtoBuf$MapEntryWriter(this.$outer, this.currentTagOrNull, this.encoder);
      default:throw new SerializationException('Primitives are not supported at top-level');
    }
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedInt_dpg1yx$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedByte_19qe40$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedShort_veccj0$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedLong_19wkf8$ = function (tag, value) {
    this.encoder.writeLong_scxzc4$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedFloat_vlf4p8$ = function (tag, value) {
    this.encoder.writeFloat_vjorfl$(value, tag.first);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedDouble_e37ph5$ = function (tag, value) {
    this.encoder.writeDouble_12fank$(value, tag.first);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedBoolean_iuyhfk$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value ? 1 : 0, tag.first, ProtoNumberType$DEFAULT_getInstance());
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedChar_19qo1q$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value | 0, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedString_l9l8mx$ = function (tag, value) {
    this.encoder.writeString_bm4lxs$(value, tag.first);
  };
  ProtoBuf$ProtobufWriter.prototype.writeTaggedEnum_qffkiy$ = function (tag, enumClass, value) {
    this.encoder.writeInt_hp6twd$(value.ordinal, tag.first, ProtoNumberType$DEFAULT_getInstance());
  };
  ProtoBuf$ProtobufWriter.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    return ProtoBuf$Companion_getInstance().getProtoDesc_0($receiver, index);
  };
  ProtoBuf$ProtobufWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufWriter',
    interfaces: [TaggedOutput]
  };
  function ProtoBuf$ObjectWriter($outer, parentTag, parentEncoder, stream) {
    this.$outer = $outer;
    if (stream === void 0)
      stream = ByteArrayOutputStream_init();
    ProtoBuf$ProtobufWriter.call(this, this.$outer, new ProtoBuf$ProtobufEncoder(stream));
    this.parentTag = parentTag;
    this.parentEncoder_0 = parentEncoder;
    this.stream_0 = stream;
  }
  ProtoBuf$ObjectWriter.prototype.writeFinished_f6e2p$ = function (desc) {
    if (this.parentTag != null) {
      this.parentEncoder_0.writeObject_ir89t6$(this.stream_0.toByteArray(), this.parentTag.first);
    }
     else {
      this.parentEncoder_0.out.write_fqrh44$(this.stream_0.toByteArray());
    }
  };
  ProtoBuf$ObjectWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ObjectWriter',
    interfaces: [ProtoBuf$ProtobufWriter]
  };
  function ProtoBuf$MapEntryWriter($outer, parentTag, parentEncoder) {
    this.$outer = $outer;
    ProtoBuf$ObjectWriter.call(this, this.$outer, parentTag, parentEncoder);
  }
  ProtoBuf$MapEntryWriter.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    if (index === 0) {
      return to(1, (tmp$_0 = (tmp$ = this.parentTag) != null ? tmp$.second : null) != null ? tmp$_0 : ProtoNumberType$DEFAULT_getInstance());
    }
     else {
      return to(2, (tmp$_2 = (tmp$_1 = this.parentTag) != null ? tmp$_1.second : null) != null ? tmp$_2 : ProtoNumberType$DEFAULT_getInstance());
    }
  };
  ProtoBuf$MapEntryWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntryWriter',
    interfaces: [ProtoBuf$ObjectWriter]
  };
  function ProtoBuf$RepeatedWriter($outer, encoder, curTag) {
    this.$outer = $outer;
    ProtoBuf$ProtobufWriter.call(this, this.$outer, encoder);
    this.curTag = curTag;
  }
  ProtoBuf$RepeatedWriter.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    return this.curTag;
  };
  ProtoBuf$RepeatedWriter.prototype.shouldWriteElement_6zine4$ = function (desc, tag, index) {
    return index !== SIZE_INDEX;
  };
  ProtoBuf$RepeatedWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RepeatedWriter',
    interfaces: [ProtoBuf$ProtobufWriter]
  };
  function ProtoBuf$ProtobufEncoder(out) {
    this.out = out;
  }
  ProtoBuf$ProtobufEncoder.prototype.writeObject_ir89t6$ = function (bytes, tag) {
    var header = this.encode32_0(tag << 3 | ProtoBuf$Companion_getInstance().SIZE_DELIMITED_0);
    var len = this.encode32_0(bytes.length);
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(len);
    this.out.write_fqrh44$(bytes);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeInt_hp6twd$ = function (value, tag, format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? ProtoBuf$Companion_getInstance().i32_0 : ProtoBuf$Companion_getInstance().VARINT_0;
    var header = this.encode32_0(tag << 3 | wireType);
    var content = this.encode32_0(value, format);
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeLong_scxzc4$ = function (value, tag, format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? ProtoBuf$Companion_getInstance().i64_0 : ProtoBuf$Companion_getInstance().VARINT_0;
    var header = this.encode32_0(tag << 3 | wireType);
    var content = this.encode64_0(value, format);
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeString_bm4lxs$ = function (value, tag) {
    var bytes = toUtf8Bytes(value);
    this.writeObject_ir89t6$(bytes, tag);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeDouble_12fank$ = function (value, tag) {
    var header = this.encode32_0(tag << 3 | ProtoBuf$Companion_getInstance().i64_0);
    var content = ByteBuffer$Companion_getInstance().allocate_za3lpa$(8).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).putDouble_14dthe$(value).array();
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeFloat_vjorfl$ = function (value, tag) {
    var header = this.encode32_0(tag << 3 | ProtoBuf$Companion_getInstance().i32_0);
    var content = ByteBuffer$Companion_getInstance().allocate_za3lpa$(4).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).putFloat_mx4ult$(value).array();
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.encode32_0 = function (number, format) {
    if (format === void 0)
      format = ProtoNumberType$DEFAULT_getInstance();
    switch (format.name) {
      case 'FIXED':
        return ByteBuffer$Companion_getInstance().allocate_za3lpa$(4).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).putInt_za3lpa$(number).array();
      case 'DEFAULT':
        return ProtoBuf$Varint_getInstance().encodeVarint_8e33dg$(Kotlin.Long.fromInt(number));
      case 'SIGNED':
        return ProtoBuf$Varint_getInstance().encodeVarint_kcn2v3$(number << 1 ^ number >> 31);
      default:return Kotlin.noWhenBranchMatched();
    }
  };
  ProtoBuf$ProtobufEncoder.prototype.encode64_0 = function (number, format) {
    if (format === void 0)
      format = ProtoNumberType$DEFAULT_getInstance();
    switch (format.name) {
      case 'FIXED':
        return ByteBuffer$Companion_getInstance().allocate_za3lpa$(8).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).putLong_s8cxhz$(number).array();
      case 'DEFAULT':
        return ProtoBuf$Varint_getInstance().encodeVarint_8e33dg$(number);
      case 'SIGNED':
        return ProtoBuf$Varint_getInstance().encodeVarint_8e33dg$(number.shiftLeft(1).xor(number.shiftRight(63)));
      default:return Kotlin.noWhenBranchMatched();
    }
  };
  ProtoBuf$ProtobufEncoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufEncoder',
    interfaces: []
  };
  function ProtoBuf$ProtobufReader($outer, decoder) {
    this.$outer = $outer;
    TaggedInput.call(this);
    this.decoder = decoder;
    this.context = this.$outer.context;
    this.indexByTag_0 = LinkedHashMap_init();
  }
  ProtoBuf$ProtobufReader.prototype.findIndexByTag_0 = function (desc, serialId) {
    var tmp$;
    var $receiver = until(0, desc.associatedFieldsCount);
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$_0;
      tmp$_0 = $receiver.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        if (this.getTag_fr5t0y$(desc, element).first === serialId) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }
      }
      firstOrNull$result = null;
    }
     while (false);
    return (tmp$ = firstOrNull$result) != null ? tmp$ : -1;
  };
  ProtoBuf$ProtobufReader.prototype.readBegin_276rha$ = function (desc, typeParams) {
    switch (desc.kind.name) {
      case 'LIST':
      case 'MAP':
      case 'SET':
        return new ProtoBuf$RepeatedReader(this.$outer, this.decoder, this.currentTag);
      case 'CLASS':
      case 'OBJECT':
      case 'SEALED':
      case 'POLYMORPHIC':
        return new ProtoBuf$ProtobufReader(this.$outer, ProtoBuf$Companion_getInstance().makeDelimited_0(this.decoder, this.currentTagOrNull));
      case 'ENTRY':
        return new ProtoBuf$MapEntryReader(this.$outer, ProtoBuf$Companion_getInstance().makeDelimited_0(this.decoder, this.currentTagOrNull), this.currentTagOrNull);
      default:throw new SerializationException('Primitives are not supported at top-level');
    }
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedBoolean_11rb$ = function (tag) {
    switch (this.decoder.nextInt_bmwen1$(ProtoNumberType$DEFAULT_getInstance())) {
      case 0:
        return false;
      case 1:
        return true;
      default:throw new ProtobufDecodingException('Expected boolean value');
    }
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedByte_11rb$ = function (tag) {
    return toByte(this.decoder.nextInt_bmwen1$(tag.second));
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedShort_11rb$ = function (tag) {
    return toShort(this.decoder.nextInt_bmwen1$(tag.second));
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedInt_11rb$ = function (tag) {
    return this.decoder.nextInt_bmwen1$(tag.second);
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedLong_11rb$ = function (tag) {
    return this.decoder.nextLong_bmwen1$(tag.second);
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedFloat_11rb$ = function (tag) {
    return this.decoder.nextFloat();
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedDouble_11rb$ = function (tag) {
    return this.decoder.nextDouble();
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedChar_11rb$ = function (tag) {
    return toBoxedChar(toChar(this.decoder.nextInt_bmwen1$(tag.second)));
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedString_11rb$ = function (tag) {
    return this.decoder.nextString();
  };
  ProtoBuf$ProtobufReader.prototype.readTaggedEnum_bu9nms$ = function (tag, enumClass) {
    return enumFromOrdinal(enumClass, this.decoder.nextInt_bmwen1$(ProtoNumberType$DEFAULT_getInstance()));
  };
  ProtoBuf$ProtobufReader.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    return ProtoBuf$Companion_getInstance().getProtoDesc_0($receiver, index);
  };
  ProtoBuf$ProtobufReader.prototype.readElement_f6e2p$ = function (desc) {
    while (true) {
      if (this.decoder.curId === -1)
        return KInput$Companion_getInstance().READ_DONE;
      var $receiver = this.indexByTag_0;
      var key = this.decoder.curId;
      var tmp$;
      var value = $receiver.get_11rb$(key);
      if (value == null) {
        var answer = this.findIndexByTag_0(desc, this.decoder.curId);
        $receiver.put_xwzc9p$(key, answer);
        tmp$ = answer;
      }
       else {
        tmp$ = value;
      }
      var ind = tmp$;
      if (ind === -1)
        this.decoder.skipElement();
      else
        return ind;
    }
  };
  ProtoBuf$ProtobufReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufReader',
    interfaces: [TaggedInput]
  };
  function ProtoBuf$RepeatedReader($outer, decoder, targetTag) {
    this.$outer = $outer;
    ProtoBuf$ProtobufReader.call(this, this.$outer, decoder);
    this.targetTag = targetTag;
    this.ind_0 = 0;
  }
  ProtoBuf$RepeatedReader.prototype.readElement_f6e2p$ = function (desc) {
    return this.decoder.curId === this.targetTag.first ? (this.ind_0 = this.ind_0 + 1 | 0, this.ind_0) : KInput$Companion_getInstance().READ_DONE;
  };
  ProtoBuf$RepeatedReader.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    return this.targetTag;
  };
  ProtoBuf$RepeatedReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RepeatedReader',
    interfaces: [ProtoBuf$ProtobufReader]
  };
  function ProtoBuf$MapEntryReader($outer, decoder, parentTag) {
    this.$outer = $outer;
    ProtoBuf$ProtobufReader.call(this, this.$outer, decoder);
    this.parentTag = parentTag;
  }
  ProtoBuf$MapEntryReader.prototype.getTag_fr5t0y$ = function ($receiver, index) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    if (index === 0) {
      return to(1, (tmp$_0 = (tmp$ = this.parentTag) != null ? tmp$.second : null) != null ? tmp$_0 : ProtoNumberType$DEFAULT_getInstance());
    }
     else {
      return to(2, (tmp$_2 = (tmp$_1 = this.parentTag) != null ? tmp$_1.second : null) != null ? tmp$_2 : ProtoNumberType$DEFAULT_getInstance());
    }
  };
  ProtoBuf$MapEntryReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapEntryReader',
    interfaces: [ProtoBuf$ProtobufReader]
  };
  function ProtoBuf$ProtobufDecoder(inp) {
    this.inp = inp;
    this.curTag_0 = to(-1, -1);
    this.readTag_0();
  }
  Object.defineProperty(ProtoBuf$ProtobufDecoder.prototype, 'curId', {
    get: function () {
      return this.curTag_0.first;
    }
  });
  ProtoBuf$ProtobufDecoder.prototype.readTag_0 = function () {
    var tmp$;
    var header = this.decode32_0(void 0, true);
    if (header === -1) {
      tmp$ = to(-1, -1);
    }
     else {
      var wireType = header & 7;
      var fieldId = header >>> 3;
      tmp$ = to(fieldId, wireType);
    }
    this.curTag_0 = tmp$;
    return this.curTag_0;
  };
  ProtoBuf$ProtobufDecoder.prototype.skipElement = function () {
    switch (this.curTag_0.second) {
      case 0:
        this.nextInt_bmwen1$(ProtoNumberType$DEFAULT_getInstance());
        break;
      case 1:
        this.nextLong_bmwen1$(ProtoNumberType$FIXED_getInstance());
        break;
      case 2:
        this.nextObject();
        break;
      case 5:
        this.nextInt_bmwen1$(ProtoNumberType$FIXED_getInstance());
        break;
    }
    this.readTag_0();
  };
  ProtoBuf$ProtobufDecoder.prototype.nextObject = function () {
    if (this.curTag_0.second !== ProtoBuf$Companion_getInstance().SIZE_DELIMITED_0)
      throw new ProtobufDecodingException('Unexpected wire type: ' + this.curTag_0.second);
    var len = this.decode32_0();
    if (!(len >= 0)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    var ans = readExactNBytes(this.inp, len);
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextInt_bmwen1$ = function (format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? ProtoBuf$Companion_getInstance().i32_0 : ProtoBuf$Companion_getInstance().VARINT_0;
    if (wireType !== this.curTag_0.second)
      throw new ProtobufDecodingException('Unexpected wire type: ' + this.curTag_0.second);
    var ans = this.decode32_0(format);
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextLong_bmwen1$ = function (format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? ProtoBuf$Companion_getInstance().i64_0 : ProtoBuf$Companion_getInstance().VARINT_0;
    if (wireType !== this.curTag_0.second)
      throw new ProtobufDecodingException('Unexpected wire type: ' + this.curTag_0.second);
    var ans = this.decode64_0(format);
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextFloat = function () {
    if (this.curTag_0.second !== ProtoBuf$Companion_getInstance().i32_0)
      throw new ProtobufDecodingException('Unexpected wire type: ' + this.curTag_0.second);
    var ans = readToByteBuffer(this.inp, 4).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).getFloat();
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextDouble = function () {
    if (this.curTag_0.second !== ProtoBuf$Companion_getInstance().i64_0)
      throw new ProtobufDecodingException('Unexpected wire type: ' + this.curTag_0.second);
    var ans = readToByteBuffer(this.inp, 8).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).getDouble();
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextString = function () {
    var bytes = this.nextObject();
    return stringFromUtf8Bytes(bytes);
  };
  ProtoBuf$ProtobufDecoder.prototype.decode32_0 = function (format, eofAllowed) {
    if (format === void 0)
      format = ProtoNumberType$DEFAULT_getInstance();
    if (eofAllowed === void 0)
      eofAllowed = false;
    switch (format.name) {
      case 'DEFAULT':
        return ProtoBuf$Varint_getInstance().decodeVarint_pwta7l$(this.inp, 64, eofAllowed).toInt();
      case 'SIGNED':
        return ProtoBuf$Varint_getInstance().decodeSignedVarintInt_wq5eom$(this.inp);
      case 'FIXED':
        return readToByteBuffer(this.inp, 4).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).getInt();
      default:return Kotlin.noWhenBranchMatched();
    }
  };
  ProtoBuf$ProtobufDecoder.prototype.decode64_0 = function (format) {
    if (format === void 0)
      format = ProtoNumberType$DEFAULT_getInstance();
    switch (format.name) {
      case 'DEFAULT':
        return ProtoBuf$Varint_getInstance().decodeVarint_pwta7l$(this.inp, 64);
      case 'SIGNED':
        return ProtoBuf$Varint_getInstance().decodeSignedVarintLong_wq5eom$(this.inp);
      case 'FIXED':
        return readToByteBuffer(this.inp, 8).order_w2g0y3$(ByteOrder$LITTLE_ENDIAN_getInstance()).getLong();
      default:return Kotlin.noWhenBranchMatched();
    }
  };
  ProtoBuf$ProtobufDecoder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufDecoder',
    interfaces: []
  };
  function ProtoBuf$Varint() {
    ProtoBuf$Varint_instance = this;
  }
  ProtoBuf$Varint.prototype.encodeVarint_kcn2v3$ = function (inp) {
    var tmp$;
    var value = inp;
    var byteArrayList = new Int8Array(10);
    var i = 0;
    while ((value & (new Kotlin.Long(-128, 0)).toInt()) !== 0) {
      byteArrayList[tmp$ = i, i = tmp$ + 1 | 0, tmp$] = toByte(value & 127 | 128);
      value = value >>> 7;
    }
    byteArrayList[i] = toByte(value & 127);
    var out = new Int8Array(i + 1 | 0);
    while (i >= 0) {
      out[i] = byteArrayList[i];
      i = i - 1 | 0;
    }
    return out;
  };
  ProtoBuf$Varint.prototype.encodeVarint_8e33dg$ = function (inp) {
    var tmp$;
    var value = inp;
    var byteArrayList = new Int8Array(10);
    var i = 0;
    while (!equals(value.and(Kotlin.Long.fromInt(-128)), Kotlin.Long.ZERO)) {
      byteArrayList[tmp$ = i, i = tmp$ + 1 | 0, tmp$] = toByte(value.and(Kotlin.Long.fromInt(127)).or(Kotlin.Long.fromInt(128)).toInt());
      value = value.shiftRightUnsigned(7);
    }
    byteArrayList[i] = toByte(value.and(Kotlin.Long.fromInt(127)).toInt());
    var out = new Int8Array(i + 1 | 0);
    while (i >= 0) {
      out[i] = byteArrayList[i];
      i = i - 1 | 0;
    }
    return out;
  };
  ProtoBuf$Varint.prototype.decodeVarint_pwta7l$ = function (inp, bitLimit, eofOnStartAllowed) {
    if (bitLimit === void 0)
      bitLimit = 32;
    if (eofOnStartAllowed === void 0)
      eofOnStartAllowed = false;
    var result = Kotlin.Long.ZERO;
    var shift = 0;
    var b;
    do {
      if (shift >= bitLimit) {
        throw new ProtobufDecodingException('Varint too long');
      }
      b = inp.read();
      if (b === -1) {
        if (eofOnStartAllowed && shift === 0)
          return Kotlin.Long.NEG_ONE;
        else
          throw new IOException('Unexpected EOF');
      }
      result = result.or(Kotlin.Long.fromInt(b).and(Kotlin.Long.fromInt(127)).shiftLeft(shift));
      shift = shift + 7 | 0;
    }
     while ((b & 128) !== 0);
    return result;
  };
  ProtoBuf$Varint.prototype.decodeSignedVarintInt_wq5eom$ = function (inp) {
    var raw = this.decodeVarint_pwta7l$(inp, 32).toInt();
    var temp = (raw << 31 >> 31 ^ raw) >> 1;
    return temp ^ raw & 1 << 31;
  };
  ProtoBuf$Varint.prototype.decodeSignedVarintLong_wq5eom$ = function (inp) {
    var raw = this.decodeVarint_pwta7l$(inp, 64);
    var temp = raw.shiftLeft(63).shiftRight(63).xor(raw).shiftRight(1);
    return temp.xor(raw.and(new Kotlin.Long(0, -2147483648)));
  };
  ProtoBuf$Varint.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Varint',
    interfaces: []
  };
  var ProtoBuf$Varint_instance = null;
  function ProtoBuf$Varint_getInstance() {
    if (ProtoBuf$Varint_instance === null) {
      new ProtoBuf$Varint();
    }
    return ProtoBuf$Varint_instance;
  }
  function ProtoBuf$Companion() {
    ProtoBuf$Companion_instance = this;
    this.VARINT_0 = 0;
    this.i64_0 = 1;
    this.SIZE_DELIMITED_0 = 2;
    this.i32_0 = 5;
    this.plain = new ProtoBuf();
  }
  ProtoBuf$Companion.prototype.makeDelimited_0 = function (decoder, parentTag) {
    if (parentTag == null)
      return decoder;
    var bytes = decoder.nextObject();
    return new ProtoBuf$ProtobufDecoder(ByteArrayInputStream_init(bytes));
  };
  ProtoBuf$Companion.prototype.getProtoDesc_0 = function ($receiver, index) {
    var tmp$, tmp$_0;
    var $receiver_0 = $receiver.getAnnotationsForIndex_za3lpa$(index);
    var destination = ArrayList_init_0();
    var tmp$_1;
    tmp$_1 = $receiver_0.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      if (Kotlin.isType(element, SerialId))
        destination.add_11rb$(element);
    }
    var tag = single_0(destination).id;
    var $receiver_1 = $receiver.getAnnotationsForIndex_za3lpa$(index);
    var destination_0 = ArrayList_init_0();
    var tmp$_2;
    tmp$_2 = $receiver_1.iterator();
    while (tmp$_2.hasNext()) {
      var element_0 = tmp$_2.next();
      if (Kotlin.isType(element_0, ProtoType))
        destination_0.add_11rb$(element_0);
    }
    var format = (tmp$_0 = (tmp$ = onlySingleOrNull(destination_0)) != null ? tmp$.type : null) != null ? tmp$_0 : ProtoNumberType$DEFAULT_getInstance();
    return to(tag, format);
  };
  ProtoBuf$Companion.prototype.dump_20fw5n$ = function (saver, obj) {
    return this.plain.dump_20fw5n$(saver, obj);
  };
  ProtoBuf$Companion.prototype.dump_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.Companion.dump_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      var $this = this.plain;
      return $this.dump_20fw5n$(klassSerializer($this.context, getKClass(T_0)), obj);
    };
  }));
  ProtoBuf$Companion.prototype.dumps_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.Companion.dumps_issdgt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      var $this = this.plain;
      return internal.HexConverter.printHexBinary_1fhb37$($this.dump_20fw5n$(klassSerializer($this.context, getKClass(T_0)), obj), true);
    };
  }));
  ProtoBuf$Companion.prototype.load_8dtdds$ = function (loader, raw) {
    return this.plain.load_8dtdds$(loader, raw);
  };
  ProtoBuf$Companion.prototype.load_5geitx$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.Companion.load_5geitx$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, raw) {
      var $this = this.plain;
      return $this.load_8dtdds$(klassSerializer($this.context, getKClass(T_0)), raw);
    };
  }));
  ProtoBuf$Companion.prototype.loads_3zqiyt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.Companion.loads_3zqiyt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, hex) {
      var $this = this.plain;
      var raw = internal.HexConverter.parseHexBinary_61zpoe$(hex);
      return $this.load_8dtdds$(klassSerializer($this.context, getKClass(T_0)), raw);
    };
  }));
  ProtoBuf$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ProtoBuf$Companion_instance = null;
  function ProtoBuf$Companion_getInstance() {
    if (ProtoBuf$Companion_instance === null) {
      new ProtoBuf$Companion();
    }
    return ProtoBuf$Companion_instance;
  }
  ProtoBuf.prototype.dump_20fw5n$ = function (saver, obj) {
    var output = ByteArrayOutputStream_init();
    var dumper = new ProtoBuf$ProtobufWriter(this, new ProtoBuf$ProtobufEncoder(output));
    dumper.write_jsy488$(saver, obj);
    return output.toByteArray();
  };
  ProtoBuf.prototype.dump_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.dump_issdgt$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      return this.dump_20fw5n$(klassSerializer(this.context, getKClass(T_0)), obj);
    };
  }));
  ProtoBuf.prototype.dumps_issdgt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.dumps_issdgt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      return internal.HexConverter.printHexBinary_1fhb37$(this.dump_20fw5n$(klassSerializer(this.context, getKClass(T_0)), obj), true);
    };
  }));
  ProtoBuf.prototype.load_8dtdds$ = function (loader, raw) {
    var stream = ByteArrayInputStream_init(raw);
    var reader = new ProtoBuf$ProtobufReader(this, new ProtoBuf$ProtobufDecoder(stream));
    return reader.read_rf0fz3$(loader);
  };
  ProtoBuf.prototype.load_5geitx$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.load_5geitx$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, raw) {
      return this.load_8dtdds$(klassSerializer(this.context, getKClass(T_0)), raw);
    };
  }));
  ProtoBuf.prototype.loads_3zqiyt$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.protobuf.ProtoBuf.loads_3zqiyt$', wrapFunction(function () {
    var internal = _.kotlinx.serialization.internal;
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, hex) {
      var raw = internal.HexConverter.parseHexBinary_61zpoe$(hex);
      return this.load_8dtdds$(klassSerializer(this.context, getKClass(T_0)), raw);
    };
  }));
  ProtoBuf.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtoBuf',
    interfaces: []
  };
  function ProtobufDecodingException(message) {
    SerializationException.call(this, message);
    this.name = 'ProtobufDecodingException';
  }
  ProtobufDecodingException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufDecodingException',
    interfaces: [SerializationException]
  };
  function ByteBuffer(capacity) {
    ByteBuffer$Companion_getInstance();
    this.capacity = capacity;
    if (!(this.capacity >= 0)) {
      var message = 'Failed requirement.';
      throw IllegalArgumentException_init_0(message.toString());
    }
    this.dw_0 = new DataView(new ArrayBuffer(this.capacity), 0, this.capacity);
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
        throw IllegalArgumentException_init_0(message.toString());
      }
      this.limit_62obw4$_0 = value;
      if (this.position > value) {
        this.position = value;
      }
    }
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
        throw IllegalArgumentException_init_0(message.toString());
      }
      this.position_r0m5ac$_0 = newPosition;
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
    }
     else
      tmp$ = index;
    var i = tmp$;
    if (i > this.limit)
      throw IllegalArgumentException_init();
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
    }
     else {
      high = this.getInt_za3lpa$(index);
      low = this.getInt_za3lpa$(scndIdx);
    }
    return Kotlin.Long.fromInt(high).shiftLeft(32).or(Kotlin.Long.fromInt(low).and(new Kotlin.Long(-1, 0)));
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
    this.dw_0.setUint16(i, toShort(value | 0), this.order === ByteOrder$LITTLE_ENDIAN_getInstance());
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
    var low = value.and(new Kotlin.Long(-1, 0)).toInt();
    var scndIdx = index === -1 ? -1 : index + 4 | 0;
    if (this.order === ByteOrder$LITTLE_ENDIAN_getInstance()) {
      this.putInt_vux9f0$(low, index);
      this.putInt_vux9f0$(high, scndIdx);
    }
     else {
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
    }
    return ByteBuffer$Companion_instance;
  }
  ByteBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteBuffer',
    interfaces: []
  };
  function ByteBuffer_init($this) {
    $this = $this || Object.create(ByteBuffer.prototype);
    ByteBuffer.call($this, 16);
    return $this;
  }
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
    var tmp$;
    if (offset > b.length || offset < 0) {
      throw IndexOutOfBoundsException_init();
    }
    if (len < 0 || len > (b.length - offset | 0)) {
      throw IndexOutOfBoundsException_init();
    }
    tmp$ = len - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var c;
      try {
        c = this.read();
        if (c === -1) {
          return i === 0 ? -1 : i;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, IOException)) {
          if (i !== 0) {
            return i;
          }
          throw e;
        }
         else
          throw e;
      }
      b[offset + i | 0] = toByte(c);
    }
    return len;
  };
  InputStream.prototype.skip_s8cxhz$ = function (n) {
    if (n.compareTo_11rb$(Kotlin.Long.fromInt(0)) <= 0) {
      return Kotlin.Long.ZERO;
    }
    var skipped = Kotlin.Long.ZERO;
    var toRead = n.compareTo_11rb$(Kotlin.Long.fromInt(4096)) < 0 ? n.toInt() : 4096;
    var localBuf = InputStream$Companion_getInstance().skipBuf_0;
    if (localBuf == null || localBuf.length < toRead) {
      localBuf = new Int8Array(toRead);
      InputStream$Companion_getInstance().skipBuf_0 = localBuf;
    }
    while (skipped.compareTo_11rb$(n) < 0) {
      var read = this.read_mj6st8$(localBuf, 0, toRead);
      if (read === -1) {
        return skipped;
      }
      skipped = skipped.add(Kotlin.Long.fromInt(read));
      if (read < toRead) {
        return skipped;
      }
      if (n.subtract(skipped).compareTo_11rb$(Kotlin.Long.fromInt(toRead)) < 0) {
        toRead = n.subtract(skipped).toInt();
      }
    }
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
    }
    return InputStream$Companion_instance;
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
    }
     else
      tmp$_0 = -1;
    return tmp$_0;
  };
  ByteArrayInputStream.prototype.read_nzv2aj$ = function (b, offset, len) {
    if (b == null) {
      throw NullPointerException_init();
    }
    if (offset < 0 || offset > b.length || len < 0 || len > (b.length - offset | 0)) {
      throw IndexOutOfBoundsException_init();
    }
    if (this.pos_0 >= this.count_0) {
      return -1;
    }
    if (len === 0) {
      return 0;
    }
    var copylen = (this.count_0 - this.pos_0 | 0) < len ? this.count_0 - this.pos_0 | 0 : len;
    arraycopy(this.buf_0, this.pos_0, b, offset, copylen);
    this.pos_0 = this.pos_0 + copylen | 0;
    return copylen;
  };
  ByteArrayInputStream.prototype.skip_s8cxhz$ = function (n) {
    if (n.compareTo_11rb$(Kotlin.Long.fromInt(0)) <= 0) {
      return Kotlin.Long.ZERO;
    }
    var temp = this.pos_0;
    this.pos_0 = Kotlin.Long.fromInt(this.count_0 - this.pos_0 | 0).compareTo_11rb$(n) < 0 ? this.count_0 : Kotlin.Long.fromInt(this.pos_0).add(n).toInt();
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
    }
    tmp$ = offset + count - 1 | 0;
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
    }
    var newbuf = new Int8Array((this.count_0 + i | 0) * 2 | 0);
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
    }
    if (count === 0) {
      return;
    }
    this.expand_0(count);
    arraycopy(buffer, offset, this.buf_0, this.count_0, count);
    this.count_0 = this.count_0 + count | 0;
  };
  ByteArrayOutputStream.prototype.write_za3lpa$ = function (oneByte) {
    var tmp$;
    if (this.count_0 === this.buf_0.length) {
      this.expand_0(1);
    }
    this.buf_0[tmp$ = this.count_0, this.count_0 = tmp$ + 1 | 0, tmp$] = toByte(oneByte);
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
    }
     else {
      throw IllegalArgumentException_init();
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
  function Writer() {
  }
  Writer.prototype.write_za3lpa$ = function (ch) {
    this.write_8chfmy$(Kotlin.charArrayOf(toChar(ch)), 0, 1);
  };
  Writer.prototype.write_61zpoe$ = function (str) {
    this.write_8chfmy$(toCharArray(toList_0(str)), 0, str.length);
  };
  Writer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Writer',
    interfaces: []
  };
  function PrintWriter(w) {
    Writer.call(this);
    this.w = w;
  }
  PrintWriter.prototype.print_61zpoe$ = function (s) {
    this.w.write_61zpoe$(s);
  };
  PrintWriter.prototype.print_s8itvh$ = function (ch) {
    this.w.write_za3lpa$(ch | 0);
  };
  PrintWriter.prototype.print_mx4ult$ = function (value) {
    this.print_61zpoe$(value.toString());
  };
  PrintWriter.prototype.print_14dthe$ = function (value) {
    this.print_61zpoe$(value.toString());
  };
  PrintWriter.prototype.print_6taknv$ = function (value) {
    this.print_61zpoe$(value.toString());
  };
  PrintWriter.prototype.print_za3lpa$ = function (value) {
    this.print_61zpoe$(value.toString());
  };
  PrintWriter.prototype.print_s8cxhz$ = function (value) {
    this.print_61zpoe$(value.toString());
  };
  PrintWriter.prototype.print_s8jyv4$ = function (value) {
    this.print_61zpoe$(toString(value));
  };
  PrintWriter.prototype.println = function () {
    this.w.write_za3lpa$(10);
  };
  PrintWriter.prototype.println_61zpoe$ = function (s) {
    this.w.write_61zpoe$(s);
    this.println();
  };
  PrintWriter.prototype.println_s8itvh$ = function (ch) {
    this.w.write_za3lpa$(ch | 0);
    this.println();
  };
  PrintWriter.prototype.println_mx4ult$ = function (value) {
    this.println_61zpoe$(value.toString());
  };
  PrintWriter.prototype.println_14dthe$ = function (value) {
    this.println_61zpoe$(value.toString());
  };
  PrintWriter.prototype.println_6taknv$ = function (value) {
    this.println_61zpoe$(value.toString());
  };
  PrintWriter.prototype.println_za3lpa$ = function (value) {
    this.println_61zpoe$(value.toString());
  };
  PrintWriter.prototype.println_s8cxhz$ = function (value) {
    this.println_61zpoe$(value.toString());
  };
  PrintWriter.prototype.println_s8jyv4$ = function (value) {
    this.println_61zpoe$(toString(value));
  };
  PrintWriter.prototype.write_8chfmy$ = function (src, off, len) {
    this.w.write_8chfmy$(src, off, len);
  };
  PrintWriter.prototype.flush = function () {
  };
  PrintWriter.prototype.close = function () {
  };
  PrintWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrintWriter',
    interfaces: [Writer]
  };
  function StringWriter() {
    Writer.call(this);
    this.sb_0 = new StringBuilder();
  }
  StringWriter.prototype.toString = function () {
    return this.sb_0.toString();
  };
  StringWriter.prototype.write_8chfmy$ = function (src, off, len) {
    var tmp$;
    tmp$ = slice(src, until(off, off + len | 0)).iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.sb_0.append_s8itvh$(unboxChar(element));
    }
  };
  StringWriter.prototype.flush = function () {
  };
  StringWriter.prototype.close = function () {
  };
  StringWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StringWriter',
    interfaces: [Writer]
  };
  function Reader() {
  }
  Reader.prototype.read = function () {
    var a = Kotlin.charArray(1);
    this.read_8chfmy$(a, 0, 1);
    return a[0] | 0;
  };
  Reader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Reader',
    interfaces: []
  };
  function StringReader(str) {
    Reader.call(this);
    this.str = str;
    this.position_0 = 0;
  }
  StringReader.prototype.read = function () {
    var tmp$;
    if (this.position_0 === this.str.length)
      return -1;
    else {
      return this.str.charCodeAt((tmp$ = this.position_0, this.position_0 = tmp$ + 1 | 0, tmp$)) | 0;
    }
  };
  StringReader.prototype.read_8chfmy$ = function (dst, off, len) {
    var tmp$;
    var cnt = 0;
    tmp$ = off + len | 0;
    for (var i = off; i < tmp$; i++) {
      var r = this.read();
      if (r === -1)
        return cnt;
      cnt = cnt + 1 | 0;
      dst[i] = toChar(r);
    }
    return len;
  };
  StringReader.prototype.close = function () {
  };
  StringReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StringReader',
    interfaces: [Reader]
  };
  function DynamicObjectParser(context) {
    if (context === void 0)
      context = null;
    this.context = context;
  }
  DynamicObjectParser.prototype.parse_pgxeca$ = defineInlineFunction('kotlinx-serialization-runtime-js.kotlinx.serialization.DynamicObjectParser.parse_pgxeca$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var klassSerializer = _.kotlinx.serialization.klassSerializer_yop3xi$;
    return function (T_0, isT, obj) {
      return this.parse_s1q6oe$(obj, klassSerializer(this.context, getKClass(T_0)));
    };
  }));
  DynamicObjectParser.prototype.parse_s1q6oe$ = function (obj, loader) {
    return (new DynamicObjectParser$DynamicInput(this, obj)).read_rf0fz3$(loader);
  };
  function DynamicObjectParser$DynamicInput($outer, obj) {
    this.$outer = $outer;
    NamedValueInput.call(this);
    this.obj = obj;
    this.context = this.$outer.context;
    this.pos_0 = 0;
  }
  DynamicObjectParser$DynamicInput.prototype.composeName_puj7f4$ = function (parentName, childName) {
    return childName;
  };
  DynamicObjectParser$DynamicInput.prototype.readElement_f6e2p$ = function (desc) {
    var tmp$;
    while (this.pos_0 < desc.associatedFieldsCount) {
      var name = this.getTag_fr5t0y$(desc, (tmp$ = this.pos_0, this.pos_0 = tmp$ + 1 | 0, tmp$));
      var o = this.obj[name];
      if (o !== undefined)
        return this.pos_0 - 1 | 0;
    }
    return KInput$Companion_getInstance().READ_DONE;
  };
  DynamicObjectParser$DynamicInput.prototype.readTaggedEnum_bu9nms$ = function (tag, enumClass) {
    var tmp$;
    return enumFromName(enumClass, typeof (tmp$ = this.getByTag_61zpoe$(tag)) === 'string' ? tmp$ : throwCCE());
  };
  DynamicObjectParser$DynamicInput.prototype.getByTag_61zpoe$ = function (tag) {
    return this.obj[tag];
  };
  DynamicObjectParser$DynamicInput.prototype.readTaggedChar_11rb$ = function (tag) {
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
  DynamicObjectParser$DynamicInput.prototype.readTaggedValue_11rb$ = function (tag) {
    var o = this.getByTag_61zpoe$(tag);
    if (o === null || o === undefined)
      throw new MissingFieldException(tag);
    return o;
  };
  DynamicObjectParser$DynamicInput.prototype.readTaggedNotNullMark_11rb$ = function (tag) {
    var o = this.getByTag_61zpoe$(tag);
    if (o === undefined)
      throw new MissingFieldException(tag);
    return o != null;
  };
  DynamicObjectParser$DynamicInput.prototype.readBegin_276rha$ = function (desc, typeParams) {
    var tmp$, tmp$_0, tmp$_1;
    var curObj = (tmp$_0 = (tmp$ = this.currentTagOrNull) != null ? this.obj[tmp$] : null) != null ? tmp$_0 : this.obj;
    switch (desc.kind.name) {
      case 'LIST':
      case 'SET':
        tmp$_1 = new DynamicObjectParser$DynamicListInput(this.$outer, curObj);
        break;
      case 'MAP':
        tmp$_1 = new DynamicObjectParser$DynamicMapInput(this.$outer, curObj);
        break;
      case 'ENTRY':
        tmp$_1 = new DynamicObjectParser$DynamicMapValueInput(this.$outer, curObj, this.currentTag);
        break;
      default:tmp$_1 = new DynamicObjectParser$DynamicInput(this.$outer, curObj);
        break;
    }
    return tmp$_1;
  };
  DynamicObjectParser$DynamicInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicInput',
    interfaces: [NamedValueInput]
  };
  function DynamicObjectParser$DynamicMapValueInput($outer, obj, cTag) {
    this.$outer = $outer;
    DynamicObjectParser$DynamicInput.call(this, this.$outer, obj);
    this.cTag = cTag;
    this.context = this.$outer.context;
  }
  DynamicObjectParser$DynamicMapValueInput.prototype.readElement_f6e2p$ = function (desc) {
    return KInput$Companion_getInstance().READ_ALL;
  };
  DynamicObjectParser$DynamicMapValueInput.prototype.getByTag_61zpoe$ = function (tag) {
    if (equals(tag, 'key'))
      return this.cTag;
    else
      return this.obj;
  };
  DynamicObjectParser$DynamicMapValueInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicMapValueInput',
    interfaces: [DynamicObjectParser$DynamicInput]
  };
  function DynamicObjectParser$DynamicMapInput($outer, obj) {
    this.$outer = $outer;
    DynamicObjectParser$DynamicInput.call(this, this.$outer, obj);
    this.context = this.$outer.context;
    this.size_0 = 0;
    this.pos_1 = 0;
    var tmp$;
    var o = obj;
    this.size_0 = typeof (tmp$ = Object.keys(o).length) === 'number' ? tmp$ : throwCCE();
  }
  DynamicObjectParser$DynamicMapInput.prototype.elementName_xvmgof$ = function (desc, index) {
    var obj = this.obj;
    var i = index - 1 | 0;
    return Object.keys(obj)[i];
  };
  DynamicObjectParser$DynamicMapInput.prototype.readElement_f6e2p$ = function (desc) {
    var tmp$, tmp$_0;
    while (this.pos_1 < this.size_0) {
      var i = (tmp$ = this.pos_1, this.pos_1 = tmp$ + 1 | 0, tmp$);
      var obj = this.obj;
      var name = typeof (tmp$_0 = Object.keys(obj)[i]) === 'string' ? tmp$_0 : throwCCE();
      var o = obj[name];
      if (o !== undefined)
        return this.pos_1;
    }
    return KInput$Companion_getInstance().READ_DONE;
  };
  DynamicObjectParser$DynamicMapInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicMapInput',
    interfaces: [DynamicObjectParser$DynamicInput]
  };
  function DynamicObjectParser$DynamicListInput($outer, obj) {
    this.$outer = $outer;
    DynamicObjectParser$DynamicInput.call(this, this.$outer, obj);
    this.context = this.$outer.context;
    var tmp$;
    this.size_0 = typeof (tmp$ = obj.length) === 'number' ? tmp$ : throwCCE();
    this.pos_1 = 0;
  }
  DynamicObjectParser$DynamicListInput.prototype.elementName_xvmgof$ = function (desc, index) {
    return (index - 1 | 0).toString();
  };
  DynamicObjectParser$DynamicListInput.prototype.readElement_f6e2p$ = function (desc) {
    var tmp$;
    while (this.pos_1 < this.size_0) {
      var o = this.obj[tmp$ = this.pos_1, this.pos_1 = tmp$ + 1 | 0, tmp$];
      if (o !== undefined)
        return this.pos_1;
    }
    return KInput$Companion_getInstance().READ_DONE;
  };
  DynamicObjectParser$DynamicListInput.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicListInput',
    interfaces: [DynamicObjectParser$DynamicInput]
  };
  DynamicObjectParser.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DynamicObjectParser',
    interfaces: []
  };
  function serializer($receiver) {
    var tmp$, tmp$_0;
    tmp$_0 = Kotlin.isType(tmp$ = get_js($receiver).Companion.serializer(), KSerializer) ? tmp$ : null;
    if (tmp$_0 == null) {
      throw new SerializationException("Can't locate default serializer for class " + $receiver);
    }
    return tmp$_0;
  }
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  function toUtf8Bytes($receiver) {
    var tmp$;
    var s = $receiver;
    var blck = unescape(encodeURIComponent(s));
    var $receiver_0 = toList_0(typeof (tmp$ = blck) === 'string' ? tmp$ : throwCCE());
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
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
    var destination = ArrayList_init_0(bytes.length);
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
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  function toNativeArray($receiver, eClass) {
    return copyToArray($receiver);
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
  var package$serialization = package$kotlinx.serialization || (package$kotlinx.serialization = {});
  package$serialization.get_list_gekvwj$ = get_list;
  package$serialization.get_set_gekvwj$ = get_set;
  package$serialization.get_map_kgqhr1$ = get_map;
  package$serialization.SerialContext = SerialContext;
  package$serialization.klassSerializer_yop3xi$ = klassSerializer;
  package$serialization.valueSerializer_h23cll$ = valueSerializer;
  package$serialization.ContextSerializer = ContextSerializer;
  package$serialization.Serializable = Serializable;
  package$serialization.Serializer = Serializer;
  package$serialization.SerialName = SerialName;
  package$serialization.Optional = Optional;
  package$serialization.Transient = Transient;
  package$serialization.SerialInfo = SerialInfo;
  Object.defineProperty(KSerialClassKind, 'CLASS', {
    get: KSerialClassKind$CLASS_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'OBJECT', {
    get: KSerialClassKind$OBJECT_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'UNIT', {
    get: KSerialClassKind$UNIT_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'SEALED', {
    get: KSerialClassKind$SEALED_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'LIST', {
    get: KSerialClassKind$LIST_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'SET', {
    get: KSerialClassKind$SET_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'MAP', {
    get: KSerialClassKind$MAP_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'ENTRY', {
    get: KSerialClassKind$ENTRY_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'POLYMORPHIC', {
    get: KSerialClassKind$POLYMORPHIC_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'PRIMITIVE', {
    get: KSerialClassKind$PRIMITIVE_getInstance
  });
  Object.defineProperty(KSerialClassKind, 'ENUM', {
    get: KSerialClassKind$ENUM_getInstance
  });
  package$serialization.KSerialClassKind = KSerialClassKind;
  package$serialization.KSerialClassDesc = KSerialClassDesc;
  package$serialization.KSerialSaver = KSerialSaver;
  package$serialization.KSerialLoader = KSerialLoader;
  package$serialization.KSerializer = KSerializer;
  package$serialization.SerializationConstructorMarker = SerializationConstructorMarker;
  package$serialization.SerializationException = SerializationException;
  package$serialization.MissingFieldException = MissingFieldException;
  package$serialization.UnknownFieldException = UnknownFieldException;
  package$serialization.serializer_1yb8b7$ = serializer;
  package$serialization.KOutput = KOutput;
  Object.defineProperty(KInput, 'Companion', {
    get: KInput$Companion_getInstance
  });
  package$serialization.KInput = KInput;
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
  package$serialization.UpdateNotSupportedException = UpdateNotSupportedException;
  package$serialization.ElementValueOutput = ElementValueOutput;
  package$serialization.ElementValueInput = ElementValueInput;
  package$serialization.ValueTransformer = ValueTransformer;
  SerialId.Impl = SerialId$Impl;
  package$serialization.SerialId = SerialId;
  SerialTag.Impl = SerialTag$Impl;
  package$serialization.SerialTag = SerialTag;
  package$serialization.TaggedOutput = TaggedOutput;
  package$serialization.IntTaggedOutput = IntTaggedOutput;
  package$serialization.StringTaggedOutput = StringTaggedOutput;
  package$serialization.NamedValueOutput = NamedValueOutput;
  package$serialization.TaggedInput = TaggedInput;
  package$serialization.IntTaggedInput = IntTaggedInput;
  package$serialization.StringTaggedInput = StringTaggedInput;
  package$serialization.NamedValueInput = NamedValueInput;
  Mapper.prototype.OutMapper = Mapper$OutMapper;
  Mapper.prototype.OutNullableMapper = Mapper$OutNullableMapper;
  Mapper.prototype.InMapper = Mapper$InMapper;
  Mapper.prototype.InNullableMapper = Mapper$InNullableMapper;
  $$importsForInline$$['kotlinx-serialization-runtime-js'] = _;
  Object.defineProperty(package$serialization, 'Mapper', {
    get: Mapper_getInstance
  });
  CBOR.CBOREncoder = CBOR$CBOREncoder;
  CBOR.CBORDecoder = CBOR$CBORDecoder;
  Object.defineProperty(CBOR, 'Companion', {
    get: CBOR$Companion_getInstance
  });
  var package$internal = package$serialization.internal || (package$serialization.internal = {});
  var package$cbor = package$serialization.cbor || (package$serialization.cbor = {});
  package$cbor.CBOR = CBOR;
  package$cbor.CBORParsingException = CBORParsingException;
  package$internal.PrimitiveDesc = PrimitiveDesc;
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
  package$internal.EnumDesc = EnumDesc;
  package$internal.EnumSerializer = EnumSerializer;
  package$internal.makeNullable_ewacr1$ = makeNullable;
  package$internal.NullableSerializer = NullableSerializer;
  Object.defineProperty(package$internal, 'SIZE_INDEX', {
    get: function () {
      return SIZE_INDEX;
    }
  });
  package$internal.ListLikeSerializer = ListLikeSerializer;
  package$internal.MapLikeSerializer = MapLikeSerializer;
  package$internal.ReferenceArraySerializer = ReferenceArraySerializer;
  package$internal.ArrayListSerializer = ArrayListSerializer;
  package$internal.LinkedHashSetSerializer = LinkedHashSetSerializer;
  package$internal.HashSetSerializer = HashSetSerializer;
  package$internal.LinkedHashMapSerializer = LinkedHashMapSerializer;
  package$internal.HashMapSerializer = HashMapSerializer;
  Object.defineProperty(package$internal, 'KEY_INDEX', {
    get: function () {
      return KEY_INDEX;
    }
  });
  Object.defineProperty(package$internal, 'VALUE_INDEX', {
    get: function () {
      return VALUE_INDEX;
    }
  });
  package$internal.KeyValueSerializer = KeyValueSerializer;
  package$internal.MapEntryUpdatingSerializer = MapEntryUpdatingSerializer;
  package$internal.MapEntrySerializer = MapEntrySerializer;
  package$internal.PairSerializer = PairSerializer;
  package$internal.ListLikeDesc = ListLikeDesc;
  Object.defineProperty(package$internal, 'ArrayClassDesc', {
    get: ArrayClassDesc_getInstance
  });
  Object.defineProperty(package$internal, 'ArrayListClassDesc', {
    get: ArrayListClassDesc_getInstance
  });
  Object.defineProperty(package$internal, 'LinkedHashSetClassDesc', {
    get: LinkedHashSetClassDesc_getInstance
  });
  Object.defineProperty(package$internal, 'HashSetClassDesc', {
    get: HashSetClassDesc_getInstance
  });
  Object.defineProperty(package$internal, 'LinkedHashMapClassDesc', {
    get: LinkedHashMapClassDesc_getInstance
  });
  Object.defineProperty(package$internal, 'HashMapClassDesc', {
    get: HashMapClassDesc_getInstance
  });
  package$internal.MapEntry = MapEntry;
  Object.defineProperty(package$internal, 'MapEntryClassDesc', {
    get: MapEntryClassDesc_getInstance
  });
  Object.defineProperty(package$internal, 'PairClassDesc', {
    get: PairClassDesc_getInstance
  });
  Object.defineProperty(TripleSerializer, 'TripleDesc', {
    get: TripleSerializer$TripleDesc_getInstance
  });
  package$internal.TripleSerializer = TripleSerializer;
  package$internal.SerialClassDescImpl = SerialClassDescImpl;
  package$internal.onlySingleOrNull_2p1efm$ = onlySingleOrNull;
  package$internal.readExactNBytes_5u4fs$ = readExactNBytes;
  package$internal.readToByteBuffer_5u4fs$ = readToByteBuffer;
  Object.defineProperty(package$internal, 'HexConverter', {
    get: HexConverter_getInstance
  });
  package$internal.getUnsignedByte_xvhwye$ = getUnsignedByte;
  package$internal.getUnsignedShort_xvhwye$ = getUnsignedShort;
  package$internal.getUnsignedInt_xvhwye$ = getUnsignedInt;
  Object.defineProperty(JSON_0, 'Companion', {
    get: JSON$Companion_getInstance
  });
  var package$json = package$serialization.json || (package$serialization.json = {});
  package$json.JSON = JSON_0;
  Object.defineProperty(ProtoNumberType, 'DEFAULT', {
    get: ProtoNumberType$DEFAULT_getInstance
  });
  Object.defineProperty(ProtoNumberType, 'SIGNED', {
    get: ProtoNumberType$SIGNED_getInstance
  });
  Object.defineProperty(ProtoNumberType, 'FIXED', {
    get: ProtoNumberType$FIXED_getInstance
  });
  var package$protobuf = package$serialization.protobuf || (package$serialization.protobuf = {});
  package$protobuf.ProtoNumberType = ProtoNumberType;
  ProtoType.Impl = ProtoType$Impl;
  package$protobuf.ProtoType = ProtoType;
  ProtoBuf.ProtobufWriter = ProtoBuf$ProtobufWriter;
  ProtoBuf.ObjectWriter = ProtoBuf$ObjectWriter;
  ProtoBuf.MapEntryWriter = ProtoBuf$MapEntryWriter;
  ProtoBuf.RepeatedWriter = ProtoBuf$RepeatedWriter;
  ProtoBuf.ProtobufEncoder = ProtoBuf$ProtobufEncoder;
  ProtoBuf.ProtobufDecoder = ProtoBuf$ProtobufDecoder;
  Object.defineProperty(ProtoBuf, 'Varint', {
    get: ProtoBuf$Varint_getInstance
  });
  Object.defineProperty(ProtoBuf, 'Companion', {
    get: ProtoBuf$Companion_getInstance
  });
  package$protobuf.ProtoBuf = ProtoBuf;
  package$protobuf.ProtobufDecodingException = ProtobufDecodingException;
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
  package$io.Writer = Writer;
  package$io.PrintWriter = PrintWriter;
  package$io.StringWriter = StringWriter;
  package$io.Reader = Reader;
  package$io.StringReader = StringReader;
  package$serialization.DynamicObjectParser = DynamicObjectParser;
  package$serialization.toUtf8Bytes_pdl1vz$ = toUtf8Bytes;
  package$serialization.stringFromUtf8Bytes_fqrh44$ = stringFromUtf8Bytes;
  package$serialization.enumFromName_nim6t3$ = enumFromName;
  package$serialization.enumFromOrdinal_szifu5$ = enumFromOrdinal;
  package$serialization.enumClassName_49fzt8$ = enumClassName;
  package$serialization.toNativeArray_9mvb00$ = toNativeArray;
  ContextSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  PrimitiveDesc.prototype.getElementIndexOrThrow_61zpoe$ = KSerialClassDesc.prototype.getElementIndexOrThrow_61zpoe$;
  PrimitiveDesc.prototype.getAnnotationsForIndex_za3lpa$ = KSerialClassDesc.prototype.getAnnotationsForIndex_za3lpa$;
  Object.defineProperty(PrimitiveDesc.prototype, 'associatedFieldsCount', Object.getOwnPropertyDescriptor(KSerialClassDesc.prototype, 'associatedFieldsCount'));
  UnitSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  BooleanSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  ByteSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  ShortSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  IntSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  LongSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  FloatSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  DoubleSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  CharSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  StringSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  EnumDesc.prototype.getElementIndexOrThrow_61zpoe$ = KSerialClassDesc.prototype.getElementIndexOrThrow_61zpoe$;
  EnumDesc.prototype.getAnnotationsForIndex_za3lpa$ = KSerialClassDesc.prototype.getAnnotationsForIndex_za3lpa$;
  Object.defineProperty(EnumDesc.prototype, 'associatedFieldsCount', Object.getOwnPropertyDescriptor(KSerialClassDesc.prototype, 'associatedFieldsCount'));
  EnumSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  KeyValueSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  ListLikeDesc.prototype.getElementIndexOrThrow_61zpoe$ = KSerialClassDesc.prototype.getElementIndexOrThrow_61zpoe$;
  ListLikeDesc.prototype.getAnnotationsForIndex_za3lpa$ = KSerialClassDesc.prototype.getAnnotationsForIndex_za3lpa$;
  Object.defineProperty(ListLikeDesc.prototype, 'associatedFieldsCount', Object.getOwnPropertyDescriptor(KSerialClassDesc.prototype, 'associatedFieldsCount'));
  SerialClassDescImpl.prototype.getElementIndexOrThrow_61zpoe$ = KSerialClassDesc.prototype.getElementIndexOrThrow_61zpoe$;
  TripleSerializer.prototype.update_qkk2oh$ = KSerializer.prototype.update_qkk2oh$;
  SIZE_INDEX = 0;
  KEY_INDEX = 0;
  VALUE_INDEX = 1;
  Kotlin.defineModule('kotlinx-serialization-runtime-js', _);
  return _;
}));

//# sourceMappingURL=kotlinx-serialization-runtime-js.js.map
