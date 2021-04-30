package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KNullLiteral
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

object NullLiteral : Literal() {
    override fun toK(target: Target): KValueExpression = KNullLiteral
}
