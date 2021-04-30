package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrDynamic
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

class WUnionTypeExpression(
    val types: List<WTypeExpression>
) : WTypeExpression() {
    override fun toIr(): TypeExpression {
        return IrDynamic
    }
}
