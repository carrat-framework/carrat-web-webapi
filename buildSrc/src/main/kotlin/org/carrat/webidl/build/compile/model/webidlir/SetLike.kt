package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class SetLike(
    val elementType: WTypeExpression,
    val readOnly: Boolean
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? = null
}
