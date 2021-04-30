package org.carrat.webidl.build.dukat

import org.jetbrains.dukat.idlReferenceResolver.IdlReferencesResolver
import org.jetbrains.dukat.translatorString.IDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import java.io.File

class MyReferencesResolver : IdlReferencesResolver {
    override fun resolveReferences(fileName: String): List<String> {
        val directory = File(fileName).parentFile
        return directory.listFiles()?.map { it.canonicalFile }?.filter {
            it.isFile &&
                    (it.name.endsWith(WEBIDL_DECLARATION_EXTENSION)
                            || it.name.endsWith(IDL_DECLARATION_EXTENSION)
                            || it.name.endsWith(".widl")) &&
                    it.absolutePath != File(fileName).canonicalFile.absolutePath
        }?.map { it.absolutePath }.orEmpty().sorted()
    }
}
