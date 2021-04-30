package org.carrat.webidl.build.compile.model.webidlir.value

import org.carrat.webidl.build.compile.model.ir.value.StringLiteral
import org.carrat.webidl.build.compile.model.ir.value.ValueExpression
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class WStringLiteral(
    val value: String
) : WLiteral() {
    override fun WidlContext.toIr(type: WTypeExpression): ValueExpression = StringLiteral(value)
}
