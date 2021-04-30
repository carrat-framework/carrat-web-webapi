package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrString
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

sealed class WStringType : WType() {
    object ByteString : WStringType()
    object DOMString : WStringType()
    object USVString : WStringType()

    override fun toIr(): TypeExpression {
        return IrString
    }
}
