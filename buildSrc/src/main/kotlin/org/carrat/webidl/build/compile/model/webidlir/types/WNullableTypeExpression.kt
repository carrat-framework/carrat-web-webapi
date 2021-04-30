package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrDynamic
import org.carrat.webidl.build.compile.model.ir.type.Nullable
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

data class WNullableTypeExpression(val baseType: WTypeExpression) : WTypeExpression() {
    override fun toIr(): TypeExpression {
        val element = baseType.toIr()
        return if (element != IrDynamic) {
            Nullable(element)
        } else {
            IrDynamic
        }
    }
}
