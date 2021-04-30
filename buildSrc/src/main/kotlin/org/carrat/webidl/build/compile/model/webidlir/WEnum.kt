package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration
import org.carrat.webidl.build.compile.model.ir.TypeAlias
import org.carrat.webidl.build.compile.model.ir.type.IrString

class WEnum(
    override val sourceFile: String,
    override val identifier: String,
    val values: List<String>
) : WDeclaration() {
    override val members: Collection<WMember>
        get() = emptySet()

    override fun WidlContext.toIr(): Declaration {
        return TypeAlias(
            identifier,
            IrString
        )
    }
}
