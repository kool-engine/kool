(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlinx-serialization-kotlinx-serialization-runtime'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlinx-serialization-kotlinx-serialization-runtime'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-serialization-kotlinx-serialization-protobuf'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-serialization-kotlinx-serialization-protobuf'.");
    }if (typeof this['kotlinx-serialization-kotlinx-serialization-runtime'] === 'undefined') {
      throw new Error("Error loading module 'kotlinx-serialization-kotlinx-serialization-protobuf'. Its dependency 'kotlinx-serialization-kotlinx-serialization-runtime' was not found. Please, check whether 'kotlinx-serialization-kotlinx-serialization-runtime' is loaded prior to 'kotlinx-serialization-kotlinx-serialization-protobuf'.");
    }root['kotlinx-serialization-kotlinx-serialization-protobuf'] = factory(typeof this['kotlinx-serialization-kotlinx-serialization-protobuf'] === 'undefined' ? {} : this['kotlinx-serialization-kotlinx-serialization-protobuf'], kotlin, this['kotlinx-serialization-kotlinx-serialization-runtime']);
  }
}(this, function (_, Kotlin, $module$kotlinx_serialization_kotlinx_serialization_runtime) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var StructureKind = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.StructureKind;
  var equals = Kotlin.equals;
  var PolymorphicKind = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.PolymorphicKind;
  var SerializationException = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.SerializationException;
  var MapLikeSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.MapLikeSerializer;
  var throwCCE = Kotlin.throwCCE;
  var MapEntrySerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.builtins.MapEntrySerializer_2yqygg$;
  var SetSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.builtins.SetSerializer_swdriu$;
  var Map = Kotlin.kotlin.collections.Map;
  var ByteArraySerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.builtins.ByteArraySerializer;
  var TaggedEncoder = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.TaggedEncoder;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var ByteArrayOutputStream_init = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.io.ByteArrayOutputStream_init;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var encodeToByteArray = Kotlin.kotlin.text.encodeToByteArray_pdl1vz$;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var toByte = Kotlin.toByte;
  var toShort = Kotlin.toShort;
  var toChar = Kotlin.toChar;
  var toBoxedChar = Kotlin.toBoxedChar;
  var Any = Object;
  var TaggedDecoder = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.TaggedDecoder;
  var readExactNBytes = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.readExactNBytes_5u4fs$;
  var L0 = Kotlin.Long.ZERO;
  var decodeToString = Kotlin.kotlin.text.decodeToString_964n91$;
  var L_128 = Kotlin.Long.fromInt(-128);
  var L127 = Kotlin.Long.fromInt(127);
  var L128 = Kotlin.Long.fromInt(128);
  var L_1 = Kotlin.Long.NEG_ONE;
  var L_9223372036854775808 = Kotlin.Long.MIN_VALUE;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var ByteArrayInputStream_init = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.io.ByteArrayInputStream_init_fqrh44$;
  var BinaryFormat = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.BinaryFormat;
  var encode = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.encode_dt3ugd$;
  var decode = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.decode_cmswi7$;
  var modules = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.modules;
  var L4294967295 = new Kotlin.Long(-1, 0);
  var toRawBits = Kotlin.floatToRawBits;
  var toRawBits_0 = Kotlin.doubleToRawBits;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var mapCapacity = Kotlin.kotlin.collections.mapCapacity_za3lpa$;
  var coerceAtLeast = Kotlin.kotlin.ranges.coerceAtLeast_dqglrj$;
  var LinkedHashMap_init_0 = Kotlin.kotlin.collections.LinkedHashMap_init_bwtc7$;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var Annotation = Kotlin.kotlin.Annotation;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  ProtoBuf$ProtobufWriter.prototype = Object.create(TaggedEncoder.prototype);
  ProtoBuf$ProtobufWriter.prototype.constructor = ProtoBuf$ProtobufWriter;
  ProtoBuf$ObjectWriter.prototype = Object.create(ProtoBuf$ProtobufWriter.prototype);
  ProtoBuf$ObjectWriter.prototype.constructor = ProtoBuf$ObjectWriter;
  ProtoBuf$MapRepeatedWriter.prototype = Object.create(ProtoBuf$ObjectWriter.prototype);
  ProtoBuf$MapRepeatedWriter.prototype.constructor = ProtoBuf$MapRepeatedWriter;
  ProtoBuf$RepeatedWriter.prototype = Object.create(ProtoBuf$ProtobufWriter.prototype);
  ProtoBuf$RepeatedWriter.prototype.constructor = ProtoBuf$RepeatedWriter;
  ProtoBuf$ProtobufReader.prototype = Object.create(TaggedDecoder.prototype);
  ProtoBuf$ProtobufReader.prototype.constructor = ProtoBuf$ProtobufReader;
  ProtoBuf$RepeatedReader.prototype = Object.create(ProtoBuf$ProtobufReader.prototype);
  ProtoBuf$RepeatedReader.prototype.constructor = ProtoBuf$RepeatedReader;
  ProtoBuf$MapEntryReader.prototype = Object.create(ProtoBuf$ProtobufReader.prototype);
  ProtoBuf$MapEntryReader.prototype.constructor = ProtoBuf$MapEntryReader;
  ProtoNumberType.prototype = Object.create(Enum.prototype);
  ProtoNumberType.prototype.constructor = ProtoNumberType;
  ProtobufDecodingException.prototype = Object.create(SerializationException.prototype);
  ProtobufDecodingException.prototype.constructor = ProtobufDecodingException;
  function ProtoBuf(encodeDefaults, context) {
    ProtoBuf$Default_getInstance();
    if (encodeDefaults === void 0)
      encodeDefaults = true;
    if (context === void 0)
      context = modules.EmptyModule;
    this.encodeDefaults = encodeDefaults;
    this.context_vehvk$_0 = context;
  }
  Object.defineProperty(ProtoBuf.prototype, 'context', {
    get: function () {
      return this.context_vehvk$_0;
    }
  });
  function ProtoBuf$ProtobufWriter($outer, encoder) {
    this.$outer = $outer;
    TaggedEncoder.call(this);
    this.encoder = encoder;
  }
  Object.defineProperty(ProtoBuf$ProtobufWriter.prototype, 'context', {
    get: function () {
      return this.$outer.context;
    }
  });
  ProtoBuf$ProtobufWriter.prototype.shouldEncodeElementDefault_3zr2iy$ = function (descriptor, index) {
    return this.$outer.encodeDefaults;
  };
  ProtoBuf$ProtobufWriter.prototype.beginStructure_r0sa6z$ = function (descriptor, typeSerializers) {
    var tmp$;
    tmp$ = descriptor.kind;
    if (equals(tmp$, StructureKind.LIST))
      return new ProtoBuf$RepeatedWriter(this.$outer, this.encoder, this.currentTag);
    else if (equals(tmp$, StructureKind.CLASS) || equals(tmp$, StructureKind.OBJECT) || Kotlin.isType(tmp$, PolymorphicKind))
      return new ProtoBuf$ObjectWriter(this.$outer, this.currentTagOrNull, this.encoder);
    else if (equals(tmp$, StructureKind.MAP))
      return new ProtoBuf$MapRepeatedWriter(this.$outer, this.currentTagOrNull, this.encoder);
    else
      throw new SerializationException('Primitives are not supported at top-level');
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedInt_dpg1yx$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedByte_19qe40$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedShort_veccj0$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedLong_19wkf8$ = function (tag, value) {
    this.encoder.writeLong_scxzc4$(value, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedFloat_vlf4p8$ = function (tag, value) {
    this.encoder.writeFloat_vjorfl$(value, tag.first);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedDouble_e37ph5$ = function (tag, value) {
    this.encoder.writeDouble_12fank$(value, tag.first);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedBoolean_iuyhfk$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value ? 1 : 0, tag.first, ProtoNumberType$DEFAULT_getInstance());
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedChar_19qo1q$ = function (tag, value) {
    this.encoder.writeInt_hp6twd$(value | 0, tag.first, tag.second);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedString_l9l8mx$ = function (tag, value) {
    this.encoder.writeString_bm4lxs$(value, tag.first);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeTaggedEnum_v153v3$ = function (tag, enumDescription, ordinal) {
    this.encoder.writeInt_hp6twd$(extractParameters(enumDescription, ordinal, true).first, tag.first, ProtoNumberType$DEFAULT_getInstance());
  };
  ProtoBuf$ProtobufWriter.prototype.getTag_m47q6f$ = function ($receiver, index) {
    return ProtoBuf$Default_getInstance().getProtoDesc_0($receiver, index);
  };
  ProtoBuf$ProtobufWriter.prototype.encodeSerializableValue_tf03ej$ = function (serializer, value) {
    var tmp$, tmp$_0, tmp$_1;
    if (Kotlin.isType(serializer, MapLikeSerializer)) {
      var serializer_0 = Kotlin.isType(tmp$ = serializer, MapLikeSerializer) ? tmp$ : throwCCE();
      var mapEntrySerial = MapEntrySerializer(serializer_0.keySerializer, serializer_0.valueSerializer);
      SetSerializer(mapEntrySerial).serialize_awe97i$(this, (Kotlin.isType(tmp$_0 = value, Map) ? tmp$_0 : throwCCE()).entries);
    } else if (equals(serializer.descriptor, ByteArraySerializer().descriptor)) {
      this.encoder.writeBytes_ir89t6$(Kotlin.isByteArray(tmp$_1 = value) ? tmp$_1 : throwCCE(), this.popTag().first);
    } else
      serializer.serialize_awe97i$(this, value);
  };
  ProtoBuf$ProtobufWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufWriter',
    interfaces: [TaggedEncoder]
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
  ProtoBuf$ObjectWriter.prototype.endEncode_qatsm0$ = function (descriptor) {
    if (this.parentTag != null) {
      this.parentEncoder_0.writeBytes_ir89t6$(this.stream_0.toByteArray(), this.parentTag.first);
    } else {
      this.parentEncoder_0.out.write_fqrh44$(this.stream_0.toByteArray());
    }
  };
  ProtoBuf$ObjectWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ObjectWriter',
    interfaces: [ProtoBuf$ProtobufWriter]
  };
  function ProtoBuf$MapRepeatedWriter($outer, parentTag, parentEncoder) {
    this.$outer = $outer;
    ProtoBuf$ObjectWriter.call(this, this.$outer, parentTag, parentEncoder);
  }
  ProtoBuf$MapRepeatedWriter.prototype.getTag_m47q6f$ = function ($receiver, index) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    if (index % 2 === 0) {
      return to(1, (tmp$_0 = (tmp$ = this.parentTag) != null ? tmp$.second : null) != null ? tmp$_0 : ProtoNumberType$DEFAULT_getInstance());
    } else {
      return to(2, (tmp$_2 = (tmp$_1 = this.parentTag) != null ? tmp$_1.second : null) != null ? tmp$_2 : ProtoNumberType$DEFAULT_getInstance());
    }
  };
  ProtoBuf$MapRepeatedWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapRepeatedWriter',
    interfaces: [ProtoBuf$ObjectWriter]
  };
  function ProtoBuf$RepeatedWriter($outer, encoder, curTag) {
    this.$outer = $outer;
    ProtoBuf$ProtobufWriter.call(this, this.$outer, encoder);
    this.curTag = curTag;
  }
  ProtoBuf$RepeatedWriter.prototype.getTag_m47q6f$ = function ($receiver, index) {
    return this.curTag;
  };
  ProtoBuf$RepeatedWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RepeatedWriter',
    interfaces: [ProtoBuf$ProtobufWriter]
  };
  function ProtoBuf$ProtobufEncoder(out) {
    this.out = out;
  }
  ProtoBuf$ProtobufEncoder.prototype.writeBytes_ir89t6$ = function (bytes, tag) {
    var header = this.encode32_0(tag << 3 | 2);
    var len = this.encode32_0(bytes.length);
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(len);
    this.out.write_fqrh44$(bytes);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeInt_hp6twd$ = function (value, tag, format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? 5 : 0;
    var header = this.encode32_0(tag << 3 | wireType);
    var content = this.encode32_0(value, format);
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeLong_scxzc4$ = function (value, tag, format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? 1 : 0;
    var header = this.encode32_0(tag << 3 | wireType);
    var content = this.encode64_0(value, format);
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeString_bm4lxs$ = function (value, tag) {
    var bytes = encodeToByteArray(value);
    this.writeBytes_ir89t6$(bytes, tag);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeDouble_12fank$ = function (value, tag) {
    var header = this.encode32_0(tag << 3 | 1);
    var content = toByteArray_0(toLittleEndian_3(value));
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.writeFloat_vjorfl$ = function (value, tag) {
    var header = this.encode32_0(tag << 3 | 5);
    var content = toByteArray(toLittleEndian_2(value));
    this.out.write_fqrh44$(header);
    this.out.write_fqrh44$(content);
  };
  ProtoBuf$ProtobufEncoder.prototype.encode32_0 = function (number, format) {
    if (format === void 0)
      format = ProtoNumberType$DEFAULT_getInstance();
    switch (format.name) {
      case 'FIXED':
        return toByteArray(toLittleEndian_0(number));
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
        return toByteArray_0(toLittleEndian_1(number));
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
    TaggedDecoder.call(this);
    this.decoder = decoder;
    this.indexByTag_0 = LinkedHashMap_init();
  }
  Object.defineProperty(ProtoBuf$ProtobufReader.prototype, 'context', {
    get: function () {
      return this.$outer.context;
    }
  });
  ProtoBuf$ProtobufReader.prototype.findIndexByTag_0 = function (desc, serialId, zeroBasedDefault) {
    if (zeroBasedDefault === void 0)
      zeroBasedDefault = false;
    var tmp$;
    var $receiver = until(0, desc.elementsCount);
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$_0;
      tmp$_0 = $receiver.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        if (extractParameters(desc, element, zeroBasedDefault).first === serialId) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }}
      firstOrNull$result = null;
    }
     while (false);
    return (tmp$ = firstOrNull$result) != null ? tmp$ : -1;
  };
  ProtoBuf$ProtobufReader.prototype.beginStructure_r0sa6z$ = function (descriptor, typeParams) {
    var tmp$;
    tmp$ = descriptor.kind;
    if (equals(tmp$, StructureKind.LIST))
      return new ProtoBuf$RepeatedReader(this.$outer, this.decoder, this.currentTag);
    else if (equals(tmp$, StructureKind.CLASS) || equals(tmp$, StructureKind.OBJECT) || Kotlin.isType(tmp$, PolymorphicKind))
      return new ProtoBuf$ProtobufReader(this.$outer, ProtoBuf$Default_getInstance().makeDelimited_0(this.decoder, this.currentTagOrNull));
    else if (equals(tmp$, StructureKind.MAP))
      return new ProtoBuf$MapEntryReader(this.$outer, ProtoBuf$Default_getInstance().makeDelimited_0(this.decoder, this.currentTagOrNull), this.currentTagOrNull);
    else
      throw new SerializationException('Primitives are not supported at top-level');
  };
  ProtoBuf$ProtobufReader.prototype.endStructure_qatsm0$ = function (descriptor) {
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedBoolean_11rb$ = function (tag) {
    var i = this.decoder.nextInt_bmwen1$(ProtoNumberType$DEFAULT_getInstance());
    switch (i) {
      case 0:
        return false;
      case 1:
        return true;
      default:throw new ProtobufDecodingException('Expected boolean value (0 or 1), found ' + i);
    }
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedByte_11rb$ = function (tag) {
    return toByte(this.decoder.nextInt_bmwen1$(tag.second));
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedShort_11rb$ = function (tag) {
    return toShort(this.decoder.nextInt_bmwen1$(tag.second));
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedInt_11rb$ = function (tag) {
    return this.decoder.nextInt_bmwen1$(tag.second);
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedLong_11rb$ = function (tag) {
    return this.decoder.nextLong_bmwen1$(tag.second);
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedFloat_11rb$ = function (tag) {
    return this.decoder.nextFloat();
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedDouble_11rb$ = function (tag) {
    return this.decoder.nextDouble();
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedChar_11rb$ = function (tag) {
    return toBoxedChar(toChar(this.decoder.nextInt_bmwen1$(tag.second)));
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedString_11rb$ = function (tag) {
    return this.decoder.nextString();
  };
  ProtoBuf$ProtobufReader.prototype.decodeTaggedEnum_xicdkz$ = function (tag, enumDescription) {
    return this.findIndexByTag_0(enumDescription, this.decoder.nextInt_bmwen1$(ProtoNumberType$DEFAULT_getInstance()), true);
  };
  ProtoBuf$ProtobufReader.prototype.decodeSerializableValue_w63s0f$ = function (deserializer) {
    var tmp$, tmp$_0, tmp$_1;
    if (Kotlin.isType(deserializer, MapLikeSerializer)) {
      var serializer = Kotlin.isType(tmp$ = deserializer, MapLikeSerializer) ? tmp$ : throwCCE();
      var mapEntrySerial = MapEntrySerializer(serializer.keySerializer, serializer.valueSerializer);
      var setOfEntries = SetSerializer(mapEntrySerial).deserialize_nts5qn$(this);
      var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault(setOfEntries, 10)), 16);
      var destination = LinkedHashMap_init_0(capacity);
      var tmp$_2;
      tmp$_2 = setOfEntries.iterator();
      while (tmp$_2.hasNext()) {
        var element = tmp$_2.next();
        destination.put_xwzc9p$(element.key, element.value);
      }
      return (tmp$_0 = destination) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
    } else if (equals(deserializer.descriptor, ByteArraySerializer().descriptor))
      return (tmp$_1 = this.decoder.nextObject()) == null || Kotlin.isType(tmp$_1, Any) ? tmp$_1 : throwCCE();
    else
      return deserializer.deserialize_nts5qn$(this);
  };
  ProtoBuf$ProtobufReader.prototype.getTag_m47q6f$ = function ($receiver, index) {
    return ProtoBuf$Default_getInstance().getProtoDesc_0($receiver, index);
  };
  ProtoBuf$ProtobufReader.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    while (true) {
      if (this.decoder.curId === -1)
        return -1;
      var $receiver = this.indexByTag_0;
      var key = this.decoder.curId;
      var tmp$;
      var value = $receiver.get_11rb$(key);
      if (value == null) {
        var answer = this.findIndexByTag_0(descriptor, this.decoder.curId);
        $receiver.put_xwzc9p$(key, answer);
        tmp$ = answer;
      } else {
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
    interfaces: [TaggedDecoder]
  };
  function ProtoBuf$RepeatedReader($outer, decoder, targetTag) {
    this.$outer = $outer;
    ProtoBuf$ProtobufReader.call(this, this.$outer, decoder);
    this.targetTag = targetTag;
    this.ind_0 = -1;
  }
  ProtoBuf$RepeatedReader.prototype.decodeElementIndex_qatsm0$ = function (descriptor) {
    return this.decoder.curId === this.targetTag.first ? (this.ind_0 = this.ind_0 + 1 | 0, this.ind_0) : -1;
  };
  ProtoBuf$RepeatedReader.prototype.getTag_m47q6f$ = function ($receiver, index) {
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
  ProtoBuf$MapEntryReader.prototype.getTag_m47q6f$ = function ($receiver, index) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    if (index % 2 === 0) {
      return to(1, (tmp$_0 = (tmp$ = this.parentTag) != null ? tmp$.second : null) != null ? tmp$_0 : ProtoNumberType$DEFAULT_getInstance());
    } else {
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
    } else {
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
      default:throw new ProtobufDecodingException('Unsupported start group or end group wire type');
    }
  };
  ProtoBuf$ProtobufDecoder.prototype.assertWireType_0 = function (expected) {
    if (this.curTag_0.second !== expected)
      throw new ProtobufDecodingException('Expected wire type ' + expected + ', but found ' + this.curTag_0.second);
  };
  ProtoBuf$ProtobufDecoder.prototype.nextObject = function () {
    if (this.curTag_0.second !== 2)
      throw new ProtobufDecodingException('Expected wire type ' + 2 + ', but found ' + this.curTag_0.second);
    var len = this.decode32_0();
    if (!(len >= 0)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }var ans = readExactNBytes(this.inp, len);
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextInt_bmwen1$ = function (format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? 5 : 0;
    if (this.curTag_0.second !== wireType)
      throw new ProtobufDecodingException('Expected wire type ' + wireType + ', but found ' + this.curTag_0.second);
    var ans = this.decode32_0(format);
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextLong_bmwen1$ = function (format) {
    var wireType = format === ProtoNumberType$FIXED_getInstance() ? 1 : 0;
    if (this.curTag_0.second !== wireType)
      throw new ProtobufDecodingException('Expected wire type ' + wireType + ', but found ' + this.curTag_0.second);
    var ans = this.decode64_0(format);
    this.readTag_0();
    return ans;
  };
  ProtoBuf$ProtobufDecoder.prototype.nextFloat = function () {
    if (this.curTag_0.second !== 5)
      throw new ProtobufDecodingException('Expected wire type ' + 5 + ', but found ' + this.curTag_0.second);
    var ans = this.readIntLittleEndian_0();
    this.readTag_0();
    return Kotlin.floatFromBits(ans);
  };
  ProtoBuf$ProtobufDecoder.prototype.readIntLittleEndian_0 = function () {
    var result = 0;
    for (var i = 0; i <= 3; i++) {
      var byte = this.inp.read();
      result = result << 8 | byte;
    }
    return toLittleEndian_0(result);
  };
  ProtoBuf$ProtobufDecoder.prototype.readLongLittleEndian_0 = function () {
    var result = L0;
    for (var i = 0; i <= 7; i++) {
      var byte = this.inp.read();
      result = result.shiftLeft(8).or(Kotlin.Long.fromInt(byte));
    }
    return toLittleEndian_1(result);
  };
  ProtoBuf$ProtobufDecoder.prototype.nextDouble = function () {
    if (this.curTag_0.second !== 1)
      throw new ProtobufDecodingException('Expected wire type ' + 1 + ', but found ' + this.curTag_0.second);
    var ans = this.readLongLittleEndian_0();
    this.readTag_0();
    return Kotlin.doubleFromBits(ans);
  };
  ProtoBuf$ProtobufDecoder.prototype.nextString = function () {
    var bytes = this.nextObject();
    return decodeToString(bytes);
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
        return this.readIntLittleEndian_0();
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
        return this.readLongLittleEndian_0();
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
    while ((value & -128) !== 0) {
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
    while (!equals(value.and(L_128), L0)) {
      byteArrayList[tmp$ = i, i = tmp$ + 1 | 0, tmp$] = toByte(value.and(L127).or(L128).toInt());
      value = value.shiftRightUnsigned(7);
    }
    byteArrayList[i] = toByte(value.and(L127).toInt());
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
    var result = L0;
    var shift = 0;
    var b;
    do {
      if (shift >= bitLimit) {
        throw new ProtobufDecodingException('Varint too long: exceeded ' + bitLimit + ' bits');
      }b = inp.read();
      if (b === -1) {
        if (eofOnStartAllowed && shift === 0)
          return L_1;
        else {
          throw IllegalStateException_init('Unexpected EOF'.toString());
        }
      }result = result.or(Kotlin.Long.fromInt(b).and(L127).shiftLeft(shift));
      shift = shift + 7 | 0;
    }
     while ((b & 128) !== 0);
    return result;
  };
  ProtoBuf$Varint.prototype.decodeSignedVarintInt_wq5eom$ = function (inp) {
    var raw = this.decodeVarint_pwta7l$(inp, 32).toInt();
    var temp = (raw << 31 >> 31 ^ raw) >> 1;
    return temp ^ raw & -2147483648;
  };
  ProtoBuf$Varint.prototype.decodeSignedVarintLong_wq5eom$ = function (inp) {
    var raw = this.decodeVarint_pwta7l$(inp, 64);
    var temp = raw.shiftLeft(63).shiftRight(63).xor(raw).shiftRight(1);
    return temp.xor(raw.and(L_9223372036854775808));
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
    }return ProtoBuf$Varint_instance;
  }
  function ProtoBuf$Default() {
    ProtoBuf$Default_instance = this;
    this.$delegate_300ess$_0 = new ProtoBuf();
    this.VARINT_8be2vx$ = 0;
    this.i64_8be2vx$ = 1;
    this.SIZE_DELIMITED_8be2vx$ = 2;
    this.i32_8be2vx$ = 5;
    this.plain = new ProtoBuf();
  }
  ProtoBuf$Default.prototype.makeDelimited_0 = function (decoder, parentTag) {
    if (parentTag == null)
      return decoder;
    var bytes = decoder.nextObject();
    return new ProtoBuf$ProtobufDecoder(ByteArrayInputStream_init(bytes));
  };
  ProtoBuf$Default.prototype.getProtoDesc_0 = function ($receiver, index) {
    return extractParameters($receiver, index);
  };
  Object.defineProperty(ProtoBuf$Default.prototype, 'context', {
    get: function () {
      return this.$delegate_300ess$_0.context;
    }
  });
  ProtoBuf$Default.prototype.dump_tf03ej$ = function (serializer, value) {
    return this.$delegate_300ess$_0.dump_tf03ej$(serializer, value);
  };
  ProtoBuf$Default.prototype.load_dntfbn$ = function (deserializer, bytes) {
    return this.$delegate_300ess$_0.load_dntfbn$(deserializer, bytes);
  };
  ProtoBuf$Default.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Default',
    interfaces: [BinaryFormat]
  };
  var ProtoBuf$Default_instance = null;
  function ProtoBuf$Default_getInstance() {
    if (ProtoBuf$Default_instance === null) {
      new ProtoBuf$Default();
    }return ProtoBuf$Default_instance;
  }
  ProtoBuf.prototype.dump_tf03ej$ = function (serializer, value) {
    var encoder = ByteArrayOutputStream_init();
    var dumper = new ProtoBuf$ProtobufWriter(this, new ProtoBuf$ProtobufEncoder(encoder));
    encode(dumper, serializer, value);
    return encoder.toByteArray();
  };
  ProtoBuf.prototype.load_dntfbn$ = function (deserializer, bytes) {
    var stream = ByteArrayInputStream_init(bytes);
    var reader = new ProtoBuf$ProtobufReader(this, new ProtoBuf$ProtobufDecoder(stream));
    return decode(reader, deserializer);
  };
  ProtoBuf.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtoBuf',
    interfaces: [BinaryFormat]
  };
  function toLittleEndian($receiver) {
    return toShort(($receiver & 255) << 8 | ($receiver & 65535) >>> 8);
  }
  function toLittleEndian_0($receiver) {
    return toLittleEndian(toShort($receiver & 65535)) << 16 | toLittleEndian(toShort($receiver >>> 16)) & 65535;
  }
  function toLittleEndian_1($receiver) {
    return Kotlin.Long.fromInt(toLittleEndian_0($receiver.and(L4294967295).toInt())).shiftLeft(32).or(Kotlin.Long.fromInt(toLittleEndian_0($receiver.shiftRightUnsigned(32).toInt())).and(L4294967295));
  }
  function toLittleEndian_2($receiver) {
    return toLittleEndian_0(toRawBits($receiver));
  }
  function toLittleEndian_3($receiver) {
    return toLittleEndian_1(toRawBits_0($receiver));
  }
  function toByteArray($receiver) {
    var value = $receiver;
    var result = new Int8Array(4);
    for (var i = 3; i >= 0; i--) {
      result[3 - i | 0] = toByte(value >> (i * 8 | 0));
    }
    return result;
  }
  function toByteArray_0($receiver) {
    var value = $receiver;
    var result = new Int8Array(8);
    for (var i = 7; i >= 0; i--) {
      result[7 - i | 0] = toByte(value.shiftRight(i * 8 | 0).toInt());
    }
    return result;
  }
  function ProtoId(id) {
    this.id = id;
  }
  ProtoId.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtoId',
    interfaces: [Annotation]
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
  ProtoType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtoType',
    interfaces: [Annotation]
  };
  function extractParameters(descriptor, index, zeroBasedDefault) {
    if (zeroBasedDefault === void 0)
      zeroBasedDefault = false;
    var tmp$, tmp$_0, tmp$_1;
    var tmp$_2;
    var $receiver = descriptor.getElementAnnotations_za3lpa$(index);
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$_3;
      tmp$_3 = $receiver.iterator();
      while (tmp$_3.hasNext()) {
        var element = tmp$_3.next();
        if (Kotlin.isType(element, ProtoId)) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }}
      firstOrNull$result = null;
    }
     while (false);
    var protoId = (tmp$_2 = firstOrNull$result) == null || Kotlin.isType(tmp$_2, ProtoId) ? tmp$_2 : throwCCE();
    var idx = (tmp$ = protoId != null ? protoId.id : null) != null ? tmp$ : zeroBasedDefault ? index : index + 1 | 0;
    var tmp$_4;
    var $receiver_0 = descriptor.getElementAnnotations_za3lpa$(index);
    var firstOrNull$result_0;
    firstOrNull$break: do {
      var tmp$_5;
      tmp$_5 = $receiver_0.iterator();
      while (tmp$_5.hasNext()) {
        var element_0 = tmp$_5.next();
        if (Kotlin.isType(element_0, ProtoType)) {
          firstOrNull$result_0 = element_0;
          break firstOrNull$break;
        }}
      firstOrNull$result_0 = null;
    }
     while (false);
    var format = (tmp$_1 = (tmp$_0 = (tmp$_4 = firstOrNull$result_0) == null || Kotlin.isType(tmp$_4, ProtoType) ? tmp$_4 : throwCCE()) != null ? tmp$_0.type : null) != null ? tmp$_1 : ProtoNumberType$DEFAULT_getInstance();
    return to(idx, format);
  }
  function ProtobufDecodingException(message) {
    SerializationException.call(this, message);
    this.name = 'ProtobufDecodingException';
  }
  ProtobufDecodingException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProtobufDecodingException',
    interfaces: [SerializationException]
  };
  var findAnnotation = defineInlineFunction('kotlinx-serialization-kotlinx-serialization-protobuf.kotlinx.serialization.protobuf.findAnnotation_rdoxmg$', wrapFunction(function () {
    var throwCCE = Kotlin.throwCCE;
    return function (A_0, isA, $receiver, elementIndex) {
      var tmp$;
      var $receiver_0 = $receiver.getElementAnnotations_za3lpa$(elementIndex);
      var firstOrNull$result;
      firstOrNull$break: do {
        var tmp$_0;
        tmp$_0 = $receiver_0.iterator();
        while (tmp$_0.hasNext()) {
          var element = tmp$_0.next();
          if (isA(element)) {
            firstOrNull$result = element;
            break firstOrNull$break;
          }}
        firstOrNull$result = null;
      }
       while (false);
      return (tmp$ = firstOrNull$result) == null || isA(tmp$) ? tmp$ : throwCCE();
    };
  }));
  ProtoBuf.ProtobufWriter = ProtoBuf$ProtobufWriter;
  ProtoBuf.ObjectWriter = ProtoBuf$ObjectWriter;
  ProtoBuf.MapRepeatedWriter = ProtoBuf$MapRepeatedWriter;
  ProtoBuf.RepeatedWriter = ProtoBuf$RepeatedWriter;
  ProtoBuf.ProtobufEncoder = ProtoBuf$ProtobufEncoder;
  $$importsForInline$$['kotlinx-serialization-kotlinx-serialization-protobuf'] = _;
  ProtoBuf.ProtobufDecoder = ProtoBuf$ProtobufDecoder;
  Object.defineProperty(ProtoBuf, 'Varint', {
    get: ProtoBuf$Varint_getInstance
  });
  Object.defineProperty(ProtoBuf, 'Default', {
    get: ProtoBuf$Default_getInstance
  });
  var package$kotlinx = _.kotlinx || (_.kotlinx = {});
  var package$serialization = package$kotlinx.serialization || (package$kotlinx.serialization = {});
  var package$protobuf = package$serialization.protobuf || (package$serialization.protobuf = {});
  package$protobuf.ProtoBuf = ProtoBuf;
  package$protobuf.ProtoId = ProtoId;
  Object.defineProperty(ProtoNumberType, 'DEFAULT', {
    get: ProtoNumberType$DEFAULT_getInstance
  });
  Object.defineProperty(ProtoNumberType, 'SIGNED', {
    get: ProtoNumberType$SIGNED_getInstance
  });
  Object.defineProperty(ProtoNumberType, 'FIXED', {
    get: ProtoNumberType$FIXED_getInstance
  });
  package$protobuf.ProtoNumberType = ProtoNumberType;
  package$protobuf.ProtoType = ProtoType;
  package$protobuf.extractParameters_dkiwnm$ = extractParameters;
  package$protobuf.ProtobufDecodingException = ProtobufDecodingException;
  Kotlin.defineModule('kotlinx-serialization-kotlinx-serialization-protobuf', _);
  return _;
}));

//# sourceMappingURL=kotlinx-serialization-kotlinx-serialization-protobuf.js.map
