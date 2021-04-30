package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.PromiseTypeExpression
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

data class WPromiseTypeExpression(
    val memberType: WTypeExpression
) : WTypeExpression() {
    override fun toIr(): TypeExpression {
        return PromiseTypeExpression(memberType.toIr())
    }
}
