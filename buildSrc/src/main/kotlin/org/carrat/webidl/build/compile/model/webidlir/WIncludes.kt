package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration

class WIncludes(
    override val sourceFile: String,
    override val identifier: String,
    override val inherits: String
) : WDeclaration() {
    override val members: Collection<WMember> = emptyList()
    override fun WidlContext.toIr(): Declaration? {
        return null
    }
}
