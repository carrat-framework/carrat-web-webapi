package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression
import org.carrat.webidl.build.compile.model.webidlir.value.WLiteral

data class WDictionaryEntry(
    val required: Boolean,
    val type: WTypeExpression,
    val identifier: String,
    val defaultValue: WLiteral?
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        return null//TODO
    }
}
