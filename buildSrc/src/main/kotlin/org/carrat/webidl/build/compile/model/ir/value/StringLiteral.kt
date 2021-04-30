package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KStringLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class StringLiteral(
    val value: String
) : Literal() {
    override fun toK(target: Target): KValueExpression = KStringLiteral(value)
}
