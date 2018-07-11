(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-atomicfu'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-atomicfu'.");
    }
    root['kotlinx-atomicfu'] = factory(typeof this['kotlinx-atomicfu'] === 'undefined' ? {} : this['kotlinx-atomicfu'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var toString = Kotlin.toString;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var equals = Kotlin.equals;
  var loop = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.loop_jlk8u1$', function ($receiver, action) {
    while (true) {
      action($receiver.value);
    }
  });
  var update = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.update_xk4wt8$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_xwzc9q$(cur, upd))
        return;
    }
  });
  var getAndUpdate = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.getAndUpdate_xk4wt8$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_xwzc9q$(cur, upd))
        return cur;
    }
  });
  var updateAndGet = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.updateAndGet_xk4wt8$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_xwzc9q$(cur, upd))
        return upd;
    }
  });
  var loop_0 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.loop_737bgt$', function ($receiver, action) {
    while (true) {
      action($receiver.value);
    }
  });
  var update_0 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.update_4tf4dm$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_vux9f0$(cur, upd))
        return;
    }
  });
  var getAndUpdate_0 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.getAndUpdate_4tf4dm$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_vux9f0$(cur, upd))
        return cur;
    }
  });
  var updateAndGet_0 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.updateAndGet_4tf4dm$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_vux9f0$(cur, upd))
        return upd;
    }
  });
  var loop_1 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.loop_ian79v$', function ($receiver, action) {
    while (true) {
      action($receiver.value);
    }
  });
  var update_1 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.update_ifkm8b$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_3pjtqy$(cur, upd))
        return;
    }
  });
  var getAndUpdate_1 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.getAndUpdate_ifkm8b$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_3pjtqy$(cur, upd))
        return cur;
    }
  });
  var updateAndGet_1 = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.updateAndGet_ifkm8b$', function ($receiver, function_0) {
    while (true) {
      var cur = $receiver.value;
      var upd = function_0(cur);
      if ($receiver.compareAndSet_3pjtqy$(cur, upd))
        return upd;
    }
  });
  function atomic(initial) {
    return new AtomicRef(initial);
  }
  function atomic_0(initial) {
    return new AtomicInt(initial);
  }
  function atomic_1(initial) {
    return new AtomicLong(initial);
  }
  function AtomicRef(value) {
    this.value = value;
  }
  AtomicRef.prototype.lazySet_11rb$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicRef.lazySet_11rb$', function (value) {
    this.value = value;
  });
  AtomicRef.prototype.compareAndSet_xwzc9q$ = function (expect, update) {
    if (this.value !== expect)
      return false;
    this.value = update;
    return true;
  };
  AtomicRef.prototype.getAndSet_11rb$ = function (value) {
    var oldValue = this.value;
    this.value = value;
    return oldValue;
  };
  AtomicRef.prototype.toString = function () {
    return toString(this.value);
  };
  AtomicRef.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicRef',
    interfaces: []
  };
  function AtomicInt(value) {
    this.value = value;
  }
  AtomicInt.prototype.lazySet_za3lpa$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicInt.lazySet_za3lpa$', function (value) {
    this.value = value;
  });
  AtomicInt.prototype.compareAndSet_vux9f0$ = function (expect, update) {
    if (this.value !== expect)
      return false;
    this.value = update;
    return true;
  };
  AtomicInt.prototype.getAndSet_za3lpa$ = function (value) {
    var oldValue = this.value;
    this.value = value;
    return oldValue;
  };
  AtomicInt.prototype.getAndIncrement = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicInt.getAndIncrement', function () {
    var tmp$;
    return tmp$ = this.value, this.value = tmp$ + 1 | 0, tmp$;
  });
  AtomicInt.prototype.getAndDecrement = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicInt.getAndDecrement', function () {
    var tmp$;
    return tmp$ = this.value, this.value = tmp$ - 1 | 0, tmp$;
  });
  AtomicInt.prototype.getAndAdd_za3lpa$ = function (delta) {
    var oldValue = this.value;
    this.value = this.value + delta | 0;
    return oldValue;
  };
  AtomicInt.prototype.addAndGet_za3lpa$ = function (delta) {
    this.value = this.value + delta | 0;
    return this.value;
  };
  AtomicInt.prototype.incrementAndGet = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicInt.incrementAndGet', function () {
    return this.value = this.value + 1 | 0, this.value;
  });
  AtomicInt.prototype.decrementAndGet = function () {
    return this.value = this.value - 1 | 0, this.value;
  };
  AtomicInt.prototype.plusAssign_za3lpa$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicInt.plusAssign_za3lpa$', function (delta) {
    this.getAndAdd_za3lpa$(delta);
  });
  AtomicInt.prototype.minusAssign_za3lpa$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicInt.minusAssign_za3lpa$', function (delta) {
    this.getAndAdd_za3lpa$(-delta | 0);
  });
  AtomicInt.prototype.toString = function () {
    return this.value.toString();
  };
  AtomicInt.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicInt',
    interfaces: []
  };
  function AtomicLong(value) {
    this.value = value;
  }
  AtomicLong.prototype.lazySet_s8cxhz$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicLong.lazySet_s8cxhz$', function (value) {
    this.value = value;
  });
  AtomicLong.prototype.compareAndSet_3pjtqy$ = function (expect, update) {
    if (!equals(this.value, expect))
      return false;
    this.value = update;
    return true;
  };
  AtomicLong.prototype.getAndSet_s8cxhz$ = function (value) {
    var oldValue = this.value;
    this.value = value;
    return oldValue;
  };
  AtomicLong.prototype.getAndIncrement = function () {
    var tmp$;
    return tmp$ = this.value, this.value = tmp$.inc(), tmp$;
  };
  AtomicLong.prototype.getAndDecrement = function () {
    var tmp$;
    return tmp$ = this.value, this.value = tmp$.dec(), tmp$;
  };
  AtomicLong.prototype.getAndAdd_s8cxhz$ = function (delta) {
    var oldValue = this.value;
    this.value = this.value.add(delta);
    return oldValue;
  };
  AtomicLong.prototype.addAndGet_s8cxhz$ = function (delta) {
    this.value = this.value.add(delta);
    return this.value;
  };
  AtomicLong.prototype.incrementAndGet = function () {
    return this.value = this.value.inc(), this.value;
  };
  AtomicLong.prototype.decrementAndGet = function () {
    return this.value = this.value.dec(), this.value;
  };
  AtomicLong.prototype.plusAssign_s8cxhz$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicLong.plusAssign_s8cxhz$', function (delta) {
    this.getAndAdd_s8cxhz$(delta);
  });
  AtomicLong.prototype.minusAssign_s8cxhz$ = defineInlineFunction('kotlinx-atomicfu.kotlinx.atomicfu.AtomicLong.minusAssign_s8cxhz$', function (delta) {
    this.getAndAdd_s8cxhz$(delta.unaryMinus());
  });
  AtomicLong.prototype.toString = function () {
    return this.value.toString();
  };
  AtomicLong.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicLong',
    interfaces: []
  };
  $$importsForInline$$['kotlinx-atomicfu'] = _;
  var package$kotlinx = _.kotlinx || (_.kotlinx = {});
  var package$atomicfu = package$kotlinx.atomicfu || (package$kotlinx.atomicfu = {});
  package$atomicfu.loop_jlk8u1$ = loop;
  package$atomicfu.update_xk4wt8$ = update;
  package$atomicfu.getAndUpdate_xk4wt8$ = getAndUpdate;
  package$atomicfu.updateAndGet_xk4wt8$ = updateAndGet;
  package$atomicfu.loop_737bgt$ = loop_0;
  package$atomicfu.update_4tf4dm$ = update_0;
  package$atomicfu.getAndUpdate_4tf4dm$ = getAndUpdate_0;
  package$atomicfu.updateAndGet_4tf4dm$ = updateAndGet_0;
  package$atomicfu.loop_ian79v$ = loop_1;
  package$atomicfu.update_ifkm8b$ = update_1;
  package$atomicfu.getAndUpdate_ifkm8b$ = getAndUpdate_1;
  package$atomicfu.updateAndGet_ifkm8b$ = updateAndGet_1;
  package$atomicfu.atomic_mh5how$ = atomic;
  package$atomicfu.atomic_za3lpa$ = atomic_0;
  package$atomicfu.atomic_s8cxhz$ = atomic_1;
  package$atomicfu.AtomicRef = AtomicRef;
  package$atomicfu.AtomicInt = AtomicInt;
  package$atomicfu.AtomicLong = AtomicLong;
  Kotlin.defineModule('kotlinx-atomicfu', _);
  return _;
}));

//# sourceMappingURL=kotlinx-atomicfu.js.map
