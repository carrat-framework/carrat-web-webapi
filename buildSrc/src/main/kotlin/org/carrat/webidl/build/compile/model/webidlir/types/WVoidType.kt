package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrUnit
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

object WVoidType : WType() {
    override fun toIr(): TypeExpression = IrUnit
}
