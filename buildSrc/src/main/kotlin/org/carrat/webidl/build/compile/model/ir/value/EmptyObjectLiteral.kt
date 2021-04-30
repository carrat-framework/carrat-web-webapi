package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KEmptyObjectLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

object EmptyObjectLiteral : Literal() {
    override fun toK(target: Target): KValueExpression = KEmptyObjectLiteral
}
