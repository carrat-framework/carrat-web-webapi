package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KLongLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class LongLiteral(
    val value: Long
) : Literal() {
    override fun toK(target: Target): KValueExpression = KLongLiteral(value)
}
