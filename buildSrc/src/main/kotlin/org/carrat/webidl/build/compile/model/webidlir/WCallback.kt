package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration
import org.carrat.webidl.build.compile.model.ir.TypeAlias
import org.carrat.webidl.build.compile.model.ir.type.LambdaTypeExpression
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class WCallback(
    override val sourceFile: String,
    override val identifier: String,
    val type: WTypeExpression,
    val arguments: List<WArgument>
) : WDeclaration() {
    override val members: Collection<WMember>
        get() = emptySet()

    override fun WidlContext.toIr(): Declaration {
        return TypeAlias(
            identifier,
            LambdaTypeExpression(
                arguments.map { with(it) { toIr(false) } },
                type.toIr()
            )
        )
    }
}
