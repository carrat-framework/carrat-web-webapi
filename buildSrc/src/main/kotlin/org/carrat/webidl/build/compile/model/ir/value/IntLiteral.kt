package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KIntLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class IntLiteral(
    val value: Int
) : Literal() {
    override fun toK(target: Target): KValueExpression = KIntLiteral(value)
}
