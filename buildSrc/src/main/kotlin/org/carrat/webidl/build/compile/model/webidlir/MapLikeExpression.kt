package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class MapLikeExpression(
    val keyType: WTypeExpression,
    val valueType: WTypeExpression,
    val readOnly: Boolean
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        return null//TODO
    }
}
