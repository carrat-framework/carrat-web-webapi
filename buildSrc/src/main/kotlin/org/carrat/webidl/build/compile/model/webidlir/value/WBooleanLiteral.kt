package org.carrat.webidl.build.compile.model.webidlir.value

import org.carrat.webidl.build.compile.model.ir.value.BooleanLiteral
import org.carrat.webidl.build.compile.model.ir.value.ValueExpression
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class WBooleanLiteral(
    val value: Boolean
) : WLiteral() {
    override fun WidlContext.toIr(type: WTypeExpression): ValueExpression = BooleanLiteral(value)
}
