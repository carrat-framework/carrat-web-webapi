package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrDynamic
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

object WObjectType : WType() {
    override fun toIr(): TypeExpression = IrDynamic
}
