package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

abstract class WTypeExpression {
    abstract fun toIr(): TypeExpression
}
