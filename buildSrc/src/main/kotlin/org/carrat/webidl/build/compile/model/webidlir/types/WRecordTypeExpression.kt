package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrDynamic
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

data class WRecordTypeExpression(
    val left: WStringType,
    val right: WTypeExpression
) : WTypeExpression() {
    override fun toIr(): TypeExpression {
        return IrDynamic//TODO
    }
}
