package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration
import org.carrat.webidl.build.compile.model.ir.TypeAlias
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class WTypedef(
    override val sourceFile: String,
    val type: WTypeExpression,
    override val identifier: String
) : WDeclaration() {
    override val members: Collection<WMember>
        get() = emptySet()

    override fun WidlContext.toIr(): Declaration {
        return TypeAlias(identifier, type.toIr())
    }
}
