package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.ir.type.TypeExpression
import org.carrat.webidl.build.compile.model.ir.value.ValueExpression
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.KParameter

data class Argument(
    val identifier: String?,
    val type: TypeExpression,
    val vararg: Boolean,
    val defaultValue: ValueExpression?
) {
    fun toK(`package`: KPackage, target: Target): KParameter {
        return KParameter(
            identifier,
            type.toK(`package`, target),
            vararg,
            defaultValue?.toK(target)
        )
    }
}
