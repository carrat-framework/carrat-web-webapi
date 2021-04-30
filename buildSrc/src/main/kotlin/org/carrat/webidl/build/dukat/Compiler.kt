package org.carrat.webidl.build.dukat

import com.google.common.collect.Multimaps
import com.squareup.kotlinpoet.FileSpec
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.carrat.webidl.build.compile.model.kotlinir.KDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.webidlir.WDeclaration
import org.carrat.webidl.build.compile.parser.IrBuilderContext
import org.carrat.webidl.build.compile.parser.IrParser
import org.carrat.webidl.build.grammar.WebIdlLexer
import org.carrat.webidl.build.grammar.WebIdlParser
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.commonLowerings.AddExplicitGettersAndSetters
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlLowerings.*
import org.jetbrains.dukat.model.commonLowerings.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class Compiler(
    private val commonOutputDirectory: Path,
    private val jsOutputDirectory: Path,
    private val otherOutputDirectories: Collection<Path>,
    private val packageName: String,
    private val ignoreDeclarations: Set<String>
) {
    fun compile(input: Path) {
//        val referencesResolver = MyReferencesResolver()
//        val idlTranslator = IdlInputTranslator(referencesResolver)
//        val sourceSet = idlTranslator.translate(Files.list(input).findFirst().get().toString())
        val declarations = parse(input)
        val wIndex = declarations.associateBy { it.identifier }
        val sourceSet = translateSet(WidlToDukatTranslator.translate(declarations)).lower(RemoveConflictingOverloads())
        val `package` = KPackage.parse(packageName)!!
        val kCommonDeclarations = CommonTranslator(`package`).translate(wIndex, sourceSet)
        val kJsDeclarations = JsTranslator(`package`).translate(wIndex, sourceSet)
        val kOtherDeclarations = OtherTranslator(`package`).translate(wIndex, sourceSet)
        writeKDeclarations(kCommonDeclarations, commonOutputDirectory)
        writeKDeclarations(kJsDeclarations, jsOutputDirectory)
        otherOutputDirectories.forEach { writeKDeclarations(kOtherDeclarations, it) }
    }

    private fun parse(basePath: Path): Set<WDeclaration> {
        val irBuilder = IrBuilderContext()
        val irParser = IrParser(irBuilder)
        Files.walk(basePath).forEach {
            if (Files.isRegularFile(it)) {
                System.err.println("Compiling $it")
                val lexer = WebIdlLexer(
                    ANTLRFileStream(
                        it.toString(),
                        StandardCharsets.UTF_8.name()
                    )
                )
                val parser = WebIdlParser(CommonTokenStream(lexer))
                irParser.processDocument(parser.document())
            }
        }
        return irBuilder.build()
    }

    private fun writeKDeclarations(
        declarations: Collection<KDeclaration>,
        path: Path
    ) {
        Multimaps.index(declarations) { it!!.name }.asMap().forEach { (name, declarations) ->
            val builder = FileSpec.builder(name.`package`.toString(), name.name)
            declarations.forEach { it.toPoet(builder) }
            builder.build().writeTo(path)
        }
    }

    fun translateSet(sourceSet: IDLSourceSetDeclaration): SourceSetModel {
        return sourceSet
            .resolvePartials()
//            .addConstructors()
            .resolveTypedefs()
            .specifyEventHandlerTypes()
            .specifyDefaultValues()
            .resolveImplementsStatements()
            .resolveMixins()
            .addItemArrayLike()
            .resolveTypes(setOf("undefined", "WindowProxy"))
            .markAbstractOrOpen()
            .addMissingMembers()
            .addOverloadsForCallbacks()
            .convertToModel()
            .lower(
                ModelContextAwareLowering()
                    .lower { context, inheritanceContext ->
                        LowerOverrides(context, inheritanceContext)
                    },
                EscapeIdentificators(),
                AddExplicitGettersAndSetters()
            )
            .addKDocs()
            .relocateDeclarations()
            .resolveTopLevelVisibility(alwaysPublic())
            .addImportsForUsedPackages()
            .omitStdLib()
    }
}

private fun alwaysPublic(): VisibilityModifierResolver = object : VisibilityModifierResolver {
    override fun resolve(): VisibilityModifierModel = VisibilityModifierModel.PUBLIC
}
