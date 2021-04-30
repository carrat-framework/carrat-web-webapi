package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.value.WStringLiteral

data class EnumEntry(
    val name: WStringLiteral
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        return null//TODO
    }
}
