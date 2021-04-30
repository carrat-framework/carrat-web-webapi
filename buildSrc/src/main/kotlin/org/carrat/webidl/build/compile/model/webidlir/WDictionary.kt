package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration
import org.carrat.webidl.build.compile.model.ir.Interface

class WDictionary(
    override val sourceFile: String,
    override val identifier: String,
    override val inherits: String?,
    override val members: List<WMember>
) : WDeclaration() {
    override fun WidlContext.toIr(): Declaration {
        return Interface(
            identifier,
            listOfNotNull(inherits),
            members.mapNotNull { with(it) { toIr(identifier) } },
            false
        )
    }
}
