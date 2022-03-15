package de.fabmax.kool.modules.ksl.lang

class KslDeclareArray(val declareVar: KslArray<*>, val elements: List<KslExpression<*>>, parentScope: KslScopeBuilder)
    : KslStatement("declareArray", parentScope) {

    constructor(declareVar: KslArray<*>, element: KslExpression<*>, parentScope: KslScopeBuilder) :
            this(declareVar, List(declareVar.arraySize) { element }, parentScope)

    init {
        check(elements.size == declareVar.arraySize) {
            "Incorrect number of array init elements: arraySize: ${declareVar.arraySize}, init elements: ${elements.size}"
        }
        parentScope.definedStates += declareVar
        elements.forEach { addExpressionDependencies(it) }
        addMutation(declareVar.mutate())
    }

    override fun toPseudoCode(): String {
        return "declareArray(${declareVar.stateName}) = (${elements.joinToString { it.toPseudoCode() }}) // ${dependenciesAndMutationsToString()}"
    }
}