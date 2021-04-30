package org.carrat.webidl.build.compile.model.webidlir.value

import org.carrat.webidl.build.compile.model.ir.type.*
import org.carrat.webidl.build.compile.model.ir.value.*
import org.carrat.webidl.build.compile.model.webidlir.WidlContext
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

data class WIntegerLiteral(
    val value: Long
) : WLiteral() {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun WidlContext.toIr(type: WTypeExpression): ValueExpression {
        val irType = type.resolve().toIr()
        val nonNullIrType = if (irType is Nullable) irType.element else irType
        return when (nonNullIrType) {
            IrByte -> ByteLiteral(value.toByte())
            IrShort -> ShortLiteral(value.toShort())
            IrUShort -> UShortLiteral(value.toUShort())
            IrInt -> IntLiteral(value.toInt())
            IrUInt -> UIntLiteral(value.toUInt())
            IrLong -> LongLiteral(value)
            IrULong -> ULongLiteral(value.toULong())
            IrFloat -> FloatLiteral(value.toFloat())
            IrDouble -> DoubleLiteral(value.toDouble())
            else -> throw IllegalArgumentException("Type $type is incompatible with $this")
        }
    }
}
