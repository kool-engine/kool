package de.fabmax.kool.modules.ksl.lang

class KslDeclareArray(val declareVar: KslArray<*>, val elements: List<KslExpression<*>>, parentScope: KslScopeBuilder)
    : KslStatement("declareArray", parentScope) {

    constructor(declareVar: KslArray<*>, element: KslExpression<*>, parentScope: KslScopeBuilder) :
            this(declareVar, List(declareVar.arraySize) { element }, parentScope)

    constructor(declareVar: KslArray<*>, assignExpr: KslArrayExpression<*>, parentScope: KslScopeBuilder) :
            this(declareVar, listOf(assignExpr), parentScope)

    constructor(declareVar: KslArray<*>, assignExpr: KslScalarArrayExpression<*>, parentScope: KslScopeBuilder) :
            this(declareVar, listOf(assignExpr), parentScope)

    constructor(declareVar: KslArray<*>, assignExpr: KslVectorArrayExpression<*,*>, parentScope: KslScopeBuilder) :
            this(declareVar, listOf(assignExpr), parentScope)

    constructor(declareVar: KslArray<*>, assignExpr: KslMatrixArrayExpression<*,*>, parentScope: KslScopeBuilder) :
            this(declareVar, listOf(assignExpr), parentScope)

    init {
        check(elements.size == declareVar.arraySize || (elements.size == 1 && elements[0].expressionType == declareVar.expressionType)) {
            "Incorrect number of array init elements: arraySize: ${declareVar.arraySize}, init elements: ${elements.size}"
        }
        parentScope.definedStates += declareVar
        elements.forEach { addExpressionDependencies(it) }
        addMutation(declareVar.mutate())
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("declareArray(${declareVar.stateName}) = (${elements.joinToString { it.toPseudoCode() }})")
    }
}