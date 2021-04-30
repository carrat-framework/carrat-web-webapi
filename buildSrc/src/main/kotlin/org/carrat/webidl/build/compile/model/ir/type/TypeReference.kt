package org.carrat.webidl.build.compile.model.ir.type

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeReference

data class TypeReference(
    val identifier: String
) : TypeExpression() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression {
        return KTypeReference(KName(`package`, identifier))
    }
}
