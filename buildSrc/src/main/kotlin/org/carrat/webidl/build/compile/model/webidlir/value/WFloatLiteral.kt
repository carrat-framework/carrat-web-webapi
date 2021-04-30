package org.carrat.webidl.build.compile.model.webidlir.value

import org.carrat.webidl.build.compile.model.ir.type.IrDouble
import org.carrat.webidl.build.compile.model.ir.type.IrFloat
import org.carrat.webidl.build.compile.model.ir.type.Nullable
import org.carrat.webidl.build.compile.model.ir.value.DoubleLiteral
import org.carrat.webidl.build.compile.model.ir.value.FloatLiteral
import org.carrat.webidl.build.compile.model.ir.value.ValueExpression
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class WFloatLiteral(
    val value: Double
) : WLiteral() {
    override fun WidlContext.toIr(type: WTypeExpression): ValueExpression {
        val irType = type.resolve().toIr()
        val nonNullIrType = if (irType is Nullable) irType.element else irType
        return when (nonNullIrType) {
            IrDouble -> DoubleLiteral(value)
            IrFloat -> FloatLiteral(value.toFloat())
            else -> throw IllegalArgumentException("Type $type is incompatible with $this")
        }
    }
}
