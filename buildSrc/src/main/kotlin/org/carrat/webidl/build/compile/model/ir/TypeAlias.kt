package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.ir.type.TypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.KDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.KTypeAlias

class TypeAlias(
    override val identifier: String,
    val type: TypeExpression
) : Declaration() {
    override fun toK(`package`: KPackage, target: Target): KDeclaration? {
        return if (target == Target.COMMON) {
            KTypeAlias(KName(`package`, identifier), type.toK(`package`, target))
        } else {
            null
        }
    }
}
