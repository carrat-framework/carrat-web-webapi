package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.*

data class WIntegerType(
    val unsigned: Boolean,
    val baseType: IntegerBaseType
) : WPrimitiveType() {
    override fun toIr(): TypeExpression {
        return when (unsigned) {
            true -> baseType.unsigned
            false -> baseType.signed
        }
    }
}

enum class IntegerBaseType(
    val unsigned: TypeExpression,
    val signed: TypeExpression
) {
    SHORT(IrUShort, IrShort),
    LONG(IrUInt, IrInt),
    LONG_LONG(IrULong, IrLong)
}
