package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KByteLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class ByteLiteral(
    val value: Byte
) : Literal() {
    override fun toK(target: Target): KValueExpression = KByteLiteral(value)
}
