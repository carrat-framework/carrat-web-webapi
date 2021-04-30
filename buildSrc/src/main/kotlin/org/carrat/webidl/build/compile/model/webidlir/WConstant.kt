package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.ir.PropertyType
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression
import org.carrat.webidl.build.compile.model.webidlir.value.WLiteral

data class WConstant(
    val type: WTypeExpression,
    val name: String,
    val value: WLiteral
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration {
        return MemberDeclaration(
            name,
            true,
            false,
            PropertyType(
                type.toIr(),
                false
            )
        )
    }
}
