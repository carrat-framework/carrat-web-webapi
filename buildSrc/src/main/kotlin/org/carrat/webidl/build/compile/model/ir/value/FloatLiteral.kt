package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KFloatLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

class FloatLiteral(
    val value: Float
) : Literal() {
    override fun toK(target: Target): KValueExpression = KFloatLiteral(value)
}
