package org.carrat.webidl.build.compile.gradle

import org.carrat.webidl.build.dukat.Compiler
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Paths

abstract class CompileWebIdlTask : DefaultTask() {
    @InputDirectory
    abstract fun getInput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @Input
    abstract fun getPackage(): Property<String>

    @Input
    abstract fun getIgnoreDeclarations(): ListProperty<String>

    @OutputDirectory
    abstract fun getCommonOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @OutputDirectory
    abstract fun getJsOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @OutputDirectories
    abstract fun getOtherOutputs(): ListProperty<String>

    @TaskAction
    fun compile() {
        val basePath = Paths.get(getInput().get())
//        val irBuilder = IrBuilderContext()
//        val irParser = IrParser(irBuilder)
//        Files.walk(basePath).forEach {
//            if(Files.isRegularFile(it)) {
//                System.err.println("Compiling $it")
//                val lexer = WebIdlLexer(
//                    ANTLRFileStream(
//                        it.toString(),
//                        StandardCharsets.UTF_8.name()
//                    )
//                )
//                val parser = WebIdlParser(CommonTokenStream(lexer))
//                irParser.processDocument(parser.document())
//            }
//        }
        val compiler = Compiler(
            Paths.get(getCommonOutput().get()),
            Paths.get(getJsOutput().get()),
            getOtherOutputs().get().map { Paths.get(it) },
            getPackage().get(),
            getIgnoreDeclarations().get().toSet()
        )
        compiler.compile(basePath)
    }
}
