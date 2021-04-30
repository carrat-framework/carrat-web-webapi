package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.*

object WUndefinedType : WPrimitiveType() {
    override fun toIr(): TypeExpression {
        return IrUnit
    }
}

object WBooleanType : WPrimitiveType() {
    override fun toIr(): TypeExpression {
        return IrBoolean
    }
}

object WByteType : WPrimitiveType() {
    override fun toIr(): TypeExpression = IrByte
}

object WOctetType : WPrimitiveType() {
    override fun toIr(): TypeExpression {
        return IrDynamic//TODO
    }
}

object WBigIntType : WPrimitiveType() {
    override fun toIr(): TypeExpression {
        return IrDynamic//TODO
    }
}
