(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlinx-atomicfu'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlinx-atomicfu'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-coroutines-core'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-coroutines-core'.");
    }
    if (typeof this['kotlinx-atomicfu'] === 'undefined') {
      throw new Error("Error loading module 'kotlinx-coroutines-core'. Its dependency 'kotlinx-atomicfu' was not found. Please, check whether 'kotlinx-atomicfu' is loaded prior to 'kotlinx-coroutines-core'.");
    }
    root['kotlinx-coroutines-core'] = factory(typeof this['kotlinx-coroutines-core'] === 'undefined' ? {} : this['kotlinx-coroutines-core'], kotlin, this['kotlinx-atomicfu']);
  }
}(this, function (_, Kotlin, $module$kotlinx_atomicfu) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Unit = Kotlin.kotlin.Unit;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED;
  var toString = Kotlin.toString;
  var wrapFunction = Kotlin.wrapFunction;
  var Throwable = Error;
  var atomic = $module$kotlinx_atomicfu.kotlinx.atomicfu.atomic_za3lpa$;
  var atomic_0 = $module$kotlinx_atomicfu.kotlinx.atomicfu.atomic_mh5how$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Continuation = Kotlin.kotlin.coroutines.experimental.Continuation;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Any = Object;
  var throwCCE = Kotlin.throwCCE;
  var CoroutineImpl = Kotlin.kotlin.coroutines.experimental.CoroutineImpl;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var throwUPAE = Kotlin.throwUPAE;
  var CoroutineContext$Element = Kotlin.kotlin.coroutines.experimental.CoroutineContext.Element;
  var ContinuationInterceptor = Kotlin.kotlin.coroutines.experimental.ContinuationInterceptor;
  var equals = Kotlin.equals;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var AbstractCoroutineContextElement = Kotlin.kotlin.coroutines.experimental.AbstractCoroutineContextElement;
  var RuntimeException = Kotlin.kotlin.RuntimeException;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var CoroutineContext$Key = Kotlin.kotlin.coroutines.experimental.CoroutineContext.Key;
  var startCoroutine = Kotlin.kotlin.coroutines.experimental.startCoroutine_xtwlez$;
  var startCoroutine_0 = Kotlin.kotlin.coroutines.experimental.startCoroutine_uao1qo$;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var buildSequence = Kotlin.kotlin.coroutines.experimental.buildSequence_of7nec$;
  var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var emptySequence = Kotlin.kotlin.sequences.emptySequence_287e2$;
  var L0 = Kotlin.Long.ZERO;
  var UnsupportedOperationException_init_0 = Kotlin.kotlin.UnsupportedOperationException_init;
  var ensureNotNull = Kotlin.ensureNotNull;
  var SuspendFunction1 = Function;
  var coerceAtMost = Kotlin.kotlin.ranges.coerceAtMost_2p08ub$;
  var Long$Companion$MAX_VALUE = Kotlin.Long.MAX_VALUE;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var NoSuchElementException = Kotlin.kotlin.NoSuchElementException;
  var IndexedValue = Kotlin.kotlin.collections.IndexedValue;
  var IndexOutOfBoundsException = Kotlin.kotlin.IndexOutOfBoundsException;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var HashSet_init = Kotlin.kotlin.collections.HashSet_init_287e2$;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var indexOf = Kotlin.kotlin.collections.indexOf_mjy6jw$;
  var createCoroutineUnchecked = Kotlin.kotlin.coroutines.experimental.intrinsics.createCoroutineUnchecked_xtwlez$;
  var createCoroutineUnchecked_0 = Kotlin.kotlin.coroutines.experimental.intrinsics.createCoroutineUnchecked_uao1qo$;
  var shuffle = Kotlin.kotlin.collections.shuffle_vvxzk3$;
  var Annotation = Kotlin.kotlin.Annotation;
  var RuntimeException_init = Kotlin.kotlin.RuntimeException_init_pdl1vj$;
  var IllegalStateException = Kotlin.kotlin.IllegalStateException;
  var hashCode = Kotlin.hashCode;
  var L2147483647 = Kotlin.Long.fromInt(2147483647);
  var coerceIn = Kotlin.kotlin.ranges.coerceIn_ekzx8g$;
  var L9223372036854775 = new Kotlin.Long(-1511828489, 2147483);
  var Long$Companion$MIN_VALUE = Kotlin.Long.MIN_VALUE;
  var L_9223372036854775 = new Kotlin.Long(1511828489, -2147484);
  var L1000 = Kotlin.Long.fromInt(1000);
  CancelHandler.prototype = Object.create(CancelHandlerBase.prototype);
  CancelHandler.prototype.constructor = CancelHandler;
  InvokeOnCancel.prototype = Object.create(CancelHandler.prototype);
  InvokeOnCancel.prototype.constructor = InvokeOnCancel;
  AbstractCoroutine.prototype = Object.create(JobSupport.prototype);
  AbstractCoroutine.prototype.constructor = AbstractCoroutine;
  AwaitAll$DisposeHandlersOnCancel.prototype = Object.create(CancelHandler.prototype);
  AwaitAll$DisposeHandlersOnCancel.prototype.constructor = AwaitAll$DisposeHandlersOnCancel;
  CompletionHandlerBase.prototype = Object.create(LinkedListNode.prototype);
  CompletionHandlerBase.prototype.constructor = CompletionHandlerBase;
  JobNode.prototype = Object.create(CompletionHandlerBase.prototype);
  JobNode.prototype.constructor = JobNode;
  AwaitAll$AwaitAllNode.prototype = Object.create(JobNode.prototype);
  AwaitAll$AwaitAllNode.prototype.constructor = AwaitAll$AwaitAllNode;
  StandaloneCoroutine.prototype = Object.create(AbstractCoroutine.prototype);
  StandaloneCoroutine.prototype.constructor = StandaloneCoroutine;
  LazyStandaloneCoroutine.prototype = Object.create(StandaloneCoroutine.prototype);
  LazyStandaloneCoroutine.prototype.constructor = LazyStandaloneCoroutine;
  RunCompletion.prototype = Object.create(AbstractContinuation.prototype);
  RunCompletion.prototype.constructor = RunCompletion;
  RemoveOnCancel.prototype = Object.create(CancelHandler.prototype);
  RemoveOnCancel.prototype.constructor = RemoveOnCancel;
  DisposeOnCancel.prototype = Object.create(CancelHandler.prototype);
  DisposeOnCancel.prototype.constructor = DisposeOnCancel;
  CancellableContinuationImpl.prototype = Object.create(AbstractContinuation.prototype);
  CancellableContinuationImpl.prototype.constructor = CancellableContinuationImpl;
  CompletableDeferredImpl.prototype = Object.create(JobSupport.prototype);
  CompletableDeferredImpl.prototype.constructor = CompletableDeferredImpl;
  Cancelled.prototype = Object.create(CompletedExceptionally.prototype);
  Cancelled.prototype.constructor = Cancelled;
  CancelledContinuation.prototype = Object.create(CompletedExceptionally.prototype);
  CancelledContinuation.prototype.constructor = CancelledContinuation;
  CoroutineDispatcher.prototype = Object.create(AbstractCoroutineContextElement.prototype);
  CoroutineDispatcher.prototype.constructor = CoroutineDispatcher;
  CoroutineStart.prototype = Object.create(Enum.prototype);
  CoroutineStart.prototype.constructor = CoroutineStart;
  DeferredCoroutine.prototype = Object.create(AbstractCoroutine.prototype);
  DeferredCoroutine.prototype.constructor = DeferredCoroutine;
  LazyDeferredCoroutine.prototype = Object.create(DeferredCoroutine.prototype);
  LazyDeferredCoroutine.prototype.constructor = LazyDeferredCoroutine;
  JobImpl.prototype = Object.create(JobSupport.prototype);
  JobImpl.prototype.constructor = JobImpl;
  LinkedListHead.prototype = Object.create(LinkedListNode.prototype);
  LinkedListHead.prototype.constructor = LinkedListHead;
  NodeList.prototype = Object.create(LinkedListHead.prototype);
  NodeList.prototype.constructor = NodeList;
  InvokeOnCompletion.prototype = Object.create(JobNode.prototype);
  InvokeOnCompletion.prototype.constructor = InvokeOnCompletion;
  ResumeOnCompletion.prototype = Object.create(JobNode.prototype);
  ResumeOnCompletion.prototype.constructor = ResumeOnCompletion;
  DisposeOnCompletion.prototype = Object.create(JobNode.prototype);
  DisposeOnCompletion.prototype.constructor = DisposeOnCompletion;
  SelectJoinOnCompletion.prototype = Object.create(JobNode.prototype);
  SelectJoinOnCompletion.prototype.constructor = SelectJoinOnCompletion;
  SelectAwaitOnCompletion.prototype = Object.create(JobNode.prototype);
  SelectAwaitOnCompletion.prototype.constructor = SelectAwaitOnCompletion;
  JobCancellationNode.prototype = Object.create(JobNode.prototype);
  JobCancellationNode.prototype.constructor = JobCancellationNode;
  InvokeOnCancellation.prototype = Object.create(JobCancellationNode.prototype);
  InvokeOnCancellation.prototype.constructor = InvokeOnCancellation;
  ChildJob.prototype = Object.create(JobCancellationNode.prototype);
  ChildJob.prototype.constructor = ChildJob;
  ChildContinuation.prototype = Object.create(JobCancellationNode.prototype);
  ChildContinuation.prototype.constructor = ChildContinuation;
  ChildCompletion.prototype = Object.create(JobNode.prototype);
  ChildCompletion.prototype.constructor = ChildCompletion;
  NonCancellable.prototype = Object.create(AbstractCoroutineContextElement.prototype);
  NonCancellable.prototype.constructor = NonCancellable;
  TimeoutCoroutine.prototype = Object.create(AbstractCoroutine.prototype);
  TimeoutCoroutine.prototype.constructor = TimeoutCoroutine;
  TimeoutOrNullCoroutine.prototype = Object.create(TimeoutCoroutine.prototype);
  TimeoutOrNullCoroutine.prototype.constructor = TimeoutOrNullCoroutine;
  CancellationException.prototype = Object.create(IllegalStateException.prototype);
  CancellationException.prototype.constructor = CancellationException;
  TimeoutCancellationException.prototype = Object.create(CancellationException.prototype);
  TimeoutCancellationException.prototype.constructor = TimeoutCancellationException;
  Unconfined.prototype = Object.create(CoroutineDispatcher.prototype);
  Unconfined.prototype.constructor = Unconfined;
  AbstractAtomicDesc.prototype = Object.create(AtomicDesc.prototype);
  AbstractAtomicDesc.prototype.constructor = AbstractAtomicDesc;
  AddLastDesc.prototype = Object.create(AbstractAtomicDesc.prototype);
  AddLastDesc.prototype.constructor = AddLastDesc;
  AbstractSendChannel$SendBufferedDesc.prototype = Object.create(AddLastDesc.prototype);
  AbstractSendChannel$SendBufferedDesc.prototype.constructor = AbstractSendChannel$SendBufferedDesc;
  AbstractSendChannel$SendConflatedDesc.prototype = Object.create(AbstractSendChannel$SendBufferedDesc.prototype);
  AbstractSendChannel$SendConflatedDesc.prototype.constructor = AbstractSendChannel$SendConflatedDesc;
  RemoveFirstDesc.prototype = Object.create(AbstractAtomicDesc.prototype);
  RemoveFirstDesc.prototype.constructor = RemoveFirstDesc;
  AbstractSendChannel$TryOfferDesc.prototype = Object.create(RemoveFirstDesc.prototype);
  AbstractSendChannel$TryOfferDesc.prototype.constructor = AbstractSendChannel$TryOfferDesc;
  AbstractSendChannel$TryEnqueueSendDesc.prototype = Object.create(AddLastDesc.prototype);
  AbstractSendChannel$TryEnqueueSendDesc.prototype.constructor = AbstractSendChannel$TryEnqueueSendDesc;
  AbstractSendChannel$SendSelect.prototype = Object.create(LinkedListNode.prototype);
  AbstractSendChannel$SendSelect.prototype.constructor = AbstractSendChannel$SendSelect;
  AbstractSendChannel$SendBuffered.prototype = Object.create(LinkedListNode.prototype);
  AbstractSendChannel$SendBuffered.prototype.constructor = AbstractSendChannel$SendBuffered;
  AbstractChannel$TryPollDesc.prototype = Object.create(RemoveFirstDesc.prototype);
  AbstractChannel$TryPollDesc.prototype.constructor = AbstractChannel$TryPollDesc;
  AbstractChannel$TryEnqueueReceiveDesc.prototype = Object.create(AddLastDesc.prototype);
  AbstractChannel$TryEnqueueReceiveDesc.prototype.constructor = AbstractChannel$TryEnqueueReceiveDesc;
  AbstractChannel$RemoveReceiveOnCancel.prototype = Object.create(CancelHandler.prototype);
  AbstractChannel$RemoveReceiveOnCancel.prototype.constructor = AbstractChannel$RemoveReceiveOnCancel;
  Receive.prototype = Object.create(LinkedListNode.prototype);
  Receive.prototype.constructor = Receive;
  AbstractChannel$ReceiveElement.prototype = Object.create(Receive.prototype);
  AbstractChannel$ReceiveElement.prototype.constructor = AbstractChannel$ReceiveElement;
  AbstractChannel$ReceiveHasNext.prototype = Object.create(Receive.prototype);
  AbstractChannel$ReceiveHasNext.prototype.constructor = AbstractChannel$ReceiveHasNext;
  AbstractChannel$ReceiveSelect.prototype = Object.create(Receive.prototype);
  AbstractChannel$ReceiveSelect.prototype.constructor = AbstractChannel$ReceiveSelect;
  AbstractChannel.prototype = Object.create(AbstractSendChannel.prototype);
  AbstractChannel.prototype.constructor = AbstractChannel;
  SendElement.prototype = Object.create(LinkedListNode.prototype);
  SendElement.prototype.constructor = SendElement;
  Closed.prototype = Object.create(LinkedListNode.prototype);
  Closed.prototype.constructor = Closed;
  ArrayBroadcastChannel$Subscriber.prototype = Object.create(AbstractChannel.prototype);
  ArrayBroadcastChannel$Subscriber.prototype.constructor = ArrayBroadcastChannel$Subscriber;
  ArrayBroadcastChannel.prototype = Object.create(AbstractSendChannel.prototype);
  ArrayBroadcastChannel.prototype.constructor = ArrayBroadcastChannel;
  ArrayChannel.prototype = Object.create(AbstractChannel.prototype);
  ArrayChannel.prototype.constructor = ArrayChannel;
  BroadcastCoroutine.prototype = Object.create(AbstractCoroutine.prototype);
  BroadcastCoroutine.prototype.constructor = BroadcastCoroutine;
  LazyBroadcastCoroutine.prototype = Object.create(BroadcastCoroutine.prototype);
  LazyBroadcastCoroutine.prototype.constructor = LazyBroadcastCoroutine;
  ClosedSendChannelException.prototype = Object.create(CancellationException.prototype);
  ClosedSendChannelException.prototype.constructor = ClosedSendChannelException;
  ClosedReceiveChannelException.prototype = Object.create(NoSuchElementException.prototype);
  ClosedReceiveChannelException.prototype.constructor = ClosedReceiveChannelException;
  ChannelCoroutine.prototype = Object.create(AbstractCoroutine.prototype);
  ChannelCoroutine.prototype.constructor = ChannelCoroutine;
  ConflatedChannel.prototype = Object.create(AbstractChannel.prototype);
  ConflatedChannel.prototype.constructor = ConflatedChannel;
  ConflatedBroadcastChannel$Subscriber.prototype = Object.create(ConflatedChannel.prototype);
  ConflatedBroadcastChannel$Subscriber.prototype.constructor = ConflatedBroadcastChannel$Subscriber;
  LinkedListChannel.prototype = Object.create(AbstractChannel.prototype);
  LinkedListChannel.prototype.constructor = LinkedListChannel;
  ProducerCoroutine.prototype = Object.create(ChannelCoroutine.prototype);
  ProducerCoroutine.prototype.constructor = ProducerCoroutine;
  RendezvousChannel.prototype = Object.create(AbstractChannel.prototype);
  RendezvousChannel.prototype.constructor = RendezvousChannel;
  AtomicOp.prototype = Object.create(OpDescriptor.prototype);
  AtomicOp.prototype.constructor = AtomicOp;
  SelectBuilderImpl$SelectOnCancellation.prototype = Object.create(JobCancellationNode.prototype);
  SelectBuilderImpl$SelectOnCancellation.prototype.constructor = SelectBuilderImpl$SelectOnCancellation;
  SelectBuilderImpl$AtomicSelectOp.prototype = Object.create(AtomicOp.prototype);
  SelectBuilderImpl$AtomicSelectOp.prototype.constructor = SelectBuilderImpl$AtomicSelectOp;
  SelectBuilderImpl$DisposeNode.prototype = Object.create(LinkedListNode.prototype);
  SelectBuilderImpl$DisposeNode.prototype.constructor = SelectBuilderImpl$DisposeNode;
  SelectBuilderImpl.prototype = Object.create(LinkedListHead.prototype);
  SelectBuilderImpl.prototype.constructor = SelectBuilderImpl;
  MutexImpl$TryLockDesc$PrepareOp.prototype = Object.create(OpDescriptor.prototype);
  MutexImpl$TryLockDesc$PrepareOp.prototype.constructor = MutexImpl$TryLockDesc$PrepareOp;
  MutexImpl$TryLockDesc.prototype = Object.create(AtomicDesc.prototype);
  MutexImpl$TryLockDesc.prototype.constructor = MutexImpl$TryLockDesc;
  MutexImpl$TryEnqueueLockDesc.prototype = Object.create(AddLastDesc.prototype);
  MutexImpl$TryEnqueueLockDesc.prototype.constructor = MutexImpl$TryEnqueueLockDesc;
  MutexImpl$LockedQueue.prototype = Object.create(LinkedListHead.prototype);
  MutexImpl$LockedQueue.prototype.constructor = MutexImpl$LockedQueue;
  MutexImpl$LockWaiter.prototype = Object.create(LinkedListNode.prototype);
  MutexImpl$LockWaiter.prototype.constructor = MutexImpl$LockWaiter;
  MutexImpl$LockCont.prototype = Object.create(MutexImpl$LockWaiter.prototype);
  MutexImpl$LockCont.prototype.constructor = MutexImpl$LockCont;
  MutexImpl$LockSelect.prototype = Object.create(MutexImpl$LockWaiter.prototype);
  MutexImpl$LockSelect.prototype.constructor = MutexImpl$LockSelect;
  MutexImpl$UnlockOp.prototype = Object.create(OpDescriptor.prototype);
  MutexImpl$UnlockOp.prototype.constructor = MutexImpl$UnlockOp;
  CompletionHandlerException.prototype = Object.create(RuntimeException.prototype);
  CompletionHandlerException.prototype.constructor = CompletionHandlerException;
  JobCancellationException.prototype = Object.create(CancellationException.prototype);
  JobCancellationException.prototype.constructor = JobCancellationException;
  DispatchException.prototype = Object.create(RuntimeException.prototype);
  DispatchException.prototype.constructor = DispatchException;
  NodeDispatcher$ClearTimeout.prototype = Object.create(CancelHandler.prototype);
  NodeDispatcher$ClearTimeout.prototype.constructor = NodeDispatcher$ClearTimeout;
  NodeDispatcher.prototype = Object.create(CoroutineDispatcher.prototype);
  NodeDispatcher.prototype.constructor = NodeDispatcher;
  MessageQueue.prototype = Object.create(Queue.prototype);
  MessageQueue.prototype.constructor = MessageQueue;
  WindowDispatcher$queue$ObjectLiteral.prototype = Object.create(MessageQueue.prototype);
  WindowDispatcher$queue$ObjectLiteral.prototype.constructor = WindowDispatcher$queue$ObjectLiteral;
  WindowDispatcher.prototype = Object.create(CoroutineDispatcher.prototype);
  WindowDispatcher.prototype.constructor = WindowDispatcher;
  TimeUnit.prototype = Object.create(Enum.prototype);
  TimeUnit.prototype.constructor = TimeUnit;
  var UNDECIDED;
  var SUSPENDED;
  var RESUMED;
  function AbstractContinuation(delegate, resumeMode) {
    this.delegate_8vztre$_0 = delegate;
    this.resumeMode_enh867$_0 = resumeMode;
    this._decision_0 = atomic(0);
    this._state_0 = atomic_0(ACTIVE);
    this.parentHandle_0 = null;
  }
  Object.defineProperty(AbstractContinuation.prototype, 'delegate', {
    get: function () {
      return this.delegate_8vztre$_0;
    }
  });
  Object.defineProperty(AbstractContinuation.prototype, 'resumeMode', {
    get: function () {
      return this.resumeMode_enh867$_0;
    }
  });
  Object.defineProperty(AbstractContinuation.prototype, 'state_8be2vx$', {
    get: function () {
      return this._state_0.value;
    }
  });
  Object.defineProperty(AbstractContinuation.prototype, 'isActive', {
    get: function () {
      return Kotlin.isType(this.state_8be2vx$, NotCompleted);
    }
  });
  Object.defineProperty(AbstractContinuation.prototype, 'isCompleted', {
    get: function () {
      return !Kotlin.isType(this.state_8be2vx$, NotCompleted);
    }
  });
  Object.defineProperty(AbstractContinuation.prototype, 'isCancelled', {
    get: function () {
      return Kotlin.isType(this.state_8be2vx$, CancelledContinuation);
    }
  });
  Object.defineProperty(AbstractContinuation.prototype, 'useCancellingState', {
    get: function () {
      return false;
    }
  });
  AbstractContinuation.prototype.initParentJobInternal_x4lgmv$ = function (parent) {
    if (!(this.parentHandle_0 == null)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    if (parent == null) {
      this.parentHandle_0 = NonDisposableHandle_getInstance();
      return;
    }
    parent.start();
    var handle = parent.invokeOnCompletion_ct2b2z$(true, void 0, new ChildContinuation(parent, this));
    this.parentHandle_0 = handle;
    if (this.isCompleted) {
      handle.dispose();
      this.parentHandle_0 = NonDisposableHandle_getInstance();
    }
  };
  AbstractContinuation.prototype.takeState = function () {
    return this.state_8be2vx$;
  };
  AbstractContinuation.prototype.cancel_dbl4no$ = function (cause) {
    while (true) {
      var state = this.state_8be2vx$;
      if (!Kotlin.isType(state, NotCompleted))
        return false;
      if (Kotlin.isType(state, Cancelling))
        return false;
      if (this.tryCancel_0(state, cause))
        return true;
    }
  };
  AbstractContinuation.prototype.trySuspend_0 = function () {
    var $receiver = this._decision_0;
    while (true) {
      switch ($receiver.value) {
        case 0:
          if (this._decision_0.compareAndSet_vux9f0$(0, 1))
            return true;
          break;
        case 2:
          return false;
        default:throw IllegalStateException_init('Already suspended'.toString());
      }
    }
  };
  AbstractContinuation.prototype.tryResume_0 = function () {
    var $receiver = this._decision_0;
    while (true) {
      switch ($receiver.value) {
        case 0:
          if (this._decision_0.compareAndSet_vux9f0$(0, 2))
            return true;
          break;
        case 1:
          return false;
        default:throw IllegalStateException_init('Already resumed'.toString());
      }
    }
  };
  AbstractContinuation.prototype.getResult = function () {
    if (this.trySuspend_0())
      return COROUTINE_SUSPENDED;
    var state = this.state_8be2vx$;
    if (Kotlin.isType(state, CompletedExceptionally))
      throw state.cause;
    return this.getSuccessfulResult_tpy1pm$(state);
  };
  AbstractContinuation.prototype.resume_11rb$ = function (value) {
    this.resumeImpl_0(value, this.resumeMode);
  };
  AbstractContinuation.prototype.resumeWithException_tcv7n7$ = function (exception) {
    this.resumeImpl_0(new CompletedExceptionally(exception), this.resumeMode);
  };
  AbstractContinuation.prototype.invokeOnCancellation_f05bi3$ = function (handler) {
    var handleCache = {v: null};
    while (true) {
      var state = this.state_8be2vx$;
      var tmp$, tmp$_0, tmp$_1;
      if (Kotlin.isType(state, Active)) {
        var tmp$_2;
        if ((tmp$ = handleCache.v) != null)
          tmp$_2 = tmp$;
        else {
          var $receiver = this.makeHandler_0(handler);
          handleCache.v = $receiver;
          tmp$_2 = $receiver;
        }
        var node = tmp$_2;
        if (this._state_0.compareAndSet_xwzc9q$(state, node)) {
          return;
        }
      }
       else if (Kotlin.isType(state, CancelHandler)) {
        throw IllegalStateException_init(("It's prohibited to register multiple handlers, tried to register " + handler + ', already has ' + toString(state)).toString());
      }
       else if (Kotlin.isType(state, CancelledContinuation)) {
        invokeIt(handler, (tmp$_1 = Kotlin.isType(tmp$_0 = state, CompletedExceptionally) ? tmp$_0 : null) != null ? tmp$_1.cause : null);
        return;
      }
       else if (Kotlin.isType(state, Cancelling)) {
        throw IllegalStateException_init("Cancellation handlers for continuations with 'Cancelling' state are not supported".toString());
      }
       else
        return;
    }
  };
  AbstractContinuation.prototype.makeHandler_0 = function (handler) {
    return Kotlin.isType(handler, CancelHandler) ? handler : new InvokeOnCancel(handler);
  };
  AbstractContinuation.prototype.tryCancel_0 = function (state, cause) {
    if (this.useCancellingState) {
      if (!!Kotlin.isType(state, CancelHandler)) {
        var message = "Invariant: 'Cancelling' state and cancellation handlers cannot be used together";
        throw IllegalArgumentException_init(message.toString());
      }
      return this._state_0.compareAndSet_xwzc9q$(state, new Cancelling(new CancelledContinuation(this, cause)));
    }
    return this.updateStateToFinal_0(state, new CancelledContinuation(this, cause), 0);
  };
  AbstractContinuation.prototype.onCompletionInternal_0 = function (mode) {
    if (this.tryResume_0())
      return;
    dispatch(this, mode);
  };
  AbstractContinuation.prototype.loopOnState_0 = function (block) {
    while (true) {
      block(this.state_8be2vx$);
    }
  };
  AbstractContinuation.prototype.resumeImpl_0 = function (proposedUpdate, resumeMode) {
    while (true) {
      var state = this.state_8be2vx$;
      if (Kotlin.isType(state, Cancelling))
        if (!Kotlin.isType(proposedUpdate, CompletedExceptionally)) {
          var update = state.cancel;
          if (this.updateStateToFinal_0(state, update, resumeMode))
            return;
        }
         else {
          var update_0;
          if (Kotlin.isType(proposedUpdate.cause, CancellationException)) {
            update_0 = proposedUpdate;
            this.coerceWithException_0(state, update_0);
          }
           else {
            var exception = proposedUpdate.cause;
            var currentException = state.cancel.cause;
            !Kotlin.isType(currentException, CancellationException) || currentException.cause !== exception;
            update_0 = new CompletedExceptionally(exception);
          }
          if (this.updateStateToFinal_0(state, update_0, resumeMode)) {
            return;
          }
        }
       else if (Kotlin.isType(state, NotCompleted)) {
        if (this.updateStateToFinal_0(state, proposedUpdate, resumeMode))
          return;
      }
       else if (Kotlin.isType(state, CancelledContinuation)) {
        if (Kotlin.isType(proposedUpdate, NotCompleted) || Kotlin.isType(proposedUpdate, CompletedExceptionally)) {
          throw IllegalStateException_init(('Unexpected update, state: ' + toString(state) + ', update: ' + toString(proposedUpdate)).toString());
        }
        return;
      }
       else {
        throw IllegalStateException_init(('Already resumed, but proposed with update ' + toString(proposedUpdate)).toString());
      }
    }
  };
  AbstractContinuation.prototype.coerceWithException_0 = function (state, proposedUpdate) {
    var originalCancellation = state.cancel;
    var originalException = originalCancellation.cause;
    var updateCause = proposedUpdate.cause;
    var isSameCancellation = Kotlin.isType(originalCancellation.cause, CancellationException) && originalException.cause === updateCause.cause;
    !isSameCancellation && originalException.cause !== updateCause;
  };
  AbstractContinuation.prototype.updateStateToFinal_0 = function (expect, proposedUpdate, mode) {
    if (!this.tryUpdateStateToFinal_0(expect, proposedUpdate)) {
      return false;
    }
    this.completeStateUpdate_0(expect, proposedUpdate, mode);
    return true;
  };
  AbstractContinuation.prototype.tryUpdateStateToFinal_0 = function (expect, update) {
    var tmp$;
    if (!!Kotlin.isType(update, NotCompleted)) {
      var message = 'Failed requirement.';
      throw IllegalArgumentException_init(message.toString());
    }
    if (!this._state_0.compareAndSet_xwzc9q$(expect, update))
      return false;
    if ((tmp$ = this.parentHandle_0) != null) {
      tmp$.dispose();
      this.parentHandle_0 = NonDisposableHandle_getInstance();
    }
    return true;
  };
  AbstractContinuation.prototype.completeStateUpdate_0 = function (expect, update, mode) {
    var tmp$;
    var exceptionally = Kotlin.isType(tmp$ = update, CompletedExceptionally) ? tmp$ : null;
    this.onCompletionInternal_0(mode);
    if (Kotlin.isType(update, CancelledContinuation) && Kotlin.isType(expect, CancelHandler)) {
      try {
        expect.invoke(exceptionally != null ? exceptionally.cause : null);
      }
       catch (ex) {
        if (Kotlin.isType(ex, Throwable)) {
          this.handleException_0(new CompletionHandlerException('Exception in completion handler ' + expect + ' for ' + this, ex));
        }
         else
          throw ex;
      }
    }
  };
  AbstractContinuation.prototype.handleException_0 = function (exception) {
    handleCoroutineException(this.context, exception);
  };
  AbstractContinuation.prototype.toString = function () {
    return this.nameString() + '{' + this.stateString_0() + '}@' + get_hexAddress(this);
  };
  AbstractContinuation.prototype.nameString = function () {
    return get_classSimpleName(this);
  };
  AbstractContinuation.prototype.stateString_0 = function () {
    var tmp$;
    var state = this.state_8be2vx$;
    if (Kotlin.isType(state, NotCompleted))
      tmp$ = 'Active';
    else if (Kotlin.isType(state, CancelledContinuation))
      tmp$ = 'Cancelled';
    else if (Kotlin.isType(state, CompletedExceptionally))
      tmp$ = 'CompletedExceptionally';
    else
      tmp$ = 'Completed';
    return tmp$;
  };
  AbstractContinuation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractContinuation',
    interfaces: [DispatchedTask, Continuation]
  };
  function NotCompleted() {
  }
  NotCompleted.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'NotCompleted',
    interfaces: []
  };
  function Active() {
  }
  Active.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Active',
    interfaces: [NotCompleted]
  };
  var ACTIVE;
  function Cancelling(cancel) {
    this.cancel = cancel;
  }
  Cancelling.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Cancelling',
    interfaces: [NotCompleted]
  };
  function CancelHandler() {
    CancelHandlerBase.call(this);
  }
  CancelHandler.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CancelHandler',
    interfaces: [NotCompleted, CancelHandlerBase]
  };
  function InvokeOnCancel(handler) {
    CancelHandler.call(this);
    this.handler_0 = handler;
  }
  InvokeOnCancel.prototype.invoke = function (cause) {
    this.handler_0(cause);
  };
  InvokeOnCancel.prototype.toString = function () {
    return 'InvokeOnCancel[' + get_classSimpleName(this.handler_0) + '@' + get_hexAddress(this) + ']';
  };
  InvokeOnCancel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InvokeOnCancel',
    interfaces: [CancelHandler]
  };
  function AbstractCoroutine(parentContext, active) {
    if (active === void 0)
      active = true;
    JobSupport.call(this, active);
    this.parentContext_ly5fyv$_0 = parentContext;
    this.context_4jptjh$_0 = this.parentContext_ly5fyv$_0.plus_dvqyjb$(this);
  }
  Object.defineProperty(AbstractCoroutine.prototype, 'context', {
    get: function () {
      return this.context_4jptjh$_0;
    }
  });
  Object.defineProperty(AbstractCoroutine.prototype, 'coroutineContext', {
    get: function () {
      return this.context;
    }
  });
  AbstractCoroutine.prototype.initParentJob_8be2vx$ = function () {
    this.initParentJobInternal_x4lgmv$(this.parentContext_ly5fyv$_0.get_8oh8b3$(Job$Key_getInstance()));
  };
  AbstractCoroutine.prototype.onStart = function () {
  };
  AbstractCoroutine.prototype.onStartInternal = function () {
    this.onStart();
  };
  AbstractCoroutine.prototype.onCancellation_dbl4no$ = function (cause) {
  };
  AbstractCoroutine.prototype.onCancellationInternal_kybjp5$ = function (exceptionally) {
    this.onCancellation_dbl4no$(exceptionally != null ? exceptionally.cause : null);
  };
  AbstractCoroutine.prototype.onCompleted_11rb$ = function (value) {
  };
  AbstractCoroutine.prototype.onCompletedExceptionally_tcv7n7$ = function (exception) {
  };
  AbstractCoroutine.prototype.onCompletionInternal_cypnoy$ = function (state, mode) {
    var tmp$;
    if (Kotlin.isType(state, CompletedExceptionally))
      this.onCompletedExceptionally_tcv7n7$(state.cause);
    else {
      this.onCompleted_11rb$((tmp$ = state) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE());
    }
  };
  Object.defineProperty(AbstractCoroutine.prototype, 'defaultResumeMode', {
    get: function () {
      return 0;
    }
  });
  AbstractCoroutine.prototype.resume_11rb$ = function (value) {
    this.makeCompletingOnce_42w2xh$(value, this.defaultResumeMode);
  };
  AbstractCoroutine.prototype.resumeWithException_tcv7n7$ = function (exception) {
    this.makeCompletingOnce_42w2xh$(new CompletedExceptionally(exception), this.defaultResumeMode);
  };
  AbstractCoroutine.prototype.handleException_tcv7n7$ = function (exception) {
    handleCoroutineException(this.parentContext_ly5fyv$_0, exception);
  };
  AbstractCoroutine.prototype.nameString = function () {
    var tmp$;
    tmp$ = get_coroutineName(this.context);
    if (tmp$ == null) {
      return JobSupport.prototype.nameString.call(this);
    }
    var coroutineName = tmp$;
    return '"' + coroutineName + '"' + ':' + JobSupport.prototype.nameString.call(this);
  };
  AbstractCoroutine.prototype.start_97aoev$ = function (start, block) {
    this.initParentJob_8be2vx$();
    start.invoke_c3kej2$(block, this);
  };
  AbstractCoroutine.prototype.start_1qsk3b$ = function (start, receiver, block) {
    this.initParentJob_8be2vx$();
    start.invoke_bmqrhp$(block, receiver, this);
  };
  AbstractCoroutine.prototype.invokeOnCompletion_ct2b2z$$default = function (onCancelling, invokeImmediately, handler) {
    return this.invokeOnCompletion_ct2b2z$(onCancelling, invokeImmediately, handler, JobSupport.prototype.invokeOnCompletion_ct2b2z$$default.bind(this));
  };
  AbstractCoroutine.prototype.cancel_dbl4no$$default = function (cause) {
    return this.cancel_dbl4no$(cause, JobSupport.prototype.cancel_dbl4no$$default.bind(this));
  };
  AbstractCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractCoroutine',
    interfaces: [CoroutineScope, Continuation, JobSupport, Job]
  };
  function awaitAll(deferreds_0, continuation_0, suspended) {
    var instance = new Coroutine$awaitAll(deferreds_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$awaitAll(deferreds_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$deferreds = deferreds_0;
  }
  Coroutine$awaitAll.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$awaitAll.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$awaitAll.prototype.constructor = Coroutine$awaitAll;
  Coroutine$awaitAll.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$deferreds.length === 0) {
              return emptyList();
            }
             else {
              this.state_0 = 2;
              this.result_0 = (new AwaitAll(this.local$deferreds)).await(this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }

          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          case 3:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  function awaitAll_0($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$awaitAll_0($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$awaitAll_0($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$awaitAll_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$awaitAll_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$awaitAll_0.prototype.constructor = Coroutine$awaitAll_0;
  Coroutine$awaitAll_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$$receiver.isEmpty()) {
              return emptyList();
            }
             else {
              this.state_0 = 2;
              this.result_0 = (new AwaitAll(copyToArray(this.local$$receiver))).await(this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }

          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          case 3:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function joinAll(jobs_0, continuation_0, suspended) {
    var instance = new Coroutine$joinAll(jobs_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$joinAll(jobs_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$forEach$result = void 0;
    this.local$tmp$ = void 0;
    this.local$jobs = jobs_0;
  }
  Coroutine$joinAll.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$joinAll.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$joinAll.prototype.constructor = Coroutine$joinAll;
  Coroutine$joinAll.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = 0;
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (this.local$tmp$ === this.local$jobs.length) {
              this.state_0 = 5;
              continue;
            }

            var element = this.local$jobs[this.local$tmp$];
            this.state_0 = 3;
            this.result_0 = element.join(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 4;
            continue;
          case 4:
            ++this.local$tmp$;
            this.state_0 = 2;
            continue;
          case 5:
            return this.local$forEach$result;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function joinAll_0($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$joinAll_0($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$joinAll_0($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$forEach$result = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$joinAll_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$joinAll_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$joinAll_0.prototype.constructor = Coroutine$joinAll_0;
  Coroutine$joinAll_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$tmp$.hasNext()) {
              this.state_0 = 4;
              continue;
            }

            var element = this.local$tmp$.next();
            this.state_0 = 3;
            this.result_0 = element.join(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            return this.local$forEach$result;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function AwaitAll(deferreds) {
    this.deferreds_0 = deferreds;
    this.notCompletedCount_0 = atomic(this.deferreds_0.length);
  }
  var Array_0 = Array;
  function AwaitAll$await$lambda(this$AwaitAll) {
    return function (cont) {
      var size = this$AwaitAll.deferreds_0.length;
      var array = Array_0(size);
      var tmp$;
      tmp$ = array.length - 1 | 0;
      for (var i = 0; i <= tmp$; i++) {
        var this$AwaitAll_0 = this$AwaitAll;
        var deferred = this$AwaitAll_0.deferreds_0[i];
        deferred.start();
        var $receiver = new AwaitAll$AwaitAllNode(this$AwaitAll_0, cont, deferred);
        $receiver.handle = deferred.invokeOnCompletion_f05bi3$($receiver);
        array[i] = $receiver;
      }
      var nodes = array;
      var disposer = new AwaitAll$DisposeHandlersOnCancel(this$AwaitAll, nodes);
      var tmp$_0;
      for (tmp$_0 = 0; tmp$_0 !== nodes.length; ++tmp$_0) {
        var element = nodes[tmp$_0];
        element.disposer = disposer;
      }
      if (cont.isCompleted) {
        disposer.disposeAll();
      }
       else {
        cont.invokeOnCancellation_f05bi3$(disposer);
      }
      return Unit;
    };
  }
  function suspendCancellableCoroutine$lambda(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 1);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  AwaitAll.prototype.await = function (continuation) {
    return suspendCancellableCoroutine$lambda(false, AwaitAll$await$lambda(this))(continuation.facade);
  };
  function AwaitAll$DisposeHandlersOnCancel($outer, nodes) {
    this.$outer = $outer;
    CancelHandler.call(this);
    this.nodes_0 = nodes;
  }
  AwaitAll$DisposeHandlersOnCancel.prototype.disposeAll = function () {
    var $receiver = this.nodes_0;
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      element.handle.dispose();
    }
  };
  AwaitAll$DisposeHandlersOnCancel.prototype.invoke = function (cause) {
    this.disposeAll();
  };
  AwaitAll$DisposeHandlersOnCancel.prototype.toString = function () {
    return 'DisposeHandlersOnCancel[' + this.nodes_0 + ']';
  };
  AwaitAll$DisposeHandlersOnCancel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DisposeHandlersOnCancel',
    interfaces: [CancelHandler]
  };
  function AwaitAll$AwaitAllNode($outer, continuation, job) {
    this.$outer = $outer;
    JobNode.call(this, job);
    this.continuation_0 = continuation;
    this.handle_ge4cd2$_0 = this.handle_ge4cd2$_0;
    this.disposer = null;
  }
  Object.defineProperty(AwaitAll$AwaitAllNode.prototype, 'handle', {
    get: function () {
      if (this.handle_ge4cd2$_0 == null)
        return throwUPAE('handle');
      return this.handle_ge4cd2$_0;
    },
    set: function (handle) {
      this.handle_ge4cd2$_0 = handle;
    }
  });
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  AwaitAll$AwaitAllNode.prototype.invoke = function (cause) {
    if (cause != null) {
      var token = this.continuation_0.tryResumeWithException_tcv7n7$(cause);
      if (token != null) {
        this.continuation_0.completeResume_za3rmp$(token);
        var disposer = this.disposer;
        if (disposer != null)
          disposer.disposeAll();
      }
    }
     else if (this.$outer.notCompletedCount_0.decrementAndGet() === 0) {
      var tmp$ = this.continuation_0;
      var $receiver = this.$outer.deferreds_0;
      var destination = ArrayList_init_0($receiver.length);
      var tmp$_0;
      for (tmp$_0 = 0; tmp$_0 !== $receiver.length; ++tmp$_0) {
        var item = $receiver[tmp$_0];
        destination.add_11rb$(item.getCompleted());
      }
      tmp$.resume_11rb$(destination);
    }
  };
  AwaitAll$AwaitAllNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AwaitAllNode',
    interfaces: [JobNode]
  };
  AwaitAll.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AwaitAll',
    interfaces: []
  };
  function launch(context, start, parent, onCompletion, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    if (parent === void 0)
      parent = null;
    if (onCompletion === void 0)
      onCompletion = null;
    var newContext = newCoroutineContext(context, parent);
    var coroutine = start.isLazy ? new LazyStandaloneCoroutine(newContext, block) : new StandaloneCoroutine(newContext, true);
    if (onCompletion != null)
      coroutine.invokeOnCompletion_f05bi3$(onCompletion);
    coroutine.start_1qsk3b$(start, coroutine, block);
    return coroutine;
  }
  function launch_0(context, start, parent, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    if (parent === void 0)
      parent = null;
    return launch(context, start, parent, void 0, block);
  }
  function launch_1(context, start, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    return launch(context, start, void 0, void 0, block);
  }
  function launch_2(context, start, block) {
    return launch(context, start ? CoroutineStart$DEFAULT_getInstance() : CoroutineStart$LAZY_getInstance(), void 0, void 0, block);
  }
  function withContext$lambda(closure$context, closure$block, closure$start) {
    return function (cont) {
      var oldContext = cont.context;
      if (closure$context === oldContext || (Kotlin.isType(closure$context, CoroutineContext$Element) && oldContext.get_8oh8b3$(closure$context.key) === closure$context)) {
        return closure$block(cont, false);
      }
      var newContext = oldContext.plus_dvqyjb$(closure$context);
      if (newContext === oldContext) {
        return closure$block(cont, false);
      }
      if (equals(newContext.get_8oh8b3$(ContinuationInterceptor.Key), oldContext.get_8oh8b3$(ContinuationInterceptor.Key))) {
        var newContinuation = new RunContinuationDirect(newContext, cont);
        return closure$block(newContinuation, false);
      }
      var value = !closure$start.isLazy;
      if (!value) {
        var message = closure$start.toString() + ' start is not supported';
        throw IllegalArgumentException_init(message.toString());
      }
      var completion = new RunCompletion(newContext, cont, closure$start === CoroutineStart$ATOMIC_getInstance() ? 0 : 1);
      completion.initParentJobInternal_x4lgmv$(newContext.get_8oh8b3$(Job$Key_getInstance()));
      closure$start.invoke_c3kej2$(closure$block, completion);
      return completion.getResult();
    };
  }
  function withContext(context, start, block, continuation) {
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    return withContext$lambda(context, block, start)(continuation.facade);
  }
  function run(context, start, block, continuation) {
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    return withContext(context, start, block, continuation);
  }
  function run_0(context, block, continuation) {
    return withContext(context, CoroutineStart$ATOMIC_getInstance(), block, continuation);
  }
  function StandaloneCoroutine(parentContext, active) {
    AbstractCoroutine.call(this, parentContext, active);
    this.parentContext_0 = parentContext;
  }
  StandaloneCoroutine.prototype.hasOnFinishingHandler_s8jyv4$ = function (update) {
    return Kotlin.isType(update, CompletedExceptionally);
  };
  StandaloneCoroutine.prototype.onFinishingInternal_s8jyv4$ = function (update) {
    if (Kotlin.isType(update, CompletedExceptionally))
      handleCoroutineException(this.parentContext_0, update.cause);
  };
  StandaloneCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StandaloneCoroutine',
    interfaces: [AbstractCoroutine]
  };
  function LazyStandaloneCoroutine(parentContext, block) {
    StandaloneCoroutine.call(this, parentContext, false);
    this.block_0 = block;
  }
  LazyStandaloneCoroutine.prototype.onStart = function () {
    startCoroutineCancellable_0(this.block_0, this, this);
  };
  LazyStandaloneCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LazyStandaloneCoroutine',
    interfaces: [StandaloneCoroutine]
  };
  function RunContinuationDirect(context, continuation) {
    this.context_j8kok8$_0 = context;
    this.$delegate_l8rxcv$_0 = continuation;
  }
  Object.defineProperty(RunContinuationDirect.prototype, 'context', {
    get: function () {
      return this.context_j8kok8$_0;
    }
  });
  RunContinuationDirect.prototype.resume_11rb$ = function (value) {
    return this.$delegate_l8rxcv$_0.resume_11rb$(value);
  };
  RunContinuationDirect.prototype.resumeWithException_tcv7n7$ = function (exception) {
    return this.$delegate_l8rxcv$_0.resumeWithException_tcv7n7$(exception);
  };
  RunContinuationDirect.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RunContinuationDirect',
    interfaces: [Continuation]
  };
  function RunCompletion(context, delegate, resumeMode) {
    AbstractContinuation.call(this, delegate, resumeMode);
    this.context_17qr5w$_0 = context;
  }
  Object.defineProperty(RunCompletion.prototype, 'context', {
    get: function () {
      return this.context_17qr5w$_0;
    }
  });
  Object.defineProperty(RunCompletion.prototype, 'useCancellingState', {
    get: function () {
      return true;
    }
  });
  RunCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RunCompletion',
    interfaces: [AbstractContinuation]
  };
  function CancellableContinuation() {
  }
  CancellableContinuation.prototype.tryResume_19pj23$ = function (value, idempotent, callback$default) {
    if (idempotent === void 0)
      idempotent = null;
    return callback$default ? callback$default(value, idempotent) : this.tryResume_19pj23$$default(value, idempotent);
  };
  CancellableContinuation.prototype.cancel_dbl4no$ = function (cause, callback$default) {
    if (cause === void 0)
      cause = null;
    return callback$default ? callback$default(cause) : this.cancel_dbl4no$$default(cause);
  };
  CancellableContinuation.prototype.invokeOnCompletion_ct2b2z$ = function (onCancelling, invokeImmediately, handler, callback$default) {
    if (onCancelling === void 0)
      onCancelling = false;
    if (invokeImmediately === void 0)
      invokeImmediately = true;
    return callback$default ? callback$default(onCancelling, invokeImmediately, handler) : this.invokeOnCompletion_ct2b2z$$default(onCancelling, invokeImmediately, handler);
  };
  CancellableContinuation.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CancellableContinuation',
    interfaces: [Continuation]
  };
  function suspendCancellableCoroutine(holdCancellability_0, block_0, continuation) {
    if (holdCancellability_0 === void 0)
      holdCancellability_0 = false;
    return suspendCancellableCoroutine$lambda(holdCancellability_0, block_0)(continuation.facade);
  }
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.suspendCancellableCoroutine_z67fre$', wrapFunction(function () {
    var CancellableContinuationImpl_init = _.kotlinx.coroutines.experimental.CancellableContinuationImpl;
    function suspendCancellableCoroutine$lambda(closure$holdCancellability, closure$block) {
      return function (cont) {
        var cancellable = new CancellableContinuationImpl_init(cont, 1);
        if (!closure$holdCancellability)
          cancellable.initCancellability();
        closure$block(cancellable);
        return cancellable.getResult();
      };
    }
    return function (holdCancellability_0, block_0, continuation) {
      if (holdCancellability_0 === void 0)
        holdCancellability_0 = false;
      Kotlin.suspendCall(suspendCancellableCoroutine$lambda(holdCancellability_0, block_0)(Kotlin.coroutineReceiver().facade));
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function suspendAtomicCancellableCoroutine(holdCancellability_0, block_0, continuation) {
    if (holdCancellability_0 === void 0)
      holdCancellability_0 = false;
    return suspendAtomicCancellableCoroutine$lambda(holdCancellability_0, block_0)(continuation.facade);
  }
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.suspendAtomicCancellableCoroutine_z67fre$', wrapFunction(function () {
    var CancellableContinuationImpl_init = _.kotlinx.coroutines.experimental.CancellableContinuationImpl;
    function suspendAtomicCancellableCoroutine$lambda(closure$holdCancellability, closure$block) {
      return function (cont) {
        var cancellable = new CancellableContinuationImpl_init(cont, 0);
        if (!closure$holdCancellability)
          cancellable.initCancellability();
        closure$block(cancellable);
        return cancellable.getResult();
      };
    }
    return function (holdCancellability_0, block_0, continuation) {
      if (holdCancellability_0 === void 0)
        holdCancellability_0 = false;
      Kotlin.suspendCall(suspendAtomicCancellableCoroutine$lambda(holdCancellability_0, block_0)(Kotlin.coroutineReceiver().facade));
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function removeOnCancel($receiver, node) {
    removeOnCancellation($receiver, node);
    return NonDisposableHandle_getInstance();
  }
  function removeOnCancellation($receiver, node) {
    $receiver.invokeOnCancellation_f05bi3$(new RemoveOnCancel(node));
  }
  function disposeOnCompletion($receiver, handle) {
    disposeOnCancellation($receiver, handle);
    return NonDisposableHandle_getInstance();
  }
  function disposeOnCancellation($receiver, handle) {
    $receiver.invokeOnCancellation_f05bi3$(new DisposeOnCancel(handle));
  }
  function RemoveOnCancel(node) {
    CancelHandler.call(this);
    this.node_0 = node;
  }
  RemoveOnCancel.prototype.invoke = function (cause) {
    this.node_0.remove();
  };
  RemoveOnCancel.prototype.toString = function () {
    return 'RemoveOnCancel[' + this.node_0 + ']';
  };
  RemoveOnCancel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemoveOnCancel',
    interfaces: [CancelHandler]
  };
  function DisposeOnCancel(handle) {
    CancelHandler.call(this);
    this.handle_0 = handle;
  }
  DisposeOnCancel.prototype.invoke = function (cause) {
    this.handle_0.dispose();
  };
  DisposeOnCancel.prototype.toString = function () {
    return 'DisposeOnCancel[' + this.handle_0 + ']';
  };
  DisposeOnCancel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DisposeOnCancel',
    interfaces: [CancelHandler]
  };
  function CancellableContinuationImpl(delegate, resumeMode) {
    AbstractContinuation.call(this, delegate, resumeMode);
    this.context_wbcuzk$_0 = delegate.context;
  }
  Object.defineProperty(CancellableContinuationImpl.prototype, 'context', {
    get: function () {
      return this.context_wbcuzk$_0;
    }
  });
  CancellableContinuationImpl.prototype.initCancellability = function () {
    this.initParentJobInternal_x4lgmv$(this.delegate.context.get_8oh8b3$(Job$Key_getInstance()));
  };
  CancellableContinuationImpl.prototype.invokeOnCompletion_ct2b2z$$default = function (onCancelling, invokeImmediately, handler) {
    this.invokeOnCancellation_f05bi3$(handler);
    return NonDisposableHandle_getInstance();
  };
  CancellableContinuationImpl.prototype.tryResume_19pj23$$default = function (value, idempotent) {
    while (true) {
      var state = this.state_8be2vx$;
      if (Kotlin.isType(state, NotCompleted)) {
        var update = idempotent == null ? value : new CompletedIdempotentResult(idempotent, value, state);
        if (this.tryUpdateStateToFinal_0(state, update))
          return state;
      }
       else if (Kotlin.isType(state, CompletedIdempotentResult))
        if (state.idempotentResume === idempotent) {
          if (!(state.result === value)) {
            var message = 'Non-idempotent resume';
            throw IllegalStateException_init(message.toString());
          }
          return state.token;
        }
         else
          return null;
      else
        return null;
    }
  };
  CancellableContinuationImpl.prototype.tryResumeWithException_tcv7n7$ = function (exception) {
    while (true) {
      var state = this.state_8be2vx$;
      if (Kotlin.isType(state, NotCompleted)) {
        if (this.tryUpdateStateToFinal_0(state, new CompletedExceptionally(exception)))
          return state;
      }
       else
        return null;
    }
  };
  CancellableContinuationImpl.prototype.completeResume_za3rmp$ = function (token) {
    var tmp$;
    this.completeStateUpdate_0(Kotlin.isType(tmp$ = token, NotCompleted) ? tmp$ : throwCCE(), this.state_8be2vx$, this.resumeMode);
  };
  CancellableContinuationImpl.prototype.resumeUndispatched_276mab$ = function ($receiver, value) {
    var tmp$;
    var dc = Kotlin.isType(tmp$ = this.delegate, DispatchedContinuation) ? tmp$ : null;
    this.resumeImpl_0(value, (dc != null ? dc.dispatcher : null) === $receiver ? 3 : this.resumeMode);
  };
  CancellableContinuationImpl.prototype.resumeUndispatchedWithException_eq13df$ = function ($receiver, exception) {
    var tmp$;
    var dc = Kotlin.isType(tmp$ = this.delegate, DispatchedContinuation) ? tmp$ : null;
    this.resumeImpl_0(new CompletedExceptionally(exception), (dc != null ? dc.dispatcher : null) === $receiver ? 3 : this.resumeMode);
  };
  CancellableContinuationImpl.prototype.getSuccessfulResult_tpy1pm$ = function (state) {
    var tmp$, tmp$_0;
    return Kotlin.isType(state, CompletedIdempotentResult) ? (tmp$ = state.result) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE() : (tmp$_0 = state) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
  };
  CancellableContinuationImpl.prototype.nameString = function () {
    return 'CancellableContinuation(' + toDebugString(this.delegate) + ')';
  };
  CancellableContinuationImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CancellableContinuationImpl',
    interfaces: [CancellableContinuation, AbstractContinuation, Runnable]
  };
  function CompletedIdempotentResult(idempotentResume, result, token) {
    this.idempotentResume = idempotentResume;
    this.result = result;
    this.token = token;
  }
  CompletedIdempotentResult.prototype.toString = function () {
    return 'CompletedIdempotentResult[' + toString(this.result) + ']';
  };
  CompletedIdempotentResult.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompletedIdempotentResult',
    interfaces: []
  };
  function CompletableDeferred() {
  }
  CompletableDeferred.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CompletableDeferred',
    interfaces: [Deferred]
  };
  function CompletableDeferred_0(parent) {
    if (parent === void 0)
      parent = null;
    return new CompletableDeferredImpl(parent);
  }
  function CompletableDeferred_1() {
    return new CompletableDeferredImpl(null);
  }
  function CompletableDeferred_2(value) {
    var $receiver = new CompletableDeferredImpl(null);
    $receiver.complete_11rb$(value);
    return $receiver;
  }
  function CompletableDeferredImpl(parent) {
    JobSupport.call(this, true);
    this.initParentJobInternal_x4lgmv$(parent);
  }
  Object.defineProperty(CompletableDeferredImpl.prototype, 'onCancelMode', {
    get: function () {
      return 1;
    }
  });
  CompletableDeferredImpl.prototype.getCompleted = function () {
    var tmp$;
    return (tmp$ = this.getCompletedInternal_8be2vx$()) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  CompletableDeferredImpl.prototype.await = function (continuation_0, suspended) {
    var instance = new Coroutine$await(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$await($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$await.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$await.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$await.prototype.constructor = Coroutine$await;
  Coroutine$await.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            this.state_0 = 2;
            this.result_0 = this.$this.awaitInternal_8be2vx$(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return (tmp$ = this.result_0) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Object.defineProperty(CompletableDeferredImpl.prototype, 'onAwait', {
    get: function () {
      return this;
    }
  });
  CompletableDeferredImpl.prototype.registerSelectClause1_t4n5y6$ = function (select, block) {
    this.registerSelectClause1Internal_noo60r$(select, block);
  };
  CompletableDeferredImpl.prototype.complete_11rb$ = function (value) {
    return this.makeCompleting_8ea4ql$(value);
  };
  CompletableDeferredImpl.prototype.completeExceptionally_tcv7n7$ = function (exception) {
    return this.makeCompleting_8ea4ql$(new CompletedExceptionally(exception));
  };
  CompletableDeferredImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompletableDeferredImpl',
    interfaces: [SelectClause1, CompletableDeferred, JobSupport]
  };
  function CompletedExceptionally(cause) {
    this.cause = cause;
  }
  CompletedExceptionally.prototype.toString = function () {
    return get_classSimpleName(this) + '[' + this.cause + ']';
  };
  CompletedExceptionally.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompletedExceptionally',
    interfaces: []
  };
  function Cancelled(job, cause) {
    CompletedExceptionally.call(this, cause != null ? cause : new JobCancellationException('Job was cancelled normally', null, job));
  }
  Cancelled.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Cancelled',
    interfaces: [CompletedExceptionally]
  };
  function CancelledContinuation(continuation, cause) {
    CompletedExceptionally.call(this, cause != null ? cause : new CancellationException('Continuation ' + continuation + ' was cancelled normally'));
  }
  CancelledContinuation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CancelledContinuation',
    interfaces: [CompletedExceptionally]
  };
  function CoroutineDispatcher() {
    AbstractCoroutineContextElement.call(this, ContinuationInterceptor.Key);
  }
  CoroutineDispatcher.prototype.isDispatchNeeded_dvqyjb$ = function (context) {
    return true;
  };
  CoroutineDispatcher.prototype.interceptContinuation_n4f53e$ = function (continuation) {
    return new DispatchedContinuation(this, continuation);
  };
  CoroutineDispatcher.prototype.plus_nhy9at$ = function (other) {
    return other;
  };
  CoroutineDispatcher.prototype.toString = function () {
    return get_classSimpleName(this) + '@' + get_hexAddress(this);
  };
  CoroutineDispatcher.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CoroutineDispatcher',
    interfaces: [ContinuationInterceptor, AbstractCoroutineContextElement]
  };
  function handleCoroutineException(context, exception) {
    var tmp$, tmp$_0;
    try {
      if ((tmp$ = context.get_8oh8b3$(CoroutineExceptionHandler$Key_getInstance())) != null) {
        tmp$.handleException_y5fbjc$(context, exception);
        return;
      }
      if (Kotlin.isType(exception, CancellationException))
        return;
      (tmp$_0 = context.get_8oh8b3$(Job$Key_getInstance())) != null ? tmp$_0.cancel_dbl4no$(exception) : null;
      handleCoroutineExceptionImpl(context, exception);
    }
     catch (handlerException) {
      if (Kotlin.isType(handlerException, Throwable)) {
        if (handlerException === exception)
          throw exception;
        var $receiver = new RuntimeException('Exception while trying to handle coroutine exception', exception);
        handlerException;
        handlerException;
        throw $receiver;
      }
       else
        throw handlerException;
    }
  }
  var CoroutineExceptionHandler = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.CoroutineExceptionHandler_av07nd$', wrapFunction(function () {
    var AbstractCoroutineContextElement = Kotlin.kotlin.coroutines.experimental.AbstractCoroutineContextElement;
    var Kind_CLASS = Kotlin.Kind.CLASS;
    var CoroutineExceptionHandler = _.kotlinx.coroutines.experimental.CoroutineExceptionHandler;
    CoroutineExceptionHandler$ObjectLiteral.prototype = Object.create(AbstractCoroutineContextElement.prototype);
    CoroutineExceptionHandler$ObjectLiteral.prototype.constructor = CoroutineExceptionHandler$ObjectLiteral;
    function CoroutineExceptionHandler$ObjectLiteral(closure$handler, key) {
      this.closure$handler = closure$handler;
      AbstractCoroutineContextElement.call(this, key);
    }
    CoroutineExceptionHandler$ObjectLiteral.prototype.handleException_y5fbjc$ = function (context, exception) {
      this.closure$handler(context, exception);
    };
    CoroutineExceptionHandler$ObjectLiteral.$metadata$ = {
      kind: Kind_CLASS,
      interfaces: [CoroutineExceptionHandler, AbstractCoroutineContextElement]
    };
    return function (handler) {
      return new CoroutineExceptionHandler$ObjectLiteral(handler, CoroutineExceptionHandler.Key);
    };
  }));
  function CoroutineExceptionHandler_0() {
    CoroutineExceptionHandler$Key_getInstance();
  }
  function CoroutineExceptionHandler$Key() {
    CoroutineExceptionHandler$Key_instance = this;
  }
  CoroutineExceptionHandler$Key.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Key',
    interfaces: [CoroutineContext$Key]
  };
  var CoroutineExceptionHandler$Key_instance = null;
  function CoroutineExceptionHandler$Key_getInstance() {
    if (CoroutineExceptionHandler$Key_instance === null) {
      new CoroutineExceptionHandler$Key();
    }
    return CoroutineExceptionHandler$Key_instance;
  }
  CoroutineExceptionHandler_0.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CoroutineExceptionHandler',
    interfaces: [CoroutineContext$Element]
  };
  function CoroutineScope() {
  }
  CoroutineScope.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CoroutineScope',
    interfaces: []
  };
  function CoroutineStart(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function CoroutineStart_initFields() {
    CoroutineStart_initFields = function () {
    };
    CoroutineStart$DEFAULT_instance = new CoroutineStart('DEFAULT', 0);
    CoroutineStart$LAZY_instance = new CoroutineStart('LAZY', 1);
    CoroutineStart$ATOMIC_instance = new CoroutineStart('ATOMIC', 2);
    CoroutineStart$UNDISPATCHED_instance = new CoroutineStart('UNDISPATCHED', 3);
  }
  var CoroutineStart$DEFAULT_instance;
  function CoroutineStart$DEFAULT_getInstance() {
    CoroutineStart_initFields();
    return CoroutineStart$DEFAULT_instance;
  }
  var CoroutineStart$LAZY_instance;
  function CoroutineStart$LAZY_getInstance() {
    CoroutineStart_initFields();
    return CoroutineStart$LAZY_instance;
  }
  var CoroutineStart$ATOMIC_instance;
  function CoroutineStart$ATOMIC_getInstance() {
    CoroutineStart_initFields();
    return CoroutineStart$ATOMIC_instance;
  }
  var CoroutineStart$UNDISPATCHED_instance;
  function CoroutineStart$UNDISPATCHED_getInstance() {
    CoroutineStart_initFields();
    return CoroutineStart$UNDISPATCHED_instance;
  }
  CoroutineStart.prototype.invoke_c3kej2$ = function (block, completion) {
    switch (this.name) {
      case 'DEFAULT':
        startCoroutineCancellable(block, completion);
        break;
      case 'ATOMIC':
        startCoroutine(block, completion);
        break;
      case 'UNDISPATCHED':
        startCoroutineUndispatched(block, completion);
        break;
      case 'LAZY':
        break;
      default:Kotlin.noWhenBranchMatched();
        break;
    }
  };
  CoroutineStart.prototype.invoke_bmqrhp$ = function (block, receiver, completion) {
    switch (this.name) {
      case 'DEFAULT':
        startCoroutineCancellable_0(block, receiver, completion);
        break;
      case 'ATOMIC':
        startCoroutine_0(block, receiver, completion);
        break;
      case 'UNDISPATCHED':
        startCoroutineUndispatched_0(block, receiver, completion);
        break;
      case 'LAZY':
        break;
      default:Kotlin.noWhenBranchMatched();
        break;
    }
  };
  Object.defineProperty(CoroutineStart.prototype, 'isLazy', {
    get: function () {
      return this === CoroutineStart$LAZY_getInstance();
    }
  });
  CoroutineStart.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CoroutineStart',
    interfaces: [Enum]
  };
  function CoroutineStart$values() {
    return [CoroutineStart$DEFAULT_getInstance(), CoroutineStart$LAZY_getInstance(), CoroutineStart$ATOMIC_getInstance(), CoroutineStart$UNDISPATCHED_getInstance()];
  }
  CoroutineStart.values = CoroutineStart$values;
  function CoroutineStart$valueOf(name) {
    switch (name) {
      case 'DEFAULT':
        return CoroutineStart$DEFAULT_getInstance();
      case 'LAZY':
        return CoroutineStart$LAZY_getInstance();
      case 'ATOMIC':
        return CoroutineStart$ATOMIC_getInstance();
      case 'UNDISPATCHED':
        return CoroutineStart$UNDISPATCHED_getInstance();
      default:throwISE('No enum constant kotlinx.coroutines.experimental.CoroutineStart.' + name);
    }
  }
  CoroutineStart.valueOf_61zpoe$ = CoroutineStart$valueOf;
  function Deferred() {
  }
  Object.defineProperty(Deferred.prototype, 'isComputing', {
    get: function () {
      return this.isActive;
    }
  });
  Deferred.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Deferred',
    interfaces: [Job]
  };
  function async(context, start, parent, onCompletion, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    if (parent === void 0)
      parent = null;
    if (onCompletion === void 0)
      onCompletion = null;
    var newContext = newCoroutineContext(context, parent);
    var coroutine = start.isLazy ? new LazyDeferredCoroutine(newContext, block) : new DeferredCoroutine(newContext, true);
    if (onCompletion != null)
      coroutine.invokeOnCompletion_f05bi3$(onCompletion);
    coroutine.start_1qsk3b$(start, coroutine, block);
    return coroutine;
  }
  function async_0(context, start, parent, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    if (parent === void 0)
      parent = null;
    return async(context, start, parent, void 0, block);
  }
  function async_1(context, start, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    return async(context, start, void 0, void 0, block);
  }
  function async_2(context, start, block) {
    return async(context, start ? CoroutineStart$DEFAULT_getInstance() : CoroutineStart$LAZY_getInstance(), void 0, void 0, block);
  }
  function defer(context, block) {
    return async(context, void 0, void 0, void 0, block);
  }
  function DeferredCoroutine(parentContext, active) {
    AbstractCoroutine.call(this, parentContext, active);
  }
  DeferredCoroutine.prototype.getCompleted = function () {
    var tmp$;
    return (tmp$ = this.getCompletedInternal_8be2vx$()) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  DeferredCoroutine.prototype.await = function (continuation_0, suspended) {
    var instance = new Coroutine$await_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$await_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$await_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$await_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$await_0.prototype.constructor = Coroutine$await_0;
  Coroutine$await_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            this.state_0 = 2;
            this.result_0 = this.$this.awaitInternal_8be2vx$(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return (tmp$ = this.result_0) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Object.defineProperty(DeferredCoroutine.prototype, 'onAwait', {
    get: function () {
      return this;
    }
  });
  DeferredCoroutine.prototype.registerSelectClause1_t4n5y6$ = function (select, block) {
    this.registerSelectClause1Internal_noo60r$(select, block);
  };
  DeferredCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DeferredCoroutine',
    interfaces: [SelectClause1, Deferred, AbstractCoroutine]
  };
  function LazyDeferredCoroutine(parentContext, block) {
    DeferredCoroutine.call(this, parentContext, false);
    this.block_0 = block;
  }
  LazyDeferredCoroutine.prototype.onStart = function () {
    startCoroutineCancellable_0(this.block_0, this, this);
  };
  LazyDeferredCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LazyDeferredCoroutine',
    interfaces: [DeferredCoroutine]
  };
  function Delay() {
  }
  function Delay$delay$lambda(closure$time, closure$unit, this$Delay) {
    return function (it) {
      this$Delay.scheduleResumeAfterDelay_v6u85w$(closure$time, closure$unit, it);
      return Unit;
    };
  }
  function suspendCancellableCoroutine$lambda_0(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 1);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  Delay.prototype.delay_wex4td$$default = function (time, unit, continuation) {
    if (time.toNumber() <= 0)
      return;
    return suspendCancellableCoroutine$lambda_0(false, Delay$delay$lambda(time, unit, this))(continuation.facade);
  };
  Delay.prototype.delay_wex4td$ = function (time, unit, continuation, callback$default) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    return callback$default ? callback$default(time, unit, continuation) : this.delay_wex4td$$default(time, unit, continuation);
  };
  Delay.prototype.invokeOnTimeout_myg4gi$ = function (time, unit, block) {
    return DefaultDelay.invokeOnTimeout_myg4gi$(time, unit, block);
  };
  Delay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Delay',
    interfaces: []
  };
  function delay(time, continuation) {
    return delay_0(Kotlin.Long.fromInt(time), TimeUnit$MILLISECONDS_getInstance(), continuation);
  }
  function delay$lambda(closure$time, closure$unit) {
    return function (cont) {
      get_delay(cont.context).scheduleResumeAfterDelay_v6u85w$(closure$time, closure$unit, cont);
      return Unit;
    };
  }
  function delay_0(time, unit, continuation) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    if (time.toNumber() <= 0)
      return;
    return suspendCancellableCoroutine$lambda_0(false, delay$lambda(time, unit))(continuation.facade);
  }
  function get_delay($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver.get_8oh8b3$(ContinuationInterceptor.Key), Delay) ? tmp$ : null) != null ? tmp$_0 : DefaultDelay;
  }
  var UNDEFINED;
  function DispatchedContinuation(dispatcher, continuation) {
    this.dispatcher = dispatcher;
    this.continuation = continuation;
    this._state_0 = UNDEFINED;
    this.resumeMode_fpnkpi$_0 = 0;
  }
  Object.defineProperty(DispatchedContinuation.prototype, 'resumeMode', {
    get: function () {
      return this.resumeMode_fpnkpi$_0;
    },
    set: function (resumeMode) {
      this.resumeMode_fpnkpi$_0 = resumeMode;
    }
  });
  DispatchedContinuation.prototype.takeState = function () {
    var state = this._state_0;
    if (!(state !== UNDEFINED)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    this._state_0 = UNDEFINED;
    return state;
  };
  Object.defineProperty(DispatchedContinuation.prototype, 'delegate', {
    get: function () {
      return this;
    }
  });
  DispatchedContinuation.prototype.resume_11rb$ = function (value) {
    var context = this.continuation.context;
    if (this.dispatcher.isDispatchNeeded_dvqyjb$(context)) {
      this._state_0 = value;
      this.resumeMode = 0;
      this.dispatcher.dispatch_jts95w$(context, this);
    }
     else {
      this.context;
      this.continuation.resume_11rb$(value);
    }
  };
  DispatchedContinuation.prototype.resumeWithException_tcv7n7$ = function (exception) {
    var context = this.continuation.context;
    if (this.dispatcher.isDispatchNeeded_dvqyjb$(context)) {
      this._state_0 = new CompletedExceptionally(exception);
      this.resumeMode = 0;
      this.dispatcher.dispatch_jts95w$(context, this);
    }
     else {
      this.context;
      this.continuation.resumeWithException_tcv7n7$(exception);
    }
  };
  DispatchedContinuation.prototype.resumeCancellable_11rb$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.DispatchedContinuation.resumeCancellable_11rb$', wrapFunction(function () {
    return function (value) {
      var context = this.continuation.context;
      if (this.dispatcher.isDispatchNeeded_dvqyjb$(context)) {
        this._state_0 = value;
        this.resumeMode = 1;
        this.dispatcher.dispatch_jts95w$(context, this);
      }
       else {
        this.context;
        this.continuation.resume_11rb$(value);
      }
    };
  }));
  DispatchedContinuation.prototype.resumeCancellableWithException_tcv7n7$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.DispatchedContinuation.resumeCancellableWithException_tcv7n7$', wrapFunction(function () {
    var CompletedExceptionally_init = _.kotlinx.coroutines.experimental.CompletedExceptionally;
    return function (exception) {
      var context = this.continuation.context;
      if (this.dispatcher.isDispatchNeeded_dvqyjb$(context)) {
        this._state_0 = new CompletedExceptionally_init(exception);
        this.resumeMode = 1;
        this.dispatcher.dispatch_jts95w$(context, this);
      }
       else {
        this.context;
        this.continuation.resumeWithException_tcv7n7$(exception);
      }
    };
  }));
  DispatchedContinuation.prototype.resumeUndispatched_11rb$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.DispatchedContinuation.resumeUndispatched_11rb$', wrapFunction(function () {
    return function (value) {
      this.context;
      this.continuation.resume_11rb$(value);
    };
  }));
  DispatchedContinuation.prototype.resumeUndispatchedWithException_tcv7n7$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.DispatchedContinuation.resumeUndispatchedWithException_tcv7n7$', wrapFunction(function () {
    return function (exception) {
      this.context;
      this.continuation.resumeWithException_tcv7n7$(exception);
    };
  }));
  DispatchedContinuation.prototype.dispatchYield_1c3m6u$ = function (value) {
    var context = this.continuation.context;
    this._state_0 = value;
    this.resumeMode = 1;
    this.dispatcher.dispatch_jts95w$(context, this);
  };
  DispatchedContinuation.prototype.toString = function () {
    return 'DispatchedContinuation[' + this.dispatcher + ', ' + toDebugString(this.continuation) + ']';
  };
  Object.defineProperty(DispatchedContinuation.prototype, 'context', {
    get: function () {
      return this.continuation.context;
    }
  });
  DispatchedContinuation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DispatchedContinuation',
    interfaces: [DispatchedTask, Continuation]
  };
  function resumeCancellable($receiver, value) {
    if (Kotlin.isType($receiver, DispatchedContinuation)) {
      var context = $receiver.continuation.context;
      if ($receiver.dispatcher.isDispatchNeeded_dvqyjb$(context)) {
        $receiver._state_0 = value;
        $receiver.resumeMode = 1;
        $receiver.dispatcher.dispatch_jts95w$(context, $receiver);
      }
       else {
        $receiver.context;
        $receiver.continuation.resume_11rb$(value);
      }
    }
     else
      $receiver.resume_11rb$(value);
  }
  function resumeCancellableWithException($receiver, exception) {
    if (Kotlin.isType($receiver, DispatchedContinuation)) {
      var context = $receiver.continuation.context;
      if ($receiver.dispatcher.isDispatchNeeded_dvqyjb$(context)) {
        $receiver._state_0 = new CompletedExceptionally(exception);
        $receiver.resumeMode = 1;
        $receiver.dispatcher.dispatch_jts95w$(context, $receiver);
      }
       else {
        $receiver.context;
        $receiver.continuation.resumeWithException_tcv7n7$(exception);
      }
    }
     else
      $receiver.resumeWithException_tcv7n7$(exception);
  }
  function resumeDirect($receiver, value) {
    if (Kotlin.isType($receiver, DispatchedContinuation))
      $receiver.continuation.resume_11rb$(value);
    else
      $receiver.resume_11rb$(value);
  }
  function resumeDirectWithException($receiver, exception) {
    if (Kotlin.isType($receiver, DispatchedContinuation))
      $receiver.continuation.resumeWithException_tcv7n7$(exception);
    else
      $receiver.resumeWithException_tcv7n7$(exception);
  }
  function DispatchedTask() {
  }
  Object.defineProperty(DispatchedTask.prototype, 'resumeMode', {
    get: function () {
      return 1;
    }
  });
  DispatchedTask.prototype.getSuccessfulResult_tpy1pm$ = function (state) {
    var tmp$;
    return (tmp$ = state) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  DispatchedTask.prototype.getExceptionalResult_s8jyv4$ = function (state) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = state, CompletedExceptionally) ? tmp$ : null) != null ? tmp$_0.cause : null;
  };
  DispatchedTask.prototype.run = function () {
    var tmp$;
    try {
      var delegate = Kotlin.isType(tmp$ = this.delegate, DispatchedContinuation) ? tmp$ : throwCCE();
      var continuation = delegate.continuation;
      var context = continuation.context;
      var job = get_isCancellableMode(this.resumeMode) ? context.get_8oh8b3$(Job$Key_getInstance()) : null;
      var state = this.takeState();
      if (job != null && !job.isActive)
        continuation.resumeWithException_tcv7n7$(job.getCancellationException());
      else {
        var exception = this.getExceptionalResult_s8jyv4$(state);
        if (exception != null)
          continuation.resumeWithException_tcv7n7$(exception);
        else
          continuation.resume_11rb$(this.getSuccessfulResult_tpy1pm$(state));
      }
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        throw new DispatchException('Unexpected exception running ' + this, e);
      }
       else
        throw e;
    }
  };
  DispatchedTask.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DispatchedTask',
    interfaces: [Runnable]
  };
  function dispatch($receiver, mode) {
    if (mode === void 0)
      mode = 1;
    var useMode = mode;
    var delegate = $receiver.delegate;
    if (get_isDispatchedMode(mode) && Kotlin.isType(delegate, DispatchedContinuation) && get_isCancellableMode(mode) === get_isCancellableMode($receiver.resumeMode)) {
      var dispatcher = delegate.dispatcher;
      var context = delegate.context;
      if (dispatcher.isDispatchNeeded_dvqyjb$(context)) {
        dispatcher.dispatch_jts95w$(context, $receiver);
        return;
      }
       else {
        useMode = 3;
      }
    }
    var state = $receiver.takeState();
    var exception = $receiver.getExceptionalResult_s8jyv4$(state);
    if (exception != null) {
      resumeWithExceptionMode(delegate, exception, useMode);
    }
     else {
      resumeMode(delegate, $receiver.getSuccessfulResult_tpy1pm$(state), useMode);
    }
  }
  function Job() {
    Job$Key_getInstance();
  }
  function Job$Key() {
    Job$Key_instance = this;
    CoroutineExceptionHandler$Key_getInstance();
  }
  Job$Key.prototype.invoke_c6qot0$ = function (parent) {
    if (parent === void 0)
      parent = null;
    return Job_0(parent);
  };
  Job$Key.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Key',
    interfaces: [CoroutineContext$Key]
  };
  var Job$Key_instance = null;
  function Job$Key_getInstance() {
    if (Job$Key_instance === null) {
      new Job$Key();
    }
    return Job$Key_instance;
  }
  Job.prototype.getCompletionException = function () {
    return this.getCancellationException();
  };
  Job.prototype.cancel_dbl4no$ = function (cause, callback$default) {
    if (cause === void 0)
      cause = null;
    return callback$default ? callback$default(cause) : this.cancel_dbl4no$$default(cause);
  };
  Job.prototype.cancelChildren_dbl4no$ = function (cause, callback$default) {
    if (cause === void 0)
      cause = null;
    callback$default ? callback$default(cause) : this.cancelChildren_dbl4no$$default(cause);
  };
  Job.prototype.invokeOnCompletion_h883ze$ = function (onCancelling_, handler, callback$default) {
    if (onCancelling_ === void 0)
      onCancelling_ = false;
    return callback$default ? callback$default(onCancelling_, handler) : this.invokeOnCompletion_h883ze$$default(onCancelling_, handler);
  };
  Job.prototype.invokeOnCompletion_ct2b2z$ = function (onCancelling, invokeImmediately, handler, callback$default) {
    if (onCancelling === void 0)
      onCancelling = false;
    if (invokeImmediately === void 0)
      invokeImmediately = true;
    return callback$default ? callback$default(onCancelling, invokeImmediately, handler) : this.invokeOnCompletion_ct2b2z$$default(onCancelling, invokeImmediately, handler);
  };
  Job.prototype.plus_r3p3g3$ = function (other) {
    return other;
  };
  Job.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Job',
    interfaces: [CoroutineContext$Element]
  };
  function Job_0(parent) {
    if (parent === void 0)
      parent = null;
    return new JobImpl(parent);
  }
  function DisposableHandle() {
  }
  DisposableHandle.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DisposableHandle',
    interfaces: []
  };
  function unregisterOnCompletion($receiver, registration) {
    return $receiver.invokeOnCompletion_f05bi3$(new DisposeOnCompletion($receiver, registration));
  }
  function disposeOnCompletion_0($receiver, handle) {
    return $receiver.invokeOnCompletion_f05bi3$(new DisposeOnCompletion($receiver, handle));
  }
  function cancelAndJoin($receiver, continuation) {
    $receiver.cancel_dbl4no$();
    return $receiver.join(continuation);
  }
  function cancelChildren($receiver, cause) {
    if (cause === void 0)
      cause = null;
    var tmp$;
    tmp$ = $receiver.children.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.cancel_dbl4no$(cause);
    }
  }
  function joinChildren($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$joinChildren($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$joinChildren($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$joinChildren.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$joinChildren.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$joinChildren.prototype.constructor = Coroutine$joinChildren;
  Coroutine$joinChildren.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$$receiver.children.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$tmp$.hasNext()) {
              this.state_0 = 4;
              continue;
            }

            var element = this.local$tmp$.next();
            this.state_0 = 3;
            this.result_0 = element.join(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function get_isActive($receiver) {
    var tmp$;
    return ((tmp$ = $receiver.get_8oh8b3$(Job$Key_getInstance())) != null ? tmp$.isActive : null) === true;
  }
  function cancel($receiver, cause) {
    if (cause === void 0)
      cause = null;
    var tmp$, tmp$_0;
    return (tmp$_0 = (tmp$ = $receiver.get_8oh8b3$(Job$Key_getInstance())) != null ? tmp$.cancel_dbl4no$(cause) : null) != null ? tmp$_0 : false;
  }
  function cancelChildren_0($receiver, cause) {
    if (cause === void 0)
      cause = null;
    var tmp$;
    (tmp$ = $receiver.get_8oh8b3$(Job$Key_getInstance())) != null ? (cancelChildren(tmp$, cause), Unit) : null;
  }
  function join($receiver, continuation) {
    return $receiver.join(continuation);
  }
  function NonDisposableHandle() {
    NonDisposableHandle_instance = this;
  }
  NonDisposableHandle.prototype.dispose = function () {
  };
  NonDisposableHandle.prototype.toString = function () {
    return 'NonDisposableHandle';
  };
  NonDisposableHandle.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'NonDisposableHandle',
    interfaces: [DisposableHandle]
  };
  var NonDisposableHandle_instance = null;
  function NonDisposableHandle_getInstance() {
    if (NonDisposableHandle_instance === null) {
      new NonDisposableHandle();
    }
    return NonDisposableHandle_instance;
  }
  function JobSupport(active) {
    this._state_0 = atomic_0(active ? EmptyActive : EmptyNew);
    this.parentHandle_0 = null;
  }
  Object.defineProperty(JobSupport.prototype, 'key', {
    get: function () {
      return Job$Key_getInstance();
    }
  });
  JobSupport.prototype.initParentJobInternal_x4lgmv$ = function (parent) {
    if (!(this.parentHandle_0 == null)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    if (parent == null) {
      this.parentHandle_0 = NonDisposableHandle_getInstance();
      return;
    }
    parent.start();
    var handle = parent.attachChild_r3p3g3$(this);
    this.parentHandle_0 = handle;
    if (this.isCompleted) {
      handle.dispose();
      this.parentHandle_0 = NonDisposableHandle_getInstance();
    }
  };
  Object.defineProperty(JobSupport.prototype, 'state_8be2vx$', {
    get: function () {
      var $receiver = this._state_0;
      while (true) {
        var state = $receiver.value;
        if (!Kotlin.isType(state, OpDescriptor))
          return state;
        state.perform_s8jyv4$(this);
      }
    }
  });
  JobSupport.prototype.loopOnState_0 = function (block) {
    while (true) {
      block(this.state_8be2vx$);
    }
  };
  Object.defineProperty(JobSupport.prototype, 'isActive', {
    get: function () {
      var state = this.state_8be2vx$;
      return Kotlin.isType(state, Incomplete) && state.isActive;
    }
  });
  Object.defineProperty(JobSupport.prototype, 'isCompleted', {
    get: function () {
      return !Kotlin.isType(this.state_8be2vx$, Incomplete);
    }
  });
  Object.defineProperty(JobSupport.prototype, 'isCancelled', {
    get: function () {
      var state = this.state_8be2vx$;
      return Kotlin.isType(state, Cancelled) || (Kotlin.isType(state, JobSupport$Finishing) && state.cancelled != null);
    }
  });
  JobSupport.prototype.updateState_0 = function (expect, proposedUpdate, mode) {
    var update = this.coerceProposedUpdate_0(expect, proposedUpdate);
    if (!this.tryUpdateState_0(expect, update))
      return false;
    this.completeUpdateState_0(expect, update, mode);
    return true;
  };
  JobSupport.prototype.coerceProposedUpdate_0 = function (expect, proposedUpdate) {
    return Kotlin.isType(expect, JobSupport$Finishing) && expect.cancelled != null && !this.isCorrespondinglyCancelled_0(expect.cancelled, proposedUpdate) ? this.createCancelled_0(expect.cancelled, proposedUpdate) : proposedUpdate;
  };
  JobSupport.prototype.isCorrespondinglyCancelled_0 = function (cancelled, proposedUpdate) {
    if (!Kotlin.isType(proposedUpdate, Cancelled))
      return false;
    return equals(proposedUpdate.cause, cancelled.cause) || Kotlin.isType(proposedUpdate.cause, JobCancellationException);
  };
  JobSupport.prototype.createCancelled_0 = function (cancelled, proposedUpdate) {
    if (!Kotlin.isType(proposedUpdate, CompletedExceptionally))
      return cancelled;
    var exception = proposedUpdate.cause;
    if (equals(cancelled.cause, exception))
      return cancelled;
    if (!Kotlin.isType(cancelled.cause, JobCancellationException)) {
      cancelled.cause;
    }
    return new Cancelled(this, exception);
  };
  JobSupport.prototype.tryUpdateState_0 = function (expect, update) {
    var tmp$;
    if (!!Kotlin.isType(update, Incomplete)) {
      var message = 'Failed requirement.';
      throw IllegalArgumentException_init(message.toString());
    }
    if (!this._state_0.compareAndSet_xwzc9q$(expect, update))
      return false;
    if ((tmp$ = this.parentHandle_0) != null) {
      tmp$.dispose();
      this.parentHandle_0 = NonDisposableHandle_getInstance();
    }
    return true;
  };
  JobSupport.prototype.completeUpdateState_0 = function (expect, update, mode) {
    var tmp$, tmp$_0;
    var exceptionally = Kotlin.isType(tmp$ = update, CompletedExceptionally) ? tmp$ : null;
    if (!this.get_isCancelling_0(expect))
      this.onCancellationInternal_kybjp5$(exceptionally);
    this.onCompletionInternal_cypnoy$(update, mode);
    var cause = exceptionally != null ? exceptionally.cause : null;
    if (Kotlin.isType(expect, JobNode)) {
      try {
        expect.invoke(cause);
      }
       catch (ex) {
        if (Kotlin.isType(ex, Throwable)) {
          this.handleException_tcv7n7$(new CompletionHandlerException('Exception in completion handler ' + expect + ' for ' + this, ex));
        }
         else
          throw ex;
      }
    }
     else {
      (tmp$_0 = expect.list) != null ? (this.notifyCompletion_0(tmp$_0, cause), Unit) : null;
    }
  };
  JobSupport.prototype.notifyHandlers_0 = wrapFunction(function () {
    var equals = Kotlin.equals;
    return function (T_0, isT, list, cause) {
      var tmp$;
      var exception = {v: null};
      var cur = list._next;
      while (!equals(cur, list)) {
        if (isT(cur)) {
          var node = cur;
          var tmp$_0;
          try {
            node.invoke(cause);
          }
           catch (ex) {
            if (Kotlin.isType(ex, Throwable)) {
              var tmp$_1;
              if ((tmp$_0 = exception.v) != null) {
                ex;
                ex;
                tmp$_1 = tmp$_0;
              }
               else
                tmp$_1 = null;
              if (tmp$_1 == null) {
                ex;
                exception.v = new CompletionHandlerException('Exception in completion handler ' + node + ' for ' + this, ex);
              }
            }
             else
              throw ex;
          }
        }
        cur = cur._next;
      }
      if ((tmp$ = exception.v) != null) {
        this.handleException_tcv7n7$(tmp$);
      }
    };
  });
  JobSupport.prototype.notifyCompletion_0 = function ($receiver, cause) {
    var tmp$;
    var exception = {v: null};
    var cur = $receiver._next;
    while (!equals(cur, $receiver)) {
      if (Kotlin.isType(cur, JobNode)) {
        var node = cur;
        var tmp$_0;
        try {
          node.invoke(cause);
        }
         catch (ex) {
          if (Kotlin.isType(ex, Throwable)) {
            var tmp$_1;
            if ((tmp$_0 = exception.v) != null) {
              ex;
              ex;
              tmp$_1 = tmp$_0;
            }
             else
              tmp$_1 = null;
            if (tmp$_1 == null) {
              ex;
              exception.v = new CompletionHandlerException('Exception in completion handler ' + node + ' for ' + this, ex);
            }
          }
           else
            throw ex;
        }
      }
      cur = cur._next;
    }
    if ((tmp$ = exception.v) != null) {
      this.handleException_tcv7n7$(tmp$);
    }
  };
  JobSupport.prototype.notifyCancellation_0 = function (list, cause) {
    var tmp$;
    var exception = {v: null};
    var cur = list._next;
    while (!equals(cur, list)) {
      if (Kotlin.isType(cur, JobCancellationNode)) {
        var node = cur;
        var tmp$_0;
        try {
          node.invoke(cause);
        }
         catch (ex) {
          if (Kotlin.isType(ex, Throwable)) {
            var tmp$_1;
            if ((tmp$_0 = exception.v) != null) {
              ex;
              ex;
              tmp$_1 = tmp$_0;
            }
             else
              tmp$_1 = null;
            if (tmp$_1 == null) {
              ex;
              exception.v = new CompletionHandlerException('Exception in completion handler ' + node + ' for ' + this, ex);
            }
          }
           else
            throw ex;
        }
      }
      cur = cur._next;
    }
    if ((tmp$ = exception.v) != null) {
      this.handleException_tcv7n7$(tmp$);
    }
  };
  JobSupport.prototype.start = function () {
    while (true) {
      switch (this.startInternal_0(this.state_8be2vx$)) {
        case 0:
          return false;
        case 1:
          return true;
      }
    }
  };
  JobSupport.prototype.startInternal_0 = function (state) {
    if (Kotlin.isType(state, Empty)) {
      if (state.isActive)
        return 0;
      if (!this._state_0.compareAndSet_xwzc9q$(state, EmptyActive))
        return -1;
      this.onStartInternal();
      return 1;
    }
     else if (Kotlin.isType(state, NodeList)) {
      var $receiver = state.tryMakeActive();
      if ($receiver === 1)
        this.onStartInternal();
      return $receiver;
    }
     else
      return 0;
  };
  JobSupport.prototype.onStartInternal = function () {
  };
  JobSupport.prototype.getCancellationException = function () {
    var tmp$;
    var state = this.state_8be2vx$;
    if (Kotlin.isType(state, JobSupport$Finishing) && state.cancelled != null)
      tmp$ = this.toCancellationException_0(state.cancelled.cause, 'Job is being cancelled');
    else if (Kotlin.isType(state, Incomplete)) {
      throw IllegalStateException_init(('Job was not completed or cancelled yet: ' + this).toString());
    }
     else if (Kotlin.isType(state, CompletedExceptionally))
      tmp$ = this.toCancellationException_0(state.cause, 'Job has failed');
    else
      tmp$ = new JobCancellationException('Job has completed normally', null, this);
    return tmp$;
  };
  JobSupport.prototype.toCancellationException_0 = function ($receiver, message) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, CancellationException) ? tmp$ : null) != null ? tmp$_0 : new JobCancellationException(message, $receiver, this);
  };
  JobSupport.prototype.getCompletionCause_0 = function () {
    var tmp$;
    var state = this.state_8be2vx$;
    if (Kotlin.isType(state, JobSupport$Finishing) && state.cancelled != null)
      tmp$ = state.cancelled.cause;
    else if (Kotlin.isType(state, Incomplete)) {
      throw IllegalStateException_init('Job was not completed or cancelled yet'.toString());
    }
     else if (Kotlin.isType(state, CompletedExceptionally))
      tmp$ = state.cause;
    else
      tmp$ = null;
    return tmp$;
  };
  JobSupport.prototype.invokeOnCompletion_f05bi3$ = function (handler) {
    return this.invokeOnCompletion_ct2b2z$(false, true, handler);
  };
  JobSupport.prototype.invokeOnCompletion_1tj72s$ = function (handler, onCancelling) {
    return this.invokeOnCompletion_ct2b2z$(onCancelling, true, handler);
  };
  JobSupport.prototype.invokeOnCompletion_h883ze$$default = function (onCancelling_, handler) {
    return this.invokeOnCompletion_ct2b2z$(onCancelling_, true, handler);
  };
  JobSupport.prototype.invokeOnCompletion_ct2b2z$$default = function (onCancelling, invokeImmediately, handler) {
    var nodeCache = {v: null};
    while (true) {
      var state = this.state_8be2vx$;
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
      if (Kotlin.isType(state, Empty))
        if (state.isActive) {
          var tmp$_4;
          if ((tmp$ = nodeCache.v) != null)
            tmp$_4 = tmp$;
          else {
            var $receiver = this.makeNode_0(handler, onCancelling);
            nodeCache.v = $receiver;
            tmp$_4 = $receiver;
          }
          var node = tmp$_4;
          if (this._state_0.compareAndSet_xwzc9q$(state, node))
            return node;
        }
         else
          this.promoteEmptyToNodeList_0(state);
      else if (Kotlin.isType(state, Incomplete)) {
        var list = state.list;
        if (list == null) {
          this.promoteSingleToNodeList_0(Kotlin.isType(tmp$_0 = state, JobNode) ? tmp$_0 : throwCCE());
        }
         else {
          if (Kotlin.isType(state, JobSupport$Finishing) && state.cancelled != null && onCancelling) {
            if (invokeImmediately)
              handler(state.cancelled.cause);
            return NonDisposableHandle_getInstance();
          }
          var tmp$_5;
          if ((tmp$_1 = nodeCache.v) != null)
            tmp$_5 = tmp$_1;
          else {
            var $receiver_0 = this.makeNode_0(handler, onCancelling);
            nodeCache.v = $receiver_0;
            tmp$_5 = $receiver_0;
          }
          var node_0 = tmp$_5;
          if (this.addLastAtomic_0(state, list, node_0))
            return node_0;
        }
      }
       else {
        if (invokeImmediately) {
          invokeIt(handler, (tmp$_3 = Kotlin.isType(tmp$_2 = state, CompletedExceptionally) ? tmp$_2 : null) != null ? tmp$_3.cause : null);
        }
        return NonDisposableHandle_getInstance();
      }
    }
  };
  JobSupport.prototype.makeNode_0 = function (handler, onCancelling) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    var tmp$_5;
    if (onCancelling) {
      var tmp$_6;
      if ((tmp$_0 = Kotlin.isType(tmp$ = handler, JobCancellationNode) ? tmp$ : null) != null) {
        if (!(tmp$_0.job === this)) {
          var message = 'Failed requirement.';
          throw IllegalArgumentException_init(message.toString());
        }
        tmp$_6 = tmp$_0;
      }
       else
        tmp$_6 = null;
      tmp$_5 = (tmp$_1 = tmp$_6) != null ? tmp$_1 : new InvokeOnCancellation(this, handler);
    }
     else {
      var tmp$_7;
      if ((tmp$_3 = Kotlin.isType(tmp$_2 = handler, JobNode) ? tmp$_2 : null) != null) {
        if (!(tmp$_3.job === this && !Kotlin.isType(tmp$_3, JobCancellationNode))) {
          var message_0 = 'Failed requirement.';
          throw IllegalArgumentException_init(message_0.toString());
        }
        tmp$_7 = tmp$_3;
      }
       else
        tmp$_7 = null;
      tmp$_5 = (tmp$_4 = tmp$_7) != null ? tmp$_4 : new InvokeOnCompletion(this, handler);
    }
    return tmp$_5;
  };
  function JobSupport$addLastAtomic$lambda(this$JobSupport, closure$expect) {
    return function () {
      return this$JobSupport.state_8be2vx$ === closure$expect;
    };
  }
  JobSupport.prototype.addLastAtomic_0 = function (expect, list, node) {
    var addLastIf_mo06xv$result;
    addLastIf_mo06xv$break: do {
      if (!JobSupport$addLastAtomic$lambda(this, expect)()) {
        addLastIf_mo06xv$result = false;
        break addLastIf_mo06xv$break;
      }
      list.addLast_tsj8n4$(node);
      addLastIf_mo06xv$result = true;
    }
     while (false);
    return addLastIf_mo06xv$result;
  };
  JobSupport.prototype.promoteEmptyToNodeList_0 = function (state) {
    this._state_0.compareAndSet_xwzc9q$(state, new NodeList(state.isActive));
  };
  JobSupport.prototype.promoteSingleToNodeList_0 = function (state) {
    state.addOneIfEmpty_tsj8n4$(new NodeList(true));
    var list = state._next;
    this._state_0.compareAndSet_xwzc9q$(state, list);
  };
  function JobSupport$join$lambda(cont) {
    checkCompletion(cont.context);
    return Unit;
  }
  JobSupport.prototype.join = function (continuation_0, suspended) {
    var instance = new Coroutine$join(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$join($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$join.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$join.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$join.prototype.constructor = Coroutine$join;
  Coroutine$join.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (!this.$this.joinInternal_0()) {
              this.state_0 = 2;
              this.result_0 = JobSupport$join$lambda(this.facade);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.$this.joinSuspend_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            return this.result_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  JobSupport.prototype.joinInternal_0 = function () {
    while (true) {
      var state = this.state_8be2vx$;
      if (!Kotlin.isType(state, Incomplete))
        return false;
      if (this.startInternal_0(state) >= 0)
        return true;
    }
  };
  function JobSupport$joinSuspend$lambda(this$JobSupport) {
    return function (cont) {
      disposeOnCancellation(cont, this$JobSupport.invokeOnCompletion_f05bi3$(new ResumeOnCompletion(this$JobSupport, cont)));
      return Unit;
    };
  }
  function suspendCancellableCoroutine$lambda_1(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 1);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  JobSupport.prototype.joinSuspend_0 = function (continuation) {
    return suspendCancellableCoroutine$lambda_1(false, JobSupport$joinSuspend$lambda(this))(continuation.facade);
  };
  Object.defineProperty(JobSupport.prototype, 'onJoin', {
    get: function () {
      return this;
    }
  });
  JobSupport.prototype.registerSelectClause0_f8j5hl$ = function (select, block) {
    while (true) {
      var state = this.state_8be2vx$;
      if (select.isSelected)
        return;
      if (!Kotlin.isType(state, Incomplete)) {
        if (select.trySelect_s8jyv4$(null)) {
          checkCompletion(select.completion.context);
          startCoroutineUndispatched(block, select.completion);
        }
        return;
      }
      if (this.startInternal_0(state) === 0) {
        select.disposeOnSelect_lo7ng2$(this.invokeOnCompletion_f05bi3$(new SelectJoinOnCompletion(this, select, block)));
        return;
      }
    }
  };
  JobSupport.prototype.removeNode_29b37s$ = function (node) {
    while (true) {
      var state = this.state_8be2vx$;
      if (Kotlin.isType(state, JobNode)) {
        if (state !== node)
          return;
        if (this._state_0.compareAndSet_xwzc9q$(state, EmptyActive))
          return;
      }
       else if (Kotlin.isType(state, Incomplete)) {
        if (state.list != null)
          node.remove();
        return;
      }
       else
        return;
    }
  };
  Object.defineProperty(JobSupport.prototype, 'onCancelMode', {
    get: function () {
      return 0;
    }
  });
  JobSupport.prototype.cancel_dbl4no$$default = function (cause) {
    switch (this.onCancelMode) {
      case 0:
        return this.makeCancelling_0(cause);
      case 1:
        return this.makeCompletingOnCancel_0(cause);
      default:throw IllegalStateException_init(('Invalid onCancelMode ' + this.onCancelMode).toString());
    }
  };
  JobSupport.prototype.updateStateCancelled_0 = function (state, cause) {
    return this.updateState_0(state, new Cancelled(this, cause), 0);
  };
  JobSupport.prototype.makeCancelling_0 = function (cause) {
    while (true) {
      var state = this.state_8be2vx$;
      if (Kotlin.isType(state, Empty))
        if (state.isActive) {
          this.promoteEmptyToNodeList_0(state);
        }
         else {
          if (this.updateStateCancelled_0(state, cause))
            return true;
        }
       else if (Kotlin.isType(state, JobNode))
        this.promoteSingleToNodeList_0(state);
      else if (Kotlin.isType(state, NodeList))
        if (state.isActive) {
          if (this.tryMakeCancelling_0(state, state.list, cause))
            return true;
        }
         else {
          if (this.updateStateCancelled_0(state, cause))
            return true;
        }
       else if (Kotlin.isType(state, JobSupport$Finishing)) {
        if (state.cancelled != null)
          return false;
        if (this.tryMakeCancelling_0(state, state.list, cause))
          return true;
      }
       else {
        return false;
      }
    }
  };
  JobSupport.prototype.tryMakeCancelling_0 = function (expect, list, cause) {
    var cancelled = new Cancelled(this, cause);
    if (!this._state_0.compareAndSet_xwzc9q$(expect, new JobSupport$Finishing(list, cancelled, false)))
      return false;
    this.onFinishingInternal_s8jyv4$(cancelled);
    this.onCancellationInternal_kybjp5$(cancelled);
    this.notifyCancellation_0(list, cause);
    return true;
  };
  JobSupport.prototype.makeCompletingOnCancel_0 = function (cause) {
    return this.makeCompleting_8ea4ql$(new Cancelled(this, cause));
  };
  JobSupport.prototype.makeCompleting_8ea4ql$ = function (proposedUpdate) {
    if (this.makeCompletingInternal_0(proposedUpdate, 0) === 0)
      return false;
    else
      return true;
  };
  JobSupport.prototype.makeCompletingOnce_42w2xh$ = function (proposedUpdate, mode) {
    switch (this.makeCompletingInternal_0(proposedUpdate, mode)) {
      case 1:
        return true;
      case 2:
        return false;
      default:throw IllegalStateException_0('Job ' + this + ' is already complete or completing, ' + ('but is being completed with ' + toString(proposedUpdate)), this.get_exceptionOrNull_0(proposedUpdate));
    }
  };
  JobSupport.prototype.makeCompletingInternal_0 = function (proposedUpdate, mode) {
    while (true) {
      var state = this.state_8be2vx$;
      block$break: do {
        var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5;
        if (!Kotlin.isType(state, Incomplete))
          return 0;
        if (Kotlin.isType(state, JobSupport$Finishing) && state.completing)
          return 0;
        tmp$_0 = this.firstChild_0(state);
        if (tmp$_0 == null) {
          if (!Kotlin.isType(state, JobSupport$Finishing) && this.hasOnFinishingHandler_s8jyv4$(proposedUpdate))
            tmp$ = null;
          else if (this.updateState_0(state, proposedUpdate, mode))
            return 1;
          else
            break block$break;
          tmp$_0 = tmp$;
        }
        var child = tmp$_0;
        tmp$_1 = state.list;
        if (tmp$_1 == null) {
          if (Kotlin.isType(state, Empty)) {
            this.promoteEmptyToNodeList_0(state);
            break block$break;
          }
           else if (Kotlin.isType(state, JobNode)) {
            this.promoteSingleToNodeList_0(state);
            break block$break;
          }
           else {
            throw IllegalStateException_init(('Unexpected state with an empty list: ' + toString(state)).toString());
          }
        }
        var list = tmp$_1;
        if (Kotlin.isType(proposedUpdate, CompletedExceptionally))
          child != null ? (this.cancelChildrenInternal_0(child, proposedUpdate.cause), Unit) : null;
        var cancelled = (tmp$_5 = (tmp$_3 = Kotlin.isType(tmp$_2 = state, JobSupport$Finishing) ? tmp$_2 : null) != null ? tmp$_3.cancelled : null) != null ? tmp$_5 : Kotlin.isType(tmp$_4 = proposedUpdate, Cancelled) ? tmp$_4 : null;
        var completing = new JobSupport$Finishing(list, cancelled, true);
        if (this._state_0.compareAndSet_xwzc9q$(state, completing)) {
          if (!Kotlin.isType(state, JobSupport$Finishing))
            this.onFinishingInternal_s8jyv4$(proposedUpdate);
          if (child != null && this.tryWaitForChild_0(child, proposedUpdate))
            return 2;
          if (this.updateState_0(completing, proposedUpdate, 0))
            return 1;
        }
      }
       while (false);
    }
  };
  JobSupport.prototype.cancelChildrenInternal_0 = function ($receiver, cause) {
    var tmp$;
    $receiver.childJob.cancel_dbl4no$(new JobCancellationException('Child job was cancelled because of parent failure', cause, $receiver.childJob));
    (tmp$ = this.nextChild_0($receiver)) != null ? (this.cancelChildrenInternal_0(tmp$, cause), Unit) : null;
  };
  JobSupport.prototype.get_exceptionOrNull_0 = function ($receiver) {
    var tmp$, tmp$_0;
    return (tmp$_0 = Kotlin.isType(tmp$ = $receiver, CompletedExceptionally) ? tmp$ : null) != null ? tmp$_0.cause : null;
  };
  JobSupport.prototype.firstChild_0 = function (state) {
    var tmp$, tmp$_0, tmp$_1;
    return (tmp$_1 = Kotlin.isType(tmp$ = state, ChildJob) ? tmp$ : null) != null ? tmp$_1 : (tmp$_0 = state.list) != null ? this.nextChild_0(tmp$_0) : null;
  };
  JobSupport.prototype.tryWaitForChild_0 = function (child, proposedUpdate) {
    var tmp$;
    var handle = child.childJob.invokeOnCompletion_ct2b2z$(void 0, false, new ChildCompletion(this, child, proposedUpdate));
    if (handle !== NonDisposableHandle_getInstance())
      return true;
    tmp$ = this.nextChild_0(child);
    if (tmp$ == null) {
      return false;
    }
    var nextChild = tmp$;
    return this.tryWaitForChild_0(nextChild, proposedUpdate);
  };
  JobSupport.prototype.continueCompleting_tsdog4$ = function (lastChild, proposedUpdate) {
    while (true) {
      var state = this.state_8be2vx$;
      if (!Kotlin.isType(state, JobSupport$Finishing))
        throw IllegalStateException_0('Job ' + this + ' is found in expected state while completing with ' + toString(proposedUpdate), this.get_exceptionOrNull_0(proposedUpdate));
      var waitChild = this.nextChild_0(lastChild);
      if (waitChild != null && this.tryWaitForChild_0(waitChild, proposedUpdate))
        return;
      if (this.updateState_0(state, proposedUpdate, 0))
        return;
    }
  };
  JobSupport.prototype.nextChild_0 = function ($receiver) {
    var cur = $receiver;
    while (cur._removed) {
      cur = cur._prev;
    }
    while (true) {
      cur = cur._next;
      if (cur._removed)
        continue;
      if (Kotlin.isType(cur, ChildJob))
        return cur;
      if (Kotlin.isType(cur, NodeList))
        return null;
    }
  };
  function JobSupport$get_JobSupport$children$lambda(this$JobSupport_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$JobSupport$get_JobSupport$children$lambda(this$JobSupport_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$JobSupport$get_JobSupport$children$lambda(this$JobSupport_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$JobSupport = this$JobSupport_0;
    this.local$tmp$ = void 0;
    this.local$tmp$_0 = void 0;
    this.local$cur = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$JobSupport$get_JobSupport$children$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$JobSupport$get_JobSupport$children$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$JobSupport$get_JobSupport$children$lambda.prototype.constructor = Coroutine$JobSupport$get_JobSupport$children$lambda;
  Coroutine$JobSupport$get_JobSupport$children$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var state = this.local$this$JobSupport.state_8be2vx$;
            if (Kotlin.isType(state, ChildJob)) {
              this.state_0 = 8;
              this.result_0 = this.local$$receiver.yield_11rb$(state.childJob, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              if (Kotlin.isType(state, Incomplete)) {
                if ((this.local$tmp$ = state.list) != null) {
                  this.local$cur = this.local$tmp$._next;
                  this.state_0 = 2;
                  continue;
                }
                 else {
                  this.local$tmp$_0 = null;
                  this.state_0 = 6;
                  continue;
                }
              }
               else {
                this.state_0 = 7;
                continue;
              }
            }

          case 1:
            throw this.exception_0;
          case 2:
            if (equals(this.local$cur, this.local$tmp$)) {
              this.state_0 = 5;
              continue;
            }

            if (Kotlin.isType(this.local$cur, ChildJob)) {
              this.state_0 = 3;
              this.result_0 = this.local$$receiver.yield_11rb$(this.local$cur.childJob, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 3:
            this.state_0 = 4;
            continue;
          case 4:
            this.local$cur = this.local$cur._next;
            this.state_0 = 2;
            continue;
          case 5:
            this.local$tmp$_0 = Unit;
            this.state_0 = 6;
            continue;
          case 6:
            return this.local$tmp$_0;
          case 7:
            this.state_0 = 9;
            continue;
          case 8:
            return this.result_0;
          case 9:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Object.defineProperty(JobSupport.prototype, 'children', {
    get: function () {
      return buildSequence(JobSupport$get_JobSupport$children$lambda(this));
    }
  });
  JobSupport.prototype.attachChild_r3p3g3$ = function (child) {
    return this.invokeOnCompletion_ct2b2z$(true, void 0, new ChildJob(this, child));
  };
  JobSupport.prototype.cancelChildren_dbl4no$$default = function (cause) {
    cancelChildren(this, cause);
  };
  JobSupport.prototype.handleException_tcv7n7$ = function (exception) {
    throw exception;
  };
  JobSupport.prototype.onCancellationInternal_kybjp5$ = function (exceptionally) {
  };
  JobSupport.prototype.hasOnFinishingHandler_s8jyv4$ = function (update) {
    return false;
  };
  JobSupport.prototype.onFinishingInternal_s8jyv4$ = function (update) {
  };
  JobSupport.prototype.onCompletionInternal_cypnoy$ = function (state, mode) {
  };
  JobSupport.prototype.toString = function () {
    return this.nameString() + '{' + this.stateString_0() + '}@' + get_hexAddress(this);
  };
  JobSupport.prototype.nameString = function () {
    return get_classSimpleName(this);
  };
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init;
  JobSupport.prototype.stateString_0 = function () {
    var tmp$;
    var state = this.state_8be2vx$;
    if (Kotlin.isType(state, JobSupport$Finishing)) {
      var $receiver = StringBuilder_init();
      if (state.cancelled != null)
        $receiver.append_gw00v9$('Cancelling');
      if (state.completing)
        $receiver.append_gw00v9$('Completing');
      tmp$ = $receiver.toString();
    }
     else if (Kotlin.isType(state, Incomplete))
      tmp$ = state.isActive ? 'Active' : 'New';
    else if (Kotlin.isType(state, Cancelled))
      tmp$ = 'Cancelled';
    else if (Kotlin.isType(state, CompletedExceptionally))
      tmp$ = 'CompletedExceptionally';
    else
      tmp$ = 'Completed';
    return tmp$;
  };
  function JobSupport$Finishing(list, cancelled, completing) {
    this.list_7ikv57$_0 = list;
    this.cancelled = cancelled;
    this.completing = completing;
  }
  Object.defineProperty(JobSupport$Finishing.prototype, 'list', {
    get: function () {
      return this.list_7ikv57$_0;
    }
  });
  Object.defineProperty(JobSupport$Finishing.prototype, 'isActive', {
    get: function () {
      return this.cancelled == null;
    }
  });
  JobSupport$Finishing.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Finishing',
    interfaces: [Incomplete]
  };
  JobSupport.prototype.get_isCancelling_0 = function ($receiver) {
    return Kotlin.isType($receiver, JobSupport$Finishing) && $receiver.cancelled != null;
  };
  Object.defineProperty(JobSupport.prototype, 'isCompletedExceptionally', {
    get: function () {
      return Kotlin.isType(this.state_8be2vx$, CompletedExceptionally);
    }
  });
  JobSupport.prototype.getCompletionExceptionOrNull = function () {
    var state = this.state_8be2vx$;
    if (!!Kotlin.isType(state, Incomplete)) {
      var message = 'This job has not completed yet';
      throw IllegalStateException_init(message.toString());
    }
    return this.get_exceptionOrNull_0(state);
  };
  JobSupport.prototype.getCompletedInternal_8be2vx$ = function () {
    var state = this.state_8be2vx$;
    if (!!Kotlin.isType(state, Incomplete)) {
      var message = 'This job has not completed yet';
      throw IllegalStateException_init(message.toString());
    }
    if (Kotlin.isType(state, CompletedExceptionally))
      throw state.cause;
    return state;
  };
  JobSupport.prototype.awaitInternal_8be2vx$ = function (continuation) {
    while (true) {
      var state = this.state_8be2vx$;
      if (!Kotlin.isType(state, Incomplete)) {
        if (Kotlin.isType(state, CompletedExceptionally))
          throw state.cause;
        return state;
      }
      if (this.startInternal_0(state) >= 0)
        break;
    }
    return this.awaitSuspend_0(continuation);
  };
  function JobSupport$awaitSuspend$lambda$lambda(this$JobSupport, closure$cont) {
    return function (it) {
      var state = this$JobSupport.state_8be2vx$;
      if (!!Kotlin.isType(state, Incomplete)) {
        var message = 'Check failed.';
        throw IllegalStateException_init(message.toString());
      }
      if (Kotlin.isType(state, CompletedExceptionally))
        closure$cont.resumeWithException_tcv7n7$(state.cause);
      else
        closure$cont.resume_11rb$(state);
      return Unit;
    };
  }
  function JobSupport$awaitSuspend$lambda(this$JobSupport) {
    return function (cont) {
      disposeOnCancellation(cont, this$JobSupport.invokeOnCompletion_f05bi3$(JobSupport$awaitSuspend$lambda$lambda(this$JobSupport, cont)));
      return Unit;
    };
  }
  JobSupport.prototype.awaitSuspend_0 = function (continuation) {
    return suspendCancellableCoroutine$lambda_1(false, JobSupport$awaitSuspend$lambda(this))(continuation.facade);
  };
  JobSupport.prototype.registerSelectClause1Internal_noo60r$ = function (select, block) {
    while (true) {
      var state = this.state_8be2vx$;
      var tmp$;
      if (select.isSelected)
        return;
      if (!Kotlin.isType(state, Incomplete)) {
        if (select.trySelect_s8jyv4$(null)) {
          if (Kotlin.isType(state, CompletedExceptionally))
            select.resumeSelectCancellableWithException_tcv7n7$(state.cause);
          else {
            startCoroutineUndispatched_0(block, (tmp$ = state) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), select.completion);
          }
        }
        return;
      }
      if (this.startInternal_0(state) === 0) {
        select.disposeOnSelect_lo7ng2$(this.invokeOnCompletion_f05bi3$(new SelectAwaitOnCompletion(this, select, block)));
        return;
      }
    }
  };
  JobSupport.prototype.selectAwaitCompletion_noo60r$ = function (select, block) {
    var tmp$;
    var state = this.state_8be2vx$;
    if (Kotlin.isType(state, CompletedExceptionally))
      select.resumeSelectCancellableWithException_tcv7n7$(state.cause);
    else {
      startCoroutineCancellable_0(block, (tmp$ = state) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), select.completion);
    }
  };
  JobSupport.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JobSupport',
    interfaces: [SelectClause0, Job]
  };
  var ON_CANCEL_MAKE_CANCELLING;
  var ON_CANCEL_MAKE_COMPLETING;
  var COMPLETING_ALREADY_COMPLETING;
  var COMPLETING_COMPLETED;
  var COMPLETING_WAITING_CHILDREN;
  var RETRY;
  var FALSE;
  var TRUE;
  var EmptyNew;
  var EmptyActive;
  function Empty(isActive) {
    this.isActive_6b1bzz$_0 = isActive;
  }
  Object.defineProperty(Empty.prototype, 'isActive', {
    get: function () {
      return this.isActive_6b1bzz$_0;
    }
  });
  Object.defineProperty(Empty.prototype, 'list', {
    get: function () {
      return null;
    }
  });
  Empty.prototype.toString = function () {
    return 'Empty{' + (this.isActive ? 'Active' : 'New') + '}';
  };
  Empty.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Empty',
    interfaces: [Incomplete]
  };
  function JobImpl(parent) {
    if (parent === void 0)
      parent = null;
    JobSupport.call(this, true);
    this.initParentJobInternal_x4lgmv$(parent);
  }
  Object.defineProperty(JobImpl.prototype, 'onCancelMode', {
    get: function () {
      return 1;
    }
  });
  JobImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JobImpl',
    interfaces: [JobSupport]
  };
  function Incomplete() {
  }
  Incomplete.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Incomplete',
    interfaces: []
  };
  function JobNode(job) {
    CompletionHandlerBase.call(this);
    this.job = job;
  }
  Object.defineProperty(JobNode.prototype, 'isActive', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(JobNode.prototype, 'list', {
    get: function () {
      return null;
    }
  });
  JobNode.prototype.dispose = function () {
    var tmp$;
    (Kotlin.isType(tmp$ = this.job, JobSupport) ? tmp$ : throwCCE()).removeNode_29b37s$(this);
  };
  JobNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JobNode',
    interfaces: [Incomplete, DisposableHandle, CompletionHandlerBase]
  };
  function NodeList(active) {
    LinkedListHead.call(this);
    this._active_0 = atomic(active ? 1 : 0);
  }
  Object.defineProperty(NodeList.prototype, 'isActive', {
    get: function () {
      return this._active_0.value !== 0;
    }
  });
  Object.defineProperty(NodeList.prototype, 'list', {
    get: function () {
      return this;
    }
  });
  NodeList.prototype.tryMakeActive = function () {
    if (this._active_0.value !== 0)
      return 0;
    if (this._active_0.compareAndSet_vux9f0$(0, 1))
      return 1;
    return -1;
  };
  NodeList.prototype.toString = function () {
    var $receiver = StringBuilder_init();
    $receiver.append_gw00v9$('List');
    $receiver.append_gw00v9$(this.isActive ? '{Active}' : '{New}');
    $receiver.append_gw00v9$('[');
    var first = {v: true};
    var cur = this._next;
    while (!equals(cur, this)) {
      if (Kotlin.isType(cur, JobNode)) {
        var node = cur;
        if (first.v)
          first.v = false;
        else
          $receiver.append_gw00v9$(', ');
        $receiver.append_s8jyv4$(node);
      }
      cur = cur._next;
    }
    $receiver.append_gw00v9$(']');
    return $receiver.toString();
  };
  NodeList.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NodeList',
    interfaces: [Incomplete, LinkedListHead]
  };
  function InvokeOnCompletion(job, handler) {
    JobNode.call(this, job);
    this.handler_0 = handler;
  }
  InvokeOnCompletion.prototype.invoke = function (cause) {
    this.handler_0(cause);
  };
  InvokeOnCompletion.prototype.toString = function () {
    return 'InvokeOnCompletion[' + get_classSimpleName(this) + '@' + get_hexAddress(this) + ']';
  };
  InvokeOnCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InvokeOnCompletion',
    interfaces: [JobNode]
  };
  function ResumeOnCompletion(job, continuation) {
    JobNode.call(this, job);
    this.continuation_0 = continuation;
  }
  ResumeOnCompletion.prototype.invoke = function (cause) {
    this.continuation_0.resume_11rb$(Unit);
  };
  ResumeOnCompletion.prototype.toString = function () {
    return 'ResumeOnCompletion[' + this.continuation_0 + ']';
  };
  ResumeOnCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ResumeOnCompletion',
    interfaces: [JobNode]
  };
  function DisposeOnCompletion(job, handle) {
    JobNode.call(this, job);
    this.handle_0 = handle;
  }
  DisposeOnCompletion.prototype.invoke = function (cause) {
    this.handle_0.dispose();
  };
  DisposeOnCompletion.prototype.toString = function () {
    return 'DisposeOnCompletion[' + this.handle_0 + ']';
  };
  DisposeOnCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DisposeOnCompletion',
    interfaces: [JobNode]
  };
  function SelectJoinOnCompletion(job, select, block) {
    JobNode.call(this, job);
    this.select_0 = select;
    this.block_0 = block;
  }
  SelectJoinOnCompletion.prototype.invoke = function (cause) {
    if (this.select_0.trySelect_s8jyv4$(null))
      startCoroutineCancellable(this.block_0, this.select_0.completion);
  };
  SelectJoinOnCompletion.prototype.toString = function () {
    return 'SelectJoinOnCompletion[' + this.select_0 + ']';
  };
  SelectJoinOnCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SelectJoinOnCompletion',
    interfaces: [JobNode]
  };
  function SelectAwaitOnCompletion(job, select, block) {
    JobNode.call(this, job);
    this.select_0 = select;
    this.block_0 = block;
  }
  SelectAwaitOnCompletion.prototype.invoke = function (cause) {
    if (this.select_0.trySelect_s8jyv4$(null))
      this.job.selectAwaitCompletion_noo60r$(this.select_0, this.block_0);
  };
  SelectAwaitOnCompletion.prototype.toString = function () {
    return 'SelectAwaitOnCompletion[' + this.select_0 + ']';
  };
  SelectAwaitOnCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SelectAwaitOnCompletion',
    interfaces: [JobNode]
  };
  function JobCancellationNode(job) {
    JobNode.call(this, job);
  }
  JobCancellationNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JobCancellationNode',
    interfaces: [JobNode]
  };
  function InvokeOnCancellation(job, handler) {
    JobCancellationNode.call(this, job);
    this.handler_0 = handler;
    this._invoked_0 = atomic(0);
  }
  InvokeOnCancellation.prototype.invoke = function (cause) {
    if (this._invoked_0.compareAndSet_vux9f0$(0, 1))
      this.handler_0(cause);
  };
  InvokeOnCancellation.prototype.toString = function () {
    return 'InvokeOnCancellation[' + get_classSimpleName(this) + '@' + get_hexAddress(this) + ']';
  };
  InvokeOnCancellation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'InvokeOnCancellation',
    interfaces: [JobCancellationNode]
  };
  function ChildJob(parent, childJob) {
    JobCancellationNode.call(this, parent);
    this.childJob = childJob;
  }
  ChildJob.prototype.invoke = function (cause) {
    this.childJob.cancel_dbl4no$(this.job.getCancellationException());
  };
  ChildJob.prototype.toString = function () {
    return 'ChildJob[' + this.childJob + ']';
  };
  ChildJob.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ChildJob',
    interfaces: [JobCancellationNode]
  };
  function ChildContinuation(parent, child) {
    JobCancellationNode.call(this, parent);
    this.child = child;
  }
  ChildContinuation.prototype.invoke = function (cause) {
    this.child.cancel_dbl4no$(this.job.getCancellationException());
  };
  ChildContinuation.prototype.toString = function () {
    return 'ChildContinuation[' + this.child + ']';
  };
  ChildContinuation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ChildContinuation',
    interfaces: [JobCancellationNode]
  };
  function ChildCompletion(parent, child, proposedUpdate) {
    JobNode.call(this, child.childJob);
    this.parent_0 = parent;
    this.child_0 = child;
    this.proposedUpdate_0 = proposedUpdate;
  }
  ChildCompletion.prototype.invoke = function (cause) {
    this.parent_0.continueCompleting_tsdog4$(this.child_0, this.proposedUpdate_0);
  };
  ChildCompletion.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ChildCompletion',
    interfaces: [JobNode]
  };
  function NonCancellable() {
    NonCancellable_instance = this;
    AbstractCoroutineContextElement.call(this, Job$Key_getInstance());
  }
  Object.defineProperty(NonCancellable.prototype, 'isActive', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(NonCancellable.prototype, 'isCompleted', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(NonCancellable.prototype, 'isCancelled', {
    get: function () {
      return false;
    }
  });
  NonCancellable.prototype.start = function () {
    return false;
  };
  NonCancellable.prototype.join = function (continuation) {
    throw UnsupportedOperationException_init('This job is always active');
  };
  Object.defineProperty(NonCancellable.prototype, 'onJoin', {
    get: function () {
      throw UnsupportedOperationException_init('This job is always active');
    }
  });
  NonCancellable.prototype.getCancellationException = function () {
    throw IllegalStateException_init('This job is always active');
  };
  NonCancellable.prototype.invokeOnCompletion_f05bi3$ = function (handler) {
    return NonDisposableHandle_getInstance();
  };
  NonCancellable.prototype.invokeOnCompletion_1tj72s$ = function (handler, onCancelling) {
    return NonDisposableHandle_getInstance();
  };
  NonCancellable.prototype.invokeOnCompletion_h883ze$$default = function (onCancelling_, handler) {
    return NonDisposableHandle_getInstance();
  };
  NonCancellable.prototype.invokeOnCompletion_ct2b2z$$default = function (onCancelling, invokeImmediately, handler) {
    return NonDisposableHandle_getInstance();
  };
  NonCancellable.prototype.cancel_dbl4no$$default = function (cause) {
    return false;
  };
  Object.defineProperty(NonCancellable.prototype, 'children', {
    get: function () {
      return emptySequence();
    }
  });
  NonCancellable.prototype.attachChild_r3p3g3$ = function (child) {
    return NonDisposableHandle_getInstance();
  };
  NonCancellable.prototype.cancelChildren_dbl4no$$default = function (cause) {
  };
  NonCancellable.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'NonCancellable',
    interfaces: [Job, AbstractCoroutineContextElement]
  };
  var NonCancellable_instance = null;
  function NonCancellable_getInstance() {
    if (NonCancellable_instance === null) {
      new NonCancellable();
    }
    return NonCancellable_instance;
  }
  var MODE_ATOMIC_DEFAULT;
  var MODE_CANCELLABLE;
  var MODE_DIRECT;
  var MODE_UNDISPATCHED;
  var MODE_IGNORE;
  function get_isCancellableMode($receiver) {
    return $receiver === 1;
  }
  function get_isDispatchedMode($receiver) {
    return $receiver === 0 || $receiver === 1;
  }
  function resumeMode($receiver, value, mode) {
    var tmp$;
    switch (mode) {
      case 0:
        $receiver.resume_11rb$(value);
        break;
      case 1:
        resumeCancellable($receiver, value);
        break;
      case 2:
        resumeDirect($receiver, value);
        break;
      case 3:
        var $this = Kotlin.isType(tmp$ = $receiver, DispatchedContinuation) ? tmp$ : throwCCE();
        $this.context;
        $this.continuation.resume_11rb$(value);
        break;
      case 4:
        break;
      default:throw IllegalStateException_init(('Invalid mode ' + mode).toString());
    }
  }
  function resumeWithExceptionMode($receiver, exception, mode) {
    var tmp$;
    switch (mode) {
      case 0:
        $receiver.resumeWithException_tcv7n7$(exception);
        break;
      case 1:
        resumeCancellableWithException($receiver, exception);
        break;
      case 2:
        resumeDirectWithException($receiver, exception);
        break;
      case 3:
        var $this = Kotlin.isType(tmp$ = $receiver, DispatchedContinuation) ? tmp$ : throwCCE();
        $this.context;
        $this.continuation.resumeWithException_tcv7n7$(exception);
        break;
      case 4:
        break;
      default:throw IllegalStateException_init(('Invalid mode ' + mode).toString());
    }
  }
  function withTimeout(time, block, continuation) {
    return withTimeout_0(Kotlin.Long.fromInt(time), TimeUnit$MILLISECONDS_getInstance(), block, continuation);
  }
  function withTimeout$lambda(closure$time, closure$unit, closure$block) {
    return function (cont) {
      return setupTimeout(new TimeoutCoroutine(closure$time, closure$unit, cont), closure$block);
    };
  }
  function withTimeout_0(time, unit, block, continuation) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    if (time.compareTo_11rb$(L0) <= 0)
      throw new CancellationException('Timed out immediately');
    return withTimeout$lambda(time, unit, block)(continuation.facade);
  }
  function setupTimeout(coroutine, block) {
    var cont = coroutine.cont;
    var context = cont.context;
    disposeOnCompletion_0(coroutine, get_delay(context).invokeOnTimeout_myg4gi$(coroutine.time, coroutine.unit, coroutine));
    return startUndispatchedOrReturn_0(coroutine, coroutine, block);
  }
  function withTimeout$lambda_0(closure$block_0) {
    return function ($receiver, continuation_0, suspended) {
      var instance = new Coroutine$withTimeout$lambda(closure$block_0, $receiver, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$withTimeout$lambda(closure$block_0, $receiver, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$block = closure$block_0;
  }
  Coroutine$withTimeout$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withTimeout$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withTimeout$lambda.prototype.constructor = Coroutine$withTimeout$lambda;
  Coroutine$withTimeout$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$closure$block(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function withTimeout_1(time, unit, block, continuation) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    return withTimeout_0(time, unit, withTimeout$lambda_0(block), continuation);
  }
  function TimeoutCoroutine(time, unit, cont) {
    AbstractCoroutine.call(this, cont.context, true);
    this.time = time;
    this.unit = unit;
    this.cont = cont;
  }
  Object.defineProperty(TimeoutCoroutine.prototype, 'defaultResumeMode', {
    get: function () {
      return 2;
    }
  });
  TimeoutCoroutine.prototype.run = function () {
    this.cancel_dbl4no$(TimeoutCancellationException_0(this.time, this.unit, this));
  };
  TimeoutCoroutine.prototype.onCompletionInternal_cypnoy$ = function (state, mode) {
    var tmp$;
    if (Kotlin.isType(state, CompletedExceptionally))
      resumeWithExceptionMode(this.cont, state.cause, mode);
    else {
      resumeMode(this.cont, (tmp$ = state) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), mode);
    }
  };
  TimeoutCoroutine.prototype.nameString = function () {
    return AbstractCoroutine.prototype.nameString.call(this) + '(' + this.time + ' ' + this.unit + ')';
  };
  TimeoutCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimeoutCoroutine',
    interfaces: [Runnable, AbstractCoroutine, Continuation]
  };
  function withTimeoutOrNull(time, block, continuation) {
    return withTimeoutOrNull_0(Kotlin.Long.fromInt(time), TimeUnit$MILLISECONDS_getInstance(), block, continuation);
  }
  function withTimeoutOrNull$lambda(closure$time, closure$unit, closure$block) {
    return function (cont) {
      return setupTimeout(new TimeoutOrNullCoroutine(closure$time, closure$unit, cont), closure$block);
    };
  }
  function withTimeoutOrNull_0(time, unit, block, continuation) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    if (time.compareTo_11rb$(L0) <= 0)
      return null;
    return withTimeoutOrNull$lambda(time, unit, block)(continuation.facade);
  }
  function withTimeoutOrNull$lambda_0(closure$block_0) {
    return function ($receiver, continuation_0, suspended) {
      var instance = new Coroutine$withTimeoutOrNull$lambda(closure$block_0, $receiver, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$withTimeoutOrNull$lambda(closure$block_0, $receiver, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$block = closure$block_0;
  }
  Coroutine$withTimeoutOrNull$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withTimeoutOrNull$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withTimeoutOrNull$lambda.prototype.constructor = Coroutine$withTimeoutOrNull$lambda;
  Coroutine$withTimeoutOrNull$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$closure$block(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function withTimeoutOrNull_1(time, unit, block, continuation) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    return withTimeoutOrNull_0(time, unit, withTimeoutOrNull$lambda_0(block), continuation);
  }
  function TimeoutOrNullCoroutine(time, unit, cont) {
    TimeoutCoroutine.call(this, time, unit, cont);
  }
  TimeoutOrNullCoroutine.prototype.onCompletionInternal_cypnoy$ = function (state, mode) {
    var tmp$;
    if (Kotlin.isType(state, CompletedExceptionally)) {
      var exception = state.cause;
      if (Kotlin.isType(exception, TimeoutCancellationException) && exception.coroutine_8be2vx$ === this)
        resumeMode(this.cont, null, mode);
      else
        resumeWithExceptionMode(this.cont, exception, mode);
    }
     else {
      resumeMode(this.cont, (tmp$ = state) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE(), mode);
    }
  };
  TimeoutOrNullCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimeoutOrNullCoroutine',
    interfaces: [TimeoutCoroutine]
  };
  function TimeoutCancellationException(message, coroutine) {
    CancellationException.call(this, message);
    this.coroutine_8be2vx$ = coroutine;
    this.name = 'TimeoutCancellationException';
  }
  TimeoutCancellationException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimeoutCancellationException',
    interfaces: [CancellationException]
  };
  function TimeoutCancellationException_init(message, $this) {
    $this = $this || Object.create(TimeoutCancellationException.prototype);
    TimeoutCancellationException.call($this, message, null);
    return $this;
  }
  function TimeoutCancellationException_0(time, unit, coroutine) {
    return new TimeoutCancellationException('Timed out waiting for ' + time + ' ' + unit, coroutine);
  }
  function Unconfined() {
    Unconfined_instance = this;
    CoroutineDispatcher.call(this);
  }
  Unconfined.prototype.isDispatchNeeded_dvqyjb$ = function (context) {
    return false;
  };
  Unconfined.prototype.dispatch_jts95w$ = function (context, block) {
    throw UnsupportedOperationException_init_0();
  };
  Unconfined.prototype.toString = function () {
    return 'Unconfined';
  };
  Unconfined.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Unconfined',
    interfaces: [CoroutineDispatcher]
  };
  var Unconfined_instance = null;
  function Unconfined_getInstance() {
    if (Unconfined_instance === null) {
      new Unconfined();
    }
    return Unconfined_instance;
  }
  function yield$lambda(cont) {
    var context = cont.context;
    checkCompletion(context);
    if (!Kotlin.isType(cont, DispatchedContinuation))
      return Unit;
    if (!cont.dispatcher.isDispatchNeeded_dvqyjb$(context))
      return Unit;
    cont.dispatchYield_1c3m6u$(Unit);
    return COROUTINE_SUSPENDED;
  }
  function yield_0(continuation) {
    return yield$lambda(continuation.facade);
  }
  function checkCompletion($receiver) {
    var job = $receiver.get_8oh8b3$(Job$Key_getInstance());
    if (job != null && !job.isActive)
      throw job.getCancellationException();
  }
  function AbstractSendChannel() {
    this.queue = new LinkedListHead();
  }
  AbstractSendChannel.prototype.offerInternal_11rb$ = function (element) {
    var tmp$;
    while (true) {
      tmp$ = this.takeFirstReceiveOrPeekClosed();
      if (tmp$ == null) {
        return OFFER_FAILED;
      }
      var receive = tmp$;
      var token = receive.tryResumeReceive_19pj23$(element, null);
      if (token != null) {
        receive.completeResumeReceive_za3rmp$(token);
        return receive.offerResult;
      }
    }
  };
  AbstractSendChannel.prototype.offerSelectInternal_26cf95$ = function (element, select) {
    var offerOp = this.describeTryOffer_11rb$(element);
    var failure = select.performAtomicTrySelect_qopb37$(offerOp);
    if (failure != null)
      return failure;
    var receive = offerOp.result;
    receive.completeResumeReceive_za3rmp$(ensureNotNull(offerOp.resumeToken));
    return receive.offerResult;
  };
  Object.defineProperty(AbstractSendChannel.prototype, 'closedForSend', {
    get: function () {
      var tmp$;
      return Kotlin.isType(tmp$ = this.queue._prev, Closed) ? tmp$ : null;
    }
  });
  Object.defineProperty(AbstractSendChannel.prototype, 'closedForReceive', {
    get: function () {
      var tmp$;
      return Kotlin.isType(tmp$ = this.queue._next, Closed) ? tmp$ : null;
    }
  });
  AbstractSendChannel.prototype.takeFirstSendOrPeekClosed = function () {
    var $this = this.queue;
    var removeFirstIfIsInstanceOfOrPeekIf_14urrv$result;
    removeFirstIfIsInstanceOfOrPeekIf_14urrv$break: do {
      var next = $this._next;
      if (next === $this) {
        removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = null;
        break removeFirstIfIsInstanceOfOrPeekIf_14urrv$break;
      }
      if (!Kotlin.isType(next, Send)) {
        removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = null;
        break removeFirstIfIsInstanceOfOrPeekIf_14urrv$break;
      }
      if (Kotlin.isType(next, Closed)) {
        removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = next;
        break removeFirstIfIsInstanceOfOrPeekIf_14urrv$break;
      }
      if (!next.remove()) {
        var message = 'Should remove';
        throw IllegalStateException_init(message.toString());
      }
      removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = next;
    }
     while (false);
    return removeFirstIfIsInstanceOfOrPeekIf_14urrv$result;
  };
  AbstractSendChannel.prototype.sendBuffered_11rb$ = function (element) {
    var $this = this.queue;
    var node = new AbstractSendChannel$SendBuffered(element);
    addLastIfPrev_ajzm8d$break: do {
      var prev = $this._prev;
      if (Kotlin.isType(prev, ReceiveOrClosed))
        return prev;
      if (!true) {
        false;
        break addLastIfPrev_ajzm8d$break;
      }
      $this.addLast_tsj8n4$(node);
      true;
    }
     while (false);
    return null;
  };
  AbstractSendChannel.prototype.sendConflated_11rb$ = function (element) {
    var node = new AbstractSendChannel$SendBuffered(element);
    var $this = this.queue;
    addLastIfPrev_ajzm8d$break: do {
      var prev = $this._prev;
      if (Kotlin.isType(prev, ReceiveOrClosed))
        return prev;
      if (!true) {
        false;
        break addLastIfPrev_ajzm8d$break;
      }
      $this.addLast_tsj8n4$(node);
      true;
    }
     while (false);
    this.conflatePreviousSendBuffered_tsj8n4$(node);
    return null;
  };
  AbstractSendChannel.prototype.conflatePreviousSendBuffered_tsj8n4$ = function (node) {
    var tmp$, tmp$_0;
    var prev = node._prev;
    (tmp$_0 = Kotlin.isType(tmp$ = prev, AbstractSendChannel$SendBuffered) ? tmp$ : null) != null ? tmp$_0.remove() : null;
  };
  AbstractSendChannel.prototype.describeSendBuffered_11rb$ = function (element) {
    return new AbstractSendChannel$SendBufferedDesc(this.queue, element);
  };
  function AbstractSendChannel$SendBufferedDesc(queue, element) {
    AddLastDesc.call(this, queue, new AbstractSendChannel$SendBuffered(element));
  }
  AbstractSendChannel$SendBufferedDesc.prototype.failure_b1buut$ = function (affected, next) {
    if (Kotlin.isType(affected, ReceiveOrClosed))
      return OFFER_FAILED;
    return null;
  };
  AbstractSendChannel$SendBufferedDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SendBufferedDesc',
    interfaces: [AddLastDesc]
  };
  AbstractSendChannel.prototype.describeSendConflated_11rb$ = function (element) {
    return new AbstractSendChannel$SendConflatedDesc(this.queue, element);
  };
  function AbstractSendChannel$SendConflatedDesc(queue, element) {
    AbstractSendChannel$SendBufferedDesc.call(this, queue, element);
  }
  AbstractSendChannel$SendConflatedDesc.prototype.finishOnSuccess_9p47n0$ = function (affected, next) {
    var tmp$, tmp$_0;
    AbstractSendChannel$SendBufferedDesc.prototype.finishOnSuccess_9p47n0$.call(this, affected, next);
    (tmp$_0 = Kotlin.isType(tmp$ = affected, AbstractSendChannel$SendBuffered) ? tmp$ : null) != null ? tmp$_0.remove() : null;
  };
  AbstractSendChannel$SendConflatedDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SendConflatedDesc',
    interfaces: [AbstractSendChannel$SendBufferedDesc]
  };
  Object.defineProperty(AbstractSendChannel.prototype, 'isClosedForSend', {
    get: function () {
      return this.closedForSend != null;
    }
  });
  Object.defineProperty(AbstractSendChannel.prototype, 'isFull', {
    get: function () {
      return !Kotlin.isType(this.queue._next, ReceiveOrClosed) && this.isBufferFull;
    }
  });
  AbstractSendChannel.prototype.send_11rb$ = function (element, continuation) {
    if (this.offer_11rb$(element))
      return;
    return this.sendSuspend_bupgmg$_0(element, continuation);
  };
  AbstractSendChannel.prototype.offer_11rb$ = function (element) {
    var tmp$;
    var result = this.offerInternal_11rb$(element);
    if (result === OFFER_SUCCESS)
      tmp$ = true;
    else if (result === OFFER_FAILED)
      tmp$ = false;
    else if (Kotlin.isType(result, Closed))
      throw result.sendException;
    else {
      throw IllegalStateException_init(('offerInternal returned ' + result).toString());
    }
    return tmp$;
  };
  function AbstractSendChannel$sendSuspend$lambda(closure$element, this$AbstractSendChannel) {
    return function (cont) {
      var send = new SendElement(closure$element, cont);
      loop: while (true) {
        var enqueueResult = this$AbstractSendChannel.enqueueSend_kqrzrn$_0(send);
        if (enqueueResult == null) {
          cont.initCancellability();
          removeOnCancellation(cont, send);
          return;
        }
         else if (Kotlin.isType(enqueueResult, Closed)) {
          cont.resumeWithException_tcv7n7$(enqueueResult.sendException);
          return;
        }
        var offerResult = this$AbstractSendChannel.offerInternal_11rb$(closure$element);
        if (offerResult === OFFER_SUCCESS) {
          cont.resume_11rb$(Unit);
          return;
        }
         else if (offerResult === OFFER_FAILED)
          continue loop;
        else if (Kotlin.isType(offerResult, Closed)) {
          cont.resumeWithException_tcv7n7$(offerResult.sendException);
          return;
        }
         else {
          throw IllegalStateException_init(('offerInternal returned ' + offerResult).toString());
        }
      }
    };
  }
  function suspendAtomicCancellableCoroutine$lambda(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 0);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  AbstractSendChannel.prototype.sendSuspend_bupgmg$_0 = function (element, continuation) {
    return suspendAtomicCancellableCoroutine$lambda(true, AbstractSendChannel$sendSuspend$lambda(element, this))(continuation.facade);
  };
  function AbstractSendChannel$enqueueSend$lambda(this$AbstractSendChannel) {
    return function () {
      return this$AbstractSendChannel.isBufferFull;
    };
  }
  AbstractSendChannel.prototype.enqueueSend_kqrzrn$_0 = function (send) {
    if (this.isBufferAlwaysFull) {
      var $this = this.queue;
      addLastIfPrev_ajzm8d$break: do {
        var prev = $this._prev;
        if (Kotlin.isType(prev, ReceiveOrClosed))
          return prev;
        if (!true) {
          false;
          break addLastIfPrev_ajzm8d$break;
        }
        $this.addLast_tsj8n4$(send);
        true;
      }
       while (false);
    }
     else {
      var $this_0 = this.queue;
      var addLastIfPrevAndIf_hs5ca2$result;
      addLastIfPrevAndIf_hs5ca2$break: do {
        var prev_0 = $this_0._prev;
        if (Kotlin.isType(prev_0, ReceiveOrClosed))
          return prev_0;
        if (!true) {
          addLastIfPrevAndIf_hs5ca2$result = false;
          break addLastIfPrevAndIf_hs5ca2$break;
        }
        if (!AbstractSendChannel$enqueueSend$lambda(this)()) {
          addLastIfPrevAndIf_hs5ca2$result = false;
          break addLastIfPrevAndIf_hs5ca2$break;
        }
        $this_0.addLast_tsj8n4$(send);
        addLastIfPrevAndIf_hs5ca2$result = true;
      }
       while (false);
      if (!addLastIfPrevAndIf_hs5ca2$result)
        return ENQUEUE_FAILED;
    }
    return null;
  };
  AbstractSendChannel.prototype.close_dbl4no$$default = function (cause) {
    var tmp$;
    var closed = new Closed(cause);
    while (true) {
      var receive = this.takeFirstReceiveOrPeekClosed();
      if (receive == null) {
        var $this = this.queue;
        var addLastIfPrev_ajzm8d$result;
        addLastIfPrev_ajzm8d$break: do {
          var prev = $this._prev;
          if (Kotlin.isType(prev, Closed))
            return false;
          if (!!Kotlin.isType(prev, ReceiveOrClosed)) {
            addLastIfPrev_ajzm8d$result = false;
            break addLastIfPrev_ajzm8d$break;
          }
          $this.addLast_tsj8n4$(closed);
          addLastIfPrev_ajzm8d$result = true;
        }
         while (false);
        if (addLastIfPrev_ajzm8d$result) {
          this.onClosed_f9b9m0$(closed);
          this.afterClose_dbl4no$(cause);
          return true;
        }
        continue;
      }
      if (Kotlin.isType(receive, Closed))
        return false;
      Kotlin.isType(tmp$ = receive, Receive) ? tmp$ : throwCCE();
      receive.resumeReceiveClosed_8093bk$(closed);
    }
  };
  AbstractSendChannel.prototype.onClosed_f9b9m0$ = function (closed) {
  };
  AbstractSendChannel.prototype.afterClose_dbl4no$ = function (cause) {
  };
  AbstractSendChannel.prototype.takeFirstReceiveOrPeekClosed = function () {
    var $this = this.queue;
    var removeFirstIfIsInstanceOfOrPeekIf_14urrv$result;
    removeFirstIfIsInstanceOfOrPeekIf_14urrv$break: do {
      var next = $this._next;
      if (next === $this) {
        removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = null;
        break removeFirstIfIsInstanceOfOrPeekIf_14urrv$break;
      }
      if (!Kotlin.isType(next, ReceiveOrClosed)) {
        removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = null;
        break removeFirstIfIsInstanceOfOrPeekIf_14urrv$break;
      }
      if (Kotlin.isType(next, Closed)) {
        removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = next;
        break removeFirstIfIsInstanceOfOrPeekIf_14urrv$break;
      }
      if (!next.remove()) {
        var message = 'Should remove';
        throw IllegalStateException_init(message.toString());
      }
      removeFirstIfIsInstanceOfOrPeekIf_14urrv$result = next;
    }
     while (false);
    return removeFirstIfIsInstanceOfOrPeekIf_14urrv$result;
  };
  AbstractSendChannel.prototype.describeTryOffer_11rb$ = function (element) {
    return new AbstractSendChannel$TryOfferDesc(element, this.queue);
  };
  function AbstractSendChannel$TryOfferDesc(element, queue) {
    RemoveFirstDesc.call(this, queue);
    this.element = element;
    this.resumeToken = null;
  }
  AbstractSendChannel$TryOfferDesc.prototype.failure_b1buut$ = function (affected, next) {
    if (!Kotlin.isType(affected, ReceiveOrClosed))
      return OFFER_FAILED;
    if (Kotlin.isType(affected, Closed))
      return affected;
    return null;
  };
  AbstractSendChannel$TryOfferDesc.prototype.validatePrepared_11rb$ = function (node) {
    var tmp$;
    tmp$ = node.tryResumeReceive_19pj23$(this.element, this);
    if (tmp$ == null) {
      return false;
    }
    var token = tmp$;
    this.resumeToken = token;
    return true;
  };
  AbstractSendChannel$TryOfferDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TryOfferDesc',
    interfaces: [RemoveFirstDesc]
  };
  function AbstractSendChannel$TryEnqueueSendDesc($outer, element, select, block) {
    this.$outer = $outer;
    AddLastDesc.call(this, this.$outer.queue, new AbstractSendChannel$SendSelect(element, this.$outer, select, block));
  }
  AbstractSendChannel$TryEnqueueSendDesc.prototype.failure_b1buut$ = function (affected, next) {
    var tmp$, tmp$_0;
    if (Kotlin.isType(affected, ReceiveOrClosed)) {
      return (tmp$_0 = Kotlin.isType(tmp$ = affected, Closed) ? tmp$ : null) != null ? tmp$_0 : ENQUEUE_FAILED;
    }
    return null;
  };
  AbstractSendChannel$TryEnqueueSendDesc.prototype.onPrepare_9p47n0$ = function (affected, next) {
    if (!this.$outer.isBufferFull)
      return ENQUEUE_FAILED;
    return AddLastDesc.prototype.onPrepare_9p47n0$.call(this, affected, next);
  };
  AbstractSendChannel$TryEnqueueSendDesc.prototype.finishOnSuccess_9p47n0$ = function (affected, next) {
    AddLastDesc.prototype.finishOnSuccess_9p47n0$.call(this, affected, next);
    this.node.disposeOnSelect();
  };
  AbstractSendChannel$TryEnqueueSendDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TryEnqueueSendDesc',
    interfaces: [AddLastDesc]
  };
  function AbstractSendChannel$get_AbstractSendChannel$onSend$ObjectLiteral(this$AbstractSendChannel) {
    this.this$AbstractSendChannel = this$AbstractSendChannel;
  }
  AbstractSendChannel$get_AbstractSendChannel$onSend$ObjectLiteral.prototype.registerSelectClause2_9926h0$ = function (select, param, block) {
    this.this$AbstractSendChannel.registerSelectSend_nqrhtt$_0(select, param, block);
  };
  AbstractSendChannel$get_AbstractSendChannel$onSend$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [SelectClause2]
  };
  Object.defineProperty(AbstractSendChannel.prototype, 'onSend', {
    get: function () {
      return new AbstractSendChannel$get_AbstractSendChannel$onSend$ObjectLiteral(this);
    }
  });
  AbstractSendChannel.prototype.registerSelectSend_nqrhtt$_0 = function (select, element, block) {
    var tmp$;
    while (true) {
      if (select.isSelected)
        return;
      if (this.isFull) {
        var enqueueOp = new AbstractSendChannel$TryEnqueueSendDesc(this, element, select, block);
        tmp$ = select.performAtomicIfNotSelected_qopb37$(enqueueOp);
        if (tmp$ == null) {
          return;
        }
        var enqueueResult = tmp$;
        if (enqueueResult === ALREADY_SELECTED)
          return;
        else if (enqueueResult !== ENQUEUE_FAILED)
          if (Kotlin.isType(enqueueResult, Closed))
            throw enqueueResult.sendException;
          else {
            throw IllegalStateException_init(('performAtomicIfNotSelected(TryEnqueueSendDesc) returned ' + enqueueResult).toString());
          }
      }
       else {
        var offerResult = this.offerSelectInternal_26cf95$(element, select);
        if (offerResult === ALREADY_SELECTED)
          return;
        else if (offerResult !== OFFER_FAILED)
          if (offerResult === OFFER_SUCCESS) {
            startCoroutineUndispatched_0(block, this, select.completion);
            return;
          }
           else if (Kotlin.isType(offerResult, Closed))
            throw offerResult.sendException;
          else {
            throw IllegalStateException_init(('offerSelectInternal returned ' + offerResult).toString());
          }
      }
    }
  };
  AbstractSendChannel.prototype.toString = function () {
    return get_classSimpleName(this) + '@' + get_hexAddress(this) + '{' + this.queueDebugStateString_fftov7$_0 + '}' + this.bufferDebugString;
  };
  Object.defineProperty(AbstractSendChannel.prototype, 'queueDebugStateString_fftov7$_0', {
    get: function () {
      var tmp$;
      var head = this.queue._next;
      if (head === this.queue)
        return 'EmptyQueue';
      if (Kotlin.isType(head, Closed))
        tmp$ = head.toString();
      else if (Kotlin.isType(head, Receive))
        tmp$ = 'ReceiveQueued';
      else if (Kotlin.isType(head, Send))
        tmp$ = 'SendQueued';
      else
        tmp$ = 'UNEXPECTED:' + head;
      var result = tmp$;
      var tail = this.queue._prev;
      if (tail !== head) {
        result += ',queueSize=' + this.countQueueSize_pjh27m$_0();
        if (Kotlin.isType(tail, Closed))
          result += ',closedForSend=' + tail;
      }
      return result;
    }
  });
  AbstractSendChannel.prototype.countQueueSize_pjh27m$_0 = function () {
    var size = {v: 0};
    var $this = this.queue;
    var cur = $this._next;
    while (!equals(cur, $this)) {
      if (Kotlin.isType(cur, LinkedListNode)) {
        size.v = size.v + 1 | 0;
      }
      cur = cur._next;
    }
    return size.v;
  };
  Object.defineProperty(AbstractSendChannel.prototype, 'bufferDebugString', {
    get: function () {
      return '';
    }
  });
  function AbstractSendChannel$SendSelect(pollResult, channel, select, block) {
    LinkedListNode.call(this);
    this.pollResult_44yhp$_0 = pollResult;
    this.channel = channel;
    this.select = select;
    this.block = block;
  }
  Object.defineProperty(AbstractSendChannel$SendSelect.prototype, 'pollResult', {
    get: function () {
      return this.pollResult_44yhp$_0;
    }
  });
  AbstractSendChannel$SendSelect.prototype.tryResumeSend_s8jyv4$ = function (idempotent) {
    return this.select.trySelect_s8jyv4$(idempotent) ? SELECT_STARTED : null;
  };
  AbstractSendChannel$SendSelect.prototype.completeResumeSend_za3rmp$ = function (token) {
    if (!(token === SELECT_STARTED)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    startCoroutine_0(this.block, this.channel, this.select.completion);
  };
  AbstractSendChannel$SendSelect.prototype.disposeOnSelect = function () {
    this.select.disposeOnSelect_lo7ng2$(this);
  };
  AbstractSendChannel$SendSelect.prototype.dispose = function () {
    this.remove();
  };
  AbstractSendChannel$SendSelect.prototype.resumeSendClosed_8093bk$ = function (closed) {
    if (this.select.trySelect_s8jyv4$(null))
      this.select.resumeSelectCancellableWithException_tcv7n7$(closed.sendException);
  };
  AbstractSendChannel$SendSelect.prototype.toString = function () {
    return 'SendSelect(' + toString(this.pollResult) + ')[' + this.channel + ', ' + this.select + ']';
  };
  AbstractSendChannel$SendSelect.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SendSelect',
    interfaces: [DisposableHandle, Send, LinkedListNode]
  };
  function AbstractSendChannel$SendBuffered(element) {
    LinkedListNode.call(this);
    this.element = element;
  }
  Object.defineProperty(AbstractSendChannel$SendBuffered.prototype, 'pollResult', {
    get: function () {
      return this.element;
    }
  });
  AbstractSendChannel$SendBuffered.prototype.tryResumeSend_s8jyv4$ = function (idempotent) {
    return SEND_RESUMED;
  };
  AbstractSendChannel$SendBuffered.prototype.completeResumeSend_za3rmp$ = function (token) {
    if (!(token === SEND_RESUMED)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
  };
  AbstractSendChannel$SendBuffered.prototype.resumeSendClosed_8093bk$ = function (closed) {
  };
  AbstractSendChannel$SendBuffered.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SendBuffered',
    interfaces: [Send, LinkedListNode]
  };
  AbstractSendChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractSendChannel',
    interfaces: [SendChannel]
  };
  function AbstractChannel() {
    AbstractSendChannel.call(this);
  }
  AbstractChannel.prototype.pollInternal = function () {
    var tmp$;
    while (true) {
      tmp$ = this.takeFirstSendOrPeekClosed();
      if (tmp$ == null) {
        return POLL_FAILED;
      }
      var send = tmp$;
      var token = send.tryResumeSend_s8jyv4$(null);
      if (token != null) {
        send.completeResumeSend_za3rmp$(token);
        return send.pollResult;
      }
    }
  };
  AbstractChannel.prototype.pollSelectInternal_qqlfgi$ = function (select) {
    var pollOp = this.describeTryPoll();
    var failure = select.performAtomicTrySelect_qopb37$(pollOp);
    if (failure != null)
      return failure;
    var send = pollOp.result;
    send.completeResumeSend_za3rmp$(ensureNotNull(pollOp.resumeToken));
    return pollOp.pollResult;
  };
  Object.defineProperty(AbstractChannel.prototype, 'hasReceiveOrClosed', {
    get: function () {
      return Kotlin.isType(this.queue._next, ReceiveOrClosed);
    }
  });
  Object.defineProperty(AbstractChannel.prototype, 'isClosedForReceive', {
    get: function () {
      return this.closedForReceive != null && this.isBufferEmpty;
    }
  });
  Object.defineProperty(AbstractChannel.prototype, 'isEmpty', {
    get: function () {
      return !Kotlin.isType(this.queue._next, Send) && this.isBufferEmpty;
    }
  });
  AbstractChannel.prototype.receive = function (continuation) {
    var result = this.pollInternal();
    if (result !== POLL_FAILED)
      return this.receiveResult_22e2qt$_0(result);
    return this.receiveSuspend_9p3i4g$_0(continuation);
  };
  AbstractChannel.prototype.receiveResult_22e2qt$_0 = function (result) {
    var tmp$;
    if (Kotlin.isType(result, Closed))
      throw result.receiveException;
    return (tmp$ = result) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  function AbstractChannel$receiveSuspend$lambda(this$AbstractChannel) {
    return function (cont) {
      var tmp$, tmp$_0;
      var receive = new AbstractChannel$ReceiveElement(Kotlin.isType(tmp$ = cont, CancellableContinuation) ? tmp$ : throwCCE(), false);
      while (true) {
        if (this$AbstractChannel.enqueueReceive_3pouqz$_0(receive)) {
          cont.initCancellability();
          this$AbstractChannel.removeReceiveOnCancel_ya0nqp$_0(cont, receive);
          return;
        }
        var result = this$AbstractChannel.pollInternal();
        if (Kotlin.isType(result, Closed)) {
          cont.resumeWithException_tcv7n7$(result.receiveException);
          return;
        }
        if (result !== POLL_FAILED) {
          cont.resume_11rb$((tmp$_0 = result) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE());
          return;
        }
      }
      return Unit;
    };
  }
  AbstractChannel.prototype.receiveSuspend_9p3i4g$_0 = function (continuation) {
    return suspendAtomicCancellableCoroutine$lambda(true, AbstractChannel$receiveSuspend$lambda(this))(continuation.facade);
  };
  function AbstractChannel$enqueueReceive$lambda(this$AbstractChannel) {
    return function () {
      return this$AbstractChannel.isBufferEmpty;
    };
  }
  AbstractChannel.prototype.enqueueReceive_3pouqz$_0 = function (receive) {
    var tmp$;
    if (this.isBufferAlwaysEmpty) {
      var $this = this.queue;
      var addLastIfPrev_ajzm8d$result;
      addLastIfPrev_ajzm8d$break: do {
        if (!!Kotlin.isType($this._prev, Send)) {
          addLastIfPrev_ajzm8d$result = false;
          break addLastIfPrev_ajzm8d$break;
        }
        $this.addLast_tsj8n4$(receive);
        addLastIfPrev_ajzm8d$result = true;
      }
       while (false);
      tmp$ = addLastIfPrev_ajzm8d$result;
    }
     else {
      var $this_0 = this.queue;
      var addLastIfPrevAndIf_hs5ca2$result;
      addLastIfPrevAndIf_hs5ca2$break: do {
        if (!!Kotlin.isType($this_0._prev, Send)) {
          addLastIfPrevAndIf_hs5ca2$result = false;
          break addLastIfPrevAndIf_hs5ca2$break;
        }
        if (!AbstractChannel$enqueueReceive$lambda(this)()) {
          addLastIfPrevAndIf_hs5ca2$result = false;
          break addLastIfPrevAndIf_hs5ca2$break;
        }
        $this_0.addLast_tsj8n4$(receive);
        addLastIfPrevAndIf_hs5ca2$result = true;
      }
       while (false);
      tmp$ = addLastIfPrevAndIf_hs5ca2$result;
    }
    var result = tmp$;
    if (result)
      this.onReceiveEnqueued();
    return result;
  };
  AbstractChannel.prototype.receiveOrNull = function (continuation) {
    var result = this.pollInternal();
    if (result !== POLL_FAILED)
      return this.receiveOrNullResult_mq3ucx$_0(result);
    return this.receiveOrNullSuspend_hkc36y$_0(continuation);
  };
  AbstractChannel.prototype.receiveOrNullResult_mq3ucx$_0 = function (result) {
    var tmp$;
    if (Kotlin.isType(result, Closed)) {
      if (result.closeCause != null)
        throw result.closeCause;
      return null;
    }
    return (tmp$ = result) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  function AbstractChannel$receiveOrNullSuspend$lambda(this$AbstractChannel) {
    return function (cont) {
      var tmp$;
      var receive = new AbstractChannel$ReceiveElement(cont, true);
      while (true) {
        if (this$AbstractChannel.enqueueReceive_3pouqz$_0(receive)) {
          cont.initCancellability();
          this$AbstractChannel.removeReceiveOnCancel_ya0nqp$_0(cont, receive);
          return;
        }
        var result = this$AbstractChannel.pollInternal();
        if (Kotlin.isType(result, Closed)) {
          if (result.closeCause == null)
            cont.resume_11rb$(null);
          else
            cont.resumeWithException_tcv7n7$(result.closeCause);
          return;
        }
        if (result !== POLL_FAILED) {
          cont.resume_11rb$((tmp$ = result) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE());
          return;
        }
      }
      return Unit;
    };
  }
  AbstractChannel.prototype.receiveOrNullSuspend_hkc36y$_0 = function (continuation) {
    return suspendAtomicCancellableCoroutine$lambda(true, AbstractChannel$receiveOrNullSuspend$lambda(this))(continuation.facade);
  };
  AbstractChannel.prototype.poll = function () {
    var result = this.pollInternal();
    return result === POLL_FAILED ? null : this.receiveOrNullResult_mq3ucx$_0(result);
  };
  AbstractChannel.prototype.cancel_dbl4no$$default = function (cause) {
    var $receiver = this.close_dbl4no$(cause);
    this.cleanupSendQueueOnCancel();
    return $receiver;
  };
  AbstractChannel.prototype.cleanupSendQueueOnCancel = function () {
    var tmp$, tmp$_0;
    var tmp$_1;
    if ((tmp$ = this.closedForSend) != null)
      tmp$_1 = tmp$;
    else {
      throw IllegalStateException_init('Cannot happen'.toString());
    }
    var closed = tmp$_1;
    while (true) {
      var tmp$_2;
      if ((tmp$_0 = this.takeFirstSendOrPeekClosed()) != null)
        tmp$_2 = tmp$_0;
      else {
        throw IllegalStateException_init('Cannot happen'.toString());
      }
      var send = tmp$_2;
      if (Kotlin.isType(send, Closed)) {
        if (!(send === closed)) {
          var message = 'Check failed.';
          throw IllegalStateException_init(message.toString());
        }
        return;
      }
      send.resumeSendClosed_8093bk$(closed);
    }
  };
  AbstractChannel.prototype.iterator = function () {
    return new AbstractChannel$Itr(this);
  };
  AbstractChannel.prototype.describeTryPoll = function () {
    return new AbstractChannel$TryPollDesc(this.queue);
  };
  function AbstractChannel$TryPollDesc(queue) {
    RemoveFirstDesc.call(this, queue);
    this.resumeToken = null;
    this.pollResult = null;
  }
  AbstractChannel$TryPollDesc.prototype.failure_b1buut$ = function (affected, next) {
    if (Kotlin.isType(affected, Closed))
      return affected;
    if (!Kotlin.isType(affected, Send))
      return POLL_FAILED;
    return null;
  };
  AbstractChannel$TryPollDesc.prototype.validatePrepared_11rb$ = function (node) {
    var tmp$, tmp$_0;
    tmp$ = node.tryResumeSend_s8jyv4$(this);
    if (tmp$ == null) {
      return false;
    }
    var token = tmp$;
    this.resumeToken = token;
    this.pollResult = (tmp$_0 = node.pollResult) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
    return true;
  };
  AbstractChannel$TryPollDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TryPollDesc',
    interfaces: [RemoveFirstDesc]
  };
  function AbstractChannel$TryEnqueueReceiveDesc($outer, select, block, nullOnClose) {
    this.$outer = $outer;
    AddLastDesc.call(this, this.$outer.queue, new AbstractChannel$ReceiveSelect(this.$outer, select, block, nullOnClose));
  }
  AbstractChannel$TryEnqueueReceiveDesc.prototype.failure_b1buut$ = function (affected, next) {
    if (Kotlin.isType(affected, Send))
      return ENQUEUE_FAILED;
    return null;
  };
  AbstractChannel$TryEnqueueReceiveDesc.prototype.onPrepare_9p47n0$ = function (affected, next) {
    if (!this.$outer.isBufferEmpty)
      return ENQUEUE_FAILED;
    return AddLastDesc.prototype.onPrepare_9p47n0$.call(this, affected, next);
  };
  AbstractChannel$TryEnqueueReceiveDesc.prototype.finishOnSuccess_9p47n0$ = function (affected, next) {
    AddLastDesc.prototype.finishOnSuccess_9p47n0$.call(this, affected, next);
    this.$outer.onReceiveEnqueued();
    this.node.removeOnSelectCompletion();
  };
  AbstractChannel$TryEnqueueReceiveDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TryEnqueueReceiveDesc',
    interfaces: [AddLastDesc]
  };
  function AbstractChannel$get_AbstractChannel$onReceive$ObjectLiteral(this$AbstractChannel) {
    this.this$AbstractChannel = this$AbstractChannel;
  }
  AbstractChannel$get_AbstractChannel$onReceive$ObjectLiteral.prototype.registerSelectClause1_t4n5y6$ = function (select, block) {
    this.this$AbstractChannel.registerSelectReceive_yl4xl3$_0(select, block);
  };
  AbstractChannel$get_AbstractChannel$onReceive$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [SelectClause1]
  };
  Object.defineProperty(AbstractChannel.prototype, 'onReceive', {
    get: function () {
      return new AbstractChannel$get_AbstractChannel$onReceive$ObjectLiteral(this);
    }
  });
  AbstractChannel.prototype.registerSelectReceive_yl4xl3$_0 = function (select, block) {
    var tmp$, tmp$_0, tmp$_1;
    while (true) {
      if (select.isSelected)
        return;
      if (this.isEmpty) {
        var enqueueOp = new AbstractChannel$TryEnqueueReceiveDesc(this, select, Kotlin.isType(tmp$ = block, SuspendFunction1) ? tmp$ : throwCCE(), false);
        tmp$_0 = select.performAtomicIfNotSelected_qopb37$(enqueueOp);
        if (tmp$_0 == null) {
          return;
        }
        var enqueueResult = tmp$_0;
        if (enqueueResult === ALREADY_SELECTED)
          return;
        else if (enqueueResult !== ENQUEUE_FAILED) {
          throw IllegalStateException_init(('performAtomicIfNotSelected(TryEnqueueReceiveDesc) returned ' + enqueueResult).toString());
        }
      }
       else {
        var pollResult = this.pollSelectInternal_qqlfgi$(select);
        if (pollResult === ALREADY_SELECTED)
          return;
        else if (pollResult !== POLL_FAILED)
          if (Kotlin.isType(pollResult, Closed))
            throw pollResult.receiveException;
          else {
            startCoroutineUndispatched_0(block, (tmp$_1 = pollResult) == null || Kotlin.isType(tmp$_1, Any) ? tmp$_1 : throwCCE(), select.completion);
            return;
          }
      }
    }
  };
  function AbstractChannel$get_AbstractChannel$onReceiveOrNull$ObjectLiteral(this$AbstractChannel) {
    this.this$AbstractChannel = this$AbstractChannel;
  }
  AbstractChannel$get_AbstractChannel$onReceiveOrNull$ObjectLiteral.prototype.registerSelectClause1_t4n5y6$ = function (select, block) {
    this.this$AbstractChannel.registerSelectReceiveOrNull_rw67tb$_0(select, block);
  };
  AbstractChannel$get_AbstractChannel$onReceiveOrNull$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [SelectClause1]
  };
  Object.defineProperty(AbstractChannel.prototype, 'onReceiveOrNull', {
    get: function () {
      return new AbstractChannel$get_AbstractChannel$onReceiveOrNull$ObjectLiteral(this);
    }
  });
  AbstractChannel.prototype.registerSelectReceiveOrNull_rw67tb$_0 = function (select, block) {
    var tmp$, tmp$_0;
    while (true) {
      if (select.isSelected)
        return;
      if (this.isEmpty) {
        var enqueueOp = new AbstractChannel$TryEnqueueReceiveDesc(this, select, block, true);
        tmp$ = select.performAtomicIfNotSelected_qopb37$(enqueueOp);
        if (tmp$ == null) {
          return;
        }
        var enqueueResult = tmp$;
        if (enqueueResult === ALREADY_SELECTED)
          return;
        else if (enqueueResult !== ENQUEUE_FAILED) {
          throw IllegalStateException_init(('performAtomicIfNotSelected(TryEnqueueReceiveDesc) returned ' + enqueueResult).toString());
        }
      }
       else {
        var pollResult = this.pollSelectInternal_qqlfgi$(select);
        if (pollResult === ALREADY_SELECTED)
          return;
        else if (pollResult !== POLL_FAILED)
          if (Kotlin.isType(pollResult, Closed))
            if (pollResult.closeCause == null) {
              if (select.trySelect_s8jyv4$(null))
                startCoroutineUndispatched_0(block, null, select.completion);
              return;
            }
             else
              throw pollResult.closeCause;
          else {
            startCoroutineUndispatched_0(block, (tmp$_0 = pollResult) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE(), select.completion);
            return;
          }
      }
    }
  };
  AbstractChannel.prototype.takeFirstReceiveOrPeekClosed = function () {
    var $receiver = AbstractSendChannel.prototype.takeFirstReceiveOrPeekClosed.call(this);
    if ($receiver != null && !Kotlin.isType($receiver, Closed))
      this.onReceiveDequeued();
    return $receiver;
  };
  AbstractChannel.prototype.onReceiveEnqueued = function () {
  };
  AbstractChannel.prototype.onReceiveDequeued = function () {
  };
  AbstractChannel.prototype.removeReceiveOnCancel_ya0nqp$_0 = function (cont, receive) {
    cont.invokeOnCancellation_f05bi3$(new AbstractChannel$RemoveReceiveOnCancel(this, receive));
  };
  function AbstractChannel$RemoveReceiveOnCancel($outer, receive) {
    this.$outer = $outer;
    CancelHandler.call(this);
    this.receive_0 = receive;
  }
  AbstractChannel$RemoveReceiveOnCancel.prototype.invoke = function (cause) {
    if (this.receive_0.remove())
      this.$outer.onReceiveDequeued();
  };
  AbstractChannel$RemoveReceiveOnCancel.prototype.toString = function () {
    return 'RemoveReceiveOnCancel[' + this.receive_0 + ']';
  };
  AbstractChannel$RemoveReceiveOnCancel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemoveReceiveOnCancel',
    interfaces: [CancelHandler]
  };
  function AbstractChannel$Itr(channel) {
    this.channel = channel;
    this.result = POLL_FAILED;
  }
  AbstractChannel$Itr.prototype.hasNext = function (continuation) {
    if (this.result !== POLL_FAILED)
      return this.hasNextResult_0(this.result);
    this.result = this.channel.pollInternal();
    if (this.result !== POLL_FAILED)
      return this.hasNextResult_0(this.result);
    return this.hasNextSuspend_0(continuation);
  };
  AbstractChannel$Itr.prototype.hasNextResult_0 = function (result) {
    if (Kotlin.isType(result, Closed)) {
      if (result.closeCause != null)
        throw result.receiveException;
      return false;
    }
    return true;
  };
  function AbstractChannel$Itr$hasNextSuspend$lambda(this$Itr) {
    return function (cont) {
      var receive = new AbstractChannel$ReceiveHasNext(this$Itr, cont);
      while (true) {
        if (this$Itr.channel.enqueueReceive_3pouqz$_0(receive)) {
          cont.initCancellability();
          this$Itr.channel.removeReceiveOnCancel_ya0nqp$_0(cont, receive);
          return;
        }
        var result = this$Itr.channel.pollInternal();
        this$Itr.result = result;
        if (Kotlin.isType(result, Closed)) {
          if (result.closeCause == null)
            cont.resume_11rb$(false);
          else
            cont.resumeWithException_tcv7n7$(result.receiveException);
          return;
        }
        if (result !== POLL_FAILED) {
          cont.resume_11rb$(true);
          return;
        }
      }
      return Unit;
    };
  }
  AbstractChannel$Itr.prototype.hasNextSuspend_0 = function (continuation) {
    return suspendAtomicCancellableCoroutine$lambda(true, AbstractChannel$Itr$hasNextSuspend$lambda(this))(continuation.facade);
  };
  AbstractChannel$Itr.prototype.next = function (continuation) {
    var tmp$;
    var result = this.result;
    if (Kotlin.isType(result, Closed))
      throw result.receiveException;
    if (result !== POLL_FAILED) {
      this.result = POLL_FAILED;
      return (tmp$ = result) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
    }
    return this.channel.receive(continuation);
  };
  AbstractChannel$Itr.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Itr',
    interfaces: [ChannelIterator]
  };
  function AbstractChannel$ReceiveElement(cont, nullOnClose) {
    Receive.call(this);
    this.cont = cont;
    this.nullOnClose = nullOnClose;
  }
  AbstractChannel$ReceiveElement.prototype.tryResumeReceive_19pj23$ = function (value, idempotent) {
    return this.cont.tryResume_19pj23$(value, idempotent);
  };
  AbstractChannel$ReceiveElement.prototype.completeResumeReceive_za3rmp$ = function (token) {
    this.cont.completeResume_za3rmp$(token);
  };
  AbstractChannel$ReceiveElement.prototype.resumeReceiveClosed_8093bk$ = function (closed) {
    if (closed.closeCause == null && this.nullOnClose)
      this.cont.resume_11rb$(null);
    else
      this.cont.resumeWithException_tcv7n7$(closed.receiveException);
  };
  AbstractChannel$ReceiveElement.prototype.toString = function () {
    return 'ReceiveElement[' + this.cont + ',nullOnClose=' + this.nullOnClose + ']';
  };
  AbstractChannel$ReceiveElement.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ReceiveElement',
    interfaces: [Receive]
  };
  function AbstractChannel$ReceiveHasNext(iterator, cont) {
    Receive.call(this);
    this.iterator = iterator;
    this.cont = cont;
  }
  AbstractChannel$ReceiveHasNext.prototype.tryResumeReceive_19pj23$ = function (value, idempotent) {
    var token = this.cont.tryResume_19pj23$(true, idempotent);
    if (token != null) {
      if (idempotent != null)
        return new AbstractChannel$IdempotentTokenValue(token, value);
      this.iterator.result = value;
    }
    return token;
  };
  AbstractChannel$ReceiveHasNext.prototype.completeResumeReceive_za3rmp$ = function (token) {
    if (Kotlin.isType(token, AbstractChannel$IdempotentTokenValue)) {
      this.iterator.result = token.value;
      this.cont.completeResume_za3rmp$(token.token);
    }
     else
      this.cont.completeResume_za3rmp$(token);
  };
  AbstractChannel$ReceiveHasNext.prototype.resumeReceiveClosed_8093bk$ = function (closed) {
    var token = closed.closeCause == null ? this.cont.tryResume_19pj23$(false) : this.cont.tryResumeWithException_tcv7n7$(closed.receiveException);
    if (token != null) {
      this.iterator.result = closed;
      this.cont.completeResume_za3rmp$(token);
    }
  };
  AbstractChannel$ReceiveHasNext.prototype.toString = function () {
    return 'ReceiveHasNext[' + this.cont + ']';
  };
  AbstractChannel$ReceiveHasNext.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ReceiveHasNext',
    interfaces: [Receive]
  };
  function AbstractChannel$ReceiveSelect($outer, select, block, nullOnClose) {
    this.$outer = $outer;
    Receive.call(this);
    this.select = select;
    this.block = block;
    this.nullOnClose = nullOnClose;
  }
  AbstractChannel$ReceiveSelect.prototype.tryResumeReceive_19pj23$ = function (value, idempotent) {
    return this.select.trySelect_s8jyv4$(idempotent) ? value != null ? value : NULL_VALUE : null;
  };
  AbstractChannel$ReceiveSelect.prototype.completeResumeReceive_za3rmp$ = function (token) {
    var tmp$;
    var value = (tmp$ = token === NULL_VALUE ? null : token) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
    startCoroutine_0(this.block, value, this.select.completion);
  };
  AbstractChannel$ReceiveSelect.prototype.resumeReceiveClosed_8093bk$ = function (closed) {
    if (this.select.trySelect_s8jyv4$(null)) {
      if (closed.closeCause == null && this.nullOnClose) {
        startCoroutine_0(this.block, null, this.select.completion);
      }
       else {
        this.select.resumeSelectCancellableWithException_tcv7n7$(closed.receiveException);
      }
    }
  };
  AbstractChannel$ReceiveSelect.prototype.removeOnSelectCompletion = function () {
    this.select.disposeOnSelect_lo7ng2$(this);
  };
  AbstractChannel$ReceiveSelect.prototype.dispose = function () {
    if (this.remove())
      this.$outer.onReceiveDequeued();
  };
  AbstractChannel$ReceiveSelect.prototype.toString = function () {
    return 'ReceiveSelect[' + this.select + ',nullOnClose=' + this.nullOnClose + ']';
  };
  AbstractChannel$ReceiveSelect.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ReceiveSelect',
    interfaces: [DisposableHandle, Receive]
  };
  function AbstractChannel$IdempotentTokenValue(token, value) {
    this.token = token;
    this.value = value;
  }
  AbstractChannel$IdempotentTokenValue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IdempotentTokenValue',
    interfaces: []
  };
  AbstractChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractChannel',
    interfaces: [Channel, AbstractSendChannel]
  };
  var OFFER_SUCCESS;
  var OFFER_FAILED;
  var POLL_FAILED;
  var ENQUEUE_FAILED;
  var SELECT_STARTED;
  var NULL_VALUE;
  var CLOSE_RESUMED;
  var SEND_RESUMED;
  function Send() {
  }
  Send.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Send',
    interfaces: []
  };
  function ReceiveOrClosed() {
  }
  ReceiveOrClosed.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ReceiveOrClosed',
    interfaces: []
  };
  function SendElement(pollResult, cont) {
    LinkedListNode.call(this);
    this.pollResult_guszzk$_0 = pollResult;
    this.cont = cont;
  }
  Object.defineProperty(SendElement.prototype, 'pollResult', {
    get: function () {
      return this.pollResult_guszzk$_0;
    }
  });
  SendElement.prototype.tryResumeSend_s8jyv4$ = function (idempotent) {
    return this.cont.tryResume_19pj23$(Unit, idempotent);
  };
  SendElement.prototype.completeResumeSend_za3rmp$ = function (token) {
    this.cont.completeResume_za3rmp$(token);
  };
  SendElement.prototype.resumeSendClosed_8093bk$ = function (closed) {
    this.cont.resumeWithException_tcv7n7$(closed.sendException);
  };
  SendElement.prototype.toString = function () {
    return 'SendElement(' + toString(this.pollResult) + ')[' + this.cont + ']';
  };
  SendElement.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SendElement',
    interfaces: [Send, LinkedListNode]
  };
  function Closed(closeCause) {
    LinkedListNode.call(this);
    this.closeCause = closeCause;
  }
  Object.defineProperty(Closed.prototype, 'sendException', {
    get: function () {
      var tmp$;
      return (tmp$ = this.closeCause) != null ? tmp$ : new ClosedSendChannelException(DEFAULT_CLOSE_MESSAGE);
    }
  });
  Object.defineProperty(Closed.prototype, 'receiveException', {
    get: function () {
      var tmp$;
      return (tmp$ = this.closeCause) != null ? tmp$ : new ClosedReceiveChannelException(DEFAULT_CLOSE_MESSAGE);
    }
  });
  Object.defineProperty(Closed.prototype, 'offerResult', {
    get: function () {
      return this;
    }
  });
  Object.defineProperty(Closed.prototype, 'pollResult', {
    get: function () {
      return this;
    }
  });
  Closed.prototype.tryResumeSend_s8jyv4$ = function (idempotent) {
    return CLOSE_RESUMED;
  };
  Closed.prototype.completeResumeSend_za3rmp$ = function (token) {
    if (!(token === CLOSE_RESUMED)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
  };
  Closed.prototype.tryResumeReceive_19pj23$ = function (value, idempotent) {
    return CLOSE_RESUMED;
  };
  Closed.prototype.completeResumeReceive_za3rmp$ = function (token) {
    if (!(token === CLOSE_RESUMED)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
  };
  Closed.prototype.resumeSendClosed_8093bk$ = function (closed) {
    throw IllegalStateException_init('Should be never invoked'.toString());
  };
  Closed.prototype.toString = function () {
    return 'Closed[' + toString(this.closeCause) + ']';
  };
  Closed.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Closed',
    interfaces: [ReceiveOrClosed, Send, LinkedListNode]
  };
  function Receive() {
    LinkedListNode.call(this);
  }
  Object.defineProperty(Receive.prototype, 'offerResult', {
    get: function () {
      return OFFER_SUCCESS;
    }
  });
  Receive.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Receive',
    interfaces: [ReceiveOrClosed, LinkedListNode]
  };
  function ArrayBroadcastChannel(capacity) {
    AbstractSendChannel.call(this);
    this.capacity = capacity;
    if (!(this.capacity >= 1)) {
      var message = 'ArrayBroadcastChannel capacity must be at least 1, but ' + this.capacity + ' was specified';
      throw IllegalArgumentException_init(message.toString());
    }
    this.bufferLock_0 = new NoOpLock();
    this.buffer_0 = Kotlin.newArray(this.capacity, null);
    this.head_0 = L0;
    this.tail_0 = L0;
    this.size_0 = 0;
    this.subs_0 = subscriberList();
  }
  Object.defineProperty(ArrayBroadcastChannel.prototype, 'isBufferAlwaysFull', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(ArrayBroadcastChannel.prototype, 'isBufferFull', {
    get: function () {
      return this.size_0 >= this.capacity;
    }
  });
  ArrayBroadcastChannel.prototype.openSubscription = function () {
    var $receiver = new ArrayBroadcastChannel$Subscriber(this);
    this.updateHead_0($receiver);
    return $receiver;
  };
  ArrayBroadcastChannel.prototype.close_dbl4no$$default = function (cause) {
    if (!this.close_dbl4no$(cause, AbstractSendChannel.prototype.close_dbl4no$$default.bind(this)))
      return false;
    this.checkSubOffers_0();
    return true;
  };
  ArrayBroadcastChannel.prototype.cancel_dbl4no$$default = function (cause) {
    var $receiver = this.close_dbl4no$(cause);
    var tmp$;
    tmp$ = this.subs_0.iterator();
    while (tmp$.hasNext()) {
      var sub = tmp$.next();
      sub.cancel_dbl4no$(cause);
    }
    return $receiver;
  };
  ArrayBroadcastChannel.prototype.offerInternal_11rb$ = function (element) {
    var tmp$;
    if ((tmp$ = this.closedForSend) != null) {
      return tmp$;
    }
    var size = this.size_0;
    if (size >= this.capacity)
      return OFFER_FAILED;
    var tail = this.tail_0;
    this.buffer_0[tail.modulo(Kotlin.Long.fromInt(this.capacity)).toInt()] = element;
    this.size_0 = size + 1 | 0;
    this.tail_0 = tail.add(Kotlin.Long.fromInt(1));
    this.checkSubOffers_0();
    return OFFER_SUCCESS;
  };
  ArrayBroadcastChannel.prototype.offerSelectInternal_26cf95$ = function (element, select) {
    var tmp$;
    if ((tmp$ = this.closedForSend) != null) {
      return tmp$;
    }
    var size = this.size_0;
    if (size >= this.capacity)
      return OFFER_FAILED;
    if (!select.trySelect_s8jyv4$(null)) {
      return ALREADY_SELECTED;
    }
    var tail = this.tail_0;
    this.buffer_0[tail.modulo(Kotlin.Long.fromInt(this.capacity)).toInt()] = element;
    this.size_0 = size + 1 | 0;
    this.tail_0 = tail.add(Kotlin.Long.fromInt(1));
    this.checkSubOffers_0();
    return OFFER_SUCCESS;
  };
  ArrayBroadcastChannel.prototype.checkSubOffers_0 = function () {
    var tmp$;
    var updated = false;
    var hasSubs = false;
    tmp$ = this.subs_0.iterator();
    while (tmp$.hasNext()) {
      var sub = tmp$.next();
      hasSubs = true;
      if (sub.checkOffer())
        updated = true;
    }
    if (updated || !hasSubs)
      this.updateHead_0();
  };
  ArrayBroadcastChannel.prototype.updateHead_0 = function (addSub, removeSub) {
    if (addSub === void 0)
      addSub = null;
    if (removeSub === void 0)
      removeSub = null;
    var send = {v: null};
    var token = {v: null};
    action$break: do {
      var tmp$, tmp$_0;
      if (addSub != null) {
        addSub.subHead = this.tail_0;
        var wasEmpty = this.subs_0.isEmpty();
        this.subs_0.add_11rb$(addSub);
        if (!wasEmpty)
          return;
      }
      if (removeSub != null) {
        this.subs_0.remove_11rb$(removeSub);
        if (!equals(this.head_0, removeSub.subHead))
          return;
      }
      var minHead = this.computeMinHead_0();
      var tail = this.tail_0;
      var head = this.head_0;
      var targetHead = coerceAtMost(minHead, tail);
      if (targetHead.compareTo_11rb$(head) <= 0)
        return;
      var size = this.size_0;
      while (head.compareTo_11rb$(targetHead) < 0) {
        this.buffer_0[head.modulo(Kotlin.Long.fromInt(this.capacity)).toInt()] = null;
        var wasFull = size >= this.capacity;
        this.head_0 = (head = head.inc(), head);
        this.size_0 = (size = size - 1 | 0, size);
        if (wasFull) {
          while (true) {
            tmp$ = this.takeFirstSendOrPeekClosed();
            if (tmp$ == null) {
              break;
            }
            send.v = tmp$;
            if (Kotlin.isType(send.v, Closed))
              break;
            token.v = ensureNotNull(send.v).tryResumeSend_s8jyv4$(null);
            if (token.v != null) {
              this.buffer_0[tail.modulo(Kotlin.Long.fromInt(this.capacity)).toInt()] = (Kotlin.isType(tmp$_0 = send.v, Send) ? tmp$_0 : throwCCE()).pollResult;
              this.size_0 = size + 1 | 0;
              this.tail_0 = tail.add(Kotlin.Long.fromInt(1));
              break action$break;
            }
          }
        }
      }
      return;
    }
     while (false);
    ensureNotNull(send.v).completeResumeSend_za3rmp$(ensureNotNull(token.v));
    this.checkSubOffers_0();
    this.updateHead_0();
  };
  ArrayBroadcastChannel.prototype.computeMinHead_0 = function () {
    var tmp$;
    var minHead = Long$Companion$MAX_VALUE;
    tmp$ = this.subs_0.iterator();
    while (tmp$.hasNext()) {
      var sub = tmp$.next();
      minHead = coerceAtMost(minHead, sub.subHead);
    }
    return minHead;
  };
  ArrayBroadcastChannel.prototype.elementAt_0 = function (index) {
    var tmp$;
    return (tmp$ = this.buffer_0[index.modulo(Kotlin.Long.fromInt(this.capacity)).toInt()]) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
  };
  function ArrayBroadcastChannel$Subscriber(broadcastChannel) {
    AbstractChannel.call(this);
    this.broadcastChannel_0 = broadcastChannel;
    this.subLock_0 = new NoOpLock();
    this.subHead = L0;
  }
  Object.defineProperty(ArrayBroadcastChannel$Subscriber.prototype, 'isBufferAlwaysEmpty', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(ArrayBroadcastChannel$Subscriber.prototype, 'isBufferEmpty', {
    get: function () {
      return this.subHead.compareTo_11rb$(this.broadcastChannel_0.tail_0) >= 0;
    }
  });
  Object.defineProperty(ArrayBroadcastChannel$Subscriber.prototype, 'isBufferAlwaysFull', {
    get: function () {
      throw IllegalStateException_init('Should not be used'.toString());
    }
  });
  Object.defineProperty(ArrayBroadcastChannel$Subscriber.prototype, 'isBufferFull', {
    get: function () {
      throw IllegalStateException_init('Should not be used'.toString());
    }
  });
  ArrayBroadcastChannel$Subscriber.prototype.cancel_dbl4no$$default = function (cause) {
    var $receiver = this.close_dbl4no$(cause);
    if ($receiver)
      this.broadcastChannel_0.updateHead_0(void 0, this);
    this.clearBuffer_0();
    return $receiver;
  };
  ArrayBroadcastChannel$Subscriber.prototype.clearBuffer_0 = function () {
    this.subHead = this.broadcastChannel_0.tail_0;
  };
  ArrayBroadcastChannel$Subscriber.prototype.checkOffer = function () {
    var tmp$, tmp$_0;
    var updated = false;
    var closed = null;
    loop: while (this.needsToCheckOfferWithoutLock_0()) {
      if (!this.subLock_0.tryLock())
        break;
      var receive;
      var token;
      try {
        var result = this.peekUnderLock_0();
        if (result === POLL_FAILED)
          continue loop;
        else if (Kotlin.isType(result, Closed)) {
          closed = result;
          break loop;
        }
        tmp$ = this.takeFirstReceiveOrPeekClosed();
        if (tmp$ == null) {
          break;
        }
        receive = tmp$;
        if (Kotlin.isType(receive, Closed))
          break;
        token = receive.tryResumeReceive_19pj23$((tmp$_0 = result) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE(), null);
        if (token == null)
          continue;
        var subHead = this.subHead;
        this.subHead = subHead.add(Kotlin.Long.fromInt(1));
        updated = true;
      }
      finally {
        this.subLock_0.unlock();
      }
      ensureNotNull(receive).completeResumeReceive_za3rmp$(ensureNotNull(token));
    }
    if (closed != null) {
      this.close_dbl4no$(closed.closeCause);
    }
    return updated;
  };
  ArrayBroadcastChannel$Subscriber.prototype.pollInternal = function () {
    var tmp$, tmp$_0;
    var updated = {v: false};
    var result = this.peekUnderLock_0();
    if (!Kotlin.isType(result, Closed))
      if (result !== POLL_FAILED) {
        var subHead = this.subHead;
        this.subHead = subHead.add(Kotlin.Long.fromInt(1));
        updated.v = true;
      }
    var result_0 = result;
    if ((tmp$_0 = Kotlin.isType(tmp$ = result_0, Closed) ? tmp$ : null) != null) {
      this.close_dbl4no$(tmp$_0.closeCause);
    }
    if (this.checkOffer())
      updated.v = true;
    if (updated.v)
      this.broadcastChannel_0.updateHead_0();
    return result_0;
  };
  ArrayBroadcastChannel$Subscriber.prototype.pollSelectInternal_qqlfgi$ = function (select) {
    var tmp$, tmp$_0;
    var updated = {v: false};
    var result = this.peekUnderLock_0();
    if (!Kotlin.isType(result, Closed))
      if (result !== POLL_FAILED) {
        if (!select.trySelect_s8jyv4$(null)) {
          result = ALREADY_SELECTED;
        }
         else {
          var subHead = this.subHead;
          this.subHead = subHead.add(Kotlin.Long.fromInt(1));
          updated.v = true;
        }
      }
    var result_0 = result;
    if ((tmp$_0 = Kotlin.isType(tmp$ = result_0, Closed) ? tmp$ : null) != null) {
      this.close_dbl4no$(tmp$_0.closeCause);
    }
    if (this.checkOffer())
      updated.v = true;
    if (updated.v)
      this.broadcastChannel_0.updateHead_0();
    return result_0;
  };
  ArrayBroadcastChannel$Subscriber.prototype.needsToCheckOfferWithoutLock_0 = function () {
    if (this.closedForReceive != null)
      return false;
    if (this.isBufferEmpty && this.broadcastChannel_0.closedForReceive == null)
      return false;
    return true;
  };
  ArrayBroadcastChannel$Subscriber.prototype.peekUnderLock_0 = function () {
    var tmp$;
    var subHead = this.subHead;
    var closedBroadcast = this.broadcastChannel_0.closedForReceive;
    var tail = this.broadcastChannel_0.tail_0;
    if (subHead.compareTo_11rb$(tail) >= 0) {
      return (tmp$ = closedBroadcast != null ? closedBroadcast : this.closedForReceive) != null ? tmp$ : POLL_FAILED;
    }
    var result = this.broadcastChannel_0.elementAt_0(subHead);
    var closedSub = this.closedForReceive;
    if (closedSub != null)
      return closedSub;
    return result;
  };
  ArrayBroadcastChannel$Subscriber.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Subscriber',
    interfaces: [SubscriptionReceiveChannel, AbstractChannel, ReceiveChannel]
  };
  Object.defineProperty(ArrayBroadcastChannel.prototype, 'bufferDebugString', {
    get: function () {
      return '(buffer:capacity=' + this.buffer_0.length + ',size=' + this.size_0 + ')';
    }
  });
  ArrayBroadcastChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ArrayBroadcastChannel',
    interfaces: [BroadcastChannel, AbstractSendChannel]
  };
  function ArrayChannel(capacity) {
    AbstractChannel.call(this);
    this.capacity = capacity;
    if (!(this.capacity >= 1)) {
      var message = 'ArrayChannel capacity must be at least 1, but ' + this.capacity + ' was specified';
      throw IllegalArgumentException_init(message.toString());
    }
    this.lock_pga5tr$_0 = new NoOpLock();
    this.buffer_n6mt8q$_0 = Kotlin.newArray(this.capacity, null);
    this.head_pdwjxm$_0 = 0;
    this.size_pk1cyx$_0 = 0;
  }
  Object.defineProperty(ArrayChannel.prototype, 'isBufferAlwaysEmpty', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(ArrayChannel.prototype, 'isBufferEmpty', {
    get: function () {
      return this.size_pk1cyx$_0 === 0;
    }
  });
  Object.defineProperty(ArrayChannel.prototype, 'isBufferAlwaysFull', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(ArrayChannel.prototype, 'isBufferFull', {
    get: function () {
      return this.size_pk1cyx$_0 === this.capacity;
    }
  });
  ArrayChannel.prototype.offerInternal_11rb$ = function (element) {
    var receive = {v: null};
    var token = {v: null};
    action$break: do {
      var tmp$, tmp$_0;
      var size = this.size_pk1cyx$_0;
      if ((tmp$ = this.closedForSend) != null) {
        return tmp$;
      }
      if (size < this.capacity) {
        this.size_pk1cyx$_0 = size + 1 | 0;
        if (size === 0) {
          loop: while (true) {
            tmp$_0 = this.takeFirstReceiveOrPeekClosed();
            if (tmp$_0 == null) {
              break loop;
            }
            receive.v = tmp$_0;
            if (Kotlin.isType(receive.v, Closed)) {
              this.size_pk1cyx$_0 = size;
              return ensureNotNull(receive.v);
            }
            token.v = ensureNotNull(receive.v).tryResumeReceive_19pj23$(element, null);
            if (token.v != null) {
              this.size_pk1cyx$_0 = size;
              break action$break;
            }
          }
        }
        this.buffer_n6mt8q$_0[(this.head_pdwjxm$_0 + size | 0) % this.capacity] = element;
        return OFFER_SUCCESS;
      }
      return OFFER_FAILED;
    }
     while (false);
    ensureNotNull(receive.v).completeResumeReceive_za3rmp$(ensureNotNull(token.v));
    return ensureNotNull(receive.v).offerResult;
  };
  ArrayChannel.prototype.offerSelectInternal_26cf95$ = function (element, select) {
    var receive = {v: null};
    var token = {v: null};
    action$break: do {
      var tmp$;
      var size = this.size_pk1cyx$_0;
      if ((tmp$ = this.closedForSend) != null) {
        return tmp$;
      }
      if (size < this.capacity) {
        this.size_pk1cyx$_0 = size + 1 | 0;
        if (size === 0) {
          loop: while (true) {
            var offerOp = this.describeTryOffer_11rb$(element);
            var failure = select.performAtomicTrySelect_qopb37$(offerOp);
            if (failure == null) {
              this.size_pk1cyx$_0 = size;
              receive.v = offerOp.result;
              token.v = offerOp.resumeToken;
              if (!(token.v != null)) {
                var message = 'Check failed.';
                throw IllegalStateException_init(message.toString());
              }
              break action$break;
            }
             else if (failure === OFFER_FAILED)
              break loop;
            else if (failure === ALREADY_SELECTED || Kotlin.isType(failure, Closed)) {
              this.size_pk1cyx$_0 = size;
              return failure;
            }
             else {
              throw IllegalStateException_init(('performAtomicTrySelect(describeTryOffer) returned ' + toString(failure)).toString());
            }
          }
        }
        if (!select.trySelect_s8jyv4$(null)) {
          this.size_pk1cyx$_0 = size;
          return ALREADY_SELECTED;
        }
        this.buffer_n6mt8q$_0[(this.head_pdwjxm$_0 + size | 0) % this.capacity] = element;
        return OFFER_SUCCESS;
      }
      return OFFER_FAILED;
    }
     while (false);
    ensureNotNull(receive.v).completeResumeReceive_za3rmp$(ensureNotNull(token.v));
    return ensureNotNull(receive.v).offerResult;
  };
  ArrayChannel.prototype.pollInternal = function () {
    var send = {v: null};
    var token = {v: null};
    var result = {v: null};
    var tmp$, tmp$_0;
    var size = this.size_pk1cyx$_0;
    if (size === 0)
      return (tmp$ = this.closedForSend) != null ? tmp$ : POLL_FAILED;
    result.v = this.buffer_n6mt8q$_0[this.head_pdwjxm$_0];
    this.buffer_n6mt8q$_0[this.head_pdwjxm$_0] = null;
    this.size_pk1cyx$_0 = size - 1 | 0;
    var replacement = POLL_FAILED;
    if (size === this.capacity) {
      loop: while (true) {
        tmp$_0 = this.takeFirstSendOrPeekClosed();
        if (tmp$_0 == null) {
          break;
        }
        send.v = tmp$_0;
        token.v = ensureNotNull(send.v).tryResumeSend_s8jyv4$(null);
        if (token.v != null) {
          replacement = ensureNotNull(send.v).pollResult;
          break loop;
        }
      }
    }
    if (replacement !== POLL_FAILED && !Kotlin.isType(replacement, Closed)) {
      this.size_pk1cyx$_0 = size;
      this.buffer_n6mt8q$_0[(this.head_pdwjxm$_0 + size | 0) % this.capacity] = replacement;
    }
    this.head_pdwjxm$_0 = (this.head_pdwjxm$_0 + 1 | 0) % this.capacity;
    if (token.v != null)
      ensureNotNull(send.v).completeResumeSend_za3rmp$(ensureNotNull(token.v));
    return result.v;
  };
  ArrayChannel.prototype.pollSelectInternal_qqlfgi$ = function (select) {
    var send = {v: null};
    var token = {v: null};
    var result = {v: null};
    var tmp$;
    var size = this.size_pk1cyx$_0;
    if (size === 0)
      return (tmp$ = this.closedForSend) != null ? tmp$ : POLL_FAILED;
    result.v = this.buffer_n6mt8q$_0[this.head_pdwjxm$_0];
    this.buffer_n6mt8q$_0[this.head_pdwjxm$_0] = null;
    this.size_pk1cyx$_0 = size - 1 | 0;
    var replacement = POLL_FAILED;
    if (size === this.capacity) {
      loop: while (true) {
        var pollOp = this.describeTryPoll();
        var failure = select.performAtomicTrySelect_qopb37$(pollOp);
        if (failure == null) {
          send.v = pollOp.result;
          token.v = pollOp.resumeToken;
          if (!(token.v != null)) {
            var message = 'Check failed.';
            throw IllegalStateException_init(message.toString());
          }
          replacement = ensureNotNull(send.v).pollResult;
          break loop;
        }
         else if (failure === POLL_FAILED)
          break loop;
        else if (failure === ALREADY_SELECTED) {
          this.size_pk1cyx$_0 = size;
          this.buffer_n6mt8q$_0[this.head_pdwjxm$_0] = result.v;
          return failure;
        }
         else if (Kotlin.isType(failure, Closed)) {
          send.v = failure;
          token.v = failure.tryResumeSend_s8jyv4$(null);
          replacement = failure;
          break loop;
        }
         else {
          throw IllegalStateException_init(('performAtomicTrySelect(describeTryOffer) returned ' + toString(failure)).toString());
        }
      }
    }
    if (replacement !== POLL_FAILED && !Kotlin.isType(replacement, Closed)) {
      this.size_pk1cyx$_0 = size;
      this.buffer_n6mt8q$_0[(this.head_pdwjxm$_0 + size | 0) % this.capacity] = replacement;
    }
     else {
      if (!select.trySelect_s8jyv4$(null)) {
        this.size_pk1cyx$_0 = size;
        this.buffer_n6mt8q$_0[this.head_pdwjxm$_0] = result.v;
        return ALREADY_SELECTED;
      }
    }
    this.head_pdwjxm$_0 = (this.head_pdwjxm$_0 + 1 | 0) % this.capacity;
    if (token.v != null)
      ensureNotNull(send.v).completeResumeSend_za3rmp$(ensureNotNull(token.v));
    return result.v;
  };
  ArrayChannel.prototype.cleanupSendQueueOnCancel = function () {
    var times = this.size_pk1cyx$_0;
    for (var index = 0; index < times; index++) {
      this.buffer_n6mt8q$_0[this.head_pdwjxm$_0] = 0;
      this.head_pdwjxm$_0 = (this.head_pdwjxm$_0 + 1 | 0) % this.capacity;
    }
    this.size_pk1cyx$_0 = 0;
    AbstractChannel.prototype.cleanupSendQueueOnCancel.call(this);
  };
  Object.defineProperty(ArrayChannel.prototype, 'bufferDebugString', {
    get: function () {
      return '(buffer:capacity=' + this.buffer_n6mt8q$_0.length + ',size=' + this.size_pk1cyx$_0 + ')';
    }
  });
  ArrayChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ArrayChannel',
    interfaces: [AbstractChannel]
  };
  function broadcast$lambda(this$broadcast_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$broadcast$lambda(this$broadcast_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$broadcast$lambda(this$broadcast_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$broadcast = this$broadcast_0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$broadcast$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$broadcast$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$broadcast$lambda.prototype.constructor = Coroutine$broadcast$lambda;
  Coroutine$broadcast$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$broadcast.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var e_0 = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$$receiver.send_11rb$(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 2;
            continue;
          case 7:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function broadcast($receiver, capacity, start) {
    if (capacity === void 0)
      capacity = 1;
    if (start === void 0)
      start = CoroutineStart$LAZY_getInstance();
    return broadcast_0(Unconfined_getInstance(), capacity, start, void 0, consumes($receiver), broadcast$lambda($receiver));
  }
  function broadcast_0(context, capacity, start, parent, onCompletion, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (capacity === void 0)
      capacity = 1;
    if (start === void 0)
      start = CoroutineStart$LAZY_getInstance();
    if (parent === void 0)
      parent = null;
    if (onCompletion === void 0)
      onCompletion = null;
    var channel = BroadcastChannel_0(capacity);
    var newContext = newCoroutineContext(context, parent);
    var coroutine = start.isLazy ? new LazyBroadcastCoroutine(newContext, channel, block) : new BroadcastCoroutine(newContext, channel, true);
    if (onCompletion != null)
      coroutine.invokeOnCompletion_f05bi3$(onCompletion);
    coroutine.start_1qsk3b$(start, coroutine, block);
    return coroutine;
  }
  function BroadcastCoroutine(parentContext, _channel, active) {
    AbstractCoroutine.call(this, parentContext, active);
    this._channel_0 = _channel;
  }
  Object.defineProperty(BroadcastCoroutine.prototype, 'channel', {
    get: function () {
      return this;
    }
  });
  BroadcastCoroutine.prototype.cancel_dbl4no$$default = function (cause) {
    return this.cancel_dbl4no$(cause, AbstractCoroutine.prototype.cancel_dbl4no$$default.bind(this));
  };
  BroadcastCoroutine.prototype.onCancellationInternal_kybjp5$ = function (exceptionally) {
    var tmp$;
    var cause = exceptionally != null ? exceptionally.cause : null;
    if (Kotlin.isType(exceptionally, Cancelled))
      tmp$ = this._channel_0.cancel_dbl4no$(cause);
    else
      tmp$ = this._channel_0.close_dbl4no$(cause);
    var processed = tmp$;
    if (!processed && cause != null)
      handleCoroutineException(this.context, cause);
  };
  BroadcastCoroutine.prototype.send_11rb$ = function (element, continuation) {
    return this._channel_0.send_11rb$(element, continuation);
  };
  Object.defineProperty(BroadcastCoroutine.prototype, 'isClosedForSend', {
    get: function () {
      return this._channel_0.isClosedForSend;
    }
  });
  Object.defineProperty(BroadcastCoroutine.prototype, 'isFull', {
    get: function () {
      return this._channel_0.isFull;
    }
  });
  Object.defineProperty(BroadcastCoroutine.prototype, 'onSend', {
    get: function () {
      return this._channel_0.onSend;
    }
  });
  BroadcastCoroutine.prototype.close_dbl4no$$default = function (cause) {
    return this._channel_0.close_dbl4no$$default(cause);
  };
  BroadcastCoroutine.prototype.offer_11rb$ = function (element) {
    return this._channel_0.offer_11rb$(element);
  };
  BroadcastCoroutine.prototype.open = function () {
    return this._channel_0.open();
  };
  BroadcastCoroutine.prototype.openSubscription = function () {
    return this._channel_0.openSubscription();
  };
  BroadcastCoroutine.prototype.openSubscription1 = function () {
    return this._channel_0.openSubscription1();
  };
  BroadcastCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BroadcastCoroutine',
    interfaces: [BroadcastChannel, ProducerScope, AbstractCoroutine]
  };
  function LazyBroadcastCoroutine(parentContext, channel, block) {
    BroadcastCoroutine.call(this, parentContext, channel, false);
    this.block_0 = block;
  }
  LazyBroadcastCoroutine.prototype.openSubscription = function () {
    var subscription = this._channel_0.openSubscription();
    this.start();
    return subscription;
  };
  LazyBroadcastCoroutine.prototype.onStart = function () {
    startCoroutineCancellable_0(this.block_0, this, this);
  };
  LazyBroadcastCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LazyBroadcastCoroutine',
    interfaces: [BroadcastCoroutine]
  };
  function BroadcastChannel() {
    BroadcastChannel$Factory_getInstance();
  }
  function BroadcastChannel$Factory() {
    BroadcastChannel$Factory_instance = this;
  }
  BroadcastChannel$Factory.prototype.invoke_ww73n8$ = function (capacity) {
    return BroadcastChannel_0(capacity);
  };
  BroadcastChannel$Factory.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Factory',
    interfaces: []
  };
  var BroadcastChannel$Factory_instance = null;
  function BroadcastChannel$Factory_getInstance() {
    if (BroadcastChannel$Factory_instance === null) {
      new BroadcastChannel$Factory();
    }
    return BroadcastChannel$Factory_instance;
  }
  BroadcastChannel.prototype.openSubscription1 = function () {
    var tmp$;
    return Kotlin.isType(tmp$ = this.openSubscription(), SubscriptionReceiveChannel) ? tmp$ : throwCCE();
  };
  BroadcastChannel.prototype.open = function () {
    var tmp$;
    return Kotlin.isType(tmp$ = this.openSubscription(), SubscriptionReceiveChannel) ? tmp$ : throwCCE();
  };
  BroadcastChannel.prototype.cancel_dbl4no$ = function (cause, callback$default) {
    if (cause === void 0)
      cause = null;
    return callback$default ? callback$default(cause) : this.cancel_dbl4no$$default(cause);
  };
  BroadcastChannel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'BroadcastChannel',
    interfaces: [SendChannel]
  };
  function BroadcastChannel_0(capacity) {
    switch (capacity) {
      case 0:
        throw IllegalArgumentException_init('Unsupported 0 capacity for BroadcastChannel');
      case 2147483647:
        throw IllegalArgumentException_init('Unsupported UNLIMITED capacity for BroadcastChannel');
      case -1:
        return new ConflatedBroadcastChannel();
      default:return new ArrayBroadcastChannel(capacity);
    }
  }
  function SubscriptionReceiveChannel() {
  }
  SubscriptionReceiveChannel.prototype.close = function () {
    this.cancel_dbl4no$();
  };
  SubscriptionReceiveChannel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SubscriptionReceiveChannel',
    interfaces: [Closeable, ReceiveChannel]
  };
  var use = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.use_e0tfc5$', wrapFunction(function () {
    var Throwable = Error;
    return function ($receiver, block) {
      var exception = null;
      try {
        return block($receiver);
      }
       catch (t) {
        if (Kotlin.isType(t, Throwable)) {
          exception = t;
          throw t;
        }
         else
          throw t;
      }
      finally {
        $receiver.cancel_dbl4no$(exception);
      }
    };
  }));
  function SendChannel() {
  }
  SendChannel.prototype.close_dbl4no$ = function (cause, callback$default) {
    if (cause === void 0)
      cause = null;
    return callback$default ? callback$default(cause) : this.close_dbl4no$$default(cause);
  };
  SendChannel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SendChannel',
    interfaces: []
  };
  function ReceiveChannel() {
  }
  ReceiveChannel.prototype.cancel_dbl4no$ = function (cause, callback$default) {
    if (cause === void 0)
      cause = null;
    return callback$default ? callback$default(cause) : this.cancel_dbl4no$$default(cause);
  };
  ReceiveChannel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ReceiveChannel',
    interfaces: []
  };
  function ChannelIterator() {
  }
  ChannelIterator.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ChannelIterator',
    interfaces: []
  };
  function Channel() {
    Channel$Factory_getInstance();
  }
  function Channel$Factory() {
    Channel$Factory_instance = this;
    this.UNLIMITED = 2147483647;
    this.CONFLATED = -1;
  }
  Channel$Factory.prototype.invoke_ww73n8$ = function (capacity) {
    if (capacity === void 0)
      capacity = 0;
    return Channel_1(capacity);
  };
  Channel$Factory.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Factory',
    interfaces: []
  };
  var Channel$Factory_instance = null;
  function Channel$Factory_getInstance() {
    if (Channel$Factory_instance === null) {
      new Channel$Factory();
    }
    return Channel$Factory_instance;
  }
  Channel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Channel',
    interfaces: [ReceiveChannel, SendChannel]
  };
  function Channel_0() {
    return new RendezvousChannel();
  }
  function Channel_1(capacity) {
    switch (capacity) {
      case 0:
        return new RendezvousChannel();
      case 2147483647:
        return new LinkedListChannel();
      case -1:
        return new ConflatedChannel();
      default:return new ArrayChannel(capacity);
    }
  }
  function ClosedSendChannelException(message) {
    CancellationException.call(this, message);
    this.name = 'ClosedSendChannelException';
  }
  ClosedSendChannelException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ClosedSendChannelException',
    interfaces: [CancellationException]
  };
  function ClosedReceiveChannelException(message) {
    NoSuchElementException.call(this, message);
    this.name = 'ClosedReceiveChannelException';
  }
  ClosedReceiveChannelException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ClosedReceiveChannelException',
    interfaces: [NoSuchElementException]
  };
  function ChannelCoroutine(parentContext, _channel, active) {
    AbstractCoroutine.call(this, parentContext, active);
    this._channel_0 = _channel;
  }
  Object.defineProperty(ChannelCoroutine.prototype, 'channel', {
    get: function () {
      return this;
    }
  });
  ChannelCoroutine.prototype.receive = function (continuation) {
    return this._channel_0.receive(continuation);
  };
  ChannelCoroutine.prototype.send_11rb$ = function (element, continuation) {
    return this._channel_0.send_11rb$(element, continuation);
  };
  ChannelCoroutine.prototype.receiveOrNull = function (continuation) {
    return this._channel_0.receiveOrNull(continuation);
  };
  ChannelCoroutine.prototype.cancel_dbl4no$$default = function (cause) {
    return this.cancel_dbl4no$(cause, AbstractCoroutine.prototype.cancel_dbl4no$$default.bind(this));
  };
  Object.defineProperty(ChannelCoroutine.prototype, 'isClosedForReceive', {
    get: function () {
      return this._channel_0.isClosedForReceive;
    }
  });
  Object.defineProperty(ChannelCoroutine.prototype, 'isClosedForSend', {
    get: function () {
      return this._channel_0.isClosedForSend;
    }
  });
  Object.defineProperty(ChannelCoroutine.prototype, 'isEmpty', {
    get: function () {
      return this._channel_0.isEmpty;
    }
  });
  Object.defineProperty(ChannelCoroutine.prototype, 'isFull', {
    get: function () {
      return this._channel_0.isFull;
    }
  });
  Object.defineProperty(ChannelCoroutine.prototype, 'onReceive', {
    get: function () {
      return this._channel_0.onReceive;
    }
  });
  Object.defineProperty(ChannelCoroutine.prototype, 'onReceiveOrNull', {
    get: function () {
      return this._channel_0.onReceiveOrNull;
    }
  });
  Object.defineProperty(ChannelCoroutine.prototype, 'onSend', {
    get: function () {
      return this._channel_0.onSend;
    }
  });
  ChannelCoroutine.prototype.close_dbl4no$$default = function (cause) {
    return this._channel_0.close_dbl4no$$default(cause);
  };
  ChannelCoroutine.prototype.iterator = function () {
    return this._channel_0.iterator();
  };
  ChannelCoroutine.prototype.offer_11rb$ = function (element) {
    return this._channel_0.offer_11rb$(element);
  };
  ChannelCoroutine.prototype.poll = function () {
    return this._channel_0.poll();
  };
  ChannelCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ChannelCoroutine',
    interfaces: [Channel, AbstractCoroutine]
  };
  var DEFAULT_CLOSE_MESSAGE;
  function asReceiveChannel$lambda(this$asReceiveChannel_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$asReceiveChannel$lambda(this$asReceiveChannel_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$asReceiveChannel$lambda(this$asReceiveChannel_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$asReceiveChannel = this$asReceiveChannel_0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$asReceiveChannel$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$asReceiveChannel$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$asReceiveChannel$lambda.prototype.constructor = Coroutine$asReceiveChannel$lambda;
  Coroutine$asReceiveChannel$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$asReceiveChannel.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$tmp$.hasNext()) {
              this.state_0 = 4;
              continue;
            }

            var element = this.local$tmp$.next();
            this.state_0 = 3;
            this.result_0 = this.local$$receiver.send_11rb$(element, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function asReceiveChannel($receiver, context) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, void 0, asReceiveChannel$lambda($receiver));
  }
  function asReceiveChannel$lambda_0(this$asReceiveChannel_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$asReceiveChannel$lambda_0(this$asReceiveChannel_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$asReceiveChannel$lambda_0(this$asReceiveChannel_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$asReceiveChannel = this$asReceiveChannel_0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$asReceiveChannel$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$asReceiveChannel$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$asReceiveChannel$lambda_0.prototype.constructor = Coroutine$asReceiveChannel$lambda_0;
  Coroutine$asReceiveChannel$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$asReceiveChannel.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$tmp$.hasNext()) {
              this.state_0 = 4;
              continue;
            }

            var element = this.local$tmp$.next();
            this.state_0 = 3;
            this.result_0 = this.local$$receiver.send_11rb$(element, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function asReceiveChannel_0($receiver, context) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, void 0, asReceiveChannel$lambda_0($receiver));
  }
  var consume = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.consume_tbmr54$', function ($receiver, block) {
    var channel = $receiver.openSubscription();
    try {
      return block(channel);
    }
    finally {
      channel.cancel_dbl4no$();
    }
  });
  function consumeEach($receiver, action, continuation, suspended) {
    var instance = new Coroutine$consumeEach($receiver, action, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$consumeEach($receiver, action, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$channel = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$action = action;
  }
  Coroutine$consumeEach.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$consumeEach.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$consumeEach.prototype.constructor = Coroutine$consumeEach;
  Coroutine$consumeEach.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$channel = this.local$$receiver.openSubscription();
            this.exceptionState_0 = 7;
            this.local$tmp$ = this.local$channel.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var element = this.result_0;
            this.local$action(element);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [6];
            this.state_0 = 8;
            continue;
          case 6:
            return Unit;
          case 7:
            this.finallyPath_0 = [9];
            this.state_0 = 8;
            continue;
          case 8:
            this.local$channel.cancel_dbl4no$();
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.consumeEach_4puyb6$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    return function ($receiver, action, continuation) {
      var channel = $receiver.openSubscription();
      try {
        var tmp$;
        tmp$ = channel.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var element = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          action(element);
        }
      }
      finally {
        channel.cancel_dbl4no$();
      }
      return Unit;
    };
  }));
  function consumeEach_0($receiver_0, action_0, continuation_0, suspended) {
    var instance = new Coroutine$consumeEach_0($receiver_0, action_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$consumeEach_0($receiver_0, action_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 10;
    this.local$channel = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$action = action_0;
  }
  Coroutine$consumeEach_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$consumeEach_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$consumeEach_0.prototype.constructor = Coroutine$consumeEach_0;
  Coroutine$consumeEach_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$channel = this.local$$receiver.openSubscription();
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$channel.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 6;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var element = this.result_0;
            this.state_0 = 5;
            this.result_0 = this.local$action(element, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 1;
            continue;
          case 6:
            this.exceptionState_0 = 10;
            this.finallyPath_0 = [7];
            this.state_0 = 9;
            continue;
          case 7:
            this.result_0 = Unit;
            return this.result_0;
          case 8:
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 9:
            this.local$channel.cancel_dbl4no$();
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 10) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function consumes$lambda(this$consumes) {
    return function (cause) {
      this$consumes.cancel_dbl4no$(cause);
      return Unit;
    };
  }
  function consumes($receiver) {
    return consumes$lambda($receiver);
  }
  function consumesAll$lambda(closure$channels) {
    return function (cause) {
      var tmp$, tmp$_0;
      var exception = null;
      tmp$ = closure$channels;
      for (tmp$_0 = 0; tmp$_0 !== tmp$.length; ++tmp$_0) {
        var channel = tmp$[tmp$_0];
        try {
          channel.cancel_dbl4no$(cause);
        }
         catch (e) {
          if (Kotlin.isType(e, Throwable)) {
            if (exception == null) {
              exception = e;
            }
          }
           else
            throw e;
        }
      }
      if (exception != null) {
        throw exception;
      }
      return Unit;
    };
  }
  function consumesAll(channels) {
    return consumesAll$lambda(channels);
  }
  var consume_0 = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.consume_e0tfc5$', wrapFunction(function () {
    var Throwable = Error;
    return function ($receiver, block) {
      var cause = null;
      try {
        return block($receiver);
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
    };
  }));
  function consumeEach_1($receiver, action, continuation, suspended) {
    var instance = new Coroutine$consumeEach_1($receiver, action, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$consumeEach_1($receiver, action, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$action = action;
  }
  Coroutine$consumeEach_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$consumeEach_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$consumeEach_1.prototype.constructor = Coroutine$consumeEach_1;
  Coroutine$consumeEach_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$action(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            return Unit;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.consumeEach_z9p47f$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, action, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          action(e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      return Unit;
    };
  }));
  function consumeEach_2($receiver_0, action_0, continuation_0, suspended) {
    var instance = new Coroutine$consumeEach_2($receiver_0, action_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$consumeEach_2($receiver_0, action_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 10;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$action = action_0;
  }
  Coroutine$consumeEach_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$consumeEach_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$consumeEach_2.prototype.constructor = Coroutine$consumeEach_2;
  Coroutine$consumeEach_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 7;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 6;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.state_0 = 5;
            this.result_0 = this.local$action(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 1;
            continue;
          case 6:
            this.exceptionState_0 = 10;
            this.finallyPath_0 = [9];
            this.state_0 = 8;
            continue;
          case 7:
            this.finallyPath_0 = [10];
            this.exceptionState_0 = 8;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 8:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 9:
            this.result_0 = Unit;
            return this.result_0;
          case 10:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 10) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function consumeEachIndexed($receiver, action, continuation, suspended) {
    var instance = new Coroutine$consumeEachIndexed($receiver, action, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$consumeEachIndexed($receiver, action, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$action = action;
  }
  Coroutine$consumeEachIndexed.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$consumeEachIndexed.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$consumeEachIndexed.prototype.constructor = Coroutine$consumeEachIndexed;
  Coroutine$consumeEachIndexed.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            this.local$action(new IndexedValue_init((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.consumeEachIndexed_g8cfyq$', wrapFunction(function () {
    var IndexedValue_init = Kotlin.kotlin.collections.IndexedValue;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, action, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          action(new IndexedValue_init((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
    };
  }));
  function elementAt($receiver_0, index_0, continuation_0, suspended) {
    var instance = new Coroutine$elementAt($receiver_0, index_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$elementAt($receiver_0, index_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 12;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$count = void 0;
    this.local$$receiver = $receiver_0;
    this.local$index = index_0;
  }
  Coroutine$elementAt.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$elementAt.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$elementAt.prototype.constructor = Coroutine$elementAt;
  Coroutine$elementAt.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 1;
            continue;
          case 1:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            var tmp$;
            if (this.local$index < 0) {
              throw new IndexOutOfBoundsException("ReceiveChannel doesn't contain element at index " + this.local$index + '.');
            }

            this.local$count = 0;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 2;
            continue;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var element = this.result_0;
            if (this.local$index === (tmp$ = this.local$count, this.local$count = tmp$ + 1 | 0, tmp$)) {
              this.result_0 = element;
              this.exceptionState_0 = 8;
              this.finallyPath_0 = [11];
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 6:
            this.state_0 = 2;
            continue;
          case 7:
            throw new IndexOutOfBoundsException("ReceiveChannel doesn't contain element at index " + this.local$index + '.');
          case 8:
            this.finallyPath_0 = [12];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            if (!false) {
              this.state_0 = 11;
              continue;
            }

            this.state_0 = 1;
            continue;
          case 11:
            return this.result_0;
          case 12:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 12) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function elementAtOrElse($receiver, index, defaultValue, continuation, suspended) {
    var instance = new Coroutine$elementAtOrElse($receiver, index, defaultValue, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$elementAtOrElse($receiver, index, defaultValue, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 13;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$count = void 0;
    this.local$$receiver = $receiver;
    this.local$index = index;
    this.local$defaultValue = defaultValue;
  }
  Coroutine$elementAtOrElse.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$elementAtOrElse.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$elementAtOrElse.prototype.constructor = Coroutine$elementAtOrElse;
  Coroutine$elementAtOrElse.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 1;
            var tmp$;
            if (this.local$index < 0) {
              this.exceptionState_0 = 13;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = this.local$defaultValue(this.local$index);
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 1:
            this.finallyPath_0 = [13];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 2:
            return this.$returnValue;
          case 3:
            this.local$count = 0;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 4;
            continue;
          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            if (!this.result_0) {
              this.state_0 = 10;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 6:
            this.state_0 = 7;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            var element = this.result_0;
            if (this.local$index === (tmp$ = this.local$count, this.local$count = tmp$ + 1 | 0, tmp$)) {
              this.exceptionState_0 = 13;
              this.finallyPath_0 = [8];
              this.state_0 = 12;
              this.$returnValue = element;
              continue;
            }
             else {
              this.state_0 = 9;
              continue;
            }

          case 8:
            return this.$returnValue;
          case 9:
            this.state_0 = 4;
            continue;
          case 10:
            this.exceptionState_0 = 13;
            this.finallyPath_0 = [11];
            this.state_0 = 12;
            this.$returnValue = this.local$defaultValue(this.local$index);
            continue;
          case 11:
            return this.$returnValue;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            throw this.exception_0;
          case 14:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 13) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.elementAtOrElse_gefu6u$', wrapFunction(function () {
    var Throwable = Error;
    return function ($receiver, index, defaultValue, continuation) {
      var cause = null;
      try {
        var tmp$, tmp$_0;
        if (index < 0)
          return defaultValue(index);
        var count = 0;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var element = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (index === (tmp$_0 = count, count = tmp$_0 + 1 | 0, tmp$_0))
            return element;
        }
        return defaultValue(index);
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
    };
  }));
  function elementAtOrNull($receiver_0, index_0, continuation_0, suspended) {
    var instance = new Coroutine$elementAtOrNull($receiver_0, index_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$elementAtOrNull($receiver_0, index_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 13;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$count = void 0;
    this.local$$receiver = $receiver_0;
    this.local$index = index_0;
  }
  Coroutine$elementAtOrNull.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$elementAtOrNull.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$elementAtOrNull.prototype.constructor = Coroutine$elementAtOrNull;
  Coroutine$elementAtOrNull.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 1;
            var tmp$;
            if (this.local$index < 0) {
              this.exceptionState_0 = 13;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 1:
            this.finallyPath_0 = [13];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 2:
            return this.$returnValue;
          case 3:
            this.local$count = 0;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 4;
            continue;
          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            if (!this.result_0) {
              this.state_0 = 10;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 6:
            this.state_0 = 7;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            var element = this.result_0;
            if (this.local$index === (tmp$ = this.local$count, this.local$count = tmp$ + 1 | 0, tmp$)) {
              this.exceptionState_0 = 13;
              this.finallyPath_0 = [8];
              this.state_0 = 12;
              this.$returnValue = element;
              continue;
            }
             else {
              this.state_0 = 9;
              continue;
            }

          case 8:
            return this.$returnValue;
          case 9:
            this.state_0 = 4;
            continue;
          case 10:
            this.exceptionState_0 = 13;
            this.finallyPath_0 = [11];
            this.state_0 = 12;
            this.$returnValue = null;
            continue;
          case 11:
            return this.$returnValue;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            throw this.exception_0;
          case 14:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 13) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function find($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$find($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$find($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 12;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$find.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$find.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$find.prototype.constructor = Coroutine$find;
  Coroutine$find.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 1;
            continue;
          case 1:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 2;
            continue;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.result_0 = e_0;
              this.exceptionState_0 = 8;
              this.finallyPath_0 = [11];
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 6:
            this.state_0 = 2;
            continue;
          case 7:
            this.exceptionState_0 = 12;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [12];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            this.result_0 = null;
            if (!false) {
              this.state_0 = 11;
              continue;
            }

            this.state_0 = 1;
            continue;
          case 11:
            return this.result_0;
          case 12:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 12) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.find_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      firstOrNull$break: do {
        var cause = null;
        try {
          var tmp$;
          tmp$ = $receiver.iterator();
          while (true) {
            Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
            if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
              break;
            Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
            var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
            if (predicate(e_0)) {
              Kotlin.setCoroutineResult(e_0, Kotlin.coroutineReceiver());
              break firstOrNull$break;
            }
          }
        }
         catch (e) {
          if (Kotlin.isType(e, Throwable)) {
            cause = e;
            throw e;
          }
           else
            throw e;
        }
        finally {
          $receiver.cancel_dbl4no$(cause);
        }
        Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
        Kotlin.setCoroutineResult(null, Kotlin.coroutineReceiver());
      }
       while (false);
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function findLast($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$findLast($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$findLast($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$last = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$findLast.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$findLast.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$findLast.prototype.constructor = Coroutine$findLast;
  Coroutine$findLast.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$last = {v: null};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.local$last.v = e_0;
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            this.result_0 = this.local$last.v;
            return this.result_0;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.findLast_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var last = {v: null};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            last.v = e_0;
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      Kotlin.setCoroutineResult(last.v, Kotlin.coroutineReceiver());
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function first($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$first($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$first($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 7;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$first.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$first.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$first.prototype.constructor = Coroutine$first;
  Coroutine$first.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 4;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0)
              throw new NoSuchElementException('ReceiveChannel is empty.');
            this.state_0 = 2;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.exceptionState_0 = 7;
            this.finallyPath_0 = [3];
            this.state_0 = 5;
            this.$returnValue = this.result_0;
            continue;
          case 3:
            return this.$returnValue;
          case 4:
            this.finallyPath_0 = [7];
            this.exceptionState_0 = 5;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 5:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 6:
            return;
          case 7:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 7) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function first_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$first_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$first_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$first_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$first_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$first_0.prototype.constructor = Coroutine$first_0;
  Coroutine$first_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = e_0;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            throw new NoSuchElementException_init('ReceiveChannel contains no element matching the predicate.');
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.first_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var NoSuchElementException_init = Kotlin.kotlin.NoSuchElementException;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            return e_0;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      throw new NoSuchElementException_init('ReceiveChannel contains no element matching the predicate.');
    };
  }));
  function firstOrNull($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$firstOrNull($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$firstOrNull($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$firstOrNull.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$firstOrNull.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$firstOrNull.prototype.constructor = Coroutine$firstOrNull;
  Coroutine$firstOrNull.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 9;
              this.finallyPath_0 = [2];
              this.state_0 = 7;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [5];
            this.state_0 = 7;
            this.$returnValue = this.result_0;
            continue;
          case 5:
            return this.$returnValue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            return;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function firstOrNull_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$firstOrNull_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$firstOrNull_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$firstOrNull_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$firstOrNull_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$firstOrNull_0.prototype.constructor = Coroutine$firstOrNull_0;
  Coroutine$firstOrNull_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = e_0;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return null;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.firstOrNull_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            return e_0;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return null;
    };
  }));
  function indexOf_0($receiver_0, element_0, continuation_0, suspended) {
    var instance = new Coroutine$indexOf($receiver_0, element_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$indexOf($receiver_0, element_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 11;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$element = element_0;
  }
  Coroutine$indexOf.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$indexOf.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$indexOf.prototype.constructor = Coroutine$indexOf;
  Coroutine$indexOf.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (equals(this.local$element, e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = this.local$index.v;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.local$index.v = this.local$index.v + 1 | 0;
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return -1;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function indexOfFirst($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$indexOfFirst($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$indexOfFirst($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$indexOfFirst.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$indexOfFirst.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$indexOfFirst.prototype.constructor = Coroutine$indexOfFirst;
  Coroutine$indexOfFirst.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = this.local$index.v;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.local$index.v = this.local$index.v + 1 | 0;
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return -1;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.indexOfFirst_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            return index.v;
          index.v = index.v + 1 | 0;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return -1;
    };
  }));
  function indexOfLast($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$indexOfLast($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$indexOfLast($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$lastIndex = void 0;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$indexOfLast.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$indexOfLast.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$indexOfLast.prototype.constructor = Coroutine$indexOfLast;
  Coroutine$indexOfLast.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$lastIndex = {v: -1};
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0))
              this.local$lastIndex.v = this.local$index.v;
            this.local$index.v = this.local$index.v + 1 | 0;
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$lastIndex.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.indexOfLast_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var lastIndex = {v: -1};
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            lastIndex.v = index.v;
          index.v = index.v + 1 | 0;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return lastIndex.v;
    };
  }));
  function last($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$last($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$last($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 12;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$last = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$last.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$last.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$last.prototype.constructor = Coroutine$last;
  Coroutine$last.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 9;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0)
              throw new NoSuchElementException('ReceiveChannel is empty.');
            this.state_0 = 2;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.local$last = this.result_0;
            this.state_0 = 3;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 5;
              continue;
            }

          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.local$last = this.result_0;
            this.state_0 = 3;
            continue;
          case 7:
            this.exceptionState_0 = 12;
            this.finallyPath_0 = [8];
            this.state_0 = 10;
            this.$returnValue = this.local$last;
            continue;
          case 8:
            return this.$returnValue;
          case 9:
            this.finallyPath_0 = [12];
            this.exceptionState_0 = 10;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 10:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 11:
            return;
          case 12:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 12) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function last_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$last_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$last_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$last = void 0;
    this.local$found = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$last_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$last_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$last_0.prototype.constructor = Coroutine$last_0;
  Coroutine$last_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$_0;
            this.local$last = {v: null};
            this.local$found = {v: false};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.local$last.v = e_0;
              this.local$found.v = true;
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            if (!this.local$found.v)
              throw new NoSuchElementException_init('ReceiveChannel contains no element matching the predicate.');
            return (tmp$_0 = this.local$last.v) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.last_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var NoSuchElementException_init = Kotlin.kotlin.NoSuchElementException;
    var Any = Object;
    var throwCCE = Kotlin.throwCCE;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var tmp$_0;
      var last = {v: null};
      var found = {v: false};
      var cause = null;
      try {
        var tmp$_1;
        tmp$_1 = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$_1.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$_1.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            last.v = e_0;
            found.v = true;
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      if (!found.v)
        throw new NoSuchElementException_init('ReceiveChannel contains no element matching the predicate.');
      return (tmp$_0 = last.v) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
    };
  }));
  function lastIndexOf($receiver_0, element_0, continuation_0, suspended) {
    var instance = new Coroutine$lastIndexOf($receiver_0, element_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$lastIndexOf($receiver_0, element_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 9;
    this.local$lastIndex = void 0;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$element = element_0;
  }
  Coroutine$lastIndexOf.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$lastIndexOf.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$lastIndexOf.prototype.constructor = Coroutine$lastIndexOf;
  Coroutine$lastIndexOf.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$lastIndex = {v: -1};
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (equals(this.local$element, e_0))
              this.local$lastIndex.v = this.local$index.v;
            this.local$index.v = this.local$index.v + 1 | 0;
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$lastIndex.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function lastOrNull($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$lastOrNull($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$lastOrNull($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 14;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$last = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$lastOrNull.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$lastOrNull.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$lastOrNull.prototype.constructor = Coroutine$lastOrNull;
  Coroutine$lastOrNull.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 11;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 14;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$last = this.result_0;
            this.state_0 = 5;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            this.local$last = this.result_0;
            this.state_0 = 5;
            continue;
          case 9:
            this.exceptionState_0 = 14;
            this.finallyPath_0 = [10];
            this.state_0 = 12;
            this.$returnValue = this.local$last;
            continue;
          case 10:
            return this.$returnValue;
          case 11:
            this.finallyPath_0 = [14];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            return;
          case 14:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 14) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function lastOrNull_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$lastOrNull_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$lastOrNull_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$last = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$lastOrNull_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$lastOrNull_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$lastOrNull_0.prototype.constructor = Coroutine$lastOrNull_0;
  Coroutine$lastOrNull_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$last = {v: null};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.local$last.v = e_0;
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$last.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.lastOrNull_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var last = {v: null};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            last.v = e_0;
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return last.v;
    };
  }));
  function single($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$single($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$single($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 8;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$single = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$single.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$single.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$single.prototype.constructor = Coroutine$single;
  Coroutine$single.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 5;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0)
              throw new NoSuchElementException('ReceiveChannel is empty.');
            this.state_0 = 2;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.local$single = this.result_0;
            this.state_0 = 3;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (this.result_0)
              throw IllegalArgumentException_init('ReceiveChannel has more than one element.');
            this.exceptionState_0 = 8;
            this.finallyPath_0 = [4];
            this.state_0 = 6;
            this.$returnValue = this.local$single;
            continue;
          case 4:
            return this.$returnValue;
          case 5:
            this.finallyPath_0 = [8];
            this.exceptionState_0 = 6;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 6:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 7:
            return;
          case 8:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 8) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function single_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$single_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$single_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$single = void 0;
    this.local$found = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$single_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$single_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$single_0.prototype.constructor = Coroutine$single_0;
  Coroutine$single_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$_0;
            this.local$single = {v: null};
            this.local$found = {v: false};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              if (this.local$found.v)
                throw IllegalArgumentException_init('ReceiveChannel contains more than one matching element.');
              this.local$single.v = e_0;
              this.local$found.v = true;
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            if (!this.local$found.v)
              throw new NoSuchElementException_init('ReceiveChannel contains no element matching the predicate.');
            return (tmp$_0 = this.local$single.v) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.single_455pvd$', wrapFunction(function () {
    var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
    var Unit = Kotlin.kotlin.Unit;
    var NoSuchElementException_init = Kotlin.kotlin.NoSuchElementException;
    var Any = Object;
    var throwCCE = Kotlin.throwCCE;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var tmp$_0;
      var single = {v: null};
      var found = {v: false};
      var cause = null;
      try {
        var tmp$_1;
        tmp$_1 = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$_1.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$_1.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            if (found.v)
              throw IllegalArgumentException_init('ReceiveChannel contains more than one matching element.');
            single.v = e_0;
            found.v = true;
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      if (!found.v)
        throw new NoSuchElementException_init('ReceiveChannel contains no element matching the predicate.');
      return (tmp$_0 = single.v) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE();
    };
  }));
  function singleOrNull($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$singleOrNull($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$singleOrNull($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 12;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$single = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$singleOrNull.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$singleOrNull.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$singleOrNull.prototype.constructor = Coroutine$singleOrNull;
  Coroutine$singleOrNull.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 9;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 12;
              this.finallyPath_0 = [2];
              this.state_0 = 10;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$single = this.result_0;
            this.state_0 = 5;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            if (this.result_0) {
              this.exceptionState_0 = 12;
              this.finallyPath_0 = [6];
              this.state_0 = 10;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 6:
            return this.$returnValue;
          case 7:
            this.exceptionState_0 = 12;
            this.finallyPath_0 = [8];
            this.state_0 = 10;
            this.$returnValue = this.local$single;
            continue;
          case 8:
            return this.$returnValue;
          case 9:
            this.finallyPath_0 = [12];
            this.exceptionState_0 = 10;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 10:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 11:
            return;
          case 12:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 12) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function singleOrNull_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$singleOrNull_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$singleOrNull_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 13;
    this.local$single = void 0;
    this.local$found = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$e = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$singleOrNull_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$singleOrNull_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$singleOrNull_0.prototype.constructor = Coroutine$singleOrNull_0;
  Coroutine$singleOrNull_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$single = {v: null};
            this.local$found = {v: false};
            this.local$cause = null;
            this.exceptionState_0 = 9;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 8;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$e = this.result_0;
            if (this.local$predicate(this.local$e)) {
              if (this.local$found.v) {
                this.exceptionState_0 = 13;
                this.finallyPath_0 = [5];
                this.state_0 = 10;
                this.$returnValue = null;
                continue;
              }
               else {
                this.state_0 = 6;
                continue;
              }
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.local$single.v = this.local$e;
            this.local$found.v = true;
            this.state_0 = 7;
            continue;
          case 7:
            this.state_0 = 1;
            continue;
          case 8:
            this.exceptionState_0 = 13;
            this.finallyPath_0 = [11];
            this.state_0 = 10;
            continue;
          case 9:
            this.finallyPath_0 = [13];
            this.exceptionState_0 = 10;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 10:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 11:
            this.result_0 = Unit;
            if (!this.local$found.v) {
              return null;
            }
             else {
              this.state_0 = 12;
              continue;
            }

          case 12:
            return this.local$single.v;
          case 13:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 13) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.singleOrNull_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var single = {v: null};
      var found = {v: false};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            if (found.v)
              return null;
            single.v = e_0;
            found.v = true;
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      if (!found.v)
        return null;
      return single.v;
    };
  }));
  function drop$lambda(closure$n_0, this$drop_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$drop$lambda(closure$n_0, this$drop_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$drop$lambda(closure$n_0, this$drop_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$n = closure$n_0;
    this.local$this$drop = this$drop_0;
    this.local$tmp$ = void 0;
    this.local$tmp$_0 = void 0;
    this.local$remaining = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$drop$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$drop$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$drop$lambda.prototype.constructor = Coroutine$drop$lambda;
  Coroutine$drop$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (!(this.local$closure$n >= 0)) {
              var message = 'Requested element count ' + this.local$closure$n + ' is less than zero.';
              throw IllegalArgumentException_init(message.toString());
            }

            this.local$remaining = this.local$closure$n;
            if (this.local$remaining > 0) {
              this.local$tmp$ = this.local$this$drop.iterator();
              this.state_0 = 2;
              continue;
            }
             else {
              this.state_0 = 8;
              continue;
            }

          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var e_0 = this.result_0;
            this.local$remaining = this.local$remaining - 1 | 0;
            if (this.local$remaining === 0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 6:
            this.state_0 = 2;
            continue;
          case 7:
            this.state_0 = 8;
            continue;
          case 8:
            this.local$tmp$_0 = this.local$this$drop.iterator();
            this.state_0 = 9;
            continue;
          case 9:
            this.state_0 = 10;
            this.result_0 = this.local$tmp$_0.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 10:
            if (!this.result_0) {
              this.state_0 = 14;
              continue;
            }
             else {
              this.state_0 = 11;
              continue;
            }

          case 11:
            this.state_0 = 12;
            this.result_0 = this.local$tmp$_0.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 12:
            var e_1 = this.result_0;
            this.state_0 = 13;
            this.result_0 = this.local$$receiver.send_11rb$(e_1, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 13:
            this.state_0 = 9;
            continue;
          case 14:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function drop($receiver, n, context) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), drop$lambda(n, $receiver));
  }
  function dropWhile$lambda(this$dropWhile_0, closure$predicate_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$dropWhile$lambda(this$dropWhile_0, closure$predicate_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$dropWhile$lambda(this$dropWhile_0, closure$predicate_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$dropWhile = this$dropWhile_0;
    this.local$closure$predicate = closure$predicate_0;
    this.local$tmp$ = void 0;
    this.local$tmp$_0 = void 0;
    this.local$e = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$dropWhile$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$dropWhile$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$dropWhile$lambda.prototype.constructor = Coroutine$dropWhile$lambda;
  Coroutine$dropWhile$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$dropWhile.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.local$e = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$predicate(this.local$e, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 7;
              this.result_0 = this.local$$receiver.send_11rb$(this.local$e, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 8;
              continue;
            }

          case 7:
            this.state_0 = 9;
            continue;
          case 8:
            this.state_0 = 2;
            continue;
          case 9:
            this.local$tmp$_0 = this.local$this$dropWhile.iterator();
            this.state_0 = 10;
            continue;
          case 10:
            this.state_0 = 11;
            this.result_0 = this.local$tmp$_0.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 11:
            if (!this.result_0) {
              this.state_0 = 15;
              continue;
            }
             else {
              this.state_0 = 12;
              continue;
            }

          case 12:
            this.state_0 = 13;
            this.result_0 = this.local$tmp$_0.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 13:
            var e_0 = this.result_0;
            this.state_0 = 14;
            this.result_0 = this.local$$receiver.send_11rb$(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 14:
            this.state_0 = 10;
            continue;
          case 15:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function dropWhile($receiver, context, predicate) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), dropWhile$lambda($receiver, predicate));
  }
  function filter$lambda(this$filter_0, closure$predicate_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$filter$lambda(this$filter_0, closure$predicate_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$filter$lambda(this$filter_0, closure$predicate_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$filter = this$filter_0;
    this.local$closure$predicate = closure$predicate_0;
    this.local$tmp$ = void 0;
    this.local$e = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$filter$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filter$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filter$lambda.prototype.constructor = Coroutine$filter$lambda;
  Coroutine$filter$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$filter.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.local$e = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$predicate(this.local$e, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (this.result_0) {
              this.state_0 = 7;
              this.result_0 = this.local$$receiver.send_11rb$(this.local$e, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 8;
              continue;
            }

          case 7:
            this.state_0 = 8;
            continue;
          case 8:
            this.state_0 = 2;
            continue;
          case 9:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function filter($receiver, context, predicate) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), filter$lambda($receiver, predicate));
  }
  function filterIndexed$lambda(this$filterIndexed_0, closure$predicate_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$filterIndexed$lambda(this$filterIndexed_0, closure$predicate_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$filterIndexed$lambda(this$filterIndexed_0, closure$predicate_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$filterIndexed = this$filterIndexed_0;
    this.local$closure$predicate = closure$predicate_0;
    this.local$tmp$ = void 0;
    this.local$index = void 0;
    this.local$e = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$filterIndexed$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterIndexed$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterIndexed$lambda.prototype.constructor = Coroutine$filterIndexed$lambda;
  Coroutine$filterIndexed$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            this.local$index = 0;
            this.local$tmp$ = this.local$this$filterIndexed.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.local$e = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$predicate((tmp$ = this.local$index, this.local$index = tmp$ + 1 | 0, tmp$), this.local$e, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (this.result_0) {
              this.state_0 = 7;
              this.result_0 = this.local$$receiver.send_11rb$(this.local$e, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 8;
              continue;
            }

          case 7:
            this.state_0 = 8;
            continue;
          case 8:
            this.state_0 = 2;
            continue;
          case 9:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function filterIndexed($receiver, context, predicate) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), filterIndexed$lambda($receiver, predicate));
  }
  function filterIndexedTo($receiver, destination, predicate, continuation, suspended) {
    var instance = new Coroutine$filterIndexedTo($receiver, destination, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterIndexedTo($receiver, destination, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$predicate = predicate;
  }
  Coroutine$filterIndexedTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterIndexedTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterIndexedTo.prototype.constructor = Coroutine$filterIndexedTo;
  Coroutine$filterIndexedTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            var f = new IndexedValue((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0);
            var index = f.component1()
            , element = f.component2();
            if (this.local$predicate(index, element))
              this.local$destination.add_11rb$(element);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.filterIndexedTo_svhj2$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var IndexedValue_init = Kotlin.kotlin.collections.IndexedValue;
    var Throwable = Error;
    return function ($receiver, destination, predicate, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          var f = new IndexedValue_init((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0);
          var index_0 = f.component1()
          , element = f.component2();
          if (predicate(index_0, element))
            destination.add_11rb$(element);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function filterIndexedTo_0($receiver, destination, predicate, continuation, suspended) {
    var instance = new Coroutine$filterIndexedTo_0($receiver, destination, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterIndexedTo_0($receiver, destination, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$predicate = predicate;
  }
  Coroutine$filterIndexedTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterIndexedTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterIndexedTo_0.prototype.constructor = Coroutine$filterIndexedTo_0;
  Coroutine$filterIndexedTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            var f = new IndexedValue((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0);
            var index = f.component1()
            , element = f.component2();
            if (this.local$predicate(index, element)) {
              this.state_0 = 5;
              this.result_0 = this.local$destination.send_11rb$(element, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            this.state_0 = 6;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.local$destination;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.filterIndexedTo_tky26j$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var IndexedValue_init = Kotlin.kotlin.collections.IndexedValue;
    var Throwable = Error;
    return function ($receiver, destination, predicate, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          var f = new IndexedValue_init((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0);
          var index_0 = f.component1()
          , element = f.component2();
          if (predicate(index_0, element)) {
            Kotlin.suspendCall(destination.send_11rb$(element, Kotlin.coroutineReceiver()));
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function filterNot$lambda(closure$predicate_0) {
    return function (it_0, continuation_0, suspended) {
      var instance = new Coroutine$filterNot$lambda(closure$predicate_0, it_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$filterNot$lambda(closure$predicate_0, it_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$closure$predicate = closure$predicate_0;
    this.local$it = it_0;
  }
  Coroutine$filterNot$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterNot$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterNot$lambda.prototype.constructor = Coroutine$filterNot$lambda;
  Coroutine$filterNot$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$closure$predicate(this.local$it, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return !this.result_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function filterNot($receiver, context, predicate) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return filter($receiver, context, filterNot$lambda(predicate));
  }
  function filterNot_0($receiver, predicate) {
    return filterNot($receiver, void 0, predicate);
  }
  function filterNotNull$lambda(it_0, continuation_0, suspended) {
    var instance = new Coroutine$filterNotNull$lambda(it_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterNotNull$lambda(it_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$filterNotNull$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterNotNull$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterNotNull$lambda.prototype.constructor = Coroutine$filterNotNull$lambda;
  Coroutine$filterNotNull$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$it != null;
          case 1:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function filterNotNull($receiver) {
    var tmp$;
    return Kotlin.isType(tmp$ = filter($receiver, void 0, filterNotNull$lambda), ReceiveChannel) ? tmp$ : throwCCE();
  }
  function filterNotNullTo($receiver_0, destination_0, continuation_0, suspended) {
    var instance = new Coroutine$filterNotNullTo($receiver_0, destination_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterNotNullTo($receiver_0, destination_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$destination = destination_0;
  }
  Coroutine$filterNotNullTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterNotNullTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterNotNullTo.prototype.constructor = Coroutine$filterNotNullTo;
  Coroutine$filterNotNullTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (e_0 != null)
              this.local$destination.add_11rb$(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function filterNotNullTo_0($receiver_0, destination_0, continuation_0, suspended) {
    var instance = new Coroutine$filterNotNullTo_0($receiver_0, destination_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterNotNullTo_0($receiver_0, destination_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$destination = destination_0;
  }
  Coroutine$filterNotNullTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterNotNullTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterNotNullTo_0.prototype.constructor = Coroutine$filterNotNullTo_0;
  Coroutine$filterNotNullTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (e_0 != null) {
              this.state_0 = 5;
              this.result_0 = this.local$destination.send_11rb$(e_0, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            this.state_0 = 6;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.local$destination;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function filterNotTo($receiver, destination, predicate, continuation, suspended) {
    var instance = new Coroutine$filterNotTo($receiver, destination, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterNotTo($receiver, destination, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$predicate = predicate;
  }
  Coroutine$filterNotTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterNotTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterNotTo.prototype.constructor = Coroutine$filterNotTo;
  Coroutine$filterNotTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (!this.local$predicate(e_0))
              this.local$destination.add_11rb$(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.filterNotTo_3cvoim$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (!predicate(e_0))
            destination.add_11rb$(e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function filterNotTo_0($receiver, destination, predicate, continuation, suspended) {
    var instance = new Coroutine$filterNotTo_0($receiver, destination, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterNotTo_0($receiver, destination, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$predicate = predicate;
  }
  Coroutine$filterNotTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterNotTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterNotTo_0.prototype.constructor = Coroutine$filterNotTo_0;
  Coroutine$filterNotTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (!this.local$predicate(e_0)) {
              this.state_0 = 5;
              this.result_0 = this.local$destination.send_11rb$(e_0, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            this.state_0 = 6;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.local$destination;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.filterNotTo_lwiivt$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (!predicate(e_0)) {
            Kotlin.suspendCall(destination.send_11rb$(e_0, Kotlin.coroutineReceiver()));
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function filterTo($receiver, destination, predicate, continuation, suspended) {
    var instance = new Coroutine$filterTo($receiver, destination, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterTo($receiver, destination, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$predicate = predicate;
  }
  Coroutine$filterTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterTo.prototype.constructor = Coroutine$filterTo;
  Coroutine$filterTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0))
              this.local$destination.add_11rb$(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.filterTo_3cvoim$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            destination.add_11rb$(e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function filterTo_0($receiver, destination, predicate, continuation, suspended) {
    var instance = new Coroutine$filterTo_0($receiver, destination, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$filterTo_0($receiver, destination, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$predicate = predicate;
  }
  Coroutine$filterTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$filterTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$filterTo_0.prototype.constructor = Coroutine$filterTo_0;
  Coroutine$filterTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.state_0 = 5;
              this.result_0 = this.local$destination.send_11rb$(e_0, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            this.state_0 = 6;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.local$destination;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.filterTo_lwiivt$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            Kotlin.suspendCall(destination.send_11rb$(e_0, Kotlin.coroutineReceiver()));
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function take$lambda(closure$n_0, this$take_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$take$lambda(closure$n_0, this$take_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$take$lambda(closure$n_0, this$take_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$n = closure$n_0;
    this.local$this$take = this$take_0;
    this.local$tmp$ = void 0;
    this.local$remaining = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$take$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$take$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$take$lambda.prototype.constructor = Coroutine$take$lambda;
  Coroutine$take$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$closure$n === 0) {
              return;
            }
             else {
              this.state_0 = 2;
              continue;
            }

          case 1:
            throw this.exception_0;
          case 2:
            if (!(this.local$closure$n >= 0)) {
              var message = 'Requested element count ' + this.local$closure$n + ' is less than zero.';
              throw IllegalArgumentException_init(message.toString());
            }

            this.local$remaining = this.local$closure$n;
            this.local$tmp$ = this.local$this$take.iterator();
            this.state_0 = 3;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 5;
              continue;
            }

          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            var e_0 = this.result_0;
            this.state_0 = 7;
            this.result_0 = this.local$$receiver.send_11rb$(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            this.local$remaining = this.local$remaining - 1 | 0;
            if (this.local$remaining === 0) {
              return;
            }
             else {
              this.state_0 = 8;
              continue;
            }

          case 8:
            this.state_0 = 3;
            continue;
          case 9:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function take($receiver, n, context) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), take$lambda(n, $receiver));
  }
  function takeWhile$lambda(this$takeWhile_0, closure$predicate_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$takeWhile$lambda(this$takeWhile_0, closure$predicate_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$takeWhile$lambda(this$takeWhile_0, closure$predicate_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$takeWhile = this$takeWhile_0;
    this.local$closure$predicate = closure$predicate_0;
    this.local$tmp$ = void 0;
    this.local$e = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$takeWhile$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$takeWhile$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$takeWhile$lambda.prototype.constructor = Coroutine$takeWhile$lambda;
  Coroutine$takeWhile$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$takeWhile.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.local$e = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$predicate(this.local$e, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              return;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$$receiver.send_11rb$(this.local$e, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            this.state_0 = 2;
            continue;
          case 9:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function takeWhile($receiver, context, predicate) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), takeWhile$lambda($receiver, predicate));
  }
  function associate($receiver, transform, continuation, suspended) {
    var instance = new Coroutine$associate($receiver, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$associate($receiver, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$destination = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$transform = transform;
  }
  Coroutine$associate.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$associate.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$associate.prototype.constructor = Coroutine$associate;
  Coroutine$associate.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$destination = LinkedHashMap_init();
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var pair = this.local$transform(e_0);
            this.local$destination.put_xwzc9p$(pair.first, pair.second);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            this.result_0 = this.local$destination;
            return this.result_0;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.associate_hngued$', wrapFunction(function () {
    var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, transform, continuation) {
      var destination = LinkedHashMap_init();
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var pair = transform(e_0);
          destination.put_xwzc9p$(pair.first, pair.second);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      Kotlin.setCoroutineResult(destination, Kotlin.coroutineReceiver());
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function associateBy($receiver, keySelector, continuation, suspended) {
    var instance = new Coroutine$associateBy($receiver, keySelector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$associateBy($receiver, keySelector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$destination = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$keySelector = keySelector;
  }
  Coroutine$associateBy.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$associateBy.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$associateBy.prototype.constructor = Coroutine$associateBy;
  Coroutine$associateBy.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$destination = LinkedHashMap_init();
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.put_xwzc9p$(this.local$keySelector(e_0), e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            this.result_0 = this.local$destination;
            return this.result_0;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.associateBy_9iro28$', wrapFunction(function () {
    var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, keySelector, continuation) {
      var destination = LinkedHashMap_init();
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          destination.put_xwzc9p$(keySelector(e_0), e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      Kotlin.setCoroutineResult(destination, Kotlin.coroutineReceiver());
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function associateBy_0($receiver, keySelector, valueTransform, continuation, suspended) {
    var instance = new Coroutine$associateBy_0($receiver, keySelector, valueTransform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$associateBy_0($receiver, keySelector, valueTransform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$destination = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$keySelector = keySelector;
    this.local$valueTransform = valueTransform;
  }
  Coroutine$associateBy_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$associateBy_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$associateBy_0.prototype.constructor = Coroutine$associateBy_0;
  Coroutine$associateBy_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$destination = LinkedHashMap_init();
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.put_xwzc9p$(this.local$keySelector(e_0), this.local$valueTransform(e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            this.result_0 = this.local$destination;
            return this.result_0;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.associateBy_qyj62m$', wrapFunction(function () {
    var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, keySelector, valueTransform, continuation) {
      var destination = LinkedHashMap_init();
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          destination.put_xwzc9p$(keySelector(e_0), valueTransform(e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      Kotlin.setCoroutineResult(destination, Kotlin.coroutineReceiver());
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function associateByTo($receiver, destination, keySelector, continuation, suspended) {
    var instance = new Coroutine$associateByTo($receiver, destination, keySelector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$associateByTo($receiver, destination, keySelector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$keySelector = keySelector;
  }
  Coroutine$associateByTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$associateByTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$associateByTo.prototype.constructor = Coroutine$associateByTo;
  Coroutine$associateByTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.put_xwzc9p$(this.local$keySelector(e_0), e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.associateByTo_ok3pfr$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, keySelector, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          destination.put_xwzc9p$(keySelector(e_0), e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function associateByTo_0($receiver, destination, keySelector, valueTransform, continuation, suspended) {
    var instance = new Coroutine$associateByTo_0($receiver, destination, keySelector, valueTransform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$associateByTo_0($receiver, destination, keySelector, valueTransform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$keySelector = keySelector;
    this.local$valueTransform = valueTransform;
  }
  Coroutine$associateByTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$associateByTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$associateByTo_0.prototype.constructor = Coroutine$associateByTo_0;
  Coroutine$associateByTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.put_xwzc9p$(this.local$keySelector(e_0), this.local$valueTransform(e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.associateByTo_f4zkpz$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, keySelector, valueTransform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          destination.put_xwzc9p$(keySelector(e_0), valueTransform(e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function associateTo($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$associateTo($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$associateTo($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$associateTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$associateTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$associateTo.prototype.constructor = Coroutine$associateTo;
  Coroutine$associateTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var pair = this.local$transform(e_0);
            this.local$destination.put_xwzc9p$(pair.first, pair.second);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.associateTo_qllpv8$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var pair = transform(e_0);
          destination.put_xwzc9p$(pair.first, pair.second);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function toChannel($receiver_0, destination_0, continuation_0, suspended) {
    var instance = new Coroutine$toChannel($receiver_0, destination_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$toChannel($receiver_0, destination_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 10;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$destination = destination_0;
  }
  Coroutine$toChannel.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$toChannel.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$toChannel.prototype.constructor = Coroutine$toChannel;
  Coroutine$toChannel.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 7;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 6;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.state_0 = 5;
            this.result_0 = this.local$destination.send_11rb$(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 1;
            continue;
          case 6:
            this.exceptionState_0 = 10;
            this.finallyPath_0 = [9];
            this.state_0 = 8;
            continue;
          case 7:
            this.finallyPath_0 = [10];
            this.exceptionState_0 = 8;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 8:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 9:
            this.result_0 = Unit;
            return this.local$destination;
          case 10:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 10) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function toCollection($receiver_0, destination_0, continuation_0, suspended) {
    var instance = new Coroutine$toCollection($receiver_0, destination_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$toCollection($receiver_0, destination_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$destination = destination_0;
  }
  Coroutine$toCollection.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$toCollection.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$toCollection.prototype.constructor = Coroutine$toCollection;
  Coroutine$toCollection.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.add_11rb$(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function toList($receiver, continuation) {
    return toMutableList($receiver, continuation);
  }
  function toMap($receiver, continuation) {
    return toMap_0($receiver, LinkedHashMap_init(), continuation);
  }
  function toMap_0($receiver_0, destination_0, continuation_0, suspended) {
    var instance = new Coroutine$toMap($receiver_0, destination_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$toMap($receiver_0, destination_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
    this.local$destination = destination_0;
  }
  Coroutine$toMap.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$toMap.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$toMap.prototype.constructor = Coroutine$toMap;
  Coroutine$toMap.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.put_xwzc9p$(e_0.first, e_0.second);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function toMutableList($receiver, continuation) {
    return toCollection($receiver, ArrayList_init(), continuation);
  }
  function toSet($receiver, continuation) {
    return toMutableSet($receiver, continuation);
  }
  function flatMap$lambda(this$flatMap_0, closure$transform_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$flatMap$lambda(this$flatMap_0, closure$transform_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$flatMap$lambda(this$flatMap_0, closure$transform_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$flatMap = this$flatMap_0;
    this.local$closure$transform = closure$transform_0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$flatMap$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$flatMap$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$flatMap$lambda.prototype.constructor = Coroutine$flatMap$lambda;
  Coroutine$flatMap$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$ = this.local$this$flatMap.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 8;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var e_0 = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$transform(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 7;
            this.result_0 = toChannel(this.result_0, this.local$$receiver, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            this.state_0 = 2;
            continue;
          case 8:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function flatMap($receiver, context, transform) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), flatMap$lambda($receiver, transform));
  }
  function groupBy($receiver, keySelector, continuation, suspended) {
    var instance = new Coroutine$groupBy($receiver, keySelector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$groupBy($receiver, keySelector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$destination = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$keySelector = keySelector;
  }
  Coroutine$groupBy.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$groupBy.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$groupBy.prototype.constructor = Coroutine$groupBy;
  Coroutine$groupBy.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$destination = LinkedHashMap_init();
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var key = this.local$keySelector(e_0);
            var tmp$;
            var value = this.local$destination.get_11rb$(key);
            if (value == null) {
              var answer = ArrayList_init();
              this.local$destination.put_xwzc9p$(key, answer);
              tmp$ = answer;
            }
             else {
              tmp$ = value;
            }

            var list = tmp$;
            list.add_11rb$(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            this.result_0 = this.local$destination;
            return this.result_0;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.groupBy_9iro28$', wrapFunction(function () {
    var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
    var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, keySelector, continuation) {
      var destination = LinkedHashMap_init();
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var key = keySelector(e_0);
          var tmp$_0;
          var value = destination.get_11rb$(key);
          if (value == null) {
            var answer = ArrayList_init();
            destination.put_xwzc9p$(key, answer);
            tmp$_0 = answer;
          }
           else {
            tmp$_0 = value;
          }
          var list = tmp$_0;
          list.add_11rb$(e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      Kotlin.setCoroutineResult(destination, Kotlin.coroutineReceiver());
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function groupBy_0($receiver, keySelector, valueTransform, continuation, suspended) {
    var instance = new Coroutine$groupBy_0($receiver, keySelector, valueTransform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$groupBy_0($receiver, keySelector, valueTransform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$destination = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$keySelector = keySelector;
    this.local$valueTransform = valueTransform;
  }
  Coroutine$groupBy_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$groupBy_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$groupBy_0.prototype.constructor = Coroutine$groupBy_0;
  Coroutine$groupBy_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$destination = LinkedHashMap_init();
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var key = this.local$keySelector(e_0);
            var tmp$;
            var value = this.local$destination.get_11rb$(key);
            if (value == null) {
              var answer = ArrayList_init();
              this.local$destination.put_xwzc9p$(key, answer);
              tmp$ = answer;
            }
             else {
              tmp$ = value;
            }

            var list = tmp$;
            list.add_11rb$(this.local$valueTransform(e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            this.result_0 = this.local$destination;
            return this.result_0;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.groupBy_qyj62m$', wrapFunction(function () {
    var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
    var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, keySelector, valueTransform, continuation) {
      var destination = LinkedHashMap_init();
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var key = keySelector(e_0);
          var tmp$_0;
          var value = destination.get_11rb$(key);
          if (value == null) {
            var answer = ArrayList_init();
            destination.put_xwzc9p$(key, answer);
            tmp$_0 = answer;
          }
           else {
            tmp$_0 = value;
          }
          var list = tmp$_0;
          list.add_11rb$(valueTransform(e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      Kotlin.setCoroutineResult(destination, Kotlin.coroutineReceiver());
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function groupByTo($receiver, destination, keySelector, continuation, suspended) {
    var instance = new Coroutine$groupByTo($receiver, destination, keySelector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$groupByTo($receiver, destination, keySelector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$keySelector = keySelector;
  }
  Coroutine$groupByTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$groupByTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$groupByTo.prototype.constructor = Coroutine$groupByTo;
  Coroutine$groupByTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var key = this.local$keySelector(e_0);
            var tmp$;
            var value = this.local$destination.get_11rb$(key);
            if (value == null) {
              var answer = ArrayList_init();
              this.local$destination.put_xwzc9p$(key, answer);
              tmp$ = answer;
            }
             else {
              tmp$ = value;
            }

            var list = tmp$;
            list.add_11rb$(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.groupByTo_ehvg6s$', wrapFunction(function () {
    var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, keySelector, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var key = keySelector(e_0);
          var tmp$_0;
          var value = destination.get_11rb$(key);
          if (value == null) {
            var answer = ArrayList_init();
            destination.put_xwzc9p$(key, answer);
            tmp$_0 = answer;
          }
           else {
            tmp$_0 = value;
          }
          var list = tmp$_0;
          list.add_11rb$(e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function groupByTo_0($receiver, destination, keySelector, valueTransform, continuation, suspended) {
    var instance = new Coroutine$groupByTo_0($receiver, destination, keySelector, valueTransform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$groupByTo_0($receiver, destination, keySelector, valueTransform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$keySelector = keySelector;
    this.local$valueTransform = valueTransform;
  }
  Coroutine$groupByTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$groupByTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$groupByTo_0.prototype.constructor = Coroutine$groupByTo_0;
  Coroutine$groupByTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var key = this.local$keySelector(e_0);
            var tmp$;
            var value = this.local$destination.get_11rb$(key);
            if (value == null) {
              var answer = ArrayList_init();
              this.local$destination.put_xwzc9p$(key, answer);
              tmp$ = answer;
            }
             else {
              tmp$ = value;
            }

            var list = tmp$;
            list.add_11rb$(this.local$valueTransform(e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.groupByTo_ckxsxm$', wrapFunction(function () {
    var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, keySelector, valueTransform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var key = keySelector(e_0);
          var tmp$_0;
          var value = destination.get_11rb$(key);
          if (value == null) {
            var answer = ArrayList_init();
            destination.put_xwzc9p$(key, answer);
            tmp$_0 = answer;
          }
           else {
            tmp$_0 = value;
          }
          var list = tmp$_0;
          list.add_11rb$(valueTransform(e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function map$lambda(closure$transform_0, this$map_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$map$lambda(closure$transform_0, this$map_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$map$lambda(closure$transform_0, this$map_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 11;
    this.local$closure$transform = closure$transform_0;
    this.local$this$map = this$map_0;
    this.local$$receiver = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver_0 = $receiver_0;
  }
  Coroutine$map$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$map$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$map$lambda.prototype.constructor = Coroutine$map$lambda;
  Coroutine$map$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$$receiver = this.local$this$map;
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.state_0 = 5;
            this.result_0 = this.local$closure$transform(e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$$receiver_0.send_11rb$(this.result_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.result_0;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function map($receiver, context, transform) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), map$lambda(transform, $receiver));
  }
  function mapIndexed$lambda(this$mapIndexed_0, closure$transform_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$mapIndexed$lambda(this$mapIndexed_0, closure$transform_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$mapIndexed$lambda(this$mapIndexed_0, closure$transform_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$mapIndexed = this$mapIndexed_0;
    this.local$closure$transform = closure$transform_0;
    this.local$tmp$ = void 0;
    this.local$index = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$mapIndexed$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapIndexed$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapIndexed$lambda.prototype.constructor = Coroutine$mapIndexed$lambda;
  Coroutine$mapIndexed$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            this.local$index = 0;
            this.local$tmp$ = this.local$this$mapIndexed.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 8;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var e_0 = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$transform((tmp$ = this.local$index, this.local$index = tmp$ + 1 | 0, tmp$), e_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 7;
            this.result_0 = this.local$$receiver.send_11rb$(this.result_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            this.state_0 = 2;
            continue;
          case 8:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function mapIndexed($receiver, context, transform) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), mapIndexed$lambda($receiver, transform));
  }
  function mapIndexedNotNull($receiver, context, transform) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return filterNotNull(mapIndexed($receiver, context, transform));
  }
  function mapIndexedNotNullTo($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapIndexedNotNullTo($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapIndexedNotNullTo($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapIndexedNotNullTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapIndexedNotNullTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapIndexedNotNullTo.prototype.constructor = Coroutine$mapIndexedNotNullTo;
  Coroutine$mapIndexedNotNullTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            var f = new IndexedValue((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0);
            var index = f.component1()
            , element = f.component2();
            var tmp$_0;
            if ((tmp$_0 = this.local$transform(index, element)) != null) {
              this.local$destination.add_11rb$(tmp$_0);
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapIndexedNotNullTo_l4mw9x$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var IndexedValue_init = Kotlin.kotlin.collections.IndexedValue;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          var f = new IndexedValue_init((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0);
          var index_0 = f.component1()
          , element = f.component2();
          var tmp$_1;
          if ((tmp$_1 = transform(index_0, element)) != null) {
            destination.add_11rb$(tmp$_1);
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapIndexedNotNullTo_0($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapIndexedNotNullTo_0($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapIndexedNotNullTo_0($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapIndexedNotNullTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapIndexedNotNullTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapIndexedNotNullTo_0.prototype.constructor = Coroutine$mapIndexedNotNullTo_0;
  Coroutine$mapIndexedNotNullTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            var f = new IndexedValue((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0);
            var index = f.component1()
            , element = f.component2();
            var tmp$_0;
            if ((tmp$_0 = this.local$transform(index, element)) != null) {
              this.state_0 = 5;
              this.result_0 = this.local$destination.send_11rb$(tmp$_0, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            this.state_0 = 6;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.local$destination;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapIndexedNotNullTo_t6nuoi$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var IndexedValue_init = Kotlin.kotlin.collections.IndexedValue;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          var f = new IndexedValue_init((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0);
          var index_0 = f.component1()
          , element = f.component2();
          var tmp$_1;
          if ((tmp$_1 = transform(index_0, element)) != null) {
            Kotlin.suspendCall(destination.send_11rb$(tmp$_1, Kotlin.coroutineReceiver()));
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapIndexedTo($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapIndexedTo($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapIndexedTo($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapIndexedTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapIndexedTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapIndexedTo.prototype.constructor = Coroutine$mapIndexedTo;
  Coroutine$mapIndexedTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            this.local$destination.add_11rb$(this.local$transform((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapIndexedTo_37jn20$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          destination.add_11rb$(transform((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapIndexedTo_0($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapIndexedTo_0($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapIndexedTo_0($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 10;
    this.local$index = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapIndexedTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapIndexedTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapIndexedTo_0.prototype.constructor = Coroutine$mapIndexedTo_0;
  Coroutine$mapIndexedTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 7;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 6;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            this.state_0 = 5;
            this.result_0 = this.local$destination.send_11rb$(this.local$transform((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), e_0), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 1;
            continue;
          case 6:
            this.exceptionState_0 = 10;
            this.finallyPath_0 = [9];
            this.state_0 = 8;
            continue;
          case 7:
            this.finallyPath_0 = [10];
            this.exceptionState_0 = 8;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 8:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 9:
            this.result_0 = Unit;
            return this.local$destination;
          case 10:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 10) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapIndexedTo_ku18bz$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var index = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          Kotlin.suspendCall(destination.send_11rb$(transform((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), e_0), Kotlin.coroutineReceiver()));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapNotNull($receiver, context, transform) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return filterNotNull(map($receiver, context, transform));
  }
  function mapNotNullTo($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapNotNullTo($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapNotNullTo($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapNotNullTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapNotNullTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapNotNullTo.prototype.constructor = Coroutine$mapNotNullTo;
  Coroutine$mapNotNullTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            if ((tmp$ = this.local$transform(e_0)) != null) {
              this.local$destination.add_11rb$(tmp$);
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapNotNullTo_moac21$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          if ((tmp$_0 = transform(e_0)) != null) {
            destination.add_11rb$(tmp$_0);
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapNotNullTo_0($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapNotNullTo_0($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapNotNullTo_0($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapNotNullTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapNotNullTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapNotNullTo_0.prototype.constructor = Coroutine$mapNotNullTo_0;
  Coroutine$mapNotNullTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            if ((tmp$ = this.local$transform(e_0)) != null) {
              this.state_0 = 5;
              this.result_0 = this.local$destination.send_11rb$(tmp$, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            this.state_0 = 6;
            continue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return this.local$destination;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapNotNullTo_oe46tu$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          if ((tmp$_0 = transform(e_0)) != null) {
            Kotlin.suspendCall(destination.send_11rb$(tmp$_0, Kotlin.coroutineReceiver()));
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapTo($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapTo($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapTo($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapTo.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapTo.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapTo.prototype.constructor = Coroutine$mapTo;
  Coroutine$mapTo.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$destination.add_11rb$(this.local$transform(e_0));
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$destination;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapTo_a61fbo$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          destination.add_11rb$(transform(e_0));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function mapTo_0($receiver, destination, transform, continuation, suspended) {
    var instance = new Coroutine$mapTo_0($receiver, destination, transform, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$mapTo_0($receiver, destination, transform, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 10;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$destination = destination;
    this.local$transform = transform;
  }
  Coroutine$mapTo_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$mapTo_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$mapTo_0.prototype.constructor = Coroutine$mapTo_0;
  Coroutine$mapTo_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 7;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 6;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.state_0 = 5;
            this.result_0 = this.local$destination.send_11rb$(this.local$transform(e_0), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 1;
            continue;
          case 6:
            this.exceptionState_0 = 10;
            this.finallyPath_0 = [9];
            this.state_0 = 8;
            continue;
          case 7:
            this.finallyPath_0 = [10];
            this.exceptionState_0 = 8;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 8:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 9:
            this.result_0 = Unit;
            return this.local$destination;
          case 10:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 10) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.mapTo_y58ukr$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, destination, transform, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          Kotlin.suspendCall(destination.send_11rb$(transform(e_0), Kotlin.coroutineReceiver()));
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return destination;
    };
  }));
  function withIndex$lambda(this$withIndex_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$withIndex$lambda(this$withIndex_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$withIndex$lambda(this$withIndex_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$withIndex = this$withIndex_0;
    this.local$tmp$ = void 0;
    this.local$index = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$withIndex$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withIndex$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withIndex$lambda.prototype.constructor = Coroutine$withIndex$lambda;
  Coroutine$withIndex$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            this.local$index = 0;
            this.local$tmp$ = this.local$this$withIndex.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var e_0 = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$$receiver.send_11rb$(new IndexedValue((tmp$ = this.local$index, this.local$index = tmp$ + 1 | 0, tmp$), e_0), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 2;
            continue;
          case 7:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function withIndex($receiver, context) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), withIndex$lambda($receiver));
  }
  function distinct$lambda(it_0, continuation_0, suspended) {
    var instance = new Coroutine$distinct$lambda(it_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$distinct$lambda(it_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$it = it_0;
  }
  Coroutine$distinct$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$distinct$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$distinct$lambda.prototype.constructor = Coroutine$distinct$lambda;
  Coroutine$distinct$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$it;
          case 1:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function distinct($receiver) {
    return distinctBy($receiver, void 0, distinct$lambda);
  }
  function distinctBy$lambda(this$distinctBy_0, closure$selector_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$distinctBy$lambda(this$distinctBy_0, closure$selector_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$distinctBy$lambda(this$distinctBy_0, closure$selector_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$distinctBy = this$distinctBy_0;
    this.local$closure$selector = closure$selector_0;
    this.local$tmp$ = void 0;
    this.local$keys = void 0;
    this.local$e = void 0;
    this.local$k = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$distinctBy$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$distinctBy$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$distinctBy$lambda.prototype.constructor = Coroutine$distinctBy$lambda;
  Coroutine$distinctBy$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$keys = HashSet_init();
            this.local$tmp$ = this.local$this$distinctBy.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 5;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.local$e = this.result_0;
            this.state_0 = 6;
            this.result_0 = this.local$closure$selector(this.local$e, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.local$k = this.result_0;
            if (!this.local$keys.contains_11rb$(this.local$k)) {
              this.state_0 = 7;
              this.result_0 = this.local$$receiver.send_11rb$(this.local$e, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 8;
              continue;
            }

          case 7:
            this.local$keys.add_11rb$(this.local$k);
            this.state_0 = 8;
            continue;
          case 8:
            this.state_0 = 2;
            continue;
          case 9:
            return Unit;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function distinctBy($receiver, context, selector) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumes($receiver), distinctBy$lambda($receiver, selector));
  }
  function toMutableSet($receiver, continuation) {
    return toCollection($receiver, LinkedHashSet_init(), continuation);
  }
  function all($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$all($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$all($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$all.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$all.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$all.prototype.constructor = Coroutine$all;
  Coroutine$all.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (!this.local$predicate(e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = false;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return true;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.all_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (!predicate(e_0))
            return false;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return true;
    };
  }));
  function any($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$any($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$any($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 6;
    this.local$cause = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$any.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$any.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$any.prototype.constructor = Coroutine$any;
  Coroutine$any.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 3;
            this.state_0 = 1;
            this.result_0 = this.local$$receiver.iterator().hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.exceptionState_0 = 6;
            this.finallyPath_0 = [2];
            this.state_0 = 4;
            this.$returnValue = this.result_0;
            continue;
          case 2:
            return this.$returnValue;
          case 3:
            this.finallyPath_0 = [6];
            this.exceptionState_0 = 4;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 4:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 5:
            return;
          case 6:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 6) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function any_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$any_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$any_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$any_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$any_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$any_0.prototype.constructor = Coroutine$any_0;
  Coroutine$any_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = true;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return false;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.any_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            return true;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return false;
    };
  }));
  function count($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$count($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$count($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 9;
    this.local$count = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$count.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$count.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$count.prototype.constructor = Coroutine$count;
  Coroutine$count.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$count = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$count.v = this.local$count.v + 1 | 0;
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$count.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function count_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$count_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$count_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$count = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$count_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$count_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$count_0.prototype.constructor = Coroutine$count_0;
  Coroutine$count_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$count = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.local$count.v = this.local$count.v + 1 | 0;
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$count.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.count_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var count = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            count.v = count.v + 1 | 0;
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return count.v;
    };
  }));
  function fold($receiver, initial, operation, continuation, suspended) {
    var instance = new Coroutine$fold($receiver, initial, operation, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$fold($receiver, initial, operation, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$accumulator = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$initial = initial;
    this.local$operation = operation;
  }
  Coroutine$fold.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$fold.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$fold.prototype.constructor = Coroutine$fold;
  Coroutine$fold.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$accumulator = {v: this.local$initial};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$accumulator.v = this.local$operation(this.local$accumulator.v, e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$accumulator.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.fold_map5c$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, initial, operation, continuation) {
      var accumulator = {v: initial};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          accumulator.v = operation(accumulator.v, e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return accumulator.v;
    };
  }));
  function foldIndexed($receiver, initial, operation, continuation, suspended) {
    var instance = new Coroutine$foldIndexed($receiver, initial, operation, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$foldIndexed($receiver, initial, operation, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$index = void 0;
    this.local$accumulator = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$initial = initial;
    this.local$operation = operation;
  }
  Coroutine$foldIndexed.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$foldIndexed.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$foldIndexed.prototype.constructor = Coroutine$foldIndexed;
  Coroutine$foldIndexed.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$index = {v: 0};
            this.local$accumulator = {v: this.local$initial};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            var tmp$;
            this.local$accumulator.v = this.local$operation((tmp$ = this.local$index.v, this.local$index.v = tmp$ + 1 | 0, tmp$), this.local$accumulator.v, e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$accumulator.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.foldIndexed_jdlsz8$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, initial, operation, continuation) {
      var index = {v: 0};
      var accumulator = {v: initial};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var tmp$_0;
          accumulator.v = operation((tmp$_0 = index.v, index.v = tmp$_0 + 1 | 0, tmp$_0), accumulator.v, e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return accumulator.v;
    };
  }));
  function maxBy($receiver, selector, continuation, suspended) {
    var instance = new Coroutine$maxBy($receiver, selector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$maxBy($receiver, selector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 14;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$maxElem = void 0;
    this.local$maxValue = void 0;
    this.local$$receiver = $receiver;
    this.local$selector = selector;
  }
  Coroutine$maxBy.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$maxBy.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$maxBy.prototype.constructor = Coroutine$maxBy;
  Coroutine$maxBy.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 11;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 14;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$maxElem = this.result_0;
            this.local$maxValue = this.local$selector(this.local$maxElem);
            this.state_0 = 5;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            var e_0 = this.result_0;
            var v = this.local$selector(e_0);
            if (Kotlin.compareTo(this.local$maxValue, v) < 0) {
              this.local$maxElem = e_0;
              this.local$maxValue = v;
            }

            this.state_0 = 5;
            continue;
          case 9:
            this.exceptionState_0 = 14;
            this.finallyPath_0 = [10];
            this.state_0 = 12;
            this.$returnValue = this.local$maxElem;
            continue;
          case 10:
            return this.$returnValue;
          case 11:
            this.finallyPath_0 = [14];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            return;
          case 14:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 14) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.maxBy_gmycv5$', wrapFunction(function () {
    var Throwable = Error;
    return function ($receiver, selector, continuation) {
      var cause = null;
      try {
        var iterator = $receiver.iterator();
        Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
        if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
          return null;
        Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
        var maxElem = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
        var maxValue = selector(maxElem);
        while (true) {
          Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var v = selector(e_0);
          if (Kotlin.compareTo(maxValue, v) < 0) {
            maxElem = e_0;
            maxValue = v;
          }
        }
        return maxElem;
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
    };
  }));
  function maxWith($receiver_0, comparator_0, continuation_0, suspended) {
    var instance = new Coroutine$maxWith($receiver_0, comparator_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$maxWith($receiver_0, comparator_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 14;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$max = void 0;
    this.local$$receiver = $receiver_0;
    this.local$comparator = comparator_0;
  }
  Coroutine$maxWith.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$maxWith.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$maxWith.prototype.constructor = Coroutine$maxWith;
  Coroutine$maxWith.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 11;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 14;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$max = this.result_0;
            this.state_0 = 5;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            var e_0 = this.result_0;
            if (this.local$comparator.compare(this.local$max, e_0) < 0)
              this.local$max = e_0;
            this.state_0 = 5;
            continue;
          case 9:
            this.exceptionState_0 = 14;
            this.finallyPath_0 = [10];
            this.state_0 = 12;
            this.$returnValue = this.local$max;
            continue;
          case 10:
            return this.$returnValue;
          case 11:
            this.finallyPath_0 = [14];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            return;
          case 14:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 14) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function minBy($receiver, selector, continuation, suspended) {
    var instance = new Coroutine$minBy($receiver, selector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$minBy($receiver, selector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 14;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$minElem = void 0;
    this.local$minValue = void 0;
    this.local$$receiver = $receiver;
    this.local$selector = selector;
  }
  Coroutine$minBy.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$minBy.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$minBy.prototype.constructor = Coroutine$minBy;
  Coroutine$minBy.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 11;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 14;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$minElem = this.result_0;
            this.local$minValue = this.local$selector(this.local$minElem);
            this.state_0 = 5;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            var e_0 = this.result_0;
            var v = this.local$selector(e_0);
            if (Kotlin.compareTo(this.local$minValue, v) > 0) {
              this.local$minElem = e_0;
              this.local$minValue = v;
            }

            this.state_0 = 5;
            continue;
          case 9:
            this.exceptionState_0 = 14;
            this.finallyPath_0 = [10];
            this.state_0 = 12;
            this.$returnValue = this.local$minElem;
            continue;
          case 10:
            return this.$returnValue;
          case 11:
            this.finallyPath_0 = [14];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            return;
          case 14:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 14) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.minBy_gmycv5$', wrapFunction(function () {
    var Throwable = Error;
    return function ($receiver, selector, continuation) {
      var cause = null;
      try {
        var iterator = $receiver.iterator();
        Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
        if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
          return null;
        Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
        var minElem = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
        var minValue = selector(minElem);
        while (true) {
          Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          var v = selector(e_0);
          if (Kotlin.compareTo(minValue, v) > 0) {
            minElem = e_0;
            minValue = v;
          }
        }
        return minElem;
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
    };
  }));
  function minWith($receiver_0, comparator_0, continuation_0, suspended) {
    var instance = new Coroutine$minWith($receiver_0, comparator_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$minWith($receiver_0, comparator_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 14;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$min = void 0;
    this.local$$receiver = $receiver_0;
    this.local$comparator = comparator_0;
  }
  Coroutine$minWith.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$minWith.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$minWith.prototype.constructor = Coroutine$minWith;
  Coroutine$minWith.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 11;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0) {
              this.exceptionState_0 = 14;
              this.finallyPath_0 = [2];
              this.state_0 = 12;
              this.$returnValue = null;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 2:
            return this.$returnValue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$min = this.result_0;
            this.state_0 = 5;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 9;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            var e_0 = this.result_0;
            if (this.local$comparator.compare(this.local$min, e_0) > 0)
              this.local$min = e_0;
            this.state_0 = 5;
            continue;
          case 9:
            this.exceptionState_0 = 14;
            this.finallyPath_0 = [10];
            this.state_0 = 12;
            this.$returnValue = this.local$min;
            continue;
          case 10:
            return this.$returnValue;
          case 11:
            this.finallyPath_0 = [14];
            this.exceptionState_0 = 12;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 12:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 13:
            return;
          case 14:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 14) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function none($receiver_0, continuation_0, suspended) {
    var instance = new Coroutine$none($receiver_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$none($receiver_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 6;
    this.local$cause = void 0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$none.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$none.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$none.prototype.constructor = Coroutine$none;
  Coroutine$none.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 3;
            this.state_0 = 1;
            this.result_0 = this.local$$receiver.iterator().hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.exceptionState_0 = 6;
            this.finallyPath_0 = [2];
            this.state_0 = 4;
            this.$returnValue = !this.result_0;
            continue;
          case 2:
            return this.$returnValue;
          case 3:
            this.finallyPath_0 = [6];
            this.exceptionState_0 = 4;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 4:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 5:
            return;
          case 6:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 6) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function none_0($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$none_0($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$none_0($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 11;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$none_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$none_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$none_0.prototype.constructor = Coroutine$none_0;
  Coroutine$none_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 8;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.exceptionState_0 = 11;
              this.finallyPath_0 = [5];
              this.state_0 = 9;
              this.$returnValue = false;
              continue;
            }
             else {
              this.state_0 = 6;
              continue;
            }

          case 5:
            return this.$returnValue;
          case 6:
            this.state_0 = 1;
            continue;
          case 7:
            this.exceptionState_0 = 11;
            this.finallyPath_0 = [10];
            this.state_0 = 9;
            continue;
          case 8:
            this.finallyPath_0 = [11];
            this.exceptionState_0 = 9;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 9:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 10:
            this.result_0 = Unit;
            return true;
          case 11:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 11) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.none_455pvd$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0))
            return false;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return true;
    };
  }));
  function reduce($receiver, operation, continuation, suspended) {
    var instance = new Coroutine$reduce($receiver, operation, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$reduce($receiver, operation, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 12;
    this.local$cause = void 0;
    this.local$iterator = void 0;
    this.local$accumulator = void 0;
    this.local$$receiver = $receiver;
    this.local$operation = operation;
  }
  Coroutine$reduce.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$reduce.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$reduce.prototype.constructor = Coroutine$reduce;
  Coroutine$reduce.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 9;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0)
              throw UnsupportedOperationException_init("Empty channel can't be reduced.");
            this.state_0 = 2;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.local$accumulator = this.result_0;
            this.state_0 = 3;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 5;
              continue;
            }

          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.local$accumulator = this.local$operation(this.local$accumulator, this.result_0);
            this.state_0 = 3;
            continue;
          case 7:
            this.exceptionState_0 = 12;
            this.finallyPath_0 = [8];
            this.state_0 = 10;
            this.$returnValue = this.local$accumulator;
            continue;
          case 8:
            return this.$returnValue;
          case 9:
            this.finallyPath_0 = [12];
            this.exceptionState_0 = 10;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 10:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 11:
            return;
          case 12:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 12) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.reduce_fktvs7$', wrapFunction(function () {
    var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
    var Throwable = Error;
    return function ($receiver, operation, continuation) {
      var cause = null;
      try {
        var iterator = $receiver.iterator();
        Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
        if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
          throw UnsupportedOperationException_init("Empty channel can't be reduced.");
        Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
        var accumulator = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
        while (true) {
          Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
          accumulator = operation(accumulator, Kotlin.coroutineResult(Kotlin.coroutineReceiver()));
        }
        return accumulator;
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
    };
  }));
  function reduceIndexed($receiver, operation, continuation, suspended) {
    var instance = new Coroutine$reduceIndexed($receiver, operation, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$reduceIndexed($receiver, operation, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 12;
    this.local$cause = void 0;
    this.local$tmp$_0 = void 0;
    this.local$iterator = void 0;
    this.local$index = void 0;
    this.local$accumulator = void 0;
    this.local$$receiver = $receiver;
    this.local$operation = operation;
  }
  Coroutine$reduceIndexed.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$reduceIndexed.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$reduceIndexed.prototype.constructor = Coroutine$reduceIndexed;
  Coroutine$reduceIndexed.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$cause = null;
            this.exceptionState_0 = 9;
            var tmp$;
            this.local$iterator = this.local$$receiver.iterator();
            this.state_0 = 1;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            if (!this.result_0)
              throw UnsupportedOperationException_init("Empty channel can't be reduced.");
            this.local$index = 1;
            this.state_0 = 2;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.local$accumulator = this.result_0;
            this.state_0 = 3;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$iterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            if (!this.result_0) {
              this.state_0 = 7;
              continue;
            }
             else {
              this.state_0 = 5;
              continue;
            }

          case 5:
            this.local$tmp$_0 = (tmp$ = this.local$index, this.local$index = tmp$ + 1 | 0, tmp$);
            this.state_0 = 6;
            this.result_0 = this.local$iterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.local$accumulator = this.local$operation(this.local$tmp$_0, this.local$accumulator, this.result_0);
            this.state_0 = 3;
            continue;
          case 7:
            this.exceptionState_0 = 12;
            this.finallyPath_0 = [8];
            this.state_0 = 10;
            this.$returnValue = this.local$accumulator;
            continue;
          case 8:
            return this.$returnValue;
          case 9:
            this.finallyPath_0 = [12];
            this.exceptionState_0 = 10;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 10:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 11:
            return;
          case 12:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 12) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.reduceIndexed_2fhyob$', wrapFunction(function () {
    var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
    var Throwable = Error;
    return function ($receiver, operation, continuation) {
      var cause = null;
      try {
        var tmp$, tmp$_0;
        var iterator = $receiver.iterator();
        Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
        if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
          throw UnsupportedOperationException_init("Empty channel can't be reduced.");
        var index = 1;
        Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
        var accumulator = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
        while (true) {
          Kotlin.suspendCall(iterator.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          tmp$_0 = (tmp$ = index, index = tmp$ + 1 | 0, tmp$);
          Kotlin.suspendCall(iterator.next(Kotlin.coroutineReceiver()));
          accumulator = operation(tmp$_0, accumulator, Kotlin.coroutineResult(Kotlin.coroutineReceiver()));
        }
        return accumulator;
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
    };
  }));
  function sumBy($receiver, selector, continuation, suspended) {
    var instance = new Coroutine$sumBy($receiver, selector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$sumBy($receiver, selector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$sum = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$selector = selector;
  }
  Coroutine$sumBy.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$sumBy.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$sumBy.prototype.constructor = Coroutine$sumBy;
  Coroutine$sumBy.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$sum = {v: 0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$sum.v = this.local$sum.v + this.local$selector(e_0) | 0;
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$sum.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.sumBy_12yr82$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, selector, continuation) {
      var sum = {v: 0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          sum.v = sum.v + selector(e_0) | 0;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return sum.v;
    };
  }));
  function sumByDouble($receiver, selector, continuation, suspended) {
    var instance = new Coroutine$sumByDouble($receiver, selector, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$sumByDouble($receiver, selector, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$sum = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$selector = selector;
  }
  Coroutine$sumByDouble.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$sumByDouble.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$sumByDouble.prototype.constructor = Coroutine$sumByDouble;
  Coroutine$sumByDouble.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$sum = {v: 0.0};
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            this.local$sum.v += this.local$selector(e_0);
            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return this.local$sum.v;
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.sumByDouble_gzejry$', wrapFunction(function () {
    var Unit = Kotlin.kotlin.Unit;
    var Throwable = Error;
    return function ($receiver, selector, continuation) {
      var sum = {v: 0.0};
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          sum.v += selector(e_0);
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return sum.v;
    };
  }));
  function requireNoNulls$lambda(this$requireNoNulls_0) {
    return function (it_0, continuation_0, suspended) {
      var instance = new Coroutine$requireNoNulls$lambda(this$requireNoNulls_0, it_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$requireNoNulls$lambda(this$requireNoNulls_0, it_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$requireNoNulls = this$requireNoNulls_0;
    this.local$it = it_0;
  }
  Coroutine$requireNoNulls$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$requireNoNulls$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$requireNoNulls$lambda.prototype.constructor = Coroutine$requireNoNulls$lambda;
  Coroutine$requireNoNulls$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$it == null) {
              throw IllegalArgumentException_init('null element found in ' + this.local$this$requireNoNulls + '.');
            }

            return this.local$it;
          case 1:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function requireNoNulls($receiver) {
    return map($receiver, void 0, requireNoNulls$lambda($receiver));
  }
  function partition($receiver, predicate, continuation, suspended) {
    var instance = new Coroutine$partition($receiver, predicate, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$partition($receiver, predicate, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 9;
    this.local$first = void 0;
    this.local$second = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$$receiver = $receiver;
    this.local$predicate = predicate;
  }
  Coroutine$partition.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$partition.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$partition.prototype.constructor = Coroutine$partition;
  Coroutine$partition.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$first = ArrayList_init();
            this.local$second = ArrayList_init();
            this.local$cause = null;
            this.exceptionState_0 = 6;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var e_0 = this.result_0;
            if (this.local$predicate(e_0)) {
              this.local$first.add_11rb$(e_0);
            }
             else {
              this.local$second.add_11rb$(e_0);
            }

            this.state_0 = 1;
            continue;
          case 5:
            this.exceptionState_0 = 9;
            this.finallyPath_0 = [8];
            this.state_0 = 7;
            continue;
          case 6:
            this.finallyPath_0 = [9];
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 7:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 8:
            this.result_0 = Unit;
            return new Pair_init(this.local$first, this.local$second);
          case 9:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 9) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.channels.partition_455pvd$', wrapFunction(function () {
    var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
    var Unit = Kotlin.kotlin.Unit;
    var Pair_init = Kotlin.kotlin.Pair;
    var Throwable = Error;
    return function ($receiver, predicate, continuation) {
      var first = ArrayList_init();
      var second = ArrayList_init();
      var cause = null;
      try {
        var tmp$;
        tmp$ = $receiver.iterator();
        while (true) {
          Kotlin.suspendCall(tmp$.hasNext(Kotlin.coroutineReceiver()));
          if (!Kotlin.coroutineResult(Kotlin.coroutineReceiver()))
            break;
          Kotlin.suspendCall(tmp$.next(Kotlin.coroutineReceiver()));
          var e_0 = Kotlin.coroutineResult(Kotlin.coroutineReceiver());
          if (predicate(e_0)) {
            first.add_11rb$(e_0);
          }
           else {
            second.add_11rb$(e_0);
          }
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          cause = e;
          throw e;
        }
         else
          throw e;
      }
      finally {
        $receiver.cancel_dbl4no$(cause);
      }
      Kotlin.setCoroutineResult(Unit, Kotlin.coroutineReceiver());
      return new Pair_init(first, second);
    };
  }));
  function zip$lambda(t1, t2) {
    return to(t1, t2);
  }
  function zip($receiver, other) {
    return zip_0($receiver, other, void 0, zip$lambda);
  }
  function zip$lambda_0(closure$other_0, this$zip_0, closure$transform_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$zip$lambda(closure$other_0, this$zip_0, closure$transform_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$zip$lambda(closure$other_0, this$zip_0, closure$transform_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 15;
    this.local$closure$other = closure$other_0;
    this.local$this$zip = this$zip_0;
    this.local$closure$transform = closure$transform_0;
    this.local$otherIterator = void 0;
    this.local$$receiver = void 0;
    this.local$cause = void 0;
    this.local$tmp$ = void 0;
    this.local$e = void 0;
    this.local$closure$transform_0 = void 0;
    this.local$$receiver_0 = $receiver_0;
  }
  Coroutine$zip$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$zip$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$zip$lambda.prototype.constructor = Coroutine$zip$lambda;
  Coroutine$zip$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$otherIterator = this.local$closure$other.iterator();
            this.local$$receiver = this.local$this$zip;
            this.local$cause = null;
            this.exceptionState_0 = 12;
            this.local$tmp$ = this.local$$receiver.iterator();
            this.state_0 = 1;
            continue;
          case 1:
            this.state_0 = 2;
            this.result_0 = this.local$tmp$.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            if (!this.result_0) {
              this.state_0 = 11;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$tmp$.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$e = this.result_0;
            this.local$closure$transform_0 = this.local$closure$transform;
            this.state_0 = 5;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = this.local$otherIterator.hasNext(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            if (!this.result_0) {
              this.state_0 = 10;
              continue;
            }
             else {
              this.state_0 = 7;
              continue;
            }

          case 7:
            this.state_0 = 8;
            this.result_0 = this.local$otherIterator.next(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            var element2 = this.result_0;
            this.state_0 = 9;
            this.result_0 = this.local$$receiver_0.send_11rb$(this.local$closure$transform_0(this.local$e, element2), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 9:
            if (!false) {
              this.state_0 = 10;
              continue;
            }

            this.state_0 = 5;
            continue;
          case 10:
            this.state_0 = 1;
            continue;
          case 11:
            this.exceptionState_0 = 15;
            this.finallyPath_0 = [14];
            this.state_0 = 13;
            continue;
          case 12:
            this.finallyPath_0 = [15];
            this.exceptionState_0 = 13;
            var e = this.exception_0;
            if (Kotlin.isType(e, Throwable)) {
              this.local$cause = e;
              throw e;
            }
             else
              throw e;
          case 13:
            this.local$$receiver.cancel_dbl4no$(this.local$cause);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 14:
            this.result_0 = Unit;
            return this.result_0;
          case 15:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 15) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function zip_0($receiver, other, context, transform) {
    if (context === void 0)
      context = Unconfined_getInstance();
    return produce(context, void 0, void 0, consumesAll([$receiver, other]), zip$lambda_0(other, $receiver, transform));
  }
  function ConflatedBroadcastChannel() {
    ConflatedBroadcastChannel$Companion_getInstance();
    this._state_0 = atomic_0(ConflatedBroadcastChannel$Companion_getInstance().INITIAL_STATE);
    this._updating_0 = atomic(0);
  }
  function ConflatedBroadcastChannel$Companion() {
    ConflatedBroadcastChannel$Companion_instance = this;
    this.CLOSED = new ConflatedBroadcastChannel$Closed(null);
    this.UNDEFINED = new Symbol('UNDEFINED');
    this.INITIAL_STATE = new ConflatedBroadcastChannel$State(this.UNDEFINED, null);
  }
  ConflatedBroadcastChannel$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ConflatedBroadcastChannel$Companion_instance = null;
  function ConflatedBroadcastChannel$Companion_getInstance() {
    if (ConflatedBroadcastChannel$Companion_instance === null) {
      new ConflatedBroadcastChannel$Companion();
    }
    return ConflatedBroadcastChannel$Companion_instance;
  }
  function ConflatedBroadcastChannel$State(value, subscribers) {
    this.value = value;
    this.subscribers = subscribers;
  }
  ConflatedBroadcastChannel$State.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'State',
    interfaces: []
  };
  function ConflatedBroadcastChannel$Closed(closeCause) {
    this.closeCause = closeCause;
  }
  Object.defineProperty(ConflatedBroadcastChannel$Closed.prototype, 'sendException', {
    get: function () {
      var tmp$;
      return (tmp$ = this.closeCause) != null ? tmp$ : new ClosedSendChannelException(DEFAULT_CLOSE_MESSAGE);
    }
  });
  Object.defineProperty(ConflatedBroadcastChannel$Closed.prototype, 'valueException', {
    get: function () {
      var tmp$;
      return (tmp$ = this.closeCause) != null ? tmp$ : IllegalStateException_init(DEFAULT_CLOSE_MESSAGE);
    }
  });
  ConflatedBroadcastChannel$Closed.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Closed',
    interfaces: []
  };
  Object.defineProperty(ConflatedBroadcastChannel.prototype, 'value', {
    get: function () {
      var $receiver = this._state_0;
      while (true) {
        var state = $receiver.value;
        var tmp$;
        if (Kotlin.isType(state, ConflatedBroadcastChannel$Closed))
          throw state.valueException;
        else if (Kotlin.isType(state, ConflatedBroadcastChannel$State)) {
          if (state.value === ConflatedBroadcastChannel$Companion_getInstance().UNDEFINED)
            throw IllegalStateException_init('No value');
          return (tmp$ = state.value) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
        }
         else {
          throw IllegalStateException_init(('Invalid state ' + state).toString());
        }
      }
    }
  });
  Object.defineProperty(ConflatedBroadcastChannel.prototype, 'valueOrNull', {
    get: function () {
      var tmp$;
      var state = this._state_0.value;
      if (Kotlin.isType(state, ConflatedBroadcastChannel$Closed))
        return null;
      else if (Kotlin.isType(state, ConflatedBroadcastChannel$State)) {
        if (state.value === ConflatedBroadcastChannel$Companion_getInstance().UNDEFINED)
          return null;
        return (tmp$ = state.value) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
      }
       else {
        throw IllegalStateException_init(('Invalid state ' + state).toString());
      }
    }
  });
  Object.defineProperty(ConflatedBroadcastChannel.prototype, 'isClosedForSend', {
    get: function () {
      return Kotlin.isType(this._state_0.value, ConflatedBroadcastChannel$Closed);
    }
  });
  Object.defineProperty(ConflatedBroadcastChannel.prototype, 'isFull', {
    get: function () {
      return false;
    }
  });
  ConflatedBroadcastChannel.prototype.openSubscription = function () {
    var subscriber = new ConflatedBroadcastChannel$Subscriber(this);
    var $receiver = this._state_0;
    while (true) {
      var state = $receiver.value;
      var tmp$, tmp$_0;
      if (Kotlin.isType(state, ConflatedBroadcastChannel$Closed)) {
        subscriber.close_dbl4no$(state.closeCause);
        return subscriber;
      }
       else if (Kotlin.isType(state, ConflatedBroadcastChannel$State)) {
        if (state.value !== ConflatedBroadcastChannel$Companion_getInstance().UNDEFINED) {
          subscriber.offerInternal_11rb$((tmp$ = state.value) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE());
        }
        var update = new ConflatedBroadcastChannel$State(state.value, this.addSubscriber_0((Kotlin.isType(tmp$_0 = state, ConflatedBroadcastChannel$State) ? tmp$_0 : throwCCE()).subscribers, subscriber));
        if (this._state_0.compareAndSet_xwzc9q$(state, update))
          return subscriber;
      }
       else {
        throw IllegalStateException_init(('Invalid state ' + state).toString());
      }
    }
  };
  ConflatedBroadcastChannel.prototype.closeSubscriber_0 = function (subscriber) {
    var $receiver = this._state_0;
    while (true) {
      var state = $receiver.value;
      var tmp$;
      if (Kotlin.isType(state, ConflatedBroadcastChannel$Closed))
        return;
      else if (Kotlin.isType(state, ConflatedBroadcastChannel$State)) {
        var update = new ConflatedBroadcastChannel$State(state.value, this.removeSubscriber_0(ensureNotNull((Kotlin.isType(tmp$ = state, ConflatedBroadcastChannel$State) ? tmp$ : throwCCE()).subscribers), subscriber));
        if (this._state_0.compareAndSet_xwzc9q$(state, update))
          return;
      }
       else {
        throw IllegalStateException_init(('Invalid state ' + state).toString());
      }
    }
  };
  ConflatedBroadcastChannel.prototype.addSubscriber_0 = function (list, subscriber) {
    if (list == null) {
      var array = Array_0(1);
      var tmp$;
      tmp$ = array.length - 1 | 0;
      for (var i = 0; i <= tmp$; i++) {
        array[i] = subscriber;
      }
      return array;
    }
    return list.concat([subscriber]);
  };
  ConflatedBroadcastChannel.prototype.removeSubscriber_0 = function (list, subscriber) {
    var tmp$;
    var n = list.length;
    var i = indexOf(list, subscriber);
    if (!(i >= 0)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    if (n === 1)
      return null;
    var update = Kotlin.newArray(n - 1 | 0, null);
    arraycopy(list, 0, update, 0, i);
    arraycopy(list, i + 1 | 0, update, i, n - i - 1 | 0);
    return Kotlin.isArray(tmp$ = update) ? tmp$ : throwCCE();
  };
  ConflatedBroadcastChannel.prototype.close_dbl4no$$default = function (cause) {
    var $receiver = this._state_0;
    while (true) {
      var state = $receiver.value;
      var tmp$, tmp$_0;
      if (Kotlin.isType(state, ConflatedBroadcastChannel$Closed))
        return false;
      else if (Kotlin.isType(state, ConflatedBroadcastChannel$State)) {
        var update = cause == null ? ConflatedBroadcastChannel$Companion_getInstance().CLOSED : new ConflatedBroadcastChannel$Closed(cause);
        if (this._state_0.compareAndSet_xwzc9q$(state, update)) {
          if ((tmp$_0 = (Kotlin.isType(tmp$ = state, ConflatedBroadcastChannel$State) ? tmp$ : throwCCE()).subscribers) != null) {
            var tmp$_1;
            for (tmp$_1 = 0; tmp$_1 !== tmp$_0.length; ++tmp$_1) {
              var element = tmp$_0[tmp$_1];
              element.close_dbl4no$(cause);
            }
          }
          return true;
        }
      }
       else {
        throw IllegalStateException_init(('Invalid state ' + state).toString());
      }
    }
  };
  ConflatedBroadcastChannel.prototype.cancel_dbl4no$$default = function (cause) {
    return this.close_dbl4no$(cause);
  };
  ConflatedBroadcastChannel.prototype.send_11rb$ = function (element, continuation) {
    var tmp$;
    if ((tmp$ = this.offerInternal_0(element)) != null) {
      throw tmp$.sendException;
    }
  };
  ConflatedBroadcastChannel.prototype.offer_11rb$ = function (element) {
    var tmp$;
    if ((tmp$ = this.offerInternal_0(element)) != null) {
      throw tmp$.sendException;
    }
    return true;
  };
  ConflatedBroadcastChannel.prototype.offerInternal_0 = function (element) {
    if (!this._updating_0.compareAndSet_vux9f0$(0, 1))
      return null;
    try {
      var $receiver = this._state_0;
      while (true) {
        var state = $receiver.value;
        var tmp$, tmp$_0;
        if (Kotlin.isType(state, ConflatedBroadcastChannel$Closed))
          return state;
        else if (Kotlin.isType(state, ConflatedBroadcastChannel$State)) {
          var update = new ConflatedBroadcastChannel$State(element, (Kotlin.isType(tmp$ = state, ConflatedBroadcastChannel$State) ? tmp$ : throwCCE()).subscribers);
          if (this._state_0.compareAndSet_xwzc9q$(state, update)) {
            if ((tmp$_0 = state.subscribers) != null) {
              var tmp$_1;
              for (tmp$_1 = 0; tmp$_1 !== tmp$_0.length; ++tmp$_1) {
                var element_0 = tmp$_0[tmp$_1];
                element_0.offerInternal_11rb$(element);
              }
            }
            return null;
          }
        }
         else {
          throw IllegalStateException_init(('Invalid state ' + state).toString());
        }
      }
    }
    finally {
      this._updating_0.value = 0;
    }
  };
  function ConflatedBroadcastChannel$get_ConflatedBroadcastChannel$onSend$ObjectLiteral(this$ConflatedBroadcastChannel) {
    this.this$ConflatedBroadcastChannel = this$ConflatedBroadcastChannel;
  }
  ConflatedBroadcastChannel$get_ConflatedBroadcastChannel$onSend$ObjectLiteral.prototype.registerSelectClause2_9926h0$ = function (select, param, block) {
    this.this$ConflatedBroadcastChannel.registerSelectSend_0(select, param, block);
  };
  ConflatedBroadcastChannel$get_ConflatedBroadcastChannel$onSend$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [SelectClause2]
  };
  Object.defineProperty(ConflatedBroadcastChannel.prototype, 'onSend', {
    get: function () {
      return new ConflatedBroadcastChannel$get_ConflatedBroadcastChannel$onSend$ObjectLiteral(this);
    }
  });
  ConflatedBroadcastChannel.prototype.registerSelectSend_0 = function (select, element, block) {
    var tmp$;
    if (!select.trySelect_s8jyv4$(null))
      return;
    if ((tmp$ = this.offerInternal_0(element)) != null) {
      select.resumeSelectCancellableWithException_tcv7n7$(tmp$.sendException);
      return;
    }
    startCoroutineUndispatched_0(block, this, select.completion);
  };
  function ConflatedBroadcastChannel$Subscriber(broadcastChannel) {
    ConflatedChannel.call(this);
    this.broadcastChannel_0 = broadcastChannel;
  }
  ConflatedBroadcastChannel$Subscriber.prototype.cancel_dbl4no$$default = function (cause) {
    var $receiver = this.close_dbl4no$(cause);
    if ($receiver)
      this.broadcastChannel_0.closeSubscriber_0(this);
    return $receiver;
  };
  ConflatedBroadcastChannel$Subscriber.prototype.offerInternal_11rb$ = function (element) {
    return ConflatedChannel.prototype.offerInternal_11rb$.call(this, element);
  };
  ConflatedBroadcastChannel$Subscriber.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Subscriber',
    interfaces: [SubscriptionReceiveChannel, ConflatedChannel, ReceiveChannel]
  };
  ConflatedBroadcastChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ConflatedBroadcastChannel',
    interfaces: [BroadcastChannel]
  };
  function ConflatedBroadcastChannel_init(value, $this) {
    $this = $this || Object.create(ConflatedBroadcastChannel.prototype);
    ConflatedBroadcastChannel.call($this);
    $this._state_0.value = new ConflatedBroadcastChannel$State(value, null);
    return $this;
  }
  function ConflatedChannel() {
    AbstractChannel.call(this);
  }
  Object.defineProperty(ConflatedChannel.prototype, 'isBufferAlwaysEmpty', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(ConflatedChannel.prototype, 'isBufferEmpty', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(ConflatedChannel.prototype, 'isBufferAlwaysFull', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(ConflatedChannel.prototype, 'isBufferFull', {
    get: function () {
      return false;
    }
  });
  ConflatedChannel.prototype.onClosed_f9b9m0$ = function (closed) {
    this.conflatePreviousSendBuffered_tsj8n4$(closed);
  };
  ConflatedChannel.prototype.offerInternal_11rb$ = function (element) {
    while (true) {
      var result = AbstractChannel.prototype.offerInternal_11rb$.call(this, element);
      if (result === OFFER_SUCCESS)
        return OFFER_SUCCESS;
      else if (result === OFFER_FAILED) {
        var sendResult = this.sendConflated_11rb$(element);
        if (sendResult == null)
          return OFFER_SUCCESS;
        else if (Kotlin.isType(sendResult, Closed))
          return sendResult;
      }
       else if (Kotlin.isType(result, Closed))
        return result;
      else {
        throw IllegalStateException_init(('Invalid offerInternal result ' + result).toString());
      }
    }
  };
  ConflatedChannel.prototype.offerSelectInternal_26cf95$ = function (element, select) {
    var tmp$;
    while (true) {
      var result = this.hasReceiveOrClosed ? AbstractChannel.prototype.offerSelectInternal_26cf95$.call(this, element, select) : (tmp$ = select.performAtomicTrySelect_qopb37$(this.describeSendConflated_11rb$(element))) != null ? tmp$ : OFFER_SUCCESS;
      if (result === ALREADY_SELECTED)
        return ALREADY_SELECTED;
      else if (result === OFFER_SUCCESS)
        return OFFER_SUCCESS;
      else if (result !== OFFER_FAILED)
        if (Kotlin.isType(result, Closed))
          return result;
        else {
          throw IllegalStateException_init(('Invalid result ' + result).toString());
        }
    }
  };
  ConflatedChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ConflatedChannel',
    interfaces: [AbstractChannel]
  };
  function LinkedListChannel() {
    AbstractChannel.call(this);
  }
  Object.defineProperty(LinkedListChannel.prototype, 'isBufferAlwaysEmpty', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(LinkedListChannel.prototype, 'isBufferEmpty', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(LinkedListChannel.prototype, 'isBufferAlwaysFull', {
    get: function () {
      return false;
    }
  });
  Object.defineProperty(LinkedListChannel.prototype, 'isBufferFull', {
    get: function () {
      return false;
    }
  });
  LinkedListChannel.prototype.offerInternal_11rb$ = function (element) {
    while (true) {
      var result = AbstractChannel.prototype.offerInternal_11rb$.call(this, element);
      if (result === OFFER_SUCCESS)
        return OFFER_SUCCESS;
      else if (result === OFFER_FAILED) {
        var sendResult = this.sendBuffered_11rb$(element);
        if (sendResult == null)
          return OFFER_SUCCESS;
        else if (Kotlin.isType(sendResult, Closed))
          return sendResult;
      }
       else if (Kotlin.isType(result, Closed))
        return result;
      else {
        throw IllegalStateException_init(('Invalid offerInternal result ' + result).toString());
      }
    }
  };
  LinkedListChannel.prototype.offerSelectInternal_26cf95$ = function (element, select) {
    var tmp$;
    while (true) {
      var result = this.hasReceiveOrClosed ? AbstractChannel.prototype.offerSelectInternal_26cf95$.call(this, element, select) : (tmp$ = select.performAtomicTrySelect_qopb37$(this.describeSendBuffered_11rb$(element))) != null ? tmp$ : OFFER_SUCCESS;
      if (result === ALREADY_SELECTED)
        return ALREADY_SELECTED;
      else if (result === OFFER_SUCCESS)
        return OFFER_SUCCESS;
      else if (result !== OFFER_FAILED)
        if (Kotlin.isType(result, Closed))
          return result;
        else {
          throw IllegalStateException_init(('Invalid result ' + result).toString());
        }
    }
  };
  LinkedListChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedListChannel',
    interfaces: [AbstractChannel]
  };
  function ProducerScope() {
  }
  ProducerScope.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ProducerScope',
    interfaces: [SendChannel, CoroutineScope]
  };
  function ProducerJob() {
  }
  ProducerJob.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ProducerJob',
    interfaces: [Job, ReceiveChannel]
  };
  function produce(context, capacity, parent, onCompletion, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (capacity === void 0)
      capacity = 0;
    if (parent === void 0)
      parent = null;
    if (onCompletion === void 0)
      onCompletion = null;
    var channel = Channel_1(capacity);
    var newContext = newCoroutineContext(context, parent);
    var coroutine = new ProducerCoroutine(newContext, channel);
    if (onCompletion != null)
      coroutine.invokeOnCompletion_f05bi3$(onCompletion);
    coroutine.start_1qsk3b$(CoroutineStart$DEFAULT_getInstance(), coroutine, block);
    return coroutine;
  }
  function produce_0(context, capacity, parent, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (capacity === void 0)
      capacity = 0;
    if (parent === void 0)
      parent = null;
    return produce(context, capacity, parent, void 0, block);
  }
  function produce_1(context, capacity, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (capacity === void 0)
      capacity = 0;
    var tmp$;
    return Kotlin.isType(tmp$ = produce(context, capacity, void 0, void 0, block), ProducerJob) ? tmp$ : throwCCE();
  }
  function buildChannel(context, capacity, block) {
    if (capacity === void 0)
      capacity = 0;
    var tmp$;
    return Kotlin.isType(tmp$ = produce(context, capacity, void 0, void 0, block), ProducerJob) ? tmp$ : throwCCE();
  }
  function ProducerCoroutine(parentContext, channel) {
    ChannelCoroutine.call(this, parentContext, channel, true);
  }
  ProducerCoroutine.prototype.onCancellationInternal_kybjp5$ = function (exceptionally) {
    var tmp$;
    var cause = exceptionally != null ? exceptionally.cause : null;
    if (Kotlin.isType(exceptionally, Cancelled))
      tmp$ = this._channel_0.cancel_dbl4no$(Kotlin.isType(cause, CancellationException) ? null : cause);
    else
      tmp$ = this._channel_0.close_dbl4no$(cause);
    var processed = tmp$;
    if (!processed && cause != null)
      handleCoroutineException(this.context, cause);
  };
  ProducerCoroutine.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ProducerCoroutine',
    interfaces: [ProducerJob, ProducerScope, ChannelCoroutine]
  };
  function RendezvousChannel() {
    AbstractChannel.call(this);
  }
  Object.defineProperty(RendezvousChannel.prototype, 'isBufferAlwaysEmpty', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(RendezvousChannel.prototype, 'isBufferEmpty', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(RendezvousChannel.prototype, 'isBufferAlwaysFull', {
    get: function () {
      return true;
    }
  });
  Object.defineProperty(RendezvousChannel.prototype, 'isBufferFull', {
    get: function () {
      return true;
    }
  });
  RendezvousChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RendezvousChannel',
    interfaces: [AbstractChannel]
  };
  function OpDescriptor() {
  }
  OpDescriptor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OpDescriptor',
    interfaces: []
  };
  var NO_DECISION;
  function AtomicOp() {
    OpDescriptor.call(this);
    this._consensus_8dnnqx$_0 = atomic_0(NO_DECISION);
  }
  Object.defineProperty(AtomicOp.prototype, 'isDecided', {
    get: function () {
      return this._consensus_8dnnqx$_0.value !== NO_DECISION;
    }
  });
  AtomicOp.prototype.tryDecide_s8jyv4$ = function (decision) {
    if (!(decision !== NO_DECISION)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    return this._consensus_8dnnqx$_0.compareAndSet_xwzc9q$(NO_DECISION, decision);
  };
  AtomicOp.prototype.decide_zcgz0p$_0 = function (decision) {
    return this.tryDecide_s8jyv4$(decision) ? decision : this._consensus_8dnnqx$_0.value;
  };
  AtomicOp.prototype.perform_s8jyv4$ = function (affected) {
    var tmp$, tmp$_0;
    var decision = this._consensus_8dnnqx$_0.value;
    if (decision === NO_DECISION) {
      decision = this.decide_zcgz0p$_0(this.prepare_11rb$((tmp$ = affected) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE()));
    }
    this.complete_19pj23$((tmp$_0 = affected) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE(), decision);
    return decision;
  };
  AtomicOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicOp',
    interfaces: [OpDescriptor]
  };
  function AtomicDesc() {
  }
  AtomicDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicDesc',
    interfaces: []
  };
  function Symbol(symbol) {
    this.symbol = symbol;
  }
  Symbol.prototype.toString = function () {
    return this.symbol;
  };
  Symbol.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Symbol',
    interfaces: []
  };
  function startCoroutineCancellable($receiver, completion) {
    resumeCancellable(createCoroutineUnchecked($receiver, completion), Unit);
  }
  function startCoroutineCancellable_0($receiver, receiver, completion) {
    resumeCancellable(createCoroutineUnchecked_0($receiver, receiver, completion), Unit);
  }
  function startCoroutineUndispatched($receiver, completion) {
    var tmp$, tmp$_0;
    try {
      tmp$ = $receiver(completion, false);
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        completion.resumeWithException_tcv7n7$(e);
        return;
      }
       else
        throw e;
    }
    var value = tmp$;
    if (value !== COROUTINE_SUSPENDED) {
      completion.resume_11rb$((tmp$_0 = value) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE());
    }
  }
  function startCoroutineUndispatched_0($receiver, receiver, completion) {
    var tmp$, tmp$_0;
    try {
      tmp$ = $receiver(receiver, completion, false);
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        completion.resumeWithException_tcv7n7$(e);
        return;
      }
       else
        throw e;
    }
    var value = tmp$;
    if (value !== COROUTINE_SUSPENDED) {
      completion.resume_11rb$((tmp$_0 = value) == null || Kotlin.isType(tmp$_0, Any) ? tmp$_0 : throwCCE());
    }
  }
  function startUndispatchedOrReturn($receiver, block) {
    $receiver.initParentJob_8be2vx$();
    var tmp$, tmp$_0;
    try {
      tmp$ = block($receiver, false);
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        tmp$ = new CompletedExceptionally(e);
      }
       else
        throw e;
    }
    var result = tmp$;
    if (result === COROUTINE_SUSPENDED)
      tmp$_0 = COROUTINE_SUSPENDED;
    else if ($receiver.makeCompletingOnce_42w2xh$(result, 4))
      if (Kotlin.isType(result, CompletedExceptionally))
        throw result.cause;
      else
        tmp$_0 = result;
    else
      tmp$_0 = COROUTINE_SUSPENDED;
    return tmp$_0;
  }
  function startUndispatchedOrReturn_0($receiver, receiver, block) {
    $receiver.initParentJob_8be2vx$();
    var tmp$, tmp$_0;
    try {
      tmp$ = block(receiver, $receiver, false);
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        tmp$ = new CompletedExceptionally(e);
      }
       else
        throw e;
    }
    var result = tmp$;
    if (result === COROUTINE_SUSPENDED)
      tmp$_0 = COROUTINE_SUSPENDED;
    else if ($receiver.makeCompletingOnce_42w2xh$(result, 4))
      if (Kotlin.isType(result, CompletedExceptionally))
        throw result.cause;
      else
        tmp$_0 = result;
    else
      tmp$_0 = COROUTINE_SUSPENDED;
    return tmp$_0;
  }
  function undispatchedResult($receiver, startBlock) {
    var tmp$, tmp$_0;
    try {
      tmp$ = startBlock();
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        tmp$ = new CompletedExceptionally(e);
      }
       else
        throw e;
    }
    var result = tmp$;
    if (result === COROUTINE_SUSPENDED)
      tmp$_0 = COROUTINE_SUSPENDED;
    else if ($receiver.makeCompletingOnce_42w2xh$(result, 4))
      if (Kotlin.isType(result, CompletedExceptionally))
        throw result.cause;
      else
        tmp$_0 = result;
    else
      tmp$_0 = COROUTINE_SUSPENDED;
    return tmp$_0;
  }
  function SelectBuilder() {
  }
  SelectBuilder.prototype.invoke_1c9369$ = function ($receiver, block) {
    this.invoke_n39bqh$($receiver, null, block);
  };
  SelectBuilder.prototype.onTimeout_yg8mdg$ = function (time, unit, block, callback$default) {
    if (unit === void 0)
      unit = TimeUnit$MILLISECONDS_getInstance();
    callback$default ? callback$default(time, unit, block) : this.onTimeout_yg8mdg$$default(time, unit, block);
  };
  SelectBuilder.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SelectBuilder',
    interfaces: []
  };
  function SelectClause0() {
  }
  SelectClause0.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SelectClause0',
    interfaces: []
  };
  function SelectClause1() {
  }
  SelectClause1.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SelectClause1',
    interfaces: []
  };
  function SelectClause2() {
  }
  SelectClause2.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SelectClause2',
    interfaces: []
  };
  function SelectInstance() {
  }
  SelectInstance.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'SelectInstance',
    interfaces: []
  };
  function select(builder_0, continuation) {
    return select$lambda(builder_0)(continuation.facade);
  }
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.selects.select_2ojkow$', wrapFunction(function () {
    var SelectBuilderImpl_init = _.kotlinx.coroutines.experimental.selects.SelectBuilderImpl;
    var Throwable = Error;
    function select$lambda(closure$builder) {
      return function (cont) {
        var scope = new SelectBuilderImpl_init(cont);
        try {
          closure$builder(scope);
        }
         catch (e) {
          if (Kotlin.isType(e, Throwable)) {
            scope.handleBuilderException_tcv7n7$(e);
          }
           else
            throw e;
        }
        return scope.getResult();
      };
    }
    return function (builder_0, continuation) {
      Kotlin.suspendCall(select$lambda(builder_0)(Kotlin.coroutineReceiver().facade));
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  var ALREADY_SELECTED;
  var UNDECIDED_0;
  var RESUMED_0;
  function SelectBuilderImpl(delegate) {
    LinkedListHead.call(this);
    this.delegate_0 = delegate;
    this._state_0 = atomic_0(this);
    this._result_0 = atomic_0(UNDECIDED_0);
    this.parentHandle_0 = null;
  }
  Object.defineProperty(SelectBuilderImpl.prototype, 'context', {
    get: function () {
      return this.delegate_0.context;
    }
  });
  Object.defineProperty(SelectBuilderImpl.prototype, 'completion', {
    get: function () {
      return this;
    }
  });
  SelectBuilderImpl.prototype.doResume_0 = wrapFunction(function () {
    var IllegalStateException_init_0 = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
    return function (value, block) {
      if (!this.isSelected) {
        var message = 'Must be selected first';
        throw IllegalStateException_init_0(message.toString());
      }
      var $receiver = this._result_0;
      while (true) {
        var result = $receiver.value;
        if (result === UNDECIDED_0) {
          if (this._result_0.compareAndSet_xwzc9q$(UNDECIDED_0, value()))
            return;
        }
         else if (result === COROUTINE_SUSPENDED) {
          if (this._result_0.compareAndSet_xwzc9q$(COROUTINE_SUSPENDED, RESUMED_0)) {
            block();
            return;
          }
        }
         else
          throw IllegalStateException_init('Already resumed');
      }
    };
  });
  SelectBuilderImpl.prototype.resume_11rb$ = function (value) {
    doResume_0$break: do {
      if (!this.isSelected) {
        var message = 'Must be selected first';
        throw IllegalStateException_init(message.toString());
      }
      var $receiver = this._result_0;
      while (true) {
        var result = $receiver.value;
        if (result === UNDECIDED_0) {
          if (this._result_0.compareAndSet_xwzc9q$(UNDECIDED_0, value))
            break doResume_0$break;
        }
         else if (result === COROUTINE_SUSPENDED) {
          if (this._result_0.compareAndSet_xwzc9q$(COROUTINE_SUSPENDED, RESUMED_0)) {
            resumeDirect(this.delegate_0, value);
            break doResume_0$break;
          }
        }
         else
          throw IllegalStateException_init('Already resumed');
      }
    }
     while (false);
  };
  SelectBuilderImpl.prototype.resumeWithException_tcv7n7$ = function (exception) {
    doResume_0$break: do {
      if (!this.isSelected) {
        var message = 'Must be selected first';
        throw IllegalStateException_init(message.toString());
      }
      var $receiver = this._result_0;
      while (true) {
        var result = $receiver.value;
        if (result === UNDECIDED_0) {
          if (this._result_0.compareAndSet_xwzc9q$(UNDECIDED_0, new SelectBuilderImpl$Fail(exception)))
            break doResume_0$break;
        }
         else if (result === COROUTINE_SUSPENDED) {
          if (this._result_0.compareAndSet_xwzc9q$(COROUTINE_SUSPENDED, RESUMED_0)) {
            resumeDirectWithException(this.delegate_0, exception);
            break doResume_0$break;
          }
        }
         else
          throw IllegalStateException_init('Already resumed');
      }
    }
     while (false);
  };
  SelectBuilderImpl.prototype.resumeSelectCancellableWithException_tcv7n7$ = function (exception) {
    doResume_0$break: do {
      if (!this.isSelected) {
        var message = 'Must be selected first';
        throw IllegalStateException_init(message.toString());
      }
      var $receiver = this._result_0;
      while (true) {
        var result = $receiver.value;
        if (result === UNDECIDED_0) {
          if (this._result_0.compareAndSet_xwzc9q$(UNDECIDED_0, new SelectBuilderImpl$Fail(exception)))
            break doResume_0$break;
        }
         else if (result === COROUTINE_SUSPENDED) {
          if (this._result_0.compareAndSet_xwzc9q$(COROUTINE_SUSPENDED, RESUMED_0)) {
            resumeCancellableWithException(this.delegate_0, exception);
            break doResume_0$break;
          }
        }
         else
          throw IllegalStateException_init('Already resumed');
      }
    }
     while (false);
  };
  SelectBuilderImpl.prototype.getResult = function () {
    if (!this.isSelected)
      this.initCancellability_0();
    var result = this._result_0.value;
    if (result === UNDECIDED_0) {
      if (this._result_0.compareAndSet_xwzc9q$(UNDECIDED_0, COROUTINE_SUSPENDED))
        return COROUTINE_SUSPENDED;
      result = this._result_0.value;
    }
    if (result === RESUMED_0)
      throw IllegalStateException_init('Already resumed');
    else if (Kotlin.isType(result, SelectBuilderImpl$Fail))
      throw result.exception;
    else
      return result;
  };
  SelectBuilderImpl.prototype.initCancellability_0 = function () {
    var tmp$;
    tmp$ = this.context.get_8oh8b3$(Job$Key_getInstance());
    if (tmp$ == null) {
      return;
    }
    var parent = tmp$;
    var newRegistration = parent.invokeOnCompletion_ct2b2z$(true, void 0, new SelectBuilderImpl$SelectOnCancellation(this, parent));
    this.parentHandle_0 = newRegistration;
    if (this.isSelected)
      newRegistration.dispose();
  };
  function SelectBuilderImpl$SelectOnCancellation($outer, job) {
    this.$outer = $outer;
    JobCancellationNode.call(this, job);
  }
  SelectBuilderImpl$SelectOnCancellation.prototype.invoke = function (cause) {
    if (this.$outer.trySelect_s8jyv4$(null))
      this.$outer.resumeSelectCancellableWithException_tcv7n7$(this.job.getCancellationException());
  };
  SelectBuilderImpl$SelectOnCancellation.prototype.toString = function () {
    return 'SelectOnCancellation[' + this.$outer + ']';
  };
  SelectBuilderImpl$SelectOnCancellation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SelectOnCancellation',
    interfaces: [JobCancellationNode]
  };
  Object.defineProperty(SelectBuilderImpl.prototype, 'state_0', {
    get: function () {
      var $receiver = this._state_0;
      while (true) {
        var state = $receiver.value;
        if (!Kotlin.isType(state, OpDescriptor))
          return state;
        state.perform_s8jyv4$(this);
      }
    }
  });
  SelectBuilderImpl.prototype.handleBuilderException_tcv7n7$ = function (e) {
    if (this.trySelect_s8jyv4$(null))
      this.resumeWithException_tcv7n7$(e);
    else
      handleCoroutineException(this.context, e);
  };
  Object.defineProperty(SelectBuilderImpl.prototype, 'isSelected', {
    get: function () {
      return this.state_0 !== this;
    }
  });
  function SelectBuilderImpl$disposeOnSelect$lambda(this$SelectBuilderImpl) {
    return function () {
      return this$SelectBuilderImpl.state_0 === this$SelectBuilderImpl;
    };
  }
  SelectBuilderImpl.prototype.disposeOnSelect_lo7ng2$ = function (handle) {
    var node = new SelectBuilderImpl$DisposeNode(handle);
    while (true) {
      var state = this.state_0;
      if (state === this) {
        var addLastIf_mo06xv$result;
        addLastIf_mo06xv$break: do {
          if (!SelectBuilderImpl$disposeOnSelect$lambda(this)()) {
            addLastIf_mo06xv$result = false;
            break addLastIf_mo06xv$break;
          }
          this.addLast_tsj8n4$(node);
          addLastIf_mo06xv$result = true;
        }
         while (false);
        if (addLastIf_mo06xv$result)
          return;
      }
       else {
        handle.dispose();
        return;
      }
    }
  };
  SelectBuilderImpl.prototype.doAfterSelect_0 = function () {
    var tmp$;
    (tmp$ = this.parentHandle_0) != null ? (tmp$.dispose(), Unit) : null;
    var cur = this._next;
    while (!equals(cur, this)) {
      if (Kotlin.isType(cur, SelectBuilderImpl$DisposeNode)) {
        cur.handle.dispose();
      }
      cur = cur._next;
    }
  };
  SelectBuilderImpl.prototype.trySelect_s8jyv4$ = function (idempotent) {
    if (!!Kotlin.isType(idempotent, OpDescriptor)) {
      var message = 'cannot use OpDescriptor as idempotent marker';
      throw IllegalStateException_init(message.toString());
    }
    while (true) {
      var state = this.state_0;
      if (state === this) {
        if (this._state_0.compareAndSet_xwzc9q$(this, idempotent)) {
          this.doAfterSelect_0();
          return true;
        }
      }
       else if (idempotent == null)
        return false;
      else if (state === idempotent)
        return true;
      else
        return false;
    }
  };
  SelectBuilderImpl.prototype.performAtomicTrySelect_qopb37$ = function (desc) {
    return (new SelectBuilderImpl$AtomicSelectOp(this, desc, true)).perform_s8jyv4$(null);
  };
  SelectBuilderImpl.prototype.performAtomicIfNotSelected_qopb37$ = function (desc) {
    return (new SelectBuilderImpl$AtomicSelectOp(this, desc, false)).perform_s8jyv4$(null);
  };
  function SelectBuilderImpl$AtomicSelectOp($outer, desc, select) {
    this.$outer = $outer;
    AtomicOp.call(this);
    this.desc = desc;
    this.select = select;
  }
  SelectBuilderImpl$AtomicSelectOp.prototype.prepare_11rb$ = function (affected) {
    var tmp$;
    if (affected == null) {
      if ((tmp$ = this.prepareIfNotSelected()) != null) {
        return tmp$;
      }
    }
    return this.desc.prepare_oxcio3$(this);
  };
  SelectBuilderImpl$AtomicSelectOp.prototype.complete_19pj23$ = function (affected, failure) {
    this.completeSelect_0(failure);
    this.desc.complete_xgvua9$(this, failure);
  };
  SelectBuilderImpl$AtomicSelectOp.prototype.prepareIfNotSelected = function () {
    var $receiver = this.$outer._state_0;
    this.$outer;
    while (true) {
      var this$SelectBuilderImpl = this.$outer;
      var state = $receiver.value;
      if (state === this)
        return null;
      else if (Kotlin.isType(state, OpDescriptor))
        state.perform_s8jyv4$(this$SelectBuilderImpl);
      else if (state === this$SelectBuilderImpl) {
        if (this$SelectBuilderImpl._state_0.compareAndSet_xwzc9q$(this$SelectBuilderImpl, this))
          return null;
      }
       else
        return ALREADY_SELECTED;
    }
  };
  SelectBuilderImpl$AtomicSelectOp.prototype.completeSelect_0 = function (failure) {
    var selectSuccess = this.select && failure == null;
    var update = selectSuccess ? null : this.$outer;
    if (this.$outer._state_0.compareAndSet_xwzc9q$(this, update)) {
      if (selectSuccess)
        this.$outer.doAfterSelect_0();
    }
  };
  SelectBuilderImpl$AtomicSelectOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicSelectOp',
    interfaces: [AtomicOp]
  };
  SelectBuilderImpl.prototype.invoke_pe6gvw$ = function ($receiver, block) {
    $receiver.registerSelectClause0_f8j5hl$(this, block);
  };
  SelectBuilderImpl.prototype.invoke_lv5haq$ = function ($receiver, block) {
    $receiver.registerSelectClause1_t4n5y6$(this, block);
  };
  SelectBuilderImpl.prototype.invoke_n39bqh$ = function ($receiver, param, block) {
    $receiver.registerSelectClause2_9926h0$(this, param, block);
  };
  function SelectBuilderImpl$onTimeout$lambda(this$SelectBuilderImpl, closure$block) {
    return function () {
      if (this$SelectBuilderImpl.trySelect_s8jyv4$(null))
        startCoroutineCancellable(closure$block, this$SelectBuilderImpl.completion);
      return Unit;
    };
  }
  function Runnable$ObjectLiteral(closure$block) {
    this.closure$block = closure$block;
  }
  Runnable$ObjectLiteral.prototype.run = function () {
    this.closure$block();
  };
  Runnable$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Runnable]
  };
  SelectBuilderImpl.prototype.onTimeout_yg8mdg$$default = function (time, unit, block) {
    if (time.compareTo_11rb$(L0) <= 0) {
      if (this.trySelect_s8jyv4$(null))
        startCoroutineUndispatched(block, this.completion);
      return;
    }
    var action = new Runnable$ObjectLiteral(SelectBuilderImpl$onTimeout$lambda(this, block));
    this.disposeOnSelect_lo7ng2$(get_delay(this.context).invokeOnTimeout_myg4gi$(time, unit, action));
  };
  function SelectBuilderImpl$DisposeNode(handle) {
    LinkedListNode.call(this);
    this.handle = handle;
  }
  SelectBuilderImpl$DisposeNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DisposeNode',
    interfaces: [LinkedListNode]
  };
  function SelectBuilderImpl$Fail(exception) {
    this.exception = exception;
  }
  SelectBuilderImpl$Fail.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Fail',
    interfaces: []
  };
  SelectBuilderImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SelectBuilderImpl',
    interfaces: [Continuation, SelectInstance, SelectBuilder, LinkedListHead]
  };
  function selectUnbiased(builder_0, continuation) {
    return selectUnbiased$lambda(builder_0)(continuation.facade);
  }
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.selects.selectUnbiased_2ojkow$', wrapFunction(function () {
    var UnbiasedSelectBuilderImpl_init = _.kotlinx.coroutines.experimental.selects.UnbiasedSelectBuilderImpl;
    var Throwable = Error;
    function selectUnbiased$lambda(closure$builder) {
      return function (cont) {
        var scope = new UnbiasedSelectBuilderImpl_init(cont);
        try {
          closure$builder(scope);
        }
         catch (e) {
          if (Kotlin.isType(e, Throwable)) {
            scope.handleBuilderException_tcv7n7$(e);
          }
           else
            throw e;
        }
        return scope.initSelectResult();
      };
    }
    return function (builder_0, continuation) {
      Kotlin.suspendCall(selectUnbiased$lambda(builder_0)(Kotlin.coroutineReceiver().facade));
      return Kotlin.coroutineResult(Kotlin.coroutineReceiver());
    };
  }));
  function UnbiasedSelectBuilderImpl(cont) {
    this.instance = new SelectBuilderImpl(cont);
    this.clauses = ArrayList_init();
  }
  UnbiasedSelectBuilderImpl.prototype.handleBuilderException_tcv7n7$ = function (e) {
    this.instance.handleBuilderException_tcv7n7$(e);
  };
  UnbiasedSelectBuilderImpl.prototype.initSelectResult = function () {
    if (!this.instance.isSelected) {
      try {
        shuffle(this.clauses);
        var tmp$;
        tmp$ = this.clauses.iterator();
        while (tmp$.hasNext()) {
          var element = tmp$.next();
          element();
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          this.instance.handleBuilderException_tcv7n7$(e);
        }
         else
          throw e;
      }
    }
    return this.instance.getResult();
  };
  function UnbiasedSelectBuilderImpl$invoke$lambda(this$UnbiasedSelectBuilderImpl, closure$block, this$invoke) {
    return function () {
      this$invoke.registerSelectClause0_f8j5hl$(this$UnbiasedSelectBuilderImpl.instance, closure$block);
      return Unit;
    };
  }
  UnbiasedSelectBuilderImpl.prototype.invoke_pe6gvw$ = function ($receiver, block) {
    this.clauses.add_11rb$(UnbiasedSelectBuilderImpl$invoke$lambda(this, block, $receiver));
  };
  function UnbiasedSelectBuilderImpl$invoke$lambda_0(this$UnbiasedSelectBuilderImpl, closure$block, this$invoke) {
    return function () {
      this$invoke.registerSelectClause1_t4n5y6$(this$UnbiasedSelectBuilderImpl.instance, closure$block);
      return Unit;
    };
  }
  UnbiasedSelectBuilderImpl.prototype.invoke_lv5haq$ = function ($receiver, block) {
    this.clauses.add_11rb$(UnbiasedSelectBuilderImpl$invoke$lambda_0(this, block, $receiver));
  };
  function UnbiasedSelectBuilderImpl$invoke$lambda_1(this$UnbiasedSelectBuilderImpl, closure$param, closure$block, this$invoke) {
    return function () {
      this$invoke.registerSelectClause2_9926h0$(this$UnbiasedSelectBuilderImpl.instance, closure$param, closure$block);
      return Unit;
    };
  }
  UnbiasedSelectBuilderImpl.prototype.invoke_n39bqh$ = function ($receiver, param, block) {
    this.clauses.add_11rb$(UnbiasedSelectBuilderImpl$invoke$lambda_1(this, param, block, $receiver));
  };
  function UnbiasedSelectBuilderImpl$onTimeout$lambda(this$UnbiasedSelectBuilderImpl, closure$time, closure$unit, closure$block) {
    return function () {
      this$UnbiasedSelectBuilderImpl.instance.onTimeout_yg8mdg$(closure$time, closure$unit, closure$block);
      return Unit;
    };
  }
  UnbiasedSelectBuilderImpl.prototype.onTimeout_yg8mdg$$default = function (time, unit, block) {
    this.clauses.add_11rb$(UnbiasedSelectBuilderImpl$onTimeout$lambda(this, time, unit, block));
  };
  UnbiasedSelectBuilderImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnbiasedSelectBuilderImpl',
    interfaces: [SelectBuilder]
  };
  function select$lambda(closure$builder) {
    return function (cont) {
      var scope = new SelectBuilderImpl(cont);
      try {
        closure$builder(scope);
      }
       catch (e) {
        if (Kotlin.isType(e, Throwable)) {
          scope.handleBuilderException_tcv7n7$(e);
        }
         else
          throw e;
      }
      return scope.getResult();
    };
  }
  function whileSelect(builder_0, continuation_0, suspended) {
    var instance = new Coroutine$whileSelect(builder_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$whileSelect(builder_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$builder = builder_0;
  }
  Coroutine$whileSelect.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$whileSelect.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$whileSelect.prototype.constructor = Coroutine$whileSelect;
  Coroutine$whileSelect.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = select$lambda(this.local$builder)(this.facade);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            if (!this.result_0) {
              this.state_0 = 5;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 4:
            this.state_0 = 2;
            continue;
          case 5:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mutex() {
  }
  Mutex.prototype.tryLock_s8jyv4$ = function (owner, callback$default) {
    if (owner === void 0)
      owner = null;
    return callback$default ? callback$default(owner) : this.tryLock_s8jyv4$$default(owner);
  };
  Mutex.prototype.lock_s8jyv4$ = function (owner, continuation, callback$default) {
    if (owner === void 0)
      owner = null;
    return callback$default ? callback$default(owner, continuation) : this.lock_s8jyv4$$default(owner, continuation);
  };
  Mutex.prototype.unlock_s8jyv4$ = function (owner, callback$default) {
    if (owner === void 0)
      owner = null;
    callback$default ? callback$default(owner) : this.unlock_s8jyv4$$default(owner);
  };
  Mutex.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Mutex',
    interfaces: []
  };
  function Mutex_0(locked) {
    if (locked === void 0)
      locked = false;
    return new MutexImpl(locked);
  }
  function withLock($receiver, owner, action, continuation, suspended) {
    var instance = new Coroutine$withLock($receiver, owner, action, continuation);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$withLock($receiver, owner, action, continuation) {
    CoroutineImpl.call(this, continuation);
    this.exceptionState_0 = 5;
    this.local$$receiver = $receiver;
    this.local$owner = owner;
    this.local$action = action;
  }
  Coroutine$withLock.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withLock.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withLock.prototype.constructor = Coroutine$withLock;
  Coroutine$withLock.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$owner === void 0)
              this.local$owner = null;
            this.state_0 = 1;
            this.result_0 = this.local$$receiver.lock_s8jyv4$(this.local$owner, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.exceptionState_0 = 5;
            this.finallyPath_0 = [3];
            this.state_0 = 4;
            this.$returnValue = this.local$action();
            continue;
          case 2:
            this.finallyPath_0 = [5];
            this.state_0 = 4;
            continue;
          case 3:
            return this.$returnValue;
          case 4:
            this.local$$receiver.unlock_s8jyv4$(this.local$owner);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 5:
            throw this.exception_0;
          case 6:
            return;
        }
      }
       catch (e) {
        if (this.state_0 === 5) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.sync.withLock_ze35pb$', function ($receiver, owner, action, continuation) {
    if (owner === void 0)
      owner = null;
    Kotlin.suspendCall($receiver.lock_s8jyv4$(owner, Kotlin.coroutineReceiver()));
    try {
      return action();
    }
    finally {
      $receiver.unlock_s8jyv4$(owner);
    }
  });
  function withLock_0($receiver_0, owner_0, action_0, continuation_0, suspended) {
    var instance = new Coroutine$withLock_0($receiver_0, owner_0, action_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$withLock_0($receiver_0, owner_0, action_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 6;
    this.local$$receiver = $receiver_0;
    this.local$owner = owner_0;
    this.local$action = action_0;
  }
  Coroutine$withLock_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withLock_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withLock_0.prototype.constructor = Coroutine$withLock_0;
  Coroutine$withLock_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$owner === void 0)
              this.local$owner = null;
            this.state_0 = 1;
            this.result_0 = this.local$$receiver.lock_s8jyv4$(this.local$owner, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.exceptionState_0 = 4;
            this.state_0 = 2;
            this.result_0 = this.local$action(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.result_0 = this.result_0;
            this.exceptionState_0 = 6;
            this.finallyPath_0 = [3];
            this.state_0 = 5;
            continue;
          case 3:
            return this.result_0;
          case 4:
            this.finallyPath_0 = [6];
            this.state_0 = 5;
            continue;
          case 5:
            this.local$$receiver.unlock_s8jyv4$(this.local$owner);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 6:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 6) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function withLock_1($receiver_0, action_0, continuation_0, suspended) {
    var instance = new Coroutine$withLock_1($receiver_0, action_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$withLock_1($receiver_0, action_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 6;
    this.local$$receiver = $receiver_0;
    this.local$action = action_0;
  }
  Coroutine$withLock_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withLock_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withLock_1.prototype.constructor = Coroutine$withLock_1;
  Coroutine$withLock_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 1;
            this.result_0 = this.local$$receiver.lock_s8jyv4$(null, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.exceptionState_0 = 4;
            this.state_0 = 2;
            this.result_0 = this.local$action(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.result_0 = this.result_0;
            this.exceptionState_0 = 6;
            this.finallyPath_0 = [3];
            this.state_0 = 5;
            continue;
          case 3:
            return this.result_0;
          case 4:
            this.finallyPath_0 = [6];
            this.state_0 = 5;
            continue;
          case 5:
            this.local$$receiver.unlock_s8jyv4$(null);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 6:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 6) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function withMutex($receiver_0, action_0, continuation_0, suspended) {
    var instance = new Coroutine$withMutex($receiver_0, action_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$withMutex($receiver_0, action_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 6;
    this.local$$receiver = $receiver_0;
    this.local$action = action_0;
  }
  Coroutine$withMutex.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$withMutex.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$withMutex.prototype.constructor = Coroutine$withMutex;
  Coroutine$withMutex.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 1;
            this.result_0 = this.local$$receiver.lock_s8jyv4$(null, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.exceptionState_0 = 4;
            this.state_0 = 2;
            this.result_0 = this.local$action(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.result_0 = this.result_0;
            this.exceptionState_0 = 6;
            this.finallyPath_0 = [3];
            this.state_0 = 5;
            continue;
          case 3:
            return this.result_0;
          case 4:
            this.finallyPath_0 = [6];
            this.state_0 = 5;
            continue;
          case 5:
            this.local$$receiver.unlock_s8jyv4$(null);
            this.state_0 = this.finallyPath_0.shift();
            continue;
          case 6:
            throw this.exception_0;
        }
      }
       catch (e) {
        if (this.state_0 === 6) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  var LOCK_FAIL;
  var ENQUEUE_FAIL;
  var UNLOCK_FAIL;
  var SELECT_SUCCESS;
  var LOCKED;
  var UNLOCKED;
  var RESUME_QUIESCENT;
  var RESUME_ACTIVE;
  var EmptyLocked;
  var EmptyUnlocked;
  function Empty_0(locked) {
    this.locked = locked;
  }
  Empty_0.prototype.toString = function () {
    return 'Empty[' + this.locked + ']';
  };
  Empty_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Empty',
    interfaces: []
  };
  function MutexImpl(locked) {
    this._state_0 = atomic_0(locked ? EmptyLocked : EmptyUnlocked);
    this._resumeNext_0 = atomic_0(RESUME_QUIESCENT);
  }
  Object.defineProperty(MutexImpl.prototype, 'isLocked', {
    get: function () {
      var $receiver = this._state_0;
      while (true) {
        var state = $receiver.value;
        if (Kotlin.isType(state, Empty_0))
          return state.locked !== UNLOCKED;
        else if (Kotlin.isType(state, MutexImpl$LockedQueue))
          return true;
        else if (Kotlin.isType(state, OpDescriptor))
          state.perform_s8jyv4$(this);
        else {
          throw IllegalStateException_init(('Illegal state ' + toString(state)).toString());
        }
      }
    }
  });
  Object.defineProperty(MutexImpl.prototype, 'isLockedEmptyQueueState_8be2vx$', {
    get: function () {
      var state = this._state_0.value;
      return Kotlin.isType(state, MutexImpl$LockedQueue) && state.isEmpty;
    }
  });
  MutexImpl.prototype.tryLock_s8jyv4$$default = function (owner) {
    var $receiver = this._state_0;
    while (true) {
      var state = $receiver.value;
      if (Kotlin.isType(state, Empty_0)) {
        if (state.locked !== UNLOCKED)
          return false;
        var update = owner == null ? EmptyLocked : new Empty_0(owner);
        if (this._state_0.compareAndSet_xwzc9q$(state, update))
          return true;
      }
       else if (Kotlin.isType(state, MutexImpl$LockedQueue)) {
        if (!(state.owner !== owner)) {
          var message = 'Already locked by ' + toString(owner);
          throw IllegalStateException_init(message.toString());
        }
        return false;
      }
       else if (Kotlin.isType(state, OpDescriptor))
        state.perform_s8jyv4$(this);
      else {
        throw IllegalStateException_init(('Illegal state ' + toString(state)).toString());
      }
    }
  };
  MutexImpl.prototype.lock_s8jyv4$$default = function (owner, continuation) {
    if (this.tryLock_s8jyv4$(owner))
      return;
    return this.lockSuspend_0(owner, continuation);
  };
  function MutexImpl$lockSuspend$lambda$lambda$lambda(this$MutexImpl, closure$state) {
    return function () {
      return this$MutexImpl._state_0.value === closure$state;
    };
  }
  function MutexImpl$lockSuspend$lambda(closure$owner, this$MutexImpl) {
    return function (cont) {
      var waiter = new MutexImpl$LockCont(closure$owner, cont);
      var $receiver = this$MutexImpl._state_0;
      while (true) {
        var this$MutexImpl_0 = this$MutexImpl;
        var closure$owner_0 = closure$owner;
        var state = $receiver.value;
        if (Kotlin.isType(state, Empty_0))
          if (state.locked !== UNLOCKED) {
            this$MutexImpl_0._state_0.compareAndSet_xwzc9q$(state, new MutexImpl$LockedQueue(state.locked));
          }
           else {
            var update = closure$owner_0 == null ? EmptyLocked : new Empty_0(closure$owner_0);
            if (this$MutexImpl_0._state_0.compareAndSet_xwzc9q$(state, update)) {
              cont.resume_11rb$(Unit);
              return;
            }
          }
         else if (Kotlin.isType(state, MutexImpl$LockedQueue)) {
          var curOwner = state.owner;
          if (!(curOwner !== closure$owner_0)) {
            var message = 'Already locked by ' + toString(closure$owner_0);
            throw IllegalStateException_init(message.toString());
          }
          var condition = MutexImpl$lockSuspend$lambda$lambda$lambda(this$MutexImpl_0, state);
          var addLastIf_mo06xv$result;
          addLastIf_mo06xv$break: do {
            if (!condition()) {
              addLastIf_mo06xv$result = false;
              break addLastIf_mo06xv$break;
            }
            state.addLast_tsj8n4$(waiter);
            addLastIf_mo06xv$result = true;
          }
           while (false);
          if (addLastIf_mo06xv$result) {
            cont.initCancellability();
            removeOnCancellation(cont, waiter);
            return;
          }
        }
         else if (Kotlin.isType(state, OpDescriptor))
          state.perform_s8jyv4$(this$MutexImpl_0);
        else {
          throw IllegalStateException_init(('Illegal state ' + toString(state)).toString());
        }
      }
      return Unit;
    };
  }
  function suspendAtomicCancellableCoroutine$lambda_0(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 0);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  MutexImpl.prototype.lockSuspend_0 = function (owner, continuation) {
    return suspendAtomicCancellableCoroutine$lambda_0(true, MutexImpl$lockSuspend$lambda(owner, this))(continuation.facade);
  };
  Object.defineProperty(MutexImpl.prototype, 'onLock', {
    get: function () {
      return this;
    }
  });
  MutexImpl.prototype.registerSelectClause2_9926h0$ = function (select, owner, block) {
    while (true) {
      if (select.isSelected)
        return;
      var state = this._state_0.value;
      if (Kotlin.isType(state, Empty_0))
        if (state.locked !== UNLOCKED) {
          this._state_0.compareAndSet_xwzc9q$(state, new MutexImpl$LockedQueue(state.locked));
        }
         else {
          var failure = select.performAtomicTrySelect_qopb37$(new MutexImpl$TryLockDesc(this, owner));
          if (failure == null) {
            startCoroutineUndispatched_0(block, this, select.completion);
            return;
          }
           else if (failure === ALREADY_SELECTED)
            return;
          else if (failure !== LOCK_FAIL) {
            throw IllegalStateException_init(('performAtomicTrySelect(TryLockDesc) returned ' + toString(failure)).toString());
          }
        }
       else if (Kotlin.isType(state, MutexImpl$LockedQueue)) {
        if (!(state.owner !== owner)) {
          var message = 'Already locked by ' + toString(owner);
          throw IllegalStateException_init(message.toString());
        }
        var enqueueOp = new MutexImpl$TryEnqueueLockDesc(this, owner, state, select, block);
        var failure_0 = select.performAtomicIfNotSelected_qopb37$(enqueueOp);
        if (failure_0 == null) {
          select.disposeOnSelect_lo7ng2$(enqueueOp.node);
          return;
        }
         else if (failure_0 === ALREADY_SELECTED)
          return;
        else if (failure_0 !== ENQUEUE_FAIL) {
          throw IllegalStateException_init(('performAtomicIfNotSelected(TryEnqueueLockDesc) returned ' + toString(failure_0)).toString());
        }
      }
       else if (Kotlin.isType(state, OpDescriptor))
        state.perform_s8jyv4$(this);
      else {
        throw IllegalStateException_init(('Illegal state ' + toString(state)).toString());
      }
    }
  };
  function MutexImpl$TryLockDesc(mutex, owner) {
    AtomicDesc.call(this);
    this.mutex = mutex;
    this.owner = owner;
  }
  function MutexImpl$TryLockDesc$PrepareOp($outer, op) {
    this.$outer = $outer;
    OpDescriptor.call(this);
    this.op_0 = op;
  }
  MutexImpl$TryLockDesc$PrepareOp.prototype.perform_s8jyv4$ = function (affected) {
    var tmp$;
    var update = this.op_0.isDecided ? EmptyUnlocked : this.op_0;
    (Kotlin.isType(tmp$ = affected, MutexImpl) ? tmp$ : throwCCE())._state_0.compareAndSet_xwzc9q$(this, update);
    return null;
  };
  MutexImpl$TryLockDesc$PrepareOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrepareOp',
    interfaces: [OpDescriptor]
  };
  MutexImpl$TryLockDesc.prototype.prepare_oxcio3$ = function (op) {
    var prepare = new MutexImpl$TryLockDesc$PrepareOp(this, op);
    if (!this.mutex._state_0.compareAndSet_xwzc9q$(EmptyUnlocked, prepare))
      return LOCK_FAIL;
    return prepare.perform_s8jyv4$(this.mutex);
  };
  MutexImpl$TryLockDesc.prototype.complete_xgvua9$ = function (op, failure) {
    var tmp$;
    if (failure != null)
      tmp$ = EmptyUnlocked;
    else {
      tmp$ = this.owner == null ? EmptyLocked : new Empty_0(this.owner);
    }
    var update = tmp$;
    this.mutex._state_0.compareAndSet_xwzc9q$(op, update);
  };
  MutexImpl$TryLockDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TryLockDesc',
    interfaces: [AtomicDesc]
  };
  function MutexImpl$TryEnqueueLockDesc(mutex, owner, queue, select, block) {
    AddLastDesc.call(this, queue, new MutexImpl$LockSelect(owner, mutex, select, block));
    this.mutex = mutex;
  }
  MutexImpl$TryEnqueueLockDesc.prototype.onPrepare_9p47n0$ = function (affected, next) {
    if (this.mutex._state_0.value !== this.queue)
      return ENQUEUE_FAIL;
    return AddLastDesc.prototype.onPrepare_9p47n0$.call(this, affected, next);
  };
  MutexImpl$TryEnqueueLockDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TryEnqueueLockDesc',
    interfaces: [AddLastDesc]
  };
  MutexImpl.prototype.holdsLock_za3rmp$ = function (owner) {
    var state = this._state_0.value;
    var block$result;
    if (Kotlin.isType(state, Empty_0)) {
      block$result = state.locked === owner;
    }
     else if (Kotlin.isType(state, MutexImpl$LockedQueue)) {
      block$result = state.owner === owner;
    }
     else {
      block$result = false;
    }
    return block$result;
  };
  MutexImpl.prototype.unlock_s8jyv4$$default = function (owner) {
    var $receiver = this._state_0;
    while (true) {
      var state = $receiver.value;
      var tmp$, tmp$_0;
      if (Kotlin.isType(state, Empty_0)) {
        if (owner == null) {
          if (!(state.locked !== UNLOCKED)) {
            var message = 'Mutex is not locked';
            throw IllegalStateException_init(message.toString());
          }
        }
         else {
          if (!(state.locked === owner)) {
            var message_0 = 'Mutex is locked by ' + state.locked + ' but expected ' + toString(owner);
            throw IllegalStateException_init(message_0.toString());
          }
        }
        if (this._state_0.compareAndSet_xwzc9q$(state, EmptyUnlocked))
          return;
      }
       else if (Kotlin.isType(state, OpDescriptor))
        state.perform_s8jyv4$(this);
      else if (Kotlin.isType(state, MutexImpl$LockedQueue)) {
        if (owner != null) {
          if (!(state.owner === owner)) {
            var message_1 = 'Mutex is locked by ' + state.owner + ' but expected ' + toString(owner);
            throw IllegalStateException_init(message_1.toString());
          }
        }
        var waiter = state.removeFirstOrNull();
        if (waiter == null) {
          var op = new MutexImpl$UnlockOp(state);
          if (this._state_0.compareAndSet_xwzc9q$(state, op) && op.perform_s8jyv4$(this) == null)
            return;
        }
         else {
          var token = (Kotlin.isType(tmp$ = waiter, MutexImpl$LockWaiter) ? tmp$ : throwCCE()).tryResumeLockWaiter();
          if (token != null) {
            state.owner = (tmp$_0 = waiter.owner) != null ? tmp$_0 : LOCKED;
            if (this.startResumeNext_0(waiter, token)) {
              waiter.completeResumeLockWaiter_za3rmp$(token);
              this.finishResumeNext_0();
            }
            return;
          }
        }
      }
       else {
        throw IllegalStateException_init(('Illegal state ' + toString(state)).toString());
      }
    }
  };
  function MutexImpl$ResumeReq(waiter, token) {
    this.waiter = waiter;
    this.token = token;
  }
  MutexImpl$ResumeReq.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ResumeReq',
    interfaces: []
  };
  MutexImpl.prototype.startResumeNext_0 = function (waiter, token) {
    var $receiver = this._resumeNext_0;
    while (true) {
      var resumeNext = $receiver.value;
      if (resumeNext === RESUME_QUIESCENT) {
        this._resumeNext_0.value = RESUME_ACTIVE;
        return true;
      }
       else if (resumeNext === RESUME_ACTIVE) {
        if (this._resumeNext_0.compareAndSet_xwzc9q$(resumeNext, new MutexImpl$ResumeReq(waiter, token)))
          return false;
      }
       else {
        throw IllegalStateException_init('Cannot happen'.toString());
      }
    }
  };
  MutexImpl.prototype.finishResumeNext_0 = function () {
    var $receiver = this._resumeNext_0;
    while (true) {
      var resumeNext = $receiver.value;
      if (resumeNext === RESUME_ACTIVE) {
        if (this._resumeNext_0.compareAndSet_xwzc9q$(resumeNext, RESUME_QUIESCENT))
          return;
      }
       else if (Kotlin.isType(resumeNext, MutexImpl$ResumeReq)) {
        this._resumeNext_0.value = RESUME_ACTIVE;
        resumeNext.waiter.completeResumeLockWaiter_za3rmp$(resumeNext.token);
      }
       else {
        throw IllegalStateException_init('Cannot happen'.toString());
      }
    }
  };
  MutexImpl.prototype.toString = function () {
    var $receiver = this._state_0;
    while (true) {
      var state = $receiver.value;
      if (Kotlin.isType(state, Empty_0))
        return 'Mutex[' + state.locked + ']';
      else if (Kotlin.isType(state, OpDescriptor))
        state.perform_s8jyv4$(this);
      else if (Kotlin.isType(state, MutexImpl$LockedQueue))
        return 'Mutex[' + state.owner + ']';
      else {
        throw IllegalStateException_init(('Illegal state ' + toString(state)).toString());
      }
    }
  };
  function MutexImpl$LockedQueue(owner) {
    LinkedListHead.call(this);
    this.owner = owner;
  }
  MutexImpl$LockedQueue.prototype.toString = function () {
    return 'LockedQueue[' + this.owner + ']';
  };
  MutexImpl$LockedQueue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LockedQueue',
    interfaces: [LinkedListHead]
  };
  function MutexImpl$LockWaiter(owner) {
    LinkedListNode.call(this);
    this.owner = owner;
  }
  MutexImpl$LockWaiter.prototype.dispose = function () {
    this.remove();
  };
  MutexImpl$LockWaiter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LockWaiter',
    interfaces: [DisposableHandle, LinkedListNode]
  };
  function MutexImpl$LockCont(owner, cont) {
    MutexImpl$LockWaiter.call(this, owner);
    this.cont = cont;
  }
  MutexImpl$LockCont.prototype.tryResumeLockWaiter = function () {
    return this.cont.tryResume_19pj23$(Unit);
  };
  MutexImpl$LockCont.prototype.completeResumeLockWaiter_za3rmp$ = function (token) {
    this.cont.completeResume_za3rmp$(token);
  };
  MutexImpl$LockCont.prototype.toString = function () {
    return 'LockCont[' + toString(this.owner) + ', ' + this.cont + ']';
  };
  MutexImpl$LockCont.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LockCont',
    interfaces: [MutexImpl$LockWaiter]
  };
  function MutexImpl$LockSelect(owner, mutex, select, block) {
    MutexImpl$LockWaiter.call(this, owner);
    this.mutex = mutex;
    this.select = select;
    this.block = block;
  }
  MutexImpl$LockSelect.prototype.tryResumeLockWaiter = function () {
    return this.select.trySelect_s8jyv4$(null) ? SELECT_SUCCESS : null;
  };
  MutexImpl$LockSelect.prototype.completeResumeLockWaiter_za3rmp$ = function (token) {
    if (!(token === SELECT_SUCCESS)) {
      var message = 'Check failed.';
      throw IllegalStateException_init(message.toString());
    }
    startCoroutine_0(this.block, this.mutex, this.select.completion);
  };
  MutexImpl$LockSelect.prototype.toString = function () {
    return 'LockSelect[' + toString(this.owner) + ', ' + this.mutex + ', ' + this.select + ']';
  };
  MutexImpl$LockSelect.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LockSelect',
    interfaces: [MutexImpl$LockWaiter]
  };
  function MutexImpl$UnlockOp(queue) {
    OpDescriptor.call(this);
    this.queue = queue;
  }
  MutexImpl$UnlockOp.prototype.perform_s8jyv4$ = function (affected) {
    var tmp$;
    var success = this.queue.isEmpty;
    var update = success ? EmptyUnlocked : this.queue;
    (Kotlin.isType(tmp$ = affected, MutexImpl) ? tmp$ : throwCCE())._state_0.compareAndSet_xwzc9q$(this, update);
    return affected._state_0.value === this.queue ? UNLOCK_FAIL : null;
  };
  MutexImpl$UnlockOp.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnlockOp',
    interfaces: [OpDescriptor]
  };
  MutexImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MutexImpl',
    interfaces: [SelectClause2, Mutex]
  };
  function JvmName(name) {
    this.name = name;
  }
  JvmName.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JvmName',
    interfaces: [Annotation]
  };
  function JvmMultifileClass() {
  }
  JvmMultifileClass.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JvmMultifileClass',
    interfaces: [Annotation]
  };
  function JvmField() {
  }
  JvmField.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JvmField',
    interfaces: [Annotation]
  };
  function Volatile() {
  }
  Volatile.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Volatile',
    interfaces: [Annotation]
  };
  function CompletionHandlerBase() {
    LinkedListNode.call(this);
  }
  CompletionHandlerBase.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompletionHandlerBase',
    interfaces: [LinkedListNode]
  };
  var get_asHandler = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.get_asHandler_h9unsn$', function ($receiver) {
    return $receiver;
  });
  function CancelHandlerBase() {
  }
  CancelHandlerBase.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CancelHandlerBase',
    interfaces: []
  };
  var get_asHandler_0 = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.get_asHandler_hadnfv$', function ($receiver) {
    return $receiver;
  });
  function invokeIt($receiver, cause) {
    if (equals(typeof $receiver, 'function'))
      $receiver(cause);
    else
      $receiver.invoke(cause);
  }
  var UNDEFINED_0;
  var DefaultDispatcher;
  var DefaultDelay;
  function newCoroutineContext(context, parent) {
    if (parent === void 0)
      parent = null;
    var wp = parent == null ? context : context.plus_dvqyjb$(parent);
    return context !== DefaultDispatcher && context.get_8oh8b3$(ContinuationInterceptor.Key) == null ? wp.plus_dvqyjb$(DefaultDispatcher) : wp;
  }
  var withCoroutineContext = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.withCoroutineContext_ms9kem$', function (context, block) {
    return block();
  });
  function toDebugString($receiver) {
    return $receiver.toString();
  }
  function get_coroutineName($receiver) {
    return null;
  }
  function handleCoroutineExceptionImpl(context, exception) {
    console.error(exception);
  }
  var counter;
  function get_hexAddress($receiver) {
    var tmp$;
    var result = $receiver.__debug_counter;
    if (typeof result !== 'number') {
      result = (counter = counter + 1 | 0, counter);
      $receiver.__debug_counter = result;
    }
    return (typeof (tmp$ = result) === 'number' ? tmp$ : throwCCE()).toString();
  }
  function get_classSimpleName($receiver) {
    var tmp$;
    return (tmp$ = Kotlin.getKClassFromExpression($receiver).simpleName) != null ? tmp$ : 'Unknown';
  }
  function CompletionHandlerException(message, cause) {
    RuntimeException_init(withCause(message, cause), this);
    this.cause_j1vl5g$_0 = cause;
    this.name = 'CompletionHandlerException';
  }
  Object.defineProperty(CompletionHandlerException.prototype, 'cause', {
    get: function () {
      return this.cause_j1vl5g$_0;
    }
  });
  CompletionHandlerException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompletionHandlerException',
    interfaces: [RuntimeException]
  };
  function CancellationException(message) {
    IllegalStateException_init(message, this);
    this.name = 'CancellationException';
  }
  CancellationException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CancellationException',
    interfaces: [IllegalStateException]
  };
  function JobCancellationException(message, cause, job) {
    CancellationException.call(this, withCause(message, cause));
    this.cause_v7pqee$_0 = cause;
    this.job_8be2vx$ = job;
    this.name = 'JobCancellationException';
  }
  Object.defineProperty(JobCancellationException.prototype, 'cause', {
    get: function () {
      return this.cause_v7pqee$_0;
    }
  });
  JobCancellationException.prototype.toString = function () {
    return CancellationException.prototype.toString.call(this) + '; job=' + this.job_8be2vx$;
  };
  JobCancellationException.prototype.equals = function (other) {
    return other === this || (Kotlin.isType(other, JobCancellationException) && equals(other.message, this.message) && equals(other.job_8be2vx$, this.job_8be2vx$) && equals(other.cause, this.cause));
  };
  JobCancellationException.prototype.hashCode = function () {
    var tmp$, tmp$_0;
    return (((hashCode(ensureNotNull(this.message)) * 31 | 0) + hashCode(this.job_8be2vx$) | 0) * 31 | 0) + ((tmp$_0 = (tmp$ = this.cause) != null ? hashCode(tmp$) : null) != null ? tmp$_0 : 0) | 0;
  };
  JobCancellationException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JobCancellationException',
    interfaces: [CancellationException]
  };
  function DispatchException(message, cause) {
    RuntimeException_init(withCause(message, cause), this);
    this.name = 'DispatchException';
  }
  DispatchException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DispatchException',
    interfaces: [RuntimeException]
  };
  function IllegalStateException_0(message, cause) {
    return IllegalStateException_init(withCause(message, cause));
  }
  function withCause($receiver, cause) {
    return cause == null ? $receiver : $receiver + '; caused by ' + toString(cause);
  }
  var addSuppressedThrowable = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.addSuppressedThrowable_oz8fe6$', function ($receiver, other) {
  });
  function NodeDispatcher() {
    CoroutineDispatcher.call(this);
  }
  function NodeDispatcher$dispatch$lambda(closure$block) {
    return function () {
      closure$block.run();
      return Unit;
    };
  }
  NodeDispatcher.prototype.dispatch_jts95w$ = function (context, block) {
    setTimeout(NodeDispatcher$dispatch$lambda(block), 0);
  };
  function NodeDispatcher$scheduleResumeAfterDelay$lambda(closure$continuation, this$NodeDispatcher) {
    return function () {
      var receiver = closure$continuation;
      receiver.resumeUndispatched_276mab$(this$NodeDispatcher, Unit);
      return Unit;
    };
  }
  NodeDispatcher.prototype.scheduleResumeAfterDelay_v6u85w$ = function (time, unit, continuation) {
    var handle = setTimeout(NodeDispatcher$scheduleResumeAfterDelay$lambda(continuation, this), toIntMillis(time, unit));
    continuation.invokeOnCancellation_f05bi3$(new NodeDispatcher$ClearTimeout(handle));
  };
  function NodeDispatcher$ClearTimeout(handle) {
    CancelHandler.call(this);
    this.handle_0 = handle;
  }
  NodeDispatcher$ClearTimeout.prototype.dispose = function () {
    clearTimeout(this.handle_0);
  };
  NodeDispatcher$ClearTimeout.prototype.invoke = function (cause) {
    this.dispose();
  };
  NodeDispatcher$ClearTimeout.prototype.toString = function () {
    return 'ClearTimeout[' + this.handle_0 + ']';
  };
  NodeDispatcher$ClearTimeout.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ClearTimeout',
    interfaces: [DisposableHandle, CancelHandler]
  };
  function NodeDispatcher$invokeOnTimeout$lambda(closure$block) {
    return function () {
      closure$block.run();
      return Unit;
    };
  }
  NodeDispatcher.prototype.invokeOnTimeout_myg4gi$ = function (time, unit, block) {
    var handle = setTimeout(NodeDispatcher$invokeOnTimeout$lambda(block), toIntMillis(time, unit));
    return new NodeDispatcher$ClearTimeout(handle);
  };
  NodeDispatcher.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NodeDispatcher',
    interfaces: [Delay, CoroutineDispatcher]
  };
  function WindowDispatcher(window_0) {
    CoroutineDispatcher.call(this);
    this.window_0 = window_0;
    this.messageName_0 = 'dispatchCoroutine';
    this.queue_0 = new WindowDispatcher$queue$ObjectLiteral(this);
    this.window_0.addEventListener('message', WindowDispatcher_init$lambda(this), true);
  }
  WindowDispatcher.prototype.dispatch_jts95w$ = function (context, block) {
    this.queue_0.enqueue_id2gbd$(block);
  };
  function WindowDispatcher$scheduleResumeAfterDelay$lambda(closure$continuation, this$WindowDispatcher) {
    return function () {
      var receiver = closure$continuation;
      receiver.resumeUndispatched_276mab$(this$WindowDispatcher, Unit);
      return Unit;
    };
  }
  WindowDispatcher.prototype.scheduleResumeAfterDelay_v6u85w$ = function (time, unit, continuation) {
    this.window_0.setTimeout(WindowDispatcher$scheduleResumeAfterDelay$lambda(continuation, this), toIntMillis(time, unit));
  };
  function WindowDispatcher$invokeOnTimeout$lambda(closure$block) {
    return function () {
      closure$block.run();
      return Unit;
    };
  }
  function WindowDispatcher$invokeOnTimeout$ObjectLiteral(this$WindowDispatcher, closure$handle) {
    this.this$WindowDispatcher = this$WindowDispatcher;
    this.closure$handle = closure$handle;
  }
  WindowDispatcher$invokeOnTimeout$ObjectLiteral.prototype.dispose = function () {
    this.this$WindowDispatcher.window_0.clearTimeout(this.closure$handle);
  };
  WindowDispatcher$invokeOnTimeout$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [DisposableHandle]
  };
  WindowDispatcher.prototype.invokeOnTimeout_myg4gi$ = function (time, unit, block) {
    var handle = this.window_0.setTimeout(WindowDispatcher$invokeOnTimeout$lambda(block), toIntMillis(time, unit));
    return new WindowDispatcher$invokeOnTimeout$ObjectLiteral(this, handle);
  };
  function WindowDispatcher$queue$ObjectLiteral(this$WindowDispatcher) {
    this.this$WindowDispatcher = this$WindowDispatcher;
    MessageQueue.call(this);
  }
  WindowDispatcher$queue$ObjectLiteral.prototype.schedule = function () {
    this.this$WindowDispatcher.window_0.postMessage(this.this$WindowDispatcher.messageName_0, '*');
  };
  WindowDispatcher$queue$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [MessageQueue]
  };
  function WindowDispatcher_init$lambda(this$WindowDispatcher) {
    return function (event) {
      if (event.source == this$WindowDispatcher.window_0 && event.data == this$WindowDispatcher.messageName_0) {
        event.stopPropagation();
        this$WindowDispatcher.queue_0.process();
      }
      return Unit;
    };
  }
  WindowDispatcher.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WindowDispatcher',
    interfaces: [Delay, CoroutineDispatcher]
  };
  function MessageQueue() {
    Queue.call(this);
    this.yieldEvery = 16;
    this.scheduled_0 = false;
  }
  MessageQueue.prototype.enqueue_id2gbd$ = function (element) {
    this.add_trkh7z$(element);
    if (!this.scheduled_0) {
      this.scheduled_0 = true;
      this.schedule();
    }
  };
  MessageQueue.prototype.process = function () {
    try {
      var times = this.yieldEvery;
      for (var index = 0; index < times; index++) {
        var tmp$;
        tmp$ = this.poll();
        if (tmp$ == null) {
          return;
        }
        var element = tmp$;
        element.run();
      }
    }
    finally {
      if (this.isEmpty) {
        this.scheduled_0 = false;
      }
       else {
        this.schedule();
      }
    }
  };
  MessageQueue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MessageQueue',
    interfaces: [Queue]
  };
  function toIntMillis($receiver, unit) {
    return coerceIn(unit.toMillis_s8cxhz$($receiver), L0, L2147483647).toInt();
  }
  function Queue() {
    this.queue_0 = Kotlin.newArray(8, null);
    this.head_0 = 0;
    this.tail_0 = 0;
  }
  Object.defineProperty(Queue.prototype, 'isEmpty', {
    get: function () {
      return this.head_0 === this.tail_0;
    }
  });
  Queue.prototype.poll = function () {
    var tmp$;
    if (this.isEmpty)
      return null;
    var result = ensureNotNull(this.queue_0[this.head_0]);
    this.queue_0[this.head_0] = null;
    this.head_0 = this.next_0(this.head_0);
    return Kotlin.isType(tmp$ = result, Any) ? tmp$ : throwCCE();
  };
  Queue.prototype.add_trkh7z$ = function (element) {
    var newTail = this.next_0(this.tail_0);
    if (newTail === this.head_0) {
      this.resize_0();
      this.add_trkh7z$(element);
      return;
    }
    this.queue_0[this.tail_0] = element;
    this.tail_0 = newTail;
  };
  Queue.prototype.resize_0 = function () {
    var tmp$;
    var i = this.head_0;
    var j = 0;
    var a = Kotlin.newArray(this.queue_0.length * 2 | 0, null);
    while (i !== this.tail_0) {
      a[tmp$ = j, j = tmp$ + 1 | 0, tmp$] = this.queue_0[i];
      i = this.next_0(i);
    }
    this.queue_0 = a;
    this.head_0 = 0;
    this.tail_0 = j;
  };
  Queue.prototype.next_0 = function ($receiver) {
    var j = $receiver + 1 | 0;
    return j === this.queue_0.length ? 0 : j;
  };
  Queue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Queue',
    interfaces: []
  };
  function promise(context, start, parent, onCompletion, block) {
    if (context === void 0)
      context = DefaultDispatcher;
    if (start === void 0)
      start = CoroutineStart$DEFAULT_getInstance();
    if (parent === void 0)
      parent = null;
    if (onCompletion === void 0)
      onCompletion = null;
    return asPromise(async(context, start, parent, onCompletion, block));
  }
  function asPromise$lambda$lambda(this$asPromise, closure$reject, closure$resolve) {
    return function (it) {
      var e = this$asPromise.getCompletionExceptionOrNull();
      if (e != null) {
        closure$reject(e);
      }
       else {
        closure$resolve(this$asPromise.getCompleted());
      }
      return Unit;
    };
  }
  function asPromise$lambda(this$asPromise) {
    return function (resolve, reject) {
      this$asPromise.invokeOnCompletion_f05bi3$(asPromise$lambda$lambda(this$asPromise, reject, resolve));
      return Unit;
    };
  }
  function asPromise($receiver) {
    var promise = new Promise(asPromise$lambda($receiver));
    promise.deferred = $receiver;
    return promise;
  }
  function asDeferred$lambda(this$asDeferred_0) {
    return function ($receiver, continuation_0, suspended) {
      var instance = new Coroutine$asDeferred$lambda(this$asDeferred_0, $receiver, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$asDeferred$lambda(this$asDeferred_0, $receiver, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$asDeferred = this$asDeferred_0;
  }
  Coroutine$asDeferred$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$asDeferred$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$asDeferred$lambda.prototype.constructor = Coroutine$asDeferred$lambda;
  Coroutine$asDeferred$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = await_0(this.local$this$asDeferred, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function asDeferred($receiver) {
    var deferred = $receiver.deferred;
    return deferred != null ? deferred : async(void 0, CoroutineStart$UNDISPATCHED_getInstance(), void 0, void 0, asDeferred$lambda($receiver));
  }
  function await$lambda$lambda(closure$cont) {
    return function (it) {
      closure$cont.resume_11rb$(it);
      return Unit;
    };
  }
  function await$lambda$lambda_0(closure$cont) {
    return function (it) {
      closure$cont.resumeWithException_tcv7n7$(it);
      return Unit;
    };
  }
  function await$lambda(this$await) {
    return function (cont) {
      this$await.then(await$lambda$lambda(cont), await$lambda$lambda_0(cont));
      return Unit;
    };
  }
  function suspendCancellableCoroutine$lambda_2(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 1);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  function await_0($receiver, continuation) {
    return suspendCancellableCoroutine$lambda_2(false, await$lambda($receiver))(continuation.facade);
  }
  function Runnable() {
  }
  Runnable.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Runnable',
    interfaces: []
  };
  var Runnable_0 = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.Runnable_o14v8n$', wrapFunction(function () {
    var Kind_CLASS = Kotlin.Kind.CLASS;
    var Runnable = _.kotlinx.coroutines.experimental.Runnable;
    function Runnable$ObjectLiteral(closure$block) {
      this.closure$block = closure$block;
    }
    Runnable$ObjectLiteral.prototype.run = function () {
      this.closure$block();
    };
    Runnable$ObjectLiteral.$metadata$ = {
      kind: Kind_CLASS,
      interfaces: [Runnable]
    };
    return function (block) {
      return new Runnable$ObjectLiteral(block);
    };
  }));
  function asCoroutineDispatcher($receiver) {
    var tmp$;
    var tmp$_0;
    if ((tmp$ = $receiver.coroutineDispatcher) != null)
      tmp$_0 = tmp$;
    else {
      var $receiver_0 = new WindowDispatcher($receiver);
      $receiver.coroutineDispatcher = $receiver_0;
      tmp$_0 = $receiver_0;
    }
    return tmp$_0;
  }
  function awaitAnimationFrame$lambda(this$awaitAnimationFrame) {
    return function (cont) {
      asWindowAnimationQueue(this$awaitAnimationFrame).enqueue_rv82kq$(cont);
      return Unit;
    };
  }
  function suspendCancellableCoroutine$lambda_3(closure$holdCancellability, closure$block) {
    return function (cont) {
      var cancellable = new CancellableContinuationImpl(cont, 1);
      if (!closure$holdCancellability)
        cancellable.initCancellability();
      closure$block(cancellable);
      return cancellable.getResult();
    };
  }
  function awaitAnimationFrame($receiver, continuation) {
    return suspendCancellableCoroutine$lambda_3(false, awaitAnimationFrame$lambda($receiver))(continuation.facade);
  }
  function asWindowAnimationQueue($receiver) {
    var tmp$;
    var tmp$_0;
    if ((tmp$ = $receiver.coroutineAnimationQueue) != null)
      tmp$_0 = tmp$;
    else {
      var $receiver_0 = new WindowAnimationQueue($receiver);
      $receiver.coroutineAnimationQueue = $receiver_0;
      tmp$_0 = $receiver_0;
    }
    return tmp$_0;
  }
  function WindowAnimationQueue(window_0) {
    this.window_0 = window_0;
    this.dispatcher_0 = asCoroutineDispatcher(this.window_0);
    this.scheduled_0 = false;
    this.current_0 = new Queue();
    this.next_0 = new Queue();
    this.timestamp_0 = 0.0;
  }
  function WindowAnimationQueue$enqueue$lambda(this$WindowAnimationQueue) {
    return function (ts) {
      this$WindowAnimationQueue.timestamp_0 = ts;
      var prev = this$WindowAnimationQueue.current_0;
      this$WindowAnimationQueue.current_0 = this$WindowAnimationQueue.next_0;
      this$WindowAnimationQueue.next_0 = prev;
      this$WindowAnimationQueue.scheduled_0 = false;
      this$WindowAnimationQueue.process();
      return Unit;
    };
  }
  WindowAnimationQueue.prototype.enqueue_rv82kq$ = function (cont) {
    this.next_0.add_trkh7z$(cont);
    if (!this.scheduled_0) {
      this.scheduled_0 = true;
      this.window_0.requestAnimationFrame(WindowAnimationQueue$enqueue$lambda(this));
    }
  };
  WindowAnimationQueue.prototype.process = function () {
    var tmp$;
    while (true) {
      tmp$ = this.current_0.poll();
      if (tmp$ == null) {
        return;
      }
      var element = tmp$;
      element.resumeUndispatched_276mab$(this.dispatcher_0, this.timestamp_0);
    }
  };
  WindowAnimationQueue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WindowAnimationQueue',
    interfaces: []
  };
  function arraycopy(source, srcPos, destination, destinationStart, length) {
    var tmp$, tmp$_0;
    var destinationIndex = destinationStart;
    tmp$ = srcPos + length | 0;
    for (var sourceIndex = srcPos; sourceIndex < tmp$; sourceIndex++) {
      destination[tmp$_0 = destinationIndex, destinationIndex = tmp$_0 + 1 | 0, tmp$_0] = source[sourceIndex];
    }
  }
  function Closeable() {
  }
  Closeable.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Closeable',
    interfaces: []
  };
  var withLock_2 = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.withLock_ful2h8$', function ($receiver, action) {
    return action();
  });
  function NoOpLock() {
  }
  NoOpLock.prototype.tryLock = function () {
    return true;
  };
  NoOpLock.prototype.unlock = function () {
  };
  NoOpLock.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NoOpLock',
    interfaces: []
  };
  function subscriberList() {
    return ArrayList_init();
  }
  function LinkedListNode() {
    this._next = this;
    this._prev = this;
    this._removed = false;
  }
  Object.defineProperty(LinkedListNode.prototype, 'nextNode', {
    get: defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.get_nextNode', function () {
      return this._next;
    })
  });
  Object.defineProperty(LinkedListNode.prototype, 'prevNode', {
    get: defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.get_prevNode', function () {
      return this._prev;
    })
  });
  Object.defineProperty(LinkedListNode.prototype, 'isRemoved', {
    get: defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.get_isRemoved', function () {
      return this._removed;
    })
  });
  LinkedListNode.prototype.addLast_tsj8n4$ = function (node) {
    var prev = this._prev;
    node._next = this;
    node._prev = prev;
    prev._next = node;
    this._prev = node;
  };
  LinkedListNode.prototype.remove = function () {
    if (this._removed)
      return false;
    var prev = this._prev;
    var next = this._next;
    prev._next = next;
    next._prev = prev;
    this._removed = true;
    return true;
  };
  LinkedListNode.prototype.addOneIfEmpty_tsj8n4$ = function (node) {
    if (this._next !== this)
      return false;
    this.addLast_tsj8n4$(node);
    return true;
  };
  LinkedListNode.prototype.addLastIf_mo06xv$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.addLastIf_mo06xv$', function (node, condition) {
    if (!condition())
      return false;
    this.addLast_tsj8n4$(node);
    return true;
  });
  LinkedListNode.prototype.addLastIfPrev_ajzm8d$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.addLastIfPrev_ajzm8d$', function (node, predicate) {
    if (!predicate(this._prev))
      return false;
    this.addLast_tsj8n4$(node);
    return true;
  });
  LinkedListNode.prototype.addLastIfPrevAndIf_hs5ca2$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.addLastIfPrevAndIf_hs5ca2$', function (node, predicate, condition) {
    if (!predicate(this._prev))
      return false;
    if (!condition())
      return false;
    this.addLast_tsj8n4$(node);
    return true;
  });
  LinkedListNode.prototype.removeFirstOrNull = function () {
    var next = this._next;
    if (next === this)
      return null;
    if (!next.remove()) {
      var message = 'Should remove';
      throw IllegalStateException_init(message.toString());
    }
    return next;
  };
  LinkedListNode.prototype.removeFirstIfIsInstanceOfOrPeekIf_14urrv$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListNode.removeFirstIfIsInstanceOfOrPeekIf_14urrv$', wrapFunction(function () {
    var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
    return function (T_0, isT, predicate) {
      var next = this._next;
      if (next === this)
        return null;
      if (!isT(next))
        return null;
      if (predicate(next))
        return next;
      if (!next.remove()) {
        var message = 'Should remove';
        throw IllegalStateException_init(message.toString());
      }
      return next;
    };
  }));
  LinkedListNode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedListNode',
    interfaces: []
  };
  function AddLastDesc(queue, node) {
    AbstractAtomicDesc.call(this);
    this.queue = queue;
    this.node = node;
  }
  Object.defineProperty(AddLastDesc.prototype, 'affectedNode', {
    get: function () {
      return this.queue._prev;
    }
  });
  AddLastDesc.prototype.onPrepare_9p47n0$ = function (affected, next) {
    return null;
  };
  AddLastDesc.prototype.onComplete = function () {
    this.queue.addLast_tsj8n4$(this.node);
  };
  AddLastDesc.prototype.finishOnSuccess_9p47n0$ = function (affected, next) {
  };
  AddLastDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AddLastDesc',
    interfaces: [AbstractAtomicDesc]
  };
  function RemoveFirstDesc(queue) {
    AbstractAtomicDesc.call(this);
    this.queue = queue;
    this.affectedNode_fhgfec$_0 = this.queue._next;
  }
  Object.defineProperty(RemoveFirstDesc.prototype, 'result', {
    get: function () {
      var tmp$;
      return (tmp$ = this.affectedNode) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
    }
  });
  Object.defineProperty(RemoveFirstDesc.prototype, 'affectedNode', {
    get: function () {
      return this.affectedNode_fhgfec$_0;
    }
  });
  RemoveFirstDesc.prototype.validatePrepared_11rb$ = function (node) {
    return true;
  };
  RemoveFirstDesc.prototype.onPrepare_9p47n0$ = function (affected, next) {
    var tmp$;
    this.validatePrepared_11rb$((tmp$ = this.affectedNode) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE());
    return null;
  };
  RemoveFirstDesc.prototype.onComplete = function () {
    this.queue.removeFirstOrNull();
  };
  RemoveFirstDesc.prototype.finishOnSuccess_9p47n0$ = function (affected, next) {
  };
  RemoveFirstDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemoveFirstDesc',
    interfaces: [AbstractAtomicDesc]
  };
  function AbstractAtomicDesc() {
    AtomicDesc.call(this);
  }
  AbstractAtomicDesc.prototype.prepare_oxcio3$ = function (op) {
    var affected = this.affectedNode;
    var next = affected._next;
    var failure = this.failure_b1buut$(affected, next);
    if (failure != null)
      return failure;
    return this.onPrepare_9p47n0$(affected, next);
  };
  AbstractAtomicDesc.prototype.complete_xgvua9$ = function (op, failure) {
    this.onComplete();
  };
  AbstractAtomicDesc.prototype.failure_b1buut$ = function (affected, next) {
    return null;
  };
  AbstractAtomicDesc.prototype.retry_b1buut$ = function (affected, next) {
    return false;
  };
  AbstractAtomicDesc.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AbstractAtomicDesc',
    interfaces: [AtomicDesc]
  };
  function LinkedListHead() {
    LinkedListNode.call(this);
  }
  Object.defineProperty(LinkedListHead.prototype, 'isEmpty', {
    get: function () {
      return this._next === this;
    }
  });
  LinkedListHead.prototype.forEach_8jvfi5$ = defineInlineFunction('kotlinx-coroutines-core.kotlinx.coroutines.experimental.internal.LinkedListHead.forEach_8jvfi5$', wrapFunction(function () {
    var equals = Kotlin.equals;
    return function (T_0, isT, block) {
      var cur = this._next;
      while (!equals(cur, this)) {
        if (isT(cur))
          block(cur);
        cur = cur._next;
      }
    };
  }));
  LinkedListHead.prototype.remove = function () {
    throw UnsupportedOperationException_init_0();
  };
  LinkedListHead.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LinkedListHead',
    interfaces: [LinkedListNode]
  };
  function TimeUnit(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function TimeUnit_initFields() {
    TimeUnit_initFields = function () {
    };
    TimeUnit$MILLISECONDS_instance = new TimeUnit('MILLISECONDS', 0);
    TimeUnit$SECONDS_instance = new TimeUnit('SECONDS', 1);
  }
  var TimeUnit$MILLISECONDS_instance;
  function TimeUnit$MILLISECONDS_getInstance() {
    TimeUnit_initFields();
    return TimeUnit$MILLISECONDS_instance;
  }
  var TimeUnit$SECONDS_instance;
  function TimeUnit$SECONDS_getInstance() {
    TimeUnit_initFields();
    return TimeUnit$SECONDS_instance;
  }
  TimeUnit.prototype.toMillis_s8cxhz$ = function (time) {
    switch (this.name) {
      case 'MILLISECONDS':
        return time;
      case 'SECONDS':
        if (time.compareTo_11rb$(L9223372036854775) >= 0)
          return Long$Companion$MAX_VALUE;
        else if (time.compareTo_11rb$(L_9223372036854775) <= 0)
          return Long$Companion$MIN_VALUE;
        else
          return time.multiply(L1000);
      default:return Kotlin.noWhenBranchMatched();
    }
  };
  TimeUnit.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimeUnit',
    interfaces: [Enum]
  };
  function TimeUnit$values() {
    return [TimeUnit$MILLISECONDS_getInstance(), TimeUnit$SECONDS_getInstance()];
  }
  TimeUnit.values = TimeUnit$values;
  function TimeUnit$valueOf(name) {
    switch (name) {
      case 'MILLISECONDS':
        return TimeUnit$MILLISECONDS_getInstance();
      case 'SECONDS':
        return TimeUnit$SECONDS_getInstance();
      default:throwISE('No enum constant kotlinx.coroutines.experimental.timeunit.TimeUnit.' + name);
    }
  }
  TimeUnit.valueOf_61zpoe$ = TimeUnit$valueOf;
  $$importsForInline$$['kotlinx-coroutines-core'] = _;
  $$importsForInline$$['kotlinx-atomicfu'] = $module$kotlinx_atomicfu;
  var package$kotlinx = _.kotlinx || (_.kotlinx = {});
  var package$coroutines = package$kotlinx.coroutines || (package$kotlinx.coroutines = {});
  var package$experimental = package$coroutines.experimental || (package$coroutines.experimental = {});
  package$experimental.AbstractContinuation = AbstractContinuation;
  package$experimental.NotCompleted = NotCompleted;
  package$experimental.Cancelling = Cancelling;
  package$experimental.CancelHandler = CancelHandler;
  package$experimental.AbstractCoroutine = AbstractCoroutine;
  package$experimental.awaitAll_hcji7n$ = awaitAll;
  package$experimental.awaitAll_rbef5q$ = awaitAll_0;
  package$experimental.joinAll_hz058w$ = joinAll;
  package$experimental.joinAll_3ofj8v$ = joinAll_0;
  package$experimental.launch_35c74u$ = launch;
  package$experimental.launch_ej4974$ = launch_0;
  package$experimental.launch_3he5ka$ = launch_1;
  package$experimental.launch_duazz$ = launch_2;
  package$experimental.withContext_xy9lwp$ = withContext;
  package$experimental.run_xy9lwp$ = run;
  package$experimental.run_g3zeo5$ = run_0;
  package$experimental.CancellableContinuation = CancellableContinuation;
  package$experimental.removeOnCancel_qng3go$ = removeOnCancel;
  package$experimental.removeOnCancellation_qng3go$ = removeOnCancellation;
  package$experimental.disposeOnCompletion_y25j86$ = disposeOnCompletion;
  package$experimental.disposeOnCancellation_y25j86$ = disposeOnCancellation;
  package$experimental.CancellableContinuationImpl = CancellableContinuationImpl;
  package$experimental.CompletableDeferred = CompletableDeferred;
  package$experimental.CompletableDeferred_br6pg6$ = CompletableDeferred_0;
  package$experimental.CompletableDeferred_287e2$ = CompletableDeferred_1;
  package$experimental.CompletableDeferred_mh5how$ = CompletableDeferred_2;
  package$experimental.CompletedExceptionally = CompletedExceptionally;
  package$experimental.Cancelled = Cancelled;
  package$experimental.CancelledContinuation = CancelledContinuation;
  package$experimental.CoroutineDispatcher = CoroutineDispatcher;
  package$experimental.handleCoroutineException_y5fbjc$ = handleCoroutineException;
  package$experimental.CoroutineExceptionHandler = CoroutineExceptionHandler_0;
  package$experimental.CoroutineExceptionHandler_av07nd$ = CoroutineExceptionHandler;
  Object.defineProperty(CoroutineExceptionHandler_0, 'Key', {
    get: CoroutineExceptionHandler$Key_getInstance
  });
  package$experimental.CoroutineScope = CoroutineScope;
  Object.defineProperty(CoroutineStart, 'DEFAULT', {
    get: CoroutineStart$DEFAULT_getInstance
  });
  Object.defineProperty(CoroutineStart, 'LAZY', {
    get: CoroutineStart$LAZY_getInstance
  });
  Object.defineProperty(CoroutineStart, 'ATOMIC', {
    get: CoroutineStart$ATOMIC_getInstance
  });
  Object.defineProperty(CoroutineStart, 'UNDISPATCHED', {
    get: CoroutineStart$UNDISPATCHED_getInstance
  });
  package$experimental.CoroutineStart = CoroutineStart;
  package$experimental.Deferred = Deferred;
  package$experimental.async_vewznt$ = async;
  package$experimental.async_nrwt9h$ = async_0;
  package$experimental.async_frprgf$ = async_1;
  package$experimental.async_uhk0km$ = async_2;
  package$experimental.defer_l4f24z$ = defer;
  package$experimental.Delay = Delay;
  package$experimental.delay_za3lpa$ = delay;
  package$experimental.delay_wex4td$ = delay_0;
  package$experimental.get_delay_3jfoph$ = get_delay;
  package$experimental.withCoroutineContext_ms9kem$ = withCoroutineContext;
  package$experimental.DispatchedContinuation = DispatchedContinuation;
  package$experimental.resumeCancellable_seoz30$ = resumeCancellable;
  package$experimental.resumeCancellableWithException_nzgwnd$ = resumeCancellableWithException;
  package$experimental.resumeDirect_seoz30$ = resumeDirect;
  package$experimental.resumeDirectWithException_nzgwnd$ = resumeDirectWithException;
  package$experimental.DispatchedTask = DispatchedTask;
  package$experimental.dispatch_ku5vcm$ = dispatch;
  Object.defineProperty(Job, 'Key', {
    get: Job$Key_getInstance
  });
  package$experimental.Job = Job;
  package$experimental.Job_c6qot0$ = Job_0;
  package$experimental.DisposableHandle = DisposableHandle;
  package$experimental.unregisterOnCompletion_dwx8z6$ = unregisterOnCompletion;
  package$experimental.disposeOnCompletion_dwx8z6$ = disposeOnCompletion_0;
  package$experimental.cancelAndJoin_c6qotg$ = cancelAndJoin;
  package$experimental.cancelChildren_irwgr4$ = cancelChildren;
  package$experimental.joinChildren_c6qotg$ = joinChildren;
  package$experimental.get_isActive_45jet4$ = get_isActive;
  package$experimental.cancel_erq06s$ = cancel;
  package$experimental.cancelChildren_erq06s$ = cancelChildren_0;
  package$experimental.join_c6qotg$ = join;
  Object.defineProperty(package$experimental, 'NonDisposableHandle', {
    get: NonDisposableHandle_getInstance
  });
  package$experimental.JobSupport = JobSupport;
  Object.defineProperty(package$experimental, 'ON_CANCEL_MAKE_CANCELLING_8be2vx$', {
    get: function () {
      return ON_CANCEL_MAKE_CANCELLING;
    }
  });
  Object.defineProperty(package$experimental, 'ON_CANCEL_MAKE_COMPLETING_8be2vx$', {
    get: function () {
      return ON_CANCEL_MAKE_COMPLETING;
    }
  });
  package$experimental.JobImpl = JobImpl;
  package$experimental.Incomplete = Incomplete;
  package$experimental.JobNode = JobNode;
  package$experimental.NodeList = NodeList;
  package$experimental.DisposeOnCompletion = DisposeOnCompletion;
  package$experimental.JobCancellationNode = JobCancellationNode;
  package$experimental.ChildJob = ChildJob;
  package$experimental.ChildContinuation = ChildContinuation;
  Object.defineProperty(package$experimental, 'NonCancellable', {
    get: NonCancellable_getInstance
  });
  Object.defineProperty(package$experimental, 'MODE_ATOMIC_DEFAULT', {
    get: function () {
      return MODE_ATOMIC_DEFAULT;
    }
  });
  Object.defineProperty(package$experimental, 'MODE_CANCELLABLE', {
    get: function () {
      return MODE_CANCELLABLE;
    }
  });
  Object.defineProperty(package$experimental, 'MODE_DIRECT', {
    get: function () {
      return MODE_DIRECT;
    }
  });
  Object.defineProperty(package$experimental, 'MODE_UNDISPATCHED', {
    get: function () {
      return MODE_UNDISPATCHED;
    }
  });
  Object.defineProperty(package$experimental, 'MODE_IGNORE', {
    get: function () {
      return MODE_IGNORE;
    }
  });
  package$experimental.get_isCancellableMode_8e50z4$ = get_isCancellableMode;
  package$experimental.get_isDispatchedMode_8e50z4$ = get_isDispatchedMode;
  package$experimental.resumeMode_ym8jpa$ = resumeMode;
  package$experimental.resumeWithExceptionMode_ydqgjr$ = resumeWithExceptionMode;
  package$experimental.withTimeout_dv38ag$ = withTimeout;
  package$experimental.withTimeout_4ks2t3$ = withTimeout_0;
  package$experimental.withTimeout_n358oh$ = withTimeout_1;
  package$experimental.withTimeoutOrNull_dv38ag$ = withTimeoutOrNull;
  package$experimental.withTimeoutOrNull_4ks2t3$ = withTimeoutOrNull_0;
  package$experimental.withTimeoutOrNull_n358oh$ = withTimeoutOrNull_1;
  package$experimental.TimeoutCancellationException_init_61zpoe$ = TimeoutCancellationException_init;
  package$experimental.TimeoutCancellationException = TimeoutCancellationException;
  package$experimental.TimeoutCancellationException_sjbusr$ = TimeoutCancellationException_0;
  Object.defineProperty(package$experimental, 'Unconfined', {
    get: Unconfined_getInstance
  });
  package$experimental.yield = yield_0;
  package$experimental.checkCompletion_3jfoph$ = checkCompletion;
  AbstractSendChannel.TryOfferDesc = AbstractSendChannel$TryOfferDesc;
  var package$channels = package$experimental.channels || (package$experimental.channels = {});
  package$channels.AbstractSendChannel = AbstractSendChannel;
  AbstractChannel.TryPollDesc = AbstractChannel$TryPollDesc;
  package$channels.AbstractChannel = AbstractChannel;
  Object.defineProperty(package$channels, 'OFFER_SUCCESS_8be2vx$', {
    get: function () {
      return OFFER_SUCCESS;
    }
  });
  Object.defineProperty(package$channels, 'OFFER_FAILED_8be2vx$', {
    get: function () {
      return OFFER_FAILED;
    }
  });
  Object.defineProperty(package$channels, 'POLL_FAILED_8be2vx$', {
    get: function () {
      return POLL_FAILED;
    }
  });
  Object.defineProperty(package$channels, 'ENQUEUE_FAILED_8be2vx$', {
    get: function () {
      return ENQUEUE_FAILED;
    }
  });
  Object.defineProperty(package$channels, 'SELECT_STARTED_8be2vx$', {
    get: function () {
      return SELECT_STARTED;
    }
  });
  Object.defineProperty(package$channels, 'NULL_VALUE_8be2vx$', {
    get: function () {
      return NULL_VALUE;
    }
  });
  Object.defineProperty(package$channels, 'CLOSE_RESUMED_8be2vx$', {
    get: function () {
      return CLOSE_RESUMED;
    }
  });
  Object.defineProperty(package$channels, 'SEND_RESUMED_8be2vx$', {
    get: function () {
      return SEND_RESUMED;
    }
  });
  package$channels.Send = Send;
  package$channels.ReceiveOrClosed = ReceiveOrClosed;
  package$channels.SendElement = SendElement;
  package$channels.Closed = Closed;
  package$channels.ArrayBroadcastChannel = ArrayBroadcastChannel;
  package$channels.ArrayChannel = ArrayChannel;
  package$channels.broadcast_4xqp0s$ = broadcast;
  package$channels.broadcast_yibscd$ = broadcast_0;
  Object.defineProperty(BroadcastChannel, 'Factory', {
    get: BroadcastChannel$Factory_getInstance
  });
  package$channels.BroadcastChannel = BroadcastChannel;
  package$channels.BroadcastChannel_ww73n8$ = BroadcastChannel_0;
  package$channels.SubscriptionReceiveChannel = SubscriptionReceiveChannel;
  package$channels.use_e0tfc5$ = use;
  package$channels.SendChannel = SendChannel;
  package$channels.ReceiveChannel = ReceiveChannel;
  package$channels.ChannelIterator = ChannelIterator;
  Object.defineProperty(Channel, 'Factory', {
    get: Channel$Factory_getInstance
  });
  package$channels.Channel = Channel;
  package$channels.Channel_287e2$ = Channel_0;
  package$channels.Channel_ww73n8$ = Channel_1;
  package$channels.ClosedSendChannelException = ClosedSendChannelException;
  package$channels.ClosedReceiveChannelException = ClosedReceiveChannelException;
  package$channels.ChannelCoroutine = ChannelCoroutine;
  Object.defineProperty(package$channels, 'DEFAULT_CLOSE_MESSAGE_8be2vx$', {
    get: function () {
      return DEFAULT_CLOSE_MESSAGE;
    }
  });
  package$channels.asReceiveChannel_20e8xb$ = asReceiveChannel;
  package$channels.asReceiveChannel_x6rbgj$ = asReceiveChannel_0;
  package$channels.consume_tbmr54$ = consume;
  package$channels.consumeEach_7tq7si$ = consumeEach_0;
  package$channels.consumes_wnk14d$ = consumes;
  package$channels.consumesAll_7pip7h$ = consumesAll;
  package$channels.consume_e0tfc5$ = consume_0;
  package$channels.consumeEach_c5i63v$ = consumeEach_2;
  package$channels.elementAt_3te9k0$ = elementAt;
  package$channels.elementAtOrNull_3te9k0$ = elementAtOrNull;
  package$channels.first_w1g8jm$ = first;
  package$channels.firstOrNull_w1g8jm$ = firstOrNull;
  package$channels.indexOf_q5pg58$ = indexOf_0;
  package$channels.last_w1g8jm$ = last;
  package$channels.lastIndexOf_q5pg58$ = lastIndexOf;
  package$channels.lastOrNull_w1g8jm$ = lastOrNull;
  package$channels.single_w1g8jm$ = single;
  package$channels.singleOrNull_w1g8jm$ = singleOrNull;
  package$channels.drop_gw43p1$ = drop;
  package$channels.dropWhile_tw4ism$ = dropWhile;
  package$channels.filter_tw4ism$ = filter;
  package$channels.filterIndexed_7siz5u$ = filterIndexed;
  package$channels.filterNot_tw4ism$ = filterNot;
  package$channels.filterNot_jkehw1$ = filterNot_0;
  package$channels.filterNotNull_vnfp5n$ = filterNotNull;
  package$channels.filterNotNullTo_kpw1p6$ = filterNotNullTo;
  package$channels.filterNotNullTo_a61q3h$ = filterNotNullTo_0;
  package$channels.take_gw43p1$ = take;
  package$channels.takeWhile_tw4ism$ = takeWhile;
  package$channels.toChannel_tozirq$ = toChannel;
  package$channels.toCollection_h3rikd$ = toCollection;
  package$channels.toList_w1g8jm$ = toList;
  package$channels.toMap_eh40bl$ = toMap;
  package$channels.toMap_nca142$ = toMap_0;
  package$channels.toMutableList_w1g8jm$ = toMutableList;
  package$channels.toSet_w1g8jm$ = toSet;
  package$channels.flatMap_yuy0u4$ = flatMap;
  package$channels.map_do98gf$ = map;
  package$channels.mapIndexed_vcxus5$ = mapIndexed;
  package$channels.mapIndexedNotNull_98d6bc$ = mapIndexedNotNull;
  package$channels.mapNotNull_1q82r0$ = mapNotNull;
  package$channels.withIndex_ydf5hv$ = withIndex;
  package$channels.distinct_w1g8jm$ = distinct;
  package$channels.distinctBy_do98gf$ = distinctBy;
  package$channels.toMutableSet_w1g8jm$ = toMutableSet;
  package$channels.any_w1g8jm$ = any;
  package$channels.count_w1g8jm$ = count;
  package$channels.maxWith_6lag2q$ = maxWith;
  package$channels.minWith_6lag2q$ = minWith;
  package$channels.none_w1g8jm$ = none;
  package$channels.requireNoNulls_vnfp5n$ = requireNoNulls;
  package$channels.zip_nivstn$ = zip;
  package$channels.zip_sp6mz5$ = zip_0;
  package$channels.ConflatedBroadcastChannel_init_mh5how$ = ConflatedBroadcastChannel_init;
  package$channels.ConflatedBroadcastChannel = ConflatedBroadcastChannel;
  package$channels.ConflatedChannel = ConflatedChannel;
  package$channels.LinkedListChannel = LinkedListChannel;
  package$channels.ProducerScope = ProducerScope;
  package$channels.ProducerJob = ProducerJob;
  package$channels.produce_ffviej$ = produce;
  package$channels.produce_o3zqmb$ = produce_0;
  package$channels.produce_m0uxjh$ = produce_1;
  package$channels.buildChannel_m0uxjh$ = buildChannel;
  package$channels.RendezvousChannel = RendezvousChannel;
  var package$internal = package$experimental.internal || (package$experimental.internal = {});
  package$internal.OpDescriptor = OpDescriptor;
  package$internal.AtomicOp = AtomicOp;
  package$internal.AtomicDesc = AtomicDesc;
  package$internal.Symbol = Symbol;
  var package$intrinsics = package$experimental.intrinsics || (package$experimental.intrinsics = {});
  package$intrinsics.startCoroutineCancellable_xtwlez$ = startCoroutineCancellable;
  package$intrinsics.startCoroutineCancellable_uao1qo$ = startCoroutineCancellable_0;
  package$intrinsics.startCoroutineUndispatched_xtwlez$ = startCoroutineUndispatched;
  package$intrinsics.startCoroutineUndispatched_uao1qo$ = startCoroutineUndispatched_0;
  package$intrinsics.startUndispatchedOrReturn_4yh02o$ = startUndispatchedOrReturn;
  package$intrinsics.startUndispatchedOrReturn_ecekzd$ = startUndispatchedOrReturn_0;
  var package$selects = package$experimental.selects || (package$experimental.selects = {});
  package$selects.SelectBuilder = SelectBuilder;
  package$selects.SelectClause0 = SelectClause0;
  package$selects.SelectClause1 = SelectClause1;
  package$selects.SelectClause2 = SelectClause2;
  package$selects.SelectInstance = SelectInstance;
  Object.defineProperty(package$selects, 'ALREADY_SELECTED_8be2vx$', {
    get: function () {
      return ALREADY_SELECTED;
    }
  });
  package$selects.SelectBuilderImpl = SelectBuilderImpl;
  package$selects.UnbiasedSelectBuilderImpl = UnbiasedSelectBuilderImpl;
  package$selects.whileSelect_en850d$ = whileSelect;
  var package$sync = package$experimental.sync || (package$experimental.sync = {});
  package$sync.Mutex = Mutex;
  package$sync.Mutex_6taknv$ = Mutex_0;
  package$sync.withLock_ea6vdh$ = withLock_0;
  package$sync.withLock_b5he4h$ = withLock_1;
  package$sync.withMutex_b5he4h$ = withMutex;
  package$sync.MutexImpl = MutexImpl;
  var package$internalAnnotations = package$experimental.internalAnnotations || (package$experimental.internalAnnotations = {});
  package$internalAnnotations.JvmName = JvmName;
  package$internalAnnotations.JvmMultifileClass = JvmMultifileClass;
  package$internalAnnotations.JvmField = JvmField;
  package$internalAnnotations.Volatile = Volatile;
  package$experimental.CompletionHandlerBase = CompletionHandlerBase;
  package$experimental.get_asHandler_h9unsn$ = get_asHandler;
  package$experimental.CancelHandlerBase = CancelHandlerBase;
  package$experimental.get_asHandler_hadnfv$ = get_asHandler_0;
  package$experimental.invokeIt_beznmj$ = invokeIt;
  Object.defineProperty(package$experimental, 'DefaultDispatcher', {
    get: function () {
      return DefaultDispatcher;
    }
  });
  Object.defineProperty(package$experimental, 'DefaultDelay_8be2vx$', {
    get: function () {
      return DefaultDelay;
    }
  });
  package$experimental.newCoroutineContext_y0hpzz$ = newCoroutineContext;
  package$experimental.toDebugString_34n6ex$ = toDebugString;
  package$experimental.get_coroutineName_3jfoph$ = get_coroutineName;
  package$experimental.handleCoroutineExceptionImpl_bgelrv$ = handleCoroutineExceptionImpl;
  package$experimental.get_hexAddress_8ea4r1$ = get_hexAddress;
  package$experimental.get_classSimpleName_8ea4r1$ = get_classSimpleName;
  package$experimental.CompletionHandlerException = CompletionHandlerException;
  package$experimental.CancellationException = CancellationException;
  package$experimental.JobCancellationException = JobCancellationException;
  package$experimental.DispatchException = DispatchException;
  package$experimental.IllegalStateException_ly7if3$ = IllegalStateException_0;
  package$experimental.addSuppressedThrowable_oz8fe6$ = addSuppressedThrowable;
  package$experimental.NodeDispatcher = NodeDispatcher;
  package$experimental.WindowDispatcher = WindowDispatcher;
  package$experimental.MessageQueue = MessageQueue;
  package$experimental.Queue = Queue;
  package$experimental.promise_vewznt$ = promise;
  package$experimental.asPromise_l87ck7$ = asPromise;
  package$experimental.asDeferred_t11jrl$ = asDeferred;
  package$experimental.await_t11jrl$ = await_0;
  package$experimental.Runnable = Runnable;
  package$experimental.Runnable_o14v8n$ = Runnable_0;
  package$experimental.asCoroutineDispatcher_nz12v2$ = asCoroutineDispatcher;
  package$experimental.awaitAnimationFrame_nz12v2$ = awaitAnimationFrame;
  package$internal.arraycopy_t6l26v$ = arraycopy;
  package$internal.Closeable = Closeable;
  package$internal.withLock_ful2h8$ = withLock_2;
  package$internal.NoOpLock = NoOpLock;
  package$internal.subscriberList_tnbmyv$ = subscriberList;
  package$internal.LinkedListNode = LinkedListNode;
  package$internal.AddLastDesc = AddLastDesc;
  package$internal.RemoveFirstDesc = RemoveFirstDesc;
  package$internal.AbstractAtomicDesc = AbstractAtomicDesc;
  package$internal.LinkedListHead = LinkedListHead;
  Object.defineProperty(TimeUnit, 'MILLISECONDS', {
    get: TimeUnit$MILLISECONDS_getInstance
  });
  Object.defineProperty(TimeUnit, 'SECONDS', {
    get: TimeUnit$SECONDS_getInstance
  });
  var package$timeunit = package$experimental.timeunit || (package$experimental.timeunit = {});
  package$timeunit.TimeUnit = TimeUnit;
  AbstractContinuation.prototype.getSuccessfulResult_tpy1pm$ = DispatchedTask.prototype.getSuccessfulResult_tpy1pm$;
  AbstractContinuation.prototype.getExceptionalResult_s8jyv4$ = DispatchedTask.prototype.getExceptionalResult_s8jyv4$;
  AbstractContinuation.prototype.run = DispatchedTask.prototype.run;
  Job.prototype.plus_dvqyjb$ = CoroutineContext$Element.prototype.plus_dvqyjb$;
  Job.prototype.fold_m9u1mr$ = CoroutineContext$Element.prototype.fold_m9u1mr$;
  Job.prototype.get_8oh8b3$ = CoroutineContext$Element.prototype.get_8oh8b3$;
  Job.prototype.minusKey_ds72xk$ = CoroutineContext$Element.prototype.minusKey_ds72xk$;
  JobSupport.prototype.getCompletionException = Job.prototype.getCompletionException;
  JobSupport.prototype.plus_r3p3g3$ = Job.prototype.plus_r3p3g3$;
  JobSupport.prototype.plus_dvqyjb$ = Job.prototype.plus_dvqyjb$;
  JobSupport.prototype.fold_m9u1mr$ = Job.prototype.fold_m9u1mr$;
  JobSupport.prototype.get_8oh8b3$ = Job.prototype.get_8oh8b3$;
  JobSupport.prototype.minusKey_ds72xk$ = Job.prototype.minusKey_ds72xk$;
  JobSupport.prototype.invokeOnCompletion_h883ze$ = Job.prototype.invokeOnCompletion_h883ze$;
  JobSupport.prototype.invokeOnCompletion_ct2b2z$ = Job.prototype.invokeOnCompletion_ct2b2z$;
  JobSupport.prototype.cancel_dbl4no$ = Job.prototype.cancel_dbl4no$;
  JobSupport.prototype.cancelChildren_dbl4no$ = Job.prototype.cancelChildren_dbl4no$;
  CancellableContinuationImpl.prototype.cancel_dbl4no$$default = AbstractContinuation.prototype.cancel_dbl4no$;
  CancellableContinuationImpl.prototype.cancel_dbl4no$ = CancellableContinuation.prototype.cancel_dbl4no$;
  CancellableContinuationImpl.prototype.invokeOnCompletion_ct2b2z$ = CancellableContinuation.prototype.invokeOnCompletion_ct2b2z$;
  CancellableContinuationImpl.prototype.tryResume_19pj23$ = CancellableContinuation.prototype.tryResume_19pj23$;
  Deferred.prototype.getCompletionException = Job.prototype.getCompletionException;
  Deferred.prototype.plus_r3p3g3$ = Job.prototype.plus_r3p3g3$;
  Deferred.prototype.plus_dvqyjb$ = Job.prototype.plus_dvqyjb$;
  Deferred.prototype.fold_m9u1mr$ = Job.prototype.fold_m9u1mr$;
  Deferred.prototype.get_8oh8b3$ = Job.prototype.get_8oh8b3$;
  Deferred.prototype.minusKey_ds72xk$ = Job.prototype.minusKey_ds72xk$;
  Deferred.prototype.cancel_dbl4no$ = Job.prototype.cancel_dbl4no$;
  Deferred.prototype.cancelChildren_dbl4no$ = Job.prototype.cancelChildren_dbl4no$;
  Deferred.prototype.invokeOnCompletion_h883ze$ = Job.prototype.invokeOnCompletion_h883ze$;
  Deferred.prototype.invokeOnCompletion_ct2b2z$ = Job.prototype.invokeOnCompletion_ct2b2z$;
  Object.defineProperty(CompletableDeferred.prototype, 'isComputing', Object.getOwnPropertyDescriptor(Deferred.prototype, 'isComputing'));
  CompletableDeferred.prototype.getCompletionException = Deferred.prototype.getCompletionException;
  CompletableDeferred.prototype.plus_r3p3g3$ = Deferred.prototype.plus_r3p3g3$;
  CompletableDeferred.prototype.plus_dvqyjb$ = Deferred.prototype.plus_dvqyjb$;
  CompletableDeferred.prototype.fold_m9u1mr$ = Deferred.prototype.fold_m9u1mr$;
  CompletableDeferred.prototype.get_8oh8b3$ = Deferred.prototype.get_8oh8b3$;
  CompletableDeferred.prototype.minusKey_ds72xk$ = Deferred.prototype.minusKey_ds72xk$;
  CompletableDeferred.prototype.cancel_dbl4no$ = Deferred.prototype.cancel_dbl4no$;
  CompletableDeferred.prototype.cancelChildren_dbl4no$ = Deferred.prototype.cancelChildren_dbl4no$;
  CompletableDeferred.prototype.invokeOnCompletion_h883ze$ = Deferred.prototype.invokeOnCompletion_h883ze$;
  CompletableDeferred.prototype.invokeOnCompletion_ct2b2z$ = Deferred.prototype.invokeOnCompletion_ct2b2z$;
  Object.defineProperty(CompletableDeferredImpl.prototype, 'isComputing', Object.getOwnPropertyDescriptor(CompletableDeferred.prototype, 'isComputing'));
  CoroutineExceptionHandler_0.prototype.fold_m9u1mr$ = CoroutineContext$Element.prototype.fold_m9u1mr$;
  CoroutineExceptionHandler_0.prototype.get_8oh8b3$ = CoroutineContext$Element.prototype.get_8oh8b3$;
  CoroutineExceptionHandler_0.prototype.minusKey_ds72xk$ = CoroutineContext$Element.prototype.minusKey_ds72xk$;
  CoroutineExceptionHandler_0.prototype.plus_dvqyjb$ = CoroutineContext$Element.prototype.plus_dvqyjb$;
  Object.defineProperty(DeferredCoroutine.prototype, 'isComputing', Object.getOwnPropertyDescriptor(Deferred.prototype, 'isComputing'));
  DispatchedContinuation.prototype.getSuccessfulResult_tpy1pm$ = DispatchedTask.prototype.getSuccessfulResult_tpy1pm$;
  DispatchedContinuation.prototype.getExceptionalResult_s8jyv4$ = DispatchedTask.prototype.getExceptionalResult_s8jyv4$;
  DispatchedContinuation.prototype.run = DispatchedTask.prototype.run;
  NonCancellable.prototype.plus_r3p3g3$ = Job.prototype.plus_r3p3g3$;
  NonCancellable.prototype.getCompletionException = Job.prototype.getCompletionException;
  NonCancellable.prototype.invokeOnCompletion_h883ze$ = Job.prototype.invokeOnCompletion_h883ze$;
  NonCancellable.prototype.invokeOnCompletion_ct2b2z$ = Job.prototype.invokeOnCompletion_ct2b2z$;
  NonCancellable.prototype.cancel_dbl4no$ = Job.prototype.cancel_dbl4no$;
  NonCancellable.prototype.cancelChildren_dbl4no$ = Job.prototype.cancelChildren_dbl4no$;
  AbstractSendChannel.prototype.close_dbl4no$ = SendChannel.prototype.close_dbl4no$;
  Channel.prototype.close_dbl4no$ = SendChannel.prototype.close_dbl4no$;
  Channel.prototype.cancel_dbl4no$ = ReceiveChannel.prototype.cancel_dbl4no$;
  AbstractChannel.prototype.cancel_dbl4no$ = Channel.prototype.cancel_dbl4no$;
  SubscriptionReceiveChannel.prototype.cancel_dbl4no$ = ReceiveChannel.prototype.cancel_dbl4no$;
  ArrayBroadcastChannel$Subscriber.prototype.close = SubscriptionReceiveChannel.prototype.close;
  BroadcastChannel.prototype.close_dbl4no$ = SendChannel.prototype.close_dbl4no$;
  ArrayBroadcastChannel.prototype.openSubscription1 = BroadcastChannel.prototype.openSubscription1;
  ArrayBroadcastChannel.prototype.open = BroadcastChannel.prototype.open;
  ArrayBroadcastChannel.prototype.cancel_dbl4no$ = BroadcastChannel.prototype.cancel_dbl4no$;
  ProducerScope.prototype.close_dbl4no$ = SendChannel.prototype.close_dbl4no$;
  BroadcastCoroutine.prototype.close_dbl4no$ = ProducerScope.prototype.close_dbl4no$;
  ChannelCoroutine.prototype.close_dbl4no$ = Channel.prototype.close_dbl4no$;
  ConflatedBroadcastChannel$Subscriber.prototype.close = SubscriptionReceiveChannel.prototype.close;
  ConflatedBroadcastChannel.prototype.openSubscription1 = BroadcastChannel.prototype.openSubscription1;
  ConflatedBroadcastChannel.prototype.open = BroadcastChannel.prototype.open;
  ConflatedBroadcastChannel.prototype.close_dbl4no$ = BroadcastChannel.prototype.close_dbl4no$;
  ConflatedBroadcastChannel.prototype.cancel_dbl4no$ = BroadcastChannel.prototype.cancel_dbl4no$;
  ProducerJob.prototype.getCompletionException = Job.prototype.getCompletionException;
  ProducerJob.prototype.plus_r3p3g3$ = Job.prototype.plus_r3p3g3$;
  ProducerJob.prototype.plus_dvqyjb$ = Job.prototype.plus_dvqyjb$;
  ProducerJob.prototype.fold_m9u1mr$ = Job.prototype.fold_m9u1mr$;
  ProducerJob.prototype.get_8oh8b3$ = Job.prototype.get_8oh8b3$;
  ProducerJob.prototype.minusKey_ds72xk$ = Job.prototype.minusKey_ds72xk$;
  ProducerJob.prototype.cancelChildren_dbl4no$ = Job.prototype.cancelChildren_dbl4no$;
  ProducerJob.prototype.invokeOnCompletion_h883ze$ = Job.prototype.invokeOnCompletion_h883ze$;
  ProducerJob.prototype.invokeOnCompletion_ct2b2z$ = Job.prototype.invokeOnCompletion_ct2b2z$;
  SelectBuilderImpl.prototype.invoke_1c9369$ = SelectBuilder.prototype.invoke_1c9369$;
  SelectBuilderImpl.prototype.onTimeout_yg8mdg$ = SelectBuilder.prototype.onTimeout_yg8mdg$;
  UnbiasedSelectBuilderImpl.prototype.invoke_1c9369$ = SelectBuilder.prototype.invoke_1c9369$;
  UnbiasedSelectBuilderImpl.prototype.onTimeout_yg8mdg$ = SelectBuilder.prototype.onTimeout_yg8mdg$;
  MutexImpl.prototype.tryLock_s8jyv4$ = Mutex.prototype.tryLock_s8jyv4$;
  MutexImpl.prototype.lock_s8jyv4$ = Mutex.prototype.lock_s8jyv4$;
  MutexImpl.prototype.unlock_s8jyv4$ = Mutex.prototype.unlock_s8jyv4$;
  NodeDispatcher.prototype.delay_wex4td$$default = Delay.prototype.delay_wex4td$$default;
  NodeDispatcher.prototype.delay_wex4td$ = Delay.prototype.delay_wex4td$;
  WindowDispatcher.prototype.delay_wex4td$$default = Delay.prototype.delay_wex4td$$default;
  WindowDispatcher.prototype.delay_wex4td$ = Delay.prototype.delay_wex4td$;
  UNDECIDED = 0;
  SUSPENDED = 1;
  RESUMED = 2;
  ACTIVE = new Active();
  UNDEFINED = new Symbol('UNDEFINED');
  ON_CANCEL_MAKE_CANCELLING = 0;
  ON_CANCEL_MAKE_COMPLETING = 1;
  COMPLETING_ALREADY_COMPLETING = 0;
  COMPLETING_COMPLETED = 1;
  COMPLETING_WAITING_CHILDREN = 2;
  RETRY = -1;
  FALSE = 0;
  TRUE = 1;
  EmptyNew = new Empty(false);
  EmptyActive = new Empty(true);
  MODE_ATOMIC_DEFAULT = 0;
  MODE_CANCELLABLE = 1;
  MODE_DIRECT = 2;
  MODE_UNDISPATCHED = 3;
  MODE_IGNORE = 4;
  OFFER_SUCCESS = new Symbol('OFFER_SUCCESS');
  OFFER_FAILED = new Symbol('OFFER_FAILED');
  POLL_FAILED = new Symbol('POLL_FAILED');
  ENQUEUE_FAILED = new Symbol('ENQUEUE_FAILED');
  SELECT_STARTED = new Symbol('SELECT_STARTED');
  NULL_VALUE = new Symbol('NULL_VALUE');
  CLOSE_RESUMED = new Symbol('CLOSE_RESUMED');
  SEND_RESUMED = new Symbol('SEND_RESUMED');
  DEFAULT_CLOSE_MESSAGE = 'Channel was closed';
  NO_DECISION = new Symbol('NO_DECISION');
  ALREADY_SELECTED = new Symbol('ALREADY_SELECTED');
  UNDECIDED_0 = new Symbol('UNDECIDED');
  RESUMED_0 = new Symbol('RESUMED');
  LOCK_FAIL = new Symbol('LOCK_FAIL');
  ENQUEUE_FAIL = new Symbol('ENQUEUE_FAIL');
  UNLOCK_FAIL = new Symbol('UNLOCK_FAIL');
  SELECT_SUCCESS = new Symbol('SELECT_SUCCESS');
  LOCKED = new Symbol('LOCKED');
  UNLOCKED = new Symbol('UNLOCKED');
  RESUME_QUIESCENT = new Symbol('RESUME_QUIESCENT');
  RESUME_ACTIVE = new Symbol('RESUME_ACTIVE');
  EmptyLocked = new Empty_0(LOCKED);
  EmptyUnlocked = new Empty_0(UNLOCKED);
  UNDEFINED_0 = 'undefined';
  var tmp$, tmp$_0;
  if (!equals(typeof navigator, UNDEFINED_0) && navigator != null && navigator.product == 'ReactNative')
    tmp$ = new NodeDispatcher();
  else {
    var tmp$_1 = !equals(typeof window, UNDEFINED_0) && window != null;
    if (tmp$_1) {
      tmp$_1 = !equals(typeof window.addEventListener, UNDEFINED_0);
    }
    if (tmp$_1)
      tmp$ = asCoroutineDispatcher(window);
    else
      tmp$ = new NodeDispatcher();
  }
  DefaultDispatcher = tmp$;
  DefaultDelay = Kotlin.isType(tmp$_0 = DefaultDispatcher, Delay) ? tmp$_0 : throwCCE();
  counter = 0;
  Kotlin.defineModule('kotlinx-coroutines-core', _);
  return _;
}));

//# sourceMappingURL=kotlinx-coroutines-core.js.map
