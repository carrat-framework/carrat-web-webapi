package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Class
import org.carrat.webidl.build.compile.model.ir.Declaration

class WNamespace(
    override val sourceFile: String,
    override val identifier: String,
    override val members: List<WMember>
) : WDeclaration() {
    override fun WidlContext.toIr(): Declaration {
        return Class(
            identifier,
            null,
            emptyList(),
            members.mapNotNull { with(it) { toIr(identifier) } }
        )
    }
}
