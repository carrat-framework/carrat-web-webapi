package org.carrat.webidl.build.compile

import com.squareup.kotlinpoet.FileSpec
import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.webidlir.WDeclaration
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import java.nio.file.Path

class Compiler(
    private val commonOutputDirectory: Path,
    private val jsOutputDirectory: Path,
    private val otherOutputDirectories: Collection<Path>,
    private val packageName: String,
    private val ignoreDeclarations: Set<String>
) {
    fun compile(wDeclarations: Collection<WDeclaration>) {
        val wIndex = wDeclarations.associateBy { it.identifier }
        val widlContext = WidlContext(wIndex)
        with(widlContext) {
            val irDeclarations = wDeclarations.mapNotNull { with(it) { toIr() } }
            val `package` = KPackage.parse(packageName)!!
            val kCommonDeclarations = irDeclarations.mapNotNull { it.toK(`package`, Target.COMMON) }
            val kJsDeclarations = irDeclarations.mapNotNull { it.toK(`package`, Target.JS) }
            val kOtherDeclarations = irDeclarations.mapNotNull { it.toK(`package`, Target.OTHER) }
            writeKDeclarations(kCommonDeclarations, commonOutputDirectory)
            writeKDeclarations(kJsDeclarations, jsOutputDirectory)
            otherOutputDirectories.forEach { writeKDeclarations(kOtherDeclarations, it) }
        }
    }

    private fun writeKDeclarations(
        kDeclarations: Collection<KDeclaration>,
        path: Path
    ) {
        kDeclarations.forEach {
            if (it.name.name !in ignoreDeclarations) {
                val builder = FileSpec.builder(it.name.`package`.toString(), it.name.name)
                it.toPoet(builder)
                builder.build().writeTo(path)
            }
        }
    }
}
