package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration

abstract class WDeclaration {
    abstract val sourceFile: String
    abstract val identifier: String
    abstract val members: Collection<WMember>
    open val inherits: String? = null

    abstract fun WidlContext.toIr(): Declaration?
}
