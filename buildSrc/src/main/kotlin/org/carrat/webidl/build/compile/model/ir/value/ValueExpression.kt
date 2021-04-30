package org.carrat.webidl.build.compile.model.ir.value

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

abstract class ValueExpression {
    abstract fun toK(target: Target): KValueExpression
}
