package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.ConstructorType
import org.carrat.webidl.build.compile.model.ir.MemberDeclaration

class WConstructor(
    val arguments: List<WArgument>
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration {
        return MemberDeclaration(
            null,
            false,
            false,
            ConstructorType(
                arguments.map { with(it) { toIr(true) } }
            )
        )
    }
}
