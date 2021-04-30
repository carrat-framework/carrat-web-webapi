package org.carrat.webidl.build.compile.model.ir.type

import org.carrat.webidl.build.compile.model.ir.Argument
import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KLambdaTypeExpression

class LambdaTypeExpression(
    val arguments: List<Argument>,
    val returnType: TypeExpression
) : TypeExpression() {
    override fun toK(`package`: KPackage, target: Target): KLambdaTypeExpression {
        return KLambdaTypeExpression(
            arguments.map { it.toK(`package`, target) },
            returnType.toK(`package`, target)
        )
    }
}
