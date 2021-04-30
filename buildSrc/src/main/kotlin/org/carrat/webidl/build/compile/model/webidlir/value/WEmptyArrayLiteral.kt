package org.carrat.webidl.build.compile.model.webidlir.value

import org.carrat.webidl.build.compile.model.ir.value.EmptyArrayLiteral
import org.carrat.webidl.build.compile.model.ir.value.ValueExpression
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

object WEmptyArrayLiteral : WLiteral() {
    override fun WidlContext.toIr(type: WTypeExpression): ValueExpression = EmptyArrayLiteral
}
