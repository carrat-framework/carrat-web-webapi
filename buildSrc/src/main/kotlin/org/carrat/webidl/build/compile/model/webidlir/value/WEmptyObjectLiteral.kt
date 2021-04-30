package org.carrat.webidl.build.compile.model.webidlir.value

import org.carrat.webidl.build.compile.model.ir.value.EmptyObjectLiteral
import org.carrat.webidl.build.compile.model.ir.value.ValueExpression
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

object WEmptyObjectLiteral : WLiteral() {
    override fun WidlContext.toIr(type: WTypeExpression): ValueExpression = EmptyObjectLiteral
}
