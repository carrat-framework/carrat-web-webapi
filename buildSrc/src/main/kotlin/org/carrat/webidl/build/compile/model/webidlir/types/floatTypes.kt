package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrDouble
import org.carrat.webidl.build.compile.model.ir.type.IrFloat
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

data class WFloatType(
    val unrestricted: Boolean,
    val baseType: FloatBaseType
) : WPrimitiveType() {
    override fun toIr(): TypeExpression = baseType.irType
}

enum class FloatBaseType(
    val irType: TypeExpression
) {
    FLOAT(IrFloat),
    DOUBLE(IrDouble);
}
