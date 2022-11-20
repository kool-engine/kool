import de.fabmax.webidl.generator.js.JsInterfaceGenerator
import de.fabmax.webidl.parser.WebIdlParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileNotFoundException

open class PhysxJsGenerator : DefaultTask() {
    @Input
    var idlSource = ""
    @Input
    var generatorOutput = "./generated"

    @TaskAction
    fun generate() {
        val idlFile = File(idlSource)
        if (!idlFile.exists()) {
            throw FileNotFoundException("PhysX WebIDL definition not found!")
        }

        TODO("Update idl generator stuff")
//        val model = WebIdlParser().parse(idlFile.path)
//        JsInterfaceGenerator().apply {
//            outputDirectory = generatorOutput
//            packagePrefix = "physx"
//            moduleName = "physx-js-webidl"
//
//            nullableAttributes += "PxBatchQueryDesc.preFilterShader"
//            nullableAttributes += "PxBatchQueryDesc.postFilterShader"
//            nullableParameters += "PxArticulationBase.createLink" to "parent"
//            nullableReturnValues += "PxArticulationLink.getInboundJoint"
//        }.generate(model)
    }
}
