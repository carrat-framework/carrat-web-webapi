package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KUByteLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

@OptIn(ExperimentalUnsignedTypes::class)
data class UByteLiteral(
    val value: UByte
) : Literal() {
    override fun toK(target: Target): KValueExpression = KUByteLiteral(value)
}
