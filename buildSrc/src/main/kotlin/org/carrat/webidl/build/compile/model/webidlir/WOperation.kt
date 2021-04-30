package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.ir.MethodType
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

class WOperation(
    val special: Special?,
    val type: WTypeExpression,
    val name: String?,
    val arguments: List<WArgument>,
    val static: Boolean = false
) : WMember() {
    enum class Special(val keyword: String) {
        GETTER("getter"),
        SETTER("setter"),
        DELETER("deleter");

        companion object {
            val byKeyword = values().associateBy { it.keyword }
        }
    }

    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        return if (name != null) {
            val type = this.getOperationType(this@WOperation, declaration)
            if (type != null) {
                MemberDeclaration(
                    name,
                    static = false,
                    overrides = overrides(this@WOperation, declaration),
                    MethodType(type)
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun asStatic(): WMember = WOperation(special, type, name, arguments, true)
}

