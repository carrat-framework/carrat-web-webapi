package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

class WAttribute(
    val readOnly: Boolean,
    val inherited: Boolean,
    val type: WTypeExpression,
    val name: String,
    val static: Boolean = false
) : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        val memberType = getPropertyType(this@WAttribute, declaration)
        return if (memberType != null) {
            MemberDeclaration(name, false, inherited || overrides(this@WAttribute, declaration), memberType)
        } else {
            null
        }
    }

    override fun asStatic(): WMember = WAttribute(readOnly, inherited, type, name, true)
}
