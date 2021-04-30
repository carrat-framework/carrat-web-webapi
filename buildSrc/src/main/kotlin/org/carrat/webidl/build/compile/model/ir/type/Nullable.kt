package org.carrat.webidl.build.compile.model.ir.type

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KNullableType
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

data class Nullable(
    val element: TypeExpression
) : TypeExpression() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression {
        return KNullableType(element.toK(`package`, target))
    }
}
