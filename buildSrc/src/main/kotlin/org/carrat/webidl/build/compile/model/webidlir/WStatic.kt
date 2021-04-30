package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration

data class WStatic(
    val actual: WMember
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        val ir = with(actual) { toIr(declaration) }
        return ir?.let { MemberDeclaration(it.identifier, static = true, overrides = false, it.memberType) }
    }
}
