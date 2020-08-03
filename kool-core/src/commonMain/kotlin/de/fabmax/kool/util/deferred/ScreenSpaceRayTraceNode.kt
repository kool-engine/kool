package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.shadermodel.*

class ScreenSpaceRayTraceNode(val positionTex: TextureNode, graph: ShaderGraph) : ShaderNode("rayTrace_${graph.nextNodeId}", graph) {
    var baseStepFac = ShaderNodeIoVar(ModelVar1fConst(0.02f))
    var stepIncFac = ShaderNodeIoVar(ModelVar1fConst(1.2f))
    var maxIterations = ShaderNodeIoVar(ModelVar1iConst(24))
    var maxRefinements = ShaderNodeIoVar(ModelVar1iConst(5))

    var inProjMat = ShaderNodeIoVar(ModelVarMat4f("_"))
    var inRayOrigin = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inRayDirection = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inRayOffset = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))

    val outRayPos = ShaderNodeIoVar(ModelVar3f("${name}_outPos"), this)
    val outSamplePos = ShaderNodeIoVar(ModelVar3f("${name}_outSamplePos"), this)
    val outSampleWeight = ShaderNodeIoVar(ModelVar1f("${name}_outHitWeight"), this)
    val outCost = ShaderNodeIoVar(ModelVar1f("${name}_outSteps"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(baseStepFac, stepIncFac, maxIterations, maxRefinements)
        dependsOn(inProjMat, inRayOrigin, inRayDirection, inRayOffset)
    }

    override fun generateCode(generator: CodeGenerator) {
        // this silently assumes that inProjMat is a uniform...
        generator.appendFunction("projectViewPos", """
            vec3 projectViewPos(vec3 viewPos) {
                vec4 proj = $inProjMat * vec4(viewPos, 1.0);
                return (proj.xyz / proj.w) * 0.5 + 0.5;
            }
        """)

        generator.appendFunction("linearStep", """
            float linearStep(float edge0, float edge1, float x) {
                float f = clamp(x, edge0, edge1);
                return (f - edge0) / (edge1 - edge0);
            }
        """.trimIndent())

        generator.appendMain("""
            vec3 ${name}_rayPos = ${inRayOrigin.ref3f()};
            vec3 ${name}_rayStepPos = ${name}_rayPos;
            float ${name}_rayStep = -${name}_rayPos.z * ${baseStepFac.ref1f()};
            float ${name}_rayOffset = ${inRayOffset.ref1f()};
            
            float ${name}_sampleDepth = 0.0;
            float ${name}_dDepth = 0.0;
            bool ${name}_refineHit = false;

            int ${name}_iSteps, ${name}_iRef;
            for (${name}_iSteps = 0, ${name}_iRef = 0; ${name}_iSteps < ${maxIterations.ref1i()} && ${name}_iRef < ${maxRefinements.ref1i()}; ${name}_iSteps++) {
                ${name}_rayStepPos += ${inRayDirection.ref3f()} * ${name}_rayStep;
                ${name}_rayPos = ${name}_rayStepPos + ${inRayDirection.ref3f()} * ${name}_rayStep * ${name}_rayOffset * ${stepIncFac.ref1f()};
                
                vec2 ${name}_samplePos = projectViewPos(${name}_rayPos).xy;
                if (${name}_samplePos.x < 0.0 || ${name}_samplePos.x > 1.0
                        || ${name}_samplePos.x < 0.0 || ${name}_samplePos.x > 1.0
                        || ${name}_rayPos.z > 0.0) {
                    break;
                }
                
                ${name}_sampleDepth = ${generator.sampleTexture2d(positionTex.name, "${name}_samplePos")}.z;

                // set a large depth if sampleDepth is positive (clear value)
                ${name}_sampleDepth -= 1e5 * step(0.1, ${name}_sampleDepth);
                
                // diff between ray position depth and scene depth
                //   negative -> ray pos is behind scene depth, i.e. covered by an object
                //   positive -> ray pos is in front of scene
                ${name}_dDepth = ${name}_rayPos.z - ${name}_sampleDepth;
                
                if (!${name}_refineHit) {
                    // search for hit
                    if (${name}_dDepth < 0.0 && ${name}_dDepth > (-${name}_rayStep - 0.2)) {
                        // hit -> roll back position to previous ray position and start refinement
                        ${name}_rayStepPos = ${name}_rayPos - ${inRayDirection.ref3f()} * ${name}_rayStep;
                        ${name}_rayOffset = 0.0;
                        ${name}_rayStep *= 0.5;
                        ${name}_refineHit = true;
                    } else {
                        // no hit, increase step size
                        ${name}_rayStep *= ${stepIncFac.ref1f()};
                    }

                } else {
                    // refine hit position
                    ${name}_rayStep = 0.5 * abs(${name}_rayStep) * sign(${name}_dDepth);
                    ${name}_iRef++;
                }
            }
            
            ${outCost.declare()} = float(${name}_iSteps) / ${maxIterations.ref1f()};
            ${outSampleWeight.declare()} = 0.0;
            ${outRayPos.declare()} = vec3(0.0);
            ${outSamplePos.declare()} = vec3(0.0);
            
            if (${maxIterations.ref1i()} > 0) {
                $outRayPos = ${name}_rayPos + ${inRayDirection.ref3f()} * ${name}_rayStep;
                $outSamplePos = projectViewPos($outRayPos);
                
                $outSampleWeight = 
                              linearStep(0.0, 0.1, ${outSamplePos}.x) * (1.0 - linearStep(0.9, 1.0, ${outSamplePos}.x))
                            * linearStep(0.0, 0.1, ${outSamplePos}.y) * (1.0 - linearStep(0.9, 1.0, ${outSamplePos}.y))
                            * (1.0 - step(0.9999, ${outSamplePos}.z))
                            * (1.0 - linearStep(0.0, -${name}_rayPos.z / 10.0, abs(${name}_dDepth)));
            }
        """)
    }
}