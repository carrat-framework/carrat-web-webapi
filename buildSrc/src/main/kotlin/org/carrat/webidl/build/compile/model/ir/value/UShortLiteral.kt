package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KUShortLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

@OptIn(ExperimentalUnsignedTypes::class)
data class UShortLiteral(
    val value: UShort
) : Literal() {
    override fun toK(target: Target): KValueExpression = KUShortLiteral(value)
}
