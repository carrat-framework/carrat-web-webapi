package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KUndefinedLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

object UndefinedLiteral : Literal() {
    override fun toK(target: Target): KValueExpression = KUndefinedLiteral
}
