package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KUIntLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

@OptIn(ExperimentalUnsignedTypes::class)
data class UIntLiteral(
    val value: UInt
) : Literal() {
    override fun toK(target: Target): KValueExpression = KUIntLiteral(value)
}
