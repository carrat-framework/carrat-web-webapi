package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration

abstract class WMember {
    abstract fun WidlContext.toIr(declaration: String): MemberDeclaration?
    open fun asStatic(): WMember = this
}
