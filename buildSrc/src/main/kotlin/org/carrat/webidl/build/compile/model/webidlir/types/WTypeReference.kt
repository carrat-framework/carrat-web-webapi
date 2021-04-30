package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.TypeExpression
import org.carrat.webidl.build.compile.model.ir.type.TypeReference

data class WTypeReference(
    val identifier: String
) : WTypeExpression() {
    override fun toIr(): TypeExpression {
        return TypeReference(identifier)
    }
}
