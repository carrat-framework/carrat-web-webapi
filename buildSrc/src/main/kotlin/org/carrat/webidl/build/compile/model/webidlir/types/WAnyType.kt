package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrAny
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

object WAnyType : WType() {
    override fun toIr(): TypeExpression = IrAny
}
