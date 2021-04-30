package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KShortLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class ShortLiteral(
    val value: Short
) : Literal() {
    override fun toK(target: Target): KValueExpression = KShortLiteral(value)
}
