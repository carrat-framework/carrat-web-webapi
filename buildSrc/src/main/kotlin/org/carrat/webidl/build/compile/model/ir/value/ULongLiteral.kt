package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KULongLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

@OptIn(ExperimentalUnsignedTypes::class)
data class ULongLiteral(
    val value: ULong
) : Literal() {
    override fun toK(target: Target): KValueExpression = KULongLiteral(value)
}
