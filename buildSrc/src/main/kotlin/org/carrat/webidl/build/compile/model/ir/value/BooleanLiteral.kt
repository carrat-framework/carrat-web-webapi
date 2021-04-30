package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KBooleanLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class BooleanLiteral(
    val value: Boolean
) : Literal() {
    override fun toK(target: Target): KValueExpression = KBooleanLiteral(value)
}
