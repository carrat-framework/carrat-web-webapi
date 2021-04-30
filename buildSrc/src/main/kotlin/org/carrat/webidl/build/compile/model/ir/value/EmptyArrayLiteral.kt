package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KEmptyArrayLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

object EmptyArrayLiteral : Literal() {
    override fun toK(target: Target): KValueExpression = KEmptyArrayLiteral
}
