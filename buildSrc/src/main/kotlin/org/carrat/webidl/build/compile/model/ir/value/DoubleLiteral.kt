package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KDoubleLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

class DoubleLiteral(
    val value: Double
) : Literal() {
    override fun toK(target: Target): KValueExpression = KDoubleLiteral(value)
}
